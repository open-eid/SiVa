#!/usr/bin/env bash

ls -al

unzip -o distribution.zip

# Rename applications
find . -name "siva-webapp*.jar" -exec sh -c 'mv "$1" "siva-webapp.jar"' _ {} \;
find . -name "siva-sample-application*.jar" -exec sh -c 'mv "$1" "siva-sample-application.jar"' _ {} \;
find . -name "xroad-validation-service*.jar" -exec sh -c 'mv "$1" "siva-xroad-validation.jar"' _ {} \;
