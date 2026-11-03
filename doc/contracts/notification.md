# Component: notification (theme design)
tier: T1
category: feedback
preview: ${PREVIEW_URL}/notification.zul
rules: see .claude/skills/zk-component-rules/components/toast-notification.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Snackbar.css / Alert.css
- `doc/spec/DESIGN.md` sections: "Brand & Status Colors", "Elevation / Shadow", "Border Rules"

## Expected values

> `.z-notification` is a bare layout shell — NO background, padding, shadow or
> radius (see skill `toast-notification.md`). All visual card styling lives on
> `.z-notification-content`. Putting padding on the shell shifts the absolute
> `.z-notification-icon` away from its left offset; putting a background on it
> double-paints behind the card. (A duplicate `.z-notification` rule in misc.css
> once regressed exactly this.)

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-notification-content` | min-height | `var(--zk-notification-height)` |
| c2 | `.z-notification` | padding | 0 (shell carries no padding) |
| c2b | `.z-notification` | background-color | transparent (no frame behind the card) |
| c3 | `.z-notification-content` | border-radius | `var(--zk-notification-radius)` |
| c4 | `.z-notification-content` | box-shadow | `var(--zk-elevation-2)` |
| c5 | `.z-notification-content` | font-size | `var(--zk-typescale-body-medium-size)` |
| c6 | `.z-notification-info .z-notification-content` | background | **opaque** `var(--zk-notification-bg)` (surface-container-highest) — alpha must be 1 (floating overlay must not let content bleed through) |
| c8 | `.z-notification-warning .z-notification-content` | background | **opaque** `var(--zk-color-warning-container)` — alpha must be 1 |
| c9 | `.z-notification-error .z-notification-content` | background | **opaque** `var(--zk-color-error-container)` — alpha must be 1 |
| c10 | `.z-notification-icon` | size | 20–24px, matching status colour; there is no accent stripe (severity is carried by the icon colour and the container fill) |
| c11 | `.z-notification-content` | single-line vertical alignment | message text optically centred within the `min-height: var(--zk-notification-height)` card (`display: block; align-content: center`; flex is not used because a flex container strips the spaces around inline HTML in a message) — a block layout without `align-content` top-aligns the line and misaligns it with the centered icon; text centre must be within ±1px of the card centre |

## States to evaluate
- [ ] info, warning, error (ZK `Clients`/`Notification` only support these three; no SUCCESS type)
