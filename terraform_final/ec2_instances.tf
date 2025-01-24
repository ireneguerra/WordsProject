############################################
# PROVIDER (puedes ajustarlo a tu región)
############################################
provider "aws" {
  region = "us-east-1"
}

############################################
# VPC PRINCIPAL
############################################
resource "aws_vpc" "main_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "Main-VPC"
  }
}

############################################
# INTERNET GATEWAY (para subred pública)
############################################
resource "aws_internet_gateway" "main_igw" {
  vpc_id = aws_vpc.main_vpc.id

  tags = {
    Name = "Main-Internet-Gateway"
  }
}

############################################
# SUBRED PÚBLICA (10.0.1.0/24)
############################################
resource "aws_subnet" "main_public_subnet" {
  vpc_id                  = aws_vpc.main_vpc.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true

  tags = {
    Name = "Main-Public-Subnet"
  }
}

############################################
# ROUTE TABLE PÚBLICA (asociada al IGW)
############################################
resource "aws_route_table" "main_public_route_table" {
  vpc_id = aws_vpc.main_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main_igw.id
  }

  tags = {
    Name = "Main-Public-Route-Table"
  }
}

############################################
# ASOCIAR SUBRED PÚBLICA A ROUTE TABLE PÚBLICA
############################################
resource "aws_route_table_association" "public_route_table_association" {
  subnet_id      = aws_subnet.main_public_subnet.id
  route_table_id = aws_route_table.main_public_route_table.id
}

############################################
# SUBRED PRIVADA (10.0.2.0/24)
############################################
resource "aws_subnet" "main_private_subnet" {
  vpc_id                  = aws_vpc.main_vpc.id
  cidr_block              = "10.0.2.0/24"
  map_public_ip_on_launch = false

  tags = {
    Name = "Main-Private-Subnet"
  }
}

############################################
# EIP PARA NAT GATEWAY
############################################
resource "aws_eip" "nat_eip" {
  domain = "vpc"
}

############################################
# NAT GATEWAY (en subred pública)
############################################
resource "aws_nat_gateway" "nat_gw" {
  allocation_id = aws_eip.nat_eip.id
  subnet_id     = aws_subnet.main_public_subnet.id

  tags = {
    Name = "Main-NAT-Gateway"
  }
}

############################################
# ROUTE TABLE PRIVADA (usa el NAT Gateway)
############################################
resource "aws_route_table" "private_route_table" {
  vpc_id = aws_vpc.main_vpc.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat_gw.id
  }

  tags = {
    Name = "Main-Private-Route-Table"
  }
}

############################################
# ASOCIAR SUBRED PRIVADA A ROUTE TABLE PRIVADA
############################################
resource "aws_route_table_association" "private_rta" {
  subnet_id      = aws_subnet.main_private_subnet.id
  route_table_id = aws_route_table.private_route_table.id
}

############################################
# SG MongoDB (privado)
############################################
resource "aws_security_group" "mongodb_sg" {
  name_prefix = "mongodb-sg"
  vpc_id      = aws_vpc.main_vpc.id

  ingress {
    description = "Allow traffic from the public subnet to Mongo (27017)"
    from_port   = 27017
    to_port     = 27017
    protocol    = "tcp"
    # Solo la subred pública (10.0.1.0/24), o la IP del orquestador
    cidr_blocks = ["10.0.1.0/24"]
  }

  ingress {
    description = "Allow SSH from the public subnet (opcional)"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["10.0.1.0/24"]
  }

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

############################################
# SG Orquestador+API (público)
############################################
resource "aws_security_group" "orchestrator_sg" {
  name        = "orchestrator-sg"
  description = "Orchestration and API instance security group"
  vpc_id      = aws_vpc.main_vpc.id

  ingress {
    description = "Allow access to HTTP"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Allow access to SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "orchestrator-sg"
  }
}

############################################
# CLAVE SSH
############################################
resource "tls_private_key" "ssh_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "aws_key_pair" "my_key" {
  key_name   = "ec2-key"
  public_key = tls_private_key.ssh_key.public_key_openssh
}

############################################
# INSTANCIA MONGODB (SUBRED PRIVADA)
############################################
resource "aws_instance" "mongodb_instance" {
  ami           = "ami-0c02fb55956c7d316"  # Amazon Linux 2
  instance_type = "t2.micro"

  key_name               = aws_key_pair.my_key.key_name
  subnet_id              = aws_subnet.main_private_subnet.id
  vpc_security_group_ids = [aws_security_group.mongodb_sg.id]

  user_data = <<-EOF
    #!/bin/bash
    sudo yum update -y
    echo "[mongodb-org-6.0]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/amazon/2/mongodb-org/6.0/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-6.0.asc" | sudo tee /etc/yum.repos.d/mongodb-org-6.0.repo
    sudo yum install -y mongodb-org
    sudo sed -i 's/bindIp: 127.0.0.1/bindIp: 0.0.0.0/' /etc/mongod.conf
    sudo systemctl start mongod
    sudo systemctl enable mongod
    sudo yum install -y net-tools
  EOF

  tags = {
    Name = "MongoDB-Private-Instance"
  }
}

############################################
# INSTANCIA ORQUESTADOR+API (SUBRED PÚBLICA)
############################################
resource "aws_instance" "orchestrator_and_api_instance" {
  ami                    = "ami-0c02fb55956c7d316"
  instance_type          = "t2.medium"
  key_name               = aws_key_pair.my_key.key_name
  subnet_id              = aws_subnet.main_public_subnet.id
  vpc_security_group_ids = [aws_security_group.orchestrator_sg.id]

  # Coloca tu IAM Instance Profile para acceso S3
  iam_instance_profile = "myS3Role"

  user_data = <<-EOF
    #!/bin/bash
    sudo yum update -y
    sudo yum install -y java-11-amazon-corretto aws-cli cronie

    # Crear el script de orquestación
    cat <<'EOT' > /home/ec2-user/orchestrate_and_api.sh
    #!/bin/bash
    BUCKET_JARS="jars-app-bucket"

    # Función para verificar si un puerto está en uso y detener el proceso correspondiente
    stop_port_if_in_use() {
      local port=$1
      local pid=$(sudo lsof -t -i:$port)
      if [ ! -z "$pid" ]; then
        echo "El puerto $port está en uso por el proceso con PID $pid. Deteniendo..."
        sudo kill -9 $pid
        echo "Proceso detenido en el puerto $port."
      else
        echo "El puerto $port está libre."
      fi
    }

    # Verificar y detener servicios en puertos clave (por ejemplo, 8080 para la API)
    stop_port_if_in_use 8080

    # Función para verificar si un archivo existe en S3
    check_file_in_s3() {
      local file=$1
      aws s3 ls "s3://$BUCKET_JARS/$file" > /dev/null 2>&1
      if [ $? -ne 0 ]; then
        echo "Archivo $file no encontrado en el bucket $BUCKET_JARS. Esperando..."
        return 1
      else
        echo "Archivo $file encontrado en S3."
        return 0
      fi
    }

    # Función para descargar y ejecutar un archivo JAR
    process_file() {
      local file=$1
      while ! check_file_in_s3 "$file"; do
        sleep 10
      done
      echo "Descargando $file..."
      aws s3 cp "s3://$BUCKET_JARS/$file" . || exit 1
      echo "Ejecutando $file..."
      java -jar "$file" || exit 1
    }

    # Descargar y ejecutar los JARs necesarios
    process_file "BookCrawler.jar"
    process_file "WordCounterDatamart.jar"
    process_file "WordsGraph.jar"

    # Manejo del APIGraph.jar
    process_file "APIGraph.jar"

    # Verificar nuevamente el puerto 8080 y asegurarse de que no haya conflictos antes de iniciar la API
    stop_port_if_in_use 8080

    echo "Iniciando API..."
    nohup java -jar APIGraph.jar > /home/ec2-user/apigraph.log 2>&1 &
    EOT

    chmod +x /home/ec2-user/orchestrate_and_api.sh

    # Ejecutar el script inmediatamente
    bash /home/ec2-user/orchestrate_and_api.sh >> /home/ec2-user/orchestrator_and_api.log 2>&1

    echo "0 2 * * * ec2-user bash /home/ec2-user/orchestrate_and_api.sh >> /home/ec2-user/orchestrator_and_api.log 2>&1" | sudo tee /etc/cron.d/orchestrate_and_api
    sudo systemctl start crond
    sudo systemctl enable crond
  EOF

  tags = {
    Name = "Orchestrator-And-API-EC2"
  }
}


############################################
# CREAR BUCKET S3 + SUBIR ip_mongo.txt
############################################

# 1) Agregamos el local "always_trigger" para forzar cambio en cada apply
locals {
  bucket_name_3  = "ip-mongo-bucket"
  always_trigger = timestamp()  # <-- Esto se actualizará en cada plan/apply
}

# Conservamos la creación del bucket igual
resource "null_resource" "create_bucket_and_upload" {
  provisioner "local-exec" {
    interpreter = ["powershell", "-Command"]
    command = <<EOT
      $BucketName = "${local.bucket_name_3}"
      if (aws s3api head-bucket --bucket $BucketName 2>$null) {
        Write-Output "El bucket $BucketName ya existe. Eliminando contenido..."
        aws s3 rm "s3://$BucketName" --recursive
        Write-Output "Eliminando el bucket $BucketName..."
        aws s3api delete-bucket --bucket $BucketName --region us-east-1
      }
      Write-Output "Creando el bucket $BucketName..."
      aws s3api create-bucket --bucket $BucketName --region us-east-1
    EOT
  }
}

# 2) local_file que contiene la IP
resource "local_file" "ip_file" {
  filename = "ip_mongo.txt"
  content  = aws_instance.mongodb_instance.private_ip
}

# 3) Usar triggers en el null_resource para que se ejecute SIEMPRE
resource "null_resource" "upload_public_ip" {
  # triggers se ve forzado a cambiar en cada apply gracias a la funcion timestamp()
  triggers = {
    forced = local.always_trigger
  }

  provisioner "local-exec" {
    interpreter = ["powershell", "-Command"]
    command = <<EOT
      $BucketName = "${local.bucket_name_3}"
      $FilePath   = "${local_file.ip_file.filename}"
      aws s3 cp $FilePath "s3://$BucketName/ip_mongo.txt"
      Write-Output "Archivo ip_mongo.txt subido al bucket $BucketName."
    EOT
  }
}