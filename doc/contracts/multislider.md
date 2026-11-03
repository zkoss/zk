# Component: multislider
tier: T2
category: input
shared-css-file: ../zkcml/zkmax/src/main/resources/web/js/zkmax/slider/css/multislider.css
siblings: []
preview: ${PREVIEW_URL}/multislider.zul

## References
- MUI CSS: Slider.css
- `doc/spec/DESIGN.md` sections: "Text Colors", "Brand & Status Colors", "Motion", "Border Rules"

## DOM key selectors
```
.z-multislider               ← root
.z-multislider-track         ← track rail (NOT a -center element; inset:0 of inner)
.z-sliderbuttons-area        ← filled range between two thumbs (JS sets top/height or left/width)
.z-sliderbuttons-button      ← thumb handle (JS sets top:X% / left:X% = value position)
.z-multislider-horizontal / -vertical
```
Note: the thumb and area classes are the shared `.z-sliderbuttons-*` family (same as rangeslider) — see
`components/slider.md` "Related: multislider, rangeslider".

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-multislider-track` | height | `var(--zk-rangeslider-inner-size)` (horizontal track) |
| c2 | `.z-multislider-track` | background-color | `var(--zk-multislider-track-bg)` |
| c3 | `.z-sliderbuttons-area` | background-color | `var(--zk-multislider-accent)` |
| c4 | `.z-sliderbuttons-button` | width / height | 16–20px |
| c5 | `.z-sliderbuttons-button` | background-color | `var(--zk-multislider-accent)` |
| c6 | `.z-sliderbuttons-button` | border-radius | 50% |

## Geometry assertions

| id | orientation | check | expected |
|----|-------------|-------|----------|
| v1 | vertical | thumb **center** y vs its JS `top:X%` offset on the track | center y == track.top + X% × track.height (±1px). The min-value (top:0%) thumb center == track top; the max-value (top:100%) thumb center == track bottom. NOT the thumb's top edge. Requires `margin-top: -½(thumb size)` on `.z-multislider-vertical .z-sliderbuttons-button` (mirrors horizontal `margin-left`). |
| v2 | both | perpendicular gap from track near edge → `.z-multislider-mark-label` near edge | **18px ±1** in BOTH orientations (horizontal: `label.top − track.bottom`; vertical: `label.left − track.right`). Must equal the rangeslider gap (same `--zk-rangeslider-label-gap` token). Catches independently-tuned H/V offsets drifting apart (was 27px H / 15px V). Note: multislider marks render the label ONLY (no dot), so the calc correction differs from rangeslider's. |

## States to evaluate
- [ ] horizontal (3 ranges), vertical (2 ranges)
