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

	$init: function () {
		this._pxLateBlur = this.proxy(this._lateBlur);
		this.$supers('$init', arguments);
	},
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
				zDom[fnm](this.getNode(), zcls + '-disd');
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

	setConstraint: function (cst) {
		if (typeof cst == 'string')
			this._cst = new zul.inp.SimpleConstraint(cst);
		else if (cst == true) //by-server
			this._cst = true;
		else
			this._cst = cst;
	},
	getConstraint: function () {
		return this._cst;
	},
	//dom event//
	doFocus_: function (evt) {
		if (!zDom.tag(zEvt.target(evt)) //Bug 2111900
		|| !this.domFocus_())
			return;

		if (this.isListen('onChanging')) {
			this._lastChg = this.einp.value;
			this._tidChg = setInterval(this.proxy(this._onChanging), 500);
		}
		zDom.addClass(this.einp, this.getZclass() + '-focus');
	},
	doBlur_: function (evt) {
		this._stopOnChanging();

		zDom.rmClass(this.einp, this.getZclass() + '-focus');
		this.domBlur_();

		setTimeout(this._pxLateBlur, 0);
			//curretFocus still unknow, so wait a while to execute
	},
	_lateBlur: function () {
		if (this.shallUpdate_(zk.currentFocus))
			this._updateChange();
	},
	shallUpdate_: function (focus) {
		return !focus || !zUtl.isAncestor(this, focus);
	},
	validate_: function (value) {
		if (this._cst) {
			zul.inp.validating = true;
			try {
				if (this._cst == true) { //by server
					return; //TODO
				}
				return this._cst.validate(this, value);
			} finally {
				zul.inp.validating = false;
			}
		}
	},
	showError_: function (msg) {
		var eb = new zul.inp.Errorbox();
		eb.show(this, msg);
		return eb;
	},
	_updateChange: function () {
		if (zul.inp.validating) return; //avoid deadloop (when both focus and blur fields invalid)

		var inp = this.einp,
			val = inp.value;
		if (val != this._lastValVld) {
			this._lastValVld = val;
			this._destroyerrbox();
			var msg = this.validate_(val);
			if (msg) {
				zDom.addClass(inp, this.getZclass() + "-text-invalid");

				var cst = this._cst, errbox;
				if (cst) {
					errbox = cst.showCustomError;
					if (errbox) errbox = errbox.call(cst, this, msg);
				}

				if (!errbox) this._errbox = this.showError_(msg);

				this.fire('onError',
					{value: val, message: msg, marshal: this._onErrMarshal},
					null, -1);
				return;
			} else
				zDom.rmClass(inp, this.getZclass() + "-text-invalid");
		}
		if (val != inp.defaultValue) {
			inp.defaultValue = val;
			this.fire('onChange', this._onChangeData(val), null, 150);
		}
//TODO else zk_err => fire onError to clear message
	},
	_onChanging: function () {
		var inp = this.einp,
			val = this.valueEnter__ || inp.value;
		if (this._lastChg != val) {
			this._lastChg = val;
			var valsel = this.valueSel_;
			this.valueSel_ = null;
			this.fire('onChanging', this._onChangeData(val, valsel == val),
				{ignorable:1}, 100);
		}
	},
	_onChangeData: function (val, selbak) {
		return {value: val,
			bySelectBack: selbak,
			start: zDom.selectionRange(this.einp)[0],
			marshal: this._onChangeMarshal}
	},
	_onChangeMarshal: function () {
		return [this.value, this.bySelectBack, this.start];
	},
	_onErrMarshal: function () {
		return [this.vale, this.message];
	},
	_stopOnChanging: function () {
		if (this._tidChg) {
			clearInterval(this._tidChg);
			this._tidChg = this._lastChg = this.valueEnter_ =
			this.valueSel_ = null;
		}
	},

	//super//
	focus: function (timeout) {
		if (this.isVisible() && this.canActivate({checkOnly:true})) {
			zDom.focus(this.einp, timeout);
			return true;
		}
		return false;
	},
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
		var inp = this.einp = zDom.$(this.uuid + '$inp') || this.getNode(),
			$InputWidget = zul.inp.InputWidget;
		zEvt.listen(inp, "focus", this.proxy(this.doFocus_, '_pxFocus'));
		zEvt.listen(inp, "blur", this.proxy(this.doBlur_, '_pxBlur'));
		zEvt.listen(inp, "select", $InputWidget._doSelect);
	//Bug 1486556: we have to enforce to send value back for validating
	//at the server
	//TODO
	},
	unbind_: function () {
		this._destroyerrbox();

		var n = this.einp,
			$InputWidget = zul.inp.InputWidget;
		zEvt.unlisten(n, "focus", this._pxFocus);
		zEvt.unlisten(n, "blur", this._pxBlur);
		zEvt.unlisten(n, "select", $InputWidget._doSelect);
		if (zDom.tag(n) == 'TEXTAREA')
			zEvt.unlisten(n, "keyup", $InputWidget._doKeyUp);

		this.einp = null;
		this.$supers('unbind_', arguments);
	},
	_destroyerrbox: function () {
		var n = this._errbox;
		if (n) {
			this._errbox = null;
			n.destroy();
		}
		delete this._lastValVld; //cause re-valid
	},
	isImportantEvent_: function (evtnm) {
		return 'onChange' == evtnm;
	},
	doKeyDown_: function (evt) {
		var keyCode = evt.keyCode;
		if ((keyCode == 13 && this.isListen('onOK'))
		|| (keyCode == 27 && this.isListen('onCancel'))) {
			this._stopOnChanging();
			this._updateChange();
		}
	},
	doKeyUp_: function () {
		//Support maxlength for Textarea
		if (this.isMultiline()) {
			var maxlen = this._maxlength;
			if (maxlen > 0) {
				var inp = this.einp, val = inp.value;
				if (val != inp.defaultValue && val.length > maxlen)
					inp.value = val.substring(0, maxlen);
			}
		}
		this.$supers('doKeyUp_', arguments);
	}

},{
	_doSelect: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt.isListen('onSelection')) {
			var inp = wgt.einp,
				sr = zDom.selectionRange(inp),
				b = sr[0], e = sr[1];
			wgt.fire('onSelection', {start: b, end: e,
				selected: inp.value.substring(b, e),
				marshal: zul.inp.InputWidget._onSelMarshal});
		}
	},
	_onSelMarshal: function () {
		return [this.start, this.end, this.selected];
	}
});
