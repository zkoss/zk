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
	zWatch.listen({ onSize: wgt });
	const v = wgt._upload;
	if (v)
		wgt._uplder = new zul.Upload(wgt, undefined, v);
}

function _cleanUpld(wgt: zul.wgt.Toolbarbutton): void {
	const v = wgt._uplder;
	if (v) {
		zWatch.unlisten({ onSize: wgt });
		wgt._uplder = undefined;
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
	_adbs?: boolean;
	_disabled?: boolean;
	_autodisable?: string;

	_orient = 'horizontal';
	_dir = 'normal';
	_mode = 'default';
	_checked = false;
	override _tabindex = 0;
	_type?: string;
	_href?: string;
	_target?: string;
	_upload?: string;

	/**
	 * Returns the mode.
	 * @return String
	 */
	getMode(): string | undefined {
		return this._mode;
	}

	/**
	 * Sets the mode. (default/toggle)
	 * @param String mode
	 */
	setMode(mode: string, opts?: Record<string, boolean>): this {
		const o = this._mode;
		this._mode = mode;

		if (o !== mode || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/** Returns whether it is checked. (Note:It's only available in toggle mode.)
	 * <p>Default: false.
	 * @return boolean
	 */
	isChecked(): boolean {
		return this._checked;
	}

	/** Sets whether it is checked. (Note:It's only available in toggle mode.)
	 * @param boolean checked
	 */
	setChecked(checked: boolean, opts?: Record<string, boolean>): this {
		const o = this._checked;
		this._checked = checked;

		if (o !== checked || opts?.force) {
			if (this.desktop && this._mode == 'toggle')
				jq(this.$n_())[checked ? 'addClass' : 'removeClass'](this.$s('checked'));
		}

		return this;
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @return boolean
	 */
	isDisabled(): boolean {
		return !!this._disabled;
	}

	/** Sets whether it is disabled.
	 * @param boolean disabled
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;

		// eslint-disable-next-line zk/preferStrictBooleanType
		let value: boolean | undefined = disabled;

		//B60-ZK-1176
		// Autodisable should not re-enable when setDisabled(true) is called during onClick
		if (opts?.adbs)
			// called from zul.wgt.ADBS.autodisable
			this._adbs = true;	// Start autodisabling
		else if (!opts || opts.adbs === undefined)
			// called somewhere else (including server-side)
			this._adbs = false;	// Stop autodisabling
		if (!value) {
			if (this._adbs)
				// autodisable is still active, enable allowed
				this._adbs = false;
			// eslint-disable-next-line @typescript-eslint/no-unnecessary-boolean-literal-compare
			else if (opts && opts.adbs === false)
				// ignore re-enable by autodisable mechanism
				value = this._disabled;
		}

		this._disabled = value;

		if (o !== value || opts?.force) {
			const doDisable = (): void => {
				if (this.desktop) {
					jq(this.$n_()).attr('disabled', value ? 'disabled' : null); // use jQuery's attr() instead of dom.disabled for non-button element. Bug ZK-2146
					if (this._upload)
						value ? _cleanUpld(this) : _initUpld(this);
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
	getHref(): string | undefined {
		return this._href;
	}

	/** Sets the href.
	 * @param String href
	 */
	setHref(href: string): this {
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
	getTarget(): string | undefined {
		return this._target;
	}

	/** Sets the target frame or window.
	 * @param String target the name of the frame or window to hyperlink.
	 */
	setTarget(target: string): this {
		this._target = target;
		return this;
	}

	/** Returns the direction.
	 * <p>Default: "normal".
	 * @return String
	 */
	getDir(): string {
		return this._dir;
	}

	/** Sets the direction.
	 * @param String dir either "normal" or "reverse".
	 */
	setDir(dir: string, opts?: Record<string, boolean>): this {
		const o = this._dir;
		this._dir = dir;

		if (o !== dir || opts?.force) {
			this.updateDomContent_();
		}

		return this;
	}

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 * @return String
	 */
	getOrient(): string {
		return this._orient;
	}

	/** Sets the orient.
	 * @param String orient either "horizontal" or "vertical".
	 */
	setOrient(orient: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = orient;

		if (o !== orient || opts?.force) {
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
	getAutodisable(): string | undefined {
		return this._autodisable;
	}

	/** Sets whether to disable the button after the user clicks it.
	 * @param String autodisable
	 */
	setAutodisable(autodisable: string): this {
		this._autodisable = autodisable;
		return this;
	}

	/** Returns non-null if this button is used for file upload, or null otherwise.
	 * Refer to {@link #setUpload} for more details.
	 * @return String
	 */
	getUpload(): string | undefined {
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
	setUpload(upload: string, opts?: Record<string, boolean>): this {
		const o = this._upload;
		this._upload = upload;

		if (o !== upload || opts?.force) {
			var n = this.$n();
			if (n) {
				_cleanUpld(this);
				if (upload && upload != 'false' && !this._disabled)
					_initUpld(this);
			}
		}

		return this;
	}

	// super//
	override getTextNode(): HTMLElement | undefined {
		return this.$n('cnt');
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (!this._disabled) {
			var n = this.$n_();
			this.domListen_(n, 'onFocus', 'doFocus_')
				.domListen_(n, 'onBlur', 'doBlur_');
		}
		if (!this._disabled && this._upload) _initUpld(this);
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		_cleanUpld(this);
		var n = this.$n_();
		this.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');

		super.unbind_(skipper, after, keepRod);
	}

	override domContent_(): string {
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

	override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no),
			zcls = this.getZclass(),
			nozcls = (!no || !no.zclass);

		if (this._mode == 'toggle' && this._checked && nozcls && zcls) {
			scls += ' ' + this.$s('checked');
		}

		return scls;
	}

	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var attr = super.domAttrs_(no);
		if (this._disabled)
			attr += ' disabled="disabled"';
		return attr;
	}

	override onSize(): void {
		if (this._uplder)
			this._uplder.sync();
	}

	override doClick_(evt: zk.Event<zk.EventMetaData>, popupOnly?: boolean): void {
		if (!this._disabled) {
			if (!this._upload)
				zul.wgt.ADBS.autodisable(this);
			else
				this._uplder!.openFileDialog();

			this.fireX(evt);

			if (!evt.stopped) {
				var href = this._href,
					isMailTo = href ? href.toLowerCase().startsWith('mailto:') : false;

				if (href) {
					// ZK-2506: use iframe to open a 'mailto' href
					if (isMailTo) {
						var ifrm = jq.newFrame('mailtoFrame', href, undefined);
						jq(ifrm).remove();
					} else {
						zUtl.go(href, { target: this._target || (evt.data!.ctrlKey ? '_blank' : '') });
					}
				}

				super.doClick_(evt, true);

				if (this._mode == 'toggle') {
					this.setChecked(!this.isChecked());
					this.fire('onCheck', this.isChecked());
				}
			}
		}
	}

	override focus_(timeout: number): boolean {
		if (this._tabindex != undefined || this._href || this._upload) {
			const n = this.$n_();
			zk.afterAnimate(() => {
				try {
					n.focus();
					zk.currentFocus = this;
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