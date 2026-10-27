# Component Theme Variables

> **Feature name:** *Component Theme Variables* (CTV). The individual `--zk-<comp>-*` custom
> properties are the theme variables; this doc also calls a single one a **knob** informally.
> This is a **declarative CSS custom-property** surface — there is **no** JS/Java call API (that
> role belongs to `MarbleBrand` / `MarbleDensity`).

Marble's tokens recolor the *whole* theme coherently (see [brand-override.md](brand-override.md))
and resize it coherently (see [data-dense-mode.md](data-dense-mode.md)). But enterprise
adopters also routinely need to restyle **one component** — "make *our* buttons pill-shaped",
"give the grid header our brand tint" — without forking the theme or fighting it with
brittle `!important` overrides. Marble exposes this as **per-component theme variables**:
each themed component reads a small, curated set of `--zk-<comp>-*` custom properties. Set
them at `:root` (whole app) or on any container (one region) and only that component changes.
No forking, no build step, no recompilation.

This is the same mechanism the theme already uses internally for control *sizes*
(`tokens/_sizing.css`) and for the splitter family (`tokens/_splitter.css`); Component Theme
Variables generalize it to the **appearance** dimension (color / border / radius / state) and
document it as a public, supported surface.

> **Progress tracking:** conformance of each component against the criteria below is tracked
> separately in [`../../tasks/component-theme-variables-progress.md`](../../tasks/component-theme-variables-progress.md).

## How it works

Knob defaults live in [`tokens/_component-theme.css`](../../zul/src/main/resources/web/zul/css/tokens/_component-theme.css),
declared **unlayered** at `:root`, and are bundled into `norm.css.dsp`:

```css
:root {
    --zk-button-bg:     var(--zk-color-primary);
    --zk-button-radius: var(--zk-shape-button);
    /* … */
}
```

Each component consumes the knob from inside `@layer zk-components`:

```css
@layer zk-components {
.z-button {
    background-color: var(--zk-button-bg);
    border-radius:    var(--zk-button-radius);
}
}
```

Because every default equals the value the component used before, **declaring nothing
changes nothing** — Component Theme Variables are purely additive and render-neutral until an
adopter opts in.

### Where knob defaults live — centralized, not per-component

All knob defaults are declared in the single central file `tokens/_component-theme.css`,
never inside a component's own CSS. This follows the established project convention: the
stock ZK theme (this repo's LESS-based `master` branch) declares **all** ~1,700 of its
`--zk-*` custom properties — global *and* component-specific alike — in one central `:root`
profile (`zul/less/profiles/_default.less`, with `_compact.less` for density) and **zero**
inside any component `.less`. Marble keeps that discipline, only splitting the central defs
by category (`_colors.css`, `_sizing.css`, `_splitter.css`, …); the component appearance
knobs are one more such file — the sibling of `_sizing.css`, which already centralizes every
component's *size* knob in one file. Authoring rule 1 is the other reason: a default declared
on the component element would shadow `:root`/region overrides for that element's subtree.

> A component *may* still declare a `--zk-<comp>-*` on a **variant/modifier** selector to
> re-parameterize its base rule — e.g. `.z-chip-info { --zk-chip-bg: … }`,
> `.z-avatar-small { --zk-avatar-size: … }`. That is a private *variant* mechanism, not an
> adopter-facing default, and is overridable only per-instance (inline) — not the whole-app +
> region contract Component Theme Variables define. To bring such a component fully in, hoist its
> base default into `tokens/_component-theme.css`.

## Overriding

### Whole app — at `:root`

```css
/* adopter stylesheet, loaded AFTER the Marble bundle */
:root {
    --zk-button-radius: 9999px;   /* every button becomes a pill */
    --zk-button-bg: #6750a4;
}
```

### One region — on any container

Custom properties inherit, so setting a knob on an ancestor restyles only that subtree.
This is the common case and needs no special load order:

```xml
<div style="--zk-button-radius:9999px; --zk-button-bg:#6750a4; --zk-button-fg:#fff">
    <button label="Pill"/>          <!-- restyled -->
</div>
<button label="Default"/>            <!-- untouched — sibling, not a descendant -->
```

There is no dedicated demo page. Each component's own preview page (for example `button.zul`) shows its
default look, and the knob contract is regression-tested on those pages in
[`component-theme-variables.spec.ts`](../../zkpreview/src/test/playwright/component-theme-variables.spec.ts).

## Cascade & load-order rules (important)

The knob defaults are **unlayered**, and components consume them from `@layer zk-components`.
Since unlayered rules always beat layered ones, the mechanics are:

- **Regional override (on an ancestor element)** always wins by *inheritance distance* — the
  nearest ancestor that sets the property governs its subtree, regardless of cascade layer or
  source order. No load-order concern.
- **Whole-app override (at `:root`)** competes with the theme's own `:root` default on the
  *same* element. Both are unlayered with equal specificity (0,1,0), so **source order
  decides** — the adopter's stylesheet must load **after** `norm.css.dsp`, **or** use a
  slightly higher-specificity selector. The theme's own brand presets already rely on this:
  `:root[data-brand="…"]` (0,2,0) beats the base `:root` (0,1,0). `html:root { … }` works too.

## Authoring rules

1. **Declare knobs only at `:root` (or an override scope) — never on the component element.**
   A `.z-button { --zk-button-bg: … }` declaration would *shadow* any `:root` or region
   override for the button's own subtree. (Same trap `tokens/_sizing.css` layer-3 documents.)
2. **Every default must equal the current value** — the exact token or literal the component
   used before — so adopting the knob is zero visual regression.
3. **Consume without a fallback** (`var(--zk-button-bg)`, not `var(--zk-button-bg, …)`): the
   default is defined once, at `:root`, keeping the knob discoverable in DevTools and avoiding
   a fallback that could drift from the `:root` default.
4. **Curate the surface.** Expose the themeable essentials (fill / text / border / radius / key
   states / size) — not every property. A knob is a supported contract; keep the set small.
5. **Typography stays global — it is not a component knob.** Font size / weight / line-height /
   family remain on the shared `--zk-typescale-*` scale. A component consuming
   `--zk-typescale-title-medium-*` (as `window` does for its title) is *correct*, not a gap:
   per-component type fragments the system's vertical rhythm, and every mainstream framework keeps
   typography a system token (MUI components reference typography *variants*; Ant font derives from
   global aliases; Chakra / Tailwind / Bootstrap use a shared scale). If a real need arises to
   restyle **one** component's type in isolation, expose a knob that *defaults to the typescale
   slot* — `--zk-window-title-size: var(--zk-typescale-title-medium-size)`, never a literal like
   `16px` — and do not add it speculatively. Rationale + framework survey:
   [component-theme-typography-scope.md](../component-theme-typography-scope.md).

### Relationship to brand override & density

| Want to… | Use |
|----------|-----|
| Recolor the **whole theme** from one seed | `--zk-color-primary` (+ role seeds) — [brand-override.md](brand-override.md) |
| Resize the **whole theme / a region** | `data-density` + `--zk-*-height` — [data-dense-mode.md](data-dense-mode.md) |
| Restyle **one component's appearance** | this feature's `--zk-<comp>-*` variables |

> **Freeze caveat.** Overriding an upstream *global* seed (e.g. `--zk-color-primary`) on a
> *region* will **not** reflow a knob whose default is `var(--zk-color-primary)` — that value
> was substituted at `:root` and inherited frozen. To recolor a region, override the component
> **knob** directly; to recolor the whole app, override the seed at `:root`.

## Conformance criteria (檢驗條件)

The purpose in one line: *let an adopter restyle **one** component — whole-app or in one
region — by overriding **only that component's** `--zk-<comp>-*` variables, **never** a shared
global token, and with **zero regression** when they override nothing.*

A component **conforms** when it satisfies every applicable criterion below. The
[progress tracker](../../tasks/component-theme-variables-progress.md) records each component's status
against these IDs. CTV-1…4 are the load-bearing *purpose* gates; CTV-5…9 are the
mechanics / hygiene / verification that keep them true.

| ID | Criterion | How to check |
|----|-----------|--------------|
| **CTV-1** | **Zero-regression default.** Every `--zk-<comp>-*` default resolves to the component's exact pre-existing value; overriding nothing renders identically to before the variables existed. | Default in `_component-theme.css` equals the old token/literal; computed style unchanged before/after. |
| **CTV-2** | **Isolation from global tokens (core purpose).** The exposed appearance properties are read *through* the component's own variables, so an adopter restyles them by overriding only `--zk-<comp>-*` — **never** a global `--zk-color-*` / `--zk-shape-*` / …. No exposed property still reads a global token directly, bypassing the variable. | Overriding the component variable changes only this component; the global token it defaults to and other components are unaffected. |
| **CTV-3** | **Region scoping.** Setting a variable on a container restyles only that subtree's instances; siblings outside are unchanged. | Holds by CSS inheritance (a custom property set on an ancestor reaches only its descendants), so no test re-checks it. Documented per-variant exceptions (e.g. severity-pinned chip/badge) are stated in the component entry. |
| **CTV-4** | **Whole-app override wins.** A `:root` override loaded after `norm.css.dsp` (or at higher specificity) wins for all instances. | Inject a `:root` override; assert it applies. |
| **CTV-5** | **Declared at `:root`, unlayered, not on the element.** Defaults live in `tokens/_component-theme.css` at `:root`; not declared on the base component element (which would shadow region overrides for its own subtree). Variant/modifier re-parameterization is allowed but must be documented. | Grep the component CSS: no base `.z-<comp> { --zk-<comp>-*: … }`; default present in `_component-theme.css`. |
| **CTV-6** | **Meaningful coverage.** The variables cover the component's salient appearance axes (as applicable: fill, text, border, radius, the defining state/accent) — enough for a recognizable restyle without forking. Size lives in `_sizing.css`; elevation may be intentionally mode-driven. | Review against the component's DOM + states; record deliberate exclusions. |
| **CTV-7** | **State integrity.** Disabled / readonly / selected / error treatments stay correct under an override (they don't accidentally inherit a fill/border variable and lose their state distinction) — unless a state is itself an intentional, documented variable. | Override the region; assert the disabled (etc.) instance keeps its treatment. |
| **CTV-8** | **Documented.** Variable names + defaults (+ scope/exceptions) are listed in this doc's per-family table. | This file's reference section has the entry. |
| **CTV-9** | **Regression-tested.** A test in `component-theme-variables.spec.ts`, run on the component's own preview page, reads the stock value, injects a `:root` override after the theme bundle and reads again. That proves CTV-1/2/4/5: a hardcoded value or a default declared on the component element would shadow the inherited `:root` value and fail the second read. | Test exists and is green. |

> CTV-6 and CTV-7 are judgment calls (what counts as "enough" coverage / a preserved state);
> the tracker records the rationale, not just a checkmark.

## Variable reference (per component)

### Button — shipped

Consumed by `.z-button` (filled base) + `.z-uploadbutton`. Color/outlined/text/icon/FAB
**variants** keep their own colors via higher-specificity rules; the disabled treatment is
intentionally not knob-driven. State is an MD3 `::before` overlay (a tint whose opacity
animates), so the knobs are an overlay color + per-state opacities — matching the real
implementation, not a per-state background.

| Knob | Default |
|------|---------|
| `--zk-button-bg` | `var(--zk-color-primary)` |
| `--zk-button-fg` | `var(--zk-color-on-primary)` |
| `--zk-button-radius` | `var(--zk-shape-button)` |
| `--zk-button-elevation` | `var(--zk-elevation-resting)` |
| `--zk-button-padding-y` / `-padding-x` | `6px` (`4px` under `data-density="compact"`, so every variant is exactly `--zk-button-height` = 30px) / `var(--zk-spacing-4)` |
| `--zk-button-overlay-color` | `var(--zk-color-on-primary)` |
| `--zk-button-hover-opacity` / `-focus-opacity` / `-active-opacity` | state-layer opacity tokens |
| `--zk-button-height` (size — in `_sizing.css`) | `36px` |

Their knob vocabularies differ by design because their **state models** differ: inputs drive
per-state `border-color`; window/grid swap `background-color`; only button uses the overlay
model.

### Input — shipped

Shared by `.z-textbox` / `.z-intbox` / `.z-decimalbox` / `.z-doublebox` / `.z-longbox` /
`.z-passwordbox` (outlined MD3 text field). State is a per-state `border-color` change (no
overlay). Readonly/disabled use the muted `outline-variant` treatment and are not knob-driven.

| Knob | Default |
|------|---------|
| `--zk-input-bg` | `var(--zk-color-surface)` |
| `--zk-input-fg` | `var(--zk-color-on-surface)` |
| `--zk-input-radius` | `var(--zk-shape-input)` |
| `--zk-input-border-color` | `var(--zk-color-outline)` |
| `--zk-input-border-color-hover` | `var(--zk-color-on-surface)` |
| `--zk-input-border-color-focus` | `var(--zk-color-primary)` |
| `--zk-input-border-color-error` | `var(--zk-color-error)` |
| `--zk-input-height` (size — in `_sizing.css`) | `var(--zk-control-height)` |

### Window — shipped

Consumed by `.z-window` + header. **Elevation stays mode-driven** (embedded/overlapped/modal →
`_elevation.css`, per [window-design-rules.md](window-design-rules.md)) and is intentionally
not a knob. The header shares the window surface fill.

| Knob | Default |
|------|---------|
| `--zk-window-bg` | `var(--zk-color-surface)` |
| `--zk-window-border-color` | `var(--zk-color-outline-variant)` |
| `--zk-window-radius` | `var(--zk-shape-corner-extra-small)` |
| `--zk-window-header-fg` | `var(--zk-color-on-surface)` |
| `--zk-window-icon-hover-bg` | `var(--zk-color-surface-container)` |
| `--zk-window-close-hover-bg` | `var(--zk-window-icon-hover-bg)` |
| `--zk-window-header-height` (size — in `_sizing.css`) | `56px` |

### Grid — shipped

Consumed by `.z-grid` / `.z-column` / `.z-row`. Curated to the grid "lines" + fills; secondary
accents (group/foot separators, legacy `.z-grid-striped`/`.z-grid-odd`-container fallbacks) stay
on the base tokens. `--zk-grid-row-hover-bg` keeps its stock `rgba` literal for zero regression.

| Knob | Default |
|------|---------|
| `--zk-grid-bg` | `var(--zk-color-surface)` |
| `--zk-grid-border-color` | `var(--zk-color-outline-variant)` |
| `--zk-grid-radius` | `var(--zk-shape-card)` |
| `--zk-grid-header-fg` | `var(--zk-color-on-surface-variant)` |
| `--zk-grid-row-hover-bg` | `rgba(0, 0, 0, 0.04)` |
| `--zk-grid-stripe-bg` | `var(--zk-color-surface-container-lowest)` |
| `--zk-grid-foot-bg` | `var(--zk-color-surface-container-low)` |
| `--zk-grid-cell-padding` (size — in `_sizing.css`) | `var(--zk-spacing-4)` |
| `--zk-grid-detail-padding` (size — in `_sizing.css`) | `var(--zk-spacing-3)` |

### Listbox — shipped

Grid's outlined-table model **plus row selection** (`.z-listitem` / `.z-listheader`). Selection
uses the MD3 list-row family (secondary-container fill + paired text). Foot/group accents and
legacy striping fallbacks stay on the base tokens.

| Knob | Default |
|------|---------|
| `--zk-listbox-bg` | `var(--zk-color-surface)` |
| `--zk-listbox-border-color` | `var(--zk-color-outline-variant)` |
| `--zk-listbox-radius` | `var(--zk-shape-card)` |
| `--zk-listbox-header-fg` | `var(--zk-color-on-surface-variant)` |
| `--zk-listbox-row-hover-bg` | `color-mix(… on-surface 8%, surface)` |
| `--zk-listbox-stripe-bg` | `var(--zk-color-surface-container-lowest)` |
| `--zk-listbox-selected-bg` | `var(--zk-color-secondary-container)` |
| `--zk-listbox-selected-fg` | `var(--zk-color-on-secondary-container)` |
| `--zk-listbox-cell-padding` (size — in `_sizing.css`) | `var(--zk-spacing-4)` |

### Tree — shipped

Same as listbox, minus striping (`.z-tree` / `.z-treerow` / `.z-treecol`).

| Knob | Default |
|------|---------|
| `--zk-tree-bg` | `var(--zk-color-surface)` |
| `--zk-tree-border-color` | `var(--zk-color-outline-variant)` |
| `--zk-tree-radius` | `var(--zk-shape-card)` |
| `--zk-tree-header-fg` | `var(--zk-color-on-surface-variant)` |
| `--zk-tree-row-hover-bg` | `color-mix(… on-surface 8%, surface)` |
| `--zk-tree-selected-bg` | `var(--zk-color-secondary-container)` |
| `--zk-tree-selected-fg` | `var(--zk-color-on-secondary-container)` |
| `--zk-tree-cell-padding` (size — in `_sizing.css`) | `var(--zk-spacing-4)` |
| `--zk-tree-header-padding` (size — in `_sizing.css`) | `var(--zk-spacing-4)` |

### Panel — shipped

Card container with header (`.z-panel`). Window-like, but carries a **resting elevation**
(so `--zk-panel-elevation` is a knob, unlike window). Note the stock panel renders border-less
(`.z-panel-noborder`), so `--zk-panel-border-color` only paints when a bordered mold is used.

| Knob | Default |
|------|---------|
| `--zk-panel-bg` | `var(--zk-color-surface)` |
| `--zk-panel-border-color` | `var(--zk-color-outline-variant)` |
| `--zk-panel-radius` | `var(--zk-shape-card)` |
| `--zk-panel-elevation` | `var(--zk-elevation-resting)` |
| `--zk-panel-header-fg` | `var(--zk-color-on-surface)` |
| `--zk-panel-icon-hover-bg` | `var(--zk-color-surface-container)` |
| `--zk-panel-header-height` (size — in `_sizing.css`) | `48px` |

### Groupbox — shipped

Bordered container with collapsible header (`.z-groupbox`).

| Knob | Default |
|------|---------|
| `--zk-groupbox-bg` | `var(--zk-color-surface)` |
| `--zk-groupbox-border-color` | `var(--zk-color-outline-variant)` |
| `--zk-groupbox-radius` | `var(--zk-shape-card)` |
| `--zk-groupbox-elevation` | `var(--zk-elevation-card)` |
| `--zk-groupbox-header-fg` | `var(--zk-color-on-surface)` |
| `--zk-groupbox-icon-hover-bg` | `var(--zk-color-surface-container)` |
| `--zk-groupbox-header-height` (size — in `_sizing.css`) | `48px` |

### Combobox — shipped

Outlined input field (`.z-combobox-input` + `.z-combobox-button`) + dropdown popup
(`.z-combobox-popup`) + option selection (`.z-comboitem`). Head of the dropdown-input
family; datebox/timebox/spinner/bandbox share this field-knob vocabulary (see below).
Note combobox uses a **split-border** DOM (input + button each carry half the border,
focus grows border-width 1px→2px); the other four use a **wrapper-border** DOM (border
on the root, focus = inset ring). Same knobs, different plumbing.

| Knob | Default |
|------|---------|
| `--zk-combobox-bg` | `var(--zk-color-surface)` |
| `--zk-combobox-fg` | `var(--zk-color-on-surface)` |
| `--zk-combobox-radius` | `var(--zk-shape-input)` |
| `--zk-combobox-border-color` | `var(--zk-color-outline)` |
| `--zk-combobox-border-color-hover` | `var(--zk-color-on-surface)` |
| `--zk-combobox-border-color-focus` | `var(--zk-color-primary)` |
| `--zk-combobox-popup-bg` | `var(--zk-color-surface)` |
| `--zk-combobox-popup-radius` | `var(--zk-shape-menu)` |
| `--zk-combobox-item-hover-bg` | `rgba(0, 0, 0, 0.08)` |
| `--zk-combobox-selected-bg` | `var(--zk-color-secondary-container)` |
| `--zk-combobox-selected-fg` | `var(--zk-color-on-secondary-container)` |

### Datebox / Timebox / Spinner / Bandbox — shipped

The rest of the dropdown-input family. All four use the **wrapper-border** model: the
border sits on the root wrapper (`.z-datebox` / `.z-timebox` / `.z-spinner` / `.z-bandbox`)
and focus is an inset ring (Mechanism A — see `reference/focus-affordance-no-layout-shift.md`),
so `--zk-<comp>-border-color-focus` tints **both** the outline and the ring (and, for datebox,
the open-state trigger glyph). Datebox and bandbox add a popup surface; timebox and spinner are
steppers with no popup. `.z-doublespinner` shares the spinner ruleset, so it reads the same
`--zk-spinner-*` knobs. Invalid/error states stay on `--zk-color-error` (not a knob), and the
internal stepper/divider lines + datebox's nested timezone `<select>` keep their base tokens.

| Knob | Default | Applies to |
|------|---------|------------|
| `--zk-datebox-bg` / `--zk-timebox-bg` / `--zk-spinner-bg` / `--zk-bandbox-bg` | `var(--zk-color-surface)` | wrapper fill |
| `--zk-datebox-fg` / `--zk-timebox-fg` / `--zk-spinner-fg` / `--zk-bandbox-fg` | `var(--zk-color-on-surface)` | input text |
| `--zk-datebox-radius` / `--zk-timebox-radius` / `--zk-spinner-radius` / `--zk-bandbox-radius` | `var(--zk-shape-input)` | wrapper + trigger corners |
| `--zk-<comp>-border-color` | `var(--zk-color-outline)` | resting outline |
| `--zk-<comp>-border-color-hover` | `var(--zk-color-on-surface)` | hover outline |
| `--zk-<comp>-border-color-focus` | `var(--zk-color-primary)` | focus/open outline + inset ring |
| `--zk-datebox-popup-bg` | `var(--zk-color-surface)` | calendar popup surface |
| `--zk-datebox-popup-radius` | `var(--zk-shape-corner-medium)` | calendar popup corners |
| `--zk-bandbox-popup-bg` | `var(--zk-color-surface)` | band popup surface |
| `--zk-bandbox-popup-radius` | `var(--zk-shape-menu)` | band popup corners |

### Daterangebox — shipped

Two-ended date-range picker field + range-calendar popup (EE, `zkmax/db/daterangebox.css`).
Same **wrapper-border** model as datebox/timebox/spinner/bandbox above: the border sits on the
root `.z-daterangebox` and focus is an inset ring (Mechanism A — see
`reference/focus-affordance-no-layout-shift.md`), so `--zk-daterangebox-border-color-focus` tints
both the outline and the ring. The trigger button's `border-radius` reuses the same
`--zk-daterangebox-radius` knob (it caps one corner of the shared field). Invalid state stays on
`--zk-color-error` (not a knob, same convention as datebox); disabled's surface/border likewise
stay on base tokens, not knob-driven. The range-highlight fills (begin/mid/end/preview-mid/
preview-end `::before` cells), the popup footer's Clear/Today/Cancel buttons, and the
begin/end input separator stay on base tokens — secondary sub-features kept out of the curated
surface (same convention as calendar/toolbar's minor sub-features).

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-daterangebox-bg` | `var(--zk-color-surface)` | wrapper fill |
| `--zk-daterangebox-fg` | `var(--zk-color-on-surface)` | begin/end input text |
| `--zk-daterangebox-radius` | `var(--zk-shape-corner-extra-small)` | wrapper + trigger-button corners |
| `--zk-daterangebox-border-color` | `var(--zk-color-outline)` | resting outline |
| `--zk-daterangebox-border-color-hover` | `var(--zk-color-on-surface)` | hover outline |
| `--zk-daterangebox-border-color-focus` | `var(--zk-color-primary)` | focus outline + inset ring |
| `--zk-daterangebox-popup-bg` | `var(--zk-color-surface)` | range-calendar popup surface |
| `--zk-daterangebox-popup-radius` | `var(--zk-shape-menu)` | range-calendar popup corners |

### Timepicker — shipped

Composite time-input field + time-list popup (PE, `zkmax/inp/timepicker.css`). Same
**wrapper-border** model as datebox/timebox/spinner/bandbox/daterangebox above: the border sits on
the root `.z-timepicker` and focus is an inset ring (Mechanism A — see
`reference/focus-affordance-no-layout-shift.md`), so `--zk-timepicker-border-color-focus` tints
both the outline and the ring. The clock trigger button's `border-radius` reuses the same
`--zk-timepicker-radius` knob (it caps one corner of the shared field, same convention as the
datebox/daterangebox trigger buttons). Invalid state (`:has(.z-timepicker-invalid)`) stays on
`--zk-color-error` (not a knob, same convention as datebox/timebox); the clock button's hover fill
(`color-mix` on-surface 8%) and disabled's opacity fade likewise stay on base tokens, not
knob-driven (same convention as button/input). The popup's `.z-timepicker-option`/`-selected` text
and hover fill stay on base tokens — a secondary sub-feature kept out of the curated surface (same
convention as calendar/toolbar's minor sub-features).

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-timepicker-bg` | `var(--zk-color-surface)` | wrapper fill |
| `--zk-timepicker-fg` | `var(--zk-color-on-surface)` | input text |
| `--zk-timepicker-radius` | `var(--zk-shape-corner-extra-small)` | wrapper + trigger-button corners |
| `--zk-timepicker-border-color` | `var(--zk-color-outline)` | resting outline |
| `--zk-timepicker-border-color-hover` | `var(--zk-color-on-surface)` | hover outline |
| `--zk-timepicker-border-color-focus` | `var(--zk-color-primary)` | focus outline + inset ring |
| `--zk-timepicker-popup-bg` | `var(--zk-color-surface)` | time-list popup surface |
| `--zk-timepicker-popup-radius` | `var(--zk-shape-menu)` | time-list popup corners |

### Chosenbox — shipped

Multi-select input field + option popup (EE, `zkmax/inp/css/chosenbox.css`). Same
**wrapper-border** model as datebox/timebox/spinner/bandbox/daterangebox/timepicker above: the
border sits on the root `.z-chosenbox` and focus (`.z-chosenbox-focus`) is a `box-shadow` inset
ring, so `--zk-chosenbox-border-color-focus` tints both the outline and the ring.
`--zk-chosenbox-fg` covers **both** the search input's text (`.z-chosenbox-input`) **and** each
selected chip's text (`.z-chosenbox-item-content`) — both read `var(--zk-color-on-surface)`
today, so one shared knob keeps them in lockstep, the same "one knob, several roles" precedent as
tab/calendar's accent. The selected chip has its own resting/defining-state fill pair:
`--zk-chosenbox-item-bg` is the resting chip fill (`.z-chosenbox-item`), and
`--zk-chosenbox-item-focus-bg` is the fill a chip swaps to once clicked and armed for keyboard
delete (`.z-chosenbox-item-focus`) — the defining state, same "one knob per state" precedent as
listbox/tree's `-selected-bg`. The delete-button's hover fill, the popup option's hover fill, and
the creatable-option accent stay on base tokens — secondary sub-features kept out of the curated
surface (same convention as calendar/toolbar's minor sub-features). Disabled
(`.z-chosenbox-disabled`) stays on its own surface token, not knob-driven (same convention as
input/button).

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-chosenbox-bg` | `var(--zk-color-surface)` | wrapper fill |
| `--zk-chosenbox-fg` | `var(--zk-color-on-surface)` | search-input text + selected-chip text |
| `--zk-chosenbox-radius` | `var(--zk-shape-input)` | wrapper corners |
| `--zk-chosenbox-border-color` | `var(--zk-color-outline)` | resting outline |
| `--zk-chosenbox-border-color-hover` | `var(--zk-color-on-surface)` | hover outline |
| `--zk-chosenbox-border-color-focus` | `var(--zk-color-primary)` | focus outline + inset ring |
| `--zk-chosenbox-popup-bg` | `var(--zk-color-surface)` | option-popup surface |
| `--zk-chosenbox-popup-radius` | `var(--zk-shape-menu)` | option-popup corners |
| `--zk-chosenbox-item-bg` | `var(--zk-color-surface-container-high)` | selected-chip resting fill |
| `--zk-chosenbox-item-focus-bg` | `var(--zk-color-secondary-container)` | selected-chip armed-for-delete fill |

### Cascader — shipped

Read-only trigger field (not an `<input>` — shows the selected-path text or a placeholder,
`.z-cascader`) + a right-expanding tree popup (EE, `zkmax/inp/css/cascader.css`). Same
**wrapper-border** model as datebox/timebox/spinner/bandbox/daterangebox/timepicker/chosenbox
above: the border sits on the root `.z-cascader` and focus/open (`.z-cascader-focus` /
`.z-cascader-open`, one shared rule) is a `box-shadow` inset ring, so
`--zk-cascader-border-color-focus` tints both the outline and the ring. `--zk-cascader-fg` covers
**both** the trigger's selected-path label (`.z-cascader-label`) **and** each popup item's text
(`.z-cascader-item`) — both read `var(--zk-color-on-surface)` today, the same "one knob, several
roles" precedent as chosenbox's `fg`. `--zk-cascader-accent` is the component's one path-selection
signal: the currently-selected node's text color (`.z-cascader-item.z-cascader-selected`), the
defining state. The placeholder text, the trigger/item icons, the item hover/active fill (a
literal `rgba`), the cave-column divider, and the popup's own `border-color` stay on base tokens —
secondary sub-features kept out of the curated surface (same convention as
calendar/toolbar/timepicker's minor sub-features). Disabled (`.z-cascader-disabled`) dims via
`opacity` only, not knob-driven (same convention as button/input/rating).

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-cascader-bg` | `var(--zk-color-surface)` | wrapper fill |
| `--zk-cascader-fg` | `var(--zk-color-on-surface)` | trigger's selected-path text + popup item text |
| `--zk-cascader-radius` | `var(--zk-shape-input)` | wrapper corners |
| `--zk-cascader-border-color` | `var(--zk-color-outline)` | resting outline |
| `--zk-cascader-border-color-hover` | `var(--zk-color-on-surface)` | hover outline |
| `--zk-cascader-border-color-focus` | `var(--zk-color-primary)` | focus/open outline + inset ring |
| `--zk-cascader-popup-bg` | `var(--zk-color-surface)` | tree-popup surface |
| `--zk-cascader-popup-radius` | `var(--zk-shape-menu)` | tree-popup corners |
| `--zk-cascader-accent` | `var(--zk-color-primary)` | selected-path node text |

### Searchbox — shipped

Multi-select dropdown trigger field (not an `<input>` — shows the selected-label text or a
placeholder, `.z-searchbox`) + a detached search popup (EE, `zkmax/inp/css/searchbox.css`). Same
**wrapper-border** model as datebox/timebox/spinner/bandbox/daterangebox/timepicker/chosenbox/
cascader above: the border sits on the root `.z-searchbox` and focus/open is a `box-shadow` inset
ring (Mechanism A), so `--zk-searchbox-border-color-focus` tints both the outline and the ring.
`--zk-searchbox-fg` covers **four** roles: the trigger's selected-label text
(`.z-searchbox-label`), the popup's own text color, the search input's text, and each
(non-selected) item's text — all four read `var(--zk-color-on-surface)` today, the same "one
knob, several roles" precedent as chosenbox/cascader's `fg`. The selected item is the component's
one defining state, and per `doc/contracts/searchbox.md` it belongs to the **LIST-ROW** selection
family (secondary-container fill + paired text, MD3 `list-item.selected`), so it
gets its own pair: `--zk-searchbox-selected-bg` / `-selected-fg` (same pairing precedent as
combobox's `-selected-bg`/`-fg`); the selected item's hover fill (`color-mix`) also reads this
pair, so an override stays tonally consistent. The placeholder text, trigger/item icons, item
hover/keyboard-active background, the nested search input's own bg/border/radius, and the
multi-select check-icon's fill stay on base tokens — secondary sub-features kept out of the
curated surface (same convention as chosenbox/cascader's minor sub-features). Disabled dims via
opacity (plus its own surface token), not knob-driven (same convention as button/input).

**CTV-3 scope note** (same convention as combobox/datebox/bandbox/daterangebox/timepicker/
chosenbox/cascader's option popups): `.z-searchbox-popup` is reparented to `<body>` via
`makeVParent()` on open, so the region/whole-app assertions cover only the trigger field left in
place (wrapper `border-radius`/`border-color` and `.z-searchbox-label`'s `fg`), not the
reparented popup or its selected item — the `popup-bg`/`-popup-radius`/`-selected-bg`/
`-selected-fg` knobs are declared and consumed but not individually asserted, matching the
existing treatment of the other wrapper-border family members' popup knobs.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-searchbox-bg` | `var(--zk-color-surface)` | wrapper fill |
| `--zk-searchbox-fg` | `var(--zk-color-on-surface)` | trigger's selected-label text + popup text + search-input text + item text |
| `--zk-searchbox-radius` | `var(--zk-shape-input)` | wrapper corners |
| `--zk-searchbox-border-color` | `var(--zk-color-outline)` | resting outline |
| `--zk-searchbox-border-color-hover` | `var(--zk-color-on-surface)` | hover outline |
| `--zk-searchbox-border-color-focus` | `var(--zk-color-primary)` | focus/open outline + inset ring |
| `--zk-searchbox-popup-bg` | `var(--zk-color-surface)` | search-popup surface |
| `--zk-searchbox-popup-radius` | `var(--zk-shape-corner-medium)` | search-popup corners |
| `--zk-searchbox-selected-bg` | `var(--zk-color-secondary-container)` | selected-item fill (+ hover) |
| `--zk-searchbox-selected-fg` | `var(--zk-color-on-secondary-container)` | selected-item text |

### Tab (tabbox) — shipped

Nav chrome + active accent. State is a `::before` overlay; `--zk-tab-accent` colors the
state-layer tint, the selected label, and the bottom indicator. `--zk-tab-border-color` drives
every tab-component border (bar divider across top/bottom/left/right molds + tabpanel frames).

| Knob | Default |
|------|---------|
| `--zk-tab-bg` | `var(--zk-color-surface)` |
| `--zk-tab-border-color` | `var(--zk-color-outline-variant)` |
| `--zk-tab-fg` | `var(--zk-color-on-surface-variant)` |
| `--zk-tab-fg-hover` | `var(--zk-color-on-surface)` |
| `--zk-tab-accent` | `var(--zk-color-primary)` |
| `--zk-tab-height` (size — in `_sizing.css`) | `48px` |

### Menu — shipped

Menubar surface + dropdown popup + selected item (`.z-menubar` / `.z-menupopup` / `.z-menuitem`).
Menu/menuitem hover uses a shared `::before` overlay (not knob-driven).

| Knob | Default |
|------|---------|
| `--zk-menubar-bg` | `var(--zk-color-surface-container)` |
| `--zk-menupopup-bg` | `var(--zk-color-surface)` |
| `--zk-menupopup-radius` | `var(--zk-shape-menu)` |
| `--zk-menupopup-border-color` | `var(--zk-color-outline-variant)` |
| `--zk-menuitem-selected-bg` | `var(--zk-color-secondary-container)` |

### Avatar / avatar-group — shipped

Native ZK-6097 components. Base defaults were declared on the `.z-avatar` element (which
shadowed region overrides); they are hoisted here so a `:root`/region override reaches plain
avatars. Size variants (`.z-avatar-small/-large`) still set size/font per-variant.

| Knob | Default |
|------|---------|
| `--zk-avatar-bg` | `var(--zk-color-primary-container)` |
| `--zk-avatar-fg` | `var(--zk-color-on-primary-container)` |
| `--zk-avatar-size` | `40px` |
| `--zk-avatar-font-size` | `var(--zk-typescale-label-large-size)` |
| `--zk-avatargroup-overlap` | `-8px` |

### Chip — shipped (with a caveat)

Base defaults (`--zk-chip-bg` / `-border` / `-color`) are hoisted here for consistency (no
more inline fallback). **Caveat:** ZK stamps a default severity class (`z-chip-info`) on every
chip, and the severity variants (`.z-chip-info/-success/…`) set these vars *on the element* —
which shadows a `:root`/region override. So chip is themed **per-severity / inline**
(`style="--zk-chip-bg:…"`), not regionally. The base knob applies only to a severity-less chip.

| Knob | Default |
|------|---------|
| `--zk-chip-bg` | `var(--zk-color-surface-container-high)` |
| `--zk-chip-border` | `var(--zk-color-outline-variant)` |
| `--zk-chip-color` | `var(--zk-color-on-surface-variant)` |

### Badge — shipped

The fourth ZK-6097 native (completing the badge/chip/avatar/avatar-group set). The
`.z-badge-indicator` resolves the knobs from `:root`. ZK always stamps a severity class
(default `info`) and the indicator is a **child** of it, so a per-severity rule on the indicator
shadows any inherited value. We exploit that deliberately: the default `info` fill is routed
through the base `--zk-badge-bg` (there is **no** `.z-badge-info` rule), so a **default badge is
region- and inline-overridable**; the non-default severities (`.z-badge-success/-warning/-danger/-secondary`)
pin `--zk-badge-bg` per-variant and keep their semantic color. `--zk-badge-fg` and
`--zk-badge-radius` have no per-severity override, so they apply to **every** badge. Dot-mode
radius (`50%`) is a shape concern and stays hardcoded, not a knob.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-badge-bg` | `oklch(from var(--zk-color-status-info) 0.70 calc(c * 1.3) h)` (≈ `#00ade9`) | default (info) badge only; non-default severities pin their own. A *bright* tone derived from the (deliberately dark) info token, so it sits with the four bright `status-*` siblings |
| `--zk-badge-fg` | `var(--zk-color-on-surface)` | every badge. Dark, not white: every badge fill is a bright tone, and MD3 pairs a light tone with a dark on-colour |
| `--zk-badge-radius` | `10px` | every badge (count/pill; dot stays `50%`) |

### Rating — shipped

Star-rating glyph fill only (`.z-rating-icon`) — an icon-only component, so there is no
background, border, or radius knob to expose. Two color axes cover it: `--zk-rating-fg` for
the resting (outline) star and `--zk-rating-accent` for the selected/hover (filled) star —
the defining state. Disabled dims via `--zk-state-disabled-opacity`; readonly disables
interaction only, at full opacity. Neither disabled nor readonly is knob-driven.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-rating-fg` | `var(--zk-color-outline)` | resting (outline) star |
| `--zk-rating-accent` | `var(--zk-color-primary)` | selected/hover (filled) star |

### Progressmeter — shipped

MD3 linear progress: a two-layer bar (track + fill), no icon, no text. Two color knobs
cover it — `--zk-progressmeter-bg` for the track and `--zk-progressmeter-fill` for the
progress bar — plus a shared radius knob for both layers. The color **variants**
(`.z-progressmeter-secondary/-success/-warning/-error`) keep their own semantic colors via
higher-specificity rules on the element (same treatment as button's color variants); they
are intentionally **not** knob-driven, so only the default (primary) bar responds to an
override.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-progressmeter-bg` | `var(--zk-color-primary-container)` | track (default/primary bar only) |
| `--zk-progressmeter-fill` | `var(--zk-color-primary)` | fill bar (default/primary bar only) |
| `--zk-progressmeter-radius` | `2px` | every progressmeter (track + fill) |

### Paging — shipped

MD3 pagination bar. Two color/shape axes cover the pager buttons —
`--zk-paging-fg` for the button text/icon and `--zk-paging-radius` for the
shared pill shape — plus the current-page indicator's own fill/text pair
(`--zk-paging-selected-bg` / `-fg`, the defining state). The jump-to-page
input (rendered only by the **default** mold) gets its own bg/fg/border-color
trio, curated separately from the pager buttons.

Numbered page buttons — and therefore `.z-paging-selected` — only render in
the **`os`** mold; the default mold shows prev/next buttons plus the
jump-to-page input, with no page numbers. `component-theme-variables.spec.ts` reads the
`.z-paging-selected` button that `paging.zul` renders in the `os` mold, so
`--zk-paging-selected-bg/-fg/-radius` are actually exercised; the jump-to-page input
knobs are shipped but not tested (the `os` mold never renders the input). Disabled
buttons/input are intentionally not knob-driven (same convention as
button/input). The jump-input's `border-radius` also stays hardcoded on
`--zk-shape-corner-extra-small` rather than exposed as a knob — a defensible
curation choice (the shared `--zk-paging-radius` only applies to pager
buttons), noted here rather than fixed.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-paging-fg` | `var(--zk-color-on-surface-variant)` | every pager button (text/icon) |
| `--zk-paging-radius` | `var(--zk-shape-corner-full)` | every pager button (pill shape) |
| `--zk-paging-selected-bg` | `var(--zk-color-primary-container)` | current-page button fill |
| `--zk-paging-selected-fg` | `var(--zk-color-primary)` | current-page button text |
| `--zk-paging-input-bg` | `var(--zk-color-surface)` | jump-to-page input (default mold only) |
| `--zk-paging-input-fg` | `var(--zk-color-on-surface)` | jump-to-page input (default mold only) |
| `--zk-paging-input-border-color` | `var(--zk-color-outline)` | jump-to-page input (default mold only) |

### Combobutton — shipped

Split button (primary action + dropdown arrow), `.z-combobutton` /
`.z-combobutton-content` / `.z-combobutton-button`. Reuses button's overlay
state model (an MD3 `::before` tint over the content area whose opacity
animates per state) rather than a per-state background, plus a
`-divider-color` knob for the 1px seam between the content and arrow areas
(default is the stock literal `rgba(255, 255, 255, 0.3)`, kept as-is — it
only reads against the filled `bg`, so it isn't promoted to a token).
`toolbar` mold is a **color variant** (same treatment as button's
outlined/text variants): it pins its own semantic colors
(`--zk-color-on-surface-variant` / `--zk-color-outline`) directly on the
element at higher specificity, so it does not read the base knobs. Disabled
is likewise not knob-driven (button convention).

| Knob | Default |
|------|---------|
| `--zk-combobutton-bg` | `var(--zk-color-primary)` |
| `--zk-combobutton-fg` | `var(--zk-color-on-primary)` |
| `--zk-combobutton-radius` | `var(--zk-shape-button)` |
| `--zk-combobutton-divider-color` | `rgba(255, 255, 255, 0.3)` |
| `--zk-combobutton-overlay-color` | `var(--zk-color-on-primary)` |
| `--zk-combobutton-hover-opacity` / `-focus-opacity` / `-active-opacity` | state-layer opacity tokens |

### Selectbox — shipped

Native `<select>` element — `<listbox mold="select">` renders `<select
class="z-select">` directly (no wrapper); Listbox's `css-uri` covers every
mold, so listbox.css's `.z-select` block is the effective ruleset (not
select.css's — see note below). State is a per-state border-color change, no
overlay, same model as Input. Disabled is opacity-only, not knob-driven (same
convention as input/button). The dropdown chevron is a literal-color SVG
baked into a `background-image` data URI — a custom property can't be
interpolated inside a `url()` string, so its stroke color stays hardcoded,
not a knob.

| Knob | Default |
|------|---------|
| `--zk-selectbox-bg` | `var(--zk-color-surface)` |
| `--zk-selectbox-fg` | `var(--zk-color-on-surface)` |
| `--zk-selectbox-radius` | `var(--zk-shape-input)` |
| `--zk-selectbox-border-color` | `var(--zk-color-outline)` |
| `--zk-selectbox-border-color-hover` | `var(--zk-color-on-surface)` |
| `--zk-selectbox-border-color-focus` | `var(--zk-color-primary)` |

**Duplicate `.z-select` ruleset note**: `js/zul/sel/css/select.css` also
declares a `.z-select` block (reading the same six knobs), but its
`select.css.dsp` is never requested by Listbox's `css-uri` (confirmed via
`check:css-dsp` — it's an unreferenced "extra"); it is dormant duplicate
coverage, not the effective stylesheet. Both files consume the identical
knobs so they stay in lockstep regardless of which one ever loads.

### Inputgroup — shipped

Input + addon(s) combined into a single field (`.z-inputgroup` +
`.z-inputgroup-text` addon + the grouped textbox/combobox). Border color and
radius are **shared** axes: `--zk-inputgroup-border-color` paints both the
addon's `border` and the grouped input/combobox's border override, so the
whole group reads as one continuous outline; `--zk-inputgroup-radius` drives
the rounded ends in both horizontal and vertical layout, plus the
`:focus-within` ring's corner. The addon also carries its own fill/text pair.
Focus stays on the global `--zk-focus-ring` (same convention as
button/window/grid), not a knob.

| Knob | Default |
|------|---------|
| `--zk-inputgroup-text-bg` | `var(--zk-color-surface-container-low)` |
| `--zk-inputgroup-text-fg` | `var(--zk-color-on-surface-variant)` |
| `--zk-inputgroup-border-color` | `var(--zk-color-outline)` |
| `--zk-inputgroup-radius` | `var(--zk-shape-input)` |

### Calendar — shipped

Self-contained month grid + nav header (`.z-calendar`); also the shell used inside a
datebox popup, which flattens it back to flat content (see the `:not()` guard in
calendar.css) — unaffected by these knobs. `--zk-calendar-fg` is the day-number/title
text; `--zk-calendar-header-fg` is the muted weekday-label + nav-icon (resting) text.
`--zk-calendar-accent` / `-accent-fg` is a single defining-state pair — it colors the
selected-day disc fill (`::before`) + text, the today ring, and the Today-link label
together (all four read `var(--zk-color-primary)`/`-on-primary` today), the same "one
knob, several roles" precedent as tab's `--zk-tab-accent`. Hover state layers
(title/icon/day-cell hover tints, the Today-link hover tint) stay on base tokens, not
knob-driven (same convention as menu's `::before` overlay). Disabled/outside/out-of-range
days, the week-of-year column, and the month/year/decade picker's pill radius
(`--zk-shape-button`) also stay on base tokens — secondary/minor sub-features kept out of
the curated surface (same convention as grid/listbox).

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-calendar-bg` | `var(--zk-color-surface)` | card + header fill |
| `--zk-calendar-fg` | `var(--zk-color-on-surface)` | day-number + title text |
| `--zk-calendar-header-fg` | `var(--zk-color-on-surface-variant)` | weekday-label + nav-icon (resting) text |
| `--zk-calendar-border-color` | `var(--zk-color-outline-variant)` | card border + today-link divider |
| `--zk-calendar-radius` | `var(--zk-shape-card)` | card corners |
| `--zk-calendar-accent` | `var(--zk-color-primary)` | selected-day fill, today ring, Today-link text |
| `--zk-calendar-accent-fg` | `var(--zk-color-on-primary)` | selected-day text |

### Toolbar — shipped

Full-width chrome bar (`.z-toolbar`), no radius (same treatment as menubar). A
single shared border-color knob styles two things: the bar's own edge
(`border-bottom` in the horizontal mold, `border-right` in the vertical mold —
the only edge with a real DOM match, and the one the Playwright test asserts)
and the `.z-toolbarseparator` item divider (both orientations). **Caveat**:
`.z-toolbarseparator` has no matching class in current ZK 10 core —
`<separator bar="true">` renders `z-separator-horizontal-bar`/
`-vertical-bar`, not `z-toolbarseparator` — so that half of the knob's
"family-wide" reach is CSS-only (pre-existing dead code, unrelated to this
knob, zero regression either way). The app-bar context variant (`.z-north
.z-toolbar`, including its toolbarseparator color-mix tint) is a distinct
color **VARIANT** (same treatment as combobutton's `toolbar` mold): it pins
its own semantic colors (`--zk-color-primary`/`-on-primary`) directly on the
element at higher specificity, so it does not read the base knobs. The
tabs-embedded variant (`.z-toolbar-tabs`, transparent/no-border) and the
overflow popup (`.z-toolbar-popup`, a minor sub-feature) also stay on base
tokens, not knob-driven.

| Knob | Default |
|------|---------|
| `--zk-toolbar-bg` | `var(--zk-color-surface)` |
| `--zk-toolbar-border-color` | `var(--zk-color-outline-variant)` |
| `--zk-toolbar-height` (size — in `_sizing.css`) | `48px` |

### Toolbarbutton — shipped

Icon/text button rendered inside toolbar chrome (`.z-toolbarbutton`,
`js/zul/wgt/css/toolbarbutton.css`) — its own widget/knob family, not a
`--zk-button-*` variant. State is an MD3 `::before` overlay that reads
`currentColor`, so `--zk-toolbarbutton-fg` drives **both** the resting
text/icon color **and** the overlay tint automatically — no separate
overlay-color knob is needed (unlike button/combobutton, whose overlay color
differs from their fill). `--zk-toolbarbutton-radius` is the pill shape (the
overlay clips to it via `border-radius: inherit`).
`--zk-toolbarbutton-checked-bg` / `-checked-fg` is the one defining state
(`mode="toggle" checked="true"`) — a resting/defining-state fill+text pair,
the same precedent as listbox/tree's `-selected-bg`/`-fg`; the checked fg also
re-tints the checked-state overlay via `currentColor`. The checked focus-ring
recolor (`outline-color: var(--zk-color-primary)`) and the hover/focus/active
overlay opacities stay on base tokens, not knob-driven (same convention as
menu's `::before` overlay); so does disabled (opacity only, same convention as
button/input/rating). The app-bar context variant (`.z-north .z-toolbar
.z-toolbarbutton`, in `toolbar.css`) is a distinct color **VARIANT** that pins
`on-primary` directly at higher specificity (same treatment as combobutton's
`toolbar` mold), so it does not read these knobs.

| Knob | Default |
|------|---------|
| `--zk-toolbarbutton-fg` | `var(--zk-color-primary)` |
| `--zk-toolbarbutton-radius` | `var(--zk-shape-corner-full)` |
| `--zk-toolbarbutton-checked-bg` | `var(--zk-color-primary-container)` |
| `--zk-toolbarbutton-checked-fg` | `var(--zk-color-on-primary-container)` |

### Slider — shipped

MD3 range input — track + fill + thumb (`.z-slider`); no text/border, so the knob
surface is: the resting track color (`--zk-slider-track-bg`), a single defining accent
(`--zk-slider-accent`) that drives both the active fill and the thumb (both read
`var(--zk-color-primary)` today, so they share one knob — the same "one knob, several
roles" precedent as tab/calendar's accent), a shared corner radius used by the track,
fill, and thumb alike (`--zk-slider-radius`), and the thumb's resting elevation
(`--zk-slider-elevation`). The thumb's hover/focus/active state-layer ring (a
`color-mix` tint) reads the same accent, so an override stays tonally consistent; the
knob mold (PE)'s SVG arc strokes (`.z-slider-knob-inner` / `-area`) mirror the
identical track/fill concept and read the same two knobs too. The sphere mold's
3D-gradient thumb is a color **VARIANT** (like button's outlined/text variants) that
pins its own gradient at higher specificity, deliberately not knob-driven. The
numeric-input overlay (`.z-slider-input`) and the value tooltip (`.z-slider-popup`)
are minor sub-features kept on base tokens (same convention as calendar's
week-of-year column); disabled dims via `opacity` only, not knob-driven (same
convention as button/input/rating). Rangeslider (PE) and multislider (EE) were
each moved into their own knob family below in later passes.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-slider-track-bg` | `var(--zk-color-outline-variant)` | resting track fill |
| `--zk-slider-accent` | `var(--zk-color-primary)` | active fill + thumb fill (+ PE knob-mold arc stroke) |
| `--zk-slider-radius` | `var(--zk-shape-corner-full)` | track / fill / thumb corner radius |
| `--zk-slider-elevation` | `var(--zk-elevation-1)` | thumb resting shadow |

### Rangeslider — shipped

MD3 dual-thumb range input (`zkex/slider/css/rangeslider.css`) — track + the
active-range fill between the two thumbs + the thumbs themselves — reusing
Slider's track/accent/radius/elevation vocabulary above. The resting track
color (`--zk-rangeslider-track-bg`) doubles as the resting (unselected)
mark-dot's `border-color`, a secondary role. A single defining accent
(`--zk-rangeslider-accent`) drives the active-range fill between the two
thumbs (`.z-sliderbuttons-area`), each thumb's fill (`.z-sliderbuttons-button`),
and the thumb's hover/focus state-layer ring (`::before`) — merging the file's
former separate `-area-color`/`-button-color` into one shared knob, the same
"one knob, several roles" precedent as tab/calendar/slider's accent. A shared
corner radius (`--zk-rangeslider-radius`) drives the track and the
active-range fill; the thumb stays a hardcoded circular `50%` (a fixed shape
like radio/badge's dot mode, not knob-driven). The thumb's resting elevation
(`--zk-rangeslider-elevation`) is exposed; the pressed/active-state shadow
stays on its own elevation token, not knob-driven. The marks system (mark
dots, mark labels, the active mark-dot's border-color) and the value tooltip
are minor sub-features kept on base tokens, not knob-driven (same convention
as calendar's week-of-year column / slider's numeric-input-overlay and
tooltip exclusions); disabled dims via `opacity` only, not knob-driven (same
convention as button/input/rating/slider). Multislider (EE) shares this same
Sliderbuttons sub-widget markup and was moved into its own knob family below
in a later pass.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-rangeslider-track-bg` | `var(--zk-color-outline-variant)` | resting track fill + resting mark-dot border |
| `--zk-rangeslider-accent` | `var(--zk-color-primary)` | active-range fill + thumb fill + thumb hover ring |
| `--zk-rangeslider-radius` | `var(--zk-shape-corner-full)` | track + active-range fill corner radius |
| `--zk-rangeslider-elevation` | `var(--zk-elevation-1)` | thumb resting shadow |

### Multislider — shipped

MD3 multi-range slider (`zkmax/slider/css/multislider.css`, EE) — track + N
active-range fills + N thumbs — sharing the same Sliderbuttons sub-widget
markup (`.z-sliderbuttons-area` / `.z-sliderbuttons-button`) and the
track/accent/radius/elevation vocabulary as Rangeslider above. The resting
track color (`--zk-multislider-track-bg`) doubles as each mark dot's fill, a
secondary role. A single defining accent (`--zk-multislider-accent`) drives
every active-range fill, every thumb's fill, and each thumb's hover/focus
state-layer ring (`::before`) — the same "one knob, several roles" precedent
as tab/calendar/slider/rangeslider's accent. A shared corner radius
(`--zk-multislider-radius`) drives the track and each active-range fill; each
thumb stays a hardcoded circular `50%` (a fixed shape like rangeslider's
thumb, not knob-driven). The thumb's resting elevation
(`--zk-multislider-elevation`) is exposed; the pressed/active-state shadow
stays on its own elevation token, not knob-driven. Mark labels and the value
tooltip are minor sub-features kept on base tokens, not knob-driven (same
convention as rangeslider's marks/tooltip exclusions); disabled dims via
`opacity` only, not knob-driven (same convention as
button/input/rating/slider/rangeslider).

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-multislider-track-bg` | `var(--zk-color-outline-variant)` | resting track fill + mark-dot fill |
| `--zk-multislider-accent` | `var(--zk-color-primary)` | active-range fill + thumb fill + thumb hover ring |
| `--zk-multislider-radius` | `var(--zk-shape-corner-full)` | track + active-range fill corner radius |
| `--zk-multislider-elevation` | `var(--zk-elevation-1)` | thumb resting shadow |

### Checkbox — shipped (default mold only)

Consumed by the default mold (`.z-checkbox` / `.z-checkbox-mold` / `.z-checkbox-content`).
Resting (unchecked) reads the border/text knobs; checked and indeterminate swap to a single
accent (mold fill + border + the hover-ring tint), the defining state — the same "one knob,
several roles" precedent as tab/calendar's accent. The checkmark/dash glyph is a literal-color
SVG baked into a `background-image` data URI (same limitation as selectbox's chevron), so it
stays hardcoded, not a knob. Disabled dims via opacity only, not knob-driven (same convention as
button/input/rating). **Out of scope for this pass**: the `switch` and `toggle` molds are
distinct visual treatments (different DOM/state model) and keep reading base tokens directly —
a natural follow-on candidate. radio/radiogroup share this same CSS file but are a separate
widget with their own knobs — see the Radio entry below.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-checkbox-fg` | `var(--zk-color-on-surface)` | wrapper + label text |
| `--zk-checkbox-border-color` | `var(--zk-color-on-surface-variant)` | resting (unchecked) mold border |
| `--zk-checkbox-radius` | `2px` | mold corner radius |
| `--zk-checkbox-accent` | `var(--zk-color-primary)` | checked/indeterminate fill + border + hover-ring tint |

### Radio — shipped

Consumed by `.z-radio` / `.z-radio-on` / `.z-radio-disabled` (`zul/wgt/checkbox.css` — shares
the file with checkbox, but is a separate widget). There is no `z-radio-mold` element:
`input[type="radio"]` itself is the visual, so unlike checkbox there's no separate mold element
to knob. Resting (unselected) reads the border/text knobs; selected swaps the border color and
the inner-dot fill (same rule) to a single accent, the defining state — the same "one knob,
several roles" precedent as checkbox's accent. The circular shape (`border-radius: 50%`) stays
hardcoded — a fixed shape concern, not a knob (same convention as badge's dot mode). Disabled
dims via opacity only, not knob-driven (same convention as button/input/rating/checkbox).
`z-radiogroup` (the horizontal/vertical layout container) has no color/border/radius surface of
its own and is untouched.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-radio-fg` | `var(--zk-color-on-surface)` | wrapper + label text |
| `--zk-radio-border-color` | `var(--zk-color-on-surface-variant)` | resting (unselected) input border |
| `--zk-radio-accent` | `var(--zk-color-primary)` | selected border-color + inner-dot fill |

### Messagebox — shipped

Consumed by the alert dialog `Messagebox.show()` creates (`.z-messagebox-window.z-window` +
`.z-messagebox` + `.z-messagebox-buttons`). A distinct family from `--zk-window-*`: the dialog
owns a fixed, mode-independent surface/border/elevation rather than window's mode-driven chrome,
so it gets its own knobs. `--zk-messagebox-bg` covers the dialog surface, header, and content
areas alike (all three read the same fill — the same "one knob, several roles" precedent as
window's header sharing its surface fill). `--zk-messagebox-border-color` covers both divider
lines — the header's `border-bottom` and the button row's `border-top` — the same "one knob,
several dividers" precedent as toolbar's `--zk-toolbar-border-color`. Icon-type colors
(information/exclamation/error/question) are semantic status colors tied to the message type,
kept on their own tokens, not knob-driven (same convention as button/progressmeter's color
variants).

**CTV-3 (region scoping) is structurally N/A here, not a defect.** `Messagebox.show()` calls
`Executions.createComponents(_templ, desktop.getFirstPage(), null, arg)`, so the dialog Window is
always parented to the page root — never to the container that triggered it. A region override
on an ancestor of the triggering button cannot reach the dialog: verified empirically (the
dialog's DOM parent chain is `BODY`/`HTML`, a sibling of the triggering container, not its
descendant; `isDescendantOf` the container is `false`). This is the same "not regionally
demonstrable" class as chip, for a different root cause (chip pins vars on the component itself;
messagebox re-mounts to the page root). The whole-app (`:root`) override path is unaffected and
works normally.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-messagebox-bg` | `var(--zk-color-surface)` | dialog surface + header + content fill |
| `--zk-messagebox-fg` | `var(--zk-color-on-surface-variant)` | message body text |
| `--zk-messagebox-header-fg` | `var(--zk-color-on-surface)` | title text |
| `--zk-messagebox-border-color` | `var(--zk-color-outline-variant)` | header bottom border + button-row top border |
| `--zk-messagebox-radius` | `var(--zk-shape-dialog)` | dialog corners |
| `--zk-messagebox-elevation` | `var(--zk-elevation-dialog)` | dialog shadow (static — always modal, not mode-driven) |

### Notification — shipped (info-default variant)

Floating alert card created by `Clients.showNotification()` / `Notification.show()` (`.z-notification`
wrapper + `.z-notification-content` card). `Notification.show()` turns a `null` type into `"info"`, so a
notification made through the public API always carries a type variant. `--zk-notification-bg` / `-fg`
therefore drive the **info** (default) variant's fill and text, plus the pointer arrows, which follow the
fill; an untyped card (only reachable by calling `Clients.showNotification()` with no type) reads the same
two knobs on the base rule. `--zk-notification-radius` colors the corners of every type (declared on the
base `.z-notification-content` rule). There is **no accent knob**: the left accent stripe was removed for
MD3 fidelity (alerts carry severity with the leading icon plus the container tint, not a leading color
bar), and an untyped notification renders no icon glyph at all — ZK picks the glyph class from its type
map, so with no type there is nothing for an accent color to paint. The
`.z-notification-warning/-error` variants pin their own bg/fg via higher-specificity compound selectors
(e.g. `.z-notification-error .z-notification-content`) — the same color-**variant** convention as
button/progressmeter and toast — so a warning or error notification's semantic color is unaffected by an
override (CTV-7). Elevation (`box-shadow`) stays on its base token, not knob-driven — a deliberate
curation choice to keep the surface small (same convention as calendar/slider's minor
sub-features). Width/height are a size-dimension concern and already have their own knob
(`--zk-notification-height` in `tokens/_sizing.css`), not duplicated here. The close button's
icon `color: inherit` reads from `.z-notification`, not `.z-notification-content`, so it is
unaffected by these knobs either way.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-notification-bg` | `var(--zk-color-surface-container-highest)` | info/untyped card fill and pointer arrows |
| `--zk-notification-fg` | `var(--zk-color-on-surface)` | info/untyped card text |
| `--zk-notification-radius` | `var(--zk-shape-corner-extra-small)` | card corners, all types |

### Toast — shipped (info-default variant only)

Floating MD3 snackbar created by `Toast.show()` (`.z-toast` wrapper + `.z-toast-content` card +
icon + optional close button). Unlike notification, `Toast.show()` always defaults a `null` type
to `"info"`, so there is no untyped/bare card to route through — these knobs drive the **info
(default)** variant: `--zk-toast-bg` / `-fg` color `.z-toast-info .z-toast-content`'s fill and
text; `--zk-toast-accent` colors the icon. (It also colored a left accent stripe until that was
removed for MD3 fidelity. Unlike notification, whose accent knob was retired with the stripe, this
one survives: `Toast.show()` always defaults a `null` type to `"info"`, so a toast always carries a
type and therefore always renders an icon glyph for the knob to paint.)
`--zk-toast-radius` is shared by **every** toast (declared on the base
`.z-toast-content` rule, not per-type). The `.z-toast-warning` / `-error` type variants pin their
own bg/fg/accent via higher-specificity compound selectors (same color-variant convention as
notification/button/progressmeter), so they are unaffected by an override (CTV-7). The
close-button icon's color per type stays on its own token, not knob-driven (same convention as
notification's close icon). Elevation (`box-shadow`) stays on its base token, not knob-driven
(same convention as notification/calendar/slider's minor sub-features).

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-toast-bg` | `var(--zk-color-surface-container-highest)` | info (default) toast card fill |
| `--zk-toast-fg` | `var(--zk-color-on-surface)` | info (default) toast card text |
| `--zk-toast-radius` | `var(--zk-shape-corner-extra-small)` | every toast's card corners |
| `--zk-toast-accent` | `var(--zk-color-status-info)` | info (default) toast's leading icon |

### A (anchor) — shipped

Consumed by `.z-a`, the plain-text MD3 link — no background, border, or radius knob applies.
Resting and hover text color are the same value (`var(--zk-color-primary)`) today, so a single
`fg` knob covers both — the same "one knob, several roles" precedent as tab/calendar's accent.
Focus stays on the global `--zk-focus-ring` (same convention as button/window/grid), not a knob;
disabled keeps its own `--zk-color-disabled` literal, also not knob-driven (same convention as
button/input/rating). `.z-a` renders in place (no client-side reparenting), so region scoping
works normally — unlike messagebox/popup's structural exceptions.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-a-fg` | `var(--zk-color-primary)` | resting + hover text color |

### Drawer — shipped

MD3 side-sheet panel (EE, `zkmax/wgt/css/drawer.css`) — `.z-drawer` fixed-position container +
`.z-drawer-mask` backdrop scrim + `.z-drawer-real` sliding panel + `.z-drawer-header` +
`.z-drawer-close` button + `.z-drawer-container`/`-cave` content area. All four directions
(left/right/top/bottom) share the same ruleset. `--zk-drawer-elevation` is a single **static**
shadow — every position gets the same shadow, unlike window's mode-driven elevation (same
convention as panel's resting-elevation knob). `--zk-drawer-border-color` is the header's bottom
divider, the panel's only dividing line (same convention as window/panel's shared border-color
knob). The close button's hover fill (`--zk-drawer-close-hover-bg`) mirrors the
window/panel/groupbox icon-hover-bg precedent; its resting/hover text color and the backdrop
mask's scrim color (a literal `rgba(0, 0, 0, 0.32)`, distinct from `--zk-color-scrim`'s `0.5`)
stay on base tokens/literals, not knob-driven (same convention as window's icon text color and the
shared modal-mask scrim). The panel renders edge-to-edge with no border-radius, so there is no
radius knob.

**CTV-3 (region scoping) is structurally N/A here, not a defect** — same exception class as
messagebox/popup, for a different root cause. `Drawer.prototype.setVisible()` calls
`zk.makeVParent()` on open (reparenting the **entire** `.z-drawer` root — header + real + close +
mask together — to the floating root, `document.body`) and `undoVParent()` on close (confirmed in
the compiled zkmax 10.4 widget bundle). Verified empirically: overriding
`--zk-drawer-border-color`/`--zk-drawer-header-fg` on a container ancestor of the trigger button
does not reach the open drawer (its `parentElement` is `BODY`, a sibling of the container, not a
descendant); the same override at `:root` hits normally. Unlike the
combobox/datebox/…/searchbox popup family — which reparents only the popup and leaves a trigger
wrapper in place to assert region scoping against — drawer reparents its whole root, so there is
no in-place remnant to test region scoping on; only the whole-app (`:root`) path is exercised.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-drawer-bg` | `var(--zk-color-surface)` | sliding panel fill |
| `--zk-drawer-border-color` | `var(--zk-color-outline-variant)` | header bottom divider |
| `--zk-drawer-elevation` | `var(--zk-elevation-3)` | panel shadow (static — every direction, not mode-driven) |
| `--zk-drawer-header-fg` | `var(--zk-color-on-surface)` | header title text |
| `--zk-drawer-close-hover-bg` | `var(--zk-color-surface-container)` | close-button hover fill |

### Nav — shipped

Side/top navigation (EE, `zkmax/nav/css/nav.css`) — `.z-navbar` container + collapsible nav
group `.z-nav` (header `.z-nav-content`) + leaf `.z-navitem` (link `.z-navitem-content`) +
divider `.z-navseparator`. `--zk-navbar-bg` is the container's own tonal surface fill (MD3
Navigation Drawer convention — always a tonal step, never inherits the page background).
`--zk-navitem-fg` and `--zk-navitem-radius` are shared by **both** the collapsible group header
link (`.z-nav-content`) and the leaf item link (`.z-navitem-content`) — one ruleset styles both
today, the same "one knob, several roles" precedent as tab/calendar's accent. The selected leaf
item is the component's defining state — a rounded tonal pill (MD3 Navigation Drawer's active
indicator, no left-edge accent bar; see `doc/contracts/navbar.md` c7) — so it gets its own
fill/text pair, `--zk-navitem-selected-bg` / `-fg`, the same pairing precedent as listbox/tree's
`-selected-bg`/`-fg`. The group header's own open/selected color swap
(`.z-nav-open`/`.z-nav-selected > .z-nav-content`), the hover state layer (`::before` overlay +
navitem's `rgba` hover fill), the group-label text (`.z-nav-header`), the horizontal mode's
submenu/overflow popup surface (`.z-nav-popup`, `.z-navbar-horizontal .z-nav > ul`), and the
badge (`.z-nav-info`/`.z-navitem-info`, which read `--zk-color-primary`/`-on-primary` directly)
stay on base tokens — secondary sub-features kept out of the curated surface (same convention as
calendar/toolbar's minor sub-features). Disabled dims via its own `--zk-color-disabled` literal,
not knob-driven (same convention as button/input/rating). `.z-navbar` renders in place with no
client-side reparenting, so region scoping works normally — unlike messagebox/popup/drawer's
structural exceptions.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-navbar-bg` | `var(--zk-color-surface-container-low)` | navbar container fill |
| `--zk-navitem-fg` | `var(--zk-color-on-surface-variant)` | group-header + leaf item link text |
| `--zk-navitem-radius` | `var(--zk-shape-corner-small)` | group-header + leaf item link corners |
| `--zk-navitem-selected-bg` | `color-mix(in srgb, var(--zk-color-primary) 12%, transparent)` | selected leaf item pill fill |
| `--zk-navitem-selected-fg` | `var(--zk-color-primary)` | selected leaf item text |

### Anchornav — shipped

Anchor-link navigation (EE, `zkmax/nav/css/anchornav.css`) — `.z-anchornav` wraps `a` or `button`
links that jump to page sections. No background, border, or radius knob applies — the container
paints no fill/border of its own. Only two color axes: `--zk-anchornav-fg` is the resting link's
text color. The active link is the component's one defining state — Anchornav.ts adds
`.z-anchornav-link-active` to it, and `--zk-anchornav-accent` colors an `a` link's text (with a
heavier weight) and the outline of a `button` link (a `button` is already a filled primary
button, so its text color cannot carry the state). One knob keeps both in sync, the same "one knob,
several roles" precedent as tab/calendar/nav's accent. The outline's width and offset stay literals, not
knob-driven (same convention as tab's `border-bottom-color` knob — width stays fixed, only color is
exposed). `text-decoration: none` is a structural declaration, not a themeable appearance axis, so
it isn't exposed. `.z-anchornav` renders in place with no client-side reparenting, so region
scoping works normally — unlike messagebox/popup/drawer's structural exceptions.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-anchornav-fg` | `var(--zk-color-on-surface-variant)` | resting item link text |
| `--zk-anchornav-accent` | `var(--zk-color-primary)` | active `a` link text, active `button` link outline |

### Stepbar — shipped

Connected-circle step-progress indicator (EE, `zkmax/wgt/css/stepbar.css`) — `.z-stepbar` container
+ `.z-step` step item + `.z-step-content` wrapper (always present) + `.z-step-icon` circular marker
+ `.z-step-title` label text. `--zk-stepbar-connector-color` is the resting (upcoming) connector-line
color — it's layered onto both the inline `.z-step::before` segment and the wrapped-label mode's
`.z-step-content::before`/`::after` halves (both read the same token today), the same "one knob,
several roles" precedent as toolbar/messagebox's shared border-color. `--zk-stepbar-icon-border-color`
is the upcoming (empty) circle's outline color. Active and complete are visually the same defining
state — a filled primary circle — so a single `--zk-stepbar-accent` drives both the icon fill/border
and the lit connector (inline, vertical, and wrapped-label variants alike), the same "one knob,
several roles" precedent as tab/calendar's accent; `--zk-stepbar-accent-fg` is the paired glyph color
inside that filled circle (the same accent/accent-fg pairing precedent as calendar).
`--zk-stepbar-fg` is the title text color shared by the resting and complete states (both read the
same token today); `--zk-stepbar-fg-active` is the active state's title text color — a
"resting + active" pairing precedent like tab's fg/fg-hover. The error state (icon fill/border/text
+ title) stays on `--zk-color-error`/`-on-error` directly, not knob-driven — the same convention as
datebox/timebox/timepicker's invalid state. The circle's `border-radius` (50%) is a fixed shape
constraint, not a themeable knob (same convention as badge's dot mode / avatar). There is no
background knob: the root renders `background: transparent` by design — it's meant to sit on the
embedding page's own surface, not read as its own card (see `doc/contracts/stepbar.md` s5).
`.z-stepbar` renders in place with no client-side reparenting, so region scoping works normally —
unlike messagebox/popup/drawer's structural exceptions.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-stepbar-connector-color` | `var(--zk-color-outline-variant)` | resting (upcoming) connector line — inline `::before` + wrapped-label `::before`/`::after` |
| `--zk-stepbar-icon-border-color` | `var(--zk-color-outline)` | upcoming (empty) circle outline |
| `--zk-stepbar-fg` | `var(--zk-color-on-surface-variant)` | resting + complete step title text |
| `--zk-stepbar-fg-active` | `var(--zk-color-on-surface)` | active step title text |
| `--zk-stepbar-accent` | `var(--zk-color-primary)` | active/complete icon fill + border, lit connector |
| `--zk-stepbar-accent-fg` | `var(--zk-color-on-primary)` | active/complete icon glyph color |

### Coachmark — shipped

MD3 guided-tour rich-tooltip card (EE, `zkmax/nav/css/coachmark.css`) pointing at a target
element — `.z-coachmark-content` card body + `.z-coachmark-pointer` directional triangle
(up/down/left/right) + `.z-coachmark-close` button. `--zk-coachmark-bg` is shared by **both** the
card's own fill **and** all four pointer-triangle variants' `border-color` (all four read the same
token today, so the triangle always reads as an extension of the card), the same "one knob,
several roles" precedent as tab/calendar's accent. The close button's icon color, its hover/focus
overlay tint, and the pointer triangle's fixed `10px` border-width stay on base tokens/literals,
not knob-driven — minor sub-features kept out of the curated surface (same convention as
calendar/toolbar's minor sub-features).

**CTV-3 (region scoping) is structurally N/A here, not a defect** — same exception class as
drawer, for the same root cause. `Coachmark.prototype._open()` calls `zk(n).makeVParent()` on the
root `.z-coachmark` node (reparenting the **entire** root — content + pointer + close together —
to the floating root, `document.body`) and `undoVParent()` on close (confirmed in the compiled
zkmax 10.4 widget bundle). As with drawer, there is no in-place remnant to test region scoping
against; only the whole-app (`:root`) path is exercised.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-coachmark-bg` | `var(--zk-color-surface-container-low)` | card fill + all four pointer-triangle border-colors |
| `--zk-coachmark-fg` | `var(--zk-color-on-surface)` | card text |
| `--zk-coachmark-radius` | `var(--zk-shape-card)` | card corners |
| `--zk-coachmark-elevation` | `var(--zk-elevation-2)` | card shadow |

### Colorbox — shipped

Color-picker swatch trigger + gradient/palette popup (PE, `zkex/inp/css/colorbox.css`) —
`.z-colorbox` root + `.z-colorbox-current` color swatch + `.z-colorbox-button` caret button +
`.z-colorbox-popup` (detached to `<body>` when open; popup chrome is shared with the
menu-content mold's `.z-menu-popup`). Unlike the other wrapper-border field components
(datebox/timebox/spinner/bandbox/daterangebox/timepicker/chosenbox/cascader/searchbox),
`.z-colorbox` has only resting + hover states — no focus/open border state — so there is no
`-border-color-focus` knob. There is also no fg/text knob: the root shows no text, only the
swatch (its color set inline per selection) and a caret icon. Six knobs total:
`--zk-colorbox-bg`/`-radius`/`-border-color`/`-border-color-hover` style the wrapper itself;
`-popup-bg`/`-popup-radius` follow the same bg/radius vocabulary as the rest of the
dropdown-input family's popups — the popup's own `border-color` and `box-shadow` stay on base
tokens, the same convention as combobox/datebox/bandbox's popups. The swatch's own border
(`.z-colorbox-current`) and the caret-button icon color (`.z-colorbox-button`) stay on base
tokens too, not knob-driven — minor sub-features kept out of the curated surface (same
convention as cascader/searchbox's trigger icons). Disabled dims via opacity only, not
knob-driven; its `:hover` border stays the resting outline literal (`--zk-color-outline`), not
the hover knob, the same convention as datebox/timebox/timepicker's disabled treatment. Only
`.z-colorbox`'s popup reparents to `<body>` on open — the wrapper itself renders in place with
no client-side reparenting, the same structure as the rest of the combobox/datebox/…/searchbox
family, so region scoping is exercised on the wrapper the same way as those family members.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-colorbox-bg` | `var(--zk-color-surface)` | wrapper fill |
| `--zk-colorbox-radius` | `var(--zk-shape-input)` | wrapper corners |
| `--zk-colorbox-border-color` | `var(--zk-color-outline)` | wrapper resting border |
| `--zk-colorbox-border-color-hover` | `var(--zk-color-on-surface)` | wrapper hover border |
| `--zk-colorbox-popup-bg` | `var(--zk-color-surface)` | popup fill |
| `--zk-colorbox-popup-radius` | `var(--zk-shape-menu)` | popup corners |

### Biglistbox — shipped

Virtual/lazy-loading data grid (EE, `zkmax/big/css/biglistbox.css`) — `.z-biglistbox` root +
`.z-biglistbox-header` column header + `.z-biglistbox-row td` data cell + a self-drawn `WScroll`
scrollbar (see `doc/contracts/biglistbox.md`). Shares the same curated surface as the Grid
family — this component has no stripe/foot rows to mirror, so those two knobs aren't repeated.
`--zk-biglistbox-border-color` drives three roles at once: the root's own `border`, the header's
`border-bottom`, and every data row's `border-bottom` (all three read the same token today), the
same "one knob, several roles" precedent as grid's `-border-color`. `--zk-biglistbox-header-fg`
is the header text color. `--zk-biglistbox-row-hover-bg` keeps the stock `rgba(0, 0, 0, 0.04)`
literal for zero regression, the same convention as grid's `-row-hover-bg`. The sort-icon color
(`.z-biglistbox-sorticon`), the sort-hover fill (`.z-biglistbox-sort:hover`), and the self-drawn
scrollbar's thumb/track/groove colors stay on base tokens, not knob-driven — minor sub-features
kept out of the curated surface (same convention as grid's column-sort-icon/sort-hover). The data
cell's text color (`.z-biglistbox-row td { color }`) also stays on a base token, matching grid
(which has no `--zk-grid-fg` either). Cell padding is a size-dimension concern, not added here —
size stays in `_sizing.css`, untouched by this pass (same convention as the rest of the family).
`.z-biglistbox` renders in place with no client-side reparenting, so region scoping works
normally.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-biglistbox-bg` | `var(--zk-color-surface)` | root fill |
| `--zk-biglistbox-border-color` | `var(--zk-color-outline-variant)` | root border + header bottom border + row bottom border |
| `--zk-biglistbox-radius` | `var(--zk-shape-card)` | root corners |
| `--zk-biglistbox-header-fg` | `var(--zk-color-on-surface-variant)` | header text |
| `--zk-biglistbox-row-hover-bg` | `rgba(0, 0, 0, 0.04)` | row hover fill |

### Fisheye / Fisheyebar — shipped

Magnetic dock icon bar (PE, `zkex/menu/css/fisheye.css`) — `.z-fisheyebar` container +
`.z-fisheye` item (`.z-fisheye-image` icon + `.z-fisheye-text` label). Magnification is
JS-driven (`zul.menu.Fisheye` sets inline `width`/`height` on `mousemove`), so there is no
hover/selected color state to expose — the curated surface is just the label text color and the
icon's corner radius: `--zk-fisheye-fg` (`.z-fisheye-text`'s `color`), `--zk-fisheye-radius`
(`.z-fisheye-image`'s `border-radius`). **No bg/border knob**: `.z-fisheyebar` keeps
`background: transparent` by design — a floating dock overlay, not a card, the same convention as
stepbar's transparent root — so it isn't listed as a variable. `.z-fisheyebar` renders in place
with no client-side reparenting, so region scoping works normally.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-fisheye-fg` | `var(--zk-color-inverse-on-surface)` | label text (tooltip chip on `inverse-surface`) |
| `--zk-fisheye-radius` | `var(--zk-shape-corner-extra-small)` | icon corners |

### Pdfviewer — shipped

PDF document viewer (PE, `zkex/pdfviewer/css/pdfviewer.css`) — `.z-pdfviewer` root wrapper +
`.z-pdfviewer-container` scrollable canvas + a floating, bottom-centred `.z-pdfviewer-toolbar`
(pill-shaped, icon buttons). Seven knobs total. `--zk-pdfviewer-border-color` drives two roles at
once: the root wrapper's own `border` and the toolbar separator's (`.z-pdfviewer-toolbar-separator`)
`border-left` (both read the same token today) — the same "one knob, several roles" precedent as
toolbar/messagebox's shared border-color. `--zk-pdfviewer-bg`/`-radius` are the root wrapper's
fill and corners. The canvas area (`--zk-pdfviewer-container-bg`) and the floating toolbar
(`--zk-pdfviewer-toolbar-bg`/`-radius`/`-fg`) are visually distinct surfaces from the root card,
each with their own fill/radius — `-toolbar-fg` is the toolbar icon buttons' resting icon color.
The per-page `box-shadow` (paper-lift effect) and the floating toolbar's own `box-shadow` stay on
base tokens, not knob-driven — same convention as the wrapper-border family's popup shadows
(combobox/datebox/bandbox). Toolbar-button hover/active state-layer tints (`color-mix` on
`--zk-color-on-surface`) and the disabled icon color also stay on base tokens — minor
sub-features kept out of the curated surface (same convention as calendar/toolbar). `.z-pdfviewer`
renders in place with no client-side reparenting, so region scoping works normally.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-pdfviewer-bg` | `var(--zk-color-surface)` | root fill |
| `--zk-pdfviewer-border-color` | `var(--zk-color-outline-variant)` | root border + toolbar separator border |
| `--zk-pdfviewer-radius` | `var(--zk-shape-corner-medium)` | root corners |
| `--zk-pdfviewer-container-bg` | `var(--zk-color-surface-variant)` | scrollable canvas fill |
| `--zk-pdfviewer-toolbar-bg` | `var(--zk-color-surface-container-high)` | floating toolbar fill |
| `--zk-pdfviewer-toolbar-radius` | `var(--zk-shape-corner-full)` | floating toolbar corners |
| `--zk-pdfviewer-toolbar-fg` | `var(--zk-color-on-surface)` | toolbar icon buttons' resting icon color |

### Tbeditor — shipped

MD3 outlined rich-text editor (EE, `zkmax/tbeditor/css/tbeditor.css`) — `.z-tbeditor-box` outer
wrapper + `.z-tbeditor-button-pane` toolbar + `.z-tbeditor-editor` contenteditable canvas (+
`.z-tbeditor-textarea` HTML-source view) + `.z-tbeditor-dropdown` formatting/link popup. Ten
knobs total. `--zk-tbeditor-bg` is shared by the wrapper, the editor canvas, and the source
textarea (all three read the same token today) — the same "one knob, several roles" precedent as
tab/calendar's accent; `--zk-tbeditor-fg` likewise covers the editor/textarea text and the
dropdown button labels. The wrapper's only border states are resting + focus-within (no hover),
so there is **no** `-border-color-hover` knob. `--zk-tbeditor-elevation` is the wrapper's static
resting shadow (not mode-driven), the same convention as panel/groupbox's resting elevation.
`--zk-tbeditor-toolbar-bg` is the button-pane's own tonal surface, distinct from the wrapper fill
(same convention as pdfviewer's floating toolbar). `--zk-tbeditor-active-bg` is the toggled
formatting button's fill (`.z-tbeditor-active`, e.g. Bold when the caret is inside bold text) —
the defining state. The dropdown popup gets its own `-popup-bg`/`-popup-radius` pair, following
the same vocabulary as the rest of the family's popups; its `border-color` and `box-shadow` stay
on base tokens, the same convention as combobox/datebox/bandbox/colorbox's popups. Toolbar-button
hover/active state-layer tints (`color-mix` on `--zk-color-primary`), the SVG icon fill
(resting/hover/active), the dropdown-caret affordance, and the button-group separator stay on
base tokens — minor sub-features kept out of the curated surface (same convention as
calendar/toolbar). Disabled dims the toolbar buttons via opacity only and keeps the wrapper border
on its own muted literal, not knob-driven (same convention as datebox/timebox/timepicker's
disabled treatment); fullscreen mode's border/radius/box-shadow reset to `none`, also not
knob-driven. `.z-tbeditor-box` renders in place with no client-side reparenting, so region scoping
works normally.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-tbeditor-bg` | `var(--zk-color-surface)` | wrapper + editor canvas + source textarea fill |
| `--zk-tbeditor-fg` | `var(--zk-color-on-surface)` | editor/textarea text + dropdown button labels |
| `--zk-tbeditor-radius` | `var(--zk-shape-corner-small)` | wrapper corners |
| `--zk-tbeditor-border-color` | `var(--zk-color-outline)` | wrapper resting border |
| `--zk-tbeditor-border-color-focus` | `var(--zk-color-primary)` | wrapper focus-within border |
| `--zk-tbeditor-elevation` | `var(--zk-elevation-1)` | wrapper resting shadow |
| `--zk-tbeditor-toolbar-bg` | `var(--zk-color-surface-container)` | button-pane fill |
| `--zk-tbeditor-active-bg` | `var(--zk-color-primary-container)` | toggled formatting-button fill |
| `--zk-tbeditor-popup-bg` | `var(--zk-color-surface)` | dropdown popup fill |
| `--zk-tbeditor-popup-radius` | `var(--zk-shape-menu)` | dropdown popup corners |

### Signature — shipped

Signature-pad canvas field (EE, `zkmax/signature/css/signature.css`) — `.z-signature` root
wrapper (canvas + a floating toolbar of icon-only tool buttons). Four knobs total.
`--zk-signature-bg`/`-radius` are the root wrapper's fill and corners; `--zk-signature-border-color`/
`-border-color-focus` are the wrapper's resting and focus-within border colors. The wrapper's only
border states are resting + focus-within (no hover), so there is **no** `-border-color-hover` knob
— same convention as tbeditor's no-hover exception. There is also **no** fg/text knob: the root
shows no text, only the canvas and the toolbar's icon-only buttons. The floating toolbar's tool
buttons (background/border/hover/active state, icon color, box-shadow) stay on base tokens, not
knob-driven — a secondary sub-feature kept out of the curated surface (same convention as
calendar/toolbar's minor sub-features). Disabled dims via opacity only, not knob-driven (same
convention as button/input/rating). `.z-signature` renders in place with no client-side
reparenting, so region scoping works normally.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-signature-bg` | `var(--zk-color-surface)` | root fill |
| `--zk-signature-radius` | `var(--zk-shape-corner-medium)` | root corners |
| `--zk-signature-border-color` | `var(--zk-color-outline)` | wrapper resting border |
| `--zk-signature-border-color-focus` | `var(--zk-color-primary)` | wrapper focus-within border |

### Cropper — shipped

Image-crop field (EE, `zkmax/cropper/css/cropper.css`) — `.z-cropper` root wrapper (shrink-wraps
its border to the image, no fill of its own) plus a floating, pill-shaped `.z-cropper-toolbar`
carrying two text action links (Crop / Cancel). Six knobs total. There is **no** bg knob on the
root: `.z-cropper` paints no background — it wraps the (opaque) image, so only its border-color
and radius are exposed. There is also **no** `-border-color-hover` knob: the wrapper has no
interactive hover state, just a resting border — same convention as tbeditor/signature's no-hover
exception. The floating toolbar is the component's one filled surface, so it gets its own
`-toolbar-bg`/`-toolbar-radius` pair, the same convention as pdfviewer's floating toolbar; its
`box-shadow` stays on the base elevation token, not knob-driven — same convention as
pdfviewer/tbeditor's toolbar and popup shadows. The two action links follow a rating-style
resting/accent pair: `--zk-cropper-fg` is the Cancel (dismissive, neutral) link text,
`--zk-cropper-accent` is the Crop (confirming) link text — the defining action. Both links' hover/
active state-layer tint is a `currentColor` overlay, so it automatically follows whichever of
`-fg`/`-accent` is in effect — no separate state-layer knob needed. The Jcrop-injected selection
geometry (drag handles, dragbars, selection outline/lines) keeps its literal white — an
intentional Jcrop visibility convention over a dimmed photo, not a token, and not adopter-facing
appearance. `.z-cropper` renders in place with no client-side reparenting, so region scoping works
normally.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-cropper-border-color` | `var(--zk-color-outline-variant)` | root wrapper resting border |
| `--zk-cropper-radius` | `var(--zk-shape-corner-extra-small)` | root wrapper corners |
| `--zk-cropper-toolbar-bg` | `var(--zk-color-surface-container)` | floating toolbar fill |
| `--zk-cropper-toolbar-radius` | `var(--zk-shape-corner-full)` | floating toolbar corners |
| `--zk-cropper-fg` | `var(--zk-color-on-surface-variant)` | Cancel (dismissive) link text |
| `--zk-cropper-accent` | `var(--zk-color-primary)` | Crop (confirming) link text — defining action |

### Dropupload — shipped

HTML5 drag-and-drop file-upload drop zone (EE, `zkmax/wgt/css/dropupload.css`) — `.z-dropupload`
root is the **only** themable surface: the resting box's border, radius, and background. Three
knobs total. ZK never emits a drag-over or disabled state class (verified against
`Dropupload.ts`/`.java` — drag feedback is the native browser cursor only, see
`.claude/skills/zk-component-rules/components/dropupload.md`), so there is **no** hover/focus/
disabled knob. There is also **no** fg/text knob: the `content` attribute is arbitrary author
HTML appended as a child and toggled visible/hidden — the component itself never repaints it.
`border-width` (2px) and `border-style` (dashed) stay on their literal values, not knob-driven —
same convention as the rest of the family (only border-color is exposed, not width/style).
`.z-dropupload` renders in place with no client-side reparenting, so region scoping works
normally.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-dropupload-bg` | `transparent` | root fill (paints no background of its own) |
| `--zk-dropupload-border-color` | `var(--zk-color-outline)` | root resting border |
| `--zk-dropupload-radius` | `var(--zk-shape-corner-extra-small)` | root corners |

### Organigram — shipped

Org-chart tree (EE, zkmax, `zkmax/layout/css/organigram.css`) — `.z-organigram` root plus
`.z-orgnode` node card (the visible chip), `.z-orgchildren`/`.z-orgitem` connector lines (a
horizontal bus segment and drop-down/drop-out vertical segments, all drawn via `::before`/
`::after`), and `.z-orgnode-icon` expand/collapse glyph. Ten knobs total.
`--zk-organigram-border-color` is shared by the node card's own resting border **and** every
connector-line role (the bus segment and both vertical drop directions) — one knob, several
roles, the same convention as grid/messagebox/toolbar's shared border-color. Hover swaps the
card's border to `-border-color-hover` and its fill to `-hover-bg` (kept as the stock rgba
literal, the same convention as grid/combobox's row-hover-bg). Selected is the defining state —
its own bg/border/fg triad, the same three-axis shape as chip's bg/border/color; the selected
node's icon reads the same `-selected-fg` (one knob, several roles, the same convention as
tab/calendar's accent). The resting icon color gets its own `-icon-fg`. Disabled dims via opacity
only and keeps its background on the base `--zk-color-surface` token, not the knob, so overriding
`-bg` doesn't get inherited into the disabled state and lose its state distinction (CTV-7, the
same convention as button/input/rating's disabled treatment). Focus stays on the global
`--zk-focus-ring` (the same convention as button/window/grid), not a knob. `.z-organigram` renders
in place with no client-side reparenting, so region scoping works normally.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-organigram-bg` | `var(--zk-color-surface)` | node card resting fill |
| `--zk-organigram-fg` | `var(--zk-color-on-surface)` | node card resting text |
| `--zk-organigram-border-color` | `var(--zk-color-outline-variant)` | node card resting border + all connector-line segments (bus, drop-down, drop-out) |
| `--zk-organigram-border-color-hover` | `var(--zk-color-on-surface)` | node card hover border |
| `--zk-organigram-radius` | `var(--zk-shape-card)` | node card corners |
| `--zk-organigram-hover-bg` | `rgba(0, 0, 0, 0.04)` | node card hover fill |
| `--zk-organigram-selected-bg` | `var(--zk-color-primary-container)` | selected node card fill |
| `--zk-organigram-selected-border-color` | `var(--zk-color-primary)` | selected node card border |
| `--zk-organigram-selected-fg` | `var(--zk-color-on-primary-container)` | selected node card text + selected node's icon |
| `--zk-organigram-icon-fg` | `var(--zk-color-on-surface-variant)` | resting expand/collapse icon color |

### Goldenlayout — shipped

Dockable tab layout (EE, `zkmax/goldenlayout/css/goldenlayout.css`) — `.z-goldenlayout` root
(transparent, no card framing of its own) + `.lm_header` tab strip + `.lm_tab` (+ `.lm_close_tab`)
+ `.lm_controls` header icon buttons + `.z-goldenpanel` per-panel content card. Eight knobs total.
`--zk-goldenlayout-border-color` is shared by the header strip's bottom divider **and** the
goldenpanel's own border (both read the same token today) — one knob, several roles, the same
convention as grid/messagebox/toolbar's shared border-color. `--zk-goldenlayout-radius` likewise
drives **both** the header's top corners and the goldenpanel's corners (both read
`var(--zk-shape-card)` today), the same convention as calendar's bg covering card + header fill.
`--zk-goldenlayout-fg` / `-fg-hover` are the resting/hover text shared by the tab label, the
close-tab icon, and the header-controls icon (all three read the same values today) — the same
"one knob, several roles" precedent as tab's fg/fg-hover. `--zk-goldenlayout-accent` is the one
defining-state color: the active tab's label + underline, the tab's MD3 state-layer overlay tint,
and the header-controls hover icon (all read `var(--zk-color-primary)` today) — the same "one
knob, several roles" precedent as tab/calendar's accent. `--zk-goldenlayout-panel-elevation` is
the goldenpanel's static resting shadow (not mode-driven), the same convention as
panel/groupbox's resting elevation. The drag-proxy (`.z-goldenlayout-dragProxy`), the drop-target
indicator, and the overflow-tab dropdown (`.z-goldenlayout-dropdown`) stay on base tokens —
transient/secondary sub-features kept out of the curated surface (same convention as
calendar/toolbar's minor sub-features). `.z-goldenlayout`/`.z-goldenpanel` render in place with no
client-side reparenting, so region scoping works normally.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-goldenlayout-header-bg` | `var(--zk-color-surface-container)` | tab-strip fill |
| `--zk-goldenlayout-panel-bg` | `var(--zk-color-surface)` | goldenpanel content fill |
| `--zk-goldenlayout-border-color` | `var(--zk-color-outline-variant)` | header bottom divider + goldenpanel border |
| `--zk-goldenlayout-radius` | `var(--zk-shape-card)` | header top corners + goldenpanel corners |
| `--zk-goldenlayout-fg` | `var(--zk-color-on-surface-variant)` | resting tab text + close-icon + header-controls icon |
| `--zk-goldenlayout-fg-hover` | `var(--zk-color-on-surface)` | tab hover text + close-icon hover text |
| `--zk-goldenlayout-accent` | `var(--zk-color-primary)` | active tab text + underline, state-layer tint, header-controls hover |
| `--zk-goldenlayout-panel-elevation` | `var(--zk-elevation-1)` | goldenpanel resting shadow |

### Portallayout — shipped

Transparent multi-column drag-drop dashboard shell (EE, `zkmax/layout/css/portallayout.css`) —
`.z-portallayout` root + `.z-portalchildren` column (both always `background: transparent` by
design, a layout shell rather than a card, same convention as stepbar/fisheyebar's transparent
root) + the **optional** `.z-portalchildren-frame` card chrome (shown only when a column carries
a `title=""` attribute) + its `.z-portalchildren-title` text + the
`.z-portalchildren-counter-on` panel-count badge. `--zk-portallayout-bg` / `-border-color` /
`-radius` style the frame — the component's **only** filled/carded surface (a plain, unframed
column has nothing to knob); `--zk-portallayout-fg` is the frame's title text color. The
panel-count badge is a visually distinct sub-part with its own bg/fg/radius triad
(`--zk-portallayout-counter-bg` / `-fg` / `-radius`), the same "sub-part gets its own knob set"
precedent as pdfviewer's toolbar/container knobs. The drag ghost (`.z-panel-move-ghost`) and drop
placeholder (`.z-panel-move-block`) are transient drag-feedback surfaces — kept on base tokens,
not knob-driven, the same convention as goldenlayout's drag-proxy / drop-target indicator.
`.z-portallayout` renders in place with no client-side reparenting (only the drag ghost is
prepended to `<body>`, and only for the duration of an active drag), so region scoping works
normally.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-portallayout-bg` | `var(--zk-color-surface)` | framed-column fill |
| `--zk-portallayout-border-color` | `var(--zk-color-outline-variant)` | framed-column border |
| `--zk-portallayout-radius` | `var(--zk-shape-card)` | framed-column corners |
| `--zk-portallayout-fg` | `var(--zk-color-on-surface-variant)` | framed-column title text |
| `--zk-portallayout-counter-bg` | `var(--zk-color-primary-container)` | panel-count badge fill |
| `--zk-portallayout-counter-fg` | `var(--zk-color-primary)` | panel-count badge text |
| `--zk-portallayout-counter-radius` | `var(--zk-shape-corner-full)` | panel-count badge shape |

### Confirmpopup — shipped

Lightweight anchored confirmation popover (CE, `zul/wgt/css/confirmpopup.css`) — a Popover
analog, **not** a modal dialog: `.z-confirmpopup` card + a two-layer CSS-triangle `.z-confirmpopup-arrow`
+ an optional `.z-confirmpopup-header` + an unconditional `.z-confirmpopup-body` (icon + optional
message) + an unconditional `.z-confirmpopup-footer` (Cancel/OK). `--zk-confirmpopup-bg` covers
**both** the card's own fill **and** the arrow's inner fill layer (all four placements read the
same surface token today), the same "one knob, several roles" precedent as tab/calendar/coachmark's
shared fill. `--zk-confirmpopup-header-fg` covers **both** the root's own text color **and** the
optional header's title text (both read on-surface); the message body reads the muted tone via its
own `--zk-confirmpopup-fg` — the same "header vs body" fg pairing precedent as messagebox's
`-header-fg`/`-fg`. `--zk-confirmpopup-border-color` covers the arrow's outer edge layer (all four
placements) **and** both dividers (header's `border-bottom`, footer's `border-top`) — the same
"one knob, several dividers" precedent as toolbar/messagebox's shared border-color. Severity
recolors **only the icon** (identical severity→status-token mapping as chip/badge) and stays on
its own semantic tokens, intentionally not knob-driven (same convention as
button/progressmeter/messagebox's color/type variants). The footer's OK/Cancel are plain native
`<button>` elements, **not** `.z-button` widgets, so they get their own knobs rather than reusing
`--zk-button-*`: `--zk-confirmpopup-button-radius` is shared by both (one shape, same precedent as
slider's shared track/fill/thumb radius); OK is the high-emphasis filled/committing action
(`-ok-bg`/`-ok-fg`, the latter also driving its `::before` state-layer tint — same value today, one
knob); Cancel is the low-emphasis outlined/dismissal action (`-cancel-border-color`/`-cancel-fg`,
the latter likewise driving its own `::before` tint). Both buttons' focus ring stays on the global
`--zk-focus-ring`, not a knob (same convention as button/window/grid).

**CTV-3 (region scoping) is structurally N/A here, not a defect** — the identical root cause as
popup, its closest sibling. `Confirmpopup` extends `Popup` and its `open()` override calls
`super.open(...)`, inheriting `Popup.prototype.open()`'s own `zk.makeVParent()` call, which
reparents the widget's real DOM node to `document.body` (confirmed in
`zul/src/main/resources/web/js/zul/wgt/Popup.ts` and `Confirmpopup.ts`). A region override on an
ancestor of the triggering button cannot reach the open popover for the same reason it cannot reach
an open `.z-popup`; only the whole-app (`:root`) override path applies, and is unaffected.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-confirmpopup-bg` | `var(--zk-color-surface-container-low)` | card fill + arrow inner-fill layer |
| `--zk-confirmpopup-fg` | `var(--zk-color-on-surface-variant)` | message body text |
| `--zk-confirmpopup-header-fg` | `var(--zk-color-on-surface)` | root text color + header title text |
| `--zk-confirmpopup-border-color` | `var(--zk-color-outline-variant)` | arrow outer-edge layer + header/footer dividers |
| `--zk-confirmpopup-radius` | `var(--zk-shape-card)` | card corners |
| `--zk-confirmpopup-elevation` | `var(--zk-elevation-2)` | card shadow (static — always the same, not mode-driven) |
| `--zk-confirmpopup-button-radius` | `var(--zk-shape-button)` | OK + Cancel shared corner radius |
| `--zk-confirmpopup-ok-bg` | `var(--zk-color-primary)` | OK button fill |
| `--zk-confirmpopup-ok-fg` | `var(--zk-color-on-primary)` | OK button text + its state-layer tint |
| `--zk-confirmpopup-cancel-border-color` | `var(--zk-color-outline)` | Cancel button outline |
| `--zk-confirmpopup-cancel-fg` | `var(--zk-color-on-surface-variant)` | Cancel button text + its state-layer tint |

### Breadcrumb — shipped

Inline text-chrome navigation trail (CE, `zul/wgt/css/breadcrumb.css`) — rendered as borderless
inline text like an `a`/`caption`, **not** a card: `.z-breadcrumb` root `<nav>` +
`.z-breadcrumb-list` `<ol>` + `.z-breadcrumbitem` entries (a link `<a>` or the terminal current
`<span>`) + `.z-breadcrumb-separator` + a client-injected collapse `.z-breadcrumb-ellipsis`
button. There is deliberately **no `-bg` / `-border-color` / `-radius` knob** — the trail has no
card surface for those axes to theme (the same curation choice as `a`). `--zk-breadcrumb-fg`
covers **three** roles at once — the separator, the ellipsis button (resting), and a link item
(resting) — all three share the identical muted tone by design, the same "one knob, several
roles" precedent as tab's `--zk-tab-fg` / calendar's `--zk-calendar-accent`.
`--zk-breadcrumb-fg-hover` covers link-hover/focus and ellipsis-hover/focus together (same
precedent). `--zk-breadcrumb-current-fg` is the one defining-state knob — the terminal "you are
here" item's full-emphasis color, matching the "one knob per defining state" precedent
(chosenbox's `-item-focus-bg`, tab's `-accent`). The disabled item's opacity fade, the ellipsis
button's stripped native-button chrome (`background`/`border`/`padding` zeroed), and the focus
ring (global `--zk-focus-ring`) stay on base tokens/literals, not knob-driven (same convention as
button/window/grid). Breadcrumb renders in place with no client-side reparenting, so region
scoping (CTV-3) works normally — unlike popup/messagebox/confirmpopup.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-breadcrumb-fg` | `var(--zk-color-on-surface-variant)` | separator + ellipsis button (resting) + link item (resting) |
| `--zk-breadcrumb-fg-hover` | `var(--zk-color-on-surface)` | link + ellipsis hover/focus |
| `--zk-breadcrumb-current-fg` | `var(--zk-color-on-surface)` | terminal "current page" item text |

### Carousel — shipped

Full-bleed media frame (CE, `zul/wgt/css/carousel.css`) — `.z-carousel` root (clipped to a
rounded rectangle) + `.z-carousel-track` flex track + `.z-carouselitem` slides + overlay
`.z-carousel-arrow-prev`/`-next` + overlay `.z-carousel-indicators`/`.z-carousel-indicator` + an
optional `.z-carouselitem-label` caption chip. Ten knobs. One shape knob (`--zk-carousel-radius`)
is shared by the root frame **and** the caption chip — both are "containers" under DESIGN.md §5's
container/control rule, so one knob moves both together (see c1/c27). The nine color knobs split
three ways by overlay region (arrow / indicator / label), each region pairing a "dark voice"
(defaults to `--zk-color-scrim`) with a "light voice" (defaults to `--zk-color-inverse-on-surface`
— brand-**independent** by design, **not** `--zk-color-on-primary`, which a light-primary rebrand
would flip dark; see the contract), because the slides underneath are arbitrary imagery and the
overlay chrome cannot key off surface/on-surface tokens. There are two exceptions: the active
indicator pill (`-indicator-active-bg` / `-indicator-active-ring`, primary / on-primary, below) and
`--zk-carousel-arrow-bg-hover`, the single non-token-rooted literal default (`rgba(0, 0, 0, 0.65)`
— a hand-picked darkening of the scrim with no token to derive from), matching the
`--zk-grid-row-hover-bg` / `--zk-combobox-item-hover-bg` literal-hover precedent. The indicator
has four knobs, one per paint: resting fill and ring (`-indicator-bg`, `-indicator-ring`) and
the active pill's fill and ring (`-indicator-active-bg`, `-indicator-active-ring`). The active
pill is primary with an on-primary ring — the one overlay part that uses brand tokens — so it
reads on light slides; inactive dots are the scrim/inverse-on-surface dots as before.
Hover/focus/disabled opacities and the chevron/dot geometry stay on base tokens or literal geometry, not knob-driven
(same convention as button/input/rating) — the one exception is the indicator's hover cue
(`transform: scale(1.25)`, c30), which is geometry rather than a color/opacity token specifically
because it must work against either fill knob. Arrow size is density-bound via the new
`--zk-carousel-arrow-size` alias in `tokens/_sizing.css` (`var(--zk-control-height-sm)`, 32px);
the indicator's 24px hit area is a WCAG 2.1 SC 2.5.8 accessibility floor, deliberately **not**
ladder-bound and exempt from density scaling in both directions. Carousel renders in place with
no client-side reparenting, so region scoping (CTV-3) works normally — unlike
popup/messagebox/confirmpopup.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-carousel-radius` | `var(--zk-shape-card)` | root frame corners + caption chip corners |
| `--zk-carousel-arrow-bg` | `var(--zk-color-scrim)` | arrow resting background |
| `--zk-carousel-arrow-bg-hover` | `rgba(0, 0, 0, 0.65)` | arrow hover background (literal — no darker-scrim token) |
| `--zk-carousel-arrow-fg` | `var(--zk-color-inverse-on-surface)` | arrow chevron color |
| `--zk-carousel-indicator-bg` | `var(--zk-color-scrim)` | resting dot fill |
| `--zk-carousel-indicator-ring` | `var(--zk-color-inverse-on-surface)` | resting dot ring |
| `--zk-carousel-indicator-active-bg` | `var(--zk-color-primary)` | active pill fill |
| `--zk-carousel-indicator-active-ring` | `var(--zk-color-on-primary)` | active pill ring |
| `--zk-carousel-label-bg` | `var(--zk-color-scrim)` | caption chip backdrop |
| `--zk-carousel-label-fg` | `var(--zk-color-inverse-on-surface)` | caption text |

### Codeeditor — shipped

CodeMirror 6 source editor (CE, `@since` ZK 11.0.0, `zul/code/css/codeeditor.css`) —
`.z-codeeditor` root + `.z-codeeditor-cave` mount point + CodeMirror's own `.cm-editor` /
`.cm-scroller` / `.cm-gutters` / `.cm-content` subtree. Fifteen knobs, and they come in **two
families that must not be homogenized**.

Nine are Marble's own and follow the ordinary field convention (`-bg`, `-fg`, `-radius`,
`-border-color[-hover|-focus]`, plus `-gutter-bg`/`-gutter-fg`/`-gutter-border-color`): the root
is the outlined text field's chrome, so a codeeditor sits beside a textbox without looking
imported, and the focus ring is Mechanism A's **overlay** variant — the root has no padding to
compensate a 1px→2px border with, so growing the border would shove the whole editor viewport, and
the ring has to sit on an `::after` overlay rather than an inset box-shadow on the root because
CodeMirror's DOM is flush against the border and paints opaque (see `codeeditor.css`).

Three keep names **ZK core authored** — `--zk-codeeditor-background`, `--zk-codeeditor-color`,
`--zk-codeeditor-gutter-color`. Those are a documented public API (the componentreference page
tells app authors to set them per element, e.g. `style="--zk-codeeditor-background:#0b1021"`), so
the spelling is fixed and deliberately does not become `-bg`/`-fg`. ZK core also documents
`--zk-codeeditor-active-line` and ten `--zk-codeeditor-token-*` syntax colors; **neither is
declared here**. The CE extension set never enables `highlightActiveLine`, so `.cm-activeLine` is
never emitted and an `-active-line` default would be an orphan token; the ten token colors live in
the widget JS with VS Code Dark+ literals as `var()` fallbacks, so a default here would duplicate
a ZK core value with no CSS consumer (the orphan-token rule counts only component CSS as a
consumer).

The dark surface default is a **literal near-black, not `--zk-color-inverse-surface`** — the one
knob group in this file that deliberately refuses a theme token. The ten syntax colors are not
theme-derived, so the surface has to keep them legible; measured 2026-09-08, all twelve
foregrounds clear WCAG AA on `#1e1e1e` (worst 4.52:1) while five fail on `#2d3748`
(keyword/tag 4.07, comment 3.60, meta 3.39, gutter 3.25). Same "contrast cannot key off theme
tokens here" carve-out as the carousel overlay chrome. Disabled opacity stays on
`--zk-state-disabled-opacity`, not a knob (button/input convention). Codeeditor renders in place
with no client-side reparenting, so region scoping (CTV-3) works normally.

| Knob | Default | Scope |
|------|---------|-------|
| `--zk-codeeditor-bg` | `var(--zk-color-surface)` | light surface fill |
| `--zk-codeeditor-fg` | `var(--zk-color-on-surface)` | light text + caret |
| `--zk-codeeditor-radius` | `var(--zk-shape-input)` | root frame corners |
| `--zk-codeeditor-border-color` | `var(--zk-color-outline)` | resting frame |
| `--zk-codeeditor-border-color-hover` | `var(--zk-color-on-surface)` | hover frame |
| `--zk-codeeditor-border-color-focus` | `var(--zk-color-primary)` | focus frame + inset ring |
| `--zk-codeeditor-gutter-bg` | `var(--zk-color-surface-container-low)` | light gutter fill |
| `--zk-codeeditor-gutter-fg` | `var(--zk-color-on-surface-variant)` | light line numbers |
| `--zk-codeeditor-gutter-border-color` | `var(--zk-color-outline-variant)` | light gutter divider |
| `--zk-codeeditor-background` | `#1e1e1e` | dark surface fill (root + editor + gutter) — ZK-core name |
| `--zk-codeeditor-color` | `#d4d4d4` | dark text + caret — ZK-core name |
| `--zk-codeeditor-gutter-color` | `#858585` | dark line numbers — ZK-core name |
| `--zk-codeeditor-dark-border-color` | `rgba(255, 255, 255, 0.23)` | dark-surface resting frame |
| `--zk-codeeditor-dark-border-color-hover` | `rgba(255, 255, 255, 0.6)` | dark-surface hover frame |
| `--zk-codeeditor-dark-border-color-focus` | `oklch(from var(--zk-color-primary) 0.8 c h)` ≈ `#80bdff` | dark-surface focus ring — MD3 puts primary at **tone 80** on a dark scheme; the light knob measured only 3.45:1 on the `#1e1e1e` fill and was reported unreadable in design review (2026-09-08). Derived by absolute OKLCH lightness off the seed so a brand override stays hue-consistent; 8.45:1 |

The dark surface needs its **own** border pair rather than reusing
`--zk-codeeditor-border-color[-hover]`: those are black-alpha, and composited over the opaque
`#1e1e1e` fill they collapse onto the fill itself — measured rest `rgb(23,23,23)`, hover
`rgb(4,4,4)`, a **1.14:1** change, i.e. dark mode shipped with no visible hover affordance at all
(caught by MD3 Gate 2, 2026-09-08). The component's *outline* was never at risk — the fill alone
is 16.67:1 against the page — it was the state *change* that was invisible, which is precisely the
kind of defect a "do the two states differ?" assertion cannot catch, because they did differ,
numerically. The light-alpha pair mirrors the light surface and clears the 3:1 UI-state bar
(`rgb(82,82,82)` → `rgb(165,165,165)`, **3.17:1**) without becoming a white halo around a black
box. Focus needs no dark variant — primary blue already clears 3.45:1 on the near-black fill — but
the dark rules must **re-assert** it after the dark hover rule, since the shared
`:focus-within` rule sits earlier in the file at equal specificity. **Generalizable:** any
component with a per-widget dark-surface modifier inherits this trap.

## Recipe — adding a component

Validated by the button pilot; repeat per component (then update the
[progress tracker](../../tasks/component-theme-variables-progress.md)):

1. **Audit** the component CSS: which properties are worth exposing, and how are states
   implemented (overlay vs border-color vs background swap)? The knob names follow that model.
2. **Declare** the `--zk-<comp>-*` knobs at `:root` in `tokens/_component-theme.css`, each
   defaulting to the property's exact current value.
3. **Refactor** the component CSS to consume `var(--zk-<comp>-knob)` in place of the direct
   token/literal — surgically, only the exposed properties, no behavior change.
4. **Build** (`npm run build:css`) and confirm the knob appears in `norm.css.dsp` and is
   consumed in the component's `.css.dsp`; `npm run check:css-dsp` stays green.
5. **Test**: add the component to `component-theme-variables.spec.ts`, on the component's own preview page,
   and assert the knob contract (stock values unchanged, then a `:root` override wins). If the page
   does not render a state a knob needs (selected row, open popup), create it in the test with a real
   click or the client widget API.

## Status

- **Shipped**: button, input (textbox family), window, grid, listbox, tree, panel, groupbox,
  combobox, datebox, timebox, spinner (+ doublespinner), bandbox, daterangebox, timepicker, tab (tabbox), menu,
  avatar/avatar-group, chip (base hoisted; see caveat), badge (base hoisted; see caveat), rating,
  progressmeter, paging, combobutton, selectbox, inputgroup, calendar, toolbar, toolbarbutton,
  slider, rangeslider, multislider, checkbox
  (default mold only — see entry for the switch/toggle-mold exclusion), radio (see entry above —
  shares checkbox's CSS file but is its own widget/knob family), messagebox (see entry for the
  CTV-3 structural exception), notification (untyped/default card
  only — see entry for the info/warning/error type-variant exclusion), toast (info-default
  variant only — see entry for the warning/error type-variant exclusion), a (anchor), chosenbox,
  cascader, searchbox, drawer (see entry for the CTV-3 structural exception — whole-root
  reparenting on open), nav, anchornav, stepbar, coachmark (see entry for the CTV-3 structural
  exception — whole-root reparenting on open), colorbox (see entry for the no-focus-knob and
  no-fg-knob exclusions), biglistbox, fisheye/fisheyebar (see entry for the no-bg/-border-knob
  exclusion), pdfviewer, tbeditor (see entry for the no-border-color-hover-knob exclusion),
  signature (see entry for the no-hover-knob and no-fg-knob exclusions), cropper (see entry for
  the no-bg-knob and no-border-color-hover-knob exclusions), dropupload (see entry for the
  no-hover/-focus/-disabled-knob and no-fg-knob exclusions), organigram (see entry for the
  shared-border-color-across-card-and-connector-lines convention and the not-knob-driven disabled
  background and focus-ring exclusions), goldenlayout (see entry for the shared
  border-color/radius across the header strip and goldenpanel, and the drag-proxy/drop-target/
  overflow-dropdown exclusions), portallayout (see entry for the frame-only-surface convention and
  the drag-ghost/drop-placeholder exclusions), confirmpopup (see entry for the CTV-3 structural
  exception — inherits Popup's own makeVParent() reparenting on open), breadcrumb (borderless
  inline text chrome — no bg/border/radius knob; renders in place, so region scoping works),
  carousel (full-bleed media frame — scrim/inverse-on-surface overlay voices, arrow size
  density-bound, indicator 24px WCAG floor; renders in place, so region scoping works),
  codeeditor (see entry for the two-family naming split — nine Marble knobs plus three
  ZK-core-authored dark-surface names — the literal near-black dark default, and the
  deliberately-undeclared `-active-line` and ten `-token-*` hooks).
- **Not exposed** (by design): purely structural/layout components (box, div, cell, separator,
  layouts) and content atoms (label, image) have no meaningful appearance knob — this excludes
  the anchor/link, which **is** exposed despite its similarly minimal text-only vocabulary (see
  the A entry above); checkbox's switch/toggle molds are a candidate for a
  future pass if adopter demand appears — add via the Recipe above.
