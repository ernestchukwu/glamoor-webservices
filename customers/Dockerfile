
FROM maven:3.8.7-openjdk-18-slim AS builder
ARG VERSION=0.0.1-SNAPSHOT
WORKDIR /build/
COPY pom.xml /build/
COPY src /build/src/

RUN mvn clean package -DskipTests

# RUN ls -l target/

RUN cp target/customers-${VERSION}.jar target/customers.jar

FROM openjdk:17-slim
WORKDIR /app/

COPY --from=BUILDER /build/target/customers.jar /app/

COPY wait-for-it.sh /usr/local/bin/wait-for-it.sh
RUN chmod +x /usr/local/bin/wait-for-it.sh

EXPOSE 8002
ENTRYPOINT ["java", "-jar", "/app/customers.jar"]
