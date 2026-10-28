# CSS file bundling

ZK serves component styles as `.css.dsp` files. Several `.css.dsp` files bundle **multiple components together**. Editing a bundle affects every component in it; the verification harness must re-evaluate every sibling after a change.

## Shared `.css.dsp` files

| Output file | Components covered |
|-------------|--------------------|
| `combo.css.dsp` (zul/inp) | combobox, datebox, timebox, spinner, doublespinner, bandbox |
| `input.css.dsp` (zul/inp) | textbox, intbox, longbox, decimalbox, doublebox, passwordbox |
| `listbox.css.dsp` | listbox (zul); zkmax override exists for biglistbox |
| `box.css.dsp` (zul/box) | hlayout, vlayout, hbox, vbox, separator, splitter (see Delivery quirks below) |
| `footer.css.dsp` | toolbarbutton (despite the file name) |
| `norm.css.dsp` | All token files (tokens/*.css) + base utilities (base/*.css) |

A change to `combo.css.dsp` cascades to all 6 components in the input bundle. The harness's Generator must flag `RE_EVAL_NEEDED` for every sibling, and the Evaluator must re-check each one even if only one was originally failing.

### Bundling ≠ source file sharing

A common mistake: assuming components that share a `.css.dsp` output also share a source file. **They do not.** Inside `combo.css.dsp` the build concatenates six independent source files:

```
zul/src/main/resources/web/js/zul/inp/css/combobox.css
zul/src/main/resources/web/js/zul/inp/css/bandbox.css
zul/src/main/resources/web/js/zul/inp/css/datebox.css
zul/src/main/resources/web/js/zul/inp/css/timebox.css
zul/src/main/resources/web/js/zul/inp/css/spinner.css    (covers spinner + doublespinner)
```

A rule written in `combobox.css` is delivered in the same `.css.dsp` as `datebox.css`, but it does NOT apply to datebox because the selectors are `.z-combobox-*`. A behavior all six components need (e.g. `buttonVisible="false"` hide rule) **must be added to each source file independently** — there is no shared file for "all combo-family inputs" to live in.

The Generator's "shared-selector check" (apply-to-all vs split) only applies to selectors *already grouped* in one file. It does NOT mean a change in one file automatically reaches sibling files.

## One-to-one mapping (the simple case)

Components NOT listed above each compile to their own `.css.dsp` file matching the source CSS filename. Example: `js/zul/wgt/css/button.css` → `button.css.dsp`. No sibling impact.

## Delivery quirks

Some component CSS files exist on disk but are **not delivered to the browser** because they are not registered as `<css>` entries in `lang-addon.xml` (or are merged into another bundle by the build script):

- **splitter.css** is merged into `box.css.dsp` — there is no standalone `splitter.css.dsp` served. Edits to `js/zul/box/css/splitter.css` end up in `box.css`.
- **barcode.css, captcha.css, loadingbar.css, errorbox.css** generated but not registered. Theme builds that want to style these must register them in `lang-addon.xml` or merge them manually.

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

## Token files

Tokens live in `zul/src/main/resources/web/zul/css/tokens/_*.css` and bundle into `norm.css.dsp`. Component CSS should reference tokens via `var(--zk-*)`, never hard-code values. If a token's resolved value is wrong, fix the token file, not every component that uses it (token-rooted vs component-rooted distinction — see harness docs).
