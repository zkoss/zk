# Marble Theme — Specification Index

This directory collects the **normative specifications** for the Marble theme: the
documents that *define* how the theme must look and behave. When two sources
disagree, these specs win.

For the *why* behind these rules (design rationale), point-in-time audits, harness
process docs, and reference lookups, see the other documents that remain directly
under [`../`](../).

## Specs

| Spec | What it governs |
|------|-----------------|
| [DESIGN.md](DESIGN.md) | Root design-language rulebook — surface palette, text colors, elevation hierarchy |
| [design-decisions.md](design-decisions.md) | Scope & won't-do decisions — dark mode, dedicated high-contrast theme, RTL backlog, theme priority, absent font props, ZK version support |
| [component-state-model.md](component-state-model.md) | Readonly vs disabled state model — popup controls render readonly as active, grey reserved for disabled |
| [new-component-checklist.md](new-component-checklist.md) | **Cross-cutting Definition of Done** for new components — CTV / brand / forced-colors / density / tablet obligations per harness role; the mandatory contract `## Cross-cutting features` section + `x-*` check-id vocabulary |
| [brand-override.md](brand-override.md) | Brand-color override: seed one token (`--zk-color-primary`) → containers/overlays derive via `oklch(from …)` absolute tone |
| [auto-contrast-text.md](auto-contrast-text.md) | Auto-contrast text color — why Marble pairs tokens + caps fills one-directionally instead of computing light-vs-dark text; the `contrast-color()` / relative-`oklch()` options and their browser status |
| [component-theme-variables.md](component-theme-variables.md) | **Component Theme Variables** — per-component appearance variables (`--zk-<comp>-*`) restyle one component (fill/border/radius/state) at `:root` or a region without forking; cascade/load-order rules, conformance criteria (CTV-1…9) + per-family vocabulary. Shipped: button, input, window, grid, listbox, tree, panel, groupbox, combobox, datebox, timebox, spinner, bandbox, tab, menu, avatar, chip, badge. Status: [progress tracker](../../tasks/component-theme-variables-progress.md) |
| [window-design-rules.md](window-design-rules.md) | Window `mode` → elevation mapping; `border` must never drive shadow |
| [spacing-policy.md](spacing-policy.md) | Widgets carry zero default margins; spacing is opt-in via containers/utilities |
| [utility-naming.md](utility-naming.md) | Naming rules for the 423 `z-*` utility classes — Bootstrap vocabulary, Tailwind only where shorter *and* unambiguous; keep the hyphen (`z-m-1`); no aliases; a trailing `-sm/-md/-lg/-xl` always means a size, breakpoints go in the infix slot (`z-d-md-none`); logical properties only; a utility never takes a noun that could name a ZK component; accuracy beats brevity. Also how to verify a rename, and why a *shift* within a closed set needs a different gate from a rename |
| [navigation-surface-rules.md](navigation-surface-rules.md) | Navigation chrome uses MD3 tonal elevation (surface-container tiers), not box-shadows |
| [md3-close-affordance-placement.md](md3-close-affordance-placement.md) | Where close/dismiss icons sit (inline trailing vs surface top-corner) |
| [icon-policy.md](icon-policy.md) | FontAwesome → Lucide icon name mapping policy |
| [icon-index.md](icon-index.md) | Auto-generated canonical lookup of valid `z-icon-*` names + FA aliases (the reference data `icon-policy.md` governs; regenerate via `npm run build:css`) |
| [css-dsp-file-structure.md](css-dsp-file-structure.md) | The required `*.css.dsp` output files the theme must produce |
| [reset-scoping.md](reset-scoping.md) | `org.zkoss.zul.theme.browserDefault` reset-CSS scoping for JS-Embed host pages |
| [data-dense-mode.md](data-dense-mode.md) | Compact density via `data-density="compact"`; control-height ladder + alias layer |
| [tablet-design-overview.md](tablet-design-overview.md) | Tablet/mobile responsive theme layer (`tablet.css.dsp`) |
| [responsive-design.md](responsive-design.md) | Responsive utility system — `z-grid-fill` auto-fit + `z-grid-cols-auto`; intrinsic/auto-fit over Bootstrap Grid |
| [print-styles.md](print-styles.md) | Print stylesheet — opt-in `.z-d-print-*` visibility utilities + automatic `@media print` reset (hide chrome, un-stick headers, expand scroll bodies, elevation→hairline border); keeps brand color (no ink-saving) |
| [zindex-scale.md](zindex-scale.md) | Stacking scale — `--zk-index-*` tokens + `.z-index-*` utilities. **Key fact:** ZK stamps floating-widget z-index inline at runtime (base 1800, flat global counter), so only non-floating values are load-bearing; the 1000–2000 component values are cosmetic fallback. Enterprise guidance: stay below 1800 or use `setTopmost()`, don't out-bid the counter |
| [zk-mui-structural-gaps.md](zk-mui-structural-gaps.md) | ZK↔MUI differences unresolvable by CSS alone (floating label, multi-select, calendar today-marker, ripple) — expected limits, not bugs |
| [native-modern-ui-components.md](native-modern-ui-components.md) | Native `badge`/`chip`/`avatar`/`avatargroup` (ZK 10.4+) — class/attribute contract; old-utility → native-severity mapping; replaces the former `.z-badge`/`.z-chip`/`.z-avatar` utility CSS |
| [screenshot-tolerance.md](screenshot-tolerance.md) | Playwright screenshot gate tolerance — `maxDiffPixels` floor for state shots, per-project `threshold`, `maxDiffPixelRatio` opt-ins; the zero-tolerance equivalence run stays at zero |

## Related

- [`../contracts/`](../contracts/) — per-component design contracts (one `.md` per ZK component). These are the component-level specs; they stay outside this directory because they are wired into the verification harness, agents, and skills.
- Everything else under [`../`](../) — design rationale, audits, process playbooks, and reference docs.
