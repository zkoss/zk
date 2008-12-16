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
	isMultiline: function() {
		return false;
	},

	getValue: function () {
		return this._value;
	},
	setValue: function (value) {
		if (this._value != value) {
			this._value = value;
			if (this.einp)
				this.einp.value = value;
		}
	},

	getName: function () {
		return this._name;
	},
	setName: function (name) {
		if (this._name != name) {
			this._name = name;
			if (this.einp)
				this.einp.name = name;
		}
	},
	isDisabled: function () {
		return this._disabled;
	},
	setDisabled: function (disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			if (this.einp) {
				this.einp.disabled = disabled;
				var zcls = this.getZclass(),
					fnm = disabled ? 'addClass': 'rmClass';
				zDom[fnm](this.node, zcls + '-disd');
				zDom[fnm](this.einp, zcls + '-text-disd');
			}
		}
	},
	isReadonly: function () {
		return this._readonly;
	},
	setReadonly: function (readonly) {
		if (this._readonly != readonly) {
			this._readonly = readonly;
			if (this.einp) {
				this.einp.readOnly = readonly;
				zDom[readonly ? 'addClass': 'rmClass'](this.einp,
					this.getZclass() + '-readonly');
			}
		}
	},

	getCols: function () {
		return this._cols;
	},
	setCols: function (cols) {
		if (this._cols != cols) {
			this._cols = cols;
			if (this.einp)
				if (this.isMultiline()) this.einp.cols = cols;
				else this.einp.size = cols;
		}
	},
	getMaxlength: function () {
		return this._maxlength;
	},
	setMaxlengths: function (maxlength) {
		if (this._maxlength != maxlength) {
			this._maxlength = maxlength;
			if (this.einp && !this.isMultiline())
				this.einp.maxLength = maxlength;
		}
	},
	getTabindex: function () {
		return this._tabindex;
	},
	setTabindex: function (tabindex) {
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			if (this.einp)
				this.einp.tabIndex = tabindex;
		}
	},

	coerceToString_: function (value) {
		return value || '';
	},

	innerAttrs_: function () {
		var html = '', v;
		if (this.isMultiline()) {
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
	_areaText: function () {
		return zUtl.encodeXML(this.coerceToString_(this._value));
	},

	//dom event//
	doFocus_: function (evt) {
		if (!zDom.tag(zEvt.target(evt))) return; //Bug 2111900

		zDom.addClass(this.einp, this.getZclass() + '-focus');
	},
	doBlur_: function (evt) {
		//TODO
		zDom.rmClass(this.einp, this.getZclass() + '-focus');
	},

	//super//
	domClass_: function (no) {
		var sc = this.$supers('domClass_', arguments),
			zcls = this.getZclass();
		if (!no || !no.zclass) {
			if (this._disabled)
				sc += ' ' + zcls + '-disd';
		}
		if (!no || !no.input) {
			if (this._disabled)
				sc += ' ' + zcls + '-text-disd';
			if (this._readonly)
				sc += ' ' + zcls + '-readonly';
		}
		return sc;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var inp = this.einp = zDom.$(this.uuid + '$inp') || this.node,
			$InputWidget = zul.inp.InputWidget;
		zEvt.listen(inp, "focus", this._$doFocus = this.proxy(this.doFocus_));
		zEvt.listen(inp, "blur", this._$doBlur = this.proxy(this.doBlur_));
		zEvt.listen(inp, "select", $InputWidget._doSelect);
		zEvt.listen(inp, "keydown", $InputWidget._doKeyDown);
		if (zDom.tag(inp) == 'TEXTAREA')
			zEvt.listen(inp, "keyup", $InputWidget._doKeyUp);
	//Bug 1486556: we have to enforce zkTxbox to send value back for validating
	//at the server
	//TODO
	},
	unbind_: function () {
		var inp = this.einp,
			$InputWidget = zul.inp.InputWidget;
		zEvt.unlisten(inp, "focus", this._$doFocus);
		zEvt.unlisten(inp, "blur", this._$doBlur);
		zEvt.unlisten(inp, "select", $InputWidget._doSelect);
		zEvt.unlisten(inp, "keydown", $InputWidget._doKeyDown);
		if (zDom.tag(inp) == 'TEXTAREA')
			zEvt.unlisten(inp, "keyup", $InputWidget._doKeyUp);

		//TODO: clean up errorbox

		this._$doFocus = this._$doBlur = this.einp = null;
		this.$supers('unbind_', arguments);
	},
	isImportantEvent_: function (evtnm) {
		return 'onChange' == evtnm;
	}

},{
	_doSelect: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt.isListen('onSelection')) {
			var inp = wgt.einp,
				sr = zDom.getSelectionRange(inp),
				b = sr[0], e = sr[1];
			wgt.fire('onSelection', {start: b, end: e,
				selected: inp.value.substring(b, e),
				marshal: zul.inp.InputWidget._onSelMarshal});
		}
	},
	_onSelMarshal: function () {
		return [this.start, this.end, this.selected];
	},
	_doKeyDown: function (evt) {
		//TODO
	},
	_doKeyUp: function (evt) {
		//Request 1565288 and 1738246: support maxlength for Textarea
		var wgt = zk.Widget.$(evt),
			maxlen = wgt._maxlength;
		if (maxlen > 0) {
			var val = inp.value;
			if (val != inp.defaultValue && val.length > maxlen)
				inp.value = val.substring(0, maxlen);
		}
	}
});
