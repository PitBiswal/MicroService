FROM openjdk:8
LABEL maintainer="Pitabasauser"
ADD target/PhotoAppApiUsers-0.0.1-SNAPSHOT.jar photoappuser.jar
ENTRYPOINT ["java","-jar","-Djava.security.egd=file:/dev/./urandom","photoappuser.jar"]