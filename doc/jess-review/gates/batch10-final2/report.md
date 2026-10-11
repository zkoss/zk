使用 8105

# 批次 10 最終判定報告第 2 輪（GATE10-FINAL2：只重量 #61 tbeditor；#4、#6、#40 第 1 輪已通過不重量；#47、#72、#1 不量）

- 日期：2026-10-08；Verifier：Fable；`PREVIEW_URL=http://127.0.0.1:8105`（B 線 jess-b worktree）。Playwright 1.59.1、viewport 1280×900、DPR 2、transition／animation 關閉；色差一律 CIE76 ΔE。
- 方法：lines/line-b-plan.md（過程資料已清除） 第七之二節 J61 + 「批次 10 RED 結果與方法定稿」第 6 點 + 「GATE10-FINAL 第 1 輪」方法修正 1–5（判定值以此為準，未再改）。腳本從 `batch10-final/` 複製（`lib.js` 原樣），`final61.js` 調整處列於第四節。
- 本目錄只新增腳本、JSON、截圖與 Playwright 輸出／log；未改 CSS／TS／Java／預覽頁／baseline／文件，未 commit，未用 `--update-snapshots`。

## 〇、build 確認

`/web/button.zul` 的 `zk.wcs`（`/zkres/web/80d82a23/zul/css/zk.wcs`，539,718 bytes）：

- 新標記 `tbeditor-button-pane svg{width:14px;height:14px;fill:rgb(from var(--zk-color-on-surface-variant) r g b / 1);color:rgb(from var(--zk-color-on-surface-variant) r g b / 1);opacity:.6;flex:none}` → **命中 1 筆**
- 第 1 輪舊標記 `…fill:var(--zk-color-on-surface-variant)` → 0 筆

→ 伺服的是第 2 輪新 build。

## 一、摘要表

| 編號 | 判定 | 第 1 輪數值 | 現在數值 | 通過／失敗 |
|---|---|---|---|---|
| J61-1 | 20 顆 svg rect 寬＝高＝14±1、完整在按鈕內 | 20 顆 14×14，在按鈕內 | 20 顆 **14×14**（computed `14px`），全部在 35×35 按鈕內；portallayout 與 tbeditor.zul 相同 | **通過** |
| J61-2 | RED 指名三顆 align-left／undo／strong 筆畫 run 中位 ≤ 2px | 2／1.5／1.5 | **2／1.5／1.5**（兩頁相同；Formatting 2.5、Fullscreen 2.5 依修正 2 不納入） | **通過** |
| J61-3（改後） | 20 顆最深像素兩兩 ΔE ≤ 3 | 19 顆 (96,98,100)，Fullscreen **(38,39,40)**，最差 ΔE 25.88 | View HTML (96,98,100)、其餘 19 顆 (95,97,99)，**Fullscreen (95,97,99)**；190 對兩兩 ΔE **最差 0.41**（View HTML vs Undo），> 3 的對數 **0** | **通過** |
| 單層合成 | svg computed `opacity`／`fill`／`color` | fill＝color＝`rgba(0,0,0,.6)`，opacity 未量 | 20 顆 `opacity: 0.6`、`fill: color(srgb 0 0 0)`、`color: color(srgb 0 0 0)`（不透明黑 × 單層 .6） → 疊在 pane 底 (240,244,250) 的理論值 (96,98,100)，實測最深 (95,97,99)／(96,98,100) | 通過（顏色來自單層合成，Fullscreen 重疊區不再加深） |
| P61 幾何 | 按鈕 35×35、分隔線 `::before` 1×35 `rgba(0,0,0,.12)`、portallayout 兩列換行、tbeditor.zul pane 高 36 | 35×35；11 條 1×35 .12；rows 233／268，第二列自 Align Left 起；pane 1214×36 | **完全相同**：35×35（20 顆）；11 條 `::before` `1px`×`35px` `rgba(0,0,0,.12)`；portallayout pane 559×71、rows [233,268]、Align Left 在 (692.5,278.5) 起第二列；tbeditor.zul pane 1214×36 單列 | 通過 |
| P61 hover 底色 | primary 8% | `color(srgb .2157 .4353 .8157 / .08)` | 20 顆 hover 皆 `color(srgb 0.215686 0.435294 0.815686 / 0.08)`，radius 8px；`:hover`／`:active` 20 顆皆 true | 通過 |
| P61 hover／active 18 顆 fill→primary 且不因 opacity 變淡 | 最深像素 ≈ primary (55,111,208)，不是 ~40% 淡色 | fill→primary，墨水中位 Undo (55,111,208)、Align (139,171,227)；opacity 未量 | hover 時 svg `opacity` **0.6 → 1**、`fill: rgb(55,111,208)`；18 顆中 **16 顆最深像素 = (55,111,208)（ΔE 0）**，Formatting／Link 最深 (90,93,98) 是下拉小三角（走 currentColor，第 1 輪亦然），其主字形墨水中位 (90,111,208)／(90,116,208) 與第 1 輪完全相同；active 與 hover 相同 | 通過 |
| P61 view-html／fullscreen hover | 不要求變 primary | View HTML hover 灰 (90,93,98)；Fullscreen hover 混色 (70,93,110) | View HTML hover／active／toggled-on **純黑 (0,0,0)**，墨水中位 (11,11,12)；Fullscreen hover 最深 (0,0,0)、墨水中位 (55,94,149)（黑＋藍邊） | 依定稿「不要求變 primary」不判失敗；**但與第 1 輪不同（灰→黑），見第四節缺陷 1** |
| P61 disabled | 若有 disabled 圖示，外觀 ≈ `.38 × .6` 合成、不得加倍變淡 | 未量 | **無 disabled 狀態可達**：兩頁工具列無 `disabled` 屬性／class；`zkmax.tbeditor.Tbeditor` widget 無 `setDisabled`；頁面無 disabled 切換；View HTML 模式只在 View HTML 鈕加 `z-tbeditor-active`，其餘 19 顆維持 `opacity .6`、最深 (95,97,99)（`final61r2-tbeditor-disabled-toolbar.png`） | 無法量（無此狀態；見缺陷 2） |
| P61 dropdown svg | `.z-tbeditor-dropdown button svg` 18×18 不變 | 未量（RED 記 18×18） | Formatting 下拉 6 顆 svg rect **18×18**（computed `18px`），`fill: rgba(0,0,0,.6)`、`color: rgba(0,0,0,.87)`、`opacity: 1`，最深像素 (102,102,102)（= .6 黑疊白） | 通過 |
| 回歸 | 四 project 全跑 + chromium／gallery `-g` | 184 passed／0 failed；25＋6 passed | **184 passed、47 skipped、0 failed**；chromium **25 passed**、gallery **6 passed**，0 failed；無 diff／actual 圖 | 通過 |

## 二、#61 細節（`portallayout.zul`、`tbeditor.zul`、`toolbar.zul`；`final61.js` → `final61r2.json`、`final61r2.log`、截圖見下）

截圖：`final61r2-portallayout-toolbar.png`、`final61r2-tbeditor-toolbar.png`（靜止工具列）、`final61r2-tbeditor-hover-{0,1,2}.png`／`-active-{0,1,2}.png`（View HTML、Undo、Redo）、`final61r2-tbeditor-disabled-toolbar.png`（View HTML 模式）、`final61r2-tbeditor-dropdown.png`（Formatting 下拉）、`final61r2-toolbar-lucide.png`（框架 Lucide 參考）、`final61r2-magnify-icons.png`（6× 放大：View HTML／Undo／Fullscreen／Align Left 的 rest／hover／active，附第 1 輪 hover-0／active-0 對照）。

### 20 顆圖示（靜止；兩頁數值相同，表列 tbeditor.zul）

| 圖示 | svg rect | 在按鈕內 | computed opacity／fill／color | 字形 bbox | run 中位 h／v → thickness | 核心 run | **最深像素** | 墨水中位 |
|---|---|---|---|---|---|---|---|---|
| View HTML | 14×14 | 是 | .6／黑／黑 | 12×8 | 2／2 → 2 | 2 | **(96,98,100)** | (97,99,101) |
| Undo | 14×14 | 是 | .6／黑／黑 | 9.5×5.5 | 1.5／1.5 → **1.5** | 1 | (95,97,99) | (95,97,99) |
| Redo | 14×14 | 是 | .6／黑／黑 | 10×6 | 1.5／1.5 → 1.5 | 1 | (95,97,99) | (95,97,99) |
| Formatting ¶ | 14×14 | 是 | .6／黑／黑 | 17×14（含三角） | 3／2.5 → 2.5（不納入） | 1 | (95,97,99) | (96,98,100) |
| Strong | 14×14 | 是 | .6／黑／黑 | 6.5×8 | 1.5／1.5 → **1.5** | 1 | (95,97,99) | (95,97,99) |
| Emphasis | 14×14 | 是 | .6／黑／黑 | 3.5×8 | 1.5／3 → 1.5 | 1 | (95,97,99) | (95,97,99) |
| Deleted | 14×14 | 是 | .6／黑／黑 | 9×8 | 2／1 → 1 | 1 | (95,97,99) | (130,132,135) |
| Superscript／Subscript | 14×14 | 是 | .6／黑／黑 | 9×8／11×9.5 | 1.5／2 → 1.5 | 1 | (95,97,99) | (95,97,99) |
| Link | 14×14 | 是 | .6／黑／黑 | 19.5×15.5（含三角） | 1.5／1.5 → 1.5 | 1 | (95,97,99) | (97,99,101) |
| Insert Image | 14×14 | 是 | .6／黑／黑 | 14×11 | 1.5／2 → 1.5 | 1 | (95,97,99) | (95,97,99) |
| Align Left | 14×14 | 是 | .6／黑／黑 | 11×9 | 10／2 → **2** | 1 | (95,97,99) | (167,170,174) |
| Align Center／Right／Justify | 14×14 | 是 | .6／黑／黑 | 11×9 | 10～11／2 → 2 | 1 | (95,97,99) | (167,170,174) |
| Unordered／Ordered list | 14×14 | 是 | .6／黑／黑 | 11×9／10.5×9 | 6.5／2 → 2 | 1 | (95,97,99) | (167,170,174) |
| Insert horizontal rule | 14×14 | 是 | .6／黑／黑 | 11×2 | 11／2 → 2 | 1 | (95,97,99) | (167,170,174) |
| Remove format | 14×14 | 是 | .6／黑／黑 | 10.5×9.5 | 2／1 → 1 | 1 | (95,97,99) | (97,99,101) |
| Fullscreen | 14×14 | 是 | .6／黑／黑 | 11×11 | 2.5／2.5 → 2.5（不納入） | 2.5 | **(95,97,99)**（第 1 輪 (38,39,40)） | (95,97,99)（第 1 輪 (80,82,83)） |

「黑」= `color(srgb 0 0 0)`。token：`--zk-color-on-surface-variant` 原值 `#0009`、計算值 `rgba(0,0,0,.6)`；primary 計算值 `rgb(55,111,208)`。

**J61-3 的根據：** 20 顆的最深像素只有兩種值 (96,98,100)（View HTML）與 (95,97,99)（其餘 19 顆，含 Fullscreen），兩兩 ΔE 最差 0.41，190 對中 0 對 > 3。Fullscreen 的核心 run 從第 1 輪的 1.5 變 2.5、墨水中位從 (80,82,83) 變 (95,97,99)，與其他實心圖示一致，表示重疊區不再加深——顏色在 svg 層級以 `opacity .6` 合成一次，`fill`／`color` 皆為不透明黑。`final61r2-magnify-icons.png` 第二列「Fullscreen rest」肉眼也與其他圖示同灰。

框架參考（`toolbar.zul` Lucide，`final61r2-toolbar-lucide.png`）：`::before` 14×14、字形 11–13px、thickness 1–2.5、核心 0.5–1.5；與第 1 輪相同。

### hover／active（`tbeditor.zul`，`final61r2.json` 的 `tbeditorStates`）

| 圖示 | rest opacity／最深 | hover opacity／fill／最深／墨水中位 | active |
|---|---|---|---|
| Undo、Redo、Strong、Insert Image | .6／(95,97,99) | 1／primary／**(55,111,208)**／(55,111,208) | 同 hover |
| Emphasis、Superscript、Subscript、Deleted、Remove format | .6／(95,97,99) | 1／primary／(55,111,208)／(60,115,209)～(97,141,217)（細線 AA） | 同 hover |
| Align ×4、list ×2、hr | .6／(95,97,99) | 1／primary／(55,111,208)／(139,171,227)（1px 線 AA） | 同 hover |
| Formatting、Link | .6／(95,97,99) | 1／primary／(90,93,98)（下拉小三角，currentColor）／(90,111,208)、(90,116,208) | 同 hover |
| View HTML | .6／(96,98,100) | 1／primary／**(0,0,0)**／(11,11,12) | (0,0,0)／(14,15,16)，按鈕底 `oklch(.92 …)`（toggle 進入 HTML 檢視） |
| Fullscreen | .6／(95,97,99) | 1／primary／**(0,0,0)**／(55,94,149) | 同 hover |

18 顆走 `fill` 的圖示 hover 下最深像素 = primary 實色（Formatting／Link 的最深像素是小三角，主字形墨水中位與第 1 輪完全相同），**沒有因 opacity 變淡**（opacity 在 hover 時為 1）。第 1 輪 18 顆的墨水中位與本輪逐顆相同（例：Undo (55,111,208)、Align (139,171,227)、Deleted (97,141,217)）。

View HTML／Fullscreen 的 hover：這兩顆走 `stroke=currentColor`／混合結構，hover 時 `color` 是不透明黑且 opacity 變 1，所以渲染為**純黑 (0,0,0)**；第 1 輪是灰 (90,93,98)／混色 (70,93,110)。`final61r2-magnify-icons.png` 第一列「View HTML hover／active／toggled-on」與第三列「round-1 hover-0／active-0」可直接對照。View HTML 的 toggled-on（`z-tbeditor-active`，滑鼠離開後）同樣純黑，墨水中位 (14,15,16)。

### disabled

兩頁都沒有 disabled 的工具列按鈕（`button[disabled]` 0、class 含 `disable` 的 0，`trumbowyg-not-disable` 不算）。可達性探查：`zk.Widget.$('.z-tbeditor')` 是 `zkmax.tbeditor.Tbeditor`，無 `setDisabled`；`tbeditor.zul` 頁面文字無 "disabl"，無 checkbox／button 控制項；進入 View HTML 模式（`final61r2-tbeditor-disabled-toolbar.png`）其餘 19 顆 class 不變、`opacity .6`、最深像素 (95,97,99)，與靜止完全相同（trumbowyg 原版會加 `trumbowyg-disable`，ZK 版沒有）。因此「`.38 × .6` 合成值」沒有樣本可比。

### dropdown

Formatting 下拉（`final61r2-tbeditor-dropdown.png`）6 顆 `.z-tbeditor-dropdown button svg`：rect 18×18、computed `18px`／`18px`、`fill: rgba(0,0,0,.6)`、`color: rgba(0,0,0,.87)`、`opacity: 1`、最深像素 (102,102,102)（.6 黑疊白面板）。未被 pane 的 14px／opacity 規則波及。

## 三、回歸結果

| 執行 | 指令要點 | 結果 |
|---|---|---|
| `component-theming`、`hit-target`、`focus-scan`、`forced-colors` | `cd zkpreview && PREVIEW_URL=http://127.0.0.1:8105 npx playwright test --config src/test/playwright/playwright.config.ts --project=component-theming --project=hit-target --project=focus-scan --project=forced-colors --reporter=list --output <本目錄>/pw`（`pw-regression.log`） | **184 passed、47 skipped、0 failed**（2.2m）。47 skipped 皆為 `focus-scan` 既有 skip，數量同第 1 輪 |
| `chromium`（`-g "tbeditor\|portallayout\|button\|calendar\|checkbox\|radio\|label"`） | 同上，`--project=chromium`，`--output <本目錄>/pw-chromium`（`pw-chromium.log`）；執行前 `--list` 確認命中 31 測試（25 chromium + 6 gallery）且 `doc/screenshots`／`zkpreview` 工作樹乾淨 | **25 passed、0 failed**（19.5s） |
| `gallery`（同 -g） | `--project=gallery`，`--output <本目錄>/pw-gallery`（`pw-gallery.log`） | **6 passed、0 failed**（calendar、combobutton、label、portallayout、radiogroup、tbeditor） |

失敗清單：**無**。已知失敗（`calendar-tablet`、`slider-tablet`、`grid-header-gallery`）不在本次執行的 project／filter 內。`pw/`、`pw-chromium/`、`pw-gallery/` 皆空（無 diff／actual 圖）。寫入檢查：`git status --porcelain zkpreview/ doc/screenshots/` 執行前（`git-status-before.txt`，0 行）與執行後皆空；未設 `FOCUS_SCAN_UPDATE`。#4、#6、#40 相關測試（button／calendar／checkbox／radiogroup／label）全部通過，未見與它們相關的失敗，依指示不重量。

## 四、方法缺陷、歧義與腳本調整

**腳本調整（相對 `batch10-final/final61.js`，`lib.js` 原樣）：**
1. 輸出檔名 `final61-*`→`final61r2-*`、`final61.json`→`final61r2.json`。
2. 靜止與 hover／active 的 svg computed 多讀 `opacity`（修正 5 的「單層合成」確認）；`tbeditor()` 內新增 20 顆最深像素兩兩 ΔE 的計算與 log（`j613`：最差對、> 3 的對數）。
3. `states()` 的像素摘要多輸出 `darkest`（判「hover 不因 opacity 變淡」用）。
4. 新增 `disabledState()`（點 View HTML 進入 HTML 模式後量 19 顆的 class／opacity／像素，再點回）與 `dropdown()`（點 Formatting 展開後量 `.z-tbeditor-dropdown button svg`）。兩者各用**獨立 context**：`states()` 對 20 顆各點一次，結束時編輯器已處於 View HTML + fullscreen 模式，第一次執行時 disabled／dropdown 在同一 context 量到錯的狀態（dropdown 開不出來、View HTML 被點回），已重跑。
5. 另用暫存腳本產生 `final61r2-magnify-icons.png`（6× 放大，純視覺佐證；「Fullscreen active」格因 fullscreen 切換後 clip 失效而空白，不影響判定）。

**缺陷與歧義：**
1. **View HTML／Fullscreen 的 hover 變成純黑，定稿沒有條文可判。** 修正 3 只說這兩顆「不要求變 primary」，沒有要求「與第 1 輪相同」或「不得變深」。實測：第 1 輪 hover 是灰 (90,93,98)／混色 (70,93,110)，本輪 hover、active、toggled-on 都是 (0,0,0)（opacity .6→1 而 `color` 為不透明黑）。這是本輪 CSS 改法的副作用：第 1 輪記為 follow-up 的「這兩顆 hover 不跟其他圖示一起變藍」仍在，而且從「不變色」變成「變黑」，視覺上比第 1 輪更突兀（`final61r2-magnify-icons.png` 第一列 vs 第三列）。依「不得再改判定值」本報告不據此判失敗，請 Planner 裁決是否納入本批修正或維持 follow-up。
2. **disabled 保護項無樣本。** 工具列沒有任何可達的 disabled 狀態（第二節「disabled」），「`.38 × .6` 合成值」無法量；若 Planner 要驗這一項，需要指定製造 disabled 的方法（例如 Planner 查 `Tbeditor.ts` 是否有 readonly／disabled 路徑）。
3. **分隔線像素採樣與第 1 輪同樣沒有量到分隔線本體**（採樣欄位 6 個半像素全為 pane 底 (240,244,250)，第 1 輪亦同），分隔線判定依 computed `::before` 1px×35px `rgba(0,0,0,.12)`，與第 1 輪一致，沿用。
4. **Formatting／Link 在 hover 下的最深像素是下拉小三角 (90,93,98)**，不是主字形；主字形以墨水中位 (90,111,208)／(90,116,208) 判為 primary，與第 1 輪數值相同。若嚴格讀「18 顆最深像素應與 primary 相近」，這兩顆會因小三角不過；本報告視為既有結構（第 1 輪同值）不計。
5. 回歸容差仍吸收本批全部視覺變化（同第 1 輪缺陷 6）：tbeditor gallery 在 Fullscreen 由近黑變灰、View HTML hover 由灰變黑的情況下都沒有觸發 baseline 失敗，screenshot 回歸對 #61 沒有偵測力。

## 五、結論

J61-1、J61-2、J61-3（改後：最深像素兩兩 ΔE 最差 0.41 ≤ 3）全部通過；保護項：按鈕／分隔線／換行／pane 高、hover 底色、18 顆 hover 變 primary 且不變淡、dropdown 18×18 全部通過；disabled 無狀態可量；回歸 184＋25＋6 passed、0 failed。方法缺口：View HTML／Fullscreen hover 變純黑（缺陷 1），定稿未涵蓋，請 Planner 裁決。

GATE10-FINAL2: PASS
