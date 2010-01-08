/* Style.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan 14 15:28:14     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The style component used to specify CSS styles for the owner desktop.
 * <p>
 * Note: a style component can appear anywhere in a ZUML page, but it affects
 * all components in the same desktop.
 * <p>
 * Note: if the src and content properties are both set, the later one overrides
 * the previous one.
 */
zul.utl.Style = zk.$extends(zk.Widget, {
	$define: {
    	/**
		 * Returns the URI of an external style sheet.
		 * <p>
		 * Default: null.
		 * 
		 * @return String
		 */
		/**
		 * Sets the URI of an external style sheet.
		 * 
		 * @param String src the URI of an external style sheet
		 */
		src: function () {
			if (this.desktop) this._updLink();
		}
	},

	//super//
	bind_: function () {
		this.$supers('bind_', arguments);
		this._updLink();
	},
	unbind_: function () {
		jq(this._getLink()).remove();
		this.$supers('unbind_', arguments);
	},
	_updLink: function () {
		if (this._src) {
			jq(this.uuid + '-css', zk).remove();

			var head = this._getHead(),
				ln = this._getLink(head),
				n = this.$n();
			if (n) n.innerHTML = '';
			if (ln) ln.href = this._src;
			else {
				ln = document.createElement("LINK");
				ln.id = this.uuid;
				ln.rel = "stylesheet";
				ln.type = "text/css";
				ln.href = this._src;
				head.appendChild(ln);
			}
		}
	},
	_getHead: function () {
		return document.getElementsByTagName("HEAD")[0];
	},
	_getLink: function (head) {
		head = head || this._getHead();
		for (var lns = head.getElementsByTagName("LINK"), j = lns.length,
		uuid = this.uuid; j--;)
			if (lns[j].id == uuid)
				return lns[j];
	},
	redraw: function () { //nothing to do
	}
});