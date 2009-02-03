_z='zul.inp';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

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
		if (this._cst) delete this._lastRawValVld; //revalidate required
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
	_doSelect: function (evt) {
		if (this.isListen('onSelection')) {
			var inp = this.einp,
				sr = zDom.selectionRange(inp),
				b = sr[0], e = sr[1];
			this.fire('onSelection', {start: b, end: e,
				selected: inp.value.substring(b, e),
				marshal: this._onSelMarshal});
		}
	},
	_onSelMarshal: function () {
		return [this.start, this.end, this.selected];
	},
	_lateBlur: function () {
		if (this.shallUpdate_(zk.currentFocus))
			this._updateChange();
	},
	shallUpdate_: function (focus) {
		return !focus || !zUtl.isAncestor(this, focus);
	},
	getErrorMesssage: function () {
		return this._errmsg;
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
	_markError: function (val, msg) {
		this._errmsg = msg;

		if (this.desktop) { //err not visible if not attached
			zDom.addClass(this.einp, this.getZclass() + "-text-invalid");

			var cst = this._cst, errbox;
			if (cst) {
				errbox = cst.showCustomError;
				if (errbox) errbox = errbox.call(cst, this, msg);
			}

			if (!errbox) this._errbox = this.showError_(msg);

			this.fire('onError',
				{value: val, message: msg, marshal: this._onErrMarshal});
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
					var errmsg = val.error;
					if (errmsg) {
						this.clearErrorMessage(true);
						this._markError(val, errmsg);
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
					this._markError(val, msg);
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
	_updateChange: function () {
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
		var inp = this.einp = this.getSubnode('inp') || this.getNode();
		zEvt.listen(inp, "focus", this.proxy(this.doFocus_, '_pxFocus'));
		zEvt.listen(inp, "blur", this.proxy(this.doBlur_, '_pxBlur'));
		zEvt.listen(inp, "select", this.proxy(this._doSelect, '_pxSelect'));
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
});

(_zkwg=_zkpk.InputWidget).prototype.className='zul.inp.InputWidget';
zul.inp.Errorbox = zk.$extends('zul.wgt.Popup', {
	$init: function () {
		this.$supers('$init', arguments);
		this.setWidth("260px");
		this.setSclass('z-errbox');
	},
	show: function (owner, msg) {
		this.parent = owner; //fake
		this.msg = msg;
		this.insertHTML(document.body, "beforeEnd");
		this.open(owner, null, "end_before", {overflow:true});
	},
	destroy: function () {
		this.close();
		var n = this.getNode();
		this.unbind_();
		zDom.remove(n);
		this.parent = null;
	},
	//super//
	bind_: function () {
		this.$supers('bind_', arguments);

		var $Errorbox = zul.inp.Errorbox;
		this._drag = new zk.Draggable(this, null, {
			starteffect: zk.$void,
			endeffect: $Errorbox._enddrag,
			ignoredrag: $Errorbox._ignoredrag,
			change: $Errorbox._change
		});
		zWatch.listen('onScroll', this);
	},
	unbind_: function () {
		this._drag.destroy();
		zWatch.unlisten('onScroll', this);

		this.$supers('unbind_', arguments);
		this._drag = null;
	},
	onScroll: function (wgt) {
		if (wgt) { //scroll requires only if inside, say, borderlayout
			this.position(this.parent, null, "end_before", {overflow:true});
			this._fixarrow();
		}
	},
	setDomVisible_: function (node, visible) {
		this.$supers('setDomVisible_', arguments);
		var stackup = this._stackup;
		if (stackup) stackup.style.display = visible ? '': 'none';
	},
	doMouseMove_: function (evt) {
		var el = evt.nativeTarget;
		if (el == this.getSubnode('c')) {
			var y = evt.data.pageY,
				size = zk.parseInt(zDom.getStyle(el, 'padding-right'))
				offs = zDom.revisedOffset(el);
			if (y >= offs[1] && y < offs[1] + size)	zDom.addClass(el, 'z-errbox-close-over');
			else zDom.rmClass(el, 'z-errbox-close-over');
		} else this.$supers('doMouseMove_', arguments);
	},
	doMouseOut_: function (evt) {
		var el = evt.nativeTarget;
		if (el == this.getSubnode('c'))
			zDom.rmClass(el, 'z-errbox-close-over');
		else
			this.$supers('doMouseOut_', arguments);
	},
	doClick_: function (evt) {
		var el = evt.nativeTarget;
		if (el == this.getSubnode('c') && zDom.hasClass(el, 'z-errbox-close-over'))
			this.parent.clearErrorMessage(true, true);
		else {
			this.$supers('doClick_', arguments);
			this.parent.focus(0);
		}
	},
	open: function () {
		this.$supers('open', arguments);
		this.setTopmost();
		this._fixarrow();
	},
	prologHTML_: function (out) {
		var id = this.uuid;
		out.push('<div id="', id);
		out.push('$a" class="z-errbox-left z-arrow" title="')
		out.push(zUtl.encodeXML(mesg.GOTO_ERROR_FIELD));
		out.push('"><div id="', id, '$c" class="z-errbox-right z-errbox-close"><div class="z-errbox-center">');
		out.push(zUtl.encodeXML(this.msg, {multiline:true})); //Bug 1463668: security
		out.push('</div></div></div>');
	},
	onFloatUp: function (wgt) {
		if (wgt == this) {
			this.setTopmost();
			return;
		}
		if (!wgt || wgt == this.parent || !this.isVisible())
			return;

		var top1 = this, top2 = wgt;
		while ((top1 = top1.parent) && !top1.isFloating_())
			;
		for (; top2 && !top2.isFloating_(); top2 = top2.parent)
			;
		if (top1 == top2) { //uncover if sibling
			var n = wgt.getNode();
			if (n) this._uncover(n);
		}
	},
	_uncover: function (el) {
		var elofs = zDom.cmOffset(el),
			node = this.getNode(),
			nodeofs = zDom.cmOffset(node);

		if (zDom.isOverlapped(
		elofs, [el.offsetWidth, el.offsetHeight],
		nodeofs, [node.offsetWidth, node.offsetHeight])) {
			var parent = this.parent.getNode(), y;
			var ptofs = zDom.cmOffset(parent),
				pthgh = parent.offsetHeight,
				ptbtm = ptofs[1] + pthgh;
			y = elofs[1] + el.offsetHeight <=  ptbtm ? ptbtm: ptofs[1] - node.offsetHeight;
				//we compare bottom because default is located below

			var ofs = zDom.toStyleOffset(node, 0, y);
			node.style.top = ofs[1] + "px";
			this._fixarrow();
		}
	},
	_fixarrow: function () {
		var parent = this.parent.getNode(),
			node = this.getNode(),
			arrow = this.getSubnode('a'),
			ptofs = zDom.revisedOffset(parent),
			nodeofs = zDom.revisedOffset(node);
		var dx = nodeofs[0] - ptofs[0], dy = nodeofs[1] - ptofs[1], dir;
		if (dx >= parent.offsetWidth - 2) {
			dir = dy < -10 ? "ld": dy >= parent.offsetHeight - 2 ? "lu": "l";
		} else if (dx < 0) {
			dir = dy < -10 ? "rd": dy >= parent.offsetHeight - 2 ? "ru": "r";
		} else {
			dir = dy < 0 ? "d": "u";
		}
		arrow.className = 'z-errbox-left z-arrow-' + dir;
	}
},{
	_enddrag: function (dg) {
		var errbox = dg.control;
		errbox.setTopmost();
		errbox._fixarrow();
	},
	_ignoredrag: function (dg, pointer, evt) {
		return zEvt.target(evt) == dg.control.getSubnode('c') && zDom.hasClass(dg.control.getSubnode('c'), 'z-errbox-close-over');
	},
	_change: function (dg) {
		var errbox = dg.control,
			stackup = errbox._stackup;
		if (stackup) {
			var el = errbox.getNode();
			stackup.style.top = el.style.top;
			stackup.style.left = el.style.left;
		}
		errbox._fixarrow();
	}
});
(_zkwg=_zkpk.Errorbox).prototype.className='zul.inp.Errorbox';
zul.inp.SimpleConstraint = zk.$extends(zk.Object, {
	$init: function (a, b, c) {
		if (typeof a == 'string') {
			this._flags = {};
			this._init(a);
		} else {
			if (a) this._flags = typeof a == 'number' ? this._cvtNum(a): a;
			this._regex = typeof b == 'string' ? new RegExp(b): b;
			this._errmsg = c; 
		}
	},
	_init: function (cst) {
		l_out:
		for (var j = 0, k = 0, len = cst.length; k >= 0; j = k + 1) {
			for (;; ++j) {
				if (j >= len) return; //done

				var cc = cst.charAt(j);
				if (cc == '/') {
					for (k = ++j;; ++k) { //look for ending /
						if (k >= len) { //no ending /
							k = -1;
							break;
						}

						cc = cst.charAt(k);
						if (cc == '/') break; //ending / found
						if (cc == '\\') ++k; //skip one
					}
					this._regex = new RegExp(k >= 0 ? cst.substring(j, k): cst.substring(j));
					continue l_out;
				}
				if (cc == ':') {
					this._errmsg = cst.substring(j + 1).trim();
					return; //done
				}
				if (!zk.isWhitespace(cc))
					break;
			}

			var s;
			for (k = j;; ++k) {
				if (k >= len) {
					s = cst.substring(j);
					k = -1;
					break;
				}
				var cc = cst.charAt(k);
				if (cc == ',' || cc == ':' || cc == ';' || cc == '/') {
					s = cst.substring(j, k);
					if (cc == ':' || cc == '/') --k;
					break;
				}
			}

			this.parseConstraint_(s.trim().toLowerCase());
		}
	},
	parseConstraint_: function (cst) {
		var f = this._flags;
		if (cst == "no positive")
			f.NO_POSITIVE = true;
		else if (cst == "no negative")
			f.NO_NEGATIVE = true;
		else if (cst == "no zero")
			f.NO_ZERO = true;
		else if (cst == "no empty")
			f.NO_EMPTY = true;
		else if (cst == "no future")
			f.NO_FUTURE = true;
		else if (cst == "no past")
			f.NO_PAST = true;
		else if (cst == "no today")
			f.NO_TODAY = true;
		else if (cst == "strict")
			f.STRICT = true;
	},
	_cvtNum: function (v) { //compatible with server side
		var f = {};
		if (v & 1)
			f.NO_POSITIVE = f.NO_FUTURE = true;
		if (v & 2)
			f.NO_NEGATIVE = f.NO_PAST = true;
		if (v & 4)
			f.NO_ZERO = f.NO_TODAY = true;
		if (v & 0x100)
			f.NO_EMPTY = true;
		if (v & 0x200)
			f.STRICT = true;
		return f;
	},
	validate: function (wgt, val) {
		var f = this._flags,
			msg = this._errmsg;

		switch (typeof val) {
		case 'string':
			if (f.NO_EMPTY && (!val || !val.trim()))
				return msgzul.EMPTY_NOT_ALLOWED;
			var regex = this._regex;
			if (regex && !regex.test(val))
				return msg || msgzul.ILLEGAL_VALUE;
			if (f.STRICT && val) {
				//TODO VALUE_NOT_MATCHED;
			}
			return;
		case 'number':
			if (val > 0) {
				if (f.NO_POSITIVE) return msg || this._msgNumDenied();
			} else if (val == 0) {
				if (f.NO_ZERO) return msg || this._msgNumDenied();
			} else
				if (f.NO_NEGATIVE) return msg || this._msgNumDenied();
			return;
		}

		if (val && val.getFullYear) {
			var today = zUtl.today(),
				val = new Date(val.getFullYear(), val.getMonth(), val.getDate());
			if (val > today) {
				if (f.NO_FUTURE) return msg || this._msgDateDenied();
			} else if (val == today) {
				if (f.NO_TODAY) return msg || this._msgDateDenied();
			} else
				if (f.NO_PAST) return msg || this._msgDateDenied();
			return;
		}

		if (val && val.compareTo) {
			var b = val.compareTo(0);
			if (b > 0) {
				if (f.NO_POSITIVE) return msg || this._msgNumDenied();
			} else if (b == 0) {
				if (f.NO_ZERO) return msg || this._msgNumDenied();
			} else
				if (f.NO_NEGATIVE) return msg || this._msgNumDenied();
			return;
		}

		if (!val && f.NO_EMPTY) return msg || msgzul.EMPTY_NOT_ALLOWED;
	},
	_msgNumDenied: function () {
		var f = this._flags;
		if (f.NO_POSITIVE)
			return f.NO_ZERO ?
				f.NO_NEGATIVE ? NO_POSITIVE_NEGATIVE_ZERO: msgzul.NO_POSITIVE_ZERO:
				f.NO_NEGATIVE ? msgzul.NO_POSITIVE_NEGATIVE: msgzul.NO_POSITIVE;
		else if (f.NO_NEGATIVE)
			return f.NO_ZERO ? msgzul.NO_NEGATIVE_ZERO: msgzul.NO_NEGATIVE;
		else if (f.NO_ZERO)
			return msgzul.NO_ZERO;
		return msgzul.ILLEGAL_VALUE;
	},
	_msgDateDenied: function () {
		var f = this._flags;
		if (f.NO_FUTURE)
			return f.NO_TODAY ?
				f.NO_PAST ? NO_FUTURE_PAST_TODAY: msgzul.NO_FUTURE_TODAY:
				f.NO_PAST ? msgzul.NO_FUTURE_PAST: msgzul.NO_FUTURE;
		else if (f.NO_PAST)
			return f.NO_TODAY ? msgzul.NO_PAST_TODAY: msgzul.NO_PAST;
		else if (f.NO_TODAY)
			return msgzul.NO_TODAY;
		return msgzul.ILLEGAL_VALUE;
	}
});


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
	innerAttrs_: function () {
		var html = this.$supers('innerAttrs_', arguments);
		if (this._multiline)
			html += ' rows="' + this._rows + '"';
		return html;
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-textbox";
	}
});

(_zkwg=_zkpk.Textbox).prototype.className='zul.inp.Textbox';_zkmd={};
_zkmd['default']=
function (out) {
	if(this.isMultiline()) 
		out.push('<textarea', this.domAttrs_(), this.innerAttrs_(), '>',
				this._areaText(), '</textarea>');
	else
		out.push('<input', this.domAttrs_(), this.innerAttrs_(), '/>');
}
zkmld(_zkwg,_zkmd);
zul.inp.FormatWidget = zk.$extends(zul.inp.InputWidget, {
	getFormat: function () {
		return this._format;
	},
	setFormat: function (format) {
		if (this._format != format) {
			this._format = format;
			if (this.einp)
				this.einp.value = this.coerceToString_(this._value);
		}
	}
});

(_zkwg=_zkpk.FormatWidget).prototype.className='zul.inp.FormatWidget';
zul.inp.Intbox = zk.$extends(zul.inp.FormatWidget, {
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zNumFormat.unformat(this._format, value),
			val = parseInt(info.raw);
		if (info.raw != ''+val)
			return {error: zMsgFormat.format(msgzul.INTEGER_REQUIRED, value)};

		if (info.divscale) val = Math.round(val / Math.pow(10, info.divscale));
		return val;
	},
	coerceToString_: function (value) {
		var fmt = this._format;
		return fmt ? zNumFormat.format(fmt, value): value ? ''+value: '';
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-intbox";
	}
});
(_zkwg=_zkpk.Intbox).prototype.className='zul.inp.Intbox';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<input', this.domAttrs_(), this.innerAttrs_(), '/>');
}
zkmld(_zkwg,_zkmd);
zul.inp.Doublebox = zk.$extends(zul.inp.Intbox, {
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zNumFormat.unformat(this._format, value),
			val = parseFloat(info.raw);
		if (info.raw != ''+val && info.raw.indexOf('e') < 0) //unable to handle 1e2
			return {error: zMsgFormat.format(msgzul.NUMBER_REQUIRED, value)};

		if (info.divscale) val = Math.round(val / Math.pow(10, info.divscale));
		return val;
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-doublebox";
	}
});
(_zkwg=_zkpk.Doublebox).prototype.className='zul.inp.Doublebox';_zkmd={};
_zkmd['default']=[_zkpk.Intbox,'default'];zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}