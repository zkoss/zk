# A focus ring drawn outward gets clipped

`outline` is painted **outside** the element's border box. It is not clipped by the element's own
`overflow`, but it **is** clipped by any ancestor that clips its overflow. Several ZK components put
a focusable element flush against such an ancestor, so an outward ring is cut — sometimes on all
four sides, sometimes only on the first/last child.

This is a **structural** property of the widget's DOM, not a design preference: it holds for every
theme, so every theme has to solve it.

## When it bites

An outward ring is unsafe whenever **both** hold:

1. The focusable element is **full-bleed** — its box is exactly as wide (or as tall) as the
   container that holds it, leaving no gutter for the ring to occupy.
2. Some ancestor clips overflow, or is a `<table>`/`<tr>` whose cells bound the paint area.

ZK ships several DOM shapes that satisfy both. This is a list of shapes **at risk** — whether a given
theme has actually tripped over one depends on whether it styles focus there at all:

| Widget | Focusable element | The shape |
|--------|-------------------|-----------|
| navbar / nav | `a.z-nav-content`, `a.z-navitem-content` | The link is exactly the width of the list that holds it. In a nav group it sits inside the submenu cave `<ul>` — a container a theme normally has to clip, because collapsing the group animates the cave's height. At top level the ring instead spills past the navbar's own surface; inside the horizontal-mode overflow popup, past the popup's clip. |
| listbox | `tr.z-listitem` | A `<tr>` has no paint area of its own — a ring on it is cut at the cell boundaries. Applies to any row-as-`<tr>` widget. |
| tree | `tr.z-treerow` | Same `<tr>` problem, and the row is full-bleed inside the scrolling body. |
| grid | `tr.z-row` | Same `<tr>` problem. |
| menu / menupopup | `a.z-menu-content`, `a.z-menuitem-content` | A popup surface is normally clipped so its rounded corners stay rounded, and the item is full-bleed inside it. |

## The two mechanisms that work

**A. Inset outline — `outline-offset: <negative>`.** Keeps the whole ring inside the element's own
border box, so no ancestor can reach it. The border-radius shrinks with it, so a rounded item still
gets a rounded ring. This is the default choice for a full-bleed row or nav link.

```css
.z-thing:focus-visible {
    outline: <theme focus ring>;
    outline-offset: -2px;      /* negative — never a positive offset here */
}
```

**B. Inset `box-shadow` on a child that does have a paint area.** For `<tr>`-based rows an outline
has nowhere to live at all; put an inset shadow on the first cell instead, which paints inside that
cell's own box.

```css
.z-thing:focus-visible .z-thing-cell:first-child { box-shadow: inset <edge marker>; }
```

Both mechanisms also avoid the layout-shift trap — see `focus-affordance-no-layout-shift.md`, which
covers the related case of a focus affordance that changes the element's size.

## How to catch it

Measuring `outline-color` alone passes on a ring nobody can see. Assert **geometry**: inflate the
element's rect by `outline-offset + outline-width`, then walk the ancestors and fail if any ancestor
that clips overflow has a rect smaller than the inflated ring on any side. That is a single
`page.evaluate` and it catches the defect regardless of which ancestor is responsible.

Also assert `outline-style` explicitly. A component with **no** `:focus-visible` rule at all does not
look unstyled in a screenshot — the browser draws its own ring (Chrome: `outline-style: auto`, 1px,
its own blue, offset `+1px`). It is off-palette *and* outward, so a missing rule and a wrong rule
present as the same bug. `outline-style: solid` distinguishes a theme ring from the UA fallback.

Keyboard focus also has to be *reachable* to be observed. A mouse click gives `:focus` but not
`:focus-visible`; press a key afterwards (or arrive by keyboard) before measuring.

## Scanning for it across a whole theme

Reachability is what stops the per-component check from generalising: scripted Tab-walking does not
reach every widget's focus (a navbar under `za11y` takes one Tab for the whole component and moves
with arrow keys), and a click-then-keypress has to be written per widget.

The way around it is to stop driving input and **force the pseudo-class** through the DevTools
protocol, which every Chromium automation tool can reach:

```js
const cdp = await context.newCDPSession(page);
await cdp.send('DOM.enable');
await cdp.send('CSS.enable');
const { root } = await cdp.send('DOM.getDocument', { depth: -1 });
const { nodeId } = await cdp.send('DOM.querySelector', { nodeId: root.nodeId, selector });
await cdp.send('CSS.forcePseudoState', { nodeId, forcedPseudoClasses: ['focus-visible'] });
// …every computed value read now is the real focused one…
await cdp.send('CSS.forcePseudoState', { nodeId, forcedPseudoClasses: [] });
```

Pair it with the population the theme itself defines: walk `document.styleSheets`, collect every
selector carrying a `:focus-visible` rule that draws an outline, strip the pseudo-class, and that
selector list *is* the set of elements to check. Two traps in that walk:

- A plain `CSSStyleRule` also exposes `.cssRules` in current Chrome (CSS Nesting), so "has
  `cssRules`" does **not** mean "is a grouping at-rule". Test the rule for a `selectorText` and
  recurse independently, or a theme wrapped in `@layer` yields zero rules.
- `@import`ed sheets hang off `.styleSheet`, not `.cssRules`.

When judging whether a ring is *visible* rather than merely present — a focus colour and a selection
fill can collapse to the same system colour under `forced-colors` — compare the ring against what is
painted **where the ring lands**, not against the element's own background: an inset ring covers the
element's fill (or a child's, since a selected table row fills its cells and not the `<tr>`), while
an outset ring covers whatever is outside the element. Hit-testing the middle of the ring band with
`document.elementFromPoint` answers this without having to reason about it; the outline itself is
not hit-tested, so the call returns exactly what the ring is drawn on top of.

## Reading a token's real value before you compare it

Two habits that silently produce wrong contrast figures in a theme built on
relative colours and alpha. Both measured in Chromium.

**Contrast → sample a rendered pixel. Hue → read the `oklch()` serialization.**
`getComputedStyle` resolves `oklch(from var(--x) …)` to *absolute*
`oklch(L C H)`, **not** to `rgb()`. So a checker that regex-parses `.color` as
RGB measures the oklch numbers as if they were channels — it does not throw, it
just reports nonsense. Sampling the painted pixel avoids that and is the right
reading for contrast, because 8-bit sRGB is what the user actually sees. But the
sRGB round-trip perturbs *hue*, and the error scales inversely with chroma:

| Colour | Chroma | Hue from `oklch()` | Hue after sRGB round-trip | Error |
|---|---|---|---|---|
| a mid-chroma blue | 0.066 | 260.56° | 260.25° | **0.31°** |
| a near-neutral slate | 0.015 | 248.60° | 244.43° | **4.17°** |

Lightness survives either way (0.5389 / 0.5407 against a declared 0.54), so
contrast is safe from a pixel. Anything hue-based — "did the derived container
keep the seed's hue?" — must read the serialization, or a desaturated brand
preset raises a false alarm.

**A colour with alpha must be composited over the element's own background, not
over white.** Tokens like `--zk-color-on-surface: rgba(0, 0, 0, 0.87)` are
translucent; compositing over an assumed white page understates contrast on any
tinted surface. Read the actual backdrop (hit-test it if a child paints the
fill) and composite over that.
