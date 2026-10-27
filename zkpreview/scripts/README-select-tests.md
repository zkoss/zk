# select-tests: which Playwright tests do my changes need?

`select-tests.mjs` takes changed files and answers `full`, `partial` (with exact commands) or `none`.
The rules it implements are in `doc/marble-selective-test-rules.md` (section 3 and 4).
Run everything from `zkpreview/`. Node only, no extra npm dependencies, no preview server needed
(the test inventory comes from `playwright test --list`).

## Usage

```bash
# explicit paths
node scripts/select-tests.mjs zul/src/main/resources/web/js/zul/wgt/css/button.css

# everything changed since a revision, uncommitted and untracked files included
node scripts/select-tests.mjs --base HEAD --zkcml-base HEAD

# only these paths, each diffed against the base (changed rules only, not the whole file)
node scripts/select-tests.mjs --base <rev> --only zul/src/main/resources/web/js/zul/wgt/css/button.css

# machine-readable
node scripts/select-tests.mjs --json <paths>
```

Accepted path forms:

| form | meaning |
|---|---|
| `zul/src/...`, `zkpreview/src/...`, `doc/...` | relative to the zk repo root |
| `zkcml:zkmax/src/...` | relative to the sibling `zkcml` repo (`../zkcml` of the zk repo) |
| `/abs/path/...` | absolute, under either repo |

`--threshold F` overrides, for one run, the share of all tests above which the answer is `full`
(a fraction, `0 < F <= 1`; default `2/3`, e.g. `--threshold 0.5`).

Output: `mode`, `reasons`, `pages`, `uncovered`, `commands`, and the selected/total test counts. With `--json`
the object carries `selectedTests`, `totalTests` and `tests` (selected test count per Playwright project). Commands are meant to be run from `zkpreview/`;
set `PREVIEW_URL` when the preview server is not on 8085.

A partial run is ONE command, `npx playwright test -c src/test/playwright/playwright.config.ts [--fully-parallel]
--project=<p> ... -g '<regex>'`: every needed project is repeated as `--project`, and a single `-g` regex is the
alternation of anchored title paths (`^(?:<project> <file> <describe...> <title>|...)$`; a whole project or file is
`<project> .*` / `<project> <file> .*`). The selector checks, against the `--list` inventory, that the regex selects
exactly the intended tests. A full run is the bare `npx playwright test -c ...`.

`--fully-parallel` is added when no selected spec file is in `UNSAFE_PARALLEL_SPECS` (top of `select-tests.mjs`);
`reasons` says which case applied. The constant is empty: the audit (doc/marble-test-phase1-selector.md section 11.2)
found no spec that depends on test order or shared state in the way the printed command runs it. Two specs are
order-dependent only in update mode, which the command never enables: `font-size-equivalence.spec.ts`
(`UPDATE_FONT_BASELINE=1`, serial) and `focus-ring-scan.spec.ts` (`FOCUS_SCAN_UPDATE=1`, read-modify-write of one
JSON). Never add either variable to a command that carries `--fully-parallel`. Add a spec to the constant when it
gains `describe.serial`, a `beforeAll` its tests rely on, or a module-level object its tests write.

## How a path is resolved

0. A changed `.css` file whose content differs from its `--base` version only in `/* … */` comments or whitespace
   is treated as "no tests needed" (reported as "comment/whitespace-only change, rendered CSS is unaffected"). This
   check runs before the full-run triggers in step 1, and applies to every `.css` file, including token/base/utility
   CSS and `_layer-order.css`. Without `--base` (bare paths) there is nothing to compare, so the usual path rules
   still apply. Non-CSS triggers (`.wcs`, `lang.xml`, gradle, fonts) are unchanged. Implementation:
   `isCommentOnlyCssChange` in `select-tests.mjs`.
1. Full-run triggers: token/base/utility CSS (except comment/whitespace-only changes, see step 0), `_layer-order.css`
   (same exception), any `.wcs`, `lang.xml`/`lang-addon.xml`, CSS build scripts, `*.gradle`, font files,
   `playwright.config.ts`, `package.json`/lockfiles.
2. No-test paths: `doc/**`, `*.md`, `.claude/**`, `tasks/**`, `logs/**`, `node_modules`, `zkpreview/scripts/**`,
   `*-forced-colors.png` (review artifacts).
3. Component CSS (`js/<module>/<pkg>/css/*.css` in zul, zkmax, zkex; `zkmax/css/tablet/*.css`): the `.z-*` classes
   to look at (see "Which rules are read" below), then per class:
   present on a page at rest -> those pages; else the longest present name prefix (`z-datebox-popup` -> `z-datebox`);
   else the pages of the file's own component (`combobox.css` -> `z-combobox`); else it is reported in `uncovered`
   and the result is `full`. `misc.css` is shared and always `full`. Two rules apply on top of that, for every class:
   * **Own page.** `<name>.css` always selects the top-level page `<name>.zul` when it exists, and with it every
     test that opens it (including the `popup` project). The same holds for the widget `<Name>.ts`.
   * **Owner pages.** The owner of a class (its longest present name prefix, else the CSS file's component) always
     contributes the pages where the owner's root class is present, even when the class itself is also on a page
     at rest.
4. Preview `.zul` -> that page; a `pv/` template -> every page that applies it, transitively. Aggregate pages are the
   exception, see "Aggregate pages".
5. Spec file -> that spec only. Baseline PNG -> the page named by its file name; a gallery baseline
   (`<page>-gallery.png`, the sectioned `<page>-<NN>-<slug>.png`, or the slug-less
   `<page>-<name>-<NN>.png` such as `window-overlapped-00.png`) -> only that page's desktop gallery tests. `<page>-compact-<NN>-<slug>.png` and `<page>-brand-<NN>-<slug>.png` -> only that page's `compact-gallery` / `brand-gallery` test (`variantGalleryOfBaseline`). `zkpreview/doc/*.json` -> the specs reading it.
6. Widget source (`<Name>.ts`) -> pages containing `z-<name>`.
7. Anything else -> `full`.

More than 2/3 of all tests selected (rounded up: 897 of 1,345) also gives `full`. The total is the
`playwright test --list` inventory.

## Aggregate pages

`aggregate-pages.json` lists the pages that combine many components (`inputs`, `overview`, `preview`;
each component also has its own page). Their tests are selected only when that page's own
file changes: its `.zul` or its baseline PNG. A CSS rule, a class, a widget, or a `pv/*-content.zul` template change
never selects them (`reasons` lists the aggregate pages left out). They are left out of the class -> pages map, so a
class that appears only on an aggregate page falls through to the name-prefix and own-component steps. A full run is
unchanged. `gallery-scan.spec.ts` keeps its own `SKIP` list; it is not wired to this file yet (to do after the
sectioned-gallery branch merges).

## Which rules are read

With `--base` / `--zkcml-base` the selector reads only the rules a diff touches (`git diff -U0 <base>`):
the selectors of every rule whose block overlaps a changed line, in the base version and in the working copy,
so removed and edited rules both count (nested rules add their parents' selectors). The CSS is parsed with
`postcss` (already in `node_modules`). `reasons` says `parsed changed rules (N selector(s))` or
`parsed whole file (<why>)` for each CSS file. The whole file is read when:

* the input is a bare path (no diff);
* the file is new or deleted;
* the CSS does not parse;
* a changed line is an at-rule header or closing line, or inside an at-rule whose body is not a list of rules
  (`@keyframes`, `@font-face`, `@import`, `@layer a, b;`, ...). Rules inside `@media`/`@layer`/`@supports`/
  `@container`/`@scope` are attributed to the inner rule.

Changes that touch only blank lines or comments select nothing. Changed rules without any `.z-*` selector
(`:root`, element selectors) give `full`.

## Keeping it current

* **Build the index from a server with client ROD off**, and run the tests against a server with the default:
  ```bash
  cd zkpreview
  tail -f /dev/null | JAVA_TOOL_OPTIONS=-Dorg.zkoss.zul.client.rod=false withjdk.sh 17 bash ./gradlew appRun -PhttpPort=<port> --console=plain
  node scripts/build-class-index.mjs --url http://127.0.0.1:<port>
  ```
  With `org.zkoss.zul.client.rod=false` the combobox, bandbox, datebox, menu and popup pop-ups are rendered at page
  load, so more pop-up classes are present at rest and map straight to their pages (measured in
  doc/client-rod-probe.md: no page loses a class). Never run the Playwright tests against that server: a Tree
  renders without its ROD spacers and `tree-header` fails by about 16,000 pixels. Stop that server before the tests.
* `zkpreview/doc/class-index.json` is the page -> classes index, keyed by URL path (`a.zul`, `pv/input-invalid-guard.zul`,
  `utility/print.zul`). It covers every top-level page plus every `.zul` a spec names as a string literal URL
  (`'/pv/...zul'`; paths in comments and titles do not count). Rebuild it after a full run, with the preview server up:
  `node scripts/build-class-index.mjs --url http://127.0.0.1:8085`. It fails (and writes nothing) if any page errors.
  Rebuild it whenever a spec starts navigating to a new fixture page.
* `component-theme-variables.spec.ts` opens its page from the test title (`/${PAGES[comp] ?? comp}.zul`). `computedUrlPage`
  in `select-tests.mjs` parses its `PAGES*` maps and applies them to the title; it is the only place that knows this.
  Comments in a spec are ignored when looking for the pages a test opens.
* The page -> test mapping is one function, `buildTestsForPage` (and `titleNamesPage` beside it) in `select-tests.mjs`.
  Gallery baselines are recognised by `galleryPageOfBaseline` and `isGalleryTest`; if the gallery test titles change, adjust those.
* Each generated `-g` pattern is checked against the inventory before it is printed; the script throws if it would
  select any test other than the intended ones.
