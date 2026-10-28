# Component: rangeslider
tier: T2
category: input
shared-css-file: ../zkcml/zkex/src/main/resources/web/js/zkex/slider/css/rangeslider.css
siblings: []
preview: ${PREVIEW_URL}/rangeslider.zul

## References
- MUI CSS: Slider.css
- DESIGN.md sections: §2, §3, §9, §11

## DOM key selectors
```
.z-rangeslider          ← root
.z-rangeslider-center   ← track
.z-rangeslider-area     ← filled range between start/end thumbs
.z-rangeslider-button   ← start/end thumb
.z-rangeslider-scale    ← scale marks (markScale variant)
```

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-rangeslider-center` | height | ~4px (horizontal track) |
| c2 | `.z-rangeslider-center` | background-color | rgba(0, 0, 0, 0.12) |
| c3 | `.z-rangeslider-area` | background-color | rgb(55, 111, 208) |
| c4 | `.z-rangeslider-button` | width / height | 16–20px |
| c5 | `.z-rangeslider-button` | background-color | rgb(55, 111, 208) |
| c6 | `.z-rangeslider-button` | border-radius | 50% |

## Geometry assertions

| id | orientation | check | expected |
|----|-------------|-------|----------|
| g1 | both | perpendicular gap from track near edge → `.z-rangeslider-mark-label` near edge | **18px ±1** in BOTH orientations (horizontal: `label.top − track.bottom`; vertical: `label.left − track.right`). Driven by `--zk-rangeslider-label-gap` (single source of truth shared with multislider); each orientation adds a `+4px` mark-anchor correction via `calc()`. Keeps the slider family's label spacing consistent. |

> Selector note: the rows above (`.z-rangeslider-center/-area/-button`) are stale — live DOM uses `.z-rangeslider-track` (rail), `.z-sliderbuttons-area` (fill), `.z-sliderbuttons-button` (thumbs), same as multislider. Correct when next revising this contract (logged in `doc/skill-gaps.md`).

## States to evaluate
- [ ] horizontal default, horizontal with scale marks, vertical
