FROM gradle:4.10-jdk8-alpine AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:8-jdk-alpine

COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar

ENTRYPOINT [ "java", "-jar", "/app.jar" ]