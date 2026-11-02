# Component: captcha
tier: T3
category: utility
shared-css-file: zul/src/main/resources/web/js/zul/wgt/css/captcha.css
siblings: []
preview: ${PREVIEW_URL}/captcha.zul

## T3 metadata

library: server-rendered image (zul CaptchaEngine renders a PNG/JPEG on the server; client receives an `<img>`)
library-version: ZK 10.x core
theme-bridge:
  available: false           # the image content is pixels — no CSS hooks
  variables: {}
wrapper-selectors:
  - .z-captcha               # root `<img>` element
forbidden-selectors:
  - (empty list) — `.z-captcha` is the only styling surface; the image content is pixels drawn by the server, not DOM.
escalation-path: ESCALATED_LIBRARY_CONFIG  # changing the rendered image (font, colour, distortion) requires CaptchaEngine.java changes, not CSS

## References
- ZK source: zul/src/main/java/org/zkoss/zul/Captcha.java
- ZK widget: zul/src/main/resources/web/js/zul/wgt/Captcha.ts
- doc/spec/DESIGN.md sections: "Corner Radii", "Border Rules"

## DOM key selectors (wrapper-only)
```
img.z-captcha               ← the rendered captcha image
```
The captcha may be surrounded by an inputgroup (label + textbox for the answer + captcha image + refresh button) but each piece is a separate ZK widget. This contract only covers the `<img class="z-captcha">`.

## Expected values

| id | selector | property | expected | source |
|----|----------|----------|----------|--------|
| c1 | `img.z-captcha` | border | none (0px): the frame is the `frame` attribute, drawn inside the image by the CaptchaEngine | owner ruling 2026-10-16 |
| c2 | `img.z-captcha` | border-radius | 0px (a radius clips the corners of the engine-drawn frame) | owner ruling 2026-10-16 |
| c3 | `img.z-captcha` | display | block | `captcha.css` (`.z-captcha { display: block; }`) |

## States to evaluate
- [ ] default rendered (image visible, no theme border; `frame="true"` shows exactly one frame line)
- [ ] (no hover / focus / disabled states meaningful on an image)

## Notes for Generator
- Image refresh affordance (if implemented) is typically a separate `<button>` next to the image, not part of this widget. Style that as a regular icon-button via toolbarbutton rules.
- The actual captcha glyph styling (font, colour distortion) is server-side. Any "make the captcha letters match the design" feedback escalates to `ESCALATED_LIBRARY_CONFIG`.
