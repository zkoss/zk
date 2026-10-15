# zkdemo B9 Marble 修正：獨立驗證（2026-10-15）

驗證者：獨立 agent（未參與實作；對原始碼唯讀，未 commit）。依據：`doc/zkdemo-b9-impl-brief.md`、`doc/zkdemo-b9-impl-report.md`（其主張視為待驗證）、`doc/zkdemo-b9-triage.md`、`doc/zkdemo-b9-jira-drafts.md`、`.claude/skills/marble-theme/reference/decision-principles.md`。

## 環境

- 伺服器 `http://127.0.0.1:8087`（`b9/zk/zkpreview`，Jetty PID 86435，holder 85485/85487/85488；未停止、未重啟、未重建 jar；驗證結束時四個 PID 仍在）。未碰 8085，未用 pkill／killall。
- 確認 8087 送出的是待驗證的 commit：jar 內的 `pdfviewer.css`、`fisheye.css`、`rangeslider.css`（zkex）與 `multislider.css`（zkmax）與 `zkdemo-b9` HEAD 逐位元組相同；送出的 `zk.wcs` 內 `--zk-fisheye-fg:var(--zk-color-inverse-on-surface)`（zk `9ffc805583`）。
- Playwright Chromium，2x DPR，全部用 `page.mouse`。「還原」量測是在同一頁面把 `zk.wcs` 內對應規則的宣告改回 parent commit（`fe5649586` / `523f64b14d`）的內容（CSSOM 逐條替換，不是另加覆寫層），用來證明每項檢查在修正被拿掉時會失敗。
- 探針頁 `b9v-slider.zul`、`b9v-fish.zul`、`b9v-fishscroll.zul`、`b9v-captcha.zul`、`b9v-captcha2.zul`、`b9v-col.zul` 只放在 `b9/zk/zkpreview/build/inplaceWebapp/`，用完已刪除，六個 URL 皆回 404。
- 腳本與 A 部分截圖：`$V = /private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/511f070c-8726-4e44-9e03-65ce2a98cef5/scratchpad/verify`（`$V/shots/`）。B 部分截圖：`/Users/hawk/Documents/workspace/ZK10/zk/doc/zkdemo-b9-core-evidence/`（以下簡稱 `ev/`）。
- 兩個 worktree 驗證後 `git status` 只有既有的 `node_modules` symlink（untracked），無修改、無 staged。

## Part A：修正驗證

### A1 Z1 pdfviewer（zkcml `f99b40755`，`/pdfviewer.zul`，11 個 text span）

| # | 檢查 | 結果 | 數值（修正後 ／ 還原後） | 證據 |
|---|---|---|---|---|
| A1.1 | span computed `color` 透明、`position: absolute` | PASS | 11/11 `rgba(0, 0, 0, 0)`、11/11 `absolute` ／ 還原：11/11 `rgba(0, 0, 0, 0.87)`、11/11 `static` | `$V/a1-pdf.js` |
| A1.2 | 其他宣告 | PASS | layer `overflow: hidden`、`line-height: 13px`（=1）；span `white-space: pre`、`cursor: text`、`transform-origin: 0px 0px` ／ 還原：`visible`、`20px`、`normal`、`auto` | 同上 |
| A1.3 | 頁面上無灰字（文字層可見 vs `visibility:hidden` 的 page 截圖逐像素比對，門檻 >8） | PASS | 0 / 2,007,328 px 不同 ／ 還原：3,765 px 不同（max Δ 45），左上角出現灰色重複文字 | `$V/shots/a1-cur-page.png`、`$V/shots/a1-old-page.png` |
| A1.4 | `::selection` 可見 | PASS | computed `rgb(55, 111, 208)`（primary）；選取後白底變 `rgb(215, 226, 246)`，對白底 1.30:1 ／ 拿掉此規則：`rgb(239, 246, 254)`，1.09:1（IceBlue 的 `blue`×0.2 約 1.54:1） | `$V/shots/a1-sel-cur.png`、`a1-sel-nosel.png` |
| A1.5 | 0° 拖曳選取與字跡對齊 | FAIL（原因不在本 commit，見 B4） | 真實拖曳選到 `1. ✅ Producing a test link (Recommended)`。左緣：`1.`、`✅`、`Producing`、`(Recommended` 的 span 左緣距字跡起點 ≤1.5px。但 span 帶高 13px（326.2–339.3）而字跡為 325.0–344.5；span 寬只有字跡的約 72%（13px／18px），`(Recommended` 選取帶止於 "Recommen"。注入 `.z-pdfviewer-page{--scale-factor:1}` 後 span 聯集 [414.3, 326.1, 758.3, 344.1] 對字跡 [415.9, 325.0, 757.9, 344.5]，四邊差 ≤1.6px。殘差全部來自 `--scale-factor` 未定義 | `ev/b4-pdfviewer-rot0-selection.png`、`ev/b4-pdfviewer-rot0-selection-with-scale-factor-1.png` |

工程師回報 §2 的數字（span `rgba(0,0,0,0)`、`::selection` `rgb(55,111,208)`、`--scale-factor` 未定義）與本次量測一致。

### A2 Z6 fisheye（zkcml `563ea56b5` + zk `9ffc805583`；探針 `itemWidth=60`、`itemMaxWidth=120`，hover 第 2 項）

| # | 檢查 | 結果 | 數值 | 證據 |
|---|---|---|---|---|
| A2.1 | item／image／label 皆 `position: absolute` | PASS | 8 組皆 `absolute,absolute,absolute` ／ 還原：`absolute,static,static` | `$V/a2-fish.js` |
| A2.2 | 標籤在指定邊、在 item 外（8 組 orient/attachEdge/labelEdge，真實滑鼠 hover） | PASS 8/8 | 水平 top/top、top/bottom、bottom/top、bottom/bottom，垂直 left/left、left/right、right/left、right/right：label 與 item 指定邊的間距 −0.2～0.0px，置中偏差 0.1px（水平）／0.6px（垂直）。例：水平 attach=bottom label=top，item [270,140,390,260]、label [279,119,381.2,140.2] ／ 還原：8/8 不在指定邊，label 在 item 內底部（[279,244.4,381,260]），垂直時置中偏差 52.2px | `$V/shots/a2-cur-*.png`（8 張）、`$V/shots/a2-old-*.png`（8 張），例 `a2-cur-horizontal-bottom-top.png` 對 `a2-old-horizontal-bottom-top.png` |
| A2.3 | 標籤文字對比 ≥4.5:1（量在第一個不透明背景，即 label 自身 `rgb(45, 55, 72)`） | PASS | `rgb(240, 244, 250)` on `rgb(45, 55, 72)` = 10.86:1。只套 zkcml commit、不套 zk `9ffc805583`（注入 `--zk-fisheye-fg:var(--zk-color-on-surface)`）：`rgba(0,0,0,0.87)` on 同底 = 1.68:1 → 會失敗 | `$V/a2-token.js` |
| A2.4 | 標籤靜止時隱藏、移開後恢復 | PASS（但不受本 commit 影響） | 8 組 hover 前 3/3 `display: none`，移開後 3/3 `none`、item 回到 60/60/60。還原舊 CSS 時同樣 3/3 `none`：隱藏來自 mold 的 inline `display:none`，CSS 的 `display: none` 是冗餘的保險，這一項不能證明修正 | 同 A2.1 |
| A2.5 | V6：外觀同 `.z-slider-popup` | PASS | 兩者皆 `inverse-surface` 底、label-small、`shape-corner-extra-small`、`elevation-1`、`4px 8px`；label 實測 11px | 送出的 `zk.wcs` |

觀察（不影響判定，O10）：rangeslider tooltip 的背景與文字都走 `--zk-rangeslider-tooltip-{bg,color}` knob；fisheye label 只有文字色走 knob，背景直接用 `--zk-color-inverse-surface`。

### A3 rangeslider／multislider（zkcml `4cd1bceb1`、`420927b0b`；水平 500px、垂直 300px，`step=10`、`tooltipVisible`、預設刻度）

超出量＝元素 bounding box 超出元件 border box 的 px（>0.5 才算），列每邊最大值。

| # | 檢查 | 結果 | 修正後 | 還原後 | 
|---|---|---|---|---|
| A3.1 | rangeslider 水平：刻度標籤、提示框、按鈕都在 box 內（初始與兩端） | PASS | 0 個元素超出（初始與拖到 0／100 後） | 初始 10 個超出：上 34（tooltip）、下 28（label）、右 9.2；兩端 12 個：左 11.5、右 17.2（tooltip） |
| A3.2 | rangeslider 垂直 | FAIL（只有右側 tooltip） | 刻度標籤與按鈕 0 超出；右側 tooltip 超出 7.8px（初始）／12.4px（兩端） | 上下 14（tooltip）、上下 10（label）、右 11.8／16.4 |
| A3.3 | multislider 水平 | PASS | 0 超出（初始與兩端） | 初始上 32、下 26；兩端左 11.5、右 17.2 |
| A3.4 | multislider 垂直 | FAIL（只有右側 tooltip） | 標籤與按鈕 0 超出；右側 tooltip 1.8px（初始）／6.4px（兩端） | 下 21（label）、下 26（tooltip）；右側 tooltip 1.8／6.4（與修正後相同） |
| A3.5 | `page.mouse` 拖曳：step 對齊、min／max 夾住 | PASS（四個元件，修正前後相同） | 起始 [20,80] → 起點拖到 rail 47% → [50,80] → 終點拖到 120% → [50,100] → 起點拖到 −20% → [0,100] | 同左（還原後完全相同） |
| A3.6 | multislider contract v1：垂直按鈕中心 = track.top + X%·track.height | PASS | mv、rv 皆 20%→148.4/148.4、80%→311.6/311.6（差 0） | — |
| A3.7 | 版面成本 | 記錄 | rangeslider 水平 box 500×24 → 500×86（+62），rail 500 → 464；垂直 50×300 → 54×300，rail 300 → 272。multislider 水平 500×28 → 500×86（+58），rail 500 → 464；垂直 70×300 不變，rail 300 → 272。上方 44px 一律保留（沒有 `tooltipVisible` 的 slider 也一樣） | 與工程師回報 §4 一致 |

A3.2／A3.4 的 FAIL 是依派工的嚴格條件（「每個 tooltip 都在 border box 內」）。commit 與檔內註解明寫「垂直時右側 tooltip 依設計在 box 外」；修正讓 rangeslider 垂直的超出從 11.8／16.4 降到 7.8／12.4，multislider 不變。相關的開啟中 Jira：ZK-6192「rangeslider / multislider vertical tooltip cut off」。要不要把右側也納入 box 是範圍問題，留給 PM 決定。
截圖：`$V/shots/a3-cur-initial.png`、`a3-cur-ends.png`、`a3-old-initial.png`、`a3-old-ends.png`。另觀察到：垂直且 `tooltipVisible` 時，按鈕在 0／100 的 tooltip 會蓋住端點刻度標籤「0」「100」（`a3-cur-ends.png`），不在本次範圍。

### A4 三個 zk commit

| # | 檢查 | 結果 | 依據 |
|---|---|---|---|
| A4.1 | `9ffc805583` 是 component token 的預設值，不是新的公開 token，也不是 global role token（§4 E4） | PASS | `--zk-fisheye-fg` 在 `523f64b14d` 已存在（`_component-theme.css:883`、`doc/spec/component-theme-variables.md:1250`），只改預設 `on-surface` → `inverse-on-surface`；`tokens/_colors.css` 等角色 token 未動。依 §3「Public `--zk-*` token：保留 knob，只改預設」自行決定，非 E4 |
| A4.2 | `9ffc805583` 是否必要 | 必要 | 沒有它，label 文字對比 1.68:1（A2.3）。`component-theming.spec.ts:1282` 斷言區域覆寫 `--zk-fisheye-fg` 會改 label 顏色，所以 label 必須繼續讀 knob，不能在 fisheye.css 直接寫死顏色 |
| A4.3 | `4c40be8bca` 只改 3 張 PNG | FAIL（範圍正確，訊息有誤） | `git show --stat`：只有 `zkpreview/doc/screenshots/{fisheyebar,multislider,rangeslider}-gallery.png`。實際尺寸：**multislider 1280×1348 → 1280×1638（+290 = 5 個水平 multislider × 58）**、**rangeslider 1312 → 1622（+310 = 5 個水平 rangeslider × 62）**。commit 訊息與工程師回報 §5 把兩者的數字對調了。fisheyebar 同尺寸，icon 群 bbox [40,340,500,400] → [40,332,500,392]：上移 8px、水平 0px；commit 訊息寫「8px higher/left」，「left」不成立。三張的改變都可由版面修正解釋（水平 slider 變高、bar 拿掉 padding） |
| A4.4 | gallery 基準與目前畫面一致 | PASS | 在 8087 以 repo config 跑 `gallery-scan -g "gallery (fisheyebar\|multislider\|rangeslider\|pdfviewer)$"`：4 passed（容忍值 `maxDiffPixelRatio` 0.002）；跑完 `git status` 無新 PNG。`component-theming -g "fisheye\|pdfviewer\|rangeslider\|multislider"`：8 passed |
| A4.5 | `937b68bf3c` contract 與新 CSS 一致 | PASS | fisheyebar c1：bar 背景 `rgba(0,0,0,0)`；c2：item `transform: none`、無 transition（computed `transition: all`＝0s）；c3：label 11px；c4：三者 `absolute`、label 靜止 `none`；c5：`inverse-surface` = `rgb(45,55,72)`、文字 `rgb(240,244,250)`。pdfviewer 設計段落新句子與 A1.1–A1.4 量測相符，c23 opacity 0.2 仍成立。rangeslider.md／multislider.md 沒有與新 padding 矛盾的斷言 |

附註：`doc/contracts/fisheyebar.md` 的「DOM key selectors」仍寫 `.z-fisheye-img`，實際 class 是 `.z-fisheye-image`（`937b68bf3c` 之前就有，c4 已用正確名稱）。

### A5 Z8 PNG（`doc/zkdemo-b9-z8/`）

| # | 檢查 | 結果 | 依據 |
|---|---|---|---|
| A5.1 | 檔名與內容相符 | PASS | 開了 `current-frame-true-on-white`（圓角灰外框內再有引擎畫的青色框，雙框可見）、`current-frame-false-on-white`（只有圓角灰框）、`optA-frame-true-on-white`（只剩引擎的方角青框）、`optA-frame-false-on-surface-container-low`（無任何框，淺底）、`optC-frame-false-on-surface-container-low`（方角灰框）、`sheet-z8-captcha-comparison.png`（12 格標題與各檔一致，每列同一次載入的字形）。單張 496 px 寬 = (200+24×2)×2，符合 2x DPR 與 24px 裁切 |

## Part B：核心 bug 重現（Jira 證據）

| # | 項目 | 結果 | 數值 | 證據 |
|---|---|---|---|---|
| B1 | Z13 fisheyebar 捲動後放大被取消 | 重現 | viewport 1280×800，`document.body.offsetHeight` = 800，文件高 3260，`zk(BODY).revisedOffset()` = [0,0]。fisheyebar B 在捲動 1320px 後的 mouseout `pageY` = 1695 > 800。滑鼠橫掃 3 個 item（21 步）：A（第一個 viewport）0 次取消，最大 item 寬序列 `64,71,79,86,94,99,94,90,91,…`；B（捲動後）**10 次 mouseout、10 次 `_calc(-1,-1)`**，在每次跨越元素邊界時全部縮回 50（序列 `64,50,50,86,94,99,94,50,91,…,50,…`），下一次 mousemove 又放大，表現為閃爍；`jq(document).off('mouseout', w._onBodyOut)` 後 B 為 0 次取消，序列與 A 相同。同一步（第 7 步）的 item 尺寸：B 正常 `50x50,50x50,50x50`，B 移除 handler `90x90,86x86,61x61`，A `90x90,86x86,61x61` | `ev/b1-z13-B-scrolled-normal.png`、`ev/b1-z13-B-scrolled-onBodyOut-detached.png`、`ev/b1-z13-A-first-viewport-normal.png` |
| B1 補充 | 兩主題共有？ | 依原始碼成立（O2） | `Fisheyebar.ts` 與 master 無差異；IceBlue `norm.less`（master）有 `html, body { height: 100% }`（在 `browserDefault` 未設定時）。未在 IceBlue 執行 | — |
| B2 | JHLabsCaptchaEngine 窄圖切字 | 右緣：重現；左緣：也看到 | 每種尺寸 72 個樣本（6 次載入 × 12，`frame=false`、`noise=false`）。已知值測試：`90×40` `ABCDEFG` 每張只看得到 ABCDE（F、G 不見）；`120×50` `ABCDEFGHJK` 只到 F 或半個 G；`150×40` `ABCDEFGHJ` 只到 H 或半個 I；`200×50` `ABCDE`（預設尺寸）6/6 完整。最後一欄被引擎的 `drawRect` 蓋掉，所以以 x = W−2 有 ≥2 個墨點為「碰右緣」：90×40×7 11/72、120×50×10 12/72、100×40×8 7/72、150×40×9 4/72、200×50×5 1/72、200×50×7 1/72。**左緣**：窄圖 x=1 有墨點 72/72（90×40×7、120×50×10、100×40×8）、71/72（150×40×9），截圖可見首字左側筆畫被切（例：「ABCDE」的 A 只剩右半）。原因推論：`textwidth > width` 時 `left = 0`，而 `distortion()` 的 `RippleFilter` 以 `XAmplitude 5.6f` 在固定寬度的圖內水平位移像素，會把 x≈0 的筆畫推出左緣。草稿「左緣不可能」只看了 `drawText`，沒有考慮 ripple | `ev/b2-captcha-known-values-load1.png`、`ev/b2-captcha-known-values-load2.png`、`ev/b2-captcha-overview-load1.png`、`ev/b2-captcha-{90x40x7,120x50x10,100x40x8,150x40x9,200x50x5,200x50x7}-right-edge.png` |
| B3 | Z5 columnlayout 執行中 `setWidth` | 重現 | 30%／70%、`width="900px"`：columnchildren 270／630（inline `270px`／`630px`）。真實 click 按鈕 `cl.setWidth("500px")` 後：columnlayout 500，columnchildren 仍 270／630，第二欄被 `overflow:hidden` 裁掉（只看得到約 230px） | `ev/b3-z5-columnlayout-before.png`、`ev/b3-z5-columnlayout-after-setWidth-500px.png` |
| B4 | pdfviewer 旋轉：文字層是否跟著轉 | 不跟著轉（O2，兩主題共有） | 以真實滑鼠按 toolbar「Rotate Clockwise」後：page 595.3×841.9 → 841.9×595.3，canvas 字跡變直排（ink bbox x 928.6–948.1、y 285.5–627.5），文字層 `data-main-rotation="90"` 但 computed `transform: none`，span 仍橫排在上方（聯集 [320.9, 292.8, 803.5, 305.8]），span inline style 旋轉前後相同（`left: 12.1%; top: 13.57%; font-size: calc(var(--scale-factor)*18.00px)…`）。拖曳選取整行仍選到全文，但選取帶在頁面上方橫向，與字跡無關。文字層到 document 的祖先鏈上沒有任何 `--scale-factor`（computed 空值） | `ev/b4-pdfviewer-rot90-selection.png` |
| B4 判定 | 共用核心或 Marble 限定 | O2（共用） | `git diff master -- …/Pdfviewer.ts`：0 行差異；`pdf.mjs` 與 master 相同（pdf.js 4.10.38）。master 的 LESS 與 Marble 的 CSS 都沒有 `scale-factor` 或 `data-main-rotation`（`git grep` 只在 `ext/pdf.mjs`、`pdf.worker.mjs` 命中）。pdf.js 4.x 的 `setLayerDimensions` 與 span 的 `font-size` 都依賴宿主設定 `--scale-factor`，旋轉依賴 pdf_viewer.css 的 `[data-main-rotation]` transform，兩者 ZK 都沒提供。Marble 文字層 CSS 現在與 IceBlue `pdfviewer.less:53-73` 等價（只差 selection 顏色），所以 IceBlue 在同一份 JS 下行為相同。`--scale-factor` 依縮放而變，屬執行期數值（O3：只能由 JS 設定），不是主題能補的 | — |

## 每個 commit 的總判定

| repo | commit | 判定 | 說明 |
|---|---|---|---|
| zkcml | `f99b40755` Z1 pdfviewer | PASS | A1.1–A1.4 全過且還原會失敗。A1.5 的殘差由 `--scale-factor` 缺失造成（B4，O2），不是此 commit 的缺陷 |
| zkcml | `563ea56b5` Z6 fisheye | PASS（需搭配 zk `9ffc805583`） | 8/8 組合標籤在指定邊外側；沒有 `9ffc805583` 時文字對比 1.68:1，不可單獨採用 |
| zkcml | `4cd1bceb1` rangeslider | PASS，附一個開放點 | 水平全部在 box 內；垂直的刻度與按鈕在內，右側 tooltip 仍超出 7.8／12.4px（依註解為設計，原本 11.8／16.4）。拖曳數值不變。成本：水平 +62px 高，rail −36px |
| zkmax（zkcml） | `420927b0b` multislider | PASS，附同一個開放點 | 水平全部在 box 內；垂直右側 tooltip 1.8／6.4px（修正前後相同）。成本：水平 +58px 高 |
| zk | `9ffc805583` fisheye fg token | PASS（必要） | 既有 component knob 的預設值，非 E4 |
| zk | `4c40be8bca` gallery 基準 | PASS（內容），commit 訊息需更正 | 只動 3 張 PNG 且都可由版面修正解釋；訊息中 multislider／rangeslider 的高度對調，fisheyebar 只上移 8px、沒有左移 |
| zk | `937b68bf3c` contracts | PASS | 斷言與實測一致；`.z-fisheye-img` 是既有錯字，未在範圍內 |

工程師回報中與實測不符之處：§5 gallery 尺寸（rangeslider／multislider 對調）、`4c40be8bca` 訊息的「left」。其他數值（A1、A2、A3 的前後量測與拖曳序列）與本次獨立量測一致。
