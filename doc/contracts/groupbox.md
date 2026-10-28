# Component: groupbox (theme design)
tier: T2
category: container
preview: ${PREVIEW_URL}/groupbox.zul
rules: see .claude/skills/zk-component-rules/components/groupbox.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- DESIGN.md sections: §1, §5, §7, §11
- Closest-sibling CSS to read first: `zul/src/main/resources/web/js/zul/wnd/css/panel.css` (reuse padding, radius, shadow, header treatment)
- Closest MUI: Card with CardHeader + Collapse.
- Visual goal: panel-lite (smaller emphasis than panel; subtle outline instead of full card shadow when nested).

## Expected values (T2)
- Border: 1px solid outline-variant (rgba(0,0,0,0.12)).
- Border-radius: 6px (card-like).
- Title font-size 14–16px, weight 500.
- Content padding: 16px.
- Header `box-sizing: border-box` and height ≤ 56px — min-height (48px) must INCLUDE its 12px vertical padding, not add to it. A content-box blow-up reads ~73px (guards the dropped-reset regression; see doc/skill-gaps.md 2026-06-16). The 3d-mold header uses `padding:0` so it is unaffected.
- Collapsed state: only header visible, with chevron indicator rotated.

## States to evaluate
- [ ] default, collapsed, readonly, notitle, 3d
