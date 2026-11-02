# Native modern UI components — badge / chip / avatar (since ZK 11.0.0)

## Status
Adopted with the **ZK 10.4.0.FL.20260713-Eval** upgrade (base of ZK 11.0). Replaces the
theme's former hand-rolled `.z-badge` / `.z-chip` / `.z-avatar` **utility CSS**, which was
deleted (`zul/css/base/_badges.css`, `_chips.css`, `_avatars.css`).

## What changed
ZK 11.0.0 ships four native "modern UI components" (ZK-6097), registered in ZK core
`metainfo/zk/lang.xml`: **`avatar`, `badge`, `chip`, `avatargroup`**. (There is **no `tag`
component** — a "tag pill" is `chip`.) The theme now styles the native widgets instead of
utility classes.

- Theme CSS lives at `zul/src/main/resources/web/js/zul/wgt/css/{badge,chip,avatar,avatargroup}.css`
  (package `zul.wgt`; auto-scanned 1:1 → `<name>.css.dsp`). Each is self-contained, token-only,
  wrapped in `@layer zk-components`. Registered as `CSS_URI_BACKED` in `scripts/build-css.js`.
- No `lang-addon.xml` edits: ZK core owns the component→css-uri wiring; `StandardThemeProvider.beforeWidgetCSS`
  rewrites `~./js/zul/wgt/css/<c>.css.dsp` → `~./marble/…`.

## Native class / attribute contract
The DOM classes and attributes come from the native components: `Badge.java`, `Chip.java`, `Avatar.java`,
`Avatargroup.java` and their widget TS under `zul/src/main/resources/web/js/zul/wgt/`. Read those files for the classes and attributes.

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

The per-theme intent→palette bridge in `chip.css`/`badge.css` is intentional, not a defect: the mapping table above is the source of truth.

## Other native components (since 11.0.0)
ZK 11.0.0 also introduced three more native components — **breadcrumb, carousel,
confirmpopup**. All three have Marble CSS: `js/zul/wgt/css/{breadcrumb,carousel,confirmpopup}.css`.
confirmpopup also has a contract (`doc/contracts/confirmpopup.md`) and preview (`confirmpopup.zul`); its severity reuses
the same info/success/warning/error/neutral mapping as badge/chip, icon-only recolor.

The same upgrade also brought the zkmax **daterangebox** (date-range picker) live; it is likewise
now themed (`js/zkmax/db/css/daterangebox.css`; contract `doc/contracts/daterangebox.md`; preview
`daterangebox.zul`).
