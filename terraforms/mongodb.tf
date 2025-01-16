locals {
  bucket_name_3 = "public-ip-mongo-bucket"
}

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


provider "aws" {
  region = "us-east-1"
}

resource "tls_private_key" "ssh_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "aws_key_pair" "my_key" {
  key_name   = "ec2-key"
  public_key = tls_private_key.ssh_key.public_key_openssh
}

resource "aws_security_group" "mongodb_sg" {
  name_prefix = "mongodb-sg"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 27017
    to_port     = 27017
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "mongodb_instance" {
  ami           = "ami-0c02fb55956c7d316"
  instance_type = "t2.micro"

  key_name               = aws_key_pair.my_key.key_name
  security_groups        = [aws_security_group.mongodb_sg.name]
  associate_public_ip_address = true

  tags = {
    Name = "MongoDB-Server"
  }

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
}

resource "local_file" "public_ip_file" {
  filename = "public_ip.txt"
  content  = aws_instance.mongodb_instance.public_ip
}

resource "null_resource" "upload_public_ip" {
  provisioner "local-exec" {
    interpreter = ["powershell", "-Command"]
    command = <<EOT
      $BucketName = "${local.bucket_name_3}"
      $FilePath = "${local_file.public_ip_file.filename}"
      aws s3 cp $FilePath "s3://$BucketName/public_ip.txt"
      Write-Output "Archivo public_ip.txt subido al bucket $BucketName."
    EOT
  }
}

output "private_key" {
  value     = tls_private_key.ssh_key.private_key_pem
  sensitive = true
}

output "public_ip" {
  value = aws_instance.mongodb_instance.public_ip
}