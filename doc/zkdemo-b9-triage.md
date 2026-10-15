# zkdemo B9 回報分流（2026-10-15）

來源：zkdemo-b5 交辦（zkdemo `tasks/new-demo/p4-b9-plan.md` §3「B9-Z」，加上訊息中新增的 Z13、JHLabs、rangeslider 超出範圍）。版本：zk 11.0.0.FL.20261014-Eval、marble 11.0.0.FL.20261014。zkdemo 端的探針與截圖在 `$S = /private/tmp/claude-501/-Users-hawk-Documents-workspace-zkdemo/14687e04-9b7a-4c90-8efc-39fba816829f/scratchpad/p4-b9/`。
文件類 Z3／Z4 的文件部分、Z7、Z9 已由 zkdoc `d1322d3` 修正，本文件不處理；Z12（zul-writer `--describe`）交 Skill Auditor。

以下判定都依原始碼（zk／zkcml `marble` 對照 `master` 的 IceBlue）；本 session 尚未在瀏覽器重現。

## Marble

| 項目 | 判定 | 動作 | 依據 |
|---|---|---|---|
| Z1 pdfviewer 文字層 | Marble 退化（O1） | 修正（ZK-6112） | IceBlue `zkex/.../pdfviewer/less/pdfviewer.less:53-73`：`overflow: hidden; line-height: 1.0`、`::selection`、`> span { color: transparent; position: absolute; white-space: pre; cursor: text; transform-origin: 0 0 }`。Marble `pdfviewer.css:31-35` 只有 `position: absolute; inset: 0; opacity: 0.2` |
| Z6 fisheye 標籤／圖片定位 | Marble 退化（O1、V4：JS 設定的 `left`／`top` 要生效） | 修正（ZK-6112） | IceBlue `fisheye.less`：`.z-fisheye, -text, -image { position: absolute }`、`-text { display: none }`（JS 顯示）、tooltip 樣式小卡、`.z-fisheyebar-inner { position: relative }`。Marble `fisheye.css` 把 bar 設成 flex（含 padding、gap），image／text 是 flex 子元素 |
| rangeslider 超出範圍 | Marble 退化（O1、V5：基本留白以 IceBlue 為準） | 修正（ZK-6112） | IceBlue `.z-rangeslider-horizontal .z-rangeslider-inner { margin: 40px 12px 30px }`（上方留提示框、下方留刻度標籤）、垂直 `margin: 12px 30px 12px 12px`。Marble 水平只有 `padding: 10px 0`（box 24px）；垂直 box 已照 IceBlue 的 50px，但刻度標籤仍超出約 10px（zkdemo 量測）。O7：修正真缺陷即使佔版面也採用，成本記入 release note |
| Z11 codeeditor readonly 外觀同可編輯 | 設計如此，不改（§3「Readonly field」：唯讀欄位看起來和可編輯欄位一樣；灰色或 38% opacity 只代表 disabled） | 無 | `codeeditor.css` 開頭註解已寫明此意圖 |
| Z8 captcha `frame="true"` 雙外框 | 需使用者決定（E3：要改 `doc/contracts/captcha.md` 的 c1／c2） | 待決 D1 | Marble `captcha.css` 加了 1px `outline-variant` 外框與 4px 圓角；IceBlue 沒有 captcha 樣式。引擎在圖片內自畫 1px 外框（`JHLabsCaptchaEngine.java:138-140`）。widget 是 `zul.wgt.Image` 的空子類別，不輸出 frame 狀態，CSS 無法只在 `frame="true"` 時拿掉外框 |

## ZK 核心（O2：兩個主題相同，開 Jira，由核心團隊修；送出前需使用者同意，E6）

| 項目 | 依據 |
|---|---|
| Z13 Fisheyebar 頁面捲動後放大失效 | `zkex/.../menu/Fisheyebar.ts:374-386` `_onBodyOut` 以 `pageX`／`pageY` 比對 `zk(BODY).revisedOffset()` + `BODY.offsetWidth／offsetHeight`。IceBlue `norm.less:7-9` 也有 `html, body { height: 100% }`，與 Marble `_reset.css` 相同，所以兩個主題都會發生。該檔與 master 無差異 |
| Z2 `Captcha.setNoise`／`setFrame` 不重畫 | `zul/.../Captcha.java:294-310` 只寫欄位；`setLength` 等會呼叫 `smartDrawCaptcha()` |
| Z3 rangeslider `markScale="0"` 無法隱藏刻度 | `Rangeslider.java:45` `_markScale = 0`、`:211` getter 在 0 時回 20、`:338` 在 0 時不送；用戶端預設 `'20'`。zkdoc 補充：若送出 0，用戶端 `redrawMarks_` 會無窮迴圈 |
| Z4 rangeslider `marks="…"` 在 ZUL 回 500 | `zul.xsd:2799`、`:3041` 宣告字串 `marks`；伺服器只有 `setMarks(Map<Integer,String>)`（`Rangeslider.java:250`）；`Rangeslider.ts` 的 JSDoc 寫字串形式 |
| Z5 執行中改 columnlayout `width` 不重新分配 | 依 zkdemo 量測（`$S/probe/col3.log`、`col5.log`）；`Columnlayout.ts` 的 `render()` 只在 `onSize` 與子元件增減時執行 |
| Z10 `zul.xsd` columnchildren 只接受 panel | `zul.xsd:1729-1736`；執行時接受任何元件 |
| JHLabsCaptchaEngine 字元起點超出左緣 | 依 zkdemo 量測（`captcha-verify-accepted.png`）；`JHLabsCaptchaEngine.java` 隨機起點未以 `imageWidth - textWidth` 夾住 |

## 下一步

- Marble 三項（Z1、Z6、rangeslider）：Sonnet 在 worktree 實作，另一個 Opus agent 驗證。派工：`doc/zkdemo-b9-impl-brief.md`；回報：`doc/zkdemo-b9-impl-report.md`。同一位工程師也拍 Z8 的比較截圖給 D1 使用。
- ZK 核心七項：Jira 草稿寫在 `doc/zkdemo-b9-jira-drafts.md`（含重複 issue 檢查），使用者同意後才送出。

## Jira 草稿結果（2026-10-15）

草稿：`doc/zkdemo-b9-jira-drafts.md`（Sonnet 撰寫，未送出）。JQL 與 CaseFoundry 都沒有找到重複的 issue（Z13 相關但不同：ZK-6031；Z10 的背景：ZK-523）。原始碼核對的修正：

- JHLabs：原主張「字元起點超出左緣」不成立（`JHLabsCaptchaEngine.java:164-189` 的 `left` 從 0 起只會增加；zkdemo 300 次統計的墨點都在 1–198px 內）。唯一可能的是窄圖加上多字元時右緣被切掉，要先實測，未實測前不送。
- Z3：用戶端 `redrawMarks_` 的無窮迴圈只在收到字串 `"0"` 時發生；伺服器送的是整數 0，會在 `Rangeslider.ts:391` 提早返回。草稿寫成用戶端缺少防護，不寫成 ZUL 可觸發。
- Z13：程式推論在 master 成立，但 zkdemo 沒有保存「拿掉 handler 後就正常」的輸出，`norm-menu.png`、`det-menu.png` 也不能當證據。送出前要在預覽站重現並截圖。
- Z13、Z5、JHLabs 需要手動附截圖（Atlassian MCP 無法上傳附件）。

## 實作與驗證結果（2026-10-15）

- 實作回報：`doc/zkdemo-b9-impl-report.md`（Sonnet）；驗證：`doc/zkdemo-b9-verification.md`（另一個 Opus agent，每項都以「改回 parent 規則」確認檢查會失敗）。
- 已 rebase 到目前的 marble（range-diff 內容相同；marble 期間的變動只涉及 Tree／Listbox）：
  - zkcml branch `zkdemo-b9`（worktree `ZK10/b9/zkcml`）：`eeb747f99` Z1、`6b187e5b5` Z6、`33623a6ce` rangeslider、`5d29d14b0` multislider（V6 同族）。
  - zk branch `zkdemo-b9`（worktree `ZK10/b9/zk`）：`b53e4279ba` `--zk-fisheye-fg` 預設改 `inverse-on-surface`（Z6 的前提；只改既有 component knob 的預設，不屬 E4）、`2698b15e5d` 3 張 gallery 基準（訊息已依驗證者更正高度）、`db78dad81f` contract。
- 判定：七個 commit 都 PASS。量測：pdfviewer 文字層灰字差 0px（還原 3,765px）；fisheye 8 種組合標籤都在指定邊外側，對比 10.86:1；水平 slider 0 個元素超出 box（還原 10–12 個），拖曳數值前後相同。
- 版面成本（寫進 release note）：水平 rangeslider 高 24→86px、multislider 28→86px，rail 短 28–36px。提示框空間一律預留，因為 `tooltipVisible` 只是 inline `display`，沒有 class 可判斷。
- 垂直 slider 右側提示框仍超出 box（rangeslider 7.8／12.4px，原 11.8／16.4；multislider 前後相同）：既有設計（`rangeslider.css` 註解）、相關 Jira ZK-6192，依 O10 不在本次範圍。
- 核心 bug 在 8087 重現：Z13（捲動後每次越過項目邊界就縮回，拿掉 handler 後 0 次）、JHLabs（右緣與左緣都會被切）、Z5（270／630px 不變）、新增 B4 pdfviewer（旋轉後文字層不轉、`--scale-factor` 未定義使選取對不齊；兩個主題相同，O2）。截圖：`doc/zkdemo-b9-core-evidence/`。
- 8087（Jetty 86435）仍在執行，worktree HEAD 已換到 rebase 後的 commit（jar 未重建）。

## 決定與結果（2026-10-16，使用者同意「都照建議」）

- D1（Z8）：選項 A，拿掉 Marble 的 captcha 外框與圓角，外框只由 `frame` 屬性決定（V4）。比較頁：https://claude.ai/artifact/8e39CEQvgiMDqTyAeLsgWo 。`doc/contracts/captcha.md` 的 c1／c2 已改。判定只依 V4，屬於一次性的裁示，`decision-principles.md` 不新增條目（§5）。
- D2：兩個 repo 的 marble 都已 fast-forward（未 push）。合併前 zk 的 B9 branch 再 rebase 到 `16e1c1c8a7`（range-diff 相同）。
  - zk：`c9e06da41f`（fisheye 標籤顏色 knob）、`cec4b892cb`（3 張 gallery 基準）、`64b5a47ef3`（contract）、`95c2511824`（Z8 captcha）。
  - zkcml：`eeb747f99`（Z1）、`6b187e5b5`（Z6）、`33623a6ce`（rangeslider）、`5d29d14b0`（multislider）。
  - 合併前在 8087 以重建後的 jar 重驗：captcha computed `border 0px none`、`radius 0px`（修正前 1px、4px）；`gallery-scan` 4 passed（fisheyebar、multislider、pdfviewer、rangeslider），`render-smoke` + `component-theming` 13 passed，跑完沒有新的 PNG。
- D3：Jira 已送出（Affects 11.0.0，截圖要手動附加，檔案在 `doc/zkdemo-b9-core-evidence/`）：
  - Z2 [ZK-6206](https://zkoss.atlassian.net/browse/ZK-6206) captcha `setNoise`／`setFrame` 不重畫
  - Z3 [ZK-6207](https://zkoss.atlassian.net/browse/ZK-6207) rangeslider `markScale="0"`
  - Z4 [ZK-6208](https://zkoss.atlassian.net/browse/ZK-6208) rangeslider `marks` 字串
  - Z5 [ZK-6209](https://zkoss.atlassian.net/browse/ZK-6209) columnlayout 改寬不重新分配（附 `b3-z5-*`）
  - Z10 [ZK-6210](https://zkoss.atlassian.net/browse/ZK-6210) `zul.xsd` columnchildren
  - JHLabs [ZK-6211](https://zkoss.atlassian.net/browse/ZK-6211) captcha 字被切（附 `b2-captcha-known-values-load1.png`、`b2-captcha-120x50x10-right-edge.png`、`b2-captcha-90x40x7-right-edge.png`、`b2-captcha-200x50x5-right-edge.png`）
  - B4 [ZK-6212](https://zkoss.atlassian.net/browse/ZK-6212) pdfviewer 文字層（附 `b4-pdfviewer-rot0-selection.png`、`b4-pdfviewer-rot90-selection.png`、`b4-pdfviewer-rot0-selection-with-scale-factor-1.png`）
  - Z13：另一個 session 已於 2026-10-15 送出 [ZK-6205](https://zkoss.atlassian.net/browse/ZK-6205)（`doc/zkdemo-b9-z13-fisheyebar-jira-draft.md`），本 session 不重複送。
- 送出時與草稿的差異：刪掉沒有實測的 Workaround（Z3「先設非 0 再設 0」、Z5 `invalidate()`、B4 `--scale-factor` 腳本）；Z2 的 Workaround 只留 `randomValue()`（已確認它經 `setValue()` 重畫），「重設同一個 fontColor」會提早 return，所以刪掉；Z10 只留已實測的 `label`；附件名稱改成實際檔名。
