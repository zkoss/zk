# dropupload

An HTML5 drag-and-drop file-upload zone. A single widget (`<dropupload>`) that renders one box into which the user drops files; the drop starts the upload automatically. ZK-EE only (ships in `zkmax.jar`).

## DOM structure

```
.z-dropupload                         (<div>, root; the drop zone — the ONLY theme-stylable surface)
└─ (content)                          the `content` HTML, rendered as child node(s) of the root;
                                       shown/hidden by _setContentVisible(), never restyled
```

The `content` attribute is arbitrary author HTML (e.g. `<b>Drop Here</b><br/>size &lt; 5MB`). It is appended as children of the root and toggled visible/hidden — it is not wrapped in a themed element.

## State classes — there are NONE

CRITICAL: ZK `Dropupload` toggles **no** state class on itself. Verified against `Dropupload.ts` and `Dropupload.java` (zkmax): the strings `dropupload-active` and `dropupload-disabled` appear **nowhere** in the ZK source tree.

- **No drag-over / active class.** When a file is dragged over the zone, the only feedback is the native browser cursor: the widget sets `dataTransfer.dropEffect = 'copy'` (`_copyEffect` / `_stopAndCopyEffect` in `Dropupload.ts`). The component's own CSS does **not** change — there is no highlight, border swap, or background tint. Do **not** style `.z-dropupload-active`; ZK never adds it, so any such rule is dead CSS.
- **No disabled state.** `Dropupload.java` has no `setDisabled` (its setters are `maxsize`, `detection`, `viewerClass`, `content`, `native`, `anchor`, `maxFileCount`, `suppressedErrors`, `accept`). `disabled="true"` is unsupported and would error at parse time. Do **not** style `.z-dropupload-disabled`.

The theme's only stylable surface is the resting `.z-dropupload` box (border, radius, background).

## `detection` controls visibility, not appearance

`detection` (`none` | `browser` (default) | `self` | `<componentId>`) decides **when** the zone and its content are visible relative to the user's drag — it does not change how the box is styled. The `.z-dropupload` box looks identical in every mode; only visibility/timing differs:

- `none` — always visible; content always shown. Drag-over only sets the native copy cursor.
- `browser` (default) — root is `hide()`-den initially; appears when the user drags files into the browser window.
- `self` — visible initially, but the `content` children are hidden (`_setContentVisible(false)`) until a file is dragged over the component, then shown.
- `<componentId>` — like `self`, but the trigger area is the referenced component (used with `anchor` to overlay another panel, Gmail-style).

A theme therefore never needs a per-`detection` style variant.

## Why this matters for theming

A drop zone *looks* like it should have an MD3 drag-over highlight, so a theme author (or an MD3-driven harness) will be tempted to invent `.z-dropupload-active` / `.z-dropupload-disabled`. Resist it — ZK provides none of these hooks. Drag affordance is delegated entirely to the browser's native drag cursor. Style the resting box only.
