/* HeaderWidget.js

	Purpose:
		
	Description:
		
	History:
		Mon Dec 29 17:33:15     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.HeaderWidget = zk.$extends(zul.LabelImageWidget, {
	getAlign: function () {
		return this._align;
	},
	setAlign: function (align) {
		if (this._align != align) {
			this._align = align;
			this.invalidateWhole_();
		}
	},
	getValign: function () {
		return this._valign;
	},
	setValign: function (valign) {
		if (this._valign != valign) {
			this._valign = valign;
			this.invalidateWhole_();
		}
	},
	invalidateWhole_: function () {
		var wgt = this.getOwner();
		if (wqt) wgt.rerender();
	},
	getOwner: function () {
		return this.parent ? this.parent.parent : null;
	},
	isSortable_: function () {
		return false;
	},
	getColAttrs: function () {
		return (this._align ? ' align="' + this._align + '"' : '')
			+ (this._valign ? ' valign="' + this._valign + '"' : '') ;
	},
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			this.$supers('setVisible', arguments);
			this.invalidateWhole();
		}
	},
	domAttrs_: function (no) {
		var attrs = this.$supers('domAttrs_', arguments);
		return attrs + this.getColAttrs();
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.parent.isSizable()) this._initsz();
		this._fixedFaker();
	},
	unbind_: function () {
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}
		this.$supers('unbind_', arguments);
	},
	_initsz: function () {
		var n = this.getNode();
		if (n && !this._drag) {
			var $Header = this.$class;
			this._drag = new zk.Draggable(this, null, {
				revert: true, constraint: "horizontal",
				ghosting: $Header._ghostsizing,
				endghosting: $Header._endghostsizing,
				snap: $Header._snapsizing,
				ignoredrag: $Header._ignoresizing,
				endeffect: $Header._aftersizing
			});
		}
	},
	_fixedFaker: function () {
		var n = this.getNode(),
			index = zDom.cellIndex(n),
			owner = this.getOwner();
		for (var faker, fs = this.$class._faker, i = fs.length; --i >= 0;) {
			faker = owner['e' + fs[i]]; // internal element
			if (faker && !this.getSubnode(fs[i])) 
				faker[faker.cells.length > index ? "insertBefore" : "appendChild"]
					(this._createFaker(n, fs[i]), faker.cells[index]);
		}
	},
	_createFaker: function (n, postfix) {
		var t = document.createElement("TH"), 
			d = document.createElement("DIV");
		t.id = n.id + "$" + postfix;
		t.className = n.className;
		t.style.cssText = n.style.cssText;
		d.style.overflow = "hidden";
		t.appendChild(d);
		return t;
	},
	doClick_: function (evt) {
		if (!zk.dragging && zk.Widget.$(evt.nativeTarget) == this && this.isSortable_() 
			&& zDom.tag(evt.nativeTarget) != "INPUT") {
			this.fire('onSort');
			evt.stop();
		}
	},
	doMouseMove_: function (evt) {
		if (zk.dragging || !this.parent.isSizable()) return;
		var n = this.getNode(),
			ofs = zDom.revisedOffset(n); // Bug #1812154
		if (this._insizer(evt.data.pageX - ofs[0])) {
			zDom.addClass(n, this.getZclass() + "-sizing");
		} else {
			zDom.rmClass(n, this.getZclass() + "-sizing");
		}
	},
	doMouseOut_: function (evt) {
		if (this.parent.isSizable()) {
			var n = this.getNode()
			zDom.rmClass(n, this.getZclass() + "-sizing");
		}
	},
	_insizer: function (x) {
		return x >= this.getNode().offsetWidth - 10;
	}
}, {
	_faker: ["hdfaker", "bdfaker", "ftfaker"],
	
	_onSizingMarshal: function () {
		return [this.index, this.uuid, this.width, this.keys ? this.keys.marshal(): ''];
	},
	//dragdrop//
	_ghostsizing: function (dg, ofs, evt) {
		var wgt = dg.control,
			el = wgt.getOwner().eheadtbl;
			of = zDom.revisedOffset(el),
			n = wgt.getNode();
		
		ofs[1] = of[1];
		ofs[0] += zDom.offsetWidth(n);
		document.body.insertAdjacentHTML("afterBegin",
			'<div id="zk_hdghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:3px;height:'+zDom.offsetHeight(el.parentNode.parentNode)
			+'px;background:darkgray"></div>');
		return zDom.$("zk_hdghost");		
	},
	_endghostsizing: function (dg, origin) {
		dg._zszofs = zDom.revisedOffset(dg.node)[0] - zDom.revisedOffset(origin)[0];
	},
	_snapsizing: function (dg, pointer) {
		var n = dg.control.getNode(),
			ofs = zDom.revisedOffset(n);
		pointer[0] += zDom.offsetWidth(n); 
		if (ofs[0] + dg._zmin >= pointer[0])
			pointer[0] = ofs[0] + dg._zmin;
		return pointer;
	},
	_ignoresizing: function (dg, pointer, evt) {
		var wgt = dg.control,
			n = wgt.getNode(),
			ofs = zDom.revisedOffset(n); // Bug #1812154
			
		if (wgt._insizer(pointer[0] - ofs[0])) {
			dg._zmin = 10 + zDom.frameWidth(n);		
				return false;
		}
		return true;
	},
	_aftersizing: function (dg, evt) {
		var wgt = dg.control,
			n = wgt.getNode(),
			owner = wgt.getOwner(),
			wd = dg._zszofs,
			table = owner.eheadtbl,
			head = table.tBodies[0].rows[0], 
			rwd = zDom.revisedWidth(n, wd),
			cells = head.cells,
			cidx = zDom.cellIndex(n),
			total = 0;
			
		for (var k = cells.length; --k >= 0;)
			if (k !== cidx) total += cells[k].offsetWidth;

		// For Opera, the code of adjusting width must be in front of the adjusting table.
		// Otherwise, the whole layout in Opera always shows wrong.
		if (owner.efoottbl) {
			owner.eftfaker.cells[cidx].style.width = wd + "px";
		}
		var fixed;
		if (owner.ebodytbl) {
			if (zk.opera && !owner.ebodytbl.style.tableLayout) {
				fixed = "auto";
				owner.ebodytbl.style.tableLayout = "fixed";
			}
			owner.ebdfaker.cells[cidx].style.width = wd + "px";
		}
		
		head.cells[cidx].style.width = wd + "px";
		n.style.width = rwd + "px";
		var cell = n.firstChild;
		cell.style.width = zDom.revisedWidth(cell, rwd) + "px";
		
		table.style.width = total + wd + "px";		
		if (owner.efoottbl)
			owner.efoottbl.style.width = table.style.width;
		
		if (owner.ebodytbl)
			owner.ebodytbl.style.width = table.style.width;
			
		if (zk.opera && fixed) owner.ebodytbl.style.tableLayout = fixed;
		
		wgt.parent.fire('onColSize', {
			index: cidx,
			uuid: wgt.uuid,
			width: wd + "px",
			keys: zEvt.keyMetaData(evt),
			marshal: wgt.$class._onSizingMarshal
		}, null, 0);
	}
});
