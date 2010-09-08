/* Menuitem.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:33     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A single choice in a {@link Menupopup} element.
 * It acts much like a button but it is rendered on a menu.
 * 
 * <p>Default {@link #getZclass}: z-menu-item.
 */
zul.menu.Menuitem = zk.$extends(zul.LabelImageWidget, {
	_value: "",

	$define: {
		/** Returns whether the check mark shall be displayed in front
		 * of each item.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether the check mark shall be displayed in front
		 * of each item.
		 * @param boolean checkmark
		 */
		checkmark: _zkf = function () {
			this.rerender();
		},
		/** Returns whether it is disabled.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is disabled.
		 * @param boolean disabled
		 */
		disabled: _zkf,
		/** Returns the href.
		 * <p>Default: null. If null, the button has no function unless you
		 * specify the onClick handler.
		 * @return String
		 */
		/** Sets the href.
		 * @param String href
		 */
		href: _zkf,
		/** Returns the value.
		 * <p>Default: "".
		 * @return String
		 */
		/** Sets the value.
		 * @param String value
		 */
		value: null,
		/** Returns whether it is checked.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is checked.
		 * <p> This only applies when {@link #isCheckmark()} = true.
		 * @param boolean checked
		 */
		checked: function (checked) {
			if (checked)
				this._checkmark = checked;
			var n = this.$n('a');
			if (n && !this.isTopmost() && !this.getImage()) {
				var zcls = this.getZclass(),
					$n = jq(n);
				$n.removeClass(zcls + '-cnt-ck')
					.removeClass(zcls + '-cnt-unck');
				if (this._checkmark)
					$n.addClass(zcls + (checked ? '-cnt-ck' : '-cnt-unck'));
			}
		},
		/** Returns whether the menuitem check mark will update each time
		 * the menu item is selected.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether the menuitem check mark will update each time
		 * the menu item is selected.
		 * <p> This only applies when {@link #isCheckmark()} = true.
		 * @param boolean autocheck
		 */
		autocheck: null,
		/** Returns the target frame or window.
		 * <p>Note: it is useful only if href ({@link #setHref}) is specified
		 * (i.e., use the onClick listener).
		 * <p>Default: null.
		 * @return String
		 */
		/** Sets the target frame or window.
		 * @param String target the name of the frame or window to hyperlink.
		 */
		target: function (target) {
			var anc = this.$n('a');
			if (anc) {
				if (this.isTopmost())
					anc = anc.parentNode;
				anc.target = this._target;
			}
		},
		/** Returns non-null if this button is used for file upload, or null otherwise.
		 * Refer to {@link #setUpload} for more details.
		 * @return String
		 */
		/** Sets the JavaScript class at the client to handle the upload if this
		 * button is used for file upload.
		 * <p>Default: null.
		 *  
		 * @param String upload a JavaScript class to handle the file upload
		 * at the client, or "true" if the default class is used,
		 * or null or "false" to disable the file download (and then
		 * this button behaves like a normal button).
		 */
		upload: function (v) {
			var n = this.$n();
			if (n) {
				this._cleanUpld();
				if (v && v != 'false') this._initUpld();
			}
		}
	},
	/** Returns whether this is an top-level menu, i.e., not owning
	 * by another {@link Menupopup}.
	 * @return boolean
	 */
	isTopmost: function () {
		return this._topmost;
	},
	beforeParentChanged_: function (newParent) {
		this._topmost = newParent && !(newParent.$instanceof(zul.menu.Menupopup));
		this.$supers("beforeParentChanged_", arguments);
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
	/** Returns the {@link Menubar} that contains this menuitem, or null if not available.
	 * @return zul.menu.Menubar
	 */
	getMenubar: zul.menu.Menu.prototype.getMenubar,
	bind_: function () {
		this.$supers(zul.menu.Menuitem, 'bind_', arguments);

		if (!this.isDisabled()) {
			if (this.isTopmost()) {
				var anc = this.$n('a');
				this.domListen_(anc, "onFocus", "doFocus_")
					.domListen_(anc, "onBlur", "doBlur_");
			}
			if (this._upload) this._initUpld();
		}
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

		this.$supers(zul.menu.Menuitem, 'unbind_', arguments);
	},
	_initUpld: function () {
		zWatch.listen(zk.ie7_ ? {onShow: this, onSize: this} : {onShow: this});
		var v;
		if (v = this._upload)
			this._uplder = new zul.Upload(this, this.isTopmost() ? this.$n() : this.$n('a'), v);
	},
	_cleanUpld: function () {
		var v;
		if (v = this._uplder) {
			zWatch.unlisten(zk.ie7_ ? {onShow: this, onSize: this} : {onShow: this});
			this._uplder = null;
			v.destroy();
		}
	},
	onShow: _zkf = function () {
		if (this._uplder)
			this._uplder.sync();
	},
	onSize: zk.ie7_ ? _zkf : zk.$void, 
	doClick_: function (evt) {
		if (this._disabled)
			evt.stop();
		else {
			if (!this._canActivate(evt)) return;

			var topmost = this.isTopmost(),
				anc = this.$n('a');

			if (topmost) {
				jq(anc).removeClass(this.getZclass() + '-body-over');
				anc = anc.parentNode;
			}
			if (anc.href.startsWith('javascript:')) {
				if (this.isAutocheck()) {
					this.setChecked(!this.isChecked());
					this.fire('onCheck', this.isChecked());
				}
				this.fireX(evt);
			} else {
				if (zk.ie && topmost && this.$n().id != anc.id)
					zUtl.go(anc.href, {target: anc.target});
					// Bug #1886352 and #2154611
					//Note: we cannot eat onclick. or, <a> won't work
					
				if (zk.gecko3 && topmost && this.$n().id != anc.id) {				
					zUtl.go(anc.href, {target: anc.target});
					evt.stop();
					// Bug #2154611 we shall eat the onclick event, if it is FF3.
				}
			}
			if (!topmost)
				for (var p = this.parent; p; p = p.parent)
					if (p.$instanceof(zul.menu.Menupopup)) {
						// if close the popup before choosing a file, the file chooser can't be triggered.
						if (!p.isOpen() || this._uplder /*Bug #2911385 && !this._popup*/)
							break;
						p.close({sendOnOpen:true});
					} else if (!p.$instanceof(zul.menu.Menu)) //either menubar or non-menu*
						break;

			var menubar;
			if (zk.safari && (menubar=this.getMenubar()) && menubar._autodrop)
				menubar._noFloatUp = true;
				//_noFloatUp used in Menu.js to fix Bug 1852304

			this.$class._rmActive(this);
			this.$super('doClick_', evt, true);
		}
	},
	_canActivate: function (evt) {
		return !this.isDisabled() && (!zk.ie || !this.isTopmost() || this._uplder
				|| jq.isAncestor(this.$n('a'), evt.domTarget));
	},
	doMouseOver_: function (evt) {
		var menubar = this.getMenubar();
		if (menubar) {
			menubar._bOver = true;
			menubar._noFloatUp = false;
		}
		if (!this.$class._isActive(this) && this._canActivate(evt)) {
			this.$class._addActive(this);
			if (zul.menu._nOpen || !this.isTopmost())
				zWatch.fire('onFloatUp', this); //notify all
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		var menubar = this.getMenubar();
		if (menubar) {
			menubar._bOver = false;
			menubar._closeOnOut();
		}
		if (!this.isDisabled()) {
			var deact = !zk.ie;
			if (!deact) {
				var n = this.$n('a'),
					xy = zk(n).revisedOffset(),
					x = evt.pageX,
					y = evt.pageY,
					diff = this.isTopmost() ? 1 : 0;
				deact = x - diff <= xy[0] || x > xy[0] + n.offsetWidth
					|| y - diff <= xy[1] || y > xy[1] + n.offsetHeight + (zk.ie ? -1 : 0);
			}
			if (deact)
				this.$class._rmActive(this);
		}
		this.$supers('doMouseOut_', arguments);
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
