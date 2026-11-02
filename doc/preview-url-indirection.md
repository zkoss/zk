# Preview URL indirection — refer to the preview server by name, not by address

Status: **adopted** (full adoption, implemented 2026-09-09)

## The problem

Moving the preview app from one port to another once produced a diff across about 90 contract
files in `doc/contracts/`. Every line was the same edit:

```diff
-preview: http://localhost:8080/cropper.zul
+preview: http://localhost:8081/cropper.zul
```

Nothing about any component's design changed. The churn happened because many tracked files each
restate the preview server's **address** when all they mean is "the preview server".

Three properties make this worse than ordinary duplication:

1. **No machine reads the value.** Nothing in `scripts/`, no `.js`/`.ts`/`.sh` file parses the
   `preview:` field. It is consumed only by humans and by LLM agents reading the contract. A bulk
   address edit buys no functional correctness.
2. **Restated values drift silently.** Some documents kept the old port after the move.
3. **It hides a real gotcha.** Chrome resolves `localhost` to IPv6 `::1` while the preview app
   binds IPv4 only. If every contract hardcodes `localhost`, every reader needs a standing
   instruction to rewrite it to `127.0.0.1`.

## What the field is actually worth

The `preview:` field is **not** redundant with the filename. Some contracts point somewhere
other than `<name>.zul`, and they carry navigation instructions a filename cannot:

```
preview: ${PREVIEW_URL}/bandbox.zul   (open bandbox popup)
preview: ${PREVIEW_URL}/textbox.zul   (passwordbox section on textbox preview)
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

`${PREVIEW_URL}` is defined once, in the Ports section of
`.claude/skills/marble-theme/reference/verification.md`. Defining it as `127.0.0.1` (not
`localhost`) removes the IPv6 gotcha at the source.

Why this spelling:

- **One name serves both readers.** `PREVIEW_URL` works as a shell environment variable for scripts
  *and* as a prose placeholder for agents. There is no second vocabulary to learn or keep in sync.
- **It matches existing precedent.** `scripts/render-iceblue-baseline.sh` already does
  `BASE_URL="${ICEBLUE_URL:-http://localhost:8082}"`.
- **It is self-announcing.** An agent reading `${PREVIEW_URL}/cropper.zul` knows a substitution is
  required and goes looking. A bare relative path (`/cropper.zul`) would be ambiguous against the
  repo-relative paths that fill these same documents.

## Where a literal address survives — and why

| File | Why it keeps a literal |
|------|------------------------|
| `zkthemelab/build.gradle` (`httpPort`) and `zkthemelab/start.sh` | Bind the port. The origin of the fact. |
| `zkthemelab/src/test/playwright/playwright.config.ts` | Machine consumer: `baseURL` is `process.env.PREVIEW_URL` with the default port as fallback. |
| `.claude/skills/marble-theme/reference/verification.md` | **Defines** `${PREVIEW_URL}` for every agent and session. |
| `zkthemelab/README.md` | Human getting-started page — a reader wants a clickable URL, not a placeholder. |

A future port move edits these few files, and none of them is a design document.

Both ends of the pair are overridable at run time, so a temporary port change needs no edit at
all: the server takes `./gradlew appRun -PhttpPort=<n>` and the browser side takes the
`PREVIEW_URL` environment variable. Edit the files above only when the new port is meant to be
permanent.

## Scope: two servers, two names

`${PREVIEW_URL}` covers the **Marble preview app only**. The **IceBlue** comparison app keeps its
own separate name, `ICEBLUE_URL` (see `scripts/render-iceblue-baseline.sh`, which already defaults
it).

They are two different servers, not one server at two addresses, so one shared name would be wrong
regardless of how many files it saved. A document that says "the preview server" means Marble, and
a baseline capture that says `ICEBLUE_URL` means the other app. Collapsing them would make every
such reference ambiguous.

The churn argument does not apply here either: IceBlue's address appears in only a few places, and
one of them is already an overridable shell default.

**Rule for a third server**: give it its own `<NAME>_URL`, define it where its readers will look,
and do not fold it into `${PREVIEW_URL}`.
