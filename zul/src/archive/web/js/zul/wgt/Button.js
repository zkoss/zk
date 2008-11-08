/* Button.js

	Purpose:
		
	Description:
		
	History:
		Sat Nov  8 22:58:16     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Button = zk.$extends(zul.Widget, {
	/** Returns the label of this button.
	 */
	getLabel: function () {
		var v = this._label;
		return v ? v: '';
	},
	/** Sets the label of this button.
	 */
	setLabel: function(label) {
		if (label == null) label = '';
		if (this._label != label) {
			this._label = label;
			var n = this.node;
			if (n) {
				//TODO
			}
		}
	},

	//super//
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-button";
	}
});