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
	var fxNodes = [], //what to fix
		$fns = {}; //super's fn

	//fix DOM
	function fixDom(n, nxt) { //exclude nxt (if null, means to the end)
		for (; n && n != nxt; n = n.nextSibling)
			if (n.nodeType == 1) {
				fxNodes.push(n);
				setTimeout(fixDom0, 100);
			}
	}
	function fixDom0() {
		var n = fxNodes.shift();
		if (n) {
			zjq._alphafix(n);
			fixBU(n.getElementsByTagName("a")); //Bug 1635685, 1612312
			fixBU(n.getElementsByTagName("area")); //Bug 1896749

			if (fxNodes.length) setTimeout(fixDom0, 300);
		}
	}
	function fixBU(ns) {
		for (var j = ns.length; j--;) {
			var n = ns[j];
			if (!n.z_fixed && n.href.indexOf("javascript:") >= 0) {
				n.z_fixed = true;
				jq(n).click(doSkipBfUnload);
			}
		}
	}
	function doSkipBfUnload() {
		zk.skipBfUnload = true;
		setTimeout(unSkipBfUnload, 0); //restore
	}
	function unSkipBfUnload() {
		zk.skipBfUnload = false;
	}

zk.override(jq.fn, $fns, {
	before: function () {
		var e = this[0], ref;
		if (e) ref = e.previousSibling;

		ret = $fns.before.apply(this, arguments);

		if (e) fixDom(ref ? ref.nextSibling:
			e.parentNode ? e.parentNode.firstChild: null, e);
			//IE: som element might have no parent node (such as audio)
		return ret;
	},
	after: function () {
		var e = this[0], ref;
		if (e) ref = e.nextSibling;

		ret = $fns.after.apply(this, arguments);

		if (e) fixDom(e.nextSibling, ref);
		return ret;
	},
	append: function () {
		var e = this[0], ref;
		if (e) ref = e.lastChild;

		ret = $fns.append.apply(this, arguments);

		if (e) fixDom(ref ? ref.nextSibling: e.firstChild);
		return ret;
	},
	prepend: function () {
		var e = this[0], ref;
		if (e) ref = e.firstChild;

		ret = $fns.prepend.apply(this, arguments);

		if (e) fixDom(e.firstChild, ref);
		return ret;
	},
	replaceWith: function () {
		var e = this[0], ref, ref2;
		if (e) {
			ref = e.previousSibling;
			ref2 = e.nextSibling;
		}

		ret = $fns.replaceWith.apply(this, arguments);

		if (e) fixDom(ref ? ref.nextSibling:
			e.parentNode ? e.parentNode.firstChild: null, ref2);
			//IE: som element might have no parent node (such as audio)
		return ret;
	},
	html: function (content) {
		var e = content === undefined ? null: this[0];

		ret = $fns.html.apply(this, arguments);

		if (e) fixDom(e.firstChild);
		return ret;
	}
});

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
		var n = evt.srcElement, tag = n ? jq.nodeName(n): '';
		return (tag == "textarea" || tag == "input") && (n.type == "text" || n.type == "password");
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

zjq._alphafix = zk.$void; //overriden if ie6_

zk.override(jq.event, zjq._evt = {}, {
	fix: function (evt) {
		evt = zjq._evt.fix.apply(this, arguments);
		if (!evt.which && evt.button === 0)
			evt.which = 1; //IE
		return evt;
	}
});
