/* Frozen.js

	Purpose:

	Description:

	History:
		Wed Sep  2 10:07:04     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _colspan(c) { //colspan specified in widget
		var v = zk.Widget.$(c)._colspan;
		return v ? v: 1;
	}
	function _resetColspan(c) {
		c.colSpan = _colspan(c);
	}
	function _fixaux(cells, from, to) {
		for (var j = 0, k = 0, cl = cells.length; j < cl; ++j) {
			var ke = k + _colspan( zk.Widget.$(cells[j]));
			if (from >= k && ke > from) { //found
				for (; j < cl && k < to; ++j, k = ke) {
					var cell = cells[j],
						ke = k + _colspan(cell),
						v = from - k, v2 = ke - to;
					v = (v > 0 ? v: 0) + (v2 > 0 ? v2: 0);
					if (v) {
						cell.colSpan = v;
						cell.style.display = "";
					} else {
						_resetColspan(cell);
						cell.style.display = "none";
					}
				}
				for (; j < cl; ++j) {
					var cell = cells[j];
					_resetColspan(cell);
					if (cell.style.display != "none")
						break; //done
					cell.style.display = "";
				}
				return;
			}
			k = ke;
		}
	}

/**
 * A frozen component to represent a frozen column or row in grid, like MS Excel. 
 * <p>Default {@link #getZclass}: z-frozen.
 */
zul.mesh.Frozen = zk.$extends(zul.Widget, {
	_start: 0,
	
	$define: {
    	/**
    	 * Returns the number of columns to freeze.
    	 * <p>Default: 0
    	 * @return int
    	 */
    	/**
    	 * Sets the number of columns to freeze.(from left to right)
    	 * @param int columns positive only
    	 */
		columns: [function(v) {
			return v < 0 ? 0 : v;
		}, function(v) {
			if (this._columns) {
				if (this.desktop) {
					this.onShow();
					this.syncScorll();
				}
			} else this.rerender();
		}],
		/**
		 * Returns the start position of the scrollbar.
		 * <p>Default: 0
		 * @return int
		 */
		/**
		 * Sets the start position of the scrollbar.
		 * <p> Default: 0
		 * @param int start the column number
		 */
		start: function () {
			this.syncScorll();
		}
	},
	/**
	 * Synchronizes the scrollbar according to {@link #getStart}.
	 */
	syncScorll: function () {
		var scroll = this.$n('scrollX');
		if (scroll)
			scroll.scrollLeft = this._start * 50;
	},
	getZclass: function () {
		return this._zclass == null ? "z-frozen" : this._zclass;
	},
	onShow: _zkf = function () {
		if (!this._columns) return;
		var parent = this.parent,
			bdfaker = parent.ebdfaker;
		if (!bdfaker) {
			bdfaker = parent.ebodyrows[0];
			if (bdfaker)
				bdfaker = bdfaker.$n();
		}
		if (bdfaker) {
			var leftWidth = 0;
			for (var i = this._columns, n = bdfaker.firstChild; n && i--; n = n.nextSibling)
				leftWidth += n.offsetWidth;

			this.$n('cave').style.width = jq.px0(leftWidth);
			var scroll = this.$n('scrollX'),
				width = parent.$n('body').offsetWidth;
				width -= leftWidth;
			scroll.style.width = jq.px0(width);
			
			var scrollScale = bdfaker.childNodes.length - this._columns -
					(parent.isSizedByContent() ? 1 : 2 /** fixed a bug related to the feature #3025419*/);
			
			scroll.firstChild.style.width = jq.px0(width + 50 * scrollScale);
			this.syncScorll();
		}
	},
	onSize: _zkf,
	_onScroll: function (evt) {
		if (!evt.data) return;
		this.setStart(this._start + 2);
		this.parent.ebody.scrollLeft = 0;
		evt.stop();
	},
	bind_: function () {
		this.$supers(zul.mesh.Frozen, 'bind_', arguments);
		zWatch.listen({onShow: this, onSize: this});
		var scroll = this.$n('scrollX'),
			gbody = this.parent.$n('body');

		this.$n().style.height = this.$n('cave').style.height = scroll.style.height
			 = scroll.firstChild.style.height = jq.px0(jq.scrollbarWidth());

		this.parent.listen({onScroll: this.proxy(this._onScroll)}, -1000);
		this.domListen_(scroll, 'onScroll');

		if (gbody)
			gbody.style.overflowX = 'hidden';
	},
	unbind_: function () {
		zWatch.unlisten({onShow: this, onSize: this});
		var p;
		if ((p = this.parent) && (p = p.$n('body')))
			p.style.overflowX = '';
		this.$supers(zul.mesh.Frozen, 'unbind_', arguments);
	},
	_doScroll: function (evt) {
		var scroll = this.$n('scrollX'),
			num = Math.ceil(scroll.scrollLeft / 50);
		if (this._lastScale == num)
			return;
		this._lastScale = num;
		this._doScrollNow(num);
		this.smartUpdate('start', num);
		this._start = num;
	},
	_doScrollNow: function (num) {
		var width = this.$n('cave').offsetWidth,
			mesh = this.parent,
			cnt = num,
			rows = mesh.ebodyrows;

		if (!mesh.head && (!rows || rows.length))
			return;

		if (mesh.head) {

			// set fixed size
			for (var faker, n = mesh.head.firstChild.$n('hdfaker'); n;
					n = n.nextSibling) {
				if (n.style.width.indexOf('px') == -1) {
					var sw = n.style.width = jq.px0(n.offsetWidth),
						wgt = zk.Widget.$(n);
					if ((faker = wgt.$n('bdfaker')))
						faker.style.width = sw;
					if ((faker = wgt.$n('ftfaker')))
						faker.style.width = sw;
				}
			}
			var colhead = mesh.head.getChildAt(this._columns).$n();
			for (var display, faker, index = this._columns,
					tail = mesh.head.nChildren - index,
					n = colhead;
					n; n = n.nextSibling, index++, tail--) {
				display = cnt-- <= 0 ? '' : 'none';
				if (n.style.display != display) {
					n.style.display = display;
					if ((faker = jq('#' + n.id + '-hdfaker')[0]))
						faker.style.display = display;
					if ((faker = jq('#' + n.id + '-bdfaker')[0]))
						faker.style.display = display;
					if ((faker = jq('#' + n.id + '-fdfaker')[0]))
						faker.style.display = display;

					//body
					for (var i = 0, rl = rows.length, cells;
					i < rl && (ofs = (cells = rows[i++].cells).length - tail) >= 0;)
						cells[ofs].style.display = display;
				}
			}

			//auxhead
			for (var hdr = colhead.parentNode, hdrs = hdr.parentNode.rows,
				i = hdrs.length, r; i--;)
				if ((r = hdrs[i]) != hdr) //skip Column
					_fixaux(r.cells, this._columns, this._columns + num);

			for (var n = mesh.head.getChildAt(this._columns + num).$n('hdfaker');
					n; n = n.nextSibling)
				width += zk.parseInt(n.style.width);

		} else {
			
			// set fixed size
			for (var index = this._columns, c = rows[0].firstChild; c; c = c.nextSibling) {
				if (c.style.width.indexOf('px') == -1)
					c.style.width = jq.px0(zk(c).revisedWidth(c.offsetWidth));
			}

			for (var first = rows[0], display, index = this._columns,
					len = first.childNodes.length; index < len; index++) {
				display = cnt-- <= 0 ? '' : 'none';
				for (var r = first; r; r = r.nextSibling)
					r.cells[index].style.display = display;
			}

			for (var c = rows[0].cells[this._columns + num]; c; c = c.nextSibling)
				width += zk.parseInt(c.style.width);
		}

		width = jq.px0(width);
		if (mesh.eheadtbl)
			mesh.eheadtbl.style.width = width;
		if (mesh.ebodytbl)
			mesh.ebodytbl.style.width = width;
		if (mesh.efoottbl)
			mesh.efoottbl.style.width = width;
	}
});

})();