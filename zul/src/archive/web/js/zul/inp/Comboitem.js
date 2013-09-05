/* Comboitem.js

	Purpose:
		
	Description:
		
	History:
		Sun Mar 29 20:53:45     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An item of a combo box.
 *
 * <p>Non-XUL extension. Refer to {@link Combobox}.
 * 
 * <p>Default {@link #getZclass}: z-comboitem.
 *
 * @see Combobox
 */
zul.inp.Comboitem = zk.$extends(zul.LabelImageWidget, {
	$define: {
		/** Returns whether it is disabled.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is disabled.
		 * @param boolean disabled
		 */
		disabled: function (v) {
			var n = this.$n();
			if (n) {
				var disd = this.$s('disabled');
				v ? jq(n).addClass(disd): jq(n).removeClass(disd);
			}
		},
		/** Returns the description (never null).
		 * The description is used to provide extra information such that
		 * users is easy to make a selection.
		 * <p>Default: "".
		 * <p>Deriving class can override it to return whatever it wants
		 * other than null.
		 * @return String
		 */
		/** Sets the description.
		 * @param String desc
		 */
		description: _zkf = function () {
			this.rerender();
		},
		/** Returns the embedded content (i.e., HTML tags) that is
		 * shown as part of the description.
		 *
		 * <p>It is useful to show the description in more versatile way.
		 *
		 * <p>Default: empty ("").
		 *
		 * <p>Deriving class can override it to return whatever it wants
		 * other than null.
		 * @return String
		 * @see #getDescription
		 */
		/** Sets the embedded content (i.e., HTML tags) that is
		 * shown as part of the description.
		 *
		 * <p>It is useful to show the description in more versatile way.
		 * @param String content
		 * @see #setDescription
		 */
		content: _zkf
	},

	//super
	domLabel_: function () {
		return zUtl.encodeXML(this.getLabel(), {pre: 1});
	},
	doClick_: function (evt) {
		if (!this._disabled) {

			var cb = this.parent;
			cb._select(this, {sendOnSelect:true, sendOnChange: true});
			this._updateHoverImage();
			cb.close({sendOnOpen:true, focus:true});
			
			// Fixed the onFocus event is triggered too late in IE.
			cb._shallClose = true;
			if (zul.inp.InputCtrl.isPreservedFocus(this))
				zk(cb.getInputNode()).focus();
			evt.stop();
		}
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (this._disabled && (!no || !no.zclass)) {
			scls += ' ' + this.$s('disabled');
		}
		return scls;
	},
	deferRedrawHTML_: function (out) {
		out.push('<li', this.domAttrs_({domClass:1}), ' class="z-renderdefer"></li>');
	}
});
