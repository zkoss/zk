/* HeaderWidget.js

	Purpose:
		
	Description:
		
	History:
		Mon Dec 29 17:33:15     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.mesh.HeaderWidget = zk.$extends(zul.LabelImageWidget, {
	$define: {
		align: function (v) {
			this.updateMesh_('align', v);
		},
		valign: function (v) {
			this.updateMesh_('valign', v);
		},
		width: _zkf = function () {
			this.updateMesh_();
		},
		height: _zkf
	},
	updateMesh_: function (nm, val) { //TODO: don't rerender
		if (this.desktop) {
			var wgt = this.getMeshWidget();
			if (wgt) {
				if (wgt._inUpdateMesh) {//recursive back, cannot rerender now; avoid endless loop
					wgt._flexRerender = true; //mark to do rerender later
					return;
				}
				wgt._inUpdateMesh = true;
				try {
					if (!this.parent._inAfterChildrenFlex) { //reset all hflex columns
						for(var kid = this.parent.firstChild; kid; kid = kid.nextSibling)
							delete kid._hflexWidth;
					}
					wgt.rerender(); //might recursive back via HeadWidget#afterChildrenFlex_()
					if (wgt._flexRerender) {//was mark to do rerender in #updateMesh_()
						try {
							wgt.rerender();
						} finally {
							delete wgt._flexRerender;
						}
					}
				} finally {
					delete wgt._inUpdateMesh;
				}
			}
		}
	},
	setFlexSize_: function (sz) { //TODO: if updateMesh_ is not implemented with rerender
		if (sz.width !== undefined && sz.width != 'auto' && sz.width != '') {
			//remember the value in _hflexWidth and use it when rerender(@see #domStyle_)
			//for faker column, so don't use revisedWidth().
			this._hflexWidth = jq.px(sz.width);
			return {width: sz.width};
		} else
			return this.$supers('setFlexSize_', arguments);
	},
	getParentSize_: function (p) {
		var wgt = this.getMeshWidget();
		if (zk(wgt.ehead).isVisible()) 
			return this.$supers('getParentSize_', arguments);
		else {
			var xp = wgt.$n();
			return {height: 0, width: zk(xp).revisedWidth(xp.offsetWidth)};
		}
	},
	domStyle_: function (no) {
		var style = '';
		if (this._hflexWidth) { //handle hflex
			style = 'width:'+ this._hflexWidth+';';
			
			if (no) no.width = true;
			else no = {width:true};
		}
		return style + this.$super('domStyle_', no);
	},
	getMeshWidget: function () {
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
			this.updateMesh_('visible', visible);
		}
	},
	domAttrs_: function (no) {
		var attrs = this.$supers('domAttrs_', arguments);
		return attrs + this.getColAttrs();
	},
	getTextNode: function () {
		return jq(this.$n()).find('>div:first')[0];
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.parent.isSizable()) this._initsz();
		this.fixedFaker_();
	},
	unbind_: function () {
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}
		this.$supers('unbind_', arguments);
	},
	_initsz: function () {
		var n = this.$n();
		if (n && !this._drag) {
			var $Header = this.$class;
			this._drag = new zk.Draggable(this, null, {
				revert: true, constraint: "horizontal",
				ghosting: $Header._ghostsizing,
				endghosting: $Header._endghostsizing,
				snap: $Header._snapsizing,
				ignoredrag: $Header._ignoresizing,
				zIndex: 1000,
				endeffect: $Header._aftersizing
			});
		}
	},
	fixedFaker_: function () {
		var n = this.$n(),
			index = zk(n).cellIndex(),
			owner = this.getMeshWidget();
		for (var faker, fs = this.$class._faker, i = fs.length; i--;) {
			faker = owner['e' + fs[i]]; // internal element
			if (faker && !this.$n(fs[i])) 
				faker[faker.cells.length > index ? "insertBefore" : "appendChild"]
					(this._createFaker(n, fs[i]), faker.cells[index]);
		}
	},
	_createFaker: function (n, postfix) {
		var t = document.createElement("TH"), 
			d = document.createElement("DIV");
		t.id = n.id + "-" + postfix;
		t.className = n.className;
		t.style.cssText = n.style.cssText;
		d.style.overflow = "hidden";
		t.appendChild(d);
		return t;
	},
	doClick_: function (evt) {
		var wgt = zk.Widget.$(evt.domTarget),
			n = this.$n(),
			ofs = this._drag ? zk(n).revisedOffset() : false;
		if (!zk.dragging && (wgt == this || wgt.$instanceof(zul.wgt.Label)) && this.isSortable_() &&
				evt.domTarget.tagName != "INPUT" && (!this._drag || !this._insizer(evt.pageX - ofs[0]))) {
			this.fire('onSort');
			evt.stop();
		} else {
			if (evt.domTarget.tagName == "INPUT")
				evt.stop({propagation: true});
			this.$supers('doClick_', arguments);
		}
	},
	doDoubleClick_: function (evt) {
		if (this._drag) {
			var n = this.$n(),
				$n = zk(n),
				ofs = $n.revisedOffset();
			if (this._insizer(evt.pageX - ofs[0])) {
				var mesh = this.getMeshWidget(),
					max = zk(this.$n('cave')).textSize()[0],
					cIndex = $n.cellIndex();
				for (var rows = mesh.ebodyrows, len = rows.length; len--;) {
					var cell = rows[len].cells[cIndex], $c;
					if (cell && ($c = zk(cell)).isVisible()) {
						var size = $c.jq.find('div:first-child').zk.textSize();
						if (max < size[0])
							max = size[0];
					}
				}
				max += $n.padBorderWidth();
				this.$class._aftersizing({control: this, _zszofs: max + (this.isSortable_() ? 20 : 0)}, evt);
			} else
				this.$supers('doDoubleClick_', arguments);
		} else
			this.$supers('doDoubleClick_', arguments);
	},
	doMouseMove_: function (evt) {
		if (zk.dragging || !this.parent.isSizable()) return;
		var n = this.$n(),
			ofs = zk(n).revisedOffset(); // Bug #1812154
		if (this._insizer(evt.pageX - ofs[0])) {
			jq(n).addClass(this.getZclass() + "-sizing");
		} else {
			jq(n).removeClass(this.getZclass() + "-sizing");
		}
	},
	doMouseOut_: function (evt) {
		if (this.parent.isSizable()) {
			var n = this.$n()
			jq(n).removeClass(this.getZclass() + "-sizing");
		}
	},
	_insizer: function (x) {
		return x >= this.$n().offsetWidth - 10;
	}
}, { //static
	_faker: ["hdfaker", "bdfaker", "ftfaker"],
	
	//drag
	_ghostsizing: function (dg, ofs, evt) {
		var wgt = dg.control,
			el = wgt.getMeshWidget().eheadtbl,
			of = zk(el).revisedOffset(),
			n = wgt.$n();
		
		ofs[1] = of[1];
		ofs[0] += zk(n).offsetWidth();
		jq(document.body).append(
			'<div id="zk_hdghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:3px;height:'+zk(el.parentNode.parentNode).offsetHeight()
			+'px;background:darkgray"></div>');
		return jq("#zk_hdghost")[0];
	},
	_endghostsizing: function (dg, origin) {
		dg._zszofs = zk(dg.node).revisedOffset()[0] - zk(origin).revisedOffset()[0];
	},
	_snapsizing: function (dg, pointer) {
		var n = dg.control.$n(), $n = zk(n),
			ofs = $n.revisedOffset();
		pointer[0] += $n.offsetWidth(); 
		if (ofs[0] + dg._zmin >= pointer[0])
			pointer[0] = ofs[0] + dg._zmin;
		return pointer;
	},
	_ignoresizing: function (dg, pointer, evt) {
		var wgt = dg.control,
			n = wgt.$n(), $n = zk(n),
			ofs = $n.revisedOffset(); // Bug #1812154
			
		if (wgt._insizer(pointer[0] - ofs[0])) {
			dg._zmin = 10 + $n.padBorderWidth();
			return false;
		}
		return true;
	},
	_aftersizing: function (dg, evt) {
		var wgt = dg.control,
			n = wgt.$n(), $n = zk(n),
			owner = wgt.getMeshWidget(),
			wd = dg._zszofs,
			table = owner.eheadtbl,
			head = table.tBodies[0].rows[0], 
			rwd = $n.revisedWidth(wd),
			cells = head.cells,
			cidx = $n.cellIndex(),
			total = 0;
			
		for (var k = cells.length; k--;)
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
		cell.style.width = zk(cell).revisedWidth(rwd) + "px";
		
		table.style.width = total + wd + "px";
		if (owner.efoottbl)
			owner.efoottbl.style.width = table.style.width;
		
		if (owner.ebodytbl)
			owner.ebodytbl.style.width = table.style.width;
			
		if (zk.opera && fixed) owner.ebodytbl.style.tableLayout = fixed;
		
		wgt.parent.fire('onColSize', zk.copy({
			index: cidx,
			column: wgt,
			width: wd + "px"
		}, evt.data), null, 0);
		
		var mesh = wgt.getMeshWidget();
		
		// bug #2799258 in IE, we have to force to recalculate the size.
		mesh.$n()._lastsz = null;
		
		// bug #2799258
		zWatch.fireDown('onSize', mesh);
	},

	redraw: function (out) {
		var uuid = this.uuid,
			zcls = this.getZclass();
		out.push('<th', this.domAttrs_(), '><div id="', uuid, '-cave" class="',
				zcls, '-cnt"', this.domTextStyleAttr_(), '>', this.domContent_());

		if (this.parent._menupopup && this.parent._menupopup != 'none')
			out.push('<a id="', uuid, '-btn"  href="javascript:;" class="', zcls, '-btn"></a>');
	
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div></th>');	
	}
});
