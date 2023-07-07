#!/usr/bin/env bash 

./mvnw clean install -DupdateAsiceVerifier=true -Dpackaging.type=jar -pl '!siva-parent/siva-distribution' -Dmaven.test.skip=true -P\!dependency-check

