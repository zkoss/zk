/* debugger.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 10:19:46     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.debug.Debugger = zk.$extends(zk.Object, {
	outId: 'zk_debugger',
	
	getConsole: function () {
		var console = jq(this.outId, zk)[0];
		if (!console) {
			console = document.createElement("div");
			document.body.appendChild(console);
			jq(console).replaceWith('<div id="' + this.outId +'" class="z-debug-domtree"></div>');
			console = jq(this.outId, zk)[0];
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
		console.innerHTML += '<div class="z-debug-domtree-header">'
				+ '<div class="z-debug-domtree-close" onclick="jq(\'#'
				+ this.outId + '\').remove()" onmouseover="jq(this).addClass(\'z-debug-domtree-close-over\');"'
				+ ' onmouseout="jq(this).removeClass(\'z-debug-domtree-close-over\');"></div> [' + wgt.className + '] '
				+ wgt.uuid + '&nbsp;&nbsp;&nbsp;&nbsp;<span style="color:red;">ErrorNumber: '
				+ handler.getErrorNumber() + '</span></div><div class="z-debug-domtree-body">' + handler.toHTML() + '</div>';
				
		if (zk.ie && console.offsetHeight){} 
		console.scrollTop = console.scrollHeight;
	},
	parseHTML: function(text, handler) {
		var begin, content, deep = 0, empty;
			
		while (text) {
			text = text.trim();
			begin = text.indexOf('<');
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
					text = text.substring(end + 2).trim();
					if (text.startsWith('</')) 
						deep--;
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

zDebug = new zk.debug.Debugger(); // global

zk.debug.DefaultHandler = zk.$extends(zk.Object, {
	_errorNumber: 0,
	$init: function () {
		this.out = [];
		this.stack = [];
	},
	endTag: function (deep, content, isEmpty) {
		var startTag = this.stack.pop(),
			endTag = content.substring(2, content.length-1);
		if (startTag != endTag) {
			this._errorNumber++;
			this.out.push('<span style="color:red">Unmatched start tag : [<span style="color:blue;">&lt;',
					startTag, '&gt;</span>], end tag : [<span style="color:blue;">&lt;/',
					endTag, '&gt;</span>]</span><br/>');
			return;
		}
		this.out.push(isEmpty ? '' : this._getSpace(deep), zUtl.encodeXML(content), '<br/>');
	},
	comment: function (deep, content) {
		this.out.push(this._getSpace(deep), zUtl.encodeXML(content), '<br/>');
	},
	startTag: function (deep, content, isSingle, isEmpty) {
		this.out.push(this._getSpace(deep), this._parseAttribute(content, isSingle), isEmpty ? '' : '<br/>');
	},
	_parseAttribute: function (content, isSingle) {
		var out = [];
		for (var odd, start, c, i = 0, j = content.length; i < j; i++) {
			c = content.charAt(i);
			switch (c) {
				case '=':
					out.push('=<span style="color:#0666FD">');
					cnt = 0;
					if (!odd)
						odd = false;
					break;
				case '<':
					if (start) {// error caused by double '<' syntax
						out.push('<span style="color:red;">', zUtl.encodeXML(content.substring(i)), '</span>');
						this._errorNumber++;
						return out.join('');
					}
					out.push('&lt;');
					start = true;
					break;
				case '>':
					if (!isSingle)
						if (start)
							this.stack.push(content.substring(1, i));
					
					out.push('&gt;');
					break;
				case ' ':
					if (!isSingle) {
						isSingle = true;
						if (start)
							this.stack.push(content.substring(1, i));
					}
					out.push(c);
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
		if (content.indexOf('>') > -1)
			this.error(content);		
		else
			this.out.push(this._getSpace(deep), zUtl.encodeXML(content), '<br/>');
	},
	error: function (content) {
		this._errorNumber++;
		this.out.push('<span style="color:red"> Error caused by {', zUtl.encodeXML(content), '}</span><br/>');
	},
	toHTML: function () {
		return this.out.join('');
	},
	getErrorNumber: function () {
		return this._errorNumber;
	},
	_getSpace: function (deep) {
		var out = [];
		for (; deep-- > 0;)
			out.push('&nbsp;&nbsp;&nbsp;&nbsp;');
		return out.join('');
	}
});
