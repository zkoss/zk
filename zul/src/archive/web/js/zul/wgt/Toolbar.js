/* Toolbar.js

	Purpose:

	Description:

	History:
		Sat Dec 24 12:58:43	 2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A toolbar.
 * 
 * <p>Mold:
 * <ol>
 * <li>default</li>
 * <li>panel: this mold is used for {@link zul.wnd.Panel} component as its
 * foot toolbar.</li>
 * </ol>
 * <p>Default {@link #getZclass}: z-toolbar, if {@link #getMold()} is panel,
 * z-toolbar-panel is assumed.
 */
zul.wgt.Toolbar = zk.$extends(zul.Widget, {
	_orient: "horizontal",
	_align: "start",

	$define: {
		/** Returns the alignment.
		 * <p>Default: null (use browser default).
		 * @return String
		 */
		/** Sets the alignment: one of left, center, right, ustify,
		 * @param String align
		 */
		align: _zkf = function () {
			this.rerender();
		},
		/** Returns the orient.
		 * <p>Default: "horizontal".
		 * @return String
		 */
		/** Sets the orient.
		 * @param String orient either "horizontal" or "vertical".
		 */
		orient: _zkf
	},

	// super
	getZclass: function(){
		var zcls = this._zclass;
		return zcls ? zcls : "z-toolbar"
			+ (this.parent && zk.isLoaded('zul.tab') && this.parent.$instanceof(zul.tab.Tabbox) ? "-tabs" : "") 
			+ (this.inPanelMold() ? "-panel" : "");
	}, 
	/**
	 * Returns whether is in panel mold or not.
	 * @return boolean
	 */
	inPanelMold: function(){
		return this._mold == "panel";
	},
	// protected
	onChildAdded_: function(){
		this.$supers('onChildAdded_', arguments);
		if (this.inPanelMold()) 
			this.rerender();
	},
	onChildRemoved_: function(){
		this.$supers('onChildRemoved_', arguments);
		if (!this.childReplacing_ && this.inPanelMold())
			this.rerender();
	}
	
});
