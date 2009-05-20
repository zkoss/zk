/* Flash.js

	Purpose:
		
	Description:
		
	History:
		Sat Mar 28 22:02:52     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.med.Flash = zk.$extends(zul.Widget, {
	_wmode: 'transparent',
	_quality: 'high',
	_autoplay: true,
	_loop: false,
	_version: '6,0,0,0',

	$define: {
		version: function () {
			this.rerender();
		},
		src: function (v) {
			var n = this._embedNode();
			if (n) n.movie = v || '';
		},
		wmode: function (wmode) {
			var n = this._embedNode();
			if (n) n.wmode = v || '';
		},
		bgcolor: function (v) {
			var n = this._embedNode();
			if (n) n.bgcolor = v || '';
		},
		quality: function (v) {
			var n = this._embedNode();
			if (n) n.quality = v || '';
		},
		autoplay: function (autoplay) {
			var n = this._embedNode();
			if (n) n.autoplay = v || '';
		},
		loop: function (v) {
			var n = this._embedNode();
			if (n) n.loop = v || '';
		}
	},

	//super//
	setHeight: function (height) {
		this._height = height;
		var n = this._embedNode();
		if (n) n.height = height ? height: '';
	},
	setWidth: function (width) {
		this._width = width;
		var n = this._embedNode();
		if (n) n.width = width ? width: '';
	},

	_embedNode: function () {
		return this.getSubnode('emb');
	}
});
