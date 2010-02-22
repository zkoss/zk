/* Treerow.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:43     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A treerow.
 * <p>Default {@link #getZclass}: z-treerow
 */
zul.sel.Treerow = zk.$extends(zul.Widget, {
	/** Returns the {@link Tree} instance containing this element.
	 * @return Tree
	 */
	getTree: function () {
		return this.parent ? this.parent.getTree() : null;
	},
	/** Returns the level this cell is. The root is level 0.
	 * @return int
	 */
	getLevel: function () {
		return this.parent ? this.parent.getLevel(): 0;
	},
	/** Returns the {@link Treechildren} associated with this
	 * {@link Treerow}.
	 * @return Treechildren
	 */
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
	/** Returns the same as {@link Treeitem#getContext}.
	 * @return String
	 */
	getContext: function () {
		return this.parent ? this.parent.getContext() : null;
	},
	/** Returns the same as {@link Treeitem#getPopup}.
	 * @return String
	 */
	getPopup: function () {
		return this.parent ? this.parent.getPopup() : null;
	},
	/** Returns the same as {@link Treeitem#getTooltip}.
	 * @return String
	 */
	getTooltip: function () {
		return this.parent ? this.parent.getTooltip() : null;
	},
	/** Returns the same as {@link Treeitem#getTooltiptext}.
	 * @return String
	 */
	getTooltiptext: function () {
		return this.parent ? this.parent.getTooltiptext() : null;
	},
	/** Returns whether this is visible.
	 * whether all its ancestors is open.
	 * @return boolean
	 */
	isVisible: function () {
		if (!this.parent || !this.$supers('isVisible', arguments))
			return false;
		if (!this.parent.isVisible())
			return false;
		var child = this.parent.parent;
		return child && child.isVisible();
	},
	//@Override
	removeChild: function (child) {
		for (var w = child.firstChild; w;) {
			var n = w.nextSibling; //remember, since remove will null the link
			child.removeChild(w); //deep first
			w = n;
		}
		this.$supers('removeChild', arguments);
	},
	//@Override
	ignoreClick_: function (evt) {
		var n = evt.domTarget;
		return n && n.id == this.uuid + '-open';
	}
});