# Gate：第六批 #25 最終判定（GATE6B-FINAL）— slider 提示（slidetip）對齊

- **使用 8085**（共用的 zkpreview；本次沒有 kill、沒有重啟、沒有開第二個 port；整個 run 期間沒有中斷）。8085 伺服的是重啟後的新 build：`slider.css.dsp` 回應 3252 bytes（批次 6 最終判定時 3056）；`zk.wcs` 路徑 `/zkres/web/80d82a3c/zul/css/zk.wcs`。
- **日期：** 2026-10-08，Verifier。Playwright 1.59.1、Chromium 147、viewport 1280×900、DPR 2、注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`、量前游標移到 (2,2)、每個 slider 在重新載入的頁面上量。
- **方法：** jess-review-verification-plan.md（過程資料已清除）「第六批」#25 一節 + 檔尾「#25 更正與定稿」（定稿取代舊措辭：J25-1／J25-2 為判定，J25-3 為保護項）。RED 基準：[batch6-red.md](batch6-red.md) #25 小節（`batch6-red/red25.json`）。
- **腳本與原始輸出：** `gates/batch6b-final/`：`lib.js`、`final25.js`（複製自 `batch6-red/red25.js`，只改輸出檔名）→ `final25.json`／`final25.log`／`final25-s{0..4}-*.png`；`pixcheck25.js` → `pixcheck25.json`／`.log`（從 PNG 像素另算提示框中心，交叉驗證 DOM rect）；`probe25-others.js` → `probe25-others.json`／`.log`／`probe25-others-*.png`（三頁靜止時與按住時的 `.z-slider-popup`／tooltip class 盤點）；`diffpng.js`（複製自 batch6-final）+ `diff-fresh.log`、`pw/fresh/`（slider／multislider／rangeslider gallery 重拍）；回歸 `pw-regression.log`。
- **沒有修改**任何 theme／CSS／TS／Java／預覽頁／baseline 檔案，沒有看 diff 或 CSS 內容，沒有 commit；頁內只注入 lib.js 的停動畫規則。（`git status` 裡的 `slider.css` 修改是 Generator 的工作樹狀態，本次沒碰。）

## 結論摘要

| 項目 | 要求 | 結果 | 量到 |
|---|---|---|---|
| J25-1 水平提示框中心（default／sphere／scale） | 與 thumb 水平中心差 ≤ 1px，文字「5」「6」「50」「100」 | **通過** | 三個 mold × 四個文字 **全部 dx 0.23**（RED 1.50／1.65／4.97／7.41）；像素交叉驗證 dx 0.48／−0.02／−0.02 |
| J25-2 垂直提示框中心（default／sphere） | 與 thumb 垂直中心差 ≤ 1px | **通過** | 兩個 mold × 四個位置 **全部 dy 0.00**（RED 4.0）；像素交叉驗證 dy 0.00 |
| 保護 文字置中（J25-3） | ink 中心差 ≤ 1px（水平與垂直） | 通過 | Range ink 0／0 全部；像素 ink 水平 −0.18～0.04、垂直 0（= RED） |
| 保護 不重疊 | 框與 thumb rect 無交集 | 通過 | 20 個樣本 `overlapsThumb` 全 false；水平框底 = thumb 頂（gap 0）、垂直框左緣 131 ≥ thumb 右緣 130.77 |
| 保護 viewport 內 | | 通過 | 20/20 `inViewport` true |
| 保護 跟隨 thumb | ≥ 4 個位置 | 通過 | 每個 slider 4 個位置（drag10px／value5／value50／value100），框都貼著當時的 thumb |
| 保護 放開後消失 | `#zul_slidetip` 不存在 | 通過 | 5/5 `afterRelease.tipExists` false、curpos 100 |
| 保護 顏色／字級／padding | = RED | 通過 | `rgb(240, 244, 250)`、11px、`4px 8px`、line-height 20px、框高 28（= RED） |
| 保護 沒拖曳時無 `.z-slider-popup` | | 通過 | slider／multislider／rangeslider 三頁靜止時 `.z-slider-popup` 0 個、`#zul_slidetip` 無 |
| 另量 multislider／rangeslider tooltip | 位置不變、列出 class | 通過 | 用的是自己的 **`z-sliderbuttons-tooltip`**（不是 `.z-slider-popup`）；靜止與按住時水平 dx 0、垂直 dy 0；gallery 重拍與批次 6 最終判定的重拍 **0 px 差**（下） |
| 回歸 | 無新增差異 | 通過 | gallery 5/5、component-theming 107/107、hit-target 3/3、focus-scan 57 通過 47 skipped（= 批次 6） |

## J25-1／J25-2 — 每個樣本（`final25.json`；dx／dy = 提示框中心 − thumb 中心，CSS px）

| slider | 樣本 | 文字 | 提示框 rect（l,t,r,b） | thumb rect（l,t,r,b） | RED dx／dy | **現在 dx／dy** | ink（Range／像素） |
|---|---|---|---|---|---|---|---|
| S0 horizontal | drag10px | 6 | 105.59,169,128.41,197（寬 22.83） | 106.77,197,126.77,217 | 1.65／−24 | **0.23／−24** | 0,0／0,0 |
| S0 | value5 | 5 | 103.73,169,126.27,197（22.53） | 104.77,197,124.77,217 | 1.50／−24 | **0.23／−24** | 0,0／0,0 |
| S0 | value50 | 50 | 172.27,169,201.73,197（29.47） | 176.77,197,196.77,217 | 4.97／−24 | **0.23／−24** | 0,0／0,0 |
| S0 | value100 | 100 | 249.82,169,284.18,197（34.36） | 256.77,197,276.77,217 | 7.41／−24 | **0.23／−24** | 0,0／0,0 |
| S1 horizontal sphere | 四個樣本 | 6／5／50／100 | 與 S0 相同的 x，y 225–253 | y 253–273 | 1.65／1.50／4.97／7.41 | **0.23 全部**／−24 | 0,0／0,0 |
| S2 horizontal scale | 四個樣本 | 6／5／50／100 | 與 S0 相同的 x，y 281–309 | y 309–329 | 同上 | **0.23 全部**／−24 | 0,0／0,0 |
| S3 vertical | drag10px | 10 | 131,456,158.42,484 | 110.77,460,130.77,480 | 23.95／4.0 | 23.95／**0.00** | 0,0／0.04,0 |
| S3 | value5 | 5 | 131,451,153.53,479 | 110.77,455,130.77,475 | 21.50／4.0 | 21.50／**0.00** | 0,0／−0.02,0 |
| S3 | value50 | 50 | 131,496,160.47,524 | 110.77,500,130.77,520 | 24.97／4.0 | 24.97／**0.00** | 0,0／0.02,0 |
| S3 | value100 | 100 | 131,546,165.36,574 | 110.77,550,130.77,570 | 27.41／4.0 | 27.41／**0.00** | 0,0／−0.18,0 |
| S4 vertical sphere | 四個樣本 | 10／5／50／100 | 與 S3 相同的 x，y +133 | y +133 | 同 S3 | 同 S3：**dy 0.00 全部** | 同 S3 |

- 水平：RED 時提示框左緣 = thumb 左緣，偏差 = (提示寬 − 20)/2 隨位數變；現在三種寬度（22.53／29.47／34.36）的中心差都是 **0.23**，即偏差不再取決於提示寬度。提示框底仍 = thumb 頂（dy −24 不變，與 RED 相同的垂直位置）。
- 垂直：RED 時提示框頂 = thumb 頂（dy 4.0 = (28−20)/2）；現在 dy 0.00。水平位置不變（左緣 131 = thumb 右緣，dx 21.5–27.41 與 RED 完全相同）。
- 20 個樣本的 `position`／`left`／`top` inline 值與 RED 相同（`107px/169px` 等），即 JS 的 `position(btn, 'before_start'|'end_before')` 基準沒有變，偏移是 CSS 補的；量到的 computed `transform` 為 `none`（只記錄，不是判定依據）。
- **像素交叉驗證（`pixcheck25.json`）：** 從 PNG 找提示框底色 (45,55,72)（±12）的 bounding box 中心，與由裁圖原點換算的 thumb DOM 中心比：水平「5」0.48、「50」−0.02、「100」−0.02（default／sphere／scale 三個 mold 的「100」都 −0.02）；垂直 S3「5」「50」「100」與 S4「100」都 0.00。DOM rect 與畫出來的框一致。

## 保護項

- **文字置中：** 20 個樣本 Range ink 中心差 0／0；像素 ink（框內距邊 4px、與底色 ΔE ≥ 20）水平 −0.18～0.04、垂直 0 —— 與 RED 的數字逐一相同。
- **不重疊／viewport：** `overlapsThumb` 20/20 false；`inViewport` 20/20 true。
- **跟隨：** 每個 slider 按住後經 4 個位置（curpos 6→5→50→100，垂直 10→5→50→100），每個位置的提示框都貼著當時的 thumb（上表 rect），`#zul_slidetip` 全程存在。
- **放開：** 5/5 `tipExists` false，curpos 100。
- **樣式：** color `rgb(240, 244, 250)`、font-size 11px、padding `4px 8px`、line-height 20px、框高 28 —— 全部 = RED。
- **沒拖曳時：** `probe25-others.json`：slider／multislider／rangeslider 三頁靜止時 `.z-slider-popup` 0 個、`#zul_slidetip` 不存在；slider 頁按住 thumb 時恰好 1 個，放開後 0 個。

## multislider／rangeslider 的 tooltip（`probe25-others.json`）

- 兩個 widget 的 tooltip 是 **`div.z-sliderbuttons-tooltip`**（multislider 頁 18 個、rangeslider 頁 14 個），不是 `.z-slider-popup`；按住 thumb 拖曳時也沒有 `#zul_slidetip`／`.z-slider-popup`（0 個），只是把自己的 `z-sliderbuttons-tooltip` 變 visible（`disp block visible`，28→32.2 高，自身放大）。
- 位置：水平 widget 每個 tooltip 中心與自己 thumb 中心 **dx 0**、dy −32（按住時 −36.8，放大造成）；垂直 widget **dy 0**、dx −32.8～−38.7（在左側）；靜止與按住時相同。它們自帶 `translate(-50%)` 式的 transform（`matrix(1,0,0,1,-14.5,0)` 等），本來就置中。
- **像素證據（`diff-fresh.log`，`diffpng.js` 通道差 > 8 計 1 px）：** 以臨時 config（snapshotDir 指到 `pw/fresh/`，不碰 `doc/screenshots`）重拍三頁 gallery，與批次 6 最終判定的重拍（`batch6-final/pw/fresh/`）比：**slider 0 px（byte-identical）、multislider 0 px、rangeslider 0 px** → #25 之後靜止畫面沒有任何像素變動。與 `doc/screenshots` baseline 比：slider 0 px（baseline 10-08 15:47 已重生，byte-identical）、multislider 9,463 px、rangeslider 18,857 px —— 與批次 6 最終判定報告的數字**完全相同**（09-11 baseline 的全域字型漂移，非本批）。
- 記錄（不在本批）：multislider／rangeslider 頁「Always-visible tooltips」那組的 tooltip 在靜止時 computed `visibility: hidden`（與其他組相同），與 #25 無關，只記。

## 回歸（Playwright，8085，唯讀比對，無 `--update-snapshots`；`pw-regression.log`）

| project | 範圍 | 結果 | 與批次 6 最終判定比 |
|---|---|---|---|
| `gallery` | `-g "gallery (slider\|multislider\|rangeslider\|combobutton\|rating)( \|$)"`（`--list` 確認恰 5 個） | **5/5 通過** | 批次 6：slider／multislider／rangeslider／combobutton／rating 都通過；相同 |
| `component-theming` | 全跑 | **107/107** | 107/107，相同 |
| `hit-target` | 全跑（slider／multislider／rangeslider 各一） | **3/3** | 3/3，相同 |
| `focus-scan` | 全跑 | **57 通過、47 skipped** | 57／47，相同 |

- 已知失敗 `slider-tablet`、`calendar-tablet` 屬 `tablet` project，不在本次指定範圍，不計。
- slider gallery／knob：本次重拍與批次 6 最終判定的重拍 byte-identical，與（已重生的）baseline byte-identical；knob 區（y 800–1000）0 px 差 → 沒有新增差異。
- 沒有更新任何 baseline。

## 結論

- **判定：** J25-1 通過（三個水平 mold × 四個文字，中心差全部 0.23 ≤ 1，RED 1.50–7.41）；J25-2 通過（兩個垂直 mold × 四個位置，中心差全部 0.00 ≤ 1，RED 4.0）。
- **保護項：** 全部成立（文字置中、不重疊、viewport、跟隨、放開消失、樣式不變、沒拖曳時沒有 `.z-slider-popup`）。
- **其他 widget：** multislider／rangeslider 用 `z-sliderbuttons-tooltip`，位置 dx 0／dy 0 不變，gallery 與批次 6 最終判定 0 px 差。
- **回歸：** 指定範圍全部通過，與批次 6 最終判定相同，沒有新增差異。

GATE6B-FINAL: PASS (J25-1 dx 0.23 for "5"/"6"/"50"/"100" on default/sphere/scale, RED 1.50–7.41; J25-2 dy 0.00 on default/sphere, RED 4.0; pixel cross-check |dx| ≤ 0.48, dy 0; all protections hold; multislider/rangeslider use z-sliderbuttons-tooltip, 0 px change; gallery 5/5, component-theming 107/107, hit-target 3/3, focus-scan 57 passed 47 skipped)
