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
	/** Sets the content of JavaScript code snippet to execute.
	 * <p>Note: the codes won't be evaluated until it is attached the
	 * DOM tree. If it is attached, the JavaScript code sippet is executed
	 * immediately. In additions, it executes only once.
	 * <p>When the JavaScript code snipped is executed, you can access
	 * this widget by use of <code>this</code>. In other words,
	 * it was executed in the context of a member method of this widget.
	 * @param cnt the content. It could be a String instance, or
	 * a Function instance.
	 */
	setContent: function (cnt) {
		this._content = cnt;
		if (cnt) {
			this._fn = typeof cnt == 'function' ? cnt: new Function(cnt);
			if (this.desktop) //check parent since no this.getNode()
				this._exec();
		} else
			this._fn = null;
	},
	/** Returns the src of the JavaScript file.
	 */
	getSrc: function () {
		return this._src;
	},
	/** Sets the src (URI) of the JavaScript file.
	 */
	setSrc: function (src) {
		this._src = src;
		if (src) {
			this._srcrun = false;
			if (this.desktop)
				this._exec();
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

	_exec: function () {
		var pkgs = this.packages; //not visible to client (since meaningless)
		if (!pkgs) return this._exec0();

		this.packages = null; //only once
		for (var j = 0;;) {
			var k = pkgs.indexOf(',', j),
				pkg = (k >= 0 ? pkgs.substring(j, k): pkgs.substring(j)).trim();
			if (pkg) zPkg.load(pkg);
			if (k < 0) break;
			j = k + 1;
		}
		zk.afterLoad(this.proxy(this._exec0));
	},
	_exec0: function () {
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
	bind_: function () {
		this.$supers('bind_', arguments);
		this._exec();
	}
});
