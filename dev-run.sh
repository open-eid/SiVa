#!/usr/bin/env bash 

source ./helper-scripts.conf

./build.sh && ./apache-tomcat-$TOMCAT_VERSION/bin/catalina.sh run
