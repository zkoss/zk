/* noNull.test.ts

	Purpose:
		
	Description:
		
	History:
		10:53 AM 2022/7/26, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
import { noNull } from '@rules';
import { typedRuleTester as ruleTester } from '../util';

function omitNull(line: number, column: number) {
	return {
		messageId: 'useUndefined' as const,
		line,
		column,
	};
}

ruleTester.run('noNull', noNull, {
	valid: [`
class Test {
  type: undefined | string = '';
  value = undefined;
  foo = Object.assign({}, {
    a: <undefined | number> 3,
  });
  dom = "" as undefined | string;
  bar() {
    jq('aa').attr('shouldBeOkay', null);
    jq('aa').attr('shouldBeOkay2', true ? null : '');
  }
}`,
	],
	invalid: [
		{
			code: `
class Test {
  type: null | string = '';
  value = null;
  foo = Object.assign({}, {
    a: <null | number> 3,
  });
  dom = "" as null | string;
  bar() {
    jq('aa').attr('shouldBeOkay', null);
    jq('aa').attr('shouldBeOkay2', true ? null : '');
  }
}`,
			output: `
class Test {
  type: undefined | string = '';
  value = undefined;
  foo = Object.assign({}, {
    a: <undefined | number> 3,
  });
  dom = "" as undefined | string;
  bar() {
    jq('aa').attr('shouldBeOkay', null);
    jq('aa').attr('shouldBeOkay2', true ? null : '');
  }
}`,
			errors: [
				omitNull(3, 9),
				omitNull(4, 11),
				omitNull(6, 9),
				omitNull(8, 15),
			],
		}
	]
});