/* Iframe.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 19 11:47:53     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.utl.Iframe = zk.$extends(zul.Widget, {
	_scrolling: "auto",

	$define: {
		src: function (v) {
			var n = this.$n();
			if (n) n.src = v || '';
		},
		scrolling: function (v) {
			if (!v) this._scrolling = v = "auto";
			var n = this.$n();
			if (n) {
				if (zk.ie || zk.safari)
					this.rerender();
				else
					n.scrolling = v;
			}
		},
		align: function (v) {
			var n = this.$n();
			if (n) n.align = v || '';
		},
		name: function (v) {
			if (n) n.name = v || '';
		},
		autohide: function (v) {
			var n = this.$n();
			if (n) jq(n).attr('z_autohide', v);
		}
	},
	//super//
	domAttrs_: function(no){
		var attr = this.$supers('domAttrs_', arguments)
				+ ' src="' + (this._src || '') + '" frameborder="0"',
			v = this._scrolling;
		if ("auto" != v)
			attr += ' scrolling="' + ('true' == v ? 'yes': 'false' == v ? 'no': v) + '"';
		if (v = this._align) 
			attr += ' align="' + v + '"';
		if (v = this._name) 
			attr += ' name="' + v + '"';
		if (v = this._autohide) 
			attr += ' z_autohide="' + v + '"';
		return attr;
	}
});
