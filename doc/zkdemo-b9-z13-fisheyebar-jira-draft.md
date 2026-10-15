# B9-Z13 Fisheyebar 捲動後不放大：Jira 草稿（未提交）

狀態：已於 2026-10-15 提交為 ZK-6205（Affects Version 11.0.0）。D2 已決（選項 A）：依規則交給核心團隊，Marble 不修。
來源：zkdemo-b5 轉來的 B9 缺陷報告；本 session 於 2026-10-15 在 Marble（8085）實測重現。

## 量測值

- Marble，viewport 1568x756：bar 在頁首時，hover 第 3 項寬度為 80,120,160,120,80,80。
- 在 bar 上方插入 1500px spacer 並捲到 bar（scrollY=1051）：`document.body.offsetHeight` 為 918（等於 viewport），同樣 hover 寬度為 80,80,80,80,80,80。
- `jq(document).off('mouseout', w._onBodyOut)` 後，同樣 hover 回到 80,120,160,120,80,80。
- IceBlue：未在瀏覽器實測。`iceblue_c-10.3.0.FL.20260112` 的 CSS 有 `html,body{height:100%}`，原始碼推論條件相同。

## Jira 內容（英文，提交時使用）

**Project / Type:** ZK / Bug
**Affects Version/s:** 11.0.0 (Marble) and the current IceBlue-based releases (to be confirmed)
**Component:** Fisheyebar (zkex)

**Summary:** Fisheyebar stops magnifying once the page is scrolled, because `_onBodyOut` compares page coordinates with the viewport-tall body box

**Description**

`zkex/menu/Fisheyebar.ts` registers `_onBodyOut` on `mouseout`. It compares `evt.pageX`/`evt.pageY` with `zk(BODY).revisedOffset()` plus `BODY.offsetWidth`/`offsetHeight`. When `html, body { height: 100% }` is in effect, the body box is only as tall as the viewport, so after the page is scrolled every `mouseout` is treated as leaving the body and the magnification collapses.

**Steps to reproduce**

1. Open a page with a horizontal `<fisheyebar>` below the first viewport, for example put a 1500px `<div>` above it.
2. Scroll so the bar is visible.
3. Hover an item.

**Expected:** the hovered item grows up to `itemMaxHeight` (160 in the sample).
**Actual:** every item stays at `itemHeight` (80).

**Verification:** `jq(document).off('mouseout', widget._onBodyOut)` in the console restores the magnification.

**Suggested fix:** compare `evt.clientX`/`evt.clientY` with the viewport size, or collapse only when `evt.relatedTarget` is null.

## 預覽頁附註（不屬於此 Jira）

`zkpreview/src/main/webapp/web/fisheyebar.zul` 的 attachEdge 勾選框初始未勾選，但 fisheyebar 初始就是 `bottom`；第一次勾選設成 `bottom`（無變化）。attachEdge 只影響 hover 時的放大方向（bottom 向上長、top 向下長），靜止狀態看不出差別。
