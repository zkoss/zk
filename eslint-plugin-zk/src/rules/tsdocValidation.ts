import { AST_TOKEN_TYPES, AST_NODE_TYPES, TSESTree } from '@typescript-eslint/utils';
// import { createRule, getParserServices } from '../util';
import {
	DocBlock,
	DocComment,
	TextRange,
	TSDocConfiguration,
	TSDocParser,
	TSDocTagDefinition,
	TSDocTagSyntaxKind,
	TSDocMessageId,
	DocParamBlock,
} from '@microsoft/tsdoc';
// import ts from 'typescript';
import { createRule } from '../util';
import { getForSourceFile } from './tsdocConfigCache';

/**
 * Variables containing the substring "TSDoc" is written as "tsdoc" or "Tsdoc".
 * Here, any mentioning of "TSDoc" refers to a TSDoc block comment.
 */

type PropertyNameNode = TSESTree.Node & { key: { type: AST_NODE_TYPES, value?: TSESTree.Literal['value'] } };
type PropertyNameNonComputedNode = TSESTree.Node & { key: TSESTree.Identifier | TSESTree.StringLiteral }

const restOfLinePattern = /.*\n/y;
const indentPattern = /(\s*)\S/y;
const sincePattern = /\s+\d+\.\d+\.\d+\s+/y;
/**
 * ```
 * /\s+\w+(\.\w+)*(\s+-)?\s+(\w+)?/y.exec('  13eee_ee123.1.3\n')
 * ['  13eee_ee123.1.3\n', '.3', undefined, undefined, ... ]
 * 
 * /\s+\w+(\.\w+)*(\s+-)?\s+(\w+)?/y.exec('  13eee_ee123.1.3 - ')
 * ['  13eee_ee123.1.3 - ', '.3', ' -', undefined, ... ]
 * 
 * /\s+\w+(\.\w+)*(\s+-)?\s+(\w+)?/y.exec('  13eee_ee123.1.3 - 23')
 * ['  13eee_ee123.1.3 - 23', '.3', ' -', '23', ... ]
 * 
 * /\s+\w+(\.\w+)*(\s+-)?\s+(\w+)?/y.exec('  13eee_ee123.1.3 23')
 * ['  13eee_ee123.1.3 23', '.3', undefined, '23', ... ]
 * ```
 */
const javadocParamTypePattern = /\s+\w+(\.\w+)*(\s+-)?\s+(\w+)?/y;
const uselessParamPattern = /\s*(\w+(\.\w+)*)?\s*-?\s*\n/y;
const topOfFile = { line: 1, column: 1 };

const builtinTsdocMessages = Object.fromEntries(
	new TSDocConfiguration().allTsdocMessageIds
		.map(messageId => [messageId, `${messageId}: {{unformattedText}}`])
) as Record<TSDocMessageId, string>;

export const tsdocValidation = createRule({
	name: 'tsdocValidation',
	meta: {
		type: 'problem',
		docs: {
			description: 'TSDoc Validation.',
			recommended: 'error',
		},
		fixable: 'code',
		hasSuggestions: true,
		messages: {
			...builtinTsdocMessages,
			cannotLoadTsdocConfig: 'Cannot load tsdoc.json: {{message}}',
			cannotApplyTsdocConfig: 'Cannot apply tsdoc.json: {{message}}',
			multipleTsdocs: 'Found multiple TSDoc associated with me; lines {{tsdocLines}}. Leave at most one TSDoc comment, or consider separating the declaration overloads.',
			annotateInternal: 'Add @internal to the TSDoc, as a name that starts or ends with an underscore should not be exposed.',
			noAnnotateInternal: 'Remove @internal from the TSDoc, as a name that does not start nor end with an underscore should be public.',
			sinceFormat: '@since should be followed by a semver (MAJOR.MINOR.PATCH).',
			paramFormatUnrecognizable: 'The format for this @param block is plain wrong.',
			transformJavadocParam: 'Transform the JavaDoc pattern "@param{{from}}" to the TSDoc pattern "@param{{to}}".',
			uselessParam: 'Remove this useless @param block.',
			tsdocHeaderNewline: 'The start (`/**`) of a multi-line TSDoc should be on a separate line. "{{restOfLine}}"'
		},
		schema: []
	},
	defaultOptions: [],
	create(context) {
		const tsdocConfig = getTsdocConfig();
		if (!tsdocConfig) {
			return {};
		}
		const tsdocParser = new TSDocParser(tsdocConfig);
		const sourceCode = context.getSourceCode();

		return {
			Program() { // Surface all builtin TSDoc errors.
				for (const comment of sourceCode.getAllComments()) {
					const textRange = getTsdocRange(comment);
					if (!textRange) {
						continue;
					}
					const { messages } = tsdocParser.parseRange(textRange).log;
					for (const { textRange, messageId, unformattedText } of messages) {
						context.report({
							loc: {
								start: sourceCode.getLocFromIndex(textRange.pos),
								end: sourceCode.getLocFromIndex(textRange.end)
							},
							messageId,
							data: {
								unformattedText
							}
						});
					}
				}
			},
			MethodDefinition(node){
				transformMethod(node, node);
			} ,
			Property(node) {
				switch (node.value.type) {
					case AST_NODE_TYPES.FunctionExpression:
						transformMethod(node, node);
						break;
				}
			},
			FunctionDeclaration: transformFunction,
			// TSDeclareFunction: transformFunction,
			// TSAbstractMethodDefinition: transformFunction,

			PropertyDefinition: markPropertyInternal,
			TSPropertySignature(node) {
				if (node.parent?.type === AST_NODE_TYPES.TSInterfaceBody) {
					markPropertyInternal(node);
				}
			},
			TSMethodSignature(node) {
				if (node.parent?.type === AST_NODE_TYPES.TSInterfaceBody) {
					markPropertyInternal(node);
				}
			},
			TSAbstractPropertyDefinition: markPropertyInternal,
			TSEnumDeclaration(node) {
				markInternal(node.id, node.id.name);
			},
			TSNamespaceExportDeclaration(node) {
				markInternal(node.id, node.id.name);
			}
		};

		function markPropertyInternal(node: TSESTree.Node & { key: TSESTree.PropertyName, computed?: boolean}) {
			if (!node.computed && node.key.type === AST_NODE_TYPES.Identifier) {
				if (!sourceCode.getCommentsBefore(node).some(comment =>
					comment.value.includes('@internal')
				)) {
					markInternal(node, node.key.name);
				}
			}
		}

		function transformFunction(node: TSESTree.FunctionDeclaration | TSESTree.TSDeclareFunction): void {
			if (node.id) {
				const { parent } = node;
				transformMethod({
					...node,
					key: node.id,
				},
				parent && parent.type === AST_NODE_TYPES.ExportNamedDeclaration ? parent : node);
			}
		}

		function transformMethod(node: PropertyNameNode, commentNode: TSESTree.Node | TSESTree.Token): void {
			if (!isPropertyNameNonComputed(node)) {
				return;
			}
			const tsdoc = ensureSingleTsdoc(node, commentNode);
			if (!tsdoc) {
				return;
			}
			ensureTsdocHeaderNewline(tsdoc);
			const { docComment } = tsdocParser.parseRange(tsdoc);
			ensureInternal(node, docComment, tsdoc);
			for (const block of docComment.customBlocks) {
				switch (block.blockTag.tagName) {
					case '@since':
						validateSince(block);
						break;
				}
			}
			for (const param of docComment.params) {
				transformJavadocParam(param);
			}
		}

		function getTsdocConfig(): TSDocConfiguration | undefined {
			const tsdocConfig = new TSDocConfiguration();
			try {
				const tsdocConfigFile = getForSourceFile(context.getFilename());
				if (!tsdocConfigFile.fileNotFound) {
					if (tsdocConfigFile.hasErrors) {
						throw Error(`\n${tsdocConfigFile.getErrorSummary()}`);
					}
					try {
						tsdocConfigFile.configureParser(tsdocConfig);
					} catch (e) {
						context.report({
							loc: topOfFile,
							messageId: 'cannotApplyTsdocConfig',
							data: {
								message: (e as Error).message
							}
						});
						return;
					}
				}
			} catch (e) {
				context.report({
					loc: topOfFile,
					messageId: 'cannotLoadTsdocConfig',
					data: {
						message: (e as Error).message
					}
				});
				return;
			}
			return tsdocConfig;
		}

		function getTsdocRange(comment: TSESTree.Comment): TextRange | undefined {
			const textRange = TextRange.fromStringRange(sourceCode.text, ...comment.range);
			if (
				comment.type === AST_TOKEN_TYPES.Block &&
				textRange.buffer.startsWith('/**', textRange.pos) &&
				textRange.length >= '/***/'.length
			) {
				return textRange;
			}
			return;
		}

		function isPropertyNameNonComputed(node: PropertyNameNode): node is PropertyNameNonComputedNode {
			switch (node.key.type) {
				case AST_NODE_TYPES.Identifier:
					return true;
				case AST_NODE_TYPES.Literal:
					return typeof node.key.value === 'string';
				default: // Ignore nodes of other types.
					return false;
			}
		}

		function getPropertyNameNonComputed(node: PropertyNameNonComputedNode): string {
			if (node.key.type === AST_NODE_TYPES.Identifier) {
				return node.key.name;
			}
			return node.key.value;
		}

		function ensureSingleTsdoc(node: PropertyNameNonComputedNode, commentNode: TSESTree.Node | TSESTree.Token): TextRange | undefined {
			const comments = sourceCode.getCommentsBefore(commentNode);
			const tsdocs = new Array<TextRange>();
			const tsdocLocs = new Array<string>();
			for (const comment of comments) {
				const textRange = getTsdocRange(comment);
				if (textRange) {
					tsdocs.push(textRange);

					const startLine = comment.loc.start.line;
					const endLine = comment.loc.end.line;
					tsdocLocs.push(startLine === endLine ? `${startLine}` : `${startLine}-${endLine}`);
				}
			}
			if (tsdocs.length > 1) {
				context.report({
					node: node.key,
					messageId: 'multipleTsdocs',
					data: {
						tsdocLines: tsdocLocs.join(', '),
					},
				});
				return;
			}
			if (tsdocs.length === 0) {
				markInternal(node, getPropertyNameNonComputed(node));
			}
			return tsdocs[0];
		}

		function ensureTsdocHeaderNewline(tsdoc: TextRange): void {
			const startOfDoc = sourceCode.getLocFromIndex(tsdoc.pos);
			if (startOfDoc.line === sourceCode.getLocFromIndex(tsdoc.end).line) {
				return; // TSDoc is single line.
			}
			let firstNonAsterisk = tsdoc.pos + 3;
			while (tsdoc.buffer[firstNonAsterisk] === '*') {
				++firstNonAsterisk;
			}
			restOfLinePattern.lastIndex = firstNonAsterisk; 
			const matchResult = restOfLinePattern.exec(tsdoc.buffer);
			if (!matchResult) {
				return;
			}
			const restOfLine = matchResult[0];
			if (!restOfLine || restOfLine.startsWith('\n')) {
				return;
			}
			context.report({
				loc: {
					start: startOfDoc,
					end: {
						line: startOfDoc.line,
						column: startOfDoc.column + 3,
					}
				},
				messageId: 'tsdocHeaderNewline',
				data: {
					restOfLine,
				},
				fix(fixer) {
					const nextNewline = firstNonAsterisk + restOfLine.length;
					const nextAsterisk = tsdoc.buffer.indexOf('*', nextNewline);
					const indent = tsdoc.buffer.substring(nextNewline, nextAsterisk);
					return fixer.replaceTextRange([firstNonAsterisk, nextNewline], `\n${indent}*${restOfLine}`);
				}
			});
		}

		function markInternal(node: TSESTree.Node & { key?: TSESTree.Node }, name: string) {
			if (!name.startsWith('_') && !name.endsWith('_')) {
				return;
			}
			const {text} = sourceCode;
			const newline = text.lastIndexOf('\n', node.range[0]);
			indentPattern.lastIndex = newline + 1;
			const matchResult = indentPattern.exec(text);
			if (!matchResult) {
				return;
			}
			const indent = matchResult[1];
			if (!indent) {
				return;
			}
			context.report({
				node: node.key ?? node,
				messageId: 'annotateInternal',
				fix(fixer) {
					return fixer.insertTextAfterRange([newline, newline + 1], `${indent}/** @internal */\n`);
				}
			});
		}

		function ensureInternal(node: PropertyNameNonComputedNode, docComment: DocComment, tsdoc: TextRange): void {
			const name = getPropertyNameNonComputed(node);
			const isAnnotatedInternal = docComment.modifierTagSet.isInternal();
			if (name.startsWith('_') || name.endsWith('_')) {
				if (!isAnnotatedInternal) {
					context.report({
						node: node.key,
						messageId: 'annotateInternal',
						fix(fixer) {
							let beforeLastAsterisk = tsdoc.end - 2;
							while (tsdoc.buffer[beforeLastAsterisk] === '*') {
								--beforeLastAsterisk;
							}
							if (sourceCode.getLocFromIndex(tsdoc.pos).line === sourceCode.getLocFromIndex(tsdoc.end).line) {
								// Single line TSDoc comment.
								const tag = tsdoc.buffer[beforeLastAsterisk] === ' ' ? '@internal ' : ' @internal ';
								return fixer.insertTextAfterRange([beforeLastAsterisk, beforeLastAsterisk + 1], tag);
							}
							//// Multi-line TSDoc comment.

							// docComment.modifierTagSet.addTag(new DocBlockTag({
							// 	tagName: '@internal',
							// 	configuration: tsdocConfiguration,
							// }));
							// return fixer.replaceTextRange([tsdoc.pos, tsdoc.end], parserContext.docComment.emitAsTsdoc());
							//// The commented code above is a simple fix that will destroy the original comment format.

							const lastNewline = tsdoc.buffer.lastIndexOf('\n', beforeLastAsterisk);
							const indent = tsdoc.buffer.substring(lastNewline + 1, beforeLastAsterisk + 1);
							return fixer.insertTextAfterRange([lastNewline, lastNewline + 1], `${indent}* @internal\n`);
						}
					});
				}
			} else if (isAnnotatedInternal) {
				const internal = docComment.modifierTagSet.tryGetTag(new TSDocTagDefinition({
					tagName: '@internal',
					syntaxKind: TSDocTagSyntaxKind.ModifierTag,
				}));
				const textRange = internal!.getTokenSequence().getContainingTextRange();
				context.report({
					loc: {
						start: sourceCode.getLocFromIndex(textRange.pos),
						end: sourceCode.getLocFromIndex(textRange.end),
					},
					messageId: 'noAnnotateInternal',
					fix(fixer) {
						return fixer.removeRange([textRange.pos, textRange.end]);
					}
				});
			}
		}

		function validateSince(block: DocBlock): void {
			const textRange = block.blockTag.getTokenSequence().getContainingTextRange();
			sincePattern.lastIndex = textRange.end;
			if (!sincePattern.test(textRange.buffer)) {
				context.report({
					loc: {
						start: sourceCode.getLocFromIndex(textRange.pos),
						end: sourceCode.getLocFromIndex(textRange.end),
					},
					messageId: 'sinceFormat',
				});
			}
		}

		function transformJavadocParam(param: DocParamBlock): void {
			const { pos: start, end, buffer } = param.blockTag.getTokenSequence().getContainingTextRange();
			uselessParamPattern.lastIndex = end;
			if (uselessParamPattern.test(buffer)) {
				const lineEnd = uselessParamPattern.lastIndex;
				context.report({
					loc: {
						start: sourceCode.getLocFromIndex(start),
						end: sourceCode.getLocFromIndex(lineEnd),
					},
					messageId: 'uselessParam',
					fix(fixer) {
						return fixer.removeRange([
							buffer.lastIndexOf('\n', start) + 1,
							lineEnd,
						]);
					}
				});
				return;
			}

			javadocParamTypePattern.lastIndex = end;
			const matchResult = javadocParamTypePattern.exec(buffer);
			if (!matchResult) {
				context.report({
					loc: {
						start: sourceCode.getLocFromIndex(start),
						end: sourceCode.getLocFromIndex(end),
					},
					messageId: 'paramFormatUnrecognizable',
				});
				return;
			}
			const [from, _, hyphen, secondItem] = matchResult;
			if (hyphen || !secondItem) {
				return;
			}

			const replacementEnd = javadocParamTypePattern.lastIndex;
			const to = ` ${secondItem} -`;
			context.report({
				loc: {
					start: sourceCode.getLocFromIndex(start),
					end: sourceCode.getLocFromIndex(replacementEnd),
				},
				messageId: 'transformJavadocParam',
				data: { from, to },
				fix(fixer) {
					return fixer.replaceTextRange([end, replacementEnd], to);
				}
			});
		}
	}
});

/**
 * The format of `Comment.value` returned by `getCommentsBefore`:
 * 1. Single line comments will have their `//` stripped off. Leaving everything else intact.
 * 2. Multi-line comments will have their enclosing delimiters stripped off. Leaving everything else intact.
 */