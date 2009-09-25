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
			fixBU(n.getElementsByTagName("A")); //Bug 1635685, 1612312
			fixBU(n.getElementsByTagName("AREA")); //Bug 1896749

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

jq.zrm = function (el) { //override remove (see jquery.js)
	//refer http://kossovsky.net/index.php/2009/07/ie-memory-leak-jquery-garbage-collector/
	var gcid = '_z_lkgc',
		gc = document.getElementById(gcid);
	if (!gc) {
		gc = document.createElement('div');
		gc.id = gcid;
		gc.style.display = 'none';
		document.body.appendChild(gc);
	}
	
	gc.appendChild(el);
	gc.innerHTML = '';
};
