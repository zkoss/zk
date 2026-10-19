---
name: marble-theme
description: >-
  Everything needed to maintain Marble, the Material Design theme for ZK Framework that becomes
  the default look-and-feel in ZK 11.0. Use this whenever the work touches Marble's CSS, tokens
  or theme architecture: implementing or refining a component's styles; adding, renaming or
  auditing a `--zk-*` token; wiring a new `.css.dsp` output; a component rendering unstyled or
  a stylesheet 404-ing; brand-colour or seed overrides; compact/data-dense density; z-index and
  stacking questions; running the preview app or the Playwright visual-regression suite; filing a
  ZK bug found while theming; or asking how Marble's vocabulary relates to the older IceBlue
  theme. Also use it when someone asks "how does this theme get built", "where does this CSS
  end up", "which port is the preview app on", or "why is there an `!important` here" — even if
  they never say "Marble". Also covers auditing CSS hygiene — orphan tokens, hardcoded colours,
  duplicate shadows, or redundant `display` declarations — and removing, reducing or justifying
  an `!important`. Also use it to settle an appearance question — which style or token, whether
  sibling components should match, whether Marble, a ZK Jira or the preview page owns a defect —
  and to decide whether the question needs the owner at all.
---

# Maintaining Marble

Marble is a pure-CSS Material Design theme for ZK Framework, visually aligned with MUI v7 while
its tokens follow MD3 naming. No LESS, no preprocessor. It is slated to become the default
look-and-feel for **ZK 11.0** and has had no public release yet, so there is no backward
compatibility to preserve — but there *is* an installed base on the older IceBlue theme whose
vocabulary Marble does not share (see `reference/iceblue-parity.md`).

## The five rules that are never negotiable

1. **Every custom property is `--zk-*`.** `--md-sys-*` is banned, even though the token *values*
   follow MD3. Never invent a token family that does not already exist — in particular there are
   no `--zk-font-weight-*` tokens.
2. **No hardcoded colour.** Every colour comes through `var(--zk-color-*)`. A literal hex in
   component CSS is a defect.
3. **Cascade layers are assigned at build time**, in the order
   `zk-base < zk-components < zk-utilities`. Reset rules **must** land in `zk-base`.
   `build-css.js`'s `assertLayer()` enforces this and fails the build.
4. **Source is `.css`; the shipped artifact is `.css.dsp`.** ZK does not discover component CSS
   by convention — every file is named individually by a `<css-uri>` entry in `lang.xml`. See
   `reference/css-dsp.md` before adding, renaming or removing any file.
5. **Surgical changes.** Touch only what the task requires; match the surrounding style. When a
   theme-global invention collides with a ZK JS assumption, the first option to evaluate is
   *deleting the invention*, not engineering exceptions around it.

## Decide, don't ask

Before putting an appearance question to the owner — which colour or token, whether siblings
should match, who fixes a defect — read `reference/decision-principles.md`. Settle what it
settles, cite the principle IDs in the report, and ask only for the cases in its §4. After the
owner rules, fold the ruling back into that file (its §5).

## Where things live

CE (this checkout):

```
zul/src/main/resources/web/zul/css/
├── _layer-order.css   the one @layer order statement (first entry of norm.css.dsp)
├── tokens/  _colors _typography _spacing _sizing _shape _elevation
│            _motion _zindex _fonts _splitter _component-theme _forced-colors
├── base/    _reset _icons _cssflex _dnd
└── utility/ _colors _spacing _layout _typography _borders _elevation
             _surface _stack _print

zul/src/main/resources/web/js/zul/<pkg>/css/   → per-component CSS, 1:1 with a .css.dsp output
```

EE/PE (sibling checkout `../zkcml`):

```
../zkcml/zkmax/src/main/resources/web/js/zkmax/<pkg>/css/   → zkmax (EE)
../zkcml/zkex/src/main/resources/web/js/zkex/<pkg>/css/     → zkex (PE)
../zkcml/zkmax/src/main/resources/web/zkmax/css/tablet/     → concatenated into one tablet.css.dsp (EE-injected)
```

Build output lands in `zul/codegen/resources/web/` (CE) and `../zkcml/zkmax/codegen/resources/web/`
/ `../zkcml/zkex/codegen/resources/web/` (EE/PE). `_`-prefixed files are **partials** — they are
bundled by `build-css.js`, never shipped individually.

## Commands

```bash
node scripts/build-css.js --module zul                            # build CE theme CSS (--module required, exit 2 on misuse)
node scripts/build-css.js --module zkmax                          # or zkex — EE/PE, run from ../zkcml
./gradlew :zul:compileMarbleCss                                    # the Gradle task the builder is wired into
                                                                    # (../zkcml: :zkmax:compileMarbleCss / :zkex:compileMarbleCss);
                                                                    # also runs inside every ./gradlew build
node scripts/check-css-dsp.js --module zul --zk-home /Users/hawk/Documents/workspace/ZK10   # coverage check (also zkmax, zkex)
npm run lint:css                                                  # stylelint hygiene pass (CE; EE/PE from ../zkcml)
bash .claude/skills/marble-theme/scripts/audit-css.sh --out <file>                # hardcoded px / orphan tokens
node .claude/skills/marble-theme/scripts/check-default-display.js --out <file>    # check 5 of the audit
node .claude/skills/marble-theme/scripts/count-important.js [<css root>]          # !important inventory
node .claude/skills/marble-theme/scripts/probe.js …                              # needs the preview module up
cd zkpreview && ./gradlew appRun -PhttpPort=8085 --console=plain   # preview app — keep stdin open, see below
```

**The preview app is on `http://127.0.0.1:8085`.** Use `127.0.0.1`, never `localhost`. `appRun`
waits for a key on stdin and treats EOF as that key, so keep stdin open (interactive terminal, or a
FIFO held open in scripts); **never `appStart`** — under gretty 3.1.1 on Gradle 8.10 its client
never returns. `npm run lint:css` runs stylelint over the CE theme CSS (`.stylelintrc.json`;
EE/PE has its own in `../zkcml`). There is no `npm run build:css`, `watch`, `audit:css`,
`check:forced-colors` or `check:doc-links` script in zk — the builder takes `--module`, so it is
called directly, and the audit and `!important` tooling is the four skill scripts above. Details
and the Playwright projects: `reference/verification.md`.

## Reference

| File | Read it when |
|---|---|
| `reference/tokens.md` | Adding, renaming or auditing a token; component-level theming variables; the z-index scale; what counts as a consumer |
| `reference/layers.md` | Anything touching `@layer`, the reset, the icon stub, or a surviving `!important` |
| `reference/zul-authoring.md` | Writing or fixing a preview page; a page that 404s or 500s; text that will not take a font; a flex row that stacks |
| `reference/brand-override.md` | A customer wants to rebrand by overriding one seed colour |
| `reference/density.md` | Compact / data-dense mode; anything about control heights |
| `reference/css-dsp.md` | Adding or renaming a CSS file; a component renders unstyled; a stylesheet 404s |
| `reference/verification.md` | Running the preview app, the Playwright suite, or screenshot baselines; the tablet gate; A/B against another theme |
| `reference/decision-principles.md` | **Before asking the owner any appearance question.** Who fixes it, which source wins (MD3, MUI, ZK API, IceBlue), same-family baselines, and the only cases that still need the owner |
| `reference/pitfalls.md` | **Read this before any non-trivial change.** Mistakes already made once |
| `reference/bug-filing.md` | The defect is ZK's, not the theme's, and needs a Jira ticket |
| `reference/iceblue-parity.md` | Questions about IceBlue, the `--zk-*` public API break, or Theme Pack palettes |
| `reference/css-audit.md` | Auditing CSS hygiene — orphan tokens, hardcoded colours, repeated shadows, `display` declarations that restate a default; what `scripts/audit-css.sh` reports and how to triage it |
| `reference/important-reduction.md` | Removing, reducing or justifying an `!important`; the count and probe scripts; the render-neutral proof bar |
| `reference/batch-workflow.md` | Running a batch of Marble fixes (an NS batch) that edits a shared selector: impact list before editing, separate engineer and verifier, local commit |
| `reference/jess-review.md` | Answering a screenshot from the designer (Jess), starting a verification batch for her issues, or touching a meeting record |

## ZK version coordinates

`zkpreview` resolves **no published ZK artifact**. Its `settings.gradle` points every `org.zkoss.*`
module at the working trees in `../../zk` and `../../zkcml` via `includeBuild` +
`dependencySubstitution`, so what you preview is the source you just edited. There is no
`zkpreview/pom.xml` and no `<zk.version>` to bump — the version lives in `gradle.properties`.

Everything below applies only to a **separate** app that consumes published FL/Eval jars, not to
anything in this repository.

ZK's FL/Eval builds publish **two servlet flavours under distinct version strings** in the eval
repository, and `dependency:resolve` succeeding does **not** tell you which one you got:

- javax (legacy): `11.0.0.FL.<date>-Eval`
- jakarta: `11.0.0-jakarta.FL.<date>-Eval` — `-jakarta` sits **before** `.FL`, not at the end

A Spring Boot 3 app needs the **jakarta** flavour; the wrong one fails at startup with
`Failed to introspect … ZkAutoConfiguration: javax/servlet/…`. `zkpreview` is the opposite case:
it compiles against `javax.servlet:servlet-api:2.4` and runs on gretty/Jetty. List candidates with
`curl -s https://mavensync.zkoss.org/eval/org/zkoss/zk/zk/maven-metadata.xml | grep '<version>11'`;
check a resolved jar's flavour with
`unzip -p <zk.jar> 'org/zkoss/zk/ui/http/*.class' | strings | grep -c javax/servlet`.

## External reference: MUI's CSS

Marble's visual values follow MUI. Before implementing or refining any component's CSS, read the
matching MUI stylesheet for exact padding, font sizes, state-layer colours and transitions:
`/Users/hawk/Documents/workspace/THEME/material-ui-7.3.1/static-css-output/` — the `INDEX.md`
there holds the ZK → MUI file lookup (Button → `Inputs/Button.css`, Grid/Listbox →
`DataDisplay/Table.css`, Window dialog → `Feedback/Dialog.css`, Tabbox → `Navigation/Tabs.css`,
and so on). This path is machine-local; after migration, re-home or re-fetch the extract.

## Scripts

This skill's tooling lives in `scripts/`.

- `.claude/skills/marble-theme/scripts/audit-css.sh` — mechanical hygiene pass; run the script directly.
- `.claude/skills/marble-theme/scripts/check-default-display.js` — check 5 of the audit; resolves root tags from ZK molds.
- `.claude/skills/marble-theme/scripts/count-important.js` — comment-aware `!important` inventory.
- `.claude/skills/marble-theme/scripts/probe.js` — computed-style A/B probe against the running preview app.

`doc/spec/index.md` remains the normative index for the specifications this skill summarises.
