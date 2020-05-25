FROM openjdk:8-jre-alpine

ADD target/api.jar /app/api.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/api.jar"]