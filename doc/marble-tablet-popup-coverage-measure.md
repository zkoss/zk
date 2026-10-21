# Tablet 彈出層覆蓋量測（NS22）

日期：2026-10-20。只有量測：沒有改任何基準圖，沒有 commit；暫時的 instrumentation 已用 `git checkout` 還原。對應桌面版量測：`doc/marble-popup-gallery-conclusion.md`（A1），盤點：`doc/marble-popup-gallery-conclusion.md`。

## 1. 執行事實

| 項目 | 值 |
|---|---|
| 工作位置 | `/Users/hawk/Documents/workspace/ZK10/p3/zk`，分支 `gaps-round2`，由 `9064f77890` 以 `git merge --ff-only 860e151bef` 快轉到 `860e151bef`（marble 的 `ZK-6112: record the residual toolbar overflow overshoot …`） |
| EE 配對 | `/Users/hawk/Documents/workspace/ZK10/p3/zkcml`，分支 `gaps-round2`，HEAD `fe6323380`（`merge --ff-only fe6323380` 回「Already up to date」，與指示一致） |
| 建置 | `withjdk.sh 17 bash ./gradlew :zul:compileMarbleCss :zul:jar`（zk）、`:zkmax:compileMarbleCss :zkmax:jar :zkex:jar`（zkcml），皆成功；zkcml 這次沒有改寫 `lib/spel2js/package-lock.json` |
| 伺服器 | `p3/zk/zkpreview` 的 `appRun -PhttpPort=8087`，`tail -f /dev/null` 持有 stdin；`http://127.0.0.1:8087/smoke.zul` 回 200；所有指令用 `PREVIEW_URL=http://127.0.0.1:8087` |
| 記錄的 PID | bash 41807、tail 41810、gradle client 41811、Jetty 44394（`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/7d5b9c66-28ef-4caf-8453-4b21d3a621a0/scratchpad/tab/pids.txt`） |
| 指令 | `npx playwright test -c src/test/playwright/playwright.config.ts --project=<project> --update-snapshots=none --workers=2` |

| Playwright project | 結果 | 時間 | 螢幕截圖（`toHaveScreenshot`） |
|---|---|---|---|
| `tablet` | 58 passed、0 failed | 52.5 秒 | 31 張（`tablet.spec.ts` 的 30 個 `visualCases` + `scrollview-tablet.png`） |
| `mesh-header-footer-tablet` | 135 passed、0 failed | 2.0 分 | 0 張（幾何斷言；`header.screenshot()` 只用來算像素，不是基準圖） |
| `forced-colors-tablet` | 14 passed、0 failed | 11.4 秒 | 0 張 |

基準圖：三個 project 跑完後，`git status --porcelain` 在 p3/zk 只剩 untracked 的 `node_modules`、`zkpreview/node_modules`；`forced-colors-gallery` 沒有跑，99 張 forced-colors review PNG 沒有被改寫。p3/zkcml 乾淨。

### 1.1 量測方法

三個 spec 暫時加了同一個 helper（`_instr.ts`，已從 repo 移除，副本在 `/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/7d5b9c66-28ef-4caf-8453-4b21d3a621a0/scratchpad/tab/_instr.ts`；diff 在 `/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/7d5b9c66-28ef-4caf-8453-4b21d3a621a0/scratchpad/tab/instrumentation.diff`）：

- 每個 `toHaveScreenshot` 之前呼叫 `dump()`：掃全部帶 `class` 的元素，取 `display` 不是 none、`visibility` 不是 hidden、寬高大於 0，且與截圖 clip（`.z-p-8` 的 boundingBox）相交者，記下它的 `z-` class。這與 A1 的 `inClip` 定義相同（不處理 z-order 遮擋、`opacity`、`overflow` 裁切）。
- 每個測試的 `afterEach` 再 `dump()` 一次（範圍是整個 viewport），記下測試結束時畫面上可見的 class；用來判斷「打開了但沒截圖」。colorbox 測試在 `openPopup()` 之後另外 `dump()` 一次。
- 這些 `dump()` 的原始資料：`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/7d5b9c66-28ef-4caf-8453-4b21d3a621a0/scratchpad/tab/instr-tablet.jsonl`（87 筆）、`instr-mesh-header-footer-tablet.jsonl`（135 筆）、`instr-forced-colors-tablet.jsonl`（14 筆）。

## 2. Q1：只在 tablet CSS 出現的 class

來源：`/Users/hawk/Documents/workspace/ZK10/p3/zkcml/zkmax/src/main/resources/web/zkmax/css/tablet/`（13 個檔），去註解、`url()`、字串後取 `.z-*` selector，共 131 個 class。

### 2.1 class-index 是桌面建的，怎麼處理

`zkpreview/doc/class-index.json`（`generatedAt` 2026-10-19T11:40:03Z，commit `173c2a33d9`，143 頁；聯集 3346 個 class）由桌面 UA 產生，不含只在 mobile UA 才渲染的 DOM。處理方式：

1. 先用 class-index 聯集過濾：131 個中 105 個在聯集內，26 個不在。
2. 再用 tablet UA（iPad UA、834x1112、`hasTouch`、`isMobile`）把 class-index 的 143 頁全部載入一次（`domcontentloaded` + `networkidle`），收集靜止時 DOM 內所有 `z-` class（包含 display:none 的元素，與盤點 §1 同一定義），共 3329 個（`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/7d5b9c66-28ef-4caf-8453-4b21d3a621a0/scratchpad/tab/rest-tablet.json`）。
3. 結果：26 個中，10 個在 tablet 靜止 DOM 內（timebox 的 wheel 隱藏在 popup 內，5 頁：`inputs`、`overview`、`preview`、`pv/invalid-size-guard`、`timebox`），16 個在 tablet 靜止時也不存在。

### 2.2 26 個 class

| class | tablet CSS 檔 | tablet 靜止 DOM | 類別 | 觸發 |
|---|---|---|---|---|
| `z-calendar-wheel-body`、`-button`、`-cave`、`-date`、`-footer`、`-left`、`-line`、`-list`、`-list-selected`、`-right`（10 個） | `_wheel.css` | 不存在 | popup | tap `.z-datebox-button` 或 datebox 的 input（mobile UA 下 input 為 readonly）；wheel 由 `zkmax/touch/datebox-touch.ts` 渲染 |
| `z-timebox-popup`、`z-timebox-wheel-body`、`-button`、`-cave`、`-footer`、`-left`、`-line`、`-list`、`-right`、`-time`（10 個） | `_wheel.css` | 存在（隱藏，在 popup 內） | popup | tap `.z-timebox-button` 或 input；另外 datebox 的 datetime 格式在 wheel 內嵌一個 timebox wheel（tap 探測看到 `z-timebox-wheel-time`） |
| `z-timebox-wheel-list-selected` | `_wheel.css` | 不存在 | popup | 同上（開啟後 ZK 才加在置中那一列） |
| `z-notification-close` | `_feedback.css` | 不存在 | api | 已在桌面的 88 個內（`z-notification-close`）；不算 tablet 新增 |
| `z-button-icon` | `_buttons.css` | 不存在 | variant | 某個 sclass 或 ZK 內部才給的 class；產生者未確認 |
| `z-passwordbox` | `_inputs.css` | 不存在 | other（無預覽頁） | 與盤點 §2 的 no-page 同因：沒有 passwordbox 預覽頁 |
| `z-treecol-button` | `_mesh.css` | 不存在 | variant | `HeaderWidget.ts` 的 `$s('button')`，要 treecol 設定 menupopup 才出現；它是開啟欄選單 popup 的觸發元件 |
| `z-uploadbutton` | `_buttons.css` | 不存在 | variant | `fileupload` 屬性；`tablet.spec.ts` 註解寫此項 SKIP（non-deterministic） |

小計：26 個；扣除與桌面 88 個重疊的 `z-notification-close` 後，tablet 新增 25 個：popup 21（datebox wheel 10、timebox wheel 11）、variant/other 4。

### 2.3 tablet CSS 內另外會作用在彈出層的 class（不算「靜止時不存在」）

這些 class 在桌面 class-index 內（因為元素靜止時就在 DOM），但 tablet 規則的作用對象在彈出層內：`z-combobox-popup`（`_inputs.css` bottom sheet）、`z-comboitem`（44px，也在 88 內）、`z-datebox-popup`（`_wheel.css`，也在 88 內）、`z-menu-content`/`z-menuitem-content`/`z-menu-image`/`z-menuitem-image`/`z-menu-icon`/`z-menuitem-icon`（`_menu.css`，menubar 列與下拉列共用）、`z-errorbox-close`（`_feedback.css`）、`z-confirmpopup-ok`/`z-confirmpopup-cancel`（`_buttons.css`）、`z-window-header`/`z-window-icon`（`_window.css`，modal 也用）。下面 §3 把 `z-combobox-popup`、`z-errorbox-close`、menu 與 confirmpopup 單獨量。

## 3. Q2：覆蓋量測

母集合：桌面 88 個（popup + api）+ tablet 新增 25 個 = 113 個。

| 結果 | 桌面 88 | tablet 新增 25 | 合計 |
|---|---|---|---|
| 在 tablet 螢幕截圖內可見 | 0 | 0 | 0 |
| tablet spec 打開了（沒截圖），測試結束時畫面上可見 | 2（`z-comboitem`、`z-datebox-popup`） | 21（datebox wheel 10、timebox wheel 11） | 23 |
| 沒有打開 | 86 | 4 | 90 |

量測值的限制：「打開」是以測試結束（或 colorbox 的 `openPopup()` 之後）畫面上某元素帶該 class 且有非零盒子判定。

### 3.1 tablet 螢幕截圖在拍什麼

31 張截圖全是 `goto` 後靜止狀態（`.z-p-8` 區域），沒有任何一張包含被打開的彈出層。母集合 113 個 class 一個也沒有在截圖內出現。母集合以外：`tablet-errorbox` 那張靜止畫面內有 `z-errorbox-close`（頁面本身就畫出 errorbox）、`tablet-menubar` 內有 `z-menu-content`、`z-menuitem-content`、`z-menu-image`、`z-menuitem-image`、`z-menu-icon`（menubar 列本身；下拉的 menupopup 列沒有打開）。`z-notification-close` 在 `tablet-notification` 那張不可見（頁面靜止時沒有 notification 實體；`tablet.spec.ts` 註解已說明）。

### 3.2 tablet spec 打開了哪些彈出層（都沒有截圖）

| 測試（`tablet.spec.ts`） | 開法 | 開啟的彈出層 | 斷言內容 |
|---|---|---|---|
| `tablet-datebox-wheel` 的 `picker opens on-screen …`、`tapping the input lands the sheet flush …` | `page.touchscreen.tap` 在 `.z-datebox-button` / `.z-datebox-input` | datebox wheel sheet | wheel-list 高度 ≤ 240、sheet 在畫面內、底部貼齊 viewport（2 個開啟測試） |
| `tablet-timebox-wheel`（同上兩個） | `touchscreen.tap` | timebox wheel sheet | 同上（2 個開啟測試） |
| `tablet-combobox-sheet`（2 個） | `touchscreen.tap` 在 `.z-combobox-button`；第二個先捲動 400px | combobox bottom sheet | `position: fixed`、貼齊底部、捲動後仍貼齊 |
| `tablet-colorbox-dismiss`（2 個） | `widget.openPopup()`（API）；再 `touchscreen.tap(500, 300)` 或對色塊派發 mouse 事件 | colorbox popup | 關閉後 `display:none`、選色後沒有殘留 `z-palette-button`；`openPopup()` 之後 viewport 內沒有 `z-colorbox-popup` 或 `z-colorpalette-*` 可見（原因未確認：斷言只檢查 `display`、父節點為 BODY） |
| `tablet-datebox-wheel`、`tablet-timebox-wheel` 的 `mobile-readonly trigger stays interactive` | 不開，只讀 `pointer-events` | 無 | |

合計 8 個測試打開彈出層，0 個截圖。`tablet-matrix-*`、`tablet-touch-size`、`tablet-no-horizontal-overflow`、`mesh-header-footer-tablet`（135 個）、`forced-colors-tablet`（14 個）都不打開彈出層（讀程式碼加 `afterEach` 紀錄確認：這些測試結束時沒有母集合的 popup class 可見）。

### 3.3 母集合 113 個 class 逐項（依元件分組）

欄位說明：「tablet 螢幕截圖」＝是否在 31 張截圖內可見；「tablet spec 開啟」＝上面 §3.2；「桌面配方在 tablet UA 的重現」＝另外做的探測（§3.4）。每組前兩行是 Q3 的答案（tablet 開法、與桌面差異）。

### ROD loading

- tablet 開法：捲動 ROD grid/listbox/tree（觸控捲動）
- 與桌面差異：`_mesh.css` 作用於 mesh；click 配方測試通過

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-grid-loading` | api | 否 | 否 | 桌面配方通過（斷言 overlay；未記錄 class） |
| `z-listbox-loading` | api | 否 | 否 | 桌面配方通過（斷言 overlay；未記錄 class） |
| `z-tree-loading` | api | 否 | 否 | 桌面配方通過（斷言 overlay；未記錄 class） |

### bandbox

- tablet 開法：tap `.z-bandbox-button`
- 與桌面差異：無差異：absolute 下拉 300x70，click 配方在 tablet UA 通過（無 tablet popup CSS）

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-bandbox-open` | popup | 否 | 否 | click 配方可見 |
| `z-bandpopup` | popup | 否 | 否 | click 配方可見 |
| `z-bandpopup-content` | popup (?) | 否 | 否 | 未量到 |

### busy/loading/mask

- tablet 開法：tap 按鈕（`Clients.showBusy`）
- 與桌面差異：無 tablet 規則；click 配方 2 個 busy 測試通過

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-apply-loading` | api | 否 | 否 | click 配方可見 |
| `z-apply-loading-icon` | api | 否 | 否 | click 配方可見 |
| `z-apply-loading-indicator` | api | 否 | 否 | click 配方可見 |
| `z-apply-mask` | api | 否 | 否 | click 配方可見 |
| `z-loading` | api | 否 | 否 | click 配方可見 |
| `z-loading-icon` | api | 否 | 否 | click 配方可見 |
| `z-loading-indicator` | api | 否 | 否 | click 配方可見 |
| `z-mask` | api (?) | 否 | 否 | 未量到 |

### cascader

- tablet 開法：tap `.z-cascader`
- 與桌面差異：無差異（click 配方通過；absolute 200x118）

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-cascader-active` | popup | 否 | 否 | click 配方可見 |
| `z-cascader-open` | popup | 否 | 否 | click 配方可見 |

### chosenbox

- tablet 開法：tap `.z-chosenbox`；輸入文字需軟鍵盤（未確認 readonly 與否）
- 與桌面差異：click 配方通過（position relative，popup 在 chosenbox 內）

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-chosenbox-create` | popup | 否 | 否 | click 配方可見 |
| `z-chosenbox-empty-creatable` | popup | 否 | 否 | click 配方可見 |
| `z-chosenbox-option-hover` | popup | 否 | 否 | click 配方可見 |

### colorbox

- tablet 開法：tap `.z-colorbox`（palette）；API `openPopup()`（tablet.spec 已用）
- 與桌面差異：與桌面不同：tap 探測看到 `z-colorpalette`、`z-colorpalette-color`，沒有 `z-colorpalette-popup`；popup absolute 358x318。click 配方斷言 `z-colorpalette-popup`/`-selected`/`z-colorpicker-popup` 失敗

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-colorpalette-button` | popup (?) | 否 | 否 | 未量到 |
| `z-colorpalette-popup` | popup | 否 | 否 | 未量到 |
| `z-colorpalette-selected` | popup | 否 | 否 | 未量到 |
| `z-colorpicker-popup` | popup | 否 | 否 | 未量到 |

### combobox

- tablet 開法：tap `.z-combobox-button`（touchscreen.tap）
- 與桌面差異：bottom sheet：`_inputs.css` `.z-combobox-popup{position:fixed;inset:auto 10px 0}`、`.z-comboitem{min-height:var(--zk-touch-target-min)}`；量測 fixed x10 y832 814x280（viewport 834x1112）。`z-combobox-open` 在 tablet 不出現（click 配方 6 個 combobox 測試失敗）。input 在 tablet 的輸入路徑（empty-search）未確認

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-combobox-empty-search-message` | popup | 否 | 否 | 未量到 |
| `z-combobox-empty-search-message-hidden` | popup | 否 | 否 | 未量到 |
| `z-combobox-open` | popup | 否 | 否 | 未量到 |
| `z-comboitem` | popup | 否 | 是：tablet-combobox-sheet x2 | tap 探測可見 |
| `z-comboitem-icon` | popup | 否 | 否 | 未量到 |
| `z-comboitem-image` | popup | 否 | 否 | tap 探測可見 |
| `z-comboitem-inner` | popup | 否 | 否 | 未量到 |
| `z-comboitem-selected` | popup | 否 | 否 | 未量到 |
| `z-selected` | popup (?) | 否 | 否 | 未量到 |

### combobutton

- tablet 開法：tap `.z-combobutton-button`
- 與桌面差異：無差異（click 配方通過，menupopup absolute 160x98）；`_buttons.css` 只放大箭頭 44px

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-combobutton-open` | popup | 否 | 否 | click 配方可見 |

### datebox

- tablet 開法：tap `.z-datebox-button` 或 input（touchscreen.tap）
- 與桌面差異：wheel sheet：`_wheel.css` `.z-datebox-popup:has(.z-calendar-wheel-cave){position:fixed;inset:auto 10px 0}`；量測 fixed x10 y908 814x204；沒有桌面的格狀 calendar，`z-datebox-open` 不出現，`z-datebox-timezone` 未見

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-datebox-open` | popup | 否 | 否 | 未量到 |
| `z-datebox-popup` | popup | 否 | 是：tablet-datebox-wheel x2 | tap 探測可見 |
| `z-datebox-timezone` | popup | 否 | 否 | 未量到 |

### daterangebox

- tablet 開法：tap `.z-daterangebox-button`
- 與桌面差異：無 tablet CSS；popup absolute 756x421 在 834 寬 viewport 內；click 配方 4 個 open 測試通過；hover 預覽（`z-cell-range-preview-*`）在觸控上是否可達：未確認

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-cell-range-begin` | popup | 否 | 否 | click 配方可見 |
| `z-cell-range-end` | popup | 否 | 否 | click 配方可見 |
| `z-cell-range-mid` | popup | 否 | 否 | click 配方可見 |
| `z-cell-range-preview-end` | popup | 否 | 否 | click 配方可見 |
| `z-cell-range-preview-mid` | popup | 否 | 否 | click 配方可見 |
| `z-daterangebox-popup` | popup | 否 | 否 | click 配方可見 |
| `z-daterangebox-popup-cancel` | popup | 否 | 否 | click 配方可見 |
| `z-daterangebox-popup-clear` | popup | 否 | 否 | click 配方可見 |
| `z-daterangebox-popup-footer` | popup | 否 | 否 | click 配方可見 |
| `z-daterangebox-popup-panels` | popup | 否 | 否 | click 配方可見 |
| `z-daterangebox-popup-times` | popup | 否 | 否 | click 配方可見 |
| `z-daterangebox-popup-today` | popup | 否 | 否 | click 配方可見 |

### drawer

- tablet 開法：tap `Open Left` 按鈕
- 與桌面差異：無差異（fixed 834x1112 全 viewport）

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-drawer-open` | popup | 否 | 否 | click 配方可見 |

### errorbox

- tablet 開法：輸入不合法值後 blur（click/tap 欄位再點別處）
- 與桌面差異：無 tablet 位置差異；`_feedback.css` 放大 `.z-errorbox-close` 到 24px；`pv/popup-fixtures.zul` 版面在 tablet UA 未量

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-errorbox-right` | api | 否 | 否 | click 配方可見 |
| `z-errorbox-up` | api | 否 | 否 | click 配方可見 |

### loadingbar

- tablet 開法：API：按鈕呼叫 loadingbar
- 與桌面差異：`pv/popup-api-fixtures.zul` 在 tablet 的文件寬 1001x1335，超出 834 viewport；click 配方在按鈕 stable 檢查逾時，原因未確認

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-loadingbar-position-bottom` | api (?) | 否 | 否 | 未量到 |

### menu

- tablet 開法：tap `.z-menubar .z-menu`（touchscreen.tap）
- 與桌面差異：`_menu.css`：`.z-menu-content`/`.z-menuitem-content` min-height 44px、圖示 24px；click 配方的 `z-menu-selected` 斷言失敗，tap 探測看得到 `z-menu-selected`、`z-menupopup-separator`；checked 測試 click 通過

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-menu-selected` | popup | 否 | 否 | tap 探測可見 |
| `z-menuitem-checked` | popup | 否 | 否 | click 配方可見 |
| `z-menuitem-selected` | popup (?) | 否 | 否 | 未量到 |
| `z-menupopup` | popup | 否 | 否 | click 配方可見 |
| `z-menupopup-content` | popup | 否 | 否 | click 配方可見 |
| `z-menupopup-separator` | popup | 否 | 否 | 未量到 |

### messagebox

- tablet 開法：tap 按鈕（API `Messagebox.show`）
- 與桌面差異：無 tablet 專屬規則；messagebox 按鈕是否吃到 `_buttons.css` 的 `.z-button` 44px：未確認；click 配方通過，absolute 480x198

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-messagebox-error` | api | 否 | 否 | click 配方可見 |
| `z-messagebox-exclamation` | api | 否 | 否 | click 配方可見 |
| `z-messagebox-question` | api | 否 | 否 | click 配方可見 |

### navbar

- tablet 開法：tap collapsed `.z-nav`
- 與桌面差異：無差異（click 配方通過）

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-nav-popup` | popup | 否 | 否 | click 配方可見 |

### notification

- tablet 開法：tap 按鈕（API `Clients.showNotification`）
- 與桌面差異：`_feedback.css` 放大 `.z-notification-close` 到 24px；click 配方通過

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-notification-close` | api | 否 | 否 | click 配方可見 |
| `z-notification-down` | api | 否 | 否 | click 配方可見 |
| `z-notification-left` | api | 否 | 否 | click 配方可見 |
| `z-notification-pointer` | api | 否 | 否 | click 配方可見 |
| `z-notification-right` | api | 否 | 否 | click 配方可見 |
| `z-notification-up` | api | 否 | 否 | click 配方可見 |

### popup

- tablet 開法：tap `Show Popup`；tooltip 是 hover/長按
- 與桌面差異：popup 通過 click 配方；tooltip 在觸控上如何出現：未確認

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-popup` | popup | 否 | 否 | click 配方可見 |
| `z-popup-content` | popup | 否 | 否 | click 配方可見 |
| `z-popup-tooltip` | popup | 否 | 否 | click 配方可見 |

### searchbox

- tablet 開法：tap `.z-searchbox`
- 與桌面差異：無差異（click 配方通過，absolute 190x312）

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-searchbox-active` | popup | 否 | 否 | click 配方可見 |
| `z-searchbox-open` | popup | 否 | 否 | click 配方可見 |

### slider

- tablet 開法：touch drag 手把（`page.touchscreen` 或 mouse）
- 與桌面差異：`_slider.css` 放大手把；click 配方通過

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-slider-popup` | popup | 否 | 否 | click 配方可見 |

### timepicker

- tablet 開法：tap `.z-timepicker-button`
- 與桌面差異：無 tablet CSS；click 配方通過，absolute 400x300

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-timepicker-option-selected` | popup | 否 | 否 | click 配方可見 |

### toast

- tablet 開法：tap 按鈕（API `Clients.showNotification`/toast）
- 與桌面差異：無差異（click 配方通過）

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-toast-position` | api | 否 | 否 | click 配方可見 |
| `z-toast-position-bottom-center` | api | 否 | 否 | click 配方可見 |
| `z-toast-position-bottom-left` | api | 否 | 否 | click 配方可見 |
| `z-toast-position-bottom-right` | api | 否 | 否 | click 配方可見 |
| `z-toast-position-middle-center` | api | 否 | 否 | click 配方可見 |
| `z-toast-position-middle-left` | api | 否 | 否 | click 配方可見 |
| `z-toast-position-middle-right` | api | 否 | 否 | click 配方可見 |
| `z-toast-position-top-center` | api | 否 | 否 | click 配方可見 |
| `z-toast-position-top-left` | api | 否 | 否 | click 配方可見 |
| `z-toast-position-top-right` | api | 否 | 否 | click 配方可見 |
| `z-toast-position-wrapper` | api | 否 | 否 | click 配方可見 |

### toolbar

- tablet 開法：tap overflow button
- 與桌面差異：無差異（click 配方通過，absolute 439x54 貼 viewport 底）

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-toolbar-popup-open` | popup | 否 | 否 | click 配方可見 |

### window

- tablet 開法：tap `Open modal window`；API `doModal()`
- 與桌面差異：`_window.css` header 56px、icon 44px；click 配方失敗：fixture 頁在 tablet 文件寬 1001x1335，斷言 viewport 834x1112 不成立

| class | 盤點類別 | tablet 螢幕截圖 | tablet spec 開啟 | 桌面配方在 tablet UA 的重現 |
|---|---|---|---|---|
| `z-window-modal` | api (?) | 否 | 否 | 未量到 |

### datebox wheel / timebox wheel（tablet 新增 21 個）

- tablet 開法：`touchscreen.tap`（`tablet.spec.ts` 已有 `tapFirstTrigger`）。
- 與桌面差異：整個 DOM 與桌面不同（`.z-calendar-wheel-*`、`.z-timebox-wheel-*`），`_wheel.css` 把 popup 釘在 viewport 底部（`position:fixed; inset:auto 10px 0`），列高 40px x 3 列，Set/Cancel 兩個 44px 按鈕。
- 21 個 class 全部被 `tablet-datebox-wheel`、`tablet-timebox-wheel` 的 4 個開啟測試打開（結束時可見），沒有截圖。

| class 群 | tablet 螢幕截圖 | tablet spec 開啟 |
|---|---|---|
| `z-calendar-wheel-body/-button/-cave/-date/-footer/-left/-line/-list/-list-selected/-right`（10） | 否 | 是（`tablet-datebox-wheel`） |
| `z-timebox-popup`、`z-timebox-wheel-body/-button/-cave/-footer/-left/-line/-list/-list-selected/-right/-time`（11） | 否 | 是（`tablet-timebox-wheel`） |

### 其他 tablet 新增（4 個，沒有打開）

| class | 類別 | tablet 開法 |
|---|---|---|
| `z-button-icon` | variant | 產生者未確認 |
| `z-passwordbox` | other（無預覽頁） | 需要先有預覽頁 |
| `z-treecol-button` | variant | tree 的 treecol 設 menupopup，tap 該按鈕開欄選單 popup；欄選單 popup 沒有預覽頁示範（`z-columns-menupopup` 在 `tablet-grid`、mesh 測試結束時可見，是靜止畫面上的元素，不是被打開） |
| `z-uploadbutton` | variant | `fileupload` 屬性；spec 註解標為 non-deterministic |

### 母集合以外、tablet CSS 作用在彈出層的 class

| class | tablet CSS | 狀態 |
|---|---|---|
| `z-combobox-popup` | `_inputs.css` bottom sheet | 打開沒截圖（`tablet-combobox-sheet` 2 個） |
| `z-errorbox-close` | `_feedback.css` 24px | `tablet-errorbox` 靜止截圖內可見；沒有「條件失敗後才出現」的 errorbox 截圖 |
| `z-menu-content`、`z-menuitem-content`、`z-menu-image`、`z-menuitem-image`、`z-menu-icon` | `_menu.css` | menubar 列在 `tablet-menubar` 截圖內；下拉 menupopup 的列沒有打開 |
| `z-menuitem-icon` | `_menu.css` | 不在任何 tablet 截圖或測試結束畫面內 |
| `z-confirmpopup-ok`、`z-confirmpopup-cancel` | `_buttons.css` | 不在任何 tablet 截圖或測試結束畫面內；沒有 confirmpopup 的 tablet 測試 |

### 3.4 桌面配方在 tablet UA 的探測

為了知道「沿用桌面 `popup-gallery.spec.ts` 的開法」在 tablet 上會怎樣，用暫時的 config（`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/7d5b9c66-28ef-4caf-8453-4b21d3a621a0/scratchpad/tab/_tablet-popup.config.ts`，已從 repo 移除；`snapshotDir` 指到 scratchpad，所以沒有任何 repo 基準圖被讀寫）把 `popup-gallery.spec.ts` 以 tablet 的 UA、viewport、`hasTouch` 跑一次（加上 A1 的 instrumentation，diff：`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/7d5b9c66-28ef-4caf-8453-4b21d3a621a0/scratchpad/tab/instrumentation-popup-tablet.diff`），`--update-snapshots=missing` 只寫到 scratchpad。這是在你指定的三個 tablet project 之外多跑的探測，沒有碰 repo 內的任何基準。

- 56 個測試：4 passed、52 failed。52 個失敗中 37 個只是「scratchpad 內沒有基準圖」（popup 已打開、`recordVisible` 已記錄、圖已寫入 scratchpad），實質等於配方可用；15 個是真的失敗：
  - combobox 6 個、datebox 3 個：斷言的 `z-combobox-open`、`z-datebox-open` 在 tablet 不出現（touch mold 是 bottom sheet / wheel）；combobox 2 個 empty-search 測試找不到可見的 `.z-comboitem`／empty-search message（click 在 input 上沒有展開；tablet 的輸入路徑未確認）；datebox timezone 測試的 `td[aria-label=…]` 格狀 calendar 在 wheel 下不存在。
  - colorbox 3 個：`z-colorpalette-popup`、`z-colorpalette-selected`、`z-colorpicker-popup` 在 click 配方下不出現。
  - menubar 1 個：`z-menu-selected` 在 click 配方下不出現（改用 tap 探測可見）。
  - window modal 1 個：fixture 頁 `pv/popup-api-fixtures.zul` 在 tablet UA 文件寬 1001x1335，超出 834x1112 viewport，斷言 viewport 尺寸失敗。
  - loadingbar 1 個：按鈕 `waiting for element to be visible, enabled and stable` 逾時，原因未確認（同一個 fixture 頁）。
- 通過的 4 個：menubar separator 保持隱藏、ROD grid、ROD listbox、ROD tree。
- tap 探測（`touchscreen.tap`，`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/7d5b9c66-28ef-4caf-8453-4b21d3a621a0/scratchpad/tab/tap.cjs`，結果 `/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/7d5b9c66-28ef-4caf-8453-4b21d3a621a0/scratchpad/tab/tap.json`）：combobox、datebox、colorbox、menubar、bandbox、timepicker、daterangebox 七種各 tap 前 8 個觸發元件。測得的 tablet 幾何（viewport 834x1112）：

| 彈出層 | position | 盒子 | 備註 |
|---|---|---|---|
| combobox popup | fixed | x10 y832 814x280 | bottom sheet；`z-comboitem-image` 在 `pv/popup-api-fixtures.zul` 可見 |
| datebox popup | fixed | x10 y908 814x204 | wheel；`z-calendar-wheel-*`、`z-timebox-wheel-*` |
| colorbox popup | absolute | x238 y794 358x318 | 內容為 `z-colorpalette`、`-head`、`-newcolor`、`-input`、`-body`、`-color`；沒有 `z-colorpalette-popup` |
| menubar menupopup | absolute | x48 y206 160x283 | tap 可見 `z-menu-selected`、`z-menupopup-separator`、`z-menupopup-open` |
| bandbox popup | absolute | x97 y233 300x70 | `z-bandbox-open` 可見（沒有 touch mold） |
| timepicker popup | absolute | x217 y816 400x300 | |
| daterangebox popup | absolute | x32 y228 756x421 | `-times`、`-today`、`-clear`、`-footer`、`-panels`、`-cancel` 可見 |

## 4. Q3：沒打開的 class 要怎麼補

把 §3.3 的各組整理：

### 4.1 tablet 的彈出層與桌面不同（要新的 tablet 測試，不能搬桌面斷言）

| 群組 | 不同處（CSS 出處） | 建議測試 |
|---|---|---|
| combobox | bottom sheet，`_inputs.css` `.z-combobox-popup{position:fixed; inset:auto 10px 0}`、`.z-comboitem` 44px；`z-combobox-open` 不存在 | 1 張預設 sheet、1 張帶 description/icon/image 的項目（`z-comboitem-inner/-icon/-image`）；共 2 |
| datebox / timebox | wheel sheet，`_wheel.css`（整份）；沒有格狀 calendar、`z-datebox-open`、`z-datebox-timezone` 路徑未確認 | date wheel、datetime wheel（內嵌 timebox wheel）、timebox wheel 各 1 張；timezone 1（需先確認 tablet 上有無 `z-datebox-timezone`）；共 3 至 4 |
| colorbox | popup 內容為 `z-colorpalette` 而非 `z-colorpalette-popup`；palette 與 picker 兩種 | palette 1、picker 1；共 2（且先確認 picker 在 tablet 的切換方式，未確認） |
| menu / menubar | `_menu.css` 44px 列與 24px 圖示；用 `touchscreen.tap` 才看得到 `z-menu-selected` | 預設 menu 1、checked item 1；共 2 |
| combobutton | `_buttons.css` 箭頭 44px；下拉為 menupopup | 1 |
| errorbox | `_feedback.css` close 24px | 1（觸控下制造 constraint 失敗） |
| notification | `_feedback.css` close 24px | 1 |
| window modal | `_window.css` header 56px、icon 44px；fixture 頁要先修成不超出 834 寬 | 1 |
| treecol 欄選單 | `_mesh.css` 的 `z-treecol-button` 44px | 1（需先加預覽頁 fixture） |
| confirmpopup | `_buttons.css` 的 `z-confirmpopup-ok/-cancel` | 1（需 fixture，目前預覽頁有無 confirmpopup 未確認） |

小計：約 15 至 16 個測試，涵蓋 tablet CSS 直接作用的彈出層。

### 4.2 tablet 的彈出層與桌面同形（沒有 tablet CSS 規則，只是在 834 寬 viewport 內）

bandbox 1、chosenbox 2、cascader 1、searchbox 1、timepicker 1、daterangebox 2（seeded、time+clear/today；hover 預覽在觸控的可達性未確認）、popup 1（tooltip 在觸控上如何出現未確認）、navbar 1、toolbar overflow 1、drawer 1、slider 1、messagebox 1、toast 1、busy 2（全頁、元件）、loadingbar 1（需先修 fixture）、ROD 3（grid、listbox、tree）。小計約 23 個。

這 23 個的 class 在 tablet 上用 click 配方已證實可見（§3.4），因為 tablet CSS 沒有規則作用其上，它們的樣式與桌面相同；是否值得為它們再各拍一張 tablet 基準圖，是範圍決定，不是量測結論。

### 4.3 估計

| 範圍 | 測試數 |
|---|---|
| 只補 tablet CSS 直接作用的彈出層（§4.1） | 約 15 至 16 |
| 再加上同形彈出層的 tablet 基準（§4.2） | 約 38 至 39 |

這是粗估：以「一個彈出層狀態一張截圖」計，同一張截圖盡量涵蓋多個 class（例如 timebox wheel 一張涵蓋 11 個 class）；實際數量取決於是否每個桌面狀態都要有 tablet 對應。

## 5. 未驗證與限制

- 「可見」只看 `display`、`visibility`、盒子大小與 clip 相交，沒有看 `opacity`、遮蓋、`overflow` 裁切。
- 「打開沒截圖」用測試結束時的畫面判定；如果彈出層在測試中途開過、結束前已關閉（colorbox 的第一個測試只在 `openPopup()` 之後有一次 mid 紀錄），可能量不到。
- 母集合的 88 個直接取自桌面量測（A1）所用的盤點 §4，盤點本身沒有重算；盤點的 class-index 是 2026-10-18 的版本，本次 §2 使用的是 2026-10-19 的版本（143 頁），兩者差異未對帳。
- §3.4 的桌面配方探測用 `click`（滑鼠事件）而非 `touchscreen.tap`，所以 combobox、datebox 的失敗有一部分可能是 click 與 tap 的差異；tap 探測只做了 7 種元件各前 8 個觸發元件，其他元件的「tap 開法」是由 click 配方通過推斷，沒有用 tap 驗證，標為未確認。
- 在 tablet 上 combobox／chosenbox 的文字輸入路徑（empty-search message、create）未確認：click 在 input 上沒有展開，是否 readonly 沒有查。
- `z-menuitem-selected`、`z-selected`、`z-bandpopup-content`、`z-colorpalette-button`、`z-mask`、`z-window-modal`、`z-loadingbar-position-bottom` 沿用桌面量測的 (?) 標記：在 tablet 上同樣沒有量到；ROD 三個 loading class 因該三個測試沒有 instrumentation，只知道配方通過。
- `pv/popup-api-fixtures.zul` 在 tablet UA 的文件寬度 1001x1335 超出 834x1112 viewport（探測中量到）；此頁用於 tablet 測試前要先處理，原因未查。
- 量測是單次執行，沒有重跑；只判斷「有沒有畫出來」，不比較像素。
- 與桌面的幾何差異只量了 tablet 一側；桌面的盒子尺寸沒有重量（例如 daterangebox 756 寬在桌面是否相同：未確認）。

## 6. 清理

- 三個 spec 與 `popup-gallery.spec.ts` 已 `git checkout` 還原；`_instr.ts`、`_tablet-popup.config.ts` 已移出 repo。
- 伺服器：只 kill 記錄的 PID（41810、41811、44394；bash 41807 隨之結束）；8087 無 LISTEN；8085（PID 33534）未動；Gradle daemon 沒有動；沒有使用 `pkill`/`killall`。
- `git -C p3/zk status --porcelain`：只有 untracked `node_modules`、`zkpreview/node_modules`；`git -C p3/zkcml status --porcelain`：空。
