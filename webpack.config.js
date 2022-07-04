const path = require('path');
var webpack = require('webpack');
// var CircularDependencyPlugin = require('circular-dependency-plugin');
// var ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');

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
		// new ForkTsCheckerWebpackPlugin(),
		new webpack.IgnorePlugin({
			resourceRegExp: /^\.\/locale$/
		}),
		// new CircularDependencyPlugin({
		// 	failOnError: true
		// })
	],

	devtool: 'source-map',
};
