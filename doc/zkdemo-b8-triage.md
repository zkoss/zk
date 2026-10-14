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
