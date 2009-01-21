/* Menupopup.js

	Purpose:
		
	Description:
		
	History:
		Thu Jan 15 09:02:34     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.menu.Menupopup = zk.$extends('zul.wgt.Popup', {
	_curIndex: -1,
	
	_getCurrentIndex: function () {
		return this._curIndex;
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu-popup" : this._zclass;
	},
	getSelectedItem: function () {
		return this.getSelectedItemAt(this.getSelectedIndex());
	},
	_isActiveItem: function (wgt) { 
		return wgt.isVisible() && (wgt.$instanceof(zul.menu.Menu) || (wgt.$instanceof(zul.menu.Menuitem) && !wgt.isDisabled()));
	},
	_currentChild: function (index) {
		var index = index != null ? index : this._curIndex;
		for (var w = this.firstChild, k = -1; w; w = w.nextSibling) 
			if (this._isActiveItem(w) && ++k === index)
				return w;
		return null;
	},
	_previousChild: function (wgt) {
		wgt = wgt ? wgt.previousSibling : this.lastChild;
		var lastChild = this.lastChild == wgt;
		for (; wgt; wgt = wgt.previousSibling) 
			if (this._isActiveItem(wgt)) {
				this._curIndex--;
				return wgt;
			}
		if (lastChild) return null; // avoid deadloop;
		this.curIndex = 0;
		for (wgt = this.firstChild; wgt; wgt = wgt.nextSibling)
			if (this._isActiveItem(wgt)) this._curIndex++;
		return this._previousChild();
	},
	_nextChild: function (wgt) {
		wgt = wgt ? wgt.nextSibling : this.firstChild;
		var firstChild = this.firstChild == wgt;
		for (; wgt; wgt = wgt.nextSibling) 
			if (this._isActiveItem(wgt)) {
				this._curIndex++;
				return wgt;
			}
		if (firstChild) return null; // avoid deadloop;
		this._curIndex = -1;	
		return this._nextChild();		
	},
	_syncShadow: function () {
		if (!this._shadow)
			this._shadow = new zk.eff.Shadow(this.getNode(), {stackup:zk.ie6Only});
		this._shadow.sync();
	},
	_hideShadow: function () {
		if (this._shadow) this._shadow.hide();
	},
	close: function () {
		this.$supers('close', arguments);
		this._hideShadow();
		var menu = this.parent;
		if (menu.$instanceof(zul.menu.Menu) && menu.isTopmost())
			zDom.rmClass(menu.getSubnode('a'), menu.getZclass() + "-btn-seld");
		
		var item = this._currentChild();
		if (item) item.$class._rmActive(item);
		this._curIndex = -1;
		this.$class._rmActive(this);
		// TODO for columns' menu zk.fire(pp, "close");
	},
	open: function (ref, offset, position, opts) {
		if (this.parent.$instanceof(zul.menu.Menu)) {
			if (!offset) {
				ref = this.parent.getSubnode('a');
				if (!position)
					if (this.parent.isTopmost())
						position = this.parent.parent.getOrient() == 'vertical'
							? 'end_before' : 'after_start';
					else position = 'end_before';
			}
		}
		this.$super('open', ref, offset, position, opts);
	},
	onFloatUp: function(wgt) {
		if (!this.isVisible()) 
			return;
		var org = wgt;
		if (this.parent.menupopup == this && !this.parent.isTopmost() && !this.parent.$class._isActive(this.parent)) {
			this.close({sendOnOpen:true});
			return;
		}
		
		// check if the wgt belongs to the popup
		for (var floatFound; wgt; wgt = wgt.parent) {
			if (wgt == this || (wgt.menupopup == this && !this._shallClose)) {
				if (!floatFound)
					this.setTopmost();
				return;
			}
			floatFound = floatFound || wgt.isFloating_();
		}
		
		// check if the popup is one of the wgt's children
		for (var floatFound, wgt = this.parent; wgt; wgt = wgt.parent) {
			if (wgt == org) {
				if (this._shallClose) break;
				if (!floatFound) 
					this.setTopmost();
				return;
			}
			floatFound = floatFound || wgt.isFloating_();
		}
		
		// check if the popup is an active menu
		if (!this._shallClose && this.parent.$instanceof(zul.menu.Menu)) {
			var menubar = this.parent.getMenubar();
			if (menubar && menubar._lastTarget == this.parent)
				return;
		}
		this.close({sendOnOpen:true});
	},
	onVisible: function (wgt) {
		if (zk.ie7) {
			var pp = this.getNode();
			if (!pp.style.width) {// Bug 2105158 and Bug 1911129
				var ul = this.getSubnode('cave');
				pp.style.width = ul.offsetWidth + zDom.frameWidth(pp) + "px";
			}
		}
		this._syncShadow();
		var anc = this.getSubnode('a');
		if (anc) {
			
			// just in case
			if (zk.ie)
				zDom.cleanVisibility(this.getNode());
				
			anc.focus();
		}
	},
	onHide: function (wgt) {
		this._hideShadow();
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen('onHide', this);
	},
	unbind_: function () {
		if (this._shadow)
			this._shadow.destroy();
		this._shadow = null;
		zWatch.unlisten('onHide', this);
		this.$supers('unbind_', arguments);
	},
	doKeyDown_: function (evt) {
		var w = this._currentChild(),
			keycode = evt.data.keyCode;
		switch (keycode) {
		case 38: //UP
		case 40: //DOWN
			if (w) w.$class._rmActive(w);
			w = keycode == 38 ? this._previousChild(w) : this._nextChild(w);
			if (w) w.$class._addActive(w);
			break;
		case 37: //LEFT
			this.close();
			
			if (this.parent.$instanceof(zul.menu.Menu) && !this.parent.isTopmost()) {
				var pp = this.parent.parent;
				if (pp) {
					var anc = pp.getSubnode('a');
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
				w.doClick_(new zk.Event(w, 'onClick', null, {
					ctl: true
				}, evt.nativeEvent));
				zWatch.fire('onFloatUp', null, w); //notify all
				this.close({sendOnOpen:true});
			}
			break;
		}
		evt.stop();
		this.$supers('doKeyDown_', arguments);
	},
	doMouseOver_: function (evt) {
		this._shallClose = false;
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		this._shallClose = true;
		this.$supers('doMouseOut_', arguments);
	}	
}, {
	_rmActive: function (wgt) {
		if (wgt.parent.$instanceof(zul.menu.Menu)) {
			wgt.parent.$class._rmActive(wgt.parent);
			if (!wgt.parent.isTopmost())
				this._rmActive(wgt.parent.parent);
		}
	}
});
