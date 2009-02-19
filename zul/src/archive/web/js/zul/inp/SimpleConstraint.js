/* constraint.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan  9 10:32:19     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
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
		l_out:
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
					continue l_out;
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
		var f = this._flags;
		if (cst == "no positive")
			f.NO_POSITIVE = true;
		else if (cst == "no negative")
			f.NO_NEGATIVE = true;
		else if (cst == "no zero")
			f.NO_ZERO = true;
		else if (cst == "no empty")
			f.NO_EMPTY = true;
		else if (cst == "no future")
			f.NO_FUTURE = true;
		else if (cst == "no past")
			f.NO_PAST = true;
		else if (cst == "no today")
			f.NO_TODAY = true;
		else if (cst == "strict")
			f.STRICT = true;
	},
	_cvtNum: function (v) { //compatible with server side
		var f = {};
		if (v & 1)
			f.NO_POSITIVE = f.NO_FUTURE = true;
		if (v & 2)
			f.NO_NEGATIVE = f.NO_PAST = true;
		if (v & 4)
			f.NO_ZERO = f.NO_TODAY = true;
		if (v & 0x100)
			f.NO_EMPTY = true;
		if (v & 0x200)
			f.STRICT = true;
		return f;
	},
	validate: function (wgt, val) {
		var f = this._flags,
			msg = this._errmsg;

		switch (typeof val) {
		case 'string':
			if (f.NO_EMPTY && (!val || !val.trim()))
				return msgzul.EMPTY_NOT_ALLOWED;
			var regex = this._regex;
			if (regex && !regex.test(val))
				return msg || msgzul.ILLEGAL_VALUE;
			if (f.STRICT && val) {
				//TODO VALUE_NOT_MATCHED;
			}
			return;
		case 'number':
			if (val > 0) {
				if (f.NO_POSITIVE) return msg || this._msgNumDenied();
			} else if (val == 0) {
				if (f.NO_ZERO) return msg || this._msgNumDenied();
			} else
				if (f.NO_NEGATIVE) return msg || this._msgNumDenied();
			return;
		}

		if (val && val.getFullYear) {
			var today = zUtl.today(),
				val = new Date(val.getFullYear(), val.getMonth(), val.getDate());
			if (val > today) {
				if (f.NO_FUTURE) return msg || this._msgDateDenied();
			} else if (val == today) {
				if (f.NO_TODAY) return msg || this._msgDateDenied();
			} else
				if (f.NO_PAST) return msg || this._msgDateDenied();
			return;
		}

		if (val && val.compareTo) {
			var b = val.compareTo(0);
			if (b > 0) {
				if (f.NO_POSITIVE) return msg || this._msgNumDenied();
			} else if (b == 0) {
				if (f.NO_ZERO) return msg || this._msgNumDenied();
			} else
				if (f.NO_NEGATIVE) return msg || this._msgNumDenied();
			return;
		}

		if (!val && f.NO_EMPTY) return msg || msgzul.EMPTY_NOT_ALLOWED;
	},
	_msgNumDenied: function () {
		var f = this._flags;
		if (f.NO_POSITIVE)
			return f.NO_ZERO ?
				f.NO_NEGATIVE ? NO_POSITIVE_NEGATIVE_ZERO: msgzul.NO_POSITIVE_ZERO:
				f.NO_NEGATIVE ? msgzul.NO_POSITIVE_NEGATIVE: msgzul.NO_POSITIVE;
		else if (f.NO_NEGATIVE)
			return f.NO_ZERO ? msgzul.NO_NEGATIVE_ZERO: msgzul.NO_NEGATIVE;
		else if (f.NO_ZERO)
			return msgzul.NO_ZERO;
		return msgzul.ILLEGAL_VALUE;
	},
	_msgDateDenied: function () {
		var f = this._flags;
		if (f.NO_FUTURE)
			return f.NO_TODAY ?
				f.NO_PAST ? NO_FUTURE_PAST_TODAY: msgzul.NO_FUTURE_TODAY:
				f.NO_PAST ? msgzul.NO_FUTURE_PAST: msgzul.NO_FUTURE;
		else if (f.NO_PAST)
			return f.NO_TODAY ? msgzul.NO_PAST_TODAY: msgzul.NO_PAST;
		else if (f.NO_TODAY)
			return msgzul.NO_TODAY;
		return msgzul.ILLEGAL_VALUE;
	}
});
