/* tree.ts

	Purpose:
		
	Description:
		
	History:
		11:16 AM 2023/12/25, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
import {TSESTree} from '@typescript-eslint/utils';

export const tree = {

	/**
	 * Gets the identifier from the node.
	 *
	 * @param {Node} node - Node to get the identifier for.
	 *
	 * @returns {Identifier} - The identifier node.
	 */
	getIdentifier: function (node: TSESTree.Node) {

		// Function calls use the callee as name.
		if (node.type === 'CallExpression')
			node = node.callee;

		// Get the member property.
		if (node.type === 'MemberExpression') {
			if (node.computed)
				node = node.object;
			else
				node = node.property;
		}

		if (node.type !== 'Identifier')
			return null;

		return node;
	},

	/**
	 * Gets the name of the node.
	 *
	 * @param {Node} node - The node to get the name for.
	 *
	 * @returns {string} Node name.
	 */
	getNodeName: function (node: TSESTree.Node): string {

		// Check the 'this' expression.
		if (node.type === 'ThisExpression')
			return 'this';

		// Expect identifier or similar node.
		const id = tree.getIdentifier(node);
		return id ? id.name : '';
	},

	/**
	 * Gets the function name.
	 *
	 * @param {Node} func - Function node.
	 *
	 * @returns {string} Function name with optional '.' for member functions.
	 */
	getFullItemName: function (func: TSESTree.Node): string {

		// Unwrap the possible call expression.
		if (func.type === 'CallExpression')
			func = func.callee;

		// Resolve the name stack from the member expression.
		// This gathers it in reverse.
		const name = [];
		while (func.type === 'MemberExpression') {
			name.push(((func as TSESTree.MemberExpression).property as TSESTree.Identifier).name);
			func = func.object;
		}

		// Ensure the last object name is an identifier at this point.
		// We don't support [] indexed access for encoders.
		if (func.type === 'Identifier')
			name.push(func.name);

		// Reverse the stack to get it in correct order and join functio names
		// using '.'
		name.reverse();
		return name.join('.');
	},

	/**
	 * Gets the function name candidates for the rules.
	 *
	 * @param {Node} func - Function node.
	 *
	 * @returns {string} Names of rules that affect this function.
	 */
	getRuleNames: function (func: TSESTree.Node): string[] {

		// Unwrap the possible call expression.
		if (func.type === 'CallExpression')
			func = func.callee;

		// Unwrap the member expressions and get the last identifier.
		const names = [];
		const memberIdentifiers = [];
		for (let fc: undefined | TSESTree.Node & {
			object?: TSESTree.Node,
			computed?: boolean
		} = func; fc; fc = fc.object) {

			// Skip computed properties.
			if (fc.computed)
				continue;

			const identifier = tree.getIdentifier(fc);
			if (!identifier)
				break;

			memberIdentifiers.unshift(identifier.name);

			// Add '.' prefix is this is part of a member function.
			const prefix = (fc.object ? '.' : '');
			names.unshift(prefix + memberIdentifiers.join('.'));
		}

		return names;
	},

	/**
	 * Gets the parent function identifier.
	 *
	 * @param {Node} node - Node for which to get the parent function.
	 *
	 * @returns {Identifier} - The function identifier or null.
	 */
	getParentFunctionIdentifier: function (node: TSESTree.Node): TSESTree.Node | null {

		// We'll want to get the closest function.
		let func: TSESTree.Node | undefined = node;

		while (func &&
		func.type !== 'FunctionExpression' &&
		func.type !== 'FunctionDeclaration' &&
		func.type !== 'ArrowFunctionExpression') {

			// Continue getting the parent.
			func = func.parent;
		}

		// Not everything is inside functions.
		if (!func)
			return null;

		// If the function is named, return the function name.
		if (func.id)
			return func.id;

		// Otherwise see if it is being assigned to a variable.
		const parent = func.parent;
		if (parent && parent.type === 'VariableDeclarator')
			return parent.id;
		if (parent && parent.type === 'AssignmentExpression')
			return parent.left;

		return null;
	},

	/**
	 * Checks whether the node is part of the parameters of the expression.
	 *
	 * @param {Node} node - Node to check.
	 * @param {Expression} expr - The expression we are interested in.
	 *
	 * @returns {bool} True, if the node is a parameter.
	 */
	isParameter: function (node: TSESTree.Node, expr: TSESTree.Node) {

		if (expr.type === 'CallExpression') {

			// Check whether any of the call arguments equals the node.
			let isParameter = false;
			expr.arguments.forEach(function (a) {
				if (a === node)
					isParameter = true;
			});

			// Return the result.
			return isParameter;

		} else if (expr.type === 'AssignmentExpression') {

			// Assignments count the right side as the paramter.
			return expr.right === node;

		} else if (expr.type === 'VariableDeclarator') {

			// Declaration count the init expression as the paramter.
			return expr.init === node;

		} else if (expr.type === 'Property') {

			// Properties consider the property value as the parameter.
			return expr.value === node;

		} else if (expr.type === 'ArrayExpression') {

			// For arrays check whether the node is any of the elements.
			let isElement = false;
			expr.elements.forEach(function (e) {
				if (e === node)
					isElement = true;
			});
			return isElement;

		} else if (expr.type === 'FunctionExpression') {

			// Function expression has no 'parameters'.
			// None of the fields end up directly into the HTML (that we know
			// of without solving the halting problem...)
			return false;

		} else if (expr.type === 'ConditionalExpression') {

			return node === expr.alternate || node === expr.consequent;

		} else if (expr.type === 'ArrowFunctionExpression') {

			return node === expr.body;
		}

		return true;
	},
};