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
	
	function _setFakerWd(i, wd, hdfaker, bdfaker, ftfaker, headn) {
		bdfaker.cells[i].style.width = zk(bdfaker.cells[i]).revisedWidth(wd) + "px";
		hdfaker.cells[i].style.width = bdfaker.cells[i].style.width;
		if (ftfaker) ftfaker.cells[i].style.width = bdfaker.cells[i].style.width;
		if (headn) {
			var cpwd = zk(headn.cells[i]).revisedWidth(zk.parseInt(hdfaker.cells[i].style.width));
			headn.cells[i].style.width = cpwd + "px";
			var cell = headn.cells[i].firstChild;
			cell.style.width = zk(cell).revisedWidth(cpwd) + "px";
		}
	}
	function _calcMinWd(wgt) {
		var wgtn = wgt.$n(),
			ws = wgtn ? wgtn.style.whiteSpace : ""; //bug#3106514: sizedByContent with not visible columns
		if (wgtn) {
			if (zk.ie8_ || zk.ie9)
				wgt._wsbak = ws; // B50-ZK-432
			if (zk.ie < 8)
				jq(wgtn).addClass('z-word-nowrap'); // B50-ZK-333
			else
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
			fakerflex = head ? head.$n('hdfakerflex') : null,
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
			for (var i = hdfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
				var hdfakercell = hdfaker.cells[i],
					headcell = headn.cells[i],
					headcave = headcell.firstChild;
				hdfakerws[i] = hdfakercell.style.width;
				hdfakercell.style.width = '';
				hdws[i] = headcell.style.width;
				headcell.style.width = '';
				hdcavews[i] = headcave.style.width;
				headcave.style.width = '';
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
			if (ftfaker)
				for (var i = ftfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
					var ftcell = ftfaker.cells[i];
					ftfakerws[i] = ftcell.style.width;
					ftcell.style.width = '';
				}
		}
		if (wgt.ebodytbl) {//clear and backup body faker widths
			wgt.ebody.style.width = '';
			ebodytblw = wgt.ebodytbl.width;
			wgt.ebodytbl.width = '';
			wgt.ebodytbl.style.width = '';
			ebodytblfix = wgt.ebodytbl.style.tableLayout;
			wgt.ebodytbl.style.tableLayout = '';
			if (bdfaker)
				for (var i = bdfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
					var bdcell = bdfaker.cells[i];
					bdfakerws[i] = bdcell.style.width;
					bdcell.style.width = '';
				}
		}

		//calculate widths
		var wds = [],
			width = 0,
			w = head ? head = head.lastChild : null,
			headWgt = wgt.getHeadWidget(),
			max = 0, maxj;
		if (bdfaker && w) {
			for (var i = bdfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
				var wd = bdfaker.cells[i].offsetWidth,
					$cv = zk(w.$n('cave')),
					hdwd = w && w.isVisible() ? ($cv.textSize()[0] + $cv.padBorderWidth() + zk(w.$n()).padBorderWidth()) : 0,
					ftwd = ftfaker && zk(ftfaker.cells[i]).isVisible() ? ftfaker.cells[i].offsetWidth : 0,
					header;
				if ((header = headWgt.getChildAt(i)) && header.getWidth())
					hdwd = Math.max(hdwd, hdfaker.cells[i].offsetWidth);
				if (hdwd > wd) wd = hdwd;
				if (ftwd > wd) wd = ftwd;
				wds[i] = wd;
				if (zk.ie < 8 && max < wd) {
					max = wd;
					maxj = i;
				} else if (zk.ff > 4 || zk.ie >= 9) {// firefox4 & IE9 & IE10 still cause break line in case B50-3147926 column 1
					++wds[i];
				}
				if (zk.ie < 8) // B50-ZK-206
					wds[i] += 2;
				width += wds[i]; // using wds[i] instead of wd for B50-3183172.zul
				if (w) w = w.previousSibling;
			}
			/* Fixed for B50-2979776.zul
			 * if (zk.ie < 8) //*Tricky. ie6/ie7 strange behavior, will generate horizontal scrollbar, minus one to avoid it! 
				--wds[maxj];*/
		} else {
			var tr;
			if (tr = _getSigRow(wgt)) {
				for (var cells = tr.cells, i = cells.length; i--;) {
					var wd = cells[i].offsetWidth;
					wds[i] = wd;
					if (zk.ie < 8 && max < wd) {
						max = wd;
						maxj = i;
					} else if (zk.ff > 4 || zk.ie >= 9) // firefox4 & IE9 & IE10 still cause break line in case B50-3147926 column 1
						++wds[i];
					if (zk.ie < 8) // B50-ZK-206
						wds[i] += 2;
					width += wds[i]; // using wds[i] instead of wd for B50-3183172.zul
				}
			}
		}

		if (wgt.eheadtbl && headn) {//restore headers widths
			wgt.eheadtbl.width = eheadtblw||'';
			wgt.eheadtbl.style.tableLayout = eheadtblfix||'';
			for (var i = hdfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
				hdfaker.cells[i].style.width = hdfakerws[i];
				var headcell = headn.cells[i],
					headcave = headcell.firstChild;
				headcell.style.width = hdws[i];
				headcave.style.width = hdcavews[i];
			}
		}
		if (wgt.efoottbl) {//restore footers widths
			wgt.efoottbl.width = efoottblw||'';
			wgt.efoottbl.style.tableLayout = efoottblfix||'';
			if (ftfaker)
				for (var i = ftfaker.cells.length - (fakerflex ? 1 : 0); i--;)
					ftfaker.cells[i].style.width = ftfakerws[i];
		}
		if (wgt.ebodytbl) {//restore body fakers widths
			wgt.ebodytbl.width = ebodytblw||'';
			wgt.ebodytbl.style.tableLayout = ebodytblfix||'';
			if (bdfaker)
				for (var i = bdfaker.cells.length - (fakerflex ? 1 : 0); i--;)
					bdfaker.cells[i].style.width = bdfakerws[i];
		}

		if (wgtn) {
			if (zk.ie < 8)
				jq(wgtn).removeClass('z-word-nowrap'); // B50-ZK-333
			else if (!(zk.ie8_ || zk.ie9)) // B50-ZK-432: restore later for IE 8
				wgtn.style.whiteSpace = ws;
		}
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
				c.style.width = (zk.safari_ ? wds[i++] : zk(c).revisedWidth(wds[i++])) + "px";
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
	function _syncbodyrows(wgt) {
		var bds = wgt.ebodytbl.tBodies;
		wgt.ebodyrows = wgt.ebodytbl.tBodies[bds.length > 3 ? wgt.ehead ? 2 : 1 : wgt.ehead ? 1 : 0].rows;
		//Note: bodyrows is null in FF if no rows, so no err msg
	}
	function _adjMinWd(wgt) {
		if (wgt._hflex == 'min') {
			var w = _getMinWd(wgt);
			wgt._hflexsz = w + zk(wgt).padBorderWidth(); //override
			wgt.$n().style.width = jq.px0(w);
		}
	}
	function _getMinWd(wgt) {
		wgt._calcMinWds();
		var bdfaker = wgt.ebdfaker,
			hdtable = wgt.eheadtbl,
			bdtable = wgt.ebodytbl,
			wd,
			wds = [],
			width,
			_minwds = wgt._minWd.wds;
		if (wgt.head && bdfaker) {
			width = 0;
			for (var w = wgt.head.firstChild, i = 0; w; w = w.nextSibling) {
				if (zk(bdfaker.cells[i]).isVisible()) {
					wd = wds[i] = w._hflex == 'min' ? _minwds[i] : (w._width && w._width.indexOf('px') > 0) ? 
							zk.parseInt(w._width) : bdfaker.cells[i].offsetWidth;
					width += wd;
				}
				++i;
			}
		} else
			width = wgt._minWd.width; // no header
		return width + (zk.ie < 8 ? 1 : 0);
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
		var dst = wgt.efoot.firstChild.rows[0],
			srcrows = wgt.ebodyrows;
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
			src = document.createElement("TR");
			src.style.height = "0px";
				//Note: we cannot use display="none" (offsetWidth won't be right)
			for (var j = 0; j < maxnc; ++j)
				src.appendChild(document.createElement("TD"));
			srcrows[0].parentNode.appendChild(src);
		}
	
		//we have to clean up first, since, in FF, if dst contains %
		//the copy might not be correct
		for (var j = maxnc; j--;)
			dst.cells[j].style.width = "";
	
		var sum = 0;
		for (var j = maxnc; j--;) {
			var d = dst.cells[j], s = src.cells[j];
			if (zk.opera) {
				sum += s.offsetWidth;
				d.style.width = zk(s).revisedWidth(s.offsetWidth);
			} else {
				d.style.width = s.offsetWidth + "px";
				if (maxnc > 1) { //don't handle single cell case (bug 1729739)
					var v = s.offsetWidth - d.offsetWidth;
					if (v != 0) {
						v += s.offsetWidth;
						if (v < 0) v = 0;
						d.style.width = v + "px";
					}
				}
			}
		}
		if (zk.opera && wgt.isSizedByContent())
			dst.parentNode.parentNode.style.width = sum + "px";
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
	_pagingPosition: "bottom",
	_prehgh: -1,
	_minWd: null, //minimum width for each column
	$init: function () {
		this.$supers('$init', arguments);
		this.heads = [];
	},

	_innerWidth: "100%",
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
			if (v == null) this._innerWidth = v = "100%";
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
		return "paging" == this.getMold();
	},

	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		if (this.desktop) {
			if (zk.ie6_ && this.ebody)
				this.ebody.style.height = height;
			// IE6 cannot shrink its height, we have to specify this.body's height to equal the element's height. 
			this._setHgh(height);
			this.onSize();
		}
	},
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		if (this.eheadtbl) this.eheadtbl.style.width = "";
		if (this.efoottbl) this.efoottbl.style.width = "";
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
		var td, frozen;
		if (this.head && (td = this.head.getChildAt(index).$n()) && parseInt(td.style.width) == 0 && 
			(frozen = zk.Widget.$(this.efrozen.firstChild)) &&
			(index = index - frozen.getColumns()) >= 0) {
			frozen.setStart(index);
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
		if (zk.ie < 8 && this.isVflex()) { // B50-ZK-195
			// added by Jumper for IE to get a correct offsetHeight so we need 
			// to add this command faster than the this._calcSize() function.
			var hgh = this.$n().style.height;
			if (!hgh || hgh == "auto") this.$n().style.height = "99%"; // avoid border 1px;
		}
		this._bindDomNode();
		if (this._hflex != 'min')
			this._fixHeaders();
		// Bug ZK-1284: Scrolling on grid/listbox header could cause column heading/body to misalign 
		if (this.head && this.ehdheaders && zk(this.ehdheaders).isVisible()) {
			this.domListen_(this.ehead, 'onScroll');
		}
		if (this.ebody) {
			this.domListen_(this.ebody, 'onScroll');
			this.ebody.style.overflow = ''; // clear
			if (this.efrozen)
				jq(this.ebody).addClass('z-word-nowrap').css('overflow-x', 'hidden');// keep non line break
		}
		zWatch.listen({onSize: this, beforeSize: this, onResponse: this});
		var paging;
		if (zk.ie7_ && (paging = this.$n('pgib')))
			zk(paging).redoCSS();
	},
	unbind_: function () {
		// Bug ZK-1284: Scrolling on grid/listbox header could cause column heading/body to misalign
		if (this.head && this.ehdheaders && zk(this.ehdheaders).isVisible())
			this.domUnlisten_(this.ehead, 'onScroll');
		
		if (this.ebody)
			this.domUnlisten_(this.ebody, 'onScroll');

		zWatch.unlisten({onSize: this, beforeSize: this, onResponse: this});
		
		this.$supers(zul.mesh.MeshWidget, 'unbind_', arguments);
	},
	clearCache: function () {
		this.$supers('clearCache', arguments);
		this.ebody = this.ehead = this.efoot = this.efrozen = this.ebodytbl
			= this.eheadtbl = this.efoottbl = this.ebodyrows = this.ehdheaders
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
					if (hdsmin && !w._width && !w._nhflex) {
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
		// start of ZK-922/ZK-1024: can remove after FF 13
		var ff = zk.gecko,
			ff_10_12 = ff > 9 && ff < 13,
			ebody = this.ebody, 
			h;
		if (ff_10_12) {
			if (ebody)
				h = ebody.style.height;
			if (h)
				ebody.style.height = '';
		}
		// end of ZK-922/ZK-1024
		var head = this.head;
		if (head) {
			var hdfaker = this.ehdfaker,
				bdfaker = this.ebdfaker,
				ftfaker = this.eftfaker,
				headn = head.$n(),
				i = 0;
			for (var w = head.firstChild, wd; w; w = w.nextSibling) {
				if ((wd = w._hflexWidth) !== undefined)
					_setFakerWd(i, wd, hdfaker, bdfaker, ftfaker, headn);
				++i;
			}
			_adjMinWd(this);
		}
		// start of ZK-922/ZK-1024: can remove after FF 13
		if (ff_10_12 && h)
			ebody.style.height = h;
		// end of ZK-922/ZK-1024
	},
	_bindDomNode: function () { //called by Treecell
		for (var n = this.$n().firstChild; n; n = n.nextSibling)
			switch(n.id) {
			case this.uuid + '-head':
				this.ehead = n;
				this.eheadtbl = jq(n).find('>table:first')[0];
				break;
			case this.uuid + '-body':
				this.ebody = n;
				this.ebodytbl = jq(n).find('>table:first')[0];
				break;
			case this.uuid + '-foot':
				this.efoot = n;
				this.efoottbl = jq(n).find('>table:first')[0];
				break;
			case this.uuid + '-frozen':
				this.efrozen = n;
				break;
			}

		if (this.ebody) {
			//ie7 will auto generate an empty <tbody> which confuse the if statements 
			var bds = this.ebodytbl.tBodies,
				ie7special = zk.ie7_ && bds && bds.length == 1 && !this.ehead && !bds[0].id;
			if (!bds || !bds.length || (this.ehead && bds.length < 2 || ie7special)) {
				if (ie7special) //remove the empty tbody
					jq(bds[0]).remove();
				var out = [];
				if (this.domPad_ && !this.inPagingMold() && this._mold != 'select') this.domPad_(out, '-tpad');
				out.push('<tbody id="',this.uuid,'-rows"/>');
				if (this.domPad_ && !this.inPagingMold() && this._mold != 'select') this.domPad_(out, '-bpad');
				jq(this.ebodytbl ).append(out.join(''));
			}
			_syncbodyrows(this);
		}
		if (this.ehead) {
			this.ehdfaker = this.eheadtbl.tBodies[0].rows[0];
			this.ehdheaders = this.eheadtbl.tBodies[1].rows[0];
			this.ebdfaker = this.ebodytbl.tBodies[0].rows[0];
			if (this.efoottbl)
				this.eftfaker = this.efoottbl.tBodies[0].rows[0];
		}
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
			_syncbodyrows(this);
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
		//see onSize. Chrome/Safari can calc scrollbar size wrong when sizing. 
		//must display:none then restore to make it recalc, but it also cause scrolling. 
		//ignore it here to keep the _currentTop/_currentLeft intact!
		if (zk.safari && this._ignoreDoScroll) 
			return;
		
		var ehead = this.ehead,
			ehdheaders = this.ehdheaders,
			ebody = this.ebody,
			efoot = this.efoot;
		
		
		// Bug ZK-1284: Scrolling on grid/listbox header could cause column heading/body to misalign
		if (ehead && zk(ehead).isVisible() && ehdheaders && zk(ehdheaders).isVisible() && //Bug ZK-1649: should check if ehead is visible or not
				!(this.fire('onScroll', ehead.scrollLeft).stopped)) {
			if (this._currentLeft != ehead.scrollLeft) {
				if (ebody)
					ebody.scrollLeft = ehead.scrollLeft;
				if (efoot) 
					efoot.scrollLeft = ehead.scrollLeft;
			}
			
		}
		
		if (!(this.fire('onScroll', ebody.scrollLeft).stopped)) {
			if (this._currentLeft != ebody.scrollLeft) { //care about horizontal scrolling only
				if (ehead) {
					ehead.scrollLeft = ebody.scrollLeft;
					//bug# 3039339: Column is not aligned in some special combination of dimension
					var diff = ebody.scrollLeft - ehead.scrollLeft;
					var hdflex = jq(ehead).find('table>tbody>tr>th:last-child')[0];
					if (diff) { //use the hdfakerflex to compensate
						hdflex.style.width = (hdflex.offsetWidth + diff) + 'px';
						ehead.scrollLeft = ebody.scrollLeft;
					} else if (parseInt(hdflex.style.width) != 0 && ebody.scrollLeft == 0) {
						hdflex.style.width = '';
					}
				}
				if (efoot) 
					efoot.scrollLeft = ebody.scrollLeft;
			}
		}
		
		var t = zul.mesh.Scrollbar.getScrollPosV(this),
			l = ebody.scrollLeft,
			scrolled = (t != this._currentTop || l != this._currentLeft);
		if (scrolled && 
				// Bug ZK-353 ignore in rod
				!this._listbox$rod && !this._grid$rod) {
			this._currentTop = t; 
		}
		
		if (scrolled) {
			// always sync for B30-1737660.zul
			this._currentLeft = l;
		}
		
		if (!this.paging && !this._paginal)
			this.fireOnRender(zk.gecko ? 200 : 60);

		if (scrolled)
			this._fireOnScrollPos();
	},
	_timeoutId: null,
	_fireOnScrollPos: function (time) { //overriden in zkmax
		clearTimeout(this._timeoutId);
		//IE6 caused issue if the time too short, test case  http://www.zkoss.org/zksandbox/#g8
		this._timeoutId = setTimeout(this.proxy(this._onScrollPos), time >= 0 ? time :  (zk.gecko || zk.ie6_) ? 200 : 60);
	},
	_onScrollPos: function () {
		// Bug ZK-414
		if (this.ebody) {
			this._currentTop = zul.mesh.Scrollbar.getScrollPosV(this);
			this._currentLeft = this.ebody.scrollLeft;
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

		var rows = this.ebodyrows;
		if (this.inPagingMold() && this._autopaging && rows && rows.length)
			if (_fixPageSize(this, rows))
				return; //need to reload with new page size
		
		if ((zk.ie8_ || zk.ie9) && (this._wsbak !== undefined)) { // B50-ZK-432
			this.$n().style.whiteSpace = this._wsbak;
			delete this._wsbak;
		}
		if (zk.ie < 8)
			this._syncBodyHeight(); // B50-ZK-171
		
		
		//ZK-926 IE7 scrollbar not working after zoom or classname is changed in redoCSS.
		//       Since ZK-335 is not reproducible in ZK 5.0.11 and it's causing bigger side effect,so we comment this first. 
//		if (zk.ie7_)
//			zk(this.ebody).redoCSS(); // B50-ZK-335: Grid, Tree may have extra horizonal scroll bar
		
		if (!this.desktop || !this._model || !rows || !rows.length) return;

		//Note: we have to calculate from top to bottom because each row's
		//height might diff (due to different content)
		var items = [],
			min = zul.mesh.Scrollbar.getScrollPosV(this), max = min + this.ebody.offsetHeight;
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
		if (items.length)
			this.fire('onRender', {items: items}, {implicit:true});
	},
	_syncBodyHeight: function () { //called only if ie6/7 (overriden in SelectWidget)
		var ebody = this.ebody,
			ebodytbl = this.ebodytbl;

		// fixed B50-3175465.zul for IE6 if this.desktop is null
		if (!this.desktop || this._height || (this._vflex && this._vflex != 'min'))
			return; // height is predetermined, skip sync
		
		// fixed for B30-1919180.zul and B30-1822564.zul,
		//  and a side effect for B50-3188023.zul 	
		if (ebody.style.height == "0px")
			ebody.style.height = '';
		// no scroll bar, but extra height on ebody
		if (ebody.offsetHeight - ebodytbl.offsetHeight > 11 &&
				ebody.offsetWidth >= ebodytbl.offsetWidth) 
			ebody.style.height = (ebodytbl.offsetHeight) + 'px';
		zjq.fixOnResize(0); // defer detection of doc resize
	},
	//derive must override
	//getHeadWidgetClass
	//getBodyWidgetIterator

	//watch//
	beforeSize: function () {
		// IE6 needs to reset the width of each sub node if the width is a percentage
		var wd = zk.ie6_ ? this.getWidth() : this.$n().style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			var n = this.$n();
			
			if (!zk.ie6_ && n._lastsz && n._lastsz.height == n.offsetHeight && n._lastsz.width == n.offsetWidth)
				return; //do nothing.
				
			if (this.ebody) 
				this.ebody.style.width = "";
			if (this.ehead) 
				this.ehead.style.width = "";
			if (this.efoot) 
				this.efoot.style.width = "";
		
			// Bug 3310051
			if (zk.ie6_ && this._hflex && this._hflex != 'min') {
				n.style.width = '';
			}
			n._lastsz = null;// Bug #3013683: ie6 will do onSize twice
		}
		
		// Bug 2896474
		if (zk.ie6_ && this._vflex && this._vflex != 'min') {
			var hgh = this.getHeight();
			if (!hgh || hgh == "auto" || hgh.indexOf('%') >= 0) {
				var n = this.$n();
				n.style.height = '';
				if (this.ebody) 
					this.ebody.style.height = "";
				n._lastsz = null;
			}
		}
		
	},
	onSize: function () {
		if (this.isRealVisible()) { // sometimes the caller is not zWatch
			var n = this.$n();
			if (n._lastsz && n._lastsz.height == n.offsetHeight && n._lastsz.width == n.offsetWidth) {
				this.fireOnRender(155); // force to render while using live grouping
				return; // unchanged
			}
			
			this._calcSize();// Bug #1813722
			
			this.fireOnRender(155);
			
			// Bug ZK-355
			if (this.ebody.scrollHeight >= this._currentTop) {
				var ebody = this.ebody, cave = this.ebodytbl;
				if (zk.mobile) // attribute scrollTop does not working on mobile 
					jq(cave).offset().top = jq(ebody).offset().top - this._currentTop;
				else
					ebody.scrollTop = this._currentTop;
			}
				
			if (this.ebody.scrollWidth >= this._currentLeft) {
				this.ebody.scrollLeft = this._currentLeft;
				if (this.ehead) 
					this.ehead.scrollLeft = this._currentLeft;
				if (this.efoot) 
					this.efoot.scrollLeft = this._currentLeft;
			}
			this._shallSize = false;
		}
	},

	_vflexSize: function (hgh) {
		var n = this.$n();
		if (zk.ie6_) { 
			// ie6 must reset the height of the element,
			// otherwise its offsetHeight might be wrong.
			n.style.height = "";
			n.style.height = hgh;
		}
		
		var pgHgh = 0
		if (this.paging) {
			var pgit = this.$n('pgit'),
				pgib = this.$n('pgib');
			if (pgit) pgHgh += pgit.offsetHeight;
			if (pgib) pgHgh += pgib.offsetHeight;
		}
		return zk(n).revisedHeight(n.offsetHeight) - (this.ehead ? this.ehead.offsetHeight : 0)
			- (this.efoot ? this.efoot.offsetHeight : 0)
			- (this.efrozen ? this.efrozen.offsetHeight : 0)
			- pgHgh; // Bug #1815882 and Bug #1835369
	},
	setFlexSize_: function(sz) {
		var n = this.$n(),
			head = this.$n('head');
		if (sz.height !== undefined) {
			if (sz.height == 'auto') {
				n.style.height = '';
				if (head) head.style.height = '';
			} else {
				if (zk.ie < 8 && this._vflex == 'min' && this._vflexsz === undefined)
					sz.height += 1;
				return this.$supers('setFlexSize_', arguments);
			}
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto') {
				if (this._hflex != 'min') n.style.width = '';
				if (head) head.style.width = '';
			} else {
				if (zk.ie < 8 && head && this._hflex == 'min' && this._hflexsz === undefined)
					sz.width += 1;
				return this.$supers('setFlexSize_', arguments);
			}
		}
		return {height: n.offsetHeight, width: n.offsetWidth};
	},
	/* set the height. */
	_setHgh: function (hgh) {
		var ebody = this.ebody,
			ebodyStyle = ebody.style;
		if (this.isVflex() || (hgh && hgh != "auto" && hgh.indexOf('%') < 0)) {
			if (zk.safari // Bug ZK-417, ignore to set the same size
			&& ebodyStyle.height == jq.px(this._vflexSize(hgh)))
				return;

			ebodyStyle.height = ''; //allow browser adjusting to default size
			var h = this._vflexSize(hgh); 
			if (h < 0) h = 0;

			if (!zk.ie || zk.ie8 || this._vflex != "min")
				ebodyStyle.height = h + "px";
			//2007/12/20 We don't need to invoke the body.offsetHeight to avoid a performance issue for FF. 
			if (zk.ie && ebody.offsetHeight) // bug #1812001.
				;
			// note: we have to invoke the body.offestHeight to resolve the scrollbar disappearing in IE6 
			// and IE7 at initializing phase.
		} else {
			//Bug 1556099
			var n = this.$n();
			ebodyStyle.height = "";
			n.style.height = hgh;
			// B50-ZK-599: Grid has no vertical scrollbar when height is set by percentage
			// have to assign a height to ebody or it will sized by content automatically.
			if (hgh && hgh.indexOf('%') > 0) {
				var h = this._vflexSize(n.offsetHeight + 'px'); 
				if (h < 0) h = 0;
				if (!zk.ie || zk.ie8 || this._vflex != "min")
					ebodyStyle.height = h + "px";
				if (zk.ie && ebody.offsetHeight) // bug #1812001
					;
			}
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
		//FF and IE: sometime a horizontal scrollbar appear (though it shalln't)
		//note: we don't solve this bug for paging yet
		var n = this.$n(),
			wd = n.style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			wd = zk(n).revisedWidth(n.offsetWidth);
			if (wd) 
				wd += "px";
		}
		if (wd) {
			this.ebody.style.width = wd;
			if (this.ehead) 
				this.ehead.style.width = wd;
			if (this.efoot) 
				this.efoot.style.width = wd;
		}
		
		if (zk.ie7_ && this.ebodytbl) { // B50-ZK-335: re-sync ebodytbl width
			var s = this.ebodytbl.style,
				sw = s.width;
			if (!sw) {
				s.width = '100%';
				s.width = sw;
			}
		}
		//Bug 1659601: we cannot do it in init(); or, IE failed!
		var tblwd = this._getEbodyWd(),
			hgh = this.getHeight() || n.style.height, // bug in B36-2841185.zul
			sizedByContent = this.isSizedByContent();
		if (zk.ie) {//By experimental: see zk-blog.txt
			if (this.eheadtbl && this.eheadtbl.offsetWidth != this.ebodytbl.offsetWidth) 
				this.ebodytbl.style.width = ""; //reset
				 
			if (tblwd && 
					// fixed column's sizing issue in B30-1895907.zul
					(!this.eheadtbl || !this.ebodytbl || !this.eheadtbl.style.width ||
					this.eheadtbl.style.width != this.ebodytbl.style.width
					|| this.ebody.offsetWidth == this.ebodytbl.offsetWidth) &&
					// end of the fixed
					this.ebody.offsetWidth - tblwd > 11) { //scrollbar
				this.ebodytbl.style.width = jq.px0(--tblwd);
			}
			// bug #2799258 and #1599788
			if (!zk.ie8 && !this.isVflex() && (!hgh || hgh == "auto") && !this._ignoreHghExt()) {
				if (zk(this.ebody).hasVScroll()) //v-scrollbar 
					this.ebody.style.height = jq.px0(this.ebodytbl.offsetHeight); //extend body height to remove the v-scrollbar
				// resync
				tblwd = this.ebody.clientWidth;
			}
		}
		if (this.ehead) {
			if (tblwd) 
				this.ehead.style.width = tblwd + 'px';
			if (sizedByContent && this.ebodyrows && this.ebodyrows.length)
				this._adjHeadWd();
			else if (tblwd && this.efoot) 
				this.efoot.style.width = tblwd + 'px';
		} else if (this.efoot) {
			if (tblwd) this.efoot.style.width = tblwd + 'px';
			if (this.efoottbl.rows.length && this.ebodyrows && this.ebodyrows.length)
				_cpCellWd(this);
		}
		
		//check if need to span width
		this._adjSpanWd();
		// no header case
		_fixBodyMinWd(this, true);
		
		//bug# 3022669: listbox hflex="min" sizedByContent="true" not work
		if (this._hflexsz === undefined && this._hflex == 'min' && 
				this._width === undefined && n.offsetWidth > this.ebodytbl.offsetWidth) {
			n.style.width = this.ebodytbl.offsetWidth + 'px';
			this._hflexsz = n.offsetWidth;
		}
		// B50-ZK-543, B50-ZK-773
		// should re-calculate height because
		// the string height maybe changed after width changed.
		if (sizedByContent && this.getRows && this.getRows() > 1 && (typeof this._calcHgh == "function")
				&& this.ebody.style.height) // check only if height exists for F50-3000339.zul
			this._calcHgh(); // recalculate height again ZK-796
		
		n._lastsz = {height: n.offsetHeight, width: n.offsetWidth}; // cache for the dirty resizing.
		
		this._afterCalcSize();
	},
	_getEbodyWd: function () {
		return this.ebody.clientWidth;
	},
	_beforeCalcSize: function () {
		this._setHgh(this.$n().style.height);
	},
	_afterCalcSize: function () {
		// Bug in B36-2841185.zul
		if (zk.ie8 && this.isModel() && this.inPagingMold())
			zk(this).redoCSS();
		
		this._removeHorScrollbar();
		//bug#3186596: unwanted v-scrollbar
		this._removeScrollbar();
	},
	_removeHorScrollbar: (zk.ie == 8) ? function () {
		var h = this._height;
		if (!this._vflex && (!h || h == 'auto') && !this._rows 
			&& this.ebody.offsetWidth >= this.ebodytbl.offsetWidth) {
			
			var ebodyhghbak = this.ebody.style.height,
				wgt = this;
			this.ebody.style.height = this.ebodytbl.offsetHeight + 'px';
			setTimeout(function () {
				// 20120216 TonyQ: Bug fux for paging will disappear in B35-2096807.zul.
				// It's happening when setVflex in borderlayout , onSize is trigger earlier before setvflex.
				// We set the body height in async way and it's invoked too late,
				// so we just ignore the restoring actiion when vflex is set.
				// Here we assume if dom set a vflex after _removeHorScrollbar but before the restoring action, 
				// it's already be handled the height in vflex.
				if(!wgt._vflex) 
					wgt.ebody.style.height = ebodyhghbak; 
			}, 0);
				
		}
	} : zk.$void,
	_removeScrollbar: zk.ie ? function() { //see HeadWidget#afterChildrenFlex_
		if (this._vflex) return;
		
		var hgh = this.getHeight() || this.$n().style.height || (this.getRows && this.getRows()); // bug in B36-2841185.zul
		if (!hgh || hgh == "auto") {
			var ebody = this.ebody,
				ebodytbl = this.ebodytbl;
			if(zk.ie < 8) { 
				var $ebody;
				if (($ebody=zk(ebody)).hasVScroll()) { //v-scroll, expand body height to remove v-scroll
					ebody.style.height = jq.px0(ebodytbl.offsetHeight);
					if ($ebody.hasVScroll()) //still v-scroll, expand body height for extra h-scroll space to remove v-scroll 
						ebody.style.height = jq.px0(ebodytbl.offsetHeight+jq.scrollbarWidth());
				}
			} else if (!this.efrozen) {
				//IE8 sometimes will fail to show the h-scrollbar; enforce it!
				//IE9: Bug ZK-238
				ebody.style.overflowX = 
					ebodytbl.offsetWidth > ebody.offsetWidth ?
					'scroll': '';
			}
		}
	}: zk.$void,
	//return if all widths of columns are fixed (directly or indirectly)
	_isAllWidths: function() {
		if (this.isSizedByContent())
			return true;
		if (!this.head)
			return false;
		var allwidths = true;
		for (var w = this.head.firstChild; w; w = w.nextSibling) {
			if (allwidths && (w._width === undefined || w._width.indexOf('px') <= 0) && (w._hflex != 'min' || w._hflexsz === undefined) && w.isVisible()) {
				allwidths = false;
				break;
			}
		}
		return allwidths;
	},	
	domFaker_: function (out, fakeId, zcls) { //used by mold
		var head = this.head;
		out.push('<tbody style="visibility:hidden;height:0px"><tr id="',
				head.uuid, fakeId, '" class="', zcls, '-faker">');
		var allwidths = true,
			// IE6/7 bug in F30-1904532.zul
			totalWidth = 0, shallCheckSize = zk.ie < 8;
		
		for (var w = head.firstChild; w; w = w.nextSibling) {
			out.push('<th id="', w.uuid, fakeId, '"', w.domAttrs_(),
				 	'><div style="overflow:hidden"></div></th>');
			if (allwidths && w._width === undefined && w._hflex === undefined && w.isVisible()) {
				allwidths = false;
				shallCheckSize = false;
			} else if (shallCheckSize) {
				var width = w._width;
				if (width && width.indexOf('px') != -1)
					totalWidth += zk.parseInt(width);
				else shallCheckSize = false;
			}
		}
		
		if (shallCheckSize) {
			var w = this._width;
			if (w && w.indexOf('px') != -1)
				allwidths = zk.parseInt(w) != totalWidth;
		}
		
		//feature #3025419: flex column to compensate widget width and summation of column widths
		out.push('<th id="', head.uuid, fakeId, 'flex"', 
				(allwidths || this.isSizedByContent() ? '' : ' style="width:0px"'), '></th></tr></tbody>');
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
				this._fixHeaders(true/* B50-3315594.zul */);
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
		if (!hdfaker || !bdfaker || !hdfaker.cells.length
		|| !bdfaker.cells.length)
			return;
		
		var head = this.head.$n();
		if (!head) return; 
		this._calcMinWds();
		var hdtable = this.eheadtbl,
			bdtable = this.ebodytbl,
			wd,
			wds = [],
			width = 0,
			fakerflex = this.head.$n('hdfakerflex'),
			hdfakervisible = zk(hdfaker).isRealVisible(true),
			_minwds = this._minWd.wds;
		for (var w = this.head.firstChild, i = 0; w; w = w.nextSibling) {
			if (zk(hdfaker.cells[i]).isVisible()) {
				wd = wds[i] = w._hflex == 'min' ? _minwds[i] : (w._width && w._width.indexOf('px') > 0) ? 
						zk.parseInt(w._width) : hdfakervisible ? hdfaker.cells[i].offsetWidth : bdfaker.cells[i].offsetWidth;
				width += wd;
			}
			++i;
		}
		//ie6/ie7 leave a vertical scrollbar space, use offsetWidth if not setting height
		// B50-ZK-1038: in IE, when body has 0 height, it has 0 client width
		var useOffset = zk.ie && (bdtable.parentNode.offsetHeight == 0 || 
			(zk.ie < 8 && !this.getHeight() && !this.$n().style.height));
		//Tricky. ie6/ie7 strange behavior, will generate horizontal scrollbar, minus one to avoid it!
		var	total = bdtable.parentNode[useOffset ? 'offsetWidth' : 'clientWidth'] - (zk.ie < 8 ? 1 : 0), 
			extSum = total - width;
		
		var count = total,
			visj = -1;
		if (this._nspan < 0) { //span to all columns
			for (var i = hdfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
				if (!zk(hdfaker.cells[i]).isVisible()) continue;
				wds[i] = wd = extSum <= 0 ? wds[i] : (((wds[i] * total / width) + 0.5) | 0);
				var rwd = zk(bdfaker.cells[i]).revisedWidth(wd),
					stylew = jq.px0(rwd);
				count -= wd;
				visj = i;
				if (bdfaker.cells[i].style.width == stylew)
					continue;
				bdfaker.cells[i].style.width = stylew; 
				hdfaker.cells[i].style.width = stylew;
				if (ftfaker) ftfaker.cells[i].style.width = stylew;
				var cpwd = zk(head.cells[i]).revisedWidth(rwd);
				head.cells[i].style.width = jq.px0(cpwd);
				var cell = head.cells[i].firstChild;
				cell.style.width = zk(cell).revisedWidth(cpwd) + "px";
			}
			//compensate calc error
			if (extSum > 0 && count != 0 && visj >= 0) {
				wd = wds[visj] + count;
				var rwd = zk(bdfaker.cells[visj]).revisedWidth(wd),
					stylew = jq.px0(rwd);
				bdfaker.cells[visj].style.width = stylew; 
				hdfaker.cells[visj].style.width = stylew;
				if (ftfaker) ftfaker.cells[visj].style.width = stylew;
				var cpwd = zk(head.cells[visj]).revisedWidth(rwd);
				head.cells[visj].style.width = jq.px0(cpwd);
				var cell = head.cells[visj].firstChild;
				cell.style.width = zk(cell).revisedWidth(cpwd) + "px";
			}
		} else { //feature#3184415: span to a specific column
			// start of ZK-1043: can remove after FF 13
			var ff = zk.gecko,
				ff_10_12 = ff > 9 && ff < 13,
				ebody = this.ebody, 
				h;
			if (ff_10_12) {
				if (ebody)
					h = ebody.style.height;
				if (h)
					ebody.style.height = '';
			}
			// end of ZK-1043
			visj = this._nspan - 1;
			for (var i = hdfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
				if (!zk(hdfaker.cells[i]).isVisible()) continue;
				wd = visj == i && extSum > 0 ? (wds[visj] + extSum) : wds[i];
				var rwd = zk(bdfaker.cells[i]).revisedWidth(wd),
					stylew = jq.px0(rwd);
				if (bdfaker.cells[i].style.width == stylew)
					continue;
				bdfaker.cells[i].style.width = stylew; 
				hdfaker.cells[i].style.width = stylew;
				if (ftfaker) ftfaker.cells[i].style.width = stylew;
				var cpwd = zk(head.cells[i]).revisedWidth(rwd);
				head.cells[i].style.width = jq.px0(cpwd);
				var cell = head.cells[i].firstChild;
				cell.style.width = zk(cell).revisedWidth(cpwd) + "px";
			}
			// start of ZK-1043: can remove after FF 13
			if (ff_10_12 && h)
				ebody.style.height = h;
			// end of ZK-1043
		}
		//bug 3188738: Opera only. Grid/Listbox/Tree span="x" not working
		if (zk.opera) 
			zk(this.$n()).redoCSS();
		
	},
	_adjHeadWd: function () {
		var hdfaker = this.ehdfaker,
			bdfaker = this.ebdfaker,
			ftfaker = this.eftfaker,
			fakerflex = this.head ? this.head.$n('hdfakerflex') : null;
		if (!hdfaker || !bdfaker || !hdfaker.cells.length
		|| !bdfaker.cells.length || !zk(hdfaker).isRealVisible()
		|| !this.getBodyWidgetIterator().hasNext()) return;
		
		var hdtable = this.ehead.firstChild, head = this.head.$n();
		if (!head) return;
		
		// Bug #1886788 the size of these table must be specified a fixed size.
		var bdtable = this.ebody.firstChild;
		
		var	total = Math.max(hdtable.offsetWidth, bdtable.offsetWidth), 
			tblwd = Math.min(bdtable.parentNode.clientWidth, bdtable.offsetWidth);
			
		if (total == this.ebody.offsetWidth && 
			this.ebody.offsetWidth > tblwd && this.ebody.offsetWidth - tblwd < 20)
			total = tblwd;
		this._calcMinWds(); //i.e. this._minWd = _calcMinWd(this);
		var xwds = this._minWd,
			wds = xwds.wds,
			width = xwds.width;
		for (var i = bdfaker.cells.length - (fakerflex ? 1 : 0), 
				hwgt = this.head.lastChild; i--; hwgt = hwgt.previousSibling) {
			// sizedByContent shall not override column width
			if (!zk(hdfaker.cells[i]).isVisible() || hwgt._width) continue; 
			var wd = wds[i], 
				rwd = zk(bdfaker.cells[i]).revisedWidth(wd),
				wdpx = rwd + "px";
			hdfaker.cells[i].style.width = bdfaker.cells[i].style.width = wdpx;
			if (ftfaker) 
				ftfaker.cells[i].style.width = wdpx;
			var cpwd = zk(head.cells[i]).revisedWidth(rwd);
			head.cells[i].style.width = cpwd + "px";
			var cell = head.cells[i].firstChild;
			cell.style.width = zk(cell).revisedWidth(cpwd) + "px";
		}
		
		// in some case, the total width of this table may be changed.
		if (total != hdtable.offsetWidth) {
			total = hdtable.offsetWidth;
			tblwd = Math.min(this.ebody.clientWidth, bdtable.offsetWidth);
			if (total == this.ebody.offsetWidth && 
				this.ebody.offsetWidth > tblwd && this.ebody.offsetWidth - tblwd < 20)
				total = tblwd;
		}
		
		_adjMinWd(this);
	}
});
/** @class zul.mesh.Scrollbar
 * @import zk.Widget
 * The extra Scrollbar for the MeshWidget.
 * It is designed to be overriden
 * @since 6.5.0
 */
zul.mesh.Scrollbar = {
	/**
	 * Initialize the scrollbar of MeshWidget.
	 * @param zk.Widget wgt a widget
	 */
	init: function (wgt) {
		return;
	},
	/**
	 * Return the vertical scroll position of the body element of given MeshWidget.
	 * @param zk.Widget wgt the widget
	 * @return int
	 */
	getScrollPosV: function (wgt) {
		return wgt.ebody.scrollTop;
	}
};
})();
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

	function _getFirstRowCells(tbody) {
		if (tbody) {
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
				out.push('<tr id="', tbody.id, '-fakeRow" style="visiblity:none">');
				for (var i = 0; i < ncols; i++)
					out.push('<td></td>');
				out.push('</tr>');
				jq(tbody.rows[0]).before(out.join(''));
				out = null;
				return tbody.rows[0].cells;
			}
		}
	};
	function _deleteFakeRow(tbody) {
		jq('#' + tbody.id + '-fakeRow').remove();
	};
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
			var headcol = hdfaker.firstChild,
				headcell = headn.firstChild;
			for (var i = 0; headcol; headcol = headcol.nextSibling) {
				var headcave = headcell.firstChild;
				hdfakerws[i] = headcol.style.width;
				headcol.style.width = '';
				hdws[i] = headcell.style.width;
				headcell.style.width = '';
				hdcavews[i++] = headcave.style.width;
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
			w = head ? head = head.firstChild : null,
			headWgt = wgt.getHeadWidget(),
			max = 0, maxj;
		if (bdfaker && w) {
			var bodycells = _getFirstRowCells(wgt.ebodyrows),
				footcells = ftfaker ? _getFirstRowCells(wgt.efootrows) : null;
			
			for (var i = 0, len = bodycells.length; i < len; i++) {
				var wd = zk.parseInt(bodycells[i].offsetWidth),
					$cv = zk(w.$n('cave')),
					hdwd = w && w.isVisible() ? ($cv.textSize()[0] + $cv.padBorderWidth() + zk(w.$n()).padBorderWidth()) : 0,
					ftwd = footcells && zk(footcells[i]).isVisible() ? zk.parseInt(footcells[i].offsetWidth) : 0,
					header;
				
				if ((header = headWgt.getChildAt(i)) && header.getWidth())
					hdwd = Math.max(hdwd, ftwd);
				if (hdwd > wd)
					wd = hdwd;
				if (ftwd > wd)
					wd = ftwd;
				wds[i] = wd;
				if (zk.ff > 4 || zk.ie >= 9) // firefox4 & IE9 & IE10 still cause break line in case B50-3147926 column 1
					++wds[i];
				width += wds[i]; // using wds[i] instead of wd for B50-3183172.zul
				if (w)
					w = w.nextSibling;
			}
			_deleteFakeRow(wgt.ebodyrows);
			if (ftfaker)
				_deleteFakeRow(wgt.efootrows);
		} else {
			var tr;
			if (tr = _getSigRow(wgt)) {
				for (var cells = tr.cells, i = cells.length; i--;) {
					var wd = cells[i].offsetWidth;
					wds[i] = wd;
					if (zk.ff > 4 || zk.ie >= 9) // firefox4 & IE9 & IE10 still cause break line in case B50-3147926 column 1
						++wds[i];
					width += wds[i]; // using wds[i] instead of wd for B50-3183172.zul
				}
			}
		}

		if (wgt.eheadtbl && headn) {//restore headers widths
			wgt.eheadtbl.width = eheadtblw || '';
			wgt.eheadtbl.style.tableLayout = eheadtblfix || '';
			var headcol = hdfaker.firstChild,
				headcell = headn.firstChild;
			for (var i = 0; headcol; headcol = headcol.nextSibling) {
				var headcave = headcell.firstChild;
				
				headcol.style.width = hdfakerws[i];
				headcell.style.width = hdws[i];
				headcave.style.width = hdcavews[i++];
				
				headcell = headcell.nextSibling;
			}
		}
		if (wgt.efoottbl) {//restore footers widths
			wgt.efoottbl.width = efoottblw || '';
			wgt.efoottbl.style.tableLayout = efoottblfix || '';
			if (ftfaker) {
				var footcol = ftfaker.firstChild
				for (var i = 0; footcol; footcol = footcol.nextSibling)
					footcol.style.width = ftfakerws[i++];
			}
		}
		if (wgt.ebodytbl) {//restore body fakers widths
			wgt.ebodytbl.width = ebodytblw || '';
			wgt.ebodytbl.style.tableLayout = ebodytblfix || '';
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
			var w = _getMinWd(wgt);
			wgt._hflexsz = w + zk(wgt).padBorderWidth(); //override
			wgt.$n().style.width = jq.px0(w);
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
					wd = wds[i] = _minwds[i];
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
				d.style.width = zk(s).revisedWidth(s.offsetWidth);
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
		var td, frozen;
		if (this.head && (td = this.head.getChildAt(index).$n()) && parseInt(td.style.width) == 0 &&
			(frozen = zk.Widget.$(this.efrozen.firstChild)) &&
			(index = index - frozen.getColumns()) >= 0) {
			frozen.setStart(index);
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
		zWatch.listen({onSize: this, onResponse: this});
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, onResponse: this});
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
					if (hdsmin && !w._width && !w._nhflex) {
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
				if ((wd = w._hflexWidth) !== undefined) {
					bdcol.style.width = zk(bdcol).revisedWidth(wd) + 'px';
					hdcol.style.width = bdcol.style.width;
					if (ftcol)
						ftcol.style.width = bdcol.style.width;
				}
				bdcol = bdcol.nextSibling;
				hdcol = hdcol.nextSibling;
				if (ftcol)
					ftcol = ftcol.nextSibling;
				//++i;
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
		
		if (this.ebody) {
			if (zk.isLoaded('zul.grid') 
					&& this.$instanceof(zul.grid.Grid) && this.rows)
				this.ebodyrows = this.rows.$n();
			else
				this.ebodyrows = this.$n('rows');
		}
		
		if (this.ehead) {
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
			scrolled = (t != this._currentTop || l != this._currentLeft);
		
		if (scrolled &&	!this._listbox$rod && !this._grid$rod) // Bug ZK-353 ignore in rod
			this._currentTop = t;

		if (scrolled) // always sync for B30-1737660.zul
			this._currentLeft = l;

		if (!this.paging && !this._paginal)
			this.fireOnRender(zk.gecko ? 200 : 60);

		if (scrolled)
			this._fireOnScrollPos();
	},
	_timeoutId: null,
	_fireOnScrollPos: function (time) { //overriden in zkmax
		clearTimeout(this._timeoutId);
		//IE6 caused issue if the time too short, test case  http://www.zkoss.org/zksandbox/#g8
		this._timeoutId = setTimeout(this.proxy(this._onScrollPos), time >= 0 ? time : (zk.gecko) ? 200 : 60);
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
		if (items.length)
			this.fire('onRender', {items: items}, {implicit:true});
	},
	onSize: function () {
		if (this.isRealVisible()) { // sometimes the caller is not zWatch
			var n = this.$n();
			if (n._lastsz && n._lastsz.height == n.offsetHeight && n._lastsz.width == n.offsetWidth) {
				this.fireOnRender(155); // force to render while using live grouping
				return; // unchanged
			}
			this._calcSize();// Bug #1813722
			this.fireOnRender(155);
			this._shallSize = false;
		}
	},
	_vflexSize: function (hgh) {
		var n = this.$n(), pgHgh = 0;
		if (this.paging) {
			var pgit = this.$n('pgit'),
				pgib = this.$n('pgib');
			if (pgit) pgHgh += pgit.offsetHeight;
			if (pgib) pgHgh += pgib.offsetHeight;
		}
		return zk(n).revisedHeight(n.offsetHeight) - (this.ehead ? this.ehead.offsetHeight : 0)
			- (this.efoot ? this.efoot.offsetHeight : 0)
			- (this.efrozen ? this.efrozen.offsetHeight : 0)
			- pgHgh; // Bug #1815882 and Bug #1835369
	},
	setFlexSize_: function(sz) {
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
		return {height: n.offsetHeight, width: n.offsetWidth};
	},
	/* set the height. */
	_setHgh: function (hgh) {
		var ebody = this.ebody,
			ebodyStyle = ebody.style;
		if (this.isVflex() || (hgh && hgh != 'auto' && hgh.indexOf('%') < 0)) {
			if (zk.safari && ebodyStyle.height == jq.px(this._vflexSize(hgh)))
				return; // Bug ZK-417, ignore to set the same size
			
			ebodyStyle.height = ''; //allow browser adjusting to default size
			var h = this._vflexSize(hgh);
			if (h < 0)
				h = 0;
			if (this._vflex != 'min')
				ebodyStyle.height = h + 'px';
		} else {
			//Bug 1556099
			var n = this.$n();
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
			wd = n.style.width;
		
		//Bug 1659601: we cannot do it in init(); or, IE failed!
		var tblwd = this._getEbodyWd(),
			sizedByContent = this.isSizedByContent();
		
		if (this.ehead) {
			if (tblwd)
				this.ehead.style.width = tblwd + 'px';
			if (sizedByContent && this.ebodyrows)
				this._adjHeadWd();
			else if (tblwd && this.efoot)
				this.efoot.style.width = tblwd + 'px';
		} else if (this.efoot) {
			if (tblwd)
				this.efoot.style.width = tblwd + 'px';
			if (this.efootrows && this.ebodyrows)
				_cpCellWd(this);
		}

		//check if need to span width
		this._adjSpanWd();
		// no header case
		_fixBodyMinWd(this, true);
		
		n._lastsz = {height: n.offsetHeight, width: n.offsetWidth}; // cache for the dirty resizing.
		
		this._afterCalcSize();
	},
	_getEbodyWd: function () {
		return this.ebody.offsetWidth;
	},
	_beforeCalcSize: function () {
		this._setHgh(this.$n().style.height);
	},
	_afterCalcSize: function () {
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
	domFaker_: function (out, fakeId, zcls) { //used by redraw
		var head = this.head;
		out.push('<colgroup id="', head.uuid, fakeId, '">');
		
		for (var w = head.firstChild; w; w = w.nextSibling)
			out.push('<col id="', w.uuid, fakeId, '" style="', w.domStyle_(), '"/>');
		
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
			_minwds = this._minWd.wds;
		
		for (var w = this.head.firstChild, i = 0; w; w = w.nextSibling) {
			if (zk(hdcol).isVisible()) {
				var wdh = w._width;
				
				if (w._hflex == 'min')
					wd = wds[i] = _minwds[i];
				else if (wdh && wdh.endsWidth('px'))
					wd = wds[i] = zk.parseInt(wdh);
				else
					wd = wds[i] = zk.parseInt(hdcol.style.width);
				
				width += wd;
			}
			++i;
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
			for (var i = 0; hdcol; hdcol = hdcol.nextSibling) {
				if (!zk(hdcol).isVisible()) {
					++i;
					bdcol = bdcol.nextSibling;
					if (ftcol)
						ftcol = ftcol.nextSibling;
					continue;
				} else {
					wds[i] = wd = extSum <= 0 ? wds[i] : (((wds[i] * total / width) + 0.5) || 0);
					var stylew = jq.px0(wd);
					count -= wd;
					visj = i++;
					
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
			for (var i = 0; hdcol; hdcol = hdcol.nextSibling) {
				if (!zk(hdcol).isVisible()) {
					++i;
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
					++i;
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
			total = Math.max(hdtable.offsetWidth, bdtable.offsetWidth),
			tblwd = Math.min(ebody.offsetWidth, bdtable.offsetWidth);
		
		if (total == ebody.offsetWidth 
				&& ebody.offsetWidth > tblwd && ebody.offsetWidth - tblwd < 20)
			total = tblwd;
		
		var wds = this._calcMinWds().wds,
			hdcol = hdfaker.firstChild,
			bdcol = bdfaker.firstChild,
			ftcol = ftfaker ? ftfaker.firstChild : null;
		
		for (var i = 0, hwgt = this.head.firstChild; hwgt; hwgt = hwgt.nextSibling) {
			// sizedByContent shall not override column width
			if (hwgt._width) {
				hdcol = hdcol.nextSibling;
				bdcol = bdcol.nextSibling;
				if (ftcol)
					ftcol = ftcol.nextSibling;
				continue;
			} else {
				var wd = jq.px(wds[i++]);
				hdcol.style.width = bdcol.style.width = wd;
				hdcol = hdcol.nextSibling;
				bdcol = bdcol.nextSibling;
				if (ftcol) {
					ftcol.style.width = wd;
					ftcol = ftcol.nextSibling;
				}
			}
		}
		_adjMinWd(this);
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
		var headtbl = wgt.eheadtbl,
			foottbl = wgt.efoottbl;
		if (headtbl)
			headtbl.style[zk.vendor + 'Transform'] = 'translate(0, 0)';
		if (foottbl)
			foottbl.style[zk.vendor + 'Transform'] = 'translate(0, 0)';
		var embed = jq(wgt.$n()).data('embedscrollbar');
		wgt._scrollbar = new zul.Scrollbar(wgt.ebody, wgt.ebodytbl, {
			embed: embed,
			onSyncPosition: function() {
				var pos = this.getCurrentPosition(),
					headtbl = wgt.eheadtbl,
					foottbl = wgt.efoottbl;
				if (pos) {
					if (headtbl)
						headtbl.style[zk.vendor + 'Transform'] = 
							'translate(' + pos.x + 'px, 0)';
					if (foottbl)
						foottbl.style[zk.vendor + 'Transform'] = 
							'translate(' + pos.x + 'px, 0)';
				}
			},
			onScrollEnd: function() {
				wgt._doScroll();
			}
		});
		return wgt._scrollbar;
	},
	/**
	 * Return the vertical scroll position of the body element of given MeshWidget.
	 * @param zk.Widget wgt the widget
	 * @return int
	 */
	getScrollPosV: function (wgt) {
		var bar = wgt._scrollbar;
		if (bar) {
			var currPos = bar.getCurrentPosition();
			return Math.abs(Math.round(currPos.y));
		}
		return 0;
	},
	/**
	 * Return the horizontal scroll position of the body element of given MeshWidget.
	 * @param zk.Widget wgt the widget
	 * @return int
	 * @since 7.0.0
	 */
	getScrollPosH: function (wgt) {
		var bar = wgt._scrollbar;
		if (bar) {
			var currPos = bar.getCurrentPosition();
			return Math.abs(Math.round(currPos.x));
		}
		return 0;
	}
};
})();