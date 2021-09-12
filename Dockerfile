FROM openjdk:11-jdk-slim AS build-env
ADD . /build-dir
WORKDIR /build-dir
RUN ./gradlew :server:bootJar

FROM gcr.io/distroless/java:11
COPY --from=build-env /build-dir/server/build/libs/server.jar /server/
WORKDIR /server
ENTRYPOINT ["java", "-jar", "server.jar"]