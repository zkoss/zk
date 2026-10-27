# Reset CSS scoping for JS-Embed (`org.zkoss.zul.theme.browserDefault`)

## Why

ZK's reset/normalize CSS is global. When a customer embeds a ZUL page into their own HTML
via the **JS Embed API**, ZK does **not** use an `<iframe>` — it renders its widgets directly
as DOM elements (rooted at `<div class="z-page">`) inside the host document. Host and ZK
therefore share one document, one `<html>`/`<body>`, and one CSS scope. ZK's global reset
(`*{box-sizing}`, `html`, `body`, `a`, `img`, scrollbars) then **leaks onto the host page's
own elements**, which the customer doesn't want.

`org.zkoss.zul.theme.browserDefault` is the switch that confines the reset to the ZK subtree
so the host page is left alone.

## The switch

`org.zkoss.zul.theme.browserDefault` is a **boolean** library property (set in `zk.xml`),
default **`false`** — same name and polarity as ZK's historical property:

| Value | Meaning | Served reset |
|-------|---------|--------------|
| `false` (default) | Override the browser default with ours — global reset, exactly as a standalone ZK app expects. | `reset.css` |
| `true` | Do **not** override the host: confine the reset to the ZK subtree and never touch the host `<html>`/`<body>`. | `reset-embed.css` |

```xml
<!-- zk.xml — opt a JS-Embed deployment into host-safe reset -->
<library-property>
    <name>org.zkoss.zul.theme.browserDefault</name>
    <value>true</value>
</library-property>
```

Existing apps need no change: unset/`false` reproduces today's global reset byte-for-byte.

## How it works (DSP-free)

Legacy ZK themes implemented this in `norm.css.dsp` with a server-side DSP `<c:if>` that read
the property and prefixed `.z-page ` onto every selector. Marble does **not** use DSP. Instead:

1. **One source** — [`web/zul/css/base/_reset.css`](../../zul/src/main/resources/web/zul/css/base/_reset.css)
   — with the `<html>`/`<body>` rules wrapped in `/* page-frame:start … page-frame:end */`
   markers.
2. **Two built variants** ([`scripts/build-css.js`](../../scripts/build-css.js) `buildResetVariants`),
   emitted as their own stylesheets (no longer bundled into `norm.css.dsp`):
   - `reset.css` — the source verbatim (global, frame intact, unscoped).
   - `reset-embed.css` — the page-frame block dropped and the remaining widget reset wrapped
     in **`@scope (.z-page) { … }`**, so it can only match inside the ZK subtree. Neither
     variant declares the layer order; `norm.css.dsp` does (see
     [layer-architecture-review.md](layer-architecture-review.md)).
3. **Java picks the variant** — [`MarbleThemeProvider.getThemeURIs`](../../zul/src/main/java/org/zkoss/zul/theme/StandardThemeProvider.java)
   reads the property with `Boolean.parseBoolean(Library.getProperty(…))` and inserts the
   chosen file **immediately before the `~./zul/css/zk.wcs` widget bundle**, preserving the
   reset's historical "loads first" cascade position.

### Build note — CleanCSS and `@scope`

CleanCSS (level 1, used by the packaged build) does not understand `@scope` — it drops the
first nested rule and hoists the rest out of the block. So `reset-embed.css` is built by
**minifying the plain rules first** (while they are ordinary CSS) and only **then** wrapping
the result in `@scope`; the minifier never parses the at-rule. (`@layer` has a separate,
pre-existing CleanCSS workaround in `minifyCss`.)

## Browser support

`@scope` requires Chrome/Edge 118+, Safari 17.4+, Firefox 128+ — within the theme's
"modern browsers, last 2 versions" mandate. Only the embed (`true`) path uses `@scope`;
standalone apps are unaffected.

## Known limitation — floating widgets in embed mode

When opened, ZK floating widgets (combobox/bandbox dropdowns, menupopup, tooltip, errorbox,
notification, overlapped/modal window) are moved to `document.body` via `makeVParent()`, i.e.
**outside `.z-page`**. The `@scope (.z-page)` reset therefore does not reach an open float, so
in embed mode a float's custom scrollbar styling falls back to the browser default. Box-sizing
is unaffected — Marble's component CSS already sets it on the float roots. This matches the
behavior of ZK's legacy `.z-page `-prefix approach.

## Tests

[`zkthemelab/src/test/playwright/reset-scoping.spec.ts`](../../zkthemelab/src/test/playwright/reset-scoping.spec.ts)
(project `reset`) verifies, against the running preview app:
- default mode links `reset.css` ahead of `zk.wcs` and does not link `reset-embed.css`;
- `reset.css` is unscoped and keeps the `html`/`body` frame;
- `reset-embed.css` is `@scope (.z-page)`-confined with the frame dropped;
- the extracted reset still takes effect (body `margin:0`, widgets `border-box`).

Run: `npx playwright test --config zkthemelab/src/test/playwright/playwright.config.ts --project reset`
(needs `withjdk.sh 17 mvn test exec:java@preview-app`).
