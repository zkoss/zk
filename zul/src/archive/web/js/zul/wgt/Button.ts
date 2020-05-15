/* Button.js

	Purpose:

	Description:

	History:
		Sat Nov  8 22:58:16     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _initUpld(wgt: zk.Widget): void {
		var v;
		if (v = wgt._upload)
			wgt._uplder = new zul.Upload(wgt, null, v);
	}

	function _cleanUpld(wgt: zk.Widget): void {
		var v;
		if (v = wgt._uplder) {
			wgt._uplder = null;
			v.destroy();
		}
	}

let _zkf: () => void;
var Button =
/**
 * A button.
 * <p>Default {@link #getZclass}: z-button.
 */
zul.wgt.Button = zk.$extends(zul.LabelImageWidget, {
	_orient: 'horizontal',
	_dir: 'normal',
	_type: 'button',

	$define: {
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
		href: null,
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
		target: null,
		/** Returns the direction.
		 * <p>Default: "normal".
		 * @return String
		 */
		/** Sets the direction.
		 * @param String dir either "normal" or "reverse".
		 */
		dir: _zkf = function (this: zk.Widget) {
			this.updateDomContent_();
		},
		/** Returns the orient.
		 * <p>Default: "horizontal".
		 * @return String
		 */
		/** Sets the orient.
		 * @param String orient either "horizontal" or "vertical".
		 */
		orient: _zkf,
		/** Returns the button type.
		 * <p>Default: "button".
		 * @return String
		 */
		/** Sets the button type.
		 * @param String type either "button", "submit" or "reset".
		 */
		type: _zkf,
		/** Returns whether it is disabled.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is disabled.
		 * @param boolean disabled
		 */
		disabled: [
			// B60-ZK-1176
			// Autodisable should not re-enable when setDisabled(true) is called during onClick
			function (this: zk.Widget, v: boolean, opts: Partial<{adbs: boolean}>) {
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
			function (this: zk.Widget, v: boolean) {
				var doDisable = (): void => {
						if (this.desktop) {
							jq(this.$n()).attr('disabled', v ? 'disabled' : null); // use jQuery's attr() instead of dom.disabled for non-button element. Bug ZK-2146
							// B70-ZK-2059: Initialize or clear upload when disabled attribute changes.
							if (this._upload)
								v ? _cleanUpld(this) : _initUpld(this);
						}
					};

				// ZK-2042: delay the setting when the button's type is submit
				if (this._type == 'submit')
					setTimeout(doDisable, 50);
				else
					doDisable();
			}
		],
		/*	 B70-ZK-2031: Use LabelImageWidget's define instead
		image: function (v) {
			if (v && this._preloadImage) zUtl.loadImage(v);
			var n = this.getImageNode();
			if (n)
				n.src = v || '';
		},*/
		/** Returns a list of component IDs that shall be disabled when the user
		 * clicks this button.
		 *
		 * <p>To represent the button itself, the developer can specify <code>self</code>.
		 * For example,
		 * <pre><code>
		 * button.setId('ok');
		 * wgt.setAutodisable('self,cancel');
		 * </code></pre>
		 * is the same as
		 * <pre><code>
		 * button.setId('ok');
		 * wgt.setAutodisable('ok,cancel');
		 * </code></pre>
		 * that will disable
		 * both the ok and cancel buttons when an user clicks it.
		 *
		 * <p>The button being disabled will be enabled automatically
		 * once the client receives a response from the server.
		 * In other words, the server doesn't notice if a button is disabled
		 * with this method.
		 *
		 * <p>However, if you prefer to enable them later manually, you can
		 * prefix with '+'. For example,
		 * <pre><code>
		 * button.setId('ok');
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
		/** Sets whether to disable the button after the user clicks it.
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
		 * <p>For example, the following example declares a button for file upload:
		 * <pre><code>
		 * button.setLabel('Upload');
		 * button.setUpload('true');
		 * </code></pre>
		 *
		 * <p>If you want to customize the handling of the file upload at
		 * the client, you can specify a JavaScript class when calling
		 * this method:
		 * <code>button.setUpload('foo.Upload');</code>
		 *
		 * <p> Another options for the upload can be specified as follows:
		 *  <pre><code>button.setUpload('true,maxsize=-1,native');</code></pre>
		 *  <ul>
		 *  <li>maxsize: the maximal allowed upload size of the component, in kilobytes, or
		 * a negative value if no limit.</li>
		 *  <li>native: treating the uploaded file(s) as binary, i.e., not to convert it to
		 * image, audio or text files.</li>
		 *  </ul>
		 *
		 * @param String upload a JavaScript class to handle the file upload
		 * at the client, or "true" if the default class is used,
		 * or null or "false" to disable the file download (and then
		 * this button behaves like a normal button).
		 */
		upload(v: string) {
			var n = this.$n();
			if (n && !this._disabled) {
				_cleanUpld(this);
				if (v && v != 'false') _initUpld(this);
			}
		}
	},

	//super//
	focus_(timeout?: number): boolean {
		// Bug ZK-1295 and ZK-2935: Disabled buttons cannot regain focus by re-enabling and then setting focus
		let btn = this.$n() as HTMLButtonElement;
		if (btn && btn.disabled) {
			if (!this._delayFocus) {
				this._delayFocus = true;
				setTimeout(() => {
					if (this.desktop && !this.isDisabled()) {
						if (!zk.focusBackFix || !this._upload) {
							zk(this.$n()).focus(timeout);
						}
					}
					this._delayFocus = null;
				}, 0);
			}
			return false;
		}

		// Bug ZK-354: refer to _docMouseDown in mount.js for details
		if (!zk.focusBackFix || !this._upload) {
			zk(btn).focus(timeout);
		}
		return true;
	},

	domContent_(): string {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.getImage(),
			iconSclass = this.domIcon_();
		if (!img && !iconSclass) return label;

		if (!img) img = iconSclass;
		else
			img = `<img class="${this.$s('image')}" src="${img}" alt="" aria-hidden="true" />${iconSclass ? ' ' + iconSclass : ''}`;
		var space = 'vertical' == this.getOrient() ? '<br/>' : ' ';
		return this.getDir() == 'reverse' ?
			label + space + img : img + space + label;
	},
	onShow() {
		// ZK-2233: should sync upload position when button showed
		if (this.$n() && !this._disabled && this._uplder) {
			this._uplder.sync();
		}
	},
	bind_() {
		this.$supers(Button, 'bind_', arguments);

		var n = this.$n();
		this.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_');
		zWatch.listen({onShow: this});

		if (!this._disabled && this._upload) _initUpld(this);
	},
	unbind_() {
		_cleanUpld(this);

		var n = this.$n();
		this.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');
		zWatch.unlisten({onShow: this});

		this.$supers(Button, 'unbind_', arguments);
	},
	doClick_(evt: zk.Event) {
		if (!evt.domEvent) // mobile will trigger doClick twice
			return;

		if (!this._disabled) {
			if (!this._upload)
				zul.wgt.ADBS.autodisable(this);
			else if (!zk.ie || zk.ie > 10) // ZK-2471
				this._uplder.openFileDialog();

			this.fireX(evt);

			if (!evt.stopped) {
				var href = this._href,
					isMailTo = href ? href.toLowerCase().startsWith('mailto:') : false;

				if (href) {
					// ZK-2506: use iframe to open a 'mailto' href
					if (isMailTo) {
						var ifrm = jq.newFrame('mailtoFrame', href, null);
						jq(ifrm).remove();
					} else {
						zUtl.go(href, {target: this._target || (evt.data.ctrlKey ? '_blank' : '')});
					}
				}
				this.$super('doClick_', evt, true);
			}
		}
		//Unlike DOM, we don't proprogate to parent (otherwise, onClick
		//will fired)
	},
	setFlexSize_(sz: Partial<{height: string; width: string}>) { //Bug #2870652
		var n = this.$n() as HTMLElement;
		if (sz.height !== undefined) {
			if (sz.height == 'auto')
				n.style.height = '';
			else if (sz.height != '')
				n.style.height = jq.px0(parseInt(sz.height));
			else
				n.style.height = this._height ? this._height : '';
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto')
				n.style.width = '';
			else if (sz.width != '')
				n.style.width = jq.px0(parseInt(sz.width));
			else
				n.style.width = this._width ? this._width : '';
		}
	}
});
//handle autodisabled buttons
zul.wgt.ADBS = zk.$extends(zk.Object, {
	$init(ads: zk.Widget[]) {
		this._ads = ads;
	},
	onResponse() {
		for (var ads = this._ads, ad: zk.Widget; ad = ads.shift();) {
			// B60-ZK-1176: distinguish from other usages
			ad.setDisabled(false, {adbs: false, skip: true});
			if (zk.chrome) ad.domListen_(ad.$n(), 'onBlur', 'doBlur_'); //ZK-2739: prevent chrome fire onBlur event after autodisabled
		}
		zWatch.unlisten({onResponse: this});
	}
},{ //static
	/* Disable Targets and re-enable after response
	 * @param zk.Widget wgt
	 */
	autodisable(wgt: zk.Widget) {
		var ads = wgt._autodisable,
			aded: zk.Widget[] | undefined,
			uplder;
		if (ads) {
			if (zk.chrome) wgt.domUnlisten_(wgt.$n(), 'onBlur', 'doBlur_'); //ZK-2739: prevent chrome fire onBlur event after autodisabled
			ads = ads.split(',');
			for (var j = ads.length; j--;) {
				var ad = ads[j].trim();
				if (ad) {
					var perm = ad.charAt(0) == '+' ;
					if (perm)
						ad = ad.substring(1);
					ad = 'self' == ad ? wgt : wgt.$f(ad);
					//B50-3304877: autodisable and Upload
					if (ad == wgt) { //backup uploader before disable
						uplder = wgt._uplder;
						wgt._uplder = null;
						wgt._autodisable_self = true;
					}
					if (ad && !ad._disabled) {
						// B60-ZK-1176: distinguish from other usages
						ad.setDisabled(true, {adbs: true, skip: true});
						if (wgt.inServer) {
							if (perm)
								ad.smartUpdate('disabled', true);
							else if (!aded) aded = [ad];
							else aded.push(ad);
						}
					}
				}
			}
		}
		if (aded) {
			let adbs = new zul.wgt.ADBS(aded);
			if (uplder) {
				uplder._aded = adbs;
				wgt._uplder = uplder;//zul.Upload.sendResult came on it.
			} else if (wgt.isListen('onClick', {asapOnly: true}))
				zWatch.listen({onResponse: adbs});
			else
				setTimeout(() => adbs.onResponse(), 800);
		}
	}
});

})();
