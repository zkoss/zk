# Marble tablet follow-up 65 rebase 驗證（獨立驗證者）

日期：2026-10-20
對象：p3 worktree，`/Users/hawk/Documents/workspace/ZK10/p3/zk`（branch `gaps-round2`，HEAD `8904c6c656` + `tree.css` 一行未提交修改）與 `/Users/hawk/Documents/workspace/ZK10/p3/zkcml`（HEAD `234cbfa0f`）。
Marble tip：zk `4d02e9c9db`、zkcml `76b51e9e6`。
伺服器：p3 `zkpreview`，port 8087，`--no-daemon`。所有 Playwright run 都用 repo config 和 `--update-snapshots=none`，`PREVIEW_URL=http://127.0.0.1:8087`。
證據（截圖、log、腳本）放在 scratchpad `/private/tmp/claude-501/-Users-hawk-Documents-workspace-ZK10-zk/028a7b57-cfcf-44e9-a25e-5d68a4869b5a/scratchpad/ver4/`（下文以 `$S` 表示）。

## 結論

| 項目 | 判定 |
|---|---|
| 1. Diff review | PASS |
| 2. Playwright run | FAIL：2 個 screenshot 失敗，兩個都來自 marble 的另一個 session 的 commit（見第 3 項）；另有 5 個 `font-size` 失敗，與先前 base 結果相同 |
| 3. 失敗原因歸屬 | PASS：每個失敗都用 route-rewrite 重現並歸屬 |
| 4. `tree.css` merge 等價 | PASS |
| 5. p3 狀態前後一致 | PASS |

需要重拍的 baseline（我沒有重拍）：

1. `zkpreview/doc/screenshots/colorbox-tablet-popup-picker.png`：我們的檔案。rebase 衝突時取了我們的版本，但這張是在 marble 改 CSS 之前拍的。新的 actual 看起來正確。
2. `zkpreview/doc/screenshots/daterangebox-popup-open-today.png`：不在我們的 commit 內。原因是 marble zkcml `76b51e9e6` 的 daterangebox readonly 規則，那個 commit 沒有重拍這張。要不要放進這批，由 owner 決定。

其他 4 張重拍過的 PNG（`colorbox-tablet-popup-palette`、`combobox-tablet-popup-sheet`、`combobox-tablet-popup-description`、`datebox-tablet-popup-wheel-time`）在 `maxDiffPixels: 0`（threshold 0.02）下都是 0 px 差異。

## 1. Diff review

### zk `git diff 4d02e9c9db HEAD`

8 個檔案：5 張 PNG、`pv/popup-tablet-fixtures.zul`（+16 行：8 個 item 的兩個 combobox、一個 colorbox，加上 1500px spacer）、`tablet.spec.ts`（+87/−19：F4 兩個 helper、datebox 接縫斷言、picker 列斷言、colorbox 捲動測試、palette 的 `position` 改為 `fixed`）、`tree.css`（−5/+3：刪掉 `.z-treecol:has(.z-treecol-button) .z-treecol-sorticon` 規則，兩個置中 checkmark selector 拿掉 `:has(.z-treecol-button)`，並改了一句註解）。和 commit 訊息所說的範圍相符。

工作區修改（`tree.css` 第 545 行）：

```diff
-.z-treecol:not(.z-treecol-sort, :has(.z-treecol-button)) > .z-treecol-content:has(.z-treecol-checkable) {
+.z-treecol:not(.z-treecol-sort) > .z-treecol-content:has(.z-treecol-checkable) {
```

### zkcml `git diff 76b51e9e6 HEAD`

4 個檔案：`tablet/_inputs.css` +33（combobox sheet 的 `max-height: none`、colorbox popup 定位、picker 三列的 `top` 和高度）、`_mesh.css` −1 selector（`.z-treecol-button`）、`_wheel.css` +11（date／time band 內側直角）、`_window.css`（刪掉 window／panel header 的 `min-height`，並改註解）。和 commit 訊息相符。

### `!important`

- colorbox 規則（`.z-colorbox-popup`）恰有 3 個：`position: fixed`、`inset: auto 0 0`、`transform: none`。
- `count-important.js` 掃 zkcml tablet 資料夾（HEAD）：**11**（`_inputs.css` 7、`_wheel.css` 4）。
- 同一支 script 掃 marble tip `76b51e9e6` 的 tablet 資料夾：**8**。差值 3，就是我們的 colorbox 規則。
- marble 在 `_inputs.css` 加的 5 行是 `.z-combobox { --zk-combobox-button-width: var(--zk-touch-target-min); }` 和一行註解，其中**沒有** `!important`，所以 11 = 11 + 0。
- 用途是否衝突：marble 的這條規則作用在 combobox **輸入框**（右側 padding 預留按鈕寬度，`combobox.css` 讀這個變數）；我們的規則作用在 `.z-combobox-popup` 的 `max-height` 和 `.z-colorbox-popup`／`.z-colorpicker*`。三者沒有共用的 selector 或屬性。實測：`combobox-tablet.png`、兩張 combobox sheet PNG 和 F4 斷言都通過。

### `tree.css` 合併的形式檢查

- `tree.css` 和 `_mesh.css` 裡都找不到 `treecol-button`（grep exit 1）。
- `tree.css` 沒有 conflict marker（grep `^(<<<<<<<|=======|>>>>>>>)` exit 1）。
- 去掉註解後，`{` 77 個、`}` 77 個，最終深度 0，最小深度 0。
- Treecol 沒有產生 `-button` 的程式碼：`zul/sel/mold/treecol.js` 只輸出 `-cave` 和 `-sort-icon`；`HeaderWidget.redraw` 只在 `parent._menupopup` 存在時輸出 `$s('button')`，但 Treecol 用的是自己的 mold，`Treecols` 的 TS／Java 也沒有 `menupopup`。
- 觀察（不影響行為）：`tree.css` 第 543 行，marble 新加的註解仍寫著「or one with the column-menu button keeps the room its icon uses inside that padding」，現在已經沒有對應的 selector。

## 2. Playwright 結果

伺服器 PID：sh 19411、`tail -f /dev/null` 19412、gradle client 19413、single-use daemon 19433、Jetty 21831。`/smoke.zul` 在第 4 次輪詢回 200。啟動時重建了 `:zk:zul:compileMarbleCss`、`:zkcml:zkmax:compileMarbleCss`、`zul`／`zkmax`／`zkex` jar（log `/tmp/server-8087-ver4.log`）。

served CSS 確認：

- `tablet.css.dsp` 含 `.z-combobox{--zk-combobox-button-width:var(--zk-touch-target-min)}`（marble）、`.z-colorbox-popup{max-width:fit-content;margin-inline:auto;position:fixed!important;inset:auto 0 0!important;transform:none!important}`、`.z-colorpicker-hsv/-hex/.z-colorpicker` 三條規則、`max-height:none`（我們的），以及 marble `_mesh.css` 的 `.z-column:has(.z-column-button) .z-column-content,…` 規則；`treecol-button` 0 次。
- `zk.wcs`（在瀏覽器中讀 CSSOM）含合併後的三條 `:not(.z-treecol-sort)` 規則，桌面 UA 和平板 UA 都有；`treecol-button` 0 次。

| Run | 結果 |
|---|---|
| `--project=tablet` | 75 個：74 passed，1 failed（`colorbox-tablet-popup-picker.png`） |
| `--project=popup`（第 1 次） | 56 個：55 passed，1 failed（`daterangebox-popup-open-today.png`） |
| `--project=popup`（第 2 次） | 56 個：55 passed，1 failed（同上） |
| daterangebox today 測試 `--repeat-each=5` | 5/5 failed，每次 47 px |
| 已知 flake `datebox.zul: open calendar with the timezone selector` | 本次 2 個完整 `popup` run 中失敗 0/2 |
| `--project=forced-colors-tablet` | 14 passed |
| `mesh-header-footer-tablet` + `mesh-header-footer`（額外加跑，因為改了 `tree.css`） | 135 + 288 passed |
| `select-tests.mjs --base 4d02e9c9db` | partial，186/1732，8 頁（colorbox、combobox、datebox、pv/mesh-header-footer-guard、pv/popup-rod-fixtures、pv/popup-tablet-fixtures、tree、tree-header） |
| 照它印出的指令跑，但拿掉 `forced-colors-gallery`，`tablet`、`popup` 改由上面的完整 run 涵蓋 | 92 個：84 passed，3 skipped，5 failed（全是 `font-size`） |

選擇器選到的 186 個 = 92 + `tablet` 73（含在完整 75 內）+ `popup` 16（含在完整 56 內）+ `forced-colors-gallery` 5（**沒有跑**：它會重寫 tracked 的 `*-forced-colors.png`）。

### `font-size`（已知環境漂移）

| 頁 | 本次差異 | 先前 base `237bfa8586` 的結果（`doc/marble-tablet-defect-fix-f3-verification.md`） |
|---|---|---|
| `colorbox.zul` | `13px` 1732 → 1735 | +3（例：1732 → 1735） |
| `combobox.zul` | `13px` 89 → 92 | +3 |
| `datebox.zul` | `13px` 129 → 132 | +3 |
| `tree.zul` | `12px` 3 → 37，`13px` 1011 → 1017，`14px` 78 → 41 | 「較大」，例：`12px` 3 → 37 |
| `tree-header.zul` | `0px` 120 → 130，`11px` 7 → 8，`12px` 24 → 96，`13px` 864 → 904，`14px` 140 → 74 | 「較大」（先前報告沒有記錄數字） |

失敗的頁和先前相同，有記錄的數字也相同。合併後的 `tree.css` diff 沒有任何 `font-size` 宣告。

## 3. 失敗原因與歸屬

像素統計用 `$S/pxdiff.cjs`：任一 channel 差 > 0 就算一個 px；maxDelta 是 0–255。Playwright 自己報的 px 數用的是 threshold 0.02（tablet）或預設 threshold（popup）。

### `colorbox-tablet-popup-picker.png`（tablet）

- 預算：`maxDiffPixels: 10`，`threshold: 0.02`。實測：Playwright 報 194 px；任意差值算法 490 px，maxDelta 222，bbox x 30–55、y 368–481（只有 RGB／HSV／HEX 三個 label 的位置）。
- actual／expected／diff：`$S/out-A/tablet-tablet-popup-colorb-2fb86-tch-the-popup-to-the-picker-tablet/`。
- 同一個 490 px 的差異，在 marble 自己的重拍中也出現過：`237bfa8586` 版與 `4d02e9c9db` 版的同名 PNG 相比是 490 px、maxDelta 222、label 欄位、同樣的列（兩張都是 352×490）。rebase 取了我們 352×514 的版本，那是在 marble 改 CSS 之前拍的。
- route-rewrite（`$S/shoot2.cjs`，複製 `tabletPopupShot` 的步驟）：在 served `zk.wcs` 中，把 `.z-textbox,…,.z-passwordbox{…}` 這條規則內的 `vertical-align:baseline` 改回 `middle`（命中 1 次，也就是 marble `input.css` 的 `vertical-align: middle → baseline`，B86_ZK_4048）。結果：沒有 rewrite 時，與我們的 baseline 差 491 px；有 rewrite 時差 1 px，maxDelta 1。
- 歸屬：**marble 的另一個 session 的 commit `4d02e9c9db`（`input.css`）**。我們的 picker 列修正（列距、高度 514）在 actual 中都保留著。
- actual 看起來對：label 下移 1 px，墨跡列從 369–378／420–429／471–480 變成 370–379／421–430／472–481，與輸入框內數字的墨跡列（370–379／421–430）相同，也就是 label 與輸入文字對齊。三列不重疊，HEX 留在 picker 內（同一測試的列斷言在 screenshot 之前執行，而且通過）。
- 須重拍：是。

### `daterangebox-popup-open-today.png`（popup，desktop）

- 預算：`maxDiffPixels: 10`（`DEFAULT_MAX_DIFF`）。實測：Playwright 報 47 px，兩次完整 run 和 5 次 repeat 都一樣；任意差值算法 67 px，maxDelta 94，bbox x 350–360、y 386–396。
- actual／expected／diff：`$S/out-B/popup-gallery-daterangebox-16050-ul-open-with-the-today-link-popup/`；放大圖 `$S/drb-zoom.png`。
- 差異不在 popup 本身，而在 12px PAD 範圍內、popup 下方另一個 daterangebox 的日曆圖示：baseline 是淡色，actual 是全不透明。
- route-rewrite：把 served `zk.wcs` 的 `.z-daterangebox.z-daterangebox-readonly .z-daterangebox-button{cursor:pointer}` 改回 `{opacity:var(--zk-state-disabled-opacity);pointer-events:none}`（命中 1 次）。圖示區（y 386–396）的差異消失。腳本與 spec 擷取方式的差異留下 8 px（maxDelta 13，x 219–221、y 158–185）；有沒有 rewrite 這 8 px 都在，而且不在圖示區。
- 歸屬：**marble 的另一個 session 的 zkcml commit `76b51e9e6`（`daterangebox.css`，ZK-6131：readonly 時按鈕保持可用）**。我們兩個 commit 都沒有碰 daterangebox，這張 baseline 也不在我們的 diff 裡。依此推論，在 marble tip 上這個測試也會失敗；我沒有在 marble tip 上跑。
- 須重拍：是，但這張屬於 marble 的範圍。

### 其他

- 5 個 `font-size` 失敗：見上表，是環境漂移，與先前的 base 相同。
- tree／tree-header 受合併影響的區段：`gallery` 的 `tree-header`、`chromium` 的 `tree gallery`／`tree hover`、`brand`、`compact`、`focus-scan`、`mesh-header-footer`（含 tablet）的 tree 測試全部通過。

## 4. `tree.css` 合併等價

`$S/treeprobe.cjs`：頁面為 `tree.zul`、`tree-header.zul`、`pv/mesh-header-footer-guard.zul`，UA 為桌面和平板（平板時 `tablet.css` 在 `document.styleSheets` 中為 true）。對所有 `.z-treecol`、`.z-treecol-content`、`.z-treecol-checkable`、`.z-treecol-sorticon` 記錄 padding（四邊）、margin-left/right、clip-path、left/right、width/height、overflow、text-overflow 和 bounding box。

三個變體：

- `merged`：照 served 的 CSS。
- `tip`：在 served `zk.wcs` 中把三個 `:has(.z-treecol-button)` 參數，以及我們刪掉的 sort-icon 規則放回去（4 個 rewrite，每頁每次各命中 1 次；CSSOM 中含 `treecol-button` 的規則數為 4）。
- `mutant`：對照組，讓 clip-path 和 padding-inline 規則排除 checkable header。

| 頁 / UA | 元素數 | checkmark header 數 | `.z-treecol-button` DOM 數 | `tip` vs `merged` 差異 | `mutant` vs `merged` 差異 |
|---|---|---|---|---|---|
| tree.zul 桌面 | 53 | 2 | 0 | 0 | 2 |
| tree-header.zul 桌面 | 108 | 0 | 0 | 0 | 0 |
| mesh-header-footer-guard 桌面 | 214 | 22 | 0 | 0 | 29 |
| tree.zul 平板 | 53 | 2 | 0 | 0 | 2 |
| tree-header.zul 平板 | 108 | 0 | 0 | 0 | 0 |
| mesh-header-footer-guard 平板 | 214 | 22 | 0 | 0 | 29 |

對照組的差異範例：`z-treecol-content` 的 clip-path 由 `inset(0px 4px 0px 0px)` 變成 `none`。這證明探針能偵測到這幾條規則的改變。`tree-header.zul` 沒有 checkmark header，所以這一頁不測這幾條規則；有測到的是 tree.zul 和 guard 頁。

結論：桌面和平板上，合併後 checkmark header 的 `.z-treecol-content` padding、clip-path、margin 和 box，與 marble tip 的語意（有 `:has(.z-treecol-button)`）完全相同。

## 5. p3 git 狀態

執行前後各存一份 `git status --porcelain`、`git diff --stat`、`git diff --cached`（`$S/before-*.txt`、`$S/after-*.txt`），用 `cmp` 比對，兩個 repo 三項都相同。

```
# p3/zk（HEAD 8904c6c656）
 M zul/src/main/resources/web/js/zul/sel/css/tree.css
?? node_modules
?? zkpreview/node_modules
 zul/src/main/resources/web/js/zul/sel/css/tree.css | 2 +-
 1 file changed, 1 insertion(+), 1 deletion(-)
git diff --cached: 0 bytes

# p3/zkcml（HEAD 234cbfa0f）
status: 空
diff --stat: 空
git diff --cached: 0 bytes
```

- 停伺服器：只 kill `tail` 19412。appRun 正常結束（`BUILD SUCCESSFUL`），5 個 PID 都已不存在，8087 沒有 listener。8085（PID 37900）沒有動過。
- 沒有建立探測頁：`build/inplaceWebapp/web/pv/` 沒有 probe 或 repro 檔。所有探測都在 served CSS 上做 route-rewrite。
- 沒有修改任何 tracked file、PNG 或 spec。
