使用 8105

# 批次 10 最終判定報告（GATE10-FINAL：#4 button、#6 calendar、#40 label／radio、#61 tbeditor；#47 不量）

- 日期：2026-10-08；Verifier：Fable；`PREVIEW_URL=http://127.0.0.1:8105`（B 線 jess-b worktree）。Playwright 1.59.1、viewport 1280×900、DPR 2、transition／animation 關閉；色差一律 CIE76 ΔE。
- 方法：lines/line-b-plan.md（過程資料已清除） 第七之二節 J4／J6／J40／J61 + 「批次 10 RED 結果與方法定稿」第 1–4、6 點。腳本從 `batch10-red/` 複製（`lib.js` 原樣），調整處列於第四節。
- 本目錄只新增腳本、JSON、截圖與 Playwright 輸出／log；未改 CSS／TS／Java／預覽頁／baseline／文件，未 commit，未用 `--update-snapshots`。

## 〇、build 確認

`/web/button.zul` 的 `zk.wcs`（`/zkres/web/80d82a23/zul/css/zk.wcs`，640,989 bytes）含三個標記，皆命中：

- `z-button-text-error.z-button{color:var(--zk-color-error,#d32f2f);box-shadow:none;…`
- `td.z-calendar-cell.z-calendar-disabled{color:var(--zk-color-disabled)…`
- `.z-tbeditor-button-pane svg{width:14px;height:14px;fill:var(--zk-color-on-surface-variant);color:var(--zk-color-on-sur…`

→ 伺服的是新 build。

## 一、結論摘要表

| 編號 | 判定 | RED 數值 | 現在數值 | 通過／失敗 |
|---|---|---|---|---|
| J4-1 | 六 text 變體 × 四狀態 computed `box-shadow: none`；靜止／hover／按住外圍 4px 環（左右上三側）ΔE ≤ 1 | 彩色五變體靜止／hover／按住皆 resting 陰影，環最差 ΔE 2.79 | 24 個 (變體,狀態) 全 `none`；靜止／hover／按住 4px 環四側 ΔE 全 0（0／3904 px > 1）；聚焦只看 computed = `none`（環 77.55 為 focus outline） | **通過** |
| P4-a | filled／outlined／icon 陰影與 RED 相同；`z-button-outlined-*` 沒被動到 | filled／secondary／iconOnly：靜止 resting、hover elevation-2；outlined none／none；outlined-secondary resting／resting | 完全相同；另量 outlined-success／warning／error／info 靜止／hover 皆 resting（與 RED 報告所述一致，未被動到） | 通過 |
| P4-b | disabled 全變體 `none` | 22 顆 none | 22 顆 none | 通過 |
| P4-c | 文字色／`::before` opacity／cursor | primary／oklch(.54…)／#2e7d32／#bd3f00／#d32f2f／#007fab；0／.08／.12／.12；pointer | 相同 | 通過 |
| P4-d | 按鈕 rect | text 76.69×36、outlined 78.69×36、icon 46×36 | 相同 | 通過 |
| J6-1 | Oct 2026（no past）當月 disabled 日（含週末、排除選取日）computed `color` 相同、最深像素 ΔE ≤ 1；同時 Mar 2020 範例；P6-f 其他 constraint | 平日 `.38` vs 週末 `.87`，ΔE 40.46 | Oct 2026 no past：平日 1,2,5,6,7 與週末 3,4 皆 `rgba(0,0,0,.38)`，最深像素 (98,98,98)，群內與跨群 ΔE **0**；Mar 2020 no past 31 天全 `.38`、ΔE 0；Oct 2026 no future 23 天 ΔE 0 | **通過** |
| J6-2 | disabled 仍 line-through／not-allowed／pointer-events none | 通過 | 三組 disabled 全部相同 | 通過 |
| P6-a～P6-e | 可選週末＝平日色；選取日 on-primary；月外 opacity .38；表頭色；hover 圓盤只在可選日 | `.87`／`.87`；白字、圓盤 (55,111,208)；.38；七個 th `.6`；可選 (237,237,237)、disabled 不成立 | 全部相同 | 通過 |
| J40-1 | checkbox 與 radio 文字 font-size／weight／line-height 相同 | checkbox 13px、radio **14px**（400／20px） | 兩者皆 **13px／400／20px**（block 列與 flex 列） | **通過** |
| P40-a | checkbox 13px | 13px | 13px | 通過 |
| P40-b | 圓圈尺寸、min-height、\|文字墨水中心 − 圓圈中心\| ≤ 0.5px | 20×20、內點 13×13、40px；0.0（textRect −0.5） | 20×20、內點 13×13、40px／rect 高 40；墨水中心差 **+0.5**（textRect 差 0.0） | 通過（在邊界 0.5） |
| P40-c | radio disabled／checked／focus 外觀 | 見 RED | 完全相同（off (102,102,102)、on (55,111,208)、disabled (197,197,197)／opacity .38、focus outline 2px primary、`::before` 36px .12、暈 2037 px） | 通過 |
| P40-d | flex 列文字垂直置中（≤ RED + 1px） | radio 0.0、checkbox +1.5 | radio +0.5、checkbox +1.5 | 通過 |
| J61-1 | 每顆 svg rect 寬＝高＝14±1 且在按鈕內 | svg 35×150，上下溢出 57.5px | 20 顆皆 **14×14**，全部在 35×35 按鈕內（portallayout 與 tbeditor.zul 兩頁相同） | **通過** |
| J61-2 | 筆畫 run 中位 ≤ 2px | 3–4.5（align-left 4.5、undo 3.5、strong 3.5） | RED 指名的 align-left／undo／strong：**2／1.5／1.5**；全 20 顆：18 顆 ≤ 2，Formatting（¶）2.5、Fullscreen 2.5（核心 run 全 20 顆 ≤ 2；見第四節缺陷 3） | 通過（指名三顆）；全 20 顆讀法見缺陷 3 |
| J61-3 | 20 顆字形墨水兩兩 ΔE ≤ 2，目標 `on-surface-variant` | view-html、fullscreen 純黑 (0,0,0)，其餘 (96,98,100)，ΔE ≈ 40 | computed fill／color 20 顆皆 `rgba(0,0,0,.6)`（= token，疊 pane 底 → (96,98,100)）；**最深像素：19 顆 (96,98,100)，Fullscreen (38,39,40)，ΔE 25.88**；墨水中位兩兩最差 34.79（Align Left (167,170,174) vs Fullscreen (80,82,83)） | **失敗**（Fullscreen 明顯更深；見第二節 J61） |
| P61 | 按鈕 35×35、分隔線 `::before` 1×35 `rgba(0,0,0,.12)`、兩列換行、tbeditor.zul 單列 pane 高 36 | 同左 | 35×35（20 顆）；11 條分隔線 1×35 `rgba(0,0,0,.12)`；portallayout 兩列 y=233／268、第二列自 Align Left 起；tbeditor.zul pane 1214×36 單列 | 通過 |
| P61 hover／active | 在 `tbeditor.zul` 量；hover 圖示色與 RED 相同 | **RED 無 hover 資料**（portallayout 內 `:hover` 不成立） | `tbeditor.zul` 20 顆 `:hover`／`:active` 皆成立；hover 時 svg computed `fill` 變 primary `rgb(55,111,208)`、`color` 仍 `rgba(0,0,0,.6)`、按鈕底 primary 8% 狀態層；墨水：18 顆走 fill 的變 primary 藍、View HTML（stroke=currentColor）仍灰 (90,93,98)、Fullscreen 混色 (70,93,110) | **無法與 RED 比對**（缺陷 4）；現況 hover 下三種顏色並存 |

## 二、各 issue 細節

### J4 button（`button.zul`；`final4.js` → `final4.json`、`final4.log`、`final4-<變體>-<狀態>.png`、`final4-P-*.png`）

量法同 RED（六變體各取 enabled 那顆；靜止＝滑鼠 (2,2)、hover＝按鈕中心、聚焦＝前一可聚焦元素 `focus()` 後 Tab、按住＝`mouse.down`）。依定稿第 1 點：聚焦只看 computed；像素環以左、右、上三側為準（本次四側皆 0，含底側）。

| 變體 | 靜止 | hover | 聚焦 | 按住 | 4px 環最差 ΔE 靜止／hover／按住（top／left／right） |
|---|---|---|---|---|---|
| `z-button-text` | none | none | none | none | 0／0／0 |
| `-secondary` | none | none | none | none | 0／0／0 |
| `-success` | none | none | none | none | 0／0／0 |
| `-warning` | none | none | none | none | 0／0／0 |
| `-error` | none | none | none | none | 0／0／0 |
| `-info` | none | none | none | none | 0／0／0 |

聚焦狀態的環 ΔE 77.55（info 底側 0，被容器裁掉）全部來自 `outline solid 2px rgb(55,111,208)`，六變體一致，依定稿不計。`::before` opacity 靜止 0、hover .08、聚焦 .12、按住 .12；`cursor: pointer`；文字色與 RED 相同。

保護項：filled／secondary／iconOnly 靜止 `rgba(50,50,93,.024) 0 2px 5px -1px, rgba(0,0,0,.05) 0 1px 3px -1px`、hover `rgba(0,0,0,.12) 0 2px 6px, rgba(0,0,0,.14) 0 1px 2px`；`z-button-outlined` none／none；`z-button-outlined-{secondary,success,warning,error,info}` 靜止／hover 皆 resting（RED 只量了 outlined-secondary，RED 報告記錄其餘四顆亦為 resting；本次五顆皆未變，符合「沒被動到」）。22 顆 disabled computed 全 `none`。

### J6 calendar（`calendar.zul`；`final6.js` → `final6.json`、`final6.log`、`final6-*.png`）

| 截圖 | 群 | 日期 | computed color | 最深像素中位 | 群內最大 ΔE | 刪除線／cursor／pe |
|---|---|---|---|---|---|---|
| `final6-nopast-current.png`（Oct 2026，no past） | disabled 平日 | 1,2,5,6,7 | `rgba(0,0,0,.38)` | (98,98,98) | 0 | line-through／not-allowed／none |
| 同上 | disabled 週末 | 3,4 | `rgba(0,0,0,.38)` | (98,98,98) | 0 | 同上 |
| 同上 | enabled 平日／週末 | 8–30／10,…,31 | `.87`／`.87` | (33,33,33) | 0 | none／pointer／auto |
| `final6-nopast-mar2020.png`（設計師範例，全月 disabled） | disabled 平日 22 天／週末 9 天 | — | `.38`／`.38` | (98,98,98)／(98,98,98) | 0 | line-through／not-allowed／none |
| `final6-nofuture-current.png`（Oct 2026，no future；P6-f） | disabled 平日 16 天／週末 7 天 | 9–30／10,…,31 | `.38`／`.38` | (98,98,98) | 0 | 同上 |

跨群：disabled 平日 vs disabled 週末 ΔE **0**（RED 40.46）；disabled vs enabled 平日 28.81（僅供參考，依定稿不作判定）。月外 disabled 四天 `.38`、opacity .38，最深 (195,195,195)，群內 ΔE 0（RED 13.3，同根因一併消失）。

其他保護項：`final6-default-mar2020.png` 選取日 Sun 15 computed `rgb(255,255,255)`、圓盤中位 (55,111,208)、最亮 (255,255,255)；表頭七個 th `rgba(0,0,0,.6)`；hover：可選平日 8／週末 10 探針 (237,237,237)（`final6-hover-enabled-*.png`），disabled 1／3 探針 (255,255,255)、`:hover` 不成立（`final6-hover-disabled-*.png`）。

**selected + disabled 同時出現？** 三個快照的 `td.z-calendar-cell` 全掃一遍（`final6.json` 的 `selectedAndDisabled`／`selectedAny`）：沒有任何 cell 同時帶 `z-calendar-selected` 與 `z-calendar-disabled`。更具體的觀察：三個 calendar 的 value 都是 2020-03-15，(a) no past 的 Mar 2020（15 是 disabled 過去日）**沒有任何 selected cell**；(b) no future 導到 Oct 2026（15 是 disabled 未來日）也**沒有 selected cell**；(c) no past 導到 Oct 2026（15 可選）才出現 `z-calendar-weekday z-calendar-selected` 的 15。亦即在 ZK 現有渲染下，被 constraint 停用的日期不會被標成 selected——這是以 DOM class 的實測歸納（兩個 disabled 案例 + 一個 enabled 案例），不是讀 widget 原始碼的結論；若要百分之百確認，需由 Planner 查 `Calendar.ts` 的 cell class 邏輯。

### J40 label／radio（`label.zul`、`radiogroup.zul`；`final40.js` → `final40.json`、`final40.log`、`final40-label-rows.png`、`final40-radiogroup-states.png`、`final40-radio-focus.png`）

| 列 | 元件 | content font | host 高／min-height | input | 文字墨水中心 − 圓圈墨水中心 | textRect 中心 − input 中心 |
|---|---|---|---|---|---|---|
| block | checkbox「By email」 | 13px／400／20px | 40／40px | 18×18 | +1.5（RED +1.5） | 0 |
| block | radio「Yes」「No」 | **13px**／400／20px（RED 14px） | 40／40px | 20×20 | **+0.5**（RED 0.0） | 0（RED −0.5） |
| flex | checkbox | 13px／400／20px | 40／40px | 18×18 | +1.5 | 0 |
| flex | radio | **13px**／400／20px | 40／40px | 20×20 | +0.5 | 0 |

P40-b：|+0.5| ≤ 0.5 通過（剛好在門檻；13px 字的墨水高從 11 變 10，中心落在半像素）。`radiogroup.zul` 八顆 radio 狀態值與 RED 完全相同（外圈 20×20、內點 13×13、色 (102,102,102)／(55,111,208)／(197,197,197)、content opacity .38、focus-visible outline `solid 2px rgb(55,111,208)`、`::before` 36px opacity .12、暈 2037 px 中位 (228,228,228)），content font 也都變成 13px。

### J61 tbeditor（`portallayout.zul`、`tbeditor.zul`、`toolbar.zul`；`final61.js` → `final61.json`、`final61.log`、`final61-portallayout-toolbar.png`、`final61-tbeditor-toolbar.png`、`final61-tbeditor-hover-{0,1,2}.png`、`final61-tbeditor-active-{0,1,2}.png`、`final61-toolbar-lucide.png`、`final61-magnify-icons.png`）

**J61-1／J61-2（兩頁數值相同）：**

| 圖示 | svg rect | 在按鈕內 | 字形 bbox | 筆畫 run 中位（h／v）→ thickness | 核心 run | 最深像素 | 墨水中位 |
|---|---|---|---|---|---|---|---|
| View HTML | 14×14 | 是 | 12×8 | 2／2 → 2 | 2 | (96,98,100) | (96,98,100) |
| Undo／Redo | 14×14 | 是 | 9.5×5.5／10×6 | 1.5／1.5 → 1.5 | 1 | (96,98,100) | (96,98,100) |
| Formatting ¶ | 14×14 | 是 | 17×14（含下拉三角） | 3／2.5 → **2.5** | 1 | (96,98,100) | (96,98,100) |
| Strong／Emphasis | 14×14 | 是 | 6.5×8／3.5×8 | 1.5 | 1 | (96,98,100) | (96,98,100) |
| Deleted | 14×14 | 是 | 9×8 | 2／1 → 1 | 1 | (96,98,100) | (131,133,136) |
| Superscript／Subscript | 14×14 | 是 | 9×8／11×9.5 | 1.5 | 1 | (96,98,100) | (96,98,100) |
| Link／Insert Image | 14×14 | 是 | 19.5×15.5（含三角）／14×11 | 1.5 | 1 | (96,98,100) | (96,98,100) |
| Align ×4、兩種 list、hr | 14×14 | 是 | 11×9（hr 11×2） | 10～11／2 → 2 | 1 | (96,98,100) | (167,170,174) |
| Remove format | 14×14 | 是 | 10.5×9.5 | 2／1 → 1 | 1 | (96,98,100) | (96,98,100) |
| Fullscreen | 14×14 | 是 | 11×11 | 2.5／2.5 → **2.5** | 1.5 | **(38,39,40)** | **(80,82,83)** |

框架參考（`toolbar.zul` Lucide，`final61-toolbar-lucide.png`）：`::before` 14×14、字形 11–13px、thickness 1–2.5、核心 0.5–1.5——tbeditor 現在的盒、字形、筆畫都落在同一級距。

**J61-3 失敗的根據：** computed `fill` 與 `color` 20 顆都是 `rgba(0,0,0,.6)`（頁面 `--zk-color-on-surface-variant` 原值 `#0009`，計算值 `rgba(0,0,0,.6)`），疊在 pane 底 (240,244,250) 上的目標色為 (96,98,100)；19 顆的最深像素正是 (96,98,100)（ΔE 0），**Fullscreen 的最深像素卻是 (38,39,40)**（ΔE 25.88），且其墨水中位 (80,82,83) 也深於其他 19 顆的實心色，表示不是邊角少數像素，而是字形主體更深。`final61-magnify-icons.png`（6× 放大）肉眼即可見 Fullscreen 四支箭頭近黑、其餘圖示為灰。數值上 (38,39,40) ≈ 兩層 60% 黑疊加（1 − 0.4² = 0.84 → 255×0.16 ≈ 41），與 RED 記錄的 Fullscreen 符號結構一致（兩個 shape，其中一個 `fill=currentColor`、另一個 stroke）：半透明的 token 色在重疊的填色＋描邊上疊加兩次就變深。純黑 (0,0,0) 的 RED 狀態已被拉到 token，但 token 是半透明色，重疊區仍然不一致。判定：**J61-3 失敗**（1／20 顆；RED 2／20 顆）。

**hover／active（`tbeditor.zul`，事件可達）：** 20 顆 `:hover`、`:active` 皆為 true。hover：按鈕底 `color(srgb .2157 .4353 .8157 / .08)`（primary 8%）、radius 8px；svg computed `fill` → `rgb(55,111,208)`、`color` 仍 `rgba(0,0,0,.6)`。像素：18 顆走 `fill` 的圖示墨水變 primary 藍（Undo (55,111,208)、Align (139,171,227) 為細線 AA），**View HTML（stroke=currentColor）維持灰 (90,93,98)**，Fullscreen 混色 (70,93,110)。active 與 hover 相同（無第二層狀態層），只有 View HTML 按下後按鈕底變 `oklch(0.92 …)`（它是 toggle 鈕，按下即進入 HTML 檢視的 active 樣式）。RED 沒有任何 hover 數值可比（RED 的 hover 探針在 portallayout 內 `:hover` 不成立），「hover 時各圖示顏色必須與 RED 相同」無法判定；但可確定的是 hover 下 20 顆圖示不是同一色（三種），與 J61-3 同源（`color` 與 `fill` 走不同值）。

## 三、回歸結果

| 執行 | 指令要點 | 結果 |
|---|---|---|
| `component-theming`、`hit-target`、`focus-scan`、`forced-colors` | `cd zkpreview && PREVIEW_URL=http://127.0.0.1:8105 npx playwright test --config src/test/playwright/playwright.config.ts --project=component-theming --project=hit-target --project=focus-scan --project=forced-colors --reporter=list --output <本目錄>/pw`（`pw-regression.log`） | **184 passed、47 skipped、0 failed**（2.1m）。47 個 skipped 全是 `focus-scan` 的既有 skip，數量與批次 9 最終判定（47）相同 |
| `chromium`（`-g "button\|calendar\|checkbox\|radio\|label\|portallayout\|tbeditor"`） | 同上，`--project=chromium`，`--output <本目錄>/pw-chromium`（`pw-chromium.log`）；未用 `--update-snapshots`，執行前先確認 25 個命中測試的 baseline 檔都存在（避免 default 的 missing 模式寫入 `doc/screenshots`） | **25 passed、0 failed**：button gallery／default-*／outlined-*／color-variant-disabled-state／md3-gap、checkbox gallery／hover／focus／tristate、radiogroup hover／focus，以及 -g 子字串順帶命中的 combobox／coachmark／stepbar／messagebox／combobutton／label-css-is-served |
| `gallery`（補充，同 -g；calendar／label／portallayout／tbeditor 的 gallery 截圖在這個 project，不在 chromium） | `--project=gallery`，`--output <本目錄>/pw-gallery`（`pw-gallery.log`） | **6 passed**（calendar、combobutton、label、portallayout、radiogroup、tbeditor） |

**baseline 像素差清單：無。** 沒有任何 `toHaveScreenshot` 失敗，`pw/`、`pw-chromium/`、`pw-gallery/` 皆為空（無 diff／actual 圖）。本批預期的視覺變化（#4 text 彩色變體失去陰影、#6 disabled 週末變淡、#40 radio 文字 14→13px、#61 tbeditor 圖示縮小）都沒有觸發 baseline 失敗，原因是容差：chromium 的 `threshold 0.05`（YIQ）對 ΔE 2.79 的 resting 陰影不敏感、button gallery 裁的是 `.z-p-8` 整頁；gallery project 用 `maxDiffPixelRatio 0.01`，工具列圖示、週末日期、radio 文字在整頁中都不到 1% 像素。baseline 本身未被改過（`git log` 最後觸及為 09-11／10-06），因此「0 失敗」是容差吸收，不是 baseline 被重切。已知失敗（`calendar-tablet`、`slider-tablet`、`grid-header-gallery`）不在本次執行的 project 內。

寫入檢查：`git status --porcelain zkpreview/` 執行前後皆空（`git-status-before.txt`）；`zkpreview/test-results/` 不存在；`doc/screenshots` 與 `doc/focus-ring-known-clips.json` 未變（未設 `FOCUS_SCAN_UPDATE`）。`playwright.config.ts` 只在 `baseURL` 的預設值寫 `localhost:8085`，`PREVIEW_URL` 環境變數可覆蓋，未改 config。

## 四、方法缺陷、歧義與腳本調整

**腳本調整（相對 `batch10-red/`）：**
1. `final4.js`：輸出檔名 `red4-`→`final4-`；`PROTECT` 多加 `z-button-outlined-{success,warning,error,info}`（為「`z-button-outlined-*` 沒被動到」提供直接數值）。
2. `final6.js`：輸出檔名；`noFutureCurrent` 也保存逐 cell 資料；新增 `selectedAndDisabled`／`selectedAny` 統計；`goToMonth` 在每次點「下一月」後 `waitForFunction` 等標題真的改變再進下一輪（第一次執行時 RED 原版迴圈在 AU 往返中讀到舊標題而連點兩次，跳過 Oct 2026 停在 Feb 2029；修正後正常停在 Oct 2026，判定值不受影響，只是導航穩定性）。
3. `final40.js`：只改輸出檔名。
4. `final61.js`：輸出檔名；移除 RED 的 `addStyleTag` 注入實驗與 `button.zul`／`utility/icons.zul` 畫廊量測（定稿已定 14px）；新增 `tbeditor.zul` 上 20 顆按鈕的 hover／active 量測（`:hover`／`:active` 可達性、按鈕底色、svg `fill`／`color`、墨水中位）、分隔線 `::before` 尺寸／色、`--zk-color-on-surface-variant` 的 computed 值；`lucide()` 補回 RED 原有的 `scrollIntoView`（第一次漏掉導致截圖 clip 出界，重跑）。另用一支暫存腳本產生 `final61-magnify-icons.png`（6× 放大六顆圖示，純視覺佐證）。

**缺陷與歧義：**
1. **J61-3 的「墨水中位」在 14px 下不再是顏色的好代理**：細線圖示（Align ×4、list、hr：1px 線）過半像素是反鋸齒邊緣，中位變成 (167,170,174)，與 token 色 ΔE 28，即使顏色正確也會「失敗」；所以本報告以「最深像素」（實心核心色）作主判定、墨水中位作輔助。兩種量法對 Fullscreen 的結論一致（更深），對其餘 19 顆則只有最深像素能證明同色。建議 Planner 將 J61-3 條文定為「最深像素（或核心色）兩兩 ΔE ≤ 2」。
2. **Fullscreen 變深的機制是半透明 token 疊兩層**：`on-surface-variant` 是 `rgba(0,0,0,.6)`，符號的填色與描邊重疊處會合成 84% 黑。只靠換 token 拉不齊，需要處理符號結構（或改用不透明色）——這是給 Generator 的線索，不是方法問題。
3. **J61-2 的範圍歧義**：定稿寫「筆畫 run 中位 ≤ 2px」未指明哪幾顆；RED 的建議明寫 align-left／undo／strong 三顆（本次 2／1.5／1.5 通過）。若讀成全 20 顆，Formatting（¶ 的實心碗讓 run 量到的是面寬 3／2.5）與 Fullscreen（重疊處）為 2.5 > 2，但 RED 的 X2（14px）實驗當時就記錄 Formatting 2.5、Fullscreen 3，Planner 定 14px 時已知；且 20 顆的核心 run（排除 AA）全部 ≤ 2。本報告按三顆讀法記「通過」，請 Planner 確認。
4. **「hover 時各圖示顏色必須與 RED 相同」無法執行**：RED 沒有任何 tbeditor hover 數值（portallayout 內 `:hover` 不成立，RED 報告第四節第 11 點已預告）。本次只能記錄現況（hover 下 fill→primary、color 不變，導致 18 藍、1 灰、1 混）。若 Planner 的意思是「本批不得改 hover 規則」，需另以 diff 確認（Verifier 依規則不讀 CSS／diff）。
5. **P40-b 剛好落在門檻 0.5**（墨水中心差 +0.5），是 DPR 2 下半像素量化的結果；未來若改 13px 行高或 input 尺寸，這一項會先翻。
6. **回歸容差吸收了本批所有預期變化**（第三節），screenshot 回歸對這四個 issue 等於沒有偵測力；baseline 像素差清單為空不代表畫面沒變。
7. **J6「selected 與 disabled 同時出現」**只能以 DOM class 實測歸納（第二節 J6），未讀 widget 原始碼。
8. J4 的 `z-button-text-info` 底緣仍被容器裁掉（RED 缺陷 2），本次依定稿以左右上三側為準；實測四側皆 0，不影響判定。

## 五、結論

J4-1、J6-1、J40-1、J61-1、J61-2（三顆讀法）通過，所有保護項通過（hover 色「與 RED 相同」一項無基線可比，記為無法判定），回歸無預期之外的失敗；**J61-3 失敗**（Fullscreen 字形色 (38,39,40) vs 其餘 (96,98,100)，ΔE 25.88）。

GATE10-FINAL: FAIL
