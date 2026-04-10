---
name: rootcause
description: >-
  Use this skill when the user mentions "rootcause" or asks for a concise post-bug-fix
  summary focused on root cause, fix approach, and optional Feature/Further Todo items.
---

# rootcause

Concise post-fix communication workflow: explain what happened, how it was fixed, and what comes next.

## When to use

Use this skill when:

- A bug fix is completed and a short, focused write-up is needed.
- The user asks for root cause + solution summary.
- The user wants optional follow-up ideas (Feature / Further Todo).

## Required input

Before writing, confirm these are available:

1. Bug symptom (what users saw).
2. Root cause (why it happened).
3. Actual fix applied (what changed).

If any item is missing, keep it as `TBD` instead of guessing.

## Instructions

1. Summarize in short form
	- Keep each section to 1-2 lines.
	- Prioritize clarity and impact over implementation detail.

2. Explain cause and solution
	- Root Cause: state the direct technical reason.
	- Solution: state the minimal change that fixed it.
	- Avoid long timeline or unrelated context.

3. Add verification evidence
	- Mention test type and result (e.g., unit/UI/manual).
	- If unverified, explicitly state pending checks.

4. Add optional follow-ups
	- `Feature` (optional): improvement ideas beyond bug fix.
	- `Further Todo` (optional): concrete next tasks.
	- Keep both short; omit if no meaningful item exists.

## Output template (default)

Use this exact structure:

```md
Root Cause:
- [One-line technical cause]

Solution:
- [One-line fix summary]

Feature (Optional):
- [Potential enhancement, if any]

Further Todo (Optional):
- [Next actionable task, if any]
```

Omit the "Feature" and "Further Todo" sections entirely if no meaningful item exists. Do not include them with empty or placeholder text.

## CaseFoundry Feedback (Auto-invoked)

After the root cause summary is finalized, **feed it back to CaseFoundry** to close the knowledge loop — but only if the MCP server is connected.

### When MCP is available

```
record_feedback(
  tool_name="diagnose_bug",
  score=1,
  comment="ZK-[ISSUE_NUMBER]: Root Cause: <one-line cause>. Fix: <one-line fix>. Pattern: <pattern if known>"
)
```

This ensures future developers diagnosing similar issues will benefit from this fix.

### When MCP is unavailable

Skip the `record_feedback` call. The root cause summary in the commit message and this rootcause output still serves as documentation. No action is lost — the feedback can be submitted later when MCP becomes available.

Also skip if no ZK issue number is available (e.g., ad-hoc debugging without a JIRA ticket).

---

## Guardrails

- If referencing a `Test.java` in the summary, note whether it overrides `isHeadless()` — this is forbidden in ZK test files.
- Be concise: default total length within 6-10 lines.
- Do not claim certainty without evidence.
- Do not add speculative feature ideas unless they are clearly valuable.
- Prefer actionable language over generic statements.
