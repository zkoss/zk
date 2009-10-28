/* grid.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Mon Jan  9 17:45:32     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.zul");

////
zk.Grid = zClass.create();
zk.Grid.prototype = {
	initialize: function (comp) {
		this.id = comp.id;
		zkau.setMeta(comp, this);
		this.init();
	},
	init: function () {
		this.element = $e(this.id);
		if (!this.element) return;
		if (getZKAttr(this.element, "vflex") == "true") {
			if (zk.ie) this.element.style.overflow = "hidden";
			// added by Jumper for IE to get a correct offsetHeight so we need
			// to add this command faster than the this._calcSize() function.
			var hgh = this.element.style.height;
			if (!hgh || hgh == "auto") this.element.style.height = "99%"; // avoid border 1px;
		}

		this.body = $e(this.id + "!body");
		this.paging = getZKAttr(this.element, "pg") != null;
		if (this.body) {
			this.bodytbl = zk.firstChild(this.body, "TABLE", true);

			this.head = $e(this.id + "!head");
			this.foot = $e(this.id + "!foot");
			if (this.foot) this.foottbl = zk.firstChild(this.foot, "TABLE", true);
			if (this.head) {
				this.headtbl = zk.firstChild(this.head, "TABLE", true);
				this.headrows = this.headtbl.tBodies[this.headtbl.tBodies.length-1].rows;
				this.hdfaker = this.headtbl.tBodies[0].rows[0]; // head's faker
				this.bdfaker = this.bodytbl.tBodies[0].rows[0]; // body's faker
				if (this.foot) this.ftfaker = this.foottbl.tBodies[0].rows[0]; // foot's faker
			}

			if (this.bodytbl.tBodies && this.bodytbl.tBodies[this.head ? 1 : 0])
				this.bodyrows = this.bodytbl.tBodies[this.head ? 1 : 0].rows;
				//Note: bodyrows is null in FF if no rows, so no err msg
		}

		var meta = this; //the nested function only see local var

		//FF: a small fragment is shown
		//IE: Bug 1775014
		if (this.headtbl && this.headrows.length) {
			var empty = true;
			l_out:
			for (var j = this.headrows.length; j;) {
				var headrow = this.headrows[--j];
				for (var k = headrow.cells.length; k;) {
					var n = headrow.cells[--k].firstChild; // Bug #1819037
					for (n = n ? n.firstChild: n; n; n = n.nextSibling)
						if (!n.id || (!n.id.endsWith("!hint") && !n.id.endsWith("!btn"))) {
							empty = false;
							break l_out;
						}
				}
			}
			if (empty) this.head.style.display = "none"; // Bug #1819037, #1970048
			//we have to hide if empty (otherwise, a small block is shown)
			else this.head.style.display = "";// Bug #1832359

		}

		this.body.onscroll = function () {
			if (meta.head) meta.head.scrollLeft = meta.body.scrollLeft;
			if (meta.foot) meta.foot.scrollLeft = meta.body.scrollLeft;
			if (!meta.paging) meta._render(zk.gecko ? 200: 60);
				//Moz has a bug to send the request out if we don't wait long enough
				//How long is enough is unknown, but 200 seems fine
		};
	},
	/** Returns the size for vflex
	 */
	_vflexSize: function () {
		if (zk.ie6Only) {
			// ie6 must reset the height of the element,
			// otherwise its offsetHeight might be wrong.
			var hgh = this.element.style.height;
			this.element.style.height = "";
			this.element.style.height = hgh;
		}
		return this.element.offsetHeight - 2 - (this.head ? this.head.offsetHeight : 0)
			- (this.foot ? this.foot.offsetHeight : 0); // Bug #1815882 and Bug #1835369
	},
	/* set the height. */
	setHgh: function (hgh) {
		if (getZKAttr(this.element, "vflex") == "true" || (hgh && hgh != "auto" && hgh.indexOf('%') < 0)) {
			var h =  this._vflexSize();
			if (this.paging) {
				var pgit = $e(this.id + "!pgit"), pgib = $e(this.id + "!pgib");
				if (pgit) h -= pgit.offsetHeight;
				if (pgib) h -= pgib.offsetHeight;
			}
			if (h < 0) h = 0;

			this.body.style.height = h + "px";

			//2007/12/20 We don't need to invoke the body.offsetHeight to avoid a performance issue for FF.
			if (zk.ie && this.body.offsetHeight) {} // bug #1812001.
			// note: we have to invoke the body.offestHeight to resolve the scrollbar disappearing in IE6
			// and IE7 at initializing phase.
		} else {
			//Bug 1556099: it is strange if we ever check the value of
			//body.offsetWidth. The grid's body's height is 0 if init called
			//after grid become visible (due to opening an accordion tab)
			this.body.style.height = "";
			this.element.style.height = hgh;
		}
	},
	/* set the size*/
	updSize: function () {
		var hgh = this.element.style.height;
		this.setHgh(hgh);
		//Bug 1553937: wrong sibling location
		//Otherwise,
		//IE: element's width will be extended to fit body
		//FF and IE: sometime a horizontal scrollbar appear (though it shalln't)
		//note: we don't solve this bug for paging yet
		var wd = this.element.style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			wd = zk.revisedSize(this.element, this.element.offsetWidth);
			if (wd < 0) wd = 0;
			if (wd) wd += "px";
		}
		if (wd) {
			this.body.style.width = wd;
			if (this.head) this.head.style.width = wd;
			if (this.foot) this.foot.style.width = wd;
		}
	},
	_beforeSize: function () {
		var wd = this.element.style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			if (this.body) this.body.style.width = "";
			if (this.head) this.head.style.width = "";
			if (this.foot) this.foot.style.width = "";
		}
	},
	cleanup: function () {
		this.element = this.body = this.bodytbl = this.bodyrows
			= this.head = this.headtbl = this.foot = this.foottbl = null;
			//in case: GC not works properly
	},

	/** Stripes the rows. */
	stripe: function () {
		var scOdd = getZKAttr(this.element, "scOddRow");
		if (!scOdd || !this.bodyrows) return;

		for (var j = 0, even = true, bl = this.bodyrows.length; j < bl; ++j) {
			var row = this.bodyrows[j];
			if ($visible(row) && getZKAttr(row, "nostripe") != "true") {
				zk.addClass(row, scOdd, !even);
				zk.fire(row, "stripe");
				even = !even;
			}
		}
	},

	/** Calculates the size. */
	_calcSize: function () {
		this.updSize();
			//Bug 1659601: we cannot do it in init(); or, IE failed!
		var tblwd = this.body.clientWidth;
		if (zk.ie) {//By experimental: see zk-blog.txt
			if (this.headtbl && this.headtbl.offsetWidth != this.bodytbl.offsetWidth) 
				this.bodytbl.style.width = ""; //reset
			if (tblwd && this.body.offsetWidth == this.bodytbl.offsetWidth && this.body.offsetWidth - tblwd > 11) { //scrollbar
				if (--tblwd < 0) 
					tblwd = 0;
				this.bodytbl.style.width = tblwd + "px";
			}
			
			// bug #2799258
			var hgh = this.element.style.height;
			if (!hgh || hgh == "auto") {
				hgh = this.body.offsetWidth - this.body.clientWidth;
				if (this.bodyrows && this.bodyrows.length && hgh > 11) 
					this.body.style.height = this.body.offsetHeight + zk.getScrollBarWidth() + "px";
					
				// resync
				tblwd = this.body.clientWidth;
			}
		}
		if (this.headtbl) {
			if (tblwd) this.head.style.width = tblwd + 'px';
			if (getZKAttr(this.element, "fixed") != "true")
				zul.adjustHeadWidth(this.hdfaker, this.bdfaker, this.ftfaker, this.bodyrows);
			else if (tblwd && this.foot) this.foot.style.width = tblwd + 'px';
		} else if (this.foottbl) {
			if (tblwd) this.foot.style.width = tblwd + 'px';
			if (this.foottbl.rows.length)
				zk.cpCellWidth(this.foottbl.rows[0], this.bodyrows, this); //assign foot's col width
		}
	},
	/** Resize the specified column.
	 * @param cmp columns
	 */
	resizeCol: function (cmp, icol, col, wd, keys) {
		if (this.bodyrows)
			zulHdr.resizeAll(this, cmp, icol, col, wd, keys);
	},
	/** set attribute.
	 */
	setAttr: function (nm, val) {
		switch (nm) {
		case "z.innerWidth":
			if (this.headtbl) this.headtbl.style.width = val;
			if (this.bodytbl) this.bodytbl.style.width = val;
			if (this.foottbl) this.foottbl.style.width = val;
			return true;
		case "style.height":
			this.element.style.height = val;
			if (zk.ie6Only && this.body) this.body.style.height = val;
				// IE6 cannot shrink its height, we have to specify this.body's height to equal the element's height.
			this.setHgh(val);
			this._recalcSize();
			return true;
		case "style.width":
			if (this.headtbl) this.headtbl.style.width = "";
			if (this.foottbl) this.foottbl.style.width = "";
		case "style":
			zkau.setAttr(this.element, nm, val);
			this._recalcSize();
			return true;
		case "z.scOddRow":
			zkau.setAttr(this.element, nm, val);
			this.stripe();
			return true;
		case "z.render":
			this._render(0);
			return true;
		case "scrollTop":
			if (this.body) {
				this.body.scrollTop = val;
				return true;
			}
			break;
		case "scrollLeft":
			if (this.body) {
				this.body.scrollLeft = val;
				return true;
			}
		case "z.vflex":
			if (val == "true") {
				if (zk.ie) this.element.style.overflow = "hidden";
				// added by Jumper for IE to get a correct offsetHeight so we need
				// to add this command faster than the this._calcSize() function.
				var hgh = this.element.style.height;
				if (!hgh || hgh == "auto") this.element.style.height = "99%"; // avoid border 1px;
			} else if (zk.ie) {
				this.element.style.overflow = ""; // cleanup style
			}
			zkau.setAttr(this.element, nm, val);
			this._recalcSize();
			return true;
		}
		return false;
	},

	/** Renders listitems that become visible by scrolling.
	 */
	_render: function (timeout) {
		if(!this.paging || getZKAttr(this.element, "hasgroup"))
			setTimeout("zkGrid._renderNow('"+this.id+"')", timeout);
	},
	_renderNow: function () {
		var rows = this.bodyrows;
		if (!rows || !rows.length || getZKAttr(this.element, "model") != "true") return;

		//Note: we have to calculate from top to bottom because each row's
		//height might diff (due to different content)
		var data = "";
		var min = this.body.scrollTop, max = min + this.body.offsetHeight;
		for (var j = 0, rl = rows.length; j < rl; ++j) {
			var r = rows[j];
			if ($visible(r)) {
				var top = zk.offsetTop(r);
				if (top + zk.offsetHeight(r) < min) continue;
				if (top > max) break; //Bug 1822517
				if (getZKAttr(r, "loaded") != "true")
					data += "," + r.id;
				else if (getZKAttr(r, "inited") != "true") zk.initAt(r);
			}
		}
		if (data) {
			data = data.substring(1);
			zkau.send({uuid: this.id, cmd: "onRender", data: [data]}, 0);
		}
	},
	_recalcSize: function () {
		if (zk.isRealVisible(this.element)) {
			this._calcSize();// Bug #1813722
			this._render(155);
			// it seems no longer to be fixed with this, commented by jumperchen on 2009/05/26
			if (zk.ie7 && this.paging) zk.redoCSS(this.element); // Bug 2096807 && Test Case Z35-grid-0102.zul
		}
	}
};

////
// Grid //
zkGrid = {};
/** Init (and re-init) a grid. */
zkGrid.init = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
	else new zk.Grid(cmp);
};
/** Called when a grid becomes visible because of its parent. */
zkGrid.childchg = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) {
		meta.init(); // sometimes, the reference of the element has been out of date
		meta._recalcSize();
	}
} ;
zkGrid.onVisi = zkGrid.onSize = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta._recalcSize();
};
zkGrid.beforeSize = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta._beforeSize();
};
zkGrid.stripe = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta.stripe();
};

/** Handles setAttr. */
zkGrid.setAttr = function (grid, nm, val) {
	var meta = zkau.getMeta(grid);
	return meta && meta.setAttr(nm, val);
};

zkGrid._renderNow = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta._renderNow();
};
zkGrw = {}; //Row
zkGrw.init = function (cmp) {
	setZKAttr(cmp, "inited", "true");
	zkGrw.stripe(cmp);
};
zkGrw.initdrag = function (cmp) {
	if (zk.gecko) {
		zk.listen(cmp, "mouseover", zkGrw.ondragover);
		zk.listen(cmp, "mouseout",  zkGrw.ondragout);
	}
};
zkGrw.cleandrag = function (cmp) {
	if (zk.gecko) {
		zk.unlisten(cmp, "mouseover", zkGrw.ondragover);
		zk.unlisten(cmp, "mouseout",  zkGrw.ondragout);
	}
};
zkGrw.ondragover = function (evt) {
	var target = Event.element(evt);
	var tag = $tag(target);
	if (tag != "INPUT" && tag != "TEXTAREA") {
		var p = $parentByType(target, "Gcl");
		if (p) p.firstChild.style.MozUserSelect = "none";
	}
};
zkGrw.ondragout = function (evt) {
	var target = Event.element(evt);
	var p = $parentByType(target, "Gcl");
	if (p) p.firstChild.style.MozUserSelect = "";
};
zkGrw.cleanup = function (cmp) {
	zkGrw.stripe(cmp, true);
};
zkGrw.setAttr = function (cmp, nm, val) {
	if (nm == "visibility") { // Bug #1836257
		var meta = zkau.getMeta(getZKAttr(cmp, "rid"));
		if (meta) {
			if (!meta.fixedStripe) meta.fixedStripe = function () {meta.stripe();};
			setTimeout(meta.fixedStripe, 0);
		}
	}
	return false;
};
zkGrw.stripe = function (cmp, isClean) {
	var meta = zkau.getMeta(getZKAttr(cmp, "rid"));
	if (meta) {
		if (!meta.fixedStripe) meta.fixedStripe = function () {meta.stripe();};
		if (isClean) zk.addCleanupLater(meta.fixedStripe, false, meta.id + "Grw");
		else zk.addInitLater(meta.fixedStripe, false, meta.id + "Grw");
	}
};
zkGcl = {}; //cell
zk.addBeforeInit(function () {
	//Column
	//init it later because zul.js might not be loaded yet
	zkCol = {}
	Object.extend(zkCol, zulHdr);

	/** Resize the column. */
	zkCol.resize = function (col1, icol, wd1, keys) {
		var grid = getZKAttr(col1.parentNode, "rid");
		if (grid) {
			var meta = zkau.getMeta(grid);
			if (meta)
				meta.resizeCol(
					$parentByType(col1, "Cols"), icol, col1, wd1, keys);
		}
	};

	/** since 3.5.0 */
	zk.copy(zkCol, {
		_zkColinit: zkCol.init,
		init: function (cmp) {
			zkCol._zkColinit(cmp);
			var pp = getZKAttr(cmp.parentNode, "mpop");
				zk.listen(cmp, "mouseover", zkCol.onHdOver);
				zk.listen(cmp, "mouseout", zkCol.onHdOut);
			if (pp != "zk_n_a") {
				var btn = $e(cmp, "btn");
				if (btn) {
					zk.listen(btn, "click", zkCol.onMenuClick);

					// zid might not be ready yet.
					zk.addInitLater(function () {
						var mpop = $e(pp);
						if (!mpop)
							mpop = zkau.getByZid(cmp.parentNode, pp);
						if (mpop) {
							if (getZKAttr(mpop, "autocreate") == "true" &&
								getZKAttr(cmp.parentNode, "columnshide") != "true" &&
								getZKAttr(cmp, "asc") != "true" &&
								getZKAttr(cmp, "dsc") != "true")
									zk.remove($e(cmp.id + "!btn"));
							zk.on(mpop, "close", zkCol.onMenuClose);
							zk.on(mpop, "onOuter", zkCol.onMenuOuter);
						}
					});
				}
			}
		},
		onMenuOuter: function (mpop) {
			zk.on(mpop, "close", zkCol.onMenuClose);
		},
		onMenuClick: function (evt) {
			if (!evt) evt = window.event;
			var cmp = $parentByType(Event.element(evt), "Col"),
				pp = zkau.getByZid(cmp.parentNode, getZKAttr(cmp.parentNode, "mpop")),
				btn = $e(cmp, "btn");
			if (!pp) return;
			zk.addClass(cmp, getZKAttr(cmp, "zcls") + "-visi");

			if (getZKAttr(pp, "autocreate") == "true") {
				var group = getZKAttr(cmp.parentNode, "columnsgroup") == "true",
					asc = getZKAttr(cmp, "asc") == "true",
					dsc = getZKAttr(cmp, "dsc") == "true",
					ul = $e(pp, "cave");
				if (ul) {
					var li = zk.firstChild(ul, "LI");
					if (group) {
						li.style.display = asc || dsc ? "" : "none";
						li = zk.nextSibling(li, "LI");
					}
					if (li) li.style.display = asc ? "" : "none";
					li = zk.nextSibling(li, "LI");
					if (li) li.style.display = dsc ? "" : "none";

					//separator
					li = zk.nextSibling(li, "LI");
					if (li) li.style.display = (asc||dsc) ? "" : "none";

				}
			}

			pp.style.position = "absolute";
			zk.setVParent(pp);
			zk.position(pp, btn, "after_start");
			var xy = zk.revisedOffset(cmp), t = $int(pp.style.top);
			if (xy[1] < t) pp.style.top = t - 4 + "px";

			zkMpop2.context(pp, cmp);
			setZKAttr(pp, "menuId", cmp.id);
			Event.stop(evt); // avoid onSort event.
		},
		onMenuClose: function (pp) {
			var cmp = $e(getZKAttr(pp, "menuId")),
				zcls = getZKAttr(cmp, "zcls");
			zk.rmClass(cmp, zcls + "-visi");
			zk.rmClass(cmp, zcls + "-over");
			return false; // stop event propagation
		},
		onHdOver: function (evt) {
			if (!evt) evt = window.event;
			var cmp = $parentByType(Event.element(evt), "Col"),
				btn = $e(cmp, "btn");
			zk.addClass(cmp, getZKAttr(cmp, "zcls") + "-over");
			if (btn) btn.style.height = cmp.offsetHeight - 1 + "px";
		},
		onHdOut: function (evt) {
			if (!evt) evt = window.event;
			var cmp = $parentByType(Event.element(evt), "Col"),
				zcls = getZKAttr(cmp, "zcls");
			if (!zk.hasClass(cmp, zcls + "-visi") &&
				(!zk.ie || !zk.isAncestor(cmp, evt.relatedTarget || evt.toElement)))
				zk.rmClass(cmp, zcls + "-over");
		}
	});
	//Columns
	zkCols = zulHdrs;
});

/** Grid Group*/
zkGrwgp = {
	init: function (cmp) {
		setZKAttr(cmp, "inited", "true");
		cmp._img = zk.firstChild(cmp, "IMG", true);
		if (cmp._img) zk.listen(cmp._img, "click", zkGrwgp.ontoggle);
		var table = cmp.parentNode.parentNode;
		if (table.tBodies.length > 1) {
			var span = 0;
			for (var row = table.rows[0], i = row.cells.length; --i >=0;)
				if(zk.isVisible(row.cells[i])) span++;
			for (var cells = cmp.cells, i = cells.length; --i >= 0;)
				span -= cells[i].colSpan;
			if (span > 0 && cmp.cells.length) cmp.cells[cmp.cells.length - 1].colSpan += span;
		}
	},
	ontoggle: function (evt) {
		if (!evt) evt = window.event;
		var target = Event.element(evt),
			row = zk.parentNode(target, "TR");
		if (!row) return; //incomplete structure

		var meta = zkau.getMeta(getZKAttr(row, "rid")),
			toOpen = !zkGrwgp.isOpen(row); //toggle
		zkGrwgp._openItem(row, toOpen);

		if (toOpen && meta || getZKAttr(meta.element, "model") == "true") {
			if (toOpen) meta.stripe();
			if (!meta.paging) meta._recalcSize(); // group in paging will invalidate the whole rows.
		}
		Event.stop(evt);
	},
	isOpen: function (row) {
		return getZKAttr(row, "open") == "true";
	},/** Opens an item */
	_openItem: function (row, toOpen, silent) {
		setZKAttr(row, "open", toOpen ? "true": "false"); //change it value
		if (row._img) {
			if (toOpen) {
				zk.rmClass(row._img,getZKAttr(row, "zcls")+"-img-close");
				zk.addClass(row._img,getZKAttr(row, "zcls")+"-img-open");
			} else {
				zk.rmClass(row._img,getZKAttr(row, "zcls")+"-img-open");
				zk.addClass(row._img,getZKAttr(row, "zcls")+"-img-close");
			}
		}
		zkGrwgp._openItemNow(row, toOpen);
		if (!silent)
			zkau.sendasap({uuid: row.id,
				cmd: "onOpen", data: [toOpen]});
				//always send since the client has to update Openable
	},
	_openItemNow: function (row, toOpen) {
		for (var table = row.parentNode.parentNode, i = row.rowIndex + 1, j = table.rows.length; i < j; i++) {
			if ($type(table.rows[i]) == "Grwgp" || $type(table.rows[i]) == "Grwgpft") break;
			if (getZKAttr(table.rows[i], "visible") == "true")
				table.rows[i].style.display = toOpen ? "" : "none";
		}
	},
	cleanup: function (row) {
		row._img = null;
		var prev, table = row.parentNode.parentNode;
		for (var i = row.rowIndex - 1; --i >= 0;) {
			if ($type(table.rows[i]) == "Grwgp") {
				prev = table.rows[i];
				break;
			}
		}
		if (prev)
			zk.addCleanupLater(function () {
				prev = $e(prev.id);
				if (prev)
					zkGrwgp._openItem(prev, zkGrwgp.isOpen(prev), true);
			}, false, row.id);
		else zkGrwgp._openItemNow(row, true);
	},
	setAttr: function (cmp, nm, val) {
		if (nm == "z.open") {
			zkGrwgp._openItem(cmp, "true" == val, true);
			if ("true" == val) {
				var meta = zkau.getMeta(getZKAttr(cmp, "rid"));
				if (meta) meta.stripe();
			}
			return true;
		}
		return false;
	},
	initdrag: zkGrw.initdrag,
	cleandrag: zkGrw.cleandrag
};
zkGrwgp.onVisi = zkGrwgp.onSize = function (cmp) {
	zkGrwgp._openItem(cmp, zkGrwgp.isOpen(cmp), true);
	zkGrw.stripe(cmp);
};
/** Grid Group Footer */
zkGrwgpft = {
	init: function(cmp) {
		setZKAttr(cmp, "inited", "true");
	},
	initdrag: zkGrw.initdrag,
	cleandrag: zkGrw.cleandrag,
	ondragover: zkGrw.ondragover,
	ondragout: zkGrw.ondragout
};
