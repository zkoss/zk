# zkdemo B8 Z9／Z11／Z12 實作派工（工程師用）

日期：2026-10-15。決策來源：`doc/zkdemo-b8-triage.md`「第二批：Z9–Z12」與 D4（使用者已同意選項 A）。先讀 `.claude/skills/marble-theme/SKILL.md`。

## 共同限制

- **不要動** `ZK10/zk`、`ZK10/zkcml` 這兩個主工作樹（其他 session 在用），不要碰 8085，也不要在 8085 上重建 jar。
- 自己建 worktree：`ZK10/b8b/{zk,zkcml}`，zk 從 `marble` 的 `aa52616af5` 切出 branch `zkdemo-b8b`；zkcml 用 `git worktree add --detach`（只拿來建 jar，不改）。預覽站用 **8086**。建法見 memory「Preview from a fresh worktree」：symlink `node_modules`（`zk/`、`zk/zkpreview/`、`zkcml/`）、`bash ./gradlew`、`withjdk.sh 17`、先 `:zul:compileMarbleCss` 和 jar 再 `appRun`。
- 停伺服器只 kill 自己啟動時記下的 PID（Jetty 與 `tail -f /dev/null` holder 都要記），**不要用 `pkill -f`、`killall`**，別的 session 用同樣的 holder。
- 不要 `publishToMavenLocal`。只 commit，不 push。`git add <paths>` → `git diff --cached --name-only` 與預期清單比對 → `git commit` 在同一個 Bash 呼叫完成。
- 探針頁放在你 worktree 的 `zkpreview/build/inplaceWebapp`，用完刪除並確認 404；不放 `src/main/webapp`。
- 只做下面三項（O10）；看到的其他問題寫進回報，不要順手修。

## 工作 1：Z9 calendar 的 width／height 屬性要生效

檔案：`zul/src/main/resources/web/js/zul/db/css/calendar.css`。

1. 在 `.z-calendar`（約 25–37 行）之後加 `.z-calendar[style*="width"] { max-width: none; }`，加註解：ZK 的 `width`／`hflex` 寫成 inline width，`max-width: 268px` 會蓋過它（V4）；預設（沒有 inline width）維持 `min(容器, 268px)`。前例：`zul/.../wgt/css/separator.css:27`。確認 tablet 的 `../zkcml/zkmax/src/main/resources/web/zkmax/css/tablet/_calendar.css`（`max-width: 360px`）與 `_wheel.css` 的預設行為不變。
2. `.z-calendar-body`（約 103–110 行）加 `flex: 1 1 auto;`，讓 `height` 屬性把日期表格撐高（IceBlue 是 `.z-calendar-body { height: 100% }`）。沒設 height 時外觀必須不變。
3. 若 `doc/contracts/calendar.md`、`.html` 或 `doc/spec/` 有寫 268px 上限或高度的斷言，同步更新。
4. commit：`ZK-6112: let the calendar width and height attributes take effect (zkdemo B8 Z9)`

## 工作 2：Z11 把 `span.z-upload` 規則補回來

IceBlue 的規則在 `git show 10.4:zul/src/main/resources/web/zul/less/norm.less` 第 539–555 行；`2100200284`（LESS 換成 CSS）漏掉了，Marble 全 repo 沒有 `z-upload` 規則。`zul/Upload.ts:183` 會在任何 `upload` 按鈕（button、fileupload、toolbarbutton、menuitem）後面插入 `<span class="z-upload"><form><input type="file">`。

1. 找出 Marble 中對應 `norm.less` 的全域 CSS 檔（一定會載入、不屬單一元件），把 `span.z-upload` 與 `span.z-upload input` 的功能性宣告搬過去：span 為 0×0 `inline-block`、`font-size: 0`、`margin`／`padding` 0、`position: relative`；input 為 `opacity: 0`、`position: absolute`、`margin`／`padding` 0、`cursor: pointer`、`z-index: -1`。這些是功能值，不是外觀值，可以照抄；`font-size: 45pt` 是否需要，讀 `Upload.ts` 的 `sync()` 後自行判斷並在 commit 說明。檔案選擇要寫明理由。
2. 先 grep 相關 `.ts` 是否以 `getComputedStyle(` 或 `.style[` 讀這些值。
3. commit：`ZK-6112: restore the zero-size upload span that the LESS-to-CSS move dropped (zkdemo B8 Z11)`

## 工作 3：Z12 停用日期格顯示 not-allowed 游標

`calendar.css` 約 241–246 行的 `.z-calendar-cell.z-calendar-disabled`：拿掉 `pointer-events: none`，保留 `cursor: not-allowed`；改寫上方註解，寫明點擊由 `Calendar.ts` `_chooseDate`（檢查 `.z-calendar-disabled`）忽略，hover 圓盤由下方 `::before` 透明規則壓掉。`.z-calendar-outrange` 不動。

commit：`ZK-6112: let disabled calendar days show the not-allowed cursor (zkdemo B8 Z12)`

## 自我檢查（在 8086 上，用暫時探針頁）

- Z9：`<calendar width="440px"/>` 寬 440；480px 容器裡 `width="100%"` 寬 480；沒設 width 的 calendar 仍是 268；`height="400px"` 時日期格高度變大、下方沒有大片留白；沒設 height 時日期表格高度和修改前相同（量修改前後）。datebox 彈出的 calendar 寬度修改前後相同。
- Z11：在 `z-d-flex z-flex-col z-gap-3` 容器內放 `<button upload="true"/>` 與 `<fileupload/>`：`span.z-upload` 的 offsetHeight 0，按鈕下方間距只剩 gap；用真實 click 打開檔案選擇器（攔 `filechooser` 事件），證明上傳仍可用。
- Z12：停用格的 computed `cursor` 為 `not-allowed`，`elementFromPoint(格子中心)` 是該 `td`；用真實滑鼠點停用格，calendar 的值不變，hover 時沒有圓盤。
- Playwright：照 CLAUDE.md 的 Playwright 段，用 repo config、`PREVIEW_URL=http://127.0.0.1:8086`，以 `-g` 篩 calendar、datebox、daterangebox、fileupload、button 跑 `gallery-scan`、`render-smoke`、`tablet`、`component-theming`。**不要跑 `forced-colors-gallery`**（會改寫全部 99 張 PNG）。基準若需更新用 `--update-snapshots=changed`，跑完 `git status --porcelain` 確認只有預期檔案；`changed` 改寫不了的，在回報中列出，不要用 `all` 繞過。
- `npm run lint -- zul/src/main/resources/web/js` 不需要（沒改 TS）；CSS 若有 lint:css 腳本就跑。

## 回報

寫在 `doc/zkdemo-b8b-impl-report.md`（繁體中文，技術詞保留英文）：每個 commit 的 hash 與訊息、改了哪些檔、上面每個檢查的實際數值（修改前／後）、Playwright 的實際輸出摘要（失敗照實寫）、無法驗證的部分、worktree 與伺服器的狀態（PID、是否已停）。不要宣稱「已驗證」；驗證由另一個 agent 負責。完成後伺服器維持在 8086 執行，讓驗證者使用，並在回報中寫明 PID。
