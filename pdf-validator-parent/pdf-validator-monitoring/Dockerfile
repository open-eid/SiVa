FROM java:8

RUN mkdir /opt/apps
ADD ./target/pdf-*.jar /opt/apps/monitoring.jar

CMD java -jar /opt/apps/monitoring.jar
