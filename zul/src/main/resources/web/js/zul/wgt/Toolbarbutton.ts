/* Toolbarbutton.ts

	Purpose:

	Description:

	History:
		Sat Dec 22 12:58:43	 2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _initUpld(wgt: zul.wgt.Toolbarbutton): void {
	zWatch.listen({onSize: wgt});
	const v = wgt._upload;
	if (v)
		wgt._uplder = new zul.Upload(wgt, null, v);
}

function _cleanUpld(wgt: zul.wgt.Toolbarbutton): void {
	const v = wgt._uplder;
	if (v) {
		zWatch.unlisten({onSize: wgt});
		wgt._uplder = null;
		v.destroy();
	}
}

/**
 * A toolbar button.
 *
 * <p>Non-xul extension: Toolbarbutton supports {@link #getHref}. If {@link #getHref}
 * is not null, the onClick handler is ignored and this element is degenerated
 * to HTML's A tag.
 *
 * <p>Default {@link #getZclass}: z-toolbarbutton.
 */
@zk.WrapClass('zul.wgt.Toolbarbutton')
export class Toolbarbutton extends zul.LabelImageWidget implements zul.LabelImageWidgetWithAutodisable {
	public _adbs?: boolean;
	public _disabled?: boolean;
	public _autodisable?: string;

	private _orient = 'horizontal';
	private _dir = 'normal';
	private _mode = 'default';
	private _checked = false;
	protected override _tabindex = 0;
	private _type?: string;
	private _href?: string;
	private _target?: string;
	public _upload?: string;

	/**
	 * Returns the mode.
	 * @return String
	 */
	public getMode(): string | null {
		return this._mode;
	}

	/**
	 * Sets the mode. (default/toggle)
	 * @param String mode
	 */
	public setMode(mode: string, opts?: Record<string, boolean>): this {
		const o = this._mode;
		this._mode = mode;

		if (o !== mode || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns whether it is checked. (Note:It's only available in toggle mode.)
	 * <p>Default: false.
	 * @return boolean
	 */
	public isChecked(): boolean {
		return this._checked;
	}

	/** Sets whether it is checked. (Note:It's only available in toggle mode.)
	 * @param boolean val
	 */
	public setChecked(val: boolean, opts?: Record<string, boolean>): this {
		const o = this._checked;
		this._checked = val;

		if (o !== val || (opts && opts.force)) {
			if (this.desktop && this._mode == 'toggle')
				jq(this.$n_())[val ? 'addClass' : 'removeClass'](this.$s('checked'));
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

		//B60-ZK-1176
		// Autodisable should not re-enable when setDisabled(true) is called during onClick
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
				v = !!this._disabled;
		}

		this._disabled = v;

		if (o !== v || (opts && opts.force)) {
			var self = this,
				doDisable = function (): void {
					if (self.desktop) {
						jq(self.$n_()).attr('disabled', v); // use jQuery's attr() instead of dom.disabled for non-button element. Bug ZK-2146
						if (self._upload)
							v ? _cleanUpld(self) : _initUpld(self);
					}
				};

			if (this._type == 'submit')
				setTimeout(doDisable, 50);
			else
				doDisable();
		}

		return this;
	}

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
	public setDir(dir: string, opts?: Record<string, boolean>): this {
		const o = this._dir;
		this._dir = dir;

		if (o !== dir || (opts && opts.force)) {
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
	public setOrient(orient: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = orient;

		if (o !== orient || (opts && opts.force)) {
			this.updateDomContent_();
		}

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

	/** Sets whether to disable the button after the user clicks it.
	 * @param String autodisable
	 */
	public setAutodisable(autodisable: string): this {
		this._autodisable = autodisable;
		return this;
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
			if (n) {
				_cleanUpld(this);
				if (v && v != 'false' && !this._disabled)
					_initUpld(this);
			}
		}

		return this;
	}

	// super//
	public override getTextNode(): HTMLElement | null | undefined {
		return this.$n('cnt');
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (!this._disabled) {
			var n = this.$n_();
			this.domListen_(n, 'onFocus', 'doFocus_')
				.domListen_(n, 'onBlur', 'doBlur_');
		}
		if (!this._disabled && this._upload) _initUpld(this);
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		_cleanUpld(this);
		var n = this.$n_();
		this.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');

		super.unbind_(skipper, after, keepRod);
	}

	protected override domContent_(): string {
		var label = zUtl.encodeXML(this.getLabel()), img = this.domImage_(),
			iconSclass = this.domIcon_();
		if (!img && !iconSclass)
			return label;

		if (!img) img = iconSclass;
		else img += (iconSclass ? ' ' + iconSclass : '');
		// B50-ZK-640: toolbarbutton with no label will display larger width blur box
		var space = label ? 'vertical' == this.getOrient() ? '<br/>' : '&nbsp;' : '';
		return this.getDir() == 'reverse' ? label + space + img : img + space + label;
	}

	protected override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no),
			zcls = this.getZclass(),
			nozcls = (!no || !no.zclass);

		if (this._mode == 'toggle' && this._checked && nozcls && zcls) {
			scls += ' ' + this.$s('checked');
		}

		return scls;
	}

	public override domAttrs_(no?: zk.DomAttrsOptions): string {
		var attr = super.domAttrs_(no);
		if (this._disabled)
			attr += ' disabled="disabled"';
		return attr;
	}

	public override onSize(): void {
		if (this._uplder)
			this._uplder.sync();
	}

	public override doClick_(evt: zk.Event<zk.EventMetaData>, popupOnly?: boolean): void {
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
						zUtl.go(href, {target: this._target || (evt.data!.ctrlKey ? '_blank' : '')});
					}
				}

				this.$super('doClick_', evt, true);

				if (this._mode == 'toggle') {
					this.setChecked(!this.isChecked());
					this.fire('onCheck', this.isChecked());
				}
			}
		}
	}

	public override focus_(timeout: number): boolean {
		if (this._tabindex != undefined || this._href || this._upload) {
			var self = this,
				n = this.$n_();
			zk.afterAnimate(function () {
				try {
					n.focus();
					zk.currentFocus = self;
					zjq.fixInput(n);
				} catch (e) {
					zk.debugLog((e as Error).message || e as string);
				}
			}, timeout);
			return true;
		}
		return false;
	}
}