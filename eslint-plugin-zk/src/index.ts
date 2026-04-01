import * as ruleExports from './rules';
import { noMixedHtml, schema as noMixedHtmlSchema } from './rules/noMixedHtml';

// Build rules map: wrap noMixedHtml (plain function) as a proper ESLint rule object
const rules: Record<string, unknown> = { ...ruleExports };
rules['noMixedHtml'] = {
	meta: {
		type: 'problem',
		schema: noMixedHtmlSchema,
	},
	create: noMixedHtml,
};
delete rules['schema']; // Remove leaked schema from noMixedHtml re-exports

export = {
	rules,
	configs: {
		recommended: {
			rules: {
				'zk/noAccessModifier': 'error',
				'zk/noTopLevelScopingIIFE': 'error',
				'zk/preferNativeInstanceof': 'error',
				'zk/preferNativeClass': 'error',
				'zk/javaStyleSetterSignature': 'error',
				'zk/noNull': 'error',
				'zk/preferStrictBooleanType': 'error',
				'zk/noPropertyFunction': 'error',
				'zk/tsdocValidation': 'error',
				'zk/noMixedHtml': 'error',
				'zk/noLocationHrefAssign': 'error',
			},
		},
	},
};