/* Treecell.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:39     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A treecell.
 *
 * <p>In XUL, treecell cannot have any child, but ZUL allows it.
 * Thus, you could place any kind of children in it. They will be placed
 * right after the image and label.
 *
 * <p>Default {@link #getZclass}: z-treecell
 */
zul.sel.Treecell = zk.$extends(zul.LabelImageWidget, {
	/**
	 * The width can't be specified in this component.
	 */
	setWidth: zk.$void, // readonly
	_colspan: 1,
	$define: {
    	/** Returns number of columns to span this cell.
    	 * Default: 1.
    	 * @return int
    	 */
    	/** Sets the number of columns to span this cell.
    	 * <p>It is the same as the colspan attribute of HTML TD tag.
    	 * @param int colspan
    	 */
		colspan: [
			function (colspan) {
				return colspan > 1 ? colspan: 1;
			},
			function () {
				var n = this.$n();
				if (n) n.colSpan = this._colspan;
			}]
	},
	/** Return the tree that owns this cell.
	 * @return Tree
	 */
	getTree: function () {
		return this.parent ? this.parent.getTree() : null;
	},
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments),
			tc = this.getTreecol();
			return this.isVisible() && tc && !tc.isVisible() ? style +
				"display:none;" : style;
	},
	/** Returns the tree col associated with this cell, or null if not available.
	 * @return Treecol
	 */
	getTreecol: function () {
		var tree = this.getTree();
		if (tree && tree.treecols) {
			var j = this.getChildIndex();
			if (j < tree.treecols.nChildren)
				return tree.treecols.getChildAt(j);
		}
		return null;
	},
	/** Returns the level this cell is. The root is level 0.
	 * @return int
	 */
	getLevel: function () {
		return this.parent ? this.parent.getLevel(): 0;
	},
	/** Returns the maximal length of each item's label.
	 * @return int
	 */
	getMaxlength: function () {
		var box = this.getTree();
		if (!box) return 0;
		var tc = this.getTreecol();
		return tc ? tc.getMaxlength() : 0;
	},
	domLabel_: function () {
		return zUtl.encodeXML(this.getLabel(), {maxlength: this.getMaxlength()});
	},
	getTextNode: function () {
		return this.getCaveNode();
	},
	domContent_: function () {
		var s1 = this.$supers('domContent_', arguments),
			s2 = this._colHtmlPre();
		return s1 ? s2 ? s2 + '&nbsp;' + s1: s1: s2;
	},
	_syncIcon: function () {
		if (this.desktop) {
			
			var arr = [],
				child;
			while(child = this.firstChild) {
				arr.push(child);
				child.detach();
			}
			
			var cn = this.getCaveNode();
			jq(cn, zk).html(this.domContent_());
			
			// reset the reference
			this.parent.clearCache();
			
			for (var i = 0, j = arr.length; i < j; ++i) {
				this.appendChild(arr[i]);
			}
		}
	},
	_colHtmlPre: function () {
		if (this.parent.firstChild == this) {
			var item = this.parent.parent,
				tree = item.getTree(),
				sb = [];
			if (tree) {
				if (tree.isCheckmark()) {
					var chkable = item.isCheckable(),
						multi = tree.isMultiple(),
						zcls = item.getZclass(),
						img = zcls + '-img';
					sb.push('<span id="', this.parent.uuid, '-cm" class="', img,
						' ', img, (multi ? '-checkbox' : '-radio'));
					
					if (!chkable || item.isDisabled())
						sb.push(' ', img, '-disd');
					
					sb.push('"');
					if (!chkable)
						sb.push(' style="visibility:hidden"');
						
					sb.push('></span>');
				}
			}
			var iconScls = tree ? tree.getZclass() : "",
				pitems = this._getTreeitems(item, tree);
			for (var j = 0, k = pitems.length; j < k; ++j)
				this._appendIcon(sb, iconScls,
					j == 0 || this._isLastVisibleChild(pitems[j]) ? zul.sel.Treecell.SPACER: zul.sel.Treecell.VBAR, false);

			if (item.isContainer()) {
				this._appendIcon(sb, iconScls,
					item.isOpen() ?
						pitems.length == 0 ? zul.sel.Treecell.ROOT_OPEN:
							 this._isLastVisibleChild(item) ? zul.sel.Treecell.LAST_OPEN: zul.sel.Treecell.TEE_OPEN:
						pitems.length == 0 ? zul.sel.Treecell.ROOT_CLOSE:
							this._isLastVisibleChild(item) ? zul.sel.Treecell.LAST_CLOSE: zul.sel.Treecell.TEE_CLOSE,
						true);
			} else {
				this._appendIcon(sb, iconScls,
					pitems.length == 0 ? zul.sel.Treecell.FIRSTSPACER:
						this._isLastVisibleChild(item) ? zul.sel.Treecell.LAST: zul.sel.Treecell.TEE, false);
			}
			return sb.join('');
		} else {
			//To make the tree's height more correct, we have to generate &nbsp;
			//for empty cell. Otherwise, IE will make the height too small
			return !this.getImage() && !this.getLabel()	&& !this.nChildren ? "&nbsp;": null;
		}
	},
	_isLastVisibleChild: function (item) {
		var parent = item.parent;
		for (var w = parent.lastChild; w; w = w.previousSibling)
			if (w.isVisible()) return w == item;
		return false;
	},
	_getTreeitems: function (item, tree) {
		var pitems = [];
		for (;;) {
			var tch = item.parent;
			if (!tch)
				break;
			item = tch.parent;
			if (!item || item == tree)
				break;
			pitems.unshift(item);
		}
		return pitems;
	},
	getZclass: function () {
		return this._zclass == null ? "z-treecell" : this._zclass;
	},
	_appendIcon: function (sb, iconScls, name, button) {
		sb.push('<span class="');
		if (name == zul.sel.Treecell.TEE || name == zul.sel.Treecell.LAST || name == zul.sel.Treecell.VBAR || name == zul.sel.Treecell.SPACER) {
			sb.push(iconScls + "-line ", iconScls, '-', name, '"');
		} else {
			sb.push(iconScls + "-ico ", iconScls, '-', name, '"');
		}
		if (button) {
			var item = this.parent.parent;
			if (item && item.treerow)
				sb.push(' id="', item.treerow.uuid, '-open"');
		}

		sb.push('></span>');
	},
	getWidth: function() {
		var col = this.getTreecol();
		return col ? col.getWidth() : null;
	},
	domAttrs_: function () {
		var head = this.getTreecol(),
			added;
		if (head)
			added = head.getColAttrs();
		return this.$supers('domAttrs_', arguments)
			+ (this._colspan > 1 ? ' colspan="' + this._colspan + '"' : '')
			+ (added ? ' ' + added : '');
	}
}, {
	ROOT_OPEN: "root-open",
	ROOT_CLOSE: "root-close",
	LAST_OPEN: "last-open",
	LAST_CLOSE: "last-close",
	TEE_OPEN: "tee-open",
	TEE_CLOSE: "tee-close",
	TEE: "tee",
	LAST: "last",
	VBAR: "vbar",
	SPACER: "spacer",
	FIRSTSPACER: "firstspacer"
});
