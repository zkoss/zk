/* noNull.ts

	Purpose:
		
	Description:
		
	History:
		6:24 PM 2022/7/25, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
import { AST_NODE_TYPES, TSESTree } from '@typescript-eslint/utils';
import { createRule } from '../util';

const visitedNode: Record<string, boolean> = {};
export const noNull = createRule({
	name: 'noNull',
	meta: {
		type: 'problem',
		docs: {
			description: 'Use undefined instead of null.',
			recommended: 'error',
		},
		fixable: 'code',
		messages: {
			useUndefined: 'Use undefined instead of null.',
		},
		schema: []
	},
	defaultOptions: [],
	create(context) {
		function checkTypeSyntax(node: TSESTree.TSTypeAnnotation | TSESTree.TSTypeAssertion | TSESTree.TSAsExpression) {
			if (node.typeAnnotation.type === AST_NODE_TYPES.TSNullKeyword) {
				context.report({
					node: node,
					messageId: 'useUndefined',
					fix(fixer) {
						return fixer.replaceText(node, 'undefined');
					}
				});
			} else if (node.typeAnnotation.type === AST_NODE_TYPES.TSUnionType) {
				for (const t of node.typeAnnotation.types) {
					if (t.type === AST_NODE_TYPES.TSNullKeyword) {
						context.report({
							node: t,
							messageId: 'useUndefined',
							fix(fixer) {
								return fixer.replaceText(t, 'undefined');
							}
						});
					}
				}
			}
		}
		return {
			Literal(node) {
				if (node.raw === 'null') {
					let parent = node.parent;
					while (parent) {
						if (visitedNode[parent.range.toString()]) {
							return; // already saw
						}
						if (parent?.type === AST_NODE_TYPES.CallExpression) {
							const callee = parent.callee;
							if (callee.type === AST_NODE_TYPES.MemberExpression
								&& callee.property.type == AST_NODE_TYPES.Identifier) {
								if (callee.property.name === 'attr') {
									visitedNode[parent.range.toString()] = true;
									// ignore for .attr(key, null);
									return;
								}
							}
						}
						parent = parent.parent;
					}
					if (node.parent?.type === AST_NODE_TYPES.BinaryExpression) {
						if (node.parent.operator === '!=' || node.parent.operator === '==') {
							// ignore `!= null` and `== null` cases
							return;
						}
					}
					context.report({
						node: node,
						messageId: 'useUndefined',
						fix(fixer) {
							return fixer.replaceText(node, 'undefined');
						}
					});
				}
			},
			TSTypeAnnotation: checkTypeSyntax,
			TSTypeAssertion: checkTypeSyntax,
			TSAsExpression: checkTypeSyntax
		};
	}
});
