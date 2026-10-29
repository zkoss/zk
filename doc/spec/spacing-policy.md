# Marble Spacing Policy (opt-in, zero default margins)

> Revised 2026-06-05 (user ruling): the previous
> "default vertical rhythm" rule (`_rhythm.css`, auto `margin-block-end` on container
> widgets) has been **removed**. This page records the current policy and why.

## TL;DR

| 軸向 | 預設 (auto) | 間距機制 (opt-in) |
|------|-------------|-------------------|
| **垂直** | ❌ 零 margin | 父層 `.z-vstack[-sm/-md/-lg]`、單顆 `.z-mb-*`、或 ZK 原生 `<vlayout spacing>` |
| **水平** | ❌ 零 margin | 父層 `.z-hstack[-sm/-md/-lg]`、`.z-d-flex` + `.z-gap-*`、或 `<hlayout spacing>` |

**核心規則:任何 widget 都不帶 theme 預設 margin。** 間距永遠由「容器」或「明確的 utility class」宣告。

## 為什麼不做預設 rhythm(2026-06-05 反轉的原因)

之前的版本給 block-level 容器類 widget(window, panel, grid, listbox, …)預設
`margin-block-end: var(--zk-spacing-3)`,目標是「不加 sclass 擺下去就好看」。
實際運行後發現**結構性矛盾**:

1. **Stock ZK 的 widget 全部零 margin,ZK 的 JS sizing 是在這個前提下寫的。**
   `zk/flex.ts applyCSSFlex` 會量測子元件的 margin 並寫
   `calc(100% - marginHeight)`(row 模式,**兩個軸都扣**)— theme 預設的
   12px margin 直接變成 splitlayout 窗格旁的 12px 破洞(gap log 2026-06-05,
   contract `splitlayout.md` M12)。同類的 margin 量測散佈在多個 JS code path
   (`setFlexSize_`、layout region sizing…),例外清單列不完。
2. **Bootstrap / MUI 實務也是零 widget margin**:Bootstrap reboot 只對
   `<p>/<h1-6>/<table>` 等純 HTML 元素加 margin,`.card`/`.btn`/`.alert` 全部
   沒有;MUI 靠 `<Stack spacing>` — 對應 Marble 的 `.z-vstack`/`.z-hstack`。
3. **對 Java 工程師更友善**:ZK 本來就有他們熟悉的 `<vlayout spacing>`/
   `<hlayout>`;「父層加一行 `sclass="z-vstack"`」照文件抄就對,失敗模式
   一眼看懂(元件貼在一起)。rhythm 壞掉的症狀(JS 算出 `calc(100% - 12px)`
   的不對稱破洞)反而只有讀過 ZK 原始碼才能 debug。

ZK 事實(margin subtraction)記錄在 skill:
`.claude/skills/zk-component-rules/reference/css-flex-classes.md`。

## 使用指南(寫 ZUL 頁面時)

```xml
<!-- 垂直堆疊區塊:父層一個 class,所有直接子元件之間 12px -->
<div sclass="z-vstack">
    <window title="A" border="normal">…</window>
    <grid>…</grid>
    <window title="B" border="normal">…</window>
</div>

<!-- 水平排 button:flex + gap,永遠不用 per-widget margin -->
<div sclass="z-hstack">
    <button label="Save"/>
    <button label="Cancel"/>
</div>

<!-- 單顆例外:明確的 margin utility -->
<window sclass="z-mb-6" …/>
```

尺寸變體:`z-vstack-sm` (8px) / 預設 (12px) / `z-vstack-md` (16px) /
`z-vstack-lg` (24px);`z-hstack` 同系列。見 `zul/css/utility/_stack.css`。

## 元件內部的間距

元件**內部**(panel header 與 body 之間、portallayout 欄與欄之間…)的間距是
component CSS 的責任,scoped 在該元件的 selector 下 — 例如 portallayout 的
stacked-panel gutter(`.z-portalchildren-content > .z-panel { margin-bottom }`,
portallayout 自己的 JS 管 sizing,不經過 css-flex,所以安全)。
**絕不**用全域 widget selector(裸 `.z-panel`、`.z-window`)加 margin。

## Layout primitive 沒有預設 padding(wrap, don't pad)

跟「零預設 margin」對稱:ZK 的 **layout primitive** — `vlayout`、`hlayout`、
`vbox`、`hbox`、`div`、`cell`、`borderlayout`,以及 layout 家族裡的 `*-body` —
一律**零 padding**。把內容包進 `<div>`/`<vlayout>` 後文字會貼齊容器邊緣,這
**不是 bug**:它對齊 web 平台的 Grid/Flexbox/Box 語意。若每層 `<vlayout>` 都帶
16px padding,巢狀 layout 會累積出無法解釋的死白(三層 = 48px)。

規則:**primitive 保持零 padding,作者在需要處明確 opt-in。**

```xml
<!-- ❌ primitive 不吃 padding -->
<vlayout>Content</vlayout>
<!-- ✅ 內層 wrapper 加 padding utility -->
<vlayout><div sclass="z-p-4">Content</div></vlayout>
<!-- ✅ 或用本來就帶 padding 的 semantic container -->
<panel title="…"><panelchildren>Content</panelchildren></panel>
```

| 元件 | 預設 body padding | 原因 |
|------|------------------|------|
| `vlayout`/`hlayout`/`div`/`vbox`/`hbox`/`cell`、`borderlayout` 的 `*-body` | 無 | layout primitive — 負責組合 |
| `panel`(`panelchildren`)、`groupbox`、`window` 內容區 | 有 | semantic container — 代表卡片/區塊/對話框 |

padding utility 見 `zul/css/utility/_spacing.css`(`z-p-*`/`z-px-*`/`z-py-*`/
per-side,數字對應 `--zk-spacing-N`)。**禁止**直接對 `.z-vlayout`/`.z-hlayout`/
`.z-div` 或 `*-body` 加 padding — always-on padding 會破壞組合性;要永遠帶
padding 就改用 semantic container。

## Use-case / demo 頁面:優先組合 `z-*` utilities

在 use-case / demo 頁面(`zkthemelab/src/main/webapp/web/**`)裡,優先用內建 `z-*`
utility class 組合出版面,而不是自訂 page-local `sclass`。demo 頁面的目的是證明既有內建
CSS 就能完成多數視覺效果。

自訂 page-local class **只有**在下列情況才成立:
- **(a)** 需要 pseudo-class 規則(如 `:last-child` 移除最後一條分隔線)—
  pseudo-class 無法用 utility 表達。
- **(b)** 需要固定的 non-token 像素尺寸(如 status dot 8px 圓點、chat bubble
  的 `12px` radius + `10px 14px` padding)— 換成 token utility 會改變外觀。
- **(c)** 需要任何 utility 都沒有提供的固定高度(如 chart placeholder 的
  `220px`)。

其餘一律用 utility 組合。範例:`z-d-flex z-flex-col z-gap-4 z-p-4` 取代自訂
`page-content`;`z-text-success` 取代自訂 `trend-up`。

## 歷史:per-widget margin 的三種踩雷情境(仍然成立)

| 情境 | 問題 |
|------|------|
| 放在 `<toolbar>` / `<menubar>` (flex `gap`) | margin + gap 雙重間距 |
| 放在 grid / listbox 的 cell | 撐破 cell 對齊 |
| 放在 `hflex`/`vflex` 的 flex wrapper 裡 | ZK 把 margin 扣進 `calc()` → 破洞 |
