#!/usr/bin/env bash 

source ./helper-scripts.conf

./apache-tomcat-$TOMCAT_VERSION/bin/catalina.sh run
