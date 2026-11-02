# inputgroup

Groups an input with a text label/addon (like Bootstrap's input-group pattern).

## DOM structure

```
.z-inputgroup                       (root — display: inline-flex)
├─ .z-inputgroup-text               (wrapper ZK emits around each child that is not a button, toolbarbutton or input component; id suffix -chdex)
└─ (button / toolbarbutton / input component children — rendered directly, without the wrapper)
```

`Inputgroup.ts` `encloseChildHTML_` writes the `.z-inputgroup-text` wrapper.

## Vertical mode

`orient="vertical"` adds `.z-inputgroup-vertical` to the root — children stack instead of laying out inline.

## CSS file

`inputgroup.css.dsp`
