locals {
  bucket_name_4 = "jars-app-bucket"

  # Ajusta la ruta local de cada jar en tu máquina
  jar_files = [
    {
      name = "BookCrawler.jar"
      path = "../out/artifacts/BookCrawler_jar/BookCrawler.jar"
    },
    {
      name = "WordCounterDatamart.jar"
      path = "../out/artifacts/WordCounterDatamart_jar/WordCounterDatamart.jar"
    },
    {
      name = "WordsGraph.jar"
      path = "../out/artifacts/WordsGraph_jar/WordsGraph.jar"
    },
    {
      name = "APIGraph.jar"
      path = "../APIGraph/target/APIGraph-1.0-SNAPSHOT.jar"
    }
  ]

  # Calculamos el hash de los archivos JAR para detectar cambios
  jar_hashes = [
    for jar in local.jar_files : filemd5(jar.path)
  ]

  # Construimos la cadena de comandos para cada jar
  upload_commands = join("\n", [
    for jar in local.jar_files :
    "aws s3 cp \"${jar.path}\" \"s3://${local.bucket_name_4}/${jar.name}\" --only-show-errors"
  ])
}

resource "null_resource" "upload_jars" {
  # Activar el recurso siempre que cambien los hashes de los archivos JAR
  triggers = {
    jar_hashes = join(",", local.jar_hashes)
  }

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
