FROM openjdk:17-oracle
MAINTAINER Amin Norouzi <realaminnorouzi@gmail.com>
ADD target/loan-service-0.0.1-SNAPSHOT.jar docker-loan-service.jar
ENTRYPOINT ["java", "-jar", "/docker-loan-service.jar"]

EXPOSE 8094