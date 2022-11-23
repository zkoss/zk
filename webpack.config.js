//@ts-check
const path = require('path');
const webpack = require('webpack');
// const CircularDependencyPlugin = require('circular-dependency-plugin');

/** @type { import('webpack').Configuration } */
module.exports = {
	resolve: {
		extensions: ['.ts', '.js', '.json'],
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
		// new CircularDependencyPlugin({
		// 	failOnError: true
		// })
	],

	devtool: 'source-map',
};
