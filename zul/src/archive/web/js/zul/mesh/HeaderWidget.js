/* HeaderWidget.js

	Purpose:
		
	Description:
		
	History:
		Mon Dec 29 17:33:15     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for a header.
 */
zul.mesh.HeaderWidget = zk.$extends(zul.LabelImageWidget, {
	_sumWidth: true, //indicate shall add this width for MeshWidget. @See _fixFlex in widget.js
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
			if (wgt)
				wgt.rerender(0); //defer it since it might be called multiple times
		}
	},
	setFlexSize_: function (sz) {
		if ((sz.width !== undefined && sz.width != 'auto' && sz.width != '') || sz.width == 0) { //JavaScript deems 0 == '' 
			//remember the value in _hflexWidth and use it when rerender(@see #domStyle_)
			//for faker column, so don't use revisedWidth().
			//updated: need to concern inner padding due to _getContentEdgeWidth
			//spec in flex.js
			var rvw = this._hflex == 'min' && this.firstChild && this.isRealVisible() ? // B50-ZK-394
					zk(this.$n('cave')).revisedWidth(sz.width) : sz.width;
			this._hflexWidth = rvw;
			return {width: rvw};
		} else
			return this.$supers('setFlexSize_', arguments);
	},
	domStyle_: function (no) {
		var style = '';
		if (this._hflexWidth) { //handle hflex
			style = 'width: ' + this._hflexWidth + 'px;';
			
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
				zIndex: 99999, // Bug: B50-3285153
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
			this.fire('onSort', "ascending" != this.getSortDirection()); // B50-ZK-266
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
				mesh._calcMinWds();
				var sz = mesh._minWd.wds[cIndex];
				this.$class._aftersizing({control: this, _zszofs: sz}, evt);
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
			var n = this.$n();
			jq(n).removeClass(this.getZclass() + "-sizing");
		}
		this.$supers('doMouseOut_', arguments);
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
	listenOnFitSize_: zk.$void, // skip flex
	unlistenOnFitSize_: zk.$void,
	//@Override to find the minimum width of listheader
	beforeMinFlex_: function(o) {
		if (o == 'w') {
			var wgt = this.getMeshWidget();
			if (wgt) { 
				wgt._calcMinWds();
				if (wgt._minWd) {
					var n = this.$n(), zkn = zk(n),
						cidx = zkn.cellIndex();
					return zkn.revisedWidth(wgt._minWd.wds[cidx]);
				}
			}
		}
		return null;
	},
	clearCachedSize_: function() {
		this.$supers('clearCachedSize_', arguments);
		var mw;
		if (mw = this.getMeshWidget())
			mw._clearCachedSize();
	},
	//@Override to get width/height of MeshWidget 
	getParentSize_: function() {
		//to be overridden
		var mw = this.getMeshWidget(),
			p = mw.$n(),
			zkp = p ? zk(p) : null;
		if (zkp) {
			var w = zkp.revisedWidth(p.offsetWidth);
			// Bug #3255116
			if (mw.ebody) {
				var scroll = mw.ebody.offsetWidth - mw.ebody.clientWidth; // zkq#hasVScroll() cause issue in IE9
				if (scroll > 11) {
					w -= scroll;
					
					// For bug #3255116, we have to avoid IE to appear the hor. scrollbar.
					if (zk.ie) {
						var hdflex = jq(mw.ehead).find('table>tbody>tr>th:last-child')[0];
						hdflex.style.width = '';
						if (mw.ebodytbl)
							mw.ebodytbl.width = "";
					}
				} else if (zk.ie && mw.ebodytbl) {
					// reset the width for IE
					if (mw.ebodytbl && !mw.ebodytbl.width)
						mw.ebodytbl.width = "100%";
				}
			}
			return {
				height: zkp.revisedHeight(p.offsetHeight),
				width: w
			}
		}
		return {};
	},
	isWatchable_: function (name, p, cache) {
		//Bug 3164504: Hflex will not recalculate when the colum without label
		//Cause: DIV (parent of HeadWidget) is invisible if all columns have no label
		var wp;
		return this._visible && (wp=this.parent) && wp._visible //check this and HeadWidget
			&& (wp=wp.parent) && wp.isWatchable_(name, p, cache); //then MeshWidget.isWatchable_
	},
	_insizer: function (x) {
		return x >= this.$n().offsetWidth - 10;
	},
	deferRedrawHTML_: function (out) {
		out.push('<th', this.domAttrs_({domClass:1}), ' class="z-renderdefer"></th>');
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
		var fixed, disp;
		if (mesh.ebodytbl) {
			if (zk.opera && !mesh.ebodytbl.style.tableLayout) {
				fixed = 'auto';
				mesh.ebodytbl.style.tableLayout = "fixed";
			}
			mesh.ebdfaker.cells[cidx].style.width = wd + "px";
		}
		
		head.cells[cidx].style.width = wd + "px";
		n.style.width = rwd + "px";
		var cell = n.firstChild;
		cell.style.width = zk(cell).revisedWidth(rwd) + "px";
		
		//feature#3177275: Listheader should override hflex when sized by end user
		var hdfakercells = mesh.ehdfaker.cells,
			bdfakercells = mesh.ebdfaker.cells,
			wds = [];
			i = 0;
		for (var w = mesh.head.firstChild, i = 0; w; w = w.nextSibling) {
			var stylew = hdfakercells[i].style.width;
			w._width = wds[i] = stylew ? stylew : jq.px0(hdfakercells[i].offsetWidth); //bug#3180189. setWidth() has side effect
			if (!stylew) //bug#3183228.
				bdfakercells[i].style.width = hdfakercells[i].style.width = w._width;
			++i;
		}
		
		delete mesh._span; //no span!
		delete mesh._sizedByContent; //no sizedByContent!
		for (var w = mesh.head.firstChild; w; w = w.nextSibling)
			w.setHflex_(null); //has side effect of setting w.$n().style.width of w._width
		
		//bug#3147926: auto fit. 
		//Adjust hdfakerflex/bdfakerflex
		var hdflex = jq(mesh.ehead).find('table>tbody>tr>th:last-child')[0],
			bdflex = jq(mesh.ebody).find('table>tbody>tr>th:last-child')[0],
			hdflexVal = hdflex.style.width;
		
		hdflex.style.width = '';
		
		// fixed for B50-3183228.zul unexpected hor. scrollbar
		if (zk.ie < 8) {
			if (hdflex.offsetWidth == 1)
				hdflex.style.width = hdflexVal;
		} 
		if (bdflex) {
			var bdflexVal = bdflex.style.width;
			bdflex.style.width = '';
			
			// fixed for B50-3183228.zul unexpected hor. scrollbar
			if (zk.ie < 8) {
				if (bdflex.offsetWidth == 1)
					bdflex.style.width = bdflexVal;
			}
		}
		
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
			width: wd + "px",
			widths: wds
		}, evt.data), null, 0);
		
		// bug #2799258 in IE, we have to force to recalculate the size.
		meshn._lastsz = null;
		
		// bug #2799258
		zUtl.fireSized(mesh, -1); //no beforeSize
		
		// fixed for B50-3147926.zul
		if (zk.ie < 8)
			zk(mesh).redoCSS();
	},

	redraw: function (out) {
		var uuid = this.uuid,
			zcls = this.getZclass();
		out.push('<th', this.domAttrs_(), '><div id="', uuid, '-cave" class="',
				zcls, '-cnt"', this.domTextStyleAttr_(), '><div class="', zcls, '-sort-img"></div>', this.domContent_());

		if (this.parent._menupopup && this.parent._menupopup != 'none')
			out.push('<a id="', uuid, '-btn"  href="javascript:;" class="', zcls, '-btn"></a>');
	
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div></th>');	
	}
});
