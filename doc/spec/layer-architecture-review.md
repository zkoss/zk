# CSS `@layer` Architecture — Review & Cascade Model

Durable record of the theme's CSS cascade-layer design: why it exists, how it is built, and how
downstream users customize it. Supersedes the ad-hoc layer notes; pairs with
[reset-scoping.md](reset-scoping.md).

## Why (the problem this solved)

Originally the theme declared `@layer zk-components, zk-utilities;` in `_reset.css` and wrapped only the
utility files in `@layer zk-utilities`. **Component CSS and the reset were left unlayered.** Because unlayered
rules outrank any layer, the declared intent ("utilities win over components") was inverted in practice:
`sclass="z-p-3"` on a `<button>` silently did nothing, and downstream users had no layer-based guarantee that
their CSS could override the theme without `!important`. The `zk-components` layer was declared but empty.

## The model (as built)

Cascade layer order, lowest → highest priority, declared once in
[`_layer-order.css`](../../zul/src/main/resources/web/zul/css/_layer-order.css), the first entry of
`norm.css.dsp` and so the first file in `zk.wcs`. It is not in
`reset.css`: that sheet holds only `zk-base`, so loading it first cannot change the order, and a page that never
loads it (a custom `ThemeProvider`, a failed request) still keeps utilities above components:

```
@layer zk-base, zk-components, zk-utilities;
```

| Layer | Holds | Assigned |
|---|---|---|
| `zk-base` | reset/normalize rules + base icon/badge/chip/avatar styles + generated `.z-icon-*{--_icon}` | in source (Lucide block emitted by build) |
| `zk-components` | every `js/**` component CSS, combo/footer component bundles, moldOnly (notification/toast/captcha/misc) | in source (each file self-declares) |
| `zk-utilities` | the 8 `zul/css/utility/_*.css` files | in source |
| *(unlayered)* | design tokens (`:root` vars), tablet overrides, `_cssflex`/`_dnd` runtime classes, and **all downstream USER CSS** | — |

Priority (low → high): `zk-base` < `zk-components` < `zk-utilities` < **unlayered** < inline.

Consequences (all verified by computed-style probe):
- **Utilities override components** — `z-p-3` on `.z-button` changes padding (16px→12px). ✓
- **Components override base** — the checkmark `::before` override (zk-components) beats `_icons.css`'s generic
  mask rule (zk-base): `mask-image:none`, real border box. ✓
- **Unlayered USER CSS beats every theme layer with any specificity and without `!important`.** This is the headline
  win for customization.

## How it's built

Each source CSS file **self-declares** its cascade layer (block form `@layer name { … }`, which CleanCSS handles
correctly — the header comment is kept above the `@layer` line for readability). `scripts/build-css.js` does **not**
inject layers; it concatenates the source and **verifies** the wrapper is present:

- `assertLayer(relPath, content, layer)` runs on every component (`zk-components`) and base (`zk-base`) source file
  during the build. A file that forgets its wrapper **fails the build** instead of silently shipping unlayered
  (where it would beat utilities + user CSS). This guard is what makes source-level layering robust — fire-tested.
- The reset rules carry their own `@layer zk-base { … }` block in `_reset.css`; the file has no order statement.
  Lightning CSS does not keep the order statement verbatim — it emits the layer blocks in the declared order
  instead, which declares the same order — so tests check the order layer names first appear in `zk.wcs`, not
  the statement text. The embed variant (`toEmbedReset`) just scopes that block to
  `.z-page` via `@scope` — it adds no layer of its own. The reset MUST be in `zk-base` — unlayered it would beat the
  layered components (`a{color}`, `::-webkit-scrollbar`, `img`, `.z-page`).
- The generated Lucide `.z-icon-*{--_icon}` rules are emitted by the build (not a source file), so the generator
  wraps its own output in `@layer zk-base { … }`.
- Tablet CSS and `_cssflex`/`_dnd` stay unlayered by design: tablet must override desktop components on mobile, and
  the ZK-JS-toggled `z-flex`/`z-dragged` classes must win when applied.

## The three review questions

1. **Is every override-layer correctly defined?** Yes now. `zk-base`, `zk-components`, `zk-utilities` are all
   populated and ordered; the previously-empty `zk-components` holds all component CSS.
2. **Does `!important` cause override difficulty?** There are 0 `!important` inside layers (clean). The original 109
   in component CSS are being reduced now that the layer order does the work: **Phase 2a removed the 63
   checkbox/radio mask-icon overrides** in `listbox.css`/`tree.css` — they win over `_icons.css` purely because
   `zk-components` outranks `zk-base`, so `!important` was redundant (verified rendering-neutral by computed-style
   probe + HEAD A/B). The ~46 that remain are predominantly **irreducible**: they neutralize ZK-JS-set **inline**
   styles (splitlayout buttons, tablet popups, modal `.z-mask`, rotated borderlayout titles, collapsed
   panel/groupbox heights) — no cascade layer can beat an inline style — plus `prefers-reduced-motion` resets and
   add-on internals. The small tail (menu separator, messagebox) is **also irreducible** — those are same-layer
   (`zk-components`) author-vs-author conflicts where the layer order gives no help; they correctly keep `!important`.
   Net: the icon overrides were the only cluster the layer order made redundant.
3. **Can users customize without `!important`?** Yes, two clean paths: (a) override `--zk-*` tokens at `:root`
   (tokens are unlayered, so a later `:root` wins by source order); (b) write any unlayered CSS — it beats every
   theme layer regardless of specificity, no `!important` needed. The only exceptions are the remaining 109
   `!important` rules.

## Customization guidance (downstream)

- **Rebrand / resize:** redefine `--zk-*` tokens at `:root` in a stylesheet loaded after the theme. No `!important`.
- **Override a component rule:** write plain (unlayered) CSS — it wins over all theme layers with any specificity.
- **The `!important` hotspots** (checkbox color/size, tablet popups, splitter button, modal mask) still require
  matching them until the reduction pass exposes token hooks.

## Verification notes

- Phase 1 (this layering) was verified **rendering-neutral**: a HEAD-vs-change A/B run produces the identical set
  of screenshot results (same diffs), and the cascade behavior was confirmed by direct computed-style probes.
- **Caveat — stale screenshot baselines:** the committed baselines under `doc/screenshots/` no longer match even
  HEAD's CSS (e.g. textbox gallery differs by 1390px on *both* HEAD and this change). The screenshot suite is
  therefore not currently a valid pass/fail gate; regenerate baselines (`npm run screenshot:update`) before relying
  on it. This is pre-existing maintenance debt, unrelated to the layer work.

## Forward note — ZK Font Awesome → Lucide

The next ZK version this theme targets deprecates Font Awesome for native Lucide. The layer topology is unaffected,
but the "no coexisting unreachable framework CSS" property depends on the theme stubbing whatever icon CSS ZK ships
(today: an empty `font-awesome.css.dsp` stub). On that upgrade, re-check ZK's Lucide stylesheet, refresh the
`_icons.css` FA references, and re-run icon screenshots.
