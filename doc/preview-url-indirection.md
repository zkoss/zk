# Preview URL indirection — refer to the preview server by name, not by address

Status: **adopted** · D29 = option A (full adoption), decided and implemented 2026-09-09

## The problem

Moving the preview app from port 8080 to 8081 produced a 90-file, 91-line diff across
`doc/contracts/`. Every line was the same edit:

```diff
-preview: http://localhost:8080/cropper.zul
+preview: http://localhost:8081/cropper.zul
```

Nothing about any component's design changed. The churn happened because ~118 tracked files each
restate the preview server's **address** when all they actually mean is "the preview server".

Three properties make this worse than ordinary duplication:

1. **No machine reads the value.** Nothing in `scripts/`, no `.js`/`.ts`/`.sh` file parses the
   `preview:` field. It is consumed only by humans and by LLM agents reading the contract. So the
   90-file diff bought exactly zero functional correctness — it was pure bookkeeping.
2. **The address is already wrong in places.** `tasks/multislider-knob-hit-target-analysis.md` and
   `doc/daterangebox-focus-ux-review.md` still say `8080`; the multislider doc even carries a note
   that "CLAUDE.md's localhost:8080 table is stale". Restated values drift silently.
3. **It hides a real gotcha.** `.claude/agents/zk-theme-evaluator.md` has to carry a standing
   instruction to rewrite `localhost` → `127.0.0.1` when reading a contract, because Chrome resolves
   `localhost` to IPv6 `::1` while the preview app binds IPv4 only. That rewrite rule exists purely
   because the contracts hardcode the wrong host.

## What the field is actually worth

The `preview:` field is **not** redundant with the filename — 19 of 94 contracts point somewhere
other than `<name>.zul`, and they carry navigation instructions a filename cannot:

```
preview: http://localhost:8081/bandbox.zul   (open bandbox popup)
preview: http://localhost:8081/textbox.zul   (passwordbox section on textbox preview)
preview: trigger validation in any input preview that uses Constraint (e.g., …/textbox.zul)
```

So deleting the field and deriving it from the filename is not an option. Only the **host and port
prefix** is duplicated; the path and the note are real content.

## The convention

Refer to the preview server by one name, `${PREVIEW_URL}`, everywhere except the few places that
must state a value.

```
preview: ${PREVIEW_URL}/cropper.zul
preview: ${PREVIEW_URL}/bandbox.zul   (open bandbox popup)
```

**`${PREVIEW_URL}` = `http://127.0.0.1:8081`** — defined once, in `CLAUDE.md`.

Why this spelling:

- **One name serves both readers.** `PREVIEW_URL` works as a shell environment variable for scripts
  *and* as a prose placeholder for agents. There is no second vocabulary to learn or keep in sync.
- **It matches existing precedent.** `scripts/render-iceblue-baseline.sh` already does
  `BASE_URL="${ICEBLUE_URL:-http://localhost:8082}"`.
- **It is self-announcing.** An agent reading `${PREVIEW_URL}/cropper.zul` knows a substitution is
  required and goes looking. A bare relative path (`/cropper.zul`) would be ambiguous against the
  repo-relative paths that fill these same documents.
- **It is safe with `check:doc-links`.** Verified empirically: that checker's path regex never
  matches `${PREVIEW_URL}/cropper.zul` at all, because the closing brace breaks the name/slash
  sequence it looks for. Where a longer path does surface a tail (`usecase/index.zul`), the leading
  segment is not one of its `REPO_DIRS` (`tasks`, `doc`, `src`, `scripts`, `target`, `.claude`) and
  it is skipped. No new exclusion is needed.
- **It fixes the IPv6 gotcha at the source.** Defining the name as `127.0.0.1` retires the
  "rewrite localhost to 127.0.0.1" instruction in `.claude/agents/zk-theme-evaluator.md`.

## Where a literal address survives — and why

Four files, each of which genuinely needs a value rather than a name:

| File | Why it keeps a literal |
|------|------------------------|
| `src/test/java/zk/example/ThemePreviewApp.java` | Binds the port (`server.port`). The origin of the fact. |
| `src/test/playwright/playwright.config.ts` | Machine consumer. Becomes `process.env.PREVIEW_URL ?? 'http://localhost:8081'`. |
| `CLAUDE.md` | **Defines** `${PREVIEW_URL}` for every agent and session. |
| `readme.md` | Human getting-started page — a reader wants a clickable URL, not a placeholder. |

A future port move edits 4 files instead of 118, and none of the 4 is a design document.

Both ends of the pair are already overridable at run time, so a temporary port change needs no edit
at all: the server takes `-Dserver.port=…` (`ThemePreviewApp.java` sets 8081 only as a default) and
the browser side takes the `PREVIEW_URL` environment variable. Edit the four files only when the new
port is meant to be permanent.

## What is deliberately NOT migrated

`tasks/harness/eval-reports/*.md` (5 files) keep their literal `8080` URLs. They are machine-written,
point-in-time records of runs that really did curl `8080`; CLAUDE.md marks the directory
"do not hand-edit". Rewriting them would falsify the record.

## Migration (completed 2026-09-09)

The 8080→8081 edit is currently **uncommitted** in the working tree. Applying this migration on top
of it collapses both into a single commit whose diff reads `8080` → `${PREVIEW_URL}` — no revert,
no destructive git operation, and no intermediate noise commit in history.

1. **Contracts (91 files)** — rewrite both the `preview:` field and inline prose mentions:
   ```bash
   sed -i '' 's|http://localhost:8081|${PREVIEW_URL}|g' doc/contracts/*.md
   ```
   → verify: `grep -rl "localhost:808" doc/contracts | wc -l` is `0`
2. **Agents and skills (9 files)** — same substitution across `.claude/agents/*.md` and
   `.claude/skills/**`, plus delete the now-dead `localhost`→`127.0.0.1` rewrite instruction in
   `zk-theme-evaluator.md`.
   → verify: the evaluator no longer mentions rewriting the host
3. **Prose docs (7 files)** — `doc/*.md` and `doc/spec/*.md`, including the two files still stuck on
   `8080`, which this pass corrects as a side effect.
   → verify: `grep -rl "localhost:808\|127.0.0.1:808" doc --exclude-dir=harness` is empty
4. **Playwright (1 file)** — `baseURL: process.env.PREVIEW_URL ?? 'http://localhost:8081'`; the 8
   spec-file comments become `${PREVIEW_URL}`.
   → verify: `npx playwright test --list` still resolves, and one spec run passes
5. **Define the name** — add a short "Preview server address" section to `CLAUDE.md` stating
   `${PREVIEW_URL}` = `http://127.0.0.1:8081`, that agents must substitute it, and that
   `ThemePreviewApp.java` is where the port is set. Update the CLAUDE.md URL table to use the name.
   → verify: `npm run check:doc-links` clean; a fresh agent asked for the cropper preview URL
     resolves it correctly

## Outcome

118 files now refer to the server by name. A literal address survives in exactly the four sanctioned
places. Verified 2026-09-09:

- `npm run check:doc-links` — clean.
- `npx playwright test --list` — 724 tests across 14 files still resolve.
- `render-smoke.spec.ts` against the live app — **115/115 passed**, proving `baseURL` is wired.
- Override honoured: `PREVIEW_URL=http://127.0.0.1:9090` resolves `baseURL` to that value.

Two pre-existing defects were corrected as a side effect: `tasks/multislider-knob-hit-target-analysis.md`
and `doc/daterangebox-focus-ux-review.md` were still on port 8080, and the former carried a note that
CLAUDE.md's table was stale. The now-dead "rewrite `localhost` to `127.0.0.1`" instruction in
`.claude/agents/zk-theme-evaluator.md` was removed; the IPv6 rationale it existed for is preserved at
the definition site instead.

## Scope: two servers, two names (decided)

`${PREVIEW_URL}` covers the **Marble preview app only**. The **iceblue** comparison app keeps its
own separate name, `ICEBLUE_URL` (see `scripts/render-iceblue-baseline.sh`, which already defaults
it). D30 = option A, decided 2026-09-09.

They are two different servers, not one server at two addresses, so one shared name would be wrong
regardless of how many files it saved — a document that says "the preview server" means Marble, and
a baseline capture that says `ICEBLUE_URL` means the other app. Collapsing them would make every
such reference ambiguous. Launching iceblue also needs more than an address (both
`-Dspring.profiles.active=iceblue` and `-Dorg.zkoss.theme.preferred=iceblue`, or port 8082 silently
serves Marble), which is a second reason its setup does not reduce to a URL.

The churn argument does not apply here either: iceblue's address appears in 3 places, not 118, and
one of them is already an overridable shell default.

**Rule for a third server**: give it its own `<NAME>_URL`, define it where its readers will look,
and do not fold it into `${PREVIEW_URL}`.
