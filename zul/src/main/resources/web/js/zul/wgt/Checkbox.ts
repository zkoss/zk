/* Checkbox.ts

	Purpose:

	Description:

	History:
		Wed Dec 10 16:17:14     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

//Two onclick are fired if clicking on label, so ignore it if so
function _shallIgnore(evt: zk.Event): boolean | undefined {
	var v = evt.domEvent;
	// B96-ZK-4821: shall ignore if target is not the real input checkbox (label or span)
	return v && !jq.nodeName(v.target as Node, 'input');
}

/**
 * A checkbox.
 *
 * <p>Event:
 * <ol>
 * <li>onCheck is sent when a checkbox
 * is checked or unchecked by user.</li>
 * </ol>
 */
@zk.WrapClass('zul.wgt.Checkbox')
export class Checkbox extends zul.LabelImageWidget implements zul.LabelImageWidgetWithDisable {
	//_tabindex: 0,
	protected _checked = false;
	public _disabled?: boolean = false; // disabled by default
	public _adbs?: boolean;
	public _autodisable?: string;
	private _name?: string;
	private _value?: string;
	private _indeterminate?: boolean;

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
	public setDisabled(v: boolean | undefined, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		
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
				v = this._disabled;
		}
		this._disabled = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n('real') as HTMLInputElement;
			if (n) {
				n.disabled = v!;
				jq(this.$n()!).toggleClass(this.$s(this.getMoldPrefix_() + 'disabled'), v);
				if (!this._isDefaultMold()) {
					this._setTabIndexForMold();
				}
			}
		}

		return this;
	}

	/** Returns whether it is checked.
	 * <p>Default: false.
	 * @return boolean
	 */
	public isChecked(): boolean {
		return this._checked;
	}

	/** Sets whether it is checked,
	 * changing checked will set indeterminate to false.
	 * @param boolean checked
	 */
	public setChecked(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._checked;
		this._checked = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n('real');
			if (n) {
				//B70-ZK-2057: prop() method can access right property values;
				jq(n).prop('checked', v);

				this.clearStateClassName_();
				jq(this.$n()!).addClass(this.getClassNameByState_());
			}
		}

		return this;
	}

	/** Returns the name of this component.
	 * <p>Default: null.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * @return String
	 */
	public getName(): string | undefined {
		return this._name;
	}

	/** Sets the name of this component.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 *
	 * @param String name the name of this component.
	 */
	public setName(v: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n('real') as HTMLInputElement;
			if (n) n.name = v || '';
		}

		return this;
	}

	/** Returns the tab order of this component.
	 * <p>Default: -1 (means the same as browser's default).
	 * @return int
	 */
	public override getTabindex(): number | null | undefined {
		return this._tabindex;
	}

	/** Sets the tab order of this component.
	 * @param int tabindex
	 */
	public override setTabindex(v: number, opts?: Record<string, boolean>): this {
		const o = this._tabindex;
		this._tabindex = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n('real') as HTMLInputElement;
			if (n) {
				if (v == null)
					n.removeAttribute('tabindex');
				else
					n.tabIndex = v;
			}
		}

		return this;
	}

	/** Returns the value.
	 * <p>Default: "".
	 * @return String
	 * @since 5.0.4
	 */
	public getValue(): string | undefined {
		return this._value;
	}

	/** Sets the value.
	 * @param String value the value; If null, it is considered as empty.
	 * @since 5.0.4
	 */
	public setValue(v: string, opts?: Record<string, boolean>): this {
		const o = this._value;
		this._value = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n<HTMLInputElement>('real');
			if (n) n.value = v || '';
		}

		return this;
	}

	/** Returns a list of checkbox component IDs that shall be disabled when the user
	 * clicks this checkbox.
	 *
	 * <p>To represent the checkbox itself, the developer can specify <code>self</code>.
	 * For example,
	 * <pre><code>
	 * checkbox.setId('ok');
	 * wgt.setAutodisable('self,cancel');
	 * </code></pre>
	 * is the same as
	 * <pre><code>
	 * checkbox.setId('ok');
	 * wgt.setAutodisable('ok,cancel');
	 * </code></pre>
	 * that will disable
	 * both the ok and cancel checkboxes when an user clicks it.
	 *
	 * <p>The checkbox being disabled will be enabled automatically
	 * once the client receives a response from the server or a fixed timeout.
	 * In other words, the server doesn't notice if a checkbox is disabled
	 * with this method.
	 *
	 * <p>However, if you prefer to enable them later manually, you can
	 * prefix with '+'. For example,
	 * <pre><code>
	 * checkbox.setId('ok');
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

	/** Sets whether to disable the checkbox after the user clicks it.
	 * @param String autodisable
	 */
	public setAutodisable(autodisable: string): this {
		this._autodisable = autodisable;
		return this;
	}

	/**
	 * Return whether checkbox is in indeterminate state.
	 * Default: false.
	 *
	 * @return boolean
	 * @since 8.6.0
	 */
	public isIndeterminate(): boolean | undefined {
		return this._indeterminate;
	}

	/**
	 * Set whether checkbox is in indeterminate state.
	 *
	 * @param boolean indeterminate whether checkbox is indeterminate
	 * @since 8.6.0
	 */
	public setIndeterminate(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._indeterminate;
		this._indeterminate = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n('real');
			if (n) {
				jq(n).prop('indeterminate', v);
				if (!this._isDefaultMold()) {
					this.clearStateClassName_();
					jq(this.$n()!).addClass(this.getClassNameByState_());
				}
			}
		}

		return this;
	}

	/** Returns the current state according to isIndeterminate() and isChecked().
	 *
	 * @return Boolean
	 * @since 9.0.0
	 */
	public getState(): boolean | null {
		if (this.isIndeterminate())
			return null;
		else
			return this.isChecked();
	}

	//super//
	public override focus_(timeout?: number): boolean {
		zk(this.$n('real') || this.$n()).focus(timeout);
		return true;
	}

	protected contentAttrs_(): string {
		var html = '',
			v: string | undefined | number | null; // cannot use this._name for radio
		if (v = this.getName())
			html += ` name="${v}"`;
		if (this._disabled)
			html += ' disabled="disabled"';
		if (this._checked)
			html += ' checked="checked"';
		if (v = this._tabindex)
			html += ` tabindex="${v}"`;
		if (v = this.getValue())
			html += ` value="${v}"`;
		return html;
	}

	protected _moldA11yAttrs(): string {
		return '';
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		var n = this.$n('real') as HTMLInputElement,
			mold = this.$n('mold')!,
			indeterminate = this.isIndeterminate();

		// Bug 2383106
		if (n.checked != n.defaultChecked)
			n.checked = n.defaultChecked;
		if (indeterminate)
			n.indeterminate = true;

		this.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_')
			.domListen_(mold, 'onMouseDown', '_doMoldMouseDown');

		this._setTabIndexForMold();
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		var n = this.$n('real')!,
			mold = this.$n('mold')!;

		this.domUnlisten_(mold, 'onMouseDown', '_doMoldMouseDown')
			.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');

		super.unbind_(skipper, after, keepRod);
	}

	private _setTabIndexForMold(): void {
		var mold = this.$n('mold') as HTMLLabelElement;
		if (mold)
			mold.tabIndex = this._canTabOnMold() ? 0 : -1;
	}

	private _canTabOnMold(): boolean {
		return !this._isDefaultMold() && !this.isDisabled();
	}

	protected override doSelect_(evt: zk.Event): void {
		if (!_shallIgnore(evt))
			super.doSelect_(evt);
	}

	public override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		// B96-ZK-4821: shall not doClick if the checkbox is disabled
		if (!_shallIgnore(evt) && !this.isDisabled()) {
			// F55-ZK-12: Checkbox automatically disable itself after clicked
			// use the autodisable handler of button directly
			zul.wgt.ADBS.autodisable(this);
			if (this._isTristateMold()) {
				this.nextState_();
				this.fireOnCheck_(this.getState());
			} else {
				var real = this.$n_<HTMLInputElement>('real'),
					checked = real.checked;
				if (checked != this._checked) { //changed
					this.setChecked(checked); //so Radio has a chance to override it
					this.setIndeterminate(false);
					this.fireOnCheck_(checked);
				}
				if (zk.webkit && !zk.mobile)
					zk(real).focus();

				// B65-ZK-1837: should stop propagation
				evt.stop({propagation: true});
				super.doClick_(evt, popupOnly);

				// B85-ZK-3866: do extra click, if it's a radio
				if (this instanceof zul.wgt.Radio) {
					this.getRadiogroup()?.doClick_(evt);
				}
			}
		}
	}

	protected _doMoldMouseDown(evt: zk.Event): void {
		if (this.isDisabled())
			evt.stop();
	}

	protected fireOnCheck_(checked: boolean | null): void {
		this.fire('onCheck', checked);
	}

	protected override beforeSendAU_(wgt: zk.Widget, evt: zk.Event): void {
		if (evt.name != 'onClick') //don't stop event if onClick (otherwise, check won't work)
			super.beforeSendAU_(wgt, evt);
	}

	public override getTextNode(): HTMLElement | null | undefined {
		return this.$n('cnt');
	}

	public override shallIgnoreClick_(): boolean | undefined {
		return this.isDisabled();
	}

	protected override domClass_(no?: zk.DomClassOptions): string {
		var cls = super.domClass_(no),
			mold = this.getMold();

		cls += ' ' + this.$s(mold);

		cls += ' ' + this.getClassNameByState_();
		if (this.isDisabled())
			cls += ' ' + this.$s(this.getMoldPrefix_() + 'disabled');

		return cls;
	}

	private _isDefaultMold(): boolean {
		return this.getMold() == 'default';
	}

	private _isTristateMold(): boolean {
		return this.getMold() == 'tristate';
	}

	protected nextState_(): void {
		if (this._indeterminate) {
			this.setIndeterminate(false);
			this.setChecked(true);
		} else {
			if (this._checked) {
				this.setChecked(false);
			} else {
				this.setIndeterminate(true);
			}
		}
	}

	protected getMoldPrefix_(): string {
		return this._isDefaultMold() ? '' : (this.getMold() + '-');
	}

	protected getClassNameByState_(): string {
		let moldPrefix = this.getMoldPrefix_();
		if (this._indeterminate) {
			return this.$s(moldPrefix + 'indeterminate');
		} else {
			return this.$s(moldPrefix + (this._checked ? 'on' : 'off'));
		}
	}

	protected clearStateClassName_(): void {
		let n = jq(this.$n()!),
			moldPrefix = this.getMoldPrefix_();
		if (n) {
			n.removeClass(this.$s(moldPrefix + 'off'))
				.removeClass(this.$s(moldPrefix + 'indeterminate'))
				.removeClass(this.$s(moldPrefix + 'on'));
		}
	}

	protected override doKeyDown_(evt: zk.Event): void {
		super.doKeyDown_(evt);
		const spaceKeyCode = 32;
		if (evt.domTarget == this.$n('mold') && evt.keyCode == spaceKeyCode) {
			if (this._isTristateMold()) {
				this.nextState_();
				this.fireOnCheck_(this.getState());
				return;
			}
			let checked = !this.isChecked();
			this.setChecked(checked);
			this.fireOnCheck_(checked);
		}
	}
}