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
			if (this.node) this.updateDomContent_();
		}
	},
	getImage: function () {
		return this._image;
	},
	setImage: function(image) {
		if (this._image != image) {
			this._image = image;
			if (this.node) this.updateDomContent_();
		}
	},

	updateDomContent_: function () {
		this.rerender();
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.getImage();
		if (!img) return label;

		img = '<img src="' + img + '" align="absmiddle" />';
		return label ? img + ' ' + label: img;
	}
});
