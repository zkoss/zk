/* Window.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 17 17:52:31     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wnd.Window = zk.$extends(zul.Widget, {
	_mode: 'embedded',
	_border: 'none',
	_minheight: 100,
	_minwidth: 200,

	$init: function (uuid, mold) {
		this.$super('$init', uuid, mold);
		this._fellows = [];
	},

	getMode: function () {
		return this._mode;
	},
	setMode: function (mode) {
		this['do' + mode.charAt(0).toUpperCase() + mode.substring(1)]();
	},
	doOverlapped: function () {
		if (this._setMode('overlapped')) {
			this._doOverOrPopup();
			zk.overlapped.push(this);
		}
	},
	doPopup: function () {
		if (this._setMode('popup')) {
			this._doOverOrPopup();
			zk.popup.push(this);
		}
	},
	doHilighted: _zkf = function () {
	},
	doModal: _zkf,
	doEmbedded: function () {
	},
	_doOverOrPopup: function () {
		this._updateDomOuter();

		var pos = this.getPosition(),
			isV = this._shallVParent(),
			n = this.node;
		if (!pos && isV && !n.style.top && !n.style.left) {		
			var xy = zDom.revisedOffset(n);
			n.style.left = xy[0] + "px";
			n.style.top = xy[1] + "px";
		} else if (pos == "parent" && isV) {
			var xy = zk.revisedOffset(n.parentNode),
				left = zk.parseInt(n.style.left), top = zk.parseInt(n.style.top);
			this._offset = xy;
			n.style.left = xy[0] + zk.parseInt(n.style.left) + "px";
			n.style.top = xy[1] + zk.parseInt(n.style.top) + "px";
		}

		if (isV) zDom.makeVParent(n);
		//if (pos) this._position(pos);

//		if (this.isVisible()) //TO getZKAttr(visible...
			n.style.visibility = 'visible';
		zDom.show(n);
	},
	_setMode: function (mode) {
		if (this._mode != mode) {
			var n = this.node;
			if (n) this._cleanMode();
			this._mode = mode;
			if (n) return true;
		}
		return false;
	},
	_cleanMode: function () {
		switch (this.getMode()) {
		case 'overlapped': zk.overlapped.$remove(this); break;
		case 'popup': zk.popup.$remove(this); break;
		}
	},
	_shallVParent: function () {
		for (var wgt = this; wgt = wgt.parent;)
			if (wgt._mode && wgt._mode != 'embedded')
				return false;
		return true;
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
			if (n) ;//TODO
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

	//super//
	focus: function () {
		if (this.node) {
			var cap = this.caption;
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (w != cap && w.focus())
					return true;
			return cap && cap.focus();
		}
		return false;
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-window-" + this._mode;
	},

	appendChild: function (child) {
		if (this.$super('appendChild', child)) {
			if (child.$instanceof(zul.wgt.Caption))
				this.caption = child;
			return true;
		}
		return false;
	},
	insertBefore: function (child, sibling) {
		if (this.$super('insertBefore', child, sibling)) {
			if (child.$instanceof(zul.wgt.Caption))
				this.caption = child;
			return true;
		}
		return false;
	},
	removeChild: function (child) {
		if (this.$super('removeChild', child)) {
			if (child == this.caption)
				this.caption = null;
			return true;
		}
		return false;
	},
	domStyle_: function (no) {
		var style = this.$super('domStyle_', no),
			visible = this.isVisible();
		if ((!no || !no.visible) && visible && this.isMinimized())
			style = 'display:none;'+style;
		if (this.getMode() != 'embedded')
			style = (visible ? "position:absolute;visibility:hidden;" : "position:absolute;")
				+style;
		return style;
	},
	bind_: function (desktop, skipper) {
		this.$super('bind_', desktop, skipper);

		var uuid = this. uuid,
			$Window = this.$class;
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
	},
	unbind_: function (skipper) {
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
		this.$super('unbind_', skipper);
	}
},{ //static
	closeclick: function (evt) {
		var wnd = zk.Widget.$(evt);
		wnd.fire2('onClose');
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
	}
});
