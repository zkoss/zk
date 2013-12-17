/* MeshWidget.js

	Purpose:

	Description:

	History:
		Sat May  2 09:36:31     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The two-dimensional mesh related widgets.
 * A mesh widget is a two-dimensional widgets, such as grid, listbox and tree.
 * Classes in this package is the skeletal implementation that can be used
 * to simplify the implementation of grid, listbox and tree.
 */
//zk.$package('zul.mesh');

(function () {
	var _shallFocusBack;
	function _calcMinWd(wgt) {
		var wgtn = wgt.$n(),
			ws = wgtn ? wgtn.style.whiteSpace : ''; //bug#3106514: sizedByContent with not visible columns
		if (wgtn) {
			if (zk.ie8)
				wgt._wsbak = ws; // B50-ZK-432
			wgtn.style.whiteSpace = 'nowrap'; // B50-3316030, B50-3346235: pre cause extra space
		}
		var eheadtblw,
			efoottblw,
			ebodytblw,
			eheadtblfix,
			efoottblfix,
			ebodytblfix,
			hdfaker = wgt.ehdfaker,
			bdfaker = wgt.ebdfaker,
			ftfaker = wgt.eftfaker,
			head = wgt.head,
			headn = head ? head.$n() : null,
			hdfakerws = [],
			bdfakerws = [],
			ftfakerws = [],
			hdws = [],
			hdcavews = [];
		
		if (wgt.eheadtbl && headn) {//clear and backup headers widths
			wgt.ehead.style.width = '';
			eheadtblw = wgt.eheadtbl.width;
			wgt.eheadtbl.width = '';
			wgt.eheadtbl.style.width = '';
			eheadtblfix = wgt.eheadtbl.style.tableLayout;
			wgt.eheadtbl.style.tableLayout = '';
			if (zk.chrome)
				wgt.ebodytbl.style.display = 'block';
			var headcol = hdfaker.firstChild,
				headcell = headn.firstChild;
			for (var i = 0; headcol; headcol = headcol.nextSibling, i++) {
				var headcave = headcell.firstChild;
				if (!headcave)
					continue;
				hdfakerws[i] = headcol.style.width;
				headcol.style.width = '';
				hdws[i] = headcell.style.width;
				headcell.style.width = '';
				hdcavews[i] = headcave.style.width;
				headcave.style.width = '';
				
				headcell = headcell.nextSibling;
			}
		}
		if (headn)
			headn.style.width = '';
		if (wgt.efoottbl) {//clear and backup footers widths
			wgt.efoot.style.width = '';
			efoottblw = wgt.efoottbl.width;
			wgt.efoottbl.width = '';
			wgt.efoottbl.style.width = '';
			efoottblfix = wgt.efoottbl.style.tableLayout;
			wgt.efoottbl.style.tableLayout = '';
			if (zk.chrome)
				wgt.ebodytbl.style.display = 'block';
			if (ftfaker) {
				var footcol = ftfaker.firstChild
				for (var i = 0; footcol; footcol = footcol.nextSibling) {
					ftfakerws[i++] = footcol.style.width;
					footcol.style.width = '';
				}
			}
		}
		if (wgt.ebodytbl) {//clear and backup body faker widths
			wgt.ebody.style.width = '';
			ebodytblw = wgt.ebodytbl.width;
			wgt.ebodytbl.width = '';
			wgt.ebodytbl.style.width = '';
			ebodytblfix = wgt.ebodytbl.style.tableLayout;
			wgt.ebodytbl.style.tableLayout = '';
			if (zk.chrome)
				wgt.ebodytbl.style.display = 'block';
			if (bdfaker) {
				var bodycol = bdfaker.firstChild;
				for (var i = 0; bodycol; bodycol = bodycol.nextSibling) {
					bdfakerws[i++] = bodycol.style.width;
					bodycol.style.width = '';
				}
			}
		}

		//calculate widths
		var wds = [],
			width = 0,
			len = head ? head.nChildren : 0,
			w = head ? head = head.firstChild : null,
			headWgt = wgt.getHeadWidget(),
			max = 0, maxj;
		if (bdfaker && w) {
			var bodycells = wgt._getFirstRowCells(wgt.ebodyrows),
				footcells = ftfaker ? wgt._getFirstRowCells(wgt.efootrows) : null;
			
			for (var i = 0; i < len; i++) {
				var wd = bodycells && bodycells[i] ? bodycells[i].offsetWidth : 0,
					$cv = zk(w.$n('cave')),
					hdwd = w && w.isVisible() ? ($cv.textSize()[0] + $cv.padBorderWidth() + zk(w.$n()).padBorderWidth()) : 0,
					ftwd = footcells && footcells[i] && zk(footcells[i]).isVisible() ? footcells[i].offsetWidth : 0,
					header;
				
				if ((header = headWgt.getChildAt(i)) && header.getWidth())
					hdwd = Math.max(hdwd, ftwd);
				if (hdwd > wd)
					wd = hdwd;
				if (ftwd > wd)
					wd = ftwd;
				wds[i] = wd;
				if (zk.ff > 4 || zk.ie > 8) // firefox4 & IE9, 10, 11 still cause break line in case B50-3147926 column 1
					++wds[i];
				width += wds[i]; // using wds[i] instead of wd for B50-3183172.zul
				if (w)
					w = w.nextSibling;
			}
			wgt._deleteFakeRow(wgt.ebodyrows);
			if (ftfaker)
				wgt._deleteFakeRow(wgt.efootrows);
		} else {
			var tr;
			if (tr = _getSigRow(wgt)) {
				for (var cells = tr.cells, i = cells.length; i--;) {
					var wd = cells[i].offsetWidth;
					wds[i] = wd;
					if (zk.ff > 4 || zk.ie > 8) // firefox4 & IE9, 10, 11 still cause break line in case B50-3147926 column 1
						++wds[i];
					width += wds[i]; // using wds[i] instead of wd for B50-3183172.zul
				}
			}
		}

		if (wgt.eheadtbl && headn) {//restore headers widths
			wgt.eheadtbl.width = eheadtblw || '';
			wgt.eheadtbl.style.tableLayout = eheadtblfix || '';
			if (zk.chrome)
				wgt.eheadtbl.style.display = '';
			var headcol = hdfaker.firstChild,
				headcell = headn.firstChild;
			for (var i = 0; headcol; headcol = headcol.nextSibling, i++) {
				var headcave = headcell.firstChild;
				if (!headcave)
					continue;
				headcol.style.width = hdfakerws[i];
				headcell.style.width = hdws[i];
				headcave.style.width = hdcavews[i];
				
				headcell = headcell.nextSibling;
			}
		}
		if (wgt.efoottbl) {//restore footers widths
			wgt.efoottbl.width = efoottblw || '';
			wgt.efoottbl.style.tableLayout = efoottblfix || '';
			if (zk.chrome)
				wgt.efoottbl.style.display = '';
			if (ftfaker) {
				var footcol = ftfaker.firstChild
				for (var i = 0; footcol; footcol = footcol.nextSibling)
					footcol.style.width = ftfakerws[i++];
			}
		}
		if (wgt.ebodytbl) {//restore body fakers widths
			wgt.ebodytbl.width = ebodytblw || '';
			wgt.ebodytbl.style.tableLayout = ebodytblfix || '';
			if (zk.chrome)
				wgt.ebodytbl.style.display = '';
			if (bdfaker) {
				var bodycol = bdfaker.firstChild;
				for (var i = 0; bodycol; bodycol = bodycol.nextSibling)
					bodycol.style.width = bdfakerws[i++];
			}
		}

		if (wgtn)
			wgtn.style.whiteSpace = ws;
		return {width: width, wds: wds};
	}
	function _fixBodyMinWd(wgt, fixMesh) {
		// effective only when there is no header
		var sbc = wgt.isSizedByContent(),
			meshmin = wgt._hflex == 'min';
		if (!wgt.head && (meshmin || sbc)) {
			var bdw = zk(wgt.$n()).padBorderWidth(),
				wd = _getMinWd(wgt) + bdw, // has to call _getMinWd first so wgt._minWd will be available
				tr = wgt.ebodytbl,
				wds = wgt._minWd.wds,
				wlen = wds.length;
			if (fixMesh && meshmin)
				wgt.setFlexSize_({width:wd}, true);
			if (!(tr = tr.firstChild) || !(tr = tr.firstChild))
				return; // no first tr
			for (var c = tr.firstChild, i = 0; c && (i < wlen); c = c.nextSibling)
				c.style.width = jq.px(wds[i++]);
			if (sbc && !meshmin) {
				// add flex <td> if absent
				var bdfx = tr.lastChild,
					bdfxid = wgt.uuid + '-bdflex';
				if (!bdfx || bdfx.id != bdfxid) {
					jq(tr).append('<td id="' + bdfxid + '"></td>');
					bdfx = tr.lastChild;
				}
			}
		}
	}
	function _fixPageSize(wgt, rows) {
		var ebody = wgt.ebody;
		if (!ebody)
			return; //not ready yet
		var max = ebody.offsetHeight;
		if (zk(ebody).hasHScroll()) //with horizontal scrollbar
			max -= jq.scrollbarWidth();
		if (max == wgt._prehgh) return false; //same height, avoid fixing page size
		wgt._prehgh = max;
		var ebodytbl = wgt.ebodytbl,
			etbparent = ebodytbl.offsetParent,
			etbtop = ebodytbl.offsetTop,
			hgh = 0,
			row,
			j = 0;
		for (var it = wgt.getBodyWidgetIterator({skipHidden:true}),
				len = rows.length, w; (w = it.next()) && j < len; j++) {
			row = rows[j];
			var top = row.offsetTop - (row.offsetParent == etbparent ? etbtop : 0);
			if (top > max) {
				--j;
				break;
			}
			hgh = top;
		}
		if (row) { //there is row
			if (top <= max) { //row not exceeds the height, estimate
				hgh = hgh + row.offsetHeight;
				j = Math.floor(j * max / hgh);
			}
			//enforce pageSize change
			if (j == 0) j = 1; //at least one per page
			if (j != wgt.getPageSize()) {
				wgt.fire('onPageSize', {size: j});
				return true;
			}
		}
	}
	function _adjMinWd(wgt) {
		if (wgt._hflex == 'min') {
			var w = _getMinWd(wgt),
				n = wgt.$n();
			wgt._hflexsz = w + zk(n).padBorderWidth(); //override
			n.style.width = jq.px0(wgt._hflexsz);
		}
	}
	function _getMinWd(wgt) {
		wgt._calcMinWds();
		var bdfaker = wgt.ebdfaker,
			wd,
			wds = [],
			width,
			_minwds = wgt._minWd.wds;
		if (wgt.head && bdfaker) {
			width = 0;
			var w = wgt.head.firstChild,
				bdcol = bdfaker.firstChild;
			for (var i = 0; w; w = w.nextSibling) {
				if (w._hflex == 'min')
					wd = wds[i] = _minwds[i] + zk(w.$n()).padBorderWidth();
				else {
					if (w._width && w._width.indexOf('px') > 0)
						wd = wds[i] = zk.parseInt(w._width);
					else
						wd = wds[i] = zk.parseInt(bdcol.style.width);
				}
				width += wd;
				++i;
				bdcol = bdcol.nextSibling;
			}
		} else
			width = wgt._minWd.width; // no header
		return width;
	}
	function _getSigRow(wgt) {
		// scan for tr with largest number of td children
		var rw = wgt.getBodyWidgetIterator().next(),
			tr = rw ? rw.$n() : null;
		if (!tr)
			return;
		for (var maxtr = tr, len, max = maxtr.cells.length; tr; tr = tr.nextSibling)
			if ((len = tr.cells.length) > max) {
				maxtr = tr;
				max = len;
			}
		return maxtr;
	}
	function _cpCellWd(wgt) {
		var dst = wgt.efootrows.rows[0],
			srcrows = wgt.ebodyrows.rows;
		if (!dst || !srcrows || !srcrows.length || !dst.cells.length)
			return;
		var ncols = dst.cells.length,
			src, maxnc = 0;
		for (var j = 0, it = wgt.getBodyWidgetIterator({skipHidden:true}), w; (w = it.next());) {
			if (wgt._modal && !w._loaded)
				continue;

			var row = srcrows[j++], $row = zk(row),
				cells = row.cells, nc = $row.ncols(),
				valid = cells.length == nc && $row.isVisible();
				//skip with colspan and invisible
			if (valid && nc >= ncols) {
				maxnc = ncols;
				src = row;
				break;
			}
			if (nc > maxnc) {
				src = valid ? row: null;
				maxnc = nc;
			} else if (nc == maxnc && !src && valid) {
				src = row;
			}
		}
		if (!maxnc) return;

		var fakeRow = !src;
		if (fakeRow) { //the longest row containing colspan
			src = document.createElement('TR');
			src.style.height = '0px';
				//Note: we cannot use display="none" (offsetWidth won't be right)
			for (var j = 0; j < maxnc; ++j)
				src.appendChild(document.createElement('TD'));
			srcrows[0].parentNode.appendChild(src);
		}
		//we have to clean up first, since, in FF, if dst contains %
		//the copy might not be correct
		for (var j = maxnc; j--;)
			dst.cells[j].style.width = '';

		var sum = 0;
		for (var j = maxnc; j--;) {
			var d = dst.cells[j], s = src.cells[j];
			if (zk.opera) {
				sum += s.offsetWidth;
				d.style.width = zk(s).contentWidth();
			} else {
				d.style.width = s.offsetWidth + 'px';
				if (maxnc > 1) { //don't handle single cell case (bug 1729739)
					var v = s.offsetWidth - d.offsetWidth;
					if (v != 0) {
						v += s.offsetWidth;
						if (v < 0) v = 0;
						d.style.width = v + 'px';
					}
				}
			}
		}
		if (zk.opera && wgt.isSizedByContent())
			dst.parentNode.parentNode.style.width = sum + 'px';
		if (fakeRow)
			src.parentNode.removeChild(src);
	}

/**
 *  A skeletal implementation for a mesh widget.
 *  @see zul.grid.Grid
 *  @see zul.sel.Tree
 *  @see zul.sel.Listbox
 */
zul.mesh.MeshWidget = zk.$extends(zul.Widget, {
	_pagingPosition: 'bottom',
	_prehgh: -1,
	_minWd: null, //minimum width for each column
	$init: function () {
		this.$supers('$init', arguments);
		this.heads = [];
	},

	_innerWidth: '100%',
	_currentTop: 0,
	_currentLeft: 0,

	$define: {
		/**
		 * Returns how to position the paging of the widget at the client screen.
		 * It is meaningless if the mold is not in "paging".
		 * @return String
		 */
		/**
		 * Sets how to position the paging of the widget at the client screen.
		 * It is meaningless if the mold is not in "paging".
		 * @param String pagingPosition how to position. It can only be "bottom" (the default), or
		 * "top", or "both".
		 */
		pagingPosition: _zkf = function () {
			this.rerender();
		},
		/**
		 * Returns whether sizing the widget column width by its content. Default is false.
		 * <p>Note: if the "sized-by-content" attribute of component is specified,
		 * it's prior to the original value.
		 * @return boolean
		 * @see #setSizedByContent
		 */
		/**
		 * Sets whether sizing the widget column width by its content. Default is false, i.e.
		 * the outline of the widget is dependent on browser. It means, we don't
		 * calculate the width of each cell. If set to true, the outline will count on
		 * the content of body. In other words, the outline of the widget will be like
		 * ZK version 2.4.1 that the header's width is only for reference.
		 *
		 * <p> You can also specify the "sized-by-content" attribute of component in
		 * lang-addon.xml directly, it will then take higher priority.
		 * @param boolean byContent
		 */
		sizedByContent: _zkf,
		/**
		 * Return column span hint of this widget.
		 * <p>Default: null
		 * @return String column span hint of this widget.
		 * @since 5.0.6
		 * @see #setSpan
		 */
		/**
		 * Sets column span hint of this mesh widget.
		 * <p>The parameter span is a number in String type indicating how this
		 * component distributes remaining empty space to the
		 * specified column(0-based). "0" means distribute remaining empty space to the 1st column; "1" means
		 * distribute remaining empty space to the 2nd column, etc.. The spanning column will grow to
		 * fit the extra remaining space.</p>
		 * <p>Special span hint with "true" means span ALL columns proportionally per their
		 * original widths while null or "false" means NOT spanning any column.</p>
		 * <p>Default: null. That is, NOT span any column.</p>
		 * <p>Note span is meaningful only if there is remaining empty space for columns.</p>
		 *
		 * @param String span the column span hint.
		 * @since 5.0.6
		 * @see #getSpan
		 */
		span: function(v) {
			var x = (true === v || 'true' == v) ? -65500 : (false === v || 'false' == v) ? 0 : (zk.parseInt(v) + 1);
			this._nspan = x < 0 && x != -65500 ? 0 : x;
			this.rerender();
		},
		/**
		 * Returns whether turn on auto-paging facility when mold is
		 * "paging". If it is set to true, the {@link #setPageSize} is ignored;
		 * rather, the page size(number of item count) is automatically determined by the
		 * height of this widget dynamically.
		 * @return boolean
		 * @see #setAutopaging
		 */
		/**
		 * Sets whether turn on auto-paging facility when mold is
		 * "paging". If it is set to true, the {@link #setPageSize} is ignored;
		 * rather, the page size(number of item count) is automatically determined by the
		 * height of this widget dynamically.
		 * @param boolean autopaging
		 */
		autopaging: _zkf,
		/**
		 * Returns the external Paging widget, if any.
		 * @return Paging
		 */
		/**
		 * Sets the external Paging widget.
		 * @param Paging paging
		 */
		paginal: null,
		/**
		 * Returns whether the widget is in model mode or not.
		 * @return boolean
		 */
		/**
		 * Sets whether the widget is in model mode.
		 * @param boolean inModel
		 */
		model: null,
		/**
		 * Returns the inner width of this component.
		 * The inner width is the width of the inner table.
		 * <p>Default: "100%"
		 * @see #setInnerWidth
		 * @return String
		 */
		/**
		 * Sets the inner width of this component.
		 * The inner width is the width of the inner table.
		 * By default, it is 100%. That is, it is the same as the width
		 * of this component. However, it is changed when the user
		 * is sizing the column's width.
		 *
		 * <p>Application developers rarely call this method, unless
		 * they want to preserve the widths of sizable columns
		 * changed by the user.
		 * To preserve the widths, the developer have to store the widths of
		 * all columns and the inner width ({@link #getInnerWidth}),
		 * and then restore them when re-creating this component.
		 *
		 * @param String innerWidth the inner width. If null, "100%" is assumed.
		 */
		innerWidth: function (v) {
			if (v == null) this._innerWidth = v = '100%';
			if (this.eheadtbl) this.eheadtbl.style.width = v;
			if (this.ebodytbl) this.ebodytbl.style.width = v;
			if (this.efoottbl) this.efoottbl.style.width = v;
		}
	},
	/** Returns the page size, aka., the number rows per page.
	 * @return int
	 * @see Paging#getPageSize
	 */
	getPageSize: function () {
		return (this.paging || this._paginal).getPageSize();
	},
	/** Sets the page size, aka., the number rows per page.
	 * @param int pageSize
	 * @see Paging#setPageSize
	 */
	setPageSize: function (pgsz) {
		(this.paging || this._paginal).setPageSize(pgsz);
	},
	/** Returns the number of pages.
	 * Note: there is at least one page even no item at all.
	 * @return int
	 * @see Paging#getPageCount
	 */
	getPageCount: function () {
		return (this.paging || this._paginal).getPageCount();
	},
	/** Returns the active page (starting from 0).
	 * @return int
	 * @see Paging#getActivePage
	 */
	getActivePage: function () {
		return (this.paging || this._paginal).getActivePage();
	},
	/** Sets the active page (starting from 0).
	 * @param int activePage
	 * @see Paging#setActivePage
	 */
	setActivePage: function (pg) {
		(this.paging || this._paginal).setActivePage(pg);
	},
	/**
	 * Returns whether the widget is in paging mold.
	 * @return boolean
	 */
	inPagingMold: function () {
		return 'paging' == this.getMold();
	},

	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		if (this.desktop) {
			this._setHgh(height);
			this.onSize();
		}
	},
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		if (this.eheadtbl) this.eheadtbl.style.width = '';
		if (this.efoottbl) this.efoottbl.style.width = '';
		if (this.desktop)
			this.onSize();
	},
	setStyle: function (style) {
		if (this._style != style) {
			this.$supers('setStyle', arguments);
			if (this.desktop)
				this.onSize();
		}
	},

	/**
	 * Returns the self's head widget.
	 * @return zul.mesh.HeadWidget
	 * @since 5.0.4
	 */
	getHeadWidget: function () {
		return this.head;
	},
	/**
	 * Returns the index of the cell including the child got focus.
	 * @param DOMElement el the element got focus.
	 * @return int the cell index.
	 * @since 5.0.7
	 */
	getFocusCell: function (el) {
	},
	_moveToHidingFocusCell: function (index) { //used in Row/Listcell
		//B50-3178977 navigating the input in hiddin column.
		var td = this.ehdfaker ? this.ehdfaker.childNodes[index] : null,
			frozen = this.frozen,
			bar;
		if (td && frozen && zk.parseInt(td.style.width) == 0 &&
			(index = index - frozen.getColumns()) >= 0) {
			if (this._nativebar) {
				frozen.setStart(index);
			} else if (bar = this._scrollbar) {
				frozen._doScrollNow(index);
				bar.setBarPosition(index);
			}
			_shallFocusBack = true;
		}
	},
	_restoreFocus: function () { //used in Frozen
		if (_shallFocusBack && zk.currentFocus) {
			_shallFocusBack = false;
			zk.currentFocus.focus();
		}
	},

	bind_: function () {
		this.$supers(zul.mesh.MeshWidget, 'bind_', arguments);
		
		this._bindDomNode();
		if (this._hflex != 'min')
			this._fixHeaders();
		if ((zk.webkit || zk.ie < 11) && this.ehead) //sync scroll for input tab key scroll
			this.domListen_(this.ehead, 'onScroll', '_doSyncScroll');
		
		var ebody = this.ebody;
		if (this._nativebar && ebody) {
			this.domListen_(ebody, 'onScroll', '_doScroll');
			ebody.style.overflow = 'auto';
			ebody.style.position = 'static'; //IE8: avoid scrollbar missing
			if (this.efrozen)
				jq(ebody).css('overflow-x', 'hidden'); // keep non line break
		}
		zWatch.listen({onSize: this, onResponse: this});
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, onResponse: this});
		if ((zk.webkit || zk.ie < 11) && this.ehead) //sync scroll for input tab key scroll
			this.domUnlisten_(this.ehead, 'onScroll', '_doSyncScroll');
		var ebody = this.ebody;
		if (this._nativebar && ebody && this.efrozen)
			jq(ebody).css('overflow-x', 'auto');
		this.$supers(zul.mesh.MeshWidget, 'unbind_', arguments);
	},
	clearCache: function () {
		this.$supers('clearCache', arguments);
		this.ebody = this.ehead = this.efoot = this.efrozen = this.ebodytbl
			= this.eheadtbl = this.efoottbl = this.ebodyrows
			= this.ehdfaker = this.ebdfaker = null;
	},

	/** Synchronizes the size immediately.
	 * This method is called automatically if the widget is created
	 * at the server (i.e., {@link #inServer} is true).
	 * You have to invoke this method only if you create this widget
	 * at client and add or remove children from this widget.
	 * @since 5.0.8
	 */
	syncSize: function () {
		if (this.desktop) {
			this.clearCachedSize_();
			if (this._hflex == 'min') {
				zFlex.onFitSize.apply(this);
			} else {
				this._calcMinWds();
				this._fixHeaders();
				this.onSize();
			}
		}
	},
	onResponse: function () {
		if (this._shallSize)
			this.syncSize();
	},
	_syncSize: function () {
		// fixed for F50-3025422.zul on ZTL
		if (this.desktop)
			this._shallSize = true;
	},
	_fixHeaders: function (force) { //used in HeadWidget
		if (this.head && this.ehead) {
			var empty = true,
				flex = false,
				hdsmin = (this._hflex == 'min') || this.isSizedByContent();
			for (var i = this.heads.length; i-- > 0;)
				for (var w = this.heads[i].firstChild; w; w = w.nextSibling) {
					if (hdsmin && !this.ehdfaker.childNodes[i].style.width && !w._nhflex) {
						// B50-3357475: assume header hflex min if width/hflex unspecified
						w._hflex = 'min';
						w._nhflex = -65500; // min
						w._nhflexbak = true;
					}
					if (!flex && w._nhflex)
						flex = true;
					if (w.getLabel() || w.getImage() || w.nChildren)
						empty = false;
				}
			var old = this.ehead.style.display,
				tofix = force && flex && this.isRealVisible(); //Bug ZK-1647: no need to consider empty header for flex calculation
			this.ehead.style.display = empty ? 'none' : '';
			//onSize is not fired to empty header when loading page, so we have to simulate it here
			for (var w = this.head.firstChild; w; w = w.nextSibling) {
				if (tofix && w._nhflex)
					w.fixFlex_();
				if (w._nhflexbak) {
					delete w._hflex;
					delete w._nhflex;
					delete w._nhflexbak;
				}
			}
			return old != this.ehead.style.display;
		}
	},
	_adjFlexWd: function () { //used by HeadWidget
		var head = this.head;
		if (head) {
			var hdfaker = this.ehdfaker,
				bdfaker = this.ebdfaker,
				hdcol = hdfaker.firstChild,
				bdcol = bdfaker.firstChild,
				ftfaker = this.eftfaker,
				ftcol;
			
			if (ftfaker)
				ftcol = ftfaker.firstChild;
			
			for (var w = head.firstChild, wd; w; w = w.nextSibling) {
				// B70-ZK-2036: Do not adjust widget's width if it is not visible.
				if (w.isVisible() && (wd = w._hflexWidth) !== undefined) {
					bdcol.style.width = zk(bdcol).revisedWidth(wd) + 'px';
					hdcol.style.width = bdcol.style.width;
					if (ftcol)
						ftcol.style.width = bdcol.style.width;
				}
				bdcol = bdcol.nextSibling;
				hdcol = hdcol.nextSibling;
				if (ftcol)
					ftcol = ftcol.nextSibling;
			}
			_adjMinWd(this);
		}
	},
	_bindDomNode: function () {
		this.ehead = this.$n('head');
		this.eheadtbl = this.$n('headtbl');
		this.ebody = this.$n('body');
		this.ebodytbl = this.$n('cave');
		this.efoot = this.$n('foot');
		this.efoottbl = this.$n('foottbl');
		this.efrozen = this.$n('frozen');
		
		// Grid will bind ebodyrows in Rows widget
		var erows = this.$n('rows');
		if (this.ebody && erows)
			this.ebodyrows = erows;
		
		if (this.ehead) {
			this.eheadrows = this.$n('headrows');
			this.ehdfaker = this.head.$n('hdfaker');
			this.ebdfaker = this.head.$n('bdfaker');
			if (this.efoot)
				this.eftfaker = this.head.$n('ftfaker');
		}
		if (this.efoot)
			this.efootrows = this.$n('footrows');
	},
	replaceHTML: function() { //tree outer
		var old = this._syncingbodyrows;
		this._syncingbodyrows = true;
		try {
			//bug #2995434
			//20100503, Henri: cannot use $supers('replaceHTML') since it
			//will recursive back to this function via fire('onSize'). However,
			//ZK's $supers() is simulated and when we call $supers() again
			//here, the system thought it is calling from its super class rather
			//than this class and it will be wrong. Therefore, we are forced to
			//call super class's replaceHTML directly instead.
			//Therefore, we have to specify MeshWidget as follows
			this.$supers(zul.mesh.MeshWidget, 'replaceHTML', arguments);
		} finally {
			this._syncingbodyrows = old;
		}
	},
	replaceChildHTML_: function() { //rows outer
		var old = this._syncingbodyrows;
		this._syncingbodyrows = true;
		try {
			this.$supers('replaceChildHTML_', arguments);
		} finally {
			this._syncingbodyrows = old;
		}
	},
	fireOnRender: function (timeout) {
		if (!this._pendOnRender) {
			this._pendOnRender = true;
			setTimeout(this.proxy(this._onRender), timeout ? timeout : 100);
		}
	},
	_doScroll: function () { //called zkmax, overriden in Listbox
		var t = zul.mesh.Scrollbar.getScrollPosV(this),
			l = zul.mesh.Scrollbar.getScrollPosH(this),
			scrolled = (t != this._currentTop || l != this._currentLeft),
			ebody = this.ebody,
			ehead = this.ehead,
			efoot = this.efoot;

		if (this._nativebar && !(this.fire('onScroll', ebody.scrollLeft).stopped)) {
			if (this._currentLeft != ebody.scrollLeft) {
				if (ehead)
					ehead.scrollLeft = ebody.scrollLeft;
				if (efoot)
					efoot.scrollLeft = ebody.scrollLeft;
			}
		}


		
		// ZK-2046: should sync currentTop in rod mode see also Bug ZK-353
		if (scrolled /* && !this._listbox$rod && !this._grid$rod*/)
			this._currentTop = t;

		if (scrolled) // always sync for B30-1737660.zul
			this._currentLeft = l;

		if (!this.paging && !this._paginal)
			this.fireOnRender(zk.gecko ? 200 : 60);

		if (scrolled)
			this._fireOnScrollPos();
	},
	_doSyncScroll: function () { //sync scroll for input tab key scroll
		var ehead = this.ehead,
			ebody = this.ebody,
			efoot = this.efoot;
		if (ehead && zk(ehead).isVisible()) {
			if (this._currentLeft != ehead.scrollLeft) {
				if (ebody)
					ebody.scrollLeft = ehead.scrollLeft;
				if (efoot) 
					efoot.scrollLeft = ehead.scrollLeft;
			}
		}
	},
	_timeoutId: null,
	_fireOnScrollPos: function (time) { //overriden in zkmax
		clearTimeout(this._timeoutId);
		this._timeoutId = setTimeout(this.proxy(this._onScrollPos), time >= 0 ? time : 300);
	},
	_onScrollPos: function () {
		// Bug ZK-414
		if (this.ebody) {
			this._currentTop = zul.mesh.Scrollbar.getScrollPosV(this);
			this._currentLeft = zul.mesh.Scrollbar.getScrollPosH(this);
			this.fire('onScrollPos', {
				top: this._currentTop,
				left: this._currentLeft
			});
		}
	},
	_onRender: function () { //overriden in zkmax
		if (!this.$n())
			return; // the target may not exist. B50-ZK-963

		this._pendOnRender = false;
		if (this._syncingbodyrows || zAu.processing()) { //wait if busy (it might run outer)
			this.fireOnRender(zk.gecko ? 200 : 60); //is syncing rows, try it later
			return true;
		}

		var rows = this.ebodyrows ? this.ebodyrows.rows : null;
		if (this.inPagingMold() && this._autopaging && rows && rows.length)
			if (_fixPageSize(this, rows))
				return; //need to reload with new page size

		if (zk.ie8 && (this._wsbak !== undefined)) { // B50-ZK-432
			this.$n().style.whiteSpace = this._wsbak;
			delete this._wsbak;
		}

		if (!this.desktop || !this._model || !rows || !rows.length) return;

		//Note: we have to calculate from top to bottom because each row's
		//height might diff (due to different content)
		var items = [],
			min = zul.mesh.Scrollbar.getScrollPosV(this),
			max = min + this.ebody.offsetHeight;
		for (var j = 0, it = this.getBodyWidgetIterator({skipHidden:true}),
				len = rows.length, w; (w = it.next()) && j < len; j++) {
			if (!w._loaded) {
				var row = rows[j], $row = zk(row),
					top = $row.offsetTop();

				if (top + $row.offsetHeight() < min) continue;
				if (top > max) break; //Bug 1822517
				items.push(w);
			}
		}
		if (items.length) {
			this._shallFireOnRender = true;
			this.fire('onRender', {items: items}, {implicit:true});
		}
	},
	onSize: function () {
		if (this.isRealVisible()) { // sometimes the caller is not zWatch
			var n = this.$n();
			if (n._lastsz && n._lastsz.height == n.offsetHeight 
					&& n._lastsz.width == n.offsetWidth) {
				this.fireOnRender(155); // force to render while using live grouping
				return; // unchanged
			}
			this._calcSize();// Bug #1813722
			this.fireOnRender(155);
			
			if (this._nativebar) { // Bug ZK-355: keep scrollbar position
				var ebody = this.ebody;
				if (ebody.scrollHeight >= this._currentTop)
					ebody.scrollTop = this._currentTop;
				
				if (ebody.scrollWidth >= this._currentLeft) {
					ebody.scrollLeft = this._currentLeft;
					if (this.ehead) 
						this.ehead.scrollLeft = this._currentLeft;
					if (this.efoot) 
						this.efoot.scrollLeft = this._currentLeft;
				}
			}
			this._shallSize = false;
		}
	},
	_vflexSize: function () {
		var n = this.$n(),
			pgHgh = 0;
		if (this.paging) {
			var pgit = this.$n('pgit'),
				pgib = this.$n('pgib');
			if (pgit) pgHgh += pgit.offsetHeight;
			if (pgib) pgHgh += pgib.offsetHeight;
		}
		// Bug #1815882 and Bug #1835369
		var hgh = zk(n).contentHeight()
			- (this.ehead ? this.ehead.offsetHeight : 0)
			- (this.efoot ? this.efoot.offsetHeight : 0)
			- pgHgh;
		return this.frozen && this._nativebar ?
				hgh - this.efrozen.offsetHeight : hgh;
	},
	setFlexSize_: function (sz) {
		var n = this.$n(),
			head = this.$n('head');
		if (sz.height !== undefined) {
			if (sz.height == 'auto') {
				n.style.height = '';
				if (head)
					head.style.height = '';
			} else {
				return this.$supers('setFlexSize_', arguments);
			}
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto') {
				if (this._hflex != 'min')
					n.style.width = '';
				if (head)
					head.style.width = '';
			} else {
				return this.$supers('setFlexSize_', arguments);
			}
		}
	},
	/* set the height. */
	_setHgh: function (hgh) {
		var n = this.$n(),
			ebody = this.ebody,
			ebodyStyle = ebody.style;
		if (this.isVflex() || (hgh && hgh != 'auto' && hgh.indexOf('%') < 0)) {
			if (zk.webkit && ebodyStyle.height == jq.px(this._vflexSize()))
				return; // Bug ZK-417, ignore to set the same size
			ebodyStyle.height = ''; //allow browser adjusting to default size
			var h = this._vflexSize();
			if (h < 0)
				h = 0;
			if (this._vflex != 'min')
				ebodyStyle.height = h + 'px';
		} else {
			//Bug 1556099
			ebodyStyle.height = '';
			n.style.height = hgh;
		}
	},
	_ignoreHghExt: function () {
		return false;
	},
	/** Calculates the size. */
	_calcSize: function () {
		this._beforeCalcSize();
		//Bug 1553937: wrong sibling location
		//Otherwise,
		//IE: element's width will be extended to fit body
		//note: we don't solve this bug for paging yet
		var n = this.$n(),
			//Bug 1659601: we cannot do it in init(); or, IE failed!
			tblwd = zk(n).contentWidth(),
			sizedByContent = this.isSizedByContent(),
			ehead = this.ehead,
			ebody = this.ebody,
			ebodyrows = this.ebodyrows,
			efoot = this.efoot,
			efootrows = this.efootrows;
		
		if (ehead) {
			if (tblwd) {
				ehead.style.width = tblwd + 'px';
				if (ebody)
					ebody.style.width = tblwd + 'px';
				if (efoot)
					efoot.style.width = tblwd + 'px';
			}
			if (sizedByContent && ebodyrows)
				this._adjHeadWd();
			else if (tblwd && efoot)
				efoot.style.width = tblwd + 'px';
		} else if (efoot) {
			if (tblwd)
				efoot.style.width = tblwd + 'px';
			if (efootrows && ebodyrows)
				_cpCellWd(this);
		}
		
		//check if need to span width
		this._adjSpanWd();
		// no header case
		_fixBodyMinWd(this, true);
		
		// B50-ZK-543, B50-ZK-773
		// should re-calculate height because
		// the string height maybe changed after width changed.
		if (sizedByContent 
				&& this.getRows && this.getRows() > 1 
				&& (typeof this._calcHgh == 'function')
				&& this.ebody.style.height) { // check only if height exists for F50-3000339.zul
			this._calcHgh(); // recalculate height again ZK-796
		}
		
		n._lastsz = {height: n.offsetHeight, width: n.offsetWidth}; // cache for the dirty resizing.
		
		this._afterCalcSize();
	},
	_beforeCalcSize: function () {
		this._setHgh(this.$n().style.height);
	},
	_afterCalcSize: function () {
		// Set style width to table to avoid colgroup width not working 
		// because of width attribute (width="100%") on table 
		if (this._isAllWidths()) {
			var hdtbl = this.eheadtbl,
				bdtbl = this.ebodytbl,
				fttbl = this.efoottbl;
			if (hdtbl) {
				var wd = 0;
				for (var w = this.ehdfaker.firstChild; w; w = w.nextSibling) {
					if (w.style.display != 'none')
						wd += zk.parseInt(w.style.width);
				}
				hdtbl.style.width = wd + 'px';
				if (bdtbl)
					bdtbl.style.width = wd + 'px';
				if (fttbl)
					fttbl.style.width = wd + 'px';
			}
		}
		if (this._nativebar && !this.frozen) {
			var zkb = zk(this.ebody),
				hScroll = zkb.hasHScroll(),
				vScroll = zkb.hasVScroll(),
				hdfakerbar = this.head ? this.head.$n('hdfaker-bar') : null,
				ftfakerbar = this.eftfaker ? this.head.$n('ftfaker-bar') : null;
			if (vScroll) {
				if (hdfakerbar)
					hdfakerbar.style.width = vScroll + 'px';
				if (ftfakerbar)
					ftfakerbar.style.width = vScroll + 'px';
			}
		}
		// Bug in B36-2841185.zul
		if (zk.ie8 && this.isModel() && this.inPagingMold())
			zk(this).redoCSS();
	},
	//return if all widths of columns are fixed (directly or indirectly)
	_isAllWidths: function() {
		if (this.isSizedByContent())
			return true;
		if (!this.head)
			return false;
		var allwidths = true;
		for (var w = this.head.firstChild; w; w = w.nextSibling) {
			if (allwidths 
					&& (w._width === undefined || w._width.indexOf('px') <= 0) 
					&& (w._hflex != 'min' || w._hflexsz === undefined) 
					&& w.isVisible()) {
				allwidths = false;
				break;
			}
		}
		return allwidths;
	},
	domFaker_: function (out, fakeId) { //used by redraw
		var head = this.head;
		out.push('<colgroup id="', head.uuid, fakeId, '">');
		for (var w = head.firstChild; w; w = w.nextSibling) {
			var wd = w._hflexWidth ? w._hflexWidth + 'px' : w.getWidth(),
				visible = !w.isVisible() ? 'display:none;' : '';
			// B70-ZK-2036: Style width should end with 'px'.
			wd = wd ? 'width: ' + wd + ';' : '';
			out.push('<col id="', w.uuid, fakeId, '" style="', wd, visible, '"/>');
		}
		if (this._nativebar && !this.frozen && (fakeId.indexOf('hd') > 0 || fakeId.indexOf('ft') > 0))
			out.push('<col id="', head.uuid, fakeId, '-bar" style="width: 0px" />');
		out.push('</colgroup>');
	},

	//super//
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);

		if (child.$instanceof(this.getHeadWidgetClass())) {
			this.head = child;
			this._minWd = null;
		} else if (!child.$instanceof(zul.mesh.Auxhead))
			return;

		var nsib = child.nextSibling;
		if (nsib)
			for (var hds = this.heads, j = 0, len = hds.length; j < len; ++j)
				if (hds[j] == nsib) {
					hds.splice(j, 0, child);
					return; //done
				}
		this.heads.push(child);
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);

		if (child == this.head) {
			this._minWd = this.head = null;
			this.heads.$remove(child);
		} else if (child.$instanceof(zul.mesh.Auxhead))
			this.heads.$remove(child);
		else if (child.$instanceof(zul.mesh.Frozen))
			this.efrozen = null;
	},
	//bug# 3022669: listbox hflex="min" sizedByContent="true" not work
	beforeMinFlex_: function (orient) {
		if (this._hflexsz === undefined && orient == 'w' && this._width === undefined) {
			if (this.isSizedByContent())
				this._calcSize();
			if (this.head) {
				this._fixHeaders(true);/* B50-3315594.zul */
				for(var w = this.head.firstChild; w; w = w.nextSibling)
					if (w._hflex == 'min' && w.hflexsz === undefined) //header hflex="min" not done yet!
						return null;
			}
			_fixBodyMinWd(this); // sized by content without header
			return _getMinWd(this); //grid.invalidate() with hflex="min" must maintain the width
		}
		return null;
	},
	// fixed for B50-3315594.zul
	beforeParentMinFlex_: function (orient) {
		if (orient == 'w') {
			if (this.isSizedByContent())
				this._calcSize();
			if (this.head)
				this._fixHeaders();
		} else
			this._calcSize();
	},
	clearCachedSize_: function() {
		this.$supers('clearCachedSize_', arguments);
		this._clearCachedSize();

		var tr;
		if (!this.ebdfaker && (tr = _getSigRow(this))) { //empty head case
			for (var cells = tr.cells, i = cells.length; i--;)
				cells[i].style.width = '';
		}
		var head = this.getHeadWidget();
		if (head) {
			for (var w = head.firstChild, wn; w; w = w.nextSibling)
				delete w._hflexsz;
		}
	},
	_clearCachedSize: function() {
		var n;
		if (n = this.$n())
			n._lastsz = this._minWd = null;
	},
	_calcMinWds: function () { //used in HeaderWidgets
		if (!this._minWd)
			this._minWd = _calcMinWd(this);
		return this._minWd;
	},
	_adjSpanWd: function () { //used in HeadWidgets
		if (!this._isAllWidths() || !this.isSpan())
			return;
		var hdfaker = this.ehdfaker,
			bdfaker = this.ebdfaker,
			ftfaker = this.eftfaker;
		if (!hdfaker || !bdfaker)
			return;

		var head = this.head.$n();
		if (!head) 
			return;
		this._calcMinWds();
		var wd,
			wds = [],
			width = 0,
			hdcol = hdfaker.firstChild,
			_minwds = this._minWd.wds,
			hdlen = this.head.nChildren;
		
		for (var w = this.head.firstChild, i = 0; w; w = w.nextSibling, i++) {
			if (zk(hdcol).isVisible()) {
				var wdh = w._width;
				
				if (w._hflex == 'min')
					wd = wds[i] = _minwds[i];
				else if (wdh && wdh.endsWith('px'))
					wd = wds[i] = zk.parseInt(wdh);
				else
					wd = wds[i] = zk.parseInt(hdcol.style.width);
				
				width += wd;
			}
			hdcol = hdcol.nextSibling;
		}
		
		var	hdcol = hdfaker.firstChild,
			bdcol = bdfaker.firstChild,
			ftcol = ftfaker ? ftfaker.firstChild : null,
			total = this.ebody.clientWidth,
			extSum = total - width,
			count = total,
			visj = -1;
		
		if (this._nspan < 0) { //span to all columns
			for (var i = 0; hdcol && i < hdlen; hdcol = hdcol.nextSibling, i++) {
				if (!zk(hdcol).isVisible()) {
					bdcol = bdcol.nextSibling;
					if (ftcol)
						ftcol = ftcol.nextSibling;
					continue;
				} else {
					wds[i] = wd = extSum <= 0 ? wds[i] : (((wds[i] * total / width) + 0.5) || 0);
					var stylew = jq.px0(wd);
					count -= wd;
					visj = i;
					
					hdcol.style.width = stylew;
					bdcol.style.width = stylew;
					bdcol = bdcol.nextSibling;
					
					if (ftcol) {
						ftcol.style.width = stylew;
						ftcol = ftcol.nextSibling;
					}
				}
			}
			//compensate calc error
			if (extSum > 0 && count != 0 && visj >= 0) {
				wd = wds[visj] + count;
				var stylew = jq.px0(wd);
				
				bdfaker.childNodes[visj].style.width = stylew;
				hdfaker.childNodes[visj].style.width = stylew;
				
				if (ftfaker)
					ftfaker.childNodes[visj].style.width = stylew;
			}
		} else { //feature#3184415: span to a specific column
			visj = this._nspan - 1;
			for (var i = 0; hdcol && i < hdlen; hdcol = hdcol.nextSibling, i++) {
				if (!zk(hdcol).isVisible()) {
					bdcol = bdcol.nextSibling;
					if (ftcol)
						ftcol = ftcol.nextSibling;
					continue;
				} else {
					wd = visj == i && extSum > 0 ? (wds[visj] + extSum) : wds[i];
					var stylew = jq.px0(wd);
					hdcol.style.width = stylew;
					bdcol.style.width = stylew;
					bdcol = bdcol.nextSibling;
					if (ftcol) {
						ftcol.style.width = stylew;
						ftcol = ftcol.nextSibling;
					}
				}
			}
		}
		//bug 3188738: Opera only. Grid/Listbox/Tree span="x" not working
		if (zk.opera)
			zk(this.$n()).redoCSS();
	},
	_adjHeadWd: function () {
		var hdfaker = this.ehdfaker,
			bdfaker = this.ebdfaker,
			ftfaker = this.eftfaker;
		
		if (!hdfaker || !bdfaker || !this.getBodyWidgetIterator().hasNext())
			return;
		
		var hdtable = this.eheadtbl,
			head = this.head.$n();
		
		if (!head)
			return;
		
		// Bug #1886788 the size of these table must be specified a fixed size.
		var ebody = this.ebody,
			bdtable = this.ebodytbl,
			bdwd = ebody.offsetWidth,
			total = Math.max(hdtable.offsetWidth, bdtable.offsetWidth),
			tblwd = Math.min(bdwd, bdtable.offsetWidth);
		
		if (total == bdwd && bdwd > tblwd && bdwd - tblwd < 20)
			total = tblwd;
		
		var minWd = this._calcMinWds(),
			wds = minWd.wds,
			width = minWd.width,
			hdcol = hdfaker.firstChild,
			bdcol = bdfaker.firstChild,
			ftcol = ftfaker ? ftfaker.firstChild : null,
			hwgt = this.head.firstChild;
		
		for (var i = 0; hwgt; hwgt = hwgt.nextSibling, i++) {
			// sizedByContent shall not override column width
			if (hwgt._width || wds[i] == 0) {
				if (wds[i] == 0) {
					hdcol.style.width = zk.chrome ? '0.1px' : '0px';
					bdcol.style.width = '0px';
					if (ftcol)
						ftcol.style.width = '0px';
				}
				hdcol = hdcol.nextSibling;
				bdcol = bdcol.nextSibling;
				if (ftcol)
					ftcol = ftcol.nextSibling;
			} else {
				var wd = jq.px(wds[i]);
				hdcol.style.width = bdcol.style.width = wd;
				hdcol = hdcol.nextSibling;
				bdcol = bdcol.nextSibling;
				if (ftcol) {
					ftcol.style.width = wd;
					ftcol = ftcol.nextSibling;
				}
			}
		}
		
		hdtable.style.width = jq.px(width);
		bdtable.style.width = jq.px(width);
		if (ftfaker)
			this.efoottbl.style.width = jq.px(width);
		
		_adjMinWd(this);
	},
	_getFirstRowCells: function (tbody) {
		if (tbody && tbody.rows && tbody.rows.length) {
			var cells = tbody.rows[0].cells,
				length = cells.length,
				ncols = 0;
			for (var i = 0; i < length; i++) {
				var span = cells[i].colSpan;
				ncols += span != 1 ? span : 1;
			}
			if (ncols == length)
				return cells;
			else {
				var out = [];
				out.push('<tr id="', tbody.id,
						'-fakeRow" style="visibility:hidden;height:0">');
				for (var i = 0; i < ncols; i++)
					out.push('<td></td>');
				out.push('</tr>');
				jq(tbody.rows[0]).before(out.join(''));
				out = null;
				return tbody.rows[0].cells;
			}
		}
	},
	_deleteFakeRow: function (tbody) {
		if (tbody)
			jq('#' + tbody.id + '-fakeRow').remove();
	}
});
/** @class zul.mesh.Scrollbar
 * @import zk.Widget
 * The extra Scrollbar for the MeshWidget.
 * @since 6.5.0
 */
zul.mesh.Scrollbar = {
	/**
	 * Initialize the scrollbar of MeshWidget.
	 * @param zk.Widget wgt a widget
	 */
	init: function (wgt) {
		var embed = jq(wgt.$n()).data('embedscrollbar'),
			frozen = wgt.frozen,
			startPositionX = 0;
		
		if (frozen) {
			var columns = frozen.getColumns();
			if (wgt.eheadtbl) {
				var cells = wgt._getFirstRowCells(wgt.eheadrows);
				if (cells) {
					for (var i = 0; i < columns; i++)
						startPositionX += cells[i].offsetWidth;
				}
				wgt._deleteFakeRow(wgt.eheadrows);
			}
		}
		var scrollbar = new zul.Scrollbar(wgt.ebody, wgt.ebodytbl, {
			embed: embed,
			startPositionX: startPositionX,
			onSyncPosition: function() {
				var pos = this.getCurrentPosition(),
					head = wgt.ehead,
					foot = wgt.efoot;
				if (pos && this.hasHScroll()) {
					if (head)
						head.scrollLeft = pos.x;
					if (foot)
						foot.scrollLeft = pos.x;
				}
			},
			onScrollEnd: function() {
				wgt._doScroll();
			}
		});
		return scrollbar;
	},
	/**
	 * Return the vertical scroll position of the body element of given MeshWidget.
	 * @param zk.Widget wgt the widget
	 * @return int
	 */
	getScrollPosV: function (wgt) {
		var bar = wgt._scrollbar;
		if (bar)
			return bar.getCurrentPosition().y;
		
		return wgt.ebody.scrollTop;
	},
	/**
	 * Return the horizontal scroll position of the body element of given MeshWidget.
	 * @param zk.Widget wgt the widget
	 * @return int
	 * @since 7.0.0
	 */
	getScrollPosH: function (wgt) {
		var bar = wgt._scrollbar;
		if (bar)
			return bar.getCurrentPosition().x;
		
		return wgt.ebody.scrollLeft;
	}
};
})();