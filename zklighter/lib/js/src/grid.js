_z='zul.grid';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zul.grid.HeaderWidget = zk.$extends(zul.LabelImageWidget, {
	getAlign: function () {
		return this._align;
	},
	setAlign: function (align) {
		if (this._align != align) {
			this._align = align;
			this.invalidateWhole_();
		}
	},
	getValign: function () {
		return this._valign;
	},
	setValign: function (valign) {
		if (this._valign != valign) {
			this._valign = valign;
			this.invalidateWhole_();
		}
	},
	invalidateWhole_: function () {
		var wgt = this.getOwner();
		if (wqt) wgt.rerender();
	},
	getOwner: function () {
		return this.parent ? this.parent.parent : null;
	},
	isSortable_: function () {
		return false;
	},
	getColAttrs: function () {
		return (this._align ? ' align="' + this._align + '"' : '')
			+ (this._valign ? ' valign="' + this._valign + '"' : '') ;
	},
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			this.$supers('setVisible', arguments);
			this.invalidateWhole();
		}
	},
	domAttrs_: function (no) {
		var attrs = this.$supers('domAttrs_', arguments);
		return attrs + this.getColAttrs();
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.parent.isSizable()) this._initsz();
		this._fixedFaker();
	},
	unbind_: function () {
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}
		this.$supers('unbind_', arguments);
	},
	_initsz: function () {
		var n = this.getNode();
		if (n && !this._drag) {
			var $Header = this.$class;
			this._drag = new zk.Draggable(this, null, {
				revert: true, constraint: "horizontal",
				ghosting: $Header._ghostsizing,
				endghosting: $Header._endghostsizing,
				snap: $Header._snapsizing,
				ignoredrag: $Header._ignoresizing,
				endeffect: $Header._aftersizing
			});
		}
	},
	_fixedFaker: function () {
		var n = this.getNode(),
			index = zDom.cellIndex(n),
			owner = this.getOwner();
		for (var faker, fs = this.$class._faker, i = fs.length; --i >= 0;) {
			faker = owner['e' + fs[i]]; // internal element
			if (faker && !this.getSubnode(fs[i])) 
				faker[faker.cells.length > index ? "insertBefore" : "appendChild"]
					(this._createFaker(n, fs[i]), faker.cells[index]);
		}
	},
	_createFaker: function (n, postfix) {
		var t = document.createElement("TH"), 
			d = document.createElement("DIV");
		t.id = n.id + "$" + postfix;
		t.className = n.className;
		t.style.cssText = n.style.cssText;
		d.style.overflow = "hidden";
		t.appendChild(d);
		return t;
	},
	doClick_: function (evt) {
		if (!zk.dragging && zk.Widget.$(evt.nativeTarget) == this && this.isSortable_() 
			&& zDom.tag(evt.nativeTarget) != "INPUT") {
			this.fire('onSort');
			evt.stop();
		}
	},
	doMouseMove_: function (evt) {
		if (zk.dragging || !this.parent.isSizable()) return;
		var n = this.getNode(),
			ofs = zDom.revisedOffset(n); // Bug #1812154
		if (this._insizer(evt.data.pageX - ofs[0])) {
			zDom.addClass(n, this.getZclass() + "-sizing");
		} else {
			zDom.rmClass(n, this.getZclass() + "-sizing");
		}
	},
	doMouseOut_: function (evt) {
		if (this.parent.isSizable()) {
			var n = this.getNode()
			zDom.rmClass(n, this.getZclass() + "-sizing");
		}
	},
	_insizer: function (x) {
		return x >= this.getNode().offsetWidth - 10;
	}
}, {
	_faker: ["hdfaker", "bdfaker", "ftfaker"],
	
	_onSizingMarshal: function () {
		return [this.index, this.uuid, this.width, this.keys ? this.keys.marshal(): ''];
	},
	//dragdrop//
	_ghostsizing: function (dg, ofs, evt) {
		var wgt = dg.control,
			el = wgt.getOwner().eheadtbl;
			of = zDom.revisedOffset(el),
			n = wgt.getNode();
		
		ofs[1] = of[1];
		ofs[0] += zDom.offsetWidth(n);
		document.body.insertAdjacentHTML("afterBegin",
			'<div id="zk_hdghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:3px;height:'+zDom.offsetHeight(el.parentNode.parentNode)
			+'px;background:darkgray"></div>');
		return zDom.$("zk_hdghost");		
	},
	_endghostsizing: function (dg, origin) {
		dg._zszofs = zDom.revisedOffset(dg.node)[0] - zDom.revisedOffset(origin)[0];
	},
	_snapsizing: function (dg, pointer) {
		var n = dg.control.getNode(),
			ofs = zDom.revisedOffset(n);
		pointer[0] += zDom.offsetWidth(n); 
		if (ofs[0] + dg._zmin >= pointer[0])
			pointer[0] = ofs[0] + dg._zmin;
		return pointer;
	},
	_ignoresizing: function (dg, pointer, evt) {
		var wgt = dg.control,
			n = wgt.getNode(),
			ofs = zDom.revisedOffset(n); // Bug #1812154
			
		if (wgt._insizer(pointer[0] - ofs[0])) {
			dg._zmin = 10 + zDom.padBorderWidth(n);		
				return false;
		}
		return true;
	},
	_aftersizing: function (dg, evt) {
		var wgt = dg.control,
			n = wgt.getNode(),
			owner = wgt.getOwner(),
			wd = dg._zszofs,
			table = owner.eheadtbl,
			head = table.tBodies[0].rows[0], 
			rwd = zDom.revisedWidth(n, wd),
			cells = head.cells,
			cidx = zDom.cellIndex(n),
			total = 0;
			
		for (var k = cells.length; --k >= 0;)
			if (k !== cidx) total += cells[k].offsetWidth;

		// For Opera, the code of adjusting width must be in front of the adjusting table.
		// Otherwise, the whole layout in Opera always shows wrong.
		if (owner.efoottbl) {
			owner.eftfaker.cells[cidx].style.width = wd + "px";
		}
		var fixed;
		if (owner.ebodytbl) {
			if (zk.opera && !owner.ebodytbl.style.tableLayout) {
				fixed = "auto";
				owner.ebodytbl.style.tableLayout = "fixed";
			}
			owner.ebdfaker.cells[cidx].style.width = wd + "px";
		}
		
		head.cells[cidx].style.width = wd + "px";
		n.style.width = rwd + "px";
		var cell = n.firstChild;
		cell.style.width = zDom.revisedWidth(cell, rwd) + "px";
		
		table.style.width = total + wd + "px";		
		if (owner.efoottbl)
			owner.efoottbl.style.width = table.style.width;
		
		if (owner.ebodytbl)
			owner.ebodytbl.style.width = table.style.width;
			
		if (zk.opera && fixed) owner.ebodytbl.style.tableLayout = fixed;
		
		wgt.parent.fire('onColSize', {
			index: cidx,
			uuid: wgt.uuid,
			width: wd + "px",
			keys: zEvt.keyMetaData(evt),
			marshal: wgt.$class._onSizingMarshal
		}, null, 0);
	}
});

(_zkwg=_zkpk.HeaderWidget).prototype.className='zul.grid.HeaderWidget';
zul.grid.HeadersWidget = zk.$extends(zul.Widget, {
	$init: function () {
		this.$supers('$init', arguments);
		this.listen('onColSize', this, null, -1000);
	},
	onColSize: function (evt) {
		var owner = this.parent;
		if (!owner.isFixedLayout()) owner.$class.adjustHeadWidth(owner);
		owner.fire('onInnerWidth', owner.eheadtbl.style.width);
		owner.fireScrollRender(zk.gecko ? 200 : 60);
	},
	isSizable: function () {
		return this._sizable;
	},
	setSizable: function (sizable) {
		if (this._sizable != sizable) {
			this._sizable = sizable;
			this.rerender();
		}
	},
	unbind_: function () {
		if (this.hdfaker) zDom.remove(this.hdfaker);
		if (this.bdfaker) zDom.remove(this.bdfaker);
		if (this.ftfaker) zDom.remove(this.ftfaker);
		this.$supers('unbind_', arguments);
	}
});

(_zkwg=_zkpk.HeadersWidget).prototype.className='zul.grid.HeadersWidget';
zul.grid.Column = zk.$extends(zul.grid.HeaderWidget, {
	_sortDir: "natural",
	_sortAsc: "none",
	_sortDsc: "none",
	
	$init: function () {
		this.$supers('$init', arguments);
		this.listen('onSort', this, null, -1000);
	},
	getGrid: function () {
		return this.getOwner();
	},
	setSort: function (type) {
		if (type && type.startsWith('client')) {
			this.setSortAscending(type);
			this.setSortDescending(type);
		} else {
			this.setSortAscending('none');
			this.setSortDescending('none');
		}
	},
	getSortDirection: function () {
		return this._sortDir;
	},
	setSortDirection: function (sortDir) {
		if (this._sortDir != sortDir) {
			this._sortDir = sortDir;
			var n = this.getNode();
			
			if (n) {
				var zcls = this.getZclass();
				zDom.rmClass(n, zcls + "-sort-dsc");
				zDom.rmClass(n, zcls + "-sort-asc");
				switch (sortDir) {
				case "ascending":
					zDom.addClass(n, zcls + "-sort-asc");
					break;
				case "descending":
					zDom.addClass(n, zcls + "-sort-dsc");
					break;
				default: // "natural"
					zDom.addClass(n, zcls + "-sort");
					break;
				}
			}
		}
	},
	isSortable_: function () {
		return this._sortAsc != "none" || this._sortDsc != "none";
	},
	getSortAscending: function () {
		return this._sortAsc;
	},
	setSortAscending: function (sorter) {
		if (!sorter) sorter = "none";
		if (this._sortAsc != sorter) {
			this._sortAsc = sorter;
			var n = this.getNode(),
				zcls = this.getZclass();
			if (n) {
				if (sorter == "none") {
					zDom.rmClass(n, zcls + "-sort-asc");
					if (this._sortDsc == "none")
						zDom.rmClass(n, zcls + "-sort");					
				} else
					zDom.addClass(n, zcls + "-sort");
			}
		}
	},
	getSortDescending: function () {
		return this._sortDsc;
	},
	setSortDescending: function (sorter) {
		if (!sorter) sorter = "none";
		if (this._sortDsc != sorter) {
			this._sortDsc = sorter;
			var n = this.getNode(),
				zcls = this.getZclass();
			if (n) {
				if (sorter == "none") {
					zDom.rmClass(n, zcls + "-sort-dsc");
					if (this._sortAsc == "none")
						zDom.rmClass(n, zcls + "-sort");					
				} else
					zDom.addClass(n, zcls + "-sort");
			}
		}
	},
	sort: function (ascending, evt) {
		var dir = this.getSortDirection();
		if (ascending) {
			if ("ascending" == dir) return false;
		} else {
			if ("descending" == dir) return false;
		}

		var sorter = ascending ? this._sortAsc: this._sortDsc;
		if (sorter == "fromServer")
			return false;
		else if (sorter == "none") {
			evt.stop();
			return false;
		}
		
		var grid = this.getGrid();
		if (!grid || grid.isModel()) return false;
			// if in model, the sort should be done by server
			
		var	rows = grid.rows;		
		if (!rows) return false;
		rows.parent.removeChild(rows);
		evt.stop();
		var d = [], col = this.getChildIndex();
		for (var i = 0, z = 0, row = rows.firstChild; row; row = row.nextSibling, z++)
			for (var k = 0, cell = row.firstChild; cell; cell = cell.nextSibling, k++) 
				if (k == col) {
					d[i++] = {
						wgt: cell,
						index: z
					};
				}
		
		var dsc = dir == "ascending" ? 1 : -1,
			fn = this.sorting,
			isNumber = sorter == "client(number)";
		d.sort(function(a, b) {
			var v = fn(a.wgt, b.wgt, isNumber) * dsc;
			if (v == 0) {
				v = (a.index < b.index ? -1 : 1);
			}
			return v;
		});
		for (var i = 0, k = d.length;  i < k; i++) {
			rows.appendChild(d[i].wgt.parent);
		}
		this._fixDirection(ascending);
		grid.appendChild(rows);
		return true;
	},
	sorting: function(a, b, isNumber) {
		var v1 = a.getValue(), v2 = b.getValue();
			if (isNumber) return v1 - v2;
		return v1 > v2 ? 1 : (v1 < v2 ? -1 : 0);
	},
	_fixDirection: function (ascending) {
		//maintain
		for (var w = this.parent.firstChild; w; w = w.nextSibling) {
			w.setSortDirection(
				w != this ? "natural": ascending ? "ascending": "descending");
		}
	},
	setLabel: function (label) {
		this.$supers('setLabel', arguments);
		// TODO menupopup
	},
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			this.$supers('setVisible', arguments);
			// TODO menupopup
		}
	},
	onSort: function (evt) {
		var dir = this.getSortDirection();
		if ("ascending" == dir) this.sort(false, evt);
		else if ("descending" == dir) this.sort(true, evt);
		else if (!this.sort(true, evt)) this.sort(false, evt);
	},
	getZclass: function () {
		return this._zclass == null ? "z-column" : this._zclass;
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = this._sortAsc != "none" || this._sortDsc != "none" ?  this.getZclass() + '-sort': '';
			return scls != null ? scls + ' ' + added : added;
		}
		return scls;
	}
});

(_zkwg=_zkpk.Column).prototype.className='zul.grid.Column';_zkmd={};
_zkmd['default']=
function (out) {
	var zcls = this.getZclass();
	out.push('<th', this.domAttrs_(), '><div id="', this.uuid, '$cave" class="',
			zcls, '-cnt">', this.domContent_());
	if (this.parent.menupopup && this.parent.menupopup != 'none')
		out.push('<a id="', this.uuid, '$btn"  href="javascript:;" class="', zcls, '-btn"></a>');
	
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div></th>');	
}

zkmld(_zkwg,_zkmd);
zul.grid.Columns = zk.$extends(zul.grid.HeadersWidget, {
	_mpop: "none",
	_columnshide: true,
	_columnsgroup: true,
	
	getGrid: function () {
		return this.parent;
	},
	setColumnshide: function (columnshide) {
		if (this._columnshide != columnshide) {
			this._columnshide = columnshide;
			//postOnInitLater();
			//smartUpdate("z.columnshide", _columnshide);
		}
	},
	isColumnshide: function () {
		return this._columnshide;
	},
	setColumnsgroup: function (columnsgroup) {
		if (this._columnsgroup != columnsgroup) {
			this._columnsgroup = columnsgroup;
			//postOnInitLater();
			//smartUpdate("z.columnsgroup", _columnsgroup);
		}
	},
	isColumnsgroup: function () {
		return this._columnsgroup;
	},
	getMenupopup: function () {
		return this._mpop;
	},
	setMenupopup: function (mpop) {
		/**if (!Objects.equals(_mpop, mpop)) {
			_mpop = mpop;
			invalidate();
			postOnInitLater();
		}*/
	},
	rerender: function () {
		if (this.desktop) {
			if (this.parent)
				this.parent.rerender();
			else 
				this.$superts('rerender', arguments);
		}
	},
	setPopup: function (mpop) {
		if (zk.Widget.isInstance(mpop))
			this._mpop = mpop;
	},
	_getMpopId: function () {
		/**final String mpop = getMenupopup();
		if ("none".equals(mpop)) return "zk_n_a";
		if ("auto".equals(mpop)) return _menupopup.getId();
		return mpop;*/
	},
	getZclass: function () {
		return this._zclass == null ? "z-columns" : this._zclass;
	}
});

(_zkwg=_zkpk.Columns).prototype.className='zul.grid.Columns';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<tr', this.domAttrs_(), ' align="left">');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</tr>');
}
zkmld(_zkwg,_zkmd);
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
			zEvt.listen(this.ebody, 'scroll', this.proxy(this.doScroll_, '_pxDoScroll'));
			this.ebody.style.overflow = ''; // clear
		}
		zWatch.listen("onSize", this);
		zWatch.listen("onVisible", this);
		zWatch.listen("beforeSize", this);
	},
	unbind_: function () {
		if (this.ebody)
			zEvt.unlisten(this.ebody, 'scroll', this._pxDoScroll);
			
		this.ebody = this.ehead = this.efoot = this.ebodytbl
			= this.eheadtbl = this.efoottbl = null;
		
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onVisible", this);
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
	doScroll_: function () {
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
	onVisible: _zkf,
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
		var ncols = dst.cells.length, //TODO: handle colspan for dst: ncols = zk.ncols(dst.cells);
			src, maxnc = 0, loadIdx = wgt._lastLoadIdx;
		for (var j = 0, len = loadIdx || srcrows.length; j < len; ++j) {
			var row = srcrows[j];
			if (!zDom.isVisible(row) || !zk.Widget.$(row)._loaded) continue;
			var cells = row.cells, nc = zDom.ncols(cells),
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

(_zkwg=_zkpk.Grid).prototype.className='zul.grid.Grid';_zkmd={};
_zkmd['paging']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		innerWidth = this.getInnerWidth(),
		width = innerWidth == '100%' ? ' width="100%"' : '',
		width1 =  innerWidth != '100%' ? 'width:' + innerWidth : '',
		inPaging = this._inPagingMold();
	out.push('<div', this.domAttrs_(), (this.getAlign() ? ' align="' + this.getAlign() + '"' : ''), '>');
	
	if (inPaging && this.paging
			&& (this.getPagingPosition() == 'top' || this.getPagingPosition() == 'both')) {
		out.push('<div id="', uuid, '$pgit" class="', zcls, '-pgi-t">');
		this.paging.redraw(out);
		out.push('</div>');
	}
	
	if (this.columns) {
		out.push('<div id="', uuid, '$head" class="', zcls, '-header">',
				'<table', width, zUtl.cellps0,
				' style="table-layout:fixed;', width1,'">');
		this.domFaker_(out, '$hdfaker', zcls);
		
		for (var hds = this.getHeads(), w = hds.shift(); w; w = hds.shift())
			w.redraw(out);
	
		out.push('</table></div>');
	}
	out.push('<div id="', uuid, '$body" class="', zcls, '-body"');
	
	var hgh = this.getHeight();
	if (hgh) out.push(' style="height:', hgh, '"');
	
	out.push('><table', width, zUtl.cellps0);
	
	if (this.isFixedLayout())
		out.push(' style="table-layout:fixed;', width1,'"');
		
	out.push('>');
	
	if (this.columns)
		this.domFaker_(out, '$bdfaker', zcls);
	
	if (this.rows) this.rows.redraw(out);
	
	out.push('</table></div>');
	
	if (this.foot) {
		out.push('<div id="', uuid, '$foot" class="', zcls, '-footer">',
				'<table', width, zUtl.cellps0, ' style="table-layout:fixed;', width1,'">');
		if (this.columns) 
			this.domFaker_(out, '$ftfaker', zcls);
			
		this.foot.redraw(out);
		out.push('</table></div>');
	}
	if (inPaging && this.paging
			&& (this.getPagingPosition() == 'bottom' || this.getPagingPosition() == 'both')) {
		out.push('<div id="', uuid, '$pgib" class="', zcls, '-pgi-b">');
		this.paging.redraw(out);
		out.push('</div>');
	}
	out.push('</div>');
}

_zkmd['default']=[_zkpk.Grid,'paging'];zkmld(_zkwg,_zkmd);
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
(_zkwg=_zkpk.Row).prototype.className='zul.grid.Row';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<tr', this.domAttrs_(), '>');
	var	zcls = this.getZclass(),
		overflow = this.getGrid().isFixedLayout() ? 'z-overflow-hidden' : '' ;
	for (var j = 0, w = this.firstChild; w; w = w.nextSibling, j++)
		this.encloseChildHTML_({child:w, index: j, zclass: zcls, cls: overflow, out: out});
	out.push('</tr>');	
}

zkmld(_zkwg,_zkmd);
zul.grid.Rows = zk.$extends(zul.Widget, {
	_visibleItemCount: 0,
	$init: function () {
		this.$supers('$init', arguments);
		this._groupsInfo = [];
		this._groups = [];
	},
	getGrid: function () {
		return this.parent;
	},
	getGroupCount: function () {
		return this._groupsInfo.length;
	},
	getGroups: function () {
		return this._groups;
	},
	hasGroup: function () {
		return this._groupsInfo.length != 0;
	},
	getZclass: function () {
		return this._zclass == null ? "z-rows" : this._zclass;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen('onResponse', this);
		zk.afterMount(this.proxy(this.onResponse));
	},
	unbind_: function () {
		zWatch.unlisten('onResponse', this);
		this.$supers('unbind_', arguments);
	},
	onResponse: function () {
		if (this._shallStripe) {
			this.stripe();
			this.getGrid().onSize();
		}
	},
	_syncStripe: function () {
		this._shallStripe = true;
		if (!this.inServer && this.desktop)
			this.onResponse();
	},
	stripe: function () {
		var scOdd = this.getGrid().getOddRowSclass();
		if (!scOdd) return;
		var n = this.getNode();
		for (var j = 0, w = this.firstChild, even = true; w; w = w.nextSibling, ++j) {
			if (w.isVisible() && w.isStripeable_()) {
				zDom[even ? 'rmClass' : 'addClass'](n.rows[j], scOdd);
				w.fire("onStripe");
				even = !even;
			}
		}
		this._shallStripe = false;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		this._syncStripe();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		this._syncStripe();
	},
	//Paging//
	getVisibleItemCount: function () {
		return this._visibleItemCount;
	},
	_addVisibleItemCount: function (count) {
		if (count) {
			this._visibleItemCount += count;
			if (this.parent != null && this.parent.inPagingMold()) {
				var pgi = this.parent.getPaginal();
				pgi.setTotalSize(this._visibleItemCount);
				// TODO invalidate(); // the set of visible items might change
			}
		}
	},
	_fixGroupIndex: function (j, to, infront) {
		//TODO:
		/**for (Iterator it = getChildren().listIterator(j);
		it.hasNext() && (to < 0 || j <= to); ++j) {
			Object o = it.next();
			if (o instanceof Group) {
				int[] g = getLastGroupsInfoAt(j + (infront ? -1 : 1));
				if (g != null) {
					g[0] = j;
					if (g[2] != -1) g[2] += (infront ? 1 : -1);
				}
			}
		}*/
	},
	getGroup: function (index) {
		if (!this._groupsInfo.length) return null;
		var g = this._getGroupsInfoAt(index);
		if (g != null) return ; // TODO (Group)getChildren().get(g[0]);
		return null;
	},
	/**
	 * Returns the last groups info which matches with the same index.
	 * Because dynamically maintain the index of the groups will occur the same index
	 * at the same time in the loop. 
	 */
	_getLastGroupsInfoAt: function (index) {
		/**
		int [] rg = null;
		for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
			int[] g = (int[])it.next();
			if (index == g[0]) rg = g;
			else if (index < g[0]) break;
		}
		return rg;*/
	},
	/**
	 * Returns an int array that it has two length, one is an index of Group,
	 * and the other is the number of items of Group(inclusive).
	 */
	_getGroupsInfoAt: function (index, isGroup) {
		// TODO:
		/**
		for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
			int[] g = (int[])it.next();
			if (isGroup) {
				if (index == g[0]) return g;
			} else if ((index > g[0] && index <= g[0] + g[1]))
				return g;
		}
		return null;*/
	}
	//-- super --//
	/** TODO
	 * public boolean insertBefore(Component child, Component refChild) {
		if (!(child instanceof Row))
			throw new UiException("Unsupported child for rows: "+child);
		Row newItem = (Row) child;
		final int jfrom = hasGroup() && newItem.getParent() == this ? newItem.getIndex(): -1;	

		final boolean isReorder = child.getParent() == this;
		if (newItem instanceof Groupfoot){
			if (!hasGroup())
				throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");
			if (refChild == null) {
				if (getLastChild() instanceof Groupfoot)
					throw new UiException("Only one Goupfooter is allowed per Group");
				if (isReorder) {
					final int idx = newItem.getIndex();				
					final int[] ginfo = getGroupsInfoAt(idx);
					if (ginfo != null) {
						ginfo[1]--; 
						ginfo[2] = -1;
					}
				}
				final int[] g = (int[]) _groupsInfo.get(getGroupCount()-1);
				g[2] = getChildren().size() - (isReorder ? 2 : 1);
			} else {
				final int idx = ((Row)refChild).getIndex();				
				final int[] g = getGroupsInfoAt(idx);
				if (g == null)
					throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");				
				if (g[2] != -1)
					throw new UiException("Only one Goupfooter is allowed per Group");
				if (idx != (g[0] + g[1]))
					throw new UiException("Groupfoot must be placed after the last Row of the Group");
				g[2] = idx-1;
				if (isReorder) {
					final int nindex = newItem.getIndex();				
					final int[] ginfo = getGroupsInfoAt(nindex);
					if (ginfo != null) {
						ginfo[1]--; 
						ginfo[2] = -1;
					}
				}
			}							
		}
		if (super.insertBefore(child, refChild)) {
			if(hasGroup()) {
				final int
					jto = refChild instanceof Row ? ((Row)refChild).getIndex(): -1,
					fixFrom = jfrom < 0 || (jto >= 0 && jfrom > jto) ? jto: jfrom;
				if (fixFrom >= 0) fixGroupIndex(fixFrom,
					jfrom >=0 && jto >= 0 ? jfrom > jto ? jfrom: jto: -1, !isReorder);
			}
			if (newItem instanceof Group) {
				Group group = (Group) newItem;
				int index = group.getIndex();
				if (_groupsInfo.isEmpty())
					_groupsInfo.add(new int[]{group.getIndex(), getChildren().size() - index, -1});
				else {
					int idx = 0;
					int[] prev = null, next = null;
					for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
						int[] g = (int[])it.next();
						if(g[0] <= index) {
							prev = g;
							idx++;
						} else {
							next = g;
							break;
						}
					}
					if (prev != null) {
						int leng = index - prev[0], 
							size = prev[1] - leng + 1;
						prev[1] = leng;
						_groupsInfo.add(idx, new int[]{index, size, size > 1 ? prev[2] : -1});
						if (size > 1) prev[2] = -1; // reset groupfoot
					} else if (next != null) {
						_groupsInfo.add(idx, new int[]{index, next[0] - index, -1});
					}
				}
			} else if (hasGroup()) {
				int index = newItem.getIndex();
				final int[] g = getGroupsInfoAt(index);
				if (g != null) {
					g[1]++;
					if (g[2] != -1) g[2]++;
				}
				
			}
			
			afterInsert(child);
			return true;
		}
		return false;
	}*/
	/**
	 * If the child is a group, its groupfoot will be removed at the same time.
	 */
	/** TODO
	 * public boolean removeChild(Component child) {
		if (child.getParent() == this)
			beforeRemove(child);
		int index = hasGroup() ? ((Row)child).getIndex() : -1;
		if(super.removeChild(child)) {
			if (child instanceof Group) {
				int[] prev = null, remove = null;
				for(Iterator it = _groupsInfo.iterator(); it.hasNext();) {
					int[] g = (int[])it.next();
					if (g[0] == index) {
						remove = g;
						break;
					}
					prev = g;
				}
				if (prev != null && remove !=null) {
					prev[1] += remove[1] - 1;
				}
				fixGroupIndex(index, -1, false);
				if (remove != null) {
					_groupsInfo.remove(remove);
					final int idx = remove[2];
					if (idx != -1) {
						removeChild((Component) getChildren().get(idx -1));
							// Because the fixGroupIndex will skip the first groupinfo,
							// we need to subtract 1 from the idx variable
					}
				}
			} else if (hasGroup()) {
				final int[] g = getGroupsInfoAt(index);
				if (g != null) {
					g[1]--;
					if (g[2] != -1) g[2]--;
					fixGroupIndex(index, -1, false);
				}
				else fixGroupIndex(index, -1, false);
				if (child instanceof Groupfoot){
					final int[] g1 = getGroupsInfoAt(index);	
					if(g1 != null){ // group info maybe remove cause of grouphead removed in previous op
						g1[2] = -1;
					}
				}
			}
			return true;
		}
		return false;
	}*/
	/** Callback if a child has been inserted.
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 */
	/**protected void afterInsert(Component comp) {
		updateVisibleCount((Row) comp, false);
		checkInvalidateForMoved(comp, false);
	}*/
	/** Callback if a child will be removed (not removed yet).
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 */
	/**protected void beforeRemove(Component comp) {
		updateVisibleCount((Row) comp, true);
		checkInvalidateForMoved(comp, true);
	}*/
	/**
	 * Update the number of the visible item before it is removed or after it is added.
	 */
	/**private void updateVisibleCount(Row row, boolean isRemove) {
		if (row instanceof Group || row.isVisible()) {
			final Group g = getGroup(row.getIndex());
			
			// We shall update the number of the visible item in the following cases.
			// 1) If the row is a type of Groupfoot, it is always shown.
			// 2) If the row is a type of Group, it is always shown.
			// 3) If the row doesn't belong to any group.
			// 4) If the group of the row is open.
			if (row instanceof Groupfoot || row instanceof Group || g == null || g.isOpen())
				addVisibleItemCount(isRemove ? -1 : 1);
			
			if (row instanceof Group) {
				final Group group = (Group) row;
				
				// If the previous group exists, we shall update the number of
				// the visible item from the number of the visible item of the current group.
				final Row preRow = (Row) row.getPreviousSibling();
				if (preRow == null) {
					if (!group.isOpen()) {
						addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
					}
				} else {
					final Group preGroup = preRow instanceof Group ? (Group) preRow : getGroup(preRow.getIndex());
					if (preGroup != null) {
						if (!preGroup.isOpen() && group.isOpen())
							addVisibleItemCount(isRemove ? -group.getVisibleItemCount() : group.getVisibleItemCount());
						else if (preGroup.isOpen() && !group.isOpen())
							addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
					} else {
						if (!group.isOpen())
							addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
					}
				}
			}
		}
		final Grid grid = getGrid();
		if (grid != null && grid.inPagingMold())
			grid.getPaginal().setTotalSize(getVisibleItemCount());
	}*/
	/** Checks whether to invalidate, when a child has been added or 
	 * or will be removed.
	 * @param bRemove if child will be removed
	 */
	/**private void checkInvalidateForMoved(Component child, boolean bRemove) {
		//No need to invalidate if
		//1) act == last and child in act
		//2) act != last and child after act
		//Except removing last elem which in act and act has only one elem
		final Grid grid = getGrid();
		if (grid != null && grid.inPagingMold() && !isInvalidated()) {
			final List children = getChildren();
			final int sz = children.size(),
				pgsz = grid.getPageSize();
			int n = sz - (grid.getActivePage() + 1) * pgsz;
			if (n <= 0) {//must be last page
				n += pgsz; //check in-act (otherwise, check after-act)
				if (bRemove && n <= 1) { //last elem, in act and remove
					invalidate();
					return;
				}
			} else if (n > 50)
				n = 50; //check at most 50 items (for better perf)

			for (ListIterator it = children.listIterator(sz);
			--n >= 0 && it.hasPrevious();)
				if (it.previous() == child)
					return; //no need to invalidate

			invalidate();
		}
	}*/

	/** Returns an iterator to iterate thru all visible children.
	 * Unlike {@link #getVisibleItemCount}, it handles only the direct children.
	 * Component developer only.
	 * @since 3.5.1
	 */
	/**public Iterator getVisibleChildrenIterator() {
		final Grid grid = getGrid();
		if (grid != null && grid.inSpecialMold())
			return grid.getDrawerEngine().getVisibleChildrenIterator();
		return new VisibleChildrenIterator();
	}*/
	/**
	 * An iterator used by visible children.
	 */
	/**private class VisibleChildrenIterator implements Iterator {
		private final ListIterator _it = getChildren().listIterator();
		private Grid _grid = getGrid();
		private int _count = 0;
		public boolean hasNext() {
			if (_grid == null || !_grid.inPagingMold()) return _it.hasNext();
			
			if (_count >= _grid.getPaginal().getPageSize()) {
				return false;
			}

			if (_count == 0) {
				final Paginal pgi = _grid.getPaginal();
				int begin = pgi.getActivePage() * pgi.getPageSize();
				for (int i = 0; i < begin && _it.hasNext();) {
					getVisibleRow((Row)_it.next());
					i++;
				}
			}
			return _it.hasNext();
		}
		private Row getVisibleRow(Row row) {
			if (row instanceof Group) {
				final Group g = (Group) row;
				if (!g.isOpen()) {
					for (int j = 0, len = g.getItemCount(); j < len
							&& _it.hasNext(); j++)
						_it.next();
				}
			}
			while (!row.isVisible())
				row = (Row)_it.next();
			return row;
		}
		public Object next() {
			if (_grid == null || !_grid.inPagingMold()) return _it.next();
			_count++;
			final Row row = (Row)_it.next();
			return _it.hasNext() ? getVisibleRow(row) : row;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}*/
});

(_zkwg=_zkpk.Rows).prototype.className='zul.grid.Rows';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<tbody', this.domAttrs_() , '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
		
	out.push('</tbody>');	
}

zkmld(_zkwg,_zkmd);
zul.grid.Foot = zk.$extends(zul.Widget, {
	getGrid: function () {
		return this.parent;
	},
	getZclass: function () {
		return this._zclass == null ? "z-foot" : _zclass;
	}
});
(_zkwg=_zkpk.Foot).prototype.className='zul.grid.Foot';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<tr', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</tr>');
}

zkmld(_zkwg,_zkmd);
zul.grid.Footer = zk.$extends(zul.LabelImageWidget, {
	_span: 1,
	
	getGrid: function () {
		return this.parent ? this.parent.parent : null;
	},
	getColumn: function () {
		var grid = this.getGrid();
		if (grid) {
			var cs = grid.columns;
			if (cs)
				return cs.getChildAt(this.getChildIndex());
		}
		return null;
	},
	getSpan: function () {
		return this._span;
	},
	setSpan: function (span) {
		if (this._span != span) {
			this._span = span;
			var n = this.getNode();
			if (n) n.colspan = span;
		}
	},
	getZclass: function () {
		return this._zclass == null ? "z-footer" : this._zclass;
	}
});
(_zkwg=_zkpk.Footer).prototype.className='zul.grid.Footer';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<td', this.domAttrs_(), '><div id="', this.uuid,
		'$cave" class="', this.getZclass(), '">', this.domContent_());
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div></td>');
}

zkmld(_zkwg,_zkmd);
zul.grid.Paging = zk.$extends(zul.Widget, {
	_pgsz: 20,
	_ttsz: 0,
	_npg: 1,
	_actpg: 0,
	_pginc: 10,
	_autohide: 10,
	replaceHTML: function () {
		if (this.isBothPaging())
			this.parent.rerender();
		else
			this.$supers('replaceHTML', arguments);
	},
	isBothPaging: function () {
		return this.parent && this.parent.getPagingPosition
					&& "both" == this.parent.getPagingPosition();
	},
	getPageSize: function () {
		return this._pgsz;
	},
	setPageSize: function (size) {
		if (this._pgsz != size) {
			this._pgsz = size;
			this._updatePageNum();
			// TODO this.fire('onPagingImpl', this._actpg);
		}
	},
	getTotalSize: function () {
		return this._ttsz;
	},
	setTotalSize: function (size) {
		if (this._ttsz != size) {
			this._ttsz = size;
			this._updatePageNum();
			if (this._detailed) rerender();
		}
	},
	_updatePageNum: function () {
		var v = Math.floor((this._ttsz - 1) / this._pgsz + 1);
		if (v == 0) v = 1;
		if (v != this._npg) {
			this._npg = v;
			if (this._actpg >= this._npg)
				this._actpg = this._npg - 1;
		}
	},
	getPageCount: function () {
		return this._npg;
	},
	setPageCount: function (npg) {
		this._npg = npg;
	},
	getActivePage: function () {
		return this._actpg;
	},
	setActivePage: function (pg) {
		if (this._actpg != pg) {
			this._actpg = pg;
			// TODO this.fire('onPagingImpl', this._actpg);
		}
	},
	getPageIncrement: function () {
		return this._pginc;
	},
	setPageIncrement: function (pginc) {
		if (_pginc != pginc) {
			_pginc = pginc;
			this.rerender();
		}
	},
	isDetailed: function () {
		return this._detailed;
	},
	setDetailed: function (detailed) {
		if (this._detailed != detailed) {
			this._detailed = detailed;
			this.rerender();
		}
	},
	isAutohide: function () {
		return this._autohide;
	},
	setAutohide: function (autohide) {
		if (this._autohide != autohide) {
			this._autohide = autohide;
			if (this._npg == 1) this.rerender();
		}
	},
	_infoTags: function () {
		if (this._ttsz == 0)
			return "";
		var lastItem = (this._actpg+1) * this._pgsz,
			out = [];
		out.push('<div class="', this.getZclass(), '-info">[ ', lastItem + 1,
				' - ', lastItem > this._ttsz ? this._ttsz : lastItem, ' / ',
				this._ttsz, ' ]</div>');
		return out.join('');
	},
	_innerTags: function () {
		var out = [];

		var half = this._pginc / 2,
			begin, end = this._actpg + half - 1;
		if (end >= this._npg) {
			end = this._npg - 1;
			begin = end - this._pginc + 1;
			if (begin < 0) begin = 0;
		} else {
			begin = this._actpg - half;
			if (begin < 0) begin = 0;
			end = begin + this._pginc - 1;
			if (end >= this._npg) end = this._npg - 1;
		}
		var zcs = this.getZclass();
		if (this._actpg > 0) {
			if (begin > 0) //show first
				this.appendAnchor(zcs, out, msgzul.FIRST, 0);
			this.appendAnchor(zcs, out, msgzul.PREV, this._actpg - 1);
		}

		var bNext = this._actpg < this._npg - 1;
		for (; begin <= end; ++begin) {
			if (begin == this._actpg) {
				this.appendAnchor(zcs, out, begin + 1, begin, true);
			} else {
				this.appendAnchor(zcs, out, begin + 1, begin);
			}
		}

		if (bNext) {
			this.appendAnchor(zcs, out, msgzul.NEXT, this._actpg + 1);
			if (end < this._npg - 1) //show last
				this.appendAnchor(zcs, out, msgzul.LAST, this._npg - 1);
		}
		if (this._detailed)
			out.push('<span>[', this._actpg * this._pgsz + 1, '/', this._ttsz, "]</span>");
		return out.join('');
	},
	appendAnchor: function (zclass, out, label, val, seld) {
		zclass += "-cnt" + (seld ? " " + zclass + "-seld" : "");
		out.push('<a class="', zclass, '" href="javascript:;" onclick="zul.grid.Paging.go(this,',
				val, ')">', label, '</a>&nbsp;');
	},
	getZclass: function () {
		var added = "os" == this.getMold() ? "-os" : "";
		return this._zclass == null ? "z-paging" + added : this._zclass;
	},
	isVisible: function () {
		var visible = this.$supers('isVisible', arguments);
		return visible && (this._npg > 1 || !this._autohide);
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.getMold() == "os") return;
		var uuid = this.uuid,
			inputs = zDom.$$(uuid, 'real'),
			zcls = this.getZclass(),
			$Paging = this.$class;
			
		for (var i = inputs.length; --i>=0;) {
			zEvt.listen(inputs[i], "keydown", $Paging._doKeyDown);
			zEvt.listen(inputs[i], "blur", $Paging._doBlur);
		}
		
		for (var postfix = ['first', 'prev', 'last', 'next'], k = postfix.length; --k >=0; ) {
			var btn = zDom.$$(uuid, postfix[k]);
			for (var j = btn.length; --j>=0;) {
				zEvt.listen(btn[j], "mouseover", $Paging._doMouseOver);
				zEvt.listen(btn[j], "mouseout", $Paging._doMouseOut);
				zEvt.listen(btn[j], "mousedown", $Paging._doMouseDown);
				zEvt.listen(btn[j], "click", $Paging['_do' + postfix[k] + 'Click']);
				if (this._npg == 1)
					zDom.addClass(btn[j], zcls + "-btn-disd");
				else if (postfix[k] == 'first' || postfix[k] == 'prev') {
					if (this._actpg == 0) zDom.addClass(btn[j], zcls + "-btn-disd");
				} else if (this._actpg == this._npg - 1) {
					zDom.addClass(btn[j], zcls + "-btn-disd");
				}
			}
		}
	},
	unbind_: function () {
		if (this.getMold() != "os") {
			var uuid = this.uuid, inputs = zDom.$$(uuid, 'real'), $Paging = this.$class;
			
			for (var i = inputs.length; --i >= 0;) {
				zEvt.unlisten(inputs[i], "keydown", $Paging._doKeyDown);
				zEvt.unlisten(inputs[i], "blur", $Paging._doBlur);
			}
			
			for (var postfix = ['first', 'prev', 'last', 'next'], k = postfix.length; --k >= 0;) {
				var btn = zDom.$$(uuid, postfix[k]);
				for (var j = btn.length; --j >= 0;) {
					zEvt.unlisten(btn[j], "mouseover", $Paging._doMouseOver);
					zEvt.unlisten(btn[j], "mouseout", $Paging._doMouseOut);
					zEvt.unlisten(btn[j], "mousedown", $Paging._doMouseDown);
					zEvt.unlisten(btn[j], "click", $Paging['_do' + postfix[k] + 'Click']);
				}
			}
		}
		this.$supers('unbind_', arguments);
	}
}, {
	go: function (anc, pgno) {
		var wgt = zk.Widget.isInstance(anc) ? anc : zk.Widget.$(anc);
		if (wgt && wgt.getActivePage() != pgno)
			wgt.fire('onPaging', pgno);
	},
	_doKeyDown: function (evt) {
		if (!evt) evt = window.event;
		var inp = zEvt.target(evt),
			wgt = zk.Widget.$(inp);
		if (inp.disabled || inp.readOnly)
			return;
	
		var code =zEvt.keyCode(evt);
		switch(code){
		case 48:case 96://0
		case 49:case 97://1
		case 50:case 98://2
		case 51:case 99://3	
		case 52:case 100://4
		case 53:case 101://5
		case 54:case 102://6
		case 55:case 103://7
		case 56:case 104://8
		case 57:case 105://9
			break;		
		case 37://left
			break;		
		case 38: case 33: //up, PageUp
			wgt.$class._increase(inp, wgt, 1);
			zEvt.stop(evt);
			break;
		case 39://right
			break;		
		case 40: case 34: //down, PageDown
			wgt.$class._increase(inp, wgt, -1);
			zEvt.stop(evt);
			break;
		case 36://home
			wgt.$class.go(wgt,0);
			zEvt.stop(evt);
			break;
		case 35://end
			wgt.$class.go(wgt, wgt._npg - 1);
			zEvt.stop(evt);
			break;
		case 9: case 8: case 46: //tab, backspace, delete 
			break;
		case 13: //enter
			wgt.$class._increase(inp, wgt, 0);
			wgt.$class.go(wgt, inp.value-1);
			zEvt.stop(evt);
			break;
		default:
			if (!(code >= 112 && code <= 123) //F1-F12
			&& !evt.ctrlKey && !evt.altKey)
				zEvt.stop(evt);
		}
	},
	_doBlur: function (evt) {
		if (!evt) evt = window.event;
		var inp = zEvt.target(evt),
			wgt = zk.Widget.$(inp);
		if (inp.disabled || inp.readOnly)
			return;
		
		wgt.$class._increase(inp, wgt, 0);
		wgt.$class.go(wgt, inp.value-1);
		zEvt.stop(evt);
	},
	_increase: function (inp, wgt, add){
		var value = zk.parseInt(inp.value);
		value += add;
		if (value < 1) value = 1;
		else if (value > wgt._npg) value = wgt._npg;
		inp.value = value;
	},
	_dofirstClick: function (evt) {
		if (!evt) evt = window.event;
		var wgt = zk.Widget.$(evt),
			zcls = wgt.getZclass();
		
		if (wgt.getActivePage() != 0) {
			wgt.$class.go(wgt, 0);
			var uuid = wgt.uuid;
			for (var postfix = ['first', 'prev'], k = postfix.length; --k >= 0;)
				for (var btn = zDom.$$(uuid, postfix[k]), i = btn.length; --i >= 0;)
					zDom.addClass(btn[i], zcls + "-btn-disd");
		}
	},
	_doprevClick: function (evt) {		
		if (!evt) evt = window.event;
		var wgt = zk.Widget.$(evt),
			ap = wgt.getActivePage(),
			zcls = wgt.getZclass();
		
		if (ap > 0) {
			wgt.$class.go(wgt, ap - 1);
			if (ap - 1 == 0) {
				var uuid = wgt.uuid;
				for (var postfix = ['first', 'prev'], k = postfix.length; --k >= 0;)
					for (var btn = zDom.$$(uuid, postfix[k]), i = btn.length; --i >= 0;)
						zDom.addClass(btn[i], zcls + "-btn-disd");
			}
		}
	},
	_donextClick: function (evt) {
		if (!evt) evt = window.event;
		var wgt = zk.Widget.$(evt),
			ap = wgt.getActivePage(),
			pc = wgt.getPageCount(),
			zcls = wgt.getZclass();
		
		if (ap < pc - 1) {
			wgt.$class.go(wgt, ap + 1);
			if (ap + 1 == pc - 1) {
				var uuid = wgt.uuid;
				for (var postfix = ['last', 'next'], k = postfix.length; --k >= 0;)
					for (var btn = zDom.$$(uuid, postfix[k]), i = btn.length; --i >= 0;)
						zDom.addClass(btn[i], zcls + "-btn-disd");
			}
		}
	},
	_dolastClick: function (evt) {
		if (!evt) evt = window.event;
		var wgt = zk.Widget.$(evt),
			pc = wgt.getPageCount(),
			zcls = wgt.getZclass();
		
		if (wgt.getActivePage() < pc - 1) {
			wgt.$class.go(wgt, pc - 1);
			var uuid = wgt.uuid;
			for (var postfix = ['last', 'next'], k = postfix.length; --k >= 0;)
				for (var btn = zDom.$$(uuid, postfix[k]), i = btn.length; --i >= 0;)
					zDom.addClass(btn[i], zcls + "-btn-disd");
		}
		
	},
	_doMouseOver: function (evt) {
		if (!evt) evt = window.event;
		var target = zEvt.target(evt),
			table = zDom.parentByTag(target, "TABLE"),
			zcls = zk.Widget.$(target).getZclass();
		if (!zDom.hasClass(table, zcls + "-btn-disd")) 
			zDom.addClass(table, zcls + "-btn-over");
	},
	_doMouseOut: function (evt) {
		if (!evt) evt = window.event;
		var target = zEvt.target(evt),
			table = zDom.parentByTag(target, "TABLE"),
			wgt = zk.Widget.$(target);
		zDom.rmClass(table, wgt.getZclass() + "-btn-over");
	},
	_doMouseDown: function (evt) {		
		if (!evt) evt = window.event;
		var target = zEvt.target(evt),
			table = zDom.parentByTag(target, "TABLE"),
			wgt = zk.Widget.$(target),
			zcls = wgt.getZclass();
		if (zDom.hasClass(table, zcls + "-btn-disd")) return;
		
		zDom.addClass(table, zcls + "-btn-clk");
		wgt.$class._downbtn = table;
		zEvt.listen(document.body, "mouseup", wgt.$class._doMouseUp);
	},
	_doMouseUp: function (evt) {
		if (!evt) evt = window.event;
		if (zul.grid.Paging._downbtn) {
			var zcls = zk.Widget.$(zul.grid.Paging._downbtn).getZclass();
			zDom.rmClass(zul.grid.Paging._downbtn, zcls + "-btn-clk");
		}
		zul.grid.Paging._downbtn = null;
		zEvt.unlisten(document.body, "mouseup", zul.grid.Paging._doMouseUp);
	}
});

(_zkwg=_zkpk.Paging).prototype.className='zul.grid.Paging';_zkmd={};
_zkmd['os']=
function (out) {
	if (this.getMold() == "os") {
		out.push('<div', this.domAttrs_(), '>', this._innerTags(), '</div>');
		return;
	}
	var uuid = this.uuid,
		zcls = this.getZclass();
	out.push('<div name="', uuid, '"', this.domAttrs_(), '>', '<table', zUtl.cellps0,
			'><tbody><tr><td><table id="', uuid, '$first" name="', uuid, '$first"',
			zUtl.cellps0, ' class="', zcls, '-btn"><tbody><tr>',
			'<td class="', zcls, '-btn-l"><i>&#160;</i></td>',
			'<td class="', zcls, '-btn-m"><em unselectable="on">',
			'<button type="button" class="', zcls, '-first"> </button></em></td>',
			'<td class="', zcls, '-btn-r"><i>&#160;</i></td></tr></tbody></table></td>',
			'<td><table id="', uuid, '$prev" name="', uuid, '$prev"', zUtl.cellps0,
			' class="', zcls, '-btn"><tbody><tr><td class="', zcls, '-btn-l"><i>&#160;</i></td>',
			'<td class="', zcls, '-btn-m"><em unselectable="on"><button type="button" class="',
			zcls, '-prev"> </button></em></td><td class="', zcls, '-btn-r"><i>&#160;</i></td>',
			'</tr></tbody></table></td><td><span class="', zcls, '-sep"/></td>',
			'<td><span class="', zcls, '-text"></span></td><td><input id="',
			uuid, '$real" name="', uuid, '$real" type="text" class="', zcls,
			'-inp" value="', this.getActivePage() + 1, '" size="3"/></td>',
			'<td><span class="', zcls, '-text">/ ', this.getPageCount(), '</span></td>',
			'<td><span class="', zcls, '-sep"/></td><td><table id="', uuid,
			'$next" name="', uuid, '$next"', zUtl.cellps0, ' class="', zcls, '-btn">',
			'<tbody><tr><td class="', zcls, '-btn-l"><i>&#160;</i></td><td class="',
			zcls, '-btn-m"><em unselectable="on"><button type="button" class="',
			zcls, '-next"> </button></em></td><td class="', zcls, '-btn-r"><i>&#160;</i></td>',
			'</tr></tbody></table></td><td><table id="', uuid, '$last" name="',
			uuid, '$last"', zUtl.cellps0, ' class="', zcls, '-btn"><tbody><tr>',
			'<td class="', zcls, '-btn-l"><i>&#160;</i></td><td class="', zcls,
			'-btn-m"><em unselectable="on"><button type="button" class="', zcls,
			'-last"> </button></em></td><td class="', zcls, '-btn-r"><i>&#160;</i></td>',
			'</tr></tbody></table></td></tr></tbody></table>');
			
	if (this.isDetailed()) out.push(this._infoTags());
	out.push('</div>');
}

_zkmd['default']=[_zkpk.Paging,'os'];zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}