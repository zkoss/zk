/* Fisheye.js

	Purpose:
		
	Description:
		
	History:
		Thu May 15 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

zkex.menu.Fisheye = zk.$extends(zul.Widget, {
	_image: "",
	_label: "",
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-fisheye";
	},
	setWidth: function () {},
	setHeight: function() {},
	isChildable: function(){
		return false;
	},
	doMouseOver_: function (evt) {
		if (!evt) evt = window.event;
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
		if (!evt) evt = window.event;
		this.getSubnode("label").style.display = "none";
	},
	bind_: function () {//after compose
		this.$supers('bind_', arguments);
		var cmp=this.getNode(), 
			img=this.getSubnode("img"),
			label=this.getSubnode("label");
		zDom.disableSelection(cmp);
		if (zk.ie6Only && img.src.endsWith(".png")) {
			img.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+img.src+"', sizingMethod='scale')";
			img.src = zAu.comURI('web/img/spacer.gif');
		}
		
		// store the two attributes for better performance.
		cmp.mh = zDom.sumStyles(label, "tb", zDom.margins);
		cmp.mw = zDom.sumStyles(label, "lr", zDom.margins);
	},
	unbind_: function () {
		this.$supers('unbind_', arguments);
	}
})

//add getter and setter part
zk.def(zkex.menu.Fisheye, { 
	label: function () {
		if(this.getNode()){
			this.rerender();
		}
	},
	image: function () {
		if(this.getNode()){
			this.rerender();
		}
	}
});