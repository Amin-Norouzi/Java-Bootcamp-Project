FROM openjdk:17-oracle
MAINTAINER Amin Norouzi <realaminnorouzi@gmail.com>
ADD target/customer-service-0.0.1-SNAPSHOT.jar docker-customer-service.jar
ENTRYPOINT ["java", "-jar", "/docker-customer-service.jar"]

EXPOSE 8091