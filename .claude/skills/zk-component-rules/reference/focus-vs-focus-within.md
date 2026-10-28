# `:focus` vs `:focus-within`

For composite ZK components (root span + inner `<input>` + auxiliary button), the input that receives the keyboard focus is **inside** the root, not the root itself. The root therefore never matches `:focus` — it only matches `:focus-within`.

## Composite components requiring `:focus-within`

- **combobox**: `.z-combobox:focus-within { ... }`
- **bandbox**: `.z-bandbox:focus-within { ... }`
- **datebox**: `.z-datebox:focus-within { ... }`
- **timebox**: `.z-timebox:focus-within { ... }`
- **spinner / doublespinner**: `.z-spinner:focus-within { ... }`

If you write `.z-datebox:focus`, the rule never fires (focus is on the inner input, not the root).

## Direct-input components use `:focus`

- **textbox, intbox, decimalbox, doublebox, longbox, passwordbox**: the root IS the input, so `.z-textbox:focus { ... }` works.
- Same for `<button>` ZK button (focus lands on the rendered `<button>` element).

## Evaluator measurement caveat

Chrome DevTools Protocol cannot reliably trigger `:focus-within` via synthetic events on the root. To verify the rule:

1. Call `.focus()` on the inner input element (find via the `.z-{component}-input` selector).
2. Then read `getComputedStyle()` on the **root** element.
3. Because focus is now inside the root, `:focus-within` matches and the styles apply.

Alternatively, verify the rule exists by reading the stylesheet directly via `document.styleSheets` — sometimes more reliable than triggering the state.

## Anti-pattern

Do NOT write rules like:

```css
.z-datebox-input:focus { border-color: <primary>; }
```

This styles only the inner input element — but the visible border is on the root span (Type A from inplace-state.md), so the focus indicator is not visible. Always raise the focus styles to the root via `:focus-within`.
