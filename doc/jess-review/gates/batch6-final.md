# Gate：第六批最終判定（GATE6-FINAL）— combobutton／slider 系列／rating／inputgroup（#18 #19 #22 #24 #25 #26 #27）

- **使用 8085**（共用的 zkpreview；本次沒有 kill、沒有重啟、沒有開第二個 port。第一次派工時 8085 伺服的是 14:37 的舊 build，已回報並由 Planner 重啟；本報告是重啟後從頭跑的完整結果）。
- **日期：** 2026-10-08，Fable Verifier。
- **8085 build 確認：** 頁面載入的 `/zkres/web/80d82a3f/zul/css/zk.wcs` 含 `z-combobutton-button:before` 1 處；`combobutton／rating／slider／multislider／rangeslider .css.dsp` 回應大小 3899／1608／3056／3926／4982 bytes = 15:21 的 codegen 輸出。
- **方法：** jess-review-verification-plan.md（過程資料已清除）「第六批」+「第六批 RED run 結果與方法定稿」（定稿修正取代舊措辭）。ΔE 一律 **CIE76**（lib.js）；Playwright 1.59.1、Chromium 147、viewport 1280×900、DPR 2、注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`、量前游標移到 (2,2)、每個狀態在重新載入的頁面上量。
- **腳本與原始輸出：** `gates/batch6-final/`：`lib.js` 與 `red18/19/22/24/25/26/27.js`（複製自 batch6-red，只改輸出檔名；`red24.js` 依定稿改：thumb 上 = 距中心 ≤ 10px、格點區域 = root ∪ track、另加每個 mark／mark-dot／mark-label 的中心），輸出 `final*.json`／`final*.png`／`final*.log`；本次新增探針 `probe18-active.js`（:active 與 focus 分離）、`probe24-ring.js`（光環與軌道分離，無 click 的乾淨序列）、`probe24-focus.js`（確認 click 後的全亮環是 focus-within）、`probe26-ink.js`（knob 輸入框文字 ink 位置）。回歸：`pw-regression.log` + `pw/`。
- **沒有看 diff、沒有讀 CSS 修改內容**（只為確認 build 新舊 grep 了規則名 `z-combobutton-button:before`），沒有修改任何 theme／CSS／TS／Java／預覽頁／baseline 檔案，沒有 `--update-snapshots`，沒有 commit。

## 結論摘要

| 項目 | 判定 | 結果 | 數值 |
|---|---|---|---|
| #18 J18-1 filled，hover 箭頭 | 箭頭環帶 ≥ 2 且標籤 ≤ 1 | **通過** | 環帶 **6.50**、標籤 **0**（RED 0／6.88） |
| #18 J18-2 toolbar，hover 箭頭 | 同上 | **通過** | 環帶 **3.99**、標籤 **0**（RED 3.99／4.16） |
| #18 J18-3 filled，箭頭上按住 | 箭頭 ≥ 2 且標籤 ≤ 1 | **:active 本身通過；字面量到的標籤 10.37 來自 focus-within（R6 範圍外）** | 按住：環帶 **9.74**、標籤 **10.37**；按住中 `blur()`：環帶 **9.74**、標籤 **0**（見 #18 節） |
| #18 J18-4 toolbar，hover／按住標籤時箭頭不變 | 箭頭環帶 ≤ 1 | **hover 通過；按住同 J18-3** | hover 標籤：環帶 **0**（RED 3.99）；按住標籤：環帶 5.98 → `blur()` 後 **0**（RED 5.98） |
| #18 保護項 (a)(b) filled | 標籤變、箭頭不變 | 通過 | hover 標籤：標籤 6.88／箭頭 0；按住標籤：10.37／0（與 RED 相同） |
| #18 保護項 (c)(d)(e)(f) | 不變 | 通過 | (c) Tab 聚焦 filled 4.12／10.37、toolbar 27.63／6.25 = RED；(d) open filled 0.04／10.37、toolbar 5.98／6.25 = RED；(e) 停用 hover 全 0；(f) content 116.11×36、button 32×36、分隔線 1px，所有狀態 `geomSame` true |
| #19 J19-1 停用 filled 兩段同色 | ΔE ≤ 1 | **通過** | 箭頭四角 (224,224,224)、標籤四角 (224,224,224) → **0**（RED 196 vs 224 = 10.02） |
| #19 保護項 | | 通過 | 停用 toolbar 兩段白／白 0；啟用 filled 兩段 (55,111,208) 0、toolbar 白 0；停用文字像素 (139,139,139) 不變、computed `color` 不變；文字對比 2.58 = RED；圖示對比 2.58 ≥ RED 2.50（見 #19 節） |
| #22 J22-1／J22-2（只量） | 接縫 1px、同色 ΔE ≤ 3、外框 1px | **仍已涵蓋** | 12 組所有接縫與外框 1px；色差最大 2.45 ≤ 3；數值與 RED 完全相同（停用 textbox 230 vs 190 = 14.02 仍在，follow-up，不在本批） |
| #24 J24-1 不會動的點 default／auto | 無 `pointer` | **通過** | 10 個啟用 widget 共 3,476 個不會動的點：**`pointer` 0**；`default` 3,364；`auto` 73（頁面容器）；`grab` 39（命中元素就是 thumb 本體，距中心 10.0–11.2px，見 #24 節）。RED 時 73–99% 是 `pointer` |
| #24 J24-2 multislider hover 環 | 只亮游標底下那顆 | **通過** | hover thumb0：[5.54, 0, 0, 0, 0, 0]（3 組）、[5.26, 0, 0, 0]（垂直）；停在軌道／padding（無 click 序列）：全 **0**（RED 全部 5.0–5.5、軌道 8.5） |
| #24 J24-3 rangeslider hover 環 | 同上 | **通過** | hover thumb0：[5.54, 0]、垂直 [5.19, 0]；軌道／padding：全 **0** |
| #24 保護項 (a)–(e) | 不變 | 通過 | (a) thumb 中心 `grab`、按住 `grabbing` 全部；(b) 每顆 thumb 中心命中自己（6+4+2×8 顆）；(c) 拖 40px thumb 與 area 更新；**會動的點 743/743 仍 `pointer`**；(d) 停用 widget 全部 `auto`（元素外層 flex div）= RED；(e) plain slider 5 個：198／126 點全部會動、`pointer`、hover 只亮單顆 10.59–10.64、軌道 ≤ 1.02 = RED |
| #25（本批暫停，只確認沒被動） | 與 RED 相同 | **相同** | 水平 dx 1.65／1.50／4.97／7.41、dy −24；垂直 dy 4.0、dx 21.5–27.41；ink 0／0；提示 28px 高、11px、(240,244,250)；放開後消失 |
| #26 J26-1 knob 輸入框靜止 inline | 內部與邊緣 vs 外側 ≤ 2 | **通過** | 內部 (255,255,255) vs 外側白 **0**；邊緣 1px **0**（RED 5.18／24.39） |
| #26 J26-2 聚焦 = textbox 聚焦環 | ΔE ≤ 3、無 UA outline、矩形與文字不位移 | **通過** | 邊緣中位 (55,111,208) vs textbox 聚焦邊緣中位 (55,111,208) → **0**；computed `outline-style none`、`border 2px solid rgb(55,111,208)`；對比 4.83 ≥ 3；輸入框 rect 85–180 × 641–717 rest／focus 相同；文字 ink bbox 105–158.5 × 660.5–697.5 rest／focus **相同（0px）** |
| #26 保護項 | | 通過 | 文字 `rgb(55,111,208)`／600／50.6667px／center／Arial = RED；rect 95×76 = RED；Cmd+A、80、Enter → value 80、curpos 80、弧線 path 終點 (44.16,23.14)→(190.35,129.36)、rect 不變；forced-colors 邊框黑 ΔE 100 |
| #27 J27-1 readonly／disabled 游標 | 皆 `default`／`auto` | **通過** | 水平 Disabled／Readonly、垂直 Disabled／Readonly 4 顆：星中心 ×5、間隙 ×4、外層內點 ×3 全部 **`default`**（元素 `DIV.z-rating`）；RED 全 `pointer` |
| #27 保護項 (a)–(d) | | 通過 | (a) 7 顆可互動星中心 `pointer`（`I.z-rating-icon`）；(b) hover 第 4 顆：1–4 顆 `matrix(1.1,…)`、第 4 顆 ΔE 19.02，readonly／disabled 無 transform、5 顆 0；(c) 點第 4 顆 3→4（垂直 3→2，同 RED），readonly／disabled 3→3；(d) 可互動間隙 `pointer`（記錄，與 RED 相同） |

## #18 — combobutton hover／active 落點（`final18.json`、`probe18-active.json`）

| 狀態（各自重新載入） | filled 箭頭環帶 | filled 標籤 | toolbar 箭頭環帶 | toolbar 標籤 |
|---|---|---|---|---|
| rest | (63.4,117.1,210) | (55,111,208) | (252.5,252.5,252.5) | (255,255,255) |
| hover 箭頭中心 | **6.50** → (77.8,127.6,212.8) | **0** | **3.99** → 241 | **0** |
| hover 標籤中心 | 0 | 6.88 | **0** | 4.16 |
| 箭頭上按住 | **9.74** | 10.37（focus-within） | 11.73 | 6.25（focus-within） |
| 標籤上按住 | 0 | 10.37 | 5.98（focus-within） | 6.25 |
| Tab 聚焦 | 4.12 | 10.37 | 27.63 | 6.25 |
| open（點箭頭後游標移開） | 0.04 | 10.37 | 5.98 | 6.25 |
| 停用 hover 箭頭／標籤 | 0／0 | 0／0 | 0／0 | 0／0 |

**hover（J18-1、J18-2、J18-4 hover、保護項 (a)）全部乾淨通過**：hover 箭頭只亮箭頭，hover 標籤只亮標籤，filled 與 toolbar 都是。

**按住（J18-3、J18-4 按住、保護項 (b)）需要分離原因。** `probe18-active.js`：在按住狀態下對 widget 呼叫 `blur()`（滑鼠仍按著、`:active` 仍 true、`:focus-within` 變 false），再量同樣的矩形：

| | 按住（focused） | 按住中 blur（只剩 :active） | 放開 |
|---|---|---|---|
| filled，箭頭上按住 | 環帶 9.74／標籤 **10.37** | 環帶 **9.74**／標籤 **0** | 環帶 6.47（hover）／標籤 10.37（open） |
| filled，標籤上按住 | 0／10.37 | 0／10.37 | 0／6.88 |
| toolbar，箭頭上按住 | 11.73／6.25 | 5.98／**0** | 10.03／6.25（open） |
| toolbar，標籤上按住 | **5.98**／6.25 | **0**／6.25 | 0／4.16 |

- `:active` 的落點已經正確：箭頭上按住只有箭頭亮（filled 9.74／0、toolbar 5.98／0）；標籤上按住只有標籤亮（toolbar 箭頭 0）。
- 按住時字面量到的「另一半也亮」（filled 標籤 10.37、toolbar 箭頭 5.98）**全部來自 `:focus-within` 的狀態層**：mousedown 讓 widget 取得焦點，focus 層蓋整顆 content（toolbar 的箭頭透明，所以也透出來）。這一層是計畫 R6 明寫「focus 與 open 狀態不動」、保護項 (c) 要求不變的東西，Tab 聚焦的值（10.37／6.25）與 RED 完全相同。
- RED 當時的推論「blur 後標籤仍亮 → 來自 :active，修正後標籤 ≤ 1 可達成」只證明了 :active 有貢獻，沒有排除 focus 也有貢獻；現在 :active 被移到箭頭後，focus 的那份就露出來了。**在「focus 不動」的前提下，J18-3 字面的「標籤 ΔE ≤ 1」不可能成立**，不是 Generator 沒做到。本報告的判定：**:active 本身通過，字面值記錄如上，由 Planner 裁定 J18-3／J18-4 按住部分是否以「排除 focus 層（blur 探針）」的值為準。**
- 保護項 (c)(d)(e)(f) 的值與 RED 逐一相同（上表），幾何 `geomSame` 全 true。

## #19 — 停用 combobutton 的箭頭底色（`final19.json`）

| combobutton | 箭頭四角 | 標籤四角 | ΔE | 前景最深像素（文字／圖示） | 對比（文字對標籤／圖示對箭頭） |
|---|---|---|---|---|---|
| filled 啟用 | (55,111,208) | (55,111,208) | 0 | 白／白 | 4.83／4.83 |
| **filled 停用** | **(224,224,224)** | **(224,224,224)** | **0** | (139,139,139)／(139,139,139) | 2.58／2.58 |
| toolbar 啟用 | 白 | 白 | 0 | (102,102,102) | 5.74 |
| toolbar 停用 | 白 | 白 | 0 | (158,158,158) | 2.68 |

- computed：停用時 content `rgba(0,0,0,0.12)`、button **`rgba(0,0,0,0)`**（RED 時 button 也是 0.12，疊兩層得 196）；分隔線欄位 224（透出）。
- 停用文字像素 (139,139,139) 與 RED 相同；圖示最深像素 RED 是 (121,121,121)、現在 (139,139,139)：computed `color` 兩者都是 `rgba(0,0,0,0.38)` 沒變，差別只是同一個半透明前景疊在 196（RED）或 224（現在）上的合成結果（0.62×196 = 121.5、0.62×224 = 138.9）——正是修正本身。圖示對箭頭底對比 2.58 ≥ RED 2.50，沒有降低。

## #22 — inputgroup（只量，`final22.json`）

12 組（11 水平 + 1 垂直）：所有接縫與外框 1px（中線、t+5、b-5 三條線一致），色差：接縫 0 或 2.16（190 vs 196，半透明邊框疊不同底色），外框 0.54／2.16／2.45，全部 ≤ 3。G8（textbox + button）右側直接是 button 填色，無邊框線（同 RED）。停用 textbox 邊框 (230,230,231) vs 190 = 14.02（G9、G10）仍在，定稿已列 follow-up。**每一個數字與 RED 相同 → 已涵蓋、沒被動。**

## #24 — multislider／rangeslider 游標與 hover 環（`final24-*.json`、`probe24-ring.json`、`probe24-focus.json`）

**格點（定稿）：** root ∪ track 內縮 2px、8px 間距 + 軌道中線 + padding 列 + 每個 mark／mark-dot／mark-label 中心。每點：`elementFromPoint` 的 computed cursor，再在重新載入的頁面點一下看 thumb 是否移動。「thumb 上」= 距任一 thumb 中心 ≤ 10px。

| widget | 點數 | thumb 上 | 會動（游標） | 不會動 | 不會動的游標 |
|---|---|---|---|---|---|
| multislider 水平 3 組 | 453 | 45（全 `grab`） | 4（`pointer` 4，`z-sliderbuttons-area`） | 404 | `default` 395、`auto` 7（mark label → 頁面容器）、`grab` 2（thumb 本體，距中心 10.8） |
| multislider 垂直 | 779 | 34（`grab`） | 29（`pointer`） | 716 | `default` 699、`auto` 14（外框底下 12px 那排 + mark）、`grab` 3（10.6–10.7） |
| multislider markScale | 463 | 14（`grab`） | 26（`pointer`） | 423 | `default` 407、`auto` 12、`grab` 4（10.8） |
| multislider 文字 marks | 451 | 15（`grab` 14、`default` 1 = 命中包覆層） | 30（`pointer`） | 406 | `default` 400、`auto` 6 |
| multislider 停用 | 453 | 14 | — | — | 全部 `auto`（元素外層 `DIV.z-d-flex`）= RED |
| multislider tooltipVisible | 453 | 12（`grab`） | 25（`pointer`） | 416 | `default` 409、`auto` 7 |
| rangeslider 預設 | 390 | 20（`grab`） | 98（`pointer`） | 272 | `default` 263、`auto` 7、`grab` 2（10.0） |
| rangeslider markScale／垂直 ×2 | 390 ×3 | 15（`grab`） | 72（`pointer`） | 303 | `default` 291、`auto` 7、`grab` 5（10.0–10.4） |
| rangeslider 文字 marks | 388 | 15（`grab` 14、`default` 1） | 59（`pointer`） | 314 | `default` 302、`auto` 6、`grab` 6（10.2–11.2） |
| rangeslider 停用 | 390 | 15 | — | — | 全部 `auto` = RED |
| rangeslider tooltipVisible | 390 | 12（`grab`） | 48（`pointer`） | 330 | `default` 321、`auto` 7、`grab` 8（10.4–11.2） |
| **plain slider ×5（記錄）** | 207／135 | 8–9（`grab`） | **199／126 = 全部非 thumb 點**（`pointer`） | 0 | — |

- **J24-1：** 所有不會動的點沒有任何一個 `pointer`（RED 時 73–99%）。`default` 的元素是 widget root 與裸露的 `.z-sliderbuttons` 包覆層；`auto` 的點命中頁面容器（`DIV.z-d-flex`／`DIV.z-p-8.z-div`，在 widget 之外）；`grab` 的 39 個點（每個 widget 2–8 個）距 thumb 中心 10.0–11.2px，**`elementFromPoint` 就是 thumb 元素本身**（20px 的圓角方塊，10px 圓是方法的近似），游標 `grab` 正確，不是缺陷。
- **會動的點 743/743 全部 `pointer`**（全部命中 `.z-sliderbuttons-area`）→ 新增保護項成立；多組 multislider 下層兩組的 area 仍被上層包覆層蓋住（只有 4 個會動點），與 RED 相同，JS 行為未變。
- **mark label：** 每個 widget 的 mark label 中心，`elementFromPoint` 都是頁面容器或 widget root（`DIV.z-p-8.z-div`／`DIV.z-d-flex`／`DIV.z-multislider`），不是 label 本身；點了也沒有 thumb 移動；游標 `auto`／`default`。**如實記錄：label 在這個版本不是命中目標（`pointer-events` 讓事件穿過），所以不會動、游標也不是 `pointer`，與定稿「不會動的點必須 default／auto」一致。** mark-dot（rangeslider）與 mark（multislider）的中心：落在 thumb 上 → `grab`；落在 area 上 → `pointer` 且會動；落在裸軌道 → 命中包覆層 `default` 不會動。
- **J24-2／J24-3（hover 環）：** `red24.js` 的序列在「停在軌道」之前對 thumb0 做了 mousedown／up（保護項 (a) 的 `grabbing` 量法），widget 因此取得焦點；它量到的 hoverTrack 8.48（所有 thumb）與 RED 相同。`probe24-focus.js` 證實：點 thumb0 後把游標移到 (2,2)，環帶 = [8.48, 8.48, 8.17, 8.19, 8.19, 8.19]（與 hoverTrack 完全一致）；`blur()` 後全部 0 → 那是 `:focus-within` 的「一個聚焦、全部亮環」，定稿已列 follow-up、不在本批。`probe24-ring.js` 用**沒有 click 的乾淨序列**（rest → hover thumb0 → hover 軌道 → hover padding），並把環帶拆成「去掉軌道 ±4px 帶的光環」與「軌道本身」：

| widget | hover thumb0：光環 | hover 軌道：光環 | hover padding：光環 | 軌道色變化 |
|---|---|---|---|---|
| multislider 3 組（6 顆） | **[5.82, 0, 0, 0, 0, 0]** | [0, 0, 0, 0, 0, 0] | 全 0 | 0 |
| multislider 垂直（4 顆） | [5.51, 0, 0, 0] | 全 0 | 全 0 | 0 |
| multislider markScale | [5.87, 0] | [0, 0] | [0, 0] | 0 |
| rangeslider 預設／markScale／垂直 | [5.82, 0]／[5.87, 0]／[5.41, 0] | 全 0 | 全 0 | 0 |

  → 只有游標底下那顆亮，停在軌道或 padding 時沒有任何 thumb 亮、軌道不變色（RED 時所有 thumb 5.0–5.5）。
- 保護項 (a)(b)(c)：每顆 thumb 中心命中自己、`grab`、按住 `grabbing`（`body` 仍 `auto`）；拖 40px thumb `left` 10%→18% 等、area rect 跟著變；全部啟用 widget 通過。(d) 停用 widget 的游標代理值全部 `auto`，與 RED 相同。(e) plain slider 數值與 RED 相同（hoverThumb0 10.59–10.64、hoverTrack 0–1.02、整框可點、`pointer`）。
- 記錄（不在本批，RED 已記）：垂直 multislider 的 track 超出 root 底 12px，y=651–659 那排點命中外層 div（`auto`）。

## #25 — slidetip（本批暫停，只確認沒被動；`final25.json`）

5 個 slider × 4 個位置：水平 dx 1.65／1.50／4.97／7.41（= (提示寬 − 20)/2）、dy −24；垂直 dy 4.0、dx 21.5–27.41；Range ink 中心差 0／0、像素 ink ≤ 0.18；提示框 28px 高、padding 4px 8px、11px、(240,244,250)；不重疊、在 viewport 內、放開後 `#zul_slidetip` 不存在、curpos 100。**每個數字與 RED 相同。**

## #26 — knob mold 的數字輸入框（`final26.json`、`probe26-ink.json`）

| 狀態 | 內部帶 | 邊緣 1px | 邊內 1–3px | 外側 | ΔE 內部 vs 外 | ΔE 邊緣 vs 外 | computed |
|---|---|---|---|---|---|---|---|
| rest | 白 | 白 | 白 | 白 | **0**（RED 5.18） | **0**（RED 24.39） | `background rgba(0,0,0,0)`、`border 1px solid rgba(0,0,0,0)`、`outline none`、padding `1px 2px` |
| focus | 白 | **(55,111,208)** | (128.5,163.9,225.3) | 白 | 0 | 77.55 | `border 2px solid rgb(55,111,208)`、`outline none`、padding `0px 1px`（補償 border） |
| forced-colors | 白 | 黑 | — | 白 | — | 100 | `border rgb(0,0,0)` 可辨認 |

- 一般 textbox（`textbox.zul` 第一個）聚焦：`border 2px rgb(55,111,208)`、`outline none`，邊緣像素中位 (55,111,208) → knob 聚焦邊緣中位 (55,111,208)，**ΔE 0**（RED 的 UA outline (0,95,204) 是 10.5）；對比 4.83 ≥ 3。
- rest／focus 之間：輸入框 rect 85–180 × 641–717 不變；文字 ink bbox（藍色像素）105–158.5 × 660.5–697.5 **完全相同**（padding 1px 2px ↔ 0px 1px 補償 border 1px ↔ 2px，`box-sizing: border-box`）。
- 文字 computed 與 RED 相同；Cmd+A、`80`、Enter → value 80、curpos 80、弧線 path 改變、rect 不變。

## #27 — rating 停用／唯讀游標（`final27.json`）

- 11 顆 rating 中 readonly／disabled 4 顆（水平 #1 #2、垂直 #9 #10）：星中心、間隙、外層 (l+1,t+1)／(r−1,b−1)／中心，**12/12 點全部 `default`**（元素 `DIV.z-rating`），RED 全 `pointer`。
- 可互動 7 顆：星中心 `pointer`（`I.z-rating-icon`）；間隙 `pointer`（`DIV.z-rating`，記錄，Generator 保留今天的值）。
- hover 第 4 顆（可互動 #0）：1–4 顆 `matrix(1.1,…)` + `z-rating-hover`、第 4 顆 ΔE 19.02（1–3 顆 3.68 = 放大）；垂直 #8 同；disabled／readonly hover 無 transform、5 顆 ΔE 0。點第 4 顆：#0 3→4（`11110`）、垂直 #8 3→2（由下往上數，同 RED）；disabled／readonly 3→3。

## 回歸（Playwright，8085，唯讀比對，無 `--update-snapshots`；`pw-regression.log`、`pw/`）

一次執行七個 project（`chromium`、`gallery`、`component-theming`、`forced-colors`、`tablet`、`hit-target`、`focus-scan`），2.3 分鐘：**450 通過、3 失敗、47 skipped**。

| project | 結果 | 失敗歸因 |
|---|---|---|
| `chromium`（screenshot.spec，含 combobutton／slider／rating 的 state shots） | 132/132 | — |
| `component-theming` | 107/107 | — |
| `forced-colors` | 17/17 | —（knob 輸入框 forced-colors 邊框黑 ΔE 100，見 #26） |
| `hit-target` | 3/3 | — |
| `focus-scan` | 57 通過、47 skipped | —（skipped 與前幾批相同） |
| `tablet` | 53/55 | `tablet-calendar › gallery`、`tablet-slider › gallery` = **已知既有失敗**，不計（下表） |
| `gallery` | 81/82 | `gallery › grid-header` = **非預期，歸因為過時 baseline，非本批**（下表） |

**三個失敗的歸因（diff 圖在 `pw/<test>/`）：**

| 失敗 | 量到 | 歸因 |
|---|---|---|
| `calendar-tablet` | 900 px（Playwright）；自行 >8 差異 1,088 px，兩區 x 130–569、y 1425–1474／1914–1963 | 與第四批記錄的區域與像素數**完全相同**（datebox 按鈕邊框，`778b5a809f`），已知 |
| `slider-tablet` | 2,354 px（Playwright；第四批時 ~900）；>8 差異 16,727 px，y 852–1001 | 兩部分：y 936–1001 是第四批記錄的弧線（`64f1c07b9d`，已知）；**y 852–928 是 knob 輸入框的底色與邊框消失 = 本批 #26 預期變動**（`slider-tablet.png` 是 tablet 家族的 baseline，與 `slider-gallery.png` 同頁） |
| `grid-header-gallery` | 期望 1280×4525、實際 1280×4522（高度差 3px），131,116 px 差異，**從 y=33（頁面標題列）就開始**，整頁位移；**3/3 次重跑都是 4522，穩定重現** | **非本批**：`grid-header.zul` 沒有 combobutton／slider／rating／inputgroup（grep 0）；裁圖 `pw/grid-header-title-{expected,actual}.png`：baseline（2026-09-11 14:07，batch 3 沒有重生這張）的標題「Grid Header」與章節標題是**沒載到 Inter 的 fallback 字型**（細、regular），實際是正常的 Inter medium／semibold —— 即 baseline 當初是在字型載入前拍的（verification.md 記載的 Inter font-load race），其他 81 頁同樣的標題樣式都通過。第五批的 gallery 只跑了 `-g "chosenbox\|combobox\|cascader"`，所以這張沒被早點抓到。交 Planner：重生 `grid-header-gallery.png`（走前置工作的分類流程） |

**預期變動的量化（gallery 以 `maxDiffPixelRatio: 0.01` 通過，所以要自己量）：** 用一個只改 `snapshotDir` 到 scratchpad、`updateSnapshots: 'all'` 的臨時 config（不碰 `doc/screenshots`）重拍 `combobutton／slider／rating／multislider／rangeslider／inputgroup` 六頁 gallery（`pw/fresh/`），與 `zkpreview/doc/screenshots/*-gallery.png` 逐像素比（`diffpng.js`，通道差 > 8 計 1 px）：

| 頁 | baseline 日期 | 差異 px | 位置 | 歸因（裁圖 `pw/crop-<page>-{exp,act}.png`） |
|---|---|---|---|---|
| combobutton | 09-11 | 11,395 | 全頁文字 + 停用 combobutton | **本批 #19**：停用 filled 的箭頭由深灰變成與標籤同色（裁圖可見）；其餘是全域字型漂移（下） |
| slider | 09-11 | 24,410（y 800–1000 佔 16,604） | knob 區 | **本批 #26**：兩個 knob 的數字輸入框底色與邊框消失，數字字型本身不變 |
| rating | 09-11 | 18,839 | 全頁文字 + custom icon 列 | **非本批**：星星像素不變（游標修正不影響像素）；`z-icon-bolt`／`z-icon-gift` 列的圖示字形不同是 `b2aa291a03`（iconSclass）／`606a0c25ba`（icon set 改 URL）之後的 baseline 未重生；其餘是全域字型漂移 |
| multislider | 09-11 | 9,463 | 只有文字 | **非本批**：thumb／軌道／marks 像素不變；全域字型漂移 |
| rangeslider | 09-11 | 18,857 | 只有文字 | 同上 |
| inputgroup | 10-07 | **0** | — | 已涵蓋、沒被動 |

全域字型漂移的對照（不在本批、baseline 同為 09-11 的頁）：`label` 2,069、`progressmeter` 8,486、`separator` 6,358、`groupbox` 26,404 px，同樣從 y=33 標題列開始、章節標題字重不同 —— 這是所有 09-11 baseline 共有的既有狀況（`606a0c25ba` 10-08 14:27 把字型改成 URL 載入之後／或 09-11 當時拍到 font-swap 中），落在 1% 容忍內，與本批無關；是否全面重生 baseline 由 Planner 決定。

## 結論

- **判定：** #18 J18-1／J18-2／J18-4(hover)、#19 J19-1、#24 J24-1／J24-2／J24-3、#26 J26-1／J26-2、#27 J27-1 全部通過；#22 仍已涵蓋；#25 沒被動。
- **保護項：** 全部成立（#18 (a)–(f)、#19 三項、#24 (a)–(e) 含新增的「會動的點仍 pointer」與「停用 auto」、#26 四項、#27 (a)–(d)）。
- **回歸：** 只有本批預期變動（combobutton 停用箭頭、slider knob 輸入框，含 `slider-tablet` 的 knob 部分）+ 兩個已知 tablet 失敗 + 一個非本批的過時 baseline（`grid-header-gallery`，標題字型載入問題，3/3 穩定重現，需重生）。沒有更新任何 baseline。
- **唯一未照字面通過的：J18-3 與 J18-4 的「按住」部分。** 按住箭頭時標籤 10.37（filled）、按住標籤時 toolbar 箭頭 5.98，這兩個數字在按住中 `blur()` 後都變成 0，證明它們是 `:focus-within` 的狀態層（mousedown 取得焦點），不是 `:active`；而 focus 層是 R6 明寫不動、保護項 (c) 要求維持的。排除 focus 層後：按住箭頭 → 箭頭 9.74／標籤 0，按住標籤 → toolbar 箭頭 0，完全符合判定意圖。RED 的「修正後可達成」推論沒有排除 focus 的貢獻，所以這是**方法與 R6 的矛盾**，不是 Generator 沒做到；純 CSS 下要讓按住時標籤 ≤ 1 就必須改 focus-within 規則（違反 R6／(c)）。
- 依「PASS = 所有判定通過」的字面規則，本報告給 **FAIL**，但量到的失敗值只有上述兩個 focus-within 的數字；建議 Planner 裁定「J18-3／J18-4 按住部分以排除 focus 層（按住中 blur）的值為準」，裁定後本報告的數據即可直接判 PASS，不需要再改程式碼或重跑。

GATE6-FINAL: FAIL (only J18-3 label 10.37 and J18-4-held toolbar arrow 5.98, both = :focus-within layer that R6 forbids touching; 0 after blur while held; every other judgment, protection and regression item passes)
