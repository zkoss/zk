# DESIGN.md — Marble Design Language Spec

Distilled from existing token files, component CSS, MD3 specifications, and the MUI v7 static-CSS reference (`…/material-ui-7.3.1/static-css-output/`).
Use this as the rulebook when styling a component the MUI reference does not cover.

---

## 1. Surface Palette

| Role | Color | Token |
|------|-------|-------|
| Page background | `#f7f9fc` | `--zk-color-background` / `--zk-color-surface-container-low` |
| Card / panel surface | `#ffffff` | `--zk-color-surface` |
| Content area (inner) | `#f7f9fc` | `--zk-color-surface-variant` (same as page bg in practice) |
| Surface container | `#e8eef7` | `--zk-color-surface-container-high` |
| Sidebar background | `#233044` | (dark navy, nav item bg; drawer paper is white) |
| Input background | `#ffffff` | `--zk-color-surface` |

---

## 2. Text Colors

| Role | Color | Token |
|------|-------|-------|
| Primary text | `rgba(0,0,0,0.87)` | `--zk-color-on-surface` |
| Secondary / muted | `rgba(0,0,0,0.6)` | `--zk-color-on-surface-variant` |
| Disabled text | `rgba(0,0,0,0.38)` | `--zk-color-disabled` |
| Link / branded | `#376fd0` | `--zk-color-primary` |
| On-brand (text on blue) | `#ffffff` | `--zk-color-on-primary` |
| On-error | `#ffffff` | `--zk-color-on-error` |
| Placeholder | `rgba(0,0,0,0.6)` | `--zk-color-on-surface-variant` |

---

## 3. Brand & Status Colors

| Role | Color | Token |
|------|-------|-------|
| Brand blue | `#376fd0` | `--zk-color-primary` (seed) |
| Brand container | `≈#d6e4ff` | `--zk-color-primary-container` (derived from the seed via `oklch(from …)`) |
| Secondary | `≈#586f95` | `--zk-color-secondary` (derived from the primary seed — primary's hue at 41% chroma, `L 0.54`) |
| Error | `#d32f2f` | `--zk-color-error` (full role quartet) |
| Warning | `#bd3f00` | `--zk-color-warning` (full role quartet — one tone for text *and* fill; `#ed6c02` failed 4.5:1 as text) |
| Success | `#2e7d32` | `--zk-color-success` (full role quartet — container ≈ `#c8e6c9`) |
| Success (badge accent) | `#4caf50` | `--zk-color-status-success` |
| Info | `#007fab` | `--zk-color-status-info` |
| Neutral | `#9e9e9e` | `--zk-color-status-neutral` |
| On-status (text on a capped status fill) | `#ffffff` | `--zk-color-on-status` — used by `.z-bg-info`; badges carry a dark label instead (`--zk-badge-fg`), because their fills are bright tones |

The five semantic **roles** (primary / secondary / success / warning / error) each carry
the full quartet (`<role>` / `on-<role>` / `<role>-container` / `on-<role>-container`) plus
a `-fill`, and back both the `.z-bg-<role>`/`.z-text-<role>` utilities and the button
color variants. The **status** palette (`status-success/warning/error/info/neutral`) is an
independent, brighter accent set reserved for badges, chips, progressmeter, notification
and toast. `info`/`neutral` have no brand role and live only in the status palette.

Chips use tinted backgrounds (`rgba(color, 0.1)`). Badges use full-strength color on white.

### Brand-color override

The four semantic roles (primary / secondary / error / warning) are **seeds**. Each
role's `*-container` and `on-*-container` partners are **derived from the seed via
`oklch(from <seed> L c h)`** — pinning an absolute tone so the tint lands at a
consistent lightness for *any* brand hue — so overriding one seed (`--zk-color-primary`)
at `:root` re-tints the whole palette: containers, overlays, focus ring,
selected-row/alert/badge tints. See [brand-override.md](brand-override.md) for the full
customer contract, the (now solid-fill-only) contrast caveat, and the per-role tones.

**Coachmark is intentionally the lone brand-filled popup.** Its card uses `--zk-color-primary` fill + `on-primary` text, while every other popup-family component (notification, toast, tooltip/popup, bandpopup) is a neutral/tinted/dark surface. This is deliberate, not an inconsistency to "fix": consistency here is **by role, not by sameness**. A coachmark is *proactive guided discovery* — it dims the page with a scrim and must win attention against it (a CTA), so the brand fill is correct (and matches Material's original Feature-Discovery pattern; MD3/MUI dropped the dedicated component, so there is no canonical token answer). Consequence: because the surface is brand-filled, child filled controls (`.z-button`) inherit the global primary fill and vanish — the theme MUST invert them (`.z-coachmark-content .z-button`: white bg + primary text + flipped state-layer/focus-ring).

---

## 4. Spacing Scale (4dp baseline)

`4 / 8 / 12 / 16 / 20 / 24 / 28 / 32 / 40 / 48 / 64px`  
Tokens: `--zk-spacing-1` through `--zk-spacing-16`.

---

## 5. Corner Radii

| Context | Radius | Token |
|---------|--------|-------|
| Button, input, menu | `4px` | `--zk-shape-corner-extra-small` |
| Card, alert, badge, list | `6px` | `--zk-shape-card` (custom) |
| Chip, search box | `6px` | `--zk-shape-card` (custom) |
| Dialog | `4px` | `--zk-shape-corner-extra-small` |

Rule: **default to 6px** for any container/card; **4px** for interactive controls (inputs, buttons, dropdowns).

---

## 6. Elevation / Shadow

| Level | Shadow | Semantic token |
|-------|--------|---------------|
| 0 | none | `--zk-elevation-0` |
| 1 — card resting | `rgba(50,50,93,0.024) 0px 2px 5px -1px, rgba(0,0,0,0.05) 0px 1px 3px -1px` | `--zk-elevation-1` |
| 2 — dropdown / hover | `0 2px 6px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.14)` | `--zk-elevation-2` |
| 3 — dialog / modal | `0 4px 12px rgba(0,0,0,0.12), 0 2px 4px rgba(0,0,0,0.14)` | `--zk-elevation-3` |

Simplified card shadow alias: `rgba(50,50,93,0.024) 0px 2px 5px -1px, rgba(0,0,0,0.05) 0px 1px 3px -1px` (`--zk-elevation-card`).  
Cards have elevation only — no border. Use outlined variant (`1px solid outline-variant`) when resting on a white surface where shadow is invisible.

---

## 7. Typography

| Role | Size | Weight | Notes |
|------|------|--------|-------|
| Section label (nav) | 11px | 500 | uppercase (`MuiTypography-subtitle2`) |
| Badge / chip | 11px | 500–600 | |
| Small helper / caption | 11px | 400 | (`MuiTypography-caption` ≈ 11.2px) |
| Sub-nav item | 13px | 400 | inactive: `rgba(255,255,255,0.7)`; active: `#fff` |
| Body / table cell | 13px | 400 | (denser than MD3 body-medium) |
| Card title / label | 16px | 500 | (`MuiTypography-h6`) |
| Input / placeholder | 13px | 400 | `--zk-typescale-body-medium-*` |
| Tab label | 14px | 500 | |
| Page subtitle | 15px | 400 | (`MuiTypography-subtitle1` ≈ 14.86px) |
| Page title | 24px | 600 | (`MuiTypography-h3`) |
| KPI value | 24px | 400 | (`MuiBox` with custom font-size) |

Font family: **Inter**, then `Inter Fallback` (Arial scaled to Inter's metrics, used only until Inter has loaded; see §7), then the system sans-serif stack. Token: `--zk-typescale-font-family`.  
Marble targets a denser scale than standard MD3 (aligned to MUI v7) — prefer 13–14px body rather than 16px.

**CSS class-naming convention — `z-` vs `m-` prefix.** The `z-` prefix is for
framework / component CSS (component variants, styled in `zul/src/main/resources/`
component + utility CSS — e.g. `z-paging-outlined`, `z-badge-success`). The `m-`
prefix is for **page-level** classes that live in page-local CSS (used only by a
specific demo / use-case page — e.g. `m-card`, `m-badge`, `m-active`), never in
component CSS.

**Utility-class naming is size-based, not role-based.** The full MD3 type scale (role × size — `display/headline/title/body/label` × `large/medium/small`, each with its own size + weight + line-height) lives **only** in the `--zk-typescale-*` tokens, consumed by component CSS (`.z-button`, `.z-label`, …). The *utility* classes are a plain T-shirt size ladder — `z-text-xs … z-text-8xl` — so ZUL authors pick a size in one class instead of stacking size + weight + line-height. This is deliberate (chosen over 1:1 role-named utilities like `z-fs-title-md`): utility-first ZUL authoring wants short size-only classes, while role+size semantics belong at the token/component layer, not in page markup.

**Font loading (self-hosted, no CDN).** Inter ships **self-hosted**, never from the Google Fonts CDN: vendored from the `@fontsource-variable/inter` devDependency by `scripts/build-css.js` (`copyFonts()`) into `~./zul/font/`, declared as `@font-face` in `zul/css/tokens/_fonts.css`. It is the **variable** font (weight axis 100–900), split into two `unicode-range`-partitioned `woff2` subsets — `inter-latin-variable.woff2` (~47 KB) and `inter-latin-ext-variable.woff2` (~83 KB, for EU/Central-European glyphs, fetched only when a page needs them). The `@font-face` `url()` is written as a plain `url("~./zul/font/…")` with no DSP tag; `WcsExtendlet` encodes it when `zk.wcs` is served (context path included), so it resolves correctly inside the `zk.wcs` aggregate regardless of context path.

Rationale:
- **Self-host over CDN** — the CDN `@import` failed in air-gapped installs and leaked end-user IPs to Google (GDPR). Self-hosting is offline-safe and same-origin.
- **Self-host over system-font stack** — Marble is calibrated against the MUI v7 reference and has Playwright visual-regression tests; a system-font stack drifts per OS, destabilizing baselines and table layouts. A bundled font gives one reproducible look on every client and in CI.
- **Metric-matched fallback (`Inter Fallback`)** — ZK measures a widget once at mount and never again when a web font arrives, so a `sizedByContent` column was cut by an ellipsis whenever Inter loaded after ZK's first measurement (a 150 ms font delay is enough). `_fonts.css` defines a fallback face from the installed Arial (`size-adjust` 107.4% and matching line metrics) that sits right after `Inter` in `--zk-typescale-font-family`, so the measured width stays close to the final one. Measured on a one-listbox page: with a 150–1000 ms font delay 8/8 loads were cut without it and 0/8 with it; on a stress set of seven strings, 5 of 7 columns were cut without it and 1 of 7 (narrow glyphs) with it. `<link rel=preload>` (the font was fetched twice: the preload URL carries `;jsessionid`) and an inline data-URI font did not help. Weights 500, 600 and 700 (headers, cells) were not cut either with a 300 ms font delay, so the face is declared for the regular weight only. Where neither Arial nor Liberation Sans is installed the face is skipped and the stack behaves as before; if Inter never loads, text stays in the scaled Arial instead of the native UI font. ZK-4243 records the core position that waiting for web fonts is not ZK's job.
- **Inter over Roboto** (MD3's canonical face) — Marble's MUI-aligned reference uses Inter; Inter's tall x-height + open apertures also read better at the 13–14px dense-table sizes this theme targets. The fallback stack still degrades gracefully to each OS's native UI font if Inter never loads.

**Deprecated font library-properties — intentionally absent (do not re-add).** ZK's `org.zkoss.zul.theme.fontFamily*` and `org.zkoss.zul.theme.fontSize*` library properties have been **deprecated since ZK 7.0.0** (superseded by LESS, and now by CSS custom properties). Marble sets **zero** library properties of any kind — all typography is driven by the `--zk-typescale-*` tokens in `zul/css/tokens/_typography.css`. Their absence is a deliberate modern-CSS design choice, **not** a gap: do not introduce these properties to "configure" fonts. To change a face or size, edit the token, not a `<library-property>`. (Gap-review finding lane D / P3-1, 2026-06-26.)

---

## 8. State-Layer Overlays

Apply via a `::before` pseudo-element with `background: currentColor` and `opacity`:

| State | Opacity | Token |
|-------|---------|-------|
| Hover | `0.08` | `--zk-state-hover-opacity` |
| Focus | `0.12` | `--zk-state-focus-opacity` |
| Pressed | `0.12` | `--zk-state-pressed-opacity` |
| Dragged | `0.16` | `--zk-state-dragged-opacity` |
| Disabled content | `0.38` | `--zk-state-disabled-opacity` |
| Disabled container | `0.12` | `--zk-state-disabled-container-opacity` |

For surfaces (rows, list items) prefer a solid color: `background: #f5f5f5` or `rgba(0,0,0,0.04)` on hover.

---

## 9. Motion

Default interactive transition: `250ms cubic-bezier(0.4, 0, 0.2, 1)` (MUI standard).  
Tokens:
- Duration: `--zk-motion-duration-short3` (250ms) for most controls (background-color, box-shadow, border-color).
- Easing: `--zk-motion-easing-legacy` = `cubic-bezier(0.4, 0, 0.2, 1)`.

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

| Element | Value |
|---------|-------|
| Button height | ~35px (6px top/bottom padding) |
| Input / combobox height | ~39px inner; ~52px including floating label |
| Table header padding | `16px` (all sides) |
| Table cell padding | `16px` (all sides) |
| Nav item padding | `8px 24px` |
| Card content padding | `16px 16px 24px` |
| Topbar height | 64px |
| Page content padding | `48px` (all sides) |
| Table row height | ~52px rendered |

Rule: **table rows are ~52px** (16px padding all sides + 13px text + line-height). Inputs ~39px inner height. Buttons ~35px.

### Input width: datebox / timebox follow the `cols` API

datebox and timebox always render a `size` on the `<input>` (`Datebox` defaults `cols` to 11; `Timebox` derives it from the formatted value length), so Marble does **not** use `field-sizing: content` on them — it makes the browser ignore `size`, so an explicit `cols` would have no effect (ZK-6112). Width follows `cols`, as in IceBlue. A date format longer than ~11 characters is clipped unless the app raises `cols` (the core defect is tracked as ZK-6178). daterangebox and timepicker do not render a `size`, so they keep a content-hug policy.

| Field | Width behaviour |
|-------|-----------------|
| **datebox, timebox** | **follow `cols`** (rendered as `size`) + a `min-width` floor (`~6.5em` date, `~5em` time). |
| **daterangebox, timepicker** | **hug content** — `field-sizing: content` + a `min-width` floor, because ZK renders no (or a hardcoded) `size` for them. |
| **combobox, bandbox** (free-text) | keep the default / container-fill width. Content-hug would resize the box on every keystroke while typing — jarring. |
| **spinner, doublespinner** (numeric) | keep the default / container-fill width (numeric length is unbounded and typed). |

datebox/timebox use `flex: 1 1 auto` (basis = `size`, and they still **fill** an `hflex`/width-forced root — forms are unaffected); daterangebox uses `flex: 0 1 auto` because its two side-by-side inputs must not grow.

This is a deliberate divergence from MUI, whose single `OutlinedInput` fills its container (or the UA default) with left-aligned text and no content-hug. MUI has no opinion on a *standalone* date field's width; Marble sizes it to its content so a bare date/time field reads as one compact field rather than a fixed box with dead space, and long formats are never clipped. See contracts `datebox.md` / `timebox.md` / `daterangebox.md` and `doc/skill-gaps.md` (2026-07-20).

### Table-header background: no fill

Grid, Listbox, and Tree headers share **one rule**: **no background color**. Header cells inherit the body surface; visual hierarchy comes from three signals:

| Signal | Value |
|--------|-------|
| Font weight | 500 (label-large, `--zk-typescale-label-large-weight`) |
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

| Context | Border |
|---------|--------|
| Component default (outline) | `1px solid rgba(0,0,0,0.23)` = `--zk-color-outline` |
| Dividers / row separators | `1px solid rgba(0,0,0,0.12)` = `--zk-color-outline-variant` ≈ `#e0e0e0` |
| Very subtle row separator | `1px solid #f5f5f5` |
| Input focus | `2px solid #376fd0` (`--zk-color-primary`) |
| Input hover | `1px solid rgba(0,0,0,0.87)` (`--zk-color-on-surface`) |
| Card border | none (elevation only) |
| Outlined card | `1px solid outline-variant` + no shadow |

### Data-table frame (grid / listbox / tree)

These three are **outlined by default** (`1px solid outline-variant`, **no shadow**) — they
are assumed standalone, and on the near-white page the elevation shadow is invisible, so the
border is the only readable boundary. **Never border + shadow together.** When nested in a
bounded parent, strip the frame: automatically inside panel/groupbox
(`.z-panel-body .z-{comp}` / `.z-groupbox .z-{comp}` → `border: none`), or explicitly via the
opt-in variant sclass **`z-{grid,listbox,tree}-noborder`** (ZK emits no border attribute for
these, unlike `window`'s `z-window-noborder` — the variant name mirrors that ZK convention).
Decision ratified 2026-06-11 (outlined chosen over Mira's elevated because the near-white
`#f7f9fc` page makes the elevation shadow invisible; revisit if the surface palette darkens).

---

## 12. Iconography (Lucide SVG)

| Context | Size | Color |
|---------|------|-------|
| Inline / table / form | 16–18px | `--zk-color-on-surface-variant` |
| Nav item | 20px | `rgba(255,255,255,0.85)` (on blue) |
| Display / hero | 24px | `--zk-color-primary` |

Use Lucide icons via the `z-icon-*` CSS mask pattern already established in the theme.

### Header control icon buttons (close / maximize / minimize / collapse)

One family across every header strip that carries window-management controls (user ruling
2026-06-05 — goldenlayout had invented its own 20px text-glyph controls):

| Host | Button box | Icon size | Icons (Lucide) |
|------|------------|-----------|----------------|
| `window` (56px header) | 32×32 | 16px | `x` (close), `expand` (maximize), `compress` → `minimize-2` (restore), `minus` (minimize) |
| `panel` (48px header) | 28×28 | 14px | same set |
| `goldenlayout` `.lm_controls` (44px strip) | 28×28 (panel size — nearest header height) | 14px | `expand` (maximize), `x` (close) — the SAME icons panel's `z-icon-expand`/`z-icon-times` resolve to |

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

**Required CSS:**
```css
.z-button:has(br) {
    flex-direction: column;
    gap: 2px;
    padding: 8px 16px;
    min-height: 48px;
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
- The theme does **not** auto-centre a bare `<label>` — alignment is a container concern, never a `.z-label` property (a label must still baseline-align in running text and wrap as a caption). See skill `reference/inline-label-alignment.md`.
- The flex centre is **required only for selection controls** (radio / checkbox): a label beside a `textbox`/`combobox` already centres in a plain block row, so leave those alone. For a radio/checkbox row, wrap it: `<div sclass="z-d-flex z-items-center z-gap-3">` (verified 0px; `z-gap-*` sets the label↔field gap since flex collapses inter-element whitespace). `<hlayout valign="middle">` / `<hbox valign="middle">` are equivalent ZK-attribute alternatives. Applying the wrapper to an input row is harmless but unnecessary; for a column of mixed rows you may apply it uniformly for visual consistency.
- A bare `<div>` row is a **baseline context**: it coincidentally centres single-line inputs (textbox via `vertical-align:middle`, combobox via baseline-at-centre) but drops radio/checkbox labels ~4–5px (their baseline is the caption text, below the box centre). The default `<hlayout>` (`valign="top"`) puts every label ~10px high. Neither is a reliable form-row container — the divergence is correct CSS for the mode, not a theme defect.
- Live demo of both states: `zkthemelab/src/main/webapp/web/label.zul` (a "Default block row" group showing the radio/checkbox drop, and a "Correct" flex-centred group).

---

## 14. Splitter Family (unified spec — user ruling 2026-06-04)

One resize affordance across the app. Four implementations share this spec:
`splitter` (zul.box), `borderlayout` region splitters, `splitlayout` (zkmax), and
`goldenlayout` `.lm_splitter`.

**Implementation source (2026-06-06):** the canonical values below are defined ONCE as
`--zk-splitter-*` tokens in `zul/css/tokens/_splitter.css` (globally loaded via
`norm.css.dsp`). Component CSS must consume the tokens — never restate these values as
literals (gap log 2026-06-04 family fragmentation; 2026-06-06 cursor drift). The only
documented per-component exception is the GL transparent gutter; it stays local in that
component file with a comment citing this section. The **bar is borderless across the
whole family** — the `surface-container` fill is itself the divider; the former splitlayout
1px `outline-variant` bar border was retired as the lone outlier (gap log 2026-06-23, it
stacked into a double-line between splitlayout's bordered panes). **The pill does not resize
on hover** anywhere — MD3 communicates hover through the state-layer colour, not geometry;
borderlayout's former 28→44px hover growth was retired (gap log 2026-06-23) so all members
keep the 28px long-axis at every state (the caret already has its space at idle via
`opacity: 0`).

| Property | Canonical value |
|----------|-----------------|
| Bar thickness | **8px** (`--zk-spacing-2`) — both axes/orientations |
| Bar idle background | `--zk-color-surface-container` |
| Bar hover background | `color-mix(in srgb, var(--zk-color-primary) calc(var(--zk-state-hover-opacity) * 100%), var(--zk-color-surface-container))` |
| Bar drag/active background | same mix with `--zk-state-pressed-opacity` (12%) |
| Cursor | `col-resize` (vertical bar) / `row-resize` (horizontal bar) |
| Transition | `background-color var(--zk-motion-duration-short3) var(--zk-motion-easing-standard)` |
| Non-resizable (`*-nosplitter`) | cursor `default` AND hover keeps the idle background — no primary tint on a bar that cannot resize (false affordance; Gate-2 finding 2026-06-04). borderlayout is exempt: ZK hides the strip entirely when `splittable="false"` |

**Actuator pill** (the three ZK-mold splitters — `splitter`, `borderlayout`, `splitlayout` —
all render `<span>-button` + grip/caret/grip icons):

| Property | Canonical value |
|----------|-----------------|
| Pill size | cross-axis = bar thickness (8px), long-axis 28px, `--zk-shape-corner-full` |
| Pill idle | `--zk-color-outline-variant` fill, no border, no elevation |
| Pill hover/active | `--zk-color-primary` fill, icons `--zk-color-on-primary` |
| Grip icons | 8px, `--zk-color-on-surface-variant`, always visible (`opacity: 1`) |
| Collapse caret | hidden at idle (`opacity: 0`), fades in on hover |

Rationale: MD3 has no splitter; the spec composes the MD3 bottom-sheet drag handle
(slim inline pill) with a surface-tinted divider (originally designed for borderlayout —
that CSS's comment block is the source of record for the pill reasoning).

**GoldenLayout (revised 2026-06-05 — full family alignment; idle fill re-ruled
2026-06-06):** `.lm_splitter` is GoldenLayout-library-injected — no button element — but
that does NOT exempt it from the pill: GL has no `setBtnPos_` JS centering, so the actuator
pill is drawn entirely in CSS via `::before` (pill: bar-thickness × 28px, `outline-variant`,
`corner-full`) with grip dots via `::after` (8px Lucide `ellipsis-vertical`/`ellipsis` mask,
`on-surface-variant`, `opacity: 1`; hover → pill `primary`, dots `on-primary`). The earlier
"no pill, opacity-.5 dot marker" exception shipped an imperceptible affordance (user finding
2026-06-05) and is retired. GL's bundled CSS is never loaded in ZK, so the theme must also
set the family cursors (`col-resize` on `.lm_horizontal`, `row-resize` on `.lm_vertical`).
Remaining structural exception: no collapse caret (GoldenLayout has no collapse feature).

**GL idle-fill exception (user re-ruling 2026-06-06):** the family's `surface-container`
idle fill is REPLACED by `transparent` for GL only. The family fill exists to make the bar
visible *between surface panes*; GL bars abut `surface-container` `.lm_header` strips, so
the identical fill erased the very boundary it marks — bar + header read as one region
(worst on `lm_vertical`). GL panels are self-bordered cards on a transparent canvas: the
splitter region reads as a card *gutter*, with the pill + grips carrying the affordance.
Hover/drag tints become translucent: `color-mix(in srgb, var(--zk-color-primary)
<hover|pressed opacity>%, transparent)`. General principle for future splitter contexts:
**the bar idle fill must contrast with every surface it abuts** — when neighbors are
`surface-container`, the family fill is disqualified (contract outcome row M21 encodes
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
`.dsp` would never load (gap log 2026-06-30). Two render modes selected by
`data-embedscrollbar`: **overlay** (`false`, hover-only float, no reserved space) and
**embedded** (`true`, always-visible rail in a reserved gutter).

| Property | Value | Token |
|----------|-------|-------|
| Thumb / rail / embed corner radius | pill | `--zk-shape-corner-full` |
| Thumb idle background | `outline` @ opacity .6 | `--zk-color-outline` |
| Thumb hover background | `on-surface-variant` @ opacity .85 | `--zk-color-on-surface-variant` |
| **Track (rail) background** | **faint surface tint — a visible channel** | `--zk-color-surface-container` |
| Embed (idle) rail background | `rgba(0,0,0,0.12)` | `--zk-color-outline-variant` |
| Lane / track / thumb thickness | 12px lane · 8px track · 6px thumb (1px inset in track) | literal |
| Embed (idle) rail thickness | **8px = the hover *track* width** (not the 6px thumb) — same flush footprint as the track so rest→hover does not shift | literal |
| Cross-axis anchoring | track/thumb/arrows **edge-anchored** (vertical → `right`, horizontal → `bottom`); lane carries **no margin** → everything centres ~4px from the edge | literal |
| **Step buttons** (caret up/down/left/right) | **shown** — flat, neutral caret; faint state layer on hover. Along-axis 12px (read by `syncSize()`), cross-axis 8px and edge-anchored | `--zk-color-on-surface-variant` → `--zk-color-on-surface` / `--zk-color-surface-container-high` |
| Transition | `opacity` + `background-color`, short2 + standard easing | `--zk-motion-duration-short2`, `--zk-motion-easing-standard` |

**Distinct-from-native, but MD3 (user ruling 2026-06-30):** a `nativebar="false"` bar that
looks identical to native is pointless, so the simulated bar reads as a deliberate themed
control via (a) a **visible faint track channel** (the native overlay has none) and (b) a
**neutral, slightly bolder pill thumb** (idle `outline` → hover `on-surface-variant`, vs the
native bar's `outline-variant` → `outline`). The **caret step buttons are shown** (against
the MD3 default of hiding scrollbar arrows) per explicit user ruling — kept MD3-restrained:
flat, neutral, with a faint hover state layer, and they appear **only on hover** with the
rest of the bar (the whole `.z-scrollbar` is `display:none` at rest). ZK's `syncSize()` reads
the buttons' offset size to inset the wrapper, so the buttons must carry an explicit
width/height (12px). The `*-embed` rail carries **no** `:hover` rule — it is `display:none`
whenever the pointer is over the body (the full bar replaces it).

**Edge-hugging, no lateral jump (user ruling 2026-06-30, Option A; gap log 2026-06-30):** in
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
`.claude/skills/zk-component-rules/reference/selected-state-families.md`. Marble
follows the MD3 canonical treatment: the active marker is a **rounded tonal
container**, aligned with MD3 Navigation Drawer / MUI `ListItemButton`.

| Property | Value | Token |
|----------|-------|-------|
| Selected item background | 12% primary tint container | `color-mix(in srgb, var(--zk-color-primary) 12%, transparent)` |
| Selected item text | primary | `--zk-color-primary` |
| Selected item weight | 600 | literal |
| Content-link corner radius | 8px (the tint container is rounded) | `--zk-shape-corner-small` |

**No left-edge accent bar.** The rounded tonal container is the *whole* active
marker. An earlier iteration added a left bar — first as a `border-left` (which
curled into an arc on the rounded, `overflow:hidden` content link), then as a
straight inset `::after` strip. On design review (2026-07-07) the bar was dropped
entirely: stacking a classic/enterprise-sidebar accent on the MD3 pill states
"active" twice in two idioms and clashes where the square bar meets the container's
rounded corners. Do not re-introduce any left accent (`border-left`, `::before`, or
`::after` strip). See `doc/skill-gaps.md`.

**Navigation-active ≠ list-row focus.** The `.z-listitem` blue *left line* is
NOT a selected-state accent — the selected state is a `secondary-container` fill
(list-row family, per the selected-state-families skill). That left line is the listbox
**focus** indicator (`box-shadow: inset 3px 0 0 var(--zk-color-primary)` on the
first cell), a separate affordance that merely resembles the navbar accent. They
are intentionally different; do not unify them.
