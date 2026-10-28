# breadcrumb

A horizontal navigation trail: an ordered list of `Breadcrumbitem` children, each rendered
as a link (or plain text for the current/disabled item) and separated by an author-supplied
separator (text or icon). Supports client-only collapsing of the middle items when the item
count exceeds `maxItems`, replacing them with an expandable ellipsis button.

## Edition

CE — ships in `zul.jar` (`org.zkoss.zul.Breadcrumb` / `org.zkoss.zul.Breadcrumbitem`,
package `zul.wgt`). New in ZK 10.4.0 (`@since 10.4.0`). No PE/EE counterpart.

## DOM structure (from mold/breadcrumb.js + mold/breadcrumbitem.js)

```
nav.z-breadcrumb                                  ← root <nav>, this.domAttrs_()
└─ ol.z-breadcrumb-list
   ├─ li.z-breadcrumbitem[.z-breadcrumbitem-disabled]     ← one per Breadcrumbitem child
   │   └─ a[href][rel="noopener noreferrer" when target set]   ← when href is set AND not disabled
   │      -- or, mutually exclusive --
   │      span[tabindex="-1"]                                  ← when href is unset OR disabled=true
   │      (both variants contain the SAME optional content: image, then icon, then raw
   │       label text — see "Item content" below)
   ├─ li.z-breadcrumb-separator                       ← injected between every consecutive
   │   │                                                pair of items; NEVER before the first
   │   │                                                or after the last
   │   └─ text node (encoded `separator` string)
   │      -- or, when separator starts with "icon:" --
   │      i.{iconSclass}                              ← e.g. <i class="z-icon-chevron-right">
   ├─ li.z-breadcrumb-ellipsis                         ← JS-INJECTED ONLY during a maxItems
   │   │                                                collapse; not present in the mold output
   │   └─ button[type="button"]                        ← textContent = "…"
   └─ (li.z-breadcrumbitem / li.z-breadcrumb-separator repeat)
```

`.z-breadcrumb-ellipsis` and any `data-zk-bc-injected` node are **client-only** — they never
come from the server-rendered mold. A theme that only reads `mold/breadcrumb.js` will miss
them entirely; they must be reconciled from `Breadcrumb.ts` (`_applyCollapse`/`_undoCollapse`).

## Item content (shared LabelImageWidget rendering)

`Breadcrumbitem` extends `zul.LabelImageWidget` (Java: `LabelImageElement`). The `<a>`/`<span>`
content is assembled by the shared `domContent_()` helper, in this fixed order, each part
optional and space-joined:

1. `<img>` (from `image`) — rare for a breadcrumb but supported
2. icon — `<i class="{iconSclass}" aria-hidden="true">` for a single icon, or
   `<span class="z-icon-stack">` wrapping multiple `<i>` for `iconSclasses`
3. raw encoded label text — **no wrapping element**; it is a bare text node inside the
   `<a>`/`<span>`, not a `.z-breadcrumbitem-label` span

Because the label has no wrapping element, a selector like `.z-breadcrumbitem > a > .z-label`
will never match — style the label via the `<a>`/`<span>` element itself.

## State classes

- `.z-breadcrumbitem-disabled` — added to the `<li class="z-breadcrumbitem">` **root**
  (via `domClass_()`), not to the inner `<a>`/`<span>`, when `disabled="true"`. Setting
  `disabled` also forces the mold to render a `<span>` instead of `<a>` regardless of
  whether `href` is set (`this._href && !this._disabled` gates the `<a>` branch) — so a
  disabled item is never a real link even if it has an `href`.
- There is no `.z-breadcrumb-open`/`-collapsed` class on the root when maxItems collapses
  the middle; the only observable signal is the presence/absence of `.z-breadcrumb-ellipsis`
  and the `data-zk-bc-hidden`/`data-zk-bc-injected` attributes below.
- No `readonly` / `invalid` / `inplace` states exist for either widget.

## Attribute support

- `disabled="true"` (Breadcrumbitem) → `.z-breadcrumbitem-disabled` on the item's `<li>`
  root, AND switches its content element from `<a>` to `<span tabindex="-1">`.
- `href` (Breadcrumbitem) → selects `<a href="…">` vs `<span tabindex="-1">` as the content
  element. Unset/empty `href` renders `<span>` — this is how the terminal ("current page")
  item is normally authored, since it should not be a link.
- `target` (Breadcrumbitem) → only meaningful on the `<a>` branch; when set, ZK adds
  `rel="noopener noreferrer"` alongside `target="…"` (tabnabbing guard — do not strip via CSS,
  it is a security attribute, not styling).
- `separator` (Breadcrumb) → either a literal string rendered as an encoded text node inside
  every `.z-breadcrumb-separator`, or, when the value starts with the literal prefix `icon:`,
  an `<i class="{rest-of-string}">` instead (e.g. `separator="icon:z-icon-chevron-right"` →
  `<i class="z-icon-chevron-right">`). A theme must style **both** forms — text and icon — on
  `.z-breadcrumb-separator`, never assume one or the other.
- `maxItems` (Breadcrumb) → gates the client-only collapse in "Composition invariants" below.
  `0` (default) disables collapse; the setter rejects `1` server-side and client-side (a
  single kept item cannot collapse below the mandatory first+last pair).

## Composition invariants

### The collapse is 100% client-side — the mold never renders it

`mold/breadcrumb.js` always renders **every** item and **every** natural separator; it has
no knowledge of `maxItems` collapse. All hiding happens after mount, in
`Breadcrumb.ts#_applyCollapse()`, called from `bind_()` and re-run on certain child
rerenders. This means:

- A theme (or a test) that only inspects the initial server HTML will see every item
  uncollapsed. The collapsed view only exists in the live DOM after JS runs.
- Collapse toggles a **data attribute**, never inline `style`: `data-zk-bc-hidden="true"`
  is set on each collapsed item's `<li>` and on the *natural* separator that immediately
  follows it (the natural separator right after the LAST collapsed item is left alone if it
  is not itself flagged — see the exact loop in `_applyCollapse`). The theme MUST supply:
  ```css
  [data-zk-bc-hidden="true"] { display: none; }
  ```
  ZK's own stock behavior additionally marks this `!important` so an application author's
  own inline `display` on the item survives an expand/collapse round-trip; a theme is free
  to do the same but at minimum must not let any other rule with equal-or-higher specificity
  re-show a `data-zk-bc-hidden="true"` node.
- A synthetic separator (cloned from the first natural separator, so it carries the same
  text-vs-icon shape) and a synthetic `<li class="z-breadcrumb-ellipsis"><button>…</button></li>`
  are **inserted** into the live `<ol>`, each flagged `data-zk-bc-injected="sep"` /
  `="ellipsis"`. These nodes do not exist in the mold output; do not write CSS that assumes
  `.z-breadcrumb-ellipsis` is always present.
- Clicking the ellipsis button (`_onExpand`) removes all injected nodes, clears every
  `data-zk-bc-hidden`, and does **not** re-collapse until the *next full rerender* (a
  `separator`/`maxItems` change, or the server re-rendering the widget) — a child's own
  self-rerender (e.g. `setLabel`) re-applies the *existing* collapse state, it does not
  reset an already-expanded bar back to collapsed.

### Keep-first-keep-last collapse geometry

When collapsing, exactly the **first** item and the **last `maxItems − 1` items** stay
visible; everything strictly between them (and their trailing natural separators) is hidden
and replaced by one ellipsis. A theme must not assume a symmetric "keep N from each end"
shape — it is always 1 kept at the head.

### Keyboard navigation model

`Breadcrumb.ts#_onKeyDown` (bound on the root `<nav>`) intercepts **Left/Right/Home/End**
and moves focus among the *visible* (not `data-zk-bc-hidden`) items' `<a href>` /
`<span tabindex="-1">`, plus the ellipsis `<button>` if present — independent of native Tab
order. This means:

- Every content element needs a visible focus affordance regardless of whether it is
  reached by Tab or by these arrow keys — there is no separate "arrow-key-focused" class,
  it is the same native `:focus`/`:focus-visible` the browser already applies.
- The current/disabled `<span tabindex="-1">` is deliberately **out of the natural Tab
  order** (`tabindex="-1"`) but **is** a valid arrow-key/`.focus()` target, so `:focus-visible`
  styling must work on `span[tabindex="-1"]:focus-visible`, not only on `a:focus-visible`.

## Relational invariants (B-tier)

- All visible (non-`data-zk-bc-hidden`) `.z-breadcrumbitem` and `.z-breadcrumb-separator` /
  `.z-breadcrumb-ellipsis` siblings that occupy the same wrapped line must read as one
  horizontal trail: their vertical centers align within a small tolerance. (The root permits
  `flex-wrap`, so this is a per-line invariant, not a whole-bar invariant.)
- Every visible separator sits strictly between its two neighboring visible items —
  left-neighbor's right edge ≤ separator's left edge ≤ separator's right edge ≤
  right-neighbor's left edge (within tolerance) — never overlapping either.
- A `data-zk-bc-hidden="true"` element must occupy zero layout space (not merely
  `visibility:hidden` or `opacity:0`) — collapse is a **layout** operation, not a fade.

## State-differs invariants (C-tier)

- A navigable item (`<a href>`) must be visually distinguishable from the terminal/disabled
  item (`<span>`) by at least one dimension (color, text-decoration, weight, …) — the whole
  point of a breadcrumb is telling "clickable" apart from "you are here."
- `disabled` must differ from an equivalent enabled item by at least one dimension
  (typically reduced opacity) — ZK already suppresses the click in JS (`doClick_` stops the
  event when `_disabled`), so the visual cue is the *only* signal an interactive user gets
  that the item won't respond.
- `:focus-visible` on any content element (`a`, `span[tabindex="-1"]`, or the ellipsis
  `<button>`) must be visually distinguishable from unfocused — required for the arrow-key
  navigation model above, since a keyboard user has no other way to tell where focus landed.
- `:hover` on a navigable item must differ from resting state by at least one dimension.

## Sibling decomposition

No existing ZK component shares breadcrumb's exact composition (ordered items +
author-facing separator between every pair + client-only middle-collapse-to-ellipsis).
Two components supply partial precedent for individual pieces, not the whole:

- **`a`** (`components/a.md`) — the link-vs-plain-text-vs-disabled affordance on a single
  `.z-breadcrumbitem > a` is the same shape as a standalone link.
- **`paging`** (`components/paging.md`) — the keep-ends/collapse-middle/ellipsis idiom is
  structurally analogous to paging's page-range collapse, though paging's ellipsis is
  static server-rendered text, not a client-injected, focusable `<button>`.

## Bundle

`js/zul/wgt/css/breadcrumb.css` → own `breadcrumb.css.dsp` (not merged into any other
component's bundle — confirmed no other `zul.wgt` component shares a `<css-uri>` pointing at
this file).

## Notes

- `separator` accepts an **icon form** (`"icon:<sclass>"`) in addition to plain text —
  don't assume `.z-breadcrumb-separator`'s content is always a text node.
- The ellipsis is a **real `<button>`**, not a styled `<span>` — it exists specifically so
  keyboard users can Tab to it (well, reach it via arrow keys per the keyboard model above)
  and activate it with Enter/Space; do not replace it with a non-interactive element.
- `aria-current="page"` on the last item is layered on by the EE `za11y` add-on — the CE
  mold emits **no ARIA** of its own beyond the native semantics of `<nav>`/`<ol>`/`<a>`.
