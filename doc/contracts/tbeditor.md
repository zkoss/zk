# Component: tbeditor (theme design)
tier: T3
category: input
preview: ${PREVIEW_URL}/tbeditor.zul
rules: see .claude/skills/zk-component-rules/components/tbeditor.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zkmax/tbeditor/Tbeditor.ts
js-source-hash: 9e60a472d1ffaff6abf8295958ed75806a67eac125d10c8a2696f4e12b6eec36
closest-sibling: window (outer chrome), toolbar (button pane)

## References
- MUI CSS: no analog — tbeditor wraps Trumbowyg, a third-party rich-text library; no MUI RichTextEditor component exists. Consult MUI `OutlinedInput.css` for the outer border/shape and `Toolbar.css` for the button strip background and button hover state.
- DESIGN.md sections: §3 (color roles — surface, outline, primary), §4 (typography scale — body-medium for editor content), §5 (spacing — button padding), §6 (shape — corner-small for outer box), §7 (motion — button hover transitions)
- Iceblue baseline: doc/contracts/baselines/tbeditor-iceblue.png
- HTML contract: doc/contracts/tbeditor.html

## Design Contract

The tbeditor renders as a single bordered box with a light surface-variant toolbar strip across the top and a white editing canvas below. The outer `.z-tbeditor-box` gets a 1px `outline`-colored border, `corner-small` radius (8px), and `elevation-1` shadow to differentiate it from the surrounding page surface — matching the weight of an outlined `<textbox>`. The button pane (`.z-tbeditor-button-pane`) uses `surface-container` as its background (one tone darker than the editor canvas) with a 1px `outline-variant` bottom separator, giving a clear chrome-vs-content division without heavy contrast. Individual toolbar `<button>` elements are 35×35px touch targets with no default background; on hover they receive a `state-hover-opacity` overlay against `primary` to match the toolbarbutton hover convention. The content area (`.z-tbeditor-editor`) is `surface` white with `on-surface` text color and `body-medium` typography. When the editor is in the disabled state (`.z-tbeditor-disabled`), the button pane icons drop to `disabled` opacity (0.38) and the border shifts to `outline-variant`. Fullscreen mode expands the box to cover the viewport with `elevation-3`; the theme does not need to manage the `position:fixed` rule — Trumbowyg's own CSS handles geometry. Transitions on button hover use `short2` duration with `standard` easing.

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| box-1 | `.z-tbeditor-box` | border | `1px solid var(--zk-color-outline)` | DESIGN.md §3 |
| box-2 | `.z-tbeditor-box` | border-radius | `var(--zk-shape-corner-small)` | DESIGN.md §6 |
| box-3 | `.z-tbeditor-box` | overflow | `hidden` | structural (clips child to border-radius; safe — only root is clipped, not cnt) |
| box-4 | `.z-tbeditor-box` | box-shadow | `var(--zk-elevation-1)` | DESIGN.md §6 (separates editor from page surface) |
| box-5 | `.z-tbeditor-box` | background-color | `var(--zk-color-surface)` | DESIGN.md §3 |
| box-6 | `.z-tbeditor-box` | min-height | `300px` | Trumbowyg default — maintain to avoid collapsing when no value is set |
| pane-1 | `.z-tbeditor-button-pane` | background-color | `var(--zk-color-surface-container)` | DESIGN.md §3 |
| pane-2 | `.z-tbeditor-button-pane` | border-bottom | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §3 |
| pane-3 | `.z-tbeditor-button-pane` | padding | `0 var(--zk-spacing-1)` | DESIGN.md §5 (preserve 5px horizontal padding — toolbar buttons must not clip) |
| pane-4 | `.z-tbeditor-button-pane` | min-height | `36px` | structural — JS measures this height in _calcEditorHeight |
| pane-5 | `.z-tbeditor-button-pane` | list-style | `none` | structural (it is a `<ul>`) |
| pane-6 | `.z-tbeditor-button-pane` | margin | `0` | structural |
| btn-1 | `.z-tbeditor-button-pane button` | width | `35px` | Trumbowyg spec — do not shrink |
| btn-2 | `.z-tbeditor-button-pane button` | height | `35px` | Trumbowyg spec |
| btn-3 | `.z-tbeditor-button-pane button` | background | `transparent` | DESIGN.md §3 (default state: no fill) |
| btn-4 | `.z-tbeditor-button-pane button` | border | `none` | DESIGN.md §3 |
| btn-5 | `.z-tbeditor-button-pane button` | cursor | `pointer` | usability |
| btn-6 | `.z-tbeditor-button-pane button` | border-radius | `var(--zk-shape-corner-small)` | DESIGN.md §6 |
| btn-7 | `.z-tbeditor-button-pane button` | transition | `background-color var(--zk-motion-duration-short2) var(--zk-motion-easing-standard)` | DESIGN.md §7 |
| btn-8 | `.z-tbeditor-button-pane button:hover, .z-tbeditor-button-pane button:focus` | background-color | `color-mix(in srgb, var(--zk-color-primary) calc(var(--zk-state-hover-opacity) * 100%), transparent)` | DESIGN.md §3/§7 |
| btn-9 | `.z-tbeditor-button-pane button.z-tbeditor-active` | background-color | `var(--zk-color-primary-container)` | DESIGN.md §3 (active/toggled formatting command) |
| btn-10 | `.z-tbeditor-button-pane svg` | fill | `var(--zk-color-on-surface-variant)` | DESIGN.md §3 |
| btn-11 | `.z-tbeditor-button-pane button:hover svg, .z-tbeditor-button-pane button.z-tbeditor-active svg` | fill | `var(--zk-color-primary)` | DESIGN.md §3 |
| sep-1 | `.z-tbeditor-button-group:not(:empty) + .z-tbeditor-button-group::before` | background-color | `var(--zk-color-outline-variant)` | DESIGN.md §3 (group separator line) |
| editor-1 | `.z-tbeditor-editor` | background-color | `var(--zk-color-surface)` | DESIGN.md §3 |
| editor-2 | `.z-tbeditor-editor` | color | `var(--zk-color-on-surface)` | DESIGN.md §3 |
| editor-3 | `.z-tbeditor-editor` | font-size | `var(--zk-typescale-body-medium-size)` | DESIGN.md §4 |
| editor-4 | `.z-tbeditor-editor` | padding | `var(--zk-spacing-4)` | DESIGN.md §5 |
| editor-5 | `.z-tbeditor-editor[contenteditable=true]:empty:not(:focus)::before` | color | `var(--zk-color-disabled)` | DESIGN.md §3 (placeholder text) |
| dis-1 | `.z-tbeditor-disabled .z-tbeditor-button-pane button:not(.z-tbeditor-not-disable)` | opacity | `0.38` | DESIGN.md §3 (`--zk-color-disabled` uses 0.38 alpha; match for icons) |
| dis-2 | `.z-tbeditor-disabled .z-tbeditor-button-pane button:not(.z-tbeditor-not-disable)` | cursor | `default` | usability |
| dis-3 | `.z-tbeditor-disabled .z-tbeditor-box` | border-color | `var(--zk-color-outline-variant)` | DESIGN.md §3 |
| full-1 | `.z-tbeditor-fullscreen` | box-shadow | `var(--zk-elevation-3)` | DESIGN.md §6 |
| drop-1 | `.z-tbeditor-dropdown` | background-color | `var(--zk-color-surface)` | DESIGN.md §3 |
| drop-2 | `.z-tbeditor-dropdown` | border | `1px solid var(--zk-color-outline-variant)` | DESIGN.md §3 |
| drop-3 | `.z-tbeditor-dropdown` | border-radius | `var(--zk-shape-corner-small)` | DESIGN.md §6 |
| drop-4 | `.z-tbeditor-dropdown` | box-shadow | `var(--zk-elevation-2)` | DESIGN.md §6 |
| drop-5 | `.z-tbeditor-dropdown button:hover, .z-tbeditor-dropdown button:focus` | background-color | `var(--zk-color-surface-container)` | DESIGN.md §3 |
| drop-6 | `.z-tbeditor-dropdown button` | color | `var(--zk-color-on-surface)` | DESIGN.md §3 |

## State matrix

| state | selector | property ids |
|-------|----------|--------------|
| box default | `.z-tbeditor-box` | box-1, box-2, box-3, box-4, box-5, box-6 |
| button pane | `.z-tbeditor-button-pane` | pane-1, pane-2, pane-3, pane-4, pane-5, pane-6 |
| toolbar button default | `.z-tbeditor-button-pane button` | btn-1, btn-2, btn-3, btn-4, btn-5, btn-6, btn-7, btn-10 |
| toolbar button hover/focus | `.z-tbeditor-button-pane button:hover` | btn-8, btn-11 |
| toolbar button active (toggled) | `.z-tbeditor-button-pane button.z-tbeditor-active` | btn-9, btn-11 |
| group separator | `.z-tbeditor-button-group::before` | sep-1 |
| editor canvas | `.z-tbeditor-editor` | editor-1, editor-2, editor-3, editor-4 |
| editor placeholder | `.z-tbeditor-editor[contenteditable=true]:empty:not(:focus)::before` | editor-5 |
| fullscreen | `.z-tbeditor-fullscreen` | full-1 |
| dropdown default | `.z-tbeditor-dropdown` | drop-1, drop-2, drop-3, drop-4, drop-6 |
| dropdown hover | `.z-tbeditor-dropdown button:hover` | drop-5 |

## States to evaluate
- [ ] default (box border, toolbar background, editor canvas color)
- [ ] toolbar button hover (primary-tinted background layer)
- [ ] toolbar button active/toggled (bold, italic, etc. pressed)
- [ ] HTML-source mode (editor hidden, textarea shown — `.z-tbeditor-editor-hidden`)

> Not evaluatable: the **disabled** state is **unreachable from ZUL** — `Tbeditor` (zkmax) extends `XulElement` and exposes only `setValue`/`setConfig` (no `setDisabled`), so `disabled="true"` throws an HTTP 500 at compose time. The `dis-1`/`dis-2`/`dis-3` CSS rows above are retained for reference only.
- [ ] fullscreen (`.z-tbeditor-fullscreen` — elevation-3 shadow, no border)
- [ ] dropdown open (`.z-tbeditor-dropdown` visible below a button)

## T3 wrapper boundary

tbeditor is T3 because it wraps the Trumbowyg third-party library. ZK only owns `.z-tbeditor` (the root `<div>`) and `#uuid-cnt` (the container passed to `$.trumbowyg()`). Everything inside `.z-tbeditor-box` is Trumbowyg-injected DOM. Because Trumbowyg uses `config.prefix = 'z-tbeditor-'`, all internal class names are formally namespaced — they are CSS-reachable and must be restyled entirely (the library's default CSS will be replaced by `tbeditor.css.dsp`).

```yaml
wrapper-selectors:
  - .z-tbeditor
  - .z-tbeditor-box
  - .z-tbeditor-button-pane
  - .z-tbeditor-editor
forbidden-selectors:
  - "[id$='-cnt'] > *:not(.z-tbeditor-box)"   # internal Trumbowyg markup outside the box — rare edge cases
theme-bridge:
  # No CSS variable bridge needed — theme styles the Trumbowyg-injected DOM directly
  # because all class names are z-tbeditor-* prefixed and fully under our control.
```
