# 批次 6：修 tablet 彈出層缺陷（NS23，follow-up 65）

日期：2026-10-20。工作位置：`/Users/hawk/Documents/workspace/ZK10/p3/zk`（分支 `gaps-round2`，HEAD `237bfa8586`）與 `/Users/hawk/Documents/workspace/ZK10/p3/zkcml`（分支 `gaps-round2`，HEAD `fe6323380`）。工作區未 commit、未 staged。Marble CSS，ZK-6112。依據：`doc/marble-theme-followups.md` #65、`doc/marble-tablet-popup-batch5-verification.md`、`doc/marble-test-gaps-round2-plan.md` §13、`.claude/skills/marble-theme/reference/batch-workflow.md`、`decision-principles.md`（引用 O1、O10、V6、E5）。

## 1. 各項狀態

| 項目 | 狀態 | 改動 |
|---|---|---|
| F2 colorpicker 列重疊 | 完成 | zkcml tablet `_inputs.css` |
| F3 colorbox tap 捲動頁面 | **未做，需要決定（E5）** | 無（見 §4） |
| F4 combobox sheet 末列被切 | 完成 | zkcml tablet `_inputs.css` |
| F5 tablet window／panel header 規則 | 完成 | zkcml tablet `_window.css` |
| F6 `.z-treecol-button` 死規則 | 完成 | zkcml tablet `_mesh.css`、zk `tree.css` |
| F9 datebox wheel-time 接縫 | 完成 | zkcml tablet `_wheel.css` |

沒有新增 `--zk-*` token，沒有新增 `!important`，沒有改 widget TS／Java／ZUL component。

## 2. 影響範圍清單（改之前列出，編輯後用 `select-tests.mjs --json --only <path> --base HEAD --zkcml-base HEAD` 記錄）

所有 zkcml tablet 規則只在 `tablet.css.dsp` 內；該 stylesheet 在桌面 UA 下是 `disabled` link（`reference/verification.md`「The tablet stylesheet is gated client-side」），所以桌面頁面看不到這些規則。下表的「頁面」是選擇器依 class 名稱對應到的頁面，不是實際受影響的頁面。

| 檔案與選擇器 | 能匹配的其他元件 | select-tests 回答 | 選到的頁面 |
|---|---|---|---|
| zkcml `tablet/_inputs.css`：`.z-colorpicker-hsv`、`.z-colorpicker-hex`、`.z-colorpicker`（新）、`.z-combobox-popup`（加 `max-height`） | `.z-colorpicker*` 也出現在 `<menu content="#color=…">` 的 `.z-menu-popup`（同一個 picker）；`.z-combobox-popup` 只屬 combobox（bandbox 的 popup 是 `.z-bandbox-popup`，不受影響） | partial，175/1708，uncovered 空 | colorbox、combobox、label、portallayout、pv/combobox-content、pv/input-invalid-guard、pv/invalid-size-guard、pv/popup-api-fixtures、signature、tabbox-misc、utility/grid-layout（`inputs.zul` 為 aggregate，不選） |
| zkcml `tablet/_window.css`：刪 `.z-window-header`、`.z-panel-header` 的 `min-height`（`.z-window-icon, .z-panel-icon` 不動） | window 與 panel 所有變體（modal、embedded、messagebox、caption 內的 panel） | partial，115/1708 | window、panel、caption、loading、portallayout、scrollview、各 layout 頁、pv/messagebox-multiline-guard、pv/popup-api-fixtures、utility/elevation 等 14 頁 |
| zkcml `tablet/_mesh.css`：群組 `.z-listheader-button, .z-column-button, .z-treecol-button` 刪 `.z-treecol-button` | 群組剩下的兩個選擇器內容不變 | partial，617/1708 | 23 頁（grid、listbox、tree 系列、pv/mesh-header-footer-guard 等） |
| zkcml `tablet/_wheel.css`：`.z-calendar-wheel-body:has(.z-timebox-wheel-time) .z-calendar-wheel-line`、`.z-calendar-wheel-body .z-timebox-wheel-line`（新） | 只在 datebox 的 time-in-popup 內；獨立 timebox 的 `.z-timebox-wheel-cave` 沒有 `.z-calendar-wheel-body` 祖先 | partial，156/1708 | calendar、datebox、timebox、grid-header、grid-paging、listbox-header、portallayout、pv/invalid-size-guard |
| zk `zul/.../sel/css/tree.css`：刪 `.z-treecol:has(.z-treecol-button) .z-treecol-sorticon`；兩條 checkable 規則的 `:not(.z-treecol-sort, :has(.z-treecol-button))` → `:not(.z-treecol-sort)` | `.z-treecol` 與 `.z-treecol-checkable`，桌面與 tablet 都載入 | partial，440/1708 | tree、tree-header、pv/mesh-header-footer-guard、pv/popup-rod-fixtures |
| 全部變更檔合併（含 `tablet.spec.ts`、4 張 PNG） | | partial，942/1708，uncovered 空 | 49 頁 |

specificity 確認：`:not(.z-treecol-sort, :has(.z-treecol-button))` 取列表中最大者，兩個參數都是一個 class（0,1,0）；改成 `:not(.z-treecol-sort)` 後仍是（0,1,0），specificity 不變。

## 3. 逐項修正

### F2 colorpicker 列重疊（decision-principles O1 + V6）

- 原因：tablet 的 `.z-textbox` 等輸入框 `min-height: var(--zk-touch-input-height)`（48px），而 zkex `colorbox.css` 把三列固定在 `top: 0 / 43 / 86px`、picker 高 460px、`overflow: hidden`。
- 修正：zkcml `zkmax/src/main/resources/web/zkmax/css/tablet/_inputs.css:126-134`（新增 12 行規則與註解）：`.z-colorpicker-hsv { top: calc(var(--zk-touch-input-height) + 3px) }`、`.z-colorpicker-hex { top: calc(2 * (var(--zk-touch-input-height) + 3px)) }`、`.z-colorpicker { height: calc(460px + 3 * (var(--zk-touch-input-height) - 40px)) }`。保留桌面的 3px 行距（43 − 40），picker 高度加上三列增加的高度（3 × 8 = 24px）。沒有改 zkex `colorbox.css`，桌面 picker 因為 tablet stylesheet 不載入而不變。
- 為何不用 flex + gap：三列的容器 `.z-colorpicker-info` 內還有絕對定位的按鈕，而 `.z-colorpicker` 的 `main`／`info` 靠 `top: 50px` 位移；改成 flex 要動 zkex 的桌面規則，所以採用 brief 允許的「tablet row offsets」。
- 量測（iPad UA、834x1112，`getBoundingClientRect` 的 top／bottom）：

| | RGB 列 | HSV 列 | HEX 列 | picker 底 | HEX 底與 picker 底 |
|---|---|---|---|---|---|
| 前 | 973–1021 | 1016–1064 | 1059–1107 | 1103 | HEX 超出 4px；RGB 與 HSV、HSV 與 HEX 各重疊 5px |
| 後 | 949–997 | 1000–1048 | 1051–1099 | 1103 | HEX 在 picker 內 4px；列距 3px |
| 桌面（前後相同） | 581–621 | 624–664 | 667–707 | 711 | HEX 在 picker 內 4px |

- 重切的 PNG：`zkpreview/doc/screenshots/colorbox-tablet-popup-picker.png`（`-g "tablet-popup colorbox.zul: tap the picker" --update-snapshots=changed`，只有這一個檔變動）。
- before／after：`scratchpad/b6/F2-before.png`、`F2-after.png`（tablet）；桌面 `F2-desktop-before.png`、`F2-desktop-after.png` 是同一個未改動的基準 `colorbox-popup-picker.png`（`popup` project 56 個通過，所以前後相同）。
- 新斷言：`tablet.spec.ts`「colorbox.zul: tap the picker icon to switch the popup to the picker」，三列 `.z-colorpicker-input` 的 `boundingBox`：RGB 底 ≤ HSV 頂、HSV 底 ≤ HEX 頂、HEX 底 ≤ picker 底。在截圖之前斷言。已移除 known-defect 註解。

### F3 colorbox tap 捲動頁面：停止，需要決定（E5）

見 §4。

### F4 combobox sheet 末列被切（O1）

- 原因：zul `combobox.css:153` 桌面 `max-height: 280px` 套到 JS 設為 300px 的 touch sheet。
- 修正：`_inputs.css:109-111`，在既有 `.z-combobox-popup` 規則加 `max-height: none;`（附一行原因註解）。這條不是 `!important`。桌面 combobox popup 不變。
- 量測：

| | popup 高 | `max-height` | 8 項、捲到底時末項 |
|---|---|---|---|
| 前 | 280（inline 300） | 280px | 1072–1132，viewport 1112，被切 20px（驗證者量測） |
| 後 | 300 | none | 1052–1112，完整在 viewport 內（temp 頁 `b6-probe.zul`，已刪） |

- 重切的 PNG：`combobox-tablet-popup-sheet.png`（834x292 → 834x312）、`combobox-tablet-popup-description.png`（同樣 +20px）。before／after：`scratchpad/b6/F4-before.png`、`F4-after.png`、`F4-description-before.png`、`F4-description-after.png`；8 項捲到底的截圖：`F4-8items-after.png`。
- 新斷言：`expectSheetKeepsJsHeight(popup)`（新 helper）：渲染高度 = inline `height`（> 0）。放進兩個 combobox 測試，在截圖之前斷言。

### F5 tablet window／panel header 規則（O10 清理）

- 修正：`_window.css` 刪除 `.z-window-header { min-height }` 與 `.z-panel-header { min-height }`（原 8–13 行），並修正檔頭註解：「Taller headers」改為「Larger header affordances」，說明 header 保持桌面高度（window 56px／panel 48px，兩者都已達 48px comfortable target），圖示 44px 放得進去。`.z-window-icon, .z-panel-icon` 規則不動。
- 量測（`/window.zul`、`/panel.zul`，所有 header 的 `[高度, computed min-height]`）：

| | window 高度 | window `min-height` | panel 高度 | panel `min-height` |
|---|---|---|---|---|
| 前（tablet） | 57, 56, 77, 76, 77, 76, 77, 76 | 48px | 56, 104×6, 56, 76, 76 | 48px |
| 後（tablet） | 完全相同 | 56px | 完全相同 | 48px |
| 桌面（前後相同） | 57, 56, 65, 64, … | 56px | 56, 80, 60, … | 48px |

  所以刪除後 header 的高度沒有變，只有 window 的 computed `min-height` 由 48 回到桌面的 56（原規則把下限降低）。panel 規則確實是 no-op。`window-tablet-popup-modal.png` 比對通過（沒有 PNG 變動）。
- 重切的 PNG：無。before／after：`scratchpad/b6/F5-before.png`（HEAD 基準）與 `F5-after.png` 是同一個像素內容（比對通過）；數值見上表。

### F6 `.z-treecol-button` 死規則（O10）

- 確認沒有任何產生者或讀取者（p3/zk 與 p3/zkcml，排除 node_modules、build、codegen）：
  - Java：`Treecols.java` 沒有 `setMenupopup`（`Columns.java`、`Listhead.java` 有）；
  - TS：`HeaderWidget.ts:748` 只在 `parent._menupopup` 有值時輸出 `$s('button')`，`sel/Treecols.ts`、`sel/Tree.ts` 沒有 `menupopup`，`sel/Treecol.ts` 沒有 `-btn` 的 click 綁定；
  - mold：`sel/mold/treecol.js` 不輸出 `-btn`；
  - `lang.xml`、`lang-addon.xml`、zkex／zkmax 的 JS 與 Java 沒有出現；本 repo 沒有 IceBlue LESS 或其他主題檔含 `treecol-button`。
  - 唯一讀取處：`zkpreview/src/test/playwright/mesh-header-footer.spec.ts:376` 的 `menuButton: '.z-treecol-button'` 只放進 `querySelectorAll` 的 obstacle 選擇器（永遠沒有匹配），**我沒有改它**（O10；它不是 Marble CSS）。
- 修正：zkcml `tablet/_mesh.css:37`：群組選擇器 `.z-listheader-button, .z-column-button, .z-treecol-button` → 只剩 `.z-listheader-button, .z-column-button`。zk `tree.css`：刪除 `.z-treecol:has(.z-treecol-button) .z-treecol-sorticon` 規則與其註解（原 437–441）；兩條 checkable 規則的 `:not(.z-treecol-sort, :has(.z-treecol-button))` → `:not(.z-treecol-sort)`（原 531、535，現 526、530）；原 513 行註解刪去「a header with the column-menu button keeps the room the button reserves」。`.z-column-button`、`.z-listheader-button` 的所有群組規則保留。
- 量測：在 tablet UA 與桌面 UA 下，對 `tree.zul`、`tree-header.zul`、`pv/mesh-header-footer-guard.zul`、`pv/popup-rod-fixtures.zul`、`grid.zul`、`listbox.zul` 的 `.z-treecol*`、`.z-column*`、`.z-listheader*` 元素，記錄 padding、margin、left／right、width、height、display、text-align 與 bounding box（共 12 組，`scratchpad/b6/f6-before.json`、`f6-after.json`）：`cmp` 結果位元組相同。沒有 PNG 變動。
- 沒有 before／after crop（沒有像素差異；以上計算值相同為證據）。

### F9 datebox wheel-time 接縫（V6）

- 原因：date 帶與 time 帶各自 `border-radius: 8px`；兩帶在 x = 417 剛好相接，內側四個圓角留下缺口。
- 修正：`_wheel.css:119-128`（新增 11 行）：`.z-calendar-wheel-body:has(.z-timebox-wheel-time) .z-calendar-wheel-line { border-start-end-radius: 0; border-end-end-radius: 0 }`、`.z-calendar-wheel-body .z-timebox-wheel-line { border-start-start-radius: 0; border-end-start-radius: 0 }`。只動內側圓角。
- 量測：date 帶 `border-radius` `8px` → `8px 0 0 8px`，time 帶 `8px` → `0 8px 8px 0`；兩帶邊界 x 417 不變、背景相同（`rgb(232, 238, 247)`）。
- 重切的 PNG：`datebox-tablet-popup-wheel-time.png`。其餘同 `-g "tablet-popup (datebox|timebox)"` 的兩個測試（`datebox-tablet-popup-wheel.png`、`timebox-tablet-popup-wheel.png`）沒有變動。before／after：`scratchpad/b6/F9-before.png`、`F9-after.png`。
- 新斷言：date 帶右緣 = time 帶左緣（`toBeCloseTo(…, 0)`），date 帶右上／右下、time 帶左上／左下圓角為 `0px`。在截圖之前斷言。

## 4. 沒做的項目：F3，附選項

結果：不用 `!important` 做不到。

- 證據（`scratchpad/b6/f3.cjs`，`pv/popup-tablet-fixtures.zul` 暫時加了 3000px spacer 與一個 colorbox，量完已還原，沒有留在 repo；測試沒有加）：
  - ZK 在 popup 的 inline style 寫 `position: absolute; top: 1112px; transform: translate(0px, -318px)`，tablet.css 是 unlayered 的 normal 宣告，輸給 inline style。
  - 注入 `.z-colorbox-popup { position: fixed; inset: auto 0 0; margin: 0 auto; transform: none }`（無 `!important`）：scrollY 0 → 565，trigger y 340 → −225（仍然跳）。
  - 注入同一組加 `!important`（verifier 的探測）：scrollY 維持 0，trigger y 340 不變，popup 貼 viewport 底。
  - 未加 `!important` 的原頁面：scrollY 0 → 579，trigger y 340 → −239。
- 選項：
  - A. 允許新增 `!important`：在 tablet `_inputs.css` 為 `.z-colorbox-popup` 加 `position: fixed !important; inset: auto 0 0 !important; margin-inline: auto; transform: none !important`，比照 combobox 與 wheel 現有的 3 個 `!important`（`_inputs.css`、`_wheel.css` 的註解已寫原因）。需要用 `popup-tablet-fixtures.zul` 加一個 spacer 與 colorbox 並寫測試（scrollY 不變、trigger 仍在螢幕、popup 底邊貼 viewport 底）。358px 寬是 ZK JS 置中的設計，宣告 `margin-inline: auto` 與 `width: fit-content` 後仍需實測 x 位置。成本：count-important 加 3 個，並新增一個 `animation` 才能保留滑入動畫。
  - B. 依 O2／O3 開 ZK Jira：`colorbox-touch.ts` 的 `top = innerHeight + scroll`、focus 落在 viewport 外的元素會讓瀏覽器捲動頁面；JS 修好之後 Marble 不需要 `!important`。需要使用者同意開 Jira（E6）。
  - C. 維持現狀並記入 followups（桌面與 combobox／wheel 的行為不一致）。
- 建議：A（V6：同角色同行為，combobox 與 wheel 已經這樣處理），再另開 B 讓核心團隊移除對 CSS 蓋掉 inline style 的需要。這是 E5 的情況，所以我沒有動。

## 5. 突變證明（一次一個；改 → 重建 jar → 重啟 → 跑該項測試 → 以備份檔還原，不用 `git checkout` 以免丟掉未 commit 的真實改動）

| ID | 突變 | 測試 | 結果 |
|---|---|---|---|
| M1 | `_inputs.css` `.z-colorpicker` `height: calc(…)` → `height: 460px` | colorbox picker | 失敗：`HEX input ends inside the picker`，Expected ≤ 1103，Received 1123 |
| M2 | `.z-colorpicker-hsv` `top: calc(…)` → `top: 43px` | colorbox picker | 失敗：`RGB row ends above the HSV row`，Expected ≤ 992，Received 997 |
| M3 | `.z-combobox-popup` `max-height: none` → `280px` | combobox 2 個 | 兩個都失敗：`sheet is as tall as its inline height`，Expected 300，Received 280 |
| M4 | `_wheel.css` time 帶 `border-end-start-radius: 0` → `8px` | datebox time-in-popup | 失敗：`time band bottom-left corner`，Expected `0px`，Received `8px` |
| M5 | `.z-calendar-wheel-line` `right: 0` → `4px` | datebox time-in-popup | 失敗：`the date band ends where the time band starts`，Expected 417，Received 413 |

每次突變失敗的位置都在截圖之前，所以失敗訊息指向該斷言；還原後 `diff` 與備份相同。F5、F6 沒有新增斷言（刪除規則；以量測證明）。所有突變後都重建過乾淨的 zkcml jar 再繼續。

## 6. 測試執行（伺服器：8087，自己的 p3/zk/zkpreview）

- 選擇器對全部變更檔的回答：`partial`，942/1708，`uncovered` 空，不是 `full`。該指令（`scratchpad/b6/partial-cmd.sh`，含 `chromium`、`gallery`、`brand`、`compact`、`smoke`、`component-theme-variables`、`font-size`、`focus-scan`、`forced-colors-gallery`、`popup`、`tablet`）在兩個獨立 process 各跑一次：
  - 第 1 次：882 passed、19 skipped、41 failed = 39 font-size（環境漂移，只報數量）+ `screenshot.spec.ts:824 window gallery`（`window-04-popup-and-modal.png`，1025 px）+ `gallery-scan.spec.ts:70 tabbox-misc`（`tabbox-misc-02-horizontal-with-caption-in-tab.png`，176 px）。這兩個是 `reference/verification.md`「Flaky galleries」列的 window／tabbox 熱頁，`tablet.spec.ts` 註解也記了 tabbox 的 176 px；第 2 次通過。
  - 第 2 次：884 passed、19 skipped、39 failed = 39 font-size。
  - 19 skipped 是 `forced-colors` 等既有 skip，未逐項查（未確認）。
  - 跑完後 `forced-colors-gallery` 重寫了 5 個 `*-forced-colors.png`（caption、panel、signature、tree、window），已 `git checkout --` 還原。
- `--project=tablet` 全部：74 passed（1.3 分）。
- `--project=popup` 全部（`--workers=2`）：56 passed（1.6 分）。
- 兩次完整 partial 與 tablet、popup 都用 `--update-snapshots=none`；沒有新 PNG 出現。

## 7. Lint 與檢查

- `npm run lint:css` zk：0 error，1 warning（`zul/.../grid/css/grid.css:119` duplicate selector，不是本批改的檔案）。
- `npm run lint:css` zkcml：0 error，6 warning（goldenlayout 3、cascader 2、searchbox 1，都不是本批改的檔案）。
- `check-css-dsp.js`：zul、zkmax、zkex 各回 `All css-uri ZK requests are present in the build.`（`--zk-home /Users/hawk/Documents/workspace/ZK10/p3`）。

## 8. 變更檔清單

p3/zk：
- `zkpreview/src/test/playwright/tablet.spec.ts`
- `zkpreview/doc/screenshots/colorbox-tablet-popup-picker.png`
- `zkpreview/doc/screenshots/combobox-tablet-popup-sheet.png`
- `zkpreview/doc/screenshots/combobox-tablet-popup-description.png`
- `zkpreview/doc/screenshots/datebox-tablet-popup-wheel-time.png`
- `zul/src/main/resources/web/js/zul/sel/css/tree.css`

p3/zkcml：
- `zkmax/src/main/resources/web/zkmax/css/tablet/_inputs.css`
- `zkmax/src/main/resources/web/zkmax/css/tablet/_mesh.css`
- `zkmax/src/main/resources/web/zkmax/css/tablet/_wheel.css`
- `zkmax/src/main/resources/web/zkmax/css/tablet/_window.css`

before／after 與探測腳本：`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/7d5b9c66-28ef-4caf-8453-4b21d3a621a0/scratchpad/b6/`。

## 9. 限制（未確認）

- 未確認：iOS Safari 實機；全部是 Chromium、iPad UA、834x1112。
- F4：沒有為「≥ 6 項、捲到底」加測試（combobox 預覽頁最多 3 項）；只斷言 sheet 高度等於 JS inline 高度，並以臨時頁量過 8 項的情況。
- F2 以 `top` 偏移實作，列距 3px 與 460px／40px 是 zkex 桌面值的拷貝；zkex 桌面值若改，這三條要同步。
- F2 的三條規則也影響 `<menu content="#color=…">` 的 picker（同一個 `.z-colorpicker`）；沒有針對該 mold 另量（未確認），選擇器把 colorbox 頁列入。
- F6：`mesh-header-footer.spec.ts:376` 仍引用 `.z-treecol-button`，沒有改。
- F3 見 §4。
- 還沒有獨立 verifier 檢查本批；`batch-workflow.md` 步驟 5 與 7（獨立驗證、local commit）不在這次範圍。
- 19 個 skipped 沒有逐項查。

## 10. 清理

- 伺服器：只用 `tail -f /dev/null` 持有 stdin 的 8087；最後一組記錄的 PID TAIL 24153、CLIENT 24154、JETTY 24980，已停，`lsof -iTCP:8087 -sTCP:LISTEN` 無輸出。每次重啟都只 kill 該次記錄的 PID；沒有用 `pkill`／`killall`，沒有動 8085、8105 與其他 session 的程序，沒有停 Gradle daemon。
- `zkpreview/build/inplaceWebapp/web/b6-probe.zul` 已刪；`pv/popup-tablet-fixtures.zul` 已還原（source 與 inplaceWebapp 皆與 HEAD 相同）。
- p3/zk status：上列 6 個檔（`node_modules` 與 `zkpreview/node_modules` 為原有 untracked）；p3/zkcml status：上列 4 個檔；兩邊都沒有 staged 內容。
- 最後的 jar 是含全部變更的乾淨版本（突變都已還原後重建）。

## F3：tablet 點 colorbox 會捲動頁面（D39 = A，follow-up 65）

做法（使用者核准的 3 個 `!important`，沒有其他）：p3/zkcml `zkmax/src/main/resources/web/zkmax/css/tablet/_inputs.css` 新增 `.z-colorbox-popup { position: fixed !important; inset: auto 0 0 !important; transform: none !important; }`，與 combobox sheet、datebox wheel 同一做法，註解寫明原因（colorbox-touch.ts `_syncPosition` 寫 inline `top`／`transform`）。沒有改 JS／Java、沒有新 token。

before／after（臨時頁 `pv/colorbox-scroll-repro.zul`，iPad UA、834x1112、真實 `touchscreen.tap`，頁面先捲到 scrollY 300）：

| | scrollY（tap 前 → 後） | trigger 的 top | popup |
|---|---|---|---|
| 修正前 | 300 → 879 | -479（離開 viewport） | absolute，top 215 |
| 修正後 | 300 → 300 | 100 | fixed，top 794、bottom 1112 |

臨時頁與 `build/inplaceWebapp/web/pv/` 的複本已刪，舊 URL 回 404，未 commit。

測試與 fixture：
- `pv/popup-tablet-fixtures.zul` 加 colorbox 與 1500px spacer（頁面可捲動）。
- `tablet.spec.ts` 新增「colorbox: tap to open the popup on a scrolled page, the page does not scroll」：捲到 100、真實 tap、popup settled 後斷言 popup 底邊 = viewport 底、scrollY 不變、trigger 仍在 viewport。
- 既有「colorbox.zul: tap to open the palette」的 `position` 斷言從 `absolute` 改為 `fixed`（修正使它必然改變）。

突變證明（Playwright `context.route` 改寫 `tablet.css.dsp`，用暫存 spec 複本，已刪）：不突變通過；只拿掉 `position: fixed` → 底邊 1012（預期 1112）失敗；只拿掉 `inset` → 1530 失敗；只拿掉 `transform: none` → 794 失敗；整條規則拿掉 → scrollY 679（預期 100）失敗。只斷言 scrollY 時，單獨拿掉任一宣告仍會通過（absolute + bottom 0 錨在第一屏），所以加了「底邊 = viewport 底」斷言。

PNG baseline：`colorbox-tablet-popup-palette.png`、`colorbox-tablet-popup-picker.png` 合法改變（popup 由置中小面板變成全寬 sheet，`inset: auto 0 0` 的結果），用 `--update-snapshots=changed` 並限定 `-g "tablet-popup colorbox"` 重拍。picker PNG 原本就有 batch 6 的未提交修改，現在是兩者疊加後的版本。

Playwright（`PREVIEW_URL=http://127.0.0.1:8087`，repo config）：`tablet` 第一次 75 passed、第二次 75 passed；`popup` 第一次 55 passed 1 failed（`datebox.zul: open calendar with the timezone selector`，單獨重跑通過），第二次 56 passed。`select-tests.mjs --base 237bfa8586`：partial（563/1709，8 pages），原因包含 batch 6 的檔案；未自行跑全測。

未確認：tablet 一次中間的指令曾有 3 個 colorbox 測試連續失敗一次（position 為 absolute、rows 重疊，看似 tablet CSS 未套用），同一指令緊接重跑即恢復；後續 tablet 2 次、colorbox 數次都沒有再現，原因未查明。

伺服器：8087，sh 33321、tail 33328、client 33329、daemon 33350、Jetty 33734（前一組 30277／30279／30280／30318／30950），只 kill tail 並等其結束，皆已停，沒有用 pkill，沒有動其他 Gradle daemon。

## F3 追補（對稱與動畫）

驗證者在 F3 的 `.z-colorbox-popup` 規則上找到兩個副作用，皆依 V6（同角色同外觀，對齊 combobox sheet 與 wheel picker）處理，decided by V6。

**A. 左右不對稱：已修（`zkmax/.../tablet/_inputs.css`）。** 量測（iPad UA 834x1112，`touchscreen.tap`，頁面已捲動 100px）：修前 DOM left 0／right 16，PNG 像素 1／17；combobox sheet 為 10／10。原因是 colorbox.css 的 `max-width: calc(100% - 16px)` 加上 `inset: auto 0 0` 使 `right` 在 LTR 下被忽略，JS 寫入的 inline `left:8px` 也被蓋掉。修法是兩行一般宣告（無新增 `!important`）：`max-width: calc(100% - 20px)` 與 `margin-inline: auto`。修後 DOM 10／10，PNG 像素 11／11（多出的 1px 是 popup 的 border），與 combobox sheet 的 10／10 一致；`position: fixed`、bottom = viewport bottom、scrollY 100 → 100 不變。

**B. 滑入動畫：未加，因為依指示的那一行無效。** 依指示補上與 combobox sheet 相同的 `animation: zk-bottom-sheet-up ...` 後，用 `requestAnimationFrame` 取樣（約 290 個 frame）：`animation-name` 是 `zk-bottom-sheet-up`、`getAnimations()[0].currentTime` 由 0 遞增，但 `getBoundingClientRect().top` 全程固定 794（colorbox）。原因是 keyframe 改的是 `transform`，而同一元素有 `transform: none !important`，作者層的 `!important` 宣告優先於 animation，keyframe 不會生效。combobox sheet（top 全程 812）與 wheel picker（`_wheel.css` 同樣寫法）有同樣現象，所以這是三個 sheet 共有的既有情況：它們現在都沒有滑入動畫，colorbox 與它們一致。因此我把 colorbox 上這一行拿掉，不留無效宣告，也沒有改 combobox／wheel。要讓三個 sheet 真的滑入，需要另一個決定（例如改用 `translate` 屬性的 keyframe，或不再對 `transform` 用 `!important`），不在這次的範圍。

**測試。** `tablet.spec.ts` 的 F3 測試（"colorbox: tap to open the popup on a scrolled page..."）新增兩個斷言：左右 gap 差 ≤ 1px，且左 gap ≈ 10px（與 combobox sheet 同）。截圖前本來就用 `settledBox` 等位置穩定且 `animations: 'disabled'`，不會截到動畫途中。變異：用 `context.route` 把 `tablet.css.dsp` 內的 `margin-inline:auto;` 移除，對稱斷言失敗（left gap 0px、right gap 20px），未變異則通過。baseline 只更新 `colorbox-tablet-popup-palette.png` 與 `colorbox-tablet-popup-picker.png`。Playwright `tablet` project 兩次各 75 passed，`popup` project 56 passed。

## 追補 2（popup 貼合內容寬度、F4 fixture）

本節依 owner 的指示取代上一節「左右對稱 10px gutter、與 combobox sheet 同寬的整寬 sheet」的決定：owner 看過 before/after 截圖後，要求 colorbox popup 只貼合內容寬度，不要比內容寬很多。上一節的 B（滑入動畫未加）不受影響。

**F2/F3：colorbox popup 貼合內容（`zkmax/.../tablet/_inputs.css`）。** 原先 `max-width: calc(100% - 20px)` 加 `inset: auto 0 0` 使 popup 在 834px 視窗拉成 814px。第一次嘗試的 `width: fit-content` 無效：ZK 寫入的 inline `width: auto` 優先於任何一般宣告的 `width`，量測仍是 814px，改用 `!important` 不在許可範圍。改成 `max-width: fit-content`：inline `width: auto` 配 left/right 0 先撐滿，再被 `max-width` 夾到內容寬，`margin-inline: auto` 置中。沒有新增 `!important`、token、JS 或 Java；原本的 `max-width: calc(100% - 20px)` 窄視窗保護被取代，因為 `fit-content` 本身不會超過可用寬度，colorbox.css 的 `overflow-x: auto` 仍負責更窄時的捲動。
量測（iPad UA，`touchscreen.tap`，頁面捲動 100px）：834px 視窗 popup 寬 358px（palette 340 + padding 16 + border 2；picker 為 328（310 + padding 16 + border 2）；驗證者量測更正，原文誤寫為 358），左右 gap 238／238，bottom = viewport bottom（794 + 318 = 1112）；600px 視窗寬 358px，gap 121／121；popup 的 `scrollWidth` 356 = `clientWidth` 356，內容沒有被裁切。PNG 像素：左右陰影 gap 6／6，下方 gap 0，與 HEAD 的 PNG 尺寸相同（palette 382x330，picker 352x514，picker 比 HEAD 高 24px 是 F2 的列距修正）。其他 consumer（colorbox.zul、signature.zul、portallayout.zul、usecase/brand-switcher.zul、inputs.zul）在 834 與 600 都開在視窗內、寬 358px；`pv/colorbox-content.zul` 的 menu variant 是 `.z-menu-popup`，不受這條規則影響（`position: absolute`，寬 358px，在視窗內）。`signature.zul` 在 600px 視窗的 layout viewport 是 632px（頁面本身橫向溢出，與這次修改無關），popup 在 632px 內置中。
測試：F3 測試的對稱斷言改成「左右 gap 差 ≤ 1px」加「popup 寬 ≤ 最寬子元素 + padding + border + 1px」且「< viewport 的 60%」。變異：以 `context.route` 移除 served 的 `tablet.css.dsp` 內的 `max-width:fit-content;`（先斷言恰好命中 1 次），斷言失敗（818 > 359）；未變異則通過。baseline 只更新 `colorbox-tablet-popup-palette.png` 與 `colorbox-tablet-popup-picker.png`。

**F4：combobox fixture 改為 8 個 comboitem。** 原本 `combobox.zul` 的 combobox 只有 2 個 comboitem，sheet 的 5 列（5 x 60px = 300px）填不滿，看不到列被切掉。`combobox.zul` 與 gallery、popup、forced-colors、screenshot 等多個 spec 共用，所以不動它，改在只有 tablet spec 使用的 `pv/popup-tablet-fixtures.zul` 加兩個各 8 個 comboitem 的 combobox（一個一般、一個含 description）；兩個 F4 測試改開這個頁面，PNG `combobox-tablet-popup-sheet.png` 與 `combobox-tablet-popup-description.png` 重拍，F9 沒動。新增 `expectLastVisibleRowEndsOnSheetBottom`：以真實 tap 開啟後，確認 comboitem 多於 5 個、完整可見的列恰好 5 個、第 5 列的 bottom 與 sheet 的 bottom 差 ≤ 1px。
變異：用 `context.route` 把 `max-height:none` 還原成 `max-height:280px`（先斷言恰好命中 1 次），既有的 `expectSheetKeepsJsHeight` 在兩個測試都失敗（rendered 280、requested 300，差 20px，即 HEAD 切掉第 5 列 20px）；未變異則兩個測試都通過。

## D41 = A 實作

日期 2026-10-20。工作樹：`p3/zk` @ `74fd35c271`、`p3/zkcml` @ `234cbfa0f`（分支 gaps-round2）。預覽伺服器 8087（自己啟動、已停止）。依據：`doc/marble-fixed-sheet-overflow-investigation.md` 的 c1 與 b。

### c1：三個 bottom sheet 的 `inset` 值（沒有新增 `!important`、token、JS、Java）

| 檔案 | 規則 | 舊值 | 新值 |
|---|---|---|---|
| zkcml `zkmax/.../css/tablet/_inputs.css` | `.z-combobox-popup` | `auto 10px 0` | `auto calc(100% - 100vw + 10px) calc(100% - 100dvh) 10px` |
| 同上 | `.z-colorbox-popup` | `auto 0 0` | `auto calc(100% - 100vw) calc(100% - 100dvh) 0` |
| zkcml `zkmax/.../css/tablet/_wheel.css` | datebox / timebox wheel（一條規則兩個 selector） | `auto 10px 0` | `auto calc(100% - 100vw + 10px) calc(100% - 100dvh) 10px` |

`!important` 數量不變（colorbox 規則仍是 position、inset、transform 三個；`max-width: fit-content; margin-inline: auto` 未動）。`100%` 是 fixed 的 containing block（layout viewport），`100vw` / `100dvh` 是可視區；差值在頁面放得下時為 0。三條規則的註解都寫了原因與代價。

代價（量測值，已寫進註解）：pinch 縮小或捲到頁尾時，sheet 浮在可視區下緣上方 88 / 556px（仍完整可見）。

### fit 頁幾何（834×1112，iPad UA，tap 開啟；`x, y, width, height`）

| sheet | 改前 | 改後 |
|---|---|---|
| colorbox（`pv/popup-tablet-fixtures.zul`） | 238, 794, 358, 318 | 238, 794, 358, 318 |
| combobox（同上） | 10, 812, 814, 300 | 10, 812, 814, 300 |
| datebox wheel（`datebox.zul`） | 10, 908, 814, 204 | 10, 908, 814, 204 |

完全相同。量測腳本：scratchpad `eng5/geom.js`。

### b：`signature.zul`

三個元件（兩個 `<signature width="600px">`、一個 `<image width="600px">`）加 `sclass="z-max-w-100"`。600 寬時頁寬 632 → 600。

筆跡驗證（真實 `page.mouse`，桌面 UA，讀 canvas 像素，腳本 `eng5/stroke.js`）：

| 情境 | canvas 屬性寬 / CSS 寬 | 按下位置（CSS px） | 墨跡位置（CSS px） |
|---|---|---|---|
| 600 寬載入 | 534 / 534 | 133.5、400.5 | 132、399 |
| 1000 寬載入 | 598 / 598 | 149.5、448.5 | 148、447 |
| 1000 寬載入後縮到 600 | 534 / 534 | 133.5、400.5 | 132、399 |

canvas 屬性寬與 CSS 寬一致，沒有縮放：`Signature.onSize()` 每次都用 `canvas.offsetWidth` 重設 canvas 尺寸。調查者先前看到的 598 → 534 是載入後才注入 class、沒有觸發 `onSize` 的人工情境。結論：用 `z-max-w-100`，不需要改成固定寬度。

不動的頁面（O10，列入 followups）：600 寬 overflow 且有 sheet trigger 的 `grid-header`（表格 700px）、`tabbox-misc`（tabbox 680px）、`preview`（`z-grid-col-full` 1207px）；沒有 sheet trigger 的 `carousel`、`checkbox`、`grid-detail`、`navbar`、`responsive-grid`、`stepbar`、`tabbox`；834 寬 overflow 的 `responsive-grid`（grid 820px）、`tabbox`（flex 欄 834px）。

### 測試

- 新增 1 個 tablet 測試：`tablet.spec.ts` 的 `tablet-popup-page-wider-than-screen`，頁面 `pv/popup-tablet-overflow-fixtures.zul`（只給 tablet spec 用，`popup-tablet-fixtures.zul` 未動）。600×1112、真實 `touchscreen.tap`，對 colorbox、combobox（8 items）、datebox wheel 逐一斷言：sheet 下緣與 `visualViewport` 下緣差 ≤ 1px、左右在可視區內、頁面未捲動、trigger 仍在可視區內。
- 變異證明（Playwright `context.route` 改寫 served `tablet.css.dsp`，命中次數斷言 1 與 2）：舊 inset 值放回去，新測試失敗，三個 sheet 都被抓到（combobox 與 wheel 下緣差 556px、右緣超出 890 對 601；colorbox 右緣 629 對 601）；沒變異時通過。暫時的變異 spec 與 config 已刪除。
- `tablet` Playwright project 兩次各 76 passed（75 + 新測試）。
- `popup` Playwright project：55 passed、1 failed（`daterangebox-popup-open-today.png`，已知，marble commit 76b51e9e6 造成，與本次無關）。已知 flake `datebox.zul: open calendar with the timezone selector` 這次沒有出現。
- `forced-colors-tablet`：14 passed。
- `select-tests.mjs --base 74fd35c271`：partial（81 / 1733 個測試、2 頁：`signature`、`pv/popup-tablet-overflow-fixtures`）。排除 `forced-colors-gallery` 後執行 80 個：79 passed、1 failed——`font-size` Playwright project 的 `signature.zul`，`13px` 是 612、基準 609，即已知的每頁 +3 個 13px 元素環境漂移。
- PNG：沒有任何基準 PNG 改變（`git status` 只有下列檔案），不需要 re-shot。

### 這次的 hunk

- zkcml：`zkmax/src/main/resources/web/zkmax/css/tablet/_inputs.css`、`_wheel.css`。
- zk：`zkpreview/src/main/webapp/web/signature.zul`、`zkpreview/src/test/playwright/tablet.spec.ts`、新檔 `zkpreview/src/main/webapp/web/pv/popup-tablet-overflow-fixtures.zul`。
- 未 stage、未 commit。
