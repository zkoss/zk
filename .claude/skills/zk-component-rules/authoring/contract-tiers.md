# Contract Tiers — Authoring Discipline for zk-component-rules

This file is **required reading** for any agent or human about to write or modify either:
- `.claude/skills/zk-component-rules/components/<comp>.md` (this skill — theme-portable)
- `doc/contracts/<comp>.md` (Marble theme contract — theme-specific)

The two files form a **two-tier contract**. This guide defines the boundary, the four predicate classes, the iceblue-CSS-mining workflow, and the refuse-to-emit rules that keep the boundary clean.

---

## TL;DR

| 層 | 檔案 | 內容性質 | 跨主題會變嗎？ |
|---|---|---|---|
| 結構不變式 (Structural Invariants) | `.claude/skills/zk-component-rules/components/<comp>.md` | DOM、state class、幾何/關係、「狀態必須有區別」之類的**通用述語** | **不會**。所有主題都該通過 |
| 主題契約 (Theme Contract) | `doc/contracts/<comp>.md` (+ optional `.html` mockup) | 具體 token 綁定、明確設計取捨、預期解析值、`## Outcome assertions`（整頁尺度 M-rows） | **會**。每個主題一份 |

**Evaluator 的工作 = 兩層都通過 + M-rows 過 + AI visual review 不報嚴重 finding**。結構不變式像「物理定律」，主題契約像「這顆星球的重力」，M-rows 像「整體該看起來像個 X」。

---

## 為何需要分層

直接拿 iceblue baseline 當驗證標準會發生三件事：

1. **顏色被鎖死**：iceblue 用 `#e6f2fa`，Marble 想用 primary-container，pixel diff 必然 fail。
2. **數值被鎖死**：iceblue 的 node padding 是 4px×8px，Marble 想要 8px×12px 更舒展的卡片，數字一改就 fail。
3. **錯誤的取捨被當教條**：iceblue 把 expand icon 縮成 12px、塞在右下角——這是 2010 代的密度取捨，不該變成 2026 主題的硬性規範。

但是**有些事是 iceblue 對的**：org chart 必須是樹、父節點必須水平置中、selected 必須跟 unselected 看得出差別。這些是 organigram 之所以是 organigram 的**語意角色**，跟視覺風格無關。

**做法：把「語意角色」沉澱成結構不變式（universal），把「視覺值」鎖在主題契約（per-theme）**。看 iceblue baseline 是為了**抽取語意角色**，不是為了**複製像素**。

---

## 五類可驗證述語

A/B/C 屬於 skill（主題無關）；D 屬於 contract（主題特定 + 自底向上）；M 屬於 contract（主題特定 + 自頂向下整頁尺度，與 §3d AI visual review 形成閉環）。

### A. 結構述語 (Structural) — 主題無關

DOM 拓撲、class 出現條件、role/aria。完全由 ZK widget 行為決定，主題沒得選。

**範例 (organigram)**：
- 每個非根節點恰有一個父節點，整體形成樹
- `.z-orgitem-selected` 在被選中時出現、被取消時消失
- `.z-orgitem-close > .z-orgchildren` 必為 `display: none`
- root 有 `role="tree"`、每個 item 有 `role="treeitem"`

這層直接寫進 `.claude/skills/zk-component-rules/components/<comp>.md`，**所有主題共用**。

### B. 關係/幾何述語 (Relational) — 主題無關，用 tolerance + 相對比較

可量測但不指定絕對值。**不寫「padding = 8px」，寫「sibling 對齊在 ±2px 內」**。

**範例 (organigram)**：
- 同 generation 的 siblings：`|max(top) − min(top)| ≤ 2px`
- 父節點水平置中：`|parent.centerX − midpoint(leftmost, rightmost child centerX)| ≤ 2px`
- 連接線存在且可見：parent-bottom 到 child-top 之間有非 transparent、長度 > 0 的線段
- 節點有可見邊界：`border-width ≥ 1px ∧ border-color ≠ transparent` **∨** `background ≠ container.background` **∨** `box-shadow ≠ none`
- 文字無裁切：`text rendered width ≤ box content width`
- 文字可讀：label 對 box 背景的 WCAG contrast ratio ≥ 4.5:1

關鍵：**至少一項成立**（disjunction）而非「邊界必為 1px solid grey」。這讓不同主題可以用 border、background-tint、shadow 之中**任何一種**畫出「我是個 node」的訊息。

### C. 狀態差異述語 (State-Differs) — 主題無關

「selected 必須看得出來」是不變式；「selected 必須是藍色」不是。

**範例 (organigram)**：
- `selected` vs 未選：`{border-color, background-color, outline, box-shadow, color}` **至少一項**測得到差異
- `disabled` vs `enabled`：`opacity < 1` **∨** text contrast 低於 enabled
- `hover` vs resting (在 selectable 非 disabled 節點上)：`{background-color, border-color, box-shadow}` **至少一項**有變化
- `focus-visible`：必須有 outline **∨** box-shadow ring **∨** border-color 變化 (任一可被鍵盤使用者察覺)
- `open` vs `closed`：子節點 `display: none` (或不在 DOM 中) ↔ 子節點 visible

這層也寫在結構規則檔，跟 A、B 並列。**這是讓「可驗證」與「主題自由」相容的關鍵設計**。

### D. Token 綁定述語 (Token-Bound) — 主題特定

具體值，但綁 token、配 DESIGN.md 章節，**不綁 baseline pixel**。寫進 `doc/contracts/<comp>.md`。

**範例 (organigram, Marble)**：
- `.z-orgnode { border-radius: var(--zk-shape-card); }` — 來源 DESIGN.md §5
- `.z-orgitem-selected > .z-orgnode { background-color: var(--zk-color-primary-container); }` — 來源 DESIGN.md §3
- 連接線 `border-color: var(--zk-color-outline-variant)` — 來源 DESIGN.md §11

換主題 = 換 token 值或換這份契約；A/B/C 不動。

### M. Outcome 述語 (Outcome-Level / 整頁尺度) — 主題特定，top-down

A/B/C/D 都是「自底向上」的：對單一 selector 的單一屬性 assert。但一份契約**全 PASS** 仍可能視覺破損 — 因為沒人問「整體看起來像不像那個東西」。M-row 補這個洞。

寫進 `doc/contracts/<comp>.md` 的 `## Outcome assertions` 表，與 `## Design Contract` prose 並列。

**特徵**：
- **整頁尺度 / 跨 selector**：「每個 `.lm_header` 內的 `.lm_controls` 跟 tabs 同一橫排」、「面板互相 dock 不重疊」、「outer wrapper 沒有 redundant 卡片」
- **bbox geometry 為主**：用 `getBoundingClientRect()`，不是 `getComputedStyle()`
- **disjunctive / tolerance-based**：「border ≥ 1px OR shadow ≠ none OR bg ≠ transparent」、「±2px」、「≥ 95%」
- **outcome 不是 recipe**：assert「看起來是張卡片」，不 assert「`border: 1px solid #ccc`」
- **Failure blocks VERIFIED**：任一 M-row FAIL 就擋住 verified，即使 D-tier 全過

**範例 (goldenlayout)**：
- M1: every `.z-goldenpanel` 有可見框 AND outer `.z-goldenlayout` 沒有 redundant outer card
- M4: 同一 `.lm_header` 內所有 `.lm_tab` siblings 的 `bbox.top` range ≤ 2px
- M9: `.lm_controls` cluster bbox.right 在 `.lm_header` 右緣 8px 內，且有 ≥ 2 個可見 icon
- M13: `.lm_controls` 跟首個 `.lm_tab` **同一橫排**（vertical centers within 4px）

**與 B-tier 的關係**：B-tier 是「主題無關 + 單 selector 範圍」的關係述語；M-row 是「主題特定 + 跨 selector / 整頁範圍」的結果述語。一條 B-tier 可被**升級**成 M-row 當它的視覺判定門檻在「整體看起來對不對」這個層級 — 但升級後就脫離 skill 進到 contract。

**與 §3d AI visual review 的閉環**：Evaluator 跑 AI visual review 找到的 finding，如果在 M-rows 抓不到（geometry 通過但 AI 用眼睛看仍 FAIL），就**回頭加一條 M-row**到契約。M-row 集合會隨著每一次 finding 變嚴。goldenlayout 的 M13 就是這條路徑長出來的：M9 只查 X 軸 right-anchoring，AI vision 看出 icons 跑到第二行，M13 補了「同一橫排」的 Y 軸條件。

換主題 = 重新寫一份 M-rows（因為它們對應的視覺意圖在 DESIGN.md），但**predicate 形式**（disjunction + tolerance）跨主題通用。

---

## 從 baseline 抽取語意角色的紀律

看 `doc/contracts/baselines/<comp>-iceblue.png` 時，**只能問三種問題**：

1. **這個元件有哪些可區分的狀態？** (selected、disabled、open/closed、hover、focus...) → 變成 C 類述語
2. **這些狀態之間是用什麼維度區分的？** (顏色、邊框、填色、形狀、位置、文字粗細...) → 變成「至少一項差異」的 disjunction
3. **元件的拓撲跟相對位置有哪些幾何約束？** (父在子的水平中點上方、siblings 同高、line 連到 box 邊緣...) → 變成 B 類述語

**不能問的**：
- ~~這個顏色叫什麼？要照抄嗎？~~
- ~~這個 padding 是幾 px？~~
- ~~這個 icon 是幾號 size？~~

把 baseline 當「**這個元件的視覺語法表**」(grammar)，不是「**這個元件該長什麼樣**」(specimen)。

---

## 反面範例：警惕「mirrors iceblue」

現行 `doc/contracts/organigram.md` 有一條：

```
| node-6 | `.z-orgnode` | min-width | `64px` | DESIGN.md §10 (smallest legible org card; mirrors iceblue) |
```

「mirrors iceblue」這個理由**踩到本紀律的紅線**。正確的寫法二選一：

- **如果這值有設計原因**：寫出來。例：「64px 是顯示 4 字元中文標籤 + 12px icon + 內距的最小寬度」——這是可被質疑、可被替換的設計判斷。
- **如果這值沒設計原因，只是「iceblue 也用 64」**：把它**降級到 B 類述語**（「`width ≥ text-width + 2 × horizontal-padding` 即可」）然後從主題契約刪掉。

主題契約裡每一個絕對值，都必須能回答「**為什麼是這個值？來自 DESIGN.md 哪一節？**」。回答不出來的，搬到結構規則或刪掉。

---

## 工作流：新主題從零寫一個 contract

1. **打開舊主題 baseline PNG**（任何一個 — iceblue / breeze / 隨意）
2. **列語意角色**：寫一份簡短的「這個元件有哪些狀態、哪些幾何關係、哪些必須區分的維度」清單。**不抄數值**。
3. **寫結構規則檔** `.claude/skills/zk-component-rules/components/<comp>.md`：DOM、state class、composition invariants、A/B/C 三類述語。一旦寫好，原則上**所有未來主題共用**——只在發現 ZK 行為變動時才更新。
4. **寫主題契約** `doc/contracts/<comp>.md`：依本主題 DESIGN.md 的色票、間距、形狀、動效規則，挑選 token、寫具體期望值。每一條都附 `source` 欄位指向 DESIGN.md 章節或設計判斷的理由。
5. **跑 evaluator**：
   - 結構規則檔的述語：每條都應 pass（如果不 pass，要嘛是 CSS 寫錯，要嘛是 ZK 行為改了——後者才能修規則檔）
   - 主題契約的具體值：每條都用 computed style 比對
6. **換主題時**：步驟 4 重做，步驟 3 維持不動

---

## Evaluator 怎麼讀這兩層

```
.claude/skills/zk-component-rules/components/<comp>.md
  ├─ DOM 結構 → DOM 預期 (querySelector 應存在 / 不應存在)
  ├─ State classes → 對應狀態切換時 class 應出現
  ├─ Composition invariants → A 類: 直接驗 computed style 是否等於規定值 (如 position: relative)
  └─ Relational invariants + State-differs invariants → B/C 類述語

doc/contracts/<comp>.md
  └─ Expected values 表格 → 每條 querySelector + property → 比對 computed value
```

如果一個元件結構檔目前沒有顯式列 B、C 類述語（只有 DOM/state class/composition），代表 evaluator 只能驗 A + D。**這是目前的缺口**——逐元件補上 `## Relational invariants` 與 `## State-differs invariants` 兩節。

---

## JS source reading — mold 不是 optional

ZK widget 的「JS source」其實**有兩種**，缺一不可：

| 檔案類型 | 角色 | spec-author 在意什麼 |
|---|---|---|
| `<Widget>.ts` (或 `.js` 主檔) | 行為層：state toggle、setter、event 處理、生命週期 | state class 何時 add/remove；attribute getter/setter；server-side property mapping |
| `mold/<comp>.js` (function `<comp>$mold$(out)`) | **DOM 渲染層 — 真正的 DOM ground truth** | 實際輸出的 HTML 樹、寫死的 class、寫死的 `role`/`aria-*` attribute、wrapper 是否無條件出現 |

**只讀 `.ts` 不讀 mold 是常見錯誤**。`.ts` 裡的 `redraw_()`、`replaceHTML_()` 通常**呼叫 mold function**而不是自己組 HTML，所以光看 `.ts` 看不出真正的 DOM 形狀。

### 實例（為何這條規則存在）

`stepbar` 一輪 spec-author 漏讀了 `mold/step.js`，結果 contract 假設 `.z-step-content` 只在 wrapped-label 模式才出現。但 mold 第 16-19 行寫得很清楚：

```js
out.push('<div id="', uuid, '-content" class="', this.$s('content'), '" role="listitem" ...>');
out.push('<span ... class="', this.$s('icon'), ...);
out.push('<span ... class="', this.$s('title'), ...);
out.push('</div></div>');
```

`.z-step-content` **無條件包**`.z-step-icon + .z-step-title`。Contract 裡所有 `.z-step > .z-step-icon` 形式的 selector 全是錯的，evaluator 量了 28/28 fail，整個 generator pass 白做。

### 強制流程

1. **每個 component 在 contract 的 `js-source-files:` 必須同時列出**：
   - `<Widget>.ts`（或主 widget JS）
   - 對應的 `mold/<comp>.js`（若存在）
2. **hash 範圍要涵蓋所有列出的檔案**——用 `shasum -a 256 <files...>` 一次算，不要只算 .ts。
3. **spec-author 在重派或 drift recovery 時，必須讀完所有列出的檔案**，不能只挑 .ts 讀。如果 mold 缺漏，先把它加進 `js-source-files` 再讀。
4. **如何快速找 mold**：
   ```bash
   find /Users/hawk/Documents/workspace/ZK*/zkcml -path "*mold*" -name "<comp>.js" 2>/dev/null
   find /Users/hawk/Documents/workspace/ZK*/zk*/src/main/resources -path "*mold*" -name "<comp>.js" 2>/dev/null
   ```
   找不到 → widget 用 default mold 或自己組 HTML，這時退回 `.ts` 看 `redraw_()` / `replaceHTML_()` 找 `out.push`/`zk.WidgetRenderHelper`。

### 例外：哪些元件沒有 mold

- ZK core widget 有些用 default DOM rendering（不寫 mold function），這時 `.ts` 裡的 `redraw_()` 是 DOM ground truth。
- 第三方包裝（T3 例如 pdfviewer, tbeditor）的內部 DOM 由 library 注入，`.ts` 只負責 mount/destroy；這時 ground truth 在 library 的執行結果，evaluator 用 live DOM 量是正確的——`js-source-files` 只列 ZK 自己的 `.ts` 即可。

### 規律

> **判斷準則**：寫 contract 前，問「這個元件的 `<root>` outerHTML 是誰決定的？」
> - 答案是 `mold/*.js` → mold 必讀，必入 `js-source-files`。
> - 答案是 `redraw_()` in `.ts` → `.ts` 必讀（你已經會讀了）。
> - 答案是 third-party library 注入 → live DOM measurement 為準，spec-author 只記 wrapper boundary。

---

## 把 iceblue CSS 當作結構訊號的證據來源

iceblue 是 ZK 內建主題，已在生產被使用十幾年；它的 CSS 對「ZK 究竟會 emit 哪些 class、依賴哪些 pseudo-element、在哪些狀態組合下需要樣式」這件事是**現成的證據**。**只要紀律夠**，把它當作結構規則檔的訊號來源比讀 widget JS 還快——JS 告訴你**哪個 setter toggle 哪個 class**，CSS 告訴你**哪些 class 真的被當作樣式錨點**（有時 widget 會 emit 但從未被任何主題使用——那種 class 是雜訊，不一定要進規則檔）。

### 定位 iceblue CSS 來源（路徑可變，須有 fallback）

iceblue CSS 的取得路徑不固定（取決於使用者本機展開了哪個版本到哪裡），**不要 hardcode 任何單一路徑**。依下列順序搜尋，找不到就停下來問使用者：

1. **專案內快照**（最常用）：`temp/iceblue_c-*/web/iceblue_c/js/**/<comp>.css.dsp`
   ```bash
   find temp -path "*iceblue*" -name "<comp>.css.dsp" 2>/dev/null
   ```
2. **ZK source tree**（如果使用者把 ZK source 放在工作站）：`/Users/hawk/Documents/workspace/ZK*/zk*/src/main/resources/web/js/**/<comp>.css*`
   ```bash
   find /Users/hawk/Documents/workspace -path "*zul/src/main/resources/web/js*" -name "<comp>.css*" 2>/dev/null
   ```
3. **使用者目前未配置 iceblue CSS** → 在 conversation 印出：
   ```
   iceblue CSS for <comp> not found. Searched:
     - temp/iceblue_c-*/web/iceblue_c/js/**/<comp>.css.dsp
     - /Users/hawk/Documents/workspace/ZK*/...
   Please provide a path to the iceblue CSS source for <comp>,
   or confirm to proceed without iceblue mining (B-tier predicates
   from CSS mining will be skipped; produce A + C tiers only).
   ```
   等使用者回覆再繼續。**不要**自己往奇怪的路徑亂找，**不要**假設沒有 iceblue source 就靜默跳過。

定位到檔案後，記下絕對路徑供本次任務使用；不要寫進規則檔（規則檔不留 iceblue 路徑——它跟使用者本機環境相關）。

### 可以從 iceblue CSS 萃取的東西 (Structural Signals)

把 iceblue 的 `.css` / `.css.dsp` 檔讀過一遍，**只看選擇器與 declaration 的 property 名稱，不看 value**，可以挖出：

1. **State class 全清單** — iceblue 用了 `.z-orgitem-selected`、`.z-orgitem-disabled`、`.z-orgitem-non-selectable`、`.z-orgitem-close`，這四個就是 organigram 真正會出現的狀態。如果結構規則檔少列了任何一個，這就是缺漏。
2. **State 組合 (combined-state)** — 例如出現 `.z-orgitem-selected.z-orgitem-disabled > .z-orgnode { ... }` 表示這個組合**會發生且需要被處理**，應該加進「States to evaluate」。
3. **Pseudo-element 參與 layout 還是 decoration** — `::before` / `::after` 上若有 `position: absolute` + `top/left/width/height`，就是 layout-critical（必須在結構規則檔說明「theme must keep these positioned」）；若只設 `background` 沒設 position，是純裝飾，主題可以丟掉。
4. **互動契約** — `cursor: pointer` 標出點擊目標、`pointer-events: none` 標出 interaction-suppression、`display: none` 在某個 state class 下標出可見性契約（如 `.z-orgitem-close > .z-orgchildren { display: none }`）。這些 property **本身**是契約，property 的 value 也是契約（`none` vs `block`）——這是少數「value 也能直接抄進結構規則檔」的情形，因為它表達的是行為而非外觀。
5. **過渡的 property 清單** — `transition: background-color, border-color, box-shadow` 告訴你「主題該讓這三個 property 平滑變化」。**duration 與 easing 是主題自由**，但**有沒有 transition、transition 哪些 property**，是 UX 契約。
6. **選擇器特異性的層級關係** — iceblue 寫 `.z-orgitem-selected > .z-orgnode` 而不是 `.z-orgnode.z-orgitem-selected`，這證實「state class 在 `.z-orgitem` 上、視覺套用在 `.z-orgnode`」的 widget 約定。新主題若反向選擇必失敗。
7. **DOM 假設** — iceblue 寫 `.z-grid > .z-grid-body > table > tbody > tr` 暗示這條路徑是穩定的；可以把它編成 DOM 假設加進結構規則檔，提醒未來主題不要假設別的 DOM 形狀。

### 一定要丟掉的東西 (Visual Values — DO NOT IMPORT)

| 項目 | 為何要丟 |
|---|---|
| 顏色（含 hex、rgba、currentColor 之外的具名色） | iceblue 的色票是它自己的設計，與 Marble DESIGN.md 無關 |
| 具體尺寸 (px / em / rem 的絕對值) | padding、margin、font-size、border-radius 都是密度與形狀的設計取捨 |
| `font-family`、`font-weight` 的具體值 | typography 主題自決 |
| `box-shadow` 的具體 offset / blur / color | elevation 主題自決，但**「有 shadow」這件事**可能是 C 類差異述語 |
| `transition-duration`、`transition-timing-function` 的值 | motion 主題自決，但**「有 transition」這件事**可以保留 |
| `border-style` 之外的 border 細節（width、color） | 邊框存在性可以是 C 類差異；具體值丟 |
| 任何 vendor hack、IE workaround、deprecated 寫法 | 純歷史包袱 |

判斷準則：**「這條 declaration 若 value 完全改變、視覺隨之改變，元件還是 functional 嗎？」** 是 → 視覺值，丟。否（改了會壞掉、會少一個狀態、會看不出區別）→ 結構訊號，留。

### 工作流：從 iceblue CSS 反推一份結構規則檔的補強清單

針對某個元件 (e.g. organigram)：

1. **定位 iceblue 對應 CSS 檔**（依上文 fallback 順序）。
2. **抽出所有獨特選擇器**：`grep -oE '\.z-[a-z-]+' <comp>.css.dsp | sort -u`（粗版；複合選擇器另處理）。
3. **比對結構規則檔現有的 State classes 與 DOM 區塊**，找出 iceblue 提到但規則檔沒寫到的：
   - 缺漏的 state class
   - 缺漏的 combined-state（兩個 class 同時出現的選擇器）
   - 缺漏的 pseudo-element 用法
4. **針對每個缺漏項目，分類**：
   - **A 類（結構）**：DOM/state 一定存在的事實 → 補進規則檔 DOM 或 State classes 區
   - **B 類（關係）**：可量測的幾何/對齊 → 補進 Relational invariants 區
   - **C 類（狀態差異）**：兩個狀態之間必有某維度不同 → 補進 State-differs invariants 區
   - **D 類（視覺值）**：丟掉
5. **交叉驗證 widget JS source**：iceblue CSS 提到但 JS 沒 toggle 的 class 可能是 dead code，要查證；JS toggle 但 iceblue CSS 沒用到的，可能是「ZK 提供但被 iceblue 忽略的 hook」——這種**仍要寫進規則檔**，因為新主題可能會用到。
6. **不更新主題契約** — 這個流程的產物是更準確的**結構規則檔**，不是新的主題契約。Marble 的 `doc/contracts/<comp>.md` 不應因為 iceblue CSS 而改變。

### 兩種典型的「iceblue CSS 教會我們的事」

**例 1：發現缺漏的 combined-state**
iceblue `organigram.css.dsp` 假如有 `.z-orgitem-selected.z-orgitem-close > .z-orgnode { ... }`——這證明「selected 但 closed」是合法狀態組合，應加進規則檔的「States to evaluate」清單。

**例 2：發現缺漏的 layout-critical pseudo-element 規則**
iceblue 寫 `.z-orgnode { position: relative; }` 加上 `.z-orgnode::after { position: absolute; left: 50%; ... }`——這證實「`.z-orgnode` 必須是 positioning context」。如果其他元件的規則檔還沒提這種約束，照本範例補上。

### 紀律檢查（不要做的事）

- ❌ **不要**把 iceblue 的 CSS 整段貼進結構規則檔當「reference implementation」。規則檔只描述行為與不變式，不放 CSS code。
- ❌ **不要**在規則檔寫「iceblue uses 8px padding here」這種句子——這是把視覺值偷渡進通用層。要寫，就寫「padding must be ≥ 4px to avoid clipping」這種述語。
- ❌ **不要**把 iceblue 的 z-index、background-image (sprite) 之類純視覺實作放進規則檔。
- ❌ **不要**在發現 iceblue 沒用到某個 state class 時就從規則檔刪掉它——可能 iceblue 只是當初沒設計到，不代表 widget 不會 emit。**以 ZK widget JS 為準**。

---

## Refuse-to-emit rules（硬性）

### 寫進 `.claude/skills/zk-component-rules/components/<comp>.md` 的內容絕不能含：

- 任何 hex (`#xxx` / `#xxxxxx`)、`rgb()`、`rgba()`、`hsl()`
- 任何 `var(--zk-*)`、`var(--md-*)` token 參照
- 任何 px/em/rem 的**絕對視覺值**（padding、margin、font-size、border-radius、box-shadow 等）
- 任何 MD3 / MUI / Mira / iceblue / Sapphire / DESIGN.md 名詞或章節參照

**例外**（這些 value 允許進結構規則檔，因為表達行為而非外觀）：
- `position: relative | absolute` 等 layout-critical positioning
- `display: none | block` 等可見性契約（搭配 state class）
- `cursor: pointer | default` 等 interaction 契約
- `pointer-events: none` 等 interaction-suppression 契約
- `transition` 的 **property name list**（不含 duration、easing 的值）

### 寫進 `doc/contracts/<comp>.md` 的內容絕不能含：

- 任何 DOM tree（ASCII 樹、class 階層）
- 任何 state-class 行為說明（「ZK adds `.z-button-disabled` when…」這類）
- 任何 framework-quirk 說明（DOM mutation、JS-injected attribute 等）
- 任何 `source` 欄位寫成「mirrors iceblue」、「matches legacy」、「same as ZK iceblue」這類**無設計理由的偷渡**

如果發現自己快寫出上述任一項，**停**——它屬於另一邊。

---

## 一行版

> **看 baseline 抽取語意角色 (states + relations + maintained distinctions)，把這些語意寫成主題無關的述語放結構規則檔；具體顏色/數字綁 token 放主題契約。Evaluator 兩層都驗，但兩層都不直接跟 baseline pixel 比對。**
