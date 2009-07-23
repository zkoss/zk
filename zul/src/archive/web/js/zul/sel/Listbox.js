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
	$init: function () {
		this.$supers('$init', arguments);
		this._groupsInfo = [];
	},
	getGroupCount: function () {
		return this._groupsInfo.length;
	},
	getGroups: function () {
		return this._groupsInfo.$clone();
	},
	hasGroup: function () {
		return this._groupsInfo.length;
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
	getOddRowSclass: function () {
		return this._scOddRow == null ? this.getZclass() + "-odd" : this._scOddRow;
	},
	setOddRowSclass: function (scls) {
		if (!scls) scls = null;
		if (this._scOddRow != scls) {
			this._scOddRow = scls;
			var n = this.$n();
			if (n && this.rows)
				this.stripe();
		}
		return this;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen({onResponse: this});
		zk.afterMount(this.proxy(this.onResponse));
	},
	unbind_: function () {
		zWatch.unlisten({onResponse: this});
		this.$supers('unbind_', arguments);
	},
	onResponse: function () {
		if (this.desktop && this._shallStripe) {
			this.stripe();
			this.onSize();
		}
	},
	_syncStripe: function () {
		this._shallStripe = true;
		if (!this.inServer && this.desktop)
			this.onResponse();
	},
	stripe: function () {
		var scOdd = this.getOddRowSclass();
		if (!scOdd) return;
		for (var j = 0, even = true, it = this.getBodyWidgetIterator(), w; (w = it.next()); j++) {
			if (w.isVisible() && w.isStripeable_()) {
				jq(w.$n())[even ? 'removeClass' : 'addClass'](scOdd);
				even = !even;
			}
		}
		this._shallStripe = false;
		return this;
	},
	rerender: function () {
		this.$supers('rerender', arguments);
		this._syncStripe();		
		return this;
	},
	//-- super --//
	getZclass: function () {
		return this._zclass == null ? "z-listbox" : this._zclass;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.sel.Listitem)) {
			if (child.$instanceof(zul.sel.Listgroup))
				this._groupsInfo.push(child);
			if (!this.firstItem || !this.previousItem(child))
				this.firstItem = child;
			if (!this.lastItem || !this.nextItem(child))
				this.lastItem = child;	
			
			if (child.isSelected() && !this._selItems.$contains(child))
				this._selItems.push(child);
		} else if (child.$instanceof(zul.sel.Listhead))
			this.listhead = child;
		else if (child.$instanceof(zul.mesh.Paging))
			this.paging = child;
		else if (child.$instanceof(zul.sel.Listfoot))
			this.listfoot = child;
		this._syncStripe();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.listhead)
			this.listhead = null;
		else if (child == this.paging)
			this.paging = null;
		else if (child == this.listfoot)
			this.listfoot = null;
		else {
			if (child == this.firstItem) {
				for (var p = this.firstChild, Listitem = zul.sel.Listitem;
				p && !p.$instanceof(Listitem); p = p.nextSibling)
					;
				this.firstItem = p;
			}
			if (child == this.lastItem) {
				for (var p = this.lastChild, Listitem = zul.sel.Listitem;
				p && !p.$instanceof(Listitem); p = p.previousSibling)
					;
				this.lastItem = p;
			}
			if (child.$instanceof(zul.sel.Listgroup))
				this._groupsInfo.$remove(child);
			
			if (child.isSelected())
				this._selItems.$remove(child);
		}
		this._syncStripe();
	},
	getHeadWidgetClass: function () {
		return zul.sel.Listhead;
	},
	itemIterator: _zkf = function () {
		return new zul.sel.ItemIter(this);
	},
	getBodyWidgetIterator: _zkf
});

zul.sel.ItemIter = zk.$extends(zk.Object, {
	$init: function (box) {
		this.box = box;
	},
	_init: function () {
		if (!this._isInit) {
			this._isInit = true;
			this.p = this.box.firstItem;
		}
	},
	hasNext: function () {
		this._init();
		return this.p;
	},
	next: function () {
		this._init();
		var p = this.p;
		if (p) this.p = p.parent.nextItem(p);
		return p;
	}
});
