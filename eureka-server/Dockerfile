
FROM maven:3.8.7-openjdk-18-slim AS builder
ARG VERSION=0.0.1-SNAPSHOT
WORKDIR /build/
COPY pom.xml /build/
COPY src /build/src/

RUN mvn clean package

# RUN ls -l target/

RUN cp target/eureka-server-${VERSION}.jar target/eureka-server.jar

FROM openjdk:17-slim
WORKDIR /app/

COPY --from=BUILDER /build/target/eureka-server.jar /app/

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/app/eureka-server.jar"]
