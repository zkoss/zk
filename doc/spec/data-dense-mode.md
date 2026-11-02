# Data-Dense (Compact) Display Mode

Marble's defaults target general business UIs. Data-dense applications — ERP-class
screens (iDempiere and similar) that pack many rows and fields into a viewport — need
a tighter UI. Marble exposes this as a **density attribute**: put `data-density="compact"`
on an element and everything inside it shrinks coherently — the whole app (on `<html>`)
or just one region (on any container). No forking, no per-component classes, and the only
markup change is that one attribute.

## How it works

The density system has three layers, all defined in
[_sizing.css](../../zul/src/main/resources/web/zul/css/tokens/_sizing.css):

1. **Control-height ladder** — the seed rungs `--zk-control-height-{xs,sm,md,lg,xl}`
   (28 / 32 / 40 / 48 / 56 px by default). This is what you flip.
2. **Semantic alias layer** — `--zk-input-height`, `--zk-button-height`,
   `--zk-toolbar-height`, `--zk-window-header-height`, … Each maps a component type to
   a rung *by meaning*. The intentional size *relationships* (input 40 > button 36,
   toolbar 48 > input 40) live here, defined once — so they survive any tier change.
3. **Data-cell padding** — `--zk-grid-cell-padding`, `--zk-listbox-cell-padding`,
   `--zk-tree-cell-padding` (the biggest lever for table density), and `--zk-group-padding-y/x`
   for the group rows of Grid and Listbox (default and tablet keep 8px 16px; a tablet keeps them in compact too, because zkmax `tablet/_mesh.css` sets the two tokens beside the cell padding it already shadows).

Every component's CSS binds to a rung or an alias — never a raw px. Marble ships a
`[data-density="compact"]` rule (in _sizing.css) that re-points all of them to their
compact values, so the attribute cascades through the whole theme at once. This mirrors
how Salesforce Lightning (Comfy/Compact) and Ant Design (`controlHeight` seed) deliver
global density.

> **Why the shipped rule uses literal values, not `var()` of a rung:** a var()-derived
> alias declared at `:root` is substituted there and inherited *frozen*, so overriding a
> rung alone would not re-size the aliases on a nested region. Listing the consumed tokens
> as literals lets `data-density` work at ANY scope — see
> [Appendix: why the shipped rule uses literal values](#appendix-why-the-shipped-rule-uses-literal-values-the-freeze-problem).

## Switching to compact

**Whole app, fixed default** — set the library property in `zk.xml`. ZK renders
`data-density="compact"` on `<html>` with the page, so there is no flash and no code:

```xml
<library-property>
    <name>org.zkoss.theme.marble.density</name>
    <value>compact</value>   <!-- comfortable (default) | compact -->
</library-property>
```

It applies when ZK renders the whole HTML document (a top-level ZUL page). A page's own
`<?root-attributes data-density="…"?>` wins over it. When the `<html>` tag is yours (a ZUL
included in a JSP or another template, or a zhtml `<html>` root), put the attribute there
instead — the attribute on the document root covers body-appended popups (menus, modal
windows, notifications) too:

```html
<html data-density="compact">
```

or, at runtime from Java (no JS string, no DOM detail), use the theme helper
[MarbleDensity](../../zul/src/main/java/org/zkoss/zul/theme/MarbleDensity.java):

```java
MarbleDensity.apply(MarbleDensity.Density.COMPACT);                // whole app
MarbleDensity.apply(myGridPanel, MarbleDensity.Density.COMPACT);   // one region
```

**One region** — set it on any container; it nests and a closer descendant can override
it back to `comfortable`:

```xml
<zk xmlns:ca="client/attribute">
    <vlayout ca:data-density="compact"> … a dense grid … </vlayout>
</zk>
```

In ZUL the attribute must go through the `client/attribute` namespace (`ca:`). A plain
`data-density="compact"` on a component fails with `PropertyNotFoundException`, because it is
not a component property.

For a fixed whole-app *default*, prefer the library property (or the attribute in your
page template) over the Java call — the latter runs after first paint and can briefly
flash. See the FOUC note in MarbleDensity's Javadoc.

**Tuning the values** — the shipped compact values (below) are a balanced starting point.
To change them, write your own `html[data-density="compact"]` rule — one notch more specific
than the shipped rule, so it wins regardless of load order — and set any token from `_sizing.css`.

## The full knob set

The default and compact value of every knob (the `--zk-control-height-*` ladder, `--zk-button-height*`, `--zk-menuitem-height`, `--zk-data-row-min-height`, and the grid / listbox / tree cell, header and group padding) is in [_sizing.css](../../zul/src/main/resources/web/zul/css/tokens/_sizing.css). Reasons (not in that file):

- `--zk-tree-cell-padding` and `--zk-tree-header-padding` equal the grid and listbox values, so a tree row and header are as tall as a grid row and header.
- `--zk-group-padding-y` equals a compact data cell. `--zk-group-padding-x` for a group of a checkmark Listbox stays at the comfortable value, so its checkbox and arrow stay under the row checkboxes.
- `--zk-data-row-min-height` is read only by `listbox.css` (the `mold="paging"` bar).

On a tablet the two group tokens keep their default values in compact as well (zkmax `tablet/_mesh.css`). Measured group row height at tablet default and at compact: 41px (Grid), 40.5px (Listbox).

### Finer control

The aliases are individually overridable too. To shrink, say, only the toolbar without
touching the rest of the `lg` rung, set `--zk-toolbar-height: 40px;`. The full alias
list is in [_sizing.css](../../zul/src/main/resources/web/zul/css/tokens/_sizing.css).

## Intentionally NOT scaled

Icon-glyph font-sizes, calendar day cells, textarea min-height (content-driven), and
decorative geometry stay fixed — they are not control heights and coupling them to the
ladder would distort glyphs. Adjust them individually if a specific case needs it.

## Notes

- **Touch targets**: compact heights drop below the MD3 44/48px touch minimum. That's
  correct for dense *desktop* ERP use. On mobile UAs the tablet layer
  (`zkmax/css/tablet/_tokens.css`) still scales controls up via its own touch tokens.
- **Picking your own tier**: the compact values above are a balanced starting point.
  For an even denser tier, keep stepping the ladder down (e.g. md 28, lg 36) — the
  alias layer keeps everything proportional.

## Appendix: why the shipped rule uses literal values (the "freeze problem")

The shipped `[data-density="compact"]` rule re-points the consumed tokens to their
compact values as **literals** (e.g. `--zk-input-height: 32px`), not as `var()` of a
re-pointed rung. This is deliberate. Getting it wrong is the single most common density
bug, so the reasoning is worth recording.

### var() is substituted on the element that *declares* it

A CSS custom property has a rule that is easy to miss:

> `var()` substitution happens on the element that **declares the property** — it is
> resolved there into a concrete value, and that concrete value is what inherits to
> descendants.

Look at the two declarations on `:root`:

```css
:root {
    --zk-control-height-md: 40px;
    --zk-input-height: var(--zk-control-height-md);   /* resolved to 40px on :root */
}
```

`--zk-input-height` is substituted to the concrete value `40px` **on `:root`**. From
there it inherits `40px` — the fixed value, **not** the `var(--zk-control-height-md)`
formula.

### How the freeze happens

Suppose compact mode re-pointed only the **rung** on a *descendant* element:

```css
body.compact {
    --zk-control-height-md: 32px;   /* only the rung, only on body */
}
```

Intuitively you'd expect inputs to follow to 32px. **They don't.** Because:

- This only changes `--zk-control-height-md` on `body`.
- `--zk-input-height` is **declared only on `:root`**; `body` never re-declares it.
- With no re-declaration on `body`, its `var()` formula is not re-substituted there.
  `--zk-input-height` is still the **frozen 40px** it resolved to on `:root` and
  inherited down.

So components read `--zk-input-height` = **40px** and the input never shrinks.

> **That is the freeze:** a semantic alias resolved to a concrete value on `:root` is
> "frozen". Overriding only the rung on a descendant does not re-compute the alias —
> unless that alias is also re-declared on the same element.

This is exactly why an earlier `/usecase` demo that toggled density on `body` left the
inputs full-size: everything reached *through* the alias layer (inputs, toolbars, tabs,
headers, FAB) stayed frozen.

### Two correct fixes

1. **Override on the element that declares the alias** — i.e. `:root` / `<html>` itself.
   Rung override and alias declaration share an element, so the alias re-substitutes.
   This is why a `:root`/`html` rule works,
   and why the demo only behaved once the class moved to `document.documentElement`.
   **Limit: whole-app only — it cannot scope to a region.**

2. **Override the alias components actually read** (the semantic layer), with a literal
   value — not the underlying rung. Since you are overriding the very token components
   consume, a descendant override always takes effect, **at any nesting level**. This is
   what the shipped rule does, and it is what makes both whole-app and per-region density
   fall out of the *same* rule with no extra work.

### Why not `:has()` or a JS toggle in the core theme

A `:has()` switch or a JS `classList` toggle both assume a specific toggle widget exists
on screen. The theme shouldn't assume that. The theme's job is to ship a **declarative
knob** (the `data-density` attribute); *how* it gets set is the application's call:

- hard-coded in the HTML attribute,
- emitted by the server during render,
- the application's own JS,
- the application's own pure-CSS `:has()` switch (e.g. `:root:has(#my-switch:checked)`).

That keeps the contract framework-agnostic and genuinely reusable.

### Components that read a rung directly

A few component files read a **leaf rung** directly rather than through a semantic alias. List them with
`grep -l "var(--zk-control-height-" zul/src/main/resources/web/js`.

The concern was that if `[data-density="compact"]` overrode only the semantic aliases,
these would not shrink under a region scope. The fix: the `[data-density="compact"]`
block **also re-points the leaf rungs as literals** (not only the aliases). A rung is a
literal, not a `var()`-derived value, so it is immune to the freeze problem — wherever the
block is mounted, any "reads-rung-directly" component in that subtree picks up the override.
So all of them shrink correctly under both whole-app and region scope, with no re-pointing.
Because the 3D-mold groupbox header reads the `md` rung, a customer override of
`--zk-groupbox-header-height` changes only the default-mold header, not the 3D one.

### One-line summary

> `var()` resolves to a concrete value on the element that declares it, and freezes there;
> changing only the rung it depends on, on a descendant, does not re-compute it.
> So density overrides must either sit on the declaring element (`:root` — whole-app only),
> or override the layer components actually read (the semantic aliases — any scope,
> region-capable). The shipped rule does the latter, listing rungs and aliases as literals.
