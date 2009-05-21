/* Column.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 24 15:25:39     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Column = zk.$extends(zul.mesh.HeaderWidget, {
	_sortDirection: "natural",
	_sortAscending: "none",
	_sortDescending: "none",

	$define: {
		sortDirection: function (v) {
			var n = this.getNode();
			if (n) {
				var zcls = this.getZclass();
				zDom.rmClass(n, zcls + "-sort-dsc");
				zDom.rmClass(n, zcls + "-sort-asc");
				switch (v) {
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
		},
		sortAscending: function (v) {
			if (!v) this._sortAscending = v = "none";
			var n = this.getNode(),
				zcls = this.getZclass();
			if (n) {
				if (v == "none") {
					zDom.rmClass(n, zcls + "-sort-asc");
					if (this._sortDescending == "none")
						zDom.rmClass(n, zcls + "-sort");					
				} else
					zDom.addClass(n, zcls + "-sort");
			}
		},
		sortDescending: function (v) {
			if (!v) this._sortDescending = v = "none";
			var n = this.getNode(),
				zcls = this.getZclass();
			if (n) {
				if (v == "none") {
					zDom.rmClass(n, zcls + "-sort-dsc");
					if (this._sortAscending == "none")
						zDom.rmClass(n, zcls + "-sort");					
				} else
					zDom.addClass(n, zcls + "-sort");
			}
		}
	},

	$init: function () {
		this.$supers('$init', arguments);
		this.listen('onSort', this, null, -1000);
		this.listen('onGroup', this, null, -1000);
	},
	getGrid: zul.mesh.HeaderWidget.prototype.getMeshWidget,
	setSort: function (type) {
		if (type && type.startsWith('client')) {
			this.setSortAscending(type);
			this.setSortDescending(type);
		} else {
			this.setSortAscending('none');
			this.setSortDescending('none');
		}
	},
	isSortable_: function () {
		return this._sortAscending != "none" || this._sortDescending != "none";
	},
	sort: function (ascending, evt) {
		var dir = this.getSortDirection();
		if (ascending) {
			if ("ascending" == dir) return false;
		} else {
			if ("descending" == dir) return false;
		}

		var sorter = ascending ? this._sortAscending: this._sortDescending;
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
		
		if (!rows || rows.hasGroup()) return false;
		
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
		
		var dsc = dir == "ascending" ? -1 : 1,
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
	group: function (ascending, evt) {
		var dir = this.getSortDirection();
		if (ascending) {
			if ("ascending" == dir) return false;
		} else {
			if ("descending" == dir) return false;
		}

		var sorter = ascending ? this._sortAscending: this._sortDescending;
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
		
		if (rows.hasGroup()) {
			for (var gs = rows.getGroups(), len = gs.length; --len >= 0;)
				rows.removeChild(gs[len]);
		}
		
		var d = [], col = this.getChildIndex();
		for (var i = 0, z = 0, row = rows.firstChild; row; row = row.nextSibling, z++)
			for (var k = 0, cell = row.firstChild; cell; cell = cell.nextSibling, k++) 
				if (k == col) {
					d[i++] = {
						wgt: cell,
						index: z
					};
				}
		
		var dsc = dir == "ascending" ? -1 : 1,
			fn = this.sorting,
			isNumber = sorter == "client(number)";
		d.sort(function(a, b) {
			var v = fn(a.wgt, b.wgt, isNumber) * dsc;
			if (v == 0) {
				v = (a.index < b.index ? -1 : 1);
			}
			return v;
		});
		
		// clear all
		for (var c = rows.firstChild, p; c;) {
			p = c.nextSibling;
			rows.removeChild(c);
			c = p;
		}
		
		for (var previous, row, index = this.getChildIndex(), i = 0, k = d.length;  i < k; i++) {
			row = d[i];
			if (!previous || fn(previous.wgt, row.wgt, isNumber) != 0) {
				//new group
				var group,
					cell = row.wgt.parent.getChildAt(index);
				if (cell && cell.$instanceof(zul.wgt.Label)) {
					group = new zul.grid.Group();
					group.appendChild(new zul.wgt.Label({value: cell.getValue()}));
				} else {
					var cc = cell.firstChild;
					if (cc && cc.$instanceof(zul.wgt.Label)) {
						group = new zul.grid.Group();
						group.appendChild(new zul.wgt.Label({value: cc.getValue()}));
					} else {
						group = new zul.grid.Group();
						group.appendChild(new zul.wgt.Label({value: msgzul.GRID_OTHER}));
					}
				}
				rows.appendChild(group);
			}
			rows.appendChild(row.wgt.parent);
			previous = row;
		}
		this._fixDirection(ascending);
		grid.appendChild(rows);
		return true;
	},
	setLabel: function (label) {
		this.$supers('setLabel', arguments);
		if (this.parent)
			this.parent._syncColMenu();
	},
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			this.$supers('setVisible', arguments);
			if (this.parent)
				this.parent._syncColMenu();
		}
	},
	onSort: function (evt) {
		var dir = this.getSortDirection();
		if ("ascending" == dir) this.sort(false, evt);
		else if ("descending" == dir) this.sort(true, evt);
		else if (!this.sort(true, evt)) this.sort(false, evt);
	},
	onGroup: function (evt) {
		var dir = this.getSortDirection();
		if ("ascending" == dir) this.group(false, evt);
		else if ("descending" == dir) this.group(true, evt);
		else if (!this.group(true, evt)) this.group(false, evt);
	},
	getZclass: function () {
		return this._zclass == null ? "z-column" : this._zclass;
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = this._sortAscending != "none" || this._sortDescending != "none" ?  this.getZclass() + '-sort': '';
			return scls != null ? scls + ' ' + added : added;
		}
		return scls;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var n = this.getNode();
		this.domListen_(n, "onMouseOver");
		this.domListen_(n, "onMouseOut");
		this.domListen_(this.getSubnode('btn'), "onClick");
	},
	unbind_: function () {
		var n = this.getNode();
		this.domUnlisten_(n, "onMouseOver");
		this.domUnlisten_(n, "onMouseOut");
		var btn = this.getSubnode('btn');
		if (btn)
			this.domUnlisten_(btn, "onClick");
		this.$supers('unbind_', arguments);
	},
	_doMouseOver: function(evt) {
		if (this.parent._menupopup || this.parent._menupopup != 'none') {
			var btn = this.getSubnode('btn'),
				n = this.getNode();
			zDom.addClass(n, this.getZclass() + "-over");
			if (btn) btn.style.height = n.offsetHeight - 1 + "px";
		}
	},
	_doMouseOut: function (evt) {
		if (this.parent._menupopup || this.parent._menupopup != 'none') {
			var btn = this.getSubnode('btn'),
				n = this.getNode(),
				zcls = this.getZclass();
			if (!zDom.hasClass(n, zcls + "-visi") &&
				(!zk.ie || !zDom.isAncestor(n, evt.domEvent.relatedTarget || evt.domEvent.toElement)))
					zDom.rmClass(n, zcls + "-over");
		}
	},
	_doClick: function (evt) {
		if (this.parent._menupopup || this.parent._menupopup != 'none') {
			var pp = this.parent._menupopup,
				n = this.getNode(),
				btn = this.getSubnode('btn'),
				zcls = this.getZclass();
				
			zDom.addClass(n, zcls + "-visi");
			
			if (pp == 'auto' && this.parent._mpop)
				pp = this.parent._mpop;

			if (zul.menu.Menupopup.isInstance(pp)) {
				var ofs = zDom.revisedOffset(btn),
					asc = this.getSortAscending() != 'none',
					desc = this.getSortDescending() != 'none';
				pp.getAscitem().setVisible(asc);
				pp.getDescitem().setVisible(desc);
				if (pp.getGroupitem())
					pp.getGroupitem().setVisible((asc || desc));
					
				var sep = pp.getDescitem().nextSibling;
				if (sep) sep.setVisible((asc || desc));
				pp.open(btn, [ofs[0], ofs[1] + btn.offsetHeight - 4], null, {sendOnOpen: true});
			}
			evt.stop(); // avoid onSort event.
		}
	}
});
