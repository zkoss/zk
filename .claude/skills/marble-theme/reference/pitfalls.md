# Pitfalls

Eleven mistakes already made once, each with the rule it produced. Read this before any
non-trivial change; most of these cost hours and none of them look like mistakes up front.

## 1. Touch behaviour lives in the EE tree, not CE

An Explore pass over the CE checkout (`$ZK_SOURCE`, the root of the ZK CE checkout) concluded combobox had "no bottom-sheet" and that `_fixedVParent`
was an empty stub — i.e. no bug existed. Wrong: ZK's mobile molds (the `*-touch.ts` overrides that
swap in wheel pickers and bottom-sheets) live in the **EE** tree at
`$ZKCML_SOURCE/zkmax/src/main/resources/web/js/zkmax/touch/` (`$ZKCML_SOURCE` is the root of the zkcml EE checkout, normally `../zkcml`).

**Rule:** for any mobile/tablet/touch question, search **both** `$ZK_SOURCE` (CE) and
`$ZKCML_SOURCE/zkmax` + `zkex` (EE/PE). An "empty stub / no special handling" finding from the CE
tree alone is **not proof of absence**.

**Corollary:** before claiming a family of components shares a behaviour, confirm each one has its
own override. Siblings under a shared base class do **not** inherit a sibling's touch mold — only
combobox has `combo-touch.ts`; selectbox is a native `<select>` (unfixable via CSS, no ZK popup)
and bandbox has no touch mold at all, so pinning it `position:fixed` would *break* it.

## 2. DSP EL without its taglib silently drops the rule

See `reference/css-dsp.md` for the mechanism. **Rule:** when emitting ZK DSP EL into a generated
`.css.dsp`, ensure the matching `<%@ taglib %>` leads the file (injected *after* minification),
verify by fetching the **WCS aggregate** rather than the `.dsp` directly, and prefer
`c:encodeURL("~./…")` over a relative `url()`.

## 3. A deprecated upstream property's absence is a decision, not a gap

A "find missing features" sweep started treating every framework capability the theme doesn't use
as a gap. Marble sets zero library properties and is token-driven, so the deprecated
`org.zkoss.zul.theme.fontFamily*` / `fontSize*` are *correctly absent*.

**Rule:** in a feature-gap audit, tag every finding **PRESENT / MISSING / DEPRECATED-OK / N/A**.
Never let "the theme doesn't use X" default to MISSING.

**Rule:** any claim that a framework artifact was "removed" needs source proof. The first draft
said a LESS-era consumer DSP "no longer ships"; `ext.css.dsp` in fact *still* ships as a classpath
resource — it is simply no longer requested by any WCS/lang mechanism. Prefer "no longer loaded by
mechanism Y" over "no longer ships" unless you confirmed the file is gone.

## 4. Contracts must cite the ZKDoc component reference

The cropper contract passed spec-author, two audits, user approval and dual-gate VERIFIED without
ever citing `https://github.com/zkoss/zkdoc/blob/master/zk_component_ref/cropper.md` or its `ZKCompRef_Cropper.png` — the one
canonical reference ZK itself provides.

**Rule:** for any component, check `https://github.com/zkoss/zkdoc/blob/master/zk_component_ref/<component>.md` +
the images at `https://github.com/zkoss/zkdoc/tree/master/zk_component_ref/images` (`ZKCompRef_<Component>*.png`) **first**. The ZKDoc image is the structural ground truth
(which elements exist, where, how many) that anchors the `mockup-needed` decision; its IceBlue
styling is not a target (`decision-principles.md` V5).

**Rule (separate, and it bit twice):** **the ZKDoc edition badge is authoritative for PE/EE
classification, never the source jar or directory.** The slider knob mold was labelled EE across
skill, contract and mockup because the edition was inferred from `zkcml/zkmax/`; ZKDoc says **PE**.
Same for cropper. PE features do ship in zkmax sources.

## 5. The approval gate enforces today's template, not the one the contract was born with

Cropper's contract predated the outcome-driven format and passed the gate with no
`## Outcome assertions` section — so none of the component's actual anatomy was ever asserted.
Three safeguards missed it, including a "pending" slot in a wave-migration status file that made
the omission read as planned deferral.

**Rule:** any contract passing the approval gate must conform to the template **as it exists
today**. Wave schedules sequence proactive migration; they never exempt a contract already in
front of the gate.

## 6. Upstream codegen CSS is the structural ground truth for library wrappers

Cropper shipped with all 8 Jcrop resize handles invisible; goldenlayout with maximize broken and a
permanently visible drop-target indicator. Same root cause, detailed in `reference/css-dsp.md`.

**Rule:** diff against ZK's codegen `.css.dsp` and classify every upstream rule as
structural-required (reproduce verbatim) or decorative (theme's choice).

**Corollary:** declared-property contract rows cannot catch these. Interaction-level assertions
(click maximize, start a drag) can.

## 7. Delete the theme-global invention; don't patch exceptions onto it

The splitlayout pane-fill bug (a 12px hole before the splitter) traced to a theme-invented
`margin-block-end: 12px` on container widgets. Stock ZK widgets carry **zero** default margin, and
ZK's own sizing (`zk/flex.ts` writes `calc(100% - marginHeight)` on both axes in row mode) is
written on that assumption. The first plan kept the rule and added per-context exception selectors;
the exception list is open-ended — every JS path that measures margins × every rhythm widget is a
potential hole.

**Rule:** when a theme-global invention conflicts with a framework JS assumption, the first option
to evaluate is **deleting the invention and aligning with stock ZK**. Check what stock ZK and the
benchmark frameworks actually do before defending it. Bootstrap ships zero margin on `.card`/
`.btn`; MUI uses `<Stack spacing>`. Marble made spacing opt-in (`.z-vstack` / `.z-mb-*`).

**Corollary:** "ZK users are Java engineers" argues for *explicit documented one-liners* with a
visible failure mode, not *invisible magic* whose failure mode requires reading ZK source to debug.

## 8. Prefer a self-contained `<zk>` snippet over a zkfiddle link

While deriving the bug-writing guide, a zkfiddle link was recommended as the *preferred* repro. The
corpus disagreed: inline code-block repros appear in 74% of reports, zkfiddle links in 42%.

**Rule:** in Steps to Reproduce, prefer a minimal self-contained `<zk>…</zk>` snippet. It lives
permanently in the ticket, pins the exact case, and cannot rot or version-drift. A fiddle link is
an acceptable supplement, not the first choice.

**Corollary:** don't elevate the most *eye-catching* observed artifact into "preferred" — check
which form is both more durable and more frequent in the data.

## 9. Measure rendered width; never infer it

Investigating whether DateBox "hugs its content", both an exploration and an earlier commit
reasoned from CSS + ZK source that a size-less `<input>` with `flex:1;min-width:0` falls back to
the UA default `size=20` (~172px) and was "over-wide". A throwaway probe showed the opposite: a
bare datebox shrink-wraps to ~146px root / ~108px input, and that width is *content-insensitive*,
so a long `yyyy/MM/dd HH:mm` value was silently **clipped** (`scrollWidth` 132 > `clientWidth`
108). The real defect was clipping, not over-width — and the whole test design (a `<130px`
threshold) passed trivially on the buggy build and had to be scrapped.

**Rule and its corollaries are in `reference/verification.md` under "Empiricism, not inference".**
An extra corollary: recipe choice is empirical too — `flex:1 1 auto` beat `flex:0 1 auto` for
datebox/timebox only because a probe confirmed it hugs in an auto context yet still fills an
`hflex` root. The difference is invisible on paper.

## 10. A behaviour-preserving move makes the old baselines an oracle, not dead weight

Planning the Marble → `zk` migration, the recommendation was to discard the screenshot baselines
and re-cut them, on the grounds that the build change "invalidates them anyway". False: the change
moves *where* CSS lives and is served from, not what is in it, and the chosen build option keeps
the same minifier. **Paths do not move pixels.**

**Rule:** for any relocation, port or refactor that is *supposed to be behaviour-preserving*, the
existing verification artifacts — baselines, golden files, recorded fixtures — are the **primary
oracle for the move itself**. Carry them across first, diff against them, and only then refresh.
"They'll be invalidated anyway" needs proof that the change actually alters output, not just that
it touches the pipeline.

**Corollary:** using baselines as a migration oracle requires *tightening* tolerance for that one
run. A regression gate's tolerance and an equivalence check's tolerance are different numbers for
different jobs.

**Corollary:** separate *compared* baselines from *review artifacts* before quoting a cost.

## 11. Minifiers corrupt silently, and differently from each other

The build has used both CleanCSS and Lightning CSS (Lightning CSS since 2026-09-04). **Both
corrupt output silently, in different ways** — hence `maskLongNumbers()` and the other workarounds
in `build-css.js`. Separately, `zkless-engine`'s LESS 3 miscompiles at exit code 0.

**Rule:** "it compiled" is not "the output is equivalent". Diff source against output after any
change to the minifier, the minifier version, or a CSS feature that is new to the codebase — the
`oklch(from …)` relative colour syntax was verified empirically for exactly this reason.

**Rule:** never trust a version-pinned comment claiming a workaround is still needed. Re-verify
empirically. The `!important` reduction pass took the count from 37 to 33 by doing this.

## 12. A "no-op" legacy declaration can be load-bearing for JS that parses computed style

Re-authoring `scrollview.less` into Marble CSS dropped `.z-scrollview-content { transform:
translate3d(0,0,0) }` as visually inert. `Scrollview.doTouchStart_` seeds the scroll position by
parsing that element's **computed** transform matrix; with no transform the browser reports `none`,
the parse yields `NaN`, and every later `_move()` writes `translate3d(NaNpx, …)`, which the browser
silently rejects. Touch scrolling was dead while every probe and all six outcome rows stayed green —
they measured presence and geometry (bar exists, thumb proportional), never the interaction. Found by
hand on a phone (2026-09-12), not by the harness.

**Rule:** before replacing a widget's legacy CSS, grep its `.ts` for what it *reads* and keep every
declaration those reads depend on, with a comment naming the reading line:

```
grep -n "getComputedStyle\|\.style\[\|\.css(" <Widget>.ts
```

An inline write (`.style[x] = …`) tells you which properties JS owns; a read tells you which CSS
values are *inputs* to JS. Only the second kind can turn a dropped declaration into a dead feature.

**Rule:** for any component whose purpose *is* an interaction (scroll, drag, resize, toggle), at
least one outcome row must perform the interaction and assert that state changed. Playwright's
touchscreen API has no drag — drive it through CDP `Input.dispatchTouchEvent` (see
`zkthemelab/src/test/playwright/tablet.spec.ts`, "a touch drag moves the content").

## Debugging on a minimal repro page

When you debug or investigate a bug, first build a minimal repro page at `zkthemelab/src/main/webapp/web/pv/<topic>-repro.zul`. Put only the components that trigger the problem and the least configuration in it. Leave out the page frame, title and other sections. Reproduce, measure and verify the fix on this page, not on the full gallery page: other components, styles and load order on that page are unrelated possible influences. To compare fixes, make one variant page per fix, each differing in one place only. These pages are temporary. Delete them after debugging, including the copy under `build/inplaceWebapp`, and do not commit them. A regression test that must be kept long term goes into the proper spec instead.
