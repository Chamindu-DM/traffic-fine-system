# Implementation Guideline and AI Prompt

## Project Summary

Build a digital traffic fine payment system for the Sri Lanka Police Department. The system must modernize traffic fine collection by allowing drivers to pay fines through an Android mobile app or a web portal, while giving senior officials an admin portal to monitor nationwide fine collections.

The project should be implemented as separate applications:

- Backend REST API application
- Android mobile application for on-the-spot fine payment
- Single-page public web application for online fine payment
- Admin web portal for monitoring fine collections

## Main Functional Requirements

### Android Mobile App

Purpose: allow a driver to pay a traffic fine immediately when stopped by a traffic police officer.

Required features:

- Enter unique fine reference number.
- Enter traffic fine category identifier.
- Display fine details before payment.
- Enter payment information.
- Submit payment.
- Show payment success or failure.
- Trigger SMS notification to the relevant traffic police officer after successful payment.

### Public Web Payment Portal

Purpose: allow drivers to pay fines later if they do not pay immediately.

Required features:

- Search fine by reference number and fine category identifier.
- Display fine amount, category, district, officer, and status.
- Allow payment if fine is unpaid.
- Prevent duplicate payment if fine is already paid.
- Show confirmation after successful payment.
- Trigger SMS notification to the traffic police officer after successful payment.

### Admin Web Portal

Purpose: allow senior officials to monitor traffic fine collections nationwide.

Required features:

- JWT-based admin login.
- Dashboard with total fine collections.
- District-wise total collections.
- Fine-category-wise collection breakdown.
- Paid, unpaid, and cancelled fine counts.
- Table/list view of fine records.
- Filters by district, category, status, and date range.

### Backend REST API

Purpose: central service for authentication, fine data, payment processing, SMS notification, and reporting.

Required features:

- REST API with clear endpoints.
- JWT authentication and authorization.
- Fine creation or seed data for testing.
- Fine lookup by reference number and category.
- Payment processing endpoint.
- SMS notification integration or mock SMS service.
- Admin dashboard/reporting endpoints.
- Database persistence.

## Recommended Technology Stack

Use the project requirement preferences where possible:

- Backend: Java Spring Boot
- Security: Spring Security with JWT
- Database access: Spring Data JPA
- Database: MySQL, PostgreSQL, or H2 for development
- Public web portal: React, Vue, or Angular
- Admin portal: React, Vue, or Angular
- Android app: Native Android/Kotlin, Java Android, or Flutter
- API testing: Postman or Swagger/OpenAPI
- Version control: GitHub single repository with regular commits

## Suggested System Architecture

Use a clean layered backend architecture:

- Controller layer: handles REST requests and responses.
- Service layer: contains business logic.
- Repository layer: communicates with the database using JPA.
- Entity/model layer: database-mapped objects.
- DTO layer: request and response objects.
- Security layer: JWT authentication and role-based access.
- Integration layer: SMS and payment gateway adapters.

Suggested applications inside one Git repository:

```text
traffic-fine-system/
  backend-api/
  android-app/
  public-payment-web/
  admin-web-portal/
  docs/
```

## User Roles

Use at least these roles:

- Driver: pays traffic fines through the mobile app or public web portal.
- Admin/Senior Official: logs into admin portal and views reports.
- Traffic Police Officer: receives SMS notification after payment confirmation.

The officer does not necessarily need a login unless the team wants to add extra features.

## Suggested Database Tables

### users

- id
- full_name
- username
- password_hash
- role
- created_at

### officers

- id
- name
- badge_number
- phone_number
- district

### fine_categories

- id
- code
- name
- description
- amount

### traffic_fines

- id
- reference_number
- category_id
- officer_id
- driver_license_number
- vehicle_number
- district
- amount
- status
- issued_at
- paid_at

Suggested statuses:

- UNPAID
- PAID
- CANCELLED

### payments

- id
- fine_id
- payment_reference
- amount
- payment_method
- status
- paid_at

### sms_logs

- id
- fine_id
- officer_id
- phone_number
- message
- status
- sent_at

## Suggested API Endpoints

### Authentication

```http
POST /api/auth/login
```

Request:

```json
{
  "username": "admin",
  "password": "password"
}
```

Response:

```json
{
  "token": "jwt-token",
  "role": "ADMIN"
}
```

### Fine Lookup

```http
GET /api/fines/lookup?referenceNumber=TF123456&categoryCode=SPEEDING
```

Response:

```json
{
  "referenceNumber": "TF123456",
  "category": "Speeding",
  "amount": 5000,
  "district": "Matara",
  "status": "UNPAID"
}
```

### Pay Fine

```http
POST /api/payments
```

Request:

```json
{
  "referenceNumber": "TF123456",
  "categoryCode": "SPEEDING",
  "paymentMethod": "CARD",
  "cardLastFourDigits": "1234"
}
```

Expected behavior:

- Validate fine reference number and category.
- Check whether the fine exists.
- Reject payment if already paid.
- Create payment record.
- Mark fine as paid.
- Send SMS to the traffic police officer.
- Return payment confirmation.

### Admin Dashboard

```http
GET /api/admin/dashboard
Authorization: Bearer jwt-token
```

Response:

```json
{
  "totalCollected": 1250000,
  "paidFineCount": 245,
  "unpaidFineCount": 80,
  "districtWiseCollections": [
    {
      "district": "Matara",
      "amount": 250000
    }
  ],
  "categoryWiseCollections": [
    {
      "category": "Speeding",
      "amount": 400000
    }
  ]
}
```

## Payment Gateway Handling

For a university project, a real payment gateway is not required unless requested by the lecturer. Implement a mock payment service:

- Accept test card/payment details.
- Always return success for valid test input.
- Return failure for invalid input.
- Store a fake payment reference number.

Document clearly that this is a simulated payment flow.

## SMS Handling

Use one of these approaches:

- Integrate a real SMS API such as Twilio, Notify.lk, or another provider.
- Use a mock SMS service that logs the SMS message into the database and console.

For easier development, start with a mock SMS service first. The SMS message can be:

```text
Payment confirmed for fine TF123456. Driver license can be released.
```

## Security Guidelines

- Use JWT tokens for admin authentication.
- Protect admin endpoints with ADMIN role.
- Keep public fine lookup and payment endpoints open, but validate input carefully.
- Store passwords as hashes, not plain text.
- Never expose card details in responses or database logs.
- Validate duplicate payments.
- Use HTTPS in a real deployment.

## Recommended Development Milestones

### Milestone 1: Repository and Planning

- Create one GitHub repository.
- Add folders for backend, mobile app, web apps, and docs.
- Divide tasks among group members.
- Commit initial structure.

### Milestone 2: Backend Foundation

- Create Spring Boot project.
- Configure database and JPA.
- Create entities, repositories, services, and controllers.
- Add sample seed data for fines, officers, categories, and admin user.

### Milestone 3: Fine Lookup and Payment

- Implement fine lookup API.
- Implement mock payment API.
- Implement duplicate payment prevention.
- Implement SMS notification service.

### Milestone 4: Authentication and Admin APIs

- Add Spring Security.
- Implement JWT login.
- Protect admin endpoints.
- Implement dashboard summary APIs.

### Milestone 5: Public Web Portal

- Build fine lookup page.
- Build payment form.
- Build payment success/failure state.
- Connect to backend API.

### Milestone 6: Admin Portal

- Build login page.
- Build dashboard.
- Add district-wise and category-wise charts/tables.
- Add filters and fine list view.

### Milestone 7: Android App

- Build fine lookup screen.
- Build payment screen.
- Build confirmation screen.
- Connect app to backend API.

### Milestone 8: Testing and Final Submission

- Test all user flows end-to-end.
- Add screenshots to documentation.
- Add API documentation.
- Merge all work into main branch.
- Ensure every member has meaningful commits.

## Evaluation-Focused Checklist

Use this checklist before submission:

- Backend REST API runs successfully.
- Android app can pay a fine.
- Public web portal can pay a fine.
- Admin portal shows collection statistics.
- SMS is sent or logged after successful payment.
- JWT authentication works for admin portal.
- Database stores fines, payments, users, officers, and SMS logs.
- Duplicate payment is prevented.
- Code is organized and maintainable.
- Git commits show individual contribution.
- README explains setup and usage.
- Main branch contains the final working code.

## AI Implementation Prompt

Use this prompt when asking an AI coding assistant to help implement the project:

```text
You are a senior software engineer helping my group implement a university Software Architecture project.

Project:
Build a digital traffic fine payment system for the Sri Lanka Police Department. The system must include:
1. A separate backend REST API application.
2. An Android mobile app for on-the-spot traffic fine payment.
3. A single-page public web application for later online fine payment.
4. An admin web portal for monitoring traffic fine collections nationwide.

Core requirement:
When a traffic police officer issues a fine, the fine has a unique reference number and a fine category identifier. A driver can pay the fine through the Android app immediately or later through the public web portal. After successful payment, the system must send an SMS notification to the relevant traffic police officer so the driver can retrieve their license. Senior officials must be able to use an admin web portal to view fine collection statistics such as total collections, district-wise collections, and fine-category-wise breakdowns.

Preferred technologies:
- Backend: Java Spring Boot REST API
- Database: MySQL/PostgreSQL/H2 with Spring Data JPA
- Security: Spring Security with JWT tokens
- Web apps: React, Vue, or Angular
- Mobile app: Android Kotlin/Java or Flutter
- SMS: mock SMS service first, with optional real SMS provider integration

Please implement the system using a clean architecture:
- Controllers for API endpoints
- Services for business logic
- Repositories for database access
- Entities and DTOs separated
- JWT-based authentication and role-based access for admin APIs
- Mock payment service
- Mock SMS service that logs SMS messages and can later be replaced by a real provider

Required backend features:
- Admin login endpoint returning JWT token
- Fine lookup by reference number and category code
- Fine payment endpoint
- Duplicate payment prevention
- Payment record creation
- Fine status update from UNPAID to PAID
- SMS notification after payment
- Admin dashboard endpoint with total collection, paid/unpaid counts, district-wise totals, and category-wise totals

Required frontend features:
- Public payment web app: lookup fine, show fine details, submit payment, show result
- Admin web portal: login, dashboard, filters, tables/charts for collections
- Android app: lookup fine, pay fine, show confirmation

Important constraints:
- Keep applications separated.
- Use one Git repository with regular commits.
- Write clean, maintainable, extensible code.
- Add README setup instructions.
- Add seed data for testing.
- Use meaningful package/folder structure.
- Do not store card details or passwords in plain text.

Start by proposing the repository structure, database schema, API endpoint design, and implementation order. Then implement the backend first, followed by the web apps and Android app.
```

