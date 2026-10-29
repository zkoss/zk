# Verification: preview app, ports, screenshots

## Ports

| App | Port | Entry point |
|---|---|---|
| **Marble preview** | **8085** | `zkthemelab/build.gradle` (`httpPort`) |

The IceBlue baseline app exists only in the template until P4 — `zkthemelab` has no IceBlue
counterpart yet.

**Use `127.0.0.1`, not `localhost`.** Chrome resolves `localhost` to IPv6 `::1` while the preview
app binds IPv4. CLAUDE.md documents the base as `${PREVIEW_URL}` = `http://127.0.0.1:8085` rather
than hardcoding a port, so set `export PREVIEW_URL=http://127.0.0.1:8085` or paste the address.

Override the port with `-PhttpPort=<n>` on the `appRun` command below; there is no
`application.properties` to edit.

## Launching

```bash
cd zkthemelab && ./start.sh          # wraps the command below; carries the same three gotchas
cd zkthemelab && ./gradlew appRun -PhttpPort=8085 --console=plain
```

**`appRun`, never `appStart`.** Under gretty 3.1.1 on Gradle 8.10, `appStart`'s client never
returns. `appRun` waits for a key on stdin and treats EOF as that key, so keep stdin open — run it
in an interactive terminal, or hold a FIFO open from a script. The first start builds the composite
build (minutes); a warm start serves in about 10 s.

Every Playwright test opens a new HTTP session, and ZK keeps one `Timer` thread per session until
the session expires, so a full suite leaves about 1,300 threads behind. The preview app therefore
sets a 5-minute session timeout in its `web.xml`. If you still run several full suites back to back,
restart the server between them. Filed as ZK-6213.

Stop by pressing a key in the `appRun` terminal (closing stdin has the same effect).
**`./gradlew appStop` does not work against an `appRun` server** — it fails with
`java.net.ConnectException: Connection refused`. Under `appRun` only the `statusPort` in
`build/gretty_ports.properties` listens; the `servicePort` that `appStop` dials never opens. If you
lose the terminal, kill the `org.akhikhl.gretty.Runner` process instead.

**How to tell which theme is actually being served:** read the theme stylesheet href in the served
HTML. Marble is zk's core theme, so the reset link carries **no theme segment** —
`/zkres/web/<v>/zul/css/reset.css` (or `reset-embed.css` when
`org.zkoss.zul.theme.browserDefault=true`) — and it precedes `zk.wcs`. `screenshot.spec.ts` derives
the theme prefix from that `reset.css` link.

**No live-reload.** `zkthemelab` has no watcher and no live-reload client. After a CSS change,
rebuild (`./gradlew :zul:compileMarbleCss`, or the matching `zkcml` task for EE) and restart
`appRun`; the two live-reload `<script>` tags some preview pages still carry are inert.

## Preview pages

`http://127.0.0.1:8085/<page>.zul` — the module answers at the context root; a filter forwards
`/<page>.zul` to `/web/<page>.zul` when the page exists, and `~./` class-web resources resolve
unchanged. Sources live in `zkthemelab/src/main/webapp/web/*.zul` (159 pages). Smoke page:
`/smoke.zul`.

The **UseCase SPA** supports hash deep-links: `usecase/index.zul#<bookmark>`, where the bookmark is
the target ZUL's path relative to the web root minus `.zul` (`usecase/ops-dashboard`, or just
`button` for a root-level preview page). The `<navitem>` entries in `usecase/index.zul` are the
source of truth for what exists — consult them rather than any hardcoded list. `UseCaseVM.java`
handles `@Init` restore, the `navigate` command and `handleBookmarkChange` for back/forward.

Because deep-linking is **hash**-based, it survives a server-side redirect — a fragment never
reaches the server. Nothing in the harness uses a query string.

## Which tests to run after a change

Run `node zkthemelab/scripts/select-tests.mjs --base <base>` from the repository root (usage:
`zkthemelab/scripts/README-select-tests.md`). When it answers `partial`, run the single command it
prints against your local preview server. When it answers `full`, do not start a full run on your
own: report the selector's reasons and let the owner decide when the full suite runs. The suite
runs locally only; it is not wired to CI or Jenkins (owner's ruling, 2026-10-19).

## Running Playwright — flags that bite

- Compare against the baselines with the repo config: `npx playwright test -c src/test/playwright/playwright.config.ts ...`. A temporary config must copy `snapshotDir` and `snapshotPathTemplate`; otherwise the run writes new PNGs and passes falsely. After the run check `git status`: a new PNG means nothing was compared.
- In Playwright 1.59 a bare `--update-snapshots` defaults to `changed`. Name the mode (`--update-snapshots=changed`) and, before committing, confirm the file count with `git status --porcelain <baseline directory>`.
- `-g` matches the title path joined by spaces and starting with the Playwright project name (`project file describe title`, e.g. `chromium screenshot.spec.ts tabbox gallery`). Anchor the end with `describe title$`; anchoring the start with `^` requires the project name. Do not paste the `›` form that `--list` prints.

## Playwright projects

Config: `zkthemelab/src/test/playwright/playwright.config.ts`.

| Project | Covers |
|---|---|
| `chromium` | general specs |
| `gallery` | full-page component galleries — the bulk of the baselines |
| `smoke` | every preview ZUL renders (xmllint passing is **not** proof it renders) |
| `framework` | framework-class compliance |
| `component-theme-variables` | the per-component knob tests, one or more per component, each on that component's own preview page |
| `reset` | reset scoping |
| `iceblue` | IceBlue on ZK 11 through the `zktheme=iceblue` cookie (the iceblue jar is a `runtimeOnly` dependency): no 404 on the page, `z-icon-*` glyphs drawn |
| `hit-target` | minimum touch-target sizes |
| `responsive` | breakpoint behaviour |
| `print` | print stylesheet |
| `zindex` | the three z-index constraints |
| `focus-scan` | focus-ring visibility and clipping |
| `forced-colors`, `forced-colors-gallery` | Windows High-Contrast guards |
| `tablet` | needs a mobile UA to trigger `tablet.css.dsp` injection |

```bash
cd zkthemelab && npx playwright test --config src/test/playwright/playwright.config.ts --project=<project>
```

**Run it from `zkthemelab/`, not the repository root.** From the root, npx resolves a second copy of
`@playwright/test` and every spec dies with `Playwright Test did not expect test.describe() to be
called here` / `Total: 0 tests` — which looks exactly like a broken spec.

## Screenshot baselines — the parts that bite

- **The baselines are in place: 188 compared PNGs plus 99 `*-forced-colors.png` review captures, in
  `zkthemelab/doc/screenshots/`, beside `focus-ring-known-clips.json`.** They arrived with item 3.18b,
  so a run now *compares* — the earlier "can only create, never compare" caveat is gone.
  `playwright.config.ts:13`'s `snapshotDir: '../../../doc/screenshots'` resolves, from
  `zkthemelab/src/test/playwright/`, to that directory. The forced-colors images are **human-review
  artifacts that are always dirty** and never compared. (183 at the 2026-09-11 re-cut;
  `scrollview-tablet.png` made 184.)
- The harness resolves its `doc/` paths three levels up from `zkthemelab/src/test/playwright/` — the MODULE root (`zkthemelab/` in zk, the template's own root in the template), never the repository root; the baselines directory above and `focus-ring-known-clips.json` both live there. (Planner addendum after the 3.5 verdict, 2026-09-11 — see the migration's gates/3.5.md.)
- **The zero-tolerance comparison (P2 items 2.6–2.8) is the equivalence check that was actually
  run**: the template's unchanged specs against `zkthemelab`, every PNG compared at `threshold: 0,
  maxDiffPixels: 0` with the tracked tools under `zkThemeTemplate/doc/migration/tools/zero-tolerance/`
  — the gallery (98), state (55) and tablet (30) families came back byte-identical.
- **Always `await document.fonts.ready`.** A uniform vertical drift across every page is the Inter
  font-load race, not a CSS change. Baselines are flat files in one directory, not nested.
- **Tolerance is not one number.** `gallery-scan.spec.ts` allows `maxDiffPixelRatio: 0.002` (0.2%) —
  thousands of pixels on a full page — `tablet.spec.ts` opts into 2% for `tabbox` and `window`, and
  `screenshot.spec.ts`'s `padShot()` carries `maxDiffPixels: 20` for every hover/focus/active shot
  (chat D80, 2026-09-14 — `doc/spec/screenshot-tolerance.md`). The state shots use an absolute
  count, not a ratio: those crops run about 9 000 px, where 1% would be 92 px — enough to hide a
  whole mis-rendered focus ring. That is a *regression* gate. An equivalence check (did a refactor or a move change anything?) needs a one-off
  **zero-tolerance** run with every diff explained.
- **A preview `.zul` edit dirties two baseline families, not one.** Every page is shot by the
  `gallery` project (desktop) *and*, for the pages listed in `tablet.spec.ts`'s `visualCases`, by
  the `tablet` project. The 2026-09-11 typography sweep re-cut its 4 desktop diffs and re-verified
  `gallery` green — and left 29 stale tablet baselines behind, found only when an unrelated server
  restart ran the full `tablet` project the next day. After any change under
  `zkthemelab/src/main/webapp/web/*.zul`, run **both** `--project gallery` and `--project tablet`
  before committing, and re-cut with `--update-snapshots=changed` so only the mismatching files move.
- **`git log -1 -- <png>` lies when a commit only renamed the file.** Do not date a baseline that
  way.
- **The UseCase SPA hangs on `networkidle`.** Use `domcontentloaded`, then wait on a text marker,
  then `fonts.ready`, with a tall viewport.
- **The pop-up layer needs interaction** or roughly 180 selectors are never painted. Cursor
  position is the dominant noise source in pop-up A/B captures.
- **A placeholder PNG means the browser session desynced** — reset it rather than re-running.

## Empiricism, not inference

Before writing a width or layout claim into a contract, or designing a RED→GREEN geometry test,
**measure the live rendered value** with a 10-line `page.evaluate` over `getBoundingClientRect` /
`scrollWidth` / `clientWidth` / `getComputedStyle`. Reasoning from the CSS cascade plus widget
source predicts the wrong number: flex intrinsic sizing (`flex:1 1 0%` in a shrink-wrapped
container) does **not** fall back to the UA `size=20`.

**A test that goes green on the unfixed build proves the premise was wrong, not that the code is
fine.** And prefer a *behavioural* assertion (a longer value renders wider; `scrollWidth <=
clientWidth`) over an arbitrary px threshold — the threshold can be satisfied by the very bug you
are trying to catch.

Also: disable transitions before measuring focus rings, and never open `/preview` in the
Chrome automation session.

**Geometry claims are read off the captured PNG, never the live DOM (F57).**
`getBoundingClientRect` at `networkidle` runs before the gallery spec's font-settle wait and read
1 px wide on the splitter page while every shot showed the settled width.

## The tablet stylesheet is gated client-side, not by server UA

Traced empirically (an earlier note wrongly said the server checks the UA):

1. `org.zkoss.zkmax.theme.TabletThemeURIHandler` inserts `~./zkmax/css/tablet.css.dsp` at cascade
   position 1 (right after `zk.wcs`) on **every** request, shipped as a **`disabled`** `<link>`.
   Gated only by the library property `org.zkoss.zkmax.tablet.ui.disabled` (default enabled).
2. The ZK client runtime sets `zk.tabletUIEnabled = … && !!zk.mobile` and, on `DOMContentLoaded`,
   **enables** that link only when `zk.mobile` is truthy. `zk.mobile` comes from the request UA
   (iPad / iPhone / Android).

Consequences: a desktop UA leaves the link disabled — it is not even in `document.styleSheets`,
so **tablet CSS can never leak to desktop by construction**. `zk.wcs` (~1.6 MB, the whole theme)
sits at position 0 and `tablet.css.dsp` at position 1, so tablet rules win by source order with
no `!important`; size variants such as `.z-button-sm.z-button` keep their size by specificity.
Marble is already wired: `MarbleThemeWebAppInit` registers `tablet:marble` (EE only) and the build
emits the file. Detect in-page with
`[...document.styleSheets].some(s => s.href?.includes('zkmax/css/tablet.css'))`.

## Playwright, not Selenium or ZK WebDriver

For CSS/theme design regression the project uses Playwright and nothing else. ZK WebDriver is a
ZK-aware wrapper around Selenium built for **functional** widget / AU testing; for static CSS it
adds nothing (you assert on stable `.z-*` selectors with no server round-trip) and would bolt a
Gradle/JUnit stack onto a Maven project. Playwright wins on built-in screenshot diffing, auto-wait,
and one-line device emulation — the last is essential because the tablet stylesheet only loads
under a mobile UA. Reach for ZK WebDriver only for a genuinely functional end-to-end need.

The Playwright suite is the regression guard; the agent harness (evaluator computed-style checks
plus visual review, dual-gate VERIFIED per `.claude/skills/marble-theme/reference/harness-decisions.md`) is the
authoring-time deep verification. They are complements.

## Flaky galleries, and how to prove neutrality without them

The `listbox`, `tree`, `window`, `panel`, `tabbox` and `toast` galleries hit `toHaveScreenshot`'s
5-second stabilisation timeout on those heavy pages even when run alone. **Do not read their
failures as regressions.** Confirm rendering-neutrality with a HEAD-versus-change A/B plus
deterministic computed-style probes instead. A stale-baseline failure looks identical at HEAD with
no change applied — check that before trusting the gate. Run ad-hoc probes with
`NODE_PATH="$(pwd)/node_modules" node script.cjs` using `require('playwright')`.

## Cross-theme A/B: the determinism floor

The IceBlue baseline app is template-only until P4 — `zkthemelab` has no IceBlue counterpart to run
this comparison against yet.

Serving Marble's preview corpus under a different theme jar needs no edit here: ZK's
`Library.getProperty` falls back to `System.getProperty`, and the IceBlue baseline app leaves the
preferred theme unset, so `-Dorg.zkoss.theme.preferred=<theme>` on the command line selects it.
**Exclude `MARBLE/target/classes` from that classpath** — `MarbleThemeWebAppInit` locks its own
provider with `setCustomThemeProvider(true)`, and that provider returns null for
`font-awesome.css.dsp`, so the other theme's icons silently never load.

**Always guard-probe before trusting a run:** assert the theme segment is in the CSS URL, the
*other* theme's name appears zero times, **and the page parsed >0 CSS rules and >0 `--zk-*`
declarations**. Name checks alone pass on a page with no styles at all, because the theme name
comes from the registered Java class, not from any CSS existing. A harness whose theme never
loaded reports a perfect zero diff. `zk.wcs` is **empty on a second request**, and `body`'s font
is `Times` even on a healthy page — neither is a usable signal; read `document.styleSheets` in-page.

Measured over 116 pages, repeatedly: **byte-identical PNGs across browser processes are not
achievable.** A few pages differ by 2–42 px on border-radius arcs, by up to ±7 per channel, scaling
with the contrast of the blended colours. No Chromium flag removes it, and `--deterministic-mode`
is actively harmful. Within one process it is stable, so shoot-until-two-stable-frames converges.
The comparator's floor is therefore **`maxDelta ≤ 8` and `diffPixels ≤ 64`, not a ratio** — a 1px
border on a 200px widget is ~0.017% of a 1280×900 shot, so any ratio loose enough to absorb the
noise also absorbs a real border change. IceBlue ships 5 animated GIFs that no
`animations: 'disabled'` freezes; `page.route(...).abort()` them.

## Pop-up layer captures — the lessons that generalise

A goto-and-shoot capture never paints anything that only exists when a panel is *open* — roughly
180 selectors in the theme. Opening them by widget API taught these:

- **The cursor is the dominant noise source, and there is no general rule.** Three failures were
  all cursor position, in opposite directions: an overflow panel opening *under* the parked
  cursor (a button inside came out hovered), and a plain `<popup>` that is pointer-positioned and
  reads the pointer late, so parking raced its positioning. Default: park after the trigger;
  `holdCursor` per measured exception.
- **`locator(sel).first()` resolves to a hidden pre-rendered panel** — pages render one per widget
  (timepicker has 15). Require **exactly one visible match**, and make that function both the
  wait condition and the clip.
- **Picking a widget by DOM index is wrong** once disabled instances are filtered out; pick by a
  predicate over the widget.
- Per-widget open APIs differ and are measured, not documented: Combobox / Bandbox `open()`;
  Datebox `setOpen(true)`; Colorbox `openPopup()`; Chosenbox `open()` throws unless focused;
  Nav `setOpen(true)` expands *inline*; **Timebox has no desktop open API at all**.
- Some "pop-up" classes are modifiers on the **head** element and render at rest —
  `.z-columns-menupopup` pads the header for the caret. Check where the class actually lands
  before filing a gap as a pop-up gap.
- **Searching the wrong jar exhaustively still returns zero.** `.z-portallayout-popup*` is drawn
  by the optional `za11y` language addon, not by `zkmax`; every "no code creates this" measurement
  was correct and searched the wrong bundle. Before calling CSS dead, ask which classpath the
  claim was made on.
- A per-process constant cannot be waited out. When a shot is bistable *across* browser processes
  but stable within one, launch 8–10 independent processes and count distinct PNGs; a single run
  is a coin toss. **Keep the differing artefact on the first failure** — the next run destroys it.

## Cleaning up a probe

Never `git checkout` a spec file to remove a probe you appended. The spec usually already holds
new, uncommitted real tests, and `checkout` reverts the whole file. Remove only the probe block,
or put probes in a throwaway file from the start.

## Theme-done criterion

The theme is complete only when **every preview page** under `zkthemelab/src/main/webapp/web/*.zul` is
verified. The page set, not the component list, is the checklist — see
`reference/zul-authoring.md` for why.
