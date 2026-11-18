// verify-playwright.mjs, driven as a child process against a fake `npx` and a local HTTP server. The script is
// copied into a sandbox that has the directory layout it derives its paths from, so its known-failures file and
// its protected doc/ and src/ directories are the sandbox's, not the repository's.
// VERIFY_PLAYWRIGHT_SCRIPT points the tests at another revision of the script.
import assert from 'node:assert/strict';
import fs from 'node:fs';
import http from 'node:http';
import path from 'node:path';
import { after, before, describe, it } from 'node:test';
import { installFakeNpx, makeTempDir, readInvocations, removeTempDir, run, scriptUnderTest } from './helpers.mjs';

const SCRIPT = scriptUnderTest('verify-playwright.mjs', 'VERIFY_PLAYWRIGHT_SCRIPT');
const PLAYWRIGHT_PREFIX = ['playwright', 'test', '--config', 'src/test/playwright/playwright.config.ts'];
const ALL_PROJECTS = ['chromium', 'compact', 'forced-colors-gallery', 'smoke', 'popup', 'tablet'];
const UNSAFE_ENV = ['UPDATE_FONT_BASELINE', 'FOCUS_SCAN_UPDATE', 'POPUP_MAX_DIFF', 'POPUP_THRESHOLD', 'CSS_COVERAGE'];

let tempRoot;
let previewServer;
let previewStatus = 200;
let previewUrl;
let deadUrl;

const listen = (server) => new Promise((resolve) => server.listen(0, '127.0.0.1', () => resolve(server.address().port)));

before(async () => {
  tempRoot = makeTempDir();
  previewServer = http.createServer((request, response) => {
    response.statusCode = previewStatus;
    response.end('preview');
  });
  previewUrl = `http://127.0.0.1:${await listen(previewServer)}`;
  const closed = http.createServer();
  const closedPort = await listen(closed);
  await new Promise((resolve) => closed.close(resolve));
  deadUrl = `http://127.0.0.1:${closedPort}`;
});

after(async () => {
  await new Promise((resolve) => previewServer.close(resolve));
  removeTempDir(tempRoot);
});

// What the JSON reporter writes for `tests`: [{ project, file, title: 'describe › test', line, status }].
function reportOf(tests, errors = []) {
  const suites = [];
  for (const { project, file, title, line = 1, status = 'expected', resultStatus = status === 'unexpected' ? 'failed' : 'passed' } of tests) {
    const parts = title.split(' › ');
    let fileSuite = suites.find((suite) => suite.file === file);
    if (!fileSuite) suites.push((fileSuite = { title: file, file, suites: [], specs: [] }));
    let suite = fileSuite;
    for (const describeTitle of parts.slice(0, -1)) {
      let child = suite.suites.find((candidate) => candidate.title === describeTitle);
      if (!child) suite.suites.push((child = { title: describeTitle, suites: [], specs: [] }));
      suite = child;
    }
    suite.specs.push({ title: parts.at(-1), line, tests: [{ projectName: project, status, results: [{ status: resultStatus }] }] });
  }
  return { errors, suites };
}

const passing = (project, file, title, extra = {}) => ({ project, file, title, ...extra });
const failing = (project, file, title, extra = {}) => ({ project, file, title, status: 'unexpected', ...extra });
const keyOf = ({ project, file, title }) => `${project} › ${file} › ${title}`;

function createSandbox({ known = [], runs = {}, projects = ALL_PROJECTS } = {}) {
  const root = fs.mkdtempSync(path.join(tempRoot, 'sandbox-'));
  const zkthemelab = path.join(root, 'zkthemelab');
  fs.mkdirSync(path.join(zkthemelab, 'scripts'), { recursive: true });
  fs.mkdirSync(path.join(zkthemelab, 'src/test/playwright'), { recursive: true });
  fs.mkdirSync(path.join(zkthemelab, 'doc'));
  fs.copyFileSync(SCRIPT, path.join(zkthemelab, 'scripts/verify-playwright.mjs'));
  fs.writeFileSync(path.join(zkthemelab, 'src/test/playwright/known-failures.json'), JSON.stringify(known));
  installFakeNpx(path.join(root, 'bin'));
  const configFile = path.join(root, 'fake-npx.json');
  const logFile = path.join(root, 'fake-npx.log');
  fs.writeFileSync(configFile, JSON.stringify({ projects, runs }));
  const out = path.join(root, 'out');

  const env = { ...process.env, PATH: `${path.join(root, 'bin')}${path.delimiter}${process.env.PATH}`, FAKE_NPX_CONFIG: configFile, FAKE_NPX_LOG: logFile };
  for (const variable of [...UNSAFE_ENV, 'PREVIEW_URL', 'PLAYWRIGHT_JSON_OUTPUT_FILE']) delete env[variable];

  return {
    root, zkthemelab, out,
    verify: (args = [], extraEnv = {}) => run(process.execPath, [path.join(zkthemelab, 'scripts/verify-playwright.mjs'), ...args], {
      cwd: root, env: { ...env, ...extraEnv },
    }),
    // The Playwright runs (not the --list call), as the argument lists after `npx`.
    runs: () => readInvocations(logFile).filter((call) => !call.args.includes('--list')),
    calls: () => readInvocations(logFile),
  };
}

// The sandbox's first run is `run1`, its second `run2`; each argument list is checked on its own below.
const optionValues = (args, name) => args.filter((arg) => arg.startsWith(`${name}=`)).map((arg) => arg.slice(name.length + 1));
const projectsOf = (args) => optionValues(args, '--project');

describe('a run that cannot be trusted never reports OK', () => {
  const STALE_REPORT = reportOf([passing('chromium', 'a.spec.ts', 'passes')]);

  function sandboxWithStaleReports(runs) {
    const sandbox = createSandbox({ runs });
    fs.mkdirSync(sandbox.out, { recursive: true });
    for (const name of ['run1', 'run2']) fs.writeFileSync(path.join(sandbox.out, `${name}.json`), JSON.stringify(STALE_REPORT));
    return sandbox;
  }

  it('does not read the report of an earlier run when Playwright is killed', async () => {
    const sandbox = sandboxWithStaleReports({ run1: { kill: true }, run2: { kill: true } });
    const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out]);
    assert.equal(result.status, 1, result.stdout + result.stderr);
    assert.doesNotMatch(result.stdout, /OK/);
    assert.match(result.stderr, /died/);
  });

  it('does not read the report of an earlier run when Playwright exits without writing one', async () => {
    const sandbox = sandboxWithStaleReports({ run1: { noReport: true, exitCode: 1 }, run2: { noReport: true, exitCode: 1 } });
    const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out]);
    assert.equal(result.status, 1, result.stdout + result.stderr);
    assert.doesNotMatch(result.stdout, /OK/);
    assert.match(result.stderr, /no readable Playwright JSON report/);
  });

  it('fails when a result was interrupted', async () => {
    const interrupted = reportOf([passing('chromium', 'a.spec.ts', 'stopped', { resultStatus: 'interrupted' })]);
    const sandbox = createSandbox({ runs: { run1: { report: interrupted } } });
    const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out]);
    assert.equal(result.status, 1, result.stdout + result.stderr);
    assert.doesNotMatch(result.stdout, /OK/);
    assert.match(result.stderr, /interrupted/);
  });

  it('fails when both runs ran no test', async () => {
    const sandbox = createSandbox();
    const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out, 'src/test/playwright/matches-nothing.spec.ts']);
    assert.equal(result.status, 1, result.stdout + result.stderr);
    assert.doesNotMatch(result.stdout, /OK/);
    assert.match(result.stdout, /ran 0 tests/);
  });

  it('fails when a report holds top-level errors', async () => {
    const withErrors = reportOf([passing('chromium', 'a.spec.ts', 'passes')], [{ message: 'config could not be loaded' }]);
    const sandbox = createSandbox({ runs: { run1: { report: withErrors } } });
    const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out]);
    assert.equal(result.status, 1, result.stdout + result.stderr);
    assert.doesNotMatch(result.stdout, /OK/);
    assert.match(result.stderr, /config could not be loaded/);
  });
});

describe('classification against known-failures.json', () => {
  const newFailure = failing('chromium', 'a.spec.ts', 'suite › new failure', { line: 12 });
  const expectedFailure = failing('chromium', 'a.spec.ts', 'expected failure');
  const expectedButPassing = passing('chromium', 'a.spec.ts', 'expected but passing');
  const intermittentFailure = failing('compact', 'b.spec.ts', 'intermittent failure');
  const KNOWN = [
    { project: 'chromium', file: 'a.spec.ts', title: 'expected failure', kind: 'expected', reason: 'r' },
    { project: 'chromium', file: 'a.spec.ts', title: 'expected but passing', kind: 'expected', reason: 'r' },
    { project: 'compact', file: 'b.spec.ts', title: 'intermittent failure', kind: 'intermittent', reason: 'r' },
  ];

  const section = (stdout, heading) => {
    const start = stdout.indexOf(heading);
    return start < 0 ? null : stdout.slice(start).split('\n\n')[0];
  };

  it('lists a new failure, reruns it once with the flags of its run and exits 1', async () => {
    const newInRun2 = failing('smoke', 'c.spec.ts', 'new in run 2', { line: 7 });
    const sandbox = createSandbox({
      known: KNOWN,
      runs: {
        run1: { report: reportOf([newFailure, expectedFailure]) },
        run2: { report: reportOf([newInRun2]) },
        'run1-rerun-0': { report: reportOf([newFailure]) },
        'run2-rerun-1': { report: reportOf([newInRun2]) },
      },
    });
    const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out]);

    assert.equal(result.status, 1, result.stdout + result.stderr);
    assert.match(section(result.stdout, 'NEW failures'), new RegExp(`${keyOf(newFailure)}  \\[rerun: failed again\\]`));
    assert.match(result.stdout, /FAILED: 2 new failure\(s\)/);

    const [, , rerun1, rerun2] = sandbox.runs();
    assert.deepEqual(rerun1.args, [
      ...PLAYWRIGHT_PREFIX, '--update-snapshots=none', '--project=chromium', '--reporter=list,json',
      `--output=${path.join(sandbox.out, 'run1-rerun-0-output')}`, 'src/test/playwright/a.spec.ts:12',
    ]);
    assert.deepEqual(rerun2.args, [
      ...PLAYWRIGHT_PREFIX, '--update-snapshots=none', '--ignore-snapshots', '--project=smoke', '--reporter=list,json',
      `--output=${path.join(sandbox.out, 'run2-rerun-1-output')}`, 'src/test/playwright/c.spec.ts:7',
    ]);
    assert.equal(sandbox.runs().length, 4, 'each new failure is rerun exactly once');
  });

  it('reports a failure that passes on its rerun, and still exits 1', async () => {
    const sandbox = createSandbox({
      runs: {
        run1: { report: reportOf([newFailure]) },
        'run1-rerun-0': { report: reportOf([passing('chromium', 'a.spec.ts', 'suite › new failure', { line: 12 })]) },
      },
    });
    const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out]);
    assert.equal(result.status, 1, result.stdout + result.stderr);
    assert.match(result.stdout, /PASSED on rerun, flaky\?/);
    assert.match(result.stdout, /FAILED: 1 new failure/);
  });

  it('exits 0 for known failures and lists what needs attention', async () => {
    const sandbox = createSandbox({
      known: KNOWN,
      runs: { run1: { report: reportOf([expectedFailure, expectedButPassing, intermittentFailure, passing('chromium', 'a.spec.ts', 'plain')]) } },
    });
    const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out]);

    assert.equal(result.status, 0, result.stdout + result.stderr);
    assert.match(result.stdout, /OK: no new failure/);
    assert.match(section(result.stdout, 'Known failures that failed as expected'), new RegExp(keyOf(expectedFailure)));
    assert.match(section(result.stdout, 'Known "expected" failures that PASSED'), new RegExp(keyOf(expectedButPassing)));
    assert.match(section(result.stdout, 'Intermittent tests that failed'), new RegExp(keyOf(intermittentFailure)));
    assert.equal(section(result.stdout, 'NEW failures'), null);
    assert.equal(sandbox.runs().length, 2, 'known failures are not rerun');
  });
});

describe('the arguments of the two runs', () => {
  it('compares screenshots in run 1 and ignores them in run 2', async () => {
    const sandbox = createSandbox({
      runs: {
        run1: { report: reportOf([passing('chromium', 'a.spec.ts', 'one')]) },
        run2: { report: reportOf([passing('smoke', 'a.spec.ts', 'two')]) },
      },
    });
    const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out]);
    assert.equal(result.status, 0, result.stdout + result.stderr);

    const [run1, run2] = sandbox.runs().map((call) => call.args);
    assert.deepEqual(run1.slice(0, 4), PLAYWRIGHT_PREFIX);
    assert.deepEqual(projectsOf(run1), ['chromium', 'compact']);
    assert.deepEqual(optionValues(run1, '--update-snapshots'), ['none']);
    assert.ok(!run1.includes('--ignore-snapshots'), 'run 1 compares screenshots');
    assert.deepEqual(projectsOf(run2), ['smoke', 'popup', 'tablet']);
    assert.deepEqual(optionValues(run2, '--update-snapshots'), ['none']);
    assert.ok(run2.includes('--ignore-snapshots'));
    for (const args of [run1, run2]) {
      assert.ok(!projectsOf(args).includes('forced-colors-gallery'));
      assert.ok(!args.includes('--update-snapshots'), 'no bare --update-snapshots');
      assert.equal(args.filter((arg) => arg.startsWith('--update-snapshots')).length, 1);
    }
  });
});

describe('refusals before any Playwright call', () => {
  const neverRuns = async (sandbox, result) => {
    assert.equal(result.status, 2, result.stdout + result.stderr);
    assert.doesNotMatch(result.stdout, /OK/);
    assert.deepEqual(sandbox.calls(), [], 'Playwright was not invoked');
  };

  for (const args of [['-g', 'x'], ['--grep=x'], ['--update-snapshots'], ['--project=chromium']]) {
    it(`refuses ${args.join(' ')}`, async () => {
      const sandbox = createSandbox();
      await neverRuns(sandbox, await sandbox.verify([...args, '--url', previewUrl, '--out', sandbox.out]));
    });
  }

  for (const variable of UNSAFE_ENV) {
    it(`refuses to run while ${variable} is set`, async () => {
      const sandbox = createSandbox();
      const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out], { [variable]: '1' });
      await neverRuns(sandbox, result);
      assert.match(result.stderr, new RegExp(variable));
    });
  }

  for (const protectedDir of ['doc', 'src']) {
    it(`refuses an --out inside ${protectedDir}`, async () => {
      const sandbox = createSandbox();
      const result = await sandbox.verify(['--url', previewUrl, '--out', path.join(sandbox.zkthemelab, protectedDir, 'verify')]);
      await neverRuns(sandbox, result);
      assert.match(result.stderr, /inside/);
    });

    it(`refuses an --out inside ${protectedDir} reached through a symlink`, async () => {
      const sandbox = createSandbox();
      const link = path.join(sandbox.root, `link-to-${protectedDir}`);
      fs.symlinkSync(path.join(sandbox.zkthemelab, protectedDir), link);
      const result = await sandbox.verify(['--url', previewUrl, '--out', path.join(link, 'verify')]);
      await neverRuns(sandbox, result);
    });
  }

  it('refuses an --out that differs only in case where the file system ignores case', async (t) => {
    const sandbox = createSandbox();
    if (!fs.existsSync(path.join(sandbox.zkthemelab, 'DOC'))) return t.skip('the file system is case-sensitive');
    const result = await sandbox.verify(['--url', previewUrl, '--out', path.join(sandbox.zkthemelab, 'DOC', 'verify')]);
    await neverRuns(sandbox, result);
  });

  it('refuses --report-only together with --out', async () => {
    const sandbox = createSandbox();
    await neverRuns(sandbox, await sandbox.verify(['--report-only', sandbox.out, '--out', path.join(sandbox.root, 'other')]));
  });

  it('refuses a preview URL that answers 404', async () => {
    const sandbox = createSandbox();
    previewStatus = 404;
    try {
      const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out]);
      await neverRuns(sandbox, result);
      assert.match(result.stderr, /HTTP 404/);
    } finally {
      previewStatus = 200;
    }
  });

  it('refuses a preview URL that does not answer', async () => {
    const sandbox = createSandbox();
    const result = await sandbox.verify(['--url', deadUrl, '--out', sandbox.out]);
    await neverRuns(sandbox, result);
    assert.match(result.stderr, /does not answer/);
  });
});

describe('a partial run', () => {
  it('prints the PARTIAL banner first and last', async () => {
    const sandbox = createSandbox({
      runs: {
        run1: { report: reportOf([passing('chromium', 'a.spec.ts', 'one')]) },
        run2: { report: reportOf([passing('smoke', 'a.spec.ts', 'two')]) },
      },
    });
    const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out, 'src/test/playwright/a.spec.ts']);
    assert.equal(result.status, 0, result.stdout + result.stderr);

    const lines = result.stdout.split('\n').filter(Boolean);
    assert.match(lines[0], /^PARTIAL run/);
    assert.match(lines.at(-1), /^PARTIAL run/);
    for (const call of sandbox.runs()) {
      assert.ok(call.args.includes('src/test/playwright/a.spec.ts'));
      assert.ok(call.args.includes('--pass-with-no-tests'));
    }
  });

  it('prints no banner for the full verification', async () => {
    const sandbox = createSandbox({ runs: { run1: { report: reportOf([passing('chromium', 'a.spec.ts', 'one')]) } } });
    const result = await sandbox.verify(['--url', previewUrl, '--out', sandbox.out]);
    assert.equal(result.status, 0, result.stdout + result.stderr);
    assert.doesNotMatch(result.stdout, /PARTIAL/);
  });
});
