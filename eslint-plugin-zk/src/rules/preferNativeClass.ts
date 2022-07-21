import { AST_NODE_TYPES } from '@typescript-eslint/utils';
import { createRule } from '../util';
import { spawnSync } from 'child_process';
import path from 'path';

export const preferNativeClass = createRule({
	name: 'preferNativeClass',
	meta: {
		type: 'problem',
		docs: {
			description: 'Replace `zk.$extends` with ES6 class.',
			recommended: 'error',
		},
		fixable: 'code',
		messages: {
			replaceWithES6Class: 'Replace `zk.$extends` with ES6 class.',
		},
		schema: []
	},
	defaultOptions: [],
	create(context) {
		return {
			CallExpression(node) {
				const { callee } = node;
				if (callee.type !== AST_NODE_TYPES.MemberExpression ||
					callee.object.type !== AST_NODE_TYPES.Identifier ||
					callee.object.name !== 'zk' ||
					callee.property.type !== AST_NODE_TYPES.Identifier ||
					callee.property.name !== '$extends'
				) {
					return;
				}

				context.report({
					loc: callee.property.loc,
					messageId: 'replaceWithES6Class',
					fix(_fixer) {
						// Invoke jscodeshift to fix for us.
						spawnSync(
							'npx',
							[
								'jscodeshift',
								'-t',
								path.resolve(__dirname, '..', 'transform.js'),
								// We are using ESLint 8, so `getPhysicalFilename` exists.
								// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
								context.getPhysicalFilename!(),
							],
							{
								stdio: 'ignore'
							}
						);
						// Only return after codemod is done.
						return [];
					}
				});
			}
		};
	}
});