# Marble Theme Follow-ups

Deferred work on the Marble theme and the harness that verifies it. Items 1–5 are
concrete work steps converted from the 2026-07-22 process audit
(`doc/orchestrator-playbook-review.md`, since deleted); later items are added as they are
deferred, and name where the decision was taken. Each item is independent —
pick any up in a fresh session. When an item ships, delete its section here and
update the cross-references it names.

## 1. Brand-flip regression project

Goal: prove the theme recolors end-to-end when a customer overrides the seed token.
The per-component `x-brand-decl` check guards declarations only; nothing tests the
cascade (`doc/spec/brand-override.md`) across real pages.

- [ ] Add a `brand-flip` project to `src/test/playwright/playwright.config.ts`.
- [ ] New spec `brand-flip.spec.ts`: load representative pages (e.g. `button.zul`,
      `checkbox.zul`, `tabbox.zul`), inject
      `:root { --zk-color-primary: <distinct seed hue> }` via `addStyleTag` before
      measuring, `await document.fonts.ready`.
- [ ] Assert via computed styles, not screenshots: primary button background, checked
      checkbox/radio fill, tab indicator, and focus-ring color all shift hue with the
      injected seed (containers/overlays derive via `oklch(from …)` — assert the hue
      moved, not exact channel values).
- [ ] Add npm script `test:brand-flip` mirroring `test:forced-colors`.
- [ ] Remove the matching bullet from `doc/spec/new-component-checklist.md` § Known limits.

## 2. Compact-preset regression project

Goal: prove controls actually shrink under density compact mode. The per-component
`x-density` probe shrinks seed tokens one at a time; nothing tests the whole preset.

- [ ] Add a `compact` project to `src/test/playwright/playwright.config.ts`.
- [ ] New spec: render pages twice — default vs `data-density="compact"` on the host
      (per `doc/spec/data-dense-mode.md`; alternatively load the `doc/marble-compact.css`
      tuning preset) — and assert rendered control heights (button, textbox, combobox,
      listbox row) drop by the ladder delta from `tokens/_sizing.css`.
- [ ] Remove the matching bullet from `doc/spec/new-component-checklist.md` § Known limits.

## 3. render-smoke auto-discovery

Goal: stop hand-maintaining the `PAGES` list — new preview pages should be
smoke-tested automatically, like `gallery-scan.spec.ts` already does.

- [ ] Convert `src/test/playwright/render-smoke.spec.ts` from the hardcoded `PAGES`
      array to auto-discovery of `src/test/resources/web/*.zul` (reuse/share the
      discovery + SKIP-set helper from `gallery-scan.spec.ts`).
- [ ] Keep an explicit SKIP set for pages that are not standalone.
- [ ] Then delete bookkeeping item 3 (render-smoke `PAGES`) from
      `doc/spec/new-component-checklist.md` § Orchestrator bookkeeping gate AND the
      matching item in `doc/orchestrator-playbook.md` Step 4c (renumber), plus the
      Known-limits bullet — the obligation becomes automatic.

## 4. Evaluator migration to Playwright (capture + measurement)

Goal: remove the Chrome-MCP single point of failure (a `gif_creator` tab-group desync
once stalled Gate 2), get deterministic waits, and lift the 4-parallel-evaluator
Chrome-tab cap.

- [ ] Map the evaluator's ready-state gate to Playwright waits
      (`domcontentloaded` + per-page marker + `document.fonts.ready` — same recipe as
      the usecase screenshot specs; never `networkidle`, ZK AU keeps the connection busy).
- [ ] Replace `javascript_tool` computed-style measurement with `page.evaluate` in a
      parametrized helper under `src/test/playwright/` (disable CSS transitions before
      measuring border-color/box-shadow).
- [ ] Capture per-matrix artefacts via Playwright screenshots into `doc/screenshots/<comp>/`.
- [ ] Rewrite `.claude/agents/zk-theme-evaluator.md` §3/§3a/§3b + Tools lists: drop
      Chrome-MCP, allow Playwright runs. The existing §3a Playwright screenshot
      fallback is the seed of this migration.
- [ ] Raise the parallel-evaluator cap in `doc/orchestrator-playbook.md` Step 2 /
      Parallel execution rules accordingly.

## 5. Generator batch-build parallelism (conditional — do NOT do preemptively)

Trigger: only if Generator strict serialization becomes the throughput bottleneck
(it is not today — most rows are VERIFIED and Generator dispatches are rare).

- [ ] Generators become edit-only: skip `npm run build:css` in the Generator.
- [ ] The orchestrator runs ONE build per batch after all Generators of the batch return.
- [ ] On build failure, attribute by bisect (rebuild with half the diffs applied).
- [ ] Update `.claude/agents/zk-theme-generator.md` (build step + Tools) and
      `doc/orchestrator-playbook.md` Step 5/6 (serial-dispatch rule).

## 6. `.z-label` re-declares what it already inherits (deferred to P4)

Deferred to P4 by the user on 2026-09-11 (chat decision D6). This is shipped theme CSS
touching every Label in every ZK app, so it does not belong in a preview-only change.

Goal: delete the redundant declarations on `.z-label` so typography and colour set on a
container reach the text, instead of adding paired utility classes to work around it.

### The finding

`.z-label` (`zul/src/main/resources/web/js/zul/wgt/css/label.css`, the rule at the top of
the `zk-components` layer) declares five properties. All five reproduce what the Label
would otherwise inherit from `body` (`zul/src/main/resources/web/zul/css/base/_reset.css`):

| Property | `body` sets | `.z-label` sets | |
|---|---|---|---|
| `font-family` | `var(--zk-typescale-font-family)` | `var(--zk-typescale-font-family)` | token-identical |
| `font-size` | `var(--zk-typescale-body-medium-size)` | `var(--zk-typescale-body-medium-size)` | token-identical |
| `font-weight` | `var(--zk-typescale-body-medium-weight)` | `var(--zk-typescale-body-medium-weight)` | token-identical |
| `line-height` | `var(--zk-typescale-body-medium-line-height)` | `var(--zk-typescale-body-medium-line-height)` | token-identical |
| `color` | `var(--zk-color-on-background)` | `var(--zk-color-on-surface)` | different token, **same value** `rgba(0, 0, 0, 0.87)` |

So in the default case the rule changes nothing. Its only effect is to block every
contextual override: an element's own declaration beats an inherited value, and
inheritance is not a cascade competitor, so `@layer` order and specificity are irrelevant.
ZK wraps raw text in a `zul.wgt.Label`, so this hits any text inside a styled container.

Both `git log` and the file itself carry no rationale — the rule arrived wholesale with
`2100200284` ("replace the LESS theme pipeline with the Marble CSS sources"), so treat it
as migration residue, not a considered decision, until evidence says otherwise.

### Why it is worth doing

- It is the root cause of the 52 remaining dead text colours (chat D6): a `z-bg-*` class
  sets `background-color` **and** `color` in one rule; the background belongs on the
  wrapper and the colour on the label, so the class cannot move. Deleting `.z-label`'s
  `color` fixes all 52 with **no new classes and no paired-class API**.
- The alternative considered and rejected was adding five utilities —
  `.z-text-on-primary-container`, `.z-text-on-secondary-container`,
  `.z-text-on-surface-variant`, `.z-text-on-success-container`,
  `.z-text-on-error-container` (none exist today; only `.z-text-on-primary` and
  `.z-text-on-surface` do). Rejected because it forces authors to remember that
  `z-bg-X` and `z-text-on-X` are a pair, which is the thing MD3's container/on-container
  pairing exists to avoid.
- The same logic covers the four font properties. Had `.z-label` not declared them,
  commit `47d26068ed` (1223 typography rewrites across 142 preview pages) would not have
  been necessary — those wrappers would simply have worked. That commit is still correct
  authoring per `reference/zul-authoring.md` and should stay; it is just symptom-level.

### Scope of the 52 occurrences

Counted after `47d26068ed` in `zkpreview/src/main/webapp/web/**/*.zul`:

| Wrapper class | Count | Example page |
|---|---|---|
| `z-bg-primary-container` | 44 | `dnd.zul`, `utility/spacing.zul` |
| `z-bg-secondary-container` | 4 | `utility/responsive.zul` |
| `z-bg-surface-variant` | 2 | `dnd.zul` |
| `z-bg-success-container` | 1 | `utility/print.zul` |
| `z-bg-error-container` | 1 | `utility/zindex.zul` |

Severity is **fidelity, not accessibility**: those container backgrounds sit at lightness
0.87–0.92 and the wrongly-inherited `on-surface` is 87% black, so contrast passes. What is
lost is MD3's colour semantics — five semantic containers all render the same neutral grey
text instead of their paired hue.

`z-bg-*` has no consumer outside the preview pages: zero hits in `zul/`, `zkmax` and
`zkex` component CSS (`button.css` mentions `.z-bg-*` only in a comment) and zero in
`zktest`.

### Steps

- [ ] Stage 1 — delete only `color: var(--zk-color-on-surface);` from `.z-label`. This is
      the minimal change that closes D6. Verify before going further.
- [ ] Stage 2 — only if stage 1 is clean, evaluate deleting the four font properties too,
      which is what would make container typography work generally.
- [ ] Audit what currently relies on `.z-label` *resetting* an inherited colour — a Label
      inside any component that sets its own `color` will start following that colour.
      Grep component CSS for rules that set `color` on a container whose content is a
      Label (chips, badges, coloured panel captions, selected rows, filled buttons).
- [ ] If a component genuinely needs its Labels pinned, fix it at that component's own
      selector, not by restoring the theme-global declaration.

### Verification bar

Run against a preview app on a free port (`cd zkpreview && ./start.sh <port>`; never
assume 8085 is free — other sessions use it, and zkpreview serves the live working tree):

- [ ] `probe.js` on a `z-bg-*` wrapper: the inner `.z-label` computes the container's
      paired `on-*` colour rather than `rgba(0, 0, 0, 0.87)`.
- [ ] Playwright, all projects that passed for `47d26068ed`: `smoke` (115), `framework`
      (7), `responsive` (4), `print` (2), `zindex` (2), and `gallery` (82).
- [ ] `gallery` will legitimately move on pages whose text colour changes — regenerate
      only those baselines, and use bare `--update-snapshots` (preset `changed` in
      @playwright/test 1.59.x), never `=all`, which rewrites all 82.
- [ ] `cd zktest && ./gradlew test` — this is shipped CSS, so the component suites matter
      here in a way they did not for a preview-only change.
- [ ] Check both themes and `forced-colors`; `_forced-colors.css` overrides colour
      independently and must still win.

### Cross-references

- Chat decision D6, 2026-09-11 — deferred to P4 rather than done inline.
- `47d26068ed` — the preview-side sweep; its commit message records the 41-vs-52 count
  correction and the `z-bg-*` carve-out.
- `.claude/skills/marble-theme/reference/zul-authoring.md` § "ZK wraps raw text in a Label"
  — states the authoring rule; update it if stage 2 lands, since the rule's premise
  changes.
- `.claude/skills/marble-theme/SKILL.md` rule 5: "When a theme-global invention collides
  with a ZK JS assumption, the first option to evaluate is *deleting the invention*." That
  is the principle this item applies.

## 7. Generate the utility class index page

Goal: publish the human-readable catalogue of the `z-*` utility classes, generated from the
same manifest the IDE plugin reads, so prose documentation can never drift from the CSS.

This is Phase 3 of `doc/utility-class-discovery.md`. It was deliberately held until the naming
review landed — generating it earlier would have published names that were about to change.
That blocker is now gone: the names are final and `doc/spec/utility-naming.md` is normative.

- [ ] Generate `doc/spec/utility-index.md` from
      `zul/codegen/resources/web/zul/css/utility-classes.json`, the way
      `scripts/build-css.js --emit-docs` already generates `doc/spec/icon-index.md`.
      Group by the manifest's `category` field; show each class's `css` string.
- [ ] Carry the breakpoint note: a trailing `-sm/-md/-lg/-xl` is always a SIZE; breakpoints
      are infixes (`z-d-md-none`). Readers coming from the old suffix form will look for
      `z-d-none-md` and must be told where it went.
- [ ] Point `.claude/skills/marble-theme/reference/zul-authoring.md` at the generated index
      instead of at the raw `utility/` CSS directory.
- [ ] Add the CI guard the discovery doc describes: assert every class in the manifest
      resolves in the built `norm.css.dsp`. Note the drift risk it worried about does not
      exist — the manifest regenerates into the gitignored `codegen/` tree on every build,
      so it cannot fall behind. The *coverage* guard is the one still worth having.

### Cross-references

- `doc/utility-class-discovery.md` § Phase 3 — the original plan; D3 there settled that there
  is no `description` field, so the `css` string is the hover text.
- `doc/spec/utility-naming.md` — the naming rules the index documents.
- `b1a4a0e31c` — the commit that made those rules normative.

## 8. Regenerate the stale screenshot baselines

**Done 2026-10-06** (Jess review prerequisite, rulings D19-A to D25-A). 33 baselines regenerated
after per-image review, the organigram and tree/listbox row forced-colors focus checks fixed in the
test, the `chromium` threshold tightened to 0.05 and 4 dropdown/chip shots added. Verifier verdict
PASS: `doc/jess-review/gates/baseline-verify.md`. Rulings: `doc/jess-review/decisions.md` (D19–D25).

Goal: get `screenshot.spec.ts` and `focus-ring-scan.spec.ts` back to green, so a real
regression is visible instead of being buried under 28 known failures.

**This is not caused by the utility renames.** It was proven by A/B: the same 28 failures occur
on `15bc0c7eeb`, the commit before that series, with the preview app rebuilt and restarted on
each side. The baselines under `zkpreview/doc/screenshots/` were last regenerated 2026-09-12,
before the LESS-to-Marble CSS pipeline replacement (`2100200284`) and everything after it.

- [x] Confirm the 28 are all stale-baseline, not real defects — inspect the diff images in
      `test-results/` before accepting any of them. A genuinely broken component would hide
      in this set perfectly.
- [x] Regenerate with bare `--update-snapshots` (preset `changed`), never `=all`.
- [x] 26 are `[chromium] screenshot.spec.ts` gallery + hover/focus shots; 2 are
      `[focus-scan]` forced-colors assertions (`tree: tree row`, `organigram: organigram
      node`) which are **assertion** failures, not image diffs — those two need a real fix
      or an explicit waiver, not a regenerated image. Correction (2026-10-06): `tree: tree row`
      did not really pass after the #41/#43 fix; it passed only because it, too, sampled a
      mid-transition blend. With transitions disabled it fails, because the test still forces
      `:focus-visible` on the `.z-treerow` itself, a state ZK never produces (the row ring is
      gated on the tree's `.z-focus-a:focus-visible` since #43). In the real state the ring is
      HighlightText on Highlight. See the baseline section of
      `doc/jess-review/gates/baseline-verify.md`.
- [ ] Beware: `forced-colors-gallery.spec.ts` is a visual-review spec that **rewrites** ~100
      tracked PNGs on every run. Revert those before staging unless they are the point of
      the commit.

### Cross-references

- `aafbbaf60a` — the A/B result is recorded in its commit message.
- `doc/utility-class-naming-review.md` § 6.4 — the sweep-mechanics lessons, including the
  visual-review-spec trap.
- `doc/screenshot-tolerance-policy.md` — the existing tolerance ruling; the `padShot` floor
  (20px) is why a one-step font change is invisible to these shots.

## 9. A truncated tab label never shows an ellipsis

**Done 2026-10-10** (Jess review batch 15, `01f7010144`). `min-width: 0` on `.z-tab-content` and `.z-tab-text`, and a text-only label is a block so `text-overflow` applies. Limit: a label with an icon child keeps the flex row and shows no ellipsis. Evidence: `doc/jess-review/gates/batch15-final-r2.md`.

Found while verifying Jess #52 (2026-10-06, user ruling D14-A: tracked here, not part of #52).

`.z-tab-text` declares `overflow: hidden; text-overflow: ellipsis; white-space: nowrap`, but it is
a flex item of `.z-tab-content` (itself a flex item of `li.z-tab`) and neither carries
`min-width: 0`, so the label never shrinks below its content width. With a fixed narrow tab width
the label keeps its full width, `li.z-tab`'s `overflow: hidden` clips it on both sides, and no
ellipsis appears. Measured after the #52 fix: a 219px label in a 60px tab is shown as a centred
middle slice. The pre-fix rules have the same structure, so this is inferred to predate #52; it
has not been measured on the pre-fix tree.

- [ ] Confirm on the pre-fix tree (or accept the inference from the rule structure).
- [ ] Let the label shrink (`min-width: 0` on `.z-tab-content` and `.z-tab-text`) and check that
      the #52 indicator, which is sized from `.z-tab-content`'s padding, still matches the
      visible label width once the label is truncated.

### Cross-references

- `doc/jess-review/gates/batch1-52.md` — round 1 and round 2 extra checks.

## 10. Chosenbox dropdown option hover is a hardcoded black 4% fill

Found while adding the dropdown screenshots for D22-B (2026-10-06).

`zkcml/zkmax/src/main/resources/web/js/zkmax/inp/css/chosenbox.css:147-150` paints
`.z-chosenbox-option:hover, .z-chosenbox-option-hover` with `rgba(0, 0, 0, 0.04)`. That class is
also the keyboard-highlighted option (ArrowDown in the open dropdown). Three problems:

- It is a literal colour, against the "no hardcoded colour values" rule; no token or knob reaches it.
- Black at 4% disappears on a dark surface, so the dark preset has no visible keyboard highlight.
- MD3's hover state layer is `on-surface` at 8%; 4% is half of that.

Batch 2 (#23) did not cover it: its chosenbox knob `--zk-chosenbox-item-focus-bg` is the fill of a
clicked **chip** in the input, not of a dropdown option.

- [ ] Decide the state for a keyboard-highlighted option (hover state layer, or the list-row
      family's `secondary-container` as combobox uses) and route it through a token.
- [ ] Check the dark preset.

### Cross-references

- `doc/jess-review/gates/baseline-d22-gen.md` — the measured colours of the three dropdown items.

## 11. A long column label is covered by the sort icon (and the menu button)

Found while verifying Jess #35/#39 (batch 3, 2026-10-07; user ruling D34-A: tracked here, no theme change).

After #39 the grid sort icon is `position: absolute` at the right end of the header cell, so it no longer pushes the label. A label that fills a narrow column is drawn under the icon (and under the hover column-menu button) with no ellipsis, because `.z-column-content` is a block inside the `th` and carries no overflow rule.

An application can fix it with ellipsis plus room for the icons:

```css
.z-column-content { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; padding-right: 22px; }
.z-column:has(.z-column-button) .z-column-content { padding-right: 50px; }
```

Measured on the Basic grid, Author column at 130px: ellipsis alone still leaves the icon on top of the text; ellipsis plus padding stops the text before the icon.

- [ ] Decide whether the theme should ship this for sortable columns. Cost: `align="center"` sortable columns shift left by half the padding, so header text no longer centres over the body text.

## 12. Frozen divider is missing in the body under forced-colors

`forced-colors: active` strips `box-shadow`, so the `inset -1px` body divider added for Jess #37 does not paint; the header divider survives through its border. Same result as before the fix (0 rows), not a regression.

- [ ] Add a forced-colors rule that draws the body divider with a border (`ButtonBorder` / `CanvasText`).

## 13. Header text is 4px off the body text in listbox and tree

**Not reproducible 2026-10-10.** Header and body text are 0px apart in grid, listbox and tree, before and after sorting. The commit that fixed it was not identified. The frozen-cell hover alpha note below still stands.

Found while measuring Jess #39: listbox and tree show the same 4px offset in non-first columns (first columns vary with indentation and structure). #39 only fixed the grid header (`.z-column-sorticon`); `.z-listheader` and `.z-treecol` have their own sort icon rules.

- [ ] Measure and fix the listbox and tree headers with the same method.
- [ ] Frozen-cell hover alpha is 0.9896 in listbox and tree (0.9948 in grid), so a scrolled-under label shows through very faintly. Pre-existing; the batch 3 gate accepts alpha >= 0.98.


## 14. The tablet grid gallery shows a broken frozen header

**Done 2026-10-10** (`01f7010144`). The cause was not the tablet viewport but the mobile UA: ZK adds `z-frozen-sticky` and an inline `left` and expects `position: sticky`, and that rule was dropped in `2100200284`. Restored for grid, listbox, tree and their footers; `grid-tablet.png` and `listbox-tablet.png` regenerated.

Found while classifying the batch 3 baselines (2026-10-07; user ruling D38-A: baseline regenerated anyway, tracked here).

In `grid-tablet.png` the "Col B" and "Col C" headers overlap at x=433, the header underline has a gap, and the header boundary is at x=633 while the body boundary is at x=432. This was already in the committed baseline before batch 3, so the baseline stores a broken picture and will differ again when it is fixed.

- [ ] Find why the frozen header is laid out differently at the tablet viewport (header boundary 633 vs body 432).
- [ ] Regenerate `grid-tablet.png` once fixed.

### Cross-references

- `doc/jess-review/gates/batch3-baseline.md`, `sbs-grid-tablet.png`.

## 15. No screenshot covers a sorted column header

None of the 5 gallery screenshots shows a sort icon (0 of 85 headers), so the Jess #35 change (arrow glyph) and the #39 icon position are not protected by the screenshot tests. Same kind of gap as the dropdown screenshots added for D22-B.

- [ ] Add a screenshot of a grid header with one column sorted ascending and one descending (Basic grid, real clicks).

## 16. Custom scrollbar thumbs are invisible under forced-colors

Found while verifying Jess #78 (batch 4, 2026-10-07). Under `forced-colors: active` the biglistbox thumb and the `zul.Scrollbar` embed rail both render as the page background (centre pixel equals the background), because both are painted with `background-color`, which the browser forces to Canvas. Same family as item 12.

- [ ] Give the thumbs a forced-colors rule (`ButtonBorder` / `CanvasText` fill or a border).

## 17. The tablet baselines for slider and calendar are stale

**Done 2026-10-09** (Jess review batch 14, S6). Both baselines regenerated.

Found while classifying batch 4 (2026-10-07; user ruling D42-A: not part of the Jess batch). `slider-tablet.png` has not been regenerated since `64f1c07b9d` (vertical slider default height), and `calendar-tablet.png` since `778b5a809f` (datebox follows `cols`). The `tablet` project fails on both until someone regenerates them through the usual classify → confirmation page → approve flow.

- [ ] Regenerate `slider-tablet.png` and `calendar-tablet.png` (by the work that owns those commits).
- [ ] A commit that changes a component's CSS should run the `tablet` project, including pages that embed the component.

## 18. Untested corners of the biglistbox scrollbar

Batch 4 did not cover: the touch variant (`zkmax/css/tablet/_scrollbar.css`), `frozenCols` / `fixFrozenCols` combinations (the page's dropdowns need the composer to fill), and `doc/spec/DESIGN.md` has no biglistbox scrollbar paragraph (section 15 covers only the native and `zul.Scrollbar` bars).

- [ ] Measure the touch variant and a `frozenCols` biglistbox against the batch 4 checks.
- [ ] Decide whether DESIGN.md needs a biglistbox scrollbar note.

## 19. The tablet scrollable menubar overflows its bar

**Done 2026-10-11** (Jess review batch 16). A Marble regression, not a ZK bug: on a tablet UA zkmax's touch menubar makes `setScrollable()` a no-op and expects native scroll, which the old IceBlue tablet stylesheet provided with `.z-menubar { overflow: auto }`. Added `.z-menubar-horizontal { overflow: auto }` to zkcml `tablet/_menu.css`; `menubar-tablet.png` regenerated. Evidence: `doc/jess-review/gates/batch16-final.md`.

Found while regenerating `menubar-tablet.png` (2026-10-09, D63; commit `8cc85e8a6c`). In the "Scrollable Menubar" block at the tablet viewport, items 3–5 run past the end of the grey bar and there are no scroll arrows. The desktop gallery (`menubar-gallery.png`) shows the same block correctly (arrows, clipped items). It may be how the tablet layout has always behaved or a regression; the regenerated baseline now stores the current picture.

- [ ] Find out whether the tablet picture is intended (compare with the baseline before `221d6ed74b` and with the touch scroll behaviour).
- [ ] If it is a defect, fix it and regenerate `menubar-tablet.png`.

## 20. Stray closing brace in `checkbox.css`

Found while reading the switch section for Jess #10 (batch 12, 2026-10-09). `zul/.../wgt/css/checkbox.css` ends with an extra `}` after the toggle disabled rules (around line 580). Not caused by batch 12 and not checked for build impact.

- [ ] Check whether the brace is really stray (an unclosed block earlier in the file) and whether the CSS build or minifier hides it.

## 21. Switch follow-ups left after the MD3 change

Jess #10 (batch 12) made `mold="switch"` follow MD3. Left out on purpose:

- The check mark inside the thumb (optional in MD3, shown in the designer's picture).
- `tokens/_forced-colors.css` still carries the old switch rules (1px track border, `CanvasText` thumb). The forced-colors suite passes; it is a shared file, so any rule change needs a ruling first.
- The off-state thumb uses `--zk-color-on-surface-variant` instead of MD3's `outline` (reason: 1.41:1 against the track with Marble's light outline token). Revisit if the outline token gets darker.

- [ ] Decide whether the thumb icon is wanted.
- [ ] Re-check the forced-colors rules for the 52×32 track.

## 22. The outline and surface-container tokens are light on white

Measured for Jess #10 (batch 12). The switch track border is 2.13:1 against a white page (the `--zk-color-outline` token on white is 1.74:1) and the off-state track fill is 1.23:1. WCAG 1.4.11 asks 3:1 for the boundary of a control, so this affects every control that draws its boundary with `--zk-color-outline`, not only the switch.

- [ ] Decide whether Marble's `--zk-color-outline` should be darker (design call; touches many components).

## 23. `grid-paging` gallery flipped between runs

**Not reproduced 2026-10-10.** The gallery project passed 82/82 on three consecutive runs after batch 14. The cause of the earlier flip was not identified.

`gallery › grid-paging` failed in the merge-1 regression (2026-10-09) and passed in the batch 12 regression the same day. Someone may have regenerated `grid-paging-gallery.png` in between; the 1% pixel tolerance may also hide it.

- [ ] Confirm which commit regenerated it, or whether the test is flaky.

## 24. Nested lists inside a horizontal navbar dropdown are clipped

Found in batch 14 (line C). `.z-navbar-horizontal .z-nav > ul` is `position:absolute`, so a nested group's list lands on the parent list's bottom edge and `.z-nav > ul { overflow:hidden }` clips it. The nested group title is now indented to match its sibling items, but its children (third level) cannot be seen.

- [ ] Decide whether nested groups in the horizontal dropdown should expand statically (change the selector to `.z-navbar-horizontal > ul > .z-nav > ul`).

## 25. Menu popup `image` icons carry a 1px side margin

Batch 14 added `margin: 0 1px` to `.z-menupopup .z-menuitem-image, .z-menupopup .z-menu-image` so a 16px image sits in the same 18px slot as an iconSclass glyph. A popup that holds only image rows shifts its text right by about 2px (rows stay consistent). Nested `z-menu` images and colorbox chips were measured and are fine.

- [ ] Confirm nobody needs the old 16px slot; otherwise nothing to do.

## 26. The selectbox forced-colors rule is merged into the combobox list by the minifier

Batch 14 (S1) moved `.z-selectbox::picker-icon { background-color: CanvasText }` into `tokens/_forced-colors.css` as its own rule `(2e-cont)`. The served `norm.css.dsp` shows the minifier merged it into the selector list of (2e). A browser that does not recognise `::picker-icon` would drop the whole list, including the combobox caret. Every browser Marble targets today supports it.

- [ ] If an older browser must be supported, keep the two rules in separate blocks the minifier cannot merge.


## 27. ConfirmPopup preview samples sit closer to their labels

Batch 17 (#75) moved the top/left arrow room from `margin-bottom` / `margin-right` to a negative `margin-top` / `margin-left`, because ZK's popup positioning reads only the latter. The static placement samples on `confirmpopup.zul` carry the same `z-confirmpopup-placement-*` classes, so the top samples moved up 8px: the gap to their caption (INFO, TOP, …) shrank from about 13px to about 5px. Live popups are correct; this is preview-page layout only.

- [ ] Add spacing to the preview's static samples if the tighter caption gap reads badly.

## 28. Checkmark Listbox data rows are taller than Grid rows

**Resolved** in zk `642266f933` (`listbox.css`, guards) and zkcml `5a82b3171` (`tablet/_mesh.css`): the rows are
52 / 32 / 60px, equal to Grid, and the glyph is centred on the label. The tablet Tree checkmark row had the same
excess (64px against 60px) and is fixed by the same zkcml rule. `vertical-align: top` left the box at the top of a
row whose first cell holds a taller control (a textbox or a button): 10.5px (textbox) and 8.5px (button) above the
control centre on desktop (6 / 5px in compact density, measured), 14.5 / 12.5px on a tablet. It is replaced by `vertical-align: middle` with negative block
margins (zul `listbox.css`, zkcml `tablet/_mesh.css`), which keep the rows at Grid height and the box within 1px of
the label and of the control, and guarded for Listbox and Tree in default, compact and tablet density. The text below
is the original finding.

**Reworked (NS13 F1)** in zk `aa8198f3d1` and zkcml `e3bca9748`: the negative margins above only fitted Inter 13px/20px
(off by up to 2px under a 16px/24px type scale, 2.9px on a tablet, 1.75px next to a control under Arial, 1.6px at a
device pixel ratio of 1.25). The first cell of a checkmark Listbox row is a flex row with `align-items: center`, as the
Tree cell content already was, the span and its icon are flex containers, and the tablet 24px box gets the symmetric
margin `(line-height - touch-icon-size) / 2` from the tokens. The guard measures the painted box off a screenshot and
runs under the default, a 16px/24px and an Arial type scale in all three densities. Correction to the `894fc2c6ba`
message: the pre-fix offsets of the box above a textbox and a button re-measure 0.5px lower, 10 / 8px on desktop and
14 / 12px on a tablet (it says 10.5 / 8.5 and 14.5 / 12.5).

**First-cell spacing (NS14, D18)** in zk `7f6cf9852a`: the flex row of `aa8198f3d1` trimmed the whitespace around each
child of the first cell (`<listcell image="…" label="…"/>` showed the image touching the label, `<label value="Alice "/><a
label="link"/>` showed "Alicelink"). `listbox.css` adds `gap: var(--zk-spacing-1)` to the cell content, a 4px shorter
end padding on `.z-listitem-checkable` and a `-4px` start margin on `.z-listitem-checkable + img`, so the text and the image
stay on the header keyline and the rows keep Grid height. One rule beyond the owner's three: a centred or end-aligned
column keeps the symmetric 12px padding, as the shorter padding moved the box 2px from the header box
(`.mhf-listbox-select-all-centre`, tablet). Side effects accepted by the owner, and stacked block content unsupported:
`doc/contracts/listbox.md`, "Checkmark column". Guards: `registerCheckmarkFirstCell` in `mesh-header-footer.spec.ts`.

**First-cell spacing, round 2 (NS14, D18)** in zk `b535d283aa`: the verifier measured that the extra rule for centred and
end-aligned columns moved the box of rows with an image, a `<label>` plus a link or a `label=` text 2px (centred) or 4px
(right-aligned) left of the pre-NS13 position. The rule is narrowed to `.z-listitem-checkable:last-child`: the
checkbox-only cell stays under the header box, an image or component cell is back within 0.5px of pre-NS13. A `label=` cell
is indistinguishable from a checkbox-only one in CSS (they differ by a text node) and keeps the 2px / 4px shift, accepted to
protect the common narrow checkbox column (`doc/contracts/listbox.md`, rule 4). New desktop and tablet guard:
`registerCheckmarkBoxInAlignedFirstColumn` (cm11, cm12).

Found while verifying zkdemo NS10 (`doc/zkdemo-b6-marble-fixes.md`, D16); deferred under O10.

- Measured: 53.45 / 33.45 / 61.95px (desktop / compact / tablet) against Grid 52 / 32 / 60.
  IceBlue 10.3.1 shows no difference (33px with and without checkmark), so Marble caused it (O1).
- Cause: `.z-listitem-checkable { vertical-align: middle }` on an inline span grows the line box,
  the same mechanism as the D16 header. On tablet, zkmax `tablet/_mesh.css` also gives
  `.z-listitem-checkable` a 24px font-size, width and height (the row touch box).
- A/B: desktop `vertical-align: top` gives 52 and centres the glyph on the label. Tablet needs
  `display:inline-block; vertical-align:top; height:20px` to reach 60, which leaves the glyph 3px
  below the label centre — decide first whether the 24px touch box is still wanted on tablet.
- Reproduce: `/web/pv/mesh-header-footer-guard.zul`, compare `.mhf-listbox-vs-grid tr.z-listitem`
  with `.mhf-grid-vs-listbox tr.z-row`.

## 29. `verification.md` quotes a stale gallery tolerance and PNG count

**Resolved** in zk `c128d70b36` (0.002, 188 compared PNGs plus 99 forced-colors captures). The text below is the
original finding.

`gallery-scan.spec.ts` uses `maxDiffPixelRatio: 0.002` (0.2%), while
`.claude/skills/marble-theme/reference/verification.md` still says 0.01 and "184 compared PNGs".
Update both from the spec and a fresh `ls` of `zkpreview/doc/screenshots/`.

## 30. Narrow header columns with `menupopup` on tablet

**Resolved** in zk `c128d70b36` and `73203f131d`: (a) the
minimum width of an image-only column with the menu button, **56px desktop / 76px tablet**, is documented in
`doc/contracts/listbox.md` (and referenced from `grid.md`, whose Grid has the same image rule) and guarded on both;
(c) the minimum width of a select-all-only column with the menu button is documented in `listbox.md` only (Grid has
no select-all column) and guarded on both; (b) the tablet guard for image + sort + menu headers is in zk
`642266f933`. (c) first said 60px desktop / 80px tablet; the NS13 verifier found that the box paints only 8px there,
and a fresh measurement (DOM and painted pixels) gives **68px desktop / 88px tablet**, which the guard now asserts
through the icon's right edge against the content box's right edge. (c) was first planned as "hide the button on that header"; CSS cannot tell that header from a
labelled one (no class or attribute differs), so it follows the same rule as (a): unsupported widths are
documented and guarded, not engineered. (The first 30a numbers, 58 / 78px, came from resizing a rendered column
and were 2px too wide; a fresh render gives 56 / 76px.) The text below is the original finding.

Found while verifying the tablet menu-button reservation (zkcml `7680717b6`, zkdemo NS10); deferred
under O10. The 44px touch button needs 56px of padding-right (68px with a sort arrow), so:

- An image-only column with `menupopup` loses its icon on tablet below about 76px wide (desktop: about
  56px). Repro: `<columns menupopup="auto"><column width="60px" image="/web/img/received.png"/>`;
  tablet shows a 0px image.
- Tablet has no guard for image + sort + menu headers (`GRID_IMAGE_MENU`, `LISTBOX_IMAGE_MENU` run on
  desktop only); measured correct today (68px, label 157–181, sort 185–197, button 201–245).
- Older than the reservation: a 40px select-all-only listheader with `menupopup` already clips the box
  on desktop (padding-right 36px, content 0), and on tablet the 44px button overflows the cell by 8px.

## 31. Forced-colors buttons are 2px taller

Found while verifying NS9 (`doc/marble-ns9-plan.md`); deferred under O10. Under
`forced-colors: active`, `_forced-colors.css` gives `.z-button` a 1px border that the filled
variant does not have otherwise, so filled buttons grow by 2px (not measured per density yet).

## 32. `.z-carousel-indicator` has no font-family

NS9 item 1 gave `.z-carousel-arrow` the theme font; the indicator dots are the same bare `<button>`
and still fall back to the UA font. No text is drawn today, so nothing is visible.

## 33. `_sizing.css` cites a file that does not exist

A comment in `zul/src/main/resources/web/zul/css/tokens/_sizing.css` says the compact values
"mirror doc/marble-compact.css", which is not in the repo. Point it at
`doc/spec/data-dense-mode.md` or drop the reference.

## 34. Compact Tree rows are 36px against 32px Grid rows

Found while measuring item 28: in `data-density="compact"` a Tree row (with or without a checkmark) is 36px and a
Grid or Listbox row 32px (`--zk-data-row-min-height: 36px` in `tokens/_sizing.css`). Default (52px) and tablet
(60px) rows are equal across the three. Not caused by the checkmark; left as found (O10).

## 35. A header that holds a control lets a long label paint under the sort arrow and the menu button (M2)

Found by the NS13 verifier. After zk `eb46b0656c`, a Grid, Listbox or Tree header that holds a native control
(`button`, `a`, `input`, `select`, `textarea`) has `overflow: visible` on its content, so the label no longer ends in
an ellipsis before the sort arrow and the menu button.

- Repro (desktop, Inter 13px): a Listbox with
  `<listhead menupopup="auto"><listheader width="160px" sort="auto" label="A very long header label"><button label="Go"/></listheader><listheader label="Other"/></listhead>`,
  measured from the left edge of the header cell, in three steps:
  1. Not sorted, not hovered: the content is 16-112px, the label text runs to 178.5px, the sort arrow has no width (it
     is a 0px box at 128px) and the menu button is at 132-156px.
  2. Sorted: add `sortDirection="ascending"` to the `listheader`. Content and text are unchanged, the arrow is at
     116-128px, so the label paints under the arrow and under the button.
  3. Not sorted, hovered: the same as step 1, the arrow stays 0px wide.
  Grid (`<columns menupopup="auto"><column … sort="auto" label="A very long header label"><button/></column>`) gives
  the same numbers. A Tree header (Treecols has no `menupopup`) has the content at 16-140px, the text to 178.5px and the
  arrow at 144-156px when sorted, so the label paints under the arrow only.
- Accepted trade-off of `eb46b0656c`: a whole focus ring in every browser, Safari included, is worth more than this
  narrow combination (control, long label and sort or menu button in one header), and it matches the state before D11,
  when these headers had no ellipsis either.
- Option to evaluate later: `@supports (overflow-clip-margin: 4px) { overflow: clip; overflow-clip-margin: 4px; }` on
  the same `:has()` rule, which keeps the ellipsis and the ring on Chromium and Firefox, and leaves `overflow: visible`
  elsewhere (Safari has no `overflow-clip-margin`). The first attempt with `overflow: clip` everywhere (`6bde2fb120`)
  was replaced because it left the ring cut in Safari.

## 36. The Listbox toolbarbutton focus ring is covered by the neighbouring menubar (L1, pre-existing)

On `listbox-header`, the bottom 4px of the `.z-toolbarbutton` focus ring is covered by the adjacent `.z-menubar`
(`position: relative; z-index: 100`, `menu.css:66`). It is an overlap and not a clip, so `focus-ring-scan` cannot see
it. It is older than ZK-6112 and was left as found (O10).

## 37. Gaps in `focus-ring-scan` (L3, pre-existing)

The scan (`focus-ring-scan.spec.ts`, `measureClipping` and `clippingCandidates`) does not model:

- the standalone `translate`, `rotate` and `scale` properties as creating a containing block (only `transform`,
  `perspective`, `filter`, `contain` and `will-change` are);
- `contain: paint` and `clip-path` as clippers;
- the padding box: it measures against the border box, so a border thicker than 0 hides a clip;
- `overflow-x: clip` with `overflow-y: visible`: the y axis is still checked, as the scan skips an ancestor only when
  both axes are `visible`.

Left as found (O10).

## 38. A Tree row whose first cell holds a control is shorter than the Grid row (pre-existing)

Found while measuring item 28 (NS13 F1), with or without a checkmark: a Tree row with a control in the first cell is
shorter than the Grid row with the same control.

- Measured, desktop: a textbox row is 56.5px in a Tree against 72.5px in a Grid, a button row 52.5px against 68.5px.
  In compact density 40.5 / 38.5px against 44.5 / 42.5px; on a tablet both are 88.5 / 84.5px and equal. Rows with only
  text are equal (52px).
- Reproduce: `/web/pv/mesh-header-footer-guard.zul`, compare `.mhf-tree-control-row .mhf-textbox-row` (and
  `.mhf-button-row`) with `.mhf-grid-control-row .mhf-textbox-row`, or build a `<tree>` and a `<grid>` whose first cell
  holds `<textbox value="Alice"/>` or `<button label="Go"/>` and compare `tr.z-treerow` with `tr.z-row`.
- Not touched by the NS13 F1 rework: the checkmark Tree row keeps the height of a Tree row without a checkmark.
  Left as found (O10).

## 39. A checkmark Listbox row that starts with a `<label>` starts 3.7px left of the header text (pre-existing)

Found while measuring item 28 (NS14), the same at `aa8198f3d1` and with the gap fix, and the same before NS13 (NS14 round 2 verifier).

- Measured, with `7f6cf9852a`: the first glyph of `<listcell><label value="Alice "/><a label="link"/> tail</listcell>`
  is 3.72px (Chromium), 3.73px (Firefox) and 3.00px (WebKit) left of the first glyph of the header label, at 13px and at
  16px type. A cell with `label="Alice link tail"` is on the header glyph (-0.06px). Before NS13 the offset is the same:
  DOM -3.72 (Chromium), -3.73 (Firefox), -3.00 (WebKit); on the PNG -4.5px at 13px and -4px at 16px.
- Cause: ZK renders both the header label and a `label=` cell as `&nbsp;` followed by the text, so their glyphs start one
  space (3.66px) after the span, while a `<label>` component has no `&nbsp;` in front of it.
- Reproduce: `/web/pv/mesh-header-footer-guard.zul`, compare the first glyph of `.mhf-listbox-first-cell .mhf-inline-row
  .z-listcell-content` with `.mhf-listbox-first-cell th.z-listheader .z-listheader-content` (a Range from the first character
  that is not a space), or build a checkmark `<listbox>` whose first cell is `<label value="Alice "/><a label="link"/>` next
  to one with `label="Alice"`.
- Not fixed: outside NS14, left as found (O10).

## 40. Listbox group rows are taller than Grid group rows (unverified, found in NS17)

Seen in passing by the NS17 implementer while measuring item 4 of NS17 (last-row heights); not investigated.

- Measured on an NS17 probe page (deleted): a Listbox `<listgroup>` row was 52.5px on desktop, 52.5px in compact and
  64.5px on tablet, while a Grid `<group>` row was 37px on desktop and 41px on tablet. The probe's Listbox may have used
  `org.zkoss.zul.listbox.groupSelect=true` (a checkbox in the group row), which could account for part of it.
- Reproduce: a `<grid>` with `<group label="G"/>` and rows next to a `<listbox>` with `<listgroup label="G"/>` and
  listitems, with and without `checkmark="true"` and groupSelect; compare `tr.z-group` with `tr.z-listgroup` in desktop,
  compact (`data-density="compact"` on `<html>`) and tablet.
- Next step: confirm the numbers on a clean page before deciding whether the two group rows should match (V6).

## 41. The Listbox group checkbox (groupSelect) has no box (found in NS17 verification)

Seen by the NS17 verifier on a probe page; not investigated and not compared with IceBlue yet.

- With `<custom-attributes org.zkoss.zul.listbox.groupSelect="true"/>` on a `multiple="true" checkmark="true"` Listbox,
  each `<listgroup>` renders `SPAN.z-listgroup-checkable.z-listgroup-checkbox` inside its first `.z-listcell-content`
  (`Listcell.ts` `_colHtmlPre`). On desktop and tablet it paints as a bare small check glyph with no box, and a selected
  group looks the same as an unselected one.
- `listbox.css` has no rule for `.z-listgroup-checkable` / `.z-listgroup-icon` (the only one, which never matched, was
  removed in `ea65737cc4`); only zkmax `tablet/_mesh.css` sizes it.
- Next step: reproduce on a clean page, compare with IceBlue (O1), then decide whether it should share the listitem
  checkbox look (V6).

## 42. Forced colours: the horizontal separator bar disappears (regression, NS18)

Found while reviewing the refreshed forced-colors screenshots (`704262d610`). Not fixed: outside NS18 (O10).

**Resolved** in zk `bb04fc90aa` (`_forced-colors.css`, guard in `forced-colors.spec.ts`): under forced colours the bar is
a centred 1px `::before` filled with `CanvasText`; the normal-mode gradient is untouched. Screenshot re-cut in `8b34ed6652`.
The text below is the finding as filed.

- `separator-forced-colors.png`, y≈159–261, x 32–521: the "Default spacing" and "Large spacing (20px)" bars paint no
  pixel between "Above" and "Below". The 2026-09-11 capture showed solid (too thick) bars.
- Cause: `0b331d3db0` (2026-10-10) draws `.z-separator-horizontal-bar` as `background: linear-gradient(CanvasText,
  CanvasText) center / 100% 1px no-repeat` (`separator.css` and `_forced-colors.css` ~line 386). Under forced colours
  Chromium computes `background-image: none` and a transparent `background-color`. The vertical bar uses
  `background-color: CanvasText` and still shows.
- Possible fix: a 1px pseudo-element or border painted with `CanvasText`, or `forced-color-adjust: none` on this rule
  (re-measure either way). Check whether `forced-colors.spec.ts` should guard it.

## 43. Forced colours: the selected accordion tab chevron is black on Highlight (regression, NS18)

**Resolved** in zk `82677c0c02` (`tabbox.css`, guard in `forced-colors.spec.ts`): the selected tab's chevron is painted
`HighlightText`. Screenshot re-cut in `8b34ed6652`. The text below is the finding as filed.

- `tabbox-forced-colors.png` y≈3965–4012 (chevrons at about x 604 and x 1223), `tabbox-misc-forced-colors.png` about
  x 276–290, y 812–824: the chevron of the selected accordion tab is black on rgb(55,51,109), about 1.9:1. The
  2026-09-11 capture showed it in HighlightText.
- Cause: `1dda0e855d` (2026-10-09) moved the chevron to a mask on `.z-tabbox-accordion .z-tab-content::after`; the
  forced-colours block in `tabbox.css` (~lines 712–716) paints it `CanvasText` in every state.
- Possible fix: `.z-tabbox-accordion .z-tab.z-tab-selected .z-tab-content::after { background-color: HighlightText; }`.

## 44. Forced colours: pre-existing defects seen in the NS18 review

Same in the 2026-09-11 and the refreshed captures; not regressions. Left as found (O10).

- **Listbox paging icons nearly invisible** (`listbox-header`, y 3390–3426, x≈490–680): `listbox.css` ~line 723
  `.z-listbox-paging-bottom .z-paging-icon` paints the mask on the element itself, not on `::before`, so the
  `_forced-colors.css` (2d)/(2e) rules miss it (live background `rgba(255,255,255,.6/.38)`). Present since
  `2100200284`. The `grid-paging` and `paging` pages are fine.
- **Disabled paging buttons look enabled** (`paging`, "first page / last page" sample, y≈460–500): (2d) paints every
  `::before` `CanvasText`; there is no `GrayText` for the disabled state.
- **Caption text in a selected accordion tab is black on Highlight** (`tabbox-misc`, "search" tab, y 812–830, x 80–150,
  about 1.9:1): the (2a) backplate covers `.z-tab-text` but not a `.z-caption` inside the tab.
- **Invalid daterangebox looks like the default one**: other inputs show a double border when invalid; daterangebox
  keeps a single border.
- **tbeditor toolbar icons are grey, not CanvasText** (about 5.7:1, still legible; thinner since the 14px icons of
  `829a6952f`).
- **An invalid combobox draws its button as a separate double-bordered box** (cosmetic).
