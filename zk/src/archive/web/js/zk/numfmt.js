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
				switch(ch) {
				case '0': val = '1' + val; k = 0; break;
				case '1': val = '2' + val; k = 0; break;
				case '2': val = '3' + val; k = 0; break;
				case '3': val = '4' + val; k = 0; break;
				case '4': val = '5' + val; k = 0; break;
				case '5': val = '6' + val; k = 0; break;
				case '6': val = '7' + val; k = 0; break;
				case '7': val = '8' + val; k = 0; break;
				case '8': val = '9' + val; k = 0; break;
				case '9': val = '0' + val; break;
				default: val = ch + val;
				}
			} else
				val = ch + val;
		}
		if (j >= 0)
			val = valStr.substring(0, j) + val;
		return k ? '1'+val : val;
	}
zNumFormat = {
	format: function (fmt, val, rounding) {
		if (!val) return '';
		if (!fmt) return '' + val;
		
		//calculate number of fixed decimals
		var re = new RegExp("[^#0" + zk.DECIMAL + "]", 'g'),
			pureFmtStr = fmt.replace(re, ''),
			ind = pureFmtStr.indexOf(zk.DECIMAL),
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
		var indFmt = fmt.indexOf(zk.DECIMAL),
			pre = suf = '';
		
		//pre part
		indVal = valStr.indexOf('.');
		if (indVal == -1) 
			indVal = valStr.length;
		if (indFmt == -1) 
			indFmt = fmt.length;
		if (ind == -1)
			ind = pureFmtStr.length;
		
		for (var len = indVal - ind; --len >= 0; indFmt++)
			fmt = '#' + fmt;
		
		var groupDigit = indFmt - fmt.substring(0, indFmt).lastIndexOf(zk.GROUPING);
			
		for (var g = 1, i = indFmt - 1, j = indVal - 1; i >= 0 && j >= 0;) {
			if (g % groupDigit == 0 && pre.charAt(0) != zk.GROUPING) {
				pre = zk.GROUPING + pre;
				g++;
			}
			if (fmt.charAt(i) == '#') {
				pre = valStr.charAt(j) + pre;
				i--;
				j--;
				g++;
			} else {
				var c = fmt.charAt(i);
				if (c != zk.GROUPING) {
					pre = c + pre;
					g++;
				}
				i--;
			}
		}
		if (j >= 0) 
			pre = valStr.substr(0, j + 1) + pre;
		
		//sufpart
		for (var i = indFmt + 1, j = indVal + 1, fl = fmt.length, vl = valStr.length; i < fl && j < vl; i++) {
			if (fmt.charAt(i) == '#') {
				suf += valStr.charAt(j);
				j++;
			} else
				suf += fmt.charAt(i);
		}
		if (j < valStr.length) 
			suf = valStr.substr(j, valStr.length);
		
		//remove optional '0' digit in sufpart
		for (var m = suf.length, n = fmt.length; m > 0; --m) {
			if (suf.charAt(m-1) != '0' || fmt.charAt(--n) != '#') {
				break;
			}
		}
		suf = suf.substring(0, m);
		
		//combine
		if (!pre) 
			pre = "0";
		return (val < 0 ? zk.MINUS : '') + (suf ? pre + zk.DECIMAL + suf : pre);
	},
	unformat: function (fmt, val) {
		if (!val) return {raw: val, divscale: 0};

		var divscale = 0, //the second element
			minus, sb, cc, ignore;
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
				&& cc != zk.DECIMAL && cc != zk.MINUS && cc != '+'
				&& (zUtl.isChar(cc,{whitespace:1}) || cc == zk.GROUPING || cc == ')'
					|| (fmt && fmt.indexOf(cc) >= 0));
			if (ignore) {
				if (!sb) sb = val.substring(0, j);
			} else {
				var c2 = cc == zk.MINUS ? '-':
					cc == zk.DECIMAL ? '.':  cc;
				if (cc != c2 && !sb)
					sb = val.substring(0, j);
				if (sb) sb += c2;
			}
		}
		if (minus) {
			if (!sb) sb = val;
			if (sb.length)
				if (sb.charAt(0) == '-') sb = sb.substring(1); //-- => +
				else sb = '-' + sb;
		}
		return {raw: sb || val, divscale: divscale};
	}
};
})();
