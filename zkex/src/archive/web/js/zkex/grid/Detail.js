/* Detail.js

	Purpose:
		
	Description:
		
	History:
		Mon May 18 11:49:03     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.def(zkex.grid.Detail = zk.$extends(zul.Widget,{
	$init: function () {
		this.$supers('$init', arguments);
		this.setWidth("18px");
	},
	getZclass: function () {
		return this._zclass != null ? this._zclass : "z-detail";
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		this.parent.listen('onStripe', this);
		if (this._open)
			this.open(this._open, true);
	},
	unbind_: function () {
		this.parent.unlisten('onStripe', this);
		if (this._open)
			this.open(false, true);
		this.$supers('unbind_', arguments);
	},
	/**A callback function for changing stripe*/
	onStripe: function (evt) {
		if (this._open) {
			var fake = this.getSubnode('fake'),
				zcls = this.getZclass();
			fake.className = this.parent.getNode().className + " " + zcls + '-faker';
		}
	},
	doClick_: function () {
		this.open((this._open = !this._open));
		this.$supers('doClick_', arguments);
	},
	open: function (open, silent) {
		var n = this.getNode();
		if (n) {
			var zcls = this.getZclass();
			zDom[open ? "addClass" : "rmClass"](n, zcls + "-expd");
			if (open) {
				var td = this.getSubnode('chdextr'),
					cave = this.getSubnode('cave'),
					tr = td.parentNode,
					fake = tr.parentNode.insertRow(tr.rowIndex),
					cell = fake.insertCell(0);
				fake.id = this.uuid + '$fake';
				cell.colSpan = zDom.ncols(tr) - 1;
				
				zDom.addClass(fake, tr.className + ' ' + zcls + '-faker');
				td.rowSpan = 2;
				cell.appendChild(cave);
				cave.style.display = "";
				zWatch.fireDown('onShow', {visible:true}, this);
			} else {
				var td = this.getSubnode('chdextr'),
					cave = this.getSubnode('cave'),
					tr = td.parentNode,
					fake = this.getSubnode('fake');
				cave.style.display = "none";
				
				// fix IE6 bug #2779453
				if (zk.ie6Only) {
					try {
						var inps = cave.getElementsByTagName("INPUT");
						for (var i in inps) {
							if (inps[i].type == "checkbox")
								inps[i].defaultChecked = inps[i].checked;
						}
					} catch (e) {}
				}
				n.appendChild(cave);
				td.rowSpan = 1;
				zDom.remove(fake);
			}
			if (!silent)
				this.fire('onOpen', {open: open});
		}
	}
}),{ // zk.def
	open: function () {
		this.open(this._open, true);
	},
	contentStyle: function () {
		var cave = this.getSubnode('cave');
		if (cave)
			cave.cssText = this._contentStyle || '';
	},
	contentSclass: function () {
		var cave = this.getSubnode('cave');
		if (cave)
			cave.className = this._contentSclass || '';
	}
});
