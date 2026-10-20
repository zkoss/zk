# zkdemo B6／B7 回報問題的最終結論

來源（已整理，未修改）：
- `doc/zkdemo-b6-marble-fixes.md`
- `doc/zkdemo-b7-z5-jira-draft.md`

基準：來源記錄至 2026-10-19。來源中的 commit hash 多數因 marble 歷史改寫而失效，本文件不列 hash，請以 `ZK-6112:` commit 與來源為準。

## 一、B6 的 5 個 Marble 問題（Z1–Z5）

| # | 問題 | 歸屬 | 結果 |
|---|---|---|---|
| Z1 | `.z-anchorlayout` 的 `gap` 讓 `anchor` 寬度換行 | Marble（IceBlue 無 gap） | 移除 `gap`；2×50%、3×33% 同列（PASS） |
| Z2 | groupfoot 規則的 selector 從不命中 | Marble | 依裁示 D3-C，比照 Listbox `listgroupfoot`：底色 `surface-container-low`、上方 1px `outline-variant`、文字 `on-surface`、字重 500、非斜體；文字 16/12/20px 與 Listbox 相同（PASS） |
| Z3 | Grid `detail` 無 `.z-detail*` 樣式 | Marble（zkex 缺 Marble 版 CSS） | 補 zkex detail CSS；依裁示 D2-A，內容左側 16px、移除 `::before` 分隔線；新增 `--zk-grid-detail-padding`（12px／compact 6px）（PASS） |
| Z4 | `.sr-only` 未定義，Paging `detailed="true"` 的文字可見 | Marble | 在 utility 補 `.sr-only`，與 `.z-visually-hidden` 同定義（PASS） |
| Z5 | `.z-label-pre`、`.z-label-multiline` 為死規則 | Marble 端（非回歸） | 刪除兩條死規則，修正 `doc/contracts/label.md` l5/l6；不改 ZK Label DOM（PASS） |

Z2 的附帶發現：`listbox.css` 的 `.z-listgroupfoot` padding 同時套在 `td` 與 `.z-listcell-content`，文字位於 32px（一般列 16px）。歸屬 Marble 回歸，以 ZK-6112 修為只 pad content（PASS）。

## 二、其他 demo 回報或 demo 相關的項目

| 項目 | 歸屬 | 結果 |
|---|---|---|
| Grid groupfoot 不跟隨欄位 `align`（B6-Z6） | Marble（`.z-groupfoot-content` 的 `display:flex`） | 拿掉 `display:flex`、`align-items`、`gap`，並加防護測試；靠右與置中欄對齊（PASS） |
| `<html>` 內 table 的 `th` 置中、`td` 靠左（zkdemo-30 回報） | Marble（`html.css` 未處理對齊） | 加 `.z-html th:not([align]) { text-align: inherit; }`，並加 html-table 防護（PASS） |
| Grid 分組資料列縮排（NS4） | demo 頁（示範頁每列第一格的 inline `padding-left:15px`，非主題 CSS） | 依裁示 D8-B，移除 zkpreview grid／listbox grouping 頁的 inline 縮排並重拍基準圖；資料列、標題、小計皆 17px |
| B7-Z5：無 ref 的 Notification 不跟著頁面捲動 | ZK Jira（核心 bug，與主題無關） | 已送出 ZK-6196（2026-10-14，Affects Version 11.0.0）；截圖待手動附加 |

## 三、ZK-6196 草稿摘要（已送出）

- 標題：`notification: a ref-less bottom/middle notification moves up by the page scroll offset`
- 摘要：無 ref 時 `position()` 回退到 body box，而兩個主題都設定 `html, body { height: 100% }`，使 bottom_*／middle_* 位置受捲動位移影響；top_* 因 viewport clamp 而正常。候選修法是回退到 viewport box，會影響所有 ref-less Popup，需做回歸檢查。
- 限制：IceBlue 未實際執行，為程式碼推論；已釋出版本是否受影響，來源標為「可能」，尚未在 10.x 重現。

## 四、未納入本文件的內容

- 過程與量測細節：各批 agent 派工、驗證輪次與 F1–F6 等逐項發現、8085 PID 紀錄、squash 流程、hash 對照表、截圖容忍值的推算。
- 追蹤清單第 28 至 61 項中與 B6／B7 無關的項目（Tree、focus、forced-colors、input-invalid 等）。
- D5／D7（stacking 卡片模式的小計底色與 padding）、D10（Listbox／Tree 排序圖示）、NS5 至 NS36 的 Marble 內部改善。
- 來源中標為「不在本次範圍」或「另議」的項目，例如 zkmax stacking 模式 groupfoot 白底、Grid 分組資料列縮排 31px。
