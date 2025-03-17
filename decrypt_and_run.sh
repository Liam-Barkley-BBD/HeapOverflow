#!/bin/bash

# Check for OpenSSL
if ! command -v openssl &> /dev/null; then
    echo "Error: OpenSSL is not installed or not in PATH."
    exit 1
fi

# Check for Java
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH."
    exit 1
fi

# Decrypt .env.enc to .env
openssl enc -d -aes-256-cbc -salt -pbkdf2 -in .env.enc -out .env

# Load environment variables
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

# Run the JAR file
java -jar cli-1.0.0-SNAPSHOT.jar
