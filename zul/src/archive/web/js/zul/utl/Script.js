/* Script.js

	Purpose:
		
	Description:
		
	History:
		Thu Dec 11 15:39:59     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.utl.Script = zk.$extends(zk.Widget, {
	$define: {
		content: function (cnt) {
			if (cnt) {
				this._fn = typeof cnt == 'function' ? cnt: new Function(cnt);
				if (this.desktop) //check parent since no this.$n()
					this._exec();
			} else
				this._fn = null;
		},
		src: function (src) {
			if (src) {
				this._srcrun = false;
				if (this.desktop)
					this._exec();
			}
		},
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
			jq.appendScript(this._src, this._charset);
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
