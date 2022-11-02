FROM openjdk:11

RUN mkdir /app

RUN echo $a*

COPY /home/runner/work/sn-connector/build/libs/sn-connector.jar /app/sn-connector.jar

ENTRYPOINT ["java", "-jar", "/app/sn-connector.jar"]
