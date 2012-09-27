/* Columns.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 24 15:25:32     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Defines the columns of a grid.
 * Each child of a columns element should be a {@link Column} element.
 * <p>Default {@link #getZclass}: z-columns.
 */
zul.grid.Columns = zk.$extends(zul.mesh.ColumnMenuWidget, {
	
	/** Returns the grid that contains this columns. 
	 * @return zul.grid.Grid
	 */
	getGrid: function () {
		return this.parent;
	},
	rerender: function () {
		if (this.desktop) {
			if (this.parent)
				this.parent.rerender();
			else 
				this.$supers('rerender', arguments);
		}
		return this;
	},
	getGroupPackage_: function () {
		return 'zkex.grid';
	}
});