使用 8085（A 線，批次 7 RED）

# Gate：第七批 RED run，selectbox 箭頭、bandbox 的 listbox 藍條（#29 #3）

- **伺服器：** 8085，java pid 58306（16:04 由 `tail -f /dev/null | withjdk.sh 17 ./gradlew appRun -PhttpPort=8085` 啟動，16:06 開始聽）。**沒有 kill、沒有重啟、沒有碰 8105。** 新鮮度：`zul/src` 與 `zul/build/resources/main` 的 `selectbox.css` md5 相同（`2f57456c…`）；伺服的 `selectbox.css.dsp`、`listbox.css.dsp`、`bandpopup.css.dsp` 與 `zul/build/resources/main/web/...` 的檔案 md5 **逐一相同**；`zul/src`、`zul/build`、`zkcml/{zkmax,zkex}/src` 底下沒有任何 `.css/.css.dsp/.svg/.xml` 比伺服器啟動時間（`build/gretty_ports.properties` 16:06:15）新 → 伺服的是現在的 build。
- **日期：** 2026-10-08，Verifier，量現在的程式碼（Playwright 1.59.1、Chromium 147.0.7727.15、viewport 1280×900、deviceScaleFactor 2；注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`、游標停在 (2,2)；每個狀態都在重新載入的頁面上量）。後備路徑另用 WebKit 26.4、Firefox 148.0.2（本次 `npx playwright install webkit firefox` 裝進 `~/Library/Caches/ms-playwright`，沒有動 repo）。
- **方法：** lines/line-a-plan.md（過程資料已清除） 第二節（共用量法、#29、#3）。ΔE 一律 **CIE76**（lib.js）。
- **腳本與原始輸出：** `gates/batch7-red/`：`lib.js`（複製自 batch6-red）、`probe-dom.js/.json` + `probe-*.png`（DOM 形狀探針）、`probe-scrollbar.js/.json`（捲軸寬度探針，見 #3 的環境備註）、`red29.js/.json` + `red29-{rest,preselected,disabled,hover,hoverArrow,focus,open,open-page,combobox,forced,fallback-webkit,fallback-firefox}.png`、`red29-md3.js/.json`（Jess 參考圖的箭頭 ink）、`red3.js/.json` + `red3-*.png`、`pw/forced-colors-spec.log`（forced-colors 套件）、`ref/jess-{29-current,29-md3,3-current}.png`（Jess 的 issue 截圖）。
- **沒有修改**任何 theme／CSS／TS／Java／預覽頁檔案，沒有看 diff，沒有 commit；頁內只注入 lib.js 的停動畫規則。#3 的「補充實例」是在瀏覽器裡用 widget API `setHeight('150px')` 改執行期狀態，不是改檔、不是注入樣式（下文標明）。
- **選取 widget 的方式：** DOM id 是 uuid，一律用 class + 頁面順序（`document.querySelectorAll('.z-selectbox')[i]` 等）。
- **讀計算值的地方（只為解釋現象與保護項紀錄，不是判定依據）：** selectbox 的 `appearance`／`opacity`／border／box-shadow／`::picker-icon` 的 `transform`；bandpopup 外框的 shadow／border／radius／min-width；`elementsFromPoint` 命中的元素與其 `background-color`（計畫明寫允許，是 #3 的根因證據）。

## 結論摘要

| 項目 | 判定檢查今天 | 保護項今天 | 方法問題 |
|---|---|---|---|
| #29 J29-1 箭頭 ink 高寬比與面積落在 combobox chevron ±25% | 高寬比 **1.222 vs 1.6（−23.6%，在範圍內、臨界）**；面積 **53.75 vs 15.5 px²（+247%）** → **失敗，RED-correct**（只靠面積） | — | **METHOD-DEFECT（量法）**：「面積」拿實心三角比空心 chevron；就算把三角縮到 chevron 的 8×5 外框，實心 ink ≈ 20 px² 仍是 +29%，永遠過不了。建議改比外框 w、h（各 ±25%）或改以 MD3 基準比 |
| #29 J29-2 垂直中心差 ≤ 1px、右緣距與 combobox 差 ≤ 2px | 中心差 **+0.5**、右緣距 **12.77 vs 12.08（差 0.69）** → **今天就通過** | → 應改為保護項 | **METHOD-DEFECT（歸類）** |
| #29 保護項 (a)(b)(c)(d) | — | (a) 外框 120×40，rest／hover／focus／open 四態座標完全相同 ✓；(b) 文字 ink 左緣 110.5（內縮 13.73）、垂直中心 202.5（−0.5）四態相同 ✓；(c) rest 邊框 (196,196,196)、hover (33,33,33)、focus (55,111,208) + inset box-shadow 1px 同色（記錄基準）；(d) disabled `opacity 0.38`、`background rgb(247,249,252)`，像素底 (251,252,253)、邊框 (230,230,231)、箭頭最深 (195,195,196)（記錄） | 無 |
| #29 保護項 (e) `:open` 旋轉 180° | — | `::picker-icon` transform **`matrix(-1,0,0,-1,0,0)`** ✓；但 ink 外接矩形 **10.5×9 vs 11×9**、垂直中心 **202.5 vs 203.5（高 1px）**、列剖面反轉後差 15／215 device px（7%）→ **不是精確鏡像** | **METHOD-DEFECT（容差）**：「鏡像相符」要給容差（外框 ±0.5px、中心 ±1px），否則最終比對今天的行為就過不了 |
| #29 保護項 (f) forced-colors | — | `forced-colors` 專案 **17/17 passed**（`pw/forced-colors-spec.log`）；forced-colors 下箭頭 ink 11×9、最深 (0,0,0) 在白底上，可辨識 ✓ | 無 |
| #29 保護項 (g) 非 base-select 後備 | — | **Firefox 148**（`CSS.supports('appearance','base-select')` false）：`appearance none`、`background-image chevron-down-gray.svg`，右側 40px 內只有 **1 個** ink 區塊 12×7 @ cy 202.5（差 0）、右緣距 12.77 → 無重複箭頭 ✓。WebKit 26.4 也支援 base-select（1 個區塊 10×10.5，記錄） | 無 |
| #3 J3-1 表頭右端欄位 vs 表頭其他部分 ΔE ≤ 2 | 條 (240,244,250) vs 表頭空白 (255,255,255) → ΔE **5.18** → **失敗，RED-correct** | — | 無（但見環境備註：必須拿掉 Playwright 的 `--hide-scrollbars`） |
| #3 J3-2 listbox 區沒有其他偏藍直條 | 直條掃描只找到 x 298–304 的 (224,224,224)（ΔE 10.82、不偏藍）= 捲軸滑塊 → **沒有第二個藍色塊** | — | **說明**：J3-2 的前提不成立，不適用（計畫已預留此分支） |
| #3 範圍規則 | **一般 listbox 也有**：`listbox-header.zul` #9（`<listbox height="200px">`）同一個 `th.z-listhead-bar` (240,244,250)、ΔE 5.18；`listbox.zul` 沒有天然會垂直捲動的 listbox（8 個都 scrollHeight = clientHeight），用 widget API 給 #0 設 150px 高後也是 (240,244,250)、ΔE 5.18 | — | **觸發計畫的例外第 4、9 項 → D53，停下來問**（修在 `listbox.css` 會改所有 listbox） |
| #3 保護項 (a)–(f) | — | (a) 捲軸寬 **6px**、滑塊 (224,224,224)、軌道 (255,255,255)；(b) 表頭／body 欄位邊界 58／178／298 逐欄差 **0／0** ✓；(c) 外框 `.z-bandbox-popup.z-bandbox-shadow` 32,421 300×230：shadow `rgba(0,0,0,.12) 0 2px 6px, rgba(0,0,0,.14) 0 1px 2px`、border `1px solid rgba(0,0,0,.12)`、radius 4px、min-width 300px；下方陰影列 215→230→242→247→250→253→254→255；(d) 列 rest 白、hover (237,237,237) ΔE 6.25、選取 **(200,213,234)** ΔE 19.16（= secondary-container，D10-A）、選取+hover (186,198,219) ✓；(e) 外框與內層 `.z-bandpopup` 41,430 280×212 座標記錄；(f) `Content` 純文字 bandbox popup 122,232 282×52、平均色 (253,253,253)、字 13px/20px Inter（記錄） | 無 |

## #29 — selectbox 箭頭比例

DOM：`select.z-selectbox`（`appearance: base-select`，Chromium 147 支援），箭頭是 `::picker-icon` 偽元素（`font-size 16px`、`color rgba(0,0,0,.6)`、盒 10.55×24），**只能用像素量**。量法：控制項右側 40px、扣掉邊框側 3px 與右側兩個 6px 圓角後，與區域中位色 ΔE ≥ 8 的像素外接矩形。

| 狀態（各自重新載入） | 外框 | 箭頭 ink w×h | ink 中心 (cx, cy) | 控制項中心 | 中心差 | 右緣距外緣 | 高寬比 | ink 面積 px² | 最深像素 |
|---|---|---|---|---|---|---|---|---|---|
| rest（selectbox[0]，"Bob"） | 96.77–216.77 × 183–223 | **11×9** | (198.5, 203.5) | 203 | **+0.5** | **12.77** | **1.222** | **53.75** | (102,102,102) |
| pre-selected（selectbox[2]） | 32–152 × 334–374 | 10.5×9 | (133.75, 354.5) | 354 | +0.5 | 13.00 | 1.167 | 53.5 | (102,102,102) |
| disabled（selectbox[1]） | 684.38–804.38 × 183–223 | 10×9 | (786, 203.5) | 203 | +0.5 | 13.38 | 1.111 | 50.75 | (195,195,196)，底 (251,252,253) |
| hover（游標在控制項中央／箭頭上） | 同 rest | 11×9 | (198.5, 203.5) | 203 | +0.5 | 12.77 | 1.222 | 53.75 | 邊框變 (33,33,33) |
| focus（Tab 進入，`:focus-visible`） | 同 rest | 11×9 | (198.5, 203.5) | 203 | +0.5 | 12.77 | 1.222 | 53.75 | 邊框 (55,111,208) |
| open（點開後游標移開） | 同 rest | **10.5×9** | (198.25, **202.5**) | 203 | **−0.5** | 13.27 | 1.167 | 53.5 | `::picker-icon` transform `matrix(-1,0,0,-1,0,0)` |
| forced-colors | 同 rest | 11×9 | (198.5, 203.5) | 203 | +0.5 | 12.77 | 1.222 | 54 | (0,0,0) |
| **combobox chevron**（`combobox.zul` combobox[0]，`chevron-down.svg` 遮罩 14×14） | 112.08–317.08 × 183–223 | **8×5** | (301, 203) | 203 | **0** | **12.08** | **1.6** | **15.5** | (102,102,102)，1 個連通區塊 |
| Firefox 148 後備（`chevron-down-gray.svg` 背景圖） | 96.77–216.77 × 182.5–222.5 | 12×7 | (198, 202.5) | 202.5 | 0 | 12.77 | 1.714 | 28 | 1 個連通區塊 |
| WebKit 26.4（也支援 base-select） | 96.77–216.77 × 182.7–222.7 | 10×10.5 | (198, 202.75) | 202.7 | +0.05 | 13.77 | 0.952 | 65.75 | 1 個連通區塊 |

- **J29-1：** 高寬比 1.222／1.6 = 0.764（−23.6%，**在 ±25% 內但臨界**）；面積 53.75／15.5 = 3.47（+247%）→ 失敗。失敗只靠面積。
- **J29-2：** 中心差 0.5 ≤ 1、右緣距差 |12.77 − 12.08| = 0.69 ≤ 2 → **今天就通過**。Jess 看到的「比例不對」是 glyph 本身（11×9 的粗實心三角 vs 8×5 的細 chevron），不是位置。
- **MD3 參考（`red29-md3.json`，Jess 的 `jess-29-md3.png` 361×136）：** 藍框 270–301 × 53–91 內的箭頭 ink **4×8 px**（向右的實心三角，列剖面 1,2,3,4,4,3,2,1，ink 20 px，外框面積 32）；長短邊比 **2.0**。比例尺不明：用左側圓形圖示直徑 22px 當 20dp 算 1.1x → 1x 約 **7.3×3.6、ink 16.5**；用兩列 Label 的間距算 1.28x（第二列被圖底切掉，較不可靠）→ 1x 約 6.2×3.1。計畫寫的名目值 10×5（實心 ink 25）比圖上量到的還大。
- **「縮小並對齊成 chevron 是否就落在 MD3 基準 ±25%」（只給數字）：**
  - 今天的 selectbox 11×9：對名目 10×5 → 長邊 +10%、短邊 **+80%**、高寬比 **−39%**、ink **+115%** → 全部不在。
  - combobox chevron 8×5（今天的同族基準）：對名目 10×5 → 長邊 −20% ✓、短邊 0% ✓、高寬比 1.6 vs 2.0 = −20% ✓、外框面積 40 vs 50 = −20% ✓、**ink 15.5 vs 25 = −38% ✗**（空心對實心）；對圖上量到的 8×4／ink 20 → 長邊 0%、短邊 +25%（邊界）、高寬比 −20%、ink −22.5%，全部在。
  - 若保留實心三角、只把外框縮到 chevron 的 8×5：高寬比 1.6（−20% ✓）、ink ≈ 20（對名目 25 是 −20% ✓、對圖上 20 是 0% ✓）→ **兩邊基準都在 ±25% 內**。
  - 所以純就 ±25% 的數字，「對齊到 chevron 的外框尺寸」不會與 MD3 衝突；剩下的是**形狀**（實心三角 vs 線條 chevron）的選擇，數字分不出來，依計畫是 D52 的問題。
- **保護項 (c) 細節：** rest 上／右邊框像素 (196,196,196)；hover (33,33,33)（計算值 `rgba(0,0,0,.87)`）；focus (55,111,208)，計算值 `border 1px rgb(55,111,208)` + `box-shadow inset 0 0 0 1px rgb(55,111,208)`（2px 環）；上緣 ±4px 的逐列中位色在 `red29.json` 的 `band`。Tab 一次就到 selectbox[0]（`tabs: 1`）。
- **保護項 (e) 細節：** rest 列剖面（上→下 device px）`22,20,20,18,18,16,14,14,12,12,10,9,8,6,6,4,4,2`，open `2,3,5,5,7,7,9,11,11,13,13,15,16,17,19,19,21,21`；反轉比對逐列差總和 15（7%），外框寬差 0.5、中心高 1px。旋轉是以偽元素盒的中心轉，不是以 ink 的中心轉，所以不是精確鏡像。

## #3 — bandbox 的 listbox 右側藍條

**環境備註（最終 run 必讀）：** Playwright headless 預設帶 `--hide-scrollbars`，`probe-dom.json` 裡每個 `.z-listbox-body` 都是 `offsetWidth == clientWidth`、`th.z-listhead-bar` 寬 0 → 藍條**根本畫不出來**，現有 gallery baseline 也看不到它。`probe-scrollbar.json`：`ignoreDefaultArgs:['--hide-scrollbars']` 後（headless 與 headful 相同）捲軸佔 **6px**（Marble 的細捲軸；Jess 的截圖是她系統的傳統捲軸約 14px，機制相同）。`red3.js` 用這個設定；最終 run 與 gallery 都得這樣跑才量得到。

**根因證據（`red3.json` `bandbox.open.stack`）：** 點開「Listbox in bandpopup」後，捲軸欄位 x 298–304、表頭 y 447–500，`document.elementsFromPoint(301, 473.5)` 由上而下：**`TH.z-listhead-bar`，`background-color: rgb(240,244,250)`，rect 298,447 6×53** → `TABLE`（透明）→ `DIV.z-listbox-header`（透明）→ `DIV.z-listbox`（白）→ `DIV.z-bandpopup`（白）→ `DIV.z-bandbox-popup.z-bandbox-open.z-bandbox-shadow`（白）。這塊顏色就是 listbox 表頭最後那個補位 `th`（`z-listhead-bar`，寬 = 捲軸寬）自己的底色，與 bandbox／bandpopup 無關。

| 實例 | 捲軸寬 | 條的元素／色 | 表頭空白（欄界 ±6px）| 表頭空白（第一欄左緣）| ΔE 條 vs 空白 |
|---|---|---|---|---|---|
| bandbox.zul 的 bandpopup listbox（issue 本體） | 6 | `th.z-listhead-bar` (240,244,250) | (255,255,255) | (255,255,255) | **5.18** |
| `listbox-header.zul` #9，`<listbox height="200px">`，一般 listbox（天然垂直捲動） | 6 | `th.z-listhead-bar` (240,244,250) | (255,255,255) | (255,255,255) | **5.18** |
| `listbox.zul` #0（Row States），**執行期** `zk.$(n).setHeight('150px')` 補充實例 | 6 | `th.z-listhead-bar` (240,244,250) | (255,255,255) | (255,255,255) | **5.18** |
| `listbox.zul` 8 個 listbox 原狀 | 0（都不垂直捲動） | `th.z-listhead-bar` 存在、計算值 (240,244,250)、寬 0 | — | — | 量不到 |

- **J3-1：** 5.18 > 2 → 失敗，RED-correct。
- **J3-2：** popup 內 listbox body 區逐欄中位色掃描，只有 x 298–304 一條 (224,224,224)、ΔE 10.82、**不偏藍**（b 不大於 r）= 捲軸滑塊；沒有第二個藍色直條 → 不適用。（補充實例 listbox.zul #0 的掃描有很多偏藍的欄，那是該 listbox 本來就有一列 `selected` (200,213,234)，與本題無關。）
- **範圍規則：** 一般 listbox **也有**（listbox-header.zul #9 天然實例、listbox.zul #0 執行期實例都是同一個 `th.z-listhead-bar` 同一個色）。依計畫：修在 `listbox.css` 會改所有有垂直捲軸的 listbox 的表頭右端 → **例外第 4、9 項，D53 停下來問**。`listbox.zul` 本身沒有天然垂直捲動的實例，這點要記在計畫（最終 run 的 listbox 保護項得用 listbox-header.zul #9 或補一個實例）。
- **保護項細節：** (a) 滑塊 y 501–598.5；(b) `th` 58–178／178–298 = `td` 58–178／178–298；(c) 外框陰影右側逐欄 234→246→250→253→254→255，左上角像素 (252,252,252)、內一像素 (226,226,226)；(d) 點第一列後 popup 仍開著（`z-listitem-selected z-listitem-focus`）；(f) `Content` popup 內層 padding 16px、`min-width 0`（外框 min-width 300px）。

## 方法問題清單

1. **#29 J29-2 今天就通過**（中心差 0.5、右緣距差 0.69）。**METHOD-DEFECT（歸類）。** 建議改為保護項「修正後中心差 ≤ 1px、右緣距與 combobox 差 ≤ 2px」。
2. **#29 J29-1 的面積項拿實心三角比空心 chevron。** 任何實心三角縮到 chevron 的 8×5 外框 ink 都 ≈ 20 px²（+29%），本項按字面永遠失敗；高寬比項今天 −23.6% 已在 ±25% 內（臨界、不具鑑別力）。**METHOD-DEFECT（量法）。** 建議改比外框 w、h 各 ±25%（今天 11 vs 8 = +37.5%、9 vs 5 = +80%，仍失敗、RED 成立），或依 D52 直接以 MD3 名目 10×5 當基準。
3. **#29 保護項 (e)「鏡像相符」今天不是精確鏡像**（外框 10.5 vs 11、中心高 1px、剖面差 7%）。**METHOD-DEFECT（容差）。** 建議：旋轉後外框 w、h 差 ≤ 0.5px、中心差 ≤ 1px、`::picker-icon` transform 仍為 180°。
4. （說明）**#3 J3-2 不適用**：沒有第二個藍色塊，只有不偏藍的捲軸滑塊。
5. （說明／裁決）**#3 範圍規則觸發**：一般 listbox 也有同一條 → D53。另 `listbox.zul` 沒有天然垂直捲動的 listbox。
6. （說明／最終 run 必讀）**#3 量測必須拿掉 Playwright 的 `--hide-scrollbars`**，否則條寬 0、量不到；捲軸是 Marble 的 6px，不是 Jess 截圖的 ~14px。gallery baseline 今天看不到這條，修正後 `bandbox-gallery`／listbox gallery 的靜止畫面預期**不變**（與計畫一致），但也表示 gallery 不能當這題的回歸證據。
7. （說明）**MD3 參考圖比例尺不明**：圖上箭頭 4×8（長短比 2.0），圓形圖示校正約 1.1x；計畫的名目 10×5 是另一個基準。J29-1 若改以 MD3 為準，要先指定用哪一個。
8. （說明）WebKit 26.4 也支援 base-select，所以 (g) 的後備路徑只有 Firefox 能驗（今天通過：一個箭頭、`chevron-down-gray.svg`）。

方法缺陷：第 1 項（#29 J29-2 今天就過）、第 2 項（#29 J29-1 面積項實心比空心）、第 3 項（#29 保護項 (e) 鏡像需容差）；另第 5 項 #3 範圍規則觸發 D53、第 4 項 J3-2 不適用。

RED7: METHOD-DEFECTS
