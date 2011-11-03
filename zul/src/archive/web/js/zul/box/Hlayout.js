/* Hlayout.js

	Purpose:
		
	Description:
		
	History:
		Fri Aug  6 11:54:19 TST 2010, Created by jumperchen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A horizontal layout.
 * <p>Default {@link #getZclass}: z-hlayout.
 * @since 5.0.4
 */
zul.box.Hlayout = zk.$extends(zul.box.Layout, {
	$define: { //zk.def
		/** Sets the vertical-align to top or bottom.
		 *
		 * @param String valign the value of vertical-align property
		 * "middle", "bottom".
		 * @since 6.0.0
		 */
		/** Returns the current valign.
		 * @return String
		 */
		valign: [
					function (v) {
						var oldV = this._valign,
							n;
						if (oldV == v)
							return;
						if (n = this.$n()) {
							var $n = jq(n);
							if (oldV == "middle")
								$n.removeClass("z-valign-middle");
							else if (oldV == "bottom")
								$n.removeClass("z-valign-bottom");
						}
						return v;
					},
					function () {
						this._fixAlign();
					}
		],
	},
	isVertical_: function () {
		return false;
	},
	getZclass: function () {
		return this._zclass == null ? "z-hlayout" : this._zclass;
	},
	_fixAlign: function () {
		var v = this._valign,
			n = this.$n();
		if (n) {
			if (v == "middle")
				jq(n).addClass("z-valign-middle");
			else if (v == "bottom")
				jq(n).addClass("z-valign-bottom");
		}
	}
});