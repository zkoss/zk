/* Menu.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:33     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _toggleClickableCSS(wgt, remove) {
		if (wgt.isListen('onClick')) {
			if (remove) 
				jq(wgt.$n()).removeClass(wgt.getZclass() + '-body-clk-over');
			else 
				jq(wgt.$n()).addClass(wgt.getZclass() + '-body-clk-over');
		}
	}
	function _doClick(wgt, evt) {
		if (wgt.isListen('onClick')) {
			var arrowWidth = wgt._getArrowWidth(), //note : /img/menu/btn-arrow.gif : width = 12
				node = wgt.$n(),
				clk = wgt.isTopmost() ? jq(node).find('TABLE'): jq(node),
				offsetWidth = zk(clk).offsetWidth(),
				clickArea = offsetWidth - arrowWidth,
				ofs = zk(clk).revisedOffset(),
				clickOffsetX = evt.domEvent.clientX - ofs[0];

			if (clickOffsetX > clickArea) {
				jq(wgt.getAnchor_()).addClass(wgt.getZclass() + '-body-seld');
				wgt.menupopup._shallClose = false;
				wgt._togglePopup();
				evt.stop();
			} else
				wgt.fireX(new zk.Event(wgt, 'onClick', evt.data));
				
		} else {
			jq(wgt.getAnchor_()).addClass(wgt.getZclass() + '-body-seld');
			wgt.menupopup._shallClose = false;
			wgt._togglePopup();
		}
	}

	function _isActiveItem(wgt) {
		return wgt.isVisible() && wgt.$instanceof(zul.menu.Menu);
	}
	function _nextVisibleMenu(menu) {
		for (var m = menu; m; m = m.nextSibling) {
			if (_isActiveItem(m))
				return m;
		}
		var mb = menu.parent;
		if (mb.firstChild == menu)
			return menu;
		return _nextVisibleMenu(mb.firstChild);
	}

	function _prevVisibleMenu(menu) {
		for (var m = menu; m; m = m.previousSibling) {
			if (_isActiveItem(m))
				return m;
		}
		var mb = menu.parent;
		if (mb.lastChild == menu)
			return menu;
		return _prevVisibleMenu(mb.lastChild);
	}
/**
 * An element, much like a button, that is placed on a menu bar.
 * When the user clicks the menu element, the child {@link Menupopup}
 * of the menu will be displayed.
 * This element is also used to create submenus (of {@link Menupopup}.
 * 
 * <p>Default {@link #getZclass}: z-mean.
 */
zul.menu.Menu = zk.$extends(zul.LabelImageWidget, {
	$define: {
		/** Returns the embedded content (i.e., HTML tags) that is
		 * shown as part of the description.
		 * <p>It is useful to show the description in more versatile way.
		 * <p>Default: empty ("").
		 * @return String
		 */
		/** Sets the embedded content (i.e., HTML tags) that is
		 * shown as part of the description.
		 *
		 * <p>It is useful to show the description in more versatile way.
		 * 
		 * <p>There is a way to create {@link zkex.inp.Colorbox} automatically by using
		 * #color=#RRGGBB, usage example <code>setContent("#color=FFFFFF")</code>
		 *
		 * @param String content
		 */
		content: function (content) {
			if (!content || content.length == 0) return;
			
			if (!this._contentHandler) {
				if (zk.feature.pe) {
					var self = this;
					zk.load('zkex.inp', null, function () { 
						self._contentHandler = new zkex.inp.ContentHandler(self, content);
					});
					return;
				}
				this._contentHandler = new zul.menu.ContentHandler(this, content);
			} else
				this._contentHandler.setContent(content);
		},
		image: function (v) {
			if (v && this._preloadImage) zUtl.loadImage(v);
			this.rerender();
		}
	},
	/**
	 * Opens the menupopup that belongs to the menu.
	 * <p>
	 * Note that this function is only applied when it is topmost menu, i.e. the parent of the menu is {@link Menubar}
	 * @since 6.0.0
	 */
	open: function () {
		if (this.desktop && this.isTopmost()) {
			jq(this.getAnchor_()).addClass(this.getZclass() + '-body-seld');
			var mb = this.getMenubar();
			if (mb._lastTarget)
				this.$class._rmActive(mb._lastTarget);
			mb._lastTarget = this;
			this.menupopup._shallClose = false;
			this._togglePopup();
		}
	},
	// since ZK 6.5.0 internal use only.
	getAnchor_: function () {
		return this.$n('a');
	},
	// since ZK 6.5.0 internal use only.
	getButton_: function () {
		return this.$n('b');
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = ['<span id="', this.uuid, '-img" class="', this.getZclass(), '-img"'];
			
		img.push(this._image ? ' style="background-image:url(' + this._image + ')"' : '', '></span>', label ? ' ' + label : '');
		return img.join('');
	},
	/** Returns whether this is an top-level menu, i.e., not owning
	 * by another {@link Menupopup}.
	 * @return boolean
	 */
	isTopmost: function () {
		return this._topmost;
	},
	beforeParentChanged_: function (newParent) {
		this._topmost = newParent && !(newParent.$instanceof(zul.menu.Menupopup));
		this.$supers("beforeParentChanged_", arguments);
	},
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments);
		return this.isTopmost() ?
			style + 'padding-left:4px;padding-right:4px;': style;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.menu.Menupopup)) {
			this.menupopup = child;

			if (this._contentHandler)
				this._contentHandler.destroy();
		}
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.menupopup) {
			this.menupopup = null;

			if (this._contentHandler)
				this._contentHandler.setContent(this._content);
		}
	},
	/** Returns the {@link Menubar} that contains this menu, or null if not available.
	 * @return zul.menu.Menubar
	 */
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	onShow: function () {
		if (this._contentHandler)
			this._contentHandler.onShow();
	},
	onFloatUp: function (ctl) {
		if (this._contentHandler)
			this._contentHandler.onFloatUp(ctl);
	},
	onHide: function () {
		if (this._contentHandler)
			this._contentHandler.onHide();
	},
	focus_: function (timeout, ignoreActive/* used for Menupopup.js*/) {
		if (this.isTopmost() && zk(this.getButton_()).focus(timeout)) {
			// fixed for pressing TAB key from menupopup when the menupopup
            // is the last one, in IE it will delay to show the active effect.
			// We have to use the ignoreActive to avoid adding the active effect
			// to the menu widget.
			if (ignoreActive) {
				this._ignoreActive = true;
			}
			return true;
		}
		return this.$supers('focus_', arguments);
	},
	// used for Menupopup.js
	_getPrevVisibleMenu: function () {
		var prev = this.previousSibling;
		if (!prev) {
			var mb = this.getMenubar();
			if (mb)
				prev = mb.lastChild;
		}
		return prev ? _prevVisibleMenu(prev) : this;
	},
	// used for Menupopup.js
	_getNextVisibleMenu: function () {
		var next = this.nextSibling;
		if (!next) {
			var mb = this.getMenubar();
			if (mb)
				next = mb.firstChild;
		}
		return next ? _nextVisibleMenu(next) : this;
	},
	doKeyDown_: function (evt) {
		
		// only support for the topmost menu
		if (this.isTopmost()) {
			var keyCode = evt.keyCode,
				mb = this.getMenubar();
			
			// switch the navigation key when in vertical view
			if ('vertical' == mb.getOrient()) {
				switch (keyCode) {
				case 38: //UP
					keyCode = 37;
					break;
				case 40: //DOWN
					keyCode = 39;
					break;
				case 37: //LEFT
					keyCode = 38;
					break;
				case 39: //RIGHT
					keyCode = 40;
					break;
				}
			}
			switch (keyCode) {
			case 38: //UP
				// 1. close the menupopup if any.
				// 2. make the menu as focus effect
				var pp = this.menupopup;
				if (pp && pp.isOpen()) {
					jq(this.getAnchor_()).removeClass(this.getZclass() + '-body-seld');
					pp.close();
				}
				this.$class._addActive(this); // keep the focus
				evt.stop();
				break;
			case 40: //DOWN
				// 1. open menupopup if any.
				// 2. pass the focus control to menupopup
				if (this.menupopup) {
					jq(this.getAnchor_()).addClass(this.getZclass() + '-body-seld');
					this.menupopup._shallClose = false;
					this.menupopup.open();
				}
				evt.stop();
				break;
			case 37: //LEFT
			case 39: //RIGHT
				// LEFT: 1. jump to the previous menu if any, otherwise, jump to the last one
				// RIGHT: 1. jump to the next menu if any, otherwise, jump to the first one
				var target = keyCode == 37 ? this._getPrevVisibleMenu() : this._getNextVisibleMenu();
				if (target)
					target.focus();
				evt.stop();
				break;
			case 13: //ENTER
				// 1. toggle the open/close status for the menupopup, if any.
				if (this.menupopup)
					_doClick(this, evt);
				evt.stop();
				break;
			case 9:
				// 1. deactive this menu, then it will jump to the next focus target.
				this.$class._rmActive(this);
				break;
			}
		}
		this.$supers('doKeyDown_', arguments);
	},
	doFocus_: function () {
		if (this.isTopmost()) {
			var menubar = this.getMenubar(),
				$menu = this.$class;

			// clear the previous active target, if any.
			if (menubar && menubar._lastTarget)
				$menu._rmActive(menubar._lastTarget);
			if (!this._ignoreActive)
				$menu._addActive(this);
		}
		// delete the variable used for IE
		delete this._ignoreActive;
		this.$supers('doFocus_', arguments);
	},
	bind_: function () {
		this.$supers(zul.menu.Menu, 'bind_', arguments);

		var anc = this.getAnchor_(),
			type = this._contentType;
		if (!this.isTopmost()) {
			var	n = this.$n();
			this.domListen_(anc, "onFocus", "doFocus_")
				.domListen_(anc, "onBlur", "doBlur_")
				.domListen_(n, "onMouseOver")
				.domListen_(n, "onMouseOut");
		} else {
			this.domListen_(this.getButton_(), "onFocus", "doFocus_") // used to handle keystroke
				.domListen_(anc, "onMouseOver")
				.domListen_(anc, "onMouseOut");
			if (this.isListen('onClick')) {
				jq(this.$n()).addClass(this.getZclass() + '-body-clk');
			}
		}

		if (this._contentHandler)
			this._contentHandler.bind();
	},
	unbind_: function () {
		if (!this.isTopmost()) {
			var anc = this.getAnchor_(),
				n = this.$n();
			this.domUnlisten_(anc, "onFocus", "doFocus_")
				.domUnlisten_(anc, "onBlur", "doBlur_")
				.domUnlisten_(n, "onMouseOver")
				.domUnlisten_(n, "onMouseOut");
		} else {
			var anc = this.getAnchor_();
			this.domUnlisten_(anc, "onMouseOver")
				.domUnlisten_(anc, "onMouseOut")
				.domUnlisten_(this.getButton_(), "onFocus", "doFocus_");
		}

		if (this._contentHandler)
			this._contentHandler.unbind();

		this.$supers(zul.menu.Menu, 'unbind_', arguments);
	},
	// used for overriding from different theme
	_getArrowWidth: function () {
		return 20;
	},
	doClick_: function (evt) {
		if (this.menupopup) {
			if (this.isTopmost())
				this.getMenubar()._lastTarget = this;
			// toggle the open/close status of menupopup/contenthandler
			_doClick(this, evt);
		} else {
			var content = this._contentHandler;
			if (content && !content.isOpen())
				content.onShow();
		}

	},
	doMouseOver_: function () {
		if (!this.isTopmost()) {
			var content = this._contentHandler;
			if (content && !content.isOpen())
				content.onShow();
		}
		this.$supers('doMouseOver_', arguments);
	},
	_togglePopup: function () {
		if (!this.menupopup.isOpen()){
			if(this.isTopmost())
				_toggleClickableCSS(this);
			this.menupopup.open();
		} else if (this.isTopmost()) 
			this.menupopup.close({sendOnOpen: true});
		else
			zk(this.menupopup.$n('a')).focus(); // force to get a focus 
	},
	_doMouseOver: function (evt) { //not zk.Widget.doMouseOver_
		var menubar = this.getMenubar();
		if (menubar) {
			menubar._bOver = true;
			menubar._noFloatUp = false;
		}
		if (this.$class._isActive(this)) return;

		var	topmost = this.isTopmost();
		if(topmost)
			_toggleClickableCSS(this);
		if (topmost && zk.ie && !jq.isAncestor(this.getAnchor_(), evt.domTarget))
				return; // don't activate

		if (this.menupopup)
			this.menupopup._shallClose = false;
		if (!topmost) {
			zWatch.fire('onFloatUp', this); //notify all
			if (this.menupopup && !this.menupopup.isOpen()) this.menupopup.open();
		} else {
			if (this.menupopup && menubar._autodrop) {
				zWatch.fire('onFloatUp', this); //notify all
				if (!this.menupopup.isOpen()) this.menupopup.open();
			} else {
				var target = menubar._lastTarget;
				if (target && target != this && target.menupopup
						&& target.menupopup.isVisible()) {
					target.menupopup.close({sendOnOpen:true});
					this.$class._rmActive(target);
					if (this.menupopup) this.menupopup.open();
				}
			}
		}
		this.$class._addActive(this);
	},
	_doMouseOut: function (evt) { //not zk.Widget.doMouseOut_
		var menubar = this.getMenubar();
		if (menubar) menubar._bOver = false;
		this._updateHoverImage(); // remove hover image if any
		if (!zk.ie && jq.isAncestor(this.getAnchor_(), evt.domEvent.relatedTarget || evt.domEvent.toElement))
			return; // don't deactivate
	
		var topmost = this.isTopmost(),
			menupopup = this.menupopup;
		if (topmost) { //implies menubar
			this.$class._rmActive(this, true);
			if (menupopup && menubar._autodrop) {
				if (menupopup.isOpen())
					menupopup._shallClose = true; //autodrop -> autoclose if mouseout
				menubar._closeOnOut();
			}
		} else if (!menupopup || !menupopup.isOpen())
			this.$class._rmActive(this);
		else if (menupopup && menubar && menubar._autodrop)
			menubar._closeOnOut();
	},
	//@Override
	getImageNode: function () {
		if (!this._eimg && (this._image || this._hoverImage)) {
			var n = this.$n();
			if (n) 
				this._eimg = this.getButton_();
		}
		return this._eimg;
	},
	ignoreDescendantFloatUp_: function (des) {
		return des && des.$instanceof(zul.menu.Menupopup);
	}
}, {
	_isActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getAnchor_() : wgt.$n(),
			menupopup = wgt.menupopup,
			cls = wgt.getZclass();
		cls += top ? menupopup && menupopup.isOpen() ? '-body-seld' : '-body-over' : '-over';
		return jq(n).hasClass(cls);
	},
	_addActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getAnchor_() : wgt.$n(),
			menupopup = wgt.menupopup,
			cls = wgt.getZclass();
		cls += top ? menupopup && menupopup.isOpen() ? '-body-seld' : '-body-over' : '-over';
		jq(n).addClass(cls);
		if (top) {
			var mb = wgt.getMenubar();
			if (mb)
				mb._lastTarget = wgt;
		}
		if (!top && wgt.parent.parent.$instanceof(zul.menu.Menu))
			this._addActive(wgt.parent.parent);
	},
	_rmActive: function (wgt, ignoreSeld/* used for mouseout when topmost*/) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getAnchor_() : wgt.$n(),
			zcls = wgt.getZclass(),
			cls = zcls + (top ? (!ignoreSeld && wgt.menupopup.isOpen()) ? '-body-seld' : '-body-over' : '-over');
		var anode = jq(n);
		anode.removeClass(cls);
		if(top && !(anode.hasClass(zcls + '-body-seld') || anode.hasClass(zcls + '-body-over')))
			_toggleClickableCSS(wgt, true);
	}
});

zul.menu.ContentHandler = zk.$extends(zk.Object, {
	 $init: function(wgt, content) {
		this._wgt = wgt;
		this._content = content;
	 },
	 setContent: function (content) {
	 	if (this._content != content || !this._pp) {
			this._content = content;
			this._wgt.rerender();	
		}
	 },
	 redraw: function (out) {
	 	var wgt = this._wgt,
			zcls = wgt.getZclass();

	 	out.push('<div id="', wgt.uuid, '-cnt-pp" class="', zcls,
		'-cnt-pp" style="display:none"><div class="', zcls,'-cnt-body">', this._content, '</div></div>');
	 },
	 bind: function () {
	 	var wgt = this._wgt;
	 	if (!wgt.menupopup) {
			wgt.domListen_(wgt.$n(), 'onClick', 'onShow');
			zWatch.listen({onFloatUp: wgt, onHide: wgt});
		}
		
		this._pp = jq('#' + wgt.uuid + '-' + 'cnt-pp')[0];
	 },
	 unbind: function () {
	 	var wgt = this._wgt;
	 	if (!wgt.menupopup) {
			if (this._shadow) {
				this._shadow.destroy();
				this._shadow = null;
			}
			wgt.domUnlisten_(wgt.$n(), 'onClick', 'onShow');
			zWatch.unlisten({onFloatUp: wgt, onHide: wgt});
		}

		this._pp = null;
	 },
	 isOpen: function () {
		 var pp = this._pp;
		 return (pp && zk(pp).isVisible());
	 },
	 onShow: function () {
	 	var wgt = this._wgt,
			pp = this._pp;
		if (!pp) return;
			
		pp.style.width = pp.style.height = "auto";
		pp.style.position = "absolute";
		pp.style.overflow = "auto";
		pp.style.display = "block";
		pp.style.zIndex = "88000";
			
		jq(pp).zk.makeVParent();
		zWatch.fireDown("onVParent", this);

		zk(pp).position(wgt.$n(), this.getPosition());
		this.syncShadow();
	 },
	 onHide: function () {
		var pp = this._pp;
		if (!pp || !zk(pp).isVisible()) return;

		pp.style.display = "none";
		jq(pp).zk.undoVParent();
		zWatch.fireDown("onVParent", this);

		this.hideShadow();
	 },
	 onFloatUp: function (ctl) {
		if (!zUtl.isAncestor(this._wgt, ctl.origin))
			this.onHide();
	 },
	 syncShadow: function () {
	 	if (!this._shadow)
			this._shadow = new zk.eff.Shadow(this._wgt.$n("cnt-pp"), {stackup:(zk.useStackup === undefined ? zk.ie6_: zk.useStackup)});
		this._shadow.sync();
	 },
	 hideShadow: function () {
	 	this._shadow.hide();
	 },
	 destroy: function () {
	 	this._wgt.rerender();
	 },
	 getPosition: function () {
	 	var wgt = this._wgt;
		if (wgt.isTopmost()) {
			var bar = wgt.getMenubar();
			if (bar)
				return 'vertical' == bar.getOrient() ? 'end_before' : 'after_start';
		}
		return 'end_before';
	},
	deferRedrawHTML_: function (out) {
		var tag = this.isTopmost() ? 'td' : 'li';
		out.push('<', tag, this.domAttrs_({domClass:1}), ' class="z-renderdefer"></', tag,'>');
	}
});

})();