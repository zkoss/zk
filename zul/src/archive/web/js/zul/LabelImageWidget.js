/* LabelImageWidget.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 14:59:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.def(zul.LabelImageWidget = zk.$extends(zul.Widget, {
	_label: '',

	updateDomContent_: function () {
		this.rerender();
	},
	domImage_: function () {
		var img = this._image;
		return img ? '<img src="' + img + '" align="absmiddle" />': '';
	},
	domLabel_: function () {
		return zUtl.encodeXML(this.getLabel());
	},
	/** A combination of image ([[#domImage_]]) and label ([[#domLabel_]]). */
	domContent_: function () {
		var label = this.domLabel_(),
			img = this.domImage_();
		return img ? label ? img + ' ' + label: img: label;
	},
	doMouseOver_: function () {
		var himg = this._hoverImage;
		if (himg) {
			var n = this.getImageNode_();
			if (n) n.src = himg;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (this._hoverImage) {
			var n = this.getImageNode_();
			if (n) n.src = this._image;
		}
		this.$supers('doMouseOut_', arguments);
	},
	getImageNode_: function () {
		if (!this._eimg && this._image) {
			var n = this.getNode();
			if (n) this._eimg = zDom.firstChild(n, "IMG", true);
		}
		return this._eimg;
	},
	unbind_: function () {
		this._eimg = null;
		this.$supers('unbind_', arguments);
	}
}), { //zk.def
	label: function () {
		this.updateDomContent_();
	},
	image: function (v) {
		var n = this.getImageNode_();
		if (n) n.src = v || '';
	},
	hoverImage: null
});
