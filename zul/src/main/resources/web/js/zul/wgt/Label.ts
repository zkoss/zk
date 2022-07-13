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
 * <p>Default {@link #getZclass}: z-label.
 */
@zk.WrapClass('zul.wgt.Label')
export class Label extends zul.Widget {
	_value = '';
	_maxlength = 0;
	_multiline?: boolean;
	_pre?: boolean;

	/** Returns the value.
	 * <p>Default: "".
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
	 * @return String
	 */
	getValue(): string {
		return this._value;
	}

	/** Sets the value.
	 * @param String value
	 */
	setValue(value: string, opts?: Record<string, boolean>): this {
		const o = this._value;
		this._value = value;

		if (o !== value || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.innerHTML = this.getEncodedText();
		}

		return this;
	}

	/** Returns whether to preserve the new line and the white spaces at the
	 * begining of each line.
	 * @return boolean
	 */
	isMultiline(): boolean | undefined {
		return this._multiline;
	}

	/** Sets whether to preserve the new line and the white spaces at the
	 * begining of each line.
	 * @param boolean multiline
	 */
	setMultiline(multiline: boolean, opts?: Record<string, boolean>): this {
		const o = this._multiline;
		this._multiline = multiline;

		if (o !== multiline || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.innerHTML = this.getEncodedText();
		}

		return this;
	}

	/** Returns whether to preserve the white spaces, such as space,
	 * tab and new line.
	 *
	 * <p>It is the same as style="white-space:pre". However, IE has a bug when
	 * handling such style if the content is updated dynamically.
	 * Refer to Bug 1455584.
	 *
	 * <p>Note: the new line is preserved either {@link #isPre} or
	 * {@link #isMultiline} returns true.
	 * In other words, <code>pre</code> implies <code>multiline</code>
	 * @return boolean
	 */
	isPre(): boolean | undefined {
		return this._pre;
	}

	/** Sets whether to preserve the white spaces, such as space,
	 * tab and new line.
	 * @param boolean pre
	 */
	setPre(pre: boolean, opts?: Record<string, boolean>): this {
		const o = this._pre;
		this._pre = pre;

		if (o !== pre || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.innerHTML = this.getEncodedText();
		}

		return this;
	}

	/** Returns the maximal length of the label.
	 * <p>Default: 0 (means no limitation)
	 * @return int
	 */
	getMaxlength(): number {
		return this._maxlength;
	}

	/** Sets the maximal length of the label.
	 * @param int maxlength
	 */
	setMaxlength(maxlength: number, opts?: Record<string, boolean>): this {
		const o = this._maxlength;
		this._maxlength = maxlength;

		if (o !== maxlength || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.innerHTML = this.getEncodedText();
		}

		return this;
	}

	/**
	 * Returns the encoded text.
	 * @see zUtl#encodeXML
	 * @return String
	 */
	getEncodedText(): string {
		return zUtl.encodeXML(this._value, {multiline: this._multiline, pre: this._pre, maxlength: this._maxlength});
	}

	// fix for HTML5 doctype that give a special gap between top and button
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