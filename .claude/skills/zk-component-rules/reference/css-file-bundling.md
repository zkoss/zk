# CSS file bundling

ZK serves component styles as `.css.dsp` files. Several `.css.dsp` files bundle **multiple components together**. Editing a bundle affects every component in it; a theme's verification must re-check every sibling after a change.

## Shared `.css.dsp` files

Which source files go into which bundle is defined by the theme's build script and by the `css-uri` entries in `lang.xml` / `lang-addon.xml`. A global bundle is served on every page by `zk.wcs`; a footer bundle loads last, so its rules win ties against component CSS. Read the build script's file lists to see which components share an output file (for Marble, see `marble-theme` `reference/css-dsp.md`).

A change to a shared bundle reaches every component in it. Every sibling must be re-checked after such a change, even if only one of them was originally failing.

### Bundling ≠ source file sharing

A common mistake: assuming components that share a `.css.dsp` output also share a source file. **They do not.** `combo.css.dsp` is a concatenation of independent source files, one per component (`combobox.css`, `bandbox.css`, `datebox.css`, `timebox.css`, `spinner.css` under `zul/src/main/resources/web/js/zul/inp/css/`; `spinner.css` covers spinner and doublespinner).

A rule written in `combobox.css` is delivered in the same `.css.dsp` as `datebox.css`, but it does NOT apply to datebox because the selectors are `.z-combobox-*`. A behavior all the combo-family components need (e.g. `buttonVisible="false"` hide rule) **must be added to each source file independently** — there is no shared file for "all combo-family inputs" to live in.

The shared-selector check (apply-to-all vs split, see below) only applies to selectors *already grouped* in one file. It does NOT mean a change in one file automatically reaches sibling files.

## One-to-one mapping (the simple case)

Components that are not part of a shared bundle each compile to their own `.css.dsp` file matching the source CSS filename. Example: `js/zul/wgt/css/button.css` → `button.css.dsp`. No sibling impact.

## Delivery quirks

Some component CSS files exist on disk but are **not delivered to the browser** because they are not registered as `<css>` entries in `lang-addon.xml` (or are merged into another bundle by the build script):

- **splitter** rules live in `js/zul/box/css/box.css`. `lang.xml` gives splitter no `css-uri`; it is styled because hbox / vbox load `box.css.dsp`. Edit `box.css` for splitter.
- A component CSS with no `css-uri` must be listed in a bundle in the theme's build script, otherwise it is generated but never requested. See `component-css-must-be-bundled-or-css-uri.md`.

If you write CSS for a component that does not visually change, check whether the CSS file is actually being loaded by the browser — open DevTools → Network → search for the `.css.dsp` filename.

## Shared selectors inside a bundle

Within `combo.css.dsp` and `input.css.dsp`, many rules use **grouped selectors** covering all sibling components:

```css
.z-textbox, .z-intbox, .z-decimalbox, .z-doublebox, .z-longbox, .z-passwordbox {
    /* base input styles */
}
```

When fixing a single component's check:

- **Apply to all** when the fix benefits every sibling (the expected value is identical across siblings).
- **Split** when only this component should change. Add a component-specific rule **after** the grouped rule (so it overrides), and leave the grouped rule alone — do not delete sibling selectors from it.
