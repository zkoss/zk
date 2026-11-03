# Component Theme Variables

> **Feature name:** *Component Theme Variables* (CTV). The individual `--zk-<comp>-*` custom
> properties are the theme variables; this doc also calls a single one a **knob** informally.
> This is a **declarative CSS custom-property** surface — there is **no** JS/Java call API (that
> role belongs to `MarbleBrand` / `MarbleDensity`).

Marble's tokens recolor the *whole* theme coherently (see [brand-override.md](brand-override.md))
and resize it coherently (see [data-dense-mode.md](data-dense-mode.md)). But enterprise
adopters also routinely need to restyle **one component** — "make *our* buttons pill-shaped",
"give the grid header our brand tint" — without forking the theme or fighting it with
brittle `!important` overrides. Marble exposes this as **per-component theme variables**:
each themed component reads a small, curated set of `--zk-<comp>-*` custom properties. Set
them at `:root` (whole app) or on any container (one region) and only that component changes.
No forking, no build step, no recompilation.

This is the same mechanism the theme already uses internally for control *sizes*
(`tokens/_sizing.css`) and for the splitter family (`tokens/_splitter.css`); Component Theme
Variables generalize it to the **appearance** dimension (color / border / radius / state) and
document it as a public, supported surface.

## How it works

Knob defaults live in [`tokens/_component-theme.css`](../../zul/src/main/resources/web/zul/css/tokens/_component-theme.css),
declared **unlayered** at `:root`, and are bundled into `norm.css.dsp`:

```css
:root {
    --zk-button-bg:     var(--zk-color-primary);
    --zk-button-radius: var(--zk-shape-button);
    /* … */
}
```

Each component consumes the knob from inside `@layer zk-components`:

```css
@layer zk-components {
.z-button {
    background-color: var(--zk-button-bg);
    border-radius:    var(--zk-button-radius);
}
}
```

Because every default equals the value the component used before, **declaring nothing
changes nothing** — Component Theme Variables are purely additive and render-neutral until an
adopter opts in.

### Where knob defaults live — centralized, not per-component

All knob defaults are declared in the single central file `tokens/_component-theme.css`,
never inside a component's own CSS. This follows the established project convention: the
stock ZK theme (the pre-11.0 IceBlue LESS sources, ZK 10.x; removed in ZK 11) declares **all** of its
`--zk-*` custom properties — global *and* component-specific alike — in one central `:root`
profile (`zul/less/profiles/_default.less`, with `_compact.less` for density) and **zero**
inside any component `.less`. Marble keeps that discipline, only splitting the central defs
by category (`_colors.css`, `_sizing.css`, `_splitter.css`, …); the component appearance
knobs are one more such file — the sibling of `_sizing.css`, which already centralizes every
component's *size* knob in one file. Authoring rule 1 is the other reason: a default declared
on the component element would shadow `:root`/region overrides for that element's subtree.

> A component *may* still declare a `--zk-<comp>-*` on a **variant/modifier** selector to
> re-parameterize its base rule — e.g. `.z-chip-info { --zk-chip-bg: … }`,
> `.z-avatar-small { --zk-avatar-size: … }`. That is a private *variant* mechanism, not an
> adopter-facing default, and is overridable only per-instance (inline) — not the whole-app +
> region contract Component Theme Variables define. To bring such a component fully in, hoist its
> base default into `tokens/_component-theme.css`.

### Listing a component's variables

This document does not repeat the per-component variable lists; the CSS file is the source of
truth. To list the variables of one component, run from the repository root:

```sh
grep -o -- '--zk-<comp>-[a-z0-9-]*[a-z0-9]' zul/src/main/resources/web/zul/css/tokens/_component-theme.css | sort -u
```

Replace `<comp>` with the component name (for example `button`). The output is the appearance
knobs declared in the central file. Size knobs are declared in `zul/src/main/resources/web/zul/css/tokens/_sizing.css`
and are not listed by this command. EE (`zkmax`, `zkex`) knob defaults are declared in the same
central file, not in a separate EE file; the EE component CSS files that mention the central file
(`zkex` rangeslider, `zkmax` multislider, under `../zkcml`) say so in their comments.

## Overriding

### Whole app — at `:root`

```css
/* adopter stylesheet, loaded AFTER the Marble bundle */
:root {
    --zk-button-radius: 9999px;   /* every button becomes a pill */
    --zk-button-bg: #6750a4;
}
```

### One region — on any container

Custom properties inherit, so setting a knob on an ancestor restyles only that subtree.
This is the common case and needs no special load order:

```xml
<div style="--zk-button-radius:9999px; --zk-button-bg:#6750a4; --zk-button-fg:#fff">
    <button label="Pill"/>          <!-- restyled -->
</div>
<button label="Default"/>            <!-- untouched — sibling, not a descendant -->
```

There is no dedicated demo page. Each component's own preview page (for example `button.zul`) shows its
default look, and the knob contract is regression-tested on those pages in
[`component-theme-variables.spec.ts`](../../zkthemelab/src/test/playwright/component-theme-variables.spec.ts).

## Cascade & load-order rules (important)

The knob defaults are **unlayered**, and components consume them from `@layer zk-components`.
Since unlayered rules always beat layered ones, the mechanics are:

- **Regional override (on an ancestor element)** always wins by *inheritance distance* — the
  nearest ancestor that sets the property governs its subtree, regardless of cascade layer or
  source order. No load-order concern.
- **Whole-app override (at `:root`)** competes with the theme's own `:root` default on the
  *same* element. Both are unlayered with equal specificity (0,1,0), so **source order
  decides** — the adopter's stylesheet must load **after** `norm.css.dsp`, **or** use a
  slightly higher-specificity selector. The theme's own brand presets already rely on this:
  `:root[data-brand="…"]` (0,2,0) beats the base `:root` (0,1,0). `html:root { … }` works too.

## Authoring rules

1. **Declare knobs only at `:root` (or an override scope) — never on the component element.**
   A `.z-button { --zk-button-bg: … }` declaration would *shadow* any `:root` or region
   override for the button's own subtree. (Same trap `tokens/_sizing.css` layer-3 documents.)
2. **Every default must equal the current value** — the exact token or literal the component
   used before — so adopting the knob is zero visual regression.
3. **Consume without a fallback** (`var(--zk-button-bg)`, not `var(--zk-button-bg, …)`): the
   default is defined once, at `:root`, keeping the knob discoverable in DevTools and avoiding
   a fallback that could drift from the `:root` default.
4. **Curate the surface.** Expose the themeable essentials (fill / text / border / radius / key
   states / size) — not every property. A knob is a supported contract; keep the set small.
5. **Typography stays global — it is not a component knob.** Font size / weight / line-height /
   family remain on the shared `--zk-typescale-*` scale. A component consuming
   `--zk-typescale-title-medium-*` (as `window` does for its title) is *correct*, not a gap:
   per-component type fragments the system's vertical rhythm, and every mainstream framework keeps
   typography a system token (MUI components reference typography *variants*; Ant font derives from
   global aliases; Chakra / Tailwind / Bootstrap use a shared scale). If a real need arises to
   restyle **one** component's type in isolation, expose a knob that *defaults to the typescale
   slot* — `--zk-window-title-size: var(--zk-typescale-title-medium-size)`, never a literal like
   `16px` — and do not add it speculatively. Rationale + framework survey:
   [component-theme-typography-scope.md](../component-theme-typography-scope.md).

### Relationship to brand override & density

| Want to… | Use |
|----------|-----|
| Recolor the **whole theme** from one seed | `--zk-color-primary` (+ role seeds) — [brand-override.md](brand-override.md) |
| Resize the **whole theme / a region** | `data-density` + `--zk-*-height` — [data-dense-mode.md](data-dense-mode.md) |
| Restyle **one component's appearance** | this feature's `--zk-<comp>-*` variables |

> **Freeze caveat.** Overriding an upstream *global* seed (e.g. `--zk-color-primary`) on a
> *region* will **not** reflow a knob whose default is `var(--zk-color-primary)` — that value
> was substituted at `:root` and inherited frozen. To recolor a region, override the component
> **knob** directly; to recolor the whole app, override the seed at `:root`.

## Exceptions

These are the places where a component departs from the conventions above. Each rule gives the
reason recorded in the source. Where the source records no reason, the rule says so. Components
are named here; their individual knobs are in `_component-theme.css`, not in this document.

### Reparented on open: CTV-3 does not apply

**Rule.** For these components a region override on an ancestor does not reach the open element,
so CTV-3 (region scoping) is not applicable. Only the whole-app `:root` path (CTV-4) is exercised.

**Reason.** ZK moves the open element out of the subtree of the element that triggered it.

- Whole root moved to `document.body` on open: **drawer** (`setVisible()` calls `zk.makeVParent()`)
  and **coachmark** (`_open()` calls `makeVParent()` on the root). Both confirmed in the compiled
  zkmax 10.4 widget bundle. No in-place part is left to test region scoping on.
- Inherited from `Popup`: **confirmpopup** (its `open()` calls `super.open()`, which calls
  `makeVParent()`; confirmed in `Popup.ts` and `Confirmpopup.ts`).
- Created under the page root: **messagebox**. `Messagebox.show()` creates the dialog under the
  desktop's first page, so the dialog is never a descendant of the triggering container
  (verified empirically).
- Modal dialog: **fileupload** (the `Fileupload.get` dialog opens with `doModal()`, which makes
  Window call `makeVParent()`).

### Only the popup moves; the field stays in place

**Rule.** Region scoping is asserted on the trigger field, which stays in place. The popup's own
knobs (popup background, popup radius, and the items inside the popup) are declared and consumed
but are not individually asserted for region.

**Reason.** The popup is detached from the trigger while it is open. The searchbox entry says
these popup knobs get "the existing treatment of the other wrapper-border family members' popup
knobs".

**Affected.** combobox, datebox, bandbox, daterangebox, timepicker, chosenbox, cascader, searchbox,
colorbox. Timebox and spinner have no popup. A calendar inside a datebox popup is flattened back to
flat content by a `:not()` guard in `calendar.css`, so the calendar knobs do not reach it.

### Field border model: wrapper or split

**Rule.** Most dropdown-input fields put the border on the root wrapper, and focus is an inset
ring. Combobox uses a split border: the input and the button each carry half of the border, and
focus grows the border from 1px to 2px.

**Reason.** This follows the DOM structure; the knobs are the same and only the plumbing differs.
The inset-ring focus is described in `.claude/skills/marble-theme/reference/focus-affordance-no-layout-shift.md`.

The codeeditor is the exception to the ring. Its focus is an overlay ring, not an inset box-shadow,
because the root has no padding to absorb a 1px→2px border change, and CodeMirror's DOM is flush
against the border and paints opaque.

### Variants keep their own semantic color

**Rule.** A variant, mold or severity that sets its own colors on the element at higher
specificity does not read the base knobs. It keeps its semantic color under any base override.

**Reason.** The variant's color carries meaning (status, severity or context). The entries say
this for progressmeter ("keep their own semantic colors") and badge ("keep their semantic color");
notification and toast state that their warning and error variants are unaffected by an override
(CTV-7).

**Affected.**
- button: color, outlined, text, icon and FAB variants. The disabled treatment is also not
  knob-driven.
- progressmeter: secondary, success, warning and error variants. Only the default bar responds to
  an override.
- combobutton: toolbar mold.
- toolbar and toolbarbutton: app-bar context (under `.z-north`). The toolbar's `.z-toolbar-tabs`
  variant (transparent, no border) also stays on base tokens. The entry records no reason for the
  tabs variant beyond that.
- slider: sphere mold thumb gradient.
- notification and toast: warning and error variants.
- messagebox: icon colors for information, exclamation, error and question.
- confirmpopup: severity recolors the icon only.
- chip and badge: non-default severities (see the next group).

### Severity class on the element

**Rule.** When ZK puts a severity class on the element itself, the severity rules set the base
knobs on that same element. That shadows a `:root` or region override for the element's subtree.

- **Chip.** ZK stamps a default severity class on every chip, so the base knobs apply only to a
  chip with no severity class. A chip is themed per severity or inline, not by region.
  Base defaults were hoisted (no inline fallback) for consistency.
- **Badge.** The design here is deliberate. ZK always stamps a severity class (default `info`) and
  the indicator is a child of it, so a per-severity rule on the indicator would shadow any inherited
  value. The default `info` fill is therefore routed through the base knob with no info-specific
  rule, so a default badge can be overridden by region or inline. The non-default severities pin
  the fill per variant. The foreground and radius knobs have no per-severity override and apply to
  every badge. The dot-mode radius stays hardcoded.

### Defaults on the element are hoisted

**Rule.** Base defaults belong at `:root` in `_component-theme.css`, not on the component element
(authoring rule 1).

**Reason.** A default on the element shadows region and whole-app overrides for that element's
subtree.

**Affected.** avatar and avatar-group (ZK-6097): the base defaults were on `.z-avatar` and have been
hoisted; the size variants (`.z-avatar-small`, `.z-avatar-large`) still set size and font per
variant. chip and badge: see the previous group.

### Mold- and type-limited coverage

**Rule.** A knob covers only the mold or type named here. Other molds or types read base tokens
directly or keep their own colors.

- **checkbox:** default mold only. The switch and toggle molds have a distinct visual treatment
  with a different DOM and state model, and read base tokens directly. The entry calls them "a
  natural follow-on candidate". Radio shares checkbox's CSS file but is a separate widget with its
  own knobs.
- **notification:** info (default) variant and untyped card only. Warning and error variants pin
  their own colors. `Notification.show()` turns a `null` type into `info`, so an untyped card is
  reachable only through `Clients.showNotification()` with no type.
- **toast:** info (default) variant only. `Toast.show()` always defaults a `null` type to `info`,
  so there is no untyped card. Warning and error variants pin their own colors.
- **doublespinner:** shares the spinner ruleset and reads the spinner knobs; it has no knobs of its own.
- **paging:** the jump-to-page input renders only in the default mold; the numbered page buttons and
  the current-page indicator render only in the `os` mold. The Playwright test reads the `os` mold,
  so the jump-input knobs are declared but not tested. The jump input's border radius stays on the
  extra-small shape token rather than a knob; the entry calls this "a defensible curation choice …
  noted here rather than fixed".

### Native elements instead of the button widget

**Rule.** confirmpopup's OK and Cancel are plain native `<button>` elements, not `.z-button` widgets.
They therefore get their own knobs rather than reusing the button knobs.

**Reason.** They are not `.z-button` widgets (the entry's stated reason).

### Accent knobs follow whether a glyph is painted

**Rule.** An accent color is exposed only where the component paints it.

- **notification:** no accent knob. The left accent stripe was removed for MD3 fidelity (alerts
  carry severity through the leading icon and the container tint). An untyped notification renders
  no icon glyph, so an accent would paint nothing.
- **toast:** keeps its accent knob for the opposite reason: a toast always has a type and therefore
  always renders the icon the knob colors.

### No text, background, border or radius knob where nothing is painted

**Rule.** A component with no text, no painted card, or only an icon has no knob for that axis.

- **No background, because the root is transparent by design:** stepbar (it sits on the embedding
  page's own surface; `doc/contracts/stepbar.md` s5), fisheyebar (a floating dock overlay, not a
  card), and the portallayout root and column (a layout shell; only the optional frame is carded).
- **No background, border or radius, because the component is text chrome:** anchor (`a`), which is
  nevertheless exposed for its text color; breadcrumb (borderless inline text, no card surface);
  anchornav (paints no fill or border of its own).
- **Wraps other content:** cropper root. It wraps an opaque image and paints no background of its
  own, so only its border color and radius are exposed.
- **Icon-only:** rating. Only the glyph fill is exposed.
- **Borderless by default:** panel renders borderless (`.z-panel-noborder`), so its border color paints
  only in a bordered mold.
- **No radius, by design:** toolbar (a full-width chrome bar, same treatment as menubar) and drawer
  (the panel renders edge-to-edge, with no border-radius).
- **No text color:** colorbox (the root shows only a swatch and a caret icon); signature (a canvas
  and icon-only buttons); dropupload (the `content` attribute is arbitrary author HTML that ZK never
  repaints).

### No hover, focus or disabled knob where the state does not exist

**Rule.** A component with no hover state has no hover knob. Affected: tbeditor (its wrapper has
only resting and focus-within states), signature (the same), and cropper (a resting border only, no
interactive hover).

- **dropupload:** no hover, focus or disabled knob, because ZK never emits a drag-over or disabled
  class. Drag feedback is the native browser cursor only (verified against `Dropupload.ts` and
  `.java`).
- **colorbox:** has resting and hover states but no focus or open border state, so no focus knob.
  Its disabled state keeps the resting outline literal rather than the hover knob, the same
  treatment as datebox, timebox and timepicker.

### Focus ring stays global

**Rule.** A component whose focus uses the global focus ring has no focus knob. Affected: button,
window, grid, inputgroup, organigram, the two confirmpopup footer buttons, breadcrumb and anchor.
The wrapper-border fields are the opposite: their focus knob tints the ring as well.

**Reason.** The entries cite only the shared convention with button, window and grid. No further
reason is recorded.

### Disabled and readonly states are not knob-driven

**Rule.** Disabled is shown by opacity, or by the component's own surface token or literal, and is
not knob-driven. Readonly is not knob-driven either: the input family uses a muted outline treatment,
and rating's readonly disables interaction only, at full opacity.

**Reason.** CTV-7 (state integrity). The organigram entry states the reason directly: its disabled
background stays on the base surface token, so an override of the background is not inherited into
the disabled state, where it would lose its state distinction. The other entries cite the same
convention (button, input, rating) and record no further reason.

**Affected.** Nearly every family. Components that use their own treatment: chosenbox (its own
surface token), navitem (its own disabled literal), colorbox (resting outline literal), codeeditor
(the shared disabled-opacity token).

### Error and invalid states

**Rule.** In the dropdown-input family (datebox, timebox, timepicker, daterangebox), the invalid
state stays on the error color token and is not a knob. Stepbar's error state is the same.

**Reason.** The entries cite the same convention and record no further reason.

The text-field (input) family is the exception: it exposes an error border-color knob
(`--zk-input-border-color-error`).

### Literal and non-token defaults, with their reasons

**Rule.** These defaults are literals, or values that are not theme tokens, on purpose.

- **Zero regression:** the stock hover literal is kept in grid and biglistbox "for zero regression".
  The same convention applies to organigram's hover fill and combobox's item hover; those entries
  record no separate reason.
- **No token to derive from:** the carousel arrow's hover darkening, a hand-picked darkening of the
  scrim.
- **Glyph inside a data URI:** the selectbox chevron and the checkbox check and dash glyphs. A custom
  property cannot be interpolated inside a `url()` string, so their color stays hardcoded.
- **Reads only against a filled background:** the combobutton divider. It is kept as a literal and
  not promoted to a token.
- **Visible over imagery:** the carousel overlay chrome. Its light voice is brand-independent, not
  the on-primary token, because a light-primary rebrand would flip on-primary dark over arbitrary
  slide imagery. The exception is the active indicator pill: it is the one overlay part that uses
  brand tokens (primary fill, on-primary ring), so it reads on light slides. Inactive dots keep the
  scrim and inverse-on-surface voices. The cropper's selection geometry stays literal white, an
  intentional Jcrop visibility convention over a dimmed photo.
- **Contrast:** the codeeditor dark surface (see the next group).
- **Badge colors:** the info fill is a bright tone derived from the (deliberately dark) info token,
  so it sits with the four bright `status-*` siblings. The foreground is dark, not white, because
  every badge fill is a bright tone and MD3 pairs a light tone with a dark on-color.

### Codeeditor dark surface (measured 2026-09-08)

**Rule.** The dark surface is a literal near-black, not a theme token. The dark border and focus
pairs are separate from the light ones.

**Reason (measured 2026-09-08).**
- **Surface:** all twelve foregrounds clear WCAG AA on `#1e1e1e` (worst 4.52:1). Five fail on
  `#2d3748` (keyword/tag 4.07, comment 3.60, meta 3.39, gutter 3.25). The syntax colors are not
  theme-derived, so the surface has to keep them legible. Contrast cannot key off theme tokens here.
- **Border:** the light border pair is black-alpha. Over the opaque `#1e1e1e` fill it collapses onto
  the fill: rest `rgb(23,23,23)` and hover `rgb(4,4,4)`, a 1.14:1 change, so dark mode had no visible
  hover affordance. MD3 Gate 2 caught it. The dark pair is light-alpha, which mirrors the light
  surface and clears the 3:1 UI-state bar at 3.17:1 (`rgb(82,82,82)` to `rgb(165,165,165)`), without
  a white halo around the box.
- **Focus:** the dark focus color is primary at tone 80 (MD3 places primary at tone 80 on a dark
  scheme), derived by absolute OKLCH lightness so a brand override stays hue-consistent. The light
  focus color measured 3.45:1 on the fill and was reported unreadable in design review
  (2026-09-08); the dark focus color measures 8.45:1.
- **Re-assertion:** the dark rules re-assert focus after the dark hover rule, because the shared
  `:focus-within` rule sits earlier in the file at equal specificity.

**Generalizable.** Any component with a per-widget dark-surface modifier inherits this trap: a
shared alpha border can vanish on an opaque dark fill, and focus can lose to an earlier rule at equal
specificity.

### Naming exceptions: fileupload and codeeditor

- **fileupload:** the names follow the IceBlue LESS variables that ZK-6199 introduced (`-background-color`
  and `-color`, not Marble's `-bg` and `-fg`), so one override restyles the rows in either theme. The
  names are not changed to match Marble's convention.
- **codeeditor:** two families that must not be made uniform. The field-style knobs follow the
  ordinary field convention. Three names keep the spelling ZK core authored, because ZK core documents
  them as public API (the componentreference page tells authors to set them per element), so they do
  not become `-bg` / `-fg`. Two ZK-core hooks are deliberately not declared. The active-line color is
  not declared because the CE extension set never enables `highlightActiveLine`, so the class is never
  emitted and a default would be an orphan. The token colors are not declared because they live in
  the widget JavaScript as `var()` fallbacks (VS Code Dark+ literals), so a default here would duplicate
  a ZK core value with no CSS consumer. The orphan-token rule counts only component CSS as a consumer.

### Shared border and color roles ("one knob, several roles")

**Rule.** One knob may drive several roles when those roles read the same token today. Overriding
that knob changes all of them together. Affected:

- **Border, several edges:** organigram (node card border and all connector segments); goldenlayout
  (header bottom divider and panel border); toolbar (bar edge and separator); messagebox (header and
  button-row dividers); confirmpopup (arrow outer edge and both dividers); pdfviewer (root border and
  toolbar separator); biglistbox (root, header and row bottom borders); tab (every tab-component
  border); inputgroup (addon border and grouped input border, so the group reads as one continuous
  outline); stepbar (both connector halves).
- **Color, several roles:** window and messagebox (header shares the surface fill); chosenbox (input
  text and chip text); cascader (trigger text and item text); searchbox (four text roles); nav (group
  header and leaf link text); breadcrumb (separator, ellipsis and link text); coachmark (card fill and
  all four pointer borders); confirmpopup (card fill and arrow fill); tab, calendar, slider,
  rangeslider, multislider, goldenlayout and anchornav (one accent for several roles); checkbox
  (checked and indeterminate fill, border and hover ring).
- **Shape, shared:** carousel (frame and caption chip, under the DESIGN.md §5 container/control
  rule); confirmpopup (both footer buttons); the slider family (track, fill and thumb); daterangebox
  and timepicker (the trigger button's corners reuse the field's radius knob, which caps one corner of
  the shared field); goldenlayout (header top corners and panel corners).

**Sub-parts with their own knob set.** A visually distinct sub-part gets its own knob set rather than
sharing the root's: pdfviewer (the floating toolbar and the canvas) and portallayout (the panel-count
badge, with its own fill, text and radius). The entries cite the "sub-part gets its own knob set"
precedent and record no separate reason.

**Reason.** The entries record that the roles read the same token today, so one knob keeps them in
lockstep (for example chosenbox: "one shared knob keeps them in lockstep"). The inputgroup entry gives
its reason directly: the whole group reads as one continuous outline.

**Defining states get their own pair.** A selected, current, checked or armed state has its own knob
pair, for example listbox and tree selected, chosenbox armed-for-delete, and the breadcrumb current
item. The entries cite the "one knob per defining state" precedent and record no separate reason.

### Elevation

**Rule.** Window's elevation follows its mode, so it is not a knob (`window-design-rules.md`).
Elevation is a knob only when it is static, meaning the same shadow in every mode or position. Static
elevation knobs exist for: button, popup, panel, groupbox, drawer (every direction), messagebox
(always modal), confirmpopup, coachmark, the goldenlayout panel, tbeditor, and the thumbs of the slider
family. The pressed or active-state shadow of the rangeslider and multislider thumbs stays on its own
elevation token and is not knob-driven; the entries record no reason.

**Reason.** Window's elevation is mode-driven by design. The static knobs are recorded as "not
mode-driven". Box-shadows on notification, toast, calendar and dropdown-input popups stay on base
tokens (see the curation group).

### Curation: secondary sub-features stay on base tokens

**Rule.** Minor sub-features are not knobs. Examples: popup borders and shadows; hover tints on
overlays (menu and menuitem use a shared `::before` overlay, and the calendar hover tints are
likewise not knob-driven); close and sort icons; separators, marks and tooltips; the navigation
badge; the calendar's week-of-year column; the footer buttons of a range popup; and transient surfaces
(drag ghosts, drop placeholders, the drag proxy, the drop-target indicator, and overflow dropdowns).

**Reason.** Authoring rule 4: keep the curated surface small. The entries cite the same convention,
"secondary sub-features kept out of the curated surface". The transient drag-feedback surfaces are
recorded as "transient" feedback.

**Affected.** Most families. Examples: calendar, toolbar, menu, slider, rangeslider, grid, biglistbox,
goldenlayout, portallayout, pdfviewer, tbeditor, nav, and the popups of the dropdown-input family.
Also: datebox's nested timezone `<select>` (base tokens), and the close-button icon colors of
notification and toast, which are not knob-driven (the toast entry cites the notification convention).

### State model sets the knob vocabulary

**Rule.** A component's knobs follow its state model. Overlay models (button, combobutton) expose an
overlay color and per-state opacities. Border-color models (the input family) expose a border color
per state. Background-swap models (window, grid) expose a background color per state. Toolbarbutton's
overlay reads `currentColor`, so its text knob also drives the tint and no separate overlay knob
exists.

**Reason.** The entries record that the knob vocabularies "differ by design because their state models
differ", and that the overlay color is separate from the fill only for button and combobutton.

### Stated without a reason in the source

- **drawer:** the backdrop mask uses a literal alpha of 0.32, distinct from the shared scrim token
  `--zk-color-scrim`. No reason is recorded.
- **tbeditor:** fullscreen mode resets the wrapper border, radius and box-shadow to `none`, and these
  are not knob-driven. No reason is recorded.
- **avatar and fileupload:** the avatar font-size knob and the fileupload font-family knob both
  default to the typography scale. Neither entry records a reason.
- **grid and biglistbox:** no data-cell text knob; the cell text stays on a base token. The entries
  record no reason beyond matching each other.

### Dormant and dead selectors (caveats, not knob rules)

- **`.z-toolbarseparator`:** no matching class exists in current ZK 10 core. `<separator bar="true">`
  renders `z-separator-horizontal-bar` / `-vertical-bar` instead. The toolbar border knob's reach to
  separators is therefore CSS-only. This is pre-existing dead code, unrelated to the knob, with zero
  regression either way.
- **`js/zul/sel/css/select.css`:** was a second `.z-select` block, reading the same knobs as listbox's,
  that no `css-uri` requested. It has been deleted; `listbox.css` is the only `.z-select` source.

### Geometry and fixed shapes are not knobs

- **Circles stay at 50%:** the radio circle, the badge dot mode, the rangeslider and multislider
  thumbs, and the stepbar circle. The entries call this "a fixed shape concern, not a knob" (radio,
  badge) and "a fixed shape constraint" (stepbar).
- **Only color is exposed for borders:** widths and styles stay literal. Tab's border width is
  fixed and only its color is exposed; anchornav's outline width and offset are literals; the
  coachmark pointer's 10px border width is fixed; the dropupload's 2px dashed border keeps its literal
  width and style (the same convention as the rest of the family).
- **Hover and selected states of fisheye:** none are exposed. Magnification is JS-driven (the widget
  sets inline width and height on mousemove), so there is no hover or selected color state to expose.
- **Hover geometry:** the carousel indicator's hover cue is `transform: scale(1.25)`. It is geometry
  rather than a color or opacity so that it works against either fill knob.
- **Carousel hit area:** the indicator's 24px hit area is a WCAG 2.1 SC 2.5.8 accessibility floor. It
  is not ladder-bound and is exempt from density scaling in both directions. The carousel arrow, by
  contrast, is density-bound through the `--zk-carousel-arrow-size` alias in `_sizing.css`.
- **Structural text-decoration:** anchornav's `text-decoration: none` is structural, not an appearance
  axis, so it is not exposed.

### Not exposed by design

- Purely structural or layout components (box, div, cell, separator, layouts) and content atoms
  (label, image) have no meaningful appearance knob. The anchor (`a`) is exposed despite its similarly
  minimal text-only vocabulary.
- Checkbox's switch and toggle molds are a candidate for a future pass if adopter demand appears. Add
  them through the Recipe below.

## Conformance criteria (檢驗條件)

The purpose in one line: *let an adopter restyle **one** component — whole-app or in one
region — by overriding **only that component's** `--zk-<comp>-*` variables, **never** a shared
global token, and with **zero regression** when they override nothing.*

A component **conforms** when it satisfies every applicable criterion below. CTV-1…4 are the
load-bearing *purpose* gates; CTV-5…9 are the mechanics / hygiene / verification that keep them true.

| ID | Criterion | How to check |
|----|-----------|--------------|
| **CTV-1** | **Zero-regression default.** Every `--zk-<comp>-*` default resolves to the component's exact pre-existing value; overriding nothing renders identically to before the variables existed. | Default in `_component-theme.css` equals the old token/literal; computed style unchanged before/after. |
| **CTV-2** | **Isolation from global tokens (core purpose).** The exposed appearance properties are read *through* the component's own variables, so an adopter restyles them by overriding only `--zk-<comp>-*` — **never** a global `--zk-color-*` / `--zk-shape-*` / …. No exposed property still reads a global token directly, bypassing the variable. | Overriding the component variable changes only this component; the global token it defaults to and other components are unaffected. |
| **CTV-3** | **Region scoping.** Setting a variable on a container restyles only that subtree's instances; siblings outside are unchanged. | Holds by CSS inheritance (a custom property set on an ancestor reaches only its descendants), so no test re-checks it. Documented exceptions (e.g. reparented popups and dialogs, severity-pinned chip/badge) are listed in the Exceptions section below. |
| **CTV-4** | **Whole-app override wins.** A `:root` override loaded after `norm.css.dsp` (or at higher specificity) wins for all instances. | Inject a `:root` override; assert it applies. |
| **CTV-5** | **Declared at `:root`, unlayered, not on the element.** Defaults live in `tokens/_component-theme.css` at `:root`; not declared on the base component element (which would shadow region overrides for its own subtree). Variant/modifier re-parameterization is allowed but must be documented. | Grep the component CSS: no base `.z-<comp> { --zk-<comp>-*: … }`; default present in `_component-theme.css`. |
| **CTV-6** | **Meaningful coverage.** The variables cover the component's salient appearance axes (as applicable: fill, text, border, radius, the defining state/accent) — enough for a recognizable restyle without forking. Size lives in `_sizing.css`; elevation may be intentionally mode-driven. | Review against the component's DOM + states; record deliberate exclusions. |
| **CTV-7** | **State integrity.** Disabled / readonly / selected / error treatments stay correct under an override (they don't accidentally inherit a fill/border variable and lose their state distinction) — unless a state is itself an intentional, documented variable. | Override the region; assert the disabled (etc.) instance keeps its treatment. |
| **CTV-8** | **Documented.** Every knob family has a comment in `zul/src/main/resources/web/zul/css/tokens/_component-theme.css` stating its scope and exceptions. | The comment exists for the component's knob block. |
| **CTV-9** | **Regression-tested.** A test in `component-theme-variables.spec.ts`, run on the component's own preview page, reads the stock value, injects a `:root` override after the theme bundle and reads again. That proves CTV-1/2/4/5: a hardcoded value or a default declared on the component element would shadow the inherited `:root` value and fail the second read. | Test exists and is green. |

> **What to assert in a CTV-9 test.** A sibling `not.toBe(override)` assertion is not worth writing:
> the override is set on a container and the sibling is not its descendant, so CSS inheritance makes
> the assertion true under any implementation. The checks that catch real mistakes are: (1) the
> scoped element reads the knob (`toBe(override value)`), which catches a hardcoded colour or a
> default declared on the component element; (2) the sibling still has the stock value
> (`toBe(stock value)`), which is the CTV-1 check. Floating components (Messagebox is attached at the
> page root, Popup is moved to `body` by `zk.makeVParent()`) cannot be reached by a regional
> override, so `:root` is the only way to test their knobs.

> CTV-6 and CTV-7 are judgment calls (what counts as "enough" coverage / a preserved state).
> The rationale for each component's coverage and preserved states is recorded in the comment on
> its knob block in `_component-theme.css`, not in a checkmark. Cross-component rules are in the
> Exceptions section above.

## Recipe — adding a component

Validated by the button pilot; repeat per component:

1. **Audit** the component CSS: which properties are worth exposing, and how are states
   implemented (overlay vs border-color vs background swap)? The knob names follow that model.
2. **Declare** the `--zk-<comp>-*` knobs at `:root` in `tokens/_component-theme.css`, each
   defaulting to the property's exact current value. Comment the knob block in
   `_component-theme.css`, stating its scope and exceptions (CTV-8).
3. **Refactor** the component CSS to consume `var(--zk-<comp>-knob)` in place of the direct
   token/literal — surgically, only the exposed properties, no behavior change.
4. **Build** with `node scripts/build-css.js --module zul` and confirm the knob appears in `norm.css.dsp`
   and is consumed in the component's `.css.dsp`. `node scripts/check-css-dsp.js --module zul` must
   exit 0.
5. **Test**: add the component to `component-theme-variables.spec.ts`, on the component's own preview page,
   and assert the knob contract (stock values unchanged, then a `:root` override wins). If the page
   does not render a state a knob needs (selected row, open popup), create it in the test with a real
   click or the client widget API.
