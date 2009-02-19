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
			this._content = null;
			if (this.desktop) this._updLink();
		}
	},
	getContent: function () {
		return this._content;
	},
	setContent: function (content) {
		if (!zk.bootstrapping)
			throw "Content cannot be changed after bootstrapping";
		this._content = content;
	},

	//super//
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this._gened) this._gened = false; //<style> gened
		else this._updLink();
	},
	unbind_: function () {
		zDom.remove(this._getLink());
		this.$supers('unbind_', arguments);
	},
	_updLink: function () {
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
	redraw: function (out) {
		//IE: unable to look back LINK or STYLE with ID
		if (zk.bootstrapping && this._content) {
			out.push('<span style="display:none" id="',
				this.uuid, '"><style type="text/css">\n',
				this._content, '\n</style></span>\n');
			this._gened = true;
		}
	}
});