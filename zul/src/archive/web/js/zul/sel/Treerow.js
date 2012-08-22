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
		var scls = this.$supers('domClass_', arguments),
			p = this.parent;
		if (p && (!no || !no.zclass)) {
			var zcls = this.getZclass();
			if (p.isDisabled())
				scls += (scls ? ' ': '') + zcls + '-disd';
			if (p.isSelected())
				scls += (scls ? ' ': '') + zcls + '-seld';
		}
		return scls;
	},
	domTooltiptext_ : function () {
		return this._tooltiptext || this.parent._tooltiptext || this.parent.parent._tooltiptext;
	},
	//@Override
	domStyle_: function (no) {
		// patch the case that treerow is hidden by treeitem visibility
		return ((this.parent && !this.parent._isRealVisible() && this.isVisible()) ?
				'display:none;' : '') + this.$supers('domStyle_', arguments);
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
	doClick_: function(evt) {
		var ti = this.parent;
		if (evt.domTarget == this.$n('open')) {
			ti.setOpen(!ti._open);
			evt.stop();
		} else if (!ti.isDisabled())
			this.$supers('doClick_', arguments);
	},
	deferRedrawHTML_: function (out) {
		out.push('<tr', this.domAttrs_({domClass:1}), ' class="z-renderdefer"></tr>');
	}
});