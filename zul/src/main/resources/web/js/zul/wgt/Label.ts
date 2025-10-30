/* Label.ts

	Purpose:

	Description:

	History:
		Sun Oct  5 00:22:03     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * A label.
 *
 * @defaultValue {@link getZclass}: z-label.
 */
@zk.WrapClass('zul.wgt.Label')
export class Label extends zul.Widget {
	/** @internal */
	_value = '';
	/** @internal */
	_maxlength = 0;
	/** @internal */
	_multiline?: boolean;
	/** @internal */
	_pre?: boolean;

	/**
	 * @returns the value.
	 * @defaultValue `""`.
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
	 */
	getValue(): string {
		return this._value;
	}

	/**
	 * Sets the value.
	 */
	setValue(value: string, opts?: Record<string, boolean>): this {
		const o = this._value;
		this._value = value;

		if (o !== value || opts?.force) {
			var n = this.$n();
			// eslint-disable-next-line @microsoft/sdl/no-inner-html
			if (n) n.innerHTML = /*safe*/ this.getEncodedText();
		}

		return this;
	}

	/**
	 * @returns whether to preserve the new line and the white spaces at the
	 * begining of each line.
	 */
	isMultiline(): boolean {
		return !!this._multiline;
	}

	/**
	 * Sets whether to preserve the new line and the white spaces at the
	 * begining of each line.
	 */
	setMultiline(multiline: boolean, opts?: Record<string, boolean>): this {
		const o = this._multiline;
		this._multiline = multiline;

		if (o !== multiline || opts?.force) {
			var n = this.$n();
			// eslint-disable-next-line @microsoft/sdl/no-inner-html
			if (n) n.innerHTML = /*safe*/ this.getEncodedText();
		}

		return this;
	}

	/**
	 * @returns whether to preserve the white spaces, such as space,
	 * tab and new line.
	 *
	 * <p>It is the same as style="white-space:pre". However, IE has a bug when
	 * handling such style if the content is updated dynamically.
	 * Refer to Bug 1455584.
	 *
	 * <p>Note: the new line is preserved either {@link isPre} or
	 * {@link isMultiline} returns true.
	 * In other words, `pre` implies `multiline`
	 */
	isPre(): boolean {
		return !!this._pre;
	}

	/**
	 * Sets whether to preserve the white spaces, such as space, tab and new line.
	 */
	setPre(pre: boolean, opts?: Record<string, boolean>): this {
		const o = this._pre;
		this._pre = pre;

		if (o !== pre || opts?.force) {
			var n = this.$n();
			// eslint-disable-next-line @microsoft/sdl/no-inner-html
			if (n) n.innerHTML = /*safe*/ this.getEncodedText();
		}

		return this;
	}

	/**
	 * @returns the maximal length of the label.
	 * @defaultValue `0` (means no limitation)
	 */
	getMaxlength(): number {
		return this._maxlength;
	}

	/**
	 * Sets the maximal length of the label.
	 */
	setMaxlength(maxlength: number, opts?: Record<string, boolean>): this {
		const o = this._maxlength;
		this._maxlength = maxlength;

		if (o !== maxlength || opts?.force) {
			var n = this.$n();
			// eslint-disable-next-line @microsoft/sdl/no-inner-html
			if (n) n.innerHTML = /*safe*/ this.getEncodedText();
		}

		return this;
	}

	/**
	 * @returns the encoded text.
	 * @see zUtl#encodeXML
	 */
	getEncodedText(): string {
		return zUtl.encodeXML(this._value, {multiline: this._multiline, pre: this._pre, maxlength: this._maxlength});
	}

	// fix for HTML5 doctype that give a special gap between top and button
	/** @internal */
	override getMarginSize_(attr: zk.FlexOrient): number { //'w' for width or 'h' for height
		var o = super.getMarginSize_(attr);
		if (attr == 'h') {
			var n = this.$n()!,
				oh = zk(n).offsetHeight();
			return o + oh - n.offsetHeight;
		}
		return o;
	}
}