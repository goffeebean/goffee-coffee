# Exercise spec (delete this file once everything below is built and the real README is written)

## What's already there for you

- `entity/Roast.java`, `entity/RoastLevel.java` — the JPA entity and its enum.
- `repository/RoastRepository.java` — empty `JpaRepository<Roast, Long>`.
- `exception/RoastNotFoundException.java` — thrown when a lookup by id fails.
- `exception/OllamaUnavailableException.java` — for the stretch goal, see bottom.
- `pom.xml` — has `spring-boot-starter-validation`, `springdoc-openapi-starter-webmvc-ui`, and `h2` (test scope) already added.
- `src/test/resources/application-test.properties` — points a `test` Spring profile at an in-memory H2 database, so integration tests don't need Postgres running.
- `docker-compose.yml` — runs Postgres (and, for the stretch goal, Ollama) locally.

Everything else — DTOs, mapper, exception handler, controller, service, and all tests — is yours to build.

## Build this

**`dto/RoastRequest.java`** — a record a client sends to create/update a roast: `name`, `origin`, `roastLevel`, `price`, `tastingNotes`. Add Bean Validation annotations: `name`/`origin` required (`@NotBlank`), `roastLevel`/`price` required (`@NotNull`), `price` must not be negative (`@DecimalMin`).

**`dto/RoastResponse.java`** — a record returned to clients: `id`, `name`, `origin`, `roastLevel`, `price`, `tastingNotes`. Controllers should never return the `Roast` entity directly — always go through this.

**`dto/ApiError.java`** — a record for error responses: `timestamp`, `status` (int), `error` (string, e.g. `"Not Found"`), `message`, `details` (`List<String>`, for validation failures — empty otherwise).

**`mapper/RoastMapper.java`** — a `@Component` with methods to convert `RoastRequest` → `Roast` (new entity), `Roast` + `RoastRequest` → mutate an existing entity's fields (for updates), and `Roast` → `RoastResponse`.

**`exception/GlobalExceptionHandler.java`** — a `@RestControllerAdvice` with `@ExceptionHandler` methods:
- `RoastNotFoundException` → 404, `ApiError` with `error="Not Found"`.
- `MethodArgumentNotValidException` (thrown when `@Valid` fails) → 400, `ApiError` with `error="Bad Request"` and `details` populated from the field errors (`ex.getBindingResult().getFieldErrors()`).

**`service/RoastService.java`** — a `@Service` with:
- `findAll()` → all roasts, mapped to `RoastResponse`.
- `findById(Long id)` → one roast mapped to `RoastResponse`, or throw `RoastNotFoundException`.
- `create(RoastRequest request)` → map, save, return mapped response (must include the generated id).
- `update(Long id, RoastRequest request)` → fetch-or-throw, apply the new fields, save, return mapped response.
- `delete(Long id)` → throw if the id doesn't exist, otherwise delete it.

**`controller/RoastController.java`** — `@RestController` at `/api/v1/roasts`:
| Method | Path | Body | Success | Notes |
|---|---|---|---|---|
| GET | `/api/v1/roasts` | — | 200, list | |
| GET | `/api/v1/roasts/{id}` | — | 200 | 404 if missing |
| POST | `/api/v1/roasts` | `RoastRequest` (`@Valid`) | 201 | `Location` header = `/api/v1/roasts/{id}`; 400 on invalid body |
| PUT | `/api/v1/roasts/{id}` | `RoastRequest` (`@Valid`) | 200 | 404 if missing |
| DELETE | `/api/v1/roasts/{id}` | — | 204 | 404 if missing |

## Tests to write yourself

- **Service unit tests** (`RoastServiceTest`, JUnit 5 + Mockito): mock `RoastRepository`, use a real `RoastMapper`, cover each method above including the not-found paths.
- **Controller slice tests** (`RoastControllerTest`, `@WebMvcTest(RoastController.class)` + `MockMvc`): mock `RoastService` (`@MockitoBean` — this Spring Boot version removed `@MockBean`), cover status codes and the validation-failure case. Note: in this Boot version `@WebMvcTest`'s slice doesn't auto-provide an `ObjectMapper` bean — just `new ObjectMapper()` in the test.
- **One integration test** (`@SpringBootTest(webEnvironment = RANDOM_PORT)`, `@ActiveProfiles("test")`, real `TestRestTemplate`): exercise the full create → get → update → delete lifecycle over real HTTP against the H2 database.

Two Boot-4-specific import gotchas I hit (this version is very new and relocated some test classes): `@WebMvcTest` now lives in `org.springframework.boot.webmvc.test.autoconfigure`, and `TestRestTemplate` now lives in `org.springframework.boot.resttestclient`.

## Stretch goals (do these last, only if time allows — roughly in this order)

### 1. Tasting notes generator

An endpoint that asks a local Ollama model to write tasting notes for a roast:
- `service/OllamaTastingNoteService.java` — call `POST http://localhost:11434/api/generate` (configure the base URL and model name via `application.properties`, e.g. `ollama.base-url`, `ollama.model`) using Spring's `RestClient`, with a request body `{"model": ..., "prompt": ..., "stream": false}` and reading the `response` field back. Catch `RestClientException` and rethrow as `OllamaUnavailableException` (already written for you) so a missing/down Ollama server degrades to a clean 503 instead of breaking the app.
- `dto/TastingNoteResponse.java` — `{roastId, tastingNotes}`.
- Wire a `POST /api/v1/roasts/{id}/tasting-notes/generate` endpoint on the controller.
- `docker-compose.yml` already has an `ollama` service; run `docker compose up ollama` then `docker exec -it ollama ollama pull llama3.2:1b` (or whatever small model you configure) before testing this.

### 2. Roast waitlist ("request this roast, notify me when it's in stock")

Business framing for a non-technical stakeholder: the shop is opening soon, so a customer can ask
to be notified when a roast they want is in stock. There is **no real notification** — this is
just a row a staff member would look at and manually contact people from. Same CRUD pattern as
`Roast`, just narrower:

- `entity/RoastRequest.java` — `id`, `customerName`, `customerEmail`, a reference to the `Roast`
  they're interested in (either a `roastId` or a `@ManyToOne Roast`, your call), `requestedAt`
  (`Instant`, set on creation), `notified` (`boolean`, defaults `false`).
- `repository/RoastRequestRepository.java` — same shape as `RoastRepository`.
- `dto/RoastRequestRequest.java` / `dto/RoastRequestResponse.java` — same DTO-boundary rule as
  `Roast`: validate `customerName`/`customerEmail` (`@NotBlank`, `@Email`) and `roastId`
  (`@NotNull`) on the way in.
- `service/RoastRequestService.java` + `controller/RoastRequestController.java` at
  `/api/v1/roast-requests`:
  - `POST /api/v1/roast-requests` → 201, create a request (404 via `RoastNotFoundException` if
    `roastId` doesn't exist — reuse it, it's not `Roast`-specific in spirit even if the message
    says "Roast").
  - `GET /api/v1/roast-requests` → 200, list all (this is the staff-facing view).
  - `PATCH /api/v1/roast-requests/{id}/notify` → 200, flips `notified` to `true` (this is the
    "mark as contacted" action) — 404 if missing.
- Tests: same three layers as `Roast` (service unit tests, controller slice test, one integration
  test covering create → list → notify).

### 3. Blend recommendation chatbot (stateless — no conversation history)

A customer describes what they like in one message and gets back a recommendation of one of the
*actual* roasts in the database — not a freeform chat thread, no stored conversation state. This
builds directly on the tasting-notes pattern above:

- `service/RoastRecommendationService.java` — takes the customer's message plus the full current
  roast catalog (`RoastRepository.findAll()`, mapped to a plain-text summary of name/origin/roast
  level/tasting notes), builds a prompt instructing the model to recommend exactly one roast *from
  that list* and explain why, and calls Ollama the same way `OllamaTastingNoteService` does.
  Handle an empty catalog (nothing to recommend) as a 4xx, not a call to Ollama.
- `dto/RecommendationRequest.java` — `{message}` (`@NotBlank`).
- `dto/RecommendationResponse.java` — `{recommendedRoastId, recommendedRoastName, explanation}`.
  Getting the model to reliably return a matching id: either ask it to respond in a strict JSON
  shape and parse that, or ask for the roast *name* and look it up server-side — the latter is
  more forgiving of an imperfect model response.
- Wire a `POST /api/v1/recommendations` endpoint.
- Frontend (once you're at that exercise): a simple textarea + submit, not a chat UI — this is
  intentionally one question, one answer.
