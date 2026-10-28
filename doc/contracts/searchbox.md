# Component: searchbox (theme design)
tier: T3
category: input
preview: ${PREVIEW_URL}/searchbox.zul
rules: see .claude/skills/zk-component-rules/components/searchbox.md
contract-approved: false
zk-version: 10.2.1-jakarta

## Notes
searchbox has no dedicated CSS.dsp in ZK source — may share cascader.css or need its own file.
Evaluator should check if a dedicated `.z-searchbox` CSS file is served; if not, flag as T2 (needs own file).

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-searchbox` | border | 1px solid rgba(0, 0, 0, 0.23) |
| c2 | `.z-searchbox` | border-radius | 4px |
| c3 | `.z-searchbox-input` | font-size | 14px |
| c4 | `.z-searchbox[disabled]` | opacity | 0.38 |

### Selection (list-row family)
The searchbox dropdown is a keyboard-navigable list of options — semantically
a LIST-ROW selection, NOT a chip. Per `reference/selected-state-families.md`
it MUST use `secondary-container` (MD3 `list-item.selected`; revised 2026-10-06 from
`primary-container`, which followed MUI). Do not use `primary-container` here.

| id | selector | property | expected |
|----|----------|----------|----------|
| s1 | `.z-searchbox-selected` | background-color | `rgb(201, 213, 234)` (≈ `--zk-color-secondary-container`) |
| s2 | `.z-searchbox-selected` | color | `rgb(21, 30, 45)` (≈ `--zk-color-on-secondary-container`) |
| s3 | `.z-searchbox-selected` | background-color | MUST NOT be `rgb(178, 223, 219)` (= `--zk-color-secondary-container`) — wrong family (the pre-fix bug) |

### Popup open behaviour (detached-popup display scoping)
The mold renders `.z-searchbox-popup` with NO inline display, so the theme owns
the hidden state. It MUST be scoped to the attached popup
(`.z-searchbox .z-searchbox-popup { display:none }`), never a bare
`.z-searchbox-popup { display:none }` paired with a `.z-searchbox-open`-scoped
show rule — the latter leaves the detached popup `display:none` at measure time,
so it reveals bottom-up + mis-positions and may not close on outside-click. See
`reference/floating-popup-in-body.md` (§ "Hiding a detached popup").

Evaluate with the popup OPEN (after a real click — the popup is then a child of
`<body>`):

| id | selector / measurement | expected |
|----|------------------------|----------|
| p1 | open popup: `getComputedStyle('.z-searchbox-popup').display` | NOT `none` (should be `flex`) |
| p2 | open popup `.getBoundingClientRect().top` vs trigger `.bottom` | popup `top >= trigger bottom` when there is room below (drops downward, not upward) |
| p3 | base CSS rule `.z-searchbox-popup` | `display` MUST NOT be `none`; the hide MUST be the descendant rule `.z-searchbox .z-searchbox-popup` |
| p4 | open popup, then real-click outside (and again after typing in the search field) | popup closes (`.z-searchbox-open` removed) |

### Label / placeholder visibility (has-selection state)
ZK toggles the trigger's label/placeholder via **inline** `display` ONLY in the
*no-selection* state (label→`none`, placeholder→`inline-block`). When a selection
exists, ZK **clears both inline styles**, so the rendered display falls back to
the theme's *base* CSS rules. The theme base MUST therefore mirror stock ZK less:
label `display:inline-block` (visible) and placeholder `display:none` (hidden).
If the placeholder base has no `display`, it defaults to `block`; since both
label and placeholder carry `flex:1`, they split the trigger 50/50 and the
selected label is truncated even when it would otherwise fit.

Evaluate on a searchbox WITH a pre-existing selection (e.g. the multiple-select
"Apple, Banana" trigger on `searchbox.zul`):

| id | selector / measurement | expected |
|----|------------------------|----------|
| lp1 | base rule `.z-searchbox-placeholder` | `display: none` (NOT `block`/unset — else it steals flex width once ZK clears the inline style) |
| lp2 | selection present: `getComputedStyle('.z-searchbox-placeholder').display` | `none` (inline style cleared by ZK → base rule hides it) |
| lp3 | selection present: `.z-searchbox-label` `scrollWidth` vs `clientWidth` | label fills the trigger inner width; `scrollWidth <= clientWidth` when the joined labels fit — no premature `…` ellipsis |

## States to evaluate
- [ ] default, hover, disabled, item-selected (keyboard-active row)
- [ ] open (popup drops downward, reveals top-down, closes on outside-click)
- [ ] selection present (placeholder hidden, label fills trigger, no premature truncation)
