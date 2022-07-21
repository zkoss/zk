import { noTopLevelScopingIIFE } from '@rules';
import { typedRuleTester as ruleTester } from '../util';

function removeTopLevelScopingIIFE(line: number, column: number) {
	return {
		messageId: 'removeTopLevelScopingIIFE' as const,
		line,
		column,
	};
}

ruleTester.run('noTopLevelScopingIIFE', noTopLevelScopingIIFE, {
	valid: [`
function a() {}
`, `
(function() {})
`, `
const a = (function() {})()
`, `
(function(a) {})(1)
`, `
(function*() { yield 2 })()
`, `
(async function(){ await fetch('url') })()
`, `
function foo() {
  (function() { console.log('hello'); })();
}
`, `
() => {}
`, `
let a = () => {}
`, `
(() => {})
`, `
var a = (() => {})()
`, `
(() => 1)()
`, `
(a => {})(1)
`, `
(async () => { await fetch('url'); })()
`,
	],
	invalid: [
		{
			code: `
/** comment */
      (function() {}) () ; // comment

(() => {
  // comment
  @decorator
  class Test {
    constructor() {
      (function() {})();
    }
  }
  var a = 0;
  if (1 !== 2) {
    a = 3
  }
})(); // comment

(() => 2)()

let x = (function() {})();
`,
			output: `
/** comment */
       // comment


  // comment
  @decorator
  class Test {
    constructor() {
      (function() {})();
    }
  }
  var a = 0;
  if (1 !== 2) {
    a = 3
  } // comment

(() => 2)()

let x = (function() {})();
`,
			errors: [
				removeTopLevelScopingIIFE(3, 7),
				removeTopLevelScopingIIFE(5, 1),
			],
		}
	]
});