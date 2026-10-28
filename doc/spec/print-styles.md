# 列印樣式 `@media print`（Marble）

本文說明 Marble 的列印樣式：一組 opt-in 的列印可見性 utility，加上一套自動觸發的列印 reset。
目標客群（ERP / CRM）天天列印報表、發票、清單，因此這是 Tier 1 的企業級落差之一。

> 互動教學頁：<${PREVIEW_URL}/utility/print.zul>（原始檔 `zkthemelab/src/main/webapp/web/utility/print.zul`）。
> 所有效果**只在瀏覽器「列印預覽」（Ctrl-P / Cmd-P）下可見**。
> 實作：`zul/src/main/resources/web/zul/css/utility/_print.css`；回歸測試：`zkthemelab/src/test/playwright/print-utilities.spec.ts`（`print` 專案）。

---

## 0. TL;DR

| 層次 | 是什麼 | 用在哪 | opt-in？ |
|------|--------|--------|----------|
| 可見性 utility | `.z-d-print-{value}` | 每個元素自行加上，做「螢幕限定 / 列印限定」切換 | 是（逐元素） |
| 自動 reset | `@media print` 內的全域規則 | 隱藏浮層 chrome、解除 sticky、展開卷軸、陰影→邊框 | 否（每頁自動） |

設計選擇：**保留品牌色 / 背景**（不做 ink-saving 的黑白化）。這是使用者明確選定的「standard reset」範圍。

---

## 1. 可見性 utility：`.z-d-print-{value}`

### 定義

```css
/* zul/src/main/resources/web/zul/css/utility/_print.css */
@media print {
    .z-d-print-none         { display: none; }
    .z-d-print-block        { display: block; }
    .z-d-print-flex         { display: flex; }
    .z-d-print-grid         { display: grid; }
    .z-d-print-inline-block { display: inline-block; }
}
```

- 只在 `@media print` 生效，對螢幕顯示零影響。
- `value` 精選 5 個（`none / block / flex / grid / inline-block`），與 `_layout.css` 的 viewport `.z-d-{value}-{bp}` 同一組（Simplicity First，不做全矩陣）。
- 沒有 breakpoint 後綴 —— 列印沒有「寬度分段」的概念。

### 用法

```xml
<!-- 螢幕限定：正常顯示，列印時移除（工具列、導覽、"列印" 按鈕…） -->
<div class="z-d-print-none">Toolbar</div>

<!-- 列印限定：螢幕隱藏（z-d-none），紙上顯示（z-d-print-block） -->
<div class="z-d-none z-d-print-block">Confidential — printed copy</div>
```

> **cascade 說明**：`z-d-none` 與 `z-d-print-block` 同屬 `@layer zk-utilities`、同 specificity（單一 class）；`_print.css` 在 bundle 順序中排在 `_layout.css` 之後，故列印時 `z-d-print-block` 以「後者勝」覆蓋 `z-d-none`。

---

## 2. 自動列印 reset

無需 opt-in，`@media print` 下對每個 Marble 頁面自動生效。因 `zk-utilities` 是最上層 cascade layer，這些規則**無需 `!important`** 即可覆蓋 `zk-components` 的元件樣式。

| 行為 | 選擇器（摘要） | 為什麼 |
|------|---------------|--------|
| 隱藏浮層 chrome | `.z-modal-mask, .z-mask, .z-toast-position-wrapper, .z-loadingbar-position, .z-drawer, .z-window-resize-faker, .z-panel-move-block, .z-panel-resize-faker, .z-error` → `display:none` | modal 遮罩、拖拉 / 縮放 faker、toast、loading bar、drawer、浮動 error panel 都不該印在紙上 |
| 解除 sticky header | `.z-{grid,listbox,tree}.z-sticky-header .z-*-header` → `position:static` | 讓長表格跨頁分頁，而非每頁都釘住表頭 |
| 展開卷軸區 | `.z-grid-body, .z-listbox-body, .z-tree-body` → `overflow:visible; height:auto; max-height:none` | 讓完整資料集印出，而非只印螢幕上的卷軸視窗 |
| 陰影 → 邊框 | `[class*="z-elevation-"], .z-window, .z-panel, .z-paper, .z-groupbox` → `box-shadow:none; border:1px solid var(--zk-color-outline-variant)` | 陰影在紙上不會呈現，改用 hairline 邊框保留表面邊界 |

### 定義（節錄）

```css
@media print {
    .z-modal-mask, .z-mask, .z-toast-position-wrapper,
    .z-loadingbar-position, .z-drawer, .z-error { display: none; }

    .z-grid.z-sticky-header .z-grid-header,
    .z-listbox.z-sticky-header .z-listbox-header,
    .z-tree.z-sticky-header .z-tree-header { position: static; }

    .z-grid-body, .z-listbox-body, .z-tree-body {
        overflow: visible; height: auto; max-height: none;
    }

    [class*="z-elevation-"],
    .z-window, .z-panel, .z-paper, .z-groupbox {
        box-shadow: none;
        border: 1px solid var(--zk-color-outline-variant);
    }
}
```

---

## 3. 建置與載入

- `_print.css` 在原始檔內自宣告 `@layer zk-utilities { @media print { … } }`（build 不注入 layer）。
- 必須列於 `scripts/build-css.js` 的 `normFiles` 陣列（在 `zul/css/utility/*` 區塊、`_stack.css` 之後）—— 此陣列是**硬編碼**、非自動掃描，漏列會**靜默不打包**（無錯誤）。
- 最終 bundle 進 `norm.css.dsp`；`@media print` 可安全通過 CleanCSS level-1 minify。

---

## 4. 測試守則

`print` Playwright 專案（`print-utilities.spec.ts`）以 `page.emulateMedia({ media: 'print' })` 驗證：

- `.z-d-print-none` 在 print media 下 `display:none`、screen media 下顯示；
- `.z-d-none.z-d-print-block` 反向（screen 隱藏、print `block`）；
- `.z-elevation-2` 表面在 print 下 `box-shadow:none`（screen 下有陰影）。

```bash
npx playwright test -c zkthemelab/src/test/playwright/playwright.config.ts --project=print
```

---

## 5. 注意事項 / 限制

- **凍結欄（`<frozen>`）以 JavaScript 定位**（`translateX` 隨 `scrollLeft`），非 CSS `position:sticky`，故無法單靠列印樣式完整展開 —— 橫向卷動的 grid 在紙上可能裁掉右側凍結區。寬報表建議用分頁 / 伺服器端匯出。
- **保留背景色**：本 reset 不做 ink-saving（不強制黑白 / 移除品牌底色）。若需省墨版本，屬另一個獨立決策，未實作。
- `@page` 邊界、列印頁首 / 頁尾未納入本次範圍。
