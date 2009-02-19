/* Panel.js

	Purpose:
		
	Description:
		
	History:
		Mon Jan 12 18:31:03     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zPkg.load('zul.wgt');
zul.panel.Panel = zk.$extends(zul.Widget, {
	_border: "none",
	_title: "",
	_open: true,
	
	$init: function () {
		this.$supers('$init', arguments);
		this.listen('onClose', this, null, -1000);
		this.listen('onMove', this, null, -1000);
	},
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
	isOpen: function () {
		return this._open;
	},
	setOpen: function (open, fromServer) {
		if (this._open != open) {
			this._open = open;
			var node = this.getNode();
			if (node) {
				var zcls = this.getZclass(),
					body = this.getSubnode('body');
				if (body) {
					if (open) {
						zDom.rmClass(node, zcls + '-collapsed');
						zAnima.slideDown(this, body, {
							afterAnima: this._afterSlideDown
						});
					} else {
						zDom.addClass(node, zcls + '-collapsed');
						this._hideShadow();
						
						// windows 2003 with IE6 will cause an error when user toggles the panel in portallayout.
						if (zk.ie6Only && !node.style.width)
							node.runtimeStyle.width = "100%";
							
						zAnima.slideUp(this, body, {
							beforeAnima: this._beforeSlideUp
						});
					}
				}
				if (fromServer) this.fire('onOpen', open);
			}
		}
	},
	isFramable: function () {
		return this._framable;
	},
	setFramable: function (framable) {
		if (this._framable != framable) {
			this._framable = framable;
			this.rerender();
		}
	},
	setMovable: function (movable) {
		if (this._movable != movable) {
			this._movable = movable;
			this.rerender();
		}
	},
	isMovable: function () {
		return this._movable;
	},
	isFloatable: function () {
		return this._floatable;
	},
	setFloatable: function (floatable) {
		if (this._floatable != floatable) {
			this._floatable = floatable;
			this.rerender();
		}
	},
	isMaximized: function () {
		return this._maximized;
	},
	setMaximized: function (maximized) {
		if (this._maximized != maximized) {
			this._maximized = maximized;
			// TODO
		}
	},
	isMaximizable: function () {
		return this._maximizable;
	},
	setMaximizable: function (maximizable) {
		if (this._maximizable != maximizable) {
			this._maximizable = maximizable;
			this.rerender();
		}
	},
	isMinimized: function () {
		return this._minimized;
	},
	setMinimized: function (minimized, fromServer) {
		if (this._minimized != minimized) {
			this._minimized = minimized;
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
					zWatch.fireDown('onVisible', {visible:true}, this);
				}
				if (!fromServer) {
					var wgt = this;
					this.fire('onMinimize', {
						left: s.left,
						top: s.top,
						width: s.width,
						height: s.height,
						minimized: minimized,
						marshal: wgt.$class._onMinimizeMarshal
					});
				}
			}
		}
	},
	isMinimizable: function () {
		return this._minimizable;
	},
	setMinimizable: function (minimizable) {
		if (this._minimizable != minimizable) {
			this._minimizable = minimizable;
			this.rerender();
		}
	},
	isCollapsible: function () {
		return this._collapsible;
	},
	setCollapsible: function (collapsible) {
		if (this._collapsible != collapsible) {
			this._collapsible = collapsible;
			this.rerender();
		}
	},
	isClosable: function () {
		return this._closable;
	},
	setClosable: function (closable) {
		if (this._closable != closable) {
			this._closable = closable;
			this.rerender();
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
			this.rerender();
		}
	},
	getTitle: function () {
		return this._title;
	},
	setTitle: function (title) {
		if (!title) title = "";
		if (this._title != title) {
			this._title = title;
			this.rerender();
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
		this._left = evt.data[0];
		this._top = evt.data[1];
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
	onHide: function () {
		this._hideShadow();
	},
	_fixWdh: zk.ie7 ? function () {
		if (!this.isFramable() || !this.panelchildren) return;
		var n = this.getNode(),
			cm = this.panelchildren.getNode().parentNode
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
		if (!this.panelchildren || !this.isRealVisible()) return;
		var n = this.getNode(),
			body = this.panelchildren.getNode(),
			hgh = n.style.height;
		if (zk.ie6Only && ((hgh && hgh != "auto" )|| body.style.height)) body.style.height = "0px";
		if (hgh && hgh != "auto")
			body.style.height = zDom.revisedHeight(body, n.offsetHeight - this._frameHeight(n) - 1, true) + 'px';
	},
	_frameHeight: function (n) {
		var h = zDom.frameHeight(n) + this._titleHeight(n),
			tbar = this.getSubnode('tbar'), bbar = this.getSubnode('bbar');
	    if (this.isFramable()) {
			var body = this.getSubnode('body'),
				ft = zDom.lastChild(body, "DIV"),
				title = this.getSubnode('cap');
	        h += ft.offsetHeight;
			if (this.panelchildren)
				h += zDom.frameHeight(this.panelchildren.getNode().parentNode);
			if (title)
		        h += zDom.frameHeight(title.parentNode);
	    } else {
			var fbar = this.getSubnode('fbar');
			if (fbar) h += fbar.offsetHeight;
		}
		if (tbar) h += tbar.offsetHeight;
		if (bbar) h += bbar.offsetHeight;
	    return h;
	},
	_titleHeight: function (n) {
		var cap = this.getSubnode('cap');
		return cap ? cap.offsetHeight : 
				this.isFramable() ?
					zDom.firstChild(n, "DIV").firstChild.firstChild.offsetHeight : 0;
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
		zWatch.fireDown("onVisible", {visible:true}, this);
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
				handle: handle, overlay: true, stackup: true,
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
		zWatch.listen("onVisible", this);
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
		zWatch.unlisten("onVisible", this);
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
		switch (evt.nativeTarget) {
		case this.getSubnode('close'):
			this.fire('onClose');
			evt.stop();
			break;
		case this.getSubnode('max'):
			// TODO
			break;
		case this.getSubnode('min'):
			if (this.isMinimizable())
				this.setMinimized(!this.isMinimized());
			break;
		case this.getSubnode('toggle'):
			if (this.isCollapsible())
				this.setOpen(!this.isOpen());
			break;
		}
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
		case this.getSubnode('toggle'):
			zDom.addClass(this.getSubnode('toggle'), this.getZclass() + '-toggle-over');
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
		case this.getSubnode('toggle'):
			zDom.rmClass(this.getSubnode('toggle'), this.getZclass() + '-toggle-over');
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
			addded = this.isOpen() ? '' : zcls + '-collapsed';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.wgt.Caption))
			this.caption = child;
		else if (child.$instanceof(zul.panel.Panelchildren))
			this.panelchildren = child;
		else if (child.$instanceof(zul.wgt.Toolbar)) {
			if (this.firstChild == child)
				this.tbar = child;
			else if (this.lastChild == child && child.previousSibling.$instanceof(zul.wgt.Toolbar))
				this.fbar = child;
			else if (child.previousSibling.$instanceof(zul.panel.Panelchildren))
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
}, {
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
		switch (zEvt.target(evt)) {
			case wgt.getSubnode('close'):
			case wgt.getSubnode('max'):
			case wgt.getSubnode('min'):
			case wgt.getSubnode('toggle'):
				return true; //ignore special buttons
		}
		return false;
	},
	_aftermove: function (dg, evt) {
		var wgt = dg.control;
		wgt._syncShadow();
		var keys = zEvt.keyMetaData(evt),
			node = wgt.getNode(),
			x = zk.parseInt(node.style.left),
			y = zk.parseInt(node.style.top);
		wgt.fire('onMove', {
			x: x + 'px',
			y: y + 'px',
			keys: keys,
			marshal: wgt.$class._onMoveMarshal
		}, {ignorable: true});
	},
	_onMoveMarshal: function () {
		return [this.x, this.y, this.keys ? this.keys.marshal(): ''];
	},
	_onMinimizeMarshal: function(){
		return [this.left, this.top, this.width, this.height, this.minimized];
	}
});
