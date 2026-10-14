# How the harness can ignore sub-pixel screenshot differences

Question (2026-09-14): diffs 02 / 05 / 06 in the Marble screenshot-difference review pack are visually
negligible. Does the project already have a way to ignore differences like these?

**The review pack itself is not in the repository.** It is a local working directory
(`tasks/marble-screenshot-diffs/`, six folders each holding `template-baseline.png`, `zkpreview.png`,
`diff.png` and zoom crops, plus its own `README.md`). Every reference to "the pack" or to `README.md`
below means that untracked directory; the findings are restated here so this document stands alone.

Answer: **yes for two of the three, no for the third** — and the images in this pack were not produced by
the gate that has the tolerance.

## 1. Where the three images came from

The pack's `README.md` states it: those images are the output of the **zero-tolerance equivalence run**
(`threshold: 0, maxDiffPixels: 0`, tools under `zkThemeTemplate/doc/migration/tools/zero-tolerance/`).
That run exists precisely to ignore nothing — it answers "did the move change a single pixel?".
No setting in `playwright.config.ts` affects it. Reading these three images as gate failures is a
category error; they are equivalence-check evidence.

The everyday regression gate is the Playwright suite in
`zkpreview/src/test/playwright/`, and it already carries tolerance.

## 2. The three tolerance knobs Playwright offers

| Option | Unit | Current use in this repo |
|---|---|---|
| `maxDiffPixelRatio` | fraction of the shot's pixels | `gallery-scan.spec.ts:81` → `0.002`; `tablet.spec.ts` per-case → `0.02` |
| `maxDiffPixels` | absolute pixel count | **not used anywhere** |
| `threshold` | per-pixel YIQ colour distance (default `0.2`) | `0.05` on the `chromium` project (`playwright.config.ts`, 2026-10-06); default `0.2` everywhere else |
| `mask: [locator]` | blanks a region before comparing | not used — the obvious fix for the date-dependent case 04 |

`playwright.config.ts` has **no** global `expect: { toHaveScreenshot: … }` block and **no** `retries`,
so every spec's own options are the whole policy. (The "re-shoot up to 3×" rule mentioned in `README.md`
belongs to the zero-tolerance tool, not to the Playwright suite.)

## 3. Per-shot verdict for this pack

| # | Shot | Owning spec / project | Tolerance in force | Allowance vs actual | Already ignored? |
|---|---|---|---|---|---|
| 02 | `progressmeter-gallery` | `gallery-scan.spec.ts` / `gallery` | `maxDiffPixelRatio: 0.01` | 1280×721 → 9 228 px allowed, 202 differ | **yes** |
| 06 | `timepicker-gallery` | `gallery-scan.spec.ts` / `gallery` | `maxDiffPixelRatio: 0.01` | 1280×478 → 6 118 px allowed, 6 differ | **yes** |
| 05 | `selectbox-focus` | `screenshot.spec.ts` `padShot()` / `chromium` | none — zero tolerance | 144×64 → 0 px allowed, 2 differ | **no** |

So the only real gap is the **state shots** taken through `padShot()` (`screenshot.spec.ts:19-29`):
every hover / focus / active capture compares at zero tolerance on a small, high-contrast crop, which
is exactly where a 1–2 px anti-aliasing flicker on a focus ring shows up.

02 additionally has a root-cause fix already in place — `gallery-scan.spec.ts:60` forces
`transition-duration:0s` because progressmeter's fill animates on first render. The state shots do not
get that treatment; `padShot` passes only `animations: 'disabled'`.

## 4. Options for closing the state-shot gap — ruled A on 2026-09-14 (chat D80)

* **A (recommended) — a small absolute floor inside `padShot` only.**
  `maxDiffPixels: 20` alongside the existing `animations: 'disabled'`. One line, scoped to the shots
  that actually flake, and an absolute count (not a ratio) is the right unit on a ~9 000 px crop where
  1 % would be 92 px — enough to hide a whole mis-rendered ring segment.
  Cost: a genuine ≤20 px regression in a state shot stops failing.
* **B — a global default in `playwright.config.ts`** (`expect: { toHaveScreenshot: { maxDiffPixels: 20 } }`).
  Fewer edits, but it also relaxes the `tablet` baselines that are deliberately at zero and the
  per-case `0.02` opt-ins, turning one explicit policy into two overlapping ones.
* **C — `retries: 2` in the config.** Re-shoots a failing test, which does clear a true flake. It does
  nothing for a stable sub-pixel difference, and it doubles wall-clock on every genuine failure.

A and C are not exclusive; A is the one that answers the question asked.

### What was applied

`screenshot.spec.ts`'s `padShot()` now passes `maxDiffPixels: 20` alongside `animations: 'disabled'`.
Nothing else changed: the `gallery` project keeps `maxDiffPixelRatio: 0.01`, the `tablet` project keeps
its zero default plus the two per-case `0.02` opt-ins, and `playwright.config.ts` still has no global
`expect` block.

Verified two ways, with the preview server down (so no state shot was re-run):

1. `npx playwright test --config src/test/playwright/playwright.config.ts --project=chromium --list`
   from `zkpreview/` — 126 tests in 1 file, the spec parses. (Run it from the **module** root:
   from the repo root npx resolves a second `@playwright/test` and every `test.describe` throws.)
2. A standalone probe of the option's semantics — a 100x100 page differing from its baseline by an
   exact pixel count, compared at `maxDiffPixels: 20`: **10 differing px passes, 30 differing px fails**
   ("30 pixels (ratio 0.01 of all image pixels) are different"). The floor absorbs AA flicker and still
   catches anything larger.

## 5. Tightened colour threshold for the `chromium` project (2026-10-06, D22-B / D24-A)

`playwright.config.ts` now sets `expect: { toHaveScreenshot: { threshold: 0.05 } }` on the `chromium`
project only. `gallery`, `tablet` and the other projects keep their own policy.

Why: Jess review batch 2 moved the listbox selected row from `rgb(213,230,255)` to `rgb(200,213,234)`.
Playwright's pixelmatch YIQ distance for that change is 0.0625, below the 0.2 default, so 41,628 changed
pixels in `listbox-gallery.png` were not counted as differences and the old colour still passed
(reproduced by injecting the old `--zk-listbox-selected-bg`). At 0.05 the same injection fails.

Limit: a colour change whose YIQ distance is below 0.05 still passes. `padShot`'s `maxDiffPixels: 20`
is unchanged and applies on top of the threshold.

## 6. What no option changes

The zero-tolerance equivalence run stays at zero — that is its purpose. When it is re-run (a move, a
refactor, a version bump), every differing pair still needs the explanation `README.md` gives each of
the six here.
