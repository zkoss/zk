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
	_src?: string;
	_hover?: string;
	_align?: string;
	_hspace?: string;
	_vspace?: string;
	_preloadImage?: boolean;

	/** Returns the source URI of the image.
	 * <p>Default: null.
	 * @return String
	 */
	getSrc(): string | undefined {
		return this._src;
	}

	/** Sets the source URI of the image.
	 * @param String src the URI of the image source
	 */
	setSrc(v: string, opts?: Record<string, boolean>): this {
		const o = this._src;
		this._src = v;

		if (o !== v || (opts && opts.force)) {
			if (v && this._preloadImage) zUtl.loadImage(v);
			var n = this.getImageNode();
			if (n) n.src = v || '';
		}

		return this;
	}

	// for zephyr to treat as "src" attribute at client side
	getContent(): string | undefined {
		return this.getSrc();
	}
	// for zephyr to treat as "src" attribute at client side
	setContent(v: string, opts?: Record<string, boolean>): this {
		return this.setSrc(v, opts);
	}

	/** Returns the URI of the hover image.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Default: null.
	 * @return String
	 */
	getHover(): string | undefined {
		return this._hover;
	}

	/** Sets the image URI.
	 * The hover image is used when the mouse is moving over this component.
	 * @param String hover
	 */
	setHover(hover: string): this {
		this._hover = hover;
		return this;
	}

	/** Returns the alignment.
	 * <p>Default: null (use browser default).
	 * @return String
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	getAlign(): string | undefined {
		return this._align;
	}

	/** Sets the alignment: one of top, texttop, middle, absmiddle,
	 * bottom, absbottom, baseline, left, right and center.
	 * @param String align
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	setAlign(v: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.getImageNode();
			if (n) n.align = v || '';
		}

		return this;
	}

	/** Returns number of pixels of extra space to the left and right
	 * side of the image.
	 * <p>Default: null (use browser default).
	 * @return String
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	getHspace(): string | undefined {
		return this._hspace;
	}

	/** Sets number of pixels of extra space to the left and right
	 * side of the image.
	 * @param String hspace
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	setHspace(v: string, opts?: Record<string, boolean>): this {
		const o = this._hspace;
		this._hspace = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.getImageNode();
			if (n) n.hspace = v as unknown as number;
		}

		return this;
	}

	/** Returns number of pixels of extra space to the top and bottom
	 * side of the image.
	 * <p>Default: null (use browser default).
	 * @return String
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	getVspace(): string | undefined {
		return this._vspace;
	}

	/** Sets number of pixels of extra space to the top and bottom
	 * side of the image.
	 * @param String vspace
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	setVspace(v: string, opts?: Record<string, boolean>): this {
		const o = this._vspace;
		this._vspace = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.getImageNode();
			if (n) n.vspace = v as unknown as number;
		}

		return this;
	}

	/**
	 * Returns the image node if any.
	 * @return DOMElement
	 */
	getImageNode(): HTMLImageElement | null | undefined {
		return this.$n();
	}

	//super
	override doMouseOver_(evt: zk.Event): void {
		var hover = this._hover;
		if (hover) {
			var img = this.getImageNode();
			if (img) img.src = hover;
		}
		super.doMouseOver_(evt);
	}

	override doMouseOut_(evt: zk.Event): void {
		if (this._hover) {
			var img = this.getImageNode();
			if (img) img.src = this._src || '';
		}
		super.doMouseOut_(evt);
	}

	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var attr = super.domAttrs_(no);
		if (!no || !no.content)
			attr += this.contentAttrs_();
		return attr;
	}

	/** This method is required only if IMG is placed in the inner.
	 * And, it also has to specify {content:true} when calling [[#domAttrs_]]
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