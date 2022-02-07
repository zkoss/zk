'use strict';

module.exports = function (api) {
    api.cache(true);

    return {
        presets: [
            ['@babel/preset-env', {'modules': false,
	            "exclude": ['@babel/plugin-transform-typeof-symbol']}],
            '@babel/preset-typescript'
        ],
        plugins: [
	        ['@babel/plugin-transform-typescript', {
		        'allowDeclareFields': true
	        }],
            '@babel/plugin-proposal-class-properties',
            '@babel/plugin-proposal-object-rest-spread'
        ]
    };
};