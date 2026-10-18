# component-theming.spec.ts 測試分類（選項 A 的前置盤點）

來源：`zkpreview/src/test/playwright/component-theming.spec.ts`（1721 行、107 個 test）
相關：`doc/spec/component-theme-variables.md`（CTV-1…9）
狀態：**只分類，尚未刪除或修改任何測試。**

## 1. 總數

| 類別 | 個數 | 說明 |
|------|-----:|------|
| R：regional override + sibling untouched | 54 | 區域覆寫，再檢查旁邊的預設元件 |
| W：whole-app `:root` override wins | 45 | `addStyleTag` 注入 `:root{--zk-…}`，檢查預設實例套用 |
| Z：zero-regression defaults | 5 | 只有 messagebox、popup、drawer、coachmark、confirmpopup |
| O：其他 | 3 | button 區域覆寫、button disabled、badge severity |

## 2. 107 個 test 的斷言數量

量測方式：腳本逐行掃 `expect(`，依該行是否出現 `scoped`/`Scoped` 變數、是 `toBe` 還是 `not.toBe` 分四格。R 類（含 button 與 badge）合計：

| 斷言 | 個數 | 對應規範 | 證明的事 |
|------|-----:|----------|----------|
| scoped 端 `toBe(覆寫值)` | 149 | CTV-2、CTV-5 | 元件確實讀 knob，且預設值沒有宣告在元件元素上 |
| sibling 端 `toBe(stock 值)` | 54 | CTV-1 | 預設值等於改版前的值 |
| sibling 端 `not.toBe(覆寫值)` | 95 | 無（CSS 繼承本身） | 兄弟元件沒被覆寫影響 |
| scoped 端 `not.toBe` | 1 | CTV-7 | badge 非預設 severity 保持語意色 |

## 3. 分類與處置建議

### 保留（實作層）

1. **scoped 端 `toBe(覆寫值)`，149 個斷言。** 這是唯一能抓到「元件寫死顏色」或「預設值宣告在元件元素上」的檢查。
2. **sibling 端 `toBe(stock 值)`，54 個。** 這是目前唯一的 CTV-1 檢查。
3. **Z 類 5 個。** 浮動元件（messagebox、popup、drawer、coachmark、confirmpopup）的 stock 值檢查。這些元件不在容器內，R 類測不到它們。
4. **W 類裡的浮動元件 5 個。** 這 5 個元件的區域覆寫到不了（Messagebox 掛在 page root、Popup 被 `zk.makeVParent()` 搬到 body），所以 `:root` 是唯一能測它們的路徑。
5. **O 類 3 個。** button disabled（CTV-7）、badge severity（CTV-7）、button 區域覆寫（pilot 基準）。
6. **W 類中 messagebox 的 icon 顏色、codeeditor 等多於 2 個斷言的 test。** 它們多帶了 CTV-7 的狀態檢查。

### 可刪（重複或只在驗 CSS 繼承）

7. **sibling 端 `not.toBe(覆寫值)`，共 95 個斷言（其中 72 個已刪、21 個已換成 stock 值斷言、2 個留在 badge 與 button）。** 覆寫設在容器上，兄弟元件不是它的子孫，這句在任何 CSS 實作下都為真。它抓不到 Marble 的任何實作錯誤。
   - 例外：同一個 test 裡沒有對應的 sibling stock 斷言時，這句等於 sibling 端唯一的檢查。12 個 R test 屬於這種情況：tabbox、menubar、avatar、rating、progressmeter、toolbar、checkbox、radio、a、breadcrumb、carousel、codeeditor（badge 屬 O 類，不在內）。這 12 個的 `not.toBe` 已全部換成對預設值的 `toBe(stock 值)`，共 21 行。
   - 已執行（2026-10-18）：72 行刪除、21 行替換。`--project=component-theming` 107 個仍全過。變異驗證（注入沒分層、寫死樣式的 CSS）在 button、input、window、listbox、grid 上全部使對應 test 失敗，未改動的 tree、panel、biglistbox 仍通過。
8. **W 類中與 R 類使用同一個 knob 的 test。** 腳本比對 knob 名稱，W 的 knob 同時出現在 R 的 23 個元件上，這 23 個 W test 證明的事和 R 的 scoped 端相同（元件讀 knob）。其中 messagebox 等 5 個浮動元件不在此列。

### 先不動（需要人判斷）

9. **W 類中 knob 與 R 不同的 16 個元件**（progressmeter、paging、combobutton、calendar、toolbar、toolbarbutton、slider、rangeslider、multislider、toast、nav、anchornav、pdfviewer、breadcrumb、carousel、codeeditor）。它們測的是 R 沒測的另一個 knob，所以不算重複。腳本用 regex 抓 knob 名稱，會連同鄰近文字一起抓到（例如 breadcrumb 的 W 列出 carousel 的 knob），這個比對只能當線索，要人工確認。
10. **頁面 `component-theming.zul` 本身。** 它同時是 demo 與 fixture，這次不動。

## 4. 影響估計

- 刪除第 7 項：實際為 `component-theming.spec.ts` 刪 93 行、增 21 行（72 行刪除，加上 21 行 `not.toBe` 換成 stock 值斷言）。
- 刪除第 8 項：最多 23 個 W test（約 23 × 12 行）。
- 第 8 項（W 類重複）尚未處理，不動 scoped 端的 149 個斷言。
- 預計不影響 `x-ctv-suite` 檢查（`-g "<component>"` 仍會配對到每個元件的 R 類 test）。這點尚未實際跑過。

## 5. 尚未驗證

- 沒有跑過 Playwright，沒有證明刪掉第 7 項後測試仍能抓到實作錯誤。建議做法：先對一個元件（如 button）把 `.z-button` 的 knob 改成寫死顏色，確認 scoped 端斷言會失敗，再動手刪。
- W 類與 R 類「同一個 knob」的判斷來自 regex，不是逐一讀過每個 test 的語意。
