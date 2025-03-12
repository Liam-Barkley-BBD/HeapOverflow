# VPC
resource "aws_vpc" "my_vpc" {
  cidr_block           = var.vpc_cidr_block
  enable_dns_hostnames = var.enable_dns_hostnames
}

# Internet Gateway
resource "aws_internet_gateway" "heapoverflow_db_gateway" {
  vpc_id = aws_vpc.my_vpc.id
}

# Public Subnets
resource "aws_subnet" "subnet_a" {
  vpc_id                  = aws_vpc.my_vpc.id
  cidr_block              = cidrsubnet(var.vpc_cidr_block, 8, 1)
  availability_zone       = "af-south-1a"
  map_public_ip_on_launch = true
}

resource "aws_subnet" "subnet_b" {
  vpc_id                  = aws_vpc.my_vpc.id
  cidr_block              = cidrsubnet(var.vpc_cidr_block, 8, 2)
  availability_zone       = "af-south-1b"
  map_public_ip_on_launch = true
}

# Route Table
resource "aws_route_table" "routedb" {
  vpc_id = aws_vpc.my_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.heapoverflow_db_gateway.id
  }
}

# Route Table Associations
resource "aws_route_table_association" "subnet_a_association" {
  subnet_id      = aws_subnet.subnet_a.id
  route_table_id = aws_route_table.routedb.id
}

resource "aws_route_table_association" "subnet_b_association" {
  subnet_id      = aws_subnet.subnet_b.id
  route_table_id = aws_route_table.routedb.id
}

# Security Group for EC2
resource "aws_security_group" "ec2_sg" {
  name        = "ec2_sg"
  description = "Allow SSH and app traffic"
  vpc_id      = aws_vpc.my_vpc.id

  # Allow SSH
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow App Traffic (Spring Boot)
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  # Allow PostgreSQL Traffic 
  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow All Outbound Traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# RDS Database
resource "aws_db_instance" "heapoverflow_instance" {
  identifier          = "heapoverflow-db"
  storage_type        = "gp2"
  engine              = "postgres"
  engine_version      = "17.4"
  instance_class      = "db.t3.micro"
  allocated_storage   = 20
  publicly_accessible = true

  backup_retention_period = 7
  backup_window           = "03:00-04:00"
  maintenance_window      = "sun:05:00-sun:06:00"
  skip_final_snapshot     = true

  port                   = var.db_port
  username               = var.db_username
  password               = var.db_password
  vpc_security_group_ids = [aws_security_group.ec2_sg.id]

  db_subnet_group_name = aws_db_subnet_group.heapoverflow_db_subnet_group.name
}

# Database Subnet Group
resource "aws_db_subnet_group" "heapoverflow_db_subnet_group" {
  name       = "aws_subnet_group_heapoverflow"
  subnet_ids = [aws_subnet.subnet_a.id, aws_subnet.subnet_b.id]
}

#github runner

resource "aws_instance" "github_runner" {
  ami             = "ami-00d6d5db7a745ff3f"
  instance_type   = "t3.micro"
  subnet_id       = aws_subnet.subnet_a.id
  security_groups = [aws_security_group.ec2_sg.id]
  user_data       = <<-EOF
              #!/bin/bash
              set -ex  # Enable debugging (-x shows each command)

              LOGFILE="/home/ec2-user/setup.log"
              exec > >(tee -a $LOGFILE) 2>&1  # Log both stdout & stderr

              echo "Updating system..."
              sudo yum update -y

              echo "Installing dependencies..."
              sudo yum install -y git curl tar --allowerasing

              echo "Creating runner directory..."
              sudo mkdir -p /home/ec2-user/actions-runner

              echo "Assign Rights 1"
              sudo chown -R ec2-user:ec2-user /home/ec2-user/actions-runner
              
              echo "Assign Rights 2"
              sudo chmod -R 755 /home/ec2-user/actions-runner
              
              echo "Move into runner directory"
              cd /home/ec2-user/actions-runner

              echo "Downloading GitHub Actions Runner..."
              curl -o actions-runner-linux-x64-2.322.0.tar.gz -L https://github.com/actions/runner/releases/download/v2.322.0/actions-runner-linux-x64-2.322.0.tar.gz

              echo "Extracting runner..."
              tar xzf ./actions-runner-linux-x64-2.322.0.tar.gz

              echo "Installing ICU dependencies..."
              sudo yum install -y icu libicu-devel

              echo "Installing Docker..."
              sudo dnf install -y docker  
              sudo systemctl enable --now docker  
              sudo systemctl start docker
              sudo usermod -aG docker ec2-user  


              echo "Assign Rights 1"
              sudo chown -R ec2-user:ec2-user /home/ec2-user/actions-runner
              
              echo "Assign Rights 2"
              sudo chmod -R 755 /home/ec2-user/actions-runner

              echo "Configuring runner..."
              sudo -u ec2-user ./config.sh --url https://github.com/${var.github_account} --token ${var.github_token} --unattended --name ec2-runner --replace --labels ec2-runner
              


              echo "run the runner..."
              sudo -u ec2-user ./run.sh

              echo "Setup complete!"
              EOF

  tags = {
    Name = "github-runner"
  }
}
