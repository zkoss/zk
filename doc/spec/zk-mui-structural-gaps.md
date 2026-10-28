# ZK vs MUI — Structural Gaps (unresolvable by CSS alone)

Marble is visually aligned with MUI (React Material UI). Some MUI appearances/behaviors **cannot be reproduced** in a ZK theme by CSS alone, because they stem from fundamental framework differences (DOM shape, React-only mechanics, or missing ZK components). These are expected limits — do **not** re-attempt them as theme bugs.

> Salvaged from the retired Mira visual-parity effort (`doc/mira-reports/framework-gaps.md (retired)`). The Mira dashboard was Marble's original concrete reference; the design authority is now MD3 + the MUI v7 static-CSS reference (`…/material-ui-7.3.1/static-css-output/`). The structural facts below are reference-independent.

---

## Component-specific gaps

### radio / radiogroup — label-placement grid
- **Gap**: MUI positions the label Top/Start/Bottom/End via the `labelPlacement` prop. ZK `<radiogroup>` renders radios inline; custom label positioning requires custom DOM.
- **Workaround**: A 2×2 CSS grid with separate label + radio elements approximates the layout.

### combobox / selectbox — multiple select
- **Gap**: MUI `<Select multiple>` allows simultaneous multi-value selection with chip rendering. ZK `<combobox>` is single-selection only — no equivalent component.
- **Best effort**: Retain placeholder outlined comboboxes for visual structure; do not attempt to match multi-select behavior.
- **Note**: The *standard/underline* select variant IS achievable via CSS (`z-combobox-standard`, bottom-border-only) — that was a CSS gap, not a framework gap. Only multi-select is a true framework gap.

### textbox / datebox / timebox — floating label inside border
- **Gap**: MUI's outlined input floats the label inside the top border (CSS transform on a real `<label>` sibling). ZK's input DOM (`<input>` only, no paired label element) cannot support a floating label without wrapping in a custom component.
- **Workaround**: Place the label above the input as a separate element.

### calendar — today vs selected indicator
- **Gap**: MUI's date calendar marks *today* with an independent class (`MuiPickersDay-today`), so today can show an outline ring while the selected date shows a filled circle. ZK's `<calendar>` only has `z-calendar-selected` (used for both "today is the default" and "user selected this date"); it never emits a separate today-marker class or `data-*` attribute.
- **Root cause**: `Calendar.ts` only calls `$cell.addClass('z-calendar-selected')`; there is no today class.
- **Workaround**: A `.z-calendar-cell.z-today` rule is kept as a forward-looking stub but currently has no effect. Full support needs a JS patch injecting `z-today` on the matching `aria-label` cell — beyond pure CSS.

### calendar — header arrow layout
- **Gap**: MUI renders `"May 2026 ▾"` (dropdown trigger) left-aligned with `< >` nav arrows right-aligned. ZK `<calendar>` always renders `< title >` with arrows flanking the title (`z-calendar-left | z-calendar-title | z-calendar-right`).
- **Workaround**: The arrow-flanked layout is functionally equivalent; accept the structural difference.

### rich-text editor (tbeditor) — live WYSIWYG
- **Gap**: MUI reference dashboards embed a real Quill.js WYSIWYG editor; ZK has no built-in Quill integration.
- **Workaround**: A simulated toolbar (combobox + borderless icon buttons) over a plain `<textbox multiline>`; appearance matches, interactivity is static.

---

## General structural differences (reference)

| Category | Root cause |
|---|---|
| Component DOM wrapper | ZK wraps every component in extra `<span>/<div>` carrying a `z-*` class |
| Form input internals | ZK textbox/combobox = `<span>` outer + `<input>` inner; MUI = single `<input>` + floating label |
| Button ripple | MUI has a Material ink ripple on click; no pure-CSS equivalent in ZK |
| SVG vs font icons | MUI uses Lucide SVG; ZK uses icon-font glyphs — shapes differ by design |
| Image wrapper | ZK `<image>` renders `<span class="z-image"><img/></span>` — use `<html>` CDATA for edge-to-edge images |
| Transitions | MUI uses JS-driven mount/unmount transitions; ZK supports CSS transitions only |
| Portal dropdowns | MUI renders dropdowns via a React Portal (outside the DOM tree); ZK uses its popup mechanism |
