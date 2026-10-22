# Marble Utility Class Naming — Review and Rename Proposal

**Question it answers:** Marble's 425 utility classes were named ad hoc as each category was
written. Now that the whole set exists, do the names hold together, and do they match what a
developer arriving from Bootstrap or Tailwind already has in their fingers?

**Status:** All five decisions made — **D1, D2, D3, D4 and D5 all settled on Option A.** D3 was
approved together with the verification method in §6.2, which is a condition of it, not a
suggestion: the two-phase sweep and the three gates must run, because D3 is the one change the
existing guards cannot see.

**All commits are landed and verified**, plus one follow-up fixing a file type the sweep missed
(§6.4). The rules this produced are now normative in `doc/spec/utility-naming.md`.

The D3 renumber cleared all four gates in §6.2: zero surviving old names after phase 1, an
identical computed font-size histogram on all 128 preview pages (128/128), every size class still
mapped to its intended MD3 role, and full coverage with stylelint clean.

The rename series was A/B'd against `15bc0c7eeb`, the commit before it, with the preview app
rebuilt and restarted on each side: **28 Playwright failures before, 28 after, the same 28**.
They pre-date this work — the screenshot baselines were last regenerated 2026-09-12, before the
LESS-to-Marble pipeline replacement landed.

**Why now:** Marble is `11.0.0-SNAPSHOT` and unreleased. Every consumer of these class names
today is inside this repo (159 `.zul` files, all under `zkpreview/`). After 11.0 ships, every
rename becomes a breaking change in customer markup that no deprecation cycle can cheaply
cover, because `sclass` is a plain string with no compiler behind it. **This is the last
window in which renaming is free.**

---

## 1. Summary

The set is in better shape than it looks. **Spacing — 121 of the 425 classes, the largest
category — matches Bootstrap 5 exactly** and needs no change. The problems are concentrated in
about 85 classes (20%), and they are not "names are too long". They are:

- two names for one rule (2 cases),
- one token meaning two different things depending on which family it appears in (the
  `-sm`/`-md`/`-lg` suffix),
- one scale that contradicts the convention it borrows its words from (`z-text-base`),
- one class squatting in ZK's component-class namespace (`z-card`).

Where names *are* longer than they need to be, the fix is to adopt Tailwind's shorter form
(`z-items-center`, `z-grow-0`, `z-gap-x-4`) rather than to invent a compression scheme.

### The originating question: should `z-m-1` become `z-m1`?

**No — recommend keeping `z-m-1`.** Four reasons, in order of weight:

1. **Both dominant conventions keep the hyphen.** Bootstrap 5 is `m-1`/`mt-3`/`px-2`; Tailwind
   is `m-1`/`mt-3`/`px-2`. Marble's `z-m-1` is *exactly* those, plus the mandatory `z-` prefix.
   That recognition is the single most valuable property the spacing scale has.
2. **The frameworks that dropped the hyphen are gone.** Basscss (`m1`, `p2`) and Tachyons
   (`ma1`, `pa2`) are the precedent for `z-m1`, and neither is maintained. Adopting a dead
   convention to save one character is a bad trade.
3. **It would not stay consistent.** `z-m-1` → `z-m1` is fine, but `z-m-auto` → `z-mauto` is
   not, and neither is `z-mb-16` → `z-mb16` next to `z-mb-auto`. Numbers would concatenate
   while keywords still needed a separator — one family, two grammars.
4. **It breaks the generated manifest's grammar.** `scripts/utility-manifest.js` and the IDE
   completion planned on top of it (see `doc/utility-class-discovery.md`) parse names as
   `prefix-property-value`. A no-separator variant makes that grammar irregular for one
   category only.

The cost being avoided is one character: `z-m-1` is 5 characters, `z-m1` is 4. The two
characters that actually make Marble's names longer than Bootstrap's are the `z-` prefix, and
that is non-negotiable — it is ZK's CSS namespace.

---

## 2. Reference conventions used in this review

| | Bootstrap 5.3 | Tailwind 3 | Marble today |
|---|---|---|---|
| spacing | `m-1` `mt-1` `px-2` `mx-auto` | `m-1` `mt-1` `px-2` `mx-auto` | `z-m-1` `z-mt-1` `z-px-2` ✅ |
| display | `d-flex` `d-none` | bare `flex` `hidden` | `z-d-flex` ✅ (Bootstrap) |
| responsive | **infix** `d-md-none` | **prefix** `md:hidden` | **suffix** `z-d-none-md` ⚠️ |
| print | **infix** `d-print-none` | **prefix** `print:hidden` | **infix** `z-d-print-none` ⚠️ |
| justify | `justify-content-center` | `justify-center` | `z-justify-center` ✅ (Tailwind) |
| align items | `align-items-center` | `items-center` | `z-align-center` ⚠️ |
| flex grow | `flex-grow-1` | `grow` | `z-flex-grow-1` ⚠️ |
| flex direction | `flex-column` | `flex-col` | `z-flex-col` ✅ (Tailwind) |
| width | `w-25 w-50 w-75 w-100` | `w-1/4 w-1/2 w-full` | `z-w-25 z-w-50 z-w-75 z-w-full` ⚠️ |
| gap axis | `column-gap-*` `row-gap-*` | `gap-x-*` `gap-y-*` | `z-col-gap-*` `z-row-gap-*` ⚠️ |
| font weight | `fw-normal` | `font-normal` | `z-fw-regular` ⚠️ |
| font style | `fst-italic` `fst-normal` | `italic` | `z-fst-italic` ✅ |
| line height | `lh-1 lh-sm lh-base lh-lg` | `leading-*` | `z-lh-1 z-lh-sm …` ✅ |
| text size | `fs-1`…`fs-6` | `text-xs`…`text-9xl` | `z-text-xs`…`z-text-7xl` ⚠️ |
| stacks | `hstack` `vstack` | — | `z-hstack` `z-vstack` ✅ |
| a11y hide | `visually-hidden` | `sr-only` | **both** ⚠️ |
| border | `border-0` `border-top` `rounded-*` | `border-0` `border-t` `rounded-*` | `z-border-top` `z-rounded-*` ✅ |
| position | `position-absolute` | bare `absolute` | `z-position-absolute` ✅ (Bootstrap) |
| shadow | `shadow-sm` | `shadow-md` | `z-elevation-1` ⚠️ (MD3) |

Marble's house style is already settled and defensible: **Bootstrap for the property
vocabulary, Tailwind where Tailwind's form is materially shorter and unambiguous.** Most of the
⚠️ rows below are places where that rule was applied inconsistently, not places where a new
rule is needed.

---

## 3. Category-by-category verdict

### 3.1 Spacing (121 classes) — **no change**

`z-m-*`, `z-mt/mb/ms/me/mx/my-*`, `z-p-*`, `z-pt/pb/ps/pe/px/py-*`, scale `0 1 2 3 4 5 6 7 8
10 12 16 auto` on a 4px base.

Identical to Bootstrap 5 in every respect except the scale depth (Bootstrap stops at 5,
Tailwind runs to 96). `ms`/`me` are Bootstrap 5's logical-property names, which is the correct
modern choice and matches `z-border-start`/`z-border-end` and `z-float-start`/`z-float-end`
elsewhere in the set. **This is the best-aligned category in Marble and should not be touched.**

### 3.2 Layout (145 classes) — **the bulk of the changes**

See §4 for the responsive-suffix question (D1), which alone accounts for 40 classes.

| Today | Proposed | Why |
|---|---|---|
| `z-align-start/center/end/stretch/baseline` | `z-items-*` | `align` is ambiguous across `align-items`, `align-content`, `align-self` and `text-align`; `z-align-center` next to `z-text-center` invites exactly the wrong guess. Tailwind's `items-*` is unambiguous **and shorter**. Marble already took Tailwind's `justify-center` over Bootstrap's `justify-content-center` — `z-items-center` is its consistent partner. |
| `z-flex-grow-0/1`, `z-flex-shrink-0/1` | `z-grow-0/1`, `z-shrink-0/1` | Tailwind's form. 15 chars → 10. Marble already uses Tailwind's `z-flex-col`, so the Bootstrap-length form here is the outlier. |
| `z-col-gap-0…6`, `z-row-gap-0…4` | `z-gap-x-0…6`, `z-gap-y-0…4` | Tailwind's form, and it visibly pairs with the existing `z-gap-*`. Also removes a collision of meaning: `col` means *flex/grid column axis* in `z-col-gap-*` but *grid column count* in `z-grid-cols-3`. |
| `z-w-full`, `z-h-full` | `z-w-100`, `z-h-100` | The siblings are `z-w-25/50/75` — Bootstrap percentages. `full` is Tailwind's word for the same slot, so the scale currently changes vocabulary at its last step. Bootstrap's `w-100` completes it. (Tailwind's `w-1/2` form is rejected: `/` needs CSS escaping and reads badly in `sclass`.) |
| `z-visually-hidden` + `z-sr-only` | keep `z-visually-hidden`, drop `z-sr-only` — **D2, decided** | Both names select the same single rule block; the kept name is the one that does not claim a removal that never happened. |
| `z-position-*`, `z-top/bottom/start/end-0`, `z-float-*`, `z-overflow-*`, `z-container`, `z-clearfix`, `z-flex-1/auto/none`, `z-grid-cols-*`, `z-min-w-0`, `z-min-h-0` | unchanged | Each matches Bootstrap or Tailwind exactly. `z-position-absolute` is long but is Bootstrap-verbatim, and a shortened `z-absolute` has no precedent and reads as a fragment. |
| `z-grid-fill-*`, `z-grid-col-full`, `z-grid-cols-auto-1fr`, `z-cq-*` | unchanged | ZK-specific coinages with no cross-framework precedent to align to. `z-grid-cols-auto-1fr` is the longest class in the set at 20 characters, but it is rare and self-describing. |
| `z-index-nav/loading/loadingbar/error/float-fallback` | unchanged, but note | These read as the CSS property `z-index` because the `z-` prefix merges with the property name. That happens to be *correct* here — they do set `z-index` — so it is a happy accident rather than a defect. Worth a line in the generated docs so nobody reads `z-index-error` as "the error index". |

### 3.3 Typography (36 classes)

| Today | Proposed | Why |
|---|---|---|
| `z-fw-regular` | `z-fw-normal` | The CSS keyword is `normal`; Bootstrap is `fw-normal`; and Marble's own sibling is already `z-fst-normal`. One family should not say `regular` and `normal` for the same idea. |
| `z-text-md` (13px) + `z-text-base` (14px) | **D3** | `base` means "the default size" in Tailwind. In Marble the default body size is 13px = `z-text-md`, while `z-text-base` is 14px — one step *larger*. A developer will reach for `z-text-base` expecting the body size and get the wrong one, silently. |
| `z-h1`…`z-h7` | unchanged | Bootstrap has `h1`–`h6`; `z-h7` is a documented Marble extension. Note the near-miss with the height family (`z-h-25`): `z-h1` vs `z-h-1` differ by one hyphen, but `z-h-1` does not exist, so there is no live collision. |
| `z-fw-*`, `z-fst-*`, `z-lh-*`, `z-text-start/center/end`, `z-text-uppercase/lowercase/capitalize/none`, `z-text-nowrap` | unchanged | Bootstrap-verbatim. |

The `z-text-*` prefix carrying size, alignment, transform *and* colour is not a defect — Tailwind
overloads `text-` identically (`text-sm`, `text-center`, `text-red-500`). Leave it.

### 3.4 Colors (28 classes)

| Today | Proposed | Why |
|---|---|---|
| `z-text-secondary` + `z-text-muted` | drop `z-text-muted` | Both resolve to `--zk-color-on-surface-variant`. Bootstrap 5.3 deprecated `text-muted` for exactly this reason. Keep the name that matches the MD3 token layer. |
| `z-bg-surface-low` | `z-bg-surface-container-low` | It maps to `--zk-color-surface-container-low`, but its sibling `z-bg-surface-container` maps to `--zk-color-surface-container`. As written, `z-bg-surface-low` looks like a variant of `z-bg-surface`, not of `z-bg-surface-container`. This is the one place where the *longer* name is the right call: the abbreviation actively misleads. |
| everything else | unchanged | `z-text-*` / `z-bg-*` are Bootstrap-verbatim with MD3 role names. |

### 3.5 Borders (13 classes) — **no change**

`z-border`, `z-border-0`, `z-border-top/bottom/start/end` are Bootstrap-verbatim.
`z-rounded-none/xs/(bare)/md/lg/xl/full` matches Tailwind's shape, including Tailwind's own
wart that the bare `rounded` sits inside the scale (Marble's bare form is `shape-corner-small`).
No `z-rounded-sm` exists; the generated index should state that the bare form *is* small.

### 3.6 Elevation (4 classes) — **no change**

`z-elevation-0…3`. Bootstrap says `shadow-*` and Tailwind says `shadow-*`, so this diverges —
deliberately. Marble is an MD3 theme, the tokens are `--zk-elevation-*`, and MD3's own word is
"elevation". Consistency with the token layer directly beneath these four classes outweighs
cross-framework familiarity, and the blast radius is four names.

### 3.7 Stack (11 classes) — **no change to the base names**

`z-hstack`/`z-vstack` are Bootstrap-verbatim. The `-sm`/`-md`/`-lg` suffixes here are **gap
sizes, not breakpoints** — see D1, which is what makes that ambiguity worth fixing.

### 3.8 Print (5 authorable classes) — see D1

`z-d-print-none/block/flex/grid/inline-block` is Bootstrap-verbatim. The problem is not this
family; it is that this family uses the infix slot for its modifier while the responsive family
uses the suffix slot.

### 3.9 Components (1 class) — **D5**

`z-card` is the only entry in `_components.css`, and it is the riskiest name in the set.

---

## 4. Decisions required

### D1 — The `-sm`/`-md`/`-lg`/`-xl` suffix means two different things — **DECIDED: Option A**

**Background.** In `z-d-*` and `z-cq-*`, a trailing `-md` means *"at viewport/container width
≥ 900px"*. In `z-hstack-*`, `z-vstack-*`, `z-grid-fill-*`, `z-rounded-*`, `z-text-*` and
`z-lh-*`, a trailing `-md` means *"the medium size"*. Same four tokens, two unrelated meanings,
and nothing in the name tells you which:

```
z-d-block-md     →  display:block at ≥900px        (a breakpoint)
z-hstack-md      →  gap: 16px, at every width      (a size)
z-grid-fill-md   →  min column width 200px         (a size)
```

Separately, the modifier slot is inconsistent *within one family*: viewport breakpoints are a
**suffix** (`z-d-none-md`) while print is an **infix** (`z-d-print-none`). Bootstrap puts both
in the infix slot; Tailwind puts both in the prefix slot. Marble is the only one that splits
them.

`_layout.css` currently justifies the suffix as "aligned with MUI/Tailwind". That alignment is
real for the *breakpoint pixel values* (600/900/1200/1536 is the MUI scale) but not for the
*name ordering* — Tailwind writes `md:flex`, putting the breakpoint first. The comment
overstates its case and should be corrected either way.

**Impact if left alone.** Every reader must memorise which families take size suffixes and which
take breakpoint suffixes. Autocomplete cannot disambiguate them. It also blocks ever adding a
responsive variant to a family that already uses `-md` for size — `z-hstack-md` is taken, so
"hstack at md and up" has no name available.

**Options**

- **【Option A】Move breakpoints to the infix slot — CHOSEN** — `z-d-none-md` →
  `z-d-md-none`, `z-cq-flex-lg` → `z-cq-lg-flex`. Print already sits there, so the two modifier
  families unify with no further change. Suffixes then mean "size" everywhere, with no
  exceptions. Result is Bootstrap-verbatim (`d-md-none`), which is the form most developers have
  already seen.
  **Cost:** 40 class renames, mechanical; plus a `sed` sweep over `zkpreview/` and the
  `_layout.css` header comment.

- **【Option B】Move print to the suffix slot** — `z-d-print-none` → `z-d-none-print`. Keeps the
  existing 40 responsive names untouched and fixes only the infix/suffix split.
  **Cost:** 5 renames. **But it does not fix the size-vs-breakpoint collision at all**, and it
  abandons a Bootstrap-verbatim name (`d-print-none`) to do it.

- **【Option C】Leave it, document it.** Zero cost now. The collision becomes permanent at 11.0.

### D2 — `z-visually-hidden` and `z-sr-only` are the same rule — **DECIDED: Option A**

**Background.** Both names are on one selector list in `_layout.css`. Two public names for one
behaviour means two things to document, two to complete, and two to keep in sync forever.

The rule itself is the well-known clip-rect pattern: the element occupies no visual space but
**stays in the accessibility tree and is still announced by screen readers**. That second half
is the whole point of the utility, and it is what makes naming it hard — the honest name has to
convey "invisible, but *not* removed".

Marble therefore has three distinct hiding concepts to keep apart, only two of which are
classes:

| Intent | Visible? | In the a11y tree? | Marble |
|---|---|---|---|
| Remove from everyone | no | **no** | `z-d-none` (`display:none`) |
| Invisible but announced | no | **yes** | *this utility* |
| Visible but not announced | yes | no | `aria-hidden` attribute — not a class |

**Options**

- **【Option A】Keep `z-visually-hidden`, drop `z-sr-only` — CHOSEN** — Bootstrap 5
  deliberately renamed `sr-only` → `visually-hidden` because the content is *not* screen-reader
  only: it is reachable by any assistive technology and by find-in-page. The name states exactly
  the one property that distinguishes it from `z-d-none` — hidden *visually*, and only visually.
  Accessibility reviewers read it as the correct term.
  **Cost:** 1 removal; it is the less-used of the two in `zkpreview/`.

- **【Option B】Keep `z-sr-only`, drop `z-visually-hidden`** — 9 characters instead of 17, and it
  is Tailwind's name, which is still the more widely typed of the two.
  **Cost:** 1 removal, and Marble carries a name its own reference framework has retired for
  being inaccurate.

- **【Option C】Rename to `z-hidden` — rejected, and it is the only option here that is actively
  unsafe.** Three independent reasons:
  1. **It collides head-on with `display:none`.** In Tailwind, `hidden` *is* `display:none` —
     the single most widely typed hiding class in front-end work. A developer who writes
     `z-hidden` expecting the element to be gone instead gets an element that is invisible but
     **still read aloud**. The failure is silent: the page looks right, and only a screen-reader
     user discovers the content that was supposed to be removed.
  2. **It contradicts the platform.** HTML's own `hidden` attribute and the `[hidden]` selector
     both mean "removed for everyone". `z-hidden` reads as the class form of that and means the
     opposite in the one respect that matters.
  3. **It is ambiguous across all three rows of the table above.** `hidden` names the outcome
     without naming the audience, and the audience is the entire distinction being drawn.

  `z-sr-only` is merely imprecise — it understates who can reach the content. `z-hidden` is
  wrong in the dangerous direction: it claims a removal that did not happen. Marble already owns
  `z-d-none` for real removal, so adding `z-hidden` beside it would put two hiding names in the
  set whose behaviour differs in a way neither name discloses.

This is the one case in the review where the **longer** name is recommended: the short candidates
are not just terser, they describe a different behaviour from the one the rule implements.

### D3 — `z-text-base` is not the base size

**Background.** The scale runs `xs 11 / sm 12 / md 13 / base 14 / lg 16 / xl 22 / 2xl 24 …`.
Marble's default body size is 13px — `z-text-md`. But `base` is Tailwind's word for *the default
size*, so a developer reaching for `z-text-base` to mean "normal text" gets 14px, one step too
big, with no error and no visible symptom until a designer notices the drift.

**Options**

- **【Option A】Renumber to a pure T-shirt run (recommended)** — drop the word `base` entirely:
  `xs 11 / sm 12 / md 13 / lg 14 / xl 16 / 2xl 22 / 3xl 24 / 4xl 28 / 5xl 32 / 6xl 36 / 7xl 45
  / 8xl 57`. Monotone, no English word carrying a borrowed meaning, and it keeps all 12 steps.
  Marble's px anchors already differ from Tailwind's (Tailwind `text-xl` is 20px, Marble's is
  22px), so nothing is lost that was not already lost.
  **Cost:** 9 renames. Every `z-text-{2..7}xl` shifts up one, which is the risky part — a stale
  reference silently picks the neighbouring size rather than failing. Mitigated by the fact that
  all consumers are in this repo and the manifest build verifies every class resolves.
- **【Option B】Swap the two names** — `z-text-base` becomes 13px, `z-text-md` becomes 14px.
  **Cost:** 2 classes change *meaning while keeping their names*, which is the worst kind of
  silent breakage; and the run still reads `xs sm base md lg`, which is not an ordering anyone
  can guess.
- **【Option C】Drop `z-text-base`, leave 14px without a size utility.**
  **Cost:** 1 removal, no renumbering, but the scale loses a step that MD3 uses for
  `title-small` and `label-large`.

### D4 — Should utilities get their own sub-prefix? — **DECIDED: Option A (no `zu-` prefix)**

**Background.** Marble's utilities and ZK's component classes share one namespace. Today
`z-grid-fill` (utility) sits beside `z-grid-header` (Grid component part); `z-card` (utility)
sits beside `z-window` and `z-panel` (components) in the very same `_print.css` selector list.
Nothing collides *today*, but nothing prevents it either, and a reader cannot tell which kind of
class they are looking at.

**Options**

- **【Option A】Leave the shared `z-` prefix — CHOSEN** — the collision risk is real but
  narrow, and it is what buys the Bootstrap/Tailwind recognition that makes `z-m-1` readable at
  a glance. Manage the risk by rule instead: no new utility may take a bare component-shaped
  noun (see D5), and the generated index labels each entry's category.
  **Cost:** none now; requires the naming rule below to be written down and followed.
- **【Option B】Move utilities to `zu-`** — `zu-m-1`, `zu-d-flex`. Zero collision risk forever,
  trivially greppable, and IDE completion can filter on the prefix alone.
  **Cost:** all 425 classes rename, every `zkpreview` page, the manifest, the skill docs and the
  slide deck. And it costs the recognition: `zu-m-1` no longer reads as Bootstrap's `m-1`.

### D5 — `z-card` squats in the component namespace — **DECIDED: Option A (`z-paper`)**

**Background.** Every ZK widget renders `z-<widgetname>`. `z-card` is shaped exactly like a
component class, is grouped with real component classes in `_print.css` and
`_forced-colors.css`, and `zkmax` already ships the adjacent `z-cardlayout`. If ZK ever adds a
Card component — a normal thing for an MD3-aligned framework to do — both definitions would
target `.z-card` from different cascade layers, and the utility layer would silently override or
be overridden by the component's own tuning depending on layer order. That is worse than a plain
name clash, because nothing fails loudly.

The rule is small: `background-color: surface`, `border-radius: var(--zk-shape-card)`,
`box-shadow: var(--zk-elevation-card)`, `padding: var(--zk-spacing-4)`, `overflow: hidden`.
19 call sites in `zkpreview/`.

**Complication found while evaluating the rename.** "Card" is already Marble's *token* vocabulary
for this treatment: `--zk-shape-card` and `--zk-elevation-card` are consumed by Grid, Listbox,
Tree, Panel, Groupbox and Calendar — none of which are cards. So the token layer uses "card"
abstractly to mean "the standard elevated-surface treatment", and the utility class exposes that
same treatment. **Any rename that abandons the word splits the class vocabulary from the token
vocabulary**, unless the tokens are renamed too (a larger job touching six components).

**Candidate screen.** Checked against `zul/lang.xml`, `zkmax` and `zkex` component registries,
and against the plausibility of ZK adding such a component later:

| Candidate | Len | Registered today? | Future-component risk | Verdict |
|---|---|---|---|---|
| `z-card` | 6 | free | **high** — every MD3-aligned framework ships a Card | the problem |
| `z-box` | 5 | **taken** — `Box` is a real ZK component | — | rejected |
| `z-sheet` | 7 | free | **high** — bottom-sheet, plus Keikai/ZSS use "sheet" constantly | rejected |
| `z-tile` | 6 | free | medium | weak |
| `z-paper` | 7 | free | **low** — MUI-specific vocabulary; ZK would not name a widget "Paper" | viable |
| `z-surface` | 9 | free | **low** — an abstract role, not a widget noun | viable |
| `z-surface-card` | 14 | free | low | viable but long |

**Options**

- **【Option A】`z-paper` — CHOSEN** — MUI's `<Paper>` is precisely this primitive: the
  elevated surface every other surface is built on. 7 characters, one more than `z-card`. It is
  the only short candidate that collides with nothing, present or plausible, and it cannot be
  mistaken for a ZK widget class because ZK has no Paper and never will.
  **Cost:** 1 rename × 3 CSS files + 19 `zkpreview/` call sites. Imports one word Marble does not
  otherwise use, and leaves `--zk-shape-card`/`--zk-elevation-card` still saying "card".

- **【Option B】`z-surface`** — MD3's own word, and the rule's background is already
  `--zk-color-surface`. 9 characters.
  **Cost:** same rename cost, but it sits awkwardly beside the existing `z-bg-surface` and
  `z-bg-surface-variant` in `_colors.css` — a reader cannot tell from the names that `z-surface`
  is the full treatment while `z-bg-surface` is only the colour. That ambiguity is the reason
  this is second and not first.

- **【Option C】Keep `z-card`, and record the reservation** — write into `doc/spec/` that a future
  Card component must pick another class name, and that no new utility may take a component-shaped
  noun. **The D4 decision makes this more coherent than it looked**: having chosen to manage the
  shared namespace by rule rather than by prefix, `z-card` becomes the first case that rule
  governs rather than an exception to it. Also the only option that keeps class and token
  vocabulary aligned.
  **Cost:** free now. Pays later, and the person who pays is not the person deciding — the
  eventual Card component either takes a worse class name or forces a breaking change.

- **【Option D】`z-surface-card`** — the original proposal. Unambiguous and keeps the word, but at
  14 characters it is the second-longest name in the set after `z-grid-cols-auto-1fr`, for a
  utility used 19 times. Rejected on length.

---

## 5. Proposed change set, if all recommendations are accepted

| Category | Classes touched | Nature |
|---|---|---|
| Spacing | 0 | — |
| Responsive display (D1-A) | 40 | suffix → infix |
| `z-align-*` → `z-items-*` | 5 | rename |
| `z-flex-grow/shrink-*` → `z-grow/shrink-*` | 4 | rename |
| `z-col-gap-*`/`z-row-gap-*` → `z-gap-x-*`/`z-gap-y-*` | 12 | rename |
| `z-w-full`/`z-h-full` → `z-w-100`/`z-h-100` | 2 | rename |
| Text size renumber (D3-A) | 9 | rename |
| `z-fw-regular` → `z-fw-normal` | 1 | rename |
| `z-bg-surface-low` → `z-bg-surface-container-low` | 1 | rename |
| `z-card` → `z-paper` (D5-A) | 1 | rename |
| `z-sr-only` (D2-A) | 1 | removal |
| `z-text-muted` | 1 | removal |
| **Total** | **77 of 425 (18%)** | 75 renames, 2 removals |

348 classes — including the entire spacing scale — are unchanged.

---

## 6. Verification and commit plan

### 6.1 Why the obvious guard is not enough

The existing guard (`check-css-dsp.js` plus the manifest build) answers *"does every class in the
manifest resolve to a rule in the built bundle?"*. That catches a class deleted from the CSS and
still referenced in markup — the reference becomes an orphan and the guard fires.

**It is blind to the failure mode that matters here.** These are renames *within a closed set*.
If a `sed` rule misses a call site, the stale name usually still exists — it has simply been
reassigned to a different value:

| Change | Missed call site resolves to | Guard fires? |
|---|---|---|
| `z-d-none-md` → `z-d-md-none` (D1) | nothing — `z-d-none-md` is gone | ✅ yes, loudly |
| `z-align-center` → `z-items-center` | nothing — old name gone | ✅ yes |
| `z-card` → `z-paper` (D5) | nothing — old name gone | ✅ yes |
| **`z-text-2xl` 22px → 24px (D3)** | **`z-text-2xl`, still valid, now one step bigger** | ❌ **silent** |

Every decided change (D1, D2, D4, D5 and the mechanical renames) removes the old name, so a miss
produces an unstyled element and is caught. **D3 alone is a shift, not a rename** — nine names
survive with new meanings, so a miss produces a *plausible but wrong* result that no existing
check can see. That asymmetry is why D3 needs its own method.

Screenshots do not close the gap either. `screenshot.spec.ts` runs `maxDiffPixels: 20` on padded
state crops; a 13px→14px label on a short string can land inside that floor. Screenshots stay as
a secondary signal, never the D3 gate.

### 6.2 Proposed method for D3 — make the silent failure loud

Three layers. The first is the one that actually matters.

**Layer 1 — Two-phase rename, so a miss cannot stay silent.**

Do not rename `z-text-2xl` → `z-text-3xl` directly. Rename through a temporary namespace that
collides with nothing:

```
phase 1:  z-text-{xs,sm,md,base,lg,xl,2xl…7xl}  →  z-tmptext-{11,12,13,14,16,22,24,28,32,36,45,57}
phase 2:  z-tmptext-{px}                        →  the final name for that px value
```

This does two jobs at once:

1. **It removes the silent-failure property.** After phase 1, *no* `z-text-*` size class exists.
   Any call site the sweep missed is now an orphan — unstyled, visible, and caught by the
   existing coverage guard. The bug class is converted from "wrong value" to "no value".
2. **It prevents double-application**, which is a real hazard here and not a hypothetical. A
   naive chained sweep (`2xl→3xl`, then `3xl→4xl`, …) re-matches its own output and walks every
   class up the scale by several steps. Routing through px-keyed temporaries makes each token
   rewritten exactly once.

**Gate 1 — after phase 1, `grep -rE 'z-text-(xs|sm|md|base|lg|xl|[2-7]xl)\b'` over `**/*.zul`,
`**/*.ts`, `**/*.java` and `doc/**/*.md` must return zero hits.** Any hit is a missed call site,
named and located, before it can be disguised by phase 2.

**Layer 2 — Computed font-size equivalence, before vs after.**

A rename must be a visual no-op. `zkpreview/src/test/playwright/` already asserts computed styles
this way — `responsive-utilities.spec.ts` reads `getComputedStyle(n).display` — so the harness
exists and this is a new spec, not new infrastructure.

1. Before any edit, walk every page in `zkpreview/src/main/webapp/web/`, and for every element
   carrying a class matching `z-text-*`, record `{page, stable selector, computed fontSize}` to
   a baseline JSON.
2. After both phases, re-record.
3. **Gate 2: the diff must be empty.** A non-empty diff names the page and the element, and the
   px delta says which direction the scale slipped.

This is the gate that catches the case Layer 1 cannot: a call site that *was* rewritten, but to
the wrong step.

**Layer 3 — Manifest purity.**

Dump the manifest before the edit, apply the intended rename map to it mechanically, and diff
against the manifest generated after. **Gate 3: identical.** This proves the CSS edit was a pure
rename — that no declaration, `var()` reference or media prelude changed while names were being
moved. It catches a fat-fingered `--zk-typescale-*` swap that Layers 1 and 2 would both pass if
the markup happened to be consistent with it.

**Gate 4 (all decisions, not just D3):** manifest count lands on the expected number, every entry
resolves in the built `norm.css.dsp`, and the full Playwright suite is clean.

### 6.3 Commit plan — one commit per decision

Each commit is independently revertible and independently verifiable. Order matters only in that
the mechanical renames are cheapest to review first and D3 is riskiest last.

| # | Commit | Scope | Gate before moving on |
|---|---|---|---|
| 0 | `ZK-6112: record the utility class naming review` | this document only | — (doc only; currently untracked) |
| 1 | `ZK-6112: shorten the flex and gap utility names` | `z-align-*`→`z-items-*`, `z-flex-grow/shrink-*`→`z-grow/shrink-*`, `z-col-gap-*`/`z-row-gap-*`→`z-gap-x/y-*` (21 classes) | Gate 4 |
| 2 | `ZK-6112: complete the width and height percentage scale` | `z-w-full`/`z-h-full` → `z-w-100`/`z-h-100` (2) | Gate 4 |
| 3 | `ZK-6112: drop the duplicate utility class aliases` | remove `z-sr-only` (D2), remove `z-text-muted` (2) | Gate 4 |
| 4 | `ZK-6112: align the font weight and surface colour names` | `z-fw-regular`→`z-fw-normal`, `z-bg-surface-low`→`z-bg-surface-container-low` (2) | Gate 4 |
| 5 | `ZK-6112: rename the card utility to z-paper` | D5 — 3 CSS files + 19 call sites (1) | Gate 4 |
| 6 | `ZK-6112: move responsive breakpoints to the infix slot` | D1 — 40 classes, plus the `_layout.css` header comment correction, plus `responsive-utilities.spec.ts` | Gate 4 + `responsive-utilities.spec.ts` green |
| 7 | `ZK-6112: add the font-size equivalence baseline` | Layer 2 spec + baseline JSON, **before** any D3 edit | baseline captured and committed |
| 8 | `ZK-6112: renumber the font size scale` | D3 — 9 classes via the two-phase sweep | **Gates 1, 2, 3 and 4** |
| 9 | `ZK-6112: record the utility naming rules` | `doc/spec/utility-naming.md` (the seven rules in §5) | — |

Commits 1–5 are decided and unblocked. Commit 6 is decided. Commits 7–8 are blocked on D3.
Commit 7 must land *before* 8 or the baseline is worthless — it has to be captured against the
pre-rename tree.

Note on commit 6: `_layout.css` currently claims the suffix ordering is "aligned with
MUI/Tailwind". MUI matches on breakpoint *values* (600/900/1200/1536); Tailwind does **not** match
on name ordering — it writes `md:flex`, breakpoint first. The comment gets corrected in the same
commit that changes the ordering.

### 6.4 Sweep mechanics that apply to every commit

Written against what commits 1–6 actually hit. Each bullet after the first three cost a defect.

- Anchor on word boundaries with `-` excluded on both sides:
  `(?<![\w-])z-old-name(?![\w-])`. `sclass` is space-separated, and an unanchored match
  corrupts `z-text-xl` inside `z-text-2xl` — and, worse, rewrites `z-cardlayout` (zkmax) when
  renaming `z-card`.
- Longest name first within a single pass, or route through temporaries as in §6.2.
- Exclude `../zkcml` entirely, and `zkpreview/build/` — build output regenerates.
- **Sweep every file type, not just the obvious ones.** Commits 1–6 swept
  `.zul .ts .js .java .md .css .dsp` and missed `zkpreview/doc/focus-ring-known-clips.json`,
  whose entries key on a string embedding the clipping ancestor's whole class list
  (`".z-a ⊂ DIV.z-mb-6…z-col-gap-6.z-row-gap-2.z-align-start.z-div"`). Renaming those classes
  silently invalidated two long-known baseline entries, which the scan then reported as new
  focus-ring regressions on the `a` and `button` pages. **Verify with a repo-wide grep over
  every file type, with no extension filter at all**, not over the list you chose to sweep.
- **Exclude this document from the sweep.** It records old → new pairs; a sweep rewrites the
  "before" column and destroys the mapping. It is the one file that must keep the old names.
- **A removal is not a rename.** Rewriting the call sites of a deleted class to its survivor is
  correct in component markup but wrong in a gallery page, where it produces two identical demo
  rows (`.z-text-muted` became a second `.z-text-secondary` swatch) or a caption naming the same
  class twice. After any removal, grep the gallery pages for duplicated demo labels.
- Generated files need their **generator** changed too, or the next run reinstates the old name.
  `scripts/build-css.js` emits `icons-lucide.zul`; its template held `z-align-center`.
- Some files in the tree belong to other in-flight work (`scripts/build-css.js`,
  `scripts/utility-manifest.js`, `tasks/slides/`). Sweep them for correctness, but stage only your
  own hunks — `git show HEAD:<file>` piped through the same rename, then `git hash-object -w` +
  `git update-index --cacheinfo`, stages one line without touching a neighbour's work.
- Screenshot specs of the "visual review" kind (`forced-colors-gallery.spec.ts`) **rewrite** their
  baseline PNGs on every run. Running the full suite dirties ~100 tracked images; revert them
  before staging.

---


## 7. The naming rules this review implies

Worth recording regardless of which options are chosen:

1. Property vocabulary follows Bootstrap 5; adopt Tailwind's form only where it is both shorter
   *and* unambiguous (`items-`, `grow-`, `gap-x-`).
2. Keep the hyphen between property and value, always. `z-m-1`, never `z-m1`.
3. One rule, one name. No aliases.
4. A trailing `-xs/-sm/-md/-lg/-xl` always means a size. Breakpoints live in the infix slot.
5. Logical properties over physical: `start`/`end`, never `left`/`right`.
6. A utility never takes a bare noun that could name a ZK component.
7. Prefer the accurate name over the short one when they conflict (`visually-hidden`,
   `surface-container-low`).

---

## 8. Related documents

- `doc/utility-class-discovery.md` — how the class list is generated and shipped; its Phase 3
  generates `doc/spec/utility-index.md`, which should be written *after* any renames land.
- `doc/spec/spacing-policy.md` — the 4px scale these class names expose.
- `doc/spec/responsive-design.md` §5 — the MUI breakpoint values referenced by D1.
