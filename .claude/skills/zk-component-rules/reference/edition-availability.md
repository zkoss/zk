# Edition availability (CE / PE / EE)

ZK ships in three editions. CSS generated for an EE-only component does no harm in a CE build, but the component itself will not render. When designing a theme, decide which editions it targets — that constrains which preview pages can exist.

## Editions and their JARs

| Edition | JAR | Notes |
|---------|-----|-------|
| CE (Community Edition) | `zul.jar`, `zk.jar`, `zkbind.jar` | Open source |
| PE (Professional Edition) | adds `zkex.jar` | Paid; includes extra widgets |
| EE (Enterprise Edition) | adds `zkmax.jar` | Adds large-data and advanced widgets |

## Components by edition

### CE — everything in `zul.jar`

textbox, intbox, longbox, decimalbox, doublebox, passwordbox, combobox, bandbox, datebox, timebox, spinner, doublespinner, selectbox, checkbox, radio, button, slider, rating, label, listbox, tree, grid, tabbox, window, panel, popup, menubar, toolbar, splitter, separator, borderlayout, hlayout, vlayout, anchorlayout, hbox, vbox, captcha, audio, image, applet, iframe, html

### PE — adds `zkex.jar`

- **colorbox** — color picker
- **fisheyebar / fisheye** — fisheye navigation
- **rangeslider** — dual-thumb slider
- **portallayout / portalchildren** — drag-and-drop dashboard layout

### EE — adds `zkmax.jar`

- **chosenbox** — multi-select with chips and search
- **cascader** — hierarchical select (tree picker)
- **searchbox** — search input with model-backed dropdown
- **biglistbox** — virtual-scrolling listbox for large datasets
- **organigram** — organisation chart
- **timepicker** — alternative time entry
- **drawer** — side panel
- **anchornav** — anchor-based scroll navigation
- **coachmark** — guided-tour callouts
- **golden layout** — advanced dockable layout
- **multislider** — multi-thumb slider
- **scrollview** — touch-style horizontal scroller
- **signature** — signature canvas
- **passwordbox** (advanced features), **pdfviewer**, **camera**, **barcode**, **barcodescanner**, **cropper**, **imagemap**

## Where EE/PE overrides CE

When both CE and EE provide CSS for the same widget (rare but does happen for `listbox` and `tree`), the EE version may override:

- `js/zkmax/sel/css/listbox.css.dsp` overrides `js/zul/sel/css/listbox.css.dsp`
- `js/zkmax/sel/css/tree.css.dsp` overrides `js/zul/sel/css/tree.css.dsp`

If your theme targets EE, the EE file is the one that ships last and wins. Edit the EE file when EE is the target audience; edit the CE file when both editions must work.

## Implications for theme design

- **CE-only theme**: do not generate CSS for `chosenbox`, `cascader`, `colorbox`, etc. They will silently 404 if requested.
- **EE theme**: assume all components are available; preview pages can reference every component.
- **Preview pages**: a CE-only theme should not have `chosenbox.zul` / `cascader.zul` / `searchbox.zul` in its sidebar.

## Verification

To check which edition a given component requires, search the ZK source:

```bash
grep -r "<component-class>" /Users/hawk/Documents/workspace/ZK10/zk/{zul,zkex,zkmax}/src/main/resources/metainfo/zk/
```

The JAR-relative path tells you the edition.
