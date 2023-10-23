FROM eclipse-temurin:17

RUN mkdir /app

COPY ./chess-v2.jar /app/app.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "app.jar"]