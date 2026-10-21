# 預覽頁外框改用 native 元素計畫

分支：`preview-native-chrome`（worktree：`ZK10/pvnative/zk`）。決策：D1 = A、D2 = B、D3 = A、D4 = A（見對話紀錄）。

## 目標
預覽頁（`zkpreview/src/main/webapp/web/**/*.zul`）只展示目標元件。頁面外框（標題、區段標題、說明文字）不再使用 ZK 元件，避免迴歸測試以 CSS selector 評估其他元件時誤中外框。

## 轉換規則
1. 只轉換「不在任何元件內」的節點：祖先鏈只有 `zk`、`div`、native（`h:*`）。在 listbox、grid、tabbox、window 等元件內的 label 與 div 一律不動。
2. `<label value="T" sclass="C" style="S"/>` → `<h:span class="C" style="S">T</h:span>`。
3. `<div sclass="C" style="S">…</div>` → `<h:div class="C" style="S">…</h:div>`。
4. 只轉換屬性集合 ⊆ {`value`, `sclass`, `style`} 的 label，以及 ⊆ {`sclass`, `style`} 的 div。有 `id`、事件、`hflex` 等屬性者不動。
5. 子樹含 `parent`、`Sibling`、`getChildren` 等相對導覽字樣者，其 div 不動。
6. 排除頁面：以 label、div 本身為測試目標的頁面（`label.zul`、`div.zul`、skeleton 相關頁）。

## 驗證（D4 = A 的守門條件）
- 逐頁：轉換前後各截一張整頁圖，逐像素比對；不得有差異。
- 不用 `forced-colors-gallery`（跑一次會改寫全部 99 張圖）。
- 轉換後依 `select-tests.mjs` 結果跑對應 spec；回 `full` 時不自行跑，回報由使用者決定。

## 量測基礎（轉換前）
8085 上 179 頁掃到 8538 個 `span.z-label`；31 頁的元件內 label 與同 class native span 有 computed style 差異（color、font-size、font-weight、line-height），屬於元件內 label，依規則 1 不轉。

## 第一輪結果（2026-10-21）
- 轉換器對 137 頁產生變更，其中 `label.zul`、`skeleton.zul` 為排除頁（誤轉後已還原）。
- 整頁截圖（1280x900，關閉動畫）轉換前後逐像素比對：124 頁完全相同；13 頁不同。
- 已還原不轉：`barcodescanner`、`captcha`（驗證碼每次載入都不同，無法用像素比對）、`datebox`（1 px 差）、`pv/cascader-content`（1312 px 差）、`responsive-grid`、`video`（62 px 差）、`overview`（高度 +21 px）、`utility/grid-layout`、`utility/print`、`utility/responsive`、`utility/zindex`（高度差 21～67 px）。原因尚未查明，待逐頁診斷。
- `signature.zul` 另有手工重寫（移除 colorbox、spinner），畫面本來就不同，不屬於上述比對。
- 目前 worktree 125 個檔案有變更（含 `signature.zul`）。
- 尚未做：依 `select-tests.mjs` 跑對應 spec。worktree 缺少 `node_modules`（`Cannot find package 'postcss'`），要先安裝依賴。

## 預覽環境（D5 = A，2026-10-21）
- worktree：`ZK10/pvnative/zk`（分支 `preview-native-chrome`）與 `ZK10/pvnative/zkcml`（detached，同 `marble`）。`node_modules` 以 symlink 指向主 worktree，已寫入此 worktree 的 `info/exclude`，不會被 commit。
- 預覽伺服器：8106（8085、8090 已被其他程序占用，未動）。啟動指令與 PID 記在 session 暫存，停止時只 kill 自己記下的 PID。
- `select-tests.mjs --base marble`：partial，1733 個測試選 1079 個、115 頁。建議指令含 `--project=forced-colors…`，該 project 會改寫 99 張 PNG，執行前需使用者決定。

## 第二輪（2026-10-21，D6 = A、D8 修訂）
轉換規則改為：
1. 只轉祖先鏈全是 `zk`、`div`、native 的節點（元件內不轉）。
2. div 只允許 `sclass` 屬性（`style`、`id`、事件等一律不轉）；label 只允許 `value`、`sclass`（`style` 等不轉）。
3. div 直接子節點含文字者不轉（ZK 會把裸文字包成隱含的 Label，`.z-label` 的字級與顏色會蓋過繼承值）。
4. label 的 value 含 `&`（除 `&quot;`、`&apos;`）者不轉（native 文字原樣輸出為 HTML，`&lt;foot&gt;` 會變成真正的標籤）。
5. div 子樹內有 `hflex=`、`vflex=`、`parent`、`Sibling` 等字樣者不轉（hflex 依父層 widget 計算；native 父層不是 widget）。
6. 轉換後畫面不同的頁面不轉，不補 class 讓它們過關（會逼出基準圖重拍）。

驗證（8106，轉換前後整頁截圖，路徑文字已正規化）：121 頁有轉換候選；109 頁像素相同、7 頁因原版每次載入就不同改以元素位置比對、5 頁不同。單一 worker 重測 12 頁後，4 頁仍不同，不轉：`progressmeter`（原因未查）、`utility/print`、`utility/responsive`、`utility/zindex`（label 的文字顏色由 `.z-label` 變為繼承）。其餘通過的 117 頁加手工重寫的 `signature.zul` 共 118 個檔案套用到本 worktree。

## 第三輪：Playwright partial（D7 = A，2026-10-21）
範圍：`select-tests.mjs --base marble` 的 partial，排除 `forced-colors`、`forced-colors-gallery` 兩個 Playwright project；對 8106 執行，891 個測試。對照組：同樣 5 個有失敗的 spec 檔對 8085（原始碼）執行。

結果：
- `screenshot.spec.ts` 等截圖類測試 0 個失敗，基準圖沒有變。
- 失敗 98 個，其中 26 個在 8085 也失敗（既有）；`font-size-equivalence` 有 68 頁剛好是 13px +3（記憶筆記記錄的跨伺服器差異：新建的 za11y 多一個 `.sr-only` live region），17 頁在 8085 也失敗。
- 真正由本次轉換造成：(1) `utility/opacity.zul`：spec 選取 `.z-label.pv-opacity-label`，該 label 被轉成 span。新增規則「sclass 含 `pv-` 的 label／div 不轉」後 `utility-additions.spec.ts` 18 個全過。(2) `signature.zul`：手工重寫（移除 colorbox、spinner、改成三個 signature），`gallery-scan` 的 `signature-02-customization.png` 與 `font-size-equivalence` 的 signature 基準需要重拍（待決定）。
- `timepicker.zul` 的 font-size 13px 498 → 503：同一台伺服器上轉換前後的像素與計數相同（503），差異來自 8106 與基準檔，不是轉換。
- `splitter` 的 gallery-scan 失敗單獨重跑通過（並行時的不穩定）。
