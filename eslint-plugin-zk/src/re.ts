/* re.ts

	Purpose:
		
	Description:
		
	History:
		11:16 AM 2023/12/25, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
module.exports = {

	toRegexp: function (str: string): RegExp {
		const [first, second] = str.split('/');
		return new RegExp(first as string, second);
	},

	any: function (input: string, regexps: (RegExp)[]): boolean {

		for (const item of regexps) {
			if (item.exec(input))
				return true;
		}

		return false;
	},
};