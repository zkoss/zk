/* grid.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jan  9 17:45:32     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.zul");

////
zk.Grid = Class.create();
zk.Grid.prototype = {
	initialize: function (comp) {
		this.id = comp.id;
		zkau.setMeta(comp, this);		
		this.qcells = [];
		this.init();
	},
	init: function (isLater) {
		this.element = $e(this.id);
		if (!this.element) return;

		this.body = $e(this.id + "!body");
		if (this.body) {
			this.bodytbl = zk.firstChild(this.body, "TABLE", true);
			if (this.bodytbl.tBodies && this.bodytbl.tBodies[0])
				this.bodyrows = this.bodytbl.tBodies[0].rows;
				//Note: bodyrows is null in FF if no rows, so no err msg

			this.head = $e(this.id + "!head");
			if (this.head) this.headtbl = zk.firstChild(this.head, "TABLE", true);
			this.foot = $e(this.id + "!foot");
			if (this.foot) this.foottbl = zk.firstChild(this.foot, "TABLE", true);
		} else {
			this.paging = true;
			this.body = $e(this.id + "!paging");
			this.bodytbl = zk.firstChild(this.body, "TABLE", true);

			var bs = this.bodytbl.tBodies;
			for (var j = 0; j < bs.length; ++j)
				if (bs[j].id) {
					this.bodyrows = bs[j].rows;
					break;
				}
		}

		if (!zk.isRealVisible(this.element)) return;

		var meta = this; //the nested function only see local var
		if (!this.paging && !this._inited) {
			this._inited = true;

			this.fnResize = function () {meta.recalcSize(true);};
			zk.addOnResize(this.fnResize);
		}

		if (!this.paging) {
			//FF: a small fragment is shown
			//IE: Bug 1775014
			if (this.headtbl && this.headtbl.rows.length) {
				var empty = true;
				l_out:
				for (var j = this.headtbl.rows.length; j;) {
					var headrow = this.headtbl.rows[--j];
					for (var k = headrow.cells.length; k;) {
						var n = $e(headrow.cells[--k].id + "!cave"); // Bug #1819037
						for (n = n ? n.firstChild: n; n; n = n.nextSibling)
							if (!n.id || !n.id.endsWith("!hint")) {
								empty = false;
								break l_out;
							}
					}
				}
				if (empty) this.head.style.height = "0px"; // Bug #1819037
				//we have to hide if empty (otherwise, a small block is shown)
				else this.head.style.height = "";// Bug #1832359
					
			}

			this.body.onscroll = function () {
				if (meta.head) meta.head.scrollLeft = meta.body.scrollLeft;
				if (meta.foot) meta.foot.scrollLeft = meta.body.scrollLeft;
				meta._render(zk.gecko ? 200: 60);
					//Moz has a bug to send the request out if we don't wait long enough
					//How long is enough is unknown, but 200 seems fine
			};
		} 

		if (isLater && this.qcells.length
		&& this.headtbl && this.headtbl.rows.length
		&& this.bodytbl && this.bodytbl.rows.length > 1) { //recalc is only a few lines
			zk.cpCellArrayWidth(this.headtbl.rows[0], this.qcells);
		} else {
			setTimeout("zkGrid._calcSize('"+this.id+"')", 150); // Bug #1813722			
			this.stripe(); 
		}
		this.qcells.length = 0;
		this._render(155); //prolong a bit since calSize might not be ready
	},
	putCellQue: function (cell) {
/** no need to check replication, since the server generates one for each
		for (var j = this.qcells.length; j;)
			if (this.qcells[--j] == cell)
				return; //redudant
*/
		this.qcells.push(cell);
	},
	/* set the height. */
	setHgh: function (hgh) {		
		if (hgh && hgh != "auto" && hgh.indexOf('%') < 0) {
			var h =  this.element.offsetHeight - 2 - (!this.paging ? (this.head ? this.head.offsetHeight : 0)
				- (this.foot ? this.foot.offsetHeight : 0) : 0); // Bug #1835369
			if (h < 0) h = 0;
			this.body.style.height = h + "px";
			if (this.body.offsetHeight) {} // bug #1812001
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
		if (!this.paging) { //note: we don't solve this bug for paging yet
			var wd = this.element.style.width;
			if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
				wd = zk.revisedSize(this.element, this.element.offsetWidth) - (wd == "100%" ? 2 : 0);
				if (wd < 0) wd = 0;
				if (wd) wd += "px";
			}
			if (wd) {
				this.body.style.width = wd;
				if (this.head) this.head.style.width = wd;
				if (this.foot) this.foot.style.width = wd;
			}
		}
	},
	cleanup: function ()  {
		if (this.fnResize)
			zk.rmOnResize(this.fnResize);
		this.element = this.body = this.bodytbl = this.bodyrows
			= this.head = this.headtbl = this.foot = this.foottbl = this.qcells = null;
			//in case: GC not works properly
	},

	/** Stripes the rows. */
	stripe: function () {
		var scOdd = getZKAttr(this.element, "scOddRow");
		if (!scOdd || !this.bodyrows) return;

		for (var j = 0, even = true; j < this.bodyrows.length; ++j) {
			var row = this.bodyrows[j];
			if ($visible(row)) {
				zk.addClass(row, scOdd, !even);
				even = !even;
			}
		}
	},

	/** Calculates the size. */
	_calcSize: function () {
		this.updSize();
			//Bug 1659601: we cannot do it in init(); or, IE failed!

		if (this.paging) { // Bug #1826101
			if (this.bodytbl && this.bodytbl.rows.length) {
				var head;
				for (var j = 0; j < this.bodytbl.rows.length; j++) {
					if ($type(this.bodytbl.rows[j]) == "Cols") {
						head = this.bodytbl.rows[j];
						break;
					}
				}
				if (head) {
					for (var j = 0; j < head.cells.length; j++) {
						var d = head.cells[j];
						var cave = $e(head.cells[j].id + "!cave");
						if (cave) {
							var wd =  d.style.width;							
							if (!wd || wd == "auto" || wd.indexOf('%') > -1) 
								d.style.width = zk.revisedSize(d, d.offsetWidth) + "px";								
							var w = $int(d.style.width);
							cave.style.width = zk.revisedSize(cave, w) + "px";
						}
					}
				}
			}
			return; //nothing to adjust since single table
		}

		var tblwd = this.body.clientWidth;
		if (zk.ie) //By experimental: see zk-blog.txt
			if (tblwd && this.body.offsetWidth - tblwd > 11) { //scrollbar
				if (--tblwd < 0) tblwd = 0;
				this.bodytbl.style.width = tblwd + "px";
			} else
				this.bodytbl.style.width = "";
				
		if (this.headtbl) {
			if (tblwd) this.head.style.width = tblwd + "px";
			if (this.headtbl.rows.length) {
				var head;
				var j =0
				for(; j < this.headtbl.rows.length; j++)
					if ($type(this.headtbl.rows[j]) == "Cols") {
						head = this.headtbl.rows[j];
						break;
					}
				zk.cpCellWidth(head, this.bodyrows, this);	
				var fake = $e(head.id + "!fake");
				if (!fake || fake.cells.length != head.cells.length) {
					if (fake) fake.parentNode.removeChild(fake);
					var src = document.createElement("TR");
					src.id = head.id + "!fake";
					src.style.height = "0px";
						//Note: we cannot use display="none" (offsetWidth won't be right)
					for (var j = head.cells.length; --j >= 0;)
						src.appendChild(document.createElement("TD"));
					this.headtbl.rows[0].parentNode.insertBefore(src, this.headtbl.rows[0]);			
				}			
				var row = this.headtbl.rows[0];
				var cells = row.cells;
				for (var k =0, z = 0; k < cells.length; k++) {
					var s = cells[k], d = head.cells[k];
					var wd =  d.style.width;							
					if (!wd || wd == "auto" || wd.indexOf('%') > -1) // Bug #1822564
						d.style.width = zk.revisedSize(d, d.offsetWidth) + "px";
					wd = d.style.width;
					if (zk.isVisible(d))
						s.style.width = $int(wd) + zk.sumStyles(d, "lr", zk.borders) + zk.sumStyles(d, "lr", zk.paddings) + "px";
					else s.style.display = "none";
				} 						
			}
			if (this.foottbl && this.foottbl.rows.length)
				zk.cpCellWidth(head, this.foottbl.rows, this);
		} else if (this.foottbl) {
			if (tblwd) this.foot.style.width = tblwd + 'px';
			if (this.foottbl.rows.length)
				zk.cpCellWidth(this.foottbl.rows[0], this.bodyrows, this); //assign foot's col width
		}
	},
	/** Recalculate the size. */
	recalcSize: function (cleansz) {
		if (!zk.isRealVisible(this.element)) return;
		setTimeout("zkGrid._calcSize('"+this.id+"')", 20);
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
			if (this.bodytbl) this.headtbl.style.width = val;
			if (this.foottbl) this.headtbl.style.width = val;
			return true;
		case "style.height":
			this.element.style.height = val;
			if (zk.ie && !zk.ie7 && this.body) this.body.style.height = val;
				// IE6 cannot shrink its height, we have to specify this.body's height to equal the element's height. 
			this.setHgh(val);
			if (!this.paging) this.init();
			return true;
		case "style":
		case "style.width":
			zkau.setAttr(this.element, nm, val);
			if (!this.paging) this.init();
			return true;
		case "z.scOddRow":
			zkau.setAttr(this.element, nm, val);
			this.stripe();
			return true;
		case "z.render":
			this._render(0);
			return true;
		case "scrollTop":
			if (!this.paging && this.body) {
				this.body.scrollTop = val;
				return true;
			}
			break;
		case "scrollLeft":
			if (!this.paging && this.body) {
				this.body.scrollLeft = val;
				return true;
			}
		}
		return false;
	},

	/** Renders listitems that become visible by scrolling.
	 */
	_render: function (timeout) {
		setTimeout("zkGrid._renderNow('"+this.id+"')", timeout);
	},
	_renderNow: function () {
		var rows = this.bodyrows;
		if (!rows || !rows.length || getZKAttr(this.element, "model") != "true") return;

		//Note: we have to calculate from top to bottom because each row's
		//height might diff (due to different content)
		var data = "";
		var min = this.body.scrollTop, max = min + this.body.offsetHeight;
		for (var j = 0; j < rows.length; ++j) {
			var r = rows[j];
			if ($visible(r)) {
				var top = zk.offsetTop(r);
				if (top + zk.offsetHeight(r) < min) continue;
				if (top > max) break; //Bug 1822517
				if (getZKAttr(r, "loaded") != "true")
					data += "," + r.id;
			}
		}
		if (data) {
			data = data.substring(1);
			zkau.send({uuid: this.id, cmd: "onRender", data: [data]}, 0);
		}
	}
};

////
// Grid //
zkGrid = {};

zkGrid._init = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta._init();
};
zkGrid._calcSize = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta._calcSize();
};

/** Init (and re-init) a grid. */
zkGrid.init = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
	else new zk.Grid(cmp);
};
/**
zkGrid.childchg = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init(getZKAttr(cmp, "autowidth") != "true");
	else new zk.Grid(cmp);
}; // Bug #1817627.
*/
/** Called when a grid becomes visible because of its parent. */
zkGrid.onVisi = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
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
		if (p) $e($uuid(p) + "!cell").style.MozUserSelect = "none";
	}
};
zkGrw.ondragout = function (evt) {
	var target = Event.element(evt);
	var p = $parentByType(target, "Gcl");
	if (p) $e($uuid(p) + "!cell").style.MozUserSelect = "";	
};
zkGrw.cleanup = function (cmp) {
	zkGrw.stripe(cmp, true);
};
zkGrw.setAttr = function (cmp, nm, val) {
	if (nm == "visibility") { // Bug #1836257
		var grid = $parentByType(cmp, "Grid");
		var meta = zkau.getMeta(grid);
		if (meta) {
			if (!meta.fixedStripe) meta.fixedStripe = function () {meta.stripe();};
			setTimeout(meta.fixedStripe, 0);
		}
	}
	return false;
};
zkGrw.stripe = function (cmp, isClean) {
	var grid = $parentByType(cmp, "Grid");
	var meta = zkau.getMeta(grid);
	if (meta) {
		if (!meta.fixedStripe) meta.fixedStripe = function () {meta.stripe();};
		if (isClean) zk.addCleanupLater(meta.fixedStripe, false, true);
		else zk.addInitLater(meta.fixedStripe, false, true);
	}
};
zkGcl = {}; //cell
zkGcl.init = function (cmp) {
	var grid = $parentByType(cmp, "Grid");
	var meta = zkau.getMeta(grid);
	if (meta) {
		meta.putCellQue(cmp);
		if (!meta.fixedSize)
			meta.fixedSize = function () {meta.init(true);};	
		zk.addInitLater(meta.fixedSize, false, true);
	}
};
zk.addModuleInit(function () {
	//Column
	//init it later because zul.js might not be loaded yet
	zkCol = {}
	Object.extend(zkCol, zulHdr);

	/** Resize the column. */
	zkCol.resize = function (col1, icol, wd1, keys) {
		var grid = $parentByType(col1, "Grid");
		if (grid) {
			var meta = zkau.getMeta(grid);
			if (meta)
				meta.resizeCol(
					$parentByType(col1, "Cols"), icol, col1, wd1, keys);
		}
	};

	//Columns
	zkCols = zulHdrs;
});
