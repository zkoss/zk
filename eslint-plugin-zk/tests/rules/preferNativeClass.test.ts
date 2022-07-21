import { preferNativeClass } from '@rules';
import { typedRuleTester as ruleTester } from '../util';

function replaceWithES6Class(line: number, column: number) {
	return {
		messageId: 'replaceWithES6Class' as const,
		line,
		column,
	};
}

ruleTester.run('preferNativeClass', preferNativeClass, {
	valid: [],
	invalid: [
		{
			code: `
zkmax.nav.Anchornav = zk.$extends(zul.Widget, {});
`,
			output: `
zkmax.nav.Anchornav = zk.$extends(zul.Widget, {});
`,
			errors: [
				replaceWithES6Class(2, 26),
			],
		}
	]
});