#!/usr/bin/env bash

# Stop SiVa services
sudo systemctl stop siva-sample-app
sudo systemctl stop siva-webapp

# Build new version of SiVa
./mvnw clean package

# Start up services again
sudo systemctl start siva-sample-app
sudo systemctl start siva-webapp