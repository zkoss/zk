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
						cell.style.width = "";
					} else {
						cell.style.width = "0px";
					}
				}
				for (; j < cl; ++j) {
					var cell = cells[j];
					if (cell.style.width != "0px")
						break; //done
					cell.style.width = "";
				}
				return;
			}
			k = ke;
		}
	}
	// Bug 3218078
	function _onSizeLater(wgt) {		
		var parent = wgt.parent,
			bdfaker = parent.ebdfaker;
		if (!bdfaker) {
			bdfaker = parent.ebodyrows[0];
			if (bdfaker)
				bdfaker = bdfaker.$n();
		}
		if (bdfaker) {
			var leftWidth = 0;
			for (var i = wgt._columns, n = bdfaker.firstChild; n && i--; n = n.nextSibling)
				leftWidth += n.offsetWidth;

			wgt.$n('cave').style.width = jq.px0(leftWidth);
			var scroll = wgt.$n('scrollX'),
				width = parent.$n('body').offsetWidth;
			
			width -= leftWidth;
			scroll.style.width = jq.px0(width);
			
			var scrollScale = bdfaker.childNodes.length - wgt._columns -
					(parent.isSizedByContent() ? 1 : 2 /* fixed a bug related to the feature #3025419*/);
			
			scroll.firstChild.style.width = jq.px0(width + 50 * scrollScale);
			wgt.syncScorll();
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
		var self = this;
		// Bug 3218078, to do the sizing after the 'setAttr' command
		setTimeout(function () {
			_onSizeLater(self);
		});
	},
	onSize: _zkf,
	_onScroll: function (evt) {
		if (!evt.data || !zk.currentFocus) return;
		var p, index, td, frozen = this, 
			fn = function () {
				if (zk.currentFocus &&
					(td = p.getFocusCell(zk.currentFocus.$n())) && 
						(index = td.cellIndex - frozen._columns) >= 0) {
					frozen.setStart(index);
					p.ebody.scrollLeft = 0;
				}
			};
			
		if (p = this.parent) {
			if (zk.ie)
				setTimeout(fn, 0);
			else fn();
		}
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
			jq(gbody).addClass('z-word-nowrap').css('overflow-x', 'hidden');
	},
	unbind_: function () {
		zWatch.unlisten({onShow: this, onSize: this});
		var p;
		if ((p = this.parent) && (p = p.$n('body')))
			jq(p).removeClass('z-word-nowrap').css('overflow-x', '');
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
	_syncFrozen: function () { //called by Rows, HeadWidget...
		this._scrlcnt = (this._scrlcnt||0) + 1;
		var self = this;
		return setTimeout(function () {
				var num;
				if (!--self._scrlcnt && (num = self._start))
					self._doScrollNow(num, true);
			}, 10);
	},
	_doScrollNow: function (num, force) {
		var width = this.$n('cave').offsetWidth,
			mesh = this.parent,
			cnt = num,
			rows = mesh.ebodyrows;

		if (mesh.head) {
			// set fixed size
			for (var faker, n = mesh.head.firstChild.$n('hdfaker'); n;
					n = n.nextSibling) {
				if (n.style.width.indexOf('px') == -1) {
					var sw = n.style.width = jq.px0(n.offsetWidth),
						wgt = zk.Widget.$(n);
					if (!wgt.$instanceof(zul.mesh.HeadWidget)) {
						if ((faker = wgt.$n('bdfaker')))
							faker.style.width = sw;
						if ((faker = wgt.$n('ftfaker')))
							faker.style.width = sw;
					}
				}
			}
			var colhead = mesh.head.getChildAt(this._columns).$n(), isVisible, hdWgt, shallUpdate;
			for (var display, faker, index = this._columns,
					tail = mesh.head.nChildren - index,
					n = colhead;
					n; n = n.nextSibling, index++, tail--) {
				
				isVisible = (hdWgt = zk.Widget.$(n)) && hdWgt.isVisible();
				shallUpdate = false;
				if (cnt-- <= 0) {// show
					if (n.style.width == '0px') {
						cellWidth = hdWgt._origWd;
						hdWgt._origWd = null;
						shallUpdate = true;
					}
				} else if (n.style.width != '0px') {//hide
					hdWgt._origWd = n.style.width || jq.px0(jq(n).outerWidth());
					cellWidth = '0px';
					shallUpdate = true;
				}
				
				if (force || shallUpdate) {
					n.style.width = cellWidth;
					if ((faker = jq('#' + n.id + '-hdfaker')[0]))
						faker.style.width = cellWidth;
					if ((faker = jq('#' + n.id + '-bdfaker')[0]) && isVisible)
						faker.style.width = cellWidth;
					if ((faker = jq('#' + n.id + '-ftfaker')[0]))
						faker.style.width = cellWidth;

					// foot
					if (mesh.foot) {
						var eFootTbl = mesh.efoottbl;
						
						if (eFootTbl) {
							var tBodies = eFootTbl.tBodies;
							
							if (tBodies) {
								tBodies[tBodies.length - 1].rows[0].cells[ofs].style.width = cellWidth;
							}
						}
					}
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

		} else if (!rows || !rows.length) {
			return;
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

		mesh._restoreFocus();
		if (zk.safari) {
			var table,oldCSS;
			if (table = mesh.eheadtbl) {
				oldCSS = table.style.display;
				if (oldCSS != 'none') {
					table.style.display = 'none';
					setTimeout(function () {
						table.style.display = oldCSS;
					}, 0);
				}
			}
		}
	}
});

})();