#!/usr/bin/env bash

./mvnw clean install -P war -P \!dependency-check -DskipITs -pl '!siva-parent/siva-test' -pl '!siva-parent/siva-distribution'