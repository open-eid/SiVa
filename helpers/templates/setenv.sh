export JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
export JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=development"

