# Jenkins ZATS #1117 失敗測試分類

- 來源：https://jenkins3.pxinternal.com/job/ZK-Gradle-ZATS-Test/1117/testReport/
- 執行者：peakerlee，2026-10-14 16:42（+0800），結果 `UNSTABLE`
- 參數：`ZK_BRANCH=pr/3624`、`ZKCML_BRANCH=pr/1384`（兩者標題皆為 `[WIP] ZK-6112: a theme of Material Design 3`），`RUN_ENV=RUN_WITHOUT_A11Y`，`JDK=OpenJDK 17`
- 實際建置的 commit（皆為 squash 成單一 commit，parent 為當時的 master）：
  - zk：`6a89d6c816`（parent `e1f66aac36`）
  - zkcml：`086e3564c`（parent `fd2356735`）
- 與本地的關係：兩個 commit 都不在本地 `marble` 的歷史中；本地 zk `marble` 在 `hawkchen/marble`（`f0a4223a79`）之後又多 42 個 commit。兩個 commit 已 fetch 到本地，可用 `git show 6a89d6c816:<path>` 讀取。

## 結果總數

| 項目 | 數量 |
|---|---|
| 通過 | 3594 |
| 失敗 | 182（`REGRESSION` 181、`FAILED` 1），分布在 153 個測試類別 |
| 跳過 | 3 |

master 基準（同一個 job，`ZK_BRANCH=master`）：#1106 失敗 3、#1107 失敗 1、#1109 失敗 3、#1111 失敗 3。#1117 的 182 筆中，只有 3 筆在這四次 master 執行中也失敗過。

## 方法

1. 腳本預處理（不用模型）：依錯誤訊息特徵分組；從 `config.properties` 與測試原始碼找出對應的 `.zul` 與元件；標出 `@Tag("IceBlueOnly")`；比對測試檔在建置版本與本地 `marble` 是否不同；比對 master 是否也失敗。
   - 預處理結果：182 筆皆無 `IceBlueOnly` 標記；測試檔在建置後皆未修改；所有測試在 master 上都存在（沒有本分支新增的測試）。
2. Sonnet 5.5 分批分類（依元件區域分 8 批，唯讀，不跑測試、不改檔）。
3. Opus 5.5 抽查：每個分類抽樣，加上所有 `UNSURE` 項目。

## 分類定義

| 分類 | 意義 | 後續處理 |
|---|---|---|
| `MARBLE_REGRESSION` | Marble 的樣式造成使用者看得到的行為或版面問題（元素被遮住無法點擊、popup 位置錯、sticky 失效等） | 在 ZK-6112 下修 Marble |
| `ICEBLUE_ASSUMPTION` | 行為正確，但斷言寫死 IceBlue 的像素、顏色、字型或圖示 | 依 `.claude/rules/iceblue-tests.md` 加 `@Tag("IceBlueOnly")`，不改寫測試 |
| `TEST_GEOMETRY` | 測試意圖與主題無關，但依賴偶然的尺寸或座標（例如固定 offset 點擊），Marble 尺寸改變後點錯位置 | 待決定：加標記會失去覆蓋，改測試則需另行同意 |
| `FIXED_SINCE_BUILD` | 本地 `marble` 在建置之後的 commit 已修正 | 本地重跑確認 |
| `CORE_BUG` | 兩個主題都會發生的 widget／zul 程式問題，不是 Marble 造成 | 開 ZK Jira，交核心團隊 |
| `ENV_FLAKY` | 環境、Docker、時序問題，或在 master 也失敗 | 不處理，或重跑確認 |
| `UNSURE` | 靜態分析無法判斷 | 交 Opus 抽查或本地重跑 |

## 批次

| 批次 | 測試數 | 類別數 | 狀態 |
|---|---|---|---|
| listbox | 28 | 21 | 第一輪完成 |
| combo-popup | 33 | 30 | 第一輪完成 |
| grid-tree | 23 | 20 | 第一輪完成 |
| date-time | 22 | 14 | 第一輪完成 |
| form-input | 19 | 18 | 第一輪完成 |
| layout-container | 13 | 9 | 第一輪完成 |
| misc-a | 20 | 19 | 第一輪完成 |
| misc-b | 19 | 18 | 第一輪完成 |
| infra-or-master | 5 | 4 | 完成（見下） |

第二輪驗證完成。逐筆資料（分類、根因、證據、決定性量測）：`doc/zats-1117-triage-data.json`。

### infra-or-master（不經模型，直接判定 `ENV_FLAKY`）

| 測試 | 依據 |
|---|---|
| `B36_3051305Test#test()` | master 4 次中失敗 3 次 |
| `B96_ZK_4843Test#test()` | master 失敗 1 次；`Response was too slow!` |
| `F61_ZK_969Test#test()` | master 失敗 1 次 |
| `Z60_Touch_011Test#test()` | `UnreachableBrowserException`，遠端瀏覽器連不上 |
| `Z60_Touch_011Test#executionError` | `docker compose kill` 失敗 |

## 分類結果

### 第一輪（Sonnet 5.5，未經驗證）

| 分類 | 筆數 |
|---|---|
| `TEST_GEOMETRY` | 60 |
| `UNSURE` | 40 |
| `MARBLE_REGRESSION` | 39 |
| `ICEBLUE_ASSUMPTION` | 37 |
| `ENV_FLAKY` | 5 |
| `FIXED_SINCE_BUILD` | 1 |

不同批次對同一個共同原因給了不同分類，主要有三群：表頭 padding 移到 `th`、`pointer-events:none`、原生 checkbox `<input>` 被隱藏。

### 第二輪（Opus 5.5 驗證）

- A：第一輪判為 `MARBLE_REGRESSION`、且不屬於下面三群的 27 筆，逐筆驗證
- B：三個跨批次共同原因的 65 筆，統一分類規則
- C：所有其餘 `UNSURE`（35 筆），加上 `ICEBLUE_ASSUMPTION`、`TEST_GEOMETRY` 各抽 6 筆、`FIXED_SINCE_BUILD` 1 筆

驗證結果：140 筆中同意 91、改判 18、仍無法判斷 31；另 42 筆（未抽中的 `ICEBLUE_ASSUMPTION`／`TEST_GEOMETRY`）沿用第一輪。

### 抽查後分類（重跑前；最新總數見文末）

| 分類 | 筆數 | 處理 |
|---|---|---|
| `TEST_GEOMETRY` | 65 | 其中 21 筆來自表頭 padding，修 Marble 後應一併通過；其餘 44 筆待 D1 |
| `MARBLE_REGRESSION` | 44 | 26 個根因，見下表，在 ZK-6112 下修 |
| `ICEBLUE_ASSUMPTION` | 36 | 依 `.claude/rules/iceblue-tests.md` 加 `@Tag("IceBlueOnly")` |
| `UNSURE` | 31 | 每筆在資料檔 `decider` 欄有一個決定性量測，待重跑 |
| `ENV_FLAKY` | 5 | 不處理 |
| `FIXED_SINCE_BUILD` | 1 | `F110_ZK_6097_StyleTest`，`9e82e16a8d` 已修，重跑確認 |

本地 `marble` 對這 44 筆 regression 的成因規則都沒有改過（差異僅註解），沒有一筆需要交核心團隊（成因在 IceBlue 都不存在）。

### MARBLE_REGRESSION 根因（26 個）

| 根因 | 筆數 | 現象 | 修正位置 |
|---|---|---|---|
| `calendar-text-pointer-events` | 6 | 月曆標題的年／月文字設 `pointer-events:none`，`Calendar._changeView` 依點擊目標決定檢視，點年份只會開月檢視 | zk `calendar.css:70-72` |
| `zkmax-listbox-stacking-css-missing` | 5 | zkcml `eea2b6428` 刪了 zkmax `listbox.less`，`.z-listbox--stacking` 沒有 Marble 對應樣式，響應式 Listbox 不堆疊 | zkcml zkmax |
| `mesh-th-padding` | 4 | 表頭 padding 從內容 div 移到 `th`：`MeshWidget.ts:324` `_getMinWd` 重複計入，`hflex="min"` 多 32px／欄；`Frozen.ts:594-606` 判斷 `offsetWidth < 2` 永遠不成立，捲出的欄回不來。修正後另 21 筆 `TEST_GEOMETRY` 應一併通過 | zk `grid.css`、`listbox.css`、`tree.css` |
| `drb-readonly-button-pointer-events` | 3 | ZK-6131 之前的舊規則仍讓唯讀 daterangebox 按鈕不可點、看起來像停用 | zkcml `daterangebox.css:151-154` |
| `tab-content-pointer-events` | 2 | `.z-tab-content` 設 `pointer-events:none`，tab caption 內的 combobox／textbox 點不到 | zk `tabbox.css:205`、`:645` |
| `listbox-hidden-first-header-body-col` | 2 | 第一個 listheader 隱藏時，第一個可見 body 欄寬 0；疑為 body table 的 `border-collapse: collapse` 加上 `visibility: collapse` 的 `<col>`（中信心） | zk `listbox.css:228-232` |
| `frozen-sticky-groupfoot-missing` | 2 | sticky 選擇器清單漏 `.z-groupfoot-inner`、`.z-listgroupfoot-inner` | zk `frozen.css:149-166` |
| `chosenbox-inplace-hidden` | 2 | `.z-chosenbox-inplace{display:none}` 無條件隱藏 | zkcml `chosenbox.css:205-207` |
| `zkmax-grid-stacking-descendant-leak` | 1 | 後代選擇器讓巢狀 grid 也變卡片 | zkcml zkmax `grid.css:42-67` |
| `vertical-tabbox-panels-stretch` | 1 | 垂直 tabbox 以 flex stretch，切到短 tab 後高度不縮 | zk `tabbox.css:451-454` |
| `timepicker-hardcoded-height` | 1 | timepicker 寫死 34/36px，datebox 為 38/40px | zkcml `timepicker.css` |
| `slider-thumb-margin-offset` | 1 | thumb `margin-left:-10px` 讓拖曳格線偏 10px | zk `slider.css:51-53` |
| `progressmeter-css-transition` | 1 | CSS width transition 疊在 jQuery 動畫上 | zk |
| `portal-untitled-panel-drag-handle-hidden` | 1 | 無標題 panel 在 portallayout 沒有拖曳把手 | zk `panel.css:129` ／ zkcml `portallayout.css` |
| `nav-open-collapsed-topmost` | 1 | 收合的 navbar 仍展開子選單 | zkcml `nav.css` |
| `multislider-mark-label-pointer-events` | 1 | 刻度標籤點擊無效 | zkcml `multislider.css:296-315` |
| `mesh-table-css-layout` | 1 | `sizedByContent` 量測時 `MeshWidget.ts:55`、`:102` 清掉 inline `tableLayout`，但 `listbox.css:225`、`:231` 的 `table-layout: fixed` 仍生效 | zk `listbox.css` |
| `menupopup-no-scroll` | 1 | 無 `max-height` 且 `overflow:hidden`，長選單不能捲 | zk `menu.css` |
| `input-family-vertical-align-mismatch` | 1 | textbox 與 chosenbox／datebox 的 `vertical-align` 不一致，底邊差 1.45px（中信心） | zk／zkcml |
| `hlayout-cssflex-valign-missing` | 1 | 沒有移植 `.z-valign-*.z-flex{align-items}`，hlayout `valign` 無效 | zk `layout.css` |
| `groupbox-root-border-vflex-min` | 1 | border 移到 root，`vflex=min` 少 2px（低信心） | zk `groupbox.css` |
| `coachmark-content-height` | 1 | content 少 `height:100%` | zkcml |
| `checkbox-baseline-misalign` | 1 | switch mold 的 label 高 7px | zk `checkbox.css` |
| `calendar-weekofyear-pointer-events` | 1 | 週數格 `pointer-events:none`，`onWeekClick` 不觸發 | zk `calendar.css:261-271` |
| `accordion-border-height-math` | 1 | accordion 溢出 1–3px（低信心） | zk `tabbox.css:570` |
| `absolutelayout-no-height` | 1 | 少 `height:100%`，無高度時塌成 0 | zk `absolutelayout.css` |

### 測試端的分類（不是 Marble 缺陷）

- `ICEBLUE_ASSUMPTION` 36 筆：最大群是原生 checkbox `<input>`（10 筆，Marble 用 `opacity:0; pointer-events:none` 隱藏，真實點擊 mold 或 label 正常）；其餘為 hover／focus 以 `::before` 或 outline 呈現、popup 貼齊、Lucide 圖示等。
- `TEST_GEOMETRY` 65 筆：表頭 padding 21 筆（隨 Marble 修正一併解決）；`pointer-events:none` 的圖示、停用控制項、分隔符 15 筆（Selenium 拒絕點擊，使用者點擊結果相同）；popup `min-width: 160px` 蓋住下一個按鈕 4 筆；其餘為 slider 預設寬度、6px 捲軸等。

### UNSURE（31 筆）→ 本地重跑結果（2026-10-15）

一個 Sonnet agent 在未修改的 `marble`（zk `ce6c74b62f`）上，把 76 筆目標各跑 3 次，結果放在逐筆資料檔的 `rerun` 欄。IceBlue 對照的作法是把 `zk/dist/lib/iceblue_c.jar` 放進 classpath，再把 `org.zkoss.theme.preferred` 設成 `iceblue_c`。這個 jar 是 checkout 內附的，不是從 master 建置出來的。它本身就有 18 筆目標失敗，所以只在 IceBlue 能通過的列上拿它當對照。

- 基準：71 筆 3 次都失敗，2 筆 3 次都通過（`B65_ZK_1552`、`F110_ZK_6097_Style`），3 筆沒跑（`@ForkJVMTestOnly` 需要 Docker）。沒有忽過忽不過的。
- 71 筆失敗中，70 筆和 Jenkins 的失敗點相同，只有座標或數字不同。`F50_3306718` 在本地 3 次中有 2 次停在第一次拖曳。
- 先前寫的 font-swap 假設（`B50_2980383`）：重跑時判定「沒有成立」，但這個結論有誤，因為重跑只量了字型載入之後的狀態。G7 在 mount 當下量測，確認 Tabs 用的是 Inter 載入前的寬度（差 80px）。這一列改為 `CORE_BUG`，見 `doc/zats-1117-fix-plan.md` 的 G7 一節。

31 筆 `UNSURE` 的最終分類：

| 分類 | 筆數 | 內容 |
|---|---|---|
| `MARBLE_REGRESSION` | 17 | 12 個新根因，見下表 |
| `TEST_GEOMETRY` | 9 | 測試裡的像素計算（`B96_ZK_4920`、`F80_ZK_2708`）；半像素差（`B90_ZK_4218` ×2、`B80_ZK_3103`）；點擊落點不同，但使用者實際點擊的結果一樣（`B102_ZK_5616` ×2、`F90_ZK_4380_mvvm`、`ComponentAnnotationTest`） |
| `ENV_FLAKY` | 4 | `B50_ZK_937`、`F50_3306718`、`F55_ZK_416`：Selenium `dragAndDrop` 比 ZK `zk/drag.ts:57` 的 `setTimeout(0)` 啟動得早。成功次數 Marble 是 0–1/3，IceBlue 是 3/3；加 150ms 停頓後都是 3/3，所以要在測試端修正。`B65_ZK_1552`：本地通過，但找不到修正它的 commit |
| `UNSURE` | 1 | `B70_ZK_2351`，需要 Docker，沒跑 |

新的 `MARBLE_REGRESSION` 根因（12 個、17 筆）：

| 根因 | 筆數 | 現象（重跑量測） | 信心 |
|---|---|---|---|
| `combobox-popup-top-at-input-top` | 4 | `B102_ZK_5612`：popup 的上緣和輸入框的上緣對齊（100 vs 100），IceBlue 在輸入框下方（124） | 高 |
| `tree-rod-avg-row-height` | 3 | `B96_ZK_4834` ×2、`B96_ZK_4910`：捲動回落到 111637（和 Jenkins 相同），IceBlue 是 240000；原因還沒找到。4910 沒跑，從同一頁的桌面版推論 | 中 |
| `groupbox-6071-not-interactable` | 2 | `B110_ZK_6071`（Touch 版沒跑）：`groupbox.css:18` 的 `overflow:hidden` 加上 flex，讓外層 groupbox 只剩 2px 高 | 高 |
| `tooltip-after-popup-click` | 1 | `B90_ZK_4339`：點擊後 tooltip 還在，滑鼠移動 1px 也不消失 | 高 |
| `combobox-hflex-min-width` | 1 | `B96_ZK_5044`：值被截斷（scrollWidth 50 > clientWidth 44），但本地 IceBlue 也失敗 | 低 |
| `grid-popup-hflex-min-no-regrow` | 1 | `B90_ZK_4491`：grid 停在 124 寬，IceBlue 會從 47 變成 95；原因還沒找到 | 中 |
| `tabs-scroll-after-font-swap` | 1 | `B50_2980383`：捲動少 80px，最後一個 tab 在可視範圍外 | 中 |
| `cropper-selection-zero-width` | 1 | `B86_ZK_4269`：holder 寬 0（IceBlue 300） | 高 |
| `auxhead-detach-hflex-relayout` | 1 | `B90_ZK_4443`：移除 auxhead 後欄寬跳動 8–16px | 中 |
| `errorbox-before-end-offset` | 1 | `F50_3316591`：errorbox 比 textbox 右緣少 101px | 高 |
| `splitter-vbox-width` | 1 | `F70_ZK_112`：`.z-cell` 的 `white-space:nowrap`（`grid.css:298-310`）把 vbox 撐在 644px。先前的 decider 把這類歸為 `TEST_GEOMETRY`，但畫面上 bar 和 pane 的邊緣確實不一致 | 中 |

### 重跑後的總數（182 筆）

| 分類 | 筆數 |
|---|---|
| `TEST_GEOMETRY` | 74（表頭 padding 21 筆已隨 G1 通過，其餘 53 筆待 D1） |
| `MARBLE_REGRESSION` | 61（38 個根因） |
| `ICEBLUE_ASSUMPTION` | 36 |
| `ENV_FLAKY` | 9 |
| `FIXED_SINCE_BUILD` | 1 |
| `UNSURE` | 1 |
