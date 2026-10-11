# Gate：第二批，清單列選取色改成 MD3 的 secondary-container（#41 後半、#23）

- **判定：PASS**（2026-10-06，第一輪，D10-A）
- **依據：** MD3 token `md.comp.list.list-item.selected.container.color = secondary-container`，`label-text = on-secondary-container`（material-web tokens v34）。
- **Generator：** Sonnet。
  - `_component-theme.css`：6 個公開 knob、共 11 項的預設值。
  - `listbox.css` 的 group 列和 select mold、`menu.css` 的文字色、`selectbox.css`：原本直接寫死的顏色。
  - zkcml `searchbox.css`：只改註解。
  - 規範：`selected-state-families.md`、`component-theme-variables.md`、`DESIGN.md`，以及 8 份 contracts。
  - knob 名稱都沒有改。
- **Verifier：** Opus，全新 context，沒有看 diff，也沒有讀被排除的 CSS。

## 判定檢查

期望值：底色 `oklch(0.87 0.0317 260.6)`，文字 `oklch(0.235 0.0317 260.6)`。RED 時全部是 primary-container `oklch(0.92 …)`。

| # | 目標 | 底色 | 文字色 |
|---|---|---|---|
| 1 | listbox 選取列 | ✓ | ✓ |
| 2 | listbox group 列（用 JS 加上 class） | ✓ | 不檢查 |
| 3 | select mold 的 `option:checked` | ✓ | ✓ |
| 4 | tree 選取列 | ✓ | ✓ |
| 5 | combobox 選取項目 | ✓ | ✓ |
| 6 | menu（量 knob 本身） | ✓ | — |
| 7 | searchbox 選取項目 | ✓ | ✓ |
| 8 | chosenbox 鍵盤焦點的 chip | ✓ | 不檢查 |
| 9 | selectbox 的 `option:checked` | ✓ | ✓ |

## 也必須成立

| 項目 | 結果 |
|---|---|
| 選取列 hover 時的顏色跟其他狀態分得出來（第 1、4 項） | ✓ |
| 文字對比（第 1、4、5、7 項，用像素算） | 11.28:1（修改前 13.43:1）✓ |
| `data-brand="copper"` 時跟著換色 | ✓ |
| `--zk-listbox-selected-bg` 覆寫仍然有效 | ✓ |
| forced colors 下是 Highlight / HighlightText | ✓ |
| paging、navitem、organigram、calendar 不變 | 跟 RED 基準值相同 ✓ |
| 第一批的鍵盤焦點框 | `solid 2px` ✓ |

## 回歸

- `component-theming` 107/107、`forced-colors` 17/17、`tablet` 10/10：全部通過。
- `focus-scan`：只有既有的 organigram 那一項失敗。
- `chromium`：10 項失敗，逐像素分類後，結果如下：

| 截圖 | 判定 | 說明 |
|---|---|---|
| listbox gallery、tree gallery | 預期內 | 差異只出現在選取色和灰階反鋸齒 |
| selectbox、searchbox、chosenbox hover/focus，combobox gallery | 修改前就有 | 對照同樣失敗的 timebox、datebox、textbox 等範圍外元件，差異只有文字反鋸齒和文字變寬造成的約 2px 位移 |

## 方法上的備註

- RED run 的 `rows2.js` 在 menu 那一段就中止了，所以沒有產生 `rows2.json`，改用 `rows.json` 對照。
- 既有的文字落差不只有灰階反鋸齒，還會讓輸入類元件的 gallery 版面位移約 2px。這屬於 follow-up item 8 的範圍。

取證放在 batch2/（過程資料已清除）。
