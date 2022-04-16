FROM openjdk:17-oracle
MAINTAINER Amin Norouzi <realaminnorouzi@gmail.com>
ADD target/account-service-0.0.1-SNAPSHOT.jar docker-account-service.jar
ENTRYPOINT ["java", "-jar", "/docker-account-service.jar"]

EXPOSE 8092