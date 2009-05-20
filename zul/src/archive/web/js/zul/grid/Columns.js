/* Columns.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 24 15:25:32     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Columns = zk.$extends(zul.mesh.HeadWidget, {
	_menupopup: "none",
	_columnshide: true,
	_columnsgroup: true,

	$define: {
		columnshide: null, //TODO: postOnInitLater()?
		columnsgroup: null, //TODO: postOnInitLater()?
		columnsgroup: null,
		menupopup: null //TODO: rerender, postOnInitLater?
	},
	
	getGrid: function () {
		return this.parent;
	},
	rerender: function () {
		if (this.desktop) {
			if (this.parent)
				this.parent.rerender();
			else 
				this.$superts('rerender', arguments);
		}
	},
	setPopup: function (mpop) {
		if (zk.Widget.isInstance(mpop))
			this._menupopup = mpop;
	},
	_getMpopId: function () {
		/**final String mpop = getMenupopup();
		if ("none".equals(mpop)) return "zk_n_a";
		if ("auto".equals(mpop)) return _menupopup.getId();
		return mpop;*/
	},
	getZclass: function () {
		return this._zclass == null ? "z-columns" : this._zclass;
	}
});
