/* SortWidget.js

	Purpose:
		
	Description:
		
	History:
		Tue May 26 14:51:17     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.mesh.SortWidget = zk.$extends(zul.mesh.HeaderWidget, {
	_sortDirection: "natural",
	_sortAscending: "none",
	_sortDescending: "none",

	$define: {
		sortDirection: function (v) {
			var n = this.$n();
			if (n) {
				var zcls = this.getZclass(),
					$n = jq(n);
				$n.removeClass(zcls + "-sort-dsc").removeClass(zcls + "-sort-asc");
				switch (v) {
				case "ascending":
					$n.addClass(zcls + "-sort-asc");
					break;
				case "descending":
					$n.addClass(zcls + "-sort-dsc");
					break;
				default: // "natural"
					$n.addClass(zcls + "-sort");
					break;
				}
			}
		},
		sortAscending: function (v) {
			if (!v) this._sortAscending = v = "none";
			var n = this.$n(),
				zcls = this.getZclass();
			if (n) {
				var $n = jq(n);
				if (v == "none") {
					$n.removeClass(zcls + "-sort-asc");
					if (this._sortDescending == "none")
						$n.removeClass(zcls + "-sort");					
				} else
					$n.addClass(zcls + "-sort");
			}
		},
		sortDescending: function (v) {
			if (!v) this._sortDescending = v = "none";
			var n = this.$n(),
				zcls = this.getZclass();
			if (n) {
				var $n = jq(n);
				if (v == "none") {
					$n.removeClass(zcls + "-sort-dsc");
					if (this._sortAscending == "none")
						$n.removeClass(zcls + "-sort");					
				} else
					$n.addClass(zcls + "-sort");
			}
		}
	},
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onSort: this}, -1000);
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
		
		var mesh = this.getMeshWidget();
		if (!mesh || mesh.isModel()) return false;
			// if in model, the sort should be done by server
			
		var	body = this.getMeshBody();
		
		if (!body || body.hasGroup()) return false;
		
		var desktop = body.desktop,
			node = body.$n();
			
		evt.stop();
		try {
			body.unbind();
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
			for (var i = 0, k = d.length; i < k; i++) {
				body.appendChild(d[i].wgt.parent);
			}
			this._fixDirection(ascending);
			
		} finally {
			body.replaceHTML(node, desktop);
		}
		return true;
	},
	sorting: function(a, b, isNumber) {
		var v1, v2;
		if (typeof a.getLabel == 'function')
			v1 = a.getLabel();
		else if (typeof a.getValue == 'function')
			v1 = a.getValue();
		else v1 = a;
		
		if (typeof b.getLabel == 'function')
			v2 = b.getLabel();
		else if (typeof b.getValue == 'function')
			v2 = b.getValue();
		else v2 = b;
		
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
	onSort: function (evt) {
		var dir = this.getSortDirection();
		if ("ascending" == dir) this.sort(false, evt);
		else if ("descending" == dir) this.sort(true, evt);
		else if (!this.sort(true, evt)) this.sort(false, evt);
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var zcls = this.getZclass(),
				added;
			if (this._sortAscending != "none" || this._sortDescending != "none") {
				switch (this._sortDirection) {
				case "ascending":
					added = zcls + "-sort-asc";
					break;
				case "descending":
					added = zcls + "-sort-dsc";
					break;
				default: // "natural"
					added = zcls + "-sort";
					break;
				}
			}
			return scls != null ? scls + (added ? ' ' + added : '') : added || '';
		}
		return scls;
	}
});