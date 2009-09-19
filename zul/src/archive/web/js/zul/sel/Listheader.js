/* Listheader.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:25:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listheader = zk.$extends(zul.mesh.SortWidget, {
	getListbox: zul.mesh.HeaderWidget.prototype.getMeshWidget,
	
	getMeshBody: zul.mesh.HeaderWidget.prototype.getMeshWidget,
	
	$define: {
		maxlength: [function (v) {
			return !v || v < 0 ? 0 : v; 
		}, function () {
			if (this.desktop)
				this.updateCells_();
		}]
	},
	updateCells_: function () {
		var box = this.getListbox();
		if (box == null || box.getMold() == 'select')
			return;

		var jcol = this.getChildIndex();
		for (var it = this.getBodyWidgetIterator(), w; (w = it.next());)
			if (jcol < w.nChildren)
				return w.getChildAt(jcol).rerender();
	},
	//super//
	getZclass: function () {
		return this._zclass == null ? "z-listheader" : this._zclass;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var cm = this.$n('cm');
		if (cm) {
			this.getListbox()._headercm = cm;
			this.domListen_(cm, 'onClick');
		}
	},
	unbind_: function () {
		this.$supers('unbind_', arguments);
		var cm = this.$n('cm');
		if (cm) {
			this.getListbox()._headercm = null;
			this.domUnlisten_(cm, 'onClick');
		}
	},
	_doClick: function (evt) {
		var box = this.getListbox();
		if (evt.domTarget.checked)
			box.selectAll(true, evt)
		else
			box._select(null, evt);
	},
	domContent_: function () {
		var s = this.$supers('domContent_', arguments),
			box = this.getListbox();
		if (box != null && this.parent.firstChild == this 
		&& box.isCheckmark() && box.isMultiple())
			s = '<input type="checkbox" id="' + this.uuid + '-cm"/>'
				+ (s ? '&nbsp;' + s:'');
		return s;
	}
});
