# 批次 5 報告：tablet 彈出層截圖（D37 = A）

日期：2026-10-20。工作位置：`/Users/hawk/Documents/workspace/ZK10/p3/zk`（分支 `gaps-round2`，HEAD `921f92506d`）與 `/Users/hawk/Documents/workspace/ZK10/p3/zkcml`（`fe6323380`）。沒有 commit；沒有改 Marble CSS（突變都已 `git checkout` 還原）。依據：`doc/marble-test-gaps-round2-plan.md` §12、`doc/marble-tablet-popup-coverage-measure.md`。

## 1. 摘要

- 新增 16 個測試、16 張基準 PNG，放在 `tablet` Playwright project 的 `tablet.spec.ts` 新 describe `tablet-popup`。
- 每個測試都用 `page.touchscreen.tap` 開啟彈出層，沒有任何一個用 widget API 開。
- `tablet` 全部 74 個（原 58 + 新 16）在不同 process 連跑 2 次全過，另在乾淨重建後再跑 1 次全過；`popup` 56 個全過；既有 PNG 沒有變動。
- 12 個突變（11 個元件，window 重做一次）：11 個被新測試抓到，每個只影響該元件的測試；window 的 `.z-window-header` 突變存活（該規則是 no-op，見 §6）。
- 與量測的偏差（需要你知道）：treecol 欄選單**無法用使用者操作開啟**，改測 grid 與 listbox 的欄選單（§3 與 §6）。

## 2. 決定：新 describe，不開新 spec

放在 `tablet.spec.ts` 的新 describe `tablet-popup`。理由：

1. `tablet` project 的 `testMatch` 是 `/tablet\.spec\.ts/`，新 spec 檔要改 `playwright.config.ts`；`select-tests.mjs` 對 `playwright.config.ts` 的變更回答 `full`。
2. 可重用 `tablet.spec.ts` 已有的 `tabletCssLoaded` 等慣例，並與既有 tablet 基準集中在同一處。
3. 代價：`tablet.spec.ts` 增加約 290 行；`popup-gallery.spec.ts` 的 helper 沒有 export（import 會註冊它的測試），所以在 `tablet.spec.ts` 內重寫 `settledBox`、`tabletPopupShot` 等精簡版（逐項對應桌面慣例，見 §4）。

標題規則：每個標題含 `<page>.zul:`（fixture 頁的另加 `(pv/...)`），這是 `select-tests.mjs` 的 `titleNamesPage` 讀取的依據（`scripts/README-select-tests.md`）。

## 3. 各元件結果

開法欄全部是 `touchscreen.tap`（`tapCentre`：捲入視野後點元素中心）。基準名稱 `<page>-tablet-popup-<state>.png`，不與既有 `<page>-tablet.png` 衝突；`select-tests.mjs` 把它對應到該頁（已驗證 combobox、confirmpopup 兩張）。

| # | 測試（`tablet-popup`） | 開法 | 斷言（使它成為 tablet 版） | 基準 |
|---|---|---|---|---|
| 1 | `combobox.zul: tap the arrow to open the bottom sheet` | tap `.z-combobox-button` | `.z-combobox-popup` 唯一可見；`position:fixed`、`left/right` 10px、`bottom` 0；底邊貼 viewport 底（±2）；寬 = viewport − 20；每個 `.z-comboitem` 高 ≥ 44 | `combobox-tablet-popup-sheet.png` |
| 2 | `combobox.zul: the bottom sheet of items with a description` | tap「With description」的箭頭 | 同上；DOM 內有 `z-comboitem-inner` | `combobox-tablet-popup-description.png` |
| 3 | `datebox.zul: tap the button to open the date wheel` | tap「Seeded value」的 `.z-datebox-button` | 底部 sheet 同 #1；`.z-calendar-wheel-cave` 1 個、`td` 0 個（不是桌面格狀月曆）；`.z-calendar-wheel-button` ≥ 44 | `datebox-tablet-popup-wheel.png` |
| 4 | `datebox.zul: the date wheel with the time in the popup` | tap「Time in popup」的按鈕 | 同 #3；`.z-calendar-wheel-cave .z-timebox-wheel-time` 1 個（時間 wheel 內嵌） | `datebox-tablet-popup-wheel-time.png` |
| 5 | `timebox.zul: tap the button to open the time wheel` | tap 第一個 `.z-timebox-button`（有種子值） | 底部 sheet；`.z-timebox-wheel-*` 類別在 DOM；wheel button ≥ 44 | `timebox-tablet-popup-wheel.png` |
| 6 | `colorbox.zul: tap to open the palette` | tap `.z-colorbox` | `.z-colorbox-popup` 唯一可見、`position:absolute`、父節點 BODY、靜止後底邊貼 viewport 底 | `colorbox-tablet-popup-palette.png` |
| 7 | `colorbox.zul: tap the picker icon to switch the popup to the picker` | tap 第 2 個 colorbox，再 tap `.z-colorbox-pickericon` | `.z-colorpicker` 可見 1 個、可見色塊 0 個 | `colorbox-tablet-popup-picker.png` |
| 8 | `menubar.zul: tap a menu to open its pop-up` | tap「Project」 | `.z-menupopup` 唯一可見；每個可見 `.z-menuitem-content` ≥ 44 | `menubar-tablet-popup-open.png` |
| 9 | `menubar.zul: tap a menu with a checked menuitem` | tap View、tap「Sort by Name」、再 tap View | 同 #8；`.z-menuitem-checked .z-menuitem-icon` ≥ 24x24 | `menubar-tablet-popup-open-checked.png` |
| 10 | `combobutton.zul: tap the arrow to open the menu` | tap `.z-combobutton-button` | 箭頭 ≥ 44x44；menuitem ≥ 44 | `combobutton-tablet-popup-open.png` |
| 11 | `grid.zul: tap the column menu button to open the column menu` | tap `.z-column-button` | 按鈕 ≥ 44x44；可見 menuitem ≥ 44 | `grid-tablet-popup-column-menu.png` |
| 12 | `listbox.zul: tap the header menu button to open the header menu` | tap `.z-listheader-button` | 同 #11 | `listbox-tablet-popup-header-menu.png` |
| 13 | `errorbox.zul: tap a field, leave it empty and tap away to open the errorbox` | tap 欄位、鍵盤輸入再刪除（代表軟鍵盤）、tap 空白處 | `.z-errorbox.z-errorbox-open` 唯一可見；`.z-errorbox-close` ≥ 24x24 | `errorbox-tablet-popup-open.png` |
| 14 | `notification.zul: tap Info to show a notification` | tap「Info」按鈕 | `.z-notification-open` 唯一；`.z-notification-close` ≥ 24x24 | `notification-tablet-popup-info.png` |
| 15 | `window.zul: tap to open a modal window (pv/popup-api-fixtures.zul)` | tap「Open modal window」 | scrim 蓋滿 viewport；header ≥ 56；`.z-window-icon` ≥ 44x44 | `window-tablet-popup-modal.png` |
| 16 | `confirmpopup.zul: tap to open the confirm pop-up (pv/popup-tablet-fixtures.zul)` | tap 新 fixture 的「Delete item」 | `.z-confirmpopup-ok`、`-cancel` ≥ 44 | `confirmpopup-tablet-popup-open.png` |

### 與指示的偏差

1. **treecol 欄選單換成 grid（#11）與 listbox（#12）的欄選單。** 讀了 source：`Treecols.java` 沒有 `menupopup` 屬性（`<treecols menupopup="auto">` 會拋 `PropertyNotFoundException: Method setMenupopup not found for class org.zkoss.zul.Treecols`，已實測）；`sel/Treecol.ts` 沒有像 `grid/Column.ts`、`sel/Listheader.ts` 那樣對 `-btn` 綁 `onClick`。所以 `z-treecol-button` 沒有使用者路徑，`_mesh.css` 對它的規則今天沒有作用。量測文件 §4.1「treecol 設 menupopup」的前提不成立。`_mesh.css` 同一規則涵蓋的 `.z-column-button`、`.z-listheader-button` 有 tap 路徑，所以改測這兩個，共 2 個測試（總數仍在 15–16 範圍：16）。
2. **colorbox 沒有任何 API 開法。** 量測看不到 `openPopup()` 後的 popup class，原因已查明：popup 是 bottom sheet，`openPopup()` 設 `top: <viewport 高>`、`transform: translate(0, -318px)` 並帶 `transition: transform 300ms`，立即讀取時盒子在 y = 1112（viewport 外）；實測 0 ms、50 ms 讀到 y = 1112、1100，300 ms 後在 y = 794。`display`、`visibility`、`opacity` 一直是可見值。tap 開啟加 `settledBox` 就能穩定截到（`tablet.spec.ts` 該測試前有註解）。
3. **confirmpopup 需要 fixture。** `confirmpopup.zul` 只有靜態標記，沒有真的 component；新增 `pv/popup-tablet-fixtures.zul`（一個按鈕 + `<confirmpopup>`，按鈕下移 200px 讓向上展開的 popup 放得下）。

## 4. 沿用的桌面慣例

- `PAD` 12，截圖 clip 取彈出層盒子加 PAD，並限制在 viewport 內（bottom sheet 下方沒有空間）。
- 精確一個可見：`.<popup>:visible` 的 `toHaveCount(1)`，不用 `.first()`。
- 截圖前斷言要畫的 class 都在 DOM 內。
- `settledBox`：等盒子連續 3 次讀取相同（sheet 有 JS 滑入動畫）。
- 計算樣式斷言：用 `toHaveCSS` 讀 `position`、`left`、`right`、`bottom`，以及盒子尺寸（不貼字串）。
- notification 的 3000 ms 自動關閉計時器：沿用 `popup-gallery.spec.ts` 的作法（`addInitScript` 丟掉恰為 3000 ms 的 `setTimeout`）。
- 不 park cursor：觸控測試沒有 `mouse` 動作，3 個 process 的雜訊為 0 px（§5a）。

## 5. 修正 fixture：`pv/popup-api-fixtures.zul`

- 變更：第一個 flex 列加 `z-flex-wrap`（`sclass="z-p-8 z-d-flex z-flex-wrap z-gap-8"`），並在檔頭註解補一句原因。
- 量測：tablet 834 寬 viewport 的 `scrollWidth` 原為 1001（量測文件值），修正後 834 = `clientWidth`，第 4 個按鈕換行（y 133）；桌面 1280 寬下四個格子仍在同一列（y 32/32），版面不變。
- 證明桌面基準不變：`popup` project 56 個全過；`window-popup-modal.png`、`loadingbar-popup-bottom.png` 另用 `POPUP_MAX_DIFF=0`（容忍 0 px）單獨跑，2 個全過（`-g "open a modal window|a bar at the bottom of the window"`）。
- 新增 `pv/popup-tablet-fixtures.zul`（見上）。兩個 fixture 都在 `pv/` 下，`gallery-scan` 與 `forced-colors-gallery` 不會取用。

## 6. 證據 a–f

### a. 雜訊與容忍值

首次以 `--update-snapshots=missing` 寫入 16 張後，`POPUP_MAX_DIFF=0`（每張容忍 0 px）在 3 個獨立 process 各跑 16 個：3 次皆 16 passed，0 個差異像素。容忍值定為與桌面彈出層相同的絕對值 10 px（`maxDiffPixels: 10`，每張另加 `threshold: 0.05`，與 `popup` project 的 `expect.toHaveScreenshot.threshold` 相同；沒有改 `playwright.config.ts`）。10 px 是餘裕，不是量測到的雜訊；`POPUP_MAX_DIFF` 環境變數可覆寫它做量測。這只代表在預算內，不代表與原始碼一致。

### b. 穩定性

| 指令 | 結果 |
|---|---|
| `tablet`（不同 process）第 1 次 | 74 passed（1.3 分） |
| `tablet` 第 2 次 | 74 passed（1.3 分） |
| 還原突變並重建 zul、zkmax、zkex jar 後 `tablet` 再一次 | 74 passed |
| `popup`（`--update-snapshots=none`，`--workers=2`） | 56 passed（1.6 分） |

每次跑完 `git status --porcelain zkpreview/doc/screenshots` 只有 16 個 untracked 新 PNG，既有 PNG 沒有修改。`forced-colors-gallery` 沒有跑，沒有 review PNG 被改寫。

### c. 突變（一次一個：改 → `compileMarbleCss` + jar → 重啟 → 跑 `tablet-popup` 16 個 → 還原）

建置指令：zkcml 為 `:zkmax:compileMarbleCss :zkmax:jar :zkex:jar`（`bash ./gradlew` 在 `withjdk.sh 17` 下），每次 14–20 秒。結果表中「失敗」皆為該元件自己的測試，其餘 14–15 個通過。

| 突變 | 檔案與規則 | 失敗的測試 | 失敗方式 |
|---|---|---|---|
| M1 combobox | `_inputs.css` `.z-combobox-popup` `inset: auto 10px 0` → `auto 20px 0` | combobox 2 個（#1、#2） | 截圖 |
| M2 datebox | `_wheel.css` `.z-datebox-popup .z-calendar:has(.z-calendar-wheel-cave)` `max-width` 420 → 300px | datebox 2 個（#3、#4） | 截圖；timebox 不受影響 |
| M3 timebox | `_wheel.css` `.z-timebox-wheel-cave` `max-width` 420 → 300px | timebox 1 個（#5） | 截圖 |
| M4 colorbox | zkex `colorbox.css` `.z-colorpalette-color` 16 → 18px | colorbox palette（#6） | 截圖；picker（#7）不受影響 |
| M5 menu | `_menu.css` `.z-menuitem-image` `min-width/height` 24 → 40px | menubar 開啟（#8） | 截圖；checked（#9）沒有圖片，不受影響 |
| M6 combobutton | `_buttons.css` `.z-combobutton-button` `width` 44 → 32px | #10 | 尺寸斷言（≥ 44） |
| M7 errorbox | `_feedback.css` `.z-errorbox-close` 24 → 18px | #13 | 截圖 |
| M8 notification | `_feedback.css` `.z-notification-close` 24 → 18px | #14 | 截圖 |
| M9 window header | `_window.css` `.z-window-header` `min-height` 56 → 40px | **無（突變存活）** | 見下 |
| M9b window icon | `_window.css` `.z-window-icon, .z-panel-icon` 44 → 30px | #15 | 截圖 |
| M10 grid 欄選單 | `_mesh.css` 從群組刪 `.z-column-button` | #11 | 尺寸斷言 |
| M11 listbox 欄選單 | `_mesh.css` 從群組刪 `.z-listheader-button` | #12 | 尺寸斷言 |
| M12 confirmpopup | `_buttons.css` 從群組刪 `.z-confirmpopup-ok` | #16 | 截圖 |

M9 存活的原因：`--zk-touch-target-comfortable` 是 56px，與 Marble 桌面 `.z-window-header` 的 56px 相同，所以 `_window.css` 的 `.z-window-header { min-height }` 在 tablet 上是 no-op，沒有可測的差異。測試仍保留 header ≥ 56 的斷言（它檢查結果，不檢查規則）。`.z-panel-header` 規則（desktop 48px）有作用，但這批沒有 panel 彈出層。

### d. 選擇器

以暫時改規則（改完即還原）搭配 `node scripts/select-tests.mjs --json --base HEAD --zkcml-base HEAD --only <path>`；`--only` 對沒有 diff 的檔案回 `none`，所以要真的改一條規則。10 個編輯，全部選到新測試：

| 路徑（`zkcml:` 前綴省略） | 選到的 `tablet-popup` 測試 |
|---|---|
| `zkmax/.../tablet/_wheel.css`（datebox 規則） | datebox 2 個 |
| `_wheel.css`（timebox 規則） | datebox 2 個 + timebox 1 個 |
| `_inputs.css` | combobox 2 個 + window 1 個（`pv/popup-api-fixtures` 與 combobox 同頁映射） |
| `_menu.css` | colorbox 2、menubar 2、combobutton、grid、listbox |
| `_feedback.css`（errorbox 規則） | errorbox 1 個 |
| `_feedback.css`（notification 規則） | notification 1 個 |
| `_window.css` | window 1 個 |
| `_buttons.css`（confirmpopup 群組） | colorbox 2、menubar 2、combobutton、notification、window、confirmpopup |
| `_mesh.css` | grid、listbox |
| `zkex/.../inp/css/colorbox.css` | colorbox 2 個 |

另驗：新 PNG（`combobox-tablet-popup-sheet.png`、`confirmpopup-tablet-popup-open.png`）選到對應頁；兩個 fixture `.zul` 各選到使用它們的測試。選不到的：`_mesh.css` 的 `.z-treecol-button` 規則沒有測試可選，因為該規則沒有使用者路徑（§3 偏差 1）。

### e. 16 張 PNG（逐張打開）

1. `combobox-tablet-popup-sheet.png`（834x292）：滿版寬的白色 bottom sheet，兩個項目 Item 1、Item 2，下方大片空白。
2. `combobox-tablet-popup-description.png`（834x292）：同一個 sheet，兩個項目各有灰色描述行。
3. `datebox-tablet-popup-wheel.png`（834x216）：三欄 wheel（2025／Jan／15）、置中選取帶、OK（藍）與 Cancel 兩個圓角按鈕。
4. `datebox-tablet-popup-wheel-time.png`（834x216）：日期三欄加時間兩欄（00／00），兩段選取帶在中間相接。
5. `timebox-tablet-popup-wheel.png`（834x216）：四欄（10／30／00／AM），OK 與 Cancel。
6. `colorbox-tablet-popup-palette.png`（382x330）：調色盤 popup，上方切換圖示、色塊預覽與 `#000000` 輸入，下方 18 列色塊格。
7. `colorbox-tablet-popup-picker.png`（352x490）：色彩選取器，漸層方塊、色相條、RGB／HSV／HEX 輸入與確認按鈕。
8. `menubar-tablet-popup-open.png`（184x307）：Project 選單，含圖示的 New、Open、Save、灰色 Save As…、分隔線、Exit、Restart。
9. `menubar-tablet-popup-open-checked.png`（184x122）：View 選單，Sort by Name 前有藍色勾號，Sort by Date 無。
10. `combobutton-tablet-popup-open.png`（184x122）：兩個項目 Edit、Delete 的選單。
11. `grid-tablet-popup-column-menu.png`（184x351）：欄選單，Group、Sort Ascending、Sort Descending、分隔線與 4 個打勾的欄名。
12. `listbox-tablet-popup-header-menu.png`（184x122）：標題選單，Name、Role 兩個打勾項。
13. `errorbox-tablet-popup-open.png`（284x98）：紅色 errorbox，左側箭頭指向欄位，警告圖示、兩行訊息、右側關閉鈕。
14. `notification-tablet-popup-info.png`（300x60）：淡藍色 info notification，圓形 i 圖示、訊息、右側灰色大關閉鈕。
15. `window-tablet-popup-modal.png`（344x187）：Modal window，標題 Modal window、右側大關閉鈕、分隔線、內容文字，周圍是變暗的頁面。
16. `confirmpopup-tablet-popup-open.png`（221x186）：標題 Delete item?、紅色叉圖示與訊息、Cancel（外框）與 OK（藍）按鈕，下方箭頭指向按鈕。

### f. 清理

- 伺服器：只用 `tail -f /dev/null` 持有 stdin 的 8087 伺服器；每次停止只 kill 記錄的 PID（最後一組 TAIL 84061、CLIENT 84062、JETTY 84906），均已不在；`lsof -iTCP:8087` 無 LISTEN；8085（PID 33534）、8105 沒有動；沒有使用 `pkill`/`killall`；Gradle daemon 沒有動。
- 突變已全部 `git checkout` 還原，並以乾淨樹重建三個 jar 後跑 `tablet` 74 個全過。
- p3/zkcml：`git status --porcelain` 空；p3/zk：兩個修改檔（`pv/popup-api-fixtures.zul`、`tablet.spec.ts`）、新 fixture、16 張新 PNG，加上原有 untracked 的 `node_modules`、`zkpreview/node_modules`。沒有 staged 內容。
- 暫存探測腳本在 `/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/7d5b9c66-28ef-4caf-8453-4b21d3a621a0/scratchpad/b5/`；沒有放進 repo。
- 還有一件事：我手動把兩個 fixture 複製到 `zkpreview/build/inplaceWebapp/web/pv/`（該目錄是 build 輸出，gretty 的 inplace 複製只新增不刪除）；內容與 source 相同（`diff -r` 一致）。

## 7. 可能的缺陷（只列觀察；沒有改任何 CSS）

1. **colorbox picker 底部被裁切（tablet）。** `colorbox-tablet-popup-picker.png` 的 HEX 輸入框下緣被 popup 底邊切掉（約 4px），RGB 列比桌面 `colorbox-popup-picker.png` 低約 4px；桌面版 HEX 框完整。原因未確認：popup 的 inline style 為 `height:auto; overflow:auto`（見 §3 偏差 2 的實測），可能與 tablet 的 sheet 錨定高度有關；沒有往 CSS 追到行號。基準圖照目前畫面錄下。
2. **combobox sheet 高度與內容不符。** `.z-combobox-popup` 實測 280px 高，兩個項目各 60px，下方約 160px 空白（`combobox-tablet-popup-sheet.png`）。`_inputs.css:104` 的註解說高度由 JS 設為 `_items × _itemHeight`；為何不是 120px 未確認。
3. **colorbox sheet 沒有像 combobox／datebox 那樣固定在底部且滿版。** 它是 `position:absolute`、358px 寬、x = 238（不在 `_inputs.css` 與 `_wheel.css` 的規則內），靠 ZK 的 `top = innerHeight` 加 transform 對齊底邊；頁面捲動時是否仍貼齊未測（單一次在 scrollY = 400 的探測，頁面本身可能不可捲，沒有結論）。
4. **`_window.css:` `.z-window-header { min-height: var(--zk-touch-target-comfortable) }` 在 tablet 上是 no-op**（見 M9）：桌面本來就是 56px。
5. **`_mesh.css:38` 的 `.z-treecol-button` 是無使用者路徑的死規則**（Treecols 沒有 `menupopup` 屬性，`Treecol.ts` 沒有按鈕 click 監聽），見 §3 偏差 1。建議交給核心團隊判斷是 tablet 規則多餘，或 Treecols 缺功能；我沒有建 Jira。

## 8. 限制（未確認項）

- 未確認：colorbox picker 底部裁切與 combobox sheet 高度的原因（§7 1、2）。
- 未確認：iOS Safari 的實機表現；所有結果都是 Chromium 以 iPad UA、834x1112、`hasTouch`、`isMobile` 模擬。`ZK` 在 iOS 用 `window.innerHeight`，模擬環境只驗了 `tablet.spec.ts` 既有註解提到的 18px 差異。
- 未確認：datebox 的 timezone 選擇器、`z-datebox-timezone` 在 tablet 是否出現（本批沒有測，量測文件也標未確認）。
- 沒有測：與桌面同形的 23 個彈出層（bandbox、chosenbox、cascader 等，量測 §4.2），沒有 tablet CSS 作用其上；也沒有測 errorbox 的翻轉位置、notification 的錨點變體、combobox 的 empty-search 輸入路徑。
- 沒有測：獨立 Opus verifier 看全部新圖與 commit 同意，是計畫 §12 的後續步驟，不在本批範圍。
- 沒有跑 `npm run lint` 與 `type-check`：改動的 `tablet.spec.ts` 與 `.zul` 不在 `<module>/src/main/resources/web/js` 的 lint 範圍；Playwright 以 esbuild 轉譯，不做型別檢查。
- 10 px 容忍值沿用桌面慣例，不是從這 16 張的雜訊推出（雜訊為 0）。
- `popup` 的 56 個是 `--workers=2` 的單次執行。
- 測試 #16 的選擇器會同時選到 7 個與 `popup-tablet-fixtures.zul` 相關的測試（`select-tests.mjs` 以來源行映射，保守偏多），沒有深究。

## 9. 補修（驗證者發現）

依 `doc/marble-tablet-popup-batch5-verification.md` 只修測試側（F1、F7、F8、F2 註解）；Marble CSS 缺陷 F2–F6、F9 沒有碰。

- F1 原始雜訊：`POPUP_THRESHOLD=0 POPUP_MAX_DIFF=0`（threshold 0、容忍 0 px），3 個獨立 process 各跑 16 個，皆 16 passed，0 個差異像素（含重切的 confirmpopup 基準）。所以 0.02 下雜訊也是 0，沒有需要放寬的 shot。
- `tabletPopupShot` 的 `threshold` 由 0.05 改為 0.02（與 `brand`、`compact` 相同），可用 `POPUP_THRESHOLD` 覆寫；`maxDiffPixels` 仍是 10（量測為 0，沿用桌面餘裕，沒有放寬）。
- Vb2 突變（`_feedback.css` 的 `.z-notification-close` 加 `background-color: color-mix(in srgb, var(--zk-color-on-surface) 8%, transparent)`）：改後重建、重啟、跑 `tablet-popup`，`notification.zul: tap Info …` 失敗（截圖不符），其餘 15 個通過；已還原。
- F7：丟棄 3000 ms timer 的 `addInitScript` 從 `beforeEach` 移到只有 notification 測試。
- F8：`pv/popup-tablet-fixtures.zul` 的按鈕容器加 `z-d-flex z-justify-center`；刪除並以 `--update-snapshots=missing` 重切 `confirmpopup-tablet-popup-open.png`（221x186 → 重切後打開檢查：左右都有 PAD，popup 完整，箭頭指向按鈕）。
- F2：picker 測試前加註解，說明基準錄下已知 Marble 缺陷（列重疊 5px、HEX 切 4px，zkex `colorbox.css` top 0/43/86），修正後須重切。
- 變更檔：`zkpreview/src/test/playwright/tablet.spec.ts`、`zkpreview/src/main/webapp/web/pv/popup-tablet-fixtures.zul`、`confirmpopup-tablet-popup-open.png`。
- 驗證：還原突變並重建 jar 後，`tablet` 2 次（不同 process）各 74 passed；`popup` 56 passed；既有 PNG 無變動。
- 清理：只停記錄的 PID（TAIL 87901、CLIENT 87902、JETTY 88590），8087 無 LISTEN，8085 未動；p3/zkcml 乾淨；p3/zk 只有 2 個修改檔、1 個新 fixture、16 張新 PNG 與原有 `node_modules`；無 staged。
