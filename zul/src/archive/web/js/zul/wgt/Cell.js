/* Cell.js

	Purpose:
		
	Description:
		
	History:
		Mon Aug 31 16:50:22     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The generic cell component to be embedded into {@link Row} or {@link Vbox}
 * or {@link Hbox} for fully control style and layout.
 * 
 * <p>Default {@link #getZclass}: z-cell.
 * @import zul.grid.*
 * @import zul.box.*
 */
zul.wgt.Cell = zk.$extends(zul.Widget, {
	_colspan: 1,
	_rowspan: 1,
	_rowType: 0,
	_boxType: 1,
	
	$define: {
		/** Returns number of columns to span.
		 * Default: 1.
		 * @return int
		 */
		/** Sets the number of columns to span.
		 * <p>It is the same as the colspan attribute of HTML TD tag.
		 * @param int colspan
		 */
		colspan: function (v) {
			var n = this.$n();
			if (n)
				n.colSpan = v;
		},
		/** Returns number of rows to span.
		 * Default: 1.
		 * @return int
		 */
		/** Sets the number of rows to span.
		 * <p>It is the same as the rowspan attribute of HTML TD tag.
		 * @param int rowspan
		 */
		rowspan: function (v) {
			var n = this.$n();
			if (n)
				n.rowSpan = v;
		},
		/** Returns the horizontal alignment.
		 * <p>Default: null (system default: left unless CSS specified).
		 * @return String
		 */
		/** Sets the horizontal alignment.
		 * @param String align
		 */
		align: function (v) {
			var n = this.$n();
			if (n)
				n.align = v;
		},
		/** Returns the vertical alignment.
		 * <p>Default: null (system default: top).
		 * @return String
		 */
		/** Sets the vertical alignment of this grid.
		 * @param String valign
		 */
		valign: function (v) {
			var n = this.$n();
			if (n)
				n.valign = v;
		}
	},
	_getParentType: function () {
		var isRow = zk.isLoaded('zul.grid') && this.parent.$instanceof(zul.grid.Row);
		if (!isRow) {
			return zk.isLoaded('zul.box') && this.parent.$instanceof(zul.box.Box) ?
					this._boxType : null;
		}
		return this._rowType;
	},
	_getRowAttrs: function () {
		return this.parent._childAttrs(this, this.getChildIndex());
	},
	_getBoxAttrs: function () {
		return this.parent._childInnerAttrs(this);
	},
	//super//
	domAttrs_: function (no) {
		var s = this.$supers('domAttrs_', arguments), v;	
		if ((v = this._colspan) != 1)
			s += ' colspan="' + v + '"';
		if ((v = this._rowspan) != 1)
			s += ' rowspan="' + v + '"';
		if ((v = this._align))
			s += ' align="' + v + '"';
		if ((v = this._valign))
			s += ' valign="' + v + '"';
			
		var m1, m2 = zUtl.parseMap(s, ' ', '"');		
		switch (this._getParentType()) {
		case this._rowType:
			m1 = zUtl.parseMap(this._getRowAttrs(), ' ', '"');
			break;
		case this._boxType:
			m1 = zUtl.parseMap(this._getBoxAttrs(), ' ', '"');
			break;
		}
		if (m1) zk.copy(m1, m2);
		return ' ' + zUtl.mapToString(m1);
	},
	getZclass: function () {
		return this._zclass == null ? "z-cell" : this._zclass;
	}
});