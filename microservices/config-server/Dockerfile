FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY common-lib ./common-lib
COPY config-server ./config-server
RUN mvn clean package -pl config-server -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/config-server/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
