# ZK Composer as Spring Bean — Lifecycle, Scope & Risk Analysis

## 1. Background

ZK's `DelegatingVariableResolver` allows a Composer to be declared as a Spring Bean and
applied to a ZUL component via EL expression:

```xml
<?variable-resolver class="org.zkoss.zkplus.spring.DelegatingVariableResolver"?>
<div apply="${myComposer}">...</div>
```

This pattern is attractive because it lets Spring manage the Composer's dependencies.
However, because ZK's Composer lifecycle and Spring's Bean scope are independent concepts,
choosing the wrong Spring scope introduces subtle but serious bugs.

---

## 2. Composer Scope in ZK

A ZK Composer is **Component-scoped**: it is stored as an attribute on the component
object it is applied to. Its lifetime matches that of the component.

| ZK Event | Composer Behaviour |
|---|---|
| Page first loaded | New Composer created and bound to the component |
| AJAX request on same page | Same Composer reused (already on the component) |
| Full page reload / navigation | Component destroyed → Composer garbage-collected |
| Component detached then re-created | New Composer created for the new component |
| Component detached then re-attached (same object) | Composer unchanged — still on the same component object |

The last point is important: `comp.detach()` followed by `parent.appendChild(comp)` does
**not** create a new Composer. The Composer travels with the component object.
A new Composer is only created when `Executions.createComponents()` (or equivalent ZUL
parsing) runs and the `apply` attribute is evaluated again.

---

## 3. How a Spring Bean Becomes a Composer

When ZK processes an `apply` EL expression, the resolution chain is:

1. `UiEngineImpl.execCreateChild0()` evaluates the `apply` attribute.
2. The EL engine calls `DelegatingVariableResolver.resolveVariable(name)`.
3. The resolver delegates to `SpringUtil.getBean(name)`, which invokes Spring's
   `ApplicationContext`.
4. The returned object is passed to `Utils.newComposer()`.
5. Because the object is already a `Composer` instance (not a class name or `Class`
   object), it is returned **as-is** without further wrapping.

The critical consequence: **ZK never instantiates the Composer itself** in the Spring EL
path. It uses whatever Spring's `ApplicationContext` returns directly. Spring's scope
therefore determines whether a new instance is created or an existing one is reused.

---

## 4. Wiring Lifecycle by Composer Type

The timing of field injection depends on which Composer base class is used:

### SelectorComposer (annotation-based)
- In `doBeforeCompose`: `@WireVariable`-annotated fields are resolved via the page's
  variable resolvers and injected **before** children are created.
- In `doAfterCompose`: `@Wire` component references and `@Listen` event listeners are
  wired.

### GenericForwardComposer / GenericAutowireComposer (convention-based)
- In `doAfterCompose`: fields matching Spring bean names are resolved and injected
  **after** the component tree is fully composed.
- An internal `BeforeCreateWireListener` also re-wires on `onCreate` to catch
  late-created objects.

---

## 5. Verified Behaviors Across Lifecycle Scenarios

To confirm that Spring Prototype scope is a safe replacement for FQCN, and to document
the risk of Singleton scope, ZATS Mimic (server-side, browser-less) tests were written
covering four lifecycle scenarios.

### Group 1 — Two Components on the Same Page
Each `apply` attribute is evaluated independently per component.

| Approach | Result |
|---|---|
| FQCN | Two distinct Composer instances (one per component) |
| Spring Prototype | Two distinct Composer instances — identical to FQCN |
| Spring Singleton | One shared instance for both components ⚠ |

### Group 2 — Component Re-creation
A container's children are cleared and re-created via `Executions.createComponents()`.
This re-runs `execCreateChild0()`, re-evaluates the `apply` EL, and produces a new
Composer. State typed into a textbox before the recreation is gone after, confirming a
fresh component state.

| Approach | Result |
|---|---|
| FQCN | New Composer instance after re-creation |
| Spring Prototype | New Composer instance after re-creation — identical to FQCN |

### Group 3 — `<include>` Reload
`include.setSrc()` triggers a new `createComponents()` call. The `apply` EL is
re-evaluated, and a new Composer is produced. This is a common ZK navigation pattern
(swapping page content via an include).

| Approach | Result |
|---|---|
| FQCN | New Composer instance on each reload |
| Spring Prototype | New Composer instance on each reload — identical to FQCN |
| Spring Singleton | Same Composer instance across reloads ⚠ |

### Group 4 — Detach + Reattach (Same Component Object)
`comp.detach()` followed by `parent.appendChild(comp)` moves a component in the tree
without triggering `execCreateChild0()`. The Composer stored on the component travels
unchanged.

| Approach | Result |
|---|---|
| FQCN | Composer unchanged — same instance before and after |
| Spring Prototype | Composer unchanged — same instance before and after — identical to FQCN |

---

## 6. Conclusion

**Spring Prototype scope is behaviourally equivalent to FQCN across all tested ZK
lifecycle scenarios.**

Both approaches produce a fresh Composer instance whenever ZK creates a component (page
load, `createComponents()`, include reload), and both preserve the existing instance when
a component is only moved in the tree. The test results confirm that the choice between
FQCN and Spring Prototype is purely a matter of whether you need Spring to manage the
Composer's dependencies — there is no difference in ZK lifecycle behaviour.

Spring Singleton scope, on the other hand, is **not safe** for use as a ZK Composer.
A singleton is Application-scoped in effect: it is shared across all users, all desktops,
and all components. Any mutable field on the Composer becomes a data-race and potential
data-leak between concurrent users.

---

## 7. Recommendation

| Approach | Safe? | When to use |
|---|---|---|
| `apply="org.example.MyComposer"` (FQCN) | ✓ | No Spring dependency injection needed |
| `apply="${myComposer}"` with `scope="prototype"` | ✓ | Spring DI needed on the Composer |
| `apply="${myComposer}"` with default singleton scope | ✗ | **Never** |

> Always declare a Composer applied via EL expression with `scope="prototype"`.
> Never rely on the Spring default Singleton scope for a ZK Composer.

---

## 8. Out of Scope

`SelectorComposer` with `@WireVariable` uses a separate injection path
(`Selectors.wireVariables()` in `doBeforeCompose`). A Singleton Composer using
`@WireVariable` faces an additional risk: its injected fields are overwritten on every
composition, causing race conditions under concurrent load. This scenario was not covered
by the ZATS tests in this batch.
