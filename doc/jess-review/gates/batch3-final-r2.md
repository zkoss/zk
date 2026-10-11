# Gate：第三批最終判定 第 2 輪，#37（凍結欄 body 分界線）

- **判定：PASS**（2026-10-07）。上一輪（[batch3-final.md](batch3-final.md)）#34、#35、#36、#38、#39 已通過，只有 #37 因巢狀元件保護項 FAIL；本輪只重跑 #37 與 `frozen.css` 牽動的回歸。
- **Verifier：** Fable，全新 context。沒有看修正的 diff，沒有讀 `gates/batch3-gen.md`；只讀了驗證計畫「第三批」#37 與 D32、D33、D34、D35 裁示，以及上一輪的報告和腳本。沒有修改任何 CSS／TS／ZUL（暫時頁除外，已刪），沒有更新 baseline。
- **方法：** 上一輪腳本複製到 batch3-final-r2/（過程資料已清除） 重跑（`lib.js` 仍指向 RED 的共用 helper）。新增：`gen-zul.js` 加 3 個巢狀版面、`i37-layouts.js` 加 12 個量測目標、`extra-pages.js`（所有含 `<frozen>` 的預覽頁）、`eyeball.js`。
- **環境：** 8085，Playwright Chromium，viewport 1280×900，注入 no-animation。

## 先確認服務的是新 CSS

| 時點 | 服務中的 body 分界線規則 | 判斷 |
|---|---|---|
| 一開始 | 後代選擇器（`:has(:is(.z-column…).z-frozen-col:first-child) … :is(.z-row…) > :first-child`），沒有 `> table > tbody >` | **舊 CSS**：`zul/build/resources/main/…/frozen.css.dsp` 是 10:45 的版本，原始檔是 11:54 |
| 處理後 | `:is(.z-grid, .z-listbox, .z-tree):has(> :is(.z-grid-header…) > table > tbody > …) > :is(.z-grid-body…) > table > tbody > :is(.z-row…) > :nth-child(N)`，4 條（N = 1..4） | **新 CSS** |

處理方式：`./gradlew :zul:compileMarbleCss`，然後只停掉 8085 的 appRun（server pid 46556 及其 gradle 父行程），在同一個 port 重啟（`withjdk.sh 17 ./gradlew appRun -PhttpPort=8085`，以 `nohup` 脫離本 session，**目前仍在 8085 執行**）。沒有開其他 port。
附帶：`pkill -f "appRun -PhttpPort=8085"` 也結束了 2026-10-06 遺留、沒有在 listen 的一個 appRun wrapper（pid 45659）。

## #37 判定（`i37.js`）

| 檢查 | 結果 | 門檻 |
|---|---|---|
| grid.zul 第 5 個 grid：靜止 | **4 / 4**（ΔE 10.82），表頭線仍在 | 4 |
| `scrollLeft` = 200 後 | **4 / 4**，`x0` 位移 **0** | 4，≤ 1px |
| listbox.zul frozen 範例（靜止／捲動） | 3/3、3/3，`x0` 位移 0 | 全部 |
| tree.zul frozen 範例（靜止／捲動，量前 4 列） | 4/4、4/4，`x0` 位移 0 | 全部 |
| 凍結欄 hover alpha（D35） | grid 0.9948、listbox 0.9896、tree 0.9896 | ≥ 0.98 |
| 沒有 `<frozen>` 的 grid（grid.zul Basic） | 表頭與 3 列都沒有線 | 0 |

## D32 版面（`i37-layouts.js`，規則開 vs 只刪 inset 那條規則）

每個版面量「靜止」與「捲到最右」，比較 x0 的 6px 帶（記錄方法）、整列的垂直線、表頭／列／cell 的位置與大小。「加出的線」= 規則開時有、規則關時沒有的垂直線。

| 版面 | 靜止 | 捲到最右 | 規則加出的線 | 版面（開＝關） |
|---|---|---|---|---|
| 凍結 1／2／3／4 欄 | 4/4 | 4/4 | 只在 x0−1（232／432／732／1032） | 相同 |
| group（含 groupfoot） | 資料列 4/4；group、groupfoot 無線 | 同 | 只在資料列 x0−1 | 相同 |
| auxhead、`start="1"` | 4/4 | 4/4 | 只在 x0−1 | 相同 |
| listbox + `checkmark`、listbox | 4/4 | 4/4 | 只在 x0−1 | 相同 |
| tree（含子節點） | 5/5 | 5/5 | 只在 x0−1 | 相同 |
| 沒有 `<frozen>` 的 grid／listbox／tree | 0 | — | 無 | 相同 |
| **巢狀、沒有 `<frozen>`，放在凍結 cell：grid、listbox、tree（`R-inG/L/T`、上一輪的 `L-inner`）** | 內層 **0**，外層 4/4 | 外層 4/4 | 內層**無**；外層只在 x0−1 | 相同 |
| **巢狀、沒有 `<frozen>`，放在非凍結 cell：grid、listbox、tree（`R-nG/L/T`、`L-inner2`、`L-innerlb`）** | 內層 **0**，外層 4/4 | 外層 4/4 | 內層**無**；外層只在 x0−1 | 相同 |
| **巢狀、有 `<frozen columns="1">`：grid 在凍結 cell，listbox、tree 在非凍結 cell（`R-ffG/L/T`）** | 內層各 2/2（在自己的 x0−1：129／529／529），外層 4/4（432） | 內層 2/2、外層 4/4 | 內層只在自己的 x0−1；外層只在 432 | 相同 |
| 凍結 5 欄（原欄寬與 150px 窄欄，只記錄） | 0 | 0 | 無 | 相同 |
| grid.zul／listbox.zul／tree.zul 的 frozen 範例 | 4/4、3/3、5/5 | 同 | 只在 x0−1 | 相同 |

- **colspan 列（D33：不支援，只記錄）：** 靜止時 colspan=2 的列在 x 732、colspan=3 的列在 x 1032 多一條線，x0 上沒有；捲到最右後兩列都沒有線。與規則關閉時相比，欄寬、列高、cell 位置**完全相同**（無版面位移）。與上一輪相同。
- 截圖：`i37-nestF-rest.png`（凍結 cell 裡的三種巢狀，b|c 之間沒有線）、`i37-nestN-rest.png`（非凍結 cell）、`i37-nestFF-rest.png`（內層凍結元件各自只有自己的線）。tree 第一欄的 `a`、`d` 被縮排裁掉是 60px 欄寬的問題，規則開關都一樣，與 #37 無關。
- **forced colors（只記錄，follow-up 12）：** 表頭 ΔE 100，body 4 列都是 0，與上一輪相同。

## 回歸

`PREVIEW_URL=http://127.0.0.1:8085`，從 `zkpreview/` 執行，輸出放在 scratchpad，沒有寫進 repo。

| Project | 結果 |
|---|---|
| `component-theming` | 107/107 通過 |
| `forced-colors` | 17/17 通過 |
| `focus-scan` | 58 通過、47 skipped，無失敗 |
| `hit-target` | 3/3 通過 |
| `chromium` | 127 通過，3 失敗：`listbox › gallery`、`grid › gallery`、`tree › gallery` |
| `tablet` | 53 通過，2 失敗：`tablet-grid › gallery`、`tablet-paging › gallery` |

baseline 差異用上一輪的 `classify.js` 分群，再與上一輪的 `classify.json` 逐區比對：

| 截圖 | 差異像素（本輪／上一輪） | 區域（本輪／上一輪） | 新增或改變的區域 | 分類 |
|---|---|---|---|---|
| `grid-gallery.png` | 6735 / 6735 | 21 / 21 | 無 | 全部 E |
| `listbox-gallery.png` | 156 / 156 | 1 / 1（frozen body 分界線） | 無 | E |
| `tree-gallery.png` | 264 / 264 | 1 / 1（frozen body 分界線） | 無 | E |
| `grid-tablet.png` | 6263 / 6263 | 19 / 19 | 無 | 全部 E |
| `paging-tablet.png` | 1617 / 1617 | 4 / 4（grid 表頭文字左移 4px） | 無 | E |

沒有 D、沒有 U。新規則的差異與舊規則**逐像素相同**：在單層 frozen 範例上兩者結果一樣，只有巢狀情況不同。

### 其他含 `<frozen>` 的預覽頁（`extra-pages.js`、`eyeball.js`）

`grep -l "<frozen" zkpreview/src/main/webapp/web/*.zul`：`grid.zul`、`listbox.zul`、`tree.zul`、`grid-header.zul`、`listbox-header.zul`、`tree-header.zul`。對每一頁的**每一個** grid／listbox／tree（共 72 個）逐列掃垂直線，規則開 vs 關：

- 凍結元件 6 個（各頁 1 個）：資料列全部有線（4/4、3/3、5/5、4/4、4/4、5/5），加出的線只在 x0−1。
- 其餘 66 個沒有 `<frozen>` 的元件：加出的線 **0**。
- 目視（`eye-montage.png`）：`grid-header.zul`、`listbox-header.zul`、`tree-header.zul` 的凍結範例只有一條分界線，位置正確，沒有多餘的線。

## 清理

- 暫時頁 `zkpreview/src/main/webapp/web/tmp-b3final-r2-frozen.zul` 與 `zkpreview/build/inplaceWebapp/web/` 的那一份都已刪除，URL 回 404；內容留存在 `batch3-final-r2/tmp-b3final-r2-frozen.zul.copy`。
- `git status`：`zkpreview/doc/screenshots`、`doc/screenshots` 跑前跑後都沒有變動；`zkpreview/src` 只有原本就存在的 `grid.zul` 修改（Hawk 的 #34 修改）。

## 方法上的備註

1. 這次一開始服務的是舊 CSS，若沒有先確認就量，巢狀項會再 FAIL 一次。以後 `frozen.css` 這類改動的最終判定，第一步固定為「讀服務中的規則文字」，而不是只看某個 computed 值。
2. 「巢狀凍結元件」的判定方式：內層與外層各自當成量測目標，各自只允許在自己的 x0−1 出現新線；外層列的掃描不會把內層的短線算進去（垂直線要佔該列 90% 高度才算），所以兩者互不干擾。

取證放在 batch3-final-r2/（過程資料已清除）。

GATE3-FINAL-R2: PASS
