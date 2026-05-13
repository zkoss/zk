# Code Review Criteria

Shared review standards for the `zk-commit` skill.

---

## 1. Logic Correctness (highest priority)

- Validate control flow, null/empty handling, boundary conditions, and state transitions.
- Check async/event ordering and side effects.
- Flag behavior changes that may break backward compatibility.

## 2. Maintainability and Optimization

- Spot duplicated logic, overly complex branches, dead code, and naming clarity issues.
- Check for unnecessary allocations/loops or repeated expensive work.
- Prefer minimal, low-risk refactors tied to concrete benefit.

## 3. Test Adequacy

- Confirm tests cover happy path and key edge cases introduced by the change.
- Flag missing regression tests for any risky logic branch.
- For ZK flows, verify test intent matches UI behavior changes.

---

## 4. ZK Pattern-Specific Checks

Apply these checks when the commit touches the corresponding area.

### 4.1 Null Safety

- [ ] Detached component access: verify page/desktop reference is non-null before use — component may be detached by the time the callback executes.
- [ ] Shadow element operations: null-guard results from `ShadowElementsCtrl.getCurrentInfo()` in nested foreach/if/apply scenarios.
- [ ] EL expressions: handle null model object properties — ZK 8+ enforces stricter EL semantics and will NPE where ZK 7 silently returned null.
- [ ] Bind expressions: guard against uninitialized model when evaluation happens at constructor time.
- [ ] Session timeout: validate session validity before accessing session-scoped resources in timer or async callbacks.
- [ ] Concurrent model access: guard collection operations (e.g., `addAll()`) against null when model may be modified concurrently.

### 4.2 Rendering Performance

- [ ] `breathe()` placement: must be called BEFORE expensive DOM creation in mount loops, not after — late placement causes UI freeze when rendering takes >2.5s.
- [ ] No synchronous for-loops blocking browser thread in widget init — replace with while-loops if needed, avoid setTimeout fallback that breaks dependency ordering.
- [ ] Scroll/resize handlers: must be throttled or debounced — unthrottled high-frequency events cause layout thrashing and reflows.
- [ ] Render-on-Demand (ROD) enabled for Grid/Listbox/Tree with >50 items — without ROD the entire dataset renders at once, causing timeout on large data.
- [ ] ForEach: use incremental update (reuse previously rendered components), not full re-render on every data change.
- [ ] Loop conditions: cache model query results (e.g., `getChildCount()`) in a local variable — re-evaluating in the loop condition causes O(n) calls per iteration.
- [ ] Model replacement: call `invalidate()` after bulk model swap for Tree/Grid — without it, individual DOM operations fire instead of a single re-render.
- [ ] `rerender()` not called synchronously before DOM is ready — defer to next tick with `setTimeout(fn, 0)`.
- [ ] No re-rendering during rendering phase — check whether the widget is already queued before scheduling another rerender.

### 4.3 Component Lifecycle

- [ ] `unbind()` must be called BEFORE widget replacement, not after — calling unbind on an already-replaced widget skips cleanup of event listeners and resources.
- [ ] No UI modification during rendering phase — respect internal rendering flags; mutations after rendering begins cause unpredictable state.
- [ ] ID space cleanup triggered on partial detach — a component detached from its page but still attached to a parent must still clean up its ID space entries.
- [ ] ViewModel destroy callbacks invoked during detach/destroy — verify `@Destroy`-annotated methods fire for Window, Include, and desktop destruction scenarios.
- [ ] No firing events on detached components — dispatching events to components without a live desktop causes infinite loops or silent failures.

### 4.4 Data Binding (MVVM)

- [ ] `@init` vs `@load` used correctly: use `@load` for dynamic collections that change at runtime, `@init` only for one-time initialization values.
- [ ] `NotifyChange` / `SmartNotifyChange` on setter methods: verify the annotation is correctly processed and triggers UI refresh.
- [ ] No multiple conflicting `@init` / `@load` on the same property — this causes non-deterministic initialization order and stale data.
- [ ] `notifyChange` scoped to the specific property — notifying `"*"` or the wrong property name can cascade null resolution to unrelated bound fields.
- [ ] Type consistency: getter return type must match the field type (e.g., do not declare `Integer[]` getter but assign `int[]` — silent binding failure).
- [ ] Server-side `invalidate()` must propagate to client-side MVVM binding — if using Client MVVM, verify the invalidation triggers re-evaluation on the client.

### 4.5 Event Listener Cleanup

- [ ] Listen/unlisten symmetry: the same condition must be used for both registration and deregistration — mismatched conditions leave orphaned listeners that leak memory.
- [ ] `zWatch.unlisten` must use array format `[widget, listener]`, not a bare listener object — wrong format causes silent failure.
- [ ] Method name casing: the correct call is `unlisten` (lowercase L) — `unListen` is silently ignored.
- [ ] DOM listener tracking: use a stable identifier (element ID or fallback path), not direct DOM element reference — identity mismatch prevents cleanup.
- [ ] `unlisten` must search all matching listener entries, not stop at the first match — early exit leaves listeners from other bind levels active.

### 4.6 Resource Management

- [ ] Streams and readers must use try-with-resources or explicit close in a finally block.
- [ ] JDBC connections must be closed in a finally block after use (e.g., after report generation).
- [ ] Executor and ScheduledExecutorService instances must be stored and `shutdown()` called during cleanup — only cancelling the ScheduledFuture is not enough.
- [ ] Scripting interpreters (BeanShell, Groovy, Rhino) must override `destroy()` to nullify internal references and call `super.destroy()`.
- [ ] DataBinder: when removing a binding, also traverse and remove orphaned BindingNode entries from internal tracking maps to prevent memory leaks.

### 4.7 Security

- [ ] User-supplied data rendered in HTML must be encoded (e.g., `Encode.forHtml()`, `Encode.forHtmlAttribute()`) — do NOT use `XMLs.encodeAttribute()` for HTML contexts.
- [ ] User-supplied data rendered in JavaScript must use JavaScript-specific encoding — non-alphanumeric characters must be escaped, not just quoted.
- [ ] No user-controlled data directly embedded in HTTP response headers without sanitization.
- [ ] Uploaded file names must be sanitized before storage and rendering — strip path separators, HTML entities, and script fragments.
- [ ] Error messages must not include raw user input when rendered in client-side contexts (alert, script, HTML).
- [ ] No `eval()`, no `location.href` assignment from user-controlled data, no mixed HTML concatenation in JavaScript widget code.

---

## Finding Format

For each finding, include:

- **Severity:** `Critical` / `Major` / `Minor` / `Suggestion`
- **Location:** file and function/symbol
- **Why it matters** (impact)
- **Suggested fix** (specific and minimal)

---

## ZK-Specific Guardrails

- Flag any `Test.java` that overrides `isHeadless()` — this is forbidden and must be removed.
- Do not invent issues without evidence from diff/context.
- Prefer root-cause feedback over style-only comments.
- Keep recommendations backward compatible unless user explicitly accepts breaking change.
- If uncertain, label as "needs confirmation" and explain what to verify.
- Cross-repo: if the commit touches core ZK, flag whether zkcml may need a companion change (and vice versa).
