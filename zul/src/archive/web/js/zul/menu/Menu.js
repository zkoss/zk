/* Menu.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:33     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.menu.Menu = zk.$extends(zul.LabelImageWidget, {
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this._image;
		if (!img) img = zAu.comURI('web/img/spacer.gif');

		img = '<img src="' + img + '" align="absmiddle" class="' + this.getZclass() + '-img"/>';
		return label ? img + ' ' + label: img;
	},
	isTopmost: function () {
		return this._topmost;
	},
	beforeParentChanged_: function (newParent) {
		this._topmost = newParent && !(newParent.$instanceof(zul.menu.Menupopup));
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu" : this._zclass;
	},
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments);
		return this.isTopmost() ?
			style + 'padding-left:4px;padding-right:4px;': style;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.menu.Menupopup))
			this.menupopup = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.menupopup)
			this.menupopup = null;
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

		if (!this.isTopmost()) {
			var anc = this.getSubnode('a'), n = this.getNode();
			zEvt.listen(anc, "focus", this.proxy(this.domFocus_, '_fxFocus'));
			zEvt.listen(anc, "blur", this.proxy(this.domBlur_, '_fxBlur'));
			zEvt.listen(n, "mouseover", this.proxy(this._doMouseOver, '_fxMouseOver'));
			zEvt.listen(n, "mouseout", this.proxy(this._doMouseOut, '_fxMouseOut'));
		} else {
			if (zk.ie) this._fixBtn();

			var anc = this.getSubnode('a');
			zEvt.listen(anc, "mouseover", this.proxy(this._doMouseOver, '_fxMouseOver'));
			zEvt.listen(anc, "mouseout", this.proxy(this._doMouseOut, '_fxMouseOut'));
		}
	},
	unbind_: function () {
		if (!this.isTopmost()) {
			var anc = this.getSubnode('a'),
				n = this.getNode();
			zEvt.unlisten(anc, "focus", this._fxFocus);
			zEvt.unlisten(anc, "blur", this._fxBlur);
			zEvt.unlisten(n, "mouseover", this._fxMouseOver);
			zEvt.unlisten(n, "mouseout", this._fxMouseOut);
		} else {
			var n = this.getNode();
			zEvt.unlisten(n, "mouseover", this._fxMouseOver);
			zEvt.unlisten(n, "mouseout", this._fxMouseOut);
		}

		this.$supers('unbind_', arguments);
	},
	doClick_: function (evt) {
		if (this.isTopmost() && !zDom.isAncestor(this.getSubnode('a'), evt.domTarget)) return;
		zDom.addClass(this.getSubnode('a'), this.getZclass() + '-body-seld');
		if (this.menupopup) {
			this.menupopup._shallClose = false;
			if (this.isTopmost())
				this.getMenubar()._lastTarget = this;
			if (!this.menupopup.isOpen()) this.menupopup.open();
		}
		this.fireX(evt);
	},
	_doMouseOver: function (evt) {
		if (this.$class._isActive(this)) return;

		var	topmost = this.isTopmost();
		if (topmost && zk.ie && !zDom.isAncestor(this.getSubnode('a'), zEvt.target(evt)))
				return; // don't activate

		this.$class._addActive(this);
		if (!topmost) {
			if (this.menupopup) this.menupopup._shallClose = false;
			zWatch.fire('onFloatUp', null, this); //notify all
			if (this.menupopup && !this.menupopup.isOpen()) this.menupopup.open();
		} else {
			var menubar = this.getMenubar();
			if (this.menupopup && menubar.isAutodrop()) {
				menubar._lastTarget = this;
				this.menupopup._shallClose = false;
				zWatch.fire('onFloatUp', null, this); //notify all
				if (!this.menupopup.isOpen()) this.menupopup.open();
			} else {
				var target = menubar._lastTarget;
				if (target && target != this && menubar._lastTarget.menupopup
						&& menubar._lastTarget.menupopup.isVisible()) {
					menubar._lastTarget.menupopup.close({sendOnOpen:true});
					this.$class._rmActive(menubar._lastTarget);
					menubar._lastTarget = this;
					if (this.menupopup) this.menupopup.open();
				}
			}
		}
	},
	_doMouseOut: function (evt) {
		if (zk.ie) {
			var n = this.getSubnode('a'),
				xy = zDom.revisedOffset(n),
				p = zEvt.pointer(evt),
				x = p[0],
				y = p[1],
				diff = this.isTopmost() ? 1 : 0,
				vdiff = this.isTopmost() && 'vertical' == this.parent.getOrient() ? 1 : 0;
			if (x - diff > xy[0] && x <= xy[0] + n.offsetWidth && y - diff > xy[1] &&
					y - vdiff <= xy[1] + n.offsetHeight)
				return; // don't deactivate;
		}
		var	topmost = this.isTopmost();
		if (topmost) {
			if (this.menupopup && this.getMenubar().isAutodrop()) {
				this.$class._rmActive(this);
				if (this.menupopup.isOpen()) this.menupopup._shallClose = true;
				zWatch.fire('onFloatUp', {
					timeout: 10
				}, this); //notify all
			}
		} else if (this.menupopup && !this.menupopup.isOpen())
			this.$class._rmActive(this);
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
