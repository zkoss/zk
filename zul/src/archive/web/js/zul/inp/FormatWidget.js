/* FormatWidget.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 12:54:29     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for an input box with format.
 */
zul.inp.FormatWidget = zk.$extends(zul.inp.InputWidget, {
	$define: { //zk.def
		/** Returns the format.
		 * <p>Default: null (used what is defined in the format sheet).
		 * @return String
		 */
		/** Sets the format.
		 * @param String format
		 */
		format: function () {
			var inp = this.getInputNode();
			if (inp)
				inp.value = this.coerceToString_(this._value);
		}
	}
});
