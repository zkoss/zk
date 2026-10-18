# Marble 主題 — 與主流 Web Framework 的功能落差分析

> 分析日期：2026-07-20。基準 ZK 版本：10.4.0-jakarta。
> 本文件是**分析 + roadmap**，非 normative spec（規範以 `doc/spec/` 為準）。

> **進度更新（2026-07-23）— Tier 1 #1 已交付。** 「元件級主題變數（Component Theme Variables）」
> 從 3 元件 pilot 擴為**全面完工並入庫**：每個有專屬 stylesheet 的視覺元件都已套上可覆寫的
> `--zk-<comp>-*`（`_component-theme.css` 共 **73** 個 `--zk-<comp>-` 家族、tracker A 表 **61** 列元件），
> 含 **CTV-1…9** 檢驗條件、`component-theming` 測試 **63 綠**（各元件自己的預覽頁上，2026-10-18 起；原 105 條跑在已刪除的 `component-theming.zul` 頁上）、涵蓋全 **172** 元件的進度 tracker（**B / D 皆清空**——
> 連原本受阻、無 base CSS 的 breadcrumb / carousel 也已補齊 base 元件 CSS + 變數）。另補 **Authoring rule 5**：
> typography 屬全域 typescale、非元件旋鈕。實作採 generator（sonnet）↔ 獨立 Opus checker 的雙角色 workflow。
> 規範見 [`spec/component-theme-variables.md`](spec/component-theme-variables.md)；逐元件進度見
> [`component-theme-variables-progress.md`](component-theme-variables-progress.md)。commits `12ed13f`→`87d5dfb`（+`69415b6`）。

> **進度更新（2026-07-27）— Tier 1 #2 使用文件 / 預覽頁完善。** 「裝置自適應」核心 utility 早於 `1515fd3` 交付；
> 本次把**使用文件與預覽頁**打磨到位（非狀態變更，屬既交付項目的文件品質提升）：
> - `utility/responsive.zul` 由「功能清單」改寫為**情境導向使用指南**（仿 Bootstrap grid docs）：Part 1 依 viewport、
>   Part 2 依 container、Part 3 客製化（含「breakpoint 非 runtime 變數、需改 `_layout.css` 重編」的誠實說明），
>   每個 part 與情境都附**原始碼**，並全面**英文化**。
> - 修正一個既有缺陷：預覽頁的程式碼區塊原以 native `<h:pre>` + CDATA 輸出，會把片段當**真 HTML 渲染**（顯示成方塊而非原始碼）；
>   改用 `<div><label multiline pre>` escape，現逐字顯示原始碼。此寫法同步套用到 grid 頁。
> - `grid-utilities.zul`（原置於 web root、緊鄰 `grid.zul` 等元件頁，易誤認為元件）**移入 `utility/grid-layout.zul`**，
>   補上「Utility CSS → Grid Layout」navitem（原為 nav 孤兒頁）、統一標題為 `z-h2`（section 標題改用 `z-h4` 語意 heading utility）、全面**英文化**。
> 驗證：`responsive` 專案 4 綠、`smoke` 112 綠（含新路徑 `/utility/grid-layout.zul`）。commits `23b82f6`→`4ebd48c`。

> **進度更新（2026-07-27）— Tier 1 #3 列印樣式已交付。** 新增 `utility/_print.css`（`@layer zk-utilities` 內的 `@media print`）：
> - opt-in 可見性 utility `.z-d-print-{none/block/flex/grid/inline-block}`（對齊 `_layout.css` viewport 集）。
> - 自動列印 reset：隱藏浮層 chrome（modal 遮罩 / faker / toast / loading bar / drawer / error）、解除 `.z-sticky-header` 表頭（`position:static`，讓長表分頁）、展開 grid/list/tree 卷軸區、elevation 陰影→hairline `outline-variant` 邊框；**保留品牌色，不做 ink-saving**。
> - 註冊進 `build-css.js` 的 `normFiles`（bundle 進 `norm.css.dsp`；已驗證 `@media print` 區塊完整通過 CleanCSS level-1）。
> - 預覽頁 `utility/print.zul`（英文、`z-h4` section 標題、逐字原始碼區塊）+「Utility CSS → Print」navitem；規範 `doc/spec/print-styles.md`。
> 驗證：`print` 專案 2 綠、`smoke` 113 綠（含新路徑 `/utility/print.zul`）。**已知限制**：凍結欄（`<frozen>`）JS 定位，非 CSS，無法單靠列印樣式完整展開。#4 / #5 仍待補。

> **進度更新（2026-07-27）— Tier 1 #4 疊層尺度（z-index）已交付。** 先做**完整稽核**（[`zindex-audit.md`](zindex-audit.md)），
> 交叉比對 ZK runtime 後得到**翻案級關鍵發現**：ZK 的 JS client 在浮層一顯示時，以 `_topZIndex()`（base **1800**、
> 扁平全域計數器）寫上 **inline** `z-index` ≥1800，永遠蓋過 CSS。故 #4 從原規劃「造一把 Bootstrap 式 ladder 管浮層順序」
> 修正為**更誠實、更小**的形狀：
> - 新 `tokens/_zindex.css`：只 token 化**真正 load-bearing**（ZK 不當它是 floating widget）的值——`--zk-index-{nav 1000 /
>   loading 1450 / float-fallback 1800 / loadingbar 2000 / slider-tooltip 60000 / busy-mask 89000 / busy-loading 89500 /
>   fullscreen 99999 / error 9999999}`（值 = 原值，**零回歸**）。1000–2000 的元件浮層值維持 cosmetic fallback（ZK runtime 覆寫），**刻意不重編**。
> - `.z-index-*` utility（`_layout.css`）+ 企業指南：自訂 chrome 想在 ZK 浮層之下 → 保持 &lt; 1800；想在其上 → 用 `setTopmost()`，別和計數器出價。
> - 順手清兩個**死值**：searchbox `88000`（誤抄，ZK 官方主題用 1000）→ float-fallback；drag-ghost `90000`（ZK 拖曳時 inline 蓋為 88800）→ 加註。
> - 規範 [`spec/zindex-scale.md`](spec/zindex-scale.md)、稽核全紀錄 [`zindex-audit.md`](zindex-audit.md)、預覽頁 `utility/zindex.zul` +「Stacking (z-index)」navitem。
> 驗證：`zindex` 專案 2 綠、`smoke` **114 綠**（含新路徑 `/utility/zindex.zul`）。#5（Skeleton）仍待補。

## 目的

Marble 即將成為 ZK 11.0 的預設外觀主題。本文回答一個問題：**排除 RTL 與 Dark Theme 之後**，
相較目前市面上受歡迎的 Web Framework，Marble 在 **CSS 功能** 與 **主題（theming）功能** 上還缺少什麼？

比較基準：
- **Utility-CSS 類**：Tailwind CSS、Bootstrap 5、Open Props（token 廣度參考）
- **元件庫主題系統類**：MUI (Material UI)、Ant Design、Chakra UI

Marble 同時橫跨兩種角色 —— 既是「元件主題」（對齊 MUI），又內建整套 `z-*` utility class
（對齊 Tailwind/Bootstrap），故兩類都比。

> 明確排除（皆已有專屬決策，不在本文討論）：
> - **Dark mode** — won't-do（見 `doc/spec/design-decisions.md` §1）
> - **RTL** — backlog，唯一已知功能性缺口（§3）

---

## 先確認：Marble「已經很強」的部分（避免誤報為缺口）

- **Token 架構現代**：`oklch(from …)` relative-color 推導、`@layer` cascade layers、logical properties（RTL-ready）、`:has()`、`prefers-reduced-motion`、完整 MD3 typescale / spacing / elevation / shape / motion ladder。
- **主題客製**：單 seed 品牌換色（`--zk-color-primary` → 自動推導 container / fill / focus）、4 個品牌 preset、`MarbleBrand` / `MarbleDensity` Java runtime API、compact 密度層。
- **A11y 基礎扎實**：forced-colors（Windows 高對比）、focus-visible、sr-only / visually-hidden、reduced-motion 皆具備。
- **響應式**：intrinsic auto-fit grid（`z-grid-fill`、`z-grid-cols-auto`）+ 獨立的平板/手機 UA 主題層（`tablet.css.dsp`）。

以上不列為缺口。以下才是相對主流框架真正欠缺的。

---

## 落差清單（依企業級 ROI 分層）

### Tier 1 — 對「企業預設主題」而言真正該補的落差

| # | 落差 | 主流框架怎麼做 | Marble 現況 | 為何重要 |
|---|------|---------------|------------|---------|
| 1 | **元件級主題變數(Component Theme Variables)** ✅ | MUI `theme.components.MuiButton.styleOverrides` / `variants`；Ant `ConfigProvider theme.components.Button.*`；Chakra component theme | **✅ 已完成**：所有有 stylesheet 的視覺元件皆宣告可覆寫 `--zk-<comp>-*`（**73** 家族／A 表 **61** 列元件），含 CTV-1…9 檢驗條件 + 全 **172** 元件 tracker（B/D 清空）、`component-theming` **63** 測試綠（各元件自己的預覽頁；原為缺口，現已系統化並完工） | **MUI / Ant 最大賣點**。企業客戶最常要「只改我們的 button / grid 樣式而不 fork 主題」 |
| 2 | **裝置自適應控制**（container queries + 響應可見性） ✅ | Tailwind `@container` / `md:`；Bootstrap `.d-md-none` 響應顯示；MUI breakpoints | **✅ 已完成**：`_layout.css` 新增 viewport `.z-d-{value}-{bp}`（mobile-first / min-width，sm 600 / md 900 / lg 1200 / xl 1536）＋ container query `.z-container` / `.z-cq-{value}-{bp}`（`container-type: inline-size` + `@container`）；預覽頁 `utility/responsive.zul`、`responsive` Playwright 專案 4 綠、`responsive-design.md` §5 更新 | Dashboard / ERP 常需「依容器寬度切換」。**Container queries** 比 breakpoint 更貼合 Marble 的 intrinsic 哲學 |
| 3 | **列印樣式** `@media print` ✅ | Bootstrap `.d-print-*` + print reset | **✅ 已完成**：`utility/_print.css` 提供 opt-in `.z-d-print-*` 可見性 + 自動列印 reset（隱藏浮層 chrome、解除 sticky 表頭、展開卷軸、陰影→邊框）；`print` 專案 2 綠。規範 `doc/spec/print-styles.md`（原為 0 覆蓋，現已補齊） | ERP / CRM 天天印報表、發票、清單。目標客群最需要，卻曾是 0 覆蓋 |
| 4 | **z-index / 疊層尺度** ✅ | MUI `theme.zIndex`（appBar / drawer / modal / snackbar / tooltip）；Bootstrap `$zindex-*`；Chakra `zIndices` | **✅ 已完成**：`tokens/_zindex.css` 提供 `--zk-index-*`（只 token 化 load-bearing 值）+ `.z-index-*` utility。**關鍵發現**：ZK runtime 以 base 1800 **inline** 覆寫浮層 z-index，故元件 CSS 只是 fallback → 不造假 ladder；清 searchbox/drag-ghost 兩死值；規範 [`spec/zindex-scale.md`](spec/zindex-scale.md)、稽核 [`zindex-audit.md`](zindex-audit.md)、`zindex` 專案 2 綠 | 框架衛生。避免企業 App 自訂 overlay 時的 z-index 大戰；主題自身 overlay 排序也該有文件化尺度 |
| 5 | **Skeleton / shimmer 載入態** | MUI `Skeleton`、Ant `Skeleton`、Bootstrap placeholders | 只有 spinner / progress bar 元件 | 資料密集型 App（正是目標客群）載入時幾乎都用骨架屏。缺一個 `.z-skeleton` utility |

### Tier 2 — Utility 廣度與細節打磨（可批次補齊）

主流 utility 框架有、Marble 缺的 utility family（挑高使用率的）：

- **`opacity`**（`.z-opacity-50`）、**`cursor`**（`pointer` / `not-allowed`）、**`user-select`**、**`pointer-events`**
- **`aspect-ratio`**（`aspect-video` / `square`）+ **`object-fit`**（cover / contain）—— 卡片 / 媒體版面基本款
- **多行 line clamp**（`line-clamp-2 / 3`）—— 目前只有單行 `z-text-truncate`；卡片 / 清單摘要極常用
- **`backdrop-filter`（blur）** + **transform**（rotate / scale / translate）+ **gradient 背景** utility
- **`z-index` / `order`** utility；**border-color / border-width** 變體（目前只有 `z-border` / `z-border-0`）
- **elevation utility 補 4 / 5**（token 到 5，utility 只到 3）
- **尺寸尺度太粗**：`w-* / h-*` 只有 25 / 50 / 75 / full / auto；缺固定尺度、缺 `max-width` / `min-width`（如 `max-w-prose`）、缺 `grid-rows-*` / `place-*` / `gap-7+`
- **可重用動效 preset**（`.z-animate-fade-in` 等）—— 有 motion token 與元件內 keyframes，但沒開放成 utility（Open Props / Tailwind `animate-*` 有）
- **原生控制項快速勝利**：`accent-color`（讓原生 checkbox / radio / range 免費吃品牌色）、`color-scheme`

### Tier 3 — 設計工具與交付（tooling，非 CSS 本身）

- **Design token 匯出**（W3C DTCG JSON / Style Dictionary）+ **Figma kit** + **多 token 視覺 playground**
  （`doc/spec/design-decisions.md` §6 已自認：目前只有單 seed 色彩 picker）。
  MUI / Ant / Tailwind 都有 —— 對「ZK 11 預設主題」的**企業導入與設計團隊交付**是真實推力。
- **Skip-link / skip-to-content** a11y pattern（政府 / 金融 WCAG 稽核常見缺項；其餘 a11y 已很完整）。
- **`clamp()` fluid typography**（可選；哲學上貼合 intrinsic，但 MD3 用固定 typescale，優先度低）。

### Tier 4 — 前沿 primitive（建議延後，非企業剛需）

View Transitions API、scroll-snap（輪播 / 圖廊）、scroll-driven animations、CSS anchor positioning、subgrid。
ZK 多以 JS 定位 overlay、企業需求低，選擇性補（如圖廊用 scroll-snap）即可。

---

## 一句話結論

排除 RTL / Dark 後，Marble 的 **token 與架構層已對齊甚至領先**主流框架；真正的落差集中在兩處：

1. **元件級客製（Component Theme Variables）**（MUI / Ant 的核心賣點）—— 結構性;**現已落地**;
2. **應用層 CSS 廣度**（container queries ✅、列印 ✅、z-index 尺度 ✅ 已交付；剩 skeleton、opacity / aspect-ratio / line-clamp 等 utility）—— 量多但單項成本低，可批次補齊。

Tooling（token 匯出 / Figma）是導入推力但屬周邊。前沿 primitive 可延後。

---

## Tier 1 Roadmap（impact / effort / 切入點）

| # | 項目 | Impact | Effort | 實作切入點（要動哪些檔） |
|---|------|--------|--------|------------------------|
| 1 | **元件級主題變數(Component Theme Variables)** ✅ 已完成 | 高（最大賣點） | 高（已投入） | **✅ 已交付**（commits `12ed13f`→`87d5dfb`）。落地做法與原規劃有一處**設計修正**：改採 **no-fallback** —— 每元件於 `tokens/_component-theme.css` 的 `:root` 宣告 `--zk-<comp>-*`（預設 = 原值、零回歸），component CSS 於 `@layer zk-components` 內以 `var(--zk-button-bg)` **無 fallback** 消費（非原規劃的 `var(--zk-button-bg, …)`；理由見 Authoring rule 3：預設只在 `:root` 宣告一次、避免 fallback 漂移、DevTools 可探）。原「先做 3–5 個 pilot」已擴至**全部**有 stylesheet 的視覺元件（含新做 base CSS 的 breadcrumb / carousel）。流程：generator（sonnet）實作 ↔ 獨立 Opus checker 驗 CTV-1…9。檔：`src/main/resources/web/js/zul/**/css/*.css` + `doc/spec/component-theme-variables.md` + `doc/component-theme-variables-progress.md` |
| 2 | **裝置自適應**（container queries + 響應可見性） **✅ 已交付** | 中高 | 中 | (a) ✅ viewport 響應可見性 `.z-d-{value}-{bp}`（`value` ∈ none/block/flex/grid/inline-block × bp ∈ sm 600/md 900/lg 1200/xl 1536，mobile-first）。(b) ✅ container queries：`.z-container`（`container-type: inline-size`）＋ `.z-cq-{value}-{bp}`（`@container` 變體，同尺度）。落地：`utility/_layout.css` 既有 `@layer zk-utilities` 塊內（`@media`/`@container` 已驗證通過 CleanCSS build）；預覽頁 `src/test/resources/web/utility/responsive.zul` + `usecase` navitem「Responsive」；`responsive-design.md` §5 改寫為「已提供 + 用法」；回歸測試 `responsive-utilities.spec.ts`（`responsive` 專案，4 綠）。**設計注記**：value 精選 5 個（非全矩陣，Simplicity First）；`z-cq-*` 必須有 `z-container` 祖先 |
| 3 | **列印樣式** `@media print` **✅ 已交付** | 中高（企業） | 低 | **✅ 已交付**（本 session）。落地：`utility/_print.css` = `@layer zk-utilities` 內的單一 `@media print` 區塊 —— opt-in `.z-d-print-{none/block/flex/grid/inline-block}` + 自動 reset（elevation→hairline 邊框、展開 grid/list/tree overflow、解除 `.z-sticky-header`、隱藏浮層 chrome）。**設計注記**：使用者選定 **standard reset**（保留品牌色，非 ink-saving 變體）；因 `zk-utilities` 是最上層 layer，覆蓋 `zk-components` 無需 `!important`。註冊進 `build-css.js` `normFiles`（bundle 進 `norm.css.dsp`）。檔：`utility/_print.css` + `scripts/build-css.js` + `doc/spec/print-styles.md` + 預覽頁 `utility/print.zul` + `print` Playwright 專案 |
| 4 | **z-index / 疊層尺度** **✅ 已交付** | 中 | 低中 | **✅ 已交付**（本 session）。稽核後發現 ZK runtime 以 base 1800 **inline** 覆寫浮層 → 原規劃的 Bootstrap ladder 對 ZK 浮層無效，改為**誠實版**：`tokens/_zindex.css` 只 token 化 load-bearing 值（nav 1000 / loading 1450 / float-fallback 1800 / loadingbar 2000 / slider-tooltip 60000 / busy-mask 89000 / busy-loading 89500 / fullscreen 99999 / error 9999999，值=原值零回歸）+ `.z-index-*` utility（`_layout.css`）；1000–2000 cosmetic 浮層值不動；清 searchbox 88000 / drag-ghost 90000 兩死值。檔：`tokens/_zindex.css` + `build-css.js` + `_layout.css` + 9 元件 CSS + `doc/spec/zindex-scale.md` + `doc/zindex-audit.md` + 預覽頁 `utility/zindex.zul` + `zindex` Playwright 專案 |
| 5 | **Skeleton / shimmer** | 中高 | 低中 | 純 CSS utility `.z-skeleton`（surface-variant + shimmer keyframe，**須 reduced-motion guard**）+ `-text` / `-circle` / `-rect` 變體。沿用 `js/zul/wgt/css` 既有 keyframe 寫法。檔：新 `utility/_skeleton.css`（或併入 `_components.css`） |

**Tier 2** 建議打包成一次「utility 廣度 pass」（多為附加、低風險）：opacity / cursor / user-select / aspect-ratio / object-fit / 多行 line-clamp / backdrop-blur / transform / gradient / border-color·width / elevation 4·5 / 更細尺寸尺度 / 動效 preset / `accent-color`。落在既有 `_layout.css`、`_borders.css`、`_colors.css`、`_elevation.css` + 新 `_effects.css`。

---

## Verification（如何驗證補上的功能）

- 新 utility：`npm run build:css` 後在 preview app（`withjdk.sh 17 mvn test exec:java@preview-app`）用實際 ZUL 頁驗證，並沿用 `src/test/playwright/` 幾何 / computed-style 斷言（如 `tablet.spec.ts` 模式）。
- 元件級主題變數（Component Theme Variables）：寫一頁覆寫 `--zk-<comp>-*` 的 demo，Playwright 量 computed style 確認生效且**不影響其他元件**（已落地,見 CTV-1…9 檢驗條件）。
- 列印樣式：Playwright `page.emulateMedia({ media: 'print' })` 斷言 `.z-d-print-none` 等隱藏 / 顯示行為。
- Skeleton：`prefers-reduced-motion` 下 shimmer 動畫須停止（比照 `tokens/_motion.css` 既有守則）。
