# Making Marble's Utility Classes Discoverable

**Question it answers:** how do a human in an IDE and an AI writing ZUL both find out which
`z-*` utility classes the *installed* version of Marble actually ships?

**Status:** Phase 1 implemented and verified. Phase 2's packaging is proven; its CLI is blocked
on D4. Phase 3 not started.

**Decisions so far:** D1 — build-time generation, not runtime CSS parsing. D2 — withdrawn, see
§4. D3, D4 open; D5 raised below.

---

## 1. Summary

Marble ships **492 authorable utility classes** across ten CSS files (2026-10-02; the 425 / nine
files cited in the verification records below were correct when measured, before commit
`03a5c2ad62` added the P1 utilities and `_surface.css` was split out). Until now the only
complete record was the CSS source itself. Prose documentation is not consulted, and a
hand-maintained list inside the ZK plugin would go stale at every release — the objection that
started this discussion.

**The approach: generate a machine-readable manifest at build time, ship it inside `zul.jar`,
and let every consumer read it from the artifact they already depend on.** One generator, three
consumers:

| Consumer | How it reads the list |
|---|---|
| ZK plugin (IDE completion) | reads the JSON entry from `zul.jar` on the project's classpath |
| AI agent / CLI user | `unzip -p zul.jar web/zul/css/utility-classes.json`, or a Java renderer |
| Humans / docs site | a generated Markdown page, from the same manifest |

The manifest is the single source; a CLI and a Markdown page are thin renderers over it. The
version problem disappears because the list travels **inside the versioned artifact** — the
plugin never has to know which ZK release a project uses, it reads whichever jar is there. A
running app also serves it over HTTP, since anything under `web/` is served by ZK.

**Utility classes should not become an XSD enumeration.** Three independent blockers, any one
of them fatal: `sclass` is space-separated and legitimately accepts arbitrary application
classes, so an enumeration would reject valid ZUL; `sclass` is declared once in a shared
attribute group used by every component; and `zul.xsd` describes *components*, which are
theme-independent, while utility classes are Marble's vocabulary — an app on another theme would
be offered completions for classes that do not exist. Details in §5.3.

### Prior art

| Project | Mechanism | The lesson |
|---|---|---|
| **Spring Boot** | annotation processor emits `META-INF/spring-configuration-metadata.json` into every jar; IDEs merge the files found on the classpath | The closest analogue, and JVM-native. Completion data is a build artifact that ships with the library, never a list inside the IDE plugin. |
| **Tailwind CSS IntelliSense** | resolves the project's *own* Tailwind install and enumerates the class names it generates | Editor tooling should ask the installed version, not carry its own copy. |
| **Custom Elements Manifest** | npm packages ship `custom-elements.json`; editor plugins and doc sites consume it | One generated descriptor, many consumers — exactly the split used here. |
| **VS Code Custom Data** (`html.customData`) | JSON contributing tags, attributes and **attribute value sets** to the built-in language service | If the plugin is ever VS Code-based, the manifest can be transformed into this format rather than hand-written. |
| **Bootstrap IDE extensions** | version-pinned static class lists (separate "Bootstrap 4" / "Bootstrap 5" extensions) | The failure mode being avoided: the list forks per version and rots. |
| **This repo: `doc/spec/icon-index.md`** | `scripts/build-css.js --emit-docs` generates the canonical `z-icon-*` lookup; agents consult it, `check-icon-coverage.sh` enforces it | The pattern already existed here for icons. This generalises it to utilities and additionally ships the data in the jar. |

---

## 2. Phases

### Phase 1 — Generate the manifest — **done**

`scripts/utility-manifest.js` builds the catalogue from the utility CSS sources;
`scripts/build-css.js` calls it as part of the normal CSS build, reusing the same file list it
already uses to assemble the bundle, so the catalogue can never describe a file the browser does
not load.

Each entry carries three fields — `name`, `category`, and the rule's declarations as a CSS
string. Four more appear only where they say something a consumer could not work out for itself:
`media` for a class that exists only under a breakpoint or in print, `selector` where the class
is not the whole selector, `inputs` for the custom properties the page author must supply, and
`description` once someone writes one. 57 of the 425 entries carry any of them. §5.7 records
what an earlier draft carried and why it was dropped.

Two independent extractors run over the same input and must agree, or the build fails: a
brace-matching text scan that yields declarations and at-rule preludes verbatim, and a
Lightning CSS AST walk used purely to cross-check the set of class names. §5.1 explains why
that guard is not ceremonial.

**Verified:** 425 classes, 39 KB; every one of them resolves to a rule in the built
`norm.css.dsp`; byte-identical across consecutive runs; `check-css-dsp.js` still reports full
coverage; both scripts lint clean.

### Phase 2 — Ship it — **done; the CLI is now proposed for removal (D4)**

The manifest is written to `zul/codegen/resources/web/zul/css/utility-classes.json`, the same
generated-resources tree `norm.css.dsp` goes to, which Gradle's `processResources` copies
wholesale into the jar. **Verified:** `zul-11.0.0-SNAPSHOT.jar` contains
`web/zul/css/utility-classes.json`, and it parses to 425 classes when read straight out of the
jar with no Java involved.

Living under `web/` rather than `META-INF/` was a deliberate choice: it needs no Gradle change,
it sits beside the CSS it describes, and ZK already serves that tree, so a running app exposes
the catalogue at `/zkau/web/zul/css/utility-classes.json` for free. §5.5 records the trade-off.

The CLI originally planned for this phase has not been written, and §5.9 argues it should not
be: every access pattern it was meant to serve is already one shell command or one HTTP GET
against the file that now ships. What replaces it is a documented one-liner, which costs no Java
and cannot go stale.

### Phase 3 — Wire up the consumers — not started

- **ZK plugin:** completion for `sclass` reads the manifest from the project's `zul.jar`, with
  the declarations as hover text (D3 settled that there is no `description` field). Falls back to
  a bundled copy when no ZK jar resolves.
- **Docs (planned; `doc/spec/utility-index.md` does not exist yet):** generate it from the manifest, as `icon-index.md` is
  generated today, and point the `marble-theme` skill's `zul-authoring.md` at it instead of at
  the raw CSS directory.
- **CI guard:** assert that every class in the manifest resolves in the built bundle. Note that
  the drift risk the original plan worried about does not exist: the manifest is generated into
  the gitignored `codegen/` tree on every build, never committed, so it cannot fall behind the
  CSS. The guard that is still worth having is the *coverage* one.
- **Breakpoint note:** the `_layout.css` part is done. Its comment now says the breakpoints follow
  the Bootstrap scale and no longer claims MUI/Tailwind alignment. The generated index page is not
  written yet; the MUI table it was to carry has been removed from §5.11, and the scale itself is
  stated in `doc/spec/responsive-design.md` §5 and the `_layout.css` comment.

### Phase 4 — The agent skill — **deferred until Marble itself is complete**

D6 chose Option A: ZK publishes a skill that resolves `zul.jar` on the project classpath and reads
`web/zul/css/utility-classes.json` out of it, so an agent working in a customer project is told the
utility set exists before it writes any markup. The `CLAUDE.md` snippet (Option B) ships with it as
the zero-setup fallback.

**This is deliberately last.** Building the skill is a separate piece of work from finishing the
theme, and a skill authored against a moving utility set would have to be re-verified every time the
CSS changes. Marble reaching completion is the gate: once the class list stops moving, the skill can
be written and checked once. Nothing else in this document depends on Phase 4 — the manifest, the
docs page and the plugin all land before it.

---

## 3. What is left

Phase 3, then Phase 4. The plugin work is outside this repository and can land independently,
because the manifest is a stable contract rather than an API. The docs page and the CI guard can be
done at any time; they read the manifest from the build output. Phase 4 does not start until Marble
is finished.

---

## 4. Open decisions

- **D2** — *withdrawn.* The proposal was a non-normative `xs:documentation` note on `sclass`
  pointing at the manifest. On inspection `zul.xsd` contains no `xs:annotation` or
  `xs:documentation` element at all, and neither does `zk.xsd`; the note would be the first in
  the file, would change no validation, and is rendered inconsistently across IDEs. It buys
  nothing that the manifest does not already provide.
- **D3** — *(decided: **(a) no description field**.)* The manifest carries `name`, `category` and
  `css` only. `padding: var(--zk-spacing-4)` explains `z-p-4` to a human and to an agent alike,
  and the handful of classes whose names are terms of art are cheaper to explain once in the
  generated index page than to carry as a field on 425 entries. **Consequence to absorb
  elsewhere:** the breakpoint suffixes were a known trap (§5.11), now resolved by aligning the scale
  with Bootstrap. With no `description`, the scale is stated in the `_layout.css` comments and in
  `doc/spec/responsive-design.md`, not in the manifest.
- **D4** — whether a Java CLI is needed at all, now that the manifest ships as a file.
  *Recommendation: drop it.* The jar entry is readable with `unzip -p` and a running app serves
  it over HTTP; a `main` in a public package would add an API surface to re-implement what `jq`
  does. See §5.9.
- **D5** *(new, raised by the implementation)* — whether the catalogue should also cover
  authorable classes that live in **component** CSS rather than the utility files. See §5.2.
- **D6** — *(decided: **Option A**, a skill ZK publishes, with the `CLAUDE.md` snippet as the
  zero-setup fallback — but **scheduled as Phase 4**, not to be started until Marble itself is
  complete.)* The question was how an AI agent learns the manifest exists at all: shipping the file
  guarantees the list is **correct**, but does nothing to make an agent **look**, and no agent
  unzips a jar on speculation. §5.10 records the five options and why A wins.

---

## 5. Technical appendix

### 5.1 Extraction must be at-rule-aware, and must exclude component classes

Three counts, all over the same nine files:

| Method | Count | Wrong how |
|---|---|---|
| `grep -E '^\.z-'` | 380 | misses every rule nested in an at-rule |
| indentation-tolerant grep | 437 | picks up `z-grid`, `z-listbox`, `z-tree` from compound selectors; still misses classes whose only rule carries a combinator |
| generated manifest | **425** | — |

**47 classes carry a condition** — the responsive variants (`z-d-sm-none`, `z-d-md-flex`,
`z-d-lg-block`, `z-d-xl-grid` and the `@container` equivalents) and the print family. They live
inside `@media` / `@container` blocks and are therefore indented, which is what the first method
misses. These are precisely the classes an author is least likely to remember, so a naive
extractor omits exactly the entries that justify the feature.

Six more classes have **no rule whose selector is the bare class**: `z-clearfix` is defined only
through `.z-clearfix::after`, and the five `z-vstack*` variants only through
`.z-vstack > * + *`. A "first compound is exactly one class" rule catches these while still
rejecting `.z-grid.z-sticky-header .z-grid-header`, where the class is a component modifier.

Finally, `_print.css` is not purely authorable — it also resets component chrome under
`@media print`. Fourteen component class names reach the utility files that way: `z-window`,
`z-panel`, `z-panel-move-block`, `z-panel-resize-faker`, `z-window-resize-faker`, `z-groupbox`,
`z-mask`, `z-modal-mask`, `z-error`, `z-grid-body`, `z-listbox-body`, `z-tree-body`,
`z-loadingbar-position`, `z-toast-position-wrapper`. Those rules apply on their own and are not
something an author writes in `sclass`. The generator resolves this by asking the component
stylesheets which names they own and subtracting them, which needs no hand-maintained list and
reports what it excluded in the build log. `z-paper` survives the filter correctly: it is defined
in `_components.css` and only *referenced* by the print reset.

`lightningcss@1.33.0` was already in `package.json`, so the cross-check added no dependency.

### 5.2 D5 — authorable classes outside the utility directory

`z-sticky-header` is an opt-in class an author puts on a Grid, Listbox or Tree. It is **not** in
the manifest, because it has no rule of its own in the utility files — it is defined in
`grid.css`, `listbox.css` and `tree.css` as `.z-grid.z-sticky-header`, and the utility files only
mention it to un-stick it for print. The same applies to the `*-noborder` opt-in sclasses those
files describe.

So "utility class" and "class in the utility directory" are not the same set. The open question
is whether the catalogue should be *authorable sclasses* — which would need those component-CSS
opt-ins added, most likely via an explicit marker in the component CSS rather than a heuristic —
or stay scoped to the utility files, with component-scoped opt-ins documented per component.
Scoping to the utility files is what is implemented, because it is deterministic.

### 5.3 Why not the XSD

`sclass` is declared once, as `xs:string`, in the `htmlBasedComponentAttrGroup` shared by every
HTML-based component.

1. **It is a space-separated list of arbitrary tokens.** Modelling it as an enumeration requires
   `xs:list` of a restricted type, which would then reject every application-defined class name.
   Widening it back to a union with `xs:string` restores validity but destroys the completion
   value, because the value space is open again.
2. **It is shared.** There is one declaration for all components, so the enumeration cannot be
   scoped or varied, and the change is all-or-nothing across the schema.
3. **It is the wrong layer.** `zul.xsd` describes components, which are theme-independent.
   Utility classes are Marble's vocabulary. An application on IceBlue or a custom corporate theme
   would be offered completions for classes its stylesheet never defines, and the schema would
   assert something false. This blocker survives any workaround for the first two.
4. **Editor support for enumeration completion in attributes varies**, and hover documentation
   via `xs:annotation` on each enumeration value is rendered by some IDEs and ignored by others.

The middle path in D2 — leave the type alone, attach an `xs:documentation` note pointing at the
manifest — is a complement to the manifest, not an alternative to it.

### 5.4 Size, and why consumers need tiers

| Form | Size | Rough tokens |
|---|---|---|
| full manifest, compact JSON | 39 KB | ~11k |
| class names only | 4.5 KB | ~1.5k |

An AI agent can afford the name list in context on every task; it cannot afford the full
manifest. So whatever reads the manifest should default to names and reach for detail per class
— which is one `jq` selector, not a program. The same split argues for the generated Markdown
page being organised by category rather than dumping every declaration.

### 5.5 Why `web/` and not `META-INF/`

`processResources` copies `zul/codegen/resources/` into the jar wholesale, and `build-css.js`
already writes to the `web` subtree of it. Putting the manifest at `web/zul/css/` therefore
needed no Gradle change and no new write helper, keeps it next to the CSS it describes, and
makes it reachable over HTTP from a running app because ZK serves `web/**`.

The cost is that the catalogue is publicly served. That is not a leak — the CSS it summarises is
already public — but if the team prefers it not be served, moving it to
`zul/codegen/resources/metainfo/zk/` is a small change: a second write helper rooted one level
up from `themeDir`, and the Gradle copy already covers the path.

### 5.6 Where things are

| Thing | Path |
|---|---|
| Utility CSS sources (9 files) | `zul/src/main/resources/web/zul/css/utility/_*.css` |
| Manifest generator | `scripts/utility-manifest.js` |
| Build integration | `scripts/build-css.js`, stage 1d, using the `utilityFiles` subset of `normFiles` |
| Generated manifest | `zul/codegen/resources/web/zul/css/utility-classes.json` (gitignored; rebuilt every build) |
| Jar entry | `web/zul/css/utility-classes.json` |
| Built bundle the browser loads | `zul/codegen/resources/web/zul/css/norm.css.dsp` |
| Theme provider (candidate home for the CLI) | `zul/src/main/java/org/zkoss/zul/theme/StandardThemeProvider.java` |
| `sclass` declaration | `zul/src/main/resources/metainfo/xml/zul.xsd`, `htmlBasedComponentAttrGroup` |
| Generated-catalogue precedent | `doc/spec/icon-index.md` |

All 425 classes are CE-only; `../zkcml/` contributes no utility CSS today. The manifest carries
a `module` field so zkmax or zkex can ship their own file later and a consumer can merge them,
as Spring Boot's IDE support merges per-jar metadata.

### 5.7 Manifest shape, and what it deliberately leaves out

```json
{
  "schemaVersion": 1, "theme": "marble", "module": "zul",
  "generatedBy": "scripts/build-css.js",
  "sources": ["zul/css/utility/_colors.css", "…"],
  "classes": [
    {"name": "z-p-4", "category": "spacing", "css": "padding: var(--zk-spacing-4)"},
    {"name": "z-d-sm-none", "category": "layout", "css": "display: none",
     "media": "(min-width: 576px)"},
    {"name": "z-vstack", "category": "stack", "css": "margin-top: var(--zk-spacing-3)",
     "selector": ".z-vstack > * + *"},
    {"name": "z-grid-cols-auto", "category": "layout",
     "css": "grid-template-columns: auto repeat(var(--zk-cols, 1), minmax(var(--zk-col-min, 0), 1fr))",
     "inputs": ["--zk-cols", "--zk-col-min"]}
  ]
}
```

`inputs` is the one field that genuinely cannot be derived from the CSS alone: telling a theme
token apart from a value the author must supply requires knowing which `--zk-*` properties the
theme defines. Those four classes plus `z-grid-fill-sm/lg/xl` are the only ones in the set driven
by an inline `style="--zk-cols: 3"`.

An earlier draft of this manifest was 123 KB and carried four more fields. They were dropped
after checking what each actually contributed:

| Dropped | Why |
|---|---|
| `source` | the source file is always `_<category>.css` — fully derivable |
| `tokens` | every `var(--zk-*)` the rule reads is already visible in `css` |
| `sets` | internal plumbing for computing `inputs`; 4 classes; no consumer needs it |
| `rules[]` with structured `declarations[{property,value}]` | consumers want a string for hover text; the structure bought nothing, and a parser can recover it |

Two shape simplifications came with it. `conditions` was an array but never held more than one
entry, so it is now a single `media` string. And twelve classes had more than one rule, but
eleven of those are the same selector under the same condition split across a shared authoring
block and a specific one (`.z-h1` twice, `.z-hstack` twice) — merging them loses nothing. The
twelfth is `z-paper`, whose second rule is a genuine `@media print` override; it is dropped,
because someone completing an `sclass` is choosing the class, not reading its print behaviour.
That is the one place the manifest is lossy, and `doc/spec/print-styles.md` covers it.

### 5.8 D3 — where a description would live, and what it is for

**What it is for.** The manifest's `name` + `css` already answers "what does `z-p-4` do":
`padding: var(--zk-spacing-4)`. A description earns its place only where the name and the
declarations do *not* convey the intent. That is a minority of the catalogue:

| Class | `css` alone says | What a description adds |
|---|---|---|
| `z-p-4` | `padding: var(--zk-spacing-4)` | nothing — skip it |
| `z-d-flex` | `display: flex` | nothing — skip it |
| `z-vstack` | `margin-top: var(--zk-spacing-3)` on `> * + *` | that it is the vertical-group idiom, and that `z-hstack` is its row counterpart |
| `z-clearfix` | `display: block; content: ''; clear: both` | when you would reach for it |
| `z-grid-cols-auto` | a `grid-template-columns` expression | that the author is expected to set `--zk-cols` |
| `z-visually-hidden` | six clipping declarations | that it hides content visually but keeps it for screen readers |

So the useful answer to "how many descriptions" is not 425. It is the handful of classes whose
name is a term of art.

**Where it would live: the CSS, not a side file.** The utility sources already carry group
comments — `/* Padding — all 4 sides */`, and a 15-line block explaining `.z-vstack`. Attributing
each comment block to the rules that follow it until the next block covers **425 of 425**
classes. Measured spot checks:

| Class | Harvested from the CSS comment |
|---|---|
| `z-p-4` | "Padding — all 4 sides" |
| `z-text-center` | "Text Alignment" |
| `z-vstack` | "Stack utilities — opt-in container spacing. Naming aligns with Bootstrap 5 / SwiftUI…" |
| `z-elevation-2` | "MD3 Utility Classes — Elevation shadows" *(file header — the weak case)* |

Two consequences. First, an overrides file would be the wrong home: it puts the prose somewhere
a person editing the CSS will not see, which is exactly how the current prose documentation rotted.
Keeping the description adjacent to the rule is what makes it survive a refactor. Second, the work
is not authoring 425 strings; it is improving roughly 39 comment blocks in files a theme developer
already edits, and teaching the generator to take the first sentence rather than the whole block.

Note that harvesting requires a change to `scripts/utility-manifest.js`, which currently calls
`stripComments()` before it scans. The comments would have to be captured first.

### 5.9 D4 — the CLI is redundant once the manifest is a file

The original plan asked for a runnable command because a hardcoded list in the IDE plugin would
need updating every release. **That requirement is already met**: the catalogue is generated at
build time and shipped inside the versioned jar, so it cannot describe a version other than the
one the reader has in hand. The CLI was the delivery mechanism for a guarantee that packaging now
provides on its own.

Every access pattern it was meant to serve already works with no Java:

| Reader | How they get the list today |
|---|---|
| Human at a shell | `unzip -p …/zul-11.0.0.jar web/zul/css/utility-classes.json \| jq -r '.classes[].name'` |
| AI agent | the same command — it needs a name list, and 4.5 KB of names is affordable in context |
| ZK plugin | reads the jar entry directly; an IDE plugin already resolves the project's classpath, and shelling out to `java` would be strictly worse |
| Running app | `GET /zkau/web/zul/css/utility-classes.json` — ZK already serves the `web/` tree |

Against that, a CLI costs a class in a public package whose `main` becomes a de facto supported
entry point, plus flag parsing and output formatting that duplicate `jq`.

The one thing a CLI would genuinely add is not having to know the jar's path. That is a
documentation problem: the fix is one line in `.claude/skills/marble-theme/reference/zul-authoring.md`
and in the generated index page, not a program.

*Recommendation: withdraw D4 and drop the CLI from the plan.* If the ergonomics still matter later,
the cheap version is a shell snippet in the docs, which can be corrected without a release.

### 5.10 D6 — discovery: an agent will not find this file on its own

Everything above solves *correctness*: the catalogue in the jar always matches the jar. It does
not solve *discovery*. An agent working inside a customer's ZK application sees ZUL files, Java
sources and a `pom.xml`. It does not unzip dependencies on speculation, and it cannot guess the
path `web/zul/css/utility-classes.json`. Without a pointer the manifest is never read, and the
original problem — "nobody consults the documentation" — survives intact in a new file format.

**Today the situation is worse than neutral.** `.claude/skills/marble-theme/reference/zul-authoring.md`
line 11 tells the agent to build `sclass` from
`zul/src/main/resources/web/zul/css/utility/*.css`. That path exists only inside this repository.
An agent in a customer project follows the instruction, finds nothing, and invents class names —
which is exactly the failure the utility set was meant to prevent.

So the pointer has to travel with something the customer's project already has. Four candidates:

| Channel | Reaches the agent? | Stays current? | Notes |
|---|---|---|---|
| A skill ZK publishes, installed in the customer project | yes — skill descriptions are matched automatically before the agent starts writing | yes, *if* the skill reads the jar rather than embedding a copy | strongest option; the precedent already exists |
| A snippet ZK documents for the customer's own `CLAUDE.md` / `AGENTS.md` | yes, once pasted | yes | zero engineering, but adoption is manual and invisible to us |
| The `zk-doc` MCP server | yes, when the agent thinks to search | yes | complements a skill; does not replace it, because it depends on the agent already suspecting utilities exist |
| The IDE plugin's `sclass` completion | **no** | yes | serves humans only — an agent writing a file never triggers completion |

**The precedent, and its flaw.** The `zul-writer` skill in this repo is already built for
distribution (MIT, "Designed for Claude Code, Gemini CLI, and GitHub Copilot/Cursor") and already
bundles a ZK metadata file: `assets/zul.xsd`, with a `--xsd` override and an
`http://www.zkoss.org/2005/zul/zul.xsd` fallback. That is a **snapshot**, and it carries exactly
the staleness problem this whole document exists to avoid — a bundled copy drifts from whatever
version the customer actually compiles against.

The manifest lets a skill do better. The instruction becomes "resolve `zul.jar` on the project's
classpath and read `web/zul/css/utility-classes.json` out of it", with a bundled copy used only
when no jar resolves. The agent then reads the list for the version in front of it, and the skill
never needs a release to stay accurate. That is the payoff the build-time generation was for;
without a channel like this, the manifest is a well-built file nobody opens.

**The constraint to accept first.** There is no option in which an agent finds the manifest with
nothing installed. An agent reads the files in front of it; the manifest is inside a jar. So every
option below is some answer to "what gets into the customer's project, and who puts it there."
They are not mutually exclusive — the decision is which one ZK funds first.

#### Option A — ZK publishes a skill *(recommended)*

A distributable skill whose description matches on "writing a ZUL page" / "styling with sclass",
so the agent loads it *before* it writes markup. The body tells it to resolve `zul.jar` on the
project classpath and read `web/zul/css/utility-classes.json` out of it, with a bundled snapshot
used only when no jar resolves.

- **Why it wins:** it is the only option that fires automatically, at the right moment, without
  the customer or the agent having to suspect utility classes exist.
- **Precedent:** `zul-writer` in this repo is already built for distribution (MIT, "Designed for
  Claude Code, Gemini CLI, and GitHub Copilot/Cursor") and already bundles `assets/zul.xsd` — but
  as a *snapshot*, which carries the staleness problem this document exists to remove. Reading the
  jar instead is the improvement the manifest makes possible.
- **Cost:** packaging and a distribution channel, plus keeping the skill's fallback copy fresh.
- **Limit:** skills are a convention of Claude Code / Cursor and similar tools, not a standard.
  The customer still has to install it.
- **Reading order (decided 2026-10-02, D1-A in
  utility-discovery-without-manifest.md, kept locally under `tasks/`, not in the repository):** the manifest
  is the list and the source CSS is the detail. The alternative — consumers parse the shipped CSS
  themselves, no manifest — was evaluated and rejected because every consumer would re-implement
  the extraction rules of §5.1/§5.12 and the CSS file layout would become public API. The skill
  therefore:
  1. reads the class names from `web/zul/css/utility-classes.json` in the resolved `zul.jar`;
  2. when a class's intent is unclear, reads the source file named in the manifest's `sources`
     field — `zul.jar` ships the unminified `web/zul/css/utility/_*.css` with their comments;
  3. falls back to reading `web/zul/css/utility/*.css` directly, excluding component classes and
     print-only non-`z-d-print-*` classes, only when the jar predates the manifest.

  This makes shipping the commented source partials in `zul.jar` a commitment: a change that
  strips them from the jar breaks step 2 and must revisit D1 there first.

#### Option B — a documented snippet for the customer's own `CLAUDE.md` / `AGENTS.md`

ZK publishes three or four lines the customer pastes into the instruction file their agent already
reads.

- **Cost:** essentially zero — one page of documentation.
- **Limit:** adoption is manual and we never find out who did it. Best treated as the zero-setup
  fallback that ships alongside Option A, not as the whole answer.

#### Option C — the project template writes the pointer

ZK's Maven archetype / new-project wizard generates an `AGENTS.md` carrying the pointer, so a new
ZK project is agent-ready on creation.

- **Cost:** a change to the archetype.
- **Limit:** helps new projects only, which is the wrong end of the install base for a feature
  motivated by existing customers asking for responsive support.

#### Option D — the ZK plugin maintains the pointer file

The IDE plugin already resolves the project classpath for `sclass` completion. It could also write
and refresh an `AGENTS.md` section in the project.

- **Why it is attractive:** it is the one option that serves the human and the agent from a single
  piece of engineering, and it stays correct across ZK upgrades without the customer acting.
- **Cost:** plugin work, plus a consent flow — writing into a customer's repository uninvited is
  not acceptable by default.

#### Option E — do nothing beyond the docs page

Accept that the manifest serves the IDE plugin's completion (humans) and that agents keep guessing.

- **Cost:** zero.
- **What it forfeits:** the AI half of the original motivation. Worth naming as the baseline so the
  other options are costed against something.

**Independent of the decision:** `zul-authoring.md` line 11 points at a path that exists only in
this repository. That is wrong for every reader outside it and should be fixed whichever option wins.
*Resolved 2026-10-02:* `marble-theme` is an in-repo maintenance skill, so the in-repo path is right
for its readers; what was wrong is that it sent them to grep the raw CSS for the list. It now names
the generated manifest as the list and the sources as detail. The customer-facing equivalent
belongs to the Phase 4 skill.

### 5.11 The breakpoint suffixes use the Bootstrap scale

`.z-d-{value}-{bp}` uses the Bootstrap breakpoint scale: `sm` 576px, `md` 768px, `lg` 992px,
`xl` 1200px (`xs` is the base range, 0). The scale is aligned with Bootstrap, and with the
`responsive` breakpoints of the EE Grid and Listbox, which use the same values. The rule, and the
build check that enforces it (`scripts/check-breakpoints.js`), are described in
`doc/spec/responsive-design.md` §5.

The suffixes originally followed the MUI scale (600px, 900px, 1200px, 1536px). A customer arriving
with Bootstrap advice who wrote `z-d-md-none` expecting 768px would have got 900px, with no error.
That is why earlier notes in this document describe a migration trap; the trap no longer applies.

### 5.12 Defect, now fixed — an EE component class leaked into the catalogue

`z-drawer` is in the generated manifest, categorised `print`. It is not a utility: it is the
zkmax **Drawer** component (`../zkcml/zkmax/src/main/resources/web/js/zkmax/wgt/css/drawer.css`),
hidden by the standard print reset in `_print.css` alongside `.z-modal-mask`, `.z-toast-position-wrapper`
and the rest.

`collectComponentClasses()` suppresses that whole family by asking the component CSS which names it
owns — but it only scans `zul`'s own `web/js/zul`. EE components live in a different repository, which
a CE build cannot read, so `z-drawer` was not caught. The catalogue advertised 426 classes when only
425 were authorable.

The fix could not be "scan zkcml too": CE must build without EE present. The rule applied instead is
that **a class whose every rule sits inside `@media print` is a utility only when it is named as one**
(`z-d-print-*`); anything else appearing only there is component chrome being reset. It runs as a
second pass in `buildUtilityManifest`, after accumulation, because a class such as `z-paper` is
defined unconditionally in `_components.css` *and* overridden under print — only the completed entry
shows that it has a non-print rule.

This is a naming-convention dependency rather than something derived from the CSS, which is the one
cost of the fix: a future print-only utility that does not carry the `z-d-print-` prefix would be
dropped silently. The build log names every exclusion, so it would be visible.

**Verified after the fix:** 425 classes, 15 component classes excluded; `z-drawer` gone; `z-paper`
retained with its `_components.css` declarations; all five `z-d-print-*` utilities retained; every
class still resolves in the built `norm.css.dsp`; output byte-identical across consecutive builds.
