/* Toolbar.js

	Purpose:

	Description:

	History:
		Sat Dec 24 12:58:43	 2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Toolbar = zk.$extends(zul.Widget, {
	_orient: "horizontal",
	_align: "start",
	getAlign: function(){
		return this._align;
	},
	setAlign: function(align){
		if (!align) 
			align = "start";
		if (this._align != align) {
			this._align = align;
			this.rerender();
		}
	},
	getOrient: function(){
		return this._orient;
	},
	setOrient: function(orient){
		if (this._orient != orient) {
			this._orient = orient;
			this.rerender();
		}
	},
	// super
	getZclass: function(){
		var zcls = this._zclass;
		return zcls ? zcls : "z-toolbar" +
		(this.inPanelMold() ? "-panel" : "");
	},
	// protected 
	inPanelMold: function(){
		return this._mold == "panel";
	},
	insertBefore: function(child, sibling){
		if (this.$super('insertBefore', child, sibling)) {
			if (this.inPanelMold()) 
				this.rerender();
			return true;
		}
		return false;
	}
});
