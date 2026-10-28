# Filing a ZK bug

For defects that belong to ZK itself, not to the theme. Derived from an analysis of 50
recently-closed ZK bugs, so the conventions below are the team's own current practice rather than
an invention.

A separate global skill (`~/.claude/skills/zk-bug-filing/`) handles the Jira mechanics. This file
is the *writing* guide. First bug filed from this project: **ZK-6119**.

## The canonical template — use exactly this

```markdown
# Steps to Reproduce
<runnable artifact + the trigger action>

# Current Result
<one-line observed fact>

# Expected Result
<one-line intended behaviour>

# Debug Info
<root cause: offending code + GitHub permalink + causing commit/ticket>   ← omit if unknown

# Workaround
<runnable mitigation, e.g. zk.override or a config flip>                  ← omit if none
```

- **Headings are `#` (H1).**
- **The section is `Debug Info`** — never "Debug Information" or "Debug info".
- **The three required sections always carry real content.** 100% of real defects in the corpus do.

This is not stylistic preference. Sorting the corpus by date makes the split unambiguous: **100%
of 2026 tickets** use `#` + "Debug Info", and **100% of pre-2026 tickets** use `##` + "Debug
Information". The convention codifies the team's most recent standardization.

## Per-section rules

1. **Steps to Reproduce** — always a *runnable* repro. **Prefer a minimal self-contained
   `<zk>…</zk>` snippet** in a fenced `xml` block: it lives permanently in the ticket, pins the
   exact case, and cannot rot, disappear or version-drift the way an external fiddle can. A
   zkfiddle link (stating the ZK version) is an acceptable alternative or supplement, **not** the
   first choice — inline code-block repros appear in 74% of the corpus versus 42% for fiddles.
   Add numbered actions when there are ≥2 manual steps. Strip to the smallest case that still
   triggers the bug.
2. **Current Result** — only what is **observed**. Paste the real error text, stack trace or DOM
   output where it sharpens the report. One or two lines, and it must read on its own without the
   repro open.
3. **Expected Result** — only the **intended** behaviour, mirroring Current. One line.
4. **Debug Info** — **omit the whole section** unless you have real root-cause material. When you
   do: name the offending method and file, paste the minimal offending snippet, and add
   **line-anchored GitHub permalinks** (`github.com/zkoss/zk/blob/…#Lxxx`). For a regression, cite
   the causing commit and/or `ZK-####`.
5. **Workaround** — **omit** if none exists. When present make it runnable: a `<script>`
   `zk.override(...)` patch, a library-property change, a config flip.

## The mandatory Jira field

**Always set "Affects Version/s"** to the ZK version(s) where the bug reproduces. This is
mandatory for every ZK bug — never file without it.

Via the MCP `createJiraIssue` tool it is the `versions` field inside `additional_fields`:

```json
"additional_fields": {"versions": [{"name": "10.2.0"}]}
```

Look up the project's defined versions at filing time rather than hardcoding. **"Affects
Version/s" = `versions`; "Fix Version/s" = `fixVersions`.** Do not confuse them.

## What strong reports do

- **Lead Steps with a runnable artifact**, then the trigger action.
- **Keep Current and Expected a sharp, mirrored contrast** — one line each. Current states the
  observed fact, Expected the intended behaviour, never blended.
- **Put concrete evidence in Current.** The best layout bugs paste actual DOM output and diff it
  against a known-good version (one ticket shows `<colgroup>` widths for ZK 10 vs 9.6.5 side by
  side). Visual bugs attach a screenshot.
- **Use Debug Info for engineer-grade root cause** — the offending function in a code block, plus
  line-anchored permalinks, plus the causing commit or ticket. This is where ZK's reporting
  quality shows.
- **Frame regressions with provenance** — name the version where it worked and the ticket or
  commit that broke it ("caused by ZK-5102", "since ZK 10"). 14% of the corpus cites the
  originating ticket.

A short report with no Debug or Workaround section is fine when Steps, Current and Expected are
unambiguous and reproducible. **Optional sections are pruned, not faked.**

## What to avoid

- ❌ **An empty `# Debug Info` or `# Workaround` heading.** Four tickets in the corpus have them;
  they add noise and a stray whitespace character. Omit the section entirely instead.
- ❌ **Vague Current/Expected** that only makes sense with the fiddle open.
- ❌ **Naming drift** — three spellings of the debug section exist historically. Use `Debug Info`.
- ❌ **Blending observation and expectation** into one section.

## Before filing a theme-adjacent bug

Confirm it is actually ZK's. Two checks that have caught misattribution before:

- **Search both source trees** — CE (`ZK10/zk`) *and* EE/PE (`ZK10/zkcml/zkmax`, `zkex`). An
  "empty stub" in CE is not proof of absence (`reference/pitfalls.md` §1).
- **Check ZKDoc** — `DOC/zkdoc/zk_component_ref/<component>.md` — for the documented behaviour and
  the edition badge before asserting what ZK "should" do.

Known ZK-side artifacts that are **not** theme bugs, so do not re-file them: the DateRangeBox
popup painting day 1 of the anchor month as selected is a ZK core artifact; the mobile phantom
vertical scrollbar is preview-page horizontal overflow plus page scaling.
