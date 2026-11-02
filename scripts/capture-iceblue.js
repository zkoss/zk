'use strict';

// Playwright-based baseline capture for iceblue (default ZK theme).
// Called by render-iceblue-baseline.sh — not intended for direct use.

const { chromium } = require('@playwright/test');
const path = require('path');
const fs = require('fs');

const [,, baseUrl, outputDir, ...components] = process.argv;

if (!baseUrl || !outputDir || components.length === 0) {
  console.error('Usage: node capture-iceblue.js <baseUrl> <outputDir> <comp>...');
  process.exit(1);
}

fs.mkdirSync(outputDir, { recursive: true });

(async () => {
  const browser = await chromium.launch();
  const context = await browser.newContext({ viewport: { width: 1280, height: 900 } });
  // zkthemelab serves IceBlue per request when the zktheme=iceblue cookie is set
  // (ThemeFns.getCurrentTheme reads it before org.zkoss.theme.preferred).
  await context.addCookies([{ name: 'zktheme', value: 'iceblue', url: baseUrl }]);
  const page = await context.newPage();

  for (const comp of components) {
    const url = `${baseUrl}/${comp}.zul`;
    console.log(`Capturing ${comp} from ${url}...`);
    try {
      await page.goto(url, { waitUntil: 'networkidle', timeout: 30000 });
      // StandardThemeProvider puts the theme into the zk.wcs URL (/_zkiju-iceblue/...). Without it the
      // server rendered Marble, and saving that as the IceBlue baseline would be wrong.
      const iceblue = await page.evaluate(() => [...document.styleSheets]
        .some((s) => /\/_zkiju-iceblue\//.test(s.href ?? '')));
      if (!iceblue) throw new Error('the page did not load the IceBlue theme (no /_zkiju-iceblue/ stylesheet)');
      const outPath = path.join(outputDir, `${comp}-iceblue.png`);
      await page.screenshot({ path: outPath, fullPage: true });
      console.log(`  saved: ${outPath}`);
    } catch (err) {
      console.error(`  ERROR capturing ${comp}: ${err.message}`);
      process.exitCode = 1;
    }
  }

  await browser.close();
})();
