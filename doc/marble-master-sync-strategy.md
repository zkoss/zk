# `marble` 與 `master` 的同步策略

> 建立：2026-10-11 ｜ 修訂：2026-10-12（加入 fork 同步、`.less` 逐筆評估、測試階段）
> 對象：`hawkchen/zk` 的 `marble` 分支（ZK-6112）、`hawkchen/zk` 的 `master`、上游 `zkoss/zk` 的 `master`
> 逐輪的量測與執行紀錄（原第 2、6、9、10 節與 6.1–6.6）已移到 the local round log (tasks/marble-master-sync-log.md, not in the repository)，本文件只保留長期有效的策略與流程。

## 1. 結論

- **持續同步**，用 merge，每 1–2 週一次；上游進了會碰到元件外觀或無障礙（a11y）的 commit 時立刻同步。不要等到最後一次做完。
- **同步順序：** 上游 `zkoss/zk` `master` → fork `hawkchen/zk` `master`（fast-forward）→ merge 進 `marble`。
- **`.less` 改動不直接收檔案，也不忽略：** 逐筆回到原本要修的 issue，在 Marble 上確認有沒有同樣問題；有就改成 Marble 的 CSS 寫法，沒有就記錄「不適用」與理由。
- **最後一步是測試：** 有 CSS 變動的元件都要跑上游帶進來的測試與 Playwright 視覺回歸，並依第 5 節規則調整測試。

## 2. 為什麼不能等到最後

1. **衝突成本是非線性的**：兩邊累積的改動越多，同一段程式被兩邊都改到的機率越高，而且到時候已經忘了當初為什麼這樣改。
2. **`marble` 的驗證結果會過期**：Playwright 基準圖、Jess review 的結論都是在舊的 `master` 上量的；最後才合併，這些驗證要整批重做。
3. **最危險的情況不會出現衝突**（見第 3 節）：CSS 選擇器跟 TS 產生的 class 名稱對不上時，Git 不會提醒。

## 3. 實例：只看檔案類型就略過會漏掉的東西

上游 `79d57f6a45`（ZK-6132）把 chip / badge / confirmpopup 的 severity 值從 `danger` / `secondary` 改名為 `error` / `neutral`，
同一個 commit 改了 Java、`zul.xsd`、TS 與 `.less`。合併時 Java/TS 會乾淨地收進來，但 `marble` 的
`chip.css`、`badge.css`、`confirmpopup.css` 各還有 2 處選擇器寫著 `-danger` / `-secondary`，**合併後這些樣式會靜默失效**。

## 4. 每次同步的流程

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

每一筆 `.less` commit 依以下步驟處理，結果填進 the local round log (tasks/marble-master-sync-log.md, not in the repository) 的 `.less` 評估清單：

1. **讀 issue**：從 commit 訊息取 ZK-XXXX，用 CaseFoundry `lookup_issue` 或 Jira 看原本要修的問題與重現步驟。
2. **在 Marble 重現**：用 preview（8085）或 commit 帶進來的 zktest 頁面，在 Marble 下照同樣步驟檢查。
3. **判斷**：
   - **有同樣問題** → 改成 Marble 的 CSS 寫法：寫在對應的 `css/*.css`，只用 `--zk-*` token，不寫死顏色；照 `marble-theme` skill 的規則。不照抄 LESS 的數值，要看 Marble 的 token 怎麼對應。
   - **沒有同樣問題**（例如 Marble 結構本來就不同） → 記錄「不適用」與理由，不改。
   - **同一 commit 有改 class 名稱或 DOM**（例如 ZK-6132） → 不論外觀問題有沒有發生，Marble 的選擇器都必須跟著改。
4. 這些修改屬於 Marble 範圍，commit 掛 ZK-6112。

### 階段 3：測試

見第 5 節。

## 5. 測試

範圍：每一輪的 `.less` 評估清單（見 the local round log (tasks/marble-master-sync-log.md, not in the repository)）中所有有 CSS 變動的元件。

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

## 6. `zkcml` 的對應

`../zkcml/` 的 Marble 分支應照同樣流程處理（先同步 fork `master`，再 merge），建議與 `zk` 同一天做，
避免 EE 元件與 CE 元件用到不同版本的 `zul`。`zkcml` 的現況尚未量測。
