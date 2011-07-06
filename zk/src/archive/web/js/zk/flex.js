/* flex.js

	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 14:14:21 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _getTextWidth(zkc, zkp, zkpOffset) {
		var $zkc = zkc.jq,
			$prev = $zkc.prev(),
			start = 0;
		if ($prev.length) {
			$zkc[0].style.marginLeft = '0px';
			$zkc[0].style.marginRight = '0px';
			$prev[0].style.marginLeft = '0px';
			$prev[0].style.marginRight = '0px';
			
			start = $prev.zk.cmOffset()[0] + $prev.zk.offsetWidth();
		} else {
			start = zkpOffset[0] + zkp.sumStyles("l", jq.paddings) + zkp.sumStyles("l", jq.borders);
		}
		start = zkc.cmOffset()[0] - start;
		
		$zkc[0].style.marginLeft = '';
		$zkc[0].style.marginRight = '';
		if ($prev.length) {
			$prev[0].style.marginLeft = '';
			$prev[0].style.marginRight = '';
		}
		return !zk.ie ? Math.max(0, start) : start; // ie may have a wrong gap
		
	}
	
	function _getTextHeight(zkc, zkp, zkpOffset) {
		var $zkc = zkc.jq,
			$prev = $zkc.prev(),
			start = 0;
		if ($prev.length) {
			$zkc[0].style.marginTop = '0px';
			$zkc[0].style.marginBottom = '0px';
			$prev[0].style.marginTop = '0px';
			$prev[0].style.marginBottom = '0px';
			
			start = $prev.zk.cmOffset()[1] + $prev.zk.offsetHeight();
		} else {
			start = zkpOffset[1] + zkp.sumStyles("t", jq.paddings) + zkp.sumStyles("t", jq.borders);
		}
		start = zkc.cmOffset()[1] - start;
		
		$zkc[0].style.marginTop = '';
		$zkc[0].style.marginBottom = '';
		if ($prev.length) {
			$prev[0].style.marginTop = '';
			$prev[0].style.marginBottom = '';
		}
		return !zk.ie ? Math.max(0, start) : start; // ie may have a wrong gap
	}
	
	function _getContentEdgeHeight(cwgt) {
		var p = cwgt.$n(),
			c = cwgt.firstChild ? cwgt.firstChild.$n() : p.firstChild,
			zkp = zk(p),
			h = zkp.padBorderHeight();
		
		if (c) {
			c = c.parentNode;
			while (c && p != c) {
				var zkc = zk(c);
				h += zkc.padBorderHeight() + zkc.sumStyles("tb", jq.margins);
				c = c.parentNode;
			}
			return h;
		}
		return 0;
	}
	function _getContentEdgeWidth(cwgt) {
		var p = cwgt.$n(),
			c = cwgt.firstChild ? cwgt.firstChild.$n() : p.firstChild,
			zkp = zk(p),
			w = zkp.padBorderWidth();
		
		if (c) {
			c = c.parentNode;
			while (c && p != c) {
				var zkc = zk(c);
				w += zkc.padBorderWidth() + zkc.sumStyles("lr", jq.margins);
				c = c.parentNode;
			}
			return w;
		}
		return 0;
	}
	
	function _isSameBaseline(ref, cur, vertical) {
		if (vertical) {
			var hgh = ref._hgh || (ref._hgh = ref.top + ref.height),
				wdh = ref._wdh || (ref._wdh = ref.left + ref.width);
			return cur.top >= hgh || cur.left < wdh;
		} else {
			var hgh = ref._hgh || (ref._hgh = ref.top + ref.height),
				wdh = ref._wdh || (ref._wdh = ref.left + ref.width);
			return cur.left >= wdh || cur.top < hgh;
		}
	} 
zFlex = { //static methods
	fixFlexX: function (ctl, opts, resize) {
		var wgt = this;
		//avoid firedown("onShow") firedown("onSize") calling in again
		if ((wgt._vflex === undefined || (wgt._vflexsz && wgt._vflex == 'min'))
			&& (wgt._hflex === undefined || (wgt._hflexsz && wgt._hflex == 'min'))) 
			return;
		
		//a resize fired by myself, simply call directly to fixFlex
		if (resize) {
			zFlex.fixFlex.apply(wgt);
			return;
		}
		
		//normal triggering
		var r1 = wgt, p1 = r1,
			j1 = -1;
		if (wgt._hflex == 'min' && wgt._hflexsz === undefined && !wgt.ignoreFlexSize_('w')) {
			++j1;
			while ((p1 = p1.parent) && p1._hflex == 'min') {
				delete p1._hflexsz;
				r1 = p1;
				++j1;
				if (p1.ignoreFlexSize_('w')) //p1 will not affect its parent's flex size
					break;
			}
		}
		var r2 = wgt, p2 = r2,
			j2 = -1;
		if (wgt._vflex == 'min' && wgt._vflexsz === undefined && !wgt.ignoreFlexSize_('h')) {
			++j2;
			while ((p2 = p2.parent) && p2._vflex == 'min') {
				delete p2._vflexsz;
				r2 = p2;
				++j2;
				if (p2.ignoreFlexSize_('h')) //p2 will not affect its parent's flex size
					break;
			}
		}
		if (j1 > 0 || j2 > 0)
			zWatch.fireDown('onSize', j1 > j2 ? r1 : r2, null, true); //true to indicate wgt is a resize
		else
			zFlex.fixFlex.apply(r2);

	},
	fixFlex: function () {
		var wgt = this;
		//avoid firedown("onSize") calling in again
		if ((wgt._vflex === undefined || (wgt._vflexsz && wgt._vflex == 'min'))
			&& (wgt._hflex === undefined || (wgt._hflexsz && wgt._hflex == 'min'))) 
			return;
		
		if (!wgt.parent.beforeChildrenFlex_(wgt)) { //don't do fixflex if return false
			return;
		}
		
		if (wgt._flexFixed || (!wgt._nvflex && !wgt._nhflex)) { //other vflex/hflex sibliing has done it!
			delete wgt._flexFixed;
			return;
		}
		wgt._flexFixed = true;
		
		var pretxt = false, //pre node is a text node
			vflexs = [],
			vflexsz = 0,
			hflexs = [],
			hflexsz = 0,
			p = wgt.$n().parentNode,
			zkp = zk(p),
			psz = wgt.getParentSize_(p),
			hgh = psz.height,
			wdh = psz.width,
			c = p.firstChild,
			scrWdh = jq.scrollbarWidth();
			
		// Bug 3185686
		// has vertical scrollbar
		if(p.offsetWidth - p.clientWidth > 11)
			wdh -= scrWdh;
			
		// has horizontal scrollbar
		// check p.clientHeight for B50-3312936.zul
		if(p.clientHeight && p.offsetHeight - p.clientHeight > 11)
			hgh -= scrWdh;
			
		for (; c; c = c.nextSibling)
			if (c.nodeType != 3) break; //until not a text node
		
		
		var zkpOffset = zkp.cmOffset();

		for (; c; c = c.nextSibling) {
			//In ZK, we assume all text node is space (otherwise, it will be span enclosed)
			if (c.nodeType === 3) { //a text node
				pretxt = true;
				continue;
			}
			
			var zkc = zk(c);
			if (zkc.isVisible()) {
				var offhgh = zkc.offsetHeight(),
					offwdh = offhgh > 0 ? zkc.offsetWidth() : 0,
					cwgt = zk.Widget.$(c, {exact: 1});
				
				//horizontal size
				if (cwgt && cwgt._nhflex) {
					if (cwgt !== wgt)
						cwgt._flexFixed = true; //tell other hflex siblings I have done it.
					if (cwgt._hflex == 'min') {
						var minwdh = zFlex.fixMinFlex.apply(cwgt, [c, 'w']);
							wdh -= minwdh;
					} else {
						if (pretxt) {
							var w = _getTextWidth(zkc, zkp, zkpOffset);
							wdh -= w;
						}
						hflexs.push(cwgt);
						hflexsz += cwgt._nhflex;
					}
				} else {
					wdh -= offwdh;
					wdh -= zkc.sumStyles("lr", jq.margins);
				}
				
				//vertical size
				if (cwgt && cwgt._nvflex) {
					if (cwgt !== wgt)
						cwgt._flexFixed = true; //tell other vflex siblings I have done it.
					if (cwgt._vflex == 'min') {
						var minhgh = zFlex.fixMinFlex.apply(cwgt, [c, 'h']);
							hgh -= minhgh;
					} else {
						if (pretxt) {
							var h = _getTextHeight(zkc, zkp, zkpOffset);
							hgh -= h;
						}
						vflexs.push(cwgt);
						vflexsz += cwgt._nvflex;
					}
				} else {			
					hgh -= offhgh;
					hgh -= zkc.sumStyles("tb", jq.margins);
				}
				pretxt = false;
			}
		}
				
		//setup the height for the vflex child
		//avoid floating number calculation error(TODO: shall distribute error evenly)
		var lastsz = hgh > 0 ? hgh : 0;
		for (var j = vflexs.length - 1; j > 0; --j) {
			var cwgt = vflexs.shift(), 
				vsz = (cwgt._nvflex * hgh / vflexsz) | 0; //cast to integer
			cwgt.setFlexSize_({height:vsz});
			cwgt._vflexsz = vsz;
			lastsz -= vsz;
		}
		//last one with vflex
		if (vflexs.length) {
			var cwgt = vflexs.shift();
			cwgt.setFlexSize_({height:lastsz});
			cwgt._vflexsz = lastsz;
		}
		//3042306: H/Vflex in IE6 can't shrink; others cause scrollbar space
		//vertical scrollbar might disappear after height was set
		var newpsz = wgt.getParentSize_(p);
		if (newpsz.width > psz.width) //yes, the scrollbar gone!
			wdh += (newpsz.width - psz.width); 
		
		//setup the width for the hflex child
		//avoid floating number calculation error(TODO: shall distribute error evenly)
		lastsz = wdh > 0 ? wdh : 0;
		for (var j = hflexs.length - 1; j > 0; --j) {
			var cwgt = hflexs.shift(), //{n: node, f: hflex} 
				hsz = (cwgt._nhflex * wdh / hflexsz) | 0; //cast to integer
			cwgt.setFlexSize_({width:hsz});
			cwgt._hflexsz = hsz;
			lastsz -= hsz;
		}
		//last one with hflex
		if (hflexs.length) {
			var cwgt = hflexs.shift();
			cwgt.setFlexSize_({width:lastsz});
			cwgt._hflexsz = lastsz;
		}
		
		//notify parent widget that all of its children with hflex/vflex is done.
		wgt.parent.afterChildrenFlex_(wgt);
		wgt._flexFixed = false;
	},
	fixMinFlex: function (wgtn, o) {
		var wgt = this,
			min = wgt.beforeMinFlex_(o);
			
		//find the max size of all children
		if (o == 'h') {
			if (wgt._vflexsz === undefined) { //cached?
				var cwgt = wgt.firstChild, //bug #2928109
					n = wgtn,
					zkn = zk(n),
					max = 0;
				if (min != null)
					max = min;
				else {
					wgt.setFlexSize_({height:'auto'}, true);
					var totalsz = 0,
						vmax = 0;
					if (cwgt){ //try child widgets
						var first = cwgt,
							refDim = zk(cwgt).dimension(true);
						for (; cwgt; cwgt = cwgt.nextSibling) { //bug 3132199: hflex="min" in hlayout
							if (!cwgt.ignoreFlexSize_('h')) {
								var c = cwgt.$n();
								if (c) { //node might not exist if rod on
									var zkc = zk(c),
										sz = 0; 
									if (cwgt._vflex == 'min') {
										if (zkc.isVisible()) {
											sz += cwgt._vflexsz === undefined ? zFlex.fixMinFlex.apply(cwgt, [c, o]) : cwgt._vflexsz;
										} else
											sz += cwgt._vflexsz === undefined ? 0 : cwgt._vflexsz;
									} else {
										cwgt.beforeParentMinFlex_(o);
										sz += wgt.getChildMinSize_(o, cwgt); // fixed for B50-3157031.zul
										
										var bm = zkc.sumStyles("tb", jq.margins);
										sz += bm;
									}
									
									var curDim = first != curDim ? zkc.dimension(true) : false;
									//bug #3006276: East/West bottom cut if East/West higher than Center.
									if (cwgt._maxFlexHeight && sz > vmax) //@See West/East/Center
										vmax = sz;
									else if (cwgt._sumFlexHeight) //@See North/South
										totalsz += sz;
									else if (!cwgt._maxFlexHeight && curDim && _isSameBaseline(refDim, curDim, true))
										max += sz;
									else if (sz > max)
										max = sz;
								}
							}
						}
					} else {
						var c = wgtn.firstChild;
						if (c) { //no child widget, try html element directly
							//feature 3000339: The hflex of the cloumn will calculate by max width
							var isText = c.nodeType == 3,
								ignore = wgt.ignoreChildNodeOffset_('h'),
								refDim = isText ? null : zk(c).dimension(true);
							for(; c; c = c.nextSibling) {
								var zkc = zk(c),
									sz = 0;
								if (ignore) {
									for(; el; el = el.nextSibling) {
										var txt = el && el.nodeType == 3 ? el.nodeValue : null,
											zel;
										if (txt) {
											var dim = zkc.textSize(txt);
											if (dim[1] > sz)
												sz = dim[1];
										} else if ((zel = zk(el)).isVisible()) {
											var h = zel.offsetHeight() + zel.sumStyles("tb", jq.margins);
											if (h > sz)
												sz = h;
										}
									}
								} else {
									if (c.nodeType == 3)
										sz = c.nodeValue ? zkn.textSize(c.nodeValue)[1] : 0;
									else {
										sz = zkc.offsetHeight() + zkc.sumStyles("tb", jq.margins);
									}
								}
								if (isText) {
									if (sz > max) 
										max = sz;
								} else {
									var curDim = zkc.dimension(true);
									if (_isSameBaseline(refDim, curDim, true)) 
										max += sz;
									else if (sz > max) 
										max = sz;
								}
							}
						} else //no kids at all, use self
							max = zkn.offsetHeight();
					}
					if (vmax)
						totalsz += vmax;
					if (totalsz > max)
						max = totalsz;
				}
				
				var margin = this.getMarginSize_(o);
				if (zk.safari && margin < 0) 
					margin = 0;

				sz = wgt.setFlexSize_({height:(max + _getContentEdgeHeight(wgt) + margin)}, true);
				if (sz && sz.height >= 0)
					wgt._vflexsz = sz.height + margin;
				wgt.afterChildrenMinFlex_('h');
			}
			return wgt._vflexsz;
			
		} else if (o == 'w') {
			if (wgt._hflexsz === undefined) { //cached?
				var cwgt = wgt.firstChild, //bug #2928109
					n = wgtn,
					zkn = zk(n),
					max = 0;
				if (min != null)
					max = min;
				else {
					wgt.setFlexSize_({width:'auto'}, true);
					var totalsz = 0;
					if (cwgt) { //try child widgets
						var first = cwgt,
							refDim = zk(cwgt).dimension(true);
						for (; cwgt; cwgt = cwgt.nextSibling) { //bug#3132199: hflex="min" in hlayout
							if (!cwgt.ignoreFlexSize_('w')) {
								var c = cwgt.$n();
								if (c) { //node might not exist if rod on
									var	zkc = zk(c),
										sz = 0;
									if (cwgt._hflex == 'min') {
										if (zkc.isVisible()) {
											sz += cwgt._hflexsz === undefined ? zFlex.fixMinFlex.apply(cwgt, [c, o]) : cwgt._hflexsz;
										} else
											sz += cwgt._hflexsz === undefined ? 0 : cwgt._hflexsz;
									} else {
										cwgt.beforeParentMinFlex_(o);
										sz += wgt.getChildMinSize_(o, cwgt); // fixed for B50-3157031.zul
										var rm = zkc.sumStyles("lr", jq.margins);
											sz += rm;
									}
									var curDim = first != cwgt ? zkc.dimension(true) : false;
									if (cwgt._sumFlexWidth) //@See East/West/Center
										totalsz += sz;
									else if (curDim && _isSameBaseline(refDim, curDim))
										max += sz;
									else if (sz > max)
										max = sz;
								}
							}
						}
					} else {
						var c = wgtn.firstChild;
						if (c) { //no child widget, try html element directly
							//feature 3000339: The hflex of the cloumn will calculate by max width
							var isText = c.nodeType == 3,
								ignore = wgt.ignoreChildNodeOffset_('w'),
								refDim = isText ? null : zk(c).dimension(true);
								
							for(; c; c = c.nextSibling) { 
								var	zkc = zk(c),
									sz = 0;
								if (ignore) {
									var el = c.firstChild;
									for(; el; el = el.nextSibling) {
										var txt = el && el.nodeType == 3 ? el.nodeValue : null,
											zel;
										if (txt) {
											var dim = zkc.textSize(txt);
											if (dim[1] > sz)
												sz = dim[1];
										} else if ((zel = zk(el)).isVisible()){
											var w = zel.offsetWidth() + zel.sumStyles("lr", jq.margins);
											if (w > sz)
												sz = w;
										}
									}
								} else {
									if (c.nodeType == 3)
										sz = c.nodeValue ? zkn.textSize(c.nodeValue)[0] : 0;
									else {
										sz = zkc.offsetWidth() + zkc.sumStyles("lr", jq.margins);
									}
								}
								if (isText) {
									if (sz > max) 
										max = sz;
								} else {
									var curDim = zkc.dimension(true);
									if (_isSameBaseline(refDim, curDim)) 
										max += sz;
									else if (sz > max) 
										max = sz;
								}
							}
						} else //no kids at all, use self
							max = zkn.offsetWidth();// - zkn.padBorderWidth();
					}
					if (totalsz > max)
						max = totalsz;
				}
					
				//bug #3005284: (Chrome)Groupbox hflex="min" in borderlayout wrong sized
				//bug #3006707: The title of the groupbox shouldn't be strikethrough(Chrome)
				var margin = this.getMarginSize_(o);
				if (zk.safari && margin < 0)
					margin = 0;
				var sz = wgt.setFlexSize_({width:(max + _getContentEdgeWidth(wgt) + margin)}, true);
				if (sz && sz.width >= 0)
					wgt._hflexsz = sz.width + margin;
				wgt.afterChildrenMinFlex_('w');
			}
			return wgt._hflexsz;
		} else
			return 0;
	}
};
})();
