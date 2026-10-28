import * as fs from 'fs';
import * as path from 'path';

// Pages that combine many components (scripts/aggregate-pages.json, also read by select-tests.mjs).
// The scan-driven specs leave them out; render-smoke.spec.ts still opens them.
export const AGGREGATE_PAGES: string[] = Object.keys(
  JSON.parse(fs.readFileSync(path.resolve(__dirname, '../../../scripts/aggregate-pages.json'), 'utf8')));
