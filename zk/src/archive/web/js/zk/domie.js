/* domie.js

	Purpose:
		Enhance/fix jQuery for Safari
	Description:
		
	History:
		Fri Jun 12 15:14:49     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	//detect </script>
	function containsScript(html) {
		if (html)
			for (var j = 0, len = html.length; (j = html.indexOf("</", j)) >= 0 && j + 8 < len;)
				if (html.substring(j += 2, j + 6).toLowerCase() == "script")
					return true;
	}
	function noSkipBfUnload() {
		zk.skipBfUnload = false;
	}

var _zjq = {};
zk.override(zjq, _zjq, {
	_fixCSS: function (el) {
		var zoom = el.style.zoom;
		el.style.zoom = 1;
		_zjq._fixCSS(el);
		setTimeout(function() {
			try {el.style.zoom = zoom;} catch (e) {}
		});
	}
});
zk.copy(zjq, {
	_src0: "javascript:false;",
		//IE: prevent secure/nonsecure warning with HTTPS

	_fixIframe: function (el) { //used in widget.js
		try {
			if (jq.nodeName(el, 'iframe'))
				zk(el).redoSrc();
			else
				for (var ns = el.getElementsByTagName("iframe"), j = ns.length; j--;)
					zk(ns[j]).redoSrc();
		} catch (e) {
		}
	},

	_fixClick: function (evt) {
		//Bug 1635685, 1612312: <a>
		//Bug 1896749: <area>
		var n;
		if (jq.nodeName(n = evt.target, "a", "area")
		&& n.href.indexOf("javascript:") >= 0) {
			zk.skipBfUnload = true;
			setTimeout(noSkipBfUnload, 0); //restore
		}
	},

	_beforeOuter: zk.$void, //overridden by domie6.js
	_afterOuter: zk.$void,

	_setOuter: function (el, html) {
		var done;
		try {
			//Note: IE's outerHTML cannot handle td/th.. and ignore script
			//so we have skip them (the result is memory leak)
			//
			//We can use jquery's evalScript to handle script elements,
			//but unable to find what scripts are created since they might not be
			//children of new created elements
			if ((el = jq(el)[0]) && !jq.nodeName(el, "td", "th", "table", "tr",
			"caption", "tbody", "thead", "tfoot", "colgroup","col")
			&& !containsScript(html)) {
				var o = zjq._beforeOuter(el);
				el.outerHTML = html; //less memory leak in IE
				done = true;
				zjq._afterOuter(o);
				return;
			}
		} catch (e) { //Unable to handle table/tr/...
		}
		if (!done)
			jq(el).replaceWith(html);
	},

	//pacth IE7 bug: script ignored if it is the first child (script2.zul)
	_fix1stJS: function (out, s) { //used in widget.js
		var j;
		if (this.previousSibling || s.indexOf('<script') < 0
		|| (j = out.length) > 20)
			return;
		for (var cnt = 0; j--;)
			if (out[j].indexOf('<') >= 0 && ++cnt > 1)
				return; //more than one
	 	out.push('<span style="display:none;font-size:0">&#160;</span>');
	}
});

	function _dissel() {
		this.onselectstart = _dissel0;
	}
	function _dissel0(evt) {
		evt = evt || window.event;
		var n = evt.srcElement;
		return jq.nodeName(n, "textarea", "input") && (n.type == "text" || n.type == "password");
	}
	function _ensel() {
		this.onselectstart = null;
	}
zk.copy(zjq.prototype, {
	disableSelection: function () {
		return this.jq.each(_dissel);
	},
	enableSelection: function () {
		return this.jq.each(_ensel);
	},

	cellIndex: function () {
		var cell = this.jq[0];
		if (cell) {
			var cells = cell.parentNode.cells;
			for(var j = 0, cl = cells.length; j < cl; j++)
				if (cells[j] == cell)
					return j;
		}
		return 0;
	}
});

})();

zk.override(jq.event, zjq._evt = {}, {
	fix: function (evt) {
		evt = zjq._evt.fix.apply(this, arguments);
		if (!evt.which && evt.button === 0)
			evt.which = 1; //IE
		return evt;
	}
});
