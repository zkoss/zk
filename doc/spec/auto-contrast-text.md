# Auto-contrast text color

**Question this answers:** When a dark background is set (e.g.
`<label value="Admin" sclass="z-chip z-chip-primary"/>`), the text can end up dark → insufficient
contrast (fails WCAG). Is there a CSS mechanism that makes the text automatically pick light-vs-dark
based on the background, instead of hand-editing the text color? And does Marble already have it?

Short answer: a truly-automatic native function exists but isn't cross-browser yet; a deployable
trick exists and fits Marble's idiom; **Marble does not implement general auto-contrast today** — it
guarantees legibility a different way (token pairing + a one-directional fill cap). Details below.

---

## Part A — The CSS techniques

Four approaches exist; only two are usable in production today.

### 1. Native `contrast-color()` — the "proper" answer, not yet cross-browser
CSS Color Module 5. `color: contrast-color(var(--bg))` asks the browser to return black or white,
whichever contrasts better with `--bg`. This is what MUI / Ant compute in JS at build time.
- **Status (2026-01): Safari only.** Not in Chrome/Firefox stable → cannot be relied on
  cross-browser, even under Marble's "last 2 versions" policy.
- Its earlier draft name `color-contrast()` (pick-best-from-a-list) was dropped/renamed — don't use it.

### 2. OKLCH lightness-threshold trick — deployable now, matches Marble's idiom
Pure CSS relative-color syntax. Read the background's OKLCH lightness `l`, emit pure black or white:

```css
.auto-contrast {
  /* white text when bg is dark (l < threshold), black text when bg is light */
  --contrast-threshold: 0.6;
  color: oklch(from var(--bg) clamp(0, (var(--contrast-threshold) - l) * infinity, 1) 0 0);
}
```

`clamp(0, (threshold - l) * infinity, 1)` collapses to lightness **1** (white) when `l < threshold`
and **0** (black) when `l > threshold`; chroma/hue `0 0` make it grayscale.
- **Browser support = Marble's baseline exactly**: Chrome 119+, Safari 16.4+, Firefox 128+.
- The background must live in a CSS variable (`--bg`) so `oklch(from …)` can read it.
- Limitations: a hard black/white step (not WCAG-optimal per hue), and it doesn't handle
  translucent backgrounds.

### 3. MD3 paired `on-*` tokens — the design-system way (what Marble does)
Rather than compute contrast at runtime, every background ships with a matching foreground:
`--zk-color-primary` ↔ `--zk-color-on-primary`. The author always applies the pair. Deterministic,
WCAG-tunable per token. See Part B.

### 4. `light-dark()` — not relevant
Common confusion: `light-dark(a, b)` switches on the page's *color-scheme* (light/dark mode), not on
a background's lightness. It does not solve auto-contrast.

---

## Part B — What Marble actually has

**Verdict: Marble does not implement automatic (bidirectional) light-vs-dark text selection.** It is
a consciously deferred gap. What it has instead:

1. **Extensive `oklch(from …)` derivation** — `tokens/_colors.css` derives containers,
   on-containers, inverse-primary, and `-fill` tokens from a seed (e.g.
   `--zk-color-primary-container: oklch(from var(--zk-color-primary) 0.92 calc(c*0.25) h)`). So the
   *syntax* in technique #2 is already used heavily here.

2. **A one-directional lightness cap on solid fills** — `tokens/_colors.css`:
   `--zk-color-primary-fill: oklch(from var(--zk-color-primary) min(l, 0.54) c h)` and siblings.
   `min(l, 0.54)` darkens a fill only when it is too light, so **white text always clears WCAG AA
   (≥ 4.5:1) on any brand hue**. This is *partial* adaptation — it always keeps white text; it never
   flips to dark text on a light background. Not general auto-contrast.

3. **MD3 `on-*` token pairing** — full set in `tokens/_colors.css` (on-primary, on-secondary,
   on-error, on-warning, on-success, on-surface, on-status, …). Solid-fill foregrounds are hardcoded
   `#ffffff`; only the `*-on-*-container` variants are `oklch(from …)`-derived tone pairs. None is
   contrast-computed.

4. **Chips already pair bg + fg** — `base/_chips.css`:
   `.z-chip.z-chip-primary { background:#4782da; color:#fff; }` (and the success/warning/error/
   neutral/info variants each pair a tinted bg with a dark same-hue text). Two-class selectors beat
   `.z-label` specificity so the chip color wins when applied to a `<label>`.
   → On a current build, `<label sclass="z-chip z-chip-primary"/>` renders **white on blue, not
   black**. If black text appears, that contradicts the shipped CSS → suspect a stale build, an
   older version, or a custom background override, not the theme default.

### Documented as a gap
Both `doc/spec/brand-override.md` (the `-fill` cap section) and an inline comment in
`tokens/_colors.css` state: *"Full automatic foreground selection (MUI/Ant style) needs
`contrast-color()`, not yet baseline — see GAP 2"* (see [`design-decisions.md`](design-decisions.md)). Marble
deferred true auto-contrast until `contrast-color()` ships broadly.

---

## If you want it now

For the built-in variants you don't need anything — they already pair text with background. For
**arbitrary custom backgrounds**, add a `z-auto-contrast-text` utility using technique #2 (drives
`color` from a `--bg` variable). It fits the existing `oklch(from …)` usage and Marble's browser
baseline. Not added yet — see [brand-override.md](brand-override.md) and GAP 2 in
[design-decisions.md](design-decisions.md).
