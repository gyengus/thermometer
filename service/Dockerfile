FROM openjdk:12-alpine
MAINTAINER Gyengus <gyengus@gmail.com>

RUN apk update && apk upgrade && apk add libstdc++ && rm -f /var/cache/apk/*

WORKDIR /app
ADD ./build/libs/thermometer-service.jar /app/

EXPOSE 65080

ENTRYPOINT ["java", "-jar", "/app/thermometer-service.jar"]
