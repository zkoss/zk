# Marble 外觀決策原則：整理計畫

日期：2026-10-14。目的：把各 session 中使用者對 Marble 外觀做過的裁示歸納成原則，讓簡單或同類的問題由 agent 依原則自行決定，只有例外與特殊情況才交給使用者。

## 1. 放在哪裡

**已決（D18-A，2026-10-14）：放在 `marble-theme` skill 之下**，只放「怎麼決定」，不放「決定的經過」。

| 內容 | 位置 | 理由 |
|---|---|---|
| 決策順序、可自行決定的條件、必須問使用者的例外、同族基準表 | `.claude/skills/marble-theme/reference/decision-principles.md`（新檔） | 任何 session 動到 Marble CSS 都會載入此 skill；決策發生的當下就讀得到。Jess review 結束後原則仍有效，不該綁在 `tasks/jess-review/` |
| `SKILL.md` 的入口 | `SKILL.md` 新增一小節「Decide or escalate」，加一列到 Reference 表 | 讓 agent 在問使用者之前先查原則 |
| 每條原則的出處（D 編號、issue、日期） | 留在原文件：`tasks/jess-review/decisions.md`、`tasks/zkdemo-b*`、`doc/marble-master-sync-strategy.md` 等 | skill 每次載入都佔 context，出處只用一個檔名指回去 |
| 元件規格（某元件應長怎樣） | 維持在 `doc/spec/`、`doc/contracts/` | 那是「結果」，不是「決策方法」；原則表只指向它們 |

不放在 `CLAUDE.md`：它每個 session 都載入，包括與主題無關的工作。
不另放在 `doc/` 的中文檔：agent 不會自動讀到，等於多一層轉介。

**語言：** skill 現有檔案全是英文，依「沿用該檔語言」的規則，新檔用英文。出處文件維持原語言。

**去重：** `tasks/jess-review/rules.md` §1 的「同族基準」與 §4「可直接做／必須停下來問」搬進新檔後，原處改成一行指向 skill；`CLAUDE.md` 工作守則「主題（Marble）」中屬於外觀判斷的條目同樣搬過去，流程類（git、port、Playwright）留在 `CLAUDE.md`。

## 2. 來源

1. 使用者在 65 個 session 中的 553 則發言（抽取檔在 session scratchpad；多數是「dN - a」，題目內容要回到記錄文件找）。
2. 記錄裁示的文件：
   - Jess review：`tasks/jess-review/{decisions,rules,deferred,status}.md`
   - zkdemo 修正：`tasks/zkdemo-b6-marble-fixes.md`、`tasks/zkdemo-b7-z5-jira-draft.md`、`tasks/zkdemo-b8-*.md`、`tasks/zkdemo-b8-z5-verification.md`
   - 同步：`doc/marble-master-sync-strategy.md`
   - 規格與架構：`doc/spec/{design-decisions,DESIGN,component-state-model,navigation-surface-rules,window-design-rules,md3-close-affordance-placement,auto-contrast-text,utility-naming,responsive-design,icon-policy,spacing-policy}.md`、`doc/important-decisions.md`、`doc/design-review-feedback.md`、`doc/utility-class-naming-review.md`、`doc/breakpoint-unified-spec-proposal.md`
   - 測試與基準：`doc/screenshot-tolerance-policy.md`、`.claude/skills/marble-theme/reference/harness-decisions.md`
   - memory：`marble-regressions-fix-under-zk6112`、`core-bugs-left-to-core-team`、`jess-review-autonomous-principles`、`visual-issues-via-artifact-with-images`
3. 範圍只收**外觀**決策（顏色、尺寸、間距、狀態樣式、圖示、跨元件一致性、主題範圍歸屬）。流程類決策（推送、git、port、模型分工）不收，除非它決定「這個外觀問題歸誰修」。

## 3. 步驟

1. **抽取**（三個 Sonnet agent 並行，唯讀）：每筆記為「議題 → 使用者選擇 → 理由 → 可否推廣」。輸出到 scratchpad。
   → 驗證：每筆附來源檔名與 D 編號；抽樣 10 筆回原文核對。
2. **歸納**（Opus，主 session）：合併成原則，分三層：
   - 決策順序（衝突時誰優先）
   - 同族基準表（遇到同類問題直接套用的結論）
   - 例外清單（一定要問使用者的情況）
   → 驗證：每條原則至少有一筆裁示支持；沒有支持的不寫。
3. **回測**（獨立 Opus verifier，看不到使用者的選擇）：從抽取結果保留約 20 筆過去的外觀裁示，只給題目與選項，要它依原則作答，再與使用者當時的選擇比對。
   → 驗證：記錄一致率；不一致的每一筆要嘛修原則、要嘛列為例外，並寫明哪一種。
4. **寫入**：新增 `reference/decision-principles.md`，改 `SKILL.md`，把 `tasks/jess-review/rules.md` 與 `CLAUDE.md` 的重複內容改成指向新檔。
   → 驗證：`git diff --stat` 只含這幾個檔案；skill 檔不含客戶名稱等內部資訊（repo 是公開的）。
5. **提交**：`ZK-6112: …`，逐路徑暫存。推送另行確認。

## 4. 回測結果

2026-10-14。題目 24 題，取自 Jess review、zkdemo B6／B8、master 同步、規格與 utility 命名的過去裁示；驗證者為獨立的 Opus agent，只讀原則檔與題目，看不到使用者的選擇。

| 輪次 | 與使用者選擇一致 | 自行決定但選錯 | 判為 ASK | 指出的措辭問題 |
|---|---|---|---|---|
| 第 1 輪 | 24／24 | 0 | 2（T16 carousel、T18 utility 命名） | 8 |
| 第 2 輪（修正措辭後，全新 agent） | 24／24 | 0 | 1（T16） | 7，皆為邊界情況 |

- T16（兩個主題共有、屬設計判斷的 carousel 指示點）當時使用者確實是被詢問後才裁示，判為 ASK 是預期結果。
- 第 1 輪後依驗證者意見修改：O2／O4 的界線（何時可用結構選擇器、何時等核心修正）、設計師的 mold 選擇改歸 O6、E3 不再涵蓋 V2／V6 引起的預設外觀變更、E4 區分全域 role token 與元件 token、O7 補「移植佔用版面也接受」、V4 補「ZK 已出貨的數值勝過 MUI」、§3 文字邊緣列補巢狀內容。第 2 輪後補 O2「各主題自行選的外觀不屬 O2，屬 E2」。
- **限制：** 原則是看過這些裁示後歸納的，所以這是「樣本內」檢驗：證明原則寫得夠清楚、能重現過去的決定，不證明對新問題的判斷一定正確。新問題的實際表現要靠 §5 的回填機制持續修正。
- 題目與兩輪答案在 session scratchpad（`backtest-q/`），未進版控。
