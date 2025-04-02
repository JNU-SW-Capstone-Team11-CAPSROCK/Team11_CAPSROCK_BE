FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/Team11_CAPSROCK_BE-0.0.1-SNAPSHOT.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]