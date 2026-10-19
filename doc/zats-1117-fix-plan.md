# Jenkins #1117：Marble 缺陷修正與 UNSURE 重跑計畫

分類結果見 `doc/zats-1117-failure-triage.md`；逐筆資料（含 `decider`）見 `doc/zats-1117-triage-data.json`。

## Worktree

| 用途 | 路徑 | 起點 | 規則 |
|---|---|---|---|
| 修正 | `ZK10/z1117/{zk,zkcml}` | 分支 `marble-1117-fixes`，自 `marble`（zk `ce6c74b62f`、zkcml `e3bca9748`） | 只有工程 agent 寫入；不 commit，等使用者同意 |
| 重跑 | `ZK10/z1117rr/{zk,zkcml}` | detached，同上 commit | 只允許暫時性的探針與 build 輸出；不 commit；用完可刪 |

共通限制：
- 測試以 zktest 的 composite build 執行（`zktest/settings.gradle` 解析 `../../zk`、`../../zkcml`），不執行 `publishToMavenLocal`（會影響共用的 `~/.m2`）。
- 不碰主工作樹 `ZK10/zk`、`ZK10/zkcml`，不碰 8085 等其他 session 的伺服器；停止程序只 kill 自己記下的 PID。
- JDK：`withjdk.sh 17`（Jenkins 用 OpenJDK 17）。

## 工作一：重跑（Sonnet 5.5，`z1117rr`）

1. 跑通一個測試，把可用的建置與執行步驟寫進 `ZK10/z1117rr/RERUN-NOTES.md`，供修正 agent 參考。
2. 在未修改的 `marble` 上跑基準：44 筆 `MARBLE_REGRESSION`、1 筆 `FIXED_SINCE_BUILD`、31 筆 `UNSURE`，記錄通過與失敗，並與 Jenkins 比對。
3. 仍失敗的 `UNSURE`：依資料檔 `decider` 欄量測，給出最終分類。

## 工作二：修正 26 個原因（Sonnet 5.5，`z1117`，依序執行）

同一個 worktree 共用 build 輸出，所以各組依序執行，不並行。每組：讀 `.claude/skills/marble-theme/SKILL.md` 與 `reference/decision-principles.md`；修正前先跑相關測試確認失敗；修正；再跑確認通過；跑相關的 Playwright 視覺測試，不更新基準圖；把變更與結果寫進 `ZK10/z1117/FIX-LOG.md`。

| 組 | 原因 | 主要檔案 |
|---|---|---|
| G1 mesh | `mesh-th-padding`（另含 21 筆 `TEST_GEOMETRY`）、`mesh-table-css-layout`、`listbox-hidden-first-header-body-col`、`frozen-sticky-groupfoot-missing` | zk `grid.css`、`listbox.css`、`tree.css`、`frozen.css` |
| G2 日期 | `calendar-text-pointer-events`、`calendar-weekofyear-pointer-events`、`drb-readonly-button-pointer-events`、`timepicker-hardcoded-height`、`input-family-vertical-align-mismatch` | zk `calendar.css`、`datebox.css`；zkcml `daterangebox.css`、`timepicker.css` |
| G3 容器 | `groupbox-6071-not-interactable`（重跑新增）、`tab-content-pointer-events`、`vertical-tabbox-panels-stretch`、`accordion-border-height-math`、`portal-untitled-panel-drag-handle-hidden`、`groupbox-root-border-vflex-min`、`hlayout-cssflex-valign-missing`、`absolutelayout-no-height` | zk `tabbox.css`、`panel.css`、`groupbox.css`、`layout.css`、`absolutelayout.css`；zkcml `portallayout.css` |
| G4 EE | `zkmax-listbox-stacking-css-missing`、`zkmax-grid-stacking-descendant-leak`、`chosenbox-inplace-hidden`、`nav-open-collapsed-topmost`、`multislider-mark-label-pointer-events`、`coachmark-content-height` | zkcml zkmax／zkex |
| G5 其他 | `menupopup-no-scroll`、`slider-thumb-margin-offset`、`progressmeter-css-transition`、`checkbox-baseline-misalign` | zk `menu.css`、`slider.css`、`progressmeter.css`、`checkbox.css` |
| G6 popup／輸入（重跑新增） | `combobox-popup-top-at-input-top`、`tooltip-after-popup-click`、`combobox-hflex-min-width`、`errorbox-before-end-offset` | zk `combobox.css`、`popup.css`、`errorbox.css` 等 |
| G7 版面（重跑新增） | `tree-rod-avg-row-height`、`grid-popup-hflex-min-no-regrow`、`tabs-scroll-after-font-swap`、`cropper-selection-zero-width`、`auxhead-detach-hflex-relayout`、`splitter-vbox-width` | zk mesh／tabbox／splitter；zkcml cropper |

G6、G7 有幾個原因還沒找到（`tree-rod-avg-row-height`、`grid-popup-hflex-min-no-regrow`），工程 agent 先查出原因再修。如果需要改 widget TS，就停下回報。

## 工作三：驗證（Opus 5.5，唯讀）

所有組完成後，由獨立的 Opus agent 在 `z1117` 跑全部 44 筆 regression 與 21 筆表頭 padding 測試，以及 Playwright 視覺測試，逐筆回報通過與否和證據。驗證失敗回報使用者，不自動重試。

## 狀態

| 項目 | 狀態 |
|---|---|
| 重跑 | 完成：76 筆中 71 筆失敗、2 筆通過、3 筆需 Docker 沒跑；31 筆 `UNSURE` 重新分類為 17 筆 regression、9 筆 geometry、4 筆 flaky、1 筆仍 `UNSURE`。結果寫在 triage 文件末節 |
| G1 | 完成（工程 agent 自報，待驗證）：34 個測試通過（4 個原因的 13 筆 regression 加 21 筆表頭 padding）；Playwright 用 repo config 比對全過，沒有更新基準圖。細節見 `ZK10/z1117/FIX-LOG.md` |
| G2 | 完成（工程 agent 自報，待驗證）：13 筆中 12 筆通過；`B36_3002536` 需要 Docker／Firefox，本地沒跑。Playwright 沒有更新基準圖 |
| G3 | 修正完成（工程 agent 自報，待驗證）：12 筆中 10 筆通過；`B110_ZK_6071_TouchTest` 需要 Docker，沒有跑；`F61_ZK_970` 兩筆改為 `ICEBLUE_ASSUMPTION`。forced-colors 的雙層框線已修好（`_forced-colors.css` 不再替 root 加框線；`forced-colors.spec.ts` 改從 header 讀框線） |
| G4 | 完成（工程 agent 自報，待驗證）：G4.json 全部通過。同 class 中有 1 個不在清單內的 method 失敗，PM 已裁定，見下 |
| G5 | 修正完成（工程 agent 自報，待驗證）：4 個原因中 3 個已修；slider 那一個改為 `CORE_BUG`（D3）。checkbox 造成的 focus-scan 回歸已修好（toggle mold 加 `outline-offset:-2px`） |
| G6 | 完成（工程 agent 自報，待驗證）：4 個原因中 3 個修好；`F50_3316591` 的第一個失敗已修，剩下的斷言改為 `ICEBLUE_ASSUMPTION` |
| G7 | 修正完成（工程 agent 自報，待驗證）：8 筆中 6 筆通過；`B50_2980383` 改為 `CORE_BUG`（D4）；`B90_ZK_4491` 已通過：通用 `.z-popup` 移除了 `min-width:160px`，menupopup 保留自己的 min-width |
| 驗證 | **FAIL**（Opus，2026-10-16）。必須通過的 78 列：75 通過、1 失敗、2 列需要 Docker 沒有跑；另有 3 個新回歸，還有 1 個沒有測試涵蓋的行為回歸。詳見下方「驗證結果」，資料在 `$S/verify2/results.json`。依計畫不自動重修，等使用者決定 |

### G1 的變更與 PM 裁定

- 檔案（未 commit）：zk `grid.css`、`listbox.css`、`tree.css`、`auxhead.css`、`frozen.css`、`zkpreview/src/test/playwright/mesh-header-footer.spec.ts`；zkcml `zkmax/.../css/tablet/_mesh.css`。
- 4044 的實際原因是 th 上的 `position:relative`。它讓 jQuery `position()` 的 `offsetParent` 從 TABLE 變成 th，量出的位置因此差了一個水平捲動量。修正把 `position:relative` 移回 content div。
- 移除 header/body/footer table 的 `width:100%`、`table-layout:fixed`：單獨這項不會讓 4044 通過，但 `MeshWidget.ts:55`、`:102` 在量測時會清掉 inline 的 `tableLayout`，樣式表裡的 `fixed` 會讓這個清除沒有效果，所以保留。巢狀 table 另外用 `.z-*-body table table` 維持原本的外觀。驗證者要確認 gallery `grid-detail` 的差異是 0。
- `mesh-header-footer.spec.ts` 的修改：padding 移進 content 之後，原本把 content 的 border box 當成 label box 的量法不再成立，所以改成扣掉 padding。PM 接受這項修改，理由依 V4：不改的話，th 的 padding 會被 widget 重複計算。驗證者要逐條斷言確認強度沒有降低，特別是 select-all 那條，改成讀 content 的 padding 之後，不能變成 0 = 0 的空比較。
- 帶 label 的 tree select-all 表頭被截斷時不顯示 ellipsis：這和既有的 `:has(img)` 是同一類代價。先記為後續項目，這一輪不處理。

### G2 的變更與 PM 裁定

- 檔案（未 commit）：zk `calendar.css`、`input.css`；zkcml `daterangebox.css`、`timepicker.css`。
- 月曆文字、週數格、`.z-calendar-icon[disabled]` 移除 `pointer-events:none`。`Calendar.ts:972`、`:1011` 已經用 `disabled` 擋掉點擊，所以這條 CSS 是多餘的。
- timepicker 的高度改用 datebox 那一組 token（`--zk-control-height`、`--zk-input-height`），高度從 36px 變成 40px。這是預期的結果，gallery `timepicker`、`component-theming` 的高度跟著改變。
- `.z-textbox` 的 `vertical-align` 從 `middle` 改成 `baseline`，`textarea` 維持 `middle`。PM 依 V6 接受：同一列的輸入元件要有同一條底邊，而且應該修共用規則，不是個別元件。造成的影響是 grid／listbox 中只放 textbox 的列矮 2px（gallery `grid-header`、`listbox-header`）。依 V6，這類較大範圍的影響只需回報、不需詢問。驗證者要確認這兩張圖的差異只來自這項修改，確認後才用 `--update-snapshots=changed` 重拍這兩張，並在 commit 前檢查檔案數。
- font-size spec 每頁的 `13px` 計數都多 3：agent 判斷是全新 worktree 的 za11y 漂移，但沒有在未修改的版本上對照。驗證者要在 `z1117rr` 跑同一個 spec 確認。
- 額外發現的 `B110_ZK_6131` 三個 method 和 `F61_ZK_1175#testDatebox`，triage 時已經歸為 `TEST_GEOMETRY`（separator 和 disabled 的 `pointer-events`、popup 蓋住按鈕），屬於 D1 的範圍，這一輪不處理。

### G3 的變更與 PM 裁定

- 檔案（未 commit）：zk `tabbox.css`、`groupbox.css`、`layout.css`、`absolutelayout.css`、`zkpreview/.../component-theming.spec.ts`；zkcml `portallayout.css`。
- tab 和 accordion 的文字移除 `pointer-events:none`。垂直 tabpanels 加 `align-self:flex-start`。accordion 的分隔線從 `border-bottom` 改成 `border-top`，固定高度的 accordion 展開後，第一段因此少 1px（chromium tabbox gallery 有 12818 px 的差異，這是預期結果）。hlayout 補上 valign 的 `align-items`。absolutelayout 補上 `height:100%`。portallayout 沒有標題的 panel 補上拖曳把手。
- groupbox 依 V5 改回 IceBlue 的結構：框線從 root 移到 header 和 content，root 不再 `overflow:hidden`。PM 接受這個改法，連帶的 `component-theming.spec.ts` 改成從 header 讀 border-color，也一併接受。
- 這個改法有一個缺陷：`_forced-colors.css` 仍然在 root 畫框線，所以 forced colors 下有兩層框線，`vflex=min` 又會少 2px。PM 判定要修，已退回 G3 agent 處理。
- `F61_ZK_970#testFirstTabbox`、`#testThirdTabbox`：修正後，測試改點 accordion 的關閉圖示，但 Marble 依 owner 的裁定 c11a（2026-06-07，`tabbox.css:670-678`）不顯示這個圖示。依 V1 維持裁定，這兩筆改為 `ICEBLUE_ASSUMPTION`，歸到 NS3。
- agent 看到 `B102_ZK_5681_2`、`F100_ZK_5213`、`B80_ZK_3236`、`F102_ZK_5518` 也失敗，這些都不是 G3 的原因造成的。前三筆在 triage 時已經分類；`F100_ZK_5213` 和 `F102_ZK_5518` 不在 Jenkins 的失敗清單裡，要由驗證者在 `z1117rr` 用未修改的版本對照，確認是不是 G1–G3 造成的。

### G4 的變更與 PM 裁定

- 檔案（未 commit），都在 zkcml zkmax：新增 `sel/css/listbox.css`（從 `eea2b6428^` 的 `listbox.less` 移植 stacking 區塊，改用 Marble token，結構比照 grid 的 stacking，依 V6）；`grid/css/grid.css`（改用 child combinator，巢狀 grid 不再變成卡片）；`inp/css/chosenbox.css`（inplace 改成 IceBlue 的結構，包括 `inline-block` 和 padding）；`nav/css/nav.css`；`slider/css/multislider.css`；`nav/css/coachmark.css`。listbox 已經在 `lang-addon.xml:1166` 註冊為 `sel/css/listbox.css.dsp`，原本缺的只是檔案本身。
- `F110_ZK_6110_ResponsiveListboxTest#testFocusRingOnCardNotPerCell`（不在 Jenkins 的失敗清單裡）：測試用滑鼠點一列，然後要求卡片的 outline 是 2px。
  - **agent 的診斷有誤：** 它說 3px 來自 `--zk-focus-ring`，但這個 token 其實是 2px（`_colors.css:161`）。真正的原因是 Marble 從 `3c28eb1d44` 起，只在鍵盤操作時顯示列的 focus ring（`listbox.css` 的 `:has(.z-focus-a:focus-visible)` gate）。滑鼠點擊不會畫出 outline，而 Chrome 對沒有 outline 的元素回報的 `outline-width` 是預設值 `medium`（3px）。
  - **PM 裁定：** §3 的「Focus on list rows」規定只在鍵盤移動時顯示。這個 method 要求滑鼠點擊後也有 ring，這是 IceBlue 的行為，所以改為 `ICEBLUE_ASSUMPTION`（NS3）。Marble 不加 2px 的寫死值。
  - **驗證者另外要確認：** stacking 模式下用鍵盤移動時，卡片（`tr`）顯示一圈 2px 的 ring，而且各 cell 的 focus box-shadow 已清除。
- zkpreview 沒有 listbox stacking 的頁面，所以這部分只有 zktest 驗證，沒有截圖比對。

### G5 的變更與 PM 裁定

- 檔案（未 commit）：zk `menu.css`（`.z-menupopup` 加 `max-height:100%`，並讓 `overflow-y:auto`）；`progressmeter.css`（移除 width transition，`Progressmeter.ts` 的 jQuery animate 沒有讀這個 CSS）；`checkbox.css`（`.z-checkbox` 加 `vertical-align: middle`）。
- checkbox：只改 switch／toggle 的話，label 仍然差 5px，所以改成全部 mold 一致。PM 依 V6 接受。chromium `checkbox › gallery` 高度從 717 變成 712，這是預期結果。
- 這個改法讓 focus-scan 的 `checkbox` 出現回歸：mold 的 1px outline 被 `.z-overflow-x-auto` 裁掉。focus 指示被裁掉是無障礙缺陷，已退回 G5 agent，要求在 `checkbox.css` 內修好，不能改預覽頁或 spec 來遮掉。
- slider：移除 thumb 的 `margin-left:-10px` 沒有效果，已經還原，`slider.css` 沒有變更。真正的原因在 `Slider.ts:524` 的 `(pos - min) % step`：浮點取模讓剛好落在刻度上的值少一格（`(10.2-10)%0.05` 得到 0.0499999…）。IceBlue 能通過，只是因為它的落點剛好不在刻度上。這是 widget TS 的問題，屬於核心團隊（§1），所以 `F70_ZK_2085`、`F50_3028270` 改為 `CORE_BUG`。要不要開 Jira 見 D3。
- focus-scan 回歸的原因：toggle 的 mold 填滿 32px 的 box，改成 `middle` 後，它剛好貼到容器底緣，所以 outline 被裁掉。真正的 focus ring 在同一個位置也會被裁掉。修正方式是給 toggle mold 加 `outline-offset:-2px`，把 ring 畫在 box 裡面，和 listbox、grid、panel 的作法一樣。B95_ZK_4585 仍然通過，focus-scan 和 forced-colors 都通過。
- `B70_ZK_2419`（`TEST_GEOMETRY`，`slider-default-full-width`）修正前後都失敗，屬於 D1 的範圍。

### G6 的變更與 PM 裁定

- 檔案（未 commit）：zk `combobox.css`、`input.css`、`popup.css`、`zkpreview/.../component-theming.spec.ts`、`zkpreview/.../screenshot.spec.ts`；zkcml `zkmax/.../css/tablet/_inputs.css`。
- combobox：root 從 `inline-flex` 改成 IceBlue 的 `inline-block`，button 改成 absolute。原因是 root 帶 inline `position:absolute` 時，computed display 會變成 `flex`；而 `dom.ts:1230` 的 `offsetHeight()` 只有在 `block` 時才讀實際高度，所以 popup 沒有加上 input 的高度。`B96_ZK_5044` 也因為這項修正一起通過。
  - input 用 `fill-available` 加 `margin-right`，不用 root padding：`Widget.ts:4161` 的 `getContentEdgeWidth_` 會把 root padding 計入 hflex=min，每開一次 popup 寬度就多 32px。
- popup：外觀從外層 `.z-popup` 移到 `.z-popup-content`，改回 IceBlue 結構。原因是外層的圓角讓角落那個像素 hit-test 不到，Selenium 的 pointer 落在那裡時不會觸發 mouseout，tooltip 就一直不消失。gallery 沒有新差異。
  - 3 處 spec 的量測改讀內層。PM 依 V5 接受，理由和 G1、G3 一樣；驗證者要逐條檢查。
- `F50_3316591`：
  - Jenkins 回報的第一個失敗確實是 Marble 造成的。invalid 加上 focus 時，`.z-textbox.z-textbox-invalid:focus`（0,3,0）蓋掉了 textarea 的上下 padding，textarea 高度從 116 變成 100，下方的 radio 跟著移位，點擊因此沒有點中。已在 `input.css` 補回 textarea 的上下 padding。
  - 剩下的斷言（`:89`，389 vs 381）假設的是 IceBlue 只在單邊加 `ARROW_PADDING`。但 `doc/contracts/errorbox.md:34-35` 規定要用對稱的 `--zk-errorbox-beak`（V1），所以維持 Marble 的作法，這一列改為 `ICEBLUE_ASSUMPTION`。
- 待辦：bandbox、datebox、timebox、spinner 也是 `inline-flex`，被絕對定位時，`dimension().height` 會量到 0。依 V6，同一類元件要一起修，但這一批沒有對應的失敗測試，而且 G2 讓 textbox 以 baseline 對齊，正是依賴這些元件目前的 baseline。所以這一輪不改，另立後續項目，先寫重現測試再修。

### G7 的變更與 PM 裁定

- 檔案（未 commit）：zk `tree.css`、`listbox.css`、`grid.css`；zkcml `zkmax/.../cropper/css/cropper.css`。
- `tree-rod-avg-row-height`：原因不是列高。`tree.css:567` 的 `.z-tree-loading` 把會捲動的 body 改成 absolute flex 置中，scrollHeight 從 423600 掉到 211570，scrollTop 被夾到 111637。修正後 tree、grid、listbox 的 `-loading` 只保留 `background-color`。依 V6，grid 和 listbox 這兩個沒有專屬測試的元件一起改了；驗證者要另外跑這兩者的 ROD 捲動。`B96_ZK_4910` 需要 Docker，這筆的結果是從同機制推論的。
- cropper：全域 `_reset.css:96` 的 `img{max-width:100%}` 讓 Jcrop 量到的寬度是 0。修正改成 `.z-cropper img { max-width:none }`。
- `F70_ZK_112`：`.z-cell` 的 `nowrap` 規則改成只作用在 `.z-grid .z-cell`，和 IceBlue 的範圍一致。
- `B90_ZK_4443`：G1 修好後已經在 2px 容忍值內通過，這一組沒有再改。
- `B90_ZK_4491`：通用 `.z-popup` 的 `min-width:160px`（`popup.css:12`）是從 menu 的寬度複製過來的（`2100200284`），沒有寫原因，而且和 ZK 的 hflex=min 衝突。依 V4 移除，menupopup 保留自己的 min-width。已退回 G7 agent 處理，並請它順便確認 `B85_ZK_3898`、`B90_ZK_4293` 兩筆 `popup-min-width-covers-neighbour` 是否因此通過。
- `B50_2980383`：重跑時說 font swap 假設「已排除」，這個結論不成立，因為重跑只量了字型載入之後的狀態。這次在 mount 當下量到 `document.fonts` 還在 loading，Tabs 用的是舊寬度。CSS 修不了：`block` 無效，`optional` 會讓第一次載入時不顯示 Inter。這個問題是 Marble 的 web font 觸發的，但修正要在 widget TS 做，所以歸為 `CORE_BUG`，要不要開 Jira 見 D4。

- popup `min-width` 移除後的結果：`B90_ZK_4491`、`B90_ZK_4339`、`B90_ZK_4293` 通過。`B85_ZK_3898` 仍失敗，它用的是 menupopup，和這次修改無關。`B96_ZK_4525`（不在 Jenkins 的失敗清單）失敗，`B103_ZK_5988`、`B86_ZK_4053` 沒有產生結果，這三項都交給驗證者到 `z1117rr` 對照。Playwright 沒有新的差異。

## 驗證結果（Opus，2026-10-16）

結論：**FAIL**。證據在 `$S/verify2/`（`results.json`、`out/`、`shots/`、`beh/shots/`、`pw-diffs/`）。

| 項目 | 結果 |
|---|---|
| 必須通過的 78 列 | 75 通過；`B101_ZK_5594` 3/3 失敗；`B110_ZK_6071_TouchTest`、`B96_ZK_4910` 需要 Docker，沒有跑 |
| 全套 zktest（z1117，4h41m，3664 個） | 3523 通過、139 失敗；不在 targets 內的有 49 個：22 個既有（z1117rr 也失敗）、13 個 flaky、11 個 Docker 環境問題、3 個**新回歸**（`B85_ZK_3921`、`B86_ZK_3197`、`B96_ZK_4525`：z1117 3/3 失敗，z1117rr 2/2 通過） |
| Playwright（22 個 project） | 869 通過、136 失敗，失敗的都是 130 個 font-size 加上 6 個已接受的差異；沒有 PNG 變動。6 張已接受的圖都和 z1117rr 逐列比對過，差異只出現在裁定範圍內，只有 combobox 例外（見 F2）。`grid-detail` 差 0 px |
| font-size | z1117rr 同樣是 130 頁失敗，其中 129 頁的差異完全相同，判定為環境漂移 |
| spec 斷言 | 7 組都做了 mutation，每組都證明會失敗；G1 select-all 量到 20=20、36=36、48=48，不是 0=0 |
| 行為 | G4 stacking focus ring、forced-colors groupbox、combobox 對齊與 popup 位置：通過；ROD 捲動位置：失敗（F1） |
| 衛生 | 沒有寫死的顏色；沒有動到 `.css.dsp`、TS、Java、zktest |

發現：
- **F1（嚴重，G7）**：拿掉 `-loading` 的 layout 之後，ROD 的捲動位置會跳。
  - `B101-ZK-5594`：`scrollTop(8700)` 之後跳到 27811，z1117rr 停在 8700。
  - `B86-ZK-3197`：設成 1000000，結果是 5006348；注入舊規則後恢復正常。
  - `B85_ZK_3921` 很可能是同一個原因。
  - 原本要修的 scrollHeight 縮水確實修好了（`B86-ZK-4112` 維持 13245000）。
- **F2（嚴重，G6）**：有設 `width`／`hflex` 的 combobox，input 卡在 intrinsic 寬度 173px，`fill-available` 沒有效果。
  - 寬度 120 時重疊 85px，寬度 240 時留出 35px 空隙、外框斷開。
  - 實際頁面也受影響：`usecase/item-editor.zul` 有 395px 的空隙，`onboarding-wizard`、`utility/grid-layout`、`account-settings` 也有。
  - gallery 只掃 `web/*.zul` 根目錄，所以沒抓到。
- **F3（G6 或 G7）**：`B96_ZK_4525`「Tooltip was hidden」是新回歸，原因還沒確定，可能是 popup 外層改成方形後可以被 hit-test，或是 min-width 被移除。
- **F4（G3）**：`forced-colors.spec.ts` 的 groupbox 斷言被削弱。header 的框線在一般模式下就存在，所以這條斷言不再驗證 forced-colors 規則。目前狀態實際量到是一層框線；要補一條 root `borderTopWidth == 0` 的斷言。
- **F5（記錄）**：zkmax 新增的 `sel/css/listbox.css:30` 有 `width: 100% !important`。它是從 `eea2b6428^` 的 less 移植過來的，和 zkmax `grid.css:36` 相同，但仍然屬於這次 diff 新增的 `!important`（decision-principles §4 E5）。
- 驗證者在跑 targets 時，自己在 8117 起了 preview 並重建了 jar。那段時間跑的 21 個 method 已經作廢，改用乾淨的重跑結果。

### 驗證後的追查與裁示（2026-10-16）

- **F1 根因（已實測）**：IceBlue 在 `.z-grid-body`、`.z-listbox-body`、`.z-tree-body` 上設了 `overflow-anchor: none`（ZK-3263、ZK-4733；`e1f66aac36` 的 `grid.less:48`、`listbox.less:61`、`tree.less:47`），`2100200284` 改寫成 Marble CSS 時漏掉了。Marble 舊的 `-loading` 規則會在載入時改變捲動區本身的版面，碰巧擋掉了 Chrome 的捲動錨定；G7 把這段版面設定拿掉之後，缺口就露出來了。
  - 探針實測：在 z1117 的三個 body 規則暫時加上 `overflow-anchor: none`，`B101_ZK_5594`、`B86_ZK_3197`、`B85_ZK_3921`、`B96_ZK_4834` ×2 全部 PASSED（log 在 `$S/verify2/probe-f1.log`）。探針已移除。
  - 說明頁：https://claude.ai/artifact/APTV5jc2bSZhkdrAu888Uq
  - 建議的修法：保留 G7 對 `-loading` 的清理，把 `overflow-anchor: none` 補回三個 body 規則。另外要用 IceBlue LESS 掃描一次，找出其他只影響行為、Marble 卻漏掉的宣告。
- **D5（已決：選項 A，2026-10-16）**：派出一個 Sonnet 工程 agent，依序處理 F1（補回 `overflow-anchor`）→ F4（補上 root 框線為 0 的斷言並做 mutation 驗證）→ F2（combobox 填滿）→ F3（用二分法找出 4525 的原因）。完成後由 Opus 局部重驗。同時派一個 Sonnet agent 唯讀掃描 IceBlue LESS，找出只影響行為、Marble 卻漏掉的宣告，輸出到 `$S/sweep/`。
- **D3（已決：選項 A）**：Jira 草稿在 `doc/zats-1117-jira-drafts.md`，使用者確認後才送出。
- **D1**：使用者要求先說明分類，說明頁：https://claude.ai/artifact/Ag1u8FkaTErzTWcaUZDoAf 。仍需改測試的有 50 筆：點擊被拒（`pointer-events:none`）19 筆、寫死 IceBlue 尺寸 20 筆、點擊位置被蓋住 8 筆、拖曳太快 3 筆。
- **D1（已決：選項 A，2026-10-16）**：由使用者在另一個 session 執行，計畫在 `doc/zats-1117-test-rewrite-plan.md`（worktree `ZK10/z1117t`，port 8119）。
- **D6（已決：選項 A）**：保留 zkmax `sel/css/listbox.css:30` 的 `width: 100% !important`，與 zkmax `grid.css:36` 一致（V6）。

### `pointer-events: none` 的設計理由（zk-1e 調查，2026-10-16）

完整內容見 `doc/marble-pointer-events-rationale.md`（決策編號以該文件為準）。

- Marble CSS 共有 147 個 `pointer-events` 宣告，分成三類：
  - 約 41 個用在 state layer 或裝飾用的 `::before`／`::after`，有書面理由。
  - 約 49 個是整個 disabled 元件設 `none`，只有 breadcrumb 寫了「defense-in-depth」。
  - 約 17 個設在元件內部零件（icon、text、content），沒有任何書面理由，全部隨 `2100200284`／`eea2b6428` 一起進來。
- 確認會和 widget 讀 click target 衝突的只有 `.z-calendar-text`，G2 已經移除。`.z-daterangebox-readonly` 按鈕這條也已在 G2 處理。其餘的內部零件（menu、tab、paging、sorticon）都不影響功能；sorticon 設 `none` 之後，target 會落在 header 本體上，反而更穩定。
- **PM 裁定：** 類別一的 19 筆維持改測試，不改 CSS。內部零件的 `none` 沒有破壞 widget 行為，測試改成點宿主元素，或用 `Actions` 在相同座標點擊，就和真人操作一致。disabled 元件依 §3 的「Cursor」規則（只有可點的地方顯示 pointer，其餘用 default），維持 `none`。
- zk-1e 轉達使用者在 `doc/marble-pointer-events-rationale.md` 的 D1 選了選項 A：disabled 元件保留 `none`。這和本節的 PM 裁定一致。本計畫的 D1（50 筆測試由誰改）仍然待決。
- 後續項目：有 45 條 disabled／readonly 規則同時寫了 `cursor: not-allowed`／`default`，因為 `none` 的關係，這些宣告不會生效（rationale 文件的 D1 選項 A：刪掉沒有作用的 `cursor` 宣告）。這不影響 #1117，另外處理。

### IceBlue 行為性宣告掃描（Sonnet，唯讀，2026-10-16）

資料在 `$S/sweep/behavioural-diff.json`，共 1395 列：KEPT 701、CHANGED 151、MISSING 543。只有 high／medium 的 105 列逐列讀過；low 的 589 列是自動比對的結果，抽樣有 2 到 3 成誤報，數字只能當上限。

High（MISSING，**都只讀過 CSS 和 TS，沒有在瀏覽器確認**）：
1. grid／listbox／tree body 的 `overflow-anchor:none`：就是 F1，已經在修。
2. grid／listbox／tree footer 的 `overflow:hidden`：`MeshWidget.ts:1319` 等處會寫 `efoot.scrollLeft`，少了這條，橫向捲動時 footer 可能不會跟著動。
3. `.z-frozen-right-col` 的 `position:sticky`（ZK-4212）：右側凍結欄可能不會固定。
4. cell 的 `position:relative; z-index:0`（ZK-3864）：cell 的內容可能蓋過 sticky 凍結欄。
5. `.z-dd-stackup` 的 z-index／position：拖曳 window、splitter 時，這層 overlay 會變成一般排版的 div，不再擋住 iframe（ZK-817）。
6. `.z-toolbar-popup > *` 的 `display:table`（ZK-5617）：**`B103_ZK_5617Test` 在驗證者的全套測試中 PASSED**，可見規則表上 MISSING 不一定代表功能壞了。
7. `.z-anchornav-fixed` 的 `position:fixed`（ZK-4386）。
8. tbeditor 的 modal／overlay（ZK-2698／3336）。
9. pdfviewer text layer 的 `position:absolute`（ZK-4395）。
10. barcodescanner canvas 的 `position:absolute`（ZK-4095）。

Medium 另有 67 列，例如 inplace 按鈕用 `visibility` 或 `display`（ZK-2216）、`#zk_proc` 遮罩（ZK-5017）、daterangebox 外月份的日期、menu／nav popup 的 z-index。

**處理方式：** 第 6 項說明只讀 CSS 會有誤判，所以先派 agent 用瀏覽器逐項重現（Marble 對照本地 IceBlue jar，用各 Jira 的測試頁），確認是真的缺陷後，再排成 G8 修正。這項工作會用到 `z1117rr` 的建置，要等 F1–F4 那一輪不再使用 `z1117rr` 後才能派出。

### F1–F4 修正結果（Sonnet，2026-10-16；工程 agent 自報，待 Opus 重驗）

- **F1**：三個 body 補上 `overflow-anchor: none`，24 個 class、37 個 method 全部 PASSED，其中包括 19 個額外挑選的 ROD class。
- **F4**：`forced-colors.spec.ts` 加上 root 四邊 `border*Width == 0px` 的斷言；注入 root 框線後，這條斷言會失敗。
- **F2**：`.z-combobox-input` 加 `width: stretch`，`.z-combobox-button` 加 `margin-left:-32px`，抵銷 hflex=min 把 32px 算兩次的問題（tablet 版改用 `--zk-touch-target-min`）。各種寬度、hflex、四個預覽頁量到的間隙都是 0。lightningcss 會把 `-webkit-fill-available` 這組 fallback 壓成只剩 `width:stretch`。
- **`B96_ZK_5044`（仍然失敗）**：測試頁開了 tablet UI，combobox 設 `hflex="min"`。ZK 只用 comboitem 的文字算出 root 寬 77.3px，所以 input 填滿後只有 45px。G6 之所以通過，是因為 input 溢出了 root，這和 width 120 時 input 蓋住按鈕是同一個缺陷。Jenkins master（IceBlue）4 次都通過，但本地的 IceBlue jar 也失敗，而這個 jar 已經過期。**PM 裁定：** 不接受直接讓它失敗，也不加會影響所有 combobox 的最小寬度。先查清楚 IceBlue 的 tablet 規則在 hflex=min 下，input 為什麼能超過 80px（併入 IceBlue 規則瀏覽器確認那個 agent，唯讀分析），查清後再決定。
- **F3 `B96_ZK_4525`**：這是 flaky 測試，不是每次都失敗。舊結構 6/6 通過，目前 4/6，競態原因見 `doc/zats-1117-test-rewrite-plan.md`「追加」一節。CSS 沒辦法同時滿足它和 `B90_ZK_4339`。**PM 裁定：** 改測試，已列為改測試計畫的第 51 筆。popup.css 維持 G6／G7 的內容。

目前在跑：
- Opus 局部重驗，在 `z1117`／8117，結果寫到 `$S/verify3/`。
- IceBlue 規則的瀏覽器確認，加上 5044 的分析，在 `z1117rr`／8118，結果寫到 `$S/sweep-verify/`。

### IceBlue 行為規則的瀏覽器確認（Sonnet，2026-10-16）

結果在 `$S/sweep-verify/results.json`，截圖在 `$S/sweep-verify/shots/`。每一項都已經在 Marble 頁面上用 `addStyleTag` 注入建議的 CSS 試過，注入後的行為和 IceBlue 相同。

| # | 規則 | 結果 | 實測 |
|---|---|---|---|
| 2 | grid／listbox footer 的 `overflow:hidden` | **缺陷** | body 橫向捲動 250px，footer 沒有跟著動，差 250px。tree 已經有 `overflow-x:hidden`，沒有問題 |
| 3 | `.z-frozen-right-col` 的 sticky（ZK-4212） | **缺陷** | 最右欄沒有固定（right 1001，widget 的 right 是 600）。IceBlue 原本的規則帶 `!important` |
| 4 | cell 的 `position:relative; z-index:0`（ZK-3864） | **缺陷** | cell 內的 absolute 內容畫到凍結欄上面，grid、listbox、tree 都一樣 |
| 5 | `.z-dd-stackup`（ZK-817） | **缺陷（副作用）** | 拖曳時 overlay 排進一般排版，頁面 scrollHeight 變成兩倍（1800 對 900）。iframe 搶走拖曳的原始症狀在 headless 環境沒有重現 |
| 6 | `.z-toolbar-popup > *`（ZK-5617） | 不是缺陷 | Marble 的行為正確；IceBlue 舊 jar 反而把 invisible 項目顯示出來了。溢出的項目橫向排列，屬於外觀差異 |
| 7 | `.z-anchornav-fixed`（ZK-4386） | **缺陷** | 加上 class 之後仍然是 static，會隨頁面捲走 |
| 8 | tbeditor modal／overlay | **缺陷** | 插入圖片的對話框沒有定位，掉到頁面底部，overlay 高度是 0。`tbeditor.less:284-369` 的其他規則也沒有移植 |
| 9 | pdfviewer text layer（ZK-4395） | **缺陷** | 用來選取文字的 span 變成看得見的文字，疊在頁面左上角 |
| 10 | barcodescanner canvas（ZK-4095） | **缺陷** | 偵測框的 canvas 落在 video 外面 |

**下一步：G8。** 修正第 2、3、4、5、7、8、9、10 項，要等 Opus 局部重驗完成、不再使用 `z1117` 之後才派出。第 3 項先試試看不用 `!important`；如果非用不可，依 decision-principles §4 E5，要先問使用者。

**`B96_ZK_5044`：**
- **結構差異：** IceBlue 的 input 佔滿整個寬度，用 `padding-right` 預留按鈕的位置，按鈕用 absolute 放在那段 padding 上，所以 input 的 outerWidth 等於 hflex=min 算出的寬度。Marble 的 input 寬度則會被按鈕扣掉 32px。
- **hflex=min 的算法：** `Combobox.getChildMinSize_` 不會把按鈕算進去。
- **本地重現：** 用 IceBlue jar 加上 tablet.css 也只量到 73px。Jenkins 上能超過 80，原因沒有重現出來，可能是 Linux 的替代字型或 jar 版本。
- **PM 傾向：** 等重驗結果出來後，評估要不要把 F2 改成 IceBlue 的結構。這樣也可以拿掉負 margin 的抵銷寫法。

### Opus 局部重驗（F1–F4，2026-10-16）：PASS-WITH-FINDINGS

資料在 `$S/verify3/results.json`。

- **F1：** 所有 scrollTop 都停在設定的位置（grouped listbox、grid、listbox、tree，最遠到 6,000,000）；`B86-ZK-4112` 的 scrollHeight 在約 180 次取樣中都維持 13245000。注入 `overflow-anchor:auto` 後，grid 和 listbox 又會跳位，證明這個探針能抓到問題；tree 的探針則抓不到差異。
- **F2（Chromium）：** 各種寬度、hflex、預覽頁的間隙都是 0；注入 `width:auto` 可以重現原本 F2 的數字。**但在 Firefox 上仍然壞掉（V3-F1）：** lightningcss 只輸出 `width: stretch`，Firefox 量到的間隙是 −113 到 +367px，預覽頁的框線也斷開；WebKit 是 0。
- **F4：** 斷言有效，加回框線就會失敗。
- **F3：** `B96_ZK_4525` 跑 6 次通過 2 次（工程 agent 量到 4/6），已裁定改測試；`B90_ZK_4339` 6/6。
- **回歸掃描：** 236 個 class、355 個 method，21 個失敗。18 個在先前的全套測試就已經失敗，1 個是 5044，另外 2 個是 flaky（重跑都 2/2 通過）。沒有新的回歸。
- **Playwright：** 失敗的只有 font-size 和已接受的 6 個差異，沒有 PNG 變動。

**PM 裁定（G8a，進行中）：** 依 V5，combobox 改成 IceBlue 的結構：input 佔滿整個寬度，用 `padding-right` 預留按鈕的空間，按鈕用 absolute 定位。這樣同時解決 Firefox 的問題，拿掉 `stretch` 和正負 margin 的抵銷寫法，也讓 5044 的 input 寬度和 IceBlue 的算法一致。驗收要涵蓋 Chromium、Firefox、WebKit 三種瀏覽器。G8b（IceBlue 行為規則的 8 項）排在 G8a 之後。

### G8a 結果（工程 agent 自報，待驗證）與 PM 裁定

- `combobox.css`：input 改成 `width:100%`，加上 `padding-right: calc(button-width + spacing-3)`，按鈕用 absolute 定位，寬度改用 `--zk-combobox-button-width` 變數（tablet 用 `--zk-touch-target-min`）。`stretch`、`-webkit-fill-available`、`-moz-available` 和 input 的 `margin-right` 都已移除。
  - 按鈕仍保留 `margin-left` 負值：`flex.ts` 計算 hflex=min 時，會把 absolute 定位的按鈕也算進去。
  - 另外新增兩個寫法：input 加 `clip-path`，避免框線在按鈕底下重疊變深；`.z-combobox:not([style*="width"]) { min-width: max-content }` 讓百分比寬度的 input 在 grid track 中不會被壓扁。
  - `inputgroup.css` 補上 focus 時的 `padding-right`。
- 三種瀏覽器（Chromium、Firefox、WebKit）在 desktop 和 tablet 下量測：按鈕都在框內，文字不會跑到按鈕底下，涵蓋各種寬度、hflex、四個預覽頁。zktest gates 3 輪通過；120 個 combobox／bandbox class 跑 1 輪，沒有新的失敗。Playwright 只出現已接受的差異。
- **`B96_ZK_5044`**：input 從 45.3 變成 77.3（Firefox 76.4）。結構已經和 IceBlue 相同，本地 IceBlue 量到 73 也一樣失敗。Jenkins master 上能超過 80，推測是 Linux 字型寬度不同。**PM 裁定：** 不加固定的最小寬度（會和 `B50_2981016` 的 `width=80px` 衝突），改由下一次 Jenkins 執行確認；如果 Jenkins 仍然失敗，再處理。
- **交給驗證者的疑慮：**
  - `:not([style*="width"])` 是用屬性字串比對：inline style 裡只要有 `min-width`、`max-width` 也會命中，用 sclass 設寬度則不會命中。
  - `clip-path` 可能會裁到右側的 focus ring 或 forced-colors 的框線。
  - 這兩點都要實測，必要時改成更穩的寫法。
- G8b（8 條 IceBlue 行為規則）已經派出。第 3 項如果非用 `!important` 不可，要先停下來問使用者（E5）。

### G8b 結果（工程 agent 自報，待驗證，2026-10-17）

- 中途因為用量額度用完而中斷，重置後由同一個 agent 接著做完。
- 第 2 項：grid／listbox footer 加 `overflow:hidden`，footer 的 scrollLeft 從 0 變成 250，和 body 對齊。
- 第 3 項：`frozen.css` 加 `.z-frozen-right-col { position: sticky }`，**沒有用 `!important`**，改用比較高的 specificity。同時移植了 IceBlue 的 `:first-child{left:0}`。右欄固定在 499–599。
- 第 4 項：body cell 加 `position:relative; z-index:0`。副作用：`B86_ZK_4044` 再次失敗，原因是 offsetParent 一邊是 body、一邊是 TABLE，不一致。修法是 header 和 th 也補回 `position:relative`，兩邊都設成 positioned，和 IceBlue 相同。這等於部分撤回了 G1 的作法（G1 當時 cell 是 static）。修完後 4044 量到 430／430。
- 第 5 項：`zul/css/base/_dnd.css` 加 `.z-dd-stackup`（z-index 16800，沿用 IceBlue 的值），拖曳時 scrollHeight 從 1800 回到 900。
- 第 7 項：anchornav 修好。另外發現 `Anchornav.ts` 套用 class 時不穩定，而且會出現 `jqTarget.offset is not a function`，這是核心 widget 的問題，見 D7。
- 第 8 項：tbeditor 補上 modal、overlay 和對話框內的欄位，顏色都用 token；空白送出時會顯示 Required。
- 第 9 項：pdfviewer 的文字層恢復成透明的 absolute span，用真實滑鼠可以選到「ZK Essentials」。
- 第 10 項：barcodescanner 的 canvas 疊回 video 上。
- zktest 跑了 55 個 class，只有 `B102_ZK_5616` 失敗。它在 #1117 就已經失敗，屬於 TEST_GEOMETRY，交由改測試的計畫處理。Playwright 只有已經接受的差異。

**最終驗證（Opus，進行中）：** brief 在 `$S/verify4/verifier-brief.md`，內容包括：全套 zktest；Playwright 加上 Firefox／WebKit 的比對；G8a 的 combobox 三瀏覽器矩陣；`:not([style*="width"])` 誤判和 `clip-path` 裁切這兩個疑慮；G8b 各項對照 IceBlue 的數字；th 補回 relative 之後，表頭和凍結欄是否仍然對齊。

### 最終驗證（Opus，2026-10-19）：FAIL

資料在 `$S/verify4/results.json`，截圖在 `$S/verify4/shots/`。中途因網路錯誤中斷一次，由同一個 agent 接續完成。8117／8118 已沒有 listener。

- **必須通過的 77 列：** 75 通過、0 失敗、2 列沒跑（`B110_ZK_6071_TouchTest`、`B96_ZK_4910` 需要 Docker）。`B96_ZK_5044` 依裁定只記錄：仍失敗。
- **全套 zktest（z1117，3665 個 method）：** 3527 通過、135 失敗、3 skipped。不在 targets 的 46 個失敗中，34 個 v2 已分類，其餘 12 個為既有失敗或負載造成的 flaky（`B50_ZK_258` 在 z1117rr 也 4 次失敗 3 次）。**新回歸 0**；v2 的 `B85_ZK_3921`、`B86_ZK_3197`、`B101_ZK_5594` 現在都通過。
- **Playwright（不含 forced-colors-gallery）：** 869 通過、136 失敗 = 130 個 font-size（z1117rr 同樣失敗，環境漂移）+ 6 個已接受的差異；沒有 PNG 變動。
- **spec 斷言：** 10 組 mutation 都會失敗，沒有被削弱的斷言。
- **衛生：** 只有 D6 那一條 `!important`；沒有寫死顏色、沒有動 `.css.dsp`、TS、Java、zktest。

**沒有測試涵蓋的回歸（4 項，都和 z1117rr 對照過）：**

1. **G8a `clip-path` 接縫：** input 的 `clip-path` 邊緣和按鈕左緣在部分 sub-pixel 位置接不上，上下框線各斷 1px。掃 30 個位置：Chromium 2/30、Firefox 3/30、WebKit 0/30；z1117rr 都是 0/30。Firefox 的 combobox gallery 中「Readonly」「With description」看得到。focus、invalid、forced-colors 四邊完整。
2. **G8a `:not([style*="width"])` 誤判：** 用 sclass 或樣式表設的寬度被 `min-width: max-content` 蓋掉（`width:120px` 量到 205px，Firefox 233.3、WebKit 247；z1117rr 120）；`z-w-100` 在 150px 容器溢出 56px。inline style 有 `min-width`／`max-width` 時反而不套用。tablet + hflex=min 的短項目 input 撐破 root（z1117rr 同情況也壞，形式不同）。
3. **G8b 第 3 項，`<cell>` 組成的列：** `frozen.css` 的 selector 清單沒有 `.z-row .z-cell.z-frozen-right-col`，grid.css 的 ZK-3864 規則勝出，body cell 不固定。F110-ZK-6065 `gridGroup` 橫捲 0／40／110／300 時錯位 222／182／72／0px（z1117rr 0）。`F110_ZK_6065Test` 沒有檢查這點。
4. **G8b 第 2、3 項，固定的表頭／footer 儲存格透明：** 右側固定表頭沒有不透明背景，listbox、tree 的「Column 10」疊在「Column 6」上；footer 跟著捲動後，grid 凍結欄的「Footer 1」疊在「Footer 5」上。`frozen.css:100` 只替 `.z-frozen-col` 加背景。

**資訊：** component-theming 自 v3 起多了 2862 px 差異，全部在 pdfviewer 文字層，是 G8b 第 9 項（文字層恢復透明）的預期結果。groupbox 在三種瀏覽器都有差異（Chromium 2232 px），來自 G3 的結構改動，在 gallery 0.2% 容忍值內。

**PM 裁定（2026-10-19）：**
- 第 5 點 pdfviewer 差異接受（V5：IceBlue 的文字層本來就透明），與其他已接受差異一起在 NS4 重切基準。
- 4 項回歸的修法方向（待使用者同意後派工，見 D8）：
  - 回歸 1：拿掉 input 的 `clip-path`，改用不依賴 sub-pixel 對齊的方式處理按鈕底下的框線重疊；驗收為三瀏覽器 30 個位置 0 斷點，focus／invalid／forced-colors 四邊完整。
  - 回歸 2：拿掉 `.z-combobox:not([style*="width"])` 規則。若有測試因此失敗，回報而不改用其他 selector 技巧。tablet + hflex=min 在 z1117rr 也壞，不在本輪範圍，記為後續。
  - 回歸 3：`frozen.css` 的 selector 清單補上 `<cell>` 的情況，並補一條會在錯位時失敗的 spec 斷言。
  - 回歸 4：固定的表頭、footer 儲存格加不透明背景，用和 `.z-frozen-col` 相同的 token；grid、listbox、tree 都要涵蓋。

**重現頁（Sonnet，2026-10-19）：** 4 項都用最精簡的 zul 頁重現。頁面在 `z1117/zk` 與 `z1117rr/zk` 的 `zkpreview/build/inplaceWebapp/repro1117/`，截圖與量測在 `$S/repro4/`。說明頁：https://claude.ai/artifact/GoyVCdr6Kcs2SadrkroMTn 。`z1117rr/zk` 在 2026-10-19 17:03 被移除（原因不明），已在同一路徑以 `ce6c74b62f` 重建作為對照。另見 `doc/jenkins-zats-fork-run.md`（D9）。

**D8 使用者裁示（2026-10-19）與後續：**
- 第 1 項（combobox 接縫）：使用者要求先看解法；解法複雜或易有副作用就忽略。已在 8117 注入 CSS 試套：只拿掉 `clip-path`（選項 C）0/40 斷點，但按鈕上下框線變深；輸入框畫整個外框、按鈕不畫框線（選項 A）0/40，但要改 4 個檔案約 15 條宣告，沒有設寬度時 root 寬度平常 +1px、focus +2px，需要 padding 抵銷。說明頁：https://claude.ai/artifact/QqgmJoqX2ETj8GiCZmM8Gf 。PM 建議：不修。等使用者確認。
- 第 2 項（`:not([style*="width"])`）：使用者這次沒有裁示，待問。
- 第 3 項：使用者問 `columns` 與 `rightColumns` 同時設定是否支援，不支援就忽略並開 feature request。實測：只設 `rightColumns="1"` 的 `<cell>` 列也一樣錯位（header 395 sticky、cell 577 relative），所以這個缺陷不依賴兩者並用；改為一併修正（brief `$S/fix34/brief.md`）。是否支援並用另派 agent 研究（`$S/frozen-support/findings.md`）。
- 第 4 項：使用者裁示修正，已派 Sonnet（`$S/fix34/brief.md`）。
- 更正：先前說 tree／listbox 右凍結欄「停在中間」是誤判，它在正確的右緣（left 395 = grid 右緣 516 − 欄寬 120），右側看到的是透過透明表頭透出的捲動欄位。
- 第 3 項研究結果（Sonnet，PM 抽查證據屬實）：`columns` 與 `rightColumns` 並用**有支援**，信心高。ZK-4212 原始測試 `F86-ZK-4212-1.zul:39,61,81` 就是 `columns="1" rightColumns="1"`；`F110-ZK-6065.zul` 全部並用且 `F110_ZK_6065Test` 斷言兩者；`Frozen.ts` `_onSizeLater` 同時計算 leftWidth 與 rightWidth；`Frozen.java` 兩個 setter 互不檢查。文件只示範單獨 `rightColumns`，沒有禁止。不需要 Java API 檢查，也不開 feature request。詳見 `$S/frozen-support/findings.md`。

### G8c 結果（工程 agent 自報，待驗證，2026-10-19）

- `frozen.css`：sticky 清單補上 `.z-row .z-cell.z-frozen-right-col`；凍結表頭（grid／listbox／tree，左右）用 `--zk-color-surface`；auxheader 用 `--zk-color-surface-container-low`；grid footer 用 `--zk-grid-foot-bg`、listbox footer 用 `--zk-color-surface-container-low`，以 `:has(.z-*-frozen)` 選取（ZK 不替左側 footer cell 加 `.z-frozen-col`）。
- 新 spec：`mesh-header-footer.spec.ts` 「frozen columns」9 條，新 fixture `zkpreview/.../pv/frozen-cells-guard.zul`。mutation：拿掉 CSS 後 6 條失敗，3 條是本來就對的對照。
- 量測：`frozen-cell` 與 `frozen-cell-rightonly` 從 395／577 變成 395／395；`frozen-transparent` 表頭、footer 背景從透明變成不透明。
- zktest 28 個 frozen 相關 class：只有 `B102_ZK_5616` 失敗（#1117 既有）。Playwright 359 條：4 條失敗都是已接受的 gallery 差異，沒有 PNG 變動。沒有跑 Firefox／WebKit。
- 程序問題：engineer 第一次跑 zktest 時 8117 仍在執行，重建 jar 時讀到半寫的檔（`zip END header not found`）；停掉 8117 後重跑正常。
- Opus 驗證中：`$S/verify5/verifier-brief.md`（三瀏覽器、凍結欄的選取／hover 列、forced-colors、`:has()`、mutation，並截修正後的圖）。

### Opus 驗證 G8c（2026-10-19）：FAIL（範圍窄）

- 回歸 3、4 修好：Chromium、Firefox、WebKit × 5 頁 × 捲 40／250，右凍結欄 header／body／footer left 差 0；86 個停留 cell 都不透明，顏色與鄰格相同。F110 gridGroup 錯位 222／182／72／0 → 0。G8b 第 2、3 項數字與 verify4 相同。選取列、hover、forced-colors 沒有被新規則影響。`:has()` 已在 16 個檔案使用，500 列效能差 ≤1%。
- **Finding：** header／auxhead／footer 列最右側的 scrollbar bar cell（`.z-*-bar.z-frozen-right-col`）自 G8b 起是 sticky，但透明或半透明；垂直 scrollbar 佔寬度時（WebKit 預設，或 Chromium 顯示 scrollbar），捲過去的文字會透出。8118 沒有 sticky，所以是 G8b 造成的，屬於第 4 項同一類，已交回同一個工程 agent 修（使用者對第 4 項的裁示是修正）。
- **spec 缺口：** 不透明測試只檢查「不是透明」，header 改成 primary 色時 listbox／tree 仍通過；headless Chromium 的 bar cell 寬度是 0，測不到。一併交工程 agent 加強。
- **既有問題（8118 也有，不在本輪範圍，記為後續）：** listbox／tree hover 背景 alpha 0.9896，frozen cell 透出很淡的殘影；F110 `TD.z-detail-outer` 透明，捲動後「d2」透出；Firefox 點選列後 frozen scrollLeft 從 40 回到 0（未與 baseline 比較）。
- `z1117rr/zk` 的 `MCommon.java` 變動是重建 baseline 時 genprop 產生的檔頭時間，不是程式修改。

**G8c 後續（工程 agent 自報，2026-10-19）：** `frozen.css` 替 listhead、treecols、auxhead、foot、listfoot、treefoot 的 bar cell 加上不透明背景（treecols-bar 用 surface 加 outline-variant gradient，合成後與原本相同；columns-bar 原本就不透明）。spec「frozen columns」擴充為 23 條：檢查 alpha 為 1、合成色等於背後列色、寬度 > 0；用 `beforeAll` 自行啟動不帶 `--hide-scrollbars` 的 Chromium，全域 config 沒動。mutation（拿掉 bar 規則加上 R4）有 10 條失敗。Playwright 410 條中 406 通過，4 條是已接受的 gallery 差異。已請 Opus 做第二輪窄範圍驗證（`$S/verify5/results-r2.json`）。

**Opus 第二輪驗證（G8c 後續，2026-10-19）：PASS-WITH-FINDINGS。** bar cell 透字在 Chromium（顯示 scrollbar）與 WebKit 消失（68 個 bar cell 偏離像素最多 2；注入透明時 70–80）；Firefox headless 的 bar 寬度為 0，無從驗證。外觀與 8118 相比任一色版差 0（grid foot-bar／listfoot-bar 在 8118 看不到，改比 footer 列色，相同）。R1–R6 mutation 都讓預期測試失敗，R4 現在 listbox／tree 也會失敗；width > 0 斷言不是空殼（6px）。剩下的 spec 缺口：grid-right 的 auxheader 與 footer 沒有斷言；bar 測試只在 Chromium 跑。PM 裁定：接受，缺口記為後續，不擋本輪。結果 `$S/verify5/results-r2.json`。

**D8-2 使用者裁示（2026-10-19）：修正。** 刪除 `.z-combobox:not([style*="width"]) { min-width: max-content }`，不改用其他 selector 技巧或固定下限；若 grid track 的 min-content 擠壓（G8a 當時量到 tablet 57px）重新出現，先回報量測再裁定。已派 Sonnet（`$S/fix-d82/brief.md`），完成後交 Opus 驗證。

**D8-1 使用者裁示（2026-10-19）：不修。** combobox `clip-path` 接縫保留現狀（1px 斷口只在部分 sub-pixel 位置出現）。理由與選項見 https://claude.ai/artifact/QqgmJoqX2ETj8GiCZmM8Gf 。若日後框線 token 改為不透明色，可改用「只拿掉 `clip-path`」的一行修法。

**D8-2 結果（工程 agent 自報）與 PM 裁定（2026-10-19）：**
- 規則刪除後 `combobox-width.zul` 三瀏覽器 desktop／tablet 都是 120／120／150，與 8118 相同。
- 擠壓回來了：content-sized grid track 裡的 combobox desktop 205／233.3／247 → 167，tablet → 159；`/combobox.zul` 390px 最窄 combobox 173／201／214 → 148／39／148；`tablet-matrix-scroll-usable`（39 < 80）與 `tablet-combobox › gallery`（3022 px）失敗。其他四個 usecase／utility 頁不變。zktest 7 個 class：只有 `B96_ZK_5044` 失敗（既有）。
- **PM 裁定：選 (c)，修預覽頁。** 依 O5（預覽頁是原因就修頁面，不在主題 CSS 補償）、O10 與 V5：input `width:100%` 與 IceBlue 相同，依 CSS Sizing，百分比寬度的表單控制項是 compressible replaced element，min-content 貢獻為 0，IceBlue 在 min-content 欄一樣會被擠。只有 `pv/matrix.zul:8` 的 `--zk-col-min:min-content` 讓它顯現。作法：讓 matrix.zul 接受欄最小寬度參數（預設仍是 min-content），由 combobox.zul（以及實測同樣受影響的頁面）傳入 `max-content`。不改 combobox.css、不改測試門檻或基準圖。完成後交 Opus 驗證。
- **D8-2 方向 (c) 結果（工程 agent 自報）：** `pv/matrix.zul` 新增 `colMin` 參數（預設 min-content），只有 `pv/combobox-content.zul` 傳 `max-content`；另外 20 個 matrix 頁在三瀏覽器 × 1280／834／390 與 8118 逐字相同，不套用。`/combobox.zul` 欄寬與最窄 combobox（173／201／214）回到 8118 的數字；`combobox-width.zul` 120／120／150。tablet project 58 passed（含 `tablet-matrix-scroll-usable`、`tablet-combobox › gallery`），沒有 PNG 變動。zktest 42 個 combobox 相關 class：69／74 通過，失敗的 `B70_ZK_2929`、`B90_ZK_4417`、`F61_ZK_969`、`B96_ZK_5044`、`B102_ZK_5681_2` 在 verify4 都已失敗（`B102_ZK_5681_2` 為 TEST_GEOMETRY，列在改測試計畫）。Opus 驗證中（`$S/verify6/`）。

**Opus 驗證 D8-2（verify6，2026-10-19）：PASS-WITH-FINDINGS。** sclass／`z-w-100`／inline min/max-width 的誤判都消失，與 8118 相同（12 組）；G8a matrix 204 筆與 verify4 相同；22 個 matrix 頁 × 3 寬度 × 3 瀏覽器，198 組中 192 組與 8118 相同，其餘 6 組只差 WebKit 最窄 input 214 對 213；`/combobox.zul` 390px 三瀏覽器 = 8118；tablet project 58 passed，沒有 PNG 變動。min-content 推導：root 57（desktop）／69（tablet）= input 的 padding+border，按鈕 absolute 不計入，與 IceBlue 結構相同（推理，未在 IceBlue 實測）。
- 窄容器行為改變（與裁定一致，視為改善）：150px 框內預設 combobox 從溢出 56–110px 變成 148 縮在框內；100px grid 欄從 205–259（8118 會裁掉按鈕）變成 68／69，按鈕在框內。
- `F61_ZK_969` 不在 verify4 失敗清單：verify4 兩次都通過、verify2 失敗，分類為 ENV_FLAKY，不是回歸。
- `inputs.zul` 也引入 `combobox-content.zul`，engineer 清單漏列，但已含在驗證範圍內且與 8118 相同。

## Commit（D10 = A，2026-10-19，尚未 push）

- `z1117/zk`（branch `marble-1117-fixes`）：`5fb2b7d97fa44c4d496734effcdac7d82e99c748` ZK-6112: fix Marble regressions found in ZATS #1117
- `z1117/zkcml`（branch `marble-1117-fixes`）：`46c9c5f184ec4c87a3c98be81c2f2400045b9581` ZK-6112: fix Marble regressions found in ZATS #1117
- `z1117/zk`：`a76c2ee0c0999c82d6cfa598053f6efe3503420d` ZK-6112: update the gallery baselines for the accepted ZATS #1117 fix diffs（6 張：checkbox、component-theming、grid-header、listbox-header、tabbox、timepicker；重切前確認差異數字與 verify4 相同，重切後 6/6 通過）
- 主工作樹 `ZK10/zk`（branch `marble`）：本計畫與相關 `doc/zats-1117-*`、`doc/jenkins-zats-fork-run.md`。
- 下一步：合回 `marble` 的方式、push 到 fork 並依 D9 開 draft PR 跑 Jenkins，都等使用者同意。
