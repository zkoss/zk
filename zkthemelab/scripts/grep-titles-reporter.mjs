// Playwright reporter used by select-tests.mjs with `--list`: prints, per test, the exact string `-g` is matched against.
// The JSON reporter cannot give it: it drops the '@' of every tag and merges the tags of a describe into the test's.
// The tree has the shape of the JSON reporter's: the suites and specs of all projects are merged by location, so a
// describe spans the tests of every project (the selector reads its line range from the spec source).
import path from 'node:path';

const sameLocation = (a, b) => a.title === b.title && a.line === b.line && a.column === b.column;

function mergeSuite(into, from) {
  for (const spec of from.specs) {
    const same = into.specs.find(s => sameLocation(s, spec));
    if (same) same.tests.push(...spec.tests);
    else into.specs.push(spec);
  }
  for (const suite of from.suites) {
    const same = into.suites.find(s => sameLocation(s, suite));
    if (same) mergeSuite(same, suite);
    else into.suites.push(suite);
  }
}

export default class GrepTitlesReporter {
  onBegin(config, rootSuite) {
    const posixFile = file => path.relative(config.rootDir, file).split(path.sep).join('/');
    const serialize = suite => ({
      title: suite.title,
      line: suite.location?.line,
      column: suite.location?.column,
      specs: suite.tests.map(test => ({
        title: test.title,
        file: posixFile(test.location.file),
        line: test.location.line,
        column: test.location.column,
        tests: [{
          projectName: test.parent.project()?.name,
          // Playwright's own composition (project, file, describes, title, explicit tags); it has no public accessor.
          // The root suite, whose empty title adds a leading space, joins the tree only after the grep filter has run.
          grepTitle: test._grepTitleWithTags().replace(/^ /, ''),
        }],
      })),
      suites: suite.suites.map(serialize),
    });
    const fileSuites = [];
    for (const projectSuite of rootSuite.suites) {
      for (const fileSuite of projectSuite.suites.map(serialize)) {
        const same = fileSuites.find(s => s.title === fileSuite.title);
        if (same) mergeSuite(same, fileSuite);
        else fileSuites.push(fileSuite);
      }
    }
    process.stdout.write(JSON.stringify({ suites: fileSuites }));
  }

  printsToStdio() { return true; }
}
