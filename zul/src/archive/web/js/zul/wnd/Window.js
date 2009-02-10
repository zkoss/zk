/* Window.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 17 17:52:31     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zPkg.load('zul.wgt');
zul.wnd.Window = zk.$extends(zul.Widget, {
	_mode: 'embedded',
	_border: 'none',
	_minheight: 100,
	_minwidth: 200,
	_zIndex: 0,

	$init: function () {
		this._fellows = {};

		this.$supers('$init', arguments);

		this.listen('onClose', this, null, -1000);
		this.listen('onMove', this, null, -1000);
		this.listen('onZIndex', this, null, -1000);
		this._skipper = new zul.wnd.Skipper(this);
	},

	getMode: function () {
		return this._mode;
	},
	setMode: function (mode) {
		if (this._mode != mode) {
			this._mode = mode;
			this._updateDomOuter();
		}
	},
	doOverlapped: function () {
		this.setMode('overlapped');
	},
	doPopup: function () {
		this.setMode('popup');
	},
	doHighlighted: function () {
		this.setMode('highlighted');
	},
	doModal: function () {
		this.setMode('modal');
	},
	doEmbedded: function () {
		this.setMode('embedded');
	},

	_doOverlapped: function () {
		var pos = this.getPosition(),
			n = this.getNode();
		if (!pos && !n.style.top && !n.style.left) {
			var xy = zDom.revisedOffset(n);
			n.style.left = xy[0] + "px";
			n.style.top = xy[1] + "px";
		} else if (pos == "parent")
			this._posByParent();

		zDom.makeVParent(n);
		this._syncShadow();
		this._updateDomPos();

		if (this.isRealVisible()) {
			zDom.cleanVisibility(n);
			this.setTopmost();
		}

		this._makeFloat();
	},
	_doModal: function () {
		var pos = this.getPosition(),
			n = this.getNode();
		if (pos == "parent") this._posByParent();

		zDom.makeVParent(n);
		this._syncShadow();
		this._updateDomPos(true);

		if (!pos) { //adjust y (to upper location)
			var top = zk.parseInt(n.style.top), y = zDom.innerY();
			if (y) {
				var y1 = top - y;
				if (y1 > 100) n.style.top = top - (y1 - 100) + "px";
			} else if (top > 100)
				n.style.top = "100px";
		}

		//Note: modal must be visible
		var realVisible = this.isRealVisible();
		if (realVisible) {
			zDom.cleanVisibility(n);
			this.setTopmost();
		}
		this._syncMask();

		this._mask = new zk.eff.FullMask({
			id: this.uuid + "$mask",
			anchor: this._shadow.getBottomElement(),
				//bug 1510218: we have to make it as a sibling
			zIndex: this._zIndex,
			stackup: zk.ie6Only,
			visible: realVisible});

		if (realVisible) {
			this._prevmodal = zk.currentModal;
			var modal = zk.currentModal = this;
			this._prevfocus = zk.currentFocus; //store
			this.focus(0);
		}

		this._makeFloat();
	},
	/** Must be called before calling makeVParent. */
	_posByParent: function () {
		var n = this.getNode(),
			ofs = zDom.revisedOffset(n.parentNode),
			left = zk.parseInt(n.style.left), top = zk.parseInt(n.style.top);
		this._offset = ofs;
		n.style.left = ofs[0] + zk.parseInt(n.style.left) + "px";
		n.style.top = ofs[1] + zk.parseInt(n.style.top) + "px";
	},
	_syncShadow: function () {
		if (this._mode == 'embedded') {
			if (this._shadow) {
				this._shadow.destroy();
				this._shadow = null;
			}
		} else {
			if (!this._shadow)
				this._shadow = new zk.eff.Shadow(this.getNode(), {stackup:true});
			this._shadow.sync();
		}
	},
	_syncMask: function () {
		if (this._mask) this._mask.sync(this._shadow.getBottomElement());
	},
	_hideShadow: function () {
		var shadow = this._shadow;
		if (shadow) shadow.hide();
	},
	_makeFloat: function () {
		var handle = this.getSubnode('cap');
		if (handle && !this._drag) {
			handle.style.cursor = "move";
			var $Window = this.$class;
			this._drag = new zk.Draggable(this, null, {
				handle: handle, overlay: true, stackup: true,
				starteffect: $Window._startmove,
				ghosting: $Window._ghostmove,
				endghosting: $Window._endghostmove,
				ignoredrag: $Window._ignoremove,
				endeffect: $Window._aftermove});
		}
	},
	_updateDomPos: function (force) {
		var n = this.getNode(), pos = this._pos;
		if (pos == "parent"/*handled by the caller*/ || (!pos && !force))
			return;

		var st = n.style;
		st.position = "absolute"; //just in case
		var ol = st.left, ot = st.top;
		zDom.center(n, pos);
		var sdw = this._shadow;
		if (pos && sdw) {
			var d = sdw.getDelta(), l = n.offsetLeft, t = n.offsetTop, w = n.offsetWidth,
				h = n.offsetHeight; 
			if (pos.indexOf("left") >= 0 && d.l < 0) st.left = l - d.l + "px";
			else if (pos.indexOf("right") >= 0)
				st.left = l - (zk.ie ? Math.round((Math.abs(d.l) + d.w)/2) : Math.round((d.l + d.w)/2)) - 1 + "px";

			if (pos.indexOf("top") >= 0 && d.t < 0) st.top = t - d.t + "px";
			else if (pos.indexOf("bottom") >= 0)
				st.top = t - (zk.ie ? Math.round((Math.abs(d.t) + d.h)/2) + 1 : Math.round((d.t + d.h)/2)) - 1 + "px";
		}
		this._syncShadow();
		if (ol != st.left || ot != st.top)
			this._fireOnMove();
	},

	getTitle: function () {
		return this._title;
	},
	setTitle: function (title) {
		if (this._title != title) {
			this._title = title;
			this._updateDomOuter();
		}
	},
	getBorder: function () {
		return this._border;
	},
	setBorder: function (border) {
		if (!border || '0' == border)
			border = "none";
		if (this._border != border) {
			this._border = border;
			this._updateDomOuter();
		}
	},
	getPosition: function () {
		return this._pos;
	},
	setPosition: function (pos) {
		if (this._pos != pos) {
			this._pos = pos;

			if (this.desktop && this._mode != 'embedded') {
				this._updateDomPos(); //TODO: handle pos = 'parent'
			}
		}
	},

	getMinheight: function () {
		return this._minheight;
	},
	setMinheight: function (minheight) {
		if (this._minheight != minheight) {
			this._minheight = minheight;

			//TODO
		}
	},
	getMinwidth: function () {
		return this._minwidth;
	},
	setMinwidth: function (minwidth) {
		if (this._minwidth != minwidth) {
			this._minwidth = minwidth;

			//TODO
		}
	},

	isClosable: function () {
		return this._closable;
	},
	setClosable: function (closable) {
		if (this._closable != closable) {
			this._closable = closable;
			this._updateDomOuter();
		}
	},
	isSizable: function () {
		return this._sizable;
	},
	setSizable: function (sizable) {
		if (this._sizable != sizable) {
			this._sizable = sizable;
			this._updateDomOuter();
		}
	},
	isMaximized: function () {
		return this._maximized;
	},
	setMaximized: function (maximized) {
		if (this._maximized != maximized) {
			this._maximized = maximized;
			this._updateDomOuter();
		}
	},
	isMaximizable: function () {
		return this._maximizable;
	},
	setMaximizable: function (maximizable) {
		if (this._maximizable != maximizable) {
			this._maximizable = maximizable;
			this._updateDomOuter();
		}
	},
	isMinimized: function () {
		return this._minimized;
	},
	setMinimized: function (minimized) {
		if (this._minimized != minimized) {
			this._minimized = minimized;
			this._updateDomOuter();
		}
	},
	isMinimizable: function () {
		return this._minimizable;
	},
	setMinimizable: function (minimizable) {
		if (this._minimizable != minimizable) {
			this._minimizable = minimizable;
			this._updateDomOuter();
		}
	},

	getContentStyle: function () {
		return this._cntStyle;
	},
	setContentStyle: function (style) {
		if (this._cntStyle != style) {
			this._cntStyle = style;
			this._updateDomOuter();
		}
	},
	getContentSclass: function () {
		return this._cntSclass;
	},
	setContentSclass: function (sclass) {
		if (this._cntSclass != sclass) {
			this._cntSclass = sclass;
			this._updateDomOuter();
		}
	},

	_updateDomOuter: function () {
		this.rerender(this._skipper);
	},

	//event handler//
	onClose: function () {
		if (!this.inServer) //let server handle if in server
			this.parent.removeChild(this); //default: remove
	},
	onMove: function (evt) {
		this._left = evt.data[0];
		this._top = evt.data[1];
	},
	onZIndex: function (evt) {
		this._syncShadow();
		this._syncMask();
	},
	//watch//
	onSize: _zkf = function () {
		this._hideShadow();
		if (this.isMaximized()) {
			/** TODO 
			 * if (this.__maximized)
				this._syncMaximized();
			this.__maximized = false; // avoid deadloop
			*/
		}
		this._fixHgh();
		this._fixWdh();
		this._syncShadow();
	},
	onVisible: _zkf,
	onFloatUp: function (wgt) {
		if (!this.isVisible() || this._mode == 'embedded')
			return; //just in case

		if (this._mode == 'popup') {
			for (var floatFound; wgt; wgt = wgt.parent) {
				if (wgt == this) {
					if (!floatFound) this.setTopmost();
					return;
				}
				floatFound = floatFound || wgt.isFloating_();
			}
			this.setVisible(false);
			this.fire('onOpen', false);
		} else
			for (; wgt; wgt = wgt.parent) {
				if (wgt == this) {
					this.setTopmost();
					return;
				}
				if (wgt.isFloating_())
					return;
			}
	},
	_fixWdh: zk.ie7 ? function () {
		if (this._mode == 'embedded' || this._mode == 'popup') return;
		var n = this.getNode(),
			cm = this.getSubnode('cave').parentNode,
			wdh = n.style.width,
			tl = zDom.firstChild(n, "DIV"),
			bl = zDom.lastChild(zDom.lastChild(n, "DIV"), "DIV");
			
		if (!wdh || wdh == "auto") {
			var diff = zDom.frameWidth(cm.parentNode) + zDom.frameWidth(cm.parentNode.parentNode);
			if (tl) {
				tl.firstChild.firstChild.style.width = 
					Math.max(0, cm.offsetWidth - (zDom.frameWidth(tl) + zDom.frameWidth(tl.firstChild) - diff)) + "px";
			}
			if (bl) {
				bl.firstChild.firstChild.style.width =
					Math.max(0, cm.offsetWidth - (zDom.frameWidth(bl) + zDom.frameWidth(bl.firstChild) - diff)) + "px";
			}
		} else {
			if (tl) tl.firstChild.firstChild.style.width = "";
			if (bl) bl.firstChild.firstChild.style.width = "";
		}
	} : zk.$void,
	_fixHgh: function () {
		if (!this.isRealVisible()) return;
		var n = this.getNode(),
			cave = this.getSubnode('cave'),
			hgh = n.style.height;
		if (zk.ie6Only && ((hgh && hgh != "auto" )|| cave.style.height)) cave.style.height = "0px";
		if (hgh && hgh != "auto")
			cave.style.height = zDom.revisedHeight(cave, n.offsetHeight - this._frameHeight(n) - 1, true) + 'px';
	},
	_frameHeight: function (n) {
		var h = zDom.frameHeight(n) + this._titleHeight(n);
	    if (this._mode != 'embedded' && this._mode != 'popup') {
			var ft = zDom.lastChild(this.getSubnode('body'), "DIV"),
				title = this.getSubnode('cap'),
				cave = this.getSubnode('cave');
	        h += ft.offsetHeight;
			if (cave)
				h += zDom.frameHeight(cave.parentNode);
			if (title)
		        h += zDom.frameHeight(title.parentNode);
	    }
	    return h;
	},
	_titleHeight: function (n) {
		var cap = this.getSubnode('cap');
		return cap ? cap.offsetHeight : 
				this._mode != 'embedded' && this._mode != 'popup' ?
					zDom.firstChild(n, "DIV").firstChild.firstChild.offsetHeight : 0;
	},

	_fireOnMove: function (keys) {
		var pos = this._pos, node = this.getNode(),
			x = zk.parseInt(node.style.left),
			y = zk.parseInt(node.style.top);
		if (pos == 'parent') {
			var vparent = zDom.vparent(node);
			if (vparent) {
				var ofs = zDom.reviseOffset(vparent);
				x -= ofs[0];
				y -= ofs[1];
			}
		}
		this.fire('onMove', {
			x: x + 'px',
			y: y + 'px',
			keys: keys,
			marshal: zul.wnd.Window._onMoveMarshal
		}, {ignorable: true});
	},

	//super//
	setDomVisible_: function () {
		this.$supers('setDomVisible_', arguments);
		this._syncShadow();
		this._syncMask();
	},
	setZIndex: function () {
		this.$supers('setZIndex', arguments);
		this._syncShadow();
		this._syncMask();
	},
	focus: function (timeout) {
		if (this.desktop) {
			var cap = this.caption;
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (w != cap && w.focus(timeout))
					return true;
			return cap && cap.focus(timeout);
		}
		return false;
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-window-" + this._mode;
	},

	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.wgt.Caption))
			this.caption = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.caption)
			this.caption = null;
	},
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments),
			visible = this.isVisible();
		if ((!no || !no.visible) && visible && this.isMinimized())
			style = 'display:none;'+style;
		if (this._mode != 'embedded')
			style = (visible ? "position:absolute;visibility:hidden;" : "position:absolute;")
				+style;
		return style;
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		var $Window = this.$class;

		var mode = this._mode;
		zWatch.listen('onSize', this);
		zWatch.listen('onVisible', this);
		if (mode != 'embedded') {
			zWatch.listen('onFloatUp', this);
			this.setFloating_(true);

			if (mode == 'modal' || mode == 'highlighted') this._doModal();
			else this._doOverlapped();
		}
	},
	unbind_: function () {
		var node = this.getNode();
		node.style.visibility = 'hidden'; //avoid unpleasant effect

		//we don't check this._mode here since it might be already changed
		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = null;
		}
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}
		if (this._mask) {
			this._mask.destroy();
			this._mask = null;
		}
		
		zDom.undoVParent(node);
		zWatch.unlisten('onFloatUp', this);
		zWatch.unlisten('onSize', this);
		zWatch.unlisten('onVisible', this);
		this.setFloating_(false);

		if (zk.currentModal == this) {
			zk.currentModal = this._prevmodal;
			var prevfocus = this._prevfocus;
			if (prevfocus) prevfocus.focus(0);
			this._prevfocus = this._prevmodal = null;
		}

		var $Window = this.$class;
		for (var nms = ['close', 'max', 'min'], j = 3; --j >=0;) {
			var nm = nms[j],
				n = this['e' + nm ];
			if (n) {
				this['e' + nm ] = null;
				zEvt.unlisten(n, 'click', $Window[nm + 'click']);
				zEvt.unlisten(n, 'mouseover', $Window[nm + 'over']);
				zEvt.unlisten(n, 'mouseout', $Window[nm + 'out']);
			}
		}
		this.$supers('unbind_', arguments);
	},
	doClick_: function (evt) {
		switch (evt.nativeTarget) {
		case this.getSubnode('close'):
			this.fire('onClose');
			break;
		case this.getSubnode('max'):
			// TODO
			break;
		case this.getSubnode('min'):
			// TODO 
			// if (this.isMinimizable())
			//	this.setMinimized(!this.isMinimized());
			break;
		}
		evt.stop();
		this.$supers('doClick_', arguments);
	},
	doMouseOver_: function (evt) {
		switch (evt.nativeTarget) {
		case this.getSubnode('close'):
			zDom.addClass(this.getSubnode('close'), this.getZclass() + '-close-over');
			break;
		case this.getSubnode('max'):
			var zcls = this.getZclass(),
				added = this.isMaximized() ? ' ' + zcls + '-maximized-over' : '';
			zDom.addClass(this.getSubnode('max'), zcls + '-maximize-over' + added);
			break;
		case this.getSubnode('min'):
			zDom.addClass(this.getSubnode('min'), this.getZclass() + '-minimize-over');
			break;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		switch (evt.nativeTarget) {
		case this.getSubnode('close'):
			zDom.rmClass(this.getSubnode('close'), this.getZclass() + '-close-over');
			break;
		case this.getSubnode('max'):
			var zcls = this.getZclass(),
				max = this.getSubnode('max');
			if (this.isMaximized())
				zDom.rmClass(max, zcls + '-maximized-over');
			zDom.rmClass(max, zcls + '-maximize-over');
			break;
		case this.getSubnode('min'):
			zDom.rmClass(this.getSubnode('min'), this.getZclass() + '-minimize-over');
			break;
		}
		this.$supers('doMouseOut_', arguments);
	}
},{ //static
	_onMoveMarshal: function () {
		return [this.x, this.y, this.keys ? this.keys.marshal(): ''];
	},

	//drag
	_startmove: function (dg) {
		//Bug #1568393: we have to change the percetage to the pixel.
		var el = dg.node;
		if(el.style.top && el.style.top.indexOf("%") >= 0)
			 el.style.top = el.offsetTop + "px";
		if(el.style.left && el.style.left.indexOf("%") >= 0)
			 el.style.left = el.offsetLeft + "px";
		//TODO var real = $real(handle);
		//zkau.closeFloats(real, handle);
	},
	_ghostmove: function (dg, ofs, evt) {
		var wnd = dg.control,
			el = dg.node;
		wnd._hideShadow();
		var title = zDom.firstChild(el, "DIV"),
			fakeTitle = title.cloneNode(true);
		var html = '<div id="zk_wndghost" class="z-window-move-ghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zDom.offsetWidth(el)+'px;height:'+zDom.offsetHeight(el)
			+'px;z-index:'+el.style.zIndex+'"><ul></ul></div></div>';
		document.body.insertAdjacentHTML("afterBegin", html);
		dg._wndoffs = ofs;
		el.style.visibility = "hidden";
		var h = el.offsetHeight - title.offsetHeight;
		el = zDom.$("zk_wndghost");
		el.firstChild.style.height = zDom.revisedHeight(el.firstChild, h) + "px";
		el.insertBefore(fakeTitle, el.firstChild);
		return el;
	},
	_endghostmove: function (dg, origin) {
		var el = dg.node; //ghost
		origin.style.top = origin.offsetTop + el.offsetTop - dg._wndoffs[1] + "px";
		origin.style.left = origin.offsetLeft + el.offsetLeft - dg._wndoffs[0] + "px";

		document.body.style.cursor = "";
	},
	_ignoremove: function (dg, pointer, evt) {
		var el = dg.node,
			wgt = dg.control;
		switch (zEvt.target(evt)) {
			case wgt.getSubnode('close'):
			case wgt.getSubnode('max'):
			case wgt.getSubnode('min'):
				return true; //ignore special buttons
		}
		if (!wgt.isSizable()
		|| (el.offsetTop + 4 < pointer[1] && el.offsetLeft + 4 < pointer[0] 
		&& el.offsetLeft + el.offsetWidth - 4 > pointer[0]))
			return false; //accept if not sizable or not on border
		return true;
	},
	_aftermove: function (dg, evt) {
		dg.node.style.visibility = "";
		var wgt = dg.control;
		wgt._syncShadow();
		wgt._fireOnMove(zEvt.keyMetaData(evt));
	}
});

zul.wnd.Skipper = zk.$extends(zk.Skipper, {
	$init: function (wnd) {
		this._w = wnd;
	},
	restore: function () {
		this.$supers('restore', arguments);
		var w = this._w;
		if (w._mode != 'embedded') {
			w._updateDomPos(); //skipper's size is wrong in bind_
			w._syncShadow();
		}
	}
});
