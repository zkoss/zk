# zkpreview — the Marble preview host

A WAR that serves 162 ZUL pages against the **live `zk` / `zkcml` sources**, so you can see what
Marble actually renders. `settings.gradle` is a composite build that substitutes
`org.zkoss.zk:zul` (and its siblings) with the `:zul` project in `../../zk`, so the pages show your
working tree, not a published jar.

Marble needs no registration here — it is `zul`'s core theme, and `WEB-INF/zk.xml` sets no theme
property. To confirm which theme is being served, read the reset stylesheet href in the served
HTML: Marble carries **no theme segment** (`/zkres/web/<v>/zul/css/reset.css`) and precedes
`zk.wcs`.

## Start it

```bash
./start.sh            # http://127.0.0.1:8085
./start.sh 9090       # other port
```

The home page is `index.zul` — a link to every page below, generated from disk.

`start.sh` carries the three gotchas as comments (`appRun` not `appStart`; `127.0.0.1` not
`localhost`; first start is slow and there is no live reload). Read them before working around a
server that seems not to come up.

## The ways to reach a page

| Form | URL | Use it for |
|---|---|---|
| **Index** | `http://127.0.0.1:8085/` (or `/index.zul`) | The home page: a flat link list of all 162 pages, grouped by directory. Built by `PageIndexVM`, which scans `/web` per request, so a new page shows up without editing anything. |
| **Direct** | `http://127.0.0.1:8085/<page>.zul` | One component in isolation. The stable address — every Playwright spec uses this form. A filter forwards `/<page>.zul` to `/web/<page>.zul`. |
| **Use-case SPA** | `http://127.0.0.1:8085/usecase/index.zul#<bookmark>` | Browsing with the nav tree; deep-linking a screen. The bookmark is the target's path under the web root minus `.zul` — `#button`, `#usecase/ops-dashboard`, `#utility/colors`. Hash-based, so it survives redirects. |
| **All-on-one-page** | `http://127.0.0.1:8085/preview.zul` | Scanning every root-level page in one scroll. `ZulListVM` inlines all of them except `coachmark.zul`, which links out because it distorts the host page. |

`smoke.zul` is kept as the second welcome file and as a link on the index: five widgets that
prove the server and the theme came up, with nothing else to go wrong.

## Pages, by unit

### Use cases — whole screens (8)

Realistic compositions, the pages to look at when judging whether Marble *hangs together* rather
than whether one widget is correct. Under `web/usecase/`.

| Page | Nav label | What it exercises |
|---|---|---|
| `usecase/inventory-table.zul` | Inventory List | Dense data grid, toolbar, paging |
| `usecase/item-editor.zul` | Item Detail | Form-heavy detail view |
| `usecase/ticket-inbox.zul` | Support Ticket Inbox | List/detail split, status chips |
| `usecase/ops-dashboard.zul` | Operations Dashboard | Tiles, meters, mixed density |
| `usecase/onboarding-wizard.zul` | Onboarding Wizard | Stepbar-driven multi-step flow |
| `usecase/account-settings.zul` | Account Settings | Tabbed settings, grouped inputs |
| `usecase/sign-in.zul` | Sign-In | Centred single-purpose card |
| `usecase/brand-switcher.zul` | Brand Presets | Seed-colour override at runtime (`BrandSwitcherVM`) |

### Components — one widget per page (111 in the nav)

Each page shows one component across its states and variants. Widget ids are prefixed `pv-` so
specs can target them.

| Group | Pages |
|---|---|
| **Inputs** (30) | `bandbox` `button` `calendar` `cascader` `checkbox` `chosenbox` `codeeditor` `colorbox` `combobox` `combobutton` `datebox` `daterangebox` `decimalbox` `doublebox` `doublespinner` `inputgroup` `inputs` `intbox` `longbox` `multislider` `radiogroup` `rangeslider` `rating` `searchbox` `selectbox` `slider` `spinner` `textbox` `timebox` `timepicker` |
| **Data** (19) | `badge` `biglistbox` `chip` `dnd` `grid` `grid-detail` `grid-grouping` `grid-header` `grid-livegrouping` `grid-paging` `responsive-grid` `label` `listbox` `listbox-grouping` `listbox-header` `organigram` `paging` `tree` `tree-header` |
| **Navigation** (12) | `a` `anchornav` `breadcrumb` `coachmark` `drawer` `fisheyebar` `menubar` `navbar` `stepbar` `tabbox` `tabbox-misc` `toolbar` |
| **Containers** (5) | `caption` `groupbox` `panel` `popup` `window` |
| **Layout** (17) | `absolutelayout` `anchorlayout` `area` `borderlayout` `cardlayout` `columnlayout` `goldenlayout` `hlayout` `linelayout` `portallayout` `rowlayout` `scrollview` `space` `splitlayout` `splitter` `tablelayout` `vlayout` |
| **Feedback** (11) | `confirmpopup` `errorbox` `loading` `loadingbar` `messagebox` `notification` `progressmeter` `runtime-error` `scrollbar` `separator` `toast` |
| **Media & Upload** (17) | `audio` `avatar` `barcode` `barcodescanner` `camera` `captcha` `carousel` `cropper` `dropupload` `fileupload` `html` `iframe` `imagemap` `pdfviewer` `signature` `tbeditor` `video` |

Nav labels mostly match the filename; the ones that do not: Link → `a.zul`,
Drag & Drop → `dnd.zul`, Error → `runtime-error.zul`, Grid (Responsive) → `responsive-grid.zul`.

### Utility CSS — the token and utility-class catalogues (16)

Not components. These render the design system itself, and are what you check after touching a
token. Under `web/utility/`.

`colors` `typography` `icons` `spacing` `stack` `layout` `grid-layout` `responsive` `print`
`zindex` `borders` `elevation` `components` `opacity` `visibility` `interactions`

### Reachable only by direct URL — not in the SPA nav (4)

| Page | What it is |
|---|---|
| `icons-lucide.zul` | All 1947 Lucide icons with their `z-icon-<name>` class names |
| `overview.zul` | Older mixed-widget sampler, pre-dates the per-component pages |
| `preview.zul` | The all-on-one-page index itself |

### Supporting files, not pages to review

- `web/pv/*.zul` (22) — the **input snippets**: the popup content each input page loads
  (`bandbox-content.zul`, `combobox-content.zul`, …) plus `pv/matrix.zul`. Opening one directly
  shows a snippet out of context; that is expected. index.zul groups them under "Input snippets".
- `web/img/`, `web/media/video.mp4`, `web/link-annotation.pdf` — page assets.
- `src/main/java/zk/example/` — the models, composers and ViewModels the pages bind to
  (`UseCaseVM` drives the SPA's `@Init` restore, `navigate` command and `handleBookmarkChange`).
- `src/main/java/org/zkoss/zkpreview/http/` — `PreviewPathFilter` (the `/web` forward) and
  `ZKPreviewServlet`.
- `src/main/webapp/index.zul` + `PageIndexVM` — the home page link index. It lives at the webapp
  root next to `smoke.zul`, **not** under `/web`, so it is not itself one of the 162 pages.
  `PageIndexVM` scans the filesystem rather than carrying a curated list, so it cannot drift.

## Source of truth, and keeping this list honest

The tables above are a **snapshot for orientation**. Authoritative:

- **What pages exist** — the filesystem: `ls src/main/webapp/web/*.zul`
- **What the nav offers** — the `<navitem>` entries in `src/main/webapp/web/usecase/index.zul`

The root `index.zul` is the live, always-current answer to "what pages exist" — it reads the same
filesystem. The shell below is for when the server is not running, or to spot pages that exist but
have no nav entry:

```bash
cd src/main/webapp/web
ls *.zul | sed 's/\.zul$//' | sort > /tmp/all.txt
grep -o "page='~\./[a-z0-9-]*\.zul" usecase/index.zul | sed "s|page='~\./||;s|\.zul||" | sort -u > /tmp/innav.txt
comm -23 /tmp/all.txt /tmp/innav.txt      # pages with no nav entry
```

## Not covered here

This file is the page inventory only. For everything else, read the `marble-theme` skill:

| Question | Read |
|---|---|
| Launch details, ports, Playwright projects, screenshot baselines | `.claude/skills/marble-theme/reference/verification.md` |
| Rules for writing or fixing a preview page (utilities not page-local CSS, parse traps that look like 404s, `hflex` vs `flex-direction`) | `.claude/skills/marble-theme/reference/zul-authoring.md` |
| Tokens, `@layer`, `.css.dsp` wiring, brand overrides | the other files under `.claude/skills/marble-theme/reference/` |

Playwright specs live in `src/test/playwright/` (14 specs) and drive this app over the **direct**
URL form; `doc/focus-ring-known-clips.json` is their tracked allow-list.
