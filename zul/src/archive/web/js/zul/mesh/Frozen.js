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
	// Bug 3218078
	function _onSizeLater(wgt) {		
		var parent = wgt.parent,
			bdfaker = parent.ebdfaker;
		
		// ZK-2130: should skip fake scroll bar
		if (parent.eheadtbl && parent._nativebar) {
			var cells = parent._getFirstRowCells(parent.eheadrows),
				totalcols = cells.length,
				cellsSize = totalcols,
				columns = wgt._columns,
				leftWidth = 0;
			
			//B70-ZK-2553: one may specify frozen without any real column
			if (!cells || cellsSize <= 0) {
				//no need to do the following computation since there is no any column
				return;
			}
			
			//ZK-2776: don't take hidden column, like setVisible(false), into account
			for (var hdcol = parent.ehdfaker.firstChild; hdcol; hdcol = hdcol.nextSibling) {
				var style = hdcol.style;
				if (style.visibility == 'hidden' || style.display == 'none' /*just in case*/)
					totalcols -= 1;
			}
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
		columns: [function (v) {
			return v < 0 ? 0 : v;
		}, function (v) {
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

	/**
	 * Synchronizes the scrollbar according to parent ebody scrollleft.
	 */
	syncScrollByParentBody: function () {
		var p = this.parent,
			ebody,
			l;
		if (p && p._nativebar && (ebody = p.ebody) && (l = ebody.scrollLeft) > 0) {
			var scroll = this.$n('scrollX');
			if (scroll) {
				var scrollScale = l / (ebody.scrollWidth - ebody.clientWidth);
				scroll.scrollLeft = Math.ceil(scrollScale * (scroll.scrollWidth - scroll.clientWidth));
			}
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
			var scroll = this.$n('scrollX'),
				scrollbarWidth = jq.scrollbarWidth();
			// ZK-2583: native IE bug, add 1px in scroll div's height for workaround
			this.$n().style.height = this.$n('cave').style.height = scroll.style.height
				 = scroll.firstChild.style.height = jq.px0(zk.ie ? scrollbarWidth + 1 : scrollbarWidth);
			p._currentLeft = 0;
			this.domListen_(scroll, 'onScroll');

			var head = p.$n('head');
			if (head)
				this.domListen_(head, 'onScroll', '_doHeadScroll');
			
		} else {
			// Bug ZK-2264
			this._shallSyncScale = true;
		}
		// refix-ZK-3100455 : grid/listbox with frozen trigger "invalidate" should _syncFrozenNow
		zWatch.listen({onResponse: this});
		if (body)
			jq(body).addClass('z-word-nowrap');
		if (foot)
			jq(foot).addClass('z-word-nowrap');
	},
	unbind_: function () {
		var p = this.parent,
			body = p.$n('body'),
			foot = p.$n('foot'),
			head = p.$n('head');
		
		if (p._nativebar) {
			this.domUnlisten_(this.$n('scrollX'), 'onScroll');
			p.unlisten({onScroll: this.proxy(this._onScroll)});
			zWatch.unlisten({onSize: this});

			if (head)
				this.domUnlisten_(head, 'onScroll', '_doHeadScroll');
		} else {
			this._shallSyncScale = false;
		}
		// refix-ZK-3100455 : grid/listbox with frozen trigger "invalidate" should _syncFrozenNow
		zWatch.unlisten({onResponse: this});
		if (body)
			jq(body).removeClass('z-word-nowrap');
		if (foot)
			jq(foot).removeClass('z-word-nowrap');
		this.$supers(zul.mesh.Frozen, 'unbind_', arguments);
	},
	// Bug ZK-2264, we should resync the variable of _scrollScale, which do the same as HeadWidget.js
	onResponse: function () {
		if (this.parent._nativebar) {
			// refix-ZK-3100455 : grid/listbox with frozen trigger "invalidate" should _syncFrozenNow
			this._syncFrozenNow();
		} else if (this._shallSyncScale) {
			var hdfaker = this.parent.ehdfaker;
			if (hdfaker) {
				this._scrollScale = hdfaker.childNodes.length - this._columns - 1;
			}
			this._shallSyncScale = false;
		}
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
			//B70-ZK-2558: frozen will onSize before other columns, 
			//so there might be no any column in the beginning
			var n = phead.$n();
			firstHdcell = n ? (n.cells ? n.cells[0] : null) : null;
			//B70-ZK-2463: if firstHdcell is not undefined
			if (firstHdcell) {
				fhcs = firstHdcell.style;
				if (!fhcs.height)
					fhcs.height = firstHdcell.offsetHeight + 'px';
			}
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
		//ZK-2651: JS Error showed when clear grid children component that include frozen
		if (this.desktop && this._lastScale) //if large then 0
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

						if (p.ehead)
							p.ehead.scrollLeft = 0;
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
	_doHeadScroll: function (evt) {
		var head = evt.domTarget,
			num = Math.ceil(head.scrollLeft / 50);
		// ignore scrollLeft is 0
		if (!head.scrollLeft || this._lastScale == num)
			return;
		evt.data = head.scrollLeft;
		this._onScroll(evt);
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
				
				//ZK-2776, once a column is hidden, there is an additional style
				var style = n.style;
				if (style.visibility == 'hidden' || style.display == 'none' /*just in case*/)
					continue; //skip column which is hide
				
				if (cnt-- <= 0) { //show
					var wd = isVisible ? 
							(zk.ie ? Math.max(jq(n).width(), 0) : n.offsetWidth) // Bug ZK-2690
							: 0,
						nativebar = mesh._nativebar;
					// ZK-2071: nativebar behavior should be same as fakebar
					if (force || (wd < 1)) {
						cellWidth = hdWgt._origWd || jq.px(wd);
						// ZK-2772: consider faker's width first for layout consistent
						// if the column is visible.
						if ((wd > 1) && (faker = jq('#' + n.id + '-hdfaker')[0]) && faker.style.width)
							cellWidth = faker.style.width;
						hdWgt._origWd = null;
						shallUpdate = true;
					}
				} else if (force ||
						 // Bug ZK-2690
						((zk.ie ? Math.max(jq(n).width(), 0) : n.offsetWidth) != 0)) { //hide
					faker = jq('#' + n.id + '-hdfaker')[0];
					//ZK-2776: consider faker's width first for layout consistent
					if (faker.style.width && zk.parseInt(faker.style.width) > 1)
						hdWgt._origWd = faker.style.width;
					cellWidth = '0px';
					shallUpdate = true;
				}
				
				// ZK-2101: should give 0.1px for chrome and safari
				if ((zk.chrome || zk.safari) && cellWidth && (parseInt(cellWidth) == 0))
					cellWidth = '0.1px';
				
				if (force || shallUpdate) {
					if ((faker = jq('#' + n.id + '-hdfaker')[0]))
						faker.style.width = cellWidth;
					if ((faker = jq('#' + n.id + '-bdfaker')[0]) && isVisible)
						faker.style.width = cellWidth;
					if ((faker = jq('#' + n.id + '-ftfaker')[0]))
						faker.style.width = cellWidth;

					// ZK-2071: display causes wrong in colspan case
					// var cw = zk.parseInt(cellWidth),
					//	hidden = cw == 0;
					//
					// if (mesh._nativebar && (!hdWgt._hflex || hdWgt._hflex == 'min')) {
					//	mesh.ehdfaker.childNodes[i].style.display = hidden ? 'none' : '';
					//	hdcells[i].style.display = hidden ? 'none' : '';
					// } 
					
					hdcells[i].style.width = cellWidth;
					// foot
					if (ftcells) {
						// ZK-2071: display causes wrong in colspan case
						// 
						// if (mesh._nativebar) {
						//	mesh.eftfaker.childNodes[i].style.display = hidden ? 'none' : '';
						//	ftcells[i].style.display = hidden ? 'none' : '';
						// }
						if (ftcells.length > i)
							ftcells[i].style.width = cellWidth;
					}
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