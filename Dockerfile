FROM openjdk:8-jdk-alpine
ENV SECRET_KEY = "default"
ENV STORAGE_FOLDER = "/var/www/uploads/"
COPY ./src /src
COPY ./pom.xml /src
WORKDIR /src
RUN mvn package
ENTRYPOINT ["java","-jar","/src/app.jar"]