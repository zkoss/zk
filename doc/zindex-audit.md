# z-index 稽核 — Marble 疊層尺度（Tier 1 #4 前置）

實作 `tasks/framework-feature-gaps.md` Tier 1 #4「z-index / 疊層尺度」的**第一步**：把主題現有每一個 `z-index`
盤點、歸類到「屬於哪一層」,並標記哪些值**可自由收編進尺度**、哪些**必須與 ZK runtime 對齊不可亂動**。

> 資料來源:`src/main/resources/web/**/*.css`(掃描日 2026-07-27)。
> 產出目的:決定 #4 的 token ladder 該長怎樣、要動哪些檔、風險在哪。
> ⚠️ §5「ZK runtime 協調結論」待 zk-framework-expert 子代理回報後定稿(見該節)。

---

## 0. TL;DR — 最重要的發現

盤點後,主題的 z-index **不是**一團從 `1` 到 `9999999` 的無政府亂數。實際上分成**三個涇渭分明的區間**:

1. **元件內部堆疊(1–40)** — 純 component-local,和「浮層大戰」無關。**維持原樣**,不要收編。
2. **Marble 自有浮層階梯(1000–2000)** — nav / drawer / mask / popup / dropdown / toast…**已經是一把大致有序的 Bootstrap 式階梯**,只是「沒有 token、沒有文件」。**這才是 #4 該正名(命名 + 文件化)的核心。**
3. **ZK core 繼承的魔術數字(60000–9999999)** — slider tooltip 60000 / searchbox 88000 / busy mask 89000 / busy loading 89500 / drag ghost 90000 / fullscreen 99999 / error 9999999。這些值 **ZK core CSS 自己也在用**,是與框架協調的產物。**可否往下收編,取決於 ZK JS 是否在 runtime 覆寫 z-index(§5 待確認)。**

**⚠️ 但 §5 的 ZK runtime 查證再翻轉一次結論**:ZK 的 JS 在浮層一顯示時,就以 **base 1800 的扁平全域計數器**把 inline `z-index` ≥1800 蓋上去(inline 永遠贏 CSS)。所以區間 B 的 1000–2000 對「ZK 會開的浮層」而言**幾乎全是 cosmetic fallback**,根本不生效。

> **修正後主張:#4 的核心不是造 ladder,而是「說出真相」——文件化 ZK runtime base 1800 機制**,並**只把真正 load-bearing 的 CSS-only 值(nav / loadingbar / slider-tooltip / busy-mask·loading / `.z-loading` / error / fullscreen)token 化(值不變)**。cosmetic fallback 誠實指向 `--zk-index-float-fallback: 1800` 即可,別排假階梯。順手清兩個死值(searchbox 88000、drag-ghost 90000)。詳見 §5、§6。

---

## 1. 方法

- `grep -rn "z-index"` 掃 `src/main/resources/web/`,取出每一筆數值型 `z-index` 宣告(含負值)。
- 對每筆回溯其最近的 selector,判斷它是「元件內部相對堆疊」還是「全域浮層」。
- 對 60000+ 的極端值,交叉比對 ZK core(`/Users/hawk/Documents/workspace/ZK10`)CSS 是否同樣使用 → 判定為「繼承/協調值」。

**ZK core CSS 實測(佐證區間 3 的協調性質)**:ZK 自家 theme CSS 出現 `z-index: 60000 / 88000 / 89000 / 89500 / 99000 / 9999999 / 32000 / 31000 / 16800 / 1800` —— 與 Marble 的魔術數字高度重疊,確認 Marble 是**沿用 ZK 既有慣例**,而非亂填。

---

## 2. 區間 A — 元件內部堆疊(z 1–40):**維持原樣**

這些是 `position: relative/absolute` 元件**自身 DOM 內**的層次(表頭蓋內容、beak/arrow、frozen 欄、tab 重疊、拖曳把手…),數值極小、彼此不跨元件比較。收編進全域尺度**沒有意義且有風險**。

| z | 用途 | 檔案(節錄) |
|---|------|-----------|
| -1 | calendar 裝飾層(藏在內容後) | `db/calendar.css:170,223` |
| 1 | 元件內最基本層(menu item 內部、tab 重疊、badge、carousel、colorbox 圖示、slider track、toolbar/toolbarbutton、notification、toast body、signature、wheel) | `menu.css`×7、`tabbox.css`×5、`badge.css:38`、`carousel.css:75`、`colorbox.css:145`、`rangeslider.css:102,196`、`combobutton.css:82`、`notification.css:55`、`toast.css:60`、`toolbar.css:190`、`toolbarbutton.css:62`、`signature.css:38`、`_wheel.css:115` |
| 2 | 元件內第二層(grid/listbox/tree 表頭 cell、carousel 控制、errorbox body、input、wheel band) | `grid.css:70,151`、`listbox.css:47,129`、`tree.css:50`、`carousel.css:136,200`、`errorbox.css:59`、`input.css:284`、`rangeslider.css:120`、`goldenlayout.css:354`、`_wheel.css:133,145` |
| 8–16 | borderlayout splitter / collapse handle 疊放 | `borderlayout.css:34(16),51(14),68(12),85(10),101(8)` |
| 10 | frozen 欄 / scroll 容器內固定區 | `grid.css:415`、`listbox.css:676`、`tree.css:452`、`biglistbox.css:109` |
| 20–40 | goldenlayout 拖曳/分割層、borderlayout 拖曳把手、menu 內層 | `goldenlayout.css:42(40),450(30),64,476(20)`、`borderlayout.css:132,143(20)`、`menu.css:523(25)` |

**建議:全數不動。**（真要吹毛求疵,可日後統一成 `--zk-index-raise:10` 之類的「元件內抬升」語意 token,但屬 Tier 2 細節,非 #4 範圍。）

---

## 3. 區間 A2 — 99/100 混合帶:**低優先,大多維持**

這一帶語意混雜,**不是浮層大戰的一部分**:

| z | selector | 性質 |
|---|----------|------|
| 99 / 100 | `linelayout.css:102,166` | line layout 連接線 → 元件內 |
| 100 | `menu.css:66` `.z-menubar` | sticky 選單列 chrome → 勉強算 `sticky` 層 |
| 100 | `scrollbar.css:24,47` | 自繪捲軸覆蓋層 → 元件 chrome |
| 100 | `input.css:339` `.z-errorbox-pointer`、`errorbox.css:110`、`notification.css:110` | errorbox/notification 的 beak/箭頭 → 元件內裝飾 |
| 100 | `coachmark.css:38` | coachmark spotlight → 其實是 overlay,但獨立情境 |

**建議:先不動。** 若日後要做 `--zk-index-sticky` / `--zk-index-fixed` 語意層,menubar 與自繪捲軸可收編進去;beak/連接線維持元件內。

---

## 4. 區間 B — Marble 自有浮層階梯(1000–2000):**#4 的核心**

**這才是該正名的東西。** 這些是真正的「浮起來蓋住頁面」的元件,而且**已經排得相當有序**——只是散落在 12+ 個檔案、沒有共用 token、沒有文件,新人無從得知順序。

| 現值 | 語意層 | selector（全部同值者） | 檔案 |
|------|--------|----------------------|------|
| 1000 | 導覽下拉 | `.z-navbar-horizontal .z-nav > ul` | `nav.css:235` |
| 1200 | 抽屜 drawer | `.z-drawer` | `drawer.css:14` |
| 1400 | 遮罩 scrim | `.z-modal-mask` / `.z-mask` | `window.css:168`、`misc.css:147` |
| 1450 | 全域 loading | `.z-loading` | `misc.css:20` |
| 1500 | 浮層 / 對話框 / 拖放 faker | `.z-popup` / `.z-confirmpopup` / `.z-panel-move-block` / `.z-panel-resize-faker` / `.z-window-resize-faker` | `popup.css:6`、`confirmpopup.css:21`、`panel.css:188,195`、`window.css:214` |
| 1600 | 下拉 popup（inline 控制項） | `.z-combobox-popup` / `.z-menupopup` / `.z-bandbox-popup` / `.z-errorbox` / `.z-toolbar…` / `.z-colorbox-popup` / `.z-chosenbox-popup` / `.z-timepicker-popup` / `.z-bandpopup` | `combobox.css:145`、`menu.css:272`、`bandbox.css:90`、`input.css:243`（errorbox）、`toolbar.css:243`、`colorbox.css:101`、`chosenbox.css:112`、`timepicker.css:186`、`bandpopup.css:6` |
| 1700 | 日期 popup（較高,壓過同儕） | `.z-datebox-popup` / `.z-daterangebox-popup` | `datebox.css:208`、`daterangebox.css:211` |
| 1800 | Toast | `.z-toast-position-wrapper` | `toast.css:132` |
| 2000 | Loading bar（頂部進度條,壓過一切浮層） | `.z-loadingbar-position` | `loadingbar.css:92` |

**觀察**:
- 順序合理:nav < drawer < mask < loading < 浮層/對話框 < 下拉 < 日期下拉 < toast < loadingbar。
- 但**間隔不均**(1400→1450→1500 太密;1800→2000 跳 200),而且是「碰巧有序」——沒有共識文件,下一個人加新浮層只能猜。
- `1600` 這層塞了 9 個 selector,是最擁擠的一層。

**建議(區間 B → #4 主體)**:
- 新增 `tokens/_zindex.css`,把上表**用現值命名**成 `--zk-index-*`,元件 CSS 改 `z-index: var(--zk-index-popup)` 等。**值不變 = 零視覺回歸**,只是取得「具名 + 文件 + 可覆寫」。
- 命名草案(待定):`--zk-index-nav 1000` / `-drawer 1200` / `-mask 1400` / `-loading 1450` / `-overlay 1500` / `-popup 1600` / `-popup-tall 1700` / `-toast 1800` / `-loadingbar 2000`。
- 是否順手把間隔調勻(改成 Bootstrap 式 1000/1020/1040/…）→ **需要視覺回歸驗證**,列為選項而非預設(Surgical Changes:先命名,不重編)。

---

## 5. 區間 C — ZK core 繼承的魔術數字(60000+):**協調值,勿輕動**

| 現值 | selector | 用途 | ZK core CSS 也用? |
|------|----------|------|------------------|
| 60000 | `.z-slider-popup` / `.z-sliderbuttons-tooltip` / rangeslider tooltip | slider 拖曳提示 | ✅ 是 |
| 88000 | `.z-searchbox-popup` | searchbox 下拉(JS 設寬) | ✅ 是 |
| 89000 | `.z-apply-mask` | 元件級 busy 遮罩 | ✅ 是 |
| 89500 | `.z-apply-loading` | 元件級 busy loading（比 mask 高 500） | ✅ 是 |
| 90000 | `.z-drag-ghost` / drag 複本 tooltip | DnD 拖曳幽靈（跟隨游標,須壓過一切） | ❌ 否（Marble 自訂,但刻意落在此帶之上） |
| 99999 | tbeditor 全螢幕 | 全螢幕編輯器 | ✅ 是（ZK 慣例值） |
| 9999999 | `.z-error` | 致命錯誤面板（必須壓過所有東西） | ✅ 是 |

**解讀**:`88000/89000/89500` 看似一把「ZK busy/mask 微階梯」(mask 89000 → loading 89500,搜尋 popup 88000 在其下);`9999999` 是「錯誤面板壓過天」的慣例。這些**高到與區間 B 完全脫節**,顯示它們是為了確保「永遠在 ZK runtime 動態浮層之上」而刻意抬高。

### ✅ ZK runtime 協調結論(zk-framework-expert 已查證,對照 `10.4.0-jakarta.FL.20260713-Eval`)

**是——ZK 的 JS client 在 runtime 動態覆寫浮層 z-index,而且是一把「壓過 CSS 的印章」。** 這徹底改變本稽核的結論。

機制(`zk/zk/.../web/js/zk/widget.ts`):
- **`_topZIndex(wgt)`(widget.ts:589)base 值 = `1800`**(原始碼註解:`// we have to start from 1800 depended on all the css files.`)。函式掃全域 `_floatings` 登記表,把 `zi` 抬到「所有其他可見浮層 z-index 的 max + 1」。
- **`setVisible(true)`(widget.ts:2413)**:只要 widget 是 `_floating`,一顯示就 `setZIndex(_topZIndex(this), {floatZIndex:true})`——**不需要顯式呼叫**。
- **`setZIndex()`(widget.ts:2798)寫的是 `node.style.zIndex`——inline style**,永遠贏過任何外部 stylesheet(不管 specificity 或 `!important`)。
- 計數器是**全域、扁平、單調遞增、跨所有元件類別共用**;**沒有** per-category 分層。最後開/被 bring-to-front 的浮層贏。

**結論:區間 B 的 1000–2000 對「ZK 會開的浮層」而言,幾乎全是 cosmetic/pre-paint fallback——JS 一顯示就蓋成 inline ≥1800。** 只有以下「ZK 不當它是 floating widget」的元素,z-index 值才真正 load-bearing:

| 元素 | runtime? | 現值 | 判定 |
|------|----------|------|------|
| `.z-navbar … ul`(nav 下拉) | ❌ CSS-only | 1000 | **可自由重編** |
| `.z-loadingbar-position` | ❌ CSS-only | 2000 | **可自由重編** |
| `.z-slider-popup` / slider tooltip | ❌ CSS-only | 60000 | **可自由重編** |
| `.z-apply-mask` | ❌ CSS-only | 89000 | 可重編,但**須 < apply-loading** |
| `.z-apply-loading` | ❌ CSS-only | 89500 | 可重編,但**須 > apply-mask**(真實刻意階梯) |
| `.z-loading`(全域 please-wait) | ⚠️ load-bearing | 1450 | JS **回讀**它算配對 mask = `z−1`(`zk/utl.ts:464`);**須 > modal-mask 1400**(現已滿足) |
| `.z-error`(致命 JS 例外框,非 `.z-errorbox`) | ❌ CSS-only | 9999999 | 可重編,但**須壓過一切**(含 runtime 1800+N) |
| `.z-fullscreen`(tbeditor) | ❌ CSS-only | 99999 | **可自由重編** |
| `.z-drawer-mask` | ❌ CSS-only(但在 JS 控制的 `.z-drawer` 內) | — | 相對 sibling 安全 |

**其餘全部是 cosmetic/fallback**(ZK 一顯示就 inline ≥1800 蓋掉):window 1500、`.z-modal-mask` 1400(ZK 用 `new zk.eff.FullMask({zIndex: wgt._zIndex})` 設定;**ZK core CSS 根本沒給 `.z-modal-mask` z-index**,Marble 的 1400 純防守)、panel 1500、popup/confirmpopup 1500、menupopup 1600、combobox/bandbox/colorbox/errorbox 1600、datebox/daterangebox 1700、toolbar popup 1600、notification/toast 1800、drawer root 1200。

### 🐛 兩個「死值」(順手清掉)

子代理另外揪出兩個**根本不會生效**的值:
- **searchbox `88000`**(`searchbox.css:140`):ZK 官方三主題(sapphire/silvertail/breeze)此處都用 **1000**,且 Searchbox 走一樣的 `setFloating_`/`setTopmost`(開啟即 inline ≥1800)。88000 是**誤抄的孤兒值**,功能上惰性。ZK 原始碼唯一的真「88xxx」是**無關的** drag-ghost `88800`(`widget.ts:705`)。
- **drag-ghost `90000`**(`_dnd.css:44,63`):**死碼**。真正拖曳一開始,`zk/drag.ts:654` 就把 ghost 的 inline z-index 蓋成 **88800**。Marble 的 90000 永不套用。

### 89000/89500 才是真階梯;88000 不是

`.z-apply-mask 89000 < .z-apply-loading 89500` 是**從 ZK 7 `myatlantic` 沿用至今的刻意設計**(busy 遮罩恆在自己的 spinner 之下,提示不被自己的底遮住),純 CSS、無 JS 介入。`88000` 不屬於這把階梯。

**硬約束(重編時唯一要守的三條)**:(1) `apply-mask < apply-loading`;(2) `.z-loading > .z-modal-mask` 基線(現為 1450 > 1400);(3) 要「永遠贏」的 `.z-error` 不可低於其「壓過一切」的角色。除此之外,ZK 從不回讀其他靜態值(僅 `.z-loading` 例外),幾乎全可重編。

---

## 6. 對 #4 實作的**修正後**建議

§5 的 runtime 結論把 #4 的形狀從 roadmap 原想的「新造 Bootstrap 1000–1080 ladder + 重編所有值」,改成**更小、更誠實**的兩件事。核心體悟:**替 ZK 會開的浮層造一把 CSS ladder 是自欺**——JS 一顯示就 inline ≥1800 蓋掉。真正該做的是「說出真相 + 只 token 化真正 load-bearing 的值」。

**Part 1 — 說出真相(最高價值產出)**
新 `doc/spec/zindex-scale.md` 規範,講清楚:
- **ZK runtime base 1800 + 扁平全域計數器**;floating widget 一顯示即 inline ≥1800、蓋過所有 CSS。
- 因此**元件 CSS 的 z-index 對 ZK 浮層只是 pre-paint fallback**——不要再有人以為改 CSS 能調浮層順序。
- 企業 App 指南:自訂 chrome 想在 ZK 浮層**之下**→ 用 < 1800;想在**之上**→ 別硬幹 z-index,改用 ZK widget 或 `setTopmost()`(否則會和 1800+N 計數器打架)。
- 唯二 JS 會回讀的例外:`.z-loading`(→ 配對 mask = z−1)。

**Part 2 — 只 token 化 load-bearing 值(小而外科)**
新 `src/main/resources/web/zul/css/tokens/_zindex.css`(加進 `scripts/build-css.js` `normFiles`——同 `_print.css` 的坑;build 後 grep `norm.css.dsp` 確認)。**值 = 現值,零視覺回歸**:
- `--zk-index-nav: 1000`、`--zk-index-loading: 1450`(> mask)、`--zk-index-loadingbar: 2000`、`--zk-index-slider-tooltip: 60000`、`--zk-index-busy-mask: 89000`、`--zk-index-busy-loading: 89500`、`--zk-index-fullscreen: 99999`、`--zk-index-error: 9999999`。
- 另立 `--zk-index-float-fallback: 1800`(= ZK runtime base)**當作註解性常數**:區間 B 的 cosmetic fallback 若要收斂,一律指向它(語意誠實:「這只是 fallback,ZK 會蓋掉」),而非維持 1400/1500/1600/1700 的假階梯。是否真去改那些 cosmetic 值屬**選項**(cosmetic-only、低優先);至少加註解「fallback only — ZK overrides at runtime ≥1800」。

**Part 3 — 順手清兩個死值(§5 🐛)**
- searchbox `88000` → 對齊 `--zk-index-float-fallback`(反正惰性,消除誤導)。
- drag-ghost `90000` → 加註「ZK `zk/drag.ts:654` 拖曳時 inline 蓋為 88800,此值不生效」;或直接對齊 float-fallback。

**Part 4 — `.z-index-*` utility + 文件收尾**
- `.z-index-*` utility(roadmap 有要):**只暴露 load-bearing 語意層 + `float-fallback`**,不暴露魔術數字;附企業指南(見 Part 1)。
- 預覽頁 `utility/zindex.zul`(可選)。
- 回歸驗證:computed-style 探針證明**命名前後浮層堆疊順序不變**(重點驗 CSS-only 那幾個:nav/loadingbar/slider-tooltip/busy-mask<loading/error)。
- 完工翻 `framework-feature-gaps.md` #4 為 ✅。

---

## 7. 風險與待辦

- **回歸風險其實很低**:區間 B 幾乎全 cosmetic(JS 蓋掉),命名不改值 = 零回歸;真正要驗的只有 §5 那張表裡標 CSS-only / load-bearing 的少數幾個。
- **三條硬約束不可違反**(§5):`apply-mask < apply-loading`、`.z-loading > .z-modal-mask`、`.z-error` 壓過一切。
- **`.z-loading 1450` 是 load-bearing**:JS 回讀它算 mask(`utl.ts:464`),改值要記得配對 mask 會自動 = z−1,別讓它 ≤ 1400。
- **不要替 ZK 浮層造假 ladder**:這是本稽核最重要的設計判斷——與其排 1400/1500/1600/1700,不如誠實指向 `float-fallback: 1800` + 文件說明。
- **`normFiles` 硬編碼坑**:新 token 檔漏加會靜默不打包(同 `_print.css`)。
- **範圍決策待使用者拍板**:Part 1（純文件）幾乎零風險必做;Part 2/3（token 化 + 清死值）小而值得;「是否連 cosmetic fallback 一起收斂成 float-fallback」是 optional。

---

## 附錄:完整清單來源

掃描指令(可重跑核對):
```bash
grep -rn "z-index:" src/main/resources/web/ --include="*.css" \
  | grep -oE "^[^:]+:[0-9]+:\s*z-index:\s*-?[0-9]+" \
  | sort -t'=' -k2 -n
```
數值分佈:`-1`×2、`1`×28、`2`×14、`8`×1、`10`×5、`12`×1、`14`×1、`16`×1、`20`×5、`25`×1、`30`×1、`40`×1、
`99`×1、`100`×11、`1000`×1、`1200`×1、`1400`×2、`1450`×1、`1500`×5、`1600`×10、`1700`×2、`1800`×1、`2000`×1、
`60000`×3、`88000`×1、`89000`×1、`89500`×1、`90000`×2、`99999`×1、`9999999`×1。
