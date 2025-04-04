
FROM maven:3.8.7-openjdk-18-slim AS builder
ARG VERSION=0.0.1-SNAPSHOT
WORKDIR /build/
COPY pom.xml /build/
COPY src /build/src/

RUN mvn clean package

# RUN ls -l target/

RUN cp target/config-server-${VERSION}.jar target/config-server.jar

FROM openjdk:17-slim
WORKDIR /app/

COPY --from=BUILDER /build/target/config-server.jar /app/

EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app/config-server.jar"]
