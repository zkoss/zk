/* Text.js

	Purpose:
		
	Description:
		
	History:
		Sun Jan  4 15:35:22     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zhtml.Text = zk.$extends(zhtml.Widget, {
	_value: '',
	_encode: true,

	$define: {
		/** Returns whether to encode the text, such as converting &lt;
		 * to &amp;lt;.
		 * <p>Default: true.
		 * @return boolean
		 * @since 5.0.8
		 */
		/** Sets whether to encode the text, such as converting &lt;
		 * to &amp;lt;.
		 * <p>Default: true.
		 * @param boolean encode whether to encode
		 * @since 5.0.8
		 */
		encode: _zkf = function () {
			var n = this.$n();
			if (n) {
				var val = this._value;
				n.innerHTML = this._encode ? zUtl.encodeXML(val) : val;
				//See Bug 2871080 and ZK-294
			}
		},
		/** Returns the value of this label.
		 * @return String
		 */
		/** Sets the value of this label.
		 * @param String label the label
		 */
		value: _zkf
	},
	// ZK 7.5 enable to preserve the line break and comment into XHTML format
	// so we need to skip them here.
	_checkContentRequired: function (val) {
		if (val) {
			val = val.trim();
			if (val && !(val.startsWith('<\!--') && val.endsWith('-->')))
				return true;
		}
		return false;
	},
	redraw: function (out) {
		var attrs = this.domAttrs_({id: 1, zclass: 1}),
			val = this._value,
			span = attrs || (this.idRequired && this._checkContentRequired(val));
			// Bug 3245960: enclosed text was wrapped with <span>
		if (span) out.push('<span', ' id="', this.uuid, '"', attrs, '>');
		out.push(this._encode ? zUtl.encodeXML(val) : val);
			//See Bug 2871080 and ZK-294
		if (span) out.push('</span>');
	}
});
