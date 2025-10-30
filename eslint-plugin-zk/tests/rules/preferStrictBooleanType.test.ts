/* preferStrictBooleanType.test.ts

	Purpose:
		
	Description:
		
	History:
		2:16 PM 2022/7/26, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
import { preferStrictBooleanType } from '@rules';
import { typedRuleTester as ruleTester } from '../util';

function omitStrictBooleanType(line: number, column: number) {
	return {
		messageId: 'useBooleanTypeOnly' as const,
		line,
		column,
	};
}

ruleTester.run('preferStrictBooleanType', preferStrictBooleanType, {
	valid: [`
class Test {
  type?: boolean;
  value: boolean = true;
  foo = Object.assign({}, {
    a: <boolean> true,
  });
  flag: string | boolean | undefined;
  bar(arg1?: boolean, arg2?: boolean): boolean {
    return false;
  }
  shouldOkay(arg1: boolean, arg2: boolean | string): boolean | number {
    return 3;
  }
}`,
	],
	invalid: [
		{
			code: `
class Test {
  type?: boolean | undefined;
  value: boolean = true;
  foo = Object.assign({}, {
    a: <undefined | boolean> true,
  });
  flag: string | boolean | undefined;
  bar(arg1?: boolean, arg2: boolean | undefined): boolean | undefined {
    return false;
  }
  shouldOkay(arg1: boolean, arg2: boolean | string): boolean | number {
    return 3;
  }
}`,
			output: `
class Test {
  type?: boolean;
  value: boolean = true;
  foo = Object.assign({}, {
    a: <boolean> true,
  });
  flag: string | boolean | undefined;
  bar(arg1?: boolean, arg2: boolean): boolean {
    return false;
  }
  shouldOkay(arg1: boolean, arg2: boolean | string): boolean | number {
    return 3;
  }
}`,
			errors: [
				omitStrictBooleanType(3, 10),
				omitStrictBooleanType(6, 21),
				omitStrictBooleanType(9, 29),
				omitStrictBooleanType(9, 51)
			],
		}
	]
});