######################
# Ejemplo completo
######################
locals {
  bucket_name_4 = "bucket-de-tus-jars"

  # Ajusta la ruta local de cada jar en Windows
  jar_files = [
    {
      name = "BookCrawler.jar"
      path = "BookCrawler.jar"
    },
    {
      name = "WordCounterDatamart.jar"
      path = "WordCounterDatamart.jar"
    },
    {
      name = "WordsGraph.jar"
      path = "WordsGraph.jar"
    },
    {
      name = "APIGraph.jar"
      path = "APIGraph.jar"
    }
  ]

  # Construimos la cadena de comandos para cada jar
  upload_commands = join("\n", [
    for jar in local.jar_files :
    "aws s3 cp \"${jar.path}\" \"s3://${local.bucket_name_4}/${jar.name}\" --only-show-errors"
  ])
}

resource "null_resource" "upload_jars" {
  provisioner "local-exec" {
    interpreter = ["powershell", "-Command"]
    command = <<-EOT
      $BucketName = "${local.bucket_name_4}"

      # Verificar si el bucket existe
      if (!(aws s3api head-bucket --bucket $BucketName 2>$null)) {
        Write-Host "El bucket $BucketName no existe. Creándolo..."
        aws s3 mb "s3://$BucketName"
      } else {
        Write-Host "El bucket $BucketName ya existe."
      }

      # Subir cada jar al bucket
      ${local.upload_commands}

      Write-Host "¡JARs subidos exitosamente al bucket $BucketName!"
    EOT
  }
}
