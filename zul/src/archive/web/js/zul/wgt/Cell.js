/* Cell.js

	Purpose:
		
	Description:
		
	History:
		Mon Aug 31 16:50:22     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Cell = zk.$extends(zul.Widget, {
	_colspan: 1,
	_rowspan: 1,
	_rowType: 0,
	_boxType: 1,
	
	$define: {
		colspan: function (v) {
			var n = this.$n();
			if (n)
				n.colSpan = v;
		},
		rowspan: function (v) {
			var n = this.$n();
			if (n)
				n.rowSpan = v;
		},
		align: function (v) {
			var n = this.$n();
			if (n)
				n.align = v;
		},
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