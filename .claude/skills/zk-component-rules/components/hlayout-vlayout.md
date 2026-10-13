# hlayout / vlayout

Simple horizontal and vertical layout containers. CE-baseline components.

## DOM structure

```
.z-hlayout                         (horizontal layout root)
└─ .z-hlayout-inner * N            (child wrapper — one per child component)

.z-vlayout                         (vertical layout root)
└─ .z-vlayout-inner * N            (child wrapper — one per child component)
```

## hbox / vbox (related but separate)

`hbox`/`vbox` are the older layout components with a slightly different DOM:

```
.z-hbox
├─ .z-hbox-separator               (separator between children)
└─ (child components directly, no -inner wrapper)

.z-vbox
└─ .z-vbox-separator
```

Do not confuse `.z-hlayout-inner` (hlayout) with the hbox structure (no `-inner`).

## Alignment classes

- `.z-valign-top` / `.z-valign-middle` / `.z-valign-bottom` — vertical alignment on children
- `.z-flex` — enables flex expansion on a child

Default `valign` is `top` (`Hlayout.java:29` / `Hlayout.ts:23`); ZK adds `.z-valign-middle` /
`.z-valign-bottom` to the **root** when set. For aligning a `<label>` beside a taller field,
this is the deciding factor — see `reference/inline-label-alignment.md` (a bare `<div>` row puts
the label ~30px high, default hlayout ~10px high, `valign="middle"` centres at 0px).

## Spacing comes from ZK-injected inline padding — **not** from parent `gap`

ZK's `zul.box.Layout` (`Layout.ts` `setSpacing` / `encloseChildHTML_`) writes an
inline padding on every non-last `-inner` to realise the widget's `spacing`
attribute:

- hlayout → `style="padding-right: <spacing>"` on each `.z-hlayout-inner` except the last
- vlayout → `style="padding-bottom: <spacing>"` on each `.z-vlayout-inner` except the last
- Default `spacing` is `5px`. Setting `spacing="auto"` makes ZK omit the inline
  style entirely (the only mode where the theme controls spacing).

Theme rules that BREAK this contract:

- ❌ `display: flex; gap: …` on `.z-hlayout` / `.z-vlayout` — fixed gap overrides any
  attribute-driven value, so `<hlayout spacing="16px">` looks identical to
  `<hlayout spacing="0">`.
- ❌ `padding-right: 0 !important` (or `padding-bottom: 0 !important`) on `-inner` —
  wipes ZK's inline value; the attribute becomes a no-op.

Theme rules that respect it:

- ✅ `.z-hlayout` → `white-space: nowrap` (lets inline-block children stay on one line).
- ✅ `.z-hlayout-inner` → `display: inline-block; vertical-align: top` (or the
  valign your `.z-valign-*` modifier dictates).
- ✅ `.z-vlayout-inner` → block flow (each `-inner` is a `<div>` already), no extra rule needed.
- ✅ Customise spacing only via the user-facing `spacing="…"` attribute or by
  setting `spacing="auto"` and writing your own CSS.

This is a ZK fact (predates flexbox) — every theme has to honour it, regardless
of design system.

## Overflow clipping — `.z-hlayout` crops children that overflow their box

`.z-hlayout` is given `overflow: hidden` (it relies on `white-space: nowrap` to
keep inline-block children on one line). A consequence that bites overlays:

> **Any child that overflows the hlayout's box is clipped** — there is no way for
> an absolutely-positioned descendant to escape an ancestor's `overflow:hidden`.

This breaks elements that *intentionally* spill past their host's edge:

- **Corner badges** (a count/dot positioned with `transform: translate(50%, -50%)`
  overhangs the host's top-right corner — the top/right arc gets cropped).
- **Outside focus rings** (`outline-offset`), **tooltips/popovers** rendered inline,
  ribbons, corner tags.

`.z-vlayout` does **not** set `overflow:hidden` (it is plain block flow), but cards
and many container chromes elsewhere do — so the trap is not unique to hlayout.

**Rule:** put an overlay-decorated widget in a **non-clipping flex row**, not an
`<hlayout>`. Use the `z-hstack*` utility (or `z-d-flex` + `z-gap-*`) — flexbox with
visible overflow, and it also spaces atomic widgets the canonical way:

```xml
<!-- ❌ clips the badge's overhanging corner -->
<hlayout><badge count="7" severity="error"><label sclass="z-icon-bell"/></badge></hlayout>

<!-- ✅ overflow visible -->
<div sclass="z-hstack-lg"><badge count="7" severity="error"><label sclass="z-icon-bell"/></badge></div>
```

This is theme-independent: any ZK theme that styles overlay badges hits the same
clipping, because the `overflow:hidden`-can't-be-escaped behaviour is CSS, not MD3.
(A theme *could* set `.z-hlayout { overflow: visible }`, but that risks the nowrap
row spilling its parent — the safer contract is "don't host overlays in hlayout".)

## CSS files

`box.css.dsp` (hbox/vbox), `layout.css.dsp` (hlayout/vlayout)
