# D41 = A（c1）獨立驗證：tablet bottom sheet 的 `inset` 改成 `100% − 100vw` / `100% − 100dvh`

ZK-6112 follow-up 65。驗證日期 2026-10-20。驗證者不是實作者；沒有改任何 tracked 檔案、PNG 或 spec，也沒有 update snapshot。

- 受測：`p3/zk`（branch `gaps-round2`，HEAD `74fd35c271`）與 `p3/zkcml`（HEAD `234cbfa0f`）的未提交修改。
- 伺服器：自己從 `p3/zk/zkpreview` 啟動的 8087（`--no-daemon`）。PID：sh 64330、`tail -f /dev/null` 64331、gradle client 64332、daemon 64368、Jetty 64929。結束時只 kill 64331，appRun 印出 `BUILD SUCCESSFUL`，五個 PID 都消失，8087 沒有在 listen。8085（PID 37900，別人的）沒有碰。
- served CSS 確認：`/zkres/web/80d9e175/zkmax/css/tablet.css.dsp` 含 `calc(100% - 100vw` ×3、`100dvh` ×3；舊值 `inset:auto 10px 0`、`inset:auto 0 0` 各 0 次。
- 瀏覽器：Playwright 1.59.1 Chromium，`deviceScaleFactor: 1`、`hasTouch: true`、`isMobile: true`（另有一組 `isMobile: false` 的 scrollbar 條件），高度 1112。兩種 UA：iPad UA（repo config 的 `IPAD_USER_AGENT`）與 Android UA（`Linux; Android 13; SM-X700 … Chrome/120`）。所有 sheet 都用 `page.touchscreen.tap` 開啟。
- old/new 對照：用 `context.route` 改寫 served `tablet.css.dsp`。每次改寫都斷言命中數：新值 combobox/wheel 字串 ×2、colorbox 字串 ×1，改寫後舊值 ×2 / ×1、`100dvh` ×0；不符就丟例外（全部 run 沒有丟出）。
- 腳本與原始資料：`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/028a7b57-cfcf-44e9-a25e-5d68a4869b5a/scratchpad/c1v/`（`h.cjs`、`fit.cjs`、`matrix.cjs`、`gesture.cjs`、`extra.cjs`、`rtl2.cjs`、`pinchtap.cjs`、`sig.cjs`、`sig2.cjs`、`mut/`；結果在 `out/*.json`、圖在 `out/**.png`、彙總在 `summary.txt`）。

## 總結

| # | 項目 | 判定 |
|---|---|---|
| 1 | diff 審查 | PASS（一處註解用字；`dvh` 不支援時的退化見 §1） |
| 2 | fit 頁幾何不變 | PASS：12/12 rect 相同、PNG 差異 0 px（容忍 0） |
| 3 | overflow 修好 | PASS（LTR，兩種 UA）；RTL overflow 仍看不全；classic scrollbar 條件有退步（§3.4） |
| 4 | 已知代價 | PASS：浮起 89.33 / 556 px、完整可見；456 組 old/new 比對中 0 組「舊可見、新不可見」；matrix 外的 classic scrollbar 條件有 4 組（§3.4） |
| 5 | `signature.zul` | 頁寬、載入後筆跡、桌面 resize：PASS；mobile UA 下載入後 resize：FAIL（筆跡最多偏 −51.5 px，§5） |
| 6 | mutation | PASS |
| 7 | Playwright | tablet 76/76 ×2、forced-colors-tablet 14/14、popup 55 passed + 1 已知失敗、select-tests 79 passed + 1 font-size 已知漂移 |
| 8 | 工作樹 | PASS：前後相同，沒有 staged 內容 |

## 1. diff 審查

`git diff`（兩個 repo）與 untracked 檔案內容與宣稱相符：

- `zkcml/zkmax/.../tablet/_inputs.css`：
  - `.z-combobox-popup`：`inset: auto 10px 0 !important` → `inset: auto calc(100% - 100vw + 10px) calc(100% - 100dvh) 10px !important`。
  - `.z-colorbox-popup`：`inset: auto 0 0 !important` → `inset: auto calc(100% - 100vw) calc(100% - 100dvh) 0 !important`。
- `zkcml/zkmax/.../tablet/_wheel.css`：`.z-datebox-popup:has(.z-calendar-wheel-cave), .z-timebox-popup:has(.z-timebox-wheel-cave)` 與 combobox 相同。
- 其餘 hunk 只有註解。沒有新 token、JS、Java。
- `zk/zkpreview/src/main/webapp/web/signature.zul`：兩個 `<signature width="600px">` 與 `<image width="600px">` 加 `sclass="z-max-w-100"`（`zul/.../utility/_layout.css:305` `.z-max-w-100 { max-width: 100%; }`，既有 utility）。
- `zkpreview/src/test/playwright/tablet.spec.ts`：檔尾新增 describe `tablet-popup-page-wider-than-screen`，1 個 test。
- 新檔 `zkpreview/src/main/webapp/web/pv/popup-tablet-overflow-fixtures.zul`。

`!important` 計數（`count-important.js`，去掉註解後的真實宣告；HEAD 版本用 `git show` 取出到 scratch 再數）：

| | `_inputs.css` | `_wheel.css` | tablet 資料夾合計 |
|---|---|---|---|
| 修改前（HEAD） | 7 | 4 | 11 |
| 修改後 | 7 | 4 | 11 |

- 逐行 diff 只有三行 `inset` 的值不同。colorbox rule 仍是 3 個（`position`、`inset`、`transform`）。

`inset` 順序（top right bottom left），以 computed style 確認：
- colorbox：left `0px`；right 在 fit 頁 `0px`、overflow 600 為 `300px`（= 900 − 600）。
- combobox、wheel：left `10px`；right 在 fit 頁 `10px`、overflow 600 為 `310px`。

註解用字：`_inputs.css` combobox rule 內的註解寫「see the note on the anchor below」，但那段說明在 rule 上方的區塊註解裡。

### `dvh` 的支援與退化

- 支援版本（依公開相容性資料，本次沒有實測其他引擎）：Chrome/Edge 108+、Safari/iOS 15.4+、Firefox 101+、Samsung Internet 21+。
- 不支援的引擎會把整個 `inset` 宣告丟掉（shorthand 內有無效單位，整條宣告無效）。同 rule 的其他宣告照常生效，所以 `position: fixed !important` 還在，`top` 由 widget 的 inline style 決定。
- Chromium 模擬（把 served CSS 的 `100dvh` 改成無效的 `100dxh`，命中 3 次）：
  - fit 頁 834 / 600 的 4 種 sheet 全部在可視區外：colorbox 與 wheel 的 top = 1112（可視區下緣），combobox top 1116。舊 CSS 在同條件下 4/4 完整可見。
  - combobox 寬度縮成 71 px、wheel 寬度縮成 207 px 並靠左。
  - 結論：在不支援 `dvh` 的引擎上，舊行為不會留下來，sheet 在 fit 頁也看不到。
- wheel rule 本來就需要 `:has()`（Chrome 105+、Safari 15.4+、Firefox 121+），所以 wheel 受影響的範圍只有 Chrome 105–107。combobox 與 colorbox 沒有 `:has()`，受影響的是 Chrome < 108、Safari < 15.4。
- 保留舊值當 fallback 需要多寫一條 `inset … !important`，會使 `!important` 增加（E5）。
- ZK 11 的瀏覽器支援範圍沒有查證。

## 2. fit 頁幾何不變

條件：iPad UA、真實 tap、與既有 spec 相同的頁面與 trigger：
- colorbox：`colorbox.zul` 第一個 enabled colorbox。
- combobox：`pv/popup-tablet-fixtures.zul` 第一個 combobox（8 個 item）。
- datebox wheel：`datebox.zul` 的「Seeded value (content-fit)」。
- timebox wheel：`timebox.zul` 第一個 timebox。

old/new 各在新的 context 中開啟、等 rect 連續 3 次相同、拍整個 viewport。

| viewport | colorbox | combobox | datebox wheel | timebox wheel |
|---|---|---|---|---|
| 834×1112 | rect 238–596 × 794–1112 相同；PNG 差 0 px | 10–824 × 812–1112 相同；0 px | 10–824 × 908–1112 相同；0 px | 同 datebox；0 px |
| 1024×1366 | 333–691 × 1048–1366 相同；0 px | 10–1014 × 1066–1366 相同；0 px | 10–1014 × 1162–1366 相同；0 px | 同；0 px |
| 834×1112，`dir=rtl`（載入後切換） | 相同；0 px | 相同；0 px | 相同；0 px | 相同；0 px |

- PNG 比對是逐像素 RGBA 完全相等，容忍值 0。12/12 為 0 px。
- 每組的 `scrollY` 開啟前後都是 0。
- RTL：另外用 `context.route` 把 `dir="rtl"` 注入 served HTML，從載入就是 RTL。probe 頁在 834、600 的 fit 條件下，4 種 sheet 的 rect old/new 相同（`rtl2.json`）。
- RTL 下 `left`/`right` 與 `margin-inline` 的作用：
  - colorbox 的 left/right 是兩個實體值，寬度由 `max-width: fit-content` 限制，`margin-inline: auto` 在 LTR 與 RTL 都平分剩餘空間。fit 頁兩個 inset 都是 0，所以 RTL 也置中（238–596）。
  - combobox 與 wheel 的 `width: auto` 兩側都有值，沒有過度約束，方向不影響。
  - overflow 頁的 RTL 見 §3.3。

## 3. overflow 頁

Repro 頁是自己寫的，暫時放在 `p3/zk/zkpreview/build/inplaceWebapp/web/pv/c1v-sheet-probe.zul`，已刪除，`/pv/…` 與 `/web/pv/…` 都回 404。頁面內容：
- 200px margin，接著 colorbox、combobox（8 item）、datebox、timebox，最後 1500px spacer。
- query 參數：
  - `w`：插入 900 / 901px 寬的 div。
  - `mt`、`tall`：控制 trigger 位置與頁長。
  - `pl`：把 trigger 右移。
  - `box=scroll|transform`：把 trigger 放進 `overflow:auto` 或 `transform:translateZ(0); overflow:hidden` 的 400×300 容器。

判定方式：
- 「完整可見」= sheet rect 在 `visualViewport`（offsetLeft/Top + width/height）內，誤差 ±1px。
- 另外記錄：下緣與可視區下緣的差、`scrollY` 開啟前後、trigger 是否在可視區。

### 3.1 基本條件（scale 1）

| UA | 寬（頁寬） | 舊 CSS | 新 CSS（4 種 sheet） |
|---|---|---|---|
| iPad | 834（901） | 4/4 不完整：低 90px；可見比例 colorbox 0.717、combobox 0.655、wheel 0.523 | 4/4 完整：下緣差 0；colorbox 238–596，其他 10–824；`scrollY` 0→0；trigger 可見 |
| iPad | 600（900） | 4/4 不完整：colorbox 把可視區拉下 556、trigger 移出、右緣 629 > 600；combobox 與 wheel 完全看不到（可見比例 0） | 4/4 完整：下緣差 0；colorbox 121–479，其他 10–590；`scrollY` 0→0；trigger 可見 |
| Android | 834 / 600 | 與 iPad 相同 | 與 iPad 相同 |

- 我看過的圖：`out/matrix/ipad-tap-600-overflow-combobox-new.png`、`android-tap-600-overflow-colorbox-new.png`、`android-tap-834-overflow-timebox-new.png`。三張都是 sheet 貼著畫面下緣、左右都在畫面內，trigger 列在上方可見。
- 舊 CSS 的圖 `ipad-tap-834-overflow-datebox-old.png`：wheel 下半（OK/Cancel 列）在畫面外。`ipad-tap-600-overflow-colorbox-old.png`：只看到調色盤，右側被切，trigger 不在畫面上。

### 3.2 其他頁面形狀（新 CSS 都是完整可見，除非另外註明）

| 條件 | 舊 | 新 | 說明 |
|---|---|---|---|
| 只有垂直超出：長頁，tap 前 `scrollTo(0,150)` | fit 16/16 完整；overflow 0/16 | fit 16/16；overflow 16/16，`scrollY` 不變（150→150、600 寬 102→102） | 兩種 UA × 834/600 × 4 sheet |
| 程式捲到頁尾再 tap（`mt=2000`） | overflow 0/16 | 16/16 | `window.scrollTo` 不會移動 visual viewport（offsetTop 仍為 0），所以這不是「已知代價」的條件 |
| trigger 在 `overflow:auto` 容器內 | fit 8/8；overflow 0/8 | 8/8；8/8 | iPad UA |
| trigger 在 `transform` + `overflow:hidden` 容器內 | fit 8/8；overflow 0/8 | 8/8；8/8 | sheet 由 `makeVParent` 移到 `<body>`，容器的 transform 不影響 |
| `body { transform: translateZ(0) }` | 0/16 | 0/16 | old/new 數字完全相同：body 變成 fixed 的 containing block，sheet 低於可視區 200px。不是 c1 造成，c1 也沒有改變它 |
| `html, body { overflow-x: hidden }` | 16/16 | 16/16 | 這個設定下 layout viewport 沒有被放大 |
| 有垂直捲軸的頁面（`isMobile:false`、不隱藏 scrollbar） | 見 §3.4 | 見 §3.4 | |

### 3.3 RTL 的 overflow 頁（從載入就是 RTL）

量測到的機制：
- RTL 時，Chromium 把 fixed 的 containing block 往**左**放大：600 寬時為 x −300..600，834 寬時為 −67..834。`scrollX` 分別是 −300 / −67，可視區仍是 0..600 / 0..834。
- c1 的右側 inset `100% − 100vw` 假設多出來的寬度在右邊（LTR）。RTL 時多出來的寬度在左邊，所以 sheet 被放到左側畫面外。

| 寬 | sheet | 舊（可見比例） | 新（可見比例） |
|---|---|---|---|
| 834 | colorbox | 0.717（低 90） | 1（171–529） |
| 834 | combobox / wheel | 0.655 / 0.523（−57–824，低 90） | 0.93（−57–757，左側 57px 在畫面外） |
| 600 | colorbox | **0.919**（za11y focus 把可視區拉下 556，x −29–329） | **0.5**（−179–179） |
| 600 | combobox / wheel | 0（−290–590，低 556） | 0.5（−290–290） |

- 兩組都沒有完整可見的情況。
- 唯一可見比例下降的是 colorbox、RTL、600、overflow：0.919 → 0.5。這個條件下 colorbox 的 trigger 在 x 860–900（畫面外），old/new 都一樣。
- 圖：`out/matrix/rtl2-600-overflow-colorbox-old.png`、`rtl2-600-overflow-colorbox-new.png`、`ipad-rtl-600-overflow-combobox-new.png`。
- 推測，沒有原型驗證：改用邏輯屬性（`inset-inline-end: calc(100% - 100vw + 10px)`、`inset-inline-start: 10px`）RTL 時會落在可視區內。

### 3.4 有實體 scrollbar 的頁面（matrix 外，找到「舊可見、新不可見」）

條件：
- iPad UA（tablet CSS 有載入）、`isMobile: false`、`hasTouch: true`。
- Chromium 不帶 `--hide-scrollbars` 啟動，所以 scrollbar 佔版面：`clientWidth` 828、`innerWidth` 834。
- 834 寬。

| 頁面 | sheet | 舊 | 新 |
|---|---|---|---|
| fit（只有垂直捲軸） | colorbox | 235–593，完整 | 238–596，完整（`right` = −6px，中心右移 3px） |
| fit | combobox / wheel | 10–818，完整 | 10–824，完整（`right` = 4px，左右間距 10 / 4） |
| overflow（兩個方向都有捲軸） | colorbox | 788–1106，完整 | 794–1112：下緣低於可視區 6px（可見比例 0.981） |
| overflow | combobox / datebox / timebox | 完整 | 下緣低 6px，右緣伸到垂直捲軸下（可見比例 0.98 / 0.971 / 0.971） |

- 原因：`100vw` 含 scrollbar 寬度，`100%`（fixed 的 containing block）不含。差值是 −6px。
- 放大圖：`out/extra/crop-scrollbar-834-overflow-datebox-old.png` 的圓角下緣完整；`-new.png` 的右下圓角消失，被捲軸蓋住。
- 這個條件需要「行動 UA + 佔版面的 scrollbar」。tablet 與 forced-colors-tablet Playwright project 是 `isMobile: true`，scrollbar 是 overlay（`clientWidth` = `innerWidth`），不會碰到這個條件。哪些真實裝置會這樣，沒有驗證。

## 4. 已知代價條件與 old/new 對照

pinch 的作法：sheet 開著時用 CDP `Emulation.setPageScaleFactor` 縮到最小（`clientWidth / scrollWidth`）。另外也測「先縮小再 tap」：在縮放狀態下，`touchscreen.tap` 要用未換算的 CSS 座標才會打中 trigger；用換算後的座標時，600 overflow 那組 8 次都沒開，已改用未換算座標重測，見下表。

| 條件（兩種 UA） | 舊 | 新 |
|---|---|---|
| overflow 834，開著時 pinch 到 scale 0.9256 | 4/4 完整（下緣差 −0.67） | 4/4 完整，浮在下緣上方 **89.33px** |
| overflow 600，開著時 pinch 到 scale 0.6667 | 4/4 完整（貼底） | 4/4 完整，浮在下緣上方 **556px** |
| overflow 600，先 pinch 再 tap（iPad） | 4/4 完整（貼底） | 4/4 完整，浮 556px |
| overflow 834，先 pinch 再 tap | 4/4 完整 | 4/4 完整 |
| fit 頁 pinch | 最小 scale 是 1，沒有變化；4/4 | 4/4 |
| touch 手勢捲到頁尾再 tap（visual viewport offsetTop 90 / 556） | 834：colorbox 完整，combobox/wheel 0.935；600：0.919 / 0.67 | 4/4 完整，浮 **90 / 556px** |
| wheel 開著、touch 手勢捲到頁尾（`gEndOpen`） | 834：0.935；600：0.67 | 完整，浮 90 / 556px |
| 同上，colorbox / combobox / timebox | 手勢碰到 sheet 外，sheet 關閉（old/new 相同） | 同左 |
| touch 手勢水平捲到右端再 tap 右側 trigger（`gPanx`） | 0/4 | colorbox 完整（za11y focus 把可視區拉回）；combobox/wheel 834 可見 0.414、600 可見 0 |

- 浮起量：834 測到 89.33（= 1112 / 0.9256 − 1112），比調查文件寫的 88 多 1.33。600 為 556。所有浮起的情況下 sheet 都完整可見。
- 圖：`out/matrix/ipad-tap-600-overflow-combobox-new-pinched.png`（縮小後 sheet 在畫面中段）、`out/gesture/ipad-gEnd-600-overflow-datebox-new.png`（捲到頁尾，wheel 在畫面上半，下方空白）。
- old/new 對照總數：`matrix-*.json` 共 456 組（2 UA × 834/600 × fit/overflow × 4 sheet × 各條件，含 pinch 後、pan 後的第二次量測）。「舊完整可見、新不完整」為 **0 組**（`summary.txt`）。
- 這個 matrix 以外找到的情況：
  - §3.4 的 4 組（實體 scrollbar + overflow）：舊完整、新低 6px。
  - §3.3 的 1 組（RTL colorbox 600）：兩者都不完整，可見比例 0.919 → 0.5。
  - §1 的 `dvh` 不支援模擬：fit 頁舊完整、新完全看不到。這是模擬，不是實際引擎。

## 5. `signature.zul`

頁寬（`documentElement.scrollWidth` / `clientWidth`）：
- 600：600 / 600。修改前（HEAD 版本放在 inplaceWebapp 當暫時頁，已刪除、404）是 632 / 600。
- 834：834 / 834。

筆跡：
- 在兩個 signature canvas 的 10% / 50% / 90% 寬度處各畫一條垂直線。
- 讀回 canvas bitmap 的墨水欄位，換算成 CSS px，與筆的 x 比較。

| 情況 | canvas CSS 寬 / bitmap 寬 | 偏差（3 條線） |
|---|---|---|
| 桌面 UA + `page.mouse`，載入於 600 | 534 / 534 | +0.1 / 0 / −0.1 |
| 桌面 UA + `page.mouse`，載入於 834 | 598 / 598 | +0.2 / 0 / −0.2 |
| 桌面 UA，834 載入後縮到 600、600 載入後放到 834 | bitmap 跟著更新 | ≤ 0.2 |
| iPad UA + CDP touch 或 `page.mouse`，載入於 600 / 834 | 534/534、598/598 | ≤ 0.2 |
| **iPad UA，834 載入後縮到 600** | **534 / 598** | **−5.6 / −28.6 / −51.5** |
| **iPad UA，600 載入後放到 834** | **598 / 534** | 第 3 條（x 538）落在 bitmap 外、沒有墨水；前兩條 +7.4 / +35.8 |

- 原因：
  - `zk/src/main/resources/web/js/zk/mount.ts:926-927`：`zk.mobile` 時只聽 `orientationchange`，不聽 `resize`。
  - `zkmax/.../signature/Signature.ts:343`：bitmap 寬只在 `onSize` 重設。
  - 在 iPad UA 下 resize 後送一個 `orientationchange` 事件，bitmap 就回到 534/534、598/598。
- 修改前 canvas 固定 598 CSS px，bitmap 不會跟 CSS 寬度不一致；加了 `z-max-w-100` 之後 CSS 寬度會跟著容器變。
- 會不會在真機發生：iPad 的 Split View / Slide Over 會改變視窗寬度但不轉向。這是推論，沒有真機驗證。
- 修改前頁面在 iPad UA 600 的偏差 +31px，是我的腳本沒有扣掉 visual viewport 水平位移（632 寬的頁面），不能拿來比較。

引用 `signature.zul` 的 spec 與 baseline：
- `gallery-scan`（`signature-00-title.png` … `signature-04-toolbar-hidden.png`）
- `brand-override`（brand-residual）
- `compact-density`（compact-height）
- `render-smoke`
- `component-theme-variables`（signature）
- `font-size-equivalence`
- `focus-ring-scan`
- `forced-colors-gallery`（`signature-forced-colors.png`，依指示沒跑）
- config 裡沒有 `a11y` project。

結果見 §7：除 font-size 外都通過。`git status` 沒有任何 PNG 變動。

## 6. Mutation

做法：
- 把 `tablet.spec.ts` 複製到 scratch（`mut/tablet.spec.ts`），只在檔頭加一個 `beforeEach`，用 `context.route` 改寫 served CSS。其餘內容與原檔逐字相同，已用 `diff` 確認。
- 用 scratch config（只有 `tablet` project，use 與 repo 的 `TABLET_USE` 相同），`--update-snapshots=none`，`-g 'page-wider-than-screen'`。
- 每次改寫後記錄剩下的新值數量。

| 突變 | 剩下的新值（combobox+wheel / colorbox） | 結果 |
|---|---|---|
| 無 | 2 / 1 | 1 passed |
| 三條全改回舊值 | 0 / 0 | failed：`colorbox: the sheet ends inside the visible area`（629 > 601） |
| 只改 colorbox | 2 / 0 | failed：`colorbox: the sheet ends inside the visible area`（629） |
| 只改 combobox | 1 / 1 | failed：`combobox: the sheet bottom sits on the bottom of the visible area`（556 > 1） |
| 只改 wheel（`:has(.z-timebox-wheel-cave){` 之後的那一個） | 1 / 1 | failed：`datebox wheel: the sheet bottom sits on the bottom of the visible area`（556 > 1） |

- 測試只開 datebox wheel。timebox wheel 與它同一條 rule，沒有單獨的斷言。

## 7. Playwright（repo config、`--update-snapshots=none`、`PREVIEW_URL=http://127.0.0.1:8087`，從 `zkpreview/` 執行）

| 執行 | 結果 |
|---|---|
| `--project=tablet` 第 1 次 | 76 passed（1.3m） |
| `--project=tablet` 第 2 次 | 76 passed（1.3m） |
| `--project=forced-colors-tablet` | 14 passed |
| `--project=popup` | 55 passed，1 failed |
| select-tests 的 partial 清單，去掉 `forced-colors-gallery` | 79 passed，1 failed |

popup 的失敗：
- `daterangebox.zul: open with the today link`：`daterangebox-popup-open-today.png` 有 47 px 不同（ratio 0.01）。
- 依指示屬於已知失敗（`76b51e9e6` 在 `p3/zkcml` 歷史內）。
- popup project 的 use 是 `Desktop Chrome`，tablet CSS 不會啟用，所以 c1 不會影響它。
- 已知的桌面 flake（`datebox.zul: open calendar with the timezone selector`）這次沒有出現。

select-tests：
- `node zkpreview/scripts/select-tests.mjs --base 74fd35c271` 回 `partial`，81 個 test、2 頁。
- 跑了 80 個（去掉 `forced-colors-gallery`）。通過的有 gallery、brand、compact、smoke、component-theme-variables、focus-scan 各 1 個，以及 tablet.spec.ts 的 73 個。
- 失敗 1 個：`font-size equivalence › signature.zul`，13px 元素 609 → 612（+3）。
  - 對照組：`button.zul` 124→127、`colorbox.zul` 1732→1735、`textbox.zul` 65→68，都是 +3。屬於已知的 build 漂移，與這次修改無關。
- selector 只看 `p3/zk` 的變更。zkcml 的 CSS 修改不在它的輸入裡；它選 tablet 是因為 `tablet.spec.ts` 改了。
- selector 的 regex 只涵蓋 `tablet.spec.ts`。`tablet` project 另外 3 個 test（`frozen-sticky-tablet.spec.ts` ×2、`menubar-scroll-tablet.spec.ts` ×1）在上面兩次完整 tablet run 中都通過。

每次執行後 `git status --porcelain` 都沒有 PNG 出現。

## 8. 工作樹

`p3/zk`，執行前與執行後相同（逐字 `cmp`）：

```
 M zkpreview/src/main/webapp/web/signature.zul
 M zkpreview/src/test/playwright/tablet.spec.ts
?? node_modules
?? zkpreview/node_modules
?? zkpreview/src/main/webapp/web/pv/popup-tablet-overflow-fixtures.zul
```

```
 zkpreview/src/main/webapp/web/signature.zul  |  6 ++--
 zkpreview/src/test/playwright/tablet.spec.ts | 41 ++++++++++++++++++++++++++++
 2 files changed, 44 insertions(+), 3 deletions(-)
```

`p3/zkcml`，前後相同：

```
 M zkmax/src/main/resources/web/zkmax/css/tablet/_inputs.css
 M zkmax/src/main/resources/web/zkmax/css/tablet/_wheel.css
```

```
 .../main/resources/web/zkmax/css/tablet/_inputs.css   | 19 ++++++++++++++-----
 .../main/resources/web/zkmax/css/tablet/_wheel.css    |  8 ++++++--
 2 files changed, 20 insertions(+), 7 deletions(-)
```

- 兩個 repo 的 `git diff --cached --name-only` 前後都是空的。
- `git diff | shasum`（結束時）：zk `32f1bd45…`、zkcml `f4c68f59…`。
- inplaceWebapp 的暫時頁（`pv/c1v-sheet-probe.zul`、`pv/c1v-signature-old.zul`）已刪除，四個 URL 都回 404。

## 沒有驗證到的部分

- 真機 iOS Safari、真機 Android Chrome 都沒有測。Chromium 配 iPad UA 是混合組合。
- `dvh` 不支援的情況只在 Chromium 用無效單位模擬，沒有在 Chrome < 108 或 Safari < 15.4 上跑。
- 虛擬鍵盤開啟、工具列收合時 `dvh` 與 fixed containing block 的關係。
- 「行動 UA + 佔版面 scrollbar」（§3.4）對應哪些真實裝置。
- RTL 的邏輯屬性寫法只是推論，沒有做原型。
- `signature.zul` 在 iPad Split View 的實際行為（§5 是用 viewport resize 模擬）。
- IceBlue 沒有跑（不在指示範圍）。
