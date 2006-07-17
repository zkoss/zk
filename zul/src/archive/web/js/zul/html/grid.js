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
zk.load("zul.html.zul");

zk.Grid = Class.create();
zk.Grid.prototype = {
	initialize: function (comp) {
		this.id = comp.id;
		zkau.setMeta(comp, this);
		this.init();
	},
	init: function () {
		this.element = $(this.id);
		if (!this.element) return;

		this.head = $(this.id + "!head");
		if (this.head) this.headtbl = zk.firstChild(this.head, "TABLE", true);
		this.body = $(this.id + "!body");
		this.bodytbl = zk.firstChild(this.body, "TABLE", true);
		if (this.bodytbl.tBodies && this.bodytbl.tBodies[0])
			this.bodyrows = this.bodytbl.tBodies[0].rows;
			//Note: bodyrows is null in FF if no rows, so no err msg

		if (!zk.isRealVisible(this.element)) return;

		var meta = this; //the nested function only see local var
		if (!this._inited) {
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

		this.stripe();

		setTimeout("zkGrid._calcSize('"+this.id+"')", 5);
			//don't calc now because browser might size them later
			//after the whole HTML page is processed
	},
	/* set the size*/
	_setSize: function () {
		var hgh = this.element.style.height;
		if (!hgh) {
			hgh = this.element.getAttribute("zk_hgh");
			if (!hgh) hgh = ""; //it might not be defined yet
		}
		this.body.style.height = hgh;
		if (hgh) this.element.setAttribute("zk_hgh", hgh);
		this.element.style.height = "";	

		var wd = this.element.style.width;
		if (wd && wd != "auto" && wd.indexOf('%') < 0) {
			//IE: otherwise, element's width will be extended to fit body
			this.body.style.width = wd;
			if (this.head) this.head.style.width = wd;
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
		for (var j = 0; j < this.bodyrows.length; ++j) {
			var row = this.bodyrows[j];
			var even = (j & 1) == 0;
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
			var headrow = this.headtbl.rows.length ? this.headtbl.rows[0]: null;
			zk.cpCellWidth(headrow, this.bodyrows, 3);
		}
	},
	/** Recalculate the size. */
	_recalcSize: function (timeout) {
		this._cleansz();
		setTimeout("zkGrid._calcSize('"+this.id+"')", 20);
	},
	/* cleanup size */
	_cleansz : function () {
		this.bodytbl.style.width = "";
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
		var body = $(grid.id + "!body");
		if (body) body.style.height = value;
				//FF: height is the body's height only
		var el = $(grid.id);
		if (el) el.setAttribute("zk_hgh", value);
		return true;
	}
	return false;
};

zk.addModuleInit(function () {zkCol = zulSHdr}); //Column
	//init it later because zul.js might not be loaded yet
