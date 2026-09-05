# Frontend exercise spec (fast-follow — not part of today's backend work)

This is a separate session from the backend exercise (`../EXERCISE.md`). Don't start it until
the backend CRUD is done and green — you need real endpoints to build against.

## What's already there for you

- A Vite + React 19 + TypeScript app (`npm install && npm run dev`), proxying `/api` to
  `localhost:8080` in dev (see `vite.config.ts`) so you can call the backend with relative URLs.
- Tailwind v4 + DaisyUI wired up (`src/index.css`, `vite.config.ts`) — use DaisyUI component
  classes (`btn`, `modal`, `table`, `alert`, etc.) rather than hand-rolling styles.
- `src/api/types.ts` — TypeScript types mirroring the backend DTOs. Keep these in sync if the
  Java shapes change.

Everything else — the API client, components, and state — is yours to build.

## Build this

**`src/api/client.ts`** — thin `fetch` wrapper functions: `getRoasts()`, `getRoast(id)`,
`createRoast(request)`, `updateRoast(id, request)`, `deleteRoast(id)`. Each should call the
matching `/api/v1/roasts...` endpoint, parse JSON, and throw on a non-2xx response (parse the
body as `ApiError` and throw something that carries its `message`/`details`).

**A roast list** (e.g. `src/components/RoastTable.tsx`) — fetch and render all roasts in a
DaisyUI `table`: name, origin, roast level, price, tasting notes, and edit/delete buttons per row.

**An add/edit form** (e.g. `src/components/RoastFormModal.tsx`) — a DaisyUI `modal` with a form
for `RoastRequest`'s fields (a `select` for `roastLevel`). Reused for both create and edit —
prefill it with the existing roast's values when editing. Show validation errors from the
`ApiError.details` the backend returns on a 400.

**Delete confirmation** — a simple `confirm()` is fine for this scope, or a DaisyUI modal if you
want the practice.

**Wire it up in `App.tsx`** — load the roast list on mount, open the modal for add/edit, refetch
(or optimistically update) after create/update/delete.

## Out of scope for this pass

No routing, no auth, no pagination — one page, one table. If the Ollama tasting-notes stretch
endpoint exists on the backend by the time you get here, add a "Generate" button per row as a
bonus, but the table above is the actual goal.
