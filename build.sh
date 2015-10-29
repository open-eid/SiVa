#!/bin/bash

# builds and populates maven-local-repo with missing dependencies
SKIP_AND_COMPILE_TESTS='-DskipTests'
SKIP_TESTS='-Dmaven.test.skip=true'

# Dependency cleanup in local repository 
DEP_PURGE_CMD='dependency:purge-local-repository'
DEP_PUREGE_OPTS='-DreResolve=false'


OPTS=${SKIP_TESTS} 
# mvn clean install -Dmaven.test.skip.exec
./mvnw clean package -s settings.xml -Dmaven.test.skip.exec
