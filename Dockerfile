FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
ENV JAR_FILE ${JAR_FILE}
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
