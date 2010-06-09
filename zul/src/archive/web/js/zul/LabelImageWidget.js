/* LabelImageWidget.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 14:59:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for ZUL widgets that support both label and image.
 */
zul.LabelImageWidget = zk.$extends(zul.Widget, {
	_label: '',

	$define: {
		/** Sets the label.
		 * <p>If label is changed, the whole component is invalidate.
		 * Thus, you want to smart-update, you have to override {@link #updateDomContent_}.
		 * @param String label
		 */
		/** Returns the label (never null).
		 * <p>Default: "".
		 * @return String
		 */
		label: function () {
			this.updateDomContent_();
		},
		/** Sets the image URI.
		 * @param String image the URI of the image
		 */
		/** Returns the image URI.
		 * <p>Default: null.
		 * @return String
		 */
		image: function (v) {
			var n = this.getImageNode();
			if (n) n.src = v || '';
			else (this.desktop) //<IMG> might not be generated (Bug 3007738)
				this.updateDomContent_();
		},
		/** Sets the image URI.
		 * The hover image is used when the mouse is moving over this component.
		 * @param String src
		 */
		/** Returns the URI of the hover image.
		 * The hover image is used when the mouse is moving over this component.
		 * <p>Default: null.
		 * @return String
		 */
		hoverImage: null
	},
	/**
	 * Updates the DOM tree for the modified label and image. It is called by
	 * {@link #setLabel} and {@link #setImage} to update the new content of the
	 * label and/or image to the DOM tree.
	 * Default: invoke {@link zk.Widget#rerender} to redraw and re-bind. 
	 */
	updateDomContent_: function () {
		this.rerender();
	},
	/**
	 * Returns the HTML image content.
	 * @return String
	 */
	domImage_: function () {
		var img = this._image;
		return img ? '<img src="' + img + '" align="absmiddle" />': '';
	},
	/**
	 * Returns the encoded label.
	 * @return String
	 * @see zUtl#encodeXML
	 */
	domLabel_: function () {
		return zUtl.encodeXML(this.getLabel());
	},
	/**
	 * Returns the HTML content of the label and image.
	 * It is a fragment of HTML that you can use in the mold.
	 * @return String 
	 * @see #domImage_
	 * @see #domLabel_
	 */
	domContent_: function () {
		var label = this.domLabel_(),
			img = this.domImage_();
		return img ? label ? img + ' ' + label: img: label;
	},
	doMouseOver_: function () {
		var himg = this._hoverImage;
		if (himg) {
			var n = this.getImageNode();
			if (n) n.src = himg;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (this._hoverImage) {
			var n = this.getImageNode();
			if (n) n.src = this._image;
		}
		this.$supers('doMouseOut_', arguments);
	},
	/**
	 * Returns the image node if any.
	 * @return DOMElement
	 */
	getImageNode: function () {
		if (!this._eimg && this._image) {
			var n = this.$n();
			if (n) this._eimg = jq(n).find('img:first')[0];
		}
		return this._eimg;
	},
	//@Override
	clearCache: function () {
		this._eimg = null;
		this.$supers('clearCache', arguments);
	}
});
