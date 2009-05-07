/* Listbox.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:16:07     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.def(zul.sel.Listbox = zk.$extends(zul.sel.SelectWidget, {
	_rows: 0,

	$init: function () {
		this.$supers('$init', arguments);
	},

	nextItem: function (p) {
		if (p)
			while ((p = p.nextSibling)
			&& !p.$instanceof(zul.sel.Listitem))
				;
		return p;
	},
	previousItem: function (p) {
		if (p)
			while ((p = p.previousSibling)
			&& !p.$instanceof(zul.sel.Listitem))
				;
		return p;
	},

	//-- super --//
	getZclass: function () {
		return this._zclass == null ? "z-listbox" : this._zclass;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.sel.Listitem)) {
			if (!this.firstItem || !this.previousItem(child))
				this.firstItem = child;
			if (!this.lastItem || !this.nextItem(child))
				this.lastItem = child;
		} else if (child.$instanceof(zul.sel.Listhead))
			this.listhead = child;
		else if (child.$instanceof(zul.mesh.Paging))
			this.paging = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.listhead)
			this.listhead = null;
		else if (child == this.paging)
			this.paging = null;
		else {
			if (child == this.firstItem) {
				for (var p = this.firstChild, Listitem = zul.sel.ListItem;
				p && !p.$instanceof(Listitem); p = p.nextSibling)
					;
				this.firstItem = p;
			}
			if (child == this.lastItem) {
				for (var p = this.lastChild, Listitem = zul.sel.ListItem;
				p && !p.$instanceof(Listitem); p = p.previousSibling)
					;
				this.lastItem = p;
			}
		}
	},
	getHeadWidgetClass: function () {
		return zul.sel.Listhead;
	},
	itemIterator: _zkf = function () {
		return new zul.sel.ItemIter(this);
	},
	getBodyWidgetIterator: _zkf
}), { //zk.def
	rows: function (rows) {
		if (this.desktop) ;//TODO: recalc size
	}
});

zul.sel.ItemIter = zk.$extends(zk.Object, {
	$init: function (box) {
		this.p = box.firstItem;
	},
	hasNext: function () {
		return this.p;
	},
	next: function () {
		var p = this.p;
		this.p = box.nextItem(p);
		return p;
	}
});
