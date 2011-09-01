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

var _zjq = {}, _jq = {};
zk.override(jq.fn, _jq, {
	replaceWith: function (html) {
		//outerHTML to minimize memory leak in IE
		var done, el;
		try {
			//Note: IE's outerHTML cannot handle td/th.. and ignore script
			//so we have skip them (the result is memory leak)
			//
			//We can use jquery's evalScript to handle script elements,
			//but unable to find what scripts are created since they might not be
			//children of new created elements
			if (typeof html == 'string' && (el = this[0])
			&& !jq.nodeName(el, "td", "th", "table", "tr",
			"caption", "tbody", "thead", "tfoot", "colgroup","col")
			&& !containsScript(html)) {
				var o = zjq._beforeOuter(el);

				jq.cleanData(el.getElementsByTagName("*"));
				jq.cleanData([el]);
				el.innerHTML = ""; //seems less memory leak
				el.outerHTML = html;
				done = true;
				zjq._afterOuter(o);
				return this;
			}
		} catch (e) {
		}
		return done ? this: _jq.replaceWith.apply(this, arguments);
	}
});
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
	src0: "javascript:false;",
		//IE: prevent secure/nonsecure warning with HTTPS

	_fixIframe: function (el) { //used in widget.js (Bug 2900274)
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
		if (zk.confirmClose)
			for (var n = evt.target; n; n = n.parentNode)
				if (jq.nodeName(n, "a", "area")) {
					if (n.href.indexOf("javascript:") >= 0) {
						zk.skipBfUnload = true;
						setTimeout(noSkipBfUnload, 0); //restore
					}
					return;
				}
	},

	_beforeOuter: zk.$void, //overridden by domie6.js
	_afterOuter: zk.$void
});

	function _dissel() {
		this.onselectstart = _dissel0;
	}
	function _dissel0(evt) {
		evt = evt || window.event;
		return zk(evt.srcElement).isInput();
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

zk.override(jq.event, zjq._evt = {}, {
	fix: function (evt) {
		evt = zjq._evt.fix.apply(this, arguments);
		if (!evt.which && evt.button === 0)
			evt.which = 1; //IE
		return evt;
	}
});

//IE: use query string if possible to avoid incomplete-request problem
if (zk.ie < 8) {
	zjq.fixOnResize = function (tmout) {
		//IE6/7: it sometimes fires an "extra" onResize in loading
		//so we have to filter it out (to improve performance)
		//The other case is an extra onResize is fired if a position=absolute
		//element is created (such zk.log) -- but we don't try to fix it
		//(since it might not be worth; fix it only if really necessary)
		zk.skipResize = (zk.skipResize||0) + 1;
		setTimeout(function () {--zk.skipResize;}, tmout);
	};
	zjq._useQS = function (reqInf) {
		var s = reqInf.content, j = s.length, prev, cc;
		if (j + reqInf.uri.length < 2000) {
			while (j--) {
				cc = s.charAt(j);
				if (cc == '%' && prev >= '8') //%8x, %9x...
					return false;
				prev = cc;
			}
			return true;
		}
		return false;
	};

	function _visi0($n) {
		return $n.css('display') != 'none';
	}
	function _visi1($n) {
		return _visi0($n) && $n.css('visibility') != 'hidden';
	}
  zk.copy(zjq.prototype, {
	isRealVisible: function (strict) {
		var $n = this.jq;
		if (!$n.length) return false;

		//we cannot use jq().is(':visible') in this case, becuase it is not reliable
		var fn = strict ? _visi1: _visi0,
			body = document.body;
		do {
			if (!fn($n))
				return false;
		} while (($n = $n.parent()) && $n[0] != body); //yes, assign
		return true;
	},
	offsetWidth: function () {
		var el = this.jq[0];
		return !jq.nodeName(el, "tr") || this.isRealVisible() ? el.offsetWidth: 0;
	},
	offsetHeight: function () {
		var el = this.jq[0];
		if (!jq.nodeName(el, "tr"))
			return el.offsetHeight;

		var hgh = 0;
		if (this.isRealVisible()) {
			for (var cells = el.cells, j = cells.length; j--;) {
				var h = cells[j].offsetHeight;
				if (h > hgh) 
					hgh = h;
			}
		}
		return hgh;
	}
  });
}

})();
