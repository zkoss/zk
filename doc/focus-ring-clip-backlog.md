# Clipped focus rings — backlog

Produced by `npm run test:focus-scan` (`src/test/playwright/focus-ring-scan.spec.ts`)
on 2026-09-06, the first run of the scan built for decision **D13**. The machine-
readable list the scan checks itself against is `doc/focus-ring-known-clips.json`.

## What the scan does

For every preview page it reads the loaded stylesheets, collects every selector
that has a `:focus-visible` rule drawing an `outline`, forces `:focus-visible` on
each matching element through CDP (`CSS.forcePseudoState`) so the ring is really
applied, then inflates the element's rect by `outline-offset + outline-width` and
walks its ancestors. Any ancestor that clips overflow and is smaller than the
inflated ring on any side is a finding.

Forcing the pseudo-class rather than driving real keyboard input is what makes
this generalise: reaching a given widget's focus by scripted Tab or click is the
part that does not (navbar, for one, has a roving tabindex — Tab enters the whole
widget once and arrow keys move inside).

A second pass repeats the measurement under forced-colors for items that are
**selected as well as focused**, and compares the ring's colour against whatever
is actually painted where the ring lands (hit-tested, because an inset ring
covers the element's own fill while an outset one covers what is outside it).

## Why these matter

A ring drawn outside a box that has no room is not "slightly cropped" — on a
flush edge it is **entirely** invisible. Measured on `hlayout.zul`: the button's
box is 187→223 and the hlayout's box is *also* 187→223, so all 4px of ring above
and below the button falls outside the clip. A keyboard user cannot see which
button they are on.

## Findings — 13 distinct (element ⊂ clipper) pairs, 77 occurrences, 11 pages

Grouped by root cause. All of them are an **outward** ring (`outline-offset: 2px`)
against a container that clips.

### 1. `.z-button` inside a layout container that hides overflow — 8 pairs

| Clipper | Pages | Overshoot | Why the container clips |
|---|---|---|---|
| `.z-hlayout` (+ `-valign-*` variants) | hlayout, organigram, space, biglistbox | 4px | `white-space: nowrap` + `overflow: hidden` in `box/css/layout.css`, so a too-wide row is cut instead of spilling |
| `.z-lineitem` | linelayout | 4px | `linelayout.css` |
| `.z-caption-content` | caption | 4px | Paired with `text-overflow: ellipsis` — **cannot** simply be dropped |
| `.z-carousel` | carousel | 679.91px | The rounded media frame; **cannot** be dropped |

This is the widest-reaching group: a button inside an hlayout is completely
ordinary usage, and `.z-button` is the theme's most-used component.

### 2. `.z-toolbarbutton` inside a toolbar — 1 pair (the `.z-toolbar-tabs` clip was fixed by the toolbar-tabs min-height change, 2026-10-20)

| Clipper | Pages | Overshoot |
|---|---|---|
| `.z-toolbar-popup.z-toolbar-popup-close` | toolbar | 3px |

`.z-toolbar`'s `overflow: hidden` is load-bearing for the overflow-popup
mechanism.

### 3. `.z-button` / `.z-a` inside a preview page's own scrolling row — 2 pairs

| Clipper | Pages | Overshoot |
|---|---|---|
| `DIV.z-mb-6.z-d-grid.z-grid-cols-auto.z-overflow-x-auto…` | button, a | 4px / 1px |

Not theme chrome: this is the state-matrix wrapper the preview pages build from
utility classes. Worth keeping in the list anyway, because it is exactly what an
application does when it puts controls in a horizontally scrolling row —
`overflow-x: auto` makes the **vertical** axis compute to `auto` too, so the row
clips top and bottom as well.

## The fix is not obvious — it needs a decision

The mechanical fix used for navbar (`outline-offset: -2px`, draw the ring inside
the element) does not transfer to `.z-button`:

- A navbar item is full-bleed and tonal, so an inset ring reads fine on it.
- A **filled** button is `--zk-color-primary`, and `--zk-focus-ring` is *also*
  `--zk-color-primary`. An inset ring on a filled button is primary-on-primary —
  invisible. It would trade a clipped ring for an absent one.

The plausible directions, none free:

1. **Drop `overflow: hidden` where it is not load-bearing.** Only `.z-hlayout`
   and `.z-lineitem` are candidates (caption needs it for the ellipsis, carousel
   for the frame, toolbar for the overflow popup). Clears 6 of the 13 pairs but
   changes core layout behaviour, and ZK measures these boxes in JS.
   *Superseded by A' below — the clipping does not have to be dropped at all.*
2. **Two-tone focus indicator on buttons** — an inset ring in the on-primary
   colour plus the existing outer ring, which is what WCAG 2.2 Focus Appearance
   expects for exactly this situation. Correct, and the most work.
   *Not a Material pattern — see "What MD3 actually says" below.*
3. **Per-context override** (`.z-hlayout .z-button:focus-visible { … }`) — a
   growing list of container/component pairs; fixes the symptom per site and
   never converges.

## What MD3 actually says (researched 2026-09-06)

Sources: `material-components/material-web` — `tokens/_md-comp-focus-ring.scss`,
`focus/internal/_focus-ring.scss`, `docs/components/focus-ring.md`,
`button/internal/_shared.scss`, and a `gh search code` sweep for `inward`.

### 1. The `md.comp.focus-ring` token values

| Token | MD3 default | Marble today |
|---|---|---|
| `width` | **3px** | 2px |
| `outward-offset` | **2px** | 2px |
| `inward-offset` | **0px** | -2px (navbar) |
| `color` | **`md.sys.color.secondary`** | `--zk-color-primary` |
| `shape` | follows the component's corner | follows the component's corner |

### 2. An inward ring is a first-class MD3 mode, not a workaround

`md-focus-ring` ships an `inward` attribute. Outward draws `outline` with
`inset: calc(-1 * outward-offset)`; inward draws a `border` with
`inset: inward-offset`, i.e. **inside** the element's own box. The components
that set it are exactly the shapes this backlog is about:

`tabs/tab`, `menu/menuitem`, `list/listitem`, `select/selectoption`,
`labs/navigationtab` — every one a full-bleed row inside a container that clips.

Their CHANGELOG records the migration: *"inward focus rings must be specified
with `inward` rather than a negative offset."* In a custom-element library that
means an attribute; **in a CSS-only theme, negative `outline-offset` is the
implementation of that same mode.** The navbar fix (commit e23576eb) is
therefore MD3-canonical, not a hack, and the same treatment is right for
listitem / treerow / menuitem / tab whenever their turn comes.

### 3. MD3 does **not** use an inward ring on buttons

`button/internal/_shared.scss` overrides only the ring's four `shape-*` tokens.
Offset and colour are left at the defaults, so an MD3 button always has an
outward ring. MD3 has no button-side answer to clipping because a Material
layout never hugs a button with `overflow: hidden` — **the clipping is ZK's DOM,
not Material's, so the MD3-faithful fix is on the container.**

(For contrast: MUI's own answer is worse, not better. `Button.css`
`.MuiButton-contained.Mui-focusVisible` sets an elevation-6 `box-shadow` and
nothing else — a shadow is not a focus indicator under WCAG 2.2. Do not follow
MUI here.)

### 4. Why "just adopt MD3's secondary colour" does not rescue the inward ring

The dead-end above was primary-on-primary. Switching to MD3's default ring
colour looks like it dissolves the problem. It does not — but for a different
reason than a first pass suggests.

**Palette note.** These figures are measured against `_colors.css` as of commit
`1a1731b3` ("derive secondary from primary"), where `--zk-color-secondary` is
`oklch(from var(--zk-color-primary) 0.54 calc(c * 0.41) h)`. It used to be a
hand-picked teal `#4db6ac`; an earlier draft of this section was measured
against that and reached the right conclusion for the wrong reason. Colour
tokens are moving right now, so re-measure rather than quoting these numbers.
Values resolved by pixel-sampling a rendered swatch. Chromium *does* resolve a
relative `oklch(from …)` in `getComputedStyle`, but to absolute
`oklch(L C H)` — not to `rgb()` — so a checker that parses that string as RGB
silently measures the oklch numbers instead (measured: it yields "1.00:1" and
"8.34:1" for the two rows above). See the skill's `focus-ring-clipping.md` for
which of the two readings to use.

| Ring colour | On | Contrast | WCAG 2.2 SC 2.4.13 (needs 3:1) |
|---|---|---|---|
| `--zk-color-secondary` `#586f95` (MD3 default role) | primary fill `#326acb` | **1.02:1** | fails |
| `--zk-color-primary` `#376fd0` (today) | primary fill `#326acb` | 1.07:1 | fails |
| `--zk-color-secondary` `#586f95` | surface `#ffffff` | 5.09:1 | passes |
| `--zk-color-primary` `#376fd0` (today) | surface `#ffffff` | 4.83:1 | passes |
| `--zk-color-on-primary` `#ffffff` | primary fill `#326acb` | 5.17:1 | passes |

Read the first two rows together: since `1a1731b3`, `secondary` is *derived from
primary and keeps its hue*, so **secondary-on-a-primary-fill (1.02:1) is even
worse than the primary-on-primary it would replace (1.07:1)**. Adopting MD3's
default ring colour therefore does not unlock an inward ring on a filled button
— it is not a "secondary is too light" problem that a darker seed would fix, it
is that the two roles now share a hue by design.

Two consequences:

- **Keep `--zk-focus-ring` on `primary`.** Moving it to `secondary` buys no
  accessibility (both pass on a surface, 4.83 vs 5.09) and no new capability,
  in exchange for a theme-wide visual change. Only the `width` 2px → 3px is
  worth aligning to MD3. Note this also retires an objection an earlier draft
  raised: a secondary-coloured ring would *not* be stranded off-brand, because
  secondary now follows `--zk-color-primary` through `oklch(from …)`.
- **If an inward ring on a filled container is ever genuinely needed**, the only
  colour that works is the container's own `on-*` role — `on-primary` white at
  5.17:1. That is MD3's role-pairing rule, and it is the same move
  `_forced-colors.css` already makes for navbar (`Highlight` → `HighlightText`).
  It is not, however, what MD3 does to buttons; see §3.

## Option A' — clip one axis, or clip with a margin (measured, recommended)

`overflow: hidden` is why the ring dies, but the *intent* on `.z-hlayout` is only
"a too-wide row must not spill sideways" (it is paired with `white-space:
nowrap`). Two facts, both measured in a browser rather than read off a spec:

- `overflow-x: hidden; overflow-y: visible` **cannot express** "clip sideways
  only" — the `visible` axis computes to `auto`, which is why these rows clip
  top and bottom as well.
- `overflow: clip` **can**. `overflow-x: clip; overflow-y: visible` stays
  `clip` / `visible`, and `overflow-clip-margin` widens the paint clip rect
  without touching layout geometry at all.

Pixel-sampled probe (magenta 3px ring, `outline-offset: 2px`, sampling 4px
outside the button's border box):

| Container | ring above/below | ring left/right | runaway content |
|---|---|---|---|
| `overflow: hidden` (today) | **invisible** | **invisible** | clipped |
| `overflow-x: clip; overflow-y: visible` | visible | invisible | clipped sideways |
| `overflow: clip; overflow-clip-margin: 5px` | **visible** | **visible** | clipped beyond 5px |

So the whole of groups 1 and 2 — 10 of the 13 pairs — is a one-line change per
container, with the button untouched and MD3-correct:

```css
.z-hlayout { overflow: clip; overflow-clip-margin: 5px; }   /* was overflow: hidden */
```

Also measured, so the load-bearing objections can be retired or confirmed:

- **`text-overflow: ellipsis` still works** under `overflow: clip`, with or
  without a clip margin (identical truncation to `hidden` in a screenshot). So
  `.z-caption-content` is **not** blocked.
- **`overflow: clip` does not establish a BFC.** A float is contained under
  `hidden` (height 40px) and not under `clip` (height 0px). This is the one real
  risk and the only thing that needs verifying against ZK's JS measurement
  before adopting A'. `hidden` also makes the box programmatically scrollable
  and `clip` does not — check nothing calls `scrollTop`/`scrollLeft` on these.
- Browser support (`overflow: clip` + `overflow-clip-margin`) is Chrome 90 /
  Firefox 102 / Safari 16, inside this theme's "modern browsers only" target.

`.z-carousel`'s 679.91px overshoot is a different animal — that size means the
button is on a slide translated out of view, not a missing 5px gutter. Triage it
separately before assuming A' applies.

Recorded as an open decision (**D14**); nothing in this group has been changed.

## Status

| Group | Pairs | State |
|---|---|---|
| navbar submenu item | — | **Fixed** (commit e23576eb) |
| navbar selected + focused under forced-colors | — | **Fixed** (commit e23576eb) |
| 1. `.z-button` in a clipping layout container | 8 | Open — needs the decision above |
| 2. `.z-toolbarbutton` in a toolbar | 2 | Open — same decision |
| 3. preview-page scrolling rows | 2 | Open — may be a preview-page fix rather than a theme one |
| `.z-a` in a preview scrolling row | 1 | Open |

## Checked and NOT defects

- **tree row, organigram node, paging button** — selected + focused under
  forced-colors. All three draw a ring that contrasts with what it is actually
  painted on. Paging in particular *looks* like a collision if you compare the
  ring against the selected button's own Highlight fill, but its ring is
  **outset**, so it is painted on the paging bar behind the button, not on the
  fill. The scan hit-tests the ring band for this reason.
- **accordion tab (`.z-tab`) and listbox row (`.z-listitem`)** — not this defect,
  but worth noting: their focus affordance is a `::before` state-layer opacity
  and an inset `box-shadow` respectively, and forced-colors drops both. Their
  focus is invisible in high-contrast whether or not they are selected. Separate
  gap, not tracked here.
- **anchornav** — its link is intrinsically sized and no ancestor clips it.

## Keeping the list honest

The scan fails on a finding that is **not** in `focus-ring-known-clips.json` (a
new regression) *and* on a baseline entry that no longer reproduces ("fixed —
delete this line"), so the file cannot rot into a waiver list. After a fix:

```bash
FOCUS_SCAN_UPDATE=1 npm run test:focus-scan
```

**Why this baseline is a better instrument than a PNG right now.** The scan
asserts *geometry* — element rect vs clipping-ancestor rect — so it is immune to
palette churn. Its only colour comparison is the forced-colors pass, and there
the palette is replaced by system colours (`Highlight` / `HighlightText`), so the
theme's own tokens are out of play there too. That matters while the colour work
is in flight: the July→September palette moves shifted every tinted surface by
1–2 units per channel, enough to move >1% of the pixels on tree/toolbar/tabbox/
listbox and to muddy any before/after PNG, while leaving this list untouched.
(Related trap when reading PNG history: `git log -1 -- <png>` reports the
*rename* commit, not the capture — `478a6c99` flattened the tree, so every
gallery baseline claimed 2026-07-15 while its pixels dated from `4b96ac98`,
2026-07-01. Use `git log --follow`.)
