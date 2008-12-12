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
	importantEvents_: {onOpen:1},

	isLegend: function () {
		return this._mold == 'default';
	},
	isOpen: function () {
		return this._open;
	},
	setOpen: function (open, fromServer) {
		if (this._open != open) {
			this._open = open;

			if (this.node) {
				var panel = this.epanel;
				if (panel) { //!legend
					if (open) zAnima.slideDown(this, panel, {afterAnima: this._afterSlideDown});
					else zAnima.slideUp(this, panel, {beforeAnima: this._beforeSlideUp});
				} else {
					zDom[open ? 'rmClass': 'addClass'](this.node, this.getZclass() + "-collapsed");
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
		if (this.node) this.rerender(zk.Skipper.nonCaptionSkipper);
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
	onSize: function () {
		var n = this.ecave;
		if (n) {
			var hgh = this.node.style.height;
			if (hgh && hgh != "auto") {
				if (zk.ie6Only) n.style.height = "";
				n.style.height =
					zDom.revisedHeight(n, zDom.vflexHeight(n.parentNode), true)
					+ "px";
					//we use n.parentNode(=this.epanel) to calc vflex,
					//so we have to subtract margin, too
			}
		}
		var sdw = this.esdw;
		if (sdw)
			sdw.style.display =
				zk.parseInt(zDom.getStyle(n, "border-bottom-width")) ? "": "none";
			//if no border-bottom, hide the shadow
	},
	_afterSlideDown: function (n) {
		zWatch.fireDown("onVisible", -1, this);
	},
	_beforeSlideUp: function (n) {
		zWatch.fireDown("onHide", -1, this);
	},

	//super//
	focus: function (timeout) {
		if (this.node) {
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
	bind_: function (desktop, skipper) {
		this.$super('bind_', desktop, skipper);

		if (!this.isLegend()) {
			var uuid = this.uuid;
			this.epanel = zDom.$(uuid + '$panel');
			this.ecave = zDom.$(uuid + '$cave');
			this.esdw = zDom.$(uuid + '$sdw');

			this.onSize(); //fix height and shadow
		}
	},
	unbind_: function (skipper) {
		this.epanel = this.ecave = this.esdw = null;

		this.$super('unbind_', skipper);
	},

	appendChild: function (child) {
		if (this.$super('appendChild', child)) {
			if (child.$instanceof(zul.wgt.Caption))
				this.caption = child;
			return true;
		}
		return false;
	},
	insertBefore: function (child, sibling) {
		if (this.$super('insertBefore', child, sibling)) {
			if (child.$instanceof(zul.wgt.Caption))
				this.caption = child;
			return true;
		}
		return false;
	},
	removeChild: function (child) {
		if (this.$super('removeChild', child)) {
			if (child == this.caption)
				this.caption = null;
			return true;
		}
		return false;
	},

	domClass_: function (no) {
		var html = this.$super('domClass_', no);
		if (!this._open) {
			if (html) html += ' ';
			html += this.getZclass() + '-collapsed';
		}
		return html;
	}
});