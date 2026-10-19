# 在 Jenkins 上用 hawkchen 的 fork 跑 zktest：可行性

日期：2026-10-19。只讀了 `ZK-Gradle-ZATS-Test` 的 `config.xml` 與 job API，沒有修改任何設定。

## 現有 job 的做法

- Pipeline job，腳本直接寫在 job 設定裡（不是 Jenkinsfile）。
- 參數：`ZK_BRANCH`、`ZKCML_BRANCH`（`pr/1234` 代表 PR #1234）、`RUN_ENV`、`RUN_BY_TAGS`、`RUN_BY_CASE_ONLY`（只跑指定測試，例：`B96_ZK_4748Test`）、`ZK_XML`、`TEST_ONLY`、`JACOCO_REPORT`、`JDK`。
- checkout 的 repo 寫死：
  - zk：`https://github.com/zkoss/zk.git`，不帶 credential。
  - zkcml：`git@github.com:zkoss/zkcml.git`，credential `github-zkoss-key`。
- 參數是 `pr/N` 時，refspec 改成 `+refs/pull/*/head:refs/remotes/origin/pr/*`。GitHub 會把「任何 fork 對 zkoss 開的 PR」放在 zkoss repo 的 `refs/pull/N/head`，所以這個 job 已經能跑 fork 的程式碼。
  - #1117 就是這樣：zk PR #3624、zkcml PR #1384 都來自 `yuehfeng` 的 fork，分支 `ZK-6112`。
- 其他會被複製過去的行為：
  - `pollSCM('H 8-19 * * 1-5')`：平日 8–19 點輪詢。腳本自己用 `properties([...])` 設定，所以只改 job 設定頁不夠，腳本也要改。
  - 失敗時 `emailext` 寄給 Culprits 與 Requester。
  - `DisableConcurrentBuilds` 只限制同一個 job；複製出來的 job 會和原 job 同時佔用 agent。

## 權限量測

- `GET /view/all/newJob`、`GET /job/ZK-Gradle-ZATS-Test/configure` 都回 200。這表示 hawkchen 帳號看得到建立 job 與設定頁，但送出前無法確認是否真的有 Job/Create 權限。
- `hawkchen/zkcml` 是 private fork。`github-zkoss-key` 的名稱看起來是 zkoss 組織的金鑰；如果它是 `zkoss/zkcml` 的 deploy key，就讀不到 `hawkchen/zkcml`。這點沒有驗證。

## 選項

- **A：不複製，從 fork 對 zkoss 開 draft PR，再用現有 job 跑 `pr/N`。**
  - 不必動 Jenkins，也不需要新的 credential。
  - 可以搭配 `RUN_BY_CASE_ONLY` 只跑部分測試。
  - 代價：zkoss/zk、zkoss/zkcml 會各多一個 draft PR，標題標明 do not merge。
- **B：複製 job，把兩個 URL 改成 hawkchen 的 fork。**
  - 要同時改腳本裡的 `pollSCM`（拿掉）與 `emailext`（只寄給自己）。
  - zkcml 需要一個讀得到 private fork 的 credential。若 `github-zkoss-key` 讀不到，就得把個人金鑰或 token 存進共用的 Jenkins，需要管理者同意。
  - 會和原 job 搶同一批 agent。
- **C：複製 job，把 repo URL 也做成參數。**
  - 和 B 相同的 credential 問題，但之後任何人的 fork 都能用。
  - 等於改一個共用工具，應該由 job 的維護者決定。

兩個前提都需要使用者先同意：

- 任何一個選項都要先把 `z1117` 的修改 commit 並 push 到 fork。
- 建立 job 或 PR 都是對外的動作。
