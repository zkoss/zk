# zkpreview 執行緒數隨 Playwright 執行次數累積

對應 `tasks/marble-test-gaps.md` #10 與 `doc/marble-test-phase1-verification-2.md`。本文件只記錄量測到的事實，不含修正。

## 結論（量測值）

- 增加的執行緒是 `java.util.Timer` 的 `TimerThread`（名稱 `Timer-N`）。
- 每建立一個新的 HTTP session，就多一個 `Timer-N`。同一個 session 內重複載入頁面不增加。
- 建立處：`zk/src/main/java/org/zkoss/zk/ui/impl/SimpleDesktopCache.java:77`（`_cleaner = new Timer();`），屬 ZK core（zk 模組）。
- 這些執行緒隨 HTTP session 失效而釋放。本次量測中，242 個 `Timer-N` 在建立後約 36 分鐘於同一個 2 分鐘取樣區間內全部消失（見「釋放」）。
- 在 zk、zkcml、zkpreview 的 `src/main/java` 中，只有這一處建構 `java.util.Timer`（zktest 的 `F86_ZK_4256Composer.java:45` 另有一處，不在 zkpreview 的 classpath）。

## 環境

- worktree：`/Users/hawk/Documents/workspace/ZK10/testnet/zk`（HEAD `cc8c78572d`），伺服器從 `testnet/zk/zkpreview` 以 `appRun -PhttpPort=8086` 啟動，JDK 17。
- Jetty JVM PID 61871（cwd 為 `testnet/zk/zkpreview`，已確認）。啟動的 PID：tail 61048、gradle wrapper 61049、Jetty 61871。
- `zkpreview/src/main/webapp/WEB-INF/web.xml` 沒有 `session-config`；`zk.xml` 沒有設定 desktop 或 session timeout。
- 量測腳本與每次的 `jcmd Thread.print` 輸出：`/Users/hawk/Documents/workspace/ZK10/testnet/out/ns10/`（`count.sh`、`dump-*.txt`、`decay.log`）。

## 重現步驟與數字

執行緒數用 `jcmd <pid> Thread.print` 以名稱前綴計數。

| 步驟 | 總執行緒 | `Timer-` |
|---|---|---|
| 剛啟動 | 41 | 0 |
| `curl /smoke.zul` 5 次，每次不帶 cookie | 48 | 5 |
| `curl /smoke.zul` 5 次，共用同一個 cookie jar | 49 | 6 |
| `--project=smoke` 第 1 次（118 個測試，118 passed） | 175 | 124 |
| `--project=smoke` 第 2 次（118 passed） | 293 | 242 |

- 每次 smoke 增加 118 個 `Timer-N`，等於該 project 的測試數（每個測試開一個新 browser context、載入一頁）。
- 只有 `Timer-` 前綴隨次數成長；其餘（`qtp`、GC 等）在 smoke 後為 18 個 `qtp`，之後閒置回到 baseline 水準。
- 一個 `Timer-3` 的 stack：`java.util.TimerThread.mainLoop` → `Object.wait`，wait 在 `java.util.TaskQueue`。thread dump 看不到建立者，建立者由下列證據歸屬。

### 歸屬證據

1. 5 個新 session 對應 +5，同一個 session 5 次請求對應 +1（上表第 2、3 列）。
2. `SessionDesktopCacheProvider.getDesktopCache`（`zk/src/main/java/org/zkoss/zk/ui/impl/SessionDesktopCacheProvider.java:61`）在 session 第一次取 desktop cache 時 `new SimpleDesktopCache(...)`。
3. `SimpleDesktopCache` 建構子（`SimpleDesktopCache.java:64-71`）在 `config.getDesktopMaxInactiveInterval() >= 0` 時呼叫 `startCleaner`，`startCleaner`（`:73-79`）執行 `new Timer()`。預設值 `_dtTimeout = 3600`（`zk/src/main/java/org/zkoss/zk/ui/util/Configuration.java:149`），所以預設即建立。這段由 ZK-5435 加入；`stopCleaner` 與 `shutdownCleaner` 由 ZK-6102（commit `195983188e`）加入。
4. 與整套測試量的對應：`npx playwright test --list` 共 1,345 個測試。若每個測試一個新 session，3 次約 4,035 個 session，與 gaps #10 的 4,097 個執行緒同一量級。這是推算值；本次沒有跑全套。

## 釋放

- 取樣每 2 分鐘一次（`decay.log`）。最後一次 smoke 結束於 23:19:35。
- 23:21 到 23:53 的 17 次取樣，`Timer-` 皆為 242，總執行緒從 291 降到 283（`qtp` 閒置回收）。
- 23:53:41 取樣：總 283、`Timer-` 242。23:55:41 取樣：總 41、`Timer-` 0。242 個在同一個 2 分鐘區間內全部消失。
- 第一批 session 約在 23:15 到 23:17 建立，最後一批約在 23:19 結束；消失發生在最後一批結束後約 36 分鐘。Jetty 的 session timeout 沒有在專案內設定；本次沒有讀取 Jetty 實際的 timeout 與 scavenge 間隔，因此 30 分鐘加掃描週期只是與觀察相符的推測，未驗證。
- 釋放路徑（讀自原始碼）：session 失效時 `SessionDesktopCacheProvider.sessionDestroyed`（`:81-92`）呼叫 `dc.stop()`，`SimpleDesktopCache.stop()` 的 finally 區塊執行 `stopCleaner()`（`SimpleDesktopCache.java:260`）取消 timer。web app 停止時 `SessionDesktopCacheProvider.stop`（`:120` 起）對仍存活的 cache 呼叫 `shutdownCleaner()`。
- 頁面關閉或 browser context 關閉不會使 server 端 session 失效，所以一次測試結束後其 `Timer-N` 仍留在 JVM 裡直到 session timeout。

## 擁有者

| 項目 | 結果 |
|---|---|
| 建立 Timer 的程式 | ZK core：`zk/src/main/java/org/zkoss/zk/ui/impl/SimpleDesktopCache.java:77` |
| 觸發條件 | 預設設定（`desktopMaxInactiveInterval` 預設 3600）下，每個新 HTTP session 一個 |
| zkcml | grep `new Timer(` / `java.util.Timer` 在 `zkcml/*/src/main/java` 無結果 |
| zkpreview app | `zkpreview/src/main/java` 無 `new Timer(`；沒有覆寫 desktop cache provider 或 timeout |
| 特定頁面 | 無。smoke 的每一頁（含沒有 Timer 元件的頁面）各產生一個；`/smoke.zul` 的 curl 也產生。成長量以 session 計，與頁面內容無關 |
| 測試 | Playwright 每個測試用新 context，因此每個測試一個 session |

第 4 項（是否 ZK 預設行為）：本次量測的就是 ZK 預設值，`zk.xml` 沒有任何覆寫，所以這是 ZK core 的預設行為，不是 zkpreview demo class 或某個元件頁面造成。

## 未回答的問題

1. Jetty 實際 session timeout 與 scavenge 間隔未讀取（只觀察到約 36 分鐘後釋放）。
2. 全套測試的實際 session 數沒有量測（只有 smoke 的 118 與 `--list` 的 1,345 推算）。
3. 沒有把 `desktopMaxInactiveInterval` 設為負值重跑，所以「設成負值是否不再建立 Timer」只讀自 `SimpleDesktopCache.java:68-70` 的條件，未實測。
4. 沒有在 8085 的共用伺服器上量測；本文數字只來自 8086 的單一 JVM。
5. 還沒量測同一 JVM 的原生執行緒上限（4,097 這個數字來自 gaps #10，本次沒有跑到 OOM）。

## 附註

- 本次 Playwright 只跑 `smoke`，沒有改動 testnet 追蹤檔；`git status --porcelain` 與開始前相同（14 行）。
- 已停止自己啟動的伺服器：kill 61048（stdin holder）後 61049、61871 結束，8086 已釋放。

## 緩解（2026-10-19）

決議 D17 = A：只改 preview，不動 ZK core。

### Diff（在 `testnet/zk` worktree，未 commit）

`zkpreview/src/main/webapp/WEB-INF/web.xml`

```diff
@@ -80,6 +80,11 @@
 		<url-pattern>/zkres/*</url-pattern>
 	</servlet-mapping>
 
+	<!-- Every Playwright test opens a session and ZK keeps one Timer thread per session; see doc/zkpreview-thread-leak.md -->
+	<session-config>
+		<session-timeout>5</session-timeout>
+	</session-config>
+
 	<welcome-file-list>
```

`.claude/skills/marble-theme/reference/verification.md`

```diff
@@ -28,6 +28,11 @@
 in an interactive terminal, or hold a FIFO open from a script. The first start builds the composite
 build (minutes); a warm start serves in about 10 s.
 
+Every Playwright test opens a new HTTP session, and ZK keeps one `Timer` thread per session until
+the session expires, so a full suite leaves about 1,300 threads behind. The preview app therefore
+sets a 5-minute session timeout in its `web.xml`. If you still run several full suites back to back,
+restart the server between them. Background: [`doc/zkpreview-thread-leak.md`](../../../../doc/zkpreview-thread-leak.md).
+
 Stop by pressing a key in the `appRun` terminal (closing stdin has the same effect).
```

### 為什麼放 web.xml

- 先確認 zkpreview 內沒有任何既有的 session timeout 設定（`web.xml`、`zk.xml`、gradle／gretty 設定、Java、zul、properties 皆無 `session-timeout`／`session-config`／`setMaxInactiveInterval`）。
- Session timeout 是 servlet container 的設定，標準位置就是 `web.xml` 的 `<session-config>`。ZK 的 `zk.xml` `<session-config>` 只管 `timeout-uri`、`max-desktops-per-session` 等 ZK 層設定，不控制 HttpSession 存活時間，放那裡不會生效。
- gretty 啟動的 Jetty 讀 `web.xml`，且 `build/inplaceWebapp/WEB-INF/web.xml` 第 85 行已含 `<session-timeout>5</session-timeout>`，確認伺服器吃到變更。

### 量測

環境：`testnet/zk/zkpreview`，port 8086，JDK 17，`appRun`。測試：`PREVIEW_URL=http://127.0.0.1:8086 npx playwright test -c src/test/playwright/playwright.config.ts --project=smoke`，118 passed（1.7 分）。以 `jcmd <pid> Thread.print` 計 `"Timer-` 執行緒數。

| 時間 | Timer- 執行緒 | 說明 |
|---|---|---|
| 10:12:35 | 1 | 伺服器啟動後基準 |
| 10:14:29 | 119 | smoke 結束（t0） |
| 10:15:32 – 10:23:33（每分鐘） | 119 | 全程未變 |
| 10:24:33 | 0 | 已全部釋放 |

- 釋放發生在 t0 之後 9 分 04 秒到 10 分 04 秒之間（所有執行緒一次釋放，非逐步）。
- 對照：未設定時約 30 分鐘才釋放。
- 判讀：5 分鐘 timeout 之外，還要等 Jetty 的 session scavenge 週期，實測釋放時間約 10 分鐘。我沒有去讀 scavenge 間隔的設定值，所以「5 分 + scavenge 間隔」這個預期只以實測時間為證，沒有逐項拆解。
- 注意：smoke 只開 118 個 session（119 個 Timer 含 1 個基準），完整 suite 約 1,345 個；本次未跑完整 suite。

### PID 與清理

- 我啟動的程序：`tail -f /dev/null`（22934，stdin holder）、gradlew client（22935）、Jetty（23751）；三者 cwd 皆在 `testnet/zk/zkpreview`（以 `lsof -a -p <pid> -d cwd` 確認）。
- 只 kill 22934，22935 與 23751 隨之結束；8086 已釋放。未動 8085、8105 與其他 session 的程序，未用 `pkill`／`killall`，未停 Gradle daemon。
- `git status --porcelain` 與開始前相同（原有 10 張 PNG 修改保持原樣），僅新增上述兩個檔案的修改；未 `--update-snapshots`，未 commit。
