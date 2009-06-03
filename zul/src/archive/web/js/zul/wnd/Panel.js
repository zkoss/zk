/* Panel.js

	Purpose:

	Description:

	History:
		Mon Jan 12 18:31:03 2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zPkg.load('zul.wgt');
zul.wnd.Panel = zk.$extends(zul.Widget, {
	_border: "none",
	_title: "",
	_open: true,

	$init: function () {
		this.$supers('$init', arguments);
		this.listen('onClose', this, null, -1000);
		this.listen('onMove', this, null, -1000);
	},

	$define: {
		framable: _zkf = function () {
			this.rerender(); //TODO: like Window, use _updateDomOuter
		},
		movable: _zkf,
		floatable: _zkf,
		maximizable: _zkf,
		minimizable: _zkf,
		collapsible: _zkf,
		closable: _zkf,
		border: _zkf,
		title: _zkf,

		open: function (open, fromServer) {
			var node = this.getNode();
			if (node) {
				var zcls = this.getZclass(),
					body = this.getSubnode('body');
				if (body) {
					if (open) {
						zDom.rmClass(node, zcls + '-colpsd');
						zAnima.slideDown(this, body, {
							afterAnima: this._afterSlideDown
						});
					} else {
						zDom.addClass(node, zcls + '-colpsd');
						this._hideShadow();

						// windows 2003 with IE6 will cause an error when user toggles the panel in portallayout.
						if (zk.ie6Only && !node.style.width)
							node.runtimeStyle.width = "100%";

						zAnima.slideUp(this, body, {
							beforeAnima: this._beforeSlideUp
						});
					}
				}
				if (fromServer) this.fire('onOpen', {open:open});
			}
		},

		maximized: null, //TODO
		minimized: function (minimized, fromServer) {
			/** TODO
			 * if (_minimized) {
				_maximized = false;
				setVisible0(false); //avoid dead loop
			} else setVisible0(true);
			*/
			var node = this.getNode();
			if (node) {
				var s = node.style, l = s.left, t = s.top, w = s.width, h = s.height;
				if (minimized) {
					zWatch.fireDown('onHide', {visible:true}, this);
					zDom.hide(node);
				} else {
					zDom.show(node);
					zWatch.fireDown('onShow', {visible:true}, this);
				}
				if (!fromServer) {
					var wgt = this;
					this.fire('onMinimize', {
						left: s.left,
						top: s.top,
						width: s.width,
						height: s.height,
						minimized: minimized
					});
				}
			}
		}
	},

	//super//
	setVisible: function (visible, fromServer) {
		if (this._visible != visible) {
			/** TODO
			 * if (this.isMaximized()) {
				zkPanel.maximize(cmp, false);
			} else if (this.isMinimized()) {
				zkPanel.minimize(cmp, false);
			}*/
			this.$supers('setVisible', arguments);
		}
	},
	setHeight: function () {
		this.$supers('setHeight', arguments);
		if (this.desktop) {
			zWatch.fireDown('beforeSize', null, this);
			zWatch.fireDown('onSize', null, this);
		}
	},
	setWidth: function () {
		this.$supers('setWidth', arguments);
		if (this.desktop) {
			zWatch.fireDown('beforeSize', null, this);
			zWatch.fireDown('onSize', null, this);
		}
	},
	setTop: function () {
		this._hideShadow();
		this.$supers('setTop', arguments);
		this._syncShadow();

	},
	setLeft: function () {
		this._hideShadow();
		this.$supers('setLeft', arguments);
		this._syncShadow();
	},
	updateDomStyle_: function () {
		this.$supers('updateDomStyle_', arguments);
		if (this.desktop) {
			zWatch.fireDown('beforeSize', null, this);
			zWatch.fireDown('onSize', null, this);
		}
	},
	addToolbar: function (name, toolbar) {
		switch (name) {
			case 'tbar':
				this.tbar = toolbar;
				break;
			case 'bbar':
				this.bbar = toolbar;
				break;
			case 'fbar':
				this.fbar = toolbar;
				break;
			default: return false; // not match
		}
		return this.appendChild(toolbar);
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
		this._fixWdh();
		this._fixHgh();
		this._syncShadow();
	},
	onShow: _zkf,
	onHide: function () {
		this._hideShadow();
	},
	_fixWdh: zk.ie7 ? function () {
		if (!this.isFramable() || !this.panelchildren) return;

		var n = this.getNode(),
			wdh = n.style.width,
			tl = zDom.firstChild(n, "DIV"),
			hl = this.getSubnode("cap") ? zDom.nextSibling(tl, "DIV") : null,
			bl = zDom.lastChild(zDom.lastChild(n, "DIV"), "DIV"),
			bb = this.getSubnode("bb"),
			fl = this.getSubnode("fb") ? zDom.previousSibling(bl, "DIV"): null,
			body = this.panelchildren.getNode(),
			cm = body.parentNode;

		if (!wdh || wdh == "auto") {
			var diff = zDom.padBorderWidth(cm.parentNode) + zDom.padBorderWidth(cm.parentNode.parentNode);
			if (tl) {
				tl.firstChild.style.width = cm.offsetWidth + diff + "px";
			}
			if (hl) {
				hl.firstChild.firstChild.style.width = Math.max(cm.offsetWidth - (zDom.padBorderWidth(hl)
					+ zDom.padBorderWidth(hl.firstChild) - diff), 0) + "px";
			}
			if (bb) bb.style.width = zDom.revisedWidth(bb, body.offsetWidth);
			if (fl) {
				fl.firstChild.firstChild.style.width = Math.max(cm.offsetWidth - (zDom.padBorderWidth(fl)
					+ zDom.padBorderWidth(fl.firstChild) - diff), 0) + "px";
			}
			if (bl) {
				bl.firstChild.style.width = cm.offsetWidth + diff + "px";
			}
		} else {
			if (tl) tl.firstChild.style.width = "";
			if (hl) hl.firstChild.style.width = "";
			if (bb) bb.style.width = "";
			if (fl) fl.firstChild.style.width = "";
			if (bl) bl.firstChild.style.width = "";
		}
	} : zk.$void,
	_fixHgh: function () {
		if (!this.panelchildren || !this.isRealVisible()) return;
		var n = this.getNode(),
			body = this.panelchildren.getNode(),
			hgh = n.style.height;
		if (zk.ie6Only && ((hgh && hgh != "auto" )|| body.style.height)) body.style.height = "0px";
		if (hgh && hgh != "auto")
			zDom.setOffsetHeight(body, this._offsetHeight(n));
		if (zk.ie6Only) zDom.redoCSS(body);
	},
	_offsetHeight: function (n) {
		var h = n.offsetHeight - 1;
		h -= this._titleHeight(n);
		if (this.isFramable()) {
			var body = this.panelchildren.getNode(),
				bl = zDom.lastChild(this.getSubnode('body'), "DIV"),
				title = this.getSubnode('cap');
			h -= bl.offsetHeight;
			if (body)
				h -= zDom.padBorderHeight(body.parentNode);
			if (title)
				h -= zDom.padBorderHeight(title.parentNode);
		}
		h -= zDom.padBorderHeight(n);
		var tb = this.getSubnode('tb'),
			bb = this.getSubnode('bb'),
			fb = this.getSubnode('fb');
		if (tb) h -= tb.offsetHeight;
		if (bb) h -= bb.offsetHeight;
		if (fb) h -= fb.offsetHeight;
		return h;
	},
	_titleHeight: function (n) {
		var cap = this.getSubnode('cap'),
			top = this.isFramable() ?
				zDom.firstChild(n, 'DIV').offsetHeight: 0;
		return cap ? cap.offsetHeight + top : top;
	},
	_syncMaximized: function (cmp) {
		/** TODO
		 * if (!this._lastSize) return;
		var floated = zkPanel.isFloatable(cmp), op = floated ? zPos.offsetParent(cmp) : cmp.parentNode,
			s = cmp.style;

		// Sometimes, the clientWidth/Height in IE6 is wrong.
		var sw = zk.ie6Only && op.clientWidth == 0 ? (op.offsetWidth - zk.sumStyles(op, "rl", zk.borders)) : op.clientWidth;
		if (!floated) {
			sw -= zk.sumStyles(op, "rl", zk.paddings);
			sw = zk.revisedSize(cmp, sw);
		}
		if (sw < 0) sw = 0;
		s.width = sw + "px";
		if (getZKAttr(cmp, "open") == "true") {
			var sh = zk.ie6Only && op.clientHeight == 0 ? (op.offsetHeight - zk.sumStyles(op, "tb", zk.borders)) : op.clientHeight;
			if (!floated) {
				sh -= zk.sumStyles(op, "tb", zk.paddings);
				sh = zk.revisedSize(cmp, sh, true);
			}
			if (sh < 0) sh = 0;
			s.height = sh + "px";
		}*/
	},
	onFloatUp: function (wgt) {
		if (!this.isVisible() || !this.isFloatable())
			return; //just in case

		for (; wgt; wgt = wgt.parent) {
			if (wgt == this) {
				this.setTopmost();
				return;
			}
			if (wgt.isFloating_())
				return;
		}
	},
	getZclass: function () {
		return this._zclass == null ?  "z-panel" : this._zclass;
	},
	_afterSlideDown: function (n) {
		zWatch.fireDown("onShow", {visible:true}, this);
	},
	_beforeSlideUp: function (n) {
		zWatch.fireDown("onHide", {visible:true}, this);
	},
	_initFloat: function () {
		var n = this.getNode();
		if (!n.style.top && !n.style.left) {
			var xy = zDom.revisedOffset(n);
			n.style.left = xy[0] + "px";
			n.style.top = xy[1] + "px";
		}

		n.style.position = "absolute";
		if (this.isMovable())
			this._initMove();

		this._syncShadow();

		if (this.isRealVisible()) {
			zDom.cleanVisibility(n);
			this.setTopmost();
		}
	},
	_initMove: function (cmp) {
		var handle = this.getSubnode('cap');
		if (handle && !this._drag) {
			handle.style.cursor = "move";
			var $Panel = this.$class;
			this._drag = new zk.Draggable(this, null, {
				handle: handle, stackup: true,
				starteffect: $Panel._startmove,
				ignoredrag: $Panel._ignoremove,
				endeffect: $Panel._aftermove});
		}
	},
	_syncShadow: function () {
		if (!this.isFloatable()) {
			if (this._shadow) {
				this._shadow.destroy();
				this._shadow = null;
			}
		} else {
			if (!this._shadow)
				this._shadow = new zk.eff.Shadow(this.getNode(),
					{left: -4, right: 4, top: -2, bottom: 3, stackup:true});
			this._shadow.sync();
		}
	},
	_hideShadow: function () {
		var shadow = this._shadow;
		if (shadow) shadow.hide();
	},
	//super//
	bind_: function () {
		this.$supers('bind_', arguments);

		zWatch.listen("onSize", this);
		zWatch.listen("onShow", this);
		zWatch.listen("onHide", this);

		var uuid = this.uuid,
			$Panel = this.$class;

		if (this.isFloatable()) {
			zWatch.listen('onFloatUp', this);
			this.setFloating_(true);
			this._initFloat();
		}
	},
	unbind_: function () {
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onShow", this);
		zWatch.unlisten('onFloatUp', this);
		zWatch.unlisten("onHide", this);
		this.setFloating_(false);

		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = null;
		}
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
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
			if (this.isMinimizable())
				this.setMinimized(!this.isMinimized());
			break;
		case this.getSubnode('exp'):
			if (this.isCollapsible())
				this.setOpen(!this.isOpen());
			break;
		default:
			this.$supers('doClick_', arguments);
			return;
		}
		evt.stop();
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
			zDom.addClass(this.getSubnode('min'), this.getZclass() + '-mini-over');
			break;
		case this.getSubnode('exp'):
			zDom.addClass(this.getSubnode('exp'), this.getZclass() + '-exp-over');
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
			zDom.rmClass(this.getSubnode('min'), this.getZclass() + '-mini-over');
			break;
		case this.getSubnode('exp'):
			zDom.rmClass(this.getSubnode('exp'), this.getZclass() + '-exp-over');
			break;
		}
		this.$supers('doMouseOut_', arguments);
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var zcls = this.getZclass();
			var added = "normal" == this.getBorder() ? '' : zcls + '-noborder';
			if (added) scls += (scls ? ' ': '') + added;
			addded = this.isOpen() ? '' : zcls + '-colpsd';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.wgt.Caption))
			this.caption = child;
		else if (child.$instanceof(zul.wnd.Panelchildren))
			this.panelchildren = child;
		else if (child.$instanceof(zul.wgt.Toolbar)) {
			if (this.firstChild == child)
				this.tbar = child;
			else if (this.lastChild == child && child.previousSibling.$instanceof(zul.wgt.Toolbar))
				this.fbar = child;
			else if (child.previousSibling.$instanceof(zul.wnd.Panelchildren))
				this.bbar = child;
		}
		this.rerender();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.caption)
			this.caption = null;
		else if (child == this.panelchildren)
			this.panelchildren = null;
		else if (child == this.tbar)
			this.tbar = null;
		else if (child == this.bbar)
			this.bbar = null;
		else if (child == this.fbar)
			this.fbar = null;
		this.rerender();
	}
}, { //static
	//drag
	_startmove: function (dg) {
		dg.control._hideShadow();
		//Bug #1568393: we have to change the percetage to the pixel.
		var el = dg.node;
		if(el.style.top && el.style.top.indexOf("%") >= 0)
			 el.style.top = el.offsetTop + "px";
		if(el.style.left && el.style.left.indexOf("%") >= 0)
			 el.style.left = el.offsetLeft + "px";
		//zkau.closeFloats(cmp, handle);
	},
	_ignoremove: function (dg, pointer, evt) {
		var wgt = dg.control;
		switch (evt.domTarget) {
		case wgt.getSubnode('close'):
		case wgt.getSubnode('max'):
		case wgt.getSubnode('min'):
		case wgt.getSubnode('exp'):
			return true; //ignore special buttons
		}
		return false;
	},
	_aftermove: function (dg, evt) {
		var wgt = dg.control;
		wgt._syncShadow();
		var node = wgt.getNode(),
			x = zk.parseInt(node.style.left),
			y = zk.parseInt(node.style.top);
		wgt.fire('onMove', zk.copy({
			left: x + 'px',
			top: y + 'px'
		}, evt.data), {ignorable: true});
	}
});
