# Marble 預覽頁 class 覆蓋缺口：結論（NS21，2026-10-22）

本文件取代 NS21 期間的工作文件（交接簡報、重新量測表、批 1 與批 2 計畫、三份驗證報告）。那些文件已刪除；需要細節時看 commit 訊息與本文件。

## 1. 結果

起點：121 個 Marble class 沒有任何預覽頁示範（110 個 variant、10 個 no-page、`z-navitem-selected`，量測於 `24220468d3`，`zkpreview/doc/class-index.json` 139 頁）。其中 74 個找得到產生者，47 個找不到（見 §6）。

| 範圍 | class 數 | 狀態 |
|---|---|---|
| 已由 `skeleton.zul`（ZK-6099）覆蓋 | 6 | 完成（不在本 PR） |
| 批 1：頁面標記或屬性即可出現 | 24 | 完成 |
| 批 2：需要真實 hover／focus／click 的 state | 27 | 完成（含 `z-nav-selected`） |
| 批 3：桌面拖曳中、`z-error`、`z-tbeditor-disabled` | 9 | 未做 |
| 批 4：只在 tablet 出現 | 8 | 未做 |
| 找不到產生者，不補 | 47 | 不做（§6） |

## 2. 批 1（24 個）

每頁在最後面加一個區段，既有的區段截圖不改名：`z-a-reverse`、`z-avatargroup-overflow`、`z-biglistbox-body-shim`、`z-biglistbox-head-shim`、`z-biglistbox-verticalbar-frozen`、`z-caption-readonly`、`z-grid-paging-top`、`z-cell-hidden-column`、`z-groupbox-readonly`、`z-lineitem-stretch`、`z-listcell-hidden-header`、`z-nav-image`、`z-navitem-info`、`z-navitem-selected`、`z-panel-footer`、`z-radiogroup-vertical`、`z-scrollview-horizontal`、`z-splitter-nosplitter`、`z-toolbar-center`、`z-toolbar-end`、`z-tree-paging-top`、`z-coachmark-down`、`z-coachmark-left`、`z-coachmark-right`。

- `coachmark-positions.zul` 是新頁，載入時不開 coachmark（同時開 3 個會疊 3 層 modal mask，標題對比只有 1.19–1.87:1，方向三角形的顏色突變抓不到）。`popup-gallery.spec.ts` 用真實點擊一次開一個，每張圖只有 1 層遮罩。因此這 3 個 class 不在 `class-index.json` 內（載入快照），選測靠名稱前綴對到 `z-coachmark`。
- `z-groupbox-readonly`、`z-splitter-nosplitter` 靜止時只宣告 `cursor`，用 computed-style 斷言守住，不靠截圖。

## 3. 批 2（27 個）

`screenshot.spec.ts` 檔尾新增測試，一個 class 一個測試：互動前斷言沒有該 class，只用真實輸入（`page.mouse`、`page.keyboard`、`locator.click()`、真實 Tab），在單一元素上斷言 class，等所有 CSS transition 結束後斷言一個 computed style，再還原狀態。狀態截圖 `threshold: 0.02`、`maxDiffPixels: 20`（D1 = A；批 1 的 P2 顯示預設 0.2 擋不住小幅底色改變）。

calendar `z-calendar-month/-year/-decade`；`z-cascader-focus`、`z-chosenbox-focus`、`z-chosenbox-item-focus`、`z-daterangebox-focused`、`z-searchbox-focus`；`z-column-sizing`、`z-listheader-sizing`、`z-treecol-sizing`；`z-column-hover`、`z-listheader-hover`、`z-menu-hover`、`z-menu-focus`、`z-listitem-focus`、`z-rating-hover`；`z-listheader-checked`、`z-treecol-checked`；`z-nav-selected`；`z-listgroup-selected`、`z-sliderbuttons-tooltip-start`；`z-tbeditor-active`、`-editor-hidden`、`-fullscreen`、`-body-fullscreen`；`z-window-maximized`。

新增區段：`listbox-grouping.zul`（`groupSelect`）、`multislider.zul`（垂直 multislider 放在 160px 寬的 `overflow:hidden` 容器邊緣）、`window.zul`（480×260 容器內的 maximizable window，區段上方留 160px，避開 04 區段兩個載入就顯示的 popup window）。

## 4. 修掉的 Marble 回歸，與驗證方法

- `caption.css`：`.z-caption-readonly` 的 `pointer-events: none` 會被子元素繼承，不可關閉的 groupbox 的 caption 內的按鈕點不到（IceBlue 點得到）。已移除，並加真實點擊測試。
- `listbox.css`：marble 的 `4d02e9c9db` 把表頭 padding 移到 `.z-listheader-content` 後，隱藏表頭（0 寬的 TH，`overflow: visible`）會把它的標籤文字畫在下一個表頭上。IceBlue 與 `.z-column`、`.z-treecol` 都裁切，所以讓 `.z-listheader` 也 `overflow: hidden`。先試過 `[aria-hidden="true"]` 選擇器，但只有 za11y 會設那個屬性，CE 上沒有效果，已放棄。
- grid／listbox 隱藏欄讓後面的 cell 往左移，marble 已由 `4d02e9c9db` 以同樣的規則修好。

方法：每張新截圖先在 `threshold: 0`、`maxDiffPixels: 0` 下跨 3 個 process 量雜訊；跨 process 重跑全過；每個 class 一個 CSS 突變，一次一個，且必須失敗；選測不回 `full`；工程（Sonnet）與驗證（另一個 Opus）分開，各批都由獨立驗證者重驗後才 commit。

## 5. 已知限制

- `z-window-maximized` 沒有 CSS 規則可突變（`window.css` 的規則是空的，class 只標記 JS 狀態），由 JS 行為斷言加截圖守住。
- 5 個 class 的測試證明的是「class 出現，加上那條規則的效果存在」，不是「class 選擇器是必要的」：`z-daterangebox-focused`、`z-column-hover`、`z-menu-hover`、`z-menu-focus`（同一條規則同時有 `:focus-within`、`:hover`、`:focus-visible`，真實輸入無法把兩者分開）。`z-listheader-hover` 另走「開啟表頭選單、指標移入 menupopup」的路徑單獨證明。
- 補回的 `listbox-listheader-hover-menu.png` 只擋得住按鈕的尺寸與位置，擋不住按鈕的 hover 底色、focus 外框、transition、圓角、tablet 外觀與低於 0.02 的色差。
- tablet 上表頭按鈕的焦點環被 `.z-listheader { overflow: hidden }` 裁 3px，只有程式 `focus()` 看得到，記為 follow-up 66。
- `tbeditor-editor-hidden.png` 因為編輯器沒有內容，看不出原始碼區取代 WYSIWYG，只靠 `display: none` 斷言與突變守住。
- 狀態測試的期望 opacity 取自執行時的 `--zk-state-hover-opacity`、`--zk-state-focus-opacity`，兩個 token 本身互換時只有截圖會失敗。

已知（本 PR 之前就有）的失敗：font-size equivalence 在每頁多 3 個 13px（za11y 的 `.sr-only` live region，跨伺服器比對才有）；`listbox-header`、`listbox-grouping`、`grid-paging`、`radiogroup` 另有 marble tip 與 font-size baseline 之間的差；`compact-gallery` 的 `tabbox`、`groupbox`，`brand-gallery` 的 `groupbox`，`compact-height` 的 `caption`、`groupbox`（marble groupbox header 多 1 px）；gallery 的 `daterangebox-03`（42 px）、`timepicker-01`（高度 167 變 171）；`window-04` popup 位置偶發（約 1/18，HEAD 就有）。

## 6. 未做

### 批 3（9 個，方法不同：拖曳類只做 DOM 與 computed style 斷言，不截圖）

| 頁 | class | 觸發 |
|---|---|---|
| `goldenlayout.zul` | `z-goldenlayout-dragProxy` | 真實 `page.mouse` 拖 `.lm_tab`，等 ≥ 200 ms，移動 > 10 px，在 mouse up 前斷言 |
| `panel.zul` | `z-panel-resize-faker` | `sizable="true"` 的 panel 拖右下角（頁面目前沒有） |
| `portallayout.zul` | `z-panel-move-block`、`z-panel-move-ghost` | 拖 portalchildren 內 panel 的標題列 |
| `splitter.zul` | `z-splitter-ghost` | 拖 `.z-splitter` 中央（不是 collapse button） |
| `window.zul` | `z-window-move-ghost`、`z-window-resize-faker` | 拖 overlapped window 的標題列；拖 `sizable` window 右下角 |
| `runtime-error.zul` | `z-error` | 按頁面上既有的 Trigger Error 按鈕（`barcodescanner.zul` 在攝影機權限被拒時也會出現，但那只是環境副作用） |
| `tbeditor.zul` | `z-tbeditor-disabled` | 沒有 ZUL 屬性，只能由 JS 呼叫 trumbowyg `setDisabled()` |

### 批 4（8 個，tablet，`tablet` Playwright project，觸控）

`z-frozen-sticky`（`grid.zul`，`<frozen columns="2"/>` 在 zk.mobile 才出現）；`z-scrollview-scrollbar`、`z-scrollview-scrollbar-indicator`；`z-scrollview-load`、`-load-up`、`-load-down`、`-load-left`、`-load-right`（需要 `onScroll` 監聽與觸控拉動超過邊界）。

### 找不到產生者的 47 個（不補）

- sclass 工具類，CSS 註解寫明由開發者自行套用：`z-button-dark`、`-light`、`-fab`、`-fab-extended`、`-xs`、`-group`、`-icon`；`z-popup-anim-fade`、`-grow`、`-zoom`；`z-focus-ring`；`z-grid-striped`、`z-listbox-plain`、`z-combobox-standard`；`z-combobox-error`、`z-textbox-error`。
- 名稱與實際產生的不同（實際 class 已在索引內）：`z-grid-inner`（z-row-inner）、`z-listitem-odd`（z-listbox-odd）、`z-row-odd`（z-grid-odd）、`z-columns-border`、`z-listhead-border`（z-auxhead-border）、`z-tabbox-vertical`（z-tabbox-left）、`z-grid-emph`、`z-listbox-emph`（emptybody）、`z-valign-top`、`z-listitem-focus` 類的 grid／tree 對應（`z-row-focus`、`z-treerow-focus`）、`z-row-disabled`。
- 元件沒有該功能：`z-slider-disabled`、`z-signature-disabled`、`z-rangeslider-center`、`z-splitlayout-splitter-nosplitter`、`z-tree-emptybody`、`z-tree-emptybody-content`、`z-treecol-button`、`z-window-se`、`z-toolbarseparator`、`z-lineitem-point-image`、`z-a-disabled`、`z-uploadbutton`、`z-cropper-area`、`z-tbeditor-not-disable`、`z-groupbox-title-readonly`。
- 沒有 Passwordbox 元件（`<textbox type="password">` 產生 `z-textbox*`）：`z-passwordbox`、`-disabled`、`-inplace`、`-invalid`。

這份清單在 `24220468d3` 量測，逐 class 的證據行號會隨原始碼移動；要處理其中任何一個，先在當時的樹上重查產生者。
