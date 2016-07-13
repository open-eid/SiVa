#!/usr/bin/env bash

ls -al

unzip -o distribution.zip

# Rename applications
find . -name "siva-webapp*.jar" -exec sh -c 'mv "$1" "siva-webapp.jar"' _ {} \;
find . -name "siva-sample-application*.jar" -exec sh -c 'mv "$1" "siva-sample-application.jar"' _ {} \;

# Copy webapp to testing directory
cp siva-webapp.jar /var/apps/stage

# Start test web service
sudo systemctl start siva-test-webapp
