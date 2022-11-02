FROM adoptopenjdk/openjdk11:alpine

RUN mkdir /app

COPY Dockerfile /app/Dockerfile

COPY ./build/libs/sn-connector.jar /app/sn-connector.jar

ENTRYPOINT ["java", "-jar", "/app/sn-connector.jar"]
