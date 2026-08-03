# ZK Framework — AI Development Guide

## Project Overview
ZK is an open-source Java web framework. The sibling directory `../zkcml/` contains enterprise extensions that depend on this project.

**Modules:**
- `zk/`, `zul/`, `zkbind/`, `zhtml/` — core framework (TypeScript + Java)
- `zcommon/`, `zel/`, `zweb/`, `zweb-dsp/`, `zkplus/` — shared utilities
- `zktest/` — Selenium integration tests
- `eslint-plugin-zk/` — custom ESLint rules

## Version

**Always read the current version dynamically** from `gradle.properties` — never hardcode.
- `zk/gradle.properties` (CE)
- `zkcml/gradle.properties` (EE — at `../zkcml/`)

The active development version ends in `-SNAPSHOT`. Both files must always stay in sync.

Version code mapping (used in file naming): `10.0.0` → `100`, `10.4.0` → `104`, `11.0.0` → `110`.

To update version:
```bash
./gradlew upVer -PchangeVersionTo=X.Y.Z
./gradlew versionCheck -Pcheck.version=X.Y.Z
```

## Tech Stack
- **Frontend:** TypeScript 5.3.3 + JavaScript, Gulp 5 + Webpack 5
- **Backend:** Java 11, Gradle
- **CI:** GitHub Actions (`.github/workflows/`)

## Build Commands

### TypeScript / JavaScript
```bash
npm run build        # build all TS/JS (gulp)
npm run dev          # watch mode
npm run type-check   # type check only, no output
npm run lint -- .    # ESLint on .js and .ts files (path argument required)
```

### Java
```bash
./gradlew clean build         # full build
./gradlew checkstyleMain      # Java style check only
./gradlew :zk:build           # single module build
./gradlew publishToMavenLocal # publish to local Maven so zktest picks up latest changes
```

## Code Style

### TypeScript / JavaScript
- ESLint config: `.eslintrc.js` (root)
- Custom rules: `eslint-plugin-zk/` — do not disable without review
- Microsoft SDL plugin is enabled — security patterns are enforced
- **ESLint errors block CI** — fix all errors before committing

### Java
- Checkstyle config: `config/checkstyle/`
- Violations block CI

## Testing Requirements

**Do NOT use the VS Code IDE's built-in test runner (▶ play button).** It bypasses Gradle's resource processing, leaving `@version@` placeholders unresolved, causing `Language not found: xul/html` errors. Always use the CLI commands below.

**Every bug fix and new feature MUST include a test case.**

### How to Run Tests

```bash
# Run a single test class (from project root)
cd zktest && ./gradlew test --tests "org.zkoss.zktest.zats.test2.B104_ZK_6047Test" -PmaxParallelForks=1 --console=plain --no-daemon

# If the test has @ForkJVMTestOnly or @Tag("ForkJVMTestOnly") annotation (requires Docker)
cd zktest && ./gradlew testGroupForkJVMTestOnly --tests "org.zkoss.zktest.zats.test2.B101_ZK_5716Test" -PmaxParallelForks=1 --console=plain --no-daemon

# Full test suite (excludes WCAG and ForkJVMTestOnly)
cd zktest && ./gradlew test

# WCAG / accessibility tests (requires Lighthouse)
cd zktest && ./gradlew testWCAGOnly
```

**Important:** Check the test file for `@ForkJVMTestOnly` or `@Tag("ForkJVMTestOnly")` annotation to decide whether to use `test` or `testGroupForkJVMTestOnly`.

### Location & Naming Convention
- Path: `zktest/src/test/java/org/zkoss/zktest/zats/test2/`
- Pattern: `B{majorVersion}_{IssueId}Test.java`
- Example: `B100_ZK_5529Test.java` → ZK 10.x, issue ZK-5529

### Test Template
```java
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B100_ZK_5529Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();
        // jq() for jQuery-style selectors
        assertTrue(jq(".z-component").exists());
    }
}
```

- Extends `org.zkoss.test.webdriver.WebDriverTestCase`
- For Selenium actions, use `Actions action = getActions();` instead of instantiating `new Actions(...)` directly
- For drag interactions, do not use `dragAndDropBy`; write the sequence explicitly with `action.clickAndHold(...)` and the follow-up move/release calls
- A corresponding ZUL page must be added under `zktest/src/main/webapp/test2/`
  - ZUL naming uses dashes: `B100-ZK-5529.zul` (Java test uses underscores)
- After adding the ZUL page, register it in `zktest/src/main/webapp/test2/config.properties`:
  ```
  B100-ZK-5529.zul=A,M,ComponentName
  ```
  - Field 1 — Code Level: `A`=Important, `B`=Unknown, `C`=Unimportant
  - Field 2 — UX Level: `H`=Hard, `M`=Middle, `E`=Easy
  - Field 3+ — affected component names (e.g., `Tree`, `Listbox`, `Grid`)

## Source Layout
- TS source: `{module}/src/main/resources/web/js/`
- Java source: `{module}/src/main/java/org/zkoss/`

## Critical Constraints
- Do NOT change public Java APIs in `zk/`, `zul/`, or `zkbind/` without checking impact on `../zkcml/`
- If any ZUL component attribute or element is added/removed/changed, update `zul/src/main/resources/metainfo/xml/zul.xsd` accordingly
- Issue tracker: https://tracker.zkoss.org/projects/ZK

## Workflow for Each Issue
1. Find or create the issue in tracker
2. Write the test first in `zktest/` using the issue ID in the filename
3. Add the corresponding ZUL test page in `zktest/src/main/webapp/test2/`
4. Register the ZUL page in `zktest/src/main/webapp/test2/config.properties`
5. Implement the fix
6. If ZUL component API changed (attribute/element added or removed), update `zul/src/main/resources/metainfo/xml/zul.xsd`
7. Run `npm run lint -- . && ./gradlew checkstyleMain`
8. Verify the test passes
9. PR title must reference the issue ID
