# Marble 外觀測試缺口第二輪（計畫，待審）

日期：2026-10-19。來源：`doc/marble-test-gaps.md` 的 #2 剩餘、#12 與 §5 預覽頁缺口、#6；#5（只測 Chrome）本輪只說明、不做（使用者指示）。
執行規則：改動後依 CLAUDE.md 跑 `select-tests.mjs`；partial 本機跑，full 由使用者決定（`353f3ba14b`）。決策編號接續本對話：D24 起。

## 0. 現況（量測／依文件）

| 項目 | 數字 | 依據 |
|---|---|---|
| 基準圖 PNG | 641 張（含 99 張 forced-colors 審查用） | `ls zkpreview/doc/screenshots/*.png`，2026-10-19 |
| 彈出層盤點 | 229 個「靜止時不存在」的 class：popup 53、api 35、variant 110、no-page 10、other 21 | `doc/marble-popup-gallery-conclusion.md` §2（class-index 2026-10-18，NS9 之前） |
| 彈出層第一批 | 20 個元件、45 張截圖 | `doc/marble-popup-gallery-conclusion.md` |
| compact | 只有 `forced-colors.spec.ts`、`mesh-header-footer.spec.ts` 的尺寸斷言設 `data-density="compact"`；沒有截圖 | grep |
| 品牌色 | `component-theme-variables.spec.ts` 測元件層 `--zk-<comp>-*` 覆寫（computed style，0 張截圖）；沒有測試覆寫 `--zk-color-primary` 後的衍生色（`_colors.css` 有 22 處 `oklch(from …)`） | grep |

## 1. 工作 A：彈出層剩餘

### A1. 先量：45 張截圖實際畫出了哪些 class
在目前的 marble 上跑 `popup` project，每個測試截圖前收集可見彈出層內的 class，與盤點的 popup + api（88 個）比對，得出確切的「已涵蓋／未涵蓋」清單。不改基準圖。

### A2. 依文件已知的未涵蓋項（A1 會修正這份清單）

| 群組 | class / 狀況 | 需要 |
|---|---|---|
| combobox | `z-combobox-empty-search-message(-hidden)`、`z-comboitem-image` | `pv/popup-fixtures.zul` 加 fixture（`emptySearchMessage`、帶 image 的 comboitem） |
| menu | 巢狀子選單、`z-menuitem-selected`(?) | 放寬「恰好一個可見」：子選單用最後開啟的那個 |
| popup | hover tooltip、`z-popup-anim-*`(?) | hover 觸發；anim 要先確認截圖穩定（動畫結束後才拍） |
| chosenbox | `z-chosenbox-empty-creatable` | 已在圖內，只補斷言 |
| busy／loading | `z-loading*`、`z-apply-*`、`z-mask`(?) | `loading.zul` 既有按鈕；全頁 busy 有 14 秒 timer，要在出現後立刻拍 |
| modal | `z-window-modal` | fixture 加 `doModal()` 的 window |
| ROD／loadingbar | `z-grid-loading`、`z-listbox-loading`、`z-tree-loading`、`z-loadingbar-position-bottom` | 暫態，先確認能否穩定拍到；不行就只做 DOM 斷言 |
| tablet | 平板版的彈出層 | 放 `tablet` project；規模待 A1 後估 |

驗收：同第一批（不同 process 連跑 2 次全過、不改基準；每個元件至少一個突變會變紅；獨立 verifier 看全部新圖）。

## 2. 工作 B：預覽頁缺口（variant 110 + no-page 10 + `z-navitem-selected`）

### B1. 先量
用目前的 `class-index.json`（NS9 後重產，139 頁）重算盤點 §5.1 的 110 個，扣掉已被後來的頁面改動補上的。標 `(?)` 的先確認 ZK 真的會產生（找不到產生者的歸「其他」，不補頁面）。

### B2. 依類型處理

| 類型 | 數量（盤點） | 作法 | 抓到的方式 |
|---|---|---|---|
| attr（屬性、sclass） | 61 | 在對應預覽頁**最後面**加區段（例：`z-button-fab`、`z-tabbox-vertical`、`z-groupbox-readonly`）。依 `zul-authoring.md` | 分區截圖自動多一區 |
| data（空資料等） | 4 | 同上 | 同上 |
| state（hover、focus、選取） | 30 | 能用屬性固定的（`selected="true"` 的 navitem、`z-navitem-selected`）放預覽頁；只能互動產生的（hover、focus）在 spec 裡做狀態截圖，比照 23 個表單元件的狀態截圖 | 狀態截圖 |
| transient（拖曳中） | 15 | D26 = B：用 `page.mouse` 真實拖曳，在拖曳途中（mouse up 之前）斷言 class 出現與 computed style，不截圖 | DOM／computed style 斷言 |
| no-page | 10 | 新增 `passwordbox.zul`（若 CE/EE 有此元件，需確認）、`skeleton.zul` | 新頁自動進 gallery |

**區段一律加在頁面最後**：分區截圖的檔名是 `<page>-<序號>-<標題>.png`，插在中間會讓後面所有區段的序號位移、基準圖全部改名。

### B3. 規模控制
每頁新增區段後，依 CLAUDE.md 只重拍該頁（selector 會選到該頁）。新基準由獨立 verifier 抽查後才 commit。

## 3. 工作 C：compact 密度與品牌色覆寫

### C1. compact 截圖基準
- 新 Playwright project `compact`：每頁載入後設 `document.documentElement.dataset.density = 'compact'`，用與 gallery 相同的分區截圖。
- 範圍（D25）：代表性頁面約 15 頁（輸入類彙整、button、grid、listbox、tree、tabbox、toolbar、menubar、window、panel、groupbox、paging、navbar、datebox、combobox），或全部頁面（約 400 張新基準）。
- 另加尺寸斷言：各層級控制項高度等於 `_sizing.css` compact 值（不需基準圖，跨平台也成立）。

### C2. 品牌色覆寫
- 新 project `brand`：每頁在 `:root` 設一個非藍色的 `--zk-color-primary`（例：teal），同樣分區截圖代表性頁面。
- **殘留舊藍色掃描**（不需基準圖）：覆寫後截整頁，計算接近預設主色 `rgb(55,111,208)` 及其衍生色的像素數，超過 0 即失敗。這能抓到「某元件把顏色寫死，沒跟著換」，正是 `brand-override.md` 承諾的反面。
- 衍生色斷言：覆寫後 9 個衍生 token 的 computed 值都改變、亮度維持在規格的 tone（92／20 等）。

### C3. 選擇器
新 project 的 describe／title 沿用 `<page>` 命名，`select-tests.mjs` 才能以頁面選到；token 檔（`_sizing.css`、`_colors.css`）改動本來就是 full。

## 4. 建議順序與估計
1. C2 的殘留掃描與衍生色斷言（無基準圖、成本低、直接驗證客戶承諾）
2. A1、B1 兩個量測（只讀，可平行）
3. A2 → C1／C2 截圖 → B2（依量測結果分批，每批先給使用者確認驗證方法）

## 5. 裁示（2026-10-19）
- **D24 = A**：依第 4 節順序；每批開工前先給使用者確認驗證方法。
- **D25 = A**：compact／brand 截圖只取約 15 個代表性頁面；尺寸斷言與殘留掃描跑全部頁面。
- **D26 = B**：transient 15 個要做，只斷言 DOM 與 computed style（拖曳途中），不截圖。B2 表中 transient 一列依此改為「做」。

## 6. 批次 1：品牌色殘留掃描與衍生色斷言（驗證方法，待使用者確認）

### 作法
- 新 spec `brand-override.spec.ts`、新 Playwright project `brand`。覆寫方式用內建 preset `data-brand="copper"`（`_colors.css:227`，seed `#b45309`），即客戶實際的切換方式。
- **殘留掃描（全部頁面，頁面清單同 gallery 掃描，排除 `aggregate-pages.json` 的彙整頁）**：同一頁先截預設，再設 `data-brand` 截第二張（不重載、版面不變）。在執行時從 token 讀出預設的主色家族色值（primary、primary-container、on-primary-container、secondary 及其 container／on-container、inverse-primary、primary-fill、codeeditor focus border）。某像素在預設圖中等於家族色（每通道差 ≤ 2），且在 copper 圖中**完全沒變**，就算殘留。殘留像素數超過門檻即失敗；門檻先量後定（預期為 0）。
- **衍生色斷言（1 個測試）**：copper 下每個衍生 token 的 computed 值都和預設不同，OKLCH 亮度等於規格的 tone（0.92／0.23／0.54／0.87／0.235／0.81，誤差 0.01），色相跟著 seed。
- 範圍限制：只看靜止畫面；彈出層與 hover／focus 狀態不在本批。

### 驗證方法
1. **現況跑一次**：在自己的 worktree 伺服器（p3，8087）對目前 marble 跑，記錄每頁殘留數。殘留不為 0 的，先依 `decision-principles.md` 分類（Marble CSS 寫死 → ZK-6112 修；預覽頁寫死 → 改頁面；合理例外如圖片 → 加入排除清單並寫原因），**先回報使用者再 commit**。
2. **突變測試**：(a) 把 `button.css` 一處 `var(--zk-color-primary)` 改成字面 `#376fd0` → `button.zul` 的殘留掃描變紅，且殘留位置在按鈕上；(b) 把 `_colors.css` 的 primary-container tone 0.92 改 0.80 → 衍生色斷言變紅。還原後全過。
3. **穩定性**：不同 process 連跑 2 次，每頁殘留數相同。
4. **選擇器**：對突變 (a) 跑 `select-tests.mjs`，partial 要選到 `brand` 的 `button.zul` 測試。
5. **獨立 verifier**（另一個 Opus agent，唯讀）檢查 1–4 的證據後才 commit。
- 注意：在 `playwright.config.ts` 加 project，選擇器對該 commit 會判 full；依規則回報，不自行跑全測。

## 7. 量測結果（2026-10-19）
- **A1**（`doc/marble-popup-gallery-conclusion.md`）：88 個 popup + api class，67 個已在 45 張彈出層截圖內可見，21 個沒有（busy／loading 8、ROD／loadingbar 4、combobox 4、menu 2、modal 1、其他 2；其中 7 個標 ?，盤點未找到產生者）。`z-chosenbox-empty-creatable` 已在圖內，只缺斷言。`z-menupopup-separator` 在 DOM 但沒有可見盒子，原因未確認。tablet 未量。
- **B1**（`doc/marble-variant-gap-remeasure.md`）：121 個（variant 110 + no-page 10 + `z-navitem-selected`）目前都不在任何頁面；74 個找得到產生者，47 個沒有（不補頁面）。74 個：attr 28（含 skeleton 6）、tablet 才有的 attr 1、可用 ZUL 釘住的 state 3、需真實互動的 state 26、tablet 才有的 state 2、桌面拖曳 transient 7、tablet 觸控 transient 5、其他 2。
- 與盤點不同：沒有 Passwordbox 元件（密碼欄是 `<textbox type="password">`）；4 個 data 類都沒有產生者；`z-listitem-odd`／`z-row-odd` 實際是 `z-listbox-odd`／`z-grid-odd`（已在索引）；3 個 `*-sizing` 是滑到標題右緣 8px 的 hover 狀態，不是拖曳。
- 預覽頁實際路徑是 `zkpreview/src/main/webapp/web/<page>.zul`。

## 8. 進度
- 批次 1（品牌色殘留掃描與衍生色斷言）：D29 = A，`173c2a33d9` 已在 marble（rebase 到 `2f4bead49a` 後重跑 113/113 通過）。證據：`doc/marble-brand-density-conclusion.md`、`doc/marble-brand-density-conclusion.md`。

## 9. 批次 2：彈出層剩餘 21 個（D30 = A，2026-10-19）
- 輸入：`doc/marble-popup-gallery-conclusion.md`（21 個未涵蓋）、`doc/marble-popup-gallery-conclusion.md`（第一批作法與命名）。
- 步驟：(1) 7 個標 ? 的 class 先在瀏覽器實際觸發確認產生者，不會出現的移出清單並寫原因；(2) 可穩定截圖的放 `popup` project（combobox `emptySearchMessage`／帶圖 comboitem 的 fixture、`loading.zul` 的 busy、`doModal()` window fixture、`z-menupopup-separator` 先查為何沒有可見盒子）；(3) 截不穩的暫態（ROD `z-*-loading` 等）只做 DOM＋computed style 斷言；(4) 補 `z-chosenbox-empty-creatable` 斷言。
- 驗證：不同 process 連跑 2 次全過且不改基準；每個元件至少一個突變變紅；`--only` 選擇器選得到新測試；獨立 Opus verifier 逐張看全部新圖後才請使用者同意 commit。
- 不含：tablet 彈出層（未量）。
- 批次 2（彈出層剩餘 21 個）：D31 = A，`fb36ca7784` 已在 marble（rebase 到 `74d1bcef59` 後 `popup` 56/56）。截圖 11、斷言 5、移出 5（ZK 不產生）。遺留記在 `doc/marble-theme-followups.md` #63（`3d3a102f76`）。

## 10. 批次 3：compact 密度與品牌色截圖（D32 = A，2026-10-19）
- `compact` project：15 個代表頁（textbox 代替彙整頁 inputs.zul——彙整頁不會被元件 CSS 的改動選到（D12）；button、grid、listbox、tree、tabbox、toolbar、menubar、window、panel、groupbox、paging、navbar、datebox、combobox），`data-density="compact"` 下分區截圖。
- 全部頁面的高度斷言：compact 下各級控制項高度等於執行時從 `_sizing.css` 讀出的 compact 值。
- `brand` project 加同 15 頁在 `data-brand="copper"` 下的分區截圖。
- 驗證：雜訊先量後定、不同 process 連跑 2 次、compact 別名突變與衍生色 tone 突變都要讓對應截圖失敗、選擇器選得到、獨立 Opus verifier 看全部新圖後才請使用者同意 commit。

## 11. 批次 4：修正密度缺陷（D34 = A，2026-10-19）
來源：`doc/marble-brand-density-conclusion.md` §補修 的 KNOWN_DEFECTS 與 `doc/marble-brand-density-conclusion.md`。依 `decision-principles.md` §1 歸 Marble，於 ZK-6112 下修。
1. **先分類 panel／window header**（comfortable 56–80 vs 48、64–65 vs 56）：density.md 規定 comfortable 重現今日渲染，先確認是測試的 class→別名對應錯，還是元件沒有綁別名。對應錯 → 改測試；沒綁 → 列為缺陷。
2. **逐項修**：tab（`tabbox.css:210`）、groupbox 預設與 3D mold header（`groupbox.css:26`）、spinner／doublespinner 按鈕欄（`spinner.css:105-140`）、timebox（`timebox.css:130`）、searchbox（zkmax `searchbox.css:63`）、chosenbox（zkmax `chosenbox.css:15,39`）、listbox paging 按鈕（`listbox.css:765-770`）、toolbar overflow（`Toolbar.ts:243/:255` 未算 `toolbar.css:51-52` 的 padding／gap；修法傾向在 CSS 把留白移到 `.z-toolbar` 本身）。
3. **每項**：修 CSS → 從 KNOWN_DEFECTS 移除 → 重切受影響的 compact／gallery 截圖（`--update-snapshots=changed`，限該頁）→ before／after／IceBlue 截圖以 artifact 給使用者（memory：visual issues via artifact）。comfortable 有 1–2px 變化的逐一列出。
4. **驗證**：該項的 compact-height 由 KNOWN_DEFECTS 轉為正常通過；選擇器 partial 在本機跑；獨立 Opus verifier 看全部重切的圖。
- 批次 3（compact 與品牌色截圖、兩種密度的高度檢查）：D33 = A，`b5fec2bee5` 已在 marble（rebase 到 `54deb47a7e`、zkcml `46dccd792` 後 compact+brand 257/257、popup 56/56）。150 張新基準；KNOWN_DEFECTS 60 個 key 交批次 4。
- 批次 4（密度缺陷修正）：D36 = A，zk `9064f77890`、zkcml `fe6323380` 已在兩邊 marble（ff，未 rebase：marble 在驗證後沒有移動）。KNOWN_DEFECTS 清空；comfortable 變動：groupbox header −1px、spinner −3px、toolbar-tabs 載入跳動消失。D35 = A：toolbar 門檻附近殘留 1.9–3.9px 記為 followups #64，不開 Jira。對照圖：https://claude.ai/artifact/E4FU6kMtLuZYQykWU34pgj

## 12. 批次 5：tablet 彈出層（D37 = A，2026-10-20）
來源：`doc/marble-tablet-popup-coverage-measure.md`（NS22）。範圍：有 tablet 專屬 CSS 的彈出層，約 15–16 個測試：combobox bottom sheet、datebox／timebox wheel、colorbox、menu、combobutton、errorbox、notification、window modal、treecol 欄選單、confirmpopup。以真實 `touchscreen.tap` 開啟（只有 API 才能開的另註），放 `tablet` project，截圖加斷言。`pv/popup-api-fixtures.zul` 在 tablet 寬度溢出（1001 > 834），本批修正。
驗證：雜訊（容忍 0，3 個 process）→ 定容忍；不同 process 連跑 2 次全過、既有基準不變；每個元件一個突變（一次一個）；選擇器 `--only` 選得到；獨立 Opus verifier 看全部新圖後才請使用者同意 commit。
- 批次 5（tablet 彈出層）：`da58a01090` 已在 marble。16 個測試／16 張基準，全部以 `touchscreen.tap` 開啟，threshold 0.02；`tablet` 74/74、`popup` 56/56。Marble 的 tablet 缺陷記在 followups #65。
- 工作 B（預覽頁 74 個 class，NS21）：2026-10-20 依使用者指示交給 session zk-13；交接文件 `doc/marble-variant-gap-brief.md`。

## 13. 批次 6：修 tablet 彈出層缺陷（NS23，2026-10-20）
來源：`doc/marble-theme-followups.md` #65（F2 colorpicker 列重疊、F3 colorbox tap 捲動頁面、F4 combobox sheet 末列被切、F5 tablet window/panel header 規則、F6 `.z-treecol-button` 死規則、F9 datebox wheel 接縫）。依 `reference/batch-workflow.md`：先列影響範圍，一次修一項，重切受影響截圖，before／after 以 artifact 給使用者。新 `!important`（E5）或新 token（E4）先問使用者。

### 進度（2026-10-20）
- F2、F3、F4、F5、F6、F9 已修，marble `74fd35c271`（zk）、`234cbfa0f`（zkcml），本地、未 push。獨立 verifier 驗證；`tablet` 75/75；`popup` 56 個測試中有 2 個已知失敗（桌面 datebox timezone 不穩、`daterangebox-popup-open-today.png` 被 marble `76b51e9e6` 改變，屬另一個 session）。
- D39 = A（F3 三個 `!important`）、D40 = A（commit）。F3 的寬度依使用者意見改為貼合內容（`max-width: fit-content` + `margin-inline: auto`），不是全寬 sheet。F4 的 fixture 增為 8 項。
- rebase 到 marble 時 `colorbox-tablet-popup-picker.png` 因 marble `4d02e9c9db`（`input.css` vertical-align）重拍；`tree.css` 衝突已合併，並一併清掉 `:has(.z-treecol-button)`。
- 遺留（記在 followups #65）：fixed 底部 sheet 在頁面水平溢出時落在可見區外（D41，待使用者看 artifact）、三個 sheet 都沒有滑入動畫、340px 以下 colorbox 水平捲動、`daterangebox-popup-open-today.png` 待重拍。
