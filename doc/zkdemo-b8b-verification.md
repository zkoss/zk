# zkdemo B8 Z9／Z11／Z12 獨立驗證

日期：2026-10-15。驗證者：獨立 agent（沒有參與實作，沒有改任何原始碼，沒有 commit）。
受測 commit：zk `b826a33cbf`（Z9）、`58c01269c4`（Z12）、`b1bf62adfb`（Z11），branch `zkdemo-b8b`，base `aa52616af5`；zkcml `ff3165ee4`（Z9 tablet），base `68d1313f3`。
環境：8086 預覽站（Jetty PID 71159，沒有停止、重啟或重建 jar），Playwright（`zkpreview/node_modules/playwright`，Chromium）。desktop 用 1280×900；tablet 用 repo 的 `TABLET_USE`（834×1112、iPad UA、`hasTouch`、`isMobile`），並確認 `zkmax/css/tablet.css` 已載入。

## 「修改前」數值的取得方式

不能重建 jar，所以在同一個 8086 頁面上，以 CSSOM 還原 base commit 的 cascade：刪除兩條 `.z-calendar[style*="width"]`（zul 的 layer 內規則與 zkcml tablet 的規則）、移除 `.z-calendar-body` 的 `flex`、對 `.z-calendar-cell.z-calendar-disabled` 加回 `pointer-events: none`、刪除 `span.z-upload` 與 `span.z-upload input`。腳本記錄每次還原實際命中的規則（desktop 命中 5 條，tablet 命中 6 條，數量與四個 diff 相符）。這是把新規則從 cascade 拿掉，不是另加覆寫規則，所以其他規則（例如 `_wheel.css` 的 420px）仍照原樣參與比較。popup 都是在還原「之後」才開啟，因此 `Datebox.ts` 開啟時寫入的 inline 寬度也是在修改前的 cascade 下算出的。

## 結果

| # | 主張 | 量測方式 | 量測值 | 結果 |
|---|---|---|---|---|
| 1a | Z9 diff 只改 `calendar.css`，沒有無關修改、沒有寫死顏色 | `git show b826a33cbf` | 1 檔，+7 行：新增 `.z-calendar[style*="width"] { max-width: none }` 與註解、`.z-calendar-body` 加 `flex: 1 1 auto`；無顏色值 | PASS |
| 1b | Z9 註解「`width` 與 `hflex` 會寫成 inline width」正確 | 探針頁 `hflex="1"` 的 calendar 讀 `style` 屬性 | `hflex="1"` → `style="width: 100%;"`，寬 480 | PASS |
| 1c | Z12 diff 只拿掉 `pointer-events: none` 並改註解 | `git show 58c01269c4` | 1 檔，+4／-2；`cursor: not-allowed` 保留；`.z-calendar-outrange` 未動；註解所說的 `Calendar.ts _chooseDate` 會檢查 `$s('disabled')`（`Calendar.ts:1065`），下方 `::before { background-color: transparent }` 存在 | PASS |
| 1d | Z11 diff 只在 `base/_dnd.css` 末尾加兩條規則 | `git show b1bf62adfb` | 1 檔，+24 行；無顏色值；註解提到的 `sync()` 寫 inline `width/height: 0px` 與 `Upload.ts:174-175` 相符；`Upload.ts` 沒有讀這些值的 `getComputedStyle(` | PASS |
| 1e | zkcml tablet diff 只改 `tablet/_calendar.css` | `git show ff3165ee4` | 1 檔，+5 行（規則與一行註解）；`_wheel.css` 未動；worktree 只剩既有的 `lib/spel2js/package-lock.json` 未提交修改，不在 commit 內 | PASS |
| 2a | desktop `width="440px"` → 440 | 探針頁，`getBoundingClientRect().width` | 修改後 440；修改前 268 | PASS |
| 2b | desktop 480px 容器內 `width="100%"` → 480 | 同上 | 修改後 480；修改前 268（`hflex="1"` 亦同：480／268） | PASS |
| 2c | desktop 沒設 width → 268 | 同上 | 修改後 268；修改前 268 | PASS |
| 2d | `height="400px"` 時日期格長高、下方沒有大片留白 | 量 calendar 高、`.z-calendar-body` 高、body 底到 calendar 內緣底的距離 | 修改後 400／346／0；修改前 400／236／110 | PASS |
| 2e | 沒設 height 時幾何不變 | 同上 | 修改後 290／236／0；修改前 290／236／0 | PASS |
| 2f | tablet 沒設 width → 360 | `TABLET_USE` context，同上 | 修改後 360；修改前 360 | PASS |
| 2g | tablet `width="440px"` → 440 | 同上 | 修改後 440；修改前 360（480 容器內 `width="100%"`：480／360；`height="400px"`：body 338、留白 0／body 276、留白 62） | PASS |
| 3a | desktop datebox popup calendar 不超過 268 | 真實 `page.mouse.click` 按 `$n('btn')`，量 popup 內 `.z-calendar` | 一般 datebox 268→268；`format="yyyy/MM/dd HH:mm"`（timebox 顯示）268→268；`showTodayLink` 268→268；`width="400px"` 的 datebox（popup 400 寬）268→268；`width="360px"`＋時間＋today link（popup 360 寬）268→268。全部修改前＝修改後 | PASS |
| 3b | desktop popup calendar 是否命中 `.z-calendar[style*="width"]` | 讀 popup 內 `.z-calendar` 的 `style` 屬性、`document.querySelectorAll('.z-calendar[style*="width"]')` | desktop 的 popup calendar **沒有** inline style（`style=null`），computed `max-width` 仍是 268px。`width: auto` 是 `Datebox.ts:1057` 寫在 popup 容器 `.z-datebox-popup`（`$n('pp')`）上，該元素不帶 `z-calendar` class，不符合選擇器。命中選擇器的只有探針頁三個設了 width／hflex 的 calendar | PASS |
| 3c | EE daterangebox popup calendar 不超過 268 | 真實 click 開啟，量所有 popup calendar | 2 個月、1 個月、`showTime`＋`showTodayLink` 三種：每個 calendar 268→268，popup 寬 572／292／572 修改前後相同；calendar 無 inline style | PASS |
| 3d | tablet wheel popup 不超過 420 | `page.touchscreen.tap` 開啟，量 popup calendar | 一般、時間格式、`showTodayLink` 三種 datebox：420→420，computed `max-width` 420px。這裡 calendar **有** inline `width: auto;`（來源：zkmax `touch/datebox-touch.ts:155` 在 tablet 的 `CalendarPop.bind_` 寫入），會命中兩條新規則；但 `_wheel.css:68` 的 `.z-datebox-popup .z-calendar:has(.z-calendar-wheel-cave)`（specificity 0,3,0，未在 layer 內）勝過 `.z-calendar[style*="width"]`（0,2,0），所以上限仍是 420 | PASS |
| 3e | tablet daterangebox popup | 同上 | 每個 calendar 360→360（無 inline style，不命中） | PASS |
| 4a | Z11：flex column 內 `span.z-upload` 為 0×0 | `z-d-flex z-flex-col z-gap-3` 容器內的 `<button upload>`、`<fileupload>`；量 `offsetWidth/offsetHeight` | 修改後 0×0（兩者）；修改前 1248×20。computed `display` 為 `block`（flex item blockify），不影響尺寸。按鈕底到 fileupload 頂：修改後 24（= 兩個 12px gap，因為 0×0 的 span 本身仍是 flex item）；修改前 44 | PASS |
| 4b | `<button upload>` 真實點擊開檔案選擇器 | 每個元件一個新頁面，`page.waitForEvent('filechooser')` ＋ `page.mouse.click` 元件中心；`elementFromPoint` 確認點到的是元件本身 | 開啟（中心點命中 `BUTTON.z-button`） | PASS |
| 4c | `<fileupload>` 真實點擊開檔案選擇器 | 同上 | 開啟 | PASS |
| 4d | `<toolbarbutton upload="true">` 開檔案選擇器 | 同上 | 開啟（命中 `A.z-toolbarbutton`）；span 0×0（修改前 0×20） | PASS |
| 4e | `<menuitem upload="true">`（在 menubar）開檔案選擇器 | 同上 | 開啟（命中 `A.z-menuitem-content`）；span 插在 `.z-menuitem-content` 內，0×0（修改前 0×20） | PASS |
| 5a | Z12：停用格 computed `cursor: not-allowed` | `constraint="no past"` 的 calendar，取停用格「6」 | 修改後 `not-allowed`，`pointer-events: auto`；修改前 computed 也是 `not-allowed` 但 `pointer-events: none`，真實 hover 時命中元素的 cursor 是 `auto` | PASS |
| 5b | `elementFromPoint(格子中心)` 是該 TD | 同上 | 修改後 `TD.z-calendar-cell.z-calendar-weekday.z-calendar-disabled`，等於該格；修改前 `TABLE.z-calendar-body` | PASS |
| 5c | 真實滑鼠點停用格，值不變 | `page.mouse.click` 停用格中心，讀 `getValue()` 與 `.z-calendar-selected` | 修改後：值維持 `Thu Oct 15 2026`，選取格仍為 1 個（15）；在另一次已選 20 的狀態下點停用格，值仍為 20 | PASS |
| 5d | hover 停用格沒有圓盤 | `page.mouse.move` 到格子中心，讀 `:hover` 與 `::before` 背景 | 修改後 `:hover` 為 true，`::before` 背景 `rgba(0, 0, 0, 0)`；對照：hover 可用格「20」時 `::before` 背景為 `color(srgb 0 0 0 / 0.0696)`，cursor `pointer` | PASS |
| 5e | 可用日期仍可點選 | `page.mouse.click` 格子「20」 | 值變為 `Tue Oct 20 2026`，選取格為 20 | PASS |
| 6a | Playwright 比對全部通過 | `PREVIEW_URL=http://127.0.0.1:8086 npx playwright test -c src/test/playwright/playwright.config.ts gallery-scan.spec.ts render-smoke.spec.ts screenshot.spec.ts tablet.spec.ts component-theming.spec.ts -g "calendar\|datebox\|daterangebox\|fileupload\|button"`，沒有 `--update-snapshots`，沒有跑 `forced-colors-gallery` | 55 passed（21.2s），0 failed。比對對象的基準 PNG（`zkpreview/doc/screenshots/calendar-gallery.png`、`calendar-tablet.png`、`datebox-*.png`、`daterangebox-gallery.png`、`button-*.png` 等）都已在 git 追蹤中 | PASS |
| 6b | 跑完沒有新增或改寫 PNG | 跑前、跑後各存一次 `git -C …/b8b/zk status --porcelain` 並 diff；另用 `find -newer` 查 `zkpreview/doc/screenshots` | 兩次輸出相同（只有既有的 `M zk/src/main/java/org/zkoss/zk/mesg/MZk.java`、`?? node_modules`、`?? zkpreview/node_modules`）；`--untracked-files=all` 下 PNG 數 0；沒有比跑前新的 PNG | PASS |

## 關於第 3 項（PM 指定的風險）

PM 的前提「datebox popup calendar 帶 inline `width: auto`」只在 tablet 成立：

- desktop：`width: auto` 寫在 popup 容器 `.z-datebox-popup` 上（`Datebox.ts:1057`），內部 `.z-calendar` 沒有 inline style，不命中新規則，268px 上限仍在。即使 popup 因 datebox 本身較寬而撐到 360 或 400，calendar 仍是 268（修改前後相同）。
- tablet：`datebox-touch.ts:155` 在 calendar 本身寫 `width: auto`，命中新規則；上限改由 `_wheel.css:68` 的 420px 規則維持，修改前後都是 420。
- 方法：CSSOM 刪除新規則來重建修改前的 cascade（見上方說明），不是推論。

沒有任何 popup calendar 超過 268（desktop）或 420（tablet wheel）。

## 觀察（不在派工檢查範圍，未判定 PASS／FAIL）

- `[style*="width"]` 是子字串比對，`min-width`、`max-width`、`border-width` 也會命中。用執行期改 `style` 屬性實測：一般 calendar 設 `style="min-width:250px"` 或 `style="border-width:2px"` 時，寬度從 268 變成填滿容器（1248）；`style="height:300px"` 不受影響（268）。這是 `separator.css:27` 前例同樣的寫法，若要接受此行為需要另外決定。
- 依序在同一個頁面點多個上傳按鈕時，第一個檔案選擇器之後的 `fileupload`、`toolbarbutton` 不會再觸發 `filechooser` 事件；這個現象在修改前（CSSOM 還原）也完全相同，屬於測試序列的副作用，與本次 CSS 無關。每個元件在新頁面中單獨測都能開啟（第 4b–4e 列）。

## 無法檢查的部分

- 實際選完檔案後的上傳完成流程（只驗證檔案選擇器會開啟）。
- `autodisable` 的上傳按鈕（不呼叫 `sync()`，input 保持預設寬度）。
- tablet 上不是 wheel 的 `CalendarPop`（若存在，會帶 inline `width: auto` 卻不符合 `_wheel.css` 的 `:has(.z-calendar-wheel-cave)`，上限會被拿掉）。本次三種 tablet datebox 都是 wheel，沒有找到能產生非 wheel 版本的設定，未實測。
- 真實 Safari／iPad 裝置；tablet 只用 Chromium 的 `TABLET_USE` 模擬。
- 修改前的數值來自 CSSOM 還原，不是 base commit 建出的 jar。

## 清理

- 探針頁 `zkpreview/build/inplaceWebapp/b8b-verify.zul` 已刪除，`/b8b-verify.zul` 回 404。
- 8086（PID 71159）仍在執行，沒有碰 8085 與主工作樹（本檔除外）。
- 量測腳本在 scratchpad：`/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/bf78f57d-01bf-46cc-8c34-690307feec54/scratchpad/verify.js`（另有 `chooser.js`、`chooser2.js`、`z12en.js`、`edge.js`、`ppcls.js`）。

VERDICT: PASS

---

## 第二輪（2026-10-15，F2 選擇器修正）

受測：zk `45953b860a`、zkcml `fe5649586`（訊息皆為 `ZK-6112: lift the calendar width cap only for the width property, not min-width or border-width (zkdemo B8 Z9)`）。8086 已由工程師重啟，Jetty PID 27062（沒有停止或重啟）。探針頁 `b8b-verify2.zul` 放在 `zkpreview/build/inplaceWebapp`，用完已刪除，回 404。

「修改前」：CSSOM 刪除所有 `.z-calendar:is(...)` 規則（desktop 命中 1 條，tablet 命中 2 條），等同 base commit 沒有解除上限規則的 cascade。新選擇器 `.z-calendar:is([style^="width"], [style*=";width"], [style*="; width"])` 的 specificity 是 (0,2,0)，與第一輪的 `.z-calendar[style*="width"]` 相同。

| # | 主張 | 量測方式 | 量測值 | 結果 |
|---|---|---|---|---|
| R2-1a | zk diff 只改 `calendar.css` 的選擇器與註解 | `git show 45953b860a` | 1 檔 +3／-2；只換選擇器並多一行註解（「錨定在宣告開頭，所以 `min-width`、`border-width` 不會命中」），內容與行為相符；沒有顏色值 | PASS |
| R2-1b | zkcml diff 只改 `tablet/_calendar.css` 的選擇器與註解 | `git show fe5649586` | 1 檔 +3／-2；選擇器與 zul 相同；註解改成「layered inline-width rule」並註明錨定，正確；worktree 只剩既有的 `lib/spel2js/package-lock.json` | PASS |
| R2-1c | 兩邊選擇器真的載入 | 走訪 `document.styleSheets` 找 `.z-calendar:is(` | desktop 1 條（zul），tablet 2 條（zul＋tablet） | PASS |
| R2-2a | 沒設 width → 268／360 | `getBoundingClientRect().width`，desktop／tablet | 268／360（修改前 268／360） | PASS |
| R2-2b | `width="440px"` → 440 | 同上 | 440／440（修改前 268／360） | PASS |
| R2-2c | 480 容器內 `width="100%"` → 480 | 同上 | 480／480（修改前 268／360） | PASS |
| R2-2d | `style="min-width:250px"` → 268／360 | 同上 | 268／360，computed `max-width` 268px／360px | PASS |
| R2-2e | `style="border-width:2px"` → 268／360 | 同上 | 268／360 | PASS |
| R2-2f | `style="max-width:300px"` | 同上 | 300／300（inline `max-width` 自己的值生效；修改前也是 300／300） | PASS |
| R2-2g | `width="440px" style="color:red"` → 440 | 同上；序列化為 `color:red;width:440px;` | 440／440 | PASS |
| R2-2h | `hflex="1"`（480 容器） | 同上；序列化為 `width: 100%;` | 480／480 | PASS |
| R2-2i | 其他寫法 | 同上 | `style="color:red;width:440px"` 440／440；`style="color:red; width:440px"` 440／440；`style="color:red;min-width:250px"` 268／360；`height="400px"` 268／360；執行期 `setWidth('440px')`（瀏覽器重寫成 `color: red; width: 440px;`）440／440 | PASS |
| R2-3a | tablet datebox wheel popup 仍為 420 | `page.touchscreen.tap` 開啟一般、時間格式、`showTodayLink` 三種 | 三者都是 420，inline `width: auto;` 命中 `[style^="width"]`，computed `max-width` 420px（`_wheel.css:68` 勝出）；修改前 420 | PASS |
| R2-3b | desktop datebox／daterangebox popup 仍為 268 | 真實 `page.mouse.click` 開啟 | 一般、時間格式、`showTodayLink`、`width="400px"` 的 datebox：268；daterangebox 2 個月、1 個月、`showTime`＋`showTodayLink`：每個 calendar 268；popup calendar 都沒有 inline style；修改前相同 | PASS |
| R2-3c | tablet daterangebox popup | 同 R2-3a | 每個 calendar 360（修改前 360） | PASS |
| R2-4a | Z11：`span.z-upload` 0×0、按鈕間距 | 探針頁 flex column | `button`、`fileupload` 的 span 皆 0×0；按鈕底到 fileupload 頂 24 | PASS |
| R2-4b | Z11：真實點擊開檔案選擇器 | 每次新 context，先 `page.on('filechooser')` 再載入頁面，`page.mouse.click` 元件中心 | `button` 6/6、`fileupload` 6/6 開啟（修改前 cascade 也是 6/6、6/6）。說明見下方 | PASS |
| R2-5 | Z12：停用格 | 停用格「6」與可用格「20」，真實滑鼠 | cursor `not-allowed`；`elementFromPoint` 命中該 TD；hover 時 `::before` `rgba(0, 0, 0, 0)`；點停用格後值仍為 `Thu Oct 15 2026`；點「20」後變 `Tue Oct 20 2026` | PASS |
| R2-6a | Playwright 比對 | 與第一輪同一指令（5 個 spec，`-g "calendar\|datebox\|daterangebox\|fileupload\|button"`，沒有 `--update-snapshots`，沒有 `forced-colors-gallery`） | 55 passed（20.4s） | PASS |
| R2-6b | 沒有新增或改寫 PNG | 跑前、跑後各存一次 `git status --porcelain --untracked-files=all` 並 diff；`find -newer` 查 `zkpreview/doc/screenshots` | 相同（只有既有的 `M zk/src/main/java/org/zkoss/zk/mesg/MZk.java`）；比跑前新的 PNG 數為 0 | PASS |

### 檔案選擇器的偶發未開啟（已查明，不是 CSS 問題）

第一次抽查時，`fileupload` 在新頁面也沒有觸發 `filechooser` 事件。重複測試（`Promise.all([waitForEvent('filechooser'), mouse.click])`，點擊前沒有等待）：修改後 5/6 未開啟，修改前 cascade 1/6 未開啟。加上事件記錄後，每一次（包括未開啟的）的路徑都相同：`mousedown`／`click` 命中 `BUTTON.z-button`（`isTrusted=true`）→ `Upload` 呼叫 `input.click()`，當下 `navigator.userActivation.isActive=true` → input 收到 click。ZK 這一側沒有差異。原因是 Playwright 的 file chooser 攔截要等 `waitForEvent` 註冊後才非同步開啟，和緊接著的點擊有競態。改成在載入頁面前就 `page.on('filechooser')` 後，修改前後的 `button`、`fileupload` 都是 6/6 開啟（R2-4b）。第一輪「同一頁連續點擊只有第一個開啟」的現象，也應該用這個原因解釋，不是測試序列的其他副作用。

### 觀察（範圍外，未判定 PASS／FAIL）

- 新選擇器漏掉兩種手寫 `style` 的寫法，上限會保留（行為與 base 相同，沒有退步）：
  - 換行或多個空白後才接 `width`，例如 `style="color:red;⏎ width:440px"`，序列化為 `color:red;    width:440px;`：268／360
  - 開頭有空白，例如 `style=" width:440px"`：268／360

  ZK 屬性產生的 `width`／`hflex`，以及執行期的 `setWidth`，都已涵蓋。

### 第二輪無法檢查的部分

與第一輪相同：沒有測上傳完成流程、autodisable 上傳按鈕、tablet 上不是 wheel 的 `CalendarPop`；沒有用真實 iPad 測試。

第二輪 VERDICT: PASS
