/* Treecell.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:39     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
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
		var style = this.$super('domStyle_', zk.copy(no, {width:true})),
				//bug#3185657: not span content if given width
			tc = this.getTreecol();
			// B70-ZK-2946: adds the text-align from treecol, same as Listcell
			if (tc) if (tc._align) style += 'text-align: ' + tc._align + ';';
			return this.isVisible() && tc && !tc.isVisible() ? style +
				'display:none;' : style;
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
		return s1 ? s2 ? s2 + '<span class="' + this.$s('text') + '">&nbsp;' + s1 + '</span>' : s1: s2;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this._clearCache) { // B60-ZK-1348
			this._clearCache = false;
			var p;
			if (p = this.parent) {
				p.clearCache(); //$n('open')
			}
		}
	},
	doMouseOver_: function(evt) {
		var n = this.$n();
		
		// ZK-2136: all children should apply -moz-user-select: none
		if (n && zk.gecko && (this._draggable || this.parent._draggable)
				&& !jq.nodeName(evt.domTarget, 'input', 'textarea')) {
			jq(n).addClass('z-draggable-over');
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function(evt) {
		var n = this.$n();
		
		// ZK-2136: all children should apply -moz-user-select: none
		if (n && zk.gecko && (this._draggable || this.parent._draggable)
				&& !jq.nodeName(evt.domTarget, 'input', 'textarea')) {
			jq(n).removeClass('z-draggable-over'); // Bug ZK-580
		}
		this.$supers('doMouseOut_', arguments);
	},
	doFocus_: function (evt) {
		this.$supers('doFocus_', arguments);
		//sync frozen
		var tree = this.getTree(),
		frozen = tree ? tree.frozen : null,
		tbody = tree && tree._treechildren ? tree._treechildren.$n() : null,
		td, tds;
		if (frozen && tbody) {
			tds = jq(evt.domTarget).parents('td');
			for (var i = 0, j = tds.length; i < j; i++) {
				td = tds[i];
				if (td.parentNode.parentNode == tbody) {
					tree._moveToHidingFocusCell(td.cellIndex);
					break;
				}
			}
		}
	},
	_syncIcon: function (isRemoved) {
		this.rerender(isRemoved ? -1 : false);
		var p;
		if (p = this.parent) {
			this._clearCache = true;
		}
	},
	_colHtmlPre: function () {
		if (this.parent.firstChild == this) {
			var item = this.parent.parent,
				tree = item.getTree(),
				sb = new zk.Buffer();
			if (tree) {
				if (tree.isCheckmark()) {
					var chkable = item.isSelectable(),
						multi = tree.isMultiple(),
						cmCls = multi ? item.$s('checkbox') : item.$s('radio'),
						ckCls = multi ? ' z-icon-check' : ' z-icon-radio';
					sb.push('<span id="', this.parent.uuid, '-cm" class="',
							item.$s('checkable'), ' ', cmCls);
					
					if (!chkable || item.isDisabled())
						sb.push(' ', item.$s('disabled'));
					
					sb.push('"');
					if (!chkable)
						sb.push(' style="visibility:hidden"');
					
					sb.push('><i class="', item.$s('icon'), ckCls, '"></i></span>');
				}
			}
			var iconScls = tree ? tree.getZclass() : '',
				pitems = this._getTreeitems(item, tree);
			for (var j = 0, k = pitems.length; j < k; ++j)
				this._appendIcon(sb, iconScls, 'spacer', false);
			
			if (item.isContainer()) {
				var name = item.isOpen() ? 'open' : 'close';
				this._appendIcon(sb, iconScls, name, true);
			} else {
				this._appendIcon(sb, iconScls, 'spacer', false);
			}
			return sb.join('');
		} else {
			//To make the tree's height more correct, we have to generate &nbsp;
			//for empty cell. Otherwise, IE will make the height too small
			return !this.getImage() && !this.getLabel()	&& !this.nChildren ? '&nbsp;' : null;
		}
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
	_appendIcon: function (sb, iconScls, name, button) {
		var openCloseIcon = '';
		sb.push('<span class="');
		if (name == 'spacer') {
			sb.push(iconScls, '-line ', iconScls, '-', name, '"');
			openCloseIcon += '&nbsp;';
		} else {
			var id = '';
			if (button) {
				var item = this.parent;
				if (item)
					id = item.uuid + '-icon';
			}
			sb.push(iconScls, '-icon"');
			var icon = this.getIconOpenClass_();
			if (name.indexOf('close') > -1)
				icon = this.getIconCloseClass_();
			
			openCloseIcon += '<i id="' + id + '" class="' + icon + ' ' + iconScls +
					'-' + name + '"></i>';
		}
		if (button) {
			var item = this.parent; // B65-ZK-1608, appendChild() will invoke before treeitem._fixOnAdd() 
			if (item)
				sb.push(' id="', item.uuid, '-open"');
		}
		sb.push('>', openCloseIcon, '</span>');
		openCloseIcon = null;
	},
	getIconOpenClass_: function () {
		return 'z-icon-caret-down';
	},
	getIconCloseClass_: function () {
		return 'z-icon-caret-right';
	},
	getWidth: function() {
		var col = this.getTreecol();
		return col ? col.getWidth() : null;
	},
	domAttrs_: function () {
		return this.$supers('domAttrs_', arguments)
			+ (this._colspan > 1 ? ' colspan="' + this._colspan + '"' : '');
	},
	updateDomContent_: function () {
		this.$supers('updateDomContent_', arguments);
		if (this.parent)
			this.parent.clearCache();
	},
	deferRedrawHTML_: function (out) {
		out.push('<td', this.domAttrs_({domClass:1}), ' class="z-renderdefer"></td>');
	}
});
})();