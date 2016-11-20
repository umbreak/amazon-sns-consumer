FROM frolvlad/alpine-oraclejdk8:slim
MAINTAINER didac.montero.mendez@gmail.com
VOLUME /tmp
ADD backend-1.0.0.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]