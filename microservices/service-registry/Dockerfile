FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib ./common-lib
COPY service-registry ./service-registry
RUN mvn clean package -pl service-registry -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/service-registry/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
