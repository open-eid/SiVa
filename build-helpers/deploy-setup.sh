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

docker-compose stop && docker-compose rm -f -a
docker-compose start
