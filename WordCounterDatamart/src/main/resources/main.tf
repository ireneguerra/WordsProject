provider "aws" {
  region = "us-east-1" # Cambia a tu región preferida
}

# Generar clave SSH
resource "tls_private_key" "ssh_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

# Crear el par de claves en AWS
resource "aws_key_pair" "my_key" {
  key_name   = "terraform-key"
  public_key = tls_private_key.ssh_key.public_key_openssh
}

# Grupo de seguridad para MongoDB
resource "aws_security_group" "mongodb_sg" {
  name_prefix = "mongodb-sg"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Permitir SSH desde cualquier lugar
  }

  ingress {
    from_port   = 27017
    to_port     = 27017
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Permitir conexiones a MongoDB
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Instancia EC2
resource "aws_instance" "mongodb_instance" {
  ami           = "ami-0c02fb55956c7d316" # Amazon Linux 2 AMI
  instance_type = "t2.micro"

  key_name               = aws_key_pair.my_key.key_name
  security_groups        = [aws_security_group.mongodb_sg.name]
  associate_public_ip_address = true

  tags = {
    Name = "MongoDB-Server"
  }

  # Script para instalar y configurar MongoDB
  user_data = <<-EOF
    #!/bin/bash
    # Actualizar paquetes
    sudo yum update -y

    # Configurar repositorio de MongoDB
    echo "[mongodb-org-6.0]
    name=MongoDB Repository
    baseurl=https://repo.mongodb.org/yum/amazon/2/mongodb-org/6.0/x86_64/
    gpgcheck=1
    enabled=1
    gpgkey=https://www.mongodb.org/static/pgp/server-6.0.asc" | sudo tee /etc/yum.repos.d/mongodb-org-6.0.repo

    # Instalar MongoDB
    sudo yum install -y mongodb-org

    # Configurar MongoDB para permitir conexiones remotas
    sudo sed -i 's/bindIp: 127.0.0.1/bindIp: 0.0.0.0/' /etc/mongod.conf

    # Iniciar y habilitar MongoDB
    sudo systemctl start mongod
    sudo systemctl enable mongod

    # Instalar herramientas adicionales (opcional)
    sudo yum install -y net-tools
  EOF
}

# Output de la clave privada
output "private_key" {
  value     = tls_private_key.ssh_key.private_key_pem
  sensitive = true
}

# Output de la IP pública de la instancia
output "public_ip" {
  value = aws_instance.mongodb_instance.public_ip
}
