/* Fisheye.js

	Purpose:
		
	Description:
		
	History:
		Thu May 15 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

zkex.menu.Fisheye = zk.$extends(zul.LabelImageWidget, {
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-fisheye";
	},

	doMouseOver_: function (evt) {
		//not callback
		var cmp = this.getNode(),
			label = this.getSubnode("label");
			
		if (this._label != "") {
			label.style.display = "block";
			label.style.visibility = "hidden";
		}
		var meta = this.parent;
		if (meta) {
			if (!meta.active)
				meta.active = true;
			meta._fixLab(this);
		}
		zDom.cleanVisibility(label);
	},
	doMouseOut_: function (evt) {
		//not callback
		this.getSubnode("label").style.display = "none";
	},
	bind_: function () {//after compose
		this.$supers('bind_', arguments);
		var cmp=this.getNode(), 
			img=this.getSubnode("img"),
			label=this.getSubnode("label");
		zDom.disableSelection(cmp);
		
		// store the two attributes for better performance.
		this._mh = zDom.sumStyles(label, "tb", zDom.margins);
		this._mw = zDom.sumStyles(label, "lr", zDom.margins);
	}
});