FROM eclipse-temurin:21-jre

ARG JAR_FILE=target/mall-lite-order-1.0.jar

WORKDIR /app
COPY ${JAR_FILE} app.jar

EXPOSE 18084
ENTRYPOINT ["java", "-jar", "app.jar"]
