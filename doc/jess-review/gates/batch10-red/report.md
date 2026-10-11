使用 8105

# 批次 10 RED run 報告（#4 button、#6 calendar、#40 label、#47 fisheyebar、#61 tbeditor）

- 日期：2026-10-08；Verifier：Fable；`PREVIEW_URL=http://127.0.0.1:8105`（B 線）；Playwright 1.59.1（jess-b `zkpreview/node_modules`），viewport 1280×900、DPR 2，transition／animation 關閉。
- 方法依 lines/line-b-plan.md（過程資料已清除） 第七之二節；未修改任何 CSS／TS／Java／預覽頁／baseline。所有腳本、JSON、截圖都在本目錄（`lib.js` 複製自 `batch9-red/lib.js`，只改第一行註解）。
- 色差一律 CIE76 ΔE（`lib.js` 的 `dE`）。

## 一、結論摘要表

| 編號 | 判定 | RED 結果 | 數值 |
|---|---|---|---|
| J4-1 | text 六變體 × 四狀態 `box-shadow: none`、外圍 4px 無暈 | **失敗（符合預期）**：5／6 變體失敗 | `z-button-text` 四狀態皆 `none`、暈 ΔE 0；`-secondary`／`-success`／`-warning`／`-error`／`-info` 靜止／hover／按住 computed 皆為 `rgba(50,50,93,.024) 0 2px 5px -1px, rgba(0,0,0,.05) 0 1px 3px -1px`（= `.z-button` 的 resting elevation），4px 環最差 ΔE 2.79（634／3904 px > 1）；`-info` 因頁面容器裁掉底緣只剩 ΔE 1.04（60 px） |
| P4-a | filled／outlined／icon-only 靜止與 hover 的 `box-shadow` | 通過（基線已記錄） | filled／secondary／icon-only：靜止 resting、hover `rgba(0,0,0,.12) 0 2px 6px, rgba(0,0,0,.14) 0 1px 2px`；`z-button-outlined` 靜止／hover 皆 `none`；fab 變體頁面上不存在，無法量 |
| P4-b | disabled 全變體 `none` | 通過 | 頁面 22 顆 disabled 按鈕 computed 皆 `none` |
| P4-c | text 變體文字色／hover 狀態層／cursor | 通過（基線） | 文字色 primary／`oklch(0.54 0.066 260.56)`／`#2e7d32`／`#bd3f00`／`#d32f2f`／`#007fab`；`::before` opacity 靜止 0、hover 0.08、聚焦與按住 0.12；cursor `pointer`，disabled `not-allowed` |
| P4-d | 按鈕 rect | 通過（基線） | text 變體 76.69×36；outlined 78.69×36；icon-only 46×36 |
| J6-1 | 當月 disabled 日期（含週六日）同色 | **失敗（符合預期）** | 平日 disabled `rgba(0,0,0,.38)`（最深像素 (98,98,98)），週末 disabled `rgba(0,0,0,.87)`（最深像素 (4,4,4)），兩群最深色 ΔE **40.46**；在 Mar 2020（全月 disabled）、Oct 2026（no past 混合月）、Oct 2026（no future 混合月）三組都相同 |
| J6-2 | disabled 仍有刪除線／`not-allowed`／`pointer-events:none` | 通過（今天已成立） | 三組 disabled 全部 `line-through`、`not-allowed`、`none` |
| P6-a | 可選週末色 = 可選平日色 | 通過（基線） | 兩者 `rgba(0,0,0,.87)`，最深像素 (33,33,33)，ΔE 0 |
| P6-b | 選取日（週日 15）文字 `on-primary`、圓盤色 | 通過（基線） | computed `rgb(255,255,255)`，圓盤中位 (55,111,208)，最亮像素 (255,255,255) |
| P6-c | 月外日 opacity 0.38 | 通過（基線） | 月外 td opacity 皆 0.38（但月外 disabled 週末 vs 平日 computed 仍是 .87 vs .38，最深像素 ΔE 13.3——同一根因） |
| P6-d | 週末表頭色 | 通過（基線） | 七個 th 皆 `rgba(0,0,0,.6)` |
| P6-e | hover 圓盤只在可選日 | 通過（基線） | 可選平日／週末 hover 後圓盤探針 (237,237,237)、ΔE 6.25；disabled 平日／週末 `:hover` 不成立、探針 (255,255,255) |
| P6-f | 其他 constraint 範例 disabled 同色 | 今天同樣失敗（同根因） | `no future` 導到 Oct 2026：平日 .38 vs 週末 .87，ΔE 40.46 |
| J40-1 | checkbox 文字與 radio 文字 font-size／weight／line-height 相同 | **失敗（符合預期）** | `.z-checkbox-content` 13px／400／20px；`.z-radio-content` **14px**／400／20px（block 列與 flex 列皆同） |
| P40-a | checkbox 13px | 通過（基線） | 13px |
| P40-b | radio 圓圈尺寸、控制高度、文字／圓圈中心差 | 通過（基線） | input 20×20、外圈墨水 20×20（off 色 (102,102,102)，on 色 (55,111,208)，內點 13×13）；`.z-radio` rect 高 40、`min-height` 40px；文字墨水中心 − 圓圈中心 = **0.0px**（textRect 中心 −0.5px） |
| P40-c | radio disabled／checked／focus 外觀 | 通過（基線） | disabled：content opacity .38、外圈 (197,197,197)；checked：外圈與內點 primary；focus-visible：outline `solid 2px rgb(55,111,208)`、`::before` 36px 狀態層 opacity .12，圈外暈 2037 px、中位 (228,228,228) |
| P40-d | `label.zul` flex 居中列文字垂直置中 | 通過（基線） | flex 列 radio：文字墨水中心 − 圓圈中心 0.0px；checkbox：+1.5px（block 列亦 +1.5px） |
| J47 | 探索 | **已重現** | 垂直時容器 80×480，六個 `.z-fisheye` 寬 1.33px、高 80，全擠在 y 713–793；原因：主題把 `.z-fisheyebar` 設 `display:flex; flex-direction:row; gap 8px; padding 8px 16px; align-items:flex-end`，而 `.z-fisheye` **`position: static`**，JS 的 inline `left/top` 被忽略、`width:80px` 被 flex 壓縮 |
| J61 | 探索 | **已重現** | `<svg>` 無尺寸 → 35×150（寬被按鈕撐滿、高 150 預設），72×72 符號以 35/72 縮放、`stroke-width 8` → 筆畫 3.5–4.5px、字形 bbox 24–33px；框架標準（toolbar.zul 的 Lucide）為 14×14、字形 bbox 11–13px、筆畫 1.5px |

## 二、各 issue 細節

### J4 button（`button.zul`；`red4.js` → `red4.json`、`red4-<變體>-<狀態>.png`、`red4-P-*.png`）

量法：六種 text 變體各取 enabled 那顆；四狀態 = 靜止（滑鼠在 (2,2)）、hover（滑鼠在按鈕中心）、聚焦（先把前一個可聚焦元素 `focus()` 再按 Tab，`:focus-visible` 為 true）、按住（`mouse.down` 不放）。每狀態讀 computed `box-shadow`／`color`／`cursor`／`::before` opacity／rect，並截圖（rect 外擴 14px），以 rect 左右 10–12px 的欄位中位色為頁面底色，取 rect 外 1–4px（及 1–8px）環形像素的最差 ΔE。

| 變體 | 靜止 | hover | 聚焦 | 按住 | 4px 環最差 ΔE（靜止／hover／按住） |
|---|---|---|---|---|---|
| `z-button-text` | none | none | none | none | 0／0／0 |
| `z-button-text-secondary` | resting | resting | resting | resting | 2.79／2.79／2.79（634 px > 1） |
| `z-button-text-success` | resting | resting | resting | resting | 2.79／2.79／2.79 |
| `z-button-text-warning` | resting | resting | resting | resting | 2.79／2.79／2.79 |
| `z-button-text-error` | resting | resting | resting | resting | 2.79／2.79／2.79 |
| `z-button-text-info` | resting | resting | resting | resting | 1.04／1.04／1.04（60 px；底緣被容器裁掉，見第四節） |

「resting」= `rgba(50, 50, 93, 0.024) 0px 2px 5px -1px, rgba(0, 0, 0, 0.05) 0px 1px 3px -1px`。hover 時五個彩色 text 變體**沒有**升到 elevation-2（filled 的 hover 會），只是 resting 一直在。聚焦狀態的 4px 環 ΔE 77.55 是 focus outline（`solid 2px rgb(55,111,208)`）本身，六個變體（含 `z-button-text`）都一樣，與陰影無關——見第四節缺陷 1。

保護項基線（靜止／hover computed `box-shadow`）：filled `resting`／`elevation-2`；`z-button-secondary` 同 filled；`z-button-outlined` `none`／`none`；icon-only（settings）同 filled；`z-button-outlined-secondary` **`resting`／`resting`**。22 顆 disabled 全 `none`。

順帶觀察（不在 #4 範圍，供 Planner 決定）：`z-button-outlined-{secondary,success,warning,error,info}` 五個 outlined 彩色變體靜止與 hover 也帶 resting 陰影，而 `z-button-outlined` 本身是 `none`——與 text 變體同一型的漏重設。

### J6 calendar（`calendar.zul`；`red6.js` → `red6.json`、`red6-*.png`）

頁面把所有 calendar 的 value 固定在 2020-03-15，`constraint="no past"` 以伺服器今天（2026-10-08）為準，所以 Mar 2020 **整個月都 disabled**（設計師截圖也是如此），同頁沒有「同月可選日」可比。為了量混合月，用該 calendar 的「下一月」箭頭導到 Oct 2026（no past：1–7 disabled、8 起可選；no future：1–8 可選、9 起 disabled）。計算欄位：每個 td 的 computed `color`／`text-decoration-line`／`cursor`／`pointer-events`／`opacity`，以及 cell 內縮 3px 的最深像素（因刪除線疊在字上，最深像素比單純合成色更深，但同群內一致：群內最大兩兩 ΔE 0）。選取日（15）排除在群外。

| 截圖 | 群 | 日期 | computed color | 最深像素中位 | 刪除線／cursor／pe |
|---|---|---|---|---|---|
| `red6-nopast-mar2020.png` | disabled 平日 | 2,3,4,5,6,9,…,31（22 天） | `rgba(0,0,0,.38)` | (98,98,98) | line-through／not-allowed／none |
| 同上 | disabled 週末 | 1,7,8,14,15*,21,22,28,29 | `rgba(0,0,0,.87)` | (4,4,4) | 同上 |
| 同上 | disabled 月外 | 4/1–4/4 | 平日 .38、週六 .87 | (195,195,195)，群內 ΔE 13.3 | 同上，opacity .38 |
| `red6-nopast-current.png` | disabled 平日／週末 | 1,2,5,6,7／3,4 | .38／.87 | (98,98,98)／(4,4,4) | 同上 |
| 同上 | enabled 平日／週末 | 8–30／10,11,17,18,24,25,31 | .87／.87 | (33,33,33)／(33,33,33) | none／pointer／auto |
| `red6-nofuture-current.png` | disabled 平日／週末 | 9–30／10,…,31 | .38／.87 | (98,98,98)／(4,4,4) | line-through／not-allowed／none |

跨群：disabled 平日 vs disabled 週末 ΔE **40.46**；disabled 週末 vs enabled 平日 ΔE 11.64（週末 disabled 幾乎和可選日一樣深，只差刪除線）。

其他：`red6-default-mar2020.png` 選取日 Sun 15：computed `color rgb(255,255,255)`，圓盤中位 (55,111,208)。hover：`red6-hover-enabled-weekday.png`（8）／`-weekend.png`（10）探針 (237,237,237)；`red6-hover-disabled-weekday.png`（1）／`-weekend.png`（3）探針 (255,255,255)、`:hover` 不成立（pointer-events none）。表頭七個 th `rgba(0,0,0,.6)`。導航到 Oct 2026 後 ZK 把「15」標成選取（value 的日數），已排除。

### J40 label／radio（`label.zul`、`radiogroup.zul`；`red40.js` → `red40.json`、`red40-label-rows.png`、`red40-radiogroup-states.png`、`red40-radio-focus.png`）

同頁 `label.zul` 兩列（block 列、`z-d-flex z-items-center` 列）各有一個 checkbox 與兩個 radio：

| 列 | 元件 | content font-size／weight／line-height | host rect 高／min-height | input | 文字墨水中心 − 控制墨水中心 |
|---|---|---|---|---|---|
| block | checkbox「By email」 | 13px／400／20px | 40／40px | 18×18 | +1.5px |
| block | radio「Yes」「No」 | **14px**／400／20px | 40／40px | 20×20 | 0.0px |
| flex | checkbox | 13px／400／20px | 40／40px | 18×18 | +1.5px |
| flex | radio | **14px**／400／20px | 40／40px | 20×20 | 0.0px |

（textRect 中心 − input 中心：radio −0.5px、checkbox 0。）`radiogroup.zul` 狀態：off 外圈 20×20 色 (102,102,102)、內點區 13×13 同色（墨水為圈線）；on 外圈與內點 (55,111,208)、內點 13×13；disabled off 外圈 (197,197,197)、content opacity .38；disabled on 以 ΔE≥12 門檻抓不到圈（更淡），content opacity .38；鍵盤聚焦：`:focus-visible` true，input `outline solid 2px rgb(55,111,208)`，`::before` 36px、bg `rgba(0,0,0,.87)`、opacity .12。

### J47 fisheyebar（`fisheyebar.zul`；`red47.js`／`red47b.js`／`probe-fisheye-rules.js` → `red47.json`、`red47b.json`、`probe-fisheye-rules.json`、`red47-*.png`、`red47b-*.png`）

見第三節。

### J61 tbeditor（`portallayout.zul`、`tbeditor.zul`、`toolbar.zul`、`button.zul`、`utility/icons.zul`；`red61.js`／`probe-tbeditor*.js` → `red61.json`、`red61-*.png`、`probe-tbeditor-*.png`）

見第三節。

## 三、J47、J61 探索結果與建議判定

### J47 fisheyebar

**重現（`red47-h.png`、`red47-v.png`、`red47b-base-*.png`）。** `itemWidth/itemHeight=80`、`itemMax=160`、`attachEdge=bottom`。JS（`Fisheyebar.ts syncAttr`）把容器設 inline `width/height`，每個 `.z-fisheye` 設 inline `left/top/width/height`。

| 方向 | 容器 rect（inline） | `.z-fisheye` rect | inline left/top/width/height | rect − (容器原點 + inline) | 在容器內 |
|---|---|---|---|---|---|
| 水平 | 32,321 480×80 | l=48,124,…,428；t=313；**68×80** | 0/80/…/400, 0, 80, 80 | Δl +16…−4、Δt **−8**、Δw **−12** | 否（上緣高出 8px） |
| 水平 magnify（指到第 3 個） | 同上 | 51×80、76.5×120、**102×160**、76.5×120、51×80、51×80 | −80/0/120/280/400/480, 0/−40/−80/−40/0/0 | Δw −29～−58 | 否 |
| 垂直 | 32,321 **80×480** | l=48,57.3,…,94.7；t=**713**；**1.33×80** | 0, 0/80/…/400, 80, 80 | Δl +16…+63、Δt +392…−8、Δw **−78.7** | 是（但全部擠在底部一列） |
| 垂直 magnify | 同上 | 1×80、1.5×120、**2×160**、… | 0/−20/−40/−20/0/0, −80/0/120/280/400/480 | Δw −79～−158 | 是 |

`.z-fisheye-image` 跟著項目縮：水平 54.4×64（inline 80%×80%，置於 flex-end）、垂直 **1.06×64**。

**哪條規則造成（CSSOM 走訪 + 注入實驗，皆未改檔）。** 生效規則只有 `zul/css/zk.wcs` 的 `@layer zk-components`（`probe-fisheye-rules.json`）：

- `.z-fisheyebar { display:flex; flex-direction:row; align-items:flex-end; gap:var(--zk-spacing-2); padding:var(--zk-spacing-2) var(--zk-spacing-4); background-color:transparent }` → computed `gap 8px; padding 8px 16px; position: static`
- `.z-fisheye { display:flex; flex-direction:column; justify-content:flex-end; align-items:center; cursor:pointer; transition: width/height }` → computed **`position: static`**（沒有任何規則給 `position:absolute`）
- `.z-fisheye-image { width:100%; height:100%; object-fit:contain; display:block }`（inline 80% 蓋過，無害）

| 注入 | 垂直 rect − 預期 | 垂直全在容器內 | 水平 rect − 預期 | 結論 |
|---|---|---|---|---|
| 無（今天） | Δw −78.7、Δt 最多 +392 | 是（擠成一列） | Δw −12、Δt −8 | 失敗 |
| E1 `.z-fisheye{position:absolute}` | 位置 = inline，但相對 **viewport**（l=0,t=0…） | 否 | 同 | 容器 static，定位基準錯 |
| **E2 `.z-fisheye{position:absolute} .z-fisheyebar{position:relative}`** | **全部 0**（六項 80×80，l=32、t=321+80k） | **是** | **全部 0**（l=32+80k、t=321） | 唯一讓 rect = JS 版面的組合；magnify 後 rect 也 = inline（見下） |
| E3 `.z-fisheyebar{display:block}` | 寬 80 但 top 被忽略（流式疊放，t=329+80k） | 否 | 疊成一欄 | 不夠 |
| E4 = E3 + E2 | 同 E2 | 是 | 同 E2 | 與 E2 等價（`display` 無關） |
| E5 `.z-fisheyebar{flex-direction:column}` | 高 70.67（被壓）、l=16 | 否 | 疊成一欄 | 不對 |
| E6 `.z-fisheyebar{padding:0;gap:0}` | 寬 13.33 | 是 | Δ0（水平剛好 6×80=480） | 只證明 padding+gap 吃掉 72px；垂直仍錯 |
| E7 `.z-fisheye{flex-shrink:0}` | 寬 80 但橫排（l=48+88k，t=713） | 否 | Δl +16…、Δt −8 | 不對 |

E2 下 magnify（`red47b-E2-h1.png`／`-v.png`、`red47-exp-E2_*-magnify.png`）：rect 與 inline 完全一致（六項 Δ=0），但放大項**本來就超出容器**：水平 第 1 項 left −80（l=−48）、第 3 項 top −80 160×160（t=241 < 321）；垂直 第 3 項 left −40（l=−8）、第 6 項 top 480（b=881 > 801）。這是 ZK 的 magnify 演算法（`itemMax 160` > 容器 80，往外長），不是 CSS 能改的。切回水平（`red47b-E2-h2.png`）inline 與 rect 都正確回復，沒有殘留。另外 `aria-orientation` 在切成垂直後仍是 `"horizontal"`（widget 沒更新），屬 ZK 問題，順帶記錄。

**建議判定（供 Planner 定稿）：**

- J47-1（垂直，今天失敗）：勾「Vertical orient」後，六個 `.z-fisheye` 的 rect 各自 = 容器 rect 原點 + inline `left/top`，寬高 = inline `width/height`（80×80），誤差 ≤ 1px；同時 computed `position` 為 `absolute`、容器 computed `position` 非 `static`。等價地「全部落在容器內且寬 ≥ 80」也可，但「rect = inline」能同時抓到 E1 這種定位基準錯的半套修法。今天：Δw −78.7，失敗。
- J47-2（水平，**不建議做成「與 RED 相同」的保護項**）：今天水平也不對（寬 68、上移 8px），凍結它等於保護錯誤。建議改成與 J47-1 同式：靜止時 rect = inline（80×80、l=32+80k、t=321）；今天 Δw −12、Δt −8，失敗。
- J47-3（magnify）：建議改成「指到第 3 項後，六項 rect 仍 = 容器原點 + inline（80／120／160 的寬高，誤差 ≤ 1px）」，而**不是**「仍在容器內」——後者在 itemMax > 容器尺寸時依 ZK 設計必然失敗。今天水平 Δw −29～−58、垂直 Δw −79～−158，失敗。
- 保護項建議：`.z-fisheye-image` rect = 項目 rect 的 10%/10%/80%/80%（今天水平就不對：高 64 但位於 flex-end）；`.z-fisheye-text` 僅在 magnify 項顯示（今天 `display:none` 由 JS 控制，不受影響）；`cursor: pointer` 不變；勾選再取消後水平 rect 恢復。

### J61 tbeditor

**重現（`probe-tbeditor-portallayout-pane.png`、`red61-portallayout-toolbar.png`）。** 工具列 `.z-tbeditor-button-pane`（portallayout：559×71，flex wrap 成兩列 y=233／268；tbeditor.zul：1214×36 一列）。按鈕 `<button>` 35×35、padding 0、`display:flex; align-items:center; justify-content:center`、bg 透明、radius 8px。每顆圖示都是 **`<svg><use xlink:href="#z-tbeditor-…">`** 引用頁內 sprite 的 `<symbol viewBox="0 0 72 72">`（trumbowyg 圖示：多數為填色 path，`view-html` 與 `fullscreen` 為 `stroke="currentColor" stroke-width="8"` 的線）。**`<svg>` 沒有任何寬高（無屬性、無 CSS）**，computed `width 35px`（被按鈕撐滿）、**`height 150px`**（SVG 預設 300×150 的高）、`overflow hidden`，rect 35×150 上下各溢出按鈕 57.5px；符號以 `xMidYMid meet` 縮成 35×35 置中，所以字形剛好落在按鈕內，但縮放比是 35/72 = 0.486。

| 項目 | tbeditor 今天（20 顆） | 框架標準：`toolbar.zul` toolbarbutton Lucide（10 顆） | `utility/icons.zul` 畫廊（`z-text-3xl`，20 顆對應圖示） |
|---|---|---|---|
| 繪製方式 | `svg<use>` sprite symbol 72×72 | `::before` CSS mask（Lucide svg），`width/height 14px`，`background-color: currentColor` | 同左，24px |
| 圖示盒尺寸 | svg **35×150**，有效繪製區 35×35 | 14×14（元素 14×20） | 24×24 |
| 字形墨水 bbox | 24.5×14.5（undo）～33×26（image）；align-* 26×22；fullscreen 28×28 | 11×13～13×13（search 12×12、edit 13×13） | 20×16（align-*）、20×20（image）、22×22（link） |
| 筆畫粗細（墨水 run 中位，CSS px） | **3–4.5**（核心 2.5–3.5；fullscreen 6） | **1.5**（核心 1） | 2（核心 2） |
| 顏色 | 18 顆 `fill rgba(0,0,0,.6)` → 墨水 (96,98,100)；**`view-html`、`fullscreen` 2 顆走 `currentColor` = `rgb(0,0,0)` → 墨水 (0,0,0)**（同列兩種黑） | `color` 跟 toolbarbutton：primary (55,111,208)；toggle checked 用 `oklch(.23 …)`；disabled (178,199,237) | primary |
| 按鈕命中尺寸 | 35×35 | toolbarbutton 36×36（icon-only） | — |
| 分隔線 | `.z-tbeditor-button-group::before` 1×35px、`rgba(0,0,0,.12)`、margin 0 4px（第一群無） | — | — |
| 群間距 | 0（分隔線含在群內） | — | — |

注入實驗（`red61-exp-X*-toolbar.png`，只改 `<svg>` 尺寸）：

| 注入 `svg{width;height}` | undo 墨水 bbox | align-left bbox | 筆畫（run 中位／核心） | 備註 |
|---|---|---|---|---|
| 今天（35×150） | 24.5×14.5 | 26×22 | 3.5–4.5／3–3.5 | |
| X1 17×17（trumbowyg 原廠值） | 12×7 | 13×11 | 2–2.5／1–1.5 | 與 Lucide 14px 的 bbox（11–13）最接近 |
| X2 14×14 | 9.5×5.5 | 11×9 | 1.5–2／1 | 筆畫與 Lucide 14px 同（1.5／1），但 bbox 比 Lucide 小 2px |
| X3 20×20 | 14×8.5 | 15×12 | 2／1.5–2 | 筆畫與 Lucide 24px 同 |

hover 狀態層本次未量到（`probe-tbeditor-sep.js` 的 hover 探針 `:hover` 未成立，疑為 portallayout 內的 iframe 式編輯器搶事件），保護項若要量 hover 需先在 `tbeditor.zul` 驗證滑鼠事件可達。

**建議判定（最小解讀 R6，供 Planner 定稿）：**

- J61-1（尺寸，今天失敗）：每顆 `.z-tbeditor-button-pane button > svg` 的 rect 寬高相等且在 14–17px（Planner 擇一：14 對齊框架 toolbar icon 盒，17 對齊 trumbowyg 原廠），且 svg rect 完整落在按鈕 rect 內（今天高 150 溢出）。像素佐證：`align-left` 字形 bbox ≤ 13×11（今天 26×22）。
- J61-2（粗細）：`align-left`／`undo`／`strong` 三顆的筆畫 run 中位 ≤ 2px（今天 3.5–4.5）。這是尺寸的函數（stroke 8/72 × 盒寬），J61-1 過了就會過；若 Planner 認為多餘可併入 J61-1。
- J61-3（顏色一致）：20 顆字形墨水中位兩兩 ΔE ≤ 2（今天 `view-html`、`fullscreen` 是純黑 (0,0,0)，其餘 (96,98,100)，ΔE 約 40）；目標色是否改成 `on-surface-variant` 類 token，由 Planner 依原則二決定——今天 `fill rgba(0,0,0,.6)` 其實已是 Marble 的 secondary text 色，與 Lucide toolbarbutton 的 primary 色不同屬「框架 toolbar 用 primary」的另一條線，建議不在本 issue 擴張。
- 保護項：按鈕 rect 35×35 不縮（hit-target）；分隔線 1×35、`rgba(0,0,0,.12)` 不變；兩列換行位置（portallayout 第二列從 `justifyLeft` 開始、y=268）不變；`tbeditor.zul` 單列 pane 高 36 不變；disabled／active 狀態層待 hover 可量後再定。

## 四、方法缺陷或歧義

1. **J4-1 的「外圍 4px 像素與頁面背景一致」在聚焦狀態不可能成立**：focus-visible 的 `outline solid 2px`（primary）就畫在 rect 外 1–4px，六個變體（含今天已通過的 `z-button-text`）在聚焦時 4px 環 ΔE 都是 77.55。建議：聚焦狀態只看 computed `box-shadow: none`，像素環只量靜止／hover／按住；或聚焦時排除與 outline 色 ΔE ≤ 3 的像素。
2. **`z-button-text-info` 與 `z-button-text` 的底緣被容器裁掉**：兩者都是各自 grid（`z-overflow-x-auto`，隱含 overflow-y auto）的最後一列，陰影與 focus ring 的下半邊被裁，所以 info 的 4px 環只剩左右側的 ΔE 1.04、`z-button-text` 聚焦底側為 0。修改後若用同一頁，info 的像素判定要以左／右／上三側為準，或改用 computed。
3. **J4 的 hover 不是「升 elevation」而是「resting 一直在」**：條文寫 text 變體「吃到 `--zk-button-elevation`」，實測五個彩色 text 變體 hover 時仍是 resting（不像 filled 升到 elevation-2），修法只需重設靜止；但 P4-a 同時記到 `z-button-outlined-*` 五個彩色 outlined 也帶 resting（`z-button-outlined` 本身沒有），是否一併納入 #4 由 Planner 決定。
4. **「fab 變體」在 `button.zul` 不存在**，P4-a 的 fab 無法量；若 Marble 有 fab class，需指定頁面。
5. **J6 的同頁「可選週末日」在 Mar 2020 不存在**（`no past` 以真實今天為準，整月 disabled；`no future` 整月可選）。本次用「下一月」箭頭導到 Oct 2026 取混合月；判定文字建議明寫「導航到瀏覽器當月」，否則 P6-a／P6-e／P6-f 在設計師的那個月量不到。另：導航後 ZK 會把當月「15」標為選取（value 的日數），群統計要排除選取日。
6. **J6 的「文字像素最深色」受刪除線疊字影響**：disabled 字的最深像素（平日 (98,98,98)、週末 (4,4,4)）比單純 alpha 合成（158／33）更深，因為 `line-through` 疊在字上。群內一致所以可用於「同色」判定，但不要拿它和可選日的最深色（33）直接比「差值」。
7. **J40 的 P40-b「文字與圓圈垂直中心差 RED 值 ≤ 現值 + 0」**：今天差值已是 0.0px（墨水中心）／−0.5px（textRect 中心），13px 字的墨水高從 11 變約 10，中心仍應 0；建議寫成「|差| ≤ 0.5px」而非「≤ RED + 0」，避免浮點 0.25 的誤判。P40-d 的 checkbox 那一欄今天是 +1.5px（checkbox 不在 #40 修改範圍，只記基線）。
8. **J47 原訂 J47-2「水平與 RED 相同」會凍結錯誤**：今天水平也是 flex 排版（寬 68、上移 8px、magnify 寬 51/76.5/102），正確修法（E2）會同時改變水平——建議改成 rect = inline（見第三節）。J47-3「magnify 後仍在容器內」依 ZK 設計不可能（itemMax 160 > 容器 80），建議改成 rect = inline。
9. **J47 實驗腳本第一版的「切回水平」沒有生效**（`aria-orientation` 永遠是 `horizontal`，第一版拿它判斷狀態）；已改用 checkbox 的 `checked`，`red47.json` 為修正後重跑結果，`red47b.js` 另以全新頁面驗證切換順序，兩者一致。
10. **J61 的「標準圖示尺寸」有兩個候選**：框架 toolbarbutton 的 Lucide 盒是 14×14（字形 11–13px、筆畫 1.5），畫廊是 24px（`z-text-3xl`，非預設）。trumbowyg 原廠 svg 為 17px，注入 17 的字形 bbox（12–13）最接近 Lucide 14 盒的字形。J61-1 的目標值（14 或 17）請 Planner 定。另 `fill rgba(0,0,0,.6)` vs toolbarbutton 的 primary 是顏色體系差異，建議不擴張。
11. **J61 的 hover／active／disabled 狀態層本次未量到**（portallayout 內 `:hover` 未成立），保護項若需要，Generator 後的量測要在 `tbeditor.zul` 上先確認事件可達。
12. 本報告的 `probe-dom.json`、`probe-fisheye-rules.json` 含 CSSOM／computed 讀值，只用於 J47／J61 找原因，J4／J6／J40 的判定值全部來自 computed style、rect 與像素。

GATE10-RED: DONE
