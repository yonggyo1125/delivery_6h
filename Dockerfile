FROM eclipse-temurin:25-jdk

WORKDIR /app

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENV DB_DDL_AUTO=none

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 3000