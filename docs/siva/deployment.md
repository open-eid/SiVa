# Deployment

## System requirements

Following are minimum requirements to build and deploy SiVa validation
web service:

* Java 8 or above Oracle JVM is supported
* Git version control system version 1.8 or above is recommended
* 2GB of RAM
* 1 processor core
* Open internet connection
* 1GB of free disk space
* Supported operating system is Ubuntu 14.04 LTS  

## Building SiVa validation web service on Ubuntu 16.04

First we need to install Git and Java SDK 8 by issuing below commands:
 
```bash
sudo apt-get update
sudo apt-get install git -y
sudo apt-get install default-jdk -y
```

Next we need to clone the SiVa Github repository:

```bash
git clone https://github.com/open-eid/SiVa.git --branch develop
```

Final step is building the SiVa project using Maven Wrapper

```bash
cd SiVa
./mvnw install
```
