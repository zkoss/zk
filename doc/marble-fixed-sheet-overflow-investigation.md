# D41 調查：tablet 固定式 bottom sheet 在「頁面比螢幕寬」時跑出可視區

ZK-6112 follow-up 65。調查日期 2026-10-20。只有量測與原型，沒有改任何 tracked 檔案。

- 量測環境：`p3/zk` @ `74fd35c271`、`p3/zkcml` @ `234cbfa0f`，預覽伺服器 8087（自己啟動、已停止）。
- 瀏覽器：Playwright 1.59.1 Chromium，`isMobile: true`、`hasTouch: true`、`deviceScaleFactor: 1`，高度 1112。所有 sheet 都用 `page.touchscreen.tap` 開啟。
- 兩種 UA：
  - **iPad UA**：`tablet.spec.ts` 用的那個（`zk.ios = true`）。
  - **Android UA**：`Mozilla/5.0 (Linux; Android 13; SM-X700) … Chrome/120`（`zk.android = true`）。
  - Chromium 引擎配 Android UA 是真實存在的組合（Android 平板）。Chromium 引擎配 iPad UA 則是混合組合：ZK 走 iOS 分支，排版引擎卻是 Chromium。
- Repro 頁：暫時放在 `zkpreview/build/inplaceWebapp/web/pv/sheet-overflow-repro.zul`，已刪除，刪除後 `/pv/…` 與 `/web/pv/…` 都回 404。頁面內容：
  - 200px spacer，接著是 colorbox、combobox（8 個 item）、datebox。
  - `?wide=1` 時加一個 `width="900px"` 的 div。
  - `?right=1` 時把三個 trigger 右移 500px（只用在水平平移那組）。
  - 最後是 2400px spacer。
- 原始量測檔：`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/028a7b57-cfcf-44e9-a25e-5d68a4869b5a/scratchpad/d41/out2/<variant>/runs-*-results.json`。量測腳本是同資料夾的 `harness.js`。

## 1. 機制（量測值）

Chromium mobile emulation 下，頁面內容比 ICB 寬時：

| 量測 | fit 834 | overflow 834（頁寬 901） | fit 600 | overflow 600（頁寬 900） |
|---|---|---|---|---|
| `visualViewport` 寬×高、scale | 834×1112、1 | 834×1112、1 | 600×1112、1 | 600×1112、1 |
| `documentElement.clientWidth/Height`（ICB） | 834×1112 | 834×1112 | 600×1112 | 600×1112 |
| `window.innerWidth/innerHeight` | 834×1112 | **901×1200** | 600×1112 | **900×1668** |
| `position:fixed; inset:0` 探針（fixed 的 containing block） | 834×1112 | **901×1200** | 600×1112 | **900×1668** |
| `100vh` / `svh` / `lvh` / `dvh`，`100vw` | 1112，834 | 1112，834 | 1112，600 | 1112，600 |
| `jq.innerHeight()` | 1112 | 1112 | 1112 | 1112 |

說明：
- fixed 元素的 containing block 是 layout viewport。Chromium 會把它放大到「最小縮放時能放下整頁寬度」的大小，所以 overflow 時是 901×1200 或 900×1668；畫面上看得到的 visual viewport 仍是 1112 高。
- `bottom: 0` 因此落在可視區下緣以下：overflow 834 時低 88px，overflow 600 時低 556px。
- `window.innerHeight` 等於放大後的高度（1200 / 1668）；`jq.innerHeight()` 與 `100dvh` 等於可視區高度（1112）。

## 2. 現況（fixed）與 F3 之前（colorbox absolute）

判定方式：
- PASS 的條件：sheet 的 rect 完全在 visualViewport 內、PNG 上 sheet 的四條邊都畫得出來、sheet 下緣貼齊可視區下緣（誤差 ≤ 1px）、可視區沒有移動、trigger 仍在畫面上。
- PNG 判讀方式：另拍一張 marker 圖，在 sheet 上加 4px 洋紅 outline，數 PNG 像素。

### iPad UA

| 條件 | colorbox 現況 | combobox 現況 | datebox wheel 現況 | colorbox F3 之前 |
|---|---|---|---|---|
| fit 834 | PASS | PASS | PASS | FAIL：頁面捲動 579，trigger 移出畫面 |
| overflow 834 | FAIL：sheet 下緣低於可視區 88 | FAIL：低 88 | FAIL：低 88 | FAIL：可視區移動 667（scroll 579 + 平移 88），trigger 移出 |
| fit 600 | PASS | PASS | PASS | FAIL：捲動 579，trigger 移出 |
| overflow 600 | FAIL：可視區被拉下 556，trigger 移出，sheet 右緣被切（271–629 對 0–600） | FAIL：sheet 完全在可視區外（top 1368 > 1112） | FAIL：完全在可視區外（top 1464） | FAIL：可視區移動 1135，trigger 移出 |
| fit 834，先捲 150 | PASS | PASS | PASS | FAIL：捲動 579 |
| overflow 834，先捲 150 | FAIL：低 88 | FAIL：低 88 | FAIL：低 88 | FAIL：移動 667 |

Android UA 下，現況的 18 個結果與上表完全相同。

先前 verifier 的數字可以重現：`signature.zul` 在 600 寬時頁寬 632，colorbox sheet 低於可視區 60px（`innerHeight` 1172）。

### F3 之前的頁面跳動：真正的觸發點

- F3 之前頁面會跳，觸發點是 za11y 的 `focus()`，不是 `position:absolute` 本身。
- 呼叫位置：`za11y/src/main/resources/web/js/za11y/zkex/inp-a11y.ts:133-138` 覆寫了 `Colorpalette.onShow`，裡面執行 `wgt.$n_('palette-btn').focus()`，沒有帶 `preventScroll`。
- 這是 runtime 攔截 `HTMLElement.prototype.focus` 抓到的 stack。
- `colorbox-touch.ts` 本身的 `onShow` 把 focus 註解掉了，`_syncPosition` 也沒有呼叫 `focus()` 或 `scrollIntoView()`。
- za11y 的 `onShow` 在 `openPalette()` 裡、`_syncPosition()` 之前執行：focus 發生時 sheet 還停在 `innerHeight + scrollY` 的位置（畫面外），所以瀏覽器把頁面捲過去。
- 同一個 focus 也解釋了 overflow 600 現況下「可視區被拉下 556」：fixed sheet 在可視區外，被 focus 的按鈕在 sheet 裡。
- 驗證：F3 之前的 CSS 加上「`-palette-btn` 的 focus 強制 `preventScroll:true`」之後，fit 834、fit 600、fit 834 先捲 150 三種條件全部 PASS，可視區移動 0。

### 不套 Marble 三條 override 時，widget 自己的定位

做法：把 served CSS 裡三條 sheet rule 的 `position/inset/transform/width !important` 拿掉，其餘不變。

| 條件（Android UA） | colorbox，za11y focus 原樣 | colorbox，focus 加 preventScroll | combobox | datebox wheel |
|---|---|---|---|---|
| fit 834 / fit 600 | FAIL：捲動 579 | PASS | FAIL：低 4px | PASS |
| overflow 834 / overflow 600 | FAIL：捲動 579 | **PASS** | FAIL：低 4px | **PASS** |

iPad UA 下，widget 自己的定位在 overflow 時也低 88 / 556（約 88–92 / 556–560）。原因是 iOS 分支用 `window.innerHeight`，而 Chromium 的 `innerHeight` 是放大後的值。

結論（量測範圍內）：
- 在 Chromium 配 Android UA 的真實組合上，overflow 頁的切掉或看不見，是 Marble 的 `position:fixed; bottom:0` 錨點造成的。widget 自己（`jq.innerHeight() + scrollY`）會把 colorbox 和 wheel 放在可視區下緣。
- Marble override 原本要修的問題確實存在：combobox 低 4px、za11y focus 讓頁面跳 579。
- IceBlue 本身沒有跑（依指示不需要）。「不套 Marble override」只是 widget 行為的代理，不等於 IceBlue。

## 3. Overflow 的原因（真實預覽頁）

### `signature.zul`

- 原因在預覽頁（O5）：`<signature width="600px">` 兩個、`<image width="600px">` 一個，放在 `z-p-8`（左右各 32px）的容器裡。
- 量測：外層 `div.z-signature[style.width=600px]` 的 right = 632，parent right = 568，所以 600 寬時頁寬 = 32 + 600 = 632。
- 與 Marble CSS、ZK widget 無關。
- 頁面修法的模擬：注入 class `z-max-w-100`（既有的 utility）到 `.z-signature` 與 `.z-image`。
  - 結果：頁寬 632 → 600，colorbox sheet PASS（794–1112）。
  - 未驗證：signature 的 canvas 屬性寬仍是 598，CSS 寬變成 534，筆跡座標是否正確沒有驗證（滑鼠模擬在 mobile context 不會落墨）。
  - 改頁面的人要選 `z-max-w-100` 或其他寬度寫法，並實際畫一筆確認。

### `pv/popup-tablet-fixtures.zul`（兩個 combobox 並排）

- 在 360×780、360×1112、先 834 載入再縮到 360 三種設定下，row 的 right 都是 328，頁寬都是 360，**沒有 overflow**。
- 兩個 combobox 各 140px 寬（390 寬時各 155px）。combobox sheet PASS（480–780）。
- 先前的「482px」我重現不出來，可能是不同的 viewport、UA 或 build。
- 依量測，這頁不需要修。要找出差異，需要 verifier 提供當時的設定。

### 全部 115 個預覽頁的掃描

條件：iPad UA，頁寬 > clientWidth + 1 即算 overflow，列出最外層超出的元素。

- **834**：2 頁 overflow，都沒有 sheet trigger。
  - `responsive-grid.zul`：grid `width=820px`。
  - `tabbox.zul`：flex 欄 834px。
  - `preview.zul` 在 834 載入逾時（30 s），未量。
- **600**：11 頁 overflow，其中 4 頁有 sheet trigger：
  - `signature.zul`（colorbox）：signature 600px。
  - `grid-header.zul`（datebox）：表格 700px。
  - `tabbox-misc.zul`（combobox）：tabbox 680px。
  - `preview.zul`（四種都有）：`z-grid-col-full` 1207px。
- **600，沒有 sheet trigger 的 7 頁**：`carousel`、`checkbox`、`grid-detail`、`navbar`、`responsive-grid`、`stepbar`、`tabbox`。
- 這些都是頁面上寫死的寬度（O5）。依 O10，只有 `signature.zul` 屬於這次的問題，其他列入 followups。
- 明細：`scratchpad/d41/scan-all.json`。

## 4. 候選方案

所有候選都用 Playwright 的 `context.route` 改寫 served `tablet.css.dsp`、`addStyleTag` 或 `addInitScript` 做原型，沒有動 tracked 檔案。每個改寫都斷言命中次數符合預期：`inset:auto 0 0!important` ×1、`inset:auto 10px 0!important` ×2、`position:fixed!important` ×3、F3 的 colorbox rule ×1，全部吻合。

額外加了四種條件：
- **pan**：sheet 開著時用 touch scroll gesture 捲到頁尾。colorbox 與 combobox 被 touch 到外面就會關閉，只有 wheel 會留著。
- **zoom-out**：sheet 開著時用 `Emulation.setPageScaleFactor` 縮到最小 scale：0.6667 / 0.9267。
- **panx**：先水平平移 300px 再 tap 右側的 trigger。
- **scrolled**：tap 之前先捲 150。

| 候選 | 4 個基本條件（834/600 × fit/overflow），3 種 sheet | 先捲 150（fit/overflow 834） | pan 到頁尾（wheel） | zoom-out | 水平平移 panx（600） | 需要什麼 |
|---|---|---|---|---|---|---|
| a 不動 | fit 6/6 PASS；overflow 6/6 FAIL（低 88，或 556 完全看不見；colorbox 拉動可視區 556） | fit PASS，overflow FAIL（低 88） | FAIL（sheet 880 寬，可視區 600，右側被切） | PASS | 3/3 FAIL（看不見，或可視區被拉 556） | 無 |
| b 只修頁面（O5） | 預覽頁變成 fit，全部 PASS；客戶的寬頁面照舊是 a 的結果 | 同 a | 同 a | 同 a | 同 a | 改預覽頁 |
| c1 單位：`inset: auto calc(100% - 100vw) calc(100% - 100dvh) 0`（combobox 與 wheel 左右再各加 10px），只改既有 `inset … !important` 的值 | **12/12 PASS**（iPad 與 Android UA 都是）；fit 頁的 rect 與現況完全相同 | 6/6 PASS | FAIL：浮在可視區下緣上方 88 / 556，整個 sheet 仍看得見 | FAIL：浮在上方 88 / 556，看得見 | colorbox：可視區被水平拉回 296（za11y focus）；combobox 與 wheel 只有左半邊（0–291px）在畫面上 | 無新的 `!important`，不是 E5 |
| c2 `position:absolute`（ICB 為 containing block） | 12/12 PASS（只在 scroll 0 時成立） | **6/6 FAIL**：浮在上方 150 | FAIL：sheet 隨頁面捲走（−1662） | 未測 | 未測 | 值改動 |
| c3 `html{overflow-x:clip}`（只作參考，不准放在 pop-up rule） | 結果與 a 完全相同：Chromium 的 layout viewport 照樣放大 | 同 a | 同 a | — | — | 全域規則，不採用 |
| d widget JS 依 `window.visualViewport` 定位（原型：包住 `_syncPosition`，寫入 `--zk-sheet-inset-bottom/right/left`；既有的 `inset … !important` 改讀這些變數） | **12/12 PASS** | 6/6 PASS | **PASS**（visualViewport scroll/resize 時重算） | **PASS**（combobox 的 PNG 右緣被 iScroll 捲軸蓋住，偵測器誤判，已看圖確認可見） | **3/3 PASS** | core 團隊改 JS（ZK Jira，E6）；Marble 改既有 `inset` 的值 |

c1 與 d 的 zoom-out 與 panx 只跑了 Android UA。d 的 pan 兩種 UA 都 PASS。

## 5. 建議（單一方案）

採用 **O4 的分拆**：CSS 搆得到的部分 Marble 現在做，行為部分開 ZK Jira。

**Marble 現在做：c1。** 只改既有三條 `inset … !important` 的值，`!important` 數量不變，所以不是 E5。

- 依據：
  - O1/O4：Android UA 上，overflow 時 sheet 被切或看不見，來自 Marble 自己的 `bottom: 0` 錨點，widget 自己會放在可視區下緣（第 2 節）。
  - c1 在所有 scale 1 的 tap 條件下都 PASS（基本 12/12、先捲 6/6，兩種 UA 都是）。
  - fit 頁的幾何與現況逐 px 相同，所以不會影響已通過的 tablet 截圖。
- 代價（量測值）：
  - pinch 縮小或捲到頁尾時，sheet 浮在可視區下緣上方 88 / 556px，但仍完整可見。現況縮小時是貼底的，這是唯一一個「現況 PASS、c1 FAIL」的條件。
  - 水平平移後，combobox 與 wheel 只有半邊在畫面上。現況是整個看不見。

**同時做：b，O5。** 把 `signature.zul` 的寫死寬度改成不超出 600 寬的寫法（`z-max-w-100` 或改 `width`；canvas 筆跡要實測）。其他 10 頁列入 `doc/marble-theme-followups.md`（O10）。

**之後做：d，開 ZK Jira（E6，需要 owner 確認後才開）。**
- d 是唯一全部條件都 PASS 的方案，包括 pan、zoom-out、panx。
- 依 O2/O3：runtime 計算的位置屬於 JS。
- 修好之後 Marble 再拿掉 c1，評估能否拿掉三條 sheet override 的 `!important`。

**需要 owner 決定的事：**
- E6：要不要開下面兩張 Jira。
- 要不要接受 c1 在 zoom-out 時「浮起但可見」的代價。
- c1 本身不需要 E5。

### Jira 草稿（未開立）

Affects Version: 11.0.0（`gradle.properties` = `11.0.0-SNAPSHOT`）。

1. **Tablet bottom sheets are positioned against the layout viewport instead of the visual viewport on pages wider than the screen**
   - `_syncPosition` in `zkmax/touch/colorbox-touch.ts`, `combo-touch.ts`, `datebox-touch.ts` (`CalendarPop`) and `timebox-touch.ts` sets `top = (zk.ios ? window.innerHeight : jq.innerHeight()) + scrollY`.
   - When the page is wider than the device width, Chrome enlarges the layout viewport (`window.innerHeight` 1668 vs visible 1112 at a 600px-wide screen), and pinch-zoom or panning moves the visible area inside it, so the sheet opens below or beside the visible area.
   - Use `window.visualViewport` (`offsetTop`/`offsetLeft`/`height`/`width`, updated on its `resize`/`scroll` events) to place the sheet; the prototype keeps the sheet inside the visible area in every measured condition.
2. **Colorbox palette focus scrolls the page on tablets (za11y)**
   - `za11y/zkex/inp-a11y.ts` `Colorpalette.onShow` calls `palette-btn.focus()` without `preventScroll`.
   - It runs in `openPalette()` before `_syncPosition()`, while the pop-up still sits below the viewport.
   - The browser scrolls the page by 579px and the colorbox leaves the screen; with `focus({ preventScroll: true })` the page stays put and the sheet lands at the bottom.

## 6. 圖片

資料夾：`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/028a7b57-cfcf-44e9-a25e-5d68a4869b5a/scratchpad/d41/`。每張都是整個可見螢幕。除了標 `android` 的，其餘都是 iPad UA。

| 檔案 | 內容 |
|---|---|
| `fit-834-colorbox-now.png` | 頁面放得下：現況 sheet 貼底，trigger 可見 |
| `fit-600-colorbox-now.png` | 同上，600 寬 |
| `fit-834-colorbox-before-f3.png` | F3 之前：頁面跳 579，trigger 不見（F3 缺陷） |
| `fit-834-colorbox-before-f3-focus-noscroll.png` | F3 之前的 CSS 加上 focus `preventScroll`：sheet 貼底、頁面不動 |
| `fit-834-colorbox-widget-only-android-focus-scroll.png` | 拿掉 Marble override、Android UA：za11y focus 讓頁面跳 579 |
| `overflow-834-colorbox-now.png` / `-combobox-now` / `-datebox-now` | 頁寬 901：三種 sheet 下緣都被切 88px |
| `overflow-834-colorbox-before-f3.png` | F3 之前、頁寬 901：頁面跳動，trigger 不見 |
| `overflow-600-colorbox-now.png` | 頁寬 900：可視區被拉下 556，trigger 不見，sheet 右緣被切 |
| `overflow-600-combobox-now.png` / `overflow-600-datebox-now.png` | 頁寬 900：sheet 完全看不到 |
| `overflow-600-colorbox-before-f3.png` | F3 之前、頁寬 900 |
| `overflow-600-colorbox-widget-only-android.png` | 拿掉 Marble override 加 focus 修正、Android UA：widget 自己貼底 |
| `overflow-600-{colorbox,combobox,datebox}-c1-units.png` | c1：三種 sheet 都貼可視區下緣 |
| `overflow-600-{colorbox,combobox,datebox}-d-js.png` | d：三種 sheet 都貼可視區下緣 |
| `overflow-600-zoomout-combobox-now.png` / `-c1-units` / `-d-js` | 縮到最小：現況貼底；c1 浮在中間、寬只有 600 css px；d 貼底 |
| `overflow-600-pan-datebox-now.png` / `-c1-units` / `-d-js` | wheel 開著捲到頁尾：現況右側被切；c1 浮起 556；d 貼底 |
| `overflow-600-panx-combobox-now.png` / `-c1-units` / `-d-js` | 先水平平移 300 再 tap：現況看不見；c1 只剩左半；d 完整 |
| `fit-834-scrolled-combobox-c2-absolute.png` | c2：捲 150 後 sheet 浮在下緣上方 150 |
| `signature-600-colorbox-now.png` / `signature-600-colorbox-pagefix.png` | `signature.zul` 600 寬：現況低 60px；模擬頁面修正後貼底 |

## 7. 沒有驗證到的部分

- **真機 iOS Safari 沒有測。** 以下是推論，未驗證：
  - WebKit 在 iOS 上以 scale 1 顯示時，fixed 的 containing block 被認為等於 ICB 與可視區（Chromium 的「依最小縮放放大 layout viewport」是 Chrome Android 的行為）。
  - 如果成立，iPad 上 scale 1 的現況可能不會被切；而 `100% − 100dvh = 0`，c1 在 iPad 上會與現況相同。
  - iOS 的 `window.innerHeight` 一般反映 visual viewport，所以 widget 的 iOS 分支在 iPad 上可能是對的。
  - 虛擬鍵盤開啟、工具列收合時，`dvh` 與 fixed containing block 的關係也沒有驗證。
- 真機 Android 也沒有測。Chromium emulation 與 Chrome Android 用同一套 viewport 程式，但沒有實機確認。
- Chromium 配 iPad UA 的那些列是混合組合，不代表任何真實裝置。
- `signature.zul` 加 `z-max-w-100` 之後 canvas 被 CSS 縮放（598 → 534），筆跡座標沒有驗證。
- `popup-tablet-fixtures.zul` 的「482px」重現不出來。
- `preview.zul` 在 834 載入逾時，沒有量。
- IceBlue 沒有跑（依指示）。「拿掉 Marble override」只是 widget 行為的代理。
- zoom-out 是在 sheet 開著時才縮放；「先縮小再 tap」沒有測，因為縮放後的觸控座標換算沒有處理。
- pan 條件下，colorbox 與 combobox 會因 touch 到外面而關閉，所以 pan 只量得到 wheel。

## 8. 工作樹狀態

`p3/zk` 的 `git status --porcelain`，執行前後相同：

```
?? node_modules
?? zkpreview/node_modules
```

`p3/zkcml`：執行前後都是空的。

8087 伺服器：
- 自己啟動的 PID：sh 94013、`tail -f /dev/null` 94015、gradle client 94017、daemon 94069、Jetty 94602。
- 只 kill 了 94015，appRun 正常結束（`BUILD SUCCESSFUL`），五個 PID 都已消失，8087 沒有在 listen。
