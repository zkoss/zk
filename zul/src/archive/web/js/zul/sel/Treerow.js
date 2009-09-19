/* Treerow.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:43     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Treerow = zk.$extends(zul.Widget, {
	setContext: zk.$void, // readonly
	setPopup: zk.$void, // readonly
	setTooltip: zk.$void, // readonly
	setTooltiptext: zk.$void, // readonly
	
	getTree: function () {
		return this.parent ? this.parent.getTree() : null;
	},
	getLevel: function () {
		return this.parent ? this.parent.getLevel(): 0;
	},
	getLinkedTreechildren: function () {
		return this.parent ? this.parent.treechildren : null;
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = this.parent ? this.parent.isDisabled() ? this.getZclass() + '-disd'
					: this.parent.isSelected() ? this.getZclass() + '-seld' : '' : '';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	getZclass: function () {
		return this._zclass == null ? "z-treerow" : this._zclass;
	},
	getContext: function () {
		return this.parent ? this.parent.getContext() : null;
	},
	getPopup: function () {
		return this.parent ? this.parent.getPopup() : null;
	},
	getTooltip: function () {
		return this.parent ? this.parent.getTooltip() : null;
	},
	getTooltiptext: function () {
		return this.parent ? this.parent.getTooltiptext() : null;
	},
	isVisible: function () {
		if (!this.parent || !this.$supers('isVisible', arguments))
			return false;
		if (!this.parent.isVisible())
			return false;
		var child = this.parent.parent;
		return child && child.isVisible();
	}
});