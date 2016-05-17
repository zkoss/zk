/* A.js

	Purpose:

	Description:

	History:
		Thu Aug  6 14:31:48     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The basic widgets, such as button and div.
 */
//zk.$package('zul.wgt');

/**
 * The same as HTML A tag.
 * <p>Default {@link #getZclass}: z-a.
 */
zul.wgt.A = zk.$extends(zul.LabelImageWidget, {
	_dir: 'normal',
	//_tabindex: 0,

	$define: {
		/** Returns whether it is disabled.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is disabled.
		 * @param boolean disabled
		 */
		disabled: [
			// Refer from Button.js for the following changes
			// B60-ZK-1176
			// Autodisable should not re-enable when setDisabled(true) is called during onClick
			function (v, opts) {
				if (opts && opts.adbs)
					// called from zul.wgt.ADBS.autodisable
					this._adbs = true;	// Start autodisabling
				else if (!opts || opts.adbs === undefined)
					// called somewhere else (including server-side)
					this._adbs = false;	// Stop autodisabling
				if (!v) {
					if (this._adbs) {
						// autodisable is still active, allow enabling
						this._adbs = false;
					} else if (opts && opts.adbs === false)
						// ignore re-enable by autodisable mechanism
						return this._disabled;
				}
			return v;
			},
			function (v) {
				var self = this,
					doDisable = function () {
						if (self.desktop) {
							jq(self.$n()).attr('disabled', v); // use jQuery's attr() instead of dom.disabled for non-button element. Bug ZK-2146
						}
					};
				doDisable();
			}
		],
		/** Returns the direction.
		 * <p>Default: "normal".
		 * @return String
		 */
		/** Sets the direction.
		 * @param String dir either "normal" or "reverse".
		 */
		dir: _zkf = function () {
			var n = this.$n();
			if (n) n.innerHTML = this.domContent_();
		},
		/** Returns the href that the browser shall jump to, if an user clicks
		 * this button.
		 * <p>Default: null. If null, the button has no function unless you
		 * specify the onClick event listener.
		 * <p>If it is not null, the onClick event won't be sent.
		 * @return String
		 */
		/** Sets the href.
		 * @param String href
		 */
		href: function (v) {
			var n = this.$n();
			if (n) n.href = v || '';
		},
		/** Returns the target frame or window.
		 *
		 * <p>Note: it is useful only if href ({@link #setHref}) is specified
		 * (i.e., use the onClick listener).
		 *
		 * <p>Default: null.
		 * @return String
		 */
		/** Sets the target frame or window.
		 * @param String target the name of the frame or window to hyperlink.
		 */
		target: function (v) {
			var n = this.$n();
			if (n) n.target = v || '';
		},
		/** Returns a list of component IDs that shall be disabled when the user
		 * clicks this anchor.
		 *
		 * <p>To represent the anchor itself, the developer can specify <code>self</code>.
		 * For example,
		 * <pre><code>
		 * anchor.setId('ok');
		 * wgt.setAutodisable('self,cancel');
		 * </code></pre>
		 * is the same as
		 * <pre><code>
		 * anchor.setId('ok');
		 * wgt.setAutodisable('ok,cancel');
		 * </code></pre>
		 * that will disable
		 * both the ok and cancel anchors when an user clicks it.
		 *
		 * <p>The anchor being disabled will be enabled automatically
		 * once the client receives a response from the server.
		 * In other words, the server doesn't notice if a anchor is disabled
		 * with this method.
		 *
		 * <p>However, if you prefer to enable them later manually, you can
		 * prefix with '+'. For example,
		 * <pre><code>
		 * anchor.setId('ok');
		 * wgt.setAutodisable('+self,+cancel');
		 * </code></pre>
		 *
		 * <p>Then, you have to enable them manually such as
		 * <pre><code>if (something_happened){
		 *  ok.setDisabled(false);
		 *  cancel.setDisabled(false);
		 *</code></pre>
		 *
		 * <p>Default: null.
		 * @return String
		 */
		/** Sets whether to disable the anchor after the user clicks it.
		 * @param String autodisable
		 */
		autodisable: null
	},

	// super//
	bind_: function () {
		this.$supers(zul.wgt.A, 'bind_', arguments);
		if (!this._disabled) {
			var n = this.$n();
			this.domListen_(n, 'onFocus', 'doFocus_')
				.domListen_(n, 'onBlur', 'doBlur_');
		}
	},
	unbind_: function () {
		var n = this.$n();
		this.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');

		this.$supers(zul.wgt.A, 'unbind_', arguments);
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.getImage(),
			iconSclass = this.domIcon_();
		if (!img && !iconSclass)
			return label;

		if (!img) {
			img = iconSclass;
		} else
			img = '<img src="' + img + '" align="absmiddle" />'
				+ (iconSclass ? ' ' + iconSclass : '');
		return this.getDir() == 'reverse' ? label + img : img + label;
	},
	domAttrs_: function (no) {
		var attr = this.$supers('domAttrs_', arguments),
			v;
		if (v = this.getTarget())
			attr += ' target="' + v + '"';
		if (v = this.getHref())
			attr += ' href="' + v + '"';
		else
			attr += ' href="javascript:;"';
		if (this._disabled)
			attr += ' disabled="disabled"';
		return attr;
	},
	doClick_: function (evt) {
		var href = this.getHref();
		// ZK-2506: use iframe to open a 'mailto' href
		if (href && href.toLowerCase().startsWith('mailto:')) {
			var ifrm = jq.newFrame('mailtoFrame', href, null);
			jq(ifrm).remove();
			evt.stop();
		}
		// Bug ZK-2422
		if (zk.ie < 11 && !href) {
			evt.stop({dom: true});
		}
		if (this._disabled)
			evt.stop(); // Prevent browser default
		else {
			zul.wgt.ADBS.autodisable(this);

			this.fireX(evt);
			if (!evt.stopped)
				this.$super('doClick_', evt, true);
		}
			// Unlike DOM, we don't propagate to parent (so do not call $supers)
	}
});

