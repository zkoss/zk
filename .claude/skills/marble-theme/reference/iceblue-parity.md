# IceBlue parity and the `--zk-*` public API

**The fact that governs everything here:** IceBlue's `--zk-*` properties are **not internal
implementation — they are a documented public API** since ZK 10.3.0. The official page
`DOC/zkdoc/zk_style_customization_guide/css_variables.md` (tagged
`supported-since version="10.3.0"`) promises:

> "ZK provides hundreds of CSS custom properties for easy theming and customization **without LESS
> compilation**." … "**Override ZK CSS variables in your own CSS file**"

So those names are a product promise, not a detail. And 10.3.0 is *recent* — replacing it in 11.0
would mean shipping a customization API and retiring it one version later. The damage to trust
exceeds the technical breakage.

## The measured break

| Metric | Value |
|---|---|
| IceBlue `--zk-*` total | **842** |
| Names Marble also has | **12** |
| **Retention** | **1.4%** |
| Gone — old overrides silently stop working | **830** |
| New in Marble | 606 |

The 12 survivors are mostly coincidence rather than design: `--zk-color-primary`,
`--zk-input-height`, `--zk-input-border-color`, `--zk-popup-border-color`,
`--zk-cascader-border-color`, `--zk-searchbox-border-color`, `--zk-signature-border-color`,
`--zk-messagebox-icon-size`, and four `--zk-rangeslider-*`.

**The sharpest evidence is the official documentation's own example.** It uses 7 tokens; **6 do
not exist in Marble** — `--zk-base-border-radius`, `--zk-color-primary-dark`,
`--zk-border-radius-small`, `--zk-border-radius-large`, `--zk-base-title-font-family`,
`--zk-base-font-size`. A customer who followed the docs gets **silent** failure on upgrade: no
error, nothing applies. That is the worst failure mode there is.

**At the component layer it is worse than a naming-style difference.** `--zk-button-*`: IceBlue
19, Marble 14, **overlap 0**. Both describe the same thing — a button's colours, borders, radius —
and not one name matches. That is not an inevitable consequence of MD naming; it is simply
alignment work that was never done.

## Why "just use MD names everywhere" is wrong

1. **What breaks is a promise, not an implementation.** A major version may break things, but the
   legitimacy comes from *a better replacement* plus *a migration path*. Right now: 830 names gone,
   no mapping table, no aliases, and silent failure. All three missing.
2. **It welds a design language into a framework contract.**
   `--zk-color-surface-container-highest` presupposes MD3's tonal-elevation model. Build a Fluent-
   style or bespoke enterprise theme and that name is either meaningless or misleading. **A
   framework's variable vocabulary should not be bound to any one design language.**
3. **It destroys cross-theme portability.** An override a customer writes for Marble should *partly*
   survive a move to a non-MD theme — "make the primary colour corporate red" holds in any theme.
   If every name is MD vocabulary, that portability is zero and every theme switch is a rewrite.

## The three-tier model

Classify each token by **whether the concept is bound to a specific design language**, and give
each tier a different stability promise.

| Tier | What it is | Naming | Promise |
|---|---|---|---|
| **0 — framework-neutral contract** | ZK's own vocabulary; true in any theme | **Keep IceBlue's existing names** | Stable across major versions. This *is* ZK's API |
| **1 — design language** | Concepts only this design language has | MD naming, freely | No cross-theme existence promise |
| **2 — component `--zk-<comp>-*`** | Fully neutral ("a button's background" exists in every theme) | Should be **consistent across themes** | The worst-aligned layer today, and the one most worth fixing |

**The one-line test:**

> *"After switching to a Fluent Design theme, does this name still make sense?"*
> Yes → Tier 0 or 2, use ZK-neutral naming. No → Tier 1, MD naming is fine.

Tier 0/2 protect upgrade smoothness and cross-theme portability; Tier 1 keeps MD3's expressive
precision without diluting it for the sake of neutrality.

### Per-family verdicts

| Family | Tier | Action |
|---|---|---|
| `--zk-color-{primary,secondary,error}` | 0 | Keep; `primary` already matches. **Restore** IceBlue's `-dark`/`-light`/`-lighter` as aliases |
| `--zk-color-{success,warning}*` | 0 | Keep (IceBlue has none → pure addition) |
| `--zk-color-on-*` | 0 | Keep; **add** `--zk-text-color-*` aliases for old users |
| `--zk-color-surface-container-{lowest…highest}` | **1** | MD3 tonal elevation — keep MD naming; a non-MD theme can collapse them all to one background |
| `--zk-color-{outline,outline-variant}` | 0 | Keep; alias `--zk-base-border-color` |
| IceBlue's `accent*`, `background1/3`, `grey-*` | 0 | **Must be kept as aliases** |
| `--zk-spacing-N` | 0 | Keep (4px grid is industry convention, not MD-specific; IceBlue has none → zero breakage) |
| `--zk-shape-corner-*` | 0 (MD-ish name) | Keep the name **and** alias IceBlue's 3 radius names — renaming costs more than it returns |
| `--zk-shape-{button,input,card,dialog,menu}` | 2 | Keep — this is the good pattern |
| `--zk-typescale-*` | **1** | Keep MD naming, **but** retain `--zk-base-font-size` + `--zk-font-size-{x-small…x-large}` as neutral Tier-0 entry points |
| `--zk-elevation-{0..5}` | 0 | Keep (IceBlue scatters 19 per-component shadows → consolidate the other way) |
| `--zk-state-*-opacity` | **1** | Keep, **but** retain IceBlue's `--zk-{hover,focus,active,disabled,selected,checked}-*` as Tier-0 entry points |
| `--zk-motion-*` | **1** | Keep (IceBlue has no equivalent → zero breakage) |
| `--zk-index-*` | 0 | Keep; alias `--zk-base-popup-z-index` |
| `--zk-<comp>-*` (~400) | 2 | ⚠️ **The work that matters**: compare family by family against IceBlue's names and reuse wherever possible |

The proposed compatibility layer is **one file** of aliases for the ~78 highest-traffic globals.
Cheap, and it buys "an existing customer's override still works".

## Important context that changes the framing

**Marble and IceBlue are two *parallel* theme choices, not a migration path.** ZK 11.0 makes
Marble the default and splits IceBlue out as a separately selectable theme so legacy apps upgrade
unchanged. Under that ruling **Marble owes IceBlue's vocabulary no compatibility** — which
dismissed three earlier decision items premised on it owing some. The 830 "dead" names are a
*different vocabulary*, not a broken API, and FA-dependent apps stay on IceBlue.

Read the analysis above as the case for *voluntary* alignment where it is cheap (Tier 2 especially),
not as an obligation.

## Porting Marble's utility CSS back to IceBlue

Assessed 2026-07-28: technically feasible, but **not "copy 9 CSS files"**. Two blocking
prerequisites, both of which are really naming/foundation work rather than build work:

1. **The utility layer references 89 tokens; IceBlue has 1 of them** (`--zk-color-primary`). The
   whole spacing / shape / typescale / elevation / z-index / semantic-colour foundation has to go
   first.
2. **IceBlue has no `@layer` at all** (measured: 0 occurrences). Marble's utilities rely on
   `@layer zk-utilities` sorting last to beat component styles. Dropped into IceBlue without a
   layer mechanism, `.z-p-3` and `.z-button` have identical specificity `(0,1,0)` and the utility
   sits in the **earliest-loaded** `norm.css.dsp` → **component styles win and the whole utility
   set silently does nothing.**

A third suspected blocker was **eliminated by measurement**: `zkless-engine` compiles modern CSS
fine — `@layer`, `@container`, `@scope` and `oklch(from …)` all pass through unchanged. The one
prerequisite is bumping its `less` dependency from `^3.13.1` to `^4.x`: **LESS 3.13.1 silently
miscompiles 9 places** (the brand-colour derivation tokens, `z-grid-fill`, `grid-column: 1 / -1`)
while the build still exits 0.

## Theme Pack palettes

The 27 Theme Pack palettes are already pure `:root{--zk-*}` CSS (722 lines total). They reference
**110 IceBlue token names of which exactly 1 exists in Marble**. CSS ignores unknown custom
properties, so they compile, deploy, and **no-op**. They travel with IceBlue, because they are
written in its vocabulary.

This is also the mechanism behind a live build hazard: `zkcml/.gitmodules` points
`zkthemebuilder/template` at the `zkThemeTemplate` repo and `zkthemebuilder/build.sh:59` runs
`git submodule update --init --remote`, which tracks the **default branch**. The moment Marble
lands on that repo's `master`, the next `./build.sh -u` feeds a pure-CSS tree into a Maven +
zklessc pipeline and all 27 palettes break — later, and for someone else. The fix is to drop
`--remote` and point the submodule at the `iceblue` branch.
