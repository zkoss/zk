使用 8085（A 線，批次 11 最終判定）

# Gate：第十一批最終判定（GATE11-FINAL，R2）— accordion 標題字型、rounded panel 外框、window 拖曳 ghost、borderlayout N／S padding（#53 #54 #55 #57）

> **R4（2026-10-08 23:52–23:58，J53-2 右緣距修正：chevron 盒 margin-right −4px → −7px）：** 結論見文末 `GATE11-FINAL-R4`。pid **46159**（23:52:36），`zul-…jar` 23:52:31、`zk-…jar` 23:52:02，開跑前後 pid／jar／伺服 md5／四個 .zul 200 一致（`served-md5-r4-start.txt`／`-end.txt`）。build id `80d82a18`：`tabbox.css.dsp` **`7aa5b400…`**（10,913 bytes）= codegen = build（R3 `509a13f5…` → 換了；`tabbox.css` src 23:51:47 = build），panel／window／borderlayout 的 `.css.dsp` 與 R3 相同。頁面有渲染（`.z-tabbox` 19、accordion 2）。只重量 #53（`final53-r4.js` = `final53-r3.js` 只改檔名 → `final53-r4.json/.log/.png`）、只重跑 `gallery`／`chromium` 的 tabbox 測試與 `forced-colors` 的 tabbox 項（`pw-regression-r4.log`、`pw-visual-r4/`），其餘元件沿用 R3（檔案未改）。`shots/53-after.png`、`53-after-collapsed.png` 已換成 R4（R3 版另存 `*-r3.png`）。
>
> **R3（2026-10-08 21:53–22:05，D56-A chevron 遮罩 + D57-B ghost 底色）：** 結論見文末 `GATE11-FINAL-R3`。第一次嘗試（pid 98236）因伺服器啟動後 jar 被重建而 500，停下回報（記錄保留在下一段）；Planner 重啟後 **pid 12560（21:53:05）**，`zul-11.0.0-SNAPSHOT.jar` 21:53:02、`zk-…jar` 21:52:59，開跑前與跑完後都沒再變動（`served-md5-r3-start.txt`／`served-md5-r3-end.txt`：pid、jar mtime、四個 `.css.dsp` md5、`zk.wcs` 200／541,812、四個 .zul 皆 200 完全相同；`zul/src|build|codegen`、`zkpreview/src` 沒有比 21:53:07 新的 css/dsp/zul/jar）。伺服 build id `80d82a1a`：`tabbox.css.dsp` **`509a13f5…`**（10,913 bytes）、`window.css.dsp` **`2b41406c…`**（3,321）= codegen = build（R2 是 `44f81af5…`／`9bd79cb1…`，兩個都換了）；`panel.css.dsp` `f28068d8…`、`borderlayout.css.dsp` `1e907c61…` 不變。頁面確認有渲染：`.z-tabbox` 19、`.z-window` 12、`.z-panel` 13、`.z-borderlayout` 6。R3 腳本：`final53-r3.js`（+ forced-colors accordion、token 參考色、collapsed 列 zoom）、`final55-r3.js`（+ `--zk-color-surface` 參考）、`final54-r3.js`／`final57-r3.js`（同 R1，只改檔名）→ `final5x-r3.json/.log/.png`；回歸 `pw-regression-r3.log`、`pw-core-r3/`、`pw-visual-r3/`；gallery 歸因 `diff-gallery-r3.js/.json`；截圖 `shots/53-after.png`（R3，含新 chevron；R1 版 `53-after-r1.png`）、`shots/53-after-collapsed.png`（Tab2 收合列 zoom）、`shots/55-after.png`（R3，蓋在內容上；R1 版 `55-after-r1.png`），54／57 沿用。
>
> **R3 第一次嘗試（21:49，pid 98236）— 停止，環境不可用：** D56-A／D57-B build 後 8085 由 pid 98236（21:48:52）伺服，build id 換成 `80d82a1a`；伺服的 `tabbox.css.dsp` `509a13f5…`（10,913 bytes）、`window.css.dsp` `2b41406c…`（3,321）= codegen = build（R2 是 `44f81af5…`／`9bd79cb1…` → 兩個都換了），`panel.css.dsp` `f28068d8…`、`borderlayout.css.dsp` `1e907c61…` 不變，`zk.wcs` 200／541,812（`served-md5-r3-start.txt`）。**但伺服器啟動後又有一次 gradle 產出**：`zul/build/libs/zul-11.0.0-SNAPSHOT.jar` 21:49:33、`zul/build/classes/java` 502 個檔、`zul/codegen` 2,019 個檔 21:49:43（抽樣 md5 與 build 相同）、`zkpreview/build/classes` 也重編。結果 **`window.zul`／`panel.zul`／`borderlayout.zul` 回 HTTP 500 `java.lang.NoClassDefFoundError: org/zkoss/zk/ui/UiException$Aide`（`ClassNotFoundException`，`DHtmlLayoutServlet.doGet` → `ZKPreviewServlet`）**，`tabbox.zul` 回 200 但頁面上沒有任何 `.z-tabbox`（client 端沒有渲染）；`final53-r3.js`／`final57-r3.js` 在第一個 `page.evaluate` 就因此失敗（`final53-r3.log`／`final57-r3.log`）。這是執行中的 JVM 底下 jar 被重建覆寫的典型症狀，不是新 build 沒上線。依指示**停下不重啟、不 build**；R3 的四個腳本（`final53-r3.js` 含 forced-colors accordion、token 色參考、collapsed 列 zoom；`final55-r3.js` 含 `--zk-color-surface` 參考；`final54-r3.js`／`final57-r3.js` 只改檔名）已備妥，環境恢復後可直接重跑。R3 沒有量到任何數字，R2 結論不變。
>
> **R2（2026-10-08 21:00）：** R1 結論 FAIL 後 Planner 兩項處置：(1) `borderlayout.zul` BL1 的 N／S size 35% → 32%（預覽頁原始檔 20:59:46，8085 直接讀 .zul，不需 build／重啟；live 頁面 `zk.Widget.$(north).getSize()` 回 `32%`，R1 是 `35%` → 新版已生效）；(2) #54 保護項 (b) 口徑改為「固定高度 ±0；auto 高度 +2px」。R2 只重跑 #57（`final57-r2.js/.json/.log`、`final57-r2-*.png`）、`gallery › borderlayout`（`pw-regression-r2.log`，無 `--update-snapshots`）與像素量法 `diff-gallery-r2.js/.json`；#53／#54／#55 的量測不重跑（CSS 沒換，伺服 md5 = `served-md5-end.txt`）。R1 的數字保留在下文，R2 改動處以 **R2** 標示；`shots/57-after.png` 已換成 R2（R1 版另存 `shots/57-after-r1.png`）。

- **伺服器：** 8085，java pid 59976（20:37:45 啟動，`zkpreview/build/gretty_ports.properties` 20:37:46）。**沒有 kill、沒有重啟、沒有碰 8105。**
- **新 build 確認（修正已上線）：** 伺服的 `/zkres/web/80d82a15/js/zul/tab/css/tabbox.css.dsp` md5 `44f81af5…`（10,621 bytes）、`wnd/css/panel.css.dsp` `f28068d8…`（3,016）、`wnd/css/window.css.dsp` `9bd79cb1…`（3,280）、`layout/css/borderlayout.css.dsp` `1e907c61…`（6,729）各與 `zul/codegen/resources/web/...` 及 `zul/build/resources/main/web/...` 的同名檔 md5 相同（`served-md5-start.txt`）。RED 時期伺服的是 tabbox `1ef89c40…`、panel `f9bef75a…`、window `6229a09d…` → **三個都換了**；borderlayout `1e907c61…` **沒變**（J57-2 要的就是這個）。`zul/src` 三個 `.css` 20:36:10、`zkpreview/src/main/webapp/web/borderlayout.zul` 20:36:47、build 的 `.css.dsp` 20:37:33，都早於伺服器啟動；`zul/src|build|codegen`、`zkcml/zkmax|zkex/src`、`zkpreview/src` 底下沒有任何 `.css/.css.dsp/.zul/.xml/.svg` 比 `gretty_ports.properties` 新；`zk.wcs` 200／541,441 bytes。像素也證實：accordion 標題 computed 16px → 14px、rounded panel 四邊出現 (224,224,224) 線、ghost opacity 0.5 → 1、BL0 North body 19 → 59px。
- **跑完再確認：** 四個 `.css.dsp` 伺服 md5 在量測前與回歸跑完後相同（`served-md5-start.txt` = `served-md5-end.txt`），pid 不變 → 期間沒有別的 build 上線。
- **日期：** 2026-10-08，Fable Verifier。Playwright 1.59.1、Chromium 147.0.7727.15、viewport 1280×900、DPR 2、`ignoreDefaultArgs:['--hide-scrollbars']`、注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`、游標停在 (2,2)、每個狀態在重新載入的頁面上量；forced-colors 用 `newContext({forcedColors:'active'})`。ΔE 一律 CIE76（lib.js）；位置、尺寸用像素 ink（與底色 ΔE ≥ 8 的像素外接矩形），computed style 只讀計畫允許的值（字級、字重、行高、opacity、`.z-north-body` padding、ghost outline／z-index），ink 數字 0.5px 粒度。
- **方法：** lines/line-a-plan.md（過程資料已清除） 第七節「第 11 批」，以「第 11 批 RED run 結果與方法定稿」九條為準：J55-1 空白區落點；#55 保護項 (a) 改為「outline 2px 可見、色 = focus ring (55,111,208) 或其 50% 合成」；#55 panel 項刪除；J53-1 只比 font-size、ink 用垂直 mold Tab1–3 對 accordion Tab1–3；J54-2 加強（中段 ΔE ≥ 6、線從角落 2–4px 起、底邊量兩端各 8px）；J57-1 只要求 BL0–2、BL3–5 守住、加 J57-3（`scrollHeight ≤ clientHeight`）、保護項 (a) 改為「除 BL0／BL1 的 N/S size 外其餘不變」；#53 圖示待 D56，只記 chevron 數字。
- **腳本與原始輸出：** `gates/batch11-final/`：`lib.js`、`m11.js`（複製自 batch11-red）、`final53.js`／`final54.js`／`final55.js`／`final57.js`（複製自 batch11-red 的 `red53/54/55/57.js`，只改輸出檔名；`final54.js` 依定稿第 5 條加量每邊中段 ΔE、每個角落的線起點、底邊兩端各 8px；`final55.js` 依定稿第 3 條拿掉 panel 段；`final57.js` 依定稿第 6 條加記 `scrollHeight/clientHeight` 與 region `size`）→ `final5x.json/.log` 與同名 `.png`（檔名規則同 RED：`final53-acc-{textOnly,withImages}-{rest,tab1-zoom,hover-tab2,tab2-selected}.png`、`final54-{normal,forced}-p0…p12.png`／`-jess-pair.png`／`-page.png`、`final55-{jess,normal}[-forced]-{blank,content}-{idle,idle-page,dragging-page,ghost,dropped}.png`、`final57-bl0…bl5.png`／`-jess-view.png`）。回歸 `pw-regression.log`、`pw-core/`、`pw-visual/`（含 `screenshot-{tabbox,panel}-gallery-chromium/` 的 expected／actual／diff、`borderlayout-gallery-actual.png`、`forced-colors-gallery-after/`）；gallery 差異歸因 `diff-gallery.js/.json/.log`、`probe-bl-top.js/.json` + `probe-borderlayout-page.png`；`served-md5-start.txt`／`served-md5-end.txt`。Jess 留言用截圖 `shots/{53,54,55,57}-after.png`，修前對照 `shots/{53,54,55,57}-before.png`（從 batch11-red 的 `red53-acc-textOnly-rest.png`、`red54-normal-jess-pair.png`、`red55-jess-content-dragging-page.png`、`red57-jess-view.png` 複製）。
- **沒有看 diff、沒有讀 CSS 修改內容、沒有讀 gen 報告**，沒有修改任何 theme／CSS／TS／Java／預覽頁／baseline 檔案，沒有 `--update-snapshots`，Playwright 只用 `-g 'tabbox|panel|window|borderlayout'` 過濾 visual 專案，沒有 commit。

---

## 結論摘要

| 判定 | 門檻 | 修前（RED） | 修後（今天） | 結果 |
|---|---|---|---|---|
| J53-1 accordion 標題 font-size = 水平 tab；同字 "Tab1–3" ink 高差 ≤ 1 | 相等；≤ 1px | 16px vs 14px；ink 12.5 vs 11（差 1.5） | **14px = 14px**（三態＋disabled 皆 14px／500／20px）；accordion Tab1／2／3 ink 高 **11／11／11** = 垂直 mold 11／11／11（差 **0**） | **PASS** |
| #53 chevron（只記錄，D56 待裁示） | — | ink 11×7、距 tab 右 14.5、中心朝上 −2／朝下 +2 | **11×7、14.5、−2／+2（逐值相同）**；combobox 基準 8×5、12.08 不變 | 記錄 |
| J54-1 六個 rounded 的 4 邊外緣線 ΔE ≥ 6 且與 normal 邊框色 ΔE ≤ 3 | ≥ 6；≤ 3 | 4 邊 ΔE 0（白） | 六個（p0、p1、p4、p7 noheader、p11 collapsed、p12）4 邊皆 **(224,224,224) ΔE 10.82**；normal 也是 (224,224,224) → 色差 **0** | **PASS** |
| J54-2（加強）四角 = 頁底、邊線中段 ΔE ≥ 6、線從角落 2–4px 起；底邊只量兩端 8px | 角 ΔE ≤ 1；中段 ≥ 6；2–4px | 角 0；中段 0（空泛） | 六個 rounded 四角 ΔE **0**；四邊中段 ΔE **10.82**；線起點 top 從左 **4**／從右 **3**、bottom 從左 **3**／從右 **3.5**、left／right 從上 **2.5**；底邊兩端 (224) ΔE 10.82（p1 的 footer toolbar 線已避開）；normal 角 10.82、起點 0（方角）不變 | **PASS** |
| #54 記錄、不判定 | — | rounded 陰影 0、head 分隔線 0 | rounded 陰影 d1–d6 **全 0**、head 分隔線 head.b−2…+1 **全 0**（沒有越界）；normal 陰影 2.45／1.04／0.35、分隔線 10.82 不變 | 記錄 |
| J55-1 空白區拖曳：ghost 標題 ink 最深 vs 閒置 ΔE ≤ 3 | ≤ 3 | jess 46.66、normal 40.45 | jess **(33,33,33) vs (33,33,33) ΔE 0**；normal **0**；forced-colors jess／normal **(0,0,0) ΔE 0** | **PASS** |
| J55-2 ghost 及所有子孫 computed opacity = 1 | 全 1 | ghost 0.5 | ghost **1**，子孫（header、icons、三個 button、三個 `<i>`、`<dl>`）全 1，`nonOne` 空（六組都是） | **PASS** |
| J57-1 BL0–BL2 的 N／S 文字 ink 左緣偏移 = W／E（±1）；BL3–5 仍一致 | ±1px | BL0–2 N 0–1／S 0.5 vs W 16／E 17 | BL0 N **17**／S **16.5** vs W 16／E 17／C 16.5；BL1 **17／16.5**；BL2 **17／16.5** → 差 ≤ 1；BL3／4／5 五區仍 0–1（逐值 = RED） | **PASS** |
| J57-2 `.z-north-body`／`.z-south-body` computed padding 0；`borderlayout.css` 未改 | 0；md5 不變 | 0 | 六個 BL 的 N／S body padding **0px**，padding 全來自預覽頁 `DIV.z-p-4`（`elementsFromPoint` 第一個命中）；`borderlayout.css.dsp` 伺服 md5 `1e907c61…` = RED | **PASS** |
| J57-3 N／S 內容無捲軸、文字不被切（`scrollHeight ≤ clientHeight`） | fits | BL0／BL1 N／S 19 裝 20（溢出） | BL0 N／S body **59／59** 裝 59 ✓（size 25%）；BL1 **64／64** 裝 64 ✓（35%）；BL2 60 裝 60 ✓；BL3–5 59 裝 59 ✓ | **PASS** |

**保護項：** #53 全過（一個 0.5px 的 glyph 側邊距備註）；#54 (a)(c)(d)(e)(f)(g) 過、(b) R1 依「±0px」不過（三個 auto 高度的 rounded panel 外框盒高了 2px），**R2 依新口徑「固定高度 ±0；auto 高度 +2px」→ 過**（p1／p4／p7 ±0；p0／p11／p12 各 +2，寬 ±0）；#55 全過；#57 (c)(d) 過、(b) R1 **BL1 的 West／East／Center 溢出、出現 6px 捲軸**（預期外的像素變動）→ **R2（BL1 N/S 32%）W／E／C body 68px 裝 52px，`scrollHeight 68 = clientHeight 68`、`clientWidth 241` = body 寬，捲軸消失 → 過**。回歸見「回歸」節。

**R2 的 #57 數字（BL1，其餘 borderlayout 與 R1 逐值相同）：** north／south size **32%**、region 96 高、body **55**（R1 64）裝 55 ✓、文字 ink 距 body 左 17／16.5、距上 21；west／east／center region 108 高（t 156）、body **68**（R1 50）、scroll 68／68、clientWidth 241／241／726（R1 235／235／720）→ **無捲軸**（`final57-r2-bl1.png`）；W／E／C 文字 ink 16／17／16.5 不變。BL0 與 R1 逐值相同（N/S 25%、body 59、W/E/C 144／184 裝得下）；BL2 `final57-r2-bl2.png` md5 = R1；BL3／4／5 的 region、head、splitter、ink 與 **RED 逐值相同**，`final57-r2-bl3/4/5.png` md5 = RED。J57-1／J57-2／J57-3 在 R2 全部仍 PASS（BL1 N 17／S 16.5 vs 16／17／16.5；N/S body padding 0；N/S 55 ≥ 52）。

## #53 — accordion 標題字型（修後數字）

| tab | class | `.z-tab-text` computed（fs／fw／lh） | 列高（rect h） | 文字 ink 高 | 文字 ink 距 tab 左 | DOM text 左 | chevron ink（w×h）／距 tab 右／中心−列中心 | 最深像素 | 列底色 |
|---|---|---|---|---|---|---|---|---|---|
| 水平 "Tab States" Default／Selected／Disabled | | 14px／500／20px（= RED） | 49 | 11.5／11／10.5（= RED） | 17／16.72／17.12 | — | — | (102,102,102)／(55,111,208)／(218,218,218) | 白；hover (239,244,251) |
| 垂直 "Vertical left" Tab1／Tab2／Tab3 | | 14px／500／20px | 48 | **11／11／11** | 16.5 | 48 | — | Tab1 (55,111,208)、其餘 (102,102,102) | 白 |
| accordion（text-only）Tab1 選取 | `z-tab z-tab-selected` | **14px／500／20px**（RED 16px） | **48**（= RED） | **11**（RED 12.5） | **16.5**（= RED） | 50（= RED） | **11×7／14.5／−2**（= RED） | (7,27,62)（= RED） | (213,230,255) ΔE 16.78（= RED） |
| accordion Tab2／Tab3 | `z-tab` | 14px／500／20px | 48 | **11／11** | 16.5 | 50 | 11×7／14.5／+2 | (33,33,33) | 白 |
| accordion Tab2 hover | | | | 11 | 16.5 | | | (31,31,31) | (237,237,237) ΔE 6.25（= RED） |
| accordion Tab4／Tab5 disabled（opacity 0.38） | `z-tab z-tab-disabled` | 14px／500／20px | 48 | 10.5／10 | **16.5**（RED 17） | 50（= RED） | 11×7／14.5／+2 | (217,217,217) | 白 |
| accordion（with images）Tab1–5 | 同上 + `.z-tab-image` | 14px／500／20px | 48 | 11／11／11／10.5／10 | 40.5（圖右；RED 40.5、disabled 41 → 40.5） | 667 | 同 | 同 | 同 |

- **J53-1：** font-size 字串 `14px` = `14px`；同字 ink 高 11 = 11（差 0 ≤ 1）。字重 500、行高 20px 仍相同。→ **PASS**。
- **保護項 (a) 列高：** 五列 rect h 48、tab top 533／638／687／736／785（pitch 49）、列間 1px (224,224,224) 分隔線 y 637／686／735／784、tabbox 外框 532／833、展開 cave 581–637（56px）— **與 RED 逐值相同，差 0px**（`min-height` 吸收了字級改變，不需用到 ≤ 2px 的容許）。
- **(b) 位置：** 文字 DOM 左 50（with images 667）不變；Tab1–3 ink 左 16.5 不變；**Tab4／Tab5（disabled）ink 左 17 → 16.5**（−0.5px）— DOM 位置沒動，是較小字級的 glyph 側邊距（"T" 的左側空白）在 DPR 2 下少半個像素，屬於「字寬變小」一類，**記錄不算失敗**，交 Planner 確認。chevron ink 右緣距 tab 右 14.5 不變、中心 −2／+2 不變。
- **(c) 色彩：** 選取底 (213,230,255)、文字 (7,27,62)；未選取白底、(33,33,33)；hover (237,237,237)；disabled (217,217,217)、chevron (196,196,196)— 全部 = RED。
- **(d) cave：** `.z-tabpanel-content` 13px／20.8px／400、padding 16px、rect 581–637 — 不變。
- **(e) 水平、垂直 mold 像素比對：** `final53-horizontal0.png` md5 `efc1008c…` = RED、`final53-horizontal0-hover.png` `6975ebaf…` = RED、`final53-vertical3.png` `4245553f…` = RED → **逐像素相同**。
- **(f) 動畫：** 點 Tab2 後 Tab2 列 582–630、cave 582 起 105 高（含列）、Tab3 回 687、Tab1 收合 — 與 RED 相同；點 disabled Tab4 無變化。
- **chevron（D56 用）：** 五列 ink 11×7、x 距 tab 右 14.5、垂直中心選取（朝上）555 vs 列中心 557、未選取（朝下）664 vs 662；combobox 基準 8×5、右緣距 12.08。**本輪沒改、數字沒動。**

## #54 — `border="rounded"` panel 外框（修後數字）

| i | border | title／狀態 | root class | 外框盒 RED → 今天 | 4 邊 ΔE RED → 今天 | 中段 ΔE | 角落 ΔE | 線起點（top 左／右、bottom 左／右、left 上／right 上） | 陰影 d1–d3 | head 分隔線 |
|---|---|---|---|---|---|---|---|---|---|---|
| 0 | rounded | Panel（State Gallery，auto 高） | `z-panel z-panel-noborder` | 220×116 → **220×118** | 0 → **10.82×4** | 10.82×4 | 0 0 0 0 | 4／3、3／3.5、2.5／2.5 | 0 | 0 |
| 1 | rounded | Panel (rounded)，toolbar×2 | 同 | 280×250 → 280×250 | 0／0／0／10.82(toolbar 線) → 10.82×4 | 10.82×4 | 0×4 | 同上 | 0 | 0（y 99–102） |
| 2 | normal | Panel (normal) | `z-panel z-panel-noframe` | 280×250 不變 | 10.82×4 不變 | 10.82×4 | 10.82×4 | 0×6（方角） | 2.45／1.04／0.35 不變 | y101 10.82 不變 |
| 3 | none | Panel (no border) | `… noborder noframe` | 280×250 不變 | 0／0／0／10.82 不變 | 同 | 0 0 10.82 10.82 不變 | — | 0 | 0 |
| 4 | rounded | Panel (rounded) | noborder | 280×200 不變 | 0 → 10.82×4 | 10.82×4 | 0×4 | 同 p0 | 0 | 0 |
| 5 | normal | Panel (normal) | noframe | 280×200 不變 | 10.82×4 不變 | | 10.82×4 | 0 | 2.45／1.04／0.35 | 10.82 |
| 6 | none | Panel (no border) | noborder noframe | 280×200 不變 | 0×4 不變 | | 0×4 | — | 0 | 0 |
| 7 | rounded | 無標題 | noborder noheader | 280×200 不變 | 0 → 10.82×4 | 10.82×4 | 0×4 | 同 p0 | 0 | — |
| 8 | normal | 無標題 | noheader noframe | 280×200 不變 | 10.82×4 不變 | | 10.82×4 | 0 | 2.45／1.04／0.35 | — |
| 9 | none | 無標題 | noborder noheader noframe | 280×200 不變 | 0×4 不變 | | 0×4 | — | 0 | — |
| 10 | normal | Overflow Content | noframe | 300×200 不變 | 10.82×4 不變 | | 10.82×4 | 0 | 2.45／1.04／0.35 | 10.82 |
| 11 | rounded | Collapsed（auto 高） | noborder collapsed | 280×60 → **280×62** | 0 → 10.82×4 | 10.82×4 | 0×4 | 同 p0 | 0 | （y771 10.82 = 盒底邊線本身） |
| 12 | rounded | Expanded（auto 高） | noborder | 280×120 → **280×122** | 0 → 10.82×4 | 10.82×4 | 0×4 | 同 p0 | 0 | 0 |

- **computed（追溯用）：** rounded `border 1px solid rgba(0,0,0,.12)`、`border-radius 6px`、`box-shadow none`、head `border-bottom 0`；normal／none 的 computed 與 RED 相同。root class 六個 rounded 仍 `z-panel-noborder`、無 `noframe`（修法沒動 class）。
- **J54-1：** 六個 rounded 4 邊 (224,224,224) ΔE 10.82 ≥ 6；normal 邊框 (224,224,224) → 色差 0 ≤ 3。**PASS。**
- **J54-2（加強）：** 四角像素 (255,255,255) = 頁底；中段 ΔE 10.82；線從角落 2.5–4px 起（radius 6px；forced-colors 下 RED 量到 3.5，一致）；normal 方角 10.82、起點 0。**PASS。**
- **記錄（R6／R9，不修）：** rounded 陰影帶 d1–d6 全 (255) ΔE 0、head 分隔線全 0；**修法沒有越界**。
- **保護項：**
  - (a) normal p2／p5／p8／p10 與 none p3／p6／p9：邊、角、陰影、分隔線、外框盒、head／body 高、標題／內容／icon ink 相對 panel 盒的位置 **逐值 = RED**（p8–p10 的 viewport 座標整體上移 2px，是 p0 高了 2px 把頁尾的捲動極限推移，相對位置不變）。
  - **(b) rounded 外框盒：** 固定高度的 p1（280×250）、p4、p7（280×200）**±0** ✓；**auto 高度的 p0 116 → 118、p11（collapsed）60 → 62、p12 120 → 122，各高 2px** — 1px 上下邊框加在沒有固定 `height` 的盒子上，`box-sizing:border-box` 吸收不了。寬度不變（有固定 width）。計畫寫的是「±0px」，**嚴格讀是不過**；是否接受（normal panel 的 auto 高本來就含邊框，rounded 補框後與 normal 同高）由 Planner 裁示。內容區內縮：p1 body 190 → 188、p4 140 → 138、p7 200 → 198、head 60 不變，屬計畫寫明的預期。
  - (c) 標題／內容文字 ink 相對 panel 盒 +1／+1（p0、p1、p4、p7、p11、p12 皆 (17,23.5) → (18,24.5) 之類）；minimize／maximize／close icon ink 0／+1；collapsed 的 expand icon −1／+1 → **全部 ≤ 1px** ✓。p1／p4 的 expand icon ink 框變成 20.5×6.5 @ 137.5 是量法框（icon rect −2px）吃到標題最後一個字母的像素（標題 ink 右緣 169 → 170），與 RED 對 none p3 的同一現象相同，不是 icon 變了（minimize 等 x 不變）。
  - (d) collapsed p11 仍收合（body 0 高）、有框 ✓；(e) noheader p7 有框 ✓。
  - (f) forced-colors：13 個 panel computed `border 1px solid rgb(0,0,0)`，rounded 四邊 ΔE 100、角白、線從 2.5–3.5px 起；**forced-colors 下所有數字 = RED（含 p0 118、p11 62、p12 122 — RED 在 forced-colors 下就已經有 1px 框，所以高度本來就是 118／62／122，與今天一般模式的數字相同，間接證明 (b) 的 2px 就是邊框）**。`border="none"` 仍被畫黑框（RED 發現 3，本批外）。
  - (g) panel 沒有 move ghost（RED 發現 2）；`panel.zul` 沒有 draggable panel，本輪沒有拖曳 panel，「拖曳外觀不變」無從量、也沒有變動的來源（本批 panel.css 只為 #54 動）。
- **Window（只查）：** `window.zul` 的 `border` 模式 computed 與 RED 逐值相同（none／normal、radius 4px）。

## #55 — window 拖曳 ghost（修後數字）

| 量法 | jess（無框，空白區 −80/+120） | jess（content −600/+440，蓋在 With Title 上） | jess forced-colors（空白區） | normal 框（空白區 −80/−200） | normal（content −600/−280） | normal forced-colors |
|---|---|---|---|---|---|---|
| ghost rect | 889,122 300×180（= RED） | 369,442 300×180（= RED） | 889,122（= RED） | 889,516（= RED） | 369,436（= RED） | 889,516 |
| ghost computed opacity／子孫 | **1**／全 1（RED 0.5／1） | 1／1 | 1／1 | 1／1 | 1／1 | 1／1 |
| outline computed | `rgb(55,111,208) solid 2px`、offset −2px（= RED） | 同 | `rgba(5,0,73,.8) solid 2px` | 同 jess | 同 | 同 forced |
| outline 像素（左邊 2px 帶） | **(55,111,208)**（RED (154,182,231) = 50% 合成）= focus ring 原色 | (55,111,208) | **(55,51,109)**（RED (154,152,181)）；vs 白 ΔE 高、可見 | (55,111,208) | (55,111,208) | (55,51,109) |
| ghost bg／shadow／border／z-index／position | `rgba(0,0,0,0)`／none／0／99999／absolute（= RED） | 同 | 同 | 同 | 同 | 同 |
| 閒置標題 ink 最深 | (33,33,33)，ink 87.5×15.5 | 同 | (0,0,0) | (33,33,33) | 同 | (0,0,0) |
| ghost 標題 ink 最深 | **(33,33,33)**（RED (143,143,143)） | (33,33,33) | **(0,0,0)**（RED (126)） | **(33,33,33)**（RED (127)） | (33,33,33) | (0,0,0) |
| ΔE 閒置 vs ghost 標題（J55-1） | **0**（RED 46.66） | 0 | **0**（RED 52.8） | **0**（RED 40.45） | 0 | 0 |
| 透出：標題列底下的頁面 ink 閒置 → 拖曳中 | n 2 | n 1360：(69) → **(251)**（RED (160)；標題列現在是不透明白底，底下文字幾乎全被蓋住） | — | n 0 | n 1360：(69) → (211)（RED (140)） | n 232：(0) → (236) |
| 透出：標題以下（`<dl>`）底下頁面 ink | n 0 | n 1312：(76) → **(76) ΔE 0（仍 100% 透出）** | — | n 0 | (76) → (76) ΔE 0 | (4) → (4) ΔE 0 |
| 放開後視窗 rect vs ghost | dx 0／dy 0 | 0／0 | 0／0 | 0／0 | 0／0 | 0／0 |

- **J55-1：** 空白區 ΔE 0 ≤ 3（jess、normal；forced-colors 也 0）。**PASS。**
- **J55-2：** ghost opacity 1、全部子孫 1。**PASS。**
- **保護項：** (a) outline 2px 可見、色 = focus ring 原色 (55,111,208)（定稿第 2 條二擇一的「原色」那一支；baseline 預期變動）✓；forced-colors 下 (55,51,109)（= `rgba(5,0,73,.8)` 合成白）可見 ✓ → (f) ✓。(b) ghost 300×180、位置、`z-index 99999` 與 RED 逐值相同 ✓。(c) 放開後四組（jess／normal × blank／content）dx／dy 皆 0 ✓，forced-colors 兩組也 0。(d) 閒置視窗：`final55-jess-blank-idle.png`、`-idle-page.png`、`final55-normal-blank-idle.png`、`final55-jess-forced-blank-idle.png` md5 **= RED**（逐像素相同）；放開後 `-dropped.png` 也 = RED；modal／highlighted／maximized 與 RED 一樣只有閒置基準（`-idle-page.png` 全頁），本輪未操作。(e) sizable 縮放 RED 未量、本輪也未量（與 RED 同口徑）。
- **透出（只記錄，D56 預留）：** 標題列現在是不透明白底（複製來的 `.z-window-header` bg `rgb(255,255,255)`，`showThrough.headerBand` 69 → 251），標題以下的空 `<dl>` 仍無底色、**100% 透出**（76 → 76）。Jess GIF 裡「標題淡」解決了，「標題以下只剩藍框、內容透出」仍在 — 這是最小解讀的範圍外，截圖 `shots/55-after.png` 可見。

## #57 — North／South 內容 padding（修後數字）

| borderlayout | 區域 | region size RED → 今天 | body rect RED → 今天 | 文字 ink 距 body 左 RED → 今天 | padding 來自 | `scrollHeight`／`clientHeight`（J57-3） |
|---|---|---|---|---|---|---|
| BL0 Basic（400 高） | north | 15% → **25%** | 32,100 1210×19 → 32,100 1210×**59** | 0 → **17** | `DIV.z-p-4` 16px → `.z-north-body` 0 | 59／59 ✓ |
| | south | 15% → **25%** | 1210×19 → 1210×59（t 441 → 401） | 0.5 → **16.5** | `DIV.z-p-4` | 59／59 ✓ |
| | west／east | 20% 不變 | 241×224 → 241×**144**（t 168 → 208） | 16／17 不變 | `DIV.z-p-4` | 144／144 ✓ |
| | center | — | 710×264 → 710×**184**（t 128 → 168） | 16.5 不變 | `DIV.z-p-4` | 184／184 ✓ |
| | splitter | | north 120–128 → **160–168**；south 392–400 → **352–360**；west／east x 274–282／992–1000 不變，高 264 → 184 | | | |
| BL1 Region Titles（300 高） | north | 20% → R1 35% → **R2 32%** | 1210×19 → R1 1210×64 → **R2 1210×55** | 0 → **17** | `DIV.z-p-4` | R1 64／64 ✓；**R2 55／55 ✓** |
| | south | 20% → R1 35% → **R2 32%** | 1210×19 → R1 64 → **R2 55**（t 341 → 305） | 0.5 → **16.5** | `DIV.z-p-4` | **R2 55／55 ✓** |
| | west／east | 20% 不變 | 241×140 → R1 **235×50** → **R2 241×68**（t 160 → 196） | 16／17 不變 | `DIV.z-p-4` | R1 **52／50 ✗（溢出，6px 捲軸）** → **R2 68／68 ✓、clientWidth 241（無捲軸）** |
| | center | — | 726×140 → R1 **720×50** → **R2 726×68** | 16.5 不變 | `DIV.z-p-4` | R1 **52／50 ✗** → **R2 68／68 ✓** |
| BL2 No Region Borders（300） | north／south | 20% 不變 | 1210×60 不變 | 1／0.5 → **17／16.5** | `DIV.z-p-4`（RED 是 `SPAN.z-label`） | 60／60 ✓ |
| | west／east／center | 不變 | 242×180／242×180／726×180 不變 | 16／17／16.5 不變 | `DIV.z-p-4` | ✓ |
| BL3 autoscroll | 五區 | 不變 | 逐值 = RED | 1／0.5／0／1／0.5 = RED | label | center 270／180（RED 就如此，autoscroll 例子） |
| BL4 center margins | 五區 | 不變 | 逐值 = RED | 1／0.5／0／1／0.5 = RED | label | ✓ |
| BL5 cmargins | 五區 | 不變 | 逐值 = RED | 1／0.5／0／1／0.5 = RED | label | ✓ |

- **J57-1：** BL0／BL1／BL2 的 N 17、S 16.5 vs W 16／E 17／C 16.5 → 互差 ≤ 1 ✓；BL3／4／5 的五區偏移與 RED 逐值相同（0–1）✓。**PASS。**
- **J57-2：** `.z-north-body`／`.z-south-body` computed padding 0px（六個 BL）；padding 鏈 `DIV.z-p-4 16px → .z-north-body 0 → .z-north 0`；`borderlayout.css.dsp` md5 = RED。**PASS。**
- **J57-3：** BL0／BL1／BL2 的 N／S body `scrollHeight = clientHeight`（59／64／60），文字 ink 距 body 上 21（= W／E／C 的 21），不被切，RED 的 6px 捲軸消失。**PASS。**
- **保護項：**
  - **(a)** BL0 的 N／S size 15% → 25%、BL1 20% → 35%（定稿第 6 條允許）；**連帶** BL0 的 W／E／C 與 south 的 header／splitter 都往下／上移了 40px（north 60 → 100 高）、BL1 移了 45px — 這是 size 改變的必然結果，定稿文字「其餘區域外框與 splitter 位置不變」在同一個 borderlayout 內不可能同時成立，**記錄為預期連帶**，請 Planner 確認口徑。BL2–5 的所有外框、header、splitter 逐值 = RED ✓。
  - **(b) West／East／Center 不變：** BL0 的 W／E／C 內容 ink 相對位置不變、仍容納（144／184 高）；**R1：BL1 的 W／E／C body 從 140 高縮到 50 高，`z-p-4` 內容 52px 塞不下 → `scrollHeight 52 > clientHeight 50`，三個區域各出現 6px 垂直捲軸（clientWidth 235 vs body 241；`final57-bl1.png` 右緣可見灰色捲軸條）** — 修前沒有，修後有，屬預期外的像素變動 → R1 FAIL。BL1 高 300：N／S 各 35% = 105，中間只剩 90，扣 40px header 剩 50。**R2（N/S 32%）：** N／S region 96、中間 108、扣 header 剩 **68 ≥ 52**，`scrollHeight 68 = clientHeight 68`、clientWidth 241／241／726 = body 寬 → **無捲軸（`final57-r2-bl1.png`）→ 過**。W／E／C 文字 ink 相對 body 16／17／16.5、上 21 不變。
  - (c) `border="none"` 例子（BL2）與 Auto Scroll 例子（BL3，center `scrollHeight 270 > 180`、`.z-vlayout-inner`）行為 = RED ✓。
  - (d) `final57-bl3.png`／`-bl4.png`／`-bl5.png` md5 = RED（逐像素相同）；其他頁面見回歸 ✓。
- **預覽站搜尋（定稿第 7 條）：** 不重跑，RED 結論不變（只有 `borderlayout.zul` 用 north／south）。

## 回歸

`pw-regression.log`，從 `zkpreview/` 跑，`PREVIEW_URL=http://localhost:8085`，`--output` 指到本目錄，沒有 `--update-snapshots`：

- **core（component-theming、hit-target、focus-scan、forced-colors，全量）：** `184 passed, 47 skipped`，exit 0（與第 8 批最終判定的 184／47 相同）。`pw-core/` 空 = 無失敗 artifacts。本批元件相關項目全過：component-theming 的 window／panel／tabbox 區域覆寫、focus-scan 的 borderlayout／window／panel／tabbox、forced-colors 的「accordion selected tab uses the system selection color」「elevation-only surfaces gain a border（window）」。
- **visual（gallery + chromium + forced-colors-gallery，`-g 'tabbox|panel|window|borderlayout'`）：** 13 tests，**11 passed、2 failed**，exit 1。失敗的兩個都是預期要變的 baseline：
  - **`chromium › tabbox › gallery`（預期變動）：** 1,808 pixels（ratio 0.01）不同，尺寸不變 1280×4332。`diff-gallery.js` 逐列歸因（`diff-gallery.json.tabbox`）：差異只在 y 3983–3995、4087–4100、4136–4149、4186–4197、4235–4246 五個帶（每帶 450–650 px）= 兩個 accordion 的 Tab1–Tab5 標題字（圖 `pw-visual/screenshot-tabbox-gallery-chromium/tabbox-gallery-diff.png` 也只有那十個字亮）。頁面其餘 3983px 以上 **0 像素差** → 水平、垂直 mold 與其他 tabbox 沒動。**全部屬預期（#53 字型）。**
  - **`chromium › panel › gallery`（預期變動 + 保護項 (b) 的 +4px）：** **尺寸 1280×2241 → 1280×2245（高 4px）**，47,637 pixels（ratio 0.02）不同。尺寸不同使 Playwright 的 diff 圖整頁亮，無法直接讀；`diff-gallery.js` 用 0／2／4px 列位移補償後只剩 **11,751 px**：p0（State Gallery 的 rounded，auto 高）之後整頁下移 2px、p11／p12（Collapsed／Expanded，auto 高）那一列再下移 2px，位移來源就是 #54 保護項 (b) 的三個 +2px。補償後的差異帶（`diff-gallery.json.panel.bands`）全部落在 rounded panel 所在的列：y 186–299（p0 外框＋文字 +1px）、437–685（p1 外框、標題列、toolbar、內容 +1px；同列的 normal p2 在 `final54.json` 的逐 panel 掃描中邊、角、陰影、ink 逐值 = RED）、1183–1382（p4／p7 外框與文字；同列 p5／p6 不變）、2055–2172（p11／p12 外框與文字）。列位移分析無法在水平方向把 p1 與 p2 分開，normal／none 不變的證據以 `final54.json` 的逐 panel 數字為準（全部 = RED）。**rounded 外框屬預期；+4px 頁高是保護項 (b) 的連帶，不是預期內。**
  - **`chromium › window › gallery` PASS**（預期不變，確認不變）；`chromium › tabbox › hover`、`container-header-height › window／panel header` PASS。
  - **`gallery › borderlayout` PASS、`gallery › tabbox-misc` PASS。** borderlayout 的 PASS 要說明：計畫預期 `borderlayout-gallery` baseline 會變，但 `gallery` 專案的 `toHaveScreenshot` 用 `maxDiffPixelRatio: 0.01` + pixelmatch 預設 YIQ 0.2；用 Playwright 自己的 comparator 嚴格比（`maxDiffPixelRatio` 不設）baseline 與今天的截圖差 **9,713 px（ratio 0.003）**，低於 1% 門檻所以過。用本批的逐像素尺（每通道 > 12）則差 **294,053 px（ratio 0.090）**（`pw-visual/borderlayout-gallery-actual.png` vs `zkpreview/doc/screenshots/borderlayout-gallery.png`，`diff-gallery.json.borderlayout`）：主要帶 y 217–265（34k）、450–538（79k）、711–796（82k）、847–932（81k）= BL0／BL1 的 North／South 變高後 West／East 的淡色底（(248,250,253) 類）與白底互換，YIQ 差太小被 pixelmatch 忽略；其餘小帶（437–1,330 px）是 BL0–2 N／S 文字右移 16px 與上下移。另外 y 34–54（1,232 px）與 104–116（2,174 px）兩帶是頁標題與第一個 section 標籤：`probe-bl-top.js` 把今天的 DPR-2 全頁截圖與 RED 的 `probe-borderlayout-page.png` 比，**頁面前 200 CSS px（含標題、section 標籤、North header）0 像素差**，差異從 y 200（BL0 body）才開始 → 那兩帶是 2026-09-11 建立的 baseline 與現況本來就有的差（本批之前就在），**不是本批造成**。結論：borderlayout gallery 的實際變動屬預期（#57 預覽頁），baseline 沒被更新、也沒被測試擋下；**BL1 West／East／Center 的新捲軸在這張 gallery 截圖裡也看得到（y 847–932 帶內），同樣被 1% 容差吃掉**。
  - **`forced-colors-gallery › borderlayout／panel／tabbox／tabbox-misc／window` 5 個 PASS** — 這個專案不是比對，是**直接用 `page.screenshot({path})` 覆寫 `zkpreview/doc/screenshots/*-forced-colors.png`**（spec 註解寫明是 review artifacts、不是 CI baseline，但檔案是 commit 在 repo 裡的）。我把它納入 `-g` 過濾的 visual 跑法時沒先讀這個 spec，**跑完後五個 PNG 被改寫（20:46）**。處置：五張新圖複製到 `pw-visual/forced-colors-gallery-after/`（可當 forced-colors 修後證據），然後 `git checkout --` 還原這五個檔到 HEAD（它們在本 session 開始的 `git status` 裡不是 M，還原的是我自己的覆寫）；還原後 `git status --short zkpreview/doc/screenshots` 為空。**沒有碰 `doc/font-size-baseline.json`、沒有 `--update-snapshots`**；`gallery`／`chromium` 專案的 `toHaveScreenshot` 失敗時只寫到 `--output`（`pw-visual/`），baseline PNG 未動（`borderlayout-gallery.png` mtime 仍 09-11、`panel/tabbox/window-gallery.png` 仍 10-06）。為了確認 `gallery › borderlayout` 自己拍到什麼，另外用 scratchpad 內的 wrapper config（`snapshotDir` 指到 scratchpad）跑了一次 `--update-snapshots`，產物在 scratchpad，wrapper 已刪，repo 無殘留。
- **已知失敗** `calendar-tablet`、`slider-tablet`、`grid-header-gallery` 不在本次範圍（tablet 未跑、grid 不在過濾條件內），未觸發。
- **歸因總結（R1）：** tabbox-gallery = 預期（#53）；panel-gallery = 預期（rounded 外框）+ +4px 頁高（保護項 (b)，R2 口徑下屬預期：三個 auto 高 rounded panel 各 +2px，佔兩列）；window-gallery 不變 ✓；borderlayout-gallery 實際有變（預期，#57）但被 1% 容差放過，R1 其中含預期外的 BL1 捲軸；沒有其他頁面／專案出現預期外變動（core 184/0）。
- **R2（只重跑 `gallery › borderlayout`，`pw-regression-r2.log`，`--output pw-visual-r2`，無 `--update-snapshots`；沒有再跑 forced-colors-gallery）：** `1 passed`，`zkpreview/doc/screenshots` 仍乾淨。像素量法 `diff-gallery-r2.js`（`pw-visual/borderlayout-gallery-actual-r2.png` vs baseline）：**278,671 px（ratio 0.086）**，帶與 R1 同構：y 34–54／104–116（1,232／2,174，09-11 baseline 既有差，見 `probe-bl-top`）、217–265（34,356）與 450–538（79,416）= BL0 N／S 變高（與 R1 逐值相同）、**BL1 的帶由 R1 的 711–796／847–932（81,672／81,009）變為 711–747＋751–787（44,951＋29,142）與 856–892＋896–932（28,284＋44,922）**= N／S 32% 後 N/S body 與 W/E 淡色底的新邊界；其餘小帶（437–1,330 px）= BL0–2 N/S 文字位移，與 R1 相同。**BL0／BL1 的變動屬預期（#57 預覽頁），BL1 無捲軸；仍被 `gallery` 專案的 1% 容差放過、baseline 未更新。**

## R3 — 第二輪判定（D56-A、D57-B）與第一輪重驗

### #53 R3（chevron 遮罩）

computed 追溯（不是判定依據）：`.z-tab-content::after` 盒 14×14、`mask-image: url(.../zul/img/marble/chevron-down.svg)`、選取 `background-color = color = oklch(0.23 0.0726 260.56)`（= `--zk-color-on-primary-container`，sRGB (7,27,62)）、未選取 `rgba(0,0,0,.6)`（= `--zk-color-on-surface-variant`）、選取 `transform: matrix(-1,0,0,-1,0,0)`（180°）、未選取 `none`、`border 0`（舊的 border 畫法已拿掉）。

| 列（text-only accordion；with-images 逐值相同） | chevron ink w×h RED → R3 | 距 tab 右緣 RED → R3 | ink 中心 − 列中心 RED → R3 | 最深像素 RED → R3 | 參考色 |
|---|---|---|---|---|---|
| Tab1 選取（朝上） | 11×7 → **8×5** | 14.5 → **15** | **−2 → 0**（557 vs 557） | (6,26,61) → **(7,27,62)** | on-primary-container (7,27,62) ΔE **0** |
| Tab2／Tab3 未選取（朝下） | 11×7 → **8×5** | 14.5 → 15 | **+2 → 0**（662／711） | (102,102,102) → (102,102,102) | on-surface-variant over white (102,102,102) ΔE **0** |
| Tab2 hover | — → 8×5 | 15 | 0 | (95,95,95) → (95,95,95) | 底 (237,237,237) ΔE 6.25 = RED |
| Tab4／Tab5 disabled | 11×7 → 8×5 | 15 | +2 → 0 | (196,196,196) → (196,196,196) | opacity 0.38 = RED |
| 點 Tab2 後（Tab2 選取朝上、Tab1 收合朝下） | 全 8×5 | 15 | 全 **0**（Tab2 606 vs 606、Tab1 557 vs 557） | — | 展開 cave 582–687（105）、Tab3 回 687 = RED |
| forced-colors（`matchMedia` true） | Tab1 **8×5** (0,0,0) 在選取底 (55,51,109) 上；Tab2／3 **8.5×5** (0,0,0)；Tab4／5 8×5 (157) | — | 全 0 | — | ink 非空、可見 |
| combobox 基準（不變） | 8×5 | **12.08** | 0 | (102,102,102) | — |

- **J53-2：** (a) 寬 8／高 5 vs 8×5 → 差 **0%** ✓；(b) 展開（朝上）與收合（朝下）中心 − 列中心 **0／0** ✓（RED ±2）；(c) **右緣距：chevron ink 右緣距 tab 外右緣 15（RED 14.5），combobox 12.08 → 差 2.92 > 2 → 不符**。若把「標題右內緣」讀成 tab 的 content box 右緣（tab.r − 16 padding，= `.z-tab-content` 右緣 607），chevron ink 右緣 608 在它右邊 1px，與 combobox 的 12（量到外緣）不是同一種距離，差更大；兩種讀法都 > 2。幾何：14px 盒內 8px 遮罩居中、盒右緣在 tab.r − 12 → ink 右緣 tab.r − 15；combobox 的 14px 盒右緣在 control.r − 9。**J53-2 = FAIL（只有 (c)，超出 0.92px）。**
- **J53-3：** forced-colors chevron ink 非空（8×5／8.5×5，黑或 (157)，兩個方向都有）✓；旋轉 180°：朝上 8×5 中心 557、朝下 8×5 中心 662／711，外框 ±0、中心 ±0（鏡像成立）✓；選取 chevron (7,27,62) = on-primary-container ΔE 0、未選取 (102,102,102) = on-surface-variant ΔE 0 ✓；hover (95)／(237,237,237)、disabled (196)／opacity 0.38 = RED ✓；列高 48、pitch 49、分隔線 y 637／686／735／784、外框 532／833、cave 56 = RED ✓；點 Tab2 動畫完成（Tab2 582–630、cave 至 687）✓；點 disabled Tab4 無變化 ✓ → **PASS**。
- **J53-1 重驗：** 14px／500／20px、ink 高 11 = 垂直 11 ✓；文字 ink 左 16.5（disabled 也 16.5）、DOM 50 = R1 ✓；水平 `final53-r3-horizontal0.png`／`-hover.png`、垂直 `-vertical3.png` md5 **= RED** ✓；horizontal／vertical3 的 JSON 與 R1 逐值相同 ✓。

### #55 R3（ghost 底色）

| 量法 | jess blank | jess content（蓋在 With Title 上） | jess forced | normal blank | normal content | normal forced |
|---|---|---|---|---|---|---|
| ghost computed bg R1 → R3 | `rgba(0,0,0,0)` → **`rgb(255,255,255)`** | 同 | → `rgb(255,255,255)` | 同 | 同 | 同 |
| `--zk-color-surface` 參考（頁面解析） | rgb(255,255,255) | 同 | rgb(0,0,0)（forced 下 token 被系統色取代；body bg 仍白） | 同 | 同 | 同 |
| ghost 內部取樣（標題以下） | (255,255,255) ΔE **0** vs surface | (255,255,255) ΔE **0** | (255,255,255) | (255,255,255) | (255,255,255) | (255,255,255) |
| 透出：標題以下底下頁面 ink 閒置 → 拖曳中 | n 0 | n 1312：(76) → **(255)**（R1 (76) ΔE 0 = 100% 透出；R3 ΔE 67.68 = 完全蓋住） | — | n 0 | (76) → (255) | n 452：(4) → (255) |
| 透出：標題列底下 | — | (69) → (251)（= R1） | (2) → (251) | — | (69) → (211)（= R1） | (0) → (236) |
| J55-1 標題 ink ΔE | 0 | 0 | 0 | 0 | 0 | 0 |
| J55-2 opacity ghost／子孫 | 1／全 1 | 同 | 同 | 同 | 同 | 同 |
| outline 像素 | (55,111,208) = R1 | 同 | (55,51,109) = R1 | 同 | 同 | 同 |
| ghost rect／z-index | 889,122 300×180／99999 = R1 | 369,442 = R1 | = R1 | 889,516 = R1 | 369,436 = R1 | = R1 |
| 放開落點 dx／dy | 0／0 | 0／0 | 0／0 | 0／0 | 0／0 | 0／0 |

- **J55-3：** 空白區與內容區落點，ghost 標題以下取樣皆 (255,255,255)，與 `--zk-color-surface` (255,255,255) ΔE **0 ≤ 2**；蓋在內容上時取樣相同、底下文字 ink 從 (76) 變 (255)（不再透出）→ **PASS**。
- **J55-4：** J55-1／2 仍 0／全 1 ✓；outline 2px focus ring 色 (55,111,208) 可見 ✓；尺寸、位置、z-index = R1 ✓；落點 0 ✓；forced-colors：outline (55,51,109) 可見、ghost bg 白、內部 (4) → (255) 不透明 ✓ → **PASS**。閒置截圖 `final55-r3-*-idle.png` 與 R1／RED 同一組數字（視窗 rect、標題 ink 皆同）。

### #54、#57 R3 重驗

- **#54：** `final54-r3.json` 的 13 個 panel（normal 與 forced-colors）在 cls、rect、head、body、computed、四邊／角／中段／底邊兩端 ΔE、陰影、分隔線、標題／內容／icon ink、toolbar **全部與 R1 逐值相同**；`panel.css.dsp` md5 未變。R2 口徑（固定高 ±0、auto 高 +2）→ **維持 PASS**（J54-1、J54-2 同）。
- **#57：** `final57-r3.json` 六個 borderlayout 的 region／head／splitter／body／ink／scroll／size／padding **全部與 R2 逐值相同**；`final57-r3-bl0…bl5.png` md5 = R2（bl3–5 = RED）。J57-1／2／3 與保護項 → **維持 PASS**。

### R3 回歸

`pw-regression-r3.log`（pid 12560 全程），`--output pw-core-r3`／`pw-visual-r3`，無 `--update-snapshots`，**沒有跑 forced-colors-gallery**；跑完 `zkpreview/doc/screenshots` 與 `doc/font-size-baseline.json` 皆乾淨。

- **core 全量：** `184 passed, 47 skipped`，exit 0（= R1）。forced-colors 專案全過（含「accordion selected tab uses the system selection color」）。
- **visual（gallery + chromium，`-g 'tabbox|panel|window|borderlayout'`）：** 8 tests，6 passed、2 failed：
  - `chromium › tabbox › gallery`：**2,062 px**（R1 1,808）、尺寸不變；`diff-gallery-r3.json.tabbox` 逐列：仍只有 y 3983–3995／4087–4100／4136–4149／4186–4197／4235–4246 五帶（706／752／768／580／560 px，R1 588／634／650／470／450）= 兩個 accordion 的五列（字型 + 新 chevron），其餘 0 → **預期**。
  - `chromium › panel › gallery`：2241 → 2245、47,637 px，**`panel-gallery-actual.png` md5 與 R1 完全相同** → 與 R1 同一個預期變動（R2 口徑）。
  - `chromium › window › gallery` **PASS**（預期不變）；`tabbox › hover`、`container-header-height` window／panel PASS；`gallery › borderlayout`、`gallery › tabbox-misc` PASS。borderlayout 像素量法：`borderlayout-gallery-actual-r3.png` md5 **= R2 的截圖**（278,671 px vs baseline，同 R2 歸因，全屬 #57 預期）。
- 已知失敗三項未觸發。沒有其他預期外變動。

## 判定

- J53-1：14px = 14px、同字 ink 高 11 = 11 → **PASS**；chevron 數字不變（D56 待裁示）。
- J54-1：六個 rounded 四邊 (224) ΔE 10.82、與 normal 色差 0 → **PASS**；J54-2：角白、中段 10.82、線從 2.5–4px 起 → **PASS**；陰影、分隔線仍 0（沒越界）。
- J55-1：ΔE 0（含 forced-colors）→ **PASS**；J55-2：全 1 → **PASS**；outline 變 focus ring 原色（定稿允許）；ghost 標題以下仍 100% 透出（記錄，D56）。
- J57-1：BL0–2 差 ≤ 1、BL3–5 不變 → **PASS**；J57-2：padding 0、css 未改 → **PASS**；J57-3：N／S 不溢出 → **PASS**。
- **R1 的兩個保護項失敗與 R2 處置：**
  1. **#57 (b)：BL1 West／East／Center 溢出（52 > 50）、6px 捲軸**（`final57-bl1.png`）→ Planner 把 BL1 N/S size 改 32% → **R2 body 68 ≥ 52、無捲軸（`final57-r2-bl1.png`、`final57-r2.json.layouts[1]`）→ 過**。
  2. **#54 (b)：auto 高度 rounded panel +2px**（p0 116 → 118、p11 60 → 62、p12 120 → 122；固定高 p1／p4／p7 ±0）→ Planner 改口徑「固定高度 ±0；auto 高度 +2px」→ **R2 依新口徑：固定高 ±0 ✓、auto 高 +2 ✓、寬 ±0 ✓ → 過**（不重跑，數字同 R1；panel-gallery 的 +4px 頁高隨之屬預期）。
- **備註（不算失敗）：** #53 disabled 列文字 ink 左緣 17 → 16.5（glyph 側邊距，DOM 不動）；#57 (a) BL0／BL1 的 W／E／C 與 south 因 N／S size 改變整體位移（BL0 40px、BL1 R2 36px）— 定稿允許 size 改變的必然結果；`gallery › borderlayout` 的 1% 容差放過 8.6% 的像素變動，baseline 仍是 09-11 版（預期變動未更新，需另行處理）；forced-colors-gallery 覆寫事故已還原（見回歸節）。
- 回歸：core 184/0；visual tabbox／panel gallery 為預期 baseline 變動（R2 口徑下含 panel +4px）；window 不變；borderlayout R2 重跑 PASS、像素歸因全屬預期。

GATE11-FINAL-R2: PASS（R1 FAIL 的兩項：#57 BL1 捲軸已由 N/S 32% 消除；#54 auto 高 +2px 依新口徑通過。待辦：tabbox／panel／borderlayout gallery baseline 更新、D56 chevron、#55 ghost 內容透出為 follow-up／D56）

### R3 判定

- **J53-2：FAIL**（只有右緣距一條）：chevron ink 右緣距 tab 右緣 **15**，combobox **12.08**，差 **2.92 > 2**（超出 0.92px；RED 14.5）。寬高 8×5（0%）、兩方向中心差 0 皆過。
- J53-3：PASS（forced-colors 可見、180° 鏡像 ±0、選取／未選取色 ΔE 0、hover／disabled／列高 48／pitch 49／動畫 = RED）。
- J55-3：PASS（ghost 內部 = surface (255,255,255) ΔE 0，蓋在內容上不透出 (76) → (255)）；J55-4：PASS。
- 第一輪重驗：J53-1、J54-1／2、J55-1／2、J57-1／2／3 與各保護項全部維持（#54 = R1 逐值、#57 = R2 逐值、水平／垂直 mold PNG = RED）。
- 回歸：core 184/0；visual tabbox（字型 + chevron）／panel（= R1）為預期 baseline 變動，window 不變，borderlayout = R2。
- 環境：pid 12560、jar mtime、伺服 md5 開跑前後一致。

GATE11-FINAL-R3: FAIL（J53-2 右緣距：15 vs combobox 12.08，差 2.92 > 2px；其餘 J53-3、J55-3、J55-4 與第一輪全部項目 PASS，回歸無預期外變動。請 Planner 裁示：是調 chevron 盒的右側偏移（約 3px）還是放寬 (c) 的容差）

## R4 — #53 右緣距修正後重驗（只量 #53）

| 列（text-only；with-images 逐值相同，chevron x 1217 → 1220） | chevron ink w×h | 距 tab 右緣 R3 → R4 | 中心 − 列中心 | 最深像素 | 其他 |
|---|---|---|---|---|---|
| Tab1 選取（朝上） | 8×5 | 15 → **12** | 0（557 vs 557） | (7,27,62) = on-primary-container ΔE 0 | 底 (213,230,255) |
| Tab2／Tab3 未選取（朝下） | 8×5 | 15 → **12** | 0 | (102,102,102) = on-surface-variant ΔE 0 | — |
| Tab2 hover | 8×5 | 12 | 0 | (95,95,95) | 底 (237,237,237) ΔE 6.25 = RED |
| Tab4／Tab5 disabled | 8×5 | 12 | 0 | (196,196,196) | opacity 0.38 = RED |
| 點 Tab2 後（Tab2 朝上、Tab1 朝下） | 全 8×5 | 12 | 全 0（606／557） | — | cave 582–687、Tab3 回 687 = RED；點 Tab4 無變化 |
| forced-colors | Tab1 8×5 (0,0,0) 在 (55,51,109) 上；Tab2／3 8.5×5 (0,0,0)；Tab4／5 8×5 (157) | 12 | 全 0 | — | ink 非空 |
| combobox 基準 | 8×5 | 12.08 | 0 | (102,102,102) | 不變 |

- **J53-2 PASS：** 寬高 8×5（差 0%）；兩方向中心差 0；**右緣距 12 vs combobox 12.08 → 差 0.08 ≤ 2**（R3 2.92）。
- **J53-3 PASS：** forced-colors chevron 可見（兩方向）；180° 鏡像外框 ±0、中心 ±0；選取／未選取色 ΔE 0；hover／disabled 外觀、列高 48／pitch 49、分隔線 y 637／686／735／784、外框 532／833、cave 56 皆 = RED；動畫完成。
- **J53-1 PASS：** 14px／500／20px；Tab1–3 ink 高 11 = 垂直 11。
- **保護項：** 列高／pitch／text DOM 50（with-images 667）／ink 左 16.5（with-images 40.5）/ 色彩 / cave 皆 = R3 = RED 相應值；水平 `final53-r4-horizontal0.png`／`-hover.png`、垂直 `-vertical3.png` md5 **= RED**；`horizontal`／`vertical3`／`combobox` JSON 與 R3 逐值相同；`.z-tab-image` ink 不變。
- **回歸 R4（`pw-regression-r4.log`，無 `--update-snapshots`，未跑 forced-colors-gallery；baseline 乾淨）：** `chromium › tabbox › gallery` **2,006 px**（R3 2,062）失敗 = 預期；`diff-gallery-r4.json` 逐列仍只有 y 3983–3995／4087–4100／4136–4149／4186–4197／4235–4246 五帶（700／750／766／580／560 px）= 兩個 accordion 五列的字型 + chevron，其餘 0。`chromium › tabbox › hover`、`gallery › tabbox-misc` PASS；`forced-colors › accordion selected tab uses the system selection color` PASS。panel／window／borderlayout 的 `.css.dsp` 與預覽頁未改，沿用 R3 結果。

### R4 判定

- J53-2：8×5／中心 0／右緣距 12 vs 12.08 → **PASS**。J53-3：PASS。J53-1：PASS。#53 保護項全部維持（水平／垂直 mold PNG = RED）。
- #54、#55、#57：檔案未改（md5 = R3），R3 結論沿用（J54-1／2、J55-1／2／3／4、J57-1／2／3 PASS）。
- 回歸：tabbox gallery 差異只在 accordion 字型 + chevron；forced-colors tabbox 項 PASS；其餘沿用 R3（core 184/0）。
- 待辦（不影響判定）：tabbox／panel／borderlayout gallery baseline 更新（tabbox 2,006 px、panel 2241 → 2245、borderlayout 被 1% 容差放過但實際有變）；`forced-colors-gallery` 會覆寫 repo PNG，回歸一律排除。

GATE11-FINAL-R4: PASS
