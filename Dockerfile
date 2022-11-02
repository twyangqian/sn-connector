FROM openjdk:11

RUN mkdir /app

COPY ./build/libs/sn-connector.jar /app/sn-connector.jar

ENTRYPOINT ["java", "-jar", "/app/sn-connector.jar"]
