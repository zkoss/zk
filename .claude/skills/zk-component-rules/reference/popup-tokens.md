# Popup surface tokens — all floating popups share one bg

ZK ships several floating-popup widgets (combobox, bandbox, datebox, timebox, selectbox, searchbox, chosenbox, cascader, colorbox, menupopup, notification). When their CSS is written file-by-file, popups easily drift apart and you end up with two or three different `background-color` tokens across the theme — the user sees the surface tones jumping when they move from one popup to the next.

## Rule (zk-material)

All floating popups use the same surface token:

```css
.z-{component}-popup,                /* combobox, bandbox, datebox, chosenbox, colorbox, searchbox, … */
.z-selectbox::picker(select) {       /* selectbox uses the Customizable Select pseudo-element */
    background-color: var(--zk-color-surface);
}
```

This is **not** the MD3 spec default — MD3 Menu Container is `surface-container`. zk-material picks `surface` deliberately so the popup blends with the trigger/input surface beneath it (same color as `.z-combobox` / `.z-bandbox` / `.z-datebox` wrappers).

If you ever change the rule, change **all** popup components at once:

| Component  | Selector                              | File                                     |
|------------|----------------------------------------|------------------------------------------|
| combobox   | `.z-combobox-popup`                    | `zul/inp/css/combobox.css`               |
| bandbox    | `.z-bandbox-popup`                     | `zul/inp/css/bandbox.css`                |
| datebox    | `.z-datebox-popup`                     | `zul/inp/css/datebox.css`                |
| selectbox  | `.z-selectbox::picker(select)`         | `zul/wgt/css/selectbox.css`              |
| searchbox  | `.z-searchbox-popup`                   | `zkmax/inp/css/searchbox.css`            |
| chosenbox  | `.z-chosenbox-popup`                   | `zkmax/inp/css/chosenbox.css`            |
| colorbox   | `.z-colorbox-popup`                    | `zkex/inp/css/colorbox.css`              |

## Why this trap is easy to fall into

Each component's CSS file is independent — there is no shared mixin or token alias for "popup surface". When a Generator/Evaluator only inspects one component at a time, a single off-token slipping through (e.g. `surface-container` left over from an earlier MD3-spec-strict pass) is invisible to the audit.

The bundle check for any popup component must read its `background-color` and compare it against the canonical token in this doc — not just "is it a valid surface token".
