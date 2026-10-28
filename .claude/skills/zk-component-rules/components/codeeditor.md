# codeeditor

A server-side `<codeeditor>` component that wraps a client-assembled CodeMirror 6 instance:
line numbers, undo/redo history, syntax highlighting for a fixed set of languages, and an
editable/read-only toggle. ZK's own mold renders only two elements; everything below the
mount point is CodeMirror's own DOM tree, assembled asynchronously by its JS constructor —
not server markup, and not present at the instant the widget first attaches.

CE. Ships in `zul.jar`, widget class `zul.code.Codeeditor`, `@since 11.0.0`.

## DOM structure

```
.z-codeeditor                          (<div>, ZK root — carries domAttrs_: id, inline style, etc.)
└─ .z-codeeditor-cave                   (<div>, id="<uuid>-cave" — EMPTY in server-rendered HTML)
   └─ .cm-editor                        (CodeMirror-injected root — absent until zk.afterMount fires)
      [.cm-focused]                     (added/removed by CodeMirror itself on focus/blur)
      └─ .cm-scroller
         ├─ .cm-gutters                 (present only when lineNumbers=true, the default; absent
         │                                 from the DOM entirely when lineNumbers=false — not
         │                                 merely hidden)
         │   └─ per-line gutter cell
         └─ .cm-content                 (contenteditable="true" — the actual editable text surface;
                                           syntax-highlighted <span>s inside carry ZK-core's own
                                           inline `style`, not stable class names)
```

The mold pushes exactly `<div{domAttrs}><div id="{uuid}-cave" class="{cave}"></div></div>` — a
theme or test reading the DOM synchronously right after `bind_` will see the cave as an empty,
childless `<div>`. CodeMirror's `EditorView` is constructed one tick later, inside
`zk.afterMount(() => this._mount())`; `.cm-editor` and everything under it does not exist until
that callback runs.

## State classes

- `.z-codeeditor-dark` — on the root, toggled by `_applyDarkClass()` whenever `theme="dark"` is
  set (default `theme` is `"light"`, which renders with the class absent — there is no
  `.z-codeeditor-light` counterpart).
- `.z-codeeditor-disabled` — on the root, toggled by `_applyDisabledClass()` when
  `disabled="true"`. This call also sets CodeMirror's `contentDOM.tabIndex = -1`, dropping the
  editor out of the Tab order — the class alone does not communicate the keyboard-reachability
  half of "disabled"; both must be checked to confirm the full disabled behavior.
- **No readonly class exists.** `readonly="true"` only reconfigures CodeMirror's
  editable/read-only compartment (`_editableExtension()`, which also folds in `disabled` — the
  editor is locked when `readonly || disabled`); it leaves no DOM marker. A readonly instance
  keeps every class an enabled instance has and stays focusable, selectable, and copyable — only
  keystroke edits are blocked. Do not invent or look for `.z-codeeditor-readonly`; it never
  appears in this DOM.
- `.cm-focused` — added and removed by CodeMirror itself (not by ZK) on `.cm-editor` as the
  `contenteditable` region gains/loses DOM focus. This is the only focus signal in the tree; ZK
  adds no `-focus`/`-focused` class anywhere on this widget.

## Attribute support

- `disabled="true"` → `.z-codeeditor-disabled` on root + `contentDOM.tabIndex = -1`. Does not by
  itself change `pointer-events` or any other CSS property; that is left entirely to the theme.
- `theme="light"|"dark"` (default `light`) → `.z-codeeditor-dark` on root when `"dark"`; also
  reconfigures the syntax-highlight compartment to a dark-tuned `HighlightStyle` whose ten colors
  are each read from a fixed, ZK-core-authored custom-property name, resolved **entirely inside
  JS** with a JS-level literal fallback baked into the widget bundle — the CSS engine never
  resolves these independently; CodeMirror's `HighlightStyle` API consumes the resulting string
  and applies it as inline `style` per token span. A theme's own CSS file has no rule that
  consumes these names at all (see the theme contract for the exact spellings and defaults).
- `readonly="true"` → no class; editable compartment only (see State classes above).
- `lineNumbers` (boolean, default `true`) → gates whether the entire `.cm-gutters` subtree exists
  in the DOM at all. `false` removes it outright, it is not display:none'd.
- `tabSize` (integer, default `4`) → CodeMirror's `EditorState.tabSize` compartment. Affects only
  the rendered column width of a literal tab character inside `.cm-content`; no class, attribute,
  or other CSS-visible marker exists for it.
- `language` (`plain`|`html`|`xml`|`java`|`javascript`|`css`|`json`|`sql`|`markdown`, default
  `plain`) → selects a CodeMirror language-support extension (grammar + indent behavior + which
  tags `HighlightStyle` can match). No DOM/class signal of any kind.
- `value` → set through `<attribute name="value">`, since a plain ZUL attribute cannot carry
  embedded newlines for real multi-line source. CodeMirror normalizes CRLF/CR to LF in its own
  document model; the widget re-baselines its committed value from the actual CodeMirror doc
  after mount, not from the raw server string, specifically so a CRLF-supplied value does not
  look "changed" on first blur.
- There is no `invalid`/constraint-violation attribute on this widget — no
  `.z-codeeditor-invalid`/`-error` class exists to design a state for.

## Composition invariants

- **Root has zero padding between its border and the cave.** The cave — and everything
  CodeMirror injects below it — fills the root's content box edge-to-edge. Growing the root's
  border-width on any state (e.g. a naive focus treatment) shifts the entire CodeMirror viewport
  by that many pixels with nothing to absorb it, unlike a padded single-line input. Never use a
  border-width increase for an emphasis state — see `reference/focus-affordance-no-layout-shift.md`.
- **An emphasis ring MUST be drawn on an `::after` overlay, not as an inset box-shadow on the
  root.** This is the same zero-padding fact as above, seen from the paint side, and it is the
  trap this component walks into: an inset box-shadow paints below the element's children, and
  CodeMirror's DOM is both flush against the border and opaque. `.cm-gutters` carries an opaque
  fill in every theme, so a root-drawn ring is eaten on the gutter edge; if the theme also paints
  `.cm-editor` opaque for a dark surface, the ring disappears on **all four** sides. Measured
  2026-09-08 (designer report): light 1px/2px/2px/2px (left/top/right/bottom), dark 1px all round.
  Use the overlay recipe — root `position: relative` + `overflow: hidden`, ring on
  `.z-codeeditor::after` with `pointer-events: none` — so the ring paints above the payload and is
  uniform by construction. Generalises to any widget whose payload is a third-party editor mounted
  into a cave (tbeditor/Trumbowyg, pdfviewer/PDF.js).
- **Ring correctness cannot be checked from computed style.** `getComputedStyle(root).boxShadow`
  returns the ring declaration whether or not anything paints over it, so a theme with a fully
  buried ring passes a computed-value assertion. Verify emphasis rings on this component by
  measuring painted pixels (`screenshot.spec.ts › codeeditor` is the reference implementation).
- **Both `.z-codeeditor-cave` and CodeMirror's own `.cm-editor` must resolve to `height: 100%`**
  for the editor to fill an author-set root height. If either falls back to `auto` while the root
  has a fixed height, the visible editor collapses to zero rows while the outer border still
  draws a tall, apparently-empty box.
- **No intrinsic height.** With no `height`/`vflex` set on the component, the root sizes to `auto`
  and CodeMirror grows the visible viewport to fit its content (no internal vertical scrolling
  kicks in) — this is the documented, correct behavior, not a layout bug to "fix."
- **CodeMirror injects its own base theme through a runtime, unlayered `<style>` StyleModule tag**
  (appended under a CSP nonce read once from the page and reused across rerenders — see Notes).
  An unlayered author rule outranks every `@layer`-scoped rule regardless of specificity, so any
  property CodeMirror itself sets on a class it owns (`.cm-scroller`'s font/line-height,
  `.cm-focused`'s outline, `.cm-gutters`' fill/color/border) can only be overridden with
  `!important` (or another unlayered, equal-or-higher-specificity rule). This is a structural fact
  about how CodeMirror ships its own styling, independent of any theme's design choices.
- **`.cm-activeLine` / `.cm-activeLineGutter` / `.cm-selectionLayer` / `.cm-selectionBackground`
  are never emitted by the CE build.** The fixed CE extension set does not include
  `highlightActiveLine` or `drawSelection`. A rule targeting these selectors under a CE-only build
  is dead CSS — harmless, but do not assume it is exercised. A subclass or the EE build may add
  either extension through the sanctioned `_extraExtensions()` hook, at which point the classes
  would start appearing.
- **Syntax-highlight token coloring is inline `style` on generated `<span>`s inside `.cm-content`**
  (via CodeMirror's `HighlightStyle`/`syntaxHighlighting`), not stable CSS class names. There is no
  per-token class to select. The only sanctioned styling surface for token colors is a fixed set
  of ten ZK-core-named custom properties the widget JS itself reads (see the theme contract for
  the exact spellings) — set those, do not try to target generated spans by class.
- **`.cm-cursor` / `.cm-dropCursor` are NOT rendered by the CE build, so a theme cannot colour the
  caret through them.** They are created by CodeMirror's `drawSelection` extension, and
  `_baseExtensions()` does not include it — same fixed-extension-set fact as `.cm-activeLine`
  above. Verified 2026-09-08 against the shipped jar and against a live focused editor (0 such
  nodes). A rule for them is dead CSS; Marble shipped exactly that for a while and it was removed
  once the first real Gate-1 pass measured it. What paints instead is the **browser's native
  caret**, governed by `caret-color` on `.cm-content` — and `caret-color` does NOT inherit from
  `color`, so it falls back to the UA default (measured: pure `rgb(0,0,0)` on a light surface,
  pure `rgb(255,255,255)` on dark) regardless of what the surrounding text colour is. Any theme
  that wants a palette-matched caret must set `caret-color` explicitly; Marble knowingly does not
  (2026-09-08 decision). If `drawSelection` ever enters the extension set the nodes reappear and
  are coloured via `border-left-color`, not `background-color`/`color`.
- An author-supplied `aria-label`/`aria-labelledby` on the ZK root is read once at mount time and
  **additionally** applied onto CodeMirror's own `contentDOM` via `EditorView.contentAttributes` —
  the attribute is not removed from the root, but a screen reader interacting with the actual
  editable region gets its own copy of the label there too.

## Relational invariants

- **Cave fills root content box.** `.z-codeeditor-cave`'s rendered width/height equals the root's
  content-box width/height (no visible gap on any edge) whenever the root has a resolved size —
  a consequence of the zero-padding composition invariant above, not an independent design choice.
- **Gutter presence is binary, not visual.** `.cm-gutters` either exists as a full subtree
  (`lineNumbers=true`) or is entirely absent from the DOM (`lineNumbers=false`); there is no
  intermediate "gutter present but hidden" state to theme for.
- **Editor viewport clips to the root's rounding.** Because the root sets `overflow: hidden`, any
  border-radius applied to `.z-codeeditor` visually clips `.cm-editor`'s square corners to match —
  a theme choosing a rounded root gets a rounded editor "for free" without needing to separately
  round `.cm-editor`.

## State-differs invariants

- **`.z-codeeditor-dark` vs default (light).** At least one of `background-color`, `color` must
  differ between `.z-codeeditor-dark` and an unmodified instance, on both the root and
  `.cm-editor` — the root must repaint too (not just the inner editor), since the root owns the
  rounded corners and would otherwise show pale wedges around a dark inner editor.
- **`.z-codeeditor-disabled` vs enabled.** At least one of `opacity` (< 1 vs 1), `pointer-events`
  (on `.cm-editor`) must differ; a disabled instance must additionally have
  `contentDOM.tabIndex === -1` (see State classes) regardless of which CSS property carries the
  visual dimming.
- **`.cm-focused` vs unfocused.** At least one of `border-color`, `box-shadow`, `outline` on
  `.z-codeeditor` (or an always-present overlay it draws) must differ — CodeMirror's own default
  `outline: 1px dotted` on `.cm-focused` is not itself required to be kept (a theme may suppress
  it, per its unlayered-override composition invariant above), but *some* visible focus signal
  must exist somewhere in the ancestor chain the user is looking at.
- **hover vs resting.** No ZK- or CodeMirror-emitted class distinguishes hover; any hover
  affordance a theme adds is authored entirely through `:hover` on `.z-codeeditor` itself.

## Sibling decomposition

- **Outlined-field chrome** (border / radius / background / foreground / hover / focus color
  roles) mirrors the `textbox` single-line input pattern — see `components/textbox.md` (or the
  input family reference if consolidated): the same five-role recipe
  (`bg`/`fg`/`radius`/`border-color[-hover|-focus]`) and the same shape role a theme uses for its
  other outlined fields. **The focus mechanism differs from textbox's for a structural reason**:
  textbox can grow its border 1px→2px and absorb the shift with padding compensation (Mechanism
  B) because the border and the text share one element; codeeditor has no padding to compensate
  with (see the zero-padding composition invariant above), so it needs Mechanism A (inset
  box-shadow ring) instead — the same mechanism used by the wrapper-border combo-trio family
  (datebox/timebox/spinner/bandbox/timepicker), but for an unrelated reason: their constraint is
  min-height-pinned children, codeeditor's is zero root padding.
- **The editor viewport / gutter / caret subtree is CodeMirror's own semi-opaque payload**: a
  handful of top-level anchor classes are a legitimate, intended styling surface
  (`.cm-editor`, `.cm-scroller`, `.cm-gutters` — but NOT `.cm-cursor`/`.cm-dropCursor`, which the
  CE build never renders, see Composition invariants), but the internal
  generated structure (per-token highlight spans, and CodeMirror's own `.ͼ*` hash classes used
  for its StyleModule rules) is not a stable selector surface and must not be targeted directly —
  the same "reach into named anchor classes, leave internals alone" posture goldenlayout's
  contract takes toward `.lm_*`, just with a narrower allow-list here.

## Contract

Own file: `js/zul/code/css/codeeditor.css.dsp`. Declared directly in `lang.xml`'s own `<mold>` /
`<css-uri>` entry for the `codeeditor` component — it is not bundled into any sibling widget's
combined output (unlike, e.g., the combo-trio's shared `combo.css.dsp`); no other widget ships
through this file.

## Edition

CE (`zul.jar`). `@since 11.0.0`.

## Notes

- **Async mount.** The widget's own two server-rendered `<div>`s attach at normal `bind_` time,
  but CodeMirror's `EditorView` — and therefore every `.cm-*` node — is constructed inside
  `zk.afterMount(() => this._mount())`, one tick later. Code (tests, other widgets, a theme's own
  `bind_`-time inspection) querying `.cm-editor` synchronously at `bind_` will find nothing yet.
- `_mount()` no-ops (`if (this._cm || !this.desktop) return`) if invoked twice or after the widget
  has already been detached — safe to ignore re-entrancy from that angle.
- **CSP nonce is read once, not on every rerender.** `_nonce` is captured
  (`document.querySelector('script[nonce]') ?? document.querySelector('style[nonce]')`) the first
  time `bind_` runs, then reused on every subsequent rerender — the bootstrap `<script>`/`<style>`
  tag it was read from is pruned from the page shortly after initial load, so a later `bind_`
  could no longer find one itself if it tried to re-read.
- **Blur-commit model.** CodeMirror 6 has no native blur-commit event. ZK listens for
  `contentDOM`'s native `focusout` and fires `onChange` there (comparing the live doc against a
  `_committed` baseline, so an unmodified blur does not spuriously fire), and fires `onChanging`
  on every doc-change transaction (gated by `isListen('onChanging')`, so nothing is sent to the
  server when no listener is registered). Neither event corresponds to a CSS-visible class.
- **Destroy ordering on unbind.** `_cm` is set to `undefined` *before* `cm.destroy()` is called,
  because CodeMirror's own `destroy()` synchronously blurs a focused `contentDOM`, which fires a
  synchronous native `focusout` — the `focusout` handler's own `if (!this._cm) return` guard
  depends on `_cm` already being cleared at that point, or it would fire a spurious commit/onBlur
  event mid-teardown.
- Eleven of the ZK-core-documented custom-property names for this widget (ten syntax-token colors
  plus one active-line background) are consumed entirely inside widget JS, never inside this
  component's own CSS file — a theme has no reason to declare CSS defaults for them at all (see
  the theme contract for the exact spellings and for which of the total published custom
  properties a theme actually owns a default for).
