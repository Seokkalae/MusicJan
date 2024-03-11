#Build stage
FROM  maven:3.9.6-eclipse-temurin-21-alpine as build
LABEL authors="Seokkalae"
COPY ../pom.xml /var/maven/
COPY ../src/ /var/maven/src
RUN mvn -f /var/maven/pom.xml package -DskipTests \
    && mv /var/maven/target/*.[jwe]ar /var/maven/

#Deploy stage
FROM openjdk:21-jdk-slim
RUN addgroup --system java \
    && adduser --system --home /opt/musicjan java
USER java
WORKDIR /opt/musicjan
COPY --from=build /var/maven/*.[jwe]ar /opt/musicjan/musicjan.jar

ENTRYPOINT ["java", "-Xmx750m", "-jar","musicjan.jar"]