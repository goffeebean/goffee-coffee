# Goffeebean

A coffee-roast CRUD app: Spring Boot 4.1.1 (Java 21) backend + React 19 frontend, packaged as a
single deployable jar.

## What it does

- Full CRUD over coffee roasts (`name`, `origin`, roast level, price, tasting notes) via
  `/api/v1/roasts`.
- Optional AI-generated tasting notes: `POST /api/v1/roasts/{id}/tasting-notes/generate` asks a
  locally-running Ollama model to write tasting notes for a roast, exposed as a "Generate" button
  per row in the UI.
- A single React page — a table of roasts with add/edit/delete, no routing or auth, by design.

## Architecture

One deployable unit: Spring Boot serves the REST API and the built React app (as static resources)
from the same jar. The root `Dockerfile` builds the React app first, then copies its `dist/` output
into Spring's static resources before packaging — no separate frontend container.

## Stack

- **Backend:** Spring Boot 4.1.1, Spring Data JPA, Bean Validation, Postgres.
- **Frontend:** Vite + React 19 + TypeScript, Tailwind v4 + DaisyUI.
- **Optional:** Ollama (local LLM server) for the tasting-notes generation feature — the rest of
  the app works fully without it; if Ollama is unreachable, the generate endpoint just returns a
  clean 503 instead of breaking anything.

## Running it locally

**Backend** (expects Postgres on `localhost:5332`):
```
./mvnw spring-boot:run
```

**Frontend** (dev server, proxies `/api` to `localhost:8080`):
```
cd frontend && npm run dev
```

**Full stack** (app + Postgres + Ollama):
```
docker compose up
```

Once running, interactive API docs are at `http://localhost:8080/swagger-ui.html`.

## Commands

```
./mvnw clean verify          # backend build + test
cd frontend && npm run dev   # frontend dev server
cd frontend && npm run build
cd frontend && npm run lint  # oxlint
```

`requests.http` has example calls for every endpoint.
