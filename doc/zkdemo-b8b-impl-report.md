# zkdemo B8 Z9／Z11／Z12 實作回報

日期：2026-10-15。依據 `doc/zkdemo-b8b-impl-brief.md`。以下是量測值，驗證由另一個 agent 負責。

## Worktree 與伺服器

- zk：`/Users/hawk/Documents/workspace/ZK10/b8b/zk`，branch `zkdemo-b8b`，從 `aa52616af5` 切出。
- zkcml：`/Users/hawk/Documents/workspace/ZK10/b8b/zkcml`，`--detach`，HEAD `68d1313f3`（主 zkcml 工作樹當時的 HEAD），沒有修改。
- 預覽站 8086 仍在執行，從 b8b/zk/zkpreview 啟動。PID：
  - Jetty runner：49370（`lsof -iTCP:8086` 與 cwd 確認屬於 b8b/zk/zkpreview）
  - 外層 bash：44574；`tail -f /dev/null` holder：44577；gradlew wrapper client：44578
  - Gradle daemon 是共用的，沒有記錄也不會停。
- 第一輪（修改前基準）的伺服器 PID 28155／28157／28158／38061 已用 kill holder 的方式停掉，確認都已消失。沒有碰 8085，沒有用 pkill／killall。
- 探針頁 `b8b-probe.zul` 放在 `zkpreview/build/inplaceWebapp`，已刪除，`/b8b-probe.zul` 回 404。

## Commits（zkdemo-b8b，只 commit 未 push）

| hash | 訊息 | 檔案 |
|---|---|---|
| b826a33cbf | ZK-6112: let the calendar width and height attributes take effect (zkdemo B8 Z9) | `zul/src/main/resources/web/js/zul/db/css/calendar.css` |
| 58c01269c4 | ZK-6112: let disabled calendar days show the not-allowed cursor (zkdemo B8 Z12) | 同上 |
| b1bf62adfb | ZK-6112: restore the zero-size upload span that the LESS-to-CSS move dropped (zkdemo B8 Z11) | `zul/src/main/resources/web/zul/css/base/_dnd.css` |

commit 順序是 Z9、Z12、Z11（Z9 與 Z12 在同一個檔，先拆 hunk 分別提交）。

### 改動內容
- Z9：新增 `.z-calendar[style*="width"] { max-width: none; }` 與註解；`.z-calendar-body` 加 `flex: 1 1 auto`。
- Z12：`.z-calendar-cell.z-calendar-disabled` 拿掉 `pointer-events: none`，註解改寫（點擊由 `Calendar.ts _chooseDate` 忽略、hover 圓盤由 `::before` 透明規則壓掉）。`.z-calendar-outrange` 未動。
- Z11：把 `span.z-upload`、`span.z-upload input` 補到 `_dnd.css`。
  - 檔案選擇理由：`norm.less` 在 Marble 的對應全域檔不是單一檔案。footer bundle（`footer.css.dsp`，最後載入、不屬單一元件、不在 layer 內）收的是「ZK client 在 runtime 產生的框架 hook」，`_dnd.css` 已有同性質的 `.z-word-nowrap`。`footer.css.dsp` 內已確認有這條規則。沒有選 `_reset.css`，因為它會另外產生 `reset-embed.css` 變體。
  - `font-size: 45pt` 沒有搬：`Upload.ts` `sync()` 會把 input 的 inline width／height 設成 0（非 autodisable 情況）。`Upload.ts` 沒有 `getComputedStyle(` 或 `.style[` 讀 `.z-upload` 的值（只有 `inp.style.height/width` 的寫入）。
  - 注意：autodisable 的按鈕不會呼叫 `sync()`，該情況下 input 保持預設寬度，但它是 `opacity: 0`、`position: absolute`、`z-index: -1`，不影響版面。這個情況我沒有實測。

## 檢查（8086，暫時探針頁；修改前 → 修改後）

### Z9 calendar
| 項目 | 修改前 | 修改後 |
|---|---|---|
| 沒設 width 的 calendar 寬 | 268 | 268 |
| `width="440px"` | 268 | 440 |
| 480px 容器內 `width="100%"` | 268 | 480 |
| 沒設 height 的 calendar 高／日期表格高／表格下方留白 | 290／236／1 | 290／236／1 |
| `height="400px"` 的 calendar 高／日期表格高／表格下方留白 | 400／236／111 | 400／346／1 |
| datebox 彈出的 calendar（寬／高／表格高） | 268／288／236 | 268／288／236 |

Tablet（iPad UA，834 寬，ad hoc 腳本）：`c0`、`width="440px"`、`width="100%"` 三者都是 360（修改後）。zkcml 的 `tablet/_calendar.css` 是未在 layer 內的 `.z-calendar { max-width: 360px }`，勝過 layer 內的新規則，所以預設行為不變，但 tablet 上 `width` 屬性仍被 360px 蓋住。這是 zkcml 的檔案，沒有動；修改前的 tablet 沒有另外量（只量了修改後，由 CSS cascade 推論修改前也是 360）。`_wheel.css` 只作用於 `.z-calendar-wheel-*`，沒有驗證其實際 wheel 行為（只跑了 tablet.spec 的 datebox wheel 測試，通過）。

### Z11 upload
| 項目 | 修改前 | 修改後 |
|---|---|---|
| `span.z-upload` offsetHeight／offsetWidth（在 `z-d-flex z-flex-col z-gap-3` 內） | 20／300 | 0／0 |
| computed `font-size` | 13px | 0px |
| input opacity／position／z-index／cursor | 1／static／auto／default | 0／absolute／-1／pointer |
| `<button upload>` 底到 `<fileupload>` 頂的距離 | 44 | 24 |
| 容器高度 | 148 | 108 |

24 = 兩個 `gap-3`（12 + 12）：每個 0×0 span 本身也是 flex item，所以 gap 仍會算兩次，不是「只剩一個 gap」。若期待的是 12，這一點需要決定（結構來自 `Upload.ts` 的 span 插入位置，不是 CSS 能改）。

真實 `page.mouse.click`（攔 `filechooser` 事件）：`<button upload>` 與 `<fileupload>` 都開啟檔案選擇器（修改後）。沒有實測「選擇檔案後上傳完成」，也沒有量修改前的 filechooser。

### Z12 disabled day
- 停用格（`constraint="no past"` 的 calendar，18 格，取第一格 text=27）：
  - computed `cursor`：修改前 `not-allowed`，修改後 `not-allowed`
  - computed `pointer-events`：`none` → `auto`
  - `elementFromPoint(格子中心)`：`TABLE`（不是 cell）→ `TD` 且等於該 cell
- 真實滑鼠 hover 後 `::before` 的 background-color：`rgba(0, 0, 0, 0)`（修改後；沒有圓盤）。
- 真實滑鼠點擊停用格後，calendar 的 value 不變（`Thu Oct 15 2026` → 相同），`.z-calendar-selected` 個數仍為 1。
- 只量了這一個停用格；`.z-calendar-outrange` 沒有重新量測（未改動）。

## Playwright（repo config，`PREVIEW_URL=http://127.0.0.1:8086`，只跑比對，沒有 `--update-snapshots`）

- `gallery-scan.spec.ts` + `render-smoke.spec.ts`，`-g "calendar|datebox|daterangebox|fileupload|button"`：9 passed。
  - gallery-scan 只有 `calendar`、`combobutton`、`daterangebox` 符合；`button`、`datebox` 在 `COVERED_ELSEWHERE`，`fileupload` 在 `SKIP`，所以不在這個 spec 內。
- `tablet.spec.ts` + `component-theming.spec.ts`，同樣 `-g`：22 passed。
- 額外：`screenshot.spec.ts`（涵蓋 button／datebox 等 gallery 與狀態截圖），同樣 `-g`：24 passed。這不在派工的清單內，是我為了補上 `button`／`datebox` 的截圖比對加跑的。
- 每次執行後 `git status --porcelain doc/screenshots` 都是空的，沒有新增或改寫 PNG。
- 沒有跑 `forced-colors-gallery`，沒有用 `--update-snapshots`。

## 其他

- `npm run lint:css`：0 errors，1 warning（`zul/.../grid/css/grid.css:119` `no-duplicate-selectors`，既有，與本次無關，未處理）。
- 沒有改 TS，未跑 `npm run lint -- zul/...`。
- `doc/contracts/calendar.*`、`doc/spec/` 沒有 268px 上限或高度的斷言，因此沒有文件更新。`doc/contracts/framework-classes.md` 只列 drag／drop 與 frozen 類別，未加入 `span.z-upload`（沒有對應的 spec check，未擴張範圍）；如果要把 Z11 納入契約，需要另外決定。
- `zk/src/main/java/org/zkoss/zk/mesg/MZk.java` 在 b8b/zk 顯示為 modified：這是 Gradle build 重新產生的時間戳註解，不屬於任何 commit，也沒有 stage；留在 worktree 內。
- 沒有檢查的部分：
  - 修改前的 tablet 寬度與 filechooser 行為。
  - autodisable 上傳按鈕。
  - `menuitem`／`toolbarbutton` 的 upload（只測了 button 與 fileupload）。
  - 未做 Z9 的 EE 版 datebox（`zkmax`）以外的其他 calendar 嵌入情境（例如 daterangebox 內的 calendar 寬度，只靠 gallery 截圖比對，沒有量尺寸）。
- 看到但沒處理：tablet 上 calendar 的 `width` 屬性被 zkcml `tablet/_calendar.css` 的 `max-width: 360px` 蓋住（見 Z9）。

## 追加修正 F1（tablet 的 calendar width）

- zkcml worktree `ZK10/b8b/zkcml`：建 branch `zkdemo-b8b`（自 `68d1313f3`），commit `ff3165ee4` `ZK-6112: let the calendar width attribute take effect on tablets (zkdemo B8 Z9)`，只含 `zkmax/src/main/resources/web/zkmax/css/tablet/_calendar.css`。`lib/spel2js/package-lock.json` 是既有的未提交修改，沒有 stage。
- 改動：在 `.z-calendar { max-width: 360px; }` 後加 `.z-calendar[style*="width"] { max-width: none; }` 與一行註解（此檔未在 layer 內，zul 的 layer 內規則勝不了）。`_wheel.css` 未動。
- 量測（iPad UA，834 寬）：修改前 無 width／`width="440px"`／容器 480 內 `width="100%"` = 360／360／360；修改後 = 360／440／480。
- datebox popup（tablet，wheel 版）修改後 calendar 寬 420，inline style 為 `width: auto;`，會命中新規則，但 `_wheel.css` 的 `.z-datebox-popup .z-calendar:has(.z-calendar-wheel-cave) { max-width: 420px }`（specificity 較高）仍生效，所以仍是 420。修改前的 420 我沒有另外量，是由 cascade 推論。
- Playwright `tablet.spec.ts -g "calendar|datebox"`：7 passed，只比對；`git status --porcelain doc/screenshots` 為空。
- 第一次 `./gradlew jar` 曾 BUILD FAILED（30 秒，未查原因；與停止 server 同時進行），重跑成功。
- 伺服器：舊 PID 49370／44574／44577／44578 已停。新 PID：Jetty 71159（cwd 在 b8b/zk/zkpreview）、外層 bash 70194、holder 70196、gradlew client 70197。8086 執行中。探針頁已刪除。

## 追加修正 F2（只對 `width` 屬性解除 calendar 上限）

- 問題：`[style*="width"]` 是子字串比對，`min-width`、`border-width`、`max-width` 也會解除 268px／360px 上限。
- 8086 上實際看到的 style 序列化（DOM `getAttribute('style')`）：
  - `width="440px"` → `width:440px;`
  - `width="100%"` → `width:100%;`
  - `width="440px" style="color:red"` → `color:red;width:440px;`
  - `style="color:red;width:440px"` → `color:red;width:440px;`
  - `style="color:red; width:440px"` → `color:red; width:440px;`
  - `hflex="1"` → `width: 100%;`（冒號後有空格）
  - `style="min-width:250px"` → `min-width:250px;`
  - `style="border-width:2px"` → `border-width:2px;`
  - `style="max-width:300px"` → `max-width:300px;`
  - `width="440px" height="300px"` → `width:440px;height:300px;`
  - 宣告開頭都沒有前導空白，所以 `[style^="width"]`、`;width`、`; width` 三項已涵蓋，清單不需調整。
- 新選擇器（zul `calendar.css` 與 zkcml `tablet/_calendar.css` 相同）：`.z-calendar:is([style^="width"], [style*=";width"], [style*="; width"])`，兩處註解都寫明為何錨定。
- Commits（不 amend）：zk `45953b860a`、zkcml `fe5649586`，訊息皆為 `ZK-6112: lift the calendar width cap only for the width property, not min-width or border-width (zkdemo B8 Z9)`。zkcml 的 `lib/spel2js/package-lock.json` 既有修改未 stage。
- 修改後量測（桌面／tablet）：
  - 沒設 width：268／360
  - `width="440px"`：440／440
  - 480 容器內 `width="100%"`：480／480
  - `color:red;width:440px`（兩種空白）：440／440
  - `hflex="1"`：1300／834（撐滿容器，hflex 本來就應如此）
  - `min-width:250px`：268／360
  - `border-width:2px`：268／360
  - `max-width:300px`：300／300（max-width 自己的值生效）
  - `width` + `height`：寬 440
- Playwright 只比對：gallery-scan+render-smoke 9 passed，tablet+component-theming 22 passed，screenshot 24 passed（`-g "calendar|datebox|daterangebox|fileupload|button"`）。`git status --porcelain doc/screenshots` 為空。
- 注意：tablet 的 datebox wheel popup 的 inline style 是 `width: auto;`，仍命中 `[style^="width"]`，但 `_wheel.css` 的 `max-width: 420px`（specificity 較高）照舊生效，沒有再量。
- 伺服器：舊 PID 71159／70194／70196／70197 已停。新 PID：Jetty 27062（cwd 在 b8b/zk/zkpreview）、外層 bash 20508、holder 20510、gradlew client 20511。8086 執行中，探針頁已刪除（404）。
