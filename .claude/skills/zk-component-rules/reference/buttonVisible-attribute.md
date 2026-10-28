# buttonVisible attribute

The `buttonVisible="false"` attribute hides the auxiliary button (calendar icon, dropdown arrow, spin arrows) of an input component while keeping the input itself functional.

## Which components support it

Only input components that ship with a built-in auxiliary button:

- **combobox** — hides the dropdown arrow
- **bandbox** — hides the band button
- **datebox** — hides the calendar icon
- **timebox** — hides the clock icon
- **spinner / doublespinner** — hides the up/down arrows

**Not supported on**: textbox, intbox, longbox, decimalbox, doublebox, passwordbox, selectbox, searchbox, chosenbox, cascader, colorbox, slider, rating.

## Where ZK puts the resulting class

`buttonVisible="false"` adds `.z-{component}-disabled` to the **button element**, not to the root:

```html
<!-- buttonVisible="false" -->
<span class="z-bandbox">
  <input class="z-bandbox-input" />
  <a class="z-bandbox-button z-bandbox-disabled">...</a>  <!-- class on the button -->
</span>
```

Therefore the selector is:

```css
.z-bandbox-button.z-bandbox-disabled { display: none; }
```

**Not**: `.z-bandbox.z-bandbox-no-button` or `.z-bandbox[buttonVisible]` — those do not exist.

## Combining with other states

`buttonVisible="false"` composes with all other states. The button stays hidden regardless of `disabled`, `readonly`, `invalid`, `inplace`. In preview matrices, the "No button" row + any state column is a valid combination — show a real component, never `—`.

Exception: bandbox + `buttonVisible="false"` + `inplace="true"` is valid and ZK supports it; older preview pages incorrectly showed `—` here. Fix to show a real component.

## Click-to-open behavior is gated by `buttonVisible` (ZK JS, not CSS)

For the popup/dropdown inputs — **combobox** and **bandbox** (`ComboWidget`), and **datebox** (`Datebox`) — ZK decides *where* a click opens the popup in **JavaScript**, not CSS. A theme cannot change this and must not try to (it would require overriding ZK JS). The whole-control click-to-open is gated on `buttonVisible`, which is why hiding the button also removes the readonly click gesture.

**Rule (verified against ZK 10.3 source + live interaction probe, 2026-07-03):**

| State | click the INPUT text area | click the ICON/button |
|-------|---------------------------|-----------------------|
| editable + button visible | does **not** open (only autodrop-on-type) | opens |
| **readonly** + button visible | **opens** (whole control is the trigger) | opens |
| `buttonVisible="false"` (editable *or* readonly) | does **not** open | n/a (no button) |
| disabled | nothing | nothing |

Why readonly opens on *any* click: a readonly select can't be typed into, so ZK makes the whole field a click target for picking from the list. Documented intent — `Combobox.ts:24-27`: "the value of a read-only combobox can be changed by dropping down the list and selecting a combo item (though users cannot type anything in the input box)."

Source (`/Users/hawk/Documents/workspace/ZK10/zk/zul/src/main/resources/web/js/zul/`):
- `inp/ComboWidget.ts:779-793` — `doClick_`: `else if (this._readonly && !this.isOpen() && this._buttonVisible) this.open(...)`. Input-click open requires **both** readonly **and** buttonVisible.
- `inp/ComboWidget.ts:742-756` — `_doBtnClick`: `if (!this._buttonVisible) return`. The button handler no-ops when hidden.
- `db/Datebox.ts:714-719` — `doClick_`: `if (this._readonly && this._buttonVisible && this._pop && !this._pop.isOpen()) this._pop.open()`. Identical gate.
- Disabled is handled first in both (`if (this._disabled) return` / `if (!this._disabled)`); the theme additionally sets `pointer-events:none` on the disabled root.

**Key consequence for `buttonVisible="false"`:** hiding the button removes the *only* icon trigger AND disables the readonly whole-control click (the `&& this._buttonVisible` guard). A no-button combobox/datebox/bandbox therefore has **no click gesture that opens the popup** — this is ZK's own behavior, correct and expected, **not a theme/CSS bug**. So "clicking the input opens the popup" only ever happens for **readonly + button visible**, and it is intentional ZK design.

**Theme note (cursor honesty, minor):** the theme sets `cursor:pointer` on a readonly input to signal "click opens a menu." In the readonly + `buttonVisible="false"` corner case that cursor is slightly misleading (nothing opens). Scope it with `:has(.z-{c}-button:not(.z-{c}-disabled))` if strict honesty is wanted; currently accepted as a rare edge.

## `buttonVisible` is cosmetic — interactivity is governed by `readonly`/`disabled`

`buttonVisible` (`@since 2.4.1`, one of ZK's oldest input attributes) was **never designed to prevent operation**. The Java javadoc and the ZK Component Reference describe it purely as "hide the button / present a plain text-input style" — cosmetic only. Hiding the button removes the *mouse click-open* affordance (which is button-bound, gated on `buttonVisible` per the table above) but the control stays operable by other means:

- **`Alt+↓` (Alt+ArrowDown) and programmatic `open()` keep working** with `buttonVisible="false"` — documented intended behavior, not a defect.
- Interactivity is owned by `readonly` / `disabled`, **never** by `buttonVisible`.

**Authoring smell:** `readonly` + `buttonVisible="false"` is the one combination that leaves a **mouse-only** user no way to open the popup — can't type (readonly), no button to click, only the non-discoverable `Alt+↓` remains. For a genuinely non-changeable display, use `disabled` or a plain label / `textbox` instead of `readonly` + `buttonVisible="false"`.

Applies identically to combobox, datebox, and bandbox. See `doc/spec/component-state-model.md`.

## Buttons inside the input itself

Do not confuse `buttonVisible` (the auxiliary button) with controls like the spinner's `+/-` keyboard increments — those are not separate DOM elements and have no `buttonVisible` toggle. `buttonVisible="false"` only affects the trailing/leading icon button.

## Required CSS rule — per component, per file

Even though combo-family components bundle into a single `combo.css.dsp`, **each component has its own source CSS file** and a rule in `combobox.css` does NOT cascade to `datebox.css` or `timebox.css`. The hide-button rule must be added explicitly to every component's file:

| Component | File | Required rule |
|-----------|------|---------------|
| combobox | `js/zul/inp/css/combobox.css` | `.z-combobox-button.z-combobox-disabled { display: none; }` |
| bandbox | `js/zul/inp/css/bandbox.css` | `.z-bandbox-button.z-bandbox-disabled { display: none; }` |
| datebox | `js/zul/inp/css/datebox.css` | `.z-datebox-button.z-datebox-disabled { display: none; }` |
| timebox | `js/zul/inp/css/timebox.css` | `.z-timebox-button.z-timebox-disabled { display: none; }` |
| spinner | `js/zul/inp/css/spinner.css` | `.z-spinner-button.z-spinner-disabled { display: none; }` |
| doublespinner | `js/zul/inp/css/spinner.css` (shared with spinner) | `.z-doublespinner-button.z-doublespinner-disabled { display: none; }` |

This rule cannot be derived from MD3 / MUI / Mira reference — those design systems have no `buttonVisible` concept. It must be added based on this skill alone.

## Verification (live DOM, confirmed 2026-05-14)

For all six components above, ZK at runtime DOES add `.z-{c}-disabled` to the button element when `buttonVisible="false"` is set (verified via `getComputedStyle` on each preview page). The class is emitted by:

- mold render path: `redraw_` in `ComboWidget.ts` and `mold/spinner.js` push `$s('disabled')` into the button's class list when `!isButtonVisible`.
- runtime toggle: `RoundUtl.buttonVisible(wgt, v)` in `InputWidget.ts` adds/removes the class on `wgt.$n('btn')` when `setButtonVisible` is called after init.

So the rule above is enough — no JavaScript-side fix needed. If the rule is missing, the button just stays visible.

## Border architecture: split-border vs root-border

Themes choose one of two ways to render the visible border around a composite input + button widget. The choice changes what `buttonVisible="false"` does to the visual border.

### Pattern S — split-border (border lives on input + button)

```
.z-{c}                   ← root, no border
.z-{c}-input             ← border: 1px solid …; border-right: none;
                           border-radius: R 0 0 R
.z-{c}-button            ← border: 1px solid …; border-left: none (or shared)
                           border-radius: 0 R R 0
```

The visible rectangle is the union of two half-bordered elements. When `buttonVisible="false"` hides the button, the right edge is no longer drawn — **theme must restore it on the input**.

ZK provides the hook: the input gets the extra class `.z-{c}-input-full` when `buttonVisible="false"` (emitted by `redraw_` and toggled at runtime by `RoundUtl.buttonVisible`). The theme rule is:

```css
.z-{c}-input.z-{c}-input-full {
    border-right: 1px solid var(--zk-color-outline);    /* or whichever base colour */
    border-radius: var(--zk-shape-input);                /* uniform on all 4 corners */
}
```

If this rule is missing, the input renders with three borders and a square right side — the bug the harness must catch.

### Pattern R — root-border (border lives on the root)

```
.z-{c}                   ← border: 1px solid …; border-radius: R
.z-{c}-input             ← border: none
.z-{c}-button            ← border: none, sits inside the root's painted edge
                           (button may have its own border-radius for the right corners
                           but only as a clip mask, not as the visible component border)
```

The visible border is one continuous rectangle on the root. Hiding the button leaves the root border untouched, so no restoration rule is needed.

### Which components use which pattern (current theme)

| Pattern | Components |
|---------|------------|
| S (split) | combobox |
| R (root) | bandbox, datebox, timebox, spinner, doublespinner |

This split is a *theme implementation choice*, not a ZK fact — a different theme could put all six on Pattern S, or all six on Pattern R. The skill records the two patterns and the hook; the theme records which it uses.

**Implication for `buttonVisible="false"` verification:** for Pattern S components, the No-button check must verify BOTH (1) the button is hidden AND (2) the input still has a 4-side border + uniform corner radius via `.z-{c}-input-full`. For Pattern R components, only check (1).

## Selector traps

`.z-{c}-disabled` is overloaded — ZK uses the *same* class name on **two different elements**:

| Element | When the class appears | Meaning |
|---------|------------------------|---------|
| Root `.z-{c}` | `disabled="true"` on the widget | Component-disabled state |
| Button `.z-{c}-button` | `buttonVisible="false"` | Hide auxiliary button |

Selector pitfalls to avoid:

- ❌ `.z-{c}-disabled { display: none; }` — single-class selector matches **both** the root (disabling the whole input) and the button. Always use the two-class form.
- ❌ `.z-{c}.z-{c}-disabled .z-{c}-button { display: none; }` — only fires when the root is disabled; doesn't hide the button when only `buttonVisible="false"` is set.
- ✅ `.z-{c}-button.z-{c}-disabled { display: none; }` — element + state, fires only on the button element regardless of root state.
