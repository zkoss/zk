# Color-Variant Disabled Override Pattern

**Applies to:** any ZK theme that adds sclass-based semantic color variants to a component.

## The Problem

When a theme adds color variants via compound selectors like `.z-button-success.z-button`
(specificity 0,2,0), and the base disabled rule also uses a compound selector like
`.z-button[disabled]` (specificity 0,2,0), both rules have **identical specificity**.

CSS last-rule-wins: if the color variant block appears **after** the disabled block in the
source file, the variant color paints over the disabled appearance on a button that is both
a color variant AND disabled.

Result: `.z-button-success[disabled]` renders as a fully saturated green, visually
indistinguishable from an enabled button.

## Rule

> Every sclass-based color variant rule **must be followed** by a corresponding disabled
> override using a **higher-specificity selector** (add the attribute selector `[disabled]`
> to raise specificity to 0,3,0).

## The Extra Trap: Sub-families That Don't Inherit the Base Disabled Override

A base disabled rule like `.z-button-outlined.z-button[disabled]` does **not** match
`.z-button-outlined-secondary.z-button[disabled]`, because the latter button only carries
`z-button-outlined-secondary` as a class — it does NOT also carry `z-button-outlined`.

This means each sub-family (`outlined-{color}`, `text-{color}`, etc.) needs its own
disabled override block, even if a "base" override already exists for the parent family.

## Patterns by Variant Family

### Contained (background-color changes)
```css
/* ── Contained color variants (spec 0,2,0) ── */
.z-button-success.z-button { background-color: var(--zk-color-success); color: #fff; }

/* ── Disabled overrides — must reset to disabled token (spec 0,3,0) ── */
.z-button-success.z-button[disabled] {
    background-color: var(--zk-color-disabled-container);
    color: var(--zk-color-disabled);
    border-color: transparent;
    box-shadow: none;
}
```

### Outlined (border-color and color change)
```css
/* ── Outlined color variants (spec 0,2,0) ── */
.z-button-outlined-error.z-button { color: var(--zk-color-error); border: 1px solid var(--zk-color-error); }

/* ── Disabled overrides (spec 0,3,0) ── */
.z-button-outlined-error.z-button[disabled] {
    background-color: transparent;
    border-color: var(--zk-color-disabled-container);
    color: var(--zk-color-disabled);
    box-shadow: none;
}
```

### Text (only color changes)
```css
/* ── Text color variants (spec 0,2,0) ── */
.z-button-text-error.z-button { color: var(--zk-color-error); border: none; }

/* ── Disabled overrides (spec 0,3,0) ── */
.z-button-text-error.z-button[disabled] {
    background-color: transparent;
    border: none;
    color: var(--zk-color-disabled);
    box-shadow: none;
}
```

## Specificity Breakdown

| Selector | Spec | Wins over |
|----------|------|-----------|
| `.z-button[disabled]` (base disabled) | 0,2,0 | tied with color variants |
| `.z-button-success.z-button` (color variant) | 0,2,0 | ties with base disabled |
| `.z-button-outlined.z-button[disabled]` (outlined base disabled) | 0,3,0 | beats `.z-button-outlined.z-button` but does NOT match `z-button-outlined-success` |
| `.z-button-success.z-button[disabled]` (this pattern) | 0,3,0 | beats color variant |

## Which Components Need This

Any component CSS that:
1. Defines color variants via two-class compound selectors (`.z-{variant}.z-{component}`)
2. AND defines a base disabled rule without the variant class

Currently implemented in Marble: **button** — contained variants (`secondary`, `success`,
`warning`, `error`, `info`, `light`, `dark`), outlined variants (`outlined-{color}`), and
text variants (`text-{color}`).

When adding color variants to other components, apply the same pattern immediately.

## Checklist

When adding a new sclass color variant to any component:
- [ ] Add the variant rule (`.z-{variant}.z-{component} { ... }`)
- [ ] Immediately add a disabled override (`.z-{variant}.z-{component}[disabled] { ... }`)
- [ ] If the variant is a sub-family (e.g. `outlined-error`), do NOT rely on the base family's disabled rule — add a separate override
- [ ] Contract: add a row asserting disabled state uses disabled colors
- [ ] Playwright: assert disabled button color/bg alpha ≤ 0.5 (semi-transparent disabled token)

## Related

- `components/button.md` — button color variants + disabled selectors
- `reference/state-classes.md` — how ZK emits disabled classes vs attributes
