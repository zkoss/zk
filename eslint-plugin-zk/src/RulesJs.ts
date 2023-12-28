/* Rules.ts

	Purpose:
		
	Description:
		
	History:
		11:26 AM 2023/12/25, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
import {TSESTree} from '@typescript-eslint/utils';
import {tree} from './tree';

export interface PassThroughRule {
	obj?: boolean;
	args?: boolean;
}
export interface FunctionRule {
	htmlInput?: boolean;
	htmlOutput?: boolean;
	safe?: boolean | string[];
	passthrough?: PassThroughRule;
	sanitized?: boolean;
}
/**
 * Rule library for different nodes.
 *
 * @param {{ functionRules: Object }} options - Rule options.
 */
export class RulesJs {
	declare data: Record<string, any>;
	constructor(options: { functionRules: FunctionRule }) {
		this.data = {};
		this._addCategory('functions', options.functionRules);
	}

	/**
	 * Gets the node context rules.
	 *
	 * @param {Node} node - Node to get the rules for
	 *
	 * @returns {Object} Context rules or an empty rule object.
	 */
	get(node: TSESTree.Node): FunctionRule {
		if (node.type === 'CallExpression')
			return this.getFunctionRules(node);

		return {} as FunctionRule;
	}

	/**
	 * Gets the node context rules.
	 *
	 * @param {Node} node - Node to get the rules for
	 *
	 * @returns {Object} Context rules or an empty rule object.
	 */
	getFunctionRules(node: TSESTree.Node): FunctionRule {
		return this._getWithCache(node, this.data['functions'] as never);
	}

	_getWithCache(node: TSESTree.Node, data: {
		cache: Record<string, Record<any, any>>,
		rules: Record<string, string>
	}): FunctionRule {

		const fullName = tree.getFullItemName(node);
		if (data.cache[fullName])
			return data.cache[fullName] as FunctionRule;

		let rules: FunctionRule | undefined = undefined;
		const partialNames = tree.getRuleNames(node);
		for (let i = 0; !rules && i < partialNames.length; i++) {
			rules = data.rules[partialNames[i]!] as FunctionRule | undefined;
		}

		return data.cache[fullName] = rules || {} as FunctionRule;
	}

	_addCategory(type: string, rules: FunctionRule): void {
		// noinspection TypeScriptValidateTypes
		this.data[type] = {
			cache: Object.create(null) as never,
			rules: rules || {}
		};
	}
}