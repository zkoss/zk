/* FormatWidget.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 12:54:29     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.FormatWidget = zk.$extends(zul.inp.InputWidget, {
	getFormat: function () {
		return this._format;
	},
	setFormat: function (format) {
		if (this._format != format) {
			this._format = format;
			var inp = this.getInputNode();
			if (inp)
				inp.value = this.coerceToString_(this._value);
		}
	}
});
