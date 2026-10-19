# Jenkins #1117：51 筆測試寫法問題的修改計畫

本計畫交給**另一個 session** 執行。D1（已決：選項 A，2026-10-16）：在 ZK-6112 下由我們修改這些測試。

- 分類與每類的例子：說明頁 https://claude.ai/artifact/Ag1u8FkaTErzTWcaUZDoAf
- 逐筆資料（`final`、`root`、`cause`、`decider`）：`doc/zats-1117-triage-data.json`
- Marble 修正的進度與裁定：`doc/zats-1117-fix-plan.md`（這裡不重複）
- `pointer-events` 的設計理由：`doc/marble-pointer-events-rationale.md`（該文件的 D1 已決：disabled 元件保留 `none`，改測試，不改 CSS）

## 目標與完成條件

每一筆都要做到以下四點：

1. 改完後，在 **Marble** 和 **IceBlue**（本地 `iceblue_c.jar`）上各跑 3 次，全部通過。
2. 測試要驗證的事情不變：只改「怎麼操作」或「怎麼量」，不刪斷言，也不放寬斷言到失去意義（例如把容忍值調到 ±100px）。
3. 不加 `@Tag("IceBlueOnly")`，也不碰已經標了這個標記的測試（`.claude/rules/iceblue-tests.md`）。
4. 不改 ZUL 測試頁的內容。只有頁面本身寫死 IceBlue 尺寸，而且這是測試意圖之外的部分時，才可以改，並在修改紀錄中寫明理由。

全部 50 筆做完後，交給獨立的驗證者（Opus），以 mutation 確認改過的斷言仍然能抓到錯誤，而且沒有被削弱。

## 工作環境

| 項目 | 設定 |
|---|---|
| worktree | 新建 `ZK10/z1117t/{zk,zkcml}`，分支 `marble-1117-tests`，從目前的 `marble` 建立（zk、zkcml 各一個）。`node_modules` 用 symlink 指到主工作樹（作法同 `z1117`） |
| 不可碰的位置 | 主工作樹 `ZK10/zk`、`ZK10/zkcml`（其他 session 在用）；`ZK10/z1117`（Marble 修正，未 commit）；`ZK10/z1117rr`（基準重跑，之後會用來驗證 IceBlue 規則掃描） |
| port | 8119（zktest `appRun`，或 IceBlue 對照時用）。8085 是共用的預覽伺服器，8117 和 8118 是本計畫其他工作在用 |
| JDK | `withjdk.sh 17`；gradle 用 `bash ./gradlew` |
| 執行單一測試 | `cd ZK10/z1117t/zk/zktest && withjdk.sh 17 bash ./gradlew test --tests "org.zkoss.zktest.zats.test2.X" -PmaxParallelForks=1 --console=plain --no-daemon` |
| IceBlue 對照 | 照 `ZK10/z1117rr/RERUN-NOTES.md` 的作法：用 gradle init script 把 `zk/dist/lib/iceblue_c.jar` 放上 zktest classpath，並在 zktest 的 `zk.xml` 設 `org.zkoss.theme.preferred=iceblue_c`。跑完把 `zk.xml` 還原。這個 jar 本身就有 18 筆失敗，不代表 master 建置的結果 |
| 不 publish | 不執行 `publishToMavenLocal`，`~/.m2` 是共用的 |

## 修改規則（各類共用）

- Selenium 動作用 `Actions action = getActions();`，不要 `new Actions(...)`。
- 拖曳不用 `dragAndDrop`／`dragAndDropBy`，改寫成 `clickAndHold(...)`，接著明確寫出移動和放開的步驟。
- 要模擬真人點擊時，用 `getActions().moveToElement(toElement(x)).click().perform()`（在相同座標點擊，由瀏覽器決定哪個元素接收），不用 `element.click()`／JS click 繞過。例外：類別一的 disabled 元件，依 `doc/marble-pointer-events-rationale.md` 的裁定，可以改成斷言元素是 disabled，或改用座標點擊，再斷言事件沒有送出。
- 期望值不要寫死任何主題的像素。要從頁面量（`jq(...).outerWidth()` 等），或改量正確的元素。
- 等待用 `waitResponse()`；只有在拖曳啟動時，才用 `pause(...)` 補 ZK 的 `setTimeout(0)`（`zk/drag.ts:57`）。
- 每改一筆都在 `ZK10/z1117t/REWRITE-LOG.md` 記一列：測試、原本為什麼失敗、改了什麼、Marble 3/3、IceBlue 3/3。

## 各類的改法

| 類別 | 筆數 | 改法 |
|---|---|---|
| 一、點擊被拒 | 19 | 被點的是設了 `pointer-events:none` 的內部零件（icon、text、separator、placeholder）時，改點宿主元素，或用座標點擊。disabled 元件則改成斷言 disabled 狀態或「事件沒送出」 |
| 二、寫死 IceBlue 尺寸 | 20 | 期望值改從頁面量；減的元素改成實際包住的那一層（例如 `B50_ZK_328` 改用 `.z-panel-head`）；使用預設寬度的測試，改從元件實際寬度算移動量 |
| 三、點擊位置被蓋住 | 8 | 前一步打開的 popup 先關掉（點空白處或送 Escape），再做下一步；或改點元素真正可以點的部分 |
| 四、拖曳太快 | 3 | 改成 `clickAndHold → moveByOffset(5,0) → pause(150) → moveToElement(target) → pause(50) → release`。本地實測加 150ms 停頓後，Marble 和 IceBlue 都是 3/3 |

## 批次與順序

各批都在 `z1117t` 依序做（同一份建置輸出，不要並行跑測試）：

1. **批次一：類別四（3 筆）和類別三（8 筆）**：數量少，改法最固定，先用來確認 IceBlue 對照的流程可以跑通。
2. **批次二：類別一（19 筆）**
3. **批次三：類別二（20 筆）**：逐筆判斷該量哪個元素，最花時間。
4. **驗證（獨立的 Opus agent）**：50 筆在 Marble 和 IceBlue 上各跑 3 次；每個類別至少挑 2 筆做 mutation（故意破壞功能，確認斷言會失敗）；檢查 `git diff` 只動到這 50 筆測試的檔案，以及有寫明理由的 ZUL。
5. **與 Marble 修正合併後再跑一次**：`z1117` 的修正（`doc/zats-1117-fix-plan.md`）commit 之後，在含有兩邊改動的版本上重跑這 50 筆。原因是有幾筆會受 Marble 修正影響，例如 G6 改了 combobox 結構、G7 移除了 popup 的 `min-width`。

## Commit

- 只有在使用者同意之後才 commit。訊息格式：`ZK-6112: rewrite <n> ZATS tests that hard-coded IceBlue geometry (Jenkins #1117, <類別>)`，依批次分開 commit。
- `git add <明確的路徑>` → `git diff --cached --name-only` 與預期清單比對 → `git commit`，三步在同一個 Bash 呼叫內完成，不用 `git add -A`。
- 停止伺服器時，只 kill 自己啟動時記下的 PID，包括 `tail -f /dev/null` 這個 holder；不用 `pkill -f` 或 `killall`。

## 已知的例外

- 有 3 筆需要 Docker（`@ForkJVMTestOnly`），本地沒辦法跑：`B36_3002536Test`、`B110_ZK_6131Test#testDisabledStillBlocksThePopup`、`B95_ZK_4584Test`。照同樣的規則改，並在 LOG 標「未本地驗證」，交給 Jenkins 確認。
- `B101_ZK_5594Test` 不在清單內：它會隨 F1（`overflow-anchor`）修好。
- 「驗證者結果」欄是 Opus 驗證者在 `z1117`（含 G1–G7 修正，F1–F4 修正之前）跑出的結果。`not-run` 表示需要 Docker。

## 追加（2026-10-16，F3 調查後）

- **`B96_ZK_4525Test`（類別三，第 51 筆）**：測試在 `contextClick` 之後立刻 `moveByOffset(-20, 0)`，context popup 開啟和滑鼠移動同時進行，約三分之一的情況下，popup 會開在滑鼠剛好移到的位置，左上角就在指標下方。G6 把 popup 外層改成方形（為了 `B90_ZK_4339`，同一個像素必須 hit 到 popup），所以這種情況下指標會落在 popup 上，tooltip 不會出現。實測結果：舊結構 6/6 通過，目前 4/6。CSS 不能同時滿足兩個測試，所以改測試：先等 context popup 開好，再把滑鼠移到 popup 範圍之外，或改用和 popup 開啟位置無關的 offset。修改紀錄要寫明這個競態。

## 測試清單（50 筆）

### 類別一：點擊被拒（目標設了 `pointer-events:none`）（19 筆）

| # | 測試 | root | 驗證者結果 | 分類時的原因（triage 資料 `cause`） |
|---|---|---|---|---|
| 1.1 | `B100_ZK_5468_CascaderTest#testInsertBefore()` | `icon-child-pointer-events` | fail | Marble makes the cascader caret icon non-hit-testable (pointer-events:none), so Selenium's click on the inner <i> is reported as intercepted by .z-cascader; behaviour for a real user is unchanged. |
| 1.2 | `F102_ZK_5504_CascaderTest#testInsertBefore()` | `icon-child-pointer-events` | fail | Same as B100_ZK_5468_Cascader: caret icon has pointer-events:none in Marble. |
| 1.3 | `B100_ZK_5468_SearchboxTest#testInsertBefore()` | `searchbox-empty-placeholder-click` | fail | The test clicks the empty .z-searchbox-placeholder, which is full-size in IceBlue (height:100%) but zero-height in Marble's flex trigger, so Selenium cannot interact with it; the searchbox itself opens normally on click. |
| 1.4 | `F102_ZK_5504_SearchboxTest#testInsertBefore()` | `searchbox-empty-placeholder-click` | fail | Same as B100_ZK_5468_Searchbox: empty placeholder is zero-height in Marble's flex trigger. |
| 1.5 | `B30_1997828Test#test()` | `icon-child-pointer-events` | fail | Marble sets pointer-events:none on .z-toolbarbutton-content, so Selenium's click on the inner <img> is reported as intercepted by the <a z-toolbarbutton>, which owns the click and opens the popup anyway. |
| 1.6 | `B96_ZK_4808Test#test()` | `icon-child-pointer-events` | fail | Marble sets pointer-events:none on .z-menu-icon, so Selenium's click on the inner <i> is reported as intercepted by .z-menu-content; Menu._doClick uses evt.pageX rather than the target, so a real click on the icon behaves the same. |
| 1.7 | `F50_2923266Test#test()` | `disabled-pointer-events` | fail | The test clicks a disabled colorbox to confirm nothing opens; Marble sets pointer-events:none on a disabled colorbox, so Selenium finds .z-page at that point. |
| 1.8 | `F90_ZK_4380Test#testDisabled()` | `disabled-pointer-events` | fail | The test clicks a disabled searchbox to check it does not open; Marble sets pointer-events:none on a disabled searchbox, so Selenium finds the vlayout-inner there. |
| 1.9 | `B110_ZK_6131Test#testReadonlyPopupOpensOnBoxClick()` | `drb-separator-pointer-events` | fail | The test clicks the ~ separator, which Marble marks pointer-events:none, so Selenium refuses the click; a real click on the separator lands on the .z-daterangebox root and doClick_ opens the popup normally. |
| 1.10 | `B110_ZK_6131Test#testDisabledStillBlocksThePopup()` ⚠ 需要 Docker | `disabled-pointer-events` | fail | Marble gives a disabled .z-daterangebox pointer-events:none, so Selenium cannot click its button at all; the test assumes a disabled box is still hit-testable and only checks the popup stays closed, which a real user cannot do either. |
| 1.11 | `B110_ZK_6131Test#testDisabledBoxSendsNoClickRequest()` | `drb-separator-pointer-events` | fail | The control click on the editable box's .z-daterangebox-separator fails because the separator is pointer-events:none; the click still reaches the root for a real user. |
| 1.12 | `B36_3002536Test#test()` ⚠ 需要 Docker | `calendar-text-pointer-events` | not-run | The test clicks the month span ($n('tm')), which Marble marks pointer-events:none; the click lands on the title anchor and ZK still opens the month view for a real user, so only Selenium's direct click on the span fails. |
| 1.13 | `F90_ZK_4347_constTest#testDateboxes()` | `disabled-pointer-events` | not-run | The test deliberately clicks a disabled day cell (11) to see that nothing happens; Marble gives disabled cells pointer-events:none so Selenium rejects the click, while a user's click on a disabled day is a harmless no-op. |
| 1.14 | `B80_ZK_3519Test#test()` | `disabled-pointer-events` | fail | Marble sets pointer-events:none on disabled textbox/decimalbox so WebDriver click() on the disabled input is intercepted by the parent .z-hlayout-inner; the test's intent (no ZK log from disabled controls) still holds for a real user. |
| 1.15 | `B96_ZK_4821Test#clickOnLabelTest()` | `disabled-pointer-events` | fail | Disabled checkboxes get pointer-events:none, so WebDriver cannot click the .z-checkbox-content label and the click would hit the parent .z-div; state indeed does not change for a user. |
| 1.16 | `B30_2562880Test#test()` | `icon-child-pointer-events` | fail | The test clicks the .z-listheader-sorticon element, but Marble sets pointer-events:none on it so the hit-test returns the th; the click still reaches the header the same way a user's click does. |
| 1.17 | `B96_ZK_5040Test#test()` | `icon-child-pointer-events` | fail | The test clicks the <i.z-paging-icon> inside the paging button, but Marble sets pointer-events:none on .z-paging .z-paging-icon so Selenium sees the button as the hit target; a real click on the arrow still pages. |
| 1.18 | `B96_ZK_5107Test#test()` | `icon-child-pointer-events` | fail | The test clicks <i.z-icon-pencil> inside a toolbarbutton, but Marble sets pointer-events:none on .z-toolbarbutton-content so the hit target is the <a>; the click reaches the same button for a user. |
| 1.19 | `bind.advance.ComponentAnnotationTest#testWithoutComponentAnnotation()` | `editable-label-not-interactable` | fail | sendKeys on the editableLabel input is 'not interactable' after click(custom0); either the click missed the label (macro wrapper centre) or the textbox stayed hidden, and no Marble rule on label/textbox/vlayout explains it statically. |

### 類別二：寫死 IceBlue 的尺寸（20 筆）

| # | 測試 | root | 驗證者結果 | 分類時的原因（triage 資料 `cause`） |
|---|---|---|---|---|
| 2.1 | `B80_ZK_3252Test#test()` | `errorbox-1px` | fail | Marble errorbox is 1px off to the right of the intbox (404 vs 403) and the test allows no tolerance on offsetLeft; the layout is visually left aligned. |
| 2.2 | `B90_ZK_4475Test#test()` | `-` | fail | With the test's line-height:60px parent, Marble's bandbox button inherits the 60px line-height and becomes 60px tall while the input stays 38px and is centred (positionTop 11 vs 0); both are visually centred in the box, but the test compares tops exactly. |
| 2.3 | `B96_ZK_5121Test#test()` | `loading-box-size` | fail | The test clicks the cascader icon while the 'Processing' box (.z-loading, placed at the page's top-left) is shown; Marble's larger padded .z-loading (padding 16px 24px plus a spinner) covers the icon at (88,41), IceBlue's smaller box did not. |
| 2.4 | `B70_ZK_2419Test#test()` | `slider-default-full-width` | fail | Marble's .z-slider-button margin-left:-10px shifts the thumb away from the css left that ZK's _fixPos/_getNextPos treat as the left edge, while _realpos reads the margin-shifted revisedOffset, so a drag yields a value ~10px too low and the thumb snaps back; a … |
| 2.5 | `B86_ZK_4170Test#test()` | `slider-knob-padding` | fail | Marble's .z-slider padding:12px 0 also applies to the knob mold, so setHeight(300px) on a border-box slider leaves only a 276px content box; the knob widget draws from content size and then sets width=height (276), so the knob is smaller than the width/height … |
| 2.6 | `B96_ZK_4636Test#test()` | `slider-default-width` | fail | Marble's horizontal slider has no default width (block, fills container, min-width 120px) while IceBlue fixes 200px; the test moves the pointer sliderWidth+10 right of the thumb, which leaves the viewport (MoveTargetOutOfBounds). |
| 2.7 | `F30_1797701Test#test()` | `slider-default-width` | fail | Same as B96_ZK_4636: Marble's default horizontal slider fills the container, so moveByOffset(sliderWidth) from the thumb exceeds the viewport. |
| 2.8 | `B95_ZK_4584Test#test()` ⚠ 需要 Docker | `tablet-spinner-button-layout` | not-run | Test equates the input outerWidth with the button's right edge (IceBlue overlays the button on a full-width input); Marble lays input and a 44px tablet button side by side (flex), so the button is rightmost at 183 vs input width 140 - the stated intent (button… |
| 2.9 | `B95_ZK_4702Test#test()` | `tree-row-pitch-vs-height` | fail | The test expects tree body height ~ 5 x jQuery height() of a treerow within +-5px; in Marble the rows are 51px tall by that measure while the body is 5 x 53 (row min-height 52px + 1px collapsed border), 10px apart. |
| 2.10 | `B96_ZK_4920Test#test()` | `treecell-content-height` | fail | jQuery height() of the first .z-treecell-content is 20, less than the 36px button, i.e. the content box is 36px border-box (min-height token 36, compact value) rather than 52, or clips the button; static CSS gives min-height 52 and padding 8/16 so the box shou… |
| 2.11 | `B50_ZK_328Test#test()` | `-` | fail | Marble wraps the header in .z-panel-head which carries a 1px border-bottom outside .z-panel-header; the test subtracts only .z-panel-header outerHeight so its expected panelchildren height is 1px larger than the real remaining space (234 vs 233). |
| 2.12 | `B80_ZK_3103Test#test()` | `listbox-select-scroll-into-view` | fail | After selecting 'Item 21' (page 2, 4 listboxes with checkmark and paging) the selected row's top ends above the body top (assertion at :45); static reading does not show whether this is row-height or scrollIntoView related under Marble. |
| 2.13 | `B90_ZK_4218Test#test1()` | `grid-body-border-collapse-width` | fail | Header minus body width is 5px instead of >= scrollbar width 6px with 1px cell borders; consistent with Marble body table border-collapse: collapse (header uses separate), but not proven. |
| 2.14 | `B90_ZK_4218Test#test2()` | `grid-body-border-collapse-width` | fail | Same 5px vs 6px header/body width difference as test1 (only the click order differs). |
| 2.15 | `B95_ZK_4574Test#test()` | `-` | fail | Test compares a block div height (a 20px line box) with its inline label height (16px content area); Marble sets body/label line-height 20px on 13px text, so the two no longer coincide, while vflex=min still shrinks the div to its content. |
| 2.16 | `B95_ZK_4765Test#test()` | `splitter-btn-offsetparent` | fail | Test measures the splitter button via jQuery positionLeft() (relative to offsetParent); Marble makes .z-splitter position:relative and the button position:absolute left:0, so its position relative to the bar is always 0 and cannot show the bar moving (0 vs exp… |
| 2.17 | `F50_2943475Test#test()` | `-` | fail | Test expects processing.offsetLeft == (html.width - processing.width())/2 using jQuery width() (content box); Marble gives .z-loading 24px side padding while zUtl.progressbox positions with the border box (offsetWidth), so the content-box formula is off by the… |
| 2.18 | `F80_ZK_2708Test#test()` | `rowlayout-children-width` | fail | The sum of the 12 rowchildren outer widths (with margins) differs from the first rowlayout width by 10px or more; widths are inline percentages written by Rowlayout.ts, so the cause is a layout/wrap difference not visible in the Marble rowlayout.css. |
| 2.19 | `F80_ZK_3133Test#test()` | `marble-scrollbar-6px` | fail | Reported desktop width is jq.innerWidth() (viewport minus scrollbar); at a 450px window the page overflows and Marble's styled scrollbar is 6px wide, giving 444px, whereas IceBlue's page reported the full window width. |
| 2.20 | `F85_ZK_3181Test#test()` | `marble-scrollbar-6px` | fail | A second 'Received onClientInfo event' is logged, meaning clientInfo changed between sends; the likely driver is the 6px Marble scrollbar appearing once the ZK log box is appended (innerWidth 450 -> 444), which headless Chrome hides for IceBlue. |

### 類別三：點擊位置被別的元素蓋住（8 筆）

| # | 測試 | root | 驗證者結果 | 分類時的原因（triage 資料 `cause`） |
|---|---|---|---|---|
| 3.1 | `B85_ZK_3898Test#test()` | `popup-min-width-covers-neighbour` | fail | The right-click menupopup opens at the pointer and Marble's min-width:160px makes it cover the next button at x=177 that the test clicks while the popup is still open. |
| 3.2 | `F90_ZK_4380_mvvmTest#testSubModelSelection()` | `searchbox-submodel-selection` | fail | After selecting Asia, reopening and retyping, no .z-searchbox-selected element exists (text ''), the selected class is toggled by widget code on click, so no CSS cause is evident. |
| 3.3 | `F61_ZK_1175Test#testDatebox()` | `-` | fail | The test opens the first datebox popup and then clicks the second datebox's button without closing it; Marble's taller/wider calendar popup (header 48px, 40px rows, up to 268px wide) is placed over that button, an incidental geometry overlap. |
| 3.4 | `B103_ZK_5918Test#test()` | `-` | fail | Selenium clicks the .z-checkbox-switch wrapper centre; with Marble's 8px flex gap and 4px mold margins the centre (~x=57) is likely the dead zone right of the 52px mold (x 4..56), so nothing takes focus and Space scrolls the page (819 = one viewport page-scrol… |
| 3.5 | `B102_ZK_5616Test#testDetailNotOverlappedAfterScroll()` | `detail-hit-test-topleft` | fail | Probe elementFromPoint(round(left), round(top)) of the first row Detail cell returns an element whose class lacks "z-detail"; the ZK-5616 fix is ported (so not a missing rule) and the likeliest cause is a fractional edge or a covering header/border, which only… |
| 3.6 | `B102_ZK_5616_1Test#testDetailNotOverlappedAfterScroll()` | `detail-hit-test-topleft` | fail | Probe elementFromPoint(round(left), round(top)) of the first row Detail cell returns an element whose class lacks "z-detail"; the ZK-5616 fix is ported (so not a missing rule) and the likeliest cause is a fractional edge or a covering header/border, which only… |
| 3.7 | `bind.basic.SelectboxmodelselectionTest#test()` | `selectbox-base-select-picker` | fail | Marble opts <select> into appearance: base-select; the picker popup stays in the top layer through its exit transition and intercepts the next click(sb2) at (60,67), which the native IceBlue popup did not. |
| 3.8 | `B102_ZK_5681_2_Test#test()` | `-` | fail | The Bandbox popup opened earlier in the test stays open and, being taller under Marble (padding 8px + margin 4px + 1px border + Marble checkbox row), now covers the Menu 'Options' at (67,659) in the vlayout spacing=20px; with IceBlue the popup was shorter and … |

### 類別四：拖曳太快（3 筆）

| # | 測試 | root | 驗證者結果 | 分類時的原因（triage 資料 `cause`） |
|---|---|---|---|---|
| 4.1 | `F50_3306718Test#test()` | `selenium-dnd-drop-target` | fail | The second drag (Item 3 onto Item 2.1.2) leaves no row after Item 3, i.e. Item 3 did not move before 2.1.2; the first drag passes, so drag works in general, but whether the drop was missed or placed elsewhere cannot be told statically. |
| 4.2 | `B50_ZK_937Test#test()` | `selenium-dnd-drop-target` | fail | After dragging tab 'first' onto 'fourth' the order is the one produced by dropping on 'third' ([second, first, third, fourth] gives index2=third), i.e. the release landed one tab early or the drop was not applied; the static CSS shows no rule that would change… |
| 4.3 | `F55_ZK_416Test#testMultiple()` | `selenium-dnd-drop-target` | fail | After Ctrl+click on a second listitem and dragging it onto the other listbox, ZK Forge stays in the left list (only the dragged item or nothing moved); the single-item drag test in the same class is not in the failure list, so no CSS rule explaining a lost mul… |
