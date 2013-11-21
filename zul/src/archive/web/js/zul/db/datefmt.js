/* datefmt.js

	Purpose:

	Description:

	History:
		Fri Jan 16 19:13:43	 2009, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _parseTextToArray(txt, fmt) {
		if (fmt.indexOf('\'') > -1) //Bug ZK-1341: 'long+medium' format with single quote in zh_TW locale failed to parse AM/PM
			fmt = fmt.replace(/'/g, '');
		var ts = [], mindex = fmt.indexOf("MMM"), eindex = fmt.indexOf("EE"),
			fmtlen = fmt.length, ary = [],
			//mmindex = mindex + 3,
			aa = fmt.indexOf('a'),
			tlen = txt.replace(/[^.]/g, '').length,
			flen = fmt.replace(/[^.]/g, '').length;
			
			
		for (var i = 0, k = 0, j = txt.length; k < j; i++, k++) {
			var c = txt.charAt(k),
				f = fmtlen > i ? fmt.charAt(i) : "";
			if (c.match(/\d/)) {
				ary.push(c);
			} else if ((mindex >= 0 && mindex <= i /*&& mmindex >= i location French will lose last char */)
			|| (eindex >= 0 && eindex <= i) || (aa > -1 && aa <= i)) {
				if (c.match(/\w/)) {
					ary.push(c);
				} else {
					if (c.charCodeAt(0) < 128 && (c.charCodeAt(0) != 46 ||
								tlen == flen || f.charCodeAt(0) == 46)) {
						if (ary.length) {
							ts.push(ary.join(""));
							ary = [];
						}
					} else
						ary.push(c);
				}
			} else if (ary.length) {
				if (txt.charAt(k-1).match(/\d/))
					while (f == fmt.charAt(i-1) && f) {
						f = fmt.charAt(++i);
					}
				ts.push(ary.join(""));
				ary = [];
			} else if (c.match(/\w/))
				return; //failed
		}
		if (ary.length) ts.push(ary.join(""));
		return ts;
	}
	function _parseToken(token, ts, i, len) {
		if (len < 2) len = 2;
		if (token && token.length > len) {
			ts[i] = token.substring(len);
			return token.substring(0, len);
		}
		return token;
	}
	function _parseInt(v) {
		return parseInt(v, 10);
	}
	function _digitFixed(val, digits) {
		var s = "" + val;
		for (var j = digits - s.length; --j >= 0;)
			s = "0" + s;
		return s;
	}
	function _ckDate(ary, txt) {
		if (txt.length)
			for (var j = ary.length; j--;) {
				var k = txt.indexOf(ary[j]);
				if (k >= 0)
					txt = txt.substring(0, k) + txt.substring(k + ary[j].length);
			}
		return txt;
	}
	function _dayInYear(d, ref) {
		return Math.round((new Date(d.getFullYear(), d.getMonth(), d.getDate())-ref)/864e5);
	}
	// Converts milli-second to day.
//	function _ms2day(t) {
//		return Math.round(t / 86400000);
//	}
	// Day in year (starting at 1).
	function dayInYear(d, ref) {
		if (!ref) ref = new Date(d.getFullYear(), 0, 1);
		return _digitFixed(1 + _dayInYear(d, ref));
	}
	//Day in month (starting at 1).
	function dayInMonth(d) {
		return d.getDate();
	}
	//Week in year (starting at 1).
	function weekInYear(d, ref) {
		if (!ref) ref = new Date(d.getFullYear(), 0, 1);
		var wday = ref.getDay();
		if (wday == 7) wday = 0;
		return _digitFixed(1 + Math.floor((_dayInYear(d, ref) + wday) / 7));
	}
	//Week in month (starting at 1).
	function weekInMonth(d) {
		return weekInYear(d, new Date(d.getFullYear(), d.getMonth(), 1));
	}
	//Day of week in month.
	function dayOfWeekInMonth(d) {
		return _digitFixed(1 + Math.floor(_dayInYear(d, new Date(d.getFullYear(), d.getMonth(), 1)) / 7));
	}

// a proxy of Date object for leap day on Thai locale - B60-ZK-1010
var LeapDay = zk.$extends(zk.Object, {
	$init: function (y, m, d, hr, min, sec, msec) {
		this.$supers('$init', arguments);
		if (arguments.length > 1) {
			this._date = new Date(y, m, d, hr, min, sec, msec);
		} else
			this._date = y;
	},
	setOffset: function (v) {
		this._offset = v;
	},
	setFullYear: function (val) {
		// no need to subtract the._offset, the caller will handle
		this._date.setFullYear(val);
	},
	getFullYear: function () {
		return this._date.getFullYear() + (this._offset || 0);
	},
	getDate: function () {
		return this._date.getDate();
	},
	setDate: function (d) {
		this._date.setDate(d);
	},
	getDay: function () {
		return this._date.getDay();
	},
	setDay: function (d) {
		this._date.setDay(d);
	},
	getMonth: function () {
		return this._date.getMonth();
	},
	setMonth: function (month) {
		this._date.setMonth(month);
	},
	getHours: function () {
		return this._date.getHours();
	},
	setHours: function (h) {
		this._date.setHours(h);
	},
	getSeconds: function () {
		return this._date.getSeconds();
	},
	setSeconds: function (s) {
		this._date.setSeconds(s);
	},
	getMilliseconds: function () {
		return this._date.getMilliseconds();
	},
	setMilliseconds: function (m) {
		this._date.setMilliseconds(m);
	},
	getTimezoneOffset: function () {
		return this._date.getTimezoneOffset();
	},
	getRealDate: function () {
		return this._date;
	}
});
zk.fmt.Date = {
	parseDate : function (txt, fmt, strict, refval, localizedSymbols) {
		if (!fmt) fmt = "yyyy/MM/dd";
		refval = refval || zUtl.today(fmt);
		
		localizedSymbols = localizedSymbols || {
			DOW_1ST: zk.DOW_1ST,
				ERA: zk.ERA,    
			 YDELTA: zk.YDELTA,
			   SDOW: zk.SDOW,
			  S2DOW: zk.S2DOW,
			   FDOW: zk.FDOW,
			   SMON: zk.SMON,
			  S2MON: zk.S2MON,
			   FMON: zk.FMON,
				APM: zk.APM
		};
		var y = refval.getFullYear(),
			m = refval.getMonth(),
			d = refval.getDate(), dFound,
			hr = refval.getHours(),
			min = refval.getMinutes(),
			sec = refval.getSeconds(),
			msec = refval.getMilliseconds(),
			aa = fmt.indexOf('a'),
			hasAM = aa > -1,
			hasHour1 = hasAM && (fmt.indexOf('h') > -1 || fmt.indexOf('K') > -1),
			isAM,
			ts = _parseTextToArray(txt, fmt),
			isNumber = !isNaN(txt),
			regexp = /.*\D.*/;

		if (!ts || !ts.length) return;
		for (var i = 0, j = 0, offs = 0, fl = fmt.length; j < fl; ++j) {
			var cc = fmt.charAt(j);
			if ((cc >= 'a' && cc <= 'z') || (cc >= 'A' && cc <= 'Z')) {
				var len = 1, k;
				for (k = j; ++k < fl; ++len)
					if (fmt.charAt(k) != cc)
						break;

				var nosep, nv; //no separator
				if (k < fl) {
					var c2 = fmt.charAt(k);
					nosep = c2 == 'y' || c2 == 'M' || c2 == 'd' || c2 == 'E';
				}
				// ZK-2026: Fix for 'MM.yyyy' issue
				var token = (nosep && isNumber) ? ts[0].substring(j - offs, k - offs) : ts[i++];
				switch (cc) {
				case 'y':
					// ZK-1985:	Determine if token's length is less than the expected when strict is true.
					if (strict && token && (token.length < len))
						return;
					
					if (nosep) {
						if (len <= 3) len = 2;
						if (token && token.length > len) {
							ts[--i] = token.substring(len);
							token = token.substring(0, len);
						}
					}
					
					// ZK-1985:	Determine if token contains non-digital word when strict is true.
					if (strict && token && regexp.test(token))
						return;

					if (!isNaN(nv = _parseInt(token))) {
						y = Math.min(nv, 200000); // Bug B50-3288904: js year limit
						if (y < 100) y += y > 29 ? 1900 : 2000;
					}
					break;
				case 'M':
					var mon = token ? token.toLowerCase() : '',
						isNumber0 = !isNaN(token);
					if (!mon) break; 
					if (!isNumber0 && token) {
						for (var index = localizedSymbols.SMON.length, brkswch; --index >= 0;) {
							var smon = localizedSymbols.SMON[index].toLowerCase();
							if (mon.startsWith(smon)) {
								token = localizedSymbols.SMON[index];
								m = index;
								brkswch = true;
								break; // shall break to switch level: B50-3314513
							}
						}
						if (brkswch)
							break;
					}
					if (len == 3 && token) {
						if (nosep)
							token = _parseToken(token, ts, --i, token.length);//token.length: the length of French month is 4
						if (isNaN(nv = _parseInt(token)))
							return; // failed, B50-3314513
						m = nv - 1;
					} else if (len <= 2) {
						if (nosep && token && token.length > 2) {//Bug 2560497 : if no separator, token must be assigned.
							ts[--i] = token.substring(2);
							token = token.substring(0, 2);
						}
						if (isNaN(nv = _parseInt(token)))
							return; // failed, B50-3314513
						m = nv - 1;
					} else {
						for (var l = 0;; ++l) {
							if (l == 12) return; //failed
							if (len == 3) {
								if (localizedSymbols.SMON[l] == token) {
									m = l;
									break;
								}
							} else {
								if (token && localizedSymbols.FMON[l].startsWith(token)) {
									m = l;
									break;
								}
							}
						}
					}
					if (m > 11 /*|| m < 0 accept 0 since it is a common-yet-acceptable error*/) //restrict since user might input year for month
						return;//failed
					break;
				case 'E':
					if (nosep)
						_parseToken(token, ts, --i, len);
					break;
				case 'd':
					// ZK-1985:	Determine if token's length is less than expected when strict is true.
					if (strict && token && (token.length < len))
						return;
					
					if (nosep)
						token = _parseToken(token, ts, --i, len);
					
					// ZK-1985:	Determine if token contains non-digital word when strict is true.
					if (strict && token && regexp.test(token))
						return;
					
					if (!isNaN(nv = _parseInt(token))) {
						d = nv;
						dFound = true;
						if (d < 0 || d > 31) //restrict since user might input year for day (ok to allow 0 and 31 for easy entry)
							return; //failed
					}
					break;
				case 'H':
				case 'h':
				case 'K':
				case 'k':
					// ZK-1985:	Determine if token's length is less than the expected when strict is true.
					if (strict && token && (token.length < len))
						return;
					
					if (hasHour1 ? (cc == 'H' || cc == 'k'): (cc == 'h' || cc == 'K'))
						break;
					if (nosep)
						token = _parseToken(token, ts, --i, len);
					
					// ZK-1985:	Determine if token contains non-digital word when strict is true.
					if (strict && token && regexp.test(token))
						return;
					
					if (!isNaN(nv = _parseInt(token)))
						hr = (cc == 'h' && nv == 12) || (cc == 'k' && nv == 24) ? 
							0 : cc == 'K' ? nv % 12 : nv;
					break;
				case 'm':
				case 's':
				case 'S':
					// ZK-1985:	Determine if token's length is less than the expected when strict is true.
					if (strict && token && (token.length < len))
						return;
					
					if (nosep)
						token = _parseToken(token, ts, --i, len);
					
					// ZK-1985:	Determine if token contains non-digital word when strict is true.
					if (strict && token && regexp.test(token))
						return;
					
					if (!isNaN(nv = _parseInt(token))) {
						if (cc == 'm') min = nv;
						else if (cc == 's') sec = nv;
						else msec = nv;
					}
					break;
				case 'a':
					if (!hasHour1)
						break;
					if (!token) return; //failed
					isAM = token.startsWith(localizedSymbols.APM[0]);
					break
				//default: ignored
				}
				j = k - 1;
			} else offs++;
		}

		if (hasHour1 && isAM === false)
			hr += 12;
		var dt;
		if (m == 1 && d == 29 && localizedSymbols.YDELTA) {
			dt = new LeapDay(y - localizedSymbols.YDELTA, m, d, hr, min, sec, msec);
			dt.setOffset(localizedSymbols.YDELTA);
		} else {
			dt = new Date(y, m, d, hr, min, sec, msec);
		}
		if (!dFound && dt.getMonth() != m)
			dt = new Date(y, m + 1, 0, hr, min, sec, msec); //last date of m
		if (strict) {
			if (dt.getFullYear() != y || dt.getMonth() != m || dt.getDate() != d ||
				dt.getHours() != hr || dt.getMinutes() != min || dt.getSeconds() != sec) //ignore msec (safer though not accurate)
				return; //failed

			txt = txt.trim();
			txt = _ckDate(localizedSymbols.FDOW, txt);
			txt = _ckDate(localizedSymbols.SDOW, txt);
			txt = _ckDate(localizedSymbols.S2DOW, txt);
			txt = _ckDate(localizedSymbols.FMON, txt);
			txt = _ckDate(localizedSymbols.SMON, txt);
			txt = _ckDate(localizedSymbols.S2MON, txt);
			txt = _ckDate(localizedSymbols.APM, txt);
			for (var j = txt.length; j--;) {
				var cc = txt.charAt(j);
				if ((cc > '9' || cc < '0') && fmt.indexOf(cc) < 0)
					return; //failed
			}
		}
		return +dt == +refval ? refval: dt;
			//we have to use getTime() since dt == refVal always false
	},
	formatDate : function (val, fmt, localizedSymbols) {
		if (!fmt) fmt = "yyyy/MM/dd";

		localizedSymbols = localizedSymbols || {
			DOW_1ST: zk.DOW_1ST,
				ERA: zk.ERA,    
			 YDELTA: zk.YDELTA,
			   SDOW: zk.SDOW,
			  S2DOW: zk.S2DOW,
			   FDOW: zk.FDOW,
			   SMON: zk.SMON,
			  S2MON: zk.S2MON,
			   FMON: zk.FMON,
				APM: zk.APM
		};
		var txt = "";
		for (var j = 0, fl = fmt.length; j < fl; ++j) {
			var cc = fmt.charAt(j);
			if ((cc >= 'a' && cc <= 'z') || (cc >= 'A' && cc <= 'Z')) {
				var len = 1, k;
				for (k = j; ++k < fl; ++len)
					if (fmt.charAt(k) != cc)
						break;

				switch (cc) {
				case 'y':
					if (len <= 3) txt += _digitFixed(val.getFullYear() % 100, 2);
					else txt += _digitFixed(val.getFullYear(), len);
					break;
				case 'M':
					if (len <= 2) txt += _digitFixed(val.getMonth()+1, len);
					else if (len == 3) txt += localizedSymbols.SMON[val.getMonth()];
					else txt += localizedSymbols.FMON[val.getMonth()];
					break;
				case 'd':
					txt += _digitFixed(dayInMonth(val), len);
					break;
				case 'E':
					if (len <= 3) txt += localizedSymbols.SDOW[(val.getDay() - localizedSymbols.DOW_1ST + 7) % 7];
					else txt += localizedSymbols.FDOW[(val.getDay() - localizedSymbols.DOW_1ST + 7) % 7];
					break;
				case 'D':
					txt += dayInYear(val);
					break;
				case 'w':
					txt += weekInYear(val);
					break;
				case 'W':
					txt += weekInMonth(val);
					break;
				case 'G':
					txt += localizedSymbols.ERA;
					break;
				case 'F':
					txt += dayOfWeekInMonth(val);
					break;
				case 'H':
					if (len <= 2) txt += _digitFixed(val.getHours(), len);
					break;
				case 'k':
					var h = val.getHours();
					if (h == 0)
						h = '24';
					if (len <= 2) txt += _digitFixed(h, len);
					break;
				case 'K':
					if (len <= 2) txt += _digitFixed(val.getHours() % 12, len);
					break;
				case 'h':
					var h = val.getHours();
					h %= 12;
					if (h == 0)
						h = '12';
					if (len <= 2) txt += _digitFixed(h, len);
					break;
				case 'm':
					if (len <= 2) txt += _digitFixed(val.getMinutes(), len);
					break;
				case 's':
					if (len <= 2) txt += _digitFixed(val.getSeconds(), len);
					break;
				case 'S':
					if (len <= 3) txt += _digitFixed(val.getMilliseconds(), len);
					break;
				case 'Z':
					txt += -(val.getTimezoneOffset()/60);
					break;
				case 'a':
					txt += localizedSymbols.APM[val.getHours() > 11 ? 1 : 0];
					break;
				default:
					txt += '1';
					//fake; SimpleDateFormat.parse might ignore it
					//However, it must be an error if we don't generate a digit
				}
				j = k - 1;
			} else if (cc != "'"){
				txt += cc;
			}
		}
		return txt;
	}
};
/**
 * The <code>calendar</code> object provides a way
 * to convert between a specific instant in time for locale-sensitive
 * like buddhist's time.
 * <p>By default the year offset is specified from server if any.</p>
 * @since 5.0.1
 */
zk.fmt.Calendar = zk.$extends(zk.Object, {
	_offset: zk.YDELTA,
	$init: function (date, localizedSymbols) {
		this._date = date;
		if (localizedSymbols)
			this._offset = localizedSymbols.YDELTA;
	},
	getTime: function () {
		return this._date;
	},
	setTime: function (date) {
		this._date = date;
	},
	setYearOffset: function (val) {
		this._offset = val;
	},
	getYearOffset: function () {
		return this._offset;
	},
	formatDate: function (val, fmt, localizedSymbols) {
		var d;
		if (localizedSymbols)
			this._offset = localizedSymbols.YDELTA;
			
		if (this._offset) {
			if (val.getMonth() == 1 && val.getDate() == 29) {
				d = new LeapDay(val); // a proxy of Date
				d.setOffset(this._offset);
			} else {
				d = new Date(val);
				d.setFullYear(d.getFullYear() + this._offset);
			}
		}
		return zk.fmt.Date.formatDate(d || val, fmt, localizedSymbols);
	},
    toUTCDate: function () {
    	if (LeapDay.isInstance(this._date))
    		return this._date.getRealDate();
        var d;
        if ((d = this._date) && this._offset)
            (d = new Date(d))
                .setFullYear(d.getFullYear() - this._offset);
        return d;
    }, 
	parseDate: function (txt, fmt, strict, refval, localizedSymbols) {
		var d = zk.fmt.Date.parseDate(txt, fmt, strict, refval, localizedSymbols);
		if (localizedSymbols)
			this._offset = localizedSymbols.YDELTA;
			
		if (this._offset && fmt) {
			if (!LeapDay.isInstance(d)) {
				var cnt = 0;
				for (var i = fmt.length; i--;)
					if (fmt.charAt(i) == 'y')
						cnt++;
				if (cnt > 3)
					d.setFullYear(d.getFullYear() - this._offset);
				else if (cnt) {
					var year = d.getFullYear();
					if (year < 2000)
						d.setFullYear(year + (Math.ceil(this._offset / 100) * 100 - this._offset));
					else
						d.setFullYear(year - (this._offset % 100));
				}
			} else {
				return d.getRealDate();
			}
		}
		return d;
	},
	getYear: function () {
		return LeapDay.isInstance(this._date) ? this._date.getFullYear() : 
			this._date.getFullYear() + this._offset;
	}
});
})();
