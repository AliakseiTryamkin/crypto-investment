#define base docker image
FROM openjdk:17-alpine
LABEL maintainer="Aliaksei_Belski1"
COPY target/cryptorecommendation-1.0.0.jar /cryptoapp/crypto-investment.jar
COPY resources/input /cryptoapp/input
WORKDIR /cryptoapp
ENTRYPOINT ["java", "-jar", "crypto-investment.jar"]
