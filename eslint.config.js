const { FlatCompat } = require('@eslint/eslintrc');
const js = require('@eslint/js');
const sdlPlugin = require('@microsoft/eslint-plugin-sdl');
const path = require('path');

const compat = new FlatCompat({
	baseDirectory: __dirname,
	recommendedConfig: js.configs.recommended,
});

// Load legacy config and strip SDL plugin references
// (SDL v1.1.0 only exports flat config format — incompatible with FlatCompat)
const legacyConfig = require('./.eslintrc.js');
const cleanedConfig = {
	...legacyConfig,
	plugins: legacyConfig.plugins.filter(p => p !== '@microsoft/eslint-plugin-sdl'),
	overrides: legacyConfig.overrides.map(override => {
		if (!override.extends) return override;
		return {
			...override,
			extends: override.extends.filter(e => !e.startsWith('plugin:@microsoft/sdl'))
		};
	})
};

module.exports = [
	// Global ignores (converted from .eslintignore)
	{
		ignores: [
			'**/debug/**',
			'**/build/**',
			'**/codegen/**',
			'**/.git/**',
			'**/ext/**',
			'@types/**',
			'*.config.js',
			'.eslintrc.js',
			'jsdoc/**',
		]
	},

	// Convert legacy eslintrc config (without SDL) to flat config
	...compat.config(cleanedConfig),

	// Add SDL security rules natively in flat config (TS files only)
	{
		files: ['**/*.ts'],
		plugins: {
			'@microsoft/sdl': sdlPlugin,
		},
		rules: {
			// From plugin:@microsoft/sdl/required — common security rules
			'@microsoft/sdl/no-cookies': 'error',
			'@microsoft/sdl/no-document-domain': 'error',
			'@microsoft/sdl/no-document-write': 'error',
			'@microsoft/sdl/no-html-method': 'error',
			'@microsoft/sdl/no-inner-html': 'error',
			'@microsoft/sdl/no-insecure-url': 'error',
			'@microsoft/sdl/no-msapp-exec-unsafe': 'error',
			'@microsoft/sdl/no-postmessage-star-origin': 'error',
			'@microsoft/sdl/no-winjs-html-unsafe': 'error',
			'@microsoft/sdl/no-unsafe-alloc': 'error',
		}
	}
];
