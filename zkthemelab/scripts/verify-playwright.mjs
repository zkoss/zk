#!/usr/bin/env node
// The fixed Marble Playwright verification, and a report of only the NEW failures. Usage and the
// known-failures file: README.md ("Verify Marble with one command").
//
// Two runs, because only some projects compare screenshots:
//   run 1: chromium + compact, screenshots compared (--update-snapshots=none)
//   run 2: every other project except forced-colors-gallery, screenshots ignored
// The script takes no -g, no --update-snapshots and no project list on purpose: a narrowed or
// rewriting run is not the verification (verification.md, "Running Playwright - flags that bite").
import { spawnSync } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ZKTHEMELAB_DIR = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const PLAYWRIGHT_CONFIG = 'src/test/playwright/playwright.config.ts';
const KNOWN_FAILURES_FILE = path.join(ZKTHEMELAB_DIR, 'src/test/playwright/known-failures.json');
const DEFAULT_OUT_DIR = path.join(ZKTHEMELAB_DIR, 'test-results/verify');
const DEFAULT_PREVIEW_URL = 'http://127.0.0.1:8085';

const SCREENSHOT_PROJECTS = ['chromium', 'compact'];
// Writes PNGs into doc/screenshots instead of comparing against them.
const NEVER_RUN_PROJECTS = ['forced-colors-gallery'];
// Each one rewrites a tracked baseline, loosens a comparison or writes files when set.
const UNSAFE_ENV = ['UPDATE_FONT_BASELINE', 'FOCUS_SCAN_UPDATE', 'POPUP_MAX_DIFF', 'POPUP_THRESHOLD', 'CSS_COVERAGE'];
// The tracked directories an --out must not land in.
const PROTECTED_DIRS = ['doc', 'src'].map((dir) => path.join(ZKTHEMELAB_DIR, dir));
const URL_TIMEOUT_MS = 5000;
// A dead server fails every test; rerunning each of them would take hours.
const MAX_RERUNS = 10;

const OPTION_NAMES = { '--url': 'url', '--out': 'out', '--report-only': 'reportOnly' };

const USAGE = `Usage: node scripts/verify-playwright.mjs [--url <preview url>] [--out <dir>] [spec file ...]
       node scripts/verify-playwright.mjs --report-only <dir>
  --url          preview server (default: $PREVIEW_URL, else ${DEFAULT_PREVIEW_URL})
  --out          output directory (default: test-results/verify)
  --report-only  compare run1.json and run2.json in <dir> (an earlier --out); runs nothing, takes no --out
  spec file      paths that narrow both runs, e.g. src/test/playwright/compact-density.spec.ts (a partial run, not the verification)`;

// The real path of `target` even when it does not exist yet: the nearest existing ancestor is resolved
// (symlinks included) and the missing rest appended. Case is ignored where the file system is
// case-insensitive, so `--out DOC` cannot slip past the guard.
function realPathOf(target) {
  const missing = [];
  let existing = path.resolve(target);
  while (!fs.existsSync(existing)) {
    missing.unshift(path.basename(existing));
    existing = path.dirname(existing);
  }
  const resolved = path.join(fs.realpathSync(existing), ...missing);
  return process.platform === 'darwin' || process.platform === 'win32' ? resolved.toLowerCase() : resolved;
}

function isInside(dir, target) {
  return !path.relative(realPathOf(dir), realPathOf(target)).startsWith('..');
}

function fail(message, exitCode = 2) {
  console.error(`verify-playwright: ${message}`);
  process.exit(exitCode);
}

function parseArgs(argv) {
  const options = { files: [] };
  for (let i = 0; i < argv.length; i++) {
    const arg = argv[i];
    if (arg in OPTION_NAMES) {
      const value = argv[++i];
      if (value === undefined) fail(`${arg} needs a value\n${USAGE}`);
      options[OPTION_NAMES[arg]] = value;
    } else if (arg.startsWith('-')) {
      fail(`unknown option ${arg}\n${USAGE}`);
    } else {
      options.files.push(arg);
    }
  }
  return options;
}

function playwright(args, env = {}) {
  return spawnSync('npx', ['playwright', 'test', '--config', PLAYWRIGHT_CONFIG, ...args], {
    cwd: ZKTHEMELAB_DIR,
    env: { ...process.env, ...env },
    stdio: 'inherit',
  });
}

function listProjects() {
  const result = spawnSync('npx', ['playwright', 'test', '--config', PLAYWRIGHT_CONFIG, '--list', '--reporter=json'], {
    cwd: ZKTHEMELAB_DIR,
    encoding: 'utf8',
    maxBuffer: 256 * 1024 * 1024,
  });
  try {
    return JSON.parse(result.stdout).config.projects.map((project) => project.name);
  } catch {
    return fail(`could not list the Playwright projects:\n${result.stderr}`);
  }
}

async function requirePreviewAnswers(url) {
  try {
    const response = await fetch(url, { redirect: 'manual', signal: AbortSignal.timeout(URL_TIMEOUT_MS) });
    if (response.status >= 400) fail(`the preview server at ${url} answered HTTP ${response.status}; check the URL or pass --url`);
  } catch (error) {
    fail(`the preview server does not answer at ${url} (${error.cause?.code ?? error.message}); start it or pass --url`);
  }
}

// One Playwright run. `extraArgs` are the flags that differ between the runs; the same ones are reused to rerun a test.
function runPlaywright({ projects, extraArgs, files, outDir, name, previewUrl }) {
  const jsonFile = path.join(outDir, `${name}.json`);
  // A report left by an earlier run must never be read as this run's result.
  fs.rmSync(jsonFile, { force: true });
  const args = [
    '--update-snapshots=none',
    ...extraArgs,
    ...projects.map((project) => `--project=${project}`),
    '--reporter=list,json',
    `--output=${path.join(outDir, `${name}-output`)}`,
    ...files,
  ];
  const result = playwright(args, { PREVIEW_URL: previewUrl, PLAYWRIGHT_JSON_OUTPUT_FILE: jsonFile });
  // A non-zero status is normal when tests failed; a signal or a spawn error means Playwright died.
  if (result.error || result.signal) fail(`Playwright ${name} died (${result.error?.message ?? result.signal}); no result`, 1);
  return jsonFile;
}

// Flattens a JSON report to { key, project, file, title, line, outcome }. The title is the describe
// titles and the test title joined with " › ", as `playwright test --list` prints them.
function readResults(jsonFile) {
  let report;
  try {
    report = JSON.parse(fs.readFileSync(jsonFile, 'utf8'));
  } catch (error) {
    fail(`no readable Playwright JSON report at ${jsonFile} (${error.message})`, 1);
  }
  if (report.errors.length > 0) {
    fail(`Playwright reported errors:\n${report.errors.map((e) => e.message).join('\n')}`, 1);
  }
  const runName = path.basename(jsonFile, '.json');
  const results = [];
  const collect = (suite, file, titles) => {
    for (const child of suite.suites ?? []) collect(child, file, [...titles, child.title]);
    for (const spec of suite.specs ?? []) {
      for (const test of spec.tests) {
        // Playwright stops gracefully on SIGINT: valid JSON, a non-zero status and no signal.
        if (test.results.some((result) => result.status === 'interrupted')) fail(`${runName} was interrupted; no result`, 1);
        const title = [...titles, spec.title].join(' › ');
        results.push({
          key: testKey(test.projectName, file, title),
          project: test.projectName,
          file,
          title,
          line: spec.line,
          outcome: test.status === 'unexpected' ? 'failed' : test.status === 'skipped' ? 'skipped' : 'passed',
        });
      }
    }
  };
  for (const fileSuite of report.suites) collect(fileSuite, fileSuite.file, []);
  return results;
}

const testKey = (project, file, title) => `${project} › ${file} › ${title}`;

function readKnownFailures() {
  const entries = JSON.parse(fs.readFileSync(KNOWN_FAILURES_FILE, 'utf8'));
  return new Map(entries.map((entry) => [testKey(entry.project, entry.file, entry.title), entry]));
}

function countOutcomes(results) {
  const count = (outcome) => results.filter((result) => result.outcome === outcome).length;
  return `${count('passed')} passed, ${count('failed')} failed, ${count('skipped')} skipped`;
}

// Reruns one failed test with the flags of its run, to tell a flaky test from a real failure.
function rerunStillFails(failure, rerunContext, index) {
  const { name, outDir, ...runContext } = rerunContext;
  const rerunName = `${name}-rerun-${index}`;
  const jsonFile = runPlaywright({
    ...runContext,
    projects: [failure.project],
    files: [`src/test/playwright/${failure.file}:${failure.line}`],
    outDir,
    name: rerunName,
  });
  const rerun = readResults(jsonFile).find((result) => result.key === failure.key);
  return rerun?.outcome === 'failed';
}

function printSection(heading, lines) {
  if (lines.length === 0) return;
  console.log(`\n${heading}`);
  lines.forEach((line) => console.log(`  ${line}`));
}

function compareWithKnownFailures(runs, known, rerunFor) {
  if (runs.every(({ results }) => results.length === 0)) {
    console.log('FAILED: the runs ran 0 tests; check the spec file paths');
    return 1;
  }
  const newFailures = [];
  const expectedFailed = [];
  const expectedPassed = [];
  const intermittentFailed = [];

  for (const { label, results } of runs) {
    console.log(`${label}: ${countOutcomes(results)}`);
    for (const result of results) {
      const entry = known.get(result.key);
      if (result.outcome === 'failed') {
        if (!entry) newFailures.push({ ...result, rerunContext: rerunFor(label) });
        else (entry.kind === 'intermittent' ? intermittentFailed : expectedFailed).push(result.key);
      } else if (result.outcome === 'passed' && entry?.kind === 'expected') {
        expectedPassed.push(result.key);
      }
    }
  }

  const newFailureLines = newFailures.map((failure, index) => {
    if (index >= MAX_RERUNS) return `${failure.key}  (not rerun)`;
    if (!failure.rerunContext) return failure.key;
    const verdict = rerunStillFails(failure, failure.rerunContext, index) ? 'failed again' : 'PASSED on rerun, flaky?';
    return `${failure.key}  [rerun: ${verdict}]`;
  });
  printSection('NEW failures (not in known-failures.json):', newFailureLines);
  printSection('Known failures that failed as expected:', expectedFailed);
  printSection('Known "expected" failures that PASSED (remove them from known-failures.json):', expectedPassed);
  printSection('Intermittent tests that failed:', intermittentFailed);

  console.log(newFailures.length === 0 ? '\nOK: no new failure' : `\nFAILED: ${newFailures.length} new failure(s)`);
  return newFailures.length === 0 ? 0 : 1;
}

async function main() {
  const options = parseArgs(process.argv.slice(2));
  if (options.reportOnly && options.out) fail(`--report-only takes its directory as its value and no --out\n${USAGE}`);
  const outDir = path.resolve(options.reportOnly ?? options.out ?? DEFAULT_OUT_DIR);
  const known = readKnownFailures();

  if (options.reportOnly) {
    const runs = ['run1', 'run2'].map((name) => ({ label: name, results: readResults(path.join(outDir, `${name}.json`)) }));
    process.exit(compareWithKnownFailures(runs, known, () => null));
  }

  const unsafeEnv = UNSAFE_ENV.filter((variable) => process.env[variable] !== undefined);
  if (unsafeEnv.length > 0) fail(`${unsafeEnv.join(', ')} is set; it writes baselines or files, or loosens the comparison. Unset it.`);
  const protectedDir = PROTECTED_DIRS.find((dir) => isInside(dir, outDir));
  if (protectedDir) fail(`--out ${outDir} is inside ${protectedDir}; choose a directory outside doc and src`);

  const previewUrl = options.url ?? process.env.PREVIEW_URL ?? DEFAULT_PREVIEW_URL;
  await requirePreviewAnswers(previewUrl);

  const otherProjects = listProjects().filter(
    (project) => !SCREENSHOT_PROJECTS.includes(project) && !NEVER_RUN_PROJECTS.includes(project));
  // A path filter that matches nothing in a project would otherwise fail the whole run.
  const narrowing = options.files.length > 0 ? ['--pass-with-no-tests'] : [];
  fs.mkdirSync(outDir, { recursive: true });

  const runContexts = {
    run1: { name: 'run1', projects: SCREENSHOT_PROJECTS, extraArgs: narrowing },
    run2: { name: 'run2', projects: otherProjects, extraArgs: ['--ignore-snapshots', ...narrowing] },
  };
  const partial = options.files.length > 0;
  const partialNotice = 'PARTIAL run (narrowed by spec file): this is not the full verification.';
  if (partial) console.log(`${partialNotice}\n`);
  const runs = Object.values(runContexts).map((context) => {
    const jsonFile = runPlaywright({ ...context, files: options.files, outDir, previewUrl });
    return { label: context.name, results: readResults(jsonFile) };
  });

  const exitCode = compareWithKnownFailures(runs, known, (label) => ({
    ...runContexts[label], outDir, previewUrl,
  }));
  if (partial) console.log(`\n${partialNotice}`);
  process.exit(exitCode);
}

main();
