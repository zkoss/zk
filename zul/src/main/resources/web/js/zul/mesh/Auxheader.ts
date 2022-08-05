/* Auxheader.ts

	Purpose:

	Description:

	History:
		Mon May  4 17:00:30     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An auxiliary header.
 * <p>Default {@link #getZclass}: z-auxheader.
 */
@zk.WrapClass('zul.mesh.Auxheader')
export abstract class Auxheader extends zul.mesh.HeaderWidget {
	_colspan = 1;
	_rowspan = 1;

	/** Returns number of columns to span this header.
	 * Default: 1.
	 * @return int
	 */
	getColspan(): number {
		return this._colspan;
	}

	/** Sets the number of columns to span this header.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 * @param int colspan
	 */
	setColspan(v: number, opts?: Record<string, boolean>): this {
		const o = this._colspan;
		this._colspan = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n) {
				n.colSpan = v;
			}
		}

		return this;
	}

	/** Returns number of rows to span this header.
	 * Default: 1.
	 * @return int
	 */
	getRowspan(): number {
		return this._rowspan;
	}

	/** Sets the number of rows to span this header.
	 * <p>It is the same as the rowspan attribute of HTML TD tag.
	 * @param int rowspan
	 */
	setRowspan(v: number, opts?: Record<string, boolean>): this {
		const o = this._rowspan;
		this._rowspan = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n) {
				n.rowSpan = v;
			}
		}

		return this;
	}

	//super//
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var s = super.domAttrs_(no), v;
		if ((v = this._colspan) != 1)
			s += ' colspan="' + v + '"';
		if ((v = this._rowspan) != 1)
			s += ' rowspan="' + v + '"';
		return s;
	}
}