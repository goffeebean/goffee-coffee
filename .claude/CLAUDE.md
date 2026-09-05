# Goffeebean

A coffee-roast CRUD API (Spring Boot) with a React frontend, built as a one-day portfolio
project **and** a hands-on Java/Spring Boot training exercise for a junior engineer. Domain:
`Roast` (name, origin, roast level, price, tasting notes), plus a stretch feature that asks a
local Ollama model to generate tasting notes.

## This is a deliberate learning exercise — read before touching backend or frontend code

Large parts of this codebase are **intentionally unbuilt**. Do not implement the missing pieces
unless explicitly asked to — the point is for the junior engineer to write them.

- **Backend**: `EXERCISE.md` (repo root) is the spec. Only the entity (`Roast`, `RoastLevel`),
  `RoastRepository`, and the exception classes (`RoastNotFoundException`,
  `OllamaUnavailableException`) exist. DTOs, the mapper, `GlobalExceptionHandler`, the service,
  the controller, and all tests are unbuilt on purpose.
- **Frontend**: `frontend/FRONTEND_EXERCISE.md` is the spec. Only tooling exists (Vite + React 19
  + TS, Tailwind v4 + DaisyUI, `src/api/types.ts`). The API client, components, and state are
  unbuilt on purpose, and this is explicitly a fast-follow — not the same session as the backend
  exercise.

If asked to review progress, prefer pointing out what's wrong/missing over silently fixing it —
that's the same principle as not writing the exercise code in the first place.

## Architecture

One deployable unit: Spring Boot serves the REST API under `/api/v1/roasts` **and** serves the
built React app as static resources (`src/main/resources/static`). There's no separate frontend
container — the root `Dockerfile` builds the React app first, then copies its `dist/` output into
Spring's static resources before packaging the jar. This was a deliberate choice (single Docker
Hub image, single container) over a two-container/nginx setup.

DTOs are mandatory at the controller boundary — never return the JPA entity directly.
`GlobalExceptionHandler` (`@RestControllerAdvice`) is the only place that builds error responses,
returning a single `ApiError` shape for both 404s and validation failures.

The Ollama integration is a plain HTTP call (`RestClient` to `/api/generate`) rather than a
dependency on Spring AI or another framework — deliberately minimal, and isolated so Ollama being
unreachable degrades to a clean 503, not a broken app.

## Commands

```
./mvnw clean verify          # backend build + test
./mvnw spring-boot:run       # run backend locally (expects Postgres on localhost:5332)

cd frontend && npm run dev   # frontend dev server (proxies /api to localhost:8080)
cd frontend && npm run build
cd frontend && npm run lint  # oxlint

docker compose up            # full stack: app (built from root Dockerfile) + Postgres + Ollama
```

`requests.http` has example calls for every endpoint.

## Spring Boot 4.1.1 gotchas (this version is very new — don't waste time rediscovering these)

- `@WebMvcTest` moved to `org.springframework.boot.webmvc.test.autoconfigure`.
- `TestRestTemplate` moved to `org.springframework.boot.resttestclient`.
- `@MockBean` was removed — use `@MockitoBean` (`org.springframework.test.context.bean.override.mockito.MockitoBean`) instead.
- The `@WebMvcTest` slice does not auto-provide an `ObjectMapper` bean — construct one directly in the test (`new ObjectMapper()`).

## CI/CD

`.github/workflows/ci.yml`: `backend` and `frontend` jobs build+test on every PR and push to
main (merge gate). A `publish` job runs only on push to `main` (i.e. after merge, never on a PR)
and pushes exactly one image, `<DOCKERHUB_USERNAME>/goffeebean`, tagged `latest` and the commit
SHA. Requires repo secrets `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN` (a Docker Hub access
token). No deployment step exists anywhere — publishing to Docker Hub is the end of the pipeline.

## Known stale/leftover content

- `README.md` still describes the original Amigoscode tutorial this project started from — not
  the actual current project. Needs a rewrite once the exercises above are done.
- `EXERCISE.md` and `frontend/FRONTEND_EXERCISE.md` are meant to be deleted once their respective
  exercises are complete and the real README is written.
