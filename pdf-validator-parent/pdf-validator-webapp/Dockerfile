FROM tomcat:7.0.64-jre8

ENV APP_DIR /opt/apps
ENV CONF_DIR /usr/local/tomcat/conf/Catalina/localhost/

RUN mkdir -p ${CONF_DIR}
RUN mkdir ${APP_DIR}

ADD ./target/*.war ${APP_DIR}/webapp.war
ADD ./docker-conf/*.xml ${CONF_DIR}
