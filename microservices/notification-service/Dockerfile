FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib ./common-lib
COPY notification-service ./notification-service
RUN mvn clean package -pl notification-service -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/notification-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
