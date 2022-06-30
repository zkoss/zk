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
 * <p>Default {@link #getZclass}: z-a.
 */
export class A extends zul.LabelImageWidget<HTMLAnchorElement> {
	private _dir = 'normal';
	private _href?: string;
	private _target?: string;
	//_tabindex: 0,

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

		// Refer from Button.js for the following changes
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
				v = this._disabled;
		}
		this._disabled = v;

		if (o !== v || (opts && opts.force)) {
			var doDisable = (): void => {
					if (this.desktop) {
						jq(this.$n()!).attr('disabled', v ? 'disabled' : null); // use jQuery's attr() instead of dom.disabled for non-button element. Bug ZK-2146
					}
				};
			doDisable();
		}

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
			var n = this.$n();
			if (n) n.innerHTML = this.domContent_();
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
	public setHref(v: string, opts?: Record<string, boolean>): this {
		const o = this._href;
		this._href = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.href = v || '';
		}

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
	public setTarget(v: string, opts?: Record<string, boolean>): this {
		const o = this._target;
		this._target = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.target = v || '';
		}

		return this;
	}

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
	public getAutodisable(): string | undefined {
		return this._autodisable;
	}

	/** Sets whether to disable the anchor after the user clicks it.
	 * @param String autodisable
	 */
	public setAutodisable(autodisable: string): this {
		this._autodisable = autodisable;
		return this;
	}

	// super//
	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (!this._disabled) {
			var n = this.$n()!;
			this.domListen_(n, 'onFocus', 'doFocus_')
				.domListen_(n, 'onBlur', 'doBlur_');
		}
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		var n = this.$n()!;
		this.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_');

		super.unbind_(skipper, after, keepRod);
	}

	protected override domContent_(): string {
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

	public override domAttrs_(no?: zk.DomAttrsOptions): string {
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

	public override doClick_(evt: zk.Event): void {
		var href = this.getHref();
		// ZK-2506: use iframe to open a 'mailto' href
		if (href && href.toLowerCase().startsWith('mailto:')) {
			var ifrm = jq.newFrame('mailtoFrame', href, null);
			if (zk.chrome) // ZK-3646: for chrome, it need to let iframe exist for a longer time.
				setTimeout(() => { jq(ifrm).remove(); }, 100);
			else
				jq(ifrm).remove();
			evt.stop();
		}
		// Bug ZK-2422
		if (zk.ie && zk.ie < 11 && !href) {
			evt.stop({dom: true});
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
zul.wgt.A = zk.regClass(A);