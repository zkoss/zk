# zkdemo B8 回報分流（2026-10-14）

來源：zkdemo-30 規劃 P4 batch 8 時回報的 7 項（Z1–Z6、Z8；沒有 Z7）。截圖：https://claude.ai/artifact/GgEy8UHUs7YmLvvQv3bUEC 。
版本：zk 11.0.0.FL.20260930-Eval、marble 11.0.0.FL.20261013。Z3、Z4、Z5 已在 8085 重現；其餘項目依原始碼判定。

| 項目 | 判定 | jar 端動作 | 依據 |
|---|---|---|---|
| Z1 Toast `success` | 文件錯誤；任何主題都沒實作 | 無 | `zkmax/wgt/Toast.ts` `iconMap` 只有 warning/info/error；`zkmax/ui/util/Toast.java:58` javadoc 也只列這三種 |
| Z2 Toast position | 文件錯誤 | 無 | `Toast.java:99` 只接受 `(top\|middle\|bottom)_(left\|center\|right)`，javadoc 的 3×3 表正確 |
| Z3a 上傳對話框已選檔列 | 寫死的 inline 樣式，配色是照 IceBlue 調的；只在 Marble 下顯得突兀（Marble 沒有 dark mode，見 `doc/spec/design-decisions.md` §1） | 待決（ZK-6112） | `zul/fud/FileuploadDlg.ts:63-68`，自 ZK-5182 之前即存在 |
| Z3b 對話框排版（label 貼 Browse、按鈕間距 3px、靠左、兩顆都是 filled） | 和 IceBlue 10.3.1 相同（說明文字右緣 = Browse 左緣 294px；按鈕靠左、都是實心），不是 Marble 造成的；但和 Marble Messagebox（靠右、間距 8px）不一致。相關：ZK-5053（Open） | 待決 | `zul/html/fileuploaddlg.zul`；對話框 zclass 就是 `z-window`，沒有專屬 class 可掛 CSS |
| Z4 `Fileupload.get(null, …)` NPE | 核心 bug，與主題無關 | 依慣例開 Jira（需使用者同意） | `Fileupload.java:360` 直接 `params.put`；其他 overload 都自行傳 `new HashMap` |
| Z6 iframe `scrolling="yes"` | 文件錯誤；ZK 只把屬性原樣輸出，行為由瀏覽器決定 | 無 | `Iframe.java:67-73` |
| Z8 `<calendar value="2026-10-15"/>` 500 | 設計如此（非 regression） | 無 | `Classes.coerce` 對 `Date` 只接受 `Number`/`Date`，自 2006 年未變；marble 對 10.4 的差異只有 ZK-6167 的 module export 處理。ZUL 中請用 `value="${...}"` |
| Z5 Carousel 指示點 | 設計判斷；IceBlue（`master` `_default.less`）是同一組 fill／ring 互換配色，不是 Marble 造成的 | 待決 | `carousel.css` 的 resting／active 是 fill／ring 互換；淺色 slide 上 active 的淺色 fill 看起來像空心 |

## 決定（2026-10-14，使用者同意）

- D1（Z3）：**暫停**：使用者裁示 ZK-6199 由核心團隊修，Marble 端等 ZK-6199／ZK-5053 進來後再補配色與排版。原決定：選項 A。另外依使用者指示，JS 寫死外觀這件事開成 bug：[ZK-6199](https://zkoss.atlassian.net/browse/ZK-6199)，改用 CSS class（只有動態計算值留在 JS）。照 ZK-6192 的前例，`ZK-6199` 分支從 `65c55120a2` 切出，含 IceBlue LESS 與 zktest；對話框排版用的 class 以 ZK-5053 commit 處理；Marble 樣式以 `ZK-6112: … (ZK-6199)` commit。截圖：https://claude.ai/artifact/HQd7mCKjqEbFvc5je7WVun
- D2（Z4）：已送出 [ZK-6198](https://zkoss.atlassian.net/browse/ZK-6198)。
- D3（Z5）：選項 C（膠囊形加 primary 色）。截圖：https://claude.ai/artifact/JGbLafLi7M7WzpajFbNxNA
- 實作派工：`doc/zkdemo-b8-impl-brief.md`；回報：`doc/zkdemo-b8-impl-report.md`。
- ZK-6199 的 Jira 截圖需手動附加：`doc/zkdemo-b8-z3/fileupload-dialog-picked-file-row-{marble,iceblue}.png`。

## 結果（2026-10-14）

- Z5 已進 marble（fast-forward，本地未 push）：`24bd1c629b`、`76258d5988`、`660cb43e0e`、`4b534abe6a`。兩輪獨立驗證見 `doc/zkdemo-b8-z5-verification.md`；`carousel-gallery.png` 依使用者授權以 `--update-snapshots=all -g "gallery carousel$"` 重拍，重跑比對通過。
- Z3：暫停，等核心團隊的 ZK-6199／ZK-5053。
- 8085 已由本 session 重啟（Jetty PID 6975）；8085 上的 jar 尚未含 Z5，要重建 zul jar 並重啟後才看得到。

## 第二批：Z9–Z12（2026-10-15）

來源：zkdemo-b5 交辦（zkdemo `tasks/new-demo/p4-b8-plan.md` 第 100–103 行）。版本：zk 11.0.0.FL.20261014-Eval、marble 11.0.0.FL.20261014。四項都依原始碼判定，本 session 未在瀏覽器重現；Z9、Z11、Z12 的實測數值由 zkdemo 端提供。

| 項目 | 判定 | jar 端動作 | 依據 |
|---|---|---|---|
| Z9a calendar `width` 超過 268px 無效 | Marble 造成（IceBlue 只有 `min-width: 230px`，沒有上限）；違反 V4（ZK 屬性設定的寬度要生效） | 修正（ZK-6112） | `calendar.css:34-36` `width: 100%; max-width: 268px`；inline `width` 蓋得過 `width`，蓋不過 `max-width`。tablet 的 `_calendar.css`、`_wheel.css` 也靠 `max-width` 設上限。修法：`.z-calendar[style*="width"] { max-width: none }`（前例：`separator.css:27`），預設外觀不變 |
| Z9b calendar `height` 只在下方留白 | Marble 造成：IceBlue 有 `.z-calendar-body { height: 100% }`，Marble 的 flex column 沒讓表格長高 | 修正（ZK-6112） | `calendar.css:103-110`；修法：`.z-calendar-body` 加 `flex: 1 1 auto` |
| Z10 `Audio` 換內容後伺服器端仍記著 PLAY | 核心 bug，與主題無關 | 依慣例開 Jira（需使用者同意） | `Audio.java:135-157`、`319-327` 的 `setSrc`／`setContent` 不重設 `_currentState`；用戶端換 src 會 rerender，`Audio.ts` 的 `unbind_` 呼叫 `stop()`，STOP 要等下一個請求才回到伺服器，所以同一請求內的 `play()` 被 `Audio.java:100` 擋掉。守門條件來自 `866bf254a7`（2022-03-31）。zkmax `Video.java:267`、`469` 是同一寫法（未重現） |
| Z11 `span.z-upload` display:block、高 20px | Marble 造成：LESS 換成 CSS 時（`2100200284`）漏掉 IceBlue `norm.less:539-555` 的整組規則 | 修正（ZK-6112） | IceBlue：`span.z-upload { font-size: 0; display: inline-block; width: 0; height: 0; position: relative }`，內層 `input` 透明、絕對定位。Marble 全 repo 沒有 `z-upload` 規則；在 flex 容器裡 span 被 blockify 才變成 block |
| Z12 停用日期格 `not-allowed` 游標看不到 | Marble 的規則自相矛盾（`pointer-events: none` 讓 `cursor` 永遠不生效） | 修正（ZK-6112）：拿掉 `pointer-events: none`，保留 `not-allowed` | `calendar.css:241-246`。點擊已由 JS 擋掉（`Calendar.ts:1065` 檢查 `.z-calendar-disabled`）；hover 圓盤已由 `calendar.css:248` 的 `::before` 透明規則蓋掉（同 specificity、位置較後）。Marble 有 26 個元件 CSS 檔的停用狀態用 `not-allowed`（V6）；IceBlue 是 `cursor: default` |

依原則：Z9、Z11、Z12 由 O1（IceBlue 沒有此問題）＋ V4／V6 決定，不屬 §4；Z10 依 O2 開 Jira，送出前需確認（E6）。

### 修正後 zkdemo 的 workaround

- Z9：`sclass="z-max-w-100"` 可拿掉。zkdemo 的 IT 修正前後都會通過，不能用來證明修正。
- Z10：Jira 修好前 `beep.stop()` 要保留。`Events.echoEvent("onPlayClip", …)` 的註解寫的是另一個理由（play() 落在舊 DOM），核心修正後要重測才能決定。
- Z11：不需 workaround；修正後 /demo/fileupload/ 的 32px 空白會消失。
- Z12：CalendarIT 用座標點擊的寫法可以保留；修正後 `element.click()` 也能點到格子（JS 仍會忽略）。

### 決定（2026-10-15，使用者同意）

- D4（Z9、Z11、Z12）：選項 A，三項一起修，每項一個 `ZK-6112: …` commit；由 Sonnet 實作、另一個 Opus agent 驗證。派工：`doc/zkdemo-b8b-impl-brief.md`；回報：`doc/zkdemo-b8b-impl-report.md`。
- D5（Z10）：選項 A，已送出 [ZK-6204](https://zkoss.atlassian.net/browse/ZK-6204)（Affects 11.0.0），由核心團隊修。送出前在 8085 用暫時探針頁重現：切換 src 那次的 AU 回應只有 `setAttr src`，沒有 `invoke play`，新音檔停在 paused；之後用戶端才回報 STOP。探針頁已刪除，確認回 404。

### 結果（2026-10-15）

- 實作在 worktree `ZK10/b8b/{zk,zkcml}`，branch `zkdemo-b8b`，已 rebase 到 marble `cc457a78ec`（range-diff 四個 patch 都相同）：zk `f6c3419a9f`（Z9）、`daa42df47c`（Z12）、`fe4ed9e2b3`（Z11）、`3503dae680`（Z9，選擇器只比對 `width` 屬性）；zkcml `ff3165ee4`、`fe5649586`（tablet 的 Z9）。已依使用者同意 fast-forward 進兩個 repo 的 marble（未 push），`ZK10/b8b` worktree 已移除。
- 兩輪獨立驗證都是 PASS，見 `doc/zkdemo-b8b-verification.md`；實作回報見 `doc/zkdemo-b8b-impl-report.md`。第一輪發現 `[style*="width"]` 也會命中 `min-width`（calendar 從 268 變成撐滿 1248px 容器），F2 改成 `:is([style^="width"], [style*=";width"], [style*="; width"])` 後修正。
- 沒有涵蓋的手寫 style：換行後才接 `width`、開頭有空白。這兩種仍維持 268／360 的上限，和修正前相同。
- Z11 修正後，flex 容器裡的上傳按鈕下方仍多一個 gap（24 而非 12），因為 `Upload.ts` 插入的 `span.z-upload` 本身也是 flex item。這是共用 DOM 結構造成的，CSS 改不了，IceBlue 應該也一樣（未實測）。
- 8086 預覽伺服器已依記錄的 PID 停止。
