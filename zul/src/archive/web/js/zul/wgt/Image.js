/* Image.js

	Purpose:

	Description:

	History:
		Thu Mar 26 15:07:07     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An image.
 *
 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Customization/Alphafix_for_IE6">how to fix the alpha transparency problem of PNG files found in IE6?</a>
 */
zul.wgt.Image = zk.$extends(zul.Widget, {
	$define: {
		/** Returns the source URI of the image.
		 * <p>Default: null.
		 * @return String
		 */
		/** Sets the source URI of the image.
		 * @param String src the URI of the image source
		 */
		src: function (v) {
			if (v && this._preloadImage) zUtl.loadImage(v);
			var n = this.getImageNode();
			if (n) n.src = v || '';
		},
		/** Returns the URI of the hover image.
		 * The hover image is used when the mouse is moving over this component.
		 * <p>Default: null.
		 * @return String
		 */
		/** Sets the image URI.
		 * The hover image is used when the mouse is moving over this component.
		 * @param String hover
		 */
		hover: null,
		/** Returns the alignment.
		 * <p>Default: null (use browser default).
		 * @return String
		 * @deprecated as of release 6.0.0, use CSS instead.
		 */
		/** Sets the alignment: one of top, texttop, middle, absmiddle,
		 * bottom, absbottom, baseline, left, right and center.
		 * @param String align
		 * @deprecated as of release 6.0.0, use CSS instead.
		 */
		align: function (v) {
			var n = this.getImageNode();
			if (n) n.align = v || '';
		},
		/** Returns number of pixels of extra space to the left and right
		 * side of the image.
		 * <p>Default: null (use browser default).
		 * @return String
		 * @deprecated as of release 6.0.0, use CSS instead.
		 */
		/** Sets number of pixels of extra space to the left and right
		 * side of the image.
		 * @param String hspace
		 * @deprecated as of release 6.0.0, use CSS instead.
		 */
		hspace: function (v) {
			var n = this.getImageNode();
			if (n) n.hspace = v;
		},
		/** Returns number of pixels of extra space to the top and bottom
		 * side of the image.
		 * <p>Default: null (use browser default).
		 * @return String
		 * @deprecated as of release 6.0.0, use CSS instead.
		 */
		/** Sets number of pixels of extra space to the top and bottom
		 * side of the image.
		 * @param String vspace
		 * @deprecated as of release 6.0.0, use CSS instead.
		 */
		vspace: function (v) {
			var n = this.getImageNode();
			if (n) n.vspace = v;
		}
	},
	/**
	 * Returns the image node if any.
	 * @return DOMElement
	 */
	getImageNode: function () {
		return this.$n();
	},

	//super
	doMouseOver_: function () {
		var hover = this._hover;
		if (hover) {
			var img = this.getImageNode();
			if (img) img.src = hover;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (this._hover) {
			var img = this.getImageNode();
			if (img) img.src = this._src || '';
		}
		this.$supers('doMouseOut_', arguments);
	},
	domAttrs_: function (no) {
		var attr = this.$supers('domAttrs_', arguments);
		if (!no || !no.content)
			attr += this.contentAttrs_();
		return attr;
	},
	/** This method is required only if IMG is placed in the inner.
	 * And, it also has to specify {content:true} when calling [[#domAttrs_]]
	 */
	contentAttrs_: function () {
		var attr = ' src="' + zUtl.encodeXMLAttribute(this._src || '') + '"', v;
		if (v = this._align)
			attr += ' align="' + zUtl.encodeXMLAttribute(v) + '"';
		if (v = this._hspace)
			attr += ' hspace="' + zUtl.encodeXMLAttribute(v) + '"';
		if (v = this._vspace)
			attr += ' vspace="' + zUtl.encodeXMLAttribute(v) + '"';
		return attr;
	}
});
