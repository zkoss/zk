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
zul.layout.Layout = zk.$extends(zk.Widget, {
	$define: {
		/** Sets the spacing between adjacent children.
	 	 * @param String spacing the spacing (such as "0", "5px", "3pt" or "1em"),
	 	 * or null to use the default spacing
	 	 * @see #getSpacing
	 	 */
		/** Returns the spacing between adjacent children, or null if the default
	 	 * spacing is used.
	 	 *
	 	 * <p>Default: null (means to use the default spacing).
	 	 * @return String
	 	 */
		spacing: function () {
			var n = this.$n(),
				vert = this.isVertical_();
			if (n) {
				jq(n).children('div').css('padding-' + (vert ? 'bottom' : 'right'), this._spacing ? this._spacing : '');
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
			jq(this.$n()).append(this.encloseChildHTML_(child));
		}
		child.bind(desktop);
	},
	bind_: function () {
		this.$supers(zul.layout.Layout, 'bind_', arguments);
		zWatch.listen({onResponse: this});
	},
	unbind_: function () {
		zWatch.unlisten({onResponse: this});
		this.$supers(zul.layout.Layout, 'unbind_', arguments);
	},
	onResponse: function () {
		if (this._syncSize) {
			zWatch.fire('onSize', this);
			this._syncSize = false;
		}
	},
	onChildAdded_: function () {
		this.$supers('onChildRemoved_', arguments);
		this._syncSize = true;
	},
	onChildRemoved_: function () {
		this.$supers('onChildRemoved_', arguments);
		this._syncSize = true;
	},
	removeChildHTML_: function (child) {
		this.$supers('removeChildHTML_', arguments);
		jq(child.uuid + '-chdex', zk).remove();
	},
	/** Enclose child with HTML tag such as DIV, 
	 * and return a HTML code or add HTML fragments in out array.
	 * @param zk.Widget child the child which will be enclosed
	 * @param Array out an array of HTML fragments.
	 * @return String
	 */
	encloseChildHTML_: function (child, out) {
		var oo = [],
			vert = this.isVertical_();
		
		oo.push('<div id="', child.uuid, '-chdex" class="', this.getZclass(), '-inner"',
				this._spacing ? ' style="padding-' + (vert ? 'bottom:' : 'right:') + this._spacing + '">' : '>');
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
	_resetBoxSize: function () {
		var vert = this.isVertical_();
		for (var kid = this.firstChild; kid; kid = kid.nextSibling) {
			if (vert ? (kid._nvflex && kid.getVflex() != 'min')
					 : (kid._nhflex && kid.getHflex() != 'min')) {
				
				kid.setFlexSize_({height:'', width:''});
				var chdex = kid.$n('chdex');
				if (chdex) {
					chdex.style.height = '';
					chdex.style.width = '';
				}
			}
		}
		//bug 3010663: boxes do not resize when browser window is resized
		var p = this.$n(),
			zkp = zk(p),
			offhgh = p.offsetHeight,
			offwdh = p.offsetWidth,
			curhgh = this._vflexsz !== undefined ? this._vflexsz - zkp.sumStyles("tb", jq.margins) : offhgh,
			curwdh = this._hflexsz !== undefined ? this._hflexsz - zkp.sumStyles("lr", jq.margins) : offwdh,
			hgh = zkp.revisedHeight(curhgh < offhgh ? curhgh : offhgh),
			wdh = zkp.revisedWidth(curwdh < offwdh ? curwdh : offwdh);
		return zkp ? {height: hgh, width: wdh} : {};
	},
	//bug#3042306
	resetSize_: function (orient) { ////@Overrid zk.Widget#resetSize_, called when beforeSize
		this.$supers(zul.layout.Layout, 'resetSize_', arguments);
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
	beforeChildrenFlex_: function(child) {
		if (child._flexFixed || (!child._nvflex && !child._nhflex)) { //other vflex/hflex sibliing has done it!
			delete child._flexFixed;
			return false;
		}
		
		child._flexFixed = true;
		
		var	vert = this.isVertical_(),
			vflexs = [],
			vflexsz = vert ? 0 : 1,
			hflexs = [],
			hflexsz = !vert ? 0 : 1,
			p = child.$n('chdex').parentNode,
			psz = this._resetBoxSize(),
			hgh = psz.height,
			wdh = psz.width,
			xc = p.firstChild;
		
		for (; xc; xc = xc.nextSibling) {
			var c = xc.id && xc.id.endsWith('-chdex') ? xc.firstChild : xc,
				zkc = zk(c);
			if (zkc.isVisible()) {
				var j = c.id ? c.id.indexOf('-') : 1,
					cwgt = j < 0 ? zk.Widget.$(c.id) : null,
					offhgh = zkc.offsetHeight(),
					offwdh = zkc.offsetWidth(),
					cwdh = offwdh + zkc.sumStyles("lr", jq.margins),
					chgh = offhgh + zkc.sumStyles("tb", jq.margins);
				
				//vertical size
				if (cwgt && cwgt._nvflex) {
					if (cwgt !== child)
						cwgt._flexFixed = true; //tell other vflex siblings I have done it.
					if (cwgt._vflex == 'min') {
						cwgt.fixMinFlex_(c, 'h');
						xc.style.height = c.style.height;
						if (vert)
							hgh -= xc.offsetHeight;
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
						xc.style.width = c.style.width;
						if (!vert)
							wdh -= xc.offsetWidth;
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
		for (var j = vflexs.length; --j > 0;) {
			var cwgt = vflexs.shift(), 
				vsz = (cwgt._nvflex * hgh / vflexsz) | 0, //cast to integer
				offtop = cwgt.$n().offsetTop,
				isz = vsz - ((zk.ie && offtop > 0) ? (offtop * 2) : 0); 
			cwgt.setFlexSize_({height:isz});
			cwgt._vflexsz = vsz;
			
			var chdex = cwgt.$n('chdex');
			chdex.style.height = jq.px0(zk(chdex).revisedHeight(vsz, true));
			if (vert) lastsz -= vsz;
		}
		//last one with vflex
		if (vflexs.length) {
			var cwgt = vflexs.shift(),
				offtop = cwgt.$n().offsetTop,
				isz = lastsz - ((zk.ie && offtop > 0) ? (offtop * 2) : 0);
			cwgt.setFlexSize_({height:isz});
			cwgt._vflexsz = lastsz;
			var chdex = cwgt.$n('chdex');
			chdex.style.height = jq.px0(zk(chdex).revisedHeight(lastsz, true));
		}
		
		//setup the width for the hflex child
		//avoid floating number calculation error(TODO: shall distribute error evenly)
		lastsz = wdh > 0 ? wdh : 0;
		for (var j = hflexs.length; --j > 0;) {
			var cwgt = hflexs.shift(), //{n: node, f: hflex} 
				hsz = (cwgt._nhflex * wdh / hflexsz) | 0; //cast to integer
			cwgt.setFlexSize_({width:hsz});
			cwgt._hflexsz = hsz;
		
			var chdex = cwgt.$n('chdex');
			chdex.style.width = jq.px0(zk(chdex).revisedWidth(hsz, true));
			
			if (!vert) lastsz -= hsz;
		}
		//last one with hflex
		if (hflexs.length) {
			var cwgt = hflexs.shift();
			cwgt.setFlexSize_({width:lastsz});
			cwgt._hflexsz = lastsz;
			
			var chdex = cwgt.$n('chdex');
			chdex.style.width = jq.px0(zk(chdex).revisedWidth(lastsz, true));
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
    				if (w.firstChild.style.height) {
    					w.style.height = jq.px0(zk(w).revisedHeight(w.firstChild.offsetHeight));
    				}
    				total += w.offsetHeight;
    			}
    			n.style.height = jq.px0(total);
			} else {
    			var max = 0;
    			for (var w = n.firstChild; w; w = w.nextSibling) {
    				var h = w.firstChild.offsetHeight;
    				if (h > max)
    					max = h;
    			}
    			n.style.height = jq.px0(max);
			}
		} else {
			if (!this.isVertical_()) {
    			var total = 0;
    			for (var w = n.firstChild; w; w = w.nextSibling) {
    				if (w.firstChild.style.width) {
    					w.style.width = jq.px0(zk(w).revisedWidth(w.firstChild.offsetWidth));
    				}
    				total += w.offsetWidth;
    			}
    			n.style.width = jq.px0(total);
			} else {
    			var max = 0;
    			for (var w = n.firstChild; w; w = w.nextSibling) {
    				var wd = w.firstChild.offsetWidth;
    				if (wd > max)
    					max = wd;
    			}
    			n.style.height = jq.px0(max);				
			}
		}
	}
});