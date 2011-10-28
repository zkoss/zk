/* Include.js

	Purpose:
		
	Description:
		
	History:
		Tue Oct 14 15:23:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An include widget
 */
zul.wgt.Include = zk.$extends(zul.Widget, {
	$init: function () {
		this._fellows = {};
		this.$supers('$init', arguments);
	},

	$define: {
		/** Returns whether to generate the included content inside
		 * the HTML comment.
		 * <p>Default: false.
		 *
		 * @return boolean
		 */
		/** Sets  whether to generate the included content inside
		 * the HTML comment.
		 * @param boolean comment
		 */
		comment: null
	},

	//super//
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments);
		if (!this.previousSibling && !this.nextSibling) {
		//if it is only child, the default is 100%
			if ((!no || !no.width) && !this.getWidth())
				style += 'width:100%;';
			if ((!no || !no.height) && !this.getHeight())
				style += 'height:100%;';
		}
		return style;
	},
	bind_: function () {
		this.$supers(zul.wgt.Include, "bind_", arguments);
		var ctn;
		if (ctn = this._childjs) {
			ctn();
			this._childjs = this._xcnt = null;
				//only once since the content has been created as child widgets
		}

		if (jq.isArray(ctn = this._xcnt)) //array -> zk().detachChildren() used
			for (var n = this.$n(), j = 0; j < ctn.length; ++j)
				n.appendChild(ctn[j]);
	},
	unbind_: function () {
		if (jq.isArray(this._xcnt)) //array -> zk().detachChildren() used
			for (var n = this.$n(); n.firstChild;)
				n.removeChild(n.firstChild);
		this.$supers(zul.wgt.Include, "unbind_", arguments);
	}
});
