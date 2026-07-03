FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib ./common-lib
COPY payment-service ./payment-service
RUN mvn clean package -pl payment-service -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/payment-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
