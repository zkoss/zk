# menubar

A horizontal or vertical menu container holding `<menu>` and `<menuitem>` children.

## DOM structure — intermediate `<ul><li>` wrapper

```
.z-menubar
└─ ul
    └─ li
        └─ .z-menu | .z-menuitem
```

ZK wraps each menu item in `<ul><li>` for semantic HTML and accessibility. The `<li>` is the structural child of `.z-menubar`, not the menu element itself.

Selectors that assume direct child:

```css
/* WRONG — fails because of intermediate ul/li */
.z-menubar > .z-menu { ... }

/* CORRECT */
.z-menubar > ul > li > .z-menu { ... }
```

## Orientation

- `orient="horizontal"` (default) — `<ul>` becomes a horizontal flex container
- `orient="vertical"` — `<ul>` stacks vertically

The orientation class is on `.z-menubar`: `.z-menubar-horizontal` / `.z-menubar-vertical`.

## Menu item DOM structure

Each `.z-menu` child has this internal structure:

```
.z-menu
└─ .z-menu-content
   ├─ .z-menu-icon   (optional — icon <i> element)
   ├─ .z-menu-text   (label text)
   └─ .z-menu-icon   (trailing arrow icon, for submenus)
```

State class: `.z-menu-selected` — added by ZK when the item is active/open.

## Submenu / popup

Hovering or clicking a `.z-menu` opens its popup (`.z-menupopup`) containing nested menu items. The popup is rendered outside the menubar in the DOM tree (positioned absolutely).

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

`menubar.css.dsp` covers menubar, menu, menuitem, menupopup, menuseparator.
