# Our `.css.dsp` files are NOT DSP-processed

## The fact

This theme's build pipeline reads `.css` sources and writes them out with an added `.dsp` extension — **nothing else**. No EL evaluation, no taglib injection, no JSP-style processing. The `.dsp` extension is kept only because ZK's lang-addon `<css-uri>` registrations expect `.css.dsp` paths.

At runtime ZK serves these files as static text. The WCS bundler (`zk.wcs`) reads each `.css.dsp` and concatenates it verbatim into the per-page CSS bundle.

## What this means in practice

### `${…}` / `${c:encodeURL(…)}` / `<%@ taglib %>` syntax silently destroys the file

If a source `.css` contains any DSP-style expression like:

```css
.z-colorpicker-overlay {
    background-image: url("${c:encodeURL('~./zkex/img/colorbox/colorpicker_gradient.png')}");
}
```

then:
1. The build writes that string verbatim into the `.css.dsp` output.
2. ZK's WCS bundler attempts to process the file as DSP (the `.dsp` extension triggers this path), even though the file lacks the `<%@ taglib uri="..." prefix="c" %>` declaration.
3. The DSP processor fails to resolve the `c:` prefix → throws → returns empty content for that file.
4. The empty content is appended to the WCS bundle. **Everything in that source file — including rules with no EL — is now missing from the page.**
5. There is no console error, no build warning, no test failure. The browser just sees no rules for `.z-colorpicker-overlay` (or anything else in that file). Components fall back to default ZK CSS, or to nothing at all.

A clear symptom: a component that used to be styled by the theme suddenly renders with `display: block; width: <some default>;` and zero theme styling — and the WCS bundle has zero occurrences of that component's class names.

### Rules

1. **Never** write `${…}` or `<%@ %>` syntax in `.css` source files in this theme.
2. For URLs referring to ZK widget images (sprite PNGs, GIFs), use the **absolute path with the `/zkau/web/` prefix**:
   ```css
   background-image: url(/zkau/web/zkex/img/colorbox/colorpicker_gradient.png);
   ```
   ZK serves these via its update handler regardless of version prefix — `/zkau/web/{anything}/zkex/img/...` and `/zkau/web/zkex/img/...` both return 200.
3. For self-hosted theme assets (icon SVGs), prefer **inline data URIs** (`url("data:image/svg+xml,…")`) — the same approach the rest of `_icons.css` uses.

### Detecting the problem fast

Quick probe to confirm a `.css.dsp` is actually loaded into the page:

```bash
curl -s "${PREVIEW_URL}/zkau/web/{ver}/_zkiju-<theme>/zul/css/zk.wcs?_t=$(date +%s%N)" \
  | grep -c "z-{component}"
```

If grep returns 0 but the component is on the page, the file content is being silently dropped. Search the source for `${`.

Treat `.css.dsp` as plain text with the `.dsp` rename. Don't use the syntax. (`scripts/check-css-dsp.js` also checks that every `css-uri` has a built `.css.dsp`.)
