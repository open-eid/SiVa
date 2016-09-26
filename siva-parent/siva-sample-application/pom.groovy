project {
    modelVersion '4.0.0'

    parent ('io.github.openeid.siva:siva-parent:2.0.2-SNAPSHOT')

    artifactId 'siva-sample-application'
    packaging '${packagingType}'
    name 'SiVa Sample Web application'
    description 'SiVa REST web service sample web application to test digitally signed document validation'

    dependencies {
        dependency ('org.springframework.boot:spring-boot-starter-cache')
        dependency ('io.jmnarloch:rxjava-spring-boot-starter')
        dependency ('org.springframework.boot:spring-boot-starter-actuator')
        dependency ('org.springframework.boot:spring-boot-actuator-docs')
        dependency ('org.springframework.hateoas:spring-hateoas')
        dependency ('org.springframework.boot:spring-boot-starter-security')
        dependency ('com.domingosuarez.boot:spring-boot-starter-jade4j')
        dependency ('de.neuland-bfi:spring-jade4j')
        dependency ('de.neuland-bfi:jade4j')
        dependency ('eu.michael-simons:wro4j-spring-boot-starter')
        dependency ('com.github.ben-manes.caffeine:caffeine')
        dependency ('commons-codec:commons-codec')
        dependency ('org.zeroturnaround:zt-zip')
        dependency ('org.webjars.bower:bootstrap')
        dependency ('org.webjars.bower:jquery')
        dependency ('org.webjars.bower:tether')
        dependency ('org.webjars:dropzone')
        dependency ('org.webjars.bower:highlightjs')
        dependency ('commons-io:commons-io')
        dependency ('org.webjars.bower:js-cookie')
        dependency ('org.projectlombok:lombok')
        dependency ('org.json:json')
        dependency ('com.jayway.jsonpath:json-path:2.2.0')
        dependency ('com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.7.4')

        dependency (groupId: 'org.springframework.boot', artifactId: 'spring-boot-starter-test', scope: 'test')
        dependency (groupId: 'net.sourceforge.htmlunit', artifactId: 'htmlunit', scope: 'test')
        dependency (groupId: 'org.springframework.boot', artifactId: 'spring-boot-configuration-processor', optional: true)
        dependency (groupId: 'org.springframework.boot', artifactId: 'spring-boot-devtools', optional: true)
    }

    build {
        plugins {
            plugin ('org.springframework.boot:spring-boot-maven-plugin:${spring.boot.version}') {
                executions {
                    execution (goals: ['repackage'])
                }
                configuration {
                    executable true
                }
            }
            plugin ('com.spotify:docker-maven-plugin:0.4.11') {
                configuration {
                    imageName 'mihkels/${artifactId}'
                    imageTags {
                        imageTag '${project.version}'
                        imageTag 'latest'
                    }
                    env { JAVA_OPTS: '""' }
                    baseImage 'cogniteev/oracle-java:latest'
                    entryPoint 'exec java $JAVA_OPTS -jar /${project.build.finalName}.jar'
                    resources {
                        resource {
                            targetPath '/'
                            directory '${project.build.directory}'
                            include '${project.build.finalName}.jar'
                        }
                    }
                    serverId 'docker-hub'
                    retryPushCount '0'
                }
            }
        }
    }

    profiles {
        profile {
            id 'jar'
            activation {
                activeByDefault true
            }
            properties {
                packagingType 'jar'
            }
            dependencies {
                dependency {
                    groupId 'org.springframework.boot'
                    artifactId 'spring-boot-starter-web'
                    exclusions {
                        exclusion {
                            artifactId 'spring-boot-starter-tomcat'
                            groupId 'org.springframework.boot'
                        }
                    }
                }
                dependency ('org.springframework.boot:spring-boot-starter-undertow')
            }
        }
        profile {
            id 'war'
            build {
                plugins {
                    plugin {
                        artifactId 'maven-war-plugin'
                        version '2.6'
                        configuration {
                            failOnMissingWebXml false
                        }
                    }
                }
            }
            properties {
                packagingType 'war'
            }
            dependencies {
                dependency ('org.springframework.boot:spring-boot-starter-web')
                dependency (groupId:  'org.springframework.boot',  artifactId: 'spring-boot-starter-tomcat', scope: 'provided')
            }
        }
    }
}
