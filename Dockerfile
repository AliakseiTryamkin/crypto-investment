#define base docker image
# FROM openjdk:17-alpine
# LABEL maintainer="Aliaksei_Belski1"
# WORKDIR /cryptoapp
# COPY target/cryptorecommendation-1.0.0.jar /cryptoapp/crypto.jar
# COPY resources/input /cryptoapp/input
# # ADD . ./src/main/resources/input
# ENTRYPOINT ["java", "-jar", "crypto.jar"]
