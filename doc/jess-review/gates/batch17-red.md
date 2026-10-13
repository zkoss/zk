使用 8085（批次 17 方法；RED 結果由 Verifier 附在本檔末）

# 批次 17 方法：重現後確認的 4 個 Marble 回歸（#32 #20 #50 #75）

日期：2026-10-13。來源：[../repro/report.md](../repro/report.md)。四項都在兩個主題下重現過，且只有 Marble 出現，屬 ZK-6112。

## 共通量測條件

- Marble 8085，頁面 `/repro/jess-<N>.zul` 與預覽頁 `/web/<component>.zul`；IceBlue 8125 `/zktest/jess/jess-<N>.zul` 只作參考值，不作判定。
- Playwright Chromium，viewport 1280×900、deviceScaleFactor 2、`ignoreDefaultArgs:['--hide-scrollbars']`；注入 `*{transition:none!important;animation:none!important}`、等 `document.fonts.ready`；每個狀態在重新載入的頁面上量。
- 量測只看像素 ink、`getBoundingClientRect`、`elementFromPoint`；computed style 只用來解釋，不作判定。
- 量測前比對 served CSS（`zk.wcs` 或各 `.css.dsp`）與 build 輸出一致。

## 預計修改的檔案（Generator 只能動這些）

| Issue | 檔案 |
|---|---|
| #32 | `zkcml/zkmax/src/main/resources/web/js/zkmax/big/css/biglistbox.css` |
| #20 | `zkcml/zkmax/src/main/resources/web/js/zkmax/db/css/daterangebox.css` |
| #50 | `zkcml/zkmax/src/main/resources/web/js/zkmax/nav/css/nav.css` |
| #75 | `zk/zul/src/main/resources/web/js/zul/wgt/css/confirmpopup.css` |

## #32 biglistbox：frozen 與 scrollable 窗格並排

- **J32-1（今天必須失敗）：** `jess-32.zul`（`frozenCols=3`）與預覽頁 `biglistbox.zul` 所有設了 frozenCols 的實例：frozen head 與 scrollable head 的頂端 |Δy| ≤ 1，且 scrollable head 左緣 ≥ frozen head 右緣 − 1；body 兩窗格同樣。
- **J32-2（今天必須失敗）：** 兩窗格表頭文字 ink 的垂直中心 |Δ| ≤ 1（同一列）。
- **保護項（今天必須通過）：** biglistbox 外框矩形不變；未設 frozenCols 的實例版面不變；批次 4（#31 #78）的捲軸不蓋表頭、捲軸外觀量值不變；表頭列高不變。

## #20 daterangebox：每月 1 號不該畫成選取

- **J20-1（今天必須失敗）：** 空白 daterangebox 開啟 popup，每個面板的 1 號格（非端點）背景 ink 與同面板一般未選日（例如 2 號）的 ΔE（CIE76）< 3。
- **J20-2（今天必須失敗）：** 已選 10–15 號的 daterangebox 開啟 popup，1 號格同 J20-1。
- **保護項：** 選取範圍兩端點的填色與今天相同（ΔE < 1）；範圍內日期的底色不變；今天標記不變；獨立 `calendar.zul` 與 datebox 的 popup 中選取日仍畫實心（與今天 ΔE < 1）。

## #50 navbar：水平展開的下拉不能蓋住觸發項

- **J50-1（今天必須失敗）：** `jess-50.zul` 與預覽頁 `navbar.zul` 的水平、非 collapsed navbar：開啟有子項的 nav，下拉的頂端 ≥ 觸發項底端 − 1，且觸發項中心點的 `elementFromPoint` 是觸發項本身或其子孫。
- **J50-2（今天必須失敗）：** 再點一次觸發項，下拉關閉（不可見或高度 0）。
- **保護項：** 下拉的左緣在觸發項的 x 範圍內；水平 navbar 列高與項目位置不變；垂直展開式 navbar 的巢狀縮排（批次 8 #51 的量值）不變；水平與垂直 collapsed popup 仍可開關。

## #75 confirmpopup：left、top 方向的箭頭不能壓到按鈕

- **J75-1（今天必須失敗）：** `jess-75.zul` 與預覽頁 `confirmpopup.zul`：left 與 top 方向，箭頭 ink 與按鈕矩形沒有交集，且 popup 框到按鈕的間距與 right、bottom 方向相同（±1px）。
- **保護項：** right、bottom 方向的間距與今天相同（right 9、bottom 8，±1）；箭頭 ink 尺寸不變；popup 框尺寸不變；四向箭頭都指向按鈕。

## 回歸範圍（最終判定）

- 四個元件的 gallery（`screenshot.spec.ts` 中對應項目）與互動腳本、`component-theming`、`hit-target`、`focus-ring-scan`。沒動到顏色或邊框，不跑 forced-colors。
- Playwright `--output` 指向 scratchpad 或 gates 目錄。

## 方法修正（RED run 後定稿，之後不再改）

RED run 找到 5 個方法問題，全部依 Verifier 的建議修正。判定的意圖都沒有改，只是寫清楚量哪裡。

- **M1（J32-1 預覽頁）：** `biglistbox.zul` 載入時沒有設 frozenCols 的實例。改為「預覽頁 `#biglist` 用 frozenColsBox 選 3 之後再量」。
- **M2（J75-1 預覽頁 Left）：** 預覽頁的 Left 按鈕在 x=202，popup 寬 283，左側放不下，被夾到 x=0 蓋住按鈕（gap −80.64）。這是版面空間不夠，跟主題無關。Left 只判 `jess-75.zul`；預覽頁只判 Top，Left 只記錄數字。
- **M3（P32 表頭列高）：** 「表頭列高不變」指每個 pane 高 39、header cell 高 37，不是 head-outer。head-outer 今天因為堆疊是 78，修好後會變成 39。
- **M4（P20 今天標記）：** ZK 沒有替日期格加 `z-calendar-today`。保護項改為「今天那格的底色與環形 ink 不變」，量值會隨執行日期而變。
- **M5（J50-1 觸發項）：** 觸發項指 `a.z-nav-content`（`-cnt`）。如果取 `li.z-nav`，cave 是它的子孫，判定會不經修正就通過。

## RED 結果（Verifier，2026-10-13，8085 PID 83654，四個 served CSS 與 build 的 md5 相同）

| 項目 | 今天（Marble） | 結果 |
|---|---|---|
| J32-1 | jess-32：兩個 head 頂端 Δy 39，cnt.left 1 < fx.right 391，body Δy 742；預覽 frozen3：Δy 39，33 < 423，body Δy 372 | 失敗（符合預期） |
| J32-2 | 表頭文字 ink 中心 Δ 39 | 失敗（符合預期） |
| J20-1 / J20-2 | 1 號 (55,111,208)，2 號 (255,255,255)，ΔE 77.55（空白、已選、預覽頁都一樣） | 失敗（符合預期） |
| J50-1 | jess cave.top −24 < cnt.bottom 40，中心命中「Step Two」；預覽頁 552 < 636、502 < 636 | 失敗（符合預期） |
| J50-2 | 再點後仍可見（高 88／128／228） | 失敗（符合預期） |
| J75-1 | jess Left：箭頭在按鈕內 210 device px，gap 0（對照 8.81）；Top：gap 0.5（對照 8）；預覽 Top：gap 0.5 | 失敗（符合預期） |
| 保護項 | 全部通過；量值存在 `out/red-marble-*.json` | 通過 |

保護項的重點數值：

- **P32：** 外框 jess 600×300；預覽 striped 1210×200、biglist 1210×500。pane 39／cell 37。批次 4 的 thumb 量值（8px、(224,224,224)、貼邊）與 batch4-final 相同。
- **P20：** 端點 (55,111,208)；範圍內 (213,230,255)；calendar.zul、datebox.zul 的選取日 (55,111,208)。
- **P50：** cave.left 等於 cnt.left；水平列高 40；#51 縮排 Home 43、Contact 42.5、Reply 69、Settings 68.5、Edit profile 95；collapsed popup 移入就開、移出就關。
- **P75：** gap Right 8.81／8.39、Bottom 8／8；箭頭 8×16（左右）、16×8（上下）；jess popup 200×101.5；四個方向都指向按鈕。

**IceBlue 參考：** 13:14 另一個 session 執行 `pkill -f gretty`，8125 停機，這次沒量到。參考值改引 [../repro/report.md](../repro/report.md)。

**腳本：** scratchpad `batch17-red/`（`run-all.sh`、`summary.cjs`）。最終判定執行 `./run-all.sh final marble`，再執行 `node summary.cjs final marble red`。

GATE17-RED: OK（方法已依 M1–M5 定稿）
