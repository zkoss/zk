# Gate：#65（splitlayout 游標）

## 按鈕部分 — **PASS**（2026-10-06，第一輪，D7-A）

- **Generator：** Sonnet。只改了 zkcml 的 `zkmax/.../layout/css/splitlayout.css`，新增 `.z-splitlayout-splitter-draggable .z-splitlayout-splitter-button-disabled { cursor: inherit; }`：splitter 可以拖動時，停用的按鈕沿用 bar 的 resize 游標。
- **Verifier：** Opus，全新 context，沒有看 diff。

| 檢查 | 量到的值 | 結果 |
|---|---|---|
| 1. 第一個 splitlayout 的按鈕中心 | `col-resize`（RED 時是 `default`） | PASS |
| 2. 第二個 splitlayout 的按鈕中心 | `row-resize`（RED 時是 `default`） | PASS |
| 按鈕內的圖示、按鈕四個角 | 都跟 bar 一致 | PASS |
| bar 本身的游標 | 沒變 | PASS |
| 拖動後兩側面板尺寸的變化 | ±40px | PASS |
| 可收合的按鈕 | 仍是 `pointer`，點一下會收合 | PASS |
| box、borderlayout 的 splitter | 平常的游標沒變；拖動中是 `auto`，跟修改前一樣 | 沒有變差 |

**回歸：** `hit-target` 3/3、`chromium -g splitlayout|splitter` 2/2、`smoke` 118/118，全部通過。

## 拖動中的部分（步驟 3、4）— **未處理**

仍然是 `auto`，跟 RED 時一樣。拖動中沒有任何 class 能讓 CSS 判斷方向，要改 `Splitlayout.ts`。使用者裁示 D11-B（2026-10-06）：另開 ZK Jira，不在 Jess 這一批處理。

取證放在 batch1-65/（過程資料已清除）。
