// Drop-in replacement for '@playwright/test' that records Chromium CSS rule usage for every test.
//
// Used only by a coverage run, through `--tsconfig src/test/playwright/css-coverage/tsconfig.json`, whose `paths`
// maps '@playwright/test' here, so no spec file changes. See scripts/css-coverage.mjs for the full command.
// Without CSS_COVERAGE=1 this module re-exports the stock `test` and `chromium` untouched.
import { test as baseTest, chromium as baseChromium } from 'playwright/test';
import type { Browser } from 'playwright/test';
import { COVERAGE_ENABLED, beginTest, endTest, instrumentBrowser } from './collector';

export * from 'playwright/test';
export { default } from 'playwright/test';

export const test = COVERAGE_ENABLED
  ? baseTest.extend<{ cssCoverage: void }>({
      // `browser` is the worker's shared browser: touching its prototype once instruments every browser, context
      // and page of the worker, including those a test opens itself.
      cssCoverage: [async ({ browser }, use, testInfo) => {
        instrumentBrowser(browser);
        const record = beginTest(testInfo);
        await use();
        await endTest(record, testInfo);
      }, { auto: true }],
    })
  : baseTest;

// Some specs launch their own browser (mesh-header-footer frozen columns); its pages need the same instrumentation.
export const chromium = COVERAGE_ENABLED
  ? new Proxy(baseChromium, {
      get(target, property, receiver) {
        if (property !== 'launch') return Reflect.get(target, property, target);
        return async (...args: Parameters<typeof target.launch>): Promise<Browser> => {
          const browser = await target.launch(...args);
          instrumentBrowser(browser);
          return browser;
        };
      },
    })
  : baseChromium;
