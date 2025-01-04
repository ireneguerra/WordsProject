provider "aws" {
  region = "us-east-1"
}

resource "tls_private_key" "ssh_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "aws_key_pair" "my_key" {
  key_name   = "terraform-key-neo4jj"
  public_key = tls_private_key.ssh_key.public_key_openssh
}

resource "aws_security_group" "neo4j_sg" {
  name_prefix = "neo4j-sg"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Permitir SSH desde cualquier lugar
  }

  ingress {
    from_port   = 7474
    to_port     = 7474
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Permitir conexiones HTTP a Neo4j
  }

  ingress {
    from_port   = 7687
    to_port     = 7687
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Permitir conexiones Bolt a Neo4j
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

variable "neo4j_password" {
  description = "La contraseña para el usuario Neo4j"
  type        = string
  default     = "my-secure-password" # Puedes cambiarla o usar un tfvars
}

resource "aws_instance" "neo4j_instance" {
  ami           = "ami-0c02fb55956c7d316" # Amazon Linux 2 AMI
  instance_type = "t2.micro"

  key_name               = aws_key_pair.my_key.key_name
  security_groups        = [aws_security_group.neo4j_sg.name]
  associate_public_ip_address = true

  tags = {
    Name = "Neo4j-Server"
  }

  user_data = <<-EOF
    #!/bin/bash
    # Actualizar paquetes
    sudo yum update -y

    # Instalar Neo4j
    sudo rpm --import https://debian.neo4j.com/neotechnology.gpg.key
    echo "[neo4j]
    name=Neo4j
    baseurl=https://yum.neo4j.com/stable
    enabled=1
    gpgcheck=1" | sudo tee /etc/yum.repos.d/neo4j.repo
    sudo yum install -y neo4j

    # Configurar Neo4j para conexiones remotas
    sudo sed -i 's/#dbms.default_listen_address=0.0.0.0/dbms.default_listen_address=0.0.0.0/' /etc/neo4j/neo4j.conf
    sudo sed -i 's/#dbms.connector.bolt.listen_address=:7687/dbms.connector.bolt.listen_address=:7687/' /etc/neo4j/neo4j.conf

    # Iniciar y habilitar Neo4j
    sudo systemctl start neo4j
    sudo systemctl enable neo4j

    # Configurar contraseña predeterminada
    sleep 150 # Espera para que el servicio esté completamente disponible
    curl -u neo4j:neo4j -X POST http://localhost:7474/user/neo4j/password -d 'password=${var.neo4j_password}'
  EOF
}

output "private_key" {
  value     = tls_private_key.ssh_key.private_key_pem
  sensitive = true
}

output "public_ip" {
  value = aws_instance.neo4j_instance.public_ip
}

resource "local_file" "public_ip_file" {
  filename = "public_ip.txt"
  content  = aws_instance.neo4j_instance.public_ip
}

# Recurso para guardar la clave privada en un archivo local .pem
resource "local_file" "ssh_private_key_file" {
  filename = "terraform_neo4j_key.pem"
  content  = tls_private_key.ssh_key.private_key_pem

  # Si usas Linux/Mac/WSL, puedes fijar 0400 para que solo el propietario lea el archivo
  file_permission      = "0400"
  directory_permission = "0755"
}
