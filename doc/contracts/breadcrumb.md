# Component: breadcrumb (theme design)
tier: T1
category: navigation
preview: ${PREVIEW_URL}/breadcrumb.zul
rules: see .claude/skills/zk-component-rules/components/breadcrumb.md
contract-approved: true
zk-version: 10.4.0-jakarta
js-source-files:
  - zul/src/main/resources/web/js/zul/wgt/Breadcrumb.ts
  - zul/src/main/resources/web/js/zul/wgt/Breadcrumbitem.ts
  - zul/src/main/resources/web/js/zul/wgt/mold/breadcrumb.js
  - zul/src/main/resources/web/js/zul/wgt/mold/breadcrumbitem.js
js-source-hash: 2a7e8560487b49f5e8d612acb24ddfada4d4489d2fd2399f79de1e9946e30f71
closest-sibling: none — novel separator+collapse-to-ellipsis composition; decompose for parts only: `a` (link/current/disabled affordance on a single item) + `paging` (keep-ends/collapse-middle/ellipsis idiom precedent)
shared-css-file: js/zul/wgt/css/breadcrumb.css
siblings: [breadcrumbitem]
mockup-needed: Y — no ZKDoc canonical image exists for breadcrumb (component is new in ZK 10.4.0; no `ZKCompRef_Breadcrumb*.png` under `/Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/images/`), so condition (1) of the mockup-decision rule fires automatically — the HTML mockup is the only visual ground truth.

## References
- MUI CSS: `Navigation/Breadcrumbs.css` at
  `/Users/hawk/Documents/workspace/THEME/material-ui-7.3.1/static-css-output/` — exact
  match reused verbatim for the separator's 8px margin and the `<ol>` flex-wrap layout.
  `Navigation/Link.css` for the underline-on-hover convention consumed by
  `.z-breadcrumbitem > a`.
- DESIGN.md sections: §2 (Text Colors — on-surface / on-surface-variant), §4 (Spacing
  Scale), §7 (Typography — body-medium), §8 (State-Layer Overlays — disabled-opacity),
  §9 (Motion — default transition token pair).
- ZKDoc canonical: none found — breadcrumb is new in ZK 10.4.0 and has no entry yet under
  `/Users/hawk/Documents/workspace/DOC/zkdoc/zk_component_ref/`.
- Iceblue baseline: doc/contracts/baselines/breadcrumb-iceblue.png — **NOT captured this
  pass**. The `preview-app-iceblue` profile was not running (confirmed: `curl` to
  `${PREVIEW_URL}/breadcrumb.zul` returned no response) and no `breadcrumb.zul` preview
  page exists yet in either app to render. See "Structural surprises" in the authoring
  summary for the follow-up needed before this file can be produced.
- HTML contract: doc/contracts/breadcrumb.html.

## Design Contract

Breadcrumb renders as plain inline text chrome, not a card — no background, no border, no
radius, matching the borderless treatment of a standalone `a` link rather than a boxed
control. The trail's default voice is deliberately muted: separators and not-yet-visited
links both sit on `--zk-color-on-surface-variant` (via the shared `--zk-breadcrumb-fg`
knob) so the bar doesn't read as a wall of blue links — this is a **Marble curation
choice** applying MD3's text-color hierarchy (on-surface-variant for secondary/de-emphasized
content, on-surface reserved for the one thing the user should focus on), not a color rule
MUI's static CSS actually specifies (`Navigation/Breadcrumbs.css` carries zero color
declarations — MUI colors are theme-level `sx`/`palette` choices, outside the extracted
static CSS this project references) and not a ZK-stock convention either. Hovering or
focusing a link raises it to `--zk-color-on-surface` (`--zk-breadcrumb-fg-hover`) and adds
an underline — MUI's `underlineHover` idiom, genuinely sourced from `Navigation/Link.css`
— so interactivity is signalled without resorting to a saturated link-blue that would
compete with the rest of the page chrome. The terminal ("current page") item, always
rendered as a plain `<span>` because it carries no `href`, gets the same full-emphasis
`--zk-color-on-surface` tone (`--zk-breadcrumb-current-fg`) at rest — it is the one piece
of text in the trail that should read as "you are here," the same emphasis hierarchy
behind MUI's own demo convention of wrapping the last crumb in `color="text.primary"` (a
demo pattern, not a `Breadcrumbs.css` rule). A disabled item dims to
`--zk-state-disabled-opacity` (0.38) with `pointer-events:
none` as a defense-in-depth backstop to the widget's own click-suppression. Separators use
an 8px (`--zk-spacing-2`) margin on both sides — the exact value MUI's static Breadcrumbs
CSS ships (`.MuiBreadcrumbs-separator { margin-left/right: 8px }`) — while the
JS-synthesized ellipsis (which visually replaces a run of separators, not one) uses a
tighter 4px (`--zk-spacing-1`), since the "…" glyph itself supplies enough visual weight
that it does not need a full separator's breathing room. The ellipsis is a real `<button>`
(required for keyboard reachability — see the skill's keyboard model) but is stripped of
all native button chrome (no background, no border, no padding) so it reads as plain
text inline with the separators it stands in for, taking the same muted/hover color pair
as the rest of the bar. Every focusable content element — link, current `<span
tabindex="-1">`, and the ellipsis button — takes the theme's global `--zk-focus-ring`
on `:focus-visible`, the same un-knobbed convention as button/window/grid, since the
arrow-key navigation model (Left/Right/Home/End move focus among visible items) means a
keyboard user has no other way to tell where focus landed. The ring is drawn **outward**
(`outline-offset: 2px`), not inset, because these are borderless text elements with
nothing for an inset ring to sit inside (the same outward convention `signature` uses for
its borderless tool buttons, unlike `organigram`/`coachmark`'s inset ring on an
already-bordered card); a small `border-radius: 2px` on the ring keeps its corners soft
rather than a hard rectangle wrapped around a single line of inline text. Link/ellipsis
color transitions use the theme's default interactive pair, `--zk-motion-duration-standard`
(250ms) with `--zk-motion-easing-standard`, transitioning `color` only.

## Outcome assertions

Breadcrumb is a composite multi-item row layout (items + separators + a client-injected
collapse/ellipsis), so it earns outcome rows rather than a bare `visual-goal: trivial`
declaration. The generic card-framing template rows (visible border/shadow/background,
minimum height, inner-fill-percentage) do not apply — breadcrumb is intentionally
borderless inline chrome, like `a` — so every row below is bespoke to breadcrumb's real
failure modes instead.

| id | predicate | rationale |
|----|-----------|-----------|
| M1 | For every group of visible (`:not([data-zk-bc-hidden])`) `.z-breadcrumbitem` / `.z-breadcrumb-separator` / `.z-breadcrumb-ellipsis` siblings sharing one wrapped line: `max(bbox.verticalCenter) − min(bbox.verticalCenter) ≤ 4px` | the trail must read as one horizontal row per line, not a stack of misaligned fragments |
| M2 | For every visible `.z-breadcrumb-separator`: `previousVisibleSibling.bbox.right ≤ separator.bbox.left + 2px` AND `separator.bbox.right ≤ nextVisibleSibling.bbox.left + 2px` | separator must sit strictly between its two neighbors — catches z-fighting or reordering |
| M3 | No two visible text-bearing nodes (`.z-breadcrumbitem > a`, `> span`, `.z-breadcrumb-separator`, `.z-breadcrumb-ellipsis`) overlap by more than 1px | anti z-fight / anti-collision, generic across all visible children |
| M4 | Every element carrying `[data-zk-bc-hidden="true"]` has `bbox.width === 0 AND bbox.height === 0` | collapse must be a real layout removal, not a visual-only fade a screen-reader-off sighted user could still perceive as occupying space |
| M-ellipsis-visible | Whenever `.z-breadcrumb-ellipsis` is present in the DOM: its `> button` has `bbox.width ≥ 6px AND height ≥ 6px` AND its `bbox.verticalCenter` is within the M1 tolerance of the surrounding visible items' line | the ellipsis is the only way to reach the collapsed items — it must be real estate a pointer/keyboard user can actually find and hit, on the same row as the rest of the trail |
| M-icon-separator-visible | Whenever `.z-breadcrumb-separator > i` is present: its bbox has `width ≥ 6px AND height ≥ 6px` AND its `bbox.verticalCenter` is within the M1 tolerance of the surrounding visible items' line | unlike the text-node separator (which can't silently collapse to zero size), the icon form is a masked glyph that can vanish if the mask-image/size CSS is missing — this row is the paired visibility check for that failure mode, matching the ellipsis/current-item rows |
| M-current-visible | The last `.z-breadcrumbitem`'s content element (`a` or `span`) has non-zero bbox AND `bbox.bottom ≤ root.bbox.bottom + 2px` AND `bbox.right ≤ root.bbox.right + 2px` | the "you are here" endpoint — the crumb users care about most — must never be the one that got clipped or wrapped out of frame |
| M7 | Sampling computed `color` of a `.z-breadcrumbitem > a` and the terminal `.z-breadcrumbitem > span`: the two values differ | link vs current-page must be visually distinguishable — a trail where every crumb reads identically fails its one navigational job |

## Expected values

| id | selector | property | expected (token preferred) | token-rooted? | source |
|----|----------|----------|----------------------------|---------------|--------|
| c1 | `.z-breadcrumb` | display | `inline-block` | no | layout keyword, not a design token |
| c2 | `.z-breadcrumb` | font-size / line-height | `var(--zk-typescale-body-medium-size)` / `var(--zk-typescale-body-medium-line-height)` | yes | DESIGN.md §7 |
| c3 | `.z-breadcrumb`, `.z-breadcrumb-separator` | color | `var(--zk-breadcrumb-fg)` → `var(--zk-color-on-surface-variant)` | yes | DESIGN.md §2 — muted default voice |
| c4 | `.z-breadcrumb-separator` | margin-left / margin-right | `var(--zk-spacing-2)` (8px) | yes | DESIGN.md §4 — exact match to MUI `Breadcrumbs-separator` |
| c5 | `.z-breadcrumb-ellipsis` | margin-left / margin-right | `var(--zk-spacing-1)` (4px) | yes | DESIGN.md §4 — tighter than a full separator; the glyph itself supplies visual weight |
| c6 | `.z-breadcrumb-ellipsis > button` | background, border, padding | `none`, `0`, `0` | no | native-button-chrome reset so it reads as plain text, not a token value |
| c7 | `.z-breadcrumb-ellipsis > button` | color | `var(--zk-breadcrumb-fg)` | yes | matches c3 — reads as one voice with the separators it stands in for |
| c8 | `.z-breadcrumb-ellipsis > button:hover`, `:focus-visible` | color | `var(--zk-breadcrumb-fg-hover)` → `var(--zk-color-on-surface)` | yes | DESIGN.md §2 |
| c9 | `.z-breadcrumb-ellipsis > button:focus-visible` | outline | `var(--zk-focus-ring)` | yes | DESIGN.md §8 — global focus convention, not knob-driven |
| c10 | `.z-breadcrumbitem > a` | color | `var(--zk-breadcrumb-fg)` → `var(--zk-color-on-surface-variant)` | yes | DESIGN.md §2 — muted at rest; a Marble/MD3 text-hierarchy curation choice, not a MUI static-CSS rule (`Navigation/Breadcrumbs.css` has no color declarations) |
| c11 | `.z-breadcrumbitem > a` | text-decoration | `none` | no | resting state; underline reserved for hover (MUI `underlineHover`) |
| c12 | `.z-breadcrumbitem > a:hover`, `:focus-visible` | color | `var(--zk-breadcrumb-fg-hover)` → `var(--zk-color-on-surface)` | yes | DESIGN.md §2 |
| c13 | `.z-breadcrumbitem > a:hover` | text-decoration | `underline` | no | MUI Link `underlineHover` idiom |
| c14 | `.z-breadcrumbitem > a:focus-visible`, `span[tabindex]:focus-visible` | outline | `var(--zk-focus-ring)` | yes | DESIGN.md §8 |
| c15 | `.z-breadcrumbitem > span` | color | `var(--zk-breadcrumb-current-fg)` → `var(--zk-color-on-surface)` | yes | DESIGN.md §2 — full emphasis "you are here" |
| c16 | `.z-breadcrumbitem-disabled` | opacity | `var(--zk-state-disabled-opacity)` (0.38) | yes | DESIGN.md §8 |
| c17 | `.z-breadcrumbitem-disabled` | pointer-events | `none` | no | defense-in-depth; widget JS already stops the click |
| c18 | `.z-breadcrumbitem > a`, `> span`, `.z-breadcrumb-ellipsis > button` | transition | `color var(--zk-motion-duration-standard) var(--zk-motion-easing-standard)` (250ms) | yes | DESIGN.md §9 |
| c19 | `.z-breadcrumbitem > a:focus-visible`, `> span[tabindex]:focus-visible`, `.z-breadcrumb-ellipsis > button:focus-visible` | outline-offset | `2px` | no | DESIGN.md §9 — ring drawn outward since these are borderless text elements with nothing to sit inside; same outward convention as `signature`'s tool-button ring (`btn-focus2`), unlike `organigram`/`coachmark`'s inset ring on an already-bordered card |
| c20 | same selectors as c19 | border-radius | `2px` | no | Marble curation — softens the ring's corners around a single line of inline text; no existing shape token is this small, so it stays a literal alongside c19 |

> `[data-zk-bc-hidden="true"] { display: none; }` is a **structural** rule (makes the
> client-only collapse function at all, per the skill's Composition Invariants), not a
> theme design value — it is not repeated here as a c-row; see the skill file.

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default (link items + separators) | `.z-breadcrumb` | c1, c2, c3, c4, c10, c11, M1, M2, M3 |
| link-hover | `.z-breadcrumbitem > a:hover` | c12, c13, M7 |
| link-focus-visible | `.z-breadcrumbitem > a:focus-visible` | c14, c19, c20 |
| current-item (terminal, plain span) | `.z-breadcrumbitem:last-child > span` | c15, M-current-visible, M7 |
| current-focus-visible (arrow-key reachable via tabindex=-1) | `.z-breadcrumbitem > span[tabindex]:focus-visible` | c14, c19, c20 |
| disabled-item | `.z-breadcrumbitem-disabled` | c16, c17 |
| icon-separator variant (`separator="icon:…"`) | `.z-breadcrumb-separator > i` | c3 (color inherited), M2, M-icon-separator-visible |
| collapsed (maxItems exceeded) | `.z-breadcrumb-ellipsis` | c5, c6, c7, M4, M-ellipsis-visible |
| ellipsis-hover / focus-visible | `.z-breadcrumb-ellipsis > button:hover`, `:focus-visible` | c8, c9, c19, c20 |
| expanded (post ellipsis-click) | `.z-breadcrumb-ellipsis` absent, no `[data-zk-bc-hidden]` remaining | M1, M3 |

## Preview anchors

**No preview ZUL exists yet.** `zkthemelab/src/main/webapp/web/breadcrumb.zul` is absent — this is
a brand-new component with no existing preview page in either the Marble or iceblue apps.
Before the Evaluator can run against this contract, a preview page must be authored that
exercises every row in "States to evaluate" below, including:
- at least one item with `href` (link), one terminal item with no `href` (current), and
  one `disabled="true"` item;
- a `separator="icon:z-icon-chevron-right"` variant alongside the plain-text default;
- a `maxItems`-bound instance with enough items to force a collapse (`maxItems ≥ 2` and
  item count `> maxItems`), so `.z-breadcrumb-ellipsis` actually mounts.

Suggested anchor strategy once authored (matching the project's post-`pv.css` convention
of plain state-matrix markup, no page-local classes — see `reference_pv_css_dissolved`):
give each demo instance a stable `id` (e.g. `#pv-breadcrumb-default`,
`#pv-breadcrumb-icon-separator`, `#pv-breadcrumb-collapsed`) rather than relying on
positional `:nth-of-type` selectors, since the collapsed variant's DOM shape mutates
client-side after mount.

## DOM key selectors

```
.z-breadcrumb                     ← root <nav>
.z-breadcrumb-list                ← <ol>
.z-breadcrumbitem                 ← <li>, root of one entry
.z-breadcrumbitem-disabled        ← modifier on the .z-breadcrumbitem root
.z-breadcrumbitem > a             ← navigable entry
.z-breadcrumbitem > a:hover / :focus-visible
.z-breadcrumbitem > span          ← current-page / disabled entry
.z-breadcrumbitem > span[tabindex]:focus-visible
.z-breadcrumb-separator           ← <li>, text node or i.{iconSclass} content
.z-breadcrumb-ellipsis             ← <li>, client-injected only (absent from server HTML)
.z-breadcrumb-ellipsis > button
.z-breadcrumb-ellipsis > button:hover / :focus-visible
```

## Cross-cutting features

### Component Theme Variables
ctv: shipped
ctv-knobs: --zk-breadcrumb-fg, --zk-breadcrumb-fg-hover, --zk-breadcrumb-current-fg
ctv-probe: { knob: --zk-breadcrumb-fg, property: color, value: rgb(255, 0, 0) }

No `-bg` / `-border-color` / `-radius` knobs: breadcrumb has no card surface (per the
Design Contract, it deliberately renders as borderless inline text, the same curation
choice as `a`/`caption`) so there is nothing for those axes to theme. `--zk-breadcrumb-fg`
covers three roles at once — the separator, the ellipsis button (resting), and a link
item (resting) — the same "one knob, several roles" precedent as `tab`'s `--zk-tab-fg` /
calendar's `--zk-calendar-accent`, since all three share the identical muted tone by
design. `--zk-breadcrumb-fg-hover` covers link-hover/focus and ellipsis-hover/focus
together (same precedent). `--zk-breadcrumb-current-fg` is the one defining-state knob —
the terminal "you are here" item's full-emphasis color, matching the "one knob per
defining state" precedent (`chosenbox`'s `-item-focus-bg`, `tab`'s `-accent`). The focus
ring itself stays on the un-knobbed global `--zk-focus-ring` (button/window/grid
convention) — not part of the curated surface.

### Density
density: N/A — no intrinsic control-height. Breadcrumb is an inline text row sized by
`font-size`/`line-height`, not a control-height component — the same rationale as `a` and
`caption`. The ellipsis `<button>` has its native chrome fully stripped (c6: `background:
none; border: 0; padding: 0`) so it is sized by its text content, not by any
`--zk-control-height-*` rung; there is nothing in the `_sizing.css` ladder for it to bind
to.

### Forced colors
fc-risk: [mask-glyph]
fc-guards: N/A — already covered by the existing generic (2d) rule in
`tokens/_forced-colors.css` (`[class^="z-icon-"]::before, [class*=" z-icon-"]::before { … }`).
The only forced-colors-fragile pattern breadcrumb introduces is the optional icon-form
separator (`separator="icon:z-icon-chevron-right"`), which renders via the theme's generic
masked-icon system (`background-color: currentColor` + `mask-image` on any `z-icon-*`
class) — that class pattern is already matched by the central guard's generic selector, so
no new guard entry is needed. The disabled item's opacity fade and the link/current color
distinction are not flagged: opacity is not stripped by forced-colors, and both `<a>` and
`<span>` keep independently-readable foreground colors under the system palette (an `<a>`
commonly force-maps toward the UA's `LinkText`, distinct from plain `CanvasText`).

### Brand override
brand-allowed-literals: none — every design-bearing value in the Expected-values table
above is either a `var(--zk-*)` token or an explicitly-literal non-color/non-spacing
keyword (`display`, `text-decoration`, `pointer-events`, the button-chrome reset).

### Tablet
tablet: needs-specific — the ellipsis `<button>` is a genuinely small discrete tap target
(sized to the "…" glyph plus 4px margins, well under 44px) and, unlike the `<a>`/`<span>`
items (which are inline text — the same touch-ergonomics exemption every other inline nav
link in this theme gets; no tablet rule anywhere enlarges `.z-a`), it is the *only* way to
reach the collapsed items, so a missed tap has no fallback. The tablet bundle
(`zkmax/css/tablet/_buttons.css` precedent — icon-only buttons grow to a 44×44 hit area via
padding, not by resizing the glyph) should extend
`.z-breadcrumb-ellipsis > button` to at least `--zk-touch-target-min` (44px) in both
dimensions on a touch UA, via padding around the unchanged "…" glyph, matching how every
other small icon-only control in the tablet bundle grows its hit area without growing its
visible mark.

## States to evaluate
- [ ] default (link items + plain-text separator)
- [ ] link-hover
- [ ] link-focus-visible
- [ ] current-item (terminal, plain span, full emphasis)
- [ ] current-focus-visible (arrow-key reachable via tabindex=-1)
- [ ] disabled-item
- [ ] icon-separator variant
- [ ] collapsed (maxItems exceeded, ellipsis mounted)
- [ ] ellipsis-hover / focus-visible
- [ ] expanded (post ellipsis-click, no items remain hidden)
