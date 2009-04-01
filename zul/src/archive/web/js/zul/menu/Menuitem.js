/* Menuitem.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:33     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.menu.Menuitem = zk.$extends(zul.LabelImageWidget, {
	_value: "",

	isCheckmark: function () {
		return this._checkmark;
	},
	setCheckmark: function (checkmark) {
		if (this._checkmark != checkmark) {
			this._checkmark = checkmark;
			this.rerender();
		}
	},
	setDisabled: function (disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			this.rerender();
		}
	},
	isDisabled: function () {
		return this._disabled;
	},
	getValue: function () {
		return this._value;
	},
	setValue: function (value) {
		if (!value)	value = "";
		this._value = value;
	},
	isChecked: function () {
		return this._checked;
	},
	setChecked: function (checked) {
		if (this._checked != checked) {
			this._checked = checked;
			if (this._checked)
				this._checkmark = this._checked;
			var n = this.getNode();
			if (n && !this.isTopmost() && !this.getImage()) {
				var zcls = this.getZclass();
				zDom.rmClass(n, zcls + '-cnt-ck');
				zDom.rmClass(n, zcls + '-cnt-unck');
				if (this._checkmark)
					zDom.addClass(n, zcls + (this._checked ? '-cnt-ck' : '-cnt-unck'));
			}
		}
	},
	isAutocheck: function () {
		return this._autocheck;
	},
	setAutocheck: function (autocheck) {
		if (this._autocheck != autocheck)
			this._autocheck = autocheck;
	},
	getHref: function () {
		return this._href;
	},
	setHref: function (href) {
		if (this._href != href) {
			this._href = href;
			this.rerender();
		}
	},
	getTarget: function () {
		return this._target;
	},
	setTarget: function (target) {
		if (this._target !=  target) {
			this._target = target;
			var anc = this.getSubnode('a');
			if (anc) {
				if (this.isTopmost())
					anc = anc.parentNode;
				anc.target = this._target;
			}
		}
	},
	isTopmost: function () {
		return this._topmost;
	},
	beforeParentChanged_: function (newParent) {
		this._topmost = newParent && !(newParent.$instanceof(zul.menu.Menupopup));
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = this.isDisabled() ? this.getZclass() + '-disd' : '';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu-item" : this._zclass;
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this._image;
		if (!img) img = zAu.comURI('web/img/spacer.gif');

		img = '<img src="' + img + '" align="absmiddle" class="' + this.getZclass() + '-img"/>';
		return label ? img + ' ' + label: img;
	},
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments);
		return this.isTopmost() ?
			style + 'padding-left:4px;padding-right:4px;': style;
	},
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	/** Removes the extra space (IE only) */
	_fixBtn: function () {
		var btn = this.getSubnode('b');
		if (!btn || !btn.innerHTML.trim()) return;
		btn.style.width = zDom.textSize(btn, btn.innerHTML)[0] + zDom.padBorderWidth(btn) + "px";
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		if (!this.isDisabled()) {
			var anc = this.getSubnode('a'),
				n = this.getNode();

			zEvt.listen(n, "mouseover", this.proxy(this._doMouseOver, '_fxMouseOver'));
			zEvt.listen(n, "mouseout", this.proxy(this._doMouseOut, '_fxMouseOut'));

			if (this.isTopmost()) {
				zEvt.listen(anc, "focus", this.proxy(this.domFocus_, '_fxFocus'));
				zEvt.listen(anc, "blur", this.proxy(this.domBlur_, '_fxBlur'));
			}
		}
		if (zk.ie && this.isTopmost()) this._fixBtn();
	},
	unbind_: function () {
		if (!this.isDisabled()) {
			var anc = this.getSubnode('a'),
				n = this.getNode();
			zEvt.unlisten(n, "mouseover", this._fxMouseOver);
			zEvt.unlisten(n, "mouseout", this._fxMouseOut);

			if (this.isTopmost()) {
				zEvt.unlisten(anc, "focus", this._fxFocus);
				zEvt.unlisten(anc, "blur", this._fxBlur);
			}
		}

		this.$supers('unbind_', arguments);
	},
	doClick_: function (evt) {
		if (this._disabled)
			evt.stop();
		else {
			if (!this.$class._isActive(this)) return;

			var topmost = this.isTopmost(),
				anc = this.getSubnode('a');

			if (topmost) {
				zDom.rmClass(anc, this.getZclass() + '-body-over');
				anc = anc.parentNode;
			}
			if ('javascript:;' == anc.href) {
				if (this.isAutocheck()) {
					this.setChecked(!this.isChecked());
					this.fire('onCheck', this.isChecked());
				}
				this.fireX(evt);
			} else {
				if (zk.ie && topmost && this.getNode().id != anc.id)
					zUtl.go(anc.href, false, anc.target);
					// Bug #1886352 and #2154611
					//Note: we cannot eat onclick. or, <a> won't work
			}
			zWatch.fire('onFloatUp', null, this); //notify all
		}
	},
	_doMouseOver: function (evt) {
		if (this.$class._isActive(this)) return;
		if (!this.isDisabled()) {
			if (this.isTopmost() && zk.ie && !zDom.isAncestor(this.getSubnode('a'), zEvt.target(evt)))
				return;

			this.$class._addActive(this);
			zWatch.fire('onFloatUp', null, this); //notify all
		}
	},
	_doMouseOut: function (evt) {
		if (!this.isDisabled()) {
			if (zk.ie) {
					var n = this.getSubnode('a'),
						xy = zDom.revisedOffset(n),
						p = zEvt.pointer(evt),
						x = p[0],
						y = p[1],
						diff = this.isTopmost() ? 1 : 0;
					if (x - diff > xy[0] && x <= xy[0] + n.offsetWidth && y - diff > xy[1] &&
						y <= xy[1] + n.offsetHeight)
						return; // don't deactivate;
				}
			this.$class._rmActive(this);
		}
	}
}, {
	_isActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-body-over' : '-over');
		return zDom.hasClass(n, cls);
	},
	_addActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-body-over' : '-over');
		zDom.addClass(n, cls);
		if (!top && wgt.parent.parent.$instanceof(zul.menu.Menu))
			this._addActive(wgt.parent.parent);
	},
	_rmActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-body-over' : '-over');
		zDom.rmClass(n, cls);
	}
});
