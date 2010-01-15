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
zk.fmt.Number = {
	format: function (fmt, val, rounding) {
		if (val == null) return '';
		if (!fmt) return '' + val;
		
		var isMINUS;
		if (fmt.indexOf(';') != -1) {
			fmt = fmt.split(';');
			isMINUS = val < 0;
			fmt = fmt[isMINUS ? 1 : 0];
		}
    	
		//calculate number of fixed decimals
		var re = new RegExp("[^#0.]", 'g'),
			pureFmtStr = fmt.replace(re, ''),
			ind = pureFmtStr.indexOf('.'),
			fixed = ind >= 0 ? pureFmtStr.length - ind - 1 : 0,
			valStr = (val + '').replace(/[^0123456789.]/g, ''),
			indVal = valStr.indexOf('.'),
			valFixed = indVal >= 0 ? valStr.length - indVal - 1 : 0;
			
		//fix value subpart
		if (valFixed <= fixed) {
			if (indVal == -1)
				valStr += '.';
			for(var len = fixed - valFixed; len-- > 0;)
				valStr = valStr + '0'; 
		} else { //preprocess for rounding
			var ri = indVal + fixed + 1;
			switch(rounding) {
				case 0: //UP
					valStr = up(valStr, ri);
					break;
				case 1: //DOWN
					valStr = down(valStr, ri);
					break;
				case 2: //CELING
					valStr = val < 0 ? down(valStr, ri) : up(valStr, ri);
					break;
				case 3: //FLOOR
					valStr = val >= 0 ? down(valStr, ri) : up(valStr, ri);
					break;
				case 4: //HALF_UP
					var h = Math.pow(10, valFixed - fixed) * 5,
						r = valStr.substring(indVal + fixed + 1) | 0;
					valStr = r < h ? down(valStr, ri) : up(valStr, ri);
					break;
				case 5: //HALF_DOWN
					var h = Math.pow(10, valFixed - fixed) * 5,
						r = valStr.substring(indVal + fixed + 1) | 0;
					valStr = r <= h ? down(valStr, ri) : up(valStr, ri);
					break;
				case 6: //HALF_EVEN
					//falling down
				default:
					var h = Math.pow(10, valFixed - fixed - 1) * 5,
						r = valStr.substring(indVal + fixed + 1) | 0;
					if (r == h) { //half
						var evenChar = valStr.charAt(fixed == 0 ? indVal - 1 : indVal + fixed);
						valStr = (evenChar & 1) ? up(valStr, ri) : down(valStr, ri);
					} else
						valStr = r < h ? down(valStr, ri) : up(valStr, ri);
			}
		}
		var indFmt = fmt.indexOf('.'),
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
		var _fmt = '',
			prefmt = indVal - ind;
		for (var len = prefmt; --len >= 0; indFmt++)
			_fmt += '#';
		
		// insert extra format into correct place.
		if (_fmt) {
    		var beg = fmt.indexOf('#');
    		prefmt += beg;
    		fmt = fmt.substring(0, beg) + _fmt + fmt.substring(beg, fmt.length);
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
		var len = fmt.length - pureFmtStr.length;
		if (len > 0) {
			var p = fmt.substring(0, prefmt > 0 ? prefmt : len).replace(new RegExp("[#0.]", 'g'), '');
			if (p)
				pre = p + pre;
		}
		//sufpart
		for (var i = indFmt + 1, j = indVal + 1, fl = fmt.length, vl = valStr.length; i < fl && j < vl; i++) {
			var fmtcc = fmt.charAt(i); 
			if (fmtcc == '#' || fmtcc == '0') {
				suf += valStr.charAt(j);
				j++;
			} else
				suf += fmt.charAt(i);
		}
		if (j < valStr.length) 
			suf = valStr.substr(j, valStr.length);
		
		if (j < fmt.length && fmt.charAt(fmt.length - 1) == '%') 
			suf += zk.PERCENT;
		
		//remove optional '0' digit in sufpart
		for (var m = suf.length, n = fmt.length; m > 0; --m) {
			if (suf.charAt(m-1) != '0' || fmt.charAt(--n) != '#') {
				break;
			}
		}
		suf = suf.substring(0, m);
		
		//combine
		if (pre)
			pre = this._removePrefixSharps(pre);
		if (!pre && fmt.charAt(indFmt+1) == '#')
			pre = '0';
		return (val < 0 && !isMINUS? zk.MINUS : '') + (suf ? pre + zk.DECIMAL + suf : pre);
	},
	_removePrefixSharps: function (val) {
		var ret = '',
			sharp = true;
		for(var len = val.length, j=0; j < len; ++j) {
			var cc = val.charAt(j);
			if (sharp) {
				if (cc == '#' || cc == zk.GROUPING) continue;
				else sharp = false;
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
			zkDecimal = fmt ? zk.DECIMAL : '.'; //bug #2932443, no format and German Locale
		for (var j = 0, len = val.length; j < len; ++j) {
			cc = val.charAt(j);
			ignore = true;

			//We handle percent and (nnn) specially
			if (cc == zk.PERCENT) divscale += 2;
			else if (cc == zk.PER_MILL) divscale += 3;
			else if (cc == '(') minus = true;
			else if (cc != '+') ignore = false;

			//We don't add if cc shall be ignored (not alphanum but in fmt)
			if (!ignore)
				ignore = (cc < '0' || cc > '9')
				&& cc != zkDecimal && cc != zkMinus && cc != '+'
				&& (zUtl.isChar(cc,{whitespace:1}) || cc == zk.GROUPING || cc == ')'
					|| (fmt && fmt.indexOf(cc) >= 0));
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
		return {raw: sb, divscale: divscale};
	}
};
})();
