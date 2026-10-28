# cropper

An image cropping widget backed by the Jcrop jQuery library (PE component from zkmax — edition per ZKDoc badge, not source location). ZK renders a root `<div class="z-cropper">` containing an `<img>` for the image source and, when `enableToolbar="true"` (default), a floating `<div class="z-cropper-toolbar">` with crop/cancel action items. Jcrop initializes on the `<img>` at `bindChildren_` time and injects its entire crop-selection DOM — holder, overlay, dragbars, handles, guide lines, tracker — internally.

**Jcrop ships NO stylesheet in ZK's integration** — the theme CSS is the *only* source of structural rules for the Jcrop-injected subtree. Upstream codegen (`/Users/hawk/Documents/workspace/ZK10/zkcml/zkmax/codegen/resources/web/js/zkmax/cropper/css/cropper.css.dsp`) ships ~30 structural rules every theme MUST reproduce, or the crop UI is invisible/broken: `.z-cropper-handle` (6×6 blocks + 8 per-ordinal position/transform rules), `.jcrop-dragbar` (6px bars stretched per axis), `.z-cropper-area` (`outline: 1px solid white`, `cursor: move`), `.z-cropper-vline`/`-hline` (1px guide lines), `.z-cropper-tracker` (full-size event overlay), `.z-cropper-holder` (`cursor: crosshair` etc.), `.z-cropper-holder img { max-width: none }`, and `.z-cropper { display: inline-block }` (shrink-wrap). Confirmed 2026-06-05: Marble shipped without these → all 8 handles rendered 0×0 (invisible). The earlier "Jcrop DOM is off-limits for theme CSS" rule was over-broad — the correct boundary is: **structural rules (size/position/transform/cursor) are REQUIRED and must match upstream geometry verbatim; only decorative aspects (handle/outline color, opacity) are theme choices; never set width/height on `.z-cropper-holder` or its `img` — JS owns those.**

The toolbar is absolutely positioned by JS (`_positionToolbar`) relative to the current selection rectangle. It is hidden (`display: none`) at rest and shown only when a crop selection exists. It does not appear in the static DOM inspection without a live selection.

## DOM structure

The mold emits only three elements under `.z-cropper`. Jcrop then appends `.z-cropper-holder` as a direct sibling of the `<img>` at `bindChildren_` time (confirmed by live DOM inspection — see §Notes).

```
div.z-cropper                          (root; inline-block; position: relative)
├─ img#<uuid>-img                      (the source image; visibility: hidden until loaded;
│                                       width/height inline-set by JS after load)
├─ div.z-cropper-holder                (Jcrop-injected at bind time; direct child of root;
│                                       cursor: crosshair; position: relative)
│  ├─ div                              (anonymous selection container injected by Jcrop)
│  │  ├─ div                           (selection overlay with dragbars and handles inside)
│  │  │  ├─ div.jcrop-dragbar.ord-*   (resize bars — n/s/e/w/ne/nw/se/sw)
│  │  │  └─ div.z-cropper-handle.ord-*(corner/edge handles — 8 total)
│  │  └─ div                          (inner selection geometry div)
│  ├─ div.z-cropper-tracker            (full-overlay for mouse event capture outside selection)
│  ├─ input.jcrop-keymgr              (hidden keyboard input for Jcrop key events)
│  └─ img                             (duplicate of the source image, Jcrop-managed)
└─ div#<uuid>-toolbar.z-cropper-toolbar  (action toolbar; position: absolute;
                                          display: none at rest; z-index: 601 inline)
   └─ ul
      ├─ li.z-cropper-crop             (crop confirm action)
      │  └─ a[href="javascript:;"]     ("crop" text)
      └─ li.z-cropper-cancel           (cancel / release selection)
         └─ a[href="javascript:;"]     ("cancel" text)
```

Note: `.z-cropper-area`, `.z-cropper-vline`, `.z-cropper-hline` appear in the compiled theme CSS (from the original theme) but were NOT observed in the live DOM during inspection with a pre-set selection. Jcrop may emit them conditionally depending on version or selection state. The selection-area overlay is governed by Jcrop's internal rendering and should not be relied upon as a stable CSS target.

The toolbar is conditionally rendered: it appears in the initial HTML only when `enableToolbar="true"` (the default). If `enableToolbar="false"`, the toolbar `<div>` is not emitted at all.

**Key confirmed facts (live DOM inspection + eval report 2026-05-13):**
- `.z-cropper-canvas` is NOT present in the live DOM — never emitted by ZK or Jcrop.
- `.z-cropper-holder` is a **direct child of `.z-cropper`** (sibling of `<img>` and toolbar), injected by Jcrop at `bindChildren_` time — not nested inside the `<img>` element.
- `.z-cropper-toolbar` is always a direct child of `.z-cropper`, position absolute.

## State classes

ZK adds no state classes to the root `.z-cropper` element. There is no `disabled`, `readonly`, or selection-state class emitted on the ZK wrapper. All visual states inside the crop area are Jcrop-internal (`.jcrop-*` not theme-owned).

The toolbar element (`.z-cropper-toolbar`) has no state classes. Its visibility is controlled solely by `display: none` / `display: block` via `_showToolbar()` / `_hideToolbar()`.

States rely on Jcrop's own DOM mutation (not on ZK class toggles):
- Selection active → Jcrop makes `.z-cropper-area` visible, ZK calls `_showToolbar()`
- Selection released → Jcrop hides the area, ZK calls `_hideToolbar()`

## Attribute support

- `src="..."` → sets `<img src>` attribute; triggers `rerender()` on the ZK widget
- `enableToolbar="false"` → the `<div class="z-cropper-toolbar">` is NOT emitted in the mold at all
- `toolbarVisible="true|false"` → at runtime (after a selection exists), calls `_showToolbar()` or `_hideToolbar()`; does NOT emit a state class
- `aspectRatio="N"` → passed to Jcrop's `setOptions()`; no CSS effect
- `minWidth` / `minHeight` / `maxWidth` / `maxHeight` → passed to Jcrop; no CSS effect
- `x`, `y`, `w`, `h` → initial selection coordinates; passed to Jcrop; no CSS effect
- `width` / `height` → standard ZK widget sizing; applied as inline styles on `<img>` and propagated via `setFlexSizeW_` / `setFlexSizeH_`. **Cap fact**: the `load` handler re-clamps the img to `min(cropperWidth, naturalWidth)` / `min(cropperHeight, naturalHeight)` — ZK **never upscales** the image. Setting `width` larger than the image's natural width therefore leaves dead space inside the root (inline width stays on `.z-cropper`) in *every* theme; it's only invisible in themes with a borderless wrapper (iceblue). Pages should not set `width`/`height` beyond the image's natural size.
- `crossOrigin` → sets `crossorigin` attribute on `<img>`

## Composition invariants

- **The cropper requires an externally determined size** (explicit `width`/`height`, hflex/vflex, or a sized parent). `onSize → _jcrop.resizeImage(jqCropper.width(), jqCropper.height())` feeds the ROOT's rendered size back into the Jcrop holder/img — but with the `display: inline-block` shrink-wrapped root, the root's size is itself derived from the holder. An unsized cropper therefore collapses in a feedback loop (first onSize reads ~0 → holder set to 0 → root shrinks to 0 → stable broken state; observed live 2026-06-05 when the preview page's `width` was removed). Pages must set `width` equal to (never above — see the cap fact under Attribute support) the image's natural width.
- The root `.z-cropper` must have `position: relative` for the JS-positioned toolbar to anchor correctly. Jcrop also depends on the image being a relative-positioned context.
- The toolbar `z-index: 601` is hard-coded in the mold (`style="z-index: 601;"`). This value is chosen to exceed Jcrop's internal maximum z-index. Theme CSS must not set a `z-index` on `.z-cropper-toolbar` that conflicts (the inline style already wins).
- `overflow: hidden` on `.z-cropper` is required so that the `border-radius` on the wrapper clips the Jcrop-injected content correctly.
- `.z-cropper-holder img` has `max-width: none` (structural — confirmed in compiled theme CSS) — Jcrop needs this so the image inside its holder is not constrained by container width; removing this breaks crop geometry.
- The toolbar is positioned by JS (`_positionToolbar`) using `zk(toolbar).position(dim, position, ...)`. The theme must not position `.z-cropper-toolbar` with `position: fixed` or `position: relative` — the JS positioning mechanism requires `position: absolute`.

## Sibling decomposition

No ZK component sibling shares this CSS file. The toolbar button row (`.z-cropper-crop`, `.z-cropper-cancel`) is similar in intent to toolbar button items but has its own mold-generated structure (`<li>` inside `<ul>`) and must be styled independently. The wrapper chrome pattern (border + radius on `.z-cropper`) parallels `pdfviewer` and `signature` in T3 wrapper approach.

## Contract

`cropper.css.dsp` — shipped as `../zkcml/zkmax/src/main/resources/web/js/zkmax/cropper/css/cropper.css`, auto-scanned and emitted as `cropper.css.dsp` by the build.

## Edition

PE (per ZKDoc `cropper.md` edition badge, since 8.6.0; code ships in zkmax sources — source location does NOT determine the licensed edition, the ZKDoc badge does)

## Notes

- **Jcrop-injected DOM requires theme-shipped structural CSS** (corrected 2026-06-05; the earlier "off-limits" rule here was wrong). `.z-cropper-holder`, `.z-cropper-area`, `.z-cropper-vline`, `.z-cropper-hline`, `.z-cropper-tracker`, `.z-cropper-handle`, `.jcrop-dragbar` are Jcrop-injected but **unstyled by Jcrop itself** — copy the structural rules from the upstream codegen `cropper.css.dsp` (path in the intro above). Freely restylable: only `.z-cropper` wrapper chrome and the `.z-cropper-toolbar` + `<ul>/<li>/<a>` descendants; for the Jcrop subtree restyle decorative values only (colors/opacity), never geometry.
- **`.z-cropper-canvas` does not exist.** The earlier contract draft listed `.z-cropper-canvas` as a key selector — this is wrong. The compiled theme CSS and eval report both confirm `.z-cropper-holder` is the Jcrop-injected image area; `.z-cropper-canvas` is never emitted by ZK or Jcrop.
- The `baseClass` passed to Jcrop is the widget's `zclass` (`z-cropper`), which is why Jcrop emits classes like `.z-cropper-holder`, `.z-cropper-area`, `.z-cropper-vline`, `.z-cropper-hline`, `.z-cropper-tracker`, `.z-cropper-handle` instead of the default `jcrop-` prefix. The dragbars keep the `jcrop-dragbar` name because Jcrop hardcodes that within its source.
- The `zWatch.listen({ onSize: self })` hook in `bindChildren_` calls `_jcrop.resizeImage()` on container resize — the image and Jcrop selection geometry auto-adapt. Theme CSS must not set `width` or `height` on `.z-cropper-holder` or the contained `img` — JS owns those.
- Toolbar items use plain text ("crop", "cancel") in the default ZK widget. Compiled theme CSS shows a pill-shaped button cluster with a filled primary-color background as the convention; any theme may restyle them freely — they are fully theme-owned `<li><a>` elements.
