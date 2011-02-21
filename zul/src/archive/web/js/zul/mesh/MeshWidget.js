/* MeshWidget.js

	Purpose:
		
	Description:
		
	History:
		Sat May  2 09:36:31     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The two-dimentional mesh related widgets.
 * A mesh widget is a two-dimentional widgets, such as grid, listbox and tree.
 * Classes in this package is the skeletal implementation that can be used
 * to simplify the implementation of grid, listbox and tree.
 */
//zk.$package('zul.mesh');

(function () {
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
		if (wgtn)
			wgtn.style.whiteSpace = 'pre';//'nowrap';
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
			
		if (wgt.eheadtbl) {
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
		if (wgt.head && wgt.head.$n())
			wgt.head.$n().style.width = '';
		if (wgt.efoottbl) {
			wgt.efoot.style.width = '';
			efoottblw = wgt.efoottbl.width;
			wgt.efoottbl.width = '';
			wgt.efoottbl.style.width = '';
			efoottblfix = wgt.efoottbl.style.tableLayout;
			wgt.efoottbl.style.tableLayout = '';
			for (var i = ftfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
				var ftcell = ftfaker.cells[i];
				ftfakerws[i] = ftcell.style.width;
				ftcell.style.width = '';
			}
		}
		if (wgt.ebodytbl) {
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
		var	wds = [],
			width = 0,
			w = head ? head = head.lastChild : null,
			headWgt = wgt.getHeadWidget(),
			max = 0, maxj;
		if (bdfaker) {
			for (var i = bdfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
				var wd = bdwd = bdfaker.cells[i].offsetWidth,
					$cv = zk(w.$n('cave')),
					hdwd = w && w.isVisible() ? ($cv.textSize()[0] + $cv.padBorderWidth() + zk(w.$n()).padBorderWidth()) : 0,
					ftwd = ftfaker && zk(ftfaker.cells[i]).isVisible() ? ftfaker.cells[i].offsetWidth : 0,
					header;
				if ((header = headWgt.getChildAt(i)) && header.getWidth())
					hdwd = Math.max(hdwd, hdfaker.cells[i].offsetWidth);
				if (hdwd > wd) wd = hdwd;
				if (ftwd > wd) wd = ftwd;
				wds[i] = wd;
				if (zk.ie && !zk.ie8 && max < wd) {
					max = wd;
					maxj = i;
				}
				width += wd;
				if (w) w = w.previousSibling;
			}
			if (zk.ie && !zk.ie8) //**Tricky. ie6/ie7 strange behavior, will generate horizontal scrollbar, minus one to avoid it! 
				--wds[maxj];
		}

		if (wgt.eheadtbl) {
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
		if (wgt.efoottbl) {
			wgt.efoottbl.width = efoottblw||'';
			wgt.efoottbl.style.tableLayout = efoottblfix||'';
			for (var i = ftfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
				ftfaker.cells[i].style.width = ftfakerws[i];
			}
		}
		if (wgt.ebodytbl) {
			wgt.ebodytbl.width = ebodytblw||'';
			wgt.ebodytbl.style.tableLayout = ebodytblfix||'';
			if (bdfaker)
				for (var i = bdfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
					bdfaker.cells[i].style.width = bdfakerws[i];
				}
		}

		if (wgtn)
			wgtn.style.whiteSpace = ws;
		return {width: width, wds: wds};
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
		 * specified column. "1" means distribute remaining empty space to the 1st column; "2" means 
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
		 * @see #setSpan(boolean)
		 */
		span: function(v) {
			this._nspan = (true === v || 'true' == v) ? -1 : zk.parseInt(v);
			this.rerender(); //_zkf
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
		return this.paging.getPageSize();
	},
	/** Sets the page size, aka., the number rows per page.
	 * @param int pageSize
	 * @see Paging#setPageSize
	 */
	setPageSize: function (pgsz) {
		this.paging.setPageSize(pgsz);
	},
	/** Returns the number of pages.
	 * Note: there is at least one page even no item at all.
	 * @return int
	 * @see Paging#getPageCount
	 */
	getPageCount: function () {
		return this.paging.getPageCount();
	},
	/** Returns the active page (starting from 0).
	 * @return int
	 * @see Paging#getActivePage
	 */
	getActivePage: function () {
		return this.paging.getActivePage();
	},
	/** Sets the active page (starting from 0).
	 * @param int activePage
	 * @see Paging#setActivePage
	 */
	setActivePage: function (pg) {
		this.paging.setActivePage(pg);
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

	bind_: function () {
		this.$supers(zul.mesh.MeshWidget, 'bind_', arguments);
		if (this.isVflex()) {
			// added by Jumper for IE to get a correct offsetHeight so we need 
			// to add this command faster than the this._calcSize() function.
			var hgh = this.$n().style.height;
			if (!hgh || hgh == "auto") this.$n().style.height = "99%"; // avoid border 1px;
		}
		this._bindDomNode();
		this._fixHeaders();
		if (this.ebody) {
			this.domListen_(this.ebody, 'onScroll');
			this.ebody.style.overflow = ''; // clear
			if (this.efrozen)
				this.ebody.style.overflowX = 'hidden'; // keep to hide
		}
		zWatch.listen({onSize: this, onShow: this, beforeSize: this, onResponse: this});
		var paging;
		if (zk.ie7_ && (paging = this.$n('pgib')))
			zk(paging).redoCSS();
	},
	unbind_: function () {
		if (this.ebody)
			this.domUnlisten_(this.ebody, 'onScroll');

		zWatch.unlisten({onSize: this, onShow: this, beforeSize: this, onResponse: this});
		
		this.$supers(zul.mesh.MeshWidget, 'unbind_', arguments);
	},
	clearCache: function () {
		this.$supers('clearCache', arguments);
		this.ebody = this.ehead = this.efoot = this.efrozen = this.ebodytbl
			= this.eheadtbl = this.efoottbl = this.ebodyrows
			= this.ehdfaker = this.ebdfaker = null;
	},

	onResponse: function () {
		if (this.desktop && this._shallSize) {
			this.$n()._lastsz = null; //reset
			this.onSize();
		}
	},
	_syncSize: function () {
		this._shallSize = true;
		if (!this.inServer && this.desktop)
			this.onResponse();
	},
	_fixHeaders: function () {
		if (this.head && this.ehead) {
			var empty = true;
			var flex = false;
			for (var i = this.heads.length; i-- > 0;) {
				for (var w = this.heads[i].firstChild; w; w = w.nextSibling) {
					if (!flex && w._nhflex)
						flex = true;
					if (w.getLabel() || w.getImage()
							|| w.nChildren) {
						empty = false;
						break;
					}
				}
				if (!empty) break;
			}
			var old = this.ehead.style.display; 
			this.ehead.style.display = empty ? 'none' : '';
			//onSize is not fired to empty header when loading page, so we have to simulate it here
			if (empty && flex) 
				for (var w = this.head.firstChild; w; w = w.nextSibling)
					if (w._nhflex) w.fixFlex_();
			return old != this.ehead.style.display;
		}
	},
	_adjFlexWd: function () { //used by HeadWidget
		var head = this.head;
		if (!head) return;
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
		this._adjMinWd();
	},
	_adjMinWd: function () {
		if (this._hflex == 'min') {
			this._hflexsz = this._getMinWd(); //override
			this.$n().style.width = jq.px0(this._hflexsz);
		}
	},
	_getMinWd: function () {
		this._calcMinWds();
		var bdfaker = this.ebdfaker,
			hdtable = this.eheadtbl,
			bdtable = this.ebodytbl,
			wd,
			wds = [],
			width,
			_minwds = this._minWd.wds;
		if (this.head && bdfaker) {
			width = 0;
			for (var w = this.head.firstChild, i = 0; w; w = w.nextSibling) {
				if (zk(bdfaker.cells[i]).isVisible()) {
					wd = wds[i] = w._hflex == 'min' ? _minwds[i] : (w._width && w._width.indexOf('px') > 0) ? zk.parseInt(w._width) : bdfaker.cells[i].offsetWidth;
					width += wd;
				}
				++i;
			}
		}
		return width + (zk.ie && !zk.ie8 ? 1 : 0);
	},
	_bindDomNode: function () {
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
				ie7special = zk.ie7 && bds && bds.length == 1 && !this.ehead && !bds[0].id;
			if (!bds || !bds.length || (this.ehead && bds.length < 2 || ie7special)) {
				if (ie7special) //remove the empty tbody
					jq(bds[0]).remove();
				var out = [];
				if (this.domPad_ && !this.inPagingMold() && this._mold != 'select') this.domPad_(out, '-tpad');
				out.push('<tbody id="',this.uuid,'-rows"/>');
				if (this.domPad_ && !this.inPagingMold() && this._mold != 'select') this.domPad_(out, '-bpad');
				jq(this.ebodytbl ).append(out.join(''));
			}
			this._syncbodyrows();
		}
		if (this.ehead) {
			this.ehdfaker = this.eheadtbl.tBodies[0].rows[0];
			this.ebdfaker = this.ebodytbl.tBodies[0].rows[0];
			if (this.efoottbl)
				this.eftfaker = this.efoottbl.tBodies[0].rows[0];
		}
	},
	_syncbodyrows: function() {
		var bds = this.ebodytbl.tBodies;
		this.ebodyrows = this.ebodytbl.tBodies[bds.length > 2 ? this.ehead ? 2 : 1 : this.ehead ? 1 : 0].rows;
		//Note: bodyrows is null in FF if no rows, so no err msg
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
			this._syncbodyrows();
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
	_doScroll: function () {
		//see onSize. Chrome/Safari can calc scrollbar size wrong when sizing. 
		//must display:none then restore to make it recalc, but it also cause scrolling. 
		//ignore it here to keep the _currentTop/_currentLeft intact!
		if (zk.safari && this._ignoreDoScroll) 
			return;
		
		if (!(this.fire('onScroll', this.ebody.scrollLeft).stopped)) {
			if (this._currentLeft != this.ebody.scrollLeft) { //care about horizontal scrolling only
				if (this.ehead) {
					this.ehead.scrollLeft = this.ebody.scrollLeft;
					//bug# 3039339: Column is not aligned in some special combination of dimension
					var diff = this.ebody.scrollLeft - this.ehead.scrollLeft;
					var hdflex = jq(this.ehead).find('table>tbody>tr>th:last-child')[0];
					if (diff) { //use the hdfakerflex to compensate
						hdflex.style.width = (hdflex.offsetWidth + diff) + 'px';
						this.ehead.scrollLeft = this.ebody.scrollLeft;
					} else if (hdflex.style.width != '0px' && this.ebody.scrollLeft == 0) {
						hdflex.style.width = '';
					}
				}
				if (this.efoot) 
					this.efoot.scrollLeft = this.ebody.scrollLeft;
			}
		}
		
		var t = this.ebody.scrollTop,
			l = this.ebody.scrollLeft,
			scrolled = (t != this._currentTop || l != this._currentLeft);
		if (scrolled) {
			this._currentTop = t; 
			this._currentLeft = l;
		}
		
		if (!this.paging)
			this.fireOnRender(zk.gecko ? 200 : 60);

		if (scrolled)
			this._fireOnScrollPos();
	},
	_timeoutId: null,
	_fireOnScrollPos: function (time) {
		clearTimeout(this._timeoutId);
		this._timeoutId = setTimeout(this.proxy(this._onScrollPos), time >= 0 ? time : zk.gecko ? 200 : 60);
	},
	_onScrollPos: function () {
		this._currentTop = this.ebody.scrollTop; 
		this._currentLeft = this.ebody.scrollLeft;
		this.fire('onScrollPos', {top: this._currentTop, left: this._currentLeft});
	},
	_onRender: function () {
		this._pendOnRender = false;
		if (this._syncingbodyrows || zAu.processing()) { //wait if busy (it might run outer)
			this.fireOnRender(zk.gecko ? 200 : 60); //is syncing rows, try it later
			return true;
		}

		var rows = this.ebodyrows;
		if (this.inPagingMold() && this._autopaging && rows && rows.length)
			if (this._fixPageSize(rows)) return; //need to reload with new page size
		
		if (!this.desktop || !this._model || !rows || !rows.length) return;

		//Note: we have to calculate from top to bottom because each row's
		//height might diff (due to different content)
		var items = [],
			min = this.ebody.scrollTop, max = min + this.ebody.offsetHeight;
		for (var j = 0, it = this.getBodyWidgetIterator(), len = rows.length, w; (w = it.next()) && j < len; j++) {
			if (w.isVisible() && !w._loaded) {
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
	_fixPageSize: function(rows) {
		var ebody = this.ebody;
		if (!ebody) return; //not ready yet
		var max = ebody.offsetHeight;
		if (max == this._prehgh) return false; //same height, avoid fixing page size
		this._prehgh = max;
		var ebodytbl = this.ebodytbl,
			etbparent = ebodytbl.offsetParent,
			etbtop = ebodytbl.offsetTop,
			hgh = 0, 
			row,
			j = 0;
		for (var it = this.getBodyWidgetIterator(), len = rows.length, w; (w = it.next()) && j < len; j++) {
			if (w.isVisible()) {
				row = rows[j];
				var top = row.offsetTop - (row.offsetParent == etbparent ? etbtop : 0);
				if (top > max) {
					--j;
					break;
				}
				hgh = top;
			}
		}
		if (row) { //there is row
			if (top <= max) { //row not exceeds the height, estimate
				hgh = hgh + row.offsetHeight;
				j = Math.floor(j * max / hgh);
			}
			//enforce pageSize change
			if (j == 0) j = 1; //at least one per page
			if (j != this.getPageSize()) {
				this.fire('onChangePageSize', {size: j});
				return true;
			}
		}
		return false;
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
				
			n._lastsz = null;// Bug #3013683: ie6 will do onSize twice
		}
		
		// Bug 2896474
		if (zk.ie6_ && this._vflex) {
			var hgh = this.getHeight();
			if (!hgh || hgh == "auto" || hgh.indexOf('%') >= 0) {
				var n = this.$n();
				
				n.style.height = '';
				if (this.ebody) {
					// check if this or this.parent has _vflex='min' should reset an empty string for B50-2976912.zul
					this.ebody.style.height = (this._vflex != 'min' && (!this.parent || this.parent._vflex != 'min')) ? '0px' : '';
				}
				n._lastsz = null;
			}
		}
		
	},
	onSize: _zkf = function () {
		if (this.isRealVisible()) { // sometimes the caller is not zWatch
			var n = this.$n();
			if (n._lastsz && n._lastsz.height == n.offsetHeight && n._lastsz.width == n.offsetWidth) {
				this.fireOnRender(155); // force to render while using live grouping
				return; // unchanged
			}
				
			this._calcSize();// Bug #1813722
			
			//bug #3177128
			if (zk.safari && this.ebodytbl) {
				this._ignoreDoScroll = true; //will cause _doScroll, don't change scrolling position
				try {
					var oldCSS = this.ebodytbl.style.display;
					this.ebodytbl.style.display = 'none';
					var dummy = this.ebodytbl.offsetWidth; //force recalc
					this.ebodytbl.style.display = oldCSS;
					//bug #3185647: extra space on top of body content
					oldCss = this.ebody.style.height;
					this.ebody.style.height = jq.px0(this.ebodytbl.offsetHeight);
					dummy = this.ebody.offsetHeight; //force recalc					
					this.ebody.style.height = oldCss;
					dummy = this.ebody.offsetHeight; //force recalc					
				} finally {
					delete this._ignoreDoScroll;
				}
			}
			
			this.fireOnRender(155);
			this.ebody.scrollTop = this._currentTop;
			this.ebody.scrollLeft = this._currentLeft;
			this._shallSize = false;
		}
	},
	onShow: _zkf,
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
				if (zk.ie && !zk.ie8 && this._vflex == 'min' && this._vflexsz === undefined)
					sz.height = sz.height + 1;
				return this.$supers('setFlexSize_', arguments);
			}
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto') {
				if (this._hflex != 'min') n.style.width = '';
				if (head) head.style.width = '';
			} else {
				if (zk.ie && !zk.ie8 && this._hflex == 'min' && this._hflexsz === undefined)
					sz.width = sz.width + 1;
				return this.$supers('setFlexSize_', arguments);
			}
		}
		return {height: n.offsetHeight, width: n.offsetWidth};
	},
	/* set the height. */
	_setHgh: function (hgh) {
		if (this.isVflex() || (hgh && hgh != "auto" && hgh.indexOf('%') < 0)) {
			this.ebody.style.height = ''; //allow browser adjusting to default size
			var h = this._vflexSize(hgh); 
			if (h < 0) h = 0;

			if (!zk.ie || zk.ie8 || this._vflex != "min")
				this.ebody.style.height = h + "px";
			//2007/12/20 We don't need to invoke the body.offsetHeight to avoid a performance issue for FF. 
			if (zk.ie && this.ebody.offsetHeight) {} // bug #1812001.
			// note: we have to invoke the body.offestHeight to resolve the scrollbar disappearing in IE6 
			// and IE7 at initializing phase.
		} else {
			//Bug 1556099: it is strange if we ever check the value of
			//body.offsetWidth. The grid's body's height is 0 if init called
			//after grid become visible (due to opening an accordion tab)
			this.ebody.style.height = "";
			this.$n().style.height = hgh;
		}
	},
	/** Calculates the size. */
	_calcSize: function () {
		var n = this.$n();
		this._setHgh(n.style.height);
		
		//Bug 1553937: wrong sibling location
		//Otherwise,
		//IE: element's width will be extended to fit body
		//FF and IE: sometime a horizontal scrollbar appear (though it shalln't)
		//note: we don't solve this bug for paging yet
		var wd = n.style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			wd = zk(n).revisedWidth(n.offsetWidth);
			if (wd < 0) 
				wd = 0;
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
		
		//Bug 1659601: we cannot do it in init(); or, IE failed!
		var tblwd = this.ebody.clientWidth;
		var hgh = this.getHeight() || n.style.height; // bug in B36-2841185.zul
		if (zk.ie) {//By experimental: see zk-blog.txt
			if (this.eheadtbl &&
			this.eheadtbl.offsetWidth !=
			this.ebodytbl.offsetWidth) 
				this.ebodytbl.style.width = ""; //reset 
			if (tblwd && 
					// fixed column's sizing issue in B30-1895907.zul
					(!this.eheadtbl || !this.ebodytbl || !this.eheadtbl.style.width ||
					this.eheadtbl.style.width != this.ebodytbl.style.width
					|| this.ebody.offsetWidth == this.ebodytbl.offsetWidth) &&
					// end of the fixed
					this.ebody.offsetWidth - tblwd > 11) { //scrollbar
				if (--tblwd < 0) 
					tblwd = 0;
				this.ebodytbl.style.width = tblwd + "px";
			}
			// bug #2799258 and #1599788
			if (!zk.ie8 && !this.isVflex() && (!hgh || hgh == "auto")) {
				var scroll = this.ebody.offsetWidth - this.ebody.clientWidth;
				if (this.ebody.clientWidth && scroll > 11) //v-scrollbar 
					this.ebody.style.height = jq.px0(this.ebodytbl.offsetHeight); //extend body height to remove the v-scrollbar
				// resync
				tblwd = this.ebody.clientWidth;
			}
		}
		if (this.ehead) {
			if (tblwd) this.ehead.style.width = tblwd + 'px';
			if (this.isSizedByContent() && this.ebodyrows && this.ebodyrows.length)
				this._adjHeadWd();
			else if (tblwd && this.efoot) this.efoot.style.width = tblwd + 'px';
		} else if (this.efoot) {
			if (tblwd) this.efoot.style.width = tblwd + 'px';
			if (this.efoottbl.rows.length && this.ebodyrows && this.ebodyrows.length)
				this._cpCellWd();
		}
		
		//check if need to span width
		this._adjSpanWd();

		//bug# 3022669: listbox hflex="min" sizedByContent="true" not work
		if (this._hflexsz === undefined && this._hflex == 'min' && this._width === undefined && n.offsetWidth > this.ebodytbl.offsetWidth) {
			n.style.width = this.ebodytbl.offsetWidth + 'px';
			this._hflexsz = n.offsetWidth;
		}
		
		n._lastsz = {height: n.offsetHeight, width: n.offsetWidth}; // cache for the dirty resizing.
		
		// Bug in B36-2841185.zul
		if (zk.ie8 && this.isModel() && this.inPagingMold())
			zk(this).redoCSS();

		//bug#3186596: unwanted v-scrollbar
		this._removeScrollbar();
	},
	_removeScrollbar: function() { //see HeadWidget#afterChildrenFlex_
		var hgh = this.getHeight() || this.$n().style.height; // bug in B36-2841185.zul
		if (zk.ie && !this.isVflex() && (!hgh || hgh == "auto")) {
			if(!zk.ie8) { 
				var scroll = this.ebody.offsetWidth - this.ebody.clientWidth;
				if (this.ebody.clientWidth && scroll > 11) { //v-scroll, expand body height to remove v-scroll
					this.ebody.style.height = jq.px0(this.ebodytbl.offsetHeight);
					if ((this.ebody.offsetWidth - this.ebody.clientWidth) > 11) //still v-scroll, expand body height for extra h-scroll space to remove v-scroll 
						this.ebody.style.height = jq.px0(this.ebodytbl.offsetHeight+jq.scrollbarWidth());
				}
			} else if (this.ebodytbl.offsetWidth > this.ebody.offsetWidth) { //IE8 sometimes will fail to show the h-scrollbar; enforce it!
				var oldCss = this.ebody.style.overflowX;
				this.ebody.style.overflowX = 'scroll';
			} else
				this.ebody.style.overflowX = '';
		}
	},
	//return if all widths of columns are fixed (directly or indirectly)
	_isAllWidths: function() {
		if (this.isSizedByContent())
			return true;
		else if (!this.head)
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
			totalWidth = 0, shallCheckSize = zk.ie && !zk.ie8;
		
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
		out.push('<th id="', head.uuid, fakeId, 'flex"', (allwidths || this.isSizedByContent() ? '' : ' style="width:0px"'), '></th></tr></tbody>');
	},

	//super//
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);

		if (child.$instanceof(this.getHeadWidgetClass()))
			this.head = child;
		else if (!child.$instanceof(zul.mesh.Auxhead))
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
			this.head = null;
			this.heads.$remove(child);
		} else if (child.$instanceof(zul.mesh.Auxhead))
			this.heads.$remove(child);
	},
	//bug# 3022669: listbox hflex="min" sizedByContent="true" not work
	beforeMinFlex_: function (orient) {
		if (this._hflexsz === undefined && orient == 'w' && this._width === undefined) {
			if (this.isSizedByContent())
				this._calcSize();
			if (this.head)
				for(var w = this.head.firstChild; w; w = w.nextSibling) 
					if (w._hflex == 'min' && w.hflexsz === undefined) //header hflex="min" not done yet!
						return null;
			return this._getMinWd(); //grid.invalidate() with hflex="min" must maintain the width 
		}
		return null;
	},
	_calcMinWds: function () {
		if (!this._minWd)
			this._minWd = _calcMinWd(this); 
		return this._minWd;
	},
	_adjSpanWd: function () {
		if (!this._isAllWidths())
			return;
		var isSpan = this.isSpan();
		if (!isSpan)
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
				wd = wds[i] = w._hflex == 'min' ? _minwds[i] : (w._width && w._width.indexOf('px') > 0) ? zk.parseInt(w._width) : hdfakervisible ? hdfaker.cells[i].offsetWidth : bdfaker.cells[i].offsetWidth;
				width += wd;
			}
			++i;
		}
		var hgh = zk.ie && !zk.ie8 ? (this.getHeight() || this.$n().style.height) : true; //ie6/ie7 leave a vertical scrollbar space, use offsetWidth if not setting height
		var	total = (hgh ? bdtable.parentNode.clientWidth : bdtable.parentNode.offsetWidth) - (zk.ie && !zk.ie8 ? 1 : 0), //**Tricky. ie6/ie7 strange behavior, will generate horizontal scrollbar, minus one to avoid it!
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
		}
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
		if (zk.opera) {
			if (!hdtable.style.width) {
				var isFixed = true, tt = this.ehead.offsetWidth;
				for(var i = hdfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
					if (!hdfaker.cells[i].style.width || hdfaker.cells[i].style.width.indexOf("%") >= 0) {
						isFixed = false; 
						break;
					}
					tt -= zk.parseInt(hdfaker.cells[i].style.width);
				}
				if (!isFixed || tt >= 0) hdtable.style.tableLayout = "auto";
			}
		}
		
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

		for (var i = bdfaker.cells.length - (fakerflex ? 1 : 0); i--;) {
			if (!zk(hdfaker.cells[i]).isVisible()) continue;
			var wd = wds[i];
			bdfaker.cells[i].style.width = zk(bdfaker.cells[i]).revisedWidth(wd) + "px";
			hdfaker.cells[i].style.width = bdfaker.cells[i].style.width;
			if (ftfaker) ftfaker.cells[i].style.width = bdfaker.cells[i].style.width;
			var cpwd = zk(head.cells[i]).revisedWidth(zk.parseInt(hdfaker.cells[i].style.width));
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
		
		this._adjMinWd();
	},
	_cpCellWd: function () {
		var dst = this.efoot.firstChild.rows[0],
			srcrows = this.ebodyrows;
		if (!dst || !srcrows || !srcrows.length || !dst.cells.length)
			return;
		var ncols = dst.cells.length,
			src, maxnc = 0;
		for (var j = 0, it = this.getBodyWidgetIterator(), w; (w = it.next());) {
			if (!w.isVisible() || (this._modal && !w._loaded)) continue;

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
	
		if (zk.opera && this.isSizedByContent())
			dst.parentNode.parentNode.style.width = sum + "px";
	
		if (fakeRow)
			src.parentNode.removeChild(src);
	}
});
})();