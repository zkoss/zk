# Brand-colour override

**The promise to the customer:** override **one** seed token, `--zk-color-primary`, and a
coherent palette derives from it — containers, overlays and the focus ring all follow. Before
this existed, the palette was hardcoded and a rebrand left every container fill stuck on the old
blue.

The customer-facing contract is `doc/spec/brand-override.md`. This file records *why* it is built
the way it is, so nobody re-opens the decision.

## The mechanism: `oklch()` relative colour, absolute tone

Nine derived tokens — the `container` / `on-container` pairs for primary, secondary, error and
warning, plus `inverse-primary` — are derived from their role seed using relative colour syntax:

```css
oklch(from var(--zk-color-primary) 0.92 c h)
```

This pins **lightness to an absolute tone** while inheriting the seed's chroma and hue. Seeds,
the `on-<role>` whites, neutral surfaces and the `status-*` badge colours stay literal.

## Why not `color-mix()` — this was a real fork in the road

The first shipped implementation used `color-mix(in srgb, <seed> N%, #fff|#000)` with per-role
ratios fitted to the legacy MD3 hexes by least-ΔE. It worked for the default blue and passed
contrast and visual regression. It was then replaced, deliberately.

`color-mix()` and `oklch()` are not competitors — **`oklch` is the better engine, `color-mix` is
the better syntax**, and there were three candidate designs, not two:

| Option | Recipe | Cross-hue consistency | Auto foreground | Verdict |
|---|---|---|---|---|
| A. `color-mix(in srgb …)` | `mix(seed 23%, #fff)` | ✗ lightness drifts with hue | ✗ | shipped first, then superseded |
| B. `color-mix(in oklch …)` | `mix(in oklch, seed 23%, #fff)` | ⚠ cleaner midtones, still ratio-based → lightness still drifts | ✗ | half a fix; not worth doing alone |
| C. `oklch(from seed L c h)` | absolute tone | ✓ any hue lands at the same perceived lightness | ✓ can pick `on-*` by tone | **adopted 2026-07-07** |

**The reason this matters is the promise itself.** A fixed 23% mix applied to a dark blue and to a
pale yellow produces containers of visibly different lightness, because an sRGB ratio mix
preserves the seed's own lightness contribution. The ratios had been calibrated against the
*default blue*; a customer seeding teal or amber would get a skewed ramp. Absolute tone sidesteps
that entirely — "container = tone 92, on-container = tone 20" holds at every hue. It is a scaled-
down HCT/MD3 tonal-palette model, and the natural bridge to a full seed→palette generator if one
is ever built.

Option C also lets `on-*` foregrounds be derived by tone, which is what would remove the
mid-to-dark-seed caveat below.

## Known limits

- **Solid-fill-only contrast.** The derivation guarantees contrast for solid fills. Light seeds
  may still require the adopter to override `--zk-color-on-primary` by hand — the caveat is
  stated in the customer spec.
- **Warning containers lean slightly orange** because the seed hue is preserved rather than
  corrected.
- **Verified once, empirically, and worth re-verifying after any minifier change:** the relative
  colour syntax survives minification, cross-hue containers land at a consistent L ≈ 0.92, and
  every `on-`/container pair clears WCAG AA. Minifiers corrupt this class of syntax silently —
  see `reference/pitfalls.md`.

## When you change any of this

Overriding the seed shifts the default container tints, which **will** fail the Playwright visual
baselines for the pages that show containers (alerts, selected rows). That is expected; it needs
a deliberate baseline-refresh pass, not a tolerance bump.
