/* Area.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 15:54:35     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.def(zul.wgt.Area = zk.$extends(zk.Widget, {
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
}), { //zk.def
	shape: function (v) {
		var n = this.getNode();
		if (n) n.shape = v || '';
	},
	coords: function (coords) {
		var n = this.getNode();
		if (n) n.coords = v || '';
	}
});
