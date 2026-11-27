# Review guide

The issues reviewers found most often in pull requests to zk, zkcml and
zkThemeTemplate. Check a pull request against this list first.

The ranking comes from the zk-review runs between 2026-10-19 and 2026-11-26.
It counts only findings posted to a PR, each once, in the PR it was posted
to, and ranks the kinds by how many PRs had one (ties by findings). Findings
left undecided, follow-up re-checks and pre-existing bugs are left out.

Each example ends with the PR it was posted to: `zk#3683` is pull request
3683 of zkoss/zk, and `gh pr view 3683 -R zkoss/zk --comments` shows the
review thread.

| # | Issue | PRs | Findings |
|---|-------|-----|----------|
| 1 | Docs, comments and agent config still describe the old code | 16 | 31 |
| 2 | A fix reaches some siblings but not all | 11 | 15 |
| 3 | Dead code left behind | 10 | 10 |
| 4 | A CSS rule reaches too far or loses the cascade | 8 | 10 |
| 5 | Tests missing, not reproducing the issue, or off-convention | 8 | 9 |
| 6 | Build, SBOM or release output silently wrong | 6 | 7 |
| 7 | Duplicated logic instead of a shared helper | 6 | 7 |
| 8 | The initial render is fixed, the runtime path is not | 4 | 6 |
| 9 | Tokens and component knobs misused | 3 | 5 |
| 10 | The fix does not reach the root cause | 3 | 3 |

## 1. Docs, comments and agent config still describe the old code

A PR removes, renames or changes something (a file, token, script, npm task,
CSS rule or workflow step). Comments, `doc/`, `.claude/` skills and rules,
`CLAUDE.md`, release notes or build entries still point at the old state.
Most of them sit in files the PR never touched.

- Check: for every name the diff removes or renames, search its full name in
  zk and `../zkcml`: `doc/`, `.claude/`, CSS comments, `package.json`, Gradle
  and Maven files.
- Seen: `CLAUDE.md` still named an MCP server after `.mcp.json` was deleted
  (zk#3661). zkcml CSS comments still cited a deleted `doc/skill-gaps.md`
  (zk#3682). A PR removed the only reader of `--zk-data-row-min-height`, and
  the docs still described that reader (zk#3711). An npm script still called
  a gulp task that had been removed (zk#3661).
- Not a finding: a gap in a guideline doc (a skill, a rule file, a spec
  rationale) whose conclusion still holds. Flag one only when following it
  would lead to a wrong action.

## 2. A fix reaches some siblings but not all

The same mechanism lives in parallel places, and the PR fixes only some of
them. Typical groups are sibling components (Combobox, Bandbox, Datebox,
Timebox, Spinner; Timepicker and Daterangebox in zkcml), Listbox and Tree,
sibling XSD types, keyboard and mouse paths, the zkmax tablet sheets, and
master and the 9.6 branch.

- Check: list the places that share the mechanism and search for the same
  selector or pattern. Include the zkcml counterparts and the other
  maintenance branch.
- Seen: `mixed="true"` was added to `columnchildren` and `rowchildren` but not
  to `tablechildren` (zk#3683). The open-invalid border fix went to Combobox
  and Datebox but not Bandbox (zk#3738). A keyboard handler lacked the
  `parent.parent == this` check its click handler had (zkcml#1406).
- Not a finding: a sibling that works through a different mechanism, or
  Marble deliberately differing from IceBlue.

## 3. Dead code left behind

After the change, a guard, field, CSS rule, override or file no longer does
anything.

- Check: for each removed rule or branch, look for code that existed only to
  counter it or feed it.
- Seen: a `transform: none` whose only job was to cancel a rule the PR
  removed (zkThemeTemplate#29). `.z-groupfoot .z-label { color: inherit }`
  after `.z-label` stopped setting a colour (zk#3687). `&& this.isOpen()` made
  dead by an earlier guard (zk#3710). Data files used only by a deleted demo
  page (zk#3734).

## 4. A CSS rule reaches too far or loses the cascade

A selector hits other molds or a nested instance of the same component, or a
new rule beats or loses to a state rule.

- Check: list the molds that share the root class, and picture the same
  component nested inside itself. Compare specificity and order against the
  disabled, invalid, readonly and focus rules, against inline styles and
  against forced colours. Unlayered rules beat every `@layer`.
- Seen: `.z-slider:focus-visible { outline: none }` removed the knob mold's
  focus ring (zk#3687). A tablet checkbox margin rule also hit the toggle mold
  (zkcml#1448). `min-height: 36px` overrode an inline `height` (zk#3705). A
  hover-preview text colour beat the disabled-day colour (zkcml#1427). An
  unlayered `.z-macro` display beat the `@layer zk-utilities` display classes
  (zk#3668).

## 5. Tests missing, not reproducing the issue, or off-convention

- Check: a bug fix has a zktest case. A zkthemelab Playwright spec does not
  count, because CI does not run it (zk#3678, zkcml#1419). The test follows
  the issue's steps, for example an RTL container when the issue is about RTL
  (zk#3654). Moving the only check to `@Tag("IceBlueOnly")` leaves Marble
  untested (zkcml#1422). Each `IceBlueOnly` tag has the one-line
  `// IceBlue-only: <reason>` comment (`.claude/rules/iceblue-tests.md`;
  zk#3663, zk#3668). A change to a test task must not let the tests run
  against the wrong theme or without a jar they need (zk#3661, zk#3689).
- Not a finding: a fix that only changes `zul.xsd` needs no test.

## 6. Build, SBOM or release output silently wrong

- Check: run the whole task chain in one invocation. Compare the script with
  its sibling on the other branch. When a PR removes a build step, find every
  other consumer of what that step produced.
- Seen: the SBOM task was ordered after `jar` but not after `evalJar`
  (zk#3676). A FusionCharts sha256 pin did not match the file on the 9.6
  branch (zkcml#1426). The CE bin shipped an empty `dist/lib/zkforge`
  (zk#3734). Removing `compileLess` left IceBlue-family themes without their
  za11y sheet (zkcml#1415).
- Not a finding: a bug in a dev-only or report-only script with no product
  impact, unless it makes a test silently stop guarding the product.

## 7. Duplicated logic instead of a shared helper

- Seen: Button.ts and Toolbarbutton.ts added the same iconOnly
  label-wrapping block (zk#3725). The keyboard and click paths repeated the
  movable-panel predicate, and the two copies had already drifted apart
  (zkcml#1406). A gmavenplus block was written out three times instead of
  once in `pluginManagement` (zk#3675).
- Not a finding: a component copying `.z-button` styles. Components keep
  their own classes.

## 8. The initial render is fixed, the runtime path is not

The fix works on the first render but not after a setter, a re-render, or
focus and blur, or not on zhtml and included pages.

- Check: for each class, style or state the fix sets in `domClass_`, `bind_`
  or a renderer, find the setter and the other entry points. Watch for cached
  sizes such as `_hflexsz`.
- Seen: `A.setDir` left a stale `z-a-reverse` class (zk#3661). Brand and
  density library properties were applied only in `renderDesktop`, so zhtml
  and included pages missed them (zk#3661). An inplace `hflex="min"` combobox
  was clipped on focus (zk#3673).

## 9. Tokens and component knobs misused (Marble)

- Check: no literal where a `--zk-*` token exists. A component follows its
  own public knobs (`--zk-<component>-*`) rather than system tokens. New knobs
  are registered in `doc/spec/component-theme-variables.md` and its
  Playwright spec.
- Seen: the z-log `z-index: 99000` was written as a literal (zk#3668). The
  fileupload size line used `on-surface-variant` and ignored
  `--zk-fileupload-color` (zk#3714). The biglistbox selected row used system
  tokens instead of the list-row selected knobs (zkcml#1436).

## 10. The fix does not reach the root cause

The fix patches the symptom where it shows, and the same cause still breaks
other paths.

- Check: name the cause, then list every path that reaches it.
- Seen: a Calendar fix kept the browser-time-zone `_moment.toDate()`, which
  also feeds `getYDelta` and `getEraName` in `datefmt.ts`, so a
  Japanese-calendar Datebox at an era boundary formats the wrong era or year
  (zk#3684). Cropper coalesced setters only while `zk.ausending` was set, so
  client-side setter sequences still clipped (zkcml#1460). The shared
  `zk.wcs` listed a sheet zul does not ship and relied on a theme provider to
  skip it (zk#3678).
