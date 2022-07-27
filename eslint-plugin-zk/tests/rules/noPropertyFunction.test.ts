/* noPropertyFunction.test.ts

	Purpose:
		
	Description:
		
	History:
		5:24 PM 2022/7/27, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
import { noPropertyFunction } from '@rules';
import { typedRuleTester as ruleTester } from '../util';

function omitPropertyFunction(line: number, column: number) {
	return {
		messageId: 'omitPropertyFunction' as const,
		line,
		column,
	};
}

ruleTester.run('noPropertyFunction', noPropertyFunction, {
	valid: [`
var fun = zk.$void,
  fun2 = () => null;
class Test {
  foo(): void {}
  bar(): boolean | null {
  	return zk.ie ? null : false;
  }
  prop(): void {}
  prop2prop(): void {}
}`,
	],
	invalid: [
		{
			code: `
var fun = zk.$void,
  fun2 = () => null;
class Test {
  foo = zk.$void;
  bar = zk.ie ? null : zk.$void;
  prop = (function() {
    return function() {};
  })();
  prop2 = function() {};
}`,
			output: `
var fun = zk.$void,
  fun2 = () => null;
class Test {
  foo = zk.$void;
  bar = zk.ie ? null : zk.$void;
  prop = (function() {
    return function() {};
  })();
  prop2 = function() {};
}`,
			errors: [
				omitPropertyFunction(5, 9),
				omitPropertyFunction(6, 24),
				omitPropertyFunction(7, 10),
				omitPropertyFunction(10, 11),
			],
		}
	]
});