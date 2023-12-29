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
 * @defaultValue {@link getZclass}: z-auxheader.
 */
@zk.WrapClass('zul.mesh.Auxheader')
export abstract class Auxheader extends zul.mesh.HeaderWidget {
	/** @internal */
	override _colspan = 1;
	/** @internal */
	override _rowspan = 1;

	/**
	 * @returns number of columns to span this header.
	 * @defaultValue `1`.
	 */
	getColspan(): number {
		return this._colspan;
	}

	/**
	 * Sets the number of columns to span this header.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	setColspan(colspan: number, opts?: Record<string, boolean>): this {
		const o = this._colspan;
		this._colspan = colspan;

		if (o !== colspan || opts?.force) {
			var n = this.$n();
			if (n) {
				n.colSpan = colspan;
			}
		}

		return this;
	}

	/**
	 * @returns number of rows to span this header.
	 * @defaultValue `1`.
	 */
	getRowspan(): number {
		return this._rowspan;
	}

	/**
	 * Sets the number of rows to span this header.
	 * <p>It is the same as the rowspan attribute of HTML TD tag.
	 */
	setRowspan(rowspan: number, opts?: Record<string, boolean>): this {
		const o = this._rowspan;
		this._rowspan = rowspan;

		if (o !== rowspan || opts?.force) {
			var n = this.$n();
			if (n) {
				n.rowSpan = rowspan;
			}
		}

		return this;
	}
	
	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var /*safe*/ s = super.domAttrs_(no);
		if (this._colspan != 1)
			s += ` colspan="${this._colspan}"`;
		if (this._rowspan != 1)
			s += ` rowspan="${this._rowspan}"`;
		return DOMPurify.sanitize(s);
	}
}