# Marble 品牌色與 compact 密度檢查：結論

來源（只讀整理）：doc/marble-brand-batch1-report.md、doc/marble-brand-batch1-verification.md、doc/marble-compact-brand-batch3-report.md、doc/marble-compact-brand-batch3-verification.md、doc/marble-density-fix-batch4.md、doc/marble-density-fix-batch4-verification.md

用途：記錄 copper 品牌色與 compact 密度下的殘留掃描、截圖與高度檢查，以及 compact 密度 CSS 修正的最終狀態。

## 結論
- 品牌色殘留（`brand` Playwright project，`brand-override.spec.ts`）：112 個測試，兩次獨立 process 皆 112 passed；111 頁殘留為 0，未加排除清單。
- 掃描遮罩由 Playwright `mask` 改為注入 `img, canvas, video{visibility:hidden !important}`，因 `mask` 會蓋住其上的主題元件（驗證者實測漏抓 cropper 寫死色）。
- compact 截圖與高度檢查（`compact` project，`compact-density.spec.ts`）：`compact` + `brand` 合計 257 passed、0 failed；toolbar overflow 幾何測試由 `test.fail()` 轉為正常通過。
- 批次 4 CSS 修正 7 項：tab、groupbox 預設 header、timebox、searchbox、chosenbox 空輸入、listbox paging 按鈕、toolbar overflow。依裁示另套用 3 項：groupbox 3D header 改綁 `--zk-control-height-md`、spinner 按鈕欄 `min-height: 0; line-height: 1`、toolbar-tabs 改 `calc(var(--zk-tab-height) + 1px)`。
- 未新增 `--zk-*` token、未新增 `!important`、未改 widget TS／Java。
- `KNOWN_DEFECTS` 由批次 3 的 10 組原因（60 個 key）降為空陣列；`EXCEPTIONS` 保留 8 組內容撐高或頁面自設尺寸的原因。

## 關鍵數字
- 殘留判定：每通道容差 `CHANNEL_TOLERANCE = 2`，門檻 `RESIDUAL_MAX_PIXELS = 0`，9 個 family token。
- 衍生色測試容差：OKLCH L ±0.01、hue ±0.5°、chroma ±0.003（絕對值）。
- 截圖門檻：`compact`、`brand` 的 `toHaveScreenshot` threshold 0.02（YIQ 距離：primary-container tone 0.92→0.88 為 0.0500 stock／0.0492 copper，預設 0.2 會漏抓）；`VARIANT_SECTION_MAX_DIFF_PIXELS = 20`，實測雜訊 0；新基準 150 張（compact 75、brand 75）。
- 突變偵測：`button.css:188` 寫死色 → `button` 196 px、`popup` 176 px 紅；`checkbox.css:116-117` → `checkbox` 831 px 紅。
- compact 高度（別名），修正前 → 修正後：tab 46 → 41（40+1）；groupbox 預設 header 49 → 40（40）；3D header 40 → 32（32）；spinner 43 → 32（32）；timebox 37 → 32（32）；searchbox 40 → 32（32）；chosenbox 空 38 → 32（32）；listbox paging 按鈕 32 → 28（28）。
- comfortable（載入後）變動：groupbox 預設 header 49 → 48（−1px）；spinner 43 → 40（−3px）；toolbar-tabs 40 → 49（+9px，僅「Horizontal with Toolbar」區段）；其餘 0。
- toolbar overflow 按鈕位置（compact，300px toolbar）：344.2–374.2（toolbar 右緣 332）→ 302–332。
- 寬度掃描（240–420px 逐 px，每種密度 362 樣本）：HEAD 模擬 comfortable 288、compact 300 個樣本超出，最大 43.9px；修正後各 12 個，最大 3.9px。
- 測試：選擇器 `partial` 793／1692；全跑 754 passed、28 failed、11 skipped，28 個失敗皆為 `font-size` project（HEAD 狀態下 130／130 失敗，既有環境漂移）。

## 未解決事項
1. toolbar overflow 殘餘：寬度 comfortable 277、279、329、331、392、394 px，compact 271、273、323、325、386、388 px 時，按鈕仍超出 toolbar 右緣 1.9–3.9px。`Toolbar.ts` 屬核心 widget，Marble 側不再改；是否開 Jira 待使用者決定（D35）。原因未證實。
2. 高度檢查的 header 只有下限（高度 ≥ 別名，且 padding = `--zk-header-padding-y`），標題行高漂移造成的過高 header 會通過；toolbar-tabs 的 `+1px` 寫死，未讀 `.z-tabs` 的 border。
3. 單列 chip 的 chosenbox（`.z-chosenbox-item` 28px）在 compact 預計 38px，未驗證（預覽頁無單 chip 案例）。
4. 色彩掃描覆蓋：只掃 9 個 family 實色 token；`color-mix()` 淡色與半透明疊色不被偵測（未實測）；圖片、canvas、video、codeeditor 文字內的寫死色不被偵測。51 頁在 stock 下無 family 像素，測試必然通過。
5. 只看靜止畫面：hover、focus、popup 狀態不在範圍內；只跑 copper，slate 未跑；截圖只在 macOS Chrome 量測，跨機器字型與反鋸齒未確認。
6. compact 下 grid、tree 的 Frozen Columns 右緣多露出一欄，原因未確認；grid-detail 展開列 36.5–37px，原因未確認。
7. 範圍外：comfortable 的 overflow popup 開在 x = 0（HEAD 相同）。
8. batch 1 §11 與 batch 3 §補修的修正，來源未記載獨立 verifier 再驗證。
