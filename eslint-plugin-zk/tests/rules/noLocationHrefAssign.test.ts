// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-nocheck
/* noLocationHrefAssign.test.ts

	Purpose:
		
	Description:
		
	History:
		4:15 PM 2023/12/27, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
import {noLocationHrefAssign} from '@rules';
import {typedRuleTester as ruleTester} from '../util';

ruleTester.run('no-location-href-assign', noLocationHrefAssign, {

	valid: [
		'someLink.href = \'www\'',
		'href = \'wwww\'',
		{
			code: 'location.href = escape(\'www\')',
			options: [{escapeFunc: 'escape'}]
		},
		{
			code: 'location.href = DOMPurify.sanitize(\'www\')',
			options: [{escapeFunc: 'DOMPurify.sanitize'}]
		}
	],

	invalid: [
		{
			code: 'location.href = wrapper(escape(\'www\'))',
			options: [{escapeFunc: 'escapeXSS'}],
			errors: [{
				message: 'Dangerous location.href assignment can lead to XSS.' +
					' Please use escapeXSS(wrapper(escape(\'www\'))) ' +
					'as a wrapper for escaping'
			}]
		},
		{
			code: 'location.href = wrapper(\'www\')',
			options: [{escapeFunc: 'escape'}],
			errors: [{
				message: 'Dangerous location.href assignment can lead to XSS.' +
					' Please use escape(wrapper(\'www\')) as a wrapper for escaping'
			}]
		},
		{
			code: 'location.href = \'some location\'',
			errors: [{
				message: 'Dangerous location.href assignment can lead to XSS.' +
					' Please use escape(\'some location\') as a wrapper for escaping'
			}]
		},
		{
			code: 'location.href = \'some location for memberExpression callee\'',
			options: [{escapeFunc: 'DOMPurify.sanitize'}],
			errors: [{
				message: 'Dangerous location.href assignment can lead to XSS.' +
					' Please use DOMPurify.sanitize(\'some location for memberExpression callee\') as a wrapper for escaping'
			}]
		},
		{
			code: 'window.location.href = \'some location\'',
			errors: [{
				message: 'Dangerous location.href assignment can lead to XSS.' +
					' Please use escape(\'some location\') as a wrapper for escaping'
			}]
		},
		{
			code: 'document.location.href = \'some location\'',
			errors: [{
				message: 'Dangerous location.href assignment can lead to XSS.' +
					' Please use escape(\'some location\') as a wrapper for escaping'
			}]
		},
		{
			code: 'window.document.location.href = \'some location\'',
			errors: [{
				message: 'Dangerous location.href assignment can lead to XSS.' +
					' Please use escape(\'some location\') as a wrapper for escaping'
			}]
		},
		{
			code: 'window.document.location.href = getNextUrl()',
			errors: [{
				message: 'Dangerous location.href assignment can lead to XSS.' +
					' Please use escape(getNextUrl()) as a wrapper for escaping'
			}]
		}
	]
});