/* Groupbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:39:24     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Groupbox = zk.$extends(zul.Widget, {
	_open: true,
	_closable: true,

	$define: { //zk.def
		open: function (open, fromServer) {
			var node = this.$n();
			if (node && this._closable) {
				if (this.isLegend()) { //legend
					jq(node)[open ? 'removeClass': 'addClass'](this.getZclass() + "-colpsd");
					zWatch.fireDown(open ? 'onShow': 'onHide', this);
				} else {
					zk(this.getCaveNode())[open?'slideDown':'slideUp'](this);
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
		if (!this.isLegend()) {
			if (this.caption) s = 'border-top:0;' + (s||'');
			if (!this._open) s = 'display:none;' + (s||'');
		}
		if (s) html += ' style="' + s + '"';
		return html;
	},

	//watch//
	onSize: _zkf = function () {
		var hgh = this.$n().style.height;
		if (hgh && hgh != "auto") {
			var n = this.$n('cave');
			if (n) {
				if (zk.ie6_) n.style.height = "";
				var fix = function() {
					n.style.height =
						zk(n).revisedHeight(zk(n).vflexHeight(), true)
						+ "px";
				};
				fix();
				if (zk.gecko) setTimeout(fix, 0);
					//Gecko bug: height is wrong if the browser visits the page first time
					//(reload won't reproduce the problem) test case: test/z5.zul
			}
		}

		if (!this.isLegend())
			setTimeout(this.proxy(this._fixShadow), 500);
			//shadow raraly needs to fix so OK to delay for better performance
	},
	onShow: _zkf,
	_fixShadow: function () {
		var sdw = this.$n('sdw');
		if (sdw)
			sdw.style.display =
				zk.parseInt(jq(this.$n('cave')).css("border-bottom-width")) ? "": "none";
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

		if (!this.isLegend())
			zWatch.listen({onSize: this, onShow: this});
	},
	unbind_: function () {
		if (!this.isLegend())
			zWatch.unlisten({onSize: this, onShow: this});
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
