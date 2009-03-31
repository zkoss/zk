/* InputWidget.js

	Purpose:
		
	Description:
		
	History:
		Sat Dec 13 23:30:28     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
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
	setValue: function (value, fromServer) {
		if (fromServer) this.clearErrorMessage(true);
		else if (value == this._lastRawValVld) return; //not changed
 		else value = this._validate(value);

		if ((!value || !value.error) && (fromServer || this._value != value)) {
			this._value = value;
			if (this.einp) {
				this.einp.value = value = this.coerceToString_(value);
				if (fromServer) this.einp.defaultValue = value;
			}
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

	domAttrs_: function (no) {
		var attr = this.$supers('domAttrs_', arguments);
		if (!no || !no.text)
			attr += this.textAttrs_();
		return attr;
	},
	/** Attributes for the text control.
	 * Called automatically by [[#domAttrs_]] unless {text:true}
	 * is specified
	 */
	textAttrs_: function () {
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
		if (this._cst) delete this._lastRawValVld; //revalidate required
	},
	getConstraint: function () {
		return this._cst;
	},

	doFocus_: function (evt) {
		if (zDom.tag(evt.domTarget)) { //Bug 2111900
			if (this.isListen('onChanging')) {
				this._lastChg = this.einp.value;
				this._tidChg = setInterval(this.proxy(this._onChanging), 500);
			}
			zDom.addClass(this.einp, this.getZclass() + '-focus');
		}

		this.$supers('doFocus_', arguments);
	},
	doBlur_: function (evt) {
		this._stopOnChanging();

		zDom.rmClass(this.einp, this.getZclass() + '-focus');

		if (!zk.alerting)
			setTimeout(this._pxLateBlur, 0);
			//curretFocus still unknow, so wait a while to execute

		this.$supers('doBlur_', arguments);
	},

	//dom event//
	_domSelect: function (devt) {
		if (this.isListen('onSelection')) {
			var inp = this.einp,
				sr = zDom.getSelectionRange(inp),
				b = sr[0], e = sr[1];
			this.fire('onSelection', {start: b, end: e,
				selected: inp.value.substring(b, e)});
		}
	},
	_lateBlur: function () {
		if (this.shallUpdate_(zk.currentFocus))
			this.updateChange_();
	},
	shallUpdate_: function (focus) {
		return !focus || !zUtl.isAncestor(this, focus);
	},
	getErrorMesssage: function () {
		return this._errmsg;
	},
	showErrorMessage: function (msg) {
		this.clearErrorMessage(true, true);
		this._markError(msg, null, true);
	},
	clearErrorMessage: function (revalidate, remainError) {
		var w = this._errbox;
		if (w) {
			this._errbox = null;
			w.destroy();
		}
		if (!remainError) {
			this._errmsg = null;
			zDom.rmClass(this.einp, this.getZclass() + "-text-invalid");
		}
		if (revalidate)
			delete this._lastRawValVld; //cause re-valid
	},
	coerceFromString_: function (value) {
		return value;
	},
	coerceToString_: function (value) {
		return value || '';
	},
	_markError: function (msg, val, noOnError) { //val used only if noOnError=false
		this._errmsg = msg;

		if (this.desktop) { //err not visible if not attached
			zDom.addClass(this.einp, this.getZclass() + "-text-invalid");

			var cst = this._cst, errbox;
			if (cst) {
				errbox = cst.showCustomError;
				if (errbox) errbox = errbox.call(cst, this, msg);
			}

			if (!errbox) this._errbox = this.showError_(msg);

			if (!noOnError)
				this.fire('onError', {value: val, message: msg});
		}
	},
	validate_: function (val) {
		if (this._cst) {
			if (this._cst == true) { //by server
				return; //TODO
			}
			return this._cst.validate(this, val);
		}
	},
	_validate: function (value) {
		zul.inp.validating = true;
		try {
			var val = value;
			if (typeof val == 'string' || val == null) {
				val = this.coerceFromString_(val);
				if (val) {
					var msg = val.error;
					if (msg) {
						this.clearErrorMessage(true);
						this._markError(msg, val);
						return val;
					}
				}
			}

			//unlike server, validation occurs only if attached
			if (!this.desktop) this._errmsg = null;
			else {
				this.clearErrorMessage(true);
				var msg = this.validate_(val);
				if (msg) {
					this._markError(msg, val);
					return {error: msg};
				} else
					this._lastRawValVld = value; //raw
			}
			return val;
		} finally {
			zul.inp.validating = false;
		}
	},
	showError_: function (msg) {
		var eb = new zul.inp.Errorbox();
		eb.show(this, msg);
		return eb;
	},
	/** Updates the change to server by firing onChange if necessary. */
	updateChange_: function () {
		if (zul.inp.validating) return; //avoid deadloop (when both focus and blur fields invalid)

		var inp = this.einp,
			value = inp.value;
		if (value == this._lastRawValVld)
			return; //not changed

		var wasErr = this._errmsg,
			val = this._validate(value);
		if (!val || !val.error) {
			inp.value = value = this.coerceToString_(val);
			if (wasErr || value != inp.defaultValue) {
				this._value = val;
				inp.defaultValue = value;
				this.fire('onChange', this._onChangeData(value), null, 150);
			}
		}
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
	_onChangeData: function (val, selbk) {
		var inf = {value: val, start: zDom.getSelectionRange(this.einp)[0]}
		if (selbk) inf.bySelectBack =  true;
		return inf;
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
		var inp = this.einp = this.getSubnode('real') || this.getNode();
		zEvt.listen(inp, "focus", this.proxy(this.domFocus_, '_pxFocus'));
		zEvt.listen(inp, "blur", this.proxy(this.domBlur_, '_pxBlur'));
		zEvt.listen(inp, "select", this.proxy(this._domSelect, '_pxSelect'));
	},
	unbind_: function () {
		this.clearErrorMessage(true);

		var n = this.einp;
		zEvt.unlisten(n, "focus", this._pxFocus);
		zEvt.unlisten(n, "blur", this._pxBlur);
		zEvt.unlisten(n, "select", this._pxSelect);

		this.einp = null;
		this.$supers('unbind_', arguments);
	},
	doKeyDown_: function (evt) {
		var keyCode = evt.keyCode, keys = evt.keys;
		if (keyCode == 9 && !keys.altKey && !keys.ctrlKey && !keys.shiftKey
		&& this._tabbable) {
			var sr = zDom.getSelectionRange(inp),
				val = inp.value;
			val = val.substring(0, sr[0]) + '\t' + val.substring(sr[1]);
			inp.value = val;

			val = sr[0] + 1;
			zDom.setSelectionRange(inp, val, val);

			evt.stop();
			return;
		}

		if ((keyCode == 13 && this.isListen('onOK'))
		|| (keyCode == 27 && this.isListen('onCancel'))) {
			this._stopOnChanging();
			this.updateChange_();
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
});
