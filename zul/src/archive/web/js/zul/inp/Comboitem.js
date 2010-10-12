/* Comboitem.js

	Purpose:
		
	Description:
		
	History:
		Sun Mar 29 20:53:45     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
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
				var zcls = this.getZclass() + '-disd';
				v ? jq(n).addClass(zcls): jq(n).removeClass(zcls);
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
	doMouseOver_: function () {
		if (!this._disabled) {
			var n = this.$n(),
				$n = jq(n),
				zcls = this.getZclass();
			$n.addClass($n.hasClass(zcls + '-seld') ?
				zcls + "-over-seld": zcls + "-over");
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (!this._disabled) this._doMouseOut();
		this.$supers('doMouseOut_', arguments);
	},
	_doMouseOut: function () {
		var n = this.$n(),
			zcls = this.getZclass();
		jq(n).removeClass(zcls + '-over')
			.removeClass(zcls + '-over-seld');
	},

	doClick_: function (evt) {
		if (!this._disabled) {
			this._doMouseOut();

			var cb = this.parent;
			cb._select(this, {sendOnSelect:true, sendOnChange: true});
			cb.close({sendOnOpen:true});
			
			// Fixed the onFocus event is triggered too late in IE.
			cb._shallClose = true;
			jq(cb.getInputNode()).focus();
			evt.stop();
		}
	},

	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (this._disabled && (!no || !no.zclass)) {
			var zcls = this.getZclass();
			scls += ' ' + zcls + '-disd';
		}
		return scls;
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-comboitem";
	}
});
