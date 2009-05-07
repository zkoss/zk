/* Auxheader.js

	Purpose:
		
	Description:
		
	History:
		Mon May  4 17:00:30     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.def(zul.mesh.Auxheader = zk.$extends(zul.mesh.HeaderWidget, {
	_colspan: 1,
	_rowspan: 1,

	//super//
	domAttrs_: function () {
		var s = this.$supers('domAttrs_', arguments), v;
		if ((v = this._colspan) != 1)
			s += ' colspan="' + v + '"';
		if ((v = this._rowspan) != 1)
			s += ' rowspan="' + v + '"';
		return s;
	},
	getZclass: function () {
		return this._zclass == null ? "z-auxheader" : this._zclass;
	}
}), { //zk.def
	colspan: function (v) {
		var n = this.getNode();
		if (n)
			if (zk.ie) this.rerender(); //IE's limitation
			else n.colSpan = v;
	},
	rowspan: function (v) {
		var n = this.getNode();
		if (n)
			if (zk.ie) this.rerender(); //IE's limitation
			else n.rowSpan = v;
	}
});