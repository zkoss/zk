# Jess ZK-core issue 重現報告

日期：2026-10-13。範圍：[deferred.md](../deferred.md) Group A 的 11 個 issue。重現由 software-engineer（Sonnet）完成，本文由主 session 依其回報整理；獨立 verifier 的覆核結果見文末。

## 環境

- ZK 11.0.0-SNAPSHOT。
- Marble：zk `f498236cea`（branch `marble`）+ zkcml `e787aed4d`，zkpreview 8085。
- IceBlue：`ZK10/sync-ice` 的 master `65c55120a2`，zktest 8125。
- 瀏覽器：Playwright 1.59.1 Chromium 147.0.7727.15（headless）。
- 重現頁：本目錄 `jess-<N>.zul`（#44 另有 `jess-44-plain.zul`）；截圖 `img/jess-<N>-{marble,iceblue}.png`。各步驟的過程截圖沒有提交（依 D123，過程檔不進 repo）。

## 摘要

| # | 分類 | Marble | IceBlue | 既有 Jira | 可能來源 |
|---|---|---|---|---|---|
| 58 | 設計如此 | flyout header 的 chevron 為 inline `display:none` | 同 | 相關 ZK-5687（Open） | `LayoutRegion.ts` `setSlide()` L415-416 |
| 32 | 只有 Marble | head 與 body 上下堆疊 | 並排正常 | 無 | Marble `zkmax/.../big/css/biglistbox.css` 缺 `float:left`／`overflow:hidden`（IceBlue `biglistbox.less` L39/78/139 有） |
| 9 | ZK bug | 重現 | 重現 | 無 | `Cascader.ts` `_doSelectItem()` L339 起、`close()` L587 |
| 21 | 設計如此；附帶一個小 ZK bug | 沒有 Apply 鈕（設計如此） | 同 | 相關 ZK-6146（Closed） | `DaterangePopup.ts` `_onTimeChange`／`_doApply`／`_mergeTime` |
| 20 | 只有 Marble | 每月 1 號畫成實心 | 不畫 | ZK-6134（Closed） | Marble `zkmax/.../db/css/daterangebox.css` 缺 IceBlue `daterangebox.less` L144 的中和規則 |
| 28 | ZK bug 或需求 | 重現 | 重現 | 相關 ZK-5136 | `Searchbox.ts` `close()` L664、`open()` L627 不清 search input |
| 44 | ZK bug（依版面） | 殼結構下不捲；單純頁面可捲 | 單純頁面也不捲 | ZK-5941、ZK-5942（Open），ZK-5379（Closed） | `Anchornav.ts` `_scrollIntoViewTop()` L216-250 |
| 50 | 只有 Marble | 水平展開的 cave 蓋住觸發項，點不到 | cave 在下方，可關 | 無 | Marble `zkmax/.../nav/css/nav.css` L271 缺 `top` |
| 56 | 設計如此 | popup 出現在頁面流位置（頁底） | 同 | 相關 ZK-1391 | `Window.ts` `_doOverlapped()` L127-140 |
| 75 | 只有 Marble | left／top 箭頭壓進按鈕 8px | 無重疊 | 無 | Marble `zul/.../wgt/css/confirmpopup.css` L37-40（正 margin）；IceBlue `confirmpopup.less` L108-111 用負 margin |
| 14 | 設計如此 | 預設只有空白列 | 同 | 無 | `Chosenbox.ts` L1474-1479 預設 `&nbsp;`，需設 `noResultsText` |

- 只有 Marble：4 項（#32、#20、#50、#75），都是主題回歸，純 CSS 可修，歸 ZK-6112。
- ZK bug：3 項（#9、#28、#44），外加 #21 的小 bug。
- 設計如此：#58、#56、#14、#21，最多算功能需求。

## 逐項證據

- **#58**：點 West header `«`，再點收合條開 flyout。flyout 內的 `-btn` 是 inline `display:none`，兩個主題一樣。收合條上的 `-btned` chevron 仍可見（Marble 13×20，IceBlue 24×24），點它後 `open=true`、`slide=false`，region 回到 docked。沒有死路。
- **#32**：Marble 的 `.z-biglistbox-head-frozen` 在 y=1、`float:none`，scrollable head 在 y=40，body 也堆疊在 y=821。IceBlue 兩者都是 `float:left`，x=6 與 x=399。
- **#9**：展開 Japan 後點外面，label 空白、圖示變 `×`、Japan 仍高亮，重開也一樣；按 Escape 結果相同。server 端 log 全程為空，所以不是「提交」，而是 client 狀態與 server 不一致：未 finalize 時 `_selectedUuids` 仍被寫入。點 × 後 server 收到 `onSelect []`，選 Tokyo 收到 `[Tokyo]`。「有時殘留、有時乾淨」沒有重現成穩定差異。
- **#21**：兩個主題相同。選完日期後 popup 保持開啟，之後改時間會提交（打字、spinner 箭頭、Tab 後 Escape 都會；箭頭與點外面要等關閉時 flush 才送）。footer 只有 Clear／Cancel，類別註解明寫「no Apply button」。小 bug：先改時間再選日期，spinner 顯示 09:15，提交值卻是 00:00。未選日期就關閉則沒有 `onChange`。
- **#20**：兩個主題的 DOM 相同（每個面板的 1 號都有 `z-calendar-selected`）。Marble 把它畫成實心圓（`calendar.css` L201-208 套用到 daterangebox），IceBlue 用 `daterangebox.less` L144 的規則中和。deferred.md 說「ZK artifact，非 theme 錯誤」只對一半。
- **#28**：輸入 `Eu`、點外面、重開，輸入框仍是 `Eu`，清單仍只剩 Europe，兩個主題相同。算不算 bug 需要產品定義。
- **#44**：把頁面包成殼結構（`borderlayout > center autoscroll > div height=100%`，同 preview shell）。Marble 點 Third Window 後 win3 top 仍是 1760，沒有元素的 scrollTop 改變；IceBlue 只捲 16px。單純頁面 Marble 正常捲，IceBlue 完全不捲，因為 `zk.wcs` 有 `html,body{height:100%}`。原因：祖先是 `overflow:visible` 但高度固定、`scrollHeight>clientHeight`，被誤判為可捲動容器，offset 被吃掉。IceBlue 把祖先高度設為 auto 後可捲（scrollY=1753）。preview 的 `anchornav.zul` 三個區段各 200px，在 1049px viewport 只有約 9px 可捲，這是另一個 demo 問題（Group B）。
- **#50**：Marble 水平展開 navbar，cave 是 `position:absolute`、y=-24、高 88，蓋住觸發項（y 0..40）。觸發項中心點的 `elementFromPoint` 落在 cave 內的 navitem，再點無法關閉；Escape 與點空白也不會關。水平 collapsed 與垂直 collapsed（hover 彈出）兩個主題都能正常關閉。
- **#56**：mode=popup 的 window 載入即顯示，位置是它在頁面流中的位置（repro top=936，viewport 600；preview `window.zul` top=1054，viewport 1049）。theme 沒有設 position／transform／height。建議 demo 補 `position="center"`（未實測）。
- **#75**：Marble left 箭頭 x=300..308、按鈕 x=300，重疊 8px；top 箭頭 y=200..208、按鈕 y=200，重疊 8px；right／bottom 無重疊。IceBlue 四向都無重疊。Marble 用 `margin-bottom`／`margin-right` 預留箭頭空間，但 ZK Popup 只計 `margin-top`／`margin-left`。
- **#14**：輸入 `zzz`，預設 chosenbox 的 popup 只有空白的 `.z-chosenbox-empty`；設 `noResultsText` 才顯示 "No results"。

## 備註

- CaseFoundry MCP 全部回 `Invalid request parameters`；既有 Jira 改用 Atlassian JQL 關鍵字搜尋，不如語意搜尋完整。
- gretty 的 `build/inplaceWebapp` 是啟動時複製的，新增 zul 要同時複製進去才會被伺服。
- 本目錄以外的暫存重現頁（untracked）：`zkpreview/src/main/webapp/repro/`、`ZK10/sync-ice/zk/zktest/src/main/webapp/jess/`，以及兩者的 `build/inplaceWebapp` 複本。

## 獨立覆核（Opus verifier，盲測，未看本報告）

覆核者在同樣兩個站上自己跑，結論與上表 9 項一致，2 項不同：

- **#21**：verifier 重現不了「先改時間再選日期 → 提交 00:00」（送出的值都與 spinner 一致），但另外找到「Cancel／Escape 會提交未儲存的時間修改」。engineer 回頭覆查：00:00 是時好時壞（兩個主題各 3 次中 1 次出錯），未找到觸發條件，不建單；Cancel／Escape 的問題兩個主題都穩定重現。
- **#28**：verifier 判定為設計如此：文件寫明 `searchText` 會保留，要用 `setSearchText` 清除。採 verifier 的判定，不建單。

verifier 補充的事實：
- #32 頁面一載入就堆疊，不必先橫向捲動。
- #50 兩個主題的水平展開 navbar 都不會因點外面或 Escape 關閉（ZK 行為）；Marble 多了觸發項被 cave 蓋住，所以完全關不掉。
- #56 window 被 `makeVParent` 移到 body 後，捲動後再顯示仍用舊的絕對 top。
- #14 設了 `creatable` 時，建立提示會取代 `noResultsText`（API 文件有寫）。

## 最終處置

| # | 處置 |
|---|---|
| 9 | 建單 [ZK-6193](https://zkoss.atlassian.net/browse/ZK-6193) |
| 44 | 已有 [ZK-5941](https://zkoss.atlassian.net/browse/ZK-5941)（Open，症狀相同），補上 11.0.0 重現頁與根因留言，不另開單 |
| 21 | 原 issue 設計如此；附帶的 Cancel／Escape 問題建單 [ZK-6194](https://zkoss.atlassian.net/browse/ZK-6194)（由 ZK-6146 的 `fdc68f146` 引入，已建 Relates 連結） |
| 32、20、50、75 | 只有 Marble：主題回歸，依規則在 ZK-6112 下修，不建單 |
| 28、56、14、58 | 設計如此，最多算功能需求，不建單 |
