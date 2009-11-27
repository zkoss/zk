/* datefmt.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 19:13:43     2009, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.fmt.Date = {
	checkDate : function (ary, txt) {
		if (txt.length)
			for (var j = ary.length; j--;) {
				var k = txt.indexOf(ary[j]);
				if (k >= 0)
					txt = txt.substring(0, k) + txt.substring(k + ary[j].length);
			}
		return txt;
	},
	digitFixed : function (val, digits) {
		var s = "" + val;
		for (var j = digits - s.length; --j >= 0;)
			s = "0" + s;
		return s;
	},
	_parseToken: function (token, ts, i, len) {
		if (len < 2) len = 2;
		if (token && token.length > len) {
			ts[i] = token.substring(len);
			return token.substring(0, len);
		}
		return token;
	},
	parseDate : function (txt, fmt, strict) {
		if (!fmt) fmt = "yyyy/MM/dd";
		var val = new Date();
		var y = val.getFullYear(),
			m = val.getMonth(),
			d = val.getDate(),
			hr = val.getHours(),
			min = val.getMinutes(),
			sec = val.getSeconds(),
			aa = fmt.indexOf('a'),
			hh = fmt.indexOf('h'),
			KK = fmt.indexOf('K'),
			hasAM = aa > -1,
			hasHour1 = hasAM ? hh > -1 || KK > -1 : false,
			isAM;

		var	ts = [], mindex = fmt.indexOf("MMM"), aindex = fmt.indexOf("a"), ary = [],
			isNumber = !isNaN(txt);
		for (var i = 0, j = txt.length; i < j; i++) {
			var c = txt.charAt(i);
			if (c.match(/\d/)) {
				ary.push(c);
			} else if ((mindex > -1 && mindex <= i) || (aindex > -1 && aindex <= i)) {
				if (c.match(/\w/)) {
					ary.push(c);
				} else {
					if (c.charCodeAt() < 128) {
						if (ary.length) {
							ts.push(ary.join(""));
							ary = [];
						}
					} else
						ary.push(c);
				}
			} else if (ary.length) {
				ts.push(ary.join(""));
				ary = [];
			}
		}
		if (ary.length) ts.push(ary.join(""));
		for (var i = 0, j = 0, offs = 0, fl = fmt.length; j < fl; ++j) {
			var cc = fmt.charAt(j);
			if ((cc >= 'a' && cc <= 'z') || (cc >= 'A' && cc <= 'Z')) {
				var len = 1;
				for (var k = j; ++k < fl; ++len)
					if (fmt.charAt(k) != cc)
						break;

				var nosep; //no separator
				if (k < fl) {
					var c2 = fmt.charAt(k);
					nosep = c2 == 'y' || c2 == 'M' || c2 == 'd' || c2 == 'E';
				}
				var token = isNumber ? ts[0].substring(j - offs, k - offs) : ts[i++];
				switch (cc) {
				case 'y':
					if (nosep) {
						if (len <= 3) len = 2;
						if (token && token.length > len) {
							ts[--i] = token.substring(len);
							token = token.substring(0, len);
						}
					}
					y = zk.parseInt(token);
					if (isNaN(y)) return null; //failed
					if (y < 100) y += y > 29 ? 1900 : 2000;
					break;
				case 'M':
					var mon = txt.substring(j).toLowerCase(),
						mToken = token ? token.toLowerCase() : '';
					for (var index = zk.SMON.length; --index >= 0;) {
						var smon = zk.SMON[index].toLowerCase();
						if (mon.startsWith(smon) || (mToken && mToken.startsWith(smon))) {
							token = zk.SMON[index];
							m = index;
							break;
						}
					}
					if (len == 3 && token) {
						break; // nothing to do.
					}else if (len <= 2) {
						if (nosep && token && token.length > 2) {//Bug 2560497 : if no seperator, token must be assigned.
							ts[--i] = token.substring(2);
							token = token.substring(0, 2);
						}
						m = zk.parseInt(token) - 1;
						if (isNaN(m)) return null; //failed
					} else {
						for (var l = 0;; ++l) {
							if (l == 12) return null; //failed
							if (len == 3) {
								if (zk.SMON[l] == token) {
									m = l;
									break;
								}
							} else {
								if (token && zk.FMON[l].startsWith(token)) {
									m = l;
									break;
								}
							}
						}
					}
					break;
				case 'd':
					if (nosep)
						token = this._parseToken(token, ts, --i, len);
					d = zk.parseInt(token);
					if (isNaN(d)) return null; //failed
					break;
				case 'H':
					if (hasHour1)
						break;
					if (nosep)
						token = this._parseToken(token, ts, --i, len);
					hr = zk.parseInt(token);
					if (isNaN(hr)) return null; //failed
					break;
				case 'h':
					if (!hasHour1)
						break;
					if (nosep)
						token = this._parseToken(token, ts, --i, len);
					hr = zk.parseInt(token);
					if (hr == 12)
						hr = 0;
					if (isNaN(hr)) return null; //failed
					break;
				case 'K':
					if (!hasHour1)
						break;
					if (nosep)
						token = this._parseToken(token, ts, --i, len);
					hr = zk.parseInt(token);
					if (isNaN(hr)) return null; //failed
					hr %= 12;
					break;
				case 'k':
					if (hasHour1)
						break;
					if (nosep)
						token = this._parseToken(token, ts, --i, len);
					hr = zk.parseInt(token);
					if (hr == 24)
						hr = 0;
					if (isNaN(hr)) return null; //failed
					break;					
				case 'm':
					if (nosep)
						token = this._parseToken(token, ts, --i, len);
					min = zk.parseInt(token);
					if (isNaN(min)) return null; //failed
					break;
				case 's':
					if (nosep)
						token = this._parseToken(token, ts, --i, len);
					sec = zk.parseInt(token);
					if (isNaN(sec)) return null; //failed
					break;
				case 'a':
					if (!hasHour1)
						break;
					isAM = txt.substring(j).startsWith(zk.APM[0]);
					break
				//default: ignored
				}
				j = k - 1;
			} else offs++;
		}

		if (hasHour1 && isAM === false)
			hr += 12;
		var dt = new Date(y, m, d, hr, min, sec);
		if (strict) {
			if (dt.getFullYear() != y || dt.getMonth() != m || dt.getDate() != d ||
				dt.getHours() != hr || dt.getMinutes() != min || dt.getSeconds() != sec)
				return null; //failed

			txt = txt.trim();
			txt = this.checkDate(zk.SDOW, txt);
			txt = this.checkDate(zk.S2DOW, txt);
			txt = this.checkDate(zk.FDOW, txt);
			txt = this.checkDate(zk.SMON, txt);
			txt = this.checkDate(zk.S2MON, txt);
			txt = this.checkDate(zk.FMON, txt);
			txt = this.checkDate(zk.APM, txt);
			for (var j = txt.length; j--;) {
				var cc = txt.charAt(j);
				if ((cc > '9' || cc < '0') && fmt.indexOf(cc) < 0)
					return null; //failed
			}
		}
		return dt;
	},
	formatDate : function (val, fmt) {
		if (!fmt) fmt = "yyyy/MM/dd";

		var txt = "";
		for (var j = 0, fl = fmt.length; j < fl; ++j) {
			var cc = fmt.charAt(j);
			if ((cc >= 'a' && cc <= 'z') || (cc >= 'A' && cc <= 'Z')) {
				var len = 1;
				for (var k = j; ++k < fl; ++len)
					if (fmt.charAt(k) != cc)
						break;

				switch (cc) {
				case 'y':
					if (len <= 3) txt += this.digitFixed(val.getFullYear() % 100, 2);
					else txt += this.digitFixed(val.getFullYear(), len);
					break;
				case 'M':
					if (len <= 2) txt += this.digitFixed(val.getMonth()+1, len);
					else if (len == 3) txt += zk.SMON[val.getMonth()];
					else txt += zk.FMON[val.getMonth()];
					break;
				case 'd':
					txt += this.digitFixed(this.dayInMonth(val), len);
					break;
				case 'E':
					if (len <= 3) txt += zk.SDOW[(val.getDay() - zk.DOW_1ST) % 7];
					else txt += zk.FDOW[(val.getDay() - zk.DOW_1ST) % 7];
					break;
				case 'D':
					txt += this.dayInYear(val);
					break;
				case 'w':
					txt += this.weekInYear(val);
					break;
				case 'W':
					txt += this.weekInMonth(val);
					break;
				case 'G':
					txt += "AD";
					break;
				case 'F':
					txt += this.dayOfWeekInMonth(val);
					break;
				case 'H':
					if (len <= 2) txt += this.digitFixed(val.getHours(), len);
					break;
				case 'k':
					var h = val.getHours();
					if (h == 0)
						h = '24';
					if (len <= 2) txt += this.digitFixed(h, len);
					break;
				case 'K':
					if (len <= 2) txt += this.digitFixed(val.getHours() % 12, len);
					break;
				case 'h':
					var h = val.getHours();
					h %= 12;
					if (h == 0)
						h = '12';
					if (len <= 2) txt += this.digitFixed(h, len);
					break;
				case 'm':
					if (len <= 2) txt += this.digitFixed(val.getMinutes(), len);
					break;
				case 's':
					if (len <= 2) txt += this.digitFixed(val.getSeconds(), len);
					break;
				case 'Z':
					txt += -(val.getTimezoneOffset()/60);
					break;
				case 'a':
					txt += zk.APM[val.getHours() > 11 ? 1 : 0];
					break;
				default:
					txt += '1';
					//fake; SimpleDateFormat.parse might ignore it
					//However, it must be an error if we don't generate a digit
				}
				j = k - 1;
			} else {
				txt += cc;
			}
		}
		return txt;
	},
	/** Converts milli-second to day. */
	ms2day : function (t) {
		return Math.round(t / 86400000);
	},
	/** Day in year (starting at 1). */
	dayInYear : function (d, ref) {
		if (!ref) ref = new Date(d.getFullYear(), 0, 1);
		return 1 + this.digitFixed(d - ref);
	},
	/** Day in month (starting at 1). */
	dayInMonth : function (d) {
		return d.getDate();
	},
	/** Week in year (starting at 1). */
	weekInYear : function (d, ref) {
		if (!ref) ref = new Date(d.getFullYear(), 0, 1);
		var wday = ref.getDay();
		if (wday == 7) wday = 0;
		return 1 + Math.floor((this.digitFixed(d - ref) + wday) / 7);
	},
	/** Week in month (starting at 1). */
	weekInMonth : function (d) {
		return this.weekInYear(d, new Date(d.getFullYear(), d.getMonth(), 1));
	},
	/** Day of week in month. */
	dayOfWeekInMonth : function (d) {
		return 1 + Math.floor(this.digitFixed(d - new Date(d.getFullYear(), d.getMonth(), 1)) / 7);
	}
};