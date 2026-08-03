@.github/copilot-instructions.md

## Architecture: Component-Widget Duality

ZK uses a dual-object model — every UI element has two halves:
- **Java Component** (server): state, business logic, `smartUpdate()` pushes changes to client
- **TypeScript Widget** (client): DOM management, user interaction, `fire()` sends events to server

Mapping: `org.zkoss.zul.Button` (Java) <-> `zul.wgt.Button` (TypeScript)
When modifying a component, check if BOTH sides need changes.

## Module Dependency Order

```
zcommon -> zel -> zweb -> zweb-dsp -> zk -> zul -> zhtml -> zkbind -> zkplus -> zktest
```

- `../zkcml/` (enterprise) depends on `zk`, `zul`, `zkbind`
- `zktest` depends on ALL modules — always build upstream first if tests fail

## Bug Investigation with CaseFoundry

This project has a CaseFoundry MCP server with 18,000+ historical cases.
- `search_cases("description")` — find similar past bugs
- `lookup_issue("ZK-XXXX")` — get full context for a Jira issue
- `diagnose_from_stacktrace("...")` — automated stack trace analysis
- Use `git blame` to find commits, extract ZK-XXXX from message, then `lookup_issue`

## Commit Convention

- Format: `ZK-XXXX: short description` or `fix ZK-XXXX short description`
- Imperative mood: fix, add, support, update, remove, replace
- PR title MUST include `ZK-XXXX`
