#!/usr/bin/env bash

# Stop SiVa services
sudo systemctl stop siva-sample-app
sudo systemctl stop siva-webapp
sudo systemctl stop siva-xroad-validation

# Start up services again
sudo systemctl start siva-sample-app
sudo systemctl start siva-webapp
sudo systemctl start siva-xroad-validation
