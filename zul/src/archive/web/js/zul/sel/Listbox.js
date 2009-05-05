/* Listbox.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:16:07     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listbox = zk.$extends(zul.sel.SelectWidget, {
	_rows: 0,

	$init: function () {
		this.$supers('$init', arguments);
		this.items = [];
	},

	getRows: function () {
		return this._rows;
	},
	setRows: function (rows) {
		if (this._rows != rows) {
			this._rows = rows;
			if (this.desktop) ;//TODO: recalc size
		}
	},

	//-- super --//
	getZclass: function () {
		return this._zclass == null ? "z-listbox" : this._zclass;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.sel.Listhead))
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
	},
	getHeadWidgetClass: function () {
		return zul.sel.Listhead;
	},
	getBodyWidgetIterator: function () {
		return new zul.sel.BodyWidgetIterator(this);
	}
});

zul.sel.BodyWidgetIterator = zk.$extends(zk.Object, {
	$init: function (box) {
		this.items = box.items;
		this.j = 0;
	},
	hasNext: function () {
		return this.j < this.items.length;
	},
	next: function () {
		return this.j < this.items.length ? this.items[this.j++]: null;
	}
});
