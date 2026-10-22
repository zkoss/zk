# Prompt Audit — zk repository instruction surface

Run: 2026-09-29 · Method: `/claude-api prompt-audit` (`shared/prompt-audit.md`)

## Stated assumptions (Step 0)

The request carried no scope and no target model, so both were resolved from the repository and
are recorded here for correction:

1. **Scope** — the whole working directory's prompt surface: `CLAUDE.md`,
   `.github/copilot-instructions.md`, `.claude/agents/*.md` (5), `.claude/rules/*.md` (4),
   `.claude/skills/marble-theme/` (SKILL.md + 12 reference files),
   `.claude/skills/zk-component-rules/SKILL.md`. The 95 `zk-component-rules/components/*.md`
   files are reference data, not behavioural instruction, and were sampled rather than read line
   by line. A secondary section covers the **user-level** surface (`~/.claude/CLAUDE.md`,
   `~/.claude/agents/`) which loads into every session in this repo but lives outside it.
2. **Target model** — Claude Opus 5. Nothing in the repository names a model or documents a
   migration, so this falls through to the current flagship generation.
3. **No LLM application code exists in this repository.** A provider-marker sweep
   (`openai|langchain|generativeai|mistralai|ollama|gpt-4|gpt-5` and `anthropic|claude-*`) returned
   no source hits — the only matches were the substring `cohere` inside "coherence"/"cohesive".
   So Group 4 findings about request configuration, caching and sampling parameters do not apply;
   the auditable surface is instruction text only.

## Summary

The behavioural prompting in this repository is unusually clean. The classic dated patterns are
**absent**: no "think step by step", no `<scratchpad>` instructions, no assistant prefill or
JSON-forcing scaffold, no word caps or update-suppressors, no anti-formatting rules, no retired
model names, no "do not hallucinate", no grader vocabulary, no hedges on real requirements. The
four `.claude/rules/*.md` files and `zk-theme-generator.md` are exemplary and produced zero
findings. Emphasis (`MUST`/`NEVER`) appears 40 times but is almost entirely load-bearing:
single-writer role boundaries and verification gates, each with its reason attached.

**What the audit actually found is factual rot, concentrated in files imported from another
repository.** All five `.claude/agents/*.md` landed in one squashed commit on 2026-09-11
("…re-pointed at zk's layout"), and the re-point was incomplete for two of them.

The three highest-impact findings:

1. **`zk-theme-creator.md` instructs the banned token prefix.** 22 lines tell the CSS-writing
   agent to use `--md-sys-*`, which `marble-theme/SKILL.md` rule 1 bans outright and
   `md3-design-verifier.md:31` classifies as an automatic **Critical** Gate-2 failure. An agent
   following its own instructions is guaranteed to fail the gate that reviews it.
2. **`zk-theme-creator.md` describes a directory layout that does not exist.** It directs
   component CSS into `components/{layout,inputs,buttons,…}/` and tells the agent to register
   files in `zk-material.css`. Neither exists in this checkout; the real mechanism is a per-file
   `<css-uri>` entry in `lang.xml`.
3. **Eleven `npm run …` call sites invoke scripts that are not in `package.json`** — and
   `marble-theme/SKILL.md:89-91` already says so in plain text, so the skill contradicts its own
   reference files.

Counts: **13 findings** in the repository scope — Group 1 (dated prompt text) 2, Group 2 (brittle
skill files) 9, Group 3 (descriptions) 1, Group 4 (architecture) 1 — plus **4** on the user-level
surface. Ten findings carry a proposed diff; three are `flag` only.

### Checked and deliberately kept

| Surface | Why it stays |
|---|---|
| `md3-design-verifier.md:57` — `contracts authored after 2026-07-22 MUST contain …` | Date conditionals are a documented anti-pattern, but only 3 of 94 contracts carry the section. Removing the date would fail 91 legacy contracts. Load-bearing. |
| Role-boundary `NEVER` blocks in evaluator / generator / spec-author | Single-writer constraint in a parallel-agent harness, reason stated inline. Keep-list item 5. |
| `zk-theme-evaluator.md:86, 326` — inline incident narratives | These state *why the check exists* (an unstyled wheel picker shipped; a mismatch hid behind a clean failing-set), not archaeology. Keep-list item 1. |
| All four `.claude/rules/*.md` | Pure project mechanics, no restatement of trained knowledge. Zero findings. |
| `~/.claude/CLAUDE.md` — `tasks/lessons.md`, `withjdk.sh` | Both verified to exist and be in use; not unenforced instructions. |
| `zk-theme-creator.md:26-47` DOM table (as redundancy) | Duplicates `zk-component-rules/components/*.md`, but no contradiction found on spot-check. Working redundancy — keep-list item 8. Flagged only for the broken link beside it. |

---

## Findings

### High confidence

#### F1 — `--md-sys-*` token prefix taught to the CSS-writing agent
- **Location** `.claude/agents/zk-theme-creator.md:92-164`
- **Evidence** `/* Typography using --md-sys-typescale-* tokens */`, `/* Colors using --md-sys-color-* tokens */` … and an entire `## Material Design 3 Token Usage` section listing `--md-sys-color-primary`, `--md-sys-typescale-*`, `--md-sys-spacing-1`, `--md-sys-elevation-1`, `--md-sys-shape-corner-small` (22 lines carry the prefix).
- **Pattern** Group 1d — Fossils: a rule that outlived the repository it was written for.
- **Why obsolete** `marble-theme/SKILL.md:28` — "Every custom property is `--zk-*`. `--md-sys-*` is banned." `md3-design-verifier.md:31` — "any occurrence in component CSS or a contract is automatically a Critical finding." Current models follow instructions literally, so this text does not get quietly ignored; it produces exactly the CSS the review gate rejects.
- **Confidence** High — contradicted by two other files in the same surface.
- **Action** `rewrite` — see diff hunk 1.

#### F2 — Directory tree and CSS entry point that do not exist
- **Location** `.claude/agents/zk-theme-creator.md:67-86`, `:172`, `:174`, `:199`
- **Evidence** `components/ ├── layout/ ├── inputs/ …`, `zk-material.css  # Main entry point (@import all)`, "place component CSS in the correct subdirectory under `components/`", "Register new CSS files in `zk-material.css` main entry point".
- **Pattern** Group 2 — Volatile specifics (hardcoded paths that rot as code ships).
- **Why obsolete** `ls zul/src/main/resources/web/zul/css/` returns `base tokens utility zk.wcs` — no `components/`. `find . -name zk-material.css` returns nothing. The real layout and the `<css-uri>` registration mechanism are documented at `marble-theme/SKILL.md:43-68` and `reference/css-dsp.md`.
- **Confidence** High — verified against the filesystem.
- **Action** `rewrite` — see diff hunk 2.

#### F3 — Agent memory pointed at a different repository
- **Location** `.claude/agents/zk-theme-creator.md:214-259`, `.claude/agents/md3-design-verifier.md:163-208`
- **Evidence** "You have a persistent Persistent Agent Memory directory at `/Users/hawk/Documents/workspace/zkThemeTemplate/.claude/agent-memory/<agent>/`" and a transcript-search path of `/Users/hawk/.claude/projects/-Users-hawk-Documents-workspace-zkThemeTemplate/`.
- **Pattern** Group 2 — Volatile specifics; Group 1d — Fossils.
- **Why obsolete** `zkThemeTemplate` is a different checkout. Both agents already declare `memory: project` in frontmatter, so the harness owns memory for *this* project; the hand-written block sends institutional knowledge to the wrong repo and searches the wrong session logs. The other three agents (`zk-theme-evaluator`, `zk-theme-generator`, `zk-spec-author`) declare `memory: project` and carry no such block — these two are the outliers.
- **Confidence** High — paths verified; both point outside the project.
- **Action** `remove` — see diff hunk 3. Note the duplicated word "persistent Persistent" in both, further evidence of copy-paste import.

#### F4 — Eleven `npm run …` call sites for scripts that do not exist
- **Location** `.claude/skills/marble-theme/SKILL.md:107`, `:146`; `reference/css-audit.md:84`, `:92`, `:205`; `reference/important-reduction.md:107`, `:128`, `:129`; `reference/density.md:84`, `:93`; `reference/css-dsp.md:32`
- **Evidence** `npm run audit:css` (×4), `npm run lint:css` (×4), `npm run build:css` (×2), `npm run check:css-dsp` (×2).
- **Pattern** Group 2 — Volatile specifics; the skill contradicts itself.
- **Why obsolete** Root `package.json` scripts are `build build:single build:minify-css dev type-check type-check:watch lint build:doc prepublish:dts publish:dts postpublish:dts`. None of the four exist, and `stylelint` is not even a dependency, so `lint:css` has no equivalent at all. `marble-theme/SKILL.md:89-91` already states this in plain text — the reference files were never updated to match.
- **Confidence** High — verified against `package.json`.
- **Action** `rewrite` — see diff hunk 4 for the replacement table.

#### F5 — Stale `<css-uri>` count
- **Location** `.claude/skills/marble-theme/SKILL.md:37`
- **Evidence** "ZK does not discover component CSS by convention — 78 `<css-uri>` entries name each file individually."
- **Pattern** Group 2 — Volatile specifics (version/count pins that nothing re-checks).
- **Why obsolete** `zul/src/main/resources/metainfo/zk/lang.xml` now has **86**. The sentence's point (registration is explicit, not by convention) survives without a number; the number only creates a false sense of precision and a maintenance obligation nobody owns.
- **Confidence** High — counted.
- **Action** `rewrite` — see diff hunk 5.

#### F6 — Stale pitfall count
- **Location** `.claude/skills/marble-theme/SKILL.md:104`
- **Evidence** "**Read this before any non-trivial change.** Eleven mistakes already made once"
- **Pattern** Group 2 — Volatile specifics.
- **Why obsolete** `reference/pitfalls.md` has **12** numbered entries. Same class as F5.
- **Confidence** High — counted.
- **Action** `rewrite` — see diff hunk 5.

### Medium confidence

#### F7 — "Modern CSS Expertise" — restatement of trained knowledge
- **Location** `.claude/agents/zk-theme-creator.md:51-65`
- **Evidence** "You are proficient in all modern CSS features:" followed by 14 bullets — CSS custom properties, nesting, logical properties, container queries, `:has()/:is()/:where()/:not()`, Grid/Flexbox, `color-mix()`, `@layer`, transitions, `accent-color`, `scrollbar-gutter`, `outline-offset`, `appearance: none`.
- **Pattern** Group 1c — Padding (generic capability claims); Step 3 deletion rule — "could the model already know this?"
- **Why obsolete** Every item is standard, widely-documented web-platform knowledge. Listing it neither adds capability nor constrains behaviour, and it displaces the project-specific context that is the only thing the model cannot get elsewhere.
- **Confidence** Medium — no documented harm beyond token cost and dilution, but a clean deletion-rule match.
- **Action** `remove` — see diff hunk 6.

#### F8 — "Encyclopedic knowledge" preamble duplicating the audit checklist
- **Location** `.claude/agents/md3-design-verifier.md:16-26`
- **Evidence** "You have deep, encyclopedic knowledge of the entire Material Design 3 specification published by Google, including:" + 11 bullets restating MD3's own taxonomy (colour roles, type scale, elevation levels, shape tokens, 4dp grid, easing curves, state-layer opacities, interaction states, component specs).
- **Pattern** Group 1c — Padding + near-duplicate sentences across sections.
- **Why obsolete** The only operative content is the numeric thresholds (state layers 8/12/12/16%, disabled 38%/12%), and `Step 2 — Systematic audit` at `:69-79` already restates every one of them at the point of use. The preamble is a second copy the model must reconcile. Keep-list item 9 permits a one-line role statement; the defect is the 11-bullet knowledge claim attached to it.
- **Confidence** Medium.
- **Action** `remove` — see diff hunk 7.

#### F9 — Fake dialogue examples inside agent descriptions
- **Location** `.claude/agents/md3-design-verifier.md:3` (1,675 chars), `.claude/agents/zk-theme-creator.md:3`
- **Evidence** `\n\nExamples:\n\n- User: "I just finished styling the button component…"\n  Assistant: "Let me use the md3-design-verifier agent…"\n  (Use the Task tool to launch…)` — three such turns in the verifier, five in the creator.
- **Pattern** Group 3 — "Worked examples, fake dialogue turns, embedded protocols in the description — in any quantity."
- **Why obsolete** Descriptions ride in every session's system prompt. The dialogue turns teach the orchestrator nothing the first sentence does not already say, and they constrain routing toward the enumerated phrasings rather than the intent category. `zk-theme-evaluator`, `zk-theme-generator` and `zk-spec-author` carry no examples and route correctly — the two with examples are the outliers, not the norm.
- **Confidence** Medium.
- **Action** `remove` — see diff hunk 8. (Trigger text may carry calibrated urgency; that exemption covers emphasis, not worked examples.)

#### F10 — ESLint entry point has moved
- **Location** `.github/copilot-instructions.md:54`
- **Evidence** "- ESLint config: `.eslintrc.js` (root)"
- **Pattern** Group 2 — Volatile specifics.
- **Why obsolete** The project is on ESLint `^9.26.0`, which resolves **flat config** first. `eslint.config.js` is the real entry point; it loads `.eslintrc.js` through `FlatCompat`, strips the SDL plugin (incompatible with `FlatCompat`) and re-adds the SDL rules natively for `**/*.ts` only. An agent told to edit `.eslintrc.js` for an SDL rule will edit a file whose SDL section is filtered out at load time.
- **Confidence** Medium — verified by reading `eslint.config.js`.
- **Action** `rewrite` — see diff hunk 9. Note the adjacent claim "Microsoft SDL plugin is enabled" is still true, but only for `.ts` files; the rewrite states that.

#### F11 — Broken reference link
- **Location** `.claude/agents/zk-theme-creator.md:47`
- **Evidence** `see [component-dom-structures.md](../../doc/component-dom-structures.md)`
- **Pattern** Group 2 — Volatile specifics.
- **Why obsolete** `doc/component-dom-structures.md` does not exist. The canonical source for ZK DOM structure in this repo is `.claude/skills/zk-component-rules/components/<comp>.md`, which `zk-theme-evaluator.md:73` and `zk-theme-generator.md:20` both name as authoritative.
- **Confidence** Medium.
- **Action** `rewrite` — see diff hunk 10.

#### F12 — `npm run test:forced-colors` is right, but not from the repo root
- **Location** `.claude/agents/zk-theme-evaluator.md:452`
- **Evidence** "run `npm run test:forced-colors` and FAIL this row if the suite is red"
- **Pattern** Group 2 — Volatile specifics (a flag without its working directory).
- **Why obsolete** The script exists in `zkpreview/package.json`, not the root — and `@playwright/test` is not a root dependency. Run from the repo root the command fails with "Missing script". This is the one `npm run …` reference in the surface that is substantively correct and only needs its cwd stated.
- **Confidence** Medium.
- **Action** `rewrite` — see diff hunk 11.

### Flag only (no edit proposed)

#### F13 — `zk-theme-creator` overlaps `zk-theme-generator` and the `marble-theme` skill
- **Location** `.claude/agents/zk-theme-creator.md` (whole file)
- **Pattern** Group 4 — Redundant specialist sub-agents.
- **Observation** Three surfaces now answer "write Marble component CSS": this agent, `zk-theme-generator` (the harness's writing half, contract-driven and scope-bounded), and the `marble-theme` skill for the main agent. F1, F2, F3, F7 and F11 all live in this one file — it is the only one of the three carrying fossils, because it is the only one not rewritten for this repo. Hunks 1, 2, 3, 6 and 10 repair it; whether it should survive at all is a roster decision, not an audit call.
- **Action** `flag` — decision required.

#### F14 — Absolute machine-local reference paths
- **Location** `.claude/skills/marble-theme/SKILL.md:138`, `.claude/agents/md3-design-verifier.md:33`, `:56`
- **Evidence** `/Users/hawk/Documents/workspace/THEME/material-ui-7.3.1/static-css-output/`, `/Users/hawk/Documents/workspace/DOC/zkdoc/…`
- **Observation** All verified to exist, and SKILL.md already carries the caveat "This path is machine-local; after migration, re-home or re-fetch the extract." They are environment facts only the author knows — keep-list item 1 — but they will break for any second machine. Flagged so the caveat is a conscious choice, not an oversight.
- **Action** `flag`.

#### F15 — `--md-sys-*` shape/spacing values restate MD3, not MUI
- **Location** `.claude/agents/zk-theme-creator.md:153-164`
- **Observation** The corner-radius values (8/12/16px) and 4dp grid are MD3 defaults, while `md3-design-verifier.md:30` sets project policy as "MD3 token *naming*, MUI v7 *visual values*". Hunk 1 removes these lines as part of the token-prefix fix, so this is recorded for completeness rather than as a separate edit.
- **Action** `flag`.

---

## User-level surface (outside the working directory)

`~/.claude/CLAUDE.md` and `~/.claude/agents/` load into every session in this repo but are not
part of it. Listed separately so they can be accepted or rejected as a block. No diff is proposed
for these; the entries below are the ones that match a documented row.

| # | Location | Evidence | Pattern | Suggested action |
|---|---|---|---|---|
| U1 | `~/.claude/CLAUDE.md:71-75` | "Use subagents liberally to keep main context window clean" · "For complex problems, throw more compute at it via subagents" | Group 1a — the documented `Default to [tool]` / `If in doubt, use [tool]` row | Rewrite to a condition: "Use a subagent when a search would otherwise pull large file contents into the main context." A standing "default to X" makes the model reach for subagents on tasks that don't need them. |
| U2 | `~/.claude/CLAUDE.md:69` | "If your response is over 5 lines of text, always put it into an .md file" | Group 1f — numeric output ceiling | Re-express without the number: "Put anything someone other than you will read back later into a repo `.md`; keep the chat reply to the decision and the pointer." Group 1f is explicit that an operational reason does not convert a numeric clamp into a keeper. |
| U3 | `~/.claude/CLAUDE.md:25, 86, 89-91` | "Would a senior engineer say this is overcomplicated?" · "Would a staff engineer approve this?" · "Demand Elegance … pause and ask 'is there a more elegant way?'" | Group 1c — strategy coaching next to task rules | Delete the rhetorical self-checks; keep the substantive bars beside them ("Never mark a task complete without proving it works", "If you write 200 lines and it could be 50, rewrite it"). Removing a sentence that changes neither what is legal nor how success is measured costs nothing. |
| U4 | `~/.claude/agents/zk-evangelist.md:3` vs `~/.claude/agents/zk-technical-writer.md:3` | Both: "create promotional content / articles … about ZK Framework and its addons", near-identical example lists | Group 4 — redundant specialist sub-agents | One agent taking audience as input. A third surface, the `zk-technical-writer` *skill*, covers the same job. `~/.claude/agents/` also carries `go-web-dev` and `svelgo-dev` (SvelGo framework), whose descriptions load in this repo and can never apply here. |

---

## Proposed diff

Ten hunks, one finding each, so they can be taken selectively. Nothing below has been applied.

### Hunk 1 — F1: `--md-sys-*` → `--zk-*` (`.claude/agents/zk-theme-creator.md:92-164`)

```diff
 ```css
 /* 1. Base structure */
 .z-{component} {
     /* Reset browser defaults */
     /* Layout (display, position, sizing) */
-    /* Typography using --md-sys-typescale-* tokens */
-    /* Colors using --md-sys-color-* tokens */
-    /* Shape using --md-sys-shape-* tokens */
-    /* Spacing using --md-sys-spacing-* tokens */
-    /* Elevation using --md-sys-elevation-* tokens */
-    /* Transitions using --md-sys-motion-* tokens */
+    /* Typography using --zk-typescale-* tokens */
+    /* Colors using --zk-color-* tokens */
+    /* Shape using --zk-shape-corner-* tokens */
+    /* Spacing using --zk-spacing-* tokens */
+    /* Elevation using --zk-elevation-* tokens */
+    /* Transitions using --zk-motion-* tokens */
 }
```

```diff
-## Material Design 3 Token Usage
-
-Always use the established token system:
-
-### Colors
-- Primary actions: `--md-sys-color-primary` / `--md-sys-color-on-primary`
-- Containers: `--md-sys-color-primary-container` / `--md-sys-color-on-primary-container`
-- Surfaces: `--md-sys-color-surface` / `--md-sys-color-on-surface`
-- Surface variants: `--md-sys-color-surface-variant` / `--md-sys-color-on-surface-variant`
-- Outlines: `--md-sys-color-outline` / `--md-sys-color-outline-variant`
-- Errors: `--md-sys-color-error` / `--md-sys-color-on-error`
-
-### Typography
-- Large titles: `--md-sys-typescale-headline-*`
-- Section headers: `--md-sys-typescale-title-*`
-- Body text: `--md-sys-typescale-body-*`
-- Labels/buttons: `--md-sys-typescale-label-*`
-
-### Spacing (4dp grid)
-- `--md-sys-spacing-1` (4px) through `--md-sys-spacing-*`
-
-### Elevation
-- Flat: none
-- Raised: `--md-sys-elevation-1` through `--md-sys-elevation-5`
-
-### Shape
-- Small elements: `--md-sys-shape-corner-small` (8px)
-- Medium elements: `--md-sys-shape-corner-medium` (12px)
-- Large elements: `--md-sys-shape-corner-large` (16px)
-- Pills/chips: `--md-sys-shape-corner-full` (9999px)
+## Token usage
+
+Every custom property in this theme is `--zk-*`. `--md-sys-*` is banned even though the token
+*values* follow MD3, and any occurrence in component CSS is an automatic Critical finding at
+Gate 2. Token names and values are defined in
+`zul/src/main/resources/web/zul/css/tokens/` (`_colors.css`, `_typography.css`, `_spacing.css`,
+`_sizing.css`, `_shape.css`, `_elevation.css`, `_motion.css`) — read the file rather than
+recalling a value, because Marble follows MD3 naming but MUI v7 visual values, so the MD3
+defaults are often wrong here.
```

### Hunk 2 — F2: real file layout and registration (`…zk-theme-creator.md:67-86, 172, 174`)

```diff
-## Theme Template Architecture
-
-The zk-material theme follows this structure:
-
-```
-zul/src/main/resources/web/zul/css/
-├── tokens/          # Material Design 3 design tokens
-├── base/            # Reset, utilities, icons
-├── components/      # Component-specific styles
-│   ├── layout/      # Hbox, Vbox, BorderLayout
-│   ├── inputs/      # Textbox, Combobox, Datebox
-│   ├── buttons/     # Button, Combobutton, Toolbarbutton
-│   ├── selection/   # Checkbox, Radio, Selectbox
-│   ├── data/        # Listbox, Grid, Tree, Paging
-│   ├── navigation/  # Tabbox, Menu, Toolbar
-│   ├── containers/  # Window, Panel, Popup, Groupbox
-│   ├── widgets/     # Link, Caption, Separator
-│   └── calendar/    # Calendar/datepicker
-└── zk-material.css  # Main entry point (@import all)
-```
+## Where the CSS lives
+
+```
+zul/src/main/resources/web/zul/css/
+├── tokens/   # _colors _typography _spacing _sizing _shape _elevation _motion _zindex …
+├── base/     # _reset _icons _cssflex _dnd
+└── utility/  # _colors _spacing _layout _typography _borders _elevation …
+
+zul/src/main/resources/web/js/zul/<pkg>/css/   # per-component CSS, 1:1 with a .css.dsp output
+```
+
+EE/PE component CSS lives in the sibling checkout under
+`../zkcml/zkmax/…` and `../zkcml/zkex/…`. `_`-prefixed files are partials — bundled by
+`build-css.js`, never shipped alone. There is no `components/` subtree and no single CSS entry
+point.
```

```diff
-3. **Follow the file organization** - place component CSS in the correct subdirectory under `components/`.
-
-4. **Register new CSS files** in `zk-material.css` main entry point if you create new component files.
+3. **Follow the file organization** — place component CSS under
+   `zul/src/main/resources/web/js/zul/<pkg>/css/`, alongside the widget it styles.
+
+4. **Register new CSS files** with a `<css-uri>` entry in
+   `zul/src/main/resources/metainfo/zk/lang.xml`. ZK discovers nothing by convention: a file
+   with no entry is never served. See `.claude/skills/marble-theme/reference/css-dsp.md` before
+   adding, renaming or removing any file.
```

And in the Quality Checklist:

```diff
-- [ ] CSS file registered in main entry point
+- [ ] New CSS file has a `<css-uri>` entry in `lang.xml`
```

### Hunk 3 — F3: drop the cross-repo memory block

Delete `.claude/agents/zk-theme-creator.md:214-259` and `.claude/agents/md3-design-verifier.md:163-208`
in full (from the `# Persistent Agent Memory` heading to end of file). Both agents already declare
`memory: project` in frontmatter; the harness supplies the directory and the search paths for the
current project.

In `zk-theme-creator.md` the preceding `## Update Your Agent Memory` section (`:201-212`) can stay
— it says *what* is worth recording, which is project judgment, not a path.

### Hunk 4 — F4: replace the non-existent npm scripts

Eleven call sites; the substitutions are:

| Written | Actually run it as |
|---|---|
| `npm run build:css` | `node scripts/build-css.js --module zul` (or `--module zkmax` / `zkex` from `../zkcml`) |
| `npm run check:css-dsp` | `node scripts/check-css-dsp.js --module zul --zk-home /Users/hawk/Documents/workspace/ZK10` |
| `npm run audit:css` | `bash .claude/skills/marble-theme/scripts/audit-css.sh --out <file>` |
| `npm run lint:css` | *No equivalent — `stylelint` is not a dependency of this repo.* Drop the gate, or state plainly that it does not exist here. |

Example for the two SKILL.md sites:

```diff
-| `reference/css-audit.md` | Auditing CSS hygiene — orphan tokens, hardcoded colours, repeated shadows, `display` declarations that restate a default; what `npm run audit:css` reports and how to triage it |
+| `reference/css-audit.md` | Auditing CSS hygiene — orphan tokens, hardcoded colours, repeated shadows, `display` declarations that restate a default; what `scripts/audit-css.sh` reports and how to triage it |
```

```diff
-- `.claude/skills/marble-theme/scripts/audit-css.sh` — mechanical hygiene pass; `npm run audit:css`.
+- `.claude/skills/marble-theme/scripts/audit-css.sh` — mechanical hygiene pass; run the script directly.
```

The `lint:css` sites need a decision, not a mechanical substitution — see decision D below.

### Hunk 5 — F5 + F6: drop the two stale counts (`…marble-theme/SKILL.md:37, 104`)

```diff
-4. **Source is `.css`; the shipped artifact is `.css.dsp`.** ZK does not discover component CSS
-   by convention — 78 `<css-uri>` entries name each file individually. See
+4. **Source is `.css`; the shipped artifact is `.css.dsp`.** ZK does not discover component CSS
+   by convention — every file is named individually by a `<css-uri>` entry in `lang.xml`. See
    `reference/css-dsp.md` before adding, renaming or removing any file.
```

```diff
-| `reference/pitfalls.md` | **Read this before any non-trivial change.** Eleven mistakes already made once |
+| `reference/pitfalls.md` | **Read this before any non-trivial change.** Mistakes already made once |
```

### Hunk 6 — F7: remove the CSS capability claim (`…zk-theme-creator.md:51-65`)

```diff
-### Modern CSS Expertise
-You are proficient in all modern CSS features:
-- **CSS Custom Properties** (variables) for theming tokens
-- **CSS Nesting** for cleaner component styles
-- **CSS Logical Properties** for internationalization
-- **CSS Container Queries** where appropriate
-- **CSS `has()`, `is()`, `where()`, `not()`** selectors
-- **CSS Grid and Flexbox** for layout
-- **CSS `color-mix()`** for color manipulation
-- **CSS `@layer`** for cascade management
-- **CSS transitions and animations** with `prefers-reduced-motion` respect
-- **`accent-color`** and form styling
-- **`scrollbar-gutter`**, **`scroll-behavior`**, **`overscroll-behavior`**
-- **`outline-offset`** for focus indicators
-- **`appearance: none`** for custom form controls
+### Cascade layers in this theme
+
+Layers are assigned at build time in the order `zk-base < zk-components < zk-utilities`, and
+`build-css.js`'s `assertLayer()` fails the build if a reset rule lands outside `zk-base`.
```

### Hunk 7 — F8: remove the MD3 knowledge preamble (`…md3-design-verifier.md:16-26`)

```diff
-You have deep, encyclopedic knowledge of the entire Material Design 3 specification published by Google, including:
-
-- **Color System**: Tonal palettes, color roles (primary, secondary, tertiary, error, surface, outline, etc.), dynamic color, light/dark schemes, custom colors, and color harmonization.
-- **Typography**: Type scale (display, headline, title, body, label in large/medium/small), font weight, letter spacing, line height, and recommended font families (Roboto).
-- **Elevation**: Surface tonal color overlay system (M3 uses tonal elevation rather than shadow-only), 6 elevation levels (0-5), shadow values.
-- **Shape**: Corner radius system (none, extra-small, small, medium, large, extra-large, full), shape families, and which components use which shape tokens.
-- **Spacing**: 4dp baseline grid, consistent padding and margin patterns.
-- **Motion**: Easing curves (emphasized, emphasized-decelerate, emphasized-accelerate, standard, standard-decelerate, standard-accelerate), duration tokens (short 1-4, medium 1-4, long 1-4, extra-long 1-4).
-- **State Layers**: Hover (8% opacity), focus (12% opacity), pressed (12% opacity), dragged (16% opacity) overlay system using the content color.
-- **Interaction States**: Enabled, disabled (38% opacity for content, 12% opacity for containers), hovered, focused, pressed, selected, activated, error.
-- **Component Specifications**: Exact M3 specs for every component including buttons, checkboxes, radio buttons, text fields, cards, dialogs, navigation bars, tabs, lists, menus, chips, FABs, switches, sliders, date pickers, etc.
```

The operative thresholds already appear at their point of use in `Step 2 — Systematic audit`
(`:74-75`: state layers, disabled opacity), so nothing measurable is lost. If the numbers are
wanted in one place, move them into that Step 2 list rather than keeping a second copy here.

### Hunk 8 — F9: strip the fake dialogue from the two descriptions

`.claude/agents/md3-design-verifier.md:3` — keep everything up to and including "…terminal
`GATE2: PASS/FAIL` line.", drop the trailing `\n\nExamples:\n\n…` block (three User/Assistant
turns). Same treatment for `.claude/agents/zk-theme-creator.md:3` (five turns), keeping the
description through "…understanding ZK component DOM structures for styling purposes."

### Hunk 9 — F10: correct the ESLint entry point (`.github/copilot-instructions.md:54-56`)

```diff
-- ESLint config: `.eslintrc.js` (root)
+- ESLint config: `eslint.config.js` (root, flat config — ESLint 9). It loads the legacy
+  `.eslintrc.js` through `FlatCompat`, so rule changes usually belong in `.eslintrc.js`.
 - Custom rules: `eslint-plugin-zk/` — do not disable without review
-- Microsoft SDL plugin is enabled — security patterns are enforced
+- Microsoft SDL plugin is enabled for `**/*.ts` only, declared natively in `eslint.config.js`
+  (`FlatCompat` cannot load SDL v1.1.0, so the legacy config's SDL entries are filtered out) —
+  security patterns are enforced
```

### Hunk 10 — F11: point the DOM reference at the canonical skill (`…zk-theme-creator.md:47`)

```diff
-see [component-dom-structures.md](../../doc/component-dom-structures.md)
+The canonical, per-component source is `.claude/skills/zk-component-rules/components/<comp>.md`
+— read it before relying on the summary above, which is not maintained per component.
```

### Hunk 11 — F12: state the working directory (`…zk-theme-evaluator.md:452`)

```diff
-run `npm run test:forced-colors` and FAIL this row if the suite is red
+run `cd zkpreview && npm run test:forced-colors` and FAIL this row if the suite is red
+(the script and Playwright live in `zkpreview/package.json`, not the repo root)
```

---

## Verification (Step 7)

None of these have been probed behaviourally — the audit is static. Before applying:

- **Hunks 1, 2, 4, 5, 6, 9, 10, 11 are factual corrections** verified against the filesystem and
  `package.json` in this run. They carry no behavioural risk; the current text is wrong.
- **Hunks 3, 7, 8 are removals.** Grep for the removed strings before deleting — in particular
  check whether anything parses the `Examples:` block out of an agent description, and whether
  `.claude/skills/marble-theme/reference/harness-playbook.md` or `tasks/harness/` reference the `zkThemeTemplate` memory path.
- **Hunks 7 and 8 are the two worth re-probing**: run one `md3-design-verifier` contract-audit
  before and after on the same component and compare the findings tables. If the finding count or
  severity assignment moves, re-add the state-layer/disabled thresholds into Step 2 in their
  minimal form rather than restoring the preamble.
- Re-run this audit at the next model migration; a prompt is a per-model artifact.

---

# Addendum — source-repository investigation (2026-09-29)

Both open decisions were investigated against the migration source,
`/Users/hawk/Documents/workspace/zkThemeTemplate`. Two claims in the body above are corrected
here; neither changes a finding, but one changes a cost estimate by an order of magnitude.

## D1 — why the npm scripts are missing

**The source repository has all of them.** `zkThemeTemplate/package.json` defines
`build:css`, `build:css:dev`, `check:css-dsp`, `check:forced-colors`, `check:doc-links`,
`watch`, `minify`, `lint:css`, `audit:css`, `review:forced-colors` and five Playwright scripts.
It has `stylelint: ^16.26.1` as a devDependency and a tuned `.stylelintrc.json` at the root.
So the skill's reference files were correct **in the repository they were written for**; nothing
was lost in transit — the tooling was simply never ported alongside the prose.

Splitting the four missing scripts by *why* they are missing changes the answer:

| Script | Why it did not come across | Portable? |
|---|---|---|
| `build:css` | zk's `scripts/build-css.js` was **rewritten** for a multi-module tree — its header reads "One repository root per module, instead of the template's single webDir/themeDir", and `--module zul\|zkmax\|zkex` is mandatory (exit 2 on omission). A fixed npm script cannot carry the argument. | No — architectural. The current guidance (call the script with `--module`) is correct. |
| `check:css-dsp` | Same: zk's version needs `--module` **and** `--zk-home`. | No — architectural. |
| `audit:css` | The script itself *did* come across, as `.claude/skills/marble-theme/scripts/audit-css.sh`. Only the npm wrapper was dropped. | Already available — call the script directly. |
| `lint:css` | **No architectural obstacle.** It is a plain stylelint glob. `scripts/check-forced-colors.js`, `check-doc-links.js`, `watch-css.js` and `build-forced-colors-review.js` were likewise never copied into `zk/scripts/`. | Yes — nothing blocks it. |

The omission was recorded at the time. `zkThemeTemplate/doc/migration/drafts/brief-3.5.md:59-60`
is the migration's "FACTS about zk you may state (and only these)" list, and contains the exact
sentence that became `marble-theme/SKILL.md:89-91`. So the top-level skill page was updated
deliberately; the eleven reference-file call sites were simply never swept. **This is a known
partial sweep, not an unnoticed drift** — a correction to the framing in F4 above, which implied
nobody had noticed.

### Measured cost of porting stylelint (corrects the D1 option-B estimate)

The body of this report estimated option B at "a day or more, plus triaging ~90 component CSS
files". That was a guess, and it was wrong. Running the template's stylelint and its
`.stylelintrc.json` against zk's actual CSS gives:

| Target | Result |
|---|---|
| `zul/src/main/resources/web/**/*.css` (87 files, CE) | **Zero findings**, plus one parse error: `tokens/_fonts.css:17` contains DSP EL (`url(${c:encodeURL(…)})`), which stylelint cannot parse. It is the only CSS file in CE containing EL, so one `ignoreFiles` entry covers it. |
| `zkcml/**/src/main/**/*.css` (49 files, EE/PE) | **6 findings**: 1 `no-empty-source` in `zephyr-test/…/sample.css` (a test fixture — belongs outside the glob), 3 `no-duplicate-selectors` warnings in `zkmax/…/goldenlayout/css/goldenlayout.css`, and 2 redundant-longhand errors (`zkmax/…/db/css/daterangebox.css:357` → `inset`, `zkmax/…/grid/css/grid.css:17` → `overflow`). |

The config is already tuned to avoid noise — its own header comment records that extending
`stylelint-config-standard` produced "~989 mostly-stylistic findings" and that the ten retained
rules target duplication and dead CSS only. So the real work is: copy `.stylelintrc.json`, add
the devDependency, add a `lint:css` script with the CE+EE globs and an `ignoreFiles` entry for
`_fonts.css`, then triage five genuine findings. **Hours, not days.**

**Revised recommendation:** port it (the former option B). The four `lint:css` call sites in the
skill then become correct as written instead of needing deletion, and the gate they describe
starts existing. Option A (delete the references) remains valid if CSS lint is not wanted in zk,
but it is no longer the cheaper path by the margin the body of this report assumed.

## D2 — which agent the harness actually used

`zk-theme-creator` is a **superseded predecessor**, not a peer of `zk-theme-generator`. Five
independent lines of evidence agree:

1. **It is not in the loop's roster.** `.claude/skills/marble-theme/reference/harness-playbook.md` — identical in both repos —
   mentions `md3-design-verifier` 7 times, `zk-theme-evaluator` 6, `zk-spec-author` 6,
   `zk-theme-generator` 2, and **`zk-theme-creator` zero times**.
2. **Its memory stops before the harness starts.** Files in
   `zkThemeTemplate/.claude/agent-memory/zk-theme-creator/` are dated 2026-04-24 to 2026-05-10.
   The contract machinery it would have to obey (`zk-component-rules`, contract tiers, the
   outcome-driven format) first lands 2026-06-03. Its last write predates the harness by
   three weeks.
3. **It is the least maintained of the five.** Commits against each agent file in the template:
   evaluator 12, spec-author 10, verifier 6, generator 6, **creator 3**.
4. **Recent sessions never call it.** Across the 16 retained transcripts under
   `~/.claude/projects/-Users-hawk-Documents-workspace-zkThemeTemplate/`: evaluator 4
   invocations, verifier 2, spec-author 1, creator **0**, generator 0. (Generator's 0 only means
   the retained window is evaluator-heavy — it is in the playbook; creator's 0 is corroborated by
   the four other lines of evidence.)
5. **The migration consciously declined to fix it.** `zkThemeTemplate/doc/migration/gates/P3.md`
   identified both the memory-path defect (`:218-220`) and the dead `zk-material.css` reference,
   and ruled: "`zk-theme-creator` item 4 (`zk-material.css`) is template-era content, not
   host-specific, and is left as it is." The same P3 verdict is mirrored in
   `zk/tasks/p3-gate-verdict.md:193-197`. F1, F2 and F3 of this audit are therefore *known,
   recorded deferrals* — a second correction to the framing above.

### Can the two be merged?

Not by combining their text — they are different kinds of thing:

- `zk-theme-generator` is **contract-driven and refusing**: it stops unless an eval report exists
  with `status: NEEDS_FIX`, edits exactly the one file named in `shared-css-file`, and escalates
  rather than improvising. That strictness is the point — it is one half of a two-agent loop with
  a single-writer rule.
- `zk-theme-creator` is **free-form**: no contract, no eval report, no scope bound. Folding its
  prompt into the generator would dissolve precisely the constraint that makes the generator safe.

So the real question is not "merge them" but "is a free-form CSS entry point still wanted?" If
yes, its home is the `marble-theme` skill — which the main agent already loads, which *is* kept
current, and which carries the same architecture, commands and token rules without a second agent
definition to drift. Retiring the agent and confirming the skill covers the gap is the merge.

**Revised recommendation for D2:** retire `zk-theme-creator` (the former option B), and drop
hunks 1, 2, 6 and 10, which exist only to repair it. Hunk 3 still applies to
`md3-design-verifier.md`, which *is* in the roster and carries the same cross-repo memory path.

---

# Execution record — decisions applied (2026-09-29)

Both decisions were taken as **option A**: port stylelint (D1), retire `zk-theme-creator` (D2).
Everything below is applied and verified; nothing is pending. Hunks 1, 2, 6 and 10 were dropped —
they existed only to repair the agent that D2 retired.

## D2 — `zk-theme-creator` retired

Coverage was confirmed before deletion. Every subject the agent carried has a maintained home:

| Content in the retired agent | Where it lives now |
|---|---|
| `.z-{c}-{element}` naming convention | `zk-component-rules` skill |
| Per-component DOM structures | `zk-component-rules/components/*.md` (95 files) |
| State-layer pattern and opacities | `marble-theme/SKILL.md`, `reference/css-audit.md`, and `doc/spec/DESIGN.md:152-156` — which gives the real `--zk-state-*` tokens instead of the agent's raw `0.08` / `0.12` literals |
| Build, preview and port commands | `marble-theme/SKILL.md` |
| Focus-visible / contrast / accessibility | `reference/verification.md`, `reference/css-audit.md` |
| "Never open `/preview`" | `reference/verification.md:142` |
| `!important` guidance | `reference/important-reduction.md` (a whole page) |

No functional references remained: it appears in no playbook, no `settings.json`, and no other
agent. The two surviving mentions are historical records — `tasks/p3-gate-verdict.md:195` and this
report — and both are correct as records.

## D1 — stylelint ported

**zk (CE)** — new `.stylelintrc.json` (the template's ten hygiene rules verbatim, plus an
`ignoreFiles` entry for `tokens/_fonts.css`, the only CE stylesheet carrying DSP EL);
`stylelint ^16.26.1` added to `devDependencies`; `"lint:css": "stylelint \"zul/src/main/resources/web/**/*.css\""`.
Result: **exit 0, zero findings** across 87 files.

**zkcml (EE/PE)** — the same config with `zephyr-test/**/*.css` ignored (a test webapp, not
shipped theme CSS); `"lint:css": "stylelint \"*/src/main/resources/web/**/*.css\""`. Two genuine
findings fixed:

| File | Before | After |
|---|---|---|
| `zkmax/…/db/css/daterangebox.css:354-357` | `top: 4px; bottom: 4px; left: 0; right: 0;` | `inset: 4px 0;` |
| `zkmax/…/grid/css/grid.css:16-17` | `overflow-y: auto; overflow-x: hidden;` | `overflow: hidden auto;` |

Both are exact equivalents, so computed style is unchanged. The three `no-duplicate-selectors`
findings in `goldenlayout.css` are left as **warnings**, which is what the config declares them to
be — that file mirrors an upstream library's structure, and the template made the rule
warning-severity for exactly this reason.

**Side effect worth naming:** `npm install` in zk rewrote `package-lock.json` well beyond the one
new dependency (1,149 insertions / 1,589 deletions) — npm re-resolved and pruned a tree that had
drifted from the lockfile. `npm run type-check`, `node scripts/build-css.js --module zul` and
`node scripts/check-css-dsp.js` were all re-run afterwards and pass, and `gulp`, `eslint`,
`typescript` and `webpack` are all still installed. Review that lockfile diff before committing.

## Documentation hunks applied

| Hunk | File | Change |
|---|---|---|
| 3 | `md3-design-verifier.md` | `# Persistent Agent Memory` block removed (heading to EOF) — it pointed at `zkThemeTemplate`'s memory directory and session logs while the frontmatter already declares `memory: project` |
| 4 | `marble-theme/SKILL.md` ×2, `important-reduction.md` ×2, `density.md` ×2, `css-dsp.md` | `audit:css` / `build:css` / `check:css-dsp` replaced with the real invocations; `lint:css` left alone — D1 made those four call sites correct as written |
| 4 | `marble-theme/SKILL.md:87-94` | The script-availability paragraph now states that `lint:css` exists, and says *why* the builder is called directly (`--module` is mandatory) rather than only that no npm wrapper exists |
| 5 | `marble-theme/SKILL.md:37, 107` | Both stale counts dropped — "78 `<css-uri>` entries" (actual 86) and "Eleven mistakes" (actual 12). Neither number was carrying meaning. |
| 7 | `md3-design-verifier.md:16-26` | The 11-bullet MD3 knowledge preamble removed; its operative thresholds already appear at their point of use in Step 2 |
| 8 | `md3-design-verifier.md:3` | Fake dialogue turns stripped from the `description` — 1,669 → 601 characters, still valid |
| 9 | `.github/copilot-instructions.md:54-56` | ESLint entry point corrected to `eslint.config.js`, and the SDL claim narrowed to `**/*.ts` |
| 11 | `zk-theme-evaluator.md:452` | `npm run test:forced-colors` now carries `cd zkpreview &&` and says why |

`md3-design-verifier.md` went from 208 to 149 lines with no loss of instruction.

## Verification

| Check | Result |
|---|---|
| `npm run lint:css` (zk, CE) | exit 0, zero findings |
| `npm run lint:css` (zkcml, EE/PE) | exit 0, 3 warnings (goldenlayout, by design) |
| `npm run type-check` (zk) | exit 0 |
| `node scripts/build-css.js --module zul` | exit 0 |
| `node scripts/build-css.js --module zkmax` | exit 0 |
| `node scripts/check-css-dsp.js --module zul` | exit 0 — "All css-uri ZK requests are present in the build" |
| Every `npm run X` in `.claude/` resolves | yes — `lint:css` (root), `test:forced-colors` (zkpreview); the one remaining `build:css` is the sentence stating it does *not* exist |
| `zkThemeTemplate` references in `.claude/agents` + `.claude/rules` | 0 |
| `--md-sys-*` in `.claude/agents` | 2, both prohibitions |
| `md3-design-verifier.md` frontmatter | delimiters intact, `description` parses as a JSON string |

Not done: nothing was committed, in either repository.
