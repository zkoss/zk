// visual-baseline.sh, run in a throwaway git repository that has the layout the script derives its paths from
// (zkthemelab/scripts/ inside a git root), with a fake `npx` that records its arguments. The stamp file and every
// other file the script writes land in that repository, never in the real one.
// VISUAL_BASELINE_SCRIPT points the tests at another revision of the script.
import assert from 'node:assert/strict';
import { execFileSync } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';
import { after, before, describe, it } from 'node:test';
import { installFakeNpx, makeTempDir, readInvocations, removeTempDir, run, scriptUnderTest } from './helpers.mjs';

const SCRIPT = scriptUnderTest('visual-baseline.sh', 'VISUAL_BASELINE_SCRIPT');
const CONFIG = 'src/test/playwright/playwright.config.ts';
const GIT_ENV = {
  GIT_CONFIG_GLOBAL: '/dev/null', GIT_CONFIG_NOSYSTEM: '1',
  GIT_AUTHOR_NAME: 'test', GIT_AUTHOR_EMAIL: 'test@example.invalid',
  GIT_COMMITTER_NAME: 'test', GIT_COMMITTER_EMAIL: 'test@example.invalid',
};

let tempRoot;

before(() => { tempRoot = makeTempDir(); });
after(() => removeTempDir(tempRoot));

function createRepository() {
  const root = fs.mkdtempSync(path.join(tempRoot, 'repo-'));
  const scriptsDir = path.join(root, 'zkthemelab/scripts');
  fs.mkdirSync(scriptsDir, { recursive: true });
  fs.copyFileSync(SCRIPT, path.join(scriptsDir, 'visual-baseline.sh'));
  const git = (...args) => execFileSync('git', ['-C', root, ...args], { env: { ...process.env, ...GIT_ENV }, stdio: 'pipe' });
  git('init', '--quiet');
  git('add', '.');
  git('commit', '--quiet', '-m', 'fixture');

  installFakeNpx(path.join(root, 'bin'));
  const logFile = path.join(root, 'fake-npx.log');
  const env = { ...process.env, ...GIT_ENV, PATH: `${path.join(root, 'bin')}${path.delimiter}${process.env.PATH}`, FAKE_NPX_LOG: logFile };
  return {
    root,
    baseline: (...args) => run('bash', [path.join(scriptsDir, 'visual-baseline.sh'), ...args], { cwd: root, env }),
    playwrightArgs: () => readInvocations(logFile).map((call) => call.args),
  };
}

const COMMAND_PREFIX = ['playwright', 'test', '--config', CONFIG];
const updateSnapshotsOf = (args) => args.filter((arg) => arg.startsWith('--update-snapshots'));

// What select-tests.mjs prints is pasted after the mode, so a caller can hand over any of these.
const CALLER_UPDATE_FLAGS = [['--update-snapshots'], ['--update-snapshots=none'], ['--update-snapshots=all'], ['--update-snapshots=missing']];

describe('visual-baseline.sh capture', () => {
  for (const callerFlag of CALLER_UPDATE_FLAGS) {
    it(`passes exactly one --update-snapshots=all whatever the caller passed (${callerFlag})`, async () => {
      const repository = createRepository();
      const result = await repository.baseline('capture', ...callerFlag, '--project=gallery');
      assert.equal(result.status, 0, result.stderr);
      assert.deepEqual(repository.playwrightArgs(), [[...COMMAND_PREFIX, '--update-snapshots=all', '--project=gallery']]);
    });
  }

  it('keeps an argument with spaces in one piece', async () => {
    const repository = createRepository();
    const result = await repository.baseline('capture', '--update-snapshots=none', '-g', 'a title with  spaces', '--project=gallery');
    assert.equal(result.status, 0, result.stderr);
    assert.deepEqual(repository.playwrightArgs(), [[...COMMAND_PREFIX, '--update-snapshots=all', '-g', 'a title with  spaces', '--project=gallery']]);
  });
});

describe('visual-baseline.sh compare', () => {
  async function repositoryWithBaseline() {
    const repository = createRepository();
    const captured = await repository.baseline('capture');
    assert.equal(captured.status, 0, captured.stderr);
    return repository;
  }

  for (const callerFlag of CALLER_UPDATE_FLAGS) {
    it(`passes exactly one --update-snapshots=none whatever the caller passed (${callerFlag})`, async () => {
      const repository = await repositoryWithBaseline();
      const result = await repository.baseline('compare', ...callerFlag, '--project=gallery');
      assert.equal(result.status, 0, result.stderr);
      const [, compared] = repository.playwrightArgs();
      assert.deepEqual(compared, [...COMMAND_PREFIX, '--update-snapshots=none', '--project=gallery']);
      assert.equal(updateSnapshotsOf(compared).length, 1);
    });
  }

  it('keeps an argument with spaces in one piece', async () => {
    const repository = await repositoryWithBaseline();
    await repository.baseline('compare', '--update-snapshots=all', '-g', 'a title with  spaces');
    const [, compared] = repository.playwrightArgs();
    assert.deepEqual(compared, [...COMMAND_PREFIX, '--update-snapshots=none', '-g', 'a title with  spaces']);
  });
});
