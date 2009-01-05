/* Text.js

	Purpose:
		
	Description:
		
	History:
		Sun Jan  4 15:35:22     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zhtml.Text = zk.$extends(zhtml.Widget, {
	_value: '',

	/** Returns the value of this label.
	 */
	getValue: function () {
		return this._value;
	},
	/** Sets the value of this label.
	 */
	setValue: function(value) {
		if (!value) value = '';
		if (this._value != value) {
			this._value = value;
			var n = this.getNode();
			if (n) n.innerHTML = zUtl.encodeXML(value);
		}
	},

	redraw: function (out) {
		out.push('<span', this.domAttrs_(), '>',
			zUtl.encodeXML(this._value), '</span>');
	}
});
