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

	$define: {
		checkmark: _zkf = function () {
			this.rerender();
		},
		disabled: _zkf,
		href: _zkf,
		value: null,
		checked: function (checked) {
			if (checked)
				this._checkmark = checked;
			var n = this.getNode();
			if (n && !this.isTopmost() && !this.getImage()) {
				var zcls = this.getZclass();
				zDom.removeClass(n, zcls + '-cnt-ck');
				zDom.removeClass(n, zcls + '-cnt-unck');
				if (this._checkmark)
					zDom.addClass(n, zcls + (checked ? '-cnt-ck' : '-cnt-unck'));
			}
		},
		autocheck: null,
		target: function (target) {
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
			img = '<span class="' + this.getZclass() + '-img"' +
				(this._image ? ' style="background-image:url(' + this._image + ')"' : '')
				+ '></span>';
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
			var n = this.getNode();
			this.domListen_(n, "onMouseOver");
			this.domListen_(n, "onMouseOut");

			if (this.isTopmost()) {
				var anc = this.getSubnode('a');
				this.domListen_(anc, "onFocus", "doFocus_");
				this.domListen_(anc, "onBlur", "doBlur_");
			}
		}
		if (zk.ie && this.isTopmost()) this._fixBtn();
	},
	unbind_: function () {
		if (!this.isDisabled()) {
			var n = this.getNode();
			this.domUnlisten_(n, "onMouseOver");
			this.domUnlisten_(n, "onMouseOut");

			if (this.isTopmost()) {
				var anc = this.getSubnode('a');
				this.domUnlisten_(anc, "onFocus", "doFocus_");
				this.domUnlisten_(anc, "onBlur", "doBlur_");
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
				zDom.removeClass(anc, this.getZclass() + '-body-over');
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
			if (!topmost)
				for (var p = this.parent; p; p = p.parent)
					if (p.$instanceof(zul.menu.Menupopup))
						if (p.isOpen()) p.close({sendOnOpen:true});
						
			this.$class._rmActive(this);				
		}
	},
	_doMouseOver: function (evt) { //not zk.Widget.doMouseOver_
		if (this.$class._isActive(this)) return;
		if (!this.isDisabled()) {
			if (zk.ie && this.isTopmost() && !zDom.isAncestor(this.getSubnode('a'), evt.domTarget))
				return;

			this.$class._addActive(this);
			zWatch.fire('onFloatUp', null, this); //notify all
		}
	},
	_doMouseOut: function (evt) { //not zk.Widget.doMouseOut_
		if (!this.isDisabled()) {
			if (zk.ie) {
				var n = this.getSubnode('a'),
					xy = zDom.revisedOffset(n),
					x = evt.pageX,
					y = evt.pageY,
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
		zDom.removeClass(n, cls);
	}
});
