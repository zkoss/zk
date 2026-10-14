# Jess review 現行工作規則

更新日期：2026-10-11。適用：ZK Marble 主題的 Jess 設計審查（看板見 [status.md](status.md)，裁示見 [decisions.md](decisions.md)）。只列仍有效的規則。

## 1. 原則

- 外觀判斷的通用原則已移到 `.claude/skills/marble-theme/reference/decision-principles.md`（2026-10-14，D18-A）：誰負責修（Marble、ZK Jira、預覽頁）、MD3／MUI／ZK API／IceBlue 誰優先、同族基準表、何時才問使用者。原本此處的「兩條基本原則」（盡量遵循 MD3、框架內設計彼此一致）與「同族基準」都在該檔；新元件的結論補進該檔 §3，不補在此處。
- 以下只列 Jess review 專屬的規則：
- 做不到時用事先寫明的退路：不改、在 contract 註明差異、回覆設計師。貼出前須使用者同意。
- 目標與既有 contract 衝突時，以原則為準更新 contract，並在 diff 與看板記錄。要退掉 contract 中一整條規格，列為例外。
- 色彩對比達不到下限時，要在報告中標出。
- 已知既有失敗（`calendar-tablet`、`slider-tablet`、`grid-header-gallery`）不在本批處理，記進 follow-up。

## 2. 每批的流程

每批 3–6 個 issue，以同一個 CSS 檔或同一族元件為單位。

1. **選批（Planner）：** 以 `git status` 確認工作樹沒有他人未提交的相關檔案；不從 `git status` 推論檔案歸屬。
2. **寫方法（Planner）：** 把方法寫進該批的 `gates/batchN-red.md`。每個 issue 有「判定」（今天必須失敗）與「保護項」（今天必須通過）；量法只看像素、命中測試與 computed style，不依賴特定 CSS 寫法。
3. **RED run（Verifier）：** 在現有程式碼上量。判定今天就通過、或量不到，代表方法有缺陷：Planner 修方法後定稿，之後不再改。
4. **Generator：** 依 brief 修 CSS，只動 brief 列出的檔案。不開瀏覽器、不 build、不 commit。做不到純 CSS 時停下回報。
5. **範圍檢查（Planner）：** diff 只落在預期檔案，沒有 `!important`，沒有寫死色值。
6. **最終判定（Verifier，不看 diff）：** 全部判定與保護項，加上回歸。結論行 `GATEn-FINAL: PASS/FAIL`。Generator 最多 3 輪；3 輪仍未通過即停下回報。
7. **baseline：** 只用 `--update-snapshots=changed`，只重生預期變動的檔案，逐張確認是預期的。例外：gallery 通過但像素有變時，只針對該元件用 `--update-snapshots=all`。
8. **提交：** 見第 5 節。
9. **證據與看板：** 證據放 `gates/batchN-*`，報告首行宣告使用中的預覽站。更新看板的計數與狀態列，計數以追蹤 repo 重算。
10. **留言：** 每個 issue 一則，結構為 `# Root cause`、`# Solution`、`# Result`（附修改後截圖）。截圖放 `hawkchen/marble-issue` 的 `screenshots/batchN/`。貼出或改寫留言由使用者決定（見第 4 節）。
11. **回報：** 結果簡短：一句話結論、前後數字、例外與發現、需要的決策、下一批候選。只有在有例外或需要決策時才開議題頁（Artifact）。

**回歸範圍：**
- 每批：該批元件的 gallery 與互動腳本、`component-theming`、`hit-target`、`focus-scan`；動到顏色或邊框時加 `forced-colors`。
- 完整回歸（chromium、tablet、gallery 等全項）在合併到 `marble` 後跑一次，不在多條線同時跑。
- 改共用子元件的樣式，回歸要涵蓋所有用到它的頁面；只用元件名稱做 `-g` 過濾會漏。
- 不跑 `forced-colors-gallery` 專案：它不比對，會覆寫 repo 內的 `*-forced-colors.png`。
- Verifier 的 Playwright `--output` 指向 scratchpad 或 gates 目錄，不寫進共用的 `test-results`。

## 3. 角色與模型

- **Planner：** 主 session。寫方法、裁定方法歧義（看到結果後才裁，並寫明理由與反面讀法）、範圍檢查、提交、留言、回報。
- **Generator：** Sonnet subagent。只改 CSS，不量測。
- **Verifier：** 獨立的 Opus 5.5 subagent，看不到 CSS diff；Fable 為替代（Opus 無法使用時）。Sonnet 只用於不下判斷的截圖工作。（D124）
- 非技術文件工作：Haiku 5.5（使用者 2026-10-10 規定）。

## 4. 可以直接做、不必問；必須停下來問

**可直接做**（以下全部成立時，從選批做到 baseline、提交、證據與看板，最後一次回報）：
- issue 屬於 P1 元件類，且 MD3 與框架內一致性不衝突；
- 純 CSS 可解決，或能清楚切出純 CSS 的那一半；
- 只動 `zul`／`zkcml` 的主題 CSS、預覽頁與 baseline；
- 回歸沒有無法歸因的新失敗。

**必須停下來問**（附上標示「建議」的選項與代價，使用者一句話即可裁示）：
1. MD3 與框架內一致性互相衝突，或兩個 issue 的要求互相矛盾。
2. DECIDE 類（本質是設計決策，不是修補）。先查 widget 與預設值，把事實與工程量並列再列選項。
3. 需要改 widget TypeScript／Java、公開 Java API、`zul.xsd`，或影響 `../zkcml/` 的公開介面。
4. 要退掉 contract 中一整條規格，或改變元件預設外觀超出 issue 所述範圍。
5. 要新增、改名或刪除公開的 `--zk-` token（影響使用者自訂）。
6. 設計描述含糊，最小解讀經 RED run 重現後仍無法確定。
7. Generator 3 輪仍未通過，或原因出在 ZK 本身而非樣式。
8. 回歸出現無法歸因的失敗，或與其他 session 未提交的變更重疊。
9. 範圍擴大：需要動超過 issue 所屬元件的檔案。
10. 對外動作：推送、關閉 issue、建立 ZK Jira、貼出或改寫 Jess 留言。
11. 需要動共用檔案：`tokens/` 下任何檔案、`_forced-colors.css`、`lang.xml`／`lang-addon.xml`、`zkpreview/doc/font-size-baseline.json`、`playwright.config.ts`、任何 `*.spec.ts`。
12. rebase 或 merge 出現程式碼衝突，或同一個檔案有別人未提交的修改。

## 5. 提交與合併

- 逐路徑暫存（`git add <path>`），不用 `git add -A`；暫存前後各查一次 `git diff --cached --name-only`。
- 分支 `marble`。zkcml 先提交，再提交 zk。
- 訊息格式 `ZK-6112: …`，英文，祈使語氣。
- 同一個檔案同一時間只屬於一條工作。提交者身分都是同一個 git 帳號，`git log` 無法區分 session；歸因失敗要用提交內容與檔案日期。
- 合併前查交集：`git diff --name-only A...B` 取交集，並以 `git merge-tree --write-tree` 試算；有重疊停下問。
- 合併只用 `git merge --ff-only`。不 force push，不改寫已共用的歷史。推送由使用者決定。
- 元件 baseline PNG 衝突：不手動合併，以該元件重生。`font-size-baseline.json` 同理，合併後由持有者重生並在提交訊息說明。
- 結束 session 前先提交並更新看板；看板是交接文件，重要事項不能只留在對話中。

## 6. 做法備忘

### 預覽站（8085，共用）

- 量測前比對 served CSS（`zk.wcs`）含新規則，確認伺服的是新 build。
- Verifier 不自行重啟；遇到 500 停下回報。8085 的 jar 可能在執行中被重建，症狀是 `NoClassDefFoundError` 或 500。
- 重啟：先停 8085，再打任何 jar（打 jar 時 8085 不得運行）。打完後在 `zkpreview` 執行 `tail -f /dev/null | withjdk.sh 17 bash ./gradlew appRun -PhttpPort=8085 --console=plain`（stdin 要保持開著）。等 jar 的 mtime 穩定、四個頁面回 200，才交給 Verifier。
- CSS 重建：`node scripts/build-css.js --module zul`，再 `withjdk.sh 17 bash ./gradlew :zul:jar -x test --console=plain`。zkcml 用 `node ../zk/scripts/build-css.js --module zkmax`，再 `:zkmax:jar`。
- `zkpreview/gradlew` 在 git 中不可執行時用 `bash ./gradlew`，不要 chmod（會讓 git 狀態變髒）。
- build 要排隊：Generator 只改原始碼；Verifier 量完後才 build 與重啟。

### 量測（RED 與最終判定）

- 與捲軸有關的版面要用 `ignoreDefaultArgs:['--hide-scrollbars']`；預設會讓捲軸欄位寬度為 0。
- gallery 容差為 0.2%（原為 1%，已收緊）。收緊前先分類過期 baseline：元件 crop 平移後相同可整批重切，其餘逐頁歸因。
- 非決定性先在乾淨環境（預設 Desktop Chrome context）重現。等待條件用可量的狀態，例如 progressmeter 的 fill 寬度等於 `aria-valuenow`。
- 判定量渲染後的像素，不只量 computed 值。半透明色疊兩層時，改不透明色加 svg 層級的 `opacity`。半透明 token（`rgba`）的 ΔE 先疊在實際背景上再比。
- 唯讀 `<input>` 不理會 `user-select`；游標在截圖中看不到，用註記標籤並在留言說明。
- 尺寸判定比 ink 外框的寬與高各 ±25%，不比面積；對齊判定量 ink 中心。
- 宣稱「純 CSS 做不到」前，先查祖先與兄弟的 class／狀態，並試 `:has()` 搭配 `:active`、`:hover`、`:focus-within`。
- `mask-image` 加 `background-color` 的圖示，改動後要檢查 forced-colors：background 會被強制成 Canvas 而消失，補 `CanvasText`。

### 測試與 token

- 測試要驗證效果（例：ellipsis 實際被畫出、命中、像素），不只驗 computed 值。
- 新測試必須證明不會空轉：注入壞狀態後必須失敗。
- mutation 探針可以用 `!important`；正式測試不行。
- 公開 token 保留旋鈕，只改預設值。保護項為「注入該 token 後仍生效」。不在規則內用 `color-mix` 混色，否則使用者設的色不再原樣呈現。
- 寫 selector 前先查 ZK 為該屬性值加了哪些 class。minifier 會合併 selector list，驗收看 served CSS，不看原始碼。

### 其他

- 看板數字與剩餘清單回到原始碼與追蹤 repo 重算，不信舊數字。
- widget 根因先在預覽頁用 inline 狀態重現，實測 workaround，再寫進 ZK Jira。
- 檢查 GitHub 檔案是否存在看 HTTP 狀態碼：`--jq .sha` 在 404 時輸出 `null`，不是空字串。
- 批次文字替換前先用 `git diff -U0` 確認範圍。
