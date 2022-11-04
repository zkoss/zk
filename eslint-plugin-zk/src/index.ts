import * as rules from './rules';

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
			},
		},
	},
};