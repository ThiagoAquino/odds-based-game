FROM gradle:7.6-jdk17 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts /app/

RUN gradle clean build --no-daemon || return 0

COPY . /app

RUN gradle clean build --no-daemon

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
