FROM openjdk:11.0.6-jre
VOLUME /tmp
ARG APPJAR=target/*.jar
COPY ${APPJAR} app.jar

EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]