# component-theming.zul 下架計畫

目標：最終完全刪除 `zkpreview/src/main/webapp/web/component-theming.zul`（1273 行），CTV 契約改在各元件自己的預覽頁上以斷言驗證。
決策：議題 D5（本次對話）選項 A，先試做 3 個元件。
前置：`doc/component-theming-test-classification.md`（已縮減的 spec 現況）

## 1. 試做結果（2026-10-18）

新檔 `component-theming-borrowed.spec.ts`（project `component-theming-borrowed`）：

| 元件 | 借用頁面 | 驗證內容 |
|------|----------|----------|
| button | `/button.zul` | stock 半徑與填色；`:root` 覆寫後套用；disabled 不受填色 knob 影響（CTV-7） |
| checkbox | `/checkbox.zul` | stock 邊框與填色；`:root` accent 覆寫後套用 |
| messagebox | `/messagebox.zul` | 點「Information」開對話框；stock 值；`:root` 覆寫後套用；icon 色不變（CTV-7） |

- 3 個 test 全過（3.5 秒）；原檔 `component-theming` project 107 個仍全過。
- 變異驗證：注入沒分層、寫死樣式的 CSS，對照組 3 過，變異組 3 敗。
- 限制：變異驗證時 stock 值那一步就先失敗，所以它證明的是「寫死會被偵測」，沒有單獨證明「`:root` 覆寫那一步會偵測」。
- 技術：不再需要區域容器。element 上宣告的預設值，或寫死的值，會同時擋住繼承來的 `:root` 值，所以 `:root` 覆寫一條路就涵蓋 CTV-1/2/5。區域隔離是 CSS 繼承本身，不再驗。

## 2. 剩下的元件與遷移難度

現有 `component-theming.spec.ts` 約 52 個元件。依借用頁面的條件分三類，**以下分類是預估，尚未逐頁確認**：

1. **一般元件**（有預設狀態實例，在自己的預覽頁上）：直接套用試做的寫法。
2. **浮動元件**：messagebox、popup、notification、toast、drawer、coachmark、confirmpopup。需在測試中觸發開啟，已有 messagebox 範例可複製。
3. **需要特定狀態或資料的元件**：checkbox 的 on/off、tabbox 的 selected、listbox/tree 的 selected row、paging、inputgroup、organigram 等。要先確認自己的預覽頁有沒有該狀態實例；沒有就要在測試中操作，或回頭補預覽頁（補的是各元件預覽頁，不是 theming 頁）。

### S1 量測結果（2026-10-18，D6 選項 A）

量測方式：腳本從現有 spec 抽出每個元件的「預設實例」選擇器（61 個元件名，其中 button 另有專屬測試、其餘 60 個有選擇器可探測），分別在 `component-theming.zul` 與該元件自己的預覽頁讀第一個匹配元素的 5 個計算值（`backgroundColor`、`color`、`borderTopColor`、`borderBottomColor`、`borderTopLeftRadius`），比對是否相同。對 8085 只讀不寫。

| 項目 | 結果 |
|------|------|
| 預覽頁存在 | 全部有。5 個名稱不同：input→`textbox.zul`、toolbarbutton→`toolbar.zul`、radio→`radiogroup.zul`、nav→`navbar.zul`、fisheye→`fisheyebar.zul` |
| 預設實例選擇器在借用頁上找不到 | 只有 1 個：selectbox 的 `.z-select`（見下） |
| 5 項計算值與專屬頁不同 | 0 個 |
| 浮動元件 | messagebox、popup、notification、toast、drawer、coachmark、confirmpopup 共 7 個；各頁都有觸發按鈕，但按鈕文字與專屬頁不同，遷移時要照各頁現有文字改 |

**必須單獨處理的一項：selectbox。** `selectbox.zul` 只有 `.z-selectbox`，沒有 spec 現在讀的 `.z-select`（Listbox 的 select mold 輸出）。遷移時要先確認 `--zk-selectbox-*` knob 在 `.z-selectbox` 上讀得到哪個元素，不能直接照抄選擇器。

**這個量測的限制：**
- 只比對了預設選擇器的第一個匹配；spec 裡鏈式的子選擇器（例如 `.z-checkbox-mold`、`.z-listitem-selected`、`::before` 偽元素）沒有逐一探測，這些要在遷移每批時由測試本身驗證。
- 「5 項計算值相同」只代表預設外觀一致，不代表該頁一定有 spec 所需的每一種狀態實例。
- 計算值相同是代理指標，不是證明；真正的證明是遷移後測試通過並通過變異驗證。

### S2 第一批結果（2026-10-18，D7 選項 A）

`component-theming-borrowed.spec.ts` 新增 30 個元件的 test（加上試做的 3 個，共 33 個）：input、window、grid、listbox、tree、panel、groupbox、combobox、datebox、timebox、spinner、bandbox、daterangebox、menubar、avatar、rating、progressmeter、toolbar、a、timepicker、cascader、searchbox、colorbox、biglistbox、breadcrumb、carousel、codeeditor、slider、rangeslider、multislider。

- 產生方式：腳本從 `component-theming.spec.ts` 抽出每個元件「scoped 端」的斷言（屬性與覆寫值），對各元件自己的預覽頁量測 stock 值寫成字面值，再注入 `:root` 覆寫後重讀。注入的 knob 只取該元件自己前綴（`--zk-<comp>-*`）的部分，避免共用區域盒時帶入別的元件的 knob。
- 驗證：Playwright project `component-theming-borrowed` 33 passed；舊 spec 的 `component-theming` Playwright project 另行維持 107 passed（未動）。
- 變異驗證：對 30 個元件的根元素與子孫注入寫死樣式，30 個 test 全敗，未被改的 3 個試做 test 仍通過。
- 覆蓋比對：舊 spec 中這 30 個元件的 `whole-app` test 所用的 knob，全部都出現在新 test 注入的 knob 內（缺口 0 個）。
- 限制：變異驗證時 stock 值那一步先失敗，與試做相同，沒有單獨證明「`:root` 覆寫那一步」會偵測；選擇器是由舊 spec 機械抽出，沒有逐一人工確認每個子選擇器指到的是同一個元素。
- 這一批的 stock 值是借用頁自己的值；與舊 spec 有成對 stock 斷言的項目，沒有發現不一致。

### S2 第二批結果（2026-10-18）

新增 7 個 test：popup、drawer、coachmark、confirmpopup、notification、toast、selectbox；Playwright project `component-theming-borrowed` 共 40 passed。對這 7 個元件注入寫死樣式，7 個全敗（變異驗證的限制同第一批）。

- 浮動元件的觸發：popup 點 `Show Popup`、drawer 點 `Open Left`、confirmpopup 點 `Info`、notification 與 toast 點 `Info`；coachmark 不需點擊，`coachmark.zul` 頁載入時第一個 coachmark 就已開啟（帶 modal mask，點其他按鈕會被 mask 擋下）。
- selectbox：`--zk-selectbox-*` knob 由 `<listbox mold="select">` 輸出的 `.z-select` 讀取（`listbox.css`、`select.css` 的註解與規則一致），不是 `selectbox.zul` 頁上的 `.z-selectbox`。對 `.z-selectbox` 注入 `:root{--zk-selectbox-radius:0px}` 後，圓角仍是 4px。這是量測值；`.z-selectbox` 是否也該讀這組 knob，這份計畫不下判斷。因此這個 test 開 `listbox.zul` 頁、讀 `.z-select`。
- **預設 type（已確認原始碼）：** `Notification.show` 與 `Toast.show` 都把 `type == null` 改成 `info`（`Notification.java:95-96`、`Toast.java:94-96`），所以公開 Java API 不會產生 untyped 的 notification 或 toast。
- **toast 的 `bg`／`fg` knob：** 實測 Info（預設 type）的 toast 會跟著 `:root` 的 `--zk-toast-bg`／`--zk-toast-fg` 變化，已納入 test。
- **notification 的 `bg`／`fg` knob（D9 選項 A，已交由另一個 session 修改）：** 實測 Info 的 notification 原本不受 `:root{--zk-notification-bg:#6750a4;--zk-notification-fg:#ff0000}` 影響。原因與修法見 `doc/notification-knob-info-variant.md`：`.z-notification-info .z-notification-content` 把 bg／fg 釘在固定 token，specificity 高於讀 knob 的基底規則；修法是讓 info 的 content 與四個箭頭改讀 knob，warning／error 維持語意色。該修改在工作樹中、尚未 commit。
- **靜態示範卡的陷阱：** `notification.zul` 與 `toast.zul` 頁載入時各有 3 張靜態示範卡（info、warning、error），按鈕開出的卡是最後一張。第二批第一版的 test 用 `.first()`，讀到的是靜態卡，不是按鈕開出的 widget；已改成等卡片數量加一，再讀 `.last()`。popup、drawer、confirmpopup 頁載入時沒有開啟的實例，不受影響。
- **notification test 現況：** Info 的 bg／fg／radius 覆寫、Warning 與 Error 在覆寫後語意色不變，共 3 個 test。Info 那個在尚未重建 zul jar 的 8085 上會失敗（背景仍是 `rgb(224, 232, 244)`，期望 `rgb(238, 242, 255)`），要等 zul 重建並重啟後才會通過。Warning、Error 與 toast 的 test 現在已通過。
- toast icon 的 stock 值是 color-mix 結果（`color(srgb 0 0.409018 0.550725 / 0.974118)`），這個 test 對它只斷言「覆寫前不是紫色」，沒有寫死字面值。

尚未遷移（留給第三批）：浮動元件 7 個中的 popup、notification、toast、drawer、coachmark、confirmpopup（messagebox 已在試做）、selectbox、radio、checkbox 的其餘狀態、tabbox、menubar 以外的導覽類（nav、anchornav、stepbar）、paging、inputgroup、calendar、combobutton、toolbarbutton、organigram、goldenlayout、portallayout、pdfviewer、tbeditor、signature、cropper、dropupload、fisheye、badge。

## 3. 步驟

| 步驟 | 內容 | 驗證 |
|------|------|------|
| S1 | 逐頁量測：每個元件的預覽頁有沒有 stock 與狀態所需的實例；產出清單 | 清單覆蓋 spec 內全部元件 |
| S2 | 依清單分批遷移到 `component-theming-borrowed.spec.ts`（一批 8–10 個元件），每批跑一次變異驗證 | 每批：對照組全過、變異組該批全敗 |
| S3 | 全部遷完後，比對新舊 spec 的元件清單，一個都不能少 | 元件清單差集為空 |
| S4 | 刪除 `component-theming.zul` 與 `component-theming.spec.ts`，並移除 `playwright.config.ts` 的 `component-theming` project，將 `component-theming-borrowed` 改名為 `component-theming` | `--project=component-theming` 仍可跑，測試數符合 S3 清單 |
| S5 | 更新文件（見第 4 節） | grep 全 repo 無殘留 `component-theming.zul` 參照 |
| S6 | 刪除 zkpreview `build/inplaceWebapp` 內舊的 `component-theming.zul`，確認 `http://localhost:8085/component-theming.zul` 回 404 | HTTP 404 |

## 4. 要更新的文件

- `doc/spec/component-theme-variables.md`：第 97–98 行（示範頁連結）、CTV-9（第 171 行）、第 632 行、第 1685 行（新增元件步驟）。
- `doc/spec/new-component-checklist.md`：第 88 行 `x-ctv-suite` 的說明。
- 其他 `doc/**` 中提到 `component-theming` 的檔案（`zkdemo-b6-marble-fixes.md`、`marble-test-*.md`、`zats-1117-fix-plan.md` 等）：用 grep 逐一確認是否需要改。
- `zkpreview/doc/screenshots/component-theming-gallery.png` 與 `component-theming-forced-colors.png`：這兩張基準圖由別的 spec 使用，下架前要查是誰在讀，才能決定刪不刪。

## 5. 風險

- 測試會耦合到各預覽頁的內容；預覽頁改版（例如移除預設狀態實例）會讓測試失敗。需要在 `new-component-checklist.md` 註明這個依賴。
- `component-theming-gallery.png` 與 forced-colors 基準圖若仍被其他 spec 讀取，不能隨頁面一起刪（見第 4 節）。
- 預覽頁有 `inplaceWebapp` 複製殘留的問題（只新增不刪除），S6 要手動處理。
