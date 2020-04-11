FROM openjdk:11.0.6-jre
VOLUME /tmp
ARG APPJAR=target/*.jar
COPY target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]