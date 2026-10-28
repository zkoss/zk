# tabbox

A tabbed container with `<tabs>` (the tab strip) and `<tabpanels>` (the content panels). Optionally wraps an additional `<toolbar>` row. Renders in five orientation modes plus an "accordion" mold that replaces the strip with stacked collapsible headers.

## Five orientations

The orientation class is on the tabbox root:

- `.z-tabbox-top` (default — tabs above panels)
- `.z-tabbox-bottom` (tabs below panels)
- `.z-tabbox-left` (tabs on the left, panels on the right)
- `.z-tabbox-right` (tabs on the right, panels on the left)
- `.z-tabbox-accordion` (tabs replaced by collapsible headers; each panel renders inline below its tab — chosen by `mold="accordion"`, NOT by `orient`)

The orientation class is added by `Tabbox.domClass_()`: accordion when `inAccordionMold()`, otherwise `this.$s(orient)` where `orient` ∈ `top|bottom|left|right` (the legacy values `horizontal`/`vertical` are normalised to `top`/`left` in `setOrient`).

## DOM structure (horizontal modes)

```
.z-tabbox[.z-tabbox-{top|bottom}]
├─ .z-tabs                                       (the tab strip; `<div>`)
│   └─ ul.z-tabs-content                         (the tab list; id="{uuid}-cave")
│       └─ li.z-tab[.z-tab-selected][.z-tab-disabled]
│           └─ div.z-tab-content                 (inner content row; id="{tab-uuid}-cave")
│               ├─ div.z-tab-button              (close button — only when `closable="true"`; ZK emits it FIRST, before the label — see Notes)
│               ├─ i.z-tab-icon                  (only when `iconSclass` set; class is iconSclass)
│               └─ span.z-tab-text               (label; id="{tab-uuid}-cnt")
│                   └─ img.z-tab-image           (only when `image` set — nested INSIDE the text span, class z-tab-image not z-tab-icon)
├─ div.z-tabbox-left-scroll                      (scroll buttons — see Scroll buttons section)
├─ div.z-tabbox-right-scroll
├─ .z-toolbar                                    (only when `<toolbar>` child exists)
└─ .z-tabpanels                                  (the panel container)
    └─ .z-tabpanel[.z-tabpanel-selected]
```

For `.z-tabbox-bottom`, the mold emits children in this order: `<tabpanels>`, `<tabs>`, scroll buttons, `<toolbar>`. For `.z-tabbox-top`, the order is `<tabs>`, scroll buttons, `<toolbar>`, `<tabpanels>`.

## DOM structure (vertical modes)

```
.z-tabbox[.z-tabbox-{left|right}]
├─ .z-tabs
│   └─ ul.z-tabs-content
│       └─ li.z-tab[…]                           (same inner structure as horizontal)
├─ .z-tabpanels
│   └─ .z-tabpanel[…]
├─ div.z-tabbox-up-scroll                        (scroll buttons — vertical orient only)
├─ div.z-tabbox-down-scroll
└─ div.z-clear                                   (clearfix div, ZK-injected)
```

Vertical modes do NOT render a `<toolbar>` — `TabboxSkipper` excludes the toolbar child when `mold !== 'default'` and the mold-emit code only outputs the toolbar inside the horizontal branch.

## DOM structure (accordion mold)

In accordion mode the outer container still renders a `.z-tabpanels` wrapper, but `.z-tabs` is NOT rendered. Each section is a `.z-tabpanel` that wraps one `.z-tab` (the accordion header) and one expandable area:

```
.z-tabbox.z-tabbox-accordion
└─ .z-tabpanels
    ├─ .z-tabpanel                              (section wrapper — class is z-tabpanel only)
    │   ├─ div.z-tab[.z-tab-selected][.z-tab-disabled]   (accordion header — rendered as <div>, NOT <li>)
    │   │   └─ div.z-tab-content                (id="{tab-uuid}-cave")
    │   │       ├─ div.z-tab-button             (close button — visible when closable="true", same as strip mode)
    │   │       ├─ img.z-tab-icon               (only when `image` set)
    │   │       ├─ i.z-tab-icon                 (only when `iconSclass` set)
    │   │       └─ span.z-tab-text              (id="{tab-uuid}-cnt")
    │   └─ div.z-tabpanel-content               (expandable area; id="{tabpanel-uuid}-cave"; display toggled by JS)
    │       └─ (panel child content)
    ├─ .z-tabpanel
    └─ ...
```

**Critical class correction (live render vs ZK source):** `Tabpanel.domClass_()` source appends `$s('content')` in accordion mode, which suggests `.z-tabpanel.z-tabpanel-content` on the section wrapper. However, the **live rendered DOM** shows the section wrapper has class `z-tabpanel` only. The `z-tabpanel-content` class is on the separate expandable div (`div#{tabpanel-uuid}-cave`). Target `.z-tabpanel-content` for accordion panel content, NOT `.z-tabpanel` in this context.

When styling, check the orientation class first — many selectors differ between strip-mode and accordion-mode.

## State classes

On `.z-tab`:
- `.z-tab-selected` — added by ZK when the tab is the active/selected tab
- `.z-tab-disabled` — added when `disabled="true"` on the `<tab>` element

On `.z-tabbox` root:
- `.z-tabbox-scroll` — added/removed by `Tabs._showbutton()` when the cumulative tab width/height exceeds the strip's available width/height (i.e. overflow exists and `tabscroll` is not false). This is the ONLY signal CSS receives that scroll mode is active. The scroll-button DOM is ALWAYS present (injected unconditionally by `tab/mold/tabbox.js`); visibility is gated by this root class.
- `.z-tabbox-top` / `.z-tabbox-bottom` / `.z-tabbox-left` / `.z-tabbox-right` / `.z-tabbox-accordion` — orientation class, always present

On scroll-button children of `.z-tabbox`:
- `.z-tabbox-left-scroll` / `.z-tabbox-right-scroll` — rendered into the DOM when orient is `top` or `bottom` AND `tabscroll` is not false
- `.z-tabbox-up-scroll` / `.z-tabbox-down-scroll` — rendered into the DOM when orient is `left` or `right` AND `tabscroll` is not false
- Each button is a `<div>` with two classes: the `icon` shorthand class plus the directional `-{dir}-scroll` class, wrapping a child `<i class="z-icon-chevron-{left|right|up|down}">` glyph

States with no ZK-added class (pure pseudo-class only): `:hover`, `:focus-visible`, `:active` on `.z-tab` and on scroll-buttons. ZK does NOT emit a "disabled" / "no-more-scroll" class on the scroll buttons — when the strip is at its scroll endpoint, `Tabbox._doClick()` simply early-exits and the click is a no-op. Any "dim at endpoint" appearance must be inferred from runtime state on the strip (`scrollLeft`/`scrollTop`), not from a class.

## Attribute support

Attributes that affect DOM/class output:
- `mold="accordion"` → root gets `.z-tabbox-accordion`; strip-mode classes (`-top`/`-bottom`/`-left`/`-right`) are suppressed; `<tabs>` is not rendered as a separate element; each `<tab>` is rendered as a `<div>` inside its `<tabpanel>` section
- `orient="top|bottom|left|right"` (default `top`) → root gets the matching `.z-tabbox-{orient}` class; affects child rendering order in the mold script; only applies when `mold` is default
- `orient="horizontal"` / `orient="vertical"` → legacy values, normalised to `top` / `left` by `setOrient`
- `tabscroll="true"` (default) → scroll buttons are emitted into the DOM; root receives `.z-tabbox-scroll` when overflow is detected at runtime
- `tabscroll="false"` → scroll buttons are NOT emitted into the DOM and `.z-tabbox-scroll` is never added (overflow simply clips)
- `disabled="true"` on `<tab>` → adds `.z-tab-disabled`
- `closable="true"` on `<tab>` → emits `.z-tab-button` inside `.z-tab-content`, as its **first** child (before icon/label) — in both strip and accordion molds
- `image="<url>"` on `<tab>` → emits `<img class="z-tab-icon">`
- `iconSclass="z-icon-<name>"` on `<tab>` → emits `<i class="z-tab-icon z-icon-<name>">` (the iconSclass value is appended as a literal class)
- `selected="true"` on `<tab>` → adds `.z-tab-selected`; only one tab per tabbox can be selected at a time (set via `_setSel`)

## Layout invariants (REQUIRED for ZK mold compatibility)

These invariants come directly from how `Tabs._scrollcheck` (in `Tabs.ts`) writes inline `width` / `right` styles. Violating any of them breaks ZK's runtime layout math.

- **Scroll buttons MUST be `position: absolute`** anchored to `.z-tabbox` (root must be `position: relative`). `Tabs._scrollcheck` writes inline `style.right = "{toolbarWidth}px"` onto `.z-tabbox-right-scroll` when a toolbar is present, AND sets inline `width: "{calculated}px"` on `.z-tabs` assuming the buttons overlay the strip's 40px leading and trailing slots. If the buttons participate in normal flow (e.g. as flex children of a column-direction root), they consume extra flex rows, shrink `.z-tabpanels`, and break ZK's calculated width math. Iceblue does this; any new theme MUST do the same.
- **Toolbar nested in tabbox MUST be `position: absolute`** when ZK appends the `.z-toolbar-tabs` class (`Toolbar.domClass_` adds it whenever the parent widget is a Tabbox). `Tabs._scrollcheck` computes `tabs.style.width = tbx.offsetWidth − toolbar.offsetWidth − btnsize`. That equation is only correct when the toolbar **overlays** the right edge (horizontal-top) / bottom edge (horizontal-bottom) of the tab-strip row rather than consuming its own flex row. Style as `.z-tabbox > .z-toolbar.z-toolbar-tabs { position: absolute; top: 0; right: 0; z-index: 1; }` (with `top: auto; bottom: 0;` override for `.z-tabbox-bottom`). Toolbar does NOT render in vertical or accordion molds — no rule needed there.
- **Use `margin` (not `padding`) on `.z-tabs` to reserve scroll-arrow space** in scroll mode. ZK writes inline `width` onto `.z-tabs`. Padding compounds with that width (content-box) and shrinks the usable strip; margin sits outside the box and provides the 40px overlay zones for the absolute-positioned arrows. Iceblue uses `.z-tabbox-scroll > .z-tabs { margin: 0 var(--icon-size); }` — match this pattern (`margin: 0 40px` horizontal, `margin: 40px 0` vertical).
- **Margin reservation alone does NOT guarantee the last tab clears the arrow — give scroll buttons an opaque background.** The strip width ZK reserves is `tbx.offsetWidth − toolbarWidth − btnsize`, where `btnsize = left.offsetWidth + right.offsetWidth` (`Tabs._scrollcheck` / `_getArrowSize`, Tabs.ts:241,308). That measurement is taken right after `_showbutton(true)` adds `.z-tabbox-scroll` and can lag the theme's rendered arrow width (observed: ZK reserved ~60px while the arrows rendered at 40px each = 80px), leaving the last visible tab a few px under the right arrow (which is pinned at `right: toolbarWidth`). Because the reservation is measurement-dependent and not reliably correctable from CSS, give the arrows an **opaque** `background-color` matching the strip surface (`var(--zk-color-surface)`) so a partially-scrolled tab slides cleanly *under* the button — the MUI/MD scroll-button pattern. A transparent arrow lets the tab text bleed through and looks broken. Set the hover state-layer to mix over surface (`color-mix(… , var(--zk-color-surface))`), not over `transparent`, or hover re-exposes the bleed-through.
- **Root `.z-tabbox` can be `display: flex; flex-direction: column`** ONLY IF every layout-affecting non-content child (scroll buttons, toolbar) is `position: absolute`. Otherwise revert to iceblue's `display: block` root. The remaining in-flow flex children become exactly `.z-tabs` (row 1, auto height) and `.z-tabpanels` (row 2, `flex: 1`), which is the intended visual structure.
- **Vertical-orient scroll buttons inherit `.z-tabs` inline width**, not the rendered strip width. `Tabs._scrollcheck` writes `u.style.width = d.style.width = tabs.style.width` (Tabs.ts line 425). If the theme imposes a `min-width` on `.z-tabs` in vertical mode, the buttons WILL render narrower than the strip unless the same `min-width` is applied to `.z-tabbox-scroll > .z-tabbox-up-scroll, .z-tabbox-scroll > .z-tabbox-down-scroll`.
- **Accordion-mold section min-height MUST be reset to 0** on `.z-tabbox-accordion .z-tabpanel`. The base `.z-tabpanel { min-height: 80px }` is meant for strip-mode panel content; in accordion mold it inflates every section wrapper to 80px even when collapsed, and `Tabpanel._fixPanelHgh` then computes `cave.height = tbx.offsetHeight − Σ(sibling section heights) − selected_tab_header_height`, which goes negative and clamps to 0 — making the selected cave invisible despite the slideDown animation completing.
- **Accordion `.z-tab-content` MUST reset its base padding** to `0`. The strip-mode `.z-tab-content { padding: 12px 16px }` would double up with the accordion `.z-tab`'s own padding, inflating the header to ~60px (vs. MD3's 48–56px) and starving the cave of vertical space inside a height-constrained tabbox.
- **Vertical scroll-button (`.z-tabbox-up-scroll`/`.z-tabbox-down-scroll`) horizontal anchor MUST follow the tabs' side**: anchored `left: 0` for `.z-tabbox-left` / `.z-tabbox-vertical` (tabs on the left), `right: 0` for `.z-tabbox-right` (tabs on the right). The shared default uses `left: 0`; `.z-tabbox-right` needs an explicit override (`.z-tabbox-right > .z-tabbox-up-scroll, .z-tabbox-right > .z-tabbox-down-scroll { left: auto; right: 0; }`). This invariant is regression-prone — it gets lost when refactoring the four `.z-tabbox-{dir}-scroll` rules into a single shared block.
- **Accordion mold MUST NOT set `border-radius` on `.z-tabbox-accordion`**. The accordion's children (`.z-tab` headers, `.z-tabpanel-content` cave) span the full container width with square corners; combining them with a rounded parent produces visible sharp corners peeking out at the four extremes. The straightforward "clip with `overflow: hidden`" workaround is forbidden in accordion mode (breaks `jq.slideDown` height measurement on the cave). MD3 expansion-panel groups also render flat at the outer edges, so a flat container is both the simpler and design-correct choice.

## Composition invariants

- **Scroll-button DOM is unconditional in strip modes** — `tab/mold/tabbox.js` emits the two directional buttons whenever `isTabscroll()` returns true, regardless of whether overflow exists. CSS controls visibility via the `.z-tabbox-scroll` root class.
- **Horizontal orients emit `left-scroll` + `right-scroll`; vertical orients emit `up-scroll` + `down-scroll`** — never both pairs on the same root.
- **Chevron `<i>` glyph is injected by ZK** — the inner `<i class="z-icon-chevron-{dir}">` is added by `domIconHTML()` in the mold; the theme's icon font/font-face mapping must resolve `z-icon-chevron-left`, `-right`, `-up`, `-down` for the arrows to appear.
- **Accordion mode still emits the close button in the DOM** — when `closable="true"`, `.z-tab-button` is present in the accordion DOM, and ZK itself never hides it (iceblue renders it visible). A theme whose accordion design has no inline dismiss affordance (e.g. MD3 expansion panels) must suppress it **explicitly** (`.z-tabbox-accordion .z-tab-button { display: none }`) — there is no ZK attribute or mold option to turn it off; `closable` still works programmatically (`Tab.close()`/server-side) when the button is hidden.
- **`.z-tab-button` is emitted BEFORE the label** (first child of `.z-tab-content`, both molds; verified live 2026-06-05). In document flow it therefore renders on the *leading* side of the label — opposite to the trailing-close convention (MD3 chip / MUI / Chrome tabs). Any theme that wants the × trailing must reposition it: flex `order` on the button (Marble) or absolute positioning (iceblue). This skill's earlier DOM tree drew the button *after* the content row, which is how Marble shipped a leading × unnoticed.
- **One selected tab per tabbox** — `_setSel` deselects the previous tab before selecting the new one; CSS may not rely on multiple `.z-tab-selected` siblings.
- **In accordion mode `.z-tab` is `<div>`; in strip modes `.z-tab` is `<li>`** — element-type selectors (`li.z-tab`) do not match the accordion variant.
- **`<toolbar>` only renders in horizontal+default-mold** — vertical and accordion molds skip it.

## Bundle

`tabbox.css.dsp`.

## Edition

CE.

## Notes

- The accordion expandable area uses inline `display: none` / `display: block` toggled by ZK JS (no CSS class toggle) — slideDown/slideUp animation is JS-driven.
- The legacy id-based selector `.z-tabpanel > [id$="-cave"]` in older CSS is fragile — prefer `.z-tabpanel-content` which is the actual class on the expandable area.
- `tabscroll="false"` produces no class marker — it only prevents `.z-tabbox-scroll` from being added and skips emission of the scroll-button DOM entirely.
- Scroll-button click handlers (`_doClick`) move the strip's `scrollLeft`/`scrollTop` by `move` pixels. When `move <= 0` the call returns immediately — this is the only "disabled at endpoint" mechanism and it is purely behavioural, with no class hook.
- For horizontal scroll buttons, `Tabs._scrollcheck` writes inline `style.right = "{toolbarWidth}px"` onto the right-scroll button when a toolbar is present, so the right-scroll button sits to the left of the toolbar.
