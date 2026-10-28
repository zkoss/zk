# Tokens

All custom properties are `--zk-*`. `--md-sys-*` is banned even though the *values* follow MD3.

## Three naming schemes coexist, and that is correct

A recurring question is whether every hierarchical scale should use numbers. **No — there are
three schemes, and each one faithfully mirrors MD3's own naming for that category.**

| Family | Scheme | Members | MD3 uses the same? |
|---|---|---|---|
| `spacing` | numeric (× 4px) | `0 1 2 3 4 5 6 7 8 10 12 16` | yes — the 4dp grid, industry convention |
| `elevation` | numeric (= level) | `0`–`5` + semantic aliases | yes — `level0`–`level5` |
| `motion-duration` | word + number | `short1-4 / medium1-4 / long1-4` | yes |
| `motion-easing` | word (variant, not degree) | `standard / emphasized / legacy` | yes |
| `shape-corner` | word (t-shirt) | `none … extra-large / full` | yes |
| `typescale` | word (role + t-shirt) | `display/headline/title/body/label` × `large/medium/small` | yes |

MD3 deliberately uses different conventions because the scales have different *shapes*: elevation
and motion are dense ordinal ladders (numbers); shape and typescale are semantic tiers (words).

**Do not homogenize them.** Forcing everything numeric would diverge from MD3, touch ~1,100
reference sites, and buy nothing. This was evaluated and rejected.

### The one real inconsistency, and how it was fixed

`motion-duration` was non-monotonic: `short3` had been pulled to 250ms to serve as the de-facto
standard transition (156 references), which made `short3 > short4` and duplicated `medium1`.

Resolved value-preservingly (commit `b5cf6e5`): a semantic alias
`--zk-motion-duration-standard: var(--zk-motion-duration-medium1)` (= 250ms) was added, all 156
references moved to it, and `short3` was restored to MD3's correct **150ms** — leaving a
zero-reference but correct ladder member, kept under the scale-completeness rule.

**Two rules that fall out of this:**
- **Scale completeness beats reference counting.** A zero-reference member of a systematic ladder
  stays. `long3`/`long4` were added for symmetry for the same reason; `extra-long` was *not*,
  because the theme has no full-screen transition use case and it would be pure dead code.
- **When a token's value is wrong but its references want the old value, add a semantic alias**
  rather than changing the value under 156 call sites.

`spacing`'s numbers are non-contiguous (`0..8` then `10 12 16`). That is fine — the number is the
×4px multiplier, the upper range is just sparser. Industry convention. Do not "fix" it.

## Component theming variables (the per-component knobs)

Marble exposes `--zk-<component>-*` properties so an adopter can restyle one component without
overriding a global token that would ripple everywhere. 18 component families declare them;
the normative spec is `doc/spec/component-theme-variables.md` and `component-theme-variables.spec.ts` covers
it with per-component tests that run on each component's own preview page.

**Call them "Component Theming Variables", and use "knob" as the informal shorthand.** They were
once called an "API", which is a stretch: there is no imperative surface, it is purely declarative
CSS. Reserve "API" for `MarbleBrand` / `MarbleDensity`, which are genuine Java runtime APIs with
a call surface. The industry term closest to this feature is Salesforce's "styling hooks"; MDN,
Shoelace and Ionic simply call them CSS custom properties.

What makes the set a contract rather than just variables:

1. **Naming** — `--zk-<comp>-*`, fixed.
2. **Defaults** — every knob's default reproduces the stock appearance exactly, so declaring the
   knob is a zero-regression change.
3. **Declaration site and cascade** — declared at `:root`; region overrides and load-order rules
   are specified.
4. **Stability** — no silent renames across versions.

**Two scoping axes, and the second one is the point:**
- *Component scope* — set at `:root`, only that component type changes.
- *Region scope* — set the knob on a container, and only that subtree changes. "Restyle the
  buttons in this one toolbar" without forking component CSS or fighting `!important`. The test
  suite asserts the sibling is untouched.

**The default chain matters:** each knob defaults to its global token
(e.g. `--zk-tab-bg: var(--zk-color-surface)`), so a brand seed change still flows through. A knob
is an *additional, narrower* override point — never a replacement for the global token.

## Token override cascade — the trap

A component-local `--zk-*` declaration **shadows** a `:root` override, silently defeating the
customer's customization. When a token is meant to be overridable, **hoist its declaration to
`:root`**. This was found for real: `--zk-grid-cell-padding`, `--zk-listbox-cell-padding` and
`--zk-tree-cell-padding` were declared on the component and had to be hoisted before the compact
preset could reach them.

Corollary: **tokenize padding, not just `min-height`.** Window and panel headers are sized by
their 16px vertical padding, not by a height token, so a height-only ladder cannot compress them.
`--zk-header-padding-y` exists for exactly that reason.

## The z-index scale

The governing principle is **do not fabricate a ladder**. ZK's runtime assigns floating widgets a
base of **1800** and rewrites `z-index` inline, so most cosmetic float values in CSS only apply
for a single pre-paint moment. `tokens/_zindex.css` therefore tokenizes only the values that CSS
genuinely decides, at their existing values (zero regression):

| Token | Value | Owner |
|---|---|---|
| `--zk-index-nav` | 1000 | CSS |
| `--zk-index-loading` | 1450 | CSS |
| `--zk-index-loadingbar` | 2000 | CSS |
| `--zk-index-slider-tooltip` | 60000 | CSS |
| `--zk-index-busy-mask` | 89000 | CSS |
| `--zk-index-busy-loading` | 89500 | CSS |
| `--zk-index-fullscreen` | 99999 | CSS |
| `--zk-index-error` | 9999999 | CSS |
| `--zk-index-float-fallback` | 1800 | = ZK runtime base; an honest fallback for widgets ZK overrides inline |

**Three hard constraints — any change must preserve all three:**
1. `busy-mask` (89000) < `busy-loading` (89500)
2. `loading` (1450) > `.z-modal-mask` (1400) — ZK's JS reads back `.z-loading` and computes its
   paired mask as `z − 1`
3. `error` (9999999) outranks everything

**Deliberately out of scope:** the cosmetic 1400–1700 floats (window, popup, combobox, datebox,
menupopup, toast, drawer) keep their literal CSS values — ZK overrides them at runtime, so
rewriting 12 files would change nothing visible. The `.z-index-*` utilities in
`utility/_layout.css` expose only the semantic rungs plus `float-fallback`, never the magic
numbers. Two dead values were made honest instead of deleted: searchbox's `88000` became
`var(--zk-index-float-fallback)`, and drag-ghost's `90000` kept its value with a comment noting
ZK's `zk/drag.ts:654` overrides it to 88800 during the drag.

## Related

- `reference/brand-override.md` — how one seed cascades into a palette
- `reference/density.md` — the control-height ladder and the semantic alias layer
- `reference/iceblue-parity.md` — why IceBlue's 842 `--zk-*` names are a different vocabulary

## There are no `--zk-font-weight-*` tokens

Font weight is expressed two ways, and only two: typescale-bundled weights in
`tokens/_typography.css` (`--zk-typescale-title-medium-weight: 500`; the maximum defined is 500),
and literal values through the `.z-fw-*` utilities (`light` 300, `regular` 400, `medium` 500,
`semibold` 600, `bold` 700). Write `font-weight: 500` or the typescale weight token. A contract
once cited `--zk-font-weight-medium` and the evaluator flagged the theme for a missing token; it
was a contract authoring error, not a gap. Do not create the family to make such a contract pass.

## What counts as a token consumer

When deciding whether a `--zk-*` token is an orphan, **"referenced" means referenced by component
CSS under `zul/src/main/resources/web/`.** A hit from a preview or demo ZUL — especially an inline
`style="…var(--zk-token)…"` — is **not** a consumer; it is theme debt. Fix the ZUL to use a
built-in utility, then delete the token. Self-contained `:root` snapshots inside
`doc/contracts/*.html` are independent copies and do not block deletion either. Graduated scales
(elevation 0–5, shape corners, spacing, typescale, motion) are kept complete regardless of
reference count — see "scale completeness" above.

## The `z-` prefix is for framework component variants only

`z-` on a class name signals a **framework-shipped component variant or state** applied via
`sclass` — `z-badge-success`, `z-avatar-sm`. Page-level helpers (layout, demo scaffolding) do not
take it, whichever file they happen to live in. When auditing for naming violations, flag only
classes that act as a variant, skin or state of a ZK component; leave page-level utilities alone.

## Same value in several places is not duplication when the roles differ

The theme version lives in `pom.xml`, `config.xml`, `package.json` and `Version.java`. They are
not four copies: each declaration has its own job (artifact naming, ZK's startup comparison, the
value both XMLs are compared against), and collapsing them would delete independent declarations,
not redundancy. **State each occurrence's role before proposing to dedupe.** When the occurrences
are not interchangeable, the right shape is *one writer plus one guard* — a single command that
sets all of them and a check that they agree — not *one source of truth*. That guard does not
exist yet (`reference/css-dsp.md`, last section).
