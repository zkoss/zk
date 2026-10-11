使用 8085（A 線，批次 11 RED）

# Gate：第十一批 RED run，C 類容器：accordion 標題字型、rounded panel 外框、window 拖曳 ghost、borderlayout N／S padding（#53 #54 #55 #57）

- **伺服器：** 8085，java pid 77212（`zkpreview/build/gretty_ports.properties` 20:08:22 起聽，與計畫第七節「環境與所有權」記的同一個行程）。**沒有 kill、沒有重啟、沒有碰 8105。** 新鮮度：伺服的 `zul/tab/css/tabbox.css.dsp`（md5 `1ef89c40…`）、`zul/wnd/css/panel.css.dsp`（`f9bef75a…`）、`zul/wnd/css/window.css.dsp`（`6229a09d…`）、`zul/layout/css/borderlayout.css.dsp`（`1e907c61…`）各與 `zul/build/resources/main/web/js/...` 的檔案 md5 **逐一相同**；四個 `.css` 的 `zul/src` 與 `zul/build` md5 也相同；`zul/src`、`zul/build`、`zkcml/zkmax/src`、`zkcml/zkex/src`、`zkpreview/src` 底下沒有任何 `.css/.css.dsp/.svg/.xml/.zul` 比 `gretty_ports.properties` 新；`zk.wcs` 200／541,365 bytes → 伺服的是現在的 build。
- **日期：** 2026-10-08，Verifier，量現在的程式碼（Playwright 1.59.1、Chromium 147.0.7727.15、viewport 1280×900、deviceScaleFactor 2、`ignoreDefaultArgs:['--hide-scrollbars']`；注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`、游標停在 (2,2)；每個狀態都在重新載入的頁面上量；forced-colors 用 `newContext({forcedColors:'active'})`）。
- **方法：** lines/line-a-plan.md（過程資料已清除） 第七節「第 11 批」。ΔE 一律 **CIE76**（lib.js）。位置、尺寸用像素 ink（與底色 ΔE ≥ 8 的像素外接矩形）；computed style 只讀計畫允許的值（字級、字重、行高、opacity、`.z-north-body` 的 padding、ghost 的 outline／z-index），旁邊並列 DOM rect 供追溯。ink 數字 0.5px 粒度（DPR 2）。
- **腳本與原始輸出：** `gates/batch11-red/`：`lib.js`（複製自 batch8-red）、`m11.js`（複製自 batch8-red/m8.js，用 `inkBox`）、`probe-dom.js/.json`＋`probe-{tabbox,panel,window,borderlayout}-page.png`（DOM 形狀探針）、`probe-bl.js/.json`（borderlayout 區域結構）、`probe-ns-overflow.js/.json`＋`probe-ns-overflow-bl0-north.png`（#57 的 North／South 溢出探針）、`red53.js/.json/.log`＋`red53-{horizontal0,horizontal0-hover,vertical3,combobox}.png`、`red53-acc-{textOnly,withImages}-{rest,tab1-zoom,hover-tab2,tab2-selected}.png`、`red54.js/.json/.log`＋`red54-{normal,forced}-p0…p12.png`、`red54-{normal,forced}-jess-pair.png`、`red54-{normal,forced}-page.png`、`red55.js/.json/.log`＋`red55-{jess,normal}-{blank,content}-{idle,idle-page,dragging-page,ghost,dropped}.png`、`red55-{jess,normal}-forced-blank-*.png`、`red55-panel-dragging-page.png`、`red57.js/.json/.log`＋`red57-bl0…bl5.png`、`red57-jess-view.png`、`ref/jess-{53,54,57}-current.png`、`ref/jess-55-frame35.png`（Jess 的 issue 截圖；#55 GIF 第 35 幀）。
- **沒有修改**任何 theme／CSS／TS／Java／預覽頁檔案，沒有看 diff，沒有 commit；頁內只注入 lib.js 的停動畫規則。#55 的 panel 部分是在瀏覽器裡用 widget API `setFloatable(true)`＋`setMovable(true)` 改執行期狀態（預覽頁沒有可拖曳 panel），不是改檔。
- **讀原始碼的地方（只為解釋現象，不是判定依據）：** `Window.ts:50-77`（`_ghostmove`）、`Panel.ts:196-250、1090-1115、1427-1466`（`setMovable/setFloatable/_initMove`、drag 選項）、`panel.css:176-183`／`window.css:204-209`（ghost 規則）、`zul/css/tokens/_forced-colors.css:27-39`（forced-colors 的 `.z-panel` 邊框）。
- **選取 widget 的方式：** DOM id 是 uuid；一律用 class + 頁面順序（`document.querySelectorAll('.z-tabbox')[i]`、`.z-panel[i]`、`.z-window[i]`、`.z-borderlayout[i]`），再用 `zk.Widget.$(n)` 讀 `getBorder()/getMode()/isOpen()` 標註。

## 結論摘要

| 項目 | 判定檢查今天 | 保護項今天 | 方法問題 |
|---|---|---|---|
| #53 J53-1 accordion 標題與水平 tab 同狀態的 font-size／weight／line-height 字串相等、ink 高差 ≤ 1px | 水平 **14px／500／20px**，accordion **16px／500／20px**（三個狀態都一樣）→ 字級不同；同字 "Tab1" 的 ink 高：垂直 mold（14px）**11**，accordion **12.5** → 差 **1.5px** → **失敗，RED-correct** | — | 無（補充：水平 "Tab States" 的字是 Default／Selected／Disabled，glyph 不同；同字比對用垂直 mold 的 Tab1–Tab3，數字一致 14px／11px） |
| #53 圖示事先判準（只記數字） | accordion chevron ink **11×7**；同族 combobox chevron ink **8×5**（右緣距 12.08、中心 = 控制項中心）→ 寬 **+37.5%**、高 **+40.0%**，**兩邊都超過 25%**。右緣距 accordion 14.5 vs combobox 12.08；chevron 垂直中心：選取（朝上）比列中心**高 2px**、未選取（朝下）**低 2px** | — | 結論由 Planner 下（計畫：超過 25% → 開 D56 問） |
| #53 保護項 (a)–(f) | — | (a) 五列高 **48**（pitch 49，列間 1px (224,224,224) 分隔線）、展開 cave 56；(b) 文字 ink 左緣 tab+16.5（disabled +17）、chevron ink 右緣距 tab 右 14.5；(c) 選取底 (213,230,255) ΔE 16.78、文字最深 (7,27,62)；未選取白底、文字 (33,33,33)；hover (237,237,237) ΔE 6.25；disabled opacity 0.38、文字 (217,217,217)、chevron (196,196,196)；(d) cave `.z-tabpanel-content` 13px／20.8px／400、padding 16px；(e) 水平 `red53-horizontal0.png`（md5 `efc1008c…`）、垂直 `red53-vertical3.png`（`4245553f…`）留作基準；(f) 點 Tab2 後 Tab2 列移到 582–630、cave 630–686（56px）、Tab1 收合到只剩 48px 列 → 動畫完成 ✓ | 無 |
| #54 J54-1 rounded 的 4 邊外緣線 ΔE ≥ 6 且與 normal 邊框色 ΔE ≤ 3 | 六個 rounded（含無標題、collapsed）4 邊全是 (255,255,255) **ΔE 0**；normal 4 邊 (224,224,224) ΔE 10.82 → **失敗，RED-correct**。root class：rounded `z-panel z-panel-noborder`（**不含** `z-panel-noframe`）、normal `z-panel z-panel-noframe`、none `z-panel z-panel-noborder z-panel-noframe` ✓ 與計畫一致 | — | 無 |
| #54 J54-2 rounded 的圓角仍在 | rounded 四角像素 = 頁面白 (255) ΔE 0；normal 四角 (224) ΔE 10.82（方角、線從角落 0px 起）→ **今天通過**（但今天是「整個都白」的空泛通過；修後才有鑑別力） | → 修後再驗 | 無 |
| #54 記錄、不判定 | rounded：陰影帶 1–6px 全白 ΔE 0；head 分隔線 head.b−2…+1 全白 ΔE 0。normal：陰影 d1 (248) 2.45、d2 (252) 1.04、d3 (254) 0.35、d4+ 0；head 分隔線在 head.b−1（y 101）(224) ΔE 10.82 | — | 無 |
| #54 保護項 (a)–(g) | — | (a) normal／none 的邊、陰影、尺寸數字見下表；(b) rounded 外框盒 280×250／280×200／220×116／280×60（collapsed）／280×120；(c) 標題 ink rounded (49,63.5) h15 vs normal (354,64.5)：normal 因 1px 邊框低 1px、右 1px；body 文字 rounded y171 vs normal y173（邊框 1 + head 61 vs 60）；icons expand 8×5、minimize 10×2、maximize 12×12、close 8×8；(d) collapsed rounded 高 60、無 body、今天也無框；(e) noheader rounded 今天無框；(f) **forced-colors 下 rounded 今天就有 1px 黑框、圓角畫得出來**（線從角落 3.5px 起，角落像素白）—來自 `tokens/_forced-colors.css:27-39` 的 `.z-panel { border:1px solid CanvasText }`，**`border="none"` 也被畫出黑框**（見「發現」）；(g) panel 沒有 move ghost（見 #55） | **說明**：(f) 的「forced-colors 下可見」今天就成立，修後只能當回歸、不是進步 |
| #55 J55-1 ghost 標題 ink 最深像素與閒置 ΔE ≤ 3 | 空白區拖曳：閒置標題最深 **(33,33,33)**、ghost 標題最深 **(143,143,143)** → ΔE **46.66**（= 白底 50% 合成的 (144,144,144)，ΔE 0.38）→ **失敗，RED-correct**。normal-border 視窗同：(127,127,127) ΔE 40.45 | — | **METHOD-DEFECT（量法前提）**：ghost 落在有內容的地方時，底下頁面的文字會透進 ink 框（content 拖曳量到 (33,33,33)、ΔE 0，是透出來的「With Title」），J55-1 必須在**空白區**量，定稿要寫明落點 |
| #55 J55-2 ghost 及子孫 computed opacity 皆 1 | `#zk_wndghost.z-window-move-ghost` **opacity 0.5**，子孫（header、icons、三個 button、三個 `<i>`、`<dl>`）皆 1 → **失敗，RED-correct** | — | 無 |
| #55 保護項 (a)–(f) | — | (a) outline `rgb(55,111,208) solid 2px`、offset −2px，像素 (154,182,231)（= 50% 合成，ΔE 0.37）；(b) ghost 300×180 = 視窗尺寸，`z-index 99999`、`position:absolute`、無 bg、無 shadow；(c) 放開後視窗落在 ghost 位置 **dx 0／dy 0**（jess −80/+120、−600/+440；normal −80/−200、−600/−280 四次皆 0）；(d) 閒置視窗見 `red55-*-idle.png`／`probe-window-page.png`（modal／highlighted／maximized 本次未操作，只有閒置基準）；(e) sizable 縮放未量；(f) forced-colors 下 outline 色 `rgba(5,0,73,.8)`，像素 (154,152,181) 可見，但 ghost 標題同樣被淡成 (126) | **說明**：(a) 的「outline 不變」基準是 **50% 合成後的 (154,182,231)**；若修法改成只淡化內容、ghost 本身 opacity 1，outline 會變成 (55,111,208)（ΔE ≫ 1），保護項 (a) 要改寫成「outline 可見、色 = focus ring 或其 50% 合成」 |
| #55 透出背景（只記錄） | content 拖曳（ghost 蓋在 "With Title" 兩個視窗上）：ghost **標題列**底下的文字 ink 平均 (69) → 拖曳中 (160)（白底 50% 合成預期 162）；ghost **標題以下**（空 `<dl>`，無 bg）底下文字 (76) → (76) **ΔE 0，100% 透出** | — | 計畫已預留 D56 |
| #55 panel 的 ghost | **panel 沒有 move ghost**：`Panel._initMove` 的 `zk.Draggable` 選項沒有 `ghosting`，拖的是 panel 節點本身（opacity 1、無 outline、位移 +80/+60），`.z-panel-move-ghost` 從未出現 → `panel.css:179-183` 是死規則 | — | **說明**：計畫「panel 的 ghost 是否同樣淡化」不適用 |
| #57 J57-1 N／S 文字左緣偏移 = W／E（±1px），涵蓋所有 N／S 有字的 borderlayout | BL0／BL1／BL2：N **0**、S **0.5**、W **16**、E **17**、C **16.5** → 差 16px → **失敗**；BL3／BL4／BL5：N 1、S 0.5、W 0、E 1、C 0.5 → **今天就相等（都沒 padding）**。整體失敗，RED-correct | — | **說明**：BL3–5 今天通過是因為 W／E／C 也裸文字；修法只能動 BL0–2 的 N／S（或 BL3–5 五區一起），否則 J57-1 在 BL3–5 反而失敗 |
| #57 J57-2 `.z-north-body`／`.z-south-body` computed padding 0 | 六個 borderlayout 的 N／S body padding **0px**（region、body 的 padding 鏈全 0；W／E／C 的 16px 來自預覽頁的 `div.z-p-4.z-div`，`elementsFromPoint` 第一個命中就是它）→ **今天通過** ✓ | — | 無 |
| #57 保護項 (a)–(d) | — | (a) BL0：North 60（header 40 + body **19**）、splitter 8px @ y 120–128；South 60、splitter @ 392–400；West／East 242 寬（body 241）、splitter 8px @ x 274–282／992–1000；Center body 282–992 × 128–392；(b) W／E／C 基準 ink 見上；(c) BL3 center autoscroll：scrollH 270 > clientH 180、捲軸 6px；(d) 只改 `borderlayout.zul` | **風險（發現 1）**：BL0／BL1 的 North／South body 只有 **19px** 高（size 15%／20% 扣掉 40px header），20px 的文字**今天就溢出、已有 6px 捲軸**（(224,224,224) @ x 1236–1242）；加 16px padding 會變 52px 內容塞 19px，捲軸更明顯、文字被切。保護項 (a)「內容改變不得撐高」在 BL0／BL1 不可能同時滿足「有 padding」與「不出捲軸」，修法要連 `size` 一起調或改用只加左右 padding——Planner 決定 |

## #53 — accordion 標題的字型與圖示

**Jess 的畫面：** `ref/jess-53-current.png` 是 "Text only (disabled + closable states)" 這個 accordion（`.z-tabbox[17]`，頁面上第一個 `z-tabbox-accordion`）的 Tab1（已選取＋展開）。`red53-acc-textOnly-tab1-zoom.png`、`red53-acc-textOnly-rest.png` 重現同一畫面。

| tabbox／tab | class | `.z-tab-text` computed（fs／fw／lh） | tab rect | 文字 ink（l,t,w,h） | ink 高 | 文字距 tab 左 | 最深像素 | 列底色 |
|---|---|---|---|---|---|---|---|---|
| 水平 "Tab States" Default | `z-tab` | **14px／500／20px** | 32,186 80.28×49 | 49,204 47×11.5 | 11.5 | 17 | (102,102,102) | 白 |
| 水平 Selected | `z-tab z-tab-selected` | 14px／500／20px | 112.28 90.59×49 | 129,204.5 57×11 | 11 | 16.72 | (55,111,208) | 白 |
| 水平 Disabled | `z-tab z-tab-disabled`，opacity 0.38 | 14px／500／20px | 202.88 90.19×49 | 220,204.5 56×10.5 | 10.5 | 17.12 | (218,218,218) | 白 |
| 水平 Default hover | | | | 同 rest | 11.5 | 17 | (31,32,33) | (239,244,251) ΔE 5.62 |
| 垂直 "Vertical left" Tab1／Tab2／Tab3 | `z-tabbox-left` | 14px／500／20px | 32,365 120×48 | 48.5,383.5 28.5×**11** | **11** | 16.5 | Tab1 (55,111,208)、其餘 (102,102,102) | 白 |
| accordion（text-only）Tab1 選取 | `z-tab z-tab-selected` | **16px／500／20px** | 34,533 589×48 | 50.5,551 33×**12.5** | **12.5** | 16.5 | (7,27,62) | (213,230,255) ΔE 16.78 |
| accordion Tab2／Tab3 | `z-tab` | 16px／500／20px | 34,638／687 589×48 | 50.5,656 37×12.5 | 12.5 | 16.5 | (33,33,33) | 白 |
| accordion Tab2 hover | | | | 同 | 12.5 | 16.5 | (31,31,31) | (237,237,237) ΔE 6.25 |
| accordion Tab4／Tab5 disabled | `z-tab z-tab-disabled`，opacity 0.38 | 16px／500／20px | 34,736／785 589×48 | 51,754.5 37×11.5 | 11.5 | 17 | (217,217,217) | 白 |
| accordion（with images）Tab1 選取 | 同上 + `.z-tab-image` 20×20 @ tab+16 | 16px／500／20px | 651,533 589×48 | 文字（排除圖）ink 高 12.5 | 12.5 | 圖 16、字在圖右 | (7,27,62) | (213,230,255) |

- **computed：** `.z-tab` 與 `.z-tab-text` 同值；accordion `.z-tab` padding `8px 16px`、min-height 48px；水平 `.z-tab` padding `0 0 2px`、min-height 48px（rect 49 = 48 + 2px 底部 indicator 區，text span 高 20）。
- **J53-1：** 字級字串 `16px` ≠ `14px`；同字 "Tab1" ink 高 12.5 vs 11（差 1.5 > 1）。**失敗，RED-correct。** 字重（500）與行高（20px）今天已相同——修法只需動 font-size。
- **chevron（`.z-tab-content::after`，8×8 盒、`border` 畫兩邊、`matrix(-0.707,-0.707,0.707,-0.707)`）：** ink **11×7** @ x 597.5–608.5（五列相同），距 tab 右緣 **14.5**；垂直中心：Tab1（選取、朝上）555 vs 列中心 557（**−2**）、Tab2（朝下）664 vs 662（**+2**）；最深：選取 (6,26,61)、未選取 (102,102,102)、hover (95,95,95)、disabled (196,196,196)。
- **combobox 基準（`combobox.zul` 第一個，`.z-combobox-icon.z-icon-caret-down` 盒 14×14）：** ink **8×5** @ x 297–305、y 200.5–205.5，右緣距控制項右緣 **12.08**，中心 y 203 = 控制項中心 203，最深 (102,102,102)。
- **判準數字：** 寬 (11−8)/8 = **+37.5%**，高 (7−5)/5 = **+40.0%**；兩邊都 > 25%。
- **列結構：** 五列 48 高、pitch 49；列間 1px (224,224,224) 分隔線在 y 637／686／735／784；tabbox 外框 (224) 在 532／833。展開 cave（`.z-tabpanel-content`）581–637 = 56px，13px／20.8px／400、padding 16px。點 Tab2 → Tab2 列 582–630（選取底色）、cave 630–686、Tab3 回到 687；點 disabled Tab4 無變化。

## #54 — `border="rounded"` panel 沒有外框

**Jess 的畫面：** `ref/jess-54-current.png` = "Rounded Border — Full Controls" 的 `Panel (rounded)`（`.z-panel[1]`）與 `Panel (normal)`（`[2]`）並排；`red54-normal-jess-pair.png` 重現。

| i | border | title／狀態 | root class | 外框盒（w×h） | head h | body h | 4 邊外緣 ΔE（左／右／上／下） | 角落 ΔE（tl tr bl br） | 陰影 d1–d3 ΔE | head 分隔線 |
|---|---|---|---|---|---|---|---|---|---|---|
| 0 | rounded | Panel（State Gallery） | `z-panel z-panel-noborder` | 220×116 | 56 | 60 | 0／0／0／0 | 0 0 0 0 | 0 0 0 | 無 |
| 1 | rounded | Panel (rounded)，toolbar×2 | `z-panel z-panel-noborder` | 280×250 | 60 | 190 | 0／0／0／**10.82**（底邊那條是 footer toolbar 的下框線 (224)，不是 panel 邊框） | 0 0 0 0 | 0 0 0 | 無（y 98–101 全白） |
| 2 | normal | Panel (normal) | `z-panel z-panel-noframe` | 280×250 | 61（含 1px 下框） | 187 | 10.82×4，色 (224,224,224) | 10.82×4（方角） | 2.45／1.04／0.35 | y 101 (224) ΔE 10.82 |
| 3 | none | Panel (no border) | `z-panel z-panel-noborder z-panel-noframe` | 280×250 | 60 | 190 | 0／0／0／10.82（同 1，toolbar 線） | 0 0 10.82 10.82（toolbar 線到角） | 0 | 無 |
| 4 | rounded | Panel (rounded) | `z-panel z-panel-noborder` | 280×200 | 60 | 140 | 0×4 | 0×4 | 0 | 無 |
| 5 | normal | Panel (normal) | `z-panel z-panel-noframe` | 280×200 | 61 | 137 | 10.82×4 | 10.82×4 | 2.45／1.04／0.35 | y 101 ΔE 10.82 |
| 6 | none | Panel (no border) | `… noborder noframe` | 280×200 | 60 | 140 | 0×4 | 0×4 | 0 | 無 |
| 7 | rounded | 無標題 | `z-panel z-panel-noborder z-panel-noheader` | 280×200 | — | 200 | 0×4 | 0×4 | 0 | — |
| 8 | normal | 無標題 | `z-panel z-panel-noheader z-panel-noframe` | 280×200 | — | 198 | 10.82×4 | 10.82×4 | 2.45／1.04／0.35 | — |
| 9 | none | 無標題 | `… noborder noheader noframe` | 280×200 | — | 200 | 0×4 | 0×4 | 0 | — |
| 10 | normal | Overflow Content | `z-panel z-panel-noframe` | 300×200 | 57 | 141 | 10.82×4 | 10.82×4 | 2.45／1.04／0.35 | y 446 ΔE 10.82 |
| 11 | rounded | Collapsed，`open=false` | `z-panel z-panel-noborder z-panel-collapsed` | 280×**60** | 60 | 0 | 0×4 | 0×4 | 0 | 無 |
| 12 | rounded | Expanded | `z-panel z-panel-noborder` | 280×120 | 60 | 60 | 0×4 | 0×4 | 0 | 無 |

- **computed（追溯用）：** rounded `border 0 / border-radius 6px / box-shadow none / head border-bottom 0`；normal `border 1px solid rgba(0,0,0,.12) / radius 0 / box-shadow rgba(50,50,93,.024) 0 2px 5px -1px, rgba(0,0,0,.05) 0 1px 3px -1px / head border-bottom 1px solid rgba(0,0,0,.12)`；none 同 rounded 但 radius 0。頁面底 `rgb(255,255,255)`。
- **J54-1：** 六個 rounded 4 邊 ΔE 0（要 ≥ 6）→ **失敗，RED-correct**。normal 邊框色 (224,224,224)（= `rgba(0,0,0,.12)` 合成白）是修後比對的目標（ΔE ≤ 3）。
- **J54-2：** rounded 角落像素白 = 頁面底 ✓；normal 角落 (224) 方角 ✓ → 今天通過。注意今天 rounded 整個外緣都白，「角落 = 底色」是空泛成立；修後要同時看「邊線中段 ΔE ≥ 6」與「角落 = 底色、線從角落約 3px 起」才有鑑別力（forced-colors 那組已示範：radius 6px 的 1px 線在 x 起點 3.5px 處出現）。
- **保護項尺寸基準：** rounded 與 normal 的外框盒同尺寸（280×250 等，`box-sizing:border-box`）；normal 的 head／body／toolbar 都內縮 1px（toolbar rounded `[32,100,280,49]` vs normal `[337,102,278,49]`）。標題 ink rounded (49,63.5) vs normal (354,64.5)；body 文字 rounded y171 vs normal y173。icons ink：expand 8×5、minimize 10×2、maximize 12×12、close 8×8（none 的 expand 量到 20×6.5 是 ink 框混到旁邊 hover 無關的像素，與本批無關）。
- **forced-colors（`red54-forced-*`）：** 所有 13 個 panel computed `border: 1px solid rgb(0,0,0)`（`_forced-colors.css:27-39` 的 `.z-panel { border: 1px solid CanvasText }` 蓋過 `.z-panel-noborder`），rounded 的四邊黑線 ΔE 100、角落白、線從角落 3.5px 起（圓角保留）；normal 與 **none** 都是方角黑框；陰影無。→ 保護項 (f)「forced-colors 下 rounded 外框可見」今天就成立。
- **Window（只查）：** `window.zul` 的 `border` 只有 none／normal；`z-window-noborder` 的 computed border 0、radius 4px，沒有 rounded 語意，與 #54 無關。

## #55 — window 拖曳 ghost 的透明度

**Jess 的畫面：** `ref/jess-55-frame35.png`（GIF 第 35 幀）：無框 Overlapped 視窗（`position="right, top"`，`.z-window[9]`，class `z-window z-window-noborder z-window-overlapped z-window-shadow`，969,2 300×180，header 64 高，標題文字 Range 985–1074 × 24–44）拖到 "With Title" 兩個視窗上：標題淡、標題以下只剩藍框、後面內容透出。`red55-jess-content-dragging-page.png`／`-ghost.png` 重現同一畫面。

**ghost DOM（拖曳中，`Window.ts:50-77`）：** `body` 最前面插入 `#zk_wndghost.z-window-move-ghost`，inline style `position:absolute; top/left; width:300px; height:180px; z-index:99999`，內容 = 複製的 `.z-window-header.z-window-header-move`（含 `.z-window-icons` 三個 button）+ 空 `<dl>`（高 = 視窗高 − 標題高）；原視窗 `visibility:hidden`、位置不動。

| 量法 | jess（無框，空白區 −80/+120） | jess（content，−600/+440，蓋在 With Title 上） | jess forced-colors（空白區） | normal 框（空白區 −80/−200） | normal（content −600/−280） |
|---|---|---|---|---|---|
| ghost rect | 889,122 300×180 | 369,442 300×180 | 889,122 300×180 | 889,516 300×180 | 369,436 300×180 |
| ghost computed opacity／子孫 | **0.5**／全部 1 | 0.5／1 | 0.5／1 | 0.5／1 | 0.5／1 |
| outline computed | `rgb(55,111,208) solid 2px`，offset −2px | 同 | `rgba(5,0,73,.8) solid 2px` | 同 jess | 同 |
| outline 像素（左邊 2px 帶） | (154,182,231)，= 50% 合成預期 (155,183,232) ΔE 0.37；vs 白 ΔE 38.22 | (155,183,231) | (154,152,181) | (154,182,231) | (155,183,231) |
| ghost bg／shadow／border | `rgba(0,0,0,0)`／none／0 | 同 | `rgba(255,255,255,0)` | 同 | 同 |
| 閒置標題 ink 最深 | (33,33,33)，ink 87.5×15.5 | 同 | (0,0,0) | (33,33,33) | 同 |
| ghost 標題 ink 最深 | **(143,143,143)**（= 白底 50% 的 (144)，ΔE 0.38） | (33,33,33)＝底下 "With Title" 透出，不是 ghost 的字 | (126,126,126) | (127,127,127) | (33) 同左 |
| ΔE 閒置 vs ghost 標題 | **46.66** | （0，不可用） | 52.8 | 40.45 | （0，不可用） |
| 透出：標題列底下的頁面 ink（n 像素）閒置 → 拖曳中 | n 2（空白） | n 1360：(69) → **(160)**，50% 合成預期 (162) | — | n 0 | n 1360：(69) → (140) |
| 透出：標題以下（`<dl>`）底下頁面 ink | n 0 | n 1312：(76) → **(76) ΔE 0（100% 透出）** | — | n 0 | n 1312：(76) → (76) ΔE 0 |
| 放開後視窗 rect vs ghost | dx 0／dy 0（移了 −80/+120） | 0／0 | 0／0 | 0／0 | 0／0 |

- **J55-1：** 空白區 ΔE 46.66 → **失敗，RED-correct**（normal 框 40.45 也失敗）。
- **J55-2：** ghost opacity 0.5 → **失敗，RED-correct**。
- **「內容變透明」的來源有兩層：** (1) 整個 ghost `opacity:.5`，連複製的標題列一起淡；(2) 標題以下是空 `<dl>`、ghost 沒有底色，後面 100% 透出。最小解讀只處理 (1)。
- **panel（`panel.zul` 沒有可拖曳 panel；對 `.z-panel[1]` 用 widget API `setFloatable(true)`＋`setMovable(true)` 後拖 head +80/+60）：** `w._drag` 的選項 `[handle, stackup, starteffect, ignoredrag, endeffect, …]` **沒有 `ghosting`**（`Panel.ts:1109-1114`）→ 拖曳中頁面上**沒有任何 `*move-ghost*` 元素**，panel 節點自己移動（拖曳中 rect 112,197、opacity 1、outline none；class 暫時少了 `z-panel-shadow`，放開後回來），放開後位移 +80/+60。`.z-panel-move-ghost`（`panel.css:179-183`）從未套用。

## #57 — North／South 內容缺少 padding

**Jess 的畫面：** `ref/jess-57-current.png` = 第一個 borderlayout（Basic）；`red57-jess-view.png` 重現（注意 North／South 右端今天就有 6px 捲軸，Jess 的圖裡也有）。

| borderlayout | 區域 | region class | body rect（l,t,w,h） | body padding | 文字 Range 距 body 左 | 文字 ink 距 body 左 | ink 距 body 上 | padding 來自（`elementsFromPoint` body+6,+6） |
|---|---|---|---|---|---|---|---|---|
| BL0 Basic（400 高） | north | `z-north` | 32,100 1210×**19** | 0 | 0 | **0** | 5 | `SPAN.z-label` → `.z-north-body` pad 0 |
| | south | `z-south` | 32,441 1210×19 | 0 | 0 | 0.5 | 5 | 同 |
| | west | `z-west` | 32,168 241×224 | 0 | 16 | **16** | 21 | **`DIV.z-p-4.z-div` pad 16px** → `.z-west-body` pad 0 |
| | east | `z-east` | 1001,168 241×224 | 0 | 16 | 17 | 21 | `DIV.z-p-4` |
| | center | `z-center` | 282,128 710×264 | 0 | 16 | 16.5 | 21 | `DIV.z-p-4` |
| BL1 Region Titles（300） | north／south | | 1210×19 | 0 | 0 | 0／0.5 | 5 | label |
| | west／east／center | | 241×140／241×140／726×140 | 0 | 16 | 16／17／16.5 | 21 | `DIV.z-p-4` |
| BL2 No Region Borders | north／south | `z-north z-north-noborder` | 1210×60 | 0 | 0 | 1／0.5 | 5 | label |
| | west／east／center | `…-noborder` | 242×180／242×180／726×180 | 0 | 16 | 16／17／16.5 | 21 | `DIV.z-p-4` |
| BL3 autoscroll | north／south | | 1210×59 | 0 | 0 | 1／0.5 | 5 | label |
| | west／east | | 241×180 | 0 | 0 | **0／1** | 5 | label（**沒有 z-p-4**） |
| | center（vlayout） | | 726×180，scrollH 270 | 0 | 0 | 0.5 | 5 | `.z-vlayout-inner` pad `0 0 5px` |
| BL4 center margins | north／south／west／east | | 59／59／180／180 | 0 | 0 | 1／0.5／0／1 | 5 | label |
| | center | | 284,249 706×160（margins 10） | 0 | 0 | 0.5 | 5 | label |
| BL5 cmargins | north／south／west／east／center | | 59／59／140／140／180 | 0 | 0 | 1／0.5／0／1／0.5 | 5 | label |

- **J57-1：** BL0／BL1／BL2 的 N／S 偏移 0–1 vs W／E 16–17 → 差 ≥ 15px → **失敗，RED-correct**。BL3／BL4／BL5 五區全是 0–1 → 今天就「一致」。
- **J57-2：** `.z-north-body`／`.z-south-body` computed padding **0px**（六個 BL 全部）→ **今天通過** ✓；W／E／C 的 16px 全部來自預覽頁的 `div.z-p-4.z-div`，theme 的 `.z-west-body`／`.z-center-body` 也是 0。
- **預覽站搜尋：** `/usr/bin/grep -rln "<north\|<south" zkpreview/src/main/webapp --include=*.zul` → **只有 `web/borderlayout.zul`**（12 處），沒有任何 north／south 內放 toolbar／menubar 的用法。「若改 theme 會弄壞貼邊 toolbar」的證據**不能從預覽站取得**，要另外引 ZK 文件或 demo 的慣例。
- **溢出（發現 1，`probe-ns-overflow.json`）：** BL0／BL1 的 North／South body `clientHeight 19 / scrollHeight 20`（size 15%×400 = 60、20%×300 = 60，扣 40px header 剩 19；文字 13px／20px）→ **今天就 `overflow:auto` 出 6px 垂直捲軸**（(224,224,224) @ x 1236–1242，`red57-jess-view.png` 右端）。BL2–5 的 N／S body 59–60 高、不溢出。若在 BL0／BL1 的 North／South 內包 `div.z-p-4`，內容變 52px 塞 19px：捲軸必在、文字上緣被切（只看得到 3px）。保護項 (a) 的「N／S 高度由 size 決定、內容不得撐高」與「加 padding」在 BL0／BL1 互斥——要嘛連 `size` 一起調（改變預覽頁版面、`borderlayout-gallery` baseline 會變更多）、要嘛 N／S 用 `z-px-4` 只加左右（左緣 16 對齊，J57-1 可過，但上下仍 0）。**由 Planner 決定，RED 不下結論。**

## 方法問題清單

1. **#55 J55-1 的量法前提：ghost 要落在空白區。** content 拖曳時 ink 框量到的最深像素是底下頁面透出的字（(33,33,33)、ΔE 0），J55-1 會「假通過」。定稿寫明：J55-1 用空白區落點（jess −80/+120、normal −80/−200，兩者 ΔE 46.66／40.45），透出程度另用 content 落點記錄。**METHOD-DEFECT（量法）。**
2. **#55 保護項 (a)「outline 色與寬度不變（ΔE ≤ 1）」的基準是 50% 合成色 (154,182,231)。** 若修法把 ghost 本身 opacity 改回 1（只淡化內容或不淡化），outline 會變 (55,111,208)（與今天 ΔE ≫ 1），這條會誤判退步。建議改為「outline 2px 可見、色 = `--zk-focus-ring` (55,111,208) 或其 50% 合成（二擇一，依修法寫死）」。**METHOD-DEFECT（容差／基準）。**
3. **#55 「panel 的 ghost 是否同樣淡化」不適用：** panel 從不建 move ghost（Draggable 無 `ghosting`），`.z-panel-move-ghost` 是死規則。**說明**（可列 follow-up：刪死規則或不動）。
4. **#54 J54-2 今天是空泛通過（整個外緣都白）。** 修後的圓角判定建議明寫：四角像素 = 頁面底（ΔE ≤ 1）**且**同一邊的中段 ΔE ≥ 6 **且**邊線在角落起算 2–4px 內才開始（今天 forced-colors 的 rounded 量到 3.5px，可當參考）。**說明**（補強，不是缺陷）。
5. **#54 保護項 (f) forced-colors 今天就通過**（`_forced-colors.css` 給所有 `.z-panel` 1px CanvasText 框，連 `border="none"` 也有）。修後只能當回歸基準；另外 none 在 forced-colors 下有框是本批外的觀察（見發現 3）。**說明。**
6. **#57 保護項 (a) 與 BL0／BL1 的 19px N／S body 互斥**（發現 1）。方法要先決定 N／S 的 padding 形式（`z-p-4` / `z-px-4` / 同時調 `size`），否則最終 run 的 (a) 一定失敗或 J57-1 一定失敗。**METHOD-DEFECT（保護項與修法衝突）。**
7. **#57 J57-1 在 BL3–5 今天就通過**（五區皆裸文字）；修法若也給 BL3–5 的 N／S 加 padding 會把這三個弄成失敗。定稿要寫「只有 W／E 有 `z-p-4` 的 borderlayout（BL0–2）加 N／S padding」或「BL3–5 五區一起」。**說明。**
8. **#57 「其他頁面貼邊 toolbar」的證據不存在於預覽站**（只有 `borderlayout.zul` 用 north／south）。留言裡的「會弄壞 toolbar」要改引文件或 demo，或改寫成原則性說明。**說明。**
9. **#53 的 ink 高交叉驗證要用同字**：水平 "Tab States" 的 Default／Selected／Disabled glyph 不同（11.5／11／10.5），與 accordion 的 Tab1 不可直接比；用垂直 mold（14px）的 Tab1–Tab3（11）對 accordion Tab1–Tab3（12.5）才是同字比對。定稿寫明比對對象。**說明。**
10. **#53 圖示：** 寬 +37.5%、高 +40%，兩邊都超過 25% 的事先判準 → 依計畫觸發 D56（改圖示要先問）。另記：chevron 垂直中心朝上時 −2px、朝下 +2px（旋轉方塊的幾何偏移），若改用遮罩 chevron，J53-2 的「垂直中心差 ≤ 1px」今天也不成立，列入議題頁。**說明。**

## 發現（方法沒寫的）

1. **BL0／BL1 的 North／South 今天就溢出並顯示 6px 捲軸**（body 19px 裝 20px 文字），Jess 的截圖裡也看得到；這是 demo 的 `size` 太小，與 padding 無關，但會被 #57 的修法放大。
2. **panel 沒有 move ghost**，`panel.css:179-183` `.z-panel-move-ghost` 從未套用（Panel 的 Draggable 沒有 `ghosting`，拖的是節點本身）。
3. **forced-colors 下 `border="none"` 的 panel 也畫出 1px 黑框**（`_forced-colors.css:27-39` 對 `.z-panel` 一律給 `border:1px solid CanvasText`，沒有排除 `z-panel-noborder`）；normal／rounded／none 在高對比下看起來一樣有框。不在本批範圍，記錄給 Planner。
4. **accordion 的 chevron 垂直中心隨方向偏 ±2px**（選取朝上 555 vs 557，未選取朝下 664 vs 662）；水平 mold 沒有 chevron 可比。
5. **window ghost 的 outline 今天也是 50%**（(154,182,231)，不是 focus ring 原色），因為 outline 畫在 opacity .5 的元素上；修 #55 時 outline 會跟著變深，baseline／保護項要預期。
6. **rounded panel 的 footer toolbar 下框線**（`toolbar mold="panel"` 的 1px (224) 線，比 panel 窄：rounded 版從 x 57 到 601，normal 版滿寬）在 rounded 底邊量成 ΔE 10.82；最終 run 量 rounded 的「底邊外框」要避開有 footer toolbar 的 panel 1，或改量底邊兩端 8px。

方法缺陷：第 1 項（#55 J55-1 落點）、第 2 項（#55 outline 基準）、第 6 項（#57 保護項 (a) 與 19px body 互斥）。四個判定 J53-1、J54-1、J55-1（空白區）、J55-2、J57-1 今天都失敗，J54-2、J57-2 今天通過，RED 成立。

RED11: METHOD-DEFECTS
