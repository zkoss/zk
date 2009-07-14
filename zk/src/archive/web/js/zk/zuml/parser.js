/* zuml.js

	Purpose:
		
	Description:
		
	History:
		Fri Apr 10 14:30:00     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.zuml.Parser = {
	create: function (parent, doc, args) {
		if (typeof doc == 'string')
			doc = zUtl.parseXML(doc);

		var cwgts = [];
		zk.zuml.Parser._cw(parent, doc.documentElement, cwgts, args);

		var l = cwgts.length;
		return l ? l == 1 ? cwgts[0]: cwgts: null;
	},
	createAt: function (node, opts, args) {
		var node = jq(node)[0],
			txt = node.innerHTML,
			j = txt.indexOf('<!--');
		if (j >= 0)
			txt = txt.substring(j + 4, txt.lastIndexOf('-->'));
		var wgt = this.create(null, '<div>' + txt.trim() + '</div>', args),
			cwgts = [];

		for (var w = wgt.firstChild, sib; w;) {
			sib = w.nextSibling;
			wgt.removeChild(w);
			cwgts.push(w);
			w = sib;
		}

		var l = cwgts.length;
		if (!l) return null;

		if (!opts || opts.replaceHTML !== false) {
			var ns = [node];
			if (l > 1) {
				var p = node.parentNode, sib = node.nextSibling;
				for (j = l; --j > 0;) {
					var n = document.createElement('DIV');
					ns.push(n);
					p.insertBefore(n, sib);
				}
			}
			for (j = 0; j < l; ++j)
				cwgts[j].replaceHTML(ns[j]);
		}

		return l == 1 ? cwgts[0]: cwgts;
	},
	_cw: function (parent, e, cwgts, args) {
		if (!e) return null;

		var $Parser = zk.zuml.Parser,
			forEach = $Parser._eval(parent, e.getAttribute('forEach'), args);
		if (forEach != null) {
			var oldEach = window.each;
			for (var l = forEach.length, j = 0; j < l; j++) {
				window.each = forEach[j];
				$Parser._cw2(parent, e, cwgts, args);
			}
			window.each = oldEach;
		} else
			$Parser._cw2(parent, e, cwgts, args);
	},
	_cw2: function (parent, e, cwgts, args) {
		var $Parser = zk.zuml.Parser;
		var ifc = $Parser._eval(parent, e.getAttribute('if'), args),
			unless = $Parser._eval(parent, e.getAttribute('unless'), args);
		if ((ifc == null || ifc) && (unless == null || !unless)) {
			var tn = e.tagName, wgt;
			if ("zk" == tn) {
				wgt = parent;
			} else if ("attribute" == tn) {
				var attnm = $Parser._eval(parent, e.getAttribute('name'), args);
				if (!attnm)
					throw "The name attribute required, "+e;
				parent.set(attnm, zUtl.getElementValue(e));
				return;
			} else {
				var atts = e.attributes;

				wgt = zk.Widget.newInstance(tn)
				if (cwgts) cwgts.push(wgt);
				if (parent) parent.appendChild(wgt);

				for (var l = atts.length, j = 0; j < l; ++j) {
					var att = atts[j];
					wgt.set(att.name, $Parser._eval(wgt, att.value, args));
				}
			}

			for (e = e.firstChild; e; e = e.nextSibling) {
				var nt = e.nodeType;
				if (nt == 1) $Parser._cw(wgt, e, null, args);
				else if (nt == 3) {
					var txt = e.nodeValue;
					if (txt.trim().length || wgt.blankPreserved) {
						var w = new zk.Native();
						w.prolog = $Parser._eval(wgt, txt, args);
						wgt.appendChild(w);
					}
				}
			}
		}
	},
	_eval: function (wgt, s, args) {
		if (s)
			for (var j = 0, k, l, t, last = s.length - 1, s2;;) {
				k = s.indexOf('${', j);
				if (k < 0) {
					if (s2) s = s2 + s.substring(j);
					break;
				}

				t = s.substring(j, k);
				l = s.indexOf('}', k + 2);
				if (l < 0) {
					s = s2 ? s2 + t: t; //ignore ${...
					break;
				}

				s2 = s2 ? s2 + t: t;
				t = s.substring(k + 2, l); //EL

				try {
					var fn = new Function('var _=arguments[0];return ' + t);
					t = wgt ? fn.call(wgt, args): fn(args);
				} catch (e) {
					throw 'Failed to evaluate '+t;
				}

				if (!s2 && l == last) return t; //don't convert to string

				if (t) s2 += t;

				j = l + 1;
			}
		return s;
	}
};
