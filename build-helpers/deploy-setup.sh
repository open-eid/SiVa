#!/usr/bin/env bash

# Copy JAR files to apps directory
#mv ci-build-info.yml /var/apps
#mv *.jar /var/apps
#ls -al /var/apps
#
#rm /var/apps/*.log

# Remove keystore directory
#rm -fr /var/apps/etc

# Run installation of new apps
#chmod +x ./install.sh && ./install.sh

# Add docker compose to $PATH
export PATH=$PATH:/usr/local/bin

# Stop all SiVa validation services and remove them
docker-compose stop && docker-compose rm -f

# Pull latest docker image
docker-compose pull

# Start all SiVa validation services
docker-compose up -d
