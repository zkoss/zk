# Framework css-flex classes: z-flex / z-flex-row / z-flex-column / z-flex-item

`.z-flex`, `.z-flex-row`, `.z-flex-column`, `.z-flex-item` are **framework classes, not theme utilities**. ZK's client-side css-flex engine (`zk/flex.ts`) toggles them at runtime on any container whose children use positive `hflex`/`vflex`:

- `applyCSSFlex()` adds `z-flex` + `z-flex-row` (or `z-flex-column`) to the flex **container** and `z-flex-item` to each flexed **child**.
- `clearCSSFlex()` removes them — e.g. when a widget calls `setHflex(false)`, which splitlayout's drag-end does on purpose so it can persist pane sizes as inline px values.

A theme that omits or renames these classes **silently breaks `hflex`/`vflex` everywhere**: ZK adds the class, no CSS rule matches, the container stays `display: block`.

## Required CSS (define verbatim)

Every ZK theme MUST ship this block exactly, loaded late in the cascade. Stock source of truth: `zk/zul/src/main/resources/web/zul/less/footer.less`. Marble location: `zul/src/main/resources/web/zul/css/base/_cssflex.css` (bundled into `footer.css.dsp` via `footerFiles` in `scripts/build-css.js`).

```css
.z-flex { display: flex; }
.z-flex > :not(.z-flex-item) { flex-shrink: 0; }
.z-flex-row { flex-direction: row; }
.z-flex-column { flex-direction: column; }
.z-flex-item { flex: 1 1 0; min-height: 0; min-width: 0; }
```

## Hard rules

1. **Never rename these selectors.** Theme utility classes may use any naming (`.z-d-flex`, `.z-flex-col`, …) but they are *additional*, not replacements — the JS-toggled names above must exist with exactly these values. (Marble once renamed `.z-flex` → `.z-d-flex` and `.z-flex-column` → `.z-flex-col` in the utility pass; the css-flex engine was inert theme-wide until the framework block was restored. Gap log 2026-06-05.)
2. **Never hard-code the framework classes' effect onto a JS-managed component root.** E.g. `.z-splitlayout { display: flex }` looks equivalent to ZK adding `z-flex` — but ZK *removes* `z-flex` at drag-end so inline px sizes take over. A hard-coded `display: flex` (or a `:not(.z-flex-column)` direction fallback) survives the class removal and defeats the mechanism. The proven failure: splitlayout drag persistence (contract rows M10/M11).
3. **Never give a JS-sized child `flex-basis: 0` in component CSS.** `flex: 1 1 0` on an element whose inline `width`/`height` ZK writes makes the browser ignore the inline size on the main axis. `z-flex-item` is the only legitimate carrier of `flex: 1 1 0`, because ZK removes it before writing inline sizes.

## Margin subtraction — never give flex-capable widgets default margins

`applyCSSFlex` (zk/flex.ts ~600–624) sizes a widget whose flex wrapper is a **separate element** (`fcc != c` — e.g. splitlayout caves wrap the child widget) with an inline `calc()` that subtracts the widget's own CSS margins:

- **row** container → subtracts `zk(c).marginHeight()` (top+bottom margins) — from **both** `width` and `height`;
- **column** container → subtracts `zk(c).marginWidth()` (left+right margins) — from both.

So a theme-default `margin-bottom: 12px` on `.z-window` turns into `width/height: calc(100% - 12px)` inside any **row**-oriented flex parent: a 12px hole on the trailing edge of both axes (proven: splitlayout horizontal panes, contract row M12, gap log 2026-06-05). Column orientation masks the same margin (`marginWidth()` = 0), so the bug surfaces asymmetrically — one orientation fine, the other broken.

**Rule: stock ZK widgets carry ZERO default margin, and ZK's JS sizing paths (css-flex `calc`, `setFlexSize_`, layout-region sizing) are written on that assumption. A theme must never add default margins to widgets that can be `hflex`/`vflex` children** (in practice: any container widget — window, panel, grid, listbox, tree, tabbox, …). Inter-widget spacing must be opt-in: a stack utility on the parent (Marble: `.z-vstack`), explicit margin utilities (`.z-mb-*`), or ZK's own `<vlayout spacing>`. Marble shipped a Bootstrap-style default-rhythm rule and removed it for exactly this reason (user ruling 2026-06-05; see `doc/spec/spacing-policy.md`).

## Affected widgets

Any container whose children use positive `hflex`/`vflex`: div, window, hlayout/vlayout inner, splitlayout caves, tabbox panels, borderlayout region content, cell contents in grid/listbox, … The classes appear and disappear at runtime — component CSS must lay out correctly in **both** states (with and without `z-flex*`).

## Debugging signature

Symptom: an `hflex`/`vflex` child collapses to intrinsic size, or a JS-written inline `width`/`height` is visibly ignored (`getBoundingClientRect()` ≠ inline style value). Check: does the live container have `z-flex`/`z-flex-row|column` and do those classes resolve to real CSS rules? Does any theme rule pin `display`/`flex-direction`/`flex` on the same elements unconditionally?
