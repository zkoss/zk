# ZK-core-emitted selectors (not owned by any widget)

Some ZK CSS class names are produced directly by `zk/au.ts` and `zk/utl.ts` — they are **not** emitted by any registered widget, so no widget triggers the loading of the file that styles them. A theme that relies on the per-widget 1:1 auto-scan will silently ship without these styles even though the CSS file exists.

This file lists the known ZK-core-emitted selectors and the bundling rule themes must follow.

## The pipeline trap

In this theme, `scripts/build-css.js` does two things for component CSS:

1. **Per-widget 1:1 auto-scan**: every `zul/src/main/resources/web/js/zul/**/css/<name>.css` is copied to `zul/codegen/resources/web/<name>.css.dsp`. ZK loads each file only when a widget on the page is bound to it (`lang.xml` `css-uri` registration or convention).
2. **Global bundle (`norm.css.dsp`)**: an explicit list of files (`normFiles` in `build-css.js`) concatenated into `zul/css/norm.css.dsp`. This bundle is part of `zk.wcs` and is loaded on *every* page.

If a selector is emitted by ZK core (not a widget), nothing on the page triggers the per-widget load — so the per-widget `.css.dsp` is dead weight. The rules MUST live in a file that is in `normFiles`, otherwise `zk.wcs` will not contain them.

## ZK-core-emitted classes (must be globally bundled)

| Selector(s) | Emitted by | Trigger |
|-------------|-----------|---------|
| `.z-loading` `.z-loading-indicator` `.z-loading-icon` | `zk/utl.ts` `progressbox()` | `zAu.cmd0.showBusy(msg)` / `zk.AuCmd0.showBusy(uuid, msg)` |
| `.z-modal-mask` | `zk/utl.ts` `progressbox()` (when `mask=true`) | same as above |
| `.z-renderdefer` | `zk/widget.ts` `redraw_()` (placeholder while `_renderdefer` timer pending) | element with `renderdefer="N"` attribute |
| `.z-apply-mask` `.z-apply-loading` `.z-apply-loading-indicator` `.z-apply-loading-icon` | `zk/effect.ts` `Mask._draw()` | `Clients.showBusy(component, msg)` / `wgt.showBusy(msg)` |
| `.z-error` `.z-error #zk_err-p` `.z-error .errornumbers` `.z-error .button` `.z-error .messagecontent` `.z-error .messages` `.z-error .messages .message` `.z-error .newmessage` | `zk/zk.ts` `_Erbx` class (constructor + `push()`) | `zk.error(msg)` / `Clients.evalJavaScript("zk.error('…')")` / uncaught client errors via `_zk.sendClientErrors` |
| `.z-tooltip` | helper layer | tooltip popup helper |
| `.z-error-box` | inline error layer | `Errorbox.ts` (component CSS) — registered as widget |

The non-widget rows are NOT widget-scoped. The last row (`errorbox`) is included for contrast: `errorbox` IS a widget and its CSS is loaded the normal per-widget way.

## Rule

If a selector listed in this file (or any future ZK-core-emitted selector) needs styling, the source file holding the rule MUST appear in `normFiles` in `scripts/build-css.js`. Putting the rule in `js/zul/wgt/css/misc.css` is correct, but **the file must also be registered in `normFiles`**, otherwise the bundle won't contain it.

## How to verify

`curl -s ${PREVIEW_URL}/zkau/web/<ver>/_zkiju-<theme>/zul/css/zk.wcs | grep '\\.z-loading\\b'` — if zero hits, the bundle is missing the rule even when the `.css.dsp` file exists on disk.

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
- `.z-loading` MUST have a `z-index` above `.z-modal-mask` (the mask is `1400` in this theme; the busy box uses `1450`). Otherwise the message is hidden beneath the scrim.
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

`<theme-provider-class>` in `metainfo/zk/zk.xml` is loaded from a jar and can be **silently overridden** by `StandardThemeProvider` depending on jar load order (see `Configuration.java` lines 175–178). When that override hits, `WcsExtendlet.beforeWidgetCSS` is called against `StandardThemeProvider` (or null), not your provider — so any redirect / skip logic you wrote NEVER RUNS.

Symptoms:
- `zk.wcs` still contains stylesheets you tried to redirect (e.g. `~./zul/font/font-awesome.css.dsp`)
- A `System.err.println` inside `beforeWidgetCSS` produces zero output even after restart

Fix (apply once per theme):

```java
public class MarbleThemeWebAppInit implements WebAppInit {
    public void init(WebApp webapp) throws Exception {
        Themes.register("marble", "Marble", 700);
        Configuration config = webapp.getConfiguration();
        config.setThemeProvider(new MarbleThemeProvider());
        config.setCustomThemeProvider(true);  // locks it — no later init can replace it
    }
}
```

Without `setCustomThemeProvider(true)`, a later `WebAppInit` (e.g. from another addon jar) can still call `setThemeProvider(null)` and your provider vanishes.

**Do NOT rely on a 0-byte stub at `~./marble/zul/font/font-awesome.css.dsp`** as a workaround — the stub only helps if your provider's redirect runs, which it won't under the override race. Returning `null` from `beforeWidgetCSS` is the correct answer (`WcsExtendlet` honours `null` as "skip this `<stylesheet>` entry entirely").

## Discovery checklist for future ZK-core-emitted classes

When investigating an "I see the scrim/overlay but nothing inside it" or "this className appears in the DOM but no CSS file styles it" report:

1. `grep -rn "className\\|class=" /Users/hawk/Documents/workspace/ZK10/zk/zk/src/main/resources/web/js/zk/*.ts` — search ZK core (not `js/zul/`) for the class string.
2. If the class is written by `au.ts` / `utl.ts` / `widget.ts` / `mount.ts`, it is core-emitted. Add the rule to a file already in `normFiles`, or add a new file to `normFiles`.
3. After the fix, verify with the curl command above that `zk.wcs` contains the selector.
