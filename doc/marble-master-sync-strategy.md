# `marble` 與 `master` 的同步策略

> 建立：2026-10-11 ｜ 修訂：2026-10-12（加入 fork 同步、`.less` 逐筆評估、測試階段）
> 對象：`hawkchen/zk` 的 `marble` 分支（ZK-6112）、`hawkchen/zk` 的 `master`、上游 `zkoss/zk` 的 `master`

## 1. 結論

- **持續同步**，用 merge，每 1–2 週一次；上游進了會碰到元件外觀或無障礙（a11y）的 commit 時立刻同步。不要等到最後一次做完。
- **同步順序：** 上游 `zkoss/zk` `master` → fork `hawkchen/zk` `master`（fast-forward）→ merge 進 `marble`。
- **`.less` 改動不直接收檔案，也不忽略：** 逐筆回到原本要修的 issue，在 Marble 上確認有沒有同樣問題；有就改成 Marble 的 CSS 寫法，沒有就記錄「不適用」與理由。
- **最後一步是測試：** 有 CSS 變動的元件都要跑上游帶進來的測試與 Playwright 視覺回歸，並依第 7 節規則調整測試。

## 2. 現況實測（2026-10-12）

| 項目 | 數值 |
|---|---|
| 共同起點（merge-base） | `f6429e7f8b`（2026-09-02，ZK-6086） |
| `hawkchen/master` | 停在 `f6429e7f8b`，**沒有自己的 commit**，可以 fast-forward 到上游 |
| 上游 `master` | `65c55120a2`（2026-10-07） |
| 上游領先 `marble` | 62 個 commits，318 個檔案（+10,445 / −1,170） |
| `marble` 領先上游 | 135 個 commits，1,618 個檔案 |
| 模擬合併的衝突 | 16 個：`build.gradle` 1 個內容衝突 + 15 個 `.less` 的 modify/delete 衝突 |
| 碰到 `.less` 的上游 commits | 10 筆（見第 6 節） |
| 上游帶進來的相關測試 | 約 28 個測試類別（見第 7 節） |

## 3. 為什麼不能等到最後

1. **衝突成本是非線性的**：兩邊累積的改動越多，同一段程式被兩邊都改到的機率越高，而且到時候已經忘了當初為什麼這樣改。
2. **`marble` 的驗證結果會過期**：Playwright 基準圖、Jess review 的結論都是在舊的 `master` 上量的；最後才合併，這些驗證要整批重做。
3. **最危險的情況不會出現衝突**（見第 4 節）：CSS 選擇器跟 TS 產生的 class 名稱對不上時，Git 不會提醒。

## 4. 實例：只看檔案類型就略過會漏掉的東西

上游 `79d57f6a45`（ZK-6132）把 chip / badge / confirmpopup 的 severity 值從 `danger` / `secondary` 改名為 `error` / `neutral`，
同一個 commit 改了 Java、`zul.xsd`、TS 與 `.less`。合併時 Java/TS 會乾淨地收進來，但 `marble` 的
`chip.css`、`badge.css`、`confirmpopup.css` 各還有 2 處選擇器寫著 `-danger` / `-secondary`，**合併後這些樣式會靜默失效**。

## 5. 每次同步的流程

### 階段 0：同步 fork 的 `master`

```bash
git remote add upstream https://github.com/zkoss/zk.git   # 只需第一次
git fetch upstream
git checkout master
git merge --ff-only upstream/master     # hawkchen/master 沒有自己的 commit，必須是 fast-forward
git push hawkchen master
```

- `--ff-only` 失敗代表 fork `master` 上有不屬於上游的 commit，要先停下來查，不要硬合。
- 好處：`master` 成為有名字、可追溯的同步點；之後 PR 的 base、`git log master..marble` 的範圍都一致。

### 階段 1：把 `master` merge 進 `marble`

- 在獨立 worktree 執行 `git merge master`。不用 rebase：`marble` 有 135 個 commits 且多個 session 共用。
- `.less` modify/delete 衝突：`git rm` 保留刪除。**檔案不收，但每一筆都進入階段 2 的評估清單。**
- 其他衝突照一般方式解。
- merge commit 訊息寫上同步到的上游 commit（例：`ZK-6112: merge master up to 65c55120a2`）。

### 階段 2：`.less` 改動逐筆評估

每一筆 `.less` commit 依以下步驟處理，結果填進第 6 節的表：

1. **讀 issue**：從 commit 訊息取 ZK-XXXX，用 CaseFoundry `lookup_issue` 或 Jira 看原本要修的問題與重現步驟。
2. **在 Marble 重現**：用 preview（8085）或 commit 帶進來的 zktest 頁面，在 Marble 下照同樣步驟檢查。
3. **判斷**：
   - **有同樣問題** → 改成 Marble 的 CSS 寫法：寫在對應的 `css/*.css`，只用 `--zk-*` token，不寫死顏色；照 `marble-theme` skill 的規則。不照抄 LESS 的數值，要看 Marble 的 token 怎麼對應。
   - **沒有同樣問題**（例如 Marble 結構本來就不同） → 記錄「不適用」與理由，不改。
   - **同一 commit 有改 class 名稱或 DOM**（例如 ZK-6132） → 不論外觀問題有沒有發生，Marble 的選擇器都必須跟著改。
4. 這些修改屬於 Marble 範圍，commit 掛 ZK-6112。

### 階段 3：測試

見第 7 節。

## 6. 本次（同步到 `65c55120a2`）的 `.less` 評估清單

| # | commit | issue / 主題 | 影響元件 | Marble 有同樣問題？ | 處理 |
|---|---|---|---|---|---|
| 1 | `79d57f6a45` | ZK-6132 severity 改名 `error` / `neutral` | chip, badge, confirmpopup | **必改**（class 改名） | 移植：Marble 選擇器改 `-error`/`-neutral`；zkpreview 20 處 `severity` 改值 |
| 2 | `812859b50a` | ZK-6097 focus outline 疊在 theme ring 上、對比不足 AA | breadcrumb, chip, confirmpopup | 否 | 不適用：Marble 無全域 `*:focus` ring；相關 3 個 focus 測試標 IceBlueOnly |
| 3 | `0f48ca9bfd` | ZK-6097 對比度參數無法從 token 檔覆寫 | badge, chip, breadcrumb, avatar, avatargroup | 否 | 不適用：token 已在 `:root`；`F110_ZK_6097_ContrastTest` 標 IceBlueOnly |
| 4 | `1ec18e2496` | ZK-6105 參數無法從 token 檔覆寫 | confirmpopup, avatargroup, carousel | 否 | 不適用；`B110_ZK_6105Test` 標 IceBlueOnly |
| 5 | `3e423ab246` | ZK-6097 carousel focus outline 疊加、root 沒有 outline | carousel | 是 | 移植：`.z-carousel:focus-visible` ring（正 offset）+ arrow/dot backing |
| 6 | `02726f8114` | ZK-6097 圖片原生拖曳中斷 carousel 滑動 | carousel | 是 | 移植：slide track `user-select: none` |
| 7 | `505da7f76d` | ZK-6097 carousel 播報區不應依賴 sr-only class | carousel | 否 | 不適用：`.z-carousel-status` 自帶隱藏規則 |
| 8 | `4b5c5f72d8` | ZK-6067 WCAG 模組下表單欄位有多個 label | checkbox | 否 | 不適用 |
| 9 | `64fade23dc` | WCAG 1.4.12 Text Spacing | combo, panel, window | 否（待瀏覽器確認） | 不適用；320px + text-spacing 目視一次 |
| 10 | `c1fa1b7505` | WCAG 1.4.11 portallayout 拖曳佔位框對比 3:1 | panel（portallayout） | 是（比 IceBlue 更差，約 1.3:1） | 移植到 `zkcml` `portallayout.css` |

第 3、4 筆是「LESS 變數暴露成可覆寫的 token」。Marble 本來就用 `--zk-*` CSS 變數，可能大多不適用，但要確認同樣的參數在 Marble 也能被覆寫。

### 6.1 `zkcml`（同步到上游 `5fe712072`）

| # | commit | issue / 主題 | 影響檔案 | Marble 有同樣問題？ | 處理 |
|---|---|---|---|---|---|
| E1 | `a550f9edf` | ZK-6084 za11y 依主題載入自己的 CSS，主題沒有對應 sheet 就不載 | `za11y/less/iceblue*.less`、`A11yThemeURIHandler`、`themes.properties` | D2 已決：A | Marble 不用 za11y sheet，修正寫進元件 CSS |
| E2 | `25b1bcadf` | WCAG 1.4.13 slider tooltip 可用 Esc 關閉、可被滑鼠移入 | za11y `_slider-a11y.less` | 是 | 移植（橋接區約 14px，不照抄 IceBlue） |
| E3 | `2262d196c` | WCAG 1.4.10 cascader、searchbox 彈出層在 320px 寬內 | za11y `_inp-a11y.less`（zkmax） | 是 | 移植 |
| E4 | `99ab80da7` | WCAG 1.4.10 colorbox 彈出層在 320px 寬內 | za11y `_inp-a11y.less`（zkex） | 是 | 移植 |
| E5 | `a251a4d66` | WCAG 1.4.10 image 不超過視窗寬 | za11y `_wgt-a11y.less` | 否 | 不適用：`image.css` 已有 `max-width:100%` |
| E6 | `bcab5fded` | WCAG 1.4.10 messagebox 在 320px 寬內 | za11y `_wnd-a11y.less` | 是 | 移植 |
| E7 | `b125d04fb` | WCAG 1.4.12 Text Spacing | zkex `grid.less` | 否 | 不適用 |
| E8 | `719f519e7` | ZK-4305 popup 重繪當掉、Esc、隱藏時 setOpen | zkmax `daterangebox.less` | 否 | 不適用 |
| E9 | `5e046d504` | ZK-6097 WCAG 色盤在 badge/chip/breadcrumb/carousel/avatar 對比不足 | `zkthemebuilder/palettes/_wcag*.less` | D3 已決：A | 這次不做，併入 followups §22 |
| E10 | `be7efc855` | ZK-6097 WCAG 色盤下選取列的 mesh 焦點框對比不足 | `zkthemebuilder/palettes/_wcag*.less` | D3 已決：A | 同 E9 |
| E11 | `6cde5d387` | WCAG 1.4.11 portallayout 拖曳佔位框 3:1 | `zkthemebuilder/palettes/_wcag*.less` | D3 已決：A | 同 E9；佔位框對比由 zk #10 處理 |

## 6.2 本次執行紀錄（2026-10-12）

- 評估細節：`ZK10/sync/triage-zk.md`、`ZK10/sync/triage-zkcml.md`。
- 決策（2026-10-12）：D2 = A（za11y 的修正寫進 Marble 元件 CSS，所有使用者適用）；D3 = A（WCAG 色盤這次不做，併入 `doc/marble-theme-followups.md` §22）。
- 上游 remote 已加：`zk` → `upstream` = `https://github.com/zkoss/zk.git`；`zkcml` → `upstream` = `git@github.com:zkoss/zkcml.git`。
- 本機 `master` 已 fast-forward：`zk` 到 `65c55120a2`，`zkcml` 到 `5fe712072`。**尚未 push 到 `hawkchen`。**
- worktree：`ZK10/sync/zk`（分支 `marble-sync-65c5512`，merge commit `c4696ad699f`）、`ZK10/sync/zkcml`（分支 `marble-sync-5fe7120`，merge commit `939423216`）。**`marble` 本身尚未變動。**
- `build.gradle` 衝突：上游 ZK-6167 把編譯目標改成 JDK 17（`options.release = jdkVersion`，`jdkVersion=17`），`marble` 刪掉了 `compileCSS`；兩者都保留。**之後建置一律要用 `withjdk.sh 17`。**

## 7. 測試

範圍：第 6 節表中所有有 CSS 變動的元件。

1. **跑上游帶進來的測試**（合併後在 Marble 下執行），包括：
   - `B110_ZK_6067Test`、`B110_ZK_6105Test`、`B110_ZK_6132Test`
   - `F110_ZK_6097_*`（Avatar、Avatargroup×3、Badge×3、Breadcrumb、Carousel、CarouselBindSave、Chip、Confirmpopup、Contrast、Popup、Portalchildren、Xsd）
   - `F110_ZK_6084_*`（CombobuttonDismiss、ConfirmpopupDismiss×2、HighlightedWindowResize）
   - `wcag/` 下有變動的 `ListboxTest`、`MenubarTest`、`MenupopupTest`、`Reflow`（用 `testWCAGOnly`）
2. **失敗時依性質處理**：
   - 斷言行為、class 名稱、DOM、ARIA（例如 `hasClass("z-chip-error")`）→ Marble 必須通過，修 Marble。
   - 斷言 IceBlue 特有的數值（像素、顏色、字型）→ 依 `.claude/rules/iceblue-tests.md` 加 `@Tag("IceBlueOnly")`，**不刪、不改寫**。如果階段 2 判斷 Marble 也有該問題，另外補一個針對 Marble 的斷言或測試。
3. **Playwright 視覺回歸**：只針對受影響元件跑（不加 `-g`，避免覆寫基準）。只有在差異是階段 2 的刻意修改時才更新基準圖，並在 commit 說明。
4. **檢查 lint 與編譯**：`npm run lint -- zul/src/main/resources/web/js`、`npm run type-check`、`./gradlew checkstyleMain`。

## 8. `zkcml` 的對應

`../zkcml/` 的 Marble 分支應照同樣流程處理（先同步 fork `master`，再 merge），建議與 `zk` 同一天做，
避免 EE 元件與 CE 元件用到不同版本的 `zul`。`zkcml` 的現況尚未量測。

### 6.3 第一次驗證（2026-10-12）：FAIL，5 個阻擋項

報告：`ZK10/sync/verify-report.md`。通過的項目：severity 顏色、chip 關閉鈕焦點（4.05:1 / 4.21:1）、carousel、portallayout 佔位框（4.83:1）、320px 彈出層、視覺回歸 275/275、type-check、checkstyle、IceBlueOnly 標記稽核。

阻擋項：

1. slider 提示框橋接區蓋到水平方向的把手（提示框實高 28px，不是 24px）。
2. 垂直方向橋接區長度固定，跟實際間隙（6–17px，依數字位數變動）對不上。
3. `F110_ZK_6084_RangesliderTest` 兩個 method 失敗（server 端 change log 是空的，與橋接區無關）。
4. `SliderTooltipTest.thePointerCanTravelOntoTheVerticalTooltip`：垂直提示框跑出畫面。
5. `ToastDismissTest.pressOutsideDismissesOnlyOnRelease` 失敗。

第 1、2 項是移植本身的缺陷，第 3–5 項是上游新測試在 Marble 上失敗；已交工程師處理（`ZK10/sync/fix-report.md`）。

驗證時發現、但與這次同步無關的既有問題（尚未排入）：

- `F110_ZK_6097_StyleTest`：carousel 箭頭字型是 Arial，不是 Inter。
- wcag `CascaderTest`：`ul.z-cascader-cave` 可捲動卻無法用鍵盤聚焦（axe `scrollable-region-focusable`）。
- cascader 在 320px 下展開第二層溢出 72px。
- panel header 在 text spacing 下 icon 被推出 panel、關閉圖示被切掉。
- messagebox 長文在 320×640 下 top 為 −14px（只切到 padding）。

### 6.4 修正與第二次驗證（2026-10-12）：PASS

報告：`ZK10/sync/verify-report-r2.md`（證據在 `ZK10/sync/verify-r2/`）。

| 問題 | 原因 | 處理 |
|---|---|---|
| slider 橋接區蓋到把手、長度不對 | 移植時寫死高度 | 橋接區改貼提示框邊緣，長度等於間距（`zkcml`） |
| `F110_ZK_6084_RangesliderTest` | Marble 的 `.z-sliderbuttons` 蓋住滑軌，點滑軌無效（合併前就壞） | `pointer-events` 讓點擊穿透（`zkcml`） |
| `SliderTooltipTest` 垂直 | Marble 垂直提示框在把手左側，頁面左緣時跑出畫面 | 改放右側，同 IceBlue（`zkcml`） |
| `ToastDismissTest` | Marble 的 `body` 只有 `min-height`，Page 元件的 `height:100%` 失效，頁面只有內容高；點空白處落在 `body` 而非 Page | `_reset.css` 的 `body` 改 `height: 100%`（`zk` `60253bbb6f`）。IceBlue 下不重現，屬 Marble 問題，不開 Jira（草稿保留在 `ZK10/sync/toast-iceblue-report.md`） |

第二次驗證結果：zktest 339 + wcag 13 個 class 只剩兩個既有失敗（`F110_ZK_6097_StyleTest`、wcag `CascaderTest`，見 6.3）；Playwright 275/275、hit-target 3/3；type-check、checkstyle 乾淨。
附帶改善：根層沒有指定高度的 `<borderlayout>` 以前會塌成 0 高，現在填滿視窗，與 IceBlue 一致。

commit 訊息依 D4 改為 `ZK-6112: … (ZK-XXXX)`（2026-10-12）。

### 6.5 垂直 slider 提示框：右緣被切與水平溢出（2026-10-13）

說明頁（含截圖）：https://claude.ai/artifact/Ai2bFV7zRs1wNajvSSdcLw；量測：`ZK10/sync/d6/d6-capture-report.md`。

裁示：D6 = 選項 D + C，D8 = 選項 A。

| 項目 | 內容 | 歸屬 | 分支 |
|---|---|---|---|
| D8 | 提示框隱藏時改用 `display:none`（淡入保留 `@starting-style` + `transition-behavior: allow-discrete`），不再占版面 | ZK-6112（只有 Marble 有） | `zkcml` `marble-sync-5fe7120` |
| D6-D | 垂直 rangeslider 外框 24px → 50px，把手靠左、右側留空給提示框，比照 IceBlue | ZK-6112 | `zkcml` `marble-sync-5fe7120` |
| D6-C | 右側空間不夠時，提示框翻到把手左側（TS 量測 + 主題樣式） | ZK-6192（兩個主題都有，已開 Jira） | `zk`、`zkcml` 的 `ZK-6192`，從 `master` 切出；完成後 merge 進 `marble-sync-*`，再補 Marble 的翻轉樣式（`ZK-6112: … (ZK-6192)`） |

步驟：

1. 工程師 A（`ZK10/sync/zkcml`）：D8、D6-D 各一個 commit。
2. 工程師 B（新 worktree `ZK10/zk6192/{zk,zkcml}`，與 1 並行）：ZK-6192 的 TS、IceBlue LESS、`B110_ZK_6192Test` + zul。
3. 工程師 A：把 `ZK-6192` merge 進 `marble-sync-5fe7120`，補 Marble 翻轉樣式。
4. 驗證者（另一個 agent）：重拍 `d6` 的四組情境、橋接區、軌道點擊；zktest（Rangeslider、SliderTooltip、`B110_ZK_6192Test`，兩個主題）、Playwright slider。
5. 更新說明頁，附實作後的截圖。

推送、開 PR 仍等使用者確認（D7）。

結果（2026-10-13）：獨立驗證 PASS，複查 PASS（`ZK10/sync/verify-report-d6.md`）。

- `zkcml` `marble-sync-5fe7120`：`a031c7af6`（D8）、`7f07c5b64`（外框 50px、間距 token 6px）、`599f22557`（multislider 同間距）、`864415a70`（`tooltipVisible` 在 Marble 不顯示，只有 Marble 有，順帶修）、`0e4c547bb` / `35f549c20`（merge ZK-6192）、`4214318f7`（Marble 翻轉樣式）。
- `zk` `marble-sync-65c5512`：`3080f90b68`（pinned 提示框測試）、`cbda5b7fc79`（`rangeslider-gallery.png` 重切）、`077b8355c7` / `50821526d2` / `c514c5c83f`（merge ZK-6192 的測試）。
- `ZK-6192`（從 `master` 切出）：`zkcml` `e3e615801`、`6798bfe83`（以 padding box 判斷裁切）；`zk` `e2ad3e5bd4`、`b4139965b1`、`305886b0c0`（斷言不依賴主題幾何）。`B110_ZK_6192Test`：沒有修正時 2/3 失敗，修正後在 IceBlue、Marble 都是 3/3。
- 量測：16 個提示框在左緣、容器右緣、視窗右緣全部 100% 可見，頁面溢出 0，可 hover，拖曳時不跳邊。Playwright 278/278。
- 已知、不處理：multislider 拖到 0 時，把手 `:active` 放大，讓提示框超出容器 0.5–0.7px（只有數值改變才重新量位置）。
- `marble` 在這段期間又前進了（`zk` 16 個、`zkcml` 4 個 commit）；兩邊模擬合併都沒有衝突，`zkcml` 也需要 merge commit，不能 fast-forward。

### 6.6 合併進 `marble`（2026-10-13）

- 在 `ZK10/sync/{zk,zkcml}` 從當時的 `marble`（zk `f498236cea`、zkcml `e787aed4d`）開 `marble-merge`，merge `sync` 分支：zk `05a1419270`、zkcml `f7c8ae121`，無衝突，兩邊改動的檔案沒有重疊。
- 合併結果獨立驗證 PASS（`ZK10/sync/verify-merge-report.md`）：type-check、ESLint、checkstyle 乾淨；Playwright 275 + 3 + 136 全過、無 diff；zktest test2 57 class / 395 test + wcag 13 class，只剩既有的 `F110_ZK_6097_StyleTest`、wcag `CascaderTest`，另 3 個在負載下的偶發失敗單獨重跑 37/37 通過。
- 主 checkout 的 `marble` 以 `git merge --ff-only marble-merge` 跟上，再推送到 `hawkchen`。
- 下一輪：上游 `master` 已到 zk `f2a605bea1`、zkcml `0ae496f5b`（各 3 個 commit，含 `confirmpopup.less`、`_zkvariables.less`、`daterangebox.less` 要逐筆評估）。
- 已知：垂直 rangeslider 的預設刻度文字會讓靠右緣的頁面多出約 11px 水平捲動（與提示框無關，合併前就有）。
