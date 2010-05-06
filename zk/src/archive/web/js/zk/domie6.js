/* domie6.js

	Purpose:
		Fix the alpha transparency issue found in IE6
	Description:
 		IE 5.5/6 (not 7) has a bug that failed to render PNG with
		alpha transparency. 
		See
		http://code.google.com/p/ie7-js/
	History:
		Thu May 14 18:05:58 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var _imgFiltered = [], //filtered img
		_IMGLD = 'DXImageTransform.Microsoft.AlphaImageLoader',
		_FILTER = "progid:" + _IMGLD + "(src='%1',sizingMethod='scale')",
		_inPrint,
		_jq$fn = {}, //jq.fn's super
		_nodesToFix = []; //what to fix

	function _regexFormat(s) {
		var args = arguments;
		var regex = new RegExp("%([1-" + arguments.length + "])", "g");
		return String(s).replace(regex, function (match, index) {
			return index < args.length ? args[index] : match;
		});
	}
	function _regexEscape(s) {
		return String(s).replace(/([\/()[\]{}|*+-.,^$?\\])/g, "\\$1");
	}

	function _onpropchange() {
	 	if (!_inPrint && event.propertyName == "src"
	 	&& this.src.indexOf('spacer.gif') < 0)
	 		_fix(this);
	}
	function _fix(img) {
		var ni = new Image(img.width, img.height);
		ni.onload = function() {
			img.width = ni.width;
			img.height = ni.height;
			ni = null;
		};
		ni.src = img.src; //store the original url (we'll put it back when it's printed)
		img._pngSrc = img.src; //add the AlphaImageLoader thingy
		_addFilter(img);
	}
	function _addFilter(img) {
		var filter = img.filters[_IMGLD];
		if (filter) {
			filter.src = img.src;
			filter.enabled = true;
		} else {
			img.runtimeStyle.filter = _regexFormat(_FILTER, img.src);
			_imgFiltered.push(img);
		}
		img.src = zk.SPACER_URI; //remove the real image
	}
	function _rmFilter(img) {
		img.src = img._pngSrc;
		img.filters[_IMGLD].enabled = false;
	}

	//fix DOM
	function fixDom(n, nxt) { //exclude nxt (if null, means to the end)
		for (; n && n != nxt; n = n.nextSibling)
			if (n.nodeType == 1) {
				_nodesToFix.push(n);
				setTimeout(fixDom0, 100);
			}
	}
	function fixDom0() {
		var n = _nodesToFix.shift();
		if (n) {
			_alphafix(n);

			if (_nodesToFix.length) setTimeout(fixDom0, 100);
		}
	}
	function _alphafix(n) {
		var regex = jq.IE6_ALPHAFIX;
		if (!regex) return;

		if (typeof regex == 'string')
			regex = jq.IE6_ALPHAFIX
				= new RegExp(_regexEscape(regex) + "$", "i");
		if (!zk.SPACER_URI)
			zk.SPACER_URI = zk.ajaxURI('web/img/spacer.gif', {au:true});
		var imgs = n.getElementsByTagName("img");
		for (var j = imgs.length; j--; ) {
			var img = imgs[j], src = img.src,
				k = src.lastIndexOf(';');
			if (k >= 0) src = src.substring(0, k);
			if (regex.test(img.src)) {
				_fix(img);
				jq(img).bind("propertychange", _onpropchange);
			}
		}
	}

zk.override(jq.fn, _jq$fn, {
	before: function () {
		var e = this[0], ref;
		if (e) ref = e.previousSibling;

		ret = _jq$fn.before.apply(this, arguments);

		if (e) fixDom(ref ? ref.nextSibling:
			e.parentNode ? e.parentNode.firstChild: null, e);
			//IE: som element might have no parent node (such as audio)
		return ret;
	},
	after: function () {
		var e = this[0], ref;
		if (e) ref = e.nextSibling;

		ret = _jq$fn.after.apply(this, arguments);

		if (e) fixDom(e.nextSibling, ref);
		return ret;
	},
	append: function () {
		var e = this[0], ref;
		if (e) ref = e.lastChild;

		ret = _jq$fn.append.apply(this, arguments);

		if (e) fixDom(ref ? ref.nextSibling: e.firstChild);
		return ret;
	},
	prepend: function () {
		var e = this[0], ref;
		if (e) ref = e.firstChild;

		ret = _jq$fn.prepend.apply(this, arguments);

		if (e) fixDom(e.firstChild, ref);
		return ret;
	},
	replaceWith: function () {
		var e = this[0], ref, ref2, p;
		if (e) {
			p = e.parentNode;
			ref = e.previousSibling;
			ref2 = e.nextSibling;
		}

		ret = _jq$fn.replaceWith.apply(this, arguments);

		if (e)
			fixDom(ref ? ref.nextSibling: p ? p.firstChild: null, ref2);
			//IE: som element might have no parent node (such as audio)
		return ret;
	},
	html: function (content) {
		var e = content === undefined ? null: this[0];

		ret = _jq$fn.html.apply(this, arguments);

		if (e) fixDom(e.firstChild);
		return ret;
	},

	clone: function () {
		var clone = _jq$fn.clone.apply(this, arguments), n, nc;
		for (var j = 0; j < this.length; ++j) {
			n = this[j];
			if (jq.nodeName(n, "img") && n._pngSrc) {
				(nc = clone[j]).src = n._pngSrc;
				setTimeout(function() {_fix(nc);}, 0); //we have to wait
			}
		}
		return clone;
	}
});

zk.copy(zjq, {
	//Override domie.js
	_beforeOuter: function (e) {
		if (e)
			return {p: e.parentNode, ref: e.previousSibling, ref2: e.nextSibling};
	},
	_afterOuter: function (o) {
		if (o)
			fixDom(o.ref ? o.ref.nextSibling: o.p ? o.p.firstChild: null, o.ref2);
	}
});

jq(window).bind("beforeprint", function() {
		_inPrint = true;
		for (var ns = _imgFiltered, i = 0, len = ns.length; i < len; i++) {
			var n = ns[i];
			try {
				_rmFilter(n);
			} catch (e) {
				ns.splice(i--, 1); //no longer avail
				len--;
			}
		}
	})
	.bind("afterprint", function() {
		for (var ns = _imgFiltered, i = 0, len = ns.length; i < len; i++)
			_addFilter(ns[i]);
		_inPrint = false;
	});

})();