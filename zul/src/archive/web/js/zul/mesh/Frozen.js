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
		return v ? v : 1;
	}
	function _fixaux(cells, from, to) {
		for (var j = 0, k = 0, cl = cells.length; j < cl; ++j) {
			var ke = k + _colspan( zk.Widget.$(cells[j]));
			// B70-ZK-2071: Calculate the colspan when scroll back.
			if ((from >= k && ke > from) || (to <= k && ke > to)) { //found
				for (; j < cl && k < to; ++j, k = ke) {
					var cell = cells[j],
						ke = k + _colspan(cell),
						v = k - from, v2 = ke - to;
					v = (v > 0 ? v: 0) + (v2 > 0 ? v2: 0);
					if (v) {
						cell.style.display = '';
						cell.style.width = '';
						cell.colSpan = v;
					} else {
						cell.style.display = 'none';
						cell.style.width = '0px';
					}
				}
				for (; j < cl; ++j) {
					var cell = cells[j];
					if (zk.parseInt(cell.style.width) != 0)
						break; //done
					cell.style.display = '';
					cell.style.width = '';
					// B70-ZK-2071: Reset the colspan when the previous cell is in viewport.
					cell.colSpan = _colspan(cell);
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
		
		if (parent.eheadtbl) {
			var cells = parent._getFirstRowCells(parent.eheadrows),
				totalcols = cells.length,
				columns = wgt._columns,
				leftWidth = 0;
			
			for (var i = 0; i < columns; i++)
				leftWidth += cells[i].offsetWidth;
			
			parent._deleteFakeRow(parent.eheadrows);
			
			wgt.$n('cave').style.width = jq.px0(leftWidth);
			var scroll = wgt.$n('scrollX'),
				width = parent.$n('body').offsetWidth;
			
			// B70-ZK-2074: Resize forzen's width as meshwidget's body.
			parent.$n('frozen').style.width = jq.px0(width);
			width -= leftWidth;
			scroll.style.width = jq.px0(width);
			var scrollScale = totalcols - columns - 1; /* fixed a bug related to the feature #3025419*/
			scroll.firstChild.style.width = jq.px0(width + 50 * scrollScale);
			wgt.syncScroll();
		}
	}
	
/**
 * A frozen component to represent a frozen column or row in grid, like MS Excel. 
 * <p>Default {@link #getZclass}: z-frozen.
 */
zul.mesh.Frozen = zk.$extends(zul.Widget, {
	_start: 0,
	_scrollScale: 0,
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
					this.onSize();
					this.syncScroll();
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
			this.syncScroll();
		}
	},
	/**
	 * Synchronizes the scrollbar according to {@link #getStart}.
	 */
	syncScroll: function () {
		var p = this.parent;
		if (p && p._nativebar) {
			var scroll = this.$n('scrollX');
			if (scroll)
				scroll.scrollLeft = this._start * 50;
		}
	},
	bind_: function () {
		this.$supers(zul.mesh.Frozen, 'bind_', arguments);
		var p = this.parent,
			body = p.$n('body'),
			foot = p.$n('foot');
		
		if (p._nativebar) {
			//B70-ZK-2130: No need to reset when beforeSize, ZK-343 with native bar works fine too.
			zWatch.listen({onSize: this});
			var scroll = this.$n('scrollX');
			this.$n().style.height = this.$n('cave').style.height = scroll.style.height
				 = scroll.firstChild.style.height = jq.px0(jq.scrollbarWidth());
			p.listen({onScroll: this.proxy(this._onScroll)}, -1000);
			p._currentLeft = 0;
			this.domListen_(scroll, 'onScroll');
		}
		
		if (body)
			jq(body).addClass('z-word-nowrap');
		if (foot)
			jq(foot).addClass('z-word-nowrap');
	},
	unbind_: function () {
		var p = this.parent,
			body = p.$n('body'),
			foot = p.$n('foot');
		
		if (p._nativebar) {
			this.domUnlisten_(this.$n('scrollX'), 'onScroll');
			p.unlisten({onScroll: this.proxy(this._onScroll)});
			zWatch.unlisten({onSize: this});
		}
		
		if (body)
			jq(body).removeClass('z-word-nowrap');
		if (foot)
			jq(foot).removeClass('z-word-nowrap');
		this.$supers(zul.mesh.Frozen, 'unbind_', arguments);
	},
	onSize: function () {
		if (!this._columns)
			return;
		var self = this;
		self._syncFrozen(); // B65-ZK-1470
		
		//B70-ZK-2129: prevent height changed by scrolling
		var p = this.parent, 
			phead = p.head, 
			firstHdcell, fhcs;
		if (p._nativebar && phead) {
			firstHdcell = phead.$n().cells[0];
			fhcs = firstHdcell.style;
			if (!fhcs.height)
				fhcs.height = firstHdcell.offsetHeight+'px';
		}
		
		// Bug 3218078, to do the sizing after the 'setAttr' command
		setTimeout(function () {
			_onSizeLater(self);
			self._syncFrozenNow();
		});
	},
	_syncFrozen: function () { //called by Rows, HeadWidget...
		this._shallSync = true;
	},
	_syncFrozenNow: function () {
		var num = this._start;
		if (this._shallSync && num)
			this._doScrollNow(num, true);
		
		this._shallSync = false;
	},
	beforeParentChanged_: function (p) {
		//bug B50-ZK-238
		if (this._lastScale) //if large then 0
			this._doScroll(0);
		
		this.$supers('beforeParentChanged_', arguments);
	},
	_onScroll: function (evt) {
		if (!evt.data || !zk.currentFocus)
			return;
		
		var p = this.parent,
			td,
			frozen = this,
			fn = function () {
				var cf = zk.currentFocus;
				if (cf) {
					td = p.getFocusCell(cf.$n());
					if (td && (index = td.cellIndex - frozen._columns) >= 0) {
						frozen.setStart(index);
						p.ebody.scrollLeft = 0;
					}
				}
			};
		if (p) {
			if (zk.ie < 11)
				setTimeout(fn, 0);
			else
				fn();
		}
		evt.stop();
	},
	_doScroll: function (n) {
		var p = this.parent, num;
		if (p._nativebar)
			num = Math.ceil(this.$n('scrollX').scrollLeft / 50);
		else
			num = Math.ceil(n);
		if (this._lastScale == num)
			return;
		this._lastScale = num;
		this._doScrollNow(num);
		this.smartUpdate('start', num);
		this._start = num;
	},
	_doScrollNow: function (num, force) {
		var totalWidth = 0,
			mesh = this.parent,
			cnt = num,
			rows = mesh.ebodyrows,
			c = this._columns;

		if (mesh.head) {
			// set fixed size
			var totalCols = mesh.head.nChildren,
				hdrows = mesh.eheadrows.rows,
				// B70-ZK-2071: Use mesh.head to get columns.
				hdcells = mesh.head.$n().cells,
				hdcol = mesh.ehdfaker.firstChild,
				ftrows = mesh.foot ? mesh.efootrows : null,
				ftcells = ftrows ? ftrows.rows[0].cells : null;
			
			for (var faker, i = 0; hdcol && i < totalCols; hdcol = hdcol.nextSibling, i++) {
				if (hdcol.style.width.indexOf('px') == -1) {
					var sw = hdcol.style.width = jq.px0(hdcells[i].offsetWidth),
						wgt = zk.Widget.$(hdcol);
					if (!wgt.$instanceof(zul.mesh.HeadWidget)) {
						if ((faker = wgt.$n('bdfaker')))
							faker.style.width = sw;
						if ((faker = wgt.$n('ftfaker')))
							faker.style.width = sw;
					}
				}
			}
			// B70-ZK-2071: Use mesh.head to get column.
			for (var i = c, faker; i < totalCols; i++) {
				var n = hdcells[i],
					hdWgt = zk.Widget.$(n),
					isVisible = hdWgt && hdWgt.isVisible(),
					shallUpdate = false,
					cellWidth;
				
				if (cnt-- <= 0) { //show
					var wd = isVisible ? n.offsetWidth : 0,
						nativebar = mesh._nativebar;
					if (force
							|| (!nativebar && (wd == 0 || wd == 1)) 
							|| (nativebar && n.style.display == 'none')) {
						cellWidth = hdWgt._origWd || jq.px(wd);
						hdWgt._origWd = null;
						shallUpdate = true;
					}
				} else if (force || n.offsetWidth != 0) { //hide
					faker = jq('#' + n.id + '-hdfaker')[0];
					hdWgt._origWd = hdWgt._origWd || faker.style.width;
					cellWidth = '0px';
					shallUpdate = true;
				}
				
				// ZK-2101: should give 0.1px for chrome
				if (zk.chrome && cellWidth && (parseInt(cellWidth) == 0))
					cellWidth = '0.1px';
				
				if (force || shallUpdate) {
					if ((faker = jq('#' + n.id + '-hdfaker')[0]))
						faker.style.width = cellWidth;
					if ((faker = jq('#' + n.id + '-bdfaker')[0]) && isVisible)
						faker.style.width = cellWidth;
					if ((faker = jq('#' + n.id + '-ftfaker')[0]))
						faker.style.width = cellWidth;

					var cw = zk.parseInt(cellWidth),
						hidden = cw == 0;
					
					if (mesh._nativebar) {
						mesh.ehdfaker.childNodes[i].style.display = hidden ? 'none' : '';
						hdcells[i].style.display = hidden ? 'none' : '';
					}
					hdcells[i].style.width = cellWidth;
					// foot
					if (ftcells) {
						if (mesh._nativebar) {
							mesh.eftfaker.childNodes[i].style.display = hidden ? 'none' : '';
							ftcells[i].style.display = hidden ? 'none' : '';
						}
						if (ftcells.length > i)
							ftcells[i].style.width = cellWidth;
					}
				}
			}
			
			//auxhead
			if (mesh._nativebar) {
				var hdr = mesh.head.$n(),
					hdrs = mesh.eheadrows.rows;
				for (var i = hdrs.length, r; i--;) {
					if ((r = hdrs[i]) != hdr) //skip Column
						_fixaux(r.cells, c + this._start, c + num); // B70-ZK-2071: Count start position.
				}
			}
			
			hdcol = mesh.ehdfaker.firstChild;
			for (var i = 0; hdcol && i < totalCols; hdcol = hdcol.nextSibling, i++) {
				if (hdcol.style.display != 'none')
					totalWidth += zk.parseInt(hdcol.style.width);
			}
		}
		// Set style width to table to avoid colgroup width not working 
		// because of width attribute (width="100%") on table
		var headtbl, bodytbl, foottbl;
		if (headtbl = mesh.eheadtbl)
			headtbl.style.width = jq.px(totalWidth);
		if (bodytbl = mesh.ebodytbl)
			bodytbl.style.width = jq.px(totalWidth);
		if (foottbl = mesh.efoottbl)
			foottbl.style.width = jq.px(totalWidth);

		mesh._restoreFocus();
		
		// Bug ZK-601, Bug ZK-1572
		if (zk.ie8_) {
			zk(mesh).redoCSS();
		} else if (zk.ie9_) {
			var n = mesh.$n();
			n.className += ' ';
			if (n.offsetHeight);
			n.className.trim();
		}
	}
});

})();