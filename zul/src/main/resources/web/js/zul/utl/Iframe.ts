/* Iframe.ts

	Purpose:

	Description:

	History:
		Thu Mar 19 11:47:53     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The utility widgets, such as iframe and script.
 */
//zk.$package('zul.utl');

/**
 * Includes an inline frame.
 *
 * <p>Unlike HTML iframe, this component doesn't have the frameborder
 * property. Rather, use the CSS style to customize the border (like
 * any other components).
 */
@zk.WrapClass('zul.utl.Iframe')
export class Iframe extends zul.Widget<HTMLIFrameElement> {
	/** @internal */
	_scrolling = 'auto';
	/** @internal */
	_src?: string;
	/** @internal */
	_align?: string;
	/** @internal */
	_name?: string;
	/** @internal */
	_autohide = false;

	/**
	 * @returns the src.
	 * @defaultValue `null`.
	 */
	getSrc(): string | undefined {
		return this._src;
	}

	/**
	 * Sets the src.
	 * @param src - the source URL. If null or empty, nothing is included.
	 */
	setSrc(src: string, opts?: Record<string, boolean>): this {
		const o = this._src;
		this._src = src;

		if (o !== src || opts?.force) {
			var n = this.$n();
			if (n) n.src = src || '';
		}

		return this;
	}

	/**
	 * @returns the scroll bars.
	 * @defaultValue `"auto"`
	 * @deprecated as of release 7.0.0, use CSS instead.
	 */
	getScrolling(): string | undefined {
		return this._scrolling;
	}

	/**
	 * Define scroll bars
	 * @param scrolling - "true", "false", "yes" or "no" or "auto", "auto" by default
	 * If null, "auto" is assumed.
	 * @deprecated as of release 7.0.0, use CSS instead.
	 */
	setScrolling(scrolling: string, opts?: Record<string, boolean>): this {
		const o = this._scrolling;
		this._scrolling = scrolling;

		if (o !== scrolling || opts?.force) {
			if (!scrolling) this._scrolling = scrolling = 'auto';
			var n = this.$n();
			if (n) {
				if (zk.gecko || zk.opera)
					n.scrolling = 'true' === scrolling ? 'yes' : 'false' === scrolling ? 'no' : scrolling;
				else
					this.rerender();
			}
		}

		return this;
	}


	/**
	 * @returns the alignment.
	 * @defaultValue `null` (use browser default).
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	getAlign(): string | undefined {
		return this._align;
	}

	/**
	 * Sets the alignment: one of top, middle, bottom, left, right and
	 * center.
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	setAlign(align: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = align;

		if (o !== align || opts?.force) {
			var n = this.$n();
			if (n) n.align = align || '';
		}

		return this;
	}

	/**
	 * @returns the frame name.
	 * @defaultValue `null` (use browser default).
	 */
	getName(): string | undefined {
		return this._name;
	}

	/**
	 * Sets the frame name.
	 */
	setName(name: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = name;

		if (o !== name || opts?.force) {
			var n = this.$n();
			if (n) n.name = name || '';
		}

		return this;
	}

	/**
	 * @returns whether to automatically hide this component if
	 * a popup or dropdown is overlapped with it.
	 *
	 * @defaultValue `false`.
	 *
	 * <p>If an iframe contains PDF or other non-HTML resource,
	 * it is possible that it obscues the popup that shall be shown
	 * above it. To resolve this, you have to specify autohide="true"
	 * to this component, and specify the following in the page:
	 * ```html
	 * <script content="zk.useStack='auto';"?>
	 * ```
	 * <p>Refer to <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Customization/Stackup_and_Shadow">Stackup and Shadow</a>
	 * for more information.
	 */
	isAutohide(): boolean {
		return this._autohide;
	}

	/**
	 * Sets whether to automatically hide this component if
	 * a popup or dropdown is overlapped with it.
	 */
	setAutohide(autohide: boolean, opts?: Record<string, boolean>): this {
		const o = this._autohide;
		this._autohide = autohide;

		if (o !== autohide || opts?.force) {
			var n = this.$n();
			if (n) jq(n).attr('z_autohide', autohide as unknown as string);
		}

		return this;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (this._src) {
			after?.push(() => {this.$n_().src = this._src!;});
		}
	}

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var /*safe*/ attr = super.domAttrs_(no)
				+ ' src="' + /*safe*/ zjq.src0 + '" frameborder="0"',
		v: string | undefined | boolean = this._scrolling;
		if ('auto' != v)
			attr += ' scrolling="' + ('true' === v ? 'yes' : 'false' === v ? 'no' : v) + '"';
		if ((v = this._align))
			attr += ' align="' + zUtl.encodeXMLAttribute(v) + '"';
		if ((v = this._name))
			attr += ' name="' + zUtl.encodeXMLAttribute(v) + '"';
		if ((v = this._autohide))
			attr += ' z_autohide="' + v + '"';
		return attr;
	}
}
