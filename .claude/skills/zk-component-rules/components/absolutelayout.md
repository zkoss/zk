# absolutelayout

A container where children are positioned with absolute coordinates (x/y attributes on `<absolutechildren>`).

## DOM structure

```
.z-absolutelayout                  (root — position: relative; height: 100%)
└─ .z-absolutechildren * N         (each child — position: absolute; JS sets top/left/width/height)
```

## Layout engine

ZK writes inline `style="top: ...; left: ...; width: ...; height: ..."` on each `.z-absolutechildren` at render time. The root must remain `position: relative` to anchor the children correctly.

## CSS file

`absolutelayout.css.dsp`

## Note

Stub entry — full documentation pending spec-author run (Phase 3/4).
See `doc/spec-author-pipeline-plan.md (retired with the template)`.
