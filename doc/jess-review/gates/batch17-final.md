使用 8085（批次 17 最終判定）

# 批次 17 最終判定：biglistbox、daterangebox、navbar、confirmpopup（#32 #20 #50 #75）

- **日期：** 2026-10-13，Verifier（Opus 5.5，與 RED 為同一位，未看 diff）。
- **伺服器：** 8085 PID 88201（13:56:29 起聽）。四個 served `.css.dsp` 與新 build 的 md5 相同，且都和 RED 不同（biglistbox `b319282b…`、daterangebox `22f11077…`、nav `b9064ca6…`、confirmpopup `94034be2…`）。量測前、量測後、回歸跑完各比一次。
- **IceBlue：** 8125 仍停機，沒量；參考值見 [../repro/report.md](../repro/report.md)。
- **方法：** [batch17-red.md](batch17-red.md)（含 M1–M5）。

## 判定

| 項目 | RED | 最終 | 結果 |
|---|---|---|---|
| J32-1 jess-32 | head Δy 39，cnt.left 1 < 391，body Δy 742 | Δy 0，cnt.left 391 = fx.right 391，body Δy 0 | PASS |
| J32-1 預覽 frozen3 | Δy 39，33 < 423，body Δy 372 | Δy 0，423 = 423，body Δy 0 | PASS |
| J32-2 | 表頭 ink 中心 Δ 39 | 0 | PASS |
| J20-1 空白 | 1 號 (55,111,208)，ΔE 77.55 | 1 號 (255,255,255)，ΔE 0 | PASS |
| J20-2 已選（jess、預覽頁） | ΔE 77.55 | ΔE 0 | PASS |
| J50-1（jess、預覽 Get Started、Contact） | cave.top −24／552／502，中心命中 cave 內項目 | 40／636／636 = cnt.bottom，中心命中觸發項 | PASS |
| J50-2 | 再點仍開（高 88／128／228） | 再點關閉，高 0 | PASS |
| J75-1 jess Left | 箭頭在按鈕內 210 px，gap 0 | 0 px，gap 8（對照 8.81） | PASS |
| J75-1 jess Top、預覽 Top | 210 px，gap 0.5 | 0 px，gap 8.5（對照 8） | PASS |

## 保護項

`summary.cjs final marble red` 比對 78 項，全部與 RED 相同，0 項不同。追加檢查：

- #32 未設 frozenCols 的兩個實例像素與 RED 完全相同（diff 0）。frozen3 可以水平捲動（`_currentX` 3→5，frozen 表頭不動）。水平 thumb 在外框內（RED 時在 x=1631，跑到外框外），批次 4 的捲軸檢查仍通過。
- #50 下拉尺寸與 RED 相同（180×88、180×128、186.37×228），沒有換行；L2 文字距 cave 左緣 68.89。
- #20 hover 1 號與 2 號的 tint 都是 (237,237,237)，ΔE 0。

## 回歸

- gallery（biglistbox、daterangebox、navbar、confirmpopup、calendar、datebox）16 個中 15 個通過；component-theming 107/107；hit-target 3/3；focus-scan 57 通過、47 skip（原有的 skip 條件）。
- 唯一失敗：`gallery › confirmpopup` 高 1432 → 1400。把 RED 的 CSS 換回去重拍，與 baseline 0 差異，所以差異完全來自本次修正。原因：預覽頁的靜態示意框也帶 `z-confirmpopup-placement-*`，箭頭空間從後側 margin 移到前側，top 示意框各上移 8px，left 示意框左移 8px。即時 popup 都正確（J75）。Planner 已確認，用 `--update-snapshots=changed` 只重生這一張（`aad52811ba`）。

## follow-up（不屬本批）

- 預覽頁 confirmpopup 的 top 示意框與上方標籤（INFO 等）間距從約 13px 縮到約 5px。這是示意頁的版面，不是元件缺陷；記在 [marble-theme-followups.md](../../marble-theme-followups.md) §27。
- 水平 navbar 下拉中展開巢狀 nav（Settings）後，cave 高度不變，第三層項目畫在下拉外、看不見。把 nav CSS 換回 RED 版本也一樣，是既有問題，已記在 [marble-theme-followups.md](../../marble-theme-followups.md) §24。

GATE17-FINAL: PASS
