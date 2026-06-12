# Public Payment Web Portal — Traffic Fine Payment System

React single-page application that allows drivers to look up and pay traffic fines online.

## Tech Stack

- React 18 + Vite
- Axios (API calls)
- Plain CSS

## Features

- Fine lookup by reference number and category code
- Conditional payment form for unpaid fines only
- Payment confirmation screen with payment reference
- Loading and error states for API calls

## Running

```bash
npm install
npm run dev
```

Runs on `http://localhost:5173`.

The Vite dev server proxies `/api` to `http://localhost:8080`, so the portal can call the Spring Boot backend without extra CORS setup during local development.

If you want to override the API base URL, set it in `.env`:

```
VITE_API_BASE_URL=/api
```
