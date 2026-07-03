FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib ./common-lib
COPY fine-service ./fine-service
RUN mvn clean package -pl fine-service -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/fine-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
