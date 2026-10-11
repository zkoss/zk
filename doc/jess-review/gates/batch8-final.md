使用 8085（A 線，批次 8 最終判定）

# Gate：第八批最終判定（GATE8-FINAL）— menubar popup 項目對齊、勾選欄（D54-A 保留）、navbar 巢狀縮排（#48 #49 #51）

- **伺服器：** 8085，java pid 19767（17:19:43 啟動，`zkpreview/build/gretty_ports.properties` 17:19:44）。**沒有 kill、沒有重啟、沒有碰 8105。**
- **新 build 確認（修正已上線）：** 伺服的 `/zkau/web/80d82a3d/js/zul/menu/css/menu.css.dsp` md5 `6a7acc1da3666fe2c027f184ca33a1d5`（8546 bytes）與 `zul/codegen/resources/web/js/zul/menu/css/menu.css.dsp`、`zul/build/resources/main/web/js/zul/menu/css/menu.css.dsp` 相同；伺服的 `.../js/zkmax/nav/css/nav.css.dsp` md5 `38b767a21dcbc638bfc07d05b4e38f25`（5100 bytes）與 `../zkcml/zkmax/codegen/resources/web/js/zkmax/nav/css/nav.css.dsp`、`../zkcml/zkmax/build/resources/main/web/js/zkmax/nav/css/nav.css.dsp` 相同（build 檔 17:19:01–17:19:41 產生，都早於伺服器啟動）。RED 時期伺服的是 `menu.css.dsp` `74230a46…`、`nav.css.dsp` `c6e7ab87…` → 兩個都換了。原始檔 `menu.css`（`3e7a9530…`）與 `nav.css`（`f6d2f59c…`）都不等於各自 `git HEAD` 版本（`a6728ec3…`／`3a3d0ae5…`）；`zul/src`、`zul/codegen`、`zul/build`、`zkcml/zkmax/src|codegen|build` 底下沒有任何 `.css/.css.dsp/.svg/.xml` 比伺服器啟動時間新。像素也證實：`z-menu` 列「Open」的圖示盒由 13×20 變 18×20，navbar 第二層 nav 標題與第三層項目各多縮 26px。
- **跑完再確認：** 五個 `.css.dsp`（menu、zkmax/nav、zul/nav、selectbox、listbox）的伺服 md5 在開跑前與跑完後完全相同（`served-md5-start.txt` = `served-md5-end.txt`），pid 不變 → 期間沒有別的 build 上線。`selectbox.css.dsp` 現在是 `5b77ddb1…`（第 7 批 forced-colors 修正後的版本，與第 7 批最終判定時的 `9cae05b3…` 不同，與本批無關，只記錄）。
- **日期：** 2026-10-08，Fable Verifier。Playwright 1.59.1、Chromium 147.0.7727.15、viewport 1280×900、DPR 2、`ignoreDefaultArgs:['--hide-scrollbars']`、注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`、游標停在 (2,2)、每個狀態在重新載入的頁面上量。ΔE 一律 CIE76（lib.js）；「左緣」一律以像素 ink（與列底色 ΔE ≥ 8 的像素外接矩形）為判定依據，DOM `span` 左緣並列供追溯；ink 數字 0.5px 粒度。
- **方法：** lines/line-a-plan.md（過程資料已清除） 第三節，以「第 8 批 RED run 結果與方法定稿」為準：J48-1 只判 Jess 截圖的混合（`menuitem iconSclass` + 巢狀 `z-menu`），文字 ink 左緣互差 ≤ 1px、圖示 ink **中心**互差 ≤ 1px；`image` 與 iconSclass 的 2px 差只記錄；J51-1 展開式逐層 ≥ 12px、容差 ±1px；J51-2 改寫為 (a)(b)(c)；#49 依 **D54-A** 不修，只確認 40px 與 0px 位移不變。
- **腳本與原始輸出：** `gates/batch8-final/`：`lib.js`、`m8.js`（複製自 batch8-red）、`final48.js`／`final49.js`／`final51.js`／`final51-hover.js`（複製自 batch8-red 的 `red48/red49/red51/red51-hover.js`，只改輸出檔名）→ `final48.json/.log` + `final48-*.png`、`final49.json/.log` + `final49-*.png`、`final51.json/.log` + `final51-*.png`、`final51-hover.json/.log` + `final51h-*.png`；新增 `probe51-extra.js`（乾淨頁面的 collapsed navbar 第一層、popup 第一列左緣色帶、popup 第三層 bullet 檢查、**用 widget API 現做第四層**）→ `probe51-extra.json/.log`、`probe51-*.png`；`probe-forced.js`（forced-colors 下 File popup 與 navbar 三層）→ `probe-forced.json/.log`、`probe-forced-*.png`；回歸 `pw-regression.log`（`pw-core/`、`pw-visual/` 都是空的 = 無失敗）；Jess 留言用截圖 `jess-shots.js` → `jess-48-file-popup-after.png`、`jess-48-file-popup-after-zoom.png`、`jess-48-project-popup-after.png`、`jess-49-view-unchecked.png`、`jess-49-view-checked.png`、`jess-51-navbar-expanded-after.png`、`jess-51-collapsed-popup-after.png`（都是 DPR 2）。修前對照用 RED 的 `batch8-red/red48-A-file.png`、`red51-expanded-open.png`、`red51-popup-l3.png`。
- **沒有看 diff、沒有讀 `gates/batch8-gen.md`、沒有讀 CSS 修改內容**，沒有修改任何 theme／CSS／TS／Java／預覽頁／baseline 檔案，沒有 `--update-snapshots`，Playwright 只用 `-g 'menubar|menu|navbar'` 過濾，沒有 commit。第四層是在頁面內用 `new zkmax.nav.Nav(...)`／`zkmax.nav.Navitem` 掛到 Settings 底下（只在那個瀏覽器 context 裡，沒有動檔案）。計算值（`li` 的 `display`、巢狀 `ul` 的 `list-style/padding`、勾選符號的 `visibility`、停用列 `opacity`）只用來解釋現象，判定全部以像素為準。

---

## 結論摘要

| 判定 | 門檻 | 今天（修後） | RED（修前） | 結果 |
|---|---|---|---|---|
| J48-1 文字（File popup：New／Open(`z-menu`)／Save／Exit 文字 ink 左緣互差） | ≤ 1px | 92／**91.5**／91.5／92 → 互差 **0.5px**（span 91 全同） | 92／86.5／91.5／92 → 5.5px | **PASS** |
| J48-1 圖示（同四列圖示 ink 中心互差） | ≤ 1px | 74／**74**／74／74 → **0px**（圖示盒四列都是 [65, 18×20]） | 74／71.5／74／74 → 2.5px | **PASS** |
| J51-1 展開式：L2 nav 標題（Settings）比 L1 nav（Contact）多 | ≥ 12px，各層一致 ±1px | 100.5 − 74.5 = **26** | 0 | **PASS** |
| J51-1 展開式：L3 項目（Edit profile）比 L2 項目（Reply）多 | ≥ 12px，各層一致 ±1px | 127 − 101 = **26**；L1 item→L2 item 75→101 = 26；三個階差 26／26／26（`padding-left` 16／42／68） | 0 | **PASS** |
| J51-1 加測第四層（widget API 現做：Settings → Advanced(nav) → Deep item） | 同上 | Advanced 126 − Settings 100.5 = 25.5；Deep item 153 − Edit profile 127 = **26**（`padding-left` 94） | — | **PASS** |
| J51-2 (a) popup 內 L3 文字比 L2 多 | 12–30px | 352 − 326 = **26** | 66（瀏覽器預設 `ul` padding） | **PASS** |
| J51-2 (b) popup 內 L3 列框左緣 = L2 列框左緣，列前除圖示外沒有 ink | 相同；無 | L3 列框 **283** = L2 283 = popup 左緣 283；三列 L3 在列框左緣到圖示元素之間 **ΔE ≥ 8 的像素數 0**（RED 有 5×5 bullet @ 307.5）；巢狀 `ul` 計算值 `list-style: none`、`padding: 0` | 323；有 bullet | **PASS** |
| J51-2 (c) popup 內 L3 hover 狀態層觸及 popup 內緣 | 左右都到 | Edit profile／Change password 狀態層 **284–497**（popup 283–498.27），`reachesLeft/Right` 皆 true，色 (205,208,213) ΔE 12.67 | 323–537 | **PASS** |
| #49（D54-A，只確認不變） | 40px／0px | View popup 文字 span 距內緣 **40**（ink 40.5）；autocheck Sort by Name 後文字 ink **182.5 → 182.5（0px 位移）**，勾 ink 12×9 @ (160,368) 色 (55,111,208) | 40／0 | **不變** |

保護項全數不變（下表），回歸 0 失敗。

## #48 — menubar popup 項目對齊（修後數字）

| popup | 列 | 列種類 | 圖示盒 [x,y,w,h] | 圖示 ink 中心 | 文字 span 左緣 | 文字 ink 左緣 | 文字距內緣 | RED 文字 ink |
|---|---|---|---|---|---|---|---|---|
| A File（內緣 49，Jess 的混合） | New | menuitem / iconSclass | [65,363,18,20] | 74 | 91 | 92 | 43 | 92 |
| A | Open | **menu / iconSclass** | [65,399,**18**,20] | **74** | **91** | **91.5** | 42.5 | **86.5** |
| A | Save | menuitem / iconSclass | [65,435,18,20] | 74 | 91 | 91.5 | 42.5 | 91.5 |
| A | Exit | menuitem / iconSclass | [65,471,18,20] | 74 | 91 | 92 | 43 | 92 |
| B File→Open 子選單（內緣 208） | Project... / File... | menuitem / iconSclass | [224,…,18,20] | 233／233 | 250／250 | 251／251 | 43 | 251／251 |
| C Project（內緣 49） | New／Open／Save／Exit | menuitem / **image** | [65,…,16,16] | 73 | 89 | 90／89.5／89.5／90 | 40.5–41 | 同 |
| C | Save As...（disabled）／Restart | menuitem / iconSclass | [65,…,18,20] | 74 | 91 | 91.5／92 | 42.5–43 | 同 |
| D Help（垂直 menubar，內緣 233） | Index／About(`z-menu`) | 純文字 | — | — | 249／249 | 250／249 | 17／16 | 同 |
| E Help→About 子選單（內緣 392） | About ZK／About Potix | 純文字 | — | — | 408／408 | 408／408 | 16 | 同 |
| F 獨立 menupopup（內緣 110） | New／Open／Save／Exit | menuitem / image | [126,…,16,16] | 134 | 150 | 151／150.5／150.5／151 | 40.5–41 | 同 |

- **只有 A 的 Open 列動了**（文字 86.5 → 91.5，圖示盒 13 → 18 寬、ink 中心 71.5 → 74）；其他 popup 每一列的數字與 RED 逐一相同。
- **記錄（不判定、不修，R6／R9，follow-up）：** Project popup 的 `image` 列（文字 ink 89.5–90、盒 16×16）與 iconSclass 列（91.5–92、盒 18×20）仍差 **2–2.5px**（span 89 vs 91 = 2px），與 RED 相同，**預期不變、確認不變**。截圖 `jess-48-project-popup-after.png`。

**#48 保護項（修後 = RED）：** 列高 36 全同（A／B／C／D／E／F 每列 `cnt.h` 36）、popup 寬 160、邊框 1px、內容區上下 4px；hover 狀態層 (237,237,237) ΔE 6.25、水平 48–208（含邊框欄）、垂直 = 整列 36px（A New 355–391、A Open 391–427、C New 213–249 起、C Restart 至 438，與 RED 逐值相同）；子選單箭頭 ink 4×7 @ x 183–187（距內緣右 207 為 20px）、元素 `[179,399,12,20]`，D 的 About 箭頭 `[363,541,12,20]`、ink @ 367 也同；`image` 盒 16×16 @ 內緣+16（y 列頂+10）、iconSclass 盒 18×20 @ 內緣+16（y 列頂+8）不變；停用列 Save As... `opacity 0.38`、位置與同種列相同；分隔線 1px (224,224,224) @ y 361（C）／854（F）、前後各 4px；兩個水平 menubar 的頂層項目矩形與文字左緣（`final48.json.tops`）與 RED 逐值相同（Project [48,158,118.19,48]／84、Help [169.84,…]／205.84、File [48,300,92.53,48]／82、View [140.53,…]／156.53、Help(item) [228.23,…]／262.23 等）。

## #49 — 勾選欄（D54-A：保留，只確認數字不變）

| popup／列 | 文字前的元素 | 文字 span 距內緣 | 文字 ink 距內緣 | 文字前有無 ink |
|---|---|---|---|---|
| View，Sort by Name／Sort by Date（未勾） | `i.z-menuitem-icon.z-icon-check` 盒 16×20 @ 內緣+16、`visibility:hidden` | **40** | 40.5 | 無 |
| View，Sort by Name（autocheck 後重開，已勾） | 同一個盒、`visible`，勾 ink 12×9 @ (160,368)、色 (55,111,208) | 40 | **40.5（0px 位移）** | 勾 |
| File（iconSclass 列）／Project（image 列）／Help（純文字） | — | 42／40／16 | 42.5–43／40.5–41／16–17 | — |

全部與 RED 相同。Jess 留言用截圖：`jess-49-view-unchecked.png`（勾選前）、`jess-49-view-checked.png`（勾選後，文字不動）。

## #51 — navbar 巢狀縮排（修後數字）

| 模式 | 層 | 列 | `padding-left` | 圖示盒 x | 文字 span 左緣 | 文字 ink 左緣 | 距容器左緣 | RED 距容器左緣 |
|---|---|---|---|---|---|---|---|---|
| 展開式（navbar 左緣 32，Contact + Settings 展開） | L1 | Home／About／Freeze／Logout | 16 | 48 | 74 | 75／74.5／75／75 | 43／42.5／43／43 | 同 |
| | L1 nav | Get Started／Contact | 16 | 48 | 74 | 74.5／74.5 | 42.5 | 同 |
| | L2 | Reply／Reply all／Inbox／Edit | 42 | 74 | 100 | 101 | 69 | 69 |
| | **L2 nav** | **Settings** | **42** | **74** | **100** | **100.5** | **68.5** | 42.5 |
| | **L3** | Edit profile／Keyboard shortcuts／Change password | **68** | **100** | **126** | **127／127／126.5** | **95／95／94.5** | 69 |
| 展開式 + widget API 第四層（`probe51-extra.json.l4_rows`） | L3 nav | Advanced | 68 | 100 | 126 | 126 | 94 | — |
| | **L4** | Deep item／Deep item 2 | **94** | **126** | **152** | **153／153** | **121** | — |
| collapsed popup（`ul.z-nav-popup` 左緣 283，底 (240,244,250)） | p-L2 | Reply／Reply all／Inbox／Edit | 16 | 299 | 325 | 326 | 43 | 43 |
| | p-L2 nav | Settings | 16 | 299 | 325 | 325.5 | 42.5 | 42.5 |
| | **p-L3** | Edit profile／Keyboard shortcuts／Change password | 42（列框從 **283** 起） | 325 | 351 | **352／352／351.5** | **69／69／68.5** | 109（列框從 323 起） |
| 水平 collapsed popup（左緣 202） | p-L2／p-L2 nav | Reply 等／Settings | 16 | 218 | 244 | 245／244.5 | 43／42.5 | 同 |
| | p-L3 | Edit profile 等 | 42（列框從 202 起） | 244 | 270 | 271／271／270.5 | 69／69／68.5 | 109（列框從 242 起） |
| 水平展開 navbar[3]（點 Contact 後下拉，只記 DOM） | L2 | Reply 等 | 42 | cnt+42 | cnt+68 | — | — | 同 |
| | L2 nav | Settings | 16 | cnt+16 | cnt+42 | — | 與 L1 同 | 同 |

- **J51-1：** Settings − Contact = 26、Edit profile − Reply = 26、Reply − Home = 26；三個階差互差 0（容差 ±1）。第四層再加 26（`padding-left` 16／42／68／94 等差）。截圖 `jess-51-navbar-expanded-after.png`（修前 `batch8-red/red51-expanded-open.png`）。
- **J51-2：** (a) 26px；(b) 列框 283 = popup 左緣，`probe51-extra.json.l3_rows[*].inkBeforeIcon` 三列都是 `null`（列框左緣到圖示元素之間沒有 ΔE ≥ 8 的像素），巢狀 `ul` 的 `list-style none`／`padding 0`／`margin 0`；(c) 狀態層 284–497（下表）。水平 collapsed popup 同樣修好（271 − 245 = 26、列框 202）。截圖 `jess-51-collapsed-popup-after.png`（修前 `batch8-red/red51-popup-l3.png`）。
- **popup 第一列（Reply）左緣的 1.5px 藍色 (55,111,208) 直條（x 283–284）：** `final51.json.popup_l2` 把它量成 Reply 的 `iconInk [284, 392, 30.5, 30]`，與 RED 的 `red51.json.popup_l2` **逐值相同**（RED 也是 `[284,392,30.5,30]`）；`probe51-extra` 的色帶顯示它只在 x 283–284、Inbox 列沒有，是 popup 內 Reply（selected navitem）原有的指示條，不是本批造成，只記錄。
- **水平展開 navbar[3] 的下拉（`horizontal_open`）：** 與 RED 逐值相同——L2 項目 `padding-left` 42、L2 nav Settings 16（與 L1 同）。保護項要求「水平 navbar 不變」，今天不變；**但這表示水平 navbar 下拉內的巢狀 nav 標題仍沒有縮排**（Jess 的截圖都是垂直 navbar，#51 原文不含水平），請 Planner 決定是否列 follow-up。

**#51 狀態層（保護項 + J51-2(c)，`final51-hover.json`，只量框內、thr 2）：**

| 模式 | 列（層） | 狀態層色 | ΔE vs 底 | 水平範圍 | 容器 | 滿寬 |
|---|---|---|---|---|---|---|
| 展開式 | Home (L1 item)／Reply (L2 item)／Edit profile (L3 item) | (216,218,221) | 10.88 | 33–272 | 32–273.27 | ✓ |
| | Contact (L1 nav)／Settings (L2 nav) | (229,231,234) | 6.28 | 33–272 | 32–273.27 | ✓ |
| popup | Reply (p-L2 item)／**Edit profile／Change password (p-L3 item)** | (205,208,213) | 12.67 | **284–497** | 283–498.27 | ✓ |
| | Settings (p-L2 nav) | (223,226,232) | 6.24 | 284–497 | 283–498.27 | ✓ |

色與 ΔE 與 RED 相同；垂直範圍 = 整列（±1px，Home 182–227 含上緣外白底，與 RED 同）。

**#51 其他保護項（修後 vs RED）：**

- **第一層位置不變（同一開合狀態比）：** 關閉狀態 navbar[0] 矩形 `[32,187,176.17,268]` 與 RED 相同；L1 每列 `cnt.l` 32、icon 盒 @ 48（+16）18×15、文字 span @ 74（+42）、列高 40、pitch 41（188→229→270）；展開後 Freeze／Logout 的 `cnt.t` 713／754、navbar 高 608，與 RED 相同（列數與列高都沒變）。展開後 navbar 寬度 **176.17 → 241.27**（RED 是 → 215.27）：多 26px 是第三層文字多縮 26px 撐開（最寬的 label 是 Keyboard shortcuts），x 不變；依方法第 5 項屬內容撐開，比 x 與列高、不比寬。
- **列高：** 展開式 40、L2／L3 pitch 40、L1 pitch 41；popup 列高 36、pitch 36；分隔 20px。全同。
- **badge：** Get Started／Contact 20×18、Settings 25.47×18，右緣距 navbar 右緣 **30px**（RED 217.27／247.27 → 30；今天 243.27／273.27 → 30）；popup 內 Settings badge `[442.8,562,25.47,18]` 右緣距 popup 右緣 30 = RED。collapsed navbar 的 badge @ 列左+28.27、17.7×16 = RED。
- **展開箭頭：** `::after` ink 8×4.5，x 距 navbar 右緣 **23.27**（今天 250–258 / 273.27；RED 224–232 / 247.27），y 與 RED 逐值相同（Get Started 248.5、Contact 348、Settings 568）；popup 內 Settings 箭頭 @ 475（距右 23.27）= RED。
- **collapsed 第一層圖示位置：** navbar[1]／[2] 寬 50、icon 盒 @ 左+16、18×15、列高 35、pitch 36 = RED。乾淨頁面（navbar[0] 關閉）navbar[1] 左緣 232.17、navbar[2] 408.86；RED 只記了 navbar[0] 展開狀態下的 271.27／447.95（= 232.17／408.86 + 215.27 − 176.17），今天展開狀態是 297.27／473.95（+241.27 − 176.17），差額就是 navbar[0] 寬度差，collapsed navbar 自己的幾何不變。
- **水平 navbar 不變：** navbar[3]／[4]／[5] 的 root 與每列 `cnt`、icon、text、badge 矩形（`final51.json.horizontal_3/4/5`）與 RED **逐值相同**。
- **forced-colors（`probe-forced.json`，`emulateMedia({forcedColors:'active'})`，`matchMedia` true）：** File popup 四列文字 ink 92／91.5／91.5／92、圖示 ink 中心 74×4、箭頭 ink @ 183；展開式 navbar 三層 43／69／95、popup 44／70（popup 在 forced-colors 下多 1px 邊框，L2 與 L3 同加 1）；每一列都有文字 ink（最深色 (0,0,159)），列前沒有多餘 ink。Playwright `forced-colors` 專案也全過（含 navbar selected+focused 的 Highlight 檢查）。
- **計算值紀錄（不判定）：** 展開式 navbar 內 L2／L3 的 `li` 計算值 `display: list-item`（RED 報告文字寫 popup 內是 `list-item`、展開式是 `block`；今天展開式 L1 是 `block`、L2 以下是 `list-item`），因 `ul` 的 `list-style: none`，沒有任何 marker ink（`inkBeforeIcon` 全為 null），無像素影響。

## 回歸

`pw-regression.log`，從 `zkpreview/` 跑，`--output` 指到本目錄，沒有 `--update-snapshots`：

- **core（component-theming、hit-target、focus-scan、forced-colors，全量）：** `184 passed, 47 skipped`，exit 0（與第 7 批最終判定的 184／47 相同）。`pw-core/` 空 = 無失敗 artifacts。menubar／navbar／nav／anchornav 相關項目（focus-ring scan、component-theming 的 nav 區域覆寫、forced-colors 的 navbar selected focus ring）全過。
- **visual（gallery + chromium，`-g 'menubar|menu|navbar'`）：** 5 tests，`5 passed`，exit 0：`gallery › menubar`、`gallery › navbar`、`navbar › selected item is a rounded tonal container…`、`navbar › submenu item keyboard focus ring…`、`important-removal-guards › menupopup separator keeps its 4px vertical margin…`。`pw-visual/` 空。
- **失敗歸因：** 無失敗，無可歸因。原先預期會失敗的「menu popup 圖示文字偏移」與「navbar 縮排」**沒有出現在任何 baseline 裡**：`zkpreview/doc/screenshots/menubar-gallery.png`、`navbar-gallery.png`（2026-09-11 `cd02d189` 建立）拍的是靜止狀態——popup 關閉、Contact／Settings 收合——修正影響的只有展開後的 popup 與巢狀列。**沒有 baseline PNG 需要更新**，也沒有產生任何 `-actual.png`／`-diff.png`。已知的 `calendar-tablet`、`slider-tablet`、`grid-header-gallery` 不在本次範圍（tablet 專案未跑、grid 不在過濾條件內），未觸發。

## 判定

- J48-1：文字互差 0.5px、圖示中心互差 0px → PASS。
- J51-1：三層階差 26／26／26，第四層 26 → PASS。
- J51-2 (a) 26px、(b) 列框 283 與 L2 同、無 bullet、(c) 狀態層 284–497 滿寬 → PASS。
- #49（D54-A）：40px／0px 位移不變 → 確認。
- 保護項：#48 列高／狀態層／箭頭／圖示尺寸／停用透明度／頂層位置、#48 image vs iconSclass 2px（記錄，不變）、#51 第一層／列高／狀態層滿寬／badge／箭頭／collapsed 第一層／水平 navbar／forced-colors → 全部不變。
- 回歸：core 184/0 失敗、visual 5/0 失敗、無 baseline 差異。
- 給 Planner 的兩個記錄項（不影響判定）：(1) Project popup 的 image／iconSclass 2px 差留 follow-up（方法已定）；(2) 水平 navbar 下拉內的巢狀 nav 標題仍與 L1 同縮排（保護項要求不變，今天不變），是否列 follow-up 由 Planner 決定。

GATE8-FINAL: PASS
