# Public Payment Web Portal — Traffic Fine Payment System

React single-page application that allows drivers to look up and pay traffic fines online.

## Tech Stack

- React 18 + Vite
- React Router
- Axios (API calls)
- CSS Modules / Vanilla CSS

## Features

- Search fine by reference number and category code
- Display fine amount, district, officer, and status
- Payment form (mock card payment)
- Duplicate payment prevention
- Payment confirmation / failure screen

## Running

```bash
npm install
npm run dev
```

Runs on `http://localhost:5173`.

Set the backend URL in `.env`:

```
VITE_API_BASE_URL=http://localhost:8080/api
```
