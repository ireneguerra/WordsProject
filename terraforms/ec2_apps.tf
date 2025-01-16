# VPC
resource "aws_vpc" "main_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "Main-VPC"
  }
}

# Internet Gateway
resource "aws_internet_gateway" "main_igw" {
  vpc_id = aws_vpc.main_vpc.id

  tags = {
    Name = "Main-Internet-Gateway"
  }
}

# Route Table
resource "aws_route_table" "main_route_table" {
  vpc_id = aws_vpc.main_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main_igw.id
  }

  tags = {
    Name = "Main-Route-Table"
  }
}

# Subnet pública
resource "aws_subnet" "main_public_subnet" {
  vpc_id                  = aws_vpc.main_vpc.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true

  tags = {
    Name = "Main-Public-Subnet"
  }
}

# Asociar Subnet a la Route Table
resource "aws_route_table_association" "main_route_table_association" {
  subnet_id      = aws_subnet.main_public_subnet.id
  route_table_id = aws_route_table.main_route_table.id
}

# Security Group para la instancia (orquestador y API)
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
}

# Instancia EC2: Orquestador y API en la misma máquina
resource "aws_instance" "orchestrator_and_api_instance" {
  ami           = "ami-0c02fb55956c7d316" # Amazon Linux 2
  instance_type = "t2.medium"
  key_name      = "ec2-key"
  subnet_id     = aws_subnet.main_public_subnet.id
  vpc_security_group_ids = [aws_security_group.orchestrator_sg.id]

  iam_instance_profile = "myS3Role"

  user_data = <<-EOF
    #!/bin/bash
    sudo yum update -y
    sudo yum install -y java-11-amazon-corretto aws-cli cronie

    # Script de orquestación
    cat <<'EOT' > /home/ec2-user/orchestrate_and_api.sh
    #!/bin/bash
    BUCKET_JARS="bucket-de-tus-jars"

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

    # Procesar archivos JAR uno por uno
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

    # Ejecutar cada archivo JAR
    process_file "BookCrawler.jar"
    process_file "WordCounterDatamart.jar"
    process_file "WordsGraph.jar"

    # Manejo del APIGraph.jar de la misma forma
    echo "Preparando API..."
    process_file "APIGraph-1.0-SNAPSHOT.jar"

    # Iniciar la API después de que el archivo esté disponible
    echo "Iniciando API..."
    nohup java -jar APIGraph-1.0-SNAPSHOT.jar > /home/ec2-user/apigraph.log 2>&1 &

    echo "Pipeline y API completados."
    EOT

    chmod +x /home/ec2-user/orchestrate_and_api.sh

    # Ejecutar el script inmediatamente al iniciar la instancia
    bash /home/ec2-user/orchestrate_and_api.sh >> /home/ec2-user/orchestrator_and_api.log 2>&1

    # Configurar cron job para ejecución cada 2 horas
    echo "0 */2 * * * ec2-user bash /home/ec2-user/orchestrate_and_api.sh >> /home/ec2-user/orchestrator_and_api.log 2>&1" | sudo tee /etc/cron.d/orchestrate_and_api
    sudo systemctl start crond
    sudo systemctl enable crond
  EOF

  tags = {
    Name = "Orchestrator-And-API-EC2"
  }
}
