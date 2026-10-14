# Decision principles: settle appearance questions without asking

The project owner has ruled on well over a hundred Marble appearance questions. These principles
are what those rulings have in common. Apply them yourself: a question they settle is **not**
asked. Only the cases in §4 go to the owner.

Where each principle came from (D-ids, issues, dates) stays in the documents that recorded the
rulings — `doc/jess-review/decisions.md`, `doc/zkdemo-b6-marble-fixes.md`,
`doc/zkdemo-b8-triage.md`, `doc/marble-master-sync-strategy.md`, `doc/spec/`. This file holds
conclusions only.

## How to decide

1. **Ownership first** (§1): who fixes it, and is it a theme question at all?
2. **Look for a precedent:** §3 here, then `doc/spec/` (normative), then
   `doc/contracts/<component>.md`.
3. **No precedent:** apply the precedence order in §2.
4. **Check §4.** If no item applies, decide, act, and cite the principle IDs in the report —
   e.g. "decided by O1 + V6" — so the owner can audit the call.

## 1. Ownership

- **O1 — Reproduce in IceBlue first.** Run the same page on master with IceBlue at the same ZK
  version before deciding who fixes. Absent in IceBlue → Marble caused it → fix it in Marble CSS on
  `marble` with a `ZK-6112: …` commit; no new Jira.
- **O2 — Both themes, cause in shared code** (widget TS/JS, Java, ZUL, IceBlue LESS) → a ZK Jira
  (zk-bug-filing skill, Affects Version, screenshot). The core team fixes it; the Marble session
  does not. Filing the Jira is decided here; only the filing itself is confirmed (E6). When the
  core fix will add the hook Marble needs (always so for O3), Marble styles it after the fix
  lands and does not target inline-style values or other markup the fix will change.
  A look each theme chose on its own, with no defect in shared code, is not O2 but E2.
- **O3 — Appearance written inline by widget JS** (colour, font, border, background) is an O2
  bug, even when only one theme looks wrong. Only runtime-computed position and size may stay in
  JS.
- **O4 — Split what CSS can reach.** If the visual target is reachable through classes and states
  ZK already emits — including `:has()` with `:active`, `:hover` or `:focus-within`, or
  structural selectors such as `:nth-child` verified on today's DOM — fix that part in CSS now and
  file a Jira for the behaviour part. Before claiming "CSS
  cannot", check ancestor and sibling classes and try `:has()`.
- **O5 — The preview page is the cause** (inline `style=`, a wrong attribute, demo-only markup) →
  fix the page, never compensate in theme CSS. Pages compose existing `z-*` utilities; no
  page-specific theme CSS.
- **O6 — Wrong docs, by-design ZK behaviour, or a deliberate design** → no CSS change; answer with
  a screenshot. A choice that belongs to the designer (e.g. which mold a demo uses) is handed back
  in the reply, not settled in CSS.
- **O7 — Upstream LESS changes in a master sync.** Port only when Marble reproduces the original
  issue, or when a class or DOM name changed (always port those). Express the port with `--zk-*`
  tokens; never copy LESS numbers; measure Marble's geometry rather than reuse IceBlue constants.
  Delete Marble rules whose DOM upstream removed. A port that fixes a real defect is taken even
  when it costs layout space; record the cost for the release note.
- **O8 — New behaviour is not styling.** A request for behaviour ZK lacks (inline error mode, a
  new attribute) → a New Feature Jira, not theme CSS.
- **O9 — Accessibility fixes live in component CSS** or the central `_forced-colors.css`, never
  in an optional add-on sheet Marble does not load.
- **O10 — Stay inside the reported issue.** Other findings go to `doc/marble-theme-followups.md`;
  no drive-by fixes in adjacent code.

## 2. Precedence: which source wins

- **V1 — Normative documents.** `doc/spec/` and the component contract beat older notes. A
  contract's `## Accepted MD3 deviations` section holds owner rulings; do not re-flag them.
- **V2 — MD3 decides semantics:** colour roles and `on-*` pairing, state layers, the type scale,
  elevation as spatial role, shape, keylines. **When two options differ, pick the one that fits
  MD3 better** (the owner's stated tie-breaker). MD3 has no italic and no opacity-grey hierarchy;
  hierarchy is tone and weight.
- **V3 — MUI v7 decides values:** padding, font sizes, heights, transitions (see SKILL.md,
  "External reference: MUI's CSS"). MD3 numbers apply only where MUI is silent. Marble is
  deliberately denser than MD3 defaults.
- **V4 — The ZK API contract beats MUI's look.** Honour what ZK attributes and ZK JS assume:
  `cols`/`size` set width, `align=` hints win over theme alignment, widgets have zero margin.
  Values ZK code already ships (e.g. the EE breakpoints 576/768/992/1200) beat MUI's values.
  When a theme invention collides with ZK JS, delete the invention.
- **V5 — IceBlue is the reference for structure and behaviour, not for style.** Use it to learn
  the DOM, what ZK JS expects, whether a defect is a regression, and which basic spacing or reset a
  component needs. "Looks like IceBlue" loses to V2.
- **V6 — Same role, same look.** Siblings that play the same role look the same across
  components, modes (table and card) and densities. Fixing one sibling means fixing all of them;
  fix the shared rule rather than patching one component. The wider change this causes (every
  Window, both orientations) is reported, not asked. Consistency is by role, not by sameness: a
  component with a distinct role may differ.
- **V7 — V2 against V6 is not yours to settle** → §4 E1.
- **V8 — Designer requests.** Use the tokens and values the designer names. Take the narrowest
  reading, and reproduce the designer's screenshot (same page, viewport, steps) before fixing.
  Where the request is a partial step toward an MD3 pattern, implement the whole pattern (V2).
- **V9 — Browser limits.** When the literal request is impossible in the browser, deliver the
  visual intent and state the difference in the reply.
- **V10 — Contrast floors:** text ≥ 4.5:1, graphical objects ≥ 3:1, measured on the real
  background. When the MD3 token fails in Marble's palette, use the nearest token that passes and
  record when to revisit.

## 3. Baselines: apply directly

| Situation | Answer |
|---|---|
| Selected list row or dropdown option | `secondary-container` fill, `on-secondary-container` text |
| Hover | one strength framework-wide (the state-layer token); equal on every row regardless of striping |
| Focus on list rows | only on keyboard movement, only the current row; listbox and tree share the shape |
| Cursor | `pointer` only on what is clickable; default elsewhere, never `move` on non-draggable parts |
| Scrollbar | as `zul.Scrollbar` at rest: `outline-variant`, 8px, rounded, no track groove; hover one tone darker (`outline`); the scrollbar column in a header has the header's colour |
| Dropdown arrow (selectbox, combobox, bandbox, accordion) | one chevron-down mask, ink about 8×5, right inset measured (about 12px); `CanvasText` under forced colours |
| Readonly field | looks like the editable field; no selection highlight that suggests editing; grey or 38% opacity means disabled only |
| Toast and notification | no left colour stripe; icon `on-*-container`; both use the same `*-container` tokens |
| Grid, Listbox, Tree headers | no fill, weight 500, same height, one line with ellipsis; sort icon out of the text flow at the trailing end, same size in all three; space reserved for any icon laid over the text |
| Grid, Listbox, Tree text edges | header, body, group and footer text start on one edge; one padding layer, never td + content; nested content (e.g. `detail`) aligns to the parent row's text keyline; drop a divider that doubles an existing border |
| Inline accent that must not move layout | inset `box-shadow`, plus a border fallback under forced colours |
| Buttons inside popups and dialogs | the plain button height tokens; emphasis ladder filled > outlined > text, one filled action per surface |
| Window elevation | set by `mode`, never by `border` |
| Navigation chrome | tonal `surface-container-*` tiers, no shadow; selected nav item is the MD3 tonal container, no left bar |
| Close button placement | by shape: trailing for inline items, header corner for surfaces |
| Border or shadow | never both; outlined where a shadow would not show on the page background |
| Icons | Lucide masks; never text glyphs; header control buttons share one family (size, icon set, states) |
| Forced colours | handled centrally in `_forced-colors.css`; restore only information-carrying affordances; every mask icon needs `CanvasText` |
| Layout primitives (vlayout, hlayout, div, cell) | zero padding and margin; semantic containers (panel, groupbox, window) pad |
| Layout the spec does not support (e.g. colspan under frozen columns) | document it, guard against layout shift, do not engineer support |
| Public `--zk-*` token | keep the knob and change only its default; never change a knob's meaning (add a new token); no token that cannot work (e.g. breakpoints, since `@media` cannot read `var()`) |
| Utility class names | follow `doc/spec/utility-naming.md` (Bootstrap vocabulary first; an accurate name beats a short one; one rule, one name) |

## 4. Ask the owner only here

Present each question as an Artifact with full-width screenshots — before, IceBlue, and each
option — plus a recommended option, the principle that could not settle it, and the cost.

- **E1** MD3 and same-family consistency point different ways, or two requests contradict.
- **E2** A taste call with no precedent and no MD3/MUI answer: a new visual idiom, or improving
  in Marble a look both themes share. Measure first: if it fails V10 or breaks a §3 row it is a
  defect, and you decide it.
- **E3** Retiring a spec or contract item, or adding an accepted MD3 deviation. Changing a
  default look because V2 or V6 requires it is decided, not asked.
- **E4** Adding, renaming or removing a public `--zk-*` token or utility class (once adding is
  agreed, the name follows `doc/spec/utility-naming.md`), or changing a global role token's value
  in `tokens/` (colour roles, type scale, shape). A component token's default is decided (§3).
- **E5** The fix needs a new `!important`.
- **E6** Filing a Jira or any other outward action (always confirmed; see CLAUDE.md).
- **E7** The request stays ambiguous after reproducing its narrowest reading.
- **E8** A contrast floor (V10) cannot be met without E4.
- **E9** The request touches a won't-do in `doc/spec/design-decisions.md` (dark mode, a separate
  high-contrast theme, the deprecated font properties).
- **E10** A fix still fails after three rounds, or a regression cannot be attributed.

## 5. After the owner rules

Fold every ruling back in, in the same commit as the work it decided:

- It follows from an existing principle → add a §3 row only if the situation will recur.
- It contradicts a principle → change the principle; do not add an exception beside it.
- It is a one-off → change nothing here.

Record where the ruling came from in the working document (D-id, date), not here.
