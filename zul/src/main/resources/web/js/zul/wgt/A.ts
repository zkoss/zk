/* A.ts

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
 * @defaultValue {@link getZclass}: z-a.
 */
@zk.WrapClass('zul.wgt.A')
export class A extends zul.LabelImageWidget<HTMLAnchorElement> implements zul.LabelImageWidgetWithAutodisable {
	/** @internal */
	_dir = 'normal';
	/** @internal */
	_href?: string;
	/** @internal */
	_target?: string;
	/** @internal */
	_disabled?: boolean;
	/** @internal */
	_adbs?: boolean;
	/** @internal */
	_autodisable?: string;
	//_tabindex: 0,

	/**
	 * @returns whether it is disabled.
	 * @defaultValue `false`.
	 */
	isDisabled(): boolean {
		return !!this._disabled;
	}

	/**
	 * Sets whether it is disabled.
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;

		// eslint-disable-next-line zk/preferStrictBooleanType
		let value: boolean | undefined = disabled;

		// Refer from Button.js for the following changes
		// B60-ZK-1176
		// Autodisable should not re-enable when setDisabled(true) is called during onClick
		if (opts?.adbs)
			// called from zul.wgt.ADBS.autodisable
			this._adbs = true;	// Start autodisabling
		else if (!opts || opts.adbs === undefined)
			// called somewhere else (including server-side)
			this._adbs = false;	// Stop autodisabling
		if (!value) {
			if (this._adbs) {
				// autodisable is still active, allow enabling
				this._adbs = false;
			// eslint-disable-next-line @typescript-eslint/no-unnecessary-boolean-literal-compare
			} else if (opts && opts.adbs === false)
				// ignore re-enable by autodisable mechanism
				value = this._disabled;
		}
		this._disabled = value;

		if (o !== value || opts?.force) {
			var doDisable = (): void => {
					if (this.desktop) {
						jq(this.$n()).attr('disabled', value ? 'disabled' : null); // use jQuery's attr() instead of dom.disabled for non-button element. Bug ZK-2146
					}
				};
			doDisable();
		}

		return this;
	}

	/**
	 * @returns the direction.
	 * @defaultValue `"normal"`.
	 */
	getDir(): string {
		return this._dir;
	}

	/**
	 * Sets the direction.
	 * @param dir - either "normal" or "reverse".
	 */
	setDir(dir: string, opts?: Record<string, boolean>): this {
		const o = this._dir;
		this._dir = dir;

		if (o !== dir || opts?.force) {
			var n = this.$n();
			if (n) n.innerHTML = this.domContent_();
		}

		return this;
	}

	/**
	 * @returns the href that the browser shall jump to, if an user clicks
	 * this button.
	 * @defaultValue `null`. If null, the button has no function unless you
	 * specify the onClick event listener.
	 * <p>If it is not null, the onClick event won't be sent.
	 */
	getHref(): string | undefined {
		return this._href;
	}

	/**
	 * Sets the href.
	 */
	setHref(href: string, opts?: Record<string, boolean>): this {
		const o = this._href;
		this._href = href;

		if (o !== href || opts?.force) {
			var n = this.$n();
			if (n) n.href = href || '';
		}

		return this;
	}

	/**
	 * @returns the target frame or window.
	 *
	 * <p>Note: it is useful only if href ({@link setHref}) is specified
	 * (i.e., use the onClick listener).
	 *
	 * @defaultValue `null`.
	 */
	getTarget(): string | undefined {
		return this._target;
	}

	/**
	 * Sets the target frame or window.
	 * @param target - the name of the frame or window to hyperlink.
	 */
	setTarget(target: string, opts?: Record<string, boolean>): this {
		const o = this._target;
		this._target = target;

		if (o !== target || opts?.force) {
			var n = this.$n();
			if (n) n.target = target || '';
		}

		return this;
	}

	/**
	 * @returns a list of component IDs that shall be disabled when the user
	 * clicks this anchor.
	 *
	 * <p>To represent the anchor itself, the developer can specify `self`.
	 * For example,
	 * ```ts
	 * anchor.setId('ok');
	 * wgt.setAutodisable('self,cancel');
	 * ```
	 * is the same as
	 * ```ts
	 * anchor.setId('ok');
	 * wgt.setAutodisable('ok,cancel');
	 * ```
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
	 * ```ts
	 * anchor.setId('ok');
	 * wgt.setAutodisable('+self,+cancel');
	 * ```
	 *
	 * <p>Then, you have to enable them manually such as
	 * ```ts
	 * if (something_happened){
	 *  ok.setDisabled(false);
	 *  cancel.setDisabled(false);
	 * }
	 * ```
	 *
	 * @defaultValue `null`.
	 */
	getAutodisable(): string | undefined {
		return this._autodisable;
	}

	/**
	 * Sets whether to disable the anchor after the user clicks it.
	 */
	setAutodisable(autodisable: string): this {
		this._autodisable = autodisable;
		return this;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (!this._disabled) {
			var n = this.$n()!;
			this.domListen_(n, 'onFocus', 'doFocus_')
				.domListen_(n, 'onBlur', 'doBlur_');
		}
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		var n = this.$n()!;
		this.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');

		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	override domContent_(): string {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.domImage_(),
			iconSclass = this.domIcon_();
		if (!img && !iconSclass)
			return label;

		if (!img) {
			img = iconSclass;
		} else
			img += `${iconSclass ? ' ' + iconSclass : ''}`;
		return this.getDir() == 'reverse' ? label + img : img + label;
	}

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var attr = super.domAttrs_(no),
			v: string | undefined;
		if (v = this.getTarget())
			attr += ` target="${v}"`;
		if (v = this.getHref())
			attr += ` href="${v}"`;
		else
			attr += ' href="javascript:;"';
		if (this._disabled)
			attr += ' disabled="disabled"';
		return attr;
	}

	/** @internal */
	override doClick_(evt: zk.Event): void {
		var href = this.getHref();
		// ZK-2506: use iframe to open a 'mailto' href
		if (href && href.toLowerCase().startsWith('mailto:')) {
			var ifrm = jq.newFrame('mailtoFrame', href, undefined);
			if (zk.chrome) // ZK-3646: for chrome, it need to let iframe exist for a longer time.
				setTimeout(() => { jq(ifrm).remove(); }, 100);
			else
				jq(ifrm).remove();
			evt.stop();
		}
		if (this._disabled)
			evt.stop(); // Prevent browser default
		else {
			zul.wgt.ADBS.autodisable(this);

			this.fireX(evt);
			if (!evt.stopped)
				super.doClick_(evt, true);
		}
			// Unlike DOM, we don't propagate to parent (so do not call $supers)
	}
}