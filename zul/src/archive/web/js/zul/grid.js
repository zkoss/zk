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
		this.init();
	},
	init: function () {
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

		if (!this.bodyrows) {
			alert(mesg.INVALID_STRUCTURE + this.id);
			return;
		}

		var meta = this; //the nested function only see local var
		if (!this.paging && !this._inited) {
			this._inited = true;

			this.fnResize = function () {meta.recalcSize(true);};
			zk.addOnResize(this.fnResize);
		}

		if (!this.paging) {
			if ((zk.gecko||zk.opera) && this.headtbl && this.headtbl.rows.length == 1) {
				var headrow = this.headtbl.rows[0];
				var empty = true;
				l_out:
				for (var j = headrow.cells.length; --j>=0;)
					for (var n = headrow.cells[j].firstChild; n; n = n.nextSibling)
						if (!n.id || !n.id.endsWith("!hint")) {
							empty = false;
							break l_out;
						}
				if (empty) this.head.style.display = "none";
					//we have to hide if empty (otherwise, a small block is shown)
			}

			this.body.onscroll = function () {
				if (meta.head) meta.head.scrollLeft = meta.body.scrollLeft;
				if (meta.foot) meta.foot.scrollLeft = meta.body.scrollLeft;
				meta._render(zk.gecko ? 200: 60);
					//Moz has a bug to send the request out if we don't wait long enough
					//How long is enough is unknown, but 200 seems fine
			};
		}

		this.stripe();

		setTimeout("zkGrid._calcSize('"+this.id+"')", 5);
			//don't calc now because browser might size them later
			//after the whole HTML page is processed

		this._render(150); //prolong a bit since calSize might not be ready
	},
	/* set the height. */
	setHgh: function (hgh) {
		//note: we have to clean element.style.height. Otherwise, FF will
		//overlap element with other elements
		if (hgh && hgh != "auto" && hgh.indexOf('%') < 0) {
			this.body.style.height = hgh;
			this.element.style.height = "";	
			this.element.setAttribute("zk_hgh", hgh);
		} else {
			//Bug 1556099: it is strange if we ever check the value of
			//body.offsetWidth. The grid's body's height is 0 if init called
			//after grid become visible (due to opening an accordion tab)
			this.body.style.height = "";
			this.element.style.height = hgh;
			this.element.removeAttribute("zk_hgh");
		}
	},
	/* set the size*/
	updSize: function () {
		var hgh = this.element.style.height;
		if (!hgh) {
			hgh = this.element.getAttribute("zk_hgh");
			if (!hgh) hgh = ""; //it might not be defined yet
		}
		this.setHgh(hgh);

		//Bug 1553937: wrong sibling location
		//Otherwise,
		//IE: element's width will be extended to fit body
		//FF and IE: sometime a horizontal scrollbar appear (though it shalln't)
		if (!this.paging) { //note: we don't solve this bug for paging yet
			var wd = this.element.style.width;
			if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
				wd = this.element.clientWidth;
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
			= this.head = this.headtbl = this.foot = this.foottbl = null;
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

		if (this.paging) return; //nothing to adjust since single table

		//refer to sel.js's _calcSize for why
		var wd = this.element.style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			wd = this.element.clientWidth;
			if (wd) wd += "px";
		}
		if (wd) {
			this.body.style.width = wd;
			if (this.head) this.head.style.width = wd;
			if (this.foot) this.foot.style.width = wd;
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
			if (this.headtbl.rows.length)
				zk.cpCellWidth(this.headtbl.rows[0], this.bodyrows);
		}
		if (this.foottbl) {
			if (tblwd) this.foot.style.width = tblwd + 'px';
			if (this.foottbl.rows.length)
				zk.cpCellWidth(this.foottbl.rows[0], this.bodyrows); //assign foot's col width
		}
	},
	/** Recalculate the size. */
	recalcSize: function (cleansz) {
		if (cleansz) this.cleanSize();
		setTimeout("zkGrid._calcSize('"+this.id+"')", 20);
	},
	/** Cleanup size */
	cleanSize: function () {
		if (this.paging) return; //nothing to adjust since single table

		this.body.style.width = this.bodytbl.style.width = "";
		if (this.headtbl) {
			this.head.style.width = "";
			if (this.headtbl.rows.length) {
				var headrow = this.headtbl.rows[0];
				for (var j = headrow.cells.length; --j >=0;)
					headrow.cells[j].style.width = "";
			}
		}
		if (this.foottbl) {
			this.foot.style.width = "";
			if (this.foottbl.rows.length) {
				var footrow = this.foottbl.rows[0];
				for (var j = footrow.cells.length; --j >=0;)
					footrow.cells[j].style.width = "";
			}
		}
	},
	/** Resize the specified column.
	 * @param cmp columns
	 */
	resizeCol: function (cmp, icol, col1, wd1, col2, wd2, keys) {
		var rows = this.bodyrows;
		if (rows) {
			zulHdr.resizeAll(rows,
				cmp, icol, col1, wd1, col2, wd2, keys);
			this.recalcSize(false);
		}
	},

	/** Renders listitems that become visible by scrolling.
	 */
	_render: function (timeout) {
		setTimeout("zkGrid._renderNow('"+this.id+"')", timeout);
	},
	_renderNow: function () {
		if (getZKAttr(this.element, "model") != "true") return;

		var rows = this.bodyrows;
		if (!rows.length) return; //no row at all

		//Note: we have to calculate from top to bottom because each row's
		//height might diff (due to different content)
		var data = "";
		var min = this.body.scrollTop, max = min + this.body.offsetHeight;
		for (var j = 0; j < rows.length; ++j) {
			var r = rows[j];
			if ($visible(r)) {
				var top = zk.offsetTop(r);
				if (top + zk.offsetHeight(r) < min) continue;
				if (top >= max) break;
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
zkGrid.childchg = zkGrid.init;

/** Called when a grid becomes visible because of its parent. */
zkGrid.onVisi = zkGrid.onSize = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
};

zkGrid.stripe = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta.stripe();
};

/** Handles setAttr. */
zkGrid.setAttr = function (grid, nm, val) {
	if (nm == "style.height") {
		var meta = zkau.getMeta(grid);
		if (meta) {
			meta.setHgh(val);
			if (!meta.paging) meta.init();
			return true;
		}
	} else if (nm == "style" || nm == "style.width") {
		zkau.setAttr(grid, nm, val);
		var meta = zkau.getMeta(grid);
		if (meta && !meta.paging) meta.init();
		return true;
	} else if (nm == "z.scOddRow") {
		zkau.setAttr(grid, nm, val);
		zkGrid.stripe(grid.id);
	} else if (!this.paging && this.body) {
		if (nm == "scrollTop") {
			this.body.scrollTop = val;
			return true;
		}
		if (nm == "scrollLeft") {
			this.body.scrollLeft = val;
			return true;
		}
	}
	return false;
};

zkGrid._renderNow = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta._renderNow();
};

zk.addModuleInit(function () {
	//Column
	//init it later because zul.js might not be loaded yet
	zkCol = {}
	Object.extend(zkCol, zulHdr);

	/** Resize the column. */
	zkCol.resize = function (col1, col2, icol, wd1, wd2, keys) {
		var grid = $parentByType(col1, "Grid");
		if (grid) {
			var meta = zkau.getMeta(grid);
			if (meta)
				meta.resizeCol(
					$parentByType(col1, "Cols"), icol, col1, wd1, col2, wd2, keys);
		}
	};

	//Columns
	zkCols = zulHdrs;
});
