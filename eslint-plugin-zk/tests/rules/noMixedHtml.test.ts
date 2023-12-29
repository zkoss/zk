// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-nocheck
/* noMixedHtml.test.ts

	Purpose:
		
	Description:
		
	History:
		4:36 PM 2023/12/27, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
import {noMixedHtml} from '@rules';
import {typedRuleTester as ruleTester} from '../util';

ruleTester.run('no-mixed-html', noMixedHtml, {

	valid: [
		'var x = text',
		'x = text',
		'y = x = text',
		'obj.x = text',
		'foo( text )',
		'x.foo( text )',
		'html.foo( text )',
		'f()( text )',
		'match( /<html>/ )',

		// TODO: Find a way to add test for lone 'return'.
		'function v() { return console.log( optionator.generateHelp() ); }',
		{
			code: 'test.html( html )',
			options: [{
				functions: {
					'.html': {
						htmlInput: true,
						htmlOutput: true
					}
				}
			}]
		},
		{
			code: '$( html )',
			options: [{functions: {'$': {htmlInput: true}}}]
		},
		{
			code: 'test.html( \'<img src="zwK7XG6.gif">\' )',
			options: [{functions: {'.html': {htmlInput: true}}}],
		},
		{
			code: 'window.document.write( \'<img src="zwK7XG6.gif">\' )',
			options: [{functions: {'window.document.write': {htmlInput: true}}}],
		},
		{
			code: 'window.document.write( \'<img src="zwK7XG6.gif">\' )',
			options: [{functions: {'.document.write': {htmlInput: true}}}],
		},
		{
			code: 'window.document.write( \'<img src="zwK7XG6.gif">\' )',
			options: [{functions: {'.write': {htmlInput: true}}}],
		},
		'var x = "a" + "b";',
		{
			code: 'var html = "<div>" + encode( foo() ) + "</div>"',
			options: [{functions: {'encode': {htmlOutput: true}}}]
		},
		{
			code: 'var html = "<div>" + he.encode( foo() ) + "</div>"',
			options: [{functions: {'he.encode': {htmlOutput: true}}}]
		},
		'x = text;',
		'x = "a" + "b";',
		{
			code: 'html = "<div>" + encode( foo() ) + "</div>"',
			options: [{functions: {'encode': {htmlOutput: true}}}]
		},
		'asHtml = varHtml',
		'asHtml = htmlToo = varHtml',
		'htmlEncode = function() {}',
		'decode = function( html ) {}',

		'htmlMapping = {}',
		'mapping = { html: "<div>" }',
		'values = [ "value" ]',
		'values = [ text ]',
		'text = html ? "a" : "b"',
		'text = html ? foo : bar',
		'html = html ? "<div>" : "b"',
		{
			code: 'encoded = "<div>"',
			options: [{htmlVariableRules: ['encoded']}]
		},
		{
			code: 'asEncoded = "<div>"',
			options: [{htmlVariableRules: ['encoded/i']}]
		},

		'htmlItems = [ html ].join()',
		{
			code: 'htmlItems = assert( html )',
			options: [{functions: {assert: {passthrough: {args: true}}}}]
		},
		{
			code: 'text = assert( input )',
			options: [{functions: {assert: {passthrough: {args: true}}}}]
		},
		{
			code: 'text = en_us.format( input )',
			options: [{functions: {'.format': {passthrough: {args: true}}}}]
		},
		{
			code: 'html = en_us.format( htmlInput )',
			options: [{functions: {'.format': {passthrough: {args: true}}}}]
		},
		{
			code: 'text = str.format( en_us )',
			options: [{functions: {'.format': {passthrough: {obj: true}}}}]
		},
		{
			code: 'html = htmlStr.format( en_us )',
			options: [{functions: {'.format': {passthrough: {obj: true}}}}]
		},
		'html = /*safe*/ en_us.format( htmlInputttttt )',

		'x = /*safe*/ "This is not <html>"',
		'html = "<div>" + /*safe*/ input + "</div>"',
		'html = "<div>" + /*safe*/ obj.value + "</div>"',
		'text = /*safe*/ stuffAsHtml()',
		'html = /*safe*/ getElement()',
		'html = /*safe*/ getElement()',
		'x = /*safe*/ "This is not <html>" + text',
		'text = /*safe*/ stuffAsHtml() + text',
		'text = /*safe*/ ( "<div>" + "</div>" )',
		'text = /*safe*/ html[ 0 ]',
		'html = /*safe*/ window.document[ 0 ]',
		{
			code: 'if( !$( /*safe*/ document.element ).is() ) {}',
			options: [{
				functions: {
					'$': {
						htmlInput: true,
						safe: ['document', 'this', 'window']
					}
				}
			}]
		},
		{
			code: 'var prompt = $( "<div>" + "<span>" + /* safe */ text + "</span>" + /* safe */ value + "</div>" )',
			options: [{functions: {'$': {htmlInput: true}}}]
		},
		'obj = { fooHtml: stuffAsHtml() }',
		'obj = { foo: stuff() }',
		{
			code: 'html = foo.asHtml()',
			options: [{htmlFunctionRules: ['\.asHtml$']}]
		},
		{
			code: 'html = fullHtml.left( offset )',
			options: [{functions: {'.left': {passthrough: {obj: true}}}}]
		},
		{
			code: '$( document )',
			options: [{
				functions: {
					'$': {
						htmlInput: true,
						safe: ['document']
					}
				}
			}],
		},
		{
			code: '$( foobar )',
			options: [{functions: {'$': {htmlInput: true, safe: true}}}],
		},
		{
			code: '$( ".foo" )',
			options: [{
				functions: {
					'$': {
						htmlInput: true,
						safe: ['document']
					}
				}
			}],
		},
		{
			code: '$( this ).toggle()',
			options: [{
				functions: {
					'$': {htmlInput: true, safe: ['document', 'this']}
				}
			}],
		},
		{
			code: '$( "#item-" + CSS.escape( id ) )',
			options: [{
				functions: {
					'$': {htmlInput: true, safe: ['document']},
					'CSS.escape': {htmlOutput: true}
				}
			}],
		},
		'htmlArrs = [ /*safe*/ [ "<div>" ], /*safe*/ [ "<div>" ] ]',
		'htmlArrs = /*safe*/ [ [ "<div>" ], [ "<div>" ] ]',

		'html = function() { return "<div>" }',
		'text = function() { return input }',
		'text = function( html ) { return input }',
		'(function() { return "<div>" })',
		'(function() { return input })',
		{
			parserOptions: {ecmaVersion: 6},
			code: '(() => "<div>")',
		},
		{
			parserOptions: {ecmaVersion: 6},
			code: '(() => html)',
		},
		{
			parserOptions: {ecmaVersion: 6},
			code: 'text = (html) => input',
		},
		{
			parserOptions: {ecmaVersion: 6},
			code: '(() => input)',
		},
		{
			parserOptions: {ecmaVersion: 6},
			code: 'html = () => { return "<div>" }',
		},
		{
			parserOptions: {ecmaVersion: 6},
			code: 'text = () => { return input }',
		},
		{
			parserOptions: {ecmaVersion: 6},
			code: 'text = ( html ) => { return input }',
		},
		{
			parserOptions: {ecmaVersion: 6},
			code: '(() => { return "<div>" })',
		},
		{
			parserOptions: {ecmaVersion: 6},
			code: '(() => { return input })',
		},

		'html',
		'text',
		'"use strict"',
		'"<div>"',
		// This should be safe as the variable is not used in HTML context.
		{
			code: 'x( "<div>" )'
		},
		// This should be safe as the variable is not used in HTML context.
		{
			code: 'foo.x( "<div>" )',
		},
		{
			code: 'var s = test as HTMLDivElement',
		},
		{
			code: 'const widgett = zk.$extends(zhtml.wgt.HTML, {})',
		},
	],

	invalid: [

		{
			code: 'var html = "<div>" + text + "</div>"',
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},
		{
			code: 'html = "<div>" + text + "</div>"',
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},
		{
			code: 'x.innerHTML = "<div>" + text + "</div>"',
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},
		{
			code: 'write( html )',
			errors: [{
				message: 'HTML passed in to function \'write\'',
			}]
		},
		{
			code: 'f()( html )',
			errors: [{
				message: 'HTML passed in to function \'f()\'',
			}]
		},
		{
			code: 'x.html( "<div>" + text + "</div>" )',
			options: [{functions: {'.html': {htmlInput: true}}}],
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},
		{
			code: 'window.document.write( value )',
			options: [{functions: {'window.document.write': {htmlInput: true}}}],
			errors: [{
				message: 'Unencoded input \'value\' used in HTML context',
			}]
		},
		{
			code: 'window.document.write( value )',
			options: [{functions: {'.document.write': {htmlInput: true}}}],
			errors: [{
				message: 'Unencoded input \'value\' used in HTML context',
			}]
		},
		{
			code: 'window.document.write( value )',
			options: [{functions: {'.write': {htmlInput: true}}}],
			errors: [{
				message: 'Unencoded input \'value\' used in HTML context',
			}]
		},
		{
			code: 'write( html )',
			options: [{functions: {'.write': {htmlInput: true}}}],
			errors: [{
				message: 'HTML passed in to function \'write\'',
			}]
		},
		{
			code: 'document.write( html )',
			options: [{functions: {'.document.write': {htmlInput: true}}}],
			errors: [{
				message: 'HTML passed in to function \'document.write\'',
			}]
		},
		{
			code: 'var asHtml = text',
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},
		{
			code: 'asHtml = text',
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},
		{
			code: 'asHtml = text[ 0 ]',
			errors: [{
				message: 'Unencoded input \'text[ 0 ]\' used in HTML context',
			}]
		},
		{
			code: 'x.html( text )',
			options: [{functions: {'.html': {htmlInput: true}}}],
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},
		{
			code: 'x.innerHTML = text',
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},

		{
			code: 'var x = asHtml',
			errors: [{
				message: 'Non-HTML variable \'x\' is used to store raw HTML',
			}]
		},
		{
			code: 'x = asHtml',
			errors: [{
				message: 'Non-HTML variable \'x\' is used to store raw HTML',
			}]
		},
		{
			code: 'x( asHtml )',
			errors: [{
				message: 'HTML passed in to function \'x\'',
			}]
		},
		{
			code: 'foo.x( asHtml )',
			errors: [{
				message: 'HTML passed in to function \'foo.x\'',
			}]
		},

		{
			code: 'var x = obj.html',
			errors: [{
				message: 'Non-HTML variable \'x\' is used to store raw HTML',
			}]
		},
		{
			code: 'x = html[ 0 ]',
			errors: [{
				message: 'Non-HTML variable \'x\' is used to store raw HTML',
			}]
		},
		{
			code: 'x = obj.html',
			errors: [{
				message: 'Non-HTML variable \'x\' is used to store raw HTML',
			}]
		},
		{
			code: 'x( obj.html )',
			errors: [{
				message: 'HTML passed in to function \'x\'',
			}]
		},
		{
			code: 'foo.x( obj.html )',
			errors: [{
				message: 'HTML passed in to function \'foo.x\'',
			}]
		},

		{
			code: 'var x = "<div>"',
			errors: [{
				message: 'Non-HTML variable \'x\' is used to store raw HTML',
			}]
		},
		{
			code: 'x = "<div>"',
			errors: [{
				message: 'Non-HTML variable \'x\' is used to store raw HTML',
			}]
		},
		// This should be safe as the variable is not used in HTML context.{
		// 	code: 'x( "<div>" )',
		// 	errors: [{
		// 		message: 'HTML passed in to function \'x\'',
		// 	}]
		// },
		// This should be safe as the variable is not used in HTML context.{
		// 	code: 'foo.x( "<div>" )',
		// 	errors: [{
		// 		message: 'HTML passed in to function \'foo.x\'',
		// 	}]
		// },

		{
			code: 'foo.stuff( doc.html( text ) )',
			options: [{functions: {'.html': {htmlInput: true}}}],
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},

		{
			code: 'obj = { html: text }',
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},
		{
			code: 'obj = [ { html: text } ]',
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},
		{
			code: '_ = { text: "<html>" }',
			errors: [{
				message: 'Non-HTML variable \'text\' is used to store raw HTML',
			}]
		},
		{
			code: 'arr = [ html ]',
			errors: [{
				message: 'Non-HTML variable \'arr\' is used to store raw HTML',
			}]
		},
		{
			code: 'htmlItems = [ text ]',
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},
		{
			code: 'htmlItems = [ text ].join()',
			errors: [{
				message: 'Unencoded input \'text\' used in HTML context',
			}]
		},
		{
			code: 'textItems = [ html ].join()',
			errors: [{
				message: 'Non-HTML variable \'textItems\' is used to store raw HTML',
			}]
		},

		{
			code: 'text = encode( text )',
			options: [{functions: {'encode': {htmlOutput: true}}}],
			errors: [{
				message: 'Non-HTML variable \'text\' is used to store raw HTML'
			}]
		},
		{
			code: 'html = html ? foo : "text"',
			errors: [{
				message: 'Unencoded input \'foo\' used in HTML context'
			}]
		},
		{
			code: 'html = html ? "text" : foo',
			errors: [{
				message: 'Unencoded input \'foo\' used in HTML context'
			}]
		},
		{
			code: 'text = html ? "<div>" : foo',
			errors: [
				{message: 'Non-HTML variable \'text\' is used to store raw HTML'},
				{message: 'Unencoded input \'foo\' used in HTML context'},
			]
		},
		{
			code: 'text = encode( "foo" )',
			options: [{functions: {'encode': {htmlOutput: true}}}],
			errors: [
				{message: 'Non-HTML variable \'text\' is used to store raw HTML'}
			]
		},
		{
			code: 'var foo = function() { return "<div>"; }',
			options: [{htmlFunctionRules: ['AsHtml$']}],
			errors: [
				{message: 'Non-HTML function \'foo\' returns HTML content'}
			]
		},
		{
			code: 'var foo = function bar() { return "<div>"; }',
			options: [{htmlFunctionRules: ['AsHtml$']}],
			errors: [
				{message: 'Non-HTML function \'bar\' returns HTML content'}
			]
		},
		{
			code: 'function bar() { return "<div>"; }',
			options: [{htmlFunctionRules: ['AsHtml$']}],
			errors: [
				{message: 'Non-HTML function \'bar\' returns HTML content'}
			]
		},
		{
			code: 'function bar() { return barAsHtml(); }',
			options: [{htmlFunctionRules: ['AsHtml$']}],
			errors: [
				{message: 'Non-HTML function \'bar\' returns HTML content'}
			]
		},
		{
			parserOptions: {ecmaVersion: 6},
			code: 'var bar = y => "<div>"',
			options: [{htmlFunctionRules: ['AsHtml$']}],
			errors: [
				{message: 'Non-HTML function \'bar\' returns HTML content'}
			]
		},

		{
			code: 'var fooAsHtml = function() { return value; }',
			options: [{htmlFunctionRules: ['AsHtml$']}],
			errors: [
				{message: 'Unencoded input \'value\' used in HTML context'}
			]
		},
		{
			code: 'var foo = function barAsHtml() { return input; }',
			options: [{htmlFunctionRules: ['AsHtml$']}],
			errors: [
				{message: 'Unencoded input \'input\' used in HTML context'}
			]
		},
		{
			code: 'function barAsHtml() { return bar; }',
			options: [{htmlFunctionRules: ['AsHtml$']}],
			errors: [
				{message: 'Unencoded input \'bar\' used in HTML context'}
			]
		},
		{
			parserOptions: {ecmaVersion: 6},
			code: 'var barAsHtml = y => y',
			options: [{htmlFunctionRules: ['AsHtml$']}],
			errors: [
				{message: 'Unencoded input \'y\' used in HTML context'}
			]
		},

		{
			code: 'var foo = fooAsHtml()',
			options: [{htmlFunctionRules: ['AsHtml$']}],
			errors: [
				{message: 'Non-HTML variable \'foo\' is used to store raw HTML'}
			]
		},
		{
			code: 'var html = foo()',
			options: [{htmlFunctionRules: ['AsHtml$']}],
			errors: [
				{message: 'Unencoded return value from function \'foo\' used in HTML context'}
			]
		},

		{
			code: 'x = /*foobar*/ "This is not <html>"',
			errors: [
				{message: 'Non-HTML variable \'x\' is used to store raw HTML'}
			]
		},
		{
			code: 'x = /*safe*/ "This is not <html>" + html',
			errors: [
				{message: 'Non-HTML variable \'x\' is used to store raw HTML'}
			]
		},
		{
			code: 'obj = { fooHtml: stuff() }',
			errors: [
				{message: 'Unencoded return value from function \'stuff\' used in HTML context'}
			]
		},
		{
			code: 'obj = { fooHtml: obj.stuff() }',
			errors: [
				{message: 'Unencoded return value from function \'obj.stuff\' used in HTML context'}
			]
		},
		{
			code: 'obj = { foo: stuffAsHtml() }',
			errors: [
				{message: 'Non-HTML variable \'foo\' is used to store raw HTML'}
			]
		},
		{
			code: 'arr = [ {}, "<div>" ]',
			errors: [
				{message: 'Non-HTML variable \'arr\' is used to store raw HTML'},
				{message: 'Unencoded input \'[Object]\' used in HTML context'},
			]
		},
		{
			code: 'arr = [ [], "<div>" ]',
			errors: [
				{message: 'Non-HTML variable \'arr\' is used to store raw HTML'},
				{message: 'Unencoded input \'[Array]\' used in HTML context'},
			]
		},
		{
			code: 'htmlArr = [ {}, "<div>" ]',
			errors: [
				{message: 'Unencoded input \'[Object]\' used in HTML context'},
			]
		},
		{
			code: 'htmlArr = [ [], "<div>" ]',
			errors: [
				{message: 'Unencoded input \'[Array]\' used in HTML context'},
			]
		},
		{
			code: 'html = [ html ].join()',
			options: [{functions: {'.join': {}}}],
			errors: [
				{message: 'HTML passed in to function \'[ html ].join\''},
				{message: 'Unencoded return value from function \'[ html ].join\' used in HTML context'},
			]
		},
		{
			code: 'text = assert( "<div>" )',
			options: [{functions: {'assert': {passthrough: {args: true}}}}],
			errors: [
				{message: 'Non-HTML variable \'text\' is used to store raw HTML'},
			]
		},
		{
			code: 'text = assert( "<div>" )',
			errors: [
				{message: 'HTML passed in to function \'assert\''},
			]
		},
		{
			code: 'text = assert( getHtml() )',
			options: [{
				functions: {
					assert: {passthrough: {args: true}},
					getHtml: {htmlOutput: true},
				}
			}],
			errors: [
				{message: 'Non-HTML variable \'text\' is used to store raw HTML'},
			]
		},
		{
			code: 'html = assert( input )',
			options: [{
				functions: {
					assert: {passthrough: {args: true}},
					getHtml: {htmlOutput: true},
				}
			}],
			errors: [{message: 'Unencoded input \'input\' used in HTML context'},]
		},
		{
			code: 'html = en_us.format( input )',
			options: [{functions: {'.format': {passthrough: {obj: true}}}}],
			errors: [{message: 'Unencoded input \'en_us\' used in HTML context'},]
		},
		{
			code: 'html = en_us.format( htmlInput )',
			options: [{functions: {'.format': {passthrough: {obj: true}}}}],
			errors: [
				{message: 'HTML passed in to function \'en_us.format\''},
				{message: 'Unencoded input \'en_us\' used in HTML context'},
			]
		},
		{
			code: 'html = foo.format( en_us )',
			options: [{functions: {'.format': {passthrough: {args: true}}}}],
			errors: [{message: 'Unencoded input \'en_us\' used in HTML context'},]
		},
		{
			code: 'html = htmlStr.format( en_us )',
			options: [{functions: {'.format': {passthrough: {args: true}}}}],
			errors: [{message: 'Unencoded input \'en_us\' used in HTML context'},]
		},

		{
			code: '$( document )',
			options: [{functions: {'$': {htmlInput: true}}}],
			errors: [
				{message: 'Unencoded input \'document\' used in HTML context'},
			]
		},
		{
			code: '$( "#item-" + id )',
			options: [{
				functions: {
					'$': {
						htmlInput: true,
						safe: ['document']
					}
				}
			}],
			errors: [
				{message: 'Unencoded input \'id\' used in HTML context'},
			]
		},
		{
			code: 'htmlArrs = foo ? [ [ "<div>" ] ] : [ "div" ]',
			errors: [
				{message: 'Unencoded input \'[Array]\' used in HTML context'},
			]
		}
	]
});