# Backend API — Traffic Fine Payment System

Java Spring Boot REST API that provides authentication, fine management, payment processing, SMS notification, and admin reporting.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- MySQL (production) / H2 (development)
- Swagger / OpenAPI 3

## Package Structure

```
backend-api/src/main/java/com/trafficfine/
├── controller/       # REST controllers
├── service/          # Business logic
├── repository/       # JPA repositories
├── entity/           # JPA entities
├── dto/              # Request / response DTOs
├── security/         # JWT filter, config, util
├── integration/      # Mock payment & SMS adapters
└── config/           # App configuration & seed data
```

## Running

```bash
./mvnw spring-boot:run
```

API base URL: `http://localhost:8080/api`

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | JDBC connection URL | H2 in-memory |
| `DB_USERNAME` | Database username | `sa` |
| `DB_PASSWORD` | Database password | (empty) |
| `JWT_SECRET` | HS256 signing secret | (set in application.yml) |
| `JWT_EXPIRATION_MS` | Token TTL in ms | `86400000` (24 h) |
