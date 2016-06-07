#!/usr/bin/env bash

ls -al

unzip -o distribution.zip

# Rename applications
find . -name "siva-webapp*.jar" -exec sh -c 'mv "$1" "siva-webapp.jar"' _ {} \;
find . -name "siva-sample-application*.jar" -exec sh -c 'mv "$1" "siva-sample-application.jar"' _ {} \;

# Copy JAR files to apps directory
mv ci-build-info.yml /var/apps
mv *.jar /var/apps
ls -al /var/apps

# Run installation of new apps
chmod +x ./install.sh && ./install.sh

rm distribution.zip

