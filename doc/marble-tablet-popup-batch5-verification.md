# 批次 5 獨立驗證：tablet 彈出層截圖

日期：2026-10-20。驗證者：獨立 verifier（非實作者）。對象：`/Users/hawk/Documents/workspace/ZK10/p3/zk`（HEAD `921f92506d`）未提交的 `tablet.spec.ts`（describe `tablet-popup`，16 個測試）、`pv/popup-api-fixtures.zul`（`z-flex-wrap`）、新 `pv/popup-tablet-fixtures.zul`、16 張新 PNG；`p3/zkcml` `fe6323380`。依據：`doc/marble-test-gaps-round2-plan.md` §12、`doc/marble-tablet-popup-batch5-report.md`（以下稱「實作報告」）、`doc/marble-tablet-popup-coverage-measure.md`、CLAUDE.md 工作守則、`.claude/skills/marble-theme/reference/{verification,decision-principles,zul-authoring}.md`。

## 結論：PASS-WITH-FINDINGS

16 個測試成立：都以真實 `page.touchscreen.tap` 開啟、「恰好一個可見」有落實、截到的都是 tablet 版彈出層、`tablet` 74/74 與 `popup` 56/56 通過、PNG 沒有變動、選擇器選得到。可以 commit，但有以下問題：

- 截圖容忍值 `threshold: 0.05` 有盲點：我做的突變之一（notification 關閉鈕加上 8% state layer）沒被抓到（F1）。
- 有 3 個 Marble CSS 缺陷：colorpicker 列重疊、colorbox sheet 在可捲動頁面上跳位、combobox sheet 末列被切 20px（F2–F4）。其中 F2 已錄進基準圖。
- 實作報告有 2 處事實錯誤（F5）。

建議先處理 F1，也可以先 commit、再另開工作處理。F2–F4 依 decision-principles O1/O4 歸 ZK-6112，修正後要重切相關基準。

## 起訖狀態（相同）

| 項目 | 開始 | 結束 |
|---|---|---|
| `git -C p3/zk status --porcelain` | 2 個 M、19 個 `??`（16 PNG、新 fixture、兩個 `node_modules`） | 逐行相同（`diff` 無輸出） |
| `git diff \| shasum` | `9833f863162aee450739f644afbf56c9e690edd8` | 相同 |
| 未追蹤檔 shasum（`zkpreview/doc`、`zkpreview/src`） | 存於 scratchpad `v5/untracked-start.sha` | 相同（16 張 PNG 在任何執行中都沒有被改寫） |
| staged | 0 | 0 |
| `p3/zkcml` | clean，`fe6323380` | clean，`fe6323380` |

## 檢查 1：16 張 PNG 與 5 項觀察

### 1a. 截圖時機（colorbox 300ms transition）

- 機制：`colorbox-touch.ts` `_syncPosition` 設 `top = innerHeight + scroll`，再以 jQuery transit 做 `translate(0,-outerHeight)` 300ms。
- 實測（tap 後每 50ms 讀一次）：palette 在第一次讀取（tap 返回後 7ms）就已在 y 794，之後 12 次不變；切到 picker 後 14 次都是 y 634。
- 測試有三層保護：`settledBox` 要 3 次連續相同讀取（間隔 100ms，至少 300ms 靜止）；`animations: 'disabled'`；`toHaveScreenshot` 本身要兩幀相同。
- 結論：每次都在靜止後截圖。另有一點證據：最後的 74/74 與第一次執行都沒有改寫任何 PNG（shasum 相同）。

### 1b. 逐張判定

| # | PNG | 判定 | 說明 |
|---|---|---|---|
| 1 | `combobox-tablet-popup-sheet.png` | OK（含 by-design 空白） | 滿版 bottom sheet、2 項。下方 160px 空白是 ZK JS 設計（見 F4）。底邊貼 viewport，沒有下 PAD，符合預期 |
| 2 | `combobox-tablet-popup-description.png` | OK | 同上，含描述行 |
| 3 | `datebox-tablet-popup-wheel.png` | OK | 三欄 wheel、選取帶、OK／Cancel；不是格狀月曆 |
| 4 | `datebox-tablet-popup-wheel-time.png` | OK，有外觀觀察 | 日期帶與時間帶各自圓角，相接處有缺口（F9） |
| 5 | `timebox-tablet-popup-wheel.png` | OK | 四欄 wheel |
| 6 | `colorbox-tablet-popup-palette.png` | OK | 靜止於底部；這頁不能捲動，所以看不到 F3 |
| 7 | `colorbox-tablet-popup-picker.png` | **把缺陷錄進了基準** | RGB／HSV／HEX 三列互相重疊 5px，HEX 下框被切 4px（F2）。修正後要重切 |
| 8 | `menubar-tablet-popup-open.png` | OK | 只有 PAD 範圍內露出頁面背景，與桌面慣例一致 |
| 9 | `menubar-tablet-popup-open-checked.png` | OK | 勾號 24px |
| 10 | `combobutton-tablet-popup-open.png` | OK | |
| 11 | `grid-tablet-popup-column-menu.png` | OK | 左上角露出欄按鈕的 state layer。這是 tap 後模擬游標停在原處造成的，可重現（3 個 process 雜訊 0），不算不穩定內容 |
| 12 | `listbox-tablet-popup-header-menu.png` | OK | 同上 |
| 13 | `errorbox-tablet-popup-open.png` | OK | 左邊的紅框是 invalid 欄位本身；沒有游標（tap 到 (400,20) 後 blur，那一點是頁面空白處 `.z-p-8`） |
| 14 | `notification-tablet-popup-info.png` | OK | notification 貼在 viewport 右上角（x 546、y 0），所以上方與右方沒有 PAD。桌面也一樣（x 992、y 0），不是 tablet 問題 |
| 15 | `window-tablet-popup-modal.png` | OK | header 56、關閉鈕 44 |
| 16 | `confirmpopup-tablet-popup-open.png` | OK，有 fixture 觀察 | popup 被 ZK 夾在 x = 0（按鈕在 x 32–149，置中時應為 x ≈ −14），所以沒有左 PAD，而且測到的是「貼邊夾住」的位置（F8） |

畫面上沒有 caret、沒有拍到動畫中途。

### 1c. 實作報告的 5 項觀察：重現與分類

我以 Playwright（iPad UA、834x1112、`hasTouch`、`isMobile`）重現，並與桌面（1280x720）比較。

| 觀察 | 重現結果 | 根因 | 分類（decision-principles §1） |
|---|---|---|---|
| ① colorbox picker HEX 被切約 4px | **重現，而且比描述的嚴重**：tablet 上三列輸入框各 48px 高（`.z-textbox, .z-intbox { min-height: var(--zk-touch-input-height) }`，tablet `_inputs.css`），但 zkex `colorbox.css` 把三列用 `position:absolute` 固定在 `top: 0 / 43px / 86px`，`.z-colorpicker { height: 460px; overflow: hidden }`。實測 RGB 973–1021、HSV 1016–1064、HEX 1059–1107，`.z-colorpicker` 底邊 1103：兩處各重疊 5px，HEX 被切 4px。桌面：40px 高、間隔 3px、HEX 底 707 < 711 | 全在 Marble CSS（zkcml zkex `colorbox.css` + zkmax tablet `_inputs.css`）。popup 本身沒有切到內容（scrollHeight = clientHeight = 476），實作報告推測的「sheet 錨定高度」不是原因 | **Marble CSS 缺陷 → ZK-6112**（F2） |
| ② combobox sheet 160px 空白 | 重現：popup inline `height:300px`、cave 300px，computed 280px | `combo-touch.ts` 固定 `height = _items(5) × _itemHeight(60)`，與項目數無關，所以空白是 ZK touch JS 的設計，任何主題都一樣。高度是 280 而不是 300，是因為 Marble 桌面 `combobox.css:153` 的 `max-height: 280px`。這帶出另一個缺陷：8 個項目捲到底時，最後一項在 1072–1132，viewport 底是 1112，被切 20px | 空白：**by design（O6）**。280px 上限：**Marble CSS 缺陷 → ZK-6112**（F4） |
| ③ colorbox sheet `absolute`、358px 寬，不是 fixed 滿版 | 重現。並證實實作報告「未測」的情況：在頁面上加 3000px spacer 讓頁面可以捲動（只在探測中加，沒有改 fixture），tap colorbox 後頁面從 scrollY 0 跳到 579（從 100 跳到 679），palette 停在 y 215–533，也就是畫面中央，觸發元件被捲出畫面。同樣條件下 combobox 停在 832–1112，scrollY 不變 | 機制在 ZK JS：`colorbox-touch.ts` 的 `top = innerHeight + scroll`；focus 落在 viewport 外的 `.z-colorbox-paletteicon`，瀏覽器因此捲動頁面。Marble 對 combobox（`_inputs.css`）與 wheel（`_wheel.css`）已經用 CSS 覆寫同一個機制，只有 colorbox 沒有。探測注入 `position:fixed!important; inset:auto 0 0!important; margin:0 auto; transform:none!important` 後，scrollY 維持 0／100，sheet 在 794–1112 | 錨定：**Marble CSS 缺陷 → ZK-6112**（O4 + V6，比照 combobox 先例）（F3）。358px 寬是 ZK JS 置中的設計，不改（O6）。JS 端要不要另開 Jira 屬 E6，交使用者決定 |
| ④ window header `min-height` 是 no-op | 重現，但實作報告的數字錯：`--zk-touch-target-comfortable` 是 **48px**，不是 56px。tablet 上 `.z-window-header` 的 computed `min-height` 從桌面的 56px **降到** 48px；高度仍是 56／57，因為內容撐住。`.z-panel-header` 桌面與 tablet 的 `min-height` 都是 48px，同樣是 no-op（實作報告說 panel 規則有作用，這也不對） | Marble tablet `_window.css` | **Marble CSS 缺陷（低，清理）→ ZK-6112**（F5） |
| ⑤ `.z-treecol-button` 是死規則 | 屬實（見檢查 3）。Marble 自己的 `tree.css:439,531,535` 也用了 `:has(.z-treecol-button)` | 沒有使用者路徑 | **Marble 死 CSS → ZK-6112 清理**（O10，記入 followups）。Treecols 沒有欄選單是 ZK API 本來就沒有的功能：真的想要就開 New Feature（O8），不是核心 bug。實作報告建議交核心團隊，我不同意 |

## 檢查 2：開啟方式

- 新 describe（`tablet.spec.ts` 第 695 行以後）裡沒有 `.click(`、`dispatchEvent`、`.open(`、`setOpen`、`openPopup`、`doModal`、`mouse.` 這類呼叫，只有 `page.touchscreen.tap` 兩處：`tapCentre` 和 errorbox 的「tap away」(400, 20)。`page.evaluate` 只用來讀 DOM：class 是否存在、`parentElement.tagName`、`getBoundingClientRect`。helper 裡也沒有藏 API 開啟。
- errorbox 用 `keyboard.type('a')` 再 `Backspace`，代表軟鍵盤輸入，合理。
- 「恰好一個可見」：`tabletPopupShot` 對 `${selector}:visible` 做 `toHaveCount(1)`，同時當作等待條件與 clip 依據。notification 與 window 另外斷言 `toHaveCount(1)`。
- tap 的目標都是使用者真的會點的元素：combobox、datebox、timebox 的按鈕，colorbox 本體與 picker icon，menubar 文字，combobutton 箭頭，欄按鈕，輸入欄，Info、Open modal window、Delete item 按鈕。(400, 20) 實測是 `.z-p-8` 的空白處。

## 檢查 3：treecol 替換是否合理（成立）

- Java：`Treecols.java`（`extends HeadersElement`）沒有 `menupopup`；`Columns.java:120` 與 `Listhead.java` 都有 `setMenupopup`。
- TS：`sel/Treecols.ts` 與 `sel/Tree.ts` 都沒有 `menupopup`。`grid/Column.ts:207` 與 `sel/Listheader.ts:254` 對 `-btn` 綁了 `_doMenuClick`，`sel/Treecol.ts` 沒有綁（`_doMenuClick` 繼承自 `SortWidget`，但沒有人觸發它）。
- mold：`sel/mold/treecol.js` 不輸出 `-btn`。`HeaderWidget.redraw` 只在 `parent._menupopup` 有值時輸出，而 Treecols 沒有設定這個值的途徑。
- zkex、zkmax 也沒有任何地方產生 treecol 按鈕。結論：改測 grid 與 listbox 的欄選單是合理的，`_mesh.css` 中同一條群組規則也涵蓋這兩個。

## 檢查 4：fixture

- `pv/popup-api-fixtures.zul`：只多了 `z-flex-wrap`，加上檔頭註解。`flex-wrap: wrap` 只在項目總寬超過容器時改變排版；桌面 1280 下 1001px 放得下，所以依規格版面不變。實測：`popup` 的 `window.zul: open a modal window` 與 `loadingbar.zul: a bar at the bottom of the window` 以 `POPUP_MAX_DIFF=0` 跑，2 passed。注意這代表「沒有任何像素超過 per-pixel `threshold 0.05`」，不是位元組相同（見 F1）。
- `pv/popup-tablet-fixtures.zul`：`xmllint` 通過；放在 `pv/`，不會被 gallery-scan 與 forced-colors-gallery 取用，與 `popup-api-fixtures.zul` 一樣不在 `render-smoke` 清單（同類 fixture 的既有做法）；用 zscript 的 server-side `onClick` 開啟；沒有 `<style>` 區塊。`style="margin-top: 200px"` 是 inline 樣式，但 `pv/popup-fixtures.zul:19` 有同樣先例，而且 utility 最大只到 `z-mt-16`（`--zk-spacing-16`），夠不到 200px，可以接受。小瑕疵：內層 `<button>` 沒有縮排。
- inplaceWebapp 裡的兩個 fixture 與 source 一致（`diff` 無輸出）。

## 檢查 5：完整執行（自己在 8087 的伺服器，乾淨 jar）

| 指令（`PREVIEW_URL=http://127.0.0.1:8087`） | 結果 |
|---|---|
| `--project=tablet`（全部） | **74 passed**（1.3 分） |
| `--project=popup` | **56 passed**（1.6 分） |
| 突變全部還原、重建 jar 後 `-g tablet-popup` | 16 passed |

每次執行後 `git status --porcelain` 都是 21 行，與開始時相同；16 張 PNG 的 shasum 不變。沒有跑 forced-colors，所以沒有 review PNG 被改寫。

## 檢查 6：我的突變（一次一個；改 zkcml → 建置 `:zkmax:compileMarbleCss :zkmax:jar :zkex:jar` → 重啟 → 跑 16 個 → 還原）

| ID | 規則 | 結果 | 判定 |
|---|---|---|---|
| Va | `_wheel.css` datebox／timebox sheet：`inset: auto 10px 0` → `auto 10px 24px`（sheet 抬高 24px） | datebox 2 個與 timebox 1 個失敗（截圖由 834x216 變成 834x228，約 4000 px），其餘 13 個通過；combobox 用自己的規則，不受影響 | 合理失敗 |
| Vb1 | `_wheel.css` `.z-calendar-wheel-right, .z-timebox-wheel-right` 背景 `surface-container-high` → `secondary-container`（Cancel 鈕顏色） | 同樣 3 個失敗，各 6809 px，其餘 13 個通過 | 合理失敗 |
| Vb2 | `_feedback.css` `.z-notification-close` 加 `background-color: color-mix(in srgb, var(--zk-color-on-surface) 8%, transparent)`（MD3 hover state layer 的強度） | **16 個全過，突變存活**。再用 `POPUP_MAX_DIFF=0` 單跑 notification 也通過 | **測試盲點（F1）** |

Vb2 的分析：同一伺服器上，探測確認規則有套用（computed `color(srgb 0 0 0 / 0.0696)`，截圖肉眼可見灰色方塊）。像素比對：576 px 不同，每個 channel 最大差 11，YIQ 距離換算的最大 threshold 只有 **0.038**，低於 0.05，所以每個像素都被當成相同。

建置後確認：重建的 `zkmax-11.0.0-SNAPSHOT.jar` 中 `web/zkmax/css/tablet.css.dsp` 沒有 8% 規則，`inset` 是 `auto 10px 0`，`wheel-right` 是 `surface-container-high`。

## 檢查 7：選擇器（暫時改動，已還原）

| 暫時改動（`--only zkcml:<path> --zkcml-base HEAD --base HEAD`） | mode | 選到的 `tablet-popup` 測試 |
|---|---|---|
| `zkmax/.../tablet/_wheel.css` `inset: auto 10px 0` → `24px` | partial（156/1708） | datebox × 2、timebox × 1，與 Va 實際失敗的 3 個完全相同 |
| `zkmax/.../tablet/_feedback.css` `.z-errorbox-close` `width` → `30px` | partial（30/1708） | errorbox × 1 |

兩次改完後 `p3/zkcml` 都是 clean。

## 缺陷分類表

| ID | 缺陷 | 所在 | 分類 | 原則 |
|---|---|---|---|---|
| F2 | colorpicker 三列在 tablet 重疊 5px，HEX 被切 4px | zkex `colorbox.css`（固定 `top`、`height:460px`）＋ tablet `_inputs.css`（48px `min-height`） | Marble CSS → ZK-6112 | O1（原因全在 Marble CSS）、V6 |
| F3 | colorbox sheet 在可捲動頁面讓頁面跳位，最後停在畫面中央 | ZK JS 機制；Marble 只為 combobox／wheel 做了覆寫，colorbox 沒有 | Marble CSS → ZK-6112（JS 的 Jira 交使用者決定，E6） | O4、V6 |
| F4 | combobox sheet 末列被切 20px | zul `combobox.css:153` 桌面 `max-height:280px` 套到 tablet sheet | Marble CSS → ZK-6112 | O1 |
| — | combobox sheet 固定 5 列高，項目少時有空白 | `combo-touch.ts` | by design | O6 |
| F5 | `.z-window-header` 的 tablet `min-height` 反而把下限從 56 降到 48；`.z-panel-header` 是 no-op | zkmax tablet `_window.css` | Marble CSS 清理 → ZK-6112 | O10 |
| F6 | `.z-treecol-button` 死規則 | tablet `_mesh.css:38`、`tree.css:439,531,535` | Marble 死 CSS → ZK-6112 清理 | O10；功能面是 O8 |
| F9 | wheel-time 兩段選取帶相接處有缺口 | tablet `_wheel.css` | Marble CSS（外觀，低） | V6 |
| — | notification 貼齊 viewport 角落 | 桌面同樣 | 不屬本批，不處理 | — |

## 問題清單（依重要性排列，附具體修正）

1. **F1（中，測試）`threshold: 0.05` 抓不到 8% state layer。** Vb2 在 24x24 的關閉鈕上加了 MD3 hover 強度的填色，576 px 全部低於 per-pixel 門檻，`POPUP_MAX_DIFF=0` 也通過。實作報告說「雜訊 0 px」，那是在 0.05 門檻下量的，不是原始雜訊。
   - 修正：先用 `threshold: 0` 在 3 個 process 量出原始雜訊。若可行，把 `tabletPopupShot` 的 `threshold` 改成 0.02（與 `brand`、`compact` project 相同；依 Vb2 的實測，0.02 會抓到 576 px 中的 500 px），然後重跑 Vb2，確認它會失敗。
   - 桌面 `popup` project 用同一個 0.05，有同樣盲點，屬範圍外，建議另行記錄。

2. **F2（中，Marble，基準已錄入缺陷）colorpicker 列重疊與裁切。**
   - 修正方向：把 zkex `colorbox.css` 的三列從 `position:absolute; top:0/43px/86px` 改成 flex column 加 `gap`，`.z-colorpicker` 的高度改由內容決定，或 tablet 另外調整；也可以在 tablet CSS 把列距改成 52px（`top: 0 / 52px / 104px`），並把 `.z-colorpicker` 加高 18px。
   - 修正後重切 `colorbox-tablet-popup-picker.png`，並確認桌面 `colorbox-popup-picker.png` 不變。
   - 測試補強：在 picker 測試斷言三列 `getBoundingClientRect` 不重疊，而且 HEX 底邊 ≤ `.z-colorpicker` 底邊。這樣這個缺陷就不會被當成基準重新錄入。

3. **F3（中，Marble）colorbox sheet 在可捲動頁面跳位。**
   - 修正：在 tablet `_inputs.css` 為 `.z-colorbox-popup` 加入與 combobox 相同的錨定：`position: fixed !important; inset: auto 0 0 !important; margin-inline: auto; width: fit-content; transform: none !important;`，並用 `zk-bottom-sheet-up` keyframe 補回滑入動畫（這些 `!important` 有先例；若是新增，依 E5 要問使用者）。
   - 測試：加一個 tablet-popup 測試，開啟前讓頁面可捲動（例如新 fixture 下方放高內容），斷言 tap 後 `scrollY` 不變、sheet 底邊貼 viewport 底。

4. **F4（低，Marble）combobox sheet 末列被切 20px。**
   - 修正：在 tablet `_inputs.css` 的 `.z-combobox-popup` 加 `max-height: none`，讓 sheet 等於 JS 給的 300px。
   - 修正後重切 2 張 combobox 基準（高度會增加 20px）。可以考慮加一個 ≥ 5 項的 sheet 測試，斷言捲到底時最後一項的底邊 ≤ viewport 高。

5. **F5（低，Marble，同時更正實作報告）window／panel header `min-height`。** 刪除 `_window.css` 的兩條 `min-height`（或改成不低於桌面值的 header token），並修正檔頭註解「Taller headers」。實作報告 §6c 的「comfortable = 56px」與「panel 規則有作用」都要更正：token 是 48px，兩條規則在 tablet 上都沒有讓 header 變高，window 那條甚至把下限降低了。

6. **F7（低，測試衛生）丟棄 3000ms timer 的範圍太大。** `tablet-popup` 的 `beforeEach` 對 16 個測試都丟掉 3000ms timer；桌面只在 toast／notification 這樣做。修正：移到 notification 測試內，或只包住那一個 describe。

7. **F8（低，fixture）confirmpopup 貼左邊。** popup 被 ZK 夾在 x = 0，截圖少了左 PAD，測到的也不是一般的錨定位置。修正：外層 div 改成 `z-d-flex z-justify-center`（或用 utility 把按鈕往右移），讓 popup 不碰到 viewport 邊緣，然後重切。

8. **F6（低，Marble 清理）`.z-treecol-button`。** 記入 `doc/marble-theme-followups.md`，在之後的清理批次刪除 tablet `_mesh.css` 的選擇器與 `tree.css` 三處 `:has(.z-treecol-button)`。不交核心團隊。

9. **F9（低，外觀）wheel-time 選取帶缺口。** 可以用 `:has(.z-timebox-wheel-time)` 把日期帶的右側圓角與時間帶的左側圓角改為 0，讓它們看起來是一條連續的帶子。修正後重切 `datebox-tablet-popup-wheel-time.png`。

## 清理

- 伺服器：只在 `p3/zk/zkpreview` 啟動 8087。stdin 由 `tail -f /dev/null` 經 FIFO 持有，PID 都有記錄（5 組：16101/16100/16884、48188/48187/48832、51232/51231/51827、54110/54109/54686、64010/64009/64692）。每次只 kill 自己記錄的 TAIL PID，必要時才 kill 記錄的 JETTY、CLIENT。最後全部不在，`lsof -iTCP:8087` 沒有 LISTEN。
- 沒有碰 8085（30568／30569／30570／33534 仍在）、8105 與其他 session 的程序；沒有用 `pkill`、`killall`；沒有停 Gradle daemon。
- 突變 Va、Vb1、Vb2 與選擇器用的兩個暫時改動都已用 `git checkout` 還原。最後從乾淨 source 重建 zk（`:zul:compileMarbleCss :zul:jar`）與 zkcml（`:zkmax:compileMarbleCss :zkmax:jar :zkex:jar`），並檢查 jar 內容沒有突變殘留。
- `p3/zk` 的 status、diff hash、未追蹤檔 shasum 都與開始時相同；`p3/zkcml` clean；沒有 staged 內容。沒有傳 `--update-snapshots`。
- 探測腳本與截圖在 `/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/7d5b9c66-28ef-4caf-8453-4b21d3a621a0/scratchpad/v5/`，沒有放進 repo。
