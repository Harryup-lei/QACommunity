FROM openjdk:17-jdk-slim

WORKDIR /app

COPY pom.xml .
RUN apt-get update && apt-get install -y maven && \
    mvn dependency:resolve && \
    apt-get remove -y maven && apt-get clean

COPY src src

RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/qa-community-1.0.0.jar"]
