#!/usr/bin/env bash

./mvnw clean install -P war -P \!dependency-check -pl '!siva-parent/siva-distribution' -DskipTests