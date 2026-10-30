# MD3 Close-Affordance Placement

Where Material Design places close / dismiss / clear icons across components, and the
rules Marble derives from it. Raised 2026-06-05 while reviewing the goldenlayout
per-tab close icon (see the goldenlayout contract, M22).

## The two conventions

MD3 has no single "close icon" rule — placement follows the component's shape:

### 1. Inline components → trailing edge, vertically centered

Single-line, content-bearing elements put the dismiss affordance **after the content
in reading order**, vertically centered, with a standard inset (never flush):

| Component | Affordance | Placement |
|-----------|------------|-----------|
| Input chip | trailing remove icon | trailing edge, 8dp inset |
| Text field / Search bar | clear icon | trailing icon slot |
| Snackbar | optional dismiss × | far trailing, after the action button |
| Dismissible tab (no MD3 spec; Chrome/MUI convention) | close × | trailing, after the label |

### 2. Container surfaces → top corner of the header

Components with a header region put the close affordance in a **top corner**:

| Component | Affordance | Placement |
|-----------|------------|-----------|
| Side sheet | close icon button | top, trailing end of the header |
| Full-screen dialog | close affordance × | top **leading** (exception — see below) |
| Basic dialog | none by default | dismissal via action buttons (bottom-trailing) or scrim |
| Expansion panel / accordion header | **none** (MD3) — Marble deviates for closable headers | MD3 expansion panels are never dismissible inline; Marble's closable accordion header shows a 32px (24px compact) × instead of the chevron (owner ruling 2026-10-25, supersedes the 2026-06-07 ruling), because WCAG 2.5.8 needs a pointer close target |

**The full-screen dialog exception**: its × is top-*leading* because it doubles as
back/cancel navigation and must sit at the *opposite* end from the confirming action
(e.g. Save, top-trailing) so the two cannot be mis-tapped. Rule of thumb: when a
close affordance coexists with a primary action in the same bar, they occupy
opposite ends.

## Why the periphery

- **Reading order**: content first, exit last — the dismiss action never interrupts
  the content scanning path.
- **Predictability**: corners/edges are where users habitually look for exits
  (platform-wide convention).
- **Action separation**: edge placement keeps a destructive/exit affordance spatially
  distinct from the component's primary content and actions.

## Composition with icon-button anatomy

Placement and anatomy are independent rules that compose:

1. **Placement** (this doc): the close *button* sits at the trailing edge / top corner,
   with a standard inset.
2. **Anatomy** (MD3 icon button): the close *glyph* is optically centered — concentric
   with its circular state layer — wherever that button sits. (This is what
   goldenlayout contract M22 asserts; the state-layer circle and the glyph share a
   center.)

## How Marble components map

| Marble element | Pattern | Conforms |
|----------------|---------|----------|
| GoldenLayout per-tab × (`.lm_close_tab`) | inline — trailing after label; closable geometry **16/4/8** (leading inset / label–icon gap / trailing inset, contract close-7) | ✓ |
| GoldenLayout stack controls (`.lm_controls .lm_close`) | surface — right-anchored ≤ 8px from header edge (contract M9) | ✓ |
| Tabbox `.z-tab-button` | inline — trailing after label via flex `order: 1` (ZK emits the button *before* `.z-tab-text`); strip geometry **16/4/8** with a 24×24 hit area (WCAG 2.5.8; this rule lives in za11y `zul/css/_tab-a11y.css` and applies only when za11y is active); vertical orients hug the bar's trailing edge (`margin-left: auto`); accordion: closable header shows a 32×32 (compact 24×24) × **instead of the chevron**, centred on the chevron keyline (owner ruling 2026-10-25, supersedes user ruling 2026-06-07 `display: none`, contract c11a) | ✓ — corrected 2026-06-05 (contract c31–c35 + M1; was leading-side); accordion re-ruled 2026-06-07, then 2026-10-25 |
| Window/Panel close tool | surface — trailing end of caption bar | ✓ |
| Chip-like custom sclasses | inline — trailing remove icon, 8dp inset | use as guide |

### Derived rule for closable inline elements

When a dismiss icon trails the content, the trailing inset shrinks to roughly **half
the text inset** (here: `spacing-2` = 8px vs `spacing-4` = 16px). Implement with
`:has(> .close-element)` so non-closable instances keep symmetric padding — for GL
this discriminates exactly, since the library removes the close node when
`closable="false"`.
