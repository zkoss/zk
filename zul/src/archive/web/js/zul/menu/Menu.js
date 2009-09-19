/* Menu.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:33     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.menu.Menu = zk.$extends(zul.LabelImageWidget, {
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = '<span class="' + this.getZclass() + '-img"' +
				(this._image ? ' style="background-image:url(' + this._image + ')"' : '')
				+ '></span>';
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
		var btn = this.$n('b');
		if (btn) {
			var txt = btn.innerHTML, $btn = zk(btn);
			btn.style.width = ($btn.textSize(txt)[0] + $btn.padBorderWidth()) + "px";
		}
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		if (!this.isTopmost()) {
			var anc = this.$n('a'),
				n = this.$n();
			this.domListen_(anc, "onFocus", "doFocus_")
				.domListen_(anc, "onBlur", "doBlur_")
				.domListen_(n, "onMouseOver")
				.domListen_(n, "onMouseOut");
		} else {
			if (zk.ie) this._fixBtn();

			var anc = this.$n('a');
			this.domListen_(anc, "onMouseOver")
				.domListen_(anc, "onMouseOut");
		}
	},
	unbind_: function () {
		if (!this.isTopmost()) {
			var anc = this.$n('a'),
				n = this.$n();
			this.domUnlisten_(anc, "onFocus", "doFocus_")
				.domUnlisten_(anc, "onBlur", "doBlur_")
				.domUnlisten_(n, "onMouseOver")
				.domUnlisten_(n, "onMouseOut");
		} else {
			var anc = this.$n('a');
			this.domUnlisten_(anc, "onMouseOver")
				.domUnlisten_(anc, "onMouseOut");
		}

		this.$supers('unbind_', arguments);
	},
	doClick_: function (evt) {
		if (this.isTopmost() && !jq.isAncestor(this.$n('a'), evt.domTarget)) return;

		if (this.menupopup) {
			jq(this.$n('a')).addClass(this.getZclass() + '-body-seld');
			this.menupopup._shallClose = false;
			if (this.isTopmost())
				this.getMenubar()._lastTarget = this;
			if (!this.menupopup.isOpen()) 
				this.menupopup.open();
			else {
				var anc = this.menupopup.$n('a');
				if (anc) anc.focus(); // force to get a focus 
			}
		}
		this.fireX(evt);
	},
	_doMouseOver: function (evt) { //not zk.Widget.doMouseOver_
		if (this.$class._isActive(this)) return;

		var	topmost = this.isTopmost();
		if (topmost && zk.ie && !jq.isAncestor(this.$n('a'), evt.domTarget))
				return; // don't activate

		if (!topmost) {
			if (this.menupopup) this.menupopup._shallClose = false;
			zWatch.fire('onFloatUp', this); //notify all
			if (this.menupopup && !this.menupopup.isOpen()) this.menupopup.open();
		} else {
			var menubar = this.getMenubar();
			if (this.menupopup && menubar.isAutodrop()) {
				menubar._lastTarget = this;
				this.menupopup._shallClose = false;
				zWatch.fire('onFloatUp', this); //notify all
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
		this.$class._addActive(this);
	},
	_doMouseOut: function (evt) { //not zk.Widget.doMouseOut_
		if (zk.ie && jq.isAncestor(this.$n('a'), evt.domEvent.relatedTarget || evt.domEvent.toElement))
			return; // don't deactivate
	
		var	topmost = this.isTopmost();
		if (topmost) {
			this.$class._rmActive(this);
			if (this.menupopup && this.getMenubar().isAutodrop()) {
				if (this.menupopup.isOpen()) this.menupopup._shallClose = true;
				zWatch.fire('onFloatUp', this, {timeout: 10}); //notify all
			}
		} else if (this.menupopup && !this.menupopup.isOpen())
			this.$class._rmActive(this);
	}
}, {
	_isActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.$n('a') : wgt.$n(),
			cls = wgt.getZclass() + (top ? '-body-over' : '-over');
		return jq(n).hasClass(cls);
	},
	_addActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.$n('a') : wgt.$n(),
			cls = wgt.getZclass() + (top ? '-body-over' : '-over');
		jq(n).addClass(cls);
		if (!top && wgt.parent.parent.$instanceof(zul.menu.Menu))
			this._addActive(wgt.parent.parent);
	},
	_rmActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.$n('a') : wgt.$n(),
			cls = wgt.getZclass() + (top ? '-body-over' : '-over');
		jq(n).removeClass(cls);
	}
});
