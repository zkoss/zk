# Cascade layers

Order, declared once in `zul/css/_layer-order.css` — the first entry of `normFiles`, so the first
file in `zk.wcs`. It sits at the CSS root, not in `base/`, because `assertLayer()` requires every
`base/` file to wrap its rules in `zk-base`:

```css
@layer zk-base, zk-components, zk-utilities;   /* lowest → highest */
```

It is **not** in `_reset.css`. `reset.css` loads ahead of `zk.wcs` but holds only `zk-base`, the
lowest layer, so it cannot change the order. With the statement there instead, a page that never
loaded `reset.css` (a custom `ThemeProvider` that does not insert it, a failed request) got
`zk-utilities` first from `zk.wcs` — utilities became the *weakest* layer and every `z-text-*` /
`z-bg-*` / `z-p-*` silently lost to component CSS. `reset-scoping.spec.ts` guards this by aborting
`reset.css` and asserting `.z-p-0` still beats the button padding. Do not add an order statement
to any source file.

The spec is `doc/spec/layer-architecture-review.md`. This page is the operating knowledge.

## Layers are declared in source, not injected

Each file wraps its own rules: component CSS in `@layer zk-components { … }`, the reset and the
base icon / badge / chip / avatar rules in `@layer zk-base { … }`, utilities in
`@layer zk-utilities { … }`. `build-css.js` **does not add layers** — it concatenates and then
runs `assertLayer()`, which **fails the build** if a component or base file forgot its wrapper.
Generated Lucide icon CSS self-wraps in `zk-base` inside its generator.

Layers were briefly injected at build time and moved back into source for readability. Keep them
in source.

## Reset rules must be inside the `zk-base` block

Left unlayered, `a { color }`, `::-webkit-scrollbar`, `img` and `.z-page` beat every layered
component rule — because **unlayered author CSS outranks every layer**. This bit once (the
"reset-floor" fix). The reset *rules* go inside `@layer zk-base { }`.

## What is deliberately unlayered, and why that is load-bearing

Tokens, the tablet stylesheet, `_cssflex` / `_dnd`, and the customer's own CSS all stay
unlayered — so a customer override wins without `!important`, which is the point of the design.

The same rule is a hazard in the other direction. **Any framework CSS that reaches the page
unlayered beats the theme's layered components.** Marble is a *complete* CSS replacement: in zk there is no icon-font CSS to suppress — the
IceBlue-only `font-awesome.css.dsp` stub the template ships for it was removed in P1 (D39) — and
Marble draws icons itself via `mask-image` and generated `.z-icon-* { --_icon }` rules from
`lucide-static` in `base/_icons.css`. That "no coexisting unreachable framework CSS" property is
what makes the layer scheme safe.

**On the next ZK upgrade** — the version that deprecates Font Awesome for native Lucide — re-check
whether ZK now emits a Lucide stylesheet the theme does not stub. If it does, it loads unlayered,
beats `zk-components`, and forces `!important` back. Either stub it or give it a layer; then
update the stale "ZK85Icons, FontAwesome" comment and `font-family` reset in `_icons.css`, decide
whether the empty stub is still needed, and re-run the icon screenshots (checkbox, radio, listbox,
tree). Separate task; do not fold it into layer work.

## The minifier and `@layer` — a silent trap

**Lightning CSS rewrites the order statement rather than preserving it.** It emits the layer
*blocks* in declared order and leaves any still-empty names as a **trailing** placeholder:
`@layer zk-base, zk-components, zk-utilities;` + `@layer zk-base {…}` comes back as
`@layer zk-base{…}@layer zk-components,zk-utilities;`. Semantics survive inside one file — but
anything that moves the statement *out of the minified output* gets the wrong subset.
`toEmbedReset()` once did exactly that and **inverted the cascade** for `reset-embed.css`. So never
extract or match the statement text in built output: the shipped `zk.wcs` has no
`@layer zk-base, zk-components, zk-utilities;` line, and that is correct. Check the order in which
layer names first appear (as `reset-scoping.spec.ts` does). The
earlier minifier (CleanCSS) had the opposite failure: a bare `@layer a, b;` emptied the *entire*
output at exit 0.

## Layers cannot beat inline styles — what `!important` is still for

When the `!important` count was reduced, the only removable ones were the 63 checkbox / radio icon
overrides in listbox and tree — they had beaten `_icons.css` purely because `zk-components` is
above `zk-base`, so the layer order made them redundant. The remaining ~33 are irreducible: they
beat **inline styles ZK's JS sets at runtime**, same-layer author conflicts, `prefers-reduced-
motion`, and add-on internals. A layer never outranks an inline style. Re-verify each survivor
empirically on any ZK upgrade; never trust a version-pinned comment (`reference/pitfalls.md` §11).
The repeatable method is `reference/important-reduction.md`.

## `browserDefault`: one reset source, two shipped files

`org.zkoss.zul.theme.browserDefault` (a `zk.xml` library property, default unset) exists for
customers who embed ZUL into their own styled page via the JS Embed API and do not want ZK's reset
touching host elements. IceBlue implemented it with a server-side DSP `<c:if>` that put a tag in
*selector position* — structurally impossible in plain CSS. Marble's build instead emits **two
files from one source**: `reset.css` (global) and `reset-embed.css` (wrapped in
`@scope (.z-page)`), and `MarbleThemeProvider` picks one. Spec: `doc/spec/reset-scoping.md`.

Two DOM facts bound what scoping can do. `.z-page` is the real rendered page root (emitted
client-side by the Page widget; ZK's own tests wait on it). And **every floating widget** —
combobox and bandbox dropdowns, menupopup, popup, tooltip, errorbox, notification, overlapped and
modal windows — is moved to `document.body` by `makeVParent()` when opened, so it lives **outside
`.z-page`** and a scoped reset never reaches it. Marble's component CSS already sets `box-sizing`
on the key float roots, so the scoped universal `box-sizing` mostly survives; float scrollbars and
`a` / `img` resets do not, by design.
