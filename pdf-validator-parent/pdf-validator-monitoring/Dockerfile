FROM java:8

ENV MONITORING_HOST http://localhost:8080
ENV MONITORING_INTERVAL 300000

RUN mkdir /opt/apps
ADD ./target/pdf-*.jar /opt/apps/monitoring.jar

CMD java -jar /opt/apps/monitoring.jar --monitoring.host=${MONITORING_HOST} --monitoring.requestInterval=${MONITORING_INTERVAL}
