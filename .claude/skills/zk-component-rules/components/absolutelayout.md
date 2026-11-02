# absolutelayout

A container where children are positioned with absolute coordinates (x/y attributes on `<absolutechildren>`).

## DOM structure

```
.z-absolutelayout                  (root — position: relative; height: 100%)
└─ .z-absolutechildren * N         (each child — position: absolute; JS sets top/left from the x/y attributes)
```

## Layout engine

ZK writes inline `left` and `top` on each `.z-absolutechildren` from its `x` / `y` attributes (`Absolutechildren.ts`). The root must remain `position: relative` to anchor the children correctly.

## CSS file

`absolutelayout.css.dsp`
