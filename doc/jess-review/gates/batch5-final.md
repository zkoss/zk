«8085 in use by batch5 final gate» — 2026-10-07 17:21–17:36 Fable Verifier 使用中（判定 + 回歸）；17:36 已跑完，8085 已釋出（server 仍在跑，PID 20251）。

# Gate：第五批最終判定，cascader／chosenbox／combobox 清單與晶片（#8 #12 #13 #16 #17）

- **判定：PASS**（2026-10-07，post-fix）。五個 issue 的判定檢查（RED 時全部失敗）現在全部通過；保護項全部通過；回歸只有本批預期的 chosenbox 晶片截圖（3 張）與兩張早已知的 tablet baseline（calendar、slider，與第四批相同、非本批）。
- **Verifier：** Fable，全新 context。沒有看任何 diff、沒有讀 `combobox.css`／`cascader.css`／`chosenbox.css` 的內容（只用 `cmp` 比對 8085 回傳的 `.css.dsp` 與 codegen 檔是否相同，見下）；沒有讀 `batch5-gen-brief.md`。只讀了驗證計畫「第五批」一節到檔尾（含「裁示」「第五批 RED run 結果」的方法修正 1–5、「D48-A」）、`batch5-red.md`、`batch4-final.md`（格式）與 RED 腳本。
- **方法：** 照 jess-review-verification-plan.md（過程資料已清除）「第五批」定稿版（RED 方法修正 1–5 + D48-A）。RED 腳本原樣複製到 batch5-final/（過程資料已清除） 重跑，只改 `require`／輸出檔前綴：`final-lib.js`、`final-red8.js`、`final-red12.js`、`final-red13.js`、`final-red16.js`。另外新寫：`final-red17.js`（D48-A 的新判定：加「從下拉挑選之後」那一幀，像素基準取**同一焦點狀態**的收合幀；非唯讀的反白可見度）、`final-red8-band.js`（RED 的空帶座標 x = 260／277.5／295 命中與點擊）、`final-red8-protect.js`（欄高、cave 捲動、展開、關閉、三層）、`final-copper.js`（#8／#13／#17 的 copper 一次性記錄；#12／#16 的 copper 由原腳本產生）。
- **環境：** 8085，Playwright Chromium，viewport 1280×900、DPR 2，注入 `*{transition:none!important;animation:none!important}`，等 `document.fonts.ready`，量測前游標移到 (2,2)。
- **確認服務的是新 CSS（我重啟了 8085 一次）：** 開始時 8085（PID 66666，17:03:04 起）回傳的 `cascader.css.dsp` 是 2789 bytes = `zkmax/build/resources/main` 的 17:01:49 舊檔，而三個來源檔 17:18:24–17:18:32 才改、codegen 的 `cascader.css.dsp`（2892）、`chosenbox.css.dsp`（3688）、`combo.css.dsp`（28194，combobox 併在這一支）都是 17:18:37 — 也就是舊 server 服務的是修正前的 CSS。依 brief 允許，17:21 kill 66666，用 `tail -f /dev/null | withjdk.sh 17 ./gradlew appRun -PhttpPort=8085 --console=plain`（log 在 scratchpad）重啟，17:22:42 起聽（PID 20251）；`cmp` 比對 8085 回傳的三支 `.css.dsp` 與 codegen 版 **完全相同** 後才開始量。本輪所有判定腳本 17:23–17:26 跑完、回歸 17:26–17:36，全程同一個 server，**沒有被別的 session 中斷**（跑完後 8085 仍是 PID 20251）。
- **沒有修改** 任何 CSS／TS／Java／ZUL／測試檔、沒有改任何 baseline PNG（沒用 `--update-snapshots`，Playwright 輸出都寫到 scratchpad；跑完 `git status` 兩個 repo 都沒有已追蹤的 PNG 被修改）。**沒有建立**暫時頁（三個現成頁面夠用），所以沒有東西要刪。工作樹上另一個 session 的 `.gitignore`／`.mcp.json`／`data-dense-mode.md`／未追蹤 `doc/*.md`、zkcml 的 `lib/spel2js`／`zk85themebuilder/` 沒有碰。

## 總表

| 項目 | 判定檢查 RED → 現在 | 保護項 | 結果 |
|---|---|---|---|
| #8 單欄 hover 色塊到 popup 內緣 | 色塊右緣距內緣 **38px → 0px**；空帶 x = 260／277.5／295 命中 **popup → `LI.z-cascader-item`**；在 x = 277 點一下 **不展開 → 展開（1 → 2 欄）** | 雙欄最後一欄 0px（不變）；popup 寬 322 = Σcave 320 + 2；第一欄 160；欄高 116／80 = 項目高總和 + 8（沒有拉伸）；cave 可捲（`max-height` 280、滾輪 scrollTop 0 → 58）；Escape 後 popup `display:none` | **PASS** |
| #12 晶片字型 | 4 個晶片 rest／hover／focus：`font-style` **italic → normal**，`font-weight` **500 → 400** | 字級 14px、晶片高 28px 不變；刪除鈕中心命中 `I.z-chosenbox-icon z-icon-times`；點了晶片 2 → 1 | **PASS** |
| #13 建立列 | 圖示右緣→文字左緣 **0 → 8px**（rect）／**−0.5 → 9.5px**（ink）；垂直中心差（ink）**1.25 → 0.75px** | icon 左緣 = 選項文字左緣（293 = 293，差 0）；整列 5 點命中都在列內；hover 色塊 277–495 = popup 內緣 | **PASS** |
| #16 說明文字 | color **`rgba(0,0,0,0.87)` → `rgba(0,0,0,0.6)`**（= `--zk-color-on-surface-variant`）；size **13px → 12px**（= `--zk-typescale-body-small-size`）；主標籤仍 `rgba(0,0,0,0.87)`／13px；像素 ΔE(desc, label) **0 → 30.45** | 說明文字對比 rest 5.74／hover 5.44／selected 7.75（≥ 4.5）；列高 57px（RED 56）、兩行都在列內；選取列底色仍是 `oklch(0.87 …)`（看得出選取） | **PASS** |
| #17 唯讀 combobox 反白（D48-A） | 下拉挑 Item 1 之後 **反白像素 0、最差 ΔE 0.91**；三連點 **2304 px → 0、ΔE 27.78 → 0**；Cmd+A **2304 → 0、ΔE 0**（三者 `getSelection()` 都是 `"Item 1"`，依 D48-A 只記錄） | 非唯讀：打 `abc` 可輸入、三連點反白**看得見**（1472 px、ΔE 27.78）；唯讀：點按鈕 popup 開、挑 Item 1 後 value 變；Tab 1 跳就到、`:focus-visible` 真、焦點環 ΔE 77.55 | **PASS** |
| 回歸 | component-theming 107/107、forced-colors 17/17、hit-target 3/3、focus-scan 57 通過／47 skipped、chromium 129/132（3 失敗全是 chosenbox 晶片，預期）、tablet 53/55（calendar、slider，與第四批相同的已知 U） | — | 見「回歸」 |

## #8（D46-A）— 單欄 hover 色塊（`final-red8.json`、`final-red8-band.json`、`final-red8-protect.json`）

預設 cascader（trigger 200px）點開，hover Japan：

| 量項 | RED | 現在 |
|---|---|---|
| popup | 97–297，寬 200（inline `min-width:200px`），border 1px → 內緣 98–296 | 同 |
| `.z-cascader-cave`（單欄） | 寬 **160**（98–258） | 寬 **198**（98–296）= 內緣寬 |
| Japan li | 98–258 | 98–296 |
| 色塊（列頂 +3px 掃描） | 98→258，寬 160，色 (245,245,245) | **98→296，寬 198**，色 (245,245,245) |
| 色塊右緣距 popup 內緣 | **38px** | **0px**；左緣距內緣也是 0 |
| 空帶命中（Japan 列中線，x = 260／277.5／295／295.5） | 都是 `DIV.z-cascader-popup` | **都是 `LI.z-cascader-item`**（`onItem: true`） |
| 在 x = 277 點一下 | cave 數仍 1 | **cave 數 2**（160 + 160，popup 322），popup 仍開 |

截圖 `final-red8-default-A-single-hover.png`：灰色 hover 帶貼到 popup 右邊框。

### 保護項（`final-red8-protect.json`，三個可用的 cascader 都走過）

| 項目 | 量到的值 | 結果 |
|---|---|---|
| 雙欄最後一欄 hover 色塊（Kyoto） | 258→418 = popup 內緣 418，差 **0**（RED 0） | 通過 |
| 雙欄 popup 寬 = Σcave + 2 | 322 = 160 + 160 + 2（widget 0、2、3 都一樣） | 通過 |
| 第一欄 160px | 雙欄時 caveWidths [160, 160]；預選 Japan/Kyoto 的 widget 3 一開就是 [160, 160] | 通過 |
| 欄高沒有拉伸 | 欄高 [116, 80]、項目高總和 [108, 72]（各 + 8px padding）；第二欄 80 沒有被拉到 116 | 通過 |
| 高 cave 可捲 | 頁上沒有任何 cave 自己會捲（`scrollHeight = clientHeight`，`max-height: 280px`、`overflow-y: auto` 都在）；頁內注入 inline `max-height: 58px` 探針後滾輪 120 → `scrollTop` 0 → 58（`final-red8-protect-forced-scroll.png`） | 通過（探針，不改 repo） |
| 展開行為 | 點 Japan：cave 1 → 2、popup 200 → 322（widget 0）／220 → 322（widget 2） | 通過 |
| 關閉 | Escape 後 popup `display:none`、`visible:false`（三個 widget） | 通過 |
| 三層 | 預覽頁的 model 最深兩層（走到第二欄就沒有帶箭頭的項目），**無法量**，與 RED 相同 | 不適用 |
| 預選列（記錄） | `z-cascader-selected` 仍沒有背景色塊、只有藍字，與 RED 相同（計畫已刪除「選取色塊」判定） | 記錄 |

**記錄：** forced-colors 下 hover 色塊仍看不見（列頂 +3px 無非白像素，單欄／雙欄同），與 RED 相同（看板 follow-up）；`data-brand="copper"` 下單欄 hover 色塊 98→296、差 0px。

## #12 — 晶片字型（`final-red12.json`）

`chosenbox.zul` 上 4 個 `.z-chosenbox-item-content`（Default 的 Apple／Banana、Disabled 的 Apple／Banana），rest、hover（游標在第一個晶片文字上）、focus（點第一個 chosenbox 的 input，root 得到 `z-chosenbox-focus`）：

| 狀態 | font-style | font-weight | font-size | line-height | 晶片高 |
|---|---|---|---|---|---|
| RED（三種狀態、4 個晶片） | italic | 500 | 14px | 20px | 28px |
| **現在**（三種狀態、4 個晶片） | **normal** | **400** | 14px | 20px | 28px |

italic 來源追蹤：從晶片往上走，`DIV.z-chosenbox-item-content` 的父元素已經不是 italic（RED 時要走到 root `<i class="z-chosenbox">`）。token 不變：`label-large` 14px／500、`body-medium` weight 400。

**保護項：** 字級 14px、晶片高 28px（4 個都一樣、三種狀態不變）；刪除鈕中心 `elementFromPoint` → `I.z-chosenbox-icon z-icon-times`（兩個晶片都在鈕內）；真的點下去晶片 2 → 1。全部通過。
**記錄：** copper 下 normal／400／14px；forced-colors 下 normal／400、color `rgb(0,0,0)`。

## #13 — 建立新晶片那一列（`final-red13.json`）

「createMessage + noResultsText」那個 creatable chosenbox（uuid `cBaMz`），先點 input 量選項列參考（popup 276–496，`.z-chosenbox-option` 左緣 277、文字左緣 293），再打 `ffff`：

| 判定 | RED | 現在 | 結果 |
|---|---|---|---|
| 1. 圖示右緣→文字左緣 ≥ 8px | rect 0／ink −0.5 | rect **8**（icon 右緣 309 → span 左緣 317）／ink **9.5**（icon ink 右緣 308 → 文字 ink 左緣 317.5） | **通過** |
| 2. 字形 ink 垂直中心差 ≤ 1px | 1.25 | icon ink 462.5–475.5 中心 469；文字 ink 463–473.5 中心 468.25 → **0.75**（rect 法 1.45，僅記錄；方法以 ink 為準） | **通過** |

列的 DOM 不變（`<i class="z-chosenbox-icon z-chosenbox-create z-icon-plus-square">` + `<span>`，計算 `display:block`）；第一個 creatable（「Add new」）的 gap 也是 8。

**保護項：** icon 左緣 293 = 選項文字左緣 293（差 **0**，與 RED 相同，加了間距後沒有位移）；該列 x = 279／331.5／386／440.5／493（列中 y）`elementFromPoint` 全在列內（DIV 或其 SPAN）；hover 色塊（列頂 +2px）277→495 = popup 內緣 277–495，色 (245,245,245)。全部通過。點該列後晶片仍是 0 個（預覽頁沒有伺服端 listener，與 RED 相同，方法已改為只做命中測試）。
**記錄：** forced-colors 下 plus 圖示 432 個 ink 像素（看得見）；copper 下 gap 8。

## #16 — combobox 說明文字（`final-red16.json`）

「With description」combobox，兩個 `.z-comboitem`（說明是 `.z-comboitem-inner`，主標籤是 `.z-comboitem-text` 的裸文字節點）：

| 元素 | color | font-size | weight | line-height |
|---|---|---|---|---|
| 主標籤（兩項，RED → 現在） | `rgba(0,0,0,0.87)` → `rgba(0,0,0,0.87)` | 13px → 13px | 400 | 20px |
| 說明（兩項，RED → 現在） | `rgba(0,0,0,0.87)` → **`rgba(0,0,0,0.6)`** | 13px → **12px** | 400 | 20px |

token：`--zk-color-on-surface-variant` = `#0009` → `rgba(0,0,0,0.6)` ✓；`--zk-typescale-body-small-size` 12px ✓。像素：主標籤最暗 (33,33,33)、說明最暗 (102,102,102) → **ΔE 30.45**（RED 0，門檻 ≥ 15）。

**保護項（說明文字計算色疊在列底色上）：**

| 狀態 | 列底色 | 說明前景 | 對比 | 主標籤對比 | 列高 | 兩行在列內 |
|---|---|---|---|---|---|---|
| rest | (255,255,255) | (102,102,102) | **5.74** | 16.1 | 57 | 是 |
| hover（`rgba(0,0,0,0.08)`） | (235,235,235) | (94,94,94) | **5.44** | 13.98 | 57 | 是 |
| selected（`oklch(0.87 …)`） | (200,213,234) | (48,58,73)（說明色 `oklch(0.235 … / 0.85)`） | **7.75** | 11.28 | 57 | 是 |

列高 57（RED 56，不低於）；選取列底色與 RED 相同，截圖 `final-red16-default-selected.png` 看得出選取。
**記錄：** copper 不改 `--zk-color-on-surface-variant`（仍 `#0009`），說明文字 `rgba(0,0,0,0.6)`；forced-colors 下兩段文字都是 `rgb(0,0,0)`、ΔE 0（系統色下只剩字級分層）。

## #17 — 唯讀 combobox 的反白（D48-A，`final-red17.json`）

`readonly="true"` combobox value 空，先點按鈕挑 Item 1。像素帶 = input rect 內縮（x +1／−1，y +2／−2）；基準 = **同一焦點狀態**、`setSelectionRange(0,0)` 收合的幀（挑選後 input 是聚焦的，所以三個判定都用「已聚焦、收合」基準；另外也拍了「失焦、收合」備用，沒用到）。

| 操作 | `getSelection()`（記錄） | `selectionStart–End` | 反白像素（> 2 ΔE 的個數）／最差 ΔE | RED | 結果 |
|---|---|---|---|---|---|
| 從下拉挑 Item 1 之後 | `"Item 1"` | 0–6 | **0 px／0.91**（最差在 (580.5, 221.5) 焦點環邊緣的反鋸齒，(54,109,205) vs (55,111,208)） | （未量） | **通過** |
| 三連點 | `"Item 1"` | 0–6 | **0 px／0** | 2304 px／27.78 | **通過** |
| Cmd+A（Meta+A） | `"Item 1"` | 0–6 | **0 px／0** | 2304 px／27.78 | **通過** |
| Control+A（記錄） | `""` | 0–0 | 0／0 | 0 | 記錄（macOS 游標移動） |

截圖 `final-red17-default-ro-afterpick.png`／`-triple.png`／`-metaA.png`：文字 "Item 1" 沒有藍色反白。

**保護項：**

| 項目 | 量到的值 | 結果 |
|---|---|---|
| 非唯讀 combobox 可輸入 | 打 `abc` → value `abc` | 通過 |
| 非唯讀 combobox 反白**看得見**（D48-A 新增） | 三連點選到 `abc`（0–3），反白像素 **1472 px、最差 ΔE 27.78**，色 (179,215,254)（`final-red17-default-rw-triple.png`） | 通過 |
| 唯讀仍可開下拉並挑選 | 點按鈕 popup `display:block`、兩個項目；點 Item 1 → value `Item 1` | 通過 |
| Tab 可聚焦、焦點環還在 | 從 Default 的 input 按 Tab **1 跳**到唯讀 input，`:focus-visible` 真；root 外框像素 focus (55,111,208) vs blur 白 → ΔE **77.55**（RED 77.55） | 通過 |

**記錄：** forced-colors 下三個操作都仍有反白（2304 px，色 (55,51,109)），非唯讀 1472 px，焦點環 ΔE 100 — 系統高對比模式本來就不理會作者的 `::selection` 顏色，與 RED 一致；copper 下三連點反白 0 px、ΔE 0。

## 回歸

`PREVIEW_URL=http://127.0.0.1:8085`，從 `zkpreview/` 執行，`--output` 指到 scratchpad，沒有寫進 repo，沒有用 `--update-snapshots`。17:26:39–17:32:28 依序跑完，8085 全程沒有中斷。Log 在 batch5-final/pw-logs/（過程資料已清除）。

| Project | 結果 | 第四批結束時 |
|---|---|---|
| `component-theming` | **107/107 通過** | 107/107 |
| `forced-colors` | **17/17 通過** | 17/17 |
| `hit-target` | **3/3 通過** | 3/3 |
| `focus-scan` | **57 通過、47 skipped、0 失敗** | 57／48 skipped |
| `chromium` | **129 通過、3 失敗**（`chosenbox › hover`、`chosenbox › focus`、`chosenbox dropdown › chip focus`）；132 個（第四批 130，多出的 2 個是別人後來加的 case） | 130/130 |
| `tablet` | **53 通過、2 失敗**（`tablet-calendar › gallery`、`tablet-slider › gallery`）；`tablet-combobox › gallery` 與兩個 `tablet-combobox-sheet` 通過 | 52/55（含 biglistbox，當時 E） |

另外補跑 `gallery -g "chosenbox|combobox|cascader"`：cascader、chosenbox gallery 都在容差內通過（這個 project 用 `maxDiffPixelRatio: 0.01`，晶片字型的差異落在容差內；combobox 沒有 gallery case）。

### baseline 差異分類（diff 圖在 batch5-final/（過程資料已清除） `diff-*.png`、`actual-*.png`）

| 截圖（baseline） | 差異像素 | 區域 | 分類 |
|---|---|---|---|
| `chosenbox-hover.png` | 546（ratio 0.03；`padShot` 容差 20） | 只有 Apple／Banana 兩個晶片的文字字形（italic/500 → normal/400），其他 0 | **預期變更**（#12） |
| `chosenbox-focus.png` | 546 | 同上 | **預期變更**（#12） |
| `chosenbox-chip-focus.png` | 545–548（三次重拍） | 同上（晶片文字） | **預期變更**（#12） |
| `calendar-tablet.png` | 900 | 「With Datebox」區兩個 datebox 的按鈕邊框（與第四批記錄的 350/1088 同一區、同一原因 `778b5a809f`），calendar.zul 沒有本批元件 | **非本批**（已知 U，第四批已記） |
| `slider-tablet.png` | 1726 | 兩個圓形 knob slider 的弧（與第四批同一原因 `64f1c07b9d`） | **非本批**（已知 U，第四批已記） |

**預期會變而沒有變的：** `combobox-hover/focus/dropdown`、`combobox-tablet`、`cascader-hover/focus`、`chosenbox-dropdown` 都通過 — #8 的色塊、#16 的說明文字、#17 的 `::selection` 都只在「popup 開著且 hover／選取」或「有說明文字的那個 combobox」才看得到，而那些截圖 case 拍的不是這些狀態，所以 baseline 不動是合理的。要重生的 baseline 只有 **chosenbox 三張**（hover、focus、chip-focus），都是晶片文字；沒有 unexpected。

## 範圍檢查（`git status`，跑完後）

**zk（排除 `doc/jess-review/gates/` 下的未追蹤檔）：** M `zul/.../inp/css/combobox.css`（17:18:32，本批）；M `doc/jess-review/jess-review-verification-plan.md`（Planner 的方法文件）；M `.gitignore`、`.mcp.json`（09-17）、`doc/spec/data-dense-mode.md`（10-05）、未追蹤 `doc/css-variable-*`、`doc/data-uri-*`、`doc/debug-console-plan.md`、`doc/dsp-removal-iceblue-coexistence.md`、`doc/theme-pack-retirement-*`、`logs/` — 都早於本批、與本批無關。**brief 提到的另一個 session 的 inputgroup／rating／forced-colors PNG 改動在本輪開始時已經不在工作樹上**（應該已被那個 session commit），所以回歸裡沒有 inputgroup／rating 的失敗需要歸因。
**zkcml：** M `zkmax/.../inp/css/cascader.css`、`chosenbox.css`（17:18:24，本批）；M `.gitignore`、`lib/spel2js/package-lock.json`、未追蹤 `zk85themebuilder/` — 既有、與本批無關。
**PNG：** 兩個 repo 跑前跑後都沒有任何已追蹤的 PNG 被修改。

## 方法上的備註

1. **8085 服務舊 CSS 的情形又發生了**（第四批也遇到）：Generator 改完來源、跑了 codegen，但 appRun 的 server 是在那之前啟動的，`build/resources/main` 的副本沒有更新。最終 Verifier 開始前一定要 `cmp` 8085 回傳的 `.css.dsp` 與 codegen 檔；combobox 要比的是 `js/zul/inp/css/combo.css.dsp`（combobox 併在裡面，沒有單獨的 `combobox.css.dsp`）。
2. #17 的「挑選之後」那一幀：挑選後 input 是聚焦的且 0–6 全選（ZK 的行為沒變），所以基準要用「已聚焦、收合」幀；最差 ΔE 0.91 落在 input 自己的焦點邊框反鋸齒上，不是反白。若將來門檻改嚴（< 0.5），要把像素帶再往內縮 1px。
3. #8 單欄時 cave 從 160 變成 198（= popup 內緣寬），雙欄時仍是 160 + 160；「第一欄 160px」這條保護項只在多欄狀態有意義，單欄的 cave 寬現在跟著 popup 走，這正是修正的結果，不是回歸。
4. 三層 cascader 預覽頁沒有，仍然量不到；要驗三層得加預覽 model（看板 follow-up，不擋本批）。
5. `chromium` project 多了 2 個 case（130 → 132），不是本批加的，順便記錄。

取證放在 batch5-final/（過程資料已清除）：`final-*.js`／`final-*.json`／`final-*.png`、`diff-*.png`／`actual-*.png`、`pw-logs/`。

GATE5-FINAL: PASS
