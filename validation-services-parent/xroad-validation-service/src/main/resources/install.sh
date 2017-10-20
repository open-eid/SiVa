#!/usr/bin/env bash

./mvnw install:install-file -DlocalRepositoryPath=../../../external-lib-repository -DcreateChecksum=true -Dpackaging=jar -Dfile=./xroad-jars/asic-verifier-1.0-mod.jar -DgroupId=ee.openid.siva -DartifactId=xroad-asice-verifier -Dversion=1.0
