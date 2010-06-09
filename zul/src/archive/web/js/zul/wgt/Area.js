/* Area.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 15:54:35     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An area of a {@link Imagemap}.
 */
zul.wgt.Area = zk.$extends(zk.Widget, {
	$define: {
		/** Returns the shape of this area.
		 * <p>Default: null (means rectangle).
		 * @return String
		 */
		/** Sets the shape of this area.
		 *
		 * @param String shape the shape only allows one of
		 * null, "rect", "rectangle", "circle", "circ", "ploygon", and "poly".
		 */
		shape: function (v) {
			var n = this.$n();
			if (n) n.shape = v || '';
		},
		/** 
		 * Returns the coordination of this area.
		 * @return String
		 */
		/** Sets the coords of this area.
		 * Its content depends on {@link #getShape}:
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
		 * @param String coords
		 */
		coords: function (coords) {
			var n = this.$n();
			if (n) n.coords = v || '';
		}
	},

	//super//
	doClick_: function (evt) {
		if (zul.wgt.Imagemap._toofast()) return;

		var area = this.id || this.uuid;
		this.parent.fire('onClick', {area: area}, {ctl:true});
		evt.stop();
	},

	domAttrs_: function (no) {
		var attr = this.$supers('domAttrs_', arguments)
			+ ' href="javascript:;"', v;
		if (v = this._coords) 
			attr += ' coords="' + v + '"';
		if (v = this._shape) 
			attr += ' shape="' + v + '"';
		return attr;
	}
});
