/* FormatWidget.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 12:54:29     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for an input box with format.
 */
zul.inp.FormatWidget = zk.$extends(zul.inp.InputWidget, {
	$define: { //zk.def
		/** Returns the format.
		 * Always return null when input type is number (including Intbox, Spinner, Doublebox, Doublespinner, Longbox and Decimalbox) on tablet device.
		 * <p>Default: null (used what is defined in the format sheet).
		 * @return String
		 */
		/** Sets the format.
		 * Unsupported for tablet device when input type is number (including Intbox, Spinner, Doublebox, Doublespinner, Longbox and Decimalbox).
		 * @param String format
		 */
		format: function () {
			var inp = this.getInputNode();
			if (inp)
				inp.value = this.coerceToString_(this._value);
		}
	},
	doFocus_: function (evt) {
		this.$supers('doFocus_', arguments);
		if (this._shortcut)
			this.getInputNode().value = this._shortcut;
	},
	updateChange_: function (clear) {
		var upd = this.$supers('updateChange_', arguments);
		if (clear)
			delete this._shortcut;
		return upd;
	}
});

(function () {
if (zk.mobile) {
	var _xFormatWidget = {};
	zk.override(zul.inp.FormatWidget.prototype, _xFormatWidget, {
		setFormat: zk.$void
	});
}
})();