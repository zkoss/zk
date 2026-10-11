# Gate：第四批 RED run 第二輪（r2），biglistbox 捲軸（#31、#78）—— 修正後的方法

- **日期：** 2026-10-07，Verifier，量現在的程式碼（8085，Chromium，viewport 1280×900，deviceScaleFactor 2，已注入 no-animation、等 `document.fonts.ready`，量測前游標移到 (2,2)）。
- **目的：** 用 Planner 裁定的「方法修正 1–5」（jess-review-verification-plan.md（過程資料已清除） §第四批 RED run 結果）重跑一次 RED run，確認今天每一項判定都失敗、每一項保護項都通過。第一輪報告：[batch4-red.md](batch4-red.md)。
- **腳本與原始輸出：** `gates/batch4-red/r2-*`（第一輪的檔案未覆寫）：`r2-lib.js`（= `lib.js` 原樣複製）、`r2-doc-scrollbar.js/.json/.png`、`r2-red31.js/.json` + `r2-red31-*.png`、`r2-red78.js/.json` + `r2-red78-*.png`、`r2-protect.js/.json` + `r2-protect-*.png`。
- **來源未動：** 沒有修改任何 theme/CSS/TS/Java 檔，沒有看 diff。`biglistbox.css`（`../zkcml/zkmax/.../big/css/`）mtime 2026-07-21 23:02、`scrollbar.css`（`zul/.../wgt/css/`）mtime 2026-06-30 16:22，都早於第一輪（今天 15:14–15:31），兩輪量的是同一份程式碼。
- **暫時頁：** `zkpreview/src/main/webapp/web/biglistbox-fits.zul`（`FakerMatrixModel(2, 3)`，200px 高，沿用 biglistbox.zul 的 template；實際渲染 2 欄 × 3 列，兩個方向的 thumb 都 `display:none`）本輪重建、量完已刪除，`git status zkpreview/` 乾淨。8085 沒有重啟；ZK 快取著頁面定義，刪除後 `curl` 仍回 200（第一輪同樣情形）。本輪的 fits widget box 是 32–1248 × 100–300（第一輪 112–312，標題區高度不同），不影響任何判定。
- **選取 widget：** 預覽頁 DOM id 是 uuid，一律 `zk.$('$stripedBiglist').$n()`。

## 結論摘要

| 項目 | 今天 | 結果 |
|---|---|---|
| 文件捲軸期望值 | 與第一輪 `doc-scrollbar.json` 完全相同（顏色 ΔE 0、寬 8、距右 0、圓角、token `#0000001f`） | 參考值未變 |
| #31 判定 1（命中測試） | 3 個 widget 都命中 `.z-biglistbox-wscroll-vertical` | 失敗，RED-correct |
| #31 判定 2、3、4（修正帶） | 最差像素全部是 **槽（rgb 242，ΔE 4.51）或槽交疊處（229，9.06）**，不再是邊框／圓角 | 失敗，RED-correct；修正 1 有效 |
| #31 也必須成立 | `vflex=min` 後判定 1 仍命中；forced-colors 下 3 個 widget 判定 1 仍命中 | 失敗，RED-correct |
| #31 保護項（修正 2：垂直用 `#stripedBiglist`，`#biglist` 只水平 + MultipleRow 垂直一次） | 全部通過 | 通過 |
| #78 判定 1、3、5（`#stripedBiglist` 垂直） | ΔE 22.56／距右 3 對 0／槽 ΔE 4.51 | 失敗，RED-correct |
| #78 判定 6（`#biglist` 水平，做 1–5） | 子項 1（ΔE 22.56）、5（槽 4.51）失敗 → **判定 6 整體失敗**；子項 3 依修正 4 的定義今天**相符**（距內框下緣 1，文件 0，差 1 ≤ 1），見「需 Planner 確認」 | 失敗，RED-correct（子項 3 已相符，比照判定 2／3 規則列保護項） |
| #78 token 換色 | token 確實變 `rgb(255, 0, 0)`，thumb 仍 (162)，ΔE 105.43 | 失敗，RED-correct |
| #78 判定 2（寬度）、判定 4（圓角）→ 保護項 | 垂直：8 = 8、圓角兩端 28.87／中心 2.62；水平：8 = 8、圓角兩端 33.38／22.56、中心 0 | 通過（方法已標為保護項） |
| #78 品牌色項 | 依修正 3 **已刪除**，未量 | — |
| #78 其他保護項（放得下的 widget 無 thumb、滾輪／拖曳／夾制） | 通過 | 通過 |
| 只記錄 | hover (162)→(126) ΔE 13.82；forced-colors 下 thumb 與文件 embed rail 中心像素都是 (255) → 都看不見 | 記錄，同第一輪 |

**沒有任何判定今天通過；沒有任何保護項今天失敗。**

## 文件捲軸的期望值（`r2-doc-scrollbar.json`，對照 `doc-scrollbar.json`）

量法同第一輪：`scrollbar.zul` 第 2 個 grid（`ca:data-embedscrollbar="true"`，embedded 模式）的 `.z-scrollbar-vertical-embed`，游標在 (2,2)。容器 `.z-grid-body` 33–271 × 449–594，底色 (255,255,255)。

| 量項 | 垂直（判定依據） | 第一輪 | 水平（僅記錄） |
|---|---|---|---|
| thumb 顏色 | (224,224,224) | (224,224,224)，ΔE 0 | (224,224,224) |
| 寬度／高度 | 8px | 8 | 8px |
| 距內框右緣／下緣 | 0 | 0 | 0 |
| 圓角 | 兩端 10.82、中心 1.06 → 是 | 是 | 兩端 10.82、中心 3.17（同第一輪，反鋸齒邊） |
| 位置 | 263–271 × 449–488 | 同 | 33–155 × 586–594 |

`unchangedVsFirstRun.all = true`。`#78` 的判定仍以第一輪的 `doc-scrollbar.json` 為參考檔。

## #31（D39-A，修正帶）

幾何（viewport 座標；`R` = `.z-biglistbox-outer` 右緣 = box.r − 1；`.z-biglistbox-outer` 仍往下溢出 border-box 1px）：

| widget | box | H | B | R | 欄寬合計右緣 |
|---|---|---|---|---|---|
| `#stripedBiglist` | 32–1248 × 187–387 | 188–227 | 227–388 | 1247 | 683 |
| `#biglist`（預設 MultipleColumn） | 32–1248 × 400–900 | 401–440 | 440–901 | 1247 | 4063 |
| 暫時頁 `fitsBiglist` | 32–1248 × 100–300 | 101–140 | 140–301 | 1247 | 293 |

修正帶的實作（`r2-red31.js`）：像素先過濾 `x ∈ [box.l+1, box.r−2]`、`y ∈ [box.t+1, box.b−2]`，再排除四角各 10×10px；判定 2 的 x∈[R−14, R−1]、y∈[H.top+2, H.bottom−2]，對同 y 範圍 x∈[R−34, R−21] 的中位數色；判定 3 的 y 取整個 inset 後的 box 高度；判定 4 的 y∈[box.b−15, box.b−2]、x 在欄外空白區（colsRight+6 到 R−15）。圓角探針：(R−2, box.t+2) = 237、(R−2, box.b−3) = 248、底邊框列 = 224 —— 全部落在排除區外側／被 inset 排除，修正帶內沒有邊框像素。

### 判定檢查

| 檢查 | stripedBiglist | biglist | fitsBiglist | 結果 |
|---|---|---|---|---|
| 1. `elementFromPoint(R−7, H 中心)` | `.z-biglistbox-wscroll-vertical` | 同 | 同 | 失敗，RED-correct |
| 2. 表頭帶 | **4.51**，(1237, 190)，rgb 242（槽） | （方法排除；記錄 59.27 = 表頭文字） | **4.51**，(1237, 103)，242 | 失敗，RED-correct |
| 3. 右側整條（方法只對暫時頁） | （記錄 33.38 = thumb (162)） | （記錄 88.24 = 列文字） | **9.06**，(1236, 290)，rgb 229（垂直槽與水平槽交疊） | 失敗，RED-correct |
| 4. 底帶 y∈[box.b−15, box.b−2]、x 欄外 | **4.51**，(689, 377)，242 | （記錄 4.51，x 1187 欄外僅 14px） | **4.51**，(299, 290)，242 | 失敗，RED-correct |

與第一輪「修正寫法」欄的預測值（4.51／9.06／4.51）一致。

### 也必須成立

| 項目 | 量到的值 | 結果 |
|---|---|---|
| `#biglist` 切 vflex/hflex = min 後判定 1 | 仍命中 `.z-biglistbox-wscroll-vertical`（R 1203、H 490–529；判定 2 記錄 4.51 = 槽） | 失敗，RED-correct |
| `forcedColors: 'active'` 下判定 1 | 3 個 widget 都命中 `.z-biglistbox-wscroll-vertical` | 失敗，RED-correct |

### 保護項（`r2-protect.json`，真實滾輪／滑鼠）

| 項目 | 量到的值 | 結果 |
|---|---|---|
| 該有的軌道還在：striped 垂直 thumb | ΔE 28.87 ≥ 10；top 227 = B.top 227；與 `-drag` rect (1236,227,1244,275) 一致 | 通過 |
| 該有的軌道還在：biglist 水平 thumb | left 34 ≥ B.left 33；34–81 × 890–898 | 通過 |
| 還能捲（垂直，`#stripedBiglist`） | 滾輪：第一列 `y = 0` → `y = 1`，`_currentY` 0→1，thumb 40→41（widget 相對）；捲到底 thumb 41–89 在 B 40–201 內 | 通過 |
| 還能捲（垂直，`#biglist` 切 MultipleRow 10×100） | `y = 0` → `y = 2`，thumb 439→442；捲到底 `_currentY` 88，thumb 528–576 在 B 440–901 內 | 通過 |
| 還能捲（水平，`#biglist` 預設） | `mouse.wheel(120,0)`：`Header x = 0` → `x = 2`，thumb 34→35；捲到底 `_currentX` 91，thumb 124–172，右緣 ≤ R 1247、左緣 ≥ B.left 33 | 通過 |
| 拖得動 | 按 `.z-biglistbox-wscroll-body` 中心往下 40px：`_currentY` 0→1（striped）、0→22（biglist/MultipleRow），列有跟著捲 | 通過 |
| 表頭不動（以 widget 為基準） | 滾輪、捲到底、拖曳前後 H 相對 widget 的位置相同（本輪 viewport 座標也相同） | 通過 |
| 表頭可以點（`#biglist`，(R−20, H 中心)） | sorticon `""` → `z-icon-caret-up` | 通過 |
| #78 保護：放得下的 widget thumb 數 0 | 暫時頁兩條軌道都沒有 ΔE ≥ 10 且厚度 ≥ 3 device px 的連續段 | 通過 |

## #78（D40-A，修正 3–5）

`--zk-color-outline-variant` 計算值 `#0000001f` → 疊白底 (224,224,224)。

### `#stripedBiglist` 垂直 thumb

| 檢查 | 今天 | 文件 | 結果 |
|---|---|---|---|
| 1. 顏色（判定） | (162,162,162)；對文件 ΔE **22.56**；對 outline-variant 22.56 | (224,224,224) | 失敗，RED-correct |
| 2. 寬度（保護項） | 8 | 8 | 通過 |
| 3. 距內框右緣（判定） | **3**（thumb 1236–1244，R 1247） | 0 | 失敗，RED-correct |
| 4. 圓角（保護項） | 兩端 28.87、中心 2.62 → 是 | 是 | 通過 |
| 5. 沒有槽（判定；只量 thumb 下方 2–16px，上方是表頭區不量） | 最差 ΔE **4.51**，(1236, 277)，rgb 242 | — | 失敗，RED-correct |

### `#biglist` 水平 thumb（判定 6 = 1–5 的水平版）

thumb 34–81 × 890–898；看得見的內框下緣 = box.b − 1 = 899；B.bottom = 901。

| 檢查 | 今天 | 結果 |
|---|---|---|
| 1. 顏色 | (162,162,162)，ΔE 22.56 | 失敗 |
| 2. 高度（保護項） | 8 = 8 | 通過 |
| 3. 距看得見的內框下緣（修正 4，±1px） | 899 − 898 = **1**，文件 0，差 1 ≤ 1 → **相符**（對 B.bottom 則是 3，僅記錄） | 今天就相符（見下） |
| 4. 圓角（保護項） | 左端 33.38／右端 22.56、中心 0 → 是 | 通過 |
| 5. 沒有槽（thumb 右側 2–16px；左側只有 1px 不量） | ΔE 4.51，(83, 890)，242 | 失敗 |

判定 6 整體：子項 1、5 失敗 → **失敗，RED-correct**。

### token 換色與只記錄項

| 項目 | 量到的值 | 結果 |
|---|---|---|
| `:root{--zk-color-outline-variant: rgb(255,0,0)}` | token 計算值變 `rgb(255, 0, 0)`，thumb 仍 (162,162,162)，對紅 ΔE 105.43 | 失敗，RED-correct |
| 品牌色 copper | 依修正 3 刪除，未量 | — |
| hover（記錄） | 靜止 (162) → hover (126)，ΔE 13.82 | 記錄 |
| forced-colors thumb 可見？（記錄） | `-drag` rect 中心像素 (255,255,255) = 底色；文件 embed rail 中心像素也是 (255) → 兩者都看不見（同第一輪） | 記錄，follow-up 不擋本批 |

## 需 Planner 確認（不是方法缺陷，但要讓你看見）

- **#78 判定 6 的子項 3 今天就相符**（距看得見的內框下緣 1px，文件 0，差 1，在修正 4 的 ±1px 內）。第一輪已經回報這個數字，修正 4 是在知道它的情況下訂的；方法本文對判定 2／3 也寫明「相符的那項就是保護項，RED run 要如實標出，不算方法錯」。本輪照這條規則把它列為保護項，而判定 6 因子項 1、5 仍失敗，整體失敗。如果你希望水平方向的距下緣也必須是 RED（例如改成「差 ≤ 0」或以 B.bottom 為基準），請改方法再跑；本輪沒有自行調整。
- 垂直方向的判定 3 失敗（3 對 0）、水平方向相符（1 對 0），差別來自現況 CSS：垂直 `-drag` 有 3px 的右側間距，水平 `-drag` 貼底而 `.z-biglistbox-outer` 溢出 1px。修好後兩者應都是 0。

RED4-R2: RED-correct
