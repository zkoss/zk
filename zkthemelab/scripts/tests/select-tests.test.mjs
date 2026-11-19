// select-tests.mjs and grep-titles-reporter.mjs against the real test inventory (`playwright test --list`, which
// starts no browser and needs no preview server). Every assertion is a relation between what the script answers
// and what Playwright itself lists, so none of them depends on how many tests the suite has.
// SELECT_TESTS_SCRIPT and GREP_TITLES_REPORTER point the tests at another revision of a script.
import assert from 'node:assert/strict';
import { before, describe, it } from 'node:test';
import { run, scriptUnderTest, ZKTHEMELAB_DIR } from './helpers.mjs';

const SELECT_TESTS = scriptUnderTest('select-tests.mjs', 'SELECT_TESTS_SCRIPT');
const GREP_TITLES_REPORTER = scriptUnderTest('grep-titles-reporter.mjs', 'GREP_TITLES_REPORTER');
const PLAYWRIGHT_CONFIG = 'src/test/playwright/playwright.config.ts';
const REVIEW_CAPTURE_PROJECT = 'forced-colors-gallery';

// A component CSS file whose selection mixes whole projects with single tests, some of them tagged, and that has
// forced-colors review captures to leave out.
const BUTTON_CSS = 'zul/src/main/resources/web/js/zul/wgt/css/button.css';
const COMPACT_DENSITY_SPEC = 'zkthemelab/src/test/playwright/compact-density.spec.ts';
const REVIEW_CAPTURE_SPEC = 'zkthemelab/src/test/playwright/forced-colors-gallery.spec.ts';
const TOKEN_CSS = 'zul/src/main/resources/web/zul/css/tokens/any.css';

const env = { ...process.env };
delete env.PREVIEW_URL;

const select = (...args) => run(process.execPath, [SELECT_TESTS, ...args], { cwd: ZKTHEMELAB_DIR, env });

async function selectJson(input) {
  const result = await select('--json', input);
  assert.equal(result.status, 0, result.stderr);
  return JSON.parse(result.stdout);
}

// The tests of a `playwright test --list --reporter=json` run as a flat list, from the built-in reporter.
function testsOfJsonReport(stdout) {
  const tests = [];
  const visit = (suite) => {
    for (const spec of suite.specs ?? []) {
      for (const test of spec.tests) {
        tests.push({ project: test.projectName, file: spec.file, line: spec.line, title: spec.title, tags: spec.tags ?? [] });
      }
    }
    (suite.suites ?? []).forEach(visit);
  };
  JSON.parse(stdout).suites.forEach(visit);
  return tests;
}

const countByProject = (tests) =>
  tests.reduce((counts, { project }) => ({ ...counts, [project]: (counts[project] ?? 0) + 1 }), {});

const listWithPlaywright = (args) =>
  run('npx', ['playwright', 'test', '-c', PLAYWRIGHT_CONFIG, ...args, '--list'], { cwd: ZKTHEMELAB_DIR, env });

describe('the printed command', () => {
  let button;
  let compactDensity;

  before(async () => {
    [button, compactDensity] = await Promise.all([selectJson(BUTTON_CSS), selectJson(COMPACT_DENSITY_SPEC)]);
  });

  it('lists exactly the selected tests, tagged ones included', async () => {
    assert.equal(button.mode, 'partial');
    // The printed command is meant to be pasted into a shell, so it is run through one.
    const result = await run('sh', ['-c', `${button.commands[0]} --list --reporter=json`], { cwd: ZKTHEMELAB_DIR, env });
    assert.equal(result.status, 0, result.stderr);

    const listed = testsOfJsonReport(result.stdout);
    assert.ok(listed.some((test) => test.tags.length > 0), 'the selection holds no tagged test; pick an input that does');
    assert.equal(listed.length, button.selectedTests);
    assert.deepEqual(countByProject(listed), button.tests);
  });

  it('keeps a missing screenshot baseline from being written', () => {
    for (const answer of [button, compactDensity]) {
      assert.equal(answer.mode, 'partial');
      assert.equal(answer.commands.length, 1);
      assert.ok(answer.commands[0].includes('--update-snapshots=none'), answer.commands[0]);
    }
  });

  it('never selects the review captures, which write their screenshots whatever --update-snapshots says', () => {
    for (const answer of [button, compactDensity]) {
      assert.ok(!answer.commands[0].includes(REVIEW_CAPTURE_PROJECT), 'the review captures are never in the command');
      assert.ok(!(REVIEW_CAPTURE_PROJECT in answer.tests));
    }
  });

  it('names the review captures it left out', () => {
    assert.ok(button.reviewCaptureTests > 0);
  });

  it('answers none for a selection made only of review captures', async () => {
    const answer = await selectJson(REVIEW_CAPTURE_SPEC);
    assert.equal(answer.mode, 'none');
    assert.deepEqual(answer.commands, []);
    assert.equal(answer.selectedTests, 0);
    assert.ok(answer.reviewCaptureTests > 0);
  });

  it('warns not to run the bare command of a full answer', async () => {
    const result = await select(TOKEN_CSS);
    assert.equal(result.status, 0, result.stderr);
    assert.match(result.stdout, /^mode: full/);
    assert.match(result.stdout, /not for a bare run/);
    assert.match(result.stdout, /verify-playwright\.mjs/);
  });
});

describe('the titles grep-titles-reporter prints', () => {
  const grepTitleOf = (inventory, { file, title }) => {
    const matches = inventory.filter((test) =>
      test.file === file && test.spec === title);
    assert.ok(matches.length > 0, `no test titled "${title}" in ${file}`);
    return matches[0];
  };

  let inventory;

  before(async () => {
    const result = await listWithPlaywright([`--reporter=${GREP_TITLES_REPORTER}`]);
    assert.equal(result.status, 0, result.stderr);
    inventory = [];
    const visit = (suite, describes) => {
      for (const spec of suite.specs) {
        for (const test of spec.tests) {
          inventory.push({ project: test.projectName, file: spec.file, spec: spec.title, describes, grepTitle: test.grepTitle });
        }
      }
      suite.suites.forEach((child) => visit(child, [...describes, child.title]));
    };
    JSON.parse(result.stdout).suites.forEach((fileSuite) => visit(fileSuite, []));
  });

  const atCount = (text) => text.split('@').length - 1;
  const pathOf = ({ project, file, describes, spec }) => [project, file, ...describes, spec].join(' ');

  it('ends in the tag of a test that declares one', () => {
    const test = grepTitleOf(inventory, {
      file: 'compact-density.spec.ts', title: 'colorbox — the box is as wide as it is high in the default and the compact density' });
    assert.equal(test.grepTitle, `${pathOf(test)} @ZK-6112`);
    assert.equal(atCount(test.grepTitle), 1);
  });

  it('puts the tag of a describe right after that describe, where Playwright does', () => {
    const describeTitle = 'frozen columns on a mobile UA';
    const inDescribe = inventory.find((candidate) => candidate.file === 'mesh-header-footer.spec.ts' && candidate.describes.includes(describeTitle));
    assert.ok(inDescribe, `no test in the describe "${describeTitle}"`);
    const expected = [inDescribe.project, inDescribe.file, `${describeTitle} @tablet`, inDescribe.spec].join(' ');
    assert.equal(inDescribe.grepTitle, expected);
    assert.equal(atCount(inDescribe.grepTitle), 1);
  });

  it('keeps a tag written into the title once, where the title has it', () => {
    const test = grepTitleOf(inventory, {
      file: 'paging-preview.spec.ts', title: 'default-mold pagings show First / Last and the detailed one its detail on a tablet @tablet' });
    assert.equal(test.grepTitle, pathOf(test));
    assert.equal(atCount(test.grepTitle), 1);
  });
});
