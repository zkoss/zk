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
	/** Returns the label of this button.
	 */
	getLabel: function () {
		var v = this._label;
		return v ? v: '';
	},
	/** Sets the label of this button.
	 */
	setLabel: function(label) {
		if (label == null) label = '';
		if (this._label != label) {
			this._label = label;
			var n = this.node;
			if (n) this.updateDomContent_();
		}
	},
	/** Returns the image of this button.
	 */
	getImage: function () {
		return this._image;
	},
	/** Sets the image of this button.
	 */
	setImage: function(image) {
		if (this._image != image) {
			this._image = image;
			var n = this.node;
			if (n) this.updateDomContent_();
		}
	},

	/** Updates DOM content for the label and image.
	 * <p>Default: this.rerender().
	 */
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
