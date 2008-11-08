/* Label.js

	Purpose:
		
	Description:
		
	History:
		Sun Oct  5 00:22:03     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Label = zk.$extends(zul.Widget, {
	/** Returns the value of this label.
	 */
	getValue: function () {
		var v = this._value;
		return v ? v: '';
	},
	/** Sets the value of this label.
	 */
	setValue: function(val) {
		if (val == null) val = '';
		if (this._value != val) {
			this._value = val;
			var n = this.node;
			if (n) n.node.innerHTML = this.getValue();
		}
	}
}, {
	embedAs: 'value' //retrieve zDom.$() as value
});
