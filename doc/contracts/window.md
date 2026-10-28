# Component: window (theme design)
tier: T1
category: container
preview: ${PREVIEW_URL}/window.zul
rules: see .claude/skills/zk-component-rules/components/window.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: Dialog.css
- DESIGN.md sections: §1, §5, §6, §7, §10, §11

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-window` | background-color | rgb(255, 255, 255) |
| c2 | `.z-window` | border-radius | 4px (dialog corner) |
| c3 | `.z-window` | box-shadow | level-3 modal shadow (DESIGN.md §6) |
| c4 | `.z-window-header` | padding | 16px |
| c5 | `.z-window-header` | font-size | 16–20px |
| c6 | `.z-window-header` | font-weight | 500 |
| c7 | `.z-window-content` | padding | 16–24px |
| c8 | `.z-window-icon` | width / height | 24–32px (icon button area) |
| c9 | `.z-window-header` | box-sizing | `border-box` — min-height must INCLUDE padding, not add to it (guards the dropped-reset regression; see doc/skill-gaps.md 2026-06-16) |
| c10 | `.z-window-header` | height | ≤ 72px — compact title bar; a content-box blow-up reads ~88px. Bare title text must not wrap (`white-space: nowrap`) |

## States to evaluate
- [ ] default, modal, movable, with-close-button, embedded variant
