FROM openjdk:11

ENTRYPOINT ["java", "-jar", "/app/sn-connector.jar"]
