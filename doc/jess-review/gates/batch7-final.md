使用 8085（A 線，批次 7 最終判定）

# Gate：第七批最終判定（GATE7-FINAL）— selectbox 箭頭、bandbox／listbox 的 `th.z-listhead-bar`（#29 #3）

- **伺服器：** 8085，java pid 75281（17:07:58 啟動，`build/gretty_ports.properties` 17:07:59）。**沒有 kill、沒有重啟、沒有碰 8105。**
- **新 build 確認（修正已上線）：** 伺服的 `/zkau/web/80d82a3d/js/zul/wgt/css/selectbox.css.dsp` md5 `9cae05b38091a44f9d86fbb1c65925a9`（4367 bytes）、`.../sel/css/listbox.css.dsp` md5 `46bae7b75e616276d7c4f5d2d26902dc`（17323 bytes），與 `zul/build/resources/main/web/...` 的檔案 md5 逐一相同（build 檔 17:07:40–41 產生）。RED 時期 `selectbox.css` 原始檔 md5 是 `2f57456c…`（= `git HEAD` 版本），現在 `zul/src` 與 `zul/build` 的 `selectbox.css` 都是 `74714618…`、`listbox.css` 都是 `6d145336…`（HEAD 是 `0b474583…`）→ 伺服的是修正後的 build。像素本身也證實：箭頭由 11×9 變 7.5×5，`th.z-listhead-bar` 由 (240,244,250) 變透明。
- **跑完再確認：** 五個 `.css.dsp`（selectbox、listbox、menu、nav、combo）的伺服 md5 在開跑前與跑完後完全相同（`served-md5-start.txt` = `served-md5-end.txt`）→ 期間沒有別的 build 上線（另一個 agent 改 `menu.css`／`nav.css` 原始檔，伺服的 `menu.css.dsp` md5 `74230a46…` 前後不變）。
- **日期：** 2026-10-08，Fable Verifier。Playwright 1.59.1、Chromium 147.0.7727.15、viewport 1280×900、DPR 2、注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`、游標停在 (2,2)、每個狀態在重新載入的頁面上量；#3 一律 `ignoreDefaultArgs:['--hide-scrollbars']`（捲軸佔 6px）。後備路徑：WebKit 26.4、Firefox 148.0.2。ΔE 一律 **CIE76**（lib.js）。
- **方法：** lines/line-a-plan.md（過程資料已清除） 第二節，以「第 7 批 RED run 結果與方法定稿」為準（J29-1 比外框 w、h 各 ±25%；J29-2 改保護項；(e) 鏡像容差外框 ±0.5px、中心 ±1px；J3-2 刪除；#3 兩處都量）。裁示 **D52-A**（chevron，基準 combobox 8×5）、**D53-A**（所有 listbox 的 `th.z-listhead-bar` 與表頭同色）。
- **腳本與原始輸出：** `gates/batch7-final/`：`lib.js`、`final29.js`／`final3.js`（複製自 batch7-red，只改輸出檔名）→ `final29.json/.log`、`final29-*.png`、`final3.json/.log`、`final3-*.png`；新增 `final3-all.js`（兩頁每一個 listbox 的表頭掃描）→ `final3-all.json/.log`、`final3-all-listbox-header-9-bar-zoom.png`；探針 `probe29-forced.js`（forced-colors 下箭頭為何消失）→ `probe29-forced.json/.log`、`probe29-forced-*.png`；`probe29-forced-family.js`（同族 combobox／bandbox 在 forced-colors 下的對照）→ `probe29-forced-family.json/.log`、`probe29-forced-family-*.png`；回歸 `pw-regression.log` + `pw-visual/`（`pw-core/` 空 = 無失敗）；Jess 留言用截圖 `jess-shots.js` → `jess-3-bandbox-popup-after.png`、`jess-3-listbox-plain-after.png`、`jess-29-selectbox-default-after.png`、`jess-29-selectbox-default-after-zoom.png`、`jess-29-selectbox-open-after.png`（都是真實捲軸、DPR 2）。
- **沒有看 diff、沒有讀 `gates/batch7-gen.md`、沒有讀 CSS 修改內容**，沒有修改任何 theme／CSS／TS／Java／預覽頁／baseline 檔案，沒有 `--update-snapshots`、沒有 `-g` 強制覆寫，沒有 commit。計算值（`::picker-icon` 的 mask／background／transform、`th` 的 background-color）只用來解釋現象，判定全部以像素為準。

## 結論摘要

| 項目 | 判定 | 結果 | 數值（RED → FINAL） |
|---|---|---|---|
| #29 J29-1 箭頭 ink 外框 w、h 各在 combobox 8×5 的 ±25% | w、h 差 ≤ 25% | **通過** | 11×9 → **7.5×5**；w −6.25%、h 0%（combobox 今天仍 8×5） |
| #29 J29-2（保護項）中心差 ≤ 1px、右緣距與 combobox 差 ≤ 2px | — | **通過** | 中心差 +0.5 → **0**；右緣距 12.77 → **12.77**（combobox 12.08，差 0.69） |
| #29 保護項 (a) 外框 | ±0 | 通過 | 96.77–216.77 × 183–223 = 120×40，rest／hover／focus／open 四態完全相同 |
| #29 保護項 (b) 文字位置 | ±0 | 通過 | ink 左緣 110.5（內縮 13.73）、垂直中心 202.5，四態相同，與 RED 相同 |
| #29 保護項 (c) hover／focus 外觀 | 取樣相同 | 通過 | rest 邊框 (196,196,196)、hover (33,33,33)、focus (55,111,208) + inset 1px 同色（上緣剖面 183–184.5 四列藍）= RED |
| #29 保護項 (d) disabled | 不變 | 通過 | `opacity 0.38`、`background rgb(247,249,252)`、像素底 (251,252,253)、邊框 (230,230,231)、箭頭最深 (195,195,196) = RED |
| #29 保護項 (e) `:open` 旋轉鏡像 | transform 180°；外框 ±0.5、中心 ±1 | **通過** | transform `matrix(-1,0,0,-1,0,0)`；open ink 7.5×5 vs rest 7.5×5（差 0／0）、中心 203 vs 203（差 0）；列剖面 rest `2,5,6,7,8,8,8,6,4,2` 反轉 = open `2,4,6,8,8,8,7,6,5,2`（逐列差總和 **0**，RED 是 15） |
| #29 保護項 (f) forced-colors | 套件通過 **且** 箭頭可辨識 | **失敗（第二半）** | 套件 `forced-colors` **17/17 通過**；但 forced-colors 下控制項右側 40px **沒有任何 ink**（ΔE ≥ 8、≥ 3、≥ 1 全部 0 像素，5032 個像素全是 (255,255,255)）。RED 是 11×9、最深 (0,0,0)；同族 combobox 在同一模擬下 chevron **8.5×5、最深 (0,0,0)** 看得到 |
| #29 保護項 (g) 非 base-select 後備 | 不動、無重複箭頭 | 通過 | Firefox 148：`appearance none`、`background-image chevron-down-gray.svg`、1 個 ink 區塊 **12×7 @ cy 202.5、右緣距 12.767** = RED 逐字相同。WebKit 26.4（支援 base-select）1 個區塊 8×5 @ cy 202.5（RED 10×10.5，隨修正改變，記錄） |
| #3 J3-1 bandpopup 內 listbox：捲軸欄位那格 vs 表頭其餘 ΔE ≤ 2 | ≤ 2 | **通過** | (240,244,250) vs (255,255,255) 5.18 → **(255,255,255) vs (255,255,255) = 0**（對第一欄左緣也是 0） |
| #3 J3-1 一般 listbox（`listbox-header.zul` #9） | ≤ 2 | **通過** | 5.18 → **0**；補充實例 `listbox.zul` #0 執行期 `setHeight('150px')` 也 5.18 → **0** |
| #3 保護項 (a) 捲軸 | 不變 | 通過 | 寬 6px、滑塊 (224,224,224) y 501–598.5、軌道 (255,255,255) = RED |
| #3 保護項 (b) 表頭／body 欄寬 | ±0 | 通過 | 58／178／298 逐欄差 0／0（補充實例 33／169／305 也 0／0） |
| #3 保護項 (c) popup 陰影、邊框、圓角、min-width | 不變 | 通過 | 外框 shadow `rgba(0,0,0,.12) 0 2px 6px, rgba(0,0,0,.14) 0 1px 2px`、border `1px solid rgba(0,0,0,.12)`、radius 4px、min-width 300px；下方陰影列 215→230→242→247→250→253→254→255、右側 234→246→250→253→254→255、左上角 (252,252,252)／(226,226,226) = RED 逐值相同 |
| #3 保護項 (d) 列 hover、選取 | 不變 | 通過 | rest 白、hover (237,237,237) ΔE 6.25、選取 (200,213,234) ΔE 19.16、選取+hover (186,198,219) = RED |
| #3 保護項 (e) popup 尺寸位置 | ±0 | 通過 | 外框 32,421 300×230、內層 41,430 280×212 = RED |
| #3 保護項 (f) `Content` 純文字 bandbox | 不變 | 通過 | popup 122,232 282×52、平均色 (252.997,252.997,252.997)、padding 16px、13px/20px Inter = RED |
| #3 其他 listbox 無回歸 | 表頭不變（除該格） | 通過 | `listbox.zul` 8 個 + `listbox-header.zul` 18 個全掃（下文）；gallery `listbox`、`listbox-header`、`listbox-grouping`、`biglistbox`、chromium `listbox gallery/hover` 全部與 baseline 相同 |
| 回歸套件 | 只有預期變動 | 通過（變動全部歸因為箭頭） | 核心四專案 184 passed／47 skipped／0 failed；視覺 12 項：9 passed、3 failed = `selectbox-gallery`（64 px）、`selectbox-hover`（22 px）、`selectbox-focus`（22 px），diff 影像只有箭頭 |

**判定：所有判定項（J29-1、J3-1 兩處）與 J29-2 都通過；#29 的保護項 (f) 第二半「箭頭在 forced-colors 下仍可辨識」失敗（0 ink）。依字面規則本報告 FAIL，只差這一項。**

## #29 — selectbox 箭頭

| 狀態（各自重新載入） | 外框 | 箭頭 ink w×h | ink 中心 (cx, cy) | 控制項中心 | 中心差 | 右緣距 | 高寬比 | 最深像素 |
|---|---|---|---|---|---|---|---|---|
| rest（selectbox[0]） | 96.77–216.77 × 183–223 | **7.5×5** | (200.25, 203) | 203 | **0** | **12.77** | 1.5 | (102,102,102) |
| pre-selected（selectbox[2]） | 32–152 × 334–374 | 7.5×5 | (135.25, 354) | 354 | 0 | 13.00 | 1.5 | (102,102,102) |
| disabled（selectbox[1]） | 684.38–804.38 × 183–223 | 7×5 | (787.5, 203) | 203 | 0 | 13.38 | 1.4 | (195,195,196)，底 (251,252,253) |
| hover（控制項中央／箭頭上） | 同 rest | 7.5×5 | (200.25, 203) | 203 | 0 | 12.77 | 1.5 | 邊框 (33,33,33) |
| focus（Tab 1 次，`:focus-visible`） | 同 rest | 7.5×5 | (200.25, 203) | 203 | 0 | 12.77 | 1.5 | 邊框 (55,111,208) |
| open（點開後游標移開） | 同 rest | **7.5×5** | (200.25, **203**) | 203 | **0** | 12.77 | 1.5 | transform `matrix(-1,0,0,-1,0,0)` |
| **forced-colors** | 同 rest | **無 ink（0 像素）** | — | 203 | — | — | — | 區域 5032 像素全 (255,255,255)；邊框 (0,0,0)、文字 ink 仍在 |
| combobox chevron（`combobox.zul` [0]） | 112.08–317.08 × 183–223 | **8×5** | (301, 203) | 203 | 0 | **12.08** | 1.6 | (102,102,102)，1 個區塊 |
| Firefox 148 後備 | 96.77–216.77 × 182.5–222.5 | 12×7 | (198, 202.5) | 202.5 | 0 | 12.77 | 1.714 | 1 個區塊（= RED） |
| WebKit 26.4（base-select） | 96.77–216.77 × 182.7–222.7 | 8×5 | (201, 202.5) | 202.7 | −0.2 | 11.77 | 1.6 | 1 個區塊 |

- **J29-1：** 7.5／8 = −6.25%、5／5 = 0% → 兩者都在 ±25% 內（RED +37.5%／+80%）。箭頭現在是與 combobox 同族的 chevron（`::picker-icon` 14×14 的 `chevron-down.svg` 遮罩，`mask-size contain`；只為解釋）。
- **J29-2（保護項）：** 中心差 0 ≤ 1；右緣距差 |12.766 − 12.078| = 0.69 ≤ 2。沒有退步（RED 0.5／0.69）。
- **(e)：** 旋轉後的 ink 外框與未旋轉完全相同（7.5×5）、中心相同（203），列剖面反轉逐列相等。
- **(f) 失敗的證據（`probe29-forced.json`）：** `forcedColors:'active'` 的 context 與 `page.emulateMedia({forcedColors:'active'})` 兩種方式結果相同：`matchMedia('(forced-colors: active)')` true、控制項邊框 (0,0,0)、文字 ink 在、**右側 40px 內無任何非白像素**（`probe29-forced-forced.png`：只有空框）。解釋（計算值）：`::picker-icon` 的圖形是 `mask-image: chevron-down.svg` + `background-color`，一般模式 background `rgba(0,0,0,.6)`；forced-colors 下 background 被強制成 **`rgba(255,255,255,0.6)`**（Canvas），遮罩裡填的是白色 → 白底上看不見；`forced-color-adjust` 是 `auto`。同族對照（`probe29-forced-family.json`）：combobox 的 `.z-combobox-icon` 同樣是 `chevron-down.svg` 遮罩，但 forced-colors 下 background 是 **`rgb(0,0,0)`**、chevron 8.5×5 可見；bandbox 的 search icon (0,0,0) 11×11 可見。也就是說同族 chevron 已有 forced-colors 的處理，selectbox 的 `::picker-icon` 沒有跟上。RED 時 selectbox 用瀏覽器內建 ▼（`color` 繪製）在 forced-colors 下是黑的 11×9，所以這是本批修正**引入**的退步，不是既有狀態。`forced-colors` 套件 17/17 通過是因為該套件檢查 token／邊框／文字，不量 `::picker-icon`。
- **(g)：** Firefox 的 `chevron-down-gray.svg` 後備路徑逐值與 RED 相同、仍只有一個箭頭。

## #3 — `th.z-listhead-bar`

**根因證據（`final3.json` `bandbox.open.stack`）：** `document.elementsFromPoint(301, 473.5)` 由上而下仍是 `TH.z-listhead-bar`（rect 298,447 6×53）→ `TABLE` → `DIV.z-listbox-header` → `DIV.z-listbox`（白）→ … ，但 `th.z-listhead-bar` 的 `background-color` 由 `rgb(240,244,250)` 變成 **`rgba(0,0,0,0)`**，所以該格顯示的是 listbox 的白底。

| 實例 | 捲軸寬 | 該格像素 | 表頭空白（欄界 ±6px）| 表頭空白（第一欄左緣）| ΔE（RED → FINAL） |
|---|---|---|---|---|---|
| bandbox.zul 的 bandpopup listbox（issue 本體） | 6 | (255,255,255) | (255,255,255) | (255,255,255) | 5.18 → **0** |
| `listbox-header.zul` #9，`<listbox height="200px">`（一般 listbox） | 6 | (255,255,255) | (255,255,255) | (255,255,255) | 5.18 → **0** |
| `listbox.zul` #0 執行期 `setHeight('150px')`（補充實例） | 6 | (255,255,255) | (255,255,255) | (255,255,255) | 5.18 → **0** |

- 直條掃描（原 J3-2 的工具，只作紀錄）：bandpopup 內 listbox body 只有 x 298–304 的捲軸滑塊 (224,224,224)（= RED）；`listbox-header.zul` #9 的 x 158–159、736–737 兩條 (240,244,250) 與 RED 逐值相同（該 listbox 的 listgroup 列，與本題無關）；`listbox.zul` #0 的 41 條偏藍欄 = 其 `selected` 列 (200,213,234)，與 RED 相同。
- **兩頁全部 listbox 掃描（`final3-all.json`，真實捲軸）：** `listbox.zul` 8 個、`listbox-header.zul` 18 個。有垂直捲軸的只有 `listbox-header.zul` #9（ΔE 0，上表）；其餘 25 個捲軸寬 0、`th.z-listhead-bar` 寬 0（畫不出來，與 RED 一樣），它們的表頭空白像素：`listbox.zul` 全部 (255,255,255)、`listbox-header.zul` #0–2、#11–12（9 欄的例子）與 #17 第一欄 (247,249,252)、其餘 (255,255,255)；所有 `th` 的計算 background 現在都是透明（#5／#15 有一欄 `rgb(255,255,255)`）。這些表頭的「不變」由 gallery baseline 把關：`listbox-gallery`、`listbox-header-gallery`、`listbox-grouping-gallery`、`biglistbox-gallery` 與 chromium `listbox gallery`／`listbox hover` **全部通過**（Playwright 預設隱藏捲軸，baseline 本來就看不到那一格，所以「除該格外不變」正是這些 baseline 證明的）。

## 回歸（`pw-regression.log`，`--output` 到 `pw-core/`、`pw-visual/`；沒有 `--update-snapshots`）

- **核心四專案全跑**（`component-theming` 107、`hit-target` 3、`focus-scan` 104、`forced-colors` 17，共 231）：**184 passed、47 skipped（focus-scan 無可聚焦元件的頁面，與第 6 批相同）、0 failed**，2.4 分鐘。
- **視覺（`gallery`、`chromium`，`-g "selectbox|bandbox|listbox"`，12 項）：** 9 passed、**3 failed**：
  - `selectbox-gallery.png`：64 px 不同（ratio 0.01）— diff 只有三個 selectbox（Default、Disabled、Pre-selected）的箭頭位置 → **預期（箭頭）**。
  - `selectbox-hover.png`：22 px — diff 只有箭頭 → **預期（箭頭）**。
  - `selectbox-focus.png`：22 px — diff 只有箭頭 → **預期（箭頭）**。
  - 通過：`gallery › biglistbox`、`listbox-grouping`、`listbox-header`；chromium `listbox › gallery`、`listbox › hover`、`bandbox › gallery`、`bandbox › hover`、`bandbox › focus`、`datebox & bandbox open-state`。`bandbox-gallery` 靜止畫面不變，與計畫預期一致。
  - 已知失敗 `calendar-tablet`、`slider-tablet`、`grid-header-gallery` 不在本次篩選範圍內，沒有跑。
  - 沒有預期外的 baseline 變動；三張需要重生的 baseline 都是 selectbox 的箭頭（`pw-visual/*/‑diff.png` 可看），本報告沒有重生。

## Jess 留言用截圖（`gates/batch7-final/`，真實捲軸，修後）

- `jess-3-bandbox-popup-after.png`：bandbox 打開的 bandpopup，表頭右端（捲軸上方）與表頭同為白色，捲軸滑塊灰色。修前對照：`../batch7-red/red3-bandbox-open.png`。
- `jess-3-listbox-plain-after.png`：`listbox-header.zul` #9 一般 listbox（有垂直捲軸）。修前對照：`../batch7-red/red3-listbox-header9.png`。
- `jess-29-selectbox-default-after.png`（三個 selectbox）、`jess-29-selectbox-default-after-zoom.png`（第一個）、`jess-29-selectbox-open-after.png`（打開、箭頭朝上）。修前對照：`../batch7-red/red29-rest.png`、`red29-open-page.png`。

## 判定說明

- 兩個 issue 的訴求本身都已修好：箭頭成了同族 chevron、尺寸 7.5×5（基準 8×5）、位置與旋轉比修前更準；`th.z-listhead-bar` 在 bandpopup 與所有一般 listbox 都與表頭同色（ΔE 0），其餘保護項逐值與 RED 相同，回歸只有預期中的 selectbox 箭頭 baseline。
- **唯一失敗：#29 保護項 (f) 第二半。** forced-colors 下箭頭消失（0 ink），修前是可見的黑色 11×9，同族 combobox 的 chevron 在同一模擬下可見。這是本批修正引入的退步，屬於 `selectbox.css` 單一檔案、單一偽元素的範圍；修好後只需重跑 `final29.js`（forced 一欄）與 `probe29-forced.js`，其餘數據不受影響。
- 本報告沒有更新任何 baseline；三張 selectbox baseline 待 (f) 修好、再次通過後一起重生。

GATE7-FINAL: FAIL
