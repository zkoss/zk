/* Absolutelayout.js

	Purpose:
		
	Description:
		
	History:
		Mon Oct  3 11:14:17 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * <p>An Absolutelayout component can contain absolute positioned multiple
 * absolutechildren components.
 * 
 * <p>Default {@link #getZclass}: z-absolutelayout.
 * 
 * @author ashish
 * @since 6.0.0
 */
zul.layout.Absolutelayout = zk.$extends(zul.Widget, {
	getZclass: function () {
		return this._zclass != null ? this._zclass: "z-absolutelayout";
	}
}, {
	redraw: function (out) {
		out.push('<div ', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div>');
	}
});
