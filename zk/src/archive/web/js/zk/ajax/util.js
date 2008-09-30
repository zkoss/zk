/* util.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep 30 09:02:06     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zkUtil = { //static methods
	/** Returns the current time (new Date().getTime()).
	 * It is a number starting from 01/01/1970.
	 */
	now: function () {
		return new Date().getTime();
	},
	/** Executes a method after the specified delay (milliseconds).
	 * It is the same as window.setTimeout except <code>this</code> refers
	 * to the specified object, <code>obj</code>.
	 */
	setTimeout: function (obj, method, timeout/*, ...*/) {
		var args = [];
		for (var j = arguments.length; --j >= 3;)
			args.unshift(arguments[j]);
		setTimeout(function () {method.apply(obj, args)}, timeout);
	},
	/** Schedules a method or repeated execution.
	 * It is the same as window.setInterval exception <code>this</code> refers
	 * to the specified object, <code>obj</code>.
	 */
	setInterval: function (obj, method, period/*, ...*/) {
		var args = [];
		for (var j = arguments.length; --j >= 3;)
			args.unshift(arguments[j]);
		setInterval(function () {method.apply(obj, args)}, period);
	},

};
