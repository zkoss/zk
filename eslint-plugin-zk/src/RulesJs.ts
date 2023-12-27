// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-nocheck
/* Rules.js

	Purpose:
		
	Description:
		
	History:
		11:26 AM 2023/12/25, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/

'use strict';

var tree = require( './tree' );

/**
 * Rule library for different nodes.
 *
 * @param {{ functionRules: Object }} options - Rule options.
 */
export const RulesJs = function(options ) {

	this.data = {};
	this._addCategory( 'functions', options.functionRules );
};


/**
 * Gets the node context rules.
 *
 * @param {Node} node - Node to get the rules for
 *
 * @returns {Object} Context rules or an empty rule object.
 */
RulesJs.prototype.get = function(node ) {
	if( node.type === 'CallExpression' )
		return this.getFunctionRules( node );

	return {};
};

/**
 * Gets the node context rules.
 *
 * @param {Node} node - Node to get the rules for
 *
 * @returns {Object} Context rules or an empty rule object.
 */
RulesJs.prototype.getFunctionRules = function(node ) {
	return this._getWithCache( node, this.data.functions );
};

RulesJs.prototype._getWithCache = function(node, data ) {

	var fullName = tree.getFullItemName( node );
	if( data.cache[ fullName ] )
		return data.cache[ fullName ];

	var rules = null;
	var partialNames = tree.getRuleNames( node );
	for( var i = 0; !rules && i < partialNames.length; i++ ) {
		rules = data.rules[ partialNames[ i ] ];
	}

	return data.cache[ fullName ] = rules || {};
};

RulesJs.prototype._addCategory = function(type, rules ) {
	this.data[ type ] = {
		cache: Object.create( null ),
		rules: rules || {}
	};
};