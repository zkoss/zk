/* Groupbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:39:24     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Groupbox = zk.$extends(zul.Widget, {
	_open: true,
	_closable: true,

	$define: { //zk.def
		open: function (open, fromServer) {
			var node = this.getNode();
			if (node) {
				var panel = this.getSubnode('panel');
				if (panel) { //!legend
					if (open) zAnima.slideDown(this, panel, {afterAnima: this._afterSlideDown});
					else zAnima.slideUp(this, panel, {beforeAnima: this._beforeSlideUp});
				} else {
					zDom[open ? 'rmClass': 'addClass'](node, this.getZclass() + "-colpsd");
					zWatch.fireDown(open ? 'onShow': 'onHide', {visible:true}, this);
				}
				if (!fromServer) this.fire('onOpen', {open:open});
			}
		},
		closable: _zkf = function () {
			this._updateDomOuter();
		},
		contentStyle: _zkf,
		contentSclass: _zkf
	},

	isLegend: function () {
		return this._mold == 'default';
	},

	_updateDomOuter: function () {
		this.rerender(zk.Skipper.nonCaptionSkipper);
	},
	_contentAttrs: function () {
		var html = ' class="', s = this._contentSclass;
		if (s) html += s + ' ';
		html += this.getZclass() + '-cnt"';

		s = this._contentStyle;
		if (!this.isLegend() && this.caption) s = 'border-top:0;' + (s ? s: '');
		if (s) html += ' style="' + s + '"';
		return html;
	},

	//watch//
	onSize: _zkf = function () {
		var hgh = this.getNode().style.height;
		if (hgh && hgh != "auto") {
			var n = this.getSubnode('cave');
			if (n) {
				if (zk.ie6Only) n.style.height = "";
				n.style.height =
					zDom.revisedHeight(n, zDom.vflexHeight(n.parentNode), true)
					+ "px";
					//we use n.parentNode(=this.getSubnode('panel')) to calc vflex,
					//so we have to subtract margin, too
			}
		}
		setTimeout(this.proxy(this._fixShadow), 500);
			//shadow raraly needs to fix so OK to delay for better performance
			//(getSubnode('sdw') a bit slow due to zDom.$)
	},
	onShow: _zkf,
	_afterSlideDown: function (n) {
		zWatch.fireDown("onShow", {visible:true}, this);
	},
	_beforeSlideUp: function (n) {
		zWatch.fireDown("onHide", {visible:true}, this);
	},
	_fixShadow: function () {
		var sdw = this.getSubnode('sdw');
		if (sdw)
			sdw.style.display =
				zk.parseInt(zDom.getStyle(this.getSubnode('cave'), "border-bottom-width")) ? "": "none";
				//if no border-bottom, hide the shadow
	},
	updateDomStyle_: function () {
		this.$supers('updateDomStyle_', arguments);
		if (this.desktop) this.onSize();
	},

	//super//
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
		return zcls ? zcls: this.isLegend() ? "z-fieldset": "z-groupbox";
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		if (!this.isLegend()) {
			zWatch.listen({onSize: this, onShow: this});
		}
	},
	unbind_: function () {
		if (!this.isLegend()) {
			zWatch.unlisten({onSize: this, onShow: this});
		}
		this.$supers('unbind_', arguments);
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

	domClass_: function () {
		var html = this.$supers('domClass_', arguments);
		if (!this._open) {
			if (html) html += ' ';
			html += this.getZclass() + '-colpsd';
		}
		return html;
	}
});
