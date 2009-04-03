/* Window.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 17 17:52:31     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zPkg.load('zul.wgt');
zul.wnd.Window = zk.$extends(zul.Widget, {
	_mode: 'embedded',
	_border: 'none',
	_minheight: 100,
	_minwidth: 200,

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
				this._shadow = new zk.eff.Shadow(this.getNode(),
					{left: -4, right: 4, top: -2, bottom: 3, stackup: true});
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
			var opts = sdw.opts, l = n.offsetLeft, t = n.offsetTop; 
			if (pos.indexOf("left") >= 0 && opts.left < 0)
				st.left = (l - opts.left) + "px";
			else if (pos.indexOf("right") >= 0 && opts.right > 0)
				st.left = (l - opts.right) + "px";
			if (pos.indexOf("top") >= 0 && opts.top < 0)
				st.top = (t - opts.top) + "px";
			else if (pos.indexOf("bottom") >= 0 && opts.bottom > 0)
				st.top = (t - opts.bottom) + "px";
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
		this._left = evt.left;
		this._top = evt.top;
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
			 * if (this._maximized)
				this._syncMaximized();
			this._maximized = false; // avoid deadloop
			*/
		}
		this._fixHgh();
		this._fixWdh();
		this._syncShadow();
	},
	onShow: _zkf,
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
			this.fire('onOpen', {open:false});
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
		if (this._mode == 'embedded' || this._mode == 'popup' || !this.isRealVisible()) return;
		var n = this.getNode(),
			cave = this.getSubnode('cave').parentNode,
			wdh = n.style.width,
			tl = zDom.firstChild(n, "DIV"),
			hl = tl && this.getSubnode("cap") ? zDom.nextSibling(tl, "DIV") : null,
			bl = zDom.lastChild(n, "DIV");
			
		if (!wdh || wdh == "auto") {
			var diff = zDom.padBorderWidth(cave.parentNode) + zDom.padBorderWidth(cave.parentNode.parentNode);
			if (tl) tl.firstChild.style.width = cave.offsetWidth + diff + "px";
			if (hl) hl.firstChild.firstChild.style.width = Math.max(0, cave.offsetWidth
				- (zDom.padBorderWidth(hl) + zDom.padBorderWidth(hl.firstChild) - diff)) + "px";
			if (bl) bl.firstChild.style.width = cave.offsetWidth + diff + "px";
		} else {
			if (tl) tl.firstChild.style.width = "";
			if (hl) hl.firstChild.style.width = "";
			if (bl) bl.firstChild.style.width = "";
		}
	} : zk.$void,
	_fixHgh: function () {
		if (!this.isRealVisible()) return;
		var n = this.getNode(),
			hgh = n.style.height
			cave = this.getSubnode('cave'),
			cvh = cave.style.height;
		if (hgh && hgh != "auto") {
			if (zk.ie6Only) cave.style.height = "0px";
			zDom.setOffsetHeight(cave, this._offsetHeight(n));
		} else if (cvh && cvh != "auto") {
			if (zk.ie6Only) cave.style.height = "0px";
			cave.style.height = "";
		}
	},
	_offsetHeight: function (n) {
		var h = n.offsetHeight - 1 - this._titleHeight(n);
		if(this._mode != 'embedded' && this._mode != 'popup') {
			var cave = this.getSubnode('cave'), bl = zDom.lastChild(n, "DIV"),
				cap = this.getSubnode("cap");
			h -= bl.offsetHeight;
			if (cave)
				h -= zDom.padBorderHeight(cave.parentNode);
			if (cap)
				h -= zDom.padBorderHeight(cap.parentNode);
		}
		return h - zDom.padBorderHeight(n);
	},
	_titleHeight: function (n) {
		var cap = this.getSubnode('cap'),
			tl = zDom.firstChild(n, "DIV");
		return cap ? cap.offsetHeight + tl.offsetHeight:
			this._mode != 'embedded' && this._mode != 'popup' ?
				zDom.nextSibling(tl, "DIV").offsetHeight: 0;
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
		this.fire('onMove', zk.copy({
			left: x + 'px',
			top: y + 'px'
		}, keys), {ignorable: true});
	},

	//super//
	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		if (this.desktop) {
			this._fixHgh();
			this._syncShadow();

			zWatch.fireDown('beforeSize', null, this);
			zWatch.fireDown('onSize', null, this); // Note: IE6 is broken, because its offsetHeight doesn't update.
		}
	},
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		if (this.desktop) {
			this._fixWdh();
			this._syncShadow();

			zWatch.fireDown('beforeSize', null, this);
			zWatch.fireDown('onSize', null, this);
		}
	},
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
		zWatch.listen('onShow', this);
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
		zWatch.unlisten('onShow', this);
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
		switch (evt.domTarget) {
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
		switch (evt.domTarget) {
		case this.getSubnode('close'):
			zDom.addClass(this.getSubnode('close'), this.getZclass() + '-close-over');
			break;
		case this.getSubnode('max'):
			var zcls = this.getZclass(),
				added = this.isMaximized() ? ' ' + zcls + '-maxd-over' : '';
			zDom.addClass(this.getSubnode('max'), zcls + '-max-over' + added);
			break;
		case this.getSubnode('min'):
			zDom.addClass(this.getSubnode('min'), this.getZclass() + '-min-over');
			break;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		switch (evt.domTarget) {
		case this.getSubnode('close'):
			zDom.rmClass(this.getSubnode('close'), this.getZclass() + '-close-over');
			break;
		case this.getSubnode('max'):
			var zcls = this.getZclass(),
				max = this.getSubnode('max');
			if (this.isMaximized())
				zDom.rmClass(max, zcls + '-maxd-over');
			zDom.rmClass(max, zcls + '-max-over');
			break;
		case this.getSubnode('min'):
			zDom.rmClass(this.getSubnode('min'), this.getZclass() + '-min-over');
			break;
		}
		this.$supers('doMouseOut_', arguments);
	}
},{ //static
	//drag
	_startmove: function (dg) {
		//Bug #1568393: we have to change the percetage to the pixel.
		var el = dg.node;
		if(el.style.top && el.style.top.indexOf("%") >= 0)
			 el.style.top = el.offsetTop + "px";
		if(el.style.left && el.style.left.indexOf("%") >= 0)
			 el.style.left = el.offsetLeft + "px";
		//TODO zkau.closeFloats();
	},
	_ghostmove: function (dg, ofs, evt) {
		var wnd = dg.control,
			el = dg.node;
		wnd._hideShadow();
		var top = zDom.firstChild(el, "DIV"),
			header = zDom.nextSibling(top, 'DIV'),
			fakeT = top.cloneNode(true),
			fakeH = header.cloneNode(true),
			html = '<div id="zk_wndghost" class="z-window-move-ghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zDom.offsetWidth(el)+'px;height:'+zDom.offsetHeight(el)
			+'px;z-index:'+el.style.zIndex+'"><dl></dl></div>';
		document.body.insertAdjacentHTML("afterBegin", html);
		dg._wndoffs = ofs;
		el.style.visibility = "hidden";
		var h = el.offsetHeight - top.offsetHeight - header.offsetHeight;
		el = zDom.$("zk_wndghost");
		el.firstChild.style.height = zDom.revisedHeight(el.firstChild, h) + "px";
		el.insertBefore(fakeT, el.firstChild);
		el.insertBefore(fakeH, el.lastChild);
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
		wgt._fireOnMove(zEvt.metaData(evt));
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
