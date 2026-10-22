# Responsive Design 解法（Marble）

本文件說明 Marble 目前的響應式版面做法、設計取捨，以及如何使用。

> **互動式教學頁**（由淺入深 + 企業應用範例）：啟動 preview app 後開啟
> <${PREVIEW_URL}/utility/grid-layout.zul>（原始檔 `zkpreview/src/main/webapp/web/utility/grid-layout.zul`）。

## 0. TL;DR

| 層次 | 是什麼 | 用在哪 | 現況 |
|------|--------|--------|------|
| **通用 utility** | `.z-grid-fill`（auto-fit grid，無 breakpoint） | App／預覽頁中**彼此獨立**的卡片牆、儀表板磚、產品列表 | 已定義於 `_layout.css`、編譯進 `norm.css.dsp`；**尚未**用在任何 ZUL（預留給開發者） |
| **通用 utility** | `.z-grid-cols-auto`（auto 標籤欄 + N 個撐滿欄，`--zk-cols` 控制欄數、`--zk-col-min` 控制最小欄寬） | 「標籤 + N 個需跨列對齊的值」版面：狀態／變體矩陣、比較表、規格／定價表 | 已定義於 `_layout.css`、編譯進 `norm.css.dsp`；**預覽頁的 State Matrix 已遷移為直接使用它** |

兩個通用工具各司其職：`z-grid-fill` 用在**彼此獨立**的卡片（欄數隨寬度自動增減）；`z-grid-cols-auto` 用在**需跨列對齊**的「標籤 + N 欄」表格（欄數固定、欄位對齊）。預覽頁的 State Matrix 不再有任何客製的 grid／手機重排 CSS——它就是 `z-grid-cols-auto` + `z-overflow-x-auto`（見第 3 節）。

---

## 1. 設計原則：為什麼不引入 Bootstrap Grid

我們**刻意不**把 Bootstrap 的 12 欄 × 多 breakpoint × `col-sm-6 col-md-4` class 整套搬進 utility CSS：

1. **與現代 CSS 重複**：Bootstrap Grid 早於 CSS Grid。「卡片自動增減欄、窄螢幕換行、保持最小寬」用原生一行就能達成（見 `z-grid-fill`），不需要 breakpoint class 與 12 欄記帳。
2. **與 MD3/MUI 方向衝突**：本 theme 對齊 MD3/MUI。MUI 的 Grid 是 React 元件 props（`<Grid size={{xs:12, md:4}}>`），不是 class soup；`col-md-4` 會把 MD3 不採用的 12 欄思維帶回來，也和既有 `z-*` utility 風格不一致。
3. **範圍與維護**：整套 grid ≈ 數百行 CSS，等於自己養一個 mini-Bootstrap。

**取向**：優先用「intrinsic / auto-fit」式響應（讓內容依可用寬度自我排列），只有在真的需要「依裝置尺寸切換版面」時才用 `@media`。

---

## 2. `z-grid-fill` —— 通用響應式格線

### 定義（`zul/src/main/resources/web/zul/css/utility/_layout.css`）

```css
.z-grid-fill {
    grid-template-columns: repeat(auto-fit, minmax(min(var(--zk-grid-min, 180px), 100%), 1fr));
}
```

- `auto-fit`：能塞幾欄就塞幾欄，塞不下自動換行。
- `minmax(..., 1fr)`：每欄最少 `--zk-grid-min`（預設 **180px**），有多餘空間就平均撐滿。
- `min(var(--zk-grid-min,180px), 100%)`：當容器比一張卡片還窄時，卡片縮到容器寬（**永不橫向溢出**）。
- **無 `@media`**：欄數隨容器寬度自動變化 —— 寬螢幕多欄、手機一欄。

### 用法

`z-grid-fill` 只設定 `grid-template-columns`，所以要搭配 `z-d-grid`（`display:grid`）和一個間距（`z-gap-*`）：

```xml
<!-- 基本：卡片牆，每張至少 180px，自動換行 -->
<div sclass="z-d-grid z-grid-fill z-gap-4">
    <div sclass="z-paper">卡片 1</div>
    <div sclass="z-paper">卡片 2</div>
    <div sclass="z-paper">卡片 3</div>
    <div sclass="z-paper">卡片 4</div>
</div>
```

行為：1200px 寬 → 6 欄；600px → 3 欄；390px 手機 → 2 欄；極窄 → 1 欄。全程不需要寫任何 breakpoint。

```xml
<!-- 自訂最小卡片寬：每張至少 240px（較寬的卡片） -->
<div sclass="z-d-grid z-grid-fill z-gap-6" style="--zk-grid-min: 240px">
    <div sclass="z-paper">寬卡片 A</div>
    <div sclass="z-paper">寬卡片 B</div>
</div>
```

```xml
<!-- 儀表板磚：搭配既有 spacing / elevation utility -->
<div sclass="z-d-grid z-grid-fill z-gap-4 z-p-4">
    <div sclass="z-paper z-elevation-1 z-p-4">營收</div>
    <div sclass="z-paper z-elevation-1 z-p-4">訂單</div>
    <div sclass="z-paper z-elevation-1 z-p-4">使用者</div>
</div>
```

### `z-grid-fill` vs `z-grid-cols-N`（既有固定欄）

| | `z-grid-cols-N` | `z-grid-fill` |
|--|-----------------|---------------|
| 欄數 | **固定 N 欄**（內容收縮） | **隨寬度自動增減** |
| 換行 | 不換行 | 塞不下自動換行 |
| 適合 | 表單欄位對齊、需要固定欄數的版面 | 卡片牆、磚牆、清單，卡片彼此獨立 |
| 手機 | N 欄可能被擠很窄 | 自動降到 1~2 欄，保持可用 |

> 規則：**卡片彼此獨立（不需跨列對齊）→ 用 `z-grid-fill`；需要固定欄數對齊 → 用 `z-grid-cols-N`；「標籤欄 + N 個對齊欄」→ 用 `z-grid-cols-auto`（見下）。**

---

## 2.5 `z-grid-cols-auto` —— 標籤欄 + N 個等寬撐滿欄

### 定義（`zul/src/main/resources/web/zul/css/utility/_layout.css`）

```css
.z-grid-cols-auto {
    grid-template-columns: auto repeat(var(--zk-cols, 1), minmax(var(--zk-col-min, 0), 1fr));
}
```

- 第一欄 `auto`：依內容自動撐（放 row 標籤／描述）。
- 後面 N 欄 `minmax(--zk-col-min, 1fr)`：平均**撐滿**剩餘寬度。
- `--zk-cols` 控制資料欄數（預設 1，等同既有 `.z-grid-cols-auto-1fr`）。
- `--zk-col-min` 控制每個資料欄的**最小寬度**（預設 0 → 可任意收縮）。設成例如 `140px` 並把 grid 包進 `z-overflow-x-auto`，就得到**響應式資料表**：寬螢幕撐滿、窄螢幕守住最小寬並**水平捲動**，而非擠到不可用。

### 用法

```xml
<!-- 比較表：規格名稱 + 3 個方案欄 -->
<div sclass="z-d-grid z-grid-cols-auto z-gap-4" style="--zk-cols: 3">
    <div>儲存空間</div><div>10 GB</div><div>100 GB</div><div>無上限</div>
    <div>使用者數</div><div>1</div><div>10</div><div>無上限</div>
</div>
```

> **與 `z-grid-fill` 的差別**：`z-grid-fill` 欄數隨寬度自動增減、卡片彼此獨立、不保證跨列對齊；`z-grid-cols-auto` 欄數固定（`--zk-cols`）、有一個 auto 標籤欄、**保證跨列對齊**。需要「表格式對齊」就用它。

### 對齊的前提：所有 cell 在「同一個 grid」

`z-grid-cols-auto` 之所以能讓 `auto` 標籤欄跨列對齊，前提是**所有 cell 都是同一個 grid 的直接子元素**（`auto` 欄由全體最寬的標籤算一次、共用）。如果你的每一列被包成**獨立的 wrapper 元素**（例如為了逐列邊框），各列就會各自算自己的 `auto` 欄寬而失準。

兩種解法：
1. **`display: contents`（預覽頁採用）**：把 row wrapper 設成 layout-transparent，裡面的 cell 就「升級」成父 grid 的直接子元素 → 回到單一 grid → 天生對齊，且**不必改動 18 個內容模板**。
2. **`subgrid`**：容器當 grid、各列 `grid-template-columns: subgrid` 共用父層軌道。功能等價，但 row wrapper 仍是獨立盒子（可保留逐列邊框）。

---

## 3. State Matrix：純用通用 utility（零客製 grid CSS）

預覽頁的「狀態矩陣」是**對齊的二維表格**（共用表頭 + 多列資料列）。它**沒有任何客製的 grid 或手機重排 CSS**——grid 與 RWD 全部來自通用 utility，連結構膠水都不需要（舊有的 `pv.css` / `.pv-cols` 已完全移除）。

### 機制（`zkpreview/src/main/webapp/web/pv/matrix.zul`）

容器直接掛通用 utility，用 `display:contents` 攤平列 wrapper，只注入欄數：

```xml
<div sclass="z-d-grid z-grid-cols-auto z-overflow-x-auto z-gap-x-6 z-gap-y-2 z-items-start"
     style="--zk-cols:${arg.cols.size()}; --zk-col-min:min-content">
    <div sclass="z-grid-col-full ...">${arg.title}</div>   <!-- 區塊標題橫跨整寬 -->
    <div sclass="z-d-contents">                             <!-- 表頭列：wrapper 透明化 -->
        <div/> <forEach items="${arg.cols}" .../>
    </div>
    <apply template="${arg.rowsTemplate}"/>                 <!-- 資料列亦以 z-d-contents 攤平 -->
</div>
```

- `z-d-grid z-grid-cols-auto` → 單一 grid、`auto` 標籤欄 + N 個撐滿欄。
- `z-d-contents`（`display:contents`）→ 把每一列 wrapper 透明化，cell 升級成父 grid 的直接子元素 → 單一 flat grid → 跨列對齊。
- `z-grid-col-full`（`grid-column: 1 / -1`）→ 區塊標題橫跨整寬。
- `z-overflow-x-auto` → 窄螢幕水平捲動的容器。
- `--zk-cols` → 欄數；`--zk-col-min` → 每欄最小寬（矩陣用 `min-content`）。

### 結果

- **桌機 / 平板（≥600px）**：對齊表格，欄位撐滿、跨列對齊（與教學頁同一套機制）。
- **手機（<600px）**：表格在自己的容器內**水平捲動**（`z-overflow-x-auto`），輸入元件維持自然可用大小；文件本身不溢出 → 無幻影垂直捲軸。

> 這是把「可通用的部分全部交給通用 utility」的成果：State Matrix 從「8 個 `pv-cols-*` + subgrid + `@media` 卡片重排 + `--pv-col-N` 注入 + 一整個 `pv.css`」縮成「純 `z-grid-cols-auto` + `z-d-contents` + `z-grid-col-full` + `z-overflow-x-auto`」，**零客製 CSS**。手機 UX 由「堆疊卡片」改為「水平捲動」（標準資料表 RWD）。

---

## 4. 該用哪個？（決策）

```
要排版的是「彼此獨立的卡片」嗎？
├─ 是 → z-grid-fill（一行搞定，無 breakpoint）
└─ 否，需要跨列對齊
     ├─ 「標籤欄 + N 個對齊欄」(比較/規格/定價表、狀態矩陣) → z-grid-cols-auto + --zk-cols
     ├─ 「永遠固定 N 欄、無標籤欄」(表單兩欄對齊) → z-grid-cols-N
     └─ 二維對齊表格、手機要保住欄寬 → z-grid-cols-auto + --zk-col-min + z-overflow-x-auto（寬填滿、窄捲動）
```

---

## 5. Breakpoint 與「依裝置／容器切換版面」utility

- `z-grid-fill` 與 `z-grid-cols-auto`（第 2、2.5 節）都**不用** breakpoint（intrinsic / 捲動容器處理 RWD）——
  能用 intrinsic 就優先用，這是本 theme 的取向。
- 當真的需要「依裝置切換版面」（隱藏側欄、堆疊↔並排、只在手機顯示漢堡鈕）時，提供一組響應式 display utility。**breakpoint 尺度**採 Bootstrap：`sm` 576 / `md` 768 / `lg` 992 / `xl` 1200（`xs` 0 = base），與 EE 的 Grid／Listbox `responsive` 使用同一組值（決議見 `doc/breakpoint-unified-spec-proposal.md` §9；`scripts/build-css.js --module zkmax` 會檢查兩邊一致）。
  **不寫自訂寬度的 `@media` / `@container`**：寬度條件只能取上述尺度（`min-width` 為 576／768／992／1200，`max-width` 為其減 1），由 `scripts/check-breakpoints.js` 檢查（`scripts/build-css.js --module zkmax` 時執行）。
  互動教學頁：`usecase/index.zul#utility/responsive`（原始檔 `zkpreview/src/main/webapp/web/utility/responsive.zul`）。

### 5.1 依 viewport —— `z-d-{value}-{bp}`（mobile-first / min-width）

`z-d-{value}-{bp}` = 「viewport ≥ bp 時 `display:{value}`」。採 **mobile-first**：無後綴的 base `z-d-*`
（見 `_layout.css`「Display」）覆蓋 xs(0)，`-{bp}` 在該寬度**以上**疊加覆寫。`value` 精選
`none / block / flex / grid / inline-block`（涵蓋 hide/show + 版面切換）。

```xml
<!-- 側欄：手機隱藏，≥md 顯示 -->
<div sclass="z-d-none z-d-md-block">…sidebar…</div>
<!-- 漢堡鈕：只在 <md 顯示 -->
<button sclass="z-d-block z-d-md-none">☰</button>
<!-- 版面切換：手機堆疊、≥md 並排（純 display 切換） -->
<div sclass="z-d-block z-d-md-flex z-gap-3"> … </div>
```

> 注意：`@media` 條件**不能**用 `var()`，故 breakpoint 為字面 px（於 `_layout.css` 註解列出尺度）。

### 5.2 依容器 —— `z-container` + `z-cq-{value}-{bp}`（container queries）

貼合 intrinsic 哲學：讓元件**依自己容器的寬度**（而非 viewport）調整——同一元件放到寬／窄區域會各自適應，
特別適合 dashboard 磚、split pane。用 `.z-container`（`container-type: inline-size`）標記查詢容器，
其後代用 `z-cq-{value}-{bp}` 反應**該容器**的 inline size。value 與 bp 尺度同 5.1。

```xml
<div sclass="z-container">                        <!-- 查詢容器（必要前提） -->
    <div sclass="z-d-block z-cq-md-flex z-gap-3">  <!-- 容器 ≥md 時並排，否則堆疊 -->
        <div>Summary</div>
        <div sclass="z-cq-none z-cq-sm-block">Details</div>  <!-- 容器 ≥sm 才顯示 -->
    </div>
</div>
```

> `z-cq-*` **必須**有 `z-container` 祖先才有可量測的容器——請成對使用。breakpoint 量的是**容器**寬度，非 viewport。

### 5.3 客製 breakpoint

**斷點數值不是 runtime CSS 變數。** CSS 規定 `@media` / `@container` 的條件**不能**讀 `var()`，故 sm/md/lg/xl 是
**編譯期常數**。要調整就改 `_layout.css` 的 4 組 `@media` 與 4 組 `@container` 區塊（同一處），再 `npm run build:css`。
真正可在 runtime（`:root` 或 inline）覆寫的是 intrinsic grid 的旋鈕——`--zk-grid-min`（及 `-xs/-sm/-lg/-xl`）、
`--zk-cols`、`--zk-col-min`（見第 2、2.5 節），這些不是斷點。互動教學頁 `utility/responsive.zul` 第 3 部分有完整範例。

## 6. 測試守則

響應式版面的回歸測試放在 `zkpreview/src/test/playwright/tablet.spec.ts`（mobile UA 專案）：

- `tablet-no-horizontal-overflow`：頁面在平板寬度下 `scrollWidth <= clientWidth`（防止橫向溢出→幻影垂直捲軸）。
- `tablet-matrix-scroll-usable`：在 390px 確認 matrix 容器**內部可水平捲動**（`scrollWidth > clientWidth`）且輸入元件守住 ≥130px（防止「擠壓而非捲動」）。
- `pv-cols-fill`（desktop）：桌機最右欄貼齊容器右緣（確認欄位撐滿）。

新增任何響應式版面時，沿用「失敗先行」：先寫一條會 fail 的幾何斷言，再寫 CSS 讓它 pass。

## 7. 注意事項 / 限制

- `z-grid-fill`／`z-grid-cols-auto` 都只設 `grid-template-columns`，必須搭配 `z-d-grid` 才有效。
- `z-grid-cols-auto` 的跨列對齊前提是「所有 cell 在同一個 grid」；若列被包成獨立 wrapper，用 `display: contents`（或 `subgrid`）攤平（見第 2.5 節）。
- State Matrix 已**無任何客製 grid／`@media`／`pv.css`**；grid、對齊與 RWD 全部由通用 utility（`z-grid-cols-auto` + `z-d-contents` + `z-grid-col-full` + `z-overflow-x-auto`）提供。
