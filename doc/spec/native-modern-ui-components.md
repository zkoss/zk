# Native modern UI components — badge / chip / avatar (ZK 10.4+)

## Status
Adopted with the **ZK 10.4.0.FL.20260713-Eval** upgrade (base of ZK 11.0). Replaces the
theme's former hand-rolled `.z-badge` / `.z-chip` / `.z-avatar` **utility CSS**, which was
deleted (`zul/css/base/_badges.css`, `_chips.css`, `_avatars.css`).

## What changed
ZK 10.4 ships four native "modern UI components" (ZK-6097), registered in ZK core
`metainfo/zk/lang.xml`: **`avatar`, `badge`, `chip`, `avatargroup`**. (There is **no `tag`
component** — a "tag pill" is `chip`.) The theme now styles the native widgets instead of
utility classes.

- Theme CSS lives at `zul/src/main/resources/web/js/zul/wgt/css/{badge,chip,avatar,avatargroup}.css`
  (package `zul.wgt`; auto-scanned 1:1 → `<name>.css.dsp`). Each is self-contained, token-only,
  wrapped in `@layer zk-components`. Registered as `CSS_URI_BACKED` in `scripts/build-css.js`.
- No `lang-addon.xml` edits: ZK core owns the component→css-uri wiring; `MarbleThemeProvider`
  rewrites `~./js/zul/wgt/css/<c>.css.dsp` → `~./marble/…`.

## Native class / attribute contract
- **badge**: `<span class="z-badge z-badge-{severity} [z-badge-{placement}|z-badge-standalone]
  [z-badge-dot]">…<span class="z-badge-indicator">n</span></span>`. Attrs: `count`, `value`,
  `max` (default 99), `showZero`, `dot`, `severity` (info default), `placement` (top_right default).
- **chip**: `<span class="z-chip z-chip-{severity} [z-chip-small] [z-chip-rounded]
  [z-chip-closable] [z-chip-disabled]">…<button class="z-chip-close">…</button>?</span>`. Attrs:
  `label`/`image` (LabelImageWidget), `severity` (info default), `size`, `rounded`, `closable`,
  `disabled`. Per-instance colour via `style="--zk-chip-bg/--zk-chip-border/--zk-chip-color"`.
- **avatar**: `<span class="z-avatar z-avatar-{shape} z-avatar-{size}">img|icon|<span
  class="z-avatar-text">AB</span></span>`. Attrs: `image`, `label`, `shape` (circle default),
  `size` (medium default), `gap`. Size via `--zk-avatar-size` / `--zk-avatar-font-size` hooks.
- **avatargroup**: overlapping stack; `size`/`shape` cascade to children; `--zk-avatargroup-overlap`;
  "+N" overflow = `.z-avatargroup-overflow`.

## Severities: fixed native set vs. old utility variants
Native severities are **info · success · warning · error · neutral** (renamed from danger/secondary by ZK-6132). The old utility palette
had extra names; migration mapping (applied to all ZUL usages):

| old utility variant | native severity |
|---|---|
| `z-chip-info` / `z-badge-info` | `info` |
| `z-chip-success` / `z-badge-success` | `success` |
| `z-chip-warning` / `z-badge-warning` | `warning` |
| `z-chip-error` / `z-badge-error` | **`error`** |
| `z-chip-neutral` | **`neutral`** |
| `z-chip-primary` | **`info`** |
| `z-chip-on-primary` | **dropped** (no equivalent) |

## Preview
Dedicated pages: `badge.zul`, `chip.zul`, `avatar.zul` (component browser → **Data Display**).
Showcase: `utility/surface.zul`. Migrated real usages: `usecase/ticket-inbox`,
`inventory-table`, `ops-dashboard`, `item-editor`.

## Naming: `severity` (`danger`/`secondary`) — proposed ZK API change (adopted upstream as `error`/`neutral`, ZK-6132)

The native `severity` values `danger` and `secondary` are Bootstrap-lineage palette words that
are inconsistent with ZK's own vocabulary (`notification`/`messagebox` already use `error`) and
with the cross-framework norm. A standing proposal recommends renaming the *component API*
(not the theme) to `error` / `neutral` while ZK 11 is still in development. Full rationale,
framework survey, ZK-internal evidence, and migration notes:
`native-severity-naming-proposal.md` (retired with the template).

Until/unless ZK changes the enum, the mapping table above is the source of truth and the
per-theme intent→palette bridge in `chip.css`/`badge.css` is intentional, not a defect.

## Follow-up (out of scope here)
The 10.4 upgrade also introduced three more native components — **breadcrumb, carousel,
confirmpopup**. **confirmpopup** is now themed (source `js/zul/wgt/css/confirmpopup.css`;
contract `doc/contracts/confirmpopup.md`; preview `confirmpopup.zul`; severity reuses the same
info/success/warning/danger/secondary mapping as badge/chip, icon-only recolor). **breadcrumb**
and **carousel** remain **empty-stubbed** in `build-css.js` (render with ZK default styling) and
still need real Marble theming.

The same upgrade also brought the zkmax **daterangebox** (date-range picker) live; it is likewise
now themed (`js/zkmax/db/css/daterangebox.css`; contract `doc/contracts/daterangebox.md`; preview
`daterangebox.zul`).
