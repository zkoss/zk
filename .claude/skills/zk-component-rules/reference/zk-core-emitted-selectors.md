# ZK-core-emitted selectors (not owned by any widget)

Some ZK CSS class names are produced directly by `zk/au.ts` and `zk/utl.ts` — they are **not** emitted by any registered widget, so no widget triggers the loading of the file that styles them. A theme that relies on the per-widget 1:1 auto-scan will silently ship without these styles even though the CSS file exists.

This file lists the known ZK-core-emitted selectors and the bundling rule themes must follow.

## The pipeline trap

A component CSS reaches the page by one of two routes:

1. **Per-widget load**: ZK loads a widget's `.css.dsp` only when a widget on the page is bound to it (`lang.xml` `css-uri` registration or convention).
2. **Global bundle**: a bundle that is part of `zk.wcs` is loaded on *every* page.

If a selector is emitted by ZK core (not a widget), nothing on the page triggers the per-widget load — so the per-widget `.css.dsp` is dead weight. The rules MUST live in a file that is in the global bundle, otherwise `zk.wcs` will not contain them.

## ZK-core-emitted classes (must be globally bundled)

| Selector(s) | Emitted by | Trigger |
|-------------|-----------|---------|
| `.z-loading` `.z-loading-indicator` `.z-loading-icon` | `zk/utl.ts` `progressbox()` | `zAu.cmd0.showBusy(msg)` / `zk.AuCmd0.showBusy(uuid, msg)` |
| `.z-modal-mask` | `zk/utl.ts` `progressbox()` (when `mask=true`) | same as above |
| `.z-renderdefer` | `zk/widget.ts` `redraw_()` (placeholder while `_renderdefer` timer pending) | element with `renderdefer="N"` attribute |
| `.z-apply-mask` `.z-apply-loading` `.z-apply-loading-indicator` `.z-apply-loading-icon` | `zk/effect.ts` `Mask._draw()` | `Clients.showBusy(component, msg)` / `wgt.showBusy(msg)` |
| `.z-error` `.z-error #zk_err-p` `.z-error .errornumbers` `.z-error .button` `.z-error .messagecontent` `.z-error .messages` `.z-error .messages .message` `.z-error .newmessage` | `zk/zk.ts` `_Erbx` class (constructor + `push()`) | `zk.error(msg)` / `Clients.evalJavaScript("zk.error('…')")` / uncaught client errors via `_zk.sendClientErrors` |
| `.z-error-box` | inline error layer | `Errorbox.ts` (component CSS) — a widget, but `lang.xml` registers no `css-uri` for it, so its rules are served from `zul/src/main/resources/web/js/zul/inp/css/input.css` |

The non-widget rows are NOT widget-scoped. The last row (`errorbox`) is included for contrast: `errorbox` IS a widget, but because it has no `css-uri` its rules must ride in another served bundle (`input.css.dsp`).

## Rule

If a selector listed in this file (or any future ZK-core-emitted selector) needs styling, the source file holding the rule MUST be part of the global bundle served by `zk.wcs`. Putting the rule in `js/zul/wgt/css/misc.css` is correct, but **the file must also be in that bundle**, otherwise `zk.wcs` won't contain it.

## How to verify

`curl -s ${PREVIEW_URL}/zkau/web/<ver>/_zkiju-<theme>/zul/css/zk.wcs | grep '\\.z-loading\\b'` (`<theme>` is the theme's name) — if zero hits, the bundle is missing the rule even when the `.css.dsp` file exists on disk.

## Showbusy DOM the rules must support

```html
<div id="zk_showBusy" role="alert">
  <div id="zk_showBusy-m" class="z-modal-mask"></div>
  <div id="zk_showBusy-t" class="z-loading">
    <div class="z-loading-indicator">
      <span class="z-loading-icon"></span> Loading...
    </div>
  </div>
</div>
```

- `.z-loading` is positioned by **JS-set inline `left/top`** (computed as `(innerWidth - txt.offsetWidth)/2`), so the CSS MUST set `position: absolute` (or `fixed`) for the inline values to take effect. Without that, the busy box stays at static flow position behind the mask.
- `.z-loading` MUST have a `z-index` above `.z-modal-mask`. Otherwise the message is hidden beneath the scrim.
- `.z-loading-icon` is rendered as a `<span>` with no built-in graphic — the theme has to supply the spinner (CSS-only border-spinner or SVG mask).

## Mask quirk — the vestigial inline `left/top`

`zk/utl.ts progressbox()` ALSO writes inline `style="left:Xpx;top:Ypx"` on **`.z-modal-mask`**, using current scroll offsets (`jq.innerX()`/`jq.innerY()`). Those inline values are a relic of the pre-fixed-positioning era when the mask was `position:absolute` and the inline `top:Y` compensated for scroll so the mask stayed in viewport.

Under modern `position:fixed` (what ZK 10 themes use), the inline `top:Ypx` becomes a literal viewport offset and the scrim shifts down by the scroll amount — leaving a Ypx gap at the top of the page when the user has scrolled. The CSS rule MUST neutralise this with:

```css
.z-modal-mask {
    position: fixed;
    inset: 0;
    top: 0 !important;
    left: 0 !important;
}
```

Without the `!important` pins, the inline `top:Ypx` from JS overrides `inset:0`'s top edge.

## Per-widget busy overlay (`.z-apply-*` family)

`Clients.showBusy(component, msg)` (or `wgt.showBusy(msg)`) creates this DOM via `zk/effect.ts Mask._draw()`:

```html
<div id="…" style="display:none">
  <div class="z-apply-mask"    style="display:block;top:Y;left:X;width:W;height:H"></div>
  <div class="z-apply-loading" style="top:Y;left:X">
    <div class="z-apply-loading-indicator">
      <span class="z-apply-loading-icon"></span> Processing...
    </div>
  </div>
</div>
```

- `.z-apply-mask` is the scrim over the widget. JS sets inline `top/left/width/height` from `$anchor.revisedOffset()` + `offsetWidth/Height`. CSS MUST use `position: absolute` (the offsets are document-relative since the wrapper is non-positioned and the wrapper is appended to body).
- `.z-apply-loading` is the centred indicator card, also `position: absolute`. JS centres it in `sync()` by overwriting `top/left`.
- `.z-apply-loading-icon` is a `<span>` — same spinner pattern as `.z-loading-icon`.
- Both `.z-apply-mask` and `.z-apply-loading` need z-indexes high enough to cover whatever the anchor is inside (ZK uses 89000/89500 by default; `Mask.sync` walks the anchor's ancestor chain and raises the z-index to clear any `non-auto` z-index it finds).

## Floating error panel (`.z-error` and friends)

`zk.error(msg)` / `Clients.evalJavaScript("zk.error('…')")` / uncaught client errors create this DOM via `zk/zk.ts` `_Erbx`:

```html
<div class="z-error" id="zk_err">       <!-- starts display:none, slideDown(1000) reveals -->
  <div id="zk_err-p">                   <!-- drag handle (zk.Draggable on this) -->
    <div class="errornumbers">N Errors</div>
    <div id="zk_err-remove-btn" class="button"><i class="z-icon-times"></i></div>
    <div id="zk_err-refresh-btn" class="button"><i class="z-icon-refresh"></i></div>
  </div>
  <div class="messagecontent">
    <div class="messages">
      <div class="message">msg 1</div>
      …
      <div class="newmessage">msg N</div>  <!-- starts display:none, slideDown(600) reveals, then renamed to .message -->
    </div>
  </div>
</div>
```

CSS requirements:
- `.z-error { display: none; }` is **mandatory** — `_Erbx` calls `jq(...).slideDown(1000)` on construction. slideDown needs the element to start hidden.
- `.z-error .newmessage { display: none; }` is **mandatory** — every subsequent `errorPush(msg)` appends a `.newmessage` and slideDowns it.
- `.z-error` must be positioned (fixed/absolute) with a high `z-index` so the panel is visible above the page.
- `.z-error #zk_err-p` is the drag handle; `cursor: move` is the only UX cue.

## Render-defer placeholder (`.z-renderdefer`)

`zk/widget.ts redraw_()` emits `<div ... class="z-renderdefer"></div>` while the widget's `_renderdefer` timer has not yet fired (set by `renderdefer="N"` attribute). The default ZK theme renders this with the same progress GIF as `.z-loading-icon` — visually consistent with showBusy. Themes should reuse the showBusy spinner so the deferred-render experience matches.

## ThemeProvider wiring (ZK-1671)

A `ThemeProvider` registered by a theme jar can be **silently replaced** by `StandardThemeProvider`, the default declared in `zul/src/main/resources/metainfo/zk/zk.xml`, depending on jar load order. When that happens, `WcsExtendlet.beforeWidgetCSS` is called on `StandardThemeProvider` (or null), not on the theme's provider, so any redirect or skip logic in the theme's provider never runs.

Symptoms:
- `zk.wcs` still contains stylesheets the provider was meant to redirect
- A print inside `beforeWidgetCSS` produces no output even after restart

A theme jar that supplies its own provider calls `Configuration.setThemeProvider(...)` and then `Configuration.setCustomThemeProvider(true)` in a `WebAppInit`. Without the second call, a later `WebAppInit` (for example from another addon jar) can still replace the provider. Returning `null` from `beforeWidgetCSS` makes `WcsExtendlet` skip that `<stylesheet>` entry entirely.

## Discovery checklist for future ZK-core-emitted classes

When investigating an "I see the scrim/overlay but nothing inside it" or "this className appears in the DOM but no CSS file styles it" report:

1. `grep -rn "className\\|class=" $ZK_SOURCE/zk/src/main/resources/web/js/zk/*.ts` (`$ZK_SOURCE` is the root of the ZK CE checkout) — search ZK core (not `js/zul/`) for the class string.
2. If the class is written by `au.ts` / `utl.ts` / `widget.ts` / `mount.ts`, it is core-emitted. Add the rule to a file already in the global bundle, or add a new file to the bundle.
3. After the fix, verify with the curl command above that `zk.wcs` contains the selector.
