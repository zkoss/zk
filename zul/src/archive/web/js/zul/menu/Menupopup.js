/* Menupopup.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:34     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _getMenu(wgt) {
		var p = wgt.parent;
		return p.$instanceof(zul.menu.Menu) ? p: null;
	}
	function _isActiveItem(wgt) {
		return wgt.isVisible() && (wgt.$instanceof(zul.menu.Menu) || (wgt.$instanceof(zul.menu.Menuitem) && !wgt.isDisabled()));
	}
	//child must be _currentChild()
	function _prevChild(popup, child) {
		if (child)
			while (child = child.previousSibling)
				if (_isActiveItem(child)) {
					popup._curIndex--;
					return child;
				}

		//return the last
		popup._curIndex = -1;
		for (var w = popup.firstChild; w; w = w.nextSibling)
			if (_isActiveItem(w)) { //return the first one
				child = w;
				popup._curIndex++;
			}
		return child;
	}
	//child must be _currentChild()
	function _nextChild(popup, child) {
		if (child)
			while (child = child.nextSibling)
				if (_isActiveItem(child)) {
					popup._curIndex++;
					return child;
				}

		//return the first
		for (var w = popup.firstChild; w; w = w.nextSibling)
			if (_isActiveItem(w)) { //return the first one
				popup._curIndex = 0;
				return w;
			}
	}
	function _currentChild(popup) {
		var index = popup._curIndex;
		if (index >= 0)
			for (var w = popup.firstChild, k = 0; w; w = w.nextSibling)
				if (_isActiveItem(w) && k++ == index)
					return w;
	}

/**
 * A container used to display menus. It should be placed inside a
 * {@link Menu}.
 *
 * <p>Default {@link #getZclass}: z-menu-popup.
 */
zul.menu.Menupopup = zk.$extends(zul.wgt.Popup, {
	_curIndex: -1,

	getZclass: function () {
		return this._zclass == null ? "z-menu-popup" : this._zclass;
	},
	zsync: function () {
		this.$supers('zsync', arguments);

		if (!this._shadow)
			this._shadow = new zk.eff.Shadow(this.$n());
		this._shadow.sync();
	},
	_hideShadow: function () {
		if (this._shadow) this._shadow.hide();
	},
	close: function () {
		if (this.isOpen())
			zul.menu._nOpen--;

		this.$supers('close', arguments);
		jq(this.$n()).hide(); // force to hide the element
		this._hideShadow();
		var menu;
		if ((menu = _getMenu(this)) && menu.isTopmost())
			jq(menu.$n('a')).removeClass(menu.getZclass() + "-body-seld");

		var item = _currentChild(this);
		if (item) item.$class._rmActive(item);
		this._curIndex = -1;
		this.$class._rmActive(this);
	},
	open: function (ref, offset, position, opts) {
		if (!this.isOpen())
			zul.menu._nOpen++;
		var menu;
		if (menu = _getMenu(this)) {
			if (!offset) {
				ref = menu.$n('a');
				if (!position)
					if (menu.isTopmost())
						position = menu.parent.getOrient() == 'vertical'
							? 'end_before' : 'after_start';
					else position = 'end_before';
			}
		}
		this.$super('open', ref, offset, position, opts || {sendOnOpen: true, disableMask: true});
			//open will fire onShow which invoke this.zsync()

		var mb;
		// adjust only for topmost menu in horizontal.
		if (menu && menu.isTopmost() && (mb = menu.getMenubar()) && mb.getOrient() == 'horizontal') {
			var n;
			if (n = this.$n())
				n.style.top = jq.px0(zk.parseInt(n.style.top) + 
					zk.parseInt(jq(this.getMenubar()).css('paddingBottom')));
		}
	},
	shallStackup_: function () {
		return false;
	},
	setTopmost: function () {
		this.$supers('setTopmost', arguments);
		this.zsync();
	},
	onFloatUp: function(ctl) {
		if (!this.isVisible())
			return;

		var org = ctl.origin;
		if (this.parent.menupopup == this && !this.parent.isTopmost() && !this.parent.$class._isActive(this.parent)) {
			this.close({sendOnOpen:true});
			return;
		}

		// check if org belongs to the popup
		for (var floatFound, wgt = org; wgt; wgt = wgt.parent) {
			if (wgt == this || (wgt.menupopup == this && !this._shallClose)) {
				if (!floatFound)
					this.setTopmost();
				return;
			}
			floatFound = floatFound || wgt.isFloating_();
		}

		// check if the popup is one of org's children
		if (org && org.$instanceof(zul.menu.Menu)) {
			for (var floatFound, wgt = this; wgt = wgt.parent;) {
				if (wgt == org) {
					if (this._shallClose)
						break; //close it
					if (!floatFound)
						this.setTopmost();
					return;
				}
				floatFound = floatFound || wgt.isFloating_();
			}

			//No need to check _lastTarget since we have to close any other open menupopup
		}
		this.close({sendOnOpen:true});
	},
	onShow: function () {
		if (zk.ie7_) {
			var pp = this.$n();
			if (!pp.style.width) {// Bug 2105158 and Bug 1911129
				var ul = this.$n('cave');
				if (ul.childNodes.length)
					pp.style.width = ul.offsetWidth + zk(pp).padBorderWidth() + "px";
			}
		}
		this.zsync();
		var anc = this.$n('a');
		if (anc) {
			if(zk(anc).isRealVisible()) {
				anc.focus();
				zk.currentFocus = this; //Bug 2807475: (IE only) s.t. _docMouseDown will focus later (since menupop becomes invisible)
			}
		}
	},
	onHide: function () {
		if (this.isOpen())
			this.close();
		this._hideShadow();
	},
	bind_: function () {
		this.$supers(zul.menu.Menupopup, 'bind_', arguments);
		zWatch.listen({onHide: this, onResponse: this});
		if (!zk.css3) jq.onzsync(this);
	},
	unbind_: function () {
		if (this.isOpen())
			this.close();
		if (this._shadow)
			this._shadow.destroy();
		if (!zk.css3) jq.unzsync(this);
		this._shadow = null;
		zWatch.unlisten({onHide: this, onResponse: this});
		this.$supers(zul.menu.Menupopup, 'unbind_', arguments);
	},
	onResponse: function () {
		if (!this.isOpen())
			return; // Bug 2950364
		if (zk.ie7_) {
			var pp = this.$n();
		
    		// Bug 2935985
    		pp.style.width = '';
    		
    		// Bug 2105158 and Bug 1911129
    		var ul = this.$n('cave');
    		if (ul.childNodes.length) // Bug 2784736
    			pp.style.width = ul.offsetWidth + zk(pp).padBorderWidth() + "px";
		}
		this.zsync();
		
		this.$supers('onResponse', arguments); //Bug #2870616
	},
	doKeyDown_: function (evt) {
		var w = _currentChild(this),
			menu,
			keyCode = evt.keyCode;
		switch (keyCode) {
		case 38: //UP
		case 40: //DOWN
			if (w) w.$class._rmActive(w);
			w = keyCode == 38 ? _prevChild(this, w) : _nextChild(this, w);
			if (w) w.$class._addActive(w);
			break;
		case 37: //LEFT
			this.close();

			if (((menu = _getMenu(this))) && !menu.isTopmost()) {
				var pp = menu.parent;
				if (pp) {
					var anc = pp.$n('a');
					if (anc) anc.focus();
				}
			}
			break;
		case 39: //RIGHT
			if (w && w.$instanceof(zul.menu.Menu) && w.menupopup)
				w.menupopup.open();
			break;
		case 13: //ENTER
			if (w && w.$instanceof(zul.menu.Menuitem)) {
				//{} for emulate as onClick, escape the checking data == null at serverside
				w.doClick_(new zk.Event(w, 'onClick',{}));
				zWatch.fire('onFloatUp', w); //notify all
				this.close({sendOnOpen:true});
			}
			break;
		}
		if (keyCode != 9) // TAB
			evt.stop(); // Bug ZK-442
		this.$supers('doKeyDown_', arguments);
	},
	/** Returns the {@link Menubar} that contains this menuitem, or null if not available.
	 * @return zul.menu.Menubar
	 * @since 5.0.5
	 */
	getMenubar: zul.menu.Menu.prototype.getMenubar,
	doMouseOver_: function (evt) {
		var menubar = this.getMenubar();
		if (menubar) menubar._bOver = true;
		this._shallClose = false;
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		var menubar = this.getMenubar();
		if (menubar) menubar._bOver = false;
		this.$supers('doMouseOut_', arguments);
	}
}, {
	_rmActive: function (wgt) {
		if (wgt.parent.$instanceof(zul.menu.Menu)) {
			wgt.parent.$class._rmActive(wgt.parent);
		}
	}
});
zul.menu._nOpen = 0;
})();