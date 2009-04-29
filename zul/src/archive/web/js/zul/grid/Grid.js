/* Grid.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:23:39     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Grid = zk.$extends(zul.Widget, {
	_pagingPosition: "bottom",
	_preloadsz: 7,
	/** ROD Mold */
	_innerWidth: "100%",
	_innerTop: "height:0px;display:none",
	_innerBottom: "height:0px;display:none",
	
	isVflex: function () {
		return this._vflex;
	},
	setVflex: function (vflex) {
		if (this._vflex != vflex) {
			this._vflex = vflex;
			var n = this.getNode();
			if (n) {
				if (vflex) {
					// added by Jumper for IE to get a correct offsetHeight so we need 
					// to add this command faster than the this._calcSize() function.
					var hgh = n.style.height;
					if (!hgh || hgh == "auto") n.style.height = "99%"; // avoid border 1px;
				}
				this.onSize();
			}
		}
	},
	setFixedLayout: function (fixedLayout) {
		if(this._fixedLayout != fixedLayout) {
			this._fixedLayout = fixedLayout;
			this.rerender();
		}
	},
	isFixedLayout: function () {
		return this._fixedLayout;
	},
	getHeads: function () {
		var heads = [];
		for (var w = this.firstChild; w; w = w.nextSibling) {
			if (w.$instanceof(zul.grid.Auxhead) || w.$instanceof(zul.grid.Columns))
				heads.push(w);
		}
		return heads;
	},
	getCell: function (row, col) {
		if (!this.rows) return null;
		if (rows.nChildren <= row) return null;

		var row = rows.getChildAt(row);
		return row.nChildren <= col ? null: row.getChildAt(col);
	},
	getAlign: function () {
		return this._align;
	},
	setAlign: function (align) {
		if (this._align != align) {
			this._align = align;
			var n = this.getNode();
			if (n) n.align = align;
		}
	},
	setPagingPosition: function (pagingPosition) {
		if(this._pagingPosition != pagingPosition) {
			this._pagingPosition = pagingPosition;
			this.rerender();
		}
	},
	getPagingPosition: function () {
		return this._pagingPosition;
	},
	getPageSize: function () {
		return this.paging.getPageSize();
	},
	setPageSize: function (pgsz) {
		this.paging.setPageSize(pgsz);
	},
	getPageCount: function () {
		return this.paging.getPageCount();
	},
	getActivePage: function () {
		return this.paging.getActivePage();
	},
	setActivePage: function (pg) {
		this.paging.setActivePage(pg);
	},
	_inPagingMold: function () {
		return "paging" == this.getMold();
	},
	setInnerHeight: function (innerHeight) {
		if (innerHeight == null) innerHeight = "100%";
		if (this._innerHeight != innerHeight) {
			this._innerHeight = innerHeight;
			// TODO for ROD Mold
		}
	},
	getInnerHeight: function () {
		return this._innerHeight;
	},
	setInnerTop: function (innerTop) {
		if (innerTop == null) innerTop = "height:0px;display:none";
		if (this._innerTop != innerTop) {
			this._innerTop = innerTop;
			// TODO for ROD Mold
		}
	},
	getInnerTop: function () {
		return this._innerTop;
	},
	setInnerBottom: function (innerBottom) {
		if (innerBottom == null) innerBottom = "height:0px;display:none";
		if (this._innerBottom != innerBottom) {
			this._innerBottom = innerBottom;
			// TODO for ROD Mold
		}
	},
	getInnerBottom: function () {
		return this._innerBottom;
	},
	isModel: function () {
		return this._model;
	},
	setModel: function (model) {
		if (this._model != model) {
			this._model = model;
		}
	},
	getPreloadSize: function () {
		return this._preloadsz;
	},
	setPreloadSize: function (sz) {
		if (sz >= 0)
			this._preloadsz = sz;
	},
	setInnerWidth: function (innerWidth) {
		if (innerWidth == null) innerWidth = "100%";
		if (this._innerWidth != innerWidth) {
			this._innerWidth = innerWidth;
			if (this.eheadtbl) this.eheadtbl.style.width = innerWidth;
			if (this.ebodytbl) this.ebodytbl.style.width = innerWidth;
			if (this.efoottbl) this.efoottbl.style.width = innerWidth;
		}
	},
	getInnerWidth: function () {
		return this._innerWidth;
	},
	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		var n = this.getNode();
		if (n) {
			if (zk.ie6Only && this.ebody) 
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
	},
	setStyle: function (style) {
		if (this._style != style) {
			this.$supers('setStyle', arguments);
			this.onSize();
		}
	},
	getOddRowSclass: function () {
		return this._scOddRow == null ? this.getZclass() + "-odd" : this._scOddRow;
	},
	setOddRowSclass: function (scls) {
		if (!scls) scls = null;
		if (this._scOddRow != scls) {
			this._scOddRow = scls;
			var n = this.getNode();
			if (n && this.rows)
				this.rows.stripe();
		}
	},
	getZclass: function () {
		return this._zclass == null ? "z-grid" : this._zclass;
	},
	//-- super --//
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.grid.Rows))
			this.rows = child;
		else if (child.$instanceof(zul.grid.Columns))
			this.columns = child;
		else if (child.$instanceof(zul.grid.Foot))
			this.foot = child;			
		else if (child.$instanceof(zul.grid.Paging))
			this.paging = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.rows)
			this.rows = null;
		else if (child == this.columns)
			this.columns = null;
		else if (child == this.foot)
			this.foot = null;			
		else if (child == this.paging)
			this.paging = null;
	},
	insertChildHTML_: function (child, before, desktop) {
		if (child.$instanceof(zul.grid.Rows)) {
			this.rows = child;
			if (this.ebodytbl) {
				zDom.insertHTMLBeforeEnd(this.ebodytbl, child._redrawHTML());
				child.bind_(desktop);
				return;
			}
		} 
		
		this.rerender();
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.isVflex()) {
			// added by Jumper for IE to get a correct offsetHeight so we need 
			// to add this command faster than the this._calcSize() function.
			var hgh = this.getNode().style.height;
			if (!hgh || hgh == "auto") this.getNode().style.height = "99%"; // avoid border 1px;
		}
		this._bindDomNode();
		this._fixHeaders();
		if (this.ebody) {
			this.domListen_(this.ebody, 'scroll');
			this.ebody.style.overflow = ''; // clear
		}
		zWatch.listen("onSize", this);
		zWatch.listen("onShow", this);
		zWatch.listen("beforeSize", this);
	},
	unbind_: function () {
		if (this.ebody)
			this.domUnlisten_(this.ebody, 'scroll');
			
		this.ebody = this.ehead = this.efoot = this.ebodytbl
			= this.eheadtbl = this.efoottbl = null;
		
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onShow", this);
		zWatch.unlisten("beforeSize", this);
		
		this.$supers('unbind_', arguments);
	},
	_fixHeaders: function () {
		if (this.columns && this.ehead) {
			var empty = true;
			for (var w = this.columns.firstChild; w; w = w.nextSibling) 
				if (w.getLabel() || w.getImage()) {
					empty = false;
					break;
				}
			this.ehead.style.display = empty ? 'none' : '';
		}
	},
	_bindDomNode: function () {
		for (var n = this.getNode().firstChild; n; n = n.nextSibling)
			switch(n.id) {
			case this.uuid + '$head':
				this.ehead = n;
				this.eheadtbl = zDom.firstChild(n, 'TABLE');
				break;
			case this.uuid + '$body':
				this.ebody = n;
				this.ebodytbl = zDom.firstChild(n, 'TABLE');
				break;
			case this.uuid + '$foot':
				this.efoot = n;
				this.efoottbl = zDom.firstChild(n, 'TABLE');
				break;
			}
		if (this.ehead) {
			this.ehdfaker = this.eheadtbl.tBodies[0].rows[0];
			this.ebdfaker = this.ebodytbl.tBodies[0].rows[0];
			if (this.efoottbl)
				this.eftfaker = this.efoottbl.tBodies[0].rows[0];
		}
	},
	fireScrollRender: function (timeout) {
		setTimeout(this.proxy(this.onScrollRender), timeout ? timeout : 100);
	},
	domScroll_: function () {
		if (this.ehead)
			this.ehead.scrollLeft = this.ebody.scrollLeft;
		if (this.efoot)
			this.efoot.scrollLeft = this.ebody.scrollLeft;
		if (!this.paging) this.fireScrollRender(zk.gecko ? 200 : 60);
	},
	onScrollRender: function () {
		if (!this.isModel() || !this.rows || !this.rows.nChildren) return;

		//Note: we have to calculate from top to bottom because each row's
		//height might diff (due to different content)
		var data = "";
		var min = this.ebody.scrollTop, max = min + this.ebody.offsetHeight;
		for (var rows = this.rows.getNode().rows, j = 0, rl = rows.length; j < rl; ++j) {
			var r = rows[j];
			if (zDom.isVisible(r)) {
				var top = zDom.offsetTop(r);
				if (top + zDom.offsetHeight(r) < min) continue;
				if (top > max) break; //Bug 1822517
				if (!zk.Widget.$(r)._loaded)
					data += "," + r.id;
			}
		}
		if (data) {
			data = data.substring(1);
			this.fire('onRender', data);
		}
	},
	//watch//
	beforeSize: function () {
		// IE6 needs to reset the width of each sub node if the width is a percentage
		var wd = zk.ie6Only ? this.getWidth() : this.getNode().style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			if (this.ebody) this.ebody.style.width = "";
			if (this.ehead) this.ehead.style.width = "";
			if (this.efoot) this.efoot.style.width = "";
		}
	},
	onSize: _zkf = function () {
		if (this.isRealVisible()) {
			var n = this.getNode();
			if (n._lastsz && n._lastsz.height == n.offsetHeight && n._lastsz.width == n.offsetWidth)
				return; // unchanged
				
			this._calcSize();// Bug #1813722
			this.fireScrollRender(155);
			if (zk.ie7) zDom.redoCSS(this.getNode()); // Bug 2096807
		}
	},
	onShow: _zkf,
	onRender: function (evt) {
		var d = evt.data.marshal();
		this._curpos = d[0];
		this._visicnt = d[1];
		if (this.columns)
			this.setInnerWidth(d[2]);
			
		this.setInnerHeight(d[3]);
		this._onRender();
		evt.stop();
	},
	_vflexSize: function (hgh) {
		var n = this.getNode();
		if (zk.ie6Only) { 
			// ie6 must reset the height of the element,
			// otherwise its offsetHeight might be wrong.
			n.style.height = "";
			n.style.height = hgh;
		}
		return n.offsetHeight - 2 - (this.ehead ? this.ehead.offsetHeight : 0)
			- (this.efoot ? this.efoot.offsetHeight : 0); // Bug #1815882 and Bug #1835369
	},
	/* set the height. */
	_setHgh: function (hgh) {
		if (this.isVflex() || (hgh && hgh != "auto" && hgh.indexOf('%') < 0)) {
			var h =  this._vflexSize(hgh); 
			if (this.paging) {
				/** TODO 
				 * var pgit = $e(this.id + "!pgit"), pgib = $e(this.id + "!pgib");
				if (pgit) h -= pgit.offsetHeight;
				if (pgib) h -= pgib.offsetHeight;*/
			}
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
			this.getNode().style.height = hgh;
		}
	},
	/** Calculates the size. */
	_calcSize: function () {
		var n = this.getNode();
		this._setHgh(n.style.height);
		//Bug 1553937: wrong sibling location
		//Otherwise,
		//IE: element's width will be extended to fit body
		//FF and IE: sometime a horizontal scrollbar appear (though it shalln't)
		//note: we don't solve this bug for paging yet
		var wd = n.style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			wd = zDom.revisedWidth(n, n.offsetWidth);
			if (wd < 0) wd = 0;
			if (wd) wd += "px";
		}
		if (wd) {
			this.ebody.style.width = wd;
			if (this.ehead) this.ehead.style.width = wd;
			if (this.efoot) this.efoot.style.width = wd;
		}
		//Bug 1659601: we cannot do it in init(); or, IE failed!
		var tblwd = this.ebody.clientWidth;
		if (zk.ie) //By experimental: see zk-blog.txt
			if (this.eheadtbl && this.eheadtbl.offsetWidth
					!= this.ebodytbl.offsetWidth)
				this.ebodytbl.style.width = ""; //reset 
			if (tblwd && this.ebody.offsetWidth == this.ebodytbl.offsetWidth
					&& this.ebody.offsetWidth - tblwd > 11) { //scrollbar
				if (--tblwd < 0) tblwd = 0;
				this.ebodytbl.style.width = tblwd + "px";
			}
				
		if (this.ehead) {
			if (tblwd) this.ehead.style.width = tblwd + 'px';
			if (!this.isFixedLayout() && this.rows)
				this.$class.adjustHeadWidth(this);
		} else if (this.efoot) {
			if (tblwd) this.efoot.style.width = tblwd + 'px';
			if (this.efoottbl.rows.length && this.rows)
				this.$class.cpCellWidth(this);
		}
		n._lastsz = {height: n.offsetHeight, width: n.offsetWidth}; // cache for the dirty resizing.
	},
	domFaker_: function (out, fakeId, zcls) {
		out.push('<tbody style="visibility:hidden;height:0px"><tr id="',
				this.columns.uuid, fakeId, '" class="', zcls, '-faker">');
		for (var w = this.columns.firstChild; w; w = w.nextSibling)
			out.push('<th id="', w.uuid, fakeId, '"', w.domAttrs_(),
				 	'><div style="overflow:hidden"></div></th>');
		out.push('</tr></tbody>');
	}
}, {
	adjustHeadWidth: function (wgt) {
		// function (hdfaker, bdfaker, ftfaker, rows) {
		var hdfaker = wgt.ehdfaker,
			bdfaker = wgt.ebdfaker,
			ftfaker = wgt.eftfaker,
			rows = wgt.rows.getNode().rows;
		if (!hdfaker || !bdfaker || !hdfaker.cells.length
		|| !bdfaker.cells.length || !zDom.isRealVisible(hdfaker) || !rows.length) return;
		
		var hdtable = wgt.ehead.firstChild, head = wgt.columns.getNode();
		if (!head) return; 
		if (zk.opera) {
			if (!hdtable.style.width) {
				var isFixed = true, tt = wgt.ehead.offsetWidth;
				for(var i = hdfaker.cells.length; --i >=0;) {
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
			
		var count = total;
		hdtable.style.width = total + "px";	
		
		if (bdtable) bdtable.style.width = hdtable.style.width;
		if (wgt.efoot) wgt.efoot.firstChild.style.width = hdtable.style.width;
		
		for (var i = bdfaker.cells.length; --i >= 0;) {
			if (!zDom.isVisible(hdfaker.cells[i])) continue;
			var wd = i != 0 ? bdfaker.cells[i].offsetWidth : count;
			bdfaker.cells[i].style.width = zDom.revisedWidth(bdfaker.cells[i], wd) + "px";
			hdfaker.cells[i].style.width = bdfaker.cells[i].style.width;
			if (ftfaker) ftfaker.cells[i].style.width = bdfaker.cells[i].style.width;
			var cpwd = zDom.revisedWidth(head.cells[i], zk.parseInt(hdfaker.cells[i].style.width));
			head.cells[i].style.width = cpwd + "px";
			var cell = head.cells[i].firstChild;
			cell.style.width = zDom.revisedWidth(cell, cpwd) + "px";
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
			srcrows = wgt.rows.getNode().rows;
		if (!dst || !srcrows.length || !dst.cells.length)
			return;
		var ncols = dst.cells.length,
			src, maxnc = 0, loadIdx = wgt._lastLoadIdx;
		for (var j = 0, len = loadIdx || srcrows.length; j < len; ++j) {
			var row = srcrows[j];
			if (!zDom.isVisible(row) || !zk.Widget.$(row)._loaded) continue;
			var cells = row.cells, nc = zDom.ncols(row),
				valid = cells.length == nc && zDom.isVisible(row);
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
		for (var j = maxnc; --j >=0;)
			dst.cells[j].style.width = "";
	
		var sum = 0;
		for (var j = maxnc; --j >= 0;) {
			var d = dst.cells[j], s = src.cells[j];
			if (zk.opera) {
				sum += s.offsetWidth;
				d.style.width = zDom.revisedWidth(s, s.offsetWidth);
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
	
		if (zk.opera && !wgt.isFixedLayout())
			dst.parentNode.parentNode.style.width = sum + "px";
	
		if (fakeRow)
			src.parentNode.removeChild(src);
	}
});
