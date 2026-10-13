# Density (compact / data-dense mode)

Marble's default sizing targets general business UIs. ERP-class data-dense apps need to pack far
more onto a screen. The customer-facing spec is `doc/spec/data-dense-mode.md`.

## The one thing to understand first

**Density is a system-wide property.** A first draft shrank only data rows and inputs and was
correctly rejected: rows at 32px while buttons stay 36, toolbars/menus/tabs stay 48 and window
headers stay 56 makes the page look broken — tight data islands floating in generous chrome.
Everything scales. There are no exempt landmarks.

**The default tier reproduces today's rendering exactly.** Every comfortable value equals the
current px. Only the customer's compact override changes anything.

## Three layers, all in `tokens/_sizing.css`

**Layer 1 — the control-height ladder (the seed; this is what a customer flips):**

```css
--zk-control-height-xs: 28px;   /* small buttons, close buttons, separators */
--zk-control-height-sm: 32px;   /* paging controls, window-close */
--zk-control-height-md: 40px;   /* DEFAULT: inputs, icon-button, menu items, list rows */
--zk-control-height-lg: 48px;   /* chrome bars: toolbar, menubar, tabs, panel/groupbox header */
--zk-control-height-xl: 56px;   /* prominent: window header, FAB */
--zk-control-height: var(--zk-control-height-md);   /* kept — 24 existing consumers */
```

**Layer 2 — the semantic alias layer.** Every component binds to a rung **by meaning**, never to
a raw px. This is the discipline that prevents drift: the intentional input-40 / button-36 /
toolbar-48 *relationships* survive a tier change because the relationship is defined once, here.

```css
--zk-input-height:           var(--zk-control-height-md);   /* 40 */
--zk-button-height:          36px;                          /* off-ladder, intentional (MD base) */
--zk-button-height-sm:       var(--zk-control-height-xs);   /* 28 */
--zk-button-height-lg:       44px;                          /* off-ladder */
--zk-icon-button-size:       var(--zk-control-height-md);
--zk-fab-size:               var(--zk-control-height-xl);
--zk-toolbar-height:         var(--zk-control-height-lg);
--zk-menubar-height:         var(--zk-control-height-lg);
--zk-tab-height:             var(--zk-control-height-lg);
--zk-window-header-height:   var(--zk-control-height-xl);
--zk-panel-header-height:    var(--zk-control-height-lg);
--zk-groupbox-header-height: var(--zk-control-height-lg);
--zk-notification-height:    var(--zk-control-height-lg);
--zk-paging-control-size:    var(--zk-control-height-sm);
--zk-data-row-min-height:    52px;                          /* off-ladder */
--zk-header-padding-y:       …                              /* see "padding-driven headers" */
```

A normal customer flips only the 5 rungs plus 3 off-ladder seeds (button 36/44, row 52); every
alias follows automatically.

**Layer 3 — data-cell padding:** `--zk-grid-cell-padding`, `--zk-grid-detail-padding`, `--zk-listbox-cell-padding`,
`--zk-tree-cell-padding`. **All four must be declared at `:root`**, not on the component —
see the cascade-shadowing trap in `reference/tokens.md`.

## The switch

Two equivalent delivery mechanisms, both a `:root` override that loads after the theme and wins
the cascade: the customer pastes a compact `:root{}` block, or sets `data-density="compact"` on
`<html>` (whole app) or on any container (one region). `tokens/_sizing.css` carries the
`[data-density="compact"]` block. `MarbleDensity` is the Java API for the same thing.

The architecture was chosen after surveying Ant Design, Material 3, MUI, Salesforce Lightning,
IBM Carbon and Bootstrap. **Salesforce Lightning** supplied the delivery model (one root-scope
flip re-points token values; components consume tokens and reflow). **Ant Design and Carbon**
supplied the discipline (a small height ladder plus a semantic alias layer). MUI and Bootstrap
are the cautionary tale: with no seed, "global density" degenerates into hand-listing every
component — which is the state Marble was in before this work, with only 3 of ~24 chrome sites
using a height token and the rest hardcoding 28/32/36/40/44/48/56.

## Two issues found during verification, both now designed for

- **Padding-driven headers.** Window and panel headers are sized by 16px vertical padding, not by
  `min-height`, so a height-only ladder cannot compress them. `--zk-header-padding-y` exists for
  this; without it the header stays at 57px while everything around it shrinks.
- **Cascade shadowing.** The three cell-padding tokens were declared on the component and
  shadowed the `:root` override. Hoisted. Any new overridable token needs the same check.

## Verifying a density change

1. `node scripts/build-css.js --module zul`
2. **Default unchanged is the critical gate.** Screenshot button, toolbar, menu, tabbox, window,
   panel, grid, listbox, tree, datebox, checkbox plus the employee-grid and order-entry use-cases,
   and pixel-compare against a pre-change baseline. It must be *identical* — that is what proves
   the ladder/alias refactor changed no default value.
3. **Compact applied:** inject the compact block, reload the same pages, confirm the whole page
   shrinks coherently. Watch specifically for clipping in the nested date/time/spinner/bandbox
   inputs and for checkbox/radio centring — the latter needs auto/flex centring, not the old
   fixed `margin: 11px`, or the 18/20px mold decentres at other control heights.
4. `bash .claude/skills/marble-theme/scripts/audit-css.sh --out <file>` — no new hardcoded-px regressions; new tokens show as referenced.

The last verified probe run was 8/8: button 36→32, tabs 49→46, window header 57→48, tree row
52→36, datebox 40→32 (inner 38→30), textbox 40→32, checkbox row 40→32, grid cell padding
16px→6px 12px.
