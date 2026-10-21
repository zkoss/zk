# Marble 外觀測試的缺口盤點

日期：2026-10-16。架構說明見 `doc/marble-test-architecture.html`。
範圍：zkpreview 的 Playwright 套件、Agent 驗證流程、Jenkins ZATS、zkdemo 回報。ThemePreviewApp 與 Jess 審查是首次開發的短期做法，不列入。

依「補上之後能多抓到多少問題」排序。標「已量測」的項目是本次從 repo 讀到的；標「依文件」的項目是從文件讀到，本次沒有重跑。

| # | 缺口 | 依據 | 狀態 |
|---|---|---|---|
| 1 | 沒有自動觸發 | 已量測 | GitHub Actions 的 `zk-build` 只跑 `./gradlew clean build`，並把 `includeBuild`（含 zkpreview）註解掉；沒有任何 workflow 或 git hook 執行 Playwright、`lint:css`、`audit-css.sh` |
| 2 | 彈出層打開後才出現的樣式 | 依文件 | `verification.md`：約 180 個 selector 不會被「開頁就截圖」畫出來；只有 `screenshot.spec.ts`、`tablet.spec.ts`、`component-theming.spec.ts` 有少數幾處呼叫 `open()`／`setOpen()` |
| 3 | 六個常用元件的截圖不可靠 | 依文件 | `verification.md`：listbox、tree、window、panel、tabbox、toast 的頁面常超過 `toHaveScreenshot` 的 5 秒穩定等待，「失敗不視為回歸」 |
| 4 | 單一變體的小改動可能被全頁容忍值吸收 | 已量測 | `gallery-scan.spec.ts:81` 的 `maxDiffPixelRatio: 0.002`；1280×900 的頁面約可差 2,300 px。一顆 120×40 按鈕的 1px 邊框整圈約 316 px。只有 23 個表單元件另有容忍 20 px 的狀態截圖 |
| 5 | 只測 Chrome | 已量測 | `playwright.config.ts` 的每個 project 都是 `devices['Desktop Chrome']`；沒有 Firefox／WebKit |
| 6 | compact 密度與品牌色覆寫沒有截圖基準 | 已量測 | compact 只出現在 `forced-colors.spec.ts`、`mesh-header-footer.spec.ts` 的尺寸斷言；沒有任何 spec 測試客戶覆寫 `--zk-*` token（`reference/brand-override.md` 的用法） |
| 7 | 修改共用程式後，IceBlue 有沒有被改壞 | 已量測（部分） | zktest 有 14 個 `@Tag("IceBlueOnly")` 測試類別，預設的 `test` 會跳過；Jenkins 是否執行 `testIceBlueOnly` 未確認 |
| 8 | `font-size-equivalence` 有 25 頁的差異超出已知的跨伺服器雜訊 | 已量測 | `doc/marble-test-infra-conclusion.md`（2026-10-16）：130 頁失敗，105 頁只是 13px +3（已知雜訊），另 25 頁差異較大，例如 `{"12px":108,"13px":6,"14px":-111}`；兩次執行結果相同。尚未判斷是基準過時還是渲染改變 |
| 9 | gallery 分區截圖略過的區段 | 依 zk-e3 回報（2026-10-18，分支 `marble-sectioned-gallery` `efeb851e7d`，未合併） | carousel 的 Autoplay；grid-header、listbox-header、tree-header 的 sizedByContent 區段初次載入結果不固定（listbox-header 12 次載入中 3 次截斷），所以沒有截圖基準 |
| 10 | 預覽站連跑多次全測後執行緒耗盡 | 已量測（`doc/marble-test-infra-conclusion.md`，2026-10-18） | 同一個 Jetty 連跑約 3 次全測後執行緒數到 4,097，出現 `OutOfMemoryError: unable to create native thread`（執行緒名如 `Timer-4032`）；之後在該伺服器上的結果作廢。來源已查（`doc/zkpreview-thread-leak.md`，2026-10-19）：ZK core 的 `SimpleDesktopCache.java:77` 每個 HTTP session 建一個 `java.util.Timer`（ZK-5435 加入、ZK-6102 修過停止時機），預設 `desktopMaxInactiveInterval` 3600 就會建立；Playwright 每個測試是新 session，一次全測約 1,345 個執行緒，session 逾時（約 30 分鐘）後才釋放。已開 ZK-6213（2026-10-19） |
| 11 | 全測本身有不穩定的測試 | 已量測（同上） | 同一版本、不平行：`window gallery` 重跑 5 次失敗 2 次，forced-colors `a real invalid combobox…` 逾時 2/5；另有 `chosenbox dropdown`、`tablet-toolbar gallery`、`datebox timezone <select>` 各出現一次。機器負載高（load average 20–59）時更明顯 |
| 12 | `z-navitem-selected` 沒有任何元件頁展示 | 已量測（2026-10-19） | 舊 class 索引只在 `preview.zul` 看到它（側欄當時有選中項），新索引沒有；`navbar.zul` 沒有 `selected` 的 navitem。選擇器靠所屬元件 `z-navitem` 仍會選到 `navbar.zul`，但沒有截圖會畫出選中狀態。屬預覽頁缺口 |

## 建議順序

1. **先補 #1。** 其他缺口補上的測試同樣要有人手動跑才會生效。
2. 再補 #2、#3：都是使用者最常看到的畫面，目前沒有可靠的自動比對。
3. #4–#7 視範圍決定。

## 不算缺口、但需要更新的文件

- `doc/playwright-visual-test-guide.md`（2026-06-29）記載 6 個 project、105 個預覽頁；現況是 24 個 project、169 個 `.zul`。
- `doc/playwright-visual-test-guide.md` 引用的 `doc/state-coverage-audit.md` 不存在於 zk repo。

## 進度（2026-10-19）

| # | 狀態 | 依據 |
|---|---|---|
| 2 | 第一批完成：20 個元件、45 個彈出層與 API 元素截圖（`popup` project） | `2243947fcd`..`af2c6c1f6f`；`doc/marble-popup-gallery-verification*.md`。其餘彈出層 class 見 `doc/marble-popup-gallery-conclusion.md` |
| 3 | 不成立：六個重型頁面各連跑 10 次全過 | `doc/marble-test-infra-conclusion.md` |
| 4 | 已解決：gallery 改為分區截圖，每區絕對像素容忍；整頁 0.2% 抓不到的 `.z-paging` 紅底，分區後由 `listbox-header-04-grouping.png` 抓到（13,448 px） | `73f20c85b7`（zk-e3）；`doc/marble-popup-gallery-conclusion.md` |
| 1 | 依使用者裁示暫不自動化：改動後在本機跑選擇器的部分測試；選擇器回答全測時，由使用者決定何時跑。Jenkins 計畫暫緩（`doc/marble-test-infra-conclusion.md`）。D23（2026-10-19）：本機加了 Claude Code hook（只在使用者機器上，`.claude/hooks/marble-select-tests.mjs` + `settings.local.json`，不進 git）：session 用 Edit／Write 改到 CSS、預覽頁或 spec，那一輪結束時自動跑選擇器（`--only`，以第一次修改時的 HEAD 為起點），把 partial 指令或 full 的原因交回 session。已實測：主 session 與 sub-agent 的修改都會記錄；還原後不再提醒。不涵蓋：用 Bash 改的檔案、從其他 worktree 開的 session、人手動改 | `58ef1c897b`、`a41956c5e2`、`353f3ba14b`、`24220468d3` |
| 6（品牌色部分） | 已加：`brand` project（`brand-override.spec.ts`），`data-brand="copper"` 下掃描全部頁面的「沒跟著主色變」像素，加衍生色 L／C／H 斷言；現況 113/113 通過，沒有找到寫死主色。限制：靜止畫面、50 頁靜止時沒有主色像素、`color-mix()` 淡色不在家族色內。compact 截圖尚未做 | `173c2a33d9`；`doc/marble-brand-density-conclusion.md`、`doc/marble-brand-density-conclusion.md` |
| 2（第二批） | 剩餘 21 個：截圖 11（6 張新圖）、DOM／computed style 斷言 5、移出 5（實測 ZK 不產生）；`popup` 由 45 增為 56 個測試。tablet 彈出層未量 | `fb36ca7784`；`doc/marble-popup-gallery-conclusion.md`、`doc/marble-popup-gallery-conclusion.md`；遺留見 followups #63 |
| 6（compact 與品牌色截圖） | 已加：`compact` project（15 頁分區截圖、全部頁面兩種密度的控制項高度檢查）、`brand` 加 15 頁分區截圖；150 張新基準。找到 compact 缺陷 8 項（tab、groupbox、spinner、timebox、searchbox、chosenbox、listbox paging、toolbar overflow），記為 KNOWN_DEFECTS，於批次 4 修 | `b5fec2bee5`；`doc/marble-brand-density-conclusion.md`、`-verification.md` |
| 2（tablet） | `tablet-popup` 16 個測試、16 張基準（combobox sheet、datebox／timebox wheel、colorbox、menubar、combobutton、grid／listbox 欄選單、errorbox、notification、window modal、confirmpopup）；同形彈出層（bandbox、chosenbox 等）依 D37 未補 | `da58a01090`；`doc/marble-tablet-popup-batch5-report.md`、`-verification.md`；缺陷見 followups #65 |
| 6（tablet 缺陷修正） | F2、F3、F4、F5、F6、F9 已修；`tablet` 75 個測試（新增 colorbox 錨定、combobox sheet 底緣各一個斷言）；重拍 5 張基準；F3 的 popup 貼合內容寬度 | zk `74fd35c271`、zkcml `234cbfa0f`；`doc/marble-theme-followups.md` #65 遺留：頁面水平溢出時 fixed sheet 落在可見區外、`daterangebox-popup-open-today.png` 待重拍 |
