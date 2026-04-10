# ZK Pattern Catalog

Quick-reference catalog of CaseFoundry's top bug patterns with representative cases and check items.
Used by `zk-diagnose`, `zk-perf-check`, `zk-security-review`, and code review skills.

---

## NPE_NULL_CHECK (3,191 cases)

Common null-pointer scenarios in ZK:

| Check Item | Representative Case | Context |
|-----------|-------------------|---------|
| Detached component: access `selectedItem` / page reference after detach | `GH-68d3ddc9` | Missing null check after retrieving page reference |
| Shadow element: `ShadowElementsCtrl.getCurrentInfo()` returns null | `GH-c3afa3c1` | Nested foreach/if/apply components |
| EL expression: null model object property access | `HD-2396` | ZK 8 stricter EL evaluation vs ZK 7 |
| Bind expression: constructor call before model init | `GH-2ae125c9` | BindExpressionEvaluator with uninitialized model |
| Session timeout: component/listener accesses invalidated session | `JR-ZK-3116` | Timer component + session expiration in ZK 8 |
| Concurrent model modification: addAll() on null collection | `JR-ZK-1665` | Listbox bind tracker with concurrent mutations |
| Desktop reference before verification | `GH-6932deae` | SonarQube-flagged NPEs across core classes |

---

## UI_FREEZE (4,785 cases)

Performance and rendering freeze patterns:

| Check Item | Representative Case | Context |
|-----------|-------------------|---------|
| `breathe()` placed after expensive DOM creation | `GH-430a38a3` | Move breathe() before expensive operations in mount loops |
| Synchronous for-loop blocks browser thread | `GH-53475afc` | `_loadAndInit` / `_evalInit` using setTimeout fallback |
| Unthrottled scroll/resize events | `HD-3823` | Frozen scrollbar with high-frequency style updates |
| ROD not enabled for large dataset | `JR-ZK-5278` | Grid/Listbox renders entire dataset without Render-on-Demand |
| ForEach full re-render on every data change | `GH-c1800eeb` | Missing component reuse logic |
| `getChildCount()` re-evaluated in loop condition | `JR-ZK-1146` | Cache result in local variable before loop |
| Tree model replacement without `invalidate()` | `JR-ZK-4803` | Individual DOM ops instead of single re-render |
| Client MVVM excessive per-cell binding | `HD-16507` | 768 columns x 30 rows, switch to Server MVVM |
| Hlayout/Vlayout with many children | `GH-e11840c1` | >20 children causes excessive DOM manipulation |
| `rerender()` called synchronously before DOM ready | `GH-a493339c` | Defer to next tick with timeout(0) |
| Window re-rendering infinite loop | `GH-76e793bb` | Missing `_rdque.includes(this)` check |

---

## COMPONENT_LIFECYCLE (2,571 cases)

Component lifecycle mismanagement:

| Check Item | Representative Case | Context |
|-----------|-------------------|---------|
| `unbind()` called after widget replacement | `GH-2d5fcd9e` | Must unbind before replace in HtmlNativeComponent |
| UI modification during rendering phase | `GH-6634ffa1` | Check `_ending` flag before mutations |
| ID space cleanup on partial detach | `GH-1a979afd` | Component detached from page but still has parent |
| ViewModel destroy callbacks not invoked | `ZK-aaf60831` | Window/Include detach/destroy lifecycle |
| Firing events on detached components | `HD-12998` | Causes infinite loop instead of graceful error |
| UiVisualizer redundant remove operations | `GH-4e95a241` | Missing `_detached` tracking map |

---

## DATA_BINDING_MISMATCH (2,641 cases)

MVVM data binding issues:

| Check Item | Representative Case | Context |
|-----------|-------------------|---------|
| `@load` skips reload when VM value unchanged | `JR-ZK-5268` | UI value stale due to pending unsaved changes |
| `NotifyChange` on setter not triggering | `JR-ZK-5307` | SmartNotifyChange/NotifyChange on setter methods |
| Multiple `@init` / `@load` on same property | `JR-ZK-5290` | Conflicting initialization causing wrong field update |
| `notifyChange` cascading null to unrelated properties | `JR-ZK-2923` | Only refresh explicitly specified property |
| Type mismatch: `int[]` vs `Integer[]` | `GH-6c6bc73d` | Field type must match getter return type |
| `@init` used instead of `@load` for dynamic collection | `GH-216fc96f` | forEach loop needs @load for runtime changes |
| Client MVVM binding not refreshed after invalidation | `JR-ZK-5370` | Server-side invalidate() must propagate to client binding |
| Duplicate SaveBinding operations | `JR-ZK-5199` | Duplicate event registration in Client MVVM |

---

## EVENT_LISTENER_GHOST (765 cases)

Stale/orphaned event listeners:

| Check Item | Representative Case | Context |
|-----------|-------------------|---------|
| `_listenFlex` / `_unlistenFlex` condition mismatch | `JR-ZK-5800` | Different conditions cause listener set mismatch |
| `zWatch.unlisten` wrong format (object vs array) | `GH-282bc796` | Must use `[widget, listener]` array format |
| Method name casing: `unListen` vs `unlisten` | `GH-6068a75f` | Incorrect capitalization causes silent failure |
| DOM element identity inconsistency in listener map | `GH-3d075b14` | Use stable identifier, not direct element reference |
| `zWatch.unlisten` stops at first match | `JR-ZK-3605` | Must continue searching all matching entries |
| Mask effect incorrect unregistration | `GH-282bc796` | Pass array, not listener object directly |

---

## RESOURCE_CLEANUP (666 cases)

Resource leaks:

| Check Item | Representative Case | Context |
|-----------|-------------------|---------|
| JDBC connection not closed after report | `GH-af3d84d5` | Add close() in finally block for Jasperreport |
| BufferedReader not closed | `GH-ef8a3191` | Use try-with-resources |
| Executor thread pool not shut down | `GH-e7ea4dc4` | Store reference, call shutdown() in cleanup |
| Scripting interpreter missing `destroy()` | `GH-5c68edd9` | Override destroy() to nullify internal fields |
| DataBinder orphaned BindingNode | `JR-ZK-4080` | Traverse and remove from `_beanSameNodes` |
| DesktopEventQueue leaked listeners | `ZK-b8bbffc0` | Unregister ListenerInfo on component destroy |

---

## CONFIG_MISMATCH (1,331 cases)

Configuration errors:

| Check Item | Representative Case | Context |
|-----------|-------------------|---------|
| Component name typo in config.properties | `GH-1619db27` | 'Gird' instead of 'Grid' |
| Component name casing inconsistency | `GH-0b927c11` | Use PascalCase consistently |
| config-name mismatch in module metadata | `GH-5a6d3ecc` | zk.xml config-name vs actual module name |
| Property name missing suffix | `GH-f9722c28` | Missing `.class` suffix in Javadoc |
| Version string casing inconsistency | `GH-21792696` | Lowercase vs uppercase in build.setting |

---

## SECURITY_XSS (cross-cutting)

Security vulnerabilities (currently tagged as UNKNOWN or NPE in CaseFoundry):

| Check Item | Representative Case | Context |
|-----------|-------------------|---------|
| HTTP header injection | `JR-ZK-2464` | Unsanitized user input in response headers |
| Error message injected into JavaScript | `JR-ZK-1897` | Direct concatenation into JS alert |
| URL path injection into JS string | `JR-ZK-5445` | URL path echoed unmodified into script |
| File upload XSS | `JR-ZK-1720` | Uploaded content rendered without sanitization |
| Component attribute XSS | `JR-ZK-5182` | Attributes not encoded before HTML rendering |
| fileupload.html.dsp URL parameter injection | `JR-ZK-3899` | Query parameter rendered directly into HTML |
