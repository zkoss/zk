# Forced colors / Windows High-Contrast Mode (`forced-colors`)

*Implemented 2026-07-14 · ambiguous-case pass 2026-07-16 · GAP 5 · WCAG 2.1 SC 1.4.1 / 1.4.11 (non-text contrast)*

> **Single source of truth.** This spec records the complete `forced-colors: active`
> behaviour of the theme — the guard's mechanism, every restored block, the resolutions
> of the "ambiguous" judgment-call cases, the audit method, and how to verify and extend.
> The guard CSS itself is `zul/src/main/resources/web/zul/css/tokens/_forced-colors.css`.

## Why

Under `@media (forced-colors: active)` (Windows High-Contrast Mode; also emulable in Chrome/Edge/Firefox) the OS replaces the page palette with a small set of **system colors** and **removes every `box-shadow`**. Marble is an MD3-style theme, so several affordances would break:

| Pattern Marble uses | What forced-colors does to it | Result without a guard |
|---|---|---|
| `box-shadow` inset focus ring on text inputs (datebox/timebox/spinner/bandbox) | `box-shadow` stripped; the 1px border and its focus color both collapse to one system color | **No visible focus change** on keyboard focus |
| `box-shadow` elevation on popups / windows / menus | `box-shadow` stripped | **Boundary invisible** — floating surfaces merge into the page |
| Selected row = `primary-container` bg + `on-primary-container` text | both forced to system colors | Selection **indistinguishable** from unselected |
| Checkbox checked = `primary` box + baked white SVG check | box bg forced to `Canvas`; baked white glyph does **not** follow | **Invisible check** — cannot tell checked from unchecked |
| Masked Lucide/SVG glyphs = `background-color: currentColor` + `mask-image` (the theme's whole icon system) | `background-color` force-mapped to `Canvas` (page bg), **not** the foreground | **Every icon vanishes** — same-as-background shape (e.g. spinner up/down arrows) |
| `::before`/`::after` state-layer hover tints | pseudo `background-color` dropped | Hover/pressed feedback gone *(cosmetic — not restored)* |

Target customers (government / finance / EU accessibility-directive) frequently treat high-contrast support as a hard requirement.

## Approach

**One central, unlayered block** — `zul/src/main/resources/web/zul/css/tokens/_forced-colors.css`, bundled into the global `norm.css.dsp` via the `normFiles` list in `scripts/build-css.js` (placed last among the token files so its `--zk-focus-ring` override wins over `_colors.css`). Mirrors the GAP 4 `prefers-reduced-motion` precedent in `tokens/_motion.css`.

Why one central file instead of per-component guards:

- **Unlayered beats layered.** Component CSS lives in `@layer zk-components`; token files are unlayered. In the cascade, unlayered rules win over *any* named layer regardless of specificity — so the guards override component styles **without `!important`** and **without editing the component files**.
- All selectors are global ZK class names, so no component-local context is needed.
- Single file = one place to review, and one place to re-verify survives CleanCSS minification.

**System colors only.** Inside the media block, colors use CSS system-color keywords (`Canvas`, `CanvasText`, `Highlight`, `HighlightText`, `ButtonText`, `GrayText`) — the only palette honored in forced-colors mode. `--zk-color-*` custom properties are *not* reset by forced-colors, but any real color they feed (background, border, outline, box-shadow) is force-mapped or stripped, so brand tokens must not be relied on here.

## What the guard restores

Blocks are labelled with the same codes used in the comments of `tokens/_forced-colors.css`.

- **(1a) Elevation-only surfaces** → `border: 1px solid CanvasText` on `.z-window`, `.z-panel`, `.z-popup(-content)`, `.z-menupopup`, `.z-menu-popup`, `.z-combobox-popup`, `.z-bandbox-popup`, `.z-datebox-popup`, `.z-daterangebox-popup`, `.z-listbox`, `.z-grid`, `.z-paper`; plus audited additions (pdfviewer page/toolbar, cropper toolbar, signature tool button, drawer, tbeditor box, groupbox, calendar, errorbox, loading, notification, toast), floating popups (cascader/chosenbox shadows, searchbox/timepicker popups, toolbar/nav/tbeditor dropdowns, bandpopup, goldenlayout, `.z-confirmpopup`), and slider thumbs/tooltips. The datebox/daterangebox calendar popups are shadow-only surfaces (`--zk-elevation-dialog`, `border:none`); the pre-existing in-popup `.z-calendar` border merges with the new frame so no card-within-a-card appears.
- **(1b) Text-input focus** → `outline: 2px solid Highlight; outline-offset: -1px` on `:focus-within` for `.z-datebox`, `.z-timebox`, `.z-spinner`, `.z-doublespinner`, `.z-bandbox`, `.z-combobox`, `.z-timepicker`, `.z-searchbox-search`, `.z-selectbox`. Round slider thumb uses `outline-offset: 2px`.
- **(1c) Invalid inputs** → `border-style: double; border-width: 3px` on a control that carries its own border, a double-border overlay on a wrapper. Invalid state is otherwise signalled *only* by a red border colour, which force-maps to `CanvasText` (identical to a valid field). Border **width and style survive** the remap while colour does not, so a double border is the non-colour error affordance. Focus stays on `outline` (1b) so the two signals never collide. Covers the input components (intbox/decimalbox/doublebox/longbox/textbox/passwordbox on the control; datebox/timebox/timepicker/spinner/doublespinner via `:has()` on the wrapper; bandbox and daterangebox on the wrapper; combobox on input+button, with the seam between them kept open (`border-right-style` / `border-left-style: none`) so the pair reads as one box). **The invalid input is as large as the valid one (1c-size, 1c-wrapper, follow-up 50):** a control gives the 2px its border gained to its padding (`calc(var(--zk-spacing-3) - 2px)`, the combobox input only on the side that is bordered), so the text keeps its x; a wrapper (datebox, timebox, timepicker, spinner, doublespinner, bandbox, daterangebox) keeps its 1px border and draws the 3px double border on an out-of-flow `::before` at `inset: -1px`, because the min-height of its children, which differs per density and on a tablet, pins the content box. Measured width x height, valid = invalid, in default, compact and tablet density (before: +4 wide on every control, +4 x +4 on every wrapper, +2 wide on the combobox input and root). Guard: `an invalid <input> is as large as the valid one` in `forced-colors.spec.ts` (fixture `pv/invalid-size-guard.zul`; the tablet cases run in `forced-colors-tablet`).
- **(1d) Triangle beak pointers** → `forced-color-adjust: none` on `.z-coachmark-pointer`, `.z-confirmpopup-arrow`, `.z-errorbox-pointer`. These are CSS triangles (transparent border sides + one coloured); forced-colors force-maps the transparent sides to `CanvasText`, collapsing the beak into an opaque square. Opting out preserves the transparency so the triangle shape survives. (`forced-color-adjust` inherits, so setting it on the arrow element also covers its `::before`/`::after` triangle layers.)
- **(2a) Selected rows / tinted selections** → `background-color: Highlight; color: HighlightText` on selected `.z-listitem` / `.z-treerow` (and their cell content), plus paging current page, nav item, accordion selected tab, searchbox row, organigram node. A **UA "backplate"** paints an opaque Canvas rectangle behind inline text on these fills (hiding the near-white label); a companion `forced-color-adjust: none` on the label text kills it. The **calendar selected day** opts out too, so its primary-disc / on-primary-number pairing is reproduced (the unscoped selector also covers the portallayout-embedded calendar).
- **(2b) Compound glyph indicators** → `forced-color-adjust: none` on the checked/indeterminate `.z-checkbox-mold` (+ its `::after`) and the selected-row / select-all check icons. A *designed fill+glyph pairing* (blue box + white check) whose contrast is guaranteed by design; opting out keeps it legible rather than letting the box flip to `Canvas` under a check that can't follow.
- **(2d) Masked icon glyphs** → `background-color: CanvasText` on the whole `z-icon-*` family plus component-local masked icons (`.z-tree-icon i`, `.z-rating-icon`, `.z-colorbox-icon`, colorbox palette/picker toggles, `.z-messagebox-icon`, paging OS-mold chevrons). The theme paints every icon as a `background-color: currentColor` fill clipped by a `mask-image`; forced-colors force-maps `background-color` to `Canvas`, so the fill is re-pointed at the foreground. Content icons on a Highlight fill get `HighlightText`. Check/radio indicators are excluded (handled by 2b/2f). Being *unlayered* it beats higher-specificity component rules (e.g. `.z-combobox-button .z-icon-caret-down::before`).
- **(2e) Masked glyphs painted outside the generic path** → `background-color: CanvasText` on `.z-combobox-icon`, `.z-combobox-button .z-icon-caret-down`, `.z-signature-tool-button-icon::before`, `.z-step-icon.z-icon-check::before` — each fills on the element itself or a non-`z-icon` class, so 2d never reaches it. The listbox paging-bottom icons (`.z-listbox-paging-bottom .z-paging-icon`) are the same case. A disabled paging button's glyph, including the « » chevrons of the OS mold (`.z-paging-noborder[disabled]::after`), is re-pointed to `GrayText` (2e-disabled) so it does not read as enabled; the text of a disabled paging (`.z-paging-text-disabled` of the default mold, the disabled non-selected anchors of the OS mold) is `GrayText` as well (2e-disabled-text, follow-up 48); the tbeditor toolbar SVG glyphs get `opacity: 1` (their `opacity: 0.6` greys the CanvasText fill). The dnd drop-ok/invalid glyph (`.z-drop-icon`) additionally needs `forced-color-adjust: none` (Chromium force-maps its background even with a system-color keyword).
- **(2f) Radio dots** → `forced-color-adjust: none` on the standalone `.z-radio` dot and the listbox/tree `.z-icon-radio` dot (excluded from 2d, absent from 2b), so the checked dot keeps its designed ring+dot contrast.
- **(3) Buttons** → a 1px `ButtonText` border (delineates the shape once the fill collapses to `ButtonFace`); disabled buttons use `GrayText`. A borderless button draws it on an inset `::after` so the box keeps its normal-mode size; outlined and light buttons keep their own real border.
- **(3a) Checked toolbarbutton** → `border: 2px solid Highlight`. The checked "on" state is carried only by a primary-container pill fill, which force-maps to Canvas → checked == unchecked. Toolbarbuttons have no border by default, so border-presence is the non-colour checked affordance.
- **(4) Background-color shape fills** → whole shapes painted with `background-color` (not a masked `::before` glyph) force-map to Canvas and vanish. Re-pointed to `GrayText` for inert track/rail, `Highlight` for the active/value fill: progressmeter (track + image), separator/space bars (a vertical bar also opts out of the colour adjustment, `forced-color-adjust: none`, so the text backplate of its `&nbsp;` does not break the line into ticks; a horizontal bar is redrawn as a centred 1px `::before` of `CanvasText`; a bar between the items of a toolbar is a 1px x 24px `CanvasText` divider with no `::before`, follow-up 45 F1, D21 = B; a bar in a panel-mold toolbar stays transparent as in normal mode, follow-up 45 F2), slider/rangeslider/multislider rail + area, checkbox switch mold + toggle mold, stepbar step connectors.
- **(4a) Slider thumbs** → solid `background-color: CanvasText` disc (+ the 1a 1px border), with the `::before` state-layer halo set transparent, so slider/rangeslider/multislider thumbs render as one identical disc instead of mixed hollow rings and stray dots.
- **(5) Colour-content** → `forced-color-adjust: none` on `.z-colorbox-current` (the closed swatch — covers standalone colorbox, the portallayout embed, and signature's pen/bg colorboxes) and the palette/picker colour surfaces. The inline `background-color` ZK sets here *is* the information; the forced-colors spec names colour pickers as the canonical opt-out. Surrounding chrome (frame, caret, hex inputs, OK button) still follows the system palette.
- **(focus token) `--zk-focus-ring`** retargeted to `2px solid Highlight` (explicit; `outline-color` is force-mapped anyway, but this makes intent clear for its consumers).

## Intentionally NOT handled

- **`::before`/`::after` state-layer hover/pressed tints** — cosmetic feedback, not information loss. Focus (outline) and selection (Highlight) carry the necessary state.
- **Baked-gray dropdown chevrons** (`datebox`/`selectbox`, `stroke='%23666'` in a `background-image` data-URI) keep their fixed color. The input remains usable (border + focus outline survive); recoloring would require converting the data-URI to a `mask-image`. Known minor limitation.

## Resolved ambiguous cases (2026-07-16)

After the central guard restored the *mechanical* defects (see "Audit method" below), a
residual set of cases remained where information was genuinely lost but the repair was a
**design decision**, not a rule — restoring each meant inventing a non-colour affordance
(or deciding not to). All were dispositioned in the ambiguous-case pass:

| # | Case | Components | Resolution | Block |
|---|------|------------|------------|-------|
| 1 | Invalid-field red border collapses to plain black — invalid == valid | intbox, decimalbox, doublebox, longbox, textbox, passwordbox, datebox, timebox, timepicker, spinner, doublespinner, bandbox, combobox | **Double border** (`border-style: double; border-width: 3px`) — width+style survive the palette remap; focus stays on `outline` so the signals stay orthogonal | 1c |
| 2 | CSS-triangle beak renders as a solid black square | coachmark, errorbox | **`forced-color-adjust: none`** on the pointer — preserves the transparent sides so the beak keeps its shape; the popup/box boundary still reads via its 1a border | 1d |
| 3 | Colour-preview swatch renders empty — selected colour not conveyed | colorbox, portallayout (embed), signature pen/bg | **`forced-color-adjust: none`** on the colour-content only (swatch + palette/picker surfaces); chrome stays HC. The spec-sanctioned colour-picker opt-out | 5 |
| 4 | Calendar selected day lost when embedded | portallayout | **No change needed** — the standalone selected-day guard uses an *unscoped* selector and portallayout embeds an identical `<calendar/>` DOM, so it was already covered (verified) | 2a |
| 5 | Checked toolbarbutton looks unchecked | toolbar | **`border: 2px solid Highlight`** — border-presence is the non-colour checked affordance (unchecked buttons have none) | 3a |
| 6 | Slider thumbs read inconsistently (rings/dots/carets) | slider, rangeslider, multislider | **Solid `CanvasText` disc** + `::before` halo transparent — all three variants render identically | 4a |
| 8 | Keyboard focus ring on a **selected** item disappears (2026-09-04) | navbar (fixed); at risk: tree, paging, accordion tabbox, searchbox, organigram | **`outline-color: HighlightText`** on the selected+focused item — the selection fill and `--zk-focus-ring` both remap to `Highlight`, so the ring painted Highlight-on-Highlight (measured RGB delta 78; 687 after). Same pairing the label text already uses. Only navbar measured + fixed; the other selected-tint families in 2a are unswept | 2a focus |
| 7 | Decorative filled blocks lose their fill | dnd demo boxes, rowlayout column bars | **By design, no change** — preview/demo constructs (not theme chrome) built from `z-bg-*` utilities, each with a surviving text label; stripping a decorative `background-color` is the spec-intended behaviour. A border would be over-broad (every utility use) or alter normal-mode appearance | — |

The **"colour vs high-contrast" tension** in case 3 is resolved by the spec itself: colour
pickers and data visualisations are the canonical `forced-color-adjust: none` exceptions —
content where colour *is* the information. Opt out only the colour-bearing pixels; let the
chrome stay HC-compliant.

## Audit method & provenance

Coverage was established by an **outcome-driven audit**: a Planner → Generator (parallel
comparison agents) → Evaluator loop over **89 `<page>-gallery.png` vs `<page>-forced-colors.png`
screenshot pairs**, root-caused against the guard CSS, fixed, re-captured, and re-verified.
Emulation is Chromium `emulateMedia({ forcedColors: 'active' })` (macOS has no real
forced-colors). The pass split findings into **OBVIOUS defects** (mechanical, auto-fixed) and
**AMBIGUOUS** (design calls — resolved above).

**Already fixed in the mechanical pass** (for context): missing masked icons; selected-row /
tree / accordion labels hidden behind the UA text backplate; radio checked-dots; the paging
current-page digit; dnd drag-ghost status glyphs; and every `background-color` shape fill
(progressmeter, separator/space, sliders, checkbox switch/toggle, stepbar connectors).

**Visual review sheet (regenerable artifact).** A side-by-side contact sheet of all pairs
can be rebuilt on demand — it is **not** committed:

```bash
npm run capture:forced-colors   # writes doc/screenshots/<page>-forced-colors.png
npm run review:forced-colors    # builds doc/forced-colors-review.html (a template-side build output; not built in zk) (gitignored)
```

The committed regression gate is the **assertion** spec (`forced-colors.spec.ts`,
computed-style probes) — not the screenshots.

## Approach rationale (vs a dedicated high-contrast theme)

Marble uses a CSS `forced-colors` override, **not** a separate dedicated high-contrast theme. This matches the industry mainstream:

- **Vaadin** (the closest Java-web analog to ZK) ships forced-colors support inside its *base styles* — a CSS-override approach, not a separate theme. It hit the exact same trap (Lumo focus rings drawn with `box-shadow` vanished in WHCM) and fixed it in V24.1 by adding outlines/borders.
- **Microsoft Fluent** moved *away* from a dedicated high-contrast theme toward standard `forced-colors` + system colors (Edge blog, 2020).
- **PrimeFaces/PrimeNG, GWT, Wicket** have no systematic forced-colors layer.

Consequently, the "dedicated high-contrast theme" line in [`design-decisions.md`](design-decisions.md) is marked **won't-do** (same disposition as dark mode): shipping the forced-colors override *is* the modern way to meet the requirement.

## Extending to a new component

Because the guard is one central file keyed by ZK class, adding coverage for a new
(or previously-missed) component is a small, repeatable edit — no new file, no build
wiring. The process:

1. **Detect** whether the new component's CSS contains a fragile pattern from the table in step 2,
   and whether its class is already listed in the guard file. The former coverage check,
   `npm run check:forced-colors`, is no longer in zk (`.claude/skills/marble-theme/SKILL.md`
   line 102), and the marble-theme SKILL.md names no replacement; **to be confirmed**.
2. **Classify** the component's fragile pattern(s) and pick the matching remedy:
   | Pattern found | Remedy → which list in `_forced-colors.css` |
   |---|---|
   | `box-shadow` elevation on a surface/popup | add its class to the **(1a) border** list |
   | `box-shadow` focus ring on an input | add its `:focus-within`/`:focus` selector to the **(1b) outline** list |
   | invalid state shown only by a border colour | add its invalid selector to the **(1c) double-border** list |
   | CSS-triangle pointer (transparent borders + one colour) | add it to the **(1d) `forced-color-adjust: none`** pointer list |
   | selected-row/item tint | add its selected selector to the **(2a) Highlight/HighlightText** list (+ the backplate opt-out) |
   | baked-color SVG check/glyph indicator | add its selector to the **(2b) `forced-color-adjust: none`** list |
   | masked glyph (`mask-image` + `background-color: currentColor`) | add its selector to the **(2d)** icon-fill list (`background-color: CanvasText`, or `HighlightText` on a selection fill); **(2e)** if painted on the element/non-`z-icon` class |
   | tinted "checked/on" toggle with no border | add its checked selector to the **(3a) border** list |
   | whole shape painted with `background-color` | add it to the **(4) shape-fill** list (`GrayText` track / `Highlight` value) |
   | draggable thumb with a `background-color` fill | add it to the **(4a) thumb** list (solid `CanvasText` disc) |
   | element whose `background-color` *is* the information (colour swatch) | add it to the **(5) `forced-color-adjust: none`** colour-content list |
3. **Rebuild & verify** — `npm run build:css`, then `npm run test:forced-colors` (add a
   case to `forced-colors.spec.ts` if the component introduces a new failure mode).

### Coverage status (as of 2026-07-16)

**All ambiguous items closed.** The 2026-07-16 pass resolved the seven residual judgment-call
cases (see "Resolved ambiguous cases"): three took one guard block each (1c, 1d/3a/4a, 5), two
needed no code (embedded calendar already covered; decorative blocks by-design), and two were
resolved earlier. The guard now covers every class in the blocks listed under "What the guard restores"; the coverage check (`check:forced-colors`, no longer in zk) reported **0 gaps**
and `npm run test:forced-colors` → **13/13 pass** (no regression).

**Full component coverage.** After the initial anchor pass, the audit's tail of
lower-frequency components (calendar selected day, accordion selected tab, paging current
page, drawer/toast/notification/groupbox surfaces, slider/selectbox/timepicker/searchbox
focus, cascader/chosenbox/goldenlayout/organigram/nav, etc.) was folded into the central
lists via the process above. The strict coverage check (`check:forced-colors -- --strict`) then **exited 0** —
every component CSS with a fragile pattern is represented in the guard. This coverage was
produced with a Planner–Generator–Evaluator loop and verified by an independent
computed-style probe across all four buckets. New components should be checked against
the checklist above (the strict check is no longer in zk; see Detect in step 1).

**Masked-icon fill (pattern 5, added after the initial pass).** A field report —
"the spinner up/down arrows disappear under emulated high-contrast" — surfaced a
whole missing bucket: the theme's *entire icon system* paints glyphs as
`background-color: currentColor` clipped by a `mask-image`, and forced-colors
force-maps `background-color` to `Canvas` (page bg), so every masked icon went
invisible. Fixed centrally (block 2d in "What the guard restores"); a 5th audit
pattern (`masked-icon-fill`) now detects the `mask-image` + `currentColor` combo,
and computed-style probes confirm spinner/combobox/datebox/rating/tree/colorbox/
paging glyphs paint at the foreground while the baked check/radio boxes stay
untouched. *Audit caveat:* coverage is scored per-file (any covered class ⇒
covered), so a component already listed for another bucket (e.g. combobox, datebox,
tree, paging) will **not** be re-flagged for a masked icon — those were covered by
inspection, not by the linter. Only fully-new files (rating, colorbox, messagebox)
were flagged and added.

**Selected + focused is its own bucket (added 2026-09-04).** The audit's four buckets each look
at one pattern in isolation, so a guard that is individually correct — a `Highlight` selection
fill (2a) and a `Highlight` focus ring — can still cancel out when both apply to the same
element. Block **2a focus** fixes that for navbar. The check is not a pattern the linter can
see: it needs the *combination* driven in a browser (select the item, then focus it by keyboard)
and the ring's colour compared against the fill's, by RGB triplet — the two differed only in
alpha (0.8 vs 0.78), so a string comparison passes on an invisible ring. Guarded by
`forced-colors.spec.ts` › "focus ring on a SELECTED navbar item stays visible". The other five
selected-tint families in block 2a have **not** been measured for this
(as of 2026-09-04).

## Verify

There is **no real forced-colors on macOS** — "Increase contrast" maps to `prefers-contrast: more`, not `forced-colors`. On this dev machine you must emulate.

1. Build: `npm run build:css`, then confirm the rules survive minification:
   ```bash
   grep -o "@media (forced-colors:active)" zul/codegen/resources/web/zul/css/norm.css.dsp
   ```
2. Run the preview app: `withjdk.sh 17 mvn test exec:java@preview-app`.
3. **Manual:** Chrome/Edge DevTools → Rendering → *Emulate CSS media feature* `forced-colors: active`. Check `checkbox.zul` (three states distinguishable), `datebox.zul` (focus outline appears), `listbox.zul` (selection uses system Highlight), `window.zul` (visible border).
4. **Automated:** Playwright context option `{ forcedColors: 'active' }` (`page.emulateMedia({ forcedColors: 'active' })`), mirroring the existing tablet-UA project pattern. Under emulation `Highlight` ≈ `rgba(5,0,73,.8)` and `HighlightText` = white, so a selected row measures dark-blue bg + white text — verifiable via computed style.
5. **Regression:** turn emulation off → theme appearance is byte-for-byte unchanged (every rule is inside the media block).
