/* constraint.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan  9 10:32:19     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.SimpleConstraint = zk.$extends(zk.Object, {
	$init: function (a, b, c) {
		if (typeof a == 'string') {
			this._flags = {};
			this._init(a);
		} else {
			if (a) this._flags = typeof a == 'number' ? this._cvtNum(a): a;
			this._regex = typeof b == 'string' ? new RegExp(b): b;
			this._errmsg = c; 
		}
	},
	_init: function (cst) {
		for (var j = 0, k = 0, len = cst.length; k >= 0; j = k + 1) {
			for (;; ++j) {
				if (j >= len) return; //done

				var cc = cst.charAt(j);
				if (cc == '/') {
					for (k = ++j;; ++k) { //look for ending /
						if (k >= len) { //no ending /
							k = -1;
							break;
						}

						cc = cst.charAt(k);
						if (cc == '/') break; //ending / found
						if (cc == '\\') ++k; //skip one
					}
					this._regex = new RegExp(k >= 0 ? cst.substring(j, k): cst.substring(j));
					break;
				}
				if (cc == ':') {
					this._errmsg = cst.substring(j + 1).trim();
					return; //done
				}
				if (!zk.isWhitespace(cc))
					break;
			}

			var s;
			for (k = j;; ++k) {
				if (k >= len) {
					s = cst.substring(j);
					k = -1;
					break;
				}
				var cc = cst.charAt(k);
				if (cc == ',' || cc == ':' || cc == ';' || cc == '/') {
					s = cst.substring(j, k);
					if (cc == ':' || cc == '/') --k;
					break;
				}
			}

			this.parseConstraint_(s.trim().toLowerCase());
		}
	},
	parseConstraint_: function (cst) {
		var flags = this._flags;
		if (cst == "no positive")
			flags.NO_POSITIVE = true;
		else if (cst == "no negative")
			flags.NO_NEGATIVE = true;
		else if (cst == "no zero")
			flags.NO_ZERO = true;
		else if (cst == "no empty")
			flags.NO_EMPTY = true;
		else if (cst == "no future")
			flags.NO_FUTURE = true;
		else if (cst == "no past")
			flags.NO_PAST = true;
		else if (cst == "no today")
			flags.NO_TODAY = true;
		else if (cst == "strict")
			flags.STRICT = true;
	},
	_cvtNum: function (v) { //compatible with server side
		var flags = {};
		if (v & 1)
			flags.NO_POSITIVE = flags.NO_FUTURE = true;
		if (v & 2)
			flags.NO_NEGATIVE = flags.NO_PAST = true;
		if (v & 4)
			flags.NO_ZERO = flags.NO_TODAY = true;
		if (v & 0x100)
			flags.NO_EMPTY = true;
		if (v & 0x200)
			flags.STRICT = true;
		return flags;
	},
	validate: function (wgt, val) {
	}
});
