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

// ZK-5906: backstop reset (ms) for the skipBfUnload flag. A real same-tab
// navigation fires beforeunload/unload almost immediately after the click —
// the browser does NOT wait for the onClick AU or the network — so the flag
// is read while still set; this timer does not gate that protection. It only
// matters when the navigation is cancelled (another handler preventDefaults,
// or the user dismisses a beforeunload prompt): then it clears the otherwise
// stale flag so a later genuine teardown is not suppressed. The bfcache
// "Back" case is reset deterministically by the pageshow handler in mount.ts.
const SKIP_BF_UNLOAD_RESET_MS = 1000;

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
			// eslint-disable-next-line @microsoft/sdl/no-inner-html
			if (n) n.innerHTML = DOMPurify.sanitize(this.domContent_());
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
			/*safe*/ img = this.domImage_(),
			/*safe*/ iconSclass = this.domIcon_();
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
		var /*safe*/ attr = super.domAttrs_(no),
			v: string | undefined;
		if (v = this.getTarget())
			attr += ` target="${zUtl.encodeXMLAttribute(v)}"`;
		if (v = this.getHref())
			attr += ` href="${zUtl.encodeXMLAttribute(v)}"`;
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
			evt.stop({propagation: true}); // ZK-5785: mailto link doesn't work
		}
		if (this._disabled)
			evt.stop(); // Prevent browser default
		else {
			if (this._isNavigationHref(href) && !this._opensNewTab(evt)) {
				// ZK-5906: a real same-tab navigation will unload this page;
				// keep the desktop alive briefly so the click's onClick AU
				// isn't answered with a timeout/410 and the user reaches the
				// href instead of the timeout page.
				zk.skipBfUnload = true;
				setTimeout(() => { zk.skipBfUnload = false; }, SKIP_BF_UNLOAD_RESET_MS);
			}
			this.fireX(evt);
			zul.wgt.ADBS.autodisable(this);

			if (!evt.stopped)
				super.doClick_(evt, true);
		}
			// Unlike DOM, we don't propagate to parent (so do not call $supers)
	}

	/** @internal */
	_isNavigationHref(href?: string): boolean {
		if (!href) return false;
		// A real navigation unloads the current tab; pseudo-protocols and
		// same-page anchors (javascript:/mailto:/tel:/sms:/#) do not.
		return !/^(?:javascript:|mailto:|tel:|sms:|#)/i.test(href);
	}

	/** @internal */
	_opensNewTab(evt: zk.Event): boolean {
		// target="_blank" / a named target navigates a new tab/window, so the
		// current page keeps living and its desktop should work normally.
		const t = this.getTarget();
		if (t && t !== '_self' && t !== '_top' && t !== '_parent') return true;
		// Modifier-clicks (Ctrl/Cmd/Shift/Alt) don't unload the current tab
		// either. doClick_ only runs for a plain left-click (mount.ts dispatches
		// onClick on evt.which == 1), so there is no middle-click case here.
		return !!(evt.ctrlKey || evt.metaKey || evt.shiftKey || evt.altKey);
	}
}
