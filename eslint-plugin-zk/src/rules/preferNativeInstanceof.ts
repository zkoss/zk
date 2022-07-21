import { AST_NODE_TYPES } from '@typescript-eslint/utils';
import { createRule } from '../util';

export const preferNativeInstanceof = createRule({
	name: 'preferNativeInstanceof',
	meta: {
		type: 'problem',
		docs: {
			description: 'Prefer the `instanceof` operator to `$instanceof` and `isInstance`.',
			recommended: 'error',
		},
		fixable: 'code',
		messages: {
			useInstanceofOperator: 'Use the `instanceof` operator instead.',
		},
		schema: []
	},
	defaultOptions: [],
	create(context) {
		const sourceCode = context.getSourceCode();
		return {
			CallExpression(node) {
				const { callee } = node;
				if (callee.type !== AST_NODE_TYPES.MemberExpression ||
					callee.property.type !== AST_NODE_TYPES.Identifier
				) {
					return;
				}
				const { object } = callee;

				if (callee.property.name === '$instanceof' && node.arguments.length > 0) {
					// Replace `a.$instanceof(A, B, ...)` with `(a instanceof A || a instanceof B || ...)`.
					context.report({
						loc: callee.property.loc,
						messageId: 'useInstanceofOperator',
						fix(fixer) {
							const testedObject = sourceCode.getText(object);
							const tests = node.arguments
								.map(arg => `${testedObject} instanceof ${sourceCode.getText(arg)}`)
								.join(' || ');
							return fixer.replaceText(node, `(${tests})`);
						}
					});
				} else if (callee.property.name === 'isInstance' && node.arguments.length === 1) {
					// Replace `A.isInstance(a)` with `(a instanceof A)`.
					context.report({
						loc: callee.property.loc,
						messageId: 'useInstanceofOperator',
						fix(fixer) {
							const testedObject = sourceCode.getText(node.arguments[0]);
							const type = sourceCode.getText(object);
							return fixer.replaceText(node, `(${testedObject} instanceof ${type})`);
						}
					});
				}
			}
		};
	}
});