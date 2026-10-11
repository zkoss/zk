# Jess 設計審查裁示索引（Marble 主題）

本檔只列 Jess design review 每個裁示 ID 的最終結論，不含過程、被取代的裁示或 RED／FAIL 紀錄。被取代的裁示只在取代它的那一列標示。
ID 在整份 Jess review 內全域唯一，範圍 D1..D125；D 編號沿用各批文件，不重新編號。
Issue 欄的 #數字為 Jess 追蹤 repo 的 issue 編號；「—」表示該裁示不對應單一 issue；「未標」表示來源未註明。
證據與量測報告在 [gates/](gates/)；工作規則見 [rules.md](rules.md)。

| ID | Issue(s) | 裁示（最終結論） |
|---|---|---|
| D5-A | #41, #43 | 清單列焦點標示：滑鼠點選時不出現，鍵盤移動時只有目前列出現，listbox 與 tree 形狀相同。 |
| D6-B | #52 | tabbox 指示線採完整 MD3 primary tab 樣式（粗 3px、上方圓角 3px、寬度等於文字寬、置中）；取代 D6-A（只改粗細）。 |
| D7-A | #65 | splitter 的可收合按鈕維持 pointer；拖動游標的修法見 D11-B。 |
| D8-A | #67 | errorbox 只改展示頁靜態範例，佈景 CSS 不動；回覆 Jess 說明 errorbox 本來就可拖。 |
| D9-A | #41, #43, #52, #65, #67 | 核准第一批驗證方法。 |
| D10-A | #23, #41（後半） | 清單列選取底色改用 MD3 的 secondary-container。 |
| D11-B | #65（拖動游標） | 拖動游標的 JS 相關步驟不在本批處理，另開 ZK Jira。 |
| D13-A | #52 | 指示線量測扣除兩端反鋸齒，最小寬 24px 以同法計，容差 ±0.5px。 |
| D14-A | 未標 | tab 太窄時沒有省略號不屬於 #52，另記為待辦；該待辦後於 D119-B 修好。（結論見 gates/batch1-52.md） |
| D16-B | — | Jess 留言直接貼出，不另經草稿；已由 D125-A 取代（留言由使用者確認後才貼）。 |
| D18-A | #23, #41（後半） | 依 D10-A 同時修改七個元件的清單列選取色（第二批驗證方法核准）。 |
| D19-A | follow-up 8 | 過時的截圖 baseline 先列 diff 經使用者核准，才以 Playwright 更新失敗項目。 |
| D20-A | 非 Jess issue | 非 Jess 的前置工作依同一流程：先訂並核准驗證方法，再動手。 |
| D21-A | — | 核准 27 項 baseline 更新，含 16 項 U 類 gallery。 |
| D22-B | follow-up | 補上截圖涵蓋缺口：新增 combobox、searchbox、chosenbox 三張下拉截圖；gallery 容差已由 D118-B 取代。 |
| D23-A | follow-up | tree row 與 listbox row 的 focus-scan 照實際方式模擬（列加 -selected、-focus；:focus-visible 加在 .z-focus-a）。 |
| D24-A | — | 核准截圖測試兩個涵蓋缺口的驗證方法（D22-B 段）。 |
| D25-A | follow-up 10 | chosenbox 下拉截圖只作畫面保護；判定 3、4 改套用在新增的 chosenbox-chip-focus.png。 |
| D26 | #34 | 使用者自行修改 grid.zul 後記為已修，不跑 RED；最終 Verifier 仍重量並確認佈景 CSS 無 diff。 |
| D27 | #35 | 只改排序圖示，欄選單維持 caret。 |
| D28 | #36 | 游標只留在排序圖示上；點列右側空白不切換。 |
| D30 | #38 | 所有 hover 強度統一為 4%，token 預設值不變。 |
| D31 | #39 | 排序圖示移到文字後面，與 #35 一起做。 |
| D32-C | #37 | CSS 先修，同時開 ZK Jira；取代 D29（純 CSS 做不到才開 Jira）。 |
| D33 | #37 | colspan 列不做位置斷言（規格上不支援），只記錄現象，並要求版面不位移。 |
| D34-A | 未標 | 長標籤被排序圖示壓住：本批不改佈景，可用的 CSS 寫法記入 follow-up 11。 |
| D35 | 全部批次 | 最終判定改用 Fable；已由 D124 取代（Opus 5.5 為主，Fable 為替代）。 |
| D37 | grid, listbox, tree, paging-tablet | 核准重生這四組 baseline。 |
| D38-A | grid-tablet | 核准重生 grid-tablet baseline；既有凍結表頭問題記為 follow-up 14（後於 D119-B 修好）。 |
| D39-A | #31 | 軌道不蓋表頭；放得下的方向完全不畫軌道。 |
| D40-A | #78 | 文件捲軸照文件靜止狀態做；做不到同等外觀時改為不改，並於留言說明差異。 |
| D41-A | biglistbox | 重生 biglistbox-tablet.png。 |
| D42-A | slider, calendar | tablet baseline 本批不處理；後由 D115（S6）重切。 |
| D43-A | #31, #78 | thumb hover 比靜止深一階（--zk-color-outline）。 |
| D44-A | #8, #12, #13, #16, #17 | 第 5 批範圍只含這五個 issue；combobutton、slider 留下一批。 |
| D46-A | #8 | 「選項撐滿下拉寬度」解讀為最後一欄（或單欄）的 hover、選取色塊延伸到 popup 右緣。 |
| D47-A | #12 | font-weight 與 font-style 都修；italic 屬 theme 範圍，不轉 DEMO。 |
| D48-A | #17 | 唯讀 combobox 只要不再出現選取反白即可，字串仍可選；取代 D45-A（user-select: none）。 |
| D49-A | 批次 5 | 重生 chosenbox 三張 baseline，逐路徑提交。 |
| D50 | #25 | slider 按住效果以 body:has(.z-slider-horizontal .z-slider-button:active) 純 CSS 完成（撤回先前「純 CSS 做不到」的判定）。 |
| D51 | 工作流程 | 允許第二台預覽站使用不同 port，條件是從自己的 worktree 啟動。 |
| D52-A | #29 | selectbox 箭頭改為同族 chevron 8×5，不用實心三角。 |
| D53-A | #3 | 修改 listbox.css：所有 listbox 的 th.z-listhead-bar 與表頭同色。 |
| D54-A | #49 | 保留預留勾選欄，不改 CSS；留言說明並附前後截圖。 |
| D55-B | selectbox | 重生 selectbox 三張 baseline；指令由使用者加權限規則後執行。 |
| D56-A | #53 | accordion 展開箭頭改為同族 chevron 遮罩（8×5，右緣距 12px），並補 forced-colors。 |
| D57-B | #55 | Window 拖曳 ghost 內部加 --zk-color-surface 底色（所有 Window 拖曳外觀改變，使用者已知悉）。 |
| D59-A | #30, #68, #10, #11 | 第 12 批 DECIDE 逐題各開議題頁，順序 #30 → #68 → #10 + #11。 |
| D60-A | #30, #68, #69 | 11.0 維持彈出式 errorbox；行內錯誤模式與不可拖曳等改動交 ZK Jira（見 D61-A）。 |
| D61-A | #30, #68, #69 | 建立 ZK-6191（New Feature，Affects 11.0.0），並於 #30、#68、#69 留言附連結。 |
| D62-A | #10, #11 | #10 Switch 改為 MD3（軌道 52×32、滑塊 16/24），off 態滑塊用 on-surface-variant，預覽頁獨立成區；#11 不改 CSS，請設計師決定 mold="toggle"。 |
| D63 | menubar | menubar gallery 與 tablet baseline 以現況重生（8cc85e8a6c）；tablet「Scrollable Menubar」異常見 D120。 |
| D80-B | #72 | 所有 Window 的關閉鈕 hover 改為中性色，不限於 messagebox。 |
| D81-A | #73, #74 | 歸 ZK-CORE，開 ZK-6187，不改 CSS。 |
| D82-A | #1 | 三角脫離是 ZK Errorbox._fixarrow 的問題，歸 ZK-CORE，開 ZK-6188，不改 CSS。 |
| D83-A | #47 | fisheye 水平與垂直兩個方向一起修（.z-fisheyebar relative、.z-fisheye absolute），水平預設外觀因此改變。 |
| D84-A | #72 | 保留公開 token --zk-window-close-hover-bg，只改預設值（動 _component-theme.css 一行與 spec 一列）。 |
| D85-A | #4 | 與 marble 提交 1c739948643 重疊：丟掉 B 線重複的五行，保留 marble 的版本。 |
| D110-A | C 線 P 類 | 批次 14 做 P1–P4；P5（tbeditor 按鈕 hover）不做。 |
| D112-A | notification | notification 改用與 toast 相同的 *-container token（info 用 surface-container-highest），箭頭用同組實色。 |
| D113-B | S1, S3, S4 | 修改共用檔 _forced-colors.css（selectbox 規則搬入）、修正空轉測試、新增捲軸欄位測試。 |
| D114-A | S2 | forced-colors 下 border="none" 的 panel 仍畫 1px 黑框，視為刻意，維持現狀。 |
| D115 | S5, S6 | gallery 容差與三個已知失敗都在本批處理：先重切 baseline，再收緊容差。 |
| D116-A | toast info 圖示 | 取代 D111-A：圖示用 --zk-toast-accent，該旋鈕預設值改為 80% status-info 混 20% on-surface（對比 4.85:1）。 |
| D117 | gallery 預覽頁 | 只有預覽頁標題文字差異的頁面視為預期變更，重切 baseline（根因 47d26068ed）。 |
| D118-B | gallery 11 頁 | 11 頁一併重切；progressmeter 等待 fill 寬度等於 aria-valuenow；gallery 容差由 0.01 收緊為 0.002。 |
| D119-B | C 線 follow-up 9, 13, 14, 19 | 9 tab 省略號已修（帶圖示的標籤除外）；14 平板 frozen 表頭已修；13 不重現；19 不屬 CSS，轉 D120。 |
| D120 | C 線 follow-up 19 | 平板 scrollable menubar 不是 ZK bug，是 Marble 平板 CSS 缺 `overflow: auto`；改 zkcml `tablet/_menu.css` 一條規則，不開 Jira。 |
| D123-A | gates/ | 未提交的 509 個過程檔先打包到 repo 外（`~/Documents/jess-gates-untracked-20261011.tar.gz`），再從 gates/ 刪除。 |
| D124 | Verifier | Opus 5.5 為主，Fable 為替代；取代 D35。 |
| D125-A | Jess 留言 | 我備好草稿，使用者確認後才貼出；取代 D16-B。 |

## 尚無最終結論

- **D58：** 推送；`marble` 已推送，裁示未另記。
