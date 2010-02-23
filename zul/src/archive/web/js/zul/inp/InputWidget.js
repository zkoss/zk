/* InputWidget.js

	Purpose:
		
	Description:
		
	History:
		Sat Dec 13 23:30:28     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for a input widget.
 */
zul.inp.InputWidget = zk.$extends(zul.Widget, {
	_maxlength: 0,
	_cols: 0,
	_tabindex: -1,
	_type: 'text',
	$define: {
		/** Returns the name of this component.
		 * <p>Default: null.
		 * <p>Don't use this method if your application is purely based
		 * on ZK's event-driven model.
		 * <p>The name is used only to work with "legacy" Web application that
		 * handles user's request by servlets.
		 * It works only with HTTP/HTML-based browsers. It doesn't work
		 * with other kind of clients.
		 * @return String
		 */
		/** Sets the name of this component.
		 * <p>Don't use this method if your application is purely based
		 * on ZK's event-driven model.
		 * <p>The name is used only to work with "legacy" Web application that
		 * handles user's request by servlets.
		 * It works only with HTTP/HTML-based browsers. It doesn't work
		 * with other kind of clients.
		 *
		 * @param String name the name of this component.
		 */
		name: function (name) {
			var inp = this.getInputNode();
			if (inp)
				inp.name = name;
		},
		/** Returns whether it is disabled.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is disabled.
		 * @param boolean disabled
		 */
		disabled: function (disabled) {
			var inp = this.getInputNode();
			if (inp) {
				inp.disabled = disabled;
				var zcls = this.getZclass(),
					fnm = disabled ? 'addClass': 'removeClass';
				jq(this.$n())[fnm](zcls + '-disd');
				jq(inp)[fnm](zcls + '-text-disd');
			}
		},
		/** Returns whether it is readonly.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is readonly.
		 * @param boolean readonly
		 */
		readonly: function (readonly) {
			var inp = this.getInputNode();
			if (inp) {
				var zcls = this.getZclass(),
					fnm = readonly ? 'addClass': 'removeClass';
				
				inp.readOnly = readonly;
				jq(inp)[fnm](zcls + '-readonly');
				
				if (!this.inRoundedMold()) return;
				
				var btn = this.$n('btn');
				jq(btn)[fnm](zcls + '-btn-readonly');
				
				if (zk.ie6_)		
					jq(btn)[fnm](zcls + (this._buttonVisible ? '-btn-readonly':
													'-btn-right-edge-readonly'));
			}
		},
		/** Returns the cols.
		 * <p>Default: 0 (non-positive means the same as browser's default).
		 * @return int
		 */
		/** Sets the cols.
		 * @param int cols
		 */
		cols: function (cols) {
			var inp = this.getInputNode();
			if (inp)
				if (this.isMultiline()) inp.cols = cols;
				else inp.size = cols;
		},
		/** Returns the maxlength.
		 * <p>Default: 0 (non-postive means unlimited).
		 * @return int
		 */
		/** Sets the maxlength.
		 * @param int maxlength
		 */
		maxlength: function (maxlength) {
			var inp = this.getInputNode();
			if (inp && !this.isMultiline())
				inp.maxLength = maxlength;
		},
		/** Returns the tab order of this component.
		 * <p>Default: -1 (means the same as browser's default).
		 * @return int
		 */
		/** Sets the tab order of this component.
		 * @param int tabindex
		 */
		tabindex: function (tabindex) {
			var inp = this.getInputNode();
			if (inp)
				inp.tabIndex = tabindex;
		},
		/** Returns whether enable the inplace-editing.
		 * <p>default: false.
		 * @return boolean
		 */
		/** Sets to enable the inplace-editing function that the look and feel is
		 * like a label.
		 * @param boolean inplace
		 */
		inplace: function (inplace) {
			this.rerender();
		}
	},
	/** Returns the CSS style of inplace if inplace is not null
	 * @return String
	 */
	getInplaceCSS: function () {
		return this._inplace ? this.getZclass() + '-inplace' : '';
	},
	/** Selects the whole text in this input.
	 * @param int start the starting index of the selection range
	 * @param int end the ending index of the selection range (excluding).
	 * 		In other words, the text between start and (end-1) is selected. 
	 */
	select: function (start, end) {
		zk(this.getInputNode()).setSelectionRange(start, end);
	},
	/** Returns the type.
	 * <p>Default: text.
	 * @return String
	 */
	getType: function () {
		return this._type;
	},
	/** Returns whether it is multiline.
	 * <p>Default: false.
	 * @return boolean
	 */
	isMultiline: function() {
		return false;
	},
	/**
	 * Returns whether is in rounded mold or not.
	 * @return boolean
	 */
	inRoundedMold: function(){
		return this._mold == "rounded";
	},
	/** Returns the value in the String format.
	 * @return String
	 */
	getValue: function () {
		return this._value;
	},
	/** Sets the value in the String format.
	 * @param String value the value; If null, it is considered as empty.
	 * @param boolean fromServer it will clear error message if true
	 */
	setValue: function (value, fromServer) {
		var vi;
		if (fromServer) this.clearErrorMessage(true);
		else if (value == this._lastRawValVld) return; //not changed
 		else {
 			vi = this._validate(value);
 			value = vi.value;
 		}

		//Note: for performance reason, we don't send value back if
		//the validation shall be done at server, i.e., if (vi.server)
		if ((!vi || !vi.error) && (fromServer || this._value != value)) {
			this._value = value;
			var inp = this.getInputNode();
			if (inp) {
				inp.value = value = this.coerceToString_(value);
				if (fromServer) inp.defaultValue = value; //not clear error if by client app
			}
		}
	},
	/** Returns the input node of this widget
	 * @return DOMElement
	 */
	getInputNode: _zkf = function () {
		return this.$n('real') || this.$n();
	},
	getTextNode: _zkf,
	domAttrs_: function (no) {
		var attr = this.$supers('domAttrs_', arguments);
		if (!no || !no.text)
			attr += this.textAttrs_();
		return attr;
	},
	/** Attributes for the text control.
	 * Called automatically by [[#domAttrs_]] unless {text:true}
	 * is specified
	 * @return String
	 */
	textAttrs_: function () {
		var html = '', v;
		if (this.isMultiline()) {
			v = this._cols;
			if (v > 0) html += ' cols="' + v + '"';
		} else {
			html += ' value="' + this._areaText() + '"';
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
		
		var s = jq.filterTextStyle(this.domStyle_({width: true, height: true, top: true, left: true}));
		if (s) html += ' style="' + s + '"';
		
		return html;
	},
	_areaText: function () {
		return zUtl.encodeXML(this.coerceToString_(this._value));
	},
	/** Sets the constraint.
	 * <p>Default: null (means no constraint all all).
	 * @param String cst
	 */
	setConstraint: function (cst) {
		if (typeof cst == 'string' && cst.charAt(0) != '['/*by server*/)
			this._cst = new zul.inp.SimpleConstraint(cst);
		else
			this._cst = cst;
		if (this._cst) delete this._lastRawValVld; //revalidate required
	},
	/** Returns the constraint, or null if no constraint at all.
	 * @return zul.inp.SimpleConstraint
	 */
	getConstraint: function () {
		return this._cst;
	},
	doMouseOut_: function () {
		this._inplaceout = true;
		this.$supers('doMouseOut_', arguments);
		
	},
	doMouseOver_: function () {
		this._inplaceout = false;
		this.$supers('doMouseOut_', arguments);
	},
	doFocus_: function (evt) {
		this.$supers('doFocus_', arguments);

		if (evt.domTarget.tagName) { //Bug 2111900
			var inp = this.getInputNode();
			if (this.isListen('onChanging')) {
				this._lastChg = inp.value;
				this._tidChg = setInterval(this.proxy(this._onChanging), 500);
			}
			
			jq(this.$n()).addClass(this.getZclass() + '-focus');
			if (this._inplace) {
				jq(this.getInputNode()).removeClass(this.getInplaceCSS());
				if (this._inplaceout === undefined)
					this._inplaceout = true;
			}
			
			// Bug #2280308
			if (this._errbox) {
				var self = this;
				setTimeout(function () {
					if (self._errbox)
						self._errbox.open(self, null, "end_before", {
							overflow: true
						});
				});
			}
		}
	},
	doBlur_: function (evt) {
		this._stopOnChanging();
		
		jq(this.$n()).removeClass(this.getZclass() + '-focus');
		if (!zk.alerting && this.shallUpdate_(zk.currentFocus)) {
			this.updateChange_();
			this.$supers('doBlur_', arguments);
		}
		if (this._inplace && this._inplaceout) {
			jq(this.getInputNode()).addClass(this.getInplaceCSS());
		}
	},

	_doSelect: function (evt) {
		if (this.isListen('onSelection')) {
			var inp = this.getInputNode(),
				sr = zk(inp).getSelectionRange(),
				b = sr[0], e = sr[1];
			this.fire('onSelection', {start: b, end: e,
				selected: inp.value.substring(b, e)});
		}
	},
	/** Returns shall be update or not
	 * @param zk.Widget focus
	 */
	shallUpdate_: function (focus) {
		return !focus || !zUtl.isAncestor(this, focus);
	},
	/** Returns the error message that is caused when user entered invalid value,
	 * or null if no error at all.
	 * 
	 * <p>
	 * The error message is set when user has entered a wrong value, or setValue
	 * is called with a wrong value. It is cleared once a correct value is
	 * assigned.
	 * 
	 * <p>
	 * If the error message is set, we say this input is in the error mode.
	 * @return String
	 */
	getErrorMesssage: function () {
		return this._errmsg;
	},
	/** Marks this widget's value is wrong and show the error message.
	 * <p>It is usually called by {@link zk.AuCmd0#wrongValue} (from the sever)
	 * @param String msg the error message
	 */
	setErrorMessage: function (msg) {
		this.clearErrorMessage(true, true);
		this._markError(msg, null, true);
	},
	/** Clears the error message and the error status.
	 * <p>It is also called by {@link zk.AuCmd0#clearWrongValue} (from the server).
	 * @param boolean revalidate whether to re-validate the value next time
	 * onblur occurs
	 * @param boolean remainError whether the input widget remains in the
	 * error status, if any, after the invocation.
	 */
	clearErrorMessage: function (revalidate, remainError) {
		var w = this._errbox;
		if (w) {
			this._errbox = null;
			w.destroy();
		}
		if (!remainError) {			
			var zcls = this.getZclass();
			this._errmsg = null;
			jq(this.getInputNode()).removeClass(zcls + "-text-invalid");
			if(zk.ie6_ && this.inRoundedMold())
				jq(this.$n('btn')).removeClass(zcls + "-btn-right-edge-invalid");
			
		}
		if (revalidate)
			delete this._lastRawValVld; //cause re-valid
	},
	/** Coerces the value passed to {@link #setValue}.
	 *
	 * <p>Deriving note:<br>
	 * If you want to store the value in other type, say BigDecimal,
	 * you have to override {@link #coerceToString_} and {@link #coerceFromString_}
	 * to convert between a string and your targeting type.
	 *
	 * <p>Moreover, when {@link zul.inp.Textbox} is called, it calls this method
	 * with value = null. Derives shall handle this case properly.
	 * @return String
	 */
	coerceFromString_: function (value) {
		return value;
	},
	/** Coerces the value passed to {@link #setValue}.
	 *
	 * <p>Default: convert null to an empty string.
	 *
	 * <p>Deriving note:<br>
	 * If you want to store the value in other type, say BigDecimal,
	 * you have to override {@link #coerceToString_} and {@link #coerceFromString_}
	 * to convert between a string and your targeting type.
	 * @return String
	 */
	coerceToString_: function (value) {
		return value || '';
	},
	_markError: function (msg, val, noOnError) {
		this._errmsg = msg;
		
		var zcls = this.getZclass();
		if (this.desktop) { //err not visible if not attached
			jq(this.getInputNode()).addClass(zcls + "-text-invalid");
			if(zk.ie6_ && this.inRoundedMold() && !this._buttonVisible)
				jq(this.$n('btn')).addClass(zcls + "-btn-right-edge-invalid");

			var cst = this._cst, errbox;
			if (cst != "[c") {
				if (cst && (errbox = cst.showCustomError))
					errbox = errbox.call(cst, this, msg);

				if (!errbox) this._errbox = this.showError_(msg);
			}

			if (!noOnError)
				this.fire('onError', {value: val, message: msg});
		}
	},
	/** Make the {@link zul.inp.SimpleConstraint} calls the validate for val,
	 * if {@link zul.inp.SimpleConstraint} is exist
	 * @param Object val a String, a number, or a date,the number or name of flag, 
	 * such as "no positive", 0x0001.
	 */
	validate_: function (val) {
		var cst;
		if (cst = this._cst) {
			if (typeof cst == "string") return false; //by server
			var msg = cst.validate(this, val);
			if (!msg && cst.serverValidate) return false; //client + server
			return msg;
		}
	},
	_validate: function (value) {
		zul.inp.validating = true;
		try {
			var val = value, msg;
			if (typeof val == 'string' || val == null) {
				val = this.coerceFromString_(val);
				if (val && (msg = val.error)) {
					this.clearErrorMessage(true);
					if (this._cst == "[c") //CustomConstraint
						return {error: msg, server: true};
					this._markError(msg, val);
					return val;
				}
			}

			//unlike server, validation occurs only if attached
			if (!this.desktop) this._errmsg = null;
			else {
				this.clearErrorMessage(true);
				msg = this.validate_(val);
				if (msg === false) {
					this._lastRawValVld = value; //raw (don't validate again if no changed and no error)
					return {value: val, server: true};
				}
				if (msg) {
					this._markError(msg, val);
					return {error: msg};
				} else
					this._lastRawValVld = value; //raw
			}
			return {value: val};
		} finally {
			zul.inp.validating = false;
		}
	},
	_shallIgnore: function (evt, keys) {
		var code = (zk.ie||zk.opera) ? evt.keyCode : evt.charCode;
		if (!evt.altKey && !evt.ctrlKey && (zk.ie || code >= 32)
		&& keys.indexOf(String.fromCharCode(code)) < 0) {
			evt.stop();
			return true;
		}
	},
	/** Create a {@link zul.inp.Errorbox} widget, and show the error message
	 * @param String msg the error message
	 * @see zul.inp.Errorbox#show
	 */
	showError_: function (msg) {
		var eb = new zul.inp.Errorbox();
		eb.show(this, msg);
		return eb;
	},
	/** Updates the change to server by firing onChange if necessary. 
	 * @return boolean
	 */
	updateChange_: function () {
		if (zul.inp.validating) return false; //avoid deadloop (when both focus and blur fields invalid)

		var inp = this.getInputNode(),
			value = inp.value;
		if (value == this._lastRawValVld)
			return false; //not changed

		var wasErr = this._errmsg,
			vi = this._validate(value);
		if (!vi.error || vi.server) {
			var upd;
			if (!vi.error) {
				inp.value = value = this.coerceToString_(vi.value);
				//reason to use defaultValue rather than this._value is
				//to save the trouble of coerceToString issue
				upd = wasErr || value != inp.defaultValue;
				if (upd) {
					this._value = vi.value; //vi - not coerced
					inp.defaultValue = value;
				}
			}
			if (upd || vi.server)
				this.fire('onChange', this._onChangeData(value),
					vi.server ? {toServer:true}: null, 150);
		}
		return true;
	},
	_onChanging: function () {
		var inp = this.getInputNode(),
			val = this.valueEnter_ || inp.value;
		if (this._lastChg != val) {
			this._lastChg = val;
			var valsel = this.valueSel_;
			this.valueSel_ = null;
			this.fire('onChanging', this._onChangeData(val, valsel == val),
				{ignorable:1}, 100);
		}
	},
	_onChangeData: function (val, selbk) {
		var inf = {value: val,
			start: zk(this.getInputNode()).getSelectionRange()[0]}
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
			zk(this.getInputNode()).focus(timeout);
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
			if (this._inplace) {
				sc += ' ' + this.getInplaceCSS();
			}
		}
		return sc;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var inp = this.getInputNode();
		
		if (this._readonly)
			jq(inp).addClass(this.getZclass() + '-readonly');
			
		this.domListen_(inp, "onFocus", "doFocus_")
			.domListen_(inp, "onBlur", "doBlur_")
			.domListen_(inp, "onSelect");
	},
	unbind_: function () {
		this.clearErrorMessage(true);

		var n = this.getInputNode();
		this.domUnlisten_(n, "onFocus", "doFocus_")
			.domUnlisten_(n, "onBlur", "doBlur_")
			.domUnlisten_(n, "onSelect")
			.$supers('unbind_', arguments);
	},
	doKeyDown_: function (evt) {
		var keyCode = evt.keyCode;
		if (this._readonly && keyCode == 8) {
			evt.stop(); // Bug #2916146
			return;
		}
			
		if (!this._inplaceout)
			this._inplaceout = keyCode == 9;
		if (keyCode == 9 && !evt.altKey && !evt.ctrlKey && !evt.shiftKey
		&& this._tabbable) {
			var inp = this.getInputNode(),
				$inp = zk(inp),
				sr = $inp.getSelectionRange(),
				val = inp.value;
			val = val.substring(0, sr[0]) + '\t' + val.substring(sr[1]);
			inp.value = val;

			val = sr[0] + 1;
			$inp.setSelectionRange(val, val);

			evt.stop();
			return;
		}

		this.$supers('doKeyDown_', arguments);
	},
	doKeyUp_: function () {
		//Support maxlength for Textarea
		if (this.isMultiline()) {
			var maxlen = this._maxlength;
			if (maxlen > 0) {
				var inp = this.getInputNode(), val = inp.value;
				if (val != inp.defaultValue && val.length > maxlen)
					inp.value = val.substring(0, maxlen);
			}
		}
		this.$supers('doKeyUp_', arguments);
	},
	afterKeyDown_: function (evt) {
		if (this._inplace) {
			if (evt.keyCode == 13) {
				var $inp = jq(this.getInputNode()), inc = this.getInplaceCSS();
				if ($inp.toggleClass(inc).hasClass(inc)) 
					$inp.zk.setSelectionRange(0, $inp[0].value.length);
			} else
				jq(this.getInputNode()).removeClass(this.getInplaceCSS());
		}
		if (evt.keyCode != 13 || !this.isMultiline())
			this.$supers('afterKeyDown_', arguments);
	},
	beforeCtrlKeys_: function (evt) {
		this.updateChange_();
	}
},{
	_allowKeys: "0123456789"+zk.MINUS
});
