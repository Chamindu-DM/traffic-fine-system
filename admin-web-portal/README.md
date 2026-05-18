# Admin Web Portal — Traffic Fine Payment System

React single-page application for senior officials to monitor nationwide traffic fine collections.

## Tech Stack

- React 18 + Vite
- React Router
- Axios (API calls)
- Recharts (charts & graphs)
- CSS Modules / Vanilla CSS

## Features

- JWT-based admin login
- Dashboard: total collections, paid/unpaid/cancelled counts
- District-wise collection breakdown
- Fine-category-wise collection breakdown
- Filterable fine records table (by district, category, status, date range)

## Running

```bash
npm install
npm run dev
```

Runs on `http://localhost:5174`.

Set the backend URL in `.env`:

```
VITE_API_BASE_URL=http://localhost:8080/api
```
