# Screenshot tolerance (`toHaveScreenshot` 容忍值)

*整理自原 Q&A 式政策文件 · 規則寫在前，理由寫在後*

> **Single source of truth.** This spec defines how much pixel difference the Playwright
> screenshot gate (`toHaveScreenshot`) accepts: which knob each spec uses, the numbers in force,
> and why. It does not cover the zero-tolerance equivalence run, which accepts no difference at all.

## Why

Screenshots of small, high-contrast crops (a focus ring, a hover state) flicker by 1–2 px from
anti-aliasing. The flicker is invisible to the eye, but on a crop of about 9 000 px it turns the
shot red from one run to the next. Without a floor, the gate fails on noise that no one can see.

## Scope: two different checks

- **Zero-tolerance equivalence run.** Threshold `0`, `maxDiffPixels: 0`. The tools are under
  `zkThemeTemplate/doc/migration/tools/zero-tolerance/`. Its purpose is to answer "did a move,
  refactor or version bump change a single pixel?", so it ignores nothing. No setting in
  `playwright.config.ts` affects it. Its re-shoot rule ("re-shoot up to 3×") belongs to this tool,
  not to the Playwright suite.
- **Everyday regression gate.** The Playwright suite in `zkpreview/src/test/playwright/`. It carries
  the tolerances described below.

Differences found by the equivalence run are not gate failures. Reading them as gate failures is a
category error, because they are equivalence-check evidence. When the equivalence run is re-run,
every differing pair still needs an explanation of why it differs. That explanation was kept in an
untracked review pack, not in this spec.

## Tolerance knobs

Playwright offers four options. The table lists the unit of each and where the repo uses it.

| Option | Unit | Use in this repo |
|---|---|---|
| `maxDiffPixelRatio` | fraction of the shot's pixels | `gallery-scan.spec.ts` → `0.002`; `tablet.spec.ts` per case → `0.02` |
| `maxDiffPixels` | absolute pixel count | `padShot()` in `screenshot.spec.ts` → `20` (see [State shots](#state-shots-padshot)); `gallery-scan.spec.ts` per-section value → `20` |
| `threshold` | per-pixel YIQ colour distance (Playwright default `0.2`) | `0.05` on the `chromium` project; `0.02` on the `compact` and `brand` projects; default `0.2` elsewhere (see [Colour threshold](#colour-threshold)) |
| `mask: [locator]` | blanks a region before comparing | not used. It can blank a date-dependent region, but the repo does not use it yet |

## Rules

### Per-spec options are the whole policy

- Each spec's own options are the tolerance in force. `playwright.config.ts` has no global
  `expect: { toHaveScreenshot: … }` block and no `retries`.
- A project may set its own `expect: { toHaveScreenshot: { threshold } }` (see
  [Colour threshold](#colour-threshold)). That is per project, not global.
- **Rejected: a global default** `expect: { toHaveScreenshot: { maxDiffPixels: 20 } }` in the config.
  It would also relax the `tablet` baselines, which are deliberately at zero, and the per-case `0.02`
  opt-ins. That turns one explicit policy into two overlapping ones.
- **Rejected: `retries: 2`.** A retry clears a true flake, but it does nothing for a stable sub-pixel
  difference, and it doubles the wall-clock time of every genuine failure. It is not exclusive with the
  state-shot floor; the floor is the option that answers the flicker problem.

### State shots (`padShot()`)

- `padShot()` in `screenshot.spec.ts` passes `maxDiffPixels: 20` alongside `animations: 'disabled'`.
  It applies to state shots (hover, focus, active) only.
- Reason: these crops are small. A selectbox focus shot is 144×64. The ring's edge is high-contrast,
  so a 1–2 px anti-aliasing flicker turns the shot red. The count is absolute, not a ratio, because
  1% of a crop of about 9 000 px is 92 px, which is enough to hide a whole mis-rendered ring segment.
- Cost accepted: a genuine regression of 20 px or less in a state shot does not fail.
- The gallery and tablet projects keep their own ratio-based policy.

Verification of this rule, recorded when it was made (not re-run since):

1. `npx playwright test --config src/test/playwright/playwright.config.ts --project=chromium --list`,
   run from the `zkpreview/` module root, lists the tests of that one file, so the spec parses. From the repo
   root, `npx` resolves a second `@playwright/test` and every `test.describe` throws.
2. A standalone probe with a 100×100 page compared against a baseline with an exact pixel count, at
   `maxDiffPixels: 20`: 10 differing pixels pass; 30 differing pixels fail with "30 pixels (ratio 0.01
   of all image pixels) are different". The floor absorbs flicker and still catches anything larger.

### Animation before tolerance

- A difference caused by animation is handled by stopping the animation, not by a tolerance.
- `gallery-scan.spec.ts` forces `transition-duration:0s`, because the progressmeter fill animates on
  first render. `padShot()` passes only `animations: 'disabled'`; it does not force transitions.

### Gallery and tablet

- `gallery-scan.spec.ts` allows `maxDiffPixelRatio: 0.002` (0.2%). On a full page that is thousands of
  pixels.
- `tablet.spec.ts` has a zero default. Two cases opt into `maxDiffPixelRatio: 0.02`: `tabbox` and
  `window`.
- The reason for the two `0.02` opt-ins is not stated in the source. 待確認.

### Colour threshold

- The `chromium` project sets `threshold: 0.05` (in `playwright.config.ts`). The `gallery`, `tablet`
  and other projects keep their own policy; the default `0.2` applies where no project sets it.
- Reason: the listbox selected row changed from `rgb(213,230,255)` to `rgb(200,213,234)`. Playwright's
  YIQ distance for that change is `0.0625`, below the `0.2` default. So 41 628 changed pixels in
  `listbox-gallery.png` were not counted as differences, and the old colour still passed. Reproduced by
  injecting the old `--zk-listbox-selected-bg`; at `0.05` the same injection fails.
- Limit: a colour change whose YIQ distance is below `0.05` still passes. The `maxDiffPixels: 20` floor
  applies on top of the threshold.
- The `compact` project (compact density) and the `brand` project (copper seed) set `threshold: 0.02`,
  tighter than `chromium`'s `0.05`.
- Reason for `0.02`: a brand test must see a container tone move from `0.92` to `0.88`. The YIQ distance
  of that change is `0.0500` for the stock seed `#376fd0` and `0.0492` for the copper seed `#b45309`
  (`0.92` → `0.90`: `0.0246` and `0.0243`). So the threshold must be below `0.0492`. At the `0.2` default,
  every screenshot passed that mutation. At `0.02`, the tabbox, toolbar and paging shots failed in both
  projects.
- The noise of the new baselines is 0 px on the machine where they were measured, so the tighter
  threshold costs nothing there. Cross-machine anti-aliasing at `0.02` is not measured. 待確認.
- The residual scan in `brand-override.spec.ts` does not use `toHaveScreenshot`, so the threshold does not
  apply to it.
- The per-section `maxDiffPixels: 20` (inherited from `gallery-scan.spec.ts`) applies on top of the
  threshold in the `compact` and `brand` projects.

### Zero tolerance stays zero

- The zero-tolerance equivalence run stays at zero. That is its purpose. No option in this spec changes it.

## 待確認

- **Gallery ratio.** One table in the source states the `gallery` project uses `maxDiffPixelRatio: 0.01`.
  The knob table in the same source, and `gallery-scan.spec.ts`, state `0.002`. This spec records `0.002`
  only. 待確認 which value is in force for the `gallery` project.
- **Tablet opt-in reason.** Not stated in the source (see [Gallery and tablet](#gallery-and-tablet)).
- **Cross-machine anti-aliasing at `threshold: 0.02`.** Not measured (see [Colour threshold](#colour-threshold)).
