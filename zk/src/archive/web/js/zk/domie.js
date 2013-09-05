/* domie.js

	Purpose:
		Enhance/fix jQuery for Safari
	Description:
		
	History:
		Fri Jun 12 15:14:49     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	//detect </script>
	function containsScript(html) {
		if (html)
			for (var j = 0, len = html.length; (j = html.indexOf('</', j)) >= 0 && j + 8 < len;)
				if (html.substring(j += 2, j + 6).toLowerCase() == 'script')
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
			&& !jq.nodeName(el, 'td', 'th', 'table', 'tr',
			'caption', 'tbody', 'thead', 'tfoot', 'colgroup', 'col')
			&& !containsScript(html)) {
				var o = zjq._beforeOuter(el);

				jq.cleanData(el.getElementsByTagName('*'));
				jq.cleanData([el]);
				el.innerHTML = ''; //seems less memory leak
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
	_fixCSS: zk.ie9_ ? function (el) { // fix for filter gradient issue
		var old = el.className,
			oldDisplay = el.style.display;
		el.className = '';
		el.style.display = 'none';
		if (el.offsetHeight);
		el.className = old;
		el.style.display = oldDisplay;
	} : function (el) {
		var zoom = el.style.zoom;
		el.style.zoom = 1;
		_zjq._fixCSS(el);
		setTimeout(function() {
			try {el.style.zoom = zoom;} catch (e) {}
		});
	}
});
zk.copy(zjq, {
	src0: 'javascript:\'\';',
		//IE: prevent secure/nonsecure warning with HTTPS

	//IE sometimes won't show caret when setting a focus to an input element
	//See also Bug ZK-426
	fixInput: function (el) {
		try {
			var $n = zk(el), pos;
			if ($n.isInput()) {
				pos = $n.getSelectionRange();
				$n.setSelectionRange(pos[0], pos[1]);
			}
		} catch (e) { //ignore
		}
	},

	_fixIframe: function (el) { //used in widget.js (Bug 2900274)
		try {
			if (jq.nodeName(el, 'iframe'))
				zk(el).redoSrc();
			else
				for (var ns = el.getElementsByTagName('iframe'), j = ns.length; j--;)
					zk(ns[j]).redoSrc();
		} catch (e) {
		}
	},

	_fixClick: function (evt) {
		//Bug 1635685, 1612312: <a>
		//Bug 1896749: <area>
		if (zk.confirmClose)
			for (var n = evt.target; n; n = n.parentNode)
				if (jq.nodeName(n, 'a', 'area')) {
					if (n.href.indexOf('javascript:') >= 0) {
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
if (zk.ie >= 9)
	zjq.minWidth = function (el) {
		return zk(el).offsetWidth() + 1; //IE9/IE10: bug ZK-483: an extra pixel required
	};

})();
