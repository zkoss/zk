使用 8085（C 線，批次 14 FINAL 第二輪 R2）

# Gate：第十四批最終判定第二輪 —— P1 依 D116-A 重做（toast 旋鈕原樣落到圖示、預設值改為混深色）、S1 selectbox forced-colors 規則搬到 `_forced-colors.css`、S3 `shell-is-bare-and-no-stripe`、S4 新 spec `listhead-bar-screenshot.spec.ts`＋全量回歸

- **伺服器：** 8085，java pid 96538（2026-10-09 **22:48:42** 啟動；`zkpreview/build/gretty_ports.properties` 22:48:42）。**沒有 kill、沒有重啟。**
- **新鮮度（伺服 = 這次 build）：** (1) `js/zul/menu/css/menu.css.dsp` 伺服 md5 `b1ed3cf3…` = `zul/build/resources/main/web/js/zul/menu/css/menu.css.dsp`；`js/zkmax/nav/css/nav.css.dsp` 伺服 `aff1a9b3…` = `../zkcml/zkmax/build/…/nav.css.dsp`；`zul/css/norm.css.dsp` 伺服 md5 `47fe7b4f…` = `zul/build/resources/main/web/zul/css/norm.css.dsp`（build 檔 22:48:26，早於啟動）。(2) `zul/css/zk.wcs` 200／**542,996 bytes、md5 `d62dd075…`**（第一輪 `7f07271a…`／543,055；RED `a5a526ee…`／542,919）→ 是新 build。伺服的 zk.wcs 含 **`--zk-toast-accent:color-mix(in srgb, var(--zk-color-status-info) 80%, var(--zk-color-on-surface))`**（1 處，即混深後的預設值）與 **`.z-toast-info .z-toast-icon{color:var(--zk-toast-accent)}`**（旋鈕原樣落到圖示）；`.z-notification-info .z-notification-icon{color:color-mix(in srgb, var(--zk-color-status-info) 80%, var(--zk-color-on-surface))}` 保留。第一輪的 `.z-toast-icon{color:color-mix(…)}` 已不在伺服檔。(3) S1：伺服的 `js/zul/wgt/css/selectbox.css.dsp` 內 `forced-colors` 出現 **0 次**；伺服的 `norm.css.dsp` 在 `@media (forced-colors:active)` 區塊（offset 204113–212575）內含 `.z-selectbox::picker-icon{background-color:canvastext}`（offset 209888）。(4) `zul/src` = `zul/build` md5：`selectbox.css`（`74714618…`）、`toast.css`（`d236e04e…`）、`notification.css`（`c7e43939…`）、`tokens/_forced-colors.css`（`407192f7…`）、`tokens/_component-theme.css`（`a5fa95a0…`）全部相同。(5) `zul/src`、`zkmax/src`、`zkex/src`、`zkpreview/src` 下沒有任何 `.css/.dsp/.zul/.xml` 比 `gretty_ports.properties` 新（只有 `zul/build` 的 `norm.css.dsp`／`zk.wcs` 22:48:25–26，都在啟動之前）。
- **日期：** 2026-10-09，Verifier（Fable）。Playwright 1.59.1、Chromium 147.0.7727.15；量測腳本 viewport 1280×900、deviceScaleFactor 2、`ignoreDefaultArgs:['--hide-scrollbars']`、注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`、游標停 (2,2)、每個狀態重新載入；forced-colors 用 `newContext({forcedColors:'active'})`＋`emulateMedia`。
- **方法：** 與第一輪相同（[batch14-final.md](batch14-final.md) 方法段、lines/line-c-plan.md（過程資料已清除） 六／七節）；ΔE 一律 CIE76、對比 WCAG 2.x、位置用像素 ink（與底色 ΔE ≥ 8 的像素外接矩形，0.5px 粒度）。
- **腳本與原始輸出：** `gates/batch14-final-r2/`：`lib.js`、`m14.js`（複製自第一輪，未改）、`r2-p1p2.js`（= 第一輪 `final-p1p2.js`，只改輸出檔名）`/.json/.log`＋`p1-toast-*.png`、`p12-*-forced-*.png`、`p2-notification-*.png`、`p2-pointer-*.png`；`r2-compare.js/.log`（R2 JSON 與第一輪、RED 的逐欄位比對）；`r2-s1-selectbox.js/.json/.log`＋`s1-selectbox-{normal,forced}-{0,1,2}.png`、`s1-selectbox-gallery-replica.png`、`s1-selectbox-forced-replica.png`；`r2-s4-probe.js/.json/.log`＋`s4-bandbox-popup.png`；`s3s4/`（S3／S4 正向與非空轉的 Playwright log、S4 的 `error-context.md`）；`nv/`（非空轉用的 scratchpad config 與 spec 複本／摘錄）；`pw/`（回歸 `summary.log`、各 project log、`run.sh`）；`reg-diff.js`＋`reg-diff/`（每張失敗截圖的 expected｜actual｜diff 裁圖與 `reg-diff.json`）。Playwright `--output` 全部指到 scratchpad `batch14-r2/`，沒有碰 `test-results`，沒有跑 `forced-colors-gallery`，沒有 `--update-snapshots`。
- **沒有修改**任何 theme／CSS／TS／Java／預覽頁／spec 檔案，沒有看 diff、沒有看 CSS 原始碼（CSS 項目只看伺服輸出與像素）；為了做 S3／S4 複本與診斷讀了 `screenshot.spec.ts` 的 notification 區塊、`listhead-bar-screenshot.spec.ts`、`playwright.config.ts`、`forced-colors.spec.ts` 的 test 名單、`forced-colors-gallery.spec.ts`（只讀，沒跑）、`pv/bandbox-content.zul`／`listbox-header.zul` 的標籤與列數。非空轉複本放在 scratchpad（`nv/`，以 symlink 借用 `zkpreview/node_modules`），repo spec 沒動。頁內只注入停動畫規則與（P1 旋鈕）`:root{--zk-toast-accent:#6750a4}`。沒有 commit。

## 結論摘要

| 項目 | 第一輪 | R2 | 判定 |
|---|---|---|---|
| J-P1-1 toast info 圖示 ink 對底色 ≥ 4.5 | 4.85 | ink **(6,107,143)**（480 px，19×19 @ (48.5,200.5)）對 (224,232,244) **4.85**；live `Toast.show` 同 4.85 | **通過** |
| P1 旋鈕保護：`--zk-toast-accent:#6750a4` → 圖示恰為 rgb(103,80,164) | ink (89,70,138)（混色後，spec 失敗） | 圖示 computed **`rgb(103, 80, 164)`**，ink 483 px **全部 (103,80,164)**（mostSaturated＝darkest＝median）；ΔE vs 預設 44.18；底色／close ΔE 0 | **通過** |
| P1 warning／error／close／底色 vs RED | ΔE 0 | `r2-compare.js` 對 RED：`toast.static.warning`／`toast.static.error`／`toast.forced`／`toast.knob.warning`／`toast.knob.error`／所有 root／content／icon／close 矩形 **零個差異欄位**；warning (122,54,30) 6.65、error (127,0,10) 7.75、close ink 8×8 @ (325,206)／(669,206)／(1013,206) | **通過** |
| P1 forced-colors 圖示可見 | 491／491／566 px 黑 | toast 491／491／566 px、(0,0,0) 對白 21:1 | **通過** |
| P2 notification info 圖示 ≥ 4.5；底色 ΔE＝0 | 4.85；0／0／0 | (6,107,143) **4.85**；info／warning／error 底色 (224,232,244)／(255,215,197)／(254,205,199) 與 toast **ΔE 0／0／0**；12 格箭頭 ΔE 全 0；notification forced 491／491／566 px | **通過** |
| R2 vs 第一輪（整份 JSON） | — | **只有 16 個欄位不同**：`--zk-toast-accent` 的 raw／resolved 值，與 `toast.knob.info` 的圖示色（(89,70,138)→(103,80,164)、ink n 482→483、對比 6.38→5.22）；其餘 toast／notification／pointer 全部相同 | — |
| S1 forced-colors suite（非 gallery） | 17／0 | **17 passed**（回歸 run，`pw/forced-colors.log`；注意 `forced-colors.spec.ts` 裡**沒有** "selectbox" 測試，gen-b 給的 `-g "selectbox"` 會選到 0 個，所以跑整個 project） | **通過** |
| S1 selectbox 箭頭 ink（forced） | — | 三個 selectbox 箭頭 ink **7.5×5／7×5／7.5×5**（dpr 2，8×5 ±0.5 內），與 normal 模式外接矩形 **dw／dh／dl／dt 全 0**；forced 色 darkest (0,0,0)、`::picker-icon` computed `background-color rgb(0, 0, 0)`（CanvasText；normal 是 `rgba(0, 0, 0, 0.6)`） | **通過** |
| S1 非 forced-colors 不變 | — | `selectbox › gallery` 複刻截圖 vs 基準 `selectbox-gallery.png`：**any-diff 0 px**（1280×442 同尺寸）；回歸 chromium `selectbox` gallery／hover／focus 通過 | **通過** |
| S3 `shell-is-bare-and-no-stripe` | — | 正向 **1 passed**；非空轉（複本注入 `.z-notification-content::before{content:'';display:block;width:4px}`）→ **失敗**於第 1413 行 `Expected: 0 Received: 4` | **通過** |
| S4 `listhead-bar-screenshot.spec.ts` | — | 測試 1（listbox-header.zul）**通過**；測試 2（bandbox.zul）**失敗**：30 s timeout 等 `div:has(> label:text-is("Listbox in bandpopup"))`——頁面上 **0 個 HTML `<label>`**，ZK 的 `<label>` 渲染成 `<span class="z-label">`；**不是**沒有捲軸（實際有：見下） | **失敗（spec 自身的 selector）** |
| S4 非空轉 | — | 複本注入 `th.z-listhead-bar{background:rgb(240,244,250)}` → 測試 1 **失敗** `Expected "rgba(0, 0, 0, 0)" Received "rgb(240, 244, 250)"`；測試 2 到不了斷言（同 selector timeout） | 測試 1 非空轉成立 |
| 回歸 `component-theming` 回到修前集合 | 105／2 | **107 passed／0 failed**（= merge-1 修前） | **通過** |
| 回歸：未歸因失敗 | 0 | 0（新增失敗只有 S4 測試 2） | **通過** |

## P1 — toast（`r2-p1p2.log`、`p1-toast-*.png`、`p1-toast-knob-*.png`）

| toast | 底色 | 圖示 computed | 圖示 ink | 對比 | 第一輪 | RED |
|---|---|---|---|---|---|---|
| info | (224,232,244) | `color(srgb 0 0.409 0.551 / 0.974)` | (6,107,143)，480 px，19×19 @ (48.5,200.5) | **4.85** | 4.85 | 3.68 |
| warning | (255,215,197) | `oklch(0.42 0.101 39.55)` | (122,54,30)，478 px | 6.65 | 6.65 | 6.65 |
| error | (254,205,199) | `oklch(0.375 0.154 26.41)` | (127,0,10)，547 px | 7.75 | 7.75 | 7.75 |
| info 旋鈕 `#6750a4` | (224,232,244)（ΔE 0） | **`rgb(103, 80, 164)`** | **(103,80,164)**，483 px，19×19 @ (48.5,200.5) | 5.22 | (89,70,138) | (103,80,164) |

- token：`--zk-toast-accent` raw **`color-mix(in srgb, #007fab 80%, #000000de)`** → resolved `color(srgb 0 0.409 0.551 / 0.974)`（第一輪 raw `#007fab`）；旋鈕頁 raw `#6750a4` → `rgb(103, 80, 164)`。預設圖示色仍帶 `on-surface` 的 alpha（2.6% 透明），像素在這個底色上就是 (6,107,143)，與第一輪完全相同。
- 旋鈕頁 warning／error 的圖示 ΔE 0、三張底色 ΔE 0、close ΔE 0（`dE_close_vs_default` 0／0／0）。
- `r2-compare.js` 對第一輪 JSON：16 個差異欄位全部在 `tokens.--zk-toast-accent` 與 `toast.knob.info`；對 RED：toast 區只有 `static.info`／`live` 的 info 圖示（RED 修前 (0,127,171)）與 `knob.info.dE_icon_vs_default` 44.89→44.18，`static.warning`、`static.error`、`forced.*`、`knob.warning`、`knob.error`、所有幾何與 close／text ink **沒有任何差異欄位**。

## P2 — notification（`p2-notification-*.png`、`p2-pointer-*.png`）

| 嚴重度 | notification 底 | toast 底 | ΔE | 圖示 | 對比 | left／right／up／down ΔE |
|---|---|---|---|---|---|---|
| info | (224,232,244) | (224,232,244) | 0 | (6,107,143) | 4.85 | 0／0／0／0 |
| warning | (255,215,197) | (255,215,197) | 0 | (122,54,30) | 6.65 | 0／0／0／0 |
| error | (254,205,199) | (254,205,199) | 0 | (127,0,10) | 7.75 | 0／0／0／0 |

與第一輪逐欄位相同（compare 對第一輪在 `notification.*` 下只有 token 值兩個欄位不同）。forced-colors：三張圖示 491／491／566 px、(0,0,0) 對白 21:1。

## S1 — selectbox forced-colors（`r2-s1-selectbox.log`、`s1-selectbox-*.png`）

| selectbox | 狀態 | normal：`::picker-icon` bg | normal 箭頭 ink | forced：`::picker-icon` bg | forced 箭頭 ink | 外接矩形差 |
|---|---|---|---|---|---|---|
| [0] | enabled | `rgba(0, 0, 0, 0.6)` | 7.5×5 @ (196.5,200.5)，56 px，darkest (102,102,102)，距右緣 12.77 | **`rgb(0, 0, 0)`** | 7.5×5 @ (196.5,200.5)，56 px，darkest **(0,0,0)**，median (75,75,75) | 0／0／0／0 |
| [1] | disabled | 同上 | 7×5 @ (784,200.5)，55 px，darkest (195,195,196) | `rgb(0, 0, 0)` | 7×5 @ (784,200.5)，55 px，darkest (157,157,157) | 0／0／0／0 |
| [2] | enabled | 同上 | 7.5×5 @ (131.5,351.5)，56 px | `rgb(0, 0, 0)` | 7.5×5 @ (131.5,351.5)，56 px，darkest (0,0,0) | 0／0／0／0 |

- 沒有 RED／第一輪的 selectbox forced 數字可比；交辦的替代門檻 8×5 ±0.5px：7.5×5（dpr 2 的半像素）在門檻內，且 normal／forced 外接矩形完全一致（同一個 14×14 mask，只換填色）。`appearance: base-select`、picker 盒 14×14、`forced-color-adjust auto` 兩模式相同。
- `doc/screenshots/selectbox-forced-colors.png`（2026-10-07 16:49 的 review artifact，**早於 A #29 `e5b82332a9` 把箭頭換成 chevron**）與 R2 的 forced 複刻截圖差 6,655 px（7 條帶，含三個箭頭區：舊 ▼ 11×9 vs 新 chevron 7×6 @ dpr 1）——是 #29 的 glyph 變更，不是 S1；**沒有搬移前的 chevron forced 基準**可做 ΔE ≤ 1 的比對，故改以上述「normal/forced 同盒、forced 填 CanvasText、非 forced 零像素差」判定。
- **附帶發現（不在判定內，請 Planner 看）：** gen-b 報告說刻意把 `.z-selectbox::picker-icon` 寫成獨立規則，以免不認識 `::picker-icon` 的瀏覽器連帶丟掉整個 (2e) selector list；但伺服的 `norm.css.dsp` 裡 minifier 已把它**合併**進 (2e)：`.z-combobox-icon,.z-combobox-button .z-icon-caret-down,.z-signature-tool-button-icon:before,.z-step-icon.z-icon-check:before,.z-selectbox::picker-icon{background-color:canvastext}`。Chromium 147 認得 `::picker-icon` 所以本輪量得到效果；但在不支援 `::picker-icon` 的瀏覽器（例如 Firefox）這條合併後的 list 會整個失效，combobox caret／spinner／step icon 的 forced-colors 填色跟著消失——與 gen-b 想避免的情況相同。需要在 build 層關掉這類合併或改寫法。

## S3 — `shell-is-bare-and-no-stripe`（`s3s4/s3-positive.log`、`s3-nonvacuity.log`）

- 正向：`playwright test -c src/test/playwright/playwright.config.ts --project=chromium -g "shell-is-bare-and-no-stripe"` → **1 passed (1.8s)**。
- 非空轉：scratchpad 複本（`nv/nv-notification-screenshot.spec.excerpt.ts` 為注入處摘錄）在 notification `beforeEach` 的 `goto` 之後 `page.addStyleTag({content:".z-notification-content::before{content:'';display:block;width:4px}"})`，同一指令 → **1 failed**：`Error: content ::before accent stripe must stay removed (width 0)  Expected: 0  Received: 4`（第 1413 行）。前兩條斷言（shell 透明、padding-left 0）在注入下仍成立，證明失敗來自第三條。

## S4 — `listhead-bar-screenshot.spec.ts`（`s3s4/s4-positive.log`、`s4-nonvacuity.log`、`s4-bandbox-error-context.md`、`r2-s4-probe.log`、`s4-bandbox-popup.png`）

正向（`--project=chromium listhead-bar-screenshot`）：**1 passed、1 failed (31.7s)**。

| 測試 | 結果 | 量到的數字（`r2-s4-probe.js`，Desktop Chrome 1280×720、dpr 1、顯示捲軸） |
|---|---|---|
| 1 `listbox-header.zul scrolling listbox` | **通過** (990ms) | 18 個 listbox，第一個有垂直捲軸的是 idx **9**（Brand／Price／Hard Drive Capacity，5 列、高 200、body clientHeight 145 vs scrollHeight 423，offsetWidth 1208 vs clientWidth 1202）；`th.z-listhead-bar` 寬 **6**、bg `rgba(0, 0, 0, 0)` = 第一個 `th.z-listheader` bg `rgba(0, 0, 0, 0)` |
| 2 `bandbox.zul bandpopup listbox` | **失敗** (30.1s) | `locator.click: Test timeout of 30000ms exceeded` 等 `locator('div:has(> label:text-is("Listbox in bandpopup"))').locator('.z-bandbox-button')`。**原因：selector 錯**——`label:text-is(...)` 是 HTML `<label>` 標籤選擇器，但 `bandbox.zul` 上 **`document.querySelectorAll('label').length === 0`**；ZK `<label>` 渲染成 `<span class="z-text-xs z-text-secondary z-label">`，它的父層是 `div.z-d-flex z-flex-col z-gap-2 z-div`，裡面確實有 `.z-bandbox`。 |

- **gen-b 擔心的「沒有捲軸」不成立：** 用 `.z-label` 改寫 selector 開啟該 bandpopup 後，popup listbox 3 列、`height 180`、body clientHeight **125** vs scrollHeight **158** → 有垂直捲軸，`body.offsetWidth 246 > clientWidth 240`，`th.z-listhead-bar` 寬 **6**、bg `rgba(0, 0, 0, 0)` = `th.z-listheader` bg `rgba(0, 0, 0, 0)`。也就是 selector 改成 `.z-label`（或 `:has(> .z-label:text-is(...))`）後，測試 2 的兩條斷言今天都會通過。
- 非空轉（複本在兩個 `goto` 後注入 `th.z-listhead-bar{background:rgb(240,244,250)}`）：測試 1 **失敗** `th.z-listhead-bar background must equal a sibling header cell  Expected: "rgba(0, 0, 0, 0)"  Received: "rgb(240, 244, 250)"`（第 44 行）；測試 2 仍停在 selector timeout，到不了斷言，所以測試 2 的非空轉**無法證明**，等 selector 修好要再跑一次。
- 捲軸寬 6px 是 headless Chromium 在 macOS 顯示捲軸時的寬度；寬度 > 0 的斷言成立，不空轉。

## 回歸（8085、`pw/`，不含 `forced-colors-gallery`）

指令同第一輪（`pw/run.sh`：七個 project 依序各跑一次，`--output` 指到 scratchpad，22:55–23:02）。修前對照用 [merge-1.md](merge-1.md)；第一輪數字取自 [batch14-final.md](batch14-final.md)。

| project | passed | failed | skipped | 失敗 | 第一輪 | 修前（merge-1） |
|---|---|---|---|---|---|---|
| component-theming | **107** | **0** | 0 | — | 105／2（toast 旋鈕 ×2） | 107／0 |
| hit-target | 3 | 0 | 0 | — | 3／0 | 3／0 |
| focus-scan | 57 | 0 | 47 | — | 57／0／47 | 57／0／47 |
| forced-colors | 17 | 0 | 0 | — | 17／0 | 17／0 |
| chromium | 132 | **2** | 0 | `listhead-bar-screenshot.spec.ts › bandbox.zul …`（新 spec，S4）、`toast › gallery` | 131／1（toast gallery） | 131／1 |
| gallery | 80 | **2** | 0 | `component-theming`、`grid-header`（已知） | 80／2（同） | 79／3 |
| tablet | 49 | **6** | 0 | `calendar`、`slider`（已知）、`toolbar`、`biglistbox`、`panel`、`selectbox` | 49／6（同六個） | 49／6（同六個） |

component-theming 的兩個第一輪失敗（`toast — regional bg/fg/radius/accent override`、`toast — whole-app :root override wins`）**都回到通過**（`pw/component-theming.log`），集合與 merge-1 相同（107／0）。

**逐一歸因（`reg-diff/`；像素數為 Playwright 回報值，括號內為 `reg-diff.js` thr .2／any-diff）：**

| # | 失敗 | 差異 | 歸因 |
|---|---|---|---|
| 1 | chromium `listhead-bar-screenshot.spec.ts:46 › bandbox.zul bandpopup listbox` | 非截圖：selector timeout（上節） | **本批 S4 的 spec 自身 selector 錯**（`label` 應為 `.z-label`）；頁面有捲軸、CSS 正確。本批新增。 |
| 2 | chromium `toast › gallery` | **25 px**（> `maxDiffPixels 20`；thr .2 0 px、any-diff **156 px**，與第一輪 156 相同，全在 info 圖示 19×19） | **本批 P1 預期**：info 圖示 (0,127,171)→(6,107,143)；D116-A 的預設值與第一輪的混色結果是同一個顏色，所以像素與第一輪一致。重切 `toast-gallery.png`。 |
| 3 | gallery `component-theming` | 18387→18391（+4）；thr .2 **282,932 px**（第一輪 283,032，−100）、any-diff 1,225,305 | **merge-1 第 3 項的過時 baseline（A #54 panel +4、批次 2／3／12 累積）＋本批 toast／notification 帶**（y9291–9308、y9412–9429、y9539–9560、y9660–9681）。少掉的 100 px 在 Toast regional override 帶：旋鈕值現在原樣落到圖示。重切 `component-theming-gallery.png`。 |
| 4 | gallery `grid-header` | 4525→4522；51,831 px（同第一輪／RED） | **已知**（RED S6）。 |
| 5 | tablet `calendar` | 1,145 px（同） | **已知**。 |
| 6 | tablet `slider` | 2,354 px（同） | **已知**。 |
| 7 | tablet `toolbar` | 1539→1495、620 px（同） | **merge-1 第 4 項**（A 線批次 11）。非本批。 |
| 8 | tablet `biglistbox` | 38 px（同；y493–501、y546–554 的兩個 selectbox 箭頭 ▼→chevron） | **merge-1 第 5 項**（A #29）。非本批（S1 只動 forced-colors 下的填色，normal 模式零像素差，見 S1）。 |
| 9 | tablet `panel` | 2510→2514、15,195 px（同） | **merge-1 第 6 項**（A #54）。非本批。 |
| 10 | tablet `selectbox` | 56 px（同；三個箭頭 ▼→chevron） | **merge-1 第 7 項**（A #29）。非本批。 |

**未歸因的失敗：無。** 九張截圖失敗的 Playwright 像素數與第一輪逐一相同（25／1,145／620／38／15,195／2,354／56、尺寸差相同），`component-theming-gallery` 只少 100 px（旋鈕帶）。本批元件的截圖測試 gallery `menubar`／`navbar`／`notification`／`colorbox`／`anchornav`、tablet `menubar`／`notification`、chromium `notification › shell-is-bare-and-no-stripe`／`variant-backgrounds-are-opaque`／`single-line-content-is-vertically-centred`、`toast › base-content-has-no-dark-scrim-fill`、chromium `selectbox` ×3、`listhead-bar` 測試 1 全部通過。

**需要重切的 baseline PNG（`--update-snapshots=changed`，本次沒有重切）：**
- 本批造成：`zkpreview/doc/screenshots/toast-gallery.png`（#2，156 px 全在 info 圖示）。
- 本批有份、但早已過時：`component-theming-gallery.png`（#3）。
- 與本批無關、merge-1 已列：`toolbar-tablet.png`、`biglistbox-tablet.png`、`panel-tablet.png`、`selectbox-tablet.png`。
- 已知三個：`grid-header-gallery.png`、`calendar-tablet.png`、`slider-tablet.png`。
- 通過但像素已變（1% 容差吃掉）：`notification-gallery.png`、`menubar-gallery.png`、`navbar-gallery.png`、`notification-tablet.png`、`menubar-tablet.png`（同第一輪；通過的測試不產 actual PNG，沒量 any-diff）。

## 判定

| 判定 | 第一輪 | R2 | 門檻 | 結果 |
|---|---|---|---|---|
| J-P1-1 toast info 圖示對比 | 4.85 | **4.85** | ≥ 4.5 | 過 |
| P1 旋鈕：圖示 = `#6750a4` | (89,70,138) | **(103,80,164)**，computed `rgb(103, 80, 164)` | 恰等 | 過 |
| P1 warning／error／close／底色 vs RED；forced-colors | ΔE 0；491／491／566 | ΔE 0（零差異欄位）；491／491／566 | 不變 | 過 |
| J-P2-2 notification info 圖示；J-P2-1／3 底色與箭頭 | 4.85；0 | **4.85**；**0** | ≥ 4.5；≤ 2 | 過 |
| S1 forced-colors suite；箭頭 ink | — | 17／0；7.5×5 同盒、CanvasText | 全過；8×5 ±0.5 | 過 |
| S1 非 forced 不變 | — | any-diff 0 px | 不變 | 過 |
| S3 正向／非空轉 | — | 通過／注入後失敗 | — | 過 |
| S4 正向 | — | **測試 2 失敗（selector 選不到 `<label>`）** | 2 passed | **失敗** |
| S4 非空轉 | — | 測試 1 成立；測試 2 無法證明 | 2 failed | 部分 |
| `component-theming` 回到修前集合 | 105／2 | **107／0** | 107／0 | 過 |
| 回歸：未歸因失敗 | 0 | 0 | 0 | 過 |

所有 CSS 判定（J-P1-1、P1 旋鈕契約、J-P2-1／2／3、S1）與保護項都通過，`component-theming` 回到 107／0，回歸沒有未歸因的失敗。**唯一的阻擋是本批新增的 `listhead-bar-screenshot.spec.ts` 第二個測試**：它用 HTML `<label>` 標籤選擇器找 ZK Label（渲染為 `<span class="z-label">`），在 chromium project 裡穩定 timeout，而且因此無法證明它的非空轉；這是 spec 的一行 selector 錯誤（`label` → `.z-label`），不是頁面沒有捲軸（popup listbox 的 body 125 vs 158、bar 寬 6）、也不是 CSS。修好後要再跑一次正向＋非空轉。另附帶一項需 Planner 裁決的 build 風險：minifier 把 `.z-selectbox::picker-icon` 合併進 (2e) 的 selector list，gen-b 想保留的隔離在伺服輸出中不存在。

GATE14-FINAL-R2: FAIL
