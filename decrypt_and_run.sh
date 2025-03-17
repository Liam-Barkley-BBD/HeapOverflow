#!/bin/bash

# Decrypt .env.enc to .env
openssl enc -d -aes-256-cbc -salt -pbkdf2 -in .env.enc -out .env

# Load environment variables
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

# Run the JAR file
java -jar cli-1.0.0-SNAPSHOT.jar
