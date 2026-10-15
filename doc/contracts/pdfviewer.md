# Component: pdfviewer (theme design)
tier: T3
category: media
preview: ${PREVIEW_URL}/pdfviewer.zul
rules: see .claude/skills/zk-component-rules/components/pdfviewer.md
contract-approved: true
zk-version: 10.2.1-jakarta
js-source-files:
  - zkmax/wgt/Pdfviewer.ts is not the source — see zkex path below
  - zkex/pdfviewer/Pdfviewer.ts
  - zkex/pdfviewer/mold/pdfviewer.js
js-source-hash: d649527ad8874a916b02c116bb589684f247fe89cd93f52cc233622f7c613754
js-source-hash-note: hash of Pdfviewer.ts only (mold/pdfviewer.js is a build artifact); recompute with `shasum -a 256 /Users/hawk/Documents/workspace/ZK10/zkcml/zkex/src/main/resources/web/js/zkex/pdfviewer/Pdfviewer.ts`
closest-sibling: window

wrapper-selectors:
  - .z-pdfviewer
  - .z-pdfviewer-container
  - .z-pdfviewer-toolbar
  - .z-pdfviewer-toolbar-button
  - .z-pdfviewer-toolbar-separator
  - .z-pdfviewer-toolbar-page-active
  - .z-pdfviewer-toolbar-zoom
  - .z-pdfviewer-text-layer
forbidden-selectors:
  - canvas                    # PDF.js raw pixel output — do not style
  - .pdfViewer               # PDF.js internal class — not emitted by ZK
  - .page                    # PDF.js internal class — not emitted by ZK
  - .textLayer               # PDF.js internal class — not emitted by ZK
  - .annotationLayer         # PDF.js internal class — not emitted by ZK
  - "#viewerContainer"       # PDF.js viewer.html ID — not emitted by ZK
  - "#viewer"                # PDF.js viewer.html ID — not emitted by ZK
  - "#toolbarContainer"      # PDF.js viewer.html ID — not emitted by ZK
theme-bridge:
  note: >
    The bundled PDF.js version does NOT expose stable CSS custom properties for
    toolbar recolouring. Do not attempt to inject --toolbar-bg-color or similar.
    Wrapper-only styling is the correct approach for this ZK version.
    If a future ZK upgrade bundles a PDF.js version that exposes CSS variables,
    re-evaluate and update this block.

## References
- MUI CSS: no analog — novel ZK component (PDF viewer widget has no MUI equivalent)
- DESIGN.md sections: §1 (surface tokens), §5 (shape/radius), §6 (elevation), §11 (outline/border)
- Iceblue baseline: doc/contracts/baselines/pdfviewer-iceblue.png
- HTML contract: doc/contracts/pdfviewer.html

## Design Contract

The pdfviewer widget receives a thin Material Design 3 chrome: a 1px `outline-variant` border with `shape-corner-medium` radius on the root container, with `overflow: hidden` so the radius clips the inner viewport cleanly. The floating toolbar is a pill-shaped surface (background: `surface-container-high`, radius: `shape-corner-full`) that appears centered at the bottom of the viewer on hover or focus, fading from hidden (opacity 0) to semi-visible (0.5) on widget hover and to fully visible (1.0) on toolbar hover or keyboard focus-within. Toolbar icon buttons use the standard icon-button pattern: transparent background at rest, 8% primary state-layer on hover, 12% on press, with `shape-corner-small` radius. The page-active input and zoom select inherit the textbox/selectbox base styles with a narrowed width override. The only PDF.js internals styled are the text layer and its spans (`.z-pdfviewer-text-layer`, `> span`: transparent text, absolute, `white-space: pre`, `::selection` in `primary`), which must stay invisible text over the canvas.

## Expected values

| id | selector | property | expected (token preferred) | source |
|----|----------|----------|----------------------------|--------|
| c1 | `.z-pdfviewer` | border | 1px solid `var(--zk-color-outline-variant)` | DESIGN.md §11 |
| c2 | `.z-pdfviewer` | border-radius | `var(--zk-shape-corner-medium)` | DESIGN.md §5 |
| c3 | `.z-pdfviewer` | overflow | hidden | composition invariant |
| c4 | `.z-pdfviewer` | background-color | `var(--zk-color-surface)` | DESIGN.md §1 |
| c5 | `.z-pdfviewer-container` | background-color | `var(--zk-color-surface-variant)` | DESIGN.md §1 (surface-variant distinguishes the scrollable page canvas from the viewer chrome) |
| c6 | `.z-pdfviewer-page` | box-shadow | `var(--zk-elevation-2)` | DESIGN.md §6 (page drop shadow) |
| c7 | `.z-pdfviewer-toolbar` | background-color | `var(--zk-color-surface-container-high)` | DESIGN.md §1 |
| c8 | `.z-pdfviewer-toolbar` | border-radius | `var(--zk-shape-corner-full)` | pill shape per MD3 nav-bar pattern |
| c9 | `.z-pdfviewer-toolbar` | opacity (default) | 0 | composition invariant — toolbar hidden until interaction (see skill §State classes) |
| c10 | `.z-pdfviewer:hover .z-pdfviewer-toolbar` | opacity | 0.5 | progressive reveal — partial opacity signals interactive zone without committing to full focus |
| c11 | `.z-pdfviewer:hover .z-pdfviewer-toolbar:hover` | opacity | 1 | semantic maximum — fully visible on direct toolbar interaction |
| c12 | `.z-pdfviewer:focus-within .z-pdfviewer-toolbar` | opacity | 1 | accessibility — fully visible to keyboard users entering the toolbar |
| c13 | `.z-pdfviewer-toolbar` | padding | `var(--zk-spacing-2)` `var(--zk-spacing-3)` | DESIGN.md §5 |
| c14 | `.z-pdfviewer-toolbar` | box-shadow | `var(--zk-elevation-3)` | DESIGN.md §6 (floating toolbar) |
| c15 | `.z-pdfviewer-toolbar-button` | border-radius | `var(--zk-shape-corner-small)` | DESIGN.md §5 |
| c16 | `.z-pdfviewer-toolbar-button` | color | `var(--zk-color-on-surface)` | DESIGN.md §2 |
| c17 | `.z-pdfviewer-toolbar-button` | background-color | transparent | icon-button at rest |
| c18 | `.z-pdfviewer-toolbar-button:hover` | background-color | `color-mix(in srgb, var(--zk-color-on-surface) calc(var(--zk-state-hover-opacity) * 100%), transparent)` | MD3 state layer |
| c19 | `.z-pdfviewer-toolbar-button:active` | background-color | `color-mix(in srgb, var(--zk-color-on-surface) calc(var(--zk-state-pressed-opacity) * 100%), transparent)` | MD3 state layer |
| c20 | `.z-pdfviewer-toolbar-button[disabled]` | color | `var(--zk-color-disabled)` | DESIGN.md §8 |
| c21 | `.z-pdfviewer-toolbar-separator` | border-left | 1px solid `var(--zk-color-outline-variant)` | DESIGN.md §11 |
| c22 | `.z-pdfviewer:fullscreen` | border-radius | 0 | fullscreen removes radius |
| c23 | `.z-pdfviewer-text-layer` | opacity | 0.2 | structural — must be preserved |

## State matrix

| state | selector | properties to check |
|-------|----------|---------------------|
| default (toolbar hidden) | `.z-pdfviewer` | c1, c2, c3, c4 |
| container background | `.z-pdfviewer-container` | c5 |
| page shadow | `.z-pdfviewer-page` | c6 |
| toolbar default (hidden) | `.z-pdfviewer-toolbar` | c7, c8, c9, c13, c14 |
| toolbar on widget hover | `.z-pdfviewer:hover .z-pdfviewer-toolbar` | c10 |
| toolbar on toolbar hover | `.z-pdfviewer:hover .z-pdfviewer-toolbar:hover` | c11 |
| toolbar on focus-within | `.z-pdfviewer:focus-within .z-pdfviewer-toolbar` | c12 |
| button default | `.z-pdfviewer-toolbar-button` | c15, c16, c17 |
| button hover | `.z-pdfviewer-toolbar-button:hover` | c18 |
| button active | `.z-pdfviewer-toolbar-button:active` | c19 |
| button disabled | `.z-pdfviewer-toolbar-button[disabled]` | c20 |
| separator | `.z-pdfviewer-toolbar-separator` | c21 |
| fullscreen | `.z-pdfviewer:fullscreen` | c22 |

## States to evaluate
- [ ] default rendered (PDF loaded, toolbar opacity=0)
- [ ] toolbar appears on hover (opacity=0.5 → 1)
- [ ] toolbar fully visible on focus-within (keyboard navigation)
- [ ] toolbar navigation buttons disabled at page boundary (button[disabled])
- [ ] fullscreen mode (border-radius collapses, fullscreen icon swaps)
- [ ] PDF load pending / empty (no special ZK class — verify wrapper chrome only)
