/* InputWidget.js

	Purpose:
		
	Description:
		
	History:
		Sat Dec 13 23:30:28     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.InputWidget = zk.$extends(zul.Widget, {
	_maxlength: 0,
	_cols: 0,
	_tabindex: -1,
	_type: 'text',

	getType: function () {
		return this._type;
	},

	getValue: function () {
		return this._value;
	},
	setValue: function (value) {
		if (this._value != value) {
			this._value = value;
			//TODO
		}
	},

	coerceToString_: function (value) {
		return value || '';
	},
	_areaText: function () {
		return zUtl.encodeXML(this.coerceToString_(this._value));
	},

	innerAttrs_: function () {
		var html = '',
			v = this.isMultiline;
		if (v && v()) {
			v = this._cols;
			if (v > 0) html += ' cols="' + v + '"';
		} else {
			html += ' value="' + this.coerceToString_(this.getValue()) + '"';
			html += ' type="' + this._type + '"';
			v = this._cols;
			if (v > 0) html += ' size="' + v + '"';
			v = this._maxlength;
			if (v > 0) html += ' maxlength="' + v + '"';
		}
		v = this._tabindex;
		if (v >= 0) html += ' tabindex="' + v +'"';
		v = this._name;
		if (v) html += ' name="' + v + '"';
		if (this._disabled) html += ' disabled="disabled"';
		if (this._readonly) html += ' readonly="readonly"';
		return html;
	},

	//super//
	importantEvents_: {onChange:1}
});
