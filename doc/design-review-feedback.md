# Design-review feedback loop

How a designer reviewing the deployed preview app (see
[preview-deployment.md](preview-deployment.md)) reports what looks wrong, and how
that comes back into the theme.

Deliberately not Jira: one private GitHub repo used **only** as an issue list, and
a button in the preview app that opens a pre-filled issue for the page on screen.

| | |
|---|---|
| Tracker | `hawkchen/marble-issue` (private, issues only, no code) |
| Entry point | **Report this page** button, top of the preview sidebar |
| Label applied | `design-feedback` |
| Triage | `gh issue list --repo hawkchen/marble-issue` |

## What the designer does

1. Browses `http://<host>:<port>/marble/` — the use-case browser with every page
   in the sidebar.
2. When something looks off, clicks **Report this page**. A GitHub issue form
   opens in a new tab, already filled in with the page and browser state.
3. Writes what looks wrong, drags a screenshot into the text box, picks how much
   it matters, submits.

She needs read access to the private tracker; that is enough to open issues and
comment. Once you know her GitHub username:

```bash
GH_TOKEN=$(gh auth token --user hawkchen) \
  gh api -X PUT repos/hawkchen/marble-issue/collaborators/<username> -f permission=pull
```

## What the button captures automatically

The button is a deep link only — no server round trip, no storage, nothing to
keep in sync but the tracker URL. `marbleReportIssue()` lives in
`src/test/resources/web/usecase/report-issue.js`, which
`src/test/resources/web/usecase/index.zul` loads with
`<script src="~./usecase/report-issue.js"/>`. It pre-fills:

- **title** — `[design] <page>`
- **label** — `design-feedback`
- **body** — three empty headings (what looks wrong / what it should look like /
  how much it matters) plus a context table:

| Field | Where it comes from | Why it matters |
|---|---|---|
| Page | `location.hash` — the SPA keeps the page it shows in the bookmark | the single most-missing fact in design feedback |
| URL | `location.href` | carries host + context path, i.e. **which build** she was looking at |
| Viewport | `innerWidth × innerHeight @dpr` | tells a real layout bug from a narrow window |
| Density | `data-density` on `<html>` | comfortable vs the compact preset (`doc/spec/data-dense-mode.md`) |
| Browser | `navigator.userAgent` | rules out browser-specific rendering |

Empty hash means she is on the landing page, so the page id falls back to
`usecase/inventory-table` — `UseCaseVM.DEFAULT_PAGE`. Keep the two in step if the
landing page ever changes.

## Working the issues

```bash
export GH_TOKEN=$(gh auth token --user hawkchen)   # not the default gh account here
gh issue list  --repo hawkchen/marble-issue --label design-feedback
gh issue view  --repo hawkchen/marble-issue <n> --comments
gh issue close --repo hawkchen/marble-issue <n> \
  --comment "Fixed in <commit>; redeployed, please re-check <page>."
```

Suggested rhythm: fix a batch → `./scripts/deploy-preview.sh` → close the issues
with the page to re-check. The issue stays the conversation; the deploy is the
answer.

## If this outgrows itself

- **Structured fields instead of free text** — add
  `.github/ISSUE_TEMPLATE/design-feedback.yml` to the tracker repo and append
  `&template=design-feedback.yml` to the deep link; query-param names then map to
  field ids, so the current `page`/`body` prefill keeps working.
- **Per-page status at a glance** — a GitHub Project board over the same issues.
- Reach for Jira only if this has to join the ZK release process; a private issue
  list plus the button is deliberately the whole system for now.
