FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib ./common-lib
COPY auth-service ./auth-service
RUN mvn clean package -pl auth-service -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
