---
name: zk-commit
description: >-
  Use this skill when the user mentions "zk-commit", "code review", "CR",
  or mentions reviewing a commit hash. This skill performs structured code review
  of one or more commits, with automatic cross-repo context lookup: if the commit
  message contains a ZK-XXXX issue key, it searches both the current repo and the
  zkcml repo for related commits, and queries CaseFoundry for historical context.
  Works especially well after using zk-bug or zk-feature workflows.
---

# zk-commit

Structured code review workflow with cross-repo context and CaseFoundry-powered historical analysis.

## When to use

Use this skill when:

- The user asks to review a specific commit hash.
- The user asks whether a commit has optimization opportunities.
- The user asks for logic defects, edge-case bugs, or regression risks.
- The context is ZK work, especially after `zk-bug` or `zk-feature` changes.

## Required input

Before reviewing, confirm:

1. A valid commit hash is provided (full or abbreviated).
2. Review focus (if specified): e.g. logic correctness, performance, backward compatibility, readability.
3. Whether the user provided pasted content (code snippet, logs, notes, or concerns).

If commit hash is missing, ask for it and stop.

## Excluded directories

**Skip files under `build/`, `bin/`, and `codegen/` entirely** — these are generated outputs, not reviewable source code. Only review files under `src/`.

## Instructions

### 1. Evaluate pasted content first (if provided)

- Identify what the pasted content is claiming or highlighting.
- Validate whether the concern is valid based on commit diff and surrounding context.
- Summarize findings for pasted content before expanding scope.

### 2. Resolve and inspect the commit

- Run `git show --stat --patch <commit>`.
- If needed, inspect parent context with `git show <commit>^:<path>` and current file state.

### 3. Extract ZK issue key and gather cross-repo context

Parse the commit message for ZK issue references matching `ZK-[0-9]+`.

**If a ZK-XXXX key is found, execute ALL of the following in parallel:**

> **MCP availability check:** Attempt `lookup_issue("ZK-XXXX")` first. If it fails (connection error, tool not found), CaseFoundry is unavailable — skip steps 3a, 3d, 3e and use the **Manual Fallback** at the end of this section. Steps 3b/3c (cross-repo git search) always work regardless of MCP status.

#### 3a. CaseFoundry lookup

```
lookup_issue("ZK-XXXX")
```

Returns: JIRA issue details, related GitHub commits, helpdesk tickets, and test cases.
This provides the **full historical context** — why this issue exists, who reported it, and how similar issues were resolved.

#### 3b. Cross-repo search (zkcml)

Search the zkcml repo for commits referencing the same issue:

```bash
git -C /Users/peakerlee/prj/zk-master/zkcml log --all --oneline --grep="ZK-XXXX"
```

**If zkcml commits are found:**
- Run `git -C /Users/peakerlee/prj/zk-master/zkcml show --stat --patch <zkcml_commit>` for each.
- These are the **companion changes** in the enterprise repo — review them as part of the same logical change.

#### 3c. Cross-repo search (zk-master / current repo)

If the commit being reviewed is in zkcml, also search the core ZK repo:

```bash
git -C /Users/peakerlee/prj/zk-master log --all --oneline --grep="ZK-XXXX"
```

#### 3d. CaseFoundry review_code

Run `review_code` on the changed production files to detect known pitfalls:

```
review_code(code="<changed file content>", filename="<filename>")
```

#### 3e. CaseFoundry search_cases

Search for similar historical issues to surface hidden risks:

```
search_cases(query="<commit message / change description>", domain="ZK", top_k=5)
```

### 4. Build review context

- Identify changed files by category: production code, tests, docs/release notes, config.
- Infer intent from commit message and changed behavior.
- If a ZK issue key was found:
  - Cross-reference the commit changes against the JIRA issue description (from CaseFoundry).
  - Verify the fix/feature covers the full scope described in the issue.
  - Check if the zkcml companion commits are consistent with the core repo changes.
- Flag any discrepancies between the issue description and the actual changes.

### 5–7. Apply review criteria

> See `references/review-criteria.md` for logic correctness, maintainability, test adequacy, and ZK pattern-specific checks.

When reviewing, pay special attention to findings from CaseFoundry:
- If `review_code` flagged known pitfalls, verify whether the commit addresses or introduces them.
- If `search_cases` returned similar historical bugs, check whether this commit could reintroduce them.
- If the zkcml companion commits touch related components, verify cross-repo consistency.

### 8. Find additional points beyond pasted content

- Explicitly check for worthwhile review items not mentioned in the pasted content.
- Prioritize hidden logic risks, missing tests, and regression impact.
- Check cross-repo impact: does this change in one repo require a corresponding change in the other?

### 9. Produce actionable findings

> See `references/review-criteria.md` → **Finding Format** for required fields per finding.

### 10. Provide final verdict

Summarize:
- `Safe to merge` or `Needs changes before merge`
- Top risks (max 3)
- Highest-value optimizations (max 3)
- Cross-repo status: whether zkcml companion commits exist and are consistent

## Output format

Return results in this order:

1. **Commit summary** (what changed, which repo)
2. **Issue context** (from CaseFoundry: JIRA issue, historical cases, helpdesk tickets)
3. **Cross-repo context** (zkcml companion commits, if any)
4. **Pasted content assessment** (if provided)
5. **CaseFoundry warnings** (from review_code and search_cases)
6. **Additional findings** beyond pasted content
7. **Suggested fixes** (short, concrete)
8. **Verdict**

## Guardrails

> See `references/review-criteria.md` → **ZK-Specific Guardrails** for shared guardrails.

- **Cross-repo search is best-effort.** If the zkcml or zk-master repo is not available at the expected path, skip and note it.
- **CaseFoundry lookup is advisory.** If no results are returned, proceed with standard review.
- **CaseFoundry MCP may be unavailable.** If the MCP server is not connected, skip all CaseFoundry tool calls (3a, 3d, 3e) and rely on local analysis: git history, source code reading, and `references/review-criteria.md` pattern checks (sections 4.1–4.7). Note in the output that CaseFoundry was not consulted.
- **Do not block on missing cross-repo data.** Note what was checked and what was unavailable.
- **Private repo awareness:** All review data stays local. No external API calls except CaseFoundry MCP (internal service).
