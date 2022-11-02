/* Image.ts

	Purpose:

	Description:

	History:
		Thu Mar 26 15:07:07     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An image.
 *
 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Customization/Alphafix_for_IE6">how to fix the alpha transparency problem of PNG files found in IE6?</a>
 */
@zk.WrapClass('zul.wgt.Image')
export class Image extends zul.Widget<HTMLImageElement> {
	/** @internal */
	_src?: string;
	/** @internal */
	_hover?: string;
	/** @internal */
	_align?: string;
	/** @internal */
	_hspace?: string;
	/** @internal */
	_vspace?: string;
	/** @internal */
	_preloadImage?: boolean;

	/**
	 * @returns the source URI of the image.
	 * @defaultValue `null`.
	 */
	getSrc(): string | undefined {
		return this._src;
	}

	/**
	 * Sets the source URI of the image.
	 * @param src - the URI of the image source
	 */
	setSrc(src: string, opts?: Record<string, boolean>): this {
		const o = this._src;
		this._src = src;

		if (o !== src || opts?.force) {
			if (src && this._preloadImage) zUtl.loadImage(src);
			var n = this.getImageNode();
			if (n) n.src = src || '';
		}

		return this;
	}

	// for stateless to treat as "src" attribute at client side
	getContent(): string | undefined {
		return this.getSrc();
	}
	// for stateless to treat as "src" attribute at client side
	setContent(content: string, opts?: Record<string, boolean>): this {
		return this.setSrc(content, opts);
	}

	/**
	 * @returns the URI of the hover image.
	 * The hover image is used when the mouse is moving over this component.
	 * @defaultValue `null`.
	 */
	getHover(): string | undefined {
		return this._hover;
	}

	/**
	 * Sets the image URI.
	 * The hover image is used when the mouse is moving over this component.
	 */
	setHover(hover: string): this {
		this._hover = hover;
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
	 * Sets the alignment: one of top, texttop, middle, absmiddle,
	 * bottom, absbottom, baseline, left, right and center.
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	setAlign(align: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = align;

		if (o !== align || opts?.force) {
			var n = this.getImageNode();
			if (n) n.align = align || '';
		}

		return this;
	}

	/**
	 * @returns number of pixels of extra space to the left and right
	 * side of the image.
	 * @defaultValue `null` (use browser default).
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	getHspace(): string | undefined {
		return this._hspace;
	}

	/**
	 * Sets number of pixels of extra space to the left and right side of the image.
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	setHspace(hspace: string, opts?: Record<string, boolean>): this {
		const o = this._hspace;
		this._hspace = hspace;

		if (o !== hspace || opts?.force) {
			var n = this.getImageNode();
			if (n) n.hspace = hspace as unknown as number;
		}

		return this;
	}

	/**
	 * @returns number of pixels of extra space to the top and bottom side of the image.
	 * @defaultValue `null` (use browser default).
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	getVspace(): string | undefined {
		return this._vspace;
	}

	/**
	 * Sets number of pixels of extra space to the top and bottom side of the image.
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	setVspace(vspace: string, opts?: Record<string, boolean>): this {
		const o = this._vspace;
		this._vspace = vspace;

		if (o !== vspace || opts?.force) {
			var n = this.getImageNode();
			if (n) n.vspace = vspace as unknown as number;
		}

		return this;
	}

	/**
	 * @returns the image node if any.
	 */
	getImageNode(): HTMLImageElement | undefined {
		return this.$n();
	}

	//super
	/** @internal */
	override doMouseOver_(evt: zk.Event): void {
		var hover = this._hover;
		if (hover) {
			var img = this.getImageNode();
			if (img) img.src = hover;
		}
		super.doMouseOver_(evt);
	}

	/** @internal */
	override doMouseOut_(evt: zk.Event): void {
		if (this._hover) {
			var img = this.getImageNode();
			if (img) img.src = this._src || '';
		}
		super.doMouseOut_(evt);
	}

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var attr = super.domAttrs_(no);
		if (!no || !no.content)
			attr += this.contentAttrs_();
		return attr;
	}

	/**
	 * This method is required only if IMG is placed in the inner.
	 * And, it also has to specify `{content:true}` when calling {@link domAttrs_}
	 * @internal
	 */
	contentAttrs_(): string {
		var attr = ' src="' + (this._src || '') + '"', v;
		if (v = this._align)
			attr += ' align="' + v + '"';
		if (v = this._hspace)
			attr += ' hspace="' + v + '"';
		if (v = this._vspace)
			attr += ' vspace="' + v + '"';
		return attr;
	}
}