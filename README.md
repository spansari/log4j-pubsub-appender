Google Cloud Pubsub appender for Log4j
=======================================

Google cloud pubsub appender for log4j2 to publish logs directly to a topic in google cloud pubsub.


Usage:
=====
### `log4j2-spring.xml, log4j2.xml or log4j2.properties`

In case the Java Application is not run from Google Cloud machine,
or if you just want to control all params manually and/or use ServiceAccount credentials
config is a bit more complicated.

**xml example**
```xml

<Appenders>
  <Pubsub name="log_to_pubsub" projectId="google-projectId-with billing" topic="mytopic">
    <GoogleCloudCredentials serviceAccountJsonFileName="serviceAccountFile.json"/>
  </Pubsub>
</Appenders>
```

**properties example**
```bash
appender.pubsubappender.type = PubSub
appender.pubsubappender.name = pubsub_appender
appender.pubsubappender.topic = mytopic
appender.pubsubappender.projectId = my_gcp_project_id
appender.pubsubappender.googleCloudCredentials.type = GoogleCloudCredentials
appender.pubsubappender.googleCloudCredentials.serviceAccountJsonFileName = serviceAccountFile.json

logger.pubsublogger.name = log_to_pubsub
logger.pubsublogger.level = info
logger.pubsublogger.appenderRef.pubsubappender.ref = pubsub_appender
logger.pubsublogger.additivity = false
```

### Clone this repo and build:
```bash
mvn install
```
Then update `pom.xml` of your project where you want to use this appender

```xml
<dependency>
  <groupId>io.github.spansari</groupId>
  <artifactId>log4j2-pubsub-appender</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```
