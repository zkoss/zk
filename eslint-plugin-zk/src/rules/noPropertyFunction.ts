/* noPropertyFunction.ts

	Purpose:
		
	Description:
		
	History:
		5:20 PM 2022/7/27, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
import { AST_NODE_TYPES, TSESTree } from '@typescript-eslint/utils';
import { createRule } from '../util';

function isZK$Void(node: TSESTree.Node | null): boolean {
	return !!node && node.type === AST_NODE_TYPES.MemberExpression
		&& node.object.type === AST_NODE_TYPES.Identifier &&
			node.object.name === 'zk' &&
			node.property.type === AST_NODE_TYPES.Identifier &&
			node.property.name === '$void';
}
function isCallable(node: TSESTree.Node | null): boolean {
	return !!node && node.type === AST_NODE_TYPES.CallExpression
	&& isFunction(node.callee);
}
function isFunction(node: TSESTree.Node | null): boolean {
	return !!node && (isZK$Void(node) || isCallable(node) ||
		node.type === AST_NODE_TYPES.FunctionExpression
	);
}
export const noPropertyFunction = createRule({
	name: 'noPropertyFunction',
	meta: {
		type: 'problem',
		docs: {
			description: 'Property function may cause subclass overriding failure.',
			recommended: 'error',
		},
		messages: {
			omitPropertyFunction: 'Do not declare a property function, using ES6 class method instead.',
		},
		schema: []
	},
	defaultOptions: [],
	create(context) {
		return {
			PropertyDefinition(node) {

				// for zk.$void case
				if (isFunction(node.value)) {
					context.report({
						node: node.value as TSESTree.Node,
						messageId: 'omitPropertyFunction'
					});
				} else if (node.value?.type === AST_NODE_TYPES.ConditionalExpression) {
					if (isFunction(node.value.consequent)) {
						context.report({
							node: node.value.consequent,
							messageId: 'omitPropertyFunction'
						});
					} else if (isFunction(node.value.alternate)) {
						context.report({
							node: node.value.alternate,
							messageId: 'omitPropertyFunction'
						});
					}
				}
			}
		};
	}
});
