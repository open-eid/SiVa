<!--# Deployment view-->

![SiVa Deployment view](../img/siva/uml_siva_deployment_diagram.png)

## Load balancer

Load balancer can distribute traffic between SiVa nodes when there is more then one instance running.
SiVa do not set any specific requirements for load balancer but in diagram the Nginx reverse proxy option is used.

## SiVa web application

SiVa web appliction is executable Spring Boot JAR file. This means all the dependencies and servlet containers are
packaged inside single JAR file. The JAR file can be placed anywhere in server and the JAR must be marked executable
if its not already.

There also should be separate user created to run executalbe JAR as Linux service.

Read more about running
[Spring Boot applications as Linux system service](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-service)

## SiVa X-Road validation service

SiVa X-Road validation service is also Spring Boot executable JAR application and also should be installed as Linux
service. X-Road validation service communicates with SiVa web application over HTTP and default port is 8081

X-Road separate installation is required to avoid BouncyCastle libraries version conflicts and class loader
issues.

## Security server

Into XRoad v6 security server the SiVa validation service will be registered to provide service to other organisations
using XRoad infrastructure. Setting up XRoad Security server is out of scope for SiVa documentaiton.


