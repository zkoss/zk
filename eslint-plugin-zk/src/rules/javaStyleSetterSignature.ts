import { AST_NODE_TYPES, AST_TOKEN_TYPES, TSESTree } from '@typescript-eslint/utils';
import { createRule } from '../util';

export const javaStyleSetterSignature = createRule({
	name: 'javaStyleSetterSignature',
	meta: {
		type: 'problem',
		docs: {
			description: 'Java-style setters should return `this` and have suggestive parameter names.',
			recommended: 'error',
		},
		fixable: 'code',
		messages: {
			setterReturnThis: 'Java-style setters should return `this`.',
			setterSuggestiveParameter: 'Java-style setters should have suggestive parameter names.',
		},
		schema: []
	},
	defaultOptions: [],
	create(context) {
		const sourceCode = context.getSourceCode();
		function checkJavaStyleSetter(node: TSESTree.MethodDefinition | TSESTree.TSAbstractMethodDefinition) {
			const { key, value: functionExpression } = node;
			if (key.type !== AST_NODE_TYPES.Identifier ||
				!key.name.startsWith('set') || key.name.length <= 3
			) {
				return;
			}

			const { returnType, body, params } = functionExpression;
			if (!key.name.endsWith('_')) { // ignore protected method
				if (!returnType) {
					// Return type annotation is missing.
					let signatureTail: TSESTree.Node | TSESTree.Token = functionExpression;
					if (body) {
						// A function singature must contain a right parenthesis.
						// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
						signatureTail = sourceCode.getTokenBefore(body,
							token => token.type === AST_TOKEN_TYPES.Punctuator && token.value === ')'
						)!;
					}
					context.report({
						loc: {
							start: node.loc.start,
							end: signatureTail.loc.end
						},
						messageId: 'setterReturnThis',
						fix(fixer) {
							return fixer.insertTextAfter(signatureTail, ': this');
						}
					});
				} else if (returnType.typeAnnotation.type !== AST_NODE_TYPES.TSThisType) {
					// Replace existing return type annotation.
					context.report({
						loc: returnType.loc,
						messageId: 'setterReturnThis',
						fix(fixer) {
							return fixer.replaceText(returnType, ': this');
						}
					});
				}
			}

			if (params.length > 0) {
				const firstParam = params[0] as TSESTree.Identifier;
				const oldParamName = firstParam.name;
				// First parameter should resemble the name of the setter function, in particular,
				// if the setter is `setEnglishName`, the first parameter should be `englishName`.
				// It is assumed that indentifiers are ASCII.

				// 01234
				// setEnglishName
				//    ^ -> set to lower case
				// ^^^ -> remove
				const newParamName1 = key.name[3]!.toLowerCase() + key.name.slice(4);
				if (oldParamName === newParamName1) {
					return;
				}
				const newParamName = newParamName1.replace('_', '');
				if (oldParamName === newParamName) {
					return;
				}
				context.report({
					loc: firstParam.loc,
					messageId: 'setterSuggestiveParameter',
					fix(fixer) {
						// There must be a variable with bearing the name of the first parameter.
						// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
						const fixes = context
							.getDeclaredVariables(functionExpression)
							.find(variable => variable.name === oldParamName)!
							.references
							.map(ref => fixer.replaceText(ref.identifier, newParamName));
						const paramType = firstParam.typeAnnotation ? sourceCode.getText(firstParam.typeAnnotation) : '';
						fixes.push(fixer.replaceText(firstParam, newParamName + paramType));
						return fixes;
					}
				});
			}
		}
		return {
			MethodDefinition: checkJavaStyleSetter,
			TSAbstractMethodDefinition: checkJavaStyleSetter,
		};
	}
});