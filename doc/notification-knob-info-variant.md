# Notification knob 對公開 API 無效：查因與修改紀錄

日期：2026-10-18　分支：marble（未 commit）

## 查到什麼

- `Notification.show()` 把 `type == null` 改成 `"info"`（`Notification.java:95-96`），公開 API 產生的 notification 一律帶 `.z-notification-info`。
- `notification.css` 的 `.z-notification-info .z-notification-content` 把 bg/fg 釘死在 `--zk-color-surface-container-highest` / `--zk-color-on-surface`，specificity 高於讀 knob 的基底 `.z-notification-content`，所以 `--zk-notification-bg/-fg` 只對沒有 type 的卡片生效（只有直接呼叫 `Clients.showNotification()` 不給 type 才會碰到）。
- 這是 `_component-theme.css` 與 `doc/spec/component-theme-variables.md` 當初的設計（「只有 untyped 讀 knob」），但前提「untyped 是常態」不成立。同為 info 預設的 toast 已經是 `.z-toast-info` 讀 `--zk-toast-bg/-fg`。
- 消費者（grep，排除 build/）：`notification.css`、`tokens/_component-theme.css`、`doc/spec/component-theme-variables.md`、`doc/component-theme-variables-progress.md`、`component-theming.zul` 頁、`component-theming.spec.ts`、`doc/component-theming-page-retirement-plan.md`。`../zkcml` 沒有消費者。

## 決定（依 decision-principles.md）

- V6（同角色同外觀）：notification 與 toast 同角色，toast 的 info 讀 knob，notification 比照。
- §3「Public `--zk-*` token」：保留 knob、只改預設，不改語意；本次沒新增、改名或刪除 token，不屬 E4。預設值與原本相同，所以預設外觀不變。
- 沒有新增 `!important`（不屬 E5）；不需 Jira（共用程式碼沒有缺陷，是 Marble CSS 自己的設計，O1 + O10）。
- warning / error 維持語意色（同 toast、button、progressmeter 的 color variant 慣例，CTV-7）。

## 改了什麼

1. `zul/src/main/resources/web/js/zul/wgt/css/notification.css`：info 的 content 改讀 `var(--zk-notification-bg/-fg)`；info 的四個 pointer 箭頭改讀 `var(--zk-notification-bg)`，讓箭頭跟著卡片顏色。warning/error 不動。
2. `zul/src/main/resources/web/zul/css/tokens/_component-theme.css`：Notification 區塊註解改為「knob 驅動 info（預設）與 untyped」。
3. `doc/spec/component-theme-variables.md`：Notification 小節與 Knob 表的 Scope 同步（radius 本來就對所有 type 生效，一併寫正確）。

## 驗證結果

- 由呼叫端 session 驗證（重建 `:zul:compileMarbleCss :zul:jar` 並重啟 8085）：在 `notification.zul` 按 Info，`:root` 的 `--zk-notification-bg/-fg/-radius` 覆寫生效；Warning、Error 在覆寫後語意色不變。Playwright project `component-theming-borrowed` 42 passed、`component-theming` 107 passed（皆為對方回報的量測值，我未自行重跑）。
- 已做：`git diff` 確認只有 info 區塊的 bg/fg/箭頭改變，warning/error 規則逐字未動。

## 對 component-theming.spec.ts 的影響（交給呼叫端 session 修改）

- 第 737-772 行附近的 notification 測試與註解寫「typed notification 不受 override 影響」「sibling untouched」，現在 info 會受影響；若該頁的 sibling 是 info，斷言要改。
- 建議新增斷言：在 `notification.zul` 按 Info，注入 `:root{--zk-notification-bg:#6750a4;--zk-notification-fg:#ff0000}`，`.z-notification-info .z-notification-content` 的 bg/fg 要改變；按 Warning、Error，bg 仍為 `--zk-color-warning-container` / `--zk-color-error-container`。
- `doc/component-theming-page-retirement-plan.md` 若引用「untyped only」也要同步。
