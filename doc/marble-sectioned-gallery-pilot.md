# gallery 分區截圖（tree、tabbox pilot 與全面展開）

日期：2026-10-17 至 2026-10-18。接續 `doc/marble-sectioned-gallery-brief.md`（交接單，D7）。

## 作法

- helper：`zkpreview/src/test/playwright/section-shots.ts`（`expectSectionShots`），由 `gallery-scan.spec.ts`（`gallery` project）與 `screenshot.spec.ts` 的 16 個 gallery（`chromium` project）使用。
- 分區 = `.z-p-8` 底下每個直接子元素。第一個無標題的子元素是頁面標題（`-00-title`），其餘每個必須恰好有一個 `.z-text-uppercase.z-border-bottom` 標題列。沒有任何標題的頁面（`label`）維持整頁一張。
- 防漏（失敗條件）：直接子元素無標題（第一個除外）、有兩個以上標題、帶標題但不佔空間、相鄰區間距小於截圖邊距（16px），或拍完後各區高度與拍之前不同。不佔空間且無標題的子元素（隱藏的 `<popup>`、`<confirmpopup>`）忽略。
- 截圖：每區以頁面座標 clip，四邊各多 16px（elevation-3 陰影最遠 16px），`fullPage`。
- 檔名：`<page>-<序號>-<標題 slug>.png`。序號是區在頁面中的位置，略過的區也計入，所以略過不會改後面的檔名。序號用兩位數（超過 99 區的頁面會是三位數；目前沒有）。
- 預算：每區 `maxDiffPixels: 20`（`SECTION_MAX_DIFF_PIXELS`，兩個 spec 各一個常數）。
- 範圍：gallery-scan 全部非 SKIP 頁面、`screenshot.spec.ts` 的 16 個 COVERED_ELSEWHERE gallery。tablet 不在範圍。

## 為什麼要把視窗撐高

Playwright 對比視窗高的頁面截圖，會用 captureBeyondViewport，每截一張就 resize 一次頁面。ZK 每次 resize 都重算 widget 尺寸，有些 demo 每次 resize 結果都不同：

- tabbox 第二個 tab strip：第一次 resize 前寬 103px，之後 87px。
- anchorlayout 第一區：每次 resize 長高 111px（492 → 603 → 714 → 825 → 936）。

所以一個區段拍起來的樣子取決於前面拍了幾張；寫基準時每張圖要連續兩次相同才算穩定，比對時只拍一次，兩者張數不同，基準與比對就對不上。helper 先把視窗高度設成文件高度加 1000px（resize 只發生一次），之後每張截圖都放得進視窗，不再 resize。

文件高度仍可能比視窗高：pdfviewer（以及已於 marble 移除的 component-theming.zul）有元素貼著視窗底部定位（不屬於 `.z-p-8`），文件高度永遠是視窗高度加 20px，而 `.z-p-8` 本身不變。所以 helper 不斷言「放得進視窗」，改為拍完後比對各區矩形。

## 量測值

雜訊（預算 0，`SECTION_NOISE=1`，同一版本跨瀏覽器 process 各跑 2 次，8105，Chromium）：

| 範圍 | 區數 | 差異不為 0 的區 |
|---|---|---|
| gallery-scan（含已移除的 component-theming 112 區） | 437 | `toolbar-05-panel-mold` 1 px、1 px；`toolbar-06-vertical` 2 px、0 px |
| screenshot.spec（16 頁，含 tree、tabbox） | 83 | 無 |
| tree、tabbox（pilot） | 21 | 無 |

最大雜訊 2 px，預算 20 px 為其 10 倍。樣本只有 2 次，且在同一台機器。

與舊整頁基準逐像素比對（另一個 Opus agent，95 頁）：59 頁 311 張完全相同；其餘 36 頁的差異都能解釋：標題改為標準樣式（avatar、badge、chip）、舊基準過期（13 頁，字級改名等）、token 改動（toast、notification、progressmeter）、舊基準本來就拍到截斷或錯位的畫面（biglistbox、codeeditor、listbox-header 等）。

## 突變測試

在單一元件注入 1px 邊框或外框：

| 頁面 | 注入 | 整頁版（容忍 0.2% 面積） | 分區版（預算 20） |
|---|---|---|---|
| tree | 第一個 `.z-tree` 的 1px 紅邊 | 通過 | 失敗，896 px |
| tabbox | 第一個 `.z-tabpanel` 的 1px 紅邊 | 通過 | 失敗，3,092 px |
| component-theming（該頁已於 marble 6439754819 移除，量測時仍存在） | 第一個 `.z-button` 的 1px 紅色 outline | 容忍 47,121 px，通過 | 失敗，132 與 148 px（兩次） |
| badge | 第一個 `.z-badge` 的 1px 紅色 outline | — | 失敗 |

## 頁面與測試的改動

- avatar、badge、chip：區段標題改為標準樣式 `z-text-uppercase z-border-bottom z-pb-2 z-mb-3`。
- component-theming：本分支曾把這頁包成 111 個區段並切出 112 張分區圖；marble 在 2026-10-18（6439754819）移除了這個頁面，rebase 時採用 marble 的刪除，這部分的改動與基準圖都不在分支內。
- responsive-grid：頁面標題與說明包成一個 `<div>`。
- window：viewport 相對定位的 overlapped 視窗在視窗撐高後落在所有區段之外，改為另外對每個 `.z-window-overlapped` 單獨截圖（`window-overlapped-00.png`、`-01.png`）。
- `doc/font-size-baseline.json`：只更新 responsive-grid.zul 一條（13px 326 → 327，包標題與說明的 div 多 1 個元素）。實測 HEAD 版在 e3 伺服器上 13px 為 329，基準 326，差的 3 是 e3 的 za11y 多出的 `.sr-only` 元素（130 頁中 104 頁與基準恰好差這個），加上本次改動 330。其餘約 26 頁本來就與基準有殘差，本次沒有動。component-theming.zul 的條目已由 marble 移除。

## 略過的區段（記錄）

- carousel「Autoplay (manual/visual confirmation)」：每 2 秒換一張，頁面自己標明不屬於任何靜態畫面斷言。
- grid-header、listbox-header、tree-header 的 sizedByContent 區：初次載入就分成兩種結果（欄寬 225px 時 "Super Long Product Name Here" 被省略號截斷、228px 時完整顯示），之後撐高視窗或等待都不會改變。舊整頁基準同樣受影響。2026-10-18 在 e3 預覽站（Chromium）對 `listbox-header.zul` 每次新開 browser context 載入 12 次：

  | 條件 | 截斷 | 欄寬 |
  |---|---|---|
  | 正常載入 | 2/12（多批量測在 2/12 到 8/12 之間，與快取狀態有關） | 228 或 225 |
  | web font（Inter）延遲 1 秒 | 12/12 | 225 |
  | web font 被攔截（使用後備字型） | 0/12 | 225 |

  欄寬在初次渲染時以當下可用的字型量一次：字型較晚載入時，用的是較窄的後備字型，Inter 載入後文字變寬，超出欄寬而被省略號截斷，ZK 沒有在字型載入後重量。grid-header 單跑 10 次失敗 1 次，tree-header 同屬 sizedByContent 區。歸屬見下一節「根因與歸屬」（核心已表態這是預期行為，觸發條件在 Marble 的非同步 web font）。

## 根因與歸屬（2026-10-19）

sizedByContent 的截斷與 tabbox 首次渲染的 tab strip 寬度是**同一個根因**：ZK 的 widget 在 mount 時量一次尺寸，之後才載入的資源（web font、toolbar 圖片）改變了元素寬度，不會重算。

- sizedByContent：欄寬以當下可用的字型量一次。Marble 自行托管 Inter，`font-display: swap` 且沒有 preload，載入前先用較窄的後備字型。實測見上表。
- tabbox：同一頁連續載入，首次渲染的 tab strip 寬度是 104 → 103 → 87px，隨資源進入快取逐步收斂到正確值 87（500 − toolbar 333 − 箭頭 80）。`Tabs._scrollcheck` 以量到的 `toolbarWidth` 計算 `tabs.style.width`；104 對應當時量到的 toolbar 只有 316，最終 333（差 17px，約一張 16×16 的圖加邊距）。先前以「延遲圖片 1 秒」否定圖片假設的實驗設計有誤（正常與延遲兩種模式在 mount 時都尚未載入圖片，所以前後都是 104），已更正。
- **核心不視為 bug。** 同一根因在 ZK-4243（hflex="min" 搭配 Font Awesome 圖示，Closed、Resolution = Invalid）已被核心團隊回覆為「預期的瀏覽器行為」，並寫在組件參考文件 `LabelImageElement#IconSclass`：「ZK 在初始化時不知道 web font 是否就緒」，建議固定寬度。ZK-5164、ZK-5029 是另一個根因（`offsetWidth` 取整），已修復，與字型無關。因此不開核心 Jira。
- 歸屬：sizedByContent 的觸發條件是 Marble 的非同步 web font，應由 Marble 處理（預先載入字型、或讓後備字型的度量接近 Inter）；tabbox 的差距來自預覽頁 `<toolbarbutton image=…>` 沒有指定圖片尺寸，屬預覽頁的寫法，不是 Marble CSS。

## 已知事項

- ZK 的 tabbox（`tabbox.zul`「Horizontal with Toolbar」）：見上一節「根因與歸屬」。首次渲染恆為 104px、任何 window resize 之後為 87px；與 web font 無關，與 toolbar 圖片的載入時機有關。
- 舊基準就有的現象（非本次改動）：tabbox-02 的 tab 分隔線只畫在 strip 範圍、垂直 tabbox 的 panel 沒有頂邊框。是否處理屬 Marble 外觀問題。
- PAD 16px 涵蓋 elevation-3；若頁面有更大的陰影或 focus ring，需要調大。
- `forced-colors-gallery` 下次執行時，avatar、badge、chip、responsive-grid 的 `-forced-colors.png` 會因標題底線而改變，那是審查用截圖，需審查後再提交。
- 移除 `<page>-gallery.png` 之後，`doc/migration/tools/` 下幾支遷移用腳本（`verify-3.18b.sh`、`verify-p3-gate.sh`、`zero-tolerance/`）仍引用 `*-gallery.png`，它們找不到這些舊檔；那些腳本不是這次工作的測試。
