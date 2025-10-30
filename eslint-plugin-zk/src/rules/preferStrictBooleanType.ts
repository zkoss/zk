/* preferStrictBooleanType.ts

	Purpose:
		
	Description:
		
	History:
		2:09 PM 2022/7/26, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
import { AST_NODE_TYPES, TSESTree } from '@typescript-eslint/utils';
import { createRule } from '../util';

export const preferStrictBooleanType = createRule({
	name: 'preferStrictBooleanType',
	meta: {
		type: 'problem',
		docs: {
			description: 'Prefer boolean type only.',
			recommended: 'error',
		},
		fixable: 'code',
		messages: {
			useBooleanTypeOnly: 'Use boolean type only.',
		},
		schema: []
	},
	defaultOptions: [],
	create(context) {
		function checkBooleanTypeSyntax(node: TSESTree.TSTypeAnnotation | TSESTree.TSTypeAssertion | TSESTree.TSAsExpression) {
			if (node.typeAnnotation.type === AST_NODE_TYPES.TSUnionType) {
				let booleanType:  TSESTree.TSBooleanKeyword | null | undefined;
				for (const t of node.typeAnnotation.types) {
					if (t.type === AST_NODE_TYPES.TSBooleanKeyword) {
						if (booleanType !== null) {
							booleanType = t;
						}
					} else if (t.type !== AST_NODE_TYPES.TSNullKeyword &&
						t.type !== AST_NODE_TYPES.TSUndefinedKeyword) {
						booleanType = null;
					}
				}

				if (booleanType) {
					context.report({
						node: booleanType,
						messageId: 'useBooleanTypeOnly',
						fix(_fixer) {
							return {
								range: node.typeAnnotation.range,
								text: 'boolean'
							} as never;
						}
					});
				}
			}
		}
		return {
			TSTypeAnnotation: checkBooleanTypeSyntax,
			TSTypeAssertion: checkBooleanTypeSyntax,
			TSAsExpression: checkBooleanTypeSyntax
		};
	}
});
