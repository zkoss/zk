# DESIGN.md — Marble Design Language Spec

Distilled from existing token files, component CSS, MD3 specifications, and the MUI v7 static-CSS reference (`…/material-ui-7.3.1/static-css-output/`).
Use this as the rulebook when styling a component the MUI reference does not cover.

---

## 1. Surface Palette

Color values: `zul/src/main/resources/web/zul/css/tokens/_colors.css`. The rules below give the reason for each choice.

Page background is `--zk-color-background`; cards, panels and inputs sit on `--zk-color-surface`; tonal steps use `--zk-color-surface-container-*`. Navigation uses a tonal surface, not a dark sidebar (see [navigation-surface-rules.md](navigation-surface-rules.md) and §16).

---

## 2. Text Colors

Primary text is `--zk-color-on-surface`; secondary text and placeholder use `--zk-color-on-surface-variant`; disabled text uses `--zk-color-disabled`. Values: `tokens/_colors.css`.

---

## 3. Brand & Status Colors

Reasons (not in the token file):

- **Warning** is one tone for text *and* fill, because the former `#ed6c02` failed 4.5:1 contrast as text.
- **On-status** (`--zk-color-on-status`) is the text color on a capped status fill, used by `.z-bg-info`. Badges carry a dark label instead (`--zk-badge-fg`), because their fills are bright tones.
- **Secondary** and every `*-container` are derived from the primary seed (`oklch(from …)`), not set by hand.

The five semantic **roles** (primary / secondary / success / warning / error) each carry
the full quartet (`<role>` / `on-<role>` / `<role>-container` / `on-<role>-container`) plus
a `-fill`, and back both the `.z-bg-<role>`/`.z-text-<role>` utilities and the button
color variants. The **status** palette (`status-success/warning/error/info/neutral`) is an
independent, brighter accent set reserved for badges, chips, progressmeter, notification
and toast. `info`/`neutral` have no brand role and live only in the status palette.

Chip and badge specs live in `js/zul/wgt/css/chip.css` and `native-modern-ui-components.md`.

### Brand-color override

The four semantic roles (primary / secondary / error / warning) are **seeds**. Each
role's `*-container` and `on-*-container` partners are **derived from the seed via
`oklch(from <seed> L c h)`** — pinning an absolute tone so the tint lands at a
consistent lightness for *any* brand hue — so overriding one seed (`--zk-color-primary`)
at `:root` re-tints the whole palette: containers, overlays, focus ring,
selected-row/alert/badge tints. See [brand-override.md](brand-override.md) for the full
customer contract, the (now solid-fill-only) contrast caveat, and the per-role tones.

**Coachmark card is a neutral surface.** `--zk-coachmark-bg` is `--zk-color-surface-container-low` and the text is `--zk-coachmark-fg` (`on-surface`), like the other popup-family components (`tokens/_component-theme.css:824`). The pointer triangle takes the same `--zk-coachmark-bg`, so card and pointer cannot differ. The page behind it is dimmed by `z-modal-mask`, coloured by `--zk-color-scrim` (`js/zul/wgt/css/misc.css:140-142`).

---

## 4. Spacing Scale (4dp baseline)

`4 / 8 / 12 / 16 / 20 / 24 / 28 / 32 / 40 / 48 / 64px`  
Tokens: `--zk-spacing-1` through `--zk-spacing-16`.

---

## 5. Corner Radii

Rule: **default to `--zk-shape-card`** for any container/card and for dialogs (`--zk-shape-dialog` aliases it, so an embedded Window matches a Panel and a dialog is never less rounded than a card, as MD3 orders them); **`--zk-shape-corner-extra-small`** for interactive controls (inputs, buttons, dropdowns, menus). Token values: `tokens/_shape.css`.

---

## 6. Elevation / Shadow

| Level | Use | Semantic token |
|-------|-----|---------------|
| 0 | none | `--zk-elevation-0` |
| 1 — card resting | card | `--zk-elevation-1` |
| 2 — dropdown / hover | dropdown | `--zk-elevation-dropdown` (= `--zk-elevation-2`) |
| 3 — dialog / modal | dialog | `--zk-elevation-dialog` (= `--zk-elevation-3`) |

Two further aliases have their own values: `--zk-elevation-card` (a single light shadow) and `--zk-elevation-resting` (a subtle two-layer shadow, "button + panel" in `_elevation.css`). They are not interchangeable with `--zk-elevation-1`.

Shadow values: `tokens/_elevation.css`.  
Cards have elevation only — no border. Use outlined variant (`1px solid outline-variant`) when resting on a white surface where shadow is invisible.

---

## 7. Typography

The size, weight and line-height of each role are in `tokens/_typography.css` (`--zk-typescale-*`) and, for the utility classes, in `utility/_typography.css`. Body and table text (`--zk-typescale-body-medium-size`, aligned to MUI v7) is denser than MD3 body-medium.


Font family: **Inter**, then `Inter Fallback` (Arial scaled to Inter's metrics, used only until Inter has loaded; see §7), then the system sans-serif stack. Token: `--zk-typescale-font-family`.  
Marble targets a denser scale than standard MD3 (aligned to MUI v7) — prefer 13–14px body rather than 16px.

**CSS class-naming convention — `z-` vs `m-` prefix.** The `z-` prefix is for
framework / component CSS (component variants, styled in `zul/src/main/resources/`
component + utility CSS — e.g. `z-button-outlined`, `z-badge-success`). The `m-`
prefix is for **page-level** classes that live in page-local CSS (used only by a
specific demo / use-case page — e.g. `m-card`, `m-badge`, `m-active`), never in
component CSS.

**Utility-class naming is size-based, not role-based.** The full MD3 type scale (role × size — `display/headline/title/body/label` × `large/medium/small`, each with its own size + weight + line-height) lives **only** in the `--zk-typescale-*` tokens, consumed by component CSS (`.z-button`, `.z-label`, …). The *utility* classes are a plain T-shirt size ladder — `z-text-xs … z-text-8xl` — so ZUL authors pick a size in one class instead of stacking size + weight + line-height. This is deliberate (chosen over 1:1 role-named utilities like `z-fs-title-md`): utility-first ZUL authoring wants short size-only classes, while role+size semantics belong at the token/component layer, not in page markup.

**Font loading (self-hosted, no CDN).** Inter ships **self-hosted**, never from the Google Fonts CDN: vendored from the `@fontsource-variable/inter` devDependency by `scripts/build-css.js` (`copyFonts()`) into `~./zul/font/`, declared as `@font-face` in `zul/css/tokens/_fonts.css`. It is the **variable** font (weight axis 100–900), split into two `unicode-range`-partitioned `woff2` subsets — `inter-latin-variable.woff2` (~47 KB) and `inter-latin-ext-variable.woff2` (~83 KB, for EU/Central-European glyphs, fetched only when a page needs them). The `@font-face` `url()` is written as a plain `url("~./zul/font/…")` with no DSP tag; `WcsExtendlet` encodes it when `zk.wcs` is served (context path included), so it resolves correctly inside the `zk.wcs` aggregate regardless of context path.

Rationale:
- **Self-host over CDN** — the CDN `@import` failed in air-gapped installs and leaked end-user IPs to Google (GDPR). Self-hosting is offline-safe and same-origin.
- **Self-host over system-font stack** — Marble is calibrated against the MUI v7 reference and has Playwright visual-regression tests; a system-font stack drifts per OS, destabilizing baselines and table layouts. A bundled font gives one reproducible look on every client and in CI.
- **Metric-matched fallback (`Inter Fallback`)** — ZK measures a widget once at mount and never again when a web font arrives, so a `sizedByContent` column was cut by an ellipsis whenever Inter loaded after ZK's first measurement (a 150 ms font delay is enough). `_fonts.css` defines a fallback face from the installed Arial (`size-adjust` 107.4% and matching line metrics) that sits right after `Inter` in `--zk-typescale-font-family`, so the measured width stays close to the final one. Measured on a one-listbox page: with a 150–1000 ms font delay 8/8 loads were cut without it and 0/8 with it; on a stress set of seven strings, 5 of 7 columns were cut without it and 1 of 7 (narrow glyphs) with it. `<link rel=preload>` (the font was fetched twice: the preload URL carries `;jsessionid`) and an inline data-URI font did not help. Weights 500, 600 and 700 (headers, cells) were not cut either with a 300 ms font delay, so the face is declared for the regular weight only. Where neither Arial nor Liberation Sans is installed the face is skipped and the stack behaves as before; if Inter never loads, text stays in the scaled Arial instead of the native UI font. Since ZK-6216 the client fires the `_onFontsLoaded` watch event when `document.fonts` fires `loadingdone`; Tabs listens to it to scroll the selected tab back into view, and a widget that does not listen still depends on this fallback.
- **Inter over Roboto** (MD3's canonical face) — Marble's MUI-aligned reference uses Inter; Inter's tall x-height + open apertures also read better at the 13–14px dense-table sizes this theme targets. The fallback stack still degrades gracefully to each OS's native UI font if Inter never loads.

**Deprecated font library-properties — intentionally absent (do not re-add).** ZK's `org.zkoss.zul.theme.fontFamily*` and `org.zkoss.zul.theme.fontSize*` library properties have been **deprecated since ZK 7.0.0** (superseded by LESS, and now by CSS custom properties). Marble sets **zero** library properties of any kind — all typography is driven by the `--zk-typescale-*` tokens in `zul/css/tokens/_typography.css`. Their absence is a deliberate modern-CSS design choice, **not** a gap: do not introduce these properties to "configure" fonts. To change a face or size, edit the token, not a `<library-property>`.

---

## 8. State-Layer Overlays

Apply via a `::before` pseudo-element with `background: currentColor` and `opacity`:

Opacities are the `--zk-state-*-opacity` tokens (hover, focus, pressed, dragged, disabled, disabled-container); values: `tokens/_colors.css`.

For surfaces (rows, list items) prefer a solid color on hover instead of the overlay.

---

## 9. Motion

Default interactive transition follows the MUI standard curve. Tokens (values: `tokens/_motion.css`):
- Duration: `--zk-motion-duration-standard` for most controls (background-color, box-shadow, border-color).
- Easing: `--zk-motion-easing-standard`; `--zk-motion-easing-legacy` is the same curve.

### Reduced motion (`prefers-reduced-motion`) — *implemented 2026-06-29*

The theme honors the OS "reduce motion" setting (WCAG 2.1 SC 2.3.3, *Animation from Interactions*). When `@media (prefers-reduced-motion: reduce)` matches, all CSS transitions and animations are neutralized theme-wide via a universal reset in `tokens/_motion.css` (bundled into the global `norm.css.dsp`, so it applies in both standalone and JS-Embed/`browserDefault` modes).

- **Mechanism:** a single `*, *::before, *::after` rule sets `transition-duration`/`animation-duration` to `1ms !important`, caps `animation-iteration-count: 1`, and forces `scroll-behavior: auto`. Universal (not token-zeroing) so it also covers hardcoded durations and `@keyframes` animations (e.g. the tablet bottom-sheet slide-up).
- **`1ms`, not `0s`/`0.01ms`:** a non-zero duration still fires `transitionend`/`animationend` (avoids hanging any widget that awaits them); `1ms` specifically because CleanCSS rounds sub-millisecond values down to `0s` at build time.
- **Scope limit:** CSS motion only. ZK's JS-driven slide/fade effects (some popup open/close) are not CSS animations and are unaffected.

### Forced colors / Windows High-Contrast Mode (`forced-colors`) — *implemented 2026-07-14*

The theme honors `@media (forced-colors: active)` (Windows High-Contrast Mode; also Chrome/Edge/Firefox emulation). In this mode the OS replaces the palette with a small set of **system colors** and **strips every `box-shadow`**, which would otherwise erase Marble's `box-shadow`-based input focus rings and popup/window elevation, and would flip the checkbox/selected-row indicators to system colors while their baked-in glyphs did not follow. A single central, **unlayered** guard block in `tokens/_forced-colors.css` (bundled into `norm.css.dsp`) restores them. Full spec: [forced-colors.md](forced-colors.md).

- **Mechanism:** unlayered rules beat every `@layer zk-*` component rule, so the guards override component styles **without `!important`** and without editing the component files. Colors use CSS system-color keywords only (`Canvas`/`CanvasText`/`Highlight`/`HighlightText`/`ButtonText`/`GrayText`).
- **What it restores:** real `border` on elevation-only surfaces (window/panel/popups/menupopup/listbox/grid/card); a real `outline` on text-input `:focus-within` (datebox/timebox/spinner/bandbox/combobox); `Highlight`/`HighlightText` on selected list/tree rows; `forced-color-adjust: none` on the checkbox check + selected-row check (a designed fill+glyph pairing the OS palette would break); a `ButtonText` border on all buttons.
- **Not restored (intentional):** `::before`/`::after` state-layer hover tints — cosmetic feedback, not information loss. Baked-gray dropdown chevrons (datebox/selectbox) keep their fixed color — usable but a known minor limitation.
- **Approach rationale:** CSS `forced-colors` override (not a separate dedicated high-contrast theme). This matches the industry mainstream — Vaadin (the closest Java-web analog) ships the same CSS-override approach in its base styles, and Microsoft itself moved Fluent from a dedicated high-contrast theme to standard `forced-colors` + system colors. See [`design-decisions.md`](design-decisions.md).

---

## 10. Density

Control heights are tokens in `tokens/_sizing.css` (`--zk-button-height`, `--zk-control-height-*`); the compact variants are in [data-dense-mode.md](data-dense-mode.md).

Rule: **table rows are ~52px at the default density** (measured: the top and bottom `--zk-grid-cell-padding` plus one line of `--zk-typescale-body-medium-size` text at `--zk-typescale-body-medium-line-height`); header and cell padding are both `--zk-grid-cell-padding` on all sides.

### Input width: datebox / timebox follow the `cols` API

datebox and timebox always render a `size` on the `<input>` (`Datebox` defaults `cols` to 11; `Timebox` derives it from the formatted value length), so Marble does **not** use `field-sizing: content` on them — it makes the browser ignore `size`, so an explicit `cols` would have no effect (ZK-6112). Width follows `cols`, as in IceBlue. A date format longer than ~11 characters is clipped unless the app raises `cols` (the core defect is tracked as ZK-6178). daterangebox and timepicker do not render a `size`, so they keep a content-hug policy.

| Field | Width behaviour |
|-------|-----------------|
| **datebox, timebox** | **follow `cols`** (rendered as `size`), no `min-width` floor: a width narrower than the input and button shrinks the input, down to its padding, and keeps the button whole at the right edge; below that (about 56px) the box clips the button itself. |
| **daterangebox, timepicker** | **hug content** — `field-sizing: content` + a `min-width` floor, because ZK renders no (or a hardcoded) `size` for them. A set width (`width`, `hflex`) drops the floor, so the inputs give way to the button as in datebox; a daterangebox's two inputs shrink only to their padding, so below about 100px the box clips its button. |
| **combobox, bandbox** (free-text) | keep the default / container-fill width. Content-hug would resize the box on every keystroke while typing — jarring. |
| **spinner, doublespinner** (numeric) | keep the default / container-fill width (numeric length is unbounded and typed). |

datebox/timebox use `flex: 1 1 auto` (basis = `size`, and they still **fill** an `hflex`/width-forced root — forms are unaffected); daterangebox uses `flex: 0 1 auto` because its two side-by-side inputs must not grow.

This is a deliberate divergence from MUI, whose single `OutlinedInput` fills its container (or the UA default) with left-aligned text and no content-hug. MUI has no opinion on a *standalone* date field's width; Marble sizes it to its content so a bare date/time field reads as one compact field rather than a fixed box with dead space, and long formats are never clipped. See contracts `datebox.md` / `timebox.md` / `daterangebox.md`.

### Table-header background: no fill

Grid, Listbox, and Tree headers share **one rule**: **no background color**. Header cells inherit the body surface; visual hierarchy comes from three signals:

| Signal | Value |
|--------|-------|
| Font weight | `--zk-typescale-label-large-weight` (label-large) |
| Text color  | `--zk-color-on-surface-variant` (slightly muted vs row body) |
| Divider     | `1px solid --zk-color-outline-variant` border-bottom under the header row |

**Why no fill?**

1. **Mira reference (MUI Simple Table)** — `MuiTableCell-head` has only `font-weight: 500` and the table's body `border-bottom`; no `background-color`. Our reference theme set the precedent.
2. **MD3 Data Table spec** — Material Design 3 Data Tables use the same `surface` color for header and body; differentiation is typography + dividers, not fills. Fills are reserved for selected / hovered / pinned rows.
3. **Density & calm** — Enterprise dashboards often stack 3–5 tables on one screen. A tinted header on every table creates visual noise; an unfilled header keeps the eye on the data.

If a future variant needs strong header separation (e.g. for sticky-header reporting tables on a busy background), introduce a `--zk-grid-header-fill` token rather than re-instating the global fill.

This rule is **uniform across grid / listbox / tree**. Do not let one diverge from the others.

### Auxhead: faint tonal band on the row (ZK-only)

ZK's `<auxhead>` is a multi-level header row (no native HTML equivalent) used to group columns under a span (e.g. `<auxheader colspan="2">`). With column headers now transparent, the auxhead row needs **one** distinguishing signal so the multi-level structure reads at a glance.

**Decision**: apply a faint band on the auxhead **row** only:

| Element | Background |
|---------|------------|
| `.z-auxhead` (TR) | `--zk-color-surface-container-low` (faint tonal tint) |
| `.z-auxheader` (TH) | `transparent` |
| `.z-column` (TH below) | `transparent` |

The faint band on the row visually anchors the parent grouping; transparent cells let `colspan` groupings inherit the band cleanly. This is the same banding pattern Excel and financial tables use for category rows above leaf columns.

**Why a tinted row (not a stronger border)**: the boundary between auxhead and columns is already a `1px outline-variant` border, and a thicker border would compete with the column-header divider. The tonal band gives a 4th hierarchy tier (row band → header row → body rows → footer) without adding linework.

If a future use-case needs auxhead to match the column-header transparency exactly, downgrade to typographic-only hierarchy (label-large weight on auxheader, label-medium on column) — but keep the rule symmetric across grid / listbox / tree.

---

## 11. Border Rules

All borders are 1px, except the text-input focus ring (2px, `--zk-input-border-color-focus`). Colors come from tokens (values: `tokens/_colors.css`):

- Component default border: `--zk-color-outline`. It is the border that identifies a field, a list checkmark or an unselected rating icon, so it stays at 3:1 or more against `--zk-color-surface-container-low` (WCAG 1.4.11). It is not a text colour.
- Dividers and row separators: `--zk-color-outline-variant`.
- Input hover: `--zk-color-on-surface`.
- Card: no border (elevation only); an outlined card uses `outline-variant` and no shadow.

### Data-table frame (grid / listbox / tree)

These three are **outlined by default** (`1px solid outline-variant`, **no shadow**) — they
are assumed standalone, and on the near-white page the elevation shadow is invisible, so the
border is the only readable boundary. **Never border + shadow together.** When nested in a
bounded parent, strip the frame: automatically inside panel/groupbox
(`.z-panel-body .z-{comp}` / `.z-groupbox .z-{comp}` → `border: none`), or explicitly via the
opt-in variant sclass **`z-{grid,listbox,tree}-noborder`** (ZK emits no border attribute for
these, unlike `window`'s `z-window-noborder` — the variant name mirrors that ZK convention).
Decision ratified 2026-06-11 (outlined chosen over Mira's elevated because the near-white
page makes the elevation shadow invisible; revisit if the surface palette darkens).

---

## 12. Iconography (Lucide SVG)

| Context | Size | Color |
|---------|------|-------|
| Inline / table / form | 16–18px | `--zk-color-on-surface-variant` |
| Nav item | 20px | the nav item text color (see §16) |
| Display / hero | 24px | `--zk-color-primary` |

Use Lucide icons via the `z-icon-*` CSS mask pattern already established in the theme.

### Header control icon buttons (close / maximize / minimize / collapse)

One family across every header strip that carries window-management controls (goldenlayout had invented its own 20px text-glyph controls):

Button box and icon sizes are in `js/zul/wnd/css/window.css` and `panel.css`. The `goldenlayout` `.lm_controls` strip uses the panel size (nearest header height) and the same icons panel's `z-icon-expand`/`z-icon-times` resolve to.

Shared rules: `--zk-shape-corner-full` radius, `--zk-color-on-surface-variant` at rest,
hover reveals a circular state layer. Hover fill is `--zk-color-surface-container` where the
host header is lighter than it (window, panel); on a strip that is already `surface-container`
(goldenlayout), use a mix-based layer (`color-mix(in srgb, var(--zk-color-on-surface) 8%, transparent)`)
so the circle stays visible. Icons render as Lucide masks (via `z-icon-*` classes when ZK
emits them, or embedded data-URI masks on library-injected elements that carry no class —
the signature.css / colorbox.css precedent). Text glyphs (`×`, `⤢`, …) are banned in this
family: their optical size and side-bearings never match the SVG icons.

---

## 13. ZK-Specific DOM Quirks

Patterns where ZK's rendered DOM diverges from what the CSS selector alone would suggest.
Generator agents must read this section before modifying any listed component.

### Button — vertical orient (`orient="vertical"`)

ZK renders `orient="vertical"` as `icon + <br/> + label` inside the `<button>` element.
**No separate CSS class is added.** The `<br/>` is the only DOM indicator.

**Selector:** `.z-button:has(br)`

**Required CSS** (the same rule is in `zul/src/main/resources/web/js/zul/wgt/css/button.css`):
```css
.z-button:has(br) {
    flex-direction: column;
    gap: 2px;
    padding: 8px 16px;
    min-height: var(--zk-control-height-lg);
    line-height: 1;
}
.z-button:has(br) br { display: none; }
```

Do not remove the `:has(br)` selector or replace it with a class-based selector — no such class exists in ZK's output.

### Checkbox — mold state class naming

For non-default molds, ZK prefixes the state class with the mold name. The selector pattern is **`z-checkbox-{mold}-{state}`**, not `z-checkbox-{state}`.

| mold | unchecked class | checked class | disabled class |
|------|----------------|---------------|----------------|
| default | `z-checkbox-off` | `z-checkbox-on` | `z-checkbox-disabled` |
| switch | `z-checkbox-switch-off` | `z-checkbox-switch-on` | `z-checkbox-switch-disabled` |
| toggle | `z-checkbox-toggle-off` | `z-checkbox-toggle-on` | `z-checkbox-toggle-disabled` |

**Critical:** Using `.z-checkbox-switch.z-checkbox-on` (unprefixed state) will NEVER match the switch mold DOM. Always use `.z-checkbox-switch-on` (mold-prefixed state) as the selector.

### Combobutton — dropdown button click routing

ZK's `Combobutton.doClick_()` checks `evt.domTarget` against `this.$n('btn')` (= element with id `{uuid}-btn`) to decide whether to open the popup or fire `onClick`. The `.z-combobutton-button` element must have `pointer-events: auto` (the default) so that clicks on the arrow area register on that element. Setting `pointer-events: none` causes clicks to pass through to `.z-combobutton-content`, bypassing the popup-open branch and firing `onClick` instead.

### Bandbox — popup double border

DOM structure: `<span class="z-bandbox">` → `<div class="z-bandbox-popup">` → `<div class="z-bandpopup">`.

`.z-bandbox-popup` is the sole visual frame (border, border-radius, box-shadow, background). **Never add a border or box-shadow to `.z-bandpopup`** — it is the inner content wrapper and any such styling will produce a double border.

### Bandbox — buttonVisible="false" class target

When `buttonVisible="false"`, ZK calls `RoundUtl.buttonVisible(wgt, false)` which adds `z-bandbox-disabled` to the `<a>` button element (`$n('btn')`), NOT to the root `<span>`.

The correct hide selector is: `.z-bandbox-button.z-bandbox-disabled { display: none; }`

Do not confuse with `.z-bandbox.z-bandbox-disabled` (used when the whole component is disabled).

### Bandbox — inplace editing class

When `inplace="true"` and the input is blurred, ZK adds `z-bandbox-inplace` to the root `<span>` element. On focus, the class is removed and the full input appearance is restored.

Style `.z-bandbox.z-bandbox-inplace` to look like plain text: transparent border, transparent background, no box-shadow, hidden button. Do not style the child input or button separately — targeting the root state class is sufficient.

### Label ↔ field horizontal alignment (form rows)

A standalone `<label>` placed on the same line as a taller field has **no MD3 spec** — MD3 labels live inside/above the field. The parity target (MUI `FormLabel` / `FormControlLabel` rows, `align-items:center`) is **centre alignment** of the label against the field's control box.

Why a separate `<label>` is needed at all: most ZK inputs (textbox, combobox, datebox, …) have **no `label` attribute**, so a field caption must be an adjacent `<label>` component. Only selection controls (radio, checkbox) carry their own `label` — and those captions are part of the control, centred by the component itself.

Theme stance:
- The theme does **not** auto-centre a bare `<label>` — alignment is a container concern, never a `.z-label` property (a label must still baseline-align in running text and wrap as a caption). See the zk-component-rules skill, `.claude/skills/zk-component-rules/reference/inline-label-alignment.md`.
- The flex centre is **required only for selection controls** (radio / checkbox): a label beside a `textbox`/`combobox` already centres in a plain block row, so leave those alone. For a radio/checkbox row, wrap it: `<div sclass="z-d-flex z-items-center z-gap-3">` (verified 0px; `z-gap-*` sets the label↔field gap since flex collapses inter-element whitespace). `<hlayout valign="middle">` / `<hbox valign="middle">` are equivalent ZK-attribute alternatives. Applying the wrapper to an input row is harmless but unnecessary; for a column of mixed rows you may apply it uniformly for visual consistency.
- A bare `<div>` row is a **baseline context**: it coincidentally centres single-line inputs (textbox via `vertical-align:middle`, combobox via baseline-at-centre) but drops radio/checkbox labels ~4–5px (their baseline is the caption text, below the box centre). The default `<hlayout>` (`valign="top"`) puts every label ~10px high. Neither is a reliable form-row container — the divergence is correct CSS for the mode, not a theme defect.
- The **inline-flex field roots** carry `vertical-align: middle`, so they centre on the line like the textbox. For datebox, timebox, spinner, doublespinner, bandbox, inputgroup and, in EE, timepicker and chosenbox, the reason is Safari 18: it takes an empty field's baseline from the input's bottom edge, so the empty field sits about 14px off the text line and jumps when typed into (WebKit bug 294558, fixed in Safari 26). For cascader and searchbox (EE) the reason is in every browser: with no value and no placeholder text their first flex item is an empty placeholder with no baseline, so the box is aligned by that item's bottom edge. The text beside such a field therefore sits about 1px above the field centre, in every browser.
- Live demo of both states: `zkthemelab/src/main/webapp/web/label.zul` (a "Default block row" group showing the radio/checkbox drop, and a "Correct" flex-centred group).

---

## 14. Splitter Family (unified spec)

One resize affordance across the app. Four implementations share this spec:
`splitter` (zul.box), `borderlayout` region splitters, `splitlayout` (zkmax), and
`goldenlayout` `.lm_splitter`.

**Implementation source (2026-06-06):** the canonical values below are defined ONCE as
`--zk-splitter-*` tokens in `zul/css/tokens/_splitter.css` (globally loaded via
`norm.css.dsp`). Component CSS must consume the tokens — never restate these values as
literals (family fragmentation, 2026-06-04; cursor drift, 2026-06-06). The only
documented per-component exception is the GL transparent gutter; it stays local in that
component file with a comment citing this section. The **bar is borderless across the
whole family** — the `surface-container` fill is itself the divider; the former splitlayout
1px `outline-variant` bar border was retired as the lone outlier (2026-06-23, it
stacked into a double-line between splitlayout's bordered panes). **The pill does not resize
on hover** anywhere — MD3 communicates hover through the state-layer colour, not geometry;
borderlayout's former 28→44px hover growth was retired (2026-06-23) so all members
keep `--zk-splitter-pill-length` on the long axis at every state (the caret already has its space at idle via
`opacity: 0`).

The canonical values (bar thickness, idle/hover/drag backgrounds, cursors, transition, pill size and colors) are in `tokens/_splitter.css`. The rules that go with them:

- The bar is `--zk-splitter-thickness` on both axes, filled with `--zk-color-surface-container`; hover and drag tint it with primary through `--zk-state-hover-opacity` / `--zk-state-pressed-opacity`.
- Cursor is `col-resize` on a vertical bar and `row-resize` on a horizontal bar.
- Non-resizable (`*-nosplitter`): cursor `default` AND hover keeps the idle background. A primary tint on a bar that cannot resize is a false affordance. borderlayout is exempt: ZK hides the strip entirely when `splittable="false"`.

**Actuator pill** (the three ZK-mold splitters — `splitter`, `borderlayout`, `splitlayout` —
all render `<span>-button` + grip/caret/grip icons): the pill is as thick as the bar and `--zk-splitter-pill-length` long, fully rounded. Idle it is `--zk-color-outline-variant`, with no border and no elevation; hover/active turns it `--zk-color-primary` with `on-primary` icons. The grip icons are always visible; the collapse caret is hidden at idle and fades in on hover.

Rationale: MD3 has no splitter; the spec composes the MD3 bottom-sheet drag handle
(slim inline pill) with a surface-tinted divider (originally designed for borderlayout —
that CSS's comment block is the source of record for the pill reasoning).

**GoldenLayout (revised 2026-06-05 — full family alignment; idle fill revised
2026-06-06):** `.lm_splitter` is GoldenLayout-library-injected — no button element — but
that does NOT exempt it from the pill: GL has no `setBtnPos_` JS centering, so the actuator
pill is drawn entirely in CSS via `::before` (pill: bar-thickness × `--zk-splitter-pill-length`, `outline-variant`,
`corner-full`) with grip dots via `::after` (`--zk-splitter-icon-size` Lucide `ellipsis-vertical`/`ellipsis` mask,
`on-surface-variant`, `opacity: 1`; hover → pill `primary`, dots `on-primary`). The
"no pill, opacity-.5 dot marker" exception was rejected because it shipped an imperceptible affordance. GL's bundled CSS is never loaded in ZK, so the theme must also
set the family cursors (`col-resize` on `.lm_horizontal`, `row-resize` on `.lm_vertical`).
Remaining structural exception: no collapse caret (GoldenLayout has no collapse feature).

**GL idle-fill exception:** the family's `surface-container`
idle fill is REPLACED by `transparent` for GL only. The family fill exists to make the bar
visible *between surface panes*; GL bars abut `surface-container` `.lm_header` strips, so
the identical fill erased the very boundary it marks — bar + header read as one region
(worst on `lm_vertical`). GL panels are self-bordered cards on a transparent canvas: the
splitter region reads as a card *gutter*, with the pill + grips carrying the affordance.
Hover/drag tints become translucent: `color-mix(in srgb, var(--zk-color-primary)
<hover|pressed opacity>%, transparent)`. General principle for future splitter contexts:
**the bar idle fill must contrast with every surface it abuts** — when neighbors are
`surface-container`, the family fill is disqualified (`doc/contracts/goldenlayout.md` outcome row M21 encodes
this as an adjacent-surface comparison).

**JS centering rule (refined 2026-06-04):** all three ZK-mold splitters center the pill on
the bar's long axis via JS inline margin (`setBtnPos_`). Never **half-mix** CSS and JS
centering on the same axis — either leave the axis fully to JS (only safe when the bar's
long-axis size is CSS-fixed at bind time), or take full CSS ownership: neutralize the JS
inline margin with `margin-left/top: 0 !important` and center via `left/top: 50% +
transform`. CSS ownership is required on a flex-resolved axis (`setBtnPos_` can run while
the offset is still 0 and write margin 0 permanently) — see
`.claude/skills/zk-component-rules/components/splitlayout.md` (family-wide rule; both
half-mixing and JS-only-on-flex-axis shipped as real bugs).

## 15. Scrollbar (added 2026-06-30)

Two distinct scrollbar surfaces, both MD3-aligned to the same token language:

**Native bar** (`org.zkoss.zul.nativebar="true"` — the ZK default). Styled via
`::-webkit-scrollbar*` pseudo-elements in `base/_reset.css` (global) and
`.z-frozen-inner::-webkit-scrollbar*` in `mesh/css/frozen.css` (frozen grid columns).
Passive, OS-drawn, always occupies layout space.

**Simulated bar** (`nativebar="false"` — drawn by the `zul.Scrollbar` helper; see
`.claude/skills/zk-component-rules/components/scrollbar.md` for DOM + the must-bundle
loading rule). CSS in `js/zul/wgt/css/scrollbar.css`, **bundled into `norm.css.dsp` via
`scripts/build-css.js` `normFiles`** — it has no widget css-uri, so a standalone
`.dsp` would never load (see `.claude/skills/zk-component-rules/components/scrollbar.md`). Two render modes selected by
`data-embedscrollbar`: **overlay** (`false`, hover-only float, no reserved space) and
**embedded** (`true`, always-visible rail in a reserved gutter).

The property values (colors, radii, lane/track/thumb thickness, transition) are in `js/zul/wgt/css/scrollbar.css`. The rules and reasons that matter:

- The track (rail) is a faint surface tint, a visible channel.
- The step buttons (carets) are shown: flat, neutral, with a faint state layer on hover. Along-axis they are 12px, because `syncSize()` reads their offset size; cross-axis they are 8px and edge-anchored.
- The embed (idle) rail is as thick as the hover *track* (8px, not the 6px thumb), so rest to hover does not shift.
- Track, thumb and arrows are edge-anchored (vertical to `right`, horizontal to `bottom`); the lane carries no margin.

**Distinct-from-native, but MD3:** a `nativebar="false"` bar that
looks identical to native is pointless, so the simulated bar reads as a deliberate themed
control via (a) a **visible faint track channel** (the native overlay has none) and (b) a
**neutral, slightly bolder pill thumb** (idle `outline` → hover `on-surface-variant`, vs the
native bar's `outline-variant` → `outline`). The **caret step buttons are shown** (against
the MD3 default of hiding scrollbar arrows) — kept MD3-restrained:
flat, neutral, with a faint hover state layer, and they appear **only on hover** with the
rest of the bar (the whole `.z-scrollbar` is `display:none` at rest). ZK's `syncSize()` reads
the buttons' offset size to inset the wrapper, so the buttons must carry an explicit
width/height (12px). The `*-embed` rail carries **no** `:hover` rule — it is `display:none`
whenever the pointer is over the body (the full bar replaces it).

**Edge-hugging, no lateral jump (Option A):** in
embedded mode the rest `*-embed` rail and the hover bar must sit on the *same* cross-axis line
— otherwise the bar visibly jumps on mouse-over (MD3 continuity-of-motion; the original
design jumped ~5px). ZK's scroll-sync pins **both** the bar and the `*-embed` rail to the same
cross-axis anchor inline (`right`/`bottom = -scrollPos`, i.e. flush when unscrolled) — so the
anchor is JS-owned and identical for both; only the embed *thickness* and the bar's *internal*
layout are ours. So: (a) **edge-anchor** the track (`right:0`/`bottom:0`, flush) within the lane, thumb
(`right:1px`/`bottom:1px`, centred in the track), and arrows; (b) size the `*-embed` rail to
the **8px track width** (not the 6px thumb) so its forced-flush footprint is *identical* to
the hover track. Result: rest and hover share one 8px flush footprint centred ~4px from the
edge; on hover it merely refines into thumb-in-track + arrows, with zero lateral movement.

## 16. Navigation — navbar / navitem selected state (added 2026-07-07)

Navigation selection is its own MD3 family (the item that marks the *current
location*, not the arrow-key cursor) — see
`.claude/skills/marble-theme/reference/selected-state-families.md`. Marble
follows the MD3 canonical treatment: the active marker is a **rounded tonal
container**, aligned with MD3 Navigation Drawer / MUI `ListItemButton`.

| Property | Value | Token |
|----------|-------|-------|
| Selected item background | primary tint container | `--zk-navitem-selected-bg` |
| Selected item text | on-primary-container (primary on the tint is 3.9:1, below WCAG 1.4.3) | `--zk-navitem-selected-fg` |
| Selected item weight | 600 | literal |
| Content-link corner radius | rounded (the tint container is rounded) | `--zk-navitem-radius` |

**No left-edge accent bar.** The rounded tonal container is the *whole* active
marker. An earlier iteration added a left bar — first as a `border-left` (which
curled into an arc on the rounded, `overflow:hidden` content link), then as a
straight inset `::after` strip. On design review (2026-07-07) the bar was dropped
entirely: stacking a classic/enterprise-sidebar accent on the MD3 pill states
"active" twice in two idioms and clashes where the square bar meets the container's
rounded corners. Do not re-introduce any left accent (`border-left`, `::before`, or
`::after` strip).

**Navigation-active ≠ list-row focus.** The `.z-listitem` blue *left line* is
NOT a selected-state accent — the selected state is a `secondary-container` fill
(list-row family, per the selected-state-families skill). That left line is the listbox
**focus** indicator (`box-shadow: inset 3px 0 0 var(--zk-color-primary)` on the
first cell), a separate affordance that merely resembles the navbar accent. They
are intentionally different; do not unify them.
