FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /opt/app

COPY pom.xml ./
COPY ./src ./src
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S -g 9001 non-priv-group
RUN adduser -S -u 9001 -G non-priv-group non-priv-user
USER 9001

WORKDIR /opt/app
COPY --from=build /opt/app/target/*.jar /opt/app/*.jar
EXPOSE 8080
ENTRYPOINT ["java",  "-jar", "/opt/app/*.jar"]
