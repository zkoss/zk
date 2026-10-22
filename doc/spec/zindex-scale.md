# 疊層尺度 z-index（Marble）

本文說明 Marble 的 z-index 疊層尺度:一組 `--zk-index-*` token、對應的 `.z-index-*` utility,以及——**最重要的**
——ZK runtime 如何在背後動態決定浮層順序,因此哪些 z-index 值真正「說了算」、哪些只是 fallback。

> 互動教學頁:<${PREVIEW_URL}/utility/zindex.zul>(原始檔 `zkpreview/src/main/webapp/web/utility/zindex.zul`)。
> 定義:`zul/src/main/resources/web/zul/css/tokens/_zindex.css`;utility:`utility/_layout.css`;回歸測試:`zkpreview/src/test/playwright/zindex-scale.spec.ts`(`zindex` 專案)。
> 稽核全紀錄(每個現有 z-index 的 selector 逐一歸類)保存於本機 `tasks/` 下,不在版本庫中;本文件保留其中的決議與理由。

---

## 0. TL;DR — 先懂這件事

**ZK 的 JS client 在浮層一顯示時,就以 inline style 覆寫它的 `z-index`。** 這是理解整套尺度的前提:

- `zk/widget.ts` 的 `_topZIndex()`(base **1800**)維護一把**扁平、全域、單調遞增**的計數器。
- 任何 floating widget(Window、Popup、Menupopup、Combobox/Bandbox/Datebox 下拉、Panel、Drawer、Toast、Errorbox、Searchbox…)一 `setVisible(true)` 就被寫上 `node.style.zIndex = _topZIndex(...)`(≥1800)。
- **inline style 永遠贏過 stylesheet**(不論 specificity / `!important`)。誰最後開/被 bring-to-front 誰就在最上面。

**推論:對「ZK 會開的浮層」而言,元件 CSS 的 z-index 只是 pre-paint fallback,不決定最終堆疊。** 因此本尺度**只把真正 load-bearing 的值(ZK 不當它是 floating widget 的那些)token 化**;其餘維持 cosmetic fallback 並在此註明。

---

## 1. Token 尺度 `--zk-index-*`

定義於 `tokens/_zindex.css`(unlayered `:root`,bundle 進 `norm.css.dsp`)。**值 = 原本硬編碼的數字**,命名它們是零視覺回歸。

| Token | 值 | 用途 | 性質 |
|-------|----|------|------|
| `--zk-index-nav` | 1000 | navbar 下拉 | **load-bearing**(nav 非 floating widget) |
| `--zk-index-float-fallback` | 1800 | = ZK runtime base;ZK-managed 浮層的誠實 fallback、App overlay 的參考點 | 參考常數 |
| `--zk-index-loading` | 1450 | 全域「請稍候」 | **load-bearing**(JS 回讀,見 §3) |
| `--zk-index-loadingbar` | 2000 | 頂部進度條 | **load-bearing**(非 floating) |
| `--zk-index-slider-tooltip` | 60000 | slider 拖曳提示 | **load-bearing**(靜態,無 float) |
| `--zk-index-busy-mask` | 89000 | 元件級 busy 遮罩 | **load-bearing**(須 < busy-loading) |
| `--zk-index-busy-loading` | 89500 | 元件級 busy spinner | **load-bearing**(在自己遮罩之上) |
| `--zk-index-fullscreen` | 99999 | tbeditor 全螢幕 | **load-bearing**(非 floating) |
| `--zk-index-error` | 9999999 | 致命 JS 例外框 | **load-bearing**(壓過一切,含 runtime 1800+N) |

### 三條硬約束(改值前必讀)

1. `busy-mask (89000) < busy-loading (89500)` —— busy 遮罩恆在自己的 spinner 之下,提示不被自己的底遮住。
2. `loading (1450) > .z-modal-mask (1400)` —— 全域 loading 的配對遮罩由 JS 算成 `loading − 1`(`zk/utl.ts`),故 loading 必須高於 modal-mask 基線。
3. `error (9999999)` 必須壓過所有東西,包含 runtime 的 1800+N 計數器。

---

## 2. Utility `.z-index-*`

定義於 `utility/_layout.css`(`@layer zk-utilities`)。**給 App 自訂的 positioned 元素用,不是給 ZK widget 用。**

```css
.z-index-nav            { z-index: var(--zk-index-nav); }
.z-index-float-fallback { z-index: var(--zk-index-float-fallback); }
.z-index-loading        { z-index: var(--zk-index-loading); }
.z-index-loadingbar     { z-index: var(--zk-index-loadingbar); }
.z-index-error          { z-index: var(--zk-index-error); }
```

- **須搭配 `.z-position-*`**:z-index 只對 positioned 元素生效。
- 只暴露 App 用得到的語意層(不暴露 busy-mask/slider-tooltip 等主題內部值)。

### 企業 App 指南:別和 runtime 計數器硬幹

- 想讓自訂 chrome(sticky header、custom overlay)**壓在 ZK 浮層之下** → z-index 保持 **< 1800**(`--zk-index-float-fallback`)。這是絕大多數情況該做的。
- 想**壓在 ZK 浮層之上** → **不要**用更大的 z-index 去出價(你會和 1800+N 的計數器打不完的仗)。改用真正的 ZK widget,或對你的 widget 呼叫 `setTopmost()`,讓它進入 ZK 的浮層登記表。
- 只有「必須永遠在最上層」的極端情況(全螢幕遮罩、致命錯誤)才用 `--zk-index-error` 等級。

---

## 3. Load-bearing vs cosmetic —— 逐類對照

`.z-loading`(全域 loading)是**唯一被 JS 回讀**的靜態值:`zk/utl.ts` 讀它算配對遮罩 = `z − 1`。改它時記得遮罩會自動跟著低 1。

下列元件的 CSS z-index 是 **cosmetic fallback**(ZK 一顯示就 inline ≥1800 蓋掉),**刻意維持原值不動**(改了只影響 pre-paint 一瞬,且 ZK 官方主題亦如此):

| 元件（selector） | cosmetic 現值 | runtime 實際 |
|------------------|--------------|-------------|
| Drawer `.z-drawer` | 1200 | inline ≥1800 |
| Window mask `.z-modal-mask` / `.z-mask` | 1400 | JS `new zk.eff.FullMask({zIndex})` 設定 |
| Popup / ConfirmPopup / Panel faker / Window faker | 1500 | inline ≥1800 |
| Combobox / Menupopup / Bandbox / Errorbox / Toolbar / Colorbox / Chosenbox / Timepicker / Bandpopup | 1600 | inline ≥1800 |
| Datebox / DaterangeBox 下拉 | 1700 | inline ≥1800 |
| Toast | 1800 | inline ≥1800 |

### 不收編的值（z 1–40 與 99／100 帶）

- **規則**:元件內部堆疊的 z-index 1–40，以及 z 99／100 的混合帶，維持原值、不收編進 `--zk-index-*` 尺度。
- **理由（1–40）**:這些值只在元件自己的 DOM 內相對排列（表頭蓋內容、beak／箭頭、frozen 欄、tab 重疊、拖曳把手等），數值小、彼此不跨元件比較；收編進全域尺度沒有意義且有風險。
- **理由（99／100）**:語意混雜（例如 `.z-menubar`、自繪捲軸、errorbox／notification 的 beak、coachmark），不屬於浮層排序的問題。若日後要新增 `sticky`／`fixed` 語意層，menubar 與捲軸可再評估收編；beak 與連接線維持元件內。

---

## 4. 清掉的兩個「死值」

稽核揪出兩個**根本不生效**的舊值,本次順手誠實化:

- **searchbox `.z-searchbox-popup` 88000 → `var(--zk-index-float-fallback)`(1800)**:Searchbox 是 floating widget(ZK 開啟即 inline ≥1800),舊的 88000 從不套用,且與 ZK 官方主題(用 1000)不符——是誤抄的孤兒值。改成 float-fallback 語意誠實。
- **drag-ghost `.z-drag-ghost` / `.z-drop-ghost` 90000(保留 + 註解)**:真正拖曳一開始,`zk/drag.ts` 就把 ghost 的 inline z-index 蓋成 **88800**,故 CSS 的 90000 僅存在於 pre-drag 一瞬。保留數值但加註說明,避免誤導。

---

## 5. 建置與載入

- `_zindex.css` 是 unlayered `:root` token 檔(同其他 token 檔)。
- 必須列於 `scripts/build-css.js` 的 `normFiles`(token 區塊、`_splitter.css` 之後)—— 此陣列**硬編碼**、非自動掃描,漏列會**靜默不打包**。
- token 進 `norm.css.dsp`(最先載入);各元件 `.css.dsp` 以 `var(--zk-index-*)` **無 fallback** 消費(同 Component Theme Variables 慣例)。`loadingbar` 落在 `footer.css.dsp`、`misc`/`error`/`busy` 落在 `norm.css.dsp`。

---

## 6. 測試守則

`zindex` Playwright 專案(`zindex-scale.spec.ts`)以 computed-style 驗證 **load-bearing 值命名前後不變**、以及三條硬約束:

- `.z-index-loadingbar` 解析為 `2000`、`.z-index-error` 為 `9999999`、`.z-index-nav` 為 `1000`;
- `busy-mask (89000) < busy-loading (89500)`;
- `.z-index-loading (1450) > 1400`。

```bash
npx playwright test -c zkpreview/src/test/playwright/playwright.config.ts --project=zindex
```

---

## 7. 注意事項 / 限制

- **不替 ZK 浮層造假 ladder**:本尺度刻意**不**把 cosmetic 浮層(1200–1800)重編成整齊階梯——那對 runtime 無效、只是自欺。真正的順序由 ZK 的 1800+N 計數器決定。
- **不改 ZK runtime 行為**:計數器 base 1800、bring-to-front 遞增都是框架行為,主題不介入。
- **App 出價戰**:企業 App 若用超大 z-index 硬壓 ZK 浮層,會與 runtime 計數器衝突;正解是用 ZK widget / `setTopmost()`(見 §2 指南)。
