#!/usr/bin/env bash

# Remove files after testing
sudo systemctl stop siva-test-webapp
rm /var/apps/stage/siva-webapp.jar

rm distribution.zip
