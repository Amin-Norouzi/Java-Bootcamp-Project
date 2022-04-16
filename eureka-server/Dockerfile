FROM openjdk:17-oracle
MAINTAINER Amin Norouzi <realaminnorouzi@gmail.com>
ADD target/eureka-server-0.0.1-SNAPSHOT.jar docker-eureka-server.jar
ENTRYPOINT ["java", "-jar", "/docker-eureka-server.jar"]

EXPOSE 8090