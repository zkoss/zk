---
paths: "zktest/src/test/java/**/*Test.java"
description: ZK WebDriver test conventions and API reference
---

# Test API Quick Reference (WebDriverTestCase)

## Lifecycle
- `connect()` — load ZUL page (MUST call before getDriver())
- `connect(path)` — load specific path
- `waitResponse()` — wait for AU round-trip (call after EVERY interaction)

## Selectors & Assertions
- `jq(".z-button")` — jQuery selector, returns JQuery wrapper
- `.exists()`, `.text()`, `.css("prop")`, `.toWidget()`, `.find(sel)`
- `widget(jq)` — get ZK widget reference from JQuery
- `eval(js)` — execute JavaScript on client

## Interactions
- `click(widget)`, `type(widget, text)` — convenience methods
- `getActions()` — get Selenium Actions (NOT `new Actions(driver)`)
- Drag: `clickAndHold` -> `moveByOffset` -> `release` (NOT `dragAndDropBy`)

## Pitfalls
- Check for `@ForkJVMTestOnly` annotation -> use `testGroupForkJVMTestOnly` task
- Always `waitResponse()` between interaction and assertion
