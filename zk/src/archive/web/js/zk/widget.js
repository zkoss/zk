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
		_fixBindMem = zk.$void, //fix IE memory leak
		_bindcnt = 0,
		_floatings = [], //[{widget,node}]
		_nextUuid = 0,
		_globals = {}, //global ID space {id, wgt}
		_domevtfnm = {}, //{evtnm, funnm}
		_domevtnm = {onDoubleClick: 'dblclick'}, //{zk-evt-nm, dom-evt-nm}
		_wgtcls = {}; //{clsnm, cls}

	//such as child of hbox/vbox or border-layout
	function hiddenByParent(w) {
		var p = w.parent;
		if (p && p.isVisible() && (p=p.$n()) && (w=w.$n()))
			while ((w=zk(w).vparentNode()||w.parentNode) && p != w)
				if ((w.style||{}).display == 'none')
					return true;
	}

	//IE doesn't free _binds (when delete _binds[x]); so clean it up
	if (zk.ie)
		_fixBindMem = function () {	
			if (++_bindcnt > 2000) {
				_bindcnt = 0;
				_binds = zk.copy({}, _binds);
			}
		};

	//Check if el is a prolog
	function _isProlog(el) {
		var txt;
		return el && el.nodeType == 3 //textnode
			&& (txt=el.nodeValue) && !txt.trim().length;
	}

	//Event Handling//
	function _cloneEvt(evt, target) {
		return new zk.Event(target, evt.name, evt.data, evt.opts, evt.domEvent);
	}
	function _domEvtInf(wgt, evtnm, fn) { //proxy event listener
		if (!fn && !(fn = _domevtfnm[evtnm]))
			_domevtfnm[evtnm] = fn = '_do' + evtnm.substring(2);

		var f = wgt[fn];
		if (!f)
			throw 'Listener ' + fn + ' not found in ' + wgt.className;

		var domn = _domevtnm[evtnm];
		if (!domn)
			domn = _domevtnm[evtnm] = evtnm.substring(2).toLowerCase();
		return [domn, _domEvtProxy(wgt, f)];
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
			args.unshift(evt = jq.event.toEvent(devt, wgt));

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
			if (typeof ret == 'undefined') ret = evt.returnValue;
			if (evt.domStopped) devt.stop();
			return devt.type == 'dblclick' && typeof ret == 'undefined' ? false: ret;
		};
	}

	function _bind0(wgt) {
		_binds[wgt.uuid] = wgt;
	}
	function _unbind0(wgt) {
		delete _binds[wgt.uuid];
		wgt._node = wgt.desktop = null;
		wgt._subnodes = {};
		wgt._nodeSolved = false;
	}
	function _bindrod(wgt) {
		_bind0(wgt);
		wgt.z_rod = true;

		for (var child = wgt.firstChild; child; child = child.nextSibling)
			_bindrod(child);
	}
	function _unbindrod(wgt) {
		_unbind0(wgt);
		delete wgt.z_rod;

		for (var child = wgt.firstChild; child; child = child.nextSibling)
			_unbindrod(child);
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
		if (wgt.id) delete owner._fellows[wgt.id];
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			_rmIdSpaceDown0(wgt, owner);
	}

	function _onBind(wgt) {
		if (wgt.isListen('onBind'))
			zk.afterMount(function () {
				if (wgt.desktop) //might be unbound
					wgt.fire('onBind');
			});
	}
	function _onUnbind(wgt) {
		if (wgt.isListen('onUnbind'))
			zk.afterMount(function () {
				if (!wgt.desktop) //might be bound
					wgt.fire('onUnbind');
			});
	}
	//set minimum flex size and return it
	function _setMinFlexSize(wgt, n, o) {
		//find the max size of all children
		if (o == 'height') {
			if (wgt._vflexsize === undefined) { //cached?
				wgt.setFlexSize_({height:'auto'});
				var zkn = zk(n),
					ntop = n.offsetTop,
					noffParent = n.offsetParent,
					pb = zkn.padBorderHeight(),
					max = 0;
				for (var cwgt = wgt.firstChild; cwgt; cwgt = cwgt.nextSibling) {
					var c = cwgt.$n(),
						sz = cwgt._vflex == 'min' && cwgt._vflexsize === undefined ? //recursive 
							_setMinFlexSize(cwgt, c, o) : 
							(c.offsetHeight + c.offsetTop - (c.offsetParent == noffParent ? ntop : 0) + zk(c).sumStyles("b", jq.margins));
					if (sz > max)
						max = sz;
				}
				var margin = zkn.sumStyles("tb", jq.margins),
					sz = wgt.setFlexSize_({height:(max + pb + margin)});
				if (sz && sz.height >= 0)
					wgt._vflexsize = sz.height + margin;
			}
			return wgt._vflexsize;
			
		} else if (o == 'width') {
			if (wgt._hflexsize === undefined) { //cached?
				wgt.setFlexSize_({width:'auto'});
				var zkn = zk(n),
					nleft = n.offsetLeft,
					noffParent = n.offsetParent,
					pb = zkn.padBorderWidth(),
					max = 0;
				for (var cwgt = wgt.firstChild; cwgt; cwgt = cwgt.nextSibling) {
					var c = cwgt.$n(),
						sz = cwgt._hflex == 'min' && cwgt._hflexsize === undefined ? //recursive
							_setMinFlexSize(cwgt, c, o) : 
							(c.offsetWidth + c.offsetLeft - (c.offsetParent == noffParent ? nleft : 0) + zk(c).sumStyles("r", jq.margins));
					if (sz > max)
						max = sz;
				}
				var margin = zkn.sumStyles("lr", jq.margins);
				var sz = wgt.setFlexSize_({width:(max + pb + margin)});
				if (sz && sz.width >= 0)
					wgt._hflexsize = sz.width + margin;
			}
			return wgt._hflexsize;
		} else
			return 0;
	}
	//fix vflex/hflex of all my sibling nodes
	function _fixFlex() {
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
		if (zk.ie6_ && p.tagName == 'DIV') {
			oldPos = p.style.position;
			p.style.position = 'relative';
		}
		var sameOffParent = c ? c.offsetParent === p.offsetParent : false,
			tbp = zkp.sumStyles('t', jq.borders),
			lbp = zkp.sumStyles('l', jq.borders),
			segTop = sameOffParent ? (p.offsetTop + tbp) : tbp,
			segLeft = sameOffParent ? (p.offsetLeft + lbp) : lbp,
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
					offTop = c.offsetTop,
					offLeft = c.offsetLeft,
					marginRight = offLeft + offwdh + zkc.sumStyles("r", jq.margins),
					marginBottom = offTop + offhgh + zkc.sumStyles("b", jq.margins);
					
				var cwgt = _binds[c.id];
				//vertical size
				if (cwgt && cwgt._nvflex) {
					if (cwgt !== this)
						cwgt._flexFixed = true; //tell other vflex siblings I have done it.
					if (cwgt._vflex == 'min') {
						_setMinFlexSize(cwgt, c, 'height');
						//might change height in _setMinFlexSize(), so regain the value
						offTop = c.offsetTop;
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
				
				//horizontal size
				if (cwgt && cwgt._nhflex) {
					if (cwgt !== this)
						cwgt._flexFixed = true; //tell other hflex siblings I have done it.
					if (cwgt._hflex == 'min') {
						_setMinFlexSize(cwgt, c, 'width');
						//might change width in _setMinFlexSize(), so regain the value
						offLeft = c.offsetLeft;
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
				pretxt = false;
			}
		}

		if (zk.ie6_ && p.tagName == 'DIV') { //ie6, restore to orignial position style
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
			cwgt._vflexsize = vsz;
			lastsz -= vsz;
		}
		//last one with vflex
		if (vflexs.length) {
			var cwgt = vflexs.shift();
			cwgt.setFlexSize_({height:lastsz});
			cwgt._vflexsize = lastsz;
		}
		
		//setup the width for the hflex child
		//avoid floating number calculation error(TODO: shall distribute error evenly)
		lastsz = wdh > 0 ? wdh : 0;
		for (var j = hflexs.length - 1; j > 0; --j) {
			var cwgt = hflexs.shift(), //{n: node, f: hflex} 
				hsz = (cwgt._nhflex * wdh / hflexsz) | 0; //cast to integer
			cwgt.setFlexSize_({width:hsz});
			cwgt._hflexsize = hsz;
			lastsz -= hsz;
		}
		//last one with hflex
		if (hflexs.length) {
			var cwgt = hflexs.shift();
			cwgt.setFlexSize_({width:lastsz});
			cwgt._hflexsize = lastsz;
		}
		
		//notify parent widget that all of its children with vflex is done.
		this.parent.afterChildrenFlex_(this);
		this._flexFixed = false;
	}
	function _listenFlex(wgt) {
		if (!wgt._flexListened){
			zWatch.listen({onSize: [wgt, _fixFlex], onShow: [wgt, _fixFlex]});
			wgt._flexListened = true;
		}
	}
	function _unlistenFlex(wgt) {
		if (wgt._flexListened) {
			zWatch.unlisten({onSize: [wgt, _fixFlex], onShow: [wgt, _fixFlex]});
			delete wgt._flexListened;
		}
	}
	
	//Drag && Drop
	zk.DnD = { //for easy overriding
		getDrop: function (drag, pt, evt) {
			var wgt = evt.target;
			return wgt ? wgt.getDrop_(drag.control): null;
		},
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
		return drag.control.ingoreDrag_(pt);
	}

zk.Widget = zk.$extends(zk.Object, {
	_visible: true,
	nChildren: 0,
	bindLevel: -1,
	_mold: 'default',
	className: 'zk.Widget',

	$init: function (props) {
		this._asaps = {}; //event listened at server
		this._lsns = {}; //listeners(evtnm,listener)
		this._bklsns = {}; //backup for listners by setListeners
		this._subnodes = {}; //store sub nodes for widget(domId, domNode)

		this.$afterInit(function () {
			if (props) {
				var mold = props.mold;
				if (mold != null) {
					if (mold) this._mold = mold;
					delete props.mold; //avoid setMold being called
				}
				for (var nm in props)
					this.set(nm, props[nm]);
			}

			if (zk.spaceless) {
				if (this.id) this.uuid = this.id; //setId was called
				else if (this.uuid) this.id = this.uuid;
				else this.uuid = this.id = zk.Widget.nextUuid();
			} else if (!this.uuid) this.uuid = zk.Widget.nextUuid();
		});
	},

	$define: {
		mold: function () {
			this.rerender();
		},
		style: function () {
			this.updateDomStyle_();
		},
		sclass: function () {
			this.updateDomClass_();
		},
		zclass: function (){
			this.rerender();
		},
		width: function (v) {
			if (!this._nhflex) {
				var n = this.$n();
				if (n) n.style.width = v || '';
			}
		},
		height: function (v) {
			if (!this._nvflex) {
				var n = this.$n();
				if (n) n.style.height = v || '';
			}
		},
		left: function (v) {
			var n = this.$n();
			if (n) n.style.left = v || '';
		},
		top: function (v) {
			var n = this.$n();
			if (n) n.style.top = v || '';
		},
		tooltiptext: function (v) {
			var n = this.$n();
			if (n) n.title = v || '';
		},

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
		vflex: function(v) {
			this._nvflex = (true === v || 'true' == v) ? 1 : v == 'min' ? -65500 : zk.parseInt(v);
			if (this._nvflex < 0 && v != 'min')
				this._nvflex = 0;
			if (_binds[this.uuid] === this) { //if already bind
				if (!this._nvflex) {
					this.setFlexSize_({height: ''}); //clear the height
					delete this._vflexsize;
					if (!this._nhflex)
						_unlistenFlex(this);
				} else
					_listenFlex(this);
				zWatch.fireDown('onSize', this.parent);
			}
		},
		hflex: function(v) {
			this._nhflex = (true === v || 'true' == v) ? 1 : v == 'min' ? -65500 : zk.parseInt(v);
			if (this._nhflex < 0 && v != 'min')
				this._nhflex = 0; 
			if (_binds[this.uuid] === this) { //if already bind
				if (!this._nhflex) {
					this.setFlexSize_({width: ''}); //clear the width
					delete this._hflexsize;
					if (!this._nvflex)
						_unlistenFlex(this);
				} else
					_listenFlex(this);
				zWatch.fireDown('onSize', this.parent);
			}
		}
	},
	$o: _zkf = function () {
		for (var w = this; w; w = w.parent)
			if (w._fellows) return w;
	},
	getSpaceOwner: _zkf,
	$f: _zkf = function (id, global) {
		var f = this.$o();
		for (var ids = id.split('/'), j = 0, len = ids.length; j < len; ++j) {
			id = ids[j];
			if (id) {
				if (f) f = f._fellows[id];
				if (!f && global) f = _globals[id];
				if (!f || zk.spaceless) break;
				global = false;
			}
		}
		return f;
	},
	getFellow: _zkf,
	getId: function () {
		return this.id;
	},
	setId: function (id) {
		if (id != this.id) {
			if (zk.spaceless && this.desktop)
				throw 'id cannot be changed after bound'; //since there might be subnodes

			var old = this.id;
			if (old) {
				if (!zk.spaceless) delete _globals[id];
				_rmIdSpace(this);
			}

			this.id = id;
			if (zk.spaceless) this.uuid = id;

			if (id) {
				if (!zk.spaceless) _globals[id] = this;
				_addIdSpace(this);
			}
		}
		return this;
	},

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
			this._setListener(name, value);
		else if (arguments.length >= 3)
			zk.set(this, name, value, extra);
		else
			zk.set(this, name, value);
		return this;
	},
	getChildAt: function (j) {
		if (j >= 0)
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (--j < 0)
					return w;
	},
	getChildIndex: function () {
		var w = this.parent, j = 0;
		if (w)
			for (w = w.firstChild; w; w = w.nextSibling, ++j)
				if (w == this)
					return j;
		return 0;
	},
	setChildren: function (children) {
		if (children)
			for (var j = 0, l = children.length; j < l;)
				this.appendChild(children[j++]);
		return this;
	},
	appendChild: function (child) {
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

		if (this.z_rod || child.z_rod)
			_bindrod(child);
		else {
			var dt = this.desktop;
			if (dt) this.insertChildHTML_(child, null, dt);
		}

		this.onChildAdded_(child);
		return true;
	},
	insertBefore: function (child, sibling) {
		if (!sibling || sibling.parent != this)
			return this.appendChild(child);

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

		if (this.z_rod || child.z_rod)
			_bindrod(child);
		else {
			var dt = this.desktop;
			if (dt) this.insertChildHTML_(child, sibling, dt);
		}

		this.onChildAdded_(child);
		return true;
	},
	removeChild: function (child) {
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
			this.removeChildHTML_(child, p);
		this.onChildRemoved_(child);
		return true;
	},
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
	clear: function () {
		while (this.lastChild)
			this.removeChild(this.lastChild);
	},
	_replaceWgt: function (newwgt) { //called by au's outer
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

		if (p) {
			p.onChildRemoved_(this);
			p.onChildAdded_(newwgt);
		}
		//avoid memory leak
		this.parent = this.nextSibling = this.previousSibling
			= this._node = this._nodeSolved = null;
		this._subnodes = {};
	},
	beforeParentChanged_: function () {
	},

	isRealVisible: function (opts) {
		var dom = opts && opts.dom;
		for (var wgt = this; wgt; wgt = wgt.parent) {
			if (dom) {
				if (!zk(wgt.$n()).isVisible())
					return false;
			} else if (!wgt.isVisible())
				return false;
			if (hiddenByParent(wgt))
				return false;
			if (opts && opts.until == wgt)
				break;
		}
		return true;
	},
	isVisible: function (strict) {
		var visible = this._visible;
		if (!strict || !visible)
			return visible;
		var n = this.$n();
		return !n || zk(n).isVisible();
	},
	setVisible: function (visible) {
		if (this._visible != visible) {
			this._visible = visible;

			var p = this.parent;
			if (p && visible) p.onChildVisible_(this, true); //becoming visible
			if (this.desktop) this._setVisible(visible);
			if (p && !visible) p.onChildVisible_(this, false); //become invisible
		}
		return this;
	},
	show: function () {this.setVisible(true);},
	hide: function () {this.setVisible(false);},
	_setVisible: function (visible) {
		var parent = this.parent,
			parentVisible = !parent || parent.isRealVisible(),
			node = this.$n(),
			floating = this._floating;

		if (!parentVisible) {
			if (!floating) this.setDomVisible_(node, visible);
			return;
		}

		if (visible) {
			var zi;
			if (floating)
				this._setZIndex(zi = this._topZIndex(), true);

			this.setDomVisible_(node, true);

			//from parent to child
			for (var j = 0, fl = _floatings.length; j < fl; ++j) {
				var w = _floatings[j].widget,
					n = _floatings[j].node;
				if (this == w)
					w.setDomVisible_(n, true, {visibility:1});
				else if (this._floatVisibleDependent(w)) {
					zi = zi >= 0 ? ++zi: w._topZIndex();
					if (n != w.$n()) w.setFloatZIndex_(n, zi); //only a portion
					else w._setZIndex(zi, true);

					w.setDomVisible_(n, true, {visibility:1});
				}
			}

			zWatch.fireDown('onShow', this);
		} else {
			zWatch.fireDown('onHide', this);

			for (var j = _floatings.length, bindLevel = this.bindLevel; j--;) {
				var w = _floatings[j].widget;
				if (bindLevel >= w.bindLevel)
					break; //skip non-descendant (and this)
				if (this._floatVisibleDependent(w))
					w.setDomVisible_(_floatings[j].node, false, {visibility:1});
			}

			this.setDomVisible_(node, false);
		}
	},
	/** Returns if the specified widget's visibility depends this widget. */
	_floatVisibleDependent: function (wgt) {
		for (; wgt; wgt = wgt.parent)
			if (wgt == this) return true;
			else if (!wgt.isVisible()) break;
		return false;
	},
	setDomVisible_: function (n, visible, opts) {
		if (!opts || opts.display)
			n.style.display = visible ? '': 'none';
		if (opts && opts.visibility)
			n.style.visibility = visible ? 'visible': 'hidden';
	},
	onChildAdded_: function (/*child*/) {
	},
	onChildRemoved_: function (/*child*/) {
	},
	onChildVisible_: function (/*child, visible*/) {
	},
	setTopmost: function () {
		if (!this.desktop) return -1;

		for (var wgt = this; wgt; wgt = wgt.parent)
			if (wgt._floating) {
				var zi = wgt._topZIndex();
				wgt._setZIndex(zi, true);

				for (var j = 0, fl = _floatings.length; j < fl; ++j) { //parent first
					var w = _floatings[j].widget;
					if (wgt != w && zUtl.isAncestor(wgt, w) && w.isVisible()) {
						var n = _floatings[j].node;
						if (n != w.$n()) w.setFloatZIndex_(n, ++zi); //only a portion
						else w._setZIndex(++zi, true);
					}
				}
				return zi;
			}
		return -1;
	},
	/** Returns the topmost z-index for this widget.*/
	_topZIndex: function () {
		var zi = 1800; // we have to start from 1800 depended on all the css files.
		for (var j = _floatings.length; j--;) {
			var w = _floatings[j].widget;
			if (w._zIndex >= zi && !zUtl.isAncestor(this, w) && w.isVisible())
				zi = w._zIndex + 1;
		}
		return zi;
	},
	isFloating_: function () {
		return this._floating;
	},
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

	getZIndex: _zkf = function () {
		return this._zIndex;
	},
	getZindex: _zkf,
	setZIndex: _zkf = function (zIndex) {
		return this._setZIndex(zIndex);
	},
	setZindex: _zkf,
	_setZIndex: function (zIndex, fire) {
		if (this._zIndex != zIndex) {
			this._zIndex = zIndex;
			var n = this.$n();
			if (n) {
				n.style.zIndex = zIndex = zIndex >= 0 ? zIndex: '';
				if (fire) this.fire('onZIndex', zIndex, {ignorable: true});
			}
		}
		return this;
	},

	getScrollTop: function () {
		var n = this.$n();
		return n ? n.scrollTop: 0;
	},
	getScrollLeft: function () {
		var n = this.$n();
		return n ? n.scrollLeft: 0;
	},
	setScrollTop: function (val) {
		var n = this.$n();
		if (n) n.scrollTop = val;
		return this;
	},
	setScrollLeft: function (val) {
		var n = this.$n();
		if (n) n.scrollLeft = val;
		return this;
	},
	scrollIntoView: function () {
		zk(this.$n()).scrollIntoView();
		return this;
	},

	redraw: function (out) {
		var s = this.prolog;
		if (s) out.push(s);

		for (var p = this, mold = this._mold; p; p = p.superclass) {
			var f = p.$class.molds[mold];
			if (f) return f.apply(this, arguments);
		}
		throw "mold "+mold+" not found in "+this.className;
	},
	updateDomClass_: function () {
		if (this.desktop) {
			var n = this.$n();
			if (n) n.className = this.domClass_();
		}
	},
	updateDomStyle_: function () {
		if (this.desktop) {
			var s = jq.parseStyle(this.domStyle_());
			zk(this.$n()).setStyles(s);

			var n = this.getTextNode();
			if (n) zk(n).css(jq.filterTextStyle(s));
		}
	},
	getTextNode: function () {
	},

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
	domAttrs_: function (no) {
		var html = !no || !no.id ? ' id="' + this.uuid + '"': '';
		if (!no || !no.domStyle) {
			var s = this.domStyle_(no);
			if (s) html += ' style="' + s + '"';
		}
		if (!no || !no.domclass) {
			var s = this.domClass_();
			if (s) html += ' class="' + s + '"';
		}
		if (!no || !no.tooltiptext) {
			var s = this._tooltiptext;
			if (s) html += ' title="' + s + '"';
		}
		return html;
	},
	domTextStyleAttr_: function () {
		var s = this.getStyle();
		if (s) {
			s = jq.filterTextStyle(s);
			if (s) s = ' style="' + s + '"';
		}
		return s;
	},

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
			var oldwgt = zk.Widget.$(n, {exact:true});
			if (oldwgt) oldwgt.unbind(skipper); //unbind first (w/o removal)
			jq(n).replaceWith(this._redrawHTML(skipper, true));
			this.bind(desktop, skipper);
		}

		if (!skipper) {
			zWatch.fireDown('beforeSize', this);
			zWatch.fireDown('onSize', this);
		}

		if (cf && !zk.currentFocus) cf.focus();
		return this;
	},
	_redrawHTML: function (skipper, noprolog) {
		var out = [];
		this.redraw(out, skipper);
		if (noprolog && this.prolog && out[0] == this.prolog)
			out[0] = '';
			//Don't generate this.prolog if it is the one to re-render;
			//otherwise, prolog will be generated twice if invalidated
			//test: <div> <button onClick="self.invalidate()"/></div>
		return out.join('');
	},
	rerender: function (skipper) {
		if (this.desktop) {
			var n = this.$n();
			if (n) {
				if (skipper) {
					var skipInfo = skipper.skip(this);
					if (skipInfo) {
						this.replaceHTML(n, null, skipper);

						skipper.restore(this, skipInfo);

						zWatch.fireDown('beforeSize', this);
						zWatch.fireDown('onSize', this);
						return this; //done
					}
				}
				this.replaceHTML(n);
			}
		}
		return this;
	},

	replaceChildHTML_: function (child, n, desktop, skipper) {
		var oldwgt = zk.Widget.$(n, {exact:true});
		if (oldwgt) oldwgt.unbind(skipper); //unbind first (w/o removal)
		jq(n).replaceWith(child._redrawHTML(skipper, true));
		child.bind(desktop, skipper);
	},
	insertChildHTML_: function (child, before, desktop) {
		var bfn, ben;
		if (before) {
			bfn = before._getBeforeNode();
			if (!bfn) before = null;
		}
		if (!before)
			for (var w = this;;) {
				ben = w.getCaveNode();
				if (ben) break;

				var w2 = w.nextSibling;
				if (w2) {
					bfn = w2._getBeforeNode();
					if (bfn) break;
				}

				if (!(w = w.parent)) {
					ben = document.body;
					break;
				}
			}

		if (bfn) {
			var sib = bfn.previousSibling;
			if (_isProlog(sib)) bfn = sib;
			jq(bfn).before(child._redrawHTML());
		} else
			jq(ben).append(child._redrawHTML());
		child.bind(desktop);
	},
	getCaveNode: function () {
		return this.$n('cave') || this.$n();
	},
	_getBeforeNode: function () {
		for (var w = this; w; w = w.nextSibling) {
			var n = w._getFirstNodeDown();
			if (n) return n;
		}
	},
	_getFirstNodeDown: function () {
		var n = this.$n();
		if (n) return n;
		for (var w = this.firstChild; w; w = w.nextSibling) {
			n = w._getFirstNodeDown();
			if (n) return n;
		}
	},
	removeChildHTML_: function (child, prevsib) {
		var cf = zk.currentFocus;
		if (cf && zUtl.isAncestor(child, cf))
			zk.currentFocus = null;

		var n = child.$n();
		if (n) {
			var sib = n.previousSibling;
			if (child.prolog && _isProlog(sib))
				jq(sib).remove();
		} else
			child._prepareRemove(n = []);

		child.unbind();

		jq(n).remove();
	},
	_prepareRemove: function (ary) {
		for (var w = this.firstChild; w; w = w.nextSibling) {
			var n = w.$n();
			if (n) ary.push(n);
			else w._prepareRemove(ary);
		}
	},
	$n: _zkf = function (name) {
		if (name) {
			var n = this._subnodes[name];
			if (!n && this.desktop)
				n = this._subnodes[name] = jq(this.uuid + '-' + name, zk)[0];
			return n;
		}
		var n = this._node;
		if (!n && this.desktop && !this._nodeSolved) {
			this._node = n = jq(this.uuid, zk)[0];
			this._nodeSolved = true;
		}
		return n;
	},
	getNode: _zkf,
	getPage: function () {
		if (this.desktop && this.desktop.nChildren == 1)
			return this.desktop.firstChild;
			
		for (var page = this.parent; page; page = page.parent)
			if (page.$instanceof(zk.Page))
				return page;
				
		return null;
	},
	bind: function (desktop, skipper) {
		var after = [];
		this.bind_(desktop, skipper, after);
		for (var j = 0, len = after.length; j < len;)
			after[j++]();
		return this;
	},
	unbind: function (skipper) {
		var after = [];
		this.unbind_(skipper, after);
		for (var j = 0, len = after.length; j < len;)
			after[j++]();
		return this;
	},

	bind_: function (desktop, skipper, after) {
		if (this.z_rod) {
			_bindrod(this);
			return;
		}

		_bind0(this);

		if (!desktop) desktop = zk.Desktop.$(this.uuid);
		this.desktop = desktop;

		var p = this.parent;
		this.bindLevel = p ? p.bindLevel + 1: 0;

		if (this._draggable) this.initDrag_();
		
		if (this._nvflex || this._nhflex)
			_listenFlex(this);

		for (var child = this.firstChild; child; child = child.nextSibling)
			if (!skipper || !skipper.skipped(this, child))
				child.bind_(desktop, null, after); //don't pass skipper

		_onBind(this);
	},

	unbind_: function (skipper, after) {
		if (this.z_rod) {
			_unbindrod(this);
			return;
		}

		_unbind0(this);
		_fixBindMem();
		_unlistenFlex(this);

		for (var child = this.firstChild; child; child = child.nextSibling)
			if (!skipper || !skipper.skipped(this, child))
				child.unbind_(null, after); //don't pass skipper

		if (this._draggable) this.cleanDrag_();

		_onUnbind(this);
	},
	extraBind_: function (id, add) {
		if (add == false) delete _binds[id];
		else _binds[id] = this;
	},
	setFlexSize_: function(sz) {
		var n = this.$n();
		if (sz.height !== undefined) {
			if (sz.height == 'auto')
				n.style.height = '';
			else if (sz.height != '')
				n.style.height = jq.px(zk(n).revisedHeight(sz.height, true));
			else
				n.style.height = this._height ? this._height : '';
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto')
				n.style.width = '';
			else if (sz.width != '')
				n.style.width = jq.px(zk(n).revisedWidth(sz.width, true));
			else
				n.style.width = this._width ? this._width : '';
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
	getParentSize_: function(p) {
		//to be overridden
		var zkp = zk(p);
		return zkp ? {height: zkp.revisedHeight(p.offsetHeight), width: zkp.revisedWidth(p.offsetWidth)} : {};
	},
	fixFlex_: function() {
		_fixFlex.apply(this);
	},
	initDrag_: function () {
		this._drag = new zk.Draggable(this, this.getDragNode(), zk.copy({
			starteffect: zk.$void, //see bug #1886342
			endeffect: DD_enddrag, change: DD_dragging,
			ghosting: DD_ghosting, endghosting: DD_endghosting,
			constraint: DD_constraint,
			ignoredrag: DD_ignoredrag,
			zIndex: 88800
		}, this.getDragOptions_()));
	},
	cleanDrag_: function () {
		var drag = this._drag;
		if (drag) {
			this._drag = null;
			drag.destroy();
		}
	},
	getDragNode: function () {
		return this.$n();
	},
	getDragOptions_: function () {
	},
	ingoreDrag_: function (pt) {
		return false;
	},
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
	dropEffect_: function (over) {
		jq(this.$n()||[])[over ? "addClass" : "removeClass"]("z-drag-over");
	},
	getDragMessage_: function () {
		var tn = this.getDragNode().tagName;
		if ("TR" == tn || "TD" == tn || "TH" == tn) {
			var n = this.$n('real') || this.getCaveNode();
			return n ? n.textContent || n.innerText || '': '';
		}
	},
	onDrop_: function (drag, evt) {
		var data = zk.copy({dragged: drag.control}, evt.data);
		this.fire('onDrop', data, null, 38);
	},
	cloneDrag_: function (drag, ofs) {
		//See also bug 1783363 and 1766244

		var msg = this.getDragMessage_();
		if (typeof msg == 'string' && msg.length > 15)
			msg = msg.substring(0, 15) + "...";

		var dgelm = zk.DnD.ghost(drag, ofs, msg);

		drag._orgcursor = document.body.style.cursor;
		document.body.style.cursor = "pointer";
		jq(this.getDragNode()).addClass('z-dragged'); //after clone
		return dgelm;
	},
	uncloneDrag_: function (drag) {
		document.body.style.cursor = drag._orgcursor || '';

		jq(this.getDragNode()).removeClass('z-dragged');
	},

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
	canActivate: function (opts) {
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
	smartUpdate: function (nm, val, timeout) {
		zAu.send(new zk.Event(this, 'setAttr', [nm, val]),
			timeout >= 0 ? timeout: -1);
		return this;
	},

	//widget event//
	fireX: function (evt, timeout) {
		evt.currentTarget = this;
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
					zAu.sendAhead(_cloneEvt(evt, this), timeout >= 0 ? timeout : 38);
					//since evt will be used later, we have to make a copy and use this as target
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
						zAu.send(_cloneEvt(evt, this), asap ? timeout >= 0 ? timeout : 38 : -1);
						//since evt will be used later, we have to make a copy and use this as target
				}
			}
		}
		return evt;
	},
	fire: function (evtnm, data, opts, timeout) {
		return this.fireX(new zk.Event(this, evtnm, data, opts), timeout);
	},
	listen: function (infs, priority) {
		priority = priority ? priority: 0;
		for (var evt in infs) {
			var inf = infs[evt];
			if (inf.$array) inf = [inf[0]||this, inf[1]];
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
	unlisten: function (infs) {
		l_out:
		for (var evt in infs) {
			var inf = infs[evt],
				lsns = this._lsns[evt], lsn;
			for (var j = lsns ? lsns.length: 0; j--;) {
				lsn = lsns[j];
				if (inf.$array) inf = [inf[0]||this, inf[1]];
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
	setListeners: function (infs) {
		for (var evt in infs)
			this._setListener(evt, infs[evt]);
	},
	setListener: function (inf) { //used by server
		this._setListener(inf[0], inf[1]);
	},
	_setListener: function (evt, fn) {
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
	setOverrides: function (infs) { //used by server
		for (var nm in infs) {
			var val = infs[nm];
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
		}
	},

	//ZK event handling//
	doClick_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doClick_(evt);
		}	
	},
	doDoubleClick_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doDoubleClick_(evt);
		}
	},
	doRightClick_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doRightClick_(evt);
		}
	},
	doMouseOver_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOver_(evt);
		}
	},
	doMouseOut_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOut_(evt);
		}
	},
	doMouseDown_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseDown_(evt);
		}
	},
	doMouseUp_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseUp_(evt);
		}
	},
	doMouseMove_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseMove_(evt);
		}
	},
	doKeyDown_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyDown_(evt);
		}
	},
	doKeyUp_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyUp_(evt);
		}
	},
	doKeyPress_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyPress_(evt);
		}
	},

	doFocus_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doFocus_(evt);
		}
	},
	doBlur_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doBlur_(evt);
		}
	},

	//DOM event handling//
	domListen_: function (n, evtnm, fn) {
		if (!this.$weave) {
			var inf = _domEvtInf(this, evtnm, fn);
			jq(n, zk).bind(inf[0], inf[1]);
		}
		return this;
	},
	domUnlisten_: function (n, evtnm, fn) {
		if (!this.$weave) {
			var inf = _domEvtInf(this, evtnm, fn);
			jq(n, zk).unbind(inf[0], inf[1]);
		}
		return this;
	},
	toJSON: function () {
		return this.uuid;
	}

}, {
	$: function (n, opts) {
		if (typeof n == 'string') {
			if (n.charAt(0) == '#') n = n.substring(1);
			var j = n.indexOf('-');
			return _binds[j >= 0 ? n.substring(0, j): n];
		}

		if (!n || zk.Widget.isInstance(n)) return n;
		else if (!n.nodeType) { //skip Element
			var e = n.originalEvent;
			n = (e?e.z$target:null) || n.target || n; //check DOM event first
		}

		for (; n; n = zk(n).vparentNode()||n.parentNode) {
			var id = n.id;
			if (id) {
				var j = id.indexOf('-');
				if (j >= 0) {
					id = id.substring(0, j);
					if (opts && opts.child) {
						var wgt = _binds[id];
						if (wgt) {
							var n2 = wgt.$n();
							if (n2 && jq.isAncestor(n2, n)) return wgt;
						}
						if (opts && opts.exact) break;
						continue;
					}
				}
				wgt = _binds[id];
				if (wgt) return wgt;
			}
			if (opts && opts.exact) break;
		}
		return null;
	},

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
			if (wgt) zWatch.fire('onFloatUp', wgt); //notify all
		}
	},

	//uuid//
	uuid: function (id) {
		var uuid = typeof id == 'object' ? id.id || '' : id,
			j = uuid.indexOf('-');
		return j >= 0 ? uuid.substring(0, j): id;
	},
	nextUuid: function () {
		return '_z_' + _nextUuid++;
	},

	isAutoId: function (id) {
		return !id || id.startsWith('_z_') || id.startsWith('z_');
	},

	register: function (clsnm, blankprev) {
		var cls = zk.$import(clsnm);
		cls.prototype.className = clsnm;
		var j = clsnm.lastIndexOf('.');
		if (j >= 0) clsnm = clsnm.substring(j + 1);
		_wgtcls[clsnm.substring(0,1).toLowerCase()+clsnm.substring(1)] = cls;
		if (blankprev) cls.prototype.blankPreserved = true;
	},
	getClass: function (wgtnm) {
		return _wgtcls[wgtnm];
	},
	newInstance: function (wgtnm, opts) {
		var cls = _wgtcls[wgtnm];
		if (!cls)
			throw 'widget not found: '+wgtnm;
		return new cls(opts);
	}
});

zk.RefWidget = zk.$extends(zk.Widget, {
	bind_: function () {
		var w = zk.Widget.$(this.uuid);
		if (!w || !w.desktop) throw 'illegal: '+w;

		var p = w.parent, q;
		if (p) { //shall be a desktop
			var dt = w.desktop, n = w._node;
			w.desktop = w._node = null; //avoid unbind/bind
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
})();

zk.Page = zk.$extends(zk.Widget, {//unlik server, we derive from Widget!
	_style: "width:100%;height:100%",
	className: 'zk.Page',

	$init: function (props, contained) {
		this._fellows = {};

		this.$super('$init', props);

		if (contained) zk.Page.contained.push(this);
	},
	redraw: function (out) {
		out.push('<div', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div>');
	}
},{
	contained: []
});
zk.Widget.register('zk.Page', true);

zk.Desktop = zk.$extends(zk.Widget, {
	bindLevel: 0,
	className: 'zk.Desktop',

	$init: function (dtid, contextURI, updateURI, stateless) {
		this.$super('$init', {uuid: dtid}); //id also uuid

		this._aureqs = [];
		//Sever side effect: this.desktop = this;

		var Desktop = zk.Desktop, dts = Desktop.all, dt = dts[dtid];
		if (!dt) {
			this.uuid = this.id = dtid;
			this.updateURI = updateURI || zk.updateURI;
			this.contextURI = contextURI || zk.contextURI;
			this.stateless = stateless;
			dts[dtid] = this;
			++Desktop._ndt;
			if (!Desktop._dt) Desktop._dt = this; //default desktop
		} else {
			if (updateURI) dt.updateURI = updateURI;
			if (contextURI) dt.contextURI = contextURI;
		}

		Desktop.sync();
	},
	_exists: function () {
		var id = this._pguid; //_pguid not assigned at beginning
		return !id || jq(id, zk)[0];
	},
	bind_: zk.$void,
	unbind_: zk.$void,
	setId: zk.$void
},{
	$: function (dtid) {
		var Desktop = zk.Desktop, dts = Desktop.all, w;
		if (Desktop._ndt > 1) {
			if (typeof dtid == 'string') {
				w = dts[dtid];
				if (w) return w;
			}
			w = zk.Widget.$(dtid);
			if (w)
				for (; w; w = w.parent) {
					if (w.desktop)
						return w.desktop;
					if (w.$instanceof(Desktop))
						return w;
				}
		}
		if (w = Desktop._dt) return w;
		for (dtid in dts)
			return dts[dtid];
	},
	all: {},
	_ndt: 0,
	sync: function () {
		var Desktop = zk.Desktop, dts = Desktop.all;
		if (Desktop._dt && !Desktop._dt._exists()) //removed
			Desktop._dt = null;
		for (var dtid in dts) {
			var dt = dts[dtid];
			if (!dt._exists()) { //removed
				delete dts[dtid];
				--Desktop._ndt;
			} else if (!Desktop._dt)
				Desktop._dt = dt;
		}
		return Desktop._dt;
	}
});

zk.Skipper = zk.$extends(zk.Object, {
	skipped: function (wgt, child) {
		return wgt.caption != child;
	},
	skip: function (wgt, skipId) {
		var skip = jq(skipId || (wgt.uuid + '-cave'), zk)[0];
		if (skip && skip.firstChild) {
			skip.parentNode.removeChild(skip);
				//don't use jq to remove, since it unlisten events
			return skip;
		}
		return null;
	},
	restore: function (wgt, skip) {
		if (skip) {
			var loc = jq(skip.id, zk)[0];
			for (var el; el = skip.firstChild;) {
				skip.removeChild(el);
				loc.appendChild(el);
			}
		}
	}
});
zk.Skipper.nonCaptionSkipper = new zk.Skipper();

zk.Native = zk.$extends(zk.Widget, {
	className: 'zk.Native',

	redraw: function (out) {
		var s = this.prolog;
		if (s) {
			if (zk.ie) this._patchScript(out, s);
			out.push(s);
		}

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		s = this.epilog;
		if (s) out.push(s);
	}
});

//pacth IE7 bug: script ignored if it is the first child (script2.zul)
if (zk.ie)
	zk.Native.prototype._patchScript = function (out, s) {
		var j;
		if (this.previousSibling || s.indexOf('<script') < 0
		|| (j = out.length) > 20)
			return;
		for (var cnt = 0; j--;)
			if (out[j].indexOf('<') >= 0 && ++cnt > 1)
				return; //more than one
	 	out.push('<span style="display:none;font-size:0">&#160;</span>');
	};

zk.Macro = zk.$extends(zk.Widget, {
	className: 'zk.Macro',

	redraw: function (out) {
		out.push('<span', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</span>');
	}
});
