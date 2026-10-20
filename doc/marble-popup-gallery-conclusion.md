# 彈出層截圖測試（Marble popup gallery）結論

用途：收斂 Marble 彈出層截圖測試（批次一、批次二）、覆蓋量測、variant 缺口與 NS9 分段 gallery 的結論、決定、關鍵數字與未解決事項。

來源（只讀整理，原檔未動）：`doc/marble-popup-gallery-report.md`、`doc/marble-popup-gallery-verification.md`、`doc/marble-popup-gallery-verification-2.md`、`doc/marble-popup-inventory.md`、`doc/marble-popup-batch2-report.md`、`doc/marble-popup-batch2-verification.md`、`doc/marble-popup-coverage-measure.md`、`doc/marble-variant-gap-remeasure.md`、`doc/marble-ns9-verification.md`

## 結論

- **批次一**（branch `marble-popup-gallery`，commit `841c3fc13d`、`73dab49d1f`、`f5953fe0dd`，未 push、未 merge）：16 個有彈出層的元件加 toast、notification、messagebox、errorbox，共 20 個元件、45 個測試、45 張基準圖（`zkpreview/doc/screenshots/*-popup-*.png`）。決定 D3、D14、D15、D16 皆為 A；新觸發點放在新頁 `pv/popup-fixtures.zul`。
- 全部測試以真實手勢（`page.mouse`、click、鍵盤）開啟彈出層，沒有用 widget API 開啟。
- 批次一逐張目視（verifier 1）：42 張 OK；`navbar-popup-open.png` 的標籤被裁，已重拍，verifier 2 判定 PASS。
- `colorbox-popup-palette-selected.png` 保留原圖：hover 色塊後的瀏覽器預設 focus ring 被拍入；測試內加註解，指向 `doc/marble-theme-followups.md` #62。
- 選擇器補修（報告「補修與 D18」段）：元件自己的頁（`<name>.css/.ts` 對應 `<name>.zul`）與 owner 頁加入選取；`component-theming` 依 `PAGES` map 對應頁面；class index 改以 client ROD off 建置。
- **批次二**（branch `gaps-round2`，未 commit；決定 D30 = A）：21 個未涵蓋 class 分為截圖 11、DOM／computed style 斷言 5、移出清單 5（ZK 不產生：`z-selected`、`z-menuitem-selected`、`z-colorpalette-button`、`z-bandpopup-content`、`z-mask`）。新增 6 張基準圖與 11 個測試（`popup` project 45 → 56）。
- 批次二 verifier 2 判定 PASS-WITH-FINDINGS；已補修 F1（modal、全頁 busy 加 computed `box-shadow` 比對）、F3（class index 重產）、F4（兩張全 viewport 圖改為卡片或視窗加 PAD 裁切）；F2、F5 未處理（見未解決事項）。
- ROD loading 測試（grid、listbox、tree）攔截 `/zkau` AU 請求以維持 loading 狀態，觸發動作為真實滾輪。
- `z-menupopup-separator` 在設計上隱藏（`menu.css` 的 `display: none`），只做 DOM／computed style 斷言，不拍圖。
- 單一 toast 的 outline 突變（每張約 10 px，等於預算）偵測不到；改為 `filter: invert` 後偵測。modal 的 elevation-3 改 elevation-1 在 scrim 上截圖無變化，改以 computed `box-shadow` 斷言抓到。
- **Variant 缺口**（`marble-variant-gap-remeasure.md`）：121 個 class 對 `class-index` 皆不存在；74 找得到 emitter，47 無 emitter（多為 CSS sclass 工具類）。passwordbox 本版無元件，不建頁；skeleton 為 zkex client 端 mixin，未建頁。
- **NS9**（branch `ns9`，HEAD `a41956c5e24`，verifier PASS 5/5）：gallery 清單改由 `scripts/aggregate-pages.json` 提供，測試數與 marble 相同；分段 section PNG 可被突變偵測。

## 關鍵數字

| 項目 | 數值 | 容忍值／條件 |
|---|---|---|
| 批次一 雜訊（`POPUP_MAX_DIFF=0`，3 次獨立 process） | 45 張全部 0 px | 一般 10 px；drawer、toast positions 20 px；比對仍用 `threshold: 0.05` |
| 批次一 穩定性 | 45 passed（verifier 1：3 次，含重啟後；verifier 2：2 次） | 無 retry |
| 批次一 突變（verifier 1，7 元件 8 個突變） | 該元件測試全數變紅；差異 59 至 149166 px | 10 px（單一 toast 合計 90 px 未偵測） |
| 批次一 選擇器 | 補修前 18／20 元件有選到；補修後 combobox、messagebox 皆選到 | — |
| 批次二 雜訊 | 56 passed；6 張新圖與 45 張舊圖皆 0 px（3 次） | 10 px；兩張原 20 px，F4 後改 10 px |
| 批次二 突變 | 同時套用 8 檔 11 規則：16 失敗、40 通過；逐一：combobox 378 px、component busy 1373 px | 10 px |
| 批次二 選擇器 | `combobox.css` partial 125/1539；`window.css` partial 67/1539；`misc.css` full | — |
| 覆蓋（A1，批次一 45 測試時） | 88 個 class：圖內可見 67（根節點內 52、外 15），不在圖內 21 | 「可見」只看 display、visibility、尺寸與 clip，不看 opacity、遮蓋 |
| 盤點（inventory） | 229 個 class、87 個擁有者；popup 53、api 35、variant 110、no-page 10、other 21；(?) 70 | 完整逐 class 清單已不保留 |
| 索引頁數 | 139（批次一）→ 143（批次二 F3 重產）；class 3338 → 3346 | client ROD off 建置 |
| NS9 | gallery 81、forced-colors-gallery 98、focus-scan 117、smoke 118（同 marble）；listbox 紅底突變 13448 px | section `maxDiffPixels: 20`（舊整頁 `maxDiffPixelRatio: 0.002` 未偵測） |

## 未解決事項

- 批次一 `popup-popup-open.png`：verifier 1 建議處理（PAD 內含頁面文字），未處理；穩定（3 次 0 px）。
- `colorbox-popup-palette-selected.png`：verifier 1 請求決定 UA focus ring 是接受為真實行為，或判定為 Marble 缺陷（`.z-colorpalette:focus-visible` 無 Marble 樣式）；報告保留原圖，決定未見記錄。
- `datebox.zul: open calendar with the timezone selector` 間歇失敗：批次一與批次二皆有；批次二約 13 次完整跑失敗 2 次，單測獨立重複 51 次 0 失敗；根因未確認，測試未修。
- 去註解的 `refsInRange` 變更：舊對新選擇器比較，遺失 1,705 組（a 998 組由 spec 註解誤選造成，已逐一確認；b 707 組來自 `computedUrlPage` 取代舊的第 3 層退路，全為 `component-theming.spec.ts`）。依字面標準判 FAIL；是否改判為預期由呼叫端決定。
- 選擇器：`window.css` 變動選不到 messagebox 的 popup 測試（F5）；`misc.css` 判為 full（共用檔），是否改為 class 解析未決。
- 批次二 F2：`.z-grid-loading` 等讓 body 變成 `position:absolute; inset:0`（box 由 [33,86,238,145] 變為 [0,0,1280,145]）；ROD 斷言只讀 `background-color`，偵測不到此位置變化；待 `doc/zats-1117-fix-plan.md` G7 合併後加 box 斷言。
- 死規則（`.z-mask`、`.z-comboitem.z-selected`、`.z-menuitem.z-selected`、`.z-bandpopup-content`、`.z-colorpalette-button`）與寫死色值（grid、listbox、tree 的 `-loading` 寫死 `rgba(255, 255, 255, 0.6)`）：是否移除或改 token 未決；移除前需確認其他主題或 sclass 是否請求（未確認）。
- Variant：47 個無 emitter 項是否補 sclass 示範（如 `z-button-*`）由呼叫端決定；tablet 專屬 class（`z-frozen-sticky`、`z-scrollview-*`）未量、未建頁。
- `comboitem` 的 `image` 不縮放（圖以原尺寸顯示）：是否為缺陷、IceBlue 行為未確認。
- 覆蓋：tablet 未量（A1 為桌面 project）。
