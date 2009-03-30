/* LabelImageWidget.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 14:59:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
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
			this.updateDomContent_();
		}
	},
	getImage: function () {
		return this._image;
	},
	setImage: function(image) {
		if (this._image != image) {
			this._image = image;
			var n = this.getImageNode_();
			if (n) n.src = image || '';
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
		var himg = this._himg;
		if (himg) {
			var n = this.getImageNode_();
			if (n) n.src = himg;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (this._himg) {
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
});
