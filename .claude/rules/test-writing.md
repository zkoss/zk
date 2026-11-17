---
paths:
  - "zktest/src/test/java/**/*Test.java"
  - "zktest/src/main/webapp/**/*.zul"
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
- Don't use a deprecated tag, attribute or API in a test or its page unless it is what the test covers.
  ZUL has no deprecation marker (`zul.xsd` and `lang.xml` list a deprecated tag like any other): a tag is
  deprecated when its component class (`zul/src/main/java/org/zkoss/zul/<Tag>.java`, EE under `../zkcml/zkex|zkmax`)
  carries `@deprecated`, an attribute when its setter does; that JavaDoc names the replacement.
