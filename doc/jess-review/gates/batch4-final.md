# Gate：第四批最終判定，biglistbox 捲軸（#31、#78）

- **判定：PASS**（2026-10-07，post-fix）。#31 判定 1–4、也必須成立、保護項全部通過；#78 判定 1、3、5、6 與 token 換色全部通過，保護項全部通過。D40 退路（轉 C）**沒有觸發**。
- **Verifier：** Fable，全新 context。沒有看修正的 diff，沒有讀 `gates/batch4-gen-brief.md` 以外的 Generator 文件（brief 也只為了找暫時頁的定義而 grep 一行，沒有讀內容）；只讀了驗證計畫「第四批」一節到檔尾（含「裁示」「RED run 結果」的方法修正 1–5、「RED run 第二輪」）、`batch4-red-r2.md`、`batch3-final.md`（格式）。
- **方法：** 照 jess-review-verification-plan.md（過程資料已清除）「第四批」定稿版（r2）。RED 第二輪的腳本原樣複製到 batch4-final/（過程資料已清除） 重跑，只改 `require`／輸出檔前綴：`final-lib.js`、`final-doc-scrollbar.js`、`final-red31.js`、`final-red78.js`、`final-protect.js`；文件捲軸的參考檔 `doc-scrollbar.json` 沿用第一輪的那份（複製進來），並由 `final-doc-scrollbar.js` 重量一次確認未變。另外新寫 `final-classify.js`（baseline 差異分群與對照裁圖）。
- **環境：** 8085，Playwright Chromium，viewport 1280×900、DPR 2，注入 no-animation，等 `document.fonts.ready`，量測前游標移到 (2,2)，全部用真的滾輪／滑鼠。
- **確認服務的是新 CSS：** 我開始時 8085（PID 40209，15:47 啟動）服務的 `biglistbox.css.dsp` 是舊的 `zkmax/build/resources/main` 那份（3256 bytes），與 15:51 重新產生的 `zkmax/codegen/.../biglistbox.css.dsp`（2959 bytes）不同。正要重啟時，另一個 session 已經在 15:53 重啟它（PID 60485，15:54:53 起聽）；用 `cmp` 比對 8085 回傳的 `biglistbox.css.dsp` 與 codegen 版 **完全相同**，之後才開始量。本輪所有判定腳本在 15:55–15:56 跑完，都對著這份 CSS。
- **8085 中途被停掉一次、由我重啟一次：** 回歸跑到 `chromium` 第 62 個測試時（約 16:02），8085 被另一個 session 停掉（`ERR_CONNECTION_REFUSED`，lsof 無人監聽、無 appRun 程序），`chromium` 69 失敗、`tablet` 55 失敗全部是連線被拒，不是像素差異。依 brief 允許，我用和原本相同的方式重啟（`tail -f /dev/null | withjdk.sh 17 ./gradlew appRun -PhttpPort=8085 --console=plain`，log 在 scratchpad），16:06:25 起聽（PID 94453），再次 `cmp` 確認服務的 CSS 等於 codegen 版，然後重跑 `focus-scan`、`chromium`、`tablet`。沒有開其他 port。
- **沒有修改** 任何 CSS／TS／Java／ZUL／測試檔、沒有改任何 baseline PNG（沒用 `--update-snapshots`，Playwright 輸出都寫到 scratchpad）。暫時頁 `zkpreview/src/main/webapp/web/biglistbox-fits.zul`（`FakerMatrixModel(2, 3)`、200px 高、沿用 biglistbox.zul 的 template；實際渲染 2 欄 × 3 列，兩個方向的 `-drag` 都 `display:none`）量完已刪除，gretty 同步到 `build/inplaceWebapp/web/` 的那份也刪了，URL 回 404；內容留存在 `batch4-final/biglistbox-fits.zul.copy`。

## 總表

| 項目 | 判定檢查 | 也必須成立／保護項 | 結果 |
|---|---|---|---|
| 文件捲軸期望值 | 與第一輪 `doc-scrollbar.json` 完全相同（顏色 ΔE 0、寬 8、距右 0、圓角、token `#0000001f`） | — | 參考值未變 |
| #31 | 判定 1：3 個 widget 都**不**命中 wscroll；判定 2：0／0；判定 3（暫時頁）：0；判定 4：0／0 | `vflex=min`、forced-colors 下判定 1 仍通過；保護項全部通過 | **PASS** |
| #78 | 判定 1：ΔE 0／0；判定 3：0 對 0；判定 5：槽 ΔE 0；判定 6（水平 1–5）全過；token 換色：thumb 變 (255,0,0) | 判定 2、4（保護項）通過；放得下的 widget 無 thumb；滾輪／拖曳／夾制通過 | **PASS** |
| D40 退路 | 判定 1–5 全部在容許範圍內 → **#78 做得到，不轉 C** | — | 不觸發 |
| 回歸 | chromium 130/130、component-theming 107/107、forced-colors 17/17、focus-scan 57 通過、hit-target 3/3、tablet 52/55 | tablet 3 失敗：1 E（biglistbox）、2 U（slider、calendar，原因都可追到別的 commit，見下） | 見「回歸」 |

## 文件捲軸的期望值（`final-doc-scrollbar.json`，對照第一輪 `doc-scrollbar.json`）

量法同 RED：`scrollbar.zul` 第 2 個 grid（`ca:data-embedscrollbar="true"`，embedded 模式）的 `.z-scrollbar-vertical-embed`，游標在 (2,2)。容器 `.z-grid-body` 33–271 × 449–594，底色 (255,255,255)。

| 量項 | 垂直（判定依據） | 第一輪 | 水平（僅記錄） |
|---|---|---|---|
| thumb 顏色 | (224,224,224) | (224,224,224)，ΔE 0 | (224,224,224) |
| 寬度／高度 | 8px | 8 | 8px |
| 距內框右緣／下緣 | 0 | 0 | 0 |
| 圓角 | 兩端 10.82、中心 1.06 → 是 | 是 | 兩端 10.82、中心 3.17（同 RED，反鋸齒邊，`ok:false` 僅記錄） |
| 位置 | 263–271 × 449–488 | 同 | 33–155 × 586–594 |

`unchangedVsFirstRun.all = true`。

## #31（D39-A，修正帶）

幾何（viewport 座標；`R` = `.z-biglistbox-outer` 右緣 = box.r − 1）與 RED r2 完全相同：

| widget | box | H | B | R | 欄寬合計右緣 |
|---|---|---|---|---|---|
| `#stripedBiglist` | 32–1248 × 187–387 | 188–227 | 227–388 | 1247 | 683 |
| `#biglist`（預設 MultipleColumn） | 32–1248 × 400–900 | 401–440 | 440–901 | 1247 | 4063 |
| 暫時頁 `fitsBiglist` | 32–1248 × 100–300 | 101–140 | 140–301 | 1247 | 293 |

底色（欄外空白中位數）三個 widget 都是 (255,255,255)；判定 2 的參考色（同 y、x∈[R−34, R−21]）也是 (255,255,255)。

### 判定檢查

| 檢查 | stripedBiglist | biglist | fitsBiglist | RED r2 | 結果 |
|---|---|---|---|---|---|
| 1. `elementFromPoint(R−7, H 中心)` | `.z-biglistbox-head`（非 wscroll） | 非 wscroll | `.z-biglistbox-head` | 都命中 `-wscroll-vertical` | **通過** |
| 2. 表頭帶（修正帶） | **0**，(1233, 190)，rgb 255 | （方法排除；記錄 56.81 = 表頭文字） | **0**，(1233, 103)，255 | 4.51（槽） | **通過** |
| 3. 右側整條（方法只對暫時頁） | （記錄 10.82，(1244, 274)，rgb 224 = **thumb 本身**，不是槽） | （記錄 87.26 = 列文字） | **0**，(1233, 101)，255 | 9.06（槽交疊） | **通過** |
| 4. 底帶 y∈[box.b−15, box.b−2]、x 欄外 | **0**，(689, 372)，255 | （記錄 0） | **0**，(299, 285)，255 | 4.51（槽） | **通過** |

截圖 `final-red31-stripedBiglist.png`：thumb 從表頭下緣開始、右側沒有槽、底部沒有水平灰帶；`final-red31-fitsBiglist.png`：右側與底部整條空白。

### 也必須成立

| 項目 | 量到的值 | 結果 |
|---|---|---|
| `#biglist` 切 vflex/hflex = min 後判定 1、2 | 命中 `.z-biglistbox-header z-biglistbox-sort`（非 wscroll）；判定 2 記錄 0（R 1203、H 490–529；判定 3 記錄 10.82 = 224 的 thumb，非槽） | **通過** |
| `forcedColors: 'active'` 下判定 1 | 3 個 widget 都不命中 wscroll（`wscrollCls` 為 null） | **通過** |

### 保護項（`final-protect.json`，真實滾輪／滑鼠）

| 項目 | 量到的值 | 結果 |
|---|---|---|
| 該有的軌道還在：striped 垂直 thumb | ΔE 10.82 ≥ 10；top 227.5 ≥ B.top 227 | 通過 |
| 該有的軌道還在：biglist 水平 thumb | left 34 ≥ B.left 33 | 通過 |
| 還能捲（垂直，`#stripedBiglist`） | 滾輪：第一列 `y = 0` → `y = 1`，`_currentY` 0→1，thumb 40.5→41.5（widget 相對）；捲到底 thumb 41.5–88.5 在 B 40–201 內，`-drag` rect (1239,41,1247,89) | 通過 |
| 還能捲（垂直，`#biglist` 切 MultipleRow 10×100） | `y = 0` → `y = 2`，thumb 440.5→442.5；捲到底 `_currentY` 88，thumb 528.5–576 在 B 440–901 內 | 通過 |
| 還能捲（水平，`#biglist` 預設） | `mouse.wheel(120,0)`：`Header x = 0` → `x = 2`，thumb 34→35；捲到底 `_currentX` 91，thumb 124–171.5，右緣 ≤ R 1247、左緣 ≥ B.left 33 | 通過 |
| 拖得動 | 按 `.z-biglistbox-wscroll-body` 中心往下 40px：`_currentY` 0→1（striped）、0→18（biglist/MultipleRow），列有跟著捲 | 通過 |
| 表頭不動（以 widget 為基準） | 滾輪、捲到底、拖曳前後 H 相對 widget 的位置相同（viewport 座標也相同） | 通過 |
| 表頭可以點（`#biglist`，(R−20, H 中心)） | sorticon `""` → `z-icon-caret-up` | 通過 |
| #78 保護：放得下的 widget thumb 數 0 | 暫時頁兩條軌道都沒有 ΔE ≥ 10 且厚度 ≥ 3 device px 的連續段 | 通過 |

注意：ΔE 10.82 是 thumb 色 (224) 對白底的差，剛好過「≥ 10」的門檻 —— 這是 D40-A 選用 `outline-variant` 的必然結果（文件捲軸的靜止 rail 對白底也是 10.82），不是量測不穩。

## #78（D40-A，修正 3–5）

`--zk-color-outline-variant` 計算值 `#0000001f` → 疊白底 (224,224,224)。

### `#stripedBiglist` 垂直 thumb（`final-red78-striped.png`）

thumb 1239–1247 × 227.5–274.5。

| 檢查 | 修後 | 文件 | RED r2 | 結果 |
|---|---|---|---|---|
| 1. 顏色（判定） | (224,224,224)；對文件 ΔE **0**；對 outline-variant **0** | (224,224,224) | 22.56 | **通過** |
| 2. 寬度（保護項） | 8 | 8 | 8 | 通過 |
| 3. 距內框右緣（判定） | **0** | 0 | 3 | **通過** |
| 4. 圓角（保護項） | 兩端 10.82／10.82、中心 0 → 是 | 是 | 是 | 通過 |
| 5. 沒有槽（判定；thumb 下方 2–16px，210 px²） | 最差 ΔE **0**，(1233, 276.5)，rgb 255 | — | 4.51 | **通過** |

### `#biglist` 水平 thumb（判定 6 = 1–5 的水平版，`final-red78-biglist.png`）

thumb 34–80.5 × 891–899；看得見的內框下緣 = box.b − 1 = 899；B.bottom = 901。

| 檢查 | 修後 | RED r2 | 結果 |
|---|---|---|---|
| 1. 顏色 | (224,224,224)，ΔE 0／0 | 22.56 | 通過 |
| 2. 高度（保護項） | 8 = 8 | 8 | 通過 |
| 3. 距看得見的內框下緣（修正 4，±1px） | 899 − 899 = **0**，文件 0（對 B.bottom 是 2，僅記錄） | 1（已相符） | 通過 |
| 4. 圓角（保護項） | 左端 10.82／右端 7.36、中心 0 → 是 | 是 | 通過 |
| 5. 沒有槽（thumb 右側 2–16px） | ΔE 0，(82.5, 885)，255 | 4.51 | 通過 |

判定 6 整體：**通過**。

### token 換色與只記錄項

| 項目 | 量到的值 | 結果 |
|---|---|---|
| `:root{--zk-color-outline-variant: rgb(255,0,0)}`（判定） | token 計算值 `rgb(255, 0, 0)`，thumb 中心變 (255,0,0)，ΔE **0** | **通過**（沒有寫死顏色） |
| 品牌色 copper | 依修正 3 刪除，未量 | — |
| hover（只記錄） | 靜止 (224,224,224) → hover (196,196,196)，ΔE 10.02（RED 時 162→126，ΔE 13.82） | 記錄，不判定 |
| forced-colors thumb 可見？（只記錄） | `-drag` rect (1239,227)–(1247,275) 中心像素 (255,255,255) = 底色，**看不見**；文件 embed rail 中心像素也是 (255) → 兩者都看不見，與 RED 相同 | 記錄，follow-up 不擋本批 |

### D40 退路的判斷

判定 1（顏色）、2（寬度）、3（距邊緣）、4（圓角）、5（無槽）在垂直與水平兩個方向都在容許範圍內，而且是第一輪 Generator 就達到（Generator 輪數我沒有查，也不需要：結果已相符）。**#78 做得到，不觸發「轉 C」**；不存在「原因出在 WScroll 幾何算法」的情形可供判斷。是否動到 `Biglistbox.ts`／`WScroll.ts`，由 Planner 看 diff 確認（最終 Verifier 不看 diff）；從行為面看，thumb 的大小（48px）、位置算法、捲到底的夾制與 RED 時完全相同（`-drag` rect 同為 48px 高、`_currentY` 終點 1／88 不變），沒有行為改變的跡象。

## 回歸

`PREVIEW_URL=http://127.0.0.1:8085`，從 `zkpreview/` 執行，`--output` 指到 scratchpad，沒有寫進 repo，沒有用 `--update-snapshots`，沒有跑 `forced-colors-gallery`。

| Project | 第一輪（15:56–16:04） | 重跑（16:06–16:10，我重啟 8085 之後） | 第三批結束時 |
|---|---|---|---|
| `component-theming` | **107/107 通過** | （不需重跑；跑時 8085 在線） | 107/107 |
| `forced-colors` | **17/17 通過** | （同上） | 17/17 |
| `focus-scan` | 56 通過、48 skipped、1 失敗（`listbox`：等 `.z-page` 15s 逾時，發生在 8085 被停掉前約 1–2 分鐘） | **57 通過、48 skipped、0 失敗** | 57 通過、47 skipped（多出的 1 個 skipped 是另一個 session 的暫時頁 `tmp-cols2.zul`，當時還在 `web/`） |
| `hit-target` | **3/3 通過** | — | 3/3 |
| `chromium` | 61 通過、69 失敗（全部 `ERR_CONNECTION_REFUSED`） | **130/130 通過** | 130/130 |
| `tablet` | 55 失敗（全部 `ERR_CONNECTION_REFUSED`） | **52 通過、3 失敗**：`tablet-biglistbox › gallery`、`tablet-slider › gallery`、`tablet-calendar › gallery` | 55/55 |

`chromium` 沒有 biglistbox 的截圖 case（`screenshot.spec.ts` 不含 biglistbox；它只在 `render-smoke`、`component-theming`、`tablet` 裡），所以方法裡「biglistbox 相關 baseline 預期會變」只落在 `tablet-biglistbox`。

差異逐區分群（任一色版差 > 8 的像素，8px 格子連通；`final-classify.json`，對照裁圖 `batch4-final/diff-tablet_*.png`）：

| 截圖 | Playwright 差異像素 | 我的 >8 像素 | 區域 | 分類 |
|---|---|---|---|---|
| `biglistbox-tablet.png` | 1726 | 11674 | 2 區。逐像素分區：第一個 biglistbox 右側 14px 捲軸道 1782 px、底部 14px 帶 6002 px、第二個 biglistbox 右側捲軸道 3890 px，**捲軸道／底帶之外 0 px**。主要變化 242→255（槽消失，10317 px）、213→224／163→224（thumb 改色，955 px）、163→255／255→224（thumb 位置從表頭區移到 body 區，264 px） | **E** |
| `slider-tablet.png` | 900 | 4173 | 2 區，x 38–217 與 264–441、y 936–1001：兩個圓形（arc）slider 的弧，baseline 是被裁掉的部分弧，實際是完整的圓弧 | **U**（不在 biglistbox 範圍；原因可追到 HEAD~1 `64f1c07b9d`「give the vertical slider a default height and size the knob svg to its container」，14:38，只改 `slider.css`、沒有重生 `slider-tablet.png`；第三批 gates 在 14:11 commit，所以第三批結束時 tablet 55/55 是在這個 commit 之前量的） |
| `calendar-tablet.png` | 350 | 1088 | 2 區，x 130–185 與 514–569、各 50px 高，y 1425–1474 與 1914–1963：`calendar.zul`「With Datebox」區裡兩個 datebox 的按鈕邊框位置移動（255→196） | **U**（不在 biglistbox 範圍；`calendar.zul` 沒有 biglistbox。原因可追到另一個 session 在本輪進行中 16:08 commit 的 `778b5a809f`「let datebox and timebox follow cols by dropping field-sizing: content」：datebox 寬度改為跟著 `cols`，該 commit 重生了 datebox／timebox 自己的 baseline，但沒有重生含 datebox 的 `calendar-tablet.png`） |

沒有 D。兩個 U 都有明確的、與 biglistbox 無關的來源 commit，但依 brief 的規則（不在 biglistbox 範圍的失敗一律 U）如實標 U，交 Planner 處理（重生這兩張 tablet baseline，走「前置工作」的分類／確認流程）。

## 範圍檢查（`git status`，跑完後）

**zk（working tree，排除 `doc/jess-review/gates/` 下的未追蹤檔）：**

| 檔案 | 狀態 | mtime | 歸屬 |
|---|---|---|---|
| `doc/contracts/biglistbox.md` | M | 10-07 15:52 | brief 清單內 |
| `.claude/skills/zk-component-rules/components/biglistbox.md` | M | 10-07 15:52 | brief 清單內 |
| `doc/jess-review/jess-review-verification-plan.md` | M | 10-07 15:51 | Planner 的方法文件（本批 r2 定稿），不是 Generator 的改動 |
| `.gitignore`、`.mcp.json` | M | 09-17 | **清單外**，早於本批，與本批無關（既有的本機改動） |
| `doc/spec/data-dense-mode.md` | M | 10-05 | **清單外**，早於本批，與本批無關 |
| `zul/.../inp/css/datebox.css`、`timebox.css`、`zkpreview/doc/screenshots/datebox-forced-colors.png`、`timebox-forced-colors.png`、`screenshot.spec.ts`、`zktest/.../config.properties`、`B110-ZK-6112-DateTimeboxCols*` | 本輪開始時是 M／??，**16:08 已被另一個 session commit 成 `778b5a809f`**，現在 working tree 乾淨 | 15:54–16:07 | 另一個 session（datebox/timebox），依 brief 註記、不計入 |
| 未追蹤：`doc/css-variable-*`、`doc/data-uri-performance-claims.md`、`doc/debug-console-plan.md`、`doc/dsp-removal-iceblue-coexistence.md`、`doc/theme-pack-retirement-*.md`、`logs/` | ?? | — | 既有，與本批無關 |
| `zkpreview/src/main/webapp/web/pv/datebox-content.zul` | M（寫完報告做最後檢查時才出現） | 10-07 16:1x | 另一個 session（datebox）仍在進行中的改動，不是我改的，依 brief 註記、不計入 |

**zkcml：**

| 檔案 | 狀態 | mtime | 歸屬 |
|---|---|---|---|
| `zkmax/src/main/resources/web/js/zkmax/big/css/biglistbox.css` | M（35 行變動） | 10-07 15:51 | brief 清單內 |
| `.gitignore`、`lib/spel2js/package-lock.json` | M | 09-09／09-10 | **清單外**，早於本批，與本批無關 |
| `zk85themebuilder/` | ?? | — | 既有，與本批無關 |

**PNG：** 兩個 repo 跑前跑後都 **沒有任何已追蹤的 PNG 被修改**（`git status` 的 PNG 只有 `doc/jess-review/gates/` 下的取證檔）。本輪開始時看到的 `datebox-forced-colors.png`／`timebox-forced-colors.png` 是另一個 session 在 16:07:08–09 寫的（我的 `chromium` 重跑 16:07:12 才開始，`forced-colors` 專案不寫 baseline），16:08 已隨 `778b5a809f` commit；不是我寫的，所以沒有 `git checkout` 還原。

## 方法上的備註

1. **「不看 diff」與「確認服務新 CSS」有一點張力。** 要確認 8085 服務的是新 CSS，我比對了 8085 回傳的 `biglistbox.css.dsp` 與 codegen 檔（`cmp`），過程中看到了編譯後的 CSS 內容（不是 git diff，也不是 Generator 的推理）。判定本身仍然只用像素與命中測試，沒有讀任何 CSS 屬性。
2. **8085 是共用資源，而且本輪被另一個 session 停掉／重啟各一次。** 第一輪回歸的 `chromium`／`tablet` 全部因連線被拒失敗，只能靠重跑；建議之後跑回歸前在看板上宣告「8085 使用中」，或讓最終 Verifier 自己啟動並持有 8085。
3. **tablet baseline 的兩個 U 不是本批造成的**，但會在每一次 tablet 回歸裡持續失敗，直到有人重生 `slider-tablet.png`、`calendar-tablet.png`。改了 slider.css／datebox.css 的 commit 應該順手重跑 `tablet` 專案（和第三批備註 6 同一類問題：含該元件的其他預覽頁也要算進回歸範圍）。
4. `#31` 判定 3 對 `#stripedBiglist` 記錄到 10.82 是 thumb 本身（方法本就只對暫時頁判定 3），`vflex=min` 的 `#biglist` 也一樣；若以後要對有 thumb 的 widget 做判定 3，要先把 thumb 的範圍排除。
5. thumb 對白底 ΔE 剛好 10.82，與保護項門檻「≥ 10」只差 0.82；若將來 `--zk-color-outline-variant` 再變淺，「該有的軌道還在」會先倒，到時應改用 `-drag` rect 或把門檻改成「對底色 ΔE ≥ 文件 rail 對底色 ΔE − 1」。

取證放在 batch4-final/（過程資料已清除）：`final-*.json`／`final-*.png`、`final-classify.json`、`diff-tablet_*.png`、`biglistbox-fits.zul.copy`。

GATE4-FINAL: PASS
