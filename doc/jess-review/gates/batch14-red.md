使用 8085（C 線，批次 14 RED）

# Gate：第十四批 RED run，CSS follow-ups：toast info 圖示對比、notification 與 toast 同色階、水平 navbar 巢狀縮排、menu 列 image／iconSclass 文字起點、S6 三個已知失敗（P1 P2 P3 P4 S6）

- **伺服器：** 8085，java pid 88428（2026-10-09 21:11:05 啟動；`zkpreview/build/gretty_ports.properties` 21:11:06）。**沒有 kill、沒有重啟。** 新鮮度：(1) 伺服的 `js/zul/menu/css/menu.css.dsp`（md5 `939331cf…`）與 `js/zkmax/nav/css/nav.css.dsp`（`38b767a2…`）各與 `zul/build/resources/main/web/js/zul/menu/css/menu.css.dsp`、`../zkcml/zkmax/build/resources/main/web/js/zkmax/nav/css/nav.css.dsp` **md5 相同**；(2) `toast.css`／`notification.css` 沒有獨立的 `.css.dsp`（單檔 URL 404），它們打包在 `zul/css/zk.wcs`（200／542,919 bytes，md5 `a5a526ee…`）：從伺服的 zk.wcs 抽出的規則 `.z-toast-info .z-toast-icon{color:var(--zk-toast-accent)}`、`.z-notification-info .z-notification-content{background-color:color-mix(in srgb, var(--zk-color-status-info) 12%, var(--zk-color-surface))…}`、`.z-notification-info .z-notification-left{border-right-color:var(--zk-color-status-info)}`、warning／error 的 `-right/-up/-down` 用 `color-mix(… 12%, transparent)`，與 `zul/src/main/resources/web/js/zul/wgt/css/{toast,notification}.css` 原始碼逐條相同；(3) 四個 `.css` 的 `zul/src`＝`zul/build`、`zkmax/src`＝`zkmax/build` md5 相同；(4) `zul/src`、`zul/build`、`zkcml/zkmax/src`、`zkcml/zkex/src`、`zkpreview/src` 底下沒有任何 `.css/.css.dsp/.zul/.xml/.svg` 比 `gretty_ports.properties` 新 → 伺服的是現在的 build。
- **日期：** 2026-10-09，Verifier（Fable），量現在的程式碼（Playwright 1.59.1、Chromium 147.0.7727.15、viewport 1280×900、deviceScaleFactor 2、`ignoreDefaultArgs:['--hide-scrollbars']`；注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`、游標停在 (2,2)；每個狀態都在重新載入的頁面上量；forced-colors 用 `newContext({forcedColors:'active'})`＋`emulateMedia`）。
- **方法：** lines/line-c-plan.md（過程資料已清除） 第六節。ΔE 一律 **CIE76**（lib.js）；對比用 WCAG 2.x 相對亮度；位置用像素 ink（與底色 ΔE ≥ 8 的像素外接矩形，0.5px 粒度）；computed style 只記錄供追溯（icon colour、border colour、padding-left）。
- **腳本與原始輸出：** `gates/batch14-red/`：`lib.js`（複製自 batch11-red）、`m14.js`（複製自 batch11-red/m11.js，用 `inkBox`、`menuRows`、`measurePopup`）、`red-p1p2.js/.json/.log`＋`p1-toast-*.png`、`p12-toast-forced-info.png`、`p2-notification-gallery.png`、`p2-pointer-{info,warning,error}-{left,right,up,down}.png`、`red-p3.js/.json/.log`＋`probe-p3-nested.js/.log`＋`p3-*.png`、`red-p4.js/.json/.log`＋`p4-*.png`、`s6-diff.js/.json/.log`、`s6-shift.js`、`playwright.log`／`playwright-tablet-run2.log`＋`s6-*-band*.png`（expected｜actual｜diff 並排裁圖）。Playwright 的 `--output` 都指到 scratchpad（`…/batch14-red/s6/`），沒有碰 `test-results`，沒有跑 `forced-colors-gallery`，沒有 `--update-snapshots`。
- **沒有修改**任何 theme／CSS／TS／Java／預覽頁／spec 檔案，沒有看 diff，沒有 commit；頁內只注入停動畫規則與（P1 保護項）`:root{--zk-toast-accent:#6750a4}`。P2 的箭頭是在瀏覽器裡呼叫 `zul.wgt.Notification.show(msg, null, {ref, pos, type, dur:-1, closable:true})`（ref = "Middle Center" 按鈕，pos = `end_center`／`start_center`／`after_center`／`before_center` → `-left`／`-right`／`-up`／`-down`），不是改檔。
- **讀原始碼的地方（只為解釋現象）：** `toast.css`、`notification.css`（variants 區）、`Notification.ts:136-240`（`_fixPadding`／`_fixarrow` 的方向對應）、`nav.css:188-221、271-279、306-318`（`.z-nav > ul {overflow:hidden}`、垂直與 popup 的逐層 padding、`.z-navbar-horizontal .z-nav > ul`）、`Nav.ts:256-266`（collapsed 的 topmost nav 不響應 click）、`menu.css:233-239、385-405、470-475`（image 16px／iconSclass 18px）。

## 結論摘要

| 項目 | 判定檢查今天 | 保護項今天 | 方法問題 |
|---|---|---|---|
| P1 J-P1-1 toast info 圖示 ink 對底色對比 ≥ 4.5 | 圖示 ink 最飽和＝最深＝中位 **(0,127,171)**（480 px 全同色），底 **(224,232,244)** → **3.68:1** → **失敗，RED-correct**（與計畫 token 算出的 3.68 相同；看板的 3.13 不是對這個底色） | — | 無 |
| P1 保護項 (a) 旋鈕 | 注入 `--zk-toast-accent:#6750a4` 後 info 圖示 ink **(103,80,164)**，與預設 ΔE **44.89**（≥ 10 ✓）；warning／error 圖示、三種底色 ΔE 0 | ✓ 今天成立 | 無 |
| P1 保護項 (b)(c) | (b) warning 圖示 (122,54,30) 對底 (255,215,197) **6.65**；error 圖示 (127,0,10) 對底 (254,205,199) **7.75**；close 圖示 ink 8×8（最深 info (87,90,95)／warning (99,83,76)／error (99,80,77)）；圖示 ink 19×19 @ content+16.5、垂直置中（cy 210 = 列中心）；(c) forced-colors 下三種圖示 ink 491／491／566 px、黑 (0,0,0) 對白底 21:1 | ✓ 基準已記 | 無 |
| P2 J-P2-1 同嚴重度 notification 底色與 toast 底色 ΔE ≤ 2 | info **5.07**（(224,240,245) vs (224,232,244)）、warning **11.97**（(247,232,224) vs (255,215,197)）、error **13.54**（(250,230,230) vs (254,205,199)）→ **三者都失敗，RED-correct** | — | 無 |
| P2 J-P2-2 notification info 圖示對比 ≥ 4.5 | (0,127,171) 對 (224,240,245) → **3.88:1** → **失敗，RED-correct**（warning 7.40、error 9.19 今天就過） | — | 無 |
| P2 J-P2-3 四向箭頭色與 content 底色 ΔE ≤ 2 | **left**（`status-*` 實色）：info (0,127,171) ΔE **52.64**、warning (255,152,0) **79.12**、error (239,83,80) **71.58** → 失敗；**info right／up／down** 都是 (224,232,244)（`surface-container-highest`）ΔE **5.07** → 失敗；**warning／error 的 right／up／down** 是 12% 半透明：up ΔE **0**（通過）、right **2.11**（剛好失敗）、down **4.22**（失敗）——同一個 token 三個方向三種結果，因為半透明三角形把底下的 elevation 陰影透出來（down 落在 content 下方的陰影帶最深）。整體 **失敗，RED-correct** | — | **說明**：量半透明箭頭要寫明「三角形內部、離 content 邊 ≥ 2px」，並預期方向差（見方法問題 3） |
| P2 保護項 | notification 圖示 ink 19×19 @ content+16.5；有箭頭時 content 260×43.5、root padding 12px 在箭頭側、箭頭盒 20×20 在 root 外 8px（`left:-8px` 等）；close 圖示 (14×17 盒) 見 JSON；forced-colors 下三種圖示可見（黑 21:1）；toast 除 P1 外的數字見上列 | ✓ 基準已記 | 無 |
| P3 J-P3-1 水平下拉：第二層群組標題左緣比第一層項目大 12–30px | 水平 "Expanded" navbar（`.z-navbar[3]`）Contact 下拉：L1 項目（Reply…）文字 ink **ul+68.89**（padding-left 42px）；巢狀群組標題 Settings 文字 ink **ul+42.39**（padding-left 16px）→ 標題比項目**少 26.5px**（反向）→ **失敗，RED-correct** | — | 無 |
| P3 J-P3-1 第三層項目比第二層大 12–30px | **量不到像素。** Settings 展開後其 `ul`（`j5nOr1-cave`）被 `.z-navbar-horizontal .z-nav > ul` 一起變成 `position:absolute; z-index 1000`，落在父下拉 `ul` 的底邊（父 132–360，子 356–484），父 `ul` 又 `overflow:hidden`（`nav.css:188-190`）→ 子清單只露出 4px，三列 `elementFromPoint` 命中的都是頁面其他 div 或下一個 navbar；DOM 上 L3 項目 padding-left 42px（= L1 項目，`.z-nav > ul .z-navitem-content` 不分層）。見 `p3-h-contact-settings-open.png`、`p3-probe-nested-page.png`、`probe-p3-nested.log` | — | **METHOD-DEFECT（量法前提）**：水平模式的巢狀清單今天根本畫不出來，J-P3-1 第二句無法以像素判定；也表示 P3 的修法不只是縮排（見方法問題 1） |
| P3 保護項 | 第一層列高 40、下拉 180 寬、bg (240,244,250)、Reply hover 狀態層滿寬 423.6–603.6（= ul 寬，色 (210,213,219)）；**垂直** navbar：L1 項目 ul+69、Settings 標題 ul+68.5、L2 項目 ul+95（每層 +26）；**popup 模式**（collapsed 水平 hn1，hover 開 `.z-nav-popup`）：L1 ul+43、Settings ul+42.5、L2 ul+69（+26）、列高 36 | ✓ 基準已記 | 無 |
| P4 J-P4-1 同 popup 內有圖示列的文字 ink 左緣互差 ≤ 1px | menubar1 Project popup：image 列 New／Open／Save／Exit 文字 ink **popup+42／41.5／41.5／42**，iconSclass 列 Save As...（disabled）／Restart **+43.5／+44** → 互差 **2.5px** → **失敗，RED-correct** | — | 無 |
| P4 J-P4-1 圖示 ink 中心互差 ≤ 1px | image 列中心 x **73.0**（四列全同），iconSclass 列 **74.0** → 互差 **1.0px** → **今天剛好通過（在容差邊界上）**；垂直：image 列 cy = 列中心（231 vs 231），iconSclass 列 cy 比列中心高 1px（338 vs 339、419 vs 420） | — | **METHOD-DEFECT（容差）**：這半句今天不失敗，沒有鑑別力；若修法移動文字而不動圖示，它永遠過；建議改為「≤ 0.5px」或改量「圖示 ink 中心距 popup 左緣 = 25 ± 0.5」（見方法問題 2） |
| P4 保護項（#48 混合） | menubar[1] File popup（iconSclass 列＋巢狀 `z-menu` Open）：文字 ink 互差 **0.5px**（+44／43.5／43.5／44）、圖示中心互差 **0**（74.0）、箭頭 ink 183–187；Open 子選單 Project...／File... 互差 0；列高 36、列距 0、分隔線 1px；image 列 `<img>` natural 16×16、盒 16×16；iconSclass 盒 18×20；hover Open 底 (237,237,237) ΔE 6.25；standalone menupopup（全 image）互差 0.5；水平 menubar1 頂列：Project 文字 ink 85、image ink 64.5–79.5、列高 48 | ✓ 基準已記 | 無 |
| S6 `calendar-tablet` | Playwright：**1145 px（ratio 0.01，無容差 → 失敗）**；自算：thr .2 1432 px（0.069%）、任何差 1883 px；expected 與 actual **都是 834×2493**（沒有多出一段）；差異帶 y1425–1474、y1914–1963（各 504 px：空值 datebox 的日曆圖示右移約 7px）、y2203–2404 五條 48px 間距的帶（每條 41–123 px：月曆列的週末 8／14 從深色變灰，即 1abb82d3b7「grey out disabled weekend days」2026-10-08）；x 50–569。兩次 actual **0 px 差** → **過期 baseline（2026-09-12 切），不是迴歸也不是字型競態** | — | **說明**：計畫寫「現在多了 #6 的一段（y 2200–2404）」與實測不符——高度相同，該區是週末灰化（見方法問題 4） |
| S6 `slider-tablet` | Playwright：**2354 px（ratio 0.01 → 失敗）**；自算 thr .2 2669 px（0.288%）、任何差 17,359 px（1.87%），全部在 y852–1001、x45–403：圓形 slider 的值標籤 "40"／"75" 從有底色方框變成純文字，弧線末端位置變（63ac5567d8「centre the slider tooltip on its thumb」／64f1c07b9d，2026-10-07/08）。兩次 actual **0 px 差** → **過期 baseline** | — | 無 |
| S6 `grid-header-gallery` | Playwright：**尺寸不符 expected 1280×4525、actual 1280×4522**（三次都一樣，尺寸不符就直接失敗）；自算（相同高度內）thr .2 51,831 px（0.895%）、任何差 141,831 px（2.45%）；31 條差異帶從 y33 起：標題字級（y33–49，201d6d2a64 renumber）、欄頭對齊（y216–229，Jess #39）、排序箭頭從文字前的 caret 變文字後的 ↑↓（y1531–1650、y1707–1828，Jess #35）；位移分析：y<1500 以 k=0 對齊、y1600–1699 以 k=1、y≥1700 以 k=2 對齊 → 高度少的 3px 來自 y≈1500–1600 與 ≈1700 兩處各 1px 加底部 1px。**三次 actual 兩兩 0 px 差** → 今天是**穩定、確定性的過期 baseline（2026-09-11 切）**，**不是字型競態** | — | **說明**：計畫的「字型載入競態」在今天的三次 run 沒有出現；重切後仍照計畫連跑三次 |

## P1 — toast info 圖示對比

**量法：** `toast.zul` State Gallery 三張靜態 toast（原生 DOM，class 與 widget 相同）；另以 "Persistent (closable)" 按鈕叫出真的 `Toast.show` 交叉驗證（`p1-toast-live-info.png`：圖示 ink (0,127,171)、底 (224,232,244)、3.68 → 與靜態一致）。

| toast | content 底（ink 外左 3–9px 中位） | 圖示 ink（n、盒、色） | 對比 | 文字最深 | close ink 最深 |
|---|---|---|---|---|---|
| info | (224,232,244) = `--zk-toast-bg` → `surface-container-highest` #e0e8f4 | 480 px，19×19 @ (48.5,200.5)，(0,127,171) = `--zk-toast-accent` → `status-info` #007fab | **3.68** | (29,30,32) 13.52 | (87,90,95) |
| warning | (255,215,197) = `warning-container`（oklch .92） | 478 px，(122,54,30) = `on-warning-container` | 6.65 | (33,28,26) 12.67 | (99,83,76) |
| error | (254,205,199) = `error-container` | 547 px，(127,0,10) = `on-error-container` | 7.75 | (33,27,26) 11.95 | (99,80,77) |

- **J-P1-1：** 3.68 < 4.5 → **失敗，RED-correct**。
- **旋鈕（保護項 a）：** 重新載入後注入 `:root{--zk-toast-accent:#6750a4}`：info 圖示 ink **(103,80,164)**，ΔE 44.89 對預設；底色、warning／error 圖示、close ΔE 0。修法「圖示色與 on-surface 混深」後要再量這條（旋鈕值混深後 ΔE 仍須 ≥ 10）。
- **forced-colors（保護項 c）：** 三種圖示 ink 491／491／566 px，黑對白 21:1。
- **computed（追溯）：** `.z-toast-icon` 20×20 @ left 16、`font-size 20px`；warning／error 圖示的 computed color 是 `oklch(0.42 0.1013 39.55)`／`oklch(0.375 0.1542 26.41)`（相對色，`parseRgb` 不能解析——像素值才可靠）。

## P2 — notification 與 toast 同色階、箭頭色

| 嚴重度 | notification 底 | toast 底 | ΔE | notification 圖示 ink | 對比 |
|---|---|---|---|---|---|
| info | (224,240,245)（`color-mix(status-info 12%, surface)`） | (224,232,244) | **5.07** | (0,127,171) | **3.88** |
| warning | (247,232,224)（`color-mix(warning 12%, surface)`） | (255,215,197) | **11.97** | (122,54,30) | 7.40 |
| error | (250,230,230) | (254,205,199) | **13.54** | (127,0,10) | 9.19 |

**箭頭（`p2-pointer-<sev>-<dir>.png`；三角形內部中位色，離 content 邊 ≥ 2px）：**

| 嚴重度 | left（`end_center`，`border-right-color`） | right（`start_center`） | up（`after_center`） | down（`before_center`） |
|---|---|---|---|---|
| info | (0,127,171) ΔE **52.64** | (224,232,244) **5.07** | (224,232,244) **5.07** | (224,232,244) **5.07** |
| warning | (255,152,0) **79.12** | (241,226,218) **2.11** | (247,232,224) **0** | (235,220,212) **4.22** |
| error | (239,83,80) **71.58** | (244,224,224) **2.11** | (250,230,230) **0** | (238,218,218) **4.22** |

- computed：info 的 right／up／down 是 `rgb(224,232,244)`；warning／error 的是 `color(srgb … / 0.12)`（半透明），left 是 `status-*` 實色。半透明箭頭的像素色隨落點而變：up 在卡片上方（沒有陰影）＝content 底；right 落在側邊陰影 ΔE 2.11；down 落在 `elevation-2` 的下方陰影帶 ΔE 4.22。
- 箭頭幾何：盒 20×20、`border:10px`，root 在箭頭側 padding 12px，箭頭 `left/right/top/bottom:-8px`；有箭頭時 content 260×43.5（table-cell）、圖示 left 24（-left）／12（其他）。
- **J-P2-1、J-P2-2、J-P2-3 今天都失敗，RED-correct**（J-P2-3 只有 warning／error 的 up 今天通過）。

## P3 — 水平 navbar 下拉的巢狀縮排

**頁面結構（`red-p3.json.navbars`）：** `.z-navbar[0]` 垂直 Expanded、[1][2] 垂直 collapsed、**[3] 水平 Expanded**（含巢狀 Contact > Settings）、[4] 水平 collapsed（hn1，popup 模式）、[5] Simple nav items、[6] autoclose=false 垂直。水平 navbar 帶巢狀 `z-nav` 的例子**存在**（[3] 與 [4] 的 Contact > Settings）。

| 模式 | 列 | padding-left | 文字 ink 左（ul+） | 圖示 ink 左（ul+） | 備註 |
|---|---|---|---|---|---|
| 水平下拉（[3] Contact） | L1 navitem Reply／Reply all／Inbox／Edit | 42px | **68.89** | 44.9（中心 50.9） | 列 180×40，bg (240,244,250) |
| | L1 nav 標題 Settings | **16px** | **42.39** | 17.9（中心 24.9） | 標題比項目**左 26.5px** |
| | L2 navitem Edit profile／Keyboard shortcuts／Change password | 42px（DOM text.l 491.6 = L1） | **量不到**（被父 ul `overflow:hidden` 裁掉，子 ul absolute 落在父底邊 356–484 vs 父 132–360） | — | `elementFromPoint` 命中頁面 div／其他 navbar |
| 垂直（[0] Contact） | L1 navitem | 42px | 69 | 44.5 | 列 241×40 |
| | L1 nav 標題 Settings | 42px | 68.5 | 44.5 | |
| | L2 navitem | 68px | 95 | 70.5 | 每層 +26 |
| popup（[4] hn1 hover Contact → `.z-nav-popup z-nav-popup-horizontal`） | L1 navitem | 16px | 43 | 19.5 | 列 215×36 |
| | L1 nav 標題 Settings | 16px | 42.5 | 18.5 | |
| | L2 navitem | 42px | 69 | 46.5 | 每層 +26（#51 修好的） |

- **J-P3-1 第一句：** 標題 42.39 − 項目 68.89 = **−26.5**（要 +12～+30）→ **失敗，RED-correct**。
- **J-P3-1 第二句：** L3 項目無像素可量（方法缺陷 1）。DOM 上 L3 的 padding-left 42 = L1 項目 → 即使畫得出來，今天也會以「差 0」失敗。
- **保護項：** Reply hover 狀態層滿寬（423.6–603.6 = ul）；垂直與 popup 數字如上表。

## P4 — menu 列 image 與 iconSclass 文字起點

**menubar1 Project popup（`p4-project-popup.png`，popup 48,208 160×235，content 內縮 1px，列高 36）：**

| 列 | 圖示種類 | 圖示盒 | 圖示 ink（l–r／w×h／中心 x,y） | 列中心 y | 文字 ink 左（popup+） |
|---|---|---|---|---|---|
| New | image 16 | 65–81 16×16 | 65–81 16×16 / **73.0**, 231.0 | 231 | **42** |
| Open | image | 65–81 | 65.5–80.5 15×15.5 / 73.0, 267.25 | 267 | 41.5 |
| Save | image | 65–81 | 65–81 16×14.5 / 73.0, 303.75 | 303 | 41.5 |
| Save As...（disabled, opacity .38） | iconSclass 18 | 65–83 18×20 | 66.5–81.5 15×15 / **74.0**, 338.0 | 339 | **43.5** |
| Exit | image | 65–81 | 66–80 14×16 / 73.0, 384.0 | 384 | 42 |
| Restart | iconSclass | 65–83 | 66.5–81.5 15×15 / 74.0, 419.0 | 420 | **44** |

- **J-P4-1：** 文字 ink 互差 **2.5px**（> 1）→ **失敗，RED-correct**；圖示中心 x 互差 **1.0px**（≤ 1，邊界通過）；iconSclass 列的圖示比列中心高 1px。
- **#48 混合（menubar[1] File popup）：** New／Open(menu)／Save／Exit 文字 ink +44／43.5／43.5／44（互差 0.5）、圖示中心 74.0（互差 0）、Open 箭頭 ink 183–187；子選單 Project...／File... 互差 0。**今天通過**（保護項基準）。
- **standalone menupopup（全 image）：** +42／41.5／41.5／42，圖示中心 134.0。水平 menubar1 頂列：Project 文字 ink 85、image ink 64.5–79.5、Help 207／187–202、列高 48。

## S6 — 三個已知失敗的歸因

| 案例 | baseline 日期 | Playwright 結果 | 差異位置與內容 | 穩定性 | 歸因 |
|---|---|---|---|---|---|
| `calendar-tablet`（project `tablet`，`tablet-calendar › gallery`） | 2026-09-12（5c4888e54a） | 1145 px（ratio 0.01；無容差） | 834×2493 兩邊同尺寸；y1425–1474、y1914–1963：空值 datebox 的日曆圖示右移約 7px（各 504 px）；y2203–2404 五條（48px 間距）：月曆列週末 8／14 由深變灰（`s6-calendar-tablet-band*.png`） | run1 vs run2 actual 0 px 差 | **過期 baseline**（1abb82d3b7 等 2026-10-08 的 calendar／datebox 變更）；可直接重切 |
| `slider-tablet`（`tablet-slider › gallery`） | 2026-09-12 | 2354 px（ratio 0.01） | y852–1001、x45–403：圓形 slider 的 "40"／"75" 值標籤由方框變純文字、弧線末端位置（`s6-slider-tablet-band*.png`） | 0 px 差 | **過期 baseline**（63ac5567d8／64f1c07b9d）；可直接重切 |
| `grid-header-gallery`（project `gallery`，`gallery › grid-header`） | 2026-09-11（cd02d18943） | 尺寸不符 4525 → 4522（三次相同） | 標題字級、欄頭對齊、排序箭頭位置與字形（Jess #35／#39、字級 renumber）；高度少 3px 來自 y≈1500–1600、≈1700 各 1px 加底部 1px（`s6-shift.js`） | 三次 actual 兩兩 0 px 差 | **過期 baseline、確定性**；今天**沒有**觀察到字型競態；重切後仍連跑三次確認 |

## 方法問題清單

1. **P3 J-P3-1 第二句（第三層項目）量法前提不成立——水平下拉內的巢狀清單今天畫不出來。** `.z-navbar-horizontal .z-nav > ul`（`nav.css:271`）是後代選擇器，巢狀 nav 的 `ul` 也變成 `position:absolute`，落在父下拉的底邊，再被 `.z-nav > ul {overflow:hidden}`（`nav.css:188-190`）裁掉；`elementFromPoint` 證實三列都不在畫面上。定稿要先決定修法範圍：(a) P3 連同「巢狀清單在水平下拉內要以 static 流式展開」一起修（selector 改成 `.z-navbar-horizontal > ul > .z-nav > ul`），J-P3-1 第二句才可量；或 (b) 本批只修縮排、第二句改以 DOM `padding-left` 判定並把「巢狀清單不可見」另開 follow-up。**METHOD-DEFECT（量法前提／範圍）。**
2. **P4 J-P4-1 的「圖示 ink 中心互差 ≤ 1px」今天剛好 1.0px 通過**，沒有鑑別力；若修法只動文字（例如把 image 盒撐成 18 或給 iconSclass 盒 16），圖示中心會變或不變都過。建議改為「≤ 0.5px」或改成絕對基準「圖示 ink 中心距 popup 左緣 25 ± 0.5px（今天 image 25.0、iconSclass 26.0）」並明寫修後文字 ink 目標（+42 或 +44 二擇一）。**METHOD-DEFECT（容差）。**
3. **P2 J-P2-3 的半透明箭頭依方向落點不同：** 今天 warning／error 的 up 已 ΔE 0、right 2.11、down 4.22（陰影透出）。修後改用不透明 `*-container` token 這個差會消失，但定稿要寫明取樣區（三角形內部、離 content 邊 ≥ 2px、中位色），且 info 的 right／up／down 今天是 `surface-container-highest`（ΔE 5.07）不是 `status-info`——計畫只提到 left 用 `status-*`。**說明（補強）。**
4. **S6 `calendar-tablet` 的描述與實測不符：** 計畫寫「現在多了 #6 的一段（y 2200–2404），重切要含」，實測 expected／actual 同高 2493，y2203–2404 是週末灰化；三個案例今天都是確定性的過期 baseline，`grid-header` 的「字型競態」在三次 run 中沒出現。計畫 Phase 5 的「原因是過期 baseline 才重切」條件三者都成立。**說明。**
5. **P1 的 computed 顏色不可當基準：** warning／error 圖示與底色的 computed 是 `oklch(from …)` 相對色字串，`parseRgb` 解析不了；最終 run 的 (b) 「warning／error 圖示 ΔE ≤ 1」要用像素 ink（今天 (122,54,30)／(127,0,10)）。**說明。**
6. **P3 保護項「popup 模式不變」的基準列高是 36、水平下拉是 40**：兩種模式列高不同是今天的事實，不要在修後誤判。**說明。**

## 發現（方法沒寫的）

1. **水平 navbar 的巢狀 nav 在下拉內展開後不可見**（方法問題 1）——Jess #8 批 follow-up 的實際症狀不只是縮排，使用者點 Settings 只會看到 chevron 翻轉。
2. **水平下拉內的群組標題比項目少縮 26.5px**（標題 padding-left 16、項目 42）：`.z-nav > ul .z-navitem-content` 給所有層級的 navitem 42px，而 `.z-nav-content` 的 +26 只在 `.z-navbar-vertical` 與 `.z-nav-popup` 下有規則。
3. **notification 半透明箭頭會透出 elevation 陰影**（down 比 up 深 ΔE 4.22）。
4. **iconSclass 列的圖示 ink 比列中心高 1px**（18px 字 / 20px 盒），image 列剛好置中。
5. **toast 與 notification 的 close 圖示 ink 都是中性灰（on-surface × opacity .7／.6）**，與 P1 無關，記錄供修後比對。

方法缺陷：第 1 項（P3 第三層不可量／修法範圍）、第 2 項（P4 圖示中心容差無鑑別力）。判定 J-P1-1、J-P2-1、J-P2-2、J-P2-3（除 warning／error up）、J-P3-1（第一句）、J-P4-1（文字半句）今天都失敗；J-P4-1 圖示半句與 J-P2-3 的兩格今天通過；S6 三案例都是過期 baseline。RED 成立但有兩處方法缺陷。

RED14: METHOD-DEFECTS
