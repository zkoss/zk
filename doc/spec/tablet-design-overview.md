# Tablet Design Overview (Marble theme)

Source: `../zkcml/zkmax/src/main/resources/web/zkmax/css/tablet/_*.css`
Build: concatenated into `zkmax/css/tablet.css.dsp` (see `scripts/build-css.js`, `buildTabletSheet()`).

## How it loads — the core mechanism

The tablet bundle is **not** a media query. ZK EE (`TabletThemeURIHandler`) injects a
`<link href=…zkmax/css/tablet.css.dsp … disabled>` at cascade position 1 (after
`norm.css`) on **every** request — but it ships **disabled**. The ZK client runtime
sets `zk.mobile` from the request User-Agent and, on `DOMContentLoaded`, **enables**
the disabled link only when `zk.mobile` is true. So the switch is client-side but
keyed on UA; because the sheet loads after `norm.css`, its rules win on equal specificity.

| Context | `tablet.css.dsp` link | Rules applied |
|---------|-----------------------|---------------|
| **Desktop browser** | present but `disabled` (never loaded) | Base Marble theme only (`norm.css` + component `*.css.dsp`) |
| **Mobile / tablet browser** | enabled on `DOMContentLoaded` | Base Marble theme **+** `tablet.css.dsp` overrides |

Consequences:
- On a desktop browser the link stays disabled, so its rules are never applied and the
  sheet never even appears in `document.styleSheets`. Switching is **UA-based, not
  width-based**: resizing a desktop window narrow does **not** trigger tablet styling.
- The bundle is **geometry only** — it enlarges hit areas, glyphs and row heights.
  Palette, shape and typography tokens keep coming from the base `--zk-color-*` /
  `--zk-shape-*` tokens. No hardcoded hex anywhere.
- The design target is MD3 touch ergonomics: the minimum hit area
  (`--zk-touch-target-min`), the comfortable hit area (`--zk-touch-target-comfortable`)
  and the control glyph size (`--zk-touch-icon-size`).

## Touch tokens and per-component sizes

The touch tokens (`--zk-touch-*`) are declared in `_tokens.css` (the first partial) on top of the
regular `--zk-*` tokens, and drive every other partial. The per-component desktop → mobile sizes
(buttons, text inputs and the combo family, selection controls, calendar popup, window/panel,
listbox/grid/tree and paging, biglistbox scrollbar) are in the matching `_*.css` partial.
Notes (not in the partials):

- **Selection controls** (checkbox / radio / switch): the **glyph keeps its MD3 size**; what grows is the clickable row and the ripple state-layer, so the finger target fills `--zk-touch-target-comfortable`.
- **Window / panel**: close-affordance **placement is unchanged** (see `doc/spec/md3-close-affordance-placement.md`); only sizes grow.
- **Scrollbars**: Marble only themes one scrollbar on tablet, the biglistbox custom `WScroll` widget. Native scrollbars are left to the platform (touch overlay scrollbars), matching stock ZK.

---

## Touch geometry principles

How Marble matches the ZK default theme's mobile design — the *intent*, not the pixels:

- **(a) Match intent via touch tokens, not by copying pixels.** Reach the default
  theme's touch-target intent through Marble's own `--zk-touch-*` tokens
  (`--zk-touch-target-min` 44px, `--zk-touch-target-comfortable` 48px,
  `--zk-touch-icon-size` 24px, `--zk-touch-row-min-height` 48px,
  `--zk-touch-scrollbar-size` 16px, `--zk-touch-slider-knob` 28px). MD3's minimum
  touch target (48dp / the `--zk-touch-target-min` value) is **stricter** than the default theme's 38px, so
  Marble is not a copy — it applies the MD3 value through its token layer.
- **(b) Never copy the default theme's mobile font-size bumps.** The default theme
  scales its ~13px desktop type up to 15/17px for phone legibility. Marble's body text
  comes from `--zk-typescale-body-medium-size`, part of its tuned type scale, so these
  bumps are **not** ported — copying them would fight that scale. Only bump a
  font where Marble's mobile text is genuinely too small (none found).
- **(c) The borderlayout splitter-pill cross-axis size is an invariant.** The pill's
  cross-axis is locked to the bar thickness (`--zk-splitter-thickness`, the bar height).
  Do **not** force it to `--zk-touch-target-min` — that breaks the JS margin-based centering. The whole splitter
  strip is already the generous hover/activation zone.

## What is deliberately NOT changed
- **Palette, shape, typography** — inherited from base tokens; tablet bundle is geometry-only.
- **Close/dismiss affordance placement** — unchanged per the close-affordance doc.
- **Native scrollbars** — left to the OS touch-overlay behavior.
- **Glyph sizes inside checkboxes/radios** — only their hit area/ripple grows.
- **Width-based responsiveness** — there is none; switching is purely UA-driven.
