使用 8085（C 線，批次 14 FINAL）

# Gate：第十四批最終判定，CSS follow-ups：toast info 圖示對比、notification 與 toast 同色階、水平 navbar 巢狀縮排、menu 列 image／iconSclass 文字起點（P1 P2 P3 P4）＋回歸

- **伺服器：** 8085，java pid 34132（2026-10-09 **22:24:57** 啟動；`zkpreview/build/gretty_ports.properties` 22:24）。**沒有 kill、沒有重啟**（交辦說 21:50 重啟，實際上 8085 在 22:24:57 又重啟過一次，伺服的仍是本批 build，見新鮮度）。
- **新鮮度（伺服 ≠ RED 記錄 → 是新 build）：** (1) `js/zul/menu/css/menu.css.dsp` 伺服 md5 `b1ed3cf3…`（RED `939331cf…`）＝ `zul/build/resources/main/web/js/zul/menu/css/menu.css.dsp`；`js/zkmax/nav/css/nav.css.dsp` 伺服 `aff1a9b3…`（RED `38b767a2…`）＝ `../zkcml/zkmax/build/.../nav.css.dsp`；(2) `zul/css/zk.wcs` 200／543,055 bytes，md5 `7f07271a…`（RED `a5a526ee…`／542,919）；RED 記錄的 `.z-toast-info .z-toast-icon{color:var(--zk-toast-accent)}` 與 `.z-notification-info .z-notification-left{border-right-color:var(--zk-color-status-info)}` **都不在**伺服的 zk.wcs；現在伺服的是 `.z-toast-info .z-toast-icon{color:color-mix(in srgb, var(--zk-toast-accent) 80%, var(--zk-color-on-surface))}`、`.z-notification-info .z-notification-left{border-right-color:var(--zk-color-surface-container-highest)}`；`zul/src` 與 `zul/build` 的 `toast.css`／`notification.css` md5 相同（`479f8b12…`／`c7e43939…`）。整檔 containment 比對因 wcs 打包時改寫 `@layer` 區塊而不成立，改以伺服規則文字核對（上列）。
- **日期：** 2026-10-09，Verifier（Fable）。Playwright 1.59.1、Chromium 147.0.7727.15、viewport 1280×900、deviceScaleFactor 2、`ignoreDefaultArgs:['--hide-scrollbars']`；注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`、游標停 (2,2)；每個狀態重新載入；forced-colors 用 `newContext({forcedColors:'active'})`＋`emulateMedia`。
- **方法：** lines/line-c-plan.md（過程資料已清除） 第六節，第七節的定稿覆寫第六節（P3 量「對齊同層項目」＋第三層用 DOM padding-left；P4 圖示中心 ≤ 0.5px；J-P2-3 取三角形內部、離 content 邊 ≥ 2px；P1 用像素 ink）。ΔE 一律 CIE76（lib.js）；對比用 WCAG 2.x 相對亮度；位置用像素 ink（與底色 ΔE ≥ 8 的像素外接矩形，0.5px 粒度）。
- **腳本與原始輸出：** `gates/batch14-final/`：`lib.js`、`m14.js`（複製自 batch14-red，未改）、`final-p1p2.js/.json/.log`＋`p1-toast-*.png`、`p12-*-forced-*.png`、`p2-notification-*.png`、`p2-pointer-{info,warning,error}-{left,right,up,down}.png`（＝RED 的 `red-p1p2.js` 改輸出檔名）、`final-p3.js/.json/.log`＋`p3-*.png`、`final-p4.js/.json/.log`＋`p4-*.png`、`probe-p4-nested.js/.log`、`probe-p4-nested2.js/.log`、`probe-p4-colorbox.js/.log`＋`final-p4-*.json`＋`p4n-*.png`、`p4c-*.png`、`pw/`（回歸 log：`summary.log`、各 project `.log`）、`reg-diff.js`＋`reg-diff/`（每張失敗截圖的 expected｜actual｜diff 裁圖與 `reg-diff.json`）。Playwright `--output` 全指到 scratchpad，沒有碰 `test-results`，沒有跑 `forced-colors-gallery`，沒有 `--update-snapshots`。
- **沒有修改**任何 theme／CSS／TS／Java／預覽頁／spec 檔案，沒有看 diff、沒有看 CSS 原始碼（只讀了 gen 報告第 25／29 行確認 P4 預期），沒有 commit。頁內只注入停動畫規則、（P1 旋鈕）`:root{--zk-toast-accent:#6750a4}`；P2 箭頭用 `zul.wgt.Notification.show(...)`；P4 巢狀 `z-menu`＋image 的例子是在瀏覽器裡用 `new zul.menu.Menu({label,image})` 接到 menubar1 的 Project popup（預覽頁沒有這種例子），不是改檔。

## 結論摘要

| 項目 | RED（修前） | FINAL（修後） | 判定 |
|---|---|---|---|
| J-P1-1 toast info 圖示 ink 對底色 ≥ 4.5 | (0,127,171) 對 (224,232,244) **3.68** | ink (6,107,143)（480 px 全同色）對 (224,232,244) **4.85**；live `Toast.show` 同 4.85 | **通過** |
| P1 (a) 旋鈕 | ΔE 44.89 | 注入 `--zk-toast-accent:#6750a4` → ink (89,70,138)，ΔE **38.06** ≥ 10；底色／warning／error／close ΔE 0 | **通過** |
| P1 (b) 其他不變 | — | warning (122,54,30) 6.65、error (127,0,10) 7.75 ΔE 0；close ink 8×8 @ 同位、ΔE 0；三底色 ΔE 0；圖示 ink 19×19 @ (48.5,200.5) 等三張 ±0；root／content 矩形相同；`font-size 20px`、close opacity .7 相同 | **通過** |
| P1 (c) forced-colors | 491／491／566 px 黑 21:1 | 491／491／566 px、(0,0,0) 對白 21:1 | **通過** |
| J-P2-1 notification 底色＝toast 底色 ΔE ≤ 2 | 5.07／11.97／13.54 | info (224,232,244)、warning (255,215,197)、error (254,205,199) 與 toast **ΔE 0／0／0** | **通過** |
| J-P2-2 notification info 圖示 ≥ 4.5 | 3.88 | (6,107,143) 對 (224,232,244) **4.85**（warning 6.65、error 7.75） | **通過** |
| J-P2-3 四向箭頭＝content 底色 ΔE ≤ 2，無陰影透出 | left 52.64／79.12／71.58；info 其餘 5.07；warning／error right 2.11、down 4.22 | 12 格全部 **ΔE 0**，三角形內部最差像素 ΔE 0（nInner 306／270／340／270）；computed 邊色為不透明的 `rgb(224,232,244)`／`oklch(0.92 …)`／`oklch(0.89 …)`（無 alpha） | **通過** |
| P2 保護項 | — | 有箭頭時 content 260×43.5、箭頭盒 20×20、root padding 12px 同側、圖示 ink 19×19、close ink 位置與尺寸 12 格全同；靜態三張 root／content 矩形同、text ink 盒同、`color rgba(0,0,0,.87)` 同；forced-colors 圖示 491／491／566 px 黑 21:1；`variant-backgrounds-are-opaque`：見回歸 | **通過** |
| J-P3-1 水平下拉：巢狀群組標題 ink 左緣＝同層 navitem ink 左緣 ±1 | 標題 ul+42.39 vs 項目 ul+68.89（−26.5） | Settings 標題 **ul+68.39**（padding-left 42px）vs Reply／Reply all／Inbox／Edit **ul+68.89** → 差 **0.5px**；圖示 ink 左緣 467.5–468.5（同） | **通過** |
| J-P3-1 第三層（DOM） | L2 項目 padding-left 42px（= L1） | Edit profile／Keyboard shortcuts／Change password `padding-left` **68px** = 42 + 26 | **通過** |
| P3 保護項 | — | 頂列六項位置、尺寸逐一相同；下拉列高 40；Reply hover 層 423.6–609.6（= ul 滿寬，色 (210,213,219)）；**垂直** navbar 全部列（padL／列高／寬／text／icon ink）與 RED **完全相同**（69／68.5／95，+26）；**popup 模式**完全相同（43／42.5／69，列高 36）。**附帶變化：** 水平下拉 ul 寬 **180 → 186.375**（標題縮排後最寬列變長，auto 寬跟著長；不在保護項內，記錄） | **通過** |
| J-P4-1 文字 ink 互差 ≤ 1 | 2.5px | Project popup：New +44、Open +43.5、Save +43.5、Save As... +43.5、Exit +44、Restart +44 → **0.5px** | **通過** |
| J-P4-1 圖示 ink 中心互差 ≤ 0.5 | 1.0px（73.0 vs 74.0） | 六列全部 **cx 74.0**（popup+26）→ **0px**；圖示盒 image 16×16（66–82）、iconSclass 18×20（65–83） | **通過** |
| P4 保護項 | — | File popup（#48 混合）+44／43.5／43.5／44、cx 74.0、箭頭 183–187、子選單 +44／+44 → **與 RED 相同**；列高 36、列距 0、分隔線 1px；hover Open 底 (237,237,237) ΔE 6.25（同）；水平 menubar1 頂列 Project 文字 85、image 64.5–79.5、Help 207／187–202、列高 48（同） | **通過** |

## P1 — toast info 圖示對比（`final-p1p2.log`、`p1-toast-*.png`）

| toast | 底色 | 圖示 ink | 對比 | RED |
|---|---|---|---|---|
| info | (224,232,244) | (6,107,143)，480 px，19×19 @ (48.5,200.5) | **4.85** | 3.68 |
| warning | (255,215,197) | (122,54,30) | 6.65 | 6.65 |
| error | (254,205,199) | (127,0,10) | 7.75 | 7.75 |

- computed 圖示色是 `color(srgb 0 0.409 0.551 / 0.974)`：混色帶入 `on-surface` 的 alpha，圖示有 **2.6% 透明**；在這個底色上像素就是 (6,107,143)，對判定無影響，記錄供追溯。
- 旋鈕：(89,70,138) vs 預設 ΔE 38.06；旋鈕的像素不再等於 `#6750a4` 本身（RED 時 (103,80,164) ΔE 0）——這是 **`component-theming` 兩個 toast 測試失敗的原因**，見回歸。

## P2 — notification（`p2-notification-*.png`、`p2-pointer-*.png`）

| 嚴重度 | notification 底 | toast 底 | ΔE | 圖示 | 對比 | left | right | up | down |
|---|---|---|---|---|---|---|---|---|---|
| info | (224,232,244) | (224,232,244) | 0 | (6,107,143) | 4.85 | 0 | 0 | 0 | 0 |
| warning | (255,215,197) | (255,215,197) | 0 | (122,54,30) | 6.65 | 0 | 0 | 0 | 0 |
| error | (254,205,199) | (254,205,199) | 0 | (127,0,10) | 7.75 | 0 | 0 | 0 | 0 |

箭頭格子是三角形內部（離 content 邊 ≥ 2px）中位色對 content 底色的 ΔE；每格最差像素 ΔE 0。靜態卡片的 text ink 最深像素與 RED 差 ΔE 0.96／1.91／2.15，是底色變了之後反鋸齒像素的差，computed `color` 同為 `rgba(0,0,0,0.87)`，ink 盒尺寸與位置相同。

## P3 — 水平 navbar 下拉（`final-p3.log`、`p3-h-contact-settings-open.png`）

| 模式 | 列 | padding-left | 文字 ink（ul+） | RED |
|---|---|---|---|---|
| 水平下拉 [3] Contact | L1 navitem ×4 | 42px | 68.89 | 68.89 |
| | L1 nav 標題 Settings | **42px** | **68.39** | 42.39（16px） |
| | L2 navitem ×3（DOM） | **68px** | 量不到（巢狀 ul 仍 absolute、被父 ul `overflow:hidden` 裁掉——依七.1 列 follow-up，不在本批） | 42px |
| 垂直 [0] | L1／標題／L2 | 42／42／68 | 69／68.5／95 | 同 |
| popup [4] | L1／標題／L2 | 16／16／42 | 43／42.5／69（列高 36） | 同 |

## P4 — menu 列（`final-p4.log`、`p4-project-popup.png`）

| 列 | 種類 | 圖示盒 | 圖示 ink 中心 x（popup+） | cy 偏列中心 | 文字 ink（popup+） | RED 文字 |
|---|---|---|---|---|---|---|
| New | image | 66–82 | 26.0 | 0 | 44 | 42 |
| Open | image | 66–82 | 26.0 | +0.25 | 43.5 | 41.5 |
| Save | image | 66–82 | 26.0 | +0.75 | 43.5 | 41.5 |
| Save As...（disabled） | iconSclass | 65–83 | 26.0 | −1 | 43.5 | 43.5 |
| Exit | image | 66–82 | 26.0 | 0 | 44 | 42 |
| Restart | iconSclass | 65–83 | 26.0 | −1 | 44 | 44 |

修法效果：image 列文字右移 2px、圖示右移 1px（RED cx 25.0 → 26.0），iconSclass 列不動。`<img>` computed `margin 1px/1px`，盒仍 16×16、natural 16×16。

**附帶量（交辦要求）：**
- **巢狀 `z-menu` 帶 image（執行期接到 menubar1 Project popup，`p4n-project-with-nested-menu.png`）：** `z-menu-image` 列 "Nested img" 文字 popup+44、圖示 cx 26.0、盒 16×16（margin 1px/1px）、右箭頭 186–190；同 popup 的 iconSclass `z-menu` "Nested icon" 文字 +44、cx 26.0、箭頭 186–190 → 八列（4 image menuitem、2 iconSclass menuitem、1 image menu、1 iconSclass menu）文字互差 **0.5px**、圖示中心互差 **0**、列高 36。沒有壞。
- **只有 image 列的 popup：** standalone menupopup +44／43.5／43.5／44（RED 42／41.5／41.5／42，整體 +2）、cx 135.0（RED 134.0，+1）；menubar2（autodrop）Project popup +44／43.5／43.5／44、cx popup+26.0；互差 0.5／0。與預期（gen 第 29 行）一致。
- **colorbox 色塊 chip（`colorbox.zul` "Colorbox in Menu"，`p4c-colorbox-format-popup.png`）：** Format popup 的 Text Colour／Fill Colour 是巢狀 `z-menu`，圖示槽是 `<img class="z-menu-image z-colorbox-color">`（bg `rgb(24,77,198)`／`rgb(153,0,0)`，margin **1px/8px**）：色塊 ink 66–82 × 16×16、cx popup+26.0、cy 偏 0；文字 ink popup+**50.5／51**（比 image menuitem 列的 +44 多 6.5：chip 自帶 `margin-right 8px`）；Clear Formatting（無圖示）+17.5；列高 36；右箭頭 190–194。頂列 "Quick Colour" 的 chip 是 `display:none`（水平 menubar 不顯示 content 色塊，RED 之前就如此——`z-menu-image` 在頂列 `display:none`），文字 ink 162.5。沒有重疊、沒有裁切；popup 自動寬 166.75。**沒有修前基準可比**，colorbox-gallery 的回歸結果見下。
- **垂直 menubar [2]（`p4n-vertical-menubar.png`）：** z-menu 帶 image 的列 Project／Help／(無 label) 文字 bar+37、圖示 cx bar+24～24.25、cy 偏 ≤ 1、列高 40、箭頭 127.5–130.5 等；無 RED 基準（gallery 回歸可比）。

## 回歸（8085、`pw/`，不含 `forced-colors-gallery`）

指令：`cd zkpreview && PREVIEW_URL=http://localhost:8085 npx playwright test --config src/test/playwright/playwright.config.ts --project=<p> --output=<scratchpad>/batch14-final/pw/out-<p> --reporter=list`，七個 project 依序各跑一次（`pw/run.sh`，22:30–22:37）。**修前對照**用合併 B 線後的全量回歸紀錄 [merge-1.md](merge-1.md)（`b66c1b1206`，2026-10-09 10:29，本批之前最後一次全量）。

| project | passed | failed | skipped | 失敗 | 修前（merge-1） |
|---|---|---|---|---|---|
| component-theming | 105 | **2** | 0 | `toast — regional bg/fg/radius/accent override`、`toast — whole-app :root override wins` | 107／0 |
| hit-target | 3 | 0 | 0 | — | 3／0 |
| focus-scan | 57 | 0 | 47 | — | 57／0／47 |
| forced-colors | 17 | 0 | 0 | — | 17／0 |
| chromium | 131 | **1** | 0 | `toast › gallery` | 131／1（`tree › gallery`，之後已重切） |
| gallery | 80 | **2** | 0 | `component-theming`、`grid-header`（已知） | 79／3（同兩張＋`grid-paging`，之後已重切） |
| tablet | 49 | **6** | 0 | `calendar`（已知）、`slider`（已知）、`toolbar`、`biglistbox`、`panel`、`selectbox` | 49／6（**同六個**） |

**本批元件的截圖測試：** gallery `menubar`／`navbar`／`notification`／`colorbox`／`anchornav` 通過（1% 容差內）；tablet `menubar`／`notification` 通過；chromium `notification › shell-is-bare-and-icon-clears-stripe`、**`variant-backgrounds-are-opaque`**、`single-line-content-is-vertically-centred`、`toast › base-content-has-no-dark-scrim-fill`、navbar 兩個狀態測試、colorbox hover／focus／popup dismiss、menupopup separator guard 都通過。

**逐一歸因（`reg-diff/`，每張失敗截圖的 expected｜actual｜diff 裁圖；像素數為 Playwright 回報值）：**

| # | 失敗 | 差異 | 歸因 |
|---|---|---|---|
| 1 | component-theming spec：toast 旋鈕 ×2 | 期望 `rgb(103, 80, 164)`（= `#6750a4`），實得 `color(srgb 0.3317 0.2576 0.5282 / 0.974)`（spec 第 839／856 行 `colorOf(icon) toBe(SCOPED_PURPLE)`） | **本批 P1**：圖示色改為 `color-mix(accent 80%, on-surface)` 後，旋鈕值不再原樣落到圖示上。計畫的保護項 (a) 只要求 ΔE ≥ 10（38.06，過），但既有 spec 斷言「旋鈕＝圖示色」，且計畫 P2 保護項寫明 **`component-theming` 全過** → **保護項失敗**。修前 107／0，是本批新增。 |
| 2 | chromium `toast › gallery` | 25 px（> `maxDiffPixels 20`）；any-diff 156 px 全在 **y200–219、x48–67 = info 圖示 19×19**（`toast-gallery.png` 2026-10-06 切） | **本批 P1 預期**：info 圖示色 (0,127,171)→(6,107,143)。重切 `toast-gallery.png`。 |
| 3 | gallery `component-theming` | 期望 1280×18387、實際 18391（+4）；thr .2 283,032 px、200 條帶。+4 來自 **Panel (default)／(regional override)** 兩個 rounded panel 各 +2（y2817–2969 k=0→2→4），之後整頁 k=4 平移；非純平移的帶：Grid 表頭（y1200／1486）、Combobox／Dropdown／Tabbox／Slider／Rangeslider／Multislider／Checkbox／Radio／Messagebox／Chosenbox／Cascader／Searchbox／Anchornav／Stepbar／Coachmark／Biglistbox／Fisheye／Pdfviewer／Tbeditor／Cropper 的 regional override 區，以及 **Notification (default／regional) y9291–9308、y9412–9429（k=0 比對 0/26 列）與 Toast (default／regional) y9539–9560、y9660–9681** | **merge-1 第 3 項已有的過時 baseline（2026-09-11 切，A #54 rounded panel、批次 2／3 等累積）＋ 批次 12 checkbox switch ＋ 本批 P1／P2 的 toast／notification 帶**。本批新增的只有 toast／notification 四條帶（底色、圖示）；其餘早於本批。重切 `component-theming-gallery.png`（重切前先對照裁圖）。 |
| 4 | gallery `grid-header` | 4525→4522、51,831 px（thr .2）、帶從 y33 起 | **已知**（RED S6：與 RED 三次 run 相同的確定性過時 baseline）。 |
| 5 | tablet `calendar` | 1,145 px；帶 y1425–1474／1914–1963／2203–2404 | **已知**（與 RED、merge-1 相同的 1,145）。 |
| 6 | tablet `slider` | 2,354 px；y852–1001 | **已知**（與 RED、merge-1 相同）。 |
| 7 | tablet `toolbar` | 1539→1495（−44）、620 px；y1395–1452：overflow toolbar 的 Print 不再換行、多「…」溢位鈕 | **merge-1 第 4 項**（A 線批次 11 `a99f4da63c` toolbar overflow），baseline 2026-09-12；像素數與 merge-1 **相同（620）**。非本批。 |
| 8 | tablet `biglistbox` | 38 px；y493–501 x234–243、y546–554 x624–633：頁面控制列兩個 selectbox 箭頭 ▼→chevron | **merge-1 第 5 項**（A #29 `e5b82332a9` selectbox 箭頭），baseline 2026-10-07；像素數與 merge-1 **相同（38）**。非本批（menu.css 的改動不碰 selectbox）。 |
| 9 | tablet `panel` | 2510→2514（+4）、15,195 px；第一個 rounded panel 多 1px 外框後整頁下移 | **merge-1 第 6 項**（A #54 `1dda0e855d`），baseline 2026-09-12；像素數與 merge-1 **相同（15,195）**。非本批。 |
| 10 | tablet `selectbox` | 56 px；y199–207、y350–358：三個 selectbox 箭頭 ▼→chevron | **merge-1 第 7 項**（A #29），baseline 2026-09-12；像素數與 merge-1 **相同（56）**。非本批。 |

**未歸因的失敗：無。** 六個 tablet 失敗的像素數與 merge-1 逐一相同，證明它們在本批之前就是這樣；本批新增的只有 #1（spec 斷言）與 #2（toast info 圖示），#3 多了四條 toast／notification 帶。

**需要重切的 baseline PNG（`--update-snapshots=changed`，本次沒有重切）：**
- 本批造成：`zkpreview/doc/screenshots/toast-gallery.png`（#2）。
- 本批有份、但早已過時：`component-theming-gallery.png`（#3：A #54 panel、批次 2／3／12 累積＋本批 toast／notification）。
- 與本批無關、merge-1 已列：`toolbar-tablet.png`、`biglistbox-tablet.png`、`panel-tablet.png`、`selectbox-tablet.png`。
- 已知三個（S6）：`grid-header-gallery.png`、`calendar-tablet.png`、`slider-tablet.png`。
- **沒有失敗、不需重切但像素已變**（1% 容差吃掉；S5 收緊容差時會再浮出）：`notification-gallery.png`、`menubar-gallery.png`、`navbar-gallery.png`、`notification-tablet.png`、`menubar-tablet.png`（本次沒有量它們的 any-diff，因為通過的測試不產生 actual PNG）。

## 判定

| 判定 | 修前（RED） | 修後（FINAL） | 門檻 | 結果 |
|---|---|---|---|---|
| J-P1-1 toast info 圖示對比 | 3.68 | **4.85** | ≥ 4.5 | 過 |
| P1 (a) 旋鈕 ΔE | 44.89 | **38.06** | ≥ 10 | 過 |
| P1 (b)(c) 其餘不變、forced-colors | — | ΔE 0、±0px、491／491／566 px | ΔE ≤ 1、0px | 過 |
| J-P2-1 底色 ΔE（info／warning／error） | 5.07／11.97／13.54 | **0／0／0** | ≤ 2 | 過 |
| J-P2-2 notification info 圖示對比 | 3.88 | **4.85** | ≥ 4.5 | 過 |
| J-P2-3 箭頭 ΔE（12 格） | 0–79.12 | **全 0** | ≤ 2、無陰影 | 過 |
| P2 保護項（尺寸／位置／字型／close／不透明／forced-colors） | — | 全同；`variant-backgrounds-are-opaque` 過 | 不變 | 過 |
| P2 保護項 **`component-theming` 全過** | 107／0 | **105／2**（toast 旋鈕 ×2，本批 P1 造成） | 全過 | **失敗** |
| J-P3-1 標題 vs 同層項目 ink 左緣 | −26.5 | **0.5** | ≤ 1 | 過 |
| J-P3-1 第三層 padding-left | 42 | **68** = 42+26 | 標題 +26 | 過 |
| P3 保護項 | — | L1／列高／hover／垂直／popup 全同（下拉寬 180→186.375 附帶記錄） | 不變 | 過 |
| J-P4-1 文字 ink 互差 | 2.5 | **0.5** | ≤ 1 | 過 |
| J-P4-1 圖示中心互差 | 1.0 | **0** | ≤ 0.5 | 過 |
| P4 保護項 | — | #48 混合 0.5／0、列高 36、16／18、hover、箭頭、頂列全同；巢狀 image menu／全 image popup／colorbox chip 一致、沒壞 | 不變 | 過 |
| 回歸：未歸因失敗 | — | 0 | 0 | 過 |

四個判定（J-P1-1、J-P2-1／2／3、J-P3-1、J-P4-1）與全部像素保護項都通過，回歸沒有未歸因的失敗；但 **`component-theming` 的兩個 toast 旋鈕測試因本批 P1 的 `color-mix` 失敗**，這是計畫明寫的保護項（六.P2「`component-theming` 全過」），依規則判 FAIL。要解的話只有兩條路，由 Planner 裁：(a) CSS 改回 `.z-toast-icon{color:var(--zk-toast-accent)}`、把 **`--zk-toast-accent` 的預設值**改成混深後的色（旋鈕值原樣落到圖示，spec 不用動，J-P1-1 以預設值仍 ≥ 4.5 需再量）；或 (b) 改 spec 的兩個斷言為「隨旋鈕變、ΔE ≥ 10」（計畫 Phase 4 Generator B 可做，但等於放寬旋鈕契約）。其他九個截圖失敗全部有單一歸因（本批 toast 一張、merge-1 既有六張、已知三張），重切清單如上。

GATE14-FINAL: FAIL
