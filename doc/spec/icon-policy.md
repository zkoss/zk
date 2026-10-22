# Icon Policy

This theme delivers all icons via the [Lucide](https://lucide.dev/) icon set, rendered with CSS `mask-image` driven by per-icon SVG files served by URL (`url(~./zul/img/icons/<name>.svg)`). There are two distinct sources of `z-icon-*` class references:

| Source | Examples | Naming rule |
|--------|----------|-------------|
| ZK widget JS (compiled, immutable) | `<i class="z-icon-caret-down">` injected by `Scrollbar.ts`; `domIconHTML('left')` in `tab/mold/tabbox.js` emitting `z-icon-chevron-left` | Cannot rename. FA-style names that are not in Lucide MUST be aliased in `FA_TO_LUCIDE` (see `scripts/build-css.js`). |
| Preview / example ZULs (`zkpreview/src/main/webapp/web/**/*.zul`) | `iconSclass="z-icon-settings"`, `sclass="z-icon-trash-2"` | MUST resolve to a **served** class: a real Lucide name **or** a ZK built-in FA/custom alias (`FA_TO_LUCIDE` / `CUSTOM_ICONS`). Invented / misspelled names are FORBIDDEN. Lucide names are preferred for new content. |

## Library decision: keep Lucide for ZK 11.0

Lucide stays the icon library for ZK 11.0. The original evaluation is kept locally under `tasks/` (gitignored, not part of the
repository); its §13, "Recommendation: keep Lucide for 11.0", is the record of this decision.

Reasons:

- Lucide passes gates G1–G6 and G7 (see [Gates](#gates-for-an-icon-library-change)).
- G7 (Marble / Material Design): Lucide uses a 24×24 grid with 2px strokes and rounded
  terminals. This matches Material's outlined specification numerically, and matches the
  Material Symbols *Rounded* style in terminal treatment.
- Fill-as-state, the capability Material Design requires for outlined→filled state, is in
  production in `rating.css` (outlined star and filled star).
- Material Symbols (MD3's reference set) and Lucide tied on the weighted score. The tie was
  broken by switching cost. Material Symbols' advantage is its optical-size axis for small
  sizes. Its runtime cost was not measured; the evaluation judged it likely worse over the
  wire. The switching cost was an icon-layer rebuild plus full screenshot re-baselining.
- G7 eliminated Font Awesome Free, Bootstrap Icons and Heroicons. It did not eliminate Lucide.
- Being MD3's official icon set is not a quality test (see the G7 note below).

## Decisions behind this policy

The IDs below (D13 etc.) are the decision ids used in the original evaluation kept locally under
`tasks/` (gitignored, not part of the repository).

### D13: B — keep the full catalog; no subsetting

- Emitting the full catalog makes compression behaviour the dominant cost factor, so the
  payload must be measured over the wire (gzipped), not in raw bytes.
- Measured over the wire, the cost of the full catalog is lower than the raw byte count
  suggests. Subsetting is therefore not needed.
- Subsetting is retained as a criterion (see [Runtime cost](#runtime-cost)) for future
  re-evaluation.

### D14: customers who need Font Awesome (including brand logos) stay on `iceblue11` in ZK 11

- Font Awesome is retired per theme, not from the framework: Marble drops it, `iceblue11`
  keeps it. Font Awesome is not part of Marble; per the evaluation, it is provided by the iceblue
  theme, and the contents of the iceblue theme jar are outside this repository. ZK's own font-awesome.css.dsp sets `font-family: ZK85Icons, FontAwesome` on `z-icon-*` elements, and Marble resets that font-family in `_icons.css` so that no Font Awesome glyph bleeds through; the icon is rendered entirely by the `::before` mask-image, not by a font glyph.
- Brand logos are not a Marble gate. Marble documentation should describe an alternative
  (the evaluation record gives Simple Icons as an example).
- The Font Awesome name gap is a theme-choice migration note, not a hard break.

### D15: closed — premise invalid

- Question: does G7 apply retroactively to disqualify Lucide?
- Answer: no. Lucide passes G7, so there is no retroactive change, and Lucide is kept for 11.0.

## Gates for an icon library change

Any FAIL eliminates a candidate; a failed candidate is not scored.

| # | Gate | Why | How to test |
|---|------|-----|-------------|
| G1 | Licence permits redistribution inside a commercial, closed-source artifact, perpetually, with no per-seat or per-app fee | ZK EE is sold commercially; a "free for personal or open-source use" set is unusable | Read the LICENSE file in the published package, not the marketing page. Prefer MIT / ISC / Apache-2.0 / SIL-OFL |
| G2 | Attribution is satisfiable by a NOTICE file, not by visible UI credit | A set that demands on-screen credit cannot ship in a customer's product | Check the licence text for "visible" / "reasonable credit" wording; confirm the vendor's FAQ accepts NOTICE-only |
| G3 | Fully offline and self-hostable: no CDN, no account, no "kit" or API key | Enterprise ZK deployments are routinely air-gapped | Install the package, cut the network, render a page |
| G4 | Works with CSS only, no mandatory JS runtime | ZK applies icons server-side via `iconSclass` and re-patches the DOM on every AU response; a JS rewrite of `<i>` into `<svg>` fights that and breaks on partial updates | Render an icon with only a stylesheet and a class name |
| G5 | Covers the T1 required inventory with no gap that leaves a widget visually broken | A missing sort arrow or tree caret is a broken component, not a cosmetic issue | Diff the name list against T1; every gap needs an acceptable substitute or a hand-authored glyph |
| G6 | Package is versioned and immutable (npm / Maven, pinnable) | Reproducible builds; a moving target cannot ship in an LTS | Pin an exact version, rebuild twice, diff the output |
| G7 | Icon style conforms to the consuming theme's design language | A theme's credibility is its coherence. An icon set drawn in a competing design language's voice contradicts the theme on every screen | Three-level verdict (below). Evaluated per shipped theme: with D14, `iceblue11` keeps Font Awesome, so only Marble / Material is in scope |

### G7 verdict levels

| Verdict | Definition | Test |
|---------|------------|------|
| PASS | The design language's own reference icon system, or documented by its authors as conformant | Is it the set the design spec itself points at? |
| CONDITIONAL | Stylistically compatible (same grid family, geometric or neutral voice, no clash), but not the reference set, and/or missing a state-expression mechanism the design language requires | Does it carry a different framework's visual identity? Can it express selected state the way the design language mandates? |
| FAIL | Carries a competing design language's identity, or clashes with the theme's style | Would a designer recognise it as "that other framework's icons"? |

For Marble / Material Design the sub-tests are:

1. **Drawing style**: 24×24 grid, about 2dp stroke, consistent terminals and corner treatment.
   This is what the gate is for: does a designer read the icons as part of the same system as
   the components?
2. **Fill-as-state capability**: MD3 expresses selected state by switching an icon from
   outlined to filled. The set must be able to supply a filled counterpart for closed-path
   icons that carry state (star, heart, bookmark, flag). This is a capability test, not an
   "official set" test.

> Do NOT use "is it the design language's official icon set" as the test. It is a much
> stronger requirement than style conformance, and it reduces G7 to a single-vendor check
> (for Material Design, only Material Symbols could pass). Judge the drawing, not the provenance.

## Evaluation criteria

Two passes: first the gates above; then a weighted score over the criteria below, for
survivors only. The scoring sheet and its weights are not carried into this spec.

Three things make a framework vendor's evaluation differ from an app team's:

1. **We redistribute.** Icons ship inside the theme jar to many downstream apps, some air-gapped,
   all running OSS licence scanners. Legal and packaging constraints are gates, not scores.
2. **We define an API.** `iconSclass="z-icon-caret-down"` is public API. Icon names are API
   surface; renaming or losing one is a breaking change.
3. **We pay the cost once, and everyone pays it forever.** Payload, a11y behaviour and visual fit
   are multiplied across every ZK page.

### Coverage

Score coverage against a fixed required list, not raw icon count. A large set that lacks
`caret-down` is worse than a smaller set that has every glyph ZK emits. Total count is only
headroom for application developers.

| Tier | Definition | Gap severity |
|------|------------|--------------|
| T1, structural | Emitted by widget JS; absence breaks a component | Blocking (G5) |
| T2, API surface | Named in ZK docs or used by existing customer apps | Breaking change; alias or migration note |
| T3, content | Used only by examples, demos and preview pages | Substitutable |

T1 is the set the compiled widget JS emits (see Rule 1 below). Because T1 is entirely Font Awesome
vocabulary, every non-Font-Awesome candidate needs an alias layer.

Other coverage criteria: percentage of Font Awesome names with a 1:1 visual equivalent (the
migration cost); application-developer headroom and business-domain coverage; outlined and
filled variants (and any weight axes) in the free tier; RTL and directional mirrors (a
`mask-image` icon does not mirror automatically); metaphor neutrality across cultures.

### Integration and delivery

Score each of these separately:

- Delivery formats published (individual SVGs, webfont, sprite, framework components). A
  framework-component-only set is unusable for ZK. Publishing both SVGs and a font keeps the
  rendering mechanism a reversible choice.
- Rendering mechanisms the set permits (`content:` codepoint, `mask-image`, `background-image`,
  inline `<svg>`). SVG sprites are effectively out for ZK.
- Colour control: must recolour from CSS through `currentColor` or a custom property.
  Multi-colour or duotone icons cannot be themed and cannot survive forced-colors.
- Sizing: scales with `font-size` (`1em`) rather than fixed `width` / `height`; baseline alignment
  with adjacent text.
- Subsetting / tree-shaking: superseded by D13. Kept for future re-evaluation.
- Build-pipeline fit: works with Maven and npm without a new toolchain (font compilers, native
  binaries) or build-time network access.
- Machine-readable metadata (names, tags, aliases, codepoints). Required to generate CSS, the
  icon index and any lint rule.
- Extensibility: a documented recipe for adding one more glyph in the same idiom.

### Runtime cost

Measure the built artifact gzipped and raw. Report gzipped as the headline; use raw only for
jar size. Do not estimate. Raw bytes misrepresent the cost: URL-encoded SVG path data is repetitive
text and gzips well, while a woff2 font is already compressed.

Also consider: request count; cacheability (a separate font file caches independently, while data
URIs inside a large CSS invalidate the whole file when any icon changes); first-paint impact (web
fonts add a FOIT / FOUT race); the catalog-size multiplier (under D13 the cost scales with catalog
size for SVG delivery; a set with six weights emits six times the rules); and jar size.

### Visual and design fit

G7 is the pass/fail floor; this category scores how well a set fits above that floor.

- A single design grid (for example 24×24) with consistent live-area padding.
- Stroke consistency and scaling: does the stroke hold at 16px? A fixed 2px stroke at 24px becomes
  1.33px at 16px. An optical-size axis addresses this.
- Legibility at ZK's real sizes: render the T1 set at 12, 14, 16 and 20px and inspect.
  Small structural glyphs (carets, sort arrows) are where stroke-based sets are weakest.
- Baseline and optical alignment with adjacent label text.
- Swappability: the set is an implementation detail behind `z-icon-*`, so a future theme can
  substitute another without changing component CSS. Score the abstraction, not just the set.

### Accessibility and environment robustness

- Windows High Contrast / `forced-colors`: `mask-image` icons paint `background-color: currentColor`,
  and forced-colors overrides backgrounds, so the icons can vanish. Font glyphs survive natively.
  Test every candidate mechanism under `@media (forced-colors: active)`.
- Print: browsers with "no background graphics" drop mask and background icons; font glyphs print.
- Screen readers: decorative and meaningful icons must be distinguishable. A `::before`
  pseudo-element is invisible to assistive technology.
- Zoom and high-DPI: vector at all zoom levels, with no bitmap fallback.
- Reduced motion: animated icons (spinners) must be stoppable by CSS.
- Dark theme and contrast: single-colour icons that inherit `currentColor` pass automatically;
  duotone and multi-colour icons need a second asset and may fail contrast.

### Longevity, governance and developer experience

- Name stability across major versions: are icons renamed or removed, and are old names kept as
  aliases? Every unaliased rename is a breaking change to `iconSclass`. Verify empirically, not
  only from the changelog.
- Deprecation policy: are removed icons kept as aliases, and for how long?
- Release cadence and responsiveness (commits in the last six months, median issue response).
- Bus factor and governance (single maintainer, foundation or company).
- Funding model and freemium erosion risk: will today's free tier stay free? Community or MIT sets
  carry the opposite risk (abandonment).
- Licence-change risk: has the project relicensed before? Is the licence global or per-file?
- Developer familiarity and search: name quality, aliases and tags, a good browse site. Users
  search by meaning, not by name.
- Our own documentation cost: what must ZK document itself (for Marble: this file, the generated
  `icon-index.md` and `check-icon-coverage.sh`)? Budget it per candidate.

## Rule 1 — ZK widget icons (alias when needed)

ZK 10 emits `z-icon-*` names (the set is found by the grep under **Maintenance** below) from compiled JS/TS sources under
`/Users/hawk/Documents/workspace/ZK10/zk/zul/src/main/resources/web/js/`. We
cannot modify these. The strategy is:

1. If the name exists in `node_modules/lucide-static/icons/`, no action needed —
   `scripts/build-css.js` emits it as a regular Lucide rule.
2. If the name is FA-only (`caret-down`, `cogs`, `compress`, `info-circle`, …),
   add an entry to the `FA_TO_LUCIDE` map in `scripts/build-css.js`. The build
   step emits a second `.z-icon-{fa-name}{...}` rule reusing the matching
   Lucide SVG.

Current alias set: see the table in `doc/spec/icon-index.md`, section "FA → Lucide
aliases". The single ZK-widget name without coverage is `z-icon-fw` — it is a
FontAwesome "fixed-width" modifier with no glyph, handled by a single rule in
`zul/src/main/resources/web/zul/css/base/_icons.css` setting `width: 1.25em`.

**Maintenance**: when bumping the ZK version, re-run

```bash
grep -rhoE 'z-icon-[a-z0-9-]+' /path/to/ZK10/zk/zul/src/main/resources/web/js/ \
    | sort -u
```

and diff against `doc/spec/icon-index.md`'s Lucide list ∪ FA alias table. Any newly
appearing name must be either present in Lucide or added to `FA_TO_LUCIDE`.

## Rule 2 — Preview / example content (must resolve to a served class)

Every `z-icon-{name}` literal in `zkpreview/src/main/webapp/web/**/*.zul` MUST resolve to a
class that `scripts/build-css.js` actually serves — i.e. `{name}` is one of:

```
node_modules/lucide-static/icons/{name}.svg  EXISTS   (a Lucide name)
{name} is a key of FA_TO_LUCIDE                        (a ZK FA-style alias, e.g. caret-down)
{name} is a key of CUSTOM_ICONS                        (a bare ZK glyph, e.g. exclamation)
{name} == fw                                           (the no-glyph width modifier)
```

- **Both kinds are allowed**: a Lucide name *or* a ZK built-in FA/custom class name.
  ZK widget JS emits the FA-style names (`caret-down`, `angle-up`, `cogs`, …) and the
  build serves a real `.z-icon-{fa}` rule for each, so they render — and a static
  mockup of a widget's DOM (e.g. `scrollbar.zul`'s State Gallery, which mirrors
  `zul.Scrollbar`) SHOULD use the exact emitted name so it matches the live widget.
- **Forbidden**: invented / misspelled names (`my-custom-icon`) — anything with no
  served `.z-icon-*` rule.
- **Preferred for new content**: when you have a free choice, use the Lucide name
  directly (it is self-documenting); reach for an FA alias only when reproducing what
  ZK emits. The theme does not extend Lucide — if a glyph you want isn't in Lucide and
  isn't a ZK-emitted alias, pick a different Lucide icon.

**Excluded catalogs** (performance — these are whole-page icon listings, not ordinary
content): `zkpreview/src/main/webapp/web/icons-lucide.zul` (regenerated from
`lucide-static` on every build) and `utility/icons.zul` (the "All Lucide Icons" page,
which also hosts the FA-compat alias demo). Both are skipped by `check-icon-coverage.sh`.

> Historical note: an earlier `<!-- icon-lint:disable -->` / `<!-- icon-lint:enable -->`
> region opt-out existed to exempt FA-style names in authored ZULs. It was removed once
> Rule 2 was widened to accept ZK FA/custom aliases everywhere (both kinds now resolve to
> a served class, so no region exemption is needed).

## Enforcement

| Tool | Scope | When |
|------|-------|------|
| `scripts/check-icon-coverage.sh` | Rule 2 — all preview ZULs | Run locally; will be wired into pre-commit / CI |
| `.claude/agents/zk-theme-evaluator.md` | Rule 1 + Rule 2 (manual check during component evaluation) | When evaluator inspects a component |
| `scripts/build-css.js` (FA_TO_LUCIDE) | Rule 1 — declarative alias source | Build time |

## Canonical reference

`doc/spec/icon-index.md` — auto-generated, regenerate with `npm run build:css`.
Lists every valid Lucide name plus the active FA→Lucide alias table. This is
the file generator and evaluator should consult when in doubt.
