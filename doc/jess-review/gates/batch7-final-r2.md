使用 8085（A 線，批次 7 最終判定 R2）

# Gate：第七批最終判定 R2（GATE7-FINAL-R2）— 只重驗 #29 保護項 (f) 修正後，其餘全部重跑確認（#29 #3）

- **前次判定：** [batch7-final.md](batch7-final.md) 判 `GATE7-FINAL: FAIL`，唯一失敗項是 #29 保護項 (f) 第二半：forced-colors 下新的 selectbox chevron 消失（右側 40px 內 0 ink）。本輪是修正後的完整再驗。
- **伺服器：** 8085，java pid 19767（17:19:43 啟動，`zkpreview/build/gretty_ports.properties` 17:19）。**沒有 kill、沒有重啟、沒有碰 8105。** 另一個 Verifier 同時在同一台 8085 量第 8 批（唯讀），對本輪量測無影響（所有與前次相同的像素都逐 byte 相同，見下）。
- **新 build 確認（修正已上線）：** 伺服的 `/zkau/web/80d82a3d/js/zul/wgt/css/selectbox.css.dsp` md5 **`5b77ddb146f78944f167a1c32c6486a6`**（4452 bytes；前次是 `9cae05b3…` 4367 bytes），與 `zul/build/resources/main/web/js/zul/wgt/css/selectbox.css.dsp`（17:19:29 產生）md5 相同；`zul/src` 與 `zul/build` 的 `selectbox.css` 都是 `9de0ebdd…`（src 17:18:41；前次 `74714618…`）。`listbox.css.dsp` 伺服 md5 `46bae7b7…`、`listbox.css` src／build `6d145336…`，**與前次完全相同（listbox 沒有再動）**。伺服的 `selectbox.css.dsp` 以 `grep -c forced-colors` 得 **1**（build 檔也是 1；`git HEAD` 的 `selectbox.css` 是 0）：`@media (forced-colors:active){.z-selectbox::picker-icon{background-color:canvastext}}`。
  - 誠實說明：該檔是單行壓縮 CSS，`grep -n` 把整行印出來，所以我**看到了修正後整份 selectbox CSS 的內容**（不是 diff、不是 gen.md）。判定仍全部以像素為準；計算值只作解釋。
- **跑完再確認：** 五個 `.css.dsp`（selectbox、listbox、menu、nav、combo）開跑前（`served-md5-start.txt` 17:21）與跑完後（`served-md5-end.txt` 17:26）md5 逐一相同 → 期間沒有別的 build 上線。（`menu.css.dsp` 伺服 md5 `6a7acc1d…` 與前次的 `74230a46…` 不同，那是第 8 批那條線的 build，與本批無關；`combo.css.dsp`、`nav.css.dsp` 與前次相同。）
- **日期：** 2026-10-08，Fable Verifier。Playwright 1.59.1、Chromium 147.0.7727.15、viewport 1280×900、DPR 2、注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`、游標停在 (2,2)、每個狀態在重新載入的頁面上量；#3 一律 `ignoreDefaultArgs:['--hide-scrollbars']`（捲軸佔 6px）。後備路徑：WebKit 26.4、Firefox 148.0.2。ΔE 一律 **CIE76**（lib.js）。
- **方法：** lines/line-a-plan.md（過程資料已清除） 第二節，以「第 7 批 RED run 結果與方法定稿」為準；裁示 **D52-A**（chevron，基準 combobox 8×5）、**D53-A**（所有 listbox 的 `th.z-listhead-bar` 與表頭同色）。
- **腳本與原始輸出：** `gates/batch7-final-r2/`：`lib.js`、`final29.js`、`probe29-forced.js`、`probe29-forced-family.js`、`final3.js`、`final3-all.js`、`jess-shots.js` **全部由 `batch7-final/` 逐 byte 複製、一字未改**（md5 相同），輸出檔名也相同，便於與前次逐檔比對 → `final29.json/.log`、`final29-*.png`、`probe29-forced.json/.log`、`probe29-forced-*.png`、`probe29-forced-family.json/.log`、`probe29-forced-family-*.png`、`final3.json/.log`、`final3-*.png`、`final3-all.json/.log`、`final3-all-listbox-header-9-bar-zoom.png`、回歸 `pw-regression.log` + `pw-visual/`（`pw-core/` 空 = 無失敗）、Jess 留言用截圖 `jess-3-bandbox-popup-after.png`、`jess-3-listbox-plain-after.png`、`jess-29-selectbox-default-after.png`、`jess-29-selectbox-default-after-zoom.png`、`jess-29-selectbox-open-after.png`（真實捲軸、DPR 2，修後重存）。
- **與前次逐檔比對（md5）：** 57 個 png/json/log/js 中 **45 個與 `batch7-final/` 逐 byte 相同**；不同的只有 forced-colors 相關的 `final29-forced.png`、`probe29-forced-forced.png`、`probe29-forced-forced-emulateMedia.png`、`probe29-forced-family-selectbox-forced.png` 與其 json/log（`final29.log` 用 `diff` 比對，**只有 `forced` 那一行不同**）、`pw-regression.log`（時間戳）、新增的 `jess-shots.log`。`final3.log`、`final3-all.log` 與前次 **逐字相同**。五張 Jess 截圖與前次逐 byte 相同（修正只影響 forced-colors 下的繪製，一般模式像素不變）。
- **沒有看 diff、沒有讀 `gates/batch7-gen.md`**，沒有修改任何 theme／CSS／TS／Java／預覽頁／baseline 檔案，沒有 `--update-snapshots`、沒有 `-g` 強制覆寫，沒有 commit。

## 結論摘要

| 項目 | 判定 | 結果 | 數值（FINAL → R2） |
|---|---|---|---|
| #29 J29-1 箭頭 ink 外框 w、h 各在 combobox 8×5 的 ±25% | w、h 差 ≤ 25% | **通過** | **7.5×5 → 7.5×5**；w −6.25%、h 0%（combobox 今天仍 8×5） |
| #29 J29-2（保護項）中心差 ≤ 1px、右緣距與 combobox 差 ≤ 2px | — | **通過** | 中心差 0 → **0**；右緣距 12.766 → **12.766**（combobox 12.078，差 0.69） |
| #29 保護項 (a) 外框 | ±0 | 通過 | 96.77–216.77 × 183–223 = 120×40，rest／hover／focus／open 四態相同 = FINAL |
| #29 保護項 (b) 文字位置 | ±0 | 通過 | ink 左緣 110.5（內縮 13.73）、垂直中心 202.5，四態相同 = FINAL |
| #29 保護項 (c) hover／focus 外觀 | 取樣相同 | 通過 | rest 邊框 (196,196,196)、hover (33,33,33)、focus (55,111,208) + inset 1px 同色 = FINAL |
| #29 保護項 (d) disabled | 不變 | 通過 | `opacity 0.38`、`background rgb(247,249,252)`、像素底 (251,252,253)、邊框 (230,230,231)、箭頭 7×5 最深 (195,195,196) = FINAL |
| #29 保護項 (e) `:open` 旋轉鏡像 | transform 180°；外框 ±0.5、中心 ±1 | 通過 | `matrix(-1,0,0,-1,0,0)`；open 7.5×5 vs rest 7.5×5、中心 203 vs 203；列剖面反轉逐列差總和 **0** = FINAL |
| **#29 保護項 (f) forced-colors** | 套件通過 **且** 箭頭可辨識 | **通過（前次失敗，已修好）** | 套件 `forced-colors` **17/17**；箭頭 ink **0 像素 → 7.5×5（56 像素，與一般模式同形）**，最深 **(0,0,0)**，中心 (200.25, 203)、右緣距 12.766（與一般模式逐值相同）；兩種模擬方式（context `forcedColors:'active'`、`page.emulateMedia`）結果相同；同族 combobox 在同一模擬下 8.5×5、最深 (0,0,0) |
| #29 保護項 (g) 非 base-select 後備 | 不動、無重複箭頭 | 通過 | Firefox 148：`appearance none`、`background-image chevron-down-gray.svg`、1 個 ink 區塊 12×7 @ cy 202.5、右緣距 12.767 = FINAL = RED。WebKit 26.4（base-select）1 個區塊 8×5 @ cy 202.5 = FINAL |
| #3 J3-1 bandpopup 內 listbox：捲軸欄位那格 vs 表頭其餘 ΔE ≤ 2 | ≤ 2 | **通過** | (255,255,255) vs (255,255,255) = **0**（對第一欄左緣也 0）= FINAL |
| #3 J3-1 一般 listbox（`listbox-header.zul` #9） | ≤ 2 | **通過** | **0**；補充實例 `listbox.zul` #0 執行期 `setHeight('150px')` 也 **0** = FINAL |
| #3 保護項 (a) 捲軸 | 不變 | 通過 | 寬 6px、滑塊 (224,224,224) y 501–598.5、軌道 (255,255,255) = FINAL |
| #3 保護項 (b) 表頭／body 欄寬 | ±0 | 通過 | 58／178／298 逐欄差 0／0（補充實例 33／169／305 也 0／0）= FINAL |
| #3 保護項 (c) popup 陰影、邊框、圓角、min-width | 不變 | 通過 | shadow `rgba(0,0,0,.12) 0 2px 6px, rgba(0,0,0,.14) 0 1px 2px`、border `1px solid rgba(0,0,0,.12)`、radius 4px、min-width 300px；下方陰影列 215→230→242→247→250→253→254→255 = FINAL |
| #3 保護項 (d) 列 hover、選取 | 不變 | 通過 | rest 白、hover (237,237,237) ΔE 6.25、選取 (200,213,234) ΔE 19.16、選取+hover (186,198,219) = FINAL |
| #3 保護項 (e) popup 尺寸位置 | ±0 | 通過 | 外框 32,421 300×230、內層 41,430 280×212 = FINAL |
| #3 保護項 (f) `Content` 純文字 bandbox | 不變 | 通過 | popup 122,232 282×52、平均色 (252.997,252.997,252.997)、padding 16px、13px/20px Inter = FINAL |
| #3 其他 listbox 無回歸 | 表頭不變（除該格） | 通過 | `listbox.zul` 8 個 + `listbox-header.zul` 18 個全掃，`final3-all.log` 與前次逐字相同；gallery `listbox`、`listbox-header`、`listbox-grouping`、`biglistbox`、chromium `listbox gallery/hover` 全部通過 |
| 回歸套件 | 只有預期變動 | 通過（變動全部歸因為箭頭） | 核心四專案 184 passed／47 skipped／0 failed；視覺 12 項：9 passed、3 failed = `selectbox-gallery`（64 px）、`selectbox-hover`（22 px）、`selectbox-focus`（22 px），actual／diff／expected 三組 png 與前次 **逐 byte 相同**，diff 只有箭頭；**沒有任何新的預期外差異** |

**判定：所有判定項（J29-1、J3-1 兩處）、J29-2 與保護項 (a)–(g)、#3 (a)–(f) 全部通過；前次唯一失敗的 (f) 已修好，其餘數據與前次逐值相同。**

## #29 — selectbox 箭頭（`final29.log`）

| 狀態（各自重新載入） | 外框 | 箭頭 ink w×h | ink 中心 (cx, cy) | 控制項中心 | 中心差 | 右緣距 | 高寬比 | 最深像素 |
|---|---|---|---|---|---|---|---|---|
| rest（selectbox[0]） | 96.77–216.77 × 183–223 | 7.5×5 | (200.25, 203) | 203 | 0 | 12.766 | 1.5 | (102,102,102) |
| pre-selected（selectbox[2]） | 32–152 × 334–374 | 7.5×5 | (135.25, 354) | 354 | 0 | 13.00 | 1.5 | (102,102,102) |
| disabled（selectbox[1]） | 684.38–804.38 × 183–223 | 7×5 | (787.5, 203) | 203 | 0 | 13.375 | 1.4 | (195,195,196)，底 (251,252,253) |
| hover（控制項中央／箭頭上） | 同 rest | 7.5×5 | (200.25, 203) | 203 | 0 | 12.766 | 1.5 | 邊框 (33,33,33) |
| focus（Tab，`:focus-visible`） | 同 rest | 7.5×5 | (200.25, 203) | 203 | 0 | 12.766 | 1.5 | 邊框 (55,111,208) |
| open（點開後游標移開） | 同 rest | 7.5×5 | (200.25, 203) | 203 | 0 | 12.766 | 1.5 | transform `matrix(-1,0,0,-1,0,0)` |
| **forced-colors（修後）** | 同 rest | **7.5×5（56 像素）** | **(200.25, 203)** | 203 | **0** | **12.766** | 1.5 | **(0,0,0)**；邊框 (0,0,0)、文字 ink 在 |
| combobox chevron（`combobox.zul` [0]） | 112.08–317.08 × 183–223 | 8×5 | (301, 203) | 203 | 0 | 12.078 | 1.6 | (102,102,102)，1 個區塊 |
| Firefox 148 後備 | 96.77–216.77 × 182.5–222.5 | 12×7 | (198, 202.5) | 202.5 | 0 | 12.767 | 1.714 | 1 個區塊 |
| WebKit 26.4（base-select） | 96.77–216.77 × 182.7–222.7 | 8×5 | (201, 202.5) | 202.7 | −0.2 | 11.766 | 1.6 | 1 個區塊 |

- **(f) 修好的證據（`probe29-forced.json`、`probe29-forced-family.json`）：**
  - 兩種模擬方式結果完全相同：context `forcedColors:'active'` 與 `page.emulateMedia({forcedColors:'active'})` 下 `matchMedia('(forced-colors: active)')` 都是 true；右側 40px 區域（5032 像素）的色票：白 4965、**(0,0,0) 27**、(107,107,107) 7、(101,101,101) 5、…（前次是全部 5032 像素 (255,255,255)）。ink 在 ΔE ≥ 8、≥ 3、≥ 1 三個門檻分別是 56／56／61 像素、外框一律 7.5×5、cy 203、最深 (0,0,0)。一般模式對照：56／56／59、7.5×5、最深 (102,102,102) → 形狀與位置相同，只有顏色由 60% 黑變純黑（CanvasText）。
  - 同族對照（同一模擬）：combobox `.z-combobox-icon` chevron 8.5×5、最深 (0,0,0)（ΔE ≥ 1 時 9×5）；bandbox search icon 11×11、最深 (0,0,0)；selectbox 7.5×5、最深 (0,0,0) → 三者同為黑色 CanvasText，selectbox 的 chevron 與 combobox 的大小差 −11.8%（w）／0%（h），可辨識且與同族可比。
  - 解釋（計算值，只作說明）：`::picker-icon` 的 `background-color` 在 forced-colors 下現在是 `rgb(0,0,0)`（前次 `rgba(255,255,255,0.6)`），`mask-image` 仍是 `chevron-down.svg`、`mask-size contain`、`forced-color-adjust auto`。這正對應伺服檔裡那條 `@media (forced-colors:active)` 規則。
  - 截圖：`final29-forced.png`（控制項全圖，黑框、黑字「Bob」、右側黑色 chevron）、`probe29-forced-forced.png`／`probe29-forced-forced-emulateMedia.png`（右側 48px 放大區，兩張 md5 相同）、`probe29-forced-family-combobox-forced.png`（combobox 對照）。
- **其餘 (a)–(e)、(g) 與 J29-1／J29-2：** `final29.log` 與前次 `diff` 後**只有 `forced` 那一行不同**，所有一般模式、hover、focus、open、disabled、combobox、Firefox、WebKit 的數字逐字相同；`final29-rest/hover/hoverArrow/focus/open/open-page/preselected/disabled/combobox/fallback-*.png` 全部與前次逐 byte 相同。

## #3 — `th.z-listhead-bar`（`final3.log`、`final3-all.log`，與前次逐字相同）

**根因證據（`final3.json` `bandbox.open.stack`）：** `document.elementsFromPoint(301, 473.5)` 由上而下 `TH.z-listhead-bar`（rect 298,447 6×53，`background-color rgba(0,0,0,0)`）→ `TABLE` → `DIV.z-listbox-header` → `DIV.z-listbox`（白）→ … → 該格顯示 listbox 的白底。

| 實例 | 捲軸寬 | 該格像素 | 表頭空白（欄界 ±6px）| 表頭空白（第一欄左緣）| ΔE |
|---|---|---|---|---|---|
| bandbox.zul 的 bandpopup listbox（issue 本體） | 6 | (255,255,255) | (255,255,255) | (255,255,255) | **0** |
| `listbox-header.zul` #9，`<listbox height="200px">`（一般 listbox） | 6 | (255,255,255) | (255,255,255) | (255,255,255) | **0** |
| `listbox.zul` #0 執行期 `setHeight('150px')`（補充實例） | 6 | (255,255,255) | (255,255,255) | (255,255,255) | **0** |

- 直條掃描（紀錄）：bandpopup 內 listbox body 只有 x 298–304 的捲軸滑塊 (224,224,224)；`listbox-header.zul` #9 的 x 158–159、736–737 兩條 (240,244,250) 是 listgroup 列（與 RED、FINAL 相同，與本題無關）；`listbox.zul` #0 的 41 條偏藍欄 = `selected` 列 (200,213,234)。
- **兩頁全部 listbox 掃描（`final3-all.json`）：** 26 個 listbox 的輸出與前次逐字相同：有垂直捲軸的只有 `listbox-header.zul` #9（ΔE 0／0）；其餘 25 個捲軸寬 0、bar 格寬 0；表頭空白像素 `listbox.zul` 全部 (255,255,255)，`listbox-header.zul` #0–2、#11–12、#17 第一欄 (247,249,252)、其餘 (255,255,255)；所有 `th` 計算 background 透明（#5／#15 有一欄 `rgb(255,255,255)`）。gallery `listbox`、`listbox-header`、`listbox-grouping`、`biglistbox` 與 chromium `listbox gallery`／`hover` 全部通過。

## 回歸（`pw-regression.log`，`--output` 到 `pw-core/`、`pw-visual/`；沒有 `--update-snapshots`）

- 指令：`npx playwright test --config src/test/playwright/playwright.config.ts --project=component-theming --project=hit-target --project=focus-scan --project=forced-colors --output …/pw-core`，接著 `… --project=gallery --project=chromium -g 'selectbox|bandbox|listbox' --output …/pw-visual`（`baseURL` 預設 `http://localhost:8085`，config 沒有 `webServer`，不會自己起伺服器）。
- **核心四專案全跑**（231 項，17:22:56–17:25:27，2.5 分鐘）：**184 passed、47 skipped、0 failed** = 前次。逐專案：`component-theming` 107/0/0、`hit-target` 3/0/0、`focus-scan` 57 passed／47 skipped（無可聚焦元件的頁面，與第 6 批相同）、**`forced-colors` 17/0/0**。
- **視覺（`gallery`、`chromium`，12 項，10.0 秒）：** 9 passed、**3 failed**：
  - `selectbox-gallery.png`：64 px（ratio 0.01）— diff 只有三個 selectbox（Default、Disabled、Pre-selected）的箭頭 → **預期（箭頭）**。
  - `selectbox-hover.png`：22 px — diff 只有箭頭 → **預期（箭頭）**。
  - `selectbox-focus.png`：22 px — diff 只有箭頭 → **預期（箭頭）**。
  - 這三組的 `-actual.png`、`-diff.png`、`-expected.png` 九個檔案 md5 與前次 `batch7-final/pw-visual/` **逐一相同**；也就是說 forced-colors 修正沒有改變一般模式任何一個像素。
  - 通過：`gallery › biglistbox`、`listbox-grouping`、`listbox-header`；chromium `listbox › gallery`、`listbox › hover`、`bandbox › gallery`、`bandbox › hover`、`bandbox › focus`、`datebox & bandbox open-state`。
  - 已知失敗 `calendar-tablet`、`slider-tablet`、`grid-header-gallery` 不在篩選範圍內，沒有跑。
  - **沒有任何新的預期外差異**；三張待重生的 baseline 仍只是 selectbox 的箭頭，本報告沒有重生。

## Jess 留言用截圖（`gates/batch7-final-r2/`，真實捲軸，修後重存；與前次逐 byte 相同）

- `jess-3-bandbox-popup-after.png`：bandbox 打開的 bandpopup，表頭右端（捲軸上方）與表頭同為白色，捲軸滑塊灰色。修前對照：`../batch7-red/red3-bandbox-open.png`。
- `jess-3-listbox-plain-after.png`：`listbox-header.zul` #9 一般 listbox（有垂直捲軸）。修前對照：`../batch7-red/red3-listbox-header9.png`。
- `jess-29-selectbox-default-after.png`（三個 selectbox）、`jess-29-selectbox-default-after-zoom.png`（第一個）、`jess-29-selectbox-open-after.png`（打開、箭頭朝上）。修前對照：`../batch7-red/red29-rest.png`、`red29-open-page.png`。
- forced-colors 下的修後截圖另見 `final29-forced.png`（前次失敗的對照：`../batch7-final/final29-forced.png`，空框）。

## 判定說明

- 前次唯一失敗的 #29 保護項 (f) 第二半已修好：forced-colors 下 chevron 7.5×5、純黑 (0,0,0)，與一般模式同形同位，與同族 combobox（8.5×5、黑）可比；兩種模擬方式一致；`forced-colors` 套件 17/17。
- 修正範圍只影響 forced-colors：一般模式的所有 #29 數據、全部 #3 數據、五張 Jess 截圖、三組視覺 diff 與前次逐 byte／逐字相同，核心回歸 184/47/0 不變。
- 本報告沒有更新任何 baseline；`selectbox-gallery`、`selectbox-hover`、`selectbox-focus` 三張 baseline 可在合併時重生（diff 只有箭頭）。

GATE7-FINAL-R2: PASS
