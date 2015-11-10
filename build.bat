@echo off

set SKIP_AND_COMPILE_TESTS="-DskipTests"
set SKIP_TESTS="-Dmaven.test.skip=true"

set DEP_PURGE_CMD="dependency:purge-local-repository"
set DEP_PUREGE_OPTS="-DreResolve=false"


set OPTS=%SKIP_TESTS%
CALL mvnw.bat clean package -Dmaven.test.skip.exec 
