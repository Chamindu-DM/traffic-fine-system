# Traffic Fine Payment System — Sri Lanka Police Department

A digital traffic fine payment system that modernises fine collection by enabling drivers to pay fines via an Android mobile app or a public web portal, while giving senior officials an admin portal to monitor nationwide collections.

## Repository Structure

```
traffic-fine-system/
├── backend-api/          # Java Spring Boot REST API
├── android-app/          # Android (Kotlin) mobile app
├── public-payment-web/   # React public payment web portal
├── admin-web-portal/     # React admin monitoring portal
└── docs/                 # Architecture diagrams, API docs, and reports
```

## Applications

| Application | Technology | Purpose |
|---|---|---|
| `backend-api` | Java Spring Boot, Spring Security, JPA, MySQL | Central REST API — authentication, fines, payments, SMS, reporting |
| `android-app` | Android Kotlin | On-the-spot fine payment by drivers |
| `public-payment-web` | React | Later online fine payment via browser |
| `admin-web-portal` | React | Nationwide fine collection monitoring for senior officials |

## Quick Start

### Prerequisites

- Java 17+
- Node.js 18+
- Android Studio (for the mobile app)
- MySQL 8 (or use the bundled H2 in-memory DB for development)

### 1. Backend API

```bash
cd backend-api
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### 2. Public Payment Web Portal

```bash
cd public-payment-web
npm install
npm run dev
```

Runs on `http://localhost:5173`.

### 3. Admin Web Portal

```bash
cd admin-web-portal
npm install
npm run dev
```

Runs on `http://localhost:5174`.

### 4. Android App

Open `android-app/` in Android Studio and run on an emulator or physical device.

## Default Admin Credentials (seed data)

| Field | Value |
|---|---|
| Username | `admin` |
| Password | `admin123` |

> **Note:** Change these before any real deployment.

## Development Milestones

| Milestone | Description |
|---|---|
| 1 | Repository & planning |
| 2 | Backend foundation — entities, repositories, services, seed data |
| 3 | Fine lookup & mock payment API |
| 4 | JWT authentication & admin dashboard APIs |
| 5 | Public payment web portal |
| 6 | Admin web portal |
| 7 | Android mobile app |
| 8 | Testing, documentation & final submission |

## Key Design Decisions

- **Mock payment gateway** — accepts test card details; always returns success for valid input.
- **Mock SMS service** — logs SMS messages to the database and console; replaceable with Twilio / Notify.lk.
- **JWT-based admin auth** — public fine lookup and payment endpoints are open; admin endpoints require a Bearer token.
- **Duplicate payment prevention** — the backend rejects payment if the fine status is already `PAID`.

## Contributing

Each team member should work on a dedicated feature branch and open a pull request into `main`. Ensure your commits are meaningful and traceable to a milestone task.

## License

This project is developed as a university software architecture group assignment.
