import { AST_TOKEN_TYPES, TSESLint, TSESTree } from '@typescript-eslint/utils';
import { createRule } from '../util';

type ClassMember =
	| TSESTree.MethodDefinition
	| TSESTree.PropertyDefinition
	| TSESTree.TSAbstractMethodDefinition
	| TSESTree.TSAbstractPropertyDefinition
	| TSESTree.TSParameterProperty;

function isValidAccessModifier(modifier: unknown): modifier is 'public' | 'protected' | 'private' {
	return modifier === 'public' || modifier === 'protected' || modifier === 'private';
}

export const noAccessModifier = createRule({
	name: 'noAccessModifier',
	meta: {
		type: 'problem',
		docs: {
			description: 'All class members should be implicitly public.',
			recommended: 'error',
		},
		fixable: 'code',
		messages: {
			omitAccessModifier: 'Do not specify an access modifier.',
		},
		schema: []
	},
	defaultOptions: [],
	create(context) {
		const sourceCode = context.getSourceCode();
		function checkAccessModifier(node: ClassMember): void {
			// Knowing that `node.accessibility` is not enough; must check with
			// `isValidAccessModifier`.
			if (!isValidAccessModifier(node.accessibility)) {
				return;
			}
			const tokens = sourceCode.getTokens(node);
			const i = tokens.findIndex(({ type, value }) =>
				type === AST_TOKEN_TYPES.Keyword && isValidAccessModifier(value)
			);
			const modifier = tokens[i]!; // eslint-disable-line @typescript-eslint/no-non-null-assertion
			context.report({
				loc: modifier.loc,
				messageId: 'omitAccessModifier',
				fix(fixer: TSESLint.RuleFixer): TSESLint.RuleFix {
					const succeedingComment = sourceCode.getCommentsAfter(modifier);
					// Beware that `tokens[i + 1]` will skip comments:
					// protected /* blablabla */ foo()
					//           ^^^^^^^^^^^^^^^ we want to keep this
					const nextToken: TSESTree.Token = succeedingComment.length ?
						succeedingComment[0]! : // eslint-disable-line @typescript-eslint/no-non-null-assertion
						tokens[i + 1]!; // eslint-disable-line @typescript-eslint/no-non-null-assertion
					return fixer.removeRange([modifier.range[0], nextToken.range[0]]);
					// Ideally, one would write `return fixer.remove(tokens[i])` instead.
					// However, this will leave extra whitespaces.
				}
			});
		}

		return {
			MethodDefinition: checkAccessModifier,
			PropertyDefinition: checkAccessModifier,
			TSAbstractMethodDefinition: checkAccessModifier,
			TSAbstractPropertyDefinition: checkAccessModifier,
			TSParameterProperty: checkAccessModifier,
		};
	}
});