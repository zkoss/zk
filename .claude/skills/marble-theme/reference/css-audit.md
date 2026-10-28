# CSS theme audit

This page was merged from the former `css-theme-audit` skill on 2026-09-10 and is Step 4 of maintaining Marble hygiene.

Use this page when the work is to review, audit, clean up, lint, or check the quality/hygiene of
theme CSS, tokens, or utility classes; before tagging a release; or when adopting this template to
build a new theme and asking "how do I run the same checks I did before".

A repeatable procedure for auditing a ZK theme's CSS the way `marble` was first
audited on 2026-06-15. This page is self-contained: the **method** (how to
produce an audit for any theme) plus an inline **worked example** (what good
output looks like — the A–F structure in Step 4, with the concrete `marble`
findings cited throughout: the goldenlayout 3-warning split, the
`--zk-elevation-resting` shadow extraction, the orphan-token buckets).

The audit deliberately mixes two kinds of work:

- **Mechanical checks** a script can decide on its own — run `.claude/skills/marble-theme/scripts/audit-css.sh`.
- **Human-judgment calls** a script must NOT make alone (is this orphan token safe
  to delete? does this bare color map to a semantic token?) — you triage these.

Conflating the two is the classic failure mode: a script that "auto-cleans"
orphans will delete a complete design-token scale or a public theme API. Keep the
split.

## When to run

- Before tagging a theme release, or after finishing a styling milestone.
- When forking this template into a new theme and you want the same hygiene bar.
- Any time the user asks to review CSS quality, find unused tokens, or hunt down
  hardcoded values.

## Prerequisites & the one per-theme parameter

```bash
npm install   # brings in stylelint (the mechanical core)
```

Everything else is standard `zkThemeTemplate` layout. The **one thing that varies
per theme** is the token prefix and where tokens/CSS live. Defaults assume:

| Parameter | Default | Flag to override |
|---|---|---|
| Token prefix | `--zk-` | `--prefix` |
| Token definitions | `zul/src/main/resources/web/zul/css/tokens` | `--tokens-dir` |
| Root scanned for `var()` refs | `zul/src/main/resources/web` | `--css-root` |
| Root scanned for hardcoded values | `zul/src/main/resources/web/js/zul` | `--component-root` |

If a new theme renames its prefix or moves directories, pass the flags — the
script has no other theme-specific assumptions.

Run the script from the zk repo root; the defaults above are the CE (`zul`) locations — pass the
matching `../zkcml/zkmax/...` or `../zkcml/zkex/...` paths to audit EE/PE CSS instead.

## Step 1 — Mechanical pass (automated)

```bash
bash .claude/skills/marble-theme/scripts/audit-css.sh --out doc/css-audit-<theme>.md
# or just print to the terminal:
bash .claude/skills/marble-theme/scripts/audit-css.sh
```

It emits an A–F report skeleton with these checks filled in:

1. **stylelint (§D)** — duplication / redundancy / dead CSS via the repo's focused
   `.stylelintrc.json`. Target is **0 errors**; the 3 goldenlayout warnings are a
   known, accepted intentional split.
2. **Orphan tokens (§A3)** — every token defined in `--tokens-dir` with **zero**
   `var()` references anywhere under `--css-root`.
3. **Hardcoded color candidates (§B1)** — bare hex / `rgb()` / `rgba()` in
   component CSS, *excluding* values inside a `var(--x, …)` fallback (those are
   intentional defensive fallbacks, not violations). Comments are stripped first.
4. **Duplicate literal box-shadow (§B4)** — the same literal shadow string
   appearing in 2+ files (token references are ignored).
5. **Default-value redundancy (§G)** — `display` declarations on a bare `.z-<name>`
   root that merely restate the browser default of the element the widget renders
   (`.z-span { display: inline }` on a `<span>`). Delegated to
   `.claude/skills/marble-theme/scripts/check-default-display.js`, which resolves each root tag from the ZK
   **mold files** (`--zk-source`) and buckets hits into safe no-ops (§G1),
   verify-first candidates (§G2), skipped replaced/form elements (§G3), and
   unresolved sub-elements (§G4).

The script is **read-only and advisory** — it never edits CSS and always exits 0.
It does NOT replace the build gate; `npm run lint:css` stays the thing that must
pass.

If you can't or don't want to run the script, the checks reduce to these raw
commands (run from the repo root):

```bash
# stylelint — full human-readable listing
npm run lint:css

# orphan tokens — count var() refs for every defined token
cd zul/src/main/resources/web
for v in $(grep -rhoE --include='*.css' -e '--zk-[a-z0-9-]+:' zul/css/tokens | sed 's/:$//' | sort -u); do
  n=$(grep -rohE --include='*.css' -e "var\($v[,)]" . | wc -l | tr -d ' ')
  [ "$n" -eq 0 ] && echo "ORPHAN: $v"
done
```

## Step 2 — Human-judgment pass (manual triage)

The script lists candidates; you decide. The rules below are distilled from the
`marble` audit — they explain *why*, so adapt them rather than applying blindly.

### Orphan tokens (§A3): orphan ≠ deletable

A token with zero references is a *candidate*, not a verdict. Three buckets:

- **Keep — systematic scales.** A complete typescale / motion / elevation ladder
  is kept whole even if some rungs are unused; the completeness is the point, and
  a future component will reach for the missing rung. Don't punch holes in a scale.
- **Keep — outward-facing theme API.** Tokens are how downstream apps re-skin the
  theme. An unused color slot may exist precisely so consumers can override it. In
  particular, keep the **five semantic role quartets** — primary / secondary / success /
  warning / error, each expanded to `<role>` / `on-<role>` / `<role>-container` /
  `on-<role>-container` / `-fill` — as a whole even when a member has zero `var()`
  consumers; they are a deliberate public toolkit, not dead slots.
- **Flag for removal — one-off aliases / dead slots.** A single-use alias that
  nothing references, or a color slot that was never wired up, is a real candidate.
  Even then, confirm it isn't documented as public API before deleting.

When a token's reference count moved (e.g. a refactor repointed transitions from
`short3` to a new `--zk-motion-duration-standard`), the old token legitimately
becomes orphan — that's a real finding, not a script bug.

### Hardcoded colors (§B1): bare value vs fallback

- `var(--zk-color-x, #fallback)` is **not** a violation. The hex is a defensive
  fallback for when the variable is undefined. The script already excludes these.
- A **bare** value (`background: #f5f5f5;`) is the violation. For each one, ask:
  *is there an equivalent semantic token?* If yes, replace with
  `var(--zk-color-…, #thatvalue)` (keeping the value as fallback) for consistency.
  If no token has that value, it needs a **design decision** to mint one — flag it,
  don't invent a token name silently.
- A line can be both: `... var(--zk-color-error, #d32f2f); color: #fff;` is flagged
  because of the bare `#fff`, which is the part that needs attention.

### Cross-file duplicate token definitions (§A1)

The same token defined in two files is usually an **intentional responsive
override** (e.g. a tablet layer enlarges a cell-padding token) — not a bug.
Confirm the second definition lives in a layer that's *meant* to override (tablet,
dark, high-contrast) before treating it as duplication.

### Repeated literal box-shadow (§B4)

An identical multi-stop shadow copy-pasted across files is a DRY smell → extract a
shared token (the `marble` audit created `--zk-elevation-resting` for exactly this).
This is a refactor with visual impact, so verify the affected components after.

### Default-value redundancy (§G): inline no-ops vs block anchors

The check compares a widget's declared `display` against the browser default of the
element it actually renders. **Root tag = ZK mold source of truth**: each widget's
`<comp>$mold$(out)` emits its root tag as the first `out.push('<TAG' …)`, which
resolves the non-1:1 cases (label → `<span>`, image → `<img>`, toolbarbutton →
`<a>`). Point `--zk-source` at the ZK `js/zul` tree; a small built-in fallback map
covers widgets whose mold declares no tag (e.g. `div.js`).

- **§G1 inline no-op → safe to remove.** `display: inline` on an inline-default
  element (`.z-span`, `.z-a`) does literally nothing. Confirm the reset layer doesn't
  set `display` on that element (marble's `_reset.css` only sets `a` color/
  text-decoration, never `display`) — then delete the line. Still cheap to prove
  empirically: build and probe computed `display` stays `inline` (see below).
- **§G2 restates a non-inline default → VERIFY first, don't bulk-remove.** `display:
  block` on a `<div>`-rooted container (`.z-grid`, `.z-listbox`, `.z-tree`) restates
  the default, but the value can be a **defensive anchor**: ZK toggles framework
  display classes at runtime (the `.z-flex` family) and the `@layer` cascade can
  reorder who wins. Prove render-neutral on the live app before deleting — reuse
  `.claude/skills/marble-theme/scripts/probe.js` from `reference/important-reduction.md` ("remove → build →
  measure computed style"); if the computed `display` is unchanged with the line
  gone, it was redundant. `.z-cell { display: table-cell }` on a `<td>` is the same
  shape.
- **§G3 replaced/form elements are skipped, not findings.** `<img>`, `<input>`,
  `<button>`, `<select>`… have UA-specific defaults and are always styled
  deliberately — e.g. `.z-image img { display: block }` is an intentional
  baseline-gap fix (an `<img>` is inline), NOT redundant. The check never flags these.
- **§G4 unknown root tag → manual.** Sub-element wrappers (`.z-*-content`,
  `.z-*-icon`, `.z-listcell-cnt`) aren't widget roots, so no mold resolves them. Only
  values that *could* be a default (`block`/`inline-block`/table-family) are listed;
  check them against the rendered DOM if you pursue them.

The check is `display`-only today, but the same "restates-default" machinery extends
to other properties that commonly restate defaults (`position: static`, `float:
none`, `visibility: visible`). Add them to `check-default-display.js` when needed.

### The sections the script leaves as TODO

`A4` (semantic token holding a literal value), `B2/B3` (hardcoded px / font-size),
`C` (duplicated state-layer / focus-visible blocks), `E` (utility-layer overlap),
and `F` (dead code) need eyes on the actual CSS. For **F especially**, do not run
PurgeCSS blindly — ZK toggles classes at runtime, so static analysis over-reports
dead code. Judge against rendered DOM if you pursue it at all.

## Step 3 — Standardize the baseline

The mechanical core is meant to stay on as a standing gate:

- Keep `.stylelintrc.json` as-is. It is a **focused** rule set (duplication /
  redundancy / dead CSS), deliberately NOT `stylelint-config-standard` — the full
  standard set buries the real signal under ~989 stylistic complaints (it flags
  every `.z-*` class, every required `-webkit-` prefix, intentional cascade).
- `npm run lint:css` reports only. **Do not** blindly `stylelint --fix` this repo:
  `--fix` escapes `<!--` to `\3c !--` inside `/* */` comments that document DOM
  structure, corrupting them. Apply `--fix` to a narrow glob and review the diff.

## Step 4 — Write it up

Produce the report using the **A–F structure** below so audits of different
themes stay comparable:

```
A. Token / variable layer   (duplicates, same-value-different-name, orphans, DRY)
B. Hardcoded values         (colors, px, font-size, repeated box-shadow)
C. Cross-file duplicated CSS blocks (state-layer, focus-visible, transitions)
D. In-file duplication      (stylelint authoritative scan)
E. Utility layer            (duplicate classes, intentional aliases)
F. Dead code                (deferred — needs rendered DOM)
G. Default-value redundancy (display decls restating the element's browser default)
```

For each finding give: location, the value, whether an equivalent token exists,
and a recommendation with severity. Separate "already safely fixed" (value-
preserving) from "needs design decision / visual regression" — the latter should
wait for explicit approval, exactly as the worked example does.
