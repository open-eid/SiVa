<!--# Logging-->

Logging functionality is handled by the **SLF4J** logging facade and on top
of it the **Logback** framework is used. As a result, logging can be
configured via the standard Logback configuration file. By default,
logging works on the `INFO` level and logs are directed to the system
console as well as a log file.

The logback xml configuration file can be found at:
```
pdfValidator/pdf-validator-webapp/src/main/resources/logback.xml
```

and when compiled the file will reside at 
```
WEB-INF/classes/logback.xml
```

within the packaged war. There is also a possibility to set the location
of the default configuration file with a system
property `logback.configurationFile` as a JVM argument. The value of
this property can be a URL, a resource on the class path or a path to a
file external to the application.

```bash
java -Dlogback.configurationFile=/path/to/config.xml
```

In this configuration file there are three appenders: `STDOUT` (logs to
standard output), `FILE` (logs to a file) and `SYSLOG` (logs to syslog
server over the network). To disable certain appender from logging,
commenting out its `appender-ref` is sufficient, but it is *recommended*
that the appender itself should also be commented out. For example to
disable `SYSLOG` appender (which is the default configuration), then one
can use following configuration:

```xml
<!--
<appender name="SYSLOG" class="ch.qos.logback.classic.net.SyslogAppender">
	<syslogHost>enter\_ip\_or\_hostname\_here</syslogHost>
	<port>514</port>
	<facility>USER</facility>
	<filter class="ch.qos.logback.classic.filter.ThresholdFilter">
		<level>INFO</level>
	</filter>
	<suffixPattern>%-5level %logger{0}:%L \[%thread\] - %msg</suffixPattern>
</appender>
-->

<root level="DEBUG">
<appender-ref ref="STDOUT"/>
<appender-ref ref="FILE"/>
<!--<appender-ref ref="SYSLOG"/>-->

</root>
```

Logback configuration manual: <http://logback.qos.ch/manual/>

STDOUT appender
----------------

- Default the log level is set to DEBUG
- Appender output pattern is: `%d{HH:mm:ss.SSS} %-5level %logger{0}:%L [%thread] - %msg%n`

FILE appender
-------------

- Default log level is set to `INFO`
- uses RollingFileAppender configured with `TimeBasedRollingPolicy`.
  Current configuration makes a seperate logfile for each day and each
  file is kept for *30 days*.

  *PS!* keep in mind when using relative
  destination file path, then the path is added at the end of the
  currently working directory, i.e. where the application was started.
  (Current day's logfile path: `logs/pdf-validator-webapp.log`,
  prievious days pattern: 

		logs/pdf-validator-webapp.%d{yyyy-MM-dd}.log)

- Appender output pattern is:  `%d{HH:mm:ss.SSS} %-5level %logger{0}:%L \[%thread\] - %msg%n`

		-Dlogback.configurationFile=config.xml

SYSLOG appender
---------------

- Default log level is set to `INFO`
- Target's ip/hostname and port are configurable
- Syslog messsages' severity is configurable
- Syslog messages' payload's timestamp and hostname part are created
  implicitly and the suffixpattern is:  `%-5level %logger{0}:%L \[%thread\] - %msg`
