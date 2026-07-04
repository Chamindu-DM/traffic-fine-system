# 🚦 Traffic Fine Payment System — Project Documentation

> **Sri Lanka Police Department — Digital Traffic Fine Collection Platform**
>
> This document explains the entire project in simple, everyday language. Whether you are a project evaluator, a team member, or someone with no programming background — this guide will help you understand what we built, why we built it, and how every piece works together.

---

## 📖 Table of Contents

1. [What Is This Project About?](#1-what-is-this-project-about)
2. [The Real-World Problem We Are Solving](#2-the-real-world-problem-we-are-solving)
3. [How The System Works — Step by Step](#3-how-the-system-works--step-by-step)
4. [The Four Applications We Built](#4-the-four-applications-we-built)
5. [Technology Choices — Why We Picked Each Tool](#5-technology-choices--why-we-picked-each-tool)
6. [The Backend (Server-Side Brain)](#6-the-backend-server-side-brain)
7. [The Microservices Architecture](#7-the-microservices-architecture)
8. [Security — How We Keep the System Safe](#8-security--how-we-keep-the-system-safe)
9. [The Database — Where Information Is Stored](#9-the-database--where-information-is-stored)
10. [SMS Notification — Telling Officers the Fine Is Paid](#10-sms-notification--telling-officers-the-fine-is-paid)
11. [Infrastructure & Deployment — Running Everything Together](#11-infrastructure--deployment--running-everything-together)
12. [Glossary — Technical Terms in Plain English](#12-glossary--technical-terms-in-plain-english)

---

## 1. What Is This Project About?

Imagine you are driving in Sri Lanka and a traffic police officer stops you for a violation. Today, settling that fine involves visiting a post office or a police station, standing in line, and going through lots of paperwork. It is slow, inconvenient, and frustrating for both drivers and officers.

**Our project replaces that entire process with a digital solution.** We built:

| What | For Whom | How |
|------|----------|-----|
| **A mobile phone app** | Drivers stopped on the road | Pay the fine right there on the spot |
| **A website for payments** | Drivers who want to pay later from home | Enter the fine reference number and pay online |
| **An admin website** | Senior police officials | See reports and statistics on fine collections across Sri Lanka |
| **An SMS notification** | Traffic police officers on duty | Receive a text message confirming that the driver has paid, so the license can be returned |

---

## 2. The Real-World Problem We Are Solving

### The Current Situation (Before Our System)

Think of this everyday scenario:

> **Officer Perera** stops a van on Galle Road for running a red light. He writes a paper ticket and takes the driver's license. The driver, **Mr. Silva**, now has to visit a post office or police station within 14 days, wait in a queue, pay the fine, get a receipt, then go back to the police station to collect his license.

**Problems with this approach:**
- 🕐 **Time-consuming** — The driver loses half a day just to pay a fine
- 📝 **Paper-heavy** — Tickets can get lost, damaged, or misread
- 📊 **No real-time tracking** — Senior officials have no way to see how many fines are collected across the country in real time
- 💰 **Revenue leakage** — Some fines are never paid because of the inconvenient process

### The New Situation (With Our System)

> **Officer Perera** stops the same van. He issues a digital fine with a unique reference number (e.g., `TF-2026-001234`) and a category code (e.g., `RED_LIGHT`). **Mr. Silva** opens our mobile app on his phone, enters the reference number and category code, sees the fine details and amount (Rs. 3,000), and pays using his debit card — all in under 2 minutes. **Officer Perera** immediately receives an SMS: *"Payment confirmed for fine TF-2026-001234. Driver license can be released."* He hands back the license, and both go on their way.
>
> If Mr. Silva cannot pay on the spot, he can go home and pay later through our website using the same reference number.
>
> Meanwhile, **SSP Jayawardena** at Police Headquarters logs into the admin portal and sees that the Colombo District collected Rs. 2.4 million in fines this month, with "Speeding" being the top violation category.

---

## 3. How The System Works — Step by Step

Here is the complete flow of how a traffic fine goes from being issued to being paid:

```
┌─────────────────────────────────────────────────────────────────────┐
│                     TRAFFIC FINE LIFECYCLE                          │
│                                                                     │
│  Step 1: OFFICER ISSUES FINE                                        │
│  ┌──────────────────────┐                                           │
│  │ Officer creates fine │ ──→  System generates reference number    │
│  │ via Admin Portal     │      (e.g., TF-2026-001234)              │
│  └──────────────────────┘                                           │
│                                                                     │
│  Step 2: DRIVER LOOKS UP THE FINE                                   │
│  ┌──────────────────────────────────────┐                           │
│  │ Driver enters reference number       │                           │
│  │ + category code on the App or Website│ ──→ System shows details  │
│  └──────────────────────────────────────┘      (amount, violation)  │
│                                                                     │
│  Step 3: DRIVER PAYS                                                │
│  ┌──────────────────────────┐                                       │
│  │ Driver enters card info  │ ──→ Payment is processed              │
│  │ and confirms payment     │     Fine status changes to PAID       │
│  └──────────────────────────┘                                       │
│                                                                     │
│  Step 4: OFFICER IS NOTIFIED                                        │
│  ┌──────────────────────────┐                                       │
│  │ System sends SMS to the  │ ──→ Officer releases the              │
│  │ officer who issued fine  │     driver's license                  │
│  └──────────────────────────┘                                       │
│                                                                     │
│  Step 5: ADMIN MONITORS                                             │
│  ┌──────────────────────────┐                                       │
│  │ Senior official views    │ ──→ District-wise reports,            │
│  │ the admin dashboard      │     category breakdowns, totals       │
│  └──────────────────────────┘                                       │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 4. The Four Applications We Built

Our project is not one single application — it is **four separate applications** that all talk to the same backend server. Think of it like a restaurant: there is one kitchen (the backend), but customers can order from the dining room, through a drive-through, or via a delivery app.

### 4.1 📱 Android Mobile App (On-the-Spot Payment)

**What it does:** Lets drivers pay their traffic fine immediately from their phone at the roadside.

**Real-world scenario:**
> A driver is stopped in Kandy. The officer hands them a fine sheet. The driver opens our app, types in the reference number and category code, sees that the fine is Rs. 2,500 for "Overtaking on a Bend", enters their card details, and pays. Done in 90 seconds.

**Key screens:**
1. **Fine Lookup Screen** — Enter reference number and category code
2. **Payment Screen** — View fine details and enter card information
3. **Confirmation Screen** — See payment success and a digital receipt

**Why Android?** Over 80% of smartphone users in Sri Lanka use Android phones, making it the most practical choice to reach the widest audience.

### 4.2 🌐 Public Payment Website (Pay Later from Home)

**What it does:** A single-page website where drivers can pay their fines from any computer or phone browser.

**Real-world scenario:**
> Mr. Silva forgot to pay his fine on the spot. Three days later, he sits at home, opens our website on his laptop, enters the reference number from his fine sheet, sees the violation details, and pays with his bank card. He then receives confirmation on screen.

**Key pages:**
1. **Payment Portal** — Search for a fine, view details, and make payment
2. **Payment Success** — Confirmation page after successful payment
3. **Payment Cancel** — Page shown if the user cancels midway

**Why a separate website?** Not everyone carries the Android app. A website works on any device with a browser — phones, tablets, laptops, or desktop computers.

### 4.3 🛡️ Admin Web Portal (For Senior Officials)

**What it does:** A secure dashboard for police department officials to monitor fine collections across Sri Lanka.

**Real-world scenario:**
> SSP Jayawardena logs in with his admin username and password. He sees a dashboard with charts showing: total fines collected this month (Rs. 45 million), the Colombo district leading with Rs. 12 million, and "Speeding" being the most common violation. He clicks on a specific district to see individual fines. He can also create new fines and manage the system.

**Key pages:**
1. **Login Page** — Secure admin-only login with username and password
2. **Dashboard** — Charts, statistics, district-wise breakdowns, and category analysis
3. **Create Fine** — Form to issue new traffic fines into the system

**Why a separate admin portal?** Security and separation of concerns. Public users should never see admin controls, and admin users need a specialised interface with charts and management tools that would be irrelevant for a driver paying a fine.

### 4.4 📩 SMS Notification System

**What it does:** Automatically sends a text message to the traffic officer when a fine they issued gets paid.

**Real-world scenario:**
> Officer Perera issued a fine at 10:30 AM. At 10:45 AM, the driver pays via the mobile app. Officer Perera's phone buzzes with an SMS: *"Payment confirmed for fine TF-2026-001234. Driver license can be released."*

**How it works:** The system supports two SMS providers:
- **Notify.lk** — A real Sri Lankan SMS gateway service (for production use)
- **Mock SMS** — A simulated SMS for development and testing (logs the message to the database instead of sending a real text)

If the real SMS provider fails, the system automatically falls back to the mock provider so that no notification is lost.

---

## 5. Technology Choices — Why We Picked Each Tool

Every technology we used was chosen for a specific reason. Let us explain each one with real-world analogies.

### 5.1 Backend — Java with Spring Boot

| Aspect | Detail |
|--------|--------|
| **What is it?** | Java is a programming language. Spring Boot is a framework (a toolkit) built on top of Java that makes it faster to build web servers. |
| **Analogy** | If building a house, Java is the raw bricks and cement, while Spring Boot is a pre-fabricated building kit — it gives you ready-made walls, plumbing, and wiring so you can build faster. |

**Why we chose it:**
- **Scenario:** Our system needs to handle thousands of payment requests daily across all districts in Sri Lanka. Java is known for handling high volumes of traffic reliably — it is used by banks, airlines, and government systems worldwide.
- Spring Boot comes with built-in support for databases, security, and web APIs — we do not have to build everything from scratch.
- It is the **preferred technology** mentioned in the project requirements.

### 5.2 Database — MySQL with JPA (Java Persistence API)

| Aspect | Detail |
|--------|--------|
| **What is MySQL?** | A database management system — think of it as a giant, organised digital filing cabinet that stores all our data (fines, payments, officers, categories). |
| **What is JPA?** | A tool that lets our Java code talk to the database using simple Java objects instead of complex database language (SQL). |
| **Analogy** | MySQL is the filing cabinet. JPA is the office assistant who files and retrieves documents for you — you just say *"get me the fine for reference TF-2026-001234"* and the assistant does the rest. |

**Why we chose it:**
- **Scenario:** When Officer Perera creates a fine, we need to save it safely so it can be looked up minutes, days, or months later. When Mr. Silva pays, we need to update the fine status from "UNPAID" to "PAID" without losing any data. MySQL is specifically designed for this kind of reliable data storage.
- JPA was required by the project specification and makes database code much cleaner and more maintainable.

### 5.3 Frontend — React with Vite

| Aspect | Detail |
|--------|--------|
| **What is React?** | A popular toolkit for building interactive website interfaces. It was created by Facebook and is used by Instagram, Netflix, Airbnb, and thousands of other sites. |
| **What is Vite?** | A development tool that makes building and testing React websites extremely fast. |
| **Analogy** | React is like LEGO blocks for websites — you build small, reusable pieces (a button, a form, a chart) and snap them together to make a full page. Vite is the fast conveyor belt that assembles those pieces instantly during development. |

**Why we chose it:**
- **Scenario:** The admin dashboard needs interactive charts that update in real time. The payment portal needs a smooth, single-page experience where the user does not have to wait for full page reloads. React makes all of this seamless.
- React is used by both the admin portal and the public payment website, so the team only needed to learn one frontend technology.

### 5.4 Styling — Tailwind CSS

| Aspect | Detail |
|--------|--------|
| **What is it?** | A CSS framework that provides ready-made design classes so you can style elements directly in the code without writing separate design files. |
| **Analogy** | Instead of painting a wall by mixing raw paint colors yourself, Tailwind gives you a box of 500 pre-mixed, perfectly labeled paint colors. You just pick `"sky-blue"` or `"rounded-large"` and apply it. |

**Why we chose it:**
- **Scenario:** We needed both portals to look professional and modern — clean cards, rounded buttons, responsive layouts that work on mobile and desktop. Tailwind lets us achieve this quickly without spending weeks writing custom CSS styles from scratch.

### 5.5 Mobile App — Android with Kotlin & Jetpack Compose

| Aspect | Detail |
|--------|--------|
| **What is Kotlin?** | A modern programming language officially supported by Google for building Android apps. |
| **What is Jetpack Compose?** | A modern toolkit for building Android app interfaces using a declarative approach — you describe what the screen should look like, and the toolkit handles the rest. |
| **Analogy** | Kotlin is the language you speak to give instructions. Jetpack Compose is the smart builder that creates beautiful phone screens based on your instructions — instead of manually placing every button pixel by pixel, you say *"put a card here with a title and a pay button"* and Compose does the layout. |

**Why we chose it:**
- **Scenario:** The on-the-spot payment app needs to be fast, responsive, and visually clear so a driver standing on the road can pay in under 2 minutes. Jetpack Compose creates smooth, modern interfaces quickly and is the official recommended way to build Android apps today.

### 5.6 HTTP Communication — Retrofit (Android) & Axios/Fetch (Web)

| Aspect | Detail |
|--------|--------|
| **What are these?** | Tools that allow our apps (mobile and web) to send requests to and receive responses from the backend server over the internet. |
| **Analogy** | Think of them as the postal service between our apps and the server. When the mobile app wants to look up a fine, Retrofit "mails" the request to the server and "delivers" the response back to the app. |

**Why we chose them:**
- **Scenario:** When Mr. Silva enters a fine reference number in the app, the app needs to ask the server *"Do you have a fine with this number?"* and display the result. Retrofit (for Android) and Fetch/Axios (for web) handle this communication reliably.

### 5.7 Security — JWT (JSON Web Tokens) with Spring Security

| Aspect | Detail |
|--------|--------|
| **What is JWT?** | A small, encrypted "digital badge" that proves who you are. When an admin logs in, the server gives them a JWT. Every time the admin makes a request, they show this badge to prove their identity. |
| **What is Spring Security?** | A powerful security framework for Java applications that handles login, password encryption, and access control. |
| **Analogy** | JWT is like a government-issued ID card. When you enter a secure building (the admin portal), the guard (Spring Security) checks your ID card. If it is valid and you have the right clearance level (ADMIN role), you are allowed in. If not, you are turned away. |

**Why we chose it:**
- **Scenario:** Only SSP Jayawardena and other authorised officials should be able to view fine collection reports. A random person should not be able to access admin data. JWT ensures that after logging in once, the admin does not need to enter their password on every page — the token handles it silently and securely.
- Passwords are never stored in plain text — they are encrypted using BCrypt, which scrambles the password so even database administrators cannot read it.

### 5.8 Charting — Recharts

| Aspect | Detail |
|--------|--------|
| **What is it?** | A React library that creates beautiful, interactive charts (bar charts, pie charts, line graphs). |
| **Analogy** | Instead of manually drawing graphs on paper, Recharts is a smart tool that takes raw numbers and instantly turns them into visual charts you can click and hover over. |

**Why we chose it:**
- **Scenario:** The admin dashboard needs to show district-wise fine collections as bar charts, violation category breakdowns as pie charts, and monthly trends as line graphs. Recharts integrates perfectly with our React-based admin portal.

---

## 6. The Backend (Server-Side Brain)

The backend is the heart of the entire system. All four applications (mobile app, payment website, admin portal, and SMS system) communicate with this single backend.

### What Does the Backend Do?

Think of the backend as the **central command center** of a police radio network:

1. **Receives requests** — "Look up fine TF-2026-001234", "Process payment of Rs. 3,000", "Show me all fines in Colombo district"
2. **Processes business logic** — Validates the fine exists, checks it has not already been paid, calculates amounts
3. **Stores and retrieves data** — Saves new fines, updates payment statuses, fetches reports
4. **Sends notifications** — Triggers SMS messages to officers
5. **Enforces security** — Ensures only admins can access admin endpoints

### Backend REST API — How Apps Talk to the Server

REST API is a standard way for applications to communicate with a server over the internet. Think of it as a restaurant menu:

| API Endpoint (Menu Item) | What It Does | Who Can Use It |
|--------------------------|-------------|----------------|
| `POST /api/fines` | Create a new traffic fine | Anyone (officers via admin portal) |
| `GET /api/fines/lookup?ref=TF-2026-001234&category=RED_LIGHT` | Look up a fine by reference number | Anyone (drivers via app or website) |
| `POST /api/payments` | Process a fine payment | Anyone (drivers) |
| `POST /api/payments/initiate` | Start a payment session | Anyone (drivers) |
| `POST /api/auth/login` | Admin login | Admin users only |
| `GET /api/admin/dashboard` | Get dashboard statistics | Admin only (requires JWT token) |
| `GET /api/admin/fines` | Search and filter all fines | Admin only (requires JWT token) |
| `GET /api/admin/categories` | List all fine categories | Admin only (requires JWT token) |

### Key Data Stored in the System

| Data Entity | What It Represents | Key Information |
|-------------|-------------------|-----------------|
| **TrafficFine** | A single traffic violation | Reference number, violation category, officer, driver license, vehicle number, district, amount, status (UNPAID/PAID), issue date |
| **FineCategory** | A type of traffic violation | Code (e.g., `SPEEDING`), name, fine amount (e.g., Rs. 3,000) |
| **Officer** | A traffic police officer | Name, badge number, phone number, district |
| **Payment** | A completed payment | Associated fine, card details (masked), amount paid, payment date |
| **SmsLog** | A record of each SMS sent | Officer, phone number, message, send status (SUCCESS/FAILED), timestamp |
| **User** | An admin user account | Username, encrypted password, role (ADMIN) |

### Preventing Duplicate Payments

**Scenario:** Mr. Silva pays his fine on the mobile app. His wife, not knowing this, tries to pay the same fine on the website 10 minutes later.

**Solution:** The system checks the fine status before processing any payment. If a fine is already marked as `PAID`, the system rejects the second payment attempt with a clear error message: *"This fine has already been paid."* This prevents any driver from being charged twice.

---

## 7. The Microservices Architecture

In addition to a standard backend, this project also includes a **microservices architecture** — a more advanced, production-grade version of the backend. Let us explain this concept simply.

### What Is Monolithic vs. Microservices?

**Monolithic (our `backend-api/` folder):**
> Imagine a small family restaurant where one person takes orders, cooks the food, serves the table, and handles billing. It works fine for a small restaurant, but as the restaurant grows, that one person becomes overwhelmed.

**Microservices (our `microservices/` folder):**
> Now imagine a large hotel kitchen. There is a separate station for appetisers, a separate station for main courses, a separate station for desserts, a separate cashier, and a manager coordinating everything. Each station works independently and can be scaled up during busy hours.

### Our Microservices — What Each One Does

We broke the single backend into **6 specialised mini-servers**, plus **3 infrastructure services**:

#### Business Microservices

| Service | Port | What It Does | Restaurant Analogy |
|---------|------|-------------|-------------------|
| **Auth Service** | 8081 | Handles admin login and JWT token generation | The reception desk that checks your reservation |
| **Fine Service** | 8082 | Creates, stores, and looks up traffic fines | The order-taking station |
| **Payment Service** | 8083 | Processes payments and prevents duplicates | The cashier / billing counter |
| **Notification Service** | 8084 | Sends SMS notifications to officers | The waiter who brings your receipt |
| **Reporting Service** | 8085 | Generates dashboard data and statistics | The manager reviewing daily sales reports |

#### Infrastructure Microservices

| Service | Port | What It Does | Restaurant Analogy |
|---------|------|-------------|-------------------|
| **API Gateway** | 8080 | The single front door — all requests come here first, and it routes them to the right service | The host who greets you at the door and guides you to the right table |
| **Service Registry (Eureka)** | 8761 | Keeps a phone book of all running services so they can find each other | The hotel directory that tells you where each department is located |
| **Config Server** | 8888 | Stores shared settings in one place so all services use the same configuration | The hotel's central policy manual that all staff follow |

### Why Microservices?

**Scenario:** During a national holiday weekend, fine payments spike dramatically. With microservices, we can run **3 copies of the Payment Service** to handle the load while keeping only **1 copy of the Reporting Service** (which is less busy). In a monolithic system, we would have to duplicate the entire application, wasting resources.

### How They Communicate — RabbitMQ (Message Queue)

| Aspect | Detail |
|--------|--------|
| **What is RabbitMQ?** | A message broker — a central post office that services use to send messages to each other. |
| **Analogy** | Instead of calling each department directly (which might fail if they are busy), you drop a letter in the mailbox (RabbitMQ). The receiving department picks it up when they are ready. Even if the Notification Service is temporarily down, the message waits safely in the queue until it comes back up. |

**Scenario:** When the Payment Service confirms a payment, instead of directly calling the Notification Service (which could fail or be slow), it drops a message in RabbitMQ saying *"Fine TF-2026-001234 has been paid."* The Notification Service picks up this message and sends the SMS. If the SMS service is down for maintenance, the message waits in the queue — no data is lost.

### Redis — The Speed Booster

| Aspect | Detail |
|--------|--------|
| **What is Redis?** | An in-memory data cache — an extremely fast temporary storage. |
| **Analogy** | The database is a filing cabinet in a back room — reliable but slow to access. Redis is the notepad on your desk — instant access. We copy frequently needed data to the notepad so we do not have to walk to the filing cabinet every time. |

**How we use it:**
- **Fine lookups** — When a fine is looked up once, the result is cached in Redis. If the same fine is looked up again (e.g., by both the app and website), Redis serves it instantly without querying the database again.
- **Dashboard data** — Admin dashboard statistics are cached so repeated loads are blazing fast.
- **Duplicate payment prevention** — Redis tracks ongoing payment processing to prevent race conditions (two payments for the same fine submitted at the exact same time).

---

## 8. Security — How We Keep the System Safe

Security is critical because we handle financial transactions and personal data.

### Authentication Flow (How Admin Login Works)

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│  Admin Types  │       │   Backend    │       │    Admin     │
│  Username &   │──────→│  Verifies    │──────→│  Receives    │
│  Password     │       │  Credentials │       │  JWT Token   │
└──────────────┘       └──────────────┘       └──────────────┘
                                                     │
                        ┌──────────────┐              │
                        │ Every Future │◄─────────────┘
                        │ Request Sends│  Token is sent with
                        │ JWT Token    │  every request as proof
                        └──────────────┘
```

**In simple terms:**
1. Admin enters username (`admin`) and password (`admin123`)
2. The server checks if this is valid
3. If valid, the server creates a JWT token (a long encrypted string) and sends it back
4. The admin portal stores this token in the browser
5. For every future request (loading dashboard, viewing fines), the token is sent along as proof of identity
6. If the token is missing, expired, or tampered with, the request is rejected

### Password Security

Passwords are **never stored as plain text**. We use **BCrypt** encryption:
- When `admin123` is stored, it becomes something like `$2a$10$N9qo8uLOickgx2ZMRZoMyeIj...` — completely unreadable
- Even if someone steals the database, they cannot determine the actual password
- Think of it like a one-way meat grinder — you can turn meat into mince, but you cannot turn mince back into a steak

### Public vs. Protected Endpoints

| Endpoint Type | Example | Protection |
|--------------|---------|------------|
| **Public** (no login needed) | Fine lookup, payment, fine creation | Open to everyone — because drivers need to pay without creating accounts |
| **Admin-only** (login required) | Dashboard, reports, fine search | Requires a valid JWT token with ADMIN role |
| **API Documentation** | Swagger UI | Open for development convenience |

---

## 9. The Database — Where Information Is Stored

### Monolithic Backend Database

The standard backend uses an **H2 in-memory database** for development — this is a lightweight database that runs inside the application itself. It is fast and requires zero setup, but data is lost when the server restarts. For production, this would be swapped with MySQL.

### Microservices Databases

In the microservices version, each service gets its **own separate MySQL database**:

| Database | Used By | What It Stores |
|----------|---------|----------------|
| `auth_db` | Auth Service | Admin user accounts, login credentials |
| `fines_db` | Fine Service | Traffic fines, violation categories, officers |
| `payments_db` | Payment Service | Payment transaction records |
| `notifications_db` | Notification Service | SMS logs, delivery status |
| `reporting_db` | Reporting Service | Aggregated statistics for dashboards |

**Why separate databases?** This follows the microservices principle of **database per service**. Each service owns its data independently. If the Payment Service's database has a problem, the Fine Lookup service continues working normally — just like how a problem in the hotel's billing department does not stop the kitchen from cooking.

---

## 10. SMS Notification — Telling Officers the Fine Is Paid

### How It Works

1. Driver pays the fine (via app or website)
2. Backend marks the fine as `PAID`
3. Backend looks up which officer issued the fine
4. Backend sends an SMS to the officer's phone number:
   > *"Payment confirmed for fine TF-2026-001234. Driver license can be released."*
5. The SMS attempt is logged in the database (success or failure)

### Two SMS Providers

| Provider | When Used | How It Works |
|----------|-----------|-------------|
| **Notify.lk** | Production (real deployment) | Connects to Notify.lk — a real Sri Lankan SMS gateway that sends actual text messages |
| **Mock SMS** | Development and testing | Instead of sending a real text, it writes the message to the database and prints it on screen. This avoids SMS costs during development. |

### Fallback Mechanism

**Scenario:** The real SMS provider (Notify.lk) is temporarily down due to maintenance.

**Solution:** The system automatically switches to the mock SMS provider and logs the message. This ensures that the payment confirmation is always recorded, even if the text message cannot be delivered immediately.

---

## 11. Infrastructure & Deployment — Running Everything Together

### Docker — Packaging Applications Like Shipping Containers

| Aspect | Detail |
|--------|--------|
| **What is Docker?** | A tool that packages each application and everything it needs (the programming language, libraries, settings) into a standardised "container" that runs identically on any computer. |
| **Analogy** | Think of shipping containers at a port. Whether you are shipping electronics or clothing, everything goes into the same standardised container. The ship (computer) does not care what is inside — it just runs the containers. |

**Why we use it:**
- **Scenario:** Your colleague has a Mac, you have Windows, and the server runs Linux. Without Docker, each person would have to install Java 17, MySQL 8, Redis, RabbitMQ, and configure them all manually. With Docker, everyone runs one command (`docker-compose up`) and everything starts automatically, configured identically.

### Docker Compose — Orchestrating Multiple Containers

Our system has **12+ services** (MySQL, Redis, RabbitMQ, 6 microservices, 2 web portals, API Gateway, etc.). Docker Compose lets us define and run all of them with a single command.

```
docker-compose up --build -d
```

This one command:
1. Downloads MySQL, Redis, and RabbitMQ images
2. Creates the 5 databases
3. Starts all 6 microservices in the correct order
4. Starts the 2 web portals
5. Sets up networking so all services can communicate
6. Waits for dependencies (e.g., does not start the Fine Service until MySQL is ready)

### Nginx — The Web Server for Portals

| Aspect | Detail |
|--------|--------|
| **What is Nginx?** | A high-performance web server that serves our website files (HTML, CSS, JavaScript) to browsers. |
| **Analogy** | React builds the website pages. Nginx is the delivery truck that brings those pages to the user's browser when they visit the URL. |

Both the admin portal and the public payment website use Nginx inside their Docker containers to serve the built React application.

### Port Mappings — How to Access Each Application

Think of ports as different doors to the same building:

| Port | Application | URL |
|------|------------|-----|
| 8080 | API Gateway (main backend entrance) | `http://localhost:8080` |
| 8761 | Service Registry (Eureka Dashboard) | `http://localhost:8761` |
| 8888 | Config Server | `http://localhost:8888` |
| 5173 | Public Payment Website | `http://localhost:5173` |
| 5174 | Admin Web Portal | `http://localhost:5174` |
| 3307 | MySQL Database | (Not a web page — accessed by applications internally) |
| 6379 | Redis Cache | (Internal use only) |
| 15672 | RabbitMQ Management Console | `http://localhost:15672` |

---

## 12. Glossary — Technical Terms in Plain English

| Term | Plain English Meaning |
|------|----------------------|
| **API (Application Programming Interface)** | A set of rules that allows one piece of software to talk to another. Like a restaurant menu — you (the app) choose from the menu (API), and the kitchen (server) prepares what you ordered. |
| **REST API** | A specific style of API that uses web addresses (URLs) and standard actions (GET = read, POST = create). The most popular way to build web APIs. |
| **JWT (JSON Web Token)** | A digital identity card. After logging in, the server gives you a token that proves who you are for subsequent requests. |
| **Frontend** | The part of the application that users see and interact with — buttons, forms, pages, charts. |
| **Backend** | The part of the application that runs on the server — business logic, database access, security, processing. Users never see this directly. |
| **Database** | An organised digital storage system for data. Like a well-organised filing cabinet that can instantly find any document. |
| **JPA (Java Persistence API)** | A tool that translates between Java objects and database tables, so developers do not need to write raw database queries. |
| **Microservices** | An architectural pattern where a large application is split into many small, independent services that communicate over a network. |
| **Monolith** | The opposite of microservices — one big application that handles everything. Simpler but harder to scale. |
| **Docker** | A tool that packages applications and their dependencies into standardised containers that run anywhere. |
| **Docker Compose** | A tool for defining and running multiple Docker containers together with one command. |
| **Redis** | An ultra-fast temporary storage system that keeps frequently accessed data in memory (RAM) for instant retrieval. |
| **RabbitMQ** | A message broker that allows different services to communicate by sending messages through a central queue. |
| **Eureka** | A Netflix-created service that acts like a phone book for microservices — each service registers itself so others can find it. |
| **API Gateway** | A single entry point for all client requests. It routes requests to the correct microservice, like a hotel reception desk. |
| **Config Server** | A centralised service that stores configuration settings for all microservices in one place. |
| **Nginx** | A high-performance web server used to serve website files to browsers. |
| **Vite** | A fast development tool for building modern web applications with React. |
| **React** | A popular JavaScript library for building interactive user interfaces. Created by Facebook. |
| **Kotlin** | A modern programming language for Android development, officially supported by Google. |
| **Jetpack Compose** | Google's modern toolkit for building native Android UI with a declarative approach. |
| **Tailwind CSS** | A utility-first CSS framework that provides pre-built design classes for rapid UI development. |
| **BCrypt** | A password hashing algorithm that encrypts passwords in a way that is extremely difficult to reverse. |
| **CORS** | Cross-Origin Resource Sharing — a security feature that allows our websites (running on port 5173/5174) to communicate with the backend (running on port 8080). |
| **Swagger UI** | An automatic, interactive documentation page for the API where developers can test endpoints directly in the browser. |
| **Spring Boot** | A Java framework that simplifies building production-ready web applications and services. |
| **Spring Security** | A security framework for Java applications that handles authentication (who are you?) and authorization (what are you allowed to do?). |
| **Spring Cloud** | A collection of tools for building microservice architectures — includes Eureka, Config Server, API Gateway, and more. |
| **OpenFeign** | A tool that lets one microservice call another microservice's API as easily as calling a local function. |
| **CQRS (Command Query Responsibility Segregation)** | A pattern where reading data and writing data are handled by different systems, optimising each for its specific task. |
| **Webhook** | A mechanism where one system sends an automatic notification to another system when something happens (e.g., payment gateway notifying our backend about a payment). |
| **Idempotency** | Ensuring that performing the same operation twice has the same result as performing it once — critical for payment systems to prevent double charges. |
| **Rate Limiting** | Controlling how many requests a user can make in a given time period to prevent abuse or overload. |

---

> **Document Version:** 1.0  
> **Last Updated:** July 2026  
> **Project:** University Software Architecture Group Assignment  
> **Department:** Sri Lanka Police Department (Simulated)
