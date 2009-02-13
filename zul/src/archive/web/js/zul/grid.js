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
zk.Grid = Class.create();
zk.Grid.prototype = {
	initialize: function (comp) {
		this.id = comp.id;
		zkau.setMeta(comp, this);	
		this.init();
	},
	init: function () {
		this.element = $e(this.id);
		if (!this.element) return;

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
						if (!n.id || !n.id.endsWith("!hint")) {
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
	/* set the height. */
	setHgh: function (hgh) {		
		if (hgh && hgh != "auto" && hgh.indexOf('%') < 0) {
			var h =  this.element.offsetHeight - 2 - (this.head ? this.head.offsetHeight : 0)
				- (this.foot ? this.foot.offsetHeight : 0); // Bug #1835369
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
		var tblwd = this.body.clientWidth;
		if (zk.ie) //By experimental: see zk-blog.txt
			if (this.headtbl && this.headtbl.offsetWidth != this.bodytbl.offsetWidth)
				this.bodytbl.style.width = ""; //reset 
			if (tblwd && this.body.offsetWidth == this.bodytbl.offsetWidth && this.body.offsetWidth - tblwd > 11) { //scrollbar
				if (--tblwd < 0) tblwd = 0;
				this.bodytbl.style.width = tblwd + "px";
			}
				
		if (this.headtbl) {
			if (tblwd) this.head.style.width = tblwd + 'px';
			if (getZKAttr(this.element, "fixed") != "true")
				zul.adjustHeadWidth(this.hdfaker, this.bdfaker, this.ftfaker, this.bodyrows);
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
		}
		return false;
	},

	/** Renders listitems that become visible by scrolling.
	 */
	_render: function (timeout) {
		if(!this.paging)
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

	//Columns
	zkCols = zulHdrs;
});
