/* Html.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 23 20:35:12     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A comonent used to embed the browser native content (i.e., HTML tags)
 * into the output sent to the browser.
 * The browser native content is specified by {@link #setContent}.
 *
 * <p>Notice that {@link Html} generates HTML SPAN to enclose
 * the embedded HTML tags. Thus, you can specify the style
 * ({@link #getStyle}), tooltip {@link #getTooltip} and so on.
 */
zul.wgt.Html = zk.$extends(zul.Widget, {
	_content: '',
	$define: {
		/** Returns the embedded content (i.e., HTML tags).
		 * <p>Default: empty ("").
		 * <p>Deriving class can override it to return whatever it wants
		 * other than null.
		 * @return String
		 */
		/** Sets the embedded content (i.e., HTML tags).
		 * @param String content
		 */
		content: function (v) {
			var n = this.$n();
			if (n) n.innerHTML = v|| '';
		}
	},
	bind_: function () {
		this.$supers(zul.wgt.Html, "bind_", arguments);
		if (jq.isArray(this._content)) //z$ea
			for (var ctn = this._content, n = this.$n(), j = 0; j < ctn.length; ++j)
				n.appendChild(ctn[j]);
	},
	unbind_: function () {
		if (jq.isArray(this._content)) //z$ea
			for (var n = this.$n(); n.firstChild;)
				n.removeChild(n.firstChild);
		this.$supers(zul.wgt.Html, "unbind_", arguments);
	}
});
