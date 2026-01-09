FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

ARG JAR_FILE=target/accounts-list-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
