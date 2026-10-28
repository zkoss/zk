# Component: textbox
tier: T1
category: input
shared-css-file: zul/src/main/resources/web/js/zul/inp/css/input.css
siblings: [intbox, decimalbox, doublebox, longbox, textarea, passwordbox]
preview: ${PREVIEW_URL}/textbox.zul

## References
- MUI CSS: /Users/hawk/Documents/workspace/THEME/material-ui-7.3.1/static-css-output/Inputs/OutlinedInput.css
- DESIGN.md sections: §1, §2, §5, §7, §9, §10, §11

## Preview anchors (use these — not bare `input.z-textbox`)

The textbox preview page renders 20+ instances in different states, laid out as state matrices from `pv/matrix.zul` (see `pv/textbox-content.zul`): a **States** matrix (columns Default / Disabled / Readonly / Invalid / Inplace; rows Text / Password / Placeholder) and a **Multiline** matrix (columns Default / Disabled / Readonly; one unlabeled row). Anchor by matrix title + row label + column header — robust to reordering:

```js
function matrixCell(title, rowLabel, colHeader) {
  const m = [...document.querySelectorAll('.z-d-grid.z-grid-cols-auto')]
    .find(x => x.querySelector('.z-grid-col-full')?.textContent.trim() === title);
  const rows = [...m.querySelectorAll(':scope > .z-d-contents')];
  const col = [...rows[0].children].findIndex(c => c.textContent.trim() === colHeader);
  const row = rows.slice(1).find(r => r.children[0].textContent.trim() === rowLabel);
  return row.children[col];
}
```

| State | Anchor |
|-------|--------|
| default | `matrixCell('States', 'Text', 'Default').querySelector('input.z-textbox')` |
| disabled | `matrixCell('States', 'Text', 'Disabled').querySelector('input.z-textbox')` |
| readonly | `matrixCell('States', 'Text', 'Readonly').querySelector('input.z-textbox')` |
| invalid | `matrixCell('States', 'Text', 'Invalid').querySelector('input.z-textbox')` |
| inplace | `matrixCell('States', 'Text', 'Inplace').querySelector('input.z-textbox')` |
| placeholder | `matrixCell('States', 'Placeholder', 'Default').querySelector('input.z-textbox')` |
| textarea | Multiline matrix, first data row (its label cell is empty — pass `''` as rowLabel), Default column: `matrixCell('Multiline', '', 'Default').querySelector('textarea.z-textbox')` |

Note the layout inversion vs. the old gallery: **Placeholder is a row, not a column**, and the Multiline section now renders three textareas (Default / Disabled / Readonly) — `textarea.z-textbox` first-match is no longer unambiguous.

## DOM key selectors (raw — for reference)

```
input.z-textbox                              ← root (single-line)
input.z-textbox::placeholder                 ← placeholder text
input.z-textbox:hover
input.z-textbox:focus / :focus-visible
input.z-textbox[readonly]
input.z-textbox[disabled]
input.z-textbox.z-textbox-invalid            ← constraint violation
textarea.z-textbox                           ← multi-line variant
```

## Shared-selector warning
`input.css` defines many properties via grouped selectors:
`.z-textbox, .z-intbox, .z-decimalbox, .z-doublebox, .z-longbox, .z-passwordbox { ... }`.
**Generator: if a fix changes a grouped selector, decide consciously whether it should apply to all siblings (preferred) or be split into a textbox-only rule.**

## Expected values (DESIGN.md ground truth)

State selectors below reference the **anchor selectors** above. The check applies to that exact anchor element.

| id | state anchor | property | expected | source | token-rooted? |
|----|-------------|----------|----------|--------|--------------|
| c1 | default | min-height | 39–40px | DESIGN.md §10 | no |
| c2 | default | font-size | 13px | DESIGN.md §7 | **yes** (`--zk-typescale-body-medium-size`) |
| c3 | default | font-family | starts-with "Inter" | DESIGN.md §7 | yes (`--zk-typescale-font-family`) |
| c4 | default | background-color | rgb(255, 255, 255) | DESIGN.md §1 | no |
| c5 | default | color | rgba(0, 0, 0, 0.87) | DESIGN.md §2 | no |
| c6 | default | border | 1px solid rgba(0, 0, 0, 0.23) | DESIGN.md §11 | no |
| c7 | default | border-radius | 4px | DESIGN.md §5 | yes (`--zk-shape-input`) |
| c8 | default | transition-duration | 250ms | DESIGN.md §9 | **yes** (`--zk-motion-duration-short3`) |
| c9 | default | ::placeholder color | rgba(0, 0, 0, 0.6) | DESIGN.md §2 | no (**hard to measure — see harness gap**) |
| c10 | default + force :hover via CDP | border-color | rgba(0, 0, 0, 0.87) | DESIGN.md §11 | no |
| c11 | default + Tab focus | border-color | rgb(55, 111, 208) | DESIGN.md §11 | no |
| c12 | default + Tab focus | border-width | 2px | DESIGN.md §11 | no |
| c13 | disabled | opacity | 0.38 | DESIGN.md §8 | no |
| c14 | readonly | background-color | ≠ rgb(255, 255, 255) (tinted) | DESIGN.md §1 | no |
| c15 | invalid | border-color | rgb(211, 47, 47) | DESIGN.md §3 | no |
| c16 | textarea | min-height | ≥ 64px | inferred | no |
| c17 | default + Tab focus | content edge (`border-left + padding-left`) | **== rest (±0px)** — padding compensates the 2px border | reference/focus-affordance-no-layout-shift.md | no |
| c18 | default | transition-property | must **NOT** include `border-width` (would jitter text mid-animation) | reference/focus-affordance-no-layout-shift.md | no |

`token-rooted?` column is hint for the Evaluator to apply D6 logic: read the token's resolved value before deciding whether the failure is token- or component-rooted.

**Focus must not shift text** (gap 2026-07-14, second occurrence). c12's 2px border is absorbed by
mechanism-B padding compensation (c17) AND `border-width` must not be transitioned (c18) — animating
it without co-animating padding drifts the content edge mid-transition. Guarded by
`screenshot.spec.ts › input focus (no layout shift) › textbox family`.

## States to evaluate

- [ ] default
- [ ] hover
- [ ] focus-visible
- [ ] disabled
- [ ] readonly
- [ ] invalid
- [ ] placeholder colour
- [ ] textarea variant
