/* widget.js

	Purpose:
		Widget - the UI object at the client
	Description:
		
	History:
		Tue Sep 30 09:23:56     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var _binds = {}, //{uuid, wgt}: bind but no node
		_globals = {}, //global ID space {id, [wgt...]}
		_floatings = [], //[{widget,node}]
		_nextUuid = 0,
		_domevtfnm = {}, //{evtnm, funnm}
		_domevtnm = {onDoubleClick: 'dblclick'}, //{zk-evt-nm, dom-evt-nm}
		_wgtcls = {}, //{clsnm, cls}
		_hidden = [], //_autohide
		_noChildCallback, //used by removeChild/appendChild/insertBefore
		_syncdt = zUtl.now() + 60000; //when zk.Desktop.sync() shall be called

	//Check if el is a prolog
	function _isProlog(el) {
		var txt;
		return el && el.nodeType == 3 //textnode
			&& (txt=el.nodeValue) && !txt.trim().length;
	}

	//Event Handling//
	function _domEvtInf(wgt, evtnm, fn) { //proxy event listener
		if (typeof fn != "function") {
			if (!fn && !(fn = _domevtfnm[evtnm]))
				_domevtfnm[evtnm] = fn = '_do' + evtnm.substring(2);

			var f = wgt[fn];
			if (!f)
				throw 'Listener ' + fn + ' not found in ' + wgt.className;
			fn = f;
		}

		var domn = _domevtnm[evtnm];
		if (!domn)
			domn = _domevtnm[evtnm] = evtnm.substring(2).toLowerCase();
		return [domn, _domEvtProxy(wgt, fn)];
	}
	function _domEvtProxy(wgt, f) {
		var fps = wgt._$evproxs, fp;
		if (!fps) wgt._$evproxs = fps = {};
		else if (fp = fps[f]) return fp;
		return fps[f] = _domEvtProxy0(wgt, f);
	}
	function _domEvtProxy0(wgt, f) {
		return function (devt) {
			var args = [], evt;
			for (var j = arguments.length; --j > 0;)
				args.unshift(arguments[j]);
			args.unshift(evt = jq.Event.zk(devt, wgt));

			switch (devt.type){
			case 'focus':
				if (wgt.canActivate()) {
					zk.currentFocus = wgt;
					zWatch.fire('onFloatUp', wgt); //notify all
					break;
				}
				return; //ignore it
			case 'blur':
				//due to mimicMouseDown_ called, zk.currentFocus already corrected,
				//so we clear it only if caused by other case
				if (!zk._cfByMD) zk.currentFocus = null;
				break;
			case 'click':
			case 'dblclick':
			case 'mouseup': //we cannot simulate mousedown:(
				if (zk.Draggable.ignoreClick())
					return;
			}

			var ret = f.apply(wgt, args);
			if (ret === undefined) ret = evt.returnValue;
			if (evt.domStopped) devt.stop();
			return devt.type == 'dblclick' && ret === undefined ? false: ret;
		};
	}

	function _bind0(wgt) {
		_binds[wgt.uuid] = wgt;
		if (wgt.id)
			_addGlobal(wgt);
	}
	function _unbind0(wgt) {
		if (wgt.id)
			_rmGlobal(wgt);
		delete _binds[wgt.uuid];
		wgt.desktop = null;
		wgt.clearCache();
	}
	function _bindrod(wgt) {
		_bind0(wgt);
		if (!wgt.z_rod)
			wgt.z_rod = 9; //Bug 2948829: don't use true which is used by real ROD, such as combo-rod.js

		for (var child = wgt.firstChild; child; child = child.nextSibling)
			_bindrod(child);
	}
	function _unbindrod(wgt, nest) {
		_unbind0(wgt);

		if (!nest || wgt.z_rod === 9) { //Bug 2948829: don't delete value set by real ROD
			delete wgt.z_rod;

			for (var child = wgt.firstChild; child; child = child.nextSibling)
				_unbindrod(child, true);
		}
	}

	function _fixBindLevel(wgt, v) {
		wgt.bindLevel = v++;
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			_fixBindLevel(wgt, v);
	}

	function _addIdSpace(wgt) {
		if (wgt._fellows) wgt._fellows[wgt.id] = wgt;
		var p = wgt.parent;
		if (p) {
			p = p.$o();
			if (p) p._fellows[wgt.id] = wgt;
		}
	}
	function _rmIdSpace(wgt) {
		if (wgt._fellows) delete wgt._fellows[wgt.id];
		var p = wgt.parent;
		if (p) {
			p = p.$o();
			if (p) delete p._fellows[wgt.id];
		}
	}
	function _addIdSpaceDown(wgt) {
		var ow = wgt.parent;
		ow = ow ? ow.$o(): null;
		if (ow)
			_addIdSpaceDown0(wgt, ow);
	}
	function _addIdSpaceDown0(wgt, owner) {
		if (wgt.id) owner._fellows[wgt.id] = wgt;
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			_addIdSpaceDown0(wgt, owner);
	}
	function _rmIdSpaceDown(wgt) {
		var ow = wgt.parent;
		ow = ow ? ow.$o(): null;
		if (ow)
			_rmIdSpaceDown0(wgt, ow);
	}
	function _rmIdSpaceDown0(wgt, owner) {
		if (wgt.id)
			delete owner._fellows[wgt.id];
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			_rmIdSpaceDown0(wgt, owner);
	}
	//note: wgt.id must be checked before calling this method
	function _addGlobal(wgt) {
		var gs = _globals[wgt.id];
		if (gs)
			gs.push(wgt);
		else
			_globals[wgt.id] = [wgt];
	}
	function _rmGlobal(wgt) {
		var gs = _globals[wgt.id];
		if (gs) {
			gs.$remove(wgt);
			if (!gs.length) delete _globals[wgt.id];
		}
	}
	function _fireClick(wgt, evt) {
		if (!wgt.shallIgnoreClick_(evt) && 
			!wgt.fireX(evt).stopped && evt.shallStop) {
			evt.stop();
			return false;	
		}
		return !evt.stopped;
	}

	//set minimum flex size and return it
	function _setMinFlexSize(wgt, wgtn, o) {
		//find the max size of all children
		if (o == 'height') {
			if (wgt._vflexsz === undefined) { //cached?
				wgt.setFlexSize_({height:'auto'});
				var cwgt = wgt.firstChild, //bug #2928109
					cwgtn = cwgt && cwgt.$n(),
					n = cwgtn ? cwgtn.parentNode : wgtn,
					c = n.firstChild,
					zkn = zk(n),
					ntop = n.offsetTop,
					noffParent = n.offsetParent,
					tp = zkn.sumStyles("t", jq.paddings), //bug #3006718: The  hflex listbox after separator cause wrong width on IE6
					tbp = tp + zkn.sumStyles("t", jq.borders),
					max = 0,
					vmax = 0,
					totalsz = 0;
				if (cwgt){ //try child widgets
					for (; cwgt; cwgt = cwgt.nextSibling) {
						c = cwgt.$n();
						if (c) { //node might not exist if rod on
							//bug# 2997862: vflex="min" not working on nested tabpanel
							var zkc = zk(c),
								sameOffParent = c.offsetParent == noffParent,
								sz = 0;
							if (!cwgt.ignoreFlexSize_('h')) {
								sz = c.offsetTop - (sameOffParent ? ntop + tbp : tp); 
								if (cwgt._vflex == 'min') {
									if (zkc.isVisible()) {
										sz += cwgt._vflexsz === undefined ? _setMinFlexSize(cwgt, c, o) : cwgt._vflexsz;
										var tm = zkc.sumStyles("t", jq.margins);
										if (!zk.safari || tm >= 0)
											sz -= tm;
									} else
										sz += cwgt._vflexsz === undefined ? 0 : cwgt._vflexsz;
								} else {
									sz += c.offsetHeight;
									var bm = zkc.sumStyles("b", jq.margins);
									if (!zk.safari || bm >= 0)
										sz += bm;
								}
							}
							//bug #3006276: East/West bottom cut if East/West higher than Center.
							if (cwgt._maxFlexHeight && sz > vmax) //@See West/East/Center
								vmax = sz;
							else if (cwgt._sumFlexHeight) //@See North/South
								totalsz += sz;
							else if (sz > max)
								max = sz;
						}
					}
				} else if (c) { //no child widget, try html element directly
					//feature 3000339: The hflex of the cloumn will calculate by max width
					var ignore = wgt.ignoreChildNodeOffset_('h');
					for(; c; c = c.nextSibling) {
						var zkc = zk(c),
							sz = 0;
						if (ignore) {
							var el = c.firstChild,
								txt = el && el.nodeType == 3 ? el.nodeValue : null;
							if (txt) {
								var dim = zkc.textSize(txt);
								sz = dim[1]; //height
								if (sz > max)
									max = sz;
							}
						}
						var sameOffParent = c.offsetParent == noffParent,
							bm = zkc.sumStyles(ignore ? "tb" : "b", jq.margins);
						sz = c.offsetHeight + (ignore ? 0 : c.offsetTop - (sameOffParent ? ntop + tbp : tp));
						
						if (!zk.safari || bm >= 0)
							sz += bm;
						if (sz > max)
							max = sz;
					}
				} else //no kids at all, use self
					max = n.offsetHeight - zkn.padBorderHeight();  

				if (vmax)
					totalsz += vmax;
				if (totalsz > max)
					max = totalsz;
				
				//n might not be widget's element, add up the pad/border/margin/offsettop in between
				var pb = 0,
					precalc = false;
				while (n && n != wgtn) {
					if (!precalc)
						pb += zkn.padBorderHeight();
					else {
						pb += zkn.sumStyles("b", jq.paddings);
						pb += zkn.sumStyles("b", jq.borders);
					}
					var p = n.parentNode,
						ptop = p ? p.offsetTop : 0,
						poffParent = p ? p.offsetParent : null;
					precalc = n.offsetParent == poffParent; 
					pb += n.offsetTop;
					if (precalc)
						pb -= ptop;
					var bm = zkn.sumStyles("b", jq.margins);
					if (!zk.safari || bm >=0)
						pb += bm;
					n = p;
					zkn = zk(n);
				}
				if (!precalc)
					pb += zkn.padBorderHeight();
				else {
					pb += zkn.sumStyles("b", jq.paddings);
					pb += zkn.sumStyles("b", jq.borders);
				}
				var margin = zk(wgtn).sumStyles("tb", jq.margins);
				if (zk.safari && margin < 0) 
					margin = 0;
				sz = wgt.setFlexSize_({height:(max + pb + margin)});
				if (sz && sz.height >= 0)
					wgt._vflexsz = sz.height + margin;
				wgt.afterChildrenMinFlex_();
			}
			return wgt._vflexsz;
			
		} else if (o == 'width') {
			if (wgt._hflexsz === undefined) { //cached?
				wgt.setFlexSize_({width:'auto'});
				var cwgt = wgt.firstChild, //bug #2928109
					cwgtn = cwgt && cwgt.$n(),
					n = cwgtn ? cwgtn.parentNode : wgtn,
					c = n.firstChild,
					zkn = zk(n),
					nleft = n.offsetLeft,
					noffParent = n.offsetParent,
					lp = zkn.sumStyles("l", jq.paddings), //bug #3006718: The  hflex listbox after separator cause wrong width on IE6
					lbp = lp + zkn.sumStyles("l", jq.borders), 
					max = 0,
					totalsz = 0;
				if (cwgt) { //try child widgets
					for (; cwgt; cwgt = cwgt.nextSibling) {
						c = cwgt.$n();
						if (c) { //node might not exist if rod on
							//bug# 2997862: vflex="min" not working on nested tabpanel(shall handle hflex, too
							var zkc = zk(c),
								sameOffParent = c.offsetParent == noffParent,
								sz = 0;
							if (!cwgt.ignoreFlexSize_('w')) {
								sz = c.offsetLeft - (sameOffParent ?  nleft + lbp: lp);
								if (cwgt._hflex == 'min') {
									if (zkc.isVisible()) {
										sz += cwgt._hflexsz === undefined ? _setMinFlexSize(cwgt, c, o) : cwgt._hflexsz;
										var lm = zkc.sumStyles("l", jq.margins);
										if (!zk.safari || lm >= 0)
											sz -= lm;
									} else
										sz += cwgt._hflexsz === undefined ? 0 : cwgt._hflexsz;
								} else {
									sz += c.offsetWidth;
									var rm = zkc.sumStyles("r", jq.margins);
									if (!zk.safari || rm >= 0)
										sz += rm;
								}
								if (cwgt._sumFlexWidth) //@See East/West/Center
									totalsz += sz;
								else if (sz > max)
									max = sz;
							}
						}
					}
				} else if (c) { //no child widget, try html element directly
					//feature 3000339: The hflex of the cloumn will calculate by max width
					var ignore = wgt.ignoreChildNodeOffset_('w');
					for(; c; c = c.nextSibling) {
						var zkc = zk(c),
							sz = 0;
						if (ignore) {
							var el = c.firstChild,
								txt = el && el.nodeType == 3 ? el.nodeValue : null;
							if (txt) {
								var dim = zkc.textSize(txt);
								sz = dim[0]; //width
								if (sz > max)
									max = sz;
							}
						}
						var	sameOffParent = c.offsetParent == noffParent,
							rm = zkc.sumStyles(ignore ? "lr" : "r", jq.margins);
						sz = c.offsetWidth + (ignore ? 0 : c.offsetLeft - (sameOffParent ? nleft + lbp : lp));
						if (!zk.safari || rm >= 0)
							sz +=  rm;
						if (sz > max)
							max = sz;
					}
				} else //no kids at all, use self
					max = n.offsetWidth - zkn.padBorderWidth();
				
				if (totalsz > max)
					max = totalsz;
				
				//n might not be widget's element, add up the pad/border/margin in between
				var pb = 0,
					precalc = false;
				while (n && n != wgtn) {
					if (!precalc)
						pb += zkn.padBorderWidth();
					else {
						pb += zkn.sumStyles("r", jq.paddings);
						pb += zkn.sumStyles("r", jq.borders);
					}
					var p = n.parentNode,
						pleft = p ? p.offsetLeft : 0,
						poffParent = p ? p.offsetParent : null;
					precalc = n.offsetParent == poffParent; 
					pb += n.offsetLeft;
					if (precalc)
						pb -= pleft;
					var rm = zkn.sumStyles("r", jq.margins);
					if (!zk.safari || rm >= 0)
						pb += rm; 
					n = p;
					zkn = zk(n);
				}
				if (!precalc)
					pb += zkn.padBorderWidth();
				else {
					pb += zkn.sumStyles("r", jq.paddings);
					pb += zkn.sumStyles("r", jq.borders);
				}
					
				//bug #3005284: (Chrome)Groupbox hflex="min" in borderlayout wrong sized
				//bug #3006707: The title of the groupbox shouldn't be strikethrough(Chrome)
				var ignoreMargin = wgt._isIgnoreMargin && wgt._isIgnoreMargin(), 
					margin = ignoreMargin ? 0 : zk(wgtn).sumStyles("lr", jq.margins);
				if (zk.safari && margin < 0)
					margin = 0;
				var sz = wgt.setFlexSize_({width:(max + pb + margin)}, ignoreMargin);
				if (sz && sz.width >= 0)
					wgt._hflexsz = sz.width + margin;
				wgt.afterChildrenMinFlex_();
			}
			return wgt._hflexsz;
		} else
			return 0;
	}
	//fix vflex/hflex of all my sibling nodes
	//feature #3000873 tabbox can auto grow when select larger tabpanel
	function _fixFlexX(ctl, opts, resize) {
		//avoid firedown("onShow") firedown("onSize") calling in again
		if (this._vflexsz && this._vflex == 'min' && this._hflexsz && this._hflex == 'min') 
			return;
		
		//a resize fired by myself, simply call directly to _fixFlex
		if (resize) {
			_fixFlex.apply(this);
			return;
		}
		
		//normal triggering
		var r1 = p1 = this,
			j1 = -1;
		if (this._hflex == 'min' && this._hflexsz === undefined) {
			++j1;
			while ((p1 = p1.parent) && p1._hflex == 'min') {
				delete p1._hflexsz;
				r1 = p1;
				++j1;
			}
		}
		var r2 = p2 = this,
			j2 = -1;
		if (this._vflex == 'min' && this._vflexsz === undefined) {
			++j2;
			while ((p2 = p2.parent) && p2._vflex == 'min') {
				delete p2._vflexsz;
				r2 = p2;
				++j2;
			}
		}
		if (j1 > 0 || j2 > 0)
			zWatch.fireDown('onSize', j1 > j2 ? r1 : r2, null, true); //true to indicate this is a resize
		else
			_fixFlex.apply(r2);

	}
	//fix vflex/hflex of all my sibling nodes
	function _fixFlex() {
		//avoid firedown("onSize") calling in again
		if (this._vflexsz && this._vflex == 'min' && this._hflexsz && this._hflex == 'min') 
			return;
		
		if (!this.parent.beforeChildrenFlex_(this)) { //don't do fixflex if return false
			return;
		}
		
		if (this._flexFixed || (!this._nvflex && !this._nhflex)) { //other vflex/hflex sibliing has done it!
			delete this._flexFixed;
			return;
		}
		
		this._flexFixed = true;
		
		var pretxt = false, //pre node is a text node
			prevflex = false, //pre node is vflex
			prehflex = false, //pre node is hflex
			vflexs = [],
			vflexsz = 0,
			hflexs = [],
			hflexsz = 0,
			p = this.$n().parentNode,
			zkp = zk(p),
			psz = this.getParentSize_(p),
			hgh = psz.height,
			wdh = psz.width,
			c = p.firstChild;
		
		for (; c; c = c.nextSibling)
			if (c.nodeType != 3) break; //until not a text node
		
		//ie6 must set parent div to 'relative' or the kid div's offsetTop is not correct
		var oldPos;
		if (zk.ie6_ && jq.nodeName(p, 'div')) {
			oldPos = p.style.position;
			p.style.position = 'relative';
		}
		var ptop = p.offsetTop,
			pleft = p.offsetLeft,
			tp = zkp.sumStyles("t", jq.paddings), //bug #3006718: The  hflex listbox after separator cause wrong width on IE6
			tbp = zkp.sumStyles('t', jq.borders) + tp,
			lp = zkp.sumStyles("l", jq.paddings), //bug #3006718: The  hflex listbox after separator cause wrong width on IE6
			lbp = zkp.sumStyles('l', jq.borders) + lp, 
			segTop = 0,
			segLeft = 0,
			segBottom = segTop,
			segRight = segLeft;

		for (; c; c = c.nextSibling) {
			var zkc = zk(c);
			if (zkc.isVisible()) {
				//In ZK, we assume all text node is space (otherwise, it will be span enclosed)
				if (c.nodeType === 3) { //a text node
					pretxt = true;
					prevflex = prehflex = false;
					continue;
				}
				var offhgh = zkc.offsetHeight(),
					offwdh = offhgh > 0 ? zkc.offsetWidth() : 0, //div with zero height might have 100% width
					sameOffParent = c.offsetParent === p.offsetParent, 
					offTop = c.offsetTop - (sameOffParent ? tbp + ptop : tp),
					offLeft = c.offsetLeft - (sameOffParent ?  lbp + pleft : lp),
					marginRight = offLeft + offwdh + zkc.sumStyles("r", jq.margins),
					marginBottom = offTop + offhgh + zkc.sumStyles("b", jq.margins);
					
				var cwgt = _binds[c.id];
				
				//horizontal size
				if (cwgt && cwgt._nhflex) {
					if (cwgt !== this)
						cwgt._flexFixed = true; //tell other hflex siblings I have done it.
					if (cwgt._hflex == 'min') {
						_setMinFlexSize(cwgt, c, 'width');
						//might change width in _setMinFlexSize(), so regain the value
						offLeft = c.offsetLeft - (sameOffParent ? lbp + pleft : lp);
						offwdh = zkc.offsetWidth();
						marginRight = offLeft + offwdh + zkc.sumStyles('r', jq.margins);
						segRight = Math.max(segRight, marginRight);
						prehflex = false;
					} else {
						if (pretxt) {
							var txtmarginRight = offTop - zkc.sumStyles('l', jq.margins);
							segRight = Math.max(segRight, txtmarginRight);
						}
						if (!prehflex && segRight > segLeft) {
							wdh -= segRight - segLeft;
						}
						segLeft = segRight = marginRight;
						
						hflexs.push(cwgt);
						hflexsz += cwgt._nhflex;
						prehflex = true;
					}
				} else {
					segRight = Math.max(segRight, marginRight);
					prehflex = false;
				}
				
				//vertical size
				if (cwgt && cwgt._nvflex) {
					if (cwgt !== this)
						cwgt._flexFixed = true; //tell other vflex siblings I have done it.
					if (cwgt._vflex == 'min') {
						_setMinFlexSize(cwgt, c, 'height');
						//might change height in _setMinFlexSize(), so regain the value
						offTop = c.offsetTop - (sameOffParent ? tbp + ptop : tp);
						offhgh = zkc.offsetHeight();
						marginBottom = offTop + offhgh + zkc.sumStyles('b', jq.margins);
						segBottom = Math.max(segBottom, marginBottom);
						prevflex = false;
					} else {
						if (pretxt) {
							var txtmarginBottom = offTop - zkc.sumStyles('t', jq.margins);
							segBottom = Math.max(segBottom, txtmarginBottom);
						}
						if (!prevflex && segBottom > segTop) {
							hgh -= segBottom - segTop;
						}
						segTop = segBottom = marginBottom;
						
						vflexs.push(cwgt);
						vflexsz += cwgt._nvflex;
						prevflex = true;
					}
				} else {
					segBottom = Math.max(segBottom, marginBottom);
					prevflex = false;
				}
				pretxt = false;
			}
		}
		
		if (zk.ie6_ && jq.nodeName(p, 'div')) { //ie6, restore to orignial position style
			p.style.position = oldPos;
		}

		if (segBottom > segTop) {
			hgh -= segBottom - segTop;
		}
		if (segRight > segLeft) {
			wdh -= segRight - segLeft;
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
		
		//notify parent widget that all of its children with vflex is done.
		this.parent.afterChildrenFlex_(this);
		this._flexFixed = false;
	}
	function _listenFlex(wgt) {
		if (!wgt._flexListened){
			zWatch.listen({onSize: [wgt, _fixFlexX], onShow: [wgt, _fixFlexX]});
			wgt._flexListened = true;
		}
	}
	function _unlistenFlex(wgt) {
		if (wgt._flexListened) {
			zWatch.unlisten({onSize: [wgt, _fixFlexX], onShow: [wgt, _fixFlexX]});
			delete wgt._flexListened;
		}
	}

	/** @class zk.DnD
	 * Drag-and-drop utility.
	 * It is the low-level utility reserved for overriding for advanced customization.
	 */
	zk.DnD = { //for easy overriding
		/** Returns the widget to drop to.
		 * @param zk.Draggable drag the draggable controller
		 * @param Offset pt the mouse pointer's position.
		 * @param jq.Event evt the DOM event
		 * @return zk.Widget
		 */
		getDrop: function (drag, pt, evt) {
			var wgt = evt.target;
			return wgt ? wgt.getDrop_(drag.control): null;
		},
		/** Ghost the DOM element being dragging
		 * @param zk.Draggable drag the draggable controller
		 * @param Offset ofs the offset of the returned element (left/top)
		 * @param String msg the message to show inside the returned element
		 * @return DOMElement the element representing what is being dragged
		 */
		ghost: function (drag, ofs, msg) {
			if (msg != null)  {
				jq(document.body).append(
					'<div id="zk_ddghost" class="z-drop-ghost" style="position:absolute;top:'
					+ofs[1]+'px;left:'+ofs[0]+'px;"><div class="z-drop-cnt"><span id="zk_ddghost-img" class="z-drop-disallow"></span>&nbsp;'+msg+'</div></div>');
				drag._dragImg = jq("#zk_ddghost-img")[0];
				return jq("#zk_ddghost")[0];
			}

			var dgelm = jq(drag.node).clone()[0];
			dgelm.id = "zk_ddghost";
			zk.copy(dgelm.style, {
				position: "absolute", left: ofs[0] + "px", top: ofs[1] + "px"
			});
			document.body.appendChild(dgelm);
			return dgelm;
		}
	};
	function DD_cleanLastDrop(drag) {
		if (drag) {
			var drop;
			if (drop = drag._lastDrop) {
				drag._lastDrop = null;
				drop.dropEffect_();
			}
			drag._lastDropTo = null;
		}
	}
	function DD_pointer(evt) {
		return [evt.pageX + 10, evt.pageY + 5];
	}
	function DD_enddrag(drag, evt) {
		DD_cleanLastDrop(drag);
		var pt = [evt.pageX, evt.pageY],
			wgt = zk.DnD.getDrop(drag, pt, evt);
		if (wgt) wgt.onDrop_(drag, evt);
	}
	function DD_dragging(drag, pt, evt) {
		var dropTo;
		if (!evt || (dropTo = evt.domTarget) == drag._lastDropTo)
			return;

		var dropw = zk.DnD.getDrop(drag, pt, evt),
			found = dropw && dropw == drag._lastDrop;
		if (!found) {
			DD_cleanLastDrop(drag); //clean _lastDrop
			if (dropw) {
				drag._lastDrop = dropw;
				dropw.dropEffect_(true);
				found = true;
			}
		}

		var dragImg = drag._dragImg;
		if (dragImg)
			dragImg.className = found ? 'z-drop-allow': 'z-drop-disallow';

		drag._lastDropTo = dropTo; //do it after _cleanLastDrop
	}
	function DD_ghosting(drag, ofs, evt) {
		return drag.control.cloneDrag_(drag, DD_pointer(evt));
	}
	function DD_endghosting(drag, origin) {
		drag.control.uncloneDrag_(drag);
		drag._dragImg = null;
	}
	function DD_constraint(drag, pt, evt) {
		return DD_pointer(evt);
	}
	function DD_ignoredrag(drag, pt, evt) {
		return drag.control.ignoreDrag_(pt);
	}

	function _topnode(n) {
		for (var v; n && n != document.body; n = n.parentNode) //no need to check vparentNode
			if ((v=n.style) && ((v=v.position) == 'absolute' || v == 'relative'))
				return n;
	}
	function _zIndex(n) {
		return n ? zk.parseInt(n.style.zIndex): 0;
	}

	function _getFirstNodeDown(wgt) {
		var n = wgt.$n();
		if (n) return n;
		for (var w = wgt.firstChild; w; w = w.nextSibling) {
			n = w.getFirstNode_();
			if (n) return n;
		}
	}
	//Returns if the specified widget's visibility depends the self widget.
	function _floatVisibleDependent(self, wgt) {
		for (; wgt; wgt = wgt.parent)
			if (wgt == self) return true;
			else if (!wgt.isVisible()) break;
		return false;
	}

	//Returns the topmost z-index for this widget
	function _topZIndex(wgt) {
		var zi = 1800; // we have to start from 1800 depended on all the css files.
		for (var j = _floatings.length; j--;) {
			var w = _floatings[j].widget,
				wzi = zk.parseInt(w.getFloatZIndex_(_floatings[j].node));
			if (wzi >= zi && !zUtl.isAncestor(wgt, w) && w.isVisible())
				zi = wzi + 1;
		}
		return zi;
	}

	function _prepareRemove(wgt, ary) {
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling) {
			var n = wgt.$n();
			if (n) ary.push(n);
			else _prepareRemove(wgt, ary);
		}
	}

	//render the render defer
	function _rdrender(wgt) {
		if (wgt._z$rd) { //might be redrawn by forcerender
			delete wgt._z$rd;
			wgt._norenderdefer = true;
			wgt.replaceHTML('#' + wgt.uuid, wgt.parent ? wgt.parent.desktop: null);
		}
	}

	var _dragoptions = {
		starteffect: zk.$void, //see bug #1886342
		endeffect: DD_enddrag, change: DD_dragging,
		ghosting: DD_ghosting, endghosting: DD_endghosting,
		constraint: DD_constraint,
		ignoredrag: DD_ignoredrag,
		zIndex: 88800
	};
/** A widget, i.e., an UI object.
 * Each component running at the server is associated with a widget
 * running at the client.
 * Refer to <a href="http://docs.zkoss.org/wiki/ZK5:_Component_Development_Guide">Component Development Guide</a>
 * for more information.
 * <p>Notice that, unlike the component at the server, {@link zk.Desktop}
 * and {@link zk.Page} are derived from zk.Widget. It means desktops, pages and widgets are in a widget tree. 
 * @disable(zkgwt)
 */
zk.Widget = zk.$extends(zk.Object, {
	_visible: true,
	/** The number of children (readonly).
	 * @type int
	 */
	nChildren: 0,
	/** The bind level (readonly)
	 * The level in the widget tree after this widget is bound to a DOM tree ({@link #bind_}).
	 * For example, a widget's bind level is one plus the parent widget's
	 * <p>It starts at 0 if it is the root of the widget tree (a desktop, zk.Desktop), then 1 if a child of the root widget, and son on. Notice that it is -1 if not bound.
	 * <p>It is mainly useful if you want to maintain a list that parent widgets is in front of (or after) child widgets. 
	 * bind level.
	 * @type int
	 */
	bindLevel: -1,
	_mold: 'default',
	/** The class name of the widget.
	 * For example, zk.Widget's class name is "zk.Widget", while
	 * zul.wnd.Window's "zul.wnd.Window".
	 * <p>Notice that it is available if a widget class is loaded by WPD loader (i.e., specified in zk.wpd). If you create a widget class dynamically,
	 * you have to invoke {@link #register} to make this member available. 
	 * On the other hand, {@link zk.Object#$class} is available for all objects
	 * extending from {@link zk.Object}.
	 * 
	 * @see #widgetName
	 * @type String
	 */
	className: 'zk.Widget',
	/** The widget name of the widget.
	 * It is the same as <code>this.className.substring(this.className.lastIndexOf('.') + 1).toLowerCase()</code>.
	 * For example, if {@link #className} is zul.wnd.Window, then
	 * {@link #widgetName} is window.
	 * <p>Notice that {@link #className} is unique while {@link #widgetName}
	 * is not necessary unique.
	 * @see #className
	 * @type String
	 * @since 5.0.2
	 */
	widgetName: "widget",

	_floating: false,

	/** The first child, or null if no child at all (readonly).
	 * @see #getChildAt
	 * @type zk.Widget
	 */
	//firstChild: null,
	/** The last child, or null if no child at all (readonly).
	 * @see #getChildAt
	 * @type zk.Widget
	 */
	//lastChild: null,
	/** The parent, or null if this widget has no parent (readonly).
	 * @type zk.Widget
	 */
	//parent: null,
	/** The next sibling, or null if this widget is the last child (readonly).
	 * @type zk.Widget
	 */
	//nextSibling: null,
	/** The previous sibling, or null if this widget is the first child (readonly).
	 * @type zk.Widget
	 */
	//previousSibling: null,
	/** The desktop that this widget belongs to (readonly).
	 * It is set when it is bound to the DOM tree.
	 * <p>Notice it is always non-null if bound to the DOM tree, while
	 * {@link #$n()} is always non-null if bound. For example, {@link zul.utl.Timer}.
	 * <p>It is readonly, and set automcatically when {@link #bind_} is called. 
	 * @type zk.Desktop
	 */
	//desktop: null,
	/** The identifier of this widget, or null if not assigned (readonly).
	 * It is the same as {@link #getId}.
	 * <p>To change the value, use {@link #setId}.
	 * @type String the ID
	 */
	//id: null,
	/** Whether this widget has a peer component (readonly).
	 * It is set if a widget is created automatically to represent a component
	 ( at the server. On the other hand, it is false if a widget is created
	 * by the client application (by calling, say, <code>new zul.inp.Textox()</code>). 
	 * @type boolean
	 */
	//inServer: false,
	/** The UUID. Don't change it if it is bound to the DOM tree, or {@link #inServer} is true.
	 * Developers rarely need to modify it since it is generated automatically. 
	 * <h3>Note of ZK Light</h3>
	 * It is the same as {@link #id} if {@link _global_.zk#spaceless} is true,
	 * such as ZK Light.
	 * @type String
	 */
	//uuid: null,
	/** Indicates an invocation of {@link #appendChild} is made by
	 * {@link #insertBefore}.
	 * @type boolean
	 */
	//insertingBefore_: false,

	/** A map of objects that are associated with this widget, and
	 * they shall be removed when this widget is unbound ({@link #unbind}).
	 * <p>The key must be an unique name of the object, while the value
	 * must be an object that implement the destroy method.
	 * <p>When {@link #unbind_} is called, <code>destroy()</code> is
	 * called for each object stored in this map. Furthermore,
	 * if the visibility of this widget is changed, and the object implements
	 * the sync method, then <code>sync()</code> will be called.
	 * Notice that the sync method is optional. It is ignored if not implemented.
	 * <p>It is useful if you implement an effect, such as shadow, mask
	 * and error message, that is tightly associated with a widget.
	 * @type Map
	 */
	//effects_: null;

	/** The weave controller that is used by ZK Weaver.
	 * It is not null if it is created and controlled by ZK Weaver.
	 * In other words, it is called in the Design Mode if $weave is not null.
	 * @type Object
	 */
	//$weave: null,

	/** The constructor.
	 * For example,
<pre><code>
new zul.wnd.Window{
  border: 'normal',
  title: 'Hello World',
  closable: true
});
</code></pre>
	 * @param Map props the properties to be assigned to this widget.
	 */
	$init: function (props) {
		this._asaps = {}; //event listened at server
		this._lsns = {}; //listeners(evtnm,listener)
		this._bklsns = {}; //backup for listners by setListeners
		this._subnodes = {}; //store sub nodes for widget(domId, domNode)
		this.effects_ = {};

		this.afterInit(function () {
			if (props) {
				var mold = props.mold;
				if (mold != null) {
					if (mold) this._mold = mold;
					delete props.mold; //avoid setMold being called
				}
				for (var nm in props)
					this.set(nm, props[nm]);
			}

			if (zk.spaceless || this.rawId) {
				if (this.id) this.uuid = this.id; //setId was called
				else if (this.uuid) this.id = this.uuid;
				else this.uuid = this.id = zk.Widget.nextUuid();
			} else if (!this.uuid)
				this.uuid = zk.Widget.nextUuid();
		});
	},

	$define: {
		/** Sets this widget's mold. A mold is a template to render a widget.
		 * In other words, a mold represents a visual presentation of a widget. Depending on implementation, a widget can have multiple molds.
		 * <p>Default: <code>default</code>
		 * @param String mold the mold
		 * @return zk.Widget this widget
		 */
		/** Returns this widget's mold. A mold is a template to render a widget.
		 * In other words, a mold represents a visual presentation of a widget. Depending on implementation, a widget can have multiple molds.
		 * @return String
		 */
		mold: function () {
			this.rerender();
		},
		/** Sets the CSS style of this widget.
		 * <p>Default: null
		 * @param String style the CSS style
		 * @return zk.Widget this widget
		 * @see #getStyle
		 * @see #setSclass
		 * @see #setZclass
		 */
		/** Returns the CSS style of this widget
		 * @return String
		 * @see #setStyle
		 * @see #getSclass
		 * @see #getZclass
		 */
		style: function () {
			this.updateDomStyle_();
		},
		/** Sets the CSS class of this widget.
		 *<p>Default: null. 
		 *<p>The default styles of ZK components doesn't depend on sclass at all. Rather, setSclass is provided to perform small adjustment, e.g., changing only the font size. In other words, the default style is still applied if you change sclass.
		 *<p>To replace the default style completely, use {@link #setZclass} instead.
		 *<p>The real CSS class is a concatenation of {@link #getZclass} and {@link #getSclass}.
		 * @param String sclass the style class
		 * @return zk.Widget this widget
		 * @see #getSclass
		 * @see #setZclass
		 * @see #setStyle
		 */
		/** Returns the CSS class of this widget.
		 * @return String
		 * @see #setSclass
		 * @see #getZclass
		 * @see #getStyle
		 */
		sclass: function () {
			this.updateDomClass_();
		},
		/** Sets the ZK Cascading Style class(es) for this widget. It is the CSS class used to implement a mold of this widget. n implementation It usually depends on the implementation of the mold (@{link #getMold}).
		 * <p>Default: null but an implementation usually provides a default class, such as z-button.
		 * <p>Calling setZclass with a different value will completely replace the default style of a widget.
		 * Once you change it, all default styles are gone.
		 * If you want to perform small adjustments, use {@link #setSclass} instead.
		 * <p>The real CSS class is a concatenation of {@link #getZclass} and
		 * {@link #getSclass}. 
		 * @param String zclass the style class used to apply the whote widget.
		 * @return zk.Widget this widget
		 * @see #getZclass
		 * @see #setSclass
		 * @see #setStyle
		 */
		/** Returns the ZK Cascading Style class(es) for this widget.
		 * @return String
		 * @see #setZclass
		 * @see #getSclass
		 * @see #getStyle
		 */
		zclass: function (){
			this.rerender();
		},
		/** Sets the width of this widget.
		 * @param String width the width. Remember to specify 'px', 'pt' or '%'. 
		 * An empty or null value means "auto"
		 * @return zk.Widget this widget
		 */
		/** Returns the width of this widget.
		 * @return String
		 * @see #getHeight
		 */
		width: function (v) {
			if (!this._nhflex) {
				var n = this.$n();
				if (n) n.style.width = v || '';
			}
		},
		/** Sets the height of this widget.
		 * @param String height the height. Remember to specify 'px', 'pt' or '%'. 
		 * An empty or null value means "auto"
		 * @return zk.Widget this widget
		 */
		/** Returns the height of this widget.
		 * @return String
		 * @see #getWidth
		 */
		height: function (v) {
			if (!this._nvflex) {
				var n = this.$n();
				if (n) n.style.height = v || '';
			}
		},
		/** Sets the left of this widget.
		 * @param String left the left. Remember to specify 'px', 'pt' or '%'. 
		 * An empty or null value means "auto"
		 * @return zk.Widget this widget
		 */
		/** Returns the left of this widget.
		 * @return String
		 * @see #getTop
		 */
		left: function (v) {
			var n = this.$n();
			if (n) n.style.left = v || '';
		},
		/** Sets the top of this widget.
		 * If you want to specify <code>bottom</code>, use {@link #setStyle} instead.
		 * For example, <code>setStyle("bottom: 0px");</code>
		 * @param String top the top. Remember to specify 'px', 'pt' or '%'. 
		 * An empty or null value means "auto"
		 * @return zk.Widget this widget
		 */
		/** Returns the top of this widget.
		 * @return String
		 * @see #getLeft
		 */
		top: function (v) {
			var n = this.$n();
			if (n) n.style.top = v || '';
		},
		/** Sets the tooltip text of this widget.
		 * <p>Default implementation of setTooltiptext: update the title attribute of {@link #$n}
		 * @param String title the tooltip text
		 * @return zk.Widget this widget
		 */
		/** Returns the tooltip text of this widget.
		 * @return String
		 */
		tooltiptext: function (v) {
			var n = this.$n();
			if (n) n.title = v || '';
		},

		/** Sets the identifier of a draggable type for this widget.
		 * <p>Default: null
		 * <p>The simplest way to make a widget draggable is to set this property to "true". To disable it, set this to "false" (or null).
		 * If there are several types of draggable objects, you could assign an identifier for each type of draggable object.
		 * The identifier could be anything but empty and "false". 
		 * @param String draggable "false", null or "" to denote non-draggable; "true" for draggable with anonymous identifier; others for an identifier of draggable. 
		 * @return zk.Widget this widget
		 */
		/** Returns the identifier of a draggable type for this widget, or null if not draggable.
		 * @return String
		 */
		draggable: [
			_zkf = function (v) {
				return v && "false" != v ? v: null;
			},
			function (v) {
				var n = this.$n();
				if (this.desktop)
					if (v) this.initDrag_();
					else this.cleanDrag_();
			}
		],
		/** Sets the identifier, or a list of identifiers of a droppable type for this widget.
		 * <p>Default: null
		 * <p>The simplest way to make a component droppable is to set this attribute to "true". To disable it, set this to "false" (or null).
		 * <p>If there are several types of draggable objects and this widget accepts only some of them, you could assign a list of identifiers that this widget accepts, separated by comma.
		 * <p>For example, if this component accpets dg1 and dg2, then assign "dg1, dg2" to this attribute. 
		 * @param String droppable "false", null or "" to denote not-droppable; "true" for accepting any draggable types; a list of identifiers, separated by comma for identifiers of draggables this widget accept (to be dropped in).
		 * @return zk.Widget this widget
		 */
		/** Returns the identifier, or a list of identifiers of a droppable type for this widget, or null if not droppable.
		 * @return String
		 */
		droppable: [
			_zkf,
			function (v) {
				var dropTypes;
				if (v && v != "true") {
					dropTypes = v.split(',');
					for (var j = dropTypes.length; j--;)
						if (!(dropTypes[j] = dropTypes[j].trim()))
							dropTypes.splice(j, 1);
				}
				this._dropTypes = dropTypes;
			}
		],
		/**
		 * Returns vertical flex hint of this widget.
		 * @see #setVflex 
		 * @return String vertical flex hint of this widget.
		 */
		/**
		 * Sets vertical flexibility hint of this widget. 
		 * <p>The parameter flex is a number in String type indicating how this 
		 * widget's parent container distributes remaining empty space among 
		 * its children widget vertically. Flexible 
		 * widget grow and shrink to fit their given space. Flexible widget with 
		 * larger flex values will be made larger than widget with lower flex 
		 * values, at the ratio determined by all flexible widgets. The actual 
		 * flex value is not relevant unless there are other flexible widget within 
		 * the same parent container. Once the default sizes of widget in a 
		 * parent container are calculated, the remaining space in the parent 
		 * container is divided among the flexible widgets, according to their 
		 * flex ratios.</p>
		 * <p>Specify a flex value of negative value, 0, or "false" has the 
		 * same effect as leaving the flex attribute out entirely. 
		 * Specify a flex value of "true" has the same effect as a flex value of 1.</p>
		 * <p>Special flex hint, <b>"min"</b>, indicates that the minimum space shall be
		 * given to this flexible widget to enclose all of its children widgets.
		 * That is, the flexible widget grow and shrink to fit its children widgets.</p> 
		 * 
		 * @see #setHflex
		 * @see #getVflex 
		 * @param String flex the vertical flex hint.
		 */
		vflex: function(v) {
			this._nvflex = (true === v || 'true' == v) ? 1 : v == 'min' ? -65500 : zk.parseInt(v);
			if (this._nvflex < 0 && v != 'min')
				this._nvflex = 0;
			if (_binds[this.uuid] === this) { //if already bind
				if (!this._nvflex) {
					this.setFlexSize_({height: ''}); //clear the height
					delete this._vflexsz;
					if (!this._nhflex)
						_unlistenFlex(this);
				} else
					_listenFlex(this);
				zWatch.fireDown('onSize', this.parent);
			}
		},
		/**
		 * Sets horizontal flexibility hint of this widget. 
		 * <p>The parameter flex is a number in String type indicating how this 
		 * widget's parent container distributes remaining empty space among 
		 * its children widget horizontally. Flexible 
		 * widget grow and shrink to fit their given space. Flexible widget with 
		 * larger flex values will be made larger than widget with lower flex 
		 * values, at the ratio determined by all flexible widgets. The actual 
		 * flex value is not relevant unless there are other flexible widget 
		 * within the same parent container. Once the default sizes of widget 
		 * in a parent container are calculated, the remaining space in the parent 
		 * container is divided among the flexible widgets, according to their 
		 * flex ratios.</p>
		 * <p>Specify a flex value of negative value, 0, or "false" has the 
		 * same effect as leaving this flex attribute out entirely. 
		 * Specify a flex value of "true" has the same effect as a flex value of 1.</p>
		 * <p>Special flex hint, <b>"min"</b>, indicates that the minimum space shall be
		 * given to this flexible widget to enclose all of its children widgets.
		 * That is, the flexible widget grow and shrink to fit its children widgets.</p> 
		 * 
		 * @param String flex the horizontal flex hint.
		 * @see #setVflex
		 * @see #getHflex 
		 */
		/**
		 * Return horizontal flex hint of this widget.
		 * @return String horizontal flex hint of this widget.
		 * @see #setHflex 
		 */
		hflex: function(v) {
			this._nhflex = (true === v || 'true' == v) ? 1 : v == 'min' ? -65500 : zk.parseInt(v);
			if (this._nhflex < 0 && v != 'min')
				this._nhflex = 0; 
			if (_binds[this.uuid] === this) { //if already bind
				if (!this._nhflex) {
					this.setFlexSize_({width: ''}); //clear the width
					delete this._hflexsz;
					if (!this._nvflex)
						_unlistenFlex(this);
				} else
					_listenFlex(this);
				zWatch.fireDown('onSize', this.parent);
			}
		},
		/** Returns the number of milliseconds before rendering this component
		 * at the client.
		 * <p>Default: -1 (don't wait).
		 * @return int the number of milliseconds to wait
		 * @since 5.0.2
		 */
		/** Sets the number of milliseconds before rendering this component
		 * at the client.
		 * <p>Default: -1 (don't wait).
		 *
		 * <p>This method is useful if you have a sophiscated page that takes
		 * long to render at a slow client. You can specify a non-negative value
		 * as the render-defer delay such that the other part of the UI can appear
		 * earlier. The styling of the render-deferred widget is controlled by
		 * a CSS class called <code>z-render-defer</code>.
		 *
		 * <p>Notice that it has no effect if the component has been rendered
		 * at the client.
		 * @param int ms time to wait in milliseconds before rendering.
		 * Notice: 0 also implies deferring the rendering (just right after
		 * all others are renderred).
		 * @since 5.0.2
		 */
		 renderdefer: null
	},
	/** Returns the owner of the ID space that this widget belongs to,
	 * or null if it doesn't belong to any ID space.
	 * <p>Notice that, if this widget is an ID space owner, this method
	 * returns itself.
	 * @return zk.Widget
	 */
	$o: function () {
		for (var w = this; w; w = w.parent)
			if (w._fellows) return w;
	},
	/** Returns the map of all fellows of this widget.
	 * <pre><code>
wgt.$f().main.setTitle("foo");
</code></pre>
	 * @return Map the map of all fellows.
	 * @since 5.0.2
	 */
	/** Returns the fellow of the specified ID of the ID space that this widget belongs to. It returns null if not found. 
	 * @param String id the widget's ID ({@link #id})
	 * @return zk.Widget
	 */
	/** Returns the fellow of the specified ID of the ID space that this widget belongs to. It returns null if not found. 
	 * @param String id the widget's ID ({@link #id})
	 * @param boolean global whether to search all ID spaces of this desktop.
	 * If true, it first search its own ID space, and then the other Id spaces in this browser window (might have one or multiple desktops). 
	 * If omitted, it won't search all ID spaces.
	 * @return zk.Widget
	 */
	$f: function (id, global) {
		var f = this.$o();
		if (!arguments.length)
			return f ? f._fellows: {};
		for (var ids = id.split('/'), j = 0, len = ids.length; j < len; ++j) {
			id = ids[j];
			if (id) {
				if (f) f = f._fellows[id];
				if (!f && global && (f=_globals[id])) f = f[0];
				if (!f || zk.spaceless) break;
				global = false;
			}
		}
		return f;
	},
	/** Returns the identifier of this widget, or null if not assigned.
	 * It is the same as {@link #id}.
	 * @return String the ID
	 */
	getId: function () {
		return this.id;
	},
	/** Sets the identifier of this widget.
	 * @param String id the identifier to assigned to.
	 * @return zk.Widget this widget
	 */
	setId: function (id) {
		if (!id && this.rawId)
			id = this.uuid;

		if (id != this.id) {
			if (this.id) {
				_rmIdSpace(this);
				_rmGlobal(this);
			}

			if (zk.spaceless || this.rawId) {
				var n = this.$n();
				if (n) {
					//Note: we assume RawId doesn't have sub-nodes
					if (!this.rawId)
						throw 'id immutable after bound'; //might have subnodes
					n.id = id;
					delete _binds[this.uuid];
					_binds[id] = this;
					this.clearCache();
				}
				this.uuid = id;
			}
			this.id = id;

			if (id) {
				_addIdSpace(this);
				if (this.desktop)
					_addGlobal(this);
			}
		}
		return this;
	},

	/** Sets a property.
	 * @param String name the name of property.
	 * If the name starts with <code>on</code>, it is assumed to be
	 * an event listener and {@link #setListener} will be called.
	 * @param Object value the value
	 * @return zk.Widget this widget
	 */
	/** Sets a property.
	 * @param String name the name of property.
	 * If the name starts with <code>$on</code>, the value is assumed to
	 * be a boolean indicating if the server registers a listener.
	 * If the name starts with <code>on</code>, the value is assumed to be
	 * an event listener and {@link #setListener} will be called.
	 * @param Object value the value
	 * @param Object extra the extra argument. It could be anything.
	 * @return zk.Widget this widget
	 */
	set: function (name, value, extra) {
		var cc;
		if (name.length > 4 && name.startsWith('$$on')) {
			var cls = this.$class,
				ime = cls._importantEvts;
			(ime || (cls._importantEvts = {}))[name.substring(2)] = value;
		} else if (name.length > 3 && name.startsWith('$on'))
			this._asaps[name.substring(1)] = value;
		else if (name.length > 2 && name.startsWith('on')
		&& (cc = name.charAt(2)) >= 'A' && cc <= 'Z')
			this.setListener(name, value);
		else if (arguments.length >= 3)
			zk.set(this, name, value, extra);
		else
			zk.set(this, name, value);
		return this;
	},
	/** Retrieves a value from the specified property.
	 * @param String name the name of property.
	 * @return Object the value of the property
	 * @since 5.0.2
	 */
	get: function (name) {
		return zk.get(this, name);
	},
	/** Return the child widget at the specified index.
	 * <p>Notice this method is not good if there are a lot of children
	 * since it iterates all children one by one.
	 * @param int j the index of the child widget to return. 0 means the first
	 * child, 1 for the second and so on.
	 * @return zk.Widget the widget or null if no such index
	 * @see #getChildIndex
	 */
	getChildAt: function (j) {
		if (j >= 0 && j < this.nChildren)
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (--j < 0)
					return w;
	},
	/** Returns the child index of this widget.
	 * By child index we mean the order of the child list of the parent. For example, if this widget is the parent's first child, then 0 is returned. 
	 * <p>Notice that {@link #getChildAt} is called against the parent, while
	 * this method called against the child. In other words,
	 * <code>w.parent.getChildAt(w.getChildIndex())</code> returns <code>w</code>.
	 * <p>Notice this method is not good if there are a lot of children
	 * since it iterates all children one by one.
	 * @return int the child index
	 */
	getChildIndex: function () {
		var w = this.parent, j = 0;
		if (w)
			for (w = w.firstChild; w; w = w.nextSibling, ++j)
				if (w == this)
					return j;
		return 0;
	},
	/** Appends an array of children.
	 * Notice this method does NOT remove any existent child widget.
	 * @param Array children an array of children ({@link zk.Widget}) to add
	 * @return zk.Widget this widget
	 */
	setChildren: function (children) {
		if (children)
			for (var j = 0, l = children.length; j < l;)
				this.appendChild(children[j++]);
		return this;
	},
	/** Append a child widget.
	 * The child widget will be attached to the DOM tree automatically,
	 * if this widget has been attached to the DOM tree,
	 * unless this widget is {@link zk.Desktop}.
	 * In other words, you have to attach child widgets of {@link zk.Desktop}
	 * manually (by use of, say, {@link #replaceHTML}).
	 *
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>If this widget is bound to the DOM tree, this method invoke {@link #insertChildHTML_}
	 * to insert the DOM content of the child to the DOM tree.
	 * Thus, override {@link #insertChildHTML_} if you want to insert more than
	 * the DOM content generated by {@link #redraw}.</li>
	 * <li>If a widget wants to do something when the parent is changed, overrides {@link #beforeParentChanged_} 
	 * (which is called by {@link #insertBefore}, {@link #removeChild} and {@link #appendChild}).</li>
	 * <li>{@link #insertBefore} might invoke this method (if the widget shall be the last child).
	 * To know if it is the case you can check {@link #insertingBefore_}.</li>
	 * </ul>
	 * @param zk.Widget child the child widget to add
	 * @return boolean whether the widget was added successfully. It returns false if the child is always the last child ({@link #lastChild}).
	 * @see #insertBefore(zk.Widget,zk.Widget)
	 */
	/** Append a child widget with more control.
	 * It is similar to {@link #appendChild(zk.Widget)} except the caller
	 * could prevent it from generating DOM element.
	 * It is usually used with {@link #rerender}.
	 * @param zk.Widget child the child widget to add
	 * @param boolean ignoreDom whether not to generate DOM elements
	 * @return boolean whether the widget was added successfully. It returns false if the child is always the last child ({@link #lastChild}).
	 * @see #appendChild(zk.Widget)
	 * @see #insertBefore(zk.Widget,zk.Widget,boolean)
	 */
	appendChild: function (child, ignoreDom) {
		if (child == this.lastChild)
			return false;

		var oldpt = child.parent;
		if (oldpt != this)
			child.beforeParentChanged_(this);

		if (oldpt)
			oldpt.removeChild(child);

		child.parent = this;
		var ref = this.lastChild;
		if (ref) {
			ref.nextSibling = child;
			child.previousSibling = ref;
			this.lastChild = child;
		} else {
			this.firstChild = this.lastChild = child;
		}
		++this.nChildren;

		_addIdSpaceDown(child);

		if (!ignoreDom)
			if (this.shallChildROD_(child))
				_bindrod(child);
			else {
				var dt = this.desktop;
				if (dt) this.insertChildHTML_(child, null, dt);
			}

		if (!_noChildCallback)
			this.onChildAdded_(child);
		return true;
	},
	/** Returns whether a new child shall be ROD.
	 * <p>Default: return true if child.z_rod or this.z_rod
	 * @return boolean whether a new child shall be ROD.
	 * @since 5.0.1
	 */
	shallChildROD_: function (child) {
		return child.z_rod || this.z_rod;
	},
	/** Inserts a child widget before the reference widget (the <code>sibling</code> argument).
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>If this widget is bound to the DOM tree, this method invoke {@link #insertChildHTML_}
	 * to insert the DOM content of the child to the DOM tree. Thus, override {@link #insertChildHTML_}
	 * if you want to insert more than the DOM content generated by {@link #redraw}.</li>
	 * <li>If a widget wants to do something when the parent is changed,
	 * overrides {@link #beforeParentChanged_} (which is called by
	 * {@link #insertBefore}, {@link #removeChild} and {@link #appendChild}). 
	 *
	 * @param zk.Widget child the child widget
	 * @param zk.Widget sibling the sibling widget (the 'insert' point where
	 * the new widget will be placed before). If null or omitted, it is
	 * the same as {@link #appendChild}
	 * @return boolean whether the widget was added successfully. It returns false if the child is always the last child ({@link #lastChild}). 
	 * @see #appendChild(zk.Widget)
	 */
	/** Insert a child widget with more control.
	 * It is similar to {@link #insertBefore(zk.Widget,zk.Widget)} except the caller
	 * could prevent it from generating DOM element.
	 * It is usually used with {@link #rerender}.
	 * @param zk.Widget child the child widget
	 * @param zk.Widget sibling the sibling widget (the 'insert' point where
	 * the new widget will be placed before). If null or omitted, it is
	 * the same as {@link #appendChild}
	 * @param boolean ignoreDom whether not to generate DOM elements
	 * @return boolean whether the widget was added successfully. It returns false if the child is always the last child ({@link #lastChild}). 
	 * @see #appendChild(zk.Widget,boolean)
	 */
	insertBefore: function (child, sibling, ignoreDom) {
		if (!sibling || sibling.parent != this) {
			this.insertingBefore_ = true;
			try {
				return this.appendChild(child, ignoreDom);
			} finally {
				this.insertingBefore_ = false;
			}
		}

		if (child == sibling || child.nextSibling == sibling)
			return false;

		if (child.parent != this)
			child.beforeParentChanged_(this);

		if (child.parent)
			child.parent.removeChild(child);

		child.parent = this;
		var ref = sibling.previousSibling;
		if (ref) {
			child.previousSibling = ref;
			ref.nextSibling = child;
		} else this.firstChild = child;

		sibling.previousSibling = child;
		child.nextSibling = sibling;

		++this.nChildren;

		_addIdSpaceDown(child);

		if (!ignoreDom)
			if (this.shallChildROD_(child))
				_bindrod(child);
			else {
				var dt = this.desktop;
				if (dt) this.insertChildHTML_(child, sibling, dt);
			}

		if (!_noChildCallback)
			this.onChildAdded_(child);
		return true;
	},
	/** Removes a child.
	 * @param zk.Widget child the child to remove.
	 * @return boolean whether it is removed successfully.
	 * @see #detach
	 * @see #clear
	 */
	/** Removes a child with more control.
	 * It is similar to {@link #removeChild(zk.Widget)} except the caller
	 * could prevent it from removing the DOM element.
	 * @param zk.Widget child the child to remove.
	 * @param boolean ignoreDom whether to remove the DOM element
	 * @return boolean whether it is removed successfully.
	 * @see #detach
	 * @see #clear
	 */
	removeChild: function (child, ignoreDom) {
		if (!child.parent)
			return false;
		if (this != child.parent)
			return false;

		child.beforeParentChanged_(null);

		var p = child.previousSibling, n = child.nextSibling;
		if (p) p.nextSibling = n;
		else this.firstChild = n;
		if (n) n.previousSibling = p;
		else this.lastChild = p;
		child.nextSibling = child.previousSibling = child.parent = null;

		--this.nChildren;

		_rmIdSpaceDown(child);

		if (child.z_rod)
			_unbindrod(child);
		else if (child.desktop)
			this.removeChildHTML_(child, p, ignoreDom);
		if (!_noChildCallback)
			this.onChildRemoved_(child);
		return true;
	},
	/** Removes this widget (from its parent).
	 * If it was attached to a DOM tree, the associated DOM elements will
	 * be removed, too.
	 * @see #removeChild
	 */
	detach: function () {
		if (this.parent) this.parent.removeChild(this);
		else {
			var cf = zk.currentFocus;
			if (cf && zUtl.isAncestor(this, cf))
				zk.currentFocus = null;
			var n = this.$n();
			if (n) {
				this.unbind();
				jq(n).remove();
			}
		}
	},
	/** Removes all children.
	 */
	clear: function () {
		while (this.lastChild)
			this.removeChild(this.lastChild);
	},
	/** Replaces this widget with the specified one.
	 * The parent and siblings of this widget will become the parent
	 * and siblings of the specified one.
	 * <p>Notice that {@link #replaceHTML} is used to replace a DOM element
	 * that usually doesn't not belong to any widget.
	 * And, {@link #replaceWidget} is used to replace the widget, and
	 * it maintains both the widget tree and the DOM tree.
	 * @param zk.Widget newwgt the new widget that will replace this widget.
	 * @see #replaceHTML
	 * @since 5.0.1
	 */
	replaceWidget: function (newwgt) {
		var node = this.$n(),
			p = newwgt.parent = this.parent,
			s = newwgt.previousSibling = this.previousSibling;
		if (s) s.nextSibling = newwgt;
		else if (p) p.firstChild = newwgt;

		s = newwgt.nextSibling = this.nextSibling;
		if (s) s.previousSibling = newwgt;
		else if (p) p.lastChild = newwgt;

		_rmIdSpaceDown(this);
		_addIdSpaceDown(newwgt);

		var cf = zk.currentFocus;
		if (cf && zUtl.isAncestor(this, cf))
			zk.currentFocus = null;

		if (this.z_rod) {
			_unbindrod(this);
			_bindrod(newwgt);
		} else if (this.desktop) {
			if (!newwgt.desktop) newwgt.desktop = this.desktop;
			if (node) newwgt.replaceHTML(node, newwgt.desktop);
			else {
				this.unbind();
				newwgt.bind();
			}

			_fixBindLevel(newwgt, p ? p.bindLevel + 1: 0);
			zWatch.fire('onBindLevelMove', newwgt);
		}

		if (p)
			p.onChildReplaced_(this, newwgt);

		this.parent = this.nextSibling = this.previousSibling = null;
	},
	/** Replaced the child widgets with the specified.
	 * It is usefull if you want to replace a part of children whose
	 * DOM element is a child element of <code>subId</code> (this.$n(subId)).
	 * @param String subId the ID of the cave that contains the child widgets
	 * to replace with.
	 * @param Array wgts an arrray of widgets that will become children of this widget
	 * @param String tagBeg the beginning of HTML tag, such as &tl;tbody&gt;.
	 * Ignored if null.
	 * @param String tagEnd the ending of HTML tag, such as &lt;/tbody&gt;
	 * Ignored if null.
	 * @see zAu#createWidgets
	 */
	replaceCavedChildren_: function (subId, wgts, tagBeg, tagEnd) {
		_noChildCallback = true; //no callback
		try {
			//1. remove (but don't update DOM)
			var cave = this.$n(subId), fc, oldwgts = [];
			for (var w = this.firstChild; w;) {
				var sib = w.nextSibling;
				if (jq.isAncestor(cave, w.$n())) {
					if (!fc || fc == w) fc = sib;
					this.removeChild(w, true); //no dom
					oldwgts.push(w);
				}
				w = sib;
			}

			//2. insert (but don't update DOM)
			for (var j = 0, len = wgts.length; j < len; ++j)
				this.insertBefore(wgts[j], fc, true); //no dom
		} finally {
			_noChildCallback = false;
		}

		if (fc = this.desktop) {
			//3. generate HTML
			var out = [];
			if (tagBeg) out.push(tagBeg);
			for (var j = 0, len = wgts.length; j < len; ++j)
				wgts[j].redraw(out);
			if (tagEnd) out.push(tagEnd);

			//4. update DOM
			jq(cave).html(out.join(''));

			//5. bind
			for (var j = 0, len = wgts.length; j < len; ++j) {
				wgts[j].bind(fc);
				this.onChildReplaced_(oldwgts[j], wgts[j]);
			}
		}
	},

	/** A callback called before the parent is changed.
	 * @param zk.Widget newparent the new parent (null if it is removed)
	 */
	beforeParentChanged_: function (/*newparent*/) {
	},

	/** Returns if this widget is really visible, i.e., all ancestor widget and itself are visible. 
	 * @return boolean
	 * @see #isVisible
	 */
	/** Returns if this widget is really visible, i.e., all ancestor widget and itself are visible. 
	 * @param Map opts [optional] the options. Allowed values:
	 * <ul>
	 * <li>until - specifies the ancestor to search up to. If not specified, this method searches all ancestors. If specified, this method searches only this widget and ancestors up to the specified one.</li>
	 * </ul>
	 * @return boolean
	 * @see #isVisible
	 */
	isRealVisible: function (opts) {
		var dom = opts && opts.dom;
		for (var wgt = this; wgt; wgt = wgt.parent) {
			if (dom) {
				if (!zk(wgt.$n()).isVisible())
					return false;
			} else if (!wgt.isVisible())
				return false;

			//check if it is hidden by parent, such as child of hbox/vbox or border-layout
			var p = wgt.parent, n;
			if (p && p.isVisible() && (p=p.$n()) && (n=wgt.$n()))
				while ((n=zk(n).vparentNode()||n.parentNode) && p != n)
					if ((n.style||{}).display == 'none')
						return false; //hidden by parent

			if (opts && opts.until == wgt)
				break;
		}
		return true;
	},
	/** Returns if this widget is visible
	 * @return boolean
	 * @see #isRealVisible
	 * @see jqzk#isVisible
	 */
	/** Returns if this widget is visible
	 * @param boolean strict whether to check the visibility of the associated
	 * DOM element. If true, this widget and the associated DOM element
	 * must be both visible.
	 * @return boolean
	 * @see #isRealVisible
	 * @see jqzk#isVisible
	 * @see #setVisible
	 */
	isVisible: function (strict) {
		var visible = this._visible;
		if (!strict || !visible)
			return visible;
		var n = this.$n();
		return !n || zk(n).isVisible();
	},
	/** Sets whether this widget is visible.
	 * <h3>Subclass Notes</h3>
	 * <ul>
	 * <li>setVisible invokes the parent's {@link #onChildVisible_}, so you
	 * can override {@link #onChildVisible_} to change the related DOM element.
	 * For example, updating the additional enclosing tags (such as zul.box.Box). </li>
	 * <li>setVisible invokes {@link #setDomVisible_} to change the visibility of a child DOM element, so override it if necessary.</li>
	 * </ul>
	 * @param boolean visible whether to be visible
	 * @return zk.Widget this widget
	 */
	setVisible: function (visible) {
		if (this._visible != visible) {
			this._visible = visible;

			var p = this.parent, ocvCalled;
			if (this.desktop) {
				var parentVisible = !p || p.isRealVisible(),
					node = this.$n(),
					floating = this._floating;

				if (!parentVisible) {
					if (!floating) this.setDomVisible_(node, visible);
				} else if (visible) {
					var zi;
					if (floating)
						this.setZIndex(zi = _topZIndex(this), {fire:true});

					this.setDomVisible_(node, true);

					//from parent to child
					for (var j = 0, fl = _floatings.length; j < fl; ++j) {
						var w = _floatings[j].widget,
							n = _floatings[j].node;
						if (this == w)
							w.setDomVisible_(n, true, {visibility:1});
						else if (_floatVisibleDependent(this, w)) {
							zi = zi >= 0 ? ++zi: _topZIndex(w);
							w.setFloatZIndex_(n, zi);
							w.setDomVisible_(n, true, {visibility:1});
						}
					}

					if (ocvCalled = p) p.onChildVisible_(this);
						//after setDomVisible_ and before onShow (Box depends on it)
					
					this.fire('onShow');
					if (!zk.animating())
						zWatch.fireDown('onShow', this);
				} else {
					this.fire('onHide');
					if (!zk.animating())
						zWatch.fireDown('onHide', this);

					for (var j = _floatings.length, bindLevel = this.bindLevel; j--;) {
						var w = _floatings[j].widget;
						if (bindLevel >= w.bindLevel)
							break; //skip non-descendant (and this)
						if (_floatVisibleDependent(this, w))
							w.setDomVisible_(_floatings[j].node, false, {visibility:1});
					}

					this.setDomVisible_(node, false);
				}
			}
			if (p && !ocvCalled) p.onChildVisible_(this);
				//after setDomVisible_ and after onHide
		}
		return this;
	},
	/** Synchronizes a map of objects that are associated with this widget, and
	 * they shall be resized when the size of this widget is changed.
	 * <p>It is useful to sync the layout, such as shadow, mask
	 * and error message, that is tightly associated with a widget.
	 * @param Map opts the options, or undefined if none of them specified.
	 * Allowed values:<br/>
	 */
	zsync: function () {
		for (var nm in this.effects_) {
			var ef = this.effects_[nm];
			if (ef && ef.sync) ef.sync();
		}
	},
	/** Makes this widget visible.
	 * It is a shortcut of <code>setVisible(true)</code>
	 * @return zk.Widget this widget
	 */
	show: function () {return this.setVisible(true);},
	/** Makes this widget invisible.
	 * It is a shortcut of <code>setVisible(false)</code>
	 * @return zk.Widget this widget
	 */
	hide: function () {return this.setVisible(false);},
	/** Changes the visibility of a child DOM content of this widget.
	 * It is called by {@link #setVisible} to really change the visibility
	 * of the associated DOM elements.
	 * <p>Default: change n.style.display directly. 
	 * @param DOMElement n the element (never null)
	 * @param boolean visible whether to make it visible
	 * @param Map opts [optional] the options.
	 * If omitted, <code>{display:true}</code> is assumed. Allowed value:
	 * <ul>
	 * <li>display - Modify n.style.display</li>
	 * <li>visibility - Modify n.style.visibility</li>
	 * </ul>
	 */
	setDomVisible_: function (n, visible, opts) {
		if (!opts || opts.display)
			n.style.display = visible ? '': 'none';
		if (opts && opts.visibility)
			n.style.visibility = visible ? 'visible': 'hidden';
	},
	/** A callback called after a child has been added to this widget.
	 * <p>Notice: when overriding this method but not
	 * {@link #onChildRemoved_}, {@link #onChildReplaced_}
	 * is usually required to override, too.
	 * @param zk.Widget child the child being added
	 */
	onChildAdded_: function (/*child*/) {
	},
	/** A callback called after a child has been removed to this widget.
	 * <p>Notice: when overriding this method but not
	 * {@link #onChildAdded_}, {@link #onChildReplaced_}
	 * @param zk.Widget child the child being removed
	 */
	onChildRemoved_: function (/*child*/) {
	},
	/** A callback called after a child has been replaced.
	 * Unlike {@link #onChildAdded_} and {@link #onChildRemoved_}, this
	 * method is called only if {@link zk.AuCmd1#outer}.
	 * And if this method is called, neither {@link #onChildAdded_} nor {@link #onChildRemoved_}
	 * will be called.
	 * <p>Default: invoke {@link #onChildRemoved_} and then
	 * {@link #onChildAdded_}.
	 * Furthermore, it sets this.childReplacing_ to true before invoking
	 * {@link #onChildRemoved_} and {@link #onChildAdded_}, so we can optimize
	 * the code (such as rerender only once) by checking its value.
	 * @param zk.Widget oldc the old child (being removed). Note: it might be null.
	 * @param zk.Widget newc the new child (being added). Note: it might be null.
	 */
	onChildReplaced_: function (oldc, newc) {
		this.childReplacing_ = true;
		try {
			if (oldc) this.onChildRemoved_(oldc);
			if (newc) this.onChildAdded_(newc);
		} finally {
			this.childReplacing_ = false;
		}
	},
	/** A callback called after a child's visibility is changed
	 * (i.e., {@link #setVisible} was called).
	 * <p>Notice that this method is called after the _visible property
	 * and the associated DOM element(s) have been changed.
	 * <p>To know if it is becoming visible, you can check {@link #isVisible}
	 * (such as this._visible).
	 * @param zk.Widget child the child whose visiblity is changed
	 */
	onChildVisible_: function () {
	},
	/** Makes this widget as topmost.
	 * <p>If this widget is not floating, this method will look for its ancestors for the first ancestor who is floating. In other words, this method makes the floating containing this widget as topmost.
	 * To make a widget floating, use {@link #setFloating_}.
	 * <p>This method has no effect if it is not bound to the DOM tree, or none of the widget and its ancestors is floating. 
	 * @return int the new value of z-index of the topmost floating window, -1 if this widget and none of its ancestors is floating or not bound to the DOM tree. 
	 * @see #setFloating_
	 */
	setTopmost: function () {
		if (!this.desktop) return -1;

		for (var wgt = this; wgt; wgt = wgt.parent)
			if (wgt._floating) {
				var zi = _topZIndex(wgt);
				for (var j = 0, fl = _floatings.length; j < fl; ++j) { //from child to parent
					var w = _floatings[j].widget,
						n = _floatings[j].node;
					if (wgt == w)
						w.setFloatZIndex_(n, zi); //must be hit before any parent
					else if (zUtl.isAncestor(wgt, w) && w.isVisible())
						w.setFloatZIndex_(n, ++zi);
				}
				return zi;
			}
		return -1;
	},
	/** Sets the z-index for a floating widget.
	 * It is called by {@link #setTopmost} to set the z-index,
	 * and called only if {@link #setFloating_} is ever called.
	 * @param DOMElement node the element whose z-index needs to be set.
	 * It is the value specified in <code>opts.node</code> when {@link #setFloating_}
	 * is called. If not specified, it is the same as {@link #$n}.
	 * @param int zi the z-index to set
	 * @see #setFloating_
	 * @since 5.0.3
	 */
	setFloatZIndex_: function (node, zi) {
		if (node != this.$n()) node.style.zIndex = zi; //only a portion
		else this.setZIndex(zi, {fire:true});
	},
	/** Returns the z-index of a floating widget.
	 * It is called by {@link #setTopmost} to decide the topmost z-index,
	 * and called only if {@link #setFloating_} is ever called.
	 * @param DOMElement node the element whose z-index needs to be set.
	 * It is the value specified in <code>opts.node</code> when {@link #setFloating_}
	 * is called. If not specified, it is the same as {@link #$n}.
	 * @since 5.0.3
	 * @see #setFloating_
	 */
	getFloatZIndex_: function (node) {
		return node != this.$n() ? node.style.zIndex: this._zIndex;
	},
	/** Returns the top widget, which is the first floating ancestor,
	 * or null if no floating ancestor.
	 * @return zk.Widget
	 * @see #isFloating_
	 */
	getTopWidget: function () {
		for (var wgt = this; wgt; wgt = wgt.parent)
			if (wgt._floating)
				return wgt;
	},
	/** Returns if this widget is floating. 
	 * <p>We say a widget is floating if the widget floats on top of others, rather than embed inside the parent. For example, an overlapped window is floating, while an embedded window is not.
	 * @return boolean
	 * @see #setFloating_
	 */
	isFloating_: function () {
		return this._floating;
	},
	/** Sets a status to indicate if this widget is floating.
	 * <p>Notice that it doesn't change the DOM tree. It is caller's job. 
	 * In the other words, the caller have to adjust the style by assiging
	 * <code>position</code> with <code>absolute</code> or <code>relative</code>.
	 * @param boolean floating whther to make it floating
	 * @param Map opts [optional] The options.
	 * @return zk.Widget this widget
	 * @see #isFloating_
	 */
	setFloating_: function (floating, opts) {
		if (this._floating != floating) {
			if (floating) {
				//parent first
				var inf = {widget: this, node: opts && opts.node? opts.node: this.$n()},
					bindLevel = this.bindLevel;
				for (var j = _floatings.length;;) {
					if (--j < 0) {
						_floatings.unshift(inf);
						break;
					}
					if (bindLevel >= _floatings[j].widget.bindLevel) { //parent first
						_floatings.splice(j + 1, 0, inf);
						break;
					}
				}
				this._floating = true;
			} else {
				for (var j = _floatings.length; j--;)
					if (_floatings[j].widget == this)
						_floatings.splice(j, 1);
				this._floating = false;
			}
		}
		return this;
	},

	/** Returns the Z index.
	 * @return int
	 */
	getZIndex: _zkf = function () {
		return this._zIndex;
	},
	getZindex: _zkf,
	/** Sets the Z index.
	 * @param int zIndex the Z index to assign to
	 * @param Map opts if opts.fire is specified, the onZIndex event will be triggered.
	 * @return zk.Widget this widget.
	 */
	setZIndex: _zkf = function (zIndex, opts) {
		if (this._zIndex != zIndex) {
			this._zIndex = zIndex;
			var n = this.$n();
			if (n) {
				n.style.zIndex = zIndex = zIndex >= 0 ? zIndex: '';
				if (opts && opts.fire) this.fire('onZIndex', zIndex, {ignorable: true});
			}
		}
		return this;
	},
	setZindex: _zkf,

	/** Returns the scoll top of the associated DOM element of this widget.
	 * <p>0 is always returned if this widget is not bound to a DOM element yet.
	 * @return int
	 */
	getScrollTop: function () {
		var n = this.$n();
		return n ? n.scrollTop: 0;
	},
	/** Returns the scoll left of the associated DOM element of this widget.
	 * <p>0 is always returned if this widget is not bound to a DOM element yet.
	 * @return int
	 */
	getScrollLeft: function () {
		var n = this.$n();
		return n ? n.scrollLeft: 0;
	},
	/** Sets the scoll top of the associated DOM element of this widget.
	 * <p>This method does nothing if this widget is not bound to a DOM element yet.
	 * @param int the scroll top.
	 * @return zk.Widget this widget.
	 */
	setScrollTop: function (val) {
		var n = this.$n();
		if (n) n.scrollTop = val;
		return this;
	},
	/** Sets the scoll left of the associated DOM element of this widget.
	 * <p>This method does nothing if this widget is not bound to a DOM element yet.
	 * @param int the scroll top.
	 * @return zk.Widget this widget.
	 */
	setScrollLeft: function (val) {
		var n = this.$n();
		if (n) n.scrollLeft = val;
		return this;
	},
	/** Makes this widget visible in the browser window by scrolling ancestors up or down, if necessary.
	 * <p>Default: invoke zk(this).scrollIntoView();
	 * @see jqzk#scrollIntoView
	 * @return zk.Widget this widget
	 */
	scrollIntoView: function () {
		zk(this.$n()).scrollIntoView();
		return this;
	},

	/** Generates the HTML fragment for this widget.
	 * The HTML fragment shall be pushed to out. For example,
<pre><code>
out.push('<div', this.domAttrs_(), '>');
for (var w = this.firstChild; w; w = w.nextSibling)
	w.redraw(out);
out.push('</div>');
</code></pre>
	 * <p>Default: it retrieves the redraw function associated with
	 * the mold ({@link #getMold}) and then invoke it.
	 * The redraw function must have the same signature as this method.
	 * @param Array out an array to output HTML fragments.
	 * Technically it can be anything that has the method called <code>push</code>
	 */
	redraw: function (out) {
		if (!this.deferRedraw_(out)) {
			var s = this.prolog;
			if (s) out.push(s);

			for (var p = this, mold = this._mold; p; p = p.superclass) {
				var f = p.$class.molds[mold];
				if (f) return f.apply(this, arguments);
			}
			throw "mold "+mold+" not found in "+this.className;
		}
	},
	/* Utilities for handling the so-called render defer ({@link #setRenderdefer}).
	 * This method is called automatically by {@link #redraw},
	 * so you only need to use it if you override {@link #redraw}.
	 * <p>A typical usage is as follows.
	 * <pre><code>
redraw: function (out) {
  if (!this.deferRedraw_(out)) {
  	out.push(...); //redraw
  }
}
	 * </code></pre>
	 * @param Array out an array to output the HTML fragments.
	 * @since 5.0.2
	 */
	deferRedraw_: function (out) {
		var delay;
		if ((delay = this._renderdefer) >= 0) {
			if (!this._norenderdefer) {
				this.z_rod = this._z$rd = true;
				out.push('<div', this.domAttrs_({domClass:1}), ' class="z-renderdefer"></div>');
				out = null; //to free memory

				var wgt = this;
				setTimeout(function () {_rdrender(wgt);}, delay);
				return true;
			}
			delete this._norenderdefer;
			delete this.z_rod;
		}
		return false;
	},
	/** Forces the rendering if it is deferred.
	 * A typical way to defer the render is to specify {@link #setRenderdefer}
	 * with a non-negative value. The other example is some widget might be
	 * optimized for the performance by not rendering some or the whole part
	 * of the widget. If the rendering is deferred, the corresponding DOM elements
	 * (@{link #$n}) are not available. If it is important to you, you can
	 * force it to be rendered.
	 * <p>Notice that this method only forces this widget to render. It doesn't
	 * force any of its children. If you want, you have invoke {@link #forcerender}
	 * one-by-one
	 * <p>The derived class shall override this method, if it implements
	 * the render deferring (other than {@link #setRenderdefer}).
	 * @since 5.0.2
	 */
	forcerender: function () {
		_rdrender(this);
	},
	/** Updates the DOM element's CSS class. It is called when the CSS class is changed (e.g., setZclass is called).
	 * <p>Default: it changes the class of {@link #$n}. 
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>Override it if the class has to be copied to DOM elements other than {@link #$n}.</li>
	 * </ul>
	 * @see #updateDomStyle_
	 */
	updateDomClass_: function () {
		if (this.desktop) {
			var n = this.$n();
			if (n) n.className = this.domClass_();
			this.zsync();
		}
	},
	/** Updates the DOM element's style. It is called when the CSS style is changed (e.g., setStyle is called).
	 * <p>Default: it changes the CSS style of {@link #$n}. 
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>Override it if the CSS style has to be copied to DOM elements other than {@link #$n}.</li>
	 * </ul>
	 */
	updateDomStyle_: function () {
		if (this.desktop) {
			var s = jq.parseStyle(this.domStyle_()),
				n = this.$n();
			zk(n).clearStyles().jq.css(s);

			var t = this.getTextNode();
			if (t && t != n)
				zk(t).clearStyles().jq.css(jq.filterTextStyle(s));
			this.zsync();
		}
	},
	/** Returns the DOM element that is used to hold the text, or null
	 * if this widget doesn't show any text.
	 * <p>Default: return null (no text node).
	 * <p>For example, {@link #updateDomStyle_} will change the style
	 * of the text node, if any, to make sure the text is displayed correctly.
	 * @return DOMElement the DOM element.
	 * <p>See also <a href="http://docs.zkoss.org/wiki/How_to_pass_text_styles_to_an_inner_tag">How to pass text styles to an inner tag</a>.
	 * @see #domTextStyleAttr_
	 * @see #updateDomStyle_
	 */
	getTextNode: function () {
	},

	/** Returns the style used for the DOM element of this widget.
	 * <p>Default: a concatenation of style, width, visible and so on. 
	 * @param Map no [options] the style to exclude (i.e., to turn off).
	 * If omitted, it means none (i.e., all included). For example, you don't
	 * want width to generate, call <code>domStyle_({width:1})</code>.
	 * Notice, though a bit counter-intuition, specify 1 (or true) to denote exclusion.
	 * Allowed value (subclass might support more options):<br/>
	 * <ul>
	 * <li>style - exclude {@link #getStyle}</li>
	 * <li>width - exclude {@link #getWidth}</li>
	 * <li>height - exclude {@link #getHeight}</li>
	 * <li>left - exclude {@link #getLeft}</li>
	 * <li>top - exclude {@link #getTop}</li>
	 * <li>zIndex - exclude {@link #getZIndex}</li>
	 * </ul>
	 * @return String the content of the style, such as width:100px;z-index:1; 
	 * @see #domClass_
	 * @see #domAttrs_
	 */
	domStyle_: function (no) {
		var style = '';
		if (!this.isVisible() && (!no || !no.visible))
			style = 'display:none;';
		if (!no || !no.style) {
			var s = this.getStyle(); 
			if (s) {
				style += s;
				if (s.charAt(s.length - 1) != ';') style += ';';
			}
		}
		if (!no || !no.width) {
			var s = this.getWidth();
			if (s) style += 'width:' + s + ';';
		}
		if (!no || !no.height) {
			var s = this.getHeight();
			if (s) style += 'height:' + s + ';';
		}
		if (!no || !no.left) {
			var s = this.getLeft();
			if (s) style += 'left:' + s + ';';
		}
		if (!no || !no.top) {
			var s = this.getTop();
			if (s) style += 'top:' + s + ';';
		}
		if (!no || !no.zIndex) {
			var s = this.getZIndex();
			if (s >= 0) style += 'z-index:' + s + ';';
		}
		return style;
	},
	/** Returns the class name(s) used for the DOM element of this widget.
	 * <p>Default: a concatenation of {@link #getZclass} and {@link #getSclass}. 
	 *
	 * @param Map no [options] the style class to exclude (i.e., to turn off).
	 * If omitted, it means none (i.e., all included). For example, you don't
	 * want sclass to generate, call <code>domClass_({sclass:1})</code>.
	 * Notice, though a bit counter-intuition, specify 1 (or true) to denote exclusion.
	 * Allowed value (subclass might support more options):<br/>
	 * <ul>
	 * <li>sclass - exclude {@link #getSclass}</li>
	 * <li>zclass - exclude {@link #getZclass}</li>
	 * </ul>
	 * @return String the CSS class names, such as <code>z-button foo</code>
	 * @see #domStyle_
	 * @see #domAttrs_
	 */
	domClass_: function (no) {
		var scls = '';
		if (!no || !no.sclass) {
			var s = this.getSclass();
			if (s) scls = s;
		}
		if (!no || !no.zclass) {
			var s = this.getZclass();
			if (s) scls += (scls ? ' ': '') + s;
		}
		return scls;
	},
	/** Returns the HTML attributes that is used to generate DOM element of this widget.
	 * It is usually used to implement a mold ({@link #redraw}):
</pre><code>
function () {
 return '<div' + this.domAttrs_() + '></div>';
}</code></pre>
	 * <p>Default: it generates id, style, class, and tooltiptext.
	 * Notice that it invokes {@link #domClass_} and {@link #domStyle_},
	 * unless they are disabled by the <code>no<code> argument. 
	 *
	 * @param Map no [options] the attributes to exclude (i.e., to turn off).
	 * If omitted, it means none (i.e., all included). For example, you don't
	 * want the style class to generate, call <code>domAttrs_({domClass:1})</code>.
	 * Notice, though a bit counter-intuition, specify 1 (or true) to denote exclusion.
	 * Allowed value (subclass might support more options):<br/>
	 * <ul>
	 * <li>domClass - exclude {@link #domClass_}</li>
	 * <li>domStyle - exclude {@link #domStyle_}</li>
	 * <li>tooltiptext - exclude {@link #getTooltiptext}</li>
	 * </ul>
	 * <p>return the HTML attributes, such as id="z_u7_3" class="z-button"
	 * @return String 
	 */
	domAttrs_: function (no) {
		var html = !no || !no.id ? ' id="' + this.uuid + '"': '';
		if (!no || !no.domStyle) {
			var s = this.domStyle_(no);
			if (s) html += ' style="' + s + '"';
		}
		if (!no || !no.domClass) {
			var s = this.domClass_();
			if (s) html += ' class="' + s + '"';
		}
		if (!no || !no.tooltiptext) {
			var s = this.domTooltiptext_();
			if (s) html += ' title="' + s + '"';
		}
		return html;
	},
	/** Returns the tooltiptext for generating the title attribute of the DOM element.
	 * <p>Default: return {@link #getTooltiptext}.
	 * <p>Deriving class might override this method if the parent widget
	 * is not associated with any DOM element, such as treerow's parent: treeitem.
	 * @return String the tooltiptext
	 * @since 5.0.2
	 */
	domTooltiptext_ : function () {
		return this.getTooltiptext();
	},
	/** Returns the style attribute that contains only the text related CSS styles. For example, it returns style="font-size:12pt;font-weight:bold" if #getStyle is border:none;font-size:12pt;font-weight:bold.
	 * <p>It is usually used with {@link #getTextNode} to
	 * <a href="http://docs.zkoss.org/wiki/How_to_pass_text_styles_to_an_inner_tag">pass text styles to an inner tag</a>. 
	 * @see #getTextNode
	 * @return String the CSS style that are related to text (string).
	 */
	domTextStyleAttr_: function () {
		var s = this.getStyle();
		if (s) {
			s = jq.filterTextStyle(s);
			if (s) s = ' style="' + s + '"';
		}
		return s;
	},

	/** Replaces the specified DOM element with the HTML content generated this widget.
	 * It is the same as <code>jq(n).replaceWith(wgt, desktop, skipper)</code>.
	 * <p>The DOM element to be replaced can be {@link #$n} or any independent DOM element. For example, you can replace a DIV element (and all its descendants) with this widget (and its descendants).
	 * <p>This method is usually used to replace a DOM element with a root widget (though, with care, it is OK for non-root widgets). Non-root widgets usually use {@link #appendChild}
	 *  and {@link #insertBefore} to attach to the DOM tree[1]
	 * <p>If the DOM element doesn't exist, you can use {@link _global_.jq#before} or {@link _global_.jq#after} instead.
	 * <p>Notice that, both {@link #replaceHTML} fires the beforeSize and onSize watch events
	 * (refer to {@link zWatch}).
	 * <p>If skipper is null. It implies the caller has to fire these two events if it specifies a skipper
	 * (that is how {@link #rerender} is implemented).
	 * <h3>Subclass Note</h3>
	 * This method actually forwards the invocation to its parent by invoking
	 * parent's {@link #replaceChildHTML_} to really replace the DOM element.
	 * Thus, override {@link #replaceChildHTML_} if you want to do something special for particular child widgets.
	 *
	 * @param Object n the DOM element ({@link DOMElement}) or anything
	 * {@link #$} allowed.
	 * @param zk.Desktop desktop [optional] the desktop that this widget shall belong to.
	 * If omitted, it is retrieve from the current desktop.
	 * If null, it is decided automatically ( such as the current value of {@link #desktop} or the first desktop)
	 * @param zk.Skipper skipper [optional] it is used only if it is called by {@link #rerender}
	 * @see #replaceWidget
	 * @see _global_.jq#replaceWith
	 * @return zk.Widget
	 */
	replaceHTML: function (n, desktop, skipper) {
		if (!desktop) {
			desktop = this.desktop;
			if (!zk.Desktop._ndt) zk.stateless();
		}

		var cf = zk.currentFocus;
		if (cf && zUtl.isAncestor(this, cf)) {
			zk.currentFocus = null;
		} else
			cf = null;

		var p = this.parent;
		if (p) p.replaceChildHTML_(this, n, desktop, skipper);
		else {
			var oldwgt = zk.Widget.$(n, {strict:true});
			if (oldwgt) oldwgt.unbind(skipper); //unbind first (w/o removal)
			else if (this.z_rod) _unbindrod(this); //possible (if replace directly)
			zjq._setOuter(n, this.redrawHTML_(skipper, true));
			this.bind(desktop, skipper);
		}

		if (!skipper) {
			zWatch.fireDown('beforeSize', this);
			zWatch.fireDown('onSize', this);
		}

		if (cf && cf.desktop && !zk.currentFocus) cf.focus();
		return this;
	},
	/** Returns the HTML fragment of this widget.
	 * @param zk.Skipper skipper the skipper. Ignored if null
	 * @param boolean noprolog whether <i>not</i> to generate the prolog
	 * @return String the HTML fragment
	 */
	redrawHTML_: function (skipper, noprolog) {
		var out = [];
		this.redraw(out, skipper);
		if (noprolog && !this.rawId && this.prolog && out[0] == this.prolog)
			out[0] = '';
			//Don't generate this.prolog if it is the one to re-render;
			//otherwise, prolog will be generated twice if invalidated
			//test: <div> <button onClick="self.invalidate()"/></div>
			//However, always generated if rawId (such as XHTML), since it
			//uses prolog for the enclosing tag
		return out.join('');
	},
	/** Re-renders the DOM element(s) of this widget.
	 * By re-rendering we mean to generate HTML again ({@link #redraw})
	 * and then replace the DOM elements with the new generated HTML code snippet.
	 * <p>It is equivalent to replaceHTML(this.node, null, skipper).
	 * <p>It is usually used to implement a setter of this widget.
	 * For example, if a setter (such as <code>setBorder</code>) has to
	 * modify the visual appearance, it can update the DOM tree directly,
	 * or it can call this method to re-render all DOM elements associated
	 * with is widget and its desendants.
	 * <p>It is coonvenient to synchronize the widget's state with
	 * the DOM tree with this method. However, it shall be avoided
	 * if the HTML code snippet is complex (otherwise, the performance won't be good).
	 * <p>If re-rendering is required, you can improve the performance
	 * by passing an instance of {@link zk.Skipper} that is used to
	 * re-render some or all descendant widgets of this widget.
	 * @param zk.Skipper skipper [optional] skip some portion of this widget
	 * to speed up the re-rendering.
	 * @return zk.Widget this widget.
	 */
	rerender: function (skipper) {
		if (this.desktop) {
			var n = this.$n();
			if (n) {
				var oldrod = this.z$rod;
				this.z$rod = false;
					//to avoid side effect since the caller might look for $n(xx)

				if (skipper) {
					var skipInfo = skipper.skip(this);
					if (skipInfo) {
						this.replaceHTML(n, null, skipper);

						skipper.restore(this, skipInfo);

						zWatch.fireDown('onRestore', this);
							//to notify it is restored from rerender with skipper
						zWatch.fireDown('beforeSize', this);
						zWatch.fireDown('onSize', this);
					}
				} else
					this.replaceHTML(n);

				this.z$rod = oldrod;
			}
		}
		return this;
	},

	/** Replaces the DOM element(s) of the specified child widget.
	 * It is called by {@link #replaceHTML} to give the parent a chance to
	 * do something special for particular child widgets.
	 * @param zk.Widget child the child widget whose DOM content is used to replace the DOM tree
	 * @param DOMElement n the DOM element to be replaced
	 * @param zk.Desktop dt [optional the desktop that this widget shall belong to.
	 * If null, it is decided automatically ( such as the current value of {@link #desktop} or the first desktop)
	 * @param zk.Skipper skipper it is used only if it is called by {@link #rerender}
	 */
	replaceChildHTML_: function (child, n, desktop, skipper) {
		var oldwgt = zk.Widget.$(n, {strict:true});
		if (oldwgt) oldwgt.unbind(skipper); //unbind first (w/o removal)
		else if (this.shallChildROD_(child))
			_unbindrod(child); //possible (e.g., Errorbox: jq().replaceWith)
		zjq._setOuter(n, child.redrawHTML_(skipper, true));
		child.bind(desktop, skipper);
	},
	/** Inserts the HTML content generated by the specified child widget before the reference widget (the before argument).
	 * It is called by {@link #insertBefore} and {@link #appendChild} to handle the DOM tree.
	 * <p>Deriving classes might override this method to modify the HTML content, such as enclosing with TD.
	 * <p>Notice that when inserting the child (without the before argument), this method will call {@link #getCaveNode} to find the location to place the DOM element of the child. More precisely, the node returned by {@link #getCaveNode} is the parent DOM element of the child. The default implementation of {@link #getCaveNode} is to look for a sub-node named uuid$cave. In other words, it tried to place the child inside the so-called cave sub-node, if any.
	 * Otherwise, {@link #$n} is assumed.
	 * @param zk.Widget child the child widget to insert
	 * @param zk.Widget before the child widget as the reference to insert the new child before. If null, the HTML content will be appended as the last child. 
	 * The implementation can use before.getFirstNode_() ({@link #getFirstNode_}) to retrieve the DOM element
	 * @param zk.Desktop desktop
	 * @see #getCaveNode 
	 */
	insertChildHTML_: function (child, before, desktop) {
		var ben;
		if (before)
			before = before.getFirstNode_();
		if (!before)
			for (var w = this;;) {
				ben = w.getCaveNode();
				if (ben) break;

				var w2 = w.nextSibling;
				if (w2 && (before = w2.getFirstNode_()))
					break;

				if (!(w = w.parent)) {
					ben = document.body;
					break;
				}
			}

		if (before) {
			var sib = before.previousSibling;
			if (_isProlog(sib)) before = sib;
			jq(before).before(child.redrawHTML_());
		} else
			jq(ben).append(child.redrawHTML_());
		child.bind(desktop);
	},
	/** Called by {@link #insertChildHTML_} to to find the location to place the DOM element of the child.
	 * More precisely, the node returned by {@link #getCaveNode} is the parent DOM element of the child's DOM element.
	 * <p>Default: <code>this.$n('cave') || this.$n()</code>
	 * You can override it to return whatever DOM element you want. 
	 * @see #insertChildHTML_
	 * @return DOMElement
	 */
	getCaveNode: function () {
		return this.$n('cave') || this.$n();
	},
	/** Returns the first DOM element of this widget.
	 * If this widget has no corresponding DOM element, this method will look
	 * for its siblings.
	 * <p>This method is designed to be used with {@link #insertChildHTML_}
	 * for retrieving the DOM element of the <code>before</code> widget.
	 * @return DOMElement
	 */
	getFirstNode_: function () {
		for (var w = this; w; w = w.nextSibling) {
			var n = _getFirstNodeDown(w);
			if (n) return n;
		}
	},
	/** Removes the corresponding DOM content of the specified child. It is called by #removeChild to remove the DOM content.
	 * <p>The default implementation of this method will invoke {@link #removeHTML_}
	 * if the ignoreDom argument is false or not specified.
	 * <p>Overrides this method or {@link #removeHTML_} if you have to
	 * remove DOM elements other than child's node (and the descendants).
	 * @param zk.Widget child the child widget to remove
	 * @param zk.Widget prevsib the previous sibling, if any
	 * @param boolean ignoreDom whether to remove the DOM element
	 */
	removeChildHTML_: function (child, prevsib, ignoreDom) {
		var cf = zk.currentFocus;
		if (cf && zUtl.isAncestor(child, cf))
			zk.currentFocus = null;

		var n = child.$n();
		if (n) {
			var sib = n.previousSibling;
			if (child.prolog && _isProlog(sib))
				jq(sib).remove();
		} else
			_prepareRemove(child, n = []);

		child.unbind();

		if (!ignoreDom)
			child.removeHTML_(n);
	},
	/**
	 * Removes the HTML DOM content.
	 * <p>The default implementation simply removes the DOM element passed in.
	 * <p>Overrides this method if you have to remove the related DOM elements.
	 * @since 5.0.1
	 * @param Array n an array of {@link DOMElement} to remove.
	 * If this widget is associated with a DOM element ({@link #$n} returns non-null),
	 * n is a single element array.
	 * If this widget is not assoicated with any DOM element, an array of
	 * child widget's DOM elements are returned.
	 */
	removeHTML_: function (n) {
		jq(n).remove();
	},
	/**
	 * Returns the DOM element that this widget is bound to.
	 * It is null if it is not bound to the DOM tree, or it doesn't have the associated DOM node (for example, {@link zul.utl.Timer}).
	 * <p>Notice that {@link #desktop} is always non-null if it is bound to the DOM tree.
	 * In additions, this method is much faster than invoking jq() (see {@link _global_.jq},
	 * since it caches the result (and clean up at the {@link #unbind_}).
	 * <pre><code>var n = wgt.$n();</code></pre>
	 * @return DOMElement
	 * @see #$n(String)
	 */
	/** Returns the child element of the DOM element(s) that this widget is bound to.
	 * This method assumes the ID of the child element the concatenation of
	 * {@link #uuid}, -, and subId. For example,
<pre><code>var cave = wgt.$n('cave'); //the same as jq('#' + wgt.uuid + '-' + 'cave')[0]</code></pre>
	 * Like {@link #$n()}, this method caches the result so the performance is much better
	 * than invoking jq() directly.
	 * @param String subId the sub ID of the child element
	 * @return DOMElement
	 * @see #$n()
	 */
	$n: function (subId) {
		if (subId) {
			var n = this._subnodes[subId];
			if (!n && this.desktop) {
				n = jq(this.uuid + '-' + subId, zk)[0];
				this._subnodes[subId] = n ? n : 'n/a';
			}
			return n == 'n/a' ? null : n;
		}
		var n = this._node;
		if (!n && this.desktop && !this._nodeSolved) {
			this._node = n = jq(this.uuid, zk)[0];
			this._nodeSolved = true;
		}
		return n;
	},
	/** Clears the cached nodes (by {@link #$n}). */
	clearCache: function () {
		this._node = null;
		this._subnodes = {};
		this._nodeSolved = false;
	},
	/** Returns the page that this widget belongs to.
	 * @return zk.Page
	 */
	getPage: function () {
		if (this.desktop && this.desktop.nChildren == 1)
			return this.desktop.firstChild;
			
		for (var page = this.parent; page; page = page.parent)
			if (page.$instanceof(zk.Page))
				return page;
				
		return null;
	},
	/** Binds this widget.
	 * It is called to assoicate (aka., attach) the widget with
	 * the DOM tree.
	 * <p>Notice that you rarely need to invoke this method, since
	 * it is called automatically (such as {@link #replaceHTML}
	 * and {@link #appendChild}).
	 * <p>Notice that you rarely need to override this method, either.
	 * Rather, override {@link #bind_} instead.
	 *
	 * @see #bind_
	 * @see #unbind
	 * @param zk.Desktop dt [optional] the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper.
	 * @return zk.Widget this widget
	 */
	bind: function (desktop, skipper) {
		if (this.z_rod) 
			_bindrod(this);
		else {
			var after = [], fn;
			this.bind_(desktop, skipper, after);
			while (fn = after.shift())
				fn();
		}
		return this;
	},
	/** Unbinds this widget.
	 * It is called to remove the assoication (aka., detach) the widget from
	 * the DOM tree.
	 * <p>Notice that you rarely need to invoke this method, since
	 * it is called automatically (such as {@link #replaceHTML}).
	 * <p>Notice that you rarely need to override this method, either.
	 * Rather, override {@link #unbind_} instead.
	 *
	 * @see #unbind_
	 * @see #bind
	 * @param zk.Desktop dt [optional] the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper.
	 * @return zk.Widget this widget
	 */
	unbind: function (skipper) {
		if (this.z_rod)
			_unbindrod(this);
		else {
			var after = [];
			this.unbind_(skipper, after);
			for (var j = 0, len = after.length; j < len;)
				after[j++]();
		}
		return this;
	},

	/** Callback when this widget is bound (aka., attached) to the DOM tree.
	 * It is called after the DOM tree has been modified (with the DOM content of this widget, i.e., {@link #redraw})
	 * (for example, by {@link #replaceHTML}).
	 * <p>Note: don't invoke this method directly. Rather, invoke {@link #bind} instead.
<pre><code>
wgt.bind();
</code></pre>
	 * <h3>Subclass Note</h3>
	 * <p>Subclass overrides this method to initialize the DOM element(s), such as adding a DOM listener. Refer to Widget and DOM Events and {link #domListen_} for more information. 
	 *
	 * @see #bind
	 * @see #unbind_
	 * @param zk.Desktop dt [optional] the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper.
	 * @param Array after an array of function ({@link Function}) that will be invoked after {@link #bind_} has been called. For example, 
<pre><code>
bind_: function (desktop, skipper, after) {
  this.$super('bind_', arguments);
  var self = this;
  after.push(function () {
    self._doAfterBind(something);
    ...
  });
}
</code></pre>
	 */
	bind_: function (desktop, skipper, after) {
		_bind0(this);

		this.desktop = desktop || (desktop = zk.Desktop.$(this.parent));

		var p = this.parent;
		this.bindLevel = p ? p.bindLevel + 1: 0;

		if (this._draggable) this.initDrag_();
		
		if (this._nvflex || this._nhflex)
			_listenFlex(this);

		for (var child = this.firstChild, nxt; child; child = nxt) {
			nxt = child.nextSibling;
				//we have to store first since RefWidget will replace widget

			if (!skipper || !skipper.skipped(this, child))
				if (child.z_rod) _bindrod(child);
				else child.bind_(desktop, null, after); //don't pass skipper
		}

		if (this.isListen('onBind')) {
			var self = this;
			zk.afterMount(function () {
				if (self.desktop) //might be unbound
					self.fire('onBind');
			});
		}
	},

	/** Callback when a widget is unbound (aka., detached) from the DOM tree.
	 * It is called before the DOM element(s) of this widget is going to be removed from the DOM tree (such as {@link #removeChild}.
	 * <p>Note: don't invoke this method directly. Rather, invoke {@link #unbind} instead. 
	 * @see #bind_
	 * @see #unbind
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper 
	 * @param Array after an array of function ({@link Function})that will be invoked after {@link #unbind_} has been called. For example, 
<pre><code>
unbind_: function (skipper, after) {
  this.$super('unbind_', arguments);
  var self = this;
  after.push(function () {
    self._doAfterUnbind(something);
    ...
  }
}
</code></pre>
	 */
	unbind_: function (skipper, after) {
		_unbind0(this);
		_unlistenFlex(this);

		for (var child = this.firstChild, nxt; child; child = nxt) {
			nxt = child.nextSibling; //just in case

			if (!skipper || !skipper.skipped(this, child))
				if (child.z_rod) _unbindrod(child);
				else child.unbind_(null, after); //don't pass skipper
		}

		if (this._draggable) this.cleanDrag_();

		if (this.isListen('onUnbind')) {
			var self = this;
			zk.afterMount(function () {
				if (!self.desktop) //might be bound
					self.fire('onUnbind');
			});
		}

		for (var nm in this.effects_) {
			var ef = this.effects_[nm];
			if (ef) ef.destroy();
		}
		this.effects_ = {};
	},
	/** Associates UUID with this widget.
	 * <p>Notice that {@link #uuid} is automically associated (aka., bound) to this widget.
	 * Thus, you rarely need to invoke this method unless you want to associate with other identifiers.
	 * <p>For example, ZK Google Maps uses this method since it has to
	 * bind the anchors manually.
	 *
	 * @param String uuid the UUID to assign to the widgtet
	 * @param boolean add whether to bind. Specify true if you want to bind;
	 * false if you want to unbind.
	 */
	extraBind_: function (uuid, add) {
		if (add == false) delete _binds[uuid];
		else _binds[uuid] = this;
	},
	setFlexSize_: function(sz, ignoreMargins) {
		var n = this.$n(),
			zkn = zk(n);
		if (sz.height !== undefined) {
			if (sz.height == 'auto')
				n.style.height = '';
			else if (sz.height != '') { //bug #2943174, #2979776
				var h = zkn.revisedHeight(sz.height, !ignoreMargins),
					newh = h,
					margins = zkn.sumStyles("tb", jq.margins);
				n.style.height = jq.px0(h);
				var newmargins = zkn.sumStyles("tb", jq.margins);
				if (h == jq(n).outerHeight(false)) //border-box
					newh = sz.height - ((zk.safari && newmargins >= 0 && newmargins < margins) ? newmargins : margins);
				else if (zk.safari && newmargins >= 0 && newmargins < margins)  //safari/chrome margin changed after set style.height
					newh = zkn.revisedHeight(sz.height, !ignoreMargins);
				if (newh != h) //h changed, re-assign height
					n.style.height = jq.px0(newh);
			} else
				n.style.height = this._height || '';
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto')
				n.style.width = '';
			else if (sz.width != '') { //bug #2943174, #2979776
				var w = zkn.revisedWidth(sz.width, !ignoreMargins),
					neww = w,
					margins = zkn.sumStyles("lr", jq.margins);
				n.style.width = jq.px0(w);
				var newmargins = zkn.sumStyles("lr", jq.margins);
				if (w == jq(n).outerWidth(false)) //border-box
					neww = sz.width - ((zk.safari && newmargins >= 0 && newmargins < margins) ? newmargins : margins);
				else if (zk.safari && newmargins >= 0 && newmargins < margins) //safari/chrome margin changed after set style.width
					neww = zkn.revisedWidth(sz.width, !ignoreMargins);
				if (neww != w) //w changed, re-assign width
					n.style.width = jq.px0(neww); 
			} else
				n.style.width = this._width || '';
		}
		return {height: n.offsetHeight, width: n.offsetWidth};
	},
	beforeChildrenFlex_: function(kid) {
		//to be overridden
		return true; //return true to continue children flex fixing
	},
	afterChildrenFlex_: function(kid) {
		//to be overridden
	},
	ignoreFlexSize_: function(attr) { //'w' for width or 'h' for height calculation
		//to be overridden, whether ignore widget dimension in vflex/hflex calculation 
		return false;
	},
	ignoreChildNodeOffset_: function(attr) { //'w' for width or 'h' for height calculation
		//to be overridden, whether ignore child node offset in vflex/hflex calculation
		return false;
	},
	afterChildrenMinFlex_: function() {
		//to be overridden
	},
	getParentSize_: function(p) {
		//to be overridden
		var zkp = zk(p);
		return zkp ? {height: zkp.revisedHeight(p.offsetHeight), width: zkp.revisedWidth(p.offsetWidth)} : {};
	},
	fixFlex_: function() {
		_fixFlex.apply(this);
	},
	/** Initializes the widget to make it draggable.
	 * It is called if {@link #getDraggable} is set (and bound).
	 * <p>You rarely need to override this method, unless you want to handle drag-and-drop differently.
	 * <p>Default: use {@link zk.Draggable} to implement drag-and-drop,
	 * and the handle to drag is the element returned by {@link #getDragNode}
	 * @see #cleanDrag_
	 */
	initDrag_: function () {
		this._drag = new zk.Draggable(this, this.getDragNode(), this.getDragOptions_(_dragoptions));
	},
	/** Cleans up the widget to make it un-draggable. It is called if {@link #getDraggable}
	 * is cleaned (or unbound).
	 * <p>You rarely need to override this method, unless you want to handle drag-and-drop differently. 
	 * @see #cleanDrag_
	 */
	cleanDrag_: function () {
		var drag = this._drag;
		if (drag) {
			this._drag = null;
			drag.destroy();
		}
	},
	/** Returns the DOM element of this widget that can be dragged.
	 * <p>Default, it returns {@link #$n}, i.e., the user can drag the widget anywhere.
	 * @return DOMElement
	 * @see #ignoreDrag_
	 */
	getDragNode: function () {
		return this.$n();
	},
	/** Returns the options used to instantiate {@link zk.Draggable}.
	 * <p>Default, it returns nothing (undefined).
	 * <p>Though rarely used, you can override any option passed to
	 * {@link zk.Draggable}, such as the start effect, ghosting and so on.
	 * @param Map map the default implementation 
	 * @return Map
	 */
	getDragOptions_: function (map) {
		return map;
	},
	/** Returns if the location that an user is trying to drag is allowed.
	 * <p>Default: it always returns false.
	 * If the location that an user can drag is static, override {@link #getDragNode},
	 * which is easier to implement.
	 * @param zk.Draggable pt
	 * @return boolean whether to ignore
	 */
	ignoreDrag_: function (pt) {
		return false;
	},
	/** Returns the widget if it allows to drop the specified widget (being dragged), or null if not allowed. It is called when the user is dragging a widget on top a widget.
	 * <p>Default: it check if the values of droppable and draggable match. It will check the parent ({@link #parent}), parent's parent, and so on until matched, or none of them are matched.
	 * <p>Notice that the widget to test if droppable might be the same as the widget being dragged (i.e., this == dragged). By default, we consider them as non-matched.
	 * @param zk.Widget dragged - the widget being dragged (never null). 
	 * @return zk.Widget the widget to drop to.
	 */
	getDrop_: function (dragged) {
		if (this != dragged) {
			var dropType = this._droppable,
				dragType = dragged._draggable;
			if (dropType == 'true') return this;
			if (dropType && dragType != "true")
				for (var dropTypes = this._dropTypes, j = dropTypes.length; j--;)
					if (dragType == dropTypes[j])
						return this;
		}
		return this.parent ? this.parent.getDrop_(dragged): null;
	},
	/** Called to have some visual effect when the user is dragging a widget over this widget and this widget is droppable.
	 * <p>Default, it adds the CSS class named 'z-drag-over' if over is true, and remove it if over is false.
	 * @param boolean over whether the user is dragging over (or out, if false) 
	 */
	dropEffect_: function (over) {
		jq(this.$n()||[])[over ? "addClass" : "removeClass"]("z-drag-over");
	},
	/** Returns the message to show when an user is dragging this widget, or null if it prefers to clone the widget with {@link #cloneDrag_}.
	 * <p>Default, it return the inner text if if {@link #$n} returns a TR, TD, or TH element. Otherwise, it returns null and {@link #cloneDrag_} will be called to create a DOM element to indicate dragging. 
	 * @return String the message to indicate the dragging, or null if clone is required
	 */
	getDragMessage_: function () {
		if (jq.nodeName(this.getDragNode(), "tr", "td", "th")) {
			var n = this.$n('real') || this.getCaveNode();
			return n ? n.textContent || n.innerText || '': '';
		}
	},
	/** Called to fire the onDrop event.
	 * <p>Default, it fires the onDrop event (with {@link #fire}).
	 * The subclass can override this method to pass more options such as the coordination where a widget is dropped. 
	 * @param zk.Draggable drag the draggable controller
	 * @param zk.Event evt the event causes the drop
	 */
	onDrop_: function (drag, evt) {
		var data = zk.copy({dragged: drag.control}, evt.data);
		this.fire('onDrop', data, null, 38);
	},
	/** Clones this widget to create the visual effect representing what is being dragged.
	 * <p>This method is called if {@link #getDragMessage_} returns null.
	 * If {@link #getDragMessage_} returns a string (empty or not),
	 * a small popup containing the message is created to represent the widget being dragged.
	 * <p>You rarely need to override this method, unless you want a different visual effect. 
	 * @see #uncloneDrag_
	 * @param zk.Draggable drag the draggable controller
	 * @param Offset ofs the offset of the returned element (left/top)
	 * @return DOMElement the clone
	 */
	cloneDrag_: function (drag, ofs) {
		//See also bug 1783363 and 1766244

		var msg = this.getDragMessage_();
		if (typeof msg == 'string' && msg.length > 9)
			msg = msg.substring(0, 9) + "...";

		var dgelm = zk.DnD.ghost(drag, ofs, msg);

		drag._orgcursor = document.body.style.cursor;
		document.body.style.cursor = "pointer";
		jq(this.getDragNode()).addClass('z-dragged'); //after clone
		return dgelm;
	},
	/** Undo the visual effect created by {@link #cloneDrag_}.
	 * @param zk.Draggable drag the draggable controller
	 */
	uncloneDrag_: function (drag) {
		document.body.style.cursor = drag._orgcursor || '';

		jq(this.getDragNode()).removeClass('z-dragged');
	},

	/** Sets the focus to this widget.
	 * This method will check if this widget can be activated by invoking {@link #canActivate} first.
	 * <p>Default: call child widget's focus until it returns true, or no child at all. 
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>If a widget is able to gain focus, it shall override this method to invoke {@link _global_.jqzk#focus}.</li>
	 * </ul>
<pre><code>
focus: function (timeout) {
 if (this.isVisible() && this.canActivate({checkOnly:true}))
  zk(this).focus(timeout);
}
</pre></code>
     * @param int timeout how many milliseconds before changing the focus. If not specified or negative, the focus is changed immediately, 
	 * @return boolean whether the focus is gained to this widget. 
	 */
	focus: function (timeout) {
		var node;
		if (this.isVisible() && this.canActivate({checkOnly:true})
		&& (node = this.$n())) {
			if (zk(node).focus(timeout)) {
				this.setTopmost();
				return true;
			}
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (w.isVisible() && w.focus(timeout))
					return true;
		}
		return false;
	},
	/** Checks if this widget can be activated (gaining focus and so on).
	 * <p>Default: return false if it is not a descendant of 
	 * {@link _global_.zk#currentModal}. 
	 * @param Map opts [optional] the options. Allowed values:
	 * <ul>
	 * <li>checkOnly: not to change focus back to modal dialog if unable to
	 * activate. If not specified, the focus will be changed back to
	 * {@link _global_.zk#currentModal}.
	 * In additions, if specified, it will ignore {@link zk#busy}, which is set
	 * if {@link zk.AuCmd0#showBusy} is called.
	 * This flag is usually set by {@link #focus}, and not set
	 * if it is caused by user's activity, such as clicking.</li>
	 * </ul>
	 * The reason to ignore busy is that we allow application to change focus
	 * even if busy, while the user cannot.
	 * @return boolean
	 */
	canActivate: function (opts) {
		if (zk.busy && (!opts || !opts.checkOnly)) { //Bug 2912533: none of widget can be activated if busy
			jq.focusOut(); // Bug 2968706
			return false;
		}

		var modal = zk.currentModal;
		if (modal && !zUtl.isAncestor(modal, this)) {
			if (!opts || !opts.checkOnly) {
				var cf = zk.currentFocus;
				//Note: browser might change focus later, so delay a bit
				if (cf && zUtl.isAncestor(modal, cf)) cf.focus(0);
				else modal.focus(0);
			}
			return false;
		}
		return true;
	},

	//server comm//
	/** Smart-updates a property of the peer component associated with this widget, running at the server, with the specified value.
	 * <p>It is actually fired an AU requst named <code>setAttr</code>, and
	 * it is handled by the <code>updateByClient</code> method in <code>org.zkoss.zk.ui.AbstractComponent</code> (at the server).
	 * @param String name the property name
     * @param Object value the property value
     * @param int timeout the delay before sending out the AU request. It is optional. If omitted, -1 is assumed (i.e., it will be sent with next non-deferrable request). 
	 * @see zAu#send
	 * @return zk.Widget
	 */
	smartUpdate: function (nm, val, timeout) {
		zAu.send(new zk.Event(this, 'setAttr', [nm, val]),
			timeout >= 0 ? timeout: -1);
		return this;
	},

	//widget event//
	/** Fire a widget event.
	 * @param zk.Event evt the event to fire
	 * @param int timeout the delay before sending the non-deferrable AU request (if necessary).
	 * If not specified or negative, it is decided automatically.
	 * It is ignored if no non-deferrable listener is registered at the server. 
	 * @return zk.Event the event being fired, i.e., evt. 
	 * @see #fire
	 * @see #listen
	 */
	fireX: function (evt, timeout) {
		var oldtg = evt.currentTarget;
		evt.currentTarget = this;
		try {
			var evtnm = evt.name,
				lsns = this._lsns[evtnm],
				len = lsns ? lsns.length: 0;
			if (len) {
				for (var j = 0; j < len;) {
					var inf = lsns[j++], o = inf[0];
					(inf[1] || o[evtnm]).call(o, evt);
					if (evt.stopped) return evt; //no more processing
				}
			}

			if (!evt.auStopped) {
				var toServer = evt.opts && evt.opts.toServer;
				if (toServer || (this.inServer && this.desktop)) {
					if (evt.opts.sendAhead) {
						this.sendAU_(evt, timeout >= 0 ? timeout : 38);
					} else {
						var asap = toServer || this._asaps[evtnm];
						if (asap == null) {
							var ime = this.$class._importantEvts;
							if (ime) {
								var ime = ime[evtnm];
								if (ime != null) 
									asap = ime;
							}
						}
						if (asap != null) //true or false
							this.sendAU_(evt, asap ? timeout >= 0 ? timeout : 38 : -1);
					}
				}
			}
			return evt;
		} finally {
			evt.currentTarget = oldtg;
		}
	},
	/** Callback before sending an AU request.
	 * It is called by {@link #sendAU_}.
	 * <p>Default: this method will stop the event propagation
	 * and prevent the browser's default handling
	 * (by calling {@link zk.Event#stop}), 
	 * if the event is onClick, onRightClick or onDoubleClick.
	 * <p>Notice that {@link #sendAU_} is called against the widget sending the AU request
	 * to the server, while {@link #beforeSendAU_} is called against the event's
	 * target (evt.target).
	 *
	 * <p>Notice that since this method will stop the event propagation for onClick,
	 * onRightClick and onDoubleClick, it means the event propagation is stopped
	 * if the server registers a listener. However, it doesn't stop if
	 * only a client listener is registered (and, in this case, {@link zk.Event#stop}
	 * must be called explicitly if you want to stop).
	 *
	 * @param zk.Widget wgt the widget that causes the AU request to be sent.
	 * It will be the target widget when the server receives the event.
	 * @param zk.Event evt the event to be sent back to the server.
	 * Its content will be cloned to the AU request.
	 * @see #sendAU_
	 * @since 5.0.2
	 */
	beforeSendAU_: function (wgt, evt) {
		var en = evt.name;
		if (en == 'onClick' || en == 'onRightClick' || en == 'onDoubleClick')
			evt.shallStop = true;//Bug: 2975748: popup won't work when component with onClick handler
	},
	/** Sends an AU request to the server.
	 * It is invoked when {@link #fire} will send an AU request to the server.
	 *
	 * <p>Override Notice: {@link #sendAU_} will call evt.target's
	 * {@link #beforeSendAU_} to give the original target a chance to
	 * process it.
	 *
	 * @param zk.Event the event that will be sent to the server.
	 * @param int timeout the delay before really sending out the AU request
	 * @see #fire
	 * @see #beforeSendAU_
	 * @see zAu#sendAhead
	 * @since 5.0.1
	 */
	sendAU_: function (evt, timeout, opts) {
		(evt.target||this).beforeSendAU_(this, evt);
		evt = new zk.Event(this, evt.name, evt.data, evt.opts, evt.domEvent);
			//since evt will be used later, we have to make a copy and use this as target
		if (evt.opts.sendAhead) zAu.sendAhead(evt, timeout);
		else zAu.send(evt, timeout);
	},
	/** Check whether to ignore the click which might be caused by
	 * {@link #doClick_}
	 * {@link #doRightClick_}, or {@link #doDoubleClick_}.
	 * <p>Default: return false.
	 * <p>Deriving class might override this method to return true if
	 * it wants to ignore the click on certain DOM elements, such as
	 * the open icon of a treerow.
	 * <p>Notice: if true is returned, {@link #doClick_}
	 * {@link #doRightClick_}, and {@link #doDoubleClick_} won't be called.
	 * In additions, the popup and context of {@link zul.Widget} won't be
	 * handled, either.
	 * @param zk.Event the event that causes the click ({@link #doClick_}
	 * {@link #doRightClick_}, or {@link #doDoubleClick_}).
	 * @return boolean whether to ignore it
	 * @since 5.0.1
	 */
	shallIgnoreClick_: function (evt) {
	},

	/** Fire a widget event. An instance of {@link zk.Event} is created to represent the event.
	 *
	 * <p>The event listeners for this event will be called one-by-one unless {@link zk.Event#stop} is called.
	 *
	 * <p>If the event propagation is not stopped (i.e., {@link zk.Event#stop} not called)
	 * and {@link #inServer} is true, the event will be converted to an AU request and sent to the server.
	 Refer to <a href="http://docs.zkoss.org/wiki/Notify_Server">Notify Server</a> for more information.
	 * @param String evtnm the event name, such as onClick
	 * @param Object data [optional] the data depending on the event.
	 * Here is a list of <a href="http://docs.zkoss.org/wiki/CDG5:_Event_Data">Event Data</a>
	 * @param Map opts [optional] the options. Refer to {@link zk.Event#opts}
	 * @param int timeout the delay before sending the non-deferrable AU request (if necessary).
	 * If not specified or negative, it is decided automatically.
	 * It is ignored if no non-deferrable listener is registered at the server. 
	 * @return zk.Event the event being fired. 
	 * @see #fire
	 * @see #listen
	 */
	fire: function (evtnm, data, opts, timeout) {
		return this.fireX(new zk.Event(this, evtnm, data, opts), timeout);
	},
	/** Registers listener(s) to the specified event. For example,
<pre><code>
wgt.listen({
  onClick: wgt,
  onOpen: wgt._onOpen,
  onMove: [o, o._onMove]
});
</code></pre>
	 * <p>As shown above, you can register multiple listeners at the same time, and echo value in infos can be a target, a function, or a two-element array, where the first element is a target and the second the function.
	 * A target can be any object that this will reference to when the event listener is called.
	 * Notice it is not {@link zk.Event#target}. Rather, it is <code>this</code> when the listener is called.
	 * <p>If the function is not specified, the target must must have a method having the same name as the event. For example, if wgt.listen({onChange: target}) was called, then target.onChange(evt) will be called when onChange event is fired (by {@link #fire}). On the other hand, if the target is not specified, the widget is assumed to be the target.
	 * @param Map infos a map of event listeners.
	 * Each key is the event name, and each value can be the target, the listener function, or a two-element array, where the first element is the target and the second the listener function.
	 * Notice that the target is not {@link zk.Event#target}. Rather, it is <code>this</code> when the listener is called.
	 * @param int priority the higher the number, the earlier it is called. If omitted, 0 is assumed.
	 * If a widget needs to register a listener as the default behavior (such as zul.wnd.Window's onClose), -1000 is suggested 
	 * @return zk.Widget this widget
	 * @see #unlisten
	 * @see #fire
	 * @see #fireX
	 * @see #setListeners
	 * @see #setListener
	 */
	listen: function (infs, priority) {
		priority = priority ? priority: 0;
		for (var evt in infs) {
			var inf = infs[evt];
			if (jq.isArray(inf)) inf = [inf[0]||this, inf[1]];
			else if (typeof inf == 'function') inf = [this, inf];
			else inf = [inf||this, null];
			inf.priority = priority;

			var lsns = this._lsns[evt];
			if (!lsns) this._lsns[evt] = [inf];
			else
				for (var j = lsns.length;;)
					if (--j < 0 || lsns[j].priority >= priority) {
						lsns.splice(j + 1, 0, inf);
						break;
					}
		}
		return this;
	},
	/** Removes a listener from the sepcified event.
<pre><code>
wgt.unlisten({
  onClick: wgt,
  onOpen: wgt._onOpen,
  onMove: [o, o._onMove]
});
</code></pre>
	 * @param Map infos a map of event listeners.
	 * Each key is the event name, and each value can be the target, the listener function, or a two-element array, where the first element is the target and the second the listener function.
	 * @return zk.Widget this widget
	 * @see #listen
	 * @see #isListen
	 * @see #fire
	 * @see #fireX
	 */
	unlisten: function (infs) {
		l_out:
		for (var evt in infs) {
			var inf = infs[evt],
				lsns = this._lsns[evt], lsn;
			for (var j = lsns ? lsns.length: 0; j--;) {
				lsn = lsns[j];
				if (jq.isArray(inf)) inf = [inf[0]||this, inf[1]];
				else if (typeof inf == 'function') inf = [this, inf];
				else inf = [inf||this, null];
				if (lsn[0] == inf[0] && lsn[1] == inf[1]) {
					lsns.splice(j, 1);
					continue l_out;
				}
			}
		}
		return this;
	},
	/** Returns if a listener is registered for the specified event.
	 * @param String evtnm the event name, such as onClick.
	 * @param Map opts [optional] the options. If omitted, it checks only if the server registers any non-deferrable listener, and if the client register any listener. Allowed values:
	 * <ul>
	 * <li>any - in addition to the server's non-deferrable listener and client's listener, it also checks deferrable listener, and the so-called important events</li>
	 * <li>asapOnly - it checks only if the server registers a non-deferrable listener, and if any non-deferrable important event. Use this option, if you want to know whether an AU request will be sent.</li>
	 * </ul>
	 * @return boolean
	 */
	isListen: function (evt, opts) {
		var v = this._asaps[evt];
		if (v) return true;
		if (opts && opts.asapOnly) {
			v = this.$class._importantEvts;
			return v && v[evt];
		}
		if (opts && opts.any) {
			if (v != null) return true;
			v = this.$class._importantEvts;
			if (v && v[evt] != null) return true;
		}

		var lsns = this._lsns[evt];
		return lsns && lsns.length;
	},
	/** Sets the listener a map of listeners.
	 * It is similar to {@link #listen}, except
	 * <ul>
	 * <li>It will 'remember' what the listeners are, such that it can unlisten
	 * by specifying null as the value of the <code>infs</code> argument</li>
	 * <li>The function can be a string and it will be converted to {@link Function}
	 * automatically.</li>
	 * </ul>
	 * <p>This method is mainly designed to be called by the application running
	 * at the server.
	 * 
	 * <p>Example:
<pre><code>
wgt.setListeners({
 onChange: function (event) {this.doSomething();},
 onFocus: 'this.doMore();',
 onBlur: null //unlisten
});
<code></pre>
	 * @param Map infos a map of event listeners.
	 * Each key is the event name, and each value is a string, a function or null.
	 * If the value is null, it means unlisten.
	 * If the value is a string, it will be converted to a {@link Function}.
	 * Notice that the target is not {@link zk.Event#target}. Rather, it is <code>this</code> when the listener is called.
	 */
	setListeners: function (infs) {
		for (var evt in infs)
			this.setListener(evt, infs[evt]);
	},
	/** Sets a listener
	 * @param Array inf a two-element array. The first element is the event name,
	 * while the second is the listener function
	 * @see #setListeners
	 */
	/** Sets a listener
	 * @param String evt the event name
	 * @param Function fn the listener function.
	 * If null, it means unlisten.
	 * @see #setListeners
	 * @see #listen
	 */
	setListener: function (evt, fn) { //used by server
		if (jq.isArray(evt)) {
			fn = evt[1];
			evt = evt[0]
		}

		var bklsns = this._bklsns,
			oldfn = bklsns[evt],
			inf = {};
		if (oldfn) { //unlisten first
			delete bklsns[evt];
			inf[evt] = oldfn
			this.unlisten(inf);
		}
		if (fn) {
			inf[evt] = bklsns[evt]
				= typeof fn != 'function' ? new Function("var event=arguments[0];"+fn): fn;
			this.listen(inf);
		}
	},
	setOverride: function (nm, val) { //used by server (5.0.2)
		if (jq.isArray(nm)) {
			val = nm[1];
			nm = nm[0];
		}
		if (val) {
			var oldnm = '$' + nm;
			if (this[oldnm] == null && this[nm]) //only once
				this[oldnm] = this[nm];
			this[nm] = val;
				//use eval, since complete func decl
		} else {
			var oldnm = '$' + nm;
			this[nm] = this[oldnm]; //restore
			delete this[oldnm];
		}
	},
	setOverrides: function (infs) { //used by server
		for (var nm in infs)
			this.setOverride(nm, infs[nm]);
	},

	//ZK event handling//
	/** Called when the user clicks or right-clicks on widget or a child widget.
	 * It is called before {@link #doClick_} and {@link #doRightClick_}.
	 * <p>Default: does nothing but invokes the parent's {@link #doSelect_}.
	 * <p>Deriving class that supports selection (such as {@link zul.sel.ItemWidget})
	 * shall override this to handle the selection.
	 * <p>Technically, the selection can be handled in {@link #doClick_}.
	 * However, it is better to handle here since this method is invoked first
	 * such that the widget will be selected before one of its descendant widget
	 * handles {@link #doClick_}.
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doClick_
	 * @see #doRightClick_
	 * @since 5.0.1
	 */
	doSelect_: function(evt) {
		if (!evt.stopped) {
			var p = this.parent;
			if (p) p.doSelect_(evt);
		}
	},
	/** Called when the user clicks on a widget or a child widget.
	 * A widget doesn't need to listen the click DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and call parent's doClick_
	 * if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>If a widget, such as zul.wgt.Button, handles onClick, it is better to override this method and <i>not</i> calling back the superclass.
	 * <p>Note: if {@link #shallIgnoreClick_} returns true, {@link #fireX} won't be
	 * called and this method invokes the parent's {@link #doClick_} instead
	 * (unless {@link zk.Event#stopped} is set).
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doDoubleClick_
	 * @see #doRightClick_
	 * @see #doSelect_
	 */
	doClick_: function (evt) {
		if (_fireClick(this, evt)) {
			var p = this.parent;
			if (p) p.doClick_(evt);
		}
	},
	/** Called when the user double-clicks on a widget or a child widget.
	 * A widget doesn't need to listen the dblclick DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and call parent's
	 * doDoubleClick_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>Note: if {@link #shallIgnoreClick_} returns true, {@link #fireX} won't be
	 * called and this method invokes the parent's {@link #doDoubleClick_} instead
	 * (unless {@link zk.Event#stopped} is set).
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doClick_
	 * @see #doRightClick_
	 */
	doDoubleClick_: function (evt) {
		if (_fireClick(this, evt)) {
			var p = this.parent;
			if (p) p.doDoubleClick_(evt);
		}
	},
	/** Called when the user right-clicks on a widget or a child widget.
	 * A widget doesn't need to listen the contextmenu DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and call parent's
	 * doRightClick_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>Note: if {@link #shallIgnoreClick_} returns true, {@link #fireX} won't be
	 * called and this method invokes the parent's {@link #doRightClick_} instead
	 * (unless {@link zk.Event#stopped} is set).
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doClick_
	 * @see #doDoubleClick_
	 */
	doRightClick_: function (evt) {
		if (_fireClick(this, evt)) {
			var p = this.parent;
			if (p) p.doRightClick_(evt);
		}
	},
	/** Called when the user moves the mouse pointer on top of a widget (or one of its child widget).
	 * A widget doesn't need to listen the mouseover DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseOver_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doMouseMove_
	 * @see #doMouseOver_
	 * @see #doMouseOut_
	 * @see #doMouseDown_
	 * @see #doMouseUp_
     */
	doMouseOver_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOver_(evt);
		}
	},
	/** Called when the user moves the mouse pointer out of a widget (or one of its child widget).
	 * A widget doesn't need to listen the mouseout DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseOut_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @see #doMouseMove_
	 * @see #doMouseOver_
	 * @see #doMouseDown_
	 * @see #doMouseUp_
	 */
	doMouseOut_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOut_(evt);
		}
	},
	/** Called when the user presses down the mouse button on this widget (or one of its child widget).
	 * A widget doesn't need to listen the mousedown DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseDown_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doMouseMove_
	 * @see #doMouseOver_
	 * @see #doMouseOut_
	 * @see #doMouseUp_
	 * @see #doClick_
	 */
	doMouseDown_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseDown_(evt);
		}
	},
	/** Called when the user presses up the mouse button on this widget (or one of its child widget).
	 * A widget doesn't need to listen the mouseup DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseUp_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doMouseMove_
	 * @see #doMouseOver_
	 * @see #doMouseOut_
	 * @see #doMouseDown_
	 * @see #doClick_
	 */
	doMouseUp_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseUp_(evt);
		}
	},
	/** Called when the user moves the mouse pointer over this widget (or one of its child widget).
	 * A widget doesn't need to listen the mousemove DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseMove_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doMouseOver_
	 * @see #doMouseOut_
	 * @see #doMouseDown_
	 * @see #doMouseUp_
	 */
	doMouseMove_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseMove_(evt);
		}
	},

	/** Called when the user presses down a key when this widget has the focus ({@link #focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the keydown DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doKeyDown_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doKeyUp_
	 * @see #doKeyPress_
	 */
	doKeyDown_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyDown_(evt);
		}
	},
	/** Called when the user presses up a key when this widget has the focus ({@link #focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the keyup DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doKeyUp_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doKeyDown_
	 * @see #doKeyPress_
	 */
	doKeyUp_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyUp_(evt);
		}
	},
	/** Called when the user presses a key when this widget has the focus ({@link #focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the keypress DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doKeyPress_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doKeyDown_
	 * @see #doKeyUp_
	 */
	doKeyPress_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyPress_(evt);
		}
	},

	/** A utility to simplify the listening of <code>onFocus</code>.
	 * Unlike other doXxx_ (such as {@link #doClick_}), a widget needs to listen
	 * the onFocus event explicitly if it might gain and lose the focus.
	 * <p>For example,
<pre><code>
var fn = this.$n('focus');
this.domListen_(fn, 'onFocus', 'doFocus_');
this.domListen_(fn, 'onBlur', 'doBlur_');
</code></pre>
	 *<p>Of course, you can listen it with jQuery DOM-level utilities, if you pefer to handle it differently.
	 *
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doFocus_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doBlur_
	 */
	doFocus_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doFocus_(evt);
		}
	},
	/** A utility to simplify the listening of <code>onBlur</code>.
	 * Unlike other doXxx_ (such as {@link #doClick_}), a widget needs to listen
	 * the onBlur event explicitly if it might gain and lose the focus.
	 * <p>For example,
<pre><code>
var fn = this.$n('focus');
this.domListen_(fn, 'onFocus', 'doFocus_');
this.domListen_(fn, 'onBlur', 'doBlur_');
</code></pre>
	 *<p>Of course, you can listen it with jQuery DOM-level utilities, if you pefer to handle it differently.
	 *
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doBlur_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events">Widget and DOM Events</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doFocus_
	 */
	doBlur_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doBlur_(evt);
		}
	},

	//DOM event handling//
	/** Registers an DOM event listener for the specified DOM element (aka., node).
	 * You can use jQuery to listen the DOM event directly, or
	 * use this method instead.
<pre><code>
bind_: function () {
  this.$supers('bind_', arguments);
  this.domListen_(this.$n(), "onChange"); //fn is omitted, so _doChange is assumed
  this.domListen_(this.$n("foo"), "onSelect", "_doFooSelect"); //specify a particular listener
},
unbind_: function () {
  this.domUnlisten_(this.$n(), "onChange"); //unlisten
  this.domUnlisten_(this.$n("foo"), "onSelect", "_doFooSelect");
  this.$supers('unbind_', arguments);
},
_doChange_: function (evt) { //evt is an instance of zk.Event
  //event listener
},
_doFooSelect: function (evt) {
}
</code></pre>
	 * See also <a href="http://docs.zkoss.org/wiki/Listen_DOM_Events_with_Member_Method">Listen DOM Events with Member Method</a>.
	 *
	 * <h3>Design Mode</h3>
	 * If a widget is created and controlled by ZK Weaver for visual design,
	 * we call the widget is in design mode ({@link #$weave}).
	 * Furthermore, this method does nothing if the widget is in the design mode.
	 * Thus, if you want to listen a DOM event ({@link jq.Event}), you have
	 * to use jQuery directly.
	 * @param DOMElement node a node of this widget.
	 * It is usually retrieved by {@link #$n}.
	 * @param String evtnm the event name to register, such as onClick.
	 * @param Object fn the name ({@link String}) of the member method to handle the event,
	 * or the function ({@link Function}).
	 * It is optional. If omitted, <i>_doEvtnm</i> is assumed, where <i>evtnm</it>
	 * is the value passed thru the <code>evtnm</code> argument.
	 * For example, if the event name is onFocus, then the method is assumed to be
	 * _doFocus.
	 * @return zk.Widget this widget
	 * @see #domUnlisten_
	 */
	domListen_: function (n, evtnm, fn) {
		if (!this.$weave) {
			var inf = _domEvtInf(this, evtnm, fn);
			jq(n, zk).bind(inf[0], inf[1]);
		}
		return this;
	},
	/** Un-registers an event listener for the specified DOM element (aka., node).
	 * <p>Refer to {@link #domListen_} for more information. 
	 * @param DOMElement node a node of this widget.
	 * It is usually retrieved by {@link #$n}.
	 * @param String evtnm the event name to register, such as onClick.
	 * @param Object fn the name ({@link String}) of the member method to handle the event,
	 * or the function ({@link Function}).
	 * It is optional. If omitted, <i>_doEvtnm</i> is assumed, where <i>evtnm</it>
	 * is the value passed thru the <code>evtnm</code> argument.
	 * For example, if the event name is onFocus, then the method is assumed to be
	 * _doFocus.
	 * @return zk.Widget this widget
	 * @see #domListen_
	 */
	domUnlisten_: function (n, evtnm, fn) {
		if (!this.$weave) {
			var inf = _domEvtInf(this, evtnm, fn);
			jq(n, zk).unbind(inf[0], inf[1]);
		}
		return this;
	},
	/** Converts a coordinate related to the browser window into the coordinate
	 * related to this widget.
	 * @param int x the X coordinate related to the browser window
	 * @param int y the Y coordinate related to the browser window
	 * @return Offset the coordinate related to this widget (i.e., [0, 0] is
	 * the left-top corner of the widget).
	 * @since 5.0.2
	 */
	fromPageCoord: function (x, y) {
		var ofs = zk(this).revisedOffset();
		return [x - ofs[0], y - ofs[1]];
	},
	toJSON: function () { //used by JSON
		return this.uuid;
	}

}, {
	/** Retrieves the widget.
	 * @param Object n the object to look for. If it is a string,
	 * it tried to resolve it with jq(n, zk) -- see {@link _global_.jq}.<br/>
	 * If it is an DOM element ({@link DOMElement}), it will look up
	 * which widget it belongs to.<br/>
	 * If the object is not a DOM element and has a property called
	 * <code>target</code>, then <code>target</code> is assumed.
	 * Thus, you can pass an instance of {@link jq.Event} or {@link zk.Event},
	 * and the target widget will be returned.
	 * @param Map opts [optional] the options. Allowed values:
	 * <ul>
	 * <li>exact - only check its uuid.(since 5.0.2)</li>
	 * <li>strict - whether not to look up the parent node.(since 5.0.2)
	 * If omitted, false is assumed (and it will look up parent).</li>
	 * <li>child - whether to ensure the given element is a child element
	 * of the widget's main element ({@link #$n}). In most cases, if ID
	 * of an element is xxx-yyy, the the element must be a child of
	 * the element whose ID is xxx. However, there is some exception
	 * such as the shadow of a window.</li>
	 * </ul>
	 * @return zk.Widget
	 */
	$: function (n, opts) {
		if (n && n.zk && n.zk.jq == n) //jq()
			n = n[0];

		if (!n || zk.Widget.isInstance(n)) return n;

		var wgt, id;
		if (typeof n == 'string') {
			n = jq(id = n, zk)[0];
			if (!n) { //some widget might not have DOM element (e.g., timer)
				if (id.charAt(0) == '#') id = id.substring(1);
				wgt = _binds[id]; //try first (since ZHTML might use -)
				if (!wgt)
					wgt = (n = id.indexOf('-')) >= 0 ? _binds[id.substring(0, n)]: null;
				return wgt;
			}
		}

		if (!n.nodeType) { //n could be an event (skip Element)
			var e = n.originalEvent;
			n = (e?e.z$target:null) || n.target || n; //check DOM event first
		}

		if (opts && opts.exact)
			return _binds[n.id];

		for (; n; n = zk(n).vparentNode()||n.parentNode) {
			id = n.id || (n.getAttribute ? n.getAttribute("id") : '');
			if (id) {
				wgt = _binds[id]; //try first (since ZHTML might use -)
				if (wgt) return wgt;

				var j = id.indexOf('-');
				if (j >= 0) {
					id = id.substring(0, j);
					wgt = _binds[id];
					if (wgt)
						if (opts && opts.child) {
							var n2 = wgt.$n();
							if (n2 && jq.isAncestor(n2, n))
								return wgt;
						} else
							return wgt;
				}
			}
			if (opts && opts.strict) break;
		}
		return null;
	},

	/** Called to mimic the mouse down event fired by the browser. It is used for implement a widget. In most cases, you don't need to invoke this method. However, it is useful if the widget you are implemented will 'eat' the mouse-down event so ZK Client Engine won't be able to intercept it at the document level.
	 * @param zk.Widget wgt the widget that receives the mouse-down event
	 * @param boolean noFocusChange whether zk.currentFocus shall be changed to wgt. 
	 */
	mimicMouseDown_: function (wgt, noFocusChange) { //called by mount
		var modal = zk.currentModal;
		if (modal && !wgt) {
			var cf = zk.currentFocus;
			//Note: browser might change focus later, so delay a bit
			//(it doesn't work if we stop event instead of delay - IE)
			if (cf && zUtl.isAncestor(modal, cf)) cf.focus(0);
			else modal.focus(0);
		} else if (!wgt || wgt.canActivate()) {
			if (!noFocusChange) {
				zk.currentFocus = wgt;
				zk._cfByMD = true;
				setTimeout(function(){zk._cfByMD = false;}, 0);
					//turn it off later since onBlur_ needs it
			}
			if (wgt)
				zWatch.fire('onFloatUp', wgt); //notify all
			else
				for (var dtid in zk.Desktop.all)
					zWatch.fire('onFloatUp', zk.Desktop.all[dtid]); //notify all
		}
	},
	/**
	 * Returns all elements with the given widget name.
	 * @param String name the widget name {@link #widgetName}.
	 * @return Array an array of {@link DOMElement}
	 * @since 5.0.2
	 */
	getElementsByName: function (name) {
		var els = [];
		for (var wid in _binds) {
			if (name == '*' || name == _binds[wid].widgetName)
				els.push(_binds[wid].$n());
		}
		return els;
	},
	/**
	 * Returns all elements with the given ID.
	 * @param String id the id of a widget, {@link #id}.
	 * @return Array an array of {@link DOMElement}
	 * @since 5.0.2
	 */
	getElementsById: function (id) {
		var els = [];
		for (var wgts = _globals[id], i = wgts?wgts.length:0; i--;)
			els.unshift(wgts[i].$n());
		return els;
	},

	//uuid//
	/** Converts Converts an ID of a DOM element to UUID.
	 * It actually removes '-*'. For example, zk.Widget.uuid('z_aa-box') returns 'z_aa'. 
	 * @param String subId the ID of a DOM element
	 * @return String the uuid of the widget (notice that the widget might not exist)
	 */
	uuid: function (id) {
		var uuid = typeof id == 'object' ? id.id || '' : id,
			j = uuid.indexOf('-');
		return j >= 0 ? uuid.substring(0, j): id;
	},
	/** Returns the next unique UUID for a widget.
	 * The UUID is unique in the whole browser window and does not conflict with the peer component's UUID.
	 * <p>This method is called automatically if {@link #$init} is called without uuid.
	 * @return String the next unique UUID for a widget
	 */
	nextUuid: function () {
		return '_z_' + _nextUuid++;
	},

	/** Tests if UUID is generated automatically. 
	 * @param String uuid the UUID to test
	 * @return boolean
	 */
	isAutoId: function (id) {
		return !id || id.startsWith('_z_') || id.startsWith('z_');
	},

	/** Registers a widget class.
	 * It is called automatically if the widget is loaded by WPD loader, so you rarely
	 * need to invoke this method.
	 * However, if you create a widget class at run time, you have to call this method explicitly.
	 * Otherwise, {@link #className}, {@link #getClass}, and {@link #newInstance}
	 * won't be applicable.
	 * <p>Notice that the class must be declared before calling this method.
	 * In other words, zk.$import(clsnm) must return the class of the specified class name.
<pre><code>
zk.Widget.register('foo.Cool'); //class name
zk.Widget.getClass('cool'); //widget name
</code></pre>
	 * @param String clsnm the class name, such as zul.wnd.Window
	 * @param boolean blankPreserved whether to preserve the whitespaces between child widgets when declared in iZUML. If true, a widget of clsnm will have a data member named blankPreserved (assigned with true). And, iZUML won't trim the whitespaces (aka., the blank text) between two adjacent child widgets. 
	 */
	register: function (clsnm, blankprev) {
		var cls = zk.$import(clsnm);
		cls.prototype.className = clsnm;
		var j = clsnm.lastIndexOf('.');
		if (j >= 0) clsnm = clsnm.substring(j + 1);
		_wgtcls[cls.prototype.widgetName = clsnm.toLowerCase()] = cls;
		if (blankprev) cls.prototype.blankPreserved = true;
	},
	/** Returns the class of the specified widget's name. For example,
<pre><code>
zk.Widget.getClass('combobox');
</code></pre>
	 *<p>Notice that null is returned if the widget is not loaded (or not exist) yet. 
	 * @param String wgtnm the widget name, such as textbox.
	 * @return zk.Class the class of the widget.
	 * @see #newInstance
	 * @see #register
	 */
	getClass: function (wgtnm) {
		return _wgtcls[wgtnm];
	},
	/** Creates a widget by specifying the widget name.
	 * The widget name is the last part of the class name of a widget (and converting the first letter to lower case).
	 * For example, if a widget's class name is zul.inp.Textbox, then the widget name is textbox.
	 * <p>This method is usually used by tools, such as zk.zuml.Parser, rather than developers, since developers can create the widget directly if he knows the class name. 
	 * @param String wgtnm the widget name, such as textbox.
	 * @param Map props [optional] the properties that will be passed to
	 * {@link #$init}.
	 * @see #getClass
	 * @see #register
	 * @return zk.Widget
	 */
	newInstance: function (wgtnm, props) {
		var cls = _wgtcls[wgtnm];
		if (!cls)
			throw 'widget not found: '+wgtnm;
		return new cls(props);
	},

	_autohide: function () { //called by effect.js
		if (!_floatings.length) {
			for (var n; n = _hidden.shift();)
				n.style.visibility = n.getAttribute('z_ahvis')||'';
			return;
		}
		for (var tns = ['IFRAME', 'APPLET'], i = 2; i--;)
			l_nxtel:
			for (var ns = document.getElementsByTagName(tns[i]), j = ns.length; j--;) {
				var n = ns[j], $n = zk(n), visi;
				if ((!(visi=$n.isVisible(true)) && !_hidden.$contains(n))
				|| (!i && !n.getAttribute("z_autohide") && !n.getAttribute("z.autohide"))) //check z_autohide (5.0) and z.autohide (3.6) if iframe
					continue; //ignore

				for (var tc = _topnode(n), k = _floatings.length; k--;) {
					var f = _floatings[k].node,
						tf = _topnode(f);
					if (tf == tc || _zIndex(tf) < _zIndex(tc) || !$n.isOverlapped(f))
						continue;

					if (visi) {
						_hidden.push(n);
						try {
							n.setAttribute('z_ahvis', n.style.visibility);
						} catch (e) {
						}
						n.style.visibility = 'hidden';
					}
					continue l_nxtel;
				}

				if (_hidden.$remove(n))
					n.style.visibility = n.getAttribute('z_ahvis')||'';
			}
	}
});

/** A reference widget. It is used as a temporary widget that will be
 * replaced with a real widget when {@link #bind_} is called.
 * <p>Developers rarely need it.
 * Currently, it is used only for the server to generate the JavaScript codes
 * for mounting.
 * @disable(zkgwt)
 */
zk.RefWidget = zk.$extends(zk.Widget, {
	bind_: function () {
		var w = zk.Widget.$(this.uuid);
		if (!w || !w.desktop) throw 'illegal: '+w;

		var p = w.parent, q;
		if (p) { //shall be a desktop
			var dt = w.desktop, n = w._node;
			w.desktop = null; //avoid unbind/bind
			w.clearCache();
			p.removeChild(w);
			w.desktop = dt; w._node = n;
		}

		p = w.parent = this.parent,
		q = w.previousSibling = this.previousSibling;
		if (q) q.nextSibling = w;
		else if (p) p.firstChild = w;

		q = w.nextSibling = this.nextSibling;
		if (q) q.previousSibling = w;
		else if (p) p.lastChild = w;

		this.parent = this.nextSibling = this.previousSibling = null;

		_addIdSpaceDown(w);
		//no need to call super since it is bound
	}
});

//desktop//
/** A desktop.
 * Unlike the component at the server, a desktop is a widget.
 * <p>However, the desktop are different from normal widgets:
 * <ol>
 * <li>The desktop is a conceptual widget. It is never attached with the DOM tree. Its desktop field is always null. In addition, calling zk.Widget#appendChild won't cause the child to be attached to the DOM tree automatically.</li>
 * <li>The desktop's ID and UUID are the same. </li>
 * </ol>
 * @disable(zkgwt)
 */
zk.Desktop = zk.$extends(zk.Widget, {
	bindLevel: 0,
	/** The class name (<code>zk.Desktop</code>).
	 * @type String
	 */
	className: "zk.Desktop",
	/** The widget name (<code>desktop</code>).
	 * @type String
	 * @since 5.0.2
	 */
	widgetName: "desktop",

	/** Constructor
	 * @param String dtid the ID of the desktop
	 * @param String contextURI the context URI, such as <code>/zkdemo</code>
	 * @param String updateURI the URI of ZK Update Engine, such as <code>/zkdemo/zkau</code>
	 * @param String reqURI the URI of the request path.
	 * @param boolean stateless whether this desktop is used for a stateless page.
	 * Specify true if you want to use <a href="http://docs.zkoss.org/wiki/ZK_5.0_and_Client-centric_Approach">the client-centric approach</a>.
	 */
	$init: function (dtid, contextURI, updateURI, reqURI, stateless) {
		this.$super('$init', {uuid: dtid}); //id also uuid

		var Desktop = zk.Desktop, dts = Desktop.all, dt = zUtl.now();
		if (dt > _syncdt) { //Liferay+IE: widgets are created later so don't sync at beginning
			_syncdt = dt + 60000;
			Desktop.sync();
		}

		this._aureqs = [];
		//Sever side effect: this.desktop = this;

		if (dt = dts[dtid]) {
			if (updateURI != null) dt.updateURI = updateURI;
			if (contextURI != null) dt.contextURI = contextURI;
		} else {
			this.uuid = this.id = dtid;
			this.updateURI = updateURI != null ? updateURI: zk.updateURI;
			this.contextURI = contextURI != null ? contextURI: zk.contextURI;
			this.requestPath = reqURI || '';
			this.stateless = stateless;
			dts[dtid] = this;
			++Desktop._ndt;
			if (!Desktop._dt) Desktop._dt = this; //default desktop
		}
	},
	_exists: function () {
		if (this._pguid) //_pguid not assigned at beginning
			for (var w = this.firstChild; w; w = w.nextSibling) //under JSP, page.$n is null so test all
				if (w.$n())
					return true;
	},
	bind_: zk.$void,
	unbind_: zk.$void,
	/** This method is voided (does nothing) since the desktop's ID
	 * can be changed.
	 * @param String id the ID
	 * @return zk.Widget this widget
	 */
	setId: zk.$void
},{
	/** Returns the desktop of the specified desktop ID, widget, widget UUID, or DOM element.
	 * <p>Notice that the desktop's ID and UUID are the same.
	 * @param Object o a desktop's ID, a widget, a widget's UUID, or a DOM element.
	 * If not specified, the default desktop is assumed.
	 * @return zk.Desktop
	 */
	$: function (dtid) {
		var Desktop = zk.Desktop, w;
		if (dtid) {
			if (Desktop.isInstance(dtid))
				return dtid;

			w = Desktop.all[dtid];
			if (w)
				return w;

			w = zk.Widget.$(dtid);
			for (; w; w = w.parent) {
				if (w.desktop)
					return w.desktop;
				if (w.$instanceof(Desktop))
					return w;
			}
			return null;
		}

		if (w = Desktop._dt)
			return w;
		for (dtid in Desktop.all)
			return Desktop.all[dtid];
	},
	/** A map of all desktops (readonly).
	 * The key is the desktop ID and the value is the desktop.
	 * @type Map
	 */
	all: {},
	_ndt: 0, //used in au.js/dom.js
	/** Checks if any desktop becomes invalid, and removes the invalid desktops.
	 * This method is called automatically when a new desktop is added. Application developers rarely need to access this method.
	 * @return zk.Desktop the first desktop, or null if no desktop at all. 
	 */
	sync: function () {
		var Desktop = zk.Desktop, dts = Desktop.all, dt;
		if ((dt = Desktop._dt) && !dt._exists()) //removed
			Desktop._dt = null;
		for (var dtid in dts) {
			if (!(dt = dts[dtid])._exists()) { //removed
				delete dts[dtid];
				--Desktop._ndt;
			} else if (!Desktop._dt)
				Desktop._dt = dt;
		}
		return Desktop._dt;
	}
});
})();

/** A page.
 * Unlike the component at the server, a page is a widget.
 * @disable(zkgwt)
*/
zk.Page = zk.$extends(zk.Widget, {
	_style: "width:100%;height:100%",
	/** The class name (<code>zk.Page</code>).
	 * @type String
	 */
	className: "zk.Page",
	/** The widget name (<code>page</code>).
	 * @type String
	 * @since 5.0.2
	 */
	widgetName: "page",

	/** Constructor.
	 * @param Map props the properties to assign to this page
	 * @param boolean contained whether this page is contained.
	 * By contained we mean this page is a top page (i.e., not included
	 * by the include widget) but it is included by other technologies,
	 * such as JSP.
	 */
	$init: function (props, contained) {
		this._fellows = {};

		this.$super('$init', props);

		if (contained) zk.Page.contained.push(this);
	},
	/** Generates the HTML fragment for this macro component.
	 * <p>Default: it generate DIV to enclose the HTML fragment
	 * of all child widgets.
	 * @param Array out an array of HTML fragments.
	 */
	redraw: function (out) {
		out.push('<div', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div>');
	}
},{
	/** An array of contained pages (i.e., a standalone ZK page but included by other technology).
	 * For example, a ZUL age that is included by a JSP page.
	 * A contained page usually covers a portion of the browser window. 
	 * @type Array an array of contained pages ({@link zk.Page})
	 */
	contained: []
});
zk.Widget.register('zk.Page', true);

/** A native widget.
 * It is used mainly to represent the native componet created at the server.
 * @disable(zkgwt)
 */
zk.Native = zk.$extends(zk.Widget, {
	/** The class name (<code>zk.Native</code>)
	 * @type String
	 */
	className: "zk.Native",
	/** The widget name (<code>native</code>).
	 * @type String
	 * @since 5.0.2
	 */
	widgetName: "native",

	redraw: function (out) {
		var s = this.prolog;
		if (s) out.push(s);

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		s = this.epilog;
		if (s) out.push(s);
	}
});

/** A macro widget.
 * It is used mainly to represent the macro componet created at the server.
 */
zk.Macro = zk.$extends(zk.Widget, {
	/** The class name (<code>zk.Macro</code>).
	 * @type String
	 */
	className: "zk.Macro",
	/** The widget name (<code>macro</code>).
	 * @type String
	 * @since 5.0.2
	 */
	widgetName: "macro",
	_enclosingTag: "span",

	$define: {
		/** Returns the tag name for this macro widget.
		 * <p>Default: span
		 * @return String the tag name (such as div or span)
		 * @since 5.0.3
		 */
		/** Sets the tag name for this macro widget
		 * @param String tag the tag name, such as div
		 * @since 5.0.3
		 */
		enclosingTag: function () {
			this.rerender();
		}
	},

	/** Generates the HTML fragment for this macro component.
	 * <p>Default: it generate SPAN to enclose the HTML fragment
	 * of all child widgets.
	 * @param Array out an array of HTML fragments (String).
	 */
	redraw: function (out) {
		out.push('<', this._enclosingTag, this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</', this._enclosingTag, '>');
	}
});

/** A skipper is an object working with {@link zk.Widget#rerender}
 * to rerender portion(s) of a widget (rather than the whole widget).
 * It can improve the performance a lot if it can skip a lot of portions, such as a lot of child widgets. 
 * <p>The skipper decides what to skip (i.e., not to rerender), detach the skipped portion(s), and attach them back after rerendering. Thus, the skipped portion won't be rerendered, nor unbound/bound.
 * <p>The skipper has to implement three methods, {@link #skipped},
 * {@link #skip} and {@link #restore}. {@link #skipped} is used to test whether a child widget shall be skipped.
 * {@link #skip} and {@link #restore} works together to detach and attach the skipped portions from the DOM tree. Here is how
 * {@link zk.Widget#rerender} uses these two methods:
<pre><code>
rerender: function (skipper) {
  var skipInfo;
  if (skipper) skipInfo = skipper.skip(this);
 
  this.replaceHTML(this.node, null, skipper);
 
  if (skipInfo) skipper.restore(this, skipInfo);
}
</code></pre>
 * <p>Since {@link zk.Widget#rerender} will pass the returned value of {@link #skip} to {@link #restore}, the skipper doesn't need to store what are skipped. That means, it is possible to have one skipper to serve many widgets. {@link #nonCaptionSkipper} is a typical example.
 * <p>In additions to passing a skipper to {@link zk.Widget#rerender}, the widget has to implement the mold method to handle the skipper:
<pre><code>
function (skipper) {
 var html = '<fieldset' + this.domAttrs_() + '>',
 cap = this.caption;
 if (cap) html += cap.redraw();
 
 html += '<div id="' + this.uuid + '$cave"' + this._contentAttrs() + '>';
 if (!skipper)
  for (var w = this.firstChild; w; w = w.nextSibling)
   if (w != cap) html += w.redraw();
 return html + '</div></fieldset>';
}
</pre></code>
 * <p>See also <a href="http://docs.zkoss.org/wiki/Rerender_Portions_of_Widget">Rerender Portions of Widget</a>.
 * @disable(zkgwt)
 */
zk.Skipper = zk.$extends(zk.Object, {
	/** Returns whether the specified child wiget will be skipped by {@link #skip}.
	 * <p>Default: returns if wgt.caption != child. In other words, it skip all children except the caption. 
	 * @param zk.Widget wgt the widget to re-render
	 * @param zk.Widget child a child (descendant) of this widget.
	 * @return boolean
	 */
	skipped: function (wgt, child) {
		return wgt.caption != child;
	},
	/** Skips all or subset of the descedant (child) widgets of the specified widget.
	 * <p>Notice that the <pre>skipId</pre> argument is not used by {@link zk.Widget#rerender}.
	 * Rather it is used to simplify the overriding of this methid,
	 * such that the deriving class can call back this class and
	 * to pass a different ID to skip
	 *
	 * <p>If you don't want to pass a different ID (default: uuid + '-cave'),
	 * you can ignore <code>skipId</code>
<pre><code>
Object skip(zk.Widget wgt);
</code></pre>
	 * <p>Default: it detaches all DOM elements whose parent element is
	 * <code>jq(skipId || (wgt.uuid + '-cave'), zk)</code>. 
	
	 * @param zk.Widget wgt the widget being rerendered.
	 * @param String skipId [optional] the ID of the element where all its descendant
	 * elements shall be detached by this method, and restored later by {@link #restore}. 
	 * If not specified, <code>uuid + '-cave'</code> is assumed.
	 * @return DOMElement
	 */
	skip: function (wgt, skipId) {
		var skip = jq(skipId || (wgt.uuid + '-cave'), zk)[0];
		if (skip && skip.firstChild) {
			skip.parentNode.removeChild(skip);
				//don't use jq to remove, since it unlisten events
			return skip;
		}
		return null;
	},
	/** Restores the DOM elements that are detached (i.e., skipped) by {@link #skip}. 
	 * @param zk.Widget wgt the widget being re-rendered
	 * @param Object inf the object being returned by {@link #skip}.
	 * It depends on how a skipper is implemented. It is usually to carry the information about what are skipped 
	 */
	restore: function (wgt, skip) {
		if (skip) {
			var loc = jq(skip.id, zk)[0];
			for (var el; el = skip.firstChild;) {
				skip.removeChild(el);
				loc.appendChild(el);

				if (zk.ie) zjq._fixIframe(el); //in domie.js, Bug 2900274
			}
		}
	}
});
/** @partial zk.Skipper
 */
//@{
	/** An instance of {@link zk.Skipper} that can be used to skip the rerendering of child widgets except the caption.
	 * <p>It assumes
	 * <ol>
	 * <li>The child widget not to skip can be found by the caption data member.</li>
	 * <li>The DOM elements to skip are child elements of the DOM element whose ID is widgetUUID$cave, where widgetUUID is the UUID of the widget being rerendered. </li>
	 * </ol>
	 * <p>In other words, it detaches (i.e., skipped) all DOM elements under widget.$n('cave').
<pre><code>
setClosable: function (closable) {
 if (this._closable != closable) {
  this._closable = closable;
  if (this.node) this.rerender(zk.Skipper.nonCaptionSkipper);
 }
}
</pre></code>
	 * @type zk.Skipper
	 */
	//nonCaptionSkipper: null
//@};
zk.Skipper.nonCaptionSkipper = new zk.Skipper();

//Extra//

zkreg = zk.Widget.register; //a shortcut for WPD loader
function zkopt(opts) {
	for (var nm in opts) {
		var val = opts[nm];
		switch (nm) {
		case "pd": zk.procDelay = val; break;
		case "td": zk.tipDelay =  val; break;
		case "rd": zk.resendDelay = val; break;
		case "dj": zk.debugJS = val; break;
		case "kd": zk.keepDesktop = val; break;
		case "pf": zk.pfmeter = val; break;
		case "cd": zk.clickFilterDelay = val; break;
		case "ta": zk.timerAlive = val; break;
		case "to":
			zk.timeout = val;
			zAu._resetTimeout();
			break;
		case "ed":
			switch (val) {
			case 'e':
				zk.feature.ee = true;
			case 'p':
				zk.feature.pe = true;
			}
			break;
		case 'eu': zAu.setErrorURI(val); break;
		case 'ppos': zk.progPos = val; break;
		case 'eup': zAu.setPushErrorURI(val);
		}
	}
}
