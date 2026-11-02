# Theme CSS override is REPLACE, not merge

## The fact

When a ZK theme provides a CSS file at the same path as a stock widget CSS, ZK serves **only the theme version**. The stock file is not loaded at all. There is no merge / cascade between them.

For example, the `colorbox` widget registers `<css-uri>css/colorbox.css.dsp</css-uri>` in `zkex/lang-addon.xml`, which resolves to `~./js/zkex/inp/css/colorbox.css.dsp`. ZK first looks for `web/<theme>/js/zkex/inp/css/colorbox.css.dsp` (theme path); if found, that is served and the original `web/js/zkex/inp/css/colorbox.css.dsp` (stock path) is ignored.

This applies to every component registered via `<css-uri>` — `zul/`, `zkex/`, `zkmax/` alike.

## Why this matters

If a theme author writes a minimal theme CSS that only customizes a few rules (colors, font sizes) and assumes the stock CSS will provide the structural rules (positioning, geometry, orientation handling), **all those structural rules disappear**. The component then renders with default browser CSS, which usually means:

- `position` defaults to `static` → absolutely-positioned children stack incorrectly
- `height: 100%` on flex / floated children has nothing to inherit from
- Orientation-specific rules (`-vertical`, `-horizontal` variants) silently drop

The component LOOKS broken in a way that suggests "the theme isn't loading", but in fact the theme IS loading — it's just incomplete.

## Example (rangeslider)

A theme `rangeslider.css` that defined only a few rules left `.z-rangeslider-mark` at `position: static` (marks stacked vertically instead of along the track), gave the vertical slider a horizontal 4px track, and left the mark dot, mark label and slider-buttons classes unstyled. All of those rules exist in the stock CSS but were never loaded, because the theme file replaced it. The fix was to re-author the theme CSS with the full structural ruleset (the current `js/zkex/slider/css/rangeslider.css` in zkcml).

## The rule

When you create or modify a theme CSS at `js/{zul|zkex|zkmax}/.../css/{component}.css`:

1. **Read the stock CSS.** For any edition, extract the IceBlue file from the tracked jar in the zk checkout: `unzip -p dist/lib/iceblue_c.jar web/iceblue_c/js/{zul|zkex|zkmax}/<pkg>/css/{component}.css.dsp` (list entries with `unzip -l dist/lib/iceblue_c.jar | grep css.dsp`). The `js/**/css` source trees (`$ZK_SOURCE`, `$ZKCML_SOURCE`) now hold Marble source, not IceBlue. Fall back to fetching `${PREVIEW_URL}/zkau/web/{version}/js/{path}/css/{name}.css.dsp` after the page renders without theme override only when `unzip -l` has no entry for the component.
2. **List every selector** the stock CSS defines.
3. **Re-implement each selector** in the theme version, expressing the visual values (colours, sizes) in the theme's own way. Structural rules (`position`, `display`, `width: 100%`, orientation-specific overrides) MUST be preserved verbatim — those are part of the widget's contract with its JavaScript, not stylistic choices.
4. Only after the structural copy is in place, layer the theme's own changes (color tokens, elevation, motion).

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
curl -s "${PREVIEW_URL}/zkau/web/{ver}/_zkiju-<theme>/zul/css/zk.wcs" \
  | grep -oE "\.z-{component}[^\s{]*" | sort -u | wc -l
```

If the count is much lower than the stock CSS's selector count for the same prefix, the theme is incomplete.

## Related infrastructure note

`html` and `body` must each have a definite `height: 100%` (not `min-height`, not `100vh`) so the empty area below content is inside the body box — otherwise floating popups (combobox / datebox / colorbox / chosenbox / cascader / menupopup …) cannot be dismissed by clicking the empty area below the page content. ZK core's outside-mousedown handler ignores clicks with `clientY > body.clientHeight`. See `reference/viewport-height-fill.md` for the evidence.
