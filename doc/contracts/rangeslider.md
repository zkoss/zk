# Component: rangeslider
tier: T2
category: input
shared-css-file: ../zkcml/zkex/src/main/resources/web/js/zkex/slider/css/rangeslider.css
siblings: []
preview: ${PREVIEW_URL}/rangeslider.zul

## References
- MUI CSS: Slider.css
- `doc/spec/DESIGN.md` sections: "Brand & Status Colors", "State-Layer Overlays", "Surface Palette", "Border Rules"

## DOM key selectors
```
.z-rangeslider          ← root
.z-rangeslider-track        ← track rail (`--zk-rangeslider-inner-size` thick, sized by .z-rangeslider-inner)
.z-sliderbuttons-area       ← filled range between start/end thumbs
.z-sliderbuttons-button     ← start/end thumb
.z-rangeslider-mark-label   ← scale mark label
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-rangeslider-track` | height | `var(--zk-rangeslider-inner-size)` (horizontal track) |
| c2 | `.z-rangeslider-track` | background-color | `var(--zk-rangeslider-track-bg)` |
| c3 | `.z-sliderbuttons-area` | background-color | `var(--zk-rangeslider-accent)` |
| c4 | `.z-sliderbuttons-button` | width / height | `var(--zk-rangeslider-button-size)` |
| c5 | `.z-sliderbuttons-button` | background-color | `var(--zk-rangeslider-accent)` |
| c6 | `.z-sliderbuttons-button` | border-radius | 50% |

## Geometry assertions

| id | orientation | check | expected |
|----|-------------|-------|----------|
| g1 | both | perpendicular gap from track near edge → `.z-rangeslider-mark-label` near edge | **18px ±1** in BOTH orientations (horizontal: `label.top − track.bottom`; vertical: `label.left − track.right`). Driven by `--zk-rangeslider-label-gap` (single source of truth shared with multislider); each orientation adds a `+4px` mark-anchor correction via `calc()`. Keeps the slider family's label spacing consistent. |

## States to evaluate
- [ ] horizontal default, horizontal with scale marks, vertical
