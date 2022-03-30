FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV SECRET_KEY "default"
ENV STORAGE_FOLDER "/var/www/uploads/"
ENTRYPOINT ["java","-jar","/app.jar"]