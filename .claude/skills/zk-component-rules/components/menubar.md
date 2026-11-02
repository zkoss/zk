# menubar

A horizontal or vertical menu container holding `<menu>` and `<menuitem>` children.

## DOM structure — intermediate `<ul>` wrapper

```
.z-menubar
└─ ul
    └─ li.z-menu | li.z-menuitem
```

ZK wraps the items in a `<ul>` for semantic HTML and accessibility. `.z-menu` and `.z-menuitem` are themselves the `<li>` elements (mold `zul/menu/mold/menu.js` pushes `<li` with the widget's `domAttrs_()`), so there is no `<li>` between the `<ul>` and the `.z-menu`.

A selector that skips the `<ul>` never matches:

```css
/* fails: the <ul> sits between .z-menubar and .z-menu */
.z-menubar > .z-menu { ... }

/* matches */
.z-menubar > ul > .z-menu { ... }
```

## Orientation

- `orient="horizontal"` (default) — `<ul>` becomes a horizontal flex container
- `orient="vertical"` — `<ul>` stacks vertically

The orientation class is on `.z-menubar`: `.z-menubar-horizontal` / `.z-menubar-vertical`.

## Menu item DOM structure

Each `.z-menu` (an `<li>`) has this internal structure (source: `Menu.domContent_` in `zul/menu/Menu.ts`):

```
li.z-menu
└─ a.z-menu-content
   ├─ img.z-menu-image | i (iconSclass)   (leading image or icon)
   ├─ span.z-menu-text                    (label text)
   ├─ div.z-menu-separator
   └─ i.z-menu-icon.z-icon-caret-*        (trailing caret only)
```

State class: `.z-menu-selected` — added by ZK when the item is active/open.

## Submenu / popup

Hovering or clicking a `.z-menu` opens its popup (`.z-menupopup`) containing nested menu items. The mold renders the popup inside the `<li>`; on open ZK moves it out with `makeVParent()` (`Menu.ts`), so it is positioned absolutely outside the menubar.

## Scrollable menubar

Enabled via `setScrollable(true)` (horizontal only — ignored when `orient="vertical"`). When the content overflows, ZK wraps the item list and renders two overlay scroll arrows:

```
.z-menubar.z-menubar-horizontal.z-menubar-scroll   ← .z-menubar-scroll added by JS only while overflowing
├─ .z-menubar-left.z-menubar-scrollable            ← i.z-menubar-icon.z-icon-chevron-left
├─ .z-menubar-right.z-menubar-scrollable           ← i.z-menubar-icon.z-icon-chevron-right
└─ .z-menubar-body                                 ← JS sets margin-left/right = arrow offsetWidth
    └─ .z-menubar-content (width:5000px)
        └─ ul > li...
```

Theme-independent facts every theme must honor:

- **Arrows are `position:absolute`** (`.z-menubar-left{left:0}` / `.z-menubar-right{right:0}`), overlaid on the bar — they are NOT flex children. ZK's JS toggles their `display:block/none` and sets `.z-menubar-body` left/right margins to the arrow width so item content never sits under an arrow. Do not convert them to flex; the JS assumes the absolute model.
- **Absolute `left:0`/`right:0` resolve to the bar's *padding* edge, not its border edge.** So any horizontal padding on `.z-menubar` insets the arrows inward. A themed menubar with a gutter MUST reset `padding-left/right` to `0` under `.z-menubar-scroll` (the arrows + body margins already provide the inset), otherwise the arrows float inside the bar.
- **The scroll icon has no color of its own** — `.z-menubar-icon` should inherit the bar's text color so it contrasts with whatever surface the bar uses. Never give it a fixed `on-primary`/white that assumes a colored bar.
- **`_fixScrolling` sets `style.display='block'` inline on each arrow** (toggling block/none to show/hide). This inline declaration overrides any `display:flex`/`inline-flex` in CSS, so the arrow's `align-items`/`justify-content` do **nothing** — the icon falls back to inline flow pinned to the top of the track. Center the icon by a mechanism that survives `display:block`: absolute-position it inside the (already `position:absolute`) arrow (`top:50%;left:50%;transform:translate(-50%,-50%)`) or use a track-height `line-height`. Do not rely on flex centering on the arrow.

## Bundle

`menu.css.dsp` (source `js/zul/menu/css/menu.css`) covers menubar, menu, menuitem, menupopup, menuseparator.
