/* domie.js

	Purpose:
		Enhance/fix jQuery for Safari
	Description:
		
	History:
		Fri Jun 12 15:14:49     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.override(jq.fn, zjq._fnie = {}, {
	before: function () {
		var e = this[0], ref;
		if (e) ref = e.previousSibling;

		ret = zjq._fnie.before.apply(this, arguments);

		if (e) zjq._fixDom(ref ? ref.nextSibling: e.parentNode.firstChild, e);
		return ret;
	},
	after: function () {
		var e = this[0], ref;
		if (e) ref = e.nextSibling;

		ret = zjq._fnie.after.apply(this, arguments);

		if (e) zjq._fixDom(e.nextSibling, ref);
		return ret;
	},
	append: function () {
		var e = this[0], ref;
		if (e) ref = e.lastChild;

		ret = zjq._fnie.append.apply(this, arguments);

		if (e) zjq._fixDom(ref ? ref.nextSibling: e.firstChild);
		return ret;
	},
	prepend: function () {
		var e = this[0], ref;
		if (e) ref = e.firstChild;

		ret = zjq._fnie.prepend.apply(this, arguments);

		if (e) zjq._fixDom(e.firstChild, ref);
		return ret;
	},
	replaceWith: function () {
		var e = this[0], ref, ref2;
		if (e) {
			ref = e.previousSibling;
			ref2 = e.nextSibling;
		}

		ret = zjq._fnie.replaceWith.apply(this, arguments);

		if (e) zjq._fixDom(ref ? ref.nextSibling: e.parentNode.firstChild, ref2);
		return ret;
	},
	html: function (content) {
		var e = content === undefined ? null: this[0];

		ret = zjq._fnie.html.apply(this, arguments);

		if (e) zjq._fixDom(e.firstChild);
		return ret;
	}
});

zk.copy(zjq, {
	//fix DOM
	_fixDom: function (n, nxt) { //exclude nxt (if null, means to the end)
		for (; n && n != nxt; n = n.nextSibling)
			try {
				if (n.nodeType == 1) {
					zjq._fxns.push(n);
					setTimeout(zjq._fixDom0, 100);
				}
			} catch (e) { //some IE element (such as audio) not accessible
			}
	},
	_unfixDom: function (n) {
		if (n && !zjq._fxns.$remove(n))
			setTimeout(function() {zjq._unfixDom0(n);}, 1000);
	},
	_fxns: [], //what to fix
	_fixDom0: function () {
		var n = zjq._fxns.shift();
		if (n) {
			zjq._alphafix(n);
			zjq._fixBU(n.getElementsByTagName("A")); //Bug 1635685, 1612312
			zjq._fixBU(n.getElementsByTagName("AREA")); //Bug 1896749

			if (zjq._fxns.length) setTimeout(zjq._fixDom0, 300);
		}
	},
	_alphafix: zk.$void, //override if ie6_
	_unfixDom0: function (n) {
		if (n) {
			zjq._unfixBU(n.getElementsByTagName("A"));
			zjq._unfixBU(n.getElementsByTagName("AREA"));
		}
	},
	_fixBU: function (ns) {
		for (var j = ns.length; j--;) {
			var n = ns[j];
			if (!n.z_fixed && n.href.indexOf("javascript:") >= 0) {
				n.z_fixed = true;
				jq(n).click(zjq._doSkipBfUnload);
			}
		}
	},
	_unfixBU: function (ns) {
		for (var j = ns.length; j--;) {
			var n = ns[j];
			if (n.z_fixed) {
				n.z_fixed = false;
				jq(n).unbind("click", zjq._doSkipBfUnload);
			}
		}
	},
	_doSkipBfUnload: function () {
		zk.skipBfUnload = true;
		setTimeout(zjq._unSkipBfUnload, 0); //restore
	},
	_unSkipBfUnload: function () {
		zk.skipBfUnload = false;
	},

	//super//
	_redoCSS: function () {
		if (zjq._rdcss.length) {
			try {
				var el;
				while ((el = zjq._rdcss.pop())) {
					var z = el.style.zoom;
					el.style.zoom = 1;
					el.className += ' ';
					if (el.offsetHeight) 
						;
					el.className.trim();
					zjq._cleanCSS(el, z);
				}
			} catch (e) {}
			
			// just in case
			setTimeout(zjq._redoCSS);
		}
	},
	_cleanCSS: function(el, z) {
		setTimeout(function() {
			try {
				el.style.zoom = z;
			} catch (e) {}
		});
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
