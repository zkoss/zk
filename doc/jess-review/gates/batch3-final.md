# Gate：第三批最終判定，grid（#34–#39）

- **判定：FAIL（#37 一項保護項）**，其餘 5 個 issue PASS（2026-10-07，post-fix）。
- **Verifier：** Fable，全新 context。沒有看修正的 diff，沒有讀 `gates/batch3-gen.md`、`gates/batch3.md`；只讀了驗證計畫第三批各節、`batch3-red.md`、`batch3-red-i35-rerun.md`。
- **方法：** 照 jess-review-verification-plan.md（過程資料已清除）「第三批」及其後的 D32、D33、D34、D35 裁示。RED 腳本原樣複製到 [batch3-final/](batch3-final/) 重跑（共用 `lib.js`），另外新寫 `i37-layouts.js`（D32 的額外版面）、`long-fc.js`（D34 記錄、forced-colors 分界線）、`classify.js`（baseline 差異分類）。
- **環境：** 8085（沒有重啟，也沒有開其他 port），Playwright Chromium，viewport 1280×900，注入 no-animation，全部用真的 click / hover。
- **確認服務的是新 CSS：** `grid-grouping.zul` 的 `.z-group` 與其 `td` 的 `cursor` 都是 `auto`（RED 時是 `pointer`）。
- **沒有修改** 任何 CSS／TS／ZUL；`zkpreview/doc/screenshots` 的 `git status` 跑前跑後都是空的。暫時頁 `zkpreview/src/main/webapp/web/tmp-b3final-frozen.zul` 量完已刪除（gretty 同步到 `build/inplaceWebapp/web/` 的那一份也刪了，URL 回 404）；頁面內容留存在 `batch3-final/tmp-b3final-frozen.zul.copy`。

## 總表

| Issue | 判定檢查 | 也必須成立／保護項 | 結果 |
|---|---|---|---|
| #34 | 表頭、body 都是 0 | 其他 grid 0；CSS diff 那一項無法照字面判（見下） | **PASS** |
| #35 | desc 0.088／0.094、asc 0.211／0.216（< 0.5） | 全部成立 | **PASS** |
| #36 | 文字、右側空白都是 `auto` | 全部成立，鍵盤與 RED 相同 | **PASS** |
| #37 | 4/4 列，捲動後 4/4，`x0` 位移 0 | D32 版面全部通過；**巢狀在凍結 grid 裡、沒有 `<frozen>` 的 grid／listbox 出現分界線** | **FAIL** |
| #38 | 列間相差 0，Δ = −10 | 全部成立 | **PASS** |
| #39 | 所有欄 0，排序後也是 0；center 0 | 全部成立 | **PASS** |

## #34

| 檢查 | 修後 | RED（基準） |
|---|---|---|
| Row States：表格右緣 − body 內框右緣（表頭／body） | 0 / 0 | Planner 量到 8（`width="370px"` 時） |
| Basic、Auxhead above、Auxhead below、Empty、No-border | 全部 0 / 0 | 0 |
| Frozen（水平捲動，依 RED 修正不納入） | 786，`bodyHasHScroll: true` | 786 |

- Row States 的 grid 寬變成 362（使用者在 `grid.zul` 拿掉 `width`），欄寬仍是 180＋180，沒變。
- 「若修法是改 `grid.zul`，佈景 CSS 的 diff 必須是空的」：`grid.css`、`frozen.css` 在 working tree 有修改，但那是 #35–#39 的修正。在「不看 diff」的限制下，我**無法分辨**其中有沒有屬於 #34 的部分，只能確認 #34 的量測結果完全可以由 `grid.zul` 的修改解釋（欄寬不變、grid 寬 362）。這一項記為方法問題，不當成 FAIL。

## #35（D27-A）

量法照修正版：ink bounding box 補正方形、16×16、DPR 4、墨跡門檻 0.5（`i35-rerun.js` 原樣）。

| 檢查 | 修後 | RED | 門檻 |
|---|---|---|---|
| 判定 1 desc：IoU(排序, 欄選單) | **0.088** | 0.867 | < 0.5 |
| 判定 1 desc：IoU(排序, 群組圖示) | **0.094** | 1.000 | < 0.5 |
| 判定 2 asc：IoU(排序, 欄選單) | 0.211 | 0.135 | < 0.5 |
| 判定 2 asc：IoU(排序, 群組圖示) | 0.216 | 0.122 | < 0.5 |
| asc vs desc 排序圖示 IoU | 0.463 | 0.122 | < 0.9 |
| 未排序欄沒有墨跡 | `.z-column-sorticon` 本身 0×0，`::before` 為 `none`（Author／Title／Publisher／Pages） | 同（`i` 沒有 box） | 無墨跡 |
| 排序有作用（新頁面點一次） | `The Northern Clemency` → `Hurry Down Sunshine` | 同 | 變更 |
| 對比（靜止） | asc 5.74、desc 5.74 | 5.74 | ≥ 3 |
| forcedColors 下看得見 | 墨跡 462 px，對比 21；IoU 與一般模式幾乎相同（0.087／0.092／0.211／0.216／0.455） | 244 px、21 | 有墨跡 |
| `title` | `Ascending Order`／`Descending Order` | 同 | 存在 |

- **健全性（sanity）：** asc 與 desc 的圖示彼此不同（0.463，介於「相同 ≥ 0.867」和「不同 ≤ 0.135」之間，是上下箭頭共用豎線的典型值，跟 RED 校準的 arrow-up vs arrow-down 0.455 一致），兩者對欄選單 caret 都遠低於 0.5。
- class 名稱仍是 `z-icon-caret-up/down`，字形是 CSS 換掉的；方法本來就不讀 class，所以不影響判定。

## #36（D28-A）

| 檢查 | 修後 | RED |
|---|---|---|
| 判定 1：group 文字中心（`span.z-label`） | **`auto`** | `pointer` |
| 判定 2：group 列右側空白（`div.z-group-content`） | **`auto`** | `pointer` |
| 保護：圖示中心 | `pointer` | `pointer` |
| 保護：點圖示切換 | open true → false → true，可見列 5 → 2 → 5 | 同 |
| 保護：點右側空白／文字不切換 | 維持 open | 同 |
| 一般 `.z-row`／可排序欄頭／欄選單按鈕／欄邊緣 | `auto`／`pointer`／`pointer`／`col-resize`（th 帶 `z-column-sizing`） | 同 |
| 鍵盤（焦點在 `z-group-inner`） | Enter：true → false；Space：false → true | 同 |

## #37（D32-C、D33、D35）

### 判定（grid.zul 第 5 個 grid）

| 檢查 | 修後 | RED |
|---|---|---|
| 步驟 1：靜止時有分界線的列 | **4 / 4**（ΔE 10.82） | 0 / 4 |
| 步驟 2：`scrollLeft` = 200 後 | **4 / 4**，`x0` 位移 0 | 0 / 4 |
| listbox.zul、tree.zul 的 frozen 範例（靜止／捲動 200） | listbox 3/3、3/3；tree 4/4、4/4（只量前 4 列）；表頭分界線仍在 | body 0 |

### D32 額外版面（`i37-layouts.js`）

每個版面都量「規則開」與「規則關」（用 CSSOM 只刪掉那一條 `box-shadow: inset` 的 `:has()` 規則），比較：x0 的 6px 帶（方法本身的量法）、整列的垂直線、表頭／列／每個 cell 的位置與大小。狀態：靜止、捲到最右。

| 版面 | 靜止 | 捲到最右 | 規則加出的線 | 版面（開＝關） |
|---|---|---|---|---|
| 凍結 1 欄 | 4/4 | 4/4 | 只在 x0−1 | 相同 |
| 凍結 2 欄 | 4/4 | 4/4 | 只在 x0−1 | 相同 |
| 凍結 3 欄 | 4/4 | 4/4 | 只在 x0−1 | 相同 |
| 凍結 4 欄 | 4/4 | 4/4 | 只在 x0−1 | 相同 |
| group（2 個 group、groupfoot） | 資料列 4/4；group、groupfoot 沒有線 | 同 | 資料列只在 x0−1，group 列**沒有任何線** | 相同 |
| auxhead | 4/4 | 4/4 | 只在 x0−1 | 相同 |
| `start="1"` | 4/4 | 4/4 | 只在 x0−1 | 相同 |
| listbox + `checkmark` | 4/4 | 4/4 | 只在 x0−1 | 相同 |
| listbox | 4/4 | 4/4 | 只在 x0−1 | 相同 |
| tree（含子節點） | 5/5 | 5/5 | 只在 x0−1 | 相同 |
| 凍結 grid，凍結 cell 裡放巢狀 grid（外層） | 4/4 | 4/4 | 只在 x0−1 | 相同 |
| 沒有 `<frozen>` 的 grid／listbox／tree | 0 | — | 無 | 相同 |

限制，只記錄：
- **凍結 5 欄**（窄欄版本）：0/3，規則沒有加任何線，也沒有線畫錯欄，符合「N = 1..4」的已知限制。
- **第一欄 `colspan`（D33：不支援，不對線的位置做斷言）：** 靜止時，colspan=2 的列在第 3 欄右緣（x 732）多一條線，colspan=3 的列在第 4 欄右緣（x 1032）多一條線，x0 上都沒有線；捲到最右後這兩列都沒有線。欄寬、列高、cell 位置與規則關閉時**完全相同**（沒有版面位移）。截圖 `batch3-final/i37-colspan-rest.png`。

### 也必須成立

| 項目 | 修後 | 結果 |
|---|---|---|
| 沒有 `<frozen>` 的 grid（grid.zul Basic）| 表頭與 3 列 ΔE 0 | ✓ |
| 沒有 `<frozen>` 的 grid／listbox／tree（暫時頁） | 0 條線 | ✓ |
| **巢狀：凍結 grid 的 cell 裡、沒有 `<frozen>` 的 grid 與 listbox** | **內層 grid 在自己的第 2 欄右緣出現分界線（2/2 列，ΔE 10.82）；放在非凍結 cell 裡的 grid 和 listbox 也一樣（1/1）** | **✗** |
| 凍結欄 hover 不透明（D35，alpha ≥ 0.98） | grid 0.9948、listbox 0.9896、tree 0.9896 | ✓ |
| forced colors 下的 body 分界線 | 表頭 ΔE 100，body 4 列都是 0：沒有分界線 | 記錄（follow-up 12），不算失敗 |

**FAIL 的原因：** 規則用後代選擇器，外層 grid 符合「第 2 欄是最後一個凍結欄」之後，它裡面**任何**巢狀 grid、listbox、tree 的第 2 個 cell 也會被畫線，即使內層沒有 `<frozen>`。截圖 `batch3-final/i37-nest2-rest.png` 可以直接看到內層 grid 和 listbox 的 b | c 之間多一條線。這違反 D32 第 4 項保護項的字面（「沒有 `<frozen>` 的 grid、listbox、tree 不出現分界線」）。
這個版面不在 D32 第 2 項列舉的版面裡，是本次 brief 要求加測的。若使用者裁示「巢狀元件」和 colspan 一樣屬於不支援，#37 就是 PASS；否則要修規則（例如把 body 那一段限定在外層自己的 body 之下）。

## #38（D30-A）

| 檢查 | 修後 | RED |
|---|---|---|
| 判定 1：Row States 列間 Δ 相差 | **0**（4 列都是 −10） | 8（−10／−18） |
| 判定 1：Basic 列間 Δ 相差 | **0**（3 列都是 −10） | 8 |
| 判定 2：Δ 不是零 | −10 | 通過 |
| Δ 等於 on-surface 4%（±1） | 所有列 −10 = 預期 −10 | odd 列 −18 |
| 凍結 grid hover 時 4 個 cell 一樣 | 都是 (246,246,246) | 都是 (237,237,237) |
| knob `--zk-grid-row-hover-bg: rgb(255,0,0)` | 4 列（含 odd）都變 (255,0,0) | odd 列不變 |
| forced colors 下 hover | Δ 0 | Δ 0（相同） |
| `.z-group` hover | (240,244,250)，Δ 0 | 相同 |

## #39（D31-A）

| 檢查 | 修後 | RED |
|---|---|---|
| 判定 1：Row States Name／Status | 0 / 0 | 4 / 4 |
| 判定 1：Basic Author／Title／Publisher／Pages | 0 / 0 / 0 / 0 | 4 |
| 判定 2：排序 Author 升冪、降冪 | 每欄都是 0 | Author 22，其他 4 |
| `align="center"`（判定） | 0（Auxhead above／below） | 2.0 |
| `align="right"`（保護） | 0 | 0 |
| 排序圖示看得見、不蓋到文字（短標籤） | 墨跡 456 px，`iconOverlapsText: false` | 同 |
| 欄選單按鈕 hover 在右半、不擠壓文字 | x 308.5、文字左緣 49 不變、不重疊 | 同 |
| 欄寬、列高（所有 grid、listbox、tree） | 與 RED 的 `i39.json` 完全相同 | — |
| listbox、tree 的表頭對齊 | 與 RED 完全相同（非首欄仍是 4px，記看板，不在本批） | — |

## D34：長標籤和排序圖示、欄選單按鈕重疊（只記錄，follow-up 11）

170px 寬、`sort="auto"`、`menupopup="auto"` 的三欄，排序後：

| 欄 | 圖示蓋到文字 | hover 時按鈕蓋到文字 | 按鈕蓋到圖示 |
|---|---|---|---|
| 長標籤 `Publication title of the book` | 是（圖示 157–171，文字被裁到 187） | 是 | 否 |
| 短標籤 `Short` | 否 | 否 | 否 |
| 極長標籤 | 是 | 是 | 否 |

表頭內容是 `overflow: visible`、`white-space: nowrap`、`text-overflow: clip`，圖示疊在文字上（`batch3-final/long-col0-hover.png`）。

## 回歸

`PREVIEW_URL=http://127.0.0.1:8085`，從 `zkpreview/` 執行，失敗的輸出寫到 scratchpad，沒有寫進 repo。

| Project | 結果 |
|---|---|
| `component-theming` | 107/107 通過 |
| `forced-colors` | 17/17 通過 |
| `focus-scan` | 57 通過、47 skipped，無失敗 |
| `hit-target`（#36 的回歸範圍） | 3/3 通過 |
| `chromium` | 127 通過，3 失敗：`listbox › gallery`、`grid › gallery`、`tree › gallery` |
| `tablet` | 53 通過，2 失敗：`tablet-grid › gallery`、`tablet-paging › gallery` |

差異逐區分群（色差 > 8 的像素，8px 格子連通），每一區都切出「baseline | 實際」對照圖看過（`batch3-final/diff-*.png`）：

| 截圖 | 差異像素 | 區域 | 分類 |
|---|---|---|---|
| `grid-gallery.png` | 6735 | 21 區：18 區是表頭文字左移 4px（Row States、Basic、Auxhead、Frozen、No-border 的每個欄頭）；1 區是 Row States 右緣內縮 8px；1 區是它上方置中的「ROW STATES」標題跟著移動；1 區是 Frozen grid 的 body 分界線（x 432） | 全部 E |
| `listbox-gallery.png` | 156 | 1 區：frozen listbox 的 body 分界線（x 432） | E |
| `tree-gallery.png` | 264 | 1 區：frozen tree 的 body 分界線（x 728） | E |
| `grid-tablet.png` | 6263 | 19 區，內容與 grid-gallery 相同（表頭文字、Row States 寬度與標題、frozen 分界線） | 全部 E |
| `paging-tablet.png` | 1617 | 4 區：`paging.zul` 裡那個 grid 的表頭文字（#、First Name、Last Name、Username）左移 4px | E（grid 表頭文字位置） |

沒有 D、沒有 U。`paging-tablet` 雖然不在 #39 列出的回歸範圍裡，但差異只有 grid 表頭文字位置，屬於 E 的定義。

## 方法上的備註

1. **#37 保護項沒有考慮巢狀元件。** D32 列舉的版面裡沒有「凍結 grid 裡的巢狀 grid」，而第 4 項保護項的字面又涵蓋它，所以本次判 FAIL。需要使用者裁示：比照 colspan 列為不支援，還是修規則。
2. **#34「佈景 CSS 的 diff 必須是空的」在這一批量不了。** 同一批的其他 issue 也改了 `grid.css`，而最終 Verifier 不能看 diff，這一項只能靠 Generator 的報告或 Planner 自己確認。
3. **#35「未排序欄沒有墨跡」**：RED 時是「沒有 box 所以空洞成立」，這次另外確認 `.z-column-sorticon` 本身也是 0×0、`::before` 為 `none`，所以不是空洞成立。
4. **#37 的 N > 4 限制，用原本的欄寬量不到**（第 5 欄右緣在 1333px，超出 1280 的 viewport），改用 150px 的窄欄另測。
5. **「規則關閉」的基準**：我第一次用「選擇器含 `:has(` 和 `frozen`」來刪規則，結果連表頭分界線、hover 底色等 8 條規則一起刪了。最後一次只刪 `box-shadow` 為 `inset` 的那一條；兩次的版面比較結果都是「相同」。
6. `paging-tablet` 的 grid 不在各 issue 列出的回歸範圍裡；以後列「grid 表頭」的回歸範圍時，應該把所有含 grid 的預覽頁都算進去。

取證放在 [batch3-final/](batch3-final/)。

GATE3-FINAL: FAIL (issues #37)
