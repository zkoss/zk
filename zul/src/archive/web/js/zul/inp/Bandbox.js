/* Bandbox.js

	Purpose:
		
	Description:
		
	History:
		Tue Mar 31 14:17:28     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A band box. A bank box consists of an input box ({@link Textbox} and
 * a popup window {@link Bandpopup}.
 * It is similar to {@link Combobox} except the popup window could have
 * any kind of children. For example, you could place a textbox in
 * the popup to let user search particular items.
 *
 * <p>Default {@link #getZclass}: z-bandbox.
 */
zul.inp.Bandbox = zk.$extends(zul.inp.ComboWidget, {
	//super
	getPopupSize_: function (pp) {
		var bp = this.firstChild, //bandpopup
			w, h;
		if (bp) {
			w = bp.getWidth();
			h = bp.getHeight();
		}
		return [w||'auto', h||'auto'];
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-bandbox";
	},

	redrawpp_: function (out) {
		out.push('<div id="', this.uuid, '-pp" class="', this.getZclass(),
		'-pp" style="display:none" tabindex="-1">');

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
	
		out.push('</div>');
	}
});
