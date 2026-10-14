# NS9：第三輪同步後的 Marble 待修清單

日期：2026-10-14。來源：`doc/marble-master-sync-strategy.md` §6.3（驗證時發現的既有問題）、§9.4 V2、§10 F4。決策依 `.claude/skills/marble-theme/reference/decision-principles.md`，只有其 §4 的情況才問使用者。

## 1. 項目

| # | 問題 | 先查 |
|---|---|---|
| 1 | `F110_ZK_6097_StyleTest`：carousel 箭頭字型是 Arial，不是 Inter | IceBlue 是否通過；Marble 的箭頭元素為何沒繼承字型 |
| 2 | wcag `CascaderTest`：`ul.z-cascader-cave` 可捲動卻無法用鍵盤聚焦（axe `scrollable-region-focusable`） | IceBlue 的 cave 是否可捲動；是否為 Marble 加的 overflow／max-height |
| 3 | cascader 在 320px 下展開第二層溢出 72px | IceBlue 同條件；上游 E3（za11y `_inp-a11y.less`）的移植是否生效 |
| 4 | panel header 在 text spacing 下 icon 被推出 panel、關閉圖示被切掉 | IceBlue 同條件 |
| 5 | messagebox 長文在 320×640 下 top 為 −14px | IceBlue 同條件；與第 8 項的關係 |
| 6 | compact 密度：實心按鈕 32px、外框按鈕 34px，token `--zk-button-height` 30px；確認彈窗 OK 32、Cancel 34 | 只屬 Marble（token 與 compact 都是 Marble 的） |
| 7 | forced-colors 下 badge dot 完全看不見 | IceBlue forced-colors 下是否看得見 |
| 8 | `messagebox.css` `.z-messagebox` 的 `max-width: calc(icon + 100%)`，註解說要限制在對話框內，但算出來比容器寬 | 是否造成可見問題（第 5 項） |

## 2. 步驟

1. **分流（獨立 Opus 驗證者）**：建立隔離的 worktree（`ZK10/ns9/{zk,zkcml}` 在 `marble`，`ZK10/ns9-ice/{zk,zkcml}` 在上游 master `a447efa068`），分別在 8115（Marble）與 8125（IceBlue）跑 zktest。每項在兩個主題重現並量測，依 O1 分成：只有 Marble（ZK-6112 修）／兩邊都有且原因在共用程式碼（開 Jira，E6 確認）／預覽頁或測試本身（O5／O6）。
   → 驗證：每項都有兩個主題的量測值與截圖；分類寫明依據的原則。
2. **實作（Sonnet 工程師）**：只修分到 Marble 的項目，在 `ZK10/ns9/` 的分支 `ns9`，每項一個 commit（`ZK-6112: …`）。每項有測試：既有的 zktest（第 1、2 項）或新增 Playwright／zktest。
3. **最終驗證（另一個 Opus 驗證者，不看 diff）**：逐項重量；回歸跑 zktest 相關 class、Playwright chromium／tablet／gallery／forced-colors／component-theming。
4. **合併**：ff-only 併入 `marble`。主 checkout 有其他 session 未提交的檔案，合併前查交集；有重疊就停下來問。推送另行確認。

## 3. 限制

- 不用 8085、8095、8105；停伺服器只 kill 記下的 PID。
- 不動主 checkout `ZK10/zk`、`ZK10/zkcml` 的未提交檔案；不動 `sync-ice/`、`zk6192/`。
- `@Tag("IceBlueOnly")` 測試不刪不改。

## 4. 結果

2026-10-14。分流與最終驗證報告：`ZK10/ns9/triage-report.md`、`ZK10/ns9/verify-report.md`（`NS9-FINAL: PASS`）。分支 `ns9`（`ZK10/ns9/zk`）已於 2026-10-14 以 fast-forward 併入 `marble`（`8ca7a77189`，D19-A）；zkcml 無變更。後續發現記在 `doc/marble-theme-followups.md` §31–33。

| # | 分類（原則） | 處理 | Commit |
|---|---|---|---|
| 1 | 只有 Marble（O1） | `.z-carousel-arrow` 補 `--zk-typescale-font-family`；`F110_ZK_6097_StyleTest` 由失敗轉通過 | `9e82e16a8d` |
| 2 | 測試失敗只在 Marble（cave 上限 280px 比 IceBlue 350px 小），根因是兩主題共有的 za11y（O2） | 不改 Marble 上限（改了只是讓測試看不到缺陷）；開 Jira ZK-6200 | — |
| 3 | 兩主題都有，`Cascader.ts`（O2） | 開 Jira ZK-6201 | — |
| 4 | 只有 Marble（O1） | panel、window 標題改為可換行（`overflow-wrap: anywhere`），文字間距下 icon 0/4 外溢；280px 且標題放不下的 panel 表頭 60→80，tablet 上會斷字，換得原本被切掉的關閉鈕重新出現 | `c2f1b4cbdc`、`a4cd9cbf55` |
| 5 | 已無法重現（O6） | 結案；原 −14px 由 `44b924a412` 消除 | — |
| 6 | 只有 Marble（O1、V6） | compact 的 `--zk-button-padding-y` 改 4px，所有變體 30px（原 32／34），icon-only 32，預設密度不變 | `28f4691a96`、`57755e533f` |
| 7 | 只有 Marble（O1、O9）。分流時誤判為兩主題都有：IceBlue 上游完全沒有 forced-colors 規則，不支援這個模式，所以不是 IceBlue 的缺陷（使用者 2026-10-14 指出） | `_forced-colors.css` 讓 dot 以 `CanvasText` 呈現；記錄於 ZK-6202（已關閉） | `e9b218d6de` |
| 8 | Marble 的無效規則（O10） | 刪除 `max-width: calc(icon + 100%)` | `2a545ef32c` |

- 測試：新增 zktest `B110_ZK_6112_{PanelHeaderTextSpacing,CompactButtonHeight,BadgeForcedColors}Test`，修正前皆失敗、修正後通過；5 個 class 8/8 通過。
- 回歸（合併 marble 後）：Playwright 21 個 project 689 passed、47 skipped、0 failed；基準只重生 `panel-gallery.png`、`panel-tablet.png`。
- Jira（D20-A，2026-10-14 開立，三張都已在 IceBlue 以單內程式碼重現）：ZK-6200（cascader cave 無法鍵盤聚焦，za11y）、ZK-6201（cascader popup 窄視窗不被限制在視窗內）。ZK-6202 原本誤開為 IceBlue 缺陷（見第 7 項）；依使用者指示改寫為 Marble 的 bug（badge dot 在 forced-colors 模式消失），附上修正說明（`e9b218d6de`）後於 2026-10-14 關閉。截圖在 `ZK10/ns9/verify/`，需手動附上。
- 製程註記：工程師在第二輪曾以 `-g` 跑一次已提交的 spec（無 `--update-snapshots`，未寫任何檔案），違反「不用 `-g`」；正式更新與完整回歸都沒有用。
