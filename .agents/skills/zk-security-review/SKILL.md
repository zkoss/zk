---
name: zk-security-review
description: >-
  Use this skill when the user mentions "zk-security-review", "security", "XSS",
  "injection", "encoding", "sanitize", or when reviewing code that handles user input,
  HTTP headers, file uploads, or dynamic content rendering in ZK.
  This skill performs a structured security audit using OWASP-aligned checklists.
  Automatically invoked by zk-bug
  and zk-feature when security-sensitive code paths are detected.
---

# ZK Security Review Skill

Structured security audit for ZK Framework code, focused on XSS, injection, and encoding vulnerabilities.

---

## When to use

- Code changes touch user input handling, HTTP headers, or dynamic content rendering.
- The `zk-bug` or `zk-feature` skill detects security-sensitive code paths (see auto-detection triggers below).
- A developer explicitly asks for a security review.
- A code review involves file upload, EL expression rendering, or JavaScript generation.

### Auto-detection triggers (for zk-bug / zk-feature integration)

This skill should be automatically invoked when changed code matches ANY of these patterns:

- Writes to HTTP response headers (`setHeader`, `addHeader`)
- Generates JavaScript strings dynamically (`new StringBuilder` + script content)
- Renders user-provided content into HTML (`innerHTML`, `setContent`, template rendering)
- Handles file upload (`Fileupload`, `AuUploader`, `multipart`)
- Processes URL parameters for page rendering (`request.getParameter`)
- Uses `XMLs.encodeAttribute()` or `XMLs.escapeXML()` in HTML contexts
- Touches `ClassWebResource`, `DHtmlResourceServlet`, `DHtmlUpdateServlet`
- Modifies EL expression evaluation (`ELResolver`, `ELSupport`)

---

## Step 1: Gather Security Context

| Field | Required? | Example |
|-------|-----------|---------|
| Code or file paths to review | Yes | `src/main/java/.../*.java`, `*.zul` |
| What type of user input is handled? | Yes | Form input, URL param, file upload, HTTP header |
| Where is the output rendered? | Yes | HTML page, JavaScript string, HTTP header, error message |
| ZK version | Preferred | 10.4.0, 11.1.0 |

---

## Step 2: CaseFoundry Proactive Review

> **MCP availability check:** Attempt the `review_code` call below. If it fails (connection error, tool not found), CaseFoundry is unavailable — skip this step entirely and proceed directly to Step 3 (Security Checklist Audit). The security checklist is self-contained and does not require MCP.

### When MCP is available

Call `review_code` on all changed production files:

```
review_code(code="<file content>", filename="<filename>")
```

This returns warnings about components with known historical security issues.

Additionally, search for related security cases:

```
search_cases(query="XSS injection sanitize encode <component name>", domain="ZK", top_k=8)
```

### When MCP is unavailable

Skip CaseFoundry lookup. Proceed directly to **Step 3: Security Checklist Audit** — the OWASP-aligned checklist covers all known security patterns and does not depend on MCP. In the final report, leave the "CaseFoundry Matches" table empty and note: "CaseFoundry was not consulted (MCP unavailable)."

---

## Step 3: Security Checklist Audit

Systematically verify each item. Mark as Pass / Fail / N/A.

### 3.1 Output Encoding (OWASP A03: Injection)

- [ ] **All user-supplied data rendered in HTML uses `Encode.forHtml()` or `Encode.forHtmlAttribute()`**
  - Do NOT use `XMLs.encodeAttribute()` for HTML contexts — it uses XML encoding, not HTML encoding.
  - Use OWASP-recommended encoding for all dynamic content in HTML contexts

- [ ] **All user-supplied data rendered in JavaScript uses `Encode.forJavaScript()`**
  - Non-alphanumeric characters must be converted to backslash-escaped numeric format.
  - Never directly concatenate user input (e.g., error messages) into JS string literals

- [ ] **All user-supplied data in URL contexts uses `Encode.forUriComponent()`**
  - URL path components echoed unmodified into JavaScript string literals are exploitable

### 3.2 HTTP Header Injection

- [ ] **User-controlled data is NOT directly embedded in HTTP response headers**
  - Unsanitized user input reflected in HTTP headers enables header injection attacks
  - Fix: Sanitize and encode before including in headers; apply strict allowlist

### 3.3 File Upload Security

- [ ] **Uploaded file names are sanitized before storage and rendering**
  - Malicious file content/metadata can trigger XSS during upload or rendering
  - Check: Strip path separators, HTML entities, and script tags from filenames

- [ ] **Uploaded file content type is validated server-side (not just client-side)**
  - Do not trust `Content-Type` header from the client alone

### 3.4 Dynamic Content Rendering

- [ ] **No direct concatenation of user input into JavaScript strings**
  - Patterns like `"alert('" + userInput + "')"` are directly exploitable
  - Fix: Use `Encode.forJavaScript()` or JSON serialization

- [ ] **Component attributes and page directive values are HTML-encoded before rendering**
  - Component attributes and DSP URL parameters are common XSS vectors if not encoded

- [ ] **Error messages do not include raw user input**
  - Malicious URLs or input rendered as-is in JavaScript error messages enable XSS
  - Fix: Encode error messages before embedding in any client-side context

### 3.5 Client-Side Security (JavaScript / ZUL)

- [ ] **No `eval()` or implied eval (`new Function()`, `setTimeout(string)`).**
  - ESLint rule: `no-eval`, `no-implied-eval`

- [ ] **No direct `location.href` assignment from user-controlled data**
  - ESLint rule: `zk/noLocationHrefAssign`

- [ ] **No mixed HTML in JavaScript widget code**
  - ESLint rule: `zk/noMixedHtml`
  - Fix: Use DOM API to create elements, not innerHTML with concatenated strings

### 3.6 EL Expression Safety

- [ ] **EL expressions do not evaluate user-controlled strings**
  - Prevent server-side template injection via dynamic EL evaluation
  - If user input must be used in EL context, validate against a strict allowlist

- [ ] **Client-side parameter echo is sanitized**
  - Client-provided parameter values echoed without escaping enable reflected XSS
  - Fix: Escape all user-controlled input before echoing in responses

---

## Step 4: Produce Security Report

```md
## Security Review: [File / Component / Issue]

### Scope
- Files reviewed: [list]
- Input sources: [user form, URL param, file upload, etc.]
- Output contexts: [HTML, JavaScript, HTTP header, etc.]

### CaseFoundry Matches
| Case | Vulnerability Type | Root Cause | Fix Applied |
|------|--------------------|-----------|-------------|
| [ref] | [XSS/Injection] | [cause] | [fix] |

### Findings
| # | Check Item | Status | Severity | Location | Recommendation |
|---|-----------|--------|----------|----------|---------------|
| 1 | [item] | Fail | Critical | [file:line] | [fix with OWASP ref] |
| 2 | [item] | Fail | Major | [file:line] | [fix] |

### Summary
- Critical: [count]
- Major: [count]
- Minor: [count]
- Verdict: [Secure / Needs Remediation]
```

---

## Step 5: Chain Back (if invoked from zk-bug / zk-feature)

Return the security report to the calling skill. Any Critical or Major findings should block the fix/implementation from proceeding until resolved.

---

## Guardrails

- **Severity levels:**
  - `Critical` — exploitable XSS/injection with direct user impact
  - `Major` — potential vulnerability requiring specific conditions to exploit
  - `Minor` — defense-in-depth improvement, not directly exploitable
- **Always reference the specific OWASP category** (e.g., A03:2021 Injection).
- **Do not claim code is "secure"** — state what was checked and what passed. Security is about reducing risk, not guaranteeing absence of vulnerabilities.
- **If no issues found:** Explicitly state which checks were performed and passed.
- **False positive handling:** If `review_code` flags something that is already properly handled, explain why it's a false positive rather than silently ignoring.
