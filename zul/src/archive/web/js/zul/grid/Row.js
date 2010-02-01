/* Row.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:26:27     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A single row in a {@link Rows} element.
 * Each child of the {@link Row} element is placed in each successive cell
 * of the grid. The row with the most child elements determines the number
 * of columns in each row.
 *
 * <p>Default {@link #getZclass}: z-row.
 *
 */
zul.grid.Row = zk.$extends(zul.Widget, {
	$define: {
		/** Returns the horizontal alignment of the whole row.
		 * <p>Default: null (system default: left unless CSS specified).
		 * @return String
		 */
		/** Sets the horizontal alignment of the whole row.
		 * @param String align
		 */
		align: function (v) {
			var n = this.$n();
			if (n)
				n.align = v;
		},
		/** Returns the nowrap.
		 * <p>Default: null (system default: wrap).
		 * @return boolean
		 */
		/** Sets the nowrap.
		 * @param boolean nowrap
		 */
		nowrap: function (v) {
			var n = this.$n();
			if (n)
				n.noWrap = v;
		},
		/** Returns the vertical alignment of the whole row.
		 * <p>Default: null (system default: top).
		 * @return String
		 */
		/** Sets the vertical alignment of the whole row.
		 * @param String valign
		 */
		valign: function (v) {
			var n = this.$n();
			if (n)
				n.vAlign = v;
		}
	},
	/** Returns the grid that contains this row. 
	 * @return zul.grid.Grid
	 */
	getGrid: function () {
		return this.parent ? this.parent.parent : null;
	},	
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			this.$supers('setVisible', arguments);
			if (this.isStripeable_() && this.parent)
				this.parent.stripe();
		}
	},
	/** Returns the spans, which is a list of numbers separated by comma.
	 * <p>Default: empty.
	 * @return String
	 */
	getSpans: function () {
		return zUtl.intsToString(this._spans);
	},
	/** Sets the spans, which is a list of numbers separated by comma.
	 *
	 * <p>For example, "1,2,3" means the second column will span two columns
	 * and the following column span three columns, while others occupies
	 * one column.
	 * @param String spans
	 */
	setSpans: function (spans) {
		if (this.getSpans() != spans) {
			this._spans = zUtl.stringToInts(spans, 1);
			this.rerender();
		}
	},
	_getIndex: function () {
		return this.parent ? this.getChildIndex() : -1;
	},
	getZclass: function () {
		return this._zclass != null ? this._zclass : "z-row";
	},
	/** Returns the group that this row belongs to, or null.
	 * @return zul.grid.Group
	 */
	getGroup: function () {
		// TODO: this performance is not good.
		if (this.parent && this.parent.hasGroup())
			for (var w = this.previousSibling; w; w = w.previousSibling)
				if (w.$instanceof(zul.grid.Group)) return w;
				
		return null;
	},	
	setStyle: function (style) {
		if (this._style != style) {
			if (!zk._rowTime) zk._rowTime = zUtl.now();
			this._style = style;
			this.rerender();
		}
	},
	getSclass: function () {
		var sclass = this.$supers('getSclass', arguments);
		if (sclass != null) return sclass;

		var grid = this.getGrid();
		return grid ? grid.getSclass(): sclass;
	},
	_getChdextr: function (child) {
		return child.$n('chdextr') || child.$n();
	},
	insertChildHTML_: function (child, before, desktop) {
		var cls = !this.getGrid().isSizedByContent() ? 'z-overflow-hidden' : '';
		if (before)
			jq(this._getChdextr(before)).before(
				this.encloseChildHTML_({child: child, index: child.getChildIndex(),
						zclass: this.getZclass(), cls: cls}));
		else
			jq(this).append(
				this.encloseChildHTML_({child: child, index: child.getChildIndex(),
						zclass: this.getZclass(), cls: cls}));
		
		child.bind(desktop);
	},
	removeChildHTML_: function (child, prevsib) {
		this.$supers('removeChildHTML_', arguments);
		jq(child.uuid + '-chdextr', zk).remove();
	},
	/** Enclose child with HTML tag with TD and DIV, 
	 * and return a HTML code or add HTML fragments in out array.
	 * @param Map opts
	 * @return String
	 */
	encloseChildHTML_: function (opts) {
		var out = opts.out || [],
			child = opts.child,
			isCell = child.$instanceof(zul.wgt.Cell);
		if (!isCell) {
			out.push('<td id="', child.uuid, '-chdextr"', this._childAttrs(child, opts.index),
				'>', '<div id="', child.uuid, '-cell" class="', opts.zclass, '-cnt ',
				opts.cls, '">');
		}
		child.redraw(out);
		if (!isCell) out.push('</div></td>');
		if (!opts.out) return out.join('');
	},
	_childAttrs: function (child, index) {
		var realIndex = index, span = 1;
		if (this._spans) {
			for (var j = 0, k = this._spans.length; j < k; ++j) {
				if (j == index) {
					span = this._spans[j];
					break;
				}
				realIndex += this._spans[j] - 1;
			}
		}

		var colattrs, visible, hgh,
			grid = this.getGrid();
		
		if (grid) {
			var cols = grid.columns;
			if (cols) {
				if (realIndex < cols.nChildren) {
					var col = cols.getChildAt(realIndex);
					colattrs = col.getColAttrs();
					visible = col.isVisible() ? '' : 'display:none';
					hgh = col.getHeight();
				}
			}
		}

		var style = this.domStyle_({visible:1, width:1, height:1}),
			isDetail = zk.isLoaded('zkex.grid') && child.$instanceof(zkex.grid.Detail);
		if (isDetail) {
			var wd = child.getWidth();
			if (wd) 
				style += "width:" + wd + ";";
		}

		if (visible || hgh) {
			style += visible;
			if (hgh)
				style += 'height:' + hgh + ';';
		}
		
		var clx = isDetail ? child.getZclass() + "-outer" : this.getZclass() + "-inner";
		
		if (!colattrs && !style && span === 1)
			return ' class="' + clx + '"';

		var attrs = colattrs ? colattrs : '';
		
		if (span !== 1)
			attrs += ' colspan="' + span + '"';
		return attrs + ' style="' + style + '"' + ' class="' + clx + '"';
	},
	/**
	 * Returns whether is stripeable or not.
	 * <p>Default: true.
	 * @return boolean
	 */
	isStripeable_: function () {
		return true;
	},
	//-- super --//
	domStyle_: function (no) {
		if ((this.$instanceof(zul.grid.Group) || this.$instanceof(zul.grid.Groupfoot))
				|| (no && no.visible))
			return this.$supers('domStyle_', arguments);
			
		var style = this.$supers('domStyle_', arguments),
			group = this.getGroup();
		return group && !group.isOpen() ? style + "display:none;" : style;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.grid.Detail))
			this.detail = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.detail)
			this.detail = null;
	},
	doMouseOver_: function(evt) {
		if (zk.gecko && this._draggable) {
			var tag = evt.domTarget.tagName;
			if (tag != "INPUT" && tag != "TEXTAREA") {
				var n = this.$n();
				if (n) n.firstChild.style.MozUserSelect = "none";
			}
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function(evt) {
		if (zk.gecko && this._draggable) {
			var n = this.$n();
			if (n) n.firstChild.style.MozUserSelect = "none";
		}
		this.$supers('doMouseOut_', arguments);
	},
	domAttrs_: function (no) {
		var attr = this.$supers('domAttrs_', arguments);
		if (this._align)
			attr += ' align="' + this._align + '"';
		if (this._valign)
			attr += ' valign="' + this._valign + '"';
		if (this._nowrap)
			attr += ' nowrap="nowrap"';
		return attr;
	}
});