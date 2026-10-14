# zkdemo B8 Z5 carousel 驗證報告

日期：2026-10-14。驗證者：獨立 verifier（未參與實作，唯讀；未改 source、未 commit、未更新任何 baseline）。

對象：worktree `ZK10/b8/zk`，branch `zkdemo-b8`，`git log marble..zkdemo-b8` 只有一個 commit：
`24bd1c629b ZK-6112: draw the active carousel indicator as a primary pill so it reads on light slides (zkdemo B8 Z5)`。

依據：`doc/zkdemo-b8-impl-brief.md` 工作 2 第 4 步、`doc/zkdemo-b8-impl-report.md`。

**總結：PASS-WITH-FINDINGS**。渲染結果符合核准設計，forced-colors 規則仍成立，repo config 的 Playwright 沒有寫出新 PNG。發現的問題都在註解與文件，CSS 行為本身沒有問題（見 F1–F4）。

## 檢查結果

| # | 檢查 | 結果 | 證據 |
|---|---|---|---|
| 1a | Diff 範圍 | PASS | `git diff --stat marble...zkdemo-b8` 共 6 個檔：`carousel.css`、`tokens/_component-theme.css`、`tokens/_forced-colors.css`、`component-theming.zul`、`component-theming.spec.ts`、`carousel-forced-colors.png`，沒有無關檔案。zul/spec 的修改是把既有 knob 測試從 `--zk-carousel-indicator-active-bg` 改成 `--zk-carousel-indicator-current-bg`；因為 active 點已經不再讀舊 knob，這個改動有必要。 |
| 1b | 只用 `--zk-` token、沒有寫死顏色 | PASS | 新增 `--zk-carousel-indicator-current-bg: var(--zk-color-primary)`、`--zk-carousel-indicator-current-ring: var(--zk-color-on-primary)`。active 規則是 `width:22px; border-radius: var(--zk-shape-corner-full)`，填色與框線都走新 token。diff 裡沒有新的顏色字面值（zkpreview 原本就有的 `#6750a4` 測試 override 除外）。 |
| 1c | 註解符合新設計 | FAIL（只有註解） | `carousel.css` 的 resting/active 註解與檔頭註解已經改寫，內容正確。但有兩處不一致：見 F1（`_component-theme.css` 的錯誤敘述）、F2（`carousel.css` 檔頭仍指向未更新的 contract）。另外 `_forced-colors.css` 新註解的第 3 行約 150 字元，沒有照原本的換行寬度（F4，只是格式）。 |
| 2a | active 指示器 `::before`（480×240，activeIndex 1；image slide 與 `z-bg-primary-container` div slide 各一個） | PASS | 兩個 carousel 量到的值相同：width `22px`、height `8px`、border-radius `9999px`（=`--zk-shape-corner-full`）、border `1px`、background `rgb(55, 111, 208)`、border-color `rgb(255, 255, 255)`。在同一個 carousel 內解析 `var(--zk-color-primary)` 得到 `rgb(55, 111, 208)`，`var(--zk-color-on-primary)` 得到 `rgb(255, 255, 255)`，兩者一致。PNG（DSF 2）在 rest 狀態量到外緣寬 44 device px，等於 22 css px。 |
| 2b | 非 active 點未變 | PASS | 兩個 carousel 的兩顆非 active 點都是 8×8、`50%`、background `rgba(0, 0, 0, 0.5)`（=`--zk-color-scrim`）、border `rgb(240, 244, 250)`（=`--zk-color-inverse-on-surface`）。`git show marble:` 的 resting 規則是 `background-color: var(--zk-carousel-indicator-bg)`、`border: 1px solid var(--zk-carousel-indicator-active-bg)`，兩個 token 的值也沒有變（scrim／inverse-on-surface），所以 resting 規則與 marble 完全相同。 |
| 2c | forced-colors | PASS | 用 `emulateMedia({ forcedColors: 'active' })` 量測：active 的填色與框線都等於 `Highlight` 的解析值，形狀維持 22×8、`9999px`；非 active 的填色與框線都等於 `GrayText`，8×8。`_forced-colors.css` 的兩條規則沒有改。截圖：`doc/zkdemo-b8-z5/verify-forced-colors.png`。 |
| 2d | hover 時的 pill 尺寸（開放問題，不判 PASS/FAIL） | 量測值 | 用真實的 `page.mouse.move`，`:hover` 成立，`transform: matrix(1.25, …)`。PNG 外緣寬度從 rest 的 44 device px（22 css）變成 56 device px（約 28 css；理論值 27.5）。24px 按鈕的範圍是 device x 80–127，hover 後 pill 在 76–131，兩側各超出約 2 css px。按鈕與 `.z-carousel-indicators` 都是 `overflow: visible`，所以不會被裁切；`gap` 為 8px，hover 後 pill 右緣距右鄰 8px 圓點約 6 css px，沒有碰到。截圖：`verify-light-rest.png`、`verify-light-hover.png`。 |
| 3 | Playwright（repo config，`-g carousel`） | PASS（其中 1 項 FAIL 與本 commit 無關） | config 用 `process.env.PREVIEW_URL ?? 'http://localhost:8085'`，本次設 `PREVIEW_URL=http://127.0.0.1:8086`。共 7 項，**6 passed、1 failed**。失敗的是 `font-size-equivalence › carousel.zul`：13px 從 142 個變成 145 個（+3）。這和 memory 記錄的「新建 worktree 的 za11y `.sr-only` live region 造成每頁 13px +3」完全吻合；本 commit 沒有改任何 font-size。執行前後 `git status --porcelain` 都只有兩個未追蹤的 `node_modules` symlink，沒有新增或修改的 PNG。補充：`forced-colors-gallery` 是直接**寫入** `carousel-forced-colors.png`、不做比對；寫入後 git 沒有差異，表示已 commit 的 PNG 和目前的渲染逐 byte 相同。 |
| 4 | `carousel-gallery.png` baseline | 確認：仍是舊設計（FINDING） | 依 live DOM 量到的指示器位置裁切 baseline（`verify-gallery-baseline-crop.png`），第一個 carousel 的 active 點仍是淺色填色、深色框的舊圓點；同位置的 live 截圖是藍色 pill（`verify-gallery-live-crop.png`）。容忍值：`gallery-scan.spec.ts` 用 `maxDiffPixelRatio: 0.002`（gallery project 沒有設 `threshold`，所以是 Playwright 預設的 0.2）。實測 baseline 與 live 有 1,204 個 pixel 不同（全圖 3,179,520，佔 0.038%），容忍預算是 6,359 個 pixel，因此通過。這只代表差異在預算內，baseline 和目前的原始碼並不一致。 |
| 5 | `doc/zkdemo-b8-z5/z5-light-after.png`、`z5-img-after.png` | PASS | 兩張都是藍色 primary pill 加白色框，兩側是 scrim 點加淺色框；淺色 slide（primary-container）與照片 slide 上的 active 狀態都清楚可辨。在 `z5-light-after.png` 的指示器列（y=131，DSF 3）量到 pill 外緣寬 66 device px，等於 22 css px。 |

## 發現（Findings）

- **F1 `_component-theme.css` 註解有一句錯誤。** 註解寫「overriding that knob [`-indicator-active-bg`] recolors both the ring and the label-style light voice」。但 `git grep` 顯示 `--zk-carousel-indicator-active-bg` 只剩 `carousel.css:244`（resting 點框線）一個使用處；label 走的是獨立的 `--zk-carousel-label-fg`。所以 override 這個 knob 只會改 resting 框線。
- **F2 contract 與 spec 文件沒有更新。** `carousel.css` 檔頭寫「See doc/contracts/carousel.md Design Contract for the full rationale」，但這些文件仍描述舊的「resting/active 互換兩個 knob」設計：`doc/contracts/carousel.md`（:136、c19/c20 列 :242-243、:344）、`doc/contracts/carousel.html`（:284、:291）、`doc/spec/component-theme-variables.md`（:1576、:1596 把 `-indicator-active-bg` 寫成「active dot fill / resting dot ring」）。新的兩個 knob 也沒有登錄在 spec 的 knob 表裡。`doc/harness/eval-reports/carousel.md` 的 c20 是歷史量測，可以不改。
- **F3 knob 語意變了，名稱沒變。** `--zk-carousel-indicator-active-bg` 現在只負責 resting 點的框線。已經 override 它來改 active 點顏色的使用者，升級後 override 會悄悄失效，而且 active 點不會再跟著變。現在也沒有任何測試在檢查這個舊 knob。
- **F4 格式。** `_forced-colors.css` 新註解的第 3 行沒有換行，和同一個區塊其他行的寬度不一致。
- **F5 實作回報有遺漏。** 回報說 carousel 相關測試「最終全過」，但沒有提到 `font-size-equivalence › carousel.zul` 會失敗（環境造成，見檢查 3）。回報也把 `carousel-forced-colors.png` 描述成「baseline 更新」，實際上 `forced-colors-gallery` 是審閱用的輸出，不做比對。

## 開放問題

1. **hover 時 pill 超出 24px 按鈕。** `scale(1.25)` 讓 pill 從 22px 變成約 27.5px，兩側各超出按鈕約 2px。不會被裁切，也不會碰到鄰點（還有約 6px 間距）。這是否可以接受？還是 active 的 hover 應改成只放大高度（例如 `scale(1, 1.25)`），或不放大？
2. **`carousel-gallery.png` 是否要重拍？** 在 0.2% 的容忍值內，它永遠不會攔下 active 指示器的回歸；更新需要 `--update-snapshots=all -g "gallery carousel$"`，需要使用者授權。
3. **F3 的命名。** 要把 `--zk-carousel-indicator-active-bg` 改名（例如 `-indicator-ring`），還是保留名稱、只在 spec 裡說明語意已變？這會影響 F1／F2 的文件怎麼寫。

## 程序紀錄

- 預覽伺服器由本 verifier 從 `ZK10/b8/zk/zkpreview` 啟動在 8086（PID：tail 64899、gradle wrapper 64900、Jetty 65275，cwd 已確認是 b8）。完成後只 kill 這三個 PID，8086 已無程序在 listen。沒有碰 8085，也沒有停 gradle daemon（36717，不是本次啟動的）。
- jar 檢查：`zul-11.0.0-SNAPSHOT.jar` 內的 `carousel.css` 與 commit 的原始碼完全相同，`tokens/_component-theme.css` 和 `norm.css.dsp` 都含有新 token，所以沒有重建。
- Probe 頁 `build/inplaceWebapp/web/z5-verify-probe.zul` 已刪除，確認回 404。
- 證據截圖（新增，未 git add）：`doc/zkdemo-b8-z5/verify-*.png`。

## 第二輪驗證（76258d5988）

日期：2026-10-14。驗證者：獨立 verifier（唯讀；未改 source、未 commit、未更新任何 baseline）。

對象：worktree `ZK10/b8/zk`，branch `zkdemo-b8`，`git log marble..zkdemo-b8` 有兩個 commit：`24bd1c629b`（active 指示器改為 primary pill）與 `76258d5988`（修正 F1–F4）。

**總結：PASS-WITH-FINDINGS**。token 設計、渲染、override 行為都符合預期，resting 點與 marble 完全相同；Playwright 6/7 通過，唯一失敗是已知的 worktree za11y 差異。剩下的問題都在文件與註解的措辭（見 G1、G2），不影響 CSS 行為。

### 檢查結果

| # | 檢查 | 結果 | 證據 |
|---|---|---|---|
| 1a | Diff 範圍 | PASS | `git diff --stat marble...zkdemo-b8` 淨變更 7 個檔：`carousel.css`、`tokens/_component-theme.css`、`tokens/_forced-colors.css`、`carousel-forced-colors.png`、`doc/contracts/carousel.md`、`doc/contracts/carousel.html`、`doc/spec/component-theme-variables.md`。`component-theming.zul` 與 `.spec.ts` 在 `24bd1c629b` 改成 `-current-bg`，`76258d5988` 又改回 `-active-bg`，所以淨 diff 為零，兩個 commit 合起來沒有改到測試。沒有無關的檔案。 |
| 1b | 只用 `--zk-` token，沒有寫死顏色 | PASS | 新的 token 定義是 `--zk-carousel-indicator-ring: var(--zk-color-inverse-on-surface)`、`-active-bg: var(--zk-color-primary)`、`-active-ring: var(--zk-color-on-primary)`；`-indicator-bg: var(--zk-color-scrim)` 沒有變。`carousel.css` 的 resting 規則是 `border: 1px solid var(--zk-carousel-indicator-ring)`；active 規則是 `width:22px`、`border-radius: var(--zk-shape-corner-full)`、`background-color: var(--zk-carousel-indicator-active-bg)`、`border-color: var(--zk-carousel-indicator-active-ring)`。diff 裡沒有新增顏色字面值。 |
| 1c | 沒有殘留的 `-current-` token | PASS | `/usr/bin/grep -rn 'indicator-current' zul/src zkpreview/src doc` 沒有結果（rc=1）；`/usr/bin/grep -rn -- '-current-' zul/src zkpreview/src \| /usr/bin/grep -i carousel` 也沒有結果（rc=1）。jar 內的 `norm.css.dsp` 與 `carousel.css.dsp` 中 `indicator-current` 出現 0 次。在 live 頁面的 `:root` 上，`--zk-carousel-indicator-current-bg` 與 `-current-ring` 的值都是空字串。 |
| 1d | 註解正確且有換行（`carousel.css`、`_component-theme.css`、`_forced-colors.css`） | PASS（另有發現 G1） | 三處新寫的註解都符合現在的 CSS。F1 已修正：現在寫的是「Overriding -indicator-active-bg recolors only the active pill's fill; -indicator-ring recolors only the resting dots' ring」，與 `grep` 的結果相符（`-active-bg` 只在 `carousel.css:254` 使用，`-indicator-ring` 只在 `:244` 使用）。F4 已修正：`_forced-colors.css` 該段每行 45–79 字元，與同區塊一致。`carousel.css` 與 `_component-theme.css` 的新行最長 81–82 字元，和相鄰的既有行相同。但 `_component-theme.css` carousel 區塊開頭有一句既有的敘述沒有跟著改，見 G1。 |
| 1e | 文件與 CSS 一致（`carousel.md`、`carousel.html`、`component-theme-variables.md`） | PASS（另有發現 G1、G2） | 三份文件的 token 名稱與值都和 `_component-theme.css` 一致：`-indicator-bg`→scrim、`-indicator-ring`→inverse-on-surface、`-indicator-active-bg`→primary、`-indicator-active-ring`→on-primary。`carousel.html` 內嵌的 CSS 規則與 `carousel.css` 逐行相同，也定義了 `--zk-color-primary`、`--zk-color-on-primary`、`--zk-shape-corner-full`，所以 mockup 能解析出新值。`carousel.md` 的 c19–c21、新增的 c21a，以及 `ctv-knobs` 清單，與 spec 表格的四列都正確。「十個 knob、九個顏色」的數字也與 CSS 相符。文件不再描述舊的 swap 設計（`/usr/bin/grep` 找不到 indicator 相關的 swap／inversion 敘述；c29 的「resting/active fill swap」指的是 transition 會讓填色切換有動畫，仍然成立）。 |
| 2 | 伺服器上的 CSS | PASS | jar `zul/build/libs/zul-11.0.0-SNAPSHOT.jar`（14:15）內的 `carousel.css` 與 `_component-theme.css` 和原始碼 `diff` 相同；`norm.css.dsp` 也含有四個新 token。因此沒有重建。live 頁面 `:root` 的值是 `-indicator-bg #00000080`、`-ring #f0f4fa`、`-active-bg #376fd0`、`-active-ring #fff`。 |
| 2a | active `::before`（480×240，activeIndex 1；image slide、`z-bg-primary-container` div slide） | PASS | 兩個 carousel 量到的值相同：width `22px`、height `8px`、border-radius `9999px`、border `1px`、background `rgb(55, 111, 208)`、border-color `rgb(255, 255, 255)`。在同一個 `.z-carousel-indicators` 內解析，`var(--zk-color-primary)` 得到 `rgb(55, 111, 208)`，`var(--zk-color-on-primary)` 得到 `rgb(255, 255, 255)`，與量測值一致。 |
| 2b | resting `::before` 與 marble 相同 | PASS | 三個 carousel、每個兩顆 resting 點，量到的值都是 8×8、`50%`、border `1px`、background `rgba(0, 0, 0, 0.5)`、border `rgb(240, 244, 250)`。marble 的基準：`git show marble:` 的 resting 規則是 fill `var(--zk-carousel-indicator-bg)`、ring `var(--zk-carousel-indicator-active-bg)`，marble `_component-theme.css:1202-1203` 把這兩個 token 定為 scrim 與 inverse-on-surface。在同一頁面解析後是 `rgba(0, 0, 0, 0.5)` 與 `rgb(240, 244, 250)`，與量測值完全相同。`_colors.css` 在 marble 與 branch 之間沒有差異。 |
| 2c | 第三個 carousel：`--zk-carousel-indicator-active-bg: rgb(128,0,128)` | PASS | active 的填色是 `rgb(128, 0, 128)`；框線仍是 `rgb(255, 255, 255)`，形狀仍是 22×8、`9999px`。兩顆 resting 點都是 `rgba(0, 0, 0, 0.5)`／`rgb(240, 244, 250)`，沒有變。這表示 override 只影響 active pill 的填色，符合 token 原本的公開語意。 |
| 3 | Playwright（repo config，`-g carousel`，`PREVIEW_URL=http://127.0.0.1:8086`） | PASS（1 項 FAIL 是已知的環境差異） | 跑了兩次，結果相同：共 7 項，**6 passed、1 failed**。通過的是：`render-smoke › renders /carousel.zul`、`gallery › carousel`、`component-theming › carousel — regional arrow-bg/indicator-active-bg override, sibling untouched`、`component-theming › carousel — whole-app :root override wins`、`focus-ring scan › carousel`、`forced-colors-gallery › carousel`。失敗的是 `font-size-equivalence › carousel.zul`：13px 從 142 個變成 145 個（+3），其餘級距不變。這是已知的 worktree za11y 差異（`.sr-only` live region），本 branch 沒有改任何 font-size。跑之前與跑之後，`git status --porcelain` 都只有兩個未追蹤的 `node_modules` symlink，沒有新增或修改的 PNG，所以不需要 `git checkout`。`forced-colors-gallery` 會寫入 `carousel-forced-colors.png`，寫入後 git 沒有差異，表示已 commit 的 PNG 與目前的渲染逐 byte 相同。 |
| 4 | Mutation check：override 測試能否失敗 | PASS | 在 scratchpad 複製一份 `component-theming.spec.ts`，搭配 scratch config（`node_modules` 用 symlink 指向 b8 的 zkpreview），沒有改 repo 的檔案。與原檔唯一的差異：`beforeEach` 中，在原本關閉 transition 的那行之後，若 `MUTATE` 有設定，就注入 `.z-carousel-indicator-active::before{background-color:var(--zk-color-primary)!important}`，模擬 active 填色不再讀 `-active-bg`。對照組（不 mutate）：該測試通過。`MUTATE=1`：該測試在 `:1668` 的 `expect(pseudoBgOf(scoped … '::before')).toBe(SCOPED_PURPLE)` 失敗，Expected `rgb(103, 80, 164)`，Received `rgb(55, 111, 208)`。 |

### 發現（Findings）

- **G1 carousel 區塊的總述沒有跟著更新（文件與註解，非行為問題）。** 有三處在新的例外說明之前，仍用「全部」「唯一」這類絕對說法：
  - `_component-theme.css:1173-1183` 寫「all three overlay regions (arrow / indicator / label) … speak exactly two brand-independent voices … deliberately NOT --zk-color-on-primary」。但 active pill 現在用的是 `--zk-color-primary`／`--zk-color-on-primary`。同一個註解後面（:1190）才補一句「the active dot is the one overlay part that uses brand tokens」，前後句子互相矛盾。
  - `doc/spec/component-theme-variables.md:1567-1573` 寫「The nine color knobs … each region pairing a "dark voice" … with a "light voice" … The one exception is `--zk-carousel-arrow-bg-hover`」。現在 `-indicator-active-bg`／`-active-ring` 也是例外。
  - `doc/contracts/carousel.md:343-347`（ctv 段）同樣寫「each region pairing a dark/light voice … except `-arrow-bg-hover`」。

  這三處後面都有補充說明，讀者讀完整段可以拼出正確的設計，但開頭那句單獨看是錯的。建議把例外寫進那句總述。
- **G2 文件格式。** `doc/contracts/carousel.md:81`（218 字元）與 `:156`（264 字元）是新加入的段落文字，沒有照該檔約 90 欄的換行寬度；`ctv-knobs` 的第 2 行與 spec `:1579`（106 字元）也略長。表格列本來就很長，不算在內。這只是格式問題。
- **G3 已知、仍然存在：`carousel-gallery.png` baseline 仍是舊設計。** 與第一輪檢查 4 相同。`gallery › carousel` 在 `maxDiffPixelRatio: 0.002` 內通過；這只代表差異在預算內，不代表 baseline 與原始碼一致。要更新需要使用者授權。
- 第一輪的 F1–F4 都已解決：F1、F4 見檢查 1d；F2 見 1e；F3 已依設計恢復 `-indicator-active-bg` 的「active 填色」語意，並由 2c 與 4 證明 override 有效，而且測試會抓到退化。F5（回報遺漏）：這次的實作回報已列出 `font-size-equivalence` 的失敗與原因。

### 程序紀錄

- 預覽伺服器由本 verifier 從 `ZK10/b8/zk/zkpreview` 啟動在 8086。PID：tail 96059、gradle wrapper 96060、Jetty 96270（cwd 已確認是 `b8/zk/zkpreview`；父程序是既有的 gradle daemon 36717，不是本次啟動的，所以沒有動它）。結束時只 `kill 96270 96060 96059`，之後 `lsof` 確認 8086 已沒有 listener。沒有碰 8085。
- jar 已含新的 token，所以沒有重建。
- Probe 頁 `zkpreview/build/inplaceWebapp/web/z5r2-verify-probe.zul` 已刪除，確認回 404。
- Mutation 用的 spec 複本、scratch config 與量測腳本都放在 session scratchpad，沒有寫進 repo。這次沒有產生新的證據截圖。
