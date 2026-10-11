# Jess 設計審查：延後項目（deferred）

本頁列出目前刻意不在範圍內的 Jess 設計審查 issue。「延後」代表暫不處理，不代表關閉或駁回；以下所有 issue 在 `hawkchen/marble-issue` 中皆為 OPEN。Group A 為 ZK core 行為，theme CSS 無法修正，需另開 ZK Jira；Group B 為 preview 頁面缺陷，theme 本身正確，demo 頁有誤，修正成本低但優先度低。

## Group A：ZK core（11）

| Issue | Symptom | Note |
|---|---|---|
| #58 | borderlayout 收合的 region 無法重新停靠：flyout header 的還原 chevron 為 `display: none` | **最高嚴重度**，是功能性死路。開 Jira 前先確認來源，`z-borderlayout-icon` 未出現在 theme CSS 中 |
| #32 | biglistbox `frozenCols=3` 時，凍結區與可捲動區渲染為上下兩列 header，而非單一連續列 | 先確認 theme CSS 是否有貢獻 |
| #9 | cascader 會提交僅含父層的選取（如 Japan 未選 Tokyo），欄位空白卻仍顯示清除 "×"；重新開啟後狀態不一致 | 兩個缺陷：部分提交，以及開關時未重設暫態 |
| #21 | daterangebox 無法儲存時間設定：沒有儲存按鈕，關閉 picker 即捨棄 | |
| #20 | daterangebox 把額外日期畫成 selected（1 月 1 日、2 月 1 日與真正端點外觀相同） | 已知為 ZK artifact，非 theme 錯誤：空白或預設 popup 會把錨點月份的 1 日畫成 `z-calendar-selected`。開單前先查是否已有 ZK issue |
| #28 | searchbox 重新開啟後仍保留舊輸入 | |
| #44 | anchornav 會更新 active 高亮，但不會捲動到目標區段 | |
| #50 | navbar 下拉選單開啟後無法關閉 | |
| #56 | window popup 開在頁面下方，超出 viewport | 先確認 theme 未設定 `position`、`transform` 或 `height` |
| #75 | confirmpopup 的 left/top 定位與其觸發按鈕重疊 | 先檢查 theme 的 arrow 與 margin 尺寸 |
| #14 | chosenbox 搜尋無結果時沒有 "no result" 空狀態 | 可能是尚未實作的功能而非 bug，開單前先定義框架 |

## Group B：preview 頁面（14）

Demo 來源位於 `zkpreview/src/main/webapp/web/`。

| Issue | What to change | Verified? |
|---|---|---|
| #7 | `calendar.zul:11` 寫死 `_c.set(2020, Calendar.MARCH, 15, ...)`，改用目前日期 | ✔ 行號已核對 |
| #70 | `loading.zul:9` 的 `<timer delay="14000">` 改為 `3000` | ✔ 行號已核對 |
| #60 | `linelayout.zul`：時間軸節點以 24 個 `<button>` 呈現，改為 label | ✔ |
| #5 | `calendar.zul:21-38`：獨立 calendar 位於 "With Datebox" 區段上方，兩者無互動而易被誤認為 bug，移除或拆開 | ✔ |
| #62 | `cardlayout.zul`：卡片背景寫死，改用既有 `z-bg-*-container` utility（如 `z-bg-surface-container-*`，定義於 `zul/css/utility/_colors.css`），不新增頁面專用 CSS | ✔ |
| #63 | `rowlayout.zul`：欄位背景寫死，做法同 #62 | ✔ |
| #45 | coachmark：將 "Start tour" 移到 "Steps" 之上（demo 須先開始 tour 才會顯示） | ✔ |
| #82 | avatar：改用實心 demo 圖片 | ✔ |
| #15 | colorbox："With value" 與 "Open swatch" 兩個 demo 外觀無法區分 | ✔ |
| #42 | organigram demo 多出一條連接線 | ? |
| #46 | drawer：將 "close" 移到獨立一行 | ? |
| #33 | biglistbox 的 `fixFrozenCols` 與 `Invalidate` 切換無可見效果，用途不明 | ? |
| #59 | hlayout 混合元件未垂直置中 | ? 見下方 theme 問題 |
| #64 | space demo 的 bar 高度與內容不合 | ? 見下方 theme 問題 |

- **#59 與 #64 涉及 theme 問題**：theme 預設是否應為置中對齊，或由 demo 傳入 `valign="middle"` / 明確高度？決定前不修改 ZUL。若決定由 theme 預設，兩者即移至 P1。
- **#62 與 #63 的規則**：preview 頁只能組合既有 `z-*` utility，不得寫頁面專用 CSS。已確認 `z-bg-*-container` utility 存在，因此無需新增 utility。
