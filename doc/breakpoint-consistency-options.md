# Responsive breakpoint 斷點一致性：解法評估

來源：`/Users/hawk/Documents/workspace/DOC/meeting/2026-09-30 RD-support marble introduction - meeting conclusion.md` 議題 D2（負責人 Hawk + Peaker）。
會議紀錄本身唯讀，本文件為獨立評估。

## 1. 會議提出的問題

頁面級與元件級 breakpoint 若各有一套數值（例：全域 600px、某元件 500px），客戶改一個值時不知道要改哪裡。初步決議：全部元件參考同一套全域值，特殊斷點作為例外。

## 2. 現況量測（2026-10-13，marble 分支）

- `zul/src/main/resources/web/` 下所有 `@media` / `@container` 條件，扣除 `forced-colors`、`prefers-reduced-motion`、`print`，**只出現在 `zul/css/utility/_layout.css`**：`@media` 4 個、`@container` 4 個，值皆為 600 / 900 / 1200 / 1536 px（MUI 尺度，`doc/spec/responsive-design.md` §5）。
- 元件 CSS（`zul/.../js/zul/<pkg>/css/`）沒有任何寬度型 `@media`。
- `zkpreview/src` 沒有自己的寬度型 `@media`（只有 `responsive.zul` 的說明文字）。
- **更正（2026-10-13，使用者指出 Grid／Listbox 的 `responsive` 屬性後重查）：** 上一版只在 zkcml 搜 `@media`／`matchMedia`，漏掉以 JS 常數實作的斷點。實際上 EE 有**第二套斷點**，見 §2.1。
- CE 的 JS 側沒有 breakpoint 判斷；`ComboWidget.ts`、`messagebox.ts` 用 `jq.innerWidth() - 20` 夾住寬度，不是 breakpoint。
- 限制：`@media` / `@container` 條件**不能讀 `var()`**，所以 breakpoint 不可能做成執行期的 `--zk-*` token（`responsive.zul` 已向使用者說明）。

**對照會議的疑慮：** CE 的 CSS 只有一套值，但 EE 的 Grid／Listbox `responsive` 另有一套，兩者**今天就不一致**（見 §2.1）。

### 2.1 Grid／Listbox `responsive`（since 11.0.0，stacking 僅 EE）

- Java 側（CE：`Grid.java`、`Listbox.java`）：`responsive`（`stacking`／`none`）與 `responsiveColumns`（token 字串，例 `"sm-1 md-2 lg-none"`，預設 `"sm-1 md-none"`），可由 zk.xml library property `org.zkoss.zul.grid.responsive[.columns]` 設定。字串原樣儲存，解析在 client。
- Client 側（EE：`zkcml/zkmax/codegen/resources/web/js/zkmax/grid-responsive.ts`、`listbox-responsive.ts`）：用 `ResizeObserver` 量**元件自己的容器寬度**（不是 viewport），預設斷點 `sm 576 / md 768 / lg 992 / xl 1200 / xxl 1400`（Bootstrap 尺度）。兩個檔案各自定義一份相同常數；可用 `zul.grid.Grid.RESPONSIVE_BREAKPOINTS`、`zul.sel.Listbox.RESPONSIVE_BREAKPOINTS` 在執行期覆寫。
- `zkmax/.../db/css/daterangebox.css:442` 另有 `@media (max-width: 575px)`，同樣是 Bootstrap 的 576 邊界。

| 來源 | 單位基準 | sm | md | lg | xl | xxl |
|---|---|---|---|---|---|---|
| CE utility（`_layout.css`，MUI） | viewport／容器 | 600 | 900 | 1200 | 1536 | 無 |
| EE Grid／Listbox `responsive`（Bootstrap） | 元件容器 | 576 | 768 | 992 | 1200 | 1400 |

名稱相同（sm/md/lg/xl）、數值不同。客戶在 ZUL 同時用 `z-d-md-none`（900）和 `responsiveColumns="md-none"`（768），同一個 `md` 會在不同寬度生效。這正是會議 D2 描述的混淆，而且發生在 CE/EE 之間。

## 3. 可能解法

| # | 解法 | 做法 | 優點 | 代價 |
|---|------|------|------|------|
| A | 單一來源 + 規則 + 守門檢查 | 維持字面 px、全部集中在 `_layout.css`；在 `doc/spec/responsive-design.md` 寫明「元件不得自訂寬度型 `@media`，例外要登記」；加一支 grep 腳本（CI／preview spec）檢查 `@media`/`@container` 寬度值只能是 600/900/1200/1536 | 不增加建置依賴；符合已定的 pure CSS 方向（LESS/DSP 退場）；今天就為真 | 靠守門腳本，不是結構性保證；客戶自訂 CSS 管不到 |
| B | 元件改用 container query，不用 viewport 斷點 | 元件依自己容器寬度調整（`@container`），viewport 斷點只留給頁面級 utility | 從根本避免「兩套斷點」：元件根本不看 viewport；與 `z-grid-fill` 的 intrinsic 取向一致 | 元件仍需一個寬度門檻值，數值一致性問題換了地方出現（仍要 A 的規則）；需 `container-type` 設在父層，可能影響既有版面（containment 會改變 intrinsic 寬度計算） |
| C | 建置時展開（PostCSS `custom-media` 之類） | 一處定義 `--md: 900px`，建置時展開成字面 px | 結構性保證；改值只改一處 | 需引入 PostCSS 外掛；是否符合「無前處理器」決議未確認；與 `.css.dsp` 產生流程的相容性未驗證 |
| D | 另外公開 `--zk-bp-*` token 供 JS／文件讀取 | 在 `:root` 宣告同樣四個值，JS 用 `matchMedia` 讀 | 客戶 JS 與文件可引用同一份值 | `@media` 讀不到，等於**多一份要同步的副本**，反而增加不一致來源；不建議 |
| F | 統一尺度並改 EE 常數 | 選定一組尺度（MUI 或 Bootstrap），同步修改 `grid-responsive.ts`、`listbox-responsive.ts`（兩份常數應收斂為一份）與 `daterangebox.css`；CE `_layout.css` 若選 Bootstrap 則改它 | 結構上解決名稱同、值不同 | 這是行為改變：`responsive` 是 11.0.0 新 API，尚未發佈，現在改成本最低；發佈後改會破壞客戶設定。需與 EE 測試（`IGridRichletTest` 等）一起驗證 |
| E | 只寫文件，不加檢查 | 在規格與使用者文件寫明四個值 | 零成本 | 無偵測；新元件或 EE 元件偏離時不會被發現 |

## 4. 建議

**F（先選定一組尺度，在 11.0.0 發佈前統一）+ A（守門）。B 作為元件層的設計原則。**
理由：現況已經只有一個來源，缺的是「不讓它變多」的規則與偵測；A 成本最低且不增加建置依賴。B 只在元件真的需要「依容器調整」時採用，不為了一致性而全面改寫。C 需要先確認建置鏈與 pure CSS 決議是否相容，列為備選，不先做。D 不建議。

## 5. 待與 Peaker 確認

1. 統一採哪一組：MUI 600/900/1200/1536、Bootstrap 576/768/992/1200/1400，還是會議舉例的 600/800/1200？注意 Grid／Listbox 的 `responsiveColumns` 是「容器寬度」，用 MUI 的 900 會讓多數桌面內嵌的 Grid 一直停在 table 模式，需要 Peaker 確認設計意圖。
2. EE 其他元件是否還有同類 JS 常數或 `@media`？本次只查到 Grid、Listbox、daterangebox；zkex 與其他 zkmax 元件未逐一檢查。
3. 「例外」的登記方式：放在哪個檔案、誰核准。

## 6. 下一步（若採 A）

0. 先與 Peaker 決定尺度（若採 F）；11.0.0 發佈前處理。
1. 在 `doc/spec/responsive-design.md` §5 加上「元件不得自訂寬度型 breakpoint」的規則與例外登記格式。
2. 新增 grep 守門腳本（用 `grep`，不用 `rg`），在 zul + zkcml 範圍驗證寬度值只能是四個值；先在現況跑一次確認為真，再用故意加入 `@media (min-width: 500px)` 的複本確認會失敗。
3. 與 Peaker 同步第 5 節三點。

## 7. Framework 級斷點方案（2026-10-13 補充，取代 §4 建議）

### 7.1 為何 §4 的建議作廢

- §4 建議的「CSS token 讓 JS 讀取」撤回：token 可被客戶覆寫，`@media` 不會跟著變，反而製造執行期不一致的入口。
- §2.1 顯示斷點今天分在 CE CSS（MUI 尺度）與 EE JS（Bootstrap 尺度，且 Grid、Listbox 各一份常數）。需要的是 framework 層級的單一定義，由所有消費者遵守。

### 7.2 設計：一份定義、兩個強制點

| 層 | 內容 | 位置（待確認） |
|---|---|---|
| 定義 | 一份機器可讀檔（名稱 → px），是唯一的正式來源；規格 `doc/spec/responsive-design.md` §5 引用它 | `zk` 模組（依賴順序最底層，zul、zkmax 都看得到）|
| JS 消費 | 以 `zk.breakpoints`（暫名）公開；Grid、Listbox 的 responsive 改讀它，移除各自的 `_DEFAULT_BREAKPOINTS` 與 `RESPONSIVE_BREAKPOINTS` | `zkmax/.../grid-responsive.ts`、`listbox-responsive.ts` |
| CSS 消費 | `@media`／`@container` 仍寫字面 px（CSS 語法限制） | `_layout.css`、`daterangebox.css` 等 |
| 強制點 1：CSS | 在既有 `scripts/build-css.js` 加一個像 `assertLayer()` 的檢查：寬度型 `@media`／`@container` 的值必須是定義檔裡的值，否則建置失敗；`--module zul` 與 `zkmax`／`zkex` 都跑 | `scripts/build-css.js` |
| 強制點 2：JS | 在 `eslint-plugin-zk` 加規則：responsive 相關程式不得自行寫斷點數字常數，必須引用 `zk.breakpoints`；ESLint 錯誤會擋 CI | `eslint-plugin-zk/` |

### 7.3 為何這樣能讓「大家都遵守」

- 新增或修改元件時，違反會在建置或 CI 失敗，不靠人記得。
- CSS 與 JS 的值都來自同一份定義，改值只改一處，兩個檢查會逼另一邊跟上。
- 與 pure CSS 方向相容：不引入 PostCSS 或前處理器，只是在既有 build 加驗證。

### 7.4 限制與取捨

- 客戶不能只靠一個變數同時改 CSS 與 JS 的斷點（CSS 語法限制）。若移除 `RESPONSIVE_BREAKPOINTS` 覆寫，就沒有客戶可調的入口；要不要保留是產品決定（見 D5）。
- 容器級（Grid／Listbox 看容器寬）與 viewport 級（`z-d-md-*`）共用同一組名稱與數值，語意仍不同，規格要寫清楚。
- 以下**尚未驗證**：定義檔放 `zk` 模組後，zkcml 的 `build-css.js` 與 TS 建置是否能讀到；TS 是否能 import JSON；`eslint-plugin-zk` 規則能否可靠辨識斷點常數。

### 7.5 待決策

- D3：統一哪一組尺度（MUI 或 Bootstrap）。
- D4：採本節的「一份定義 + 兩個強制點」。
- D5：是否移除 `RESPONSIVE_BREAKPOINTS` 覆寫（11.0.0 尚未發佈，現在移除不破壞相容性）。

> 後續結論（定義位置、覆寫設計、MUI 與 Bootstrap 查證）見 `doc/breakpoint-unified-spec-proposal.md` §7、§8；§7.5 的 D5 已由該文件 §8.2 修訂。
