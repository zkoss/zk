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

	getVersion: function () {
		return this._version;
	},
	setVersion: function (version) {
		if (this._version != version) {
			this._version = version;
			if (this.desktop) this.rerender();
		}
	},

	getSrc: function () {
		return this._src;
	},
	setSrc: function (src) {
		if (this._src != src) {
			this._src = src;
			var n = this._embedNode();
			if (n) n.movie = src || '';
		}
	},

	getWmode: function () {
		return this._wmode;
	},
	setWmode: function (wmode) {
		if (this._wmode != wmode) {
			this._wmode = wmode;
			var n = this._embedNode();
			if (n) n.wmode = wmode || '';
		}
	},
	getBgcolor: function () {
		return this._bgcolor;
	},
	setBgcolor: function (bgcolor) {
		if (this._bgcolor != bgcolor) {
			this._bgcolor = bgcolor;
			var n = this._embedNode();
			if (n) n.bgcolor = bgcolor || '';
		}
	},
	getQuality: function () {
		return this._quality;
	},
	setQuality: function (quality) {
		if (this._quality != quality) {
			this._quality = quality;
			var n = this._embedNode();
			if (n) n.quality = quality || '';
		}
	},
	getAutoplay: function () {
		return this._autoplay;
	},
	setAutoplay: function (autoplay) {
		if (this._autoplay != autoplay) {
			this._autoplay = autoplay;
			var n = this._embedNode();
			if (n) n.autoplay = autoplay || '';
		}
	},
	getLoop: function () {
		return this._loop;
	},
	setLoop: function (loop) {
		if (this._loop != loop) {
			this._loop = loop;
			var n = this._embedNode();
			if (n) n.loop = loop || '';
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