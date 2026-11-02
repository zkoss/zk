# colorbox

Color picker widget: shows the currently-selected color as a swatch and opens a popup with a gradient picker and/or palette grid.

## Edition: PE (zkex.jar)

Registered by `zkex/src/main/resources/metainfo/zk/lang-addon.xml`. Available in PE and EE; not in CE. See `reference/edition-availability.md`.

## DOM structure (from mold/colorbox.js)

```
.z-colorbox                            ← root <div>
├─ .z-colorbox-current   <i>           ← swatch; ZK sets inline style="background-color: <selected>"
├─ .z-colorbox-button    <span>        ← trigger area at the corner
│   └─ .z-colorbox-icon
│       .z-icon-caret-down  <i>        ← dropdown indicator
└─ .z-colorbox-popup     <div>         ← popup (only present if picker or palette enabled)
    ├─ .z-colorbox-paletteicon         ← tab toggle for palette view (when both modes enabled)
    ├─ .z-colorbox-pickericon          ← tab toggle for picker view
    ├─ .z-colorpicker                  ← gradient + hue picker (256×256 sprite-based)
    │   ├─ .z-colorpicker-gradient     ← positioned 0,0 256×256
    │   ├─ .z-colorpicker-overlay
    │   ├─ .z-colorpicker-bar          ← hue bar 12×256, hard-coded left:7px
    │   ├─ .z-colorpicker-circle       ← drag handle on gradient
    │   ├─ .z-colorpicker-arrows       ← drag handle on hue bar
    │   ├─ .z-colorpicker-color        ← preview swatch (new vs old color)
    │   ├─ .z-colorpicker-rgb / -hsv / -hex
    │   └─ .z-colorpicker-input        ← numeric inputs
    └─ .z-colorpalette                 ← grid of preset colors
        ├─ .z-colorpalette-head        ← header strip
        ├─ .z-colorpalette-newcolor    ← preview of hovered/selected
        ├─ .z-colorpalette-color       ← each preset cell
        └─ .z-colorpalette-selected    ← currently chosen
```

## Selected color comes from inline style

The currently-selected color is **not** a class — it's set inline on the swatch:

```html
<i id="…-currcolor" class="z-colorbox-current" style="background-color: rgb(24, 77, 198);">&nbsp;</i>
```

If the swatch has zero width/height in CSS, the color is invisible. The theme **must** give `.z-colorbox-current` an explicit size (e.g. `flex: 1` if the root is flex, or `width: 100%; height: 100%` if the root is positioned).

## Indicator icon class is fixed by the mold

The mold hard-codes `class="z-colorbox-icon z-icon-caret-down"` on the indicator `<i>`. A theme that wants a different glyph for the dropdown indicator must restyle `.z-colorbox-icon` itself, because the class list cannot be changed from ZUL.

## Popup detachment

`.z-colorbox-popup` follows the standard ZK floating-popup rule — at open time it is appended to `<body>` and given inline `left/top/width`. Apply `reference/floating-popup-in-body.md`:

- Do **not** put `width: 100%` / `min-width: 100%` / `max-width: 100%` on `.z-colorbox-popup` — those resolve against body.
- For the colorbox specifically, the popup is wide (~280–320px for palette, ~480–540px for picker because of the 256×256 gradient + hue + RGB inputs). Don't try to constrain.

## Picker / palette sub-layout is geometry-locked

The colorpicker uses fixed-size bitmap sprites (`colorpicker_gradient.png`, `colorpicker_hue.png`, `colorpicker_select.gif`, `colorpicker_arrows.gif`) at hard-coded pixel positions (top: 0, left: 0, etc. in the stock IceBlue colorbox theme). A theme **cannot** restyle these layouts without breaking the picker — the drag handles' positions are calculated in JavaScript against these exact coordinates.

The theme's freedom is limited to:
- Outer popup chrome: border, border-radius, padding, shadow, background
- Tab-icon (`.z-colorbox-paletteicon`, `.z-colorbox-pickericon`) hover / focus / checked colors
- Input fields inside picker (`.z-colorpicker-input`) — can use theme typography + colors
- Palette cell hover / selected outline

Everything else (gradient size, hue bar size, RGB input layout) must be preserved at the original Sapphire/Breeze geometry.

## Tab icons sit on top of palette/picker — must be raised

DOM order inside the popup is:

```
.z-colorbox-popup
├─ .z-colorbox-paletteicon   (absolute, top:8 left:8)
├─ .z-colorbox-pickericon    (absolute, top:8 left:44)
├─ .z-colorpicker            (relative, fills popup)
└─ .z-colorpalette           (relative, fills popup)
```

The icons render **before** `.z-colorpicker` / `.z-colorpalette` in the source order. With `z-index: auto` on everything, the later-in-DOM picker/palette **paints over** the absolute-positioned icons. Visually the icons may still appear (because picker/palette reserve `top: 50px` for them at the local level), but **`elementFromPoint(iconCenter)` returns the picker/palette element**, which means real native clicks land on the picker — and the click handlers bound by `Colorbox.ts` `domListen_(paletteBtn, 'onClick', 'openPalette')` never fire.

Symptom: clicking the tab icons does nothing; `.z-colorpicker` / `.z-colorpalette` inline `style="display: none/block"` never flips. Synthetic `.click()` / `jq.trigger('click')` will toggle correctly because they bypass hit-testing — which makes this trap invisible to naive verification.

**Fix:** give both icons `z-index: 1` (or any explicit value above the siblings):

```css
.z-colorbox-paletteicon,
.z-colorbox-pickericon {
    position: absolute;
    z-index: 1;        /* lift above the later-rendered picker/palette */
    /* ... */
}
```

Verify with `document.elementFromPoint(iconRect.left + iconRect.width/2, iconRect.top + iconRect.height/2)` — must return the icon element itself, not `.z-colorpicker` / `.z-colorpalette`.

## ZK toggles palette/picker via inline `style="display: ..."`, not CSS classes

`Colorbox.ts` `openPalette()` and `openPicker()` set `pp.style.display = 'block' | 'none'` directly. The popup root also gets a `.z-colorpalette-popup` / `.z-colorpicker-popup` class added/removed for theme hooks, but the actual display gate is inline style.

Implication: writing CSS rules like `.z-colorpalette-popup .z-colorpicker { display: none }` is **redundant** (it can't override the inline `display: none` ZK already set), but harmless. The real toggle has already happened by the time CSS runs.

## Menu-content mold — colorbox embedded in a `<menu>`

ZK can create a colorbox **inside a Menu** (not the standalone `<colorbox>` widget). Setting a Menu's `content` to `#color=#RRGGBB` auto-builds a color picker:

```xml
<menubar>
    <menu label="Color" content="#color=#184dc6"/>
</menubar>
```

This is handled by `zkex.inp.ContentHandler` (a content handler of `zul.menu.Menu`, see `zkcml/zkex/src/main/resources/web/js/zkex/inp/ContentHandler.ts`). The picker/palette objects are the **same** `zkex.inp.Colorpicker` / `zkex.inp.Colorpalette` used by `<colorbox>` — but the **wrapper classes are different** because the host widget's zclass is `z-menu`, not `z-colorbox`.

### Mirror-class map (menu mold vs standalone)

| Standalone `<colorbox>` | Menu-content mold | Element |
|-------------------------|-------------------|---------|
| `.z-colorbox-popup`      | `.z-menu-popup`      | popup frame (detached to `<body>`) |
| `.z-colorbox-paletteicon`| `.z-menu-paletteicon`| palette tab toggle |
| `.z-colorbox-pickericon` | `.z-menu-pickericon` | picker tab toggle |
| `.z-colorbox-current` (swatch) | `.z-menu-image.z-colorbox-color` | the color chip beside the menu label |
| `.z-colorpicker` / `.z-colorpalette` | **same** (`.z-colorpicker` / `.z-colorpalette`) | picker/palette body — no zclass prefix |

The toggle state classes are identical for both molds: ZK adds `.z-colorpalette-popup` / `.z-colorpicker-popup` to the popup root (`.z-menu-popup` here), so selected-tab rules read e.g. `.z-colorpalette-popup .z-menu-paletteicon`.

**`.z-menu-popup` ≠ `.z-menupopup`.** Regular menubar dropdowns render the `Menupopup` widget with class `.z-menupopup` (no hyphen). The color-content popup is `wgt.$s('popup')` on the Menu (`z-menu` + `-popup`) → `.z-menu-popup` (hyphenated). The HTML-content variant (`content="<html…>"`) renders `.z-menu-content-popup` instead. So `.z-menu-popup` is exclusive to the **color** menu and safe to style without touching ordinary menus.

**Theme trap:** a theme that styles only the `.z-colorbox-*` set ships the menu mold with a chrome-less popup (no surface/border/shadow/padding) and invisible tab toggles, even though the palette/picker grid itself renders (shared classes). Any theme must style the `.z-menu-*` mirrors in lockstep — the ZK default theme groups them in one selector list (`.z-colorbox-popup, .z-menu-popup { … }`). The label swatch `.z-menu-image.z-colorbox-color` gets its `background-color` set inline by `ContentHandler.bind` (`jq(img).addClass('z-colorbox-color').css('backgroundColor', …)`), so it needs only a border + radius to read as a chip.

### The label chip shows only when the colour menu is **non-topmost**

`Menu.ts` `domContent_` (≈ line 230) writes inline `style="display:none"` on the image `<img>` **iff `isTopmost()`** — i.e. the menu sits directly in a `<menubar>`. `ContentHandler.bind` then sets the chip's `background-color` on that same node but never clears the inline `display`. Consequences:

- **Topmost colour menu** (`<menubar><menu content="#color=…"/>`): chip is inline-hidden by ZK. The label shows text + caret only; CSS cannot reveal the chip without `!important` (don't — this matches the default theme, which also leaves it hidden).
- **Nested colour menu** (`<menupopup><menu content="#color=…"/></menupopup>`): `isTopmost()` is false → no inline `display:none` → the chip is visible and CSS-controlled. This is the realistic "Text Colour / Fill Colour" dropdown pattern.

Second trap for the nested case: a theme's generic *hide-blank-placeholder* rule (`.z-menu-image[src*="R0lGODlhAQABAIAA"] { display:none }`, common because the chip's `<img src>` is a blank 1×1 GIF — the colour is CSS-only) will also hide the chip. Exclude it: `…[src*="…"]:not(.z-colorbox-color)`. Specificity won't save you here — if component CSS is split across bundles that land in different cascade layers, layer order can override specificity, so neutralise the placeholder rule at its source rather than out-specifying it.

## Bundle

`colorbox.css.dsp` in zkex. No siblings.
