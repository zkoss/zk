# Framework 斷點單一規格提案：utility 與 Grid／Listbox responsive

承接 `doc/breakpoint-consistency-options.md`（來源：會議紀錄議題 D2，`2026-09-30 RD-support marble introduction - meeting conclusion.md`，該紀錄唯讀）。
目標：把有衝突的兩份規格並排呈現，找出 framework 一致的單一規格，不讓使用者適應兩套。

## 1. 衝突範圍（縮小後）

| 規格 | 是否用斷點 | 是否與另一份衝突 |
|---|---|---|
| utility `z-d-{value}-{bp}`（viewport，`@media`）| 是 | **衝突方 A** |
| utility `z-cq-{value}-{bp}`（容器，`@container`）| 是 | **衝突方 A** |
| utility `z-grid-fill`、`z-grid-cols-auto` | 否（intrinsic／捲動容器）| 無 |
| Grid／Listbox `responsive` + `responsiveColumns`（EE client，容器寬度）| 是 | **衝突方 B** |
| `daterangebox.css` `@media (max-width: 575px)`（EE）| 是 | 第三個消費者，數值屬 B 的尺度 |

utility 是選配，使用者可不用；但 Grid／Listbox 的屬性是內建行為，兩者出現在同一頁時 `md` 會指向不同寬度。

## 2. 兩份規格並排

| 項目 | A：utility（CE） | B：Grid／Listbox responsive（EE） |
|---|---|---|
| 尺度來源 | MUI（`doc/spec/responsive-design.md` §5）| Bootstrap 風格（`grid-responsive.ts`、`listbox-responsive.ts`）|
| xs | 0（base）| 0（隱含，無 token）|
| sm | 600 | 576 |
| md | 900 | 768 |
| lg | 1200 | 992 |
| xl | 1536 | 1200 |
| xxl | 無 | 1400 |
| 比對基準 | viewport（`@media`）與容器（`@container`）| 容器（`ResizeObserver`，border-box 寬）|
| 比較方向 | mobile-first，`>=` | `>=` |
| 定義位置 | CSS 字面值，`_layout.css` | JS 常數，Grid、Listbox 各一份 |
| 能否調整 | 不能（`@media` 不能用 `var()`）| 能（`RESPONSIVE_BREAKPOINTS` 執行期覆寫）|

觀察：
- 名稱相同、數值不同，連語意都錯位：B 的 `xl`(1200) 等於 A 的 `lg`(1200)。
- 比較方向、`xs` 的處理一致，差別只有數值、`xxl` 與可調整性。
- A 本身已有容器版（`z-cq-*`，§5.2）且與 viewport 版共用同一組值。所以 framework 規格的形狀已經是「一組尺度、兩種比對基準（viewport／容器）」，Grid／Listbox 只是第三個、用容器基準的消費者。

## 3. 單一規格提案

**一組名稱與數值；viewport 與容器只是比對基準不同。** 全 framework（CE utility、EE Grid／Listbox、daterangebox）都引用這一組。

建議採 MUI 尺度：`xs 0 / sm 600 / md 900 / lg 1200 / xl 1536`。理由：
1. Marble 對齊 MD3／MUI 是已定的方向（`responsive-design.md` §1），且 CE 已有 423 個 `z-*` utility 與預覽頁、規格使用這組值。
2. `responsive` 是 11.0.0 新 API、尚未發佈，改它不破壞相容性；改 CE 則要動已完成的規格與預覽。
3. 與 MD3 window size class（600／840／1200／1600）相近。

代價（需要 Peaker 確認）：
- `xxl` 消失：`responsiveColumns` 的 token 集合 `{sm,md,lg,xl,xxl}` 要改成 `{sm,md,lg,xl}`，Java 文件、解析 regex（`(sm|md|lg|xl|xxl)-(\d+|none)`）、預設值說明都要同步。
- 預設 `"sm-1 md-none"` 的切換點從 768 變 900：容器寬 768–899 的 Grid 原本是 table、之後變成 stacking。這是行為變化，要用實際版面確認這個區間是否合理。
- `daterangebox.css` 的 575px 要對應到新的 `sm`（600 → `max-width: 599px`）。

## 4. 能否動態改變

- CSS：不能。`@media`／`@container` 條件不能用 `var()`；要改值只能改 CSS 後重建。
- JS：現在能（`RESPONSIVE_BREAKPOINTS`）。這是唯一能讓兩者在執行期分岔的入口，所以單一規格方案建議移除它（11.0.0 未發佈）。
- 由此，「使用者不必適應兩套」的前提是：斷點值是 framework 常數，不是設定。

## 5. 強制「大家都遵守」

沿用 `breakpoint-consistency-options.md` §7：一份正式定義（`zk` 模組）；`scripts/build-css.js` 檢查寬度型 `@media`／`@container` 的值；`eslint-plugin-zk` 規則禁止自寫斷點常數。以下**尚未驗證**：zkcml 能否讀到定義檔、TS 能否 import、ESLint 規則能否辨識。

## 6. 需要決議的事項

> **狀態（2026-10-13）：** D3、D4、D6 已決，D5、D7、D8 暫不做；結果見 §9。以下為決議前的分析，保留作為依據。

編號只在本文件內有效（引用時請寫 `breakpoint-unified-spec-proposal.md D3`）；會議紀錄與 `breakpoint-consistency-options.md` 另有各自的 D 編號。
對照：討論初期的「選哪種解法」已由 D4、D7 取代，「斷點值是什麼」即 D3。

| 編號 | 要決定什麼 | 建議 | 誰決定 | 前置條件 |
|---|---|---|---|---|
| D3 | 全 framework 採哪一組尺度 | MUI | Hawk + Peaker | 無（但需用實際版面看行為變化）|
| D4 | 如何強制所有消費者遵守 | 建置檢查 + ESLint 規則 | Hawk | 先完成 D7 的驗證 |
| D5 | JS 斷點是否允許使用者覆寫 | 保留，並加驗證 | Hawk + Peaker | 無 |
| D6 | 是否保留 `xxl` | 不保留 | Hawk + Peaker | 取決於 D3 |
| D7 | 正式定義放哪一種檔案 | 中立 JSON，CSS 只被檢查 | Hawk | 需驗證三件事（見 D7）|
| D8 | 覆寫入口的層級與優先順序 | 全域 + 元件兩層 | Hawk + Peaker | 與 D5 一起決定 |

決定順序：D3 → D6；D7 → D4；D5 與 D8 一起。時間點：`responsive` 是 11.0.0 新 API（`Grid.java`、`Listbox.java` 標 `@since 11.0.0`），本文假設它在 11.0.0 發佈前尚未對外；發佈後再改任何數值或移除 `xxl` 都會破壞客戶設定。這個假設未向 release 流程確認。

### D3：全 framework 採哪一組尺度

**背景：** CE utility（`_layout.css`）用 MUI 尺度，EE Grid／Listbox `responsive`（`grid-responsive.ts`、`listbox-responsive.ts`）與 `daterangebox.css` 用 Bootstrap 尺度。名稱相同、數值不同，且名稱錯位（Bootstrap `xl` 1200 = MUI `lg` 1200）。

**選項：**

| 選項 | 值 | 要改的地方 | 使用者可見的行為變化 |
|---|---|---|---|
| A. MUI（建議）| xs 0 / sm 600 / md 900 / lg 1200 / xl 1536 | EE：兩個 `*-responsive.ts` 常數、`daterangebox.css`、`Grid.java`／`Listbox.java` 的 Javadoc 與 token 說明 | 見下表 |
| B. Bootstrap | xs 0 / sm 576 / md 768 / lg 992 / xl 1200 / xxl 1400 | CE：`_layout.css` 8 個區塊、`responsive-design.md`、`utility-naming.md`、`zkpreview/.../responsive.zul`，以及用到這些 utility 的頁面與截圖基準（影響數量未盤點）| utility 的切換點從 900 變 768 等 |
| C. 參考 M3 window size class | 600 / 840 / 1200 / 1600 | 同 A，另改 CE | M3 的值是 Android dp，web 版是否有對應規格未查到 |
| D. 自訂折衷值 | — | 兩邊都改 | 變成第三套，不建議 |

**選項 A 的行為變化（Grid／Listbox 以容器寬度判斷，取「不超過容器寬度的最大斷點」）：**

| 容器寬度 | Bootstrap（現況）| MUI | 預設 `"sm-1 md-none"` 的影響 |
|---|---|---|---|
| 576–599 | sm | xs | 無（xs 取最小 token 值，仍為 1 欄 stacking）|
| 768–899 | md | sm | **table → stacking** |
| 992–1199 | lg | md | 無（預設只有 sm、md）|
| 1200–1535 | xl | lg | 無 |
| ≥ 1400 | xxl | （無 xxl）| 無 |

只有使用者自己寫了含 `lg`／`xl` 的 `responsiveColumns`（例 `"sm-1 md-2 lg-none"`）時，992–1535 之間的行為才會改變。**更正：** 先前說「只有 768–899 受影響」只適用預設 token。

**為何建議 A：**
1. Marble 對齊 MD3／MUI 是已定方向，CE 已有 423 個 `z-*` utility 與規格、預覽頁使用這組值。
2. 600／900／1200 是整百，好記；Bootstrap 的 576、992 不好記。
3. 改的是尚未發佈的 `responsive`，不破壞相容性；選 B 要動已完成的 CE。
4. 查證結果：兩套都沒有統計依據（Bootstrap 依「容器寬度為 12 的倍數」與常見裝置；MUI 官方未說明），沒有證據顯示 Bootstrap 更貼近實際裝置（§8.3）。

**需要 Peaker 確認：** 設計 `responsiveColumns` 時選 Bootstrap 尺度是否有特別理由；768–899 的 Grid 在 stacking 下的版面是否可接受。

**決定後的連動：** D6（`xxl`）、D7（定義檔的內容）、`responsive-design.md` §5。

### D4：如何強制所有消費者遵守

**背景：** 即使 D3 統一了數值，下一個新元件、EE 元件或 Theme Pack 仍可能自己寫一個 `@media (min-width: 500px)` 或自己的 JS 常數。

**選項：**

| 選項 | 內容 | 代價 |
|---|---|---|
| A. 兩個檢查（建議）| ① `scripts/build-css.js` 新增檢查（比照 `assertLayer()`）：寬度型 `@media`／`@container` 的值必須出現在定義檔，否則建置失敗，`--module zul` 與 `zkmax`／`zkex` 都跑；② `eslint-plugin-zk` 新增規則：responsive 相關程式不得自寫斷點數字常數 | 兩個檢查都要實作並各用一個故意違規的複本驗證會失敗；ESLint 規則能否可靠辨識常數**未驗證** |
| B. 只做 CSS 檢查 | 只做 ① | JS 側仍可能再分岔 |
| C. 只寫文件 | 規格寫明 | 沒有偵測 |

**範圍限制：** 檢查只管我們出貨的檔案；客戶自己的 CSS、Theme Pack／自訂主題作者重新建置 `_layout.css` 是否經過檢查，**未查**（需看 Theme Pack 產生流程）。
**排除的檢查範圍：** `forced-colors`、`prefers-reduced-motion`、`print` 不是寬度型，不檢查。

### D5：JS 斷點是否允許使用者覆寫

**背景：** 現在 `RESPONSIVE_BREAKPOINTS` 可在執行期覆寫；CSS 的 `@media` 不能。覆寫後 JS 與 CSS 會分岔，但這是使用者主動的行為。

**選項：**

| 選項 | 內容 | 代價 |
|---|---|---|
| A. 保留並加驗證（建議，採納 2026-10-13 討論）| 預設值一致即不會讓使用者感覺有兩套；覆寫者自行承擔對應 CSS；載入時驗證遞增，不合法退回預設並警告；文件寫明只影響 JS 元件，並附「讓 CSS utility 對應自訂斷點」的做法（使用者自寫 `@media`）| 使用者改了 JS 後，CSS utility 不會跟著變，只能靠文件說明 |
| B. 移除 | 斷點只有一個來源 | 客戶沒有調整 Grid 斷點的入口 |

**需補完的細節：** 現有程式只防 `undefined`（`grid-responsive.ts` 的合併邏輯），不驗證順序；`md: 500, sm: 600` 這類倒序會讓模式判斷出錯。

### D6：是否保留 `xxl`

**背景：** Bootstrap 尺度有 `xxl` 1400；MUI 尺度沒有（最大是 `xl` 1536）。`responsiveColumns` 的 token 集合與解析 regex（`(sm|md|lg|xl|xxl)-(\d+|none)`）、`Grid.java` Javadoc 都含 `xxl`。

**選項：**
- **A. 不保留（建議，限於 D3 選 A）：** token 集合改為 `{sm,md,lg,xl}`；regex、Javadoc 同步；從 Bootstrap 文件學到 `xxl` 的使用者不能再用，超過 1536 的寬螢幕沒有獨立檔位。
- **B. 保留並自訂一個值（例如 1920）：** 要在 CE `_layout.css` 補 `xxl` 的 `@media` 與 `@container` 區塊，並新增對應 utility class（數量未盤點）；多出一個 MUI 沒有的值，削弱「對齊 MUI」的理由。
- 若 D3 選 B（Bootstrap），則 `xxl` 自然保留，本項不需決定。

### D7：正式定義放哪一種檔案

**背景：** 斷點不能宣告成 CSS 變數（`@media` 讀不到 `var()`，改了無效，反而誤導）；因此「CSS 為準」已從所有選項移除（§7.1）。

**選項：**
- **A. 中立 JSON 為準，JS 執行期使用，CSS 只被檢查（建議）：** CSS 原始碼維持手寫純 CSS；數字在 JSON 與 CSS 各出現一次，由 D4 的檢查綁住。
- **B. 中立 JSON 為準，建置時產生 CSS 與 TS：** 結構上只有一處數字；CSS 原始碼要寫佔位符，不再是純 CSS，需先確認是否與 pure CSS 方向衝突。

**選定前需驗證的三件事（均未驗證）：**
1. TS 能否 import 該 JSON（`tsconfig` 的 `resolveJsonModule` 與 webpack 設定）。
2. `../zkcml` 的 `scripts/build-css.js` 能否讀到放在 `zk` 模組的檔案（zkcml 依賴 zk 的路徑）。
3. `zk` 模組是否為合適的位置（依賴順序最底層，zul、zkmax 都能引用）。

### D8：覆寫入口的層級與優先順序

**背景：** 現有 `Grid.RESPONSIVE_BREAKPOINTS` 與 `Listbox.RESPONSIVE_BREAKPOINTS` 各自獨立，只改一個會讓同一頁兩個元件斷點不同。

**選項：**
- **A. 全域 + 元件兩層並存（建議，與 D5 A 一起）：** 全域入口（暫名 `zk.breakpoints`）提供預設，Grid、Listbox 都繼承；元件層的 `RESPONSIVE_BREAKPOINTS` 有設才蓋過。優先順序：元件層 > 全域 > 定義檔預設。代價：名稱像全域，使用者易誤以為 CSS 也跟著變，需要文件與命名說明範圍。
- **B. 只保留元件層：** 名稱精確反映範圍；代價是要改兩處，容易漏改。
- **C. 只有全域：** 不能單獨調整某個元件。

**未定義的細節：** 覆寫必須在 widget 綁定前設定；在哪裡設定（`zk.xml`、頁面 `<script>`）、現有 ZK 是否已有適合的機制，**尚未查**。

## 7. 定義位置與客製化（補充）

前面各節只寫「放 `zk` 模組的一份定義」，沒有回答「是 CSS 還是 JS」。本節補上。

### 7.1 定義放哪一邊

| 方案 | 說明 | 結論 |
|---|---|---|
| CSS 為準，JS 用 `getComputedStyle` 讀 `--zk-bp-*` | `@media` 讀不到 `var()`，CSS 仍要字面值；token 可被客戶覆寫，JS 變了 CSS 不變 | 不採用 |
| JS 為準，CSS 另寫字面值 | 現況（Grid、Listbox 各一份）；CSS 讀不到 JS | 不採用（現況的問題）|
| 中立定義檔（JSON）為準；JS 在執行期使用，CSS 只被**驗證**不被產生 | `scripts/build-css.js` 檢查寬度型 `@media`／`@container` 的值必須在該檔內；TS 引用同一檔 | **建議** |
| 中立定義檔為準，建置時**產生** CSS 與 TS | CSS 原始碼要寫佔位符，不再是純 CSS | 備選；需確認與 pure CSS 決議是否相容 |

**明確決定：不宣告 `--zk-bp-*` CSS 變數。** 斷點的主要用途是 `@media`／`@container` 條件，它們讀不到 `var()`，所以改變數不會改變 CSS 的切換點；使用者會以為改了有效，實際無效，比沒有這個變數更誤導。其餘用途都不成立：給 JS 讀會讓 JS 與 CSS 分岔；一般宣告裡用（例 `max-width`）是寬度尺寸，不是斷點，不該借用 breakpoint 之名；當唯讀文件則只是會過期的副本，且可被覆寫。因此上表「CSS 為準」方案從所有選項中移除，斷點只有中立定義檔這一個來源。

建議方案的結果：CSS 原始碼維持手寫的純 CSS；JS 與 CSS 的值由建置檢查綁在同一份定義上。**未驗證**：TS 能否 import 該 JSON；zkcml 的 `build-css.js` 能否讀到 `zk` 模組內的檔案。

### 7.2 客製化：分三種需求

| 需求 | CSS（utility）| JS（Grid／Listbox）|
|---|---|---|
| 選擇用哪些斷點 | 使用者決定要用哪個 class（`z-d-md-none` 等）| `responsiveColumns` 的 token（例 `"sm-1 md-2 lg-none"`），每個元件各自決定，不改尺度本身 |
| 改變斷點的數值 | 不支援。`@media` 不能用 `var()`，使用者要自己寫 `@media` 規則 | 現況可用 `RESPONSIVE_BREAKPOINTS` 覆寫；統一規格下**建議移除**（D5）|
| 只讓某個元件不響應 | 不使用該 utility | `responsive="none"` |

- 結論：在單一規格下，斷點「值」是 framework 常數；使用者客製的是「選哪些」，不是「值是多少」。
- 若保留 `RESPONSIVE_BREAKPOINTS`（D5 選項 B），使用者改了 JS 值後 CSS utility 不會跟著變。這是文件必須寫明的限制，也是不推薦的原因。
- 若使用者確實需要自己的斷點：在自己的 CSS 寫 `@media`，在自己的程式用自己的常數。這是在 framework 規格之外，由使用者負責一致性。
- 另一個可能的擴充：`responsiveColumns` 接受數值 token（例 `w480-1`）。它讓單一元件有自己的門檻、不動尺度，但會讓元件出現第三種斷點值，與「單一規格」的目標衝突，**不建議**。
- **未確認**：Theme Pack／自訂主題的作者是否能重新建置 `_layout.css`。若能，他們改了值就不會經過我們的建置檢查。需要查 Theme Pack 的產生流程才能回答。

## 8. 使用者角度的問題與修訂（2026-10-13 補充）

### 8.1 使用者可能遇到的問題

1. **兩個覆寫入口：** `Grid.RESPONSIVE_BREAKPOINTS` 與 `Listbox.RESPONSIVE_BREAKPOINTS` 各自獨立，只改一個，同一頁兩個元件斷點不同。
2. **覆寫範圍不明：** 覆寫只影響用 JS 量寬度的元件，不影響 CSS utility；名稱若像全域，使用者會誤以為 CSS 也變。
3. **容器寬度不是視窗寬度：** Grid／Listbox 的 `md` 看容器寬，視窗夠寬但容器被側欄壓窄時仍會 stacking，文件要明說。
4. **預設行為變化：** 統一成 MUI 後，容器寬 768–899 的 Grid 從 table 變 stacking。
5. **`xxl` 消失：** 從 Bootstrap 文件學到 `xxl` 的使用者不能再用。
6. **覆寫值沒有驗證：** 現有程式只防 `undefined`；`md: 500, sm: 600` 這類倒序會讓模式判斷出錯。載入時應驗證遞增，不合法退回預設並警告。
7. **邊界互補：** `daterangebox.css` 用 `max-width: 575px`；統一後 `max-width` 與 `min-width` 要精確互補（599／600），避免縮放下出現空隙。
8. **設定時機未定：** 覆寫須在 widget 綁定前設定；在哪裡設定（zk.xml、頁面 `<script>`）尚未定義，也未查現有機制。

### 8.2 修訂後的方案（D5、D8）

- **預設值：** 由中立定義檔提供，`build-css.js` 檢查 CSS、`eslint-plugin-zk` 檢查 JS，使預設時全 framework 只有一套（D4、D7）。
- **覆寫：** 保留，採兩層。全域入口（暫名 `zk.breakpoints`）提供預設，Grid、Listbox 都繼承；元件層的 `Grid.RESPONSIVE_BREAKPOINTS`、`Listbox.RESPONSIVE_BREAKPOINTS` 有設才蓋過全域。
- 覆寫只影響 JS 元件，不影響 CSS utility；文件明寫，並附「如何讓 CSS utility 對應自訂斷點」的做法（使用者自寫 `@media`）。
- 載入時驗證遞增。
- 這取代 §6 原本的「移除覆寫」建議：使用者的覆寫是明確的主動行為，預設值一致已解決「被迫適應兩套」。

### 8.3 MUI 與 Bootstrap 數值的查證（2026-10-13）

| 來源 | 值 | 數值依據 |
|---|---|---|
| Bootstrap 5.3 | 576 / 768 / 992 / 1200 / 1400 | 官方文件：為「comfortably hold containers whose widths are multiples of 12」，並「representative of a subset of common device sizes」；**未引用任何統計或裝置調查** |
| MUI | 600 / 900 / 1200 / 1536 | 官方文件**未說明理由**，僅稱是原始 Material Design 規格的 simplified implementation |
| Tailwind | 640 / 768 / 1024 / 1280 / 1536 | 未查依據 |
| Material 3 window size class（Android dp，非 web 規格）| 600 / 840 / 1200 / 1600 | Android 文件；單位是 dp，不可直接比 |

- 「multiples of 12」指的是容器寬度，不是斷點本身（992、1400 不是 12 的倍數）。容器寬度 540／720／960／1140／1320 為記憶，**未於本次驗證**。
- 搜尋結果顯示 Bootstrap 3 從 768 起，Bootstrap 4 才加入 576；992、1200 為何不是 960、1280，沒有找到權威說明。
- **查不到**哪一種斷點被較多專案採用的可靠統計。npm 下載量比較的是函式庫，不是斷點，不採用為依據。
- 結論：兩套都是「大致合理」的約定值，沒有證據顯示 Bootstrap 更貼近實際裝置。選擇依一致性與好記程度。MUI 的 600／900／1200 為整百，較好記；Marble 對齊 MD3／MUI 且 CE 已使用。折衷出新數值只會變成第三套，不建議。D3 維持建議 MUI。
- 若要參考 M3 的 840，需先確認 web 版有無對應規格（未查到）。

Sources：https://getbootstrap.com/docs/5.3/layout/breakpoints/ 、https://mui.com/material-ui/customization/breakpoints/ 、https://ricostacruz.com/til/css-media-query-breakpoints 、https://developer.android.com/develop/ui/compose/layouts/adaptive/window-size-classes 、https://sizzy.co/blog/tailwind-responsive-breakpoints

## 9. 最終決議（2026-10-13，Hawk）

| 編號 | 決議 | 備註 |
|---|---|---|
| D3 尺度 | **採 Bootstrap** | sm 576 / md 768 / lg 992 / xl 1200（xs 0），不含 xxl |
| D6 `xxl` | **不保留**：從尺度中刪除 | 由 Peaker 以 Jira Feature Request 實作；本 session 不建立 FR、不改 EE 程式（D9）|
| D4 強制方式 | **只做建置檢查**：在 `build-css.js` 檢查 CSS 的斷點值與 JS 一致 | 不做 ESLint 規則 |
| D5 覆寫 | 暫不做 | 維持現況：`Grid.RESPONSIVE_BREAKPOINTS`、`Listbox.RESPONSIVE_BREAKPOINTS` 各自存在、無驗證 |
| D7 定義位置 | 暫不做 | 不新增 JSON；現階段 JS 常數就是「JS 這一邊的值」 |
| D8 覆寫入口 | 暫不做 | 不新增 `zk.breakpoints` |

### 9.1 這個決議實際要改什麼

**因為採 Bootstrap，CE 要改（EE 的 JS 數值不變）：**
- `zul/src/main/resources/web/zul/css/utility/_layout.css`：4 個 `@media` 與 4 個 `@container` 區塊，值由 600/900/1200/1536 改為 576/768/992/1200。區塊數量不變（sm/md/lg/xl 各一個），所以只是改數值。
- `doc/spec/responsive-design.md` §5（含「breakpoint 尺度」一句與 §5.2）、`doc/spec/utility-naming.md` 中引用 MUI 尺度的地方。
- `zkpreview/src/main/webapp/web/utility/responsive.zul` 的說明與範例。
- 用到 `z-d-{bp}`／`z-cq-{bp}` 的預覽頁與 Playwright 截圖基準：**影響數量未盤點**，改完要重跑並檢查 `git status` 確認哪些基準變動。

**因為不保留 `xxl`，EE 要改：**
- `grid-responsive.ts`、`listbox-responsive.ts`：刪除 `xxl: 1400` 與 `_BP_ORDER` 中的 `xxl`、`classify` 函式的 `xxl` 分支、token 解析 regex 改為 `(sm|md|lg|xl)-(\d+|none)`。
- `Grid.java`、`Listbox.java` 的 Javadoc（token 集合 `{sm, md, lg, xl, xxl}` 改為 `{sm, md, lg, xl}`）。
- 既有測試與範例若含 `xxl`（`zephyr-test` 的 `IGridRichlet`、`IListboxRichlet` 有提到 responsive）：**未逐一檢查**。

**不需要改：** `daterangebox.css` 的 `max-width: 575px`（與 Bootstrap `sm` 576 互補，目前已符合）。Grid／Listbox 的預設行為不變（768 起 table，與現況相同）。

### 9.2 建置檢查的設計與限制

- 位置：`scripts/build-css.js`。這支腳本在 zk repo，但已經用 `ZKCML_ROOT = ../zkcml` 讀取 EE 檔案，並支援 `--module zkmax|zkex`。
- **檢查必須放在 `--module zkmax` 的執行裡**：JS 常數在 zkcml（`zkmax/.../grid-responsive.ts`），CE 的 `--module zul` 看不到它，也不應該依賴 EE（zk 是公開 repo）。
- 檢查內容：
  1. 取出 `grid-responsive.ts` 與 `listbox-responsive.ts` 的 `_DEFAULT_BREAKPOINTS`，兩者必須彼此相等。
  2. 取出 CE `_layout.css` 與 EE CSS（例如 `daterangebox.css`）所有寬度型 `@media`／`@container` 的 px 值，每個值都必須是上述斷點之一（或其邊界互補值，例如 `max-width: 575px` = 576 − 1）。
  3. 不符則建置失敗，比照 `assertLayer()`。
- **未驗證／待確認：**
  - 以 regex 從 TS 取出常數的可靠度（目前兩檔格式相同，但格式一變就失效）。
  - 邊界互補值（`max-width: 575px`）的判斷規則要先寫清楚。
  - 檢查要先對現況跑一次確認為真，再用故意改錯的複本確認會失敗（遵守工作守則：驗證子句要能偵測它聲稱的失敗）。
  - 沒有 zkcml 的環境（只建 CE）不會執行這個檢查，所以 CE 單獨出貨的 `_layout.css` 若被改錯，只有在 EE 建置時才會發現。

### 9.3 暫不做的部分，留下的風險

- **JS 與 CSS 的值只在建置時被比對，執行期的覆寫（`RESPONSIVE_BREAKPOINTS`）不受管**：使用者覆寫後 JS 與 CSS 仍會分岔，且覆寫值沒有驗證遞增。
- **Grid 與 Listbox 仍各有一個覆寫入口**，只改一個會讓兩者不同。
- **新元件自寫斷點數字常數不會被偵測**（沒有 ESLint 規則）；只有 CSS 的 `@media` 被檢查。
- **斷點仍在兩個 TS 檔各寫一份**，只由檢查確保相等，不是單一來源。
- 以上屬刻意延後；若之後要做，D5、D7、D8 的分析仍在 §6–§8。

### 9.4 D9：刪除 `xxl` 的 Feature Request（2026-10-13，Hawk）

- **決議：** 刪除 `xxl` 這個尺度；發一張 Jira Feature Request，**由 Peaker 實作**。
- **本 session 不處理：** 不建立該 Jira、不修改 `grid-responsive.ts`、`listbox-responsive.ts`、`Grid.java`、`Listbox.java`。
- **FR 應涵蓋的範圍**（供發 FR 的人使用，內容與 §9.1「因為不保留 `xxl`，EE 要改」相同）：
  - `grid-responsive.ts`、`listbox-responsive.ts`：移除 `xxl: 1400`、`_BP_ORDER` 的 `xxl`、斷點判斷函式的 `xxl` 分支，token 解析 regex 改為 `(sm|md|lg|xl)-(\d+|none)`。
  - `Grid.java`、`Listbox.java` Javadoc：token 集合改為 `{sm, md, lg, xl}`。
  - `zephyr-test` 等既有測試與範例若含 `xxl` 需一併更新（未逐一檢查）。
- **與建置檢查的關係：** `build-css.js` 的檢查（§9.2）以 JS 常數為比對對象；`xxl` 刪除前，JS 常數仍含 1400，檢查需決定是否容許它，或等 FR 完成後再啟用。
- 先前草稿（「新增 `xxl`」）已作廢，因為決議是刪除而非加回。

### 9.5 實作狀態（2026-10-13，工作樹，未 commit）

已完成（CE，`zk` repo）：
- `zul/.../css/utility/_layout.css`：8 個 `@media`／`@container` 區塊改為 576 / 768 / 992 / 1200，註解改為 Bootstrap 尺度並註明建置檢查。
- `zkpreview/.../utility/responsive.zul`、`zkpreview/src/test/playwright/responsive-utilities.spec.ts`：數值、說明文字與測試視窗寬度（md 區 850、lg 區 1100、xl 區 1300）同步。
- `doc/spec/responsive-design.md` §5、`doc/spec/utility-naming.md`：尺度改為 Bootstrap。
- `scripts/check-breakpoints.js`（新）+ `scripts/build-css.js`：`--module zkmax` 時檢查，不符則建置失敗。

檢查規則：Grid 與 Listbox 的 `_DEFAULT_BREAKPOINTS`（sm/md/lg/xl）必須相等；zul、zkmax、zkex 所有 CSS 的寬度條件必須是這四個值（`min-width: N`、`width >= N`），或其互補（`max-width: N-1`、`width < N`）。`xxl` 不檢查（D6／D9）。

驗證（結果）：
- 現況檔案：掃到 9 個寬度條件，0 個錯誤。
- 7 個複本情境皆符合預期（用 `git show HEAD:` 的舊 MUI `_layout.css`、多一個 500px 查詢、Grid 或 Listbox 的 md 改 800、`max-width: 574px`、`@container (min-width: 900px)`）。
- `build-css.js --module zul` 與 `--module zkmax` 以 `--out` 指向暫存目錄建置成功；`norm.css.dsp` 內為 `width>=576px / 768px / 992px / 1200px`。
- 接線：以預載腳本強制檢查回報錯誤時，`--module zkmax` 建置失敗（exit 1），`--module zul` 不受影響。

**未驗證：**
- Playwright `responsive-utilities.spec.ts` 尚未執行：需要用新 CSS 啟動預覽站，8085 是共用伺服器，且不可在執行中的預覽站上重建 jar。
- 用到 `z-d-{bp}`／`z-cq-{bp}` 的其他預覽頁與截圖基準是否受影響，未盤點。
- 刪除 `xxl`（`grid-responsive.ts`、`listbox-responsive.ts`、Java Javadoc）不在本 session，由 Peaker 以 Feature Request 實作（D9）。
