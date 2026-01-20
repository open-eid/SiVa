#!/usr/bin/env bash

./mvnw clean install -P war -pl '!siva-parent/siva-distribution' -DskipTests