# GATE16-FINAL（C 線第 16 批：follow-up 19，平板 scrollable menubar）

日期 2026-10-11。預覽 8085，Playwright，平板 UA（iPad、834×1112）。根因由另一個 session 查出並由 Opus Verifier 獨立量測；修正與驗證由 C 線完成。Verifier：Opus 5.5，看不到 diff。

## 根因
Marble 的回歸，不是 ZK bug。平板 UA 下 `zk.tabletUIEnabled` 為真，zkmax 載入 `touch/menubar-touch.ts`，把 `setScrollable()` 變成 no-op（註解 "use native scroll"）。舊 IceBlue 的平板樣式給 `.z-menubar` 加 `overflow: auto` 讓它原生捲動；Marble 的平板 CSS 沒有這條，所以既沒有捲動按鈕、也不能原生捲動，item 畫到灰底外面。

## 修改
`zkcml/zkmax/.../css/tablet/_menu.css`：新增 `.z-menubar-horizontal { overflow: auto; }`。新測試 `menubar-scroll-tablet.spec.ts`；baseline `menubar-tablet.png` 重切（item 3–5 現在被裁在灰底內）。

## 修前實測
overflow-x `visible`，scrollWidth 456／clientWidth 200，scrollLeft 設 120 仍為 0，3 個 item 畫在 200px 框外。

## GATE16：PASS（Opus Verifier）
- A：overflow `auto`，scrollWidth 472／clientWidth 200；scrollLeft 120 讀回 120，最大 272；wheel 與觸控拖曳都能捲；item 3–5 中心 `elementFromPoint` 不命中 menubar；高度仍 48px、無縱向捲軸。
- B：Horizontal、Horizontal with icons（含巢狀 File > Open）、Auto drop（含 Help > About）、直式、component-theming、grid-header、listbox-header、overview 的 popup 全部在視窗內且中心可命中。popup 掛在 `<body>`，不被新的 overflow 裁到。
- C：直式 menubar 與其餘橫式 menubar 的 item 位置、高度與強制 `overflow: visible` 比較差 0；放得下的 menubar 沒有捲軸。
- D：桌面不變（`z-menubar-scroll`、箭頭、`overflow: hidden`）。
- E：2 倍截圖在 scrollLeft 0 與最大值都乾淨；看不到原生捲軸，也不佔高度。限制：這是 Chromium 模擬，真 iOS Safari 的捲動指示器看不到。

## 回歸
tablet 專案 58 條：57 通過、1 條為預期的 `tablet-menubar` baseline 差異，重切後通過。
