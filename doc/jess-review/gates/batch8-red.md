使用 8085（A 線，批次 8 RED）

# Gate：第八批 RED run，menubar 項目對齊、勾選欄空白、navbar 巢狀縮排（#48 #49 #51）

- **伺服器：** 8085，java pid 58306（16:06:15 起聽，與第 7 批同一個行程）。**沒有 kill、沒有重啟、沒有碰 8105。** 新鮮度：伺服的 `zul/menu/css/menu.css.dsp`（md5 `74230a46…`）與 `zkmax/nav/css/nav.css.dsp`（`c6e7ab87…`）各與 `zul/build`、`zkcml/zkmax/build` 底下的檔案 md5 **相同**；`zul/src`、`zul/build`、`zkcml/zkmax/src`、`zkcml/zkmax/build` 底下沒有任何 `.css/.css.dsp/.svg/.xml` 比伺服器啟動時間（`build/gretty_ports.properties` 16:06:15）新 → 伺服的是現在的 build。
- **日期：** 2026-10-08，Verifier，量現在的程式碼（Playwright 1.59.1、Chromium 147.0.7727.15、viewport 1280×900、deviceScaleFactor 2、`ignoreDefaultArgs:['--hide-scrollbars']`；注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`、游標停在 (2,2)；每個狀態都在重新載入的頁面上量）。
- **方法：** lines/line-a-plan.md（過程資料已清除） 第三節「第 8 批方法」。文字與圖示的「左緣」一律以**像素 ink**（與列底色 ΔE ≥ 8 的像素外接矩形，CIE76）為判定依據，旁邊並列 DOM 的 `span` 左緣與 `Range.getBoundingClientRect()` 左緣供追溯；ink 數字是 0.5px 粒度（DPR 2）。
- **腳本與原始輸出：** `gates/batch8-red/`：`lib.js`（複製自 batch7-red）、`m8.js`（本批輔助：列幾何、ink box、狀態層延伸）、`probe-dom.js/.json` + `probe-*.png`（DOM 形狀探針）、`probe-navpopup.js`（collapsed popup 行為探針，失敗紀錄留著）、`probe-popup-ul.js/.json` + `probe-popup-ul-l3*.png`（#51 popup 第三層的根因證據）、`red48.js/.json` + `red48-{A-file,A-file-hover-new,A-file-hover-open,A-page,B-open-submenu,C-project,C-project-hover-new,C-project-hover-restart,D-help,E-about-submenu,F-standalone}.png`、`red49.js/.json` + `red49-{view-unchecked,view-unchecked-zoom,view-checked,view-checked-zoom,file,help-plain}.png`、`red51.js/.json` + `red51-{expanded-closed,expanded-open,expanded-hover-*,popup-l2,popup-l3,popup-hover-*,popup-page,hpopup-l2,hpopup-l3,horizontal-open}.png`、`red51-hover.js/.json` + `red51h-*.png`（狀態層只量框內的第二次量法）、`ref/jess-{48-current,49-current,51-expanded,51-popup}.png`（Jess 的 issue 截圖）。
- **沒有修改**任何 theme／CSS／TS／Java／預覽頁檔案，沒有看 diff，沒有 commit；頁內只注入 lib.js 的停動畫規則。為了弄懂 collapsed navbar 的 popup 怎麼開，讀了 `zkcml/zkmax/.../nav/Nav.ts` 的 `_openPopup/_doMouseEnter/doClick_`（只讀，解釋現象用）。
- **讀計算值的地方（只為解釋現象與保護項紀錄，不是判定依據）：** 停用列的 `opacity`；勾選欄 `i.z-menuitem-icon` 的 `visibility`；popup 的 `border/padding/background-color`；#51 popup 內巢狀 `ul` 的 `list-style/padding/margin`（根因證據，與第 7 批的 `elementsFromPoint` 同性質）。
- **選取 widget 的方式：** DOM id 是 uuid；menubar 用 class + 頁面順序（`.z-menubar[i] > ul > li[j]`），navbar 用 `zk.Widget.$(li).getLabel()` 找列。

## 結論摘要

| 項目 | 判定檢查今天 | 保護項今天 | 方法問題 |
|---|---|---|---|
| #48 J48-1 同一 popup 內有圖示的列文字左緣互差 ≤ 1px | **File popup（Jess 的那一種）**：New 92、Open（`z-menu`）**86.5**、Save 91.5、Exit 92 → 互差 **5.5px**（span 91 vs 86 = 5px）→ **失敗，RED-correct**。**Project popup**（image + iconSclass 混合）：image 列 89.5–90、iconSclass 列 91.5–92 → 互差 **2.5px**（span 89 vs 91 = 2px）→ **也失敗** | — | 無（但要寫明兩種混合都在 J48-1 範圍內，數字不同：5 與 2） |
| #48 J48-1 圖示左緣互差 ≤ 1px（`z-menu` 列與 `z-menuitem` 列相同） | File popup 圖示 **ink** 左緣 67／65.5／66.5／68.5 → 互差 3px；但**同為 `z-menuitem` 的三列彼此就差 2px**（glyph 側邊距不同）；圖示**元素盒**左緣四列都是 65（寬 18／13／18／18）；ink **中心** 74／71.5／74／74（`z-menu` 列偏左 2.5） | — | **METHOD-DEFECT（量法）**：「圖示 ink 左緣 ±1」受 glyph 形狀左右，對齊後也過不了；改量圖示盒左緣（今天 65 全同、沒有鑑別力）或 **ink 中心 ±1**（今天 71.5 vs 74，失敗、有鑑別力） |
| #48 保護項 | — | 列高 36 全同、popup 寬 160、邊框 1px、內容區上下 4px；hover 狀態層 (237,237,237) ΔE 6.25，水平延伸 48–208（含邊框欄，內緣 49–207）、垂直 = 整列 36px；子選單箭頭 ink 4×7 @ x 183–187（距內緣右 20px）、元素 `[179,399,12,20]`；image 盒 16×16 @ 內緣+16（y +10）、iconSclass 盒 18×20 @ 內緣+16（y +8）、`z-menu` 列的 iconSclass 盒 **13×20**；停用列 `opacity 0.38`；分隔線 1px (224,224,224)、前後各 4px；頂層項目矩形見 `red48.json.tops` | 無 |
| #49 勾選欄（只記數字） | View popup（兩列 `checkmark="true"`、皆未勾）：文字 span 距內緣 **40**（ink 40.5）；勾選符號元素 `i.z-menuitem-icon.z-icon-check` 盒 16×20 @ 內緣+16、`visibility:hidden`，文字前 **40px 完全沒有 ink**。對照：File（iconSclass）42（ink 42.5–43）、Project 的 image 列 40（ink 40.5–41）、Help 純文字 **16**（ink 16–17）。勾選後（autocheck Sort by Name）：勾 ink 12×9 @ x 160、色 (55,111,208)，**文字左緣 182.5 不動（0px 位移）** | — | 無（依計畫，D54 問） |
| #51 J51-1 展開式第二層 nav 標題比第一層多 ≥ 12px；第三層項目比第二層項目多 ≥ 12px | Contact 74.5、**Settings 74.5（差 0）**；Reply 101、**Edit profile 101（差 0）** → **失敗，RED-correct**（span 74/74、100/100；`padding-left` 16/16、42/42） | — | 無 |
| #51 J51-2 popup 模式逐層縮排 | collapsed popup（`ul.z-nav-popup`，左緣 283）：Reply 326（距 popup 左 43）、Settings 325.5（42.5）、**Edit profile 392（109）** → 第三層比第二層多 **66px** → **按字面今天就通過** | — | **METHOD-DEFECT（判定不成立）**：這 66px 不是縮排，是 popup 內巢狀 `ul` 拿到**瀏覽器預設樣式**（`list-style: circle outside`、`padding-left: 40px`），列前還畫出 **5×5 的圓圈 bullet**（ink @ x 307.5–312.5），第三層的 hover 狀態層只從 **323** 起（popup 內緣 283）。J51-2 要重寫（見方法問題清單第 3 項） |
| #51 保護項：狀態層整列滿寬 | — | 展開式：Home／Contact／Reply／Settings／Edit profile 的狀態層都從 33 到 246（navbar 32–247.27）**滿寬** ✓；navitem hover (216,218,221) ΔE 10.88、nav hover (229,231,234) ΔE 6.28（對底 (247,249,252)）。popup：Reply (205,208,213) ΔE 12.67、Settings (223,226,232) ΔE 6.24，都 284–537 滿寬 ✓；**第三層 Edit profile／Change password 只有 323–537** ✗ | **METHOD-DEFECT（歸類）**：popup 第三層今天就不滿寬，這條不能當「今天必須通過」的保護項，要併進 J51-2 |
| #51 其他保護項 | — | 第一層：icon 盒 @ navbar+16（18×15）、文字 span @ +42、列高 40（pitch 41）；展開 Contact 後 navbar **寬度 176.17 → 215.27**（最寬的第三層 label 撐開），x 位置不變、寬度變；badge：Get Started `[197.27,240,20,18]`（展開後）、Contact `[197.27,343,20,18]`、Settings `[191.8,563,25.47,18]`；展開箭頭 ink 8×4.5 @ x 224（距 navbar 右緣 23.27），三個 nav 列相同；collapsed 第一層 icon @ navbar+16（18×15）、列高 35、badge @ +28.5；水平 navbar 列見 `red51.json.horizontal_3/4/5` | **說明**：「第一層位置不變」要在同一開合狀態下比（寬度隨內容變） |

## #48 — menubar popup 項目對齊

**Jess 的截圖是哪一種混合：** `ref/jess-48-current.png` 是「Horizontal with Icons」那個 menubar 的 **File popup**：三個 `menuitem iconSclass`（New／Save／Exit）加一個巢狀 **`z-menu`「Open」**（`iconSclass="z-icon-folder-open"`，右側有 ▸）。她的紅線落在 New／Save／Exit 文字左緣，Open 偏左——與今天量到的 5–5.5px 一致。預覽頁 `menubar.zul` 第二個 menubar 就是這個 popup（下表 A）。

| popup | 列 | 列種類 | 圖示盒 [x,y,w,h] | 圖示 ink 左緣 | 圖示 ink 中心 | 文字 span 左緣 | 文字 ink 左緣 | 文字距內緣 |
|---|---|---|---|---|---|---|---|---|
| A File（內緣 49） | New | menuitem / iconSclass | [65,363,18,20] | 67 | 74 | 91 | **92** | 43 |
| A | Open | **menu / iconSclass** | [65,399,**13**,20] | 65.5 | **71.5** | **86** | **86.5** | **37.5** |
| A | Save | menuitem / iconSclass | [65,435,18,20] | 66.5 | 74 | 91 | 91.5 | 42.5 |
| A | Exit | menuitem / iconSclass | [65,471,18,20] | 68.5 | 74 | 91 | 92 | 43 |
| B File→Open 子選單（內緣 208） | Project... | menuitem / iconSclass | [224,406,18,20] | 224.5 | 233 | 250 | 251 | 43 |
| B | File... | menuitem / iconSclass | [224,442,18,20] | 226 | 233 | 250 | 251 | 43 |
| C Project（內緣 49） | New | menuitem / **image** | [65,223,16,16] | 65 | 73 | **89** | **90** | 41 |
| C | Open | menuitem / image | [65,259,16,16] | 65.5 | 73 | 89 | 89.5 | 40.5 |
| C | Save | menuitem / image | [65,295,16,16] | 65 | 73 | 89 | 89.5 | 40.5 |
| C | Save As... | menuitem **disabled** / iconSclass | [65,329,18,20] | 66.5 | 74 | **91** | **91.5** | 42.5 |
| C | （separator 1px @ y 361，(224,224,224)） | | | | | | | |
| C | Exit | menuitem / image | [65,376,16,16] | 66 | 73 | 89 | 90 | 41 |
| C | Restart | menuitem / iconSclass | [65,410,18,20] | 66.5 | 74 | 91 | 92 | 43 |
| D Help（垂直 menubar，內緣 233） | Index | menuitem / 純文字 | — | — | — | 249 | 250 | 17 |
| D | About | **menu** / 純文字 | — | — | — | 249 | 249 | 16 |
| E Help→About 子選單（內緣 392） | About ZK | menuitem / 純文字 | — | — | — | 408 | 408 | 16 |
| E | About Potix | menuitem / 純文字 | — | — | — | 408 | 408 | 16 |
| F 獨立 menupopup（內緣 110） | New／Open／Save／Exit | menuitem / image | [126,…,16,16] | 126／126.5／126／127 | 134 | 150 | 151／150.5／150.5／151 | 41／40.5／40.5／41 |

- **哪些列不同、差多少：** (1) **巢狀 `z-menu` 列 vs `z-menuitem` 列**（同為 iconSclass）：文字差 **5px**（span）／5–5.5px（ink）；圖示盒寬 **13 vs 18**（`z-menu` 的 `<i>` 沒有被撐到 18px），圖示 ink 中心差 2.5px。(2) **image 列 vs iconSclass 列**：文字差 **2px**（span 89 vs 91）／2–2.5px（ink）；圖示盒 16 vs 18 寬、同一個左緣 65。(3) 純文字列之間、同種列之間：≤ 1px（D 的 Index 250 vs About 249 是 I／A 的 glyph 差，span 相同）。(4) 停用列（Save As...）與同種的 Restart 相同（91.5／92），停用不影響位置。(5) 純文字的 `z-menu`（About）與純文字 `menuitem`（Index）相同 → `z-menu` 列的偏差只在**有圖示**時出現。
- **J48-1：** A 失敗（5.5px）、C 失敗（2.5px）、B／D／E／F 通過。**RED-correct**；Jess 看到的就是 A。
- **註：** 第一個 menubar 的 Help 是 `disabled`，About 子選單改用垂直 menubar（第三個）量；Auto Drop menubar 的 Help（G）捲動後沒抓到 popup，與判定無關，未補。

## #49 — 勾選欄的多餘空白（只記數字）

| popup／列 | 文字前的元素 | 文字 span 距內緣 | 文字 ink 距內緣 | 文字前有無 ink |
|---|---|---|---|---|
| View，Sort by Name（未勾） | `img.z-menuitem-image` display:none；`i.z-menuitem-icon.z-icon-check` 盒 **16×20 @ 內緣+16**，`visibility:hidden` | **40** | 40.5 | 無（40px 全白） |
| View，Sort by Date（未勾） | 同上 | 40 | 40.5 | 無 |
| View，Sort by Name（**已勾**，autocheck 後重開） | 同一個盒，`visibility:visible`，勾 ink **12×9 @ (160,368)**、色 (55,111,208) | 40 | **40.5（不動）** | 勾 |
| File（iconSclass 列） | 18×20 盒 @ +16 | 42 | 42.5–43 | 圖示 |
| Project（image 列） | 16×16 盒 @ +16 | 40 | 40.5–41 | 圖示 |
| Help（純文字列） | 無 | **16** | 16–17 | 無 |

- 預留欄 = 16px 內距 + 16px 勾盒 + 8px 間距 = 文字在 **40px**；比純文字列多 **24px**；與 image 列同寬、比 iconSclass 列窄 2px。勾選後文字 0px 位移（版面穩定成立）。
- 水平 Scrollable menubar 的頂層 `item 2 checkmark="true"`（記錄）：勾盒 16×20 @ li+12、`hidden`，文字 @ li+32；旁邊 item 1 的 `z-icon-circle` 18×20 @ li+12、文字 @ li+34。
- **MD3 參考（憑記憶，未核對原文，標為未驗）：** M3 menu 的 list item 是 12dp 左右內距、24dp leading icon、icon 與 label 間 12dp（有 icon 的 label 在 48dp，無 icon 在 12dp）；我記得 Material 的 menu 指引有「同一個 menu 內只有部分項目有 icon 時，所有 label 對齊」的說法，但那是 M2 的文字，M3 頁面是否保留我不確定。M3 的 selected 狀態我記得是用 trailing／leading check icon 加 container 色，沒有「所有可勾項目預留一欄」的明文。**以上請 Planner 自行核對。**

## #51 — navbar 巢狀縮排

**DOM 巢狀（`red51.json` 的 `chain`）：**
- 展開式：Contact `nav.z-navbar > ul > li.z-nav`；Reply `… > li.z-nav > ul > li.z-navitem`；Settings `… > li.z-nav > ul > li.z-nav`；Edit profile `… > li.z-nav > ul > li.z-nav > ul > li.z-navitem`。**每一層多一組 `li.z-nav > ul`，光靠祖先選擇器就能分出三層**（`.z-navbar > ul > .z-nav > ul > .z-nav > ul > .z-navitem`）。
- collapsed popup：`ul.z-nav-popup` 是第一層 nav 自己的 `ul`（cave）被 `makeVParent` 搬到 `body`（旁邊還有 `span.z-nav-text.z-nav-text-popup` 顯示「Contact」）；Reply `body > ul.z-nav-popup > li.z-navitem`；Settings `ul.z-nav-popup > li.z-nav`；Edit profile `ul.z-nav-popup > li.z-nav > ul > li.z-navitem`。**popup 內兩層也分得出來**（`ul.z-nav-popup > li` vs `ul.z-nav-popup > li.z-nav > ul > li`），但 **`.z-navbar` 祖先不在了**，以 `.z-navbar` 為錨的選擇器碰不到 popup。水平 collapsed 的 popup 多一個 `z-nav-popup-horizontal`。
- popup 的開法（`Nav.ts`）：collapsed 第一層 nav **hover** 才開（`_doMouseEnter`），離開 100ms 後關，點第一層不做事；第三層要把游標移進 popup 再**點** Settings。量測時游標留在 popup 內。

| 模式 | 層 | 列 | `padding-left` | 圖示盒 x | 圖示 ink 左緣 | 文字 span 左緣 | 文字 ink 左緣 | 距容器左緣 |
|---|---|---|---|---|---|---|---|---|
| 展開式（navbar 左緣 32） | L1 | Home／About／Freeze／Logout | 16 | 48 | 51／51.5／53.5／51 | 74 | 75／74.5／75／75 | 43／42.5／43／43 |
| | L1 nav | Get Started／Contact | 16 | 48 | 51／50.5 | 74 | 74.5／74.5 | 42.5 |
| | L2 | Reply／Reply all／Inbox／Edit | **42** | 74 | 77.5／76.5／76.5／76.5 | 100 | **101** | 69 |
| | **L2 nav** | **Settings** | **16** | **48** | 50.5 | **74** | **74.5** | **42.5** |
| | **L3** | Edit profile／Keyboard shortcuts／Change password | **42** | 74 | 78.5／76.5／76.5 | 100 | **101／101／100.5** | 69／69／68.5 |
| collapsed popup（`ul.z-nav-popup` 左緣 283，底 (240,244,250)，`padding 4px 0`） | p-L2 | Reply／Reply all／Inbox／Edit | 16 | 299 | 302.5／301.5／301.5／301.5 | 325 | 326 | 43 |
| | p-L2 nav | Settings | 16 | 299 | 301.5 | 325 | 325.5 | 42.5 |
| | **p-L3** | Edit profile／Keyboard shortcuts／Change password | 42（列框從 **323** 起） | 365 | 369.5／367.5／367.5 | 391 | **392／392／391.5** | **109** |
| 水平 collapsed popup（左緣 202，`z-nav-popup-horizontal`） | p-L2 | Reply 等 | 16 | 218 | 221.5／220.5… | 244 | 245 | 43 |
| | p-L2 nav | Settings | 16 | 218 | 220.5 | 244 | 244.5 | 42.5 |
| | p-L3 | Edit profile 等 | 42（列框從 242 起） | 284 | 288.5／286.5／286.5 | 310 | 311／311／310.5 | 109 |
| 水平展開（navbar[3]，點 Contact 後下拉，只記 DOM） | L2 | Reply 等 | 42 | cnt+42 | — | cnt+68 | — | — |
| | L2 nav | Settings | 16 | cnt+16 | — | cnt+42 | — | 與 L1 同 |

- **J51-1：** Settings − Contact = **0**（要 ≥ 12）；Edit profile − Reply = **0** → 失敗，RED-correct。Jess 的 `jess-51-expanded.png` 就是這個狀態。
- **J51-2：** popup 內 p-L3 − p-L2 = **66px**，按字面通過。**但這不是縮排：** `probe-popup-ul.json` 顯示 popup 內 Settings 的巢狀 `ul` 計算值是 `list-style: circle outside`、`padding: 0 0 0 40px`、`li display: list-item`（瀏覽器預設；展開式 navbar 內的同一個 `ul` 是 `none` / `0px` / `li display:block`），列前畫出 **5×5 的圓圈 bullet**（ink @ x 307.5–312.5，最深 (31,32,33)，`red51-popup-l3.png`），第三層 hover 狀態層 **323–537**（popup 內緣 283，`probe-popup-ul-l3-hover.png`）。Jess 的 `jess-51-popup.png` 只開到第二層，沒看到這個。**依計畫原則（RED 若今天就通過 → 判定有問題）：J51-2 要重寫。**
- **狀態層（保護項，`red51-hover.json`，只量框內）：** 展開式五列（L1 item／L1 nav／L2 item／L2 nav／L3 item）水平 33–246、navbar 32–247.27 → 滿寬；垂直 = 整列（Home 182–227 含上緣外的白底，其餘 ±1px 內）。popup：Reply、Settings 284–537 滿寬；**Edit profile、Change password 323–537**（左 40px 是 (240,244,250) 的底，ΔE 0）。色：item hover 展開式 (216,218,221)、popup (205,208,213)；nav hover 展開式 (229,231,234)、popup (223,226,232)。
- **其他保護項數字：** 展開式列高 40、L1 pitch 41（188→229→270）、L2／L3 pitch 40；popup 列高 36、pitch 36；分隔 20px。展開箭頭（`::after`，content `""`）ink 8×4.5 @ x 224–232，三個 nav 列同；collapsed 第一層 icon 盒 @ +16、18×15、列高 35（`red51.json.collapsed_closed`）；水平 navbar[3]／[4]／[5] 第一層矩形見 `horizontal_3/4/5`。navbar 展開 Contact 後整體寬度 176.17 → 215.27（內容撐開），第一層 x 不變。

## 方法問題清單

1. **#48 J48-1 的「圖示左緣互差 ≤ 1px」用 ink 量不成立。** 同種、同盒的三列 `z-menuitem` 圖示 ink 左緣就差 2px（67／66.5／68.5，glyph 側邊距），對齊後也過不了。**METHOD-DEFECT（量法）。** 建議改為「圖示 ink **中心**互差 ≤ 1px」（今天 `z-menu` 列 71.5 vs 74，失敗、有鑑別力；盒左緣 65 全同、沒鑑別力）。文字那一半維持 ink 左緣（今天 5.5px、2.5px，失敗）。
2. （說明）**J48-1 涵蓋兩種混合：** A（iconSclass + 巢狀 `z-menu`，Jess 的）差 5px；C（image + iconSclass）差 2px。兩個 popup 都要在最終 run 量；若修法只動 `z-menu` 列，C 仍失敗。
3. **#51 J51-2 今天按字面通過（66px）。METHOD-DEFECT（判定不成立）。** 建議重寫為三條：(a) popup 內第三層文字左緣比第二層多 **12–30px**（與展開式的 L1→L2 階差 26 同量級，上限擋掉 40px 的預設 padding）；(b) popup 內第三層列框左緣與第二層相同（今天 323 vs 283）且列前除圖示外沒有 ink（今天有 bullet）；(c) 第三層 hover 狀態層觸及 popup 內緣（今天 323 起）。這三條今天都失敗。
4. **#51 保護項「狀態層整列滿寬」對 popup 第三層今天就不成立。METHOD-DEFECT（歸類）。** 展開式三層與 popup 第二層今天通過，留作保護項；popup 第三層併入第 3 項。
5. （說明）**#51 保護項「第一層位置不變」：** navbar 寬度隨展開內容變（176.17 → 215.27），要在同一開合狀態比，比 x 與列高不比寬。
6. （說明）**#51 popup 的 `ul` 在 `body` 下、沒有 `.z-navbar` 祖先**；展開式三層可純靠祖先選擇器區分（預測成立），popup 內兩層也可（`ul.z-nav-popup > li.z-nav > ul`），但 popup 內巢狀 `ul` 今天拿的是瀏覽器預設樣式，不只是縮排問題。檔案在 zkcml（`nav.css`），計畫已註明。
7. （說明）**#49 只記數字**：預留欄 24px（文字 40 vs 純文字 16），勾選後文字 0 位移；MD3 數字是憑記憶，未核對。D54 照計畫問。
8. （說明）**第一個 menubar 的 Help 是 disabled**，About 子選單用垂直 menubar 量；最終 run 要用同一個。collapsed navbar 的 popup 要用 **hover** 開、游標留在 popup 內再點 Settings（`Nav.ts`），量法寫進定稿。

方法缺陷：第 1 項（#48 圖示 ink 左緣）、第 3 項（#51 J51-2 今天就過、且問題不是縮排）、第 4 項（#51 popup 第三層狀態層不能當保護項）。J48-1 文字部分與 J51-1 今天失敗，RED 成立。

RED8: METHOD-DEFECTS
