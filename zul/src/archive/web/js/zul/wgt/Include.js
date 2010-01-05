/* Include.js

	Purpose:
		
	Description:
		
	History:
		Tue Oct 14 15:23:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An include widget
 */
zul.wgt.Include = zk.$extends(zul.Widget, {
	_content: '',

	$define: {
		/**
		 * Returns the html content.
		 * @return String
		 */
		/**
		 * Sets the html content.
		 * @param String content
		 */
		content: function (v) {
			var n = this.$n();
			if (n) {
				if (v && this._comment)
					v = '<!--\n' + v + '\n-->';
				n.innerHTML = v  || '';
			}
		},
		/** Returns whether to generate the included content inside
		 * the HTML comment.
		 * <p>Default: false.
		 *
		 * <p>Notice that it is ignored in the instance mode ({@link #getMode}).
		 * @return boolean
		 */
		/** Sets  whether to generate the included content inside
		 * the HTML comment.
		 * @param boolean comment
		 */
		comment: null
	},

	//super//
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments);
		if (!this.previousSibling && !this.nextSibling) {
		//if it is only child, the default is 100%
			if ((!no || !no.width) && !this.getWidth())
				style += 'width:100%;';
			if ((!no || !no.height) && !this.getHeight())
				style += 'height:100%;';
		}
		return style;
	},
	redraw: function (out) {
		out.push('<div', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		if (this._comment)
			out.push('<!--\n');
		out.push(this._content);
		if (this._comment)
			out.push('\n-->');
		out.push('</div>');
	}
});
