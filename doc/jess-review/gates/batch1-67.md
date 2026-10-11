# Gate：#67（errorbox 靜態範例的游標）

- **判定：PASS**（2026-10-06，第一輪）
- **分類：** 展示頁的問題，不是佈景的問題（D8-A）。真正的 errorbox 可以拖動，佈景給它的 `cursor: move` 是對的；Jess 游標停的那個框，是展示頁用 HTML 拼出來的靜態範例，背後沒有元件，拖不動。
- **Generator：** Sonnet。只改了 `zkpreview/src/main/webapp/web/errorbox.zul`：在靜態範例的 `.z-errorbox-content` 加上 inline `cursor:default` 和一段說明註解。佈景 CSS 沒有改動。
- **Verifier：** Opus，全新 context，沒有看 diff。

| 檢查 | 量到的值 | 結果 |
|---|---|---|
| 1（判定）靜態範例內文的游標 != move | `default`（RED 時是 `move`） | PASS |
| 2（保護項）真正的 errorbox 內文的游標 == move | `move` | PASS |
| 3（保護項）拖 40px 後，位置移動 ≥ 30px | 移動 40px | PASS |
| 4（保護項）關閉按鈕的游標 != move | `pointer` | PASS |
| forced colors 下外觀不變 | 跟 RED 基準圖相比 0 px 差異 | PASS |

**回歸：** `chromium -g errorbox` 1/1、`forced-colors` 17/17、`smoke` 118/118，全部通過。

取證放在 batch1-67/（過程資料已清除）。
