#!/bin/bash

mvn clean package -o -s settings.xml -Dmaven.test.skip=true
