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
function _shallIgnore(evt: zk.Event): boolean {
	const v = evt.domEvent;
	// B96-ZK-4821: shall ignore if target is not the real input checkbox (label or span)
	return !!(v && !jq.nodeName(v.target as Node, 'input'));
}

export interface Checkbox {
	$n(subId: 'real'): HTMLInputElement | undefined;
	$n(subId: 'mold'): HTMLLabelElement | undefined;
	$n<T extends HTMLElement = HTMLElement>(subId?: string): T | undefined;
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
	_checked = false;
	_disabled?: boolean = false; // disabled by default
	_adbs?: boolean;
	_autodisable?: string;
	_name?: string;
	_value?: string;
	_indeterminate?: boolean;

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

		if (opts?.adbs)
			// called from zul.wgt.ADBS.autodisable
			this._adbs = true;	// Start autodisabling
		else if (!opts || opts.adbs === undefined)
			// called somewhere else (including server-side)
			this._adbs = false;	// Stop autodisabling
		if (!disabled) {
			if (this._adbs) {
				// autodisable is still active, allow enabling
				this._adbs = false;
				// eslint-disable-next-line @typescript-eslint/no-unnecessary-boolean-literal-compare
			} else if (opts && opts.adbs === false)
				// ignore re-enable by autodisable mechanism
				disabled = !!this._disabled;
		}
		this._disabled = disabled;

		if (o !== disabled || opts?.force) {
			const n = this.$n('real');
			if (n) {
				n.disabled = disabled!;
				jq(this.$n()).toggleClass(this.$s(this.getMoldPrefix_() + 'disabled'), disabled);
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
	isChecked(): boolean {
		return this._checked;
	}

	/** Sets whether it is checked,
	 * changing checked will set indeterminate to false.
	 * @param boolean checked
	 */
	setChecked(checked: boolean, opts?: Record<string, boolean>): this {
		const o = this._checked;
		this._checked = checked;

		if (o !== checked || opts?.force) {
			const n = this.$n('real');
			if (n) {
				//B70-ZK-2057: prop() method can access right property values;
				jq(n).prop('checked', checked);

				this.clearStateClassName_();
				jq(this.$n()).addClass(this.getClassNameByState_());
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
	getName(): string | undefined {
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
	setName(name: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = name;

		if (o !== name || opts?.force) {
			const n = this.$n('real');
			if (n) n.name = name || '';
		}

		return this;
	}

	/** Returns the tab order of this component.
	 * <p>Default: -1 (means the same as browser's default).
	 * @return int
	 */
	override getTabindex(): number | undefined {
		return this._tabindex;
	}

	/** Sets the tab order of this component.
	 * @param int tabindex
	 */
	override setTabindex(tabindex: number, opts?: Record<string, boolean>): this {
		const o = this._tabindex;
		this._tabindex = tabindex;

		if (o !== tabindex || opts?.force) {
			const n = this.$n('real');
			if (n) {
				if (tabindex == null)
					n.removeAttribute('tabindex');
				else
					n.tabIndex = tabindex;
			}
		}

		return this;
	}

	/** Returns the value.
	 * <p>Default: "".
	 * @return String
	 * @since 5.0.4
	 */
	getValue(): string | undefined {
		return this._value;
	}

	/** Sets the value.
	 * @param String value the value; If null, it is considered as empty.
	 * @since 5.0.4
	 */
	setValue(value: string, opts?: Record<string, boolean>): this {
		const o = this._value;
		this._value = value;

		if (o !== value || opts?.force) {
			const n = this.$n('real');
			if (n) n.value = value || '';
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
	getAutodisable(): string | undefined {
		return this._autodisable;
	}

	/** Sets whether to disable the checkbox after the user clicks it.
	 * @param String autodisable
	 */
	setAutodisable(autodisable: string): this {
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
	isIndeterminate(): boolean {
		return !!this._indeterminate;
	}

	/**
	 * Set whether checkbox is in indeterminate state.
	 *
	 * @param boolean indeterminate whether checkbox is indeterminate
	 * @since 8.6.0
	 */
	setIndeterminate(indeterminate: boolean, opts?: Record<string, boolean>): this {
		const o = this._indeterminate;
		this._indeterminate = indeterminate;

		if (o !== indeterminate || opts?.force) {
			const n = this.$n('real');
			if (n) {
				jq(n).prop('indeterminate', indeterminate);
				if (!this._isDefaultMold()) {
					this.clearStateClassName_();
					jq(this.$n()).addClass(this.getClassNameByState_());
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
	// eslint-disable-next-line zk/preferStrictBooleanType
	getState(): boolean | undefined {
		if (this.isIndeterminate())
			return undefined;
		else
			return this.isChecked();
	}

	//super//
	override focus_(timeout?: number): boolean {
		zk(this.$n('real') || this.$n()).focus(timeout);
		return true;
	}

	contentAttrs_(): string {
		const out: string[] = [],
			name = this.getName(); // cannot use this._name for radio
		if (name)
			out.push(`name="${name}"`);
		if (this._disabled)
			out.push('disabled="disabled"');
		if (this._checked)
			out.push('checked="checked"');
		if (this._tabindex)
			out.push(`tabindex="${this._tabindex}"`);
		const value = this.getValue();
		if (value)
			out.push(`value="${value}"`);
		return out.join(' ');
	}

	_moldA11yAttrs(): string {
		return '';
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		const n = this.$n('real')!,
			mold = this.$n('mold')!;

		// Bug 2383106
		n.checked = n.defaultChecked;
		if (this.isIndeterminate())
			n.indeterminate = true;

		this.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_')
			.domListen_(mold, 'onMouseDown', '_doMoldMouseDown')
			._setTabIndexForMold();
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		const n = this.$n('real')!,
			mold = this.$n('mold')!;

		this.domUnlisten_(mold, 'onMouseDown', '_doMoldMouseDown')
			.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_')
			.unbind_(skipper, after, keepRod);
	}

	_setTabIndexForMold(): void {
		const mold = this.$n('mold');
		if (mold)
			mold.tabIndex = this._canTabOnMold() ? 0 : -1;
	}

	_canTabOnMold(): boolean {
		return !this._isDefaultMold() && !this.isDisabled();
	}

	override doSelect_(evt: zk.Event): void {
		if (!_shallIgnore(evt))
			super.doSelect_(evt);
	}

	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		// B96-ZK-4821: shall not doClick if the checkbox is disabled
		if (!_shallIgnore(evt) && !this.isDisabled()) {
			// F55-ZK-12: Checkbox automatically disable itself after clicked
			// use the autodisable handler of button directly
			zul.wgt.ADBS.autodisable(this);
			if (this._isTristateMold()) {
				this.nextState_();
				this.fireOnCheck_(this.getState());
			} else {
				const real = this.$n_<HTMLInputElement>('real'),
					checked = real.checked;
				if (checked != this._checked) { //changed
					this.setChecked(checked); //so Radio has a chance to override it
					this.setIndeterminate(false);
					this.fireOnCheck_(checked);
				}
				if (zk.webkit && !zk.mobile)
					zk(real).focus();

				// B65-ZK-1837: should stop propagation
				evt.stop({ propagation: true });
				super.doClick_(evt, popupOnly);

				// B85-ZK-3866: do extra click, if it's a radio
				if (this instanceof zul.wgt.Radio) {
					this.getRadiogroup()?.doClick_(evt);
				}
			}
		}
	}

	_doMoldMouseDown(evt: zk.Event): void {
		if (this.isDisabled())
			evt.stop();
	}
	// eslint-disable-next-line zk/preferStrictBooleanType
	fireOnCheck_(checked: boolean | undefined): void {
		this.fire('onCheck', checked);
	}

	override beforeSendAU_(wgt: zk.Widget, evt: zk.Event): void {
		if (evt.name != 'onClick') //don't stop event if onClick (otherwise, check won't work)
			super.beforeSendAU_(wgt, evt);
	}

	override getTextNode(): HTMLElement | undefined {
		return this.$n('cnt');
	}

	override shallIgnoreClick_(): boolean {
		return this.isDisabled();
	}

	override domClass_(no?: zk.DomClassOptions): string {
		const out = [
			super.domClass_(no),
			this.$s(this.getMold()),
			this.getClassNameByState_()
		];
		if (this.isDisabled())
			out.push(this.$s(this.getMoldPrefix_() + 'disabled'));
		return out.join(' ');
	}

	_isDefaultMold(): boolean {
		return this.getMold() == 'default';
	}

	_isTristateMold(): boolean {
		return this.getMold() == 'tristate';
	}

	nextState_(): void {
		if (this._indeterminate) {
			this.setIndeterminate(false);
			this.setChecked(true);
		} else if (this._checked) {
			this.setChecked(false);
		} else {
			this.setIndeterminate(true);
		}
	}

	getMoldPrefix_(): string {
		return this._isDefaultMold() ? '' : (this.getMold() + '-');
	}

	getClassNameByState_(): string {
		const moldPrefix = this.getMoldPrefix_();
		if (this._indeterminate) {
			return this.$s(moldPrefix + 'indeterminate');
		} else {
			return this.$s(moldPrefix + (this._checked ? 'on' : 'off'));
		}
	}

	clearStateClassName_(): void {
		const n = jq(this.$n()),
			moldPrefix = this.getMoldPrefix_();
		if (n) {
			n.removeClass(this.$s(moldPrefix + 'off'))
				.removeClass(this.$s(moldPrefix + 'indeterminate'))
				.removeClass(this.$s(moldPrefix + 'on'));
		}
	}

	override doKeyDown_(evt: zk.Event): void {
		super.doKeyDown_(evt);
		const spaceKeyCode = 32;
		if (evt.domTarget == this.$n('mold') && evt.keyCode == spaceKeyCode) {
			if (this._isTristateMold()) {
				this.nextState_();
				this.fireOnCheck_(this.getState());
				return;
			}
			const checked = !this.isChecked();
			this.setChecked(checked);
			this.fireOnCheck_(checked);
		}
	}
}