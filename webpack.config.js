//@ts-check
const path = require('path');
const webpack = require('webpack');
const baseDir = path.resolve(__dirname)

// const CircularDependencyPlugin = require('circular-dependency-plugin');

/** @type { import('webpack').Configuration } */
module.exports = {
	resolve: {
		extensions: ['.ts', '.js', '.json'],
		modules: [path.join(baseDir, 'node_modules') /* zk node_modules for zkcml*/, 'node_modules'],
	},

	module: {
		rules: [
			{
				test: /\.(ts|js)x?$/,
				exclude: [/node_modules/,
					/[\\/]ext[\\/]//* ignore 3rd-party library */],
				use: [
					{
						loader: 'babel-loader'
					},
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
	],
};
