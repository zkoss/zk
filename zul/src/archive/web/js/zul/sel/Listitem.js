/* Listitem.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:17:40     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listitem = zk.$extends(zul.sel.ItemWidget, {
	getListbox: zul.sel.ItemWidget.prototype.getMeshWidget,
	getListgroup: function () {
		// TODO: this performance is not good.
		if (this.parent && this.parent.hasGroup())
			for (var w = this.previousSibling; w; w = w.previousSibling)
				if (w.$instanceof(zul.sel.Listgroup)) return w;
				
		return null;
	},
	setLabel: function (val) {
		this._autoFirstCell().setLabel(val);
	},
	setImage: function (val) {
		this._autoFirstCell().setImage(val);
	},
	_autoFirstCell: function () {
		if (!this.firstChild)
			this.appendChild(new zul.sel.Listcell());
		return this.firstChild;
	},
	//super//
	getZclass: function () {
		return this._zclass == null ? "z-listitem" : this._zclass;
	},
	domStyle_: function (no) {
		if ((this.$instanceof(zul.sel.Listgroup) || this.$instanceof(zul.sel.Listgroupfoot))
				|| (no && no.visible))
			return this.$supers('domStyle_', arguments);
			
		var style = this.$supers('domStyle_', arguments),
			group = this.getListgroup();
		return group && !group.isOpen() ? style + "display:none;" : style;
	}
});