/* Groupbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:39:24     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Groupbox = zk.$extends(zul.Widget, {
	_open: true,
	_closable: true,

	isLegend: function () {
		return this._mold == 'default';
	},
	isOpen: function () {
		return this._open;
	},
	setOpen: function (open, fromServer) {
		if (this._open != open) {
			this._open = open;

			var node = this.getNode();
			if (node) {
				var panel = this.getPanelNode();
				if (panel) { //!legend
					if (open) zAnima.slideDown(this, panel, {afterAnima: this._afterSlideDown});
					else zAnima.slideUp(this, panel, {beforeAnima: this._beforeSlideUp});
				} else {
					zDom[open ? 'rmClass': 'addClass'](node, this.getZclass() + "-collapsed");
					zWatch.fireDown(open ? 'onVisible': 'onHide', -1, this);
				}
				if (!fromServer) this.fire('onOpen', open);
			}
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
		this.rerender(zk.Skipper.nonCaptionSkipper);
	},
	_contentAttrs: function () {
		var html = ' class="', s = this._cntSclass;
		if (s) html += s + ' ';
		html += this.getZclass() + '-cnt"';

		s = this._cntStyle;
		if (!this.isLegend() && this.caption) s = 'border-top:0;' + (s ? s: '');
		if (s) html += ' style="' + s + '"';
		return html;
	},

	//watch//
	onSize: _zkf = function () {
		var hgh = this.getNode().style.height;
		if (hgh && hgh != "auto") {
			var n = this.getCaveNode();
			if (n) {
				if (zk.ie6Only) n.style.height = "";
				n.style.height =
					zDom.revisedHeight(n, zDom.vflexHeight(n.parentNode), true)
					+ "px";
					//we use n.parentNode(=this.getPanelNode()) to calc vflex,
					//so we have to subtract margin, too
			}
		}
		setTimeout(this.proxy(this._fixShadow), 500);
			//shadow raraly needs to fix so OK to delay for better performance
			//(getShadowNode() a bit slow due to zDom.$)
	},
	onVisible: _zkf,
	_afterSlideDown: function (n) {
		zWatch.fireDown("onVisible", -1, this);
	},
	_beforeSlideUp: function (n) {
		zWatch.fireDown("onHide", -1, this);
	},
	_fixShadow: function () {
		var sdw = this.getShadowNode();
		if (sdw)
			sdw.style.display =
				zk.parseInt(zDom.getStyle(this.getCaveNode(), "border-bottom-width")) ? "": "none";
				//if no border-bottom, hide the shadow
	},
	updateDomStyle_: function () {
		this.$supers('updateDomStyle_', arguments);
		if (this.desktop) this.onSize();
	},

	//super//
	focus: function (timeout) {
		if (this.desktop) {
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
		return zcls ? zcls: this.isLegend() ? "z-fieldset": "z-groupbox";
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		if (!this.isLegend()) {
			zWatch.listen("onSize", this);
			zWatch.listen("onVisible", this);
		}
	},
	unbind_: function () {
		this._epanel = this._ecave = this._esdw = null;

		this.$supers('unbind_', arguments);
	},
	getPanelNode: function () {
		var n = this._epanel;
		if (!n) n = this._epanel = zDom.$(this.uuid + '$panel');
		return n;
	},
	getCaveNode: function () {
		var n = this._ecave;
		if (!n) n = this._ecave = zDom.$(this.uuid + '$cave');
		return n;
	},
	getShadowNode: function () {
		var n = this._esdw;
		if (!n) n = this._esdw = zDom.$(this.uuid + '$sdw');
		return n;
	},

	appendChild: function (child) {
		if (this.$supers('appendChild', arguments)) {
			if (child.$instanceof(zul.wgt.Caption))
				this.caption = child;
			return true;
		}
		return false;
	},
	insertBefore: function (child) {
		if (this.$supers('insertBefore', arguments)) {
			if (child.$instanceof(zul.wgt.Caption))
				this.caption = child;
			return true;
		}
		return false;
	},
	removeChild: function (child) {
		if (this.$supers('removeChild', arguments)) {
			if (child == this.caption)
				this.caption = null;
			return true;
		}
		return false;
	},

	domClass_: function () {
		var html = this.$supers('domClass_', arguments);
		if (!this._open) {
			if (html) html += ' ';
			html += this.getZclass() + '-collapsed';
		}
		return html;
	},
	isImportantEvent_: function (evtnm) {
		return 'onOpen' == evtnm;
	}
});