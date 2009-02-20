/* Row.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:26:27     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Row = zk.$extends(zul.Widget, {
	getGrid: function () {
		return this.parent ? this.parent.parent : null;
	},
	getAlign: function () {
		return this._align;
	},
	setAlign: function (align) {
		if (this._align != align) {
			this._align = align;
			var n = this.getNode();
			if (n)
				n.align = align;
		}
	},
	isNowrap: function () {
		return this._nowrap;
	},
	setNowrap: function (nowrap) {
		if (this._nowrap != nowrap) {
			this._nowrap = nowrap;
			var n = this.getNode();
			if (n)
				n.noWrap = nowrap;
		}
	},
	getValign: function () {
		return this._valign;
	},
	setValign: function (valign) {
		if (this._valign != valign) {
			this._valign = valign;
			var n = this.getNode();
			if (n)
				n.vAlign = valign;
		}
	},
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			// TODO: for rows.getGroup
			/**final Rows rows = (Rows) getParent();
			if (rows != null) {
				final Group g = rows.getGroup(getIndex());
				if (g == null || g.isOpen())
					rows.addVisibleItemCount(visible ? 1 : -1);
			}*/
			
			this.$supers('setVisible', arguments);
			if (this.isStripeable_() && this.parent)
				this.parent.stripe();
		}
	},
	getSpans: function () {
		return zUtl.intsToString(this._spans);
	},
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
	getGroup: function () {
		// TODO: for group
		/**
		if (this instanceof Group) return (Group)this;
		final Rows rows = (Rows) getParent();
		return (rows != null) ? rows.getGroup(getIndex()) : null;
		*/
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
	insertChildHTML_: function (child, before, desktop) {
		var cls = this.getGrid().isFixedLayout() ? 'z-overflow-hidden' : '';
		if (before) {
			zDom.insertHTMLBefore(before.getSubnode('chdextr'),
				this.encloseChildHTML_({child: child, index: child.getChildIndex(),
						zclass: this.getZclass(), cls: cls}));
		} else
			zDom.insertHTMLBeforeEnd(this.getNode(),
				this.encloseChildHTML_({child: child, index: child.getChildIndex(),
						zclass: this.getZclass(), cls: cls}));
		
		child.bind_(desktop);
	},
	removeChildHTML_: function (child, prevsib) {
		this.$supers('removeChildHTML_', arguments);
		zDom.remove(child.uuid + '$chdextr');
	},
	encloseChildHTML_: function (opts) {
		var out = opts.out || [],
			child = opts.child;
		out.push('<td id="', child.uuid, '$chdextr"', this._childAttrs(child, opts.index),
				'>', '<div id="', child.uuid, '$cell" class="', opts.zclass, '-cnt ',
				opts.cls, '">');
		child.redraw(out);
		out.push('</div></td>');
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
			isDetail = child.$instanceof(zul.grid.Detail);
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
	isStripeable_: function () {
		return true;
	},
	//-- super --//
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.grid.Detail))
			this.detail = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.detail)
			this.detail = null;
	}
});
/** // TODO for drag and drop
 * if (zk.gecko) {
	zul.grid.Row.prototype.doMouseOver_ = function (wevt, evt) {
		var target = this._getDirectChildByElement(zEvt.target(evt), this.getNode());
		if (target)
			target.firstChild.style.MozUserSelect = "none";
		this.$supers('doMouseOver_', arguments);
	};
	zul.grid.Row.prototype.doMouseOut_ = function (wevt, evt) {
		var target = this._getDirectChildByElement(zEvt.target(evt), this.getNode());
		if (target)
			target.firstChild.style.MozUserSelect = "";
		this.$supers('doMouseOut_', arguments);
	};
	zul.grid.Row.prototype._getDirectChildByElement = function (el, parent) {
		for (;el; el = el.parentNode)
			if (el.parentNode == parent) return el;
		return null;
	};
}*/