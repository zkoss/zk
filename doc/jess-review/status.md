# Jess 設計審查 Marble 主題：現況看板

議題追蹤在 `hawkchen/marble-issue`（#1–#82）。tracker 狀態與 commit 於 2026-10-11 核對。本文只記錄目前的最終狀態。

## 摘要

- 82 個 issue 中，**51 個**已在 `marble` 上修正或答覆，等待設計師關單。
- **6 個** issue 轉入 ZK Jira（#73 #74 #1 #30 #68 #69，對應 ZK-6187、ZK-6188、ZK-6191）。另有 ZK-6175、ZK-6176，是 #37、#65 的 widget 部分，這兩個 issue 已計入 51。
- **25 個** issue 不在目前範圍，見 [deferred.md](deferred.md)（DEMO 14、ZK-CORE 11）。
- 51 + 6 + 25 = 82，與 issue 總數相符。

## 已完成

| Batch | 元件範圍 | Issues（最終處置） | Commit(s) | Evidence |
|---|---|---|---|---|
| — | tokens、badge、notification 等 | #2、#76、#77、#79、#80、#81（修正） | — | — |
| 1 | listbox、tree、tabbox、splitlayout、errorbox | #41、#43、#52、#67、#65（button 部分，drag 部分見 ZK-6176） | — | [gates/batch1-41-43.md](gates/batch1-41-43.md)、[gates/batch1-52.md](gates/batch1-52.md)、[gates/batch1-65.md](gates/batch1-65.md)、[gates/batch1-67.md](gates/batch1-67.md) |
| 2 | list-row selection | #23（#41 的第二部分） | — | [gates/batch2.md](gates/batch2.md) |
| 3 | grid | #34、#35、#36、#37（CSS 部分，widget 部分見 ZK-6175）、#38、#39 | `2efcdc65c9` `c333a2912d` `8770af7a09` `0a8d008bda` | [gates/batch3-final-r2.md](gates/batch3-final-r2.md) |
| 4 | biglistbox | #31、#78 | `8b62f5a7d`（zkcml）`42fec29a7f` `0265f71509` | [gates/batch4-final.md](gates/batch4-final.md) |
| 5 | cascader、chosenbox、combobox | #8、#12、#13、#16、#17（#17 只改選取高亮） | — | [gates/batch5-final.md](gates/batch5-final.md) |
| 6 | combobutton、slider 系列、rating、inputgroup | #18、#19、#24、#25、#26、#27；#22 已由 `29b6d0629e` 涵蓋，無新改動 | `dd21800fdb` `63ac5567d8` `47012fa5b`（zkcml）`29b6d0629e` | [gates/batch6-final.md](gates/batch6-final.md) |
| 7 | selectbox、bandbox/listbox | #3、#29 | `e5b82332a9` | [gates/batch7-final-r2.md](gates/batch7-final-r2.md) |
| 8 | menubar、navbar | #48、#51；#49 答覆，未改程式碼（保留的檢查欄） | `e5b82332a9` `ae9e58ace`（zkcml） | [gates/batch8-final.md](gates/batch8-final.md) |
| 9 | runtime-error、loading、window close | #66、#71、#72 | — | [gates/batch9-final/report.md](gates/batch9-final/report.md)、[gates/batch72-final/report.md](gates/batch72-final/report.md) |
| 10 | button、calendar、radio、tbeditor、fisheyebar | #4、#6、#40、#47、#61 | `5a663c9ef1` | [gates/batch10-final2/report.md](gates/batch10-final2/report.md)、[gates/batch47-final/report.md](gates/batch47-final/report.md)、[gates/rebase-d85/report.md](gates/rebase-d85/report.md) |
| 11 | tabbox accordion、panel、window、borderlayout demo | #53、#54、#55、#57（修在預覽頁，不在主題） | `1dda0e855d` | [gates/batch11-final.md](gates/batch11-final.md) |
| 12 | checkbox switch | #10（修正）、#11（答覆，未改程式碼） | `11217ddc48` | [gates/batch12-final.md](gates/batch12-final.md) |

合計 51 個不重複 issue（#41 出現在 batch 1 與 2，只計一次）。

## 已開 ZK Jira

| Issue | ZK key | 主題 |
|---|---|---|
| #73、#74 | [ZK-6187](https://zkoss.atlassian.net/browse/ZK-6187) | messagebox 沒有 per-button class |
| #1 | [ZK-6188](https://zkoss.atlassian.net/browse/ZK-6188) | `Errorbox._fixarrow` 沒有清除舊的 `left`／`top` |
| #30、#68、#69 | [ZK-6191](https://zkoss.atlassian.net/browse/ZK-6191) | 選擇加入的 inline 錯誤模式；errorbox 不可拖曳，預設位置在輸入框下方 |
| #37（widget 部分） | ZK-6175 | frozen 欄位分隔線只標在表頭 cell，body cell 需要 widget 加 marker |
| #65（drag 部分） | ZK-6176 | splitlayout 拖曳時 ghost 沒有 cursor 或 orientation class |

ZK Jira 共 5 個 key。前三行涵蓋 6 個 issue；ZK-6175、ZK-6176 是 #37、#65 的 widget 部分，這兩個 issue 已計入已完成。

## Line C 的 follow-up（不是 tracker issue）

批次 14 的證據為 [gates/batch14-final-r2.md](gates/batch14-final-r2.md)，批次 15 為 [gates/batch15-final-r2.md](gates/batch15-final-r2.md)。

| 項目 | 最終結果 | Commit |
|---|---|---|
| 批次 14 P1：toast info 圖示對比 | 已修：3.68 → 4.85:1；旋鈕 `--zk-toast-accent` 仍有效 | `c411216c13` |
| 批次 14 P2：notification 與 toast 同色階 | 已修：底色與箭頭 ΔE 0 | `c411216c13` |
| 批次 14 P3：水平 navbar 下拉巢狀縮排 | 已修：群組標題與同層項目差 −26.5px → 0.5px；第三層被裁切，列為 follow-up | `0c3d9281d`（zkcml） |
| 批次 14 P4：menu 圖示列文字起點 | 已修：文字起點差 2.5 → 0.5px，圖示中心差 1.0 → 0px | `c411216c13` |
| 批次 14 S1：selectbox forced-colors 規則移到共用檔 | 已搬移；served CSS 中隔離失效（minifier 併入），列為 follow-up | `c411216c13` |
| 批次 14 S3：空轉測試 | 已改名並修正，注入 4px 色條時會失敗 | `c411216c13` |
| 批次 14 S4：listhead bar 底色守護測試 | 已新增，注入驗證時會失敗 | `c411216c13` |
| 批次 14 S5：gallery 容差 0.01 → 0.002 | 已收緊；gallery 82/82 連跑兩次全過 | `40dba1ed2a` |
| 批次 14 S6：gallery baseline 重切 | 共 48 張（37 + 11）；三個已知失敗為過期 baseline | `d1f67d8f17`、`40dba1ed2a`、`c411216c13` |
| 批次 15 第 9 節：tab 標籤省略號 | 已修；帶圖示的標籤仍無省略號（已知限制） | `01f7010144` |
| 批次 15 第 14 節：平板 frozen 表頭錯位 | 已修（grid、listbox、tree，補上 `position: sticky`） | `01f7010144` |
| 批次 15 第 13 節：表頭文字偏 4px | 不重現；未找出哪個提交修好 | — |
| 批次 15 第 19 節：平板 scrollable menubar | 不是 CSS（widget `_scrollable` 為 false）；server 端原因未查出，交由另一個 session | — |

## 未決事項

- **推送：** `marble` 已推到 `hawkchen/marble`（含 batch 14、15）。
- **關單：** 82 個 issue 在 tracker 上全部仍是 OPEN（2026-10-11 重查），等設計師關單。
- **D120：** 平板 scrollable menubar 的 server 端原因由另一個 session 調查中。
- **merge 與 close 決定：** 屬於使用者。
