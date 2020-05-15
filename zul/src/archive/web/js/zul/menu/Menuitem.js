/* Menuitem.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:33     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {

	function _initUpld(wgt) {
		zWatch.listen({onShow: wgt});
		var v;
		if (v = wgt._upload)
			wgt._uplder = new zul.Upload(wgt, wgt._getUploadRef(), v);
	}

	function _cleanUpld(wgt) {
		var v;
		if (v = wgt._uplder) {
			zWatch.unlisten({onShow: wgt});
			wgt._uplder = null;
			v.destroy();
		}
	}

(/**
 * A single choice in a {@link Menupopup} element.
 * It acts much like a button but it is rendered on a menu.
 *
 * <p>Default {@link #getZclass}: z-menuitem.
 */
zul.menu.Menuitem = zk.$extends(zul.LabelImageWidget, {
	_value: '',

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
		disabled: [
			//B60-ZK-1176
			// Autodisable should not re-enable when setDisabled(true) is called during onClick
			function (v, opts) {
				if (opts && opts.adbs)
					// called from zul.wgt.ADBS.autodisable
					this._adbs = true;	// Start autodisabling
				else if (!opts || opts.adbs === undefined)
					// called somewhere else (including server-side)
					this._adbs = false;	// Stop autodisabling
				if (!v) {
					if (this._adbs)
					// autodisable is still active, enable allowed
					this._adbs = false;
					else if (opts && opts.adbs === false)
					// ignore re-enable by autodisable mechanism
					return this._disabled;
				}
				return v;
			},
			function (v, opts) {
				this.rerender(opts && opts.skip ? -1 : 0); //bind and unbind
			}
		],
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
			var n = this.$n();
			if (n && !this.isTopmost() && !this.getImage()) {
				var $n = jq(n);
				$n[checked ? 'addClass' : 'removeClass'](this.$s('checked'));
				if (this._checkmark)
					$n.addClass(this.$s('checkable'));
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
		/** Returns a list of component IDs that shall be disabled when the user
		 * clicks this menuitem.
		 *
		 * <p>To represent the menuitem itself, the developer can specify <code>self</code>.
		 * For example, <code>&lt;menuitem id="ok" autodisable="self,cancel"/></code>
		 * is the same as <code>&lt;menuitem id="ok" autodisable="ok,cancel"/></code>
		 * that will disable
		 * both the ok and cancel menuitem when an user clicks it.
		 *
		 * <p>The menuitem being disabled will be enabled automatically
		 * once the client receives a response from the server.
		 * In other words, the server doesn't notice if a menuitem is disabled
		 * with this method.
		 *
		 * <p>However, if you prefer to enable them later manually, you can
		 * prefix with '+'. For example,
		 * <code>&lt;menuitem id="ok" autodisable="+self,+cancel"/></code>
		 *
		 * <p>Then, you have to enable them manually such as
		 * <pre><code>if (something_happened){
		 *  ok.setDisabled(false);
		 *  cancel.setDisabled(false);
		 *</code></pre>
		 *
		 * <p>Default: null.
		 * @since 5.0.7
		 * @return String
		 */
		/** Sets whether to disable the button after the user clicks it.
		 * @since 5.0.7
		 * @param String autodisable
		 */
		autodisable: null,
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
				_cleanUpld(this);
				if (v && v != 'false') _initUpld(this);
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
		this.$supers('beforeParentChanged_', arguments);
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = this.isDisabled() ? this.$s('disabled') : '';
			if (added) scls += (scls ? ' ' : '') + added;
			added = (!this.getImage() && this.isCheckmark()) ?
						this.$s('checkable') + (this.isChecked() ? ' ' + this.$s('checked') : '') : '';
			if (added) scls += (scls ? ' ' : '') + added;
		}
		return scls;
	},
	domContent_: function () {
		var label = '<span class="' + this.$s('text') + '">'
					+ (zUtl.encodeXML(this.getLabel())) + '</span>',
			icon = '<i class="' + this.$s('icon') + ' z-icon-check" aria-hidden="true"></i>',
			img = this.getImage(),
			iconSclass = this.domIcon_();

		if (img)
			img = '<img src="' + img + '" class="' + this.$s('image') + '" align="absmiddle" alt="" aria-hidden="true" />'
				+ (iconSclass ? ' ' + iconSclass : '');
		else {
			if (iconSclass) {
				img = iconSclass;
			} else {
				img = '<img ' + (this.isTopmost() ? 'style="display:none"' : '')
					+ ' src="data:image/png;base64,R0lGODlhAQABAIAAAAAAAAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==" class="' + this.$s('image') + '" align="absmiddle" alt="" aria-hidden="true" />';
			}
		}
		return img + (this.isAutocheck() || this.isCheckmark() ? icon : '') + label;
	},
	/** Returns the {@link Menubar} that contains this menuitem, or null if not available.
	 * @return zul.menu.Menubar
	 */
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	bind_: function () {
		this.$supers(zul.menu.Menuitem, 'bind_', arguments);

		if (!this.isDisabled()) {
			var anc = this.$n('a');
			if (this.isTopmost()) {
				this.domListen_(anc, 'onFocus', 'doFocus_')
					.domListen_(anc, 'onBlur', 'doBlur_');
			}
			this.domListen_(anc, 'onMouseEnter');
			if (this._upload) _initUpld(this);
		}
	},
	unbind_: function () {
		if (!this.isDisabled()) {
			if (this._upload) _cleanUpld(this);
			var anc = this.$n('a');
			if (this.isTopmost()) {
				this.domUnlisten_(anc, 'onFocus', 'doFocus_')
					.domUnlisten_(anc, 'onBlur', 'doBlur_');
			}
			this.domUnlisten_(anc, 'onMouseEnter');
		}

		this.$supers(zul.menu.Menuitem, 'unbind_', arguments);
	},

	doClick_: function (evt) {
		if (this._disabled)
			evt.stop();
		else {
			if (!this._canActivate(evt)) return;
			if (!this._upload)
				zul.wgt.ADBS.autodisable(this);
			else if (!zk.ie || zk.ie > 10) {// ZK-2471
				if (!zk.chrome || evt.domTarget.type != 'file') //ZK-3089
					this._uplder.openFileDialog();
			}

			var topmost = this.isTopmost(),
				anc = this.$n('a');

			if (anc.href.startsWith('javascript:')) {
				if (this.isAutocheck()) {
					this.setChecked(!this.isChecked());
					this.fire('onCheck', this.isChecked());
				}
				this.fireX(evt);
				//ZK-2679: prevent default behavior when upload=false
				if (!this._upload) //if upload=true, it won't fire onbeforeunload in IE <= 10
					evt.stop(); //if we stop evt when upload=true, it won't open upload window in IE <= 10
			} else if (anc.href.toLowerCase().startsWith('mailto:')) { // ZK-2506
				var ifrm = jq.newFrame('mailtoFrame', anc.href, null);
				jq(ifrm).remove();
				evt.stop();
			} else {
				if (zk.ie < 11 && topmost && this.$n().id != anc.id)
					zUtl.go(anc.href, {target: anc.target});
					// Bug #1886352 and #2154611
					//Note: we cannot eat onclick. or, <a> won't work

				if (zk.gecko && topmost && this.$n().id != anc.id) {
					zUtl.go(anc.href, {target: anc.target});
					evt.stop();
					// Bug #2154611 we shall eat the onclick event, if it is FF3.
				}
			}
			if (!topmost) {
				for (var p = this.parent; p; p = p.parent) {
					if (p.$instanceof(zul.menu.Menupopup)) {
						// if close the popup before choosing a file, the file chooser can't be triggered.
						if (!p.isOpen() || this._uplder || p._keepOpen /*Bug #2911385 && !this._popup*/)
							break;
						this._updateHoverImage(); // remove hover image
						p.close({sendOnOpen: true});
					} else if (!p.$instanceof(zul.menu.Menu)) //either menubar or non-menu*
						break;
					else
						p._updateHoverImage(); // remove parent Menu hover image
				}
			}

			var menubar;
			if (zk.webkit && (menubar = this.getMenubar()) && menubar._autodrop)
				menubar._noFloatUp = true;
				//_noFloatUp used in Menu.js to fix Bug 1852304
			this.$super('doClick_', evt, true);
		}
	},
	_canActivate: function (evt) {
		return !this.isDisabled() && (!zk.ie < 11 || !this.isTopmost() || this._uplder
				|| jq.isAncestor(this.$n('a'), evt.domTarget));
	},
	_getUploadRef: function () {
		return this.$n('a');
	},
	_doMouseEnter: function (evt) {
		if (zul.menu._nOpen || this.isTopmost())
			zWatch.fire('onFloatUp', this); //notify all
	},
	deferRedrawHTML_: function (out) {
		var tag = this.isTopmost() ? 'td' : 'li';
		out.push('<', tag, this.domAttrs_({domClass: 1}), ' class="z-renderdefer"></', tag,'>');
	},
	//@Override
	getImageNode: function () {
		if (!this._eimg && (this._image || this._hoverImage)) {
			var n = this.$n();
			if (n)
				this._eimg = this.$n('a').firstChild;
		}
		return this._eimg;
	}
}, {
	_isActive: function (wgt) {
		return jq(wgt.$n()).hasClass(wgt.$s('hover'));
	},
	_addActive: function (wgt) {
		var top = wgt.isTopmost();
		jq(wgt.$n()).addClass(wgt.$s('hover'));
		if (!top && wgt.parent.parent.$instanceof(zul.menu.Menu))
			this._addActive(wgt.parent.parent);
	},
	_rmActive: function (wgt) {
		return jq(wgt.$n()).removeClass(wgt.$s('hover'));
	}
})).prototype['onShow'] = function () {
	if (this._uplder)
		this._uplder.sync();
};

})();
