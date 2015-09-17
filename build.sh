#!/bin/bash

# builds and populates maven-local-repo with missing dependencies
mvn clean package -s settings.xml -Dmaven.test.skip=true
