/* noLocationHrefAssign.ts

	Purpose:
		
	Description:
		
	History:
		11:42 AM 2023/12/25, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
/**
 * @fileoverview prevents xss by assignment to location href javascript url string
 * @author Alexander Mostovenko
 */
'use strict';

import { createRule } from '../util';
import { TSESTree} from '@typescript-eslint/utils';

// ------------------------------------------------------------------------------
// Plugin Definition
// ------------------------------------------------------------------------------

const ERROR = 'Dangerous location.href assignment can lead to XSS';

export const noLocationHrefAssign = createRule({
	name: 'noLocationHrefAssign',
	meta: {
		type: 'problem',
		docs: {
			description: 'disallow location.href assignment (prevent possible XSS)',
			recommended: 'error',
		},
		fixable: 'code',
		messages: {
		},
		schema: [
			{
				type: 'object',
				properties: {
					escapeFunc: { type: 'string' }
				},
				additionalProperties: false
			}]
	},
	defaultOptions: [],
	create(context) {
		// eslint-disable-next-line @typescript-eslint/ban-ts-comment
		// @ts-ignore
		// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
		const escapeFunc: string = context.options[0] && context.options[0]['escapeFunc'] || 'escape';

		return {
			AssignmentExpression: function (node) {
				const left = node.left as TSESTree.Expression & {property?: {name: string}, object: {name: string, property?: TSESTree.Identifier}};
				// eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
				const isHref: boolean = left.property?.name === 'href';
				if (!isHref) {
					return;
				}
				const isLocationObject: boolean = left.object?.name === 'location';
				const isLocationProperty: boolean | undefined = left.object.property &&
					(left.object.property as TSESTree.Identifier).name === 'location';

				if (!(isLocationObject || isLocationProperty)) {
					return;
				}

				const sourceCode = context.getSourceCode();
				if (node.right.type === 'CallExpression' && (node.right.callee.type === 'Identifier') &&
					(node.right.callee.name === escapeFunc || sourceCode.getText(node.right.callee) === escapeFunc)) {
					return;
				}
				if (node.right.type === 'CallExpression' && (node.right.callee.type === 'MemberExpression') &&
					sourceCode.getText(node.right.callee) === escapeFunc) {
					return;
				}
				// ignore for new URL();
				if (node.right.type === 'MemberExpression' && (node.right.object.type === 'NewExpression') && node.right.object.callee.type === 'Identifier' && (node.right.object.callee.name === 'URL')) {
					return;
				}

				const rightSource: string = sourceCode.getText(node.right);
				const errorMsg: string = ERROR +
					'. Please use ' + escapeFunc +
					'(' + rightSource + ') as a wrapper for escaping';

				// eslint-disable-next-line @typescript-eslint/ban-ts-comment
				// @ts-ignore
				context.report({ node, message: errorMsg });
			}
		};
	}
});