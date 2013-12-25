/* Layout.js

	Purpose:

	Description:

	History:
		Fri Aug  6 16:13:00 TST 2010, Created by jumperchen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeleton of Vlayout and Hlayout.
 * @since 5.0.4
 */
zul.box.Layout = zk.$extends(zk.Widget, {
	_spacing: '5px',
	$define: {
		/** Sets the spacing between adjacent children.
	 	 * @param String spacing the spacing (such as "0", "5px", "3pt" or "1em"),
	 	 * or null to use the default spacing. If the spacing is set to "auto",
	 	 * the DOM style is left intact, so the spacing can be customized from
	 	 * CSS.
	 	 * @see #getSpacing
	 	 */
		/** Returns the spacing between adjacent children, or null if the default
	 	 * spacing is used.
	 	 *
	 	 * <p>Default: 0.3em (means to use the default spacing).
	 	 * @return String
	 	 */
		spacing: function () {
			var n = this.$n(),
				vert = this.isVertical_(),
				spc = this._spacing;
			if (n)
				jq(n).children('div:not(:last-child)').css('padding-' + (vert ? 'bottom' : 'right'), (spc && spc != 'auto') ? spc : '');
		}
	},
	_chdextr: function (child) {
		return child.$n('chdex') || child.$n();
	},
	insertChildHTML_: function (child, before, desktop) {
		if (before)
			jq(this._chdextr(before)).before(this.encloseChildHTML_(child));
		else {
			var jqn = jq(this.$n()),
			spc = this._spacing;
			jqn.children('div:last-child').css('padding-' + (this.isVertical_() ? 'bottom' : 'right'), (spc && spc != 'auto') ? spc : '');
			jqn.append(this.encloseChildHTML_(child));
		}
		child.bind(desktop);
	},
	bind_: function () {
		this.$supers(zul.box.Layout, 'bind_', arguments);
		zWatch.listen({onResponse: this});
	},
	unbind_: function () {
		zWatch.unlisten({onResponse: this});
		this.$supers(zul.box.Layout, 'unbind_', arguments);
	},
	/** Synchronizes the size immediately.
	 * This method is called automatically if the widget is created
	 * at the server (i.e., {@link #inServer} is true).
	 * You have to invoke this method only if you create this widget
	 * at client and add or remove children from this widget.
	 * @since 5.0.8
	 */
	syncSize: function () {
		this._shallSize = false;
		if (this.desktop) {
			// only fire when child has h/vflex
			for (var w = this.firstChild; w; w = w.nextSibling) {
				if (w._nvflex || w._nhflex) {
					zUtl.fireSized(this);
					break;
				}
			}
		}
	},
	onResponse: function () {
		if (this._shallSize)
			this.syncSize();
	},
	//Bug ZK-1579: should resize if child's visible state changed.
	onChildVisible_: function (child) {
		this.$supers('onChildVisible_', arguments);
		if (this.desktop) {
			this._shallSize = true;
			//Bug ZK-1650: change chdex display style according to child widget
			child.$n('chdex').style.display = child.isVisible() ? '' : 'none';
		}
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (this.desktop) {
			this._shallSize = true;
			//Bug ZK-1732: change chdex display style according to child widget
			child.$n('chdex').style.display = child.isVisible() ? '' : 'none';
		}
	},
	onChildRemoved_: function () {
		this.$supers('onChildRemoved_', arguments);
		if (this.desktop)
			this._shallSize = true;
	},
	removeChildHTML_: function (child) {
		this.$supers('removeChildHTML_', arguments);
		jq(child.uuid + '-chdex', zk).remove();
		var rmsp = this.lastChild == child;
		if(this._spacing != 'auto' && this.lastChild == child)
			jq(this.$n()).children('div:last-child').css('padding-' + (this.isVertical_() ? 'bottom' : 'right'), '');
	},
	/** Enclose child with HTML tag such as DIV,
	 * and return a HTML code or add HTML fragments in out array.
	 * @param zk.Widget child the child which will be enclosed
	 * @param Array out an array of HTML fragments.
	 * @return String
	 */
	encloseChildHTML_: function (child, out) {
		var oo = [],
			vert = this.isVertical_(),
			spc = this._spacing;

		oo.push('<div id="', child.uuid, '-chdex" class="', this.$s('inner'), '"');
		if (spc && spc != 'auto') {
			oo.push(' style="', !child.isVisible() ? 'display:none;' : ''); //Bug ZK-1650: set chdex display style according to child widget
			var next = child.nextSibling; //Bug ZK-1526: popup should not consider spacing
			if (next && !next.$instanceof(zul.wgt.Popup))
				oo.push('padding-', vert ? 'bottom:' : 'right:', spc);
			oo.push('"');
		}
		oo.push('>');
		child.redraw(oo);
		oo.push('</div>');
		if (!out) return oo.join('');

		for (var j = 0, len = oo.length; j < len; ++j)
			out.push(oo[j]);
	},
	/**
	 * Returns whether the layout is vertical
	 * @return boolean
	 */
	isVertical_: zk.$void,
	_resetBoxSize: function (vert) {
		for (var kid = this.firstChild; kid; kid = kid.nextSibling) {
			var chdex = kid.$n('chdex');
			// ZK-1861: Js error when flex + visible = false
			if (chdex) {
				//ZK-1679: clear height only vflex != min, clear width only hflex != min
				if (vert && kid._nvflex && kid.getVflex() != 'min') {
					var n;
					if ((n = kid.$n()) && (n.scrollTop || n.scrollLeft)) // keep the scroll status
						;// do nothing Bug ZK-1885: scrollable div (with vflex) and tooltip
					else
						kid.setFlexSize_({height:'', width:''});
					if (chdex)
						chdex.style.height = '';
				}
				if (!vert && kid._nhflex && kid.getHflex() != 'min') {
					var n;
					if ((n = kid.$n()) && (n.scrollTop || n.scrollLeft)) // keep the scroll status
						;// do nothing Bug ZK-1885: scrollable div (with vflex) and tooltip
					else
						kid.setFlexSize_({height:'', width:''});
					if (chdex)
						chdex.style.width = '';
				}
			}
		}
	},
	//bug#3296056
	afterResetChildSize_: function(orient) {
		for (var kid = this.firstChild; kid; kid = kid.nextSibling) {
			var chdex = kid.$n('chdex');
			if (chdex) {
				if (orient == 'h')
					chdex.style.height = '';
				if (orient == 'w')
					chdex.style.width = '';
				chdex.style.minWidth = '1px'; //Bug ZK-1509: add minium 1px width to pass isWatchable_
			}
		}
	},
	//bug#3042306
	resetSize_: function (orient) { ////@Overrid zk.Widget#resetSize_, called when beforeSize
		this.$supers(zul.box.Layout, 'resetSize_', arguments);
		var vert = this.isVertical_();
		for (var kid = this.firstChild; kid; kid = kid.nextSibling) {
			if (vert ? (kid._nvflex && kid.getVflex() != 'min')
					 : (kid._nhflex && kid.getHflex() != 'min')) {

				var chdex = kid.$n('chdex');
				if (chdex) {
					if (orient == 'h')
						chdex.style.height = '';
					if (orient == 'w')
						chdex.style.width = '';
				}
			}
		}
	},
	getChildMinSize_: function (attr, wgt) { //'w' for width or 'h' for height
		var el = wgt.$n(); //Bug ZK-1578: should get child size instead of chdex size
		//If child uses hflex="1" when parent has hflex="min"
		//   Find max sibling width and apply on the child
		if (attr == 'w' && wgt._hflex && this.isVertical_()) {
			for (var w = wgt.nextSibling, max = 0, width; w; w = w.nextSibling) {
				if (!w._hflex) {
					width = zjq.minWidth(w.$n());
					max = width > max ? width : max;
				}
			}
			return max;
		}
		if (attr == 'h') { // if display is not block the offsetHeight is wrong
			return zk(el.parentNode).contentHeight();
		} else {
			return zjq.minWidth(el); //See also bug ZK-483
		} 
	},
	//Bug ZK-1577: should consider spacing size of all chdex node
	getContentEdgeHeight_: function () {
		var h = 0;
		for (var kid = this.firstChild; kid; kid = kid.nextSibling)
			h += zk(kid.$n('chdex')).paddingHeight();

		return h;
	},
	//Bug ZK-1577: should consider spacing size of all chdex node
	getContentEdgeWidth_: function () {
		var w = 0;
		for (var kid = this.firstChild; kid; kid = kid.nextSibling)
			w += zk(kid.$n('chdex')).paddingWidth();

		return w;
	},
	beforeChildrenFlex_: function(child) {
		// optimized for performance
		this._shallSize = false;
		child._flexFixed = true;
		
		var	vert = this.isVertical_(),
			vflexs = [],
			vflexsz = vert ? 0 : 1,
			hflexs = [],
			hflexsz = !vert ? 0 : 1,
			p = this.$n(),
			psz = child.getParentSize_(p),
			zkp = zk(p),
			hgh = psz.height,
			wdh = psz.width,
			xc = this.firstChild,
			scrWdh;

		if (!zk.mounting) { // ignore for the loading time
			this._resetBoxSize(vert);
		}

		// Bug 3185686, B50-ZK-452
		if(zkp.hasVScroll()) //with vertical scrollbar
			wdh -= (scrWdh = jq.scrollbarWidth());
			
		// B50-3312936.zul
		if(zkp.hasHScroll()) //with horizontal scrollbar
			hgh -= scrWdh || jq.scrollbarWidth();
		
		for (; xc; xc = xc.nextSibling) {
			if (xc.isVisible()) {
				var cwgt = xc,
					c = cwgt.$n(),
					zkc = zk(c),
					cp = c.parentNode,
					zkxc = zk(cp);
				//vertical size
				if (xc && xc._nvflex) {
					if (cwgt !== child)
						cwgt._flexFixed = true; //tell other vflex siblings I have done it.
					if (cwgt._vflex == 'min') {
						cwgt.fixMinFlex_(c, 'h');
						
						//Bug ZK-1577: should consider padding size
						var h = c.offsetHeight + zkc.marginHeight() + zkxc.padBorderHeight();
						cp.style.height = jq.px0(h);
						if (vert)
							hgh -= cp.offsetHeight + zkxc.marginHeight();
					} else {
						vflexs.push(cwgt);
						if (vert) {
							vflexsz += cwgt._nvflex;
							
							//bug#3157031: remove chdex's padding, border, margin
							hgh = hgh - zkxc.marginHeight();
						}
					}
				} else if (vert) {
					// ZK-2038: fix the height of some div/span is not integer issue in ie
					var isIssueComp = cwgt.$instanceof(zul.wgt.Label) || cwgt.$instanceof(zul.wgt.Span) ||
							cwgt.$instanceof(zul.wgt.Div) || cwgt.$instanceof(zul.wgt.A);
					hgh -= (isIssueComp && zk.ie > 8 ? 1 : 0) + cp.offsetHeight + zkxc.marginHeight();
				}

				//horizontal size
				if (cwgt && cwgt._nhflex) {
					if (cwgt !== child)
						cwgt._flexFixed = true; //tell other hflex siblings I have done it.
					if (cwgt._hflex == 'min') {
						cwgt.fixMinFlex_(c, 'w');
						
						//Bug ZK-1577: should consider padding size
						var w = c.offsetWidth + zkc.marginWidth() + zkxc.padBorderWidth();
						cp.style.width = jq.px0(zkxc.revisedWidth(w));
						if (!vert)
							wdh -= cp.offsetWidth + zkxc.marginWidth();
					} else {
						hflexs.push(cwgt);
						if (!vert) {
							hflexsz += cwgt._nhflex;
							
							//bug#3157031: remove chdex's padding, border, margin
							wdh = wdh - zkxc.marginWidth(); 
						}
					}
				} else if (!vert)
					wdh -= cp.offsetWidth + zkxc.marginWidth();
			}
		}

		//setup the height for the vflex child
		//avoid floating number calculation error(TODO: shall distribute error evenly)
		var lastsz = hgh > 0 ? hgh : 0;
		while (vflexs.length > 1) {
			var cwgt = vflexs.shift(),
				vsz = (vert ? (cwgt._nvflex * hgh / vflexsz) : hgh) || 0, //cast to integer
				offtop = cwgt.$n().offsetTop,
				isz = vsz - ((zk.ie < 11 && offtop > 0) ? (offtop * 2) : 0),
				chdex = cwgt.$n('chdex'),
				minus = zk(chdex).padBorderHeight();
			
			// we need to remove the chdex padding and border for border-box mode
			cwgt.setFlexSize_({height:isz - minus});
			cwgt._vflexsz = vsz - minus;

			// no need to subtract padding and border for border-box mode
			chdex.style.height = jq.px0(vsz);
			if (vert) lastsz -= vsz;
		}
		//last one with vflex
		if (vflexs.length) {
			var cwgt = vflexs.shift(),
				offtop = cwgt.$n().offsetTop,
				isz = lastsz - ((zk.ie < 11 && offtop > 0) ? (offtop * 2) : 0),
				chdex = cwgt.$n('chdex'),
				minus = zk(chdex).padBorderHeight();
			
			// we need to remove the chdex padding and border for border-box mode
			cwgt.setFlexSize_({height:isz - minus});
			cwgt._vflexsz = lastsz - minus;
			
			// no need to subtract padding and border for border-box mode
			chdex.style.height = jq.px0(lastsz);
		}
		//setup the width for the hflex child
		//avoid floating number calculation error(TODO: shall distribute error evenly)
		lastsz = wdh > 0 ? wdh : 0;
		while (hflexs.length > 1) {
			var cwgt = hflexs.shift(), //{n: node, f: hflex}
				hsz = (vert ? wdh : (cwgt._nhflex * wdh / hflexsz)) || 0, //cast to integer
				chdex = cwgt.$n('chdex'),
				minus = zk(chdex).padBorderWidth();
			
			// we need to remove the chdex padding and border for border-box mode
			cwgt.setFlexSize_({width:hsz - minus});
			cwgt._hflexsz = hsz - minus;

			// no need to subtract padding and border for border-box mode
			chdex.style.width = jq.px0(hsz);

			if (!vert) lastsz -= hsz;
		}
		//last one with hflex
		if (hflexs.length) {
			var cwgt = hflexs.shift(),
				chdex = cwgt.$n('chdex'),
				minus = zk(chdex).padBorderWidth();
			
			// we need to remove the chdex padding and border for border-box mode
			cwgt.setFlexSize_({width:lastsz - minus});
			cwgt._hflexsz = lastsz - minus;

			// no need to subtract padding and border for border-box mode
			chdex.style.width = jq.px0(lastsz);
		}

		//notify all of children with xflex is done.
		child.parent.afterChildrenFlex_(child);
		child._flexFixed = false;

		return false; //to skip original _fixFlex
	},
	afterChildrenMinFlex_: function (opts) {
		var n = this.$n();
		if (opts == 'h') {
			if (this.isVertical_()) {
    			var total = 0;
    			for (var w = n.firstChild; w; w = w.nextSibling) {
    				var fchd = w.firstChild;
    				if (fchd.style.height) {
    					var hgh = fchd.offsetHeight
								+ zk(w).padBorderHeight()
								+ zk(fchd).marginHeight();
    					w.style.height = jq.px0(hgh);
    					total += hgh;
    				} else
    					total += w.offsetHeight;
    			}
    			n.style.height = jq.px0(total);
			} else {
    			var max = 0;
    			for (var w = n.firstChild; w; w = w.nextSibling) {
    				// use w.offsetHeight instead of w.firstChild.offsetHeight
    				// for avoiding span's special gap when using HTML5 doctype
    				var h = w.offsetHeight;
    				if (h > max)
    					max = h;
    			}
    			n.style.height = jq.px0(max);
			}
		} else {
			if (!this.isVertical_()) {
    			var total = 0;
    			for (var w = n.firstChild; w; w = w.nextSibling) {
    				var fchd = w.firstChild;
    				if (fchd.style.width) {
    					var wdh = fchd.offsetWidth
								+ zk(w).padBorderWidth()
								+ zk(fchd).marginWidth();
    					w.style.width = jq.px0(wdh);
    					total += wdh;
    				} else
    					total += w.offsetWidth;
    			}

				// IE9+ bug ZK-483
				if ((zk.ie > 8) && this._hflexsz)
					total = Math.max(this._hflexsz, total);

    			n.style.width = jq.px0(total);
			} else {
    			var max = 0;
    			for (var w = n.firstChild; w; w = w.nextSibling) {
    				var wd = w.firstChild.offsetWidth;
    				if (wd > max)
    					max = wd;
    			}
    			
    			// IE9+ bug ZK-483
				if ((zk.ie > 8)&& this._hflexsz)
					max = Math.max(this._hflexsz, max);
				
    			n.style.width = jq.px0(max);
			}
		}
	}
});
