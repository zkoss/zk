# inputgroup

Groups an input with a text label/addon (like Bootstrap's input-group pattern).

## DOM structure

```
.z-inputgroup                       (root — display: inline-flex)
├─ .z-inputgroup-text               (label/addon — display: flex; align-items: center)
└─ (input component children — textbox, combobox, etc.)
```

## Vertical mode

`orient="vertical"` adds `.z-inputgroup-vertical` to the root — children stack instead of laying out inline.

## Border-collapse is axis-aware (theme rule for any bordered inputgroup)

When a theme gives the children borders, adjacent children produce a doubled
2px seam. Which border to drop depends on the layout axis — **and the wrong
axis leaves a child visibly missing one side border**:

| Layout | Children touch on… | Drop on the successor… |
|--------|--------------------|------------------------|
| horizontal (default) | their vertical edges | `border-left` |
| vertical (`orient="vertical"`) | their horizontal edges | `border-top` |

A horizontal-only collapse rule **must** be scoped with
`.z-inputgroup:not(.z-inputgroup-vertical)`. An unscoped `border-left: none`
collapse leaks into vertical mode and strips the textbox's *left* border
(the seam there is top/bottom, not left/right) — the classic symptom is "the
vertical textbox has no left border." See `doc/contracts/inputgroup.md`
vertical-border row.

## CSS file

`inputgroup.css.dsp`

## Note

Stub entry — full documentation pending spec-author run (Phase 3/4).
See `doc/spec-author-pipeline-plan.md (retired with the template)`.
