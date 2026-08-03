---
paths: "*/src/main/resources/web/js/**/*.ts"
description: ZK TypeScript widget conventions
---

# TypeScript Widget Rules

- `@zk.WrapClass('module.ClassName')` decorator registers the widget class
- Extend base widget: `zul.Widget`, `zul.mesh.MeshWidget`, etc.
- Private fields prefixed with `_`: `_value`, `_disabled`, `_label`
- Setters return `this` and accept `opts?: Record<string, boolean>` for `force` flag
- Check `this.desktop` before any DOM operations
- `$n()` = root DOM element, `$n('sub')` = sub-node by suffix
- `fire(eventName, data)` sends AU request to server-side `service()`
- Use `rerender()` sparingly — prefer direct DOM manipulation in setters
