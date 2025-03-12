# State bucket
terraform {
  backend "s3" {
    bucket  = "heapoverflows-s3-bucket"
    key     = "heapoverflows-s3-bucket/terraform.tfstate"
    region  = "af-south-1"
    encrypt = true
  }
}
