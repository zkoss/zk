/* Iframe.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 19 11:47:53     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.rpt.Iframe = zk.$extends(zul.Widget, {
	_scrolling: "auto",

	getSrc: function () {
		return this._src;
	},
	setSrc: function (src) {
		if (this._src != src) {
			this._src = src;
			var n = this.getNode();
			if (n) n.src = src || '';
		}
	},
	getScrolling: function () {
		return this._scrolling;
	},
	setScrolling: function (scrolling) {
		if (!scrolling) scrolling = "auto";
		if (this._scrolling != scrolling) {
			this._scrolling = scrolling;
			var n = this.getNode();
			if (n) n.scrolling = scrolling;
		}
	},
	getAlign: function () {
		return this._align;
	},
	setAlign: function (align) {
		if (this._align != align) {
			this._align = align;
			var n = this.getNode();
			if (n) n.align = align || '';
		}
	},
	getName: function () {
		return this._name;
	},
	setName: function (name) {
		if (this._name != name) {
			this._name = name;
			var n = this.getNode();
			if (n) n.name = name || '';
		}
	},

	getAutohide: function () {
		return this._autohide;
	},
	setAutohide: function (autohide) {
		if (this._autohide != autohide) {
			this._autohide = autohide;
			var n = this.getNode();
			if (n) zDom.setAttr(n, 'z_autohide', autohide);
		}
	},

	domAttrs_: function(no){
		var attr = this.$supers('domAttrs_', arguments)
				+ ' src="' + (this._src || '') + '"',
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
