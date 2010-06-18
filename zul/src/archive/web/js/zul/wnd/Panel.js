/* Panel.js

	Purpose:

	Description:

	History:
		Mon Jan 12 18:31:03 2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Panel is a container that has specific functionality and structural components
 * that make it the perfect building block for application-oriented user interfaces.
 * The Panel contains bottom, top, and foot toolbars, along with separate header,
 * footer and body sections. It also provides built-in collapsible, closable,
 * maximizable, and minimizable behavior, along with a variety of pre-built tool 
 * buttons that can be wired up to provide other customized behavior. Panels can
 * be easily embedded into any kind of ZUL component that is allowed to have children
 * or layout component. Panels also provide specific features like float and move.
 * Unlike {@link zul.wnd.Window}, Panels can only be floated and moved inside its parent
 * node, which is not using {@link _global_.jqzk#makeVParent} function. In other words,
 * if Panel's parent node is an relative position, the floated panel is only inside
 * its parent, not the whole page.
 * The second difference of {@link zul.wnd.Window} is that Panel is not an independent ID
 * space, so the ID of each child can be used throughout the panel.
 * 
 * <p>Events:<br/>
 * onMove, onOpen, onZIndex, onMaximize, onMinimize, and onClose.<br/>
 * 
 * <p>Default {@link #getZclass}: z-panel.
 * 
 */
zul.wnd.Panel = zk.$extends(zul.Widget, {
	_border: "none",
	_title: "",
	_open: true,
	_minheight: 100,
	_minwidth: 200,

	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onMaximize: this, onClose: this, onMove: this, onSize: this.onSizeEvent}, -1000);
	},

	$define: {
		/**
		 * Sets the minimum height in pixels allowed for this panel.
		 * If negative, 100 is assumed.
		 * <p>Default: 100. 
		 * <p>Note: Only applies when {@link #isSizable()} = true.
		 * @param int minheight
		 */
		/**
		 * Returns the minimum height.
		 * <p>Default: 100.
		 * @return int
		 */
		minheight: null, //TODO
		/**
		 * Sets the minimum width in pixels allowed for this panel. If negative,
		 * 200 is assumed.
		 * <p>Default: 200. 
		 * <p>Note: Only applies when {@link #isSizable()} = true.
		 * @param int minwidth
		 */
		/**
		 * Returns the minimum width.
		 * <p>Default: 200.
		 * @return int
		 */
		minwidth: null, //TODO
		/** Sets whether the panel is sizable.
		 * If true, an user can drag the border to change the panel width.
		 * <p>Default: false.
		 * @param boolean sizable
		 */
		/** Returns whether the panel is sizable.
		 * @return boolean
		 */
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
		/**
		 * Sets whether to render the panel with custom rounded borders.
		 * <p>Default: false.
		 * @param boolean framable
		 */
		/**
		 * Returns whether to render the panel with custom rounded borders.
		 * <p>Default: false.
		 * @return boolean
		 */
		framable: _zkf = function () {
			this.rerender(); //TODO: like Window, use _updateDomOuter
		},
		/**
		 * Sets whether to move the panel to display it inline where it is rendered.
		 * 
		 * <p>Default: false;
		 * <p>Note that this method only applied when {@link #isFloatable()} is true.
		 * @param boolean movable
		 */
		/**
		 * Returns whether to move the panel to display it inline where it is rendered.
		 * <p>Default: false.
		 * @return boolean
		 */
		movable: _zkf,
		/**
		 * Sets whether to float the panel to display it inline where it is rendered.
		 * 
		 * <p>Note that by default, setting floatable to true will cause the
	     * panel to display at default offsets, which depend on the offsets of 
	     * the embedded panel from its element to <i>document.body</i> -- because the panel
	     * is absolute positioned, the position must be set explicitly by {@link #setTop(String)}
	     * and {@link #setLeft(String)}. Also, when floatable a panel you should always
	     * assign a fixed width, otherwise it will be auto width and will expand to fill
	     * to the right edge of the viewport.
	     * @param boolean floatable
		 */
		/**
		 * Returns whether to float the panel to display it inline where it is rendered.
		 * <p>Default: false.
		 * @return boolean
		 */
		floatable: _zkf,
		/**
	     * Sets whether to display the maximizing button and allow the user to maximize
	     * the panel, when a panel is maximized, the button will automatically
	     * change to a restore button with the appropriate behavior already built-in
	     * that will restore the panel to its previous size.
	     * <p>Default: false.
	     * 
		 * <p>Note: the maximize button won't be displayed if no title or caption at all.
		 * @param boolean maximizable
		 */
		/**
		 * Returns whether to display the maximizing button and allow the user to maximize
	     * the panel. 
	     * <p>Default: false.
	     * @return boolean
		 */
		maximizable: _zkf,
		/**
	     * Sets whether to display the minimizing button and allow the user to minimize
	     * the panel. Note that this button provides no implementation -- the behavior
	     * of minimizing a panel is implementation-specific, so the MinimizeEvent
	     * event must be handled and a custom minimize behavior implemented for this
	     * option to be useful.
	     * 
	     * <p>Default: false. 
		 * <p>Note: the maximize button won't be displayed if no title or caption at all.
		 * @param boolean minimizable
		 */
		/**
		 * Returns whether to display the minimizing button and allow the user to minimize
	     * the panel. 
	     * <p>Default: false.
	     * @return boolean
		 */
		minimizable: _zkf,
		/**
		 * Sets whether to show a toggle button on the title bar.
		 * <p>Default: false.
		 * <p>Note: the toggle button won't be displayed if no title or caption at all.
		 * @param boolean collapsible
		 */
		/**
		 * Returns whether to show a toggle button on the title bar.
		 * <p>Default: false.
		 * @return boolean
		 */
		collapsible: _zkf,
		/**
		 * Sets whether to show a close button on the title bar.
		 * If closable, a button is displayed and the onClose event is sent
		 * if an user clicks the button.
		 *
		 * <p>Default: false.
		 *
		 * <p>Note: the close button won't be displayed if no title or caption at all.
		 * @param boolean closable
		 */
		/**
		 * Returns whether to show a close button on the title bar.
		 * @return boolean
		 */
		closable: _zkf,
		/** 
		 * Sets the border (either none or normal).
		 * @param String border the border. If null or "0", "none" is assumed.
		 */
		/** 
		 * Returns the border.
		 * The border actually controls via {@link zul.wnd.Panelchildren#getSclass()}. 
		 * In fact, the name of the border (except "normal") is generate as part of 
		 * the style class used for the content block.
		 * Refer to {@link zul.wnd.Panelchildren#getSclass()} for more details.
		 *
		 * <p>Default: "none".
		 * @return String
		 */
		border: _zkf,
		/** 
		 * Sets the title.
		 * @param String title
		 */
		/** 
		 * Returns the title.
		 * Besides this attribute, you could use {@link zul.wgt.Caption} to define
		 * a more sophisticated caption (aka., title).
		 * <p>If a panel has a caption whose label ({@link zul.wgt.Caption#getLabel})
		 * is not empty, then this attribute is ignored.
		 * <p>Default: empty.
		 * @return String
		 */
		title: _zkf,
		/** 
		 * Opens or closes this Panel.
		 * @param boolean open
		 */
		/**
		 * Returns whether this Panel is open.
		 * <p>Default: true.
		 * @return boolean
		 */
		open: function (open, fromServer) {
			var node = this.$n();
			if (node) {
				var zcls = this.getZclass(),
					$body = jq(this.$n('body'));
				if ($body[0] && !$body.is(':animated')) {
					if (open) {
						jq(node).removeClass(zcls + '-colpsd');
						$body.zk.slideDown(this);
					} else {
						jq(node).addClass(zcls + '-colpsd');
						this._hideShadow();
						// windows 2003 with IE6 will cause an error when user toggles the panel in portallayout.
						if (zk.ie6_ && !node.style.width)
							node.runtimeStyle.width = "100%";
						$body.zk.slideUp(this);
					}
					if (!fromServer) this.fire('onOpen', {open:open});
				}
			}
		},
		/**
		 * Sets whether the panel is maximized, and then the size of the panel will depend 
		 * on it to show a appropriate size. In other words, if true, the size of the
		 * panel will count on the size of its offset parent node whose position is
		 * absolute (by {@link #isFloatable()}) or its parent node. Otherwise, its size
		 * will be original size. Note that the maximized effect will run at client's
		 * sizing phase not initial phase.
		 * 
		 * <p>Default: false.
		 * @param boolean maximized
		 */
		/**
		 * Returns whether the panel is maximized.
		 * @return boolean
		 */
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
					var sh = zk.ie6_ && $op[0].clientHeight == 0 ? $op[0].offsetHeight - $op.zk.borderHeight() : $op[0].clientHeight;
					
					if (zk.isLoaded('zkmax.layout') && this.parent.$instanceof(zkmax.layout.Portalchildren)) {
						var layout = this.parent.parent;
						if (layout.getMaximizedMode() == 'whole') {
							this._inWholeMode = true;
							var p = layout.$n();
							sh = zk.ie6_ && p.clientHeight == 0 ? p.offsetHeight - jq(p).zk.borderHeight() : p.clientHeight;
							node._scrollTop = p.parentNode.scrollTop; 
							p.parentNode.scrollTop = 0;
							$n.makeVParent();
							
							node._pos = node.style.position;
							node._ppos = p.style.position;
							node._zindex = node.style.zIndex;
							node.style.position = 'absolute';
							this.setFloating_(true);
							this.setTopmost();
							p.appendChild(node);
							p.style.position = 'relative';
							if (!p.style.height) {
								p.style.height = jq.px0(sh);
								node._pheight = true;
							}
						}
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
					var sw = zk.ie6_ && $op[0].clientWidth == 0 ? $op[0].offsetWidth - $op.zk.borderWidth() : $op[0].clientWidth;
					
					if (!floated) {
						sw -= $op.zk.paddingWidth();
						sw = $n.revisedWidth(sw);
						sh -= $op.zk.paddingHeight();
						sh = $n.revisedHeight(sh);
					}
					s.width = jq.px0(sw);
					s.height = jq.px0(sh);
					this._lastSize = {l:l, t:t, w:w, h:h};
	
					// restore.
					s.top = "0";
					s.left = "0";

					// resync
					w = s.width;
					h = s.height;
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
						
					if (this._inWholeMode) {
						$n.undoVParent();
						node.style.position = node._pos;
						node.style.zIndex = node._zindex;
						this.setFloating_(false);
						var p = this.parent.parent.$n();
						p.style.position = node._ppos;
						p.parentNode.scrollTop = node._scrollTop;
						if (node._pheight)
							p.style.height = "";
						node._scrollTop = node._ppos = node._zindex = node._pos = node._pheight = null;
						this._inWholeMode = false;
					}
				}
				if (!fromServer && isRealVisible) {
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
		/**
		 * Sets whether the panel is minimized.
		 * <p>Default: false.
		 * @param boolean minimized
		 */
		/**
		 * Returns whether the panel is minimized.
		 * <p>Default: false.
		 * @return boolean
		 */
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
			zWatch.fireDown('beforeSize', this);
			zWatch.fireDown('onSize', this);
		}
	},
	setWidth: function () {
		this.$supers('setWidth', arguments);
		if (this.desktop) {
			zWatch.fireDown('beforeSize', this);
			zWatch.fireDown('onSize', this);
		}
	},
	setTop: function () {
		this._hideShadow();
		this.$supers('setTop', arguments);
		this.zsync();

	},
	setLeft: function () {
		this._hideShadow();
		this.$supers('setLeft', arguments);
		this.zsync();
	},
	updateDomStyle_: function () {
		this.$supers('updateDomStyle_', arguments);
		if (this.desktop) {
			zWatch.fireDown('beforeSize', this);
			zWatch.fireDown('onSize', this);
		}
	},
	/**
	 * Adds the toolbar of the panel by these names, "tbar", "bbar", and "fbar".
	 * "tbar" is the name of top toolbar, and "bbar" the name of bottom toolbar,
	 * and "fbar" the name of foot toolbar.
	 * 
	 * @param String name "tbar", "bbar", and "fbar".
	 * @param zul.wgt.Toolbar toolbar
	 * @return boolean
	 */
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
		if (!this.inServer || !this.isListen('onClose', {asapOnly: 1})) //let server handle if in server
			this.parent.removeChild(this); //default: remove
	},
	onMove: function (evt) {
		this._left = evt.left;
		this._top = evt.top;
	},
	onMaximize: function (evt) {
		var data = evt.data;
		this._top = data.top;
		this._left = data.left;
		this._height = data.height;
		this._width = data.width;
	},
	onSizeEvent: function (evt) {
		var data = evt.data,
			node = this.$n(),
			s = node.style;
			
		this._hideShadow();
		if (data.width != s.width) {
			s.width = data.width;
		}
		if (data.height != s.height) {
			s.height = data.height;
			this._fixHgh();
		}
				
		if (data.left != s.left || data.top != s.top) {
			s.left = data.left;
			s.top = data.top;
			
			this.fire('onMove', zk.copy({
				left: node.style.left,
				top: node.style.top
			}, evt.data), {ignorable: true});
		}
		
		this.zsync();
		var self = this;
		setTimeout(function() {
			zWatch.fireDown('beforeSize', self);
			zWatch.fireDown('onSize', self);
		}, zk.ie6_ ? 800: 0);
	},
	beforeSize: function() {
		// Bug 2974370: IE 6 will get the wrong parent's width when self's width greater then parent's
		if (this.isMaximized() && !this.__maximized)
			jq(this.$n()).width(0);
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
			s.width = jq.px0(sw);
			if (wgt.isOpen()) {
				var sh = zk.ie6_ && $op[0].clientHeight == 0 ? $op[0].offsetHeight - $op.zk.borderHeight() : $op[0].clientHeight;
				if (!floated) {
					sh -= $op.zk.paddingHeight();
					sh = $op.zk.revisedHeight(sh);
				}
				s.height = jq.px0(sh);
			}
		}
		return function(ctl) {
			this._hideShadow();
			if (this.isMaximized()) {
				if (!this.__maximized)
					syncMaximized(this);
				this.__maximized = false; // avoid deadloop
			}
			
			if (this.tbar)
				ctl.fireDown(this.tbar);
			if (this.bbar)
				ctl.fireDown(this.bbar);
			if (this.fbar)
				ctl.fireDown(this.fbar);
			this._fixHgh();
			this.zsync();
		};
	})(),
	onShow: _zkf,
	onHide: function () {
		this._hideShadow();
	},
	_fixHgh: function () {
		var pc;
		if (!(pc=this.panelchildren) || pc.z_rod || !this.isRealVisible()) return;
		var n = this.$n(),
			body = pc.$n(),
			hgh = n.style.height;
		if (zk.ie6_ && ((hgh && hgh != "auto" )|| body.style.height)) body.style.height = "0";
		if (hgh && hgh != "auto")
			zk(body).setOffsetHeight(this._offsetHeight(n));
		if (zk.ie6_) zk(body).redoCSS();
	},
	_offsetHeight: function (n) {
		var h = n.offsetHeight - this._titleHeight(n);
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
		var isFramable = this.isFramable(),
			cap = this.$n('cap'),
			cap2 = isFramable ? jq(n).find('> div:first-child').next()[0] : cap,
			top = isFramable ? jq(n).find('> div:first-child')[0].offsetHeight: 0;
		return cap ? cap2.offsetHeight + top : top;
	},
	onFloatUp: function (ctl) {
		if (!this.isVisible() || !this.isFloatable())
			return; //just in case

		for (var wgt = ctl.origin; wgt; wgt = wgt.parent) {
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
	_makeSizer: function () {
		if (!this._sizer) {
			var Panel = this.$class;
			this._sizer = new zk.Draggable(this, null, {
				stackup: true, draw: Panel._drawsizing,
				starteffect: Panel._startsizing,
				ghosting: Panel._ghostsizing,
				endghosting: Panel._endghostsizing,
				ignoredrag: Panel._ignoresizing,
				endeffect: Panel._aftersizing});
		}
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

		this.zsync();

		if (this.isRealVisible())
			this.setTopmost();
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
	zsync: function () {
		this.$supers('zsync', arguments);

		if (!this.isFloatable()) {
			if (this._shadow) {
				this._shadow.destroy();
				this._shadow = null;
			}
		} else {
			var body = this.$n('body');
			if (body && zk(body).isRealVisible()) {
				if (!this._shadow) 
					this._shadow = new zk.eff.Shadow(this.$n(), {
						left: -4, right: 4, top: -2, bottom: 3
					});
					
				if (this.isMaximized() || this.isMinimized())
					this._hideShadow();
				else this._shadow.sync();
			}
		}
	},
	_hideShadow: function () {
		var shadow = this._shadow;
		if (shadow) shadow.hide();
	},
	//super//
	bind_: function (desktop, skipper, after) {
		this.$supers(zul.wnd.Panel, 'bind_', arguments);

		zWatch.listen({onSize: this, onShow: this, onHide: this});

		// Bug 2974370
		if (zk.ie6_)
			zWatch.listen({beforeSize: this});

		var uuid = this.uuid,
			$Panel = this.$class;

		if (this._sizable)
			this._makeSizer();
		
		this.domListen_(this.$n(), 'onMouseOver');
			
		if (this.isFloatable()) {
			zWatch.listen({onFloatUp: this});
			this.setFloating_(true);
			this._initFloat();
			if (!zk.css3)
				jq.onzsync(this); //sync shadow if it is implemented with div
		}
		
		if (this.isMaximizable() && this.isMaximized()) {
			var self = this;
			after.push(function() {
				self._maximized = false;
				self.setMaximized(true, true);				
			});
		}
	},
	unbind_: function () {
		if (this._inWholeMode) {
			var node = this.$n();
			zk(node).undoVParent();
			var p = this.parent;
			if (p && (p = p.parent) && (p = p.$n())) {
				p.style.position = node._ppos;
				p.parentNode.scrollTop = node._scrollTop;
			}
			node._scrollTop = node._ppos = node._zindex = node._pos = null;
			this._inWholeMode = false;
		}
		zWatch.unlisten({onSize: this, onShow: this, onHide: this, onFloatUp: this});
		if (zk.ie6_)
			zWatch.unlisten({beforeSize: this});
		this.setFloating_(false);
		
		if (!zk.css3) jq.unzsync(this);

		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = null;
		}
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}
		this.domUnlisten_(this.$n(), 'onMouseOver');
		this.$supers(zul.wnd.Panel, 'unbind_', arguments);
	},
	_doMouseOver: function (evt) {
		if (this._sizer) {
			var n = this.$n(),
				c = this.$class._insizer(n, zk(n).revisedOffset(), evt.pageX, evt.pageY),
				handle = this.isMovable() ? this.$n('cap') : false;
			if (!this.isMaximized() && this.isOpen() && c) {
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
		if (!this.childReplacing_)
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
		dg.control.zsync();
	},
	// drag sizing
	_startsizing: zul.wnd.Window._startsizing,
	_ghostsizing: zul.wnd.Window._ghostsizing,
	_endghostsizing: zul.wnd.Window._endghostsizing,
	_insizer: zul.wnd.Window._insizer,
	_ignoresizing: function (dg, pointer, evt) {
		var el = dg.node,
			wgt = dg.control;
		if (wgt.isMaximized() || !wgt.isOpen()) return true;

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
	_aftersizing: zul.wnd.Window._aftersizing,
	_drawsizing: zul.wnd.Window._drawsizing
});
