# F3 獨立驗證：tablet 點 colorbox 會捲動頁面（follow-up 65，ZK-6112，D39 = A）

- 驗證者：獨立 verifier（不是實作者），2026-10-20
- 受測變更：p3/zkcml `zkmax/src/main/resources/web/zkmax/css/tablet/_inputs.css` 新增
  `.z-colorbox-popup { position: fixed !important; inset: auto 0 0 !important; transform: none !important; }`；
  p3/zk `zkpreview/src/main/webapp/web/pv/popup-tablet-fixtures.zul`、`zkpreview/src/test/playwright/tablet.spec.ts`、
  `colorbox-tablet-popup-palette.png`、`colorbox-tablet-popup-picker.png`（branch `gaps-round2`）
- 實作紀錄：`doc/marble-tablet-defect-fix-batch6.md` 的「F3」節
- 環境：p3 server 8087（`p3/zk/zkpreview`，`--no-daemon`），Chromium（Playwright），iPad UA、834x1112、`hasTouch`、`isMobile`；所有 tap 都是 `page.touchscreen.tap`
- 探測腳本與輸出：`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/028a7b57-cfcf-44e9-a25e-5d68a4869b5a/scratchpad/f3/`

## 結論

| # | 檢查 | 結果 |
|---|---|---|
| 1 | 規則只有 3 個宣告、各自 `!important`，沒有其他 `!important`／token／JS／Java | PASS |
| 2 | before／after 真實 tap 重現；PNG 只改寬度與位置 | PASS，附兩項量測發現（sheet 左右不對稱；滑入動畫消失） |
| 3 | 桌面不變 | PASS |
| 4 | `.z-colorbox-popup` 的其他使用處 | PASS |
| 5 | 突變證明 | PASS |
| 6 | `tablet` ×2、`popup` ×2 | `tablet` PASS（3 次皆 75/75）；`popup` 2 次皆 55 passed、1 failed（同一個桌面 datebox 測試，即 flake b，已重現並描述，見 §6） |
| 7 | p3 status 前後一致、無 staged | PASS |

F3 本身的主張（tablet 上點 colorbox 不再捲動頁面、popup 貼 viewport 底、桌面不受影響）成立。`popup` project 未達 56 的原因是 `datebox.zul: open calendar with the timezone selector`，這是桌面 UA 的值提交時序問題，與 tablet stylesheet 無關（理由見 §6）。

## 1. 規則內容（diff 審查）

- `git -C p3/zkcml diff` 中 F3 的 hunk：註解 5 行加上規則 3 個宣告，`position: fixed !important;`、`inset: auto 0 0 !important;`、`transform: none !important;`，沒有第 4 個宣告。
- 伺服器實際送出的 `tablet.css.dsp`（10681 bytes，md5 前綴 `713f4e0c`）內含 `.z-colorbox-popup{position:fixed!important;inset:auto 0 0!important;transform:none!important}`，出現 1 次。
- `count-important.js p3/zkcml/zkmax/src/main/resources/web/zkmax/css/tablet`：共 11 個（`_inputs.css` 7、`_wheel.css` 4）。HEAD 的 `_inputs.css` 有 4 個，所以 F3 新增 3 個；`_wheel.css` 的 4 個是既有的。
- 兩個 repo 的 diff 中，新增行只有這 3 行含 `!important`（另 1 行是註解）；新增行沒有 `--zk-*:` 宣告；變更檔沒有 `.ts`、`.js`、`.java`（`tablet.spec.ts` 是測試）。
- 與聲稱比照的規則相比：combobox sheet（`_inputs.css:104-112`）與 wheel（`_wheel.css:43-47`）都是 `position: fixed`、`inset: auto 10px 0`、`width: auto`、`transform: none` 4 個 `!important`，再加上非 `!important` 的 `animation: zk-bottom-sheet-up`。colorbox 規則的差異：沒有 `width: auto !important`（`colorbox-touch.ts` `openPopup()` 已把 inline `width` 設成 `auto`，不需要覆蓋），`inset` 左右是 `0` 而不是 `10px`，沒有 `animation`。後兩項的結果見 §2 的發現。

## 2. before／after 重現（真實 tap）

before = 用 Playwright `context.route` 改寫送出的 `tablet.css.dsp`，移除該規則；每次 route 都確認規則字串剛好出現 1 次（hits = 1）。after = 原樣送出。

`pv/popup-tablet-fixtures.zul`（新 fixture）與 `colorbox.zul`：

| 頁面與起始捲動 | | scrollY（tap 前 → 後） | trigger top（tap 前 → 後） | popup position | popup top／bottom | popup left／right（寬） |
|---|---|---|---|---|---|---|
| fixture，scrollY 100 | before | 100 → 679 | 276 → -303 | absolute | 215／533 | 238／596（358） |
| | after | 100 → 100 | 276 → 276 | fixed | 794／1112 | 0／818（818） |
| fixture，scrollY 300 | before | 300 → 832 | 76 → -456 | absolute | 262／580 | 238／596（358） |
| | after | 300 → 300 | 76 → 76 | fixed | 794／1112 | 0／818（818） |
| `colorbox.zul`，scrollY 0 | before | 0 → 0 | 183 → 183 | absolute | 794／1112 | 238／596（358） |
| | after | 0 → 0 | 183 → 183 | fixed | 794／1112 | 0／818（818） |

- before 時 popup 的 inline style：`top: 1212px; left: 238px; transform: translate(0px, -318px)`（`top = innerHeight + scrollY`），與實作紀錄一致。
- after 時 inline style 仍是 `position: absolute; top: …; left: 8px; transform: translate(0px, -318px)`，被 3 個 `!important` 蓋過；computed `transform: none`。
- 溢位：after 時 `scrollWidth`／`clientWidth` = 816／816、`scrollHeight`／`clientHeight` = 316／316，沒有水平或垂直捲軸。
- 截圖：`fixture-before.png`（trigger 已捲出畫面，popup 停在畫面中段）與 `fixture-after.png`（trigger 留在原處，sheet 貼底）。

### PNG（以像素為準）

`git show HEAD:` 版本與工作樹版本的尺寸：palette 382x330 → 830x330，picker 352x490 → 830x514。clip 寬 830 = sheet 右緣 818 + `POPUP_PAD` 12，左邊從 0 起。

把 HEAD sheet（clip 內 x = 12）與工作樹 sheet（x = 0）的左緣對齊、上緣都在 y = 12，比對 HEAD sheet 的寬度範圍（右框線除外）：

- palette：356 x 318 px 中，差異 > 8 的只有 2 px（第 355 欄的第 1 與 316 列，最大差 11），位置是 HEAD 的右上／右下圓角，工作樹此處是延伸的框線。比對門檻是逐像素 max channel delta > 8（`verification.md` 的跨 process 雜訊底線）；repo 測試本身的容忍值是 `POPUP_MAX_DIFF` 10 px、`POPUP_THRESHOLD` 0.02。內容（色票、hex 輸入框、圖示）像素相同。
- picker：326 x 478 px 中，第 370 列以上只有 1 px 差異（第 325 欄第 1 列，最大差 11，同樣是右上圓角）；其餘 3183 px 差異都在第 382 列以下，是 batch 6 F2 的 RGB／HSV／HEX 列位移與 picker 高度 +24 px（已在 batch-6 驗證）。
- 所以 F3 對 PNG 的改變只有寬度與位置。看圖沒有內容被切到：picker 的 HEX 輸入框與 ✓ 按鈕完整，palette 最後一列完整。

### 發現 A：sheet 左右不對稱（量測值）

- sheet 左緣 x = 0、右緣 x = 818，右邊留 16 px、左邊 0 px。PNG 中左框線在 x = 0，右框線在 x = 817。
- 原因：`zkex/.../colorbox.css:113-116` 的 `.z-colorbox-popup { max-width: calc(100% - var(--zk-spacing-4)) }`（computed `calc(100% - 16px)`）把寬度限制在 818；`left: 0`、`right: 0`、寬度受 `max-width` 限制後過度約束，LTR 下忽略 `right`，所以靠左。JS 算出的 inline `left: 8px`（置中）被 `inset` 的 `left: 0 !important` 蓋過。
- 內容（palette 340 px、picker 約 310 px）靠左排在 818 px 寬的 sheet 內，右側是空白。
- 對照：combobox sheet 與 wheel 是左右各 10 px。實作紀錄 §4 的選項 A 原本含 `margin-inline: auto`（非 `!important`），D39 核准的範圍只有 3 個 `!important`，所以結果是不對稱。這是否要處理（例如 V6 同角色同外觀）由 PM 判斷；我沒有改任何檔。

### 發現 B：colorbox popup 不再滑入（量測值）

- after 時從 tap 起以 `requestAnimationFrame` 取樣 1.5 秒（177 幀）：popup top 每幀都是 794，computed `animation-name: none`。也就是 popup 直接出現在最終位置。
- 修正前的滑入來自 `colorbox-touch.ts` `_syncPosition` 的 JS `translateY` transition（300 ms），被 `transform: none !important` 取消。combobox 與 wheel 用 `animation: zk-bottom-sheet-up` 補回滑入，colorbox 規則沒有（D39 範圍外；實作紀錄 §4 已寫「需要新增一個 `animation` 才能保留滑入動畫」）。修正前的滑入幀我沒有另外量。

## 3. 桌面不變

- 桌面 UA 的 `colorbox.zul`：popup computed `position: absolute`，inline `left: 148px; top: 219px`，358 px 寬，`transform: none`；`pv/popup-tablet-fixtures.zul`（scrollY 100）：absolute，scrollY 100 → 100。
- 桌面 UA 下 `tablet.css.dsp` 的 `<link>` 存在且 `disabled = true`，不在 `document.styleSheets` 中，`zk.mobile` 與 `zk.tabletUIEnabled` 皆為 false（該檔仍會被下載，hash 與 tablet 相同，但不套用）。
- `popup` project 的 3 個 colorbox 測試（`open the palette`、`hover a swatch`、`switch the popup to the picker`）在 2 次 run 都通過；`git status` 沒有任何桌面 colorbox PNG 變動。

## 4. 其他使用處（tablet，before vs after）

grep `colorbox-popup`（zul、zkex、zkmax、zkpreview）：CSS 只有 `colorbox.css` 與 tablet `_inputs.css`；`<menu content="#color=…">` 的 popup class 是 `z-menu-popup z-colorpalette-popup`（`zkex/inp/ContentHandler.ts`），不含 `z-colorbox-popup`。用到 colorbox 的頁面：`colorbox.zul`、`pv/colorbox-content.zul`（含 menu 版）、`signature.zul`、`portallayout.zul`、`usecase/brand-switcher.zul`、`inputs.zul`、fixture。

scrollY「tap 時」是 `scrollIntoViewIfNeeded` 之後、tap 之前的值。

| 情境 | before：scrollY、popup | after：scrollY、popup |
|---|---|---|
| menu「Quick Colour」（menubar 頂層） | 0 → 0；absolute，top 335、bottom 653、left 128，在 viewport 內 | 完全相同 |
| menu「Format › Text Colour」（巢狀） | 0 → 0；absolute，342／660、left 202，在 viewport 內 | 完全相同 |
| `pv/colorbox-content.zul` colorbox | 0 → 0；absolute，794／1112 | 0 → 0；fixed，794／1112，0／818 |
| `signature.zul` | 0 → 112；absolute，682／1000 | 0 → 0；fixed，794／1112 |
| `portallayout.zul`（portlet 內） | 0 → 579；absolute，215／533 | 0 → 0；fixed，794／1112 |
| `usecase/brand-switcher.zul` | 0 → 579；absolute，215／533 | 0 → 0；fixed，794／1112 |
| `inputs.zul` | 5145 → 5724；absolute，215／533 | 5145 → 5145；fixed，794／1112 |

- menu 版 before 與 after 數值完全相同，沒有回歸。
- 修正前，未捲動的頁面（signature、portallayout、brand-switcher）點 colorbox 也會捲動頁面 112–579 px；修正後全部為 0。
- after 時所有 colorbox popup 都 `scrollWidth = clientWidth`（816），沒有被切。

## 5. 突變證明

在 scratchpad 建一個只含 `tablet.spec.ts` 新測試的複本（`openTabletPage`、`tapCentre`、`settledBox` 與測試本體由 `sed` 從 repo 檔逐行複製，未改寫），用 `context.route` 改寫送出的 `tablet.css.dsp`，每次斷言規則字串在送出內容中剛好 1 次。工作樹未修改。

| 突變 | 結果 | 失敗的斷言 |
|---|---|---|
| 無（第 1 次） | 通過 | — |
| 拿掉 `position: fixed !important` | 失敗 | popup 底邊 預期 1112，實得 1012 |
| 拿掉 `inset: auto 0 0 !important` | 失敗 | 預期 1112，實得 1530 |
| 拿掉 `transform: none !important` | 失敗 | 預期 1112，實得 794 |
| 拿掉整條規則 | 失敗 | 預期 1112，實得 533 |
| 無（第 2 次） | 通過 | — |

`toBeCloseTo(…, -1)` 的容忍值是差 < 5 px。前 3 個數字與實作紀錄相同。

## 6. Playwright runs（repo config、`--update-snapshots=none`、`PREVIEW_URL=http://127.0.0.1:8087`）

| run | 伺服器狀態 | 結果 |
|---|---|---|
| `tablet` #1 | 啟動後已載過頁面（warm） | 75 passed（1.2 分，3 workers） |
| `popup` #1 | warm | 55 passed、1 failed：`datebox.zul: open calendar with the timezone selector` |
| 重啟伺服器 | port 開始 listen 後立即跑，之前沒有任何 HTTP 請求 | — |
| `tablet` #2 | cold | 75 passed |
| `popup` #2 | 同一個伺服器 | 55 passed、1 failed：同一個 datebox 測試 |
| `tablet` #3 | warm | 75 passed |

每次 run 後 `git status` 沒有新 PNG、沒有 PNG 變動。

### flake (a)：3 個 colorbox tablet 測試一次失敗（position absolute、picker 列重疊）

沒有重現。條件與次數：
- `tablet` project 完整 3 次（其中 1 次是 cold server、port listen 後立即跑）：75/75 ×3。
- `-g colorbox --repeat-each=10`（5 個 colorbox 測試 × 10，4 workers）：50/50。
- 60 個全新 tablet context（6 個並行，`colorbox.zul` 與 fixture 交替）：60/60 都請求了 `tablet.css.dsp`（200，10681 bytes，同一個 md5），link 已 enable、在 `document.styleSheets` 中、含 `!important` 的 `.z-colorbox-popup` 規則與 `.z-colorpicker-hsv` 規則，`zk.mobile`、`zk.tabletUIEnabled` 皆為 true。

依實作者描述的症狀推論（未確認）：picker 列「重疊」代表 48 px 的 tablet 輸入框高度有套用（桌面 40 px 不會重疊），也就是 tablet stylesheet 有載入，但 F2 與 F3 的 colorbox 規則不在其中。這比較像當時伺服器送出的是不含 colorbox 區段的 `tablet.css.dsp`（例如較舊或突變中的 jar），而不是 stylesheet 沒有套用。我無法查證當時的伺服器：`/tmp/server-8087.log` 依指示路徑已被本次啟動覆寫。原因仍未查明。

### flake (b)：`popup` project 的 `datebox.zul: open calendar with the timezone selector`（桌面 UA）

已重現，比例如下：
- `popup` 完整 run：2/2 次在第 10 個測試失敗。
- 只跑該測試 `--repeat-each=20 --workers=1`：0/20 失敗。
- 跑 datebox describe 的 3 個測試 `--repeat-each=10`：timezone 測試 3/10 失敗（repeat 3、6、8）。
- 獨立腳本重做相同步驟（新 context、click input、type `Jan 15, 2025`、Tab）20 次：3/20 失敗；加上 instrumentation 的另一輪 20 次：4/20 失敗。

失敗時的狀態（量測值）：
- 錯誤：`expect.poll(... getValue()?.getFullYear()).toBe(2025)`，5 秒後實得 `undefined`。
- input 的值是 `Jan 15, 2025`，click 落在該 input（`elementFromPoint` 確認），click 後 focus 在該 input。
- Tab 之後 widget 的 `updateChange_` 一次都沒被呼叫（成功的 run 都呼叫 1 次），沒有 errorbox，沒有 `/zkau` 請求，8 秒內值都沒有被接收。
- 也就是說，失敗時 blur 路徑沒有提交輸入的值（`InputWidget.doBlur_` 只在 `shallUpdate_(zk.currentFocus)` 為真時呼叫 `updateChange_`；`Datebox.updateChange_` 另外要求 `!isOpen()`）。哪個條件擋下來，我沒有再追。

與 F3／batch 6 的關係：
- 桌面 UA 下 tablet stylesheet 不在 `document.styleSheets`（§3），F3 規則不會套用。
- batch 6 唯一的桌面 CSS 變更是 `tree.css` 的 `.z-treecol*`；`datebox.zul` 與 `pv/datebox-content.zul` 沒有 `<tree>`。
- 失敗機制是 widget JS 的值提交，不是幾何或樣式。
- 該測試是今天的 commit `07feed51a6`（follow-up 63）新增的。
- 我沒有在 HEAD（無本批變更）的伺服器上量過失敗率，所以「HEAD 也會失敗」是推論，未確認。

## 7. p3 狀態（run 前後相同）

前後兩次的 `git status --porcelain` 與 `git diff --stat` 輸出逐行相同：

p3/zk：
```
 M zkpreview/doc/screenshots/colorbox-tablet-popup-palette.png
 M zkpreview/doc/screenshots/colorbox-tablet-popup-picker.png
 M zkpreview/doc/screenshots/combobox-tablet-popup-description.png
 M zkpreview/doc/screenshots/combobox-tablet-popup-sheet.png
 M zkpreview/doc/screenshots/datebox-tablet-popup-wheel-time.png
 M zkpreview/src/main/webapp/web/pv/popup-tablet-fixtures.zul
 M zkpreview/src/test/playwright/tablet.spec.ts
 M zul/src/main/resources/web/js/zul/sel/css/tree.css
?? node_modules
?? zkpreview/node_modules
 .../screenshots/colorbox-tablet-popup-palette.png  | Bin 19665 -> 21297 bytes
 .../screenshots/colorbox-tablet-popup-picker.png   | Bin 42349 -> 46018 bytes
 .../combobox-tablet-popup-description.png          | Bin 6839 -> 6970 bytes
 .../screenshots/combobox-tablet-popup-sheet.png    | Bin 3020 -> 3147 bytes
 .../datebox-tablet-popup-wheel-time.png            | Bin 10301 -> 10212 bytes
 .../main/webapp/web/pv/popup-tablet-fixtures.zul   |   5 ++-
 zkpreview/src/test/playwright/tablet.spec.ts       |  48 +++++++++++++++++++--
 zul/src/main/resources/web/js/zul/sel/css/tree.css |  11 ++---
 8 files changed, 51 insertions(+), 13 deletions(-)
```

p3/zkcml：
```
 M zkmax/src/main/resources/web/zkmax/css/tablet/_inputs.css
 M zkmax/src/main/resources/web/zkmax/css/tablet/_mesh.css
 M zkmax/src/main/resources/web/zkmax/css/tablet/_wheel.css
 M zkmax/src/main/resources/web/zkmax/css/tablet/_window.css
 .../resources/web/zkmax/css/tablet/_inputs.css     | 29 ++++++++++++++++++++++
 .../main/resources/web/zkmax/css/tablet/_mesh.css  |  3 +--
 .../main/resources/web/zkmax/css/tablet/_wheel.css | 11 ++++++++
 .../resources/web/zkmax/css/tablet/_window.css     | 13 +++-------
 4 files changed, 45 insertions(+), 11 deletions(-)
```

兩個 repo 的 `git diff --cached --name-only` 前後都是空的。`build/inplaceWebapp/web/pv/` 沒有 repro／probe 頁（實作者的 `pv/colorbox-scroll-repro.zul` 已不存在）；本次沒有建立任何探測頁。

## 伺服器紀錄

- 第 1 組：sh 59431、tail 59432、gradle client 59433、single-use daemon 59471、Jetty 59825。
- 第 2 組（cold restart）：sh 90565、tail 90567、client 90568、daemon 90585、Jetty 91015。
- 兩組都只 kill `tail -f /dev/null`，並等 appRun 結束；結束後 5 個 PID 都已不存在，8087 沒有 listener。
- 沒有用 `pkill`／`killall`，沒有動 8085（另一個 session 的 PID 37900 仍在）、8086、8105 與其他 Gradle daemon；伺服器執行期間沒有重建 jar。

## 追補驗證（2026-10-20，對稱修正後）

受測變更：`.z-colorbox-popup` 新增兩個非 `!important` 宣告 `max-width: calc(100% - 20px); margin-inline: auto;`（加註解）；F3 測試加上對稱斷言（左右間距差 ≤ 1 px、左間距 ≈ 10）；重拍 `colorbox-tablet-popup-palette.png`、`colorbox-tablet-popup-picker.png`；沒有加 `animation`。伺服器：8087，sh 47533、tail 47536、client 47537、daemon 47568、Jetty 48225（`--no-daemon`）。送出的規則（minified）：`.z-colorbox-popup{max-width:calc(100% - 20px);margin-inline:auto;position:fixed!important;inset:auto 0 0!important;transform:none!important}`。

| # | 項目 | 結果 |
|---|---|---|
| 1 | diff 審查 | PASS |
| 2 | 對稱（PNG 像素）、位置、溢位、其他使用處、窄 viewport | PASS，附 1 項註解措辭與 1 項窄螢幕量測 |
| 3 | 突變 | PASS（兩個宣告各自被測試抓到，但抓到的斷言不同） |
| 4 | 滑入動畫 | 工程師的量測成立；combobox／wheel 不滑入的情況在 HEAD 就已存在，不是 batch 6／F3 造成的 |
| 5 | `tablet` ×2、`popup` ×2 | `tablet` 75/75 ×2；`popup` 56/56 一次、55/56 一次（datebox timezone flake） |

### 追補 1：diff 審查

- 新增行中含 `!important` 的仍只有那 3 行（另有 1 行註解）；新增的 2 個宣告都不是 `!important`；新增行沒有 `--zk-*:` 宣告；變更檔仍沒有 `.ts`／`.js`／`.java`（`tablet.spec.ts` 是測試）。
- `count-important.js` 量 tablet 目錄：11（`_inputs.css` 7、`_wheel.css` 4），和上一輪相同。
- 註解措辭（量測值）：新註解寫「The inline width is wider than the viewport」，但量到的 inline `width` 是 `auto`（`colorbox-touch.ts` `openPopup()` 設定）。sheet 會撐滿，是因為 `position: fixed` 加上 `left: 0; right: 0` 時 `width: auto` 會填滿 viewport；寬度受 `max-width` 限制後才產生過度約束。這句只影響註解，不影響規則行為。

### 追補 2：對稱與幾何

全部用 iPad UA 和真實 `touchscreen.tap`。「PNG 間距」是從 `page.screenshot` 取 sheet 垂直中線那一列的像素：左框線位置 x，以及右框線之後到右緣的寬度。

| 情境（viewport 寬） | scrollY | position、top／bottom | 即時 box 間距 左／右 | PNG 間距 左／右 | `scrollWidth`／`clientWidth` |
|---|---|---|---|---|---|
| fixture，scrollY 100（834） | 100 → 100，trigger 276 → 276 | fixed，794／1112 | 10／10（寬 814） | 10／10（框線 x = 10、823） | 812／812 |
| `colorbox.zul`（834） | 0 → 0 | fixed，794／1112 | 10／10 | 10／10 | 812／812 |
| `signature.zul`（834） | 0 → 0 | fixed，794／1112 | 10／10 | 10／10 | 812／812 |
| `portallayout.zul`（834） | 0 → 0 | fixed，794／1112 | 10／10 | 10／10 | 812／812 |
| `usecase/brand-switcher.zul`（834） | 0 → 0 | fixed，794／1112 | 10／10 | 10／10（背景 247，第 9 欄是陰影 227） | 812／812 |
| `inputs.zul`（834） | 5145 → 5145 | fixed，794／1112 | 10／10 | 10／10 | 812／812 |
| fixture（600） | 100 → 100 | fixed，794／1112 | 10／10（寬 580） | 10／10 | 578／578 |
| fixture（1280） | 100 → 100 | fixed，794／1112 | 10／10（寬 1260） | 10／10 | 1258／1258 |
| fixture（360） | 100 → 100 | fixed，794／1112 | 10／10（寬 340） | 10／10 | **356／338** |
| 對照：combobox sheet（834） | 0 → 0 | fixed，812／1112 | 10／10（寬 814） | 10／10（陰影到 x = 9，surface 從 x = 10 起，右側陰影從 x = 824 起） | 814／814 |

- colorbox sheet 的間距與 combobox sheet 相同，左右各 10 px，PNG 上兩側都看得到陰影。
- 內容比 viewport 窄時（palette 內容 340 px）：sheet 是拉寬，不是內容寬度置中。sheet 寬是 viewport − 20，內容靠左排在 sheet 內（`fu/fixture-834.png`，1280 寬時 sheet 為 1260）。這是 `width: auto` 加左右 inset 的結果。
- 「inline width 比 viewport 寬」的情況不存在：所有情境的 inline `width` 都是 `auto`。600 px 時 sheet 寬 580，左右各 10 px，沒有溢位。
- 360 px 時 `scrollWidth` 356 大於 `clientWidth` 338：palette 內容（340 + padding 16）超過 sheet，最右一欄色票和 hex 輸入框的右端被裁掉，需要水平捲動（`fu/fixture-360.png`）。原因是 `colorbox.css` 的 `overflow-x: auto`（WCAG reflow 的設計）。上一版的上限 `100% − 16px` 在 360 時是 344 px，`clientWidth` 342，同樣小於 356。所以這不是這次修正新造成的；修正後可用寬度少 4 px。834 px 的 iPad 不受影響。
- 重拍的 PNG 與 `git show HEAD:` 比較：尺寸 palette 382x330 → 834x330、picker 352x490 → 834x514。把 HEAD sheet（x = 12）與新 sheet（x = 10）左緣對齊後，palette 差異 > 8 的只有 2 px（最大差 11，HEAD 的右上／右下圓角處）。picker 在第 370 列以上只有 1 px（同樣是圓角）；第 382 列以下 3183 px，是 batch 6 F2 的列位移，與上一輪數字相同。除了水平 inset 與寬度，內容沒有其他改變；看圖沒有裁切。

### 追補 3：突變（scratch spec 複本 + `context.route`）

- 複本中的 helper 與測試本體由 `sed` 依行號從 repo 檔複製（helper 718–751 行、測試 919–936 行）。
- 每次都斷言整條規則字串命中 1 次、被替換的宣告命中 1 次。

| 突變 | 結果 | 失敗的斷言 |
|---|---|---|
| 無（前後各 1 次） | 通過 | — |
| 拿掉 `margin-inline:auto` | 失敗 | `the sheet is centred: left gap 0px, right gap 20px`（差 20，容忍 ≤ 1） |
| 拿掉 `max-width:calc(100% - 20px)` | 失敗 | `the side gutter matches the combobox sheet`：預期 10，實得 8（容忍差 < 0.5）；左右仍對稱，各 8 px |
| 兩個都拿掉 | 失敗 | 左 0、右 16 |

測試依賴的斷言：`margin-inline: auto` 由「左右相等」的斷言抓到；`max-width` 只由「左間距 ≈ 10」的斷言抓到。拿掉 `max-width` 時，回到 `colorbox.css` 的 `100% − 16px`，sheet 仍然置中，只是間距變成 8。

### 追補 4：滑入動畫

- 從真實 tap 起每個 `requestAnimationFrame` 取樣約 1.2 秒，記錄 `getBoundingClientRect().top`、computed `transform`、`getAnimations()`。
- 「base」= 用 route 把規則改回 HEAD 的文字：拿掉 batch 6 F4 的 `max-height:none`、拿掉整條 F3 規則；wheel 規則在 HEAD 與現在相同。每個替換都確認命中 1 次。

| 模式 | combobox | datebox wheel | colorbox |
|---|---|---|---|
| 現在 | 133 幀，top 都是 812；`zk-bottom-sheet-up` 為 `running`，但 computed `transform: none` | 136 幀，top 都是 908；同上 | 142 幀，top 都是 794，沒有動畫 |
| base（HEAD 規則） | 136 幀，top 都是 832（沒有 F4 時 280 px 高）；`running`，`transform: none` | 136 幀，top 都是 908；同上 | popup 為 absolute，inline `top: 1112px`。JS transition 使 `transform` 從 0 變到 −318，但頁面同時被撐高並捲動：`colorbox.zul` 的 scrollY 在開啟時跳到 318，再隨 transition 回到 0，所以 popup 在畫面上一直停在 794；fixture（已捲到 100）的 popup 從 533 移到 215，頁面停在 scrollY 579 |
| 對照：拿掉 `transform:none!important` | top 從 1112 動到 512（31 種值）：keyframe 會播放，但結束後 inline `translateY(-300px)` 接手，sheet 停在離底部 300 px 處 | 從 1112 動到 704：同樣停在離底部 204 px 處 | 從 794 往上動到 476 |

結論：
- combobox 與 wheel 的 `animation: zk-bottom-sheet-up` 有在跑（`running`），但只動 `transform`；作者層的 `transform: none !important` 優先於 CSS animation，所以畫面上不會滑動。工程師說三個 sheet 都不滑入，量測成立。
- HEAD 的規則文字相同（`git show HEAD:` 的 `_inputs.css`、`_wheel.css`），自 `eea2b6428` 起就是這樣；base 模式也量到同樣的靜止結果。所以「沒有滑入」在 batch 6／F3 之前就存在，不是這次變更造成的。
- 單純拿掉 `transform: none !important` 也不能修好：keyframe 結束後 JS 的 inline translate 會把 sheet 推離底部。
- 這修正了我上一輪的「發現 B」。在 HEAD 上，colorbox 在未捲動的頁面也沒有可見的滑入：JS transition 期間是頁面在捲動，popup 在畫面上的位置不變。F3 拿掉的是這段頁面捲動，不是一個可見的滑入動畫。

### 追補 5：Playwright（repo config、`--update-snapshots=none`、`PREVIEW_URL=http://127.0.0.1:8087`）

- `tablet`：第 1 次 75 passed（1.2 分），第 2 次 75 passed（1.3 分）。
- `popup`：第 1 次 56 passed；第 2 次 55 passed、1 failed，失敗的是 `datebox.zul: open calendar with the timezone selector`（第 10 個測試）。這是 §6 的 flake (b)，在本報告累計的完整 `popup` run 中失敗 3/4 次。
- 每次 run 後 `zkpreview/doc/screenshots` 的 status 與 run 前相同（5 個既有的 M，沒有新檔）。

### 追補：p3 狀態

前後兩次的 `git status --porcelain`、`git diff --stat`、`git diff --cached --name-only` 以 `diff` 比對，結果相同（`status-before-2.txt` 與 `status-after-2.txt`，存放於 scratchpad `f3/`）。和上一輪相比的變動是工程師的修改：`_inputs.css` +33 行、`tablet.spec.ts` +52 行，palette PNG 21636 bytes、picker PNG 46089 bytes。兩個 repo 都沒有 staged 內容。

- 伺服器只 kill tail 47536，並等 appRun 結束；5 個 PID 都已不存在，8087 沒有 listener。
- 沒有用 `pkill`，沒有動其他 port 或 daemon，沒有建立探測頁。
- 追補的腳本與截圖：scratchpad `f3/probe5.cjs`、`f3/anim2.cjs`、`f3/anim3.cjs`、`f3/mut2/`、`f3/fu/`。

## 追補驗證 2（2026-10-20，popup 貼合內容寬度、F4 8 項 fixture）

受測變更（未 commit）：
- `_inputs.css` 的 `.z-colorbox-popup`：`max-width: calc(100% - 20px)` 改為 `max-width: fit-content`；`margin-inline: auto` 與 3 個 `!important` 不變。
- `pv/popup-tablet-fixtures.zul`：新增兩個各有 8 個 comboitem 的 combobox（第二個含 description）。
- `tablet.spec.ts`：兩個 F4 測試改開 fixture 頁，並加上 `expectLastVisibleRowEndsOnSheetBottom`；F3 測試改為斷言寬度 ≤ 最寬子元素 + padding + border + 1，且 < viewport 的 60%。
- 重拍 4 張 PNG。

伺服器：8087，log `/tmp/server-8087-ver3.log`，sh 70639、tail 70644、client 70645、daemon 70693、Jetty 72434（`--no-daemon`）。送出的規則：`.z-colorbox-popup{max-width:fit-content;margin-inline:auto;position:fixed!important;inset:auto 0 0!important;transform:none!important}`。

| # | 項目 | 結果 |
|---|---|---|
| 1 | diff 審查、fixture 影響範圍 | PASS |
| 2 | colorbox 寬度與位置 | PASS（834、600）；附 2 項窄螢幕或寬頁面的量測發現 |
| 3 | F4 與 colorbox 的 before／after 圖 | PASS；F4 before 圖看得到被切的第 5 列 |
| 4 | 突變 | PASS |
| 5 | Playwright | `tablet` 75 ×2、`popup` 56 ×2（這兩次 flake 沒出現）；partial 550 passed、5 failed（皆為 `font-size`，與本批無關，見下） |

### 追補 2-1：diff 審查

- colorbox 規則仍是 3 個 `!important`；`count-important.js` 量 tablet 目錄：11（`_inputs.css` 7、`_wheel.css` 4）。
- 新增行沒有其他 `!important` 或 `--zk-*:` 宣告，變更檔沒有 `.ts`、`.js`、`.java`（`tablet.spec.ts` 是測試）。
- `pv/popup-tablet-fixtures.zul` 只被 `tablet.spec.ts` 引用（grep `zkpreview/src`、`scripts`）。`gallery-scan.spec.ts` 與 `forced-colors-gallery.spec.ts` 用非遞迴的 `readdirSync(WEB_DIR)`，不會讀到 `pv/`；`render-smoke.spec.ts` 的頁面清單也沒有它。
- `git status` 的 PNG 仍只有 5 張：4 張這次重拍的，加上 batch 6 F9 的 `datebox-tablet-popup-wheel-time.png`。`popup`、partial（含 `chromium`、`gallery`）的所有 run 都沒有新增或變動 PNG。

### 追補 2-2：colorbox 寬度

量測方式：iPad UA，真實 `touchscreen.tap`。fixture 先捲到 100 再點。「PNG」欄是 `page.screenshot` 在 sheet 中線那一列的框線 x 座標。

| 情境 | scrollY | popup | 寬（內容 + padding 16 + border 2） | 間距 左／右（PNG） | `scrollWidth`／`clientWidth` |
|---|---|---|---|---|---|
| fixture palette，834 | 100 → 100，trigger 424 → 424 | fixed，794／1112 | 358（palette 340） | 238／238（框線 x 238、595） | 356／356 |
| fixture picker，834 | 100 → 100 | fixed，610／1112 | 328（picker 310） | 253／253（框線 x 253、580） | 326／326 |
| fixture palette，600 | 100 → 100 | fixed，794／1112 | 358 | 121／121 | 356／356 |
| fixture picker，600 | 100 → 100 | fixed，610／1112 | 328 | 136／136 | 326／326 |
| `colorbox.zul`、`pv/colorbox-content.zul`，834／600 | 0 → 0 | fixed，794／1112 | 358 | 238／238、121／121 | 356／356 |
| `signature.zul`、`portallayout.zul`、`usecase/brand-switcher.zul`、`inputs.zul`，834 | 不變（inputs 5145 → 5145） | fixed，794／1112 | 358 | 即時 box 238／238 | 356／356 |

- 實作紀錄寫「picker 同為 358」，但量到的 picker popup 寬是 328（picker 內容 310 + 18）；palette 是 358。
- 其他使用處這幾列的 PNG 間距欄不適用：sheet 中線那一列有頁面本身的框線（例如 portallayout 在 x = 32），所以這幾列以即時 box 為準。
- `pv/colorbox-content.zul` 的 menu 版（`.z-menu-popup`）before 與 after 數值完全相同：absolute，335／653、left 128（Quick Colour）；342／660、left 202（Format › Text Colour）；都在 viewport 內，`scrollWidth = clientWidth` 356。

窄 viewport（`colorbox.zul`，頁面本身沒有水平溢出）：

| viewport | `fit-content`（現在） | 上一版 `calc(100% - 20px)`（route 改回，命中 1 次） |
|---|---|---|
| 360 | 寬 358，左右各 1，356／356，沒有溢位 | 寬 340，各 10，356／338（內部水平捲動） |
| 340 | 寬 340，左右各 0，356／338（內部水平捲動） | 寬 320，各 10，356／318 |
| 320 | 寬 320，各 0，356／318 | 寬 300，各 10，356／298 |

- 內容比 viewport 窄時，sheet 是內容寬度，置中，不拉寬。
- 內容比 viewport 寬時，`fit-content` 被可用寬度限制，sheet 等於 viewport 寬、左右沒有間距，內容由 `colorbox.css` 的 `overflow-x: auto` 在 sheet 內水平捲動。上一版在同樣寬度下留 10 px 間距，但可捲動的範圍更大。

#### 發現 C：頁面比 viewport 寬時，sheet 有一部分在畫面外（量測值，Chromium `isMobile` 模擬）

- `isMobile: true` 時，頁面若水平溢出，layout viewport 會比裝置寬。fixed 的 sheet 貼在 layout viewport 的底部，所以有一部分落在可見區（visual viewport）下方。
- 新 fixture 本身寬 482 px（兩個 combobox 並排）。viewport ≤ 360 時，layout viewport 變成 482 x 1489：colorbox sheet 的 top／bottom 是 1171／1489，可見區是 360 x 1112，sheet 的右側與下半部在畫面外（`fu3/fixture-palette-360.png`）。`isMobile: false` 時沒有這個現象（sheet 1／359、794／1112）。上一版規則在同樣條件下也一樣（462 寬，1171／1489）。
- `signature.zul` 在 600 寬時，頁面本身溢出到 632，layout viewport 632 x 1172。colorbox sheet 的 bottom 是 1172，有 60 px 在可見區下方（`fu3/wide-signature_colorbox.png`）。同頁拿掉 F3 規則（HEAD）時，頁面捲動 52 px，popup 完整可見。
- 對照：同一個 fixture 在 360 寬時，combobox sheet（HEAD 就有的規則）top 1189、bottom 1489，整個在可見區（1112）下方，有 377 px 在畫面外。
- 所以這是 fixed 底部 sheet 在水平溢出頁面上的共同行為，combobox sheet 在 HEAD 就有。colorbox 加入 fixed 以後也有。834 寬的 iPad 測試條件不受影響。iOS Safari 實機未驗證。

#### 重拍 PNG 與 `git show HEAD:` 比較（整張逐像素，門檻 max channel delta > 8）

- palette：382x330，與 HEAD 尺寸相同；2 px 不同（最大差 14，第 60 列 x = 218、252，是新色塊的下緣圓角反鋸齒），其餘相同。用 HEAD CSS（拿掉 F3 規則）重拍的 palette 與 HEAD 的 PNG 完全相同（0 px）。
- picker：352x490 → 352x514；不同的像素全部在第 394–489 列（3185 px，最大差 222），是 F2 的列位移與高度 +24。第 394 列以上沒有差異。
- 所以 colorbox 兩張 PNG 和 HEAD 相比，除了 F2 以外沒有其他變化：sheet 寬度與水平位置回到 HEAD 的 358 px 置中。
- combobox 兩張 PNG 的內容換成 fixture 的 8 項，尺寸 834x292 → 834x312，與 HEAD 不能逐像素比較（頁面不同）。

### 追補 2-3：before／after 圖（`scratchpad/f3/art2/`）

- 產生方式：scratch spec 的 `shot()`，clip 計算與 `tablet.spec.ts` 的 `tabletPopupShot()` 相同（`POPUP_PAD` 12，在 viewport 邊界截止，`animations: 'disabled'`），開啟方式與 repo 測試相同（真實 tap）。
- after 圖另外用 `toHaveScreenshot` 與 repo baseline 比對（`POPUP_MAX_DIFF` 10、`POPUP_THRESHOLD` 0.02），4 張都通過。
- before 的 CSS 用 route 改寫，每次斷言命中 1 次：
  - combobox：`.z-combobox-popup{max-height:none;` → `max-height:280px;`。
  - colorbox：拿掉整條 F3 規則；F2 的 picker 規則保留，依指示只拿掉 F3。

| 檔案 | 與 working tree baseline | 看到的內容 |
|---|---|---|
| `combobox-sheet.before.png`（834x292） | — | sheet 280 高（832–1112）；8 列分別在 832–892 … 1072–1132，第 5 列「Item 5」貼著 sheet 底，下半部 20 px 被切，文字下方沒有留白 |
| `combobox-sheet.after.png`（834x312） | 位元組相同 | sheet 300 高（812–1112）；第 5 列 1052–1112，剛好結束在 sheet 底 |
| `combobox-description.before.png` | — | 第 5 列的「Description of item 5」只剩上半截，被切的情況清楚可見 |
| `combobox-description.after.png` | 位元組相同 | 第 5 列（含 description）完整 |
| `colorbox-palette.before.png` | — | 358 寬、置中；與 HEAD 的 PNG 完全相同 |
| `colorbox-palette.after.png` | 位元組相同 | 與 before 只差 2 px（最大差 14，色塊圓角） |
| `colorbox-picker.before.png` | — | 有 F2，沒有 F3 |
| `colorbox-picker.after.png` | 不是位元組相同；0 px 差異 > 8，最大差 1 | 與 before 只差 1 px（最大差 14） |

- combobox：before 與 after 對齊後，前 280 列相同，差異只在第 290–291 列（sheet 底部）。
- colorbox：在 `colorbox.zul`（未捲動）上，F3 前後的 popup 截圖幾乎相同，所以 popup crop 看不出 F3 的效果。F3 的效果是頁面捲動，所以另外存了整個畫面的 `colorbox-scrolled-fixture.before.png` 與 `.after.png`（fixture 捲到 100）：
  - before：scrollY 100 → 679，trigger 424 → -155（離開畫面），popup absolute 215–533。
  - after：scrollY 100 → 100，trigger 不動，popup fixed 794–1112、238–596。

### 追補 2-4：突變（scratch 複本 = 完整 `tablet.spec.ts` 加一個 file-level route `beforeEach`，每次斷言替換命中 1 次）

| 突變 | 測試 | 結果 |
|---|---|---|
| 無（前後各 1 次） | F3 測試 | 通過 |
| 拿掉 `max-width:fit-content` | F3 測試 | 失敗：`the sheet is as wide as its content`，≤ 359，實得 818（置中斷言仍通過，因為 818 寬左右各 8） |
| 無（前後各 1 次） | 兩個 F4 測試 | 通過 |
| `max-height:none` → `280px` | 兩個 F4 測試 | 兩個都失敗：`sheet is as tall as its inline height`，預期 300，實得 280 |
| 同上，但在複本中刪掉 `expectSheetKeepsJsHeight` 的呼叫 | 兩個 F4 測試 | 兩個都失敗：`the sheet shows 5 complete rows`，預期 5，實得 4（未突變時兩個都通過） |

完整的 F4 測試中，先失敗的是既有的 `expectSheetKeepsJsHeight`，所以新 helper 在原本的順序下不會執行到。單獨測時，新 helper 也能抓到這個突變。

### 追補 2-5：Playwright（repo config、`--update-snapshots=none`、`PREVIEW_URL=http://127.0.0.1:8087`）

- `tablet`：75 passed（2.1 分）、75 passed（1.5 分）。
- `popup`：56 passed、56 passed。datebox timezone flake（b）這兩次沒有出現。本報告累計的完整 `popup` run：失敗 3/6 次。
- `forced-colors-tablet`（zkcml tablet CSS 的 @tablet 測試，選擇器看不到 zkcml 的 diff，所以另外跑）：14 passed。
- `select-tests.mjs --base 237bfa8586`：partial，563/1709，8 頁。
  - 跑了它列出的指令，但拿掉 `--project=forced-colors-gallery`：該 project 會用 `page.screenshot({path})` 重寫 tracked 的 `*-forced-colors.png`，違反不修改 tracked file 的規則。
  - 結果：550 passed、3 skipped、5 failed，全部是 `font-size` project：
    - colorbox、combobox、datebox：`13px` 各多 3（例如 1732 → 1735），與實作者先前記錄的環境漂移相同。
    - tree-header、tree：分布差異較大（例如 tree `12px` 3 → 37）。
  - batch 6 的 `tree.css` diff 沒有任何 `font-size` 宣告，F3 只在 tablet stylesheet（桌面不套用）；這 5 個失敗我沒有在 HEAD 上重跑，所以「與本批無關」是依 diff 內容推論。
  - `mesh-header-footer-tablet` 與 `tablet` 已含在 partial 內，全部通過。
- 每次 run 後 `git status` 不變。

### 追補 2：p3 狀態

前後兩次的 `git status --porcelain`、`git diff --stat`、`git diff --cached --name-only` 以 `diff` 比對結果相同（scratchpad `f3/status-before-3.txt`、`status-after-3.txt`），兩個 repo 都沒有 staged 內容。

- 伺服器只 kill tail 70644，並等 appRun 結束；5 個 PID 都已不存在，8087 沒有 listener。
- 沒有建立探測頁（`build/inplaceWebapp/web/pv/` 沒有 repro／probe 頁）。
- 腳本：scratchpad `f3/probe6.cjs`、`narrow.cjs`、`wide.cjs`、`pngdiff.cjs`、`probe7.cjs`、`art2spec/`、`mut3/`；截圖：`f3/fu3/`、`f3/art2/`。
