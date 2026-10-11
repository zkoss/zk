# Gate：第六批 RED run，combobutton／slider 系列／rating／inputgroup（#18 #19 #22 #24 #25 #26 #27）

- **使用 8085**（共用的 zkpreview，本次沒有 kill、沒有重啟、沒有開第二個 port；整個 run 期間沒有中斷）。
- **日期：** 2026-10-08，Verifier，量現在的程式碼（Playwright 1.59.1、Chromium 147.0.7727.15、viewport 1280×900、deviceScaleFactor 2，已注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`，量測前游標移到 (2,2)；每個狀態都在重新載入的頁面上量）。
- **方法：** jess-review-verification-plan.md（過程資料已清除） 第六批一節 + 共用規則。ΔE 一律 **CIE76**（lib.js）。
- **腳本與原始輸出：** `gates/batch6-red/`：`lib.js`（複製自 batch5-red：幾何、截圖解碼、ΔE）、`probe-dom.js/.json` + `probe-*.png`（六頁 DOM 形狀探針）、`red18.js/.json` + `red18-*.png`、`probe18-active.js/.json` + `probe18-*.png`（#18 的 :active／focus 分離探針）、`red19.js/.json` + `red19-*.png`、`red22.js/.json` + `red22-g*.png`、`red24.js` + `red24-{multislider,rangeslider,slider}.json`（每頁分開跑，檔內是 `pages.<page>.widgets[]`，每點含座標、cursor、命中元素、是否移動）+ `red24-*.png`、`red25.js/.json` + `red25-s*-*.png`、`red26.js/.json` + `red26-*.png`、`red27.js/.json` + `red27-r*-*.png`、`ref/jess-*.png`（Jess 的 issue 截圖）。
- **沒有修改**任何 theme／CSS／TS／Java／預覽頁檔案，沒有看 diff，沒有 commit；沒有在頁內注入任何樣式（只有 lib.js 的停動畫規則）。
- **選取 widget 的方式：** DOM id 是 uuid，一律用 class + 在頁面上的順序（`document.querySelectorAll('.z-xxx')[i]`），widget 屬性用 `zk.$(n)._disabled` 等讀。
- **讀原始碼的地方（只為了解釋現象，不是判定依據）：** `zul/.../Slider.ts` 的 `_startDrag`／`_fixPos`（slidetip 預設 `_slidingtext='{0}'`、`position(btn,'before_start'|'end_before')`）；`zkcml/zkex/.../Rangeslider.ts`、`zkmax/.../Multislider.ts` 的 `doMouseDown_`（只有 `domTarget` 是 `-track`／`-mark-dot`／`-mark-label`／`z-sliderbuttons-area` 時才會移動 thumb）。

## 結論摘要

| 項目 | 判定檢查今天 | 保護項今天 | 方法問題 |
|---|---|---|---|
| #18 J18-1 filled，hover 箭頭 | 箭頭環帶 ΔE **0**、標籤 ΔE 6.88 → **失敗，RED-correct**（今天亮的是標籤） | — | 無 |
| #18 J18-2 toolbar，hover 箭頭 | 箭頭環帶 ΔE 3.99 **且**標籤 ΔE 4.16 → **失敗，RED-correct**（兩段一起變） | — | 無 |
| #18 J18-3 filled，箭頭上按住 | 箭頭 ΔE 0、標籤 ΔE 10.37 → **失敗，RED-correct**；探針證實是 `:active`（blur 後仍亮、`:focus-visible` false） | — | 無 |
| #18 保護項 (a)(b) | — | **filled 通過**（hover 標籤：標籤 6.88、箭頭 0；按住標籤：標籤 10.37、箭頭 0）；**toolbar 今天失敗**（hover 標籤：標籤 4.16 **但箭頭 3.99**；按住：6.25／5.98） | **METHOD-DEFECT（範圍）**：toolbar 的狀態層蓋住整顆（箭頭透明），(a)(b)「箭頭 ΔE ≤ 1」在 toolbar 今天不成立 |
| #18 保護項 (c)(d)(e)(f) | — | (c) Tab 聚焦：filled 標籤 10.37、toolbar 6.25（toolbar 箭頭環帶 27.63 = 外框焦點環）✓；(d) open 狀態記錄：filled 標籤 (79,128,213)／箭頭不變，toolbar 標籤 (237,237,237)；(e) 停用 hover 兩段 ΔE 0 ✓；(f) 幾何：content 116.11×36、button 32×36、分隔線 1px，各狀態相同 ✓ | 無 |
| #19 J19-1 停用 filled | 箭頭角落 (196,196,196) vs 標籤角落 (224,224,224) → ΔE **10.02**，**失敗，RED-correct**；原因 = 半透明 token 疊兩層（見下） | — | 無 |
| #19 J19-2 停用 toolbar | 兩段都 (255,255,255)，ΔE **0 → 今天就通過** | → 應改為保護項 | **METHOD-DEFECT（歸類）** |
| #19 保護項 | — | 啟用 filled 兩段 (55,111,208)／ΔE 0、啟用 toolbar 兩段白／ΔE 0 ✓；停用文字 (139,139,139)、圖示 (121,121,121)、對比 2.58（記錄基準） | 無 |
| #22 J22-1 接縫 1px | 11 個水平 + 1 個垂直 inputgroup：**所有接縫與外框都是 1px**（中線、t+5、b-5 三條線一致） | — | 「同色 ΔE ≤ 2」：同一條半透明邊框疊在白底 (196,196,196) 與 addon 底 (190,191,193) 上 ΔE **2.16／2.45**（臨界）；停用 textbox 的邊框 (230,230,231) vs 190 → ΔE **14.02**（見下） | **說明／待裁**：寬度已涵蓋；顏色兩個發現要 Planner 決定是否屬 #22 |
| #22 J22-2 外框 1px | 全部 1px ✓（Input + Button 那組：button 無邊框，textbox 右框被 button 蓋掉，記錄） | — | 無 |
| #24 J24-1 不會動的點游標 default | multislider 3 組：441 點，**不會動 392 點（非 thumb 點的 99%）全部 `pointer`**；其他 multislider 93–96%、rangeslider 73–87%，全部 `pointer` → **失敗，RED-correct**；「會動的點」確實存在（4–94 點） | — | 說明：mark label 一個點都沒抽到（在外框之外）；「thumb 上」用 rect 定義會把圓角外的點算進去（游標 pointer） |
| #24 J24-2 multislider hover 環 | 停在 thumb A：**6 顆環帶全部 ΔE 5.03–5.54**；停在軌道：6 顆 8.17–8.48 → **失敗，RED-correct** | — | 無 |
| #24 J24-3 rangeslider hover 環 | 停在 thumb A：兩顆 5.24／5.54；停在軌道：兩顆 8.48 → **失敗，RED-correct** | — | 無 |
| #24 保護項 | — | (a) thumb 中心 `grab`、按住 `grabbing` ✓；(b) 每顆 thumb 中心命中自己 ✓（含 3 組 6 顆）；(c) 拖 40px thumb 與 area 都更新 ✓；(d) 停用：所有點 `auto`（元素是外層 flex div，widget 本身 `pointer-events:none`），記錄；(e) plain slider：**整個外框 198/198 點點了都會動**、游標 `pointer`、hover 只亮單顆（thumb 10.64、軌道 hover 時環帶 ≤ 1.02）→ 不改，與計畫一致 | 無 |
| #25 J25-1 水平提示框中心 | 提示框左緣 = thumb 左緣（+0.24），中心差 = **(提示寬 − 20)/2**：「5」1.50、「6」1.65、「50」4.97、「100」7.41 → **失敗，RED-correct，且偏差取決於提示寬度** | — | 固定 px 補不了（計畫已預見） |
| #25 J25-2 垂直提示框中心 | 提示框頂 = thumb 頂、左緣 = thumb 右緣：垂直中心差 **4.0**（= (28 − 20)/2，與寬度無關）、水平差 21.5–27.4 → **失敗，RED-correct** | — | 無 |
| #25 J25-3 文字在框內置中 | Range ink 中心差 **0／0**、像素 ink 差 ≤ 0.18 → **今天就通過** | → 應改為保護項 | **METHOD-DEFECT（歸類）** |
| #25 保護項 | — | 框與 thumb 不重疊（水平 gap 0px，垂直在右側）✓；在 viewport 內 ✓；跟隨 thumb（4 個位置都貼齊）✓；文字 `rgb(240,244,250)` 11px、padding 4px 8px、line-height 20px（記錄）；放開後 `#zul_slidetip` 不存在 ✓ | 說明：預覽頁不需要 `slidingtext`，Slider 預設 `'{0}'` 拖曳就顯示提示 |
| #26 J26-1 knob 輸入框靜止 | 內部帶 (240,244,250) vs 外側白 ΔE **5.18**；邊緣 1px (184,187,192) vs 外側 ΔE **24.39** → **失敗，RED-correct** | — | 無 |
| #26 J26-2 聚焦指示 | 有指示：邊緣 (0.9,95.6,204.2) = **瀏覽器 UA 預設 outline `1px auto rgb(0,95,204)`**，不是 theme 的；textbox 聚焦環 = `border 2px rgb(55,111,208)`，ΔE **10.13–10.50 > 3** → **失敗**；對比 5.94 ≥ 3 ✓ | — | **說明**：計畫只寫「已通過→保護項／沒有指示→列入修法」，今天是第三種（有指示但是 UA 色） |
| #26 保護項 | — | 文字 `rgb(55,111,208)`／600／50.6667px／center／Arial（記錄基準）；輸入框 95×76 @ (85,852)；打 80 + Enter → curpos 80、弧線 path 改變、rect 不變 ✓；forced-colors 邊框黑 ΔE 100 ✓ | 無 |
| #27 J27-1 readonly／disabled 游標 | 水平 disabled／readonly、垂直 disabled／readonly 4 顆：星星中心、間隙、外層內點 **全部 `pointer`**（元素都是 `DIV.z-rating`）→ **失敗，RED-correct** | — | 無 |
| #27 保護項 | — | (a) 可互動星星中心 `pointer` ✓；(b) hover 第 4 顆：第 1–4 顆 `scale(1.1)`、第 4 顆變藍 ΔE 19.02；readonly／disabled 無 transform、5 顆 ΔE 0 ✓；(c) 點第 4 顆 3→4 ✓（readonly／disabled 3→3）；(d) 可互動間隙游標 `pointer`（元素 `DIV.z-rating`，記錄） | 無 |

## #18 — combobutton hover／active 落點

DOM：`span.z-combobutton > span.z-combobutton-content > span.z-combobutton-text + span.z-combobutton-button(absolute, 32px) > i.z-combobutton-icon`。矩形（viewport px）：filled content 130.17–246.28 × 183–219、button 214.28–246.28；toolbar content 131.17–247.28 × 228–264、button 215.28–247.28；分隔線 = button 的 `border-left 1px`。

取樣：**箭頭環帶** = button 矩形內距邊 ≤ 3px 的像素，去掉分隔線那一欄（左 1.5px）與右側兩個 4×4 圓角（1332 px）；**標籤** = content 去掉 button、內縮 2px、去掉 text span 矩形（+1px）與左側圓角（5584 px）。平均色 ΔE：

| 狀態（各自重新載入） | filled 箭頭環帶 | filled 標籤 | toolbar 箭頭環帶 | toolbar 標籤 | 備註 |
|---|---|---|---|---|---|
| rest | (63.4,117.1,210) | (55,111,208) | (252.5,252.5,252.5) | (255,255,255) | toolbar 環帶含外框線 |
| hover 箭頭中心 | **0** | **6.88** → (70,122,211) | **3.99** → 241 | **4.16** → 243 | J18-1／J18-2 失敗 |
| hover 標籤中心 | 0 | 6.88 | 3.99 | 4.16 | 保護項 (a)：filled ✓、toolbar ✗ |
| 箭頭上按住（:active） | **0** | **10.37** → (79,128,213) | 5.98 | 6.25 | J18-3 失敗 |
| 標籤上按住 | 0 | 10.37 | 5.98 | 6.25 | 保護項 (b)：filled ✓、toolbar ✗ |
| Tab 聚焦（游標在 (2,2)） | 4.12 | 10.37 | 27.63 | 6.25 | 保護項 (c) ✓；toolbar 的 27.63 是外框焦點環落在環帶裡 |
| open（點箭頭後游標移開） | 0.04 | 10.37 | 5.98 | 6.25 | 保護項 (d) 記錄 |
| 停用 hover 箭頭／標籤 | 0／0 | 0／0 | 0／0 | 0／0 | 保護項 (e) ✓ |

- **今天哪邊亮：** filled hover 時標籤變亮（luminance 106.1 → 117.4，+11），箭頭不變（112.4）；按住時標籤 123.7。toolbar hover 時兩段一起變暗（255 → 243、252.5 → 241）。
- **`:active` 與 focus 分離（`probe18-active.json`）：** 箭頭上按住時 `:active` true、`:focus-visible` false；在按住狀態下 `blur()`，標籤仍是 (79,128,213)（ΔE 10.37 不變）→ 按住時的標籤變色來自 `:active`，不是焦點，J18-3 的「標籤 ΔE ≤ 1」在修正後是可達成的。放開後（open + hover）標籤回到 hover 色 (70,122,211)。
- **toolbar 的保護項 (a)(b) 今天不成立**：toolbar 兩段都透明，content 的狀態層（`::before`）蓋住整顆，所以 hover／按住標籤時箭頭矩形也變（3.99／5.98 > 1）。這不是量法問題，是 (a)(b) 沒有限定 mold。Planner 要決定：(a)(b) 只限 filled，或 toolbar 的「hover 標籤時箭頭不變」也列為本批判定（今天失敗）。
- 幾何（保護項 f）：所有狀態 content／button 矩形完全相同（`geomSame` true）。

## #19 — 停用 combobutton 的箭頭底色

角落取樣 = 每個矩形四角內縮 5px 的 3×3 區塊（箭頭的左側角落從分隔線再內縮 5px）：

| combobutton | 箭頭四角 | 標籤四角 | ΔE | 前景（文字／圖示最深像素） | 文字對標籤底對比 |
|---|---|---|---|---|---|
| filled 啟用 | 4 角都 (55,111,208) | 4 角都 (55,111,208) | 0 | 白 | 4.83 |
| **filled 停用** | 4 角都 **(196,196,196)** | 4 角都 **(224,224,224)** | **10.02** | (139,139,139)／(121,121,121) | 2.58／2.50 |
| toolbar 啟用 | 白 | 白 | 0 | (102,102,102) | 5.74 |
| toolbar 停用 | 白 | 白 | 0 | (158,158,158) | 2.68 |

**為什麼箭頭比較深（計畫要 RED 驗的預測，成立）：** `--zk-color-disabled-container` = `#0000001f`（12% 黑，**半透明**）。停用時 `.z-combobutton-content` 與 `.z-combobutton-button` 的 computed `background-color` **都是 `rgba(0,0,0,0.12)`**，button 是 content 的子元素、`position:absolute` 疊在上面，所以同一個半透明色塗了兩層：標籤 = 0.12 黑疊白 = 224（量到 224）；箭頭 = 0.12 黑再疊在 224 上 = 預測 197（量到 **196**）。分隔線本身是 `rgba(0,0,0,0)`（量到分隔線欄 196 = 透出下層）。toolbar 停用兩段都是 `rgba(0,0,0,0)`，所以今天 J19-2 就通過。與 Jess 截圖（`ref/jess-19-current.png`）一致。

## #22 — inputgroup 接縫（只量）

`red22.js`：每組 inputgroup 沿垂直中線（水平組）掃一條 device row，另掃 t+5／b-5 當交叉驗證；外框用 x=l+6／r-6 的垂直線。邊框像素 = 與白底和 addon 底 (247,249,252) 的 ΔE 都 ≥ 6。中線穿過字形的 run 標 `glyph?` 不計。

| 組（頁面順序） | 接縫位置 → 寬 → 色 | 外框（左／右／上／下） | 與外框起點 ΔE |
|---|---|---|---|
| G0 `@` + textbox | 70 → 1px → (190,191,193) | 1／1／1／1 px | 接縫 0；右框 (196,196,196) **2.16** |
| G1 textbox + `@example.com`（Jess 紅框） | 205 → 1px → (196,196,196) | 1／1／1／1 | 0；右框 (190,191,194) **2.45** |
| G2 `$` + textbox + `.00`（Jess 紅／藍框） | 65 → 1px → 190；238 → 1px → 196 | 1／1／1／1 | 0／**2.16**；右框 0.54 |
| G3 `$` + intbox + `.00` | 65／174 → 1px | 1／1／1／1 | 0／2.16 |
| G4 decimalbox + `kg`、G5 doublebox + `m` | 141 → 1px → 196 | 1／1／1／1 | 右框 2.45 |
| G6 `#` + longbox | 65 → 1px → 190 | 1／1／1／1 | 右框 2.16 |
| G7 `With textarea` + textarea（80px 高） | 139 → 1px → 190 | 1／1／1／1 | 右框 2.16 |
| G8 textbox + primary button | 205 起直接是 button 填色 (55,111,208)，**沒有邊框線**（button 無邊框，textbox 右框被蓋住） | 左 1／上 1／下 1；右側是 button | — |
| G9 `@` + **disabled** textbox | 70 → 1px → 190 | 1／1／1／1 | 右框 **(230,230,231)，ΔE 14.02** |
| G10 `$` + **disabled** textbox + `.00` | 301 → 1px → 190；474 → 1px → **(230,230,231)，ΔE 14.02** | 1／1／1／1 | 右框 0.54 |
| G11 vertical：`Username` / textbox / `@example.com` | 751 → 1px → 190；791 → 1px → 196（2.16） | 1／1／1／1 | — |

**結論：** 寬度 **已涵蓋**（Jess 截圖的兩組 G1／G2 所有接縫與外框都是 1px，三條掃描線一致）。顏色兩個發現要 Planner 裁決：
1. 同一條邊框 `rgba(0,0,0,0.23)` 疊在白底是 196、疊在 addon 底 (247,249,252) 是 (190,191,193)，ΔE 2.16／2.45 **剛好超過** J22-1 的 ≤ 2。這是半透明邊框的必然結果（每一組都有），不是寬度或規則差異；若要照字面判，今天每組都「失敗」。建議門檻放寬到 ≤ 3 或改比「同一條線上相鄰兩段」。
2. 停用 textbox 的邊框是 (230,230,231)（比 addon 的 190 淡很多，ΔE 14），所以「@ + disabled textbox」整組外框是兩種顏色（左段 190、右段 230）、「$ + disabled + .00」的第二道接縫 230。這可能是停用 outline token 的既定設計；Jess 的截圖沒有停用組，是否算 #22 由 Planner 決定。

## #24 — multislider／rangeslider 游標與 hover 環

**格點：** 每個 widget 以外框（root ∪ inner ∪ track）內縮 2px、8px 間距取格點，另加軌道中線每 8px 一點、外框頂／底（或左／右）內 2px 每 8px 一點。每一點：讀 `elementFromPoint` 的 computed cursor，然後**在重新載入的頁面**點一下（`mouse.click`）、等 250ms，比對所有 thumb 的 `style.left/top` 與 rect。分類：thumb 上（點在某 thumb 的 bounding rect 內）／會動／不會動。6 個 worker 並行，每點都是新載入。

| widget | 點數 | thumb 上（游標） | 會動（位置） | **不會動** | 不會動的游標 | 不會動佔非 thumb 點 |
|---|---|---|---|---|---|---|
| multislider 水平 3 組（10–70／20–50／30–40） | 441 | 45（全 `grab`） | **4**（只有 x 193–217，= 最上層那組 30–40% 的 `z-sliderbuttons-area`） | **392** | `pointer` 392 | **99%** |
| multislider 垂直 2 組 | 765 | 39（35 `grab`、4 `pointer` = rect 四角圓外） | 27（y 240–344） | 699 | `pointer` 686、`auto` 12（y=656 > 外框底 650，落在外層 flex div）、`grab` 1 | 96% |
| multislider markScale=10 | 441 | 16 | 23（x 145–321） | 402 | `pointer` 402 | 95% |
| multislider 文字 marks | 441 | 12 | 29（x 169–393） | 400 | `pointer` 400 | 93% |
| multislider tooltipVisible | 441 | 12 | 23（x 193–369） | 406 | `pointer` 406 | 95% |
| rangeslider 預設 10–90 | 378 | 30（22 `grab`、8 `pointer` 圓角外） | 94（x 97–466） | 254 | `pointer` 254 | 73% |
| rangeslider markScale=20 | 378 | 25 | 70（x 145–418） | 283 | `pointer` 283 | 80% |
| rangeslider 垂直 ×2 | 378 | 25 | 70（y 263–536） | 283 | `pointer` 283 | 80% |
| rangeslider 文字 marks | 378 | 20 | 58（x 169–394） | 300 | `pointer` 300 | 84% |
| rangeslider tooltipVisible | 378 | 20 | 46（x 193–370） | 312 | `pointer` 312 | 87% |
| **plain slider ×5**（記錄） | 207／135 | 9（`grab`） | **198／126 = 全部非 thumb 點** | **0** | — | 0% |

- **會動的點全部是 `DIV.z-sliderbuttons-area`**（兩顆 thumb 之間的填色帶）；軌道的裸露部分（range 之外）命中的是 `DIV.z-sliderbuttons`（包覆層）或 root，`doMouseDown_` 只認 `-track`／`-mark-*`／`sliderbuttons-area`，所以不動。3 組的 multislider 更極端：下面兩組的 area 被上面那組的 `z-sliderbuttons` 包覆層蓋住，**只有最上層那組 30–40% 的 50px 可點**。
- **mark label 一個點都沒抽到**（`points on mark elements 0`）：mark label 畫在外框之外（下方），不在「widget 外框內」的格點裡；但 `doMouseDown_` 會對 `-mark-label` 做移動，所以它們是「會動但沒量到」的點。方法要補「每個 mark label 中心」。
- **停用（保護項 d，最終比對用）：** multislider 停用 441 點、rangeslider 停用 378 點，全部 `auto`；thumb 中心、軌道點、padding 點、hover 都命中外層 `DIV.z-d-flex.z-flex-col`（停用 widget `pointer-events:none`），root 計算值 `not-allowed` 使用者看不到。
- **hover 環（半徑 10–18px 環帶，與 rest 比）：**

| | 停在 thumb A（第一顆 start-btn）| 停在軌道（離所有 thumb ≥ 30px） |
|---|---|---|
| multislider 3 組（6 顆） | **[5.54, 5.24, 5.03, 5.06, 5.06, 5.06]** | [8.48, 8.48, 8.17, 8.19, 8.19, 8.19] |
| multislider 垂直（4 顆） | [5.26, 5.27, 4.97, 4.92] | [8.51, 8.51, 8.08, 8.06] |
| rangeslider 預設（2 顆） | [5.54, 5.24] | [8.48, 8.48] |
| rangeslider 垂直 | [5.19, 5.16]／[5.27, 5.23] | [8.37, 8.37]／[8.52, 8.47] |
| plain slider（1 顆） | [10.64]（水平三個）／[10.64, 10.59]（垂直） | [1.02]／[0.74]／[1.02]／[0]／[0] |

→ J24-2／J24-3：hover 單顆時**所有** thumb 的環帶都變（≥ 2），而且停在軌道上變得更多（8.5 > 5.5；被 hover 的那顆沒有比別顆亮）；plain slider 只亮單顆。
- 保護項 (a)(b)(c)：每顆 thumb 中心 `elementFromPoint` 都是自己（6 顆都 ok）、cursor `grab`，按住 `grabbing`（`body` cursor 仍 `auto`）；拖 40px 後 thumb `left` 10%→18%、area rect 跟著變（所有啟用 widget 都通過）。
- 記錄（不在本批）：垂直 multislider 的 track 350–850 比 root 338–838 **低 12px**（root padding `0 12px`，track 超出外框底部 12px），所以 y 650–662 那排點落在外層 div。

## #25 — slidetip 對齊

`slider.zul` 沒有 `slidingtext` 屬性的 slider，但 `Slider._slidingtext` 預設 `'{0}'`，拖曳就有 `#zul_slidetip.z-slider-popup` → **不缺實例**。每個 slider：thumb 中心按住、移 10px（不放開）量一次，再移到 value 5／50／100 各量一次，最後放開。提示框 28px 高、padding 4px 8px、11px 字、底 (45,55,72)、文字 (240,244,250)。

| slider | 提示框 vs thumb | dx（中心差） | dy（中心差） | 文字 ink 中心差（Range／像素） | 重疊 | 放開後 |
|---|---|---|---|---|---|---|
| 水平 default／sphere／scale（三個數字相同） | 左緣 = thumb 左緣（107 vs 106.77）、底 = thumb 頂（gap 0） | 「6」**1.65**（寬 22.83）、「5」**1.50**（22.53）、「50」**4.97**（29.47）、「100」**7.41**（34.36） | −24（固定） | 0／0 ；像素 −0.16～0.02／0 | 否 | tip 移除、curpos 100 |
| 垂直 default／sphere | 左緣 = thumb 右緣（131 vs 130.77）、頂 = thumb 頂 | 21.5～27.41（在右側） | **4.0**（固定，= (28−20)/2） | 0／0 | 否 | tip 移除 |

- **水平偏差 = (提示寬 − thumb 寬 20)/2**，隨數字位數變（1 位 1.5、2 位 5.0、3 位 7.4）；JS 用 `before_start`（提示左緣對 thumb 左緣）定位，**固定 px 補不了**，要用與自身寬度有關的位移（例如 `translateX(calc(-50% + 10px))`）才能純 CSS 做到。這正是計畫的「注意」條款；是否可行要 Planner 確認後再給 Generator。
- **垂直偏差 4.0 固定**（提示高固定 28）：純 CSS 可補。
- J25-3 文字置中今天 0／0 → 應為保護項。

## #26 — knob mold 的數字輸入框

knob slider 的 `.z-slider-input`（`<input type="number">`，95×76，inline style 定位，`font-size 50.6667px`）：

| 狀態 | 內部帶（距邊 4–10px） | 邊緣 1px | 邊內 1–3px | 外側 2–5px | ΔE 內部 vs 外 | ΔE 邊緣 vs 外 | 計算值 |
|---|---|---|---|---|---|---|---|
| rest | (240,244,250) | (184,187,192) | (232,236,242) | (255,255,255) | **5.18** | **24.39** | `background rgb(240,244,250)`、`border 1px solid rgba(0,0,0,0.23)`、`outline none`、radius 4px |
| focus（點輸入框、游標移開） | (240,244,250) | **(0.9,95.6,204.2)** | (87.9,149.6,220.9) | 白 | 5.18 | 87.32 | `outline 1px auto rgb(0,95,204)`（**瀏覽器 UA 預設焦點環**）、border 不變 |
| forced-colors | 白 | 黑 | — | 白 | — | 100 | 邊框 `rgb(0,0,0)`，可辨認 |

- **一般 textbox 聚焦環（`textbox.zul` 第一個 Default）：** rest `border 1px rgba(0,0,0,0.23)`（邊緣像素 (202,202,202)）；focus `border 2px rgb(55,111,208)`、outline none，邊緣像素中位 (55,111,208)。`--zk-color-primary` = `#376fd0`。
- **J26-2：** 今天「有」聚焦指示，但它是 Chromium 的 `-webkit-focus-ring-color`（`1px auto rgb(0,95,204)`），不是 theme 的 primary 環：(0,95,204) vs (55,111,208) ΔE **10.50**（邊緣平均 10.13）> 3 → 失敗；對比 5.94 ≥ 3 通過。計畫寫的兩個分支（已通過→保護項／沒有指示→列入修法）都不是今天的情況，Planner 要指定：這項是判定（修成 textbox 的環色）還是只記錄。
- 保護項：文字 `color rgb(55,111,208)`、`font-weight 600`、`50.6667px`、`text-align center`、`font-family Arial`（基準）；輸入框 rect 85–180 × 852–928（文件座標；捲動後 641–717）；Cmd+A、打 `80`、Enter → `value 80`、`curpos 80`、藍色弧線 `path d` 從終點 (44.16,23.14) 變成 (190.35,129.36)、rect 不變。

## #27 — rating 停用／唯讀游標

`rating.zul` 共 11 顆 rating：States 列（Default／Disabled／Readonly 各 3 星）、Max 3／5／10、bolt／gift、Vertical（Default／Disabled／Readonly）→ **readonly 與 disabled、水平與垂直都有實例**。每顆 rating 量：每顆星中心、每個星間隙中點、外層 (l+1,t+1)、(r−1,b−1)、中心。

| rating | 星中心 | 間隙 | 外層內點 | 命中的元素 |
|---|---|---|---|---|
| 水平 Disabled（#1）、Readonly（#2） | `pointer` ×5 | `pointer` ×4 | `pointer` ×3 | 全部 `DIV.z-rating`（星星 `pointer-events:none`，`cursor:default` 看不到） |
| 垂直 Disabled（#9）、Readonly（#10） | `pointer` ×5 | `pointer` ×4 | `pointer` ×3 | 同上 |
| 可互動 7 顆（#0 #3–#8） | `pointer`（`I.z-rating-icon`） | `pointer`（`DIV.z-rating`，記錄） | `pointer` | — |

- 與計畫對原始碼的描述一致：`.z-rating-disabled`／`-readonly` 的 `<i>` 是 `cursor:default; pointer-events:none`，所以 `elementFromPoint` 永遠是外層 `.z-rating`（`cursor:pointer`）。
- 保護項：hover 第 4 顆（可互動 #0）：第 1–4 顆 `transform matrix(1.1,…)`、`z-rating-hover`，第 4 顆由 `rgba(0,0,0,0.23)` 變 `rgb(55,111,208)`（像素 ΔE 19.02；1–3 顆 3.68 = 放大）；disabled／readonly hover 無 transform、5 顆 ΔE 0。點第 4 顆：#0 3→4（mask `11110`）、垂直 #8 3→2（垂直由下往上數，點第 4 顆 = 第 2 顆）；disabled／readonly 3→3。

## 方法問題清單

1. **#18 保護項 (a)(b) 在 toolbar mold 今天不成立**：hover／按住標籤時箭頭環帶 ΔE 3.99／5.98（toolbar 兩段透明，content 的狀態層蓋到箭頭）。**METHOD-DEFECT（範圍）。** 建議 Planner 二選一：(a)(b) 明寫只限 filled；或把「toolbar hover 標籤時箭頭 ΔE ≤ 1」列為本批判定（今天失敗，與 J18-2 對稱）。
2. **#19 J19-2 今天就通過**（toolbar 停用兩段都是透明 → 白／白，ΔE 0）。**METHOD-DEFECT（歸類）。** 建議改為保護項「停用 toolbar 兩段仍同色（ΔE ≤ 1）」。
3. **#25 J25-3 今天就通過**（Range ink 中心差 0／0，像素 ≤ 0.18）。**METHOD-DEFECT（歸類）。** 建議改為保護項「修正後文字仍置中（≤ 1px）」。另：水平偏差 = (提示寬 − 20)/2，取決於寬度；純 CSS 要用自身寬度相關的位移（百分比 transform）才可能，請 Planner 依「注意」條款先裁定可行性再開 Generator brief。
4. **#26 J26-2 今天是第三種情況**：有聚焦指示，但是 UA 預設環 `1px auto rgb(0,95,204)`，與 textbox 的 primary 2px 邊框 ΔE 10.5。**說明／待裁。** 建議明寫 J26-2 為判定（聚焦時環色 = textbox 聚焦環，ΔE ≤ 3），今天失敗。
5. **#24「thumb 上」用 bounding rect 定義**：thumb 是圓的，rect 四角的點命中 root／area、游標 `pointer`（rangeslider 預設 8/30、垂直 multislider 4/39）。**說明。** 建議「thumb 上」= 距 thumb 中心 ≤ 10px。
6. **#24 mark label 沒被抽到**：mark label 在外框之外，格點（外框內）一個都沒命中；但 widget 原始碼對 `-mark-label` 會移動 thumb。**說明。** 建議格點另加「每個 mark label 中心」（會動 → 游標可 `pointer`）。另外垂直 multislider 的 track 超出 root 底 12px，那排點命中外層 div（`auto`），建議格點區域明寫為 root ∪ track。
7. **#22 J22-1「同色 ΔE ≤ 2」** 被半透明邊框疊不同底色的 2.16／2.45 卡住（每組都有），停用 textbox 的 230 vs 190（ΔE 14）則是另一回事。**說明／待裁。** 建議：寬度判定維持（今天通過 → 已涵蓋）；顏色門檻改 ≤ 3 或改比鄰段；停用組是否屬 #22 由 Planner 決定。
8. （說明）**#25 不需要 `slidingtext`**：Slider 預設 `'{0}'`，拖曳就顯示提示，計畫「預覽頁缺實例時」那條對 #25 不適用。
9. （說明）**#24 停用 widget 的游標代理值是 `auto`、元素是外層 div**（`pointer-events:none`），root 的 `not-allowed` 使用者看不到；最終比對請用這個值。
10. （說明）**#24 3 組 multislider 今天幾乎沒有可點的軌道**（4/396 個非 thumb 點會動；下層兩組的 area 被上層包覆層蓋住、裸軌道命中包覆層不動）；「會動的點」集合極小，修正後的「不會動 → default」範圍會是幾乎整個外框，Generator brief 要說清楚這是 CSS 游標的範圍，不是把軌道變成可點（那是 JS）。

RED6: METHOD-DEFECTS (items #18 保護項 (a)(b) toolbar 今天失敗, #19 J19-2 今天就過, #25 J25-3 今天就過, #26 J26-2 分支未定義, #24 thumb rect／mark label 取點, #22 色差門檻與停用組)
