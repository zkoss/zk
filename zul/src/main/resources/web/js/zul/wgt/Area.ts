/* Area.ts

	Purpose:

	Description:

	History:
		Thu Mar 26 15:54:35     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An area of a {@link Imagemap}.
 */
@zk.WrapClass('zul.wgt.Area')
export class Area extends zk.Widget<HTMLAreaElement> {
	/** @internal */
	_shape?: string;
	/** @internal */
	_coords?: string;

	/**
	 * @returns the shape of this area.
	 * @defaultValue `null` (means rectangle).
	 */
	getShape(): string | undefined {
		return this._shape;
	}

	/**
	 * Sets the shape of this area.
	 *
	 * @param shape - the shape only allows one of
	 * null, "rect", "rectangle", "circle", "circ", "ploygon", and "poly".
	 */
	setShape(shape: string, opts?: Record<string, boolean>): this {
		const o = this._shape;
		this._shape = shape;

		if (o !== shape || opts?.force) {
			var n = this.$n();
			if (n) n.shape = shape || '';
		}

		return this;
	}

	/**
	 * @returns the coordination of this area.
	 */
	getCoords(): string | undefined {
		return this._coords;
	}

	/**
	 * Sets the coords of this area.
	 * Its content depends on {@link getShape}:
	 * <dl>
	 * <dt>circle</dt>
	 * <dd>coords="x,y,r"</dd>
	 * <dt>polygon</dt>
	 * <dd>coords="x1,y1,x2,y2,x3,y3..."<br/>
	 * The polygon is automatically closed, so it is not necessary to repeat
	 * the first coordination.</dd>
	 * <dt>rectangle</dt>
	 * <dd>coords="x1,y1,x2,y2"</dd>
	 * </dl>
	 *
	 * <p>Note: (0, 0) is the upper-left corner.
	 */
	setCoords(coords: string, opts?: Record<string, boolean>): this {
		const o = this._coords;
		this._coords = coords;

		if (o !== coords || opts?.force) {
			// ZK-1892 rename the argument
			var n = this.$n();
			if (n) n.coords = coords || '';
		}

		return this;
	}

	/** @internal */
	override doClick_(evt: zk.Event): void {
		if (zul.wgt.Imagemap._toofast()) return;

		var area = this.id || this.uuid;
		this.parent!.fire('onClick', {area: area}, {ctl: true});
		evt.stop();
	}

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var attr = super.domAttrs_(no)
			+ ' href="javascript:;"', v;
		if (v = this._coords)
			attr += ' coords="' + v + '"';
		if (v = this._shape)
			attr += ' shape="' + v + '"';
		return attr;
	}
}