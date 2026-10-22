# 元件圖檔以 URL 載入：問題定義與 CE／EE 情境

日期：2026-10-08。做法與結果見 `tasks/data-uri-to-url-plan.md`；本文件定義問題、列舉情境，並記錄各情境的驗證狀態。

## 1. 要解決的問題

Marble 的 CSS 不能含 DSP，卻需要「請求時才知道」的網址（context path、build 號）。CSS 本身無法拼接字串，`url()` 也不接受變數，所以「誰、在哪、何時」把 `~./zkex/img/x.svg` 換成真網址，要有一個**在 CE 與 EE 各種組合下都成立**的答案。

**答案：** 元件 CSS 直接寫 `url(~./<模組>/img/…)`，`WcsExtendlet` 在組好整個 `zk.wcs` 後對全部內容轉換一次（與 DSP 的 `${c:encodeURL}` 同一個 `Encodes.encodeURL`）。路徑中的模組名已足以讓 classpath 找到檔案，轉換不必知道它屬於 CE 或 EE。

## 2. 角色

| 角色 | 擁有的圖檔 | 寫法 |
|---|---|---|
| **zul**（CE） | 27 個元件圖、1,943 個圖示、字型 | `url(~./zul/img/…)`、`url(~./zul/font/…)` |
| **zkex**（EE） | colorbox 5 個（含 2 個 GIF） | `url(~./zkex/img/marble/…)` |
| **zkmax**（EE） | signature 3 個、goldenlayout 6 個 | `url(~./zkmax/img/marble/…)` |
| zkbind／zhtml／zkplus／za11y／zuti 等 | 無 data URI | 不涉及 |
| **主題 jar**（如 `iceblue_c.jar`） | 自己的 CSS 與圖 | 以 `~./<主題名>/…` 改寫路徑（`ServletFns.resolveThemeURL`），不經過本機制 |
| **客戶的 addon／主題** | 客戶自己的圖 | 只要 CSS 經過 wcs，同樣寫 `url(~./<模組>/…)` 即可 |

## 3. 影響結果的三個維度

1. **模組組合**：只有 CE、CE + zkex、CE + zkmax、EE 全含。
2. **主題**：Marble（預設）、IceBlue（`iceblue_c.jar`）、其他主題 jar、資料夾型主題、客戶自訂主題。
3. **部署方式**：context path 為根或非根、單 war、嵌入頁（`reset-embed.css`）、Spring Boot 內嵌 jar。

## 4. 已確認的機制事實（來自原始碼）

- 非預設主題時，`StandardThemeProvider.getThemeURIs` 把 `zk.wcs` 的網址換成帶主題後綴的版本（`Aide.injectURI`），`WcsExtendlet` 剝掉後綴後**仍解析同一份 `zk.wcs`**。
- 每個 `<stylesheet>`、每個 `css-uri` 都會經 `beforeWidgetCSS`：`~./zul/css/`、`~./js/zul/`、`~./zul/font/` 以及 EE 的 `~./js/zkex/`、`~./zkmax/css/`、`~./js/zkmax/` 會被 `resolveThemeURL` 改寫到主題 jar；所以非 Marble 主題下，Marble 的元件 CSS 不會被載入。
- `WcsExtendlet` 在每個 wcs 回應末尾附加整個語言的 `css-uri` CSS，所以**不能**為每個模組另開 wcs（會重複約 285 KB）。
- 轉換只在回應含 `~./` 時才進行；沒有字面 `url(~./…)` 的內容不受影響。

## 5. 情境矩陣

「現況」欄：**已驗證**＝實際跑過測試或預覽；**推論**＝只由原始碼推得，沒有跑過。

### 5.1 Marble（預設主題）

| # | 部署 | 期望行為 | 現況 |
|---|---|---|---|
| S1 | CE only | zul 的圖、圖示、字型都有；不出錯 | 推論：沒有任何 EE 專屬程式碼 |
| S2 | CE + zkex + zkmax | 三者的圖檔網址都 200 | **已驗證**（`B110_ZK_6112Test`、預覽） |
| S3 | CE + zkex only | zkex 的圖檔正常 | 推論 |
| S4 | CE + zkmax only | zkmax 的圖檔正常 | 推論 |
| S5 | 非根 context path（如 `/app`） | 所有網址含 `/app` 前綴 | **未驗證**（同一個 `Encodes.encodeURL`，沒有測） |
| S6 | `browserDefault=true`（嵌入頁） | 圖檔照常載入 | **未驗證** |
| S7 | Spring Boot 內嵌 jar | 圖檔照常載入 | **未驗證**（不再有 classpath 掃描，風險已降低） |
| S8 | 客戶 war 自己的 CSS 寫 `url(~./foo/…)` | 被轉換 | 推論：只要經過 wcs 就會轉換，尚未文件化 |

### 5.2 非 Marble 主題

| # | 部署 | 期望行為 | 現況 |
|---|---|---|---|
| T1 | CE + IceBlue | IceBlue 自己的圖；不出現 Marble 的圖 | 推論：Marble 元件 CSS 不被載入；`iceblue_c.jar` 內 111 個檔案沒有任何字面 `url(~./…)`，轉換對它是 no-op |
| T2 | CE + EE + IceBlue | EE 元件用 IceBlue 的圖 | 推論，同 T1 |
| T3 | CE + EE + 資料夾型主題／客戶主題 jar | 同 T2 | 推論 |
| T4 | 主題 jar 想覆寫 Marble 的 EE 圖 | 客戶圖優先 | **不支援**：轉換不查主題 jar（與 IceBlue 的 `encodeURL` 相同），需另一種寫法 |
| T5 | 執行中切換主題（`CookieThemeResolver`） | 每個主題各得各的 | 推論：`zk.wcs` 以主題後綴分開快取；未驗證 |

IceBlue 端對端：`testIceBlueOnly` 需要 IceBlue theme jar 並切換主題，嘗試的方式沒有切換成功（斷言值仍是 Marble 的 24 px），所以 T1–T3 沒有端對端結果。

### 5.3 圖檔本身

| # | 情境 | 現況 |
|---|---|---|
| I1 | 圖示晚於元件出現 | 已實測：晚約 0.1–0.2 秒（1.6 Mbps、4 倍降速） |
| I2 | 為 EE 另開 wcs | 已驗證會重複 285 KB，不採用 |
| I3 | `<link>` 直接載入的樣式表（`tablet.css.dsp`、`skeleton.css.dsp`） | 不經過 wcs，不轉換；目前沒有圖檔 |

## 6. 需求與結果

| 需求 | 結果 |
|---|---|
| R1 Marble 的 CSS 與設定檔不含 DSP | 滿足 |
| R2 只有 CE 的部署能運作 | 滿足（沒有任何 EE 專屬程式碼） |
| R3 新增 EE 模組不改 CE | 滿足 |
| R4 非 Marble 主題不輸出 Marble 的圖 | 滿足（Marble 的元件 CSS 在其他主題下不會被載入） |
| R5 主題 jar 可覆寫 CE／EE 的圖 | **不滿足**，與 IceBlue 的 `encodeURL` 行為相同 |
| R6 非根 context path 網址正確 | 未實測 |
| R7 不為每個模組多一次 wcs 請求，不重複 285 KB | 滿足 |
| R8 客戶能以同樣方式加入自己的圖 | 滿足（寫 `url(~./…)` 即可，限經過 wcs 的 CSS） |
| R9 新人看得出檔案該放哪 | 滿足：沒有新慣例，路徑就寫在元件自己的 CSS |

## 7. 未解決

- **R5 主題覆寫**：若日後需要主題專屬圖，要新增類似舊 `encodeThemeURL` 的寫法（例如 `url(~./theme:zkex/img/…)`），尚未設計。
- 補驗證：CE only（S1）、非根 context path（S5）、嵌入頁（S6）、fat jar（S7）、IceBlue 端對端（T1–T3）。
