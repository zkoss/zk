/* Style.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan 14 15:28:14     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.utl.Style = zk.$extends(zk.Widget, {
	getSrc: function () {
		return this._src;
	},
	setSrc: function (src) {
		if (this._src != src) {
			this._src = src;
			if (this.desktop) this._updLink();
		}
	},

	//super//
	bind_: function () {
		this.$supers('bind_', arguments);
		this._updLink();
	},
	unbind_: function () {
		zDom.remove(this._getLink());
		this.$supers('unbind_', arguments);
	},
	_updLink: function () {
		if (this._src) {
			zDom.remove(this.uuid + '$css');

			var head = this._getHead(),
				ln = this._getLink(head),
				n = this.getNode();
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
		return head = document.getElementsByTagName("HEAD")[0];
	},
	_getLink: function (head) {
		head = head || this._getHead();
		for (var lns = head.getElementsByTagName("LINK"), j = lns.length,
		uuid = this.uuid; --j >= 0;)
			if (lns[j].id == uuid)
				return lns[j];
	},
	redraw: function () { //nothing to do
	}
});