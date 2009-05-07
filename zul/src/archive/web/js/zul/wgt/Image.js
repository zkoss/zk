/* Image.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 15:07:07     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
_zkc = zul.wgt.Image = zk.$extends(zul.Widget, {
	getImageNode_: function () {
		return this.getNode();
	},

	//super
	doMouseOver_: function () {
		var hover = this._hover;
		if (hover) {
			var img = this.getImageNode_();
			if (img) img.src = hover;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (this._hover) {
			var img = this.getImageNode_();
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
		var attr = ' src="' + (this._src || '') + '"', v;
		if (v = this._align) 
			attr += ' align="' + v + '"';
		if (v = this._border) 
			attr += ' border="' + v + '"';
		if (v = this._hspace) 
			attr += ' hspace="' + v + '"';
		if (v = this._vspace) 
			attr += ' vspace="' + v + '"';
		return attr;
	}
});

zk.def(_zkc, {
	src: function (v) {
		var n = this.getImageNode_();
		if (n) n.src = v || '';
	},
	hover: null,
	align: function (v) {
		var n = this.getImageNode_();
		if (n) n.align = v || '';
	},
	border: function (v) {
		var n = this.getImageNode_();
		if (n) n.border = v || '';
	},
	hspace: function (v) {
		var n = this.getImageNode_();
		if (n) n.hspace = v;
	},
	vspace: function (v) {
		var n = this.getImageNode_();
		if (n) n.vspace = v;
	}
});
