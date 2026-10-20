# Theme Pack 與 `.css.dsp` 退場：結論

用途：記錄 meeting D4/D6 關於 `.css.dsp` 移除與 Theme Pack 退場的最後結論、決定與未解決事項。

注意：本檔基準為 2026-10-02。之後 `dsp-fallback-proto`（Option B 原型）已決定不納入此版本，DSP 相關段落只作歷史紀錄；Theme Pack 部分未見後續變動紀錄。

來源（已整理）：doc/dsp-removal-iceblue-coexistence.md、doc/theme-pack-retirement-brief-for-jean.md、doc/theme-pack-retirement-evaluation.md

## 結論

- `.css.dsp`：Marble 改為 plain `.css`，採 Option B（`lang.xml` 宣告 `.css`，該主題缺檔時 fallback 到 `.css.dsp`）；IceBlue 與舊主題維持可用。Option C（IceBlue 改 plain CSS）為後續獨立清理。
- Option B 原型在 `dsp-fallback-proto` 分支（zk、zkcml），`B110_ZK_6112Test` 6/6 通過；完整 zktest 與 Playwright 未跑。
- Theme Pack：退場打包與 generation pipeline，保留 27 個配色（palette）為 CSS 檔，建議內建於 EE、一行設定選用並可執行期切換（Option A）。
- IceBlue 以 CSS 變數重寫放在 11.x minor；11.0 僅做 Phase 0（`zkThemeTemplate` submodule 改指 `iceblue` 分支、移除 `--remote`）。
- Marble 不受影響，使用自身的 brand override（single seed color）。

## 關鍵數字

- `<css-uri>`：zul 86、zkex 9、zkmax 35（改後 `lang.xml` 83 個，zkex/zkmax `lang-addon.xml` 44 個）。
- Marble DSP 語法：2 個 `${c:encodeURL(...)}`（`norm.css.dsp`）。
- IceBlue：77 個 `.css.dsp`、14 個使用 DSP、103 個 `<c:if>`、44 個 `${c:encodeURL}` / `${c:encodeThemeURL}`。
- 配色：Ruby 23 個變數、最大 Material 51 個、27 個配色共 110 個變數名、IceBlue 公開 `--zk-*` 842 個。
- 13 個配色使用圖片 URL：`@loadingAnimationDefer/Load` 13、`@sliderTicks` 10、`@progressmeterBackgroundImage` 3。
- 驗證面：27 配色 × 2 profile = 54 個 jar；11.0 剩餘容量約 16 engineer-weeks。

## 決定

- [DSP] D1：採 Option B（建議）。
- [DSP] D2：無 DSP 的資源 URL（Marble 字型、IceBlue 圖片），data URI 或相對 `url()`（後者未驗證）。
- [TP] D1：「退場」= 退 pipeline 與 per-theme jar，保留 27 配色為 CSS 檔（建議 A）。
- [TP] D2：交付與選擇方式，建議單一 jar、以 root class 限定配色範圍。
- [TP] D3：圖片 URL，同 [DSP] D2，不重複決定；[TP] D4：Theme Pack 公告與 EOL 負責人（product/sales）；建議 11.0 release notes 公告，11.1 或 11.2 移除。

## 未解決事項

- Marble 尚未合併；zk 與 zkcml 分支僅存於本機。
- `zkThemeTemplate` 仍產出 `.css.dsp` 與 `${c:encodeThemeURL}`，以模板建立的客戶主題仍得到 DSP。
- 一般 `.css` 需要主題資源 URL 時，目前只有 `fonts.css` 有解析機制。
- 主題未提供的 stylesheet 不 fallback 到 Marble 檔案（`reset.css`、`fonts.css` 同）；此規則已在原型實作，尚未文件化。
- 文件與命名仍含 `.css.dsp`（`check-css-dsp.js`、`CLAUDE.md`、marble-theme skill、約 120 個 `.md`）。
- 配色：compact profile 內容未確認、scoping 設計未做、HSL 與 `color-mix()` 差異是否可接受未決、Ruby 原型與 10.x 遷移頁未做。
- 11.0 Phase 0 submodule 修正（`zkcml/zkthemebuilder/build.sh`、`.gitmodules`）尚未做。
