# Theme CSS override is REPLACE, not merge

## The fact

When a ZK theme provides a CSS file at the same path as a stock widget CSS, ZK serves **only the theme version**. The stock file is not loaded at all. There is no merge / cascade between them.

For example, the `colorbox` widget registers `<css-uri>css/colorbox.css.dsp</css-uri>` in `zkex/lang-addon.xml`, which resolves to `~./js/zkex/inp/css/colorbox.css.dsp`. ZK first looks for `web/zk-material/js/zkex/inp/css/colorbox.css.dsp` (theme path); if found, that is served and the original `web/js/zkex/inp/css/colorbox.css.dsp` (stock path) is ignored.

This applies to every component registered via `<css-uri>` — `zul/`, `zkex/`, `zkmax/` alike.

## Why this matters

If a theme author writes a minimal theme CSS that only customizes a few rules (colors, font sizes) and assumes the stock CSS will provide the structural rules (positioning, geometry, orientation handling), **all those structural rules disappear**. The component then renders with default browser CSS, which usually means:

- `position` defaults to `static` → absolutely-positioned children stack incorrectly
- `height: 100%` on flex / floated children has nothing to inherit from
- Orientation-specific rules (`-vertical`, `-horizontal` variants) silently drop

The component LOOKS broken in a way that suggests "the theme isn't loading", but in fact the theme IS loading — it's just incomplete.

## Real example (rangeslider, 2026-05-14)

Before fix, our `js/zkex/slider/css/rangeslider.css` only defined CSS custom properties + a couple of rules:

```css
:root { --zk-rangeslider-inner-size: 4px; … }
.z-rangeslider-inner { width: 100%; height: var(--zk-rangeslider-inner-size); … }
.z-rangeslider-track { position: absolute; height: 100%; … }
```

Symptoms on the live page:
- `.z-rangeslider-mark` had `position: static` → marks stacked vertically instead of along the horizontal track
- Vertical rangeslider had `width: 100% height: 4px` → track was a 4-px horizontal strip even when the slider was rendered vertically
- `.z-rangeslider-mark-dot`, `.z-rangeslider-mark-label`, `.z-sliderbuttons-area`, `.z-sliderbuttons-tooltip` had no styles at all

All of those rules exist in the stock CSS, but were never loaded because the theme version replaced the stock file.

Fix was to re-author the theme CSS with the FULL structural ruleset, using `--zk-*` tokens for theme-specific values. See the current `js/zkex/slider/css/rangeslider.css`.

## The rule

When you create or modify a theme CSS at `js/{zul|zkex|zkmax}/.../css/{component}.css`:

1. **Read the stock CSS** at `/Users/hawk/Documents/workspace/ZK10/zkcml/{zk|zkex|zkmax}/.../css/{component}.css.dsp` (or fetch `${PREVIEW_URL}/zkau/web/{version}/js/{path}/css/{name}.css.dsp` after the page renders without theme override).
2. **List every selector** the stock CSS defines.
3. **Re-implement each selector** in the theme version, replacing hex / pixel literals with `--zk-*` tokens. Structural rules (`position`, `display`, `width: 100%`, orientation-specific overrides) MUST be preserved verbatim — those are part of the widget's contract with its JavaScript, not stylistic choices.
4. Only after the structural copy is in place, layer MD3-specific changes (color tokens, elevation, motion).

## Detecting the problem fast

Run on the affected component's preview page:

```javascript
// Compare against the stock count — if your theme is missing rules, you'll see <half.
[...document.styleSheets].flatMap(s => {
  try { return [...s.cssRules].filter(r => r.selectorText?.includes('z-{component}')); }
  catch (e) { return []; }
}).map(r => r.selectorText);
```

Or grep the WCS bundle:

```bash
curl -s "${PREVIEW_URL}/zkau/web/{ver}/_zkiju-zk-material/zul/css/zk.wcs" \
  | grep -oE "\.z-{component}[^\s{]*" | sort -u | wc -l
```

If the count is much lower than the stock CSS's selector count for the same prefix, the theme is incomplete.

## Related infrastructure note

`html` and `body` must have `min-height: 100% / 100vh` so the empty area below content captures clicks — otherwise floating popups (combobox / datebox / colorbox / chosenbox / cascader / menupopup …) cannot be dismissed by clicking the empty area below the page content. The outside-click handler relies on the click bubbling up to `document`, which requires the click target to actually receive a mouse event. See `base/_reset.css`.
