/* Column.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 24 15:25:39     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Column = zk.$extends(zul.mesh.SortWidget, {
	getGrid: zul.mesh.HeaderWidget.prototype.getMeshWidget,

	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onGroup: this}, -1000);
	},
	getMeshBody: function () {
		var grid = this.getGrid();
		return grid ? grid.rows : null;  
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
		
		var mesh = this.getMeshWidget();
		if (!mesh || mesh.isModel()) return false;
			// if in model, the sort should be done by server
			
		var	body = this.getMeshBody();
		if (!body) return false;
		evt.stop();
		
		var desktop = body.desktop,
			node = body.getNode();
		try {
			body.unbind();
			if (body.hasGroup()) {
				for (var gs = body.getGroups(), len = gs.length; --len >= 0;) 
					body.removeChild(gs[len]);
			}
			
			var d = [], col = this.getChildIndex();
			for (var i = 0, z = 0, it = mesh.getBodyWidgetIterator(), w; (w = it.next()); z++) 
				for (var k = 0, cell = w.firstChild; cell; cell = cell.nextSibling, k++) 
					if (k == col) {
						d[i++] = {
							wgt: cell,
							index: z
						};
					}
			
			var dsc = dir == "ascending" ? -1 : 1, fn = this.sorting, isNumber = sorter == "client(number)";
			d.sort(function(a, b) {
				var v = fn(a.wgt, b.wgt, isNumber) * dsc;
				if (v == 0) {
					v = (a.index < b.index ? -1 : 1);
				}
				return v;
			});
			
			// clear all
			for (;body.firstChild;)
				body.removeChild(body.firstChild);
			
			for (var previous, row, index = this.getChildIndex(), i = 0, k = d.length; i < k; i++) {
				row = d[i];
				if (!previous || fn(previous.wgt, row.wgt, isNumber) != 0) {
					//new group
					var group, cell = row.wgt.parent.getChildAt(index);
					if (cell && cell.$instanceof(zul.wgt.Label)) {
						group = new zul.grid.Group();
						group.appendChild(new zul.wgt.Label({
							value: cell.getValue()
						}));
					} else {
						var cc = cell.firstChild;
						if (cc && cc.$instanceof(zul.wgt.Label)) {
							group = new zul.grid.Group();
							group.appendChild(new zul.wgt.Label({
								value: cc.getValue()
							}));
						} else {
							group = new zul.grid.Group();
							group.appendChild(new zul.wgt.Label({
								value: msgzul.GRID_OTHER
							}));
						}
					}
					body.appendChild(group);
				}
				body.appendChild(row.wgt.parent);
				previous = row;
			}
			this._fixDirection(ascending);
		} finally {
			body.replaceHTML(node, desktop);
		}
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
	onGroup: function (evt) {
		var dir = this.getSortDirection();
		if ("ascending" == dir) this.group(false, evt);
		else if ("descending" == dir) this.group(true, evt);
		else if (!this.group(true, evt)) this.group(false, evt);
	},
	getZclass: function () {
		return this._zclass == null ? "z-column" : this._zclass;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var n = this.getNode();
		this.domListen_(n, "onMouseOver");
		this.domListen_(n, "onMouseOut");
		var btn = this.getSubnode('btn');
		if (btn)
			this.domListen_(btn, "onClick");
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
			jq(n).addClass(this.getZclass() + "-over");
			if (btn) btn.style.height = n.offsetHeight - 1 + "px";
		}
	},
	_doMouseOut: function (evt) {
		if (this.parent._menupopup || this.parent._menupopup != 'none') {
			var btn = this.getSubnode('btn'),
				n = this.getNode(), $n = jq(n),
				zcls = this.getZclass();
			if (!$n.hasClass(zcls + "-visi") &&
				(!zk.ie || !jq.isAncestor(n, evt.domEvent.relatedTarget || evt.domEvent.toElement)))
					$n.removeClass(zcls + "-over");
		}
	},
	_doClick: function (evt) {
		if (this.parent._menupopup || this.parent._menupopup != 'none') {
			var pp = this.parent._menupopup,
				n = this.getNode(),
				btn = this.getSubnode('btn'),
				zcls = this.getZclass();
				
			jq(n).addClass(zcls + "-visi");
			
			if (pp == 'auto' && this.parent._mpop)
				pp = this.parent._mpop;

			if (zul.menu.Menupopup.isInstance(pp)) {
				var ofs = zk(btn).revisedOffset(),
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
