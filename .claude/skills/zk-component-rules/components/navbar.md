# navbar / nav / navitem / navseparator

A navigation container (`<navbar>`) holding leaf items (`<navitem>`), collapsible groups (`<nav>`,
which nest arbitrarily deep) and dividers (`<navseparator>`). EE (`zkmax`).

## DOM structure

```
nav.z-navbar[.z-navbar-vertical | .z-navbar-horizontal][.z-navbar-collapsed]
└─ ul[role=menubar]                       ← the navbar's cave; id "{uuid}-cave"
   ├─ li.z-navitem[role=none]             ← leaf item
   │  └─ a.z-navitem-content[role=menuitem]    ← id "{uuid}-a"
   │     ├─ i.z-icon-* | img.z-navitem-image   (optional)
   │     ├─ span.z-navitem-text
   │     └─ span.z-navitem-info                (optional badge)
   ├─ li.z-nav[role=none][.z-nav-open][.z-nav-selected]      ← collapsible group
   │  ├─ a.z-nav-content[role=menuitem][aria-haspopup][aria-expanded]   ← id "{uuid}-cnt"
   │  │  ├─ i.z-icon-* | img.z-nav-image  (optional)
   │  │  ├─ span.z-nav-text
   │  │  └─ span.z-nav-info               (optional badge)
   │  └─ ul[role=menu]                    ← the SUBMENU CAVE; id "{uuid}-cave"
   │     └─ li.z-navitem …                ← children, same shape as above (may nest .z-nav)
   └─ li.z-navseparator[role=separator]        ← content is a single &nbsp;
```

Two facts that catch people out:

- **The submenu cave `<ul>` carries no class.** It is addressable only as `.z-nav > ul` (or by its
  `role="menu"` / `id="{uuid}-cave"`). A failure message that prints `element.className` for it comes
  out empty — name it by tag plus the owning `.z-nav`.
- **The content element is an `<a href>`, not the `li`.** Every visual rule (padding, state layer,
  focus) belongs on `.z-nav-content` / `.z-navitem-content`. `.z-navitem` and `.z-nav` are the
  structural `<li>`s and carry the state classes.

## Two content classes, one shared appearance

`.z-nav-content` (group header) and `.z-navitem-content` (leaf) are separate classes with the same
role. Any rule for one almost always needs the other — including hover, active and **focus**. Styling
only `.z-navitem-content` leaves group headers on the browser's own defaults.

## State classes

| Class / attribute | On | Meaning |
|---|---|---|
| `.z-nav-open` | `li.z-nav` | Group is expanded. Its cave is visible only in this state. |
| `.z-nav-selected` | `li.z-nav` | Group contains the selected item. |
| `.z-navitem-selected` | `li.z-navitem` | Item is the current selection. |
| `[disabled]` (attribute) | `a.z-*-content` | Disabled. **There is no `-disabled` class** — select on the attribute. Also gets `aria-disabled`. |
| `.z-navbar-collapsed` | root | Icon-rail mode (labels hidden). |
| `.z-navbar-vertical` / `-horizontal` | root | Orientation. |
| `.z-nav-popup` | detached `<ul>` | Horizontal-mode overflow / dropdown list. |

## Keyboard model: one tab stop, arrow keys inside

With the **EE `za11y`** addon on the classpath, the navbar follows the ARIA menu pattern with a
*roving tabindex*:

- Exactly one `[role=menuitem]` in the navbar has `tabindex="0"`; every other `<a>` is set to
  `tabindex="-1"` (`Navbar`/`Nav`/`Navitem` are patched in `web/js/za11y/zkmax/nav-a11y.js`).
- So **Tab enters and leaves the whole navbar in a single stop** — tabbing does not walk the items.
  Arrow keys move focus within, and *that* is how a group header ever becomes focused.
- Opening a group moves focus to its first visible child on its own.

The molds themselves emit no `tabindex`, so **without `za11y` every `<a href>` is an ordinary tab
stop instead.** Either way both content classes can receive keyboard focus, so both need a focus
affordance — but a test that walks with Tab alone will silently never reach a group header. Click the
item, then press a key: the click focuses it, the keypress promotes it to `:focus-visible`.

## Group expand/collapse

`Nav.open()` / `.close()` do two things: toggle `.z-nav-open` on the `<li>`, **and** drive the cave's
visibility with jQuery inline styles — `show`/`hide` or `slideDown`/`slideUp`, chosen by the widget's
`disableSlide` flag and ZK's animation speed. So:

- The cave must render hidden whenever `.z-nav-open` is **absent** — that is the only thing governing
  the server-rendered closed state, before any JS has run.
- After the first toggle, jQuery's **inline** `display` is what is in effect; theme CSS cannot
  override it and should not try.
- Because the collapse can be a height animation, the cave has to clip its overflow. That makes the
  submenu cave a **clipping ancestor sitting exactly on the item's own box edges**, so an
  outward-drawn affordance on a submenu item is cut. See `reference/focus-ring-clipping.md`.

One exception worth knowing: for a **topmost** group in a **collapsed** navbar, `open`/`close` skip
the cave animation entirely (the group is presented as a floating popup instead).

## Badge (`info`)

`badgeText` renders `span.z-nav-info` / `span.z-navitem-info` inside the content link. In
collapsed/icon-rail mode there is no label to sit beside it, so a theme typically has to position it
over the icon — meaning the content link needs a positioning context.
