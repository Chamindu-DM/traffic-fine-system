FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib ./common-lib
COPY reporting-service ./reporting-service
RUN mvn clean package -pl reporting-service -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/reporting-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
