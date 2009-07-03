/* Decimalbox.js

	Purpose:
		
	Description:
		
	History:
		Fri June 11 13:35:32     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Decimalbox = zk.$extends(zul.inp.Doublebox, {
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zNumFormat.unformat(this._format, value),
			val = parseFloat(info.raw);
		if (info.raw != ''+val && info.raw.indexOf('e') < 0) //unable to handle 1e2
			return {error: zMsgFormat.format(msgzul.NUMBER_REQUIRED, value)};

		if (info.divscale) val = Math.round(val / Math.pow(10, info.divscale));
		return val;
	},
	coerceToString_: function (value) {
		if (!value) return '';
		//caculate number of fixed decimals
		var fmtStr=this.getFormat()+"",
			pureFmtStr = fmtStr.replace(/[^#.]/g,'');
		var ind = pureFmtStr.indexOf('.');
		if(ind > 0)
			var fixed = pureFmtStr.length - ind -1;
		else
			var fixed = 0;
		var valueStr = value+"";
		valueStr = (valueStr.replace(/[^0123456789.]/g,'')*1).toFixed(fixed);
		
		var indFmt = fmtStr.indexOf('.'),
			indVal = valueStr.indexOf('.'),
			pre=suf='';
		
		//pre part
		if(indVal == -1)
			indVal = valueStr.length;
		for(var i = indFmt-1, j =indVal-1; i>=0 && j>=0;){
			if(fmtStr.charAt(i)=='#'){
				pre = valueStr.charAt(j) + pre;
				i--;
				j--;
			}else{
				pre = fmtStr.charAt(i) + pre;
				i--;
			}
		}
		if(j>=0)
			pre = valueStr.substr(0,j+1) + pre;
		
		//sufpart
		for(var i = indFmt+1, j =indVal+1; i<fmtStr.length && j<valueStr.length;){
			if(fmtStr.charAt(i)=='#'){
				suf += valueStr.charAt(j);
				i++;
				j++;
			}else{
				suf += fmtStr.charAt(i);
				i++;
			}
		}
		if(j<valueStr.length)
			suf = valueStr.substr(j,valueStr.length);
			
		//combine
		if(pre == "")
			pre = "0";
			
		if(suf=="")
			var finalStr = pre;
		else
			var finalStr = pre+"."+suf;
			
		return finalStr;
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-decimalbox";
	},
	doKeyPress_: function(evt){
		if (!this._shallIgnore(evt, zul.inp.Doublebox._allowKeys))
			this.$supers('doKeyPress_', arguments);
	}
});