FROM openjdk:11

RUN mkdir /app

ARG JAR_FILE="sn-connector.jar"

RUN apk add gradle

COPY . /app/

WORKDIR /app

RUN ./gradlew clean build

RUN cp build/libs/${JAR_FILE} /app/${JAR_FILE}

ENTRYPOINT ["java", "-jar", "/app/sn-connector.jar"]
