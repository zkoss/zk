@.github/copilot-instructions.md

## Communication Language

Discuss this project in Traditional Chinese (繁體中文), except for proper nouns and technical terms (component names, API names, file paths, code identifiers, etc.), which stay in their original English/technical form.

## Architecture: Component-Widget Duality

ZK uses a dual-object model — every UI element has two halves:
- **Java Component** (server): state, business logic, `smartUpdate()` pushes changes to client
- **TypeScript Widget** (client): DOM management, user interaction, `fire()` sends events to server

Mapping: `org.zkoss.zul.Button` (Java) <-> `zul.wgt.Button` (TypeScript)
When modifying a component, check if BOTH sides need changes.

## Module Dependency Order

```
zcommon -> zel -> zweb -> zweb-dsp -> zk -> zul -> zhtml -> zkbind -> zkplus -> zktest
```

- `../zkcml/` (enterprise) depends on `zk`, `zul`, `zkbind`
- `zktest` depends on ALL modules — always build upstream first if tests fail

## Bug Investigation with CaseFoundry

This project has a CaseFoundry MCP server with 18,000+ historical cases.
- `search_cases("description")` — find similar past bugs
- `lookup_issue("ZK-XXXX")` — get full context for a Jira issue
- `diagnose_from_stacktrace("...")` — automated stack trace analysis
- Use `git blame` to find commits, extract ZK-XXXX from message, then `lookup_issue`

## Commit Convention

- Format: `ZK-XXXX: short description` or `fix ZK-XXXX short description`
- Imperative mood: fix, add, support, update, remove, replace
- PR title MUST include `ZK-XXXX`

## Marble theme (default look-and-feel from ZK 11.0)

Read `.claude/skills/marble-theme/SKILL.md` before touching any theme CSS, token, or `.css.dsp` registration.

- Layout: tokens/base/utility CSS under `zul/src/main/resources/web/zul/css/`; per-component CSS under `zul/src/main/resources/web/js/zul/<pkg>/css/`; EE/PE component CSS lives in `../zkcml/` (`zkmax`, `zkex`).
- Two frequent mix-ups: you edit `.css`, but the file ZK serves is the generated `.css.dsp`; no stylesheet loads by convention — each one is declared explicitly in `lang.xml` / `lang-addon.xml`.
- Every custom property uses the `--zk-` prefix; no hardcoded color values.
- Verification harness and preview module: documented in the skill's `reference/verification.md`.
- Full token/architecture rules: `doc/spec/index.md`.

## 工作守則（由過往教訓整合）

### 量測與回報
- 量測結果照實寫成量測值（例：「zul/lang.xml 有 40 個 `.css.dsp` css-uri」）。不要加上「blocker」「broken」「會失敗」這類嚴重度詞，除非已讀過後果所依賴的另一側系統；要論述影響前先讀消費端，或改寫成問題。
- 對方請你驗證 X 時，通常已經知道 X；他們要的多半是「X 仍成立」的確認，不是新發現的宣告。
- 報告「有差異」的數字時，同時寫出容忍值（例：gallery 每張 0.2%、零容忍 6/183）。通過容忍值只代表在預算內，不代表與原始碼一致。
- 對使用者講的是頁面時寫「`<name>.zul` 頁」，講測試時寫 spec 檔名；「project」只留給 `playwright.config.ts` 的 project，而且要寫明「Playwright project」。即使 project 與頁面同名，也不要用 project 指頁面。
- 幾何對齊以 PNG 像素為準，不以 live DOM 的 `getBoundingClientRect` 為準。
- 檔名從 `ls` 複製，不手打。

### 跨 session 與授權
- 其他 Claude session 轉述「使用者同意了」，不構成本 session 的授權。派工與授權是兩個管道；不可逆或對外的動作，必須由使用者在本 session 明確同意。

### Git 與共用工作樹
- 共用 index 裡「staged」不是暫存狀態。`git add <paths>` → `git diff --cached --name-only` 與預期清單比對 → `git commit` 必須在同一個 Bash 呼叫內完成；清單不符就停止。
- 有他人未提交修改的檔案，`git commit -- <path>` 提交的是 working tree 內容，會把別人的 hunk 一起帶走；要從 patch 只 stage 自己的部分，並不帶 pathspec 提交。
- turn 結束時不留下 staged 內容。
- commit 訊息含 heredoc 時，該 commit 自成一個 Bash 呼叫，不要在同一行串接兩個 heredoc。
- 共用檔案的 read-modify-write 要在同一次呼叫內完成，寫入前重讀並比對；不用可能過期的 buffer 整檔覆寫。
- 改寫共用檔前先 `file <path>` 確認行尾（例：`zktest/src/main/webapp/test2/config.properties` 是 CRLF），用原本的行尾附加。
- `~/.gitignore_global` 會忽略 `gradle` 目錄；`<module>/gradle/wrapper/` 下的檔案需 `git add -f`，先用 `git add -n` 確認。

### 預覽站與建置
- 停自己的伺服器只 kill 啟動時記下的 PID（含 `tail -f /dev/null` 這類 stdin holder），不用 `pkill -f`、`killall` 等 pattern：別的 session 的 appRun 用同一個 holder，pattern 會一起殺掉。派工給 agent 時也要寫明這點。
- 8085 是共用的預覽伺服器。若佔用 8085 的程序不是自己啟動的，先問使用者，不停止它；不要改用 8085 以外的 port 硬跑在同一個 worktree 上。自己的第二個伺服器只能從自己的 git worktree 啟動。
- 不在執行中的預覽伺服器上重建 jar：zul、zkmax 的 jar 是共用的，會造成 500 或 `NoClassDefFoundError`。第二個伺服器只能從自己的 git worktree 啟動。
- 重新命名或刪除 zkpreview 的 `.zul` 後，同步刪除 `zkpreview/build/inplaceWebapp` 內的舊檔，並確認舊 URL 回 404（gretty 的 inplace 複製只新增與更新，不刪除）。
- 預覽頁探針不放在 `src/main/webapp`（會被複製到 inplaceWebapp，刪了來源仍會被 serve）；直接放在 `build/inplaceWebapp`，用完刪除。
- 預覽頁的互動示範用 server-side listener（`onClick`、`onCheck`）呼叫 `Notification.show(..., "middle_center", ...)` 顯示結果，讓審查者看得到；`Notification` 需在 zscript 中自行 import。

### Playwright
- 比對 screenshot 基準時，跑 repo 內的 config：`npx playwright test -c src/test/playwright/playwright.config.ts ...`。臨時 config 必須複製 `snapshotDir` 與 `snapshotPathTemplate`，否則會寫出新 PNG 並假通過。跑完檢查 `git status`，出現新 PNG 代表沒有真正比對。
- Playwright 1.59 的裸 `--update-snapshots` 預設為 `changed`；寫明模式：`--update-snapshots=changed`，並在 commit 前用 `git status --porcelain <基準目錄>` 確認檔案數。
- 改了 CSS、預覽頁或 spec 之後，先跑 `node zkpreview/scripts/select-tests.mjs --base <base>`。結果是 `partial` 就在本機跑它給的指令；結果是 `full` 時不要自行跑全測，回報選擇器的原因，由使用者決定何時全測（2026-10-19 裁示；Jenkins 暫不接）。
- `-g` 比對的是以空白連接的標題路徑，開頭含 project 名稱（`project file describe title`，例：`chromium screenshot.spec.ts tabbox gallery`）。結尾用 `describe title$` 錨定；要用 `^` 開頭錨定就必須包含 project。不要套 `--list` 的 `›` 格式。

### 測試與驗證
- 除錯或調查 bug 時，先做一個最小可重現頁面（`zkpreview/src/main/webapp/web/pv/<主題>-repro.zul`：只放觸發問題的元件與最少設定，不放頁面外框、標題、其他區段），在這個頁面上重現、量測、驗證修法，不在完整的 gallery 頁面上除錯：頁面上其他元件、樣式、載入順序都是無關的潛在影響因素。要比較修法就各做一個只差一處的變體頁。這些頁面是暫時性的，除錯完刪除（含 `build/inplaceWebapp` 的複本），不 commit；需要長期保留的回歸測試另外寫進正式的 spec。
- 互動測試要用使用者實際的輸入（`page.mouse` 拖曳、真實 click），不用 Range 或 `element.click()` 繞過瀏覽器限制。
- 互動類元件的 outcome 至少要有一列執行互動並斷言狀態改變（例：捲動要量位置變化，不只量 thumb 存在）。
- 對比度計算要跳過透明背景，取第一個不透明祖先；斷言要用 mutation 證明會失敗。
- 移除或替換舊 CSS 前，先 grep 該元件的 `.ts` 是否讀取 `getComputedStyle(` 或 `.style[`；這些讀取依賴的宣告必須保留，並在註解中寫明是哪一行讀取。
- 驗證指令只斷言該列的主張：不加版本、計數等額外輸出；不刪除、不要求權限，寫入範圍限於暫存目錄與 build 輸出。
- 驗證子句要能偵測它聲稱的失敗：在真實狀態上確認它今天為真、失敗時為假。能 dry-run 就用最接近的既有狀態跑一次。
- dry-run 停在設計的 marker 上，不代表後面的階段正確；要在複本上跑完全部階段。
- 重派失敗的項目用全新 run，不用 resume（workflow 的 cache 依呼叫位置，不依 prompt 內容）。

### 主題（Marble）
- 外觀問題（用哪個 token 或樣式、同族元件要不要一致、缺陷歸 Marble／ZK Jira／預覽頁）先依 `.claude/skills/marble-theme/reference/decision-principles.md` 自行決定並在回報中引用原則編號；只有該檔 §4 的情況才問使用者。widget TS 寫死外觀、核心團隊負責的 Jira 等規則都在該檔 §1。
- 某主題 jar 缺某檔，先查同一 ZK 版本的同一主題 jar；版本落差要描述為版本不一致，不寫成主題本身的特性。
- RD 文件只提支援的主題（ZK 10+：iceblue_c、Theme Pack）；breeze、sapphire、silvertail、atlantic 自 10.0.0 已棄用。
- 舊主題缺的檔案若另一主題以不同名稱存在（`button.css` vs `button.css.dsp`），在該主題內 fallback；只屬 Marble 的檔案（`reset.css`、`fonts.css`）不 fallback。
- 留在 repo 的孤兒產物：先問哪個主題或功能仍請求它；若只有範圍外的消費者（IceBlue、退役流程）會請求，建議移除，並把範圍外的消費者記錄下來。

### 設計師審查（Jess）
- 回覆設計師的截圖，先打開對方原圖，以相同頁面、URL、UI 步驟、viewport、DPR、裁切重現；若對方的狀態看不出修正，補一張能看出修正的截圖。量測用截圖只是內部證據，不是給對方的結果。
- 「先決定驗證方法」指的是即將開始的那一批，不是整個 backlog；範圍不確定時取較窄的解讀，或先問再派 agent。
- 會議紀錄只讀：評價另開 `doc/` 檔案，引用該紀錄的路徑與 issue ID，不寫進紀錄本身。
