#!/usr/bin/env bash

ls -al

# Add docker compose to $PATH
export PATH=$PATH:/usr/local/bin

# Stop all SiVa validation services and remove them
docker-compose stop && docker-compose rm -f

# Pull latest docker image
docker-compose pull

# Start all SiVa validation services
docker-compose up -d
