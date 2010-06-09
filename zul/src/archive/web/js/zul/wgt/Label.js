/* Label.js

	Purpose:
		
	Description:
		
	History:
		Sun Oct  5 00:22:03     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A label.
 * 
 * <p>Default {@link #getZclass}: z-label.
 */
zul.wgt.Label = zk.$extends(zul.Widget, {
	_value: '',
	_maxlength: 0,

	$define: {
		/** Returns the value.
		 * <p>Default: "".
		 * <p>Deriving class can override it to return whatever it wants
		 * other than null.
		 * @return String
		 */
		/** Sets the value.
		 * @param String value
		 */
		value: _zkf = function () {
			var n = this.$n();
			if (n) n.innerHTML = this.getEncodedText();
		},
		/** Returns whether to preserve the new line and the white spaces at the
		 * begining of each line.
		 * @return boolean
		 */
		/** Sets whether to preserve the new line and the white spaces at the
		 * begining of each line.
		 * @param boolean multiline
		 */
		multiline: _zkf,
		/** Returns whether to preserve the white spaces, such as space,
		 * tab and new line.
		 *
		 * <p>It is the same as style="white-space:pre". However, IE has a bug when
		 * handling such style if the content is updated dynamically.
		 * Refer to Bug 1455584.
		 *
		 * <p>Note: the new line is preserved either {@link #isPre} or
		 * {@link #isMultiline} returns true.
		 * In other words, <code>pre</code> implies <code>multiline</code>
		 * @return boolean
		 */
		/** Sets whether to preserve the white spaces, such as space,
		 * tab and new line.
		 * @param boolean pre
		 */
		pre: _zkf,
		/** Returns the maximal length of the label.
		 * <p>Default: 0 (means no limitation)
		 * @return int
		 */
		/** Sets the maximal length of the label.
		 * @param int maxlength
		 */
		maxlength: _zkf
	},
	/**
	 * Returns the encoded text.
	 * @see zUtl#encodeXML
	 * @return String
	 */
	getEncodedText: function () {
		return zUtl.encodeXML(this._value, {multiline:this._multiline,pre:this._pre, maxlength: this._maxlength});
	},

	//super//
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-label";
	}
});
