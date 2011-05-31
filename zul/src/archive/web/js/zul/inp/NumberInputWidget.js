/* NumberInputWidget.js

	Purpose:
		
	Description:
		
	History:
		Fri May 27 16:12:42 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var _allowKeys,
		globallocalizedSymbols = {};
/**
 * A skeletal implementation for number-type input box.
 * @since 5.0.8
 */
zul.inp.NumberInputWidget = zk.$extends(zul.inp.FormatWidget, {
	$define: { //zk.def
		/** Returns the rounding mode.
		 * <ul>
		 * <li>0: ROUND_UP</li>
		 * <li>1: ROUND_DOWN</li>
		 * <li>2: ROUDN_CEILING</li>
		 * <li>3: ROUND_FLOOR</li>
		 * <li>4: ROUND_HALF_UP</li>
		 * <li>5: ROUND_HALF_DOWN</li>
		 * <li>6: ROUND_HALF_EVEN</li>
		 * </ul>
		 * @return int
		 */
		/** Sets the rounding mode.
		 * <ul>
		 * <li>0: ROUND_UP</li>
		 * <li>1: ROUND_DOWN</li>
		 * <li>2: ROUDN_CEILING</li>
		 * <li>3: ROUND_FLOOR</li>
		 * <li>4: ROUND_HALF_UP</li>
		 * <li>5: ROUND_HALF_DOWN</li>
		 * <li>6: ROUND_HALF_EVEN</li>
		 * </ul>
		 * @param int rounding mode
		 */
		rounding: null,
		localizedSymbols: [
			function (val) {
				if(val) {
					var ary = jq.evalJSON(val);
					if (!globallocalizedSymbols[ary[0]])
						globallocalizedSymbols[ary[0]] = ary[1];
					return globallocalizedSymbols[ary[0]];
				} 
				return val;
			},
			function () {
				if (this._localizedSymbols) {
					this._allowKeys = "0123456789"+this._localizedSymbols.MINUS+this._localizedSymbols.PERCENT +
						 			(zk.groupingDenied ? '': this._localizedSymbols.GROUPING);
				} else {
					this._allowKeys = null;
				}
				this.rerender();
			}
		]
	},
	doKeyPress_: function(evt){
		if (!this._shallIgnore(evt, this._allowKeys || _allowKeys))
			this.$supers('doKeyPress_', arguments);
	}
});
	// Fixed merging JS issue
	zk.load('zul.lang', function () {
		_allowKeys = zul.inp.NumberInputWidget._allowKeys = "0123456789"+zk.MINUS+zk.PERCENT
			+(zk.groupingDenied ? '': zk.GROUPING);
	});
})();