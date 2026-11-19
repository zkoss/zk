// Shared fixtures for the script tests: a temp directory per test file, a fake `npx` that records its
// invocations and answers like Playwright, and an async process runner (a synchronous one would block the
// local HTTP server these tests start in the same process).
import { spawn } from 'node:child_process';
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

export const SCRIPTS_DIR = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
export const ZKTHEMELAB_DIR = path.resolve(SCRIPTS_DIR, '..');

// A test points a script at another revision of it through these variables, to prove that the test fails on the
// behaviour it guards against.
export const scriptUnderTest = (name, envVariable) => process.env[envVariable] ?? path.join(SCRIPTS_DIR, name);

export function makeTempDir() {
  return fs.realpathSync(fs.mkdtempSync(path.join(os.tmpdir(), 'zkthemelab-script-test-')));
}

export function removeTempDir(dir) {
  fs.rmSync(dir, { recursive: true, force: true });
}

// Plays Playwright for a script under test. Behaviour comes from the JSON file named by FAKE_NPX_CONFIG:
//   { projects: [...names `--list` answers with],
//     runs: { <json report name, e.g. run1 or run1-rerun-0>: { report } | { kill: true } | { noReport: true, exitCode } } }
// A run that is not configured writes an empty, error-free report. Every call is appended to FAKE_NPX_LOG.
// Without PLAYWRIGHT_JSON_OUTPUT_FILE and without --list it only records its arguments (visual-baseline.sh).
const FAKE_NPX_SOURCE = `#!${process.execPath}
const fs = require('node:fs');
const path = require('node:path');
const args = process.argv.slice(2);
fs.appendFileSync(process.env.FAKE_NPX_LOG, JSON.stringify({ args, cwd: process.cwd(), previewUrl: process.env.PREVIEW_URL }) + '\\n');
const config = process.env.FAKE_NPX_CONFIG ? JSON.parse(fs.readFileSync(process.env.FAKE_NPX_CONFIG, 'utf8')) : { projects: [], runs: {} };
if (args.includes('--list')) {
  process.stdout.write(JSON.stringify({ config: { projects: config.projects.map((name) => ({ name })) } }));
  process.exit(0);
}
const jsonFile = process.env.PLAYWRIGHT_JSON_OUTPUT_FILE;
if (!jsonFile) process.exit(0);
const behavior = config.runs[path.basename(jsonFile, '.json')] ?? {};
if (behavior.kill) process.kill(process.pid, 'SIGKILL');
if (!behavior.noReport) fs.writeFileSync(jsonFile, JSON.stringify(behavior.report ?? { errors: [], suites: [] }));
process.exit(behavior.exitCode ?? 0);
`;

export function installFakeNpx(binDir) {
  fs.mkdirSync(binDir, { recursive: true });
  const file = path.join(binDir, 'npx');
  fs.writeFileSync(file, FAKE_NPX_SOURCE, { mode: 0o755 });
}

export const readInvocations = (logFile) =>
  fs.existsSync(logFile)
    ? fs.readFileSync(logFile, 'utf8').split('\n').filter(Boolean).map((line) => JSON.parse(line))
    : [];

export function run(command, args, { cwd, env }) {
  return new Promise((resolve, reject) => {
    const child = spawn(command, args, { cwd, env, stdio: ['ignore', 'pipe', 'pipe'] });
    // A multi-byte character can be split between two chunks; decoding the stream keeps it whole.
    child.stdout.setEncoding('utf8');
    child.stderr.setEncoding('utf8');
    let stdout = '';
    let stderr = '';
    child.stdout.on('data', (chunk) => { stdout += chunk; });
    child.stderr.on('data', (chunk) => { stderr += chunk; });
    child.on('error', reject);
    child.on('close', (status, signal) => resolve({ status, signal, stdout, stderr }));
  });
}
