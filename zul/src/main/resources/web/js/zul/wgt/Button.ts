/* Button.ts

	Purpose:

	Description:

	History:
		Sat Nov  8 22:58:16     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function _initUpld(wgt: zul.wgt.Button): void {
	var v: string | undefined;
	if (v = wgt._upload)
		wgt._uplder = new zul.Upload(wgt, null, v);
}

function _cleanUpld(wgt: zul.wgt.Button): void {
	var v: zul.Upload | null | undefined;
	if (v = wgt._uplder) {
		wgt._uplder = null;
		v.destroy();
	}
}

/**
 * A button.
 * <p>Default {@link #getZclass}: z-button.
 */
@zk.WrapClass('zul.wgt.Button')
export class Button extends zul.LabelImageWidget {
	private _orient = 'horizontal';
	private _dir = 'normal';
	private _type = 'button';
	private _href?: string;
	private _target?: string;
	public _upload?: string;
	private _delayFocus?: boolean | null;

	/** Returns the href that the browser shall jump to, if an user clicks
	 * this button.
	 * <p>Default: null. If null, the button has no function unless you
	 * specify the onClick event listener.
	 * <p>If it is not null, the onClick event won't be sent.
	 * @return String
	 */
	public getHref(): string | undefined {
		return this._href;
	}

	/** Sets the href.
	 * @param String href
	 */
	public setHref(href: string): this {
		this._href = href;
		return this;
	}

	/** Returns the target frame or window.
	 *
	 * <p>Note: it is useful only if href ({@link #setHref}) is specified
	 * (i.e., use the onClick listener).
	 *
	 * <p>Default: null.
	 * @return String
	 */
	public getTarget(): string | undefined {
		return this._target;
	}

	/** Sets the target frame or window.
	 * @param String target the name of the frame or window to hyperlink.
	 */
	public setTarget(target: string): this {
		this._target = target;
		return this;
	}

	/** Returns the direction.
	 * <p>Default: "normal".
	 * @return String
	 */
	public getDir(): string {
		return this._dir;
	}

	/** Sets the direction.
	 * @param String dir either "normal" or "reverse".
	 */
	public setDir(v: string, opts?: Record<string, boolean>): this {
		const o = this._dir;
		this._dir = v;

		if (o !== v || (opts && opts.force)) {
			this.updateDomContent_();
		}

		return this;
	}

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 * @return String
	 */
	public getOrient(): string {
		return this._orient;
	}

	/** Sets the orient.
	 * @param String orient either "horizontal" or "vertical".
	 */
	public setOrient(v: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = v;

		if (o !== v || (opts && opts.force)) {
			this.updateDomContent_();
		}

		return this;
	}

	/** Returns the button type.
	 * <p>Default: "button".
	 * @return String
	 */
	public getType(): string {
		return this._type;
	}

	/** Sets the button type.
	 * @param String type either "button", "submit" or "reset".
	 */
	public setType(v: string, opts?: Record<string, boolean>): this {
		const o = this._type;
		this._type = v;

		if (o !== v || (opts && opts.force)) {
			this.updateDomContent_();
		}

		return this;
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @return boolean
	 */
	public isDisabled(): boolean | undefined {
		return this._disabled;
	}

	/** Sets whether it is disabled.
	 * @param boolean disabled
	 */
	public setDisabled(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;

		// B60-ZK-1176
		// Autodisable should not re-enable when setDisabled(true) is called during onClick
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
				v = this._disabled as boolean;
		}
		this._disabled = v;

		if (o !== v || (opts && opts.force)) {
			var doDisable = (): void => {
					if (this.desktop) {
						jq(this.$n()!).attr('disabled', v ? 'disabled' : null); // use jQuery's attr() instead of dom.disabled for non-button element. Bug ZK-2146
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

		return this;
	}

	/*     B70-ZK-2031: Use LabelImageWidget's define instead
	 image: function (v) {
	 if (v && this._preloadImage) zUtl.loadImage(v);
	 var n = this.getImageNode();
	 if (n)
	 n.src = v || '';
	 },*/
	public setAutodisable(v: string): this {
		this._autodisable = v;
		return this;
	}

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
	public getAutodisable(): string | undefined {
		return this._autodisable;
	}

	/** Returns non-null if this button is used for file upload, or null otherwise.
	 * Refer to {@link #setUpload} for more details.
	 * @return String
	 */
	public getUpload(): string | undefined {
		return this._upload;
	}

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
	public setUpload(v: string, opts?: Record<string, boolean>): this {
		const o = this._upload;
		this._upload = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n && !this._disabled) {
				_cleanUpld(this);
				if (v && v != 'false') _initUpld(this);
			}
		}

		return this;
	}

	/**
	 * Returns the file(s) belongs to this button if any.
	 * @since 10.0.0
	 */
	public getFile(): FileList | null {
		return this._uplder!.getFile();
	}

	//super//
	public override focus_(timeout?: number): boolean {
		// Bug ZK-1295 and ZK-2935: Disabled buttons cannot regain focus by re-enabling and then setting focus
		let btn = this.$n() as HTMLButtonElement;
		if (btn && btn.disabled) {
			if (!this._delayFocus) {
				this._delayFocus = true;
				zk.currentFocus = this;
				setTimeout(() => {
					// ZK-4865: do not focus if currentFocus has been changed during delay
					if (this.desktop && !this.isDisabled() && zk.currentFocus == this) {
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
	}

	protected override domContent_(): string {
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
	}

	public onShow(): void {
		// ZK-2233: should sync upload position when button showed
		if (this.$n() && !this._disabled && this._uplder) {
			this._uplder.sync();
		}
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		var n = this.$n()!;
		this.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_');
		zWatch.listen({onShow: this});

		if (!this._disabled && this._upload) _initUpld(this);
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		_cleanUpld(this);

		var n = this.$n()!;
		this.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');
		zWatch.unlisten({onShow: this});

		super.unbind_(skipper, after, keepRod);
	}

	public override doClick_(evt: zk.Event): void {
		if (!evt.domEvent) // mobile will trigger doClick twice
			return;

		if (!this._disabled) {
			if (!this._upload)
				zul.wgt.ADBS.autodisable(this);
			else if (!zk.ie || zk.ie > 10) // ZK-2471
				this._uplder!.openFileDialog();

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
						zUtl.go(href, {target: this._target || ((evt.data as zk.EventMetaData).ctrlKey ? '_blank' : '')});
					}
				}
				super.doClick_(evt, true);
			}
		}
		//Unlike DOM, we don't proprogate to parent (otherwise, onClick
		//will fired)
	}

	public override setFlexSize_(sz: {height?: string; width?: string}): void { //Bug #2870652
		var n = this.$n()!;
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

	public override shallIgnoreClick_(_evt: zk.Event): boolean | undefined {
		return this.isDisabled();
	}
}

//handle autodisabled buttons
@zk.WrapClass('zul.wgt.ADBS')
export class ADBS extends zk.Object {
	private _ads: zul.LabelImageWidget[];
	public constructor(ads: zul.LabelImageWidget[]) {
		super();
		this._ads = ads;
	}

	public onResponse(): void {
		for (var ads = this._ads, ad: zul.LabelImageWidget | undefined; ad = ads.shift();) {
			// B60-ZK-1176: distinguish from other usages
			ad.setDisabled(false, {adbs: false, skip: true});
			if (zk.chrome) ad.domListen_(ad.$n()!, 'onBlur', 'doBlur_'); //ZK-2739: prevent chrome fire onBlur event after autodisabled
		}
		zWatch.unlisten({onResponse: this});
	}

	//static
	/* Disable Targets and re-enable after response
	 * @param zk.Widget wgt
	 */
	public static autodisable(wgt: zul.LabelImageWidget): void {
		var ads: string[] | string | undefined = wgt._autodisable,
			aded: zul.LabelImageWidget[] | undefined,
			uplder: zul.Upload | null | undefined;
		if (ads) {
			if (zk.chrome) wgt.domUnlisten_(wgt.$n()!, 'onBlur', 'doBlur_'); //ZK-2739: prevent chrome fire onBlur event after autodisabled
			ads = ads.split(',');
			for (var j = ads.length; j--;) {
				var ad: string | zul.LabelImageWidget | null = ads[j].trim();
				if (ad) {
					var perm = ad.charAt(0) == '+';
					if (perm)
						ad = ad.substring(1);
					ad = 'self' == ad ? wgt : wgt.$f(ad) as zul.LabelImageWidget;
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
}