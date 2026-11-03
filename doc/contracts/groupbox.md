# Component: groupbox (theme design)
tier: T2
category: container
preview: ${PREVIEW_URL}/groupbox.zul
rules: see .claude/skills/zk-component-rules/components/groupbox.md
contract-approved: false
zk-version: 10.2.1-jakarta

## References
- `doc/spec/DESIGN.md` sections: "Surface Palette", "Corner Radii", "Typography", "Border Rules"
- Closest-sibling CSS to read first: `zul/src/main/resources/web/js/zul/wnd/css/panel.css` (reuse padding, radius, shadow, header treatment)
- Closest MUI: Card with CardHeader + Collapse.
- Visual goal: panel-lite (smaller emphasis than panel; subtle outline instead of full card shadow when nested).

## Expected values (T2)
- Border: 1px solid `var(--zk-groupbox-border-color)`.
- Border-radius: `var(--zk-groupbox-radius)` (card-like).
- Title font-size `var(--zk-typescale-title-medium-size)`, weight `var(--zk-typescale-title-medium-weight)`.
- Content padding: `var(--zk-spacing-4)`.
- Root `.z-groupbox` sets `box-sizing: border-box` in groupbox.css; the header's border-box comes from the global `*` reset in `zul/src/main/resources/web/zul/css/base/_reset.css`, not from groupbox.css. Header height ≤ 56px — min-height (`calc(var(--zk-groupbox-header-height) + 1px)`) must INCLUDE its vertical padding (the `min()` on `.z-groupbox-header`), not add to it. A content-box blow-up reads ~73px. The 3d-mold header uses `padding: 0 var(--zk-spacing-3)` (no vertical padding), so it is unaffected.
- Collapsed state: only header visible, with chevron indicator rotated.

## States to evaluate
- [ ] default, collapsed, readonly, notitle, 3d
