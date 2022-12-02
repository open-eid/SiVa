#!/usr/bin/env bash

# Stop SiVa services
sudo systemctl stop siva-webapp

# Start up services again
sudo systemctl start siva-webapp
