FROM openjdk:8-jre-alpine

WORKDIR /app

ADD target/api.jar ./api.jar

ENTRYPOINT ["java", "-jar", "/app/api.jar"]