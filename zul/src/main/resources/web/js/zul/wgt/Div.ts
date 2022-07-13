/* Div.ts

	Purpose:

	Description:

	History:
		Sun Oct  5 00:20:23     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The same as HTML DIV tag.
 * <p>Note: a {@link zul.wnd.Window} without title and caption has the same visual effect
 * as {@link Div}, but {@link Div} doesn't implement IdSpace.
 * In other words, {@link Div} won't affect the uniqueness of identifiers.
 */
@zk.WrapClass('zul.wgt.Div')
export class Div extends zul.Widget {
	_align?: string;

	/** Returns the alignment.
	 * <p>Default: null (use browser default).
	 * @return String
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	getAlign(): string | undefined {
		return this._align;
	}

	/** Sets the alignment: one of left, center, right, ustify,
	 * @param String align
	 * @deprecated as of release 6.0.0, use CSS instead.
	 */
	setAlign(v: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n)
				(n as HTMLDivElement).align = v;
		}

		return this;
	}

	//super//
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var align = this._align,
			attr = super.domAttrs_(no);
		return align != null ? attr + ' align="' + align + '"' : attr;
	}
}