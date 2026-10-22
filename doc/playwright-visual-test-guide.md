# Playwright Visual Test Guide

> **Status:** how to run the Marble Playwright suite and the rules its specs follow. Updated 2026-10-20.
> This file was `doc/test-architecture.md`; the architecture overview (layers, counts, coverage matrix,
> who triggers what) is now [marble-test-architecture.html](marble-test-architecture.html), which carries
> the current numbers. Do not copy counts into this file — they go stale.

## 1. Two layers

| Layer | What it is | Where it's documented |
|-------|-----------|------------------------|
| **A. Runnable test suite** (Playwright) | Automated checks against the live preview app: visual baselines, computed-style guards, smoke, framework classes, reset scoping, tablet, popups, brand and density. | [marble-test-architecture.html](marble-test-architecture.html), this guide |
| **B. AI verification harness** | The human+agent loop that authors and signs off component design: contracts, evaluator/generator agents, dual gates. | [harness-decisions.md](../.claude/skills/marble-theme/reference/harness-decisions.md), [harness-playbook.md](../.claude/skills/marble-theme/reference/harness-playbook.md), [zk-component-rules skill](../.claude/skills/zk-component-rules/SKILL.md) |

Layer B decides whether a component "looks right"; Layer A keeps it from silently regressing.

## 2. Configuration

Config: [zkpreview/src/test/playwright/playwright.config.ts](../zkpreview/src/test/playwright/playwright.config.ts).
The project list lives there; run `npx playwright test --list` for the current test inventory.

- `baseURL` is `${PREVIEW_URL}` (default `http://localhost:8085`); the preview app must be running.
- `snapshotDir` is `zkpreview/doc/screenshots`, snapshot path `{snapshotDir}/{arg}{ext}`.
- **Flat layout:** one file per (page, scenario) named `<page>-<scenario>.png`, e.g. `button-gallery.png`,
  `button-default-hover.png`, `button-tablet.png`. Each spec passes its snapshot name as one hyphenated
  string. Desktop and tablet are told apart by the filename suffix.
- Section shots are `<page>-NN-<section>.png`; forced-colors review images are `*-forced-colors.png`.

## 3. How to run

Start the preview app first (it is the prerequisite of every project), see
[.claude/skills/marble-theme/reference/verification.md](../.claude/skills/marble-theme/reference/verification.md):

```bash
cd zkpreview && ./start.sh                 # http://127.0.0.1:8085

# From zkpreview/:
npm run screenshot:test                    # the whole suite
npm run screenshot:update                  # playwright --update-snapshots
npx playwright test -c src/test/playwright/playwright.config.ts --project=<project>
npx playwright test -c src/test/playwright/playwright.config.ts -g "button"
node scripts/select-tests.mjs --base HEAD  # which tests a change needs (scripts/README-select-tests.md)
```

Use `screenshot:update` only for an intentional visual change. For a change you expect to be
pixel-equivalent (a token extraction, an equal-value swap) a diff is a bug in the change, not a stale
baseline: investigate it, never accept it. Prefer `--update-snapshots=changed` and look at every image.
Before running, read "Which tests to run after a change" in `verification.md`.

CSS-quality gates (not Playwright):

```bash
npm run lint:css                                      # stylelint (repo root)
.claude/skills/marble-theme/scripts/audit-css.sh      # orphan tokens, hardcoded colors, duplicate rules
scripts/check-icon-coverage.sh
```

Other tooling: `.claude/skills/zk-component-rules/tools/check-framework-classes.mjs` (framework-class
compliance across themes); `scripts/render-iceblue-baseline.sh <component...>` renders an IceBlue
reference image for comparison.

## 4. Capture rules

- **Stateful shots use `padShot()`, not an edge-tight `toHaveScreenshot(el)`.** Any state that paints
  outside the element box (a hover `box-shadow`, a focus ring) must be captured with the element plus a
  12px margin. An edge-tight shot clips it, so the baseline silently omits the effect and a later
  regression cannot be caught. (Button hover elevation went uncovered for this reason.)
- **Action target and capture target differ for wrapper-styled inputs.** bandbox, datebox, timebox and
  spinner paint the hover border and `:focus-within` ring on the wrapper, so act on the focusable inner
  `<input>` and capture the bordered wrapper. Capturing the transparent inner input gives blank baselines.
- **Gallery pages and variant pages** both shoot the `.z-p-8` wrapper; dynamic states target a bare
  `.z-*` element (`.z-textbox`, `.z-combobox-input`, ...).
- **Reusable state arrays:** `hoverFocusStates` (hover, focus) and `buttonDynamicStates` (adds `active`
  via `mouse.down()`); new form controls are added to the `FORM_CONTROL_STATES` table at the foot of
  `screenshot.spec.ts`.
- **Popups:** open them with the gesture a user makes, assert exactly one visible match, park the cursor
  after the trigger. The full rules are in the header comment of `popup-gallery.spec.ts`.
- **Tolerances** are absolute pixel budgets per shot, not ratios; see
  [screenshot-tolerance.md](spec/screenshot-tolerance.md) and the constants at the top of each spec.

## 5. Which pages get a gallery shot

- `gallery-scan.spec.ts` discovers pages by scanning `zkpreview/src/main/webapp/web/*.zul` at collection
  time, so a new preview page is covered the moment it is added.
- Pages in its `SKIP` set (aggregate pages, non-visual primitives, hardware or animated pages) are
  excluded on purpose; the `Set` in the spec is the authoritative list, tune it there.
- `COVERED_ELSEWHERE` lists the components whose depth blocks live in `screenshot.spec.ts`, which is a
  hand-maintained list: a new component needs a manually added `test.describe()` there to get state shots.
- `unstable` sections (carousel autoplay, `sizedByContent` demos) are skipped per section; see the
  comments in `gallery-scan.spec.ts`.

### Making a non-deterministic page stable: calendar

ZK bakes the server's "now" into a `<calendar>`'s default `value` (month and selected-day highlight) and
evaluates `constraint="no past/future"` greying against the real clock, so freezing only the browser clock
does not help. `calendar.zul` pins every `<calendar value="${fixedDate}">` and the datebox to a fixed
**past** date (2020-03-15): a past month is unambiguously past against any real today, so the
today-highlight never shows and the greying is stable. `portallayout.zul` uses the same idiom. Apply the
same idea elsewhere (stub media for `video` and `audio`, etc.). Canvas, embedded-document and virtualised
pages (`signature`, `cropper`, `pdfviewer`, `biglistbox`, `barcode`) carry higher cross-machine flake risk:
if a run flags one, add it to `SKIP` or raise its tolerance.

## 6. `doc/screenshots` is shared with evaluator evidence

A Playwright baseline and a manual evaluator artifact can sit in the same directory. The Playwright
baselines are the `.png` files the specs write. The files written by the `zk-theme-evaluator` agent are
referenced evidence, **not** test baselines, and must not be pruned: `cascader-page.gif`,
`cascader-popup-open.gif`, `coachmark-gallery.gif`, `goldenlayout-page.gif`, `portallayout-gallery.gif`,
`slider-page.gif`, `splitter-page.gif`, `linelayout-eval-results.json`, `linelayout-eval-results-h.json`
(measured 2026-10-20; list the non-`.png` files to see the current set).

## 7. Related docs

- [marble-test-architecture.html](marble-test-architecture.html): layers, coverage matrix, who runs what
- marble-test-gaps.md (kept locally under `tasks/`, not in the repository): open gaps and progress
- marble-test-infra-conclusion.md (kept locally under `tasks/`, not in the repository): selector rules, baseline run, measurements
- [spec/index.md](spec/index.md): normative design specs (what it must look like)
- [harness-decisions.md](../.claude/skills/marble-theme/reference/harness-decisions.md), [harness-playbook.md](../.claude/skills/marble-theme/reference/harness-playbook.md): the agent loop
