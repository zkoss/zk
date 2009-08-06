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
			var n = this.$n();
			if (n && !this.isTopmost() && !this.getImage()) {
				var zcls = this.getZclass(),
					$n = jq(n);
				$n.removeClass(zcls + '-cnt-ck')
					.removeClass(zcls + '-cnt-unck');
				if (this._checkmark)
					$n.addClass(zcls + (checked ? '-cnt-ck' : '-cnt-unck'));
			}
		},
		autocheck: null,
		target: function (target) {
			var anc = this.$n('a');
			if (anc) {
				if (this.isTopmost())
					anc = anc.parentNode;
				anc.target = this._target;
			}
		},
		upload: function (v) {
			var n = this.$n();
			if (n) {
				this._cleanUpld();
				if (v && v != 'false') this._initUpld();
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
		var btn = this.$n('b');
		if (btn) {
			var txt = btn.innerHTML, $btn = zk(btn);
			btn.style.width = ($btn.textSize(txt)[0] + $btn.padBorderWidth()) + "px";
		}
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		if (!this.isDisabled()) {
			if (this.isTopmost()) {
				var anc = this.$n('a');
				this.domListen_(anc, "onFocus", "doFocus_")
					.domListen_(anc, "onBlur", "doBlur_");
			}
			if (this._upload) this._initUpld();
		}
		if (zk.ie && this.isTopmost()) this._fixBtn();
	},
	unbind_: function () {
		if (!this.isDisabled()) {
			if (this._upload) this._cleanUpld();
			if (this.isTopmost()) {
				var anc = this.$n('a');
				this.domUnlisten_(anc, "onFocus", "doFocus_")
					.domUnlisten_(anc, "onBlur", "doBlur_");
			}
		}

		this.$supers('unbind_', arguments);
	},
	_initUpld: function () {
		zWatch.listen(zk.ie7 ? {onShow: this, onSize: this} : {onShow: this});
		var v;
		if (v = this._upload)
			this._uplder = new zul.Upload(this, this.isTopmost() ? this.$n() : this.$n('a'), v);
	},
	_cleanUpld: function () {
		var v;
		if (v = this._uplder) {
			zWatch.unlisten(zk.ie7 ? {onShow: this, onSize: this} : {onShow: this});
			this._uplder = null;
			v.destroy();
		}
	},
	onShow: _zkf = function () {
		if (this._uplder)
			this._uplder.sync();
	},
	onSize: zk.ie7 ? _zkf : zk.$void, 
	doClick_: function (evt) {
		if (this._disabled)
			evt.stop();
		else {
			if (!this.$class._isActive(this)) return;

			var topmost = this.isTopmost(),
				anc = this.$n('a');

			if (topmost) {
				jq(anc).removeClass(this.getZclass() + '-body-over');
				anc = anc.parentNode;
			}
			if ('javascript:;' == anc.href) {
				if (this.isAutocheck()) {
					this.setChecked(!this.isChecked());
					this.fire('onCheck', this.isChecked());
				}
				this.fireX(evt);
			} else {
				if (zk.ie && topmost && this.$n().id != anc.id)
					zUtl.go(anc.href, false, anc.target);
					// Bug #1886352 and #2154611
					//Note: we cannot eat onclick. or, <a> won't work
			}
			if (!topmost)
				for (var p = this.parent; p; p = p.parent)
					if (p.$instanceof(zul.menu.Menupopup))
						// if close the popup before choosing a file, the file chooser can't be triggered.
						if (p.isOpen() && !this._uplder)							
							p.close({sendOnOpen:true});
						
			this.$class._rmActive(this);				
		}
	},
	doMouseOver_: function (evt) {
		if (this.$class._isActive(this)) return;
		if (!this.isDisabled()) {
			if (zk.ie && this.isTopmost() && !this._uplder && !jq.isAncestor(this.$n('a'), evt.domTarget))
				return;

			this.$class._addActive(this);
			zWatch.fire('onFloatUp', null, this); //notify all
		}
	},
	doMouseOut_: function (evt) {
		if (!this.isDisabled()) {
			if (zk.ie) {
				var n = this.$n('a'),
					xy = zk(n).revisedOffset(),
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
