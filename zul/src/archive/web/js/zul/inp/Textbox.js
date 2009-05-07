/* Textbox.js

	Purpose:
		
	Description:
		
	History:
		Sat Dec 13 23:30:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.def(zul.inp.Textbox = zk.$extends(zul.inp.InputWidget, {
	_value: '',
	_rows: 1,

	setType: function (type) {
		if (this._type != type) {
			this._type = type;
			var inp = this.getInputNode();
			if (inp)
				inp.type = type;
		}
	},

	//super//
	textAttrs_: function () {
		var html = this.$supers('textAttrs_', arguments);
		if (this._multiline)
			html += ' rows="' + this._rows + '"';
		return html;
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-textbox";
	}
}), { //zk.def
	multiline: function () {
		this.rerender();
	},
	tabbable: null,
	rows: function (v) {
		var inp = this.getInputNode();
		if (inp && this.isMultiline())
			inp.rows = v;
	}
});
