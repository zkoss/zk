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

/**
 *  A skeletal implementation for a mesh widget.
 *  @see zul.grid.Grid
 *  @see zul.sel.Tree
 *  @see zul.sel.Listbox
 */
zul.mesh.MeshWidget = zk.$extends(zul.Widget, {
	_pagingPosition: "bottom",
	_prehgh: -1,
	
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
		return false;
	},
	_adjFlexWidth: function () {
		if (!this.head) return;
		var hdfaker = this.ehdfaker,
			bdfaker = this.ebdfaker,
			ftfaker = this.eftfaker,
			head = this.head,
			headn = head.$n(),
			i = 0;
		for (var w = head.firstChild; w; w = w.nextSibling) {
			if (w._hflexWidth !== undefined) {
				var wd = w._hflexWidth;
				this._setFakerWd(i, wd, hdfaker, bdfaker, ftfaker, headn);
			}
			++i;
		}
	},
	_setFakerWd: function (i, wd, hdfaker, bdfaker, ftfaker, headn) {
		bdfaker.cells[i].style.width = zk(bdfaker.cells[i]).revisedWidth(wd) + "px";
		hdfaker.cells[i].style.width = bdfaker.cells[i].style.width;
		if (ftfaker) ftfaker.cells[i].style.width = bdfaker.cells[i].style.width;
		if (headn) {
			var cpwd = zk(headn.cells[i]).revisedWidth(zk.parseInt(hdfaker.cells[i].style.width));
			headn.cells[i].style.width = cpwd + "px";
			var cell = headn.cells[i].firstChild;
			cell.style.width = zk(cell).revisedWidth(cpwd) + "px";
		}
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
		if (!(this.fire('onScroll', this.ebody.scrollLeft).stopped)) {
			if (this.ehead) 
				this.ehead.scrollLeft = this.ebody.scrollLeft;
			if (this.efoot) 
				this.efoot.scrollLeft = this.ebody.scrollLeft;		
		}
		
		this._currentTop = this.ebody.scrollTop;
		this._currentLeft = this.ebody.scrollLeft;
		this.fire('onScrollPos', {top: this._currentTop, left: this._currentLeft});
		
		if (!this.paging)
			this.fireOnRender(zk.gecko ? 200 : 60);
	},
	_onRender: function () {
		this._pendOnRender = false;
		if (this._syncingbodyrows) {
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
			row = null,
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
		if (row != null) { //there is row
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
	},
	onSize: _zkf = function () {
		if (this.isRealVisible()) { // sometimes the caller is not zWatch
			var n = this.$n();
			if (n._lastsz && n._lastsz.height == n.offsetHeight && n._lastsz.width == n.offsetWidth) {
				this.fireOnRender(155); // force to render while using live grouping
				return; // unchanged
			}
				
			this._calcSize();// Bug #1813722
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
			} else
				return this.$supers('setFlexSize_', arguments);
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto') {
				if (this._hflex != 'min') n.style.width = '';
				if (head) head.style.width = '';
			} else
				return this.$supers('setFlexSize_', arguments);
		}
		return {height: n.offsetHeight, width: n.offsetWidth};
	},
	/* set the height. */
	_setHgh: function (hgh) {
		if (this.isVflex() || (hgh && hgh != "auto" && hgh.indexOf('%') < 0)) {
			this.ebody.style.height = ''; //allow browser adjusting to default size
			var h = this._vflexSize(hgh); 
			if (h < 0) h = 0;

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
		if (zk.ie) {//By experimental: see zk-blog.txt
			if (this.eheadtbl &&
			this.eheadtbl.offsetWidth !=
			this.ebodytbl.offsetWidth) 
				this.ebodytbl.style.width = ""; //reset 
			if (tblwd && (this.ebody.offsetWidth == this.ebodytbl.offsetWidth) &&
			this.ebody.offsetWidth - tblwd > 11) { //scrollbar
				if (--tblwd < 0) 
					tblwd = 0;
				this.ebodytbl.style.width = tblwd + "px";
			}
			// bug #2799258 and #1599788
			var hgh = this.getHeight() || n.style.height; // bug in B36-2841185.zul
			if (!zk.ie8 && !this.isVflex() && (!hgh || hgh == "auto")) {
				hgh = this.ebody.offsetWidth - this.ebody.clientWidth;
				if (this.ebody.clientWidth && hgh > 11) 
					this.ebody.style.height = this.ebody.offsetHeight + jq.scrollbarWidth() + "px";
				
				// resync
				tblwd = this.ebody.clientWidth;
			}
		}
		if (this.ehead) {
			if (tblwd) this.ehead.style.width = tblwd + 'px';
			if (this.isSizedByContent() && this.ebodyrows && this.ebodyrows.length)
				this.$class._adjHeadWd(this);
			else if (tblwd && this.efoot) this.efoot.style.width = tblwd + 'px';
		} else if (this.efoot) {
			if (tblwd) this.efoot.style.width = tblwd + 'px';
			if (this.efoottbl.rows.length && this.ebodyrows && this.ebodyrows.length)
				this.$class.cpCellWidth(this);
		}
		n._lastsz = {height: n.offsetHeight, width: n.offsetWidth}; // cache for the dirty resizing.
		
		// Bug in B36-2841185.zul
		if (zk.ie8 && this.isModel() && this.inPagingMold())
			zk(this).redoCSS();
	},
	domFaker_: function (out, fakeId, zcls) { //used by mold
		var head = this.head;
		out.push('<tbody style="visibility:hidden;height:0px"><tr id="',
				head.uuid, fakeId, '" class="', zcls, '-faker">');
		for (var w = head.firstChild; w; w = w.nextSibling)
			out.push('<th id="', w.uuid, fakeId, '"', w.domAttrs_(),
				 	'><div style="overflow:hidden"></div></th>');
		out.push('</tr></tbody>');
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
					this.heads.splice(j, 0, child);
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
	}
}, { //static
	_adjHeadWd: function (wgt) {
		var hdfaker = wgt.ehdfaker,
			bdfaker = wgt.ebdfaker,
			ftfaker = wgt.eftfaker;
		if (!hdfaker || !bdfaker || !hdfaker.cells.length
		|| !bdfaker.cells.length || !zk(hdfaker).isRealVisible()
		|| !wgt.getBodyWidgetIterator().hasNext()) return;
		
		var hdtable = wgt.ehead.firstChild, head = wgt.head.$n();
		if (!head) return; 
		if (zk.opera) {
			if (!hdtable.style.width) {
				var isFixed = true, tt = wgt.ehead.offsetWidth;
				for(var i = hdfaker.cells.length; i--;) {
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
		var bdtable = wgt.ebody.firstChild,
			total = Math.max(hdtable.offsetWidth, bdtable.offsetWidth), 
			tblwd = Math.min(bdtable.parentNode.clientWidth, bdtable.offsetWidth);
			
		if (total == wgt.ebody.offsetWidth && 
			wgt.ebody.offsetWidth > tblwd && wgt.ebody.offsetWidth - tblwd < 20)
			total = tblwd;
			
		var xwds = wgt.$class._calcMinWd(wgt),
			wds = xwds.wds,
			width = xwds.width;
		if (!wgt.$n().style.width || width > total) {
			total = width;
			head.style.width = total + 'px';
		}
		
		var count = total;
		hdtable.style.width = total + "px";	
		
		if (bdtable) bdtable.style.width = hdtable.style.width;
		if (wgt.efoot) wgt.efoot.firstChild.style.width = hdtable.style.width;
		
		for (var i = bdfaker.cells.length; i--;) {
			if (!zk(hdfaker.cells[i]).isVisible()) continue;
			var wd = i != 0 ? wds[i] : count;
			bdfaker.cells[i].style.width = zk(bdfaker.cells[i]).revisedWidth(wd) + "px";
			hdfaker.cells[i].style.width = bdfaker.cells[i].style.width;
			if (ftfaker) ftfaker.cells[i].style.width = bdfaker.cells[i].style.width;
			var cpwd = zk(head.cells[i]).revisedWidth(zk.parseInt(hdfaker.cells[i].style.width));
			head.cells[i].style.width = cpwd + "px";
			var cell = head.cells[i].firstChild;
			cell.style.width = zk(cell).revisedWidth(cpwd) + "px";
			count -= wd;
		}
		
		// in some case, the total width of this table may be changed.
		if (total != hdtable.offsetWidth) {
			total = hdtable.offsetWidth;
			tblwd = Math.min(wgt.ebody.clientWidth, bdtable.offsetWidth);
			if (total == wgt.ebody.offsetWidth && 
				wgt.ebody.offsetWidth > tblwd && wgt.ebody.offsetWidth - tblwd < 20)
				total = tblwd;
				
			hdtable.style.width = total + "px";	
			if (bdtable) bdtable.style.width = hdtable.style.width;
			if (wgt.efoot) wgt.efoot.firstChild.style.width = hdtable.style.width;
		}
	},
	cpCellWidth: function (wgt) {
		var dst = wgt.efoot.firstChild.rows[0],
			srcrows = wgt.ebodyrows;
		if (!dst || !srcrows || !srcrows.length || !dst.cells.length)
			return;
		var ncols = dst.cells.length,
			src, maxnc = 0;
		for (var j = 0, it = wgt.getBodyWidgetIterator(), w; (w = it.next());) {
			if (!w.isVisible() || (wgt._modal && !w._loaded)) continue;

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
	},
	_calcMinWd: function (wgt) {
		if (wgt.eheadtbl) {
			wgt.ehead.style.width = '';
			wgt.eheadtbl.width = '';
			wgt.eheadtbl.style.width = '';
		}
		
		if (wgt.head && wgt.head.$n())
			wgt.head.$n().style.width = '';
			
		if (wgt.efoottbl) {
			wgt.efoot.style.width = '';
			wgt.efoottbl.width = '';
			wgt.efoottbl.style.width = '';
		}
		
		if (wgt.ebodytbl) {
			wgt.ebody.style.width = '';
			wgt.ebodytbl.width = '';
			wgt.ebodytbl.style.width = '';
		}
		
		//calculate widths
		var hdfaker = wgt.ehdfaker,
			bdfaker = wgt.ebdfaker,
			ftfaker = wgt.eftfaker,
			head = wgt.head,
			headn = head ? head.$n() : null,
			wds = [],
			width = 0,
			w = head ? head = head.lastChild : null;
		for (var i = bdfaker.cells.length; i--;) {
			var wd = bdwd = bdfaker.cells[i].offsetWidth,
				hdwd = w ? (zk(w.$n('cave')).textSize()[0] + zk(w.$n()).padBorderWidth()) : 0,
				ftwd = ftfaker && zk(ftfaker.cells[i]).isVisible() ? ftfaker.cells[i].offsetWidth : 0;
			if (hdwd > wd) wd = hdwd;
			if (ftwd > wd) wd = ftwd;
			wds[i] = wd;
			width += wd;
			if (w) w = w.previousSibling;
		}
		if (wgt.eheadtbl)
			wgt.eheadtbl.width='100%';
		
		if (wgt.efoottbl)
			wgt.efoottbl.width='100%';
		
		if (wgt.ebodytbl)
			wgt.ebodytbl.width='100%';
		
		return {'width': width, 'wds': wds};
	}
});
