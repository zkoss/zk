/* Columns.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 24 15:25:32     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Columns = zk.$extends(zul.grid.HeadersWidget, {
	_mpop: "none",
	_columnshide: true,
	_columnsgroup: true,
	
	getGrid: function () {
		return this.parent;
	},
	setColumnshide: function (columnshide) {
		if (this._columnshide != columnshide) {
			this._columnshide = columnshide;
			//postOnInitLater();
			//smartUpdate("z.columnshide", _columnshide);
		}
	},
	isColumnshide: function () {
		return this._columnshide;
	},
	setColumnsgroup: function (columnsgroup) {
		if (this._columnsgroup != columnsgroup) {
			this._columnsgroup = columnsgroup;
			//postOnInitLater();
			//smartUpdate("z.columnsgroup", _columnsgroup);
		}
	},
	isColumnsgroup: function () {
		return this._columnsgroup;
	},
	getMenupopup: function () {
		return this._mpop;
	},
	setMenupopup: function (mpop) {
		/**if (!Objects.equals(_mpop, mpop)) {
			_mpop = mpop;
			invalidate();
			postOnInitLater();
		}*/
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
			this._mpop = mpop;
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
