# Component: calendar (theme design)
tier: T1
category: media
preview: ${PREVIEW_URL}/calendar.zul   (also: datebox popup)
rules: see .claude/skills/zk-component-rules/components/calendar.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- MUI CSS: no clean analog — MUI DateCalendar/PickersDay CSS is not in static-css-output
- DESIGN.md sections: §1, §3, §5, §7, §11
- Known issue: class names in this contract's former DOM selectors section
  (`.z-calendar-day`, `.z-calendar-day-selected`) may differ from
  `.claude/skills/zk-component-rules/components/calendar.md` (`.z-calendar-cell`).
  Resolve against live ZK 10 render before marking contract-approved.

## Expected values

| id | selector | property | expected |
|----|----------|----------|----------|
| c1 | `.z-calendar-day` | width / height | ~36px |
| c2 | `.z-calendar-day` | border-radius | 50% (circular hover) |
| c3 | `.z-calendar-day:hover` | background-color | state-layer tint |
| c4 | `.z-calendar-day-selected` | background-color | rgb(55, 111, 208) |
| c5 | `.z-calendar-day-selected` | color | rgb(255, 255, 255) |
| c6 | `.z-calendar-day-today` | border / outline | 1px solid primary or ring |
| c7 | `.z-calendar-day-other` | opacity | 0.38 |
| c8 | `.z-calendar-week` | font-size | 11–12px |

## States to evaluate
- [ ] day default, hover, selected, today, other-month, weekday-header, month-nav
