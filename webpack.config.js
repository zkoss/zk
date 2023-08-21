//@ts-check
const path = require('path');
const webpack = require('webpack');
const tslib = require('tslib');
// const CircularDependencyPlugin = require('circular-dependency-plugin');

/** @type { import('webpack').Configuration } */
module.exports = {
	resolve: {
		extensions: ['.ts', '.js', '.json'],
		alias: {
			'tslib$': 'tslib/tslib.es6.js',
		},
	},

	module: {
		rules: [
			{
				test: /\.(ts|js)x?$/,
				exclude: /node_modules/,
				use: [
					{
						loader: 'ts-loader',
						options: {
							transpileOnly: true,
						},
					},
				],
			},
		],
	},
	externals: {
		'jquery': 'jq', // for jquery.transit.js
		'moment': 'zk.mm', // for moment.js
	},
	plugins: [
		new webpack.IgnorePlugin({
			resourceRegExp: /^\.\/locale$/
		}),
		new webpack.ProvidePlugin((function () {
			let options = {};
			for (let key in tslib) {
				options[key] = ['tslib', key];
			}
			return Object.assign(options, {
				'__extends': [path.join(__dirname, 'extends.js'), 'default'] // fix ZK-5441
			});
		})())
		// new CircularDependencyPlugin({
		// 	failOnError: true
		// })
	],
};
