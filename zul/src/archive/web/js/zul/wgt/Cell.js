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
 * The generic cell component to be embedded into {@link Row} or
 * {@link zul.box.Box} for fully control style and layout.
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
	_colHtmlPre: function () {
		var s = '',
			p = this.parent;
		if(zk.isLoaded('zkex.grid') && p.$instanceof(zkex.grid.Group) && this == p.firstChild)
			s += p.domContent_();
		return s;
	},
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments);
		if (this._align)
			style += ' text-align:' + this._align + ';';
		if (this._valign)
			style += ' vertical-align:' + this._valign + ';';
		return style;
	},
	//super//
	domAttrs_: function (no) {
		var s = this.$supers('domAttrs_', arguments), v;	
		if ((v = this._colspan) != 1)
			s += ' colspan="' + v + '"';
		if ((v = this._rowspan) != 1)
			s += ' rowspan="' + v + '"';
			
		var m1, m2 = zUtl.parseMap(s, ' ', '"');		
		switch (this._getParentType()) {
		case this._rowType:
			m1 = zUtl.parseMap(this._getRowAttrs(), ' ', '"');
			break;
		case this._boxType:
			m1 = zUtl.parseMap(this._getBoxAttrs(), ' ', '"');
			break;
		}
		if (m1) {
			//Bug ZK-1349: merge user style and row style instead of override
			var s1 = m1.style,
				s2 = m2.style,
				style;
			if (s1 && s2) {
				s1 = zUtl.parseMap(s1.replace(/"/g, '').replace(/:/g, '='), ';');
				s2 = zUtl.parseMap(s2.replace(/"/g, '').replace(/:/g, '='), ';');
				zk.copy(s1, s2);
				style = zUtl.mapToString(s1, ':', ';');
			}
			zk.copy(m1, m2);
			if (style)
				m1.style = '"' + style + '"';
		}
		return ' ' + zUtl.mapToString(m1);
	},
	deferRedrawHTML_: function (out) {
		out.push('<td', this.domAttrs_({domClass:1}), ' class="z-renderdefer"></td>');
	}
});