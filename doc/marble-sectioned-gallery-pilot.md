# gallery 分區截圖（tree、tabbox pilot 與全面展開）

日期：2026-10-17 至 2026-10-18。接續 `doc/marble-sectioned-gallery-brief.md`（交接單，D7）。

## 作法

- helper：`zkpreview/src/test/playwright/section-shots.ts`（`expectSectionShots`），由 `gallery-scan.spec.ts`（`gallery` project）與 `screenshot.spec.ts` 的 16 個 gallery（`chromium` project）使用。
- 分區 = `.z-p-8` 底下每個直接子元素。第一個無標題的子元素是頁面標題（`-00-title`），其餘每個必須恰好有一個 `.z-text-uppercase.z-border-bottom` 標題列。沒有任何標題的頁面（`label`）維持整頁一張。
- 防漏（失敗條件）：直接子元素無標題（第一個除外）、有兩個以上標題、帶標題但不佔空間、相鄰區間距小於截圖邊距（16px），或拍完後各區高度與拍之前不同。不佔空間且無標題的子元素（隱藏的 `<popup>`、`<confirmpopup>`）忽略。
- 截圖：每區以頁面座標 clip，四邊各多 16px（elevation-3 陰影最遠 16px），`fullPage`。
- 檔名：`<page>-<序號>-<標題 slug>.png`。序號是區在頁面中的位置，略過的區也計入，所以略過不會改後面的檔名。component-theming 有 112 張，序號 100 以上是三位數。
- 預算：每區 `maxDiffPixels: 20`（`SECTION_MAX_DIFF_PIXELS`，兩個 spec 各一個常數）。
- 範圍：gallery-scan 全部非 SKIP 頁面、`screenshot.spec.ts` 的 16 個 COVERED_ELSEWHERE gallery。tablet 不在範圍。

## 為什麼要把視窗撐高

Playwright 對比視窗高的頁面截圖，會用 captureBeyondViewport，每截一張就 resize 一次頁面。ZK 每次 resize 都重算 widget 尺寸，有些 demo 每次 resize 結果都不同：

- tabbox 第二個 tab strip：第一次 resize 前寬 103px，之後 87px。
- anchorlayout 第一區：每次 resize 長高 111px（492 → 603 → 714 → 825 → 936）。

所以一個區段拍起來的樣子取決於前面拍了幾張；寫基準時每張圖要連續兩次相同才算穩定，比對時只拍一次，兩者張數不同，基準與比對就對不上。helper 先把視窗高度設成文件高度加 1000px（resize 只發生一次），之後每張截圖都放得進視窗，不再 resize。

文件高度仍可能比視窗高：component-theming 與 pdfviewer 有元素貼著視窗底部定位（不屬於 `.z-p-8`），文件高度永遠是視窗高度加 20px，而 `.z-p-8` 本身不變（19,646px）。所以 helper 不斷言「放得進視窗」，改為拍完後比對各區高度。

## 量測值

雜訊（預算 0，`SECTION_NOISE=1`，同一版本跨瀏覽器 process 各跑 2 次，8105，Chromium）：

| 範圍 | 區數 | 差異不為 0 的區 |
|---|---|---|
| gallery-scan | 437 | `toolbar-05-panel-mold` 1 px、1 px；`toolbar-06-vertical` 2 px、0 px |
| screenshot.spec（16 頁，含 tree、tabbox） | 83 | 無 |
| tree、tabbox（pilot） | 21 | 無 |

最大雜訊 2 px，預算 20 px 為其 10 倍。樣本只有 2 次，且在同一台機器。

與舊整頁基準逐像素比對（另一個 Opus agent，95 頁）：59 頁 311 張完全相同；其餘 36 頁的差異都能解釋：標題改為標準樣式（avatar、badge、chip、component-theming）、舊基準過期（13 頁，字級改名等）、token 改動（toast、notification、progressmeter）、舊基準本來就拍到截斷或錯位的畫面（biglistbox、codeeditor、listbox-header 等）。

## 突變測試

在單一元件注入 1px 邊框或外框：

| 頁面 | 注入 | 整頁版（容忍 0.2% 面積） | 分區版（預算 20） |
|---|---|---|---|
| tree | 第一個 `.z-tree` 的 1px 紅邊 | 通過 | 失敗，896 px |
| tabbox | 第一個 `.z-tabpanel` 的 1px 紅邊 | 通過 | 失敗，3,092 px |
| component-theming | 第一個 `.z-button` 的 1px 紅色 outline | 容忍 47,121 px，通過 | 失敗，132 與 148 px（兩次） |
| badge | 第一個 `.z-badge` 的 1px 紅色 outline | — | 失敗 |

## 頁面與測試的改動

- avatar、badge、chip：區段標題改為標準樣式 `z-text-uppercase z-border-bottom z-pb-2 z-mb-3`。
- component-theming：標題改標準樣式，且 111 個平鋪的區段各包一個 `<div>`，頁面標題與說明包成一個 `<div>`；其餘內容與縮排以外沒有改動（CDATA 內文未動）。`component-theming` project 107 項測試改動前後都通過。
- responsive-grid：頁面標題與說明包成一個 `<div>`。
- window：viewport 相對定位的 overlapped 視窗在視窗撐高後落在所有區段之外，改為另外對每個 `.z-window-overlapped` 單獨截圖（`window-overlapped-00.png`、`-01.png`）。
- `doc/font-size-baseline.json`：只更新 component-theming.zul 與 responsive-grid.zul 兩條。值為 e3 伺服器的直方圖扣掉跨伺服器共同偏差 `{"13px": 3}`（130 頁中 104 頁與基準恰好差這個）。實測 HEAD 版（沒有本次改動）在 e3 上：responsive-grid 13px 329（基準 326，只差偏差 3），加上本次改動 330（多 1 個包標題的 div）；component-theming 13px 2319、12px 68、11px 149、14px 217，加上本次改動後 13px 變 2431，恰好多 112（112 個 div）。component-theming 在本次改動之前就比基準多出 13px +2、12px +16、11px +6、14px −20（淨 +4 個元素，且有 14px 變成 12/11px 的字級位移），來源是基準日（2026-10-07）之後的其他 commit，未逐一確認；不是 zkcml 10-15 的 slider commit（那兩個只改 padding、margin）。其餘有同類殘差的頁面（130 頁中約 26 頁與基準的差不只是偏差 3）本次沒有動，基準仍是舊的。

## 略過的區段（記錄）

- carousel「Autoplay (manual/visual confirmation)」：每 2 秒換一張，頁面自己標明不屬於任何靜態畫面斷言。
- grid-header、listbox-header、tree-header 的 sizedByContent 區：初次載入就分成兩種結果，之後撐高視窗或等待都不會改變。listbox-header 實測 12 次載入中 3 次（`T225`）把 "Super Long Product Name Here" 截成省略號、9 次（`F228`）完整顯示，兩種狀態的差是 1,435 px；grid-header 單跑 10 次失敗 1 次。這是 ZK sizedByContent 的初次渲染競態，不是截圖方法造成的，舊整頁基準同樣受影響。是否開 Jira 待決定（核心 widget 問題由核心團隊處理）。

## 已知事項

- ZK 的 tabbox：第一次 resize 前 tab strip 比 resize 後寬 16px，會伸到 toolbar 下方。推測是 ZK 在 `volumn.gif`（16×16）載入前量了 toolbar 寬度，載入後沒有重量；這是 ZK widget 時序，非 Marble CSS，推論未在執行期驗證。
- 舊基準就有的現象（非本次改動）：tabbox-02 的 tab 分隔線只畫在 strip 範圍、垂直 tabbox 的 panel 沒有頂邊框。是否處理屬 Marble 外觀問題。
- PAD 16px 涵蓋 elevation-3；若頁面有更大的陰影或 focus ring，需要調大。
- `forced-colors-gallery` 下次執行時，avatar、badge、chip、component-theming、responsive-grid 的 `-forced-colors.png` 會因標題底線而改變，那是審查用截圖，需審查後再提交。
- 移除 `<page>-gallery.png` 之後，`doc/migration/tools/` 下幾支遷移用腳本（`verify-3.18b.sh`、`verify-p3-gate.sh`、`zero-tolerance/`）仍引用 `*-gallery.png`，它們找不到這些舊檔；那些腳本不是這次工作的測試。
