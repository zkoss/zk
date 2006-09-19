/* grid.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jan  9 17:45:32     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.zul");

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
		} else {
			this.paging = true;
			this.body = $e(this.id + "!paging");
			this.bodytbl = zk.firstChild(this.body, "TABLE", true);
			if (this.bodytbl.tBodies.length >= 2)
				this.bodyrows = this.bodytbl.tBodies[1].rows;
		}

		if (!zk.isRealVisible(this.element)) return;

		var meta = this; //the nested function only see local var
		if (!this.paging && !this._inited) {
			this._inited = true;

			this.fnResize = function () {
				//Tom Yeh: 20051230:
				//In certain case, IE will keep sending resize (because
				//our grid might adjust size and cause resize again)
				//To avoid this endless loop, we resize once in a few seconds
				var time = new Date().getTime();
				if (!meta.nextTime || time > meta.nextTime) {
					meta.nextTime = time + 3000;
					meta._recalcSize();
				}
			};
			Event.observe(window, "resize", this.fnResize);
		}

		this._setSize();

		if (!this.paging) {
			if (zk.gecko && this.headtbl && this.headtbl.rows.length == 1) {
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
			};
		}

		this.stripe();

		if (!this.paging)
			setTimeout("zkGrid._calcSize('"+this.id+"')", 5);
			//don't calc now because browser might size them later
			//after the whole HTML page is processed
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
	_setSize: function () {
		var hgh = this.element.style.height;
		if (!hgh) {
			hgh = this.element.getAttribute("zk_hgh");
			if (!hgh) hgh = ""; //it might not be defined yet
		}
		this.setHgh(hgh);

		//Bug 1553937: wrong sibling location
		//IE: otherwise, element's width will be extended to fit body
		if (zk.ie && !this.paging) { //note: we don't solve this bug for paging yet
			var wd = this.element.style.width;
			if (wd && wd != "auto" && wd.indexOf('%') < 0) {
				this.body.style.width = wd;
				if (this.head) this.head.style.width = wd;
			}
		}
	},
	cleanup: function ()  {
		if (this.fnResize)
			Event.stopObserving(window, "resize", this.fnResize);
		this.element = this.body = this.bodytbl = this.bodyrows
			= this.head = this.headtbl = null;
			//in case: GC not works properly
	},

	/** Stripes the rows. */
	stripe: function () {
		if (!this.bodyrows) return;
		for (var j = 0, even = true; j < this.bodyrows.length; ++j) {
			var row = this.bodyrows[j];
			if (row.style.display != "none") {
				for (var k = 0; k < row.cells.length; ++k) {
					var cell = row.cells[k];
					var cs = cell.className;
					if (even) { //even
						if (cs.endsWith("od"))
							cell.className = cs.substring(0, cs.length - 2) + "ev";
					} else {
						if (cs.endsWith("ev"))
							cell.className = cs.substring(0, cs.length - 2) + "od";
					}
				}
				even = !even;
			}
		}
	},

	/** Calculates the size. */
	_calcSize: function () {
		var tblwd = this.body.clientWidth;
		if (zk.ie) //By experimental: see zk-blog.txt
			if (tblwd && this.body.offsetWidth - tblwd > 11) {
				if (--tblwd < 0) tblwd = 0;
				this.bodytbl.style.width = tblwd + "px";
			} else this.bodytbl.style.width = "";

		if (this.headtbl) {
			if (tblwd) this.head.style.width = tblwd + "px";
			if (this.headtbl.rows.length)
				zk.cpCellWidth(this.headtbl.rows[0], this.bodyrows);
		}
	},
	/** Recalculate the size. */
	_recalcSize: function (timeout) {
		this._cleansz();
		setTimeout("zkGrid._calcSize('"+this.id+"')", 20);
	},
	/* cleanup size */
	_cleansz : function () {
		this.body.style.width = this.bodytbl.style.width = "";
		if (this.headtbl) {
			this.head.style.width = "";
			if (this.headtbl.rows.length) {
				var headrow = this.headtbl.rows[0];
				for (var j = headrow.cells.length; --j >=0;)
					headrow.cells[j].style.width = "";
			}
		}
	}
};

/////////
function zkGrid() {}

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
zkGrid.onVisi = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
};

zkGrid.stripe = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta.stripe();
};

/** Handles setAttr. */
zkGrid.setAttr = function (grid, name, value) {
	if (name == "style.height") {
		var meta = zkau.getMeta(grid);
		if (meta) {
			meta.setHgh(value);
			return true;
		}
	}
	return false;
};

zk.addModuleInit(function () {zkCol = zulSHdr}); //Column
	//init it later because zul.js might not be loaded yet
