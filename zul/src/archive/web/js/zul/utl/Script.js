/* Script.js

	Purpose:
		
	Description:
		
	History:
		Thu Dec 11 15:39:59     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.utl.Script = zk.$extends(zk.Widget, {
	/** Returns the content of JavaScript codes to execute.
	 */
	getContent: function () {
		return this._content;
	},
	/** Sets the content of JavaScript codes to execute.
	 * <p>Note: the codes won't be evaluated until it is attached the
	 * DOM tree. In additions, it executes only once.
	 */
	setContent: function (cnt) {
		this._content = cnt;
		if (cnt) {
			this._fn = typeof cnt == 'function' ? cnt: new Function(cnt);
			if (this.parent && this.parent.node) //check parent since no this.node
				this._run();
		} else
			this._fn = null;
	},
	/** Returns the src of the JavaScript file.
	 */
	getSrc: function () {
		return _src;
	},
	/** Sets the src (URI) of the JavaScript file.
	 */
	setSrc: function (src) {
		this._src = src;
		if (src) {
			this._srcrun = false;
			if (this.parent && this.parent.node)
				this._run();
		}
	},

	/** Returns the charset. */
	getCharset: function () {
		return this._charset;
	},
	/** Sets the charset. */
	setCharset: function (charset) {
		this._charset = charset;
	},

	_run: function () {
		var wgt = this, fn = this._fn;
		if (fn) {
			this._fn = null; //run only once
			zk.afterMount(function () {fn.apply(wgt);});
		}
		if (this._src && !this._srcrun) {
			this._srcrun = true; //run only once
			zDom.appendScript(this._src, this._charset);
		}
	},

	//super//
	redraw: function () {
		return '';
	},
	bind_: function (desktop) {
		this._run();
	}
});
