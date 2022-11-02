FROM openjdk:11

RUN mkdir /app

COPY Dockerfile /app/Dockerfile

COPY /home/runner/work/sn-connector/sn-connector/build/libs/sn-connector.jar /app/sn-connector.jar

ENTRYPOINT ["java", "-jar", "/app/sn-connector.jar"]
