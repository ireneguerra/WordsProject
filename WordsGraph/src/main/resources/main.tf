# Definir el valor local
locals {
  bucket_name = "csv-bucket-neo4j" # Cambia este nombre al que desees
}

resource "null_resource" "create_bucket_and_upload" {
  provisioner "local-exec" {
    interpreter = ["powershell", "-Command"]
    command = <<EOT
      $BucketName = "${local.bucket_name}"
      if (aws s3api head-bucket --bucket $BucketName 2>$null) {
        Write-Output "El bucket $BucketName ya existe. Eliminando contenido..."
        aws s3 rm "s3://$BucketName" --recursive
        Write-Output "Eliminando el bucket $BucketName..."
        aws s3api delete-bucket --bucket $BucketName --region us-east-1
      }
      Write-Output "Creando el bucket $BucketName..."
      aws s3api create-bucket --bucket $BucketName --region us-east-1

      # Configurar el bucket para que sea público
      Write-Output "Haciendo público el bucket $BucketName..."
      aws s3api put-bucket-acl --bucket $BucketName --acl public-read

      # Configurar una política de bucket para hacerlo público
      $BucketPolicy = @"
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::$BucketName/*"
        }
    ]
}
"@
      Write-Output "Estableciendo política pública para el bucket $BucketName..."
      aws s3api put-bucket-policy --bucket $BucketName --policy $BucketPolicy
    EOT
  }
}
