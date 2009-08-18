/* Panel.js

	Purpose:

	Description:

	History:
		Mon Jan 12 18:31:03 2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wnd.Panel = zk.$extends(zul.Widget, {
	_border: "none",
	_title: "",
	_open: true,

	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onClose: this, onMove: this}, -1000);
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
			var node = this.$n();
			if (node) {
				var zcls = this.getZclass(),
					$body = jq(this.$n('body'));
				if ($body[0] && !$body.is(':animated')) {
					if (open) {
						jq(node).removeClass(zcls + '-colpsd');
						$body.zk.slideDown(this, {afterAnima: this._afterSlideDown});
					} else {
						jq(node).addClass(zcls + '-colpsd');
						this._hideShadow();
						// windows 2003 with IE6 will cause an error when user toggles the panel in portallayout.
						if (zk.ie6_ && !node.style.width)
							node.runtimeStyle.width = "100%";
						$body.zk.slideUp(this, {beforeAnima: this._beforeSlideUp});
					}
					if (!fromServer) this.fire('onOpen', {open:open});
				}
			}
		},
		maximized: function (maximized, fromServer) {
			var node = this.$n();
			if (node) {
				var $n = zk(node),
					isRealVisible = $n.isRealVisible();
				if (!isRealVisible && maximized) return;
	
				var l, t, w, h, s = node.style, cls = this.getZclass();
				if (maximized) {
					jq(this.$n('max')).addClass(cls + '-maxd');
					this._hideShadow();
	
					if (this.isCollapsible() && !this.isOpen()) {
						$n.jq.removeClass(cls + '-colpsd');
						var body = this.$n('body');
						if (body) body.style.display = "";
					}
	
					var floated = this.isFloatable(),
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
					if (this.isCollapsible() && !this.isOpen()) {
						jq(node).addClass(cls + "-colpsd");
						var body = this.$n('body');
						if (body) body.style.display = "none";
					}
					var body = this.panelchildren ? this.panelchildren.$n() : null;
					if (body)
						body.style.width = body.style.height = "";
				}
				if (!fromServer || isRealVisible) {
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
					zWatch.fireDown('beforeSize', null, this);
					zWatch.fireDown('onSize', null, this);
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
					zWatch.fireDown('onHide', null, this);
					jq(node).hide();
				} else {
					jq(node).show();
					zWatch.fireDown('onShow', null, this);
				}
				if (!fromServer) {
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
	onSize: _zkf = (function() {
		function syncMaximized (wgt) {
			if (!wgt._lastSize) return;
			var node = wgt.$n(),
				floated = wgt.isFloatable(),
				$op = floated ? jq(node).offsetParent() : jq(node).parent(),
				s = node.style;
		
			// Sometimes, the clientWidth/Height in IE6 is wrong.
			var sw = zk.ie6_ && $op[0].clientWidth == 0 ? $op[0].offsetWidth - $op.zk.borderWidth() : $op[0].clientWidth;
			if (!floated) {
				sw -= $op.zk.paddingWidth();
				sw = $op.zk.revisedWidth(sw);
			}
			s.width = jq.px(sw);
			if (wgt.isOpen()) {
				var sh = zk.ie6_ && $op[0].clientHeight == 0 ? $op[0].offsetHeight - $op.zk.borderHeight() : $op[0].clientHeight;
				if (!floated) {
					sh -= $op.zk.paddingHeight();
					sh = $op.zk.revisedHeight(sh);
				}
				s.height = jq.px(sh);
			}
		}
		return function() {
			this._hideShadow();
			if (this.isMaximized()) {
				if (!this.__maximized)
					syncMaximized(this);
				this.__maximized = false; // avoid deadloop
			}
			this._fixWdh();
			this._fixHgh();
			this._syncShadow();
		};
	})(),
	onShow: _zkf,
	onHide: function () {
		this._hideShadow();
	},
	_fixWdh: zk.ie7 ? function () {
		if (!this.isFramable() || !this.panelchildren) return;

		var $n = jq(this.$n()),
			wdh = $n[0].style.width,
			tl = $n.find('> :first-child')[0],
			hl = this.$n("cap") ? jq(tl).next()[0] : null,
			bl = $n.find("> :last-child")[0],
			bb = this.$n("bb"),
			fl = this.$n("fb") ? jq(bl).prev()[0]: null,
			body = this.panelchildren.$n(),
			cm = body.parentNode;

		if (!wdh || wdh == "auto") {
			var diff = zk(cm.parentNode).padBorderWidth() + zk(cm.parentNode.parentNode).padBorderWidth();
			if (tl) {
				tl.firstChild.style.width = jq.px(cm.offsetWidth + diff);
			}
			if (hl) {
				hl.firstChild.firstChild.style.width = jq.px(cm.offsetWidth - (zk(hl).padBorderWidth()
					+ zk(hl.firstChild).padBorderWidth() - diff));
			}
			if (bb) bb.style.width = zk(bb).revisedWidth(body.offsetWidth);
			if (fl) {
				fl.firstChild.firstChild.style.width = jq.px(cm.offsetWidth - (zk(fl).padBorderWidth()
					+  zk(fl.firstChild).padBorderWidth() - diff));
			}
			if (bl) {
				bl.firstChild.style.width = jq.px(cm.offsetWidth + diff);
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
		var n = this.$n(),
			body = this.panelchildren.$n(),
			hgh = n.style.height;
		if (zk.ie6_ && ((hgh && hgh != "auto" )|| body.style.height)) body.style.height = "0";
		if (hgh && hgh != "auto")
			zk(body).setOffsetHeight(this._offsetHeight(n));
		if (zk.ie6_) zk(body).redoCSS();
	},
	_offsetHeight: function (n) {
		var h = n.offsetHeight - 1;
		h -= this._titleHeight(n);
		if (this.isFramable()) {
			var body = this.panelchildren.$n(),
				bl = jq(this.$n('body')).find(':last')[0],
				title = this.$n('cap');
			h -= bl.offsetHeight;
			if (body)
				h -= zk(body.parentNode).padBorderHeight();
			if (title)
				h -= zk(title.parentNode).padBorderHeight();
		}
		h -= zk(n).padBorderHeight();
		var tb = this.$n('tb'),
			bb = this.$n('bb'),
			fb = this.$n('fb');
		if (tb) h -= tb.offsetHeight;
		if (bb) h -= bb.offsetHeight;
		if (fb) h -= fb.offsetHeight;
		return h;
	},
	_titleHeight: function (n) {
		var cap = this.$n('cap'),
			top = this.isFramable() ?
				jq(n).find('> div:first-child')[0].offsetHeight: 0;
		return cap ? cap.offsetHeight + top : top;
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
		zWatch.fireDown("onShow", null, this);
	},
	_beforeSlideUp: function (n) {
		zWatch.fireDown("onHide", null, this);
	},
	_initFloat: function () {
		var n = this.$n();
		if (!n.style.top && !n.style.left) {
			var xy = zk(n).revisedOffset();
			n.style.left = jq.px(xy[0]);
			n.style.top = jq.px(xy[1]);
		}

		n.style.position = "absolute";
		if (this.isMovable())
			this._initMove();

		this._syncShadow();

		if (this.isRealVisible()) {
			zk(n).cleanVisibility();
			this.setTopmost();
		}
	},
	_initMove: function (cmp) {
		var handle = this.$n('cap');
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
		} else if (this.isMaximized() || this.isMinimized()) {
			this._hideShadow();
		} else {
			var body = this.$n('body');
			if (body && zk(body).isRealVisible()) {
				if (!this._shadow) 
					this._shadow = new zk.eff.Shadow(this.$n(), {
						left: -4,
						right: 4,
						top: -2,
						bottom: 3,
						stackup: (zk.useStackup === undefined ? zk.ie6_: zk.useStackup)
					});
				this._shadow.sync();
			}
		}
	},
	_hideShadow: function () {
		var shadow = this._shadow;
		if (shadow) shadow.hide();
	},
	//super//
	bind_: function () {
		this.$supers('bind_', arguments);

		zWatch.listen({onSize: this, onShow: this, onHide: this});

		var uuid = this.uuid,
			$Panel = this.$class;

		if (this.isFloatable()) {
			zWatch.listen({onFloatUp: this});
			this.setFloating_(true);
			this._initFloat();
		}
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, onShow: this, onHide: this, onFloatUp: this});
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
		case this.$n('close'):
			this.fire('onClose');
			break;
		case this.$n('max'):
			this.setMaximized(!this.isMaximized());
			break;
		case this.$n('min'):
			this.setMinimized(!this.isMinimized());
			break;
		case this.$n('exp'):
			var body = this.$n('body'),
				open = body ? zk(body).isVisible() : this.isOpen();
				
			// force to open
			if (!open == this.isOpen())
				this._open = open;
			this.setOpen(!open);
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
		case this.$n('exp'):
			jq(this.$n('exp')).addClass(this.getZclass() + '-exp-over');
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
				$n = jq(this.$n('max'));
			if (this.isMaximized())
				$n.removeClass(zcls + '-maxd-over');
			$n.removeClass(zcls + '-max-over');
			break;
		case this.$n('min'):
			jq(this.$n('min')).removeClass(this.getZclass() + '-min-over');
			break;
		case this.$n('exp'):
			jq(this.$n('exp')).removeClass(this.getZclass() + '-exp-over');
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
			added = this.isOpen() ? '' : zcls + '-colpsd';
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
			if (this.firstChild == child || (this.nChildren == (this.caption ? 2 : 1)))
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
		case wgt.$n('close'):
		case wgt.$n('max'):
		case wgt.$n('min'):
		case wgt.$n('exp'):
			return true; //ignore special buttons
		}
		return false;
	},
	_aftermove: function (dg, evt) {
		var wgt = dg.control;
		wgt._syncShadow();
		var node = wgt.$n();
		wgt.fire('onMove', zk.copy({
			left: node.style.left,
			top: node.style.top
		}, evt.data), {ignorable: true});
	}
});
