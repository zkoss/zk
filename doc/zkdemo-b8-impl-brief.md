# zkdemo B8 實作派工（工程師用）

> **2026-10-14 範圍變更（使用者裁示）：** ZK-6199（FileuploadDlg.ts 寫死外觀）與 ZK-6198 交給核心團隊，本 session 不改。工作 1 取消，`ZK-6199` 分支已刪除。工作 2 只做第 4、5、6 步裡的 **Z5 carousel**；第 1–3 步（merge ZK-6199、選檔列配色、對話框排版）等核心團隊的 ZK-6199／ZK-5053 進來後再做。不要用 `:has()` 之類的選擇器繞過。

日期：2026-10-14。決策來源：`doc/zkdemo-b8-triage.md`（D1 選項 A、D3 選項 C，使用者已同意）。
截圖與選項：Z3 https://claude.ai/artifact/HQd7mCKjqEbFvc5je7WVun 、Z5 https://claude.ai/artifact/JGbLafLi7M7WzpajFbNxNA 。

依專案規則：兩個主題都有的 bug 要從 `master` 切獨立分支修（含 IceBlue LESS 與 zktest），再 merge 進 marble，Marble 樣式另外以 `ZK-6112: … (ZK-XXXX)` commit。前例：ZK-6192（`doc/marble-master-sync-strategy.md` §D6-C）。

## 共同限制

- **不要動** `ZK10/zk`、`ZK10/zkcml` 這兩個主工作樹（其他 session 在用），也不要碰 8085。
- 不要 `publishToMavenLocal`（`~/.m2` 是共用的）。zktest 的跑法照 ZK-6192 在 `ZK10/zk6192/zk` 的做法。
- 只 commit，不 push。commit 前 `git diff --cached --name-only` 和預期清單比對；`git add`、比對、`git commit` 在同一個 Bash 呼叫完成。
- JDK：`withjdk.sh 17 …`。worktree 的建法見 memory「Preview from a fresh worktree」：`git worktree add --detach`、symlink `node_modules`、`bash ./gradlew`、先建 jar 再 `appRun`。
- 新規則（`CLAUDE.md` 主題段第一條）：widget 的 TS 不寫死外觀 inline style；外觀放 CSS class，只有動態計算的值留在 JS。

## 工作 1：ZK-6199（分支 `ZK-6199`，worktree `ZK10/zk6199/zk`，從 `65c55120a2` 切出）

`65c55120a2` 是已經 merge 進 marble 的最新 master 快照，從這裡切出，merge 回 marble 時才不會帶進其他 master commit。

1. `zul/src/main/resources/web/js/zul/fud/FileuploadDlg.ts`（`ModalFileViewer` 建構子，約 63-68 行）：把列、進度圖、Cancel 連結、檔案大小列的 inline style 全部換成 class。命名用對話框的前綴，例如 `z-fileupload-file`、`z-fileupload-file-progress`、`z-fileupload-file-cancel`、`z-fileupload-file-size`；最後的名稱由你決定，並寫進 commit 訊息。保留既有的 `id`，因為 TS 其他地方會用 id 查找。先 grep 是否有程式讀取這些 inline 值（`getComputedStyle(`、`.style[`、`.style.`）。
2. IceBlue LESS：把原本的值（`#F4F8FF`、`1px solid #99AABD`、`arial,sans-serif` 11px、`#0F3B82`、Cancel 的 `#0366AC` bold 與 float、大小列的 `padding-left:24px`、進度圖 float）原樣搬到一個一定會載入的 IceBlue less 檔（例如 window 相關的 less），讓 IceBlue 外觀完全不變。檔案選擇要寫明理由。
3. ZK-5053：`zul/src/main/resources/web/zul/html/fileuploaddlg.zul` 給對話框、說明列、按鈕列加 sclass（例如 `z-fileupload-dialog`、`…-prompt`、`…-buttons`），讓主題可以排版。IceBlue 不需要為這些 class 加樣式。這部分另開一個 `ZK-5053:` commit。
4. zktest：`B110_ZK_6199Test` 加上 `zktest/src/main/webapp/test2/B110-ZK-6199.zul`，並登記到 `config.properties`（該檔是 CRLF 行尾）。測試要開啟對話框、用真實的檔案選取上傳一個小檔，然後斷言：
   - 已選檔列帶有新的 class，而且沒有 `style` 屬性含 `background` 或 `color`；
   - IceBlue 下的 computed background 仍是 rgb(244, 248, 255)。
5. lint、type-check、checkstyle，然後跑 `B110_ZK_6199Test`。

## 工作 2：marble 端（branch `zkdemo-b8`，worktree `ZK10/b8/{zk,zkcml}`，從目前的 `marble` HEAD 切出；預覽站用 8086）

1. merge `ZK-6199`（前例：merge ZK-6192 進 marble-sync）。
2. `ZK-6112: style the fileupload dialog's picked-file row with Marble tokens (ZK-6199)`：Marble CSS 給工作 1 的 class 上色。值照 Z3 Artifact 的選項 A：`surface-container-low` 底色、`outline-variant` 框線、`shape-corner-small` 圓角、`typescale-body-small` 字級、`on-surface` 字色，Cancel 連結用 `primary`，間距用 `spacing-2`。全部用 `--zk-` token，不寫死顏色。放在一定會載入的 Marble 檔（例如 `zul/wnd/css/window.css`，或 `messagebox.css` 的對應位置），先讀 `.claude/skills/marble-theme/SKILL.md`。
3. `ZK-6112: lay out the fileupload dialog like the messagebox (ZK-5053)`：說明文字與 Browse 之間間距 `spacing-2`、空間不夠時換行；按鈕列靠右、間距 `spacing-2`、按鈕 `min-width: 64px`（同 `messagebox.css` 的 `.z-messagebox-buttons`）。兩顆按鈕維持實心（和 Messagebox 一致，見 ZK-6187）。
4. `ZK-6112: draw the active carousel indicator as a primary pill so it reads on light slides (zkdemo B8 Z5)`：`zul/src/main/resources/web/js/zul/wgt/css/carousel.css` 的 `.z-carousel-indicator-active::before` 改成寬 22px、圓角 4px（或 `shape-corner-full`），填色 `primary`、框線 `on-primary`；token 放在 `tokens/_component-theme.css` 的 carousel 區（約 1202 行）。`_forced-colors.css:390-391` 的規則要確認在膠囊形下仍然正確。更新檔案頂端 resting/active 互換的註解，使其和新設計一致。
5. Playwright：先跑 carousel 相關基準，用 `--update-snapshots=changed` 更新，並用 `git status --porcelain` 確認只有 carousel 的 PNG 變動。fileupload 若有基準也一併處理。規則見 CLAUDE.md 的 Playwright 段。
6. 在 8086 上用 Z3、Z5 Artifact 的同一組情境重拍，存到 `doc/zkdemo-b8-z3/`、`doc/zkdemo-b8-z5/`（現況檔名照 Artifact）。

## 回報

寫在 `doc/zkdemo-b8-impl-report.md`：每個 commit 的 hash 與訊息、lint／type-check／checkstyle／zktest／Playwright 的實際輸出摘要（失敗就照實寫）、新截圖的路徑，以及你自己無法驗證的部分。不要宣稱已經驗證；驗證由另一個 agent 負責。
