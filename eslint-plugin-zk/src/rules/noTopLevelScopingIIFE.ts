import { AST_NODE_TYPES } from '@typescript-eslint/utils';
import { createRule } from '../util';

export const noTopLevelScopingIIFE = createRule({
	name: 'noTopLevelScopingIIFE',
	meta: {
		type: 'problem',
		docs: {
			description: 'Avoid top-level IIFE for scoping in Typescript modules.',
			recommended: 'error',
		},
		fixable: 'code',
		messages: {
			removeTopLevelScopingIIFE: 'Remove top-level IIFE for scoping.',
		},
		schema: []
	},
	defaultOptions: [],
	create(context) {
		return {
			CallExpression(node) {
				const { parent, callee } = node;

				if (callee.type !== AST_NODE_TYPES.ArrowFunctionExpression &&
					callee.type !== AST_NODE_TYPES.FunctionExpression
				) {
					return; // not iife
				}

				if (parent?.type !== AST_NODE_TYPES.ExpressionStatement ||
					parent.parent?.type !== AST_NODE_TYPES.Program
				) {
					return; // not top-level
				}

				if (callee.params.length !== 0 ||
					callee.body.type !== AST_NODE_TYPES.BlockStatement ||
					callee.async ||
					callee.generator ||
					callee.expression
				) {
					return; // not for scoping
				}

				const { body } = callee.body;
				context.report({
					node,
					messageId: 'removeTopLevelScopingIIFE',
					fix(fixer) {
						// Remove the whole statement if the body is empty.
						if (body.length === 0) {
							return fixer.remove(parent);
						}
						// `body` is guaranteed to contain something from this point on.
						return [
							// (function () {
							// ^^^^^^^^^^^^^^ -> remove until reaching the first enclosing statement
							fixer.removeRange([
								parent.range[0],
								// One character after the left parenthesis of the body statement
								callee.body.range[0] + 1
							]),
							// })();
							// ^^^^^ -> remove the trailing semicolon as well
							fixer.removeRange([
								// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
								body.at(-1)!.range[1],
								// The end of the statement includes the trailing semicolon.
								parent.range[1]
							]),
						];
					}
				});
			}
		};
	}
});