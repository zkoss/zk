'use strict';

module.exports = function (api) {
    api.cache(true);

    return {
        presets: [
            ['@babel/preset-env', {'modules': false}],
            '@babel/preset-typescript'
        ],
        plugins: [
            '@babel/plugin-proposal-class-properties',
            '@babel/plugin-proposal-object-rest-spread'
        ]
    };
};