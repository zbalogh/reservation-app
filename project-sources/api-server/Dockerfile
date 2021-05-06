#FROM openjdk:8-jdk-alpine
FROM openjdk:8

EXPOSE 8080

ADD target/*jar app.jar

ENV JAVA_OPTS=""

#ENTRYPOINT ["java", "-jar", "app.jar"]
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
