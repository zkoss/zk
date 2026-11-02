# Popup surface tokens — all floating popups share one bg

ZK ships several floating-popup widgets (combobox, bandbox, datebox, selectbox, searchbox, chosenbox, cascader, colorbox, menupopup, notification). When their CSS is written file-by-file, popups easily drift apart and you end up with two or three different `background-color` tokens across the theme — the user sees the surface tones jumping when they move from one popup to the next.

## Rule

All floating popups take their surface from `--zk-{comp}-popup-bg`, which defaults to `--zk-color-surface` (see `doc/spec/component-theme-variables.md`). This is **not** the MD3 spec default — MD3 Menu Container is `surface-container`. Marble picks `surface` deliberately so the popup blends with the trigger/input surface beneath it (same color as `.z-combobox` / `.z-bandbox` / `.z-datebox` wrappers). Keep the default of every `--zk-{comp}-popup-bg` the same. The selectbox uses the Customizable Select pseudo-element `.z-selectbox::picker(select)` instead of a `.z-{component}-popup` class; it has no knob and writes `var(--zk-color-surface)` literally (`selectbox.css:129`).

Two floating components deliberately do not take `--zk-color-surface`, and the `grep` below also lists their tokens (both token names are matched):

- **Notification** — `--zk-notification-bg` is `--zk-color-surface-container-highest` (`_component-theme.css:614`).
- **Confirmpopup** — `--zk-confirmpopup-bg` is `--zk-color-surface-container-low` (`_component-theme.css:1146`).

Notification takes a container tint because an MD3 alert carries its severity with the leading icon plus a tinted container (the knob comment in `_component-theme.css` above `--zk-notification-bg`). Confirmpopup uses a neutral card like coachmark, so all anchored floating cards read as one family (reason in `doc/contracts/confirmpopup.md`, "Design Contract"). Keep each exception in step with its knob comment when you change it.

If you ever change the rule, change **all** popup components at once. List them with:

```bash
grep -rnE "popup-bg|notification-bg" zul/src/main/resources/web/zul/css/tokens/_component-theme.css
```

## Why this trap is easy to fall into

Each component's CSS file is independent — there is no shared mixin or token alias for "popup surface". When a Generator/Evaluator only inspects one component at a time, a single off-token slipping through (e.g. `surface-container` left over from an earlier MD3-spec-strict pass) is invisible to the audit.

The bundle check for any popup component must read its `background-color` and compare it against the canonical token (`--zk-{comp}-popup-bg`, defaulting to `--zk-color-surface`) — not just "is it a valid surface token".
