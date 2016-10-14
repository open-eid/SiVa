#!/usr/bin/env bash

ls -al

# Add docker compose to $PATH
export PATH=$PATH:/usr/local/bin

# Remove all unused Docker images to free up disk space and avoid out-of-disk space problems
docker rmi $(docker images --filter "dangling=true" -q --no-trunc)

# Stop all SiVa validation services and remove them
docker-compose stop && docker-compose rm -f

# Pull latest docker image
docker-compose pull

# Start all SiVa validation services
docker-compose up -d
