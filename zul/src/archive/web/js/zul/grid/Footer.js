/* Footer.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 23 12:26:51     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A column of the footer of a grid ({@link Grid}).
 * Its parent must be {@link Foot}.
 *
 * <p>Unlike {@link Column}, you could place any child in a grid footer.
 * <p>Default {@link #getZclass}: z-footer.
 */
zul.grid.Footer = zk.$extends(zul.mesh.FooterWidget, {
	
	/** Returns the grid that this belongs to.
	 * @return zul.grid.Grid
	 */
	getGrid: function () {
		return this.getMeshWidget();
	},
	/** Returns the column that is in the same column as
	 * this footer, or null if not available.
	 * @return zul.grid.Column
	 */
	getColumn: function () {
		return this.getHeaderWidget();
	},
	getZclass: function () {
		return this._zclass == null ? "z-footer" : this._zclass;
	}
});