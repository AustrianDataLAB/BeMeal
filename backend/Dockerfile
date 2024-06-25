FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /opt/app

COPY pom.xml ./
COPY application.yml ./
COPY ./src ./src
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S -g 9001 non-priv-group
RUN adduser -S -u 9001 -G non-priv-group non-priv-user
USER 9001

WORKDIR /opt/app
COPY --from=build /opt/app/target/*.jar /opt/app/*.jar
COPY --from=build /opt/app/application.yml /opt/app/application.yml
EXPOSE 8080
ENTRYPOINT ["java",  "-jar", "-Dspring.profiles.active=container", "-Dspring.config.location=file:/opt/app/application.yml", "/opt/app/*.jar"]
