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
  const page = await browser.newPage({ viewport: { width: 1280, height: 900 } });

  for (const comp of components) {
    const url = `${baseUrl}/${comp}.zul`;
    console.log(`Capturing ${comp} from ${url}...`);
    try {
      await page.goto(url, { waitUntil: 'networkidle', timeout: 30000 });
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
