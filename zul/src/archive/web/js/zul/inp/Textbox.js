/* Textbox.js

	Purpose:

	Description:

	History:
		Sat Dec 13 23:30:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A textbox.
 * <p>Default {@link #getZclass}: z-textbox.
 */
zul.inp.Textbox = zk.$extends(zul.inp.InputWidget, {
	_value: '',
	_rows: 1,

	$define: {
		/** Returns whether it is multiline.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is multiline.
		 * @param boolean multiline
		 */
		multiline: function () {
			this.rerender();
		},
		/** Returns whether TAB is allowed.
		 * If true, the user can enter TAB in the textbox, rather than change
		 * focus.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether TAB is allowed.
		 * If true, the user can enter TAB in the textbox, rather than change
		 * focus.
		 * <p>Default: false.
		 * @param boolean tabbable
		 */
		tabbable: null,
		/** Returns the rows.
		 * <p>Default: 1.
		 * @return int
		 */
		/** Sets the rows.
		 * @param int rows
		 */
		rows: function (v) {
			var inp = this.getInputNode();
			if (inp && this.isMultiline())
				inp.rows = v;
		},
		/** Returns the type.
		 * <p>Default: text.
		 * @return String
		 */
		/** Sets the type.
		 * @param String type the type. Acceptable values are "text" and "password".
		 * Unlike XUL, "timed" is redudant because it is enabled as long as
		 * onChanging is added.
		 */
		type: function (type) {
			var inp = this.getInputNode();
			if (inp)
				inp.type = type;
		},
		/** Returns whether it is submitByEnter.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is submitByEnter.
		 * @param boolean submitByEnter
		 */
		submitByEnter: null
	},
	//super//
	textAttrs_: function () {
		var html = this.$supers('textAttrs_', arguments);
		if (this._multiline)
			html += ' rows="' + this._rows + '"';
		return html;
	},
	doKeyDown_: function (evt, simulated) {
		if (evt.keyCode == 13 && this._submitByEnter && this._multiline && !evt.shiftKey) {
			evt.stop();
			this.fire('onOK');
		}
		this.$supers('doKeyDown_', arguments);
	}
});
