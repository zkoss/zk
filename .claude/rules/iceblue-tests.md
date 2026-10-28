---
paths: "zktest/**"
description: IceBlue-only tests are tagged and skipped while the default theme is Marble
---

# IceBlue-only tests

- ZK 11's default theme is Marble. IceBlue is still a supported theme, shipped separately, but the zktest suite runs on Marble.
- A test whose assertions hold only under IceBlue (pixel sizes, colours, fonts, `~./iceblue*` paths, Font Awesome glyphs) carries `@Tag("IceBlueOnly")` — on the class when every method depends on IceBlue, on the single method otherwise.
- `./gradlew test` and `testGroupForkJVMTestOnly` exclude that tag. `./gradlew testIceBlueOnly` runs only those tests, on IceBlue: zktest already depends on `org.zkoss.theme:iceblue` and the task sets `zktest.theme=iceblue`.
- To see IceBlue elsewhere: add `-Dzktest.theme=iceblue` to another test task, or open a page under `appRun` with `?zktheme=iceblue` (gretty runs its own JVM, so `-D` does not reach it).
- Never delete, rewrite or "fix" an IceBlue-only test to make it pass on Marble — tag it. A test that merely mentions IceBlue in a label, a comment or an unused theme switcher is NOT IceBlue-only and stays untagged so it can catch Marble regressions.
- When adding a test that depends on IceBlue, tag it the same way and say so in the test's header comment.
