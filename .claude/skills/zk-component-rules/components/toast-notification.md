# toast / notification

Transient overlays. Toast is fully ephemeral; notification can be sticky or dismissible.

## Toast: nine positions

The position class is on a position container, not on the toast itself. `Toast.getPositionWrapper_` (`zkmax/wgt/Toast.ts`) creates the containers on demand under one wrapper appended to `<body>`:

```
div#_z_toastpositionwrapper.z-toast-position-wrapper
└─ div.z-toast-position.z-toast-position-{pos}      (one per position in use)
   └─ div.z-toast[.z-toast-{type}]
```

`{pos}` is the `position` value with `_` replaced by `-` (`Toast.ts`, `pos.replace('_','-')`): three rows (`top`, `middle`, `bottom`) by three columns (`left`, `center`, `right`).

When no position is given, `Toast.show` uses `top_center` (written `top-center` in the class).

## Severity classes

Both toast and notification take a `type` that becomes a severity class, `.z-toast-{type}` or `.z-notification-{type}`. The icon mapping covers three types (`Toast.iconMap` in `Toast.ts`, the same table in `zul/wgt/Notification.ts`):

- `info` (`z-icon-info-circle`)
- `warning` (`z-icon-exclamation-circle`)
- `error` (`z-icon-times-circle`)

Style by severity to align with theme colours (the semantic palette).

## Inner DOM structure

Toast:
```
.z-toast[.z-toast-{type}]
├─ i.z-toast-icon                (severity icon + the mapped z-icon-* class)
├─ .z-toast-content              (message text)
└─ .z-toast-close                (only when closable; wraps i.z-toast-icon.z-icon-times)
```

Notification:
```
.z-notification[.z-notification-{type}]
├─ .z-notification-icon          (severity icon)
├─ .z-notification-content       (message text)
└─ .z-notification-close         (close button)
```

## Auto-dismiss

`Toast.show(msg)` defaults to 5000 ms (`Toast.java:114,123`); `Notification.show` defaults to no timeout (`Notification.java`). With no timeout and `closable=false`, either one closes on the next click outside it (`Notification.ts:118`, `Toast.ts:124`); with `closable=true` it stays until its close button is used.

## .z-notification is a bare layout container — visual styling goes on the content

`.z-notification` is a `display: table` positioning shell. All visual card styling (background color, shadow, border-radius, padding) belongs on `.z-notification-content`.

Do not put these on `.z-notification`:
- `padding` — shifts `.z-notification-content` inside the table, breaking `.z-notification-icon` positioning (the icon's `left` is measured from the notification outer edge, not the content edge)
- `box-shadow` — creates a double shadow (content already has one) and the transparent padding gap shows the page background through it, creating a dark halo
- `border-radius` — redundant; only the content's radius matters visually

The content's left padding must leave room for the icon (and for any accent stripe the theme draws).

## Icon positioning is relative to .z-notification (not to content)

`.z-notification-icon` is a direct sibling of `.z-notification-content` inside `.z-notification`, and is `position: absolute`. Its containing block is `.z-notification`. Therefore:

- If `.z-notification` has **no padding**, the icon's `left` offset equals its distance from the content's left edge.
- If `.z-notification` has left padding, the icon's `left` offset is measured from the outer edge, so the icon shifts left relative to the content by that padding and can land on top of any accent stripe.

The content's own left padding is measured from the content's left edge, not from the notification's left edge.

A static preview gallery must include the `.z-notification-icon` element. A mockup without it hides the icon-overlap bug described above.

## Transient overlay backgrounds MUST be opaque

Toast and notification are floating overlays positioned over arbitrary page
content (ZK sets them `position:absolute` with a high inline `z-index`, e.g.
`1800`). `top_left` / `top_center` etc. routinely land on top of the app header
or other content.

The card background (`.z-notification-content` / `.z-toast-content`, including
every severity variant) **must be fully opaque (alpha = 1)**. A translucent fill
lets the underlying page content bleed through, so the overlay reads as if the
page is *on top of* it even though its z-index is correct. Any overlay over
unknown content needs an opaque surface.

To get a tinted-but-opaque variant background, composite the tint over an opaque
surface (for example `color-mix` of the status colour over the surface colour)
instead of using an alpha < 1.

A static mockup sits over a plain page background, so a translucent fill looks
fine there — the bug only appears once the overlay covers real content. Assert
opacity (alpha = 1) directly rather than trusting a mockup.

## Content min-height taller than one line → centre the text explicitly

Both `.z-notification-content` and `.z-toast-content` host the message text
directly. If a theme gives them a `min-height` larger than a single line's
natural height (line-box + top/bottom padding), the severity icon is centred
(`position:absolute; top:50%`) but block layout flows the text from the top,
leaving the slack at the bottom — so a single-line message reads as top-aligned
and misaligned with the icon.

Centre the content explicitly (it cannot rely on padding):

```css
.z-notification-content, .z-toast-content {
    display: flex;
    align-items: center;   /* single line centres; multi-line is unaffected */
}
```

(The pointer variant centres differently — `.z-notification-pointer ~ .z-notification-content`
uses `display:table-cell; vertical-align:middle`, which has higher specificity
and is left intact.)

## Bundle

Neither `notification` nor `toast` has a `css-uri`. Both CSS files must ride in the global bundle served by `zk.wcs`; no separate `notification.css.dsp` or `toast.css.dsp` is requested. See `reference/component-css-must-be-bundled-or-css-uri.md`.
