# zkdemo B9 Marble 修正派工（工程師用）

日期：2026-10-15。判定來源：`doc/zkdemo-b9-triage.md`。先讀 `.claude/skills/marble-theme/SKILL.md` 與 `reference/decision-principles.md`。三項都是 O1（IceBlue 沒有此問題）判定的 Marble 退化，CSS 都在 **zkcml** 的 zkex。

## 共同限制

- **不要動** `ZK10/zk`、`ZK10/zkcml` 這兩個主工作樹（其他 session 在用），不要碰 8085（不是本 session 的），也不要在 8085 上重建 jar。
- 自己建 worktree `ZK10/b9/{zk,zkcml}`：zkcml 從 `marble` 的 `fe5649586` 切出 branch `zkdemo-b9`；zk 用 `git worktree add --detach 523f64b14d`（只拿來建 jar 與跑 zkpreview，不改）。預覽站用 **8087**。建法見 memory「Preview from a fresh worktree」：symlink `node_modules`（`zk/`、`zk/zkpreview/`、`zkcml/`）、`bash ./gradlew`、`withjdk.sh 17`、先 `:zul:compileMarbleCss` 和 jar 再 `appRun`。zkex 的 CSS 若有自己的 compile task，一併執行。
- 停伺服器只 kill 自己啟動時記下的 PID（Jetty 與 `tail -f /dev/null` holder 都要記），**不要用 `pkill -f`、`killall`**。
- 不要 `publishToMavenLocal`。只 commit，不 push。`git add <paths>` → `git diff --cached --name-only` 與預期清單比對 → `git commit` 在同一個 Bash 呼叫完成。commit 訊息依 `ZK-6112: … (zkdemo B9 Zn)` 格式。
- 探針頁放在你 worktree 的 `zkpreview/build/inplaceWebapp`，用完刪除並確認 404；不放 `src/main/webapp`。
- 移除或改寫既有宣告前，先 grep 該元件的 `.ts` 是否以 `getComputedStyle(` 或 `.style[` 讀取；依賴的宣告要保留並註解是哪一行讀取。
- 只做下面三項（O10）；看到的其他問題寫進回報，不要順手修。不新增公開的 `--zk-*` token、不新增 `!important`（都是 §4 的情況）；需要時停下來寫進回報。
- 數值依 Marble 實測，不照抄 IceBlue 的數字（O7）；顏色只用既有 token。

## 工作 1：Z1 pdfviewer 文字層

檔案：`zkcml/zkex/src/main/resources/web/js/zkex/pdfviewer/css/pdfviewer.css` 約 31–35 行。IceBlue 原規則：`git -C <zkcml> show master:zkex/src/main/resources/web/js/zkex/pdfviewer/less/pdfviewer.less` 第 53–73 行。

1. 補回功能性宣告：`.z-pdfviewer-text-layer` 的 `overflow: hidden`、`line-height: 1`；`.z-pdfviewer-text-layer > span` 的 `color: transparent`、`position: absolute`、`white-space: pre`、`cursor: text`、`transform-origin: 0 0`。
2. `::selection`：讀 IceBlue 的 `@pdfviewerSelectionBackgroundColor` 值，判斷透明文字在 opacity 0.2 下是否需要它才看得出選取。需要的話用既有的色彩角色 token（例如 primary），不新增公開 token；理由寫進 commit。
3. commit：`ZK-6112: hide and align the pdfviewer text layer again (zkdemo B9 Z1)`

## 工作 2：Z6 fisheye 標籤與圖片定位

檔案：`zkcml/zkex/src/main/resources/web/js/zkex/menu/css/fisheye.css`。IceBlue：`master:zkex/src/main/resources/web/js/zkex/menu/less/fisheye.less`。JS：`zkex/.../menu/Fisheyebar.ts`、`Fisheye.ts`（讀它們如何設定 item、image、text 的 `left`／`top`／`width`／`height` 與 text 的 `display`）。

1. `.z-fisheye`、`.z-fisheye-image`、`.z-fisheye-text` 回到 `position: absolute`；`.z-fisheye-text` 預設 `display: none`（由 JS 顯示，先確認 JS 確實會設定）；`.z-fisheyebar-inner { position: relative }`。拿掉會干擾 JS 計算的 flex 版面（bar 的 `display: flex`、`padding`、`gap`，item 的 flex 排版），除非讀 JS 後證明它們無害；保留的要寫理由。
2. 標籤外觀：IceBlue 是 tooltip 樣式的小卡。依 V6（同角色同外觀），找出 Marble 現有的 tooltip 規則（例如 `zul` 的 popup／tooltip、rangeslider tooltip 用的 `inverse-surface`／`inverse-on-surface`），用相同 token、字級、圓角、padding。
3. 驗證 `labelEdge` 的 `top`、`bottom`、`left`、`right`（水平與垂直、`attachEdge` 各一）標籤都在 JS 指定的位置。
4. commit：`ZK-6112: position the fisheye image and label absolutely again so the widget layout applies (zkdemo B9 Z6)`

## 工作 3：rangeslider 刻度標籤與提示框的空間

檔案：`zkcml/zkex/src/main/resources/web/js/zkex/slider/css/rangeslider.css`。IceBlue：`master:zkex/.../slider/less/rangeslider.less` 第 87–90 行（水平 inner `margin: 40px 12px 30px`）、126–129 行（垂直 inner `margin: 12px 30px 12px 12px`）。Marble 目前水平 `.z-rangeslider-horizontal { padding: 10px 0 }`（box 24px）。

1. 量 Marble 的實際幾何：刻度標籤底緣、`tooltipVisible` 提示框頂緣、兩端按鈕超出軌道的量，相對於 `.z-rangeslider` 的 border box。zkdemo 量到：標籤到下緣下方 28px、提示框到頂端上方 34px、垂直時標籤超出約 10px。
2. 讓 rangeslider 的 box 包住刻度標籤與兩端按鈕（刻度預設就會顯示，`markScale` 預設 20）。提示框：先讀 `Sliderbuttons.ts` 的 `setTooltipVisible`，若有 class 或結構可在 CSS 判斷「常駐顯示」（O4，可用 `:has()`；**不要**比對 inline style 的值），只在常駐時預留上方空間，hover 時的提示框照一般 tooltip 浮在上層；若無法判斷，照 IceBlue 一律預留。選擇與理由寫進 commit 與回報。
3. 軌道寬度由 `track.width()` 計算（見 `rangeslider.css` 第 65–68 行註解），改完確認拖曳數值仍正確（`step` 對齊、端點 min／max）。
4. V6：檢查 `multislider`（共用 `--zk-rangeslider-label-gap`）是否有相同的超出；結構相同就一併修，另一個 commit。
5. commit：`ZK-6112: reserve room for the rangeslider mark labels and tooltips as IceBlue did (zkdemo B9)`；版面成本（box 由 24px 變多少）寫進 commit 內文。

## 工作 4：Z8 captcha 比較截圖（不改 CSS、不 commit）

給使用者決策用（D1）。在 8087 用探針頁 `<captcha value="Acme42" frame="true"/>` 與同值 `frame="false"`（各放在白底與 `z-bg-surface-container-low` 底上），2x DPR，裁到 captcha 周圍 24px，每張 PNG 都要標示清楚：

- `current-frame-{true,false}.png`：目前 Marble。
- `optA-frame-{true,false}.png`：注入 `.z-captcha { border: 0; border-radius: 0 }`（等同 IceBlue：IceBlue 沒有 captcha 樣式）。
- `optC-frame-{true,false}.png`：注入 `.z-captcha { border-radius: 0 }`（保留外框、去圓角）。

存到 `zk` 主工作樹的 `doc/zkdemo-b9-z8/`。captcha 的字是隨機繪製：每組截圖用同一個 `value`，並確認引擎（zkex）有載入。

## 自我檢查（在 8087 上）

- Z1：載入多頁 PDF（zkpreview 若有 pdfviewer 頁就用它）：文字層 span 的 computed `color` 為透明、`position: absolute`；頁面上看不到灰字；拖曳選取文字時選取範圍與 PDF 字跡對齊；旋轉 90° 後選取仍對齊。截圖修改前／後。
- Z6：`attachEdge="bottom" labelEdge="top"`（水平）與 `orient="vertical" attachEdge="left" labelEdge="right"`：用真實滑鼠移入，量標籤與放大項目的位置（標籤要在項目外側的指定邊），截圖修改前／後；移開後恢復。
- 捲動：頁面捲動到第二個 viewport 之後的 fisheyebar 仍有 Z13（核心 bug，不在本次範圍），測試時放在第一個 viewport 內。
- rangeslider：水平（有刻度、`tooltipVisible`）、垂直（有刻度）各一，量 box 與標籤、提示框、按鈕的 bounding box，證明全部在 box 內；用 `page.mouse` 拖曳按鈕，數值變化與修改前相同。
- Playwright：照 CLAUDE.md 的 Playwright 段，用 repo config、`PREVIEW_URL=http://127.0.0.1:8087`，以 `-g` 篩 pdfviewer、fisheye、rangeslider、multislider、captcha 跑 `gallery-scan`、`render-smoke`、`tablet`、`component-theming`（存在的才跑）。**不要跑 `forced-colors-gallery`**。基準若需更新用 `--update-snapshots=changed`，跑完 `git status --porcelain` 確認只有預期檔案；逐張說明為什麼改變。
- 若 `doc/contracts/{pdfviewer,fisheye,fisheyebar,rangeslider}.md` 或 `doc/spec/` 有與修正矛盾的斷言，同步更新並列在回報中。

## 回報

寫在 `zk` 主工作樹的 `doc/zkdemo-b9-impl-report.md`（繁體中文，技術詞保留英文；它與工作 4 的 `doc/zkdemo-b9-z8/` 是唯二可寫入主工作樹的路徑，都不要 commit）：每個 commit 的 hash 與訊息、改了哪些檔、上面每個檢查的實際數值（修改前／後）、截圖路徑、Playwright 的實際輸出摘要（失敗照實寫）、無法驗證的部分、worktree 與伺服器狀態（PID）。不要宣稱「已驗證」；驗證由另一個 agent 負責。完成後伺服器維持在 8087 執行，讓驗證者使用。
