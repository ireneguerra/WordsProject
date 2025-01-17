
locals {
  bucket_name_2 = "bucket-words-graphs"
}

resource "null_resource" "create_graph_bucket" {
  provisioner "local-exec" {
    interpreter = ["powershell", "-Command"]
    command = <<EOT
      $BucketName = "${local.bucket_name_2}"
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
