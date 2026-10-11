# Batch 3 baseline 分類：grid #34–#39 留下的截圖失敗（Verifier）

- 日期：2026-10-07
- 角色：Verifier（唯讀量測）。未修改任何 CSS、TS、ZUL、spec、config，未修改 `zkpreview/doc/screenshots` 下任何 PNG，未執行 `--update-snapshots`。
- 格式參考：[baseline-classify.md](baseline-classify.md)（E/D/U 定義與 `measure.js` 方法）。
- 取證目錄：batch3-baseline/（過程資料已清除）

## 結論

5 個失敗 PNG **全部是 E**。共 46 個差異區域，每一個都對到 batch 3 的某一項；**沒有 D，沒有 U**。

最強的證據是「還原比對」：在服務中的頁面上**只還原 batch 3 的三個效果**後重拍，5 張圖與 expected PNG **逐像素完全相同**（pixelmatch threshold 0，差異 0）。這三個效果是：

1. sort icon 放回 text flow，`margin-left: 4px`。
2. 拿掉 body cell 的分界線 box-shadow。
3. Row States grid 設回 `width: 370px`。

另外，未還原時重拍的結果與本輪 actual PNG 也是 0 差異，證明 live 頁面就是 actual。

## 1. 執行與失敗集合

```bash
cd zkpreview && PREVIEW_URL=http://127.0.0.1:8085 npx playwright test \
  --config src/test/playwright/playwright.config.ts --project=chromium --project=tablet \
  --output <repo>/doc/jess-review/gates/batch3-baseline/pw --reporter=list
```

- `zkpreview/.gitignore` 只忽略 `test-results/`，所以 `--output` 指向 `gates/batch3-baseline/pw`（untracked），不寫入預設目錄。
- 結果：**180 passed、5 failed**，沒有 flaky，也沒有 skipped。完整 log 在 `batch3-baseline/run.log`。

| Project | 測試 | baseline PNG | Playwright 差異像素 | threshold |
|---|---|---|---|---|
| chromium | `grid › gallery` | `grid-gallery.png` | 4134 | 0.05 |
| chromium | `listbox › gallery` | `listbox-gallery.png` | 156 | 0.05 |
| chromium | `tree › gallery` | `tree-gallery.png` | 264 | 0.05 |
| tablet | `tablet-grid › gallery` | `grid-tablet.png` | 2236 | 0.2 |
| tablet | `tablet-paging › gallery` | `paging-tablet.png` | 664 | 0.2 |

失敗集合與先前 gate 回報的一致。其他測試沒有失敗。

## 2. 方法

1. **`measure.js`**：複製自 `baseline-classify/measure.js`，唯一修改是目錄過濾多收 `tablet-*`。
   - 以 `--yiq 0.05` 執行，用 4px 容差做 connected component 分群，並對每個 cluster 找 best shift。
   - 輸出：`measure.json`、`measure.out`。
   - 尺寸：5 張的 expected 與 actual 都相同。
   - 色彩：chromatic 像素 0，沒有任何色相改變。
2. **`live.js`**：對 `http://127.0.0.1:8085` 用與 spec 相同的 viewport、UA 和 `.z-p-8` locator 處理每個 case。
   - (a) 重拍後與 actual 比對：5/5 為 0。
   - (b) 量測 header label x、sort icon box、最後一個 frozen cell、Row States grid 與 caption。
   - (c) 還原上述三個效果後重拍，與 expected 比對：5/5 在 t=0 與該 project 的 t 下都是 **0**。
   - 輸出：`live.json`、`reverted-*.png`。
3. **`compose.js`**：
   - 把每個 cluster 依 live 幾何自動歸屬到 E1、E2、E3 或 U，輸出 `attribution.json`。
   - 每張失敗 PNG 產生一張並排圖 `sbs-<base>.png`。上半部是 expected | actual | diff 全圖縮放，所有區域以紅框編號；下半部每個區域一列 3× 放大，同樣是 expected | actual | diff。

## 3. 每張 PNG 的區域歸屬

E 項目定義：

- **E1**：#39，header 文字左移，以及 sort icon 位置。
- **E2**：#37，frozen body 分界線。
- **E3**：#34，Row States grid 變窄，caption 重新置中。

bbox 為圖片座標 `[x,y w×h]`。

### `grid-gallery.png`：E（21 區），並排圖 `sbs-grid-gallery.png`

**E3（2 區）**

| # | bbox | 說明 |
|---|---|---|
| 1 | [178,164 74×8] | 「ROW STATES」caption x 181.5 → 177.5，左移 4px，是 grid 寬度變化的一半 |
| 2 | [390,186 12×266] | Row States grid 寬 370 → 362（欄寬 180+180，加 2px 邊框），右邊框 x 401 → 393。wide shift (-8,0)，殘差 28 |

**E1（18 區）**：左對齊 header label 左移 4px（例如 x 53 → 49）。

| # | bbox | header |
|---|---|---|
| 3 | [50,207] | Name |
| 4 | [229,207] | Status |
| 5 | [49,579] | Author |
| 6 | [353,579] | Title |
| 7 | [657,579] | Publisher |
| 8 | [960,579] | Pages |
| 9 | [376,915] | Left（auxhead below） |
| 11 | [50,952] | Left（auxhead above） |
| 13 | [49,1263] | Col A |
| 14 | [249,1263] | Col B |
| 15 | [449,1263] | Col C |
| 16 | [849,1263] | Col D |
| 18–21 | y 1639、1963 | Name、Value 兩組 |

- #10 [487,915] 與 #12 [161,952]：`align="center"` 的「Center」只左移 **2px**，也就是一半。
- `align="right"` 的「Right」位移 0，所以沒有差異區域。

成因已在 DOM 確認：`.z-column-sorticon` 是 `.z-column-content` 的**第一個子元素**。舊規則讓它保持 `static`、寬 0、`margin-left: 4px`，所以 label 被推右 4px；新規則改為 `position: absolute`。新圖中 header label x 與 body 文字 x 一致（例如兩者都是 49）。

**E2（1 區）**

| # | bbox | 說明 |
|---|---|---|
| 17 | [432,1296 1×211] | inset −1px 分界線，在最後一個 frozen cell 的右緣 x = 233 + 200 − 1，高度等於 body 高度 211 |

### `listbox-gallery.png`：E（1 區），`sbs-listbox-gallery.png`

| # | bbox | 類別 | 說明 |
|---|---|---|---|
| 1 | [432,1708 1×158] | E2 | frozen listbox 分界線，x = 233 + 200 − 1，高度等於 body 高度 158 |

- listbox header 文字沒有變化：`.z-listheader-sorticon` 不受 `grid.css` 影響。

### `tree-gallery.png`：E（1 區），`sbs-tree-gallery.png`

| # | bbox | 類別 | 說明 |
|---|---|---|---|
| 1 | [732,5073 1×268] | E2 | frozen tree 分界線，x = 333 + 400 − 1，高度等於 body 高度 268 |

### `grid-tablet.png`：E（19 區），`sbs-grid-tablet.png`

與 grid-gallery 同構：

| # | 類別 | 說明 |
|---|---|---|
| 1、2 | E3 | caption 左移 −4；右邊框 [390,186 12×306] 401 → 393 |
| 3–9、11、13、14、16–19 | E1 | header label 左移 −4，x 57 → 53 |
| 10、12 | E1 | Center 左移 −2 |
| 15 | E2 | [432,1408 1×243] |

- frozen 區只露出 Col A 與 Col B/C 兩個 header 文字區，原因見 §5。

### `paging-tablet.png`：E（4 區），`sbs-paging-tablet.png`

| # | bbox | 類別 | header |
|---|---|---|---|
| 1 | [53,775] | E1 | 「#」 |
| 2 | [246,775] | E1 | First Name |
| 3 | [438,775] | E1 | Last Name |
| 4 | [630,775] | E1 | Username |

4 個 header 都左移 −4，x 57 → 53 等。

### 關於「sort icon 位置」

5 張 gallery 在靜止狀態下，sort icon 的 `<i>` 寬高都是 0（live 量測 `visibleIcon` 為 0/85），所以這幾張圖**沒有** sort icon 位移或箭頭 glyph 的差異區域。也就是說，#39 的 icon 本身沒有被這些 baseline 覆蓋。

## 4. 新圖是否帶有可見回歸

我逐張看了 actual 的關鍵區域：Row States、各 header 列、frozen 區。

- **5 張的 batch 3 改動都沒有造成可見回歸**：沒有被截斷的文字，icon 沒有重疊，邊框也沒有斷裂。
  - header label 與 body 文字左緣對齊。
  - Row States grid 剛好包住兩欄，沒有右側空白。
  - 分界線與 frozen header 的分界線連續，listbox、tree、桌面 grid 都是如此。

## 5. 附帶觀察（不影響分類，不是 batch 3 引入）

1. **`grid-tablet.png` 的 frozen header 本來就是壞的**：
   - 現象：「Col B」與「Col C」的 header cell 疊在同一格（live 量測兩者都在 x 433），所以字看起來偏黑。Col A–B 之間的 header 底線斷開，header 的 frozen 陰影在 x 633，而 body 分界在 x 432。
   - 這個狀態在 expected（`5c4888e54a` 產生）裡已經存在，還原比對也證明它與 batch 3 無關。
   - 影響：regenerate 這張圖會把這個既有缺陷**再存一次**。header 邊界在 633、body 邊界在 432 的錯位也會一起存進去。
   - 建議另開 follow-up 處理 tablet smooth-frozen header。
2. **listbox header label 仍比 body 文字右移 4px**（53 vs 49）：`.z-listheader-sorticon` 仍有 `margin-left: 4px` 的空 placeholder，也就是 #39 修掉的同一種問題。這是既有狀態，不在 batch 3 範圍內。
3. 上一輪 [batch3-final-r2.md](batch3-final-r2.md) 的差異像素數，例如 grid 6735，是以「channel 差 > 8」計算。本報告的數字是 Playwright pixelmatch 計數。兩者的**區域數相同**（21/1/1/19/4）。

## 6. `--update-snapshots` 計畫（未執行）

只更新這 5 個失敗的 baseline。以下 grep 已用 `--list` 確認：chromium 剛好選中 3 個，tablet 剛好選中 2 個。

```bash
cd /Users/hawk/Documents/workspace/ZK10/zk/zkpreview
PREVIEW_URL=http://127.0.0.1:8085 npx playwright test --config src/test/playwright/playwright.config.ts \
  --project=chromium --update-snapshots=changed -g "(^| )(grid|listbox|tree) gallery$"
PREVIEW_URL=http://127.0.0.1:8085 npx playwright test --config src/test/playwright/playwright.config.ts \
  --project=tablet --update-snapshots=changed -g "tablet-(grid|paging) gallery$"
```

- 預期改寫的檔案：`zkpreview/doc/screenshots/{grid-gallery,listbox-gallery,tree-gallery,grid-tablet,paging-tablet}.png`。
- 跑完後先用 `git status --porcelain zkpreview/doc/screenshots` 確認只有這 5 個檔案改變，再重跑兩個 project，應為全綠。
- 不要用 `=all`，也**絕不要**跑 `forced-colors-gallery`。
- `grid-tablet.png` 會連同 §5.1 的既有缺陷一起存入，是否接受由 Planner 決定。

## 7. 收尾

- 執行前後 `git status --porcelain zkpreview/doc/screenshots doc/screenshots` 都是空的，沒有變動（`batch3-baseline/git-status-before.txt`）。
- 工作樹只有原本就存在的 `grid.zul`、`grid.css`、`frozen.css` 修改。
- 新增的檔案只在 `doc/jess-review/gates/batch3-baseline/` 與本報告。
