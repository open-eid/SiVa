#!/usr/bin/env bash 

./mvnw clean install -DupdateAsiceVerifier=true -Dpackaging.type=jar -pl '!siva-parent/siva-distribution' -pl '!siva-parent/siva-test' -Dmaven.test.skip=true

