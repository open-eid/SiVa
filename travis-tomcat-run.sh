#!/usr/bin/env bash

source ./helper-scripts.conf

./apache-tomcat-$TOMCAT_VERSION/bin/startup.sh

# Check that service is up
curl -sL -w "Service is up with HTTP code: %{http_code}\\n" -X POST -d "@test-files/check_status_request.xml" http://localhost:8080/pdf-validator-webapp/wservice/validationService -o /dev/null

echo Sleep for 60 seconds to make sure that all the certificates have been loaded in
sleep 60

