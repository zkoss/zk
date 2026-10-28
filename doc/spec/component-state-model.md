# Marble — Component State Model (readonly vs disabled)

Normative rules for how Marble distinguishes `readonly`, `disabled`, and status/emphasis states — so an operable control never reads as a broken one.

## 1. Readonly ≠ disabled for popup controls

A dropdown/popup control — **combobox, datebox, bandbox** — MUST render `readonly` as an *active outlined field*, never greyed or disabled-looking. In ZK there is no dedicated "select-only" mode; `readonly="true"` is the *normal, active* state for a select-style dropdown, and the popup is still openable (mouse via the button, keyboard via `Alt+↓`). Styling it as de-emphasis misuses the disabled language and misleads the user.

**Applied rule:** the `.z-*-readonly` block collapses to a single `cursor: pointer` rule and otherwise falls through to the active-field styling (white surface, visible `1px solid` outline, full-opacity text, working button). Genuinely `disabled` controls keep opacity **0.38**.

A readonly popup control MUST render identical to its editable counterpart. Never set `pointer-events: none` or reduced opacity on the button of a readonly popup control — that leaves the primary open affordance dead.

## 2. Scope

Applies to **combobox, datebox, bandbox**.

**Excluded: timebox, spinner, doublespinner.** They have no popup to open when readonly, so a de-emphasized readonly look is acceptable — they follow the normal readonly treatment.

## 3. Grey is reserved for disabled

Grey / 38% opacity is the reserved language of `disabled` — it communicates "not interactive." NEVER apply it to a control the user can still operate. Appearance must honestly reflect interactivity.

## 4. Step / status dots

"Todo" / "upcoming" step indicators (e.g. stepbar `.z-step-icon-empty`) MUST use `--zk-color-outline` (grey), not `--zk-color-primary`. This makes done vs. todo read at a glance and matches the un-lit connector; solid-primary is reserved for complete/active steps.

## 5. Delta & status colors are semantic

Delta and status colors MUST be driven by *meaning* (good vs. bad), never by arrow direction. "Low Stock ↓2" is an improvement and reads **green**, even though the arrow points down; "Open Tickets ↑" is a regression and reads **red**.

## 6. Button emphasis footgun

A bare `.z-button` defaults to **filled primary**. Secondary and tertiary actions MUST explicitly add `z-button-outlined` (or `z-button-text`) — otherwise every button reads as a primary CTA and the emphasis hierarchy collapses. A screen should carry one high-emphasis (filled) action; companions such as Cancel/Back sit lower.
