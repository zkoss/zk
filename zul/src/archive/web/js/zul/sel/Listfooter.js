/* Listfooter.js

	Purpose:
		
	Description:
		
	History:
		Tue Jun  9 18:03:07     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A column of the footer of a list box ({@link Listbox}).
 * Its parent must be {@link Listfoot}.
 *
 * <p>Unlike {@link Listheader}, you could place any child in a list footer.
 * <p>Note: {@link Listcell} also accepts children.
 * <p>Default {@link #getZclass}: z-listfooter.
 */
zul.sel.Listfooter = zk.$extends(zul.mesh.FooterWidget, {
	
	/** Returns the listbox that this belongs to.
	 * @return Listbox
	 */
	getListbox: function () {
		return this.getMeshWidget();
	},
	/** Returns the list header that is in the same column as
	 * this footer, or null if not available.
	 * @return Listheader
	 */
	getListheader: function () {
		return this.getHeaderWidget();
	},
	/** Returns the maximal length for this cell.
	 * If listbox's mold is "select", it is the same as
	 * {@link Select#getMaxlength}
	 * If not, it is the same as the correponding {@link #getListheader}'s 
	 * {@link Listheader#getMaxlength}.
	 *
	 * <p>Note: {@link Option#getMaxlength} is the same as {@link Select#getMaxlength}.
	 * @return int
	 * @since 5.0.5
	 */
	getMaxlength: function () {
		var lc = this.getListheader();
		return lc ? lc.getMaxlength() : 0;
	},
	//@Override
	getZclass: function () {
		return this._zclass == null ? "z-listfooter" : this._zclass;
	},
	//@Override
	domLabel_: function () {
		return zUtl.encodeXML(this.getLabel(), {maxlength: this.getMaxlength()});
	}
});
