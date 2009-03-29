/* Textbox.js

	Purpose:
		
	Description:
		
	History:
		Sat Dec 13 23:30:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Textbox = zk.$extends(zul.inp.InputWidget, {
	_value: '',
	_rows: 1,

	isMultiline: function () {
		return this._multiline;
	},
	setMultiline: function (multiline) {
		if (this._multiline != multiline) {
			this._multiline = multiline;
			this.rerender();
		}
	},
	isTabbable: function () {
		return this._tabbable;
	},
	setTabbable: function (tabbable) {
		this._tabbable = tabbable;
	},
	getRows: function () {
		return this._rows;
	},
	setRows: function (rows) {
		if (this._rows != rows) {
			this._rows = rows;
			if (this.einp && this.isMultiline())
				this.einp.rows = rows;
		}
	},
	setType: function (type) {
		if (this._type != type) {
			this._type = type;
			if (this.einp)
				this.einp.type = type;
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
});
