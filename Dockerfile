FROM openjdk:11

RUN mkdir /app

COPY Dockerfile /app/Dockerfile

COPY build/libs/**.jar /app/sn-connector.jar

ENTRYPOINT ["java", "-jar", "/app/sn-connector.jar"]
