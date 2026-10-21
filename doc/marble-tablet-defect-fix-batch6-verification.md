# 批次 6 獨立驗證：tablet 彈出層缺陷 F2、F4、F5、F6、F9（follow-up 65，ZK-6112）

日期：2026-10-20。驗證者：獨立 verifier（不是實作者）。範圍：F2、F4、F5、F6、F9；F3 不在範圍內。
受驗對象：`/Users/hawk/Documents/workspace/ZK10/p3/zk`（`gaps-round2`，HEAD `237bfa8586`）與 `/Users/hawk/Documents/workspace/ZK10/p3/zkcml`（`gaps-round2`，HEAD `fe6323380`）的未 commit 改動。實作紀錄：`doc/marble-tablet-defect-fix-batch6.md`。
本檔是唯一寫入 repo 的產物；探針、log、截圖都在 scratchpad `/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/028a7b57-cfcf-44e9-a25e-5d68a4869b5a/scratchpad/`（下稱 `$S`）。

## 0. 結論

| 項目 | 判定 | 主要證據 |
|---|---|---|
| F2 colorpicker 列重疊 | PASS | baseline PNG 像素：HEAD 三列 top 邊框距 43px、RGB 底邊被蓋、HEX 無底邊；新 PNG 三個 48px 框、間距 3px、HEX 底邊在 picker 內 4px。`<menu content="#color=…">` 的 picker 量到相同數值。桌面 picker 在跨 process 雜訊內相同 |
| F4 combobox sheet 末列被切 | PASS | sheet 300px（前 280px）；12 項以真實 touch 拖曳捲到底後，末項 1052–1112 完整、sheet 仍在 viewport 內（前：末項 1072–1132，切 20px） |
| F5 tablet window／panel header 規則 | PASS | 刪除後 window `min-height` 48→56px（桌面值）、panel 48→48px；所有 header 高度前後相同；圖示 44px 都在 header 內 |
| F6 `.z-treecol-button` 死規則 | PASS | 找不到任何產生者；6 頁 × 2 UA 的 computed style + box 簽章前後相同（tree-header 桌面有一個與 CSS 無關的 463/468px 雙穩態，見 §3.4）；`.z-column-button`／`.z-listheader-button` 的 selector 與宣告逐字相同 |
| F9 datebox wheel-time 接縫 | PASS | baseline PNG：HEAD 在 x 409–424、y 64–103 有白色缺口（top 列 413–420 為 255,255,255）；新 PNG 該處整段是帶色 `232,238,247`，外側圓角保留；兩張 PNG 差異只在這個框內 |

Playwright（全部 `PREVIEW_URL=http://127.0.0.1:8087`、repo config、`--update-snapshots=none`）：`tablet` 74 passed／0 failed／0 skipped；`popup` 56／0／0；tree 頁（chromium `tree gallery`、`tree hover`，gallery `tree-header`）3／0／0；另跑 `mesh-header-footer` + `mesh-header-footer-tablet` 400／0／0；`popup` 的 3 個 colorbox 測試在 `POPUP_MAX_DIFF=0` 下 3／0／0。

突變證明：在 scratch 複本上做了 3 個斷言層突變（F2、F4、F9）與 3 個 PNG 層突變，全部失敗；同一複本未突變時全部通過（§4）。

p3 兩個 repo 的 `git status --porcelain` 與 `git diff --stat` 驗證前後相同（§6）。沒有 staged 內容。

## 1. 方法

- 伺服器：自己啟動的 8087（`p3/zk/zkpreview`，`appRun --no-daemon`）。啟動前先在 p3 重建 `:zul:compileMarbleCss :zul:jar` 與 `:zkmax:compileMarbleCss :zkmax:jar :zkex:jar`（伺服器未執行時），確認 served `tablet.css.dsp` 與 zkmax jar 內檔案 `cmp` 相同，且含 4 條新規則、不含 `treecol-button`；served `zk.wcs` 不含 `treecol-button`。
- 「前」狀態：不動 tracked 檔、不用 stash。以 Playwright `context.route` 攔截 served `tablet.css.dsp`／`zk.wcs`，把本批規則逐字還原成 base 形式（每次替換都斷言恰好命中一次），即 `$S/probe/common.cjs` 的 `TABLET_REVERTS`／`WCS_REVERTS`。另外以 `git show HEAD:<png>` 取出 base 的 baseline PNG，與工作區 PNG 做像素比對（`$S/probe/base/*.HEAD.png`、`*.WT.png`）。
- 幾何判定以 PNG 像素為準（逐列／逐行掃描顏色 run，`$S/probe/scan.cjs`）；`getBoundingClientRect` 數值只作輔助。
- 互動用真實輸入：`page.touchscreen.tap`、CDP `Input.dispatchTouchEvent`（touch 拖曳）、桌面 `page.mouse.click`。
- tablet 環境：Chromium、iPad UA、834x1112、`hasTouch`、`isMobile`（同 `playwright.config.ts` 的 `TABLET_USE`）。所有 tablet 探針都確認 `zkmax/css/tablet.css` 在 `document.styleSheets` 內；桌面探針確認不在。

## 2. 改動內容（讀過兩邊 `git diff`）

| 缺陷 | 檔案 | 規則做的事 |
|---|---|---|
| F2 | zkcml `tablet/_inputs.css` | `.z-colorpicker-hsv { top: calc(var(--zk-touch-input-height) + 3px) }`、`.z-colorpicker-hex { top: calc(2 * (… + 3px)) }`、`.z-colorpicker { height: calc(460px + 3 * (… - 40px)) }`：三列依 48px 輸入框重排、保留 3px 列距，picker 加高 24px |
| F4 | zkcml `tablet/_inputs.css` | 既有 `.z-combobox-popup` 規則加 `max-height: none`，蓋掉桌面 `combobox.css` 的 280px |
| F5 | zkcml `tablet/_window.css` | 刪 `.z-window-header`、`.z-panel-header` 的 `min-height: var(--zk-touch-target-comfortable)`；改檔頭註解 |
| F6 | zkcml `tablet/_mesh.css`、zk `sel/css/tree.css` | 群組 selector 刪 `.z-treecol-button`；刪 `.z-treecol:has(.z-treecol-button) .z-treecol-sorticon`；兩條 checkable 規則 `:not(.z-treecol-sort, :has(.z-treecol-button))` → `:not(.z-treecol-sort)` |
| F9 | zkcml `tablet/_wheel.css` | 有 time 帶時，date 帶右側兩角、time 帶左側兩角 radius 設 0 |
| 測試 | zk `tablet.spec.ts` | 新 helper `expectSheetKeepsJsHeight`（兩個 combobox 測試）、datebox 接縫斷言、colorbox picker 三列不重疊斷言；移除 known-defect 註解 |
| baseline | 4 張 PNG | `colorbox-tablet-popup-picker`（352x490→352x514）、`combobox-tablet-popup-sheet`、`combobox-tablet-popup-description`（834x292→834x312）、`datebox-tablet-popup-wheel-time`（834x216，尺寸不變） |

沒有新增 `!important`、`--zk-*` token。

## 3. 逐項量測

### 3.1 F2 — PASS

baseline PNG（`colorbox-tablet-popup-picker.png`，x=107 與 x=114 兩條垂直掃描，結果相同；y 為 PNG 內座標）：

| | 出現的邊框列（196 灰） | 解讀 |
|---|---|---|
| HEAD（352x490） | 351、394、441；489 為 popup 邊 | RGB 頂 351；HSV 頂 394（距 43）；RGB 底（應在 398）不可見＝被 HSV 蓋住；441 是 HSV 底，HEX 頂（應在 437）不可見；HEX 底邊不存在（被 `overflow: hidden` 裁掉） |
| 工作區（352x514） | 351、398、402、449、453、500；513 為 popup 邊 | 三個框各 48px（351–398、402–449、453–500），列間 3px 空白；HEX 底 500 之後到 popup 邊仍有空間 |

live 探針（`$S/probe/f2.cjs`，route 還原 F2 作為「前」），picker 內截圖垂直掃描（picker 內座標）：

| | picker 高 | 邊框列 | RGB／HSV／HEX 輸入框 top–bottom（頁面座標） |
|---|---|---|---|
| colorbox.zul 前 | 460 | 330、373、420 | 973–1021／1016–1064／1059–1107，picker 底 1103：重疊 5px、HEX 超出 4px |
| colorbox.zul 後 | 484 | 330–377、381–428、432–479 | 949–997／1000–1048／1051–1099，picker 底 1103：間距 3px、HEX 在內 4px |
| `pv/colorbox-content.zul` 的 `<menu label="Quick Colour" content="#color=#2e7d32">`，點 `.z-menu-pickericon`，前 | 460 | 330、373、420 | 674–722／717–765／760–808，picker 底 804：重疊 5px、HEX 超出 4px |
| 同上，後 | 484 | 330–377、381–428、432–479 | 674–722／725–773／776–824，picker 底 828：間距 3px、HEX 在內 4px |

menu 的 `.z-menu-popup` 隨 picker 長高（前 478、後 502px，`overflow: visible`，`scrollHeight` = `clientHeight`），picker 沒有被 menu popup 裁切。截圖：`$S/probe/out/colorbox-tablet-{before,after}.png`、`menu-tablet-{before,after}.png`、`menu-popup-after.png`。

桌面不變（桌面 UA，`tablet.css` 不在 `document.styleSheets`）：

| 比對 | 結果 |
|---|---|
| colorbox.zul 桌面 picker：目前 CSS vs 全部還原（F2+F4+F5+F6+F9，含 zk.wcs） | PNG 位元組相同（0 px） |
| menu picker 桌面：目前 vs 全部還原 | 1 px、maxDelta 1；兩次都用目前 CSS 的兩個 process 之間也量到 1 px、maxDelta 1（跨 process 雜訊，在 `verification.md` 的 floor `maxDelta ≤ 8`、`diffPixels ≤ 64` 內） |
| 桌面三列幾何 | 前後都是 581–621／624–664／667–707，picker 底 711（colorbox.zul）；menu 676–716／719–759／762–802，底 806 |
| `popup` project 3 個 colorbox 測試，`POPUP_MAX_DIFF=0`（threshold 0.05） | 3 passed，對未改動的桌面 baseline |

### 3.2 F4 — PASS

探針 `$S/probe/f4.cjs`。臨時頁 `build/inplaceWebapp/web/b6v-f4-probe.zul`（12 項與 3 項的 combobox；只放在 inplaceWebapp，量完已刪，舊 URL 回 404）。

| 情境 | 前（route 還原 F4） | 後 |
|---|---|---|
| combobox.zul 第一個一般 combobox | sheet 832–1112＝280px，inline `height: 300px`，`max-height: 280px` | 812–1112＝300px，`max-height: none` |
| 12 項，開啟時 | 完整可見 4 項（01–04） | 完整可見 5 項（01–05） |
| 12 項，4 次 touch 由下往上拖曳後（CDP `Input.dispatchTouchEvent`） | 末項 1072–1132，比 sheet／viewport 底（1112）多 20px；完整可見 08–11 | 末項 1052–1112 完整；完整可見 08–12；sheet 仍是 812–1112；`scrollY` 0 |
| 3 項 | sheet 280px | sheet 300px（JS 固定 5 × 60px） |

所以 ≥10 項時清單在 sheet 內捲動，sheet 不超出 viewport。PNG：`$S/probe/out/f4-12-crop-{before,after}.png`（後：sheet 上緣陰影從 y 808 開始，前為 828；Item 12 前被底邊切掉下半，後完整）。committed baseline：`combobox-tablet-popup-sheet.png`、`-description.png` 由 834x292（280 + 12 pad）變 834x312（300 + 12 pad）。

### 3.3 F5 — PASS

探針 `$S/probe/f569.cjs f5`，`/window.zul`、`/panel.zul` 所有可見 header：

| | window header 高度 | window `min-height` | panel header 高度 | panel `min-height` |
|---|---|---|---|---|
| tablet 前 | 57, 56, 77, 76, 77, 76, 77, 76 | 48px | 56, 104×6, 56, 76, 76 | 48px |
| tablet 後 | 相同 | 56px | 相同 | 48px |
| 桌面前後 | 57, 56, 65, 64, 65, 64, 65, 64 | 56px | 56, 80, 60, 80, 80, 60, 80, 56, 60, 60 | 48px |

tablet 的 header 圖示前後都是 44x44，位於 header 內（window：上 16、下 16–17px；panel：上下各 30px）。`tablet` project 的 window／panel 相關截圖（含 `window-tablet-popup-modal.png`）在 74 個測試內通過。
附帶觀察（非本批改動造成、前後相同）：tablet 上 panel.zul 有 6 個 header 高 104px，桌面同位置是 80px。

### 3.4 F6 — PASS

產生者搜尋（p3/zk 與 p3/zkcml，`git grep`）：
- `treecol-button`／`treecol-btn` 只出現在文件（`.claude/skills/zk-component-rules/components/data-components.md:164`、`doc/marble-theme-followups.md:1285`）與 `zkpreview/src/test/playwright/mesh-header-footer.spec.ts:376`（obstacle selector，讀取端，非產生者）。zkcml 0 筆（含 za11y、zkmax、zkex、client-bind）。
- `-btn`／`$s('button')` 唯一輸出點 `zul/.../mesh/HeaderWidget.ts:746`，條件是 `this.parent._menupopup && != 'none'`；`_menupopup` 只在 `ColumnMenuWidget.ts`（`Columns`、`Listhead` 的父類）被設定。`Treecols extends zul.mesh.HeadWidget`，不是 `ColumnMenuWidget`；`Treecols.java` 沒有 menupopup（`Columns.java`、`Listhead.java` 有）。
- Treecol 的 mold `sel/mold/treecol.js` 只輸出 `-cave`、`-sorticon`、`-sort-icon`，沒有 button。zkcml 對 Treecol 的 augment（`zkmax/touch/treecol-touch.ts`、`zkmax/sel/tristate-tree.ts`、`za11y/zul/sel-a11y.ts`）與 client-bind 的 Treecols override 都不輸出 button。
- live DOM：下表 6 頁 × 2 UA 的 `.z-treecol-button` 數量都是 0。

selector 比對（base `_mesh.css` vs 工作區，去註解後展開每個 selector）：唯一差異是刪掉 `.z-treecol-button => width/height/font-size` 那一行；其餘 8 個含 `column-button`／`listheader-button` 的 selector 與宣告逐字相同，群組位置不變。tree.css diff 中沒有任何 `column-button`／`listheader-button`。specificity：`:not(.z-treecol-sort, :has(.z-treecol-button))` 取參數最大者 (0,1,0)，`:not(.z-treecol-sort)` 也是 (0,1,0)。

computed style + box 簽章（所有 `[class*=z-treecol]`、`[class*=z-column]`、`[class*=z-listheader]` 的 padding、margin、left/right、width/height、display、text-align、font-size 與 bounding box；`$S/probe/out/f6-{tablet,desktop}.json`）：

| 頁面 | treecol／column／listheader button 數 | 元素數 | tablet 前後 | 桌面前後 |
|---|---|---|---|---|
| tree.zul | 0／0／0 | 71 | 相同 | 相同 |
| tree-header.zul | 0／0／0 | 134 | 相同 | 第一次比對不同（見下） |
| pv/mesh-header-footer-guard.zul | 0／13／25 | 716 | 相同 | 相同 |
| pv/popup-rod-fixtures.zul | 0／0／0 | 13 | 相同 | 相同 |
| grid.zul | 0／4／0 | 84 | 相同 | 相同 |
| listbox.zul | 0／0／2 | 66 | 相同 | 相同 |

tree-header.zul 桌面：第 8 個 `.z-treecols`（一個 `z-items-start` flex 欄中的 tree，寬度依內容）寬 463 或 468px。重複量測：目前 CSS 6 次得 468、468、463、468、468、468；還原 F6 6 次全為 468；逐條還原的 bisect 中「無 route」也得到 468、只還原第 1 條得到 463。結論：這是與 CSS 無關的跨 process 雙穩態（5px，量級符合字型載入時序），不是 F6 造成的差異。chromium `tree gallery`（每段 20 px 預算）、`tree hover` 與 gallery `tree-header`（0.2% 比例預算）都通過。

### 3.5 F9 — PASS

baseline PNG `datebox-tablet-popup-wheel-time.png`（834x216，前後尺寸相同）水平掃描 x 405–430：

| 列 y | HEAD | 工作區 |
|---|---|---|
| 64（帶頂） | 405–408 帶色，409–412 漸淡，413–420 `255,255,255`，421–424 漸淡，425– 帶色 | 405–430 全為 `232,238,247` |
| 65、66、68、70 | 缺口由 6px 漸縮到 2px | 全為帶色 |
| 72、83 | 全為帶色 | 全為帶色 |
| 101、103（帶底） | 415–418／413–420 為白 | 全為帶色 |

外側圓角保留：y 64 時帶的範圍 HEAD 為 227–408 與 421–(…)，工作區為 227–606，227–230 與 603–606 是外角的漸淡像素。HEAD 與工作區 PNG 的差異：85 px、maxDelta 23，全部落在 x 409–424、y 64–103（只有接縫處）。
live（`$S/probe/f569.cjs f9`）：前 date 帶 radius `8px`、time 帶 `8px`；後 date 帶 `8px 0px 0px 8px`、time 帶 `0px 8px 8px 0px`；兩帶 x 223–417 與 417–611 相接，背景同為 `rgb(232, 238, 247)`。沒有 time 的 datebox 滾輪（「Seeded value (content-fit)」）與獨立 timebox 滾輪前後都是 `radius=8px`。

## 4. 突變證明（scratch 複本，不碰工作區）

複本：`$S/mut/zkpreview/`（`src/test/playwright` 與 `doc/screenshots` 的完整拷貝，`node_modules` 以 symlink 指向 p3）。拷貝後 `diff -rq` 與工作區相同。只在複本的 `tablet.spec.ts` 加了一個 `test.beforeEach` hook：`MUT=<id>` 時用 `context.route` 改寫 served `tablet.css.dsp`（每條替換斷言恰好命中一次，log 印出 `[MUT id] applied`）；第二層再以 `NOASSERT=1` 跳過本批新增的斷言，以證明 PNG 比對本身也會失敗。測試集為 4 個受影響測試（colorbox picker、兩個 combobox sheet、datebox time）。每次跑完 `diff -rq` 確認複本 baseline 沒有被改寫。

| 突變 | 層 | 結果 |
|---|---|---|
| 無 | 斷言 + PNG | 4 passed |
| 無，`NOASSERT=1` | PNG | 4 passed |
| MUT=F2（刪三條 colorpicker 規則） | 斷言 | 1 failed：`RGB row ends above the HSV row`，Expected ≤ 1016，Received 1021；其餘 3 passed |
| MUT=F4（`max-height:none` 拿掉） | 斷言 | 2 failed：`sheet is as tall as its inline height`，Expected 300，Received 280（兩個 combobox 測試）；2 passed |
| MUT=F9（刪兩條 radius 規則） | 斷言 | 1 failed：`date band top-right corner`，Expected `0px`，Received `8px`；3 passed |
| MUT=F2，`NOASSERT=1` | PNG | 1 failed：`toHaveScreenshot`，Expected 352x514，received 352x490，2558 px 不同 |
| MUT=F4，`NOASSERT=1` | PNG | 2 failed：Expected 834x312，received 834x292，117 px 不同 |
| MUT=F9，`NOASSERT=1` | PNG | 1 failed：38 px 不同（預算 `POPUP_MAX_DIFF` 10 px、threshold 0.02） |

工作區本身未突變的 `tablet` project 74 passed（§5），即「今天為真」。F5、F6 是刪除規則、沒有新斷言，以 §3.3、§3.4 的量測為證。

## 5. Playwright 執行

全部從 `p3/zk/zkpreview` 用 repo config、`PREVIEW_URL=http://127.0.0.1:8087`、`--update-snapshots=none`、`--output` 指到 `$S`。`select-tests.mjs --base HEAD --zkcml-base HEAD` 回答 `partial`，942/1708，`uncovered` 空（與實作紀錄一致）；本次依指示只跑下列項目，沒有跑完整的 partial 指令。

| Playwright project／測試 | passed | failed | skipped | 時間 | 容忍值 |
|---|---|---|---|---|---|
| `tablet`（全部） | 74 | 0 | 0 | 1.3m | 彈出層 `POPUP_MAX_DIFF` 10 px、threshold 0.02；`tabbox`／`window` 頁 2% |
| `popup`（全部，`--workers=2`） | 56 | 0 | 0 | 1.5m | 每張 10 px（兩張全 viewport 20 px），threshold 0.05 |
| `popup` 的 3 個 colorbox 測試，`POPUP_MAX_DIFF=0` | 3 | 0 | 0 | 4.0s | 0 px，threshold 0.05 |
| chromium `screenshot.spec.ts tree gallery`、`tree hover`；gallery `gallery-scan.spec.ts gallery tree-header` | 3 | 0 | 0 | 4.7s | section 20 px；hover 20 px；gallery 0.2% |
| `mesh-header-footer` + `mesh-header-footer-tablet` | 400 | 0 | 0 | 3.9m | 該 spec 的幾何斷言 |

每次執行後 `git status --porcelain zkpreview/doc/screenshots` 只有原本的 4 個 M；4 張 PNG 的內容與驗證開始時的拷貝 `cmp` 相同，沒有新增 PNG。

## 6. p3 狀態（驗證前後相同）

驗證前與驗證後各擷取一次（`$S/status-before.txt`、`$S/status-after.txt`），`diff` 無輸出：

```
== zk
 M zkpreview/doc/screenshots/colorbox-tablet-popup-picker.png
 M zkpreview/doc/screenshots/combobox-tablet-popup-description.png
 M zkpreview/doc/screenshots/combobox-tablet-popup-sheet.png
 M zkpreview/doc/screenshots/datebox-tablet-popup-wheel-time.png
 M zkpreview/src/test/playwright/tablet.spec.ts
 M zul/src/main/resources/web/js/zul/sel/css/tree.css
?? node_modules
?? zkpreview/node_modules
 .../screenshots/colorbox-tablet-popup-picker.png   | Bin 42349 -> 42868 bytes
 .../combobox-tablet-popup-description.png          | Bin 6839 -> 6970 bytes
 .../screenshots/combobox-tablet-popup-sheet.png    | Bin 3020 -> 3147 bytes
 .../datebox-tablet-popup-wheel-time.png            | Bin 10301 -> 10212 bytes
 zkpreview/src/test/playwright/tablet.spec.ts       |  31 +++++++++++++++++++--
 zul/src/main/resources/web/js/zul/sel/css/tree.css |  11 ++------
 6 files changed, 31 insertions(+), 11 deletions(-)
gaps-round2
237bfa8586 ZK-6112: record the tablet pop-up defects found by the tablet pop-up screenshots (follow-up 65)
== zkcml
 M zkmax/src/main/resources/web/zkmax/css/tablet/_inputs.css
 M zkmax/src/main/resources/web/zkmax/css/tablet/_mesh.css
 M zkmax/src/main/resources/web/zkmax/css/tablet/_wheel.css
 M zkmax/src/main/resources/web/zkmax/css/tablet/_window.css
 .../src/main/resources/web/zkmax/css/tablet/_inputs.css | 17 +++++++++++++++++
 zkmax/src/main/resources/web/zkmax/css/tablet/_mesh.css |  3 +--
 .../src/main/resources/web/zkmax/css/tablet/_wheel.css  | 11 +++++++++++
 .../src/main/resources/web/zkmax/css/tablet/_window.css | 13 ++++---------
 4 files changed, 33 insertions(+), 11 deletions(-)
gaps-round2
fe6323380 ZK-6112: bind tab, groupbox, spinner, timebox, searchbox, chosenbox, paging and toolbar heights to the density aliases
```

`git diff --cached --name-only` 兩邊都是 0 行。

## 7. 清理與環境

- 伺服器 8087：記錄的 PID 為外層 bash 47369、`sh -c` 47370、`tail -f /dev/null` 47372、gradle client 47373、single-use daemon 47493、Jetty 49869。只 kill 了 47372（關 stdin），appRun 正常結束（log：`BUILD SUCCESSFUL`），其餘 PID 隨之退出；`lsof -iTCP:8087 -sTCP:LISTEN` 無輸出。沒有用 `pkill`／`killall`，沒有動 8085（java 37900 仍在 listen）、8086、8105，沒有停其他 Gradle daemon。
- 重建 jar 只在伺服器未執行時做；build 輸出在 `build/`，不影響 git status。
- 臨時頁 `zkpreview/build/inplaceWebapp/web/b6v-f4-probe.zul` 已刪，舊 URL 回 404；沒有放進 `src/main/webapp`。
- `p3/zk/zkpreview/test-results/` 是驗證前就存在的目錄（mtime 13:00，早於本次啟動的 13:43），本次各次執行都用 `--output` 寫到 `$S`。

## 8. 值得注意的事

- F6 的 tree-header.zul 桌面 tree 寬度在 463／468px 間跨 process 雙穩態，與本批 CSS 無關（§3.4）；若日後以 live DOM 做零容忍比對，這一棵 tree 會造成假差異。
- menu picker 的桌面截圖跨 process 有 1 px、delta 1 的雜訊，同 CSS 也會出現。
- F2 的三條規則同時作用在 `<menu content="#color=…">` 的 picker（同一個 `.z-colorpicker`），量測結果與 colorbox 相同；實作紀錄 §9 列為未確認的這一項，本次已量。
- tablet 上 panel.zul 有 6 個 104px 的 header（桌面 80px），前後相同，不屬本批範圍，未查原因。
