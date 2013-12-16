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
			if (wgt) {
				// B70-ZK-2036: Clear min width cache before rerender.
				wgt._minWd = null;
				wgt.rerender();
			}
		}
	},
	setFlexSize_: function (sz) {
		if ((sz.width !== undefined && sz.width != 'auto' && sz.width != '') || sz.width == 0) { //JavaScript deems 0 == ''
			//remember the value in _hflexWidth and use it when rerender(@see #domStyle_)
			//for faker column, so don't use revisedWidth().
			//updated: need to concern inner padding due to wgt.getContentEdgeWidth_()
			//spec in flex.js
			var rvw = this._hflex == 'min' && this.firstChild && this.isRealVisible() ? // B50-ZK-394
					zk(this.$n('cave')).revisedWidth(sz.width) : sz.width;
			this._hflexWidth = rvw;
			return {width: rvw};
		} else
			this.$supers('setFlexSize_', arguments);
	},
	getContentEdgeHeight_: function () {
		return zk(this).sumStyles('tb', jq.margins);
	},
	getContentEdgeWidth_: function() {
		return zk(this).sumStyles('lr', jq.margins);
	},
	domStyle_: function (no) {
		var style = '';
		if (this._hflexWidth) { //handle hflex
			style = 'width: ' + this._hflexWidth + 'px;';

			if (no) no.width = true;
			else no = {width:true};
		}
		if (this._align)
			style += 'text-align:' + this._align + ';';
		if (this._valign)
			style += 'vertical-align:' + this._valign + ';';

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
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			this.$supers('setVisible', arguments);
			this.updateMesh_('visible', visible);
		}
	},
	getTextNode: function () {
		return jq(this.$n()).find('>div:first')[0];
	},
	bind_: function () {
		this.$supers(zul.mesh.HeaderWidget, 'bind_', arguments);
		if (this.parent.isSizable())
			this._initsz();
		var mesh = this.getMeshWidget();
		if (mesh) {
			var $n = jq(this.$n()),
				$faker = jq(this.$n('hdfaker')),
				w = this.getWidth();
			if (!this.isVisible()) {
				// B70-ZK-2036: Set width to 1px if browser is safari.
				var wd = zk.chrome ? '0.1px' : zk.safari ? '1px' : '0';
				$n.css('display', '');
				// B70-ZK-2036: Change the header width.
				$n.css('width', wd);
				$n.css('visibility', 'hidden');
				$faker.css('display', '');
				$faker.css('visibility', 'hidden');
				$faker.css('width', wd);
			} else {
				$faker.css('visibility', '');
				// B70-ZK-2036: Check if header has hflex width first.
				if (!this._hflexWidth && w) {
					$faker.css('width', w);
				}
			}
		}
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
				revert: true,
				constraint: 'horizontal',
				ghosting: $Header._ghostsizing,
				endghosting: $Header._endghostsizing,
				snap: $Header._snapsizing,
				ignoredrag: $Header._ignoresizing,
				zIndex: 99999, // Bug: B50-3285153
				endeffect: $Header._aftersizing
			});
		}
	},
	doClick_: function (evt) {
		var tg = evt.domTarget,
			wgt = zk.Widget.$(tg),
			n = this.$n(),
			ofs = this._dragsz ? zk(n).revisedOffset() : false,
			btn = wgt.$n('btn'),
			ignoreSort = false;
		
		//IE will trigger doClick during closing menupopup
		if (zk.ie < 11 && btn && !zk(btn).isRealVisible())
			ignoreSort = true;
		
		if (!zk.dragging && (wgt == this || wgt.$instanceof(zul.wgt.Label)) 
				&& this.isSortable_() && !jq.nodeName(tg, 'input') 
				&& (!this._dragsz || !this._insizer(evt.pageX - ofs[0])) 
				&& !ignoreSort) {
			this.fire('onSort', 'ascending' != this.getSortDirection()); // B50-ZK-266
			evt.stop();
		} else {
			if (jq.nodeName(tg, 'input'))
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
		if (zk.dragging || !this.parent.isSizable())
			return;
		var n = this.$n(),
			ofs = zk(n).revisedOffset(); // Bug #1812154
		if (this._insizer(evt.pageX - ofs[0])) {
			jq(n).addClass(this.$s('sizing'));
		} else {
			jq(n).removeClass(this.$s('sizing'));
		}
	},
	doMouseOut_: function (evt) {
		if (this.parent.isSizable()) {
			var n = this.$n();
			jq(n).removeClass(this.$s('sizing'));
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
			// Bug #3255116
			if (mw.ebody) {
				if (zk.ie < 11) { //Related bugs: ZK-890 and ZK-242
					if (mw.ebodytbl && !mw.ebodytbl.width) {
						mw.ebodytbl.width = '100%';
						// reset the width for IE
					}
				}
			}
			return {
				height: zkp.contentHeight(),
				width: zkp.contentWidth()
			}
		}
		return {};
	},
	isWatchable_: function (name, p, cache) {
		//Bug 3164504: Hflex will not recalculate when the colum without label
		//Cause: DIV (parent of HeadWidget) is invisible if all columns have no label
		var wp;
		return this._visible && (wp = this.parent) && wp._visible //check this and HeadWidget
			&& (wp = wp.parent) && wp.isWatchable_(name, p, cache); //then MeshWidget.isWatchable_
	},
	_insizer: function (x) {
		return x >= this.$n().offsetWidth - 8;
	},
	deferRedrawHTML_: function (out) {
		out.push('<th', this.domAttrs_({domClass:1}), ' class="z-renderdefer"></th>');
	}
}, { //static
	_faker: ['hdfaker', 'bdfaker', 'ftfaker'],

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
			mesh = wgt.getMeshWidget(),
			wd = dg._zszofs,
			hdfaker = mesh.ehdfaker,
			bdfaker = mesh.ebdfaker,
			ftfaker = mesh.eftfaker,
			cidx = zk(wgt.$n()).cellIndex();
		
		//1. set resized width to colgroup col
		var hdcols = hdfaker.childNodes,
			bdcols = bdfaker.childNodes;
		hdcols[cidx].style.width = bdcols[cidx].style.width = jq.px(wd);
			
		//2. store resized width
		var wds = [];
		for (var w = mesh.head.firstChild, i = 0; w; w = w.nextSibling, i++) {
			var stylew = hdcols[i].style.width,
				origWd = w._origWd; // ZK-1022: get original width if it is shrinked by Frozen.js#_doScrollNow
			
			if (origWd) {
				w._width = wds[i] = origWd;
			} else {
				w._width = wds[i] = stylew ? stylew : jq.px0(w.$n().offsetWidth);
			}
			
			if (!stylew)
				hdcols[i].style.width = bdcols[i].style.width = w._width;
		}
		
		//3. clear width=100% setting, otherwise it will try to expand to whole width
		mesh.eheadtbl.width = '';
		mesh.ebodytbl.width = '';
		if (mesh.efoottbl)
			mesh.efoottbl.width = '';
		
		delete mesh._span; //no span!
		delete mesh._sizedByContent; //no sizedByContent!
		for (var w = mesh.head.firstChild; w; w = w.nextSibling)
			w.setHflex_(null); //has side effect of setting w.$n().style.width of w._width
		
		wgt.parent.fire('onColSize', zk.copy({
			index: cidx,
			column: wgt,
			width: wd + 'px',
			widths: wds
		}, evt.data), null, 0);
		
		// bug #2799258 in IE, we have to force to recalculate the size.
		mesh.$n()._lastsz = null;
		
		// bug #2799258
		zUtl.fireSized(mesh, -1); //no beforeSize
	},

	redraw: function (out) {
		var uuid = this.uuid,
			zcls = this.getZclass(),
			label = this.domContent_();
		out.push('<th', this.domAttrs_({width: true}), '><div id="',
			uuid, '-cave" class="', this.$s('content'), '"',
			this.domTextStyleAttr_(), '><div class="', this.$s('sorticon'), 
			'"><i id="', uuid, '-sort-icon"></i></div>',
			((!this.firstChild && label == '' ) ? "&nbsp;" : label)); //ZK-805 MenuPopup without columns issue

		if (this.parent._menupopup && this.parent._menupopup != 'none')
			out.push('<a id="', uuid, '-btn" href="javascript:;" class="',
				this.$s('button'), '"><i class="z-icon-caret-down"></i></a>');

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div></th>');
	}
});
