# Contract Tiers — Authoring Discipline for zk-component-rules

This file is **required reading** for any agent or human about to write or modify either:
- `.claude/skills/zk-component-rules/components/<comp>.md` (this skill — theme-portable)
- `doc/contracts/<comp>.md` (Marble theme contract — theme-specific)

The two files form a **two-tier contract**. This guide defines the boundary, the five predicate classes, the iceblue-CSS-mining workflow, and the refuse-to-emit rules that keep the boundary clean.

---

## TL;DR

| Layer | File | Nature of content | Changes across themes? |
|---|---|---|---|
| Structural Invariants | `.claude/skills/zk-component-rules/components/<comp>.md` | Universal predicates such as DOM, state classes, geometry/relations, and "states must be distinguishable" | **No.** Every theme should pass them |
| Theme Contract | `doc/contracts/<comp>.md` (+ optional `.html` mockup) | Concrete token bindings, explicit design trade-offs, expected resolved values, `## Outcome assertions` (whole-page-scale M-rows) | **Yes.** One per theme |

**The evaluator's job = both layers pass + M-rows pass + the AI visual review reports no serious finding.** Structural invariants are like "physical laws", the theme contract is like "this planet's gravity", and M-rows are like "the whole thing should look like an X".

---

## Why the contract is split into layers

Using the iceblue baseline directly as the verification standard causes three problems:

1. **Colors get locked in**: iceblue uses `#e6f2fa`, Marble wants primary-container, so the pixel diff necessarily fails.
2. **Values get locked in**: iceblue's node padding is 4px×8px, while Marble wants the roomier 8px×12px card; change the numbers and it fails.
3. **A poor trade-off gets treated as dogma**: iceblue shrinks the expand icon to 12px and tucks it into the bottom-right corner. That is a 2010-era density trade-off and should not become a hard rule for a 2026 theme.

But **some things iceblue gets right**: an org chart must be a tree, parent nodes must be horizontally centered, and selected must be visibly different from unselected. These are the **semantic roles** that make an organigram an organigram, and they are independent of visual style.

**Approach: distill the "semantic roles" into structural invariants (universal), and lock the "visual values" into the theme contract (per-theme).** Look at the iceblue baseline in order to **extract semantic roles**, not to **copy pixels**.

---

## Five classes of verifiable predicates

A/B/C belong to the skill (theme-independent); D belongs to the contract (theme-specific, bottom-up); M belongs to the contract (theme-specific, top-down whole-page scale, forming a closed loop with §3d AI visual review).

### A. Structural predicates (Structural) — theme-independent

DOM topology, conditions under which classes appear, role/aria. Fully determined by ZK widget behavior; a theme has no say.

**Example (organigram)**:
- Every non-root node has exactly one parent node, and the whole forms a tree
- `.z-orgitem-selected` appears when selected and disappears when deselected
- `.z-orgitem-close > .z-orgchildren` must be `display: none`
- root has `role="tree"`, and each item has `role="treeitem"`

This layer is written directly into `.claude/skills/zk-component-rules/components/<comp>.md`, and **all themes share it**.

### B. Relational / geometry predicates (Relational) — theme-independent, use tolerance + relative comparison

Measurable but no absolute value is specified. **Do not write "padding = 8px"; write "siblings align within ±2px"**.

**Example (organigram)**:
- Siblings in the same generation: `|max(top) − min(top)| ≤ 2px`
- Parent node horizontally centered: `|parent.centerX − midpoint(leftmost, rightmost child centerX)| ≤ 2px`
- Connector line exists and is visible: between parent-bottom and child-top there is a line segment that is not transparent and has length > 0
- Node has a visible boundary: `border-width ≥ 1px ∧ border-color ≠ transparent` **∨** `background ≠ container.background` **∨** `box-shadow ≠ none`
- Text is not clipped: `text rendered width ≤ box content width`
- Text is readable: the WCAG contrast ratio of the label against the box background is ≥ 4.5:1

Key point: **at least one must hold** (disjunction), rather than "the boundary must be a 1px solid grey". This lets different themes convey "I am a node" with **any one of** border, background tint, or shadow.

### C. State-difference predicates (State-Differs) — theme-independent

"Selected must be distinguishable" is an invariant; "selected must be blue" is not.

**Example (organigram)**:
- `selected` vs. unselected: at least one of `{border-color, background-color, outline, box-shadow, color}` measurably differs
- `disabled` vs `enabled`: `opacity < 1` **∨** text contrast lower than enabled
- `hover` vs resting (on a selectable, non-disabled node): at least one of `{background-color, border-color, box-shadow}` changes
- `focus-visible`: must have an outline **∨** box-shadow ring **∨** border-color change (any one that a keyboard user can notice)
- `open` vs `closed`: child nodes `display: none` (or absent from the DOM) ↔ child nodes visible

This layer is also written in the structural rules file, alongside A and B. **This is the key design that makes "verifiable" and "theme freedom" compatible.**

### D. Token-bound predicates (Token-Bound) — theme-specific

Concrete values, bound to tokens and paired with DESIGN.md sections, **not bound to baseline pixels**. Written into `doc/contracts/<comp>.md`.

**Example (organigram, Marble)**:
- `.z-orgnode { border-radius: var(--zk-shape-card); }` — source: DESIGN.md §5
- `.z-orgitem-selected > .z-orgnode { background-color: var(--zk-color-primary-container); }` — source: DESIGN.md §3
- Connector line `border-color: var(--zk-color-outline-variant)` — source: DESIGN.md §11

Changing themes = changing token values or replacing this contract; A/B/C stay unchanged.

### M. Outcome predicates (Outcome-Level / whole-page scale) — theme-specific, top-down

A/B/C/D are all "bottom-up": they assert a single property on a single selector. But a contract can **pass in full** and still be visually broken, because nobody asks "does the whole look like the thing it is supposed to be?" M-rows close that gap.

Written into the `## Outcome assertions` table in `doc/contracts/<comp>.md`, alongside the `## Design Contract` prose.

**Characteristics**:
- **Whole-page scale / cross-selector**: "each `.lm_controls` inside a `.lm_header` sits on the same row as the tabs", "panels dock without overlapping each other", "the outer wrapper has no redundant card"
- **Mainly bbox geometry**: uses `getBoundingClientRect()`, not `getComputedStyle()`
- **Disjunctive / tolerance-based**: "border ≥ 1px OR shadow ≠ none OR bg ≠ transparent", "±2px", "≥ 95%"
- **Outcome, not recipe**: assert "looks like a card", not "`border: 1px solid #ccc`"
- **Failure blocks VERIFIED**: if any M-row FAILs, it blocks VERIFIED, even when all D-tier checks pass

**Example (goldenlayout)**:
- M1: every `.z-goldenpanel` has a visible frame AND the outer `.z-goldenlayout` has no redundant outer card
- M4: the `bbox.top` range of all `.lm_tab` siblings inside the same `.lm_header` is ≤ 2px
- M9: the `.lm_controls` cluster's bbox.right is within 8px of the `.lm_header` right edge, and has ≥ 2 visible icons
- M13: `.lm_controls` and the first `.lm_tab` are **on the same row** (vertical centers within 4px)

**Relation to the B tier**: a B-tier predicate is a theme-independent relational predicate scoped to a single selector; an M-row is a theme-specific outcome predicate scoped across selectors / the whole page. A B-tier predicate can be **promoted** to an M-row when its visual pass/fail threshold sits at the level of "does the whole look right"; but once promoted, it leaves the skill and moves into the contract.

**Closed loop with §3d AI visual review**: when a finding from the evaluator's AI visual review is not caught by any M-row (geometry passes, but the AI still judges it FAIL by eye), **add an M-row back into the contract**. The M-row set gets stricter with each finding. goldenlayout's M13 grew out of this path: M9 only checked X-axis right-anchoring, the AI vision saw the icons wrap onto a second row, and M13 added the Y-axis "same row" condition.

Changing themes = rewriting the M-rows (because their visual intent lives in DESIGN.md), but the **predicate form** (disjunction + tolerance) is common across themes.

---

## Discipline for extracting semantic roles from a baseline

When looking at `doc/contracts/baselines/<comp>-iceblue.png`, **you may only ask three kinds of questions**:

1. What distinguishable states does this component have? (selected, disabled, open/closed, hover, focus...) → becomes C-class predicates
2. Along which dimensions are these states told apart? (color, border, fill, shape, position, text weight...) → becomes an "at least one difference" disjunction
3. What geometric constraints apply to the component's topology and relative positions? (parent above the horizontal midpoint of its children, siblings at the same height, lines connecting to box edges...) → becomes B-class predicates

**Questions you must not ask**:
- ~~What is this color called? Should I copy it?~~
- ~~What is this padding in px?~~
- ~~What size is this icon?~~

Treat the baseline as "**the visual grammar of this component**" (grammar), not as "**what this component should look like**" (specimen).

---

## Counter-example: beware of "mirrors iceblue"

The current `doc/contracts/organigram.md` contains this line:

```
| node-6 | `.z-orgnode` | min-width | `64px` | DESIGN.md §10 (smallest legible org card; mirrors iceblue) |
```

The justification "mirrors iceblue" **crosses the red line of this discipline**. Write it one of two correct ways:

- **If the value has a design reason**: write the reason out. For example: "64px is the minimum width that fits a 4-character Chinese label + a 12px icon + inner padding" — this is a design judgment that can be questioned and replaced.
- **If the value has no design reason and is only "iceblue also uses 64"**: **demote it to a B-class predicate** (the form "`width ≥ text-width + 2 × horizontal-padding` is enough"), then delete it from the theme contract.

Every absolute value in a theme contract must be able to answer "**why this value? Which DESIGN.md section does it come from?**" Any value that cannot be answered gets moved to the structural rules or deleted.

---

## Workflow: writing a contract for a new theme from scratch

1. **Open an old-theme baseline PNG** (any one: iceblue / breeze / whichever)
2. **List the semantic roles**: write a short list of "which states this component has, which geometric relations, and which dimensions must be distinguished". **Do not copy values.**
3. **Write the structural rules file** `.claude/skills/zk-component-rules/components/<comp>.md`: DOM, state classes, composition invariants, and A/B/C predicates. Once written, in principle **all future themes share it**; update it only when a ZK behavior change is found.
4. **Write the theme contract** `doc/contracts/<comp>.md`: following this theme's DESIGN.md color palette, spacing, shape, and motion rules, pick tokens and write concrete expected values. Every entry carries a `source` field pointing to a DESIGN.md section or to the reason for the design judgment.
5. **Run the evaluator**:
   - Predicates in the structural rules file: each one should pass (if one does not, either the CSS is wrong or ZK behavior has changed — only the latter justifies fixing the rules file)
   - Concrete values in the theme contract: compare each one against computed style
6. **When changing themes**: redo step 4; step 3 stays unchanged

---

## How the evaluator reads the two layers

```
.claude/skills/zk-component-rules/components/<comp>.md
  ├─ DOM structure → DOM expectations (querySelector should exist / should not exist)
  ├─ State classes → class should appear when the matching state toggles
  ├─ Composition invariants → A class: directly verify computed style equals the specified value (e.g. position: relative)
  └─ Relational invariants + State-differs invariants → B/C class predicates

doc/contracts/<comp>.md
  └─ Expected values table → each querySelector + property → compare against the computed value
```

If a component's structural file does not currently list B- or C-class predicates explicitly (it only has DOM / state class / composition), the evaluator can only verify A + D. **This is a current gap** — add the `## Relational invariants` and `## State-differs invariants` sections component by component.

---

## JS source reading — mold is not optional

A ZK widget's "JS source" actually comes in **two kinds**, and both are required:

| File type | Role | What the spec-author cares about |
|---|---|---|
| `<Widget>.ts` (or the main `.js` file) | Behavior layer: state toggles, setters, event handling, lifecycle | When a state class is added/removed; attribute getter/setter; server-side property mapping |
| `mold/<comp>.js` (function `<comp>$mold$(out)`) | **DOM rendering layer — the real DOM ground truth** | The HTML tree actually emitted, hard-coded classes, hard-coded `role`/`aria-*` attributes, whether a wrapper appears unconditionally |

**Reading only the `.ts` and not the mold is a common mistake.** `redraw_()` and `replaceHTML_()` in the `.ts` usually **call the mold function** instead of building the HTML themselves, so the real DOM shape cannot be seen from the `.ts` alone.

### Example (why this rule exists)

In one round, the spec-author of `stepbar` missed reading `mold/step.js`, so the contract assumed `.z-step-content` only appears in wrapped-label mode. But lines 16-19 of the mold make it clear:

```js
out.push('<div id="', uuid, '-content" class="', this.$s('content'), '" role="listitem" ...>');
out.push('<span ... class="', this.$s('icon'), ...);
out.push('<span ... class="', this.$s('title'), ...);
out.push('</div></div>');
```

`.z-step-content` **unconditionally wraps** `.z-step-icon + .z-step-title`. Every selector in the contract of the form `.z-step > .z-step-icon` was wrong; the evaluator measured 28/28 fail, and the whole generator pass was wasted.

### Mandatory procedure

1. **Every component's `js-source-files:` in the contract must list both**:
   - `<Widget>.ts` (or the main widget JS)
   - the matching `mold/<comp>.js` (if it exists)
2. **The hash must cover all listed files** — compute it in one go with `shasum -a 256 <files...>`; do not hash only the .ts.
3. **When re-dispatching or recovering from drift, the spec-author must read every listed file**; reading only the .ts is not allowed. If the mold is missing, add it to `js-source-files` first, then read it.
4. **How to find the mold quickly** (here `$ZK_SOURCE` is the root of the ZK CE checkout, the `zk` repo, containing `zul/`, `zk/`, `zkbind/`; `$ZKCML_SOURCE` is the root of the ZK EE checkout, the `zkcml` repo, containing `zkex/`, `zkmax/`):
   ```bash
   find "$ZK_SOURCE" "$ZKCML_SOURCE" -path "*mold*" -name "<comp>.js" 2>/dev/null
   ```
   Not found → the widget uses the default mold or builds its HTML itself; in that case fall back to the `.ts` and look at `redraw_()` / `replaceHTML_()` for `out.push` / `zk.WidgetRenderHelper`.

### Exceptions: which components have no mold

- Some ZK core widgets use default DOM rendering (they do not write a mold function); in that case `redraw_()` in the `.ts` is the DOM ground truth.
- For third-party wrappers (T3, e.g. pdfviewer, tbeditor), the internal DOM is injected by the library and the `.ts` only handles mount/destroy; in that case the ground truth is the library's runtime result, and measuring the live DOM in the evaluator is correct — `js-source-files` only needs to list ZK's own `.ts`.

### Rule of thumb

> **Decision criterion**: before writing a contract, ask "who decides the outerHTML of this component's `<root>`?"
> - Answer is `mold/*.js` → the mold must be read and must be in `js-source-files`.
> - Answer is `redraw_()` in `.ts` → the `.ts` must be read (you already know how).
> - Answer is third-party library injection → live DOM measurement is authoritative; the spec-author records only the wrapper boundary.

---

## Using iceblue CSS as evidence for structural signals

iceblue is ZK's built-in theme and has been used in production for more than a decade. For the question of "which classes ZK actually emits, which pseudo-elements it depends on, and under which state combinations styling is needed", its CSS is **ready-made evidence**. **As long as the discipline holds**, using it as a signal source for the structural rules file is faster than reading the widget JS — the JS tells you **which setter toggles which class**, while the CSS tells you **which classes are actually used as style anchors** (sometimes a widget emits a class that no theme ever uses — such a class is noise and does not necessarily belong in the rules file).

### Locating the iceblue CSS source (the path varies; a fallback is required)

The path where iceblue CSS is obtained is not fixed (it depends on which version the user unpacked where on their machine), so **do not hardcode any single path**. Search in the order below; if nothing is found, stop and ask the user:

1. **Snapshot inside the project** (most common):
   ```bash
   find temp -path "*iceblue*" -name "<comp>.css.dsp" 2>/dev/null
   ```
2. **ZK source tree** (if `$ZK_SOURCE` / `$ZKCML_SOURCE` are set): `$ZK_SOURCE/zul/src/main/resources/web/js/**/<comp>.css*`, `$ZKCML_SOURCE/zkex/src/main/resources/web/js/**/<comp>.css*`, `$ZKCML_SOURCE/zkmax/src/main/resources/web/js/**/<comp>.css*`
   ```bash
   find "$ZK_SOURCE" "$ZKCML_SOURCE" -path "*/src/main/resources/web/js*" -name "<comp>.css*" 2>/dev/null
   ```
3. **The user has not configured an iceblue CSS source** → print the following in the conversation:
   ```
   iceblue CSS for <comp> not found. Searched:
     - temp/iceblue_c-*/web/iceblue_c/js/**/<comp>.css.dsp
     - $ZK_SOURCE/...
     - $ZKCML_SOURCE/...
   Please provide a path to the iceblue CSS source for <comp>,
   or confirm to proceed without iceblue mining (B-tier predicates
   from CSS mining will be skipped; produce A + C tiers only).
   ```
   Wait for the user's reply before continuing. **Do not** search random paths on your own, and **do not** assume there is no iceblue source and skip silently.

Once the file is located, note its absolute path for use in this task; do not write it into the rules file (the rules file must not keep iceblue paths, because they depend on the user's local environment).

### What can be extracted from iceblue CSS (Structural Signals)

Read through iceblue's `.css` / `.css.dsp` files, **looking only at selectors and declaration property names, not values**, and you can extract:

1. **Full list of state classes** — iceblue uses `.z-orgitem-selected`, `.z-orgitem-disabled`, `.z-orgitem-non-selectable`, and `.z-orgitem-close`; these four are the states that organigram actually produces. If the structural rules file omits any of them, that is a gap.
2. **State combinations (combined-state)** — for example, if `.z-orgitem-selected.z-orgitem-disabled > .z-orgnode { ... }` appears, it means this combination **does occur and needs handling**, and it should be added to "States to evaluate".
3. **Whether a pseudo-element takes part in layout or is decoration** — if `::before` / `::after` has `position: absolute` + `top/left/width/height`, it is layout-critical (the structural rules file must state "theme must keep these positioned"); if it only sets `background` and no position, it is pure decoration, and a theme may drop it.
4. **Interaction contracts** — `cursor: pointer` marks click targets, `pointer-events: none` marks interaction suppression, and `display: none` under a state class marks a visibility contract (e.g. `.z-orgitem-close > .z-orgchildren { display: none }`). These properties **themselves** are the contract, and their values are contract too (`none` vs `block`) — this is one of the few cases where the value can be copied directly into the structural rules file, because it expresses behavior rather than appearance.
5. **List of transitioning properties** — `transition: background-color, border-color, box-shadow` tells you "the theme should animate these three properties smoothly". **Duration and easing are theme-free**, but **whether there is a transition, and which properties it covers**, is a UX contract.
6. **Selector specificity hierarchy** — iceblue writes `.z-orgitem-selected > .z-orgnode` rather than `.z-orgnode.z-orgitem-selected`, which confirms the widget convention that "the state class sits on `.z-orgitem`, and the visuals apply to `.z-orgnode`". A new theme that selects in the reverse direction will always fail.
7. **DOM assumptions** — iceblue writes `.z-grid > .z-grid-body > table > tbody > tr`, which suggests this path is stable; it can be written as a DOM assumption in the structural rules file, reminding future themes not to assume a different DOM shape.

### Things to always discard (Visual Values — DO NOT IMPORT)

| Item | Why it is discarded |
|---|---|
| Colors (hex, rgba, and named colors other than currentColor) | iceblue's palette is its own design and has nothing to do with Marble DESIGN.md |
| Concrete sizes (absolute px / em / rem values) | padding, margin, font-size, and border-radius are design trade-offs of density and shape |
| Concrete values of `font-family` and `font-weight` | Typography is decided by the theme |
| Concrete offset / blur / color of `box-shadow` | Elevation is decided by the theme, but **"has a shadow"** may be a C-class difference predicate |
| Values of `transition-duration` and `transition-timing-function` | Motion is decided by the theme, but **"has a transition"** can be kept |
| Border details other than `border-style` (width, color) | Border presence can be a C-class difference; concrete values are discarded |
| Any vendor hack, IE workaround, or deprecated syntax | Pure historical baggage |

Decision criterion: **"If this declaration's value changed completely and the visuals changed with it, would the component still be functional?"** Yes → visual value, discard. No (changing it would break something, remove a state, or make a difference invisible) → structural signal, keep.

### Workflow: deriving a reinforcement list for a structural rules file from iceblue CSS

For a given component (e.g. organigram):

1. **Locate the matching iceblue CSS file** (in the fallback order above).
2. **Extract all unique selectors**: `grep -oE '\.z-[a-z-]+' <comp>.css.dsp | sort -u` (rough version; compound selectors are handled separately).
3. **Compare against the State classes and DOM sections already in the structural rules file**, and find what iceblue mentions but the rules file does not cover:
   - Missing state class
   - Missing combined-state (a selector where two classes appear together)
   - Missing pseudo-element usage
4. **Classify each missing item**:
   - **A class (structural)**: a fact that the DOM/state always has → add to the DOM or State classes section of the rules file
   - **B class (relational)**: measurable geometry/alignment → add to the Relational invariants section
   - **C class (state difference)**: two states must differ along some dimension → add to the State-differs invariants section
   - **D class (visual value)**: discard
5. **Cross-check the widget JS source**: a class that iceblue CSS mentions but the JS never toggles may be dead code and must be verified; a class the JS toggles but iceblue CSS never uses may be "a hook ZK provides but iceblue ignores" — this kind **must still be written into the rules file**, because a new theme may use it.
6. **Do not update the theme contract** — the output of this workflow is a more accurate **structural rules file**, not a new theme contract. Marble's `doc/contracts/<comp>.md` should not change because of iceblue CSS.

### Two typical examples of "what iceblue CSS teaches us"

**Example 1: finding a missing combined-state**
If iceblue `organigram.css.dsp` contains `.z-orgitem-selected.z-orgitem-close > .z-orgnode { ... }`, this proves that "selected but closed" is a legal state combination and should be added to the "States to evaluate" list in the rules file.

**Example 2: finding a missing layout-critical pseudo-element rule**
iceblue writes `.z-orgnode { position: relative; }` together with `.z-orgnode::after { position: absolute; left: 50%; ... }` — this confirms that "`.z-orgnode` must be a positioning context". If other components' rules files do not yet state this kind of constraint, add it following this example.

### Discipline checks (things not to do)

- ❌ **Do not** paste a whole block of iceblue CSS into the structural rules file as a "reference implementation". The rules file only describes behavior and invariants; it contains no CSS code.
- ❌ **Do not** write sentences like "iceblue uses 8px padding here" in the rules file — that smuggles a visual value into the universal layer. If you need to write it, write a predicate like "padding must be ≥ 4px to avoid clipping".
- ❌ **Do not** put purely visual implementation details such as iceblue's z-index or background-image (sprite) into the rules file.
- ❌ **Do not** delete a state class from the rules file just because you found iceblue does not use it — iceblue may simply never have designed for it, which does not mean the widget will not emit it. **The ZK widget JS is the authority.**

---

## Refuse-to-emit rules (hard)

### Content written into `.claude/skills/zk-component-rules/components/<comp>.md` must never contain:

- Any hex (`#xxx` / `#xxxxxx`), `rgb()`, `rgba()`, or `hsl()`
- Any `var(--zk-*)` or `var(--md-*)` token reference
- Any absolute px/em/rem **visual value** (padding, margin, font-size, border-radius, box-shadow, etc.)
- Any reference to MD3 / MUI / Mira / iceblue / Sapphire / DESIGN.md terms or sections

**Exceptions** (these values may enter the structural rules file, because they express behavior rather than appearance):
- layout-critical positioning such as `position: relative | absolute`
- visibility contracts such as `display: none | block` (paired with a state class)
- interaction contracts such as `cursor: pointer | default`
- interaction-suppression contracts such as `pointer-events: none`
- the **property name list** of `transition` (excluding duration and easing values)

### Content written into `doc/contracts/<comp>.md` must never contain:

- Any DOM tree (ASCII tree, class hierarchy)
- Any description of state-class behavior (such as "ZK adds `.z-button-disabled` when…")
- Any framework-quirk description (DOM mutation, JS-injected attributes, etc.)
- Any `source` field written as "mirrors iceblue", "matches legacy", "same as ZK iceblue", or similar **smuggling with no design reason**

If you find yourself about to write any of the above, **stop** — it belongs on the other side.

---

## One-line version

> **Look at the baseline to extract semantic roles (states + relations + maintained distinctions); write those semantics as theme-independent predicates in the structural rules file; bind concrete colors and numbers to tokens in the theme contract. The evaluator verifies both layers, but neither layer is compared directly against baseline pixels.**
