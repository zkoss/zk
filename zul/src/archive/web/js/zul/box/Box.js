/* Box.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 12:10:53     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The box widgets, such as hbox and vbox.
 */
//zk.$package('zul.box');

(function () {

	// Returns if the spacing is 0.
	function _spacing0(spacing) {
		return spacing && spacing.startsWith('0')
			&& (spacing.length == 1 || zUtl.isChar(spacing.charAt(1),{digit:1}));
	}
	function _spacingHTML(box, child) {
		var oo = [],
			spacing = box._spacing,
			spacing0 = _spacing0(spacing),
			vert = box.isVertical(),
			spstyle = spacing && spacing != 'auto' ? (vert?'height:':'width:') + spacing: '';

		oo.push('<t', vert?'r':'d', ' id="', child.uuid,
			'-chdex2" class="', box.$s('separator'), '"');

		var s = spstyle;
		if (spacing0 || !child.isVisible()) s = 'display:none;' + s;
		if (s) oo.push(' style="', s, '"');

		oo.push('>', vert?'<td>':'', zUtl.img0, vert?'</td></tr>':'</td>');
		return oo.join('');
	}

	//notice it is invoked as a member of Box (so no need to pass box as argument
	function _fixTd() {
		//when align is stretched must release the children, then must "shrink td" manually
		var vert = this.isVertical();
		if (this._isStretchAlign() || (vert && this._nhflex) || (!vert && this._nvflex)) {
			for(var child = this.firstChild; child; child = child.nextSibling) {
				if (child.isVisible()) {
					var c = child.$n();
					if (vert) {
						if (child._nhflex && child._nhflex > 0) // B50-ZK-159: skip when min flex
							child.setFlexSize_({width:'auto'});
						else if (c && this._isStretchAlign()) {//release width of children might cause wider box
									 //bug 2951825, widget not necessary with HTML dom element(<script>)
									 //add StretchAlign checking, see revision: 13172
							var oldwidth= c.style.width;
							if (oldwidth) {
								var oldoffwidth= c.offsetWidth;
								c.style.width= ''; //release the width of children so td can shrink
								if (c.offsetWidth > oldoffwidth)
									c.style.width= oldwidth;
							}
						}
						if (!child.$instanceof(zul.wgt.Cell) && this._nhflex) {
							var chdex = child.$n('chdex');
							chdex.style.width = '';
						}
					} else {
						if (child._nvflex && child._nvflex > 0) // B50-ZK-159: skip when min flex
							child.setFlexSize_({height:'auto'});
						else if (c && this._isStretchAlign()) {//release height of children might cause higher box
									 //bug 2951825, widget not necessary with HTML dom element(<script>)
									 //add StretchAlign checking, see revision: 13172
							var oldheight= c.style.height;
							if (oldheight) {
								var oldoffheight = c.offsetHeight;
								c.style.height= ''; //release the height of children so td can shrink
								if (c.offsetHeight > oldoffheight)
									c.style.height= oldheight;
							}
						}
						if (!child.$instanceof(zul.wgt.Cell) && this._nvflex) {
							var chdex = child.$n('chdex');
							chdex.style.height = '';
						}
					}
				}
			}
		}
		//Safari/chrome will not extend the height of td to as tr (B30-2088496.zul)
		//but cannot always give pixels if size determined by contents (big gap in B30-1769047.zul)
		var nh;
		if (zk.webkit && !vert && (nh = this.$n().style.height)) {
			var td = this.$n('frame');
			td.style.height = '';
			td.style.height = nh.indexOf('%') > 0 ? 
				jq.px0(td.offsetHeight) : nh; // B50-ZK-559
		}
	}

var Box =
/**
 * A box.
 * <p>Default {@link #getZclass}: z-vbox.
 */
zul.box.Box = zk.$extends(zul.Widget, {
	_mold: 'vertical',
	_align: 'start',
	_pack: 'start',
	_sizedByContent: true,

	$define: {
		/** Sets the alignment of cells of this box in the 'opposite' direction
	 	 * (<i>start</i>, center, end, stretch).
	 	 *
		 * @param String align the alignment in the 'opposite' direction.
		 * Allowed values: start, center, end, stretch.
	 	 * If empty or null, the browser's default is used
	 	 * (IE center and FF left, if vertical).
		 */
		/** Returns the alignment of cells of a box in the 'opposite' direction
	 	 * (<i>null</i>, start, center, end).
	 	 *
	 	 * <p>The align attribute specifies how child elements of the box are aligned,
	  	 * when the size of the box is larger than the total size of the children. For
	 	 * boxes that have horizontal orientation, it specifies how its children will
	 	 * be aligned vertically. For boxes that have vertical orientation, it is used
	 	 * to specify how its children are aligned horizontally. The pack attribute
	 	 * ({@link #getPack}) is
	 	 * related to the alignment but is used to specify the position in the
		 * opposite direction.
		 *
		 * <dl>
		 * <dt>start</dt>
		 * <dd>Child elements are aligned starting from the left or top edge of
		 * the box. If the box is larger than the total size of the children, the
		 * extra space is placed on the right or bottom side.</dd>
	 	 * <dt>center</dt>
	 	 * <dd>Extra space is split equally along each side of the child
	 	 * elements, resulting in the children being placed in the center of the box.</dd>
		 * <dt>end</dt>
		 * <dd>Child elements are placed on the right or bottom edge of the box. If
	 	 * the box is larger than the total size of the children, the extra space is
		 * placed on the left or top side.</dd>
	 	 * <dt>stretch</dt>
		 * <dd>Child elements are stretched to fill the box.</dd>
		 * </dl>
		 *
		 * <p>Default: start</p>
		 * @return String
		 */
		align:  _zkf = function () {
			this.rerender(); //TODO: a better algoithm
		},
		/** Sets the alignment of cells of this box
	 	 * (start, center, end) plus an <i>stretch</i> option.
	 	 *
	 	 * @param String pack the alignment. Allowed values: (start, center, end) plus an 
	 	 * <i>stretch</i> option. If empty or null, it defaults to "stretch,start".
	 	 * @see #getPack()
	 	 */
		/** Returns the pack alignment of cells of this box
	 	 * (start, center, end) plus an indication <i>stretch</i> option.
	 	 *
	 	 * <p>The pack attribute specifies where child elements of the box are placed
	 	 * when the box is larger that the size of the children. For boxes with
	 	 * horizontal orientation, it is used to indicate the position of children
	 	 * horizontally. For boxes with vertical orientation, it is used to indicate
	 	 * the position of children vertically. The align attribute 
	 	 * ({@link #getAlign})is used to specify
	 	 * the position in the opposite direction.
	 	 * 
	 	 * <dl>
	 	 * <dt>start</dt>
	 	 * <dd>Child elements are aligned starting from the left or top edge of
	 	 * the box. If the box is larger than the total size of the children, the
	 	 * Extra space is placed on the right or bottom side.</dd>
	 	 * <dt>center</dt>
	 	 * <dd>Extra space is split equally along each side of the child
	 	 * elements, resulting in the children being placed in the center of the box.</dd>
	 	 * <dt>end</dt>
	 	 * <dd>Child elements are placed on the right or bottom edge of the box. If
	 	 * the box is larger than the total size of the children, the extra space is
	 	 * placed on the left or top side.</dd>
	 	 * <dt>stretch</dt>
	 	 * <dd>This is an extra option in addition to the (start, center, end) options.
	 	 * When add this extra option in the pack attribute, the Extra space is placed
	 	 * proportionally and evenly along each child elements. If you specify 
	 	 * "stretch,start", then the Extra proportionally and evenly allocated space 
	 	 * for each child is placed on the right or bottom side of the child. 
	 	 * If you specify "stretch,center", then the Extra proportionally and evenly 
	 	 * allocated space for each child is split equally along each side of the
	 	 * child. If you specify "stretch,end", then the Extra proportionally and 
	 	 * evenly allocated space for each child is placed on the left or top side of 
	 	 * the child. Note that if there are {@link Splitter} child inside this Box, 
	 	 * then this Box behaves as if the pack attribute has been set the "stretch"
	 	 * option; no matter you really specify "stretch" in pack attribute or not. 
	 	 * If given null or simply "stretch" to this pack attribute then it is the 
	 	 * same as "stretch,start"</dd> 
	 	 * </dl>
	 	 *
	 	 * <p>Default: start.
	 	 * @return String
	 	 */
		pack: _zkf,
		/** Sets the spacing between adjacent children.
	 	 * @param String spacing the spacing (such as "0", "5px", "3pt" or "1em"),
	 	 * or null to use the default spacing
	 	 * @see #getSpacing
	 	 */
		/** Returns the spacing between adjacent children, or null if the default
	 	 * spacing is used.
	 	 *
	 	 * <p>The default spacing depends on the definition of the style class
	 	 * called "xxx-sp", where xxx is
	 	 *
	 	 * <ol>
	 	 *  <li>{@link #getSclass} if it is not null.</li>
	 	 *  <li>hbox if {@link #getSclass} is null and it is a horizontal box.</li>
	 	 *  <li>vbox if {@link #getSclass} is null and it is a vertical box.</li>
	 	 * </ol>
	 	 *
	 	 * <p>Default: null (means to use the default spacing).
	 	 * @return String
	 	 */
		spacing: _zkf,
		/**
		 * Sets whether sizing the cell's size by its content.
		 * <p>Default: true. It means the cell's size is depended on its content.
		 * 
		 * <p> With {@link Splitter}, you can specify the sizedByContent to be false
		 * for resizing smoothly
		 * @param boolean byContent 
		 * @since 5.0.4
		 */
		/**
		 * Returns whether sizing the cell's size by its content.
		 * <p>Default: true.
		 * @since 5.0.4
		 * @return boolean
		 */
		sizedByContent: _zkf,
		widths: _zkf = function (val) {
		    this._sizes = val;
		    this.rerender();
		}
	},
	setHeights: function (val) {
		this.setWidths(val);
	},
	getHeights: function () {
		return this.getWidths();
	},
	/** Returns whether it is a vertical box. 
	 * @return boolean
	 */
	isVertical: function () {
		return 'vertical' == this._mold;
	},
	/** Returns the orient. 
	 * @return String
	 */
	getOrient: function () {
		return this._mold;
	},

	//super//
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: this.isVertical() ? 'z-vbox' : 'z-hbox';
	},

	onChildVisible_: function (child) {
		this.$supers('onChildVisible_', arguments);
		if (this.desktop) this._fixChildDomVisible(child, child._visible);
	},
	replaceChildHTML_: function (child) {
		this.$supers('replaceChildHTML_', arguments);
		this._fixChildDomVisible(child, child._visible);
		if (child.$instanceof(zul.box.Splitter)) {
			var n = this._chdextr(child);
			if (n) {
				n.style.height = '';
				n.style.width = '';
			}
			zUtl.fireSized(this, -1); //no beforeSize
		}
	},
	_fixChildDomVisible: function (child, visible) {
		var n = this._chdextr(child);
		if (n) n.style.display = visible ? '': 'none';
		n = child.$n('chdex2');
		if (n) n.style.display = visible && !_spacing0(this._spacing) ? '': 'none';

		if (this.lastChild == child) {
			n = child.previousSibling;
			if (n) {
				n = n.$n('chdex2');
				if (n) n.style.display = visible ? '': 'none';
			}
		}
	},
	_chdextr: function (child) {
		return child.$n('chdex') || child.$n();
	},
	insertChildHTML_: function (child, before, desktop) {
		if (before) {
			jq(this._chdextr(before)).before(this.encloseChildHTML_(child));
		} else {
			var n = this.$n('real'), tbs = n.tBodies;
			if (!tbs || !tbs.length)
				n.appendChild(document.createElement('tbody'));
			jq(this.isVertical() ? tbs[0]: tbs[0].rows[0]).append(
				this.encloseChildHTML_(child, true));
		}
		child.bind(desktop);
	},
	removeChildHTML_: function (child) {
		this.$supers('removeChildHTML_', arguments);
		jq(child.uuid + '-chdex', zk).remove();
		jq(child.uuid + '-chdex2', zk).remove();
		var sib;
		if (this.lastChild == child && (sib = child.previousSibling)) //child is last
			jq(sib.uuid + '-chdex2', zk).remove();
	},
	/** Enclose child with HTML tag such as TR or TD, 
	 * and return a HTML code or add HTML fragments in out array.
	 * @param zk.Widget child the child which will be enclosed
	 * @param boolean prefixSpace if true the previousSibling of the child 
	 * will put in front of child.
	 * @param Array out an array of HTML fragments.
	 * @return String
	 */
	encloseChildHTML_: function (child, prefixSpace, out) {
		var oo = [],
			isCell = child.$instanceof(zul.wgt.Cell);
		if (this.isVertical()) {
			oo.push('<tr id="', child.uuid, '-chdex"',
				this._childOuterAttrs(child), '>');
				
			if (!isCell) {
				oo.push('<td', this._childInnerAttrs(child));
				//follow xul vbox spec.
				var v = this.getAlign();
				if (v && v != 'stretch') oo.push(' align="', zul.box.Box._toHalign(v), '"');
				oo.push('>');
			}
				
			child.redraw(oo);
			
			if (!isCell) oo.push('</td>');
			
			oo.push('</tr>');

		} else {
			if (!isCell) {
				oo.push('<td id="', child.uuid, '-chdex"',
				this._childOuterAttrs(child),
				this._childInnerAttrs(child),
				'>');
			}
			child.redraw(oo);
			if (!isCell)
				oo.push('</td>');
		}
		var next = child.nextSibling; //Bug ZK-1526: popup should not consider spacing
		if (next && !next.$instanceof(zul.wgt.Popup))
			oo.push(_spacingHTML(this, child));
		else if (prefixSpace) {
			var pre = child.previousSibling;
			if (pre) oo.unshift(_spacingHTML(this, pre));
		}
		
		if (!out) return oo.join('');

		for (var j = 0, len = oo.length; j < len; ++j)
			out.push(oo[j]);
	},
	_resetBoxSize: function (vert) {
		var	vert = this.isVertical(),
			k = -1,
			szes = this._sizes;
		
		if (!zk.mounting) {// ignore for the loading time
			if (vert) {
				for (var kid = this.firstChild; kid; kid = kid.nextSibling) {
					if (szes && !kid.$instanceof(zul.box.Splitter) && !kid.$instanceof(zul.wgt.Cell))
						++k;
					if (kid._nvflex && kid.getVflex() != 'min') {
						kid.setFlexSize_({height:'', width:''});
						var chdex = kid.$n('chdex');
						if (chdex) {
							var n;
							if ((n = kid.$n()) && (n.scrollTop || n.scrollLeft)) // keep the scroll status
								;// do nothing Bug ZK-1885: scrollable div (with vflex) and tooltip
							else {
								chdex.style.height = szes && k < szes.length ? szes[k] : '';
								chdex.style.width = '';
							}
						}
					}
				}
			} else {
				for (var kid = this.firstChild; kid; kid = kid.nextSibling) {
					if (szes && !kid.$instanceof(zul.box.Splitter) && !kid.$instanceof(zul.wgt.Cell))
						++k;
					if (kid._nhflex && kid.getHflex() != 'min') {
						kid.setFlexSize_({height:'', width:''});
						var chdex = kid.$n('chdex');
						if (chdex) {
							var n;
							if ((n = kid.$n()) && (n.scrollTop || n.scrollLeft)) // keep the scroll status
								;// do nothing Bug ZK-1885: scrollable div (with vflex) and tooltip
							else {
								chdex.style.width = szes && k < szes.length ? szes[k] : '';
								chdex.style.height = '';
							}
						}
					}
				}
			}
		}
	},
	//Bug ZK-1569: add minium 1px width on <td> to pass isWatchable_
	afterResetChildSize_: function () {
		for (var kid = this.firstChild, vert = this.isVertical(); kid; kid = kid.nextSibling) {				
			var chdex = vert ? kid.$n('chdex').firstChild : kid.$n('chdex');
			if (chdex)
				chdex.style.minWidth = '1px';
		}
	},
	//bug#3042306
	resetSize_: function (orient) { //@Overrid zk.Widget#resetSize_, called when beforeSize
		this.$supers(zul.Widget, 'resetSize_', arguments);
		var	vert = this.isVertical(),
		k = -1,
		szes = this._sizes;
		if (vert) {
			for (var kid = this.firstChild; kid; kid = kid.nextSibling) {
				if (szes && !kid.$instanceof(zul.box.Splitter) && !kid.$instanceof(zul.wgt.Cell))
					++k;
				if (kid._nvflex && kid.getVflex() != 'min') {
					var chdex = kid.$n('chdex');
					if (chdex) {
						if (orient == 'h')
							chdex.style.height = szes && k < szes.length ? szes[k] : '';
						if (orient == 'w')
							chdex.style.width = '';
					}
				}
			}
		} else {
			for (var kid = this.firstChild; kid; kid = kid.nextSibling) {
				if (szes && !kid.$instanceof(zul.box.Splitter) && !kid.$instanceof(zul.wgt.Cell))
					++k;
				if (kid._nhflex && kid.getHflex() != 'min') {
					var chdex = kid.$n('chdex');
					if (chdex) {
						if (orient == 'w')
							chdex.style.width = szes && k < szes.length ? szes[k] : '';
						if (orient == 'h')
							chdex.style.height = '';
					}
				}
			}
		}
	},
	_getContentSize: function () {
		//bug 3010663: boxes do not resize when browser window is resized
		var p = this.$n(),
			zkp = zk(p),
			hgh = this._vflexsz !== undefined ? 
					this._vflexsz - zkp.padBorderHeight() - zkp.marginHeight()
					// B50-ZK-286: subtract scroll bar width
					: zkp.contentHeight(true),
			wdh = this._hflexsz !== undefined ?
					this._hflexsz - zkp.padBorderWidth() - zkp.marginWidth()
					// B50-ZK-286: subtract scroll bar width
					: zkp.contentWidth(true);
		return zkp ? {height: hgh, width: wdh} : {height: 0, width: 0};
	},
	beforeChildrenFlex_: function(child) {
		child._flexFixed = true;
		
		var	vert = this.isVertical(),
			vflexs = [],
			vflexsz = vert ? 0 : 1,
			hflexs = [],
			hflexsz = !vert ? 0 : 1,
			chdex = child.$n('chdex'), 
			p = chdex ? chdex.parentNode : child.$n().parentNode,
			zkp = zk(this.$n()),
			psz = this._getContentSize(),
			hgh = psz.height,
			wdh = psz.width,
			xc = p.firstChild,
			k = -1,
			szes = this._sizes,
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
			var c = xc.id && xc.id.endsWith('-chdex') ? vert ? 
					xc.firstChild.id ? xc.firstChild: xc.firstChild.firstChild : xc.firstChild : xc,
				zkc = zk(c),
				fixedSize = false;
			if (zkc.isVisible()) {
				var j = c.id ? c.id.indexOf('-') : 1,
						cwgt = j < 0 ? zk.Widget.$(c.id) : null;

				if (szes && cwgt && !cwgt.$instanceof(zul.box.Splitter) && !cwgt.$instanceof(zul.wgt.Cell)) {
					++k;
					if (k < szes.length && szes[k] && ((vert && !cwgt._nvflex) || (!vert && !cwgt._nhflex))) {
						c = xc;
						zkc = zk(c);
						fixedSize = szes[k].endsWith('px');
					}
				}
				var offhgh = fixedSize && vert ? zk.parseInt(szes[k]) : 
						zk.ie && xc.id && xc.id.endsWith('-chdex2') && xc.style.height && xc.style.height.endsWith('px') ? 
						zk.parseInt(xc.style.height) : zkc.offsetHeight(),
					offwdh = fixedSize && !vert ? zk.parseInt(szes[k]) : zkc.offsetWidth(),
					cwdh = offwdh + zkc.marginWidth(),
					chgh = offhgh + zkc.marginHeight();
				
				//vertical size
				if (cwgt && cwgt._nvflex) {
					if (cwgt !== child)
						cwgt._flexFixed = true; //tell other vflex siblings I have done it.
					if (cwgt._vflex == 'min') {
						cwgt.fixMinFlex_(c, 'h');
						if (vert) hgh -= chgh;
					} else {
						vflexs.push(cwgt);
						if (vert) vflexsz += cwgt._nvflex;
					}
				} else if (vert) hgh -= chgh;
				
				//horizontal size
				if (cwgt && cwgt._nhflex) {
					if (cwgt !== child)
						cwgt._flexFixed = true; //tell other hflex siblings I have done it.
					if (cwgt._hflex == 'min') {
						cwgt.fixMinFlex_(c, 'w');
						if (!vert) wdh -= cwdh;
					} else {
						hflexs.push(cwgt);
						if (!vert) hflexsz += cwgt._nhflex;
					}
				} else if (!vert) wdh -= cwdh;
			}
		}

		//setup the height for the vflex child
		//avoid floating number calculation error(TODO: shall distribute error evenly)
		var lastsz = hgh > 0 ? hgh : 0;
		for (var j = vflexs.length - 1; j > 0; --j) {
			var cwgt = vflexs.shift(), 
				vsz = (cwgt._nvflex * hgh / vflexsz) | 0, //cast to integer
				//B50-3014664.zul offtop = cwgt.$n().offsetTop,
				isz = vsz,// B50-3014664.zul vsz - ((zk.ie && offtop > 0) ? (offtop * 2) : 0);
				chdex = cwgt.$n('chdex'),
				$chdex = zk(chdex),
				 minus = $chdex.padBorderHeight();
			
			// we need to remove the chdex padding and border for border-box mode
			cwgt.setFlexSize_({height:isz - minus});
			cwgt._vflexsz = vsz - minus;
			
			if (!cwgt.$instanceof(zul.wgt.Cell)) {				
				// no need to subtract padding and border for border-box mode
				chdex.style.height = jq.px0(vsz - $chdex.marginHeight());
			}

			if (vert) lastsz -= vsz;
		}
		//last one with vflex
		if (vflexs.length) {
			var cwgt = vflexs.shift(),
				// B50-3014664.zul offtop = cwgt.$n().offsetTop,
				isz = lastsz,// B50-3014664.zul - ((zk.ie && offtop > 0) ? (offtop * 2) : 0);
				chdex = cwgt.$n('chdex'),
				$chdex = zk(chdex),
				minus = $chdex.padBorderHeight();
			
			// we need to remove the chdex padding and border for border-box mode
			cwgt.setFlexSize_({height:isz - minus});
			cwgt._vflexsz = lastsz - minus;
			
			if (!cwgt.$instanceof(zul.wgt.Cell)) {
				// no need to subtract padding and border for border-box mode
				chdex.style.height = jq.px0(lastsz - $chdex.marginHeight());
			}
		}
		
		//setup the width for the hflex child
		//avoid floating number calculation error(TODO: shall distribute error evenly)
		lastsz = wdh > 0 ? wdh : 0;
		for (var j = hflexs.length - 1; j > 0; --j) {
			var cwgt = hflexs.shift(), //{n: node, f: hflex} 
				hsz = (cwgt._nhflex * wdh / hflexsz) | 0, //cast to integer
				chdex = cwgt.$n('chdex'),
				$chdex = zk(chdex),
				minus = $chdex.padBorderWidth();
			
			// we need to remove the chdex padding and border for border-box mode
			cwgt.setFlexSize_({width:hsz - minus});
			cwgt._hflexsz = hsz - minus;
			
			if (!cwgt.$instanceof(zul.wgt.Cell)) {
				// no need to subtract padding and border for border-box mode
				chdex.style.width = jq.px0(hsz - $chdex.marginWidth());
			}
			if (!vert) lastsz -= hsz;
		}
		//last one with hflex
		if (hflexs.length) {
			var cwgt = hflexs.shift(),
				chdex = cwgt.$n('chdex'),
				$chdex = zk(chdex),
				minus = $chdex.padBorderWidth();

			// we need to remove the chdex padding and border for border-box mode
			cwgt.setFlexSize_({width:lastsz - minus});
			cwgt._hflexsz = lastsz - minus;
			
			if (!cwgt.$instanceof(zul.wgt.Cell)) {
				// no need to subtract padding and border for border-box mode
				chdex.style.width = jq.px0(lastsz - $chdex.marginWidth());
			}
		}
		
		//notify all of children with xflex is done.
		child.parent.afterChildrenFlex_(child);
		child._flexFixed = false;
		
		return false; //to skip original _fixFlex
	},
	_childOuterAttrs: function (child) {
		var html = '';
		if (child.$instanceof(zul.box.Splitter))
			html = ' class="' + child.$s('outer') + '"';
		else if (this.isVertical()) {
			if (this._isStretchPack()) {
				var v = this._pack2; 
				html = ' valign="' + (v ? zul.box.Box._toValign(v) : 'top') + '"';
			} else html = ' valign="top"';
		} else
			return ''; //if hoz and not splitter, display handled in _childInnerAttrs

		if (!child.isVisible()) html += ' style="display:none"';
		return html;
	},
	_childInnerAttrs: function (child) {
		var html = '',
			vert = this.isVertical(),
			$Splitter = zul.box.Splitter;
		if (child.$instanceof($Splitter))
			return '';
				//spliter's display handled in _childOuterAttrs

		if (this._isStretchPack()) {
			var v = vert ? this.getAlign() : this._pack2;
			if (v) html += ' align="' + zul.box.Box._toHalign(v) + '"';
		}
		
		var style = '', szes = this._sizes;
		if (szes) {
			for (var j = 0, len = szes.length, c = this.firstChild;
			c && j < len; c = c.nextSibling) {
				if (child == c) {
					style = (vert ? 'height:':'width:') + szes[j];
					break;
				}
				if (!c.$instanceof($Splitter))
					++j;
			}
		}

		if (!vert && !child.isVisible()) style += style ? ';display:none' : 'display:none';
		if (!vert) style += style ? ';height:100%' : 'height:100%';
		return style ? html + ' style="' + style + '"': html;
	},
	_isStretchPack: function() {
		//when pack has specifies 'stretch' or there are splitter kids which 
		//implies pack='stretch'
		return this._splitterKid || this._stretchPack;
	},
	_isStretchAlign: function() {
		return this._align == 'stretch';
	},
	//called by Splitter
	_bindWatch: function () {
		if (!this._watchBound) {
			this._watchBound = true;
			zWatch.listen({onSize: this, onHide: this});
		}
	},
	_unbindWatch: function() {
		if (this._watchBound) {
			zWatch.unlisten({onSize: this, onHide: this});
			delete this._watchBound;
		}
	},
	bind_: function() {
		this.$supers(Box, 'bind_', arguments);
		this._bindFixTd();
		if (this._isStretchAlign())
			this._bindAlign();
		if (this._splitterKid)
			this._bindWatch();
	},
	unbind_: function () {
		this._unbindWatch();
		this._unbindAlign();
		this._unbindFixTd();
		this.$supers(Box, 'unbind_', arguments);
	},
	_bindAlign: function() {
		if (!this._watchAlign) {
			this._watchAlign = true;
			zWatch.listen({onSize: [this, this._fixAlign], onHide: [this, this._fixAlign]});
		}
	},
	_unbindAlign: function() {
		if (this._watchAlign) {
			zWatch.unlisten({onSize: [this, this._fixAlign], onHide: [this, this._fixAlign]});
			delete this._watchAlign;
		}
	},
	_fixAlign: function () {
		if (this._isStretchAlign()) {
			var vert = this.isVertical(),
				td = this.$n('frame'),
				zktd = zk(td),
				tdsz = vert ? zktd.revisedWidth(td.offsetWidth) : zktd.revisedHeight(td.offsetHeight);
			
			for(var child = this.firstChild, c; child; child = child.nextSibling) {
				if (child.isVisible() && (c = child.$n())) {
					//20100120, Henri Chen: Strange! After set c.style.height/width, the margin is gone in safari/chrome
					if (vert)
						c.style.width = zk(c).revisedWidth(tdsz, !zk.webkit) + 'px';
					else 
						c.style.height = zk(c).revisedHeight(tdsz - ((zk.ie && c.offsetTop > 0) ? (c.offsetTop * 2) : 0), !zk.webkit) + 'px';
				}
			}
		}
	},
	_bindFixTd: function() {
		if (!this._watchTd) {
			this._watchTd = true;
			zWatch.listen({onSize: [this, _fixTd], onHide: [this, _fixTd]});
		}
	},
	_unbindFixTd: function() {
		if (this._watchTd) {
			zWatch.unlisten({onSize: [this, _fixTd], onHide: [this, _fixTd]});
			delete this._watchTd;
		}
	},
	_configPack: function() {
		var v = this._pack;
		if (v) {
	    	var v = v.split(',');
	    	if (v[0].trim() == 'stretch') {
	    		this._stretchPack = true;
	    		this._pack2 = v.length > 1 ? v[1].trim() : null;
	    	} else {
	    		this._stretchPack = v.length > 1 && v[1].trim() == 'stretch';
	    		this._pack2 = v[0].trim();
	    	}
    	} else {
    		delete this._pack2;
    		delete this._stretchPack;
    	}
	},
	//watch//
	onSize: _zkf = function () {
		if (!this._splitterKid) return; //only when there are splitter kids
		var vert = this.isVertical(), node = this.$n(), real = this.$n('real');
		real.style.height = real.style.width = '100%'; //there are splitter kids
		
		//Note: we have to assign width/height first
		//Otherwise, the first time dragging the splitter won't be moved
		//as expected (since style.width/height might be "")

		var nd = vert ? real.rows: real.rows[0].cells,
			total = vert ? zk(real).revisedHeight(real.offsetHeight):
							zk(real).revisedWidth(real.offsetWidth),
			sizes = this._sizes;

		for (var i = nd.length; i--;) {
			var d = nd[i];
			if (zk(d).isVisible())
				if (vert) {
					var diff = d.offsetHeight;
					if(d.id && !d.id.endsWith('-chdex2')) { //TR
						//Bug 1917905: we have to manipulate height of TD in Safari
						if (d.cells.length) {
							var c = d.cells[0];
							c.style.height = zk(c).revisedHeight(i ? diff: total) + 'px';
							d.style.height = ''; //just-in-case
						} else {
							d.style.height = zk(d).revisedHeight(i ? diff: total) + 'px';
						}
					}
					total -= diff;
				} else {
					var diff = d.offsetWidth;
					//!sizes  B50-ZK-887: hbox's widths properties specified in Chrome is not precise
					//if user set the widths , we freeze the with directly 
					if(!sizes && d.id && !d.id.endsWith('-chdex2')) //TD
						d.style.width = zk(d).revisedWidth(i ? diff: total) + 'px';
					total -= diff;
				}
		}
	},
	onHide: _zkf
},{ //static
	_toValign: function (v) {
		return v ? 'start' == v ? 'top': 'center' == v ? 'middle':
			'end' == v ? 'bottom': v: null;
	},
	_toHalign: function (v) {
		return v ? 'start' == v ? 'left': 'end' == v ? 'right': v: null;
	}
});

})();
