/* noMixedHtml.ts

	Purpose:
		
	Description:
		
	History:
		11:15 AM 2023/12/25, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/

/**
 * @fileoverview Checks for missing encoding when concatenating HTML strings
 * @author Mikko Rantanen
 */
import { TSESTree } from '@typescript-eslint/utils';
import {tree} from '../tree';
import {re} from '../re';
import {RulesJs, PassThroughRule, FunctionRule} from '../RulesJs';
import {type Rule} from 'eslint';

// -----------------------------------------------------------------------------
// Rule Definition
// -----------------------------------------------------------------------------


export const noMixedHtml = function (context: Rule.RuleContext) {

	// Default options.
	let htmlVariableRules = ['html/i'] as (RegExp| string | {object: string, property: string | RegExp})[];
	let htmlFunctionRules = ['AsHtml'] as (RegExp| string)[];
	let sanitizedVariableRules = [] as (RegExp| string | {object: string, property: string | RegExp})[];
	let functionRules = {
		'.join': { passthrough: { obj: true, args: true } },
		'.toString': { passthrough: { obj: true } },
		'.substr': { passthrough: { obj: true } },
		'.substring': { passthrough: { obj: true } },
	} as Record<string, FunctionRule>;

	// Read the user specified options.
	if (context.options.length > 0) {
		// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
		const opts = context.options[0] as {
			htmlVariableRules?: (RegExp | string)[], htmlFunctionRules?: (RegExp| string)[],
			sanitizedVariableRules?: (RegExp | string)[], functions?: Record<string, FunctionRule>};

		htmlVariableRules = opts.htmlVariableRules || htmlVariableRules;
		htmlFunctionRules = opts.htmlFunctionRules || htmlFunctionRules;
		sanitizedVariableRules = opts.sanitizedVariableRules || sanitizedVariableRules;
		functionRules = opts.functions || functionRules;
	}

	// Turn the name rules from string/string array to regexp.
	// htmlVariableRules = htmlVariableRules.map(re.toRegexp);
	htmlVariableRules = htmlVariableRules.map((rule, _, __) => {
		if (typeof rule === 'string') {
			return re.toRegexp(rule);
		} else if (!(rule instanceof RegExp) && rule.property) {
			return {
				object: rule.object,  // Assuming object is always a string
				property: re.toRegexp(rule.property as never)
			};
		}
		return rule;
	}) as never;
	htmlFunctionRules = htmlFunctionRules.map(re.toRegexp as never);
	sanitizedVariableRules = sanitizedVariableRules.map((rule, _, __) => {
		if (typeof rule === 'string') {
			return re.toRegexp(rule);
		} else if (!(rule instanceof RegExp) && rule.property) {
			return {
				object: rule.object,  // Assuming object is always a string
				property: re.toRegexp(rule.property as never)
			};
		}
		return rule;
	}) as never;

	const allRules = new RulesJs({
		functionRules: functionRules as never
	});

	// Expression stack for tracking the topmost expression that is marked
	// XSS-candidate when we find '<html>' strings.
	const exprStack = [] as {node: TSESTree.Node, xss?: boolean, sanitized?: boolean | undefined}[];

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------


	/**
	 * Checks whether the node represents a passthrough function.
	 *
	 * @param {Node} node - Node to check.
	 *
	 * @returns {bool} - True, if the node is an array join.
	 */
	const getPassthrough = function (node: TSESTree.Node): PassThroughRule | false {

		if (node.type !== 'CallExpression')
			return false;

		const rules = allRules.getFunctionRules(node);
		return rules.passthrough as PassThroughRule;
	};

	/**
	 * Gets all descendants that we know to affect the possible output string.
	 *
	 * @param {Node} node - Node for which to get the descendants. Inclusive.
	 * @param {Node} _children - Collection of descendants. Leave null.
	 * @param {Node} _hasRecursed -
	 *      Defines whether the function has recursed into inner structures.
	 *      Leave false.
	 *
	 * @returns {Node[]} - Flat list of descendant nodes.
	 */
	const getDescendants = function (node: TSESTree.Node, _children?: undefined | TSESTree.Node[], _hasRecursed?: boolean): TSESTree.Node[] {

		// The children array may be passed during recursion.
		if (_children === undefined)
			_children = [];

		// Handle the special case of .join() function.
		const passthrough = getPassthrough(node);
		if (passthrough) {
			const cn = node as TSESTree.CallExpression;
			// Get the descedants from the array and the function argument.
			if (passthrough.obj) {
				getDescendants((cn.callee as TSESTree.MemberExpression).object, _children, _hasRecursed);
			}

			if (passthrough.args) {
				cn.arguments.forEach(function (a) {
					getDescendants(a, _children, _hasRecursed);
				});
			}

			return _children;
		}

		// Check the expression type.
		if (node.type === 'CallExpression' ||
			node.type === 'NewExpression' ||
			node.type === 'ThisExpression' ||
			node.type === 'ObjectExpression' ||
			node.type === 'FunctionExpression' ||
			node.type === 'UnaryExpression' ||
			node.type === 'UpdateExpression' ||
			node.type === 'MemberExpression' ||
			node.type === 'SequenceExpression' ||
			node.type === 'Literal' ||
			node.type === 'Identifier' ||
			(_hasRecursed && node.type === 'ArrayExpression')
		) {

			// Basic expressions that won't be reflected further.
			_children.push(node);

		} else if (node.type === 'ArrayExpression') {

			// For array nodes, get the descendant nodes.
			node.elements.forEach(function (e) {
				getDescendants(e as never, _children, true);
			});

		} else if (node.type === 'BinaryExpression') {

			// Binary expressions concatenate strings.
			//
			// Recurse to both left and right side.
			getDescendants(node.left, _children, true);
			getDescendants(node.right, _children, true);

		} else if (node.type === 'LogicalExpression') {

			// Binary expressions concatenate strings.
			//
			// Recurse to both left and right side.
			getDescendants(node.left, _children, true);
			getDescendants(node.right, _children, true);

		} else if (node.type === 'AssignmentExpression') {

			// There might be assignment expressions in the middle of the node.
			// Use the assignment identifier as the descendant.
			//
			// The assignment itself will be checked with its own descendants
			// check.
			getDescendants(node.left, _children, _hasRecursed);

		} else if (node.type === 'ConditionalExpression') {

			getDescendants(node.alternate, _children, _hasRecursed);
			getDescendants(node.consequent, _children, _hasRecursed);
		} else if (node.type === 'TSNonNullExpression') {
			_children.push(node.expression);
		}

		return _children;
	};

	/**
	 * Checks whether the node is safe for XSS attacks.
	 *
	 * @param {Node} node - Node to check.
	 *
	 * @returns {bool} - True, if the node is XSS safe.
	 */
	const isXssSafe = function (node: TSESTree.Node) {

		// See if the item is commented to be safe.
		if (isCommentedSafe(node))
			return true;

		// Literal nodes and function expressions are okay.
		if (node.type === 'Literal' ||
			node.type === 'FunctionExpression' ||
			(node.type === 'UpdateExpression' && (node.operator === '++' || node.operator === '--'))) {
			return true;
		}

		// Identifiers and member expressions are okay if they resolve to an
		// HTML name.
		if (node.type === 'Identifier' ||
			node.type === 'MemberExpression') {

			if (isSanitizedVariable(node)) return true;

			if (node.type === 'Identifier') {
				if (node.name === 'undefined' || node.name === 'null') return true;

				// if the variable is initialized with a literal but not with Html, it is safe
				const parent: TSESTree.VariableDeclarator | undefined = (context.getScope().variables.find(v => v.name === node.name)?.identifiers[0] as {
					parent?: TSESTree.VariableDeclarator
				})?.parent;
				if (parent && parent.init && (
					(parent.init.type === 'Literal'&& !isHTMLLiteral(parent.init)) ||
					(parent.init.type === 'ConditionalExpression' &&
						parent.init.consequent.type === 'Literal' &&
						parent.init.alternate.type === 'Literal'))) {
					return true;
				}
				const nodeParent = node.parent as TSESTree.VariableDeclarator;
				if (nodeParent.type === 'VariableDeclarator' &&
					nodeParent.init && (
					nodeParent.init.type == 'UnaryExpression' || (
						nodeParent.init.type == 'LogicalExpression'
						&& nodeParent.init.left.type == 'UnaryExpression')
				)) {
					return true;
				}

				// ignore check condition expression
				if (node.parent!.type === 'BinaryExpression' && (node.parent as TSESTree.BinaryExpression).operator.includes('=')) {
					return true;
				}
			}

			// isHtmlVariable handles both Identifiers and member expressions.
			return isHtmlVariable(node);
		}

		if (node.type === 'ThisExpression') {
			if (isSanitizedVariable(node)) return true;

			return isHtmlVariable(node.parent as never);
		}

		// Encode calls are okay.
		if (node.type === 'CallExpression') {

			return isHtmlOutputFunction(node.callee);
		}

		// Assume unsafe.
		return false;
	};

	/**
	 * Check for whether the function identifier refers to an encoding function.
	 *
	 * @param {Identifier} func - Function identifier to check.
	 *
	 * @returns {bool} True, if the function is an encoding function.
	 */
	const isHtmlOutputFunction = function (func: TSESTree.Node) {

		return allRules.getFunctionRules(func).htmlOutput ||
			re.any(tree.getFullItemName(func), htmlFunctionRules as never);
	};


	/**
	 * Checks whether the function uses raw HTML input.
	 *
	 * @param {Identifier} func - Function identifier to check.
	 *
	 * @returns {bool} True, if the function is unsafe.
	 */
	const functionAcceptsHtml = function (func: TSESTree.Node): boolean {
		return !!allRules.getFunctionRules(func).htmlInput;
	};

	/**
	 * Checks whether the node-tree contains XSS-safe data.
	 *
	 * Reports error to ESLint.
	 *
	 * @param {Node} node - Root node to check.
	 * @param {Node} target
	 *      Target node the root is used for. Affects some XSS checks.
	 */
	const checkForXss = function (node: TSESTree.Node, target: TSESTree.Node) {

		// Skip functions.
		// This stops the following from giving errors:
		// > htmlEncoder = function() {}
		if (node.type === 'FunctionExpression' ||
			node.type === 'ObjectExpression')
			return;

		// Get the rules.
		const targetRules = allRules.get(target);

		// Get the descendants.
		const nodes = getDescendants(node);

		// Check each descendant.
		nodes.forEach(function (childNode: TSESTree.Node) {

			// Return if the parameter is marked as safe in the current context.
			if (targetRules.safe === true) {
				return;
			} else if (Array.isArray(targetRules.safe) &&
				targetRules.safe.indexOf(tree.getNodeName(childNode)) !== -1) {
				return;
			}

			// Node is okay, if it is safe.
			if (isXssSafe(childNode))
				return;

			// Node wasn't deemed okay. Report error.
			let msg = 'Unencoded input \'{{ identifier }}\' used in HTML context';
			if (childNode.type === 'CallExpression') {
				msg = 'Unencoded return value from function \'{{ identifier }}\' ' +
					'used in HTML context';
				childNode = childNode.callee;
			}

			let identifier = null;
			if (childNode.type === 'ObjectExpression')
				identifier = '[Object]';
			else if (childNode.type === 'ArrayExpression')
				identifier = '[Array]';
			else
				identifier = context.sourceCode.getText(childNode as never);

			context.report({
				node: childNode as never,
				message: msg,
				data: { identifier: identifier }
			});
		});
	};

	/**
	 * Checks whether the node uses HTML.
	 *
	 * @param {Node} node - Node to check.
	 *
	 * @returns {bool} True, if the node uses HTML.
	 */
	const usesHtml = function (node: TSESTree.Node): boolean {

		// Check the node type.
		if (node.type === 'CallExpression') {

			// Check the valid call expression callees.
			return functionAcceptsHtml(node.callee);

		} else if (node.type === 'AssignmentExpression') {

			// Assignment operator.
			// x = y
			// HTML-name on the left indicates html expression.
			return isHtmlVariable(node.left);

		} else if (node.type === 'VariableDeclarator') {

			// Variable declaration.
			// var x = y
			// HTML-name as the variable name indicates html expression.
			return isHtmlVariable(node.id);

		} else if (node.type === 'Property') {

			// Property declaration.
			// x: y
			// HTML-name as the key indicates html property.
			return isHtmlVariable(node.key);

		} else if (node.type === 'ArrayExpression') {

			// Array expression.
			// [ a, b, c ]
			return usesHtml(node.parent as never);

		} else if (node.type === 'ReturnStatement') {

			// Return statement.
			const func = tree.getParentFunctionIdentifier(node);
			if (!func) return false;

			return isHtmlFunction(func);

		} else if (node.type === 'ArrowFunctionExpression') {

			// Return statement.
			const func = tree.getParentFunctionIdentifier(node);
			if (!func) return false;

			return isHtmlFunction(func);
		}

		return false;
	};

	/**
	 * Checks whether the node meets the criteria of storing HTML content.
	 *
	 * Reports error to ESLint.
	 *
	 * @param {Node} node - The node to check.
	 */
	const checkHtmlVariable = function (node: TSESTree.Node) {

		const msg = 'Non-HTML variable \'{{ identifier }}\' is used to store raw HTML';
		if (!isXssSafe(node)) {
			context.report({
				node: node as never,
				message: msg,
				data: {
					identifier: context.sourceCode.getText(node as never)
				}
			});
		}
	};

	/**
	 * Checks whether the node meets the criteria of storing HTML content.
	 *
	 * Reports error to ESLint.
	 *
	 * @param {Node} node - The node to check.
	 * @param {Node} fault
	 *      The node that causes the fail and should be reported as error location.
	 */
	const checkHtmlFunction = function (node: TSESTree.Node, fault: TSESTree.Node) {

		const msg = 'Non-HTML function \'{{ identifier }}\' returns HTML content';
		if (!isXssSafe(node)) {
			context.report({
				node: fault as never,
				message: msg,
				data: {
					identifier: context.sourceCode.getText(node as never)
				}
			});
		}
	};

	/**
	 * Checks whether the node meets the criteria of storing HTML content.
	 *
	 * Reports error to ESLint.
	 *
	 * @param {Node} node - The node to check.
	 */
	const checkFunctionAcceptsHtml = function (node: TSESTree.Node) {

		if (!functionAcceptsHtml(node)) {
			context.report({
				node: node as never,
				message: 'HTML passed in to function \'{{ identifier }}\'',
				data: {
					identifier: context.sourceCode.getText(node as never)
				}
			});
		}
	};

	/**
	 * Checks whether the node name matches the variable naming rule.
	 *
	 * @param {Node} node - Node to check
	 *
	 * @returns {bool} True, if the node matches HTML variable naming.
	 */
	const isHtmlVariable = function (node: TSESTree.Node) {

		const identifierNode = tree.getIdentifier(node);
		if (!identifierNode) return false;
		if (identifierNode.type === 'Identifier' && identifierNode.parent!.type === 'MemberExpression') {

			const parent = identifierNode.parent as TSESTree.MemberExpression;

			// ignore namespace type, for example zhtml.xxx or zul.wgt.HTML
			const source = context.sourceCode.getText(parent as never);
			if (source.startsWith('zhtml.') || source.startsWith('zul.') || source.startsWith('zk.')) {
				return false;
			}
			if (htmlVariableRules.some(function (rule) {
				if (typeof rule === 'object' && !(rule instanceof RegExp) && rule.object && rule.property) {
					const objectMatch = (parent.object as TSESTree.Identifier).name === rule.object ||
							rule.object == 'this' && parent.object.type === 'ThisExpression';

					const propertyMatch = (rule.property as RegExp).test((parent.object as TSESTree.Identifier).name);
					return objectMatch && propertyMatch;
				}
				return false;
			})) {
				return true;
			}
		}

		return htmlVariableRules.some(rule => {
			if (rule instanceof RegExp) {
				return rule.test(identifierNode.name);
			}
			return false;
		});
	};

	const isSanitizedVariable = function (node: TSESTree.Node | undefined): boolean {

		node = tree.getIdentifier(node!) as unknown as TSESTree.Identifier | undefined;
		if (!node) return false;
		if (node.type === 'Identifier' && node.parent!.type === 'MemberExpression') {

			const parent = node.parent;
			if (sanitizedVariableRules.some(rule => {
				if (typeof rule === 'object' && !(rule instanceof  RegExp) && rule.object && rule.property) {
					const objectMatch = ((parent as TSESTree.MemberExpression).object as TSESTree.Identifier).name === rule.object ||
						rule.object == 'this' && (parent as TSESTree.MemberExpression).object.type === 'ThisExpression';

					const propertyMatch = (rule.property as RegExp).test(((parent as TSESTree.MemberExpression).property as TSESTree.Identifier).name);
					return objectMatch && propertyMatch;
				}
				return false;
			})) {
				return true;
			}
		}

		return sanitizedVariableRules.some(rule => {
			if (rule instanceof RegExp) {
				return rule.test((node as TSESTree.Identifier).name);
			}
			return false;
		});
	};
	/**
	 * Checks whether the node name matches the function naming rule.
	 *
	 * @param {Node} node - Node to check
	 *
	 * @returns {bool} True, if the node matches HTML function naming.
	 */
	const isHtmlFunction = function (node: TSESTree.Node | undefined): boolean {

		// Ensure we can get the identifier.
		node = tree.getIdentifier(node!) as TSESTree.Identifier | undefined;
		if (!node) return false;

		// Make the check against the function naming rule.
		return re.any(node.name, htmlFunctionRules as never);
	};

	/**
	 * Checks whether the current node may infect the stack with XSS.
	 *
	 * @param {Node} node - Current node.
	 *
	 * @returns {bool} True, if the node can infect the stack.
	 */
	const canInfectXss = function (node: TSESTree.Node) {

		// If we got nothing in the stack, there's nothing to infect.
		if (exprStack.length === 0)
			return false;

		// Ensure the node to check is used as part of a 'parameter chain' from
		// the top stack node.
		//
		// This 'parameter chain' is the group of nodes that directly affect the
		// node result. It ignores things like function expression argument
		// lists and bodies, etc.
		//
		// We don't want to trigger xss checks in case the identifier
		// is the parent object of a function call expression for
		// example:
		// > html.encode( text )
		const top = exprStack[exprStack.length - 1]!.node;
		let parent = node;
		do {
			const child = parent;
			parent = parent.parent!;

			if (!tree.isParameter(child, parent)) {
				return false;
			}

		} while (parent !== top);

		// Assume true.
		return true;
	};

	/**
	 * Pushes node to the expression stack.
	 *
	 * @param {Node} node - Node to push.
	 */
	const pushNode = function (node: TSESTree.Node): void {

		exprStack.push({ node: node });
	};

	const isHTMLLiteral = function (node: TSESTree.Literal): boolean {
		return !isCommentedSafe(node) && !!/<\/?[a-z]/.exec(node.value as string);
	};

	/**
	 * Pops a node from the expression stack and checks it for XSS issues.
	 */
	const exitNode = function () {

		// Quick checks for whether the node is even vulnerable to XSS.
		const expr = exprStack.pop()!;
		if (!expr.xss && !usesHtml(expr.node))
			return;

		// Now we should know there is HTML involved somewhere.

		// Check whether the node has been commented safe.
		if (isCommentedSafe(expr.node))
			return;

		// Check the node based on its type.
		if (expr.node.type === 'CallExpression') {
			if (allRules.get(expr.node).sanitized)
				return;

			const nodes = expr.node.arguments.map((x) => getDescendants((x))).flat();
			const hasUnSafeHTML = nodes.some((x) => x.type === 'Literal' && isHTMLLiteral(x));

			// more check if arguments are not literal or sanitized
			if (expr.sanitized)  {
				if (!hasUnSafeHTML || !nodes.some((x) => x.type !== 'Literal' && !allRules.get(x).sanitized)) {
					return;
				}
			}

			// Call expression.
			//
			// Ensure the function accepts HTML and none of the arguments have
			// XSS issues.
			if (hasUnSafeHTML || !nodes.some(x => x.type === 'Literal') /*if all variables, we should check*/) {
				checkFunctionAcceptsHtml(expr.node.callee);
			}
			expr.node.arguments.forEach(function (a) {
				checkForXss(a, expr.node);
			});

		} else if (expr.node.type === 'AssignmentExpression') {

			if (allRules.get(expr.node.right).sanitized) return;

			// more check if right side is not literal or sanitized
			if (expr.sanitized)  {
				const nodes = getDescendants(expr.node.right);
				const hasUnSafeHTML = nodes.some((x) => x.type === 'Literal' && isHTMLLiteral(x));
				if (!hasUnSafeHTML || !nodes.some((x) => x.type !== 'Literal' && !allRules.get(x).sanitized)) {
					return;
				}
			}
			// Assignment.
			//
			// Ensure the target variable is HTML compatible and the assigned
			// value doesn't have XSS issues.
			checkHtmlVariable(expr.node.left);
			checkForXss(expr.node.right, expr.node);

		} else if (expr.node.type === 'VariableDeclarator') {
			if ((expr.node.init && allRules.get(expr.node.init).sanitized) || expr.sanitized) return;

			// New variable initialization.
			//
			// Ensure the target variable is HTML compatible and the assigned
			// value doesn't have XSS issues.
			// ignore class type
			if (!expr.node.init || expr.node.init.type !== 'ClassExpression') {
				checkHtmlVariable(expr.node.id);
			}
			if (expr.node.init && (expr.node.init.type !== 'CallExpression' || expr.node.init.callee.type !== 'FunctionExpression')) {
				checkForXss(expr.node.init, expr.node);
			}

		} else if (expr.node.type === 'Property') {
			if (expr.sanitized) return; // false alarm

			// Property declaration inside an object declaration.
			//
			// Ensure the target property is HTML compatible and the assigned
			// value doesn't have XSS issues.
			checkHtmlVariable(expr.node.key);
			checkForXss(expr.node.value, expr.node);

		} else if (expr.node.type === 'ReturnStatement') {

			// Return statement.
			//
			// Make sure the function we are returning from is compatible
			// with a HTML return value and there are no XSS issues in the
			// value returned.

			// Get the closest function scope.
			const func = tree.getParentFunctionIdentifier(expr.node);
			if (!func) return;

			if (expr.sanitized) return; // false alarm

			checkHtmlFunction(func, expr.node);
			checkForXss(expr.node.argument!, expr.node);

		} else if (expr.node.type === 'ArrowFunctionExpression') {

			// Arrow function expression.
			//
			// Make sure the function we are returning from is compatible
			// with a HTML return value and there are no XSS issues in the
			// value returned.

			// Get the closest function scope.
			const func = tree.getParentFunctionIdentifier(expr.node);
			if (!func) return;

			checkHtmlFunction(func, func);
			checkForXss(expr.node.body, expr.node);
		}
	};

	const markParentXSS = function () {

		// Ensure the current node is XSS candidate.
		const expr = exprStack.pop()!;
		if (!expr.xss && !usesHtml(expr.node))
			return;

		// Mark the parent element as XSS candidate.
		const candidate = getXssCandidateParent(expr.node);
		if (candidate) {
			candidate.xss = true;
			candidate.sanitized = expr.sanitized;
		}
	};

	/**
	 * Checks whether the given node is commented to be safe from HTML.
	 *
	 * @param {Node} node - The node to check for the comments.
	 *
	 * @returns {bool} True, if the node is commented safe.
	 */
	const isCommentedSafe = function (node: TSESTree.Node) {

		while (node && (
			node.type === 'ArrayExpression' ||
			node.type === 'Identifier' ||
			node.type === 'Literal' ||
			node.type === 'CallExpression' ||
			node.type === 'BinaryExpression' ||
			node.type === 'MemberExpression')) {

			if (nodeHasSafeComment(node))
				return true;

			node = getCommentParent(node)!;
		}

		return false;
	};

	/**
	 * Gets a parent node that might have a comment that is seemingly
	 * attached to the current node.
	 *
	 * This might differ from normal parent node in cases where the
	 * physical location of the node isn't at the start of the parent:
	 *
	 * /comment/ a + b
	 *
	 * Here the comment is attached to the binary expression node 'a+b' instead
	 * of the a 'a' identifier node.
	 *
	 * However 'a' should still be considered commented - but 'b' isn't.
	 *
	 * However this function also handles situation such as
	 * /comment/ ( a + b )
	 * Where the comment should count for both a and b.
	 *
	 * @param {Node} node - The node to get the parent for.
	 *
	 * @returns {Node} The practical parent node.
	 */
	const getCommentParent = function (node: TSESTree.Node) {

		let parent = node.parent;
		if (!parent) return parent;

		// Call expressions don't cause comment inheritance:
		// /comment/ foo( unsafe() )
		//
		// Shouldn't equal:
		// foo( /comment/ unsafe() )
		if (parent.type === 'CallExpression')
			return null;

		// Binary expressions are a bit confusing when it comes to comment
		// parenting. /comment/ x + y belongs to the binary expression instead
		// of 'x'.
		if (parent.type === 'BinaryExpression') {

			// If the node is left side of binary expression, return parent no
			// matter what.
			if (node === parent.left)
				return parent;

			// Get the closest parenthesized binary expression.
			while (parent &&
			parent.type === 'BinaryExpression' &&
			!hasParentheses(parent)) {

				parent = parent.parent;
			}

			if (parent && parent.type === 'BinaryExpression')
				return parent;

			return null;
		}

		return parent;
	};

	/**
	 * Checks whether the node is surrounded by parentheses.
	 *
	 * @param {Node} node - Node to check for parentheses.
	 *
	 * @returns {bool} True, if the node is surrounded with parentheses.
	 */
	const hasParentheses = function (node: TSESTree.Node) {
		const prevToken = context.sourceCode.getTokenBefore(node as never)!;

		return (prevToken.type === 'Punctuator' && prevToken.value === '(');
	};

	/**
	 * Checks whether the given node is commented to be safe from HTML.
	 *
	 * @param {Node} node - Node to check.
	 *
	 * @returns {bool} True, if this specific node has a /safe/ comment.
	 */
	const nodeHasSafeComment = function (node: TSESTree.Node) {

		// Check all the comments in front of the node for comment 'safe'
		let isSafe = false;
		const sourceCode = context.sourceCode;
		const comments = sourceCode.getCommentsBefore(node as never);
		if (node.type !== 'Identifier') {
			const insideComments = sourceCode.getCommentsInside(node.parent as never);
			if (insideComments.length > 0) {
				const left = (node.parent as TSESTree.AssignmentExpression).left;
				const right = (node.parent as TSESTree.AssignmentExpression).right;
				if (left && right) {
					insideComments.forEach((comment, _, __) => {
						if ((comment as TSESTree.Comment).range[0] >= left.range[1] && (comment as TSESTree.Comment).range[1] <= right.range[0]) {
							comments.push(comment);
						}
					});
				}
			}
		}
		comments.forEach(function (comment) {
			if (/^\s*safe\s*$/i.exec(comment.value))
				isSafe = true;
		});

		return isSafe;
	};

	/**
	 * Gets the closest parent node that matches the given type. May return the
	 * node itself.
	 *
	 * @param {Node} node - The node to start the search from.
	 * @param {string} parentType - The node type to search.
	 *
	 * @returns {Node} The closest node of the correct type.
	 */
	const getPathFromParent = function (node: TSESTree.Node, parentType: string): TSESTree.Node[] | null {

		const path = [node];
		while (node && node.type !== parentType) {
			node = node.parent!;
			path.push(node);
		}

		if (!node)
			return null;

		path.reverse();
		return path;
	};

	const getXssCandidateParent = function (node: TSESTree.Node) {

		// Find the infectable node.
		//
		// This takes care of call expressions that might use
		// passthrough functions. Here we need to check whether the
		// current node is in a passthrough position.
		for (let ptr = exprStack.length - 1; ptr >= 0; ptr--) {

			// Only CallExpressions may pass through the parameters.
			const candidate = exprStack[ptr]!;
			if (candidate.node.type !== 'CallExpression')
				return candidate;

			// Quick check for whether this is an passthrough at all.
			const functionRules = allRules.get(candidate.node);
			if (!functionRules.passthrough)
				return candidate;

			// The function is at least a partial passthrough.
			// Quickly check whether it passes everything through.
			if (functionRules.passthrough.obj && functionRules.passthrough.args)
				continue;

			// Only obj OR args is passed through. Figure out which one the
			// current node is.
			const path = getPathFromParent(node, 'CallExpression')!;
			const callExpr = path[0] as {callee?: TSESTree.Node};
			const callImmediateChild = path[1];

			const isCallee = callImmediateChild === callExpr.callee;
			const isParam = !isCallee;

			// Continue to next stack part if the function passes the obj through
			// and the current node is the obj.
			if (isCallee && functionRules.passthrough.obj)
				continue;

			// Continue to next stack part if the function passes the args through
			// and the current node is an argument.
			if (isParam && functionRules.passthrough.args)
				continue;

			return candidate;
		}

		return null;
	};

	const infectParentConditional = function (condition: CallableFunction, node: TSESTree.Node) {

		if (exprStack.length > 0 &&
			!isCommentedSafe(node) &&
			canInfectXss(node) &&
			condition(node)) {
			// ignore pure literal HTML
			if (node.type === 'Literal' && node.parent!.type === 'CallExpression' &&
				(node.parent! as TSESTree.CallExpression).arguments.length === 1) {
				let hasUnsafeHTML = isHTMLLiteral(node);
				if (hasUnsafeHTML) {
					hasUnsafeHTML = false;
					let parent = node.parent as TSESTree.Node;
					while (parent != null) {
						if (parent.type === 'AssignmentExpression' || parent.type === 'VariableDeclarator') {
							hasUnsafeHTML = true;
							break;
						}
						parent = parent.parent!;
					}
				}
				if (!hasUnsafeHTML) {
					return;// ignore pure literal HTML with any assignment
				}
			}

			// ignore TSTypeReference and boolean type
			if (node.type === 'Identifier' && (
				node.parent!.type === 'TSTypeReference' || node.parent!.type === 'UnaryExpression'
			)) return;

			const infectable = getXssCandidateParent(node);
			if (infectable) {
				infectable.xss = true;
				if (node.type === 'CallExpression') {
					infectable.sanitized = allRules.get(node).sanitized;
				}
			}
		}
	};

	// -------------------------------------------------------------------------
	// Public
	// -------------------------------------------------------------------------

	return {

		'AssignmentExpression': pushNode,
		'AssignmentExpression:exit': exitNode,
		'VariableDeclarator': pushNode,
		'VariableDeclarator:exit': exitNode,
		'Property': pushNode,
		'Property:exit': exitNode,
		'ReturnStatement': pushNode,
		'ReturnStatement:exit': exitNode,
		'ArrowFunctionExpression': pushNode,
		'ArrowFunctionExpression:exit': exitNode,
		'ArrayExpression': pushNode,
		'ArrayExpression:exit': markParentXSS,

		// Call expressions have a dual nature. They can either infect their
		// parents with XSS vulnerabilities or then they can suffer from them.
		'CallExpression': function (node: TSESTree.CallExpression) {

			// First check whether this expression marks the parent as dirty.
			infectParentConditional(function (node: TSESTree.CallExpression) {
				return isHtmlOutputFunction(node.callee);
			}, node);
			pushNode(node);
		},
		'CallExpression:exit': exitNode,

		// Literals infect parents if they contain <html> tags or fragments.
		'Literal': infectParentConditional.bind(null, function (node: TSESTree.Literal) {

			// Skip regex and /*safe*/ strings. Remaining strings infect parent
			// if they contain <html or </html tags.
			return !(node as unknown as {regex?: RegExp}).regex && isHTMLLiteral(node);
		}),

		// Identifiers infect parents if they refer to HTML in their name.
		'Identifier': infectParentConditional.bind(null, function (node: TSESTree.Identifier) {
			return isHtmlVariable(node);
		}),
	};
};

export const schema = [
	{
		type: 'object',
		properties: {
			htmlVariableRules: { type: 'array' },
			htmlFunctionRules: { type: 'array' },
			sanitizedVariableRules: { type: 'array' },
			functions: { type: 'object' },
		},
		additionalProperties: false
	}
];