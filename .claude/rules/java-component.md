---
paths: "*/src/main/java/**/*.java"
description: ZK Java component conventions
---

# Java Component Rules

- `smartUpdate("propName", value)` in setters pushes changes to client widget
- Override `renderProperties(ContentRenderer)` for initial property rendering
- Override `service(AuRequest, boolean)` to handle client events
- Register client events in static block: `addClientEvent(Class, eventName, flags)`
- Optional properties use inner `Auxinfo` class (lazy-init) to save memory
- Public API changes in `zk/`, `zul/`, `zkbind/` MUST be checked against `../zkcml/`
- New/changed ZUL attributes require updating `zul/src/main/resources/metainfo/xml/zul.xsd`
