'use strict';

module.exports = function (api) {
    api.cache(true);

    return {
        presets: [
            ['@babel/preset-env', {'modules': 'commonjs',
	            "exclude": ['@babel/plugin-transform-typeof-symbol']}],
        ],
        plugins: [
			['@babel/plugin-transform-modules-commonjs', {
		        loose: true,
		        strictMode: false,
	        }],
	        ['@babel/plugin-transform-runtime', {'corejs': 3}],
	        ["@babel/plugin-proposal-decorators", { "legacy": true }],
            '@babel/plugin-proposal-class-properties',
            '@babel/plugin-proposal-object-rest-spread',
			'./babel-plugin-expose-private-functions-and-variables',
        ],
	    overrides: [{
		    test: /[\\/]ext[\\/]//* treat as script for 3rd-party library */,
		    sourceType: 'script'
        }],
	    exclude: /pdfviewer\/ext/
    };
};