FROM openjdk:17-oracle
MAINTAINER Amin Norouzi <realaminnorouzi@gmail.com>
ADD target/transaction-service-0.0.1-SNAPSHOT.jar docker-transaction-service.jar
ENTRYPOINT ["java", "-jar", "/docker-transaction-service.jar"]

EXPOSE 8093