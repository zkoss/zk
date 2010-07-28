/* math.js

	Purpose:
		
	Description:
		
	History:
		Sun Dec 14 17:16:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** A big decimal.
 * @disable(zkgwt)
 */
zk.BigDecimal = zk.$extends(zk.Object, {
	_precision: 0,
	$define: {
		/** Returns the precision.
		 * <p>Default: 0
		 * @return int
		 */
		/** Sets the precision
		 * @param int precision the precision
		 */
		precision: null
	},
	/** Constructor.
	 * @param Object value a number or a string
	 */
	$init: function (value) {
		value = value ? '' + value: '0';
		var jdot = -1;
		for (var j = 0, len = value.length; j < len; ++j) {
			var cc = value.charAt(j);
			if ((cc < '0' || cc > '9') && cc != '-' && cc != '+')
				if (jdot < 0 && cc == '.') {
					jdot = j;
				} else {
					value = value.substring(0, j);
					break;
				}
		}
		if (jdot >= 0) {
			value = value.substring(0, jdot) + value.substring(jdot + 1);
			this._precision = value.length - jdot;
			this._dot = true;
		}
		this._value = value;
	},
	/** Returns a string for this big decimal (per the original form).
	 * To have a Locale-dependent string, use {@link #$toLocaleString}
	 * instead.
	 * @return String
	 */
	$toString: function() { //toString is reserved keyword for IE
		if (this._value.length == 0) return ''; 
		var j = this._value.length - this._precision,
			valFixed = '';
		if (j < 0)
			for(var len = -j; len-- > 0;)
				valFixed += '0';
		return this._value.substring(0, j) + (this._dot || this._precision ? '.' + valFixed + this._value.substring(j) : '');
	},
	/** Returns a Locale-dependent string for this big decimal(for human's eye).
	 * @return String
	 */
	$toLocaleString: function() { //toLocaleString is reserved keyword for IE
		if (this._value.length == 0) return ''; 
		var j = this._value.length - this._precision;
		if (j <= 0) {
			var valFixed = '';
			for(var len = -j; len-- > 0;)
				valFixed += '0';
			return '0' + (this._precision ? zk.DECIMAL + valFixed + this._value : '');
		}
		return this._value.substring(0, j) + (this._precision ? zk.DECIMAL + this._value.substring(j) : '');
	}
});

/** A long integer.
 * @disable(zkgwt)
 */
zk.Long = zk.$extends(zk.Object, {
	_precision: 0,
	$define: {
		/** Returns the precision.
		 * <p>Default: 0
		 * @return int
		 */
		/** Sets the precision
		 * @param int precision the precision
		 */
		precision: null
	},
	/** Constructor.
	 * @param Object value a number or a string
	 */
	$init: function (value) {
	//Note: it shall work like parseInt:
	//1) consider '.' rather than zkDecimal
	//2) ignore unrecognized characters
		value = value ? '' + value: '0';
		for (var j = 0, len = value.length; j < len; ++j) {
			var cc = value.charAt(j);
			if ((cc < '0' || cc > '9') && cc != '-' && cc != '+') {
				value = value.substring(0, j);
				break;
			}
		}
		this._value = value;
	},
	/** Returns a string for this long integer
	 * To have a Locale-dependent string, use {@link #$toLocaleString}
	 * instead.
	 * @return String
	 */
	$toString: zkf = function() { //toString is reserved keyword for IE
		if (this._value.length == 0) return ''; 
		var j = this._value.length - this._precision;
		return j <= 0 ? 0: this._value.substring(0, j);
	},
	/** Returns a Locale-dependent string for this long integer.
	 * @return String
	 */
	$toLocaleString: zkf
});
