FROM openjdk:11

ADD build/libs/sn-connector.jar sn-connector.jar

ENTRYPOINT ["java", "-jar", "/sn-connector.jar"]
