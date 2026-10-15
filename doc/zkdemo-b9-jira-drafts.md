# zkdemo B9：ZK 核心 Jira 草稿（已送出：ZK-6206–ZK-6212；Z13 另由 ZK-6205 送出）

狀態：草稿。依 E6，使用者同意前不建立、不編輯、不留言任何 Jira issue。本檔以唯讀 JQL（zkoss.atlassian.net，cloudId `2d12dac9-eeac-42e9-a6bc-c8ad849565bb`）與 CaseFoundry `search_cases` 檢查重複。

來源：`doc/zkdemo-b9-triage.md` 的「ZK 核心」表、zkdemo `tasks/new-demo/p4-b9-plan.md` §B9-Z。原始碼依 `git show master:<path>` 取行號：zk master `85c7731da4`、zkcml master `1679dd566`（`gradle.properties` version = 11.0.0-SNAPSHOT）。zkdemo 的探針腳本與 log 在另一個 session 的 scratchpad，本 session 只讀不改。2026-10-15 獨立 verifier 在 Marble 預覽伺服器（`http://127.0.0.1:8087`，Playwright Chromium、真實滑鼠）重現了 Z13（B1）、JHLabs（B2）、Z5（B3）、B4 pdfviewer，數值與截圖見 `doc/zkdemo-b9-verification.md` Part B，截圖在 `doc/zkdemo-b9-core-evidence/`（以下簡稱 `ev/`）。Z2、Z3、Z4、Z10 沒有重現，「信心」仍指原始碼層面的確認程度。重現是在 Marble 上跑的；是否兩主題共有，各節分別寫明依據。

專案：`ZK`（Bug）。Affects Version/s：11.0.0。另外 v10.3.1 tag（最新的 10.x tag）也有相同程式碼（已逐項 `git show v10.3.1:<path>` 比對，Z13、Z2、Z3、Z4、Z5、Z10 皆在），但未在 10.x 上實際執行；若要加上 10.3.1，需先在 10.x 重現。送出時要從專案已定義的版本清單查 `11.0.0` 的實際名稱，不要硬寫。

## 摘要表

| 項目 | 建議標題 | 元件 | 重複 issue | 信心 |
|---|---|---|---|---|
| Z13 | fisheyebar: magnification collapses once the page is scrolled past the first viewport (`_onBodyOut` compares pageX/pageY against the `html, body { height: 100% }` box) | Fisheyebar（zkex） | 無（ZK-6031 相關但不同，見下） | 高：B1 在 Marble 重現（`body.offsetHeight` 800、捲動後 mouseout `pageY` 1695；10 次取消）；兩主題共有依原始碼，未在 IceBlue 執行 |
| Z2 | captcha: setNoise() and setFrame() do not redraw the image at runtime | Captcha（zul） | 無 | 高 |
| Z3 | rangeslider: markScale="0" cannot hide the marks, and getMarkScale() cannot return 0 | Rangeslider（zkex） | 無 | 高（Java 側）；zkdoc 的「無窮迴圈」只對字串 `"0"` 成立，見 Z3 筆記 |
| Z4 | rangeslider: marks="10:10%, ..." in ZUL fails with ClassCastException although zul.xsd and the client doc advertise a string | Rangeslider（zkex） | 無 | 高（簽章與 xsd 確認）；500 本身依 zkdemo 的 log |
| Z5 | columnlayout: changing width at runtime does not redistribute the percentage columnchildren | Columnlayout（zkex） | 無（ZK-2309 類似但是 columnchildren visible；已 Fixed 7.0.3） | 高：B3 重現（500px 後欄寬仍 270／630） |
| Z10 | zul.xsd: columnchildren only accepts panel although any component works at runtime | zul.xsd（Columnchildren） | 無（ZK-523 是執行時支援任意子元件，Done 6.0.0，正好說明 xsd 落後） | 高 |
| JHLabs | JHLabsCaptchaEngine: text is cut off at the image edges when the text is wider than the image | JHLabsCaptchaEngine（zkex） | 無 | 高：B2 重現（右緣與左緣）；原因為推論，見該節 |
| B4 pdfviewer | pdfviewer: text layer does not follow the rotation, and `--scale-factor` is not defined so text selection does not match the glyphs | Pdfviewer（zkex） | 無（ZK-4740、ZK-5426 不同，見重複檢查） | 高：B4 重現（rot 90 的 transform 為 none、`--scale-factor` 為空）；兩主題共有依原始碼 |

## 原始碼不支持的主張（先看這裡）

本節原本列出六點；其中 JHLabs 與 Z13、Z5 三點已被 2026-10-15 verifier 的重現（`doc/zkdemo-b9-verification.md` Part B）更新，以下依更新後的狀態寫。

1. **JHLabs（已更正）**：本檔先前的版本寫「左緣超出不可能、先不送」。那個立場只看了 `drawText`（`JHLabsCaptchaEngine.java:163-189`，`left` 從 0 只增不減），**沒有考慮 `distortion()` 的 RippleFilter**。verifier 的 B2 重現推翻了它：窄圖上首字左側筆畫被切（x=1 有墨點 71-72／72），右緣也被切（`90×40` 的 `ABCDEFG` 只看得到 ABCDE）。該立場已移除，JHLabs 改為「已重現」，見 JHLabs 一節。zkdemo 原本的 300 次統計（200×50、4 到 7 字元）沒有切掉，與 B2 不矛盾：那些設定的 `textwidth` 沒有超過圖寬。`$S/captcha-verify.png` 是 broken image 圖示，仍不是證據。
2. **Z3「若送 0 則 redrawMarks_ 無窮迴圈」**：`Rangeslider.ts:395-400` 對字串 `"0"` 確實無窮迴圈（`parseInt('0')` = 0，`!isNaN` 成立，`markVal += 0`）。但伺服器端送出的是整數 `0`（`Rangeslider.java:231` `smartUpdate("marks", _markScale)`），到用戶端 `setMarks(0)` 後 `redrawMarks_` 的 `if (!marks) return;`（`:391`）因為 0 為 falsy 而提早返回，不會迴圈。也就是說：伺服器路徑今天不會迴圈，只有字串 `"0"`（例如用戶端 JS 直接 `setMarks('0')`、或將來把 0 序列化為字串）才會。草稿把它寫成「用戶端缺少 step > 0 的防護」，不寫成「目前可由 ZUL 觸發」。
3. **Z13（已重現，原本的疑慮部分保留）**：B1 已用真實滑鼠量到 10 次 mouseout、10 次 `_calc(-1,-1)`，`jq(document).off('mouseout', w._onBodyOut)` 後為 0 次，所以「拿掉該 handler 就恢復」成立（以第 7 步為例，B 正常 `50x50,50x50,50x50`，移除 handler 後 `90x90,86x86,61x61`，與第一個 viewport 的 A 相同）。仍然不成立的是 zkdemo 舊截圖：`eval-p4-fisheyebar-r3/norm-menu.png` 與 `det-menu.png` 看起來相同且 NORMAL 已放大，**不能當 bug 截圖**；要附的是 `ev/` 內 B1 的三張。Z13 描述原本寫成「不放大」，B1 的實測是「每次跨越元素邊界縮回 50px、下一次 mousemove 又放大」，所以描述不再寫「never magnifies」。未量到的：IceBlue 上的行為（依 `norm.less` 原始碼推論）；「4 個 story 都放大到 112／64px」這個 zkdemo 數字仍沒有證據可引。
4. **Z5（已重現）**：B3 的數值：`width="900px"` 時 columnchildren 為 270／630；真實 click 按鈕執行 `cl.setWidth("500px")` 後 columnlayout 為 500，columnchildren 仍 270／630，第二欄被 `overflow:hidden` 裁掉（只看得到約 230px）。先前引用的 `$S/probe/col3.log`（868px：260／607）是 zkdemo 的另一組量測，不再用於描述。
5. triage 表 Z13 的行號 `Fisheyebar.ts:374-386` 在 master 成立（`_onBodyOut` 為 `:375-386`、`jq(document).on(...)` 為 `:387-388`）。plan 寫的 `:286-293` 是舊行號，不要沿用。
6. 摘要表「Z4 回 500」：ZUL 字串 `marks` → `setMarks(Map<Integer,String>)` 的 `ClassCastException` 來自 zkdemo 的 Jetty log；我確認了簽章（`Rangeslider.java:250` 只有 `Map` 版本）與 xsd（`zul.xsd:3041` 為 `xs:string`），沒有自己重現（B1-B4 也不含 Z4）。
7. **B4 pdfviewer（新增）**：「文字層與選取對不上」的原因（`--scale-factor` 缺失、旋轉 transform 缺失）是 verifier 以注入 `--scale-factor: 1` 對照得到的，並以原始碼（pdf.js `setLayerDimensions`、`data-main-rotation`）解釋；pdf.js 的 `pdf_viewer.css` 沒有在 ZK 內被載入這件事，是以 `git grep` 無 `scale-factor`／`data-main-rotation` 命中（`ext/` 以外）推得。

## 重複檢查方式

JQL（唯讀，`project = ZK`；B4 新增的兩條見下方 B4 一節）：`text ~ "fisheyebar" OR text ~ "fisheye"`（6 筆）、`text ~ "rangeslider" OR text ~ "markScale"`（約 12 筆）、`text ~ "captcha" OR text ~ "JHLabs"`（4 筆）、`text ~ "columnlayout" OR text ~ "columnchildren"`（約 8 筆）、`summary ~ "zul.xsd" ...`（20 筆，另有下一頁）。再逐筆讀可疑項的內文。CaseFoundry `search_cases` 四次（fisheyebar 捲動、rangeslider marks、captcha noise/frame、columnlayout width）。

候選與判定：

| 候選 | 狀態 | 判定 |
|---|---|---|
| ZK-6031 fisheyebar `attachEdge="bottom"` doesn't work as expected | Open（2025-12-05） | 不同。它回報 `attachEdge="bottom"` 時元件仍畫在頁面上方（位置），Z13 是頁面捲動後放大失效（`_onBodyOut`）。兩者共用 Fisheyebar，修的時候可一併看，但不是同一個缺陷。建議在 Z13 的 Debug Info 提到它 |
| ZK-6192 rangeslider / multislider vertical tooltip cut off | Open（2026-10-13） | 不同（tooltip 被裁切） |
| ZK-5491 Multislider and Rangeslider aria-* | Closed | 不同 |
| ZK-4462、ZK-4463 rangeslider style／按鈕位置 | Closed | 不同 |
| ZK-2309 Columnlayout not resize its children after changing columnchildren's visibility | Closed，Fixed 7.0.3 | 類似但不同。它是 columnchildren `visible` 改變；Z5 是 columnlayout 自己的 `width` 改變。當年的修法（`Columnchildren.setWidth`／`setStyle` 呼叫 `fireSized(parent)`）也沒有涵蓋 Columnlayout 自己的 `setWidth` |
| ZK-2106 Columnlayout resize problem | Closed，Fixed 7.0.2 | 不同（視窗縮放換行） |
| ZK-4665 percent width in Columnlayout causes line break (Firefox/Edge) | Closed | 不同；CaseFoundry `GH-f70a64c6`、`GH-6a1f0d05` 是它的 revert，`GH-ed3282f5` 是引入 `_oriWidth` 的修法，說明 `_oriWidth`／`render()` 的來歷 |
| ZK-523 Columnlayout support any child not limited to Panel | Closed，Done 6.0.0 | 與 Z10 相關：執行時已支援任意子元件，xsd 沒跟上。不是重複 |
| ZK-2907 There is no element defined for `<captcha>` in zul.xsd | Closed | 不同（缺元素定義） |
| ZK-2908 hflex/vflex not working in Captcha | Closed | 不同 |
| ZK-1293 Captcha warnings in CE version when building a GAE project | Open | 不同 |
| ZK-6189、ZK-6184、ZK-6179、ZK-6177、ZK-6172（zul.xsd 驗證失敗，Open／Reviewing） | Open／Reviewing | 同一類（zul.xsd 與執行時不一致），但元件不同，沒有 columnchildren。Z10 可在 Debug Info 引為同系列，送出時建議用同樣的標題格式 `zul.xsd: ... fails validation` |
| ZK-6079 zul.xsd: toolbar only allows one child | Closed | 同系列，不同元件 |
| CaseFoundry `GH-c4947940` rangeslider `markScale` 方法被寫成 getter | 已修（commit） | 不同；是 stateless／client-bind 的 `markScale` 呼叫 `this.marks()` 的修正 |
| CaseFoundry `GH-6d116b39` captcha 邊框檢查 `isBorder()` 改 `isFrame()` | 已修（commit） | 不同；說明 `frame` 在引擎端有人修過，但與 Z2 的「不重畫」無關 |
| CaseFoundry `GH-7e94034b` Fisheyebar orient 動態改變未重排 | 已修（commit） | 不同 |

B4 的重複檢查（2026-10-15，Atlassian MCP `searchJiraIssuesUsingJql`，唯讀）見 B4 一節；ZK-4740（auto rotate，Done）、ZK-5426（pdfviewer cannot display some texts，Fixed）列為不同。

結論：八項都沒有找到重複 issue。

截圖說明：八項中，Z13、Z5、JHLabs、B4 屬於視覺類。verifier 已在 `doc/zkdemo-b9-core-evidence/` 保存了這四項的症狀截圖（各節列出檔名，皆複製自 `ls`）。zk-bug-filing 的規則是視覺 bug 必須附截圖，而 Atlassian MCP 不能上傳附件，所以送出前由使用者手動把各節列出的 PNG 附到 issue；描述內不寫本機路徑。其餘（Z2、Z3、Z4、Z10）是伺服器行為或 schema，不需要截圖。

---

## Z13

### 備註（繁中）

- 原始碼確認：`zkex/src/main/resources/web/js/zkex/menu/Fisheyebar.ts` master `:374-388`。`_onBodyOut` 掛在 `document` 的 `mouseout`（會冒泡，每個元素的 mouseout 都會進來）。條件是 `x >= l && x <= r && y >= t && y <= b` 才 return，否則 `this.active = false; this._calc(-1, -1)`。`l/t` 取自 `zk(BODY).revisedOffset()`，`r/b` 是 `+ BODY.offsetWidth/offsetHeight`。
- `html, body { height: 100% }` 確認：IceBlue `zul/src/main/resources/web/zul/less/norm.less:7-9`（master），Marble `zul/src/main/resources/web/zul/css/base/_reset.css:29`（marble 分支）。所以 BODY 盒高等於視窗高，文件較高時，捲動後 `pageY`（含捲動量）超過 BODY 盒的下緣，每次 mouseout 都 `_calc(-1, -1)`。兩個主題都會發生。
- 與 `_onMouseMove`（`:342-370`）的互動：mousemove 會重新放大，mouseout 立刻取消。
- **B1 重現（取代原本的「信心：中、未重現」）**：verifier 在 Marble 預覽（viewport 1280×800，文件高 3260）實測：`document.body.offsetHeight` = 800，`zk(BODY).revisedOffset()` = [0,0]；fisheyebar 在捲動 1320px 後，mouseout 的 `pageY` = 1695，大於 800。滑鼠橫掃 3 個 item（21 步）：第一個 viewport 的 fisheyebar（A）0 次取消，最大 item 寬序列 `64,71,79,86,94,99,94,90,91,…`；捲動後的 fisheyebar（B）**10 次 mouseout、10 次 `_calc(-1,-1)`**，在每次跨越元素（item）邊界時縮回 50（序列 `64,50,50,86,94,99,94,50,91,…,50,…`），下一次 mousemove 又放大。所以使用者看到的是閃爍或縮回，不是「完全不放大」（因此描述不寫 never）。`jq(document).off('mouseout', w._onBodyOut)` 後 B 為 0 次取消，序列與 A 相同。第 7 步 item 尺寸：B `50x50,50x50,50x50`，B 移除 handler `90x90,86x86,61x61`，A `90x90,86x86,61x61`。
- 兩主題共有：依原始碼成立（`Fisheyebar.ts` 與 master 無差異；IceBlue `norm.less`（master）有 `html, body { height: 100% }`），**未在 IceBlue 執行**。
- 建議修法：用 `evt.clientX/clientY` 對 `0..window.innerWidth/innerHeight`，或「`evt.relatedTarget` 為 null（指標離開文件）才取消」。後者更接近原意（只有離開頁面才重置）。
- 版本：Affects 11.0.0；v10.3.1 的 `Fisheyebar.ts:375` 有同一段（未在 10.x 執行）。
- 截圖（要附，檔案在 `doc/zkdemo-b9-core-evidence/`）：`b1-z13-B-scrolled-normal.png`（捲動後，收合）、`b1-z13-B-scrolled-onBodyOut-detached.png`（同一位置，`off('mouseout', _onBodyOut)` 後放大）、`b1-z13-A-first-viewport-normal.png`（第一個 viewport 的對照）。舊的 `norm-menu.png`／`det-menu.png` 仍**不要附**。

### Jira 標題

fisheyebar: magnification is cancelled on every mouseout once the page is scrolled past the first viewport

### Jira 描述（可貼上）

# Steps to Reproduce

```xml
<zk>
    <div height="1500px">Scroll down to the fisheyebar below.</div>
    <fisheyebar id="fsb" itemWidth="50" itemHeight="50" itemMaxWidth="100" itemMaxHeight="100">
        <fisheye label="Folder" image="~./img/Centigrade-Widget-Icons/FolderABlue-128x128.png" />
        <fisheye label="Project" image="~./img/Centigrade-Widget-Icons/Briefcase-128x128.png" />
        <fisheye label="Email" image="~./img/Centigrade-Widget-Icons/MailboxFlag-128x128.png" />
    </fisheyebar>
    <div height="1500px"/>
</zk>
```

1. open the page and scroll until the fisheyebar is in the middle of the viewport (the page must be scrolled by more than the viewport height minus the fisheyebar position, i.e. the fisheyebar is below the first screen of the document)
2. move the mouse over one of the fisheye icons

# Current Result

the magnification is cancelled on every mouseout. While the pointer sweeps across the icons of a scrolled fisheyebar, the icons collapse back to their original 50px each time the pointer crosses an element boundary, and magnify again on the next mousemove, so the user sees flicker or a collapsed bar (in our run, 10 mouseout events caused 10 cancellations over one sweep across three icons; see `fisheyebar-scrolled-collapsed.png`). The same page with the fisheyebar placed in the first viewport (not scrolled) magnifies normally, with 0 cancellations (`fisheyebar-first-viewport-magnified.png`). After `jq(document).off('mouseout', wgt._onBodyOut)` the scrolled fisheyebar also has 0 cancellations and magnifies like the one in the first viewport (`fisheyebar-scrolled-handler-removed.png`).

# Expected Result

the hovered icon and its neighbours stay magnified while the pointer is over the fisheyebar, regardless of the page scroll position.

# Debug Info

`Fisheyebar._onBodyOut` is registered as a `mouseout` listener on `document` and cancels the magnification (`this.active = false; this._calc(-1, -1)`) unless the pointer is inside the BODY box. The box is `zk(BODY).revisedOffset()` plus `BODY.offsetWidth` / `BODY.offsetHeight`, and it is compared with `evt.pageX` / `evt.pageY` (document coordinates) — https://github.com/zkoss/zkcml/blob/master/zkex/src/main/resources/web/js/zkex/menu/Fisheyebar.ts#L375-L386

Both themes set `html, body { height: 100% }` (IceBlue `zul/src/main/resources/web/zul/less/norm.less` lines 7-9, Marble `_reset.css`), so `BODY.offsetHeight` equals the viewport height, not the document height (measured: `body.offsetHeight` 800 in an 800px-high viewport, document height 3260). Once the page is scrolled, `pageY` is larger than that box for every mouseout (measured: 1695 after scrolling 1320px), so the handler cancels the magnification on every mouseout that bubbles to `document`.

Suggested fix: compare `evt.clientX` / `evt.clientY` with `0..window.innerWidth` / `0..window.innerHeight`, or only cancel when the pointer really leaves the document (`evt.relatedTarget == null`).

Related but different: ZK-6031 (attachEdge="bottom").

<!-- 手動附件（來自 doc/zkdemo-b9-core-evidence/，附件檔名可沿用描述裡的名稱）：
  b1-z13-B-scrolled-normal.png -> fisheyebar-scrolled-collapsed.png
  b1-z13-A-first-viewport-normal.png -> fisheyebar-first-viewport-magnified.png
  b1-z13-B-scrolled-onBodyOut-detached.png -> fisheyebar-scrolled-handler-removed.png
  不要用 norm-menu.png / det-menu.png。截圖為 Marble 主題、Chromium。 -->

Jira 欄位：Project ZK、Type Bug、Component（若有）Fisheyebar、Affects Version/s 11.0.0。

---

## Z2

### 備註（繁中）

- 原始碼確認：`zul/src/main/java/org/zkoss/zul/Captcha.java` master `:294-297`（`setNoise` 只寫 `_noise`）、`:308-311`（`setFrame` 只寫 `_frame`）。同檔的 `setFontColor`（`:127`）、`setBgColor`（`:158`）、`setWidth`（`:186`）、`setHeight`（`:213`）、`setValue`（`:250`）、`setLength`（`:263`）、`setExclude`（`:283`）、`setEngine`（`:357`）都呼叫 `smartDrawCaptcha()`（`:408`）。引擎端 `JHLabsCaptchaEngine.java:128-141` 確實用 `captcha.isNoise()` 與 `isFrame()`，所以這兩個欄位會影響圖片。
- 實測依據：zkdemo `$S/probe/cap1.log`（改 `noise`、`frame` 後 `src` 仍是 `/2/`，改 `fontColor` 才變 `/3/`）。我讀原始碼確認，沒有自己重現。
- 行為上「建構時」寫在 ZUL（`<captcha noise="true"/>`）有效，因為屬性在第一次繪圖前設好；只有元件已建立後才改才失效。
- Jira 的 `@since`：`Captcha.java` 註解 `@since 5.0.0`（noise）、`5.0.4`（frame），所以十年以上都有；v10.3.1 的 `Captcha.java` 同樣只有 `_noise = b;`（已比對）。
- 重複：無。

### Jira 標題

captcha: setNoise() and setFrame() do not redraw the image when called after the component is created

### Jira 描述（可貼上）

# Steps to Reproduce

```xml
<zk>
    <captcha id="cap" value="ABCDE"/>
    <button label="noise on" onClick="cap.setNoise(true)"/>
    <button label="frame on" onClick="cap.setFrame(true)"/>
    <button label="font color" onClick='cap.setFontColor("#CC3300")'/>
</zk>
```

1. open the page and note the captcha image
2. click "noise on", then "frame on"
3. click "font color"

# Current Result

after step 2 the image does not change (its `src` URL stays the same, e.g. `.../2/...`). After step 3 the image is redrawn and now also shows the noise and the frame that were set earlier.

# Expected Result

the image is redrawn right after `setNoise()` / `setFrame()`, like it is after `setFontColor()`, `setBgColor()`, `setLength()`, `setExclude()` or `setValue()`.

# Debug Info

`Captcha.setNoise()` and `Captcha.setFrame()` only assign the field; every other property setter calls `smartDrawCaptcha()` — https://github.com/zkoss/zk/blob/master/zul/src/main/java/org/zkoss/zul/Captcha.java#L294-L311 (compare with `setFontColor` at #L127 and `smartDrawCaptcha` at #L408). The engine reads both flags when it draws (`JHLabsCaptchaEngine.drawCaptcha`, `isNoise()` / `isFrame()`).

Suggested fix: call `smartDrawCaptcha()` in both setters when the value changes.

# Workaround

call any property that triggers a redraw after changing the flags, for example `cap.randomValue()` or re-apply the font color.

Jira 欄位：Project ZK、Type Bug、Component Captcha、Affects Version/s 11.0.0。

注意：Workaround 段的 `cap.randomValue()` 會換掉 `value`；`randomValue()` 內部是否呼叫 `smartDrawCaptcha()` 我沒有再查（`setValue` 有），送出前請確認或刪掉這段。

---

## Z3

### 備註（繁中）

- 原始碼確認（zkcml master，`zkex/src/main/java/org/zkoss/zkex/zul/Rangeslider.java`）：
  - `:45` `private int _markScale = 0;`
  - `:210-212` `getMarkScale()` 回 `_markScale == 0 && _marks == null ? 20 : _markScale`，所以永遠讀不到 0。
  - `:224-233` `setMarkScale`：`_markScale != markScale` 才 `smartUpdate("marks", _markScale)`。
  - `:336-339` `renderProperties`：`_marks` 非空送 `marks`；`else if (_markScale != 0)` 才送 `markScale`；0 時什麼都不送。
  - 用戶端 `Rangeslider.ts:34` `_marks = '20'`，沒收到值就維持 20。
- 結果：ZUL 寫 `markScale="0"`：`_markScale` 本來就是 0，`setMarkScale(0)` 不會觸發 `smartUpdate`（沒變），`renderProperties` 也不送，所以用戶端畫 20 的刻度。**執行中從 20 改成 0** 反而有效（送出整數 0，用戶端 `if (!marks) return;`（`Rangeslider.ts:391`）清空刻度）。所以「初始 0」與「執行中改 0」行為不一致。文件（zkdoc）寫「If the MarkScale is 0 and there is no Map information in Marks」會隱藏刻度，所以文件說的是對的，實作在初始狀態不符。
- zkdemo 實測：`$S/probe/rng1.log` 第一列 `"marks":"20"`（`markScale` 前後都沒有 `v`），不是我跑的。
- zkdoc 補充的「無窮迴圈」：見上方「原始碼不支持的主張」第 2 點。草稿只寫為「用戶端未防護 step <= 0」，並說明現在的伺服器路徑不會送字串 `"0"`。
- v10.3.1：`Rangeslider.java` 同樣有 `:338 else if (_markScale != 0)`、`:231`（已比對）。
- 重複：無（CaseFoundry 的 `GH-c4947940` 是另一件事）。

### Jira 標題

rangeslider: markScale="0" does not hide the marks, and getMarkScale() never returns 0

### Jira 描述（可貼上）

# Steps to Reproduce

```xml
<zk>
    <rangeslider id="rs" min="0" max="100" markScale="0" width="400px"/>
    <button label="read markScale" onClick='alert(rs.getMarkScale())'/>
</zk>
```

1. open the page
2. look at the marks under the track
3. click the button

# Current Result

the marks 0, 20, 40, 60, 80, 100 are displayed, and the button shows 20.

# Expected Result

no marks are displayed, as documented ("if markScale is 0 and there is no marks map, no marks are shown"), and `getMarkScale()` returns 0.

Setting `markScale` to 0 after the page is rendered (for example `rs.setMarkScale(20); ... rs.setMarkScale(0);` from a listener) does hide the marks, so the initial and the runtime behaviour differ.

# Debug Info

`Rangeslider._markScale` defaults to 0, which also means "not set":

- `getMarkScale()` returns 20 when `_markScale == 0 && _marks == null` — https://github.com/zkoss/zkcml/blob/master/zkex/src/main/java/org/zkoss/zkex/zul/Rangeslider.java#L210-L212
- `setMarkScale(0)` is a no-op at creation time because `_markScale` is already 0 (#L224-L233)
- `renderProperties` skips `marks` when `_markScale == 0` (#L336-L339), so the client keeps its default `_marks = '20'` (`zkex/.../slider/Rangeslider.ts` line 34)

There is also no guard on the client for a step of 0: `Rangeslider.redrawMarks_` parses a numeric string with `parseInt` and loops `while (markVal <= max) { ...; markVal += markStep; }` — https://github.com/zkoss/zkcml/blob/master/zkex/src/main/resources/web/js/zkex/slider/Rangeslider.ts#L395-L403. If the string `"0"` ever reaches `setMarks` (for example `wgt.setMarks('0')` from client code), the loop never ends. The server currently sends the integer 0 for a runtime change, which is falsy and returns early at #L391, so this is a latent problem and not reproducible from ZUL today.

Suggested fix: remember whether `markScale` was set explicitly (for example a nullable `Integer`, or a separate flag) so that an explicit 0 is rendered (`marks: 0`) and read back; and make `redrawMarks_` return when `markStep <= 0`.

# Workaround

none for the initial state. At runtime, set the scale to a non-zero value first and then to 0 after the component is rendered.

Jira 欄位：Project ZK、Type Bug、Component Rangeslider、Affects Version/s 11.0.0。

注意：Workaround 的「先設非 0 再設 0」只是依原始碼推論（`smartUpdate` 在值改變時送 0），我沒有執行驗證；送出前請確認或刪掉。

---

## Z4

### 備註（繁中）

- 原始碼確認：`zul/src/main/resources/metainfo/xml/zul.xsd` master `:3041` `<xs:attribute name="marks" type="xs:string" use="optional" />`（`rangesliderType`，`:3036-3050`）；`Rangeslider.java:250` `setMarks(Map<Integer, String> marks)` 是唯一的 `setMarks`；用戶端 `Rangeslider.ts:116` `setMarks(marks: string, ...)` 與 `:108-112` 的 JSDoc 寫 `<rangeslider marks="10:10%, 20:20%, 50:50%, 80:80%">`。伺服器沒有字串版本，所以 ZUL 的字串無法轉成 Map。
- 實測：zkdemo 探針 `range3.zul` 與 Jetty log 得 500 `ClassCastException: class java.lang.String cannot be converted to interface java.util.Map.`；`$S/probe/rng1.log` 的 `marks Acme => ERR`。我沒有自己跑。
- 另一個層次：即使有字串版本，用戶端 `redrawMarks_` 的 `else` 分支（`:405-407`）是對 `marks as String` 做 `for (const markVal in marks)`，那是在迭代**物件**（Java Map 序列化成的 JSON 物件）。若有人直接給字串 `"10:10%"`，`parseInt("10:10%")` 是 10，會走「純數字」分支，被當成 step 10。所以 JSDoc 的「字串形式」在用戶端也不會被解析成自訂標籤。需要同時修文件或實作。
- zkdoc 已把文件改為 composer 設 Map（`d1322d3`），但 `zul.xsd` 與 `Rangeslider.ts` JSDoc 仍在。
- v10.3.1：`zul.xsd`、`Rangeslider.java` 同樣如此（已比對 `Rangeslider.java:255` 為 `smartUpdate("marks", _marks)`；xsd 比對的是 columnchildren，rangeslider 的 `marks` 行未單獨比對 v10.3.1，送出前請確認）。
- 重複：無。

### Jira 標題

rangeslider: marks="10:10%, 20:20%" in ZUL throws ClassCastException, although zul.xsd and the client API doc declare a string

### Jira 描述（可貼上）

# Steps to Reproduce

```xml
<zk>
    <rangeslider min="0" max="100" marks="0:Low, 50:Mid, 100:High" width="400px"/>
</zk>
```

# Current Result

the page fails to render with a 500 error: ClassCastException: class java.lang.String cannot be converted to interface java.util.Map.

# Expected Result

either the string form is accepted and the marks "Low", "Mid", "High" are displayed, or `marks` is not offered as a string attribute (zul.xsd and the doc comment of the client widget) when only a `Map<Integer, String>` is supported.

# Debug Info

- `zul.xsd` declares `marks` as `xs:string` on `rangeslider` — https://github.com/zkoss/zk/blob/master/zul/src/main/resources/metainfo/xml/zul.xsd#L3041
- the client JSDoc of `Rangeslider.setMarks(marks: string)` documents `<rangeslider marks="10:10%, 20:20%, 50:50%, 80:80%">` — https://github.com/zkoss/zkcml/blob/master/zkex/src/main/resources/web/js/zkex/slider/Rangeslider.ts#L100-L123
- the server only has `setMarks(Map<Integer, String>)` — https://github.com/zkoss/zkcml/blob/master/zkex/src/main/java/org/zkoss/zkex/zul/Rangeslider.java#L250. A ZUL attribute value is a String, so the setter cannot be called from ZUL.
- the client treats a string that starts with a number as a step (`parseInt(marks)` at https://github.com/zkoss/zkcml/blob/master/zkex/src/main/resources/web/js/zkex/slider/Rangeslider.ts#L395), so the documented string form would not be parsed into labels on the client either.

Suggested fix: add `setMarks(String)` on the server that parses `value:label` pairs into a `LinkedHashMap<Integer, String>` (and document the format), or remove `marks` from `rangesliderType` in zul.xsd and correct the JSDoc to describe the Map.

# Workaround

set the marks in a composer:

```java
Map<Integer, String> marks = new LinkedHashMap<>();
marks.put(0, "Low");
marks.put(50, "Mid");
marks.put(100, "High");
rangeslider.setMarks(marks);
```

Jira 欄位：Project ZK、Type Bug、Component Rangeslider（+ zul.xsd）、Affects Version/s 11.0.0。

---

## Z5

### 備註（繁中）

- 原始碼確認（zkcml master）：
  - `zkex/.../layout/Columnlayout.ts:28-56` `render()` 以 `cmp.offsetWidth` 減去 px 欄寬，再把 `%` 欄設成 `floor(百分比 / 100 * total)`；`:58-73` 只在 `onSize`、`onChildAdded_`、`onChildRemoved_` 呼叫 `render()`。沒有覆寫 `setWidth`、`setHeight`、`setStyle`。
  - `zk/src/main/resources/web/js/zk/widget.ts:1202-1211` `Widget.setWidth` 只改 `style.width`，沒有 `fireSized`。
  - 對照 `Columnchildren.ts:67-74`：`setWidth`／`setStyle` 會更新 `_oriWidth` 並 `zUtl.fireSized(this.parent!, -1)`，所以改 columnchildren 的 width 會重新分配；改 columnlayout 自己的 width 不會。
- **B3 量測（verifier，Marble，真實 click）**：30%／70%、`width="900px"`：columnchildren 270／630（inline `270px`／`630px`）。點按鈕 `cl.setWidth("500px")` 後 columnlayout 500，columnchildren 仍 270／630，第二欄被 `overflow:hidden` 裁掉（只看得到約 230px）。另有 zkdemo 的舊量測 `$S/probe/col5.log`：改 columnchildren 為 60%／40% 後 520／347 正確重分配（對照用）。
- 與已關閉的 ZK-2309（columnchildren 的 `visible` 改變，Fixed 7.0.3）不同；CaseFoundry `ZK-edd4b5d6` 是「動態新增 columnchildren」的測試，也不同。
- 這項是視覺類：第二欄被裁切。截圖（要附，在 `doc/zkdemo-b9-core-evidence/`）：`b3-z5-columnlayout-before.png`、`b3-z5-columnlayout-after-setWidth-500px.png`。「改寬後 `invalidate()` 正常」沒有截圖也沒有實測，見檔尾注意事項。
- v10.3.1：`Columnlayout.ts` 同樣沒有 `setWidth`（`:52` `oriWidth.indexOf('%')` 已比對存在）。

### Jira 標題

columnlayout: changing the width of the columnlayout at runtime does not recalculate the width of percentage columnchildren

### Jira 描述（可貼上）

# Steps to Reproduce

```xml
<zk>
    <button label="width 500px" onClick='cl.setWidth("500px")'/>
    <columnlayout id="cl" width="900px" style="border: 1px solid #888; overflow: hidden;">
        <columnchildren width="30%">
            <panel title="30%" border="normal"><panelchildren>left</panelchildren></panel>
        </columnchildren>
        <columnchildren width="70%">
            <panel title="70%" border="normal"><panelchildren>right</panelchildren></panel>
        </columnchildren>
    </columnlayout>
</zk>
```

1. open the page (the columns are 30% / 70% of 900px)
2. click the button

# Current Result

the columnlayout becomes 500px wide, but the two columnchildren keep their old pixel widths (270px and 630px, as measured for the 900px layout, see `columnlayout-after-setwidth-500px.png`). The second column is cut off by the layout border (`overflow: hidden`) and only about 230px of it is visible. Before the click the columns are 270px and 630px (`columnlayout-before.png`).

# Expected Result

the columns are recalculated to 30% / 70% of the new width, like they are when the width of a columnchildren is changed.

# Debug Info

`Columnlayout.render()` converts the percentage widths to pixels, and it runs only from `onSize`, `onChildAdded_` and `onChildRemoved_` — https://github.com/zkoss/zkcml/blob/master/zkex/src/main/resources/web/js/zkex/layout/Columnlayout.ts#L28-L73. `Columnlayout` does not override `setWidth`, and `zk.Widget.setWidth` only updates `style.width` without firing a size event — https://github.com/zkoss/zk/blob/master/zk/src/main/resources/web/js/zk/widget.ts#L1202-L1211.

`Columnchildren.setWidth` / `setStyle` already call `zUtl.fireSized(this.parent!, -1)` — https://github.com/zkoss/zkcml/blob/master/zkex/src/main/resources/web/js/zkex/layout/Columnchildren.ts#L67-L74, which is why changing the columnchildren width works.

Suggested fix: override `setWidth` (and `setStyle` / `setHeight` if needed) in `Columnlayout` and call `this.render()` (or `zUtl.fireSized(this, -1)`) after the DOM is updated.

# Workaround

invalidate the columnlayout after changing its width: `cl.setWidth("500px"); cl.invalidate();`

Jira 欄位：Project ZK、Type Bug、Component Columnlayout、Affects Version/s 11.0.0。

注意：描述中的 270／630 與 500px 之後不變是 B3 的實測值（Marble）。Workaround 的 `invalidate()` 來自 ZK-2309 的 workaround 文字，B3 沒有在這個案例上執行；送出前請確認或刪掉。附件：`b3-z5-columnlayout-before.png` 與 `b3-z5-columnlayout-after-setWidth-500px.png`，附件檔名可沿用描述裡的 `columnlayout-before.png`、`columnlayout-after-setwidth-500px.png`。

---

## Z10

### 備註（繁中）

- 原始碼確認：`zul/src/main/resources/metainfo/xml/zul.xsd` master `:1728-1736`：`columnchildrenType` 的 `xs:choice` 只有 `baseGroup` 與 `<xs:element ref="panel"/>`。Java 端 `Columnchildren.java`（zkcml master，`zkex/.../zul/Columnchildren.java`）只檢查 parent 必須是 `Columnlayout`，沒有限制子元件。ZK-523（Closed，Done 6.0.0）就是「Columnlayout support any child not limited to Panel」。
- 與 `anyGroup` 的比較：同一個 xsd 裡 `ratingType` 等用的是 `<xs:group ref="anyGroup" .../>`，所以改用 `anyGroup`（或 `baseGroup` 加 `anyGroup`）是現成寫法（`zul.xsd:3043` 起的 `ratingType` 為 `anyGroup`）。
- 實測依據：zkdemo `$S/xsdtest/` 第一次驗證的 Layer 2 錯誤；探針 `column.zul` 放 `label` 正常顯示。我沒有自己跑 xsd 驗證。
- 同系列的 open issue：ZK-6189、ZK-6184、ZK-6179、ZK-6177、ZK-6172（標題格式 `zul.xsd: ... fails validation`）。沒有人回報 columnchildren。
- v10.3.1：`columnchildrenType` 相同（已比對）。
- 重複：無。

### Jira 標題

zul.xsd: columnchildren accepts only panel, so <columnchildren><window/></columnchildren> fails validation

### Jira 描述（可貼上）

# Steps to Reproduce

```xml
<zk xmlns="http://www.zkoss.org/2005/zul"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.zkoss.org/2005/zul http://www.zkoss.org/2005/zul/zul.xsd">
    <columnlayout>
        <columnchildren width="50%">
            <label value="plain label"/>
        </columnchildren>
        <columnchildren width="50%">
            <vbox><label value="in a vbox"/></vbox>
        </columnchildren>
    </columnlayout>
</zk>
```

1. validate the page against `zul.xsd` (for example in an XML editor / IDE that uses the schema)
2. run the page

# Current Result

schema validation reports that `label` and `vbox` are not allowed inside `columnchildren`. The page runs and renders both children correctly.

# Expected Result

the schema accepts any component inside `columnchildren`, because the component does (`Columnchildren` only restricts its parent, not its children).

# Debug Info

`columnchildrenType` allows only `baseGroup` or `panel` — https://github.com/zkoss/zk/blob/master/zul/src/main/resources/metainfo/xml/zul.xsd#L1728-L1736. The component accepts any child since ZK 6.0.0 (ZK-523), and the reference doc examples put `window`, `vbox` and `chart` inside a columnchildren. Same kind of mismatch as ZK-6189, ZK-6184, ZK-6179, ZK-6177 and ZK-6172.

Suggested fix: use `anyGroup` in `columnchildrenType` (like other containers do), keeping `panel` reachable through it.

Jira 欄位：Project ZK、Type Bug、Component（zul.xsd／Columnchildren）、Affects Version/s 11.0.0。

注意：Steps 的第 2 點「run the page」的結果（正確 render）依 zkdemo 的探針 `column.zul`（只放 `label`）；`vbox` 的情況未實測，若要嚴謹請只留 `label`。

---

## JHLabsCaptchaEngine

### 備註（繁中）

- **狀態：已重現（B2，2026-10-15 verifier，Marble 預覽伺服器）。** 本檔先前版本寫「左緣超出不可能」並建議不送；那個立場已被 verifier 的重現更正，不再成立（見「原始碼不支持的主張」第 1 點）。
- 量測（每種尺寸 72 個樣本：6 次載入 × 12，`frame=false`、`noise=false`）：
  - 已知值：`90×40` 的 `ABCDEFG` 每張只看得到 ABCDE（F、G 不見）；`120×50` 的 10 字元（`ABCDEFGHJK`）文字停在 F 或半個 G；`150×40` 的 `ABCDEFGHJ` 只到 H 或半個 I；`200×50` 的 `ABCDE`（預設尺寸、5 字元）6/6 完整。
  - 右緣：最後一欄被引擎的 `drawRect` 蓋掉，所以以 x = W−2 有 ≥2 個墨點為「碰右緣」：90×40×7 11/72、120×50×10 12/72、100×40×8 7/72、150×40×9 4/72、200×50×5 1/72、200×50×7 1/72。
  - 左緣：窄圖 x=1 有墨點 72/72（90×40×7、120×50×10、100×40×8）、71/72（150×40×9）；截圖可見首字左側筆畫被切（例：「ABCDE」的 A 只剩右半）。
- 原始碼（zkcml master，`zkex/src/main/java/org/zkoss/zkex/zul/impl/JHLabsCaptchaEngine.java`；`git diff master` 在此檔為 0 行，marble 分支行號相同）。**原因是推論，沒有逐行跟蹤驗證**：
  - `drawText` `:151-190`：`avgwidth = width / len`（`:163`）；`wgap = avgwidth - charWidth + werror`（`:179`），`wgap <= 0` 時 `werror = wgap` 且不加隨機偏移（`:180-182`、`:184`）；`left += charWidth`（`:187`）；回傳 `left` 作為文字總寬（`:189`）。字元比 slot 寬時只累計 `werror`，沒有縮小字元，所以 `textwidth` 可超過 `width`。
  - `drawCaptcha` `:125-126`：`wgap = width - textwidth; left = wgap <= 0 ? 0 : _random.nextInt(wgap)`。`textwidth > width` 時 `left` 為 0，文字從 x=0 開始畫（`:129` `drawImage(bi, left, 0, null)`），超出的部分在右緣被切。
  - 左緣：`:116` 的 `distortion(bi)` 在文字畫在 x=0 起的圖上套 RippleFilter（`:238` 建立，`:242` `setXAmplitude(5.6f)`），圖寬固定，水平位移最多約 ±5.6px，把 x≈0 的筆畫推出左緣；`left` 為 0 時沒有任何左邊距可吸收這個位移。這是 verifier 的推論，沒有拿掉 RippleFilter 對照；若要在 Jira 寫成定論，先在 `distortion()` 設 `XAmplitude` 為 0 重測左緣墨點。
- 截圖（要附，在 `doc/zkdemo-b9-core-evidence/`）：`b2-captcha-known-values-load1.png`、`b2-captcha-known-values-load2.png`、`b2-captcha-overview-load1.png`，以及各尺寸 `b2-captcha-90x40x7-right-edge.png`、`b2-captcha-120x50x10-right-edge.png`、`b2-captcha-100x40x8-right-edge.png`、`b2-captcha-150x40x9-right-edge.png`、`b2-captcha-200x50x5-right-edge.png`、`b2-captcha-200x50x7-right-edge.png`。
- 建議附（檔名皆來自 `ls`）：`b2-captcha-known-values-load1.png`（90×40 ABCDEFG，右緣）、`b2-captcha-120x50x10-right-edge.png`（右緣）、`b2-captcha-90x40x7-right-edge.png`（左緣首字被切）、`b2-captcha-200x50x5-right-edge.png`（對照：完整）。
- 版本：Affects 11.0.0；未在 10.x 執行。依原始碼，v10.3.1 是否相同未比對，送出前若要加 10.3.1 需先比對。

### Jira 標題

captcha: JHLabsCaptchaEngine cuts off the text at the image edges when the text is wider than the image

### Jira 描述（可貼上）

# Steps to Reproduce

```xml
<zk>
    <captcha width="90px" height="40px" value="ABCDEFG"/>
    <captcha width="120px" height="50px" value="ABCDEFGHJK"/>
    <captcha width="200px" height="50px" value="ABCDE"/>
</zk>
```

1. open the page
2. compare the three images (reload a few times, the character positions are random)

# Current Result

in the first image (90x40, 7 characters) only ABCDE are visible, F and G are cut off at the right border. In the second image (120x50, 10 characters) the text stops at about F. The left stroke of the first glyph is also cut off on the narrow images: the leftmost pixel column (x=1) contains ink in 71-72 of 72 samples for 90x40x7, 120x50x10, 100x40x8 and 150x40x9, and the first glyph (for example the "A" of "ABCDE") loses its left part. The third image (200x50, 5 characters) is complete. See `captcha-90x40-7chars-clipped.png`, `captcha-120x50-10chars-clipped.png` and `captcha-200x50-5chars-complete.png` (Marble theme, Chromium). Right-edge contact, measured as at least 2 ink pixels in column W-2 over 72 samples: 90x40x7 11/72, 120x50x10 12/72, 100x40x8 7/72, 150x40x9 4/72, 200x50x5 1/72.

# Expected Result

the whole text is visible, for example by scaling the characters down to the available width, or by rejecting a configuration whose text cannot fit.

# Debug Info

`JHLabsCaptchaEngine.drawText` divides the width evenly (`avgwidth = width / len`) but only accumulates the shortage in `werror` when a character is wider than its slot, and returns the text width, which can exceed the image width. `drawCaptcha` then uses `left = 0` when `width - textwidth <= 0` and draws the text image at that offset, so the part beyond `width` is cut — https://github.com/zkoss/zkcml/blob/master/zkex/src/main/java/org/zkoss/zkex/zul/impl/JHLabsCaptchaEngine.java#L125-L129 and #L163-L189.

Likely cause of the left clipping (not verified by removing the filter): with `left = 0` the distortion step (`RippleFilter`, `setXAmplitude(5.6f)`, https://github.com/zkoss/zkcml/blob/master/zkex/src/main/java/org/zkoss/zkex/zul/impl/JHLabsCaptchaEngine.java#L233-L242) displaces pixels horizontally by up to about 5.6px inside an image of fixed width, so strokes near x=0 are pushed out of the left edge, and there is no left margin to absorb it.

Jira 欄位：Project ZK、Type Bug、Component Captcha、Affects Version/s 11.0.0。

注意：描述的 Steps 使用 `value="ABCDEFG"` 等固定值是 verifier 的已知值測試；預設 `length` 與隨機值的行為（200×50 長度 4 到 7）在 zkdemo 的 300 次統計中沒有切掉，所以描述只主張「文字比圖寬」的情況。附件檔名（`captcha-90x40-7chars-clipped.png` 等）對應：`b2-captcha-known-values-load1.png`（90×40）、`b2-captcha-120x50x10-right-edge.png`、`b2-captcha-200x50x5-right-edge.png`。Debug Info 的左緣原因已標為未驗證，送出前建議先做 RippleFilter 對照。

---

## B4 pdfviewer

### 備註（繁中）

- **狀態：已重現（B4，2026-10-15 verifier，Marble 預覽，`/pdfviewer.zul`，真實滑鼠）。** 判定為兩主題共有（O2，依原始碼；未在 IceBlue 執行）。
- 現象一（旋轉）：按 toolbar「Rotate Clockwise」後 page 595.3×841.9 → 841.9×595.3，canvas 字跡變直排（ink bbox x 928.6–948.1、y 285.5–627.5）。文字層 `data-main-rotation="90"` 但 computed `transform: none`；span 仍橫排在頁面上方（聯集 [320.9, 292.8, 803.5, 305.8]），span inline style 旋轉前後相同（`left: 12.1%; top: 13.57%; font-size: calc(var(--scale-factor)*18.00px)…`）。拖曳選取整行仍選到全文，但選取帶橫在頁面上方，與字跡無關。
- 現象二（0° 的選取偏差）：文字層到 document 的祖先鏈上沒有任何 `--scale-factor`（computed 為空值），所以 pdf.js 設的 `font-size: calc(var(--scale-factor)*Npx)` 無效，選取框與字跡對不上，0° 也是：選取高度約為字跡的 13/18，寬度約 72%（verifier 的量測；`ev/` 內截圖可見）。注入 `--scale-factor: 1` 後對齊在 1.6px 內（`b4-pdfviewer-rot0-selection-with-scale-factor-1.png`）。
- 原始碼（zkcml，`zkex/src/main/resources/web/js/zkex/pdfviewer/`，已用 `git show master:<path>` 驗證下列路徑在 master 存在；`git diff master` 對 `Pdfviewer.ts` 與 `ext/pdf.mjs` 為 0 行）：
  - `Pdfviewer.ts` master `:633-636` 以 `rotation: page.rotate + this._rotation` 取 viewport；`:568-574` `_rotateBy`；`:662-687` `_handleTextLayer` 以 `new PDFJS.TextLayer({textContentSource, container: textLayer, viewport}).render()`（`:681-686`）交給 pdf.js，之後 ZK 沒有設定 `--scale-factor`，也沒有旋轉規則。
  - `ext/pdf.mjs` master `:1218-1219` `setLayerDimensions` 的寬高用 `var(--scale-factor) * …px`；`:1231` 設 `data-main-rotation`；`:3142` 依 `data-main-rotation` 判斷；`:11089` `const scaleFactorStr = "calc(var(--scale-factor)*"` 是 span 的 font-size 來源。pdf.js 版本 4.10.38（verifier 記錄）。
  - 樣式：master `less/pdfviewer.less:53-73`（`&-text-layer` 與 `> span`，`opacity: 0.2`、`transform-origin: 0 0`）沒有 `--scale-factor`、也沒有 `[data-main-rotation]` 規則；Marble 的 `css/pdfviewer.css`（marble 分支）亦同。`git grep` 對 `scale-factor`／`data-main-rotation` 在 master 的 zkex 內只命中 `ext/pdf.mjs`、`ext/pdf.worker.mjs`。pdf.js 的 `pdf_viewer.css` 把旋轉寫成 `[data-main-rotation]` 的 transform，且 `--scale-factor` 為宿主設定，ZK 兩者都沒提供。
  - `--scale-factor` 依縮放而變，屬執行期數值，只能由 JS 設定（例如在 `_getPage0` 以 `this._zoom` 設在 `page` 或 `text-layer` 上），不是主題 CSS 能補的。
- 建議修法是推論：在 `_getPage0` 或 `_handleTextLayer` 設 `--scale-factor`（= `viewport.scale`），並補上 pdf.js 的 `[data-main-rotation]` 規則（或把相關 pdf_viewer.css 規則納入 pdfviewer 樣式）。這些修法沒有實作驗證，只有「注入 `--scale-factor: 1` 對齊」的對照。
- 與 zkcml 已合併的 Z1（`f99b40755`，Marble 的 pdfviewer CSS）關係：Z1 驗證 A1.5 的殘差正是 `--scale-factor` 缺失造成，不是 Z1 的缺陷。
- 重複檢查（唯讀 JQL，Atlassian MCP，zkoss.atlassian.net，2026-10-15）：
  1. `project = ZK AND (text ~ "pdfviewer" OR text ~ "pdf.js") AND (text ~ "textLayer" OR text ~ "text layer" OR text ~ "selection" OR text ~ "rotate" OR text ~ "rotation" OR text ~ "scale-factor") ORDER BY updated DESC`：3 筆：ZK-6117 Missing translations（Open）、ZK-5426 pdfviewer cannot display some texts（Closed，Fixed）、ZK-4740 Provide an "auto rotate" feature to support landscapes PDF files（Closed，Done）。
  2. `project = ZK AND (text ~ "scale-factor" OR text ~ "scaleFactor" OR (text ~ "pdfviewer" AND text ~ "rotate")) ORDER BY updated DESC`：同樣 3 筆。
  - 判定：**無重複**。ZK-6117 與 pdfviewer 文字層無關（翻譯）；ZK-5426 是「某些文字無法顯示」（已修）；ZK-4740 是自動旋轉橫向 PDF 的功能（Done，增加了 `_rotation`／`rotation` 相關支援，但沒有處理文字層）。這三筆只讀了摘要與狀態，沒有逐筆讀內文；ZK-4740 與本項的關聯（旋轉功能的來源）建議在送出前讀一次內文再決定是否在 Debug Info 引用。CaseFoundry 沒有為 B4 另外查詢。
- 截圖（要附，在 `doc/zkdemo-b9-core-evidence/`）：`b4-pdfviewer-rot90-selection.png`（旋轉 90° 後，文字層仍橫排）、`b4-pdfviewer-rot0-selection.png`（0° 選取與字跡不符）、`b4-pdfviewer-rot0-selection-with-scale-factor-1.png`（注入 `--scale-factor: 1` 後對齊）。

### Jira 標題

pdfviewer: the text layer does not follow the page rotation, and text selection does not match the glyphs because `--scale-factor` is not defined

### Jira 描述（可貼上）

# Steps to Reproduce

```xml
<zk>
    <pdfviewer id="pv" src="/sample.pdf" width="900px" height="700px"/>
</zk>
```

1. open the page with any PDF that contains text, and drag-select a line of text at 0 degrees
2. click "Rotate Clockwise" in the toolbar
3. drag-select the text again

# Current Result

at 0 degrees the selection highlight does not cover the glyphs: it is about 13/18 of the glyph height and about 72% of the glyph width (`pdfviewer-rot0-selection-mismatch.png`). After the rotation the canvas shows the glyphs vertically, but the text layer element has `data-main-rotation="90"` and a computed `transform: none`, so its spans stay horizontal at the top of the page and the selection has no relation to the glyphs (`pdfviewer-rot90-selection-mismatch.png`). `--scale-factor` is not defined on any ancestor of the text layer (its computed value is empty). When it is set by hand to `--scale-factor: 1` on the text layer, the selection at 0 degrees matches the glyphs within 1.6px (`pdfviewer-rot0-selection-scale-factor-1.png`). Marble theme, Chromium.

# Expected Result

the selection highlight follows the glyphs at 0 degrees and after the rotation (the text layer is rotated together with the canvas).

# Debug Info

`Pdfviewer._getPage0` passes `rotation: page.rotate + this._rotation` to `page.getViewport()` and `_handleTextLayer` hands the viewport to `new PDFJS.TextLayer({...}).render()`. pdf.js writes `font-size: calc(var(--scale-factor)*Npx)` on every span (`ext/pdf.mjs` line 11089) and sets `data-main-rotation` on the text layer (line 1231), and it expects the host page to define `--scale-factor` and the `[data-main-rotation]` transform rules (pdf_viewer.css). The zkex pdfviewer styles (`less/pdfviewer.less` lines 53-73 and the Marble `css/pdfviewer.css`) define neither, so `calc(var(--scale-factor)*Npx)` is invalid and the rotation is not applied to the text layer.

- https://github.com/zkoss/zkcml/blob/master/zkex/src/main/resources/web/js/zkex/pdfviewer/Pdfviewer.ts#L633-L636
- https://github.com/zkoss/zkcml/blob/master/zkex/src/main/resources/web/js/zkex/pdfviewer/Pdfviewer.ts#L662-L687
- https://github.com/zkoss/zkcml/blob/master/zkex/src/main/resources/web/js/zkex/pdfviewer/ext/pdf.mjs#L1218-L1231
- https://github.com/zkoss/zkcml/blob/master/zkex/src/main/resources/web/js/zkex/pdfviewer/ext/pdf.mjs#L11089
- https://github.com/zkoss/zkcml/blob/master/zkex/src/main/resources/web/js/zkex/pdfviewer/less/pdfviewer.less#L53-L73

`Pdfviewer.ts` and `ext/pdf.mjs` are identical to master, and neither the master LESS nor the Marble CSS defines `scale-factor` or a rotation rule, so the problem is not theme specific. `--scale-factor` depends on the zoom, so it has to be set from JavaScript.

Suggested fix: set `--scale-factor` (the viewport scale) on the text layer (or the page layer) whenever the page is rendered, and add the `[data-main-rotation]` rules from pdf.js's `pdf_viewer.css` for the text layer.

# Workaround

after each render, set the variable from client code, for example `jq(wgt.$n('text-layer')).css('--scale-factor', wgt._zoom)`. This is not verified for the rotated case (only `--scale-factor: 1` at 0 degrees was measured).

Jira 欄位：Project ZK、Type Bug、Component Pdfviewer（zkex）、Affects Version/s 11.0.0。

注意：Steps 的 `src="/sample.pdf"` 是占位，送出前換成實際重現用的 PDF 與頁面屬性（verifier 用 `/pdfviewer.zul` 預覽頁，沒有保存 ZUL 原文）。Workaround 的 `_zoom` 對應是依 `Pdfviewer.ts:634` 的 `scale: this._zoom` 推得，沒有執行。附件對應：`b4-pdfviewer-rot0-selection.png` -> `pdfviewer-rot0-selection-mismatch.png`；`b4-pdfviewer-rot90-selection.png` -> `pdfviewer-rot90-selection-mismatch.png`；`b4-pdfviewer-rot0-selection-with-scale-factor-1.png` -> `pdfviewer-rot0-selection-scale-factor-1.png`。
