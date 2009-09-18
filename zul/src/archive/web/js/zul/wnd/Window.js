/* Window.js

	Purpose:

	Description:

	History:
		Mon Nov 17 17:52:31     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wnd.Window = zk.$extends(zul.Widget, {
	_mode: 'embedded',
	_border: 'none',
	_minheight: 100,
	_minwidth: 200,
	_shadow: true,

	$init: function () {
		if (!zk.light) this._fellows = {};

		this.$supers('$init', arguments);

		this.listen({onClose: this,	onMove: this, onSize: this.onSizeEvent, onZIndex: this}, -1000);
		this._skipper = new zul.wnd.Skipper(this);
	},

	$define: { //zk.def
		mode: _zkf = function () {
			this._updateDomOuter();
		},
		title: _zkf,
		border: _zkf,
		closable: _zkf,
		sizable: function (sizable) {
			if (this.desktop) {
				if (sizable)
					this._makeSizer();
				else if (this._sizer) {
					this._sizer.destroy();
					this._sizer = null;
				}
			}
		},
		maximizable: _zkf,
		minimizable: _zkf,
		maximized: function (maximized, fromServer) {
			var node = this.$n();
			if (node) {
				var $n = zk(node),
					isRealVisible = this.isRealVisible();
				if (!isRealVisible && maximized) return;

				var l, t, w, h, s = node.style, cls = this.getZclass();
				if (maximized) {
					jq(this.$n('max')).addClass(cls + '-maxd');
					this._hideShadow();

					var floated = this._mode != 'embedded',
						$op = floated ? jq(node).offsetParent() : jq(node).parent();
					l = s.left;
					t = s.top;
					w = s.width;
					h = s.height;

					// prevent the scroll bar.
					s.top = "-10000px";
					s.left = "-10000px";

					// Sometimes, the clientWidth/Height in IE6 is wrong.
					var sw = zk.ie6_ && $op[0].clientWidth == 0 ? $op[0].offsetWidth - $op.zk.borderWidth() : $op[0].clientWidth,
						sh = zk.ie6_ && $op[0].clientHeight == 0 ? $op[0].offsetHeight - $op.zk.borderHeight() : $op[0].clientHeight;
					if (!floated) {
						sw -= $op.zk.paddingWidth();
						sw = $n.revisedWidth(sw);
						sh -= $op.zk.paddingHeight();
						sh = $n.revisedHeight(sh);
					}
					s.width = jq.px(sw);
					s.height = jq.px(sh);
					this._lastSize = {l:l, t:t, w:w, h:h};

					// restore.
					s.top = "0";
					s.left = "0";
				} else {
					var max = this.$n('max'),
						$max = jq(max);
					$max.removeClass(cls + "-maxd").removeClass(cls + "-maxd-over");
					if (this._lastSize) {
						s.left = this._lastSize.l;
						s.top = this._lastSize.t;
						s.width = this._lastSize.w;
						s.height = this._lastSize.h;
						this._lastSize = null;
					}
					l = s.left;
					t = s.top;
					w = s.width;
					h = s.height;

					var body = this.$n('cave');
					if (body)
						body.style.width = body.style.height = "";
				}
				if (!fromServer || isRealVisible) {
					this._visible = true;
					this.fire('onMaximize', {
						left: l,
						top: t,
						width: w,
						height: h,
						maximized: maximized,
						fromServer: fromServer
					});
				}
				if (isRealVisible) {
					this.__maximized = true;
					zWatch.fireDown('beforeSize', this);
					zWatch.fireDown('onSize', this);
				}
			}
		},
		minimized: function (minimized, fromServer) {
			if (this.isMaximized())
				this.setMaximized(false);

			var node = this.$n();
			if (node) {
				var s = node.style, l = s.left, t = s.top, w = s.width, h = s.height;
				if (minimized) {
					zWatch.fireDown('onHide', this);
					jq(node).hide();
				} else {
					jq(node).show();
					zWatch.fireDown('onShow', this);
				}
				if (!fromServer) {
					this._visible = false;
					this.fire('onMinimize', {
						left: s.left,
						top: s.top,
						width: s.width,
						height: s.height,
						minimized: minimized
					});
				}
			}
		},
		contentStyle: _zkf,
		contentSclass: _zkf,

		position: function (pos) {
			if (this.desktop && this._mode != 'embedded')
				this._updateDomPos(); //TODO: handle pos = 'parent'
		},

		minheight: null, //TODO
		minwidth: null, //TODO
		shadow: function () {
			if (this._shadow) {
				this._syncShadow();	
			} else if (this._shadowWgt) {
				this._shadowWgt.destroy();
				this._shadowWgt = null;
			}
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
			n = this.$n(),
			$n = zk(n);
		if (!pos && !n.style.top && !n.style.left) {
			var xy = $n.revisedOffset();
			n.style.left = jq.px(xy[0], true);
			n.style.top = jq.px(xy[1], true);
		} else if (pos == "parent")
			this._posByParent();

		$n.makeVParent();
		this._syncShadow();
		this._updateDomPos();

		if (this.isRealVisible()) {
			$n.cleanVisibility();
			this.setTopmost();
		}

		this._makeFloat();
	},
	_doModal: function () {
		var pos = this.getPosition(),
			n = this.$n(),
			$n = zk(n);
		if (pos == "parent") this._posByParent();

		$n.makeVParent();
		this._syncShadow();
		this._updateDomPos(true);

		if (!pos) { //adjust y (to upper location)
			var top = zk.parseInt(n.style.top), y = jq.innerY();
			if (y) {
				var y1 = top - y;
				if (y1 > 100) n.style.top = jq.px(top - (y1 - 100));
			} else if (top > 100)
				n.style.top = "100px";
		}

		//Note: modal must be visible
		var realVisible = this.isRealVisible();
		if (realVisible) {
			$n.cleanVisibility();
			this.setTopmost();
		}

		this._mask = new zk.eff.FullMask({
			id: this.uuid + "-mask",
			anchor: this._shadowWgt ? this._shadowWgt.getBottomElement() : this.$n(),
				//bug 1510218: we have to make it as a sibling
			zIndex: this._zIndex,
			stackup: (zk.useStackup === undefined ? zk.ie6_: zk.useStackup),
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
		var n = this.$n(),
			ofs = zk(n.parentNode).revisedOffset(),
			left = zk.parseInt(n.style.left), top = zk.parseInt(n.style.top);
		this._offset = ofs;
		n.style.left = jq.px(ofs[0] + zk.parseInt(n.style.left), true);
		n.style.top = jq.px(ofs[1] + zk.parseInt(n.style.top), true);
	},
	_syncShadow: _zkf = function () {
		if (this._mode == 'embedded') {
			if (this._shadowWgt) {
				this._shadowWgt.destroy();
				this._shadowWgt = null;
			}
		} else if (this._shadow) {
			if (!this._shadowWgt)
				this._shadowWgt = new zk.eff.Shadow(this.$n(),
					{left: -4, right: 4, top: -2, bottom: 3, stackup: true});
			if (this.isMaximized() || this.isMinimized())
				this._hideShadow();
			else
				this._shadowWgt.sync();
		}
	},
	zsync: _zkf, //used with zsync
	_syncMask: function () {
		if (this._mask && this._shadowWgt) this._mask.sync(this._shadowWgt.getBottomElement());
	},
	_hideShadow: function () {
		var shadow = this._shadowWgt;
		if (shadow) shadow.hide();
	},
	_makeSizer: function () {
		if (!this._sizer) {
			var Window = this.$class;
			this._sizer = new zk.Draggable(this, null, {
				stackup: true, draw: Window._drawsizing,
				starteffect: Window._startsizing,
				ghosting: Window._ghostsizing,
				endghosting: Window._endghostsizing,
				ignoredrag: Window._ignoresizing,
				endeffect: Window._aftersizing});
		}
	},
	_makeFloat: function () {
		var handle = this.$n('cap');
		if (handle && !this._drag) {
			handle.style.cursor = "move";
			var Window = this.$class;
			this._drag = new zk.Draggable(this, null, {
				handle: handle, stackup: true,
				starteffect: Window._startmove,
				ghosting: Window._ghostmove,
				endghosting: Window._endghostmove,
				ignoredrag: Window._ignoremove,
				endeffect: Window._aftermove});
		}
	},
	_updateDomPos: function (force) {
		var n = this.$n(), pos = this._position;
		if (pos == "parent"/*handled by the caller*/ || (!pos && !force))
			return;

		var st = n.style;
		st.position = "absolute"; //just in case
		var ol = st.left, ot = st.top;
		zk(n).center(pos);
		var sdw = this._shadowWgt;
		if (pos && sdw) {
			var opts = sdw.opts, l = n.offsetLeft, t = n.offsetTop;
			if (pos.indexOf("left") >= 0 && opts.left < 0)
				st.left = jq.px(l - opts.left, true);
			else if (pos.indexOf("right") >= 0 && opts.right > 0)
				st.left = jq.px(l - opts.right, true);
			if (pos.indexOf("top") >= 0 && opts.top < 0)
				st.top = jq.px(t - opts.top, true);
			else if (pos.indexOf("bottom") >= 0 && opts.bottom > 0)
				st.top = jq.px(t - opts.bottom, true);
		}
		this._syncShadow();
		if (ol != st.left || ot != st.top)
			this._fireOnMove();
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
	onSizeEvent: function (evt) {
		var data = evt.data,
			node = this.$n(),
			s = node.style;
			
		this._hideShadow();
		if (data.width != s.width) {
			s.width = data.width;
			this._fixWdh();
		}	
		if (data.height != s.height) {
			s.height = data.height;
			this._fixHgh();
		}
				
		if (data.left != s.left || data.top != s.top) {
			s.left = data.left;
			s.top = data.top;
			this._fireOnMove(evt.keys);
		}
		
		this._syncShadow();
		var self = this;
		setTimeout(function() {
			zWatch.fireDown('beforeSize', self);
			zWatch.fireDown('onSize', self);
		}, zk.ie6_ ? 800: 0);
	},
	onZIndex: function (evt) {
		this._syncShadow();
		this._syncMask();
	},
	//watch//
	onShow: function (ctl) {
		var w = ctl.origin;
		if (this != w && this._mode != 'embedded' && this.isRealVisible({until: w})) {
			zk(this.$n()).cleanVisibility();
			this._syncShadow();
		}
		this.onSize(w);
	},
	onHide: function (ctl) {
		var w = ctl.origin;
		if (this != w && this._mode != 'embedded' && this.isRealVisible({until: w})) {
			this.$n().style.visibility = 'hidden';
			this._syncShadow();
		}
	},
	onSize: (function() {
		function syncMaximized (wgt) {
			if (!wgt._lastSize) return;
			var node = wgt.$n(),
				floated = wgt._mode != 'embedded',
				$op = floated ? jq(node).offsetParent() : jq(node).parent(),
				s = node.style;

			// Sometimes, the clientWidth/Height in IE6 is wrong.
			var sw = zk.ie6_ && $op[0].clientWidth == 0 ? $op[0].offsetWidth - $op.zk.borderWidth() : $op[0].clientWidth,
				sh = zk.ie6_ && $op[0].clientHeight == 0 ? $op[0].offsetHeight - $op.zk.borderHeight() : $op[0].clientHeight;
			if (!floated) {
				sw -= $op.zk.paddingWidth();
				sw = $op.zk.revisedWidth(sw);
				sh -= $op.zk.paddingHeight();
				sh = $op.zk.revisedHeight(sh);
			}

			s.width = jq.px(sw);
			s.height = jq.px(sh);
		}
		return function() {
			this._hideShadow();
			if (this.isMaximized()) {
				if (!this.__maximized)
					syncMaximized(this);
				this.__maximized = false; // avoid deadloop
			}
			this._fixHgh();
			this._fixWdh();
			if (this._mode != 'embedded') {
				this._updateDomPos();
				this._syncShadow();
			}
		};
	})(),
	onFloatUp: function (ctl) {
		if (!this.isVisible() || this._mode == 'embedded')
			return; //just in case

		var wgt = ctl.origin;
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
		if (this._mode != 'embedded' && this._mode != 'popup' && this.isRealVisible()) {
			var n = this.$n(),
				cave = this.$n('cave').parentNode,
				wdh = n.style.width,
				$n = jq(n),
				$tl = $n.find('>div:first'),
				tl = $tl[0],
				hl = tl && this.$n("cap") ? $tl.nextAll('div:first')[0]: null,
				bl = $n.find('>div:last')[0];

			if (!wdh || wdh == "auto") {
				var $cavp = zk(cave.parentNode),
					diff = $cavp.padBorderWidth() + zk(cave.parentNode.parentNode).padBorderWidth();
				if (tl) tl.firstChild.style.width = jq.px(cave.offsetWidth + diff);
				if (hl) hl.firstChild.firstChild.style.width = jq.px(cave.offsetWidth
					- (zk(hl).padBorderWidth() + zk(hl.firstChild).padBorderWidth() - diff));
				if (bl) bl.firstChild.style.width = jq.px(cave.offsetWidth + diff);
			} else {
				if (tl) tl.firstChild.style.width = "";
				if (hl) hl.firstChild.style.width = "";
				if (bl) bl.firstChild.style.width = "";
			}
		}
	} : zk.$void,
	_fixHgh: function () {
		if (this.isRealVisible()) {
			var n = this.$n(),
				hgh = n.style.height,
				cave = this.$n('cave'),
				cvh = cave.style.height;

			if (hgh && hgh != "auto") {
				if (zk.ie6_) cave.style.height = "0px";
				zk(cave).setOffsetHeight(this._offsetHeight(n));
			} else if (cvh && cvh != "auto") {
				if (zk.ie6_) cave.style.height = "0px";
				cave.style.height = "";
			}
		}
	},
	_offsetHeight: function (n) {
		var h = n.offsetHeight - this._titleHeight(n);
		if(this._mode != 'embedded' && this._mode != 'popup') {
			var cave = this.$n('cave'),
				bl = jq(n).find('>div:last')[0],
				cap = this.$n("cap");
			h -= bl.offsetHeight;
			if (cave)
				h -= zk(cave.parentNode).padBorderHeight();
			if (cap)
				h -= zk(cap.parentNode).padBorderHeight();
		}
		return h - zk(n).padBorderHeight();
	},
	_titleHeight: function (n) {
		var cap = this.$n('cap'),
			$tl = jq(n).find('>div:first'), tl = $tl[0];
		return cap ? cap.offsetHeight + tl.offsetHeight:
			this._mode != 'embedded' && this._mode != 'popup' ?
				tl.offsetHeight: 0;
	},

	_fireOnMove: function (keys) {
		var pos = this._position, node = this.$n(),
			x = zk.parseInt(node.style.left),
			y = zk.parseInt(node.style.top);
		if (pos == 'parent') {
			var vp = node.vparentNode;
			if (vp) {
				var ofs = zk(vp).reviseOffset();
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
	setVisible: function (visible) {
		if (this._visible != visible) {
			if (this.isMaximized()) {
				this.setMaximized(false);
			} else if (this.isMinimized()) {
				this.setMinimized(false);
			}
			this.$supers('setVisible', arguments);
		}
	},
	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		if (this.desktop) {
			this._fixHgh();
			this._syncShadow();

			zWatch.fireDown('beforeSize', this);
			zWatch.fireDown('onSize', this); // Note: IE6 is broken, because its offsetHeight doesn't update.
		}
	},
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		if (this.desktop) {
			this._fixWdh();
			this._syncShadow();

			zWatch.fireDown('beforeSize', this);
			zWatch.fireDown('onSize', this);
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
	setDomVisible_: function () {
		this.$supers('setDomVisible_', arguments);
		this._syncShadow();
		this._syncMask();
	},
	setZIndex: _zkf = function () {
		this.$supers('setZIndex', arguments);
		this._syncShadow();
		this._syncMask();
	},
	setZindex: _zkf,
	focus: function (timeout) {
		if (this.desktop && this.isVisible() && this.canActivate({checkOnly:true})) {
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
		var style = this.$supers('domStyle_', arguments);
		if ((!no || !no.visible) && this.isMinimized())
			style = 'display:none;'+style;
		if (this._mode != 'embedded')
			style = "position:absolute;visibility:hidden;"+style;
		return style;
	},

	bind_: function (desktop, skipper, after) {
		this.$supers('bind_', arguments);

		var mode = this._mode;
		zWatch.listen({onSize: this, onShow: this});
		if (mode != 'embedded') {
			zWatch.listen({onFloatUp: this, onHide: this});
			this.setFloating_(true);

			if (mode == 'modal' || mode == 'highlighted') this._doModal();
			else this._doOverlapped();
		}
		
		if (this._sizable)
			this._makeSizer();
		this.domListen_(this.$n(), 'onMouseOver');
		
		if (this.isMaximizable() && this.isMaximized()) {
			var self = this;
			after.push(function() {
				self._maximized = false;
				self.setMaximized(true, true);				
			});
		}

		if (this._mode != 'embedded' && (zk.ie || zk.opera || zk.gecko2_))
			jq.zsync(this); //sync shadow if it is implemented with div
	},
	unbind_: function () {
		var node = this.$n();
		node.style.visibility = 'hidden'; //avoid unpleasant effect

		if (zk.ie || zk.opera || zk.gecko2_) jq.zsync(this, false);

		//we don't check this._mode here since it might be already changed
		if (this._shadowWgt) {
			this._shadowWgt.destroy();
			this._shadowWgt = null;
		}
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}
		if (this._sizer) {
			this._sizer.destroy();
			this._sizer = null;
		}

		if (this._mask) {
			this._mask.destroy();
			this._mask = null;
		}

		zk(node).undoVParent();
		zWatch.unlisten({onFloatUp: this, onSize: this, onShow: this, onHide: this});
		this.setFloating_(false);

		if (zk.currentModal == this) {
			zk.currentModal = this._prevmodal;
			var prevfocus = this._prevfocus;
			if (prevfocus) prevfocus.focus(0);
			this._prevfocus = this._prevmodal = null;
		}

		var Window = this.$class;
		for (var nms = ['close', 'max', 'min'], j = 3; j--;) {
			var nm = nms[j],
				n = this['e' + nm ];
			if (n) {
				this['e' + nm ] = null;
				jq(n).unbind('click', Window[nm + 'click'])
					.unbind('mouseover', Window[nm + 'over'])
					.unbind('mouseout', Window[nm + 'out']);
			}
		}
		this.$supers('unbind_', arguments);
	},
	_doMouseOver: function (evt) {
		if (this._sizer) {
			var n = this.$n(),
				c = this.$class._insizer(n, zk(n).revisedOffset(), evt.pageX, evt.pageY),
				handle = this._mode == 'embedded' ? false : this.$n('cap');
			if (!this.isMaximized() && c) {
				if (this.isMaximized()) return; // unsupported this case.
				if (this._backupCursor == undefined)
					this._backupCursor = n.style.cursor;
				n.style.cursor = c == 1 ? 'n-resize': c == 2 ? 'ne-resize':
					c == 3 ? 'e-resize': c == 4 ? 'se-resize':
					c == 5 ? 's-resize': c == 6 ? 'sw-resize':
					c == 7 ? 'w-resize': 'nw-resize';
				if (handle) handle.style.cursor = "";
			} else {
				n.style.cursor = this._backupCursor;
				if (handle) handle.style.cursor = "move";
			}
		}
	},
	doClick_: function (evt) {
		switch (evt.domTarget) {
		case this.$n('close'):
			this.fire('onClose');
			break;
		case this.$n('max'):
			this.setMaximized(!this.isMaximized());
			break;
		case this.$n('min'):
			this.setMinimized(!this.isMinimized());
			break;
		default:
			this.$supers('doClick_', arguments);
			return;
		}
		evt.stop();
	},
	doMouseOver_: function (evt) {
		switch (evt.domTarget) {
		case this.$n('close'):
			jq(this.$n('close')).addClass(this.getZclass() + '-close-over');
			break;
		case this.$n('max'):
			var zcls = this.getZclass(),
				added = this.isMaximized() ? ' ' + zcls + '-maxd-over' : '';
			jq(this.$n('max')).addClass(zcls + '-max-over' + added);
			break;
		case this.$n('min'):
			jq(this.$n('min')).addClass(this.getZclass() + '-min-over');
			break;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		switch (evt.domTarget) {
		case this.$n('close'):
			jq(this.$n('close')).removeClass(this.getZclass() + '-close-over');
			break;
		case this.$n('max'):
			var zcls = this.getZclass(),
				$max = jq(this.$n('max'));
			if (this.isMaximized())
				$max.removeClass(zcls + '-maxd-over');
			$max.removeClass(zcls + '-max-over');
			break;
		case this.$n('min'):
			jq(this.$n('min')).removeClass(this.getZclass() + '-min-over');
			break;
		}
		this.$supers('doMouseOut_', arguments);
	}
},{ //static
	//drag move
	_startmove: function (dg) {
		//Bug #1568393: we have to change the percetage to the pixel.
		var el = dg.node;
		if(el.style.top && el.style.top.indexOf("%") >= 0)
			 el.style.top = el.offsetTop + "px";
		if(el.style.left && el.style.left.indexOf("%") >= 0)
			 el.style.left = el.offsetLeft + "px";
		zWatch.fire('onFloatUp', dg.control); //notify all
	},
	_ghostmove: function (dg, ofs, evt) {
		var wnd = dg.control,
			el = dg.node;
		wnd._hideShadow();
		var $el = jq(el),
			$top = $el.find('>div:first'),
			top = $top[0],
			header = $top.nextAll('div:first')[0],
			fakeT = jq(top).clone()[0],
			fakeH = jq(header).clone()[0];
		jq(document.body).prepend(
			'<div id="zk_wndghost" class="' + wnd.getZclass() + '-move-ghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+$el.zk.offsetWidth()+'px;height:'+$el.zk.offsetHeight()
			+'px;z-index:'+el.style.zIndex+'"><dl></dl></div>');
		dg._wndoffs = ofs;
		el.style.visibility = "hidden";
		var h = el.offsetHeight - top.offsetHeight - header.offsetHeight;
		el = jq("#zk_wndghost")[0];
		el.firstChild.style.height = jq.px(zk(el.firstChild).revisedHeight(h));
		el.insertBefore(fakeT, el.firstChild);
		el.insertBefore(fakeH, el.lastChild);
		return el;
	},
	_endghostmove: function (dg, origin) {
		var el = dg.node; //ghost
		origin.style.top = jq.px(origin.offsetTop + el.offsetTop - dg._wndoffs[1], true);
		origin.style.left = jq.px(origin.offsetLeft + el.offsetLeft - dg._wndoffs[0], true);

		document.body.style.cursor = "";
	},
	_ignoremove: function (dg, pointer, evt) {
		var el = dg.node,
			wgt = dg.control;
		switch (evt.domTarget) {
		case wgt.$n('close'):
		case wgt.$n('max'):
		case wgt.$n('min'):
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
		wgt._fireOnMove(evt.data);
	},
	// drag sizing
	_startsizing: function (dg) {
		zWatch.fire('onFloatUp', dg.control); //notify all
	},
	_ghostsizing: function (dg, ofs, evt) {
		var wnd = dg.control,
			el = dg.node;
		wnd._hideShadow();
		wnd.setTopmost();
		var $el = jq(el);
		jq(document.body).append(
			'<div id="zk_ddghost" class="' + wnd.getZclass() + '-resize-faker"'
			+' style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+$el.zk.offsetWidth()+'px;height:'+$el.zk.offsetHeight()
			+'px;z-index:'+el.style.zIndex+'"><dl></dl></div>');
		return jq('#zk_ddghost')[0];
	},
	_endghostsizing: function (dg, origin) {
		var el = dg.node; //ghostvar org = zkau.getGhostOrgin(dg);
		if (origin) {
			dg.z_szofs = {
				top: el.offsetTop + 'px', left: el.offsetLeft + 'px',
				height: zk(el).revisedHeight(el.offsetHeight) + 'px',
				width: zk(el).revisedWidth(el.offsetWidth) + 'px'
			};
		}
	},
	_insizer: function(node, ofs, x, y) {
		var r = ofs[0] + node.offsetWidth, b = ofs[1] + node.offsetHeight;
		if (x - ofs[0] <= 5) {
			if (y - ofs[1] <= 5)
				return 8;
			else if (b - y <= 5)
				return 6;
			else
				return 7;
		} else if (r - x <= 5) {
			if (y - ofs[1] <= 5)
				return 2;
			else if (b - y <= 5)
				return 4;
			else
				return 3;
		} else {
			if (y - ofs[1] <= 5)
				return 1;
			else if (b - y <= 5)
				return 5;
		}
	},
	_ignoresizing: function (dg, pointer, evt) {
		var el = dg.node,
			wgt = dg.control;
		if (wgt.isMaximized()) return true;

		var offs = zk(el).revisedOffset(),
			v = wgt.$class._insizer(el, offs, pointer[0], pointer[1]);
		if (v) {
			wgt._hideShadow();
			dg.z_dir = v;
			dg.z_box = {
				top: offs[1], left: offs[0] ,height: el.offsetHeight,
				width: el.offsetWidth, minHeight: zk.parseInt(wgt.getMinheight()),
				minWidth: zk.parseInt(wgt.getMinwidth())
			};
			dg.z_orgzi = el.style.zIndex;
			return false;
		}
		return true;
	},
	_aftersizing: function (dg, evt) {
		var wgt = dg.control,
			data = dg.z_szofs;
		wgt.fire('onSize', zk.copy(data, evt.keys), {ignorable: true});
		dg.z_szofs = null;
	},
	_drawsizing: function(dg, pointer, evt) {
		if (dg.z_dir == 8 || dg.z_dir <= 2) {
			var h = dg.z_box.height + dg.z_box.top - pointer[1];
			if (h < dg.z_box.minHeight) {
				pointer[1] = dg.z_box.height + dg.z_box.top - dg.z_box.minHeight;
				h = dg.z_box.minHeight;
			}
			dg.node.style.height = jq.px(h);
			dg.node.style.top = pointer[1] + 'px';
		}
		if (dg.z_dir >= 4 && dg.z_dir <= 6) {
			var h = dg.z_box.height + pointer[1] - dg.z_box.top;
			if (h < dg.z_box.minHeight)
				h = dg.z_box.minHeight;
			dg.node.style.height = jq.px(h);
		}
		if (dg.z_dir >= 6 && dg.z_dir <= 8) {
			var w = dg.z_box.width + dg.z_box.left - pointer[0];
			if (w < dg.z_box.minWidth) {
				pointer[0] = dg.z_box.width + dg.z_box.left - dg.z_box.minWidth;
				w = dg.z_box.minWidth;
			}
			dg.node.style.width = jq.px(w);
			dg.node.style.left = pointer[0] + 'px';
		}
		if (dg.z_dir >= 2 && dg.z_dir <= 4) {
			var w = dg.z_box.width + pointer[0] - dg.z_box.left;
			if (w < dg.z_box.minWidth)
				w = dg.z_box.minWidth;
			dg.node.style.width = jq.px(w);
		}
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
