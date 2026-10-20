# CSS 變數與 data URI 效能結論

用途：整理 Marble `--zk-*` 變數數量、CSS 變數作為 image URL prefix 的可行性，以及 data URI 效能疑慮（D1、D5）的結論與量測值。

來源（只讀整理）：
- doc/css-variable-performance-evidence.md
- doc/css-variable-url-prefix.md
- doc/data-uri-performance-claims.md
- doc/css-variable-benchmark/init_bench.py、doc/css-variable-benchmark/runtime_bench.py

## 結論
- `:root` 上的 custom property 數量未造成可量測的渲染影響；成本取決於變數被使用的範圍。決定：Marble token 全部保留在 `:root`（D1 Option A）。
- 不在每個元素或 `*` 上重複宣告 token（Chromium 127 記憶體回歸案例，Chromium 132 修復）。
- CSS 位元組大小是 token 的實際成本。
- CSS 變數無法做「共用 context path + 字串拼接」：CSS 無字串串接，`url()` 不接受變數，`@font-face` 不允許 `var()`。可行形式是每張圖一個變數、存完整 `url(...)`，由 server 經 `zk.wcs` 的 `<function>` 寫入；尚未實作。客製主題須自行加 `<function>` 或使用完整位址。
- data URI 疑慮：`norm.css.dsp` 內的 data URI 幾乎全是整組 SVG icon（`scripts/build-css.js:508-518`），不是會議所說的約 29 個元件圖片。「檔案過大、首屏變慢」成立；「無法 progressive 載入」與「無法快取」在 ZK 情境不成立。核心問題是整組 icon 位於 render-blocking 的 `norm.css.dsp`。
- D5 候選方案（來源未選定）：A 將 icon set 移出 `norm.css.dsp`（可非阻塞或按需載入，約移除 79% 阻塞 CSS 重量）；B 以檔案或 SVG sprite 按 URL 提供；C 維持現狀。

## 關鍵數字
- Marble：592 個唯一 `--zk-*` 名稱（652 個宣告）。
- 合成頁（10,000 個 `div`，Chrome 154.0.8037.92，中位數）：`:root` 宣告 0 / 600 / 3,000 / 10,000 時，改被引用變數 55.4 / 55.0 / 55.4 / 58.0 ms；改未引用變數 13.6 / 13.7 / 14.3 / 15.6 ms；切換無關 class 0.8 / 0.7 / 0.7 / 0.8 ms。
- 初次樣式與版面（`var` 規則）：600 宣告 113 ms、3,000 宣告 115 ms、10,000 宣告 269 ms（含解析約 200 KB CSS）。0 宣告列為冷啟動偏差，不採用；`calc` 欄改的是未被引用的 `--pad`，不代表 `calc()` 成本。
- `norm.css.dsp`：1,066,843 B raw / 105,452 B gzip；去除 data URI 後 149,038 B / 22,375 B；data URI 佔 86% raw、79% gzip（約 83 KB）。共 1,943 個 data URI，其中 4 個為 raster（`image/gif`）。
- 理論傳輸時間（未實測）：83 KB 於 10 Mbps 約 0.07 s、1.6 Mbps 約 0.4 s、400 kbps 約 1.7 s。
- 外部參考（web.dev、Igalia）只支持「成本由繼承與失效範圍決定」，未提供 ZK 專屬數字。

## 未解決事項
- 量測皆為合成頁，未用 ZK 頁面；後續可於 Marble preview 切換 `--zk-*` 並讀 DevTools「Recalculate Style」。
- 未測 Firefox（Stylo）與 Safari。
- 未完成：無節流下簡單 ZUL 頁的 first paint（有／無 icon set），以及 4x CPU 節流下的 CSS 解析時間。
- 未完成：`zk.wcs` `<function>` 原型，需驗證 Marble 字型 `@font-face`、IceBlue（`iceblue_c.jar`）與非根 context path。
- D5 方案 A／B／C 尚未決定。
