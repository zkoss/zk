# box (hbox / vbox)

XUL-style layout containers. **The default mold is TABLE-based — not flex, not div.** This is the single most important fact about them.

## DOM structure (default mold — `zul/box/mold/hbox.js` / `vbox.js`)

```
table.z-hbox|.z-vbox                  (outer table — gets the widget class + zul width/height)
└─ tr
   └─ td#<uuid>-frame                  (inline width:100%;height:100% — hbox; width:100% — vbox)
      └─ table#<uuid>-real             (inline width/height:100% per pack/align; table-layout:fixed unless sizedByContent)
         ├─ hbox: one tr > one td#<childUuid>-chdex per child
         └─ vbox: one tr#<childUuid>-chdex > td per child
```

- Between every pair of children sits a separator (`td`/`tr` `#<childUuid>-chdex2`, class `z-hbox-separator`/`z-vbox-separator`) — rendered even when `spacing="0"` (then `display:none`). Splitter JS navigates siblings with `.prev().prev()` relying on it.
- A splitter child's chdex gets class `z-splitter-outer` (td in hbox, tr in vbox). **No other chdex class exists** — there is no `.z-hbox-cell` / `.z-vbox-cell` anywhere in ZK; any theme rule on such classes is dead code styling a hallucinated DOM (`_childOuterAttrs` emits only `z-splitter-outer` or `valign` attributes).

## NEVER change `display` on `.z-hbox` / `.z-vbox`

Stock ZK leaves the elements at native `display: table` (`box.less` only resets `border-spacing`/`td padding`/`background-clip`). Overriding to flex (or anything non-table):

1. The `tbody` (`display: table-row-group`) inside a non-table parent gets wrapped in an **anonymous table with auto width/height**, so the mold's inline `width/height: 100%` on td-frame and the `-real` table can no longer resolve → the content squashes to its natural size regardless of the zul `width`/`height` (measured 2026-06-06: `-real` table 68px tall inside a 300px vbox).
2. Splitter drag dies: `Splitter._doDragEndResize` persists a drag by writing inline px `width`/`height` on the **adjacent `<td>`s**, and `_snap` clamps the drag delta to `run.prev/next.offsetHeight|Width` — squashed rows make the clamp ≈ 0 and leave the written px sizes nothing to redistribute.
3. A pane sized `width="100%"`/`height="100%"` (e.g. a vbox inside an hbox) stops tracking its td, so JS-driven pane resizes never propagate to descendants.

Theme styling for hbox/vbox is limited to: table reset (`border-spacing: 0`, `td { padding: 0; background-clip: padding-box }`), separator default size (`z-hbox-separator` width / `z-vbox-separator` height — stock 0.3em; the `spacing` attribute overrides via inline style), and `z-vbox-separator td { line-height: 0 }` (the separator holds a spacer img).

## Splitter interplay

See `components/splitter.md` for the drag-persistence mechanism and `_fixsz` sizing (bar width:100% on vertical, px height from `clientHeight` on horizontal) — both depend on the table chain above.
