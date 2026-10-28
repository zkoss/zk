# CTV 設計筆記：為什麼「字型（typography）」不做成元件級變數

> 補充說明 [component-theme-variables.md](spec/component-theme-variables.md) 的 **Authoring rule 4** 與 **CTV-6**。
> 起因：`window.css` 的標題文字讀的是全域 `--zk-typescale-title-medium-*`，而不是 `--zk-window-title-*`，
> 有人問這樣對不對、其他 framework 怎麼設計。

## 一句話結論

**這是主流做法，`window.css` 是對的。** 幾乎所有主流 framework 都把 typography（字級、字重、行高、字族）
當成**跨元件的「系統 token」（一個共用的 type scale）**，而不是每個元件各自的旋鈕。Marble 的 CTV 只暴露元件的
**「皮膚 / skin」**軸（fill / 文字*顏色* / border / radius / 關鍵狀態 / 尺寸），typography 刻意留在全域 typescale。

## 關鍵區分：skin token（元件級）vs. system token（全域）

| 類別 | 例子 | 該不該做成 `--zk-<comp>-*`？ |
|------|------|------------------------------|
| **Skin / 品牌外觀** | 背景/填色、文字**顏色**、border 顏色/寬度、圓角、hover/selected/error 的重點色 | ✅ 是 —— 這正是使用者想「只改這個元件」的東西 |
| **System / 一致性** | 字級、字重、行高、字族（type scale）；間距節奏；elevation 階梯 | ❌ 否 —— 這是全站一致性的骨架，散到各元件會破壞它 |

CTV 的目的是「讓使用者換**一個元件的皮膚**」，不是「讓使用者重新排版」。字型屬於後者。

## 各家 framework 怎麼做（都指向同一結論）

| Framework | Typography 放哪 | 元件能不能覆寫字型 |
|-----------|----------------|-------------------|
| **MUI (Material UI)** | 全域 `theme.typography` 的具名 variant（`h1–h6` / `subtitle` / `body1/2` / `button` / `caption`）。元件引用 variant —— Button → `typography.button`、DialogTitle → variant `h6`。CSS-vars 模式輸出的是 `--mui-typography-*`（全域），**不是**每個元件一個字級變數。 | 可以，但走 `styleOverrides` 這個**逃生門**，預設結構不替每個元件生字型旋鈕。 |
| **Ant Design v5** | seed → map → **alias** → component 的分層。字型 alias 是全域的：`fontSize` / `fontSizeLG` / `fontSizeHeading1–5` / `lineHeight` / `fontFamily`。 | 部分元件 component-token 確實有字型欄位，但**預設由全域 alias 推導**（global-first，component 只是可覆寫的衍生層）。 |
| **Chakra UI** | 全域 scale `fontSizes` / `fontWeights` / `lineHeights` + `textStyles`；元件 recipe 從 scale 取值。 | 元件從共用 scale 取，不各自定義字級。 |
| **Tailwind / Bootstrap / Open Props** | 純全域：`text-*` / `font-*`、`$font-size-base` / `$headings-*`、`--font-size-*`。 | 無元件級字型概念。 |

**共識**：type scale 是**系統級 token**。理由是**排版一致性與垂直節奏**是全站設計目標——
「Title 在哪裡都該長得像 Title」。如果每個元件都有獨立字級變數，scale 的一致性就碎掉了。
有元件級主題系統的 MUI / Ant 也只把「元件改字型」當**逃生門**，且 Ant 的逃生門仍**預設回落到全域 alias**。

## 對 Marble CTV 的意義

- **維持現狀**：`window.css` 標題讀 `--zk-typescale-title-medium-*` 是正確的，**不要**把它包成 `--zk-window-title-size`。
  這與 MUI（DialogTitle → `h6`）、Ant（Modal 標題 → 全域 heading token）一致。
- 這其實已寫在規格裡，只是隱含：Authoring rule 4 / CTV-6 的可暴露軸是
  **fill / text（顏色）/ border / radius / 狀態 / 尺寸**——**typography 不在清單內**，代表刻意排除。
- **逃生門（若日後真的需要）**：當出現「只想放大**這個**元件的字、又不想動全站同級文字」的真實需求時，
  採 **Ant 模式**——加一個元件旋鈕，但**預設回落到全域 typescale slot**，而**不是**寫死字面值：

  ```css
  /* ✅ 需要時這樣做：預設 = 全域 slot，零回歸、預設仍一致，只是給了覆寫點 */
  :root { --zk-window-title-size: var(--zk-typescale-title-medium-size); }

  /* ❌ 不要這樣：字面值會脫離 type scale，破壞一致性、也難維護 */
  :root { --zk-window-title-size: 16px; }
  ```

  在沒有這個需求前，**不預先加**（符合「Simplicity First / 不做投機性設計」與 rule 4「keep the set small」）。

## 建議

保持現狀。若要讓這條規則從「隱含」變「明說」，我可以在
[component-theme-variables.md](spec/component-theme-variables.md) 的 **Authoring rules** 加一條
「typography 屬全域 typescale，非元件旋鈕；有需求時以回落到 typescale slot 的方式暴露」——需要的話再說。
