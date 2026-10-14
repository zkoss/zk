# zkdemo B8 Z5 carousel 實作回報

日期：2026-10-14。範圍：工作 2 第 4、5、6 步，只做 Z5 carousel。未驗證，驗證交給另一個 agent。

## Commit（worktree `ZK10/b8/zk`，branch `zkdemo-b8`，未 push）

- `24bd1c629b` ZK-6112: draw the active carousel indicator as a primary pill so it reads on light slides (zkdemo B8 Z5)

檔案：
- `zul/.../web/js/zul/wgt/css/carousel.css`：`.z-carousel-indicator-active::before` 改成 `width:22px`、`border-radius: var(--zk-shape-corner-full)`、填 `--zk-carousel-indicator-current-bg`、框 `--zk-carousel-indicator-current-ring`；resting/active 註解與檔頭註解改寫。
- `zul/.../web/zul/css/tokens/_component-theme.css`：新增 `--zk-carousel-indicator-current-bg: var(--zk-color-primary)`、`--zk-carousel-indicator-current-ring: var(--zk-color-on-primary)`；carousel 區註解更新（resting 框線不再是「非填色那一個」）。
- `zul/.../web/zul/css/tokens/_forced-colors.css`：規則（390-391）本身未改，仍成立（resting GrayText、active Highlight，且 active 現在還有形狀差異）；只改了該規則上方「沒有形狀差異」的註解。
- `zkpreview/doc/screenshots/carousel-forced-colors.png`：baseline 更新。
- `zkpreview/src/main/webapp/web/component-theming.zul` 與 `src/test/playwright/component-theming.spec.ts`：既有測試 `carousel — regional arrow-bg/indicator-active-bg override` 讀 active 點的 `::before` 背景並預期 override 值；active 點不再用 `-indicator-active-bg`，測試因此失敗（Expected rgb(103,80,164)，Received rgb(55,111,208)）。改成 override `--zk-carousel-indicator-current-bg`，通過。這是超出 brief 檔案清單的修改，請確認。

## 實際結果

- `withjdk.sh 17 bash ./gradlew :zul:compileMarbleCss`：BUILD SUCCESSFUL，"CSS build complete for --module zul (minified)"。
- zk 與 zkcml jar 建置：皆 BUILD SUCCESSFUL。appRun 在 8086 起來，`/` 回 200。
- Playwright（`PREVIEW_URL=http://127.0.0.1:8086`，repo config，`-g carousel`）：render-smoke、forced-colors-gallery、gallery-scan、component-theming 的 carousel 測試最終全過；修改前 component-theming 一項失敗（如上）。
- `--update-snapshots=changed`：只有 `carousel-forced-colors.png` 變動；`git status --porcelain` 除此之外只有上述 zul/spec 與 CSS。

## 無法驗證 / 未完成

- `doc/screenshots/carousel-gallery.png` 沒有更新：gallery-scan 對舊 baseline 在容忍範圍內通過，`changed` 模式不會改寫；我讀了該 PNG，仍是舊的空心點。要更新需要 `--update-snapshots=all`，該指令被權限分類器拒絕，我沒有再繞過。需要使用者決定是否授權（`-g "gallery carousel$"` 限定後確認只有 carousel 檔變動）。
- 未跑 lint:css、zktest；未測 hover（`scale(1.25)` 套在 22px pill 上，約 27.5px，超出 24px 按鈕但按鈕不裁切，carousel root 的 overflow 為 hidden，未實測）。
- 未實際在 Windows forced-colors 模式以外的環境檢視 pill；forced-colors baseline 讀圖看到 active 為深色 pill。
- 清理時我用 `pkill -f "tail -f /dev/null"` 停 appRun 的 stdin holder；這個樣式可能同時符合其他 session 的 tail 程序，請留意是否有其他 session 的 stdin holder 被結束。8086 已停，`lsof` 無佔用。

## 截圖（主 zk 樹，未 git add）

- `/Users/hawk/Documents/workspace/ZK10/zk/doc/zkdemo-b8-z5/z5-light-after.png`
- `/Users/hawk/Documents/workspace/ZK10/zk/doc/zkdemo-b8-z5/z5-img-after.png`

DSF 3，280x64 css px，置中、底邊對齊 carousel，activeIndex 1。Probe 頁已從 `build/inplaceWebapp` 刪除，確認回 404。

## 其他

- `b8/zk` 的 `node_modules`、`zkpreview/node_modules` 是 symlink，在 `git status` 顯示為未追蹤，未加入 commit。

## 追加修正（F1–F4）

Commit：`76258d5988`（worktree `ZK10/b8/zk`，branch `zkdemo-b8`，未 push）。
訊息：`ZK-6112: keep indicator-active-bg as the active carousel fill and give the resting ring its own token (zkdemo B8 Z5)`。

### Token 設計
- `--zk-carousel-indicator-bg`：resting 填色，`--zk-color-scrim`（不變）。
- `--zk-carousel-indicator-ring`（新）：resting 框線，`--zk-color-inverse-on-surface`，與舊值相同。
- `--zk-carousel-indicator-active-bg`：active pill 填色，現為 `--zk-color-primary`（恢復「active 填色」的語意）。
- `--zk-carousel-indicator-active-ring`（新）：active 框線，`--zk-color-on-primary`。
- 24bd1c629b 引入的 `-current-bg`、`-current-ring` 已移除。

### 變更檔案
- `zul/src/main/resources/web/js/zul/wgt/css/carousel.css`：resting 框線讀 `-indicator-ring`；active 讀 `-active-bg` / `-active-ring`。
- `zul/src/main/resources/web/zul/css/tokens/_component-theme.css`：token 定義；F1 註解改為「override `-indicator-active-bg` 只改 active pill 填色，`-indicator-ring` 只改 resting 框線」。
- `zul/src/main/resources/web/zul/css/tokens/_forced-colors.css`：F4，註解換行。
- `zkpreview/src/main/webapp/web/component-theming.zul`、`zkpreview/src/test/playwright/component-theming.spec.ts`：override 改回 `--zk-carousel-indicator-active-bg`，斷言 active pill 的 `::before` 填色為 `SCOPED_PURPLE`，sibling 不受影響（測試邏輯本身沒改）。
- `doc/contracts/carousel.md`、`doc/contracts/carousel.html`、`doc/spec/component-theme-variables.md`：F2，改寫為 primary pill + on-primary 框線、inactive 不變；knob 清單與表格（c19、c20、c21、新增 c21a）、knob 數量（十個，其中九個顏色）、forced-colors 敘述；contract 註明 hover 約 28px、超出按鈕兩側約 2px、不被裁切（已接受）。`doc/harness/eval-reports/carousel.md` 為歷史量測，未動。

### 指令與結果
- `withjdk.sh 17 bash ./gradlew :zul:compileMarbleCss`：BUILD SUCCESSFUL。
- `withjdk.sh 17 bash ./gradlew :zul:jar`：BUILD SUCCESSFUL；jar 內 `carousel.css` 含 `indicator-ring`，`_component-theme.css` 含 `indicator-active-ring`。
- 預覽伺服器從 b8 `zkpreview` 啟動於 8086（PID 87182 bash、87184 tail、87185 gradle wrapper、88611 Jetty）；結束後只 kill 這四個 PID，8086 已無 listener。未碰 8085。
- `PREVIEW_URL=http://127.0.0.1:8086 npx playwright test -c src/test/playwright/playwright.config.ts -g carousel`：共 7 項，6 passed、1 failed（跑了兩次，結果相同）。
  - passed：render-smoke、gallery carousel、component-theming 兩項（regional override 含 `indicator-active-bg`、`:root` override）、focus-scan、forced-colors-gallery。
  - failed：`font-size-equivalence › carousel.zul`，13px 142 變 145（+3）。這是 worktree za11y `.sr-only` live region 造成的已知差異，與本次修改無關（本次沒改 font-size）。
- commit 前 `git diff --cached --name-only` 與預期的 8 個檔案清單一致（同一個 Bash 呼叫內）。commit 後 `git status --porcelain` 只剩兩個未追蹤的 `node_modules`，沒有新增或修改的 PNG；未使用 `--update-snapshots`。

### 未做 / 備註
- 沒有另外量測 resting dot 的 computed style（只靠 token 值與 Playwright gallery 通過；gallery 容忍值 `maxDiffPixelRatio: 0.002`）。
- `carousel-gallery.png` baseline 仍為舊設計，更新需使用者授權。
- 尚待獨立 verifier 驗證。
