/* Script.js

	Purpose:
		
	Description:
		
	History:
		Thu Dec 11 15:39:59     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A component to generate script codes that will be evaluated at the client.
 * It is similar to HTML SCRIPT tag.
 */
zul.utl.Script = zk.$extends(zk.Widget, {
	$define: {
    	/** Returns the content of the script element.
    	 * By content we mean the JavaScript codes that will be enclosed
    	 * by the HTML SCRIPT element.
    	 *
    	 * <p>Default: null.
    	 * @return String
    	 */
    	/** Sets the content of the script element.
    	 * By content we mean the JavaScript codes that will be enclosed
    	 * by the HTML SCRIPT element.
    	 * @param String content
    	 */
		content: function (cnt) {
			if (cnt) {
				this._fn = typeof cnt == 'function' ? cnt: new Function(cnt);
				if (this.desktop) //check parent since no this.$n()
					this._exec();
			} else
				this._fn = null;
		},
		/** Returns the URI of the source that contains the script codes.
		 * <p>Default: null.
		 * @return String
		 */
		/** Sets the URI of the source that contains the script codes.
		 *
		 * <p>You either add the script codes directly with the {@link Label}
		 * children, or
		 * set the URI to load the script codes with {@link #setSrc}.
		 * But, not both.
		 *
		 * @param String src the URI of the source that contains the script codes
		 */
		src: function (src) {
			if (src) {
				this._srcrun = false;
				if (this.desktop)
					this._exec();
			}
		},
		/** Returns the character enconding of the source.
		 * It is used with {@link #getSrc}.
		 *
		 * <p>Default: null.
		 * @return String
		 */
		/** Sets the character encoding of the source.
		 * It is used with {@link #setSrc}.
		 * @param String charset
		 */
		charset: null
	},

	_exec: function () {
		var pkgs = this.packages; //not visible to client (since meaningless)
		if (!pkgs) return this._exec0();

		this.packages = null; //only once
		zk.load(pkgs);

		if (zk.loading)
			zk.afterLoad(this.proxy(this._exec0));
		else
			this._exec0();
	},
	_exec0: function () {
		var wgt = this, fn = this._fn;
		if (fn) {
			this._fn = null; //run only once
			zk.afterMount(function () {fn.apply(wgt);});
		}
		if (this._src && !this._srcrun) {
			this._srcrun = true; //run only once
			zk.loadScript(this._src, null, this._charset);
		}
	},

	//super//
	redraw: function () {
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		this._exec();
	}
});
