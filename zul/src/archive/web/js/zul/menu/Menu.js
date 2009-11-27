/* Menu.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:33     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.menu.Menu = zk.$extends(zul.LabelImageWidget, {
	$define: {
		content: function (content) {
			this._setContent();
		}
	},
	_getContentType: function (content) {
		if (!content) return;
		
		if (content.indexOf('#color') == 0 && zk.feature.professional) {
			if (!zk.isLoaded('zkex.inp')) {
				zk.load('zkex.inp');
				if (zk.loading) {
					zk.afterLoad(this.proxy(this._setContent));
					return;
				} else
					return 'color';
			} else
				return 'color';
		}
		return 'content';
	},
	_setContent: function () {
		var content = this._content;
		if (!content || this.menupopup)
			return;
				
		var oldType = this._contentType,
			type = this._getContentType(content);
		
		if (oldType != type)
			this._destoryContent();

		this._contentType = type;
		switch (type) {
		case 'color':
			var hex = content.substr(content.indexOf('#color') + '#color'.length + 1);
			
			if (!this._color)
				this._color = new zkex.inp.Color(hex);
			else
				this._color.setHex(hex);
			
			if (!this._picker && !this._palette) {
				this._picker = new zkex.inp.PickerPop();
				this.appendChild(this._picker);
				this._palette = new zkex.inp.PalettePop();
				this.appendChild(this._palette);
				this.rerender();		
			}
			break;
		case 'content':
			this.rerender();
			break;
		}
	},
	_destoryContent: function () {
		var type = this._contentType;
		if (!type) return;
		
		switch (type) {
		case 'color':
			var picker = this._picker,
				palette = this._palette;
			if (this._color) {
				this._color = null;
			}
			if (picker && palette) {
				this.removeChild(picker);
				this.removeChild(palette);
				this._picker = this._palette = this._contentType = null;
				this.rerender();
			}
			break;
		case 'content':
			this.rerender();
			break;
		}
	},
	_openMenuContent: function () {
		var pp = this.$n("cnt-pp");
		if (!pp) return;
			
		pp.style.width = pp.style.height = "auto";
		pp.style.position = "absolute";
		pp.style.overflow = "auto";
		pp.style.display = "block";
		pp.style.zIndex = "88000";
			
		jq(pp).zk.makeVParent();
		zk(pp).position(this.$n(), this.getPosition());
		this.syncShadow('cnt-pp');
	},
	_closeMenuContent: function () {
		var pp = this.$n("cnt-pp");
		if (!pp || !zk(pp).isVisible()) return;

		pp.style.display = "none";
		jq(pp).zk.undoVParent();
		this.hideShadow('cnt-pp');
	},
	_openContent: function (evt) {
		var type = this._contentType;
		if (!type) return;
		
		switch (type) {
		case 'color':
			this.openPalette();
			break;
		case 'content':
			this._openMenuContent();
			break;
		}
	},
	openPalette: function () {
		if (this._palette)
			this._palette.open(this.getPosition());
	},
	openPicker: function () {
		if (this._picker)
			this._picker.open(this.getPosition());
	},
	syncShadow: function (target) {		
		switch (target) {
		case 'palette-pp':
			if (!this._paletteShadow)
				this._paletteShadow = new zk.eff.Shadow(this.$n("palette-pp"), {stackup:(zk.useStackup === undefined ? zk.ie6_: zk.useStackup)});
			this._paletteShadow.sync();
			break;
		case 'picker-pp':
			if (!this._pickerShadow)
				this._pickerShadow = new zk.eff.Shadow(this.$n("picker-pp"), {stackup:(zk.useStackup === undefined ? zk.ie6_: zk.useStackup)});
			this._pickerShadow.sync();
			break;
		case 'cnt-pp':
			if (!this._shadow)
				this._shadow = new zk.eff.Shadow(this.$n("cnt-pp"), {stackup:(zk.useStackup === undefined ? zk.ie6_: zk.useStackup)});
			this._shadow.sync();
			break;
		}
	},
	hideShadow: function (target) {
		switch (target) {
		case 'palette-pp':
			this._paletteShadow.hide();
			break;
		case 'picker-pp':
			this._pickerShadow.hide();
			break;
		case 'cnt-pp':
			this._shadow.hide();
			break;
		}
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = ['<span id="', this.uuid, '-img" class="', this.getZclass(), '-img"'];
			
		img.push(this._image ? ' style="background-image:url(' + this._image + ')"' : '', '></span>', label ? ' ' + label : '');
		return img.join('');
	},
	isTopmost: function () {
		return this._topmost;
	},
	beforeParentChanged_: function (newParent) {
		this._topmost = newParent && !(newParent.$instanceof(zul.menu.Menupopup));
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu" : this._zclass;
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
			
			this._destoryContent();
		}
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.menupopup) {
			this.menupopup = null;
			this._setContent();
		}
	},
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	getPicker: function () {
		return this._picker;
	},
	getPalette: function () {
		return this._palette;
	},
	getPosition: function () {
		if (this.isTopmost()) {
			var bar = this.getMenubar();
			if (bar)
				return 'vertical' == bar.getOrient() ? 'end_before' : 'after_start';
		}
		return 'end_before';
	},
	getCurrColor: function () {
		return this._color;
	},
	_closeParentPopup: function () {
		if (!this.isTopmost()) {
			for (var p = this.parent; p; p = p.parent) {
				if (p.$instanceof(zul.menu.Menupopup)) {
					p.close();
					break;
				}
			}
		}
	},
	onColorChanged: function (hex) {
		var img = this.$n('img');
		if (img)
			img.style.backgroundColor = hex;
		this._color.setHex(hex);
		this.fire("onChange", {color: hex, value: hex}, {toServer:true}, 150);
		this._closeParentPopup();
	},
	onColorCanceled: function () {
		this._closeParentPopup();
	},
	onFloatUp: function (ctl) {
		var type = this._contentType;
		if (type == 'content' && !zUtl.isAncestor(this.parent, ctl.origin))
			this._closeMenuContent();
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		var anc = this.$n('a'),
			type = this._contentType;
		if (!this.isTopmost()) {
			var	n = this.$n();
			this.domListen_(anc, "onFocus", "doFocus_")
				.domListen_(anc, "onBlur", "doBlur_")
				.domListen_(n, "onMouseOver")
				.domListen_(n, "onMouseOut");
		} else {
			this.domListen_(anc, "onMouseOver")
				.domListen_(anc, "onMouseOut");
		}
		
		if (!this.menupopup && type) {
			this.domListen_(this.$n(), 'onClick', '_openContent');
			
			switch (type) {
			case 'color':
				var img = this.$n('img');
				if (img)
					img.style.backgroundColor = this._color.getHex();
				break;
			case 'content':
				zWatch.listen({onFloatUp: this, onHide: this});
				break;
			}
		}
	},
	onHide: function () {
		if (this._contentType == 'content')
			this._closeMenuContent();
	},
	unbind_: function () {
		if (!this.isTopmost()) {
			var anc = this.$n('a'),
				n = this.$n();
			this.domUnlisten_(anc, "onFocus", "doFocus_")
				.domUnlisten_(anc, "onBlur", "doBlur_")
				.domUnlisten_(n, "onMouseOver")
				.domUnlisten_(n, "onMouseOut");
		} else {
			var anc = this.$n('a');
			this.domUnlisten_(anc, "onMouseOver")
				.domUnlisten_(anc, "onMouseOut");
		}
		
		var type = this._contentType;
		if (!this.menupopup && type) {
			this.domUnlisten_(this.$n(), 'onClick', '_openContent');
			
			switch (type) {
			case 'color':
				if (this._paletteShadow) {
					this._paletteShadow.destroy();
					this._paletteShadow = null;
				}
				if (this._pickerShadow) {
					this._pickerShadow.destroy();
					this._pickerShadow = null;
				}
				break;
			case 'content':
				if (this._shadow) {
					this._shadow.destroy();
					this._shadow = null;
				}
				zWatch.unlisten({onFloatUp: this, onHide: this});
				break;
			}
		}
		this.$supers('unbind_', arguments);
	},
	doClick_: function (evt) {
		if (this.isTopmost() && !jq.isAncestor(this.$n('a'), evt.domTarget)) return;
		
		var node = this.$n();
		if (this.menupopup) {
			jq(this.$n('a')).addClass(this.getZclass() + '-body-seld');
			this.menupopup._shallClose = false;
			if (this.isTopmost())
				this.getMenubar()._lastTarget = this;
			if (this.isListen('onClick')) {
				var arrorWidth = 12, //note : /img/menu/btn-arrow.gif : width = 12
					clk = jq(node).find('TABLE'),
					offsetWidth = zk(clk).offsetWidth(),
					clickArea = offsetWidth - arrorWidth,
					ofs = zk(clk).revisedOffset(),
					clickOffsetX = evt.domEvent.clientX - ofs[0];

				if (clickOffsetX > clickArea) {
					this._openPopup();
					Event.stop(evt);
				} else {
					jq(this.$n('a')).removeClass(this.getZclass() + '-body-seld');
					this.fireX(evt);
				}		
			} else {
				this._openPopup();	
			}
		}
	},
	_openPopup: function () {
		if (!this.menupopup.isOpen())
			this.menupopup.open();	
		else {
			var anc = this.menupopup.$n('a');
			if (anc) anc.focus(); // force to get a focus 
		}			
	},
	_doMouseOver: function (evt) { //not zk.Widget.doMouseOver_
		if (this.$class._isActive(this)) return;

		var	topmost = this.isTopmost();
		if (topmost && zk.ie && !jq.isAncestor(this.$n('a'), evt.domTarget))
				return; // don't activate

		if (!topmost) {
			if (this.menupopup) this.menupopup._shallClose = false;
			zWatch.fire('onFloatUp', this); //notify all
			if (this.menupopup && !this.menupopup.isOpen()) this.menupopup.open();
		} else {
			var menubar = this.getMenubar();
			if (this.menupopup && menubar.isAutodrop()) {
				menubar._lastTarget = this;
				this.menupopup._shallClose = false;
				zWatch.fire('onFloatUp', this); //notify all
				if (!this.menupopup.isOpen()) this.menupopup.open();
			} else {
				var target = menubar._lastTarget;
				if (target && target != this && menubar._lastTarget.menupopup
						&& menubar._lastTarget.menupopup.isVisible()) {
					menubar._lastTarget.menupopup.close({sendOnOpen:true});
					this.$class._rmActive(menubar._lastTarget);
					menubar._lastTarget = this;
					if (this.menupopup) this.menupopup.open();
				}
			}
		}
		this.$class._addActive(this);
	},
	_doMouseOut: function (evt) { //not zk.Widget.doMouseOut_
		if (zk.ie && jq.isAncestor(this.$n('a'), evt.domEvent.relatedTarget || evt.domEvent.toElement))
			return; // don't deactivate
	
		var	topmost = this.isTopmost();
		if (topmost) {
			this.$class._rmActive(this);
			if (this.menupopup && this.getMenubar().isAutodrop()) {
				if (this.menupopup.isOpen()) this.menupopup._shallClose = true;
				zWatch.fire('onFloatUp', this, {timeout: 10}); //notify all
			}
		} else if (this.menupopup && !this.menupopup.isOpen())
			this.$class._rmActive(this);
	}
}, {
	_isActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.$n('a') : wgt.$n(),
			cls = wgt.getZclass() + (top ? '-body-over' : '-over');
		return jq(n).hasClass(cls);
	},
	_addActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.$n('a') : wgt.$n(),
			cls = wgt.getZclass() + (top ? '-body-over' : '-over');
		jq(n).addClass(cls);
		if (!top && wgt.parent.parent.$instanceof(zul.menu.Menu))
			this._addActive(wgt.parent.parent);
	},
	_rmActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.$n('a') : wgt.$n(),
			cls = wgt.getZclass() + (top ? '-body-over' : '-over');
		jq(n).removeClass(cls);
	}
});