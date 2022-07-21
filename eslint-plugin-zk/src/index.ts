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
			},
		},
	},
};