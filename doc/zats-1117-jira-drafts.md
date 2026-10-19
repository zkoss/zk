# Jenkins #1117：要開給核心團隊的 ZK Jira 草稿

依 `doc/zats-1117-fix-plan.md` 的 D3（已決：選項 A，2026-10-16）起草。**使用者確認前不送出。** 送出時依 `zk-bug-filing` skill：project `ZK`、type Bug、`contentFormat: markdown`；`versions`（Affects Version/s）要在送出當下查 ZK 專案已定義的版本後再填（這段程式自 `bd09084a33`（2022-07-13）起就沒有改過，所以已發行的 10.x 都受影響）。

## D3：Slider 浮點取模

- **summary：** `slider: decimal mode snaps one step low when the position lands exactly on a step`
- **相關測試：** `F70_ZK_2085Test`、`F50_3028270Test`（Marble 下失敗，IceBlue 下只是落點剛好沒有在刻度上）

````markdown
# Steps to Reproduce

```xml
<zk>
  <slider minpos="10" maxpos="12" step="0.05" mode="decimal"
          onScrolling='self.nextSibling.setValue("" + event.getPosInDouble())'/>
  <label/>
</zk>
```

1. Drag the slider knob to the right so that the raw position is exactly 4 steps from the minimum (10.2).

The same happens without dragging: any raw position that is an exact multiple of `step` from `minpos` (10.2, 10.25, 10.35, 11.1, …) is snapped down by one step.

# Current Result

The label shows 10.15. For a raw position of 10.2, `(10.2 - 10) % 0.05` evaluates to 0.04999999999999928 in floating point instead of 0, so `_constraintPos` subtracts almost a whole step.

# Expected Result

The label shows 10.2: a position that is already on a step stays on that step.

# Debug Info

`Slider._constraintPos` snaps with a floating-point remainder:
https://github.com/zkoss/zk/blob/85c7731da4d2d66d153eb7ba3df8282990313d2f/zul/src/main/resources/web/js/zul/inp/Slider.ts#L524

`pos -= (pos - this._minpos) % step;`

Measured in Node.js (`p - ((p - 10) % 0.05)`): 10.2 → 10.15, 10.25 → 10.2, 10.35 → 10.3, 11.1 → 11.05, while 10.3 → 10.3 and 10.4 → 10.4. Snapping on the step count instead of the remainder avoids it, e.g. `minpos + Math.floor(Math.round((pos - minpos) / step * 1e9) / 1e9) * step`, followed by the existing `_roundDecimal`.

Found while triaging Jenkins ZK-Gradle-ZATS-Test #1117: F70_ZK_2085Test and F50_3028270Test fail whenever the drag lands exactly on a step; with the old default theme the drag happened to land off-step, so the tests passed by coincidence.
````

## D4：Tabs 在 web font 載入後沒有重算捲動（待使用者決定）

尚未起草。等使用者決定 D4 之後再寫。
