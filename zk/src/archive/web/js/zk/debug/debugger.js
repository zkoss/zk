/* debugger.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 10:19:46     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.debug.Debugger = zk.$extends(zk.Object, {
	outId: 'zk_debugger',
	
	getConsole: function () {
		var console = zDom.$(this.outId);
		if (!console) {
			console = document.createElement("DIV");
			var html = '<div id="' + this.outId +'" class="z-debug-domtree"></div>';
			document.body.appendChild(console);
			zDom.setOuterHTML(console, html);
			console = zDom.$(this.outId);
		}
		return console;
	},
	outDomTree: function (wgt, handler) {
		var text;
		
		if (wgt && typeof wgt.$instanceof == 'function' && wgt.$instanceof(zk.Widget)) {
			var out = [];
			wgt.redraw(out);
			text = out.join('');
		} else if (wgt) {
			text = wgt.toString();
		}
		if (!text) return;
		
		if (!handler) 
			handler = new zk.debug.DefaultHandler();
		this.parseHTML(text, handler);
		var console = this.getConsole();
		var isEmpty = console.innerHTML == "";
		console.innerHTML += '<div class="z-debug-domtree-header">'
				+ '<div class="z-debug-domtree-close" onclick="zDom.remove(\''
				+ this.outId + '\')" onmouseover="zDom.addClass(this, \'z-debug-domtree-close-over\');"'
				+ ' onmouseout="zDom.rmClass(this, \'z-debug-domtree-close-over\');"></div> [' + wgt.className + '] '
				+ wgt.uuid +'</div><div class="z-debug-domtree-body">' + handler.toHTML() + '</div>';
				
		if (!isEmpty) {
			if (zk.ie && console.offsetHeight){} 
			console.scrollTop = console.scrollHeight;
		}
	},
	parseHTML: function(text, handler) {
		var begin, content, deep = 0, empty;
			
		while (text) {
			begin = text.indexOf('<');
			text = text.trim();
			if (begin == 0 && text.startsWith('<!--')) {
				begin = text.indexOf("-->");
				if (begin != -1) {
					handler.comment(deep, text.substring(0, begin + 3));
					text = text.substring(begin + 3);
				}
				if (text.startsWith('</')) 
					deep--;
			} else if (begin >= 0 && text.indexOf('</') == begin) {
				var end = text.indexOf('>');
				if (begin != 0) {
					content = text.substring(0, begin);
					handler.content(deep, content);
					deep--;
				}
				content = text.substring(begin, end + 1);
				text = text.substring(end + 1);
				text = text.trim();
				handler.endTag(deep, content, empty);
				empty = false;
				if (text.startsWith('</')) 
					deep--;
			} else if (begin > 0) {
					content = text.substring(0, begin);
					handler.content(deep, content);
					text = text.substring(begin);
			} else if (begin == 0) {
				var mid = text.indexOf('>'), end = text.indexOf('/>');
				if (end >= 0 && end < mid) {
					content = text.substring(0, end + 2);
					handler.startTag(deep, content, true);
					text = text.substring(end + 2);
				} else {
					content = text.substring(0, mid + 1);
					text = text.substring(mid + 1).trim();
					empty = text.startsWith('</');
					handler.startTag(deep, content, false, empty);
					if (!empty) 
						deep++;
				}
			} else {
				handler.error(text);
				break;
			}
		}
	}
});
zk.debug.DefaultHandler = zk.$extends(zk.Object, {
	$init: function () {
		this.out = [];
	},
	endTag: function (deep, content, isEmpty) {
		this.out.push(isEmpty ? '' : this._getSpace(deep), zUtl.encodeXML(content), '<br/>');
	},
	comment: function (deep, content) {
		this.out.push(this._getSpace(deep), zUtl.encodeXML(content), '<br/>');
	},
	startTag: function (deep, content, isSingle, isEmpty) {
		this.out.push(this._getSpace(deep), this._parseAttribute(content), isEmpty ? '' : '<br/>');
	},
	_parseAttribute: function (content) {
		var out = [];
		for (var odd, c, i = 0, j = content.length; i < j; i++) {
			c = content.charAt(i);
			switch (c) {
				case '=':
					out.push('=<span style="color:#0666FD">');
					cnt = 0;
					odd = false;
					break;
				case '<':
					out.push('&lt;');
					break;
				case '>':
					out.push('&gt;');
					break;
				case '"':
					if (odd) {
						odd = false;
						out.push('"</span>');
						break;
					} else odd = true;
					// don't break;
				default:
					out.push(c);
			}
		}
		return out.join('');
	},
	content: function (deep, content) {
		this.out.push(this._getSpace(deep), zUtl.encodeXML(content), '<br/>');
	},
	error: function (content) {
		this.out.push('<b> Error {', content, '}</b>');
	},
	toHTML: function () {
		return this.out.join('');
	},
	_getSpace: function (deep) {
		var out = [];
		for (; deep-- > 0;)
			out.push('&nbsp;&nbsp;&nbsp;&nbsp;');
		return out.join('');
	}
});
