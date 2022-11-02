/* Style.ts

	Purpose:

	Description:

	History:
		Wed Jan 14 15:28:14     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The style component used to specify CSS styles for the owner desktop.
 * <p>
 * Note: a style component can appear anywhere in a ZUML page, but it affects
 * all components in the same desktop.
 * <p>
 * Note: if the src and content properties are both set, the later one overrides
 * the previous one.
 */
@zk.WrapClass('zul.utl.Style')
export class Style extends zk.Widget {
	/** @internal */
	_src?: string;
	/** @internal */
	_content?: string;
	/** @internal */
	_media?: string;

	/**
	 * @returns the URI of an external style sheet.
	 * @defaultValue `null`.
	 */
	getSrc(): string | undefined {
		return this._src;
	}

	/**
	 * Sets the URI of an external style sheet.
	 * <p>Calling this method implies setContent(null).
	 * @param src - the URI of an external style sheet
	 */
	setSrc(src: string, opts?: Record<string, boolean>): this {
		const o = this._src;
		this._src = src;

		if (o !== src || opts?.force) {
			delete this._content;
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the content of this style tag.
	 * @since 5.0.8
	 */
	getContent(): string | undefined {
		return this._content;
	}

	/**
	 * Sets the content of this style tag.
	 * <p>Calling this method implies setSrc(null).
	 * @param content - the content of this style tag.
	 */
	setContent(content: string, opts?: Record<string, boolean>): this {
		const o = this._content;
		this._content = content;

		if (o !== content || opts?.force) {
			delete this._src;
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the media dependencies for this style sheet.
	 * @defaultValue `null`
	 * <p>Refer to <a href="http://www.w3.org/TR/CSS2/media.html">media-depedent style sheet</a> for details.
	 * @since 5.0.3
	 */
	getMedia(): string | undefined {
		return this._media;
	}

	/**
	 * Sets the media dependencies for this style sheet.
	 * @param media - the media of this style sheet.
	 * @since 5.0.3
	 */
	setMedia(media: string, opts?: Record<string, boolean>): this {
		const o = this._media;
		this._media = media;

		if (o !== media || opts?.force) {
			var n = this.$n('real');
			if (n) (n as HTMLStyleElement).media = media;
		}

		return this;
	}
}