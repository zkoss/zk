/* numfmt.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 19:13:43     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function down(valStr, ri) {
		return valStr.substring(0, ri);
	}
	function up(valStr, ri) {
		var k = 1, val = '';
		for (var j = ri; k && --j >= 0;) {
			var ch = valStr.charAt(j);
			if (k == 1) {
				if (ch >= '0' && ch < '9') {
					ch = ch.$inc(1);
					k = 0;
				} else if (ch == '9')
					ch = '0';
			}
			val = ch + val;
		}
		if (j >= 0)
			val = valStr.substring(0, j) + val;
		return k ? '1'+val : val;
	}
	function compareHalf(valStr, ri) {
		var ch,
			result,
			base = '5';
		for (var j = ri, len = valStr.length; j < len; ++j) {
			result = valStr.charAt(j) - base;
			if (j == ri) { //first digit
				base = '0';
			}
			if (result != 0) 
				return result;
		}
		return result;
	}
	function preDigit(valStr, ri) {
		for (var j = ri; --j >= 0;) {
			var ch = valStr.charAt(j);
			if (ch >= '0' && ch <= '9')
				return ch;
		}
		return null;
	}
zk.fmt.Number = {
	setScale: function (val, scale, rounding) { //bug #3089502: setScale in decimalbox not working
		if (scale === undefined || scale < 0)
			return val;
		var valStr = val.$toString(),
			indVal = valStr.indexOf('.'),
			valFixed = indVal >= 0 ? valStr.length - indVal - 1 : 0;
		if (valFixed <= scale) // no need to do any thing
			return val;
		else {
			var ri = indVal + scale + 1;
			valStr = this.rounding(valStr, ri, rounding, valStr < 0);
			return new zk.BigDecimal(valStr);
		}
	},
	rounding: function (valStr, ri, rounding, minus) {
		switch(rounding) {
			case 0: //UP
				valStr = up(valStr, ri);
				break;
			case 1: //DOWN
				valStr = down(valStr, ri);
				break;
			case 2: //CELING
				valStr = minus ? down(valStr, ri) : up(valStr, ri);
				break;
			case 3: //FLOOR
				valStr = !minus ? down(valStr, ri) : up(valStr, ri);
				break;
			case 4: //HALF_UP
				var r = compareHalf(valStr, ri);
				valStr = r < 0 ? down(valStr, ri) : up(valStr, ri);
				break;
			case 5: //HALF_DOWN
				var r = compareHalf(valStr, ri);
				valStr = r > 0 ? up(valStr, ri) : down(valStr, ri);
				break;
			case 6: //HALF_EVEN
				//falling down
			default:
				var r = compareHalf(valStr, ri);
				if (r == 0) { //half
					var evenChar = preDigit(valStr, ri);
					valStr = (evenChar & 1) ? up(valStr, ri) : down(valStr, ri);
				} else
					valStr = r < 0 ? down(valStr, ri) : up(valStr, ri);
		}
		return valStr;
	},
	format: function (fmt, val, rounding) {
		if (val == null) return '';
		if (!fmt) return '' + val;
		
		var useMinsuFmt;
		if (fmt.indexOf(';') != -1) {
			fmt = fmt.split(';');
			useMinsuFmt = val < 0;
			fmt = fmt[useMinsuFmt ? 1 : 0];
		}
		
		//calculate number of fixed decimals
		var efmt = this._escapeQuote(fmt);
		fmt = efmt.fmt;
		var pureFmtStr = efmt.pureFmtStr,
			ind = efmt.purejdot,
			fixed = ind >= 0 ? pureFmtStr.length - ind - 1 : 0,
			valStr = (val + '').replace(/[^0123456789.]/g, ''),
			indVal = valStr.indexOf('.'),
			valFixed = indVal >= 0 ? valStr.length - indVal - 1 : 0,
			shift = efmt.shift;
			
		if (shift > 0) {
			if (indVal >= 0) { //with dot
				if (valFixed > shift) {
					valStr = valStr.substring(0, indVal) + valStr.substring(indVal+1, indVal+1+shift) + '.' + valStr.substring(indVal+1+shift);
					valFixed -= shift;
					indVal += shift;
				} else {
					valStr = valStr.substring(0, indVal) + valStr.substring(indVal+1);
					for(var len = shift - valFixed; len-- > 0;)
						valStr = valStr + '0';
					indVal = -1;
					valFixed = 0;
				}
			} else { //without dot
				for(var len = shift; len-- > 0;)
					valStr = valStr + '0';
			}
		}
			
		//fix value subpart
		if (valFixed <= fixed) {
			if (indVal == -1)
				valStr += '.';
			for(var len = fixed - valFixed; len-- > 0;)
				valStr = valStr + '0';
		} else { //preprocess for rounding
			var ri = indVal + fixed + 1;
			valStr = this.rounding(valStr, ri, rounding, val < 0);
		}
		var indFmt = efmt.jdot,
			pre = '', suf = '';
		
		//pre part
		indVal = valStr.indexOf('.');
		if (indVal == -1) 
			indVal = valStr.length;
		if (indFmt == -1) 
			indFmt = fmt.length;
		if (ind == -1)
			ind = pureFmtStr.length;
		
		// Bug 2911379
		var prefmt = indVal - ind;
		if (prefmt > 0) {
			var xfmt = '';
			for (var len = prefmt; --len >= 0; indFmt++)
				xfmt += '#';
		
			// insert extra format into correct place.
    		var beg = this._extraFmtIndex(fmt);
    		prefmt += beg;
    		fmt = fmt.substring(0, beg) + xfmt + fmt.substring(beg, fmt.length);
		}
		for (var len = ind - indVal; --len >= 0; indVal++)
			valStr = '0' + valStr;
		
		var groupDigit = indFmt - fmt.substring(0, indFmt).lastIndexOf(',');
			
		for (var g = 1, i = indFmt - 1, j = indVal - 1; i >= 0 && j >= 0;) {
			if (g % groupDigit == 0 && pre.charAt(0) != ',') {
				pre = zk.GROUPING + pre;
				g++;
			}
			var fmtcc = fmt.charAt(i); 
			if (fmtcc == '#' || fmtcc == '0') {
				var cc = valStr.charAt(j);
				pre = (cc == '0' ? fmtcc : cc) + pre;
				i--;
				j--;
				g++;
			} else {
				var c = fmt.charAt(i);
				if (c != ',') {
					pre = c + pre;
					g++;
				}
				i--;
			}
		}
		if (j >= 0) 
			pre = valStr.substr(0, j + 1) + pre;
		
		// Bug #2926718
		var len = (indFmt < 0 ? fmt.length : indFmt) - (ind < 0 ? pureFmtStr.length : ind),
			prej = efmt.prej;
		if (len > 0) {
			var p = fmt.substring(prej, prefmt > 0 ? prefmt : len).replace(new RegExp("[#0.,]", 'g'), '');
			if (p)
				pre = p + pre;
		}
		//sufpart
		for (var i = indFmt + 1, j = indVal + 1, fl = fmt.length, vl = valStr.length; i < fl; i++) {
			var fmtcc = fmt.charAt(i); 
			if (fmtcc == '#' || fmtcc == '0') {
				if (j < vl) {
					suf += valStr.charAt(j);
					j++;
				}
			} else
				suf += fmtcc == '%' ? zk.PERCENT : fmtcc;
		}
		if (j < valStr.length) 
			suf = valStr.substr(j, valStr.length);
		
		//remove optional '0' digit in sufpart
		var e = -1;
		for (var m = suf.length, n = fmt.length; m > 0; --m) {
			var cc = suf.charAt(m-1),
				fmtcc = fmt.charAt(--n);
			if (cc == '0' &&  fmtcc == '#') { //optional 0
				if (e < 0) e = m;
			} else if (e >= 0 || /[1-9]/.test(cc))
				break;
		}
		if (e >= 0)
			suf = suf.substring(0, m) + suf.substring(e);
		
		//combine
		if (pre)
			pre = fmt.substring(0, prej) + this._removePrefixSharps(pre);
		if (!pre && fmt.charAt(indFmt+1) == '#')
			pre = '0';
		return (val < 0 && !useMinsuFmt? zk.MINUS : '') + (suf ? pre + (/[\d]/.test(suf.charAt(0)) ? zk.DECIMAL : '') + suf : pre);
	},
	_escapeQuote: function (fmt) {
		//note we do NOT support mixing of quoted and unquoted percent
		var cc, q = -2, shift = 0, ret = '', jdot = -1, purejdot = -1, pure = '', prej= -1,
			validPercent = fmt ? !new RegExp('\(\'['+zk.PERCENT+'|'+zk.PER_MILL+']+\'\)', 'g').test(fmt) : true; 
			//note we do NOT support mixing of quoted and unquoted percent|permill
		for (var j = 0, len = fmt.length; j < len; ++j) {
			cc = fmt.charAt(j);
			if (cc == '%' && validPercent)
				shift += 2;
			else if (cc == zk.PER_MILL && validPercent)
				shift += 3;
			
			if (cc == '\'') { // a single quote
				if (q >= 0) {//close single quote
					ret += q == (j-1) ? '\'' : fmt.substring(q+1, j);
					q = -2;
				} else
					q = j; //open single quote
			} else if (q < 0) { //not in quote
				if (prej < 0 
					&& (cc == '#' || cc == '0' || cc == '.' || cc == '-' || cc == ',' || cc == 'E'))
					prej = ret.length;
					
				if (cc == '#' || cc == '0')
					pure += cc;
				else if(cc == '.') {
					if (purejdot < 0) 
						purejdot = pure.length;
					if (jdot < 0) 
						jdot = ret.length;
					pure += cc;
				}
				ret += cc;
			}
		}
		return {shift:shift, fmt:ret, pureFmtStr: pure, jdot:jdot, purejdot:purejdot, prej:prej};
	},
	_extraFmtIndex: function (fmt) {
		var j = 0;
		for(var len=fmt.length; j < len; ++j) {
			var c = fmt.charAt(j);
			if (c == '#' || c == '0' || c == ',')
				break;
		}
		return j;
	},	
	_removePrefixSharps: function (val) {
		var ret = '',
			sharp = true;
		for(var len = val.length, j=0; j < len; ++j) {
			var cc = val.charAt(j);
			if (sharp) {
				if (cc == '#' || cc == zk.GROUPING) continue;
				else if (/[\d]/.test(cc)) sharp = false; // Bug 2990659
			}
			ret = ret + (cc == '#' ? '0' : cc);
		}
		return ret;
	},
	unformat: function (fmt, val) {
		if (!val) return {raw: val, divscale: 0};

		var divscale = 0, //the second element
			minus, sb, cc, ignore,
			zkMinus = fmt ? zk.MINUS : '-',
			zkDecimal = fmt ? zk.DECIMAL : '.', //bug #2932443, no format and German Locale
			validPercent = fmt ? !new RegExp('\(\'['+zk.PERCENT+'|'+zk.PER_MILL+']+\'\)', 'g').test(fmt) : true; 
				//note we do NOT support mixing of quoted and unquoted percent|permill
		for (var j = 0, len = val.length; j < len; ++j) {
			cc = val.charAt(j);
			ignore = true;

			//We handle percent and (nnn) specially
			if (cc == zk.PERCENT && validPercent) divscale += 2;
			else if (cc == zk.PER_MILL && validPercent) divscale += 3;
			else if (cc == '(') minus = true;
			else if (cc != '+') ignore = false;

			//We don't add if cc shall be ignored (not alphanum but in fmt)
			if (!ignore)
				ignore = (cc < '0' || cc > '9')
				&& cc != zkDecimal && cc != zkMinus && cc != '+'
				&& (zUtl.isChar(cc,{whitespace:1}) || cc == zk.GROUPING
					|| cc == ')' || (fmt && fmt.indexOf(cc) >= 0));
			if (ignore) {
				if (sb == null) sb = val.substring(0, j);
			} else {
				var c2 = cc == zkMinus ? '-':
					cc == zkDecimal ? '.':  cc;
				if (cc != c2 && sb == null)
					sb = val.substring(0, j);
				if (sb != null) sb += c2;
			}
		}
		if (sb == null) sb = val;
		if (minus) sb = '-' + sb;
		for (;;) {
			cc = sb.charAt(0);
			if (cc == '+')
				sb = sb.substring(1);
			else if (cc == '-' && sb.charAt(1) == '-')
				sb = sb.substring(2);
			else
				break;
		}

		//remove leading 0
		//keep the zero after the decimal point (to preserve precision)
		for (var j = 0, k, len = sb.length; j < len; ++j) {
			cc = sb.charAt(j);
			if (cc > '0' && cc <= '9') {
				if (k !== undefined)
					sb = sb.substring(0, k) + sb.substring(j);
				break; //done
			} else if (cc == '0') {
				if (k === undefined)
					k = j;
			} else if (k !== undefined) {
				if (cc == zkDecimal && j > ++k)
					sb = sb.substring(0, k) + sb.substring(j);
				break;
			} else if (cc == zkDecimal) { //.xxx or .
				break;
			}
		}
		return {raw: sb, divscale: divscale};
	}
};
})();
