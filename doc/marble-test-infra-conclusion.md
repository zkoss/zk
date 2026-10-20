# Marble 測試基礎設施：結論

來源（皆在 `doc/`）：marble-test-baseline-run.md、marble-test-phase1-selector.md、marble-test-phase1-verification.md、marble-test-phase1-verification-2.md、marble-test-phase2-repro.md、marble-test-improvement-plan.md、marble-selective-test-rules.md、marble-jenkins-visual-test-plan.md、client-rod-probe.md、marble-sectioned-gallery-brief.md

用途：記錄 Marble 外觀測試（zkpreview Playwright 套件）的基準、部分重測選擇器、重型頁面、彈出層截圖的結論與目前運作規則。

## 結論

- 基準（階段 0，2026-10-16）：全測 1345 個測試，1344 個兩次結果相同；唯一不穩定項為 chromium `tabbox gallery`。
- 既有失敗：font-size 130 個（兩次都失敗）、gallery `component-theming`、chromium multislider／rangeslider 的 hover、focus 4 個（已由 D6 重建基準，之後通過）。
- 部分重測選擇器已接受並 commit `58ef1c897b`（D13 = A）。輸入變動檔案，輸出 full／partial／none、原因、單一 Playwright 指令、uncovered class。
- 運作規則（目前）：
  - 選出測試數 > ceil(總數 × 2/3) 時全測（1345 個時為 897，取代原 30 頁門檻，D10）。
  - 只解析被改動的規則（需 `--base`）；改動落在 at-rule 標頭或結尾、CSS 無法解析、檔案新增或刪除 → 整檔解析。
  - 全測觸發：`tokens`、`base`、`utility`、`_layer-order.css`、`.wcs`、`lang.xml`／`lang-addon.xml`、CSS 建置腳本、gradle、字型、`playwright.config.ts`、`package.json`／lockfile、`misc.css`（視同共用檔）、無 `.z-*` selector 的規則、class 對應不到頁面。
  - 不測：`doc/**`、`*.md`、`.claude/**`、`tasks/**`、`logs/**`、`zkpreview/scripts/**`、`*-forced-colors.png`。
  - 彙整頁（`inputs`、`overview`、`preview`、`component-theming`）只在其 `.zul` 改動時選取（D12，`zkpreview/scripts/aggregate-pages.json`）。
  - 部分重測加 `--fully-parallel`（`UNSAFE_PARALLEL_SPECS` 目前為空）；全測不加。
  - 合併進 `marble`／`main` 前與 release 前一律全測。
- 選擇器驗收：突變案例 1、2b、3、4、7 的 F ⊆ S 成立，單一指令讓 F 全部失敗；案例 5、6、9 為 full；案例 8（只改文件）為 none。
- 新突變 10b（focus-within，只經 `pv/input-invalid-guard.zul` 到達）抓到 2 個 `input-invalid` 測試；新基準檔名對應該頁的桌面 gallery 測試，各選出 1 個。
- 重型頁面（階段 2）：listbox、tree、window、panel、tabbox、toast 的 gallery 各連續 10 次通過，不需修法。`component-theming` 基準過時（D8 已失效，該頁已於 2026-10-18 刪除）。
- 客戶端 ROD（`org.zkoss.zul.client.rod=false`）：僅量測，未提交任何設定。
- 彈出層截圖（階段 3 第一批）：45 個測試，已合併進 `marble`（`2243947fcd`..`af2c6c1f6f`）。
- 分區截圖（D7）：交由 session zk-e3 執行，交接文件為 marble-sectioned-gallery-brief.md。
- Jenkins 接線（`ZK-Marble-Visual-Test`）：2026-10-19 使用者裁示暫緩；部分測試在本機跑，全測由使用者決定何時跑；D21、D22 撤回。

## 關鍵數字

| 項目 | 數值 |
|---|---|
| 階段 0 全測 wall-clock | run1 924 s、run2 675 s |
| 階段 0 結果 | passed 1162／1163，failed 136／135，skipped 47 |
| 驗收基準失敗集 R | 131（font-size 130 + `component-theming` 1） |
| 全測 wall-clock（驗收） | 不平行 380 s；平行 402 s、502 s、481 s；突變狀態 571 s、629 s、531 s |
| 突變 1（`button.css`） | 選出 646／1345，部分 203–205 s，為同狀態全測的 38–39 %（對 380 s 為 53–54 %） |
| 其他突變部分重測 | listbox 2b 68 個 29 s；datebox 115 個 39 s；combobox 128 個 49 s；button.zul 55 個 21 s；invalid 128 個 71 s；focus-within 128 個 50 s |
| 平行與不平行全測 | 失敗數 132／132／133／132 對 R；各多出 1–2 個且每次不同 |
| class-index | 137 頁（115 頂層 + 10 `pv/` + 12 `utility/`），0 錯誤 |
| 滑桿基準尺寸 | multislider 524×52 → 524×110；rangeslider 524×48 → 524×110 |
| component-theming | 基準 18407 px 高，實得 18647 px（差 240 px），像素差 607719 或 923516 |
| gallery 容忍值 | `maxDiffPixelRatio` 0.2 %；換算 tabbox 11,089 px、tree 16,481 px、component-theming 47,121 px |
| 分區統計 | 109 頁、323 區；25 頁只有 1 區 |
| 彈出層 class（盤點 229 個） | 改為 `rod=false` 後 14 個 popup class 存在；53 個 popup class 中 39 個仍不存在 |
| 執行緒耗盡 | 約 3 次全測（約 4,000 個測試）後 `unable to create native thread`，上限 4,097 |

## 未解決事項

- font-size 130 個測試兩次都失敗；其中 25 頁的差異分布未調查（細節在 `run1.json`／`run2.json`）。
- 平行與不平行全測的失敗集合不同；每個組合只取 1 次樣本，「測試不穩定、非順序相依」未以重複取樣證明。
- `mesh-header-footer.spec.ts` 360 個測試的歸屬粒度：含 `z-*` 的 guard 頁變動時整檔被選入。
- 預覽站執行緒耗盡：已開 ZK-6213；連跑全測時需重啟伺服器。
- `tabbox gallery` 的不穩定（階段 0 失敗一次）未重現（階段 2 單獨 10/10 通過）。
- 選擇器限制：`pv/` guard 頁只在 spec 以字串字面值開啟時被索引；變數拼的路徑會漏收；新增頁面後須重跑 `build-class-index.mjs`。
- 突變 1 的比例取決於分母（38–39 % 或 53–54 %）。
- 案例 3、4 的彈出層部分重測：階段 3 後是否重跑，來源未記錄。
- `forced-colors-gallery` 每次執行改寫 99 張審查用 PNG；階段 0 的 `tree-header` 差異未調查。
- 基準圖平台（D21）未決；650 個 PNG 在 macOS 產生，Linux 上的差異未量測。
- 客戶端 ROD 是否採用未決定；`window.zul` 的 `vbox`／`vlayout` 來源未追查。
- D7 分區截圖：各區容忍值尚未量測；命名方式需由 zk-e3 回報給選擇器。
