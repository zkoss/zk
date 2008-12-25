/* LabelImageWidget.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 14:59:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.LabelImageWidget = zk.$extends(zul.Widget, {
	getLabel: function () {
		var v = this._label;
		return v ? v: '';
	},
	setLabel: function(label) {
		if (label == null) label = '';
		if (this._label != label) {
			this._label = label;
			if (this.desktop) this.updateDomContent_();
		}
	},
	getImage: function () {
		return this._image;
	},
	setImage: function(image) {
		if (this._image != image) {
			this._image = image;
			if (this.desktop) this.updateDomContent_();
		}
	},
	getHoverImage: function () {
		return this._himg;
	},
	setHoverImage: function (himg) {
		this._himg = himg;
	},
	updateDomContent_: function () {
		this.rerender();
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this._image;
		if (!img) return label;

		img = '<img src="' + img + '" align="absmiddle" />';
		return label ? img + ' ' + label: img;
	},
	doMouseOver_: function () {
		var himg = this._himg;
		if (himg) {
			var img = this.getImageNode_();
			if (img) img.src = himg;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (this._himg) {
			var img = this.getImageNode_();
			if (img) img.src = this._image;
		}
		this.$supers('doMouseOut_', arguments);
	},
	getImageNode_: function () {
		if (!this._eimg && this._image)
			this._eimg = zDom.firstChild(this.getNode(), "IMG", true);
		return this._eimg;
	},
	unbind_: function () {
		this._eimg = null;
		this.$supers('unbind_', arguments);
	}
});
