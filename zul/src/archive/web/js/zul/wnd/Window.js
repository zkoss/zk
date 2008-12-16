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

	$init: function () {
		this.$supers('$init', arguments);
		this._fellows = [];
		this.listen('onClose', this, null, -1000);
		this.listen('onMove', this, null, -1000);
		this._zIndex = 0;
	},

	getMode: function () {
		return this._mode;
	},
	setMode: function (mode) {
		if (this._mode != mode) {
			this._mode = mode;
			if (this.node)
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
			n = this.node;
		if (!pos && !n.style.top && !n.style.left) {		
			var xy = zDom.revisedOffset(n);
			n.style.left = xy[0] + "px";
			n.style.top = xy[1] + "px";
		} else if (pos == "parent")
			this._posByParent();

		zDom.makeVParent(n); //OK to make vparent twice
		this._updateDomPos();

		var realVisible = this.isRealVisible();
		if (realVisible) n.style.visibility = 'visible';

		if (realVisible) this.setTopmost();
		this._syncShadow();

		this._makeFloat();
	},
	_doModal: function () {
		var pos = this.getPosition(),
			n = this.node;
		if (pos == "parent") this._posByParent();

		zDom.makeVParent(n); //OK to make vparent twice
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
		if (realVisible) n.style.visibility = 'visible';

		if (realVisible) this.setTopmost();
		this._syncShadow();
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
		var n = this.node,
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
				this._shadow = new zk.eff.Shadow(this.node, {stackup:true});
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
		var handle = this.ecap;
		if (handle && !this._drag) {
			handle.style.cursor = "move";
			var $Window = this.$class;
			this._drag = new zk.Draggable(this, null, {
				handle: handle, overlay: true, stackup: true,
				starteffect: $Window._startmove,
				ghosting: $Window._ghostmove,
				ignoredrag: $Window._ignoremove,
				endeffect: $Window._aftermove});
		}
	},
	_updateDomPos: function (force) {
		var n = this.node, pos = this._pos;
		if (pos == "parent"/*handled by the caller*/ || (!pos && !force))
			return;

		n.style.position = "absolute"; //just in case
		zDom.center(n, pos);
		var sdw = this._shadow;
		if (pos && sdw) {
			var d = sdw.getDelta(), l = n.offsetLeft, t = n.offsetTop, w = n.offsetWidth,
				h = n.offsetHeight, st = n.style; 
			if (pos.indexOf("left") >= 0 && d.l < 0) st.left = l - d.l + "px";
			else if (pos.indexOf("right") >= 0)
				st.left = l - (zk.ie ? Math.round((Math.abs(d.l) + d.w)/2) : Math.round((d.l + d.w)/2)) - 1 + "px";

			if (pos.indexOf("top") >= 0 && d.t < 0) st.top = t - d.t + "px";
			else if (pos.indexOf("bottom") >= 0)
				st.top = t - (zk.ie ? Math.round((Math.abs(d.t) + d.h)/2) + 1 : Math.round((d.t + d.h)/2)) - 1 + "px";
		}
		//TODO zkau.sendOnMove(n);
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

			var n = this.node;
			if (n) {
				this._updateDomPos(); //TODO: handle pos = 'parent'
				this._syncShadow();
			}
		}
	},

	getMinheight: function () {
		return this._minheight;
	},
	setMinheight: function (minheight) {
		if (this._minheight != minheight) {
			this._minheight = minheight;

			var n = this.node;
			if (n) ;//TODO
		}
	},
	getMinwidth: function () {
		return this._minwidth;
	},
	setMinwidth: function (minwidth) {
		if (this._minwidth != minwidth) {
			this._minwidth = minwidth;

			var n = this.node;
			if (n) ;//TODO
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
		if (this.node) this.rerender(zk.Skipper.nonCaptionSkipper);
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
				if (wgt == this) 
					this.setTopmost();
				if (wgt.isFloating_())
					return;
			}
	},

	_fireOnMove: function (keys) {
		var pos = this._pos, node = this.node,
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
		if (this.node) {
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

	appendChild: function (child) {
		if (this.$supers('appendChild', arguments)) {
			if (child.$instanceof(zul.wgt.Caption))
				this.caption = child;
			return true;
		}
		return false;
	},
	insertBefore: function (child, sibling) {
		if (this.$supers('insertBefore', arguments)) {
			if (child.$instanceof(zul.wgt.Caption))
				this.caption = child;
			return true;
		}
		return false;
	},
	removeChild: function (child) {
		if (this.$supers('removeChild', arguments)) {
			if (child == this.caption)
				this.caption = null;
			return true;
		}
		return false;
	},
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments),
			visible = this.isVisible();
		if ((!no || !no.visible) && visible && this.isMinimized())
			style = 'display:none;'+style;
		if (this.getMode() != 'embedded')
			style = (visible ? "position:absolute;visibility:hidden;" : "position:absolute;")
				+style;
		return style;
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		var uuid = this. uuid,
			$Window = this.$class;
		this.ecap = zDom.$(uuid + '$cap');

		for (var nms = ['close', 'max', 'min'], j = 3; --j >=0;) {
			var nm = nms[j],
				n = this['e' + nm ] = zDom.$(uuid + '$' + nm);
			if (n) {
				zEvt.listen(n, 'click', $Window[nm + 'click']);
				zEvt.listen(n, 'mouseover', $Window[nm + 'over']);
				zEvt.listen(n, 'mouseout', $Window[nm + 'out']);
				if (!n.style.cursor) n.style.cursor = "default";
			}
		}

		var mode = this._mode;
		if (mode != 'embedded') {
			zWatch.listen('onFloatUp', this);
			this.setFloating_(true);

			if (mode == 'modal' || mode == 'highlighted') this._doModal();
			else this._doOverlapped();
		}
	},
	unbind_: function () {
		this.node.style.visibility = 'hidden'; //avoid unpleasant effect

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

		zDom.undoVParent(this.node);
		zWatch.unlisten('onFloatUp', this);
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
	isImportantEvent_: function (evtnm) {
		return this.$class._impEvts[evtnm];
	}
},{ //static
	_impEvts: {onMove:1, onZIndex:1, onOpen:1},
	closeclick: function (evt) {
		var wnd = zk.Widget.$(evt);
		wnd.fire('onClose');
		zEvt.stop(evt);
	},
	closeover: function (evt) {
		var wnd = zk.Widget.$(evt),
			zcls = wnd.getZclass();
		zDom.addClass(wnd.eclose, zcls + '-close-over');
	},
	closeout: function (evt) {
		var wnd = zk.Widget.$(evt),
			zcls = wnd.getZclass();
		zDom.rmClass(wnd.eclose, zcls + '-close-over');
	},
	maxclick: function (evt) {
		var wnd = zk.Widget.$(evt);
		//TODO: handle popup since evt stopped
		zEvt.stop(evt);
	},
	maxover: function (evt) {
		var wnd = zk.Widget.$(evt),
			zcls = wnd.getZclass();
		if (wnd.isMaximized())
			zDom.addClass(wnd.emax, zcls + '-maximized-over');
		zDom.addClass(wnd.emax, zcls + '-maximize-over');
	},
	maxout: function (evt) {
		var wnd = zk.Widget.$(evt),
			zcls = wnd.getZclass();
		if (wnd.isMaximized())
			zDom.rmClass(wnd.emax, zcls + '-maximized-over');
		zDom.rmClass(wnd.emax, zcls + '-maximize-over');
	},
	minclick: function (evt) {
		var wnd = zk.Widget.$(evt);
		//TODO: handle popup since evt stopped
		zEvt.stop(evt);
	},
	minover: function (evt) {
		var wnd = zk.Widget.$(evt),
			zcls = wnd.getZclass();
		zDom.addClass(wnd.emin, zcls + '-minimize-over');
	},
	minout: function (evt) {
		var wnd = zk.Widget.$(evt),
			zcls = wnd.getZclass();
		zDom.rmClass(wnd.emin, zcls + '-minimize-over');
	},

	_onMoveMarshal: function () {
		return [this.x, this.y, this.keys.marshal()];
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
	_ghostmove: function (dg, ghosting, pointer) {
		if (ghosting) {
			var wnd = dg.widget,
				el = dg.node;
			wnd._hideShadow();
			var ofs = dg.beginGhostToDIV(),
				title = zDom.firstChild(el, "DIV"),
				fakeTitle = title.cloneNode(true);
			var html = '<div id="zk_wndghost" class="z-window-move-ghost" style="position:absolute;top:'
				+ofs[1]+'px;left:'+ofs[0]+'px;width:'
				+zDom.offsetWidth(el)+'px;height:'+zDom.offsetHeight(el)
				+'px;z-index:'+el.style.zIndex+'"><ul></ul></div></div>';
			document.body.insertAdjacentHTML("afterBegin", html);
			dg._wndoffs = ofs;
			el.style.visibility = "hidden";
			var h = el.offsetHeight - title.offsetHeight;
			el = dg.node = zDom.$("zk_wndghost");
			el.firstChild.style.height = zDom.revisedHeight(el.firstChild, h) + "px";
			el.insertBefore(fakeTitle, dg.node.firstChild);
		} else {
			var org = dg.getGhostOrgin();
			if (org) {
				var el = dg.node;
				org.style.top = org.offsetTop + el.offsetTop - dg._wndoffs[1] + "px";
				org.style.left = org.offsetLeft + el.offsetLeft - dg._wndoffs[0] + "px";
			}
			dg.endGhostToDIV();
			document.body.style.cursor = "";
		}
	},
	_ignoremove: function (dg, pointer, evt) {
		var target = zEvt.target(evt),
			el = dg.node;
		if (!target || target.id.indexOf("$close") > -1 || target.id.indexOf("$min") > -1
		|| target.id.indexOf("$max") > -1)
			return true; //ignore special buttons
		if (!dg.widget.isSizable()
		|| (el.offsetTop + 4 < pointer[1] && el.offsetLeft + 4 < pointer[0] 
		&& el.offsetLeft + el.offsetWidth - 4 > pointer[0]))
			return false; //accept if not sizable or not on border
		return true;
	},
	_aftermove: function (dg, evt) {
		dg.node.style.visibility = "";
		var wgt = dg.widget;
		wgt._syncShadow();
		wgt._fireOnMove(zEvt.keyMetaData(evt));
	}
});
