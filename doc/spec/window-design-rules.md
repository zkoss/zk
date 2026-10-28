# Window Design Rules (Marble / MD3)

## Background

ZK's `window` component has two orthogonal attributes that affect its appearance:

- **`mode`** — `embedded` | `overlapped` | `popup` | `modal` | `highlighted`
  Defines spatial behavior: whether the window participates in document flow (`embedded`) or floats above the page (the other four).
- **`border`** — `normal` | `none`
  A visual frame switch — should affect outline/separator chrome only.

A naive theme that ties `box-shadow` to the `border` attribute produces wrong semantics:

| Combo | What user sees | Reality | Mismatch |
|-------|----------------|---------|----------|
| `mode=embedded` + `border=normal` | floats (has shadow) | inline | shadow lies |
| `mode=overlapped` + `border=none` | flat (no shadow) | floating | shadow lies |

## Rule

> **Elevation is determined by `mode`, never by `border`.**

`border` controls outline + header separator only. It MUST NOT add or remove `box-shadow`.

## Elevation mapping

| Mode | Spatial meaning | MD3 analogue | Elevation token |
|------|-----------------|--------------|-----------------|
| `embedded` | inline in layout flow | Surface / Outlined card | **none** (level 0) |
| `overlapped` | draggable floating panel | Floating surface | `--zk-elevation-2` |
| `popup` | transient floating layer | Menu / Popover | `--zk-elevation-2` |
| `highlighted` | emphasized floating window | Elevated card | `--zk-elevation-3` |
| `modal` | blocking dialog (+ scrim) | Dialog | `--zk-elevation-3` |

## Border mapping

| Border value | Outline | Header separator | Shadow |
|--------------|---------|------------------|--------|
| `normal` (default) | `1px solid var(--zk-color-outline-variant)` | yes | unchanged (mode decides) |
| `none` → `.z-window-noborder` | none | none | **unchanged (mode decides)** |

## Legal combinations

All four corners must look semantically correct:

- `embedded + border=normal` → MD3 **Outlined card** (frame, flat, no shadow)
- `embedded + border=none` → bare inline surface (no frame, no shadow)
- `overlapped + border=normal` → framed floating window with elevation-2
- `overlapped + border=none` → MD3 **Elevated card** style (no frame, with elevation-2)
- Same pattern repeats for `popup`, `highlighted`, `modal`

## CSS structure (target)

```css
/* Base — no shadow, no border by default (mode/border classes add them) */
.z-window {
    background-color: var(--zk-color-surface);
    border-radius: var(--zk-shape-corner-extra-small);
    border: 1px solid var(--zk-color-outline-variant);
    /* no box-shadow here */
}

/* Mode-driven elevation */
.z-window-embedded   { box-shadow: none; }
.z-window-overlapped,
.z-window-popup      { box-shadow: var(--zk-elevation-2); }
.z-window-highlighted,
.z-window-modal      { box-shadow: var(--zk-elevation-3); }

/* Border-driven frame only — never touches box-shadow */
.z-window-noborder              { border: none; }
.z-window-noborder .z-window-header { border-bottom: none; }
```

## Chrome-less window content padding

> **A chrome-less window — `.z-window-noborder.z-window-noheader` — MUST render its content flush: `padding: 0` on `.z-window-content`.**

```css
.z-window-noborder.z-window-noheader .z-window-content {
    padding: 0;
}
```

When both the border and header are removed, the window is a bare surface; its
default `.z-window-content` padding (`var(--zk-spacing-4)`) would leave an
unexplained inset. Inner spacing is then the developer's responsibility. Ported
from ZK stock 10.3.0.1 (IceBlue) — the only stock CSS bugfix in that release
that applies to a pure-CSS token theme. (The stock root-level `padding:0` on
`.z-window` is intentionally omitted: Marble's `.z-window` root carries no
padding, so the rule would be dead CSS.)

## Anti-patterns (do not do)

```css
/* WRONG — border attribute removing shadow */
.z-window-noborder { box-shadow: none; }

/* WRONG — embedded mode getting elevation */
.z-window-embedded { box-shadow: var(--zk-elevation-3); }

/* WRONG — base .z-window carrying dialog shadow that mode classes must override */
.z-window { box-shadow: var(--zk-elevation-dialog); }
```

## Reference

- MUI `Dialog` / `Popover` / `Menu` — elevation tied to component role, `variant="outlined"` only affects border.
- MD3 spec: Elevation expresses spatial relationships; it is not a styling toggle.
