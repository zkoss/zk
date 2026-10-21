# Tablet bottom sheet 兩個疑似 ZK bug：最小重現結果

內部筆記，不送 Jira。調查日期 2026-10-21。只做量測，沒有修改任何 tracked 檔案。是否開 bug 由 owner 決定。

## 0. 結論摘要

| 問題 | 判定 | 一句話 |
|---|---|---|
| 1. colorbox 在觸控裝置點開時頁面捲動 | **重現**（不需要 Marble；IceBlue 一樣） | 需要 za11y、tablet mold（行動裝置 UA）、頁面可捲動的範圍至少約一個 sheet 高。拿掉 za11y 或讓那次 `focus()` 帶 `preventScroll` 就消失。Playwright WebKit（iPad 描述檔）也重現。 |
| 2. 頁面比螢幕寬時 bottom sheet 沉到可視區下方 | **只在「Chromium 引擎＋iPad UA＋`isMobile`」這個混合組合下重現**。真實組合（Chromium＋Android UA、WebKit＋iPad UA）在 scale 1 時都正確。 | 另外找到一個與頁寬無關的條件：**放大（pinch zoom-in）之後點開**，sheet 一律放在 layout viewport 底部，看不見。Chromium＋Android UA 這個真實組合會發生，IceBlue 與 Marble 都會。WebKit 無法用 Playwright 模擬 pinch，所以沒驗證。 |

## 1. 環境

| 項目 | 值 |
|---|---|
| 無 Marble 環境 | 新建的 worktree：`/Users/hawk/Documents/workspace/ZK10/repro/zk` @ `85c7731da4`（master）、`/Users/hawk/Documents/workspace/ZK10/repro/zkcml` @ `1679dd566`（master）。`gradle.properties` = `11.0.0-SNAPSHOT`，served build id `452ff08`。預設主題 IceBlue（`zul/css/zk.wcs` + `za11y/css/iceblue.css.dsp`），tablet CSS 是 master 的 stock `zkmax/css/tablet.css.dsp`。 |
| 伺服器 | `zktest` webapp，`appRun` 在 8107，context `/zktest`。 |
| 開啟 tablet UI | `zktest` 的 `zk.xml` 預設關閉 tablet UI。做法與 `TabletWebDriverTestCase` 相同：系統屬性 `org.zkoss.zk.config.path=/test2/enable-tablet-ui-zk.xml`。用 scratchpad 裡的 Gradle init script（`repro/tablet-on.init.gradle`）加到 gretty 的 `jvmArgs`，沒有改 tracked 檔。log 有 `Parsing /test2/enable-tablet-ui-zk.xml`；頁面內 `zk.tabletUIEnabled = true`，tablet CSS 已啟用。 |
| 不載入 za11y | 用 `-PzktestWithoutA11y=true` 重新啟動同一個伺服器（`zktest/gradle.properties` 原本就有的專案屬性）。頁面內 `window.za11y` 不存在，`za11y/css/iceblue.css.dsp` 也沒有載入。 |
| Marble 對照 | `/Users/hawk/Documents/workspace/ZK10/p3/zk` @ `3539d1711d`、`p3/zkcml` @ `c863e278b`（內含 follow-up 65 的 sheet 修正），8087。 |
| 瀏覽器 | Playwright 1.59.1。Chromium 147.0.7727.15；WebKit 26.4（revision 2272，**cache 裡原本就有，沒有執行 `npx playwright install`**）。 |
| 觸控 | 一律用 `page.touchscreen.tap`；desktop UA 那一列用 `page.mouse.click`。 |
| UA | Android：`Mozilla/5.0 (Linux; Android 13; SM-X710) … Chrome/120`。Chromium 的 iPad：`Mozilla/5.0 (iPad; CPU OS 16_0 like Mac OS X) … Safari/604.1`。WebKit：Playwright 內建的 `iPad Pro 11`（834×1194、dsf 2、isMobile、hasTouch）、`iPad Pro 11 landscape`、`Galaxy Tab S4`。 |
| 量測腳本 | `/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/028a7b57-cfcf-44e9-a25e-5d68a4869b5a/scratchpad/repro/h.js`。每個 run 開新 context，攔截 `HTMLElement.prototype.focus` 記下每次呼叫（id、參數、呼叫前後的 `scrollY`、當下 popup 的 rect、stack）。點開後等 sheet 停止移動，記錄 `visualViewport`、`window.innerHeight`、`jq.innerHeight()`、`zk.ios`/`zk.android`、sheet rect，並截可見畫面。原始資料：同資料夾 `out-*/runs-*-results.json`。 |
| Repro 頁 | 只放在 `zktest/build/inplaceWebapp/pv/`（與 p3 的 `zkpreview/build/inplaceWebapp/web/pv/zzrepro-*.zul`），用完已刪除，舊 URL 都回 404（見第 6 節）。 |

## 2. 問題 1：colorbox 點開時頁面捲動

### 2.1 最小 ZUL

```xml
<zk>
	<colorbox/>
	<div height="2000px"/>
</zk>
```

需求：EE 的 colorbox、行動裝置 UA（讓 `zk.mobile` 成立，套用 tablet mold）、載入 accessibility add-on（za11y）。

### 2.2 結果（IceBlue，無 Marble）

| 條件 | 頁面捲動 | sheet 位置（可視區 0–高度） | trigger 可見 |
|---|---|---|---|
| Chromium，Android UA，834×1112 | **+585** | 43–527（浮在中段，不在底部） | 否 |
| Chromium，iPad UA（混合組合），834×1112 | **+585** | 43–527 | 否 |
| Chromium，Android UA，600×1112 | **+585** | 43–527 | 否 |
| **WebKit `iPad Pro 11`**（834×1194） | **+626** | 84–568 | 否 |
| WebKit `iPad Pro 11 landscape`（1194×834） | **+446** | −96–388（上緣被切掉 96） | 否 |
| WebKit `Galaxy Tab S4`（Android UA＋WebKit，混合） | +599 | 55–539 | 否 |
| 同上 Android 834，`focus()` 對 `-palette-btn` 強制 `preventScroll:true` | 0 | 628–1112（貼底） | 是 |
| WebKit `iPad Pro 11`，同樣強制 `preventScroll` | 0 | 710–1194（貼底） | 是 |
| **不載入 za11y**（Android、iPad-Chromium、WebKit iPad Pro 11） | 0 | 貼底 | 是 |
| Desktop UA（不論 `isMobile`/`hasTouch`） | 0 | 一般下拉式 palette（tablet mold 沒有載入，`zk.tabletUIEnabled=false`） | 是 |
| Marble（p3），Android 834 | 0 | 794–1112（`position:fixed`，貼底） | 是 |

Focus 呼叫紀錄（Chromium，Android）：
- 第 1 次：`Colorbox._doBtnClick → openPopup → openPalette → Colorpalette.onShow`（za11y 版本）。此時 popup 還在原位，`scrollY` 0 → 0。
- 第 2 次：`Colorbox._doBtnClick → zUtl.fireShown → zWatch.fireDown → Colorpalette.onShow`（za11y 版本）。此時 `_syncPosition` 已經把 popup 放在 `style.top = 1112px`（= `jq.innerHeight() + scrollY`，可視區正下方），slide-up 的 transition 還沒開始。`focus()` 沒帶參數，`scrollY` 0 → **585**。
- **更正前一份報告**：`doc/marble-fixed-sheet-overflow-investigation.md` §2 寫「focus 在 `_syncPosition()` 之前」。實際上造成捲動的是第二次呼叫，發生在 `_syncPosition()` **之後**、transition 之前（經由 `fireShown`）。第一次呼叫在 `_syncPosition()` 之前，但不會捲動。結論不變：觸發點是 za11y 的 `focus()`。
- WebKit 裡 focus 的捲動是非同步的：`focus()` 回傳時 `scrollY` 仍是 0，最後停在 626。強制 `preventScroll` 後是 0，所以仍是同一次 focus 造成的。

### 2.3 縮減表（Chromium，Android UA，834×1112，IceBlue）

| 變體 | 結果 | 說明 |
|---|---|---|
| 只有 `<colorbox/>`，下方沒有 spacer | **最後不動**（focus 時暫時捲 484，sheet 滑上去後文件變短，捲動被夾回 0） | 消失 |
| 下方 spacer 100 / 200 / … / 1000px（頁高 ≤ 視窗） | 0 | 消失 |
| 下方 spacer 1150px（頁高 1184，最多可捲 72） | +72 | 出現，被可捲範圍夾住 |
| 下方 spacer 1500px | +422（= 可捲上限） | 出現 |
| 下方 spacer 2000 / 3000px | +585 | 出現 |
| colorbox 上方加 300 / 600 / 1000px | 都是 +585 | 跳動量與 colorbox 位置無關；trigger 是否被捲出畫面則取決於位置（上方 600、1000 時 trigger 仍在畫面上，但 sheet 浮在中段） |
| 點之前先捲 150 | +585 | 與起始捲動量無關 |
| `hasTouch` 關（改用滑鼠 click）、或 `isMobile` 關 | 仍 +585 | 不影響；影響的是 UA（決定 `zk.mobile`） |
| Desktop UA | 0 | 消失（沒有 tablet mold） |
| 不載入 za11y | 0 | 消失 |
| 只對 `-palette-btn` 的 `focus()` 加 `preventScroll:true` | 0 | 消失 |

跳動量的來源：被 focus 的按鈕在 popup 頂端附近，popup 當時在 `top = 可視區高度`。Chromium 把它捲到可視區中央：1112 + 約 29 − 556 ≈ 585。所以跳動量取決於視窗高度，不取決於頁高（頁高只會把它夾住）。

### 2.4 IceBlue 的實際外觀（題目 e）

會。IceBlue（stock）的 popup 是 `position:absolute`（widget JS 自己設的）。頁面捲動 585 之後 sheet 留在文件座標的原位，所以：
- sheet 浮在可視區中段（43–527 / 1112），不在底部；
- colorbox 本身若在頁面上方，會被捲出畫面；
- WebKit landscape 時 sheet 上緣被切掉 96px。

見圖 `p1-iceblue-android-834-jump585.png`、`p1-iceblue-webkit-ipadpro11-jump626.png`、`p1-iceblue-webkit-ipadpro11-landscape-jump446.png`。

Marble tip 不會跳：Marble 的 `position:fixed; bottom:0` 讓按鈕在 focus 時已經在可視區內。這是遮掉症狀，不是修正。za11y 的 `focus()` 仍然沒有帶 `preventScroll`。

### 2.5 判定

**重現**：ZK 11.0.0-SNAPSHOT（master）、IceBlue、stock tablet CSS、EE colorbox＋za11y、行動裝置 UA，頁面可捲範圍夠大時就會發生。Chromium（Android UA，真實組合）與 Playwright WebKit（iPad 描述檔）都會。拿掉 za11y 或 `preventScroll` 都會消失，所以觸發點是 za11y 的 `Colorpalette.onShow` 那次 `focus()`。

## 3. 問題 2：頁面比螢幕寬時 sheet 沉到可視區下方

### 3.1 最小 ZUL

```xml
<zk>
	<combobox>
		<comboitem label="Item 1"/><comboitem label="Item 2"/><comboitem label="Item 3"/><comboitem label="Item 4"/>
		<comboitem label="Item 5"/><comboitem label="Item 6"/><comboitem label="Item 7"/><comboitem label="Item 8"/>
	</combobox>
	<div width="900px" height="1px"/>
</zk>
```

把 `<combobox>` 換成 `<datebox/>`、`<timebox/>`、`<colorbox/>` 也一樣。

注意：`<div width="900px"/>` **沒有高度時不會讓頁面變寬**（量到 `scrollWidth` = 600，三個 run 都 PASS）。一定要有非零高度（`height="1px"`）。

### 3.2 結果（IceBlue，無 Marble；colorbox 列已強制 `preventScroll`，排除問題 1 的干擾）

LOW = sheet 下緣 − 可視區下緣（> 0 表示有一部分在可視區下方）。四種元件（combobox、datebox、timebox、colorbox）的 LOW 完全相同，表中只列一次。

| 引擎＋UA | `isMobile` | 視窗 | 頁面 | `window.innerHeight` | `jq.innerHeight()` | `visualViewport.height` | LOW | 是否為真實組合 |
|---|---|---|---|---|---|---|---|---|
| Chromium＋Android | true | 834 / 600 | fit | 1112 | 1112 | 1112 | 0 | 是 |
| Chromium＋Android | true | 834 | 頁寬 907 | **1209** | 1112 | 1112 | **0** | 是 |
| Chromium＋Android | true | 600 | 頁寬 905 | **1678** | 1112 | 1112 | **0** | 是 |
| Chromium＋iPad | true | 834 / 600 | fit | 1112 | 1112 | 1112 | 0 | 否（混合） |
| Chromium＋iPad | true | 834 | 頁寬 907 | 1209 | 1112 | 1112 | **97**（四種都在可視區外一部分） | 否（混合） |
| Chromium＋iPad | true | 600 | 頁寬 905 | 1678 | 1112 | 1112 | **566**（完全看不到） | 否（混合） |
| Chromium＋Android / iPad | false | 834 / 600 | fit 或 overflow | 1112 | 1112 | 1112 | 0 | — |
| **WebKit `iPad Pro 11`** | true | 834 / 600 | fit 或 overflow（頁寬 905/906） | **1194** | 1194 | 1194 | **0** | 最接近真實（但不是 iOS） |
| WebKit `Galaxy Tab S4` | true | 600 | 頁寬 905 | 1138 | 1138 | 1138 | 0 | 否（混合） |

縮減：
- 拿掉寬元素（fit）→ 全部 PASS。
- 寬元素沒有高度 → 不 overflow，PASS。
- combobox 只留 1 個 item → 仍低 566（item 數無關）。
- `isMobile:false` → Chromium 不放大 layout viewport，`innerHeight` = 1112，PASS。
- 換成 Android UA → `zk.ios` 為 false，改走 `jq.innerHeight()` 分支，PASS。

機制（量測值）：
- Chromium mobile emulation 下，頁面比 ICB 寬時，`window.innerHeight` 等於「依最小縮放放大後的 layout viewport」高度。
- `jq.innerHeight()` 仍是可視區高度。
- ZK 只有 `zk.ios` 時用 `window.innerHeight`，所以只有 iPad UA 會低。
- WebKit（Playwright，macOS 版）在同樣的頁面上，`window.innerHeight` = 可視區高度，layout viewport 沒有放大，所以 iOS 分支也正確。

### 3.3 縮放（Chromium，600 寬；WebKit 沒有 pinch API，沒測）

| 條件 | Android UA | iPad UA（混合） |
|---|---|---|
| 頁寬 905，開著時縮到最小（scale 0.663，可視區 0–1677） | sheet 在 802–1112：**浮在可視區下緣上方 565，但完整可見** | 1368–1678：貼底（LOW 0.8） |
| 頁寬 905，先縮到最小再點 | 同上 | 同上 |
| **fit 頁（600），先放大到 scale 2 再點**（可視區 0–556） | **802–1112：完全在可視區外（LOW 556）** | 802–1112：LOW 556 |
| 頁寬 905，先放大到 scale 2 再點 | LOW 556 | LOW 1122 |

- 放大後點開：可視區只有 556 css px 高，但 `jq.innerHeight()` 與 `window.innerHeight` 都還是 1112（layout viewport），所以 sheet 放在 layout viewport 底部，使用者看不到。
- 這與頁寬無關，在 Chromium＋Android UA（真實組合）上也成立。
- Marble tip（p3）也一樣：fit 頁 scale 2，四種 sheet 都 LOW 556（`p2-marble-chromium-androidUA-600-fit-zoom2-combobox-offscreen.png`）。Marble 的 `position:fixed; bottom:0` 錨在 layout viewport，不在 visual viewport。

### 3.4 Marble 對照（p3，含 follow-up 65 修正）

- Chromium，600，頁寬 900，Android 與 iPad UA：四種都 PASS。修正已讓混合組合也貼底。
- WebKit `iPad Pro 11`：四種都 LOW 6。
  - 原因：Marble 頁面在 macOS WebKit 上 `visualViewport` 是 828×1188，比 `innerWidth/innerHeight` 少 6px，看起來是 macOS 上不會自動隱藏的 6px 捲軸。
  - IceBlue 頁面同一個瀏覽器是 1194。
  - 這是桌面 WebKit 的捲軸，iOS 的捲軸是 overlay，應該不會發生。沒有進一步查。

### 3.5 判定

- **懷疑 A：在真實組合上不成立（REFUTED）。**
  - 量測範圍：scale 1、頁面比螢幕寬。
  - Chromium＋Android UA：widget 走 `jq.innerHeight()`，貼底。
  - WebKit＋iPad：`window.innerHeight` 本來就等於可視區，iOS 分支貼底。
  - 只有 Chromium＋iPad UA＋`isMobile` 的混合組合會沉下去，真實裝置不會有這個組合。
- **另一個條件會重現：先放大再點開。**
  - 量測：Chromium＋Android UA 在 scale 2 時，四種 sheet 都看不到，與頁寬無關，IceBlue 與 Marble 都一樣。
  - 未驗證：真實 iOS Safari 放大時 `window.innerHeight` 是否反映 visual viewport。若是，iOS 分支在 iPad 上可能是對的；這正是 ZK-2826 註解所說的情境。
  - 若要開 bug，題目應該是「sheet 依 layout viewport 而非 visual viewport 定位，放大後點開看不到」，而不是「頁面比螢幕寬」。

## 4. 沒有驗證的部分

- **真實 iOS Safari / iPadOS 沒有測。**
  - Playwright WebKit 是 macOS 上的 WebKit build，不是 iOS Safari：沒有 iOS 的 visual viewport／動態工具列／自動縮放邏輯。它縮小了問題範圍，但沒有結案。
  - 問題 1 在 WebKit 重現，在 iOS 上的跳動量可能不同。
  - 問題 2 的「WebKit `innerHeight` = 可視區」在 iOS 上未確認，尤其是放大與工具列收合時。
- **真實 Android 平板沒有測。** Chromium emulation 與 Chrome Android 共用 viewport 程式，但沒有實機確認放大時 `window.innerHeight`／`jq.innerHeight()` 的值。
- WebKit 無法用 Playwright 做 pinch，所以 iPad 的放大情境沒有量。
- 縮放用的是 CDP `Emulation.setPageScaleFactor`，不是真實的兩指手勢。
- 只在 scroll 0 與先捲 150（問題 1）兩種起始捲動量下量過。問題 2 沒有量「先垂直捲動再點」：widget 會加 `scrollY`，問題 1 的 `preventScroll` 對照列已顯示捲 150 時 sheet 仍貼底。
- 問題 1 在「頁面剛好不夠高」（bare `<colorbox/>`）時有 484 的暫時捲動又彈回 0，畫面是否閃動沒有錄影確認。
- 沒有在已發佈的 ZK 版本（例如 10.x EE）上測，只測了 master 的 11.0.0-SNAPSHOT。

## 5. 圖片

資料夾：`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/028a7b57-cfcf-44e9-a25e-5d68a4869b5a/scratchpad/repro/images/`。每張都是可見畫面（不是 full page）。

| 檔案 | 內容 |
|---|---|
| `p1-iceblue-android-834-jump585.png` | IceBlue，Android：頁面捲 585，colorbox 不見，sheet 浮在上半部 |
| `p1-iceblue-android-834-preventScroll-nojump.png` | 同上，只把那次 focus 改成 `preventScroll`：sheet 貼底、頁面不動 |
| `p1-iceblue-android-834-without-za11y-nojump.png` | 不載入 za11y：貼底、不動 |
| `p1-iceblue-webkit-ipadpro11-jump626.png` | WebKit iPad Pro 11：捲 626 |
| `p1-iceblue-webkit-ipadpro11-preventScroll-nojump.png` | WebKit，`preventScroll`：貼底 |
| `p1-iceblue-webkit-ipadpro11-landscape-jump446.png` | WebKit 橫向：捲 446，sheet 上緣被切 |
| `p1-iceblue-desktop-ua-no-touch-mold.png` | Desktop UA：一般 palette，沒有 tablet mold |
| `p1-marble-android-834-nojump.png` | Marble tip：fixed sheet 貼底，不跳 |
| `p2-iceblue-chromium-ipadUA-600-w900-combobox-below566.png` | 混合組合（Chromium＋iPad UA），頁寬 905：sheet 看不到 |
| `p2-iceblue-chromium-androidUA-600-w900-combobox-ok.png` | Chromium＋Android UA，同頁：貼底 |
| `p2-iceblue-webkit-ipadpro11-600-w900-combobox-ok.png` | WebKit iPad Pro 11，同頁：貼底 |
| `p2-iceblue-chromium-androidUA-600-w900-zoomout-combobox-floats.png` | Android，開著時縮到最小：sheet 浮起但完整可見 |
| `p2-iceblue-chromium-androidUA-600-fit-zoom2-combobox-offscreen.png` | Android，fit 頁放大 2 倍後點開：只看到 combobox，sheet 在畫面外 |
| `p2-marble-chromium-androidUA-600-fit-zoom2-combobox-offscreen.png` | Marble tip，同條件：同樣看不到 |

## 6. 清理與狀態

伺服器（只有自己的；8085 與其他 port 沒有碰）：

| 次序 | port／worktree | PID |
|---|---|---|
| 1 | 8107 `repro/zk/zktest` | sh 63148/63150、`tail -f /dev/null` 63151、gradle client 63152、daemon 63185、Jetty 64167 |
| 2 | 8107（`-PzktestWithoutA11y=true`） | sh 98288/98290、tail 98294、client 98295、daemon 98320、Jetty 99217 |
| 3 | 8087 `p3/zk/zkpreview` | sh 2555/2558、tail 2562、client 2563、daemon 2605、Jetty 3616 |

- 每次都只 kill 自己的 `tail` holder，appRun 正常結束（`BUILD SUCCESSFUL`），列出的 PID 都已消失，8107、8087 都沒有在 listen。
- Repro 頁刪除後的 HTTP 狀態：
  - `/zktest/pv/{p1,p1-final,p2,p2-final,p2-oneitem}.zul` 都是 404（8107 停止前確認），`build/inplaceWebapp/pv/` 目錄也已刪除。
  - p3 的 `/pv/zzrepro-p1.zul`、`/pv/zzrepro-p2.zul`、`/web/pv/…` 都是 404。

`git status --porcelain`：

- `p3/zk`（`3539d1711d`），前後相同：
  ```
  ?? node_modules
  ?? zkpreview/node_modules
  ```
- `p3/zkcml`（`c863e278b`）：前後都是空的。
- `repro/zk`：建立後為空，結束時 `?? node_modules`。這是 `npm ci` 的結果：marble 的 node_modules 缺 master 需要的 `gulp-postcss`、`zklessc`，不能用 symlink。
- `repro/zkcml`：建立後為空，結束時 ` M lib/spel2js/package-lock.json`。
  - 來源：`:client-bind:spel2jsInstall` 在 build 時跑 npm install，拿掉 6 個 `"peer": true`。
  - 這是 build 的副作用，沒有還原，留給 owner 決定。

新建的東西（都保留，由 owner 決定是否刪除）：
- worktree `/Users/hawk/Documents/workspace/ZK10/repro/zk`、`/Users/hawk/Documents/workspace/ZK10/repro/zkcml`（`git worktree add --detach … master`）。
- 兩個 worktree 各自的 `node_modules`（`npm ci`）、build 輸出。
- 沒有安裝任何 Playwright 瀏覽器（WebKit 2272 原本就在 cache）。

## 7. 可公開的描述

以下不含私有 repository 的路徑或程式碼，可作為日後 Jira 草稿的素材。

**A. Tapping a colorbox on a tablet scrolls the page (accessibility add-on)**

- Environment: ZK 11.0.0-SNAPSHOT EE, default IceBlue theme, tablet UI enabled, accessibility add-on (za11y) on the classpath.
- Browser: an Android tablet user agent in Chromium. Also reproduces in WebKit with an iPad device profile.
- Page:
  ```xml
  <zk>
  	<colorbox/>
  	<div height="2000px"/>
  </zk>
  ```
- Steps: tap the colorbox.
- Result:
  - The palette sheet first opens just below the visible area and then slides up.
  - Before it slides up, the add-on moves focus to the palette's mode button without preventing the scroll.
  - The browser scrolls the page so that this button is centred: 585px on an 834×1112 screen, 626px on an iPad Pro 11 profile.
  - The colorbox leaves the screen, and the sheet ends up in the middle of the screen instead of at the bottom.
- Does not happen:
  - without the accessibility add-on;
  - with a desktop user agent;
  - when the page is not tall enough to scroll.
- Making that one focus call non-scrolling (`preventScroll`) also removes it.

**B. Tablet bottom sheets open outside the visible area after zooming in**

- Environment: ZK 11.0.0-SNAPSHOT EE, IceBlue or Marble, tablet UI enabled, Android tablet user agent in Chromium.
- Page:
  ```xml
  <zk>
  	<combobox>
  		<comboitem label="Item 1"/><comboitem label="Item 2"/><comboitem label="Item 3"/><comboitem label="Item 4"/>
  		<comboitem label="Item 5"/><comboitem label="Item 6"/><comboitem label="Item 7"/><comboitem label="Item 8"/>
  	</combobox>
  </zk>
  ```
  The same happens with `<datebox/>`, `<timebox/>` and `<colorbox/>`.
- Steps: pinch-zoom in to 2×, then tap the combobox button.
- Result:
  - The sheet is placed at the bottom of the layout viewport (1112px) while only 556 CSS px are visible.
  - The sheet is not visible.
- The sheet's position is computed from the window height and scroll offset, not from the visual viewport.
- Not verified on a real device or in iOS Safari.

**C. Pages wider than the screen**

- Pages wider than the screen (for example `<div width="900px" height="1px"/>`) did not misplace the sheet in any real browser/user-agent combination we could run: Chromium with an Android UA, and WebKit with an iPad profile.
- It happened only when Chromium was given an iPad user agent, which no real device has.
