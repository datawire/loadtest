FROM openjdk:9-jdk-slim as runtime
MAINTAINER Philip Lombardi <plombardi@datawire.io>

WORKDIR /srv
COPY . .
RUN ./gradlew test fatJar

ENTRYPOINT ["java"]
CMD ["-jar", "build/libs/targetpractice-fat.jar"]
