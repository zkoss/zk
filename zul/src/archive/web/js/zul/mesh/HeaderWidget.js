/* HeaderWidget.js

	Purpose:
		
	Description:
		
	History:
		Mon Dec 29 17:33:15     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for a header.
 */
zul.mesh.HeaderWidget = zk.$extends(zul.LabelImageWidget, {
	$define: {
    	/** Returns the horizontal alignment of this column.
    	 * <p>Default: null (system default: left unless CSS specified).
    	 * @return String
    	 */
    	/** Sets the horizontal alignment of this column.
    	 * @param String align
    	 */
		align: function (v) {
			this.updateMesh_('align', v);
		},
		/** Returns the vertical alignment of this grid.
		 * <p>Default: null (system default: top).
		 * @return String
		 */
		/** Sets the vertical alignment of this grid.
		 * @param String valign
		 */
		valign: function (v) {
			this.updateMesh_('valign', v);
		},
		width: _zkf = function () {
			this.updateMesh_();
		},
		height: _zkf
	},
	/**
	 * Updates the whole mesh widget.
	 * @param String name
	 * @param Object value
	 */
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
					for(var kid = this.parent.firstChild; kid; kid = kid.nextSibling)
						delete kid._hflexWidth;
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
	setFlexSize_: function (sz) {
		if (sz.width !== undefined && sz.width != 'auto' && sz.width != '') {
			//remember the value in _hflexWidth and use it when rerender(@see #domStyle_)
			//for faker column, so don't use revisedWidth().
			this._hflexWidth = sz.width;
			return {width: sz.width};
		} else
			return this.$supers('setFlexSize_', arguments);
	},
	getParentSize_: function (p) {
		var wgt = this.getMeshWidget();
		if (zk(wgt.ehead).isVisible()) {
			//bug# 3033010
			//ie's header width shrink, so must get the one from hdfaker
			var sz = this.$supers('getParentSize_', arguments);
			if (zk.ie) {
				var hdfaker = wgt.ehdfaker;
				sz.width = zk(hdfaker).revisedWidth(hdfaker.offsetWidth);
			}
			return sz;
		} else {
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
		if (zk.ie8 && this._align)
			style += 'text-align:' + this._align + ';';
		
		return style + this.$super('domStyle_', no);
	},
	/**
	 * Returns the mesh widget that this belongs to.
	 * @return zul.mesh.MeshWidget
	 */
	getMeshWidget: function () {
		return this.parent ? this.parent.parent : null;
	},
	/**
	 * Returns whether the widget is sortable or not.
	 * <p> Default: false.
	 * @return boolean
	 */
	isSortable_: function () {
		return false;
	},
	/**
	 * Returns the column attributes. i.e. {@link #getAlign} and {@link #getValign}
	 * in HTML format. (Like a="b")
	 * @return String
	 */
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
		this.$supers(zul.mesh.HeaderWidget, 'bind_', arguments);
		if (this.parent.isSizable()) this._initsz();
		this.fixFaker_();
	},
	unbind_: function () {
		if (this._dragsz) {
			this._dragsz.destroy();
			this._dragsz = null;
		}
		this.$supers(zul.mesh.HeaderWidget, 'unbind_', arguments);
	},
	_initsz: function () {
		var n = this.$n();
		if (n && !this._dragsz) {
			var $Header = this.$class;
			this._dragsz = new zk.Draggable(this, null, {
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
	/**
	 * Fixes the faker (an visible row for adjusting column), if any.
	 */
	fixFaker_: function () {
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
		var t = document.createElement("th"), 
			d = document.createElement("div");
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
			ofs = this._dragsz ? zk(n).revisedOffset() : false;
		if (!zk.dragging && (wgt == this || wgt.$instanceof(zul.wgt.Label)) && this.isSortable_() &&
		!jq.nodeName(evt.domTarget, "input") && (!this._dragsz || !this._insizer(evt.pageX - ofs[0]))) {
			this.fire('onSort');
			evt.stop();
		} else {
			if (jq.nodeName(evt.domTarget, "input"))
				evt.stop({propagation: true});
			this.$supers('doClick_', arguments);
		}
	},
	doDoubleClick_: function (evt) {
		if (this._dragsz) {
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
	ignoreDrag_: function (pt) {
		if (this.parent.isSizable()) {
			var n = this.$n(),
				ofs = zk(n).revisedOffset();
			return this._insizer(pt[0] - ofs[0]);
		}
		return false;
	},
	//@Override to avoid add child offset 
	ignoreChildNodeOffset_: function(attr) {
		return true;
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
			mesh = wgt.getMeshWidget(),
			wd = dg._zszofs,
			table = mesh.eheadtbl,
			head = table.tBodies[0].rows[0], 
			rwd = $n.revisedWidth(wd),
			cidx = $n.cellIndex();
			
		// For Opera, the code of adjusting width must be in front of the adjusting table.
		// Otherwise, the whole layout in Opera always shows wrong.
		if (mesh.efoottbl) {
			mesh.eftfaker.cells[cidx].style.width = wd + "px";
		}
		var fixed;
		if (mesh.ebodytbl) {
			if (zk.opera && !mesh.ebodytbl.style.tableLayout) {
				fixed = "auto";
				mesh.ebodytbl.style.tableLayout = "fixed";
			}
			mesh.ebdfaker.cells[cidx].style.width = wd + "px";
		}
		
		head.cells[cidx].style.width = wd + "px";
		n.style.width = rwd + "px";
		var cell = n.firstChild;
		cell.style.width = zk(cell).revisedWidth(rwd) + "px";
		
		//bug 3061765: unexpected horizontal scrollbar when sizing
/*		table.style.width = total + wd + "px";
		
		if (mesh.efoottbl)
			mesh.efoottbl.style.width = table.style.width;
		
		if (mesh.ebodytbl)
			mesh.ebodytbl.style.width = table.style.width;
*/			
		var meshn = mesh.$n();
		if (zk.opera) {
			if(fixed) 
				mesh.ebodytbl.style.tableLayout = fixed;
			
			//bug 3061764: Opera only. Cannot sizing a column with width
			var olddisp = meshn.style.display; //force redraw
			meshn.style.display='none';
			var redrawFix = meshn.offsetHeight;
			meshn.style.display=olddisp;
		}
		
		wgt.parent.fire('onColSize', zk.copy({
			index: cidx,
			column: wgt,
			width: wd + "px"
		}, evt.data), null, 0);
		
		// bug #2799258 in IE, we have to force to recalculate the size.
		meshn._lastsz = null;
		
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
