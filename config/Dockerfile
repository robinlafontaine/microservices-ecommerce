FROM gradle:7.6-jdk AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src/main ./src/main

RUN gradle clean build

FROM amazoncorretto:21-alpine-jdk AS run

LABEL maintainer1="Robin Lafontaine"
LABEL maintainer2="Bastien Ubassy"

RUN apk --no-cache add curl # For healthcheck

RUN addgroup -S app-group && adduser -S app-user -G app-group

RUN mkdir /keys && chown app-user:app-group /keys && chmod 700 /keys

WORKDIR /app

COPY --from=build --chown=app-user:app-group /app/build/libs/config-0.0.1-SNAPSHOT.jar config.jar

EXPOSE 8888
USER app-user

CMD ["java", "-jar", "/app/config.jar"]